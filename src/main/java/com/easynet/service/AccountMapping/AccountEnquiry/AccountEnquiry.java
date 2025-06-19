package com.easynet.service.AccountMapping.AccountEnquiry;

import static com.easynet.util.ConstantKeyValue.*;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Service
public class AccountEnquiry extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(AccountEnquiry.class);

	private static final String ACCTINQUIRY = "PROC_ACCT_INQUIRY_SEARCH";

	@Autowired
	private GetProcData getProcData;

	public String ofGetAccountEnquiry(String input) {
		String ls_acct_no = StringUtils.EMPTY;
		String ls_mob_no = StringUtils.EMPTY;
		String ls_pan_no = StringUtils.EMPTY;
		String ls_cust_id = StringUtils.EMPTY;
		String ls_acct_nm = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_comp_cd = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson,responseJson;
		JSONArrayImpl responseJlist;
		LoggerImpl loggerImpl = null;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:ofGetAccountEnquiry");
			loggerImpl.generateProfiler("ofGetAccountEnquiry");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);

			ls_acct_no = requestDataJson.getString(ACCT_NO);
			ls_mob_no = requestDataJson.getString(MOB_NO);
			ls_pan_no = requestDataJson.getString(PAN_NO);
			ls_cust_id = requestDataJson.getString(CUST_ID);
			ls_acct_nm = requestDataJson.getString(ACCT_NM);
			ls_comp_cd = requestDataJson.getString(COMP_CD);
	
			loggerImpl.startProfiler("Calling ofGetAccountEnquiry API response data.");

			if (StringUtils.isNotBlank(ls_acct_no) || StringUtils.isNotBlank(ls_acct_nm) || StringUtils.isNotBlank(ls_mob_no)
					|| StringUtils.isNotBlank(ls_pan_no) || StringUtils.isNotBlank(ls_cust_id)) {
				ls_response = getProcData.getCursorData(ACCTINQUIRY,ls_comp_cd, ls_acct_no,ls_acct_nm, ls_mob_no, ls_pan_no, ls_cust_id);
				
			}
/*				responseJson=common.ofGetJsonObject(ls_response);
				
				ls_status=responseJson.getString(STATUS);
			 	
			 	if (!isSuccessStCode(ls_status)) {
			 		return ls_response;
				}
				
				responseJlist=responseJson.getJSONArray("RESPONSE");
				
				if(responseJlist.length()==0)
				{
					return ofGetResponseJson(new JSONArray(), " ", "No Data Found.", ST99, " ", "common.no_data_found")
							.toString();
				}
				
			}*/
			return ls_response;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:ofGetAccountEnquiry", "(ENP209)");
		}
		
	}

}
