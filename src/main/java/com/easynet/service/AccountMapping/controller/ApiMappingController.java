package com.easynet.service.AccountMapping.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.easynet.service.AccountMapping.AccountClosedProcess.AccountClosedProcessData;
import com.easynet.service.AccountMapping.AccountClosedProcess.DoSaveAcctClosedEnt;
import com.easynet.service.AccountMapping.AccountClosedProcess.NeftDdEntry;
import com.easynet.service.AccountMapping.AccountClosedProcess.SettleCharges;
import com.easynet.service.AccountMapping.AccountDetail.*;
import com.easynet.service.AccountMapping.AccountEnquiry.*;
import com.easynet.service.AccountMapping.AcctPhotoData.ConfirmAcctPhotoData;
import com.easynet.service.AccountMapping.AcctPhotoData.SaveAcctPhotoData;
import com.easynet.service.AccountMapping.CloseAccounts.CloseAccounts;
import com.easynet.service.AccountMapping.ConfirmAcctData.*;
import com.easynet.service.AccountMapping.DropDown.GetDirectorList;
import com.easynet.service.AccountMapping.JointSecuityDetails.SecurityTypeDetailsDml;
//import com.easynet.service.AccountMapping.PassBook.GetPassbookAccountDetails;
import com.easynet.service.AccountMapping.PassBook.PassBookDataInsert;
import com.easynet.service.AccountMapping.PassBook.PassBookPrintData;
//import com.easynet.service.AccountMapping.PassBook.PassBookPrintingValidation;
import com.easynet.service.SaveAccountData.SaveAccountData;
import com.easynet.service.SaveAccountData.UpdateTabDetails;
import com.easynet.service.SaveAccountData.ValidateAccountData;
import com.easynet.service.AccountMapping.Statment.*;
import com.easynet.service.AccountMapping.Stock.DocumentDml;
//import com.easynet.service.AccountMapping.Stock.ExpiryDate;
import com.easynet.service.AccountMapping.Stock.StockDml;
//import com.easynet.service.AccountMapping.Stock.ValidateStockData;
import com.easynet.service.AccountMapping.StockConfirmation.StockConfirmation;
import com.easynet.service.AccountMapping.TemporaryOdAgainst.TempOdDml;
import com.easynet.service.AccountMapping.TemporaryOdAgainstConfirmation.TempOdConfirmation;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.CommonBase;
import com.easynet.service.AccountMapping.AccountClosedProcess.ValidateClosedBt;
import com.easynet.service.AccountMapping.AccountClosedProcessConfirmation.GetConfrimReopenDtl;

@RestController
@RequestMapping("/accountServiceAPI")
public class ApiMappingController extends CommonBase {

	private Logger LOGGER = LoggerFactory.getLogger(ApiMappingController.class);

	@Autowired
	private AccountEnquiry accountEnquiry;

	@Autowired
	private AccountDtl accountDetail;

	@Autowired
	private StockDml stockDml;

	@Autowired
	private GetAccountDetail getAccountDetail;

	@Autowired
	private ConfirmAcctData confirmAcctData;

	@Autowired
	private SaveAccountData saveAccountData;

	@Autowired
	private GetCustomerData getCustomerData;

//	@Autowired
//	private ExpiryDate expiryDate;

	@Autowired
	private DocumentDml documentDml;

	@Autowired
	private AcctCloseValidation acctCloseValidation;

//	@Autowired
//	private ValidateStockData validateStockData;

	@Autowired
	private ConfirmAcctPhotoData confirmAcctPhotoData;

	@Autowired
	private PassBookPrintData passBookPrintData;

	@Autowired
	private TempOdDml tempOdDml;

	@Autowired
	private GetDirectorList getDirectorList;

	@Autowired
	private StockConfirmation stockConfirmation;
	
//	@Autowired
//	private PassBookPrintingValidation passBookPrintingValidation;

//	@Autowired
//	private GetPassbookAccountDetails getPassbookAccountDetails;

	@Autowired
	private TempOdConfirmation tempOdConfirmation;

	@Autowired
	private PassBookDataInsert passBookDataInsert;

	@Autowired
	private AccountClosedProcessData accountClosedProcessData;

	@Autowired
	private SettleCharges settleCharges;

	@Autowired
	private ValidateClosedBt validateClosedBt;

	@Autowired
	private DoSaveAcctClosedEnt doSaveAcctClosedEnt;

	@Autowired
	private NeftDdEntry neftDdEntry;

	@Autowired
	private GetConfrimReopenDtl getConfrimReopenDtl;

	@Autowired
	private ValidateAccountData validateAccountData;

	@Autowired
	private SaveAcctPhotoData saveAcctPhotoData;
	
	@Autowired
	private UpdateTabDetails updateTabDetails;
	
	@Autowired
	private CloseAccounts closeAccount; 

