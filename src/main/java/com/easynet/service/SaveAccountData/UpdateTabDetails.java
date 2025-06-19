package com.easynet.service.SaveAccountData;
import java.sql.Connection;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.DynamicInsertData;
import com.easynet.dao.SelectData;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;

@Service
public class UpdateTabDetails extends DynamicDml
{
	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	@Autowired
	private UpdateData updateData;

	private Logger LOGGER = LoggerFactory.getLogger(UpdateTabDetails.class);

	public String doUpdateMainTabDetails(String input) {

		String ls_comp_cd = StringUtils.EMPTY;
		String ls_branch_cd = StringUtils.EMPTY;
		String ls_loginUser = StringUtils.EMPTY;
		String ls_workingDate = StringUtils.EMPTY;
		String ls_acctType = StringUtils.EMPTY;
		String ls_acctCd = StringUtils.EMPTY;
		String ls_tran_dt = StringUtils.EMPTY;
		String ls_column_nm = StringUtils.EMPTY;
		String ls_remarks = StringUtils.EMPTY;
		Object date;
		String ls_machine_nm = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
		ArrayList<Object> la_SetPara ;

		LoggerImpl loggerImpl = null  ;
		Connection connection = null;
		String ls_response = null;

		JSONObjectImpl reqJson;
		JSONObjectImpl userJson;
		StringBuilder updQuery;
		JSONObjectImpl rowValues;
		JSONObjectImpl JsonDatastatus;
		try {
			loggerImpl = new LoggerImpl();
			loggerImpl.info(LOGGER, "Preparing request data and calling API", "IN:doUpdateMainTabDetails");
			loggerImpl.generateProfiler("doUpdateMainTabDetails");
			loggerImpl.startProfiler("Preparing request data.");

			connection = getDbConnection();
			reqJson =  common.ofGetJsonObject(input);

			for (int i = 0; i < reqJson.getJSONArray("isUpdatedRow").length(); i++)
			{

				loggerImpl.startProfiler("Calling doUpdateMainTabDetails API response data.");

				updQuery = new StringBuilder();
				rowValues = new JSONObjectImpl();
				la_SetPara = new ArrayList<>();

				rowValues = reqJson.getJSONArray("isUpdatedRow").getJSONObject(i);

				updQuery.append("UPDATE EASY_BANK.ACCT_MST_MISC_CD_TRN SET ");

				for (int j = 0; j < rowValues.getJSONArray("_UPDATEDCOLUMNS").length(); j++) 
				{
					String setkey = rowValues.getJSONArray("_UPDATEDCOLUMNS").getString(j);					
					updQuery.append(setkey).append(" = ? , ");
					la_SetPara.add(rowValues.getString(setkey));
				}
				updQuery.append("MODIFIED_BY=?, MODIFIED_DATE =?, LAST_MACHINE_NM =? "
						+ "WHERE  COMP_CD = :COMP_CD AND BRANCH_CD = :BRANCH_CD AND ACCT_TYPE = :ACCT_TYPE "
						+ "AND ACCT_CD = :ACCT_CD AND TRAN_DT = :TRAN_DT AND COLUMN_NM = :COLUMN_NM ");

				userJson = getRequestUniqueData.getLoginUserDetailsJson();
				ls_loginUser = getRequestUniqueData.getUserName();
				ls_workingDate = getRequestUniqueData.getWorkingDate();		
				date = getSqlDateFromString(ls_workingDate);
				ls_machine_nm =	getRequestUniqueData.getMachineName();
				ls_comp_cd = rowValues.getString("COMP_CD");
				ls_branch_cd = rowValues.getString("BRANCH_CD");
				ls_acctType = rowValues.getString("ACCT_TYPE");
				ls_acctCd = rowValues.getString("ACCT_CD");
				ls_tran_dt = rowValues.getString("TRAN_DT");
				ls_remarks = rowValues.getString("REMARKS");
				ls_column_nm = rowValues.getString("COLUMN_NM");

				ls_emptyResponseData = doCheckBlankData(ls_comp_cd, ls_branch_cd, ls_acctType, ls_acctCd,ls_workingDate,ls_tran_dt,ls_column_nm);

				if (StringUtils.isNotBlank(ls_emptyResponseData))
					return ls_emptyResponseData;

				la_SetPara.add(ls_loginUser);
				la_SetPara.add(date);
				la_SetPara.add(ls_machine_nm);
				la_SetPara.add(ls_comp_cd);
				la_SetPara.add(ls_branch_cd);
				la_SetPara.add(ls_acctType);
				la_SetPara.add(ls_acctCd);
				la_SetPara.add(ls_tran_dt);
				la_SetPara.add(ls_column_nm);

				ls_response = updateData.doUpdateData(connection,updQuery.toString(),la_SetPara.toArray());

				JsonDatastatus = common.ofGetJsonObject(ls_response);

				if(!isSuccessStCode(JsonDatastatus.getString("STATUS"))){
					connection.rollback();
					return ls_response;
				}
			}
			
			connection.commit();
			return ls_response;
			
		} catch (Exception exception) {
			return getExceptionMSg(exception, LOGGER, loggerImpl, "IN:doUpdateMainTabDetails", "(ENP656)");
		}finally {
			closeDbObject(connection);
		}
	}
}
