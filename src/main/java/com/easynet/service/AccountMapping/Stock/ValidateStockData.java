//package com.easynet.service.AccountMapping.Stock;
//
//import static com.easynet.util.ConstantKeyValue.*;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.easynet.bean.GetRequestUniqueData;
//import com.easynet.dao.GetProcData;
//import com.easynet.impl.JSONObjectImpl;
//import com.easynet.impl.LoggerImpl;
//import com.easynet.util.CommonBase;
//import com.easynet.util.common;
//
//@Service
//public class ValidateStockData  extends CommonBase {
//
//	static Logger LOGGER = LoggerFactory.getLogger(ValidateStockData.class);
//
//	private static final String PROC_VALIDATION_SAVE_STK = "PACK_STOCK.PROC_VALIDATION_SAVE_STK";
//
//	@Autowired
//	private GetProcData getProcData;
//
//	@Autowired
//	private GetRequestUniqueData getRequestUniqueData;
//
//	public String DoValidateStockData(String input) {
//
//		String ls_comp_cd = StringUtils.EMPTY;
//		String ls_branch_cd = StringUtils.EMPTY;
//		String ls_today_date = StringUtils.EMPTY;
//		String ls_stockValue = StringUtils.EMPTY;
//		String ls_tran_date = StringUtils.EMPTY;
//		String ls_margin = StringUtils.EMPTY;
//		String ls_lang = StringUtils.EMPTY;
//		String ls_gi_compCd = StringUtils.EMPTY;
//		String ls_creditor = StringUtils.EMPTY;
//		String ls_securityCd = StringUtils.EMPTY;
//		String ls_stockMonth = StringUtils.EMPTY;
//		String ls_workingDate = StringUtils.EMPTY;
//		String ls_response = StringUtils.EMPTY;
//		String ls_emptyResponseData = StringUtils.EMPTY;
//		JSONObjectImpl requestDataJson;
//		LoggerImpl loggerImpl = null;
//		Object WorkingDate,tran_date;
//		String ls_asonDt = StringUtils.EMPTY;
//		String ls_recievedDt = StringUtils.EMPTY;
//
//		try {
//			loggerImpl = new LoggerImpl();
//			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:DoValidateStockData");
//			loggerImpl.generateProfiler("DoValidateStockData");
//			loggerImpl.startProfiler("Preparing request data.");
//
//			requestDataJson = common.ofGetJsonObject(input);
//			
//			ls_gi_compCd=getRequestUniqueData.getCompCode();
//			ls_lang = getRequestUniqueData.getLangCode();
//			ls_workingDate = getRequestUniqueData.getWorkingDate();
//			WorkingDate = getSqlDateFromString(ls_workingDate);
//		//	ls_comp_cd = requestDataJson.getString(COMP_CD);
//			ls_branch_cd = requestDataJson.getString(BRANCH_CD);
////			ls_today_date = requestDataJson.getString("GD_TD_DATE");
//			ls_tran_date = requestDataJson.getString(TRAN_DT );
//			tran_date = getSqlDateFromString(ls_tran_date);
//			ls_asonDt=requestDataJson.getString(ASON_DT);
//			ls_stockValue = requestDataJson.getString(STOCK_VALUE);		
//			ls_margin = requestDataJson.getString(MARGIN);
//			ls_creditor=requestDataJson.getString(CREDITOR);
//			ls_securityCd = requestDataJson.getString(SECURITY_CD);		
//			ls_stockMonth = requestDataJson.getString(STOCK_MONTH);
//			ls_recievedDt = requestDataJson.getString(RECEIVED_DT);
//			
//			
//			
//			loggerImpl.startProfiler("Calling DoValidateStockData API response data.");
//
//			ls_emptyResponseData = doCheckBlankData(ls_gi_compCd, ls_branch_cd,ls_workingDate,ls_lang);
//
//			if (StringUtils.isNotBlank(ls_emptyResponseData))
//				return ls_emptyResponseData;
//
//			ls_response = getProcData.getCursorData(PROC_VALIDATION_SAVE_STK,ls_gi_compCd, ls_branch_cd,ls_workingDate,tran_date,ls_asonDt,
//					ls_stockValue,ls_margin,ls_creditor,ls_securityCd,ls_stockMonth,ls_recievedDt,ls_lang);
//
//			return ls_response;
//
//		} catch (Exception exception) {
//			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:DoValidateStockData", "(ENP344)");
//		}
//	}
//
//}
