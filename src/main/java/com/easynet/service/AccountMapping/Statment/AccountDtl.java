package com.easynet.service.AccountMapping.Statment;

import static com.easynet.util.ConstantKeyValue.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.dao.GetFuctionData;
import com.easynet.dao.GetProcData;
import com.easynet.dao.SelectData;
import com.easynet.impl.JSONArrayImpl;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.util.common;

@Service
public class AccountDtl extends CommonBase {

	static Logger LOGGER=LoggerFactory.getLogger(AccountDtl.class);

	private static final String ACCOUNTDTL="EASY_BANK.PACK_STMT_OF_ACCT.PROC_STMT_OF_ACCT_DTL";

	private static final String FUNC_GET_ACCT_SNAPSHOT_DTL="PACK_DAILY_TRN_F1.FUNC_GET_ACCT_SNAPSHOT_DTL";

	private static final String BRANCHDTL="EASY_BANK.PACK_STMT_OF_ACCT.PROC_STMT_OF_ACCT_BRANCH_DTL";

	private static final String ACCTSUM="EASY_BANK.PACK_STMT_OF_ACCT.PROC_STMT_OF_ACCT_SUMMARY";

	private static final String META_DATA="EASY_BANK.PACK_STMT_OF_ACCT.PROC_STMT_OF_ACCT_METADATA";

	private static final String NOTES="EASY_BANK.PACK_STMT_OF_ACCT.PROC_STMT_OF_ACCT_NOTES";

	private static final String  FULLACCTNO="SELECT RPAD(TRIM(EASY_BANK.FUNC_GET_INWARD_ACCT_NO(:A_FULL_ACCT_NO,'B')),4,' ') AS BRANCH_CD,\r\n"
			+ "       RPAD(TRIM(EASY_BANK.FUNC_GET_INWARD_ACCT_NO(:A_FULL_ACCT_NO,'T')),4,' ') AS ACCT_TYPE,\r\n"
			+ "       RPAD(TRIM(EASY_BANK.FUNC_GET_INWARD_ACCT_NO(:A_FULL_ACCT_NO,'N')),20,' ') AS ACCT_CD\r\n"
			+ "FROM DUAL";

	@Autowired
	private  GetProcData getProcData;
	
	@Autowired
	private  GetFuctionData getFuctionData;

	@Autowired 
	private SelectData selectData;

	@Autowired
	private GetRequestUniqueData getRequestUniqueData;

