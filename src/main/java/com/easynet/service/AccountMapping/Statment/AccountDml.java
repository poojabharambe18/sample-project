package com.easynet.service.AccountMapping.Statment;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.easynet.dao.DynamicDml;
import com.easynet.dao.UpdateData;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.ConstantKeyValue.*;

import java.sql.Connection;
import java.util.Date;

@Component
public class AccountDml extends DynamicDml {

	private static final String UPDATESTATEMNET = "UPDATE EASY_BANK.ACCT_MST SET LST_STATEMENT_DT=? WHERE COMP_CD=? AND BRANCH_CD=? AND ACCT_TYPE=? AND ACCT_CD=?";
	private static final String UPDATEPASSBKDTL = "UPDATE EASY_BANK.ACCT_MST SET PASS_BOOK_DT=? ,PASS_BOOK_LINE=?  WHERE COMP_CD=? AND BRANCH_CD=? AND ACCT_TYPE=? AND ACCT_CD=?";

	static Logger LOGGR = LoggerFactory.getLogger(AccountDml.class);

	@Autowired
	private UpdateData updateData;

	public String DoAccountDml(Connection connection,String ls_compCd,String ls_branchCd,String ls_acctType,String ls_acctCd,String ls_tranFlag,Object toDate,String ls_passBkLine ) throws Exception {
	
		String ls_response_data = StringUtils.EMPTY;
		String ls_emptyResponseData = StringUtils.EMPTY;
	
			/*
			 * resJson = common.ofGetJsonObject(input);
			 * 
			 * ls_compCd = resJson.getString(COMP_CD); ls_branchCd =
			 * resJson.getString(BRANCH_CD); ls_acctType = resJson.getString(ACCT_TYPE);
			 * ls_acctCd = resJson.getString(ACCT_CD); ls_tranFlag =
			 * resJson.getString(TRAN_FLAG);
			 * 
			 * setSchemaName("EASY_BANK");
			 */
		
			ls_emptyResponseData = doCheckBlankData(ls_compCd, ls_branchCd, ls_acctType, ls_acctCd);
			if (StringUtils.isNotBlank(ls_emptyResponseData))
			return ls_emptyResponseData;

			if (ls_tranFlag.equals("S")) {
			
				ls_response_data = updateData.doUpdateData(connection,UPDATESTATEMNET, toDate, ls_compCd, ls_branchCd,
						ls_acctType, ls_acctCd);

			} else {
				ls_response_data = updateData.doUpdateData(connection,UPDATEPASSBKDTL, toDate, ls_passBkLine, ls_compCd,
						ls_branchCd, ls_acctType, ls_acctCd);
			}

			/*
			 * configJson=setConfigurationJson("M", "EASY_BANK.ACCT_MST", "", "2", "", "5",
			 * "-2", "5");
			 * 
			 * ls_response_data=MasterDML(input, configJson);
			 */

			return ls_response_data;

		
	}

}
