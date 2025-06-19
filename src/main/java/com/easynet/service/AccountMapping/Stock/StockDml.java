package com.easynet.service.AccountMapping.Stock;

import static com.easynet.util.ConstantKeyValue.*;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.GetFuctionData;
import com.easynet.dao.GetProcData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.ProcedureConstantName;
import com.easynet.util.common;
import oracle.jdbc.OracleTypes;

@Service
public class StockDml extends DynamicDml {

	static Logger LOGGER = LoggerFactory.getLogger(StockDml.class);

	private static final String FUNC_SYS_PARA_MST_VALUE = "EASY_BANK.FUNC_SYS_PARA_MST_VALUE";

	private static final String STOCK = "EASY_BANK.STOCK";

	public static final List<String> COLUMNLIST = Arrays.asList("WITHDRAW_DT", "REMARKS", "MARGIN", "DRAWING_POWER",
			"STOCK_DESC", "ASON_DT");

	private static final String DELETE_STOCK = "DELETE FROM EASY_BANK.STOCK WHERE COMP_CD=? AND BRANCH_CD=?  AND TRAN_CD=? ";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private GetFuctionData getFuctionData;

	@Autowired
	private GetProcData getProcData;

	@Autowired
	private DrawingPowerData drawingPowerData;

	public String doStockdml(String input) {

		LoggerImpl loggerImpl = null;

		String ls_gi_comp_cd = StringUtils.EMPTY;
		String ls_gi_branch_cd = StringUtils.EMPTY;
		String ls_branch_cd = StringUtils.EMPTY;
		// String ls_through_channel = StringUtils.EMPTY;
		JSONObjectImpl reqJson;
		JSONObjectImpl userJson = null;

		boolean isNewRow, isDeleteRow;
		Connection connection = null;
		Object returnData, returnDataPara16;

		String ls_machine_nm = StringUtils.EMPTY;
		String ls_user_nm = StringUtils.EMPTY;
		String ls_response_db = StringUtils.EMPTY;
		String ls_responsedb_status = StringUtils.EMPTY;
		String ls_tran_cd = StringUtils.EMPTY;
		JSONObjectImpl response_dbJson;
		String ls_working_date = StringUtils.EMPTY;
		String ls_fmtWorkingDt = StringUtils.EMPTY;
		Object WorkingDate;

		String ls_acct_type = StringUtils.EMPTY;
		String ls_acct_cd = StringUtils.EMPTY;
		String ls_tran_amount = StringUtils.EMPTY;
		String ls_remarks = StringUtils.EMPTY;
		String ls_activity_type = StringUtils.EMPTY;
		String ls_tran_date = StringUtils.EMPTY;
		String ls_fmtTranDt = StringUtils.EMPTY;
		String ls_confrim_flag = StringUtils.EMPTY;
		String ls_user_def_remarks = StringUtils.EMPTY;
		String ls_ent_by = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;

//		ArrayList<Object> returnDataList;
//		ArrayList<Integer> procRequestDataList;
		Object tran_date, asonDate;
		String ls_asonDate = StringUtils.EMPTY;
		JSONObjectImpl DrawingPowerDataJson;

		ArrayList<Integer> procRequestDataList;
		ArrayList<Integer> returnDataList;
		String[] as_whereParainput = new String[3];

		try {

			loggerImpl = new LoggerImpl();
			connection = getDbConnection();

			loggerImpl.info(LOGGER, "preparing the request data and calling the API", "IN:doStockdml");
			loggerImpl.generateProfiler("doStockdml");
			loggerImpl.startProfiler("Preparing request Data");

			reqJson = common.ofGetJsonObject(input);
			userJson = getRequestUniqueData.getLoginUserDetailsJson();

			isNewRow = reqJson.optBoolean("_isNewRow", false);
			isDeleteRow = reqJson.optBoolean("_isDeleteRow", false);

			ls_machine_nm = getRequestUniqueData.getMachineName();

			ls_user_nm = getRequestUniqueData.getUserName();
			ls_gi_branch_cd = getRequestUniqueData.getBranchCode();
			ls_working_date = getRequestUniqueData.getWorkingDate();
			WorkingDate = getSqlDateFromString(ls_working_date);
			ls_gi_comp_cd = getRequestUniqueData.getCompCode();
			// ls_through_channel = getRequestUniqueData.getThroughChannel();

			ls_branch_cd = reqJson.getString(BRANCH_CD);
			// ls_comp_cd = reqJson.getString(COMP_CD);
			reqJson.put(COMP_CD, ls_gi_comp_cd);
			ls_asonDate = reqJson.getString(ASON_DT);
			asonDate = getSqlDateFromString(ls_asonDate);

			if (isNewRow) {

				// reqJson.put(THROUGH_CHANNEL, ls_through_channel);
				reqJson.put(ENTERED_COMP_CD, ls_gi_comp_cd);
				reqJson.put(ENTERED_BRANCH_CD, ls_gi_branch_cd);

				setSchemaName("EASY_BANK");

				returnData = getFuctionData.getAllTypeReturnValue(connection, FUNC_SYS_PARA_MST_VALUE, OracleTypes.CHAR,
						ls_gi_comp_cd, ls_gi_branch_cd, 6);

				if (returnData == null || !returnData.equals("Y")) {

					setConfirmationRules(1);
					reqJson.put(CONFIRMED, 0);
					setAllVerificationFields(false);
				} else {
					setConfirmationRules(0);
					// reqJson.remove("CONFIRMED");
					// reqJson.put(CONFIRMED, 0);
					setAllVerificationFields(true);
				}

				ls_response_db = InsertData(connection, reqJson, userJson, 1, "", STOCK);

				response_dbJson = common.ofGetJsonObject(ls_response_db);

				ls_responsedb_status = response_dbJson.getString(STATUS);

				if (!isSuccessStCode(ls_responsedb_status)) {
					connection.rollback();
					return ls_response_db;
				}

				// as per discussion with kajal mam added DP proc
				if (returnData.equals("Y")) {
					ls_acct_type = reqJson.getString(ACCT_TYPE);
					ls_acct_cd = reqJson.getString(ACCT_CD);
					DrawingPowerDataJson = drawingPowerData.ofGetDrawingPowerData(connection, ls_gi_comp_cd,
							ls_branch_cd, ls_acct_type, ls_acct_cd, asonDate);
					ls_response_db = ofGetResponseJson(new JSONArrayImpl().put(DrawingPowerDataJson), " ", "Success",
							ST0, " ", "common.success_msg").toString();
				}

				/*
				 * responseJlist = response_dbJson.getJSONArray("RESPONSE");
				 * 
				 * ls_tran_cd = responseJlist.getJSONObject(0).getString("TRAN_CD");
				 * 
				 * if (returnData.equals("Y")) {
				 * 
				 * ls_res_upd = updateData.doUpdateData(connection, UPDATESTOCK, ls_user_nm,
				 * ls_tran_cd);
				 * 
				 * resUpdateJson = common.ofGetJsonObject(ls_res_upd); ls_resUpd_status =
				 * resUpdateJson.getString(STATUS);
				 * 
				 * if (!isSuccessStCode(ls_resUpd_status)) { connection.rollback(); return
				 * ls_res_upd; } }
				 */

			} else if (isDeleteRow) {

				JSONObjectImpl mainRes = new JSONObjectImpl();

				ls_tran_cd = reqJson.getString(TRAN_CD);
				ls_acct_type = reqJson.getString(ACCT_TYPE);
				ls_acct_cd = reqJson.getString(ACCT_CD);
				ls_tran_amount = reqJson.getString(STOCK_VALUE);
				ls_activity_type = reqJson.getString(ACTIVITY_TYPE);
				ls_fmtWorkingDt = common.GetFormetedDate("yyyy-MM-dd", "yyyy-MM-dd", WorkingDate.toString());
				ls_tran_date = reqJson.getString(ENTERED_DATE);
				tran_date = getSqlDateFromString(ls_tran_date);
				ls_fmtTranDt = common.GetFormetedDate("yyyy-MM-dd", "yyyy-MM-dd", tran_date.toString());
				ls_confrim_flag = reqJson.getString(CONFIRMED);
				ls_user_def_remarks = reqJson.getString(USER_DEF_REMARKS);
				ls_ent_by = reqJson.getString(ENTERED_BY);

				if (ls_fmtWorkingDt.equals(ls_fmtTranDt)) {

//					procRequestDataList = new ArrayList<>();
//					procRequestDataList.add(OracleTypes.VARCHAR);
//					procRequestDataList.add(OracleTypes.VARCHAR);

					ls_remarks = "Delete from " + ls_activity_type;

					ls_emptyResponseData = doCheckBlankData(ls_tran_cd, ls_branch_cd, ls_gi_comp_cd, ls_acct_type,
							ls_acct_cd, ls_activity_type, ls_tran_date, ls_confrim_flag, ls_user_def_remarks);

					if (StringUtils.isNotBlank(ls_emptyResponseData))
						return ls_emptyResponseData;

					procRequestDataList = new ArrayList<>();
					procRequestDataList.add(OracleTypes.VARCHAR);
					procRequestDataList.add(OracleTypes.VARCHAR);

					ls_remarks = "Delete From " + ls_activity_type;

					returnDataList = new ArrayList<>();
					returnDataList.add(OracleTypes.VARCHAR);
					returnDataList.add(OracleTypes.VARCHAR);

					as_whereParainput[0] = ls_gi_comp_cd;
					as_whereParainput[1] = ls_branch_cd;
					as_whereParainput[2] = ls_tran_cd;

					ls_response_db = getProcData.insertAuditEntryOnDeletion(connection, DELETE_STOCK, as_whereParainput,
							ProcedureConstantName.PROC_INS_DELETE_EXCEPTION, returnDataList, ls_gi_comp_cd,
							ls_branch_cd, ls_acct_type, ls_acct_cd, ls_tran_amount, ls_gi_comp_cd, ls_gi_branch_cd,
							ls_remarks, ls_user_nm, WorkingDate, ls_activity_type, ls_machine_nm, "Delete", tran_date,
							ls_confrim_flag, ls_user_def_remarks, ls_ent_by, ls_lang);

					response_dbJson = common.ofGetJsonObject(ls_response_db);

					ls_responsedb_status = response_dbJson.getString(STATUS);

					if (!isSuccessStCode(ls_responsedb_status)) {
						connection.rollback();
						return ls_response_db;
					}

//					setSchemaName("EASY_BANK");
//
//					ls_response_db = DeleteData(connection, reqJson, userJson, STOCK);
//
//					response_dbJson = common.ofGetJsonObject(ls_response_db);
//
//					ls_responsedb_status = response_dbJson.getString(STATUS);
//
//					if (!isSuccessStCode(ls_responsedb_status)) {
//						connection.rollback();
//						return ls_response_db;
//					}
//
//					returnDataList = getProcData.getprocedureAllOutData(PROC_INS_DELETE_EXCEPTION, procRequestDataList,
//							ls_gi_comp_cd, ls_branch_cd, ls_acct_type, ls_acct_cd, ls_tran_amount, ls_gi_comp_cd,
//							ls_gi_branch_cd, ls_remarks, ls_user_nm, WorkingDate, ls_activity_type, ls_machine_nm,
//							"Delete", tran_date, ls_confrim_flag, ls_user_def_remarks, ls_ent_by, ls_lang);
//
//					ls_resStatus = (String) returnDataList.get(0);
//					ls_resMessage = (String) returnDataList.get(1);
//
//					if (!ls_resStatus.equals("0")) {
//						mainResJson = new JSONObjectImpl();
//						mainResJson.put("STATUS", ls_resStatus);
//						mainResJson.put("MESSAGE", ls_resMessage);
//
//						connection.rollback();
//						ls_response_db = ofGetResponseJson(new JSONArrayImpl().put(mainResJson), "", "", ST0, "G", "")
//								.toString();
//					}

					// added new for drawing power updation
					DrawingPowerDataJson = drawingPowerData.ofGetDrawingPowerData(connection, ls_gi_comp_cd,
							ls_branch_cd, ls_acct_type, ls_acct_cd, asonDate);

					ls_response_db = ofGetResponseJson(new JSONArrayImpl().put(DrawingPowerDataJson), " ", "Success",
							ST0, " ", "common.success_msg").toString();

				} else {
					mainRes.put("MESSAGE", "Backdated Entry Can't be Deleted.");
					mainRes.put("STATUS", 999);

					ls_response_db = ofGetResponseJson(new JSONArrayImpl().put(mainRes), " ", "Success", ST0, " ", "")
							.toString();
					return ls_response_db;
				}

			} else {

				ls_response_db = UpdateData(connection, reqJson, ls_machine_nm, ls_user_nm);

				response_dbJson = common.ofGetJsonObject(ls_response_db);

				ls_responsedb_status = response_dbJson.getString(STATUS);

				if (!isSuccessStCode(ls_responsedb_status)) {
					connection.rollback();
					return ls_response_db;
				}

				// added new for drawing power updation
				DrawingPowerDataJson = drawingPowerData.ofGetDrawingPowerData(connection, ls_gi_comp_cd, ls_branch_cd,
						ls_acct_type, ls_acct_cd, asonDate);

				ls_response_db = ofGetResponseJson(new JSONArrayImpl().put(DrawingPowerDataJson), " ", "Success", ST0,
						" ", "common.success_msg").toString();

			}

			connection.commit();
			return ls_response_db;

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doStockdml", "(ENP390)");
		} finally {
			// close the database connections object.
			closeDbObject(connection);
		}
	}

