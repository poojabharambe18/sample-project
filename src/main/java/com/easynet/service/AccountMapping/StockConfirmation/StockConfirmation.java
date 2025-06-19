package com.easynet.service.AccountMapping.StockConfirmation;

import static com.easynet.util.ConstantKeyValue.*;
import java.sql.Connection;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;
import com.easynet.service.AccountMapping.Stock.DrawingPowerData;

@Service
public class StockConfirmation  extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(StockConfirmation.class);

	private static String CONFIRMEDSTOCKDATA="UPDATE EASY_BANK.STOCK SET CONFIRMED='Y' , VERIFIED_BY=? , LAST_ENTERED_BY=?, LAST_MODIFIED_DATE=SYSDATE , LAST_MACHINE_NM=? \r\n"
			+ "WHERE COMP_CD=? AND BRANCH_CD=? AND TRAN_CD=?";

	
	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private UpdateData updateData;
	
	@Autowired
	private DrawingPowerData drawingPowerData;

	public String doStockConfirmation(String input) {
		LoggerImpl loggerImpl = null;
		JSONObjectImpl reqJson;
		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_userNm = StringUtils.EMPTY;
		String ls_entBy = StringUtils.EMPTY;
		String ls_resUpdateData = StringUtils.EMPTY;
		String ls_emptyRequstData = StringUtils.EMPTY;
		boolean isConfirmed;
		Connection connection = null;
		String ls_machineNm = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		JSONObjectImpl response_dbJson;
		String ls_responsedb_status = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		JSONObjectImpl DrawingPowerDataJson;
		Object tran_date, asonDate;
		String ls_asonDate = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		//String ls_asonDate = StringUtils.EMPTY;

		try {

			loggerImpl = new LoggerImpl();
			connection= getDbConnection();
			
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doStockConfirmation");
			loggerImpl.generateProfiler("doStockConfirmation");
			loggerImpl.startProfiler("Preparing request data.");

			reqJson = common.ofGetJsonObject(input);

			ls_userNm = getRequestUniqueData.getUserName();
			ls_machineNm=getRequestUniqueData.getMachineName();

			ls_compCd = reqJson.getString(COMP_CD);
			ls_branchCd = reqJson.getString(BRANCH_CD);
			ls_tranCd = reqJson.getString(TRAN_CD);
			ls_entBy = reqJson.getString(LAST_ENTERED_BY);
		//	ls_acctCd = reqJson.getString(ACCT_CD);
			ls_asonDate = reqJson.getString(ASON_DT);
			asonDate = getSqlDateFromString(ls_asonDate);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);
				
			isConfirmed = reqJson.optBoolean("IS_CONFIMED", false);

			ls_emptyRequstData = doCheckBlankData(ls_compCd, ls_branchCd, ls_tranCd );
			if (StringUtils.isNotBlank(ls_emptyRequstData))
				return ls_emptyRequstData;

			
			if (ls_entBy.equals(ls_userNm)) {
				
				return  ofGetResponseJson(new JSONArray(), "",
						"You can not confirm your own posted transaction", ST999, "R", "common.Posttan_confrim")
								.toString();
			}

				if (isConfirmed ) {
								
		
						ls_resUpdateData = updateData.doUpdateData(connection,CONFIRMEDSTOCKDATA,ls_userNm,ls_userNm, ls_machineNm, ls_compCd, ls_branchCd,
								ls_tranCd);
						
						response_dbJson = common.ofGetJsonObject(ls_resUpdateData);
						
						ls_responsedb_status = response_dbJson.getString(STATUS);

						if (!isSuccessStCode(ls_responsedb_status)) {
							connection.rollback();
							return ls_resUpdateData;
						}
						
//						ls_response=ofGetResponseJson(new JSONArray(), "","Successfully Confirmed.", ST0, "G", "common.confirm_successfully")
//										.toString();
						DrawingPowerDataJson = drawingPowerData.ofGetDrawingPowerData(connection, ls_compCd, ls_branchCd, ls_acctType,
								ls_acctCd, asonDate);

						ls_response = ofGetResponseJson(new JSONArrayImpl().put(DrawingPowerDataJson), " ", "Success",
								ST0, " ", "common.success_msg").toString();		
				}
				
				
			connection.commit();
			return ls_response;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doStockConfirmation", "ENP442");
		}finally {	
			closeDbObject(connection);
		}
	}
	
	
	
	

}
