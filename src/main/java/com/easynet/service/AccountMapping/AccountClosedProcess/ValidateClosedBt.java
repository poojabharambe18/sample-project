package com.easynet.service.AccountMapping.AccountClosedProcess;

import static com.easynet.util.ConstantKeyValue.*;
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
public class ValidateClosedBt extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(AccountClosedProcessData.class);

	private static final String PROC_VALIDATE_ACCT_CLOSE = "PACK_ACCT_CLOSE.PROC_VALIDATE_ACCT_CLOSE";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetProcData getProcData;

	public String ofGetValidateClosedBtData(String input) {

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
		Object workingDt,lstInstDt,opDate;
		String ls_confBal = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_amount = StringUtils.EMPTY;
		String ls_neft = StringUtils.EMPTY;
		String ls_ddPayslip = StringUtils.EMPTY;
		String ls_oppBranch = StringUtils.EMPTY;
		String ls_oppAcctType = StringUtils.EMPTY;
		String ls_oppAcctCd = StringUtils.EMPTY;
		String ls_opDate = StringUtils.EMPTY;
		String ls_tranBal = StringUtils.EMPTY;
		String ls_npaCd = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_custId = StringUtils.EMPTY;
		String ls_lstInstDt = StringUtils.EMPTY;
		
		String ls_typeCd = StringUtils.EMPTY;

		try {
			loggerImpl = new LoggerImpl();

			loggerImpl.info(LOGGER, "preparing the request data and calling the API", "IN:ofGetValidateClosedBtData");
			loggerImpl.generateProfiler("ofGetValidateClosedBtData");
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

			ls_compCd = reqJson.getString(COMP_CD);
			ls_branchCd = reqJson.getString(BRANCH_CD);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCd = reqJson.getString(ACCT_CD);
			ls_amount = reqJson.getString(AMOUNT);
			ls_typeCd = reqJson.getString(TYPE_CD);
			ls_neft= reqJson.getString(NEFT);
			ls_ddPayslip = reqJson.getString(DD_PAYSLIP);
			ls_oppBranch= reqJson.getString(OPP_BRANCH);
			ls_oppAcctType = reqJson.getString(OPP_ACCT_TYPE);
			ls_oppAcctCd = reqJson.getString(OPP_ACCT_CD);
			ls_opDate = reqJson.getString(OP_DATE);
			opDate=getSqlDateFromString(ls_opDate);
			ls_confBal = reqJson.getString(CONF_BAL);
			ls_tranBal = reqJson.getString(TRAN_BAL);
			ls_npaCd = reqJson.getString(NPA_CD);		
			ls_status= reqJson.getString(STATUS);
			ls_custId = reqJson.getString(CUSTOMER_ID);
			ls_lstInstDt = reqJson.getString(LST_INT_DT);	
			lstInstDt=getSqlDateFromString(ls_lstInstDt);
			ls_screenRef = reqJson.getString(SCREEN_REF);


			loggerImpl.startProfiler("Calling ofGetValidateClosedBtData API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_branchCd, ls_acctType, ls_acctCd, ls_screenRef);
			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_response = getProcData.getCursorData(PROC_VALIDATE_ACCT_CLOSE, ls_giCompCd, ls_giBranchCd, ls_compCd,
					ls_branchCd, ls_acctType, ls_acctCd,ls_amount,ls_typeCd,ls_neft,ls_ddPayslip,ls_oppBranch,ls_oppAcctType, ls_oppAcctCd,opDate,
					ls_confBal,ls_tranBal,ls_npaCd,ls_status,ls_custId,lstInstDt,
					workingDt, ls_userNm, ls_userLevel, ls_screenRef,ls_lang);

			return ls_response;

		} catch (Exception exception) {

			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetValidateClosedBtData", "(ENP558)");
		}
	}
}
