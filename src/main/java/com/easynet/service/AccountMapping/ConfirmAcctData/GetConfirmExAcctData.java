package com.easynet.service.AccountMapping.ConfirmAcctData;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DeleteData;
import com.easynet.dao.DynamicInsertData;
import com.easynet.dao.GetFuctionData;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

import static com.easynet.util.ConstantKeyValue.*;

@Service
public class GetConfirmExAcctData extends DynamicInsertData {

	static Logger LOGGER = LoggerFactory.getLogger(GetConfirmExAcctData.class);

	private static final String SELECT_AUDIT_DATA = "SELECT REQ_CD,OWNER_NAME,TABLE_NAME,COLUMN_NAME,COLUMN_TYPE,ACCT_TYPE,ACCT_CD ,REF_SR_CD,REF_J_TYPE,REF_LINE_CD,TRAN_CD,ACTION,\r\n"
			+ " MIN(SR_CD) OLD_VAL_SR_CD ,MAX(SR_CD) NEW_VAL_SR_CD,\r\n"
			+ " FUNC_GET_UPD_FIELD_VAL(REQ_CD,MIN(SR_CD),'OLD','ACCT') OLD_VALUE,\r\n"
			+ " FUNC_GET_UPD_FIELD_VAL(REQ_CD,MAX(SR_CD),'NEW','ACCT') NEW_VALUE,COUNT(0) UPD_CNT\r\n" + "FROM \r\n"
			+ "REQ_ACCT_MST_UPD_DTL\r\n" + " WHERE\r\n" + " REQ_CD = :REQ_CD AND   			            \r\n"
			+ " TABLE_NAME = :TABLE_NAME \r\n"
			+ "GROUP BY OWNER_NAME,TABLE_NAME,COLUMN_NAME,REQ_CD,COLUMN_TYPE,ACCT_TYPE,ACCT_CD ,REF_SR_CD,REF_J_TYPE,REF_LINE_CD,TRAN_CD,ACTION";

	private static final String SELECT_ADVANCE_CONFIG_DATA = "SELECT \r\n" + "ACCT_CD ,\r\n" + "ACCT_TYPE,\r\n"
			+ "AMOUNT_UPTO,\r\n" + "BRANCH_CD,\r\n" + "CODE,\r\n" + "COMP_CD,\r\n" + "CONFIRMED,\r\n"
			+ "DEF_TRAN_CD,\r\n" + "ENTERED_BY,\r\n" + "ENTERED_DATE,\r\n" + "FLAG,\r\n" + "FORCE_EXP_BY,\r\n"
			+ "FORCE_EXP_DT,\r\n" + "FROM_EFF_DATE,\r\n" + "LAST_ENTERED_BY,\r\n" + "LAST_MACHINE_NM,\r\n"
			+ "LAST_MODIFIED_DATE,\r\n" + "MACHINE_NM,\r\n" + "SR_CD,\r\n" + "TO_EFF_DATE FROM\r\n"
			+ "REQ_ACCT_PARA_DTL WHERE REQ_CD = :REQ_CD  AND ACTION = :ACTION  ";

	private static final String SELECT_OTHER_ADDRESS_DATA = "SELECT \r\n" + "ACCT_CD,\r\n" + "ACCT_TYPE,\r\n"
			+ "ADD1,\r\n" + "ADD2,\r\n" + "ADD3,\r\n" + "ADDRESS_TYPE,\r\n" + "AREA_CD,\r\n" + "BRANCH_CD,\r\n"
			+ "CITY_CD,\r\n" + "COMP_CD,\r\n" + "CONTACT1,\r\n" + "CONTACT2,\r\n" + "CONTACT3,\r\n" + "CONTACT4,\r\n"
			+ "COUNTRY_CD,\r\n" + "DIST_CD,\r\n" + "ENTERED_BY,\r\n" + "ENTERED_DATE,\r\n" + "LAST_ENTERED_BY,\r\n"
			+ "LAST_MACHINE_NM,\r\n" + "LAST_MODIFIED_DATE,\r\n" + "MACHINE_NM,\r\n" + "PIN_CD,\r\n" + "SR_CD,\r\n"
			+ "STATE_CD\r\n" + "FROM\r\n" + "REQ_ACCT_MST_OTHER_DTL WHERE REQ_CD = :REQ_CD  AND ACTION = :ACTION ";

	private static final String SELECT_MOBILE_REGISTRATION_DATA = "SELECT \r\n" + "ACCT_CD,\r\n" +"ACCT_TYPE,\r\n"
			+ "BRANCH_CD,\r\n" + "COMP_CD,\r\n" + "ENTERED_BY,\r\n" + "ENTERED_DATE,\r\n" + "LAST_ENTERED_BY,\r\n"
			+ "LAST_MACHINE_NM,\r\n" + "LAST_MODIFIED_DATE,\r\n" + "MACHINE_NM,\r\n" + "MOBILE_NO,\r\n" + "E_MAIL_ID,\r\n"
			+ "MOBILE_REG_FLAG,\r\n" + "REG_NO,\r\n"+ "REG_TYPE,\r\n"+ "EMAIL_TYPE,\r\n" + "SR_CD FROM\r\n"
			+ "REQ_ACCT_MOBILE_DTL WHERE REQ_CD = :REQ_CD  AND ACTION = :ACTION ";

	private static final String SELECT_RELATIVE_DETAIL = "SELECT \r\n" + "ACCT_CD,\r\n" + "ACCT_TYPE,\r\n"
			+ "BRANCH_CD,\r\n" + "COMP_CD,\r\n" + "DATE_OF_BIRTH,\r\n" + "EDUCATIONAL_QUALIFICATION,\r\n" + "EMAIL,\r\n"
			+ "ENTERED_BY,\r\n" + "ENTERED_DATE,\r\n" + "GENDER,\r\n" + "LAST_ENTERED_BY,\r\n" + "LAST_MACHINE_NM,\r\n"
			+ "LAST_MODIFIED_DATE,\r\n" + "MACHINE_NM,\r\n" + "MARITAL_STATUS,\r\n" + "MONTHLY_HOUSEHOLD_INCOME,\r\n"
			+ "NAME_OF_THE_EMPLOYER,\r\n" + "NAME_OF_THE_FIRM,\r\n" + "PAN_NO,\r\n" + "PASSPORT_NO,\r\n"
			+ "RELATIVE_CD,\r\n" + "SALARIED,\r\n" + "SELF_EMPLOYED,\r\n" + "SELF_EMPLOYEED_DETAILS,\r\n" + "SR_CD\r\n"
			+ "FROM\r\n" + "REQ_ACCT_MST_RELATIVE_DTL WHERE REQ_CD = :REQ_CD  AND ACTION = :ACTION ";
	
	
	private static final String SELECT_JOINT_ACCOUNT_MST = "SELECT \r\n" + "COMP_CD,\r\n" + "BRANCH_CD,\r\n" + "ACCT_TYPE,\r\n" + "ACCT_CD,\r\n" + "SR_CD,\r\n" + "J_TYPE,\r\n" + "REF_PERSON_NAME,\r\n"
			+ "REF_COMP_CD,\r\n" + "REF_BRANCH_CD,\r\n" + "REF_ACCT_TYPE,\r\n" + "REF_ACCT_CD,\r\n" + "PHONE,\r\n" + "CITY_CD,\r\n" + "STATE_CD,\r\n"
			+ "PIN_CODE,\r\n" + "CHEQUE_YN,\r\n" + "REMARKS,\r\n" + "PATH_PHOTO,\r\n" + "PATH_SIGN,\r\n" + "BIRTH_DATE,\r\n" + "MEM_ACCT_TYPE,\r\n"
			+ "ADD1,\r\n" + "ADD2,\r\n" + "MOBILE_NO,\r\n" + "MORT_DESCRIPTION,\r\n" + "MORT_AMT,\r\n" + "AREA_CD,\r\n" + "DESIGNATION,\r\n"
			+ "ENTERED_DATE,\r\n" + "PAN_NO,\r\n" + "CUSTOMER_ID,\r\n" + "VALUER_CODE,\r\n" + "MEM_ACCT_CD,\r\n" + "MORT_NAME,\r\n" + "VALUE_AMT,\r\n"
			+ "VALUATION_DT,\r\n" + "ADVOCATE_CODE,\r\n" + "TITLE_CLEAR_DT,\r\n" + "MORT_TYPE,\r\n" + "ACTIVE_FLAG,\r\n" + "EVENT_DT,\r\n" + "DIST_CD,\r\n"
			+ "FORM_60,\r\n" + "GENDER,\r\n" + "EASY_BANK.ENCRYPT_DECRYPT.DECRYPT(UNIQUE_ID),\r\n" + "MORTGAGE_ID,\r\n" + "ADD3,\r\n" + "SECURITY_TYPE,\r\n" + "SECURITY_CD,\r\n"
			+ "NG_CUSTOMER_ID,\r\n" + "NG_NAME,\r\n" + "NG_RELATION,\r\n" + "COUNTRY_CD,\r\n" + "DIN_NO,\r\n" + "SHARE_PER\r\n"
			+ " FROM REQ_JOINT_ACCOUNT_MST WHERE REQ_CD = :REQ_CD AND ACTION = :ACTION ";

	
	private static final String SELECT_ACCT_MST_DOC_TEMPLATE = "SELECT ACCT_CD, ACCT_TYPE, ACTION, ACTIVE, BRANCH_CD,\r\n" + 
            "COMP_CD, CONFIRMED, CUSTOMER_ID, DOC_AMOUNT, DOC_NO,\r\n" + 
            "DOC_TYPE, DOC_WEIGHTAGE, ENTERED_BY, ENTERED_DATE, ENT_BRANCH_CD,\r\n" + 
            "ENT_COMP_CD, LAST_ENTERED_BY, LAST_MACHINE_NM, LAST_MODIFIED_DATE, MACHINE_NM,\r\n" + 
            "REQ_CD, REQ_FLAG, SR_CD, SUBMIT, TEMPLATE_CD,\r\n" + 
            "TRAN_CD, VALID_UPTO, VERIFIED_BY, VERIFIED_DATE, VERIFIED_MACHINE_NM\r\n" + 
            "FROM REQ_ACCT_MST_DOC_TEMPLATE WHERE REQ_CD = :REQ_CD AND ACTION = :ACTION ";

