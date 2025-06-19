package com.easynet.service.AccountMapping.PassBook;

import static com.easynet.util.ConstantKeyValue.*;
import java.sql.Connection;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.service.AccountMapping.Statment.AccountDml;
import com.easynet.util.common;

@Service
public class PassBookDataInsert  extends DynamicDml {

	static Logger LOGGER = LoggerFactory.getLogger(PassBookDataInsert.class);
	
	private static String MAXSR_CD="SELECT NVL(MAX(SR_CD), 0) + 1 AS MAX_SR_CD\r\n"
			+ "FROM EASY_BANK.DUPLICATE_PRINT\r\n"
			+ "WHERE COMP_CD = :COMP_CD\r\n"
			+ "  AND BRANCH_CD = :BRANCH_CD\r\n"
			+ "  AND ACCT_TYPE = :ACCT_TYPE\r\n"
			+ "  AND ACCT_CD = :ACCT_CD ";
	
	@Autowired
	private SelectData selectData;
	
	@Autowired
	private GetRequestUniqueData getRequestUniqueData;
	
	@Autowired
	private AccountDml accountDml;

	public String doPassBookDataInsert(String input)  {
		
		String response_Data=StringUtils.EMPTY;
		JSONObjectImpl confJson = null;
		LoggerImpl loggerImpl=null;
		Connection connection=null;
		JSONObjectImpl requestDataJson;
		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_ResData = StringUtils.EMPTY;
		JSONObjectImpl resDataJson;
		String ls_resDataStatus = StringUtils.EMPTY;
		String ls_maxSrCd = StringUtils.EMPTY;
		String ls_giCompCd = StringUtils.EMPTY;
		String ls_giBranchCd = StringUtils.EMPTY;
		String ls_toDate = StringUtils.EMPTY;
		Object toDate;
		String ls_passBkLine = StringUtils.EMPTY;
		String ls_acctMstUpdateRes = StringUtils.EMPTY;
		JSONObjectImpl acctMstUpdateResJson;
		String ls_statusRes = StringUtils.EMPTY;
		
		try {
			
			loggerImpl = new LoggerImpl();
			connection=getDbConnection();
			
			requestDataJson = common.ofGetJsonObject(input);
			
			ls_compCd = requestDataJson.getString(COMP_CD);
			ls_branchCd = requestDataJson.getString(BRANCH_CD);
			ls_acctType= requestDataJson.getString(ACCT_TYPE);
			ls_acctCd = requestDataJson.getString(ACCT_CD);
		//	ls_fdNo = requestDataJson.getString(FD_NO);
			ls_toDate = requestDataJson.getString("STATEMENT_TO_DT");
			toDate=getSqlDateFromString(ls_toDate);
			ls_passBkLine = requestDataJson.getString("LINE_ID");
			
			ls_giCompCd = getRequestUniqueData.getCompCode();
			ls_giBranchCd = getRequestUniqueData.getBranchCode();
			
			ls_acctMstUpdateRes=accountDml.DoAccountDml(connection, ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, "P", toDate, ls_passBkLine);
			
			acctMstUpdateResJson=common.ofGetJsonObject(ls_acctMstUpdateRes);
			
			ls_statusRes=acctMstUpdateResJson.getString(STATUS);
			
			if (!isSuccessStCode(ls_statusRes)) {
				connection.rollback();
				return ls_acctMstUpdateRes;
			}

			ls_ResData = selectData.getSelectData(MAXSR_CD, ls_compCd, ls_branchCd, ls_acctType, ls_acctCd);
			
			resDataJson = common.ofGetJsonObject(ls_ResData);

			ls_resDataStatus = resDataJson.getString(STATUS);

			if (!isSuccessStCode(ls_resDataStatus)) {
				return ls_ResData;
			}

			ls_maxSrCd = resDataJson.getJSONArray("RESPONSE").getJSONObject(0).getString("MAX_SR_CD");
			
			requestDataJson.put(SR_CD, ls_maxSrCd);
			requestDataJson.put(FD_NO, 0);
			requestDataJson.put("TRN_FLAG", "P");
			requestDataJson.put(ENTERED_COMP_CD,ls_giCompCd);
			requestDataJson.put(ENTERED_BRANCH_CD,ls_giBranchCd);
			
			setSchemaName("EASY_BANK");
			
			confJson=setConfigurationJson("M","EASY_BANK.DUPLICATE_PRINT", "", "2", "", "-2", "5", "5");
			
					
			response_Data=MasterDML(requestDataJson.toString(), confJson);
			
			
			connection.commit();
			return response_Data;
			
		}
		catch(Exception exception)
		{
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doPassBookDataInsert", "ENP503");
		}finally {
			closeDbObject(connection);
		}
	}	
}
