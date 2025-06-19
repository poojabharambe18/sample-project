package com.easynet.service.AccountMapping.Stock;

import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetFuctionData;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

import oracle.jdbc.OracleTypes;

@Service
public class DrawingPowerData extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(DrawingPowerData.class);

	private static final String PROC_GEN_DRAWING_POWER = "EASY_BANK.PROC_GEN_DRAWING_POWER";

	private static final String FUNC_SYS_PARA_MST_VALUE = "EASY_BANK.FUNC_SYS_PARA_MST_VALUE";

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetFuctionData getFuctionData;

	public JSONObjectImpl ofGetDrawingPowerData(Connection connection, String ls_compCd, String ls_branchCd,
			String ls_acctType, String ls_acctCd, Object asonDate) throws Exception {

		ArrayList<Object> returnDataList;
		ArrayList<Integer> procRequestDataList;
		Object returnData;
		JSONObjectImpl newJson;
		String ls_resMessage = StringUtils.EMPTY;

		procRequestDataList = new ArrayList<>();
		procRequestDataList.add(OracleTypes.VARCHAR);

		newJson = new JSONObjectImpl();

		returnData = getFuctionData.getAllTypeReturnValue(FUNC_SYS_PARA_MST_VALUE, OracleTypes.CHAR, ls_compCd,
				ls_branchCd, 16);

		returnDataList = getProcData.getprocedureAllOutData(connection, PROC_GEN_DRAWING_POWER, false,
				procRequestDataList, ls_compCd, ls_branchCd, ls_acctType, ls_acctCd, asonDate, returnData, "S");

		ls_resMessage = (String) returnDataList.get(0);

		if (StringUtils.isNotBlank(ls_resMessage)) {
			newJson.put("STATUS", 9);

		} else {
			newJson.put("STATUS", 0);
		}

		newJson.put("MESSAGE", ls_resMessage);
		return newJson;
	}

	/*
	 * public String ofGetDrawingPowerData(String input) {
	 * 
	 * String ls_compCd = StringUtils.EMPTY; String ls_branchCd = StringUtils.EMPTY;
	 * String ls_acctType = StringUtils.EMPTY; String ls_acctCd = StringUtils.EMPTY;
	 * String ls_asonDate = StringUtils.EMPTY; String ls_method = StringUtils.EMPTY;
	 * String ls_form = StringUtils.EMPTY; String ls_lang = StringUtils.EMPTY;
	 * String ls_WorkingDt = StringUtils.EMPTY;
	 * 
	 * String ls_response = StringUtils.EMPTY; String ls_emptyResponseData =
	 * StringUtils.EMPTY; JSONObjectImpl requestDataJson; Object asonDate
	 * ,returnData; LoggerImpl loggerImpl = null;
	 * 
	 * ArrayList<Object> returnDataList; ArrayList<Integer> procRequestDataList;
	 * 
	 * String ls_resStatus = StringUtils.EMPTY; String ls_resMessage =
	 * StringUtils.EMPTY;
	 * 
	 * JSONObjectImpl mainResJson;
	 * 
	 * try { loggerImpl = new LoggerImpl(); loggerImpl.info(LOGGER,
	 * "Preparing request data and calling API", "IN:ofGetDrawingPowerData");
	 * loggerImpl.generateProfiler("ofGetDrawingPowerData");
	 * loggerImpl.startProfiler("Preparing request data.");
	 * 
	 * requestDataJson = common.ofGetJsonObject(input);
	 * 
	 * ls_compCd = requestDataJson.getString(COMP_CD); ls_branchCd =
	 * requestDataJson.getString(BRANCH_CD); ls_acctType =
	 * requestDataJson.getString(ACCT_TYPE); ls_acctCd =
	 * requestDataJson.getString(ACCT_CD); ls_asonDate =
	 * requestDataJson.getString(ASON_DT); asonDate =
	 * getSqlDateFromString(ls_asonDate); //
	 * ls_method=requestDataJson.getString("METHOD"); //
	 * ls_form=requestDataJson.getString("FROM");
	 * 
	 * procRequestDataList = new ArrayList<>();
	 * procRequestDataList.add(OracleTypes.VARCHAR);
	 * 
	 * loggerImpl.startProfiler("Calling ofGetDrawingPowerData API response data.");
	 * 
	 * ls_emptyResponseData = doCheckBlankData(ls_compCd,
	 * ls_branchCd,ls_acctType,ls_acctCd,ls_asonDate);
	 * 
	 * if (StringUtils.isNotBlank(ls_emptyResponseData)) return
	 * ls_emptyResponseData;
	 * 
	 * returnData = getFuctionData.getAllTypeReturnValue(FUNC_SYS_PARA_MST_VALUE,
	 * OracleTypes.CHAR,ls_compCd, ls_branchCd, 16);
	 * 
	 * // ls_response = getProcData.getCursorData(PROC_GEN_DRAWING_POWER,ls_comp_cd,
	 * ls_branch_cd,ls_security_cd,ls_screen_ref,working_dt,ls_tran_date,
	 * ls_limit_amount, // ls_lang);
	 * 
	 * 
	 * returnDataList = getProcData.getprocedureAllOutData(PROC_GEN_DRAWING_POWER,
	 * procRequestDataList, ls_compCd,
	 * ls_branchCd,ls_acctType,ls_acctCd,asonDate,ls_method,"S");
	 * 
	 * JSONArrayImpl mainResJson1 = new JSONArrayImpl(returnDataList);
	 * 
	 * 
	 * // ls_resMessage = (String) returnDataList.get(0); // // if
	 * (!ls_resStatus.equals("0")) { // // mainResJson.put("MESSAGE",
	 * ls_resMessage); // // connection.rollback(); // // ls_response_db =
	 * ofGetResponseJson(new JSONArrayImpl(), " ", ls_resMessage, ls_resStatus, " ",
	 * // ls_resMessage).toString(); // }else // {
	 * 
	 * ls_response = ofGetResponseJson(new JSONArrayImpl(), "", "", ST0, "G",
	 * "").toString();
	 * 
	 * // }
	 * 
	 * return ls_response;
	 * 
	 * } catch (Exception exception) { return getExceptionMSg(exception, LOGGER,
	 * loggerImpl, "IN:ofGetDrawingPowerData", "(ENP491)"); } }
	 */

}
