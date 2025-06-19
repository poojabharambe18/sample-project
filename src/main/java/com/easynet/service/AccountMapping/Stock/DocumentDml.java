package com.easynet.service.AccountMapping.Stock;

import java.sql.Connection;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import static com.easynet.util.ConstantKeyValue.*;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.dao.UpdateData;

@Service
public class DocumentDml extends DynamicDml {

	static Logger LOGGER = LoggerFactory.getLogger(DocumentDml.class);

	private static final String UPDATE = "UPDATE EASY_BANK_LOB.CBS_DOC_UPLOAD_MST SET ACTIVE =? , "
			+ "LAST_ENTERED_BY = ?,LAST_MACHINE_NM = ?,LAST_MODIFIED_DATE = SYSDATE WHERE ENTERED_COMP_CD=? AND ENTERED_BRANCH_CD=? AND TRAN_CD=? AND SR_CD=?";

	private static final String SELECT = "SELECT \r\n" + "MAX(D.TRAN_CD) AS TRAN_CD,\r\n" + "MAX(D.SR_CD) AS SR_CD,\r\n"
			+ "D.REF_TRAN_CD\r\n" + "FROM EASY_BANK_LOB.CBS_DOC_UPLOAD_MST  D, EASY_BANK.STOCK S\r\n" + "WHERE\r\n"
			+ "S.ENTERED_COMP_CD= D.ENTERED_COMP_CD  AND\r\n" + "S.ENTERED_BRANCH_CD =D.ENTERED_BRANCH_CD  AND\r\n"
			+ "S.ACCT_TYPE= D.ACCT_TYPE  AND\r\n" + "S.ACCT_CD =D.ACCT_CD  AND\r\n" + "S.TRAN_CD= D.REF_TRAN_CD AND\r\n"
			+ "(S.ENTERED_COMP_CD =:ENTERED_COMP_CD ) AND\r\n" + "(S.ENTERED_BRANCH_CD=:ENTERED_BRANCH_CD )AND \r\n"
			+ "(S.ACCT_TYPE =:ACCT_TYPE ) AND\r\n" + "(S.ACCT_CD=:ACCT_CD )AND \r\n" + "(S.TRAN_CD =:TRAN_CD ) \r\n"
			+ "GROUP BY S.TRAN_CD,D.REF_TRAN_CD\r\n";

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private UpdateData updateData;

	@Autowired
	private SelectData selectData;

