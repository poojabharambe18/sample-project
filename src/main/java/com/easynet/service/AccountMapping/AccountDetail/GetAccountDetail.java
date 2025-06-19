package com.easynet.service.AccountMapping.AccountDetail;

import static com.easynet.util.ConstantKeyValue.*;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetProcData;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.ProcedureConstantName;
import com.easynet.util.common;

@Service
public class GetAccountDetail extends CommonBase {

	private Logger LOGGER = LoggerFactory.getLogger(GetAccountDetail.class);

	@Autowired
	private SelectData selectData;

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String getAccountDetail(String input) {
		LoggerImpl loggerImpl = null;
		String ls_requestId = StringUtils.EMPTY;
		String ls_personalDtlRes = StringUtils.EMPTY;
		String ls_jointAccountDtlRes = StringUtils.EMPTY;
		String ls_relativeDtlRes = StringUtils.EMPTY;
		String ls_otherAddressDtlRes = StringUtils.EMPTY;
		String ls_mobileRegDtlRes = StringUtils.EMPTY;
		String ls_documentDtlRes = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctNumber = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_photoDtlRes = StringUtils.EMPTY;
		String ls_advanceConfigDtlRes = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_res_status = StringUtils.EMPTY;
		String ls_loginCompCd = StringUtils.EMPTY;
		String ls_loginBranchCd = StringUtils.EMPTY;
		String ls_userLevel = StringUtils.EMPTY;
		String ls_userName = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_screenRef = StringUtils.EMPTY;
		String ls_workingDate = StringUtils.EMPTY;
		String ls_customerId = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson;
		JSONObjectImpl responseDataJson;
		JSONObjectImpl dbResJson = null;
		JSONArrayImpl resDataJlist;
		JSONObject mainResJson = new JSONObject();

		try {
			loggerImpl = new LoggerImpl();

			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:getAccountDetail");
			loggerImpl.generateProfiler("getAccountDetail");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);
			ls_loginCompCd = getRequestUniqueData.getCompCode();
			ls_loginBranchCd = getRequestUniqueData.getBranchCode();
			ls_userLevel = getRequestUniqueData.getUserRole();
			ls_userName = getRequestUniqueData.getUserName();
			ls_lang = getRequestUniqueData.getLangCode();
			ls_workingDate = getRequestUniqueData.getWorkingDate();
			ls_compCd = requestDataJson.getString(COMP_CD);
			ls_branchCd = requestDataJson.getString(BRANCH_CD);
			ls_acctType = requestDataJson.getString(ACCT_TYPE);
			ls_acctNumber = requestDataJson.getString(ACCT_CD);
			ls_requestId = requestDataJson.getString(REQUEST_CD);
			ls_screenRef = requestDataJson.getString(SCREEN_REF);

			ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd, ls_acctType);
			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			loggerImpl.startProfiler("Calling getAccountDetail API response data.");

			ls_personalDtlRes = getProcData.getCursorData(ProcedureConstantName.PROC_RETRIEVE_DATA, ls_loginCompCd,
					ls_loginBranchCd, ls_compCd, ls_branchCd, ls_acctType, ls_acctNumber, ls_customerId, ls_requestId,
					"MAIN", ls_workingDate, ls_userName, ls_userLevel, ls_screenRef, ls_lang);

			responseDataJson = common.ofGetJsonObject(ls_personalDtlRes);

