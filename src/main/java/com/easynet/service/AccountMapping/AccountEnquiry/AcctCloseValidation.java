package com.easynet.service.AccountMapping.AccountEnquiry;

import static com.easynet.util.ConstantKeyValue.*;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Component
public class AcctCloseValidation extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(AcctCloseValidation.class);

	private static final String PROC_AC_CLOSE_VALIDATION = "PACK_ACCT_MST.PROC_AC_CLOSE_VALIDATION";

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String doGetCheckAcctData(String input) {

		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctNo = StringUtils.EMPTY;
		String ls_customerId = StringUtils.EMPTY;
		String ls_confirmBal = StringUtils.EMPTY;
		String ls_tranBal = StringUtils.EMPTY;
		String ls_npaCd = StringUtils.EMPTY;
		String ls_openDt = StringUtils.EMPTY;
		String ls_lstIntComputeDt = StringUtils.EMPTY;
		String ls_tranDt = StringUtils.EMPTY;
		String ls_screenRef = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_userName = StringUtils.EMPTY;
		String ls_userLevel = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		String ls_dbStatus = StringUtils.EMPTY;
		String ls_resMessage = StringUtils.EMPTY;
		String ls_procResponse = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson;
		JSONObjectImpl resDataJson;

		LoggerImpl loggerImpl = null;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:ofGetCheckAcctData");
			loggerImpl.generateProfiler("ofGetCheckAcctData");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);
			ls_userName = getRequestUniqueData.getUserName();
			ls_lang = getRequestUniqueData.getLangCode();
			ls_tranDt = getRequestUniqueData.getWorkingDate();
			ls_userLevel = getRequestUniqueData.getUserRole();
			ls_branchCd = requestDataJson.getString(BRANCH_CD);
			ls_compCd = requestDataJson.getString(COMP_CD);
			ls_acctType = requestDataJson.getString(ACCT_TYPE);
			ls_acctNo = requestDataJson.getString(ACCT_CD);
			ls_confirmBal = requestDataJson.getString(CONF_BAL);
			ls_tranBal = requestDataJson.getString(TRAN_BAL);
			ls_npaCd = requestDataJson.getString(NPA_CD);
			ls_lstIntComputeDt = requestDataJson.getString(LST_INT_COMPUTE_DT);
			ls_openDt = requestDataJson.getString(OP_DATE);
			ls_customerId = requestDataJson.getString(CUSTOMER_ID);
			ls_screenRef = requestDataJson.getString(SCREEN_REF);

			loggerImpl.startProfiler("Calling ofGetCheckAcctData API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd, ls_acctType, ls_acctNo,
				              	ls_tranDt, ls_screenRef, ls_lang, ls_userName, ls_userLevel);

			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_procResponse = getProcData.getCursorData(PROC_AC_CLOSE_VALIDATION, ls_compCd, ls_branchCd, ls_acctType,
					ls_acctNo, ls_confirmBal, ls_tranBal, ls_npaCd, ls_openDt, ls_lstIntComputeDt, ls_tranDt,
					ls_customerId, ls_screenRef, ls_lang, ls_userName, ls_userLevel);

			return ls_procResponse;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetCheckAcctData", "(ENP348)");
		}
	}

}
