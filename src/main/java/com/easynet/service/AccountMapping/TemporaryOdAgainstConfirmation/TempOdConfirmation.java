package com.easynet.service.AccountMapping.TemporaryOdAgainstConfirmation;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.SelectData;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static com.easynet.util.ConstantKeyValue.*;
import java.sql.Connection;
import com.easynet.dao.DeleteData;

@Service
public class TempOdConfirmation extends DynamicDml {

	static Logger LOGGER = LoggerFactory.getLogger(TempOdConfirmation.class);

	private static String CONFRIMEDDATA = "UPDATE EASY_BANK.ACCT_PARA_DTL SET CONFIRMED='Y' ,VERIFIED_BY=? ,VERIFIED_MACHINE_NM=? ,VERIFIED_DATE=SYSDATE\r\n"
			+ "WHERE COMP_CD =? AND BRANCH_CD=? AND ACCT_TYPE=? AND ACCT_CD=? AND SR_CD=?";

	private static String DELETE_ACCT_PARA_DTL = "DELETE FROM EASY_BANK.ACCT_PARA_DTL WHERE COMP_CD = ? AND BRANCH_CD =?"
			+ " AND ACCT_TYPE=? AND ACCT_CD =? AND SR_CD =?";

	private static String DELETE_ACCT_PARA_DOC_SUBDTL = "DELETE FROM EASY_BANK.ACCT_PARA_DOC_SUBDTL WHERE COMP_CD = ? AND BRANCH_CD =?"
			+ " AND ACCT_TYPE=? AND ACCT_CD =? AND SR_CD =?";

	private static String FORCEEXPNOBUTTON = "UPDATE EASY_BANK.ACCT_PARA_DTL SET CONFIRMED='Y' ,FORCE_EXP_DT=NULL ,FORCE_EXP_BY=NULL\r\n"
			+ "WHERE COMP_CD =? AND BRANCH_CD=? AND ACCT_TYPE=? AND ACCT_CD=? AND SR_CD=?";
	
	
	private static String SELECT_ACCT_PARA_DOC_SUBDTL = "SELECT * FROM EASY_BANK.ACCT_PARA_DOC_SUBDTL WHERE COMP_CD = ? AND BRANCH_CD =?"
			+ " AND ACCT_TYPE=? AND ACCT_CD =? AND SR_CD =?";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private UpdateData updateData;

	@Autowired
	private DeleteData deleteData;
	
	@Autowired
	private SelectData selectData;