	public String doDocumentDml(String input) {
		String response_data = StringUtils.EMPTY;
		LoggerImpl loggerImpl = null;
		Connection connection = null;
		long MaxTran_cd;
		JSONObjectImpl requestDataJson;

		JSONObjectImpl jDetails;
		JSONArrayImpl jInsertRow, jUpdateRow;
		JSONObjectImpl newRequestJson, userJson, response_dbJson, resUpdateJson;
		String ls_responsedb_status = StringUtils.EMPTY;
		String ls_req_upd = StringUtils.EMPTY;
		String ls_resUpdateStatus = StringUtils.EMPTY;

		String ls_entCompCd = StringUtils.EMPTY;
		String ls_entBranchCd = StringUtils.EMPTY;
		String ls_tranCd = StringUtils.EMPTY;
		String ls_srCd = StringUtils.EMPTY;
		String ls_active = StringUtils.EMPTY;
		String ls_userNm = StringUtils.EMPTY;
		String ls_machineNm = StringUtils.EMPTY;
		JSONObjectImpl mainResJson, UpdRequestJson, resDataJson;

		String ls_ResData = StringUtils.EMPTY;
		String ls_resDataStatus = StringUtils.EMPTY;
		String ls_exTranCd = StringUtils.EMPTY;
		String ls_exSrCd = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;

		JSONArrayImpl resDatajList;
		int exSrCd = 0;
		int sr_cd = 0;

		try {
			loggerImpl = new LoggerImpl();
			setSchemaName("EASY_BANK_LOB");
			connection = getDbConnection();

			requestDataJson = common.ofGetJsonObject(input);
			userJson = getRequestUniqueData.getLoginUserDetailsJson();

			ls_userNm = getRequestUniqueData.getUserName();
			ls_machineNm = getRequestUniqueData.getMachineName();

			jDetails = requestDataJson.getJSONObject("DETAILS_DATA");
			jInsertRow = jDetails.getJSONArray("isNewRow");
			jUpdateRow = jDetails.getJSONArray("isUpdatedRow");

			mainResJson = new JSONObjectImpl();

			if (jInsertRow.length() > 0) {

				ls_entCompCd = requestDataJson.getString(ENTERED_COMP_CD);
				ls_entBranchCd = requestDataJson.getString(ENTERED_BRANCH_CD);
				ls_acctType = requestDataJson.getString(ACCT_TYPE);
				ls_acctCd = requestDataJson.getString(ACCT_CD);
				ls_tranCd = requestDataJson.getString(TRAN_CD);

				MaxTran_cd = getMaxCd(connection, "EASY_BANK_LOB.CBS_DOC_UPLOAD_MST", TRAN_CD, 1, "");

				ls_ResData = selectData.getSelectData(SELECT, ls_entCompCd, ls_entBranchCd, ls_acctType, ls_acctCd,
						ls_tranCd);

				resDataJson = common.ofGetJsonObject(ls_ResData);

				ls_resDataStatus = resDataJson.getString(STATUS);

				if (!isSuccessStCode(ls_resDataStatus)) {
					return ls_ResData;
				}

				resDatajList = resDataJson.getJSONArray("RESPONSE");

				if (resDatajList.length() != 0) {

					ls_exTranCd = resDatajList.getJSONObject(0).getString("TRAN_CD");
					ls_exSrCd = resDatajList.getJSONObject(0).getString("SR_CD");
					exSrCd = Integer.parseInt(ls_exSrCd);
					mainResJson.put("TRAN_CD", ls_exTranCd);
				} else {
					mainResJson.put("TRAN_CD", MaxTran_cd);
				}

				for (int i = 0; i < jInsertRow.length(); i++) {
					sr_cd++;
					exSrCd++;
					newRequestJson = jInsertRow.getJSONObject(i);

					if (StringUtils.isNotBlank(ls_exTranCd)) { // && StringUtils.isNotBlank(ls_exSrCd)
						newRequestJson.put("TRAN_CD", ls_exTranCd);
						newRequestJson.put("SR_CD", exSrCd);

					} else {
						newRequestJson.put("TRAN_CD", MaxTran_cd);
						newRequestJson.put("SR_CD", sr_cd);
					}
					newRequestJson.put("TABLE_NM", "stock");
					newRequestJson.put("REF_SR_CD", 1);

					response_data = InsertData(connection, newRequestJson, userJson, 2, "",
							"EASY_BANK_LOB.CBS_DOC_UPLOAD_MST");

					response_dbJson = common.ofGetJsonObject(response_data);

					ls_responsedb_status = response_dbJson.getString(STATUS);

					if (!isSuccessStCode(ls_responsedb_status)) {
						connection.rollback();
						return response_data;
					}
				}
			}

			if (jUpdateRow.length() > 0) {
				for (int i = 0; i < jUpdateRow.length(); i++) {
					UpdRequestJson = jUpdateRow.getJSONObject(i);

					ls_entCompCd = UpdRequestJson.getString(ENTERED_COMP_CD);
					ls_entBranchCd = UpdRequestJson.getString(ENTERED_BRANCH_CD);
					ls_tranCd = UpdRequestJson.getString(TRAN_CD);
					ls_srCd = UpdRequestJson.getString(SR_CD);
					ls_active = UpdRequestJson.getString(ACTIVE);

					mainResJson.put("TRAN_CD", ls_tranCd);

					setSchemaName("EASY_BANK");
					
					ls_emptyResponseData = doCheckBlankData(ls_entCompCd, ls_entBranchCd, ls_tranCd, ls_srCd);
					if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;

					ls_req_upd = updateData.doUpdateData(connection, UPDATE, ls_active, ls_userNm, ls_machineNm,
							ls_entCompCd, ls_entBranchCd, ls_tranCd, ls_srCd);

					resUpdateJson = common.ofGetJsonObject(ls_req_upd);

					ls_resUpdateStatus = resUpdateJson.getString(STATUS);

					if (!isSuccessStCode(ls_resUpdateStatus)) {
						connection.rollback();
						return ls_req_upd;
					}
				}
			}
//			confJson = setConfigurationJson("D","","EASY_BANK_LOB.CBS_DOC_UPLOAD_MST","2","","-2","-2","5");			
//			response_data = DetailDML(requestDataJson.toString(), confJson);

			mainResJson.put("ENTERED_COMP_CD", ls_entCompCd);
			mainResJson.put("ENTERED_BRANCH_CD", ls_entBranchCd);

			connection.commit();
			return ofGetResponseJson(new JSONArrayImpl().put(mainResJson), "", "", ST0, "G", "").toString();

		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doDocumentDml", "(ENP298)");
		} finally {
			closeDbObject(connection);
		}
	}
}