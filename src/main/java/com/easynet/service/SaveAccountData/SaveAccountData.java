package com.easynet.service.SaveAccountData;

import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;

@Service
public class SaveAccountData extends SaveAccountDetail {

	private static final String GET_LAST_ENTERED_BY = " SELECT TRIM(CONFIRMED)  AS CONFIRMED, LAST_ENTERED_BY FROM REQ_ACCT_MST_HDR WHERE  REQ_CD = :REQ_CD  ";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private SelectData selectData;

	@Autowired
	private UpdateExAccountData updateExAccountData;

	private Logger LOGGER = LoggerFactory.getLogger(SaveAccountData.class);

	public String ofSaveAccountDetail(String input) {
		String ls_saveFlag = StringUtils.EMPTY;
		String ls_requestCd = StringUtils.EMPTY;
		String ls_req_cd = StringUtils.EMPTY;
		String ls_entryType = StringUtils.EMPTY;
		String ls_reqFlag = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_lastEntBy = StringUtils.EMPTY;
		String ls_confirmStatus = StringUtils.EMPTY;
		String ls_loginUser = StringUtils.EMPTY;

		boolean lb_isNewRow;

		LoggerImpl loggerImpl = null  ;
		Connection connection = null;
		String ls_response = null;
		String ls_draft_res = null;

		JSONObjectImpl reqJson;
		JSONObjectImpl dbResJson;
		JSONObjectImpl userJson;
		JSONObjectImpl responseJson;
		JSONArrayImpl responseJlist;

		try {
			loggerImpl = new LoggerImpl();
			connection = getDbConnection();
			reqJson =  common.ofGetJsonObject(input);
			userJson = getRequestUniqueData.getLoginUserDetailsJson();
			ls_loginUser = getRequestUniqueData.getUserName();
			ls_saveFlag = reqJson.getString(SAVE_FLAG);
			ls_requestCd = reqJson.getString(REQ_CD);
			ls_entryType = reqJson.getString(ENTRY_TYPE);
			ls_reqFlag = reqJson.getString(REQ_FLAG);
			lb_isNewRow = reqJson.getBoolean("IsNewRow");
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);

			if ((ls_reqFlag.equals("F") || ls_reqFlag.equals("E")) && ls_requestCd.length() != 0) {
				responseJlist = selectData.getSelectData(connection, GET_LAST_ENTERED_BY, ls_requestCd);
				if (responseJlist.length() != 0) {
					ls_lastEntBy = responseJlist.getJSONObject(0).getString(LAST_ENTERED_BY);
					ls_confirmStatus = responseJlist.getJSONObject(0).getString(CONFIRMED);
				}

				if (!ls_lastEntBy.equals(ls_loginUser) && ls_confirmStatus.equals("P")) {

					return ofGetErrDataJsonObject("common.acct_confrimation_pending", " ", ST99, "", "", ls_lastEntBy,
							ls_loginUser).toString();
				}
			}

			//if (ls_reqFlag.equals("E") && ls_acctCd.length() != 0) {
			if (ls_reqFlag.equals("E")) {

				ls_response = updateExAccountData.ofGetUpdateAccountData(connection, reqJson, userJson);
				responseJson = common.ofGetJsonObject(ls_response);
				ls_status = responseJson.getString(STATUS);

				if (isSuccessStCode(ls_status)) {
					connection.commit();
					return ls_response;
				} else {
					connection.rollback();
					return ls_response;
				}
			}

			if ((ls_saveFlag.equals("F") && ls_requestCd.length() == 0 && ls_entryType.equals("1") && lb_isNewRow)
					|| (ls_saveFlag.equals("F") && ls_requestCd.length() != 0 && !lb_isNewRow)) {
				ls_response = ofFinalSaveAccountData(input, connection, reqJson, userJson, ls_requestCd, -2, -2);
			}

			dbResJson = common.ofGetJsonObject(ls_response);
			String ls_status_in = dbResJson.getString(STATUS);

			if (isSuccessStCode(ls_status_in)) {
				connection.commit();
				return ls_response;
			} else {
				connection.rollback();
				return ls_response;
			}
		} catch (Exception exception) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException sQLException) {
					sQLException.printStackTrace();
				}
			}
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofSaveAccountDetail", "(ENP340)");
		} finally {
			// close the database connections object.
			closeDbObject(connection);
		}
	}
}
