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
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Service
public class NeftDdEntry extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(AccountClosedProcessData.class);

	private static final String PROC_GEN_ENTRY = "PACK_GEN_OTH_MODULE_DD_NEFT.PROC_GEN_ENTRY";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetProcData getProcData;

	public String ofDoNeftDdEntry(String input) {

		String ls_response = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_requestCd = StringUtils.EMPTY;
		// String ls_throughChannel = StringUtils.EMPTY;
		String ls_scroll1 = StringUtils.EMPTY;
		String ls_entCompCd = StringUtils.EMPTY;
		String ls_entBranchCd = StringUtils.EMPTY;
		String ls_machineNm = StringUtils.EMPTY;
		String ls_workingDate = StringUtils.EMPTY;
		String ls_userNm = StringUtils.EMPTY;
		String ls_userLevel = StringUtils.EMPTY;
		String ls_screenRef = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_commTypeCd = StringUtils.EMPTY;
		String ls_totDDNeftAmt = StringUtils.EMPTY;
		String ls_payFor = StringUtils.EMPTY;
		String ls_sdc = StringUtils.EMPTY;
		String ls_remarks = StringUtils.EMPTY;
		String ls_ddNeft = StringUtils.EMPTY;
		String ls_baseBranchCd = StringUtils.EMPTY;
		String ls_emptyRespData = StringUtils.EMPTY;
		String ls_ddNeftPayAmt = StringUtils.EMPTY;
		String ls_respDbStatus = StringUtils.EMPTY;
		JSONObjectImpl respJson;
		Connection connection = null;

		JSONArrayImpl paySlipNeftJList = new JSONArrayImpl();
		JSONObjectImpl reqJson;
		LoggerImpl loggerImpl = null;
		Object workingDate;

		try {

			loggerImpl = new LoggerImpl();
			connection = getDbConnection();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:ofDoNeftDdEntry");
			loggerImpl.generateProfiler("ofDoNeftDdEntry");
			loggerImpl.startProfiler("Preparing request data.");

			reqJson = common.ofGetJsonObject(input);
			ls_compCd = reqJson.getString(COMP_CD);
			ls_branchCd = reqJson.getString(BRANCH_CD);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);
			ls_tranCd = reqJson.getString(TRAN_CD);
			paySlipNeftJList = reqJson.getJSONArray("PAY_SLIP_NEFT_DTL");
			ls_commTypeCd = reqJson.getString(COMM_TYPE_CD);
			ls_totDDNeftAmt = reqJson.getString(TOT_DD_NEFT_AMT);
			ls_payFor = reqJson.getString("PAY_FOR");
			ls_sdc = reqJson.getString("SDC");
			ls_remarks = reqJson.getString(REMARKS);
			ls_ddNeft = reqJson.getString(DD_NEFT); // DD/NEFT
			ls_ddNeftPayAmt = reqJson.getString("DD_NEFT_PAY_AMT");
			ls_baseBranchCd = getRequestUniqueData.getBaseBranchCode();
			ls_scroll1 = reqJson.getString(SCROLL1);
			ls_requestCd = reqJson.getString(REQUEST_CD);

			ls_entCompCd = getRequestUniqueData.getCompCode();
			ls_entBranchCd = getRequestUniqueData.getBranchCode();
			ls_machineNm = getRequestUniqueData.getMachineName();
			ls_workingDate = getRequestUniqueData.getWorkingDate();
			workingDate = getSqlDateFromString(ls_workingDate);
			ls_userNm = getRequestUniqueData.getUserName();
			ls_userLevel = getRequestUniqueData.getUserRole();
			ls_screenRef = reqJson.getString(SCREEN_REF);
			ls_lang = getRequestUniqueData.getLangCode();
			// ls_throughChannel = getRequestUniqueData.getThroughChannel();

			loggerImpl.startProfiler("Calling ofDoNeftDdEntry API response data.");

			ls_emptyRespData = doCheckBlankData(paySlipNeftJList.toString(), ls_entCompCd, ls_entBranchCd, ls_acctType,
					ls_acctCd);

			if (StringUtils.isNotBlank(ls_emptyRespData))
				return ls_emptyRespData;

			ls_response = getProcData.getCursorData(connection, PROC_GEN_ENTRY, ls_entCompCd, ls_entBranchCd,
					ls_acctType, ls_acctCd, ls_commTypeCd, ls_totDDNeftAmt, ls_ddNeftPayAmt,
					paySlipNeftJList.toString(), ls_payFor, ls_sdc, ls_tranCd, ls_scroll1, null,
					ls_requestCd, ls_remarks, ls_ddNeft, ls_entBranchCd, ls_baseBranchCd, ls_machineNm, workingDate,
					ls_userNm, ls_userLevel, ls_screenRef, ls_lang);

			respJson = common.ofGetJsonObject(ls_response);
			ls_respDbStatus = respJson.getString(STATUS);
			if (!isSuccessStCode(ls_respDbStatus)) {
				connection.rollback();
				return ls_response;
			}
			connection.commit();
			return ls_response;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofDoNeftDdEntry", "(ENP572)");
		} finally {
			closeDbObject(connection);
		}
	}
}
