//package com.easynet.service.AccountMapping.PassBook;
//
//import static com.easynet.util.ConstantKeyValue.*;
//
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.easynet.bean.GetRequestUniqueData;
//import com.easynet.dao.GetProcData;
//import com.easynet.impl.JSONObjectImpl;
//import com.easynet.impl.LoggerImpl;
//import com.easynet.util.CommonBase;
//import com.easynet.util.common;
//@Component
//public class PassBookPrintingValidation extends CommonBase {
//	
//	static Logger LOGGER = LoggerFactory.getLogger(PassBookPrintingValidation.class);
//
//	
//	private static final String PROC_PRINT_VALIDATION ="PACK_PASSBOOK.PROC_PRINT_VALIDATION";
//	
//	@Autowired
//	private GetProcData getProcData;
//	
//	@Autowired
//	GetRequestUniqueData getRequestUniqueData;
//	
//	public String doPassBookPrintingvalidation(String input) {
//		
//
//		String ls_compCd=StringUtils.EMPTY;
//		String ls_branchCd=StringUtils.EMPTY;
//		String ls_acct_type=StringUtils.EMPTY;
//		String ls_acctCd=StringUtils.EMPTY;
//		String ls_tranCd=StringUtils.EMPTY;
//		String ls_flag=StringUtils.EMPTY;
//		String ls_lineId=StringUtils.EMPTY;
//		String ls_line_per_page=StringUtils.EMPTY;
//		String ls_fromDt=StringUtils.EMPTY;
//		String ls_gd_date=StringUtils.EMPTY;
//		String ls_user=StringUtils.EMPTY;
//		String ls_user_level=StringUtils.EMPTY;
//		String ls_screen_ref=StringUtils.EMPTY;
//		String ls_lang=StringUtils.EMPTY;
//		
//		String ls_emptyResponseData=StringUtils.EMPTY;
//		String ls_response=StringUtils.EMPTY;
//		JSONObjectImpl requestDataJson;
//		LoggerImpl loggerImpl = null;
//		
//		try {
//			loggerImpl= new LoggerImpl();
//			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doPassBookPrintingvalidation");
//			loggerImpl.generateProfiler("doPassBookPrintingvalidation");
//			loggerImpl.startProfiler("Preparing request data.");
//			
//			requestDataJson = common.ofGetJsonObject(input);
//			ls_compCd=requestDataJson.getString(COMP_CD);
//			ls_branchCd=requestDataJson.getString(BRANCH_CD);
//			ls_acct_type=requestDataJson.getString(ACCT_TYPE);
//			ls_acctCd=requestDataJson.getString(ACCT_CD);
//			ls_tranCd=requestDataJson.getString(TRAN_CD);
//			ls_flag=requestDataJson.getString(FLAG);
//			ls_lineId=requestDataJson.getString(LINE_ID);
//			ls_line_per_page=requestDataJson.getString(LINE_PER_PAGE);
//			ls_fromDt=requestDataJson.getString(FROM_DT);
//			ls_gd_date=requestDataJson.getString(GD_DATE);
//			ls_user=getRequestUniqueData.getUserName();
//			ls_user_level=getRequestUniqueData.getUserRole();
//			ls_screen_ref=requestDataJson.getString(SCREEN_REF);
//			ls_lang=getRequestUniqueData.getLangCode();
//			
//		    loggerImpl.startProfiler("Calling doPassBookPrintingvalidation API response data.");
//			
//			ls_emptyResponseData=doCheckBlankData(ls_compCd,ls_branchCd,ls_acct_type,ls_acctCd,ls_tranCd);
//			if (StringUtils.isNotBlank(ls_emptyResponseData))
//				return ls_emptyResponseData;
//			
//			ls_response=getProcData.getCursorData(PROC_PRINT_VALIDATION, ls_compCd,ls_branchCd,ls_acct_type,ls_acctCd,ls_tranCd,ls_flag,ls_lineId,ls_line_per_page,ls_fromDt,ls_gd_date,ls_user,ls_user_level,ls_screen_ref,ls_lang);
//			return ls_response;
//			
//		} catch (Exception exception) {
//			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doPassBookPrintingvalidation", "(ENP490)");
//		}
//		
//	}
//}
