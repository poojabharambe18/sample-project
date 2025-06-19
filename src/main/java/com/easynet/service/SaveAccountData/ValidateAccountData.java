package com.easynet.service.SaveAccountData;

import static com.easynet.util.ConstantKeyValue.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.ProcedureConstantName;
import com.easynet.util.common;

@Component
public class ValidateAccountData extends CommonBase {

	static Logger LOGGER = LoggerFactory.getLogger(ValidateAccountData.class);

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String ofGetValidateAccountData(String input) {

		String screenRef = StringUtils.EMPTY;
		String ls_validationRes = StringUtils.EMPTY;
		String personalDtlData = StringUtils.EMPTY;
		String jointAcctDtlJlistData = StringUtils.EMPTY;
		String docDtlJlistData = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		String ls_updColumnName = StringUtils.EMPTY;

		boolean ib_NewRow;

		JSONObjectImpl reqJson = new JSONObjectImpl();
		JSONObjectImpl document_dtlJson = new JSONObjectImpl();
		JSONObjectImpl docJson = new JSONObjectImpl();
		JSONObjectImpl responseJson = new JSONObjectImpl();
		JSONObjectImpl oldMainDataDtlJson = new JSONObjectImpl();
		JSONObjectImpl oldMainDtlJson = new JSONObjectImpl();
		JSONObjectImpl mainDtlJson = new JSONObjectImpl();

		JSONArrayImpl oldDocDataJlist = new JSONArrayImpl();
		JSONArrayImpl updColumnJlist = new JSONArrayImpl();
		JSONArrayImpl document_dtlJlist = new JSONArrayImpl();
		JSONArrayImpl jointAccountList = new JSONArrayImpl();
		JSONArrayImpl oldJointDtlJlist = new JSONArrayImpl();
		JSONArrayImpl docJlist = new JSONArrayImpl();

		reqJson = common.ofGetJsonObject(input);
		ib_NewRow = reqJson.getBoolean("IsNewRow");
		screenRef = reqJson.getString(SCREEN_REF);
		LoggerImpl loggerImpl = null;

		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:ofGetValidateAccountData");
			loggerImpl.generateProfiler("ofGetValidateAccountData");
			loggerImpl.startProfiler("Preparing request data.");

			loggerImpl.startProfiler("Calling ofGetValidateAccountData API response data.");

			if (ib_NewRow) {

				// CHECK ALL THE VALIDATIONS AND ERR_MSG
				personalDtlData = reqJson.getJSONObject("MAIN_DETAIL").toString();

				if (reqJson.has("JOINT_ACCOUNT_DTL")) {
					jointAccountList = reqJson.getJSONArray("JOINT_ACCOUNT_DTL");
					jointAcctDtlJlistData = jointAccountList.toString();
				}

				if (reqJson.has("DOC_MST")) {
					document_dtlJlist = reqJson.getJSONArray("DOC_MST");
					docDtlJlistData = document_dtlJlist.toString();

					for (int i = 0; i < document_dtlJlist.length(); i++) {

						document_dtlJson = document_dtlJlist.getJSONObject(i);
						docJson.put(DOC_AMOUNT, document_dtlJson.getString(DOC_AMOUNT));
						docJson.put(DOC_NO, document_dtlJson.getString(DOC_NO));
						docJson.put(DOC_TYPE, document_dtlJson.getString(DOC_TYPE));
						docJson.put(DOC_WEIGHTAGE, document_dtlJson.getString(DOC_WEIGHTAGE));
						docJson.put(VALID_UPTO, document_dtlJson.getString(VALID_UPTO));
						docJson.put(TEMPLATE_CD, document_dtlJson.getString(TEMPLATE_CD));
						docJson.put(ACTIVE, document_dtlJson.getString(ACTIVE));
						docJson.put(SUBMIT, document_dtlJson.getString(SUBMIT));
						docJlist.put(docJson);

					}
				}

				ls_validationRes = getProcData.getCursorData(ProcedureConstantName.PROC_ACCT_VALIDATION,
						getRequestUniqueData.getCompCode(), getRequestUniqueData.getBranchCode(), personalDtlData,
						jointAcctDtlJlistData, docDtlJlistData, getRequestUniqueData.getUserName(),
						getRequestUniqueData.getUserRole(), screenRef, getRequestUniqueData.getLangCode());

				return ls_validationRes;

			} else {

				oldMainDataDtlJson = reqJson.getJSONObject("OLD_MAIN_DATA");
				oldDocDataJlist = reqJson.getJSONArray("OLD_DOCUMENT_DATA");
				oldJointDtlJlist = reqJson.getJSONArray("OLD_JOINT_DATA");
				// check validation before update details.

				if (reqJson.has("MAIN_DETAIL")) {
					mainDtlJson = reqJson.getJSONObject("MAIN_DETAIL");
					// personalDtlData = mainDtlJson.toString();
					updColumnJlist = mainDtlJson.getJSONArray("_UPDATEDCOLUMNS");

					for (int i = 0; i < updColumnJlist.length(); i++) {

						ls_updColumnName = updColumnJlist.getString(i);

						if (oldMainDataDtlJson.has(ls_updColumnName)) {
							// remove old value and put new updated value for validation.
							//oldMainDtlJson.remove(ls_updColumnName);
							//oldMainDtlJson.put(ls_updColumnName, mainDtlJson.getString(ls_updColumnName));
							
							oldMainDataDtlJson.put(ls_updColumnName, mainDtlJson.getString(ls_updColumnName));
						}

					}

				}

				if (reqJson.has("DOC_MST")) {

					document_dtlJlist = reqJson.getJSONArray("DOC_MST");
					docDtlJlistData = document_dtlJlist.toString();

					for (int j = 0; j < document_dtlJlist.length(); j++) {
						mainDtlJson = document_dtlJlist.getJSONObject(j);
						boolean chekUpdate = mainDtlJson.getBoolean("IsNewRow");
						if (!chekUpdate) 
						{
							if(oldDocDataJlist.length() != 0)
							oldMainDtlJson = oldDocDataJlist.getJSONObject(j);
							
							updColumnJlist = mainDtlJson.getJSONArray("_UPDATEDCOLUMNS");

							for (int i = 0; i < updColumnJlist.length(); i++) {

								ls_updColumnName = updColumnJlist.getString(i);

								if (oldMainDtlJson.has(ls_updColumnName)) {
									// remove old value and put new updated value for validation.
									oldMainDtlJson.remove(ls_updColumnName);
									oldMainDtlJson.put(ls_updColumnName, mainDtlJson.getString(ls_updColumnName));
								}
							}
						}
					}
				}

				if (reqJson.has("JOINT_ACCOUNT_DTL")) {
					jointAccountList = reqJson.getJSONArray("JOINT_ACCOUNT_DTL");
					jointAcctDtlJlistData = jointAccountList.toString();

					for (int j = 0; j < jointAccountList.length(); j++) {
						
						mainDtlJson = jointAccountList.getJSONObject(j);
						
						if(oldJointDtlJlist.length() != 0)
						 oldMainDtlJson = oldJointDtlJlist.getJSONObject(j);								
						
						updColumnJlist = mainDtlJson.getJSONArray("isUpdatedRow");

						for (int i = 0; i < updColumnJlist.length(); i++)
						{
							JSONObjectImpl updateDtlJson = updColumnJlist.getJSONObject(i);
							JSONArrayImpl updateDtlJlist = updateDtlJson.getJSONArray("_UPDATEDCOLUMNS");

							for (int k = 0; k < updateDtlJlist.length(); k++) {

								ls_updColumnName = updateDtlJlist.getString(k);
								if (oldMainDtlJson.has(ls_updColumnName)) {
									// remove old value and put new updated value for validation.
									oldMainDtlJson.remove(ls_updColumnName);
									oldMainDtlJson.put(ls_updColumnName, mainDtlJson.getString(ls_updColumnName));
								}
							}
						}
					}					
				}
				ls_validationRes = getProcData.getCursorData(ProcedureConstantName.PROC_ACCT_VALIDATION,
						getRequestUniqueData.getCompCode(), getRequestUniqueData.getBranchCode(),
						oldMainDataDtlJson.toString(), oldJointDtlJlist.toString(), oldDocDataJlist.toString(),
						getRequestUniqueData.getUserName(), getRequestUniqueData.getUserRole(), screenRef,
						getRequestUniqueData.getLangCode());

				return ls_validationRes;
			}

		} catch (

		Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doValidateAccountDetail", "(ENP599)");
		}

	}
}