	public String UpdateData(Connection connection, JSONObjectImpl reqJson, String ls_machine_nm, String ls_user_nm)
			throws Exception {

		String ls_compCd = StringUtils.EMPTY;
		String ls_branchCd = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;

		ArrayList<Object> la_WherePara = new ArrayList<>();
		ArrayList<Object> la_SetPara = new ArrayList<>();
		Set<String> selectKeys;
		String ls_value = StringUtils.EMPTY;

		StringBuilder sbUpdQuery;
		StringBuilder sbSetQuery;
		StringBuilder sbWhereQuery;

		PreparedStatement pstmt = null;
		int parameterIndex;
		JSONArrayImpl updateColumnJlist;
		String ls_updateKey = StringUtils.EMPTY;
		parameterIndex = 1;

		updateColumnJlist = reqJson.getJSONArray("_UPDATEDCOLUMNS");

		ls_compCd = reqJson.getString(COMP_CD);
		ls_branchCd = reqJson.getString(BRANCH_CD);
		ls_tranCd = reqJson.getString(TRAN_CD);

		ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd, ls_tranCd);
		if (StringUtils.isNotBlank(ls_emptyResponseData))
			return ls_emptyResponseData;

		sbUpdQuery = new StringBuilder();
		sbSetQuery = new StringBuilder();
		sbWhereQuery = new StringBuilder();

