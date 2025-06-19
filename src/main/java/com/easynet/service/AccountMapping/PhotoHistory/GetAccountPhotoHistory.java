package com.easynet.service.AccountMapping.PhotoHistory;

import static com.easynet.util.ConstantKeyValue.*;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Component
public class GetAccountPhotoHistory extends CommonBase {

	private Logger LOGGER = LoggerFactory.getLogger(GetAccountPhotoHistory.class);

	private static final String GET_ACCOUNT_PHOTO_HISTORY_BY_REQ_CD = "SELECT ACCT_CD\r\n" + "ACCT_MODE,\r\n"
			+ "ACCT_PHOTO,\r\n" + "ACCT_SIGN,\r\n" + "ACCT_TYPE,\r\n" + "ACT_FLAG,\r\n" + "BRANCH_CD,\r\n"
			+ "COMP_CD,\r\n" + "CONFIRMED,\r\n" + "ENTERED_BY,\r\n" + "ENTERED_DATE,\r\n" + "FLAG,\r\n"
			+ "FROM_LIMIT,\r\n" + "FROM_TABLE,\r\n" + "J_TYPE,\r\n" + "MACHINE_NM,\r\n" + "SIGN_GROUP,\r\n"
			+ "SR_CD,\r\n" + "TO_LIMIT,\r\n" + "UPDATE_HISTORY\r\n" + " FROM REQ_ACCT_PHOTO_MST  WHERE \r\n"
			+ "COMP_CD = :COMP_CD AND BRANCH_CD = :BRANCH_CD AND REQ_CD = :REQ_CD \r\n "
			+ " ORDER BY SR_CD DESC ";

	private static final String GET_ACCOUNT_PHOTO_HISTORY_BY_ACCT_CD = "SELECT ACCT_CD\r\n" + "ACCT_MODE,\r\n"
			+ "ACCT_PHOTO,\r\n" + "ACCT_SIGN,\r\n" + "ACCT_TYPE,\r\n" + "ACT_FLAG,\r\n" + "BRANCH_CD,\r\n"
			+ "COMP_CD,\r\n" + "CONFIRMED,\r\n" + "ENTERED_BY,\r\n" + "ENTERED_DATE,\r\n" + "FLAG,\r\n"
			+ "FROM_LIMIT,\r\n" + "FROM_TABLE,\r\n" + "J_TYPE,\r\n" + "MACHINE_NM,\r\n" + "SIGN_GROUP,\r\n"
			+ "SR_CD,\r\n" + "TO_LIMIT,\r\n" + "UPDATE_HISTORY\r\n" + " FROM EASY_BANK_LOB.ACCT_PHOTO_MST  \r\n"
			+ " WHERE \r\n" + "COMP_CD = :COMP_CD AND\r\n" + "BRANCH_CD = :BRANCH_CD AND \r\n"
			+ "ACCT_TYPE = :ACCT_TYPE AND\r\n" + "ACCT_CD  = :ACCT_CD \r\n" + "ORDER BY  SR_CD DESC ";

	@Autowired
	private SelectData selectData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String ofGetAccountPhotoHistory(String input) {
		LoggerImpl loggerImpl = null;

		String ls_comp_cd = StringUtils.EMPTY;
		String ls_branch_cd = StringUtils.EMPTY;
		String ls_acct_cd = StringUtils.EMPTY;
		String ls_acct_type = StringUtils.EMPTY;
		String ls_req_cd = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_checkStatus = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson;
		JSONObject responseDataJson = new JSONObjectImpl();
		JSONObject dbResponseJson;
		JSONArray responseJlist;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing the request Data and calling Api", "IN:ofGetAccountPhotoHistory");
			loggerImpl.generateProfiler("ofGetAccountPhotoHistory");
			loggerImpl.startProfiler("Preaparing request data.");

			requestDataJson = common.ofGetJsonObject(input);
			ls_comp_cd = getRequestUniqueData.getCompCode();
			ls_branch_cd = getRequestUniqueData.getBranchCode();

			if (requestDataJson.has(REQUEST_CD)) {

				ls_req_cd = requestDataJson.getString(REQUEST_CD);

				ls_emptyResponseData = doCheckBlankData(ls_comp_cd, ls_branch_cd, ls_req_cd);
				if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;

				ls_response = selectData.getSelectData(GET_ACCOUNT_PHOTO_HISTORY_BY_REQ_CD, ls_comp_cd, ls_branch_cd,
						ls_req_cd);
			} else if (requestDataJson.has(ACCT_CD) && requestDataJson.has(ACCT_TYPE)) {

				ls_acct_cd   = requestDataJson.getString(ACCT_CD);
				ls_acct_type = requestDataJson.getString(ACCT_TYPE);

				ls_emptyResponseData = doCheckBlankData(ls_comp_cd, ls_branch_cd, ls_acct_cd,ls_acct_type);
				if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;

				ls_response = selectData.getSelectData(GET_ACCOUNT_PHOTO_HISTORY_BY_ACCT_CD, ls_comp_cd, ls_branch_cd,
						ls_acct_type,ls_acct_cd);
			} else {
				
				return ofGetResponseJson(new JSONArrayImpl(), "", "Invalid Request ", ST99, "R",
						"common.invalid_req_data").toString();
			}
			responseDataJson = common.ofGetJsonObject(ls_response);
			ls_checkStatus = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_checkStatus)) {
				responseJlist = responseDataJson.getJSONArray("RESPONSE");

				if (responseJlist.length() != 0) {
					
					return ofGetResponseJson(responseJlist, "", "Success.", ST0, "G", "common.success_msg").toString();
				}
				
				return ofGetResponseJson(new JSONArrayImpl(), "", "No Data Found.", ST99, "R", "common.no_data_found")
						.toString();
			} else
				return ls_response;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetAccountPhotoHistory", "(ENP291)");
		}
	}
}