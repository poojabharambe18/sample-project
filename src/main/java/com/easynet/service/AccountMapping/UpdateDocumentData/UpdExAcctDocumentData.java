//package com.easynet.service.AccountMapping.UpdateDocumentData;
//
//import static com.easynet.util.ConstantKeyValue.*;
//
//import java.sql.Connection;
//import java.util.Set;
//
//import org.apache.commons.lang3.StringUtils;
//import org.json.JSONArray;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Service;
//
//import com.easynet.bean.GetRequestUniqueData;
//import com.easynet.dao.DynamicInsertData;
//import com.easynet.dao.SelectData;
//import com.easynet.dao.UpdateData;
//import com.easynet.impl.JSONArrayImpl;
//import com.easynet.impl.JSONObjectImpl;
//import com.easynet.impl.LoggerImpl;
//import com.easynet.service.SaveAccountData.UpdateExAccountData;
//import com.easynet.util.common;
//
//@Service
//public class UpdExAcctDocumentData extends DynamicInsertData {
//
//	static Logger LOGGER = LoggerFactory.getLogger(UpdExAcctDocumentData.class);
//
//
//
//	private static final String UPDATE_ENTRY_TYPE = "  UPDATE  REQ_ACCT_MST_HDR SET ENTRY_TYPE = :ENTRY_TYPE WHERE  REQ_CD = :REQ_CD  ";
//
//	@Autowired
//	private UpdateData updateData;
//
//	@Autowired
//	UpdateExAccountData updateExAccountData;
//
//	@Autowired
//	private SelectData selectData;
//
//	@Autowired
//	private GetRequestUniqueData getRequestUniqueData;
//
//	public String doUpdateExCustDocumentData(JSONObjectImpl requestDataJson, long ll_ENTRY_TYPE) {
//
//		LoggerImpl loggerImpl = null;
//
//		String ls_com_cd = StringUtils.EMPTY;
//		String ls_branch_cd = StringUtils.EMPTY;
//		String ls_resStatus = StringUtils.EMPTY;
//		String ls_response = StringUtils.EMPTY;
//		String ls_insResponse = StringUtils.EMPTY;
//		String ls_req_cd = StringUtils.EMPTY;
//		String ls_auditResponse = StringUtils.EMPTY;
//		String ls_custId = StringUtils.EMPTY;
//		String ls_is_from_main = StringUtils.EMPTY;
//		String ls_is_fromReq = StringUtils.EMPTY;
//		String ls_updResponse = StringUtils.EMPTY;
//
//		boolean lb_isMainDataAdd;
//		boolean lb_isMainDataUpd;
//		boolean lb_isMainDataDel;
//
//		int li_insLength;
//		int li_updLength;
//		int li_delLength;
//
//		Connection connection = null;
//
//		JSONObjectImpl documentDtlJson = new JSONObjectImpl();
//		JSONObjectImpl responseJson = new JSONObjectImpl();
//		JSONObjectImpl responseDataJson;
//		JSONObjectImpl userJson;
//		JSONObjectImpl resDataJson = new JSONObjectImpl();
//		JSONObjectImpl detailDataJson = new JSONObjectImpl();
//		JSONObjectImpl masterDataJson = new JSONObjectImpl();
//		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
//
//		JSONArrayImpl jallColumnList = new JSONArrayImpl();
//		JSONArrayImpl dbResponseJList = new JSONArrayImpl();
//		JSONArrayImpl insertJlist = new JSONArrayImpl();
//		JSONArrayImpl deleteJlist = new JSONArrayImpl();
//		JSONArrayImpl updateJlist = new JSONArrayImpl();
//		JSONArrayImpl docDtlJlist = new JSONArrayImpl();
//
//		try {
//
//			loggerImpl = new LoggerImpl();
//			connection = getDbConnection();
//
//			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doUpdateExCustDocumentData");
//			loggerImpl.generateProfiler("doUpdateExCustDocumentData");
//			loggerImpl.startProfiler("Preparing request data.");
//
//			userJson = getRequestUniqueData.getLoginUserDetailsJson();
//			ls_com_cd = getRequestUniqueData.getCompCode();
//			ls_branch_cd = getRequestUniqueData.getBranchCode();
//
//			loggerImpl.startProfiler("Calling doUpdateExCustDocumentData API response data.");
//
//			docDtlJlist = requestDataJson.getJSONArray("DOC_MST");
//
//
//			// INSERT DATA IN REQ_ACCT_MST_DOC_TEMPLATE AND REQ_ACCT_MST_DOC_TEMPLATE_SDT
//			// TABLE.
//
//			for (int k = 0; k < docDtlJlist.length(); k++) {
//
//				masterDataJson = docDtlJlist.getJSONObject(k);
//				ls_is_from_main = masterDataJson.getString(IS_FROM_MAIN);
//				ls_is_fromReq = masterDataJson.getString(NEW_FLAG);
//
//				if (masterDataJson.has("_isNewRow")) {
//					lb_isMainDataAdd = masterDataJson.getBoolean("_isNewRow");
//
//					if (lb_isMainDataAdd) {
//
//						ls_auditResponse = updateExAccountData.ofGetCheckDocumentDetailData(connection, requestDataJson,
//								masterDataJson, userJson, true, "ADD", ls_is_from_main,
//								getRequestUniqueData.getCompCode(), getRequestUniqueData.getBranchCode(),
//								requestDataJson.getString(REQ_CD), requestDataJson.getString(REQ_FLAG),
//								requestDataJson.getString(CUSTOMER_ID), requestDataJson.getString(ACCT_TYPE),
//								requestDataJson.getString(ACCT_CD), ls_is_fromReq, ll_ENTRY_TYPE);
//
//						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_auditResponse);
//						if (!isSuccessStCode(insertResJson.getString(STATUS))) {
//							return ls_auditResponse;
//						}
//					} else {
//						// update main detail only insert data in req_doc_template_dtl table.ACTION_TYPE
//						// = M AND ACTION = UPD
//						ls_auditResponse = updateExAccountData.ofGetCheckDocumentDetailData(connection, requestDataJson,
//								masterDataJson, userJson, true, "UPD", ls_is_from_main,
//								getRequestUniqueData.getCompCode(), getRequestUniqueData.getBranchCode(),
//								requestDataJson.getString(REQ_CD), requestDataJson.getString(REQ_FLAG),
//								requestDataJson.getString(CUSTOMER_ID), requestDataJson.getString(ACCT_TYPE),
//								requestDataJson.getString(ACCT_CD), ls_is_fromReq, ll_ENTRY_TYPE);
//
//						JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_auditResponse);
//						if (!isSuccessStCode(insertResJson.getString(STATUS))) {
//							return ls_auditResponse;
//						}
//					}
//
//				} else if (masterDataJson.has("_isDeleteRow")) {
//
//					lb_isMainDataDel = masterDataJson.getBoolean("_isDeleteRow");
//
//					ls_auditResponse = updateExAccountData.ofGetCheckDocumentDetailData(connection, requestDataJson,
//							masterDataJson, userJson, lb_isMainDataDel, "DEL", ls_is_from_main,
//							getRequestUniqueData.getCompCode(), getRequestUniqueData.getBranchCode(),
//							requestDataJson.getString(REQ_CD), requestDataJson.getString(REQ_FLAG),
//							requestDataJson.getString(CUSTOMER_ID), requestDataJson.getString(ACCT_TYPE),
//							requestDataJson.getString(ACCT_CD), ls_is_fromReq, ll_ENTRY_TYPE);
//
//					JSONObjectImpl insertResJson = common.ofGetJsonObject(ls_auditResponse);
//					if (!isSuccessStCode(insertResJson.getString(STATUS))) {
//						return ls_auditResponse;
//
//					}
//				} else {
//					return ofGetResponseJson(new JSONArray(), "", "Invalid Request ", ST99, "R",
//							"common.invalid_req_data").toString();
//				}
//			}
//			JSONObjectImpl auditResJson = common.ofGetJsonObject(ls_auditResponse);
//			if (isSuccessStCode(auditResJson.getString(STATUS))) {
//				connection.commit();
//				return ofGetResponseJson(new JSONArrayImpl().put(resDataJson), "", " ", ST0, "G", "").toString();
//			} else {
//				connection.rollback();
//				return ls_auditResponse;
//			}
//		} catch (Exception exception) {
//			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAuditData", "(ENP245)");
//		} finally {
//			closeDbObject(connection);
//		}
//
//	}
//
//	public String ofGetInsertDocumentAuditData(Connection connection, JSONObjectImpl reqJson,
//			JSONObjectImpl documentJson, JSONObjectImpl userJson, String la_mst_tablename, String la_req_tablename,
//			String la_req_cd, String la_is_from_main) {
//		LoggerImpl loggerImpl = null;
//		String ls_column_type = StringUtils.EMPTY;
//		String ls_value = StringUtils.EMPTY;
//		String ls_customer_id = StringUtils.EMPTY;
//		String ls_cust_type = StringUtils.EMPTY;
//		String ls_old_value = StringUtils.EMPTY;
//		String ls_new_value = StringUtils.EMPTY;
//		String ls_upd_column = StringUtils.EMPTY;
//		String ls_entry_type = StringUtils.EMPTY;
//		String ls_audit_response = StringUtils.EMPTY;
//
//		int li_sr_cd;
//
//		JSONArrayImpl insertJlist = new JSONArrayImpl();
//		JSONArrayImpl insertAuditJlist = new JSONArrayImpl();
//		JSONArrayImpl updateAuditJlist = new JSONArrayImpl();
//		JSONArrayImpl deleteAuditJlist = new JSONArrayImpl();
//		JSONArrayImpl updateJlist = new JSONArrayImpl();
//		JSONArrayImpl updValueJlist = new JSONArrayImpl();
//		JSONArrayImpl deleteJlist = new JSONArrayImpl();
//		JSONArrayImpl responseJlist = new JSONArrayImpl();
//
//		JSONObjectImpl insertJson = new JSONObjectImpl();
//		JSONObjectImpl auditResJson = new JSONObjectImpl();
//		JSONObjectImpl jPrimaryDtl = new JSONObjectImpl();
//		JSONObjectImpl mainDtlJson = new JSONObjectImpl();
//		JSONObjectImpl oldValueJson = new JSONObjectImpl();
//		JSONObjectImpl insertAuditJson = new JSONObjectImpl();
//		JSONObjectImpl updateJson = new JSONObjectImpl();
//		JSONObjectImpl updateAuditJson = new JSONObjectImpl();
//		JSONObjectImpl deleteJson = new JSONObjectImpl();
//		JSONObjectImpl deleteAuditJson = new JSONObjectImpl();
//
//		Set<String> insertJsonKeys;
//
//		try {
//
//			insertJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isNewRow");
//			updateJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isUpdatedRow");
//			deleteJlist = documentJson.getJSONObject("DETAILS_DATA").getJSONArray("isDeleteRow");
//
//			int ins_length = insertJlist.length();
//			int upd_length = updateJlist.length();
//			int del_length = deleteJlist.length();
//
//			jPrimaryDtl.put(REQ_CD, la_req_cd);
//
//			JSONArrayImpl jallColumnList = getColumnDefination(REQ_ACCT_MST_UPD_DTL, connection, true);
//			long ll_entry_type = getMaxSrCd(connection, REQ_ACCT_MST_UPD_DTL, ENTRY_TYPE, jallColumnList, jPrimaryDtl);
//			ll_entry_type = ll_entry_type + 1;
//
//			long ll_SrCd = getMaxSrCd(connection, REQ_ACCT_MST_UPD_DTL, SR_CD, jallColumnList, jPrimaryDtl);
//			ll_SrCd = ll_SrCd + 1;
//
//			if (ins_length != 0) {
//
//				for (int i = 0; i < ins_length; i++) {
//
//					insertJson = new JSONObjectImpl();
//
//					insertJson = insertJlist.getJSONObject(i);
//					insertJsonKeys = insertJson.keySet();
//
//					for (String keyName : insertJsonKeys) {
//
//						// get column type .
//						insertAuditJson = new JSONObjectImpl();
//						ls_column_type = updateExAccountData.ofGetColumnDataType(connection, la_req_tablename, keyName);
//
//						if (!keyName.equals("DOC_IMAGE")) {
//							ls_value = insertJson.getString(keyName);
//						} else {
//							ls_value = " ";
//						}
//						insertAuditJson.put("OWNER_NAME", "EASY_BANK");
//						insertAuditJson.put("TABLE_NAME", "EASY_BANK.ACCT_MST_DOC_TEMPLATE");
//						insertAuditJson.put("REQ_CD", la_req_cd);
//						insertAuditJson.put("ENTRY_TYPE", ll_entry_type);
//						insertAuditJson.put("SR_CD", ll_SrCd);
//						insertAuditJson.put("CUSTOMER_ID", insertJson.getString(CUSTOMER_ID));
//						insertAuditJson.put("REF_SR_CD", insertJson.getString(SR_CD));
//						insertAuditJson.put("OLD_VALUE", " ");
//						insertAuditJson.put("COLUMN_TYPE", ls_column_type);
//						insertAuditJson.put("ACTION", "ADD");
//						insertAuditJson.put("TRAN_CD", insertJson.getString(TRAN_CD));
//						insertAuditJson.put("NEW_VALUE", ls_value);
//						insertAuditJson.put("COLUMN_NAME", keyName);
//
//						insertAuditJlist.put(insertAuditJson);
//						ll_SrCd++;
//					}
//				}
//				jPrimaryDtl.put(REQ_CD, la_req_cd);
//				ls_audit_response = insertDTLData(connection, insertAuditJlist, userJson, jPrimaryDtl,
//						REQ_ACCT_MST_UPD_DTL);
//
//				auditResJson = common.ofGetJsonObject(ls_audit_response);
//				if (!isSuccessStCode(auditResJson.getString(STATUS))) {
//					return ls_audit_response;
//				}
//
//			}
//			if (upd_length != 0) {
//
//				for (int i = 0; i < upd_length; i++) {
//
//					updateJson = new JSONObjectImpl();
//
//					updateJson = updateJlist.getJSONObject(i);
//					oldValueJson = updateJson.getJSONObject("_OLDROWVALUE");
//					updValueJlist = updateJson.getJSONArray("_UPDATEDCOLUMNS");
//					ls_customer_id = reqJson.getString("CUSTOMER_ID");
//					ls_cust_type = reqJson.getString(REQ_FLAG);
//
//					for (int k = 0; k < updValueJlist.length(); k++) {
//
//						updateAuditJson = new JSONObjectImpl();
//						ls_upd_column = updValueJlist.getString(k);
//
//						if (oldValueJson.has(ls_upd_column)) {
//
//							if (!ls_upd_column.equals("DOC_IMAGE")) {
//								ls_old_value = oldValueJson.getString(ls_upd_column);
//								ls_new_value = updateJson.getString(ls_upd_column);
//							} else {
//								ls_old_value = " ";
//								ls_new_value = " ";
//							}
//							ls_column_type = updateExAccountData.ofGetColumnDataType(connection, la_req_tablename,
//									ls_upd_column);
//
//							updateAuditJson.put(COLUMN_NAME, ls_upd_column);
//							updateAuditJson.put(ENTRY_TYPE, ll_entry_type);
//							updateAuditJson.put("NEW_VALUE", ls_new_value);
//							updateAuditJson.put("OLD_VALUE", ls_old_value);
//							updateAuditJson.put("OWNER_NAME", "EASY_BANK");
//							updateAuditJson.put(CUSTOMER_ID, ls_customer_id);
//							updateAuditJson.put("REF_SR_CD", updateJson.getString("SR_CD"));
//							updateAuditJson.put("TRAN_CD", updateJson.getString(TRAN_CD));
//							updateAuditJson.put(REQ_CD, la_req_cd);
//							updateAuditJson.put("ACTION", "UPD");
//							updateAuditJson.put(COLUMN_TYPE, ls_column_type);
//							updateAuditJson.put(SR_CD, ll_SrCd);
//							updateAuditJson.put("TABLE_NAME", la_mst_tablename);
//
//							updateAuditJlist.put(updateAuditJson);
//							ll_SrCd++;
//						}
//					}
//				}
//				jPrimaryDtl.put(REQ_CD, la_req_cd);
//				ls_audit_response = insertDTLData(connection, updateAuditJlist, userJson, jPrimaryDtl,
//						REQ_ACCT_MST_UPD_DTL);
//
//				auditResJson = common.ofGetJsonObject(ls_audit_response);
//				if (!isSuccessStCode(auditResJson.getString(STATUS))) {
//					return ls_audit_response;
//				}
//
//			}
//			if (del_length != 0) {
//
//				for (int i = 0; i < del_length; i++) {
//
//					deleteJson = new JSONObjectImpl();
//					deleteAuditJson = new JSONObjectImpl();
//
//					deleteJson = deleteJlist.getJSONObject(i);
//
//					deleteAuditJson.put(COLUMN_NAME, "TEST");
//					deleteAuditJson.put(ENTRY_TYPE, ll_entry_type);
//					deleteAuditJson.put("NEW_VALUE", "");
//					deleteAuditJson.put("OLD_VALUE", "");
//					deleteAuditJson.put("OWNER_NAME", "EASY_BANK");
//					deleteAuditJson.put(CUSTOMER_ID, ls_customer_id);
//					deleteAuditJson.put("REF_SR_CD", deleteJson.getString("SR_CD"));
//					deleteAuditJson.put("TRAN_CD", deleteJson.getString(TRAN_CD));
//					deleteAuditJson.put(REQ_CD, la_req_cd);
//					deleteAuditJson.put("ACTION", "DEL");
//					deleteAuditJson.put(COLUMN_TYPE, "");
//					deleteAuditJson.put(SR_CD, ll_SrCd);
//					deleteAuditJson.put("TABLE_NAME", la_mst_tablename);
//
//					deleteAuditJlist.put(deleteAuditJson);
//					ll_SrCd++;
//				}
//				jPrimaryDtl.put(REQ_CD, la_req_cd);
//				ls_audit_response = insertDTLData(connection, deleteAuditJlist, userJson, jPrimaryDtl,
//						REQ_ACCT_MST_UPD_DTL);
//
//				auditResJson = common.ofGetJsonObject(ls_audit_response);
//				if (!isSuccessStCode(auditResJson.getString(STATUS))) {
//					return ls_audit_response;
//				}
//
//			}
//
//			// check response after perform dml operation on audit table.
//			auditResJson = common.ofGetJsonObject(ls_audit_response);
//			if (isSuccessStCode(auditResJson.getString(STATUS))) {
//				connection.commit();
//				return ls_audit_response;
//			} else {
//				connection.rollback();
//				return ls_audit_response;
//			}
//
//		} catch (Exception exception) {
//			return getExceptionMSg(exception, LOGGER, loggerImpl, "ofGetInsertAuditData", "(ENP245)");
//		}
//
//	}
//
//}
