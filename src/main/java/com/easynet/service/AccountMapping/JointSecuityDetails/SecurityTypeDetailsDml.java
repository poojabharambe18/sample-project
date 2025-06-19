package com.easynet.service.AccountMapping.JointSecuityDetails;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.easynet.dao.DynamicDml;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SecurityTypeDetailsDml extends DynamicDml
{
	private Logger LOGGER = LoggerFactory.getLogger(SecurityTypeDetailsDml.class);

	public String doSecurityTypeDetails(String input) 
	{

		String table_Name = StringUtils.EMPTY;
		String ls_security_type = StringUtils.EMPTY;
		LoggerImpl loggerImpl = null  ;
		String ls_response = null;

		JSONObjectImpl reqJson;
		JSONObjectImpl confJson = null;
		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doSecurityTypeDetails");
			loggerImpl.generateProfiler("doSecurityTypeDetails");
			loggerImpl.startProfiler("Preparing request data.");
			reqJson = new JSONObjectImpl();
			reqJson =  common.ofGetJsonObject(input);

			loggerImpl.startProfiler("Calling doSecurityTypeDetails API response data.");


			ls_security_type = reqJson.getString("SECURITY_TYPE");

			if(ls_security_type.equals("STK") || ls_security_type.equals("BDC"))
				table_Name="EASY_BANK.JOINT_ACCOUNT_MST_STOCK";
			else if (ls_security_type.equals("SH"))
				table_Name="EASY_BANK.STOCK";
			else if (ls_security_type.equals("MCH") || ls_security_type.equals("VEH") 
					|| ls_security_type.equals("PRT"))
				table_Name="EASY_BANK.JOINT_ACCOUNT_MST_OTHER";
			else if (ls_security_type.equals("LIC"))
				table_Name="EASY_BANK.JOINT_ACCOUNT_MST_LIC";
			else if (ls_security_type.equals("OTH") || ls_security_type.equals("GOV")
					|| ls_security_type.equals("BRD") || ls_security_type.equals("BFD"))
				table_Name="EASY_BANK.OTHER_COLLATERAL_DTL";

			setSchemaName("EASY_BANK");

			confJson = setConfigurationJson("D","",table_Name,"1","","-2","-2","-2");			
			ls_response = DetailDML(reqJson.toString(), confJson);

			return ls_response;
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doSecurityTypeDetails", "(ENP657)");
		}
	}

}
