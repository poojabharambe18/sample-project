package com.easynet.service.AccountMapping.TemporaryOdAgainst;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.GetFuctionData;
import com.easynet.dao.GetProcData;
import com.easynet.dao.SelectData;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;

import oracle.jdbc.OracleTypes;

import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;

@Service
public class TempOdDml extends DynamicDml {

	static Logger LOGGER = LoggerFactory.getLogger(TempOdDml.class);

	private static final String PROC_VALIDATION_SAVE = "PACK_TEMP_OD_ENTRY.PROC_VALIDATION_SAVE";

	private static final String FUNC_SYS_PARA_MST_VALUE = "EASY_BANK.FUNC_SYS_PARA_MST_VALUE";

	private static final String ACCT_PARA_DTL = "EASY_BANK.ACCT_PARA_DTL";

	private static final String ACCT_PARA_DOC_SUBDTL = "EASY_BANK.ACCT_PARA_DOC_SUBDTL";

	private static final String UPDATEPARA_DTL = "UPDATE EASY_BANK.ACCT_PARA_DTL SET VERIFIED_BY =? , VERIFIED_DATE= SYSDATE , VERIFIED_MACHINE_NM= ? WHERE SR_CD=?";

	private static final String MAXSR_CD = "SELECT MAX(SR_CD) AS MAX_SR_CD FROM EASY_BANK.ACCT_PARA_DTL WHERE COMP_CD= ? AND BRANCH_CD= ? AND ACCT_TYPE= ? AND ACCT_CD=?";

	private static final String FORCEEXPIRED = "UPDATE EASY_BANK.ACCT_PARA_DTL SET CONFIRMED= ?, FORCE_EXP_DT =SYSDATE , FORCE_EXP_BY= ? , LAST_ENTERED_BY=? ,LAST_MODIFIED_DATE= SYSDATE, LAST_MACHINE_NM=? WHERE COMP_CD= ? AND BRANCH_CD= ? AND ACCT_TYPE= ? AND ACCT_CD=? AND SR_CD=?";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetFuctionData getFuctionData;

	@Autowired
	private UpdateData updateData;

	@Autowired
	private SelectData selectData;