		sbUpdQuery.append(" UPDATE  EASY_BANK.STOCK SET ");// STOCK_DESC = 'STOCK FORCEFULLY EXPIRED' ");
		sbWhereQuery.append(" WHERE COMP_CD =? AND BRANCH_CD=? AND TRAN_CD =?  ");
		la_WherePara.add(ls_compCd);
		la_WherePara.add(ls_branchCd);
		la_WherePara.add(ls_tranCd);

		selectKeys = reqJson.keySet();

		for (int i = 0; i < updateColumnJlist.length(); i++) {
			ls_updateKey = updateColumnJlist.getString(i);

			for (String key : selectKeys) {
				ls_value = reqJson.getString(key);

				if (COLUMNLIST.contains(ls_updateKey) && key.equals(ls_updateKey) && StringUtils.isNotBlank(ls_value)) {
					la_SetPara.add(ls_value);

					sbUpdQuery.append(key + " = ? , ");
				}
			}
		}

		sbSetQuery.append(
				" LAST_ENTERED_BY = ? " + " , " + " LAST_MACHINE_NM = ? " + " , " + " LAST_MODIFIED_DATE = SYSDATE ");

		la_SetPara.add(ls_user_nm);
		la_SetPara.add(ls_machine_nm);

		sbUpdQuery.append(sbSetQuery.toString().concat(sbWhereQuery.toString()));