	public String ofDoTempOdConfirmation(String input) {
		LoggerImpl loggerImpl = null;
		JSONObjectImpl reqJson;
		Connection connection = null;
		String ls_userNm = StringUtils.EMPTY;
		String ls_lastEntBy = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_srCd = StringUtils.EMPTY;
		String ls_forceExpDt = StringUtils.EMPTY;
		String ls_forceExpBy = StringUtils.EMPTY;
		String ls_resUpdateData = StringUtils.EMPTY;
		String ls_emptyRequstData = StringUtils.EMPTY;
		boolean isConfirmed;
		String ls_machineNm = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		JSONObjectImpl response_dbJson;
		String ls_responsedb_status = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_resAcctParaDtl = StringUtils.EMPTY;
		String ls_resAcctParaDtlStatus = StringUtils.EMPTY;
		String ls_resAcctParaSubDtl = StringUtils.EMPTY;
		String ls_resAcctParaSubDtlStatus = StringUtils.EMPTY;
		JSONObjectImpl resAcctParaDtlJson, resAcctParaSubDtlJson;
		
		String ls_responseData = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		JSONObjectImpl responseJson ,dbResJson; 

		try {

			loggerImpl = new LoggerImpl();
			connection = getDbConnection();

			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:ofDoTempOdConfirmation");
			loggerImpl.generateProfiler("ofDoTempOdConfirmation");
			loggerImpl.startProfiler("Preparing request data.");

			reqJson = common.ofGetJsonObject(input);

			ls_userNm = getRequestUniqueData.getUserName();
			ls_machineNm = getRequestUniqueData.getMachineName();
			ls_lastEntBy = reqJson.getString(LAST_ENTERED_BY);
			ls_compCd = reqJson.getString(COMP_CD);
			ls_branchCd = reqJson.getString(BRANCH_CD);
			ls_srCd = reqJson.getString(SR_CD);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);
			ls_forceExpDt = reqJson.getString(FORCE_EXP_DT);
			ls_forceExpBy = reqJson.getString("FORCE_EXP_BY");

			isConfirmed = reqJson.optBoolean("_isNewRow", false);

			ls_emptyRequstData = doCheckBlankData(ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, ls_srCd);
			if (StringUtils.isNotBlank(ls_emptyRequstData))
				return ls_emptyRequstData;

			if (ls_userNm.equals(ls_lastEntBy)) {
				return ofGetResponseJson(new JSONArray(), "", "You can not confirm your own posted transaction", ST999,
						"R", "common.Posttan_confrim").toString();
			}

			// Confirmed fresh entry and force expired data when clicked on yes button
			if (isConfirmed) {

				ls_resUpdateData = updateData.doUpdateData(connection, CONFRIMEDDATA, ls_userNm, ls_machineNm,
						ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, ls_srCd);

				response_dbJson = common.ofGetJsonObject(ls_resUpdateData);

				ls_responsedb_status = response_dbJson.getString(STATUS);

				if (!isSuccessStCode(ls_responsedb_status)) {
					connection.rollback();
					return ls_resUpdateData;
				}

			} else {
				
				
				// fresh entry when clicked on NO button delete the data

				if (StringUtils.isBlank(ls_forceExpDt) && StringUtils.isBlank(ls_forceExpBy)) {
					
					
					ls_responseData = selectData.getSelectData(SELECT_ACCT_PARA_DOC_SUBDTL,  ls_compCd,
							ls_branchCd, ls_acctType, ls_acctCd, ls_srCd);

					responseJson = common.ofGetJsonObject(ls_responseData);
					ls_resStatus = responseJson.getString(STATUS);

					if (!isSuccessStCode(ls_resStatus)) {
						return ls_responseData;
					}

				JSONArrayImpl	dbResJsons = responseJson.getJSONArray("RESPONSE");
					
				if(!dbResJsons.isEmpty())
				{
				
					ls_resAcctParaSubDtl = deleteData.toDeleteRow(connection, DELETE_ACCT_PARA_DOC_SUBDTL, ls_compCd,
							ls_branchCd, ls_acctType, ls_acctCd, ls_srCd);

					resAcctParaSubDtlJson = common.ofGetJsonObject(ls_resAcctParaSubDtl);

					ls_resAcctParaSubDtlStatus = resAcctParaSubDtlJson.getString(STATUS);

					if (!isSuccessStCode(ls_resAcctParaSubDtlStatus)) {
						connection.rollback();
						return ls_resAcctParaSubDtl;
					}
					
				}

					ls_resAcctParaDtl = deleteData.toDeleteRow(connection, DELETE_ACCT_PARA_DTL, ls_compCd, ls_branchCd,
							ls_acctType, ls_acctCd, ls_srCd);

					resAcctParaDtlJson = common.ofGetJsonObject(ls_resAcctParaDtl);

					ls_resAcctParaDtlStatus = resAcctParaDtlJson.getString(STATUS);

					if (!isSuccessStCode(ls_resAcctParaDtlStatus)) {
						connection.rollback();
						return ls_resAcctParaDtl;
					}

				} else // force expired entry when clicked on NO button update the data
				{
					ls_resAcctParaDtl = updateData.doUpdateData(connection, FORCEEXPNOBUTTON, ls_compCd, ls_branchCd,
							ls_acctType, ls_acctCd, ls_srCd);

					resAcctParaDtlJson = common.ofGetJsonObject(ls_resAcctParaDtl);

					ls_resAcctParaDtlStatus = resAcctParaDtlJson.getString(STATUS);

					if (!isSuccessStCode(ls_resAcctParaDtlStatus)) {
						connection.rollback();
						return ls_resAcctParaDtl;
					}
				}
			}
			connection.commit();
			return ofGetResponseJson(new JSONArrayImpl(), " ", "Success", ST0, " ", "common.success_msg").toString();

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofDoTempOdConfirmation", "ENP499");
		} finally {
			closeDbObject(connection);
		}
	}

}