	public String DoTempOdDml(String input) {
		LoggerImpl loggerImpl = null;
		Connection connection = null;

		String ls_emptyResponseData = StringUtils.EMPTY;
		JSONObjectImpl reqJson;
		String ls_validateData = StringUtils.EMPTY;
		String ls_validateDataStatus = StringUtils.EMPTY;
		JSONObjectImpl validateDataJson;
		String ls_giCompCd = StringUtils.EMPTY;
		String ls_giBranchCd = StringUtils.EMPTY;
		String ls_userNm = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_workingDate = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_fromEffDate = StringUtils.EMPTY;
		String ls_toEffDate = StringUtils.EMPTY;
		String ls_dataValue = StringUtils.EMPTY;
		String ls_amountUpto = StringUtils.EMPTY;
		String ls_machineName = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		Object workingDate;
		Object returnData;
		JSONObjectImpl userJson;
		String ls_response_db = StringUtils.EMPTY;
		String ls_responsedb_status = StringUtils.EMPTY;
		JSONObjectImpl response_dbJson;
		JSONArrayImpl responseJlist;
		String ls_Srcd = StringUtils.EMPTY;
		String ls_resUpd = StringUtils.EMPTY;
		String ls_resUpd_status = StringUtils.EMPTY;
		JSONObjectImpl resUpdateJson;
		String ls_resMaxSrCd = StringUtils.EMPTY;
		String ls_resMaxSrCd_status = StringUtils.EMPTY;
		JSONObjectImpl resMaxSrCdJson;
		String ls_maxSrCd = StringUtils.EMPTY;
		Object fromEffDate, toEffDate;
		String ls_Status = StringUtils.EMPTY;
		String ls_confrimed= StringUtils.EMPTY;
		boolean isNewRow;
		JSONArrayImpl DtldataNewRowJlist;
		JSONObjectImpl DtldataNewRowjson;

		try {

			loggerImpl = new LoggerImpl();
			connection = getDbConnection();

			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:DoTempOdDml");
			loggerImpl.generateProfiler("DoTempOdDml");
			loggerImpl.startProfiler("Preparing request data.");

			reqJson = common.ofGetJsonObject(input);
			userJson = getRequestUniqueData.getLoginUserDetailsJson();

			ls_giCompCd = getRequestUniqueData.getCompCode();
			ls_giBranchCd = getRequestUniqueData.getBranchCode();
			ls_userNm = getRequestUniqueData.getUserName();
			ls_lang = getRequestUniqueData.getLangCode();
			ls_workingDate = getRequestUniqueData.getWorkingDate();
			workingDate = getSqlDateFromString(ls_workingDate);
			ls_machineName = getRequestUniqueData.getMachineName();

			isNewRow = reqJson.optBoolean("_isNewRow", false);
			ls_branchCd = reqJson.getString(BRANCH_CD);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);

			ls_emptyResponseData = doCheckBlankData(ls_giCompCd, ls_branchCd, ls_acctType, ls_acctCd);
			if (StringUtils.isNoneBlank(ls_emptyResponseData))
				return ls_emptyResponseData;
			
			returnData = getFuctionData.getAllTypeReturnValue(FUNC_SYS_PARA_MST_VALUE, OracleTypes.CHAR,
					ls_giCompCd, ls_giBranchCd, 815);

			if (isNewRow) {

				ls_fromEffDate = reqJson.getString(FROM_EFF_DATE);
				fromEffDate = getSqlDateFromString(ls_fromEffDate);
				ls_toEffDate = reqJson.getString(TO_EFF_DATE);
				toEffDate = getSqlDateFromString(ls_toEffDate);
				ls_dataValue = reqJson.getString(CODE);
				ls_amountUpto = reqJson.getString(AMOUNT_UPTO);

//validate the data before save	
				
//				ls_validateData = getProcData.getCursorData(connection,PROC_VALIDATION_SAVE, ls_giCompCd, ls_branchCd, ls_acctType,
//						ls_acctCd, fromEffDate, toEffDate, ls_dataValue, ls_amountUpto, workingDate, ls_lang);
//
//				validateDataJson = common.ofGetJsonObject(ls_validateData);
//				ls_validateDataStatus = validateDataJson.getString(STATUS);
//
//				if (!isSuccessStCode(ls_validateDataStatus)) {
//					connection.rollback();
//					return ls_validateData;
//				}
//
//				ls_Status = validateDataJson.getJSONArray("RESPONSE").getJSONObject(0).getString("O_STATUS");
//
//				if (!ls_Status.equals("0")) {
//					return ls_validateData;
//				}

				setSchemaName("EASY_BANK");
				
//checked auto confirmed flag
				//gobal level comp_cd and branch_cd
//				returnData = getFuctionData.getAllTypeReturnValue(FUNC_SYS_PARA_MST_VALUE, OracleTypes.CHAR,
//						ls_giCompCd, ls_giBranchCd, 815);

				if (returnData == null || returnData.equals("Y")) {
					setConfirmationRules(0);
					//setAllVerificationFields(true);				
				} else {					
					setConfirmationRules(1);
					reqJson.put(CONFIRMED, returnData);
					//setAllVerificationFields(false);		
				}
				
//				}
//					setConfirmationRules(0);
//					setAllVerificationFields(true);
//				} else {
//					setAllVerificationFields(false);
//				}

				ls_resMaxSrCd = selectData.getSelectData(MAXSR_CD, ls_giCompCd, ls_branchCd, ls_acctType, ls_acctCd);

				resMaxSrCdJson = common.ofGetJsonObject(ls_resMaxSrCd);

				ls_resMaxSrCd_status = resMaxSrCdJson.getString(STATUS);

				if (!isSuccessStCode(ls_resMaxSrCd_status)) {

					return ls_resMaxSrCd;
				}

				ls_maxSrCd = resMaxSrCdJson.getJSONArray("RESPONSE").getJSONObject(0).getString("MAX_SR_CD");

				if (StringUtils.isBlank(ls_maxSrCd)) {
					ls_maxSrCd = "0";
				}
				int maxSrCd = Integer.parseInt(ls_maxSrCd);
				maxSrCd = maxSrCd + 1;

				reqJson.put(COMP_CD, ls_giCompCd);
				reqJson.put("SR_CD", maxSrCd);
				int lineId = 1;

				DtldataNewRowJlist = reqJson.getJSONObject("DETAILS_DATA").getJSONArray("isNewRow");

				if (DtldataNewRowJlist.length() > 0) {
					for (int i = 0; i < DtldataNewRowJlist.length(); i++) {
						DtldataNewRowjson = DtldataNewRowJlist.getJSONObject(i);

						DtldataNewRowjson.put(COMP_CD, ls_giCompCd);
						DtldataNewRowjson.put(BRANCH_CD, ls_branchCd);
						DtldataNewRowjson.put(ACCT_TYPE, ls_acctType);
						DtldataNewRowjson.put(ACCT_CD, ls_acctCd);
						DtldataNewRowjson.put(CODE, ls_dataValue);
						DtldataNewRowjson.put(SR_CD, maxSrCd);
						DtldataNewRowjson.put(LINE_ID, lineId);

						lineId++;
					}
				}
//Insert the data
				ls_response_db = InsertData(connection, reqJson, userJson, 2, "", ACCT_PARA_DTL, ACCT_PARA_DOC_SUBDTL);

				response_dbJson = common.ofGetJsonObject(ls_response_db);

				ls_responsedb_status = response_dbJson.getString(STATUS);

				if (!isSuccessStCode(ls_responsedb_status)) {
					connection.rollback();
					return ls_response_db;
				}
				/*
				 * responseJlist = response_dbJson.getJSONArray("RESPONSE");
				 * 
				 * ls_Srcd = responseJlist.getJSONObject(0).getString("SR_CD");
				 * 
				 * if (returnData.equals("Y")) {
				 * 
				 * //if auto confrimed flag Y then update the vrify_by and related filed
				 * ls_resUpd = updateData.doUpdateData(connection, UPDATEPARA_DTL, ls_userNm,
				 * ls_machineName, ls_Srcd);
				 * 
				 * resUpdateJson = common.ofGetJsonObject(ls_resUpd); ls_resUpd_status =
				 * resUpdateJson.getString(STATUS);
				 * 
				 * if (!isSuccessStCode(ls_resUpd_status)) { connection.rollback(); return
				 * ls_resUpd; } }
				 */

			} else {
//update data for force expired				
				ls_compCd = reqJson.getString(COMP_CD);
				ls_Srcd = reqJson.getString(SR_CD);
				
				if (returnData == null || returnData.equals("Y")) {
					
					ls_confrimed=returnData.toString();	
				}else
				{
					ls_confrimed =returnData.toString();
				}
				
				ls_emptyResponseData = doCheckBlankData(ls_compCd,ls_Srcd);
			    if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

				ls_response_db = updateData.doUpdateData(connection, FORCEEXPIRED,ls_confrimed, ls_userNm, ls_userNm, ls_machineName,
						ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, ls_Srcd);

				response_dbJson = common.ofGetJsonObject(ls_response_db);
				ls_responsedb_status = response_dbJson.getString(STATUS);

				if (!isSuccessStCode(ls_responsedb_status)) {
					connection.rollback();
					return ls_response_db;
				}
			}
			connection.commit();
			return ofGetResponseJson(new JSONArrayImpl(), " ", "Success", ST0, " ", "common.success_msg").toString();

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:DoTempOdDml", "(ENP402)");
		} finally {
			// close the database connections object.
			closeDbObject(connection);
		}

	}
}
