package com.easynet.service.AccountMapping.AcctPhotoData;

import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicInsertData;
import com.easynet.dao.GetProcData;
import com.easynet.dao.InsertData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.service.SaveAccountData.UpdateExAccountData;
import com.easynet.util.ProcedureConstantName;
import com.easynet.util.common;

@Component
public class SaveAcctPhotoData extends DynamicInsertData {

	private static Logger LOGGER = LoggerFactory.getLogger(SaveAcctPhotoData.class);

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetProcData getProcData;

	public String doInsertUpdateAcctPhotoData(String input) {

		LoggerImpl loggerImpl = null;
		Connection connection = null;

		String ls_userNm = StringUtils.EMPTY;
		String ls_machineNm = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_langCode = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_sign = StringUtils.EMPTY;
		String ls_photo = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_responseData = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_procResponse = StringUtils.EMPTY;
		String ls_dbResStatus = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson;
		JSONObjectImpl resDataJson;

		try {

			loggerImpl = new LoggerImpl();
			connection = getDbConnection();

			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doInsertUpdateAcctPhotoData");
			loggerImpl.generateProfiler("doInsertUpdateAcctPhotoData");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);
			ls_machineNm = getRequestUniqueData.getMachineName();
			ls_userNm = getRequestUniqueData.getUserName();
			ls_langCode = getRequestUniqueData.getLangCode();
			ls_compCd = requestDataJson.getString(COMP_CD);
			ls_branchCd = requestDataJson.getString(BRANCH_CD);
			ls_acctType = requestDataJson.getString(ACCT_TYPE);
			ls_acctCd = requestDataJson.getString(ACCT_CD);
			ls_photo = requestDataJson.getString(ACCT_PHOTO);
			ls_sign = requestDataJson.getString(ACCT_SIGN);
			
			
			
		

			loggerImpl.startProfiler("Calling doInsertUpdateAcctPhotoData API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, ls_machineNm,
					ls_userNm, ls_langCode);

			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_procResponse = getProcData.getCursorData(connection, ProcedureConstantName.PROC_UPD_PHOTO_SIGN,
					ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, base64StringtoBlob(connection, ls_photo), base64StringtoBlob(connection, ls_sign), ls_machineNm, ls_userNm,
					ls_langCode);

			resDataJson = common.ofGetJsonObject(ls_procResponse);
			ls_status = resDataJson.getString(STATUS);

			if (isSuccessStCode(ls_status)) {

				ls_dbResStatus = resDataJson.getJSONArray("RESPONSE").getJSONObject(0).getString("O_STATUS");
				if (isSuccessStCode(ls_dbResStatus)) {
					connection.commit();
					return ofGetResponseJson(new JSONArray(), "", "Request Accepted Successfully.", ST0, "G",
							"common.req_accepted").toString();
				} else {
					connection.rollback();
					return ls_procResponse;
				}
			} else {
				connection.rollback();
				return ls_procResponse;
			}

		} catch (Exception exception) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException sQLException) {
					sQLException.printStackTrace();
				}
			}
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doInsertUpdateAcctPhotoData", "(ENP340)");
		} finally {
			// close the database connections object.
			closeDbObject(connection);
		}

	}
}
