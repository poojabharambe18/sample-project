package com.easynet.service.SaveAccountData;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.ConstantKeyValue.*;

@Service
public class SaveAccountDetail extends DynamicDml {

	static Logger LOGGER = LoggerFactory.getLogger(SaveAccountDetail.class);

	static final String REQ_ACCT_MST = "REQ_ACCT_MST";
	static final String REQ_ACCT_MST_03 = "REQ_ACCT_MST_03";
	static final String REQ_ACCT_MST_HDR = "REQ_ACCT_MST_HDR";
	static final String REQ_ACCT_MST_OTHER_DTL = "REQ_ACCT_MST_OTHER_DTL";
	static final String REQ_ACCT_MOBILE_DTL = "REQ_ACCT_MOBILE_DTL";
	static final String REQ_ACCT_MST_RELATIVE_DTL = "REQ_ACCT_MST_RELATIVE_DTL";
	static final String REQ_JOINT_ACCOUNT_MST = "REQ_JOINT_ACCOUNT_MST";
	static final String REQ_ACCT_MST_DOC_TEMPLATE = "REQ_ACCT_MST_DOC_TEMPLATE";
	static final String REQ_ACCT_MST_DOC_TEMPLATE_SDT = "REQ_ACCT_MST_DOC_TEMPLATE_SDT";
	static final String REQ_ACCT_PARA_DTL = "REQ_ACCT_PARA_DTL";
	
	public static final List<String> ACCT_MST_03_ColumnList = Arrays.asList("EXPOSURE_TO_SENSITIVE_SECTOR", "SECURITY_DEPOSIT",
			"SECTORAL_ANALYSIS", "SECTOR", "RF_RAITING", "INVEST_IN_PLANT", "UDYAM_REG_DT","BUSINESS_CD", "INDUSTRY_CODE",
				"UDYAM_REG_NO", "ANNUAL_TURNOVER_SR_CD", "NPA_REASON", "RENRE_CD");

	private static final String GET_AUTO_CONFIRM_ACCT_DATA = "SELECT EASY_BANK.FUNC_SYS_PARA_MST_VALUE(:COMP_CD,:BRANCH_CD,33) AS PARA_33 FROM DUAL";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private UpdateExAccountData updateExAccountData;

	@Autowired
	private SelectData selectData;