	@PostMapping(value = "/GETACCTINQUIRY", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetDocuments(@RequestBody String input) {
		return accountEnquiry.ofGetAccountEnquiry(input);
	}

	@PostMapping(value = "/GETACCTDTL", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetAccountDetail(@RequestBody String input) {
		return accountDetail.getAccountDtl(input);
	}

	@PostMapping(value = "/DOSTOCKDML", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String doStockDml(@RequestBody String input) {
		return stockDml.doStockdml(input);
	}

	@PostMapping(value = "/GETACCOUNTDETAILS", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetCustAccountDetail(@RequestBody String input) {
		return getAccountDetail.getAccountDetail(input);
	}


	@PostMapping(value = "/CONFIRMACCTDATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetConfirmAcctData(@RequestBody String input) {
		return confirmAcctData.doConfirmAccountData(input);
	}

	@PostMapping(value = "/SAVEACCOUNTDATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetSaveAccountDetail(@RequestBody String input) {
		return saveAccountData.ofSaveAccountDetail(input);
	}

	@PostMapping(value = "/GETCUSTOMERDATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetCustomerDetail(@RequestBody String input) {
		return getCustomerData.getCustomerData(input);
	}

//	@PostMapping(value = "/GETEXPIRYDATE", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
//			"application/json" })
//	private final String ofGeExpiryDate(@RequestBody String input) {
//		return expiryDate.ofGetExpiryDate(input);
//	}

	@PostMapping(value = "/DOSTOCKDOCUMENTDML", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofDoDocumentDml(@RequestBody String input) {
		return documentDml.doDocumentDml(input);
	}

	@PostMapping(value = "/VALIDATEACCTDATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetValidateAcctData(@RequestBody String input) {
		return acctCloseValidation.doGetCheckAcctData(input);
	}

//	@PostMapping(value = "/VALIDATESTOCKDATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
//			"application/json" })
//	private final String DoValidateStockData(@RequestBody String input) {
//		return validateStockData.DoValidateStockData(input);
//	}

	@PostMapping(value = "/CONFIRMACCTPHOTODATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetConfirmAcctPhotoData(@RequestBody String input) {
		return confirmAcctPhotoData.doConfirmAcctPhotoData(input);
	}

	@PostMapping(value = "/GETPASSBKPRINTDATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetPassBookPrintData(@RequestBody String input) {
		return passBookPrintData.ofGetPassBookPrintData(input);
	}

	@PostMapping(value = "/DOTEMPODDML", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String DoTempOdDml(@RequestBody String input) {
		return tempOdDml.DoTempOdDml(input);
	}

	@PostMapping(value = "/GETDIRECTORLIST", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetDirectorListData(@RequestBody String input) {
		return getDirectorList.getDirectorList(input);
	}

	@PostMapping(value = "/DOSTOCKCONFIRMATION", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String doStockConfirmation(@RequestBody String input) {
		return stockConfirmation.doStockConfirmation(input);
	}

	@PostMapping(value = "/DOTEMPODCONFIRMATION", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofDoTempOdConfirmation(@RequestBody String input) {
		return tempOdConfirmation.ofDoTempOdConfirmation(input);
	}

//	@PostMapping(value = "/DOPASSBOOKPRINTINGVALIDATION", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
//			"application/json" })
//	private final String DOPASSBOOKPRINTINGVALIDATION(@RequestBody String input) {
//		return passBookPrintingValidation.doPassBookPrintingvalidation(input);
//	}

//	@PostMapping(value = "/GETPASSBOOKACCOUNTDETAILS", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
//			"application/json" })
//	private final String ofGetPassbookAccountDetails(@RequestBody String input) {
//		return getPassbookAccountDetails.doGetPassbookAccountDetails(input);
//	}

	@PostMapping(value = "/DOPASSBOOKDUPDATAINSERT", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String doPassBookDataInsert(@RequestBody String input) {
		return passBookDataInsert.doPassBookDataInsert(input);
	}

	@PostMapping(value = "/GETACCTCLOSEDPRDATA", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String getAccountClosedProcessData(@RequestBody String input) {
		return accountClosedProcessData.getAccountClosedProcessData(input);
	}

	@PostMapping(value = "/GETSETTLECHARGES", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetsettleCharges(@RequestBody String input) {
		return settleCharges.ofGetsettleCharges(input);
	}

	@PostMapping(value = "/VALIDATEACCTCLOSEBT", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetValidateClosedBtData(@RequestBody String input) {
		return validateClosedBt.ofGetValidateClosedBtData(input);
	}

	@PostMapping(value = "/DOACCTCLOSEENTRY", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String doSaveAcctClosedEnt(@RequestBody String input) {
		return doSaveAcctClosedEnt.doSaveAcctClosedEnt(input);
	}

	@PostMapping(value = "/DONEFTDDENTRY", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofDoNeftDdEntry(@RequestBody String input) {
		return neftDdEntry.ofDoNeftDdEntry(input);
	}

	@PostMapping(value = "/DOACCTCLOSECONFRIMREOPEN", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetConfrimReopenDtl(@RequestBody String input) {
		return getConfrimReopenDtl.ofGetConfrimReopenDtl(input);
	}

	@PostMapping(value = "/VALIDATEACCOUNTDTL", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String ofGetValidateAccountDtl(@RequestBody String input) {
		return validateAccountData.ofGetValidateAccountData(input);
	}

	@PostMapping(value = "/SAVEACCOUNTPHOTODTL", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
			"application/json" })
	private final String doSaveAcctPhotoDetail(@RequestBody String input) {
		return saveAcctPhotoData.doInsertUpdateAcctPhotoData(input);
	}
	
	@PostMapping(value = "/DOUPDATEMAINTABDTL", produces = { MediaType.APPLICATION_JSON_VALUE }, consumes = {
	        "application/json" })
    private final String doUpdateMainTabDetail(@RequestBody String input) {
        return updateTabDetails.doUpdateMainTabDetails(input);
    }

	 @PostMapping(value = "/DOCLOSEACCT", produces = {
	 MediaType.APPLICATION_JSON_VALUE }, consumes = { "application/json" })
	 private final String doCloseAccount(@RequestBody String input) {
	 return closeAccount.doCloseAccounts(input); }
	 

}