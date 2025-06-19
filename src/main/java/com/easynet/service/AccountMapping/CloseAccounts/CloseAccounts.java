package com.easynet.service.AccountMapping.CloseAccounts;

import java.sql.Connection;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

import oracle.jdbc.internal.OracleTypes;

@Service
public class CloseAccounts extends CommonBase {

	public static final String PR_CLOSE_ACCT = "EASY_BANK.PR_CLOSE_ACCT";

	static Logger LOGGER = LoggerFactory.getLogger(CloseAccounts.class);

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String doCloseAccounts(String input) {

		//String ls_loginCompCd = StringUtils.EMPTY;
		//String ls_loginBranchCd = StringUtils.EMPTY;
		//String new_comp = StringUtils.EMPTY;
		//String new_branch = StringUtils.EMPTY;
		//String new_type = StringUtils.EMPTY;
		//String  ls_new_acct_no= StringUtils.EMPTY;
		//String ls_response = StringUtils.EMPTY;
		//String ls_lang = StringUtils.EMPTY;

		String ls_comp_cd = StringUtils.EMPTY;
		String ls_branch_cd = StringUtils.EMPTY;
		String ls_acct_type = StringUtils.EMPTY;
		String acct_cd = StringUtils.EMPTY;
		String ls_close_Dt = StringUtils.EMPTY;		
		String ls_machineNm = StringUtils.EMPTY;
		String ls_userName = StringUtils.EMPTY;
		String ls_userRole = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String response_data = StringUtils.EMPTY;

		JSONObjectImpl resJson = new JSONObjectImpl();
		JSONArrayImpl close_acct_array = new JSONArrayImpl();
		JSONObjectImpl requestDataJson;
		LoggerImpl loggerImpl = null;	
		Connection connection = null;
		//ArrayList<Integer> outParaList;
		Object response;
		Object date;	
		//JSONObjectImpl JsonDatastatus;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doCloseAccounts");
			loggerImpl.generateProfiler("doCloseAccounts");
			loggerImpl.startProfiler("Preparing request data.");

			//outParaList = new ArrayList<Integer>();
			//outParaList.add(OracleTypes.CHAR);

			connection = getDbConnection();
			requestDataJson = common.ofGetJsonObject(input);

			//ls_loginCompCd = getRequestUniqueData.getCompCode();
			//ls_loginBranchCd = getRequestUniqueData.getBranchCode();	
			//ls_lang = getRequestUniqueData.getLangCode();
			//new_type = requestDataJson.getString("NEW_TYPE");
			//new_branch = requestDataJson.getString("NEW_BRANCH");		
			//new_comp = requestDataJson.getString("NEW_COMP");


			ls_close_Dt = getRequestUniqueData.getWorkingDate();
			date = getSqlDateFromString(ls_close_Dt);
			ls_userName = getRequestUniqueData.getUserName();	
			ls_machineNm = getRequestUniqueData.getMachineName();
			ls_userRole = getRequestUniqueData.getUserRole();

			loggerImpl.startProfiler("Calling doCloseAccounts API response data.");

			close_acct_array = requestDataJson.getJSONArray("CLOSEACCT_LIST");

			for (int i = 0; i < close_acct_array.length(); i++) 
			{
				JSONObjectImpl acct_detail = close_acct_array.getJSONObject(i);

				ls_comp_cd = acct_detail.getString("COMP_CD");
				ls_branch_cd = acct_detail.getString("BRANCH_CD");			
				ls_acct_type = acct_detail.getString("ACCT_TYPE");
				acct_cd = acct_detail.getString("ACCT_CD");	
				ls_close_Dt = acct_detail.getString("CLOSE_DT");	

				ls_emptyResponseData = doCheckBlankData( ls_comp_cd,ls_branch_cd,ls_acct_type,acct_cd,ls_close_Dt, ls_userName
						,ls_machineNm);

				if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;

				response = getProcData.procExecuteOnly(connection,PR_CLOSE_ACCT,ls_comp_cd,
						ls_branch_cd, ls_acct_type,acct_cd,ls_close_Dt,ls_userName,ls_machineNm);

				response_data = response.toString();
			}
			resJson = common.ofGetJsonObject(response_data);

			if(!isSuccessStCode(resJson.getString("STATUS"))){
				connection.rollback();
				//return response_data;
			}else {
				connection.commit();
			}
			return response_data; 
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doCloseAccounts", "(ENP668)");
		}finally {
			closeDbObject(connection);
		}
	}

}
