package com.easynet.service.AccountMapping.PassBook;

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
public class PassBookPrintData  extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(PassBookPrintData.class);

	private static final String PROC_GEN_PASSBOOK = "EASY_BANK.PACK_PASSBOOK.PROC_GEN_PASSBOOK";

	@Autowired
	private GetProcData getProcData;
	
	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String ofGetPassBookPrintData(String input) {

		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_entCompCd = StringUtils.EMPTY;
		String ls_fromDt = StringUtils.EMPTY;
		String ls_toDt = StringUtils.EMPTY;
		String ls_passType = StringUtils.EMPTY;
		String ls_printPage = StringUtils.EMPTY;
		String ls_templTranCd = StringUtils.EMPTY;
		String ls_lastLineNo = StringUtils.EMPTY;
		String ls_rePrint = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		String ls_userNm = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		JSONObjectImpl requestDataJson;
		Object fromDt,toDt;
		LoggerImpl loggerImpl = null;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:ofGetPassBookPrintData");
			loggerImpl.generateProfiler("ofGetPassBookPrintData");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);
			
			ls_compCd = requestDataJson.getString(COMP_CD);
			ls_branchCd = requestDataJson.getString(BRANCH_CD);
			ls_acctType= requestDataJson.getString(ACCT_TYPE);
			ls_acctCd = requestDataJson.getString(ACCT_CD);
			ls_entCompCd = requestDataJson.getString(ENTERED_BRANCH_CD);
			ls_fromDt = requestDataJson.getString(FROM_DT);	
			fromDt = getSqlDateFromString(ls_fromDt);	
			ls_toDt = requestDataJson.getString(TO_DT);
			toDt = getSqlDateFromString(ls_toDt);	
		//	ls_passType = requestDataJson.getString("PASSTYPE");
			ls_printPage= requestDataJson.getString("PRINT_PAGE");
			ls_templTranCd = requestDataJson.getString("TEMPL_TRAN_CD");	
			ls_lastLineNo = requestDataJson.getString("LAST_LINE_NO");		
			ls_rePrint = requestDataJson.getString("REPRINT");
			
			ls_userNm=getRequestUniqueData.getUserName();
			
		
			/*
			 * COMP_CD BRANCH_CD ACCT_TYPE ACCT_CD ENT_BRANCH_CD FR_DT TO_DT PASSTYPE
			 * PRINT_PAGE TEMPL_TRAN_CD LAST_LINE_NO REPRINT
			 */
			loggerImpl.startProfiler("Calling ofGetPassBookPrintData API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd,ls_acctType,ls_acctCd);

			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_response = getProcData.getCursorData(PROC_GEN_PASSBOOK,ls_compCd,ls_branchCd,ls_acctType,ls_acctCd,ls_entCompCd,fromDt,
					toDt,ls_printPage,ls_templTranCd,ls_lastLineNo,ls_userNm,ls_rePrint);

			return ls_response;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetPassBookPrintData", "(ENP398)");
		}
	}

}

