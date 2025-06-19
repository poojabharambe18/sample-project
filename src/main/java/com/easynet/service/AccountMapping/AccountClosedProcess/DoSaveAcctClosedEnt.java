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
public class DoSaveAcctClosedEnt  extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(DoSaveAcctClosedEnt.class);

	private static final String PROC_APPLY_CLOSE = "PACK_ACCT_CLOSE.PROC_APPLY_CLOSE";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetProcData getProcData;

	public String doSaveAcctClosedEnt(String input) {

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
		Object workingDt,lstInstDt;
		String ls_confBal = StringUtils.EMPTY;
		String ls_closeBal = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_amount = StringUtils.EMPTY;
		String ls_typeCd = StringUtils.EMPTY;
		String ls_oppBranch = StringUtils.EMPTY;
		String ls_oppAcctType = StringUtils.EMPTY;
		String ls_oppAcctCd = StringUtils.EMPTY;
		String ls_closedBal = StringUtils.EMPTY;
		String ls_confrimed = StringUtils.EMPTY;
		String ls_closeResCd = StringUtils.EMPTY;
		String ls_respDbStatus=StringUtils.EMPTY;
		String ls_chargeAmt = StringUtils.EMPTY;
		String ls_remarks = StringUtils.EMPTY;
		String ls_scroll = StringUtils.EMPTY;
		String ls_tokenNo = StringUtils.EMPTY;
		String ls_chequeNo = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_lstInstDt = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		JSONObjectImpl respJson;
		Connection connection = null;


		try {
			loggerImpl = new LoggerImpl();
			connection=getDbConnection();
			loggerImpl.info(LOGGER, "preparing the request data and calling the API", "IN:doSaveAcctClosedEnt");
			loggerImpl.generateProfiler("doSaveAcctClosedEnt");
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
			ls_oppBranch = reqJson.getString(OPP_BRANCH);
			ls_oppAcctType = reqJson.getString("OPP_ACCT_TYP");
			ls_oppAcctCd = reqJson.getString(OPP_ACCT_CD);
			ls_closedBal= reqJson.getString("CLOSE_BAL");
			ls_confrimed= reqJson.getString(CONFIRMED);
			ls_closeResCd= reqJson.getString("CLOSE_RES_CD");	
			ls_chargeAmt= reqJson.getString(CHARGE_AMT);
			ls_remarks= reqJson.getString(REMARKS);
			ls_scroll = reqJson.getString("SCROLL");
			ls_tokenNo = reqJson.getString(TOKEN_NO);	
			ls_chequeNo= reqJson.getString(CHEQUE_NO);
			ls_tranCd= reqJson.getString(TRAN_CD);
			ls_lstInstDt = reqJson.getString(LST_INT_DT);
			lstInstDt=getSqlDateFromString(ls_lstInstDt);
			ls_status = reqJson.getString(STATUS);
			ls_screenRef = reqJson.getString(SCREEN_REF);
			
			loggerImpl.startProfiler("Calling doSaveAcctClosedEnt API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_branchCd, ls_acctType, ls_acctCd, ls_screenRef);
			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_response = getProcData.getCursorData(connection,PROC_APPLY_CLOSE, ls_giCompCd, ls_giBranchCd, ls_compCd,ls_branchCd,ls_acctType,ls_acctCd,
					ls_amount,ls_typeCd,ls_oppBranch,ls_oppAcctType,ls_oppAcctCd,ls_closedBal,ls_confrimed,ls_closeResCd,
					ls_chargeAmt,ls_remarks,ls_scroll,ls_tokenNo,ls_chequeNo,ls_tranCd,lstInstDt,ls_status,workingDt,ls_userNm,
					ls_userLevel,ls_machineNm,ls_screenRef,ls_lang );

			respJson = common.ofGetJsonObject(ls_response);
			ls_respDbStatus = respJson.getString(STATUS);
			if (!isSuccessStCode(ls_respDbStatus)) {
				connection.rollback();
				return ls_response;
			}
			connection.commit();
			return ls_response;


		} catch (Exception exception) {

			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doSaveAcctClosedEnt", "(ENP565)");
		}finally {
			closeDbObject(connection);
		}
	}
}
