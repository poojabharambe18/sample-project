//package com.easynet.service.AccountMapping.Stock;
//
//import static com.easynet.util.ConstantKeyValue.*;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import com.easynet.bean.GetRequestUniqueData;
//import com.easynet.dao.GetProcData;
//import com.easynet.impl.JSONObjectImpl;
//import com.easynet.impl.LoggerImpl;
//import com.easynet.util.CommonBase;
//import com.easynet.util.common;
//
//@Service
//public class ExpiryDate extends CommonBase {
//
//	static Logger LOGGER = LoggerFactory.getLogger(ExpiryDate.class);
//
//	private static final String PROC_GET_EXP_DT = "PACK_STOCK.PROC_GET_EXP_DT";
//
//	@Autowired
//	private GetProcData getProcData;
//
//	@Autowired
//	private GetRequestUniqueData getRequestUniqueData;
//
//	public String ofGetExpiryDate(String input) {
//
//		String ls_comp_cd = StringUtils.EMPTY;
//		String ls_branch_cd = StringUtils.EMPTY;
////		String ls_today_date = StringUtils.EMPTY;
//		String ls_security_cd = StringUtils.EMPTY;
//		String ls_tran_date = StringUtils.EMPTY;
//		String ls_screen_ref = StringUtils.EMPTY;
//		String ls_limit_amount = StringUtils.EMPTY;
//		String ls_lang = StringUtils.EMPTY;
//		String ls_WorkingDt = StringUtils.EMPTY;
//
//		String ls_response = StringUtils.EMPTY;
//		String ls_emptyResponseData = StringUtils.EMPTY;
//		JSONObjectImpl requestDataJson;
//		Object working_dt;
//		LoggerImpl loggerImpl = null;
//
//		try {
//			loggerImpl = new LoggerImpl();
//			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:ofGetExpiryDate");
//			loggerImpl.generateProfiler("ofGetExpiryDate");
//			loggerImpl.startProfiler("Preparing request data.");
//
//			requestDataJson = common.ofGetJsonObject(input);
//			
//			ls_WorkingDt=getRequestUniqueData.getWorkingDate();
//			working_dt = getSqlDateFromString(ls_WorkingDt);
//			
//			ls_comp_cd = requestDataJson.getString(COMP_CD);
//			ls_branch_cd = requestDataJson.getString(BRANCH_CD);
//			ls_security_cd = requestDataJson.getString(SECURITY_CD);		
//			ls_screen_ref = requestDataJson.getString(SCREEN_REF);
//	//		ls_today_date = requestDataJson.getString("GD_TD_DATE");
//			ls_tran_date = requestDataJson.getString(TRAN_DT);
//			ls_limit_amount=requestDataJson.getString(LIMIT_AMOUNT);
//			ls_lang = getRequestUniqueData.getLangCode();
//
//			loggerImpl.startProfiler("Calling ofGetExpiryDate API response data.");
//
//			ls_emptyResponseData = doCheckBlankData(ls_comp_cd, ls_branch_cd,ls_security_cd,ls_screen_ref,ls_WorkingDt,ls_tran_date,ls_limit_amount, 
//					ls_lang);
//
//			if (StringUtils.isNotBlank(ls_emptyResponseData))
//				return ls_emptyResponseData;
//
//			ls_response = getProcData.getCursorData(PROC_GET_EXP_DT,ls_comp_cd, ls_branch_cd,ls_security_cd,ls_screen_ref,working_dt,ls_tran_date,ls_limit_amount, 
//					ls_lang);
//
//			return ls_response;
//
//		} catch (Exception exception) {
//			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetExpiryDate", "(ENP296)");
//		}
//	}
//
//}
