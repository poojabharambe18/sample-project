package com.easynet.service.AccountMapping.DropDown;

import static com.easynet.util.ConstantKeyValue.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.service.AccountMapping.AccountEnquiry.AcctCloseValidation;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Component
public class GetDirectorList extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(AcctCloseValidation.class);

	private static final String PROC_DIRECTOR_MST = "EASY_BANK.PROC_DIRECTOR_MST";

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String getDirectorList(String input) {

		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_role = StringUtils.EMPTY;
		String ls_procResponse = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;

		JSONObjectImpl requestDataJson;
		JSONObjectImpl resDataJson;

		LoggerImpl loggerImpl = null;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:getDirectorList");
			loggerImpl.generateProfiler("getDirectorList");
			loggerImpl.startProfiler("Preparing request data.");

			requestDataJson = common.ofGetJsonObject(input);
			ls_role = requestDataJson.getString(ROLE);
			ls_branchCd = getRequestUniqueData.getBranchCode();
			ls_compCd = getRequestUniqueData.getCompCode();

			loggerImpl.startProfiler("Calling getDirectorList API response data.");

			ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd, ls_role);

			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;

			ls_procResponse = getProcData.getCursorData(PROC_DIRECTOR_MST, ls_compCd, ls_branchCd, ls_role);

			return ls_procResponse;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:getDirectorList", "(ENP407)");
		}
	}

}