	public String ofFinalSaveAccountData(String input, Connection connection, JSONObjectImpl reqJson,
			JSONObjectImpl userJson, String req_cd, int ACCESS_INSERT, int ACCESS_UPDATE)
	{
		String ls_personalDtl_res = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_validationRes = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		String ls_reqFlag = StringUtils.EMPTY;
		String ls_dbResMsg = StringUtils.EMPTY;
		String ls_dbResStatus = StringUtils.EMPTY;
		String ls_dbResponse = StringUtils.EMPTY;
		String ls_reqCd = StringUtils.EMPTY;
		String ls_confirmed = StringUtils.EMPTY;
		String ls_autoConfirmPara = StringUtils.EMPTY;
		String ls_updResponse = StringUtils.EMPTY;
		String ls_addressType = StringUtils.EMPTY;
		String ls_toLimit = StringUtils.EMPTY;
		String ls_from_limit = StringUtils.EMPTY;
		String ls_signGroup = StringUtils.EMPTY;
		String ls_jType = StringUtils.EMPTY;
		String ls_actFlag = StringUtils.EMPTY;
		String ls_custId = StringUtils.EMPTY;
		String ls_resHdrTable = StringUtils.EMPTY;
		String ls_industryCd = StringUtils.EMPTY;
		String ls_businessCd = StringUtils.EMPTY;
		String ls_recreCd = StringUtils.EMPTY;
		String ls_screenRef = StringUtils.EMPTY;
		String ls_npaReason = StringUtils.EMPTY;
		String ls_annTurnSrCd = StringUtils.EMPTY;
		String ls_udyamRegNo = StringUtils.EMPTY;
		String ls_udyamRegDt = StringUtils.EMPTY;
		String ls_intSkipFlag = StringUtils.EMPTY;
		String ls_investInPlant = StringUtils.EMPTY;
		String ls_updColumnName = StringUtils.EMPTY;
		String ls_workingDate = StringUtils.EMPTY;
		String ls_security_type = StringUtils.EMPTY;
		String table_Name = StringUtils.EMPTY;

		long ll_TranCd, ll_SrCd;

		JSONArrayImpl jallColumnList = new JSONArrayImpl();
		JSONArray jointAccountList = new JSONArray();
		JSONArrayImpl joint_acc_dtlJlist = new JSONArrayImpl();
		JSONArrayImpl document_dtlJlist = new JSONArrayImpl();
		JSONArrayImpl insDtlJlist = new JSONArrayImpl();
		JSONArrayImpl docJlist = new JSONArrayImpl();
		JSONArrayImpl documentJlist = new JSONArrayImpl();
		JSONArrayImpl updColumnJlist = new JSONArrayImpl();
		JSONArrayImpl jointDtlJlist = new JSONArrayImpl();
		JSONArrayImpl oldJointDtlJlist = new JSONArrayImpl();
		JSONArrayImpl oldDocDataJlist = new JSONArrayImpl();
		JSONArrayImpl other_add_dtlJlist;
		JSONArrayImpl relative_dtlJlist;
		JSONArrayImpl advanceConfig_dtlJlist;
		JSONArrayImpl photodtl_dtlJlist;
		JSONArrayImpl mobile_reg_dtlJlist;
		JSONArrayImpl IsNewRowJlist;
		JSONArrayImpl docmstArray;
		JSONArrayImpl dbresponseJlist;
		JSONArrayImpl dbJsonList;
		JSONArrayImpl ls_Other_Security = new JSONArrayImpl();

		boolean ib_NewRow;

		int USERROLE, li_lineCd, li_srCd;
		long ll_ENTRY_TYPE;

		JSONObjectImpl responseJson;
		JSONObjectImpl masterDataJson = new JSONObjectImpl();
		JSONObjectImpl mainDtlJson = new JSONObjectImpl();
		JSONObjectImpl docJson = new JSONObjectImpl();
		JSONObjectImpl insertJson = new JSONObjectImpl();
		JSONObject resDataJson = new JSONObjectImpl();
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
		JSONObjectImpl oldMainDtlJson = new JSONObjectImpl();
		JSONObjectImpl oldMainDataDtlJson = new JSONObjectImpl();
		JSONObjectImpl responseDataJson;
		JSONObjectImpl insDtlJson;
		JSONObjectImpl dbJson;
		JSONObjectImpl personal_dtlJson;
		JSONObjectImpl other_add_dtlJson;
		JSONObjectImpl mobile_dtlJson;
		JSONObjectImpl joint_acc_dtlJson;
		JSONObjectImpl other_security_dtlJson;
		JSONObjectImpl document_dtlJson;
		JSONObjectImpl relative_dtlJson;
		JSONObjectImpl advanceConfig_dtlJson;
		JSONObjectImpl photodtl_Json;
		JSONObjectImpl insertDataJson;
		JSONObjectImpl confJson;
		JSONObject requestJson;
		JSONArrayImpl vehicle_details;
		JSONObjectImpl vehicle_details_dtlJson;
		LoggerImpl loggerImpl = null;
		Object date;
		boolean is_ColumnPresent = false;

		try {
			
			loggerImpl = new LoggerImpl();
			
			USERROLE = Integer.parseInt((String) userJson.get("USERROLE"));
			ls_compCd = getRequestUniqueData.getCompCode();
			ls_branchCd = getRequestUniqueData.getBranchCode();
			ls_workingDate = getRequestUniqueData.getWorkingDate();		
			date = getSqlDateFromString(ls_workingDate);
			ls_reqFlag = reqJson.getString(REQ_FLAG);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_custId = reqJson.getString(CUSTOMER_ID);
			ls_acctCd = reqJson.getString(ACCT_CD);
			ib_NewRow = reqJson.getBoolean("IsNewRow");
			
			if (ib_NewRow)
			{
				// INSERT DATA IN REQ_ACCT_MST_HDR TABLE
				ls_resHdrTable = updateExAccountData.ofGetInsertAccountHdrData(connection, reqJson, userJson, "A");

				dbJson = common.ofGetJsonObject(ls_resHdrTable);
				ls_status = dbJson.getString(STATUS);

				if (isSuccessStCode(ls_status))
				{
					dbJsonList = dbJson.getJSONArray("RESPONSE");
					ls_reqCd = dbJsonList.getJSONObject(0).getString(REQ_CD);
				} else {
					return ls_resHdrTable;
				}
				resDataJson.put(REQ_CD, ls_reqCd);
				ll_ENTRY_TYPE = 1;
			}else
			{
				ls_reqCd = reqJson.getString(REQ_CD);
				
				ls_emptyResponseData = doCheckBlankData(ls_reqCd);

				if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;
				
				resDataJson.put(REQ_CD, ls_reqCd);
				
				jPrimaryDtl.put(COMP_CD, ls_compCd);
			
				jPrimaryDtl.put(BRANCH_CD, ls_branchCd);
				jPrimaryDtl.put(ACCT_CD, ls_reqCd);
				jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
				jPrimaryDtl.put(REQ_FLAG, ls_reqFlag);
				jPrimaryDtl.put(REQ_CD, ls_reqCd);
				
				jallColumnList = getColumnDefination("REQ_ACCT_MST_UPD_DTL", connection, true);
				ll_ENTRY_TYPE = getMaxSrCd(connection, "REQ_ACCT_MST_UPD_DTL", ENTRY_TYPE, jallColumnList, jPrimaryDtl);
				ll_ENTRY_TYPE = ll_ENTRY_TYPE + 1;
			}

			if (reqJson.has("MAIN_DETAIL")) 
			{
				personal_dtlJson = reqJson.getJSONObject("MAIN_DETAIL");
				ib_NewRow = personal_dtlJson.optBoolean("IsNewRow", false);
				USERROLE = Integer.parseInt((String) userJson.get("USERROLE"));

				if (ib_NewRow)
				{
					if (ACCESS_INSERT <= USERROLE)
					{
						// CHECK AUTOCONFIRM PARAMETER WORK STATRT
						ls_dbResponse = selectData.getSelectData(GET_AUTO_CONFIRM_ACCT_DATA, ls_compCd, ls_branchCd);

						responseDataJson = common.ofGetJsonObject(ls_dbResponse);
						ls_resStatus = responseDataJson.getString(STATUS);
						dbresponseJlist = responseDataJson.getJSONArray("RESPONSE");

						if (dbresponseJlist.length() != 0) 
						{
							ls_autoConfirmPara = dbresponseJlist.getJSONObject(0).getString("PARA_33");
						}

						if (ls_autoConfirmPara.equals("Y"))
						{
							ls_confirmed = "Y";
						} else {
							ls_confirmed = "N";
						}
						// CHECK AUTOCONFIRM PARAMETER WORK END
						
						ls_acctType = personal_dtlJson.getString(ACCT_TYPE);

						ls_emptyResponseData = doCheckBlankData(ls_acctType,personal_dtlJson.getString(CUSTOMER_ID));
						if (StringUtils.isNotBlank(ls_emptyResponseData))
							return ls_emptyResponseData;

						// INSERT DATA IN REQ_ACCT_MST TABLE.

						personal_dtlJson.put(ENT_COMP_CD, ls_compCd);
						personal_dtlJson.put(ENT_BRANCH_CD, ls_branchCd);
						personal_dtlJson.put(COMP_CD, ls_compCd);
						personal_dtlJson.put(BRANCH_CD, ls_branchCd);
						personal_dtlJson.put(REQ_FLAG, ls_reqFlag);
						personal_dtlJson.put(DBNR_BAL, "0");
						personal_dtlJson.put(SWEEP_BAL, "0");
						personal_dtlJson.put(EOD_TRAN_BAL, "0");
						personal_dtlJson.put(NET_REG, "N");
						personal_dtlJson.put(REQ_CD, ls_reqCd);
						personal_dtlJson.put(CONFIRMED, ls_confirmed);
						personal_dtlJson.put(ACCT_TYPE, ls_acctType);

						ls_updResponse = InsertData(connection, personal_dtlJson, userJson, 2, "", REQ_ACCT_MST);
						dbJson = common.ofGetJsonObject(ls_updResponse);
						ls_status = dbJson.getString(STATUS);

						if (!isSuccessStCode(ls_status)) 
						{
							return ls_updResponse;
						}
						
						// Other details Insertion work
						
						ls_industryCd = personal_dtlJson.getString(INDUSTRY_CODE);
						ls_businessCd = personal_dtlJson.getString(BUSINESS_CD);
						ls_recreCd = personal_dtlJson.getString(RENRE_CD);
						ls_npaReason = personal_dtlJson.getString(NPA_REASON);
						ls_annTurnSrCd = personal_dtlJson.getString(ANNUAL_TURNOVER_SR_CD);
						ls_udyamRegNo = personal_dtlJson.getString(UDYAM_REG_NO);
						ls_udyamRegDt = personal_dtlJson.getString(UDYAM_REG_DT);
						ls_investInPlant = personal_dtlJson.getString(INVEST_IN_PLANT);
						
						insertJson.put(REQ_FLAG, ls_reqFlag);
						insertJson.put(REQ_CD, ls_reqCd);
						insertJson.put(INDUSTRY_CODE, ls_industryCd);
						insertJson.put(BUSINESS_CD, ls_businessCd);
						insertJson.put(RENRE_CD, ls_recreCd);
						insertJson.put(NPA_REASON, ls_npaReason);
						insertJson.put(ANNUAL_TURNOVER_SR_CD, ls_annTurnSrCd);
						insertJson.put(UDYAM_REG_NO, ls_udyamRegNo);
						insertJson.put(UDYAM_REG_DT, ls_udyamRegDt);
						insertJson.put(INVEST_IN_PLANT, ls_investInPlant);	
						insertJson.put("RF_RAITING",personal_dtlJson.getString("RF_RAITING"));
						insertJson.put("SECTOR",personal_dtlJson.getString("SECTOR"));
						insertJson.put("SECTORAL_ANALYSIS",personal_dtlJson.getString("SECTORAL_ANALYSIS"));
						insertJson.put("SECURITY_DEPOSIT",personal_dtlJson.getString("SECURITY_DEPOSIT"));
						insertJson.put("EXPOSURE_TO_SENSITIVE_SECTOR",personal_dtlJson.getString("EXPOSURE_TO_SENSITIVE_SECTOR"));
						insertJson.put(COMP_CD, ls_compCd);
						insertJson.put(BRANCH_CD, ls_branchCd);
						insertJson.put(ACCT_TYPE, ls_acctType);
						insertJson.put(ACCT_CD, ls_acctCd);

						ls_updResponse = InsertData(connection, insertJson, userJson, 2, "", REQ_ACCT_MST_03);
						dbJson = common.ofGetJsonObject(ls_updResponse);
						ls_status = dbJson.getString(STATUS);

						if (!isSuccessStCode(ls_status))
						{
							return ls_updResponse;
						}

					} else {
						return ofGetFailedMSg("common.insertrights.error", "",
								"You do not have access to insert a new row. Please contact the administrator", null);
					}
				} else
				{
					if (ACCESS_UPDATE <= USERROLE) 
					{
						ls_updResponse = UpdateData(connection, personal_dtlJson, userJson, REQ_ACCT_MST);
						dbJson = common.ofGetJsonObject(ls_updResponse);
						ls_status = dbJson.getString(STATUS);

						if (!isSuccessStCode(ls_status))
						{
							return ls_updResponse;
						}
                         // Other details update work start
						JSONArray updatedColumns = personal_dtlJson.optJSONArray("_UPDATEDCOLUMNS");
						if (updatedColumns != null && updatedColumns.length() != 0) {
						    for (int i = 0; i < updatedColumns.length(); i++) {
						        String columnName = updatedColumns.getString(i);
						        if (ACCT_MST_03_ColumnList.contains(columnName)) {
						        	is_ColumnPresent = true;
						            break;
						        }
						    }
						}
			
						if(is_ColumnPresent)
						{
							ls_updResponse = UpdateData(connection, personal_dtlJson, userJson, REQ_ACCT_MST_03);
							dbJson = common.ofGetJsonObject(ls_updResponse);
							ls_status = dbJson.getString(STATUS);

							if (!isSuccessStCode(ls_status))
							{
								return ls_updResponse;
							}	
						}	
						// Other details update work ends
					} else {
						return ofGetFailedMSg("common.updaterights.error", "","You do not have access to record updates. Please contact the administrator", null);
					}
				}
			}
			
			
			
			//Term Loan tab - Vehicles and Machinery details
			if (reqJson.has("TERMLOAN_BTN_DTL")){
				//long sr_cd=0;
				vehicle_details = reqJson.getJSONArray("TERMLOAN_BTN_DTL");

				for (int i = 0; i < vehicle_details.length(); i++) {

					vehicle_details_dtlJson = vehicle_details.getJSONObject(i);

					ib_NewRow = vehicle_details_dtlJson.optBoolean("IsNewRow");	

					if(ib_NewRow) {
						if (ACCESS_INSERT <= USERROLE) {

							/*jPrimaryDtl.put(COMP_CD, ls_compCd);
							jPrimaryDtl.put(BRANCH_CD, ls_branchCd);
							jPrimaryDtl.put(ACCT_TYPE, ls_acctType);	
							jPrimaryDtl.put(REQ_FLAG, ls_reqFlag);
							jPrimaryDtl.put("J_TYPE", vehicle_details_dtlJson.getString("J_TYPE"));

							if(vehicle_details_dtlJson.getString(REQ_CD).equals(""))
								jPrimaryDtl.put(REQ_CD, ls_reqCd);
							else
								jPrimaryDtl.put(REQ_CD, vehicle_details_dtlJson.getString(REQ_CD));

							jallColumnList = getColumnDefination("REQ_JOINT_ACCOUNT_MST_OTHER", connection, true);
							sr_cd = getMaxSrCd(connection, "REQ_JOINT_ACCOUNT_MST_OTHER", SR_CD, jallColumnList,jPrimaryDtl);

							sr_cd++;*/

							vehicle_details_dtlJson.put(SR_CD,1);
							vehicle_details_dtlJson.put(LINE_ID,1);
							vehicle_details_dtlJson.put("LETTER_DP_FLAG","L");		
							vehicle_details_dtlJson.put("CERSAI_REGI","N");
							vehicle_details_dtlJson.put(ACTIVE,"Y");
							vehicle_details_dtlJson.put(REQ_FLAG, ls_reqFlag);
							vehicle_details_dtlJson.put("DOC_VALUE",0);
							vehicle_details_dtlJson.put(REQ_CD, ls_reqCd);
							/*if(vehicle_details_dtlJson.getString(REQ_CD).equals(""))
								vehicle_details_dtlJson.put(REQ_CD, ls_reqCd);*/

							ls_updResponse = InsertData(connection, vehicle_details_dtlJson, userJson, 2, "","REQ_JOINT_ACCOUNT_MST_OTHER");
							dbJson = common.ofGetJsonObject(ls_updResponse);
							ls_status = dbJson.getString(STATUS);

							if (!isSuccessStCode(ls_status)) {
								return ls_updResponse;
							}
							
						} else {
							return ofGetFailedMSg("common.insertrights.error", "",
									"You do not have access to insert a new row. Please contact the administrator",
									null);
						}
					}else {
						if (ACCESS_UPDATE <= USERROLE)
						{			
							ls_updResponse = UpdateData(connection, vehicle_details_dtlJson, userJson,"REQ_JOINT_ACCOUNT_MST_OTHER");

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}
						} else {
							return ofGetFailedMSg("common.updaterights.error", "",
									"You do not have access to record updates. Please contact the administrator", null);
						}
					}
				}
			}

				
			if (reqJson.has("OTHER_ADDRESS_DTL")) 
			{
				// OtherAddress details
				other_add_dtlJlist = reqJson.getJSONArray("OTHER_ADDRESS_DTL");
				int li_sr_cd = 0;
				for (int i = 0; i < other_add_dtlJlist.length(); i++)
				{
					other_add_dtlJson = other_add_dtlJlist.getJSONObject(i);
					ib_NewRow = other_add_dtlJson.optBoolean("IsNewRow");
					if (ib_NewRow)
					{
						if (ACCESS_INSERT <= USERROLE) 
						{
							li_sr_cd++;
							ls_addressType = other_add_dtlJson.getString(ADDRESS_TYPE);

							ls_emptyResponseData = doCheckBlankData(ls_addressType);
							if (StringUtils.isNotBlank(ls_emptyResponseData))
								return ls_emptyResponseData;

							other_add_dtlJson.put(REQ_CD, ls_reqCd);
							other_add_dtlJson.put(BRANCH_CD, ls_branchCd);
							other_add_dtlJson.put(COMP_CD, ls_compCd);
							other_add_dtlJson.put(REQ_FLAG, ls_reqFlag);
							other_add_dtlJson.put(SR_CD, li_sr_cd);
							other_add_dtlJson.put(ACCT_TYPE, ls_acctType);

							ls_updResponse = InsertData(connection, other_add_dtlJson, userJson, 2, "",REQ_ACCT_MST_OTHER_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}

						} else {
							return ofGetFailedMSg("common.insertrights.error", "",
									"You do not have access to insert a new row. Please contact the administrator",
									null);
						}
					} else {
						if (ACCESS_UPDATE <= USERROLE)
						{
							// GET MAXIMUM SR CD
							jPrimaryDtl.put(REQ_CD, ls_reqCd);
							jallColumnList = getColumnDefination("REQ_ACCT_MST_OTHER_DTL", connection, true);
							ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_OTHER_DTL", SR_CD, jallColumnList,jPrimaryDtl);

							IsNewRowJlist = new JSONArrayImpl();
							IsNewRowJlist = other_add_dtlJson.getJSONArray("isNewRow");

							if (IsNewRowJlist.length() != 0)
							{
								for (int k = 0; k < IsNewRowJlist.length(); k++)
								{
									ll_SrCd++;
									insertDataJson = new JSONObjectImpl();
									insertDataJson = IsNewRowJlist.getJSONObject(k);

									ls_addressType = insertDataJson.getString(ADDRESS_TYPE);

									ls_emptyResponseData = doCheckBlankData(ls_addressType);
									if (StringUtils.isNotBlank(ls_emptyResponseData))
										return ls_emptyResponseData;

									insertDataJson.put(REQ_CD, ls_reqCd);
									insertDataJson.put(BRANCH_CD, ls_branchCd);
									insertDataJson.put(COMP_CD, ls_compCd);
									insertDataJson.put(REQ_FLAG, ls_reqFlag);
									insertDataJson.put(SR_CD, ll_SrCd);
									insertDataJson.put(ACCT_TYPE, ls_acctType);
								}
							}
							ls_updResponse = UpdateDetailsData(connection, other_add_dtlJson, userJson, jPrimaryDtl,REQ_ACCT_MST_OTHER_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}

						} else {
							return ofGetFailedMSg("common.updaterights.error", "",
									"You do not have access to record updates. Please contact the administrator", null);
						}
					}
				}
			}

			if (reqJson.has("MOBILE_REG_DTL"))
			{
				mobile_reg_dtlJlist = reqJson.getJSONArray("MOBILE_REG_DTL");
				int li_sr_cd = 0;
				for (int i = 0; i < mobile_reg_dtlJlist.length(); i++)
				{
					// mobile details
					mobile_dtlJson = mobile_reg_dtlJlist.getJSONObject(i);
					ib_NewRow = mobile_dtlJson.optBoolean("IsNewRow");

					if (ib_NewRow)
					{
						if (ACCESS_INSERT <= USERROLE)
						{
							li_sr_cd++;
							mobile_dtlJson.put(REQ_CD, ls_reqCd);
							mobile_dtlJson.put(BRANCH_CD, ls_branchCd);
							mobile_dtlJson.put(COMP_CD, ls_compCd);
							mobile_dtlJson.put(REQ_FLAG, ls_reqFlag);
							mobile_dtlJson.put(SR_CD, li_sr_cd);
							//mobile_dtlJson.put("REG_TYPE","M");
							mobile_dtlJson.put(ACCT_TYPE, ls_acctType);

							ls_updResponse = InsertData(connection, mobile_dtlJson, userJson, 2, "",REQ_ACCT_MOBILE_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}

						} else {
							return ofGetFailedMSg("common.insertrights.error", "",
									"You do not have access to insert a new row. Please contact the administrator",
									null);
						}
					} else {
						if (ACCESS_UPDATE <= USERROLE) 
						{
							// GET MAXIMUM SR CD
							
							jPrimaryDtl.put(REQ_CD, ls_reqCd);
							jallColumnList = getColumnDefination("REQ_ACCT_MOBILE_DTL", connection, true);
							ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MOBILE_DTL", SR_CD, jallColumnList, jPrimaryDtl);

							IsNewRowJlist = new JSONArrayImpl();
							IsNewRowJlist = mobile_dtlJson.getJSONArray("isNewRow");

							if (IsNewRowJlist.length() != 0)
							{
								for (int k = 0; k < IsNewRowJlist.length(); k++) 
								{
									ll_SrCd++;
									insertDataJson = new JSONObjectImpl();
									insertDataJson = IsNewRowJlist.getJSONObject(i);

									insertDataJson.put(REQ_CD, ls_reqCd);
									insertDataJson.put(BRANCH_CD, ls_branchCd);
									insertDataJson.put(COMP_CD, ls_compCd);				
									insertDataJson.put(REQ_FLAG, ls_reqFlag);
									insertDataJson.put(SR_CD, ll_SrCd);
									insertDataJson.put(ACCT_TYPE, ls_acctType);
									//insertDataJson.put("REG_TYPE","M");
								}
							}
							ls_updResponse = UpdateDetailsData(connection, mobile_dtlJson, userJson, jPrimaryDtl,REQ_ACCT_MOBILE_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}

						} else {
							return ofGetFailedMSg("common.updaterights.error", "",
									"You do not have access to record updates. Please contact the administrator", null);
						}
					}
				}
			}

			if (reqJson.has("JOINT_ACCOUNT_DTL")) 
			{
				joint_acc_dtlJlist = reqJson.getJSONArray("JOINT_ACCOUNT_DTL");
				long li_sr_cd = 0;
				long line_cd = 0;
				for (int i = 0; i < joint_acc_dtlJlist.length(); i++) 
				{
					// joint account details
					joint_acc_dtlJson = joint_acc_dtlJlist.getJSONObject(i);
					ib_NewRow = joint_acc_dtlJson.optBoolean("IsNewRow");
					
								
					/*
					 * if( ib_NewRow && joint_acc_dtlJson.has("OTHER_SECURITY_TYPE")) {
					 * ls_security_type=joint_acc_dtlJson.getString("SECURITY_TYPE");
					 * 
					 * if(ls_security_type.equals("STK") || ls_security_type.equals("BDC"))
					 * table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_STOCK"; else if
					 * (ls_security_type.equals("SH")) table_Name="EASY_BANK.STOCK"; else if
					 * (ls_security_type.equals("MCH") || ls_security_type.equals("VEH") ||
					 * ls_security_type.equals("PRT"))
					 * table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_OTHER"; else if
					 * (ls_security_type.equals("LIC"))
					 * table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_LIC"; else if
					 * (ls_security_type.equals("OTH") || ls_security_type.equals("GOV") ||
					 * ls_security_type.equals("BRD") || ls_security_type.equals("BFD"))
					 * table_Name="ENFINITY.REQ_OTHER_COLLATERAL_DTL"; }
					 */

					if (ib_NewRow) 
					{
						if (ACCESS_INSERT <= USERROLE) 
						{
							jPrimaryDtl.put(REQ_CD, ls_reqCd);
							jPrimaryDtl.put(J_TYPE, joint_acc_dtlJson.getString(J_TYPE));	
							jallColumnList = getColumnDefination("REQ_JOINT_ACCOUNT_MST", connection, true);
							li_sr_cd = getMaxSrCd(connection, "REQ_JOINT_ACCOUNT_MST", SR_CD, jallColumnList,jPrimaryDtl);
							li_sr_cd++;

							joint_acc_dtlJson.put(REQ_CD, ls_reqCd);
							joint_acc_dtlJson.put(BRANCH_CD, ls_branchCd);
							joint_acc_dtlJson.put(COMP_CD, ls_compCd);
							joint_acc_dtlJson.put(REQ_FLAG, ls_reqFlag);
							joint_acc_dtlJson.put(SR_CD, li_sr_cd);
							joint_acc_dtlJson.put("ACTIVE_FLAG", "Y");
							joint_acc_dtlJson.put(ACCT_TYPE, ls_acctType);
							//joint_acc_dtlJson.put(J_TYPE, "A");

							ls_updResponse = InsertData(connection, joint_acc_dtlJson, userJson, 2, "",REQ_JOINT_ACCOUNT_MST);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus))
							{
								return ls_updResponse;
							}
							
							// other security details insertion work start 
							/*
							 * if(joint_acc_dtlJson.getString(J_TYPE).trim() == "M" &&
							 * joint_acc_dtlJson.has("OTHER_SECURITY_TYPE")) { ls_Other_Security =
							 * joint_acc_dtlJson.getJSONArray("OTHER_SECURITY_TYPE");
							 * 
							 * 
							 * for (int j = 0; j < ls_Other_Security.length(); j++) { // joint account
							 * details other_security_dtlJson = ls_Other_Security.getJSONObject(j);
							 * 
							 * line_cd++; other_security_dtlJson.put(REQ_CD, ls_reqCd);
							 * other_security_dtlJson.put(BRANCH_CD, ls_branchCd);
							 * other_security_dtlJson.put(COMP_CD, ls_compCd);
							 * other_security_dtlJson.put(ACCT_TYPE, ls_acctType);
							 * other_security_dtlJson.put(REQ_FLAG, ls_reqFlag);
							 * other_security_dtlJson.put(SR_CD, li_sr_cd);
							 * other_security_dtlJson.put("LINE_CD",line_cd);
							 * other_security_dtlJson.put("ACTIVE_FLAG", "Y");
							 * other_security_dtlJson.put(J_TYPE, joint_acc_dtlJson.getString(J_TYPE));
							 * 
							 * ls_updResponse = InsertData(connection, other_security_dtlJson, userJson, 2,
							 * "",table_Name);
							 * 
							 * JSONObjectImpl db_securty_resJson = common.ofGetJsonObject(ls_updResponse);
							 * String ls_db_securty__resstatus = db_securty_resJson.getString(STATUS);
							 * 
							 * if (!isSuccessStCode(ls_db_securty__resstatus)) { return ls_updResponse; } }
							 * }
							 */
							// other security details insertion work end
							
						}else 
						{
							return ofGetFailedMSg("common.insertrights.error", "",
									"You do not have access to insert a new row. Please contact the administrator",
									null);
						}
					} else {
						if (ACCESS_UPDATE <= USERROLE)
						{
							// GET MAXIMUM SR CD
							jPrimaryDtl.put(REQ_CD, ls_reqCd);	
							jPrimaryDtl.put(BRANCH_CD, ls_branchCd);
							jPrimaryDtl.put(COMP_CD, ls_compCd);
							jPrimaryDtl.put(REQ_FLAG, ls_reqFlag);
							jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
								
							IsNewRowJlist = new JSONArrayImpl();
							IsNewRowJlist = joint_acc_dtlJson.getJSONArray("isNewRow");

							if (IsNewRowJlist.length() != 0) 
							{
								for (int k = 0; k < IsNewRowJlist.length(); k++)
								{
									insertDataJson = new JSONObjectImpl();
									insertDataJson = IsNewRowJlist.getJSONObject(k);

									ls_jType = insertDataJson.getString(J_TYPE);

									ls_emptyResponseData = doCheckBlankData(ls_jType);
									if (StringUtils.isNotBlank(ls_emptyResponseData))
										return ls_emptyResponseData;
									
									jPrimaryDtl.put(J_TYPE,ls_jType);
									jallColumnList = getColumnDefination("REQ_JOINT_ACCOUNT_MST", connection, true);
									ll_SrCd = getMaxSrCd(connection, "REQ_JOINT_ACCOUNT_MST", SR_CD, jallColumnList,jPrimaryDtl);
									ll_SrCd++;
									insertDataJson.put(REQ_CD, ls_reqCd);
									insertDataJson.put(BRANCH_CD, ls_branchCd);
									insertDataJson.put(COMP_CD, ls_compCd);
									insertDataJson.put(REQ_FLAG, ls_reqFlag);
									insertDataJson.put(SR_CD, ll_SrCd);
									insertDataJson.put("ACTIVE_FLAG", "Y");
									insertDataJson.put(ACCT_TYPE, ls_acctType);
									insertDataJson.put(J_TYPE, ls_jType);
								
									jPrimaryDtl.remove(J_TYPE);  // don't remove this line
								}
							}
							
							ls_updResponse = UpdateDetailsData(connection, joint_acc_dtlJson, userJson, jPrimaryDtl,REQ_JOINT_ACCOUNT_MST);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}
							
							/*
							 * ls_updResponse = collateralOtherSecurityButton(connection, joint_acc_dtlJson,
							 * userJson, jPrimaryDtl,reqJson);
							 * 
							 * JSONObjectImpl securityJson = common.ofGetJsonObject(ls_updResponse); String
							 * ls_securitystatus = securityJson.getString(STATUS);
							 * 
							 * if (!isSuccessStCode(ls_securitystatus)) { return ls_updResponse; }
							 */

						} else {
							return ofGetFailedMSg("common.updaterights.error", "",
									"You do not have access to record updates. Please contact the administrator", null);
						}
					}
				}
			}

			if (reqJson.has("DOC_MST")) 
			{
				docmstArray = reqJson.getJSONArray("DOC_MST");
				// GET MAXIMUM SR CD AND TRANCD

				ll_SrCd = 0;
				for (int k = 0; k < docmstArray.length(); k++)
				{
					masterDataJson = docmstArray.getJSONObject(k);
					ib_NewRow = masterDataJson.optBoolean("_isNewRow");
								
					jPrimaryDtl.put(REQ_CD, ls_reqCd);
					jPrimaryDtl.put(COMP_CD, ls_compCd);
					jPrimaryDtl.put(BRANCH_CD, ls_branchCd);
					jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
							
					if (ib_NewRow) 
					{
						ll_TranCd = getMaxCd(connection, "EASY_BANK.ACCT_MST_DOC_TEMPLATE", TRAN_CD, 0,"EASY_BANK.SEQ_ACCT_MST_DOC_TEMPLATE");
						
						if(ll_SrCd == 0 )
						{
							jallColumnList = getColumnDefination("REQ_ACCT_MST_DOC_TEMPLATE", connection, true);
							ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_DOC_TEMPLATE", "SR_CD", jallColumnList,jPrimaryDtl);
						}
						
						ll_SrCd++;
						masterDataJson.put(REQ_CD, ls_reqCd);
						masterDataJson.put(TRAN_CD, ll_TranCd);
						masterDataJson.put(SR_CD, ll_SrCd);
						masterDataJson.put(REQ_FLAG, ls_reqFlag);
						masterDataJson.put(COMP_CD, ls_compCd);
						masterDataJson.put(BRANCH_CD, ls_branchCd);
						masterDataJson.put(ENT_COMP_CD, ls_compCd);
						masterDataJson.put(ENT_BRANCH_CD, ls_branchCd);
						masterDataJson.put(ACCT_TYPE, ls_acctType);
						masterDataJson.put("DOC_TYPE", "ACCT");
						masterDataJson.put(ACTIVE, "Y");

						insDtlJlist = masterDataJson.getJSONObject("DETAILS_DATA").getJSONArray("isNewRow");
						li_lineCd = 1;
						for (int j = 0; j < insDtlJlist.length(); j++) {

							insDtlJson = insDtlJlist.getJSONObject(j);
							insDtlJson.put(REQ_CD, ls_reqCd);
							insDtlJson.put(TRAN_CD, ll_TranCd);
							insDtlJson.put(SR_CD, ll_SrCd);
							insDtlJson.put(REQ_FLAG, ls_reqFlag);
							insDtlJson.put(COMP_CD, ls_compCd);
							insDtlJson.put(BRANCH_CD, ls_branchCd);
							insDtlJson.put(ENT_COMP_CD, ls_compCd);
							insDtlJson.put(ENT_BRANCH_CD, ls_branchCd);
							insDtlJson.put(ACCT_TYPE, ls_acctType);
							insDtlJson.put("LINE_CD", li_lineCd);
							insDtlJson.put("DOC_TYPE", "ACCT");
							li_lineCd++;
						}

					} else {
						insDtlJlist = masterDataJson.getJSONObject("DETAILS_DATA").getJSONArray("isNewRow");
                     
						
						jPrimaryDtl.put(SR_CD, masterDataJson.getString(SR_CD));
						jallColumnList = getColumnDefination("REQ_ACCT_MST_DOC_TEMPLATE_SDT", connection, true);
						Long ls_line_Cd = getMaxSrCd(connection, "REQ_ACCT_MST_DOC_TEMPLATE_SDT", "LINE_CD", jallColumnList,jPrimaryDtl);
						
						for (int j = 0; j < insDtlJlist.length(); j++)
						{
							ls_line_Cd++;
							insDtlJson = insDtlJlist.getJSONObject(j);
							insDtlJson.put(REQ_CD, ls_reqCd);
							//insDtlJson.put(TRAN_CD, ll_TranCd);
							insDtlJson.put(SR_CD, ll_SrCd);
							insDtlJson.put(REQ_FLAG, ls_reqFlag);
							insDtlJson.put(COMP_CD, ls_compCd);
							insDtlJson.put(BRANCH_CD, ls_branchCd);
							insDtlJson.put(ENT_COMP_CD, ls_compCd);
							insDtlJson.put(ENT_BRANCH_CD, ls_branchCd);
							insDtlJson.put(ACCT_TYPE, ls_acctType);
							insDtlJson.put("LINE_CD", ls_line_Cd);
							insDtlJson.put("DOC_TYPE", "ACCT");
						}
					}

					confJson = setConfigurationJson("MD", REQ_ACCT_MST_DOC_TEMPLATE, REQ_ACCT_MST_DOC_TEMPLATE_SDT, "2",
							"", "-2", "-2", "-2",true);

					ls_updResponse = MasterDetailDML(masterDataJson.toString(), confJson);
					JSONObjectImpl dbJson_doc_mst = common.ofGetJsonObject(ls_updResponse);
					String ls_status_docmst = dbJson_doc_mst.getString(STATUS);

					if (!isSuccessStCode(ls_status_docmst)) {
						return ls_updResponse;
					}
				}
			}
			
			if (reqJson.has("RELATIVE_DTL"))
			{
				int li_sr_cd = 0;
				relative_dtlJlist = reqJson.getJSONArray("RELATIVE_DTL");
				for (int i = 0; i < relative_dtlJlist.length(); i++)
				{
					// relative details
					relative_dtlJson = relative_dtlJlist.getJSONObject(i);
					ib_NewRow = relative_dtlJson.optBoolean("IsNewRow");

					if (ib_NewRow) 
					{
						if (ACCESS_INSERT <= USERROLE)
						{
							li_sr_cd++;
							relative_dtlJson.put(REQ_CD, ls_reqCd);
							relative_dtlJson.put(BRANCH_CD, ls_branchCd);
							relative_dtlJson.put(COMP_CD, ls_compCd);
							relative_dtlJson.put(REQ_FLAG, ls_reqFlag);
							relative_dtlJson.put(SR_CD, li_sr_cd);
							relative_dtlJson.put(ACCT_TYPE, ls_acctType);

							ls_updResponse = InsertData(connection, relative_dtlJson, userJson, 2, "",REQ_ACCT_MST_RELATIVE_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}
						} else {
							return ofGetFailedMSg("common.insertrights.error", "",
									"You do not have access to insert a new row. Please contact the administrator",
									null);
						}
					} else {
						if (ACCESS_UPDATE <= USERROLE) {
							// GET MAXIMUM SR CD
							
							jPrimaryDtl.put(REQ_CD, ls_reqCd);
							jallColumnList = getColumnDefination("REQ_ACCT_MST_RELATIVE_DTL", connection, true);
							ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_RELATIVE_DTL", SR_CD, jallColumnList,jPrimaryDtl);

							IsNewRowJlist = new JSONArrayImpl();
							IsNewRowJlist = relative_dtlJson.getJSONArray("isNewRow");

							if (IsNewRowJlist.length() != 0) 
							{
								for (int k = 0; k < IsNewRowJlist.length(); k++) 
								{
									ll_SrCd++;
									insertDataJson = new JSONObjectImpl();
									insertDataJson = IsNewRowJlist.getJSONObject(k);

									insertDataJson.put(REQ_CD, ls_reqCd);
									insertDataJson.put(BRANCH_CD, ls_branchCd);
									insertDataJson.put(COMP_CD, ls_compCd);
									insertDataJson.put(REQ_FLAG, ls_reqFlag);
									insertDataJson.put(SR_CD, ll_SrCd);
									insertDataJson.put(ACCT_TYPE, ls_acctType);
								}
							}
							ls_updResponse = UpdateDetailsData(connection, relative_dtlJson, userJson, jPrimaryDtl,REQ_ACCT_MST_RELATIVE_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}

						} else {
							return ofGetFailedMSg("common.updaterights.error", "",
									"You do not have access to record updates. Please contact the administrator", null);
						}
					}
				}
			}
			
			if (reqJson.has("ADVANCE_CONFIG_DTL")) 
			{
				int li_sr_cd = 0;
				advanceConfig_dtlJlist = reqJson.getJSONArray("ADVANCE_CONFIG_DTL");
				for (int i = 0; i < advanceConfig_dtlJlist.length(); i++) 
				{
					// advance config details
					advanceConfig_dtlJson = advanceConfig_dtlJlist.getJSONObject(i);
					ib_NewRow = advanceConfig_dtlJson.optBoolean("IsNewRow");

					if (ib_NewRow)
					{
						if (ACCESS_INSERT <= USERROLE) 
						{
							li_sr_cd++;
							advanceConfig_dtlJson.put(REQ_CD, ls_reqCd);
							advanceConfig_dtlJson.put(BRANCH_CD, ls_branchCd);
							advanceConfig_dtlJson.put(COMP_CD, ls_compCd);
							advanceConfig_dtlJson.put(REQ_FLAG, ls_reqFlag);
							advanceConfig_dtlJson.put(SR_CD, li_sr_cd);
							advanceConfig_dtlJson.put(ACCT_TYPE, ls_acctType);

							ls_updResponse = InsertData(connection, advanceConfig_dtlJson, userJson, 2, "",REQ_ACCT_PARA_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}

						} else {
							return ofGetFailedMSg("common.insertrights.error", "",
									"You do not have access to insert a new row. Please contact the administrator",null);
						}
					} else {
						
						if (ACCESS_UPDATE <= USERROLE)
						{
							// GET MAXIMUM SR CDs			
							jPrimaryDtl.put(REQ_CD, ls_reqCd);
							jallColumnList = getColumnDefination("REQ_ACCT_PARA_DTL", connection, true);
							ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_PARA_DTL", SR_CD, jallColumnList,jPrimaryDtl);

							IsNewRowJlist = new JSONArrayImpl();
							IsNewRowJlist = advanceConfig_dtlJson.getJSONArray("isNewRow");

							if (IsNewRowJlist.length() != 0) 
							{
								for (int k = 0; k < IsNewRowJlist.length(); k++) 
								{
									ll_SrCd++;
									insertDataJson = new JSONObjectImpl();
									insertDataJson = IsNewRowJlist.getJSONObject(k);

									insertDataJson.put(REQ_CD, ls_reqCd);
									insertDataJson.put(BRANCH_CD, ls_branchCd);
									insertDataJson.put(COMP_CD, ls_compCd);
									insertDataJson.put(REQ_FLAG, ls_reqFlag);
									insertDataJson.put(SR_CD, ll_SrCd);
									insertDataJson.put(ACCT_TYPE, ls_acctType);
								}
							}
							ls_updResponse = UpdateDetailsData(connection, advanceConfig_dtlJson, userJson, jPrimaryDtl,REQ_ACCT_PARA_DTL);

							JSONObjectImpl db_resJson = common.ofGetJsonObject(ls_updResponse);
							String ls_db_resstatus = db_resJson.getString(STATUS);

							if (!isSuccessStCode(ls_db_resstatus)) {
								return ls_updResponse;
							}

						} else {
							return ofGetFailedMSg("common.updaterights.error", "",
									"You do not have access to record updates. Please contact the administrator", null);
						}
					}
				}
			}
					
			return ls_updResponse;
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofSaveAsDraft", "(ENP292)");
		}
	}
	
	public String collateralOtherSecurityButton(Connection connection,JSONObjectImpl joint_acc_dtlJson,JSONObjectImpl userJson,JSONObjectImpl jPrimaryDtl,JSONObjectImpl reqJson) throws Exception 
	{

		String ls_detailResponse = StringUtils.EMPTY;
		String ls_security_type = StringUtils.EMPTY;
		String table_Name = StringUtils.EMPTY;
		String ls_reqFlag = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCode = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_Req_cd=StringUtils.EMPTY;
		
		JSONObjectImpl insertDetailJson = new JSONObjectImpl();
		JSONObjectImpl updateDataJson = new JSONObjectImpl();
		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl updateJlist = new JSONArrayImpl();
		JSONArrayImpl insJsonObjlist = new JSONArrayImpl();
		JSONArrayImpl deleteDtlJlist = new JSONArrayImpl();
		JSONArrayImpl otherSecInsJson = new JSONArrayImpl();
		JSONArrayImpl otherSecUpdJson = new JSONArrayImpl();
		JSONArrayImpl jallColumnList = new JSONArrayImpl();
		JSONObjectImpl other_security_dtlJson;
		JSONObjectImpl Upd_other_security_dtlJson;
		long ls_Line_cd;

		try {	
			
			ls_reqFlag = reqJson.getString(REQ_FLAG);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCode = reqJson.getString(ACCT_CD);
			//ls_customerId = reqJson.getString(CUSTOMER_ID);
			ls_compCd = getRequestUniqueData.getCompCode();
			ls_branchCd = getRequestUniqueData.getBranchCode();
			ls_Req_cd = reqJson.getString(REQ_CD);
			
				int ins_length = 0;
				int upd_length = 0;
				int del_length = 0;
				
					insertJlist = joint_acc_dtlJson.getJSONArray("isNewRow");
					deleteJlist = joint_acc_dtlJson.getJSONArray("isDeleteRow");
					updateJlist = joint_acc_dtlJson.getJSONArray("isUpdatedRow");
					insJsonObjlist = new JSONArrayImpl();

					ins_length = insertJlist.length();
					upd_length = updateJlist.length();
					del_length = deleteJlist.length();

					
					//insert collateral details
					if (ins_length != 0)
					{
						for (int i = 0; i < ins_length; i++) 
						{
							insertDetailJson = insertJlist.getJSONObject(i);
							
							String j_type = insertDetailJson.getString(J_TYPE).trim();
							
							if(j_type.equals("M") && insertDetailJson.has("OTHER_SECURITY_TYPE"))
							{
								ls_security_type=insertDetailJson.getString("OTHER_SECURITY");

								if(ls_security_type.equals("STK") || ls_security_type.equals("BDC"))
									table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_STOCK";
								else if (ls_security_type.equals("SH"))
									table_Name="EASY_BANK.STOCK";
								else if (ls_security_type.equals("MCH") || ls_security_type.equals("VEH") 
										|| ls_security_type.equals("PRT"))
									table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_OTHER";
								else if (ls_security_type.equals("LIC"))
									table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_LIC";
								else if (ls_security_type.equals("OTH") || ls_security_type.equals("GOV")
										|| ls_security_type.equals("BRD") || ls_security_type.equals("BFD"))
									table_Name="ENFINITY.REQ_OTHER_COLLATERAL_DTL";

								otherSecInsJson = insertDetailJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isNewRow");
								
								if(otherSecInsJson.length() != 0)
								{
									ls_Line_cd=0;
									for (int j = 0; j < otherSecInsJson.length(); j++) 
									{
										other_security_dtlJson = otherSecInsJson.getJSONObject(j);

										ls_Line_cd++;
										other_security_dtlJson.put("ACTION", "ADD");
										other_security_dtlJson.put(REQ_FLAG, ls_reqFlag);
										other_security_dtlJson.put(ENT_COMP_CD, ls_compCd);
										other_security_dtlJson.put(ENT_BRANCH_CD, ls_branchCd);
										other_security_dtlJson.put(SR_CD,insertDetailJson.getString(SR_CD));
										other_security_dtlJson.put(J_TYPE, insertDetailJson.getString(J_TYPE));
										other_security_dtlJson.put("LINE_ID", ls_Line_cd);
										other_security_dtlJson.put(REQ_CD, ls_Req_cd);
										insJsonObjlist.put(other_security_dtlJson);
										//insertDtlJlist.put(insertDetailJson);
									}

									ls_detailResponse = insertDTLData(connection, insJsonObjlist, userJson, reqJson,table_Name);
									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);

									if (!isSuccessStCode(jobjinsRes.getString(STATUS)))
									{
										return ls_detailResponse;
									}
								}
							}
						}
					}
					//update collateral details
					
					if (upd_length != 0)
					{						
							for (int a = 0; a < upd_length; a++)
							{
								updateDataJson = updateJlist.getJSONObject(a);
								
								String j_type=updateDataJson.getString(J_TYPE).trim();
								
								if(j_type.equals("M") && updateDataJson.has("OTHER_SECURITY_TYPE"))
								{
									ls_security_type=updateDataJson.getString("OTHER_SECURITY");

									if(ls_security_type.equals("STK") || ls_security_type.equals("BDC"))
										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_STOCK";
									else if (ls_security_type.equals("SH"))
										table_Name="EASY_BANK.STOCK";
									else if (ls_security_type.equals("MCH") || ls_security_type.equals("VEH") 
											|| ls_security_type.equals("PRT"))
										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_OTHER";
									else if (ls_security_type.equals("LIC"))
										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_LIC";
									else if (ls_security_type.equals("OTH") || ls_security_type.equals("GOV")
											|| ls_security_type.equals("BRD") || ls_security_type.equals("BFD"))
										table_Name="ENFINITY.REQ_OTHER_COLLATERAL_DTL";

									otherSecInsJson = updateDataJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isNewRow");
									otherSecUpdJson = updateDataJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isUpdatedRow");

									if(otherSecInsJson.length() != 0)
									{
										other_security_dtlJson = new JSONObjectImpl();
										ls_Line_cd=0;
										for (int j = 0; j < otherSecInsJson.length(); j++) 
										{
											other_security_dtlJson = otherSecInsJson.getJSONObject(j);
											
											jPrimaryDtl.put(SR_CD,updateDataJson.getString(SR_CD));
											
											jallColumnList = getColumnDefination(table_Name, connection, true);
											ls_Line_cd = getMaxSrCd(connection, table_Name,"LINE_ID", jallColumnList,jPrimaryDtl);
		
											ls_Line_cd++;
											other_security_dtlJson.put("ACTION", "ADD");
											other_security_dtlJson.put(REQ_FLAG, ls_reqFlag);
											other_security_dtlJson.put(ENT_COMP_CD, ls_compCd);
											other_security_dtlJson.put(ENT_BRANCH_CD, ls_branchCd);
											other_security_dtlJson.put(SR_CD,updateDataJson.getString(SR_CD));
											other_security_dtlJson.put(J_TYPE, updateDataJson.getString(J_TYPE));
											other_security_dtlJson.put("LINE_ID", ls_Line_cd);
											other_security_dtlJson.put(REQ_CD, ls_Req_cd);
											insJsonObjlist.put(other_security_dtlJson);
										}

										ls_detailResponse = insertDTLData(connection, insJsonObjlist, userJson, reqJson,table_Name);
										JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);

										if (!isSuccessStCode(jobjinsRes.getString(STATUS)))
										{
											return ls_detailResponse;
										}
									}
									
									
									if(otherSecUpdJson.length() != 0)
									{
										//ls_Line_cd=0;
										for (int j = 0; j < otherSecUpdJson.length(); j++) 
										{
											Upd_other_security_dtlJson = new JSONObjectImpl();
											Upd_other_security_dtlJson = otherSecUpdJson.getJSONObject(j);

											Upd_other_security_dtlJson.put(REQ_CD, ls_Req_cd);

											ls_detailResponse = UpdateData(connection, Upd_other_security_dtlJson,userJson,table_Name);

											JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse); 
											if(!isSuccessStCode(jobjinsRes.getString(STATUS)))
											{ 
												return ls_detailResponse;
											}
										}
									}
								}
							}
					}
					
					/*
					 * if (del_length != 0) { deleteDtlJlist = new JSONArrayImpl(); deleteDtlJlist =
					 * deleteJlist;
					 * 
					 * //reqJson.put(REQ_CD, ls_req_cd); //jPrimaryDtl.put(REQ_CD, ls_req_cd);
					 * 
					 * JSONArrayImpl jallColumn = getColumnDefination("REQ_JOINT_ACCOUNT_MST",
					 * connection,true); int li_del_res = DeleteDTLData(connection, deleteJlist,
					 * userJson, jPrimaryDtl,"REQ_JOINT_ACCOUNT_MST", jallColumn);
					 * 
					 * return common.ofGetResponseJson(new JSONArrayImpl(), "", "Success", ST0, "G",
					 * "Success") .toString(); }
					 */
				//}

		} catch (Exception exception) {
		}
		return ofGetResponseJson(new JSONArrayImpl(), "", "", ST0, "G", "").toString();
	}
	
	
}
