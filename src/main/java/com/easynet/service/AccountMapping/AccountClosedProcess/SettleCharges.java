package com.easynet.service.AccountMapping.AccountClosedProcess;

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
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Service
public class SettleCharges extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(AccountClosedProcessData.class);

	private static final String PROC_SETTLE_CHARGES = "PACK_ACCT_CLOSE.PROC_SETTLE_CHARGES";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetProcData getProcData;

	public String ofGetsettleCharges(String input) {

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
		String ls_giCompCd = StringUtils.EMPTY;
		String ls_giBranchCd = StringUtils.EMPTY;
		String ls_machineNm = StringUtils.EMPTY;
		Object workingDt;
		String ls_confBal = StringUtils.EMPTY;
		String ls_closeBal = StringUtils.EMPTY;
		String ls_respDbStatus=StringUtils.EMPTY;
		JSONObjectImpl respJson;
		Connection connection = null;


		try {
			loggerImpl = new LoggerImpl();
			connection = getDbConnection();
			loggerImpl.info(LOGGER, "preparing the request data and calling the API", "IN:ofGetsettleCharges");
			loggerImpl.generateProfiler("ofGetsettleCharges");
			loggerImpl.startProfiler("Preparing request Data");

			reqJson = common.ofGetJsonObject(input);

			ls_userNm = getRequestUniqueData.getUserName();
			ls_lang = getRequestUniqueData.getLangCode();
			ls_userLevel = getRequestUniqueData.getUserRole();
			ls_workingDt = getRequestUniqueData.getWorkingDate();
			workingDt = getSqlDateFromString(ls_workingDt);
			ls_giCompCd = getRequestUniqueData.getCompCode();
			ls_giBranchCd = getRequestUniqueData.getBranchCode();
			ls_machineNm = getRequestUniqueData.getMachineIP();

			ls_branchCd = reqJson.getString(BRANCH_CD);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);
			ls_confBal = reqJson.getString("CONF_BAL");
			ls_closeBal = reqJson.getString("CLOSE_BAL");
			
			
			ls_screenRef = reqJson.getString(SCREEN_REF);
			

			loggerImpl.startProfiler("Calling ofGetsettleCharges API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_branchCd, ls_acctType, ls_acctCd, ls_screenRef);
			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_response = getProcData.getCursorData(connection,PROC_SETTLE_CHARGES, ls_giCompCd, ls_giBranchCd, ls_giCompCd,
					ls_branchCd, ls_acctType, ls_acctCd,ls_confBal,ls_closeBal, workingDt, ls_userNm, ls_userLevel, ls_screenRef,
					ls_lang);

			respJson = common.ofGetJsonObject(ls_response);
			ls_respDbStatus = respJson.getString(STATUS);
			if (!isSuccessStCode(ls_respDbStatus)) {
				connection.rollback();
				return ls_response;
			}
			connection.commit();
			return ls_response;


		} catch (Exception exception) {

			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetsettleCharges", "(ENP553)");
		}finally {
			closeDbObject(connection);
		}
	}
}
