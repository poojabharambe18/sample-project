package com.easynet.service.AccountMapping.AcctPhotoData;

import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetProcData;
import com.easynet.dao.SelectData;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.service.SaveAccountData.SaveAccountData;
import com.easynet.util.CommonBase;
import com.easynet.util.ProcedureConstantName;
import com.easynet.util.common;

@Component
public class ConfirmAcctPhotoData extends CommonBase {

	private static Logger LOGGER = LoggerFactory.getLogger(ConfirmAcctPhotoData.class);

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String doConfirmAcctPhotoData(String input) {

		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		String ls_procResponse = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_role = StringUtils.EMPTY;
		String ls_userName = StringUtils.EMPTY;
		String ls_langCode = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_jType = StringUtils.EMPTY;
		String ls_screenRef = StringUtils.EMPTY;
		String ls_srCd = StringUtils.EMPTY;
		String ls_flag = StringUtils.EMPTY;
		String ls_dbResStatus = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson;
		JSONObjectImpl resDataJson;

		LoggerImpl loggerImpl = null;
		Connection connection = null;

		try {
			loggerImpl = new LoggerImpl();
			connection = getDbConnection();

			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doConfirmAcctPhotoData");
			loggerImpl.generateProfiler("doConfirmAcctPhotoData");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);
			ls_role = getRequestUniqueData.getUserRole();
			ls_userName = getRequestUniqueData.getUserName();
			ls_langCode = getRequestUniqueData.getLangCode();
			ls_compCd = requestDataJson.getString(COMP_CD);
			ls_branchCd = requestDataJson.getString(BRANCH_CD);
			ls_acctType = requestDataJson.getString(ACCT_TYPE);
			ls_acctCd = requestDataJson.getString(ACCT_CD);
			ls_jType = requestDataJson.getString(J_TYPE);
			ls_screenRef = requestDataJson.getString(SCREEN_REF);
			ls_srCd = requestDataJson.getString(SR_CD);
			ls_flag = requestDataJson.getString(FLAG);

			loggerImpl.startProfiler("Calling doConfirmAcctPhotoData API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, ls_jType, ls_srCd,
					ls_flag, ls_userName, ls_role, ls_screenRef, ls_langCode);

			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_procResponse = getProcData.getCursorData(connection, ProcedureConstantName.PROC_CONF_PHOTO_SIGN,
					ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, ls_jType, ls_srCd, ls_flag, ls_userName, ls_role,
					ls_screenRef, ls_langCode);

			resDataJson = common.ofGetJsonObject(ls_procResponse);
			ls_resStatus = resDataJson.getString(STATUS);

			if (isSuccessStCode(ls_resStatus)) {

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
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doConfirmAcctPhotoData", "(ENP397)");
		} finally {
			closeDbObject(connection);
		}

	}

}
