package com.easynet.service.AccountMapping.AccountClosedProcessConfirmation;

import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.service.AccountMapping.AccountClosedProcess.AccountClosedProcessData;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Service
public class GetConfrimReopenDtl extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(GetConfrimReopenDtl.class);

	private static final String PROC_APPLY_CONFIRM_REOPEN = "PACK_ACCT_CLOSE.PROC_APPLY_CONFIRM_REOPEN";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetProcData getProcData;

	public String ofGetConfrimReopenDtl(String input) {

		LoggerImpl loggerImpl = null;

		String ls_emptyResponseData = StringUtils.EMPTY;
		JSONObjectImpl reqJson;
		String ls_userNm = StringUtils.EMPTY;
		String ls_userLevel = StringUtils.EMPTY;
		String ls_workingDt = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_screenRef = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		String ls_machineNm = StringUtils.EMPTY;
		Object workingDt;
		String ls_entComphCd = StringUtils.EMPTY;
		String ls_entBranchCd = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_respDbStatus=StringUtils.EMPTY;
		String ls_confirmed = StringUtils.EMPTY;
		String ls_scroll = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_flag = StringUtils.EMPTY;
		String ls_enteredBy = StringUtils.EMPTY;
		JSONObjectImpl respJson;
		Connection connection = null;


		try {
			loggerImpl = new LoggerImpl();
			connection = getDbConnection();
			loggerImpl.info(LOGGER, "preparing the request data and calling the API", "IN:ofGetConfrimReopenDtl");
			loggerImpl.generateProfiler("ofGetConfrimReopenDtl");
			loggerImpl.startProfiler("Preparing request Data");

			reqJson = common.ofGetJsonObject(input);

			ls_userNm = getRequestUniqueData.getUserName();
			ls_lang = getRequestUniqueData.getLangCode();
			ls_userLevel = getRequestUniqueData.getUserRole();
			ls_workingDt = getRequestUniqueData.getWorkingDate();
			workingDt = getSqlDateFromString(ls_workingDt);
			ls_machineNm = getRequestUniqueData.getMachineIP();

			ls_entComphCd = reqJson.getString(ENTERED_COMP_CD);
			ls_entBranchCd = reqJson.getString(ENTERED_BRANCH_CD);
			ls_compCd = reqJson.getString(COMP_CD);
			ls_branchCd = reqJson.getString(BRANCH_CD);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);
			ls_confirmed = reqJson.getString(CONFIRMED);
			ls_scroll = reqJson.getString(SCROLL1);
			ls_tranCd = reqJson.getString(TRAN_CD);
			ls_status = reqJson.getString(STATUS);
			ls_flag = reqJson.getString(FLAG);
			ls_enteredBy = reqJson.getString(ENTERED_BY);
			ls_screenRef = reqJson.getString(SCREEN_REF);

			loggerImpl.startProfiler("Calling ofGetConfrimReopenDtl API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_entComphCd, ls_entBranchCd, ls_compCd, ls_branchCd, ls_acctType,
					ls_acctCd, ls_screenRef);
			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_response = getProcData.getCursorData(connection,PROC_APPLY_CONFIRM_REOPEN, ls_entComphCd, ls_entBranchCd, ls_compCd,
					ls_branchCd, ls_acctType, ls_acctCd, ls_confirmed, ls_scroll, ls_tranCd, ls_status, ls_flag,
					ls_enteredBy, workingDt, ls_userNm, ls_userLevel, ls_machineNm, ls_screenRef, ls_lang);

			respJson = common.ofGetJsonObject(ls_response);
			ls_respDbStatus = respJson.getString(STATUS);
			if (!isSuccessStCode(ls_respDbStatus)) {
				connection.rollback();
				return ls_response;
			}
			connection.commit();
			return ls_response;


		} catch (Exception exception) {

			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetConfrimReopenDtl", "(ENP576)");
		}finally {
			closeDbObject(connection);
		}
	}
}