			ls_res_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_res_status)) {
				resDataJlist = responseDataJson.getJSONArray("RESPONSE");
				if (resDataJlist.length() == 0) {
					return ofGetResponseJson(new JSONArray(), " ", "No Data found .", ST99, " ", "common.no_data_found")
							.toString();
				} else {
					dbResJson = resDataJlist.getJSONObject(0);
					ls_customerId = dbResJson.getString(CUSTOMER_ID);
					mainResJson.put("MAIN_DETAIL", dbResJson);
				}

			} else {
				return ls_personalDtlRes;
			}
			
			// GET ADVANCE CONFIG DETAIL AND CHECK RESPONSE.
			ls_advanceConfigDtlRes = getProcData.getCursorData(ProcedureConstantName.PROC_RETRIEVE_DATA, ls_loginCompCd,
					ls_loginBranchCd, ls_compCd, ls_branchCd, ls_acctType, ls_acctNumber, ls_customerId, ls_requestId,
					"ADV_CONFIG", ls_workingDate, ls_userName, ls_userLevel, ls_screenRef, ls_lang);
			responseDataJson = new JSONObjectImpl();
			dbResJson = new JSONObjectImpl();
			responseDataJson = common.ofGetJsonObject(ls_advanceConfigDtlRes);
			ls_res_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_res_status)) {
				resDataJlist = responseDataJson.getJSONArray("RESPONSE");
				if (resDataJlist.length() != 0) {
					mainResJson.put("ADVANCE_CONFIG_DTL", resDataJlist);
				} else {
					mainResJson.put("ADVANCE_CONFIG_DTL", resDataJlist);
				}
			} else {
				return ls_advanceConfigDtlRes;
			}
			

			// check other address detail response.

			ls_otherAddressDtlRes = getProcData.getCursorData(ProcedureConstantName.PROC_RETRIEVE_DATA, ls_loginCompCd,
					ls_loginBranchCd, ls_compCd, ls_branchCd, ls_acctType, ls_acctNumber, ls_customerId, ls_requestId,
					"OTH_ADD", ls_workingDate, ls_userName, ls_userLevel, ls_screenRef, ls_lang);
			responseDataJson = new JSONObjectImpl();
			dbResJson = new JSONObjectImpl();
			responseDataJson = common.ofGetJsonObject(ls_otherAddressDtlRes);
			ls_res_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_res_status)) {
				resDataJlist = responseDataJson.getJSONArray("RESPONSE");
				if (resDataJlist.length() != 0) {
					mainResJson.put("OTHER_ADDRESS_DTL", resDataJlist);

				} else {
					mainResJson.put("OTHER_ADDRESS_DTL", resDataJlist);
				}
			} else {
				return ls_otherAddressDtlRes;
			}

			// check mobile registration detail response.
			ls_mobileRegDtlRes = getProcData.getCursorData(ProcedureConstantName.PROC_RETRIEVE_DATA, ls_loginCompCd,
					ls_loginBranchCd, ls_compCd, ls_branchCd, ls_acctType, ls_acctNumber, ls_customerId, ls_requestId,
					"MOB_REG", ls_workingDate, ls_userName, ls_userLevel, ls_screenRef, ls_lang);
			responseDataJson = new JSONObjectImpl();
			dbResJson = new JSONObjectImpl();
			responseDataJson = common.ofGetJsonObject(ls_mobileRegDtlRes);
			ls_res_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_res_status)) {
				resDataJlist = responseDataJson.getJSONArray("RESPONSE");
				if (resDataJlist.length() != 0) {
					mainResJson.put("MOBILE_REG_DTL", resDataJlist);
				} else {
					mainResJson.put("MOBILE_REG_DTL", resDataJlist);
				}
			} else {
				return ls_mobileRegDtlRes;
			}

			// check relative detail response.

			ls_relativeDtlRes = getProcData.getCursorData(ProcedureConstantName.PROC_RETRIEVE_DATA, ls_loginCompCd,
					ls_loginBranchCd, ls_compCd, ls_branchCd, ls_acctType, ls_acctNumber, ls_customerId, ls_requestId,
					"REL_DTL", ls_workingDate, ls_userName, ls_userLevel, ls_screenRef, ls_lang);

			responseDataJson = new JSONObjectImpl();
			dbResJson = new JSONObjectImpl();
			responseDataJson = common.ofGetJsonObject(ls_relativeDtlRes);
			ls_res_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_res_status)) {
				resDataJlist = responseDataJson.getJSONArray("RESPONSE");
				if (resDataJlist.length() != 0) {
					mainResJson.put("RELATIVE_DTL", resDataJlist);

				} else {
					mainResJson.put("RELATIVE_DTL", resDataJlist);
				}
			} else {
				return ls_relativeDtlRes;
			}

			// check joint account detail response.
			ls_jointAccountDtlRes = getProcData.getCursorData(ProcedureConstantName.PROC_RETRIEVE_DATA, ls_loginCompCd,
					ls_loginBranchCd, ls_compCd, ls_branchCd, ls_acctType, ls_acctNumber, ls_customerId, ls_requestId,
					"JOINT", ls_workingDate, ls_userName, ls_userLevel, ls_screenRef, ls_lang);
			responseDataJson = new JSONObjectImpl();
			dbResJson = new JSONObjectImpl();
			responseDataJson = common.ofGetJsonObject(ls_jointAccountDtlRes);
			ls_res_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_res_status)) {
				resDataJlist = responseDataJson.getJSONArray("RESPONSE");
				if (resDataJlist.length() != 0) {
					mainResJson.put("JOINT_ACCOUNT_DTL", resDataJlist);

				} else {
					mainResJson.put("JOINT_ACCOUNT_DTL", resDataJlist);
				}
			} else {
				return ls_jointAccountDtlRes;
			}

			// check document detail response.

			ls_documentDtlRes = getProcData.getCursorData(ProcedureConstantName.PROC_RETRIEVE_DATA, ls_loginCompCd,
					ls_loginBranchCd, ls_compCd, ls_branchCd, ls_acctType, ls_acctNumber, ls_customerId, ls_requestId,
					"DOC", ls_workingDate, ls_userName, ls_userLevel, ls_screenRef, ls_lang);

			responseDataJson = new JSONObjectImpl();
			dbResJson = new JSONObjectImpl();
			responseDataJson = common.ofGetJsonObject(ls_documentDtlRes);
			ls_res_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_res_status)) {
				resDataJlist = responseDataJson.getJSONArray("RESPONSE");
				if (resDataJlist.length() != 0) {
					mainResJson.put("DOC_MST", resDataJlist);
				} else {
					mainResJson.put("DOC_MST", resDataJlist);
				}
			} else {
				return ls_documentDtlRes;
			}


			return ofGetResponseJson(new JSONArray().put(mainResJson), " ", "Success", ST0, " ", "common.success_msg")
					.toString();
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:getAccountDetail", "(ENP260)");
		}
	}
}