	public String getAccountDtl (String input) {

		LoggerImpl loggerImpl=null;
		String ls_comp_cd		=StringUtils.EMPTY;
		String ls_branch_cd	=StringUtils.EMPTY;
		String ls_acct_type	=StringUtils.EMPTY;
		String ls_acct_cd		=StringUtils.EMPTY;
		String ls_fr_dt		=StringUtils.EMPTY;
		String ls_to_dt		=StringUtils.EMPTY;
		String ls_meta			=StringUtils.EMPTY;
		String ls_emptyResponse=StringUtils.EMPTY;

		String ls_workingDate = StringUtils.EMPTY;		
		String ls_base_branch_cd = StringUtils.EMPTY;
		String ls_userName = StringUtils.EMPTY;
		String ls_userRole = StringUtils.EMPTY;
		String ls_lang = StringUtils.EMPTY;
		String ls_screen_ref = StringUtils.EMPTY;

		String ls_response_acct_dtl	=StringUtils.EMPTY;
		String ls_response_acct_tran	=StringUtils.EMPTY;
		String ls_response_branch_dtl	=StringUtils.EMPTY;
		String ls_response_acct_sum	=StringUtils.EMPTY;
		String ls_response_notes		=StringUtils.EMPTY;

		String ls_status_acct_dtl 		= StringUtils.EMPTY;
		String ls_status_acct_tran 	= StringUtils.EMPTY;
		String ls_status_acct_sum 		= StringUtils.EMPTY;
		String ls_status_branch_dtl	= StringUtils.EMPTY;
		String ls_status_notes			= StringUtils.EMPTY;


		String ls_sum_value	=StringUtils.EMPTY;
		String ls_sum_label	=StringUtils.EMPTY;
		String ls_sum_curr		=StringUtils.EMPTY; 		 

		String ls_response_acct_meta	=StringUtils.EMPTY;
		String ls_status_acct_meta 	= StringUtils.EMPTY;

		String ls_full_acct_no 	= StringUtils.EMPTY;
		String ls_acct_no_db		=StringUtils.EMPTY; 
		String ls_acct_no_status		=StringUtils.EMPTY; 		
		JSONObjectImpl acctnodbJson,acct_no_reqJson;
		JSONArrayImpl acctnodbJList;

		JSONArrayImpl dbMetaJList,dbnotesJList,dbJList;
		JSONObjectImpl requestJsonData;
		JSONObjectImpl dbJson,dbMetaJson,dbNotesJson,resNotesJson;

		JSONArrayImpl mainJlist =null;
		mainJlist=new JSONArrayImpl();

		int i,j,TranListlen,sumListlen;

		JSONObjectImpl re_dtlJson,dbTranJson,resTranJson;	 																															
		JSONArrayImpl dbTranJList,dbbranchJList;
		JSONObjectImpl  dbBranchJson,resBranchJson;
		JSONObjectImpl  dbSumJson,resSumJson,sumJson;
		JSONArrayImpl dbSumJList,sumJList;
		Object date;
		try {
			loggerImpl = new LoggerImpl();

			loggerImpl.info(LOGGER, "preparing the request data and calling the API", "IN:getAccountDtl");
			loggerImpl.generateProfiler("getAccountDtl");
			loggerImpl.startProfiler("Preparing request Data");

			requestJsonData=common.ofGetJsonObject(input);

			ls_base_branch_cd = requestJsonData.getString("A_BASE_BRANCH");
			ls_workingDate = requestJsonData.getString("A_GD_DATE");
			date = getSqlDateFromString(ls_workingDate);
			ls_userName = requestJsonData.getString("A_USER_NM");
			ls_userRole = requestJsonData.getString("A_USER_LEVEL");
			ls_screen_ref=requestJsonData.getString("A_SCREEN_REF");
			ls_lang = requestJsonData.getString("A_LANG");

			ls_comp_cd=getRequestUniqueData.getCompCode();
			ls_fr_dt=requestJsonData.getString(FROM_DT);
			ls_to_dt=requestJsonData.getString(TO_DT);
			ls_meta=requestJsonData.getString(METADATA);
			ls_full_acct_no=requestJsonData.getString(FULL_ACCT_NO);

			ls_emptyResponse=doCheckBlankData(ls_comp_cd,ls_fr_dt,ls_to_dt,ls_meta);
			if(StringUtils.isNotBlank(ls_emptyResponse)) return ls_emptyResponse;

			loggerImpl.startProfiler("Preparing response Data");

			if(StringUtils.isNotEmpty(ls_full_acct_no))
			{
				ls_acct_no_db= selectData.getSelectData(FULLACCTNO,ls_full_acct_no,ls_full_acct_no,ls_full_acct_no);

				acctnodbJson = common.ofGetJsonObject(ls_acct_no_db);

				ls_acct_no_status=acctnodbJson.getString(STATUS);

				if (!isSuccessStCode(ls_acct_no_status)) {
					return ls_acct_no_db;
				}
				acctnodbJList = acctnodbJson.getJSONArray("RESPONSE");
				acct_no_reqJson=acctnodbJList.getJSONObject(0);

				ls_branch_cd=acct_no_reqJson.getString(BRANCH_CD);
				ls_acct_type=acct_no_reqJson.getString(ACCT_TYPE);
				ls_acct_cd=acct_no_reqJson.getString(ACCT_CD);

				ls_emptyResponse=doCheckBlankData(ls_branch_cd,ls_acct_type,ls_acct_cd);
				if(StringUtils.isNotBlank(ls_emptyResponse)) return ls_emptyResponse;

			}else
			{
				ls_branch_cd= requestJsonData.getString(BRANCH_CD);
				ls_acct_type=requestJsonData.getString(ACCT_TYPE);
				ls_acct_cd=requestJsonData.getString(ACCT_CD);

				ls_emptyResponse=doCheckBlankData(ls_branch_cd,ls_acct_type,ls_acct_cd);
				if(StringUtils.isNotBlank(ls_emptyResponse)) return ls_emptyResponse;

			}

			ls_response_acct_dtl= getProcData.getCursorData(ACCOUNTDTL,ls_comp_cd,ls_branch_cd,ls_acct_type,ls_acct_cd,ls_fr_dt,ls_to_dt);

			dbJson = common.ofGetJsonObject(ls_response_acct_dtl);
			ls_status_acct_dtl= dbJson.getString(STATUS);

			if (isSuccessStCode(ls_status_acct_dtl)) {

				dbJList = dbJson.getJSONArray("RESPONSE");
				re_dtlJson = new JSONObjectImpl();

				re_dtlJson=getNewJson(dbJList);

				re_dtlJson.put("TITLE","Account Details");
				re_dtlJson.put("DISPLAY_TYPE","simple");
				re_dtlJson.put("IS_DEFAULT_OPEN",true);

				mainJlist.put(re_dtlJson);
			}else{
				return ls_response_acct_dtl;
			}


			ls_response_branch_dtl= getProcData.getCursorData(BRANCHDTL,ls_comp_cd,ls_branch_cd);

			dbBranchJson = common.ofGetJsonObject(ls_response_branch_dtl);
			ls_status_branch_dtl = dbBranchJson.getString(STATUS);

			resBranchJson = new JSONObjectImpl();

			if (isSuccessStCode(ls_status_branch_dtl)) {

				dbbranchJList = dbBranchJson.getJSONArray("RESPONSE");

				resBranchJson=getNewJson(dbbranchJList);

				resBranchJson.put("TITLE","Branch Details");
				resBranchJson.put("DISPLAY_TYPE","simple");
				resBranchJson.put("IS_DEFAULT_OPEN",false);
				mainJlist.put(resBranchJson);
			}else {
				return ls_response_branch_dtl;
			}


			ls_response_acct_sum= getProcData.getCursorData(ACCTSUM,ls_comp_cd,ls_branch_cd,ls_acct_type,ls_acct_cd,ls_fr_dt,ls_to_dt);

			dbSumJson=common.ofGetJsonObject(ls_response_acct_sum);
			ls_status_acct_sum = dbSumJson.getString(STATUS);

			resSumJson = new JSONObjectImpl();
			sumJList=new JSONArrayImpl();

			if (isSuccessStCode(ls_status_acct_sum)) {

				dbSumJList=dbSumJson.getJSONArray("RESPONSE");
				sumListlen=dbSumJList.length();

				for(j=0;j<sumListlen;j++) {
					ls_sum_value=dbSumJList.getJSONObject(j).getString("DISPL_VALUE");
					ls_sum_label=dbSumJList.getJSONObject(j).getString("DISPL_LABEL");
					ls_sum_curr=dbSumJList.getJSONObject(j).getString( "DISPL_CURR");
					sumJson=new JSONObjectImpl();
					sumJson.put("VALUE", ls_sum_value);
					sumJson.put("LABEL", ls_sum_label);
					sumJson.put("CURRENCY", ls_sum_curr);
					if(ls_sum_label.equals("Debit Count")||ls_sum_label.equals("Credit Count")){
						sumJson.remove("CURRENCY");
					}
					sumJList.put(sumJson);
				}
				resSumJson.put("TITLE","Statement Summary");
				resSumJson.put("DISPLAY_TYPE","simpleGrid");
				resSumJson.put("IS_DEFAULT_OPEN",false);
				resSumJson.put("DETAILS", sumJList);
				mainJlist.put(resSumJson);
			}else{
				return ls_response_acct_sum;
			}


			ls_response_acct_tran= getFuctionData.getCursorData(FUNC_GET_ACCT_SNAPSHOT_DTL,ls_comp_cd,ls_branch_cd,ls_acct_type,ls_acct_cd,ls_fr_dt,ls_to_dt,ls_base_branch_cd,ls_userName,ls_workingDate,ls_userRole,ls_screen_ref,ls_lang);
			dbTranJson=common.ofGetJsonObject(ls_response_acct_tran);

			ls_status_acct_tran= dbTranJson.getString(STATUS);

			JSONArrayImpl tran_newJList;
			tran_newJList= new JSONArrayImpl();

			JSONObjectImpl tran_newJson;
			tran_newJson = new JSONObjectImpl();

			if (isSuccessStCode(ls_status_acct_tran)) {

				dbTranJList=dbTranJson.getJSONArray("RESPONSE");
				TranListlen=dbTranJList.length();

				for( i=0;i<TranListlen;i++)	{
					resTranJson=dbTranJList.getJSONObject(i);
					tran_newJList.put(resTranJson);
				}
				tran_newJson.put("DATA",tran_newJList );

			}else{
				return ls_response_acct_tran;
			}


			ls_response_acct_meta= getProcData.getCursorData(META_DATA,ls_comp_cd,ls_branch_cd,ls_acct_type,ls_acct_cd,ls_fr_dt,ls_to_dt,ls_meta);
			dbMetaJson=common.ofGetJsonObject(ls_response_acct_meta);

			ls_status_acct_meta= dbMetaJson.getString(STATUS);

			if (isSuccessStCode(ls_status_acct_meta)) {

				dbMetaJList=dbMetaJson.getJSONArray("RESPONSE");			
				tran_newJson.put("TITLE", "Account Statement");
				tran_newJson.put("ROW_ID_COLUMN","TRAN_CD" );
				tran_newJson.put("ENABLE_PAGINATION",true);
				tran_newJson.put("DISPLAY_TYPE", "grid");
				tran_newJson.put("IS_DEFAULT_OPEN",true);

				tran_newJson.put("METADATA",dbMetaJList );

				mainJlist.put(tran_newJson);
			}else{
				return ls_response_acct_meta;
			}


			ls_response_notes=getProcData.getCursorData(NOTES,ls_comp_cd,ls_branch_cd,ls_acct_type,ls_acct_cd,ls_fr_dt,ls_to_dt);
			dbNotesJson=common.ofGetJsonObject(ls_response_notes);

			ls_status_notes= dbNotesJson.getString(STATUS);

			resNotesJson = new JSONObjectImpl();

			if (isSuccessStCode(ls_status_notes)) {

				dbnotesJList = dbNotesJson.getJSONArray("RESPONSE");

				resNotesJson=getNewJson(dbnotesJList);

				resNotesJson.put("TITLE","Notes");
				resNotesJson.put("DISPLAY_TYPE","OnlyExport");
				resNotesJson.put("IS_DEFAULT_OPEN",false);
				mainJlist.put(resNotesJson);
			}else {
				return ls_response_notes;
			}

			//return common.ofGetResponseJson(mainJlist, "", "Success", ST0,"G","Success").toString();
			return ofGetResponseJson(mainJlist, "", "Success", ST0,"G","common.success_msg").toString();

		}catch(Exception exception)
		{
			return getExceptionMSg(exception, LOGGER, loggerImpl,"IN:getAccountDtl","ENP213");
		}

	}

	public JSONObjectImpl getNewJson (JSONArrayImpl JList) 
	{
		String ls_value=StringUtils.EMPTY;
		String ls_label=StringUtils.EMPTY;

		int i;
		int JListlen;

		JSONObjectImpl newJson;
		JSONArrayImpl newJList=null;
		JSONObjectImpl re_new_Json=null;
		newJList=new JSONArrayImpl();
		re_new_Json = new JSONObjectImpl();

		JListlen=JList.length();

		for( i=0;i<JListlen;i++)
		{
			ls_value= JList.getJSONObject(i).getString("DISPL_VALUE");
			ls_label= JList.getJSONObject(i).getString("DISPL_LABEL");
			newJson = new JSONObjectImpl();
			newJson.put("VALUE", ls_value);
			newJson.put("LABEL", ls_label);
			newJList.put(newJson);
		}

		re_new_Json.put("DETAILS",newJList );

		return re_new_Json; 
	}

}