	private static final String SELECT_ACCT_MST_DOC_TEMPLATE_SDT  = "SELECT ACCT_CD, ACCT_TYPE, ACTION, BRANCH_CD, COMP_CD,\r\n" + 
            "CONFIRMED, DOC_IMAGE, DOC_TYPE, ENTERED_BY, ENTERED_DATE,\r\n" + 
            "ENT_BRANCH_CD, ENT_COMP_CD, LAST_ENTERED_BY, LAST_MACHINE_NM, LAST_MODIFIED_DATE,\r\n" + 
            "LINE_CD, MACHINE_NM, PAGE_NO, REF_CUSTOMER_ID, REF_LINE_CD,\r\n" + 
            "REF_SR_CD, REQ_CD, REQ_FLAG, SR_CD, TRAN_CD,\r\n" + 
            "VALID_UPTO, VERIFIED_BY, VERIFIED_DATE, VERIFIED_MACHINE_NM\r\n" + 
            "FROM REQ_ACCT_MST_DOC_TEMPLATE_SDT WHERE REQ_CD = :REQ_CD AND ACTION = :ACTION ";

	
	private static String DELETE_ACCT_MST_DOC_TEMPLATE_SDT = " DELETE FROM EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT WHERE COMP_CD = ? AND BRANCH_CD = ? "
			                                       + " AND ACCT_TYPE= ? AND ACCT_CD = ?  AND SR_CD = ? AND TRAN_CD = ? ";
	
	public static final List<String> TABLE_NAME_LIST = Arrays.asList("EASY_BANK.ACCT_MST","EASY_BANK.ACCT_MST_03",
			"EASY_BANK.ACCT_MST_OTHER_DTL", "EASY_BANK.ACCT_MST_RELATIVE_DTL", "EASY_BANK.ACCT_MOBILE_DTL",
			"EASY_BANK.ACCT_PARA_DTL","EASY_BANK.JOINT_ACCOUNT_MST","EASY_BANK.ACCT_MST_DOC_TEMPLATE","EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT");

	public static final List<String> ACTION_LIST = Arrays.asList("ADD", "UPD", "DEL");

	@Autowired
	private SelectData selectData;
	
	@Autowired
     private DeleteData deleteData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	LoggerImpl loggerImpl = null;

	public String doConfirmExAccountData(Connection connection, String la_confirmed, String la_user_nm,
			String la_last_machine_nm, String la_req_cd, String la_last_entered_by, String la_comp_cd,
			String la_lastModifyDt, String la_branch_cd) {
		String ls_response = StringUtils.EMPTY;
		String ls_req_cd = StringUtils.EMPTY;
		String ls_old_value = StringUtils.EMPTY;
		String ls_new_value = StringUtils.EMPTY;
		String ls_old_date = StringUtils.EMPTY;
		String ls_new_date = StringUtils.EMPTY;
		String ls_column_nm = StringUtils.EMPTY;
		String ls_column_type = StringUtils.EMPTY;
		String ls_table_nm = StringUtils.EMPTY;
		String ls_status = StringUtils.EMPTY;
		String ls_upd_table_nm = StringUtils.EMPTY;
		String ls_action = StringUtils.EMPTY;
		String ls_updSrCd = StringUtils.EMPTY;
		String ls_machineNm = StringUtils.EMPTY;
		String ls_resStatus = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_getDataByAction = StringUtils.EMPTY;
		String ls_updJ_type = StringUtils.EMPTY;
		String ls_updLine_cd = StringUtils.EMPTY;
		String ls_tran_cd = StringUtils.EMPTY;

		StringBuilder sbUpdQuery = new StringBuilder();
		StringBuilder sbSetQuery = new StringBuilder();
		StringBuilder sbWhereQuery = new StringBuilder();

		int parameterIndex, actionListSize;

		boolean lb_first;
		boolean lb_id_date = false;
		PreparedStatement pstmt = null;

		JSONObjectImpl responseDataJson = new JSONObjectImpl();
		JSONObjectImpl otherAddJson = new JSONObjectImpl();
		JSONObjectImpl userJson = new JSONObjectImpl();
		JSONObjectImpl responseJson = new JSONObjectImpl();
		JSONObjectImpl relatedPersonJson = new JSONObjectImpl();
		JSONObjectImpl reqJson = new JSONObjectImpl();
		JSONObjectImpl deleteDtlJson = new JSONObjectImpl();
		JSONArrayImpl responseJlist = new JSONArrayImpl();
		JSONArrayImpl insertJlist = new JSONArrayImpl();
		JSONArrayImpl deleteJlist = new JSONArrayImpl();
		JSONObject dbResJson;
		ArrayList<Object> la_WherePara = new ArrayList<>();
		ArrayList<Object> la_SetPara = new ArrayList<>();

		try {

			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doConfirmExAccountData");
			loggerImpl.generateProfiler("doConfirmExAccountData");
			loggerImpl.startProfiler("Preparing request data.");
			loggerImpl.startProfiler("Calling doConfirmExAccountData API response data.");

			int li_size = TABLE_NAME_LIST.size();
			actionListSize = ACTION_LIST.size();

			userJson = getRequestUniqueData.getLoginUserDetailsJson();
			ls_machineNm = getRequestUniqueData.getMachineName();
			
			for (int a = 0; a < li_size; a++) 
			{
				lb_first = true;
				deleteJlist = new JSONArrayImpl();
				ls_upd_table_nm = TABLE_NAME_LIST.get(a);

				ls_response = selectData.getSelectData(SELECT_AUDIT_DATA, la_req_cd, ls_upd_table_nm);

				responseJson = common.ofGetJsonObject(ls_response);
				ls_resStatus = responseJson.getString(STATUS);
				responseJlist = responseJson.getJSONArray("RESPONSE");

				if (!isSuccessStCode(ls_resStatus)) {
					return ls_response;
				}
				//parameterIndex = 1;

				if (isSuccessStCode(ls_resStatus) && responseJlist.length() != 0) {

					for (int k = 0; k < responseJlist.length(); k++) {
						
						dbResJson = responseJlist.getJSONObject(k);
						ls_table_nm = dbResJson.getString("TABLE_NAME");
						ls_old_value = dbResJson.getString("OLD_VALUE");
						ls_new_value = dbResJson.getString("NEW_VALUE");
						ls_column_nm = dbResJson.getString("COLUMN_NAME");
						ls_column_type = dbResJson.getString("COLUMN_TYPE");
						lb_id_date = "DATE".equalsIgnoreCase(ls_column_type);
						ls_updSrCd = dbResJson.getString("REF_SR_CD");
						ls_updJ_type = dbResJson.getString("REF_J_TYPE");
						ls_updLine_cd = dbResJson.getString("REF_LINE_CD");
						ls_action = dbResJson.getString("ACTION");
						ls_acctType = dbResJson.getString("ACCT_TYPE");
						ls_acctCd = dbResJson.getString("ACCT_CD");
						ls_tran_cd = dbResJson.getString("TRAN_CD");

						if (ls_action.equalsIgnoreCase("ADD") || ls_action.equalsIgnoreCase("DEL")) 
						{

							ls_response = insertUpdateExAcctData(connection, userJson, la_confirmed, ls_table_nm,
									ls_action, la_req_cd, ls_acctType, ls_acctCd, la_comp_cd, la_branch_cd, ls_updSrCd,ls_updJ_type,ls_tran_cd);

							responseJson = common.ofGetJsonObject(ls_response);
							ls_resStatus = responseJson.getString(STATUS);

							if (!isSuccessStCode(ls_resStatus)) {
								return ls_response;
							}
						}

						if (ls_action.equalsIgnoreCase("UPD")) 
						{					
								sbUpdQuery = new StringBuilder();
								sbSetQuery = new StringBuilder();
								sbWhereQuery = new StringBuilder();

								la_WherePara = new ArrayList<>();
								la_SetPara = new ArrayList<>();

								sbUpdQuery.append(" UPDATE " + ls_table_nm + " SET ");
								sbWhereQuery.append(" WHERE ");
								sbWhereQuery.append(" COMP_CD = ?  AND BRANCH_CD = ? AND " + "ACCT_TYPE = ? " + " AND " + " ACCT_CD  = ? ");
								la_WherePara.add(la_comp_cd);
								la_WherePara.add(la_branch_cd);
								la_WherePara.add(ls_acctType);
								la_WherePara.add(ls_acctCd);

								if (("EASY_BANK.ACCT_MST_OTHER_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MST_RELATIVE_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MOBILE_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_PARA_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.JOINT_ACCOUNT_MST".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MST_DOC_TEMPLATE".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT".equalsIgnoreCase(ls_table_nm))
										&& "UPD".equalsIgnoreCase(ls_action) && "Y".equalsIgnoreCase(la_confirmed)) {
									sbWhereQuery.append(" AND " + "SR_CD = ? ");
									la_WherePara.add(ls_updSrCd);
								}
								
								if ("EASY_BANK.JOINT_ACCOUNT_MST".equalsIgnoreCase(ls_table_nm))
								{
									sbWhereQuery.append(" AND " + "J_TYPE = ? ");
									la_WherePara.add(ls_updJ_type);
								}
								
								if ("EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT".equalsIgnoreCase(ls_table_nm))
								{
									sbWhereQuery.append(" AND " + "LINE_CD = ? ");
									la_WherePara.add(ls_updLine_cd);
								}
								
								if (ls_old_value.isEmpty()) 
								{
									sbWhereQuery.append(" AND " + ls_column_nm + " IS NULL ");
								}else
								{
									sbWhereQuery.append(" AND " + ls_column_nm + " = ? ");
									if (lb_id_date) {
										la_WherePara.add(getSqlDateFromString(ls_old_value));
									} else {
										la_WherePara.add(ls_old_value);
									}
								}
								if ("EASY_BANK.ACCT_MST_OTHER_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MST_RELATIVE_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MOBILE_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_PARA_DTL".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MST_DOC_TEMPLATE".equalsIgnoreCase(ls_table_nm)
										|| "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT".equalsIgnoreCase(ls_table_nm))
								{
									sbSetQuery.append(" LAST_ENTERED_BY = ? " + " , " + " LAST_MACHINE_NM = ? " + " , "
											+ " LAST_MODIFIED_DATE = ? " + " , ");

									la_SetPara.add(la_last_entered_by);
									la_SetPara.add(la_last_machine_nm);
									la_SetPara.add(getSqlDateFromString(la_lastModifyDt));
								}
															

								if ("EASY_BANK.ACCT_PARA_DTL".equalsIgnoreCase(ls_table_nm)) 
								{
									sbSetQuery.append(" VERIFIED_BY = ? " + " , " + " VERIFIED_DATE = SYSDATE " + " , "
											+ " VERIFIED_MACHINE_NM = ? " + " , ");
									sbSetQuery.append(" CONFIRMED =  ? " + " , " + ls_column_nm + " =  ? ");

									la_SetPara.add(la_user_nm);
									la_SetPara.add(ls_machineNm);
									la_SetPara.add(la_confirmed);
								}
								if (lb_id_date)
								{
									la_SetPara.add(getSqlDateFromString(ls_new_value));
									sbSetQuery.append( ls_column_nm + " = ? ");
								} else{
									la_SetPara.add(ls_new_value);
									sbSetQuery.append( ls_column_nm + " = ? ");
								}					
							
								
								parameterIndex = 1;
								// concat the update query.
								sbUpdQuery.append(sbSetQuery.toString().concat(sbWhereQuery.toString()));

								pstmt = connection.prepareStatement(sbUpdQuery.toString());

								for (int b = 0; b < la_SetPara.size(); b++)
								{
									Object object = la_SetPara.get(b);
									if (object instanceof Integer) {
										pstmt.setLong(parameterIndex, Long.valueOf((int) object));
									} else if (object instanceof Long) {
										pstmt.setLong(parameterIndex, (Long) object);
									} else if (object == null || (object instanceof String && ((String) object).trim().isEmpty())) {
										pstmt.setObject(parameterIndex, null);
									} else if (object instanceof Blob) {
										pstmt.setBlob(parameterIndex, (Blob) object);//
									} else if (object instanceof NClob) {
										pstmt.setNClob((b + 1), (NClob) object);
									} else if (object instanceof Clob) {
										pstmt.setClob(parameterIndex, (Clob) object);//
									} else if (object instanceof Double) {
										pstmt.setDouble(parameterIndex, (double) object);
									} else if (object instanceof Date) {
										pstmt.setTimestamp(parameterIndex, new java.sql.Timestamp(((Date) object).getTime()));
									} else {
										pstmt.setString(parameterIndex, (String) object);
									}
									parameterIndex++;
								}

								for (int j = 0; j < la_WherePara.size(); j++) 
								{
									Object object = la_WherePara.get(j);
									if (object instanceof Integer) {
										pstmt.setLong(parameterIndex, Long.valueOf((int) object));
									} else if (object instanceof Long) {
										pstmt.setLong(parameterIndex, (Long) object);
									} else if (object == null) {
										pstmt.setObject(parameterIndex, null);
									} else if (object instanceof Double) {
										pstmt.setDouble(parameterIndex, (double) object);
									} else if (object instanceof NClob) {
										pstmt.setNClob(parameterIndex, (NClob) object);
									} else if (object instanceof Date) {
										pstmt.setTimestamp(parameterIndex, new java.sql.Timestamp(((Date) object).getTime()));
									} else {
										pstmt.setString(parameterIndex, (String) object);
									}
									parameterIndex++;
								}
								int UpdatedColumn = pstmt.executeUpdate();
								
								if (UpdatedColumn != 1) 
								{
									return ofGetResponseJson(new JSONArrayImpl(), "",ls_column_nm+" records not updated. Possibly due to unmatched WHERE condition or value", ST999, "G","common.update_fail").toString();
									/*
									 * return ofGetResponseJson(new JSONArrayImpl(), "", "Data Updation Fail...",
									 * ST999, "G", "common.update_fail").toString();
									 */
								}

						}//new
					}//new
				}
			}
			
			return ofGetResponseJson(new JSONArray(), "", "", ST0, "G", "").toString();
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doConfirmExAccountData", "(ENP297)");
		} finally {
			// It's important to close the statement when you are done with
			closeDbObject(pstmt);
		}
	}

	private String insertUpdateExAcctData(Connection connection, JSONObjectImpl userJson, String la_confirmed,
			String ls_table_nm, String ls_action, String la_req_cd, String ls_acctType, String ls_acctCd,
			String la_comp_cd, String la_branch_cd, String ls_updSrCd, String ls_updJ_type,String ls_tran_cd) throws Exception {

		String ls_detailResponse = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;

		JSONArrayImpl insertJlist;
		JSONObjectImpl responseJson;
		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
		JSONObjectImpl otherAddJson = new JSONObjectImpl();
		JSONObjectImpl deleteDtlJson = new JSONObjectImpl();
		
		if (("EASY_BANK.ACCT_MST_OTHER_DTL".equalsIgnoreCase(ls_table_nm)
				|| "EASY_BANK.ACCT_MST_RELATIVE_DTL".equalsIgnoreCase(ls_table_nm)
				|| "EASY_BANK.ACCT_MOBILE_DTL".equalsIgnoreCase(ls_table_nm)
				|| "EASY_BANK.ACCT_PARA_DTL".equalsIgnoreCase(ls_table_nm)
				|| "EASY_BANK.ACCT_MST_DOC_TEMPLATE".equalsIgnoreCase(ls_table_nm)
				|| "EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT".equalsIgnoreCase(ls_table_nm)
				|| "EASY_BANK.JOINT_ACCOUNT_MST".equalsIgnoreCase(ls_table_nm)) && "Y".equalsIgnoreCase(la_confirmed))
		{

			if ("ADD".equalsIgnoreCase(ls_action)) 
			{
				setSchemaName("EASY_BANK");
				jPrimaryDtl.put(ACCT_TYPE, ls_acctType);
				jPrimaryDtl.put(ACCT_CD, ls_acctCd);
				jPrimaryDtl.put(COMP_CD, la_comp_cd);
				jPrimaryDtl.put(BRANCH_CD, la_branch_cd);
				// jPrimaryDtl.put(SR_CD, ls_updSrCd);

				if ("EASY_BANK.ACCT_MST_OTHER_DTL".equalsIgnoreCase(ls_table_nm)) {

					insertJlist = selectData.getSelectData(connection, SELECT_OTHER_ADDRESS_DATA, la_req_cd, ls_action);

					ls_detailResponse = insertDTLData(connection, insertJlist, userJson, jPrimaryDtl,
							"EASY_BANK.ACCT_MST_OTHER_DTL");

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS))) {
						return ls_detailResponse;
					}
				} else if ("EASY_BANK.ACCT_MST_RELATIVE_DTL".equalsIgnoreCase(ls_table_nm)) {

					insertJlist = selectData.getSelectData(connection, SELECT_RELATIVE_DETAIL, la_req_cd, ls_action);

					ls_detailResponse = insertDTLData(connection, insertJlist, userJson, jPrimaryDtl,
							"EASY_BANK.ACCT_MST_RELATIVE_DTL");

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS))) {
						return ls_detailResponse;
					}
				} else if ("EASY_BANK.ACCT_MOBILE_DTL".equalsIgnoreCase(ls_table_nm)) {

					insertJlist = selectData.getSelectData(connection, SELECT_MOBILE_REGISTRATION_DATA, la_req_cd,
							ls_action);

					ls_detailResponse = insertDTLData(connection, insertJlist, userJson, jPrimaryDtl,
							"EASY_BANK.ACCT_MOBILE_DTL");

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS))) {
						return ls_detailResponse;
					}
				} else if ("EASY_BANK.ACCT_PARA_DTL".equalsIgnoreCase(ls_table_nm)) {

					insertJlist = selectData.getSelectData(connection, SELECT_ADVANCE_CONFIG_DATA, la_req_cd,
							ls_action);
					// String ls_date = common.GetCurrentDate("dd/MM/yyyy");
					for (int b = 0; b < insertJlist.length(); b++) {
						otherAddJson = insertJlist.getJSONObject(b);
						otherAddJson.put("CONFIRMED", la_confirmed);
						otherAddJson.put("VERIFIED_BY", getRequestUniqueData.getUserName());
						otherAddJson.put("VERIFIED_DATE", getSqlDateFromString(getRequestUniqueData.getWorkingDate()));
						otherAddJson.put("VERIFIED_MACHINE_NM", getRequestUniqueData.getMachineName());
					}

					ls_detailResponse = insertDTLData(connection, insertJlist, userJson, jPrimaryDtl,
							"EASY_BANK.ACCT_PARA_DTL");

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS))) {
						return ls_detailResponse;
					}
				}else if ("EASY_BANK.JOINT_ACCOUNT_MST".equalsIgnoreCase(ls_table_nm)) {

					insertJlist = selectData.getSelectData(connection, SELECT_JOINT_ACCOUNT_MST, la_req_cd,ls_action);

					ls_detailResponse = insertDTLData(connection, insertJlist, userJson, jPrimaryDtl,
							"EASY_BANK.JOINT_ACCOUNT_MST");

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS))) {
						return ls_detailResponse;
					}
				}else if ("EASY_BANK.ACCT_MST_DOC_TEMPLATE".equalsIgnoreCase(ls_table_nm))
				{

					insertJlist = selectData.getSelectData(connection, SELECT_ACCT_MST_DOC_TEMPLATE, la_req_cd,ls_action);

					ls_detailResponse = insertDTLData(connection, insertJlist, userJson, jPrimaryDtl,
							"EASY_BANK.ACCT_MST_DOC_TEMPLATE");

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS)))
					{
						return ls_detailResponse;
					}
				}else if ("EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT".equalsIgnoreCase(ls_table_nm))
				{
					//DOCUMENT DTL TABLE FROM REQ TO MAIN TABLE DETAILS INSERTION WORK.

					insertJlist = selectData.getSelectData(connection, SELECT_ACCT_MST_DOC_TEMPLATE_SDT, la_req_cd,ls_action);

					
					ls_detailResponse = insertDTLData(connection, insertJlist, userJson, jPrimaryDtl,
							"EASY_BANK.ACCT_MST_DOC_TEMPLATE_SDT");

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS)))
					{
						return ls_detailResponse;
					}
				}
			}

			if ("DEL".equalsIgnoreCase(ls_action)) 
			{
				deleteDtlJson = new JSONObjectImpl();
				setSchemaName("EASY_BANK");
				deleteDtlJson.put(ACCT_TYPE, ls_acctType);
				deleteDtlJson.put(ACCT_CD, ls_acctCd);
				deleteDtlJson.put(COMP_CD, la_comp_cd);
				deleteDtlJson.put(BRANCH_CD, la_branch_cd);
				deleteDtlJson.put(SR_CD, ls_updSrCd);
				
				ls_emptyResponseData = doCheckBlankData(ls_acctType,ls_acctCd,la_comp_cd,la_branch_cd,ls_updSrCd);
				if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;

				if("EASY_BANK.JOINT_ACCOUNT_MST".equalsIgnoreCase(ls_table_nm))
				{
					deleteDtlJson.put(J_TYPE, ls_updJ_type);
				}

				if("EASY_BANK.ACCT_MST_DOC_TEMPLATE".equalsIgnoreCase(ls_table_nm))
				{
					deleteDtlJson.put(TRAN_CD, ls_tran_cd);

					ls_detailResponse =deleteData.toDeleteRow(connection, DELETE_ACCT_MST_DOC_TEMPLATE_SDT, la_comp_cd, la_branch_cd,
							ls_acctType, ls_acctCd, ls_updSrCd,ls_tran_cd);

					ls_detailResponse = DeleteData(connection, deleteDtlJson, userJson, ls_table_nm);

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS))) {
						return ls_detailResponse;
					}
				}else 
				{			
					ls_detailResponse = DeleteData(connection, deleteDtlJson, userJson, ls_table_nm);

					responseJson = common.ofGetJsonObject(ls_detailResponse);
					if (!isSuccessStCode(responseJson.getString(STATUS))) {
						return ls_detailResponse;
					}
				}

			}
		}
		return ls_detailResponse;
	}

}
