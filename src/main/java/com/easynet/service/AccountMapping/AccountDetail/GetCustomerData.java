package com.easynet.service.AccountMapping.AccountDetail;

import static com.easynet.util.ConstantKeyValue.*;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
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
import com.easynet.util.ProcedureConstantName;
import com.easynet.util.common;

import oracle.jdbc.OracleTypes;

@Service
public class GetCustomerData extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(GetCustomerData.class);

	// private static final String PROC_GET_CUST_ID_DATA = "PROC_GET_CUST_ID_DATA";

	@Autowired
	private GetProcData getProcData;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	public String getCustomerData(String input) {

		String ls_comp_cd = StringUtils.EMPTY;
		String ls_branch_cd = StringUtils.EMPTY;
		String ls_acct_type = StringUtils.EMPTY;
		String ls_cust_id = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_todayDt = StringUtils.EMPTY;
		String ls_screen_ref = StringUtils.EMPTY;
		String ls_user_Nm = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_proc_res_data = StringUtils.EMPTY;
		String ls_validation_res = StringUtils.EMPTY;
		String ls_res_msg = StringUtils.EMPTY;
		String ls_db_status = StringUtils.EMPTY;
		String ls_db_message = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson;
		JSONObjectImpl userJson;
		JSONObjectImpl responseDataJson;
		JSONObjectImpl res_legth;
		Object date;

		JSONArrayImpl resJlist;
		JSONArrayImpl resMsgJlist = new JSONArrayImpl();

		LoggerImpl loggerImpl = null;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:getCustomerData");
			loggerImpl.generateProfiler("getCustomerData");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);

			ls_comp_cd = getRequestUniqueData.getCompCode();
			ls_acct_type = requestDataJson.getString(ACCT_TYPE);
			ls_cust_id = requestDataJson.getString(CUSTOMER_ID);
			ls_screen_ref = requestDataJson.getString(SCREEN_REF);

			ls_branch_cd = getRequestUniqueData.getBranchCode();
			userJson = getRequestUniqueData.getLoginUserDetailsJson();
			ls_user_Nm = getRequestUniqueData.getUserName();
			ls_todayDt = getRequestUniqueData.getWorkingDate();
			ls_lang = getRequestUniqueData.getLangCode();
			date = getSqlDateFromString(ls_todayDt);

			loggerImpl.startProfiler("Calling getCustomerData API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_comp_cd, ls_branch_cd, ls_acct_type, ls_cust_id, ls_todayDt,
					ls_user_Nm, ls_screen_ref, ls_lang);

			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_validation_res = getProcData.getCursorData(ProcedureConstantName.PROC_GET_CUST_ID_DATA, ls_comp_cd,
					ls_branch_cd, ls_acct_type, ls_cust_id, date, ls_user_Nm, ls_screen_ref, ls_lang, "V");

			responseDataJson = common.ofGetJsonObject(ls_validation_res);

			ls_status = responseDataJson.getString(STATUS);

			if (isSuccessStCode(ls_status)) {
				resJlist = responseDataJson.getJSONArray("RESPONSE");

				resMsgJlist = resJlist.getJSONObject(0).getJSONArray("MSG");

				for (int i = 0; i < resMsgJlist.length(); i++) {

					res_legth  = resMsgJlist.getJSONObject(i);
					
					if (res_legth.length() > 0) {

						ls_db_message = resMsgJlist.getJSONObject(i).getString("O_MESSAGE");
						ls_db_status = resMsgJlist.getJSONObject(i).getString("O_STATUS");

						if (isSuccessStCode(ls_db_status) && ls_db_message.equals("SUCCESS")) {

							ls_proc_res_data = getProcData.getCursorData(ProcedureConstantName.PROC_GET_CUST_ID_DATA,
									ls_comp_cd, ls_branch_cd, ls_acct_type, ls_cust_id, date, ls_user_Nm, ls_screen_ref,
									ls_lang, "D");

							responseDataJson = common.ofGetJsonObject(ls_proc_res_data);

							ls_status = responseDataJson.getString(STATUS);

							if (!isSuccessStCode(ls_status)) {
								return ls_proc_res_data;
							} else {
								resJlist.getJSONObject(0).put("ACCOUNT_DTL",
										responseDataJson.getJSONArray("RESPONSE").getJSONObject(0));
							}

						}
					}
				}
			} else {
				return ls_validation_res;
			}

			return ofGetResponseJson(resJlist, "", "", ST0, "G", "").toString();

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:getCustomerData", "(ENP296)");
		}
	}

}