		pstmt = connection.prepareStatement(sbUpdQuery.toString());

		for (int i = 0; i < la_SetPara.size(); i++) {
			Object object = la_SetPara.get(i);
			if (object instanceof Integer) {
				pstmt.setLong(parameterIndex, Long.valueOf((int) object));
			} else if (object instanceof Long) {
				pstmt.setLong(parameterIndex, (Long) object);
			} else if (object == null) {
				pstmt.setObject(parameterIndex, null);
			} else if (object instanceof Blob) {
				pstmt.setBlob(parameterIndex, (Blob) object);//
			} else if (object instanceof NClob) {
				pstmt.setNClob((i + 1), (NClob) object);
			} else if (object instanceof Clob) {
				pstmt.setClob(parameterIndex, (Clob) object);//
			} else if (object instanceof Double) {
				pstmt.setDouble(parameterIndex, (double) object);
			} else if (object instanceof Date) {
				pstmt.setTimestamp(parameterIndex, new java.sql.Timestamp(((Date) object).getTime()));
			} else {
				pstmt.setString(parameterIndex, (String) object);
			}

			parameterIndex += 1;

		}
		for (int j = 0; j < la_WherePara.size(); j++) {
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

			parameterIndex += 1;
		}
		int UpdatedColumn = pstmt.executeUpdate();

		if (UpdatedColumn != 1) {
			return ofGetResponseJson(new JSONArrayImpl(), "", "Delete Failed! Invalid Request", ST999, "G",
					"common.delete_fail").toString();
		}

		return ofGetResponseJson(new JSONArrayImpl(), "", "Force Expired Successfully", ST0, "G",
				"common.force_expired_suc").toString();
	}

}
