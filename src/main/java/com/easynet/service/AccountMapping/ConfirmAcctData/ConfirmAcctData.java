package com.easynet.service.AccountMapping.ConfirmAcctData;
import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetFuctionData;
import com.easynet.dao.SelectData;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

import oracle.jdbc.OracleTypes;

@Component
public class ConfirmAcctData extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(ConfirmAcctData.class);

	private static final String UPDATE_ACCT_CD_IN_REQ_ACCT_MST = "UPDATE REQ_ACCT_MST SET "
			+ " ACCT_CD = ? ,	CONFIRMED = ? , VERIFIED_BY= ? , "
			+ " VERIFIED_DATE = SYSDATE ,VERIFIED_MACHINE_NM = ? "
			+ " WHERE REQ_CD = :REQ_CD  AND COMP_CD = :COMP_CD AND BRANCH_CD = :BRANCH_CD ";

	private static final String UPDATE_ACCT_AND_DATA_TABLE_IN_HDR = "UPDATE REQ_ACCT_MST_HDR SET "
			+ "	ACCT_CD = ? , CONFIRMED = ? ,REMARKS= ? , VERIFIED_BY = ? , " + " VERIFIED_DATE = SYSDATE , VERIFIED_MACHINE_NM = ? "
			+ " WHERE REQ_CD = ? AND COMP_CD = :COMP_CD AND BRANCH_CD = :BRANCH_CD AND CONFIRMED = 'P' ";

	private static final String UPDATE_DATA_IN_HDR_TABLE = "UPDATE REQ_ACCT_MST_HDR SET "
			+ " CONFIRMED = ? ,REMARKS= ? , VERIFIED_BY = ? , " + " VERIFIED_DATE = SYSDATE , VERIFIED_MACHINE_NM = ? "
			+ " WHERE REQ_CD = ? AND COMP_CD = :COMP_CD AND BRANCH_CD = :BRANCH_CD AND CONFIRMED IN ('N','P','M') ";

	private static final String UPDATE_EX_ACCT_DATA_IN_HDR = "UPDATE REQ_ACCT_MST_HDR SET "
			+ "	 CONFIRMED = ? ,REMARKS= ? , VERIFIED_BY = ? , " + " VERIFIED_DATE = SYSDATE , VERIFIED_MACHINE_NM = ? "
			+ " WHERE REQ_CD = ? AND COMP_CD = :COMP_CD AND BRANCH_CD = :BRANCH_CD AND CONFIRMED = 'P' ";

	private static final String FUNC_GET_NEW_ACCT_CD = "FUNC_GET_NEW_ACCT_CD";

	private static final String GET_STATUS_ENTRY_USERNAME = "SELECT ACCT_TYPE ,COMP_CD,BRANCH_CD,TRIM(CONFIRMED) AS CONFIRMED , REQ_FLAG , ENTERED_BY,LAST_ENTERED_BY ,LAST_MACHINE_NM,LAST_MODIFIED_DATE FROM REQ_ACCT_MST_HDR WHERE REQ_CD = ? ";

	@Autowired
	private UpdateData updateData;

	@Autowired
	private GetConfirmExAcctData getConfirmExAcctData;

	@Autowired
	private SelectData selectData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetFuctionData getFuctionData;

	public String doConfirmAccountData(String input) {
		LoggerImpl loggerImpl = null;
		Connection connection = null;

		String ls_db_response = StringUtils.EMPTY;
		String ls_confirmed = StringUtils.EMPTY;
		String ls_user_nm = StringUtils.EMPTY;
		String ls_machine_nm = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_user_role = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		String ls_branch_cd = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		String ls_confirmStatus = StringUtils.EMPTY;
		String ls_cust_type = StringUtils.EMPTY;
		String ls_entered_by = StringUtils.EMPTY;
		String ls_lastEnteredBy = StringUtils.EMPTY;
		String ls_lastModifyDt = StringUtils.EMPTY;
		String ls_comp_cd = StringUtils.EMPTY;
		String ls_acct_type = StringUtils.EMPTY;
		String ls_acct_comp_cd = StringUtils.EMPTY;
		String ls_acct_branch_cd = StringUtils.EMPTY;
		String ls_acct_cd = StringUtils.EMPTY;
		String ls_personal_dtl_res = StringUtils.EMPTY;
		String ls_lastMachineNm = StringUtils.EMPTY;
		String ls_remark = StringUtils.EMPTY;
		String ls_responseData = StringUtils.EMPTY;
		Object returnData;
		JSONObjectImpl mainResJson = new JSONObjectImpl();


		int li_role;

		JSONObjectImpl requestDataJson;
		JSONObjectImpl responseJson;
		JSONObjectImpl dbResJson;

		try {

			loggerImpl = new LoggerImpl();
			connection = getDbConnection();

			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doConfirmAccountData");
			loggerImpl.generateProfiler("doConfirmAccountData");
			loggerImpl.startProfiler("Preparing request data.");

			StringBuilder new_acct_cd=new StringBuilder();

			requestDataJson = common.ofGetJsonObject(input);
			ls_confirmed = requestDataJson.getString(CONFIRMED);
			ls_user_nm = getRequestUniqueData.getUserName();
			ls_machine_nm = getRequestUniqueData.getMachineName();
			ls_tranCd = requestDataJson.getString(REQUEST_CD);
			ls_user_role = getRequestUniqueData.getUserRole();
			ls_branch_cd = getRequestUniqueData.getBranchCode();
			ls_comp_cd = getRequestUniqueData.getCompCode();
			li_role = Integer.parseInt(ls_user_role);
			ls_remark = requestDataJson.getString("REMARKS");

			ls_emptyResponseData = doCheckBlankData(ls_confirmed, ls_tranCd, ls_user_nm, ls_machine_nm);
			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			loggerImpl.startProfiler("Calling doConfirmAccountData API response data.");

			ls_responseData = selectData.getSelectData(GET_STATUS_ENTRY_USERNAME, ls_tranCd);

			responseJson = common.ofGetJsonObject(ls_responseData);
			ls_resStatus = responseJson.getString(STATUS);

			if (!isSuccessStCode(ls_resStatus)) {
				return ls_responseData;
			}
			
			Object resObj = responseJson.opt("RESPONSE");

			if (!(resObj instanceof JSONArray)) {
			    return ofGetResponseJson(new JSONArray(), "", 
			            "RESPONSE is not a valid array.", ST99, "R", 
			            "").toString();
			}

			JSONArray responseArray = (JSONArray) resObj;

			if ( responseArray.length() == 0) {
			    return ofGetResponseJson(new JSONArray(), "", 
			            "RESPONSE is empty. Cannot proceed.", ST99, "R", 
			            "").toString();
			}

			
			dbResJson = responseJson.getJSONArray("RESPONSE").getJSONObject(0);
			ls_confirmStatus = dbResJson.getString(CONFIRMED);
			ls_cust_type = dbResJson.getString(REQ_FLAG);
			ls_entered_by = dbResJson.getString(ENTERED_BY);
			ls_acct_type = dbResJson.getString(ACCT_TYPE);
			ls_acct_comp_cd = dbResJson.getString(COMP_CD);
			ls_acct_branch_cd = dbResJson.getString(BRANCH_CD);
			ls_lastEnteredBy = dbResJson.getString(LAST_ENTERED_BY);
			ls_lastModifyDt = dbResJson.getString(LAST_MODIFIED_DATE);
			ls_lastMachineNm = dbResJson.getString(LAST_MACHINE_NM);

			if (ls_confirmed.equals("Y")) {

				if (ls_confirmStatus.equals("P") && ls_cust_type.equals("F")) 
				{

					if (ls_entered_by.equals(ls_user_nm))
					{
						return ofGetResponseJson(new JSONArray(), "", "You cannot confirm Account created by yourself.",
								ST99, "R", "common.confirm_acct_data_err").toString();
					}

					returnData = getFuctionData.getAllTypeReturnValue(connection, FUNC_GET_NEW_ACCT_CD,
							OracleTypes.CHAR, ls_acct_comp_cd, ls_acct_branch_cd, ls_acct_type);

					ls_acct_cd = returnData.toString();

					if (ls_acct_cd.isEmpty()) 
					{
						return ofGetResponseJson(new JSONArray(), "", "Error in getting new account number.", ST99, "R","common.confirm_acct_data_err").toString();
					}

					// UPDATE ACCOUNT NUMBER AND OTHER column IN REQ_ACCT_MST TABLE.
					ls_personal_dtl_res = updateData.doUpdateData(connection, UPDATE_ACCT_CD_IN_REQ_ACCT_MST,
							ls_acct_cd, ls_confirmed, ls_user_nm, ls_machine_nm, ls_tranCd, ls_acct_comp_cd, ls_acct_branch_cd);

					responseJson = common.ofGetJsonObject(ls_personal_dtl_res);
					ls_resStatus = responseJson.getString(STATUS);

					if (!isSuccessStCode(ls_resStatus)) {
						return ls_personal_dtl_res;
					}

					// UPDATE ACCOUNT NUMBER AND OTHER column IN REQ_ACCT_MST_HDR TABLE.
					ls_db_response = updateData.doUpdateData(connection, UPDATE_ACCT_AND_DATA_TABLE_IN_HDR, ls_acct_cd,
							ls_confirmed,ls_remark, ls_user_nm, ls_machine_nm, ls_tranCd, ls_acct_comp_cd, ls_acct_branch_cd);

					responseJson = common.ofGetJsonObject(ls_db_response);
					ls_resStatus = responseJson.getString(STATUS);

					if (!isSuccessStCode(ls_resStatus)) {
						return ls_db_response;
					}

					mainResJson.put("COMP_CD", ls_acct_comp_cd);
					mainResJson.put("BRANCH_CD", ls_acct_branch_cd);
					mainResJson.put("ACCT_TYPE", ls_acct_type);
					mainResJson.put("ACCT_CD", ls_acct_cd.trim());

				} else if (ls_confirmStatus.equals("P") && ls_cust_type.equals("E")) 
				{

					// update existing account data in master tables.
					if (ls_lastEnteredBy.equals(ls_user_nm)) 
					{

						return ofGetResponseJson(new JSONArray(), "","You cannot confirm Account modified by yourself.", ST99, "R","common.confirm_ex_acct_data_err").toString();
					}

					ls_db_response = getConfirmExAcctData.doConfirmExAccountData(connection, ls_confirmed, ls_user_nm,
							ls_lastMachineNm, ls_tranCd, ls_entered_by, ls_acct_comp_cd, ls_lastModifyDt,ls_acct_branch_cd);

					responseJson = common.ofGetJsonObject(ls_db_response);
					ls_resStatus = responseJson.getString(STATUS);

					if (!isSuccessStCode(ls_resStatus))
					{
						return ls_db_response;
					}

					// UPDATE ACCOUNT NUMBER AND OTHER column IN REQ_ACCT_MST_HDR TABLE.
					ls_db_response = updateData.doUpdateData(connection, UPDATE_EX_ACCT_DATA_IN_HDR,
							ls_confirmed,ls_remark, ls_user_nm, ls_machine_nm, ls_tranCd, ls_acct_comp_cd, ls_acct_branch_cd);

					responseJson = common.ofGetJsonObject(ls_db_response);
					ls_resStatus = responseJson.getString(STATUS);

					if (!isSuccessStCode(ls_resStatus))
					{
						return ls_db_response;
					}

				} else 
				{
					return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R", "common.invalid_req_data").toString();
				}

			} else if (ls_confirmed.equals("R")) 
			{

				ls_db_response = updateData.doUpdateData(connection, UPDATE_DATA_IN_HDR_TABLE, ls_confirmed,ls_remark, ls_user_nm,
						ls_machine_nm, ls_tranCd, ls_acct_comp_cd, ls_acct_branch_cd);

			} else
			{
				return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R", "common.invalid_req_data").toString();
			}
			
			// CHECK STATUS AND RETURN RESPONSE.
			responseJson = common.ofGetJsonObject(ls_db_response);
			ls_resStatus = responseJson.getString(STATUS);

			if (isSuccessStCode(ls_resStatus)) 
			{
				connection.commit();
				return ofGetResponseJson(new JSONArray().put(mainResJson), "", "Request Accepted Successfully.", ST0, "G",
						"common.req_accepted").toString();
			} else
			{
				connection.rollback();
				return ls_db_response;
			}

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doConfirmAccountData", "(ENP296)");
		} finally {
			// It's important to close the statement when you are done with
			closeDbObject(connection);
		}
	}
}