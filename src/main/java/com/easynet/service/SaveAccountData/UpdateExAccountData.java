package com.easynet.service.SaveAccountData;

import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicInsertData;
import com.easynet.dao.SelectData;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;

@Service
public class UpdateExAccountData extends DynamicInsertData {

	private Logger LOGGER = LoggerFactory.getLogger(SaveAccountData.class);

	private static final String REQ_ACCT_MST_HDR = "REQ_ACCT_MST_HDR";
	private static final String REQ_ACCT_MST_UPD_DTL = "REQ_ACCT_MST_UPD_DTL";

	private static final String SELECT_OTHER_ADDRESS_MAX_SR_CD = "SELECT NVL(MAX(SR_CD),0) AS MAX_SR_CD FROM EASY_BANK.ACCT_MST_OTHER_DTL M WHERE M.COMP_CD = :COMP_CD AND \r\n"
			+ "M.BRANCH_CD = :BRANCH_CD AND \r\n" + "M.ACCT_TYPE = :ACCT_TYPE AND \r\n" + "M.ACCT_CD =  :ACCT_CD\r\n"
			+ "";
	private static final String SELECT_RELATIVE_DTL_MAX_SR_CD = "SELECT NVL(MAX(SR_CD),0) AS MAX_SR_CD FROM EASY_BANK.ACCT_MST_RELATIVE_DTL WHERE COMP_CD = :COMP_CD AND  \r\n"
			+ "M.BRANCH_CD = :BRANCH_CD AND \r\n" + "M.ACCT_TYPE = :ACCT_TYPE AND \r\n" + "M.ACCT_CD =  :ACCT_CD\r\n";

	private static final String SELECT_MOBILE_DTL_MAX_SR_CD = "SELECT NVL(MAX(SR_CD),0) AS MAX_SR_CD FROM EASY_BANK.ACCT_MOBILE_DTL M WHERE M.COMP_CD = :COMP_CD AND  \r\n"
			+ "M.BRANCH_CD = :BRANCH_CD AND \r\n" + "M.ACCT_TYPE = :ACCT_TYPE AND \r\n" + "M.ACCT_CD =  :ACCT_CD\r\n";

	private static final String SELECT_ADVANCE_CONFIG_DTL_MAX_SR_CD = "SELECT NVL(MAX(SR_CD),0) AS MAX_SR_CD FROM EASY_BANK.ACCT_PARA_DTL M WHERE M.COMP_CD = :COMP_CD AND  \r\n"
			+ "M.BRANCH_CD = :BRANCH_CD AND \r\n" + "M.ACCT_TYPE = :ACCT_TYPE AND \r\n" + "M.ACCT_CD =  :ACCT_CD\r\n";

	private static final String UPDATE_ENTRY_TYPE = "  UPDATE  REQ_ACCT_MST_HDR SET ENTRY_TYPE = :ENTRY_TYPE WHERE  REQ_CD = :REQ_CD  ";

	private static final String ADV_CONFIG_MAIN_TABLE = "ADV_CONFIG_MAIN_TABLE";

	private static final String SELECT_JOINT_ACCOUNT_MAX_SR_CD = "SELECT NVL(MAX(SR_CD),0) AS MAX_SR_CD FROM EASY_BANK.JOINT_ACCOUNT_MST M WHERE M.COMP_CD = :COMP_CD AND \r\n"
			+ "M.BRANCH_CD = :BRANCH_CD AND \r\n" + "M.ACCT_TYPE = :ACCT_TYPE AND \r\n" + "M.ACCT_CD =  :ACCT_CD AND \r\n" + "M.J_TYPE =  :J_TYPE\r\n "
			+ "";

	/*public static final List<String> COLUMNLIST = Arrays.asList("MAIN_DETAIL", "OTHER_ADDRESS_DTL",
			"RELATIVE_DTL", "MOBILE_REG_DTL", "ADVANCE_CONFIG_DTL", "DOC_MST", "JOINT_ACCOUNT_DTL");*/
	
	public static final List<String> ACCT_MST_03_ColumnList = Arrays.asList("EXPOSURE_TO_SENSITIVE_SECTOR", "SECURITY_DEPOSIT",
			"SECTORAL_ANALYSIS", "SECTOR", "RF_RAITING", "INVEST_IN_PLANT", "UDYAM_REG_DT","BUSINESS_CD", "INDUSTRY_CODE",
				"UDYAM_REG_NO", "ANNUAL_TURNOVER_SR_CD", "NPA_REASON", "RENRE_CD");

	@Autowired
	private SelectData selectData;

	@Autowired
	private UpdateData updateData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	String ls_response = StringUtils.EMPTY;
	String ls_resStatus = StringUtils.EMPTY;
	String ls_isNew = StringUtils.EMPTY;
	String ls_req_cd = StringUtils.EMPTY;
	String ls_value = StringUtils.EMPTY;
	String ls_status_in = StringUtils.EMPTY;
	String ls_comp_cd = StringUtils.EMPTY;
	String ls_req_flag = StringUtils.EMPTY;
	String ls_action = StringUtils.EMPTY;
	String ls_column_nm = StringUtils.EMPTY;
	String ls_column_type = StringUtils.EMPTY;
	String ls_audit_res = StringUtils.EMPTY;
	String ls_req_tablename = StringUtils.EMPTY;
	String ls_acct_cd = StringUtils.EMPTY;
	String ls_acct_type = StringUtils.EMPTY;
	String ls_customerId = StringUtils.EMPTY;
	String ls_updResponse = StringUtils.EMPTY;
	String ls_updColumnName = StringUtils.EMPTY;

	int li_length;
	int li_start_pos;
	boolean lb_docUpdFlag;

	JSONObjectImpl requestJson = null;
	JSONObjectImpl reqDataJson = null;
	JSONObjectImpl detailDataJson = new JSONObjectImpl();
	JSONObjectImpl detailJson = null;
	JSONObjectImpl dbResponseJson = null;
	JSONObjectImpl dbresJson = null;
	JSONObjectImpl responseJson = new JSONObjectImpl();

	JSONArray responseJlist;
	JSONObject resJson;

	LoggerImpl loggerImpl = null;

	public String ofGetUpdateAccountData(Connection connection, JSONObjectImpl reqJson, JSONObjectImpl userJson) {

		JSONObjectImpl mainDtlJson = new JSONObjectImpl();
		JSONObjectImpl resDataJson = new JSONObjectImpl();
		JSONObjectImpl otherAddJson = new JSONObjectImpl();
		JSONObjectImpl relativeDtlJson = new JSONObjectImpl();
		JSONObjectImpl mobileRegDtlJson = new JSONObjectImpl();
		JSONObjectImpl relativeJson = new JSONObjectImpl();
		JSONObjectImpl mobileRegJson = new JSONObjectImpl();
		JSONObjectImpl termLoanBtnJson = new JSONObjectImpl();
		JSONObjectImpl insertDetailJson = new JSONObjectImpl();
		JSONObjectImpl updateDataJson = new JSONObjectImpl();
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
		JSONObjectImpl otherAddDtlJson = new JSONObjectImpl();
		JSONObjectImpl oldValueJson = new JSONObjectImpl();
		JSONObjectImpl updDetailJson = new JSONObjectImpl();
		JSONObjectImpl oldMainDtlJson = new JSONObjectImpl();
		JSONObjectImpl oldMainDataDtlJson = new JSONObjectImpl();
		JSONObjectImpl dbJson = new JSONObjectImpl();
		JSONObjectImpl advanceConfig = new JSONObjectImpl();

		JSONArrayImpl dbResponseJList = null;
		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl mobileRegJlist = new JSONArrayImpl();
		JSONArrayImpl updateJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl insDtlJlist = new JSONArrayImpl();
		JSONArrayImpl updateDtlJlist = new JSONArrayImpl();
		JSONArrayImpl deleteDtlJlist = new JSONArrayImpl();
		JSONArrayImpl insertDtlJlist = new JSONArrayImpl();
		JSONArrayImpl jallColumnList = new JSONArrayImpl();
		JSONArrayImpl insAudJlist = new JSONArrayImpl();
		JSONArrayImpl updAudJlist = new JSONArrayImpl();
		JSONArrayImpl delAudJlist = new JSONArrayImpl();
		JSONArrayImpl relativeJlist = new JSONArrayImpl();
		JSONArrayImpl updValueJlist = new JSONArrayImpl();
		JSONArrayImpl jointDtlJlist = new JSONArrayImpl();
		JSONArrayImpl documentJlist = new JSONArrayImpl();
		JSONArrayImpl updColumnJlist = new JSONArrayImpl();
		JSONArrayImpl oldDocDataJlist = new JSONArrayImpl();
		JSONArrayImpl oldJointDtlJlist = new JSONArrayImpl();
		JSONArrayImpl advanceConfigJlist = new JSONArrayImpl();

		String ls_insResponse = StringUtils.EMPTY;
		String ls_reqFlag = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCode = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_acct_branchCd = StringUtils.EMPTY;
		String ls_acct_compCd = StringUtils.EMPTY;
		String ls_detailResponse = StringUtils.EMPTY;
		String ls_updColumn = StringUtils.EMPTY;
		String ls_newValue = StringUtils.EMPTY;
		String ls_isFromOtherAdd = StringUtils.EMPTY;
		String ls_isFromRelativeDtl = StringUtils.EMPTY;
		String ls_isFromMobileRegDtl = StringUtils.EMPTY;
		String ls_isFromMainTermLoan = StringUtils.EMPTY;
		String ls_validationRes = StringUtils.EMPTY;
		String ls_screenRef = StringUtils.EMPTY;
		String ls_dbResMsg = StringUtils.EMPTY;
		String ls_dbResStatus = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		String ls_isFromJointAcctDtl=StringUtils.EMPTY;
		String ls_IsRequest_cd=StringUtils.EMPTY;

		boolean lb_isNewRowMain;
		boolean lb_isNewRow;
		boolean lb_isFirst = true;

		int li_maxSrCd;
		long ll_SrCd, ll_ENTRY_TYPE;
		//Set<String> selectKeys;

		try {
			loggerImpl = new LoggerImpl();
			// insert data in hdr table.
			lb_isNewRowMain = reqJson.getBoolean("IsNewRow");
			ls_reqFlag = reqJson.getString(REQ_FLAG);
			ls_acctType = reqJson.getString(ACCT_TYPE);
			ls_acctCode = reqJson.getString(ACCT_CD);
			ls_acct_compCd = reqJson.getString(COMP_CD);
			ls_acct_branchCd = reqJson.getString(BRANCH_CD);	
			ls_customerId = reqJson.getString(CUSTOMER_ID);
			ls_compCd = getRequestUniqueData.getCompCode();
			ls_branchCd = getRequestUniqueData.getBranchCode();
			ls_IsRequest_cd = reqJson.getString(REQ_CD);

			// check blank data

			ls_emptyResponseData = doCheckBlankData(ls_reqFlag, ls_acctType, ls_acctCode, ls_customerId, ls_acct_compCd,ls_acct_branchCd);

			if (StringUtils.isNotBlank(ls_emptyResponseData))
				return ls_emptyResponseData;


			if (ls_IsRequest_cd.equals(""))
			{
				ls_response = ofGetInsertAccountHdrData(connection, reqJson, userJson, "M");

				responseJson = common.ofGetJsonObject(ls_response);
				ls_resStatus = responseJson.getString(STATUS);

				if (!isSuccessStCode(ls_resStatus)) 
				{
					return ls_response;
				}
				
				dbResponseJList = responseJson.getJSONArray("RESPONSE");
				ls_req_cd = dbResponseJList.getJSONObject(0).getString(REQ_CD);
				
				resDataJson.put(REQ_CD, ls_req_cd);
				reqJson.put(REQ_CD, ls_req_cd);
				ll_ENTRY_TYPE = 1;
			}else{
				
				ls_req_cd = reqJson.getString(REQ_CD);
				
				ls_emptyResponseData = doCheckBlankData(ls_req_cd);

				if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;
				
				jPrimaryDtl.put(REQ_CD, ls_req_cd);
				jallColumnList = getColumnDefination("REQ_ACCT_MST_UPD_DTL", connection, true);
				ll_ENTRY_TYPE = getMaxSrCd(connection, "REQ_ACCT_MST_UPD_DTL", ENTRY_TYPE, jallColumnList, jPrimaryDtl);
				ll_ENTRY_TYPE = ll_ENTRY_TYPE + 1;

				// update entry_type in request table REQ_ACCT_MST_HDR.

				ls_updResponse = updateData.doUpdateData(connection, UPDATE_ENTRY_TYPE, ll_ENTRY_TYPE, ls_req_cd);
				responseJson = common.ofGetJsonObject(ls_updResponse);
				ls_resStatus = responseJson.getString(STATUS);

				if (!isSuccessStCode(ls_resStatus)) 
				{
					return ls_response;
				}
				resDataJson.put(REQ_CD, ls_req_cd);
			}
		
				if (reqJson.has("MAIN_DETAIL")) 
				{
					mainDtlJson = reqJson.getJSONObject("MAIN_DETAIL");
					
					ls_comp_cd = getRequestUniqueData.getCompCode();
					mainDtlJson.put(REQ_CD, ls_req_cd);
					
					// insert data in audit table.
					ls_insResponse = ofGetInsertAuditData(connection, reqJson, mainDtlJson, userJson,"EASY_BANK.ACCT_MST", "REQ_ACCT_MST", "UPD", ll_ENTRY_TYPE);
					
					responseJson = common.ofGetJsonObject(ls_insResponse);
					ls_resStatus = responseJson.getString(STATUS);

					if (!isSuccessStCode(ls_resStatus)) {
						return ls_insResponse;
					}

				} 
					
				if (reqJson.has("TERMLOAN_BTN_DTL")) 
				{
					int ins_length = 0;
					int upd_length = 0;
					int del_length = 0;

					JSONArrayImpl termLoan_BtnDtl = reqJson.getJSONArray("TERMLOAN_BTN_DTL");

					for (int a = 0; a < termLoan_BtnDtl.length(); a++)
					{
						termLoanBtnJson = termLoan_BtnDtl.getJSONObject(a);

						ls_isFromMainTermLoan = termLoanBtnJson.getString("IS_FROM_MAIN_TERMLOAN");

						insertJlist = termLoanBtnJson.getJSONArray("isNewRow");
						deleteJlist = termLoanBtnJson.getJSONArray("isDeleteRow");
						updateJlist = termLoanBtnJson.getJSONArray("isUpdatedRow");
						insAudJlist = new JSONArrayImpl();

						ins_length = insertJlist.length();
						upd_length = updateJlist.length();
						del_length = deleteJlist.length();

						if (ins_length != 0) 
						{
							//if ("Y".equalsIgnoreCase(ls_isFromMainTermLoan)) 
							//{
							for (int i = 0; i < ins_length; i++)
							{
								insertDetailJson = insertJlist.getJSONObject(i);

								insertDetailJson.put(REQ_CD, ls_req_cd);
								insertDetailJson.put(SR_CD,1);
								insertDetailJson.put("ACTION", "ADD");
								insertDetailJson.put(REQ_FLAG, ls_reqFlag);
								insertDetailJson.put(COMP_CD, ls_acct_compCd);
								insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
								insertDetailJson.put(ACCT_TYPE, ls_acctType);
								insertDetailJson.put(ACCT_CD, ls_acctCode);
								insertDetailJson.put(ENT_COMP_CD, ls_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
								insertDetailJson.put(LINE_ID,1);
								insertDetailJson.put("LETTER_DP_FLAG","L");		
								insertDetailJson.put("CERSAI_REGI","N");
								insertDetailJson.put(ACTIVE,"Y");
								insertDetailJson.put("DOC_VALUE",0);
								insAudJlist.put(insertDetailJson);
								insertDtlJlist.put(insertDetailJson);
							}
							reqJson.put(REQ_CD, ls_req_cd);

							// insert data in the REQ_ACCT_MOBILE_DTL table.
							ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,"REQ_JOINT_ACCOUNT_MST_OTHER");

							JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);

							if (!isSuccessStCode(jobjinsRes.getString(STATUS))) 
							{
								return ls_detailResponse;
							}
						}
						
						if (upd_length != 0) 
						{
							if ("Y".equalsIgnoreCase(ls_isFromMainTermLoan))
							{
								updAudJlist = new JSONArrayImpl();
								for (int i = 0; i < upd_length; i++)
								{
									updateDataJson = new JSONObjectImpl();

									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updDetailJson = new JSONObjectImpl();
		
									for (String key : updateDataJson.keySet()) 
									{
										if (!key.equals("_UPDATEDCOLUMNS") && !key.equals("_OLDROWVALUE")) {
									        updDetailJson.put(key, updateDataJson.get(key));
									    }
									}
									
									updDetailJson.put(REQ_CD, ls_req_cd);
									updDetailJson.put("LETTER_DP_FLAG","L");		
									updDetailJson.put("CERSAI_REGI","N");
									updDetailJson.put("DOC_VALUE",0);
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, ls_reqFlag);
									updDetailJson.put(COMP_CD, ls_acct_compCd);
									updDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									updDetailJson.put(ACCT_TYPE, ls_acctType);
									updDetailJson.put(ACCT_CD, ls_acctCode);
									updDetailJson.put(ENT_COMP_CD, ls_compCd);
									updDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									updAudJlist.put(updDetailJson);
								}

								ls_detailResponse = insertDTLData(connection, updAudJlist, userJson, reqJson,"REQ_JOINT_ACCOUNT_MST_OTHER");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);

								if(!isSuccessStCode(jobjinsRes.getString(STATUS))) 
								{ 
									return ls_detailResponse;
								}

							} else {

								for (int i = 0; i < upd_length; i++) 
								{
									updateDataJson = new JSONObjectImpl();
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updateDataJson.put(COMP_CD, ls_acct_compCd);
									updateDataJson.put(BRANCH_CD, ls_acct_branchCd);
									updateDataJson.put(ACCT_TYPE, ls_acctType);
									updateDataJson.put(ACCT_CD, ls_acctCode);
									updateDataJson.put("REQ_CD", ls_req_cd);

									ls_detailResponse = UpdateData(connection, updateDataJson, userJson,"REQ_JOINT_ACCOUNT_MST_OTHER");
									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
									if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
										return ls_detailResponse;
									}
									// updateJlist.put(updateDataJson);
								}
							}
						}
					}
					
					detailDataJson = new JSONObjectImpl();
					// insert data in audit table REQ_ACCT_MST_UPD_DTL for ACTION---- ADD , UPD,DEL
					
					detailDataJson.put("isNewRow", insertDtlJlist);
					detailDataJson.put("isDeleteRow", deleteDtlJlist);
					detailDataJson.put("isUpdatedRow", updateDtlJlist);

					if(ins_length != 0 || upd_length != 0 || del_length != 0 ) 
					{
						ls_insResponse = ofGetInsertAcctDocAuditData(connection, detailDataJson, reqJson, userJson,"EASY_BANK.JOINT_ACCOUNT_MST_OTHER", "REQ_JOINT_ACCOUNT_MST_OTHER");

						responseJson = common.ofGetJsonObject(ls_insResponse);
						ls_resStatus = responseJson.getString(STATUS);

						if (!isSuccessStCode(ls_resStatus)) {
							return ls_insResponse;
						}
					}

				}
			
					
				if (reqJson.has("OTHER_ADDRESS_DTL")) 
				{
					int ins_length = 0;
					int upd_length = 0;
					int del_length = 0;
					JSONArrayImpl otherAddJlist = reqJson.getJSONArray("OTHER_ADDRESS_DTL");

					for (int i = 0; i < otherAddJlist.length(); i++) 
					{
						otherAddJson = otherAddJlist.getJSONObject(i);
						ls_isFromOtherAdd = otherAddJson.getString(IS_FROM_OTH_ADD);

						insertJlist = otherAddJson.getJSONArray("isNewRow");
						deleteJlist = otherAddJson.getJSONArray("isDeleteRow");
						updateJlist = otherAddJson.getJSONArray("isUpdatedRow");
						insAudJlist = new JSONArrayImpl();

						ins_length = insertJlist.length();
						upd_length = updateJlist.length();
						del_length = deleteJlist.length();

						if (ins_length != 0)
						{

							if ("Y".equalsIgnoreCase(ls_isFromOtherAdd))
							{
								// GET MAX SR_CD
								ls_response = selectData.getSelectData(SELECT_OTHER_ADDRESS_MAX_SR_CD, ls_compCd,
										ls_branchCd, ls_acctType, ls_acctCode);
								responseJson = common.ofGetJsonObject(ls_response);
								ls_resStatus = responseJson.getString(STATUS);

								if (isSuccessStCode(ls_resStatus))
								{
									li_maxSrCd = responseJson.getJSONArray("RESPONSE").getJSONObject(0).getInt("MAX_SR_CD");
									ll_SrCd = li_maxSrCd + 1;
								}else{
									return ls_response;
								}

								for (int j = 0; j < ins_length; j++) {

									insertDetailJson = insertJlist.getJSONObject(j);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(ACTIVE, "Y");
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
									ll_SrCd++;
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_CUST_OTHER_ADD_DTL table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
										"REQ_ACCT_MST_OTHER_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) 
								{
									return ls_detailResponse;
								}

							} else {
								// GET MAX_SR_CD FROM REQ_ACCT_CUST_OTHER_ADD_DTL BY REQ_CD.

								jPrimaryDtl.put(REQ_CD, ls_req_cd);

								jallColumnList = getColumnDefination("REQ_ACCT_MST_OTHER_DTL", connection, true);
								ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_OTHER_DTL", SR_CD, jallColumnList,
										jPrimaryDtl);
								insAudJlist = new JSONArrayImpl();
								// insert data in table REQ_ACCT_CUST_OTHER_ADD_DTL

								for (int k = 0; k < ins_length; k++) {

									ll_SrCd++;
									insertDetailJson = insertJlist.getJSONObject(k);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(ACTIVE, "Y");
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_MST_DOC_TEMPLATE table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
										                  "REQ_ACCT_MST_OTHER_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
									return ls_detailResponse;
								}
							}
						}
						if (upd_length != 0) 
						{
							
							if ("Y".equalsIgnoreCase(ls_isFromOtherAdd)) 
							{
								updAudJlist = new JSONArrayImpl();
								for (int a = 0; a < upd_length; a++) {

									updateDataJson = new JSONObjectImpl();
									
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(a);
									updDetailJson = new JSONObjectImpl();

									for (String key : updateDataJson.keySet()) 
									{
										if (!key.equals("_UPDATEDCOLUMNS") && !key.equals("_OLDROWVALUE")) {
									        updDetailJson.put(key, updateDataJson.get(key));
									    }
									}
									
									updDetailJson.put(REQ_CD, ls_req_cd);
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, ls_reqFlag);
									updDetailJson.put(COMP_CD, ls_acct_compCd);
									updDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									updDetailJson.put(ACCT_TYPE, ls_acctType);
									updDetailJson.put(ACCT_CD, ls_acctCode);
									updDetailJson.put(ENT_COMP_CD, ls_compCd);
									updDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									updAudJlist.put(updDetailJson);
								}
								reqJson.put(REQ_CD, ls_req_cd);

								
								  ls_detailResponse = insertDTLData(connection, updAudJlist, userJson, reqJson,
								  "REQ_ACCT_MST_OTHER_DTL");
								 
								  JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								
								  if(!isSuccessStCode(jobjinsRes.getString(STATUS)))
								  {
									  return ls_detailResponse;
								 }
								 
							}else{

								for (int a = 0; a < upd_length; a++) {

									updateDataJson = new JSONObjectImpl();
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(a);
									updateDataJson.put(COMP_CD, ls_acct_compCd);
									updateDataJson.put(BRANCH_CD, ls_acct_branchCd);
									updateDataJson.put(ACCT_TYPE, ls_acctType);
									updateDataJson.put(ACCT_CD, ls_acctCode);
									updateDataJson.put("REQ_CD", ls_req_cd);
								
									ls_detailResponse = UpdateData(connection, updateDataJson, userJson,"REQ_ACCT_MST_OTHER_DTL");
									
									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
									if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
										return ls_detailResponse;
									}
									// updateJlist.put(updateDataJson);
								}
							}
						}
						if (del_length != 0) 
						{
							if (!"Y".equalsIgnoreCase(ls_isFromOtherAdd)) {

								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
								
								reqJson.put(REQ_CD, ls_req_cd);	
								
								jPrimaryDtl.put(REQ_CD, ls_req_cd);
								JSONArrayImpl jallColumn = getColumnDefination("REQ_ACCT_MST_OTHER_DTL", connection,
										true);
								int li_del_res = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,
										"REQ_ACCT_MST_OTHER_DTL", jallColumn);

								if (li_del_res == 0) {
									connection.rollback();
									return common.ofGetResponseJson(new JSONArrayImpl(), "",
											"Delete Failed! Invalid Request (" + li_del_res + ").", ST999, "R",
											"Delete Failed! Invalid Request (" + li_del_res + ").").toString();
								}
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Success", ST0, "G", "Success")
										.toString();
							} else 
							{
								reqJson.put(REQ_CD, ls_req_cd);	
								
								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
							}
						}
					}
					
					detailDataJson = new JSONObjectImpl();
					// insert data in audit table REQ_ACCT_MST_UPD_DTL for ACTION---- ADD , UPD,
					// DEL.

					detailDataJson.put("isNewRow", insertDtlJlist);
					detailDataJson.put("isDeleteRow", deleteDtlJlist);
					detailDataJson.put("isUpdatedRow", updateDtlJlist);

					if(ins_length != 0 || upd_length != 0 || del_length != 0 ) 
					{
						ls_insResponse = ofGetInsertAcctDocAuditData(connection, detailDataJson, reqJson, userJson,
								        "EASY_BANK.ACCT_MST_OTHER_DTL", "REQ_ACCT_MST_OTHER_DTL");

						responseJson = common.ofGetJsonObject(ls_insResponse);
						ls_resStatus = responseJson.getString(STATUS);

						if (!isSuccessStCode(ls_resStatus)) {
							return ls_insResponse;
						}
					}

				} //else if (COLUMNLIST.contains(key) && "RELATIVE_DTL".equals(key)) {
				
				if (reqJson.has("RELATIVE_DTL"))
				{
					int ins_length = 0;
					int upd_length = 0;
					int del_length = 0;
					JSONArrayImpl relativeDtlJlist = reqJson.getJSONArray("RELATIVE_DTL");

					for (int k = 0; k < relativeDtlJlist.length(); k++)
					{
						relativeJson = relativeDtlJlist.getJSONObject(k);
						ls_isFromRelativeDtl = relativeJson.getString(IS_FROM_RELATIVE_DTL);
						insertJlist = relativeJson.getJSONArray("isNewRow");
						deleteJlist = relativeJson.getJSONArray("isDeleteRow");
						updateJlist = relativeJson.getJSONArray("isUpdatedRow");
						insAudJlist = new JSONArrayImpl();

						ins_length = insertJlist.length();
						upd_length = updateJlist.length();
						del_length = deleteJlist.length();

						if (ins_length != 0) {

							if ("Y".equalsIgnoreCase(ls_isFromRelativeDtl))
							{
								
								// GET MAX SR_CD
								ls_response = selectData.getSelectData(SELECT_RELATIVE_DTL_MAX_SR_CD, ls_compCd,
										     ls_branchCd, ls_acctType, ls_acctCode);
								
								responseJson = common.ofGetJsonObject(ls_response);
								ls_resStatus = responseJson.getString(STATUS);

								if (isSuccessStCode(ls_resStatus)) {
									li_maxSrCd = responseJson.getJSONArray("RESPONSE").getJSONObject(0)
											.getInt("MAX_SR_CD");
									ll_SrCd = li_maxSrCd + 1;	
								} else {
									return ls_response;
								}

								for (int i = 0; i < ins_length; i++) {

									insertDetailJson = insertJlist.getJSONObject(i);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
									ll_SrCd++;
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_CUST_OTHER_ADD_DTL table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
										"REQ_ACCT_MST_RELATIVE_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
									return ls_detailResponse;
								}

							}else{
								// GET MAX_SR_CD FROM REQ_ACCT_MST_RELATIVE_DTL BY REQ_CD.

								jPrimaryDtl.put(REQ_CD, ls_req_cd);

								jallColumnList = getColumnDefination("REQ_ACCT_MST_RELATIVE_DTL", connection, true);
								ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_RELATIVE_DTL", SR_CD, jallColumnList,
										jPrimaryDtl);
								insAudJlist = new JSONArrayImpl();
								// insert data in table REQ_ACCT_MST_RELATIVE_DTL

								for (int i = 0; i < ins_length; i++) {

									ll_SrCd++;
									insertDetailJson = insertJlist.getJSONObject(i);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_MST_RELATIVE_DTL table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
										"REQ_ACCT_MST_RELATIVE_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
									return ls_detailResponse;
								}
							}
						}
						if (upd_length != 0)
						{

							if ("Y".equalsIgnoreCase(ls_isFromRelativeDtl)) 
							{
								updAudJlist = new JSONArrayImpl();
								for (int i = 0; i < upd_length; i++) {

									updateDataJson = new JSONObjectImpl();
									
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updDetailJson = new JSONObjectImpl();

									for (String key : updateDataJson.keySet()) 
									{
										if (!key.equals("_UPDATEDCOLUMNS") && !key.equals("_OLDROWVALUE")) {
									        updDetailJson.put(key, updateDataJson.get(key));
									    }
									}
									updDetailJson.put(REQ_CD, ls_req_cd);
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, ls_reqFlag);
									updDetailJson.put(COMP_CD, ls_acct_compCd);
									updDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									updDetailJson.put(ACCT_TYPE, ls_acctType);
									updDetailJson.put(ACCT_CD, ls_acctCode);
									updDetailJson.put(ENT_COMP_CD, ls_compCd);
									updDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									updAudJlist.put(updDetailJson);
								}
							
								  ls_detailResponse = insertDTLData(connection, updAudJlist, userJson, reqJson,
								  "REQ_ACCT_MST_RELATIVE_DTL");
								  
								  JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								  if(!isSuccessStCode(jobjinsRes.getString(STATUS))) 
								  {
									  return ls_detailResponse;
								  }
								 
							}else{

								for (int i = 0; i < upd_length; i++) {

									updateDataJson = new JSONObjectImpl();
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updateDataJson.put(COMP_CD, ls_acct_compCd);
									updateDataJson.put(BRANCH_CD, ls_acct_branchCd);
									updateDataJson.put(ACCT_TYPE, ls_acctType);
									updateDataJson.put(ACCT_CD, ls_acctCode);
									updateDataJson.put("REQ_CD", ls_req_cd);
									ls_detailResponse = UpdateData(connection, updateDataJson, userJson,
											"REQ_ACCT_MST_RELATIVE_DTL");
									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
									if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
										return ls_detailResponse;
									}
									// updateJlist.put(updateDataJson);
								}
							}
						}
						if (del_length != 0) 
						{
							if (!"Y".equalsIgnoreCase(ls_isFromRelativeDtl)) 
							{

								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
								reqJson.put(REQ_CD, ls_req_cd);	
								
								jPrimaryDtl.put(REQ_CD, ls_req_cd);
								JSONArrayImpl jallColumn = getColumnDefination("REQ_ACCT_MST_RELATIVE_DTL", connection,true);
								
								int li_del_res = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,
										         "REQ_ACCT_MST_RELATIVE_DTL", jallColumn);

								if (li_del_res == 0)  {
									connection.rollback();
									return common.ofGetResponseJson(new JSONArrayImpl(), "",
											"Delete Failed! Invalid Request (" + li_del_res + ").", ST999, "R",
											"Delete Failed! Invalid Request (" + li_del_res + ").").toString();
								}
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Success", ST0, "G", "Success")
										.toString();
							} else
							{
								reqJson.put(REQ_CD, ls_req_cd);	
								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
							}
						}
					}
					// insert data in audit table REQ_ACCT_MST_UPD_DTL for ACTION---- ADD , UPD,
					// DEL.
					detailDataJson = new JSONObjectImpl();
					detailDataJson.put("isNewRow", insertDtlJlist);
					detailDataJson.put("isDeleteRow", deleteDtlJlist);
					detailDataJson.put("isUpdatedRow", updateDtlJlist);

					if(ins_length != 0 || upd_length != 0 || del_length != 0 ) 
					{
						ls_insResponse = ofGetInsertAcctDocAuditData(connection, detailDataJson, reqJson, userJson,
								"EASY_BANK.ACCT_MST_RELATIVE_DTL", "REQ_ACCT_MST_RELATIVE_DTL");

						responseJson = common.ofGetJsonObject(ls_insResponse);
						ls_resStatus = responseJson.getString(STATUS);

						if (!isSuccessStCode(ls_resStatus)) {
							return ls_insResponse;
						}
					}
				} //else if (COLUMNLIST.contains(key) && "MOBILE_REG_DTL".equals(key)) {
				
				if (reqJson.has("MOBILE_REG_DTL")) 
				{
					int ins_length = 0;
					int upd_length = 0;
					int del_length = 0;

					JSONArrayImpl mobileRegDtlJlist = reqJson.getJSONArray("MOBILE_REG_DTL");

					for (int a = 0; a < mobileRegDtlJlist.length(); a++) {
						mobileRegJson = mobileRegDtlJlist.getJSONObject(a);
						ls_isFromMobileRegDtl = mobileRegJson.getString(IS_FROM_MOBILE_REG_DTL);

						insertJlist = mobileRegJson.getJSONArray("isNewRow");
						deleteJlist = mobileRegJson.getJSONArray("isDeleteRow");
						updateJlist = mobileRegJson.getJSONArray("isUpdatedRow");
						insAudJlist = new JSONArrayImpl();

						ins_length = insertJlist.length();
						upd_length = updateJlist.length();
						del_length = deleteJlist.length();

						if (ins_length != 0) {

							if ("Y".equalsIgnoreCase(ls_isFromMobileRegDtl)) {
								// GET MAX SR_CD
								ls_response = selectData.getSelectData(SELECT_MOBILE_DTL_MAX_SR_CD, ls_compCd,
										ls_branchCd, ls_acctType, ls_acctCode);
								responseJson = common.ofGetJsonObject(ls_response);
								ls_resStatus = responseJson.getString(STATUS);

								if (isSuccessStCode(ls_resStatus)) {
									li_maxSrCd = responseJson.getJSONArray("RESPONSE").getJSONObject(0)
											.getInt("MAX_SR_CD");
									ll_SrCd = li_maxSrCd + 1;
								} else {
									return ls_response;
								}

								for (int i = 0; i < ins_length; i++) {

									insertDetailJson = insertJlist.getJSONObject(i);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									//insertDetailJson.put("REG_TYPE","M");
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
									ll_SrCd++;
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_MOBILE_DTL table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
									                      	"REQ_ACCT_MOBILE_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
							
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) 
								{
									return ls_detailResponse;
								}

							} else {
								// GET MAX_SR_CD FROM REQ_ACCT_MOBILE_DTL BY REQ_CD.

								jPrimaryDtl.put(REQ_CD, ls_req_cd);

								jallColumnList = getColumnDefination("REQ_ACCT_MOBILE_DTL", connection, true);
								ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MOBILE_DTL", SR_CD, jallColumnList,
										jPrimaryDtl);
								insAudJlist = new JSONArrayImpl();
								// insert data in table REQ_ACCT_MST_RELATIVE_DTL

								for (int i = 0; i < ins_length; i++) {

									ll_SrCd++;
									insertDetailJson = insertJlist.getJSONObject(i);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									//insertDetailJson.put("REG_TYPE","M");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_MOBILE_DTL table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
										"REQ_ACCT_MOBILE_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
									return ls_detailResponse;
								}
							}
						}
						if (upd_length != 0) {

							if ("Y".equalsIgnoreCase(ls_isFromMobileRegDtl))
							{
								updAudJlist = new JSONArrayImpl();
								for (int i = 0; i < upd_length; i++) {

									updateDataJson = new JSONObjectImpl();
									
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updDetailJson = new JSONObjectImpl();

			
									for (String key : updateDataJson.keySet()) 
									{
										if (!key.equals("_UPDATEDCOLUMNS") && !key.equals("_OLDROWVALUE")) {
									        updDetailJson.put(key, updateDataJson.get(key));
									    }
									}
															
									updDetailJson.put(REQ_CD, ls_req_cd);
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, ls_reqFlag);
									updDetailJson.put(COMP_CD, ls_acct_compCd);
									updDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									updDetailJson.put(ACCT_TYPE, ls_acctType);
									updDetailJson.put(ACCT_CD, ls_acctCode);
									updDetailJson.put(ENT_COMP_CD, ls_compCd);
									updDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									updAudJlist.put(updDetailJson);
								}
			
								  ls_detailResponse = insertDTLData(connection, updAudJlist, userJson, reqJson,
								  "REQ_ACCT_MOBILE_DTL");
								  
								 JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse); if
								  (!isSuccessStCode(jobjinsRes.getString(STATUS))) { return ls_detailResponse;
								 }
								 
							} else {

								for (int i = 0; i < upd_length; i++) {

									updateDataJson = new JSONObjectImpl();
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updateDataJson.put(COMP_CD, ls_acct_compCd);
									updateDataJson.put(BRANCH_CD, ls_acct_branchCd);
									updateDataJson.put(ACCT_TYPE, ls_acctType);
									updateDataJson.put(ACCT_CD, ls_acctCode);
									updateDataJson.put("REQ_CD", ls_req_cd);
									ls_detailResponse = UpdateData(connection, updateDataJson, userJson,
											"REQ_ACCT_MOBILE_DTL");
									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
									if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
										return ls_detailResponse;
									}
									// updateJlist.put(updateDataJson);
								}
							}
						}
						if (del_length != 0) {
							
							if (!"Y".equalsIgnoreCase(ls_isFromMobileRegDtl)) {

								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
								reqJson.put(REQ_CD, ls_req_cd);	
								
								jPrimaryDtl.put(REQ_CD, ls_req_cd);
								JSONArrayImpl jallColumn = getColumnDefination("REQ_ACCT_MOBILE_DTL", connection, true);
								int li_del_res = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,
										"REQ_ACCT_MOBILE_DTL", jallColumn);

								if (li_del_res == 0) {
									connection.rollback();
									return common.ofGetResponseJson(new JSONArrayImpl(), "",
											"Delete Failed! Invalid Request (" + li_del_res + ").", ST999, "R",
											"Delete Failed! Invalid Request (" + li_del_res + ").").toString();
								}
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Success", ST0, "G", "Success")
										.toString();
							} else
							{
								reqJson.put(REQ_CD, ls_req_cd);	
								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
							}
						}
					}

					// insert data in audit table REQ_ACCT_MST_UPD_DTL for ACTION---- ADD , UPD,
					// DEL.
					detailDataJson = new JSONObjectImpl();
					detailDataJson.put("isNewRow", insertDtlJlist);
					detailDataJson.put("isDeleteRow", deleteDtlJlist);
					detailDataJson.put("isUpdatedRow", updateDtlJlist);

					if(ins_length != 0 || upd_length != 0 || del_length != 0 ) 
					{
						ls_insResponse = ofGetInsertAcctDocAuditData(connection, detailDataJson, reqJson, userJson,
								                                   "EASY_BANK.ACCT_MOBILE_DTL", "REQ_ACCT_MOBILE_DTL");

						responseJson = common.ofGetJsonObject(ls_insResponse);
						ls_resStatus = responseJson.getString(STATUS);

						if (!isSuccessStCode(ls_resStatus)) {
							return ls_insResponse;
						}
					}

				} //else if (COLUMNLIST.contains(key) && "ADVANCE_CONFIG_DTL".equals(key)) {
				
				if (reqJson.has("ADVANCE_CONFIG_DTL")) 
				{
					int ins_length = 0;
					int upd_length = 0;
					int del_length = 0;

					advanceConfigJlist = reqJson.getJSONArray("ADVANCE_CONFIG_DTL");

					for (int a = 0; a < advanceConfigJlist.length(); a++) 
					{
						advanceConfig = advanceConfigJlist.getJSONObject(a);
						ls_isFromMobileRegDtl = advanceConfig.getString(ADV_CONFIG_MAIN_TABLE);

						insertJlist = advanceConfig.getJSONArray("isNewRow");
						deleteJlist = advanceConfig.getJSONArray("isDeleteRow");
						updateJlist = advanceConfig.getJSONArray("isUpdatedRow");
						insAudJlist = new JSONArrayImpl();

						ins_length = insertJlist.length();
						upd_length = updateJlist.length();
						del_length = deleteJlist.length();

						if (ins_length != 0) 
						{

							if ("Y".equalsIgnoreCase(ls_isFromMobileRegDtl))
							{
								// GET MAX SR_CD
								ls_response = selectData.getSelectData(SELECT_ADVANCE_CONFIG_DTL_MAX_SR_CD, ls_compCd,ls_branchCd, ls_acctType, ls_acctCode);
							
								responseJson = common.ofGetJsonObject(ls_response);
								ls_resStatus = responseJson.getString(STATUS);

								if (isSuccessStCode(ls_resStatus)) 
								{
									li_maxSrCd = responseJson.getJSONArray("RESPONSE").getJSONObject(0).getInt("MAX_SR_CD");
									ll_SrCd = li_maxSrCd + 1;
								} else {
									return ls_response;
								}

								for (int i = 0; i < ins_length; i++) {

									insertDetailJson = insertJlist.getJSONObject(i);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(CONFIRMED, "N");
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_CUST_OTHER_ADD_DTL table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
										                      "REQ_ACCT_PARA_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
									return ls_detailResponse;
								}

							} else {
								// GET MAX_SR_CD FROM REQ_ACCT_MST_RELATIVE_DTL BY REQ_CD.

								jPrimaryDtl.put(REQ_CD, ls_req_cd);

								jallColumnList = getColumnDefination("REQ_ACCT_PARA_DTL", connection, true);
								ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_PARA_DTL", SR_CD, jallColumnList,jPrimaryDtl);
								insAudJlist = new JSONArrayImpl();
								
								// insert data in table REQ_ACCT_MST_RELATIVE_DTL

								for (int i = 0; i < ins_length; i++) {

									ll_SrCd++;
									insertDetailJson = insertJlist.getJSONObject(i);
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put(ACTIVE, "Y");
									insertDetailJson.put(SR_CD, ll_SrCd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(CONFIRMED, "N");
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
								}

								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_ACCT_MST_DOC_TEMPLATE table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,
										"REQ_ACCT_PARA_DTL");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
									return ls_detailResponse;
								}
							}
						}
						if (upd_length != 0) {

							if ("Y".equalsIgnoreCase(ls_isFromMobileRegDtl))
							{
								updAudJlist = new JSONArrayImpl();
								for (int i = 0; i < upd_length; i++) {

									updateDataJson = new JSONObjectImpl();
									
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updDetailJson = new JSONObjectImpl();
			
									for (String key : updateDataJson.keySet()) 
									{
										if (!key.equals("_UPDATEDCOLUMNS") && !key.equals("_OLDROWVALUE")) {
									        updDetailJson.put(key, updateDataJson.get(key));
									    }
									}
									
									updDetailJson.put(REQ_CD, ls_req_cd);							
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, ls_reqFlag);
									updDetailJson.put(CONFIRMED, "N");
									updDetailJson.put(COMP_CD, ls_acct_compCd);
									updDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									updDetailJson.put(ACCT_TYPE, ls_acctType);
									updDetailJson.put(ACCT_CD, ls_acctCode);
									updDetailJson.put(ENT_COMP_CD, ls_compCd);
									updDetailJson.put(ENT_BRANCH_CD, ls_branchCd);

									updAudJlist.put(updDetailJson);
								}

								
								 ls_detailResponse = insertDTLData(connection, updAudJlist, userJson, reqJson,
								 "REQ_ACCT_PARA_DTL");
								 
								 JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								 
								 if(!isSuccessStCode(jobjinsRes.getString(STATUS))) 
								 { 
									 return ls_detailResponse;
								 }
								 
							}else{

								for (int i = 0; i < upd_length; i++) {

									updateDataJson = new JSONObjectImpl();
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(i);
									updateDataJson.put(COMP_CD, ls_acct_compCd);
									updateDataJson.put(BRANCH_CD, ls_acct_branchCd);
									updateDataJson.put(ACCT_TYPE, ls_acctType);
									updateDataJson.put(ACCT_CD, ls_acctCode);
									updateDataJson.put("REQ_CD", ls_req_cd);
									ls_detailResponse = UpdateData(connection, updateDataJson, userJson,
											"REQ_ACCT_PARA_DTL");
									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
									if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
										return ls_detailResponse;
									}
									// updateJlist.put(updateDataJson);
								}
							}
						}
						if (del_length != 0)
						{
							if (!"Y".equalsIgnoreCase(ls_isFromMobileRegDtl))
							{

								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
								
								reqJson.put(REQ_CD, ls_req_cd);	
								jPrimaryDtl.put(REQ_CD, ls_req_cd);
								JSONArrayImpl jallColumn = getColumnDefination("REQ_ACCT_PARA_DTL", connection, true);
								int li_del_res = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,"REQ_ACCT_PARA_DTL", jallColumn);

								if (li_del_res == 0)  {
									connection.rollback();
									return common.ofGetResponseJson(new JSONArrayImpl(), "",
											"Delete Failed! Invalid Request (" + li_del_res + ").", ST999, "R",
											"Delete Failed! Invalid Request (" + li_del_res + ").").toString();
								}
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Success", ST0, "G", "Success")
										.toString();
							} else
							{
								reqJson.put(REQ_CD, ls_req_cd);
								
								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
							}
						}
					}

					// insert data in audit table REQ_ACCT_MST_UPD_DTL for ACTION---- ADD , UPD,
					// DEL.
					detailDataJson = new JSONObjectImpl();
					detailDataJson.put("isNewRow", insertDtlJlist);
					detailDataJson.put("isDeleteRow", deleteDtlJlist);
					detailDataJson.put("isUpdatedRow", updateDtlJlist);

					if(ins_length != 0 || upd_length != 0 || del_length != 0 ) 
					{
						ls_insResponse = ofGetInsertAcctDocAuditData(connection, detailDataJson, reqJson, userJson,
								                                     "EASY_BANK.ACCT_PARA_DTL", "REQ_ACCT_PARA_DTL");

						responseJson = common.ofGetJsonObject(ls_insResponse);
						ls_resStatus = responseJson.getString(STATUS);

						if (!isSuccessStCode(ls_resStatus))
						{
							return ls_insResponse;
						}
					}
				} //else if (COLUMNLIST.contains(key) && "DOC_MST".equals(key)) {
				if (reqJson.has("DOC_MST"))
				{		
					  reqJson.put(REQ_CD, ls_req_cd);
					  
					  JSONArrayImpl docDtlJarraylist = reqJson.getJSONArray("DOC_MST");
					  
					  if(docDtlJarraylist.length() != 0)
					  {
						  ls_insResponse = doUpdateExCustDocumentData(reqJson, ll_ENTRY_TYPE);

						  responseJson = common.ofGetJsonObject(ls_insResponse); 
						  ls_resStatus = responseJson.getString(STATUS);

						  if (!isSuccessStCode(ls_resStatus))
						  {
							  return ls_insResponse;
						  }
					  }
					 
				} //else if(COLUMNLIST.contains(key) && "JOINT_ACCOUNT_DTL".equals(key)) {
				
				if (reqJson.has("JOINT_ACCOUNT_DTL")) 
				{
					JSONArrayImpl otherAddJlist = reqJson.getJSONArray("JOINT_ACCOUNT_DTL");
					
					int ins_length = 0;
					int upd_length = 0;
					int del_length = 0;
					
					for (int i = 0; i < otherAddJlist.length(); i++)
					{
						otherAddJson = otherAddJlist.getJSONObject(i);
						ls_isFromJointAcctDtl = otherAddJson.getString("IS_FROM_JOINT_ACCOUNT_DTL");

						insertJlist = otherAddJson.getJSONArray("isNewRow");
						deleteJlist = otherAddJson.getJSONArray("isDeleteRow");
						updateJlist = otherAddJson.getJSONArray("isUpdatedRow");
						insAudJlist = new JSONArrayImpl();

						ins_length = insertJlist.length();
						upd_length = updateJlist.length();
						del_length = deleteJlist.length();

						if (ins_length != 0)
						{

							if ("Y".equalsIgnoreCase(ls_isFromJointAcctDtl))
							{
								for (int j = 0; j < ins_length; j++) 
								{
									insertDetailJson = insertJlist.getJSONObject(j);
																
									ls_response = selectData.getSelectData(SELECT_JOINT_ACCOUNT_MAX_SR_CD,ls_compCd,ls_branchCd, ls_acctType, ls_acctCode,insertDetailJson.getString(J_TYPE));
									responseJson = common.ofGetJsonObject(ls_response); 
									ls_resStatus = responseJson.getString(STATUS);

									if (isSuccessStCode(ls_resStatus))
									{ 
										li_maxSrCd = responseJson.getJSONArray("RESPONSE").getJSONObject(0).getInt("MAX_SR_CD");
										ll_SrCd = li_maxSrCd + 1;
									}else{ 
										return ls_response;
									}
														
									insertDetailJson.put(REQ_CD, ls_req_cd);
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
									insertDetailJson.put(SR_CD, ll_SrCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
								}
								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_JOINT_ACCOUNT_MST table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,"REQ_JOINT_ACCOUNT_MST");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								
								if (!isSuccessStCode(jobjinsRes.getString(STATUS)))
								{
									return ls_detailResponse;
								}

							} else {
				
								insAudJlist = new JSONArrayImpl();

								// insert data in table JOINT_ACCOUNT_DTL
								for (int k = 0; k < ins_length; k++)
								{
									insertDetailJson = insertJlist.getJSONObject(k);
									
									jPrimaryDtl.put(COMP_CD, ls_acct_compCd);
									jPrimaryDtl.put(BRANCH_CD, ls_acct_branchCd);
									jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
									jPrimaryDtl.put(ACCT_CD, ls_acctCode);
									jPrimaryDtl.put(J_TYPE, insertDetailJson.getString(J_TYPE));	
									jallColumnList = getColumnDefination("REQ_JOINT_ACCOUNT_MST", connection, true);
									ll_SrCd = getMaxSrCd(connection, "REQ_JOINT_ACCOUNT_MST", SR_CD, jallColumnList,jPrimaryDtl);
									ll_SrCd++;
				
									insertDetailJson.put(REQ_CD, ls_req_cd);
									//insertDetailJson.put(ACTIVE, "Y");						
									insertDetailJson.put("ACTION", "ADD");
									insertDetailJson.put(REQ_FLAG, ls_reqFlag);
									insertDetailJson.put(COMP_CD, ls_acct_compCd);
									insertDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									insertDetailJson.put(ACCT_TYPE, ls_acctType);
									insertDetailJson.put(ACCT_CD, ls_acctCode);
									insertDetailJson.put(ENT_COMP_CD, ls_compCd);
									insertDetailJson.put(ENT_BRANCH_CD, ls_branchCd);					
									insertDetailJson.put(SR_CD, ll_SrCd);
									insAudJlist.put(insertDetailJson);
									insertDtlJlist.put(insertDetailJson);
								}
								reqJson.put(REQ_CD, ls_req_cd);

								// insert data in the REQ_JOINT_ACCOUNT_MST table.
								ls_detailResponse = insertDTLData(connection, insAudJlist, userJson, reqJson,"REQ_JOINT_ACCOUNT_MST");

								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								if (!isSuccessStCode(jobjinsRes.getString(STATUS))) {
									return ls_detailResponse;
								}
							}
							
							//Collateral Other Details Insertion work
							
							/*
							 * ls_updResponse =
							 * ExAcctCollateralOtherSecurityDtlButton(connection,insertJlist, userJson,
							 * jPrimaryDtl,reqJson,ls_isFromJointAcctDtl,"ADD");
							 * 
							 * JSONObjectImpl securityJson = common.ofGetJsonObject(ls_updResponse); String
							 * ls_securitystatus = securityJson.getString(STATUS);
							 * 
							 * if (!isSuccessStCode(ls_securitystatus)) { return ls_updResponse; }
							 */
							
						}
						
						if (upd_length != 0)
						{
							updAudJlist = new JSONArrayImpl();
							if ("Y".equalsIgnoreCase(ls_isFromJointAcctDtl)) {
								
								for (int a = 0; a < upd_length; a++)
								{
									updateDataJson = new JSONObjectImpl();							
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(a);
									updDetailJson = new JSONObjectImpl();
			
									for (String key : updateDataJson.keySet()) 
									{
										if (!key.equals("_UPDATEDCOLUMNS") && !key.equals("_OLDROWVALUE")) {
									        updDetailJson.put(key, updateDataJson.get(key));
									    }
									}
									updDetailJson.put(REQ_CD, ls_req_cd);					
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, ls_reqFlag);
									updDetailJson.put(COMP_CD, ls_acct_compCd);
									updDetailJson.put(BRANCH_CD, ls_acct_branchCd);
									updDetailJson.put(ACCT_TYPE, ls_acctType);
									updDetailJson.put(ACCT_CD, ls_acctCode);
									updDetailJson.put(ENT_COMP_CD, ls_compCd);
									updDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
																
									updAudJlist.put(updDetailJson);
								}
								reqJson.put(REQ_CD, ls_req_cd);
		
								 ls_detailResponse = insertDTLData(connection, updAudJlist, userJson, reqJson,"REQ_JOINT_ACCOUNT_MST");
								  
								JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
								
								if(!isSuccessStCode(jobjinsRes.getString(STATUS)))
								 {
									 return ls_detailResponse;
								 }
								
							} else {

								for (int a = 0; a < upd_length; a++) {

									updateDataJson = new JSONObjectImpl();
									updateDtlJlist = updateJlist;
									updateDataJson = updateJlist.getJSONObject(a);
									updateDataJson.put(COMP_CD, ls_acct_compCd);
									updateDataJson.put(BRANCH_CD, ls_acct_branchCd);
									updateDataJson.put(ACCT_TYPE, ls_acctType);
									updateDataJson.put(ACCT_CD, ls_acctCode);
									updateDataJson.put("REQ_CD", ls_req_cd);
									reqJson.put(REQ_CD, ls_req_cd);

									ls_detailResponse = UpdateData(connection, updateDataJson,userJson,"REQ_JOINT_ACCOUNT_MST");

									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse); 
									if(!isSuccessStCode(jobjinsRes.getString(STATUS)))
									{ 
										return ls_detailResponse;
									}
									// updateJlist.put(updateDataJson);
								}
							}
							
							//Collateral Other Details Update work
							
							/*
							 * ls_updResponse =
							 * ExAcctCollateralOtherSecurityDtlButton(connection,updateJlist, userJson,
							 * jPrimaryDtl,reqJson,ls_isFromJointAcctDtl,"UPD");
							 * 
							 * JSONObjectImpl securityJson = common.ofGetJsonObject(ls_updResponse); String
							 * ls_securitystatus = securityJson.getString(STATUS);
							 * 
							 * if (!isSuccessStCode(ls_securitystatus)) { return ls_updResponse; }
							 */
						}
						if (del_length != 0) 
						{
							if (!"Y".equalsIgnoreCase(ls_isFromJointAcctDtl))
							{
								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
								
								reqJson.put(REQ_CD, ls_req_cd);						
								jPrimaryDtl.put(REQ_CD, ls_req_cd);
								
								JSONArrayImpl jallColumn = getColumnDefination("REQ_JOINT_ACCOUNT_MST", connection,true);
								int li_del_res = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,"REQ_JOINT_ACCOUNT_MST", jallColumn);

								if (li_del_res == 0) {
									connection.rollback();
									return common.ofGetResponseJson(new JSONArrayImpl(), "",
											"Delete Failed! Invalid Request (" + li_del_res + ").", ST999, "R",
											"Delete Failed! Invalid Request (" + li_del_res + ").").toString();
								}
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Success", ST0, "G", "Success")
										.toString();
							} else
							{
								reqJson.put(REQ_CD, ls_req_cd);
								
								deleteDtlJlist = new JSONArrayImpl();
								deleteDtlJlist = deleteJlist;
							}
						}
					}
					// insert data in audit table REQ_JOINT_ACCOUNT_MST for ACTION---- ADD , UPD,DEL.
					detailDataJson = new JSONObjectImpl();
					detailDataJson.put("isNewRow", insertDtlJlist);
					detailDataJson.put("isDeleteRow", deleteDtlJlist);
					detailDataJson.put("isUpdatedRow", updateDtlJlist);

					if(ins_length != 0 || upd_length != 0 || del_length != 0 ) 
					{				
						 ls_insResponse = ofGetInsertAcctDocAuditData(connection, detailDataJson,reqJson, userJson, "EASY_BANK.JOINT_ACCOUNT_MST", "REQ_JOINT_ACCOUNT_MST");
						 
						 responseJson = common.ofGetJsonObject(ls_insResponse); ls_resStatus =
						 responseJson.getString(STATUS);
						 
						 if (!isSuccessStCode(ls_resStatus)) 
						 { 
							 return ls_insResponse;
						 }				
					}
				}
			//}
			return ofGetResponseJson(new JSONArrayImpl().put(resDataJson), "", " ", ST0, "G", "").toString();

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetUpdateCustData", "(ENP293)");
		}
	}

	public String ofGetInsertAccountHdrData(Connection connection, JSONObjectImpl reqJson, JSONObjectImpl userJson,
			String la_updFlag) {

		JSONObjectImpl insertDataJson = new JSONObjectImpl();

		String ls_date = StringUtils.EMPTY;
		String ls_updResponse = StringUtils.EMPTY;

		try {

			setConfirmationRules(1);

			insertDataJson.put(ACCT_TYPE, reqJson.getString(ACCT_TYPE));
			insertDataJson.put(ACCT_CD, reqJson.getString(ACCT_CD));
			insertDataJson.put(CONFIRMED, "P");
			insertDataJson.put(REQ_FLAG, reqJson.getString(REQ_FLAG));
			insertDataJson.put(COMP_CD, reqJson.getString(COMP_CD));
			insertDataJson.put(BRANCH_CD,reqJson.getString(BRANCH_CD));
			insertDataJson.put(ENT_COMP_CD, getRequestUniqueData.getCompCode());
			insertDataJson.put(ENT_BRANCH_CD, getRequestUniqueData.getBranchCode());
			insertDataJson.put(ENTRY_TYPE, 1);
			insertDataJson.put("REQ_DATE", getRequestUniqueData.getWorkingDate());
			insertDataJson.put("UPD_TAB_NAME", la_updFlag);

			ls_updResponse = InsertData(connection, insertDataJson, userJson, 0, "REQ_ACCT_MST_HDR_SEQ",
					REQ_ACCT_MST_HDR);
			responseJson = common.ofGetJsonObject(ls_updResponse);
			ls_resStatus = responseJson.getString(STATUS);

			return ls_updResponse;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAccountHdrData", "(ENP293)");
		}
	}

	public String ofGetInsertAuditData(Connection connection, JSONObjectImpl reqJson, JSONObjectImpl updateJson,
			JSONObjectImpl userJson, String la_mst_tablename, String la_req_tablename, String la_action,
			long ll_ENTRY_TYPE) {
		String ls_cust_type = StringUtils.EMPTY;
		String ls_entry_type = StringUtils.EMPTY;
		String ls_isNew = StringUtils.EMPTY;
		String ls_acct_type = StringUtils.EMPTY;
		String ls_acct_cd = StringUtils.EMPTY;
		String ls_req_cd = StringUtils.EMPTY;
		String ls_req_tablename = StringUtils.EMPTY;
		String ls_upd_column = StringUtils.EMPTY;
		String ls_old_value = StringUtils.EMPTY;
		String ls_new_value = StringUtils.EMPTY;
		String ls_column_type = StringUtils.EMPTY;
		String ls_audit_res = StringUtils.EMPTY;

		long ll_sr_cd = 0L;

		JSONArrayImpl updValueJlist = new JSONArrayImpl();
		JSONArrayImpl detailJlist = new JSONArrayImpl();
		JSONArrayImpl updateJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl jallColumnList = new JSONArrayImpl();

		JSONObjectImpl oldValueJson = new JSONObjectImpl();
		JSONObjectImpl mainDtlJson = new JSONObjectImpl();
		JSONObjectImpl detailJson = new JSONObjectImpl();
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();

		try {

			ls_cust_type = reqJson.getString(REQ_FLAG);
			ls_req_cd = updateJson.getString(REQ_CD);

			jPrimaryDtl.put(REQ_CD, ls_req_cd);
			jallColumnList = getColumnDefination("REQ_ACCT_MST_UPD_DTL", connection, true);
			ll_sr_cd = getMaxSrCd(connection, "REQ_ACCT_MST_UPD_DTL", SR_CD, jallColumnList, jPrimaryDtl);

			if (ls_cust_type.equals("E")) {

				oldValueJson = updateJson.getJSONObject("_OLDROWVALUE");
				String[] keysToRemove = {"PRIO_CD", "SUB_PRIO_CD", "PARENT_GROUP"};
				for (String key : keysToRemove) {
				    oldValueJson.remove(key);
				}
				updValueJlist = updateJson.getJSONArray("_UPDATEDCOLUMNS");
				ls_acct_type = reqJson.getString(ACCT_TYPE);
				ls_acct_cd = reqJson.getString(ACCT_CD);
				ls_req_tablename = la_req_tablename;

				detailJlist = new JSONArrayImpl();
				for (int i = 0; i < updValueJlist.length(); i++) 
				{
					detailJson = new JSONObjectImpl();
					ls_upd_column = updValueJlist.getString(i);

					if(ACCT_MST_03_ColumnList.contains(ls_upd_column))
					{
						detailJson.put("TABLE_NAME", "EASY_BANK.ACCT_MST_03");
						ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_03", ls_upd_column);
					}
					else
					{
						detailJson.put("TABLE_NAME", la_mst_tablename);					
				    	ls_column_type = ofGetColumnDataType(connection, ls_req_tablename, ls_upd_column);
					}
						
					if (oldValueJson.has(ls_upd_column) && !ls_column_type.equals("")) 
					{
						ll_sr_cd = ll_sr_cd + 1;
						ls_old_value = oldValueJson.getString(ls_upd_column);
						ls_new_value = updateJson.getString(ls_upd_column);
						
						detailJson.put(COLUMN_NAME, ls_upd_column);
						detailJson.put(ENTRY_TYPE, ll_ENTRY_TYPE);
						detailJson.put("NEW_VALUE", ls_new_value);
						detailJson.put("OLD_VALUE", ls_old_value);
						detailJson.put("OWNER_NAME", "EASY_BANK");
						detailJson.put(BRANCH_CD, reqJson.getString(BRANCH_CD));
						detailJson.put(COMP_CD, reqJson.getString(COMP_CD));
						detailJson.put(ACCT_CD, ls_acct_cd);
						detailJson.put(ACCT_TYPE, ls_acct_type);
						detailJson.put(REQ_CD, ls_req_cd);
						detailJson.put("ACTION", la_action);
						detailJson.put(COLUMN_TYPE, ls_column_type);
						detailJson.put(SR_CD, ll_sr_cd);
								
						detailJlist.put(detailJson);
					}
				}
				mainDtlJson.put("isNewRow", detailJlist);
				mainDtlJson.put("isDeleteRow", deleteJlist);
				mainDtlJson.put("isUpdatedRow", updateJlist);
				ls_audit_res = UpdateDetailsData(connection, mainDtlJson, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");
				return ls_audit_res;

			} else {
				return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R", "common.invalid_req_data")
						.toString();
			}

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAuditData", "(ENP293)");
		}

	}

	public String ofGetColumnDataType(Connection connection, String la_table_name, String la_upd_column) {

		String ls_table_name = StringUtils.EMPTY;
		String ls_upd_column = StringUtils.EMPTY;
		String ls_column_nm = StringUtils.EMPTY;
		String ls_column_type = StringUtils.EMPTY;

		JSONArrayImpl jallColumnList = new JSONArrayImpl();
		JSONObjectImpl jallColumnJson = new JSONObjectImpl();

		ls_table_name = la_table_name;
		ls_upd_column = la_upd_column;

		try {
			jallColumnList = getColumnDefination(ls_table_name, connection, true);

			for (int i = 0; i < jallColumnList.length(); i++) {
				jallColumnJson = new JSONObjectImpl();

				jallColumnJson = jallColumnList.getJSONObject(i);
				ls_column_nm = jallColumnJson.getString("COLUMN_NAME");

				if (ls_column_nm.equals(ls_upd_column)) {
					ls_column_type = jallColumnJson.getString("TYPE_NAME");
					break;
				}
			}

			return ls_column_type;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetColumnDataType", "(ENP293)");
		}
	}

	public String doUpdateExCustDocumentData(JSONObjectImpl requestDataJson, long ll_ENTRY_TYPE) {

		LoggerImpl loggerImpl = null;

		String ls_com_cd = StringUtils.EMPTY;
		String ls_branch_cd = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		String ls_response = StringUtils.EMPTY;
		String ls_insResponse = StringUtils.EMPTY;
		String ls_req_cd = StringUtils.EMPTY;
		String ls_auditResponse = StringUtils.EMPTY;
		String ls_custId = StringUtils.EMPTY;
		String ls_is_from_main = StringUtils.EMPTY;
		String ls_is_fromReq = StringUtils.EMPTY;
		String ls_updResponse = StringUtils.EMPTY;

		boolean lb_isMainDataAdd;
		boolean lb_isMainDataUpd;
		boolean lb_isMainDataDel;

		int li_insLength;
		int li_updLength;
		int li_delLength;

		Connection connection = null;

		JSONObjectImpl documentDtlJson = new JSONObjectImpl();
		JSONObjectImpl responseJson = new JSONObjectImpl();
		JSONObjectImpl responseDataJson;
		JSONObjectImpl userJson;
		JSONObjectImpl resDataJson = new JSONObjectImpl();
		JSONObjectImpl detailDataJson = new JSONObjectImpl();
		JSONObjectImpl masterDataJson = new JSONObjectImpl();
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();

		JSONArrayImpl jallColumnList = new JSONArrayImpl();
		JSONArrayImpl dbResponseJList = new JSONArrayImpl();
		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl updateJlist = new JSONArrayImpl();
		JSONArrayImpl docDtlJlist = new JSONArrayImpl();

		try {

			loggerImpl = new LoggerImpl();
			connection = getDbConnection();

			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doUpdateExCustDocumentData");
			loggerImpl.generateProfiler("doUpdateExCustDocumentData");
			loggerImpl.startProfiler("Preparing request data.");

			userJson = getRequestUniqueData.getLoginUserDetailsJson();
			ls_com_cd = getRequestUniqueData.getCompCode();
			ls_branch_cd = getRequestUniqueData.getBranchCode();

			loggerImpl.startProfiler("Calling doUpdateExCustDocumentData API response data.");

			docDtlJlist = requestDataJson.getJSONArray("DOC_MST");

			// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE AND REQ_ACCT_MST_DOC_TEMPLATE_SDT
			// TABLE.

			for (int k = 0; k < docDtlJlist.length(); k++) {

				masterDataJson = docDtlJlist.getJSONObject(k);
				ls_is_from_main = masterDataJson.getString(IS_FROM_MAIN);
				ls_is_fromReq = masterDataJson.getString(NEW_FLAG);

				if (masterDataJson.has("_isNewRow")) {
					lb_isMainDataAdd = masterDataJson.getBoolean("_isNewRow");

					if (lb_isMainDataAdd) {

						ls_auditResponse = ofGetCheckDocumentDetailData(connection, requestDataJson, masterDataJson,
								userJson, true, "ADD", ls_is_from_main, getRequestUniqueData.getCompCode(),
								getRequestUniqueData.getBranchCode(), requestDataJson.getString(REQ_CD),
								requestDataJson.getString(REQ_FLAG), requestDataJson.getString(CUSTOMER_ID),
								requestDataJson.getString(ACCT_TYPE), requestDataJson.getString(ACCT_CD), ls_is_fromReq,
								ll_ENTRY_TYPE);

						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_auditResponse);
						if (!isSuccessStCode(insertResJson.getString(STATUS))) {
							return ls_auditResponse;
						}

					} else {
						// update main detail only insert data in req_doc_template_dtl table.ACTION_TYPE
						// = M AND ACTION = UPD
						ls_auditResponse = ofGetCheckDocumentDetailData(connection, requestDataJson, masterDataJson,
								userJson, true, "UPD", ls_is_from_main, getRequestUniqueData.getCompCode(),
								getRequestUniqueData.getBranchCode(), requestDataJson.getString(REQ_CD),
								requestDataJson.getString(REQ_FLAG), requestDataJson.getString(CUSTOMER_ID),
								requestDataJson.getString(ACCT_TYPE), requestDataJson.getString(ACCT_CD), ls_is_fromReq,
								ll_ENTRY_TYPE);

						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_auditResponse);
						if (!isSuccessStCode(insertResJson.getString(STATUS))) {
							return ls_auditResponse;
						}
					}
					resDataJson.put(REQ_CD, requestDataJson.getString(REQ_CD));

				} else if (masterDataJson.has("_isDeleteRow")) {

					lb_isMainDataDel = masterDataJson.getBoolean("_isDeleteRow");

					ls_auditResponse = ofGetCheckDocumentDetailData(connection, requestDataJson, masterDataJson,
							userJson, lb_isMainDataDel, "DEL", ls_is_from_main, getRequestUniqueData.getCompCode(),
							getRequestUniqueData.getBranchCode(), requestDataJson.getString(REQ_CD),
							requestDataJson.getString(REQ_FLAG), requestDataJson.getString(CUSTOMER_ID),
							requestDataJson.getString(ACCT_TYPE), requestDataJson.getString(ACCT_CD), ls_is_fromReq,
							ll_ENTRY_TYPE);

					JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_auditResponse);
					if (!isSuccessStCode(insertResJson.getString(STATUS))) {
						return ls_auditResponse;

					}
					resDataJson.put(REQ_CD, requestDataJson.getString(REQ_CD));
				} else {
					return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R",
							"common.invalid_req_data").toString();
				}
			}
			JSONObjectImpl auditResJson = common.ofGetJsonObject(ls_auditResponse);
			if (isSuccessStCode(auditResJson.getString(STATUS))) {
				//				connection.commit();
				return ofGetResponseJson(new JSONArrayImpl().put(resDataJson), "", " ", ST0, "G", "").toString();
			} else {
				//				connection.rollback();
				return ls_auditResponse;
			}
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAuditData", "(ENP245)");
		} finally {
			closeDbObject(connection);
		}

	}

	public String ofGetCheckDocumentDetailData(Connection connection, JSONObjectImpl reqJson,
			JSONObjectImpl documentJson, JSONObjectImpl userJson, boolean lb_checkFirst, String la_checkAction,
			String la_checkIsFromMain, String la_compCd, String la_branchCd, String la_reqCd, String la_reqFlag,
			String la_custId, String la_acctType, String la_acctCd, String la_checkIsFromReq, long ll_ENTRY_TYPE) {

		String ls_detail_response = StringUtils.EMPTY;
		String ls_auditResponse = StringUtils.EMPTY;
		String ls_upd_column = StringUtils.EMPTY;
		String ls_new_value = StringUtils.EMPTY;

		LoggerImpl loggerImpl = null;

		long ll_Trancd = 0L;
		long ll_SrCd = 0L;
		long ll_dtlSrCd;
		int li_insLength;
		int li_updLength;
		int li_delLength;

		JSONObjectImpl insertDetailJson;
		JSONObjectImpl updateDataJson;
		JSONObjectImpl oldValueJson;
		JSONObjectImpl dtlJson = new JSONObjectImpl();
		JSONObjectImpl updDetailJson;
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
		JSONObjectImpl detailDataJson = new JSONObjectImpl();

		JSONArrayImpl jallColumnList = new JSONArrayImpl();
		JSONArrayImpl jallDtlColumnList = new JSONArrayImpl();
		JSONArrayImpl updValueJlist = new JSONArrayImpl();
		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl updateDtlJlist = new JSONArrayImpl();
		JSONArrayImpl insDtlJlist = new JSONArrayImpl();
		JSONArrayImpl  updAudJlist = new JSONArrayImpl();

		try {

			detailDataJson = documentJson.getJSONObject("DETAILS_DATA");
			insertJlist = detailDataJson.getJSONArray("isNewRow");
			updateDtlJlist = detailDataJson.getJSONArray("isUpdatedRow");
			deleteJlist = detailDataJson.getJSONArray("isDeleteRow");
			li_updLength = updateDtlJlist.length();
			li_insLength = insertJlist.length();
			li_delLength = deleteJlist.length();

			jPrimaryDtl.put(REQ_CD, la_reqCd);
			jallDtlColumnList = getColumnDefination("REQ_ACCT_MST_DOC_TEMPLATE_SDT", connection, true);
			ll_dtlSrCd = getMaxSrCd(connection, "REQ_ACCT_MST_DOC_TEMPLATE_SDT", "LINE_CD", jallDtlColumnList,jPrimaryDtl);
			ll_dtlSrCd++;

			// GET MAX SR_CD .
			jallColumnList = getColumnDefination("REQ_ACCT_MST_DOC_TEMPLATE", connection, true);
			ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_DOC_TEMPLATE", "SR_CD", jallColumnList, jPrimaryDtl);
			ll_SrCd++;

			if (la_checkAction.equals("ADD"))
			{
				// GET MAX TRANCD and LINE_CD
				
				//Object ll_MaxTrancd = getMaxCd(connection, "REQ_ACCT_MST_DOC_TEMPLATE", TRAN_CD, 0,
				//		"SEQ_REQ_ACCT_MST_DOC_TEMPLATE");
				
			    	Object ll_MaxTrancd = getMaxCd(connection, "EASY_BANK.ACCT_MST_DOC_TEMPLATE", TRAN_CD, 0,
						                           "EASY_BANK.SEQ_ACCT_MST_DOC_TEMPLATE");

				// insert data in table REQ_ACCT_MST_DOC_TEMPLATE

				if (la_checkIsFromMain.equals("Y"))
				{			
					if (lb_checkFirst)
					{
						// insert data in table REQ_ACCT_MST_DOC_TEMPLATE
						// ------ACTION_TYPE = M ACTION = ADD

						documentJson.put(REQ_CD, la_reqCd);
						documentJson.put(TRAN_CD, ll_MaxTrancd);
						documentJson.put(ACCT_TYPE, la_acctType);
						documentJson.put(ACCT_CD, la_acctCd);
						documentJson.put(ACTIVE, "Y");
						documentJson.put(DOC_TYPE, "ACCT");
						documentJson.put("ACTION", "ADD");
						documentJson.put(REQ_FLAG, la_reqFlag);
						documentJson.put(CONFIRMED, "Y");
						documentJson.put(COMP_CD, la_compCd);
						documentJson.put(CUSTOMER_ID, la_custId);
						documentJson.put(BRANCH_CD, la_branchCd);
						documentJson.put(ENT_COMP_CD, la_compCd);
						documentJson.put(SR_CD, ll_SrCd);
						documentJson.put(ENT_BRANCH_CD, la_branchCd);

						// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE_DTL TABLE.

						ls_detail_response = InsertData(connection, documentJson, userJson, 2, "","REQ_ACCT_MST_DOC_TEMPLATE");

						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_detail_response);
					
						if (!isSuccessStCode(insertResJson.getString(STATUS))) 
						{
							return ls_detail_response;
						}

						if (la_reqFlag.equals("E"))
						{
							// INSERT DATA in audit table.
							ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
									userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE", "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT",
									la_reqCd, "M", "ADD", ll_ENTRY_TYPE);

							JSONObjectImpl auditResJson = common.ofGetJsonObject(ls_detail_response);
							
							if (!isSuccessStCode(auditResJson.getString(STATUS)))
							{
								return ls_detail_response;
							}
						}

						if (li_insLength != 0)
						{
							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++)
							{
								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);
								insertDetailJson.put("ACTION", "ADD");

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}

							if (la_reqFlag.equals("E"))
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
									return ls_detail_response;
								}
							}
						}
					} else {

						if (li_insLength != 0)
						{
							insDtlJlist = new JSONArrayImpl();
							
							for (int j = 0; j < li_insLength; j++)
							{
								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}

							if (la_reqFlag.equals("E")) 
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								
								if (!isSuccessStCode(insAuditResJson.getString(STATUS)))
								{
									return ls_detail_response;
								}
							}
						}

					}
				} else {
					// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE TABLE AND
					// REQ_ACCT_MST_DOC_TEMPLATE_SDT IF IMAGE IS ADDED.
					if (lb_checkFirst)
					{
						// insert data in table REQ_ACCT_MST_DOC_TEMPLATE
						// ------ACTION_TYPE = M ACTION = ADD

						documentJson.put(REQ_CD, la_reqCd);
						// documentJson.put(TRAN_CD, ll_MaxTrancd);
						documentJson.put(ACCT_TYPE, la_acctType);
						documentJson.put(ACCT_CD, la_acctCd);
						documentJson.put(ACTIVE, "Y");
						documentJson.put(DOC_TYPE, "ACCT");
						documentJson.put("ACTION", "ADD");
						documentJson.put(REQ_FLAG, la_reqFlag);
						documentJson.put(CONFIRMED, "Y");
						documentJson.put(COMP_CD, la_compCd);
						documentJson.put(CUSTOMER_ID, la_custId);
						documentJson.put(BRANCH_CD, la_branchCd);
						documentJson.put(ENT_COMP_CD, la_compCd);
						documentJson.put(SR_CD, ll_SrCd);
						documentJson.put(ENT_BRANCH_CD, la_branchCd);

						// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE_DTL TABLE.

						ls_detail_response = InsertData(connection, documentJson, userJson, 2, "","REQ_ACCT_MST_DOC_TEMPLATE");

						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_detail_response);
						if (!isSuccessStCode(insertResJson.getString(STATUS)))
                        {
							return ls_detail_response;
						}

						if (la_reqFlag.equals("E")) {

							// INSERT DATA in audit table.
							ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
									userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE", "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT",
									la_reqCd, "M", "ADD", ll_ENTRY_TYPE);

							JSONObjectImpl auditResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(auditResJson.getString(STATUS))) {
								return ls_detail_response;
							}
						}

						if (li_insLength != 0) {

							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++) {

								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}

							if (la_reqFlag.equals("E")) {
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
									return ls_detail_response;
								}
							}

						}

						// CHECK NEW FLAG IF Y THEN DATA ALREADY IS IN REQUEST TABLE ELSE IN MAIN TABLE.

						if (la_checkIsFromReq.equals("N")) {

							// ADD THE UPDATED DATA IN REQ_ACCT_MST_DOC_TEMPLATE TABLE.
							if (li_updLength != 0) {
								updateDtlJlist = new JSONArrayImpl();
								for (int j = 0; j < li_updLength; j++) {

									updateDataJson = new JSONObjectImpl();

									updateDataJson = updateDtlJlist.getJSONObject(j);
									oldValueJson = updateDataJson.getJSONObject("_OLDROWVALUE");
									updValueJlist = updateDataJson.getJSONArray("_UPDATEDCOLUMNS");
									updDetailJson = new JSONObjectImpl();

									for (int i = 0; i < updValueJlist.length(); i++) {

										ls_upd_column = updValueJlist.getString(i);

										if (oldValueJson.has(ls_upd_column)) {
											ls_new_value = updateDataJson.getString(ls_upd_column);
											updDetailJson.put(ls_upd_column, ls_new_value);
										}
									}
									updDetailJson.put(REQ_CD, la_reqCd);
									updDetailJson.put(ACCT_TYPE, la_acctType);
									updDetailJson.put(ACCT_CD, la_acctCd);
									updDetailJson.put(ACTIVE, "Y");
									updDetailJson.put(DOC_TYPE, "ACCT");
									updDetailJson.put(TRAN_CD, updateDataJson.getString(TRAN_CD));
									updDetailJson.put(SR_CD, updateDataJson.getString(SR_CD));
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, la_reqFlag);
									updDetailJson.put(CONFIRMED, "N");
									updDetailJson.put(COMP_CD, la_compCd);
									updDetailJson.put(CUSTOMER_ID, la_custId);
									updDetailJson.put(BRANCH_CD, la_branchCd);
									updDetailJson.put(ENT_COMP_CD, la_compCd);
									updDetailJson.put(ENT_BRANCH_CD, la_branchCd);
									updateDtlJlist.put(updDetailJson);
								}
								ls_detail_response = insertDTLData(connection, updateDtlJlist, userJson, jPrimaryDtl,
										"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateResJson.getString(STATUS))) {
									return ls_detail_response;
								}
							}
						} else {
							// UPDATE DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							if (li_updLength != 0) {
								dtlJson = new JSONObjectImpl();
								dtlJson.put("isDeleteRow", new JSONArrayImpl());
								dtlJson.put("isNewRow", new JSONArrayImpl());
								dtlJson.put("isUpdatedRow", updateDtlJlist);

								// update data in REQ_ACCT_MST_DOC_TEMPLATE_SDT;
								ls_detail_response = UpdateDetailsData(connection, dtlJson, userJson, jPrimaryDtl,
										"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateDtlResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateDtlResJson.getString(STATUS))) {
									return ls_detail_response;
								}

							}
						}
						if (li_delLength != 0) 
						{
							jPrimaryDtl.put("REQ_CD", la_reqCd);
							int delRowCount = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT", jallDtlColumnList);

							if (delRowCount == 0) {
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Data Deletion Fail...", ST999,
										"G", "Data Deletion Fail...").toString();
							}

							return common.ofGetResponseJson(new JSONArrayImpl(), "", "", ST0, "G", "").toString();
						}

					}
				}
			} else if (la_checkAction.equals("UPD"))
			{
				if (la_checkIsFromMain.equals("Y"))
				{
					if (lb_checkFirst)
					{
						updateDataJson = documentJson;

						oldValueJson = updateDataJson.getJSONObject("_OLDROWVALUE");
						updValueJlist = updateDataJson.getJSONArray("_UPDATEDCOLUMNS");
						updDetailJson = new JSONObjectImpl();

						for (int i = 0; i < updValueJlist.length(); i++) {

							ls_upd_column = updValueJlist.getString(i);

							if (oldValueJson.has(ls_upd_column)) {
								ls_new_value = updateDataJson.getString(ls_upd_column);
								updDetailJson.put(ls_upd_column, ls_new_value);
							}
						}						
						updDetailJson.put(REQ_CD, la_reqCd);
						updDetailJson.put(ACCT_TYPE, la_acctType);
						updDetailJson.put(ACCT_CD, la_acctCd);
						updDetailJson.put(ACTIVE, "Y");
						updDetailJson.put(DOC_TYPE, "ACCT");
						updDetailJson.put(TRAN_CD, updateDataJson.getString(TRAN_CD));
						updDetailJson.put(TEMPLATE_CD, updateDataJson.getString(TEMPLATE_CD));
						updDetailJson.put(VALID_UPTO, updateDataJson.getString(VALID_UPTO));
						updDetailJson.put(SUBMIT, updateDataJson.getString(SUBMIT));
						updDetailJson.put(SR_CD, updateDataJson.getString(SR_CD));
						updDetailJson.put("ACTION", "UPD");
						updDetailJson.put(REQ_FLAG, la_reqFlag);
						updDetailJson.put(CONFIRMED, "Y");
						updDetailJson.put(COMP_CD, la_compCd);
						updDetailJson.put(CUSTOMER_ID, la_custId);
						updDetailJson.put(BRANCH_CD, la_branchCd);
						updDetailJson.put(ENT_COMP_CD, la_compCd);
						updDetailJson.put(ENT_BRANCH_CD, la_branchCd);

						ls_detail_response = InsertData(connection, updDetailJson, userJson, 2, "","REQ_ACCT_MST_DOC_TEMPLATE");

						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_detail_response);
						if (!isSuccessStCode(insertResJson.getString(STATUS))) {
							return ls_detail_response;
						}

						if (la_reqFlag.equals("E")) 
						{
							// INSERT DATA in audit table.
							ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, updateDataJson,
									userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE", "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT",
									la_reqCd, "M", "UPD", ll_ENTRY_TYPE);

							JSONObjectImpl auditResJson = common.ofGetJsonObject(ls_detail_response);
							
							if (!isSuccessStCode(auditResJson.getString(STATUS)))
							{
								return ls_detail_response;
							}
						}
						// 1) insetrt ,update , delete detail data(doc_image , valid_upto) in both table
						// update data in req_doc_template_dtl_sdt table.

						if (li_insLength != 0)
						{

							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++)
							{
								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}

							if (la_reqFlag.equals("E")) 
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
									return ls_detail_response;
								}
							}
						}	
						
						
						if (la_checkIsFromReq.equals("N"))
						{
							// ADD THE UPDATED DATA IN REQ_ACCT_MST_DOC_TEMPLATE TABLE.
							if (li_updLength != 0) 
							{
								updAudJlist = new JSONArrayImpl();
								for (int j = 0; j < li_updLength; j++)
								{
									updateDataJson = new JSONObjectImpl();
									updDetailJson = new JSONObjectImpl();

									updateDataJson = updateDtlJlist.getJSONObject(j);
									oldValueJson = updateDataJson.getJSONObject("_OLDROWVALUE");
									updValueJlist = updateDataJson.getJSONArray("_UPDATEDCOLUMNS");

									for (int i = 0; i < updValueJlist.length(); i++)
									{
										ls_upd_column = updValueJlist.getString(i);

										if (oldValueJson.has(ls_upd_column)) {
											ls_new_value = updateDataJson.getString(ls_upd_column);
											updDetailJson.put(ls_upd_column, ls_new_value);
										}
									}												
									updDetailJson.put(TRAN_CD, updateDataJson.getString(TRAN_CD));
									updDetailJson.put(SR_CD, updateDataJson.getString(SR_CD));		
									updDetailJson.put("PAGE_NO",updateDataJson.getString("PAGE_NO"));
									updDetailJson.put(CONFIRMED, "N");
									updDetailJson.put(ACTIVE, "Y");
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, la_reqFlag);
									updDetailJson.put(DOC_TYPE, "ACCT");
									updDetailJson.put(COMP_CD, la_compCd);
									updDetailJson.put(CUSTOMER_ID, la_custId);
									updDetailJson.put(BRANCH_CD, la_branchCd);
									updDetailJson.put(REQ_CD, la_reqCd);
									updDetailJson.put(ACCT_TYPE, la_acctType);
									updDetailJson.put(ACCT_CD, la_acctCd);
									updDetailJson.put(ENT_COMP_CD, la_compCd);
									updDetailJson.put(ENT_BRANCH_CD, la_branchCd);
									updAudJlist.put(updDetailJson);
								}
								ls_detail_response = insertDTLData(connection, updAudJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateResJson = common.ofGetJsonObject(ls_detail_response);

								if (!isSuccessStCode(updateResJson.getString(STATUS))) 
								{
									return ls_detail_response;
								}


								if (la_reqFlag.equals("E")) 
								{
									// INSERT DATA in audit table.
									ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
											userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
											"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "UPD", ll_ENTRY_TYPE);

									JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
									if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
										return ls_detail_response;
									}
								}
							}
						} else
						{
							// UPDATE DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							if (li_updLength != 0)
							{
								dtlJson = new JSONObjectImpl();
								dtlJson.put("isDeleteRow", new JSONArrayImpl());
								dtlJson.put("isNewRow", new JSONArrayImpl());
								dtlJson.put("isUpdatedRow", updateDtlJlist);

								// update data in REQ_ACCT_MST_DOC_TEMPLATE_SDT;
								ls_detail_response = UpdateDetailsData(connection, dtlJson, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateDtlResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateDtlResJson.getString(STATUS))) {
									return ls_detail_response;
								}
								/*
								 * if (li_delLength != 0) { jPrimaryDtl.put("REQ_CD", la_reqCd); int delRowCount
								 * = DeleteDTLData(connection, deleteJlist, userJson,
								 * jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT", jallDtlColumnList);
								 * 
								 * if (delRowCount == 0) { return common.ofGetResponseJson(new JSONArrayImpl(),
								 * "", "Data Deletion Fail...", ST999, "G", "Data Deletion Fail...").toString();
								 * }
								 * 
								 * return common .ofGetResponseJson(new JSONArrayImpl().put(jPrimaryDtl), "",
								 * "", ST0, "G", "") .toString(); }
								 */

								if (la_reqFlag.equals("E")) 
								{
									// INSERT DATA in audit table.
									ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
											userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
											"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "UPD", ll_ENTRY_TYPE);

									JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
									if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
										return ls_detail_response;
									}
								}
							}
						}
						
					} else {
						// 1) insetrt ,update , delete detail data(doc_image , valid_upto) in both table
						// update data in req_doc_template_dtl_sdt table.

						if (li_insLength != 0)
						{

							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++)
							{

								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}

							if (la_reqFlag.equals("E"))
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
									return ls_detail_response;
								}
							}
						}

					}
				} else
				{
					if (lb_checkFirst)
					{
						// 1)update main detail only update data in req_doc_template_dtl
						// table.-----ACTION_TYPE = M AND ACTION = UPD

						documentJson.put("REQ_CD", la_reqCd);
						
						ls_detail_response = UpdateData(connection, documentJson, userJson,"REQ_ACCT_MST_DOC_TEMPLATE");

						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_detail_response);
						if (!isSuccessStCode(insertResJson.getString(STATUS))) {
							return ls_detail_response;
						}

						if (la_reqFlag.equals("E")) 
						{
							// INSERT DATA in audit table.
							ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
									userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE", "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT",
									la_reqCd, "M", "UPD", ll_ENTRY_TYPE);

							JSONObjectImpl auditResJson = common.ofGetJsonObject(ls_detail_response);
							
							if (!isSuccessStCode(auditResJson.getString(STATUS)))
							{
								return ls_detail_response;
							}
						}

						// 1) insetrt ,update , delete detail data(doc_image , valid_upto) in both table
						// update data in req_doc_template_dtl_sdt table.

						if (li_insLength != 0)
						{

							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++)
							{

								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}

							if (la_reqFlag.equals("E")) 
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
									return ls_detail_response;
								}
							}
						}
						// CHECK NEW FLAG IF Y THEN DATA ALREADY IS IN REQUEST TABLE ELSE IN MAIN TABLE.

						if (la_checkIsFromReq.equals("N")) 
						{
							// ADD THE UPDATED DATA IN REQ_ACCT_MST_DOC_TEMPLATE TABLE.
							if (li_updLength != 0)
							{
								updateDtlJlist = new JSONArrayImpl();
								for (int j = 0; j < li_updLength; j++) {

									updateDataJson = new JSONObjectImpl();

									updateDataJson = updateDtlJlist.getJSONObject(j);
									oldValueJson = updateDataJson.getJSONObject("_OLDROWVALUE");
									updValueJlist = updateDataJson.getJSONArray("_UPDATEDCOLUMNS");
									updDetailJson = new JSONObjectImpl();

									for (int i = 0; i < updValueJlist.length(); i++) {

										ls_upd_column = updValueJlist.getString(i);

										if (oldValueJson.has(ls_upd_column)) {
											ls_new_value = updateDataJson.getString(ls_upd_column);
											updDetailJson.put(ls_upd_column, ls_new_value);
										}
									}
									updDetailJson.put(REQ_CD, la_reqCd);
									updDetailJson.put(ACCT_TYPE, la_acctType);
									updDetailJson.put(ACCT_CD, la_acctCd);
									updDetailJson.put(ACTIVE, "Y");
									updDetailJson.put(DOC_TYPE, "ACCT");
									updDetailJson.put(TRAN_CD, updateDataJson.getString(TRAN_CD));
									updDetailJson.put(SR_CD, updateDataJson.getString(SR_CD));
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, la_reqFlag);
									updDetailJson.put(CONFIRMED, "N");
									updDetailJson.put(COMP_CD, la_compCd);
									updDetailJson.put(CUSTOMER_ID, la_custId);
									updDetailJson.put(BRANCH_CD, la_branchCd);
									updDetailJson.put(ENT_COMP_CD, la_compCd);
									updDetailJson.put(ENT_BRANCH_CD, la_branchCd);
									updateDtlJlist.put(updDetailJson);
								}
								ls_detail_response = insertDTLData(connection, updateDtlJlist, userJson, jPrimaryDtl,
										"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateResJson.getString(STATUS))) {
									return ls_detail_response;
								}

							}
						} else {
							// UPDATE DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							if (li_updLength != 0)
							{
								dtlJson = new JSONObjectImpl();
								dtlJson.put("isDeleteRow", new JSONArrayImpl());
								dtlJson.put("isNewRow", new JSONArrayImpl());
								dtlJson.put("isUpdatedRow", updateDtlJlist);

								// update data in REQ_ACCT_MST_DOC_TEMPLATE_SDT;
								ls_detail_response = UpdateDetailsData(connection, dtlJson, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateDtlResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateDtlResJson.getString(STATUS))) {
									return ls_detail_response;
								}

							}
						}
						if (li_delLength != 0) 
						{
							jPrimaryDtl.put("REQ_CD", la_reqCd);
							int delRowCount = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT", jallDtlColumnList);

							if (delRowCount == 0) 
							{
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Data Deletion Fail...", ST999,
										"G", "Data Deletion Fail...").toString();
							}
							return common.ofGetResponseJson(new JSONArrayImpl(), "", "", ST0, "G", "").toString();
						}

					} else {
						// 1) insetr ,update , delete detail data(doc_image , valid_upto) in table
						// update data in req_doc_template_dtl_sdt table.

						// insert data with max line cd and update the existing which is in req with
						// existing trancd and sr cd and delete.
						if (li_insLength != 0) 
						{
							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++) 
							{

								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}

							if (la_reqFlag.equals("E"))
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								
								if (!isSuccessStCode(insAuditResJson.getString(STATUS))) 
								{
									return ls_detail_response;
								}
							}

						}
						// CHECK NEW FLAG IF Y THEN DATA ALREADY IS IN REQUEST TABLE ELSE IN MAIN TABLE.

						if (la_checkIsFromReq.equals("N"))
						{
							// ADD THE UPDATED DATA IN REQ_ACCT_MST_DOC_TEMPLATE TABLE.
							if (li_updLength != 0) {
								updateDtlJlist = new JSONArrayImpl();
								for (int j = 0; j < li_updLength; j++) {

									updateDataJson = new JSONObjectImpl();

									updateDataJson = updateDtlJlist.getJSONObject(j);
									oldValueJson = updateDataJson.getJSONObject("_OLDROWVALUE");
									updValueJlist = updateDataJson.getJSONArray("_UPDATEDCOLUMNS");
									updDetailJson = new JSONObjectImpl();

									for (int i = 0; i < updValueJlist.length(); i++) {

										ls_upd_column = updValueJlist.getString(i);

										if (oldValueJson.has(ls_upd_column)) {
											ls_new_value = updateDataJson.getString(ls_upd_column);
											updDetailJson.put(ls_upd_column, ls_new_value);
										}
									}
									updDetailJson.put(REQ_CD, la_reqCd);
									updDetailJson.put(ACCT_TYPE, la_acctType);
									updDetailJson.put(ACCT_CD, la_acctCd);
									updDetailJson.put(ACTIVE, "Y");
									updDetailJson.put(DOC_TYPE, "ACCT");
									updDetailJson.put(TRAN_CD, updateDataJson.getString(TRAN_CD));
									updDetailJson.put(SR_CD, updateDataJson.getString(SR_CD));
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, la_reqFlag);
									updDetailJson.put(CONFIRMED, "N");
									updDetailJson.put(COMP_CD, la_compCd);
									updDetailJson.put(CUSTOMER_ID, la_custId);
									updDetailJson.put(BRANCH_CD, la_branchCd);
									updDetailJson.put(ENT_COMP_CD, la_compCd);
									updDetailJson.put(ENT_BRANCH_CD, la_branchCd);
									updateDtlJlist.put(updDetailJson);
								}
								ls_detail_response = insertDTLData(connection, updateDtlJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateResJson.getString(STATUS))) {
									return ls_detail_response;
								}

							}
						} else
						{
							// UPDATE DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							if (li_updLength != 0)
							{
								dtlJson = new JSONObjectImpl();
								dtlJson.put("isDeleteRow", new JSONArrayImpl());
								dtlJson.put("isNewRow", new JSONArrayImpl());
								dtlJson.put("isUpdatedRow", updateDtlJlist);

								// update data in REQ_ACCT_MST_DOC_TEMPLATE_SDT;
								ls_detail_response = UpdateDetailsData(connection, dtlJson, userJson, jPrimaryDtl,
										"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateDtlResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateDtlResJson.getString(STATUS))) {
									return ls_detail_response;

								}
							}
							if (li_delLength != 0) 
							{
								jPrimaryDtl.put("REQ_CD", la_reqCd);
								int delRowCount = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT", jallDtlColumnList);

								if (delRowCount == 0) 
								{
									return common.ofGetResponseJson(new JSONArrayImpl(), "", "Data Deletion Fail...",
											ST999, "G", "Data Deletion Fail...").toString();
								}

								return common
										.ofGetResponseJson(new JSONArrayImpl().put(jPrimaryDtl), "", "", ST0, "G", "")
										.toString();
							}
						}
					}
				}
				
			} else if (la_checkAction.equals("DEL"))
			{
				if (la_checkIsFromMain.equals("Y")) 
				{

					if (lb_checkFirst)
					{

						// INSERT DATA in audit table.
						documentJson.put(CUSTOMER_ID, la_custId);
						
						ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson, userJson,
								"EASY_BANK.ACCT_MST_DOC_TEMPLATE", "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "M",
								"DEL", ll_ENTRY_TYPE);

						JSONObjectImpl auditResJson = common.ofGetJsonObject(ls_detail_response);
						
						if (!isSuccessStCode(auditResJson.getString(STATUS))) 
						{
							return ls_detail_response;
						}
					} else {
						if (li_insLength != 0) 
						{

							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++) {

								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}
							if (la_reqFlag.equals("E"))
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(insAuditResJson.getString(STATUS)))
								{
									return ls_detail_response;
								}
							}
						}
					}
				} else {
					if (lb_checkFirst) {

						// DELETE DATA FROM REQUEST TABLE.
						ls_detail_response = DeleteData(connection, documentJson, userJson,
								"REQ_ACCT_MST_DOC_TEMPLATE");
						JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
						if (!isSuccessStCode(insAuditResJson.getString(STATUS))) {
							return ls_detail_response;
						}
					} else {
						if (li_insLength != 0) {

							insDtlJlist = new JSONArrayImpl();
							for (int j = 0; j < li_insLength; j++) {

								insertDetailJson = insertJlist.getJSONObject(j);
								insertDetailJson.put(REQ_CD, la_reqCd);
								insertDetailJson.put(REQ_FLAG, la_reqFlag);
								insertDetailJson.put(ACCT_TYPE, la_acctType);
								insertDetailJson.put(ACCT_CD, la_acctCd);
								insertDetailJson.put(ACTIVE, "Y");
								insertDetailJson.put(DOC_TYPE, "ACCT");
								insertDetailJson.put(CONFIRMED, "Y");
								insertDetailJson.put(TRAN_CD, documentJson.getString(TRAN_CD));
								insertDetailJson.put(SR_CD, documentJson.getString(SR_CD));
								insertDetailJson.put(COMP_CD, la_compCd);
								insertDetailJson.put("REF_CUSTOMER_ID", la_custId);
								insertDetailJson.put("REF_LINE_CD", "0");
								insertDetailJson.put("REF_SR_CD", "0");
								insertDetailJson.put(BRANCH_CD, la_branchCd);
								insertDetailJson.put(ENT_COMP_CD, la_compCd);
								insertDetailJson.put(ENT_BRANCH_CD, la_branchCd);
								insertDetailJson.put("LINE_CD", ll_dtlSrCd);

								insDtlJlist.put(insertDetailJson);
								ll_dtlSrCd++;
							}
							ls_detail_response = insertDTLData(connection, insDtlJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

							JSONObjectImpl insDetailResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(insDetailResJson.getString(STATUS))) {
								return ls_detail_response;
							}
							if (la_reqFlag.equals("E"))
							{
								// INSERT DATA in audit table.
								ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
										userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE",
										"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT", la_reqCd, "D", "ADD", ll_ENTRY_TYPE);

								JSONObjectImpl insAuditResJson = common.ofGetJsonObject(ls_detail_response);
								
								if (!isSuccessStCode(insAuditResJson.getString(STATUS))) 
								{
									return ls_detail_response;
								}
							}

						}

						// CHECK NEW FLAG IF Y THEN DATA ALREADY IS IN REQUEST TABLE ELSE IN MAIN TABLE.

						if (la_checkIsFromReq.equals("N"))
						{
							// ADD THE UPDATED DATA IN REQ_ACCT_MST_DOC_TEMPLATE TABLE.
							if (li_updLength != 0) {
								updateDtlJlist = new JSONArrayImpl();
								for (int j = 0; j < li_updLength; j++)
								{
									updateDataJson = new JSONObjectImpl();

									updateDataJson = updateDtlJlist.getJSONObject(j);
									oldValueJson = updateDataJson.getJSONObject("_OLDROWVALUE");
									updValueJlist = updateDataJson.getJSONArray("_UPDATEDCOLUMNS");
									updDetailJson = new JSONObjectImpl();

									for (int i = 0; i < updValueJlist.length(); i++) {

										ls_upd_column = updValueJlist.getString(i);

										if (oldValueJson.has(ls_upd_column)) {
											ls_new_value = updateDataJson.getString(ls_upd_column);
											updDetailJson.put(ls_upd_column, ls_new_value);
										}
									}
									updDetailJson.put(REQ_CD, la_reqCd);
									updDetailJson.put(ACCT_TYPE, la_acctType);
									updDetailJson.put(ACCT_CD, la_acctCd);
									updDetailJson.put(ACTIVE, "Y");
									updDetailJson.put(DOC_TYPE, "ACCT");
									updDetailJson.put(TRAN_CD, updateDataJson.getString(TRAN_CD));
									updDetailJson.put(SR_CD, updateDataJson.getString(SR_CD));
									updDetailJson.put("ACTION", "UPD");
									updDetailJson.put(REQ_FLAG, la_reqFlag);
									updDetailJson.put(CONFIRMED, "N");
									updDetailJson.put(COMP_CD, la_compCd);
									updDetailJson.put(CUSTOMER_ID, la_custId);
									updDetailJson.put(BRANCH_CD, la_branchCd);
									updDetailJson.put(ENT_COMP_CD, la_compCd);
									updDetailJson.put(ENT_BRANCH_CD, la_branchCd);
									updateDtlJlist.put(updDetailJson);
								}
								ls_detail_response = insertDTLData(connection, updateDtlJlist, userJson, jPrimaryDtl,
										"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateResJson.getString(STATUS))) {
									return ls_detail_response;
								}

							}
						} else {
							// UPDATE DATA IN REQ_ACCT_MST_DOC_TEMPLATE_SDT TABLE.
							if (li_updLength != 0) 
							{
								dtlJson = new JSONObjectImpl();
								dtlJson.put("isDeleteRow", new JSONArrayImpl());
								dtlJson.put("isNewRow", new JSONArrayImpl());
								dtlJson.put("isUpdatedRow", updateDtlJlist);

								// update data in REQ_ACCT_MST_DOC_TEMPLATE_SDT;
								ls_detail_response = UpdateDetailsData(connection, dtlJson, userJson, jPrimaryDtl,
										"REQ_ACCT_MST_DOC_TEMPLATE_SDT");

								JSONObjectImpl updateDtlResJson = common.ofGetJsonObject(ls_detail_response);
								if (!isSuccessStCode(updateDtlResJson.getString(STATUS))) {
									return ls_detail_response;
								}

							}
						}
						if (li_delLength != 0)
						{
							jPrimaryDtl.put("REQ_CD", la_reqCd);
							int delRowCount = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,
									"REQ_ACCT_MST_DOC_TEMPLATE_SDT", jallDtlColumnList);

							if (delRowCount == 0) {
								return common.ofGetResponseJson(new JSONArrayImpl(), "", "Data Deletion Fail...", ST999,
										"G", "Data Deletion Fail...").toString();
							}
							// INSERT DATA in audit table.
							ls_detail_response = ofGetInsertAccountDocAuditData(connection, reqJson, documentJson,
									userJson, "EASY_BANK.ACCT_MST_DOC_TEMPLATE", "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT",
									la_reqCd, "D", "DEL", ll_ENTRY_TYPE);

							JSONObjectImpl updAuditResJson = common.ofGetJsonObject(ls_detail_response);
							if (!isSuccessStCode(updAuditResJson.getString(STATUS))) {
								return ls_detail_response;
							}

						}
					}
				}
			}

		} catch (Exception exception) {
 			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetCheckDocumentDetailData", "(ENP330)");
		}
		return ls_detail_response;
	}

	public String ofGetInsertAcctDocAuditData(Connection connection, JSONObjectImpl detailDataJson,
			JSONObjectImpl requestDataJson, JSONObjectImpl userJson, String la_mstTablename, String la_reqTablename) {

		JSONArrayImpl jallColumnList = new JSONArrayImpl();
		JSONArrayImpl detailJlist = new JSONArrayImpl();
		JSONArrayImpl updateJlist = new JSONArrayImpl();
		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl updValueJlist = new JSONArrayImpl();

		JSONObjectImpl oldValueJson = new JSONObjectImpl();
		JSONObjectImpl jallColumnJson = new JSONObjectImpl();
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
		JSONObjectImpl detailJson = new JSONObjectImpl();
		JSONObjectImpl mainDtlJson = new JSONObjectImpl();
		JSONObjectImpl insertDtlJson = new JSONObjectImpl();
		JSONObjectImpl deleteDtlJson = new JSONObjectImpl();
		JSONObjectImpl updateDtlJson = new JSONObjectImpl();

		String ls_custType = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_compCd = StringUtils.EMPTY;
		String ls_acct_compCd = StringUtils.EMPTY;
		String ls_acct_branchCd = StringUtils.EMPTY;
		String ls_reqCd = StringUtils.EMPTY;
		String ls_columnNm = StringUtils.EMPTY;
		String ls_columnType = StringUtils.EMPTY;
		String ls_isNew = StringUtils.EMPTY;
		String ls_reqTableName = StringUtils.EMPTY;
		String ls_auditResponse = StringUtils.EMPTY;
		String ls_newValue = StringUtils.EMPTY;
		String ls_oldValue = StringUtils.EMPTY;
		String ls_updSrCd = StringUtils.EMPTY;
		String ls_updColumn = StringUtils.EMPTY;
		String  li_updSrCd = StringUtils.EMPTY;

		int li_sr_cd;
		int li_insertLength=0;
		int li_updateLength=0;
		int li_deleteLength=0;

		//		long ll_MaxTrancd = 0L;
		long ll_SrCd = 1L;
		long ll_ENTRY_TYPE = 1L;
		boolean lb_isNew;
		boolean lb_isCheckFirst = true;

		String ls_db_resp = StringUtils.EMPTY;
		JSONObjectImpl responseDataJson;
		String ls_status = StringUtils.EMPTY;
		String ls_New_entry_type = StringUtils.EMPTY;

		try {
			ls_acct_compCd = requestDataJson.getString(COMP_CD);
			ls_acct_branchCd = requestDataJson.getString(BRANCH_CD);
			ls_custType = requestDataJson.getString(REQ_FLAG);
			ls_acctType = requestDataJson.getString(ACCT_TYPE);
			ls_acctCd = requestDataJson.getString(ACCT_CD);
			ls_reqCd = requestDataJson.getString(REQ_CD);
			lb_isNew = requestDataJson.getBoolean("IsNewRow");
			ls_compCd = getRequestUniqueData.getCompCode();

			updateJlist = detailDataJson.getJSONArray("isUpdatedRow");
			insertJlist = detailDataJson.getJSONArray("isNewRow");
			deleteJlist = detailDataJson.getJSONArray("isDeleteRow");
			li_insertLength = insertJlist.length();
			li_updateLength = updateJlist.length();
			li_deleteLength = deleteJlist.length();
			
			jPrimaryDtl.put(REQ_CD, ls_reqCd);

			jallColumnList = getColumnDefination("REQ_ACCT_MST_UPD_DTL", connection, true);
			ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_UPD_DTL", SR_CD, jallColumnList, jPrimaryDtl);

			ll_SrCd = ll_SrCd + 1;

			if (lb_isCheckFirst) {
				ll_ENTRY_TYPE = getMaxSrCd(connection, "REQ_ACCT_MST_UPD_DTL", ENTRY_TYPE, jallColumnList, jPrimaryDtl);
				ll_ENTRY_TYPE = ll_ENTRY_TYPE + 1;
				lb_isCheckFirst = false;
			}

			if (ls_custType.equals("E") && li_updateLength != 0)
			{
				detailJlist = new JSONArrayImpl();
				for (int i = 0; i < li_updateLength; i++)
				{
					updateDtlJson = updateJlist.getJSONObject(i);
					oldValueJson = updateDtlJson.getJSONObject("_OLDROWVALUE");
					updValueJlist = updateDtlJson.getJSONArray("_UPDATEDCOLUMNS");

					for (int j = 0; j < updValueJlist.length(); j++)
					{
						ls_updColumn = updValueJlist.getString(j);
						detailJson = new JSONObjectImpl();

						if (oldValueJson.has(ls_updColumn)) 
						{
							ls_oldValue = oldValueJson.getString(ls_updColumn);
							ls_newValue = updateDtlJson.getString(ls_updColumn);
							ls_columnType = ofGetColumnDataType(connection, la_reqTablename, ls_updColumn);

							detailJson.put(COLUMN_NAME, ls_updColumn);
							detailJson.put(ENTRY_TYPE, ll_ENTRY_TYPE);
							detailJson.put(COMP_CD, ls_acct_compCd);
							detailJson.put(BRANCH_CD,ls_acct_branchCd);
							detailJson.put("NEW_VALUE", ls_newValue);
							detailJson.put("OLD_VALUE", ls_oldValue);
							detailJson.put("OWNER_NAME", "EASY_BANK");
							detailJson.put(ACCT_TYPE, ls_acctType);
							detailJson.put(ACCT_CD, ls_acctCd);
							detailJson.put("REF_SR_CD",updateDtlJson.getString(SR_CD));
							
							if(la_mstTablename.equals("EASY_BANK.JOINT_ACCOUNT_MST") || la_mstTablename.equals("EASY_BANK.JOINT_ACCOUNT_MST_OTHER")  )
								detailJson.put("REF_J_TYPE", updateDtlJson.getString(J_TYPE));
						
							detailJson.put(REQ_CD, ls_reqCd);
							detailJson.put("ACTION", "UPD");
							detailJson.put(COLUMN_TYPE, ls_columnType);
							detailJson.put(SR_CD, ll_SrCd);
							detailJson.put("TABLE_NAME", la_mstTablename);
							detailJlist.put(detailJson);
							ll_SrCd++;
						}
					}
				}
				mainDtlJson.put("isNewRow", detailJlist);
				mainDtlJson.put("isDeleteRow", new JSONArrayImpl());
				mainDtlJson.put("isUpdatedRow", new JSONArrayImpl());

				ls_auditResponse = UpdateDetailsData(connection, mainDtlJson, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");
			}
			if (ls_custType.equals("E") && li_deleteLength != 0)
             {
				detailJlist = new JSONArrayImpl();
				for (int i = 0; i < li_deleteLength; i++)
				{
					deleteDtlJson = deleteJlist.getJSONObject(i);
					detailJson = new JSONObjectImpl();
					
					ls_updSrCd = deleteDtlJson.getString(SR_CD);
					jPrimaryDtl.put(REQ_CD, ls_reqCd);				
					
					detailJson.put(COLUMN_NAME, "Deleted");
					detailJson.put(ENTRY_TYPE, ll_ENTRY_TYPE);
					detailJson.put(COMP_CD,ls_acct_compCd);
					detailJson.put(BRANCH_CD,ls_acct_branchCd);
					detailJson.put("NEW_VALUE", "");
					detailJson.put("OLD_VALUE", "");
					detailJson.put("OWNER_NAME", "EASY_BANK");
					detailJson.put(ACCT_TYPE, ls_acctType);
					detailJson.put(ACCT_CD, ls_acctCd);
					detailJson.put("REF_SR_CD", ls_updSrCd);
					
					if(la_mstTablename.equals("EASY_BANK.JOINT_ACCOUNT_MST"))
						detailJson.put("REF_J_TYPE", deleteDtlJson.getString(J_TYPE));
					
					detailJson.put(REQ_CD, ls_reqCd);
					detailJson.put("ACTION", "DEL");
					detailJson.put(COLUMN_TYPE, "");
					detailJson.put(SR_CD, ll_SrCd);
					detailJson.put("TABLE_NAME", la_mstTablename);
					detailJlist.put(detailJson);
					ll_SrCd++;
				}

				mainDtlJson.put("isNewRow", detailJlist);
				mainDtlJson.put("isDeleteRow", new JSONArrayImpl());
				mainDtlJson.put("isUpdatedRow", new JSONArrayImpl());

				ls_auditResponse = UpdateDetailsData(connection, mainDtlJson, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

			}
			if (ls_custType.equals("E") && li_insertLength != 0) 
			{			
				detailJlist = new JSONArrayImpl();
				li_updSrCd = li_updSrCd + 1;

				for (int i = 0; i < li_insertLength; i++) 
				{
					insertDtlJson = insertJlist.getJSONObject(i);

					Set<String> columnNameSet = insertDtlJson.keySet();		
					String columnName = columnNameSet.iterator().next(); 
					li_updSrCd = insertDtlJson.getString(SR_CD);

					//for (String columnName : columnNameSet) {
					ls_columnType = ofGetColumnDataType(connection, la_reqTablename, columnName);

					jPrimaryDtl.put(REQ_CD, ls_reqCd);
					detailJson = new JSONObjectImpl();

					detailJson.put(COLUMN_NAME, "New Inserted");
					detailJson.put(ENTRY_TYPE, ll_ENTRY_TYPE);
					detailJson.put(COMP_CD,ls_acct_compCd);
					detailJson.put(BRANCH_CD, ls_acct_branchCd);
					detailJson.put("NEW_VALUE","");
					detailJson.put("OLD_VALUE", "");
					detailJson.put("OWNER_NAME", "EASY_BANK");
					detailJson.put(ACCT_TYPE, ls_acctType);
					detailJson.put(ACCT_CD, ls_acctCd);
					detailJson.put("REF_SR_CD", li_updSrCd);	
					
					if(la_mstTablename.equals("EASY_BANK.JOINT_ACCOUNT_MST") || la_mstTablename.equals("EASY_BANK.JOINT_ACCOUNT_MST_OTHER")  )
						detailJson.put("REF_J_TYPE", insertDtlJson.getString(J_TYPE));
					
					detailJson.put(REQ_CD, ls_reqCd);
					detailJson.put("ACTION", "ADD");
					detailJson.put(COLUMN_TYPE, ls_columnType);
					detailJson.put(SR_CD, ll_SrCd);
					detailJson.put("TABLE_NAME", la_mstTablename);
					detailJlist.put(detailJson);		
					ll_SrCd++;
					//}
					//li_updSrCd++;
				}
				insertJlist = new JSONArrayImpl();
				//insertJlist.put(detailJlist);
				mainDtlJson.put("isNewRow", insertJlist.put(detailJlist.getJSONObject(0)));  //insert only one  record
				//mainDtlJson.put("isNewRow", detailJlist);
				mainDtlJson.put("isDeleteRow", new JSONArrayImpl());
				mainDtlJson.put("isUpdatedRow", new JSONArrayImpl());

				ls_auditResponse = UpdateDetailsData(connection, mainDtlJson, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

			}
			return ls_auditResponse;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAcctDocAuditData", "(ENP329)");
		}

	}

	public String ofGetInsertDocumentAuditData(Connection connection, JSONObjectImpl reqJson,
			JSONObjectImpl documentJson, JSONObjectImpl userJson, String la_mst_tablename, String la_req_tablename,
			String la_req_cd, String la_is_from_main) {
		LoggerImpl loggerImpl = null;
		String ls_column_type = StringUtils.EMPTY;
		String ls_value = StringUtils.EMPTY;
		String ls_customer_id = StringUtils.EMPTY;
		String ls_cust_type = StringUtils.EMPTY;
		String ls_old_value = StringUtils.EMPTY;
		String ls_new_value = StringUtils.EMPTY;
		String ls_upd_column = StringUtils.EMPTY;
		String ls_entry_type = StringUtils.EMPTY;
		String ls_audit_response = StringUtils.EMPTY;

		int li_sr_cd;

		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl insertAuditJlist = new JSONArrayImpl();
		JSONArrayImpl updateAuditJlist = new JSONArrayImpl();
		JSONArrayImpl deleteAuditJlist = new JSONArrayImpl();
		JSONArrayImpl updateJlist = new JSONArrayImpl();
		JSONArrayImpl updValueJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl responseJlist = new JSONArrayImpl();

		JSONObjectImpl insertJson = new JSONObjectImpl();
		JSONObjectImpl auditResJson = new JSONObjectImpl();
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
		JSONObjectImpl mainDtlJson = new JSONObjectImpl();
		JSONObjectImpl oldValueJson = new JSONObjectImpl();
		JSONObjectImpl insertAuditJson = new JSONObjectImpl();
		JSONObjectImpl updateJson = new JSONObjectImpl();
		JSONObjectImpl updateAuditJson = new JSONObjectImpl();
		JSONObjectImpl deleteJson = new JSONObjectImpl();
		JSONObjectImpl deleteAuditJson = new JSONObjectImpl();

		Set<String> insertJsonKeys;

		try {

			insertJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isNewRow");
			updateJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isUpdatedRow");
			deleteJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isDeleteRow");

			int ins_length = insertJlist.length();
			int upd_length = updateJlist.length();
			int del_length = deleteJlist.length();

			jPrimaryDtl.put(REQ_CD, la_req_cd);

			JSONArrayImpl jallColumnList = getColumnDefination(REQ_ACCT_MST_UPD_DTL, connection, true);
			long ll_entry_type = getMaxSrCd(connection, REQ_ACCT_MST_UPD_DTL, ENTRY_TYPE, jallColumnList, jPrimaryDtl);
			ll_entry_type = ll_entry_type + 1;

			long ll_SrCd = getMaxSrCd(connection, REQ_ACCT_MST_UPD_DTL, SR_CD, jallColumnList, jPrimaryDtl);
			ll_SrCd = ll_SrCd + 1;

			if (ins_length != 0) {

				for (int i = 0; i < ins_length; i++) {

					insertJson = new JSONObjectImpl();

					insertJson = insertJlist.getJSONObject(i);
					insertJsonKeys = insertJson.keySet();

					for (String keyName : insertJsonKeys) {

						// get column type .
						insertAuditJson = new JSONObjectImpl();
						ls_column_type = ofGetColumnDataType(connection, la_req_tablename, keyName);

						if (!keyName.equals("DOC_IMAGE")) {
							ls_value = insertJson.getString(keyName);
						} else {
							ls_value = " ";
						}
						insertAuditJson.put("OWNER_NAME", "EASY_BANK");
						insertAuditJson.put("TABLE_NAME", "EASY_BANK.ACCT_MST_DOC_TEMPLATE");
						insertAuditJson.put("REQ_CD", la_req_cd);
						insertAuditJson.put("ENTRY_TYPE", ll_entry_type);
						insertAuditJson.put("SR_CD", ll_SrCd);
						insertAuditJson.put("CUSTOMER_ID", insertJson.getString(CUSTOMER_ID));
						insertAuditJson.put("REF_SR_CD", insertJson.getString(SR_CD));
						insertAuditJson.put("OLD_VALUE", " ");
						insertAuditJson.put("COLUMN_TYPE", ls_column_type);
						insertAuditJson.put("ACTION", "ADD");
						insertAuditJson.put("TRAN_CD", insertJson.getString(TRAN_CD));
						insertAuditJson.put("NEW_VALUE", ls_value);
						insertAuditJson.put("COLUMN_NAME", keyName);

						insertAuditJlist.put(insertAuditJson);
						ll_SrCd++;
					}
				}
				jPrimaryDtl.put(REQ_CD, la_req_cd);
				ls_audit_response = insertDTLData(connection, insertAuditJlist, userJson, jPrimaryDtl,
						REQ_ACCT_MST_UPD_DTL);

				auditResJson = common.ofGetJsonObject(ls_audit_response);
				if (!isSuccessStCode(auditResJson.getString(STATUS))) {
					return ls_audit_response;
				}

			}
			if (upd_length != 0) {

				for (int i = 0; i < upd_length; i++) {

					updateJson = new JSONObjectImpl();

					updateJson = updateJlist.getJSONObject(i);
					oldValueJson = updateJson.getJSONObject("_OLDROWVALUE");
					updValueJlist = updateJson.getJSONArray("_UPDATEDCOLUMNS");
					ls_customer_id = reqJson.getString("CUSTOMER_ID");
					ls_cust_type = reqJson.getString(REQ_FLAG);

					for (int k = 0; k < updValueJlist.length(); k++) {

						updateAuditJson = new JSONObjectImpl();
						ls_upd_column = updValueJlist.getString(k);

						if (oldValueJson.has(ls_upd_column)) {

							if (!ls_upd_column.equals("DOC_IMAGE")) {
								ls_old_value = oldValueJson.getString(ls_upd_column);
								ls_new_value = updateJson.getString(ls_upd_column);
							} else {
								ls_old_value = " ";
								ls_new_value = " ";
							}
							ls_column_type = ofGetColumnDataType(connection, la_req_tablename, ls_upd_column);

							updateAuditJson.put(COLUMN_NAME, ls_upd_column);
							updateAuditJson.put(ENTRY_TYPE, ll_entry_type);
							updateAuditJson.put("NEW_VALUE", ls_new_value);
							updateAuditJson.put("OLD_VALUE", ls_old_value);
							updateAuditJson.put("OWNER_NAME", "EASY_BANK");
							updateAuditJson.put(CUSTOMER_ID, ls_customer_id);
							updateAuditJson.put("REF_SR_CD", updateJson.getString("SR_CD"));
							updateAuditJson.put("TRAN_CD", updateJson.getString(TRAN_CD));
							updateAuditJson.put(REQ_CD, la_req_cd);
							updateAuditJson.put("ACTION", "UPD");
							updateAuditJson.put(COLUMN_TYPE, ls_column_type);
							updateAuditJson.put(SR_CD, ll_SrCd);
							updateAuditJson.put("TABLE_NAME", la_mst_tablename);

							updateAuditJlist.put(updateAuditJson);
							ll_SrCd++;
						}
					}
				}
				jPrimaryDtl.put(REQ_CD, la_req_cd);
				ls_audit_response = insertDTLData(connection, updateAuditJlist, userJson, jPrimaryDtl,
						REQ_ACCT_MST_UPD_DTL);

				auditResJson = common.ofGetJsonObject(ls_audit_response);
				if (!isSuccessStCode(auditResJson.getString(STATUS))) {
					return ls_audit_response;
				}

			}
			if (del_length != 0) {

				for (int i = 0; i < del_length; i++) {

					deleteJson = new JSONObjectImpl();
					deleteAuditJson = new JSONObjectImpl();

					deleteJson = deleteJlist.getJSONObject(i);

					deleteAuditJson.put(COLUMN_NAME, "TEST");
					deleteAuditJson.put(ENTRY_TYPE, ll_entry_type);
					deleteAuditJson.put("NEW_VALUE", "");
					deleteAuditJson.put("OLD_VALUE", "");
					deleteAuditJson.put("OWNER_NAME", "EASY_BANK");
					deleteAuditJson.put(CUSTOMER_ID, ls_customer_id);
					deleteAuditJson.put("REF_SR_CD", deleteJson.getString("SR_CD"));
					deleteAuditJson.put("TRAN_CD", deleteJson.getString(TRAN_CD));
					deleteAuditJson.put(REQ_CD, la_req_cd);
					deleteAuditJson.put("ACTION", "DEL");
					deleteAuditJson.put(COLUMN_TYPE, "");
					deleteAuditJson.put(SR_CD, ll_SrCd);
					deleteAuditJson.put("TABLE_NAME", la_mst_tablename);

					deleteAuditJlist.put(deleteAuditJson);
					ll_SrCd++;
				}
				jPrimaryDtl.put(REQ_CD, la_req_cd);
				ls_audit_response = insertDTLData(connection, deleteAuditJlist, userJson, jPrimaryDtl,
						REQ_ACCT_MST_UPD_DTL);

				auditResJson = common.ofGetJsonObject(ls_audit_response);
				if (!isSuccessStCode(auditResJson.getString(STATUS))) {
					return ls_audit_response;
				}

			}

			// check response after perform dml operation on audit table.
			auditResJson = common.ofGetJsonObject(ls_audit_response);
			if (isSuccessStCode(auditResJson.getString(STATUS))) {
				connection.commit();
				return ls_audit_response;
			} else {
				connection.rollback();
				return ls_audit_response;
			}

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAuditData", "(ENP245)");
		}

	}

	public String ofGetInsertAccountDocAuditData(Connection connection, JSONObjectImpl reqJson,
			JSONObjectImpl documentJson, JSONObjectImpl userJson, String la_mst_tablename, String la_dtl_tablename,
			String la_req_cd, String la_action_type, String la_action, long ll_entry_type)
	{
		LoggerImpl loggerImpl = null;
		String ls_column_type = StringUtils.EMPTY;
		String ls_value = StringUtils.EMPTY;
		String ls_customer_id = StringUtils.EMPTY;
		String ls_cust_type = StringUtils.EMPTY;
		String ls_old_value = StringUtils.EMPTY;
		String ls_new_value = StringUtils.EMPTY;
		String ls_upd_column = StringUtils.EMPTY;
		String ls_entry_type = StringUtils.EMPTY;
		String ls_audit_response = StringUtils.EMPTY;
		String ls_insResponse = StringUtils.EMPTY;

		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl insertAuditJlist = new JSONArrayImpl();
		JSONArrayImpl updateAuditJlist = new JSONArrayImpl();
		JSONArrayImpl deleteAuditJlist = new JSONArrayImpl();
		JSONArrayImpl updateJlist = new JSONArrayImpl();
		JSONArrayImpl updValueJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONArrayImpl responseJlist = new JSONArrayImpl();
		JSONArrayImpl insertAuditDtlJlist = new JSONArrayImpl();
		JSONArrayImpl updateAuditDtlJlist = new JSONArrayImpl();
		JSONArrayImpl deleteAuditDtlJlist = new JSONArrayImpl();
		

		JSONObjectImpl insertJson = new JSONObjectImpl();
		JSONObjectImpl auditResJson = new JSONObjectImpl();
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
		JSONObjectImpl mainDtlJson = new JSONObjectImpl();
		JSONObjectImpl oldValueJson = new JSONObjectImpl();
		JSONObjectImpl insertAuditJson = new JSONObjectImpl();
		JSONObjectImpl updateJson = new JSONObjectImpl();
		JSONObjectImpl updateAuditJson = new JSONObjectImpl();
		JSONObjectImpl deleteJson = new JSONObjectImpl();
		JSONObjectImpl deleteAuditJson = new JSONObjectImpl();

		Set<String> insertJsonKeys;
		boolean checkKeys = true;
		try {

			insertJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isNewRow");
			updateJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isUpdatedRow");
			deleteJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isDeleteRow");

			int ins_length = insertJlist.length();
			int upd_length = updateJlist.length();
			int del_length = deleteJlist.length();

			jPrimaryDtl.put(REQ_CD, la_req_cd);
			
			JSONArrayImpl jallColumnList = getColumnDefination("REQ_ACCT_MST_UPD_DTL", connection, true);
			long ll_SrCd = getMaxSrCd(connection, "REQ_ACCT_MST_UPD_DTL", SR_CD, jallColumnList, jPrimaryDtl);
			
			ll_SrCd = ll_SrCd + 1;

			if ("M".equalsIgnoreCase(la_action_type))
			{
				if ("ADD".equalsIgnoreCase(la_action))
				{
					// insert main data in audit table.
					insertJson = documentJson;
					insertAuditJlist = new JSONArrayImpl();
					
					insertJsonKeys = insertJson.keySet();
					String keyName = insertJsonKeys.iterator().next(); 
					
					//for (String keyName : insertJsonKeys) 
					//{
					
						// get column type .
						insertAuditJson = new JSONObjectImpl();

						/*if (IS_MAIN_DATA_ADD.equals(keyName) || "DETAILS_DATA".equals(keyName)) 
						{
							checkKeys = false;
						}*/
						//if (checkKeys) 
						//{
							ls_value = insertJson.getString(keyName);
							ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE", keyName);

							insertAuditJson.put("OWNER_NAME", "EASY_BANK");
							insertAuditJson.put("TABLE_NAME", la_mst_tablename);
							insertAuditJson.put("REQ_CD", la_req_cd);
							insertAuditJson.put(COMP_CD, insertJson.getString(COMP_CD));
							insertAuditJson.put(BRANCH_CD, insertJson.getString(BRANCH_CD));
							insertAuditJson.put(ACCT_TYPE, insertJson.getString(ACCT_TYPE));
							insertAuditJson.put(ACCT_CD, insertJson.getString(ACCT_CD));
							insertAuditJson.put("ENTRY_TYPE", ll_entry_type);
							insertAuditJson.put("SR_CD", ll_SrCd);
							insertAuditJson.put("CUSTOMER_ID", reqJson.getString(CUSTOMER_ID));
							insertAuditJson.put("REF_SR_CD", insertJson.getString(SR_CD));
							insertAuditJson.put("OLD_VALUE", "");
							insertAuditJson.put("COLUMN_TYPE", ls_column_type);
							insertAuditJson.put("ACTION", la_action);
							insertAuditJson.put("TRAN_CD", insertJson.getString(TRAN_CD));
							insertAuditJson.put("NEW_VALUE","");
							insertAuditJson.put("COLUMN_NAME","New Inserted");

							insertAuditJlist.put(insertAuditJson);

							ll_SrCd++;
						//}
					//}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, insertAuditJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS)))
					{
						return ls_audit_response;
					}

				} else if ("UPD".equalsIgnoreCase(la_action))
				{
					// insert main updated data in audit table.
					updateJson = documentJson;
					updateAuditJlist = new JSONArrayImpl();
					oldValueJson = updateJson.getJSONObject("_OLDROWVALUE");
					updValueJlist = updateJson.getJSONArray("_UPDATEDCOLUMNS");
					ls_customer_id = reqJson.getString("CUSTOMER_ID");
					ls_cust_type = reqJson.getString(REQ_FLAG);

					for (int k = 0; k < updValueJlist.length(); k++) {

						updateAuditJson = new JSONObjectImpl();
						ls_upd_column = updValueJlist.getString(k);

						if (oldValueJson.has(ls_upd_column)) {

							ls_old_value = oldValueJson.getString(ls_upd_column);
							ls_new_value = updateJson.getString(ls_upd_column);
							ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE",
									ls_upd_column);

							updateAuditJson.put(COLUMN_NAME, ls_upd_column);
							updateAuditJson.put(ENTRY_TYPE, ll_entry_type);
							updateAuditJson.put("NEW_VALUE", ls_new_value);
							updateAuditJson.put("OLD_VALUE", ls_old_value);
							updateAuditJson.put("OWNER_NAME", "EASY_BANK");
							updateAuditJson.put(CUSTOMER_ID, ls_customer_id);
							updateAuditJson.put("REF_SR_CD", updateJson.getString("SR_CD"));
							updateAuditJson.put("TRAN_CD", updateJson.getString(TRAN_CD));
							updateAuditJson.put(REQ_CD, la_req_cd);
							updateAuditJson.put("ACTION", la_action);
							updateAuditJson.put(COLUMN_TYPE, ls_column_type);
							updateAuditJson.put(SR_CD, ll_SrCd);
							updateAuditJson.put("TABLE_NAME", la_mst_tablename);
							updateAuditJson.put(COMP_CD, reqJson.getString(COMP_CD));
							updateAuditJson.put(BRANCH_CD, reqJson.getString(BRANCH_CD));
							updateAuditJson.put(ACCT_TYPE, reqJson.getString(ACCT_TYPE));
							updateAuditJson.put(ACCT_CD, reqJson.getString(ACCT_CD));
							updateAuditJlist.put(updateAuditJson);
							ll_SrCd++;
						}
					}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, updateAuditJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) 
					{
						return ls_audit_response;
					}
				} else if ("DEL".equalsIgnoreCase(la_action))
				{
					// insert main delete request data in audit table.
					deleteJson = documentJson;
					deleteAuditJson = new JSONObjectImpl();

					deleteAuditJson.put(COLUMN_NAME, "Deleted");
					deleteAuditJson.put(ENTRY_TYPE, ll_entry_type);
					deleteAuditJson.put("NEW_VALUE", "");
					deleteAuditJson.put("OLD_VALUE", "");
					deleteAuditJson.put("OWNER_NAME", "EASY_BANK");
					deleteAuditJson.put(CUSTOMER_ID, deleteJson.getString(CUSTOMER_ID));
					deleteAuditJson.put("REF_SR_CD", deleteJson.getString(SR_CD));
					deleteAuditJson.put("TRAN_CD", deleteJson.getString(TRAN_CD));
					deleteAuditJson.put(REQ_CD, la_req_cd);
					deleteAuditJson.put("ACTION", "DEL");
					deleteAuditJson.put(COLUMN_TYPE, "NUMBER");
					deleteAuditJson.put(SR_CD, ll_SrCd);
					deleteAuditJson.put("TABLE_NAME", la_mst_tablename);
					deleteAuditJson.put(COMP_CD, reqJson.getString(COMP_CD));
					deleteAuditJson.put(BRANCH_CD, reqJson.getString(BRANCH_CD));
					deleteAuditJson.put(ACCT_TYPE, reqJson.getString(ACCT_TYPE));
					deleteAuditJson.put(ACCT_CD, reqJson.getString(ACCT_CD));

					ll_SrCd++;

					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = InsertData(connection, deleteAuditJson, userJson, 2, "","REQ_ACCT_MST_UPD_DTL");
					auditResJson = common.ofGetJsonObject(ls_audit_response);
					
					if (!isSuccessStCode(auditResJson.getString(STATUS))) 
					{
						return ls_audit_response;
					}
				} else {
					return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R",
							"common.invalid_req_data").toString();
				}
			} else if ("D".equalsIgnoreCase(la_action_type))
			{

				if (ins_length != 0 && "ADD".equalsIgnoreCase(la_action))
				{
					// insert detail request data in audit table.
					insertAuditJlist = new JSONArrayImpl();
					//for (int i = 0; i < ins_length; i++) 
					//{
						insertJson = new JSONObjectImpl();
						insertJson = insertJlist.getJSONObject(0);
						insertJsonKeys = insertJson.keySet();
						
						String keyName = insertJsonKeys.iterator().next(); 

						//for (String keyName : insertJsonKeys)
						//{
							// get column type .
							//insertAuditJson = new JSONObjectImpl();
							//ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE_SDT", keyName);
							//if (keyName.equals("DOC_IMAGE")) {
								//ls_value = " ";
							//} else {
							//	ls_value = insertJson.getString(keyName);
							//}
						
							ls_value = insertJson.getString(keyName);
							
							insertAuditJson.put("OWNER_NAME", "EASY_BANK");
							insertAuditJson.put("TABLE_NAME", la_dtl_tablename);
							insertAuditJson.put("REQ_CD", la_req_cd);
							insertAuditJson.put("ENTRY_TYPE", ll_entry_type);
							insertAuditJson.put("SR_CD", ll_SrCd);
							insertAuditJson.put("CUSTOMER_ID", reqJson.getString(CUSTOMER_ID));
							insertAuditJson.put("REF_SR_CD", documentJson.getString(SR_CD));					
							insertAuditJson.put("REF_LINE_CD", insertJson.getString("LINE_CD"));				
							insertAuditJson.put("OLD_VALUE", " ");
							insertAuditJson.put("COLUMN_TYPE", ls_column_type);
							insertAuditJson.put("ACTION", la_action);
							insertAuditJson.put("TRAN_CD", documentJson.getString(TRAN_CD));
							insertAuditJson.put("NEW_VALUE","");
							insertAuditJson.put("COLUMN_NAME","New Inserted");

							insertAuditJlist.put(insertAuditJson);
							ll_SrCd++;
						//}
					//}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, insertAuditJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) {
						return ls_audit_response;
					}

				} else if (upd_length != 0 && "UPD".equalsIgnoreCase(la_action)) 
				{
					// insert detail updateded request data in audit table.
					updateAuditJlist = new JSONArrayImpl();
					for (int i = 0; i < upd_length; i++)
					{
						updateJson = new JSONObjectImpl();

						updateJson = updateJlist.getJSONObject(i);
						oldValueJson = updateJson.getJSONObject("_OLDROWVALUE");
						updValueJlist = updateJson.getJSONArray("_UPDATEDCOLUMNS");
						ls_customer_id = reqJson.getString("CUSTOMER_ID");
						ls_cust_type = reqJson.getString(REQ_FLAG);

						for (int k = 0; k < updValueJlist.length(); k++)
						{
							updateAuditJson = new JSONObjectImpl();
							ls_upd_column = updValueJlist.getString(k);

							if (ls_upd_column.equals("DOC_IMAGE")) {
								ls_old_value = " ";
								ls_new_value = " ";
							} else {
								ls_old_value = oldValueJson.getString(ls_upd_column);
								ls_new_value = updateJson.getString(ls_upd_column);
							}

							if (oldValueJson.has(ls_upd_column))
							{
								ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE_SDT",ls_upd_column);

								updateAuditJson.put(COLUMN_NAME, ls_upd_column);
								updateAuditJson.put(ENTRY_TYPE, ll_entry_type);
								updateAuditJson.put("NEW_VALUE", ls_new_value);
								updateAuditJson.put("OLD_VALUE", ls_old_value);
								updateAuditJson.put("OWNER_NAME", "EASY_BANK");
								updateAuditJson.put(CUSTOMER_ID, ls_customer_id);
								updateAuditJson.put("REF_SR_CD", updateJson.getString("SR_CD"));
								updateAuditJson.put("REF_LINE_CD", updateJson.getString("LINE_CD"));
								updateAuditJson.put("TRAN_CD", updateJson.getString(TRAN_CD));
								updateAuditJson.put(REQ_CD, la_req_cd);
								updateAuditJson.put("ACTION", la_action);
								updateAuditJson.put(COLUMN_TYPE, ls_column_type);
								updateAuditJson.put(SR_CD, ll_SrCd);
								updateAuditJson.put("TABLE_NAME", la_dtl_tablename);
								updateAuditJson.put(COMP_CD, reqJson.getString(COMP_CD));
								updateAuditJson.put(BRANCH_CD, reqJson.getString(BRANCH_CD));
								updateAuditJson.put(ACCT_TYPE, reqJson.getString(ACCT_TYPE));
								updateAuditJson.put(ACCT_CD, reqJson.getString(ACCT_CD));

								updateAuditJlist.put(updateAuditJson);
								ll_SrCd++;
							}
						}
					}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, updateAuditJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					
					if (!isSuccessStCode(auditResJson.getString(STATUS)))
					{
						return ls_audit_response;
					}
				} else if(del_length != 0 && "DEL".equalsIgnoreCase(la_action)) 
				{
					// insert detail delete request data in audit table.		
					deleteAuditJlist = new JSONArrayImpl();
					
					for (int i = 0; i < del_length; i++) 
					{
						deleteJson = new JSONObjectImpl();
						deleteAuditJson = new JSONObjectImpl();

						deleteJson = deleteJlist.getJSONObject(i);

						deleteAuditJson.put(COLUMN_NAME, "Deleted");
						deleteAuditJson.put(ENTRY_TYPE, ll_entry_type);
						deleteAuditJson.put("NEW_VALUE", "");
						deleteAuditJson.put("OLD_VALUE", "");
						deleteAuditJson.put("OWNER_NAME", "EASY_BANK");
						deleteAuditJson.put(CUSTOMER_ID, ls_customer_id);
						deleteAuditJson.put("REF_SR_CD", deleteJson.getString("SR_CD"));
						deleteAuditJson.put("REF_LINE_CD", deleteJson.getString("LINE_CD"));
						deleteAuditJson.put("TRAN_CD", deleteJson.getString(TRAN_CD));
						deleteAuditJson.put(REQ_CD, la_req_cd);
						deleteAuditJson.put("ACTION", la_action);
						deleteAuditJson.put(COLUMN_TYPE, "NUMBER");
						deleteAuditJson.put(SR_CD, ll_SrCd);
						deleteAuditJson.put("TABLE_NAME", la_dtl_tablename);

						deleteAuditJlist.put(deleteAuditJson);
						ll_SrCd++;
					}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, deleteAuditJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) {
						return ls_audit_response;
					}
				} else {
					return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R",
							"common.invalid_req_data").toString();
				}
			} else if ("MD".equalsIgnoreCase(la_action_type))
			{

				if ("ADD".equalsIgnoreCase(la_action) && ins_length != 0) 
				{

					// insert main data in audit table after that insert data in detail table.
					insertJson = documentJson;
					insertJsonKeys = insertJson.keySet();
					insertAuditJlist = new JSONArrayImpl();
					for (String keyName : insertJsonKeys) {
						checkKeys = true;
						if (IS_MAIN_DATA_ADD.equals(keyName) || "DETAILS_DATA".equals(keyName)) {
							checkKeys = false;
						}
						if (checkKeys) {
							ls_value = insertJson.getString(keyName);
							insertAuditJson = new JSONObjectImpl();

							// get column type .
							ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE", keyName);

							insertAuditJson.put("OWNER_NAME", "EASY_BANK");
							insertAuditJson.put("TABLE_NAME", la_mst_tablename);
							insertAuditJson.put("REQ_CD", la_req_cd);
							insertAuditJson.put("ENTRY_TYPE", ll_entry_type);
							insertAuditJson.put("SR_CD", ll_SrCd);
							insertAuditJson.put("CUSTOMER_ID", reqJson.getString(CUSTOMER_ID));
							insertAuditJson.put("REF_SR_CD", insertJson.getString(SR_CD));
							insertAuditJson.put("OLD_VALUE", " ");
							insertAuditJson.put("COLUMN_TYPE", ls_column_type);
							insertAuditJson.put("ACTION", la_action);
							insertAuditJson.put("TRAN_CD", insertJson.getString(TRAN_CD));
							insertAuditJson.put("NEW_VALUE", ls_value);
							insertAuditJson.put("COLUMN_NAME", keyName);
							insertAuditJlist.put(insertAuditJson);

							ll_SrCd++;
						}
					}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, insertAuditJlist, userJson, jPrimaryDtl,"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) {
						return ls_audit_response;
					}
					// insert detail request data in audit table.
					insertAuditDtlJlist = new JSONArrayImpl();
					for (int i = 0; i < ins_length; i++) {

						insertJson = new JSONObjectImpl();

						insertJson = insertJlist.getJSONObject(i);
						insertJsonKeys = insertJson.keySet();

						for (String keyName : insertJsonKeys)
						{

							// get column type .
							insertAuditJson = new JSONObjectImpl();
							ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE_SDT", keyName);

							if (keyName.equals("DOC_IMAGE"))
							{
								ls_value = " ";
							} else {
								ls_value = insertJson.getString(keyName);
							}

							insertAuditJson.put("OWNER_NAME", "EASY_BANK");
							insertAuditJson.put("TABLE_NAME", la_dtl_tablename);
							insertAuditJson.put("REQ_CD", la_req_cd);
							insertAuditJson.put("ENTRY_TYPE", ll_entry_type);
							insertAuditJson.put("SR_CD", ll_SrCd);
							insertAuditJson.put("CUSTOMER_ID", reqJson.getString(CUSTOMER_ID));
							insertAuditJson.put("REF_SR_CD", insertJson.getString(SR_CD));
							insertAuditJson.put("OLD_VALUE", " ");
							insertAuditJson.put("COLUMN_TYPE", ls_column_type);
							insertAuditJson.put("ACTION", la_action);
							insertAuditJson.put("TRAN_CD", insertJson.getString(TRAN_CD));
							insertAuditJson.put("NEW_VALUE", ls_value);
							insertAuditJson.put("COLUMN_NAME", keyName);

							insertAuditDtlJlist.put(insertAuditJson);
							ll_SrCd++;
						}
					}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, insertAuditDtlJlist, userJson, jPrimaryDtl,
							"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) {
						return ls_audit_response;
					}
				} else if ("UPD".equalsIgnoreCase(la_action) && upd_length != 0) {

					// insert main updated data in audit table.
					updateJson = documentJson;
					oldValueJson = updateJson.getJSONObject("_OLDROWVALUE");
					updValueJlist = updateJson.getJSONArray("_UPDATEDCOLUMNS");
					ls_customer_id = reqJson.getString("CUSTOMER_ID");
					ls_cust_type = reqJson.getString(REQ_FLAG);

					for (int k = 0; k < updValueJlist.length(); k++) {

						updateAuditJson = new JSONObjectImpl();
						ls_upd_column = updValueJlist.getString(k);

						if (oldValueJson.has(ls_upd_column)) {

							ls_old_value = oldValueJson.getString(ls_upd_column);
							ls_new_value = updateJson.getString(ls_upd_column);
							ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE",
									ls_upd_column);

							updateAuditJson.put(COLUMN_NAME, ls_upd_column);
							updateAuditJson.put(ENTRY_TYPE, ll_entry_type);
							updateAuditJson.put("NEW_VALUE", ls_new_value);
							updateAuditJson.put("OLD_VALUE", ls_old_value);
							updateAuditJson.put("OWNER_NAME", "EASY_BANK");
							updateAuditJson.put(CUSTOMER_ID, ls_customer_id);
							updateAuditJson.put("REF_SR_CD", updateJson.getString("SR_CD"));
							updateAuditJson.put("TRAN_CD", updateJson.getString(TRAN_CD));
							updateAuditJson.put(REQ_CD, la_req_cd);
							updateAuditJson.put("ACTION", la_action);
							updateAuditJson.put(COLUMN_TYPE, ls_column_type);
							updateAuditJson.put(SR_CD, ll_SrCd);
							updateAuditJson.put("TABLE_NAME", la_mst_tablename);

							ll_SrCd++;
						}

						jPrimaryDtl.put(REQ_CD, la_req_cd);
						ls_audit_response = InsertData(connection, updateAuditJson, userJson, 2, "",
								"REQ_ACCT_MST_UPD_DTL");

						auditResJson = common.ofGetJsonObject(ls_audit_response);
						if (!isSuccessStCode(auditResJson.getString(STATUS))) {
							return ls_audit_response;
						}
					}

					// insert detail updateded request data in audit table.
					updateAuditDtlJlist = new JSONArrayImpl();
					for (int i = 0; i < upd_length; i++) {

						updateJson = new JSONObjectImpl();

						updateJson = updateJlist.getJSONObject(i);
						oldValueJson = updateJson.getJSONObject("_OLDROWVALUE");
						updValueJlist = updateJson.getJSONArray("_UPDATEDCOLUMNS");
						ls_customer_id = reqJson.getString("CUSTOMER_ID");
						ls_cust_type = reqJson.getString(REQ_FLAG);

						for (int k = 0; k < updValueJlist.length(); k++) {

							updateAuditJson = new JSONObjectImpl();
							ls_upd_column = updValueJlist.getString(k);

							if (oldValueJson.has(ls_upd_column)) {
								ls_old_value = oldValueJson.getString(ls_upd_column);
								ls_new_value = updateJson.getString(ls_upd_column);

								ls_column_type = ofGetColumnDataType(connection, "REQ_ACCT_MST_DOC_TEMPLATE_SDT",
										ls_upd_column);

								updateAuditJson.put(COLUMN_NAME, ls_upd_column);
								updateAuditJson.put(ENTRY_TYPE, ll_entry_type);
								updateAuditJson.put("NEW_VALUE", ls_new_value);
								updateAuditJson.put("OLD_VALUE", ls_old_value);
								updateAuditJson.put("OWNER_NAME", "EASY_BANK");
								updateAuditJson.put(CUSTOMER_ID, ls_customer_id);
								updateAuditJson.put("REF_SR_CD", updateJson.getString("SR_CD"));
								updateAuditJson.put("TRAN_CD", updateJson.getString(TRAN_CD));
								updateAuditJson.put(REQ_CD, la_req_cd);
								updateAuditJson.put("ACTION", la_action);
								updateAuditJson.put(COLUMN_TYPE, ls_column_type);
								updateAuditJson.put(SR_CD, ll_SrCd);
								updateAuditJson.put("TABLE_NAME", la_dtl_tablename);

								updateAuditDtlJlist.put(updateAuditJson);
								ll_SrCd++;
							}
						}
					}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, updateAuditDtlJlist, userJson, jPrimaryDtl,
							"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) {
						return ls_audit_response;
					}
				} else if ("DEL".equalsIgnoreCase(la_action) && del_length != 0) {

					// insert main delete request data in audit table.
					deleteJson = documentJson;
					deleteAuditJson = new JSONObjectImpl();

					deleteAuditJson.put(COLUMN_NAME, "TEST");
					deleteAuditJson.put(ENTRY_TYPE, ll_entry_type);
					deleteAuditJson.put("NEW_VALUE", "");
					deleteAuditJson.put("OLD_VALUE", "");
					deleteAuditJson.put("OWNER_NAME", "EASY_BANK");
					deleteAuditJson.put(CUSTOMER_ID, ls_customer_id);
					deleteAuditJson.put("REF_SR_CD", deleteJson.getString("SR_CD"));
					deleteAuditJson.put("TRAN_CD", deleteJson.getString(TRAN_CD));
					deleteAuditJson.put(REQ_CD, la_req_cd);
					deleteAuditJson.put("ACTION", "DEL");
					deleteAuditJson.put(COLUMN_TYPE, "NUMBER");
					deleteAuditJson.put(SR_CD, ll_SrCd);
					deleteAuditJson.put("TABLE_NAME", la_mst_tablename);

					ll_SrCd++;

					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = InsertData(connection, deleteAuditJson, userJson, 2, "",
							"REQ_ACCT_MST_UPD_DTL");
					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) {
						return ls_audit_response;
					}
					deleteAuditDtlJlist = new JSONArrayImpl();
					// insert detail delete request data in audit table.
					for (int i = 0; i < del_length; i++) {

						deleteJson = new JSONObjectImpl();
						deleteAuditJson = new JSONObjectImpl();

						deleteJson = deleteJlist.getJSONObject(i);

						deleteAuditJson.put(COLUMN_NAME, "TEST");
						deleteAuditJson.put(ENTRY_TYPE, ll_entry_type);
						deleteAuditJson.put("NEW_VALUE", "");
						deleteAuditJson.put("OLD_VALUE", "");
						deleteAuditJson.put("OWNER_NAME", "EASY_BANK");
						deleteAuditJson.put(CUSTOMER_ID, ls_customer_id);
						deleteAuditJson.put("REF_SR_CD", deleteJson.getString("SR_CD"));
						deleteAuditJson.put("TRAN_CD", deleteJson.getString(TRAN_CD));
						deleteAuditJson.put(REQ_CD, la_req_cd);
						deleteAuditJson.put("ACTION", la_action);
						deleteAuditJson.put(COLUMN_TYPE, "NUMBER");
						deleteAuditJson.put(SR_CD, ll_SrCd);
						deleteAuditJson.put("TABLE_NAME", la_dtl_tablename);

						deleteAuditJlist.put(deleteAuditJson);
						ll_SrCd++;
					}
					jPrimaryDtl.put(REQ_CD, la_req_cd);
					ls_audit_response = insertDTLData(connection, deleteAuditJlist, userJson, jPrimaryDtl,
							"REQ_ACCT_MST_UPD_DTL");

					auditResJson = common.ofGetJsonObject(ls_audit_response);
					if (!isSuccessStCode(auditResJson.getString(STATUS))) {
						return ls_audit_response;
					}
				}
			} else {
				return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R", "common.invalid_req_data")
						.toString();
			}
			return ls_audit_response;
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAccountDocAuditData", "(ENP328)");
		}

	}
	
	
	//public String ExAcctCollateralOtherSecurityDtlButton(Connection connection,JSONArrayImpl insertJlist,JSONObjectImpl userJson,JSONObjectImpl jPrimaryDtl,JSONObjectImpl reqJson,String ls_isFromMain_dtl,String action) throws Exception 
	//{
//
//		String ls_detailResponse = StringUtils.EMPTY;
//		String ls_security_type = StringUtils.EMPTY;
//		String table_Name = StringUtils.EMPTY;
//		String main_table_Name = StringUtils.EMPTY;
//		String ls_reqFlag = StringUtils.EMPTY;
//		String ls_acctType = StringUtils.EMPTY;
//		String ls_acctCode = StringUtils.EMPTY;
//		String ls_compCd = StringUtils.EMPTY;
//		String ls_branchCd = StringUtils.EMPTY;
//		String ls_Req_cd=StringUtils.EMPTY;
//		String ls_insResponse = StringUtils.EMPTY;
//		
//		JSONObjectImpl insertDetailJson = new JSONObjectImpl();
//		JSONObjectImpl updateDataJson = new JSONObjectImpl();
//		//JSONArrayImpl insertJlist = new JSONArrayImpl();
//		JSONArrayImpl deleteJlist = new JSONArrayImpl();
//		JSONArrayImpl updateJlist = new JSONArrayImpl();
//		JSONArrayImpl insJsonObjlist = new JSONArrayImpl();
//		JSONArrayImpl deleteDtlJlist = new JSONArrayImpl();
//		JSONArrayImpl otherSecInsJson = new JSONArrayImpl();
//		JSONArrayImpl otherSecUpdJson = new JSONArrayImpl();
//		JSONArrayImpl jallColumnList = new JSONArrayImpl();
//		JSONArrayImpl updateDtlJlist = new JSONArrayImpl();
//		JSONArrayImpl insertDtlJlist = new JSONArrayImpl();
//		JSONArrayImpl insAudJlist = new JSONArrayImpl();
//		JSONArrayImpl updAudJlist = new JSONArrayImpl();
//		JSONObjectImpl updDetailJson = new JSONObjectImpl();
//		JSONObjectImpl other_security_dtlJson;
//		JSONObjectImpl Upd_other_security_dtlJson;
//		long ls_Line_cd;
//		int li_maxSrCd;
//		long ll_SrCd, ll_ENTRY_TYPE;
//		
//
//		try {	
//			
//			ls_reqFlag = reqJson.getString(REQ_FLAG);
//			ls_acctType = reqJson.getString(ACCT_TYPE);
//			ls_acctCode = reqJson.getString(ACCT_CD);
//			//ls_customerId = reqJson.getString(CUSTOMER_ID);
//			ls_compCd = getRequestUniqueData.getCompCode();
//			ls_branchCd = getRequestUniqueData.getBranchCode();
//			ls_Req_cd = reqJson.getString(REQ_CD);
//			
//			if (reqJson.has("JOINT_ACCOUNT_DTL")) 
//			{
//				JSONArrayImpl otherAddJlist = reqJson.getJSONArray("JOINT_ACCOUNT_DTL");
//				
//				int ins_length = 0;
//				int upd_length = 0;
//				int del_length = 0;
//				
//			//	for (int i = 0; i < otherAddJlist.length(); i++)
//			//	{
//					//otherAddJson = otherAddJlist.getJSONObject(i);
//				//	ls_isFromJointAcctDtl = otherAddJson.getString("IS_FROM_JOINT_ACCOUNT_DTL");
//
//			      	//insertJlist = otherAddJson.getJSONArray("isNewRow");
//					//deleteJlist = otherAddJson.getJSONArray("isDeleteRow");
//					//updateJlist = otherAddJson.getJSONArray("isUpdatedRow");
//					//insAudJlist = new JSONArrayImpl();
//
//					//ins_length = insertJlist.length();
//					//upd_length = updateJlist.length();
//					//del_length = deleteJlist.length();
//
//					//Insert Details
//				
//					if (insertJlist.length() != 0 && action.equals("ADD"))
//					{
//						for (int i = 0; i < insertJlist.length(); i++) 
//						{
//							insertDetailJson = insertJlist.getJSONObject(i);
//
//							String j_type = insertDetailJson.getString(J_TYPE).trim();
//
//							if(j_type.equals("M") && insertDetailJson.has("OTHER_SECURITY_TYPE"))
//							{
//								ls_security_type=insertDetailJson.getString("OTHER_SECURITY");
//
//								if(ls_security_type.equals("STK") || ls_security_type.equals("BDC"))
//								{
//									table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_STOCK";
//									main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_STOCK";
//								}
//								else if (ls_security_type.equals("SH"))
//								{
//									table_Name="EASY_BANK.STOCK";
//									main_table_Name="EASY_BANK.STOCK";
//								}
//								else if (ls_security_type.equals("MCH") || ls_security_type.equals("VEH") || ls_security_type.equals("PRT"))
//								{
//									table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_OTHER";
//									main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_OTHER";
//								}
//								else if (ls_security_type.equals("LIC"))
//								{
//									table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_LIC";
//									main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_LIC";
//								}
//								else if (ls_security_type.equals("OTH") || ls_security_type.equals("GOV") || ls_security_type.equals("BRD") || ls_security_type.equals("BFD"))
//								{
//									table_Name="ENFINITY.REQ_OTHER_COLLATERAL_DTL";
//									main_table_Name="EASY_BANK.OTHER_COLLATERAL_DTL";
//								}
//
//								otherSecInsJson = insertDetailJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isNewRow");
//
//								if(otherSecInsJson.length() != 0)
//								{
//									ls_Line_cd=0;
//									for (int j = 0; j < otherSecInsJson.length(); j++) 
//									{
//										other_security_dtlJson = otherSecInsJson.getJSONObject(j);
//
//										jPrimaryDtl.put(COMP_CD, ls_compCd);
//										jPrimaryDtl.put(BRANCH_CD, ls_branchCd);
//										jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
//										jPrimaryDtl.put(ACCT_CD, ls_acctCode);
//										jPrimaryDtl.put(SR_CD,insertDetailJson.getString(SR_CD));
//
//										if(ls_Line_cd == 0)
//										{
//											if("Y".equalsIgnoreCase(ls_isFromMain_dtl))
//											{
//												setSchemaName("EASY_BANK");
//												jallColumnList = getColumnDefination(main_table_Name, connection, true);
//												ls_Line_cd = getMaxSrCd(connection,main_table_Name, LINE_ID, jallColumnList,jPrimaryDtl);	
//												setSchemaName("ENFINITY");
//											}else
//											{
//												jPrimaryDtl.put(REQ_CD, ls_Req_cd);
//												jallColumnList = getColumnDefination(table_Name, connection, true);
//												ls_Line_cd = getMaxSrCd(connection,table_Name, LINE_ID, jallColumnList,jPrimaryDtl);
//											}
//											
//										}
//										
//										ls_Line_cd++;
//										other_security_dtlJson.put("ACTION", "ADD");
//										other_security_dtlJson.put(REQ_FLAG, ls_reqFlag);
//										other_security_dtlJson.put(ENT_COMP_CD, ls_compCd);
//										other_security_dtlJson.put(ENT_BRANCH_CD, ls_branchCd);
//										other_security_dtlJson.put(SR_CD,insertDetailJson.getString(SR_CD));
//										other_security_dtlJson.put(J_TYPE, insertDetailJson.getString(J_TYPE));
//										other_security_dtlJson.put("LINE_ID", ls_Line_cd);
//										other_security_dtlJson.put(REQ_CD, ls_Req_cd);
//										insJsonObjlist.put(other_security_dtlJson);
//										insertDtlJlist.put(other_security_dtlJson);
//									}
//
//									ls_detailResponse = insertDTLData(connection, insJsonObjlist, userJson, reqJson,table_Name);
//									JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
//
//									if (!isSuccessStCode(jobjinsRes.getString(STATUS)))
//									{
//										return ls_detailResponse;
//									}
//								}
//								  detailDataJson.put("isNewRow", insertDtlJlist);
//								  detailDataJson.put("isDeleteRow",new JSONArrayImpl());
//								  detailDataJson.put("isUpdatedRow",new JSONArrayImpl());
//								  
//								  if(insertDtlJlist.length() != 0) 
//								  { 
//									  
//									  ls_insResponse =  ofGetInsertAcctDocAuditData(connection, detailDataJson,reqJson, userJson, main_table_Name, table_Name);
//
//									  responseJson = common.ofGetJsonObject(ls_insResponse);
//									  ls_resStatus = responseJson.getString(STATUS);
//
//									  if (!isSuccessStCode(ls_resStatus))
//									  {
//										  return ls_insResponse;
//									  }
//								  }
//							}
//						}	  
//					}
//					
//					//Update Details
//					//vishal
//					if (insertJlist.length() != 0 && action.equals("UPD"))
//					{
//						updAudJlist = new JSONArrayImpl();
//						
//						if ("Y".equalsIgnoreCase(ls_isFromMain_dtl))
//						{						
//							for (int a = 0; a < insertJlist.length(); a++)
//							{
//								updateDataJson = new JSONObjectImpl();							
//								//updateDtlJlist = updateJlist;
//								updateDataJson = insertJlist.getJSONObject(a);
//
//								String j_type = updateDataJson.getString(J_TYPE).trim();
//
//								if(j_type.equals("M") && updateDataJson.has("OTHER_SECURITY_TYPE"))
//								{
//									ls_security_type=updateDataJson.getString("OTHER_SECURITY");
//
//									if(ls_security_type.equals("STK") || ls_security_type.equals("BDC"))
//									{
//										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_STOCK";
//										main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_STOCK";
//									}
//									else if (ls_security_type.equals("SH"))
//									{
//										table_Name="EASY_BANK.STOCK";
//										main_table_Name="EASY_BANK.STOCK";
//									}
//									else if (ls_security_type.equals("MCH") || ls_security_type.equals("VEH") || ls_security_type.equals("PRT"))
//									{
//										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_OTHER";
//										main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_OTHER";
//									}
//									else if (ls_security_type.equals("LIC"))
//									{
//										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_LIC";
//										main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_LIC";
//									}
//									else if (ls_security_type.equals("OTH") || ls_security_type.equals("GOV") || ls_security_type.equals("BRD") || ls_security_type.equals("BFD"))
//									{
//										table_Name="ENFINITY.REQ_OTHER_COLLATERAL_DTL";
//										main_table_Name="EASY_BANK.OTHER_COLLATERAL_DTL";
//									}
//
//									otherSecInsJson = updateDataJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isNewRow");
//									otherSecUpdJson = updateDataJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isUpdatedRow");
//
//									if(otherSecInsJson.length() != 0)
//									{
//										ls_Line_cd=0;
//										for (int j = 0; j < otherSecInsJson.length(); j++) 
//										{
//											//insertDtlJlist = otherSecInsJson;
//											other_security_dtlJson = otherSecInsJson.getJSONObject(j);
//
//											jPrimaryDtl.put(COMP_CD, ls_compCd);
//											jPrimaryDtl.put(BRANCH_CD, ls_branchCd);
//											jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
//											jPrimaryDtl.put(ACCT_CD, ls_acctCode);
//											jPrimaryDtl.put(SR_CD,updateDataJson.getString(SR_CD));
//
//											if(ls_Line_cd == 0)
//											{
//												if("Y".equalsIgnoreCase(ls_isFromMain_dtl))
//												{
//													setSchemaName("EASY_BANK");
//													jallColumnList = getColumnDefination(main_table_Name, connection, true);
//													ls_Line_cd = getMaxSrCd(connection,main_table_Name, LINE_ID, jallColumnList,jPrimaryDtl);	
//													setSchemaName("ENFINITY");
//												}else
//												{
//													jPrimaryDtl.put(REQ_CD, ls_Req_cd);
//													jallColumnList = getColumnDefination(table_Name, connection, true);
//													ls_Line_cd = getMaxSrCd(connection,table_Name, LINE_ID, jallColumnList,jPrimaryDtl);
//												}
//											}
//
//											ls_Line_cd++;
//											other_security_dtlJson.put("ACTION", "ADD");
//											other_security_dtlJson.put(REQ_FLAG, ls_reqFlag);
//											other_security_dtlJson.put(ENT_COMP_CD, ls_compCd);
//											other_security_dtlJson.put(ENT_BRANCH_CD, ls_branchCd);
//											other_security_dtlJson.put(SR_CD,updateDataJson.getString(SR_CD));
//											other_security_dtlJson.put(J_TYPE, updateDataJson.getString(J_TYPE));
//											other_security_dtlJson.put("LINE_ID", ls_Line_cd);
//											other_security_dtlJson.put(REQ_CD, ls_Req_cd);
//											insJsonObjlist.put(other_security_dtlJson);
//											insertDtlJlist.put(other_security_dtlJson);
//										}
//
//										ls_detailResponse = insertDTLData(connection, insJsonObjlist, userJson, reqJson,table_Name);
//										JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
//
//										if (!isSuccessStCode(jobjinsRes.getString(STATUS)))
//										{
//											return ls_detailResponse;
//										}
//									}
//
//									if(otherSecUpdJson.length() != 0)
//									{
//										updAudJlist = new JSONArrayImpl();
//										for (int j = 0; j < otherSecUpdJson.length(); j++) 
//										{
//											updateDtlJlist = otherSecUpdJson;
//											other_security_dtlJson = new JSONObjectImpl();	
//											other_security_dtlJson = otherSecUpdJson.getJSONObject(j);
//											updDetailJson = new JSONObjectImpl();
//
//											for (String key : other_security_dtlJson.keySet()) 
//											{
//												if (!key.equals("_UPDATEDCOLUMNS") && !key.equals("_OLDROWVALUE")) {
//													updDetailJson.put(key, other_security_dtlJson.get(key));
//												}
//											}
//
//											updDetailJson.put("ACTION", "UPD");
//											updDetailJson.put(REQ_FLAG, ls_reqFlag);
//											updDetailJson.put(ENT_COMP_CD, ls_compCd);
//											updDetailJson.put(ENT_BRANCH_CD, ls_branchCd);
//											//updDetailJson.put(SR_CD,AcctMSTState.getString(SR_CD));
//											//updDetailJson.put(J_TYPE, insertDetailJson.getString(J_TYPE));
//											updDetailJson.put(REQ_CD, ls_Req_cd);
//											updAudJlist.put(updDetailJson);
//											//updateDtlJlist.put(other_security_dtlJson);
//										}
//
//										ls_detailResponse = insertDTLData(connection, updAudJlist, userJson, reqJson,table_Name);
//										JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
//
//										if (!isSuccessStCode(jobjinsRes.getString(STATUS)))
//										{
//											return ls_detailResponse;
//										}
//									}
//
//									detailDataJson.put("isNewRow", insertDtlJlist);
//									detailDataJson.put("isDeleteRow",updateDtlJlist);
//									detailDataJson.put("isUpdatedRow",new JSONArrayImpl());
//
//									if(insertDtlJlist.length() != 0 || updateDtlJlist.length() != 0) 
//									{ 
//										ls_insResponse =  ofGetInsertAcctDocAuditData(connection, detailDataJson,reqJson, userJson, main_table_Name, table_Name);
//
//										responseJson = common.ofGetJsonObject(ls_insResponse);
//										ls_resStatus = responseJson.getString(STATUS);
//
//										if (!isSuccessStCode(ls_resStatus))
//										{
//											return ls_insResponse;
//										}
//									}
//								}
//
//							}
//
//						} else {
//
//							for (int a = 0; a < upd_length; a++)
//							{
//								updateDataJson = new JSONObjectImpl();
//								updateDataJson = updateJlist.getJSONObject(a);
//
//								String j_type = updateDataJson.getString(J_TYPE).trim();
//
//								if(j_type.equals("M") && updateDataJson.has("OTHER_SECURITY_TYPE"))
//								{
//									ls_security_type=updateDataJson.getString("OTHER_SECURITY");
//
//									if(ls_security_type.equals("STK") || ls_security_type.equals("BDC"))
//									{
//										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_STOCK";
//										main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_STOCK";
//									}
//									else if (ls_security_type.equals("SH"))
//									{
//										table_Name="EASY_BANK.STOCK";
//										main_table_Name="EASY_BANK.STOCK";
//									}
//									else if (ls_security_type.equals("MCH") || ls_security_type.equals("VEH") || ls_security_type.equals("PRT"))
//									{
//										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_OTHER";
//										main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_OTHER";
//									}
//									else if (ls_security_type.equals("LIC"))
//									{
//										table_Name="ENFINITY.REQ_JOINT_ACCOUNT_MST_LIC";
//										main_table_Name="EASY_BANK.JOINT_ACCOUNT_MST_LIC";
//									}
//									else if (ls_security_type.equals("OTH") || ls_security_type.equals("GOV") || ls_security_type.equals("BRD") || ls_security_type.equals("BFD"))
//									{
//										table_Name="ENFINITY.REQ_OTHER_COLLATERAL_DTL";
//										main_table_Name="EASY_BANK.OTHER_COLLATERAL_DTL";
//									}
//
//									otherSecInsJson = updateDataJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isNewRow");
//									otherSecUpdJson = updateDataJson.getJSONArray("OTHER_SECURITY_TYPE").getJSONObject(0).getJSONArray("isUpdatedRow");
//
//									if(otherSecInsJson.length() != 0)
//									{
//										ls_Line_cd=0;
//										for (int j = 0; j < otherSecInsJson.length(); j++) 
//										{
//											//insertDtlJlist = otherSecInsJson;
//											other_security_dtlJson = otherSecInsJson.getJSONObject(j);
//
//											jPrimaryDtl.put(COMP_CD, ls_compCd);
//											jPrimaryDtl.put(BRANCH_CD, ls_branchCd);
//											jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
//											jPrimaryDtl.put(ACCT_CD, ls_acctCode);
//											jPrimaryDtl.put(SR_CD,updateDataJson.getString(SR_CD));
//
//											if(ls_Line_cd == 0)
//											{
//												if("Y".equalsIgnoreCase(ls_isFromMain_dtl))
//												{
//													setSchemaName("EASY_BANK");
//													jallColumnList = getColumnDefination(main_table_Name, connection, true);
//													ls_Line_cd = getMaxSrCd(connection,main_table_Name, LINE_ID, jallColumnList,jPrimaryDtl);	
//													setSchemaName("ENFINITY");
//												}else
//												{
//													jPrimaryDtl.put(REQ_CD, ls_Req_cd);
//													jallColumnList = getColumnDefination(table_Name, connection, true);
//													ls_Line_cd = getMaxSrCd(connection,table_Name, LINE_ID, jallColumnList,jPrimaryDtl);
//												}
//											}
//
//											ls_Line_cd++;
//											other_security_dtlJson.put("ACTION", "ADD");
//											other_security_dtlJson.put(REQ_FLAG, ls_reqFlag);
//											other_security_dtlJson.put(ENT_COMP_CD, ls_compCd);
//											other_security_dtlJson.put(ENT_BRANCH_CD, ls_branchCd);
//											other_security_dtlJson.put(SR_CD,insertDetailJson.getString(SR_CD));
//											other_security_dtlJson.put(J_TYPE, insertDetailJson.getString(J_TYPE));
//											other_security_dtlJson.put("LINE_ID", ls_Line_cd);
//											other_security_dtlJson.put(REQ_CD, ls_Req_cd);
//											insJsonObjlist.put(other_security_dtlJson);
//											insertDtlJlist.put(other_security_dtlJson);
//										}
//
//										ls_detailResponse = insertDTLData(connection, insJsonObjlist, userJson, reqJson,table_Name);
//										JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse);
//
//										if (!isSuccessStCode(jobjinsRes.getString(STATUS)))
//										{
//											return ls_detailResponse;
//										}
//									}
//
//									if(otherSecUpdJson.length() != 0)
//									{
//										for (int j = 0; j < otherSecUpdJson.length(); j++) 
//										{
//											Upd_other_security_dtlJson = new JSONObjectImpl();
//											updateDtlJlist = otherSecUpdJson;
//											Upd_other_security_dtlJson = otherSecUpdJson.getJSONObject(j);
//
//											Upd_other_security_dtlJson.put(REQ_CD, ls_Req_cd);
//
//											ls_detailResponse = UpdateData(connection, Upd_other_security_dtlJson,userJson,table_Name);
//
//											JSONObjectImpl jobjinsRes = common.ofGetJsonObject(ls_detailResponse); 
//											if(!isSuccessStCode(jobjinsRes.getString(STATUS)))
//											{ 
//												return ls_detailResponse;
//											}
//										}
//									}
//
//									detailDataJson.put("isNewRow", insertDtlJlist);
//									detailDataJson.put("isDeleteRow",updateDtlJlist);
//									detailDataJson.put("isUpdatedRow",new JSONArrayImpl());
//
//									if(insertDtlJlist.length() != 0 || updateDtlJlist.length() != 0) 
//									{ 
//										ls_insResponse =  ofGetInsertAcctDocAuditData(connection, detailDataJson,reqJson, userJson, main_table_Name, table_Name);
//
//										responseJson = common.ofGetJsonObject(ls_insResponse);
//										ls_resStatus = responseJson.getString(STATUS);
//
//										if (!isSuccessStCode(ls_resStatus))
//										{
//											return ls_insResponse;
//										}
//									}
//								}
//							}
//						}			
//					}
//					
//					//Deletet Details 
//					
//					if (insertJlist.length() != 0 && action.equals("DEL")) 
//					{
//						if (!"Y".equalsIgnoreCase(ls_isFromMain_dtl))
//						{
//							deleteDtlJlist = new JSONArrayImpl();
//							deleteDtlJlist = deleteJlist;
//							
//							reqJson.put(REQ_CD, ls_req_cd);						
//							jPrimaryDtl.put(REQ_CD, ls_req_cd);
//							
//							JSONArrayImpl jallColumn = getColumnDefination("REQ_JOINT_ACCOUNT_MST", connection,true);
//							int li_del_res = DeleteDTLData(connection, deleteJlist, userJson, jPrimaryDtl,"REQ_JOINT_ACCOUNT_MST", jallColumn);
//
//							if (li_del_res == 0) {
//								connection.rollback();
//								return common.ofGetResponseJson(new JSONArrayImpl(), "",
//										"Delete Failed! Invalid Request (" + li_del_res + ").", ST999, "R",
//										"Delete Failed! Invalid Request (" + li_del_res + ").").toString();
//							}
//							return common.ofGetResponseJson(new JSONArrayImpl(), "", "Success", ST0, "G", "Success")
//									.toString();
//						} else
//						{
//							reqJson.put(REQ_CD, ls_req_cd);
//							
//							deleteDtlJlist = new JSONArrayImpl();
//							deleteDtlJlist = deleteJlist;
//						}
//					}
//				//}
//					
//				// insert data in audit table REQ_JOINT_ACCOUNT_MST for ACTION---- ADD , UPD,DEL.
//
//				
//				/*
//				 * detailDataJson.put("isNewRow", insertDtlJlist);
//				 * detailDataJson.put("isDeleteRow", deleteDtlJlist);
//				 * detailDataJson.put("isUpdatedRow", updateDtlJlist);
//				 * 
//				 * if(ins_length != 0 || upd_length != 0 || del_length != 0 ) { ls_insResponse =
//				 * ofGetInsertAcctDocAuditData(connection, detailDataJson,reqJson, userJson,
//				 * "EASY_BANK.JOINT_ACCOUNT_MST", "REQ_JOINT_ACCOUNT_MST");
//				 * 
//				 * responseJson = common.ofGetJsonObject(ls_insResponse); ls_resStatus =
//				 * responseJson.getString(STATUS);
//				 * 
//				 * if (!isSuccessStCode(ls_resStatus)) { return ls_insResponse; } }
//				 */
//				
//			}
//		} catch (Exception exception) {
//		}
//		return ofGetResponseJson(new JSONArrayImpl(), "", "", ST0, "G", "").toString();
//	}
	
}
	
