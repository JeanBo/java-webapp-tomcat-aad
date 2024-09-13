package za.co.liberty.web.wicket.validation.maintainagreement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import za.co.liberty.business.guicontrollers.agreement.IAgreementGUIController;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.IAgreementDetailWithMyBankNumFLO;
import za.co.liberty.interfaces.persistence.party.flow.IPartyNameAgreementFLO;

/**
 * This class represents the My Banking Number Validator.
 * All validations for My Banking Number onAgreement Details GUI  is listed here
 * @author pks2802
 * @date 05/10/2009
 *
 */
public class MyBankingNumberValidator implements IValidator {
	
	private static final long serialVersionUID = 3901862672438224156L;
	
	private transient IAgreementGUIController guiController;
	
	private transient Logger logger = Logger.getLogger(MyBankingNumberValidator.class);
	
	private static final String REGEX_DIGITS_ONLY = "[\\d]+?";
	
	private Long existingAgreementId;
	
	private List<IAgreementDetailWithMyBankNumFLO> agmtForBankNums;
	
			
	public MyBankingNumberValidator(long agmtId) {
		super();
		this.existingAgreementId = agmtId;
	}

	public IAgreementGUIController getGuiController() {
		if (guiController==null) {
			try {
				guiController = ServiceLocator.lookupService(IAgreementGUIController.class);
			} catch (NamingException e) {
				logger.fatal("Could not lookup IAgreementGUIController",e);
				throw new CommunicationException("Could not lookup IAgreementGUIController",e);
			}
		}
		return guiController;
	}

	/**
	 * Validations to be done - 
	 * 1.If an invalid value submitted (not matching positive bigint), error message as follows – 
	 * "Invalid 'My Banking Number' supplied".
	 * 
	 * 2.If value supplied is in use on an agreement having a different Party, error message as 
	 * follows – "This My Banking number is in use on XXX – 99999" where XXX is name and surname of the other 
	 * Party and 99999 is that Party’s id number.

	 */
	@SuppressWarnings("unchecked")
	@Override
	public void validate(IValidatable validatable) {
		
		IPartyNameAgreementFLO partyNameFLO = null;
		long partyOid = 0l;
		String partyName = null;
		Map<String,Object> map = new HashMap<String,Object>();
		
		if (validatable==null || validatable.getValue()==null) {
			return;
		}
		
		Object obj = validatable.getValue();
		if(!(obj instanceof String))
			return;
		
		String myBankingNum = (String)obj;
		
		if(!myBankingNum.matches(REGEX_DIGITS_ONLY)){
		
			validatable.error(new ValidationError().addKey("mybankingnum.validator.invalid"));
//			error(validatable,"mybankingnum.validator.invalid",map);
			return;
		}
		
		long myBankNum = Long.valueOf(myBankingNum);
		agmtForBankNums = null;
		
		if(doesMyBankingNumAlreadyExists(myBankNum)){
			map = new HashMap<String,Object>();
			long agmtNo = 0l;
			for(IAgreementDetailWithMyBankNumFLO agreementDetailWithMyBankNumFLO :agmtForBankNums){
				agmtNo = agreementDetailWithMyBankNumFLO.getAgreementNumber();
				break;
			}
			
			try {
				partyNameFLO = getGuiController().findPartyNameForAgreement(agmtNo);
				partyOid = partyNameFLO != null?partyNameFLO.getPartyOid():0l;
				partyName = partyNameFLO != null?partyNameFLO.getName():"NOTFOUND";
			} catch (DataNotFoundException e) {
				partyOid = 0l;
				partyName = "NOTFOUND";
			}
			
			map.put("input0", partyOid);
			map.put("input1", partyName);
//			error(validatable,"mybankingnum.validator.duplicate",map);	
			validatable.error(new ValidationError().addKey("mybankingnum.validator.duplicate")
					.setVariables(map));
			return;
		}
		
			
	}

	private boolean doesMyBankingNumAlreadyExists(long myBankNum) {
		
		if(agmtForBankNums == null){
			 agmtForBankNums = getGuiController().findAgreementsForMyBankingNumbers(Arrays.asList(new Long[] {myBankNum}));
		}
		
		if(agmtForBankNums == null || agmtForBankNums.size() == 0)
			return false;
		
		for(IAgreementDetailWithMyBankNumFLO agreementDetailWithMyBankNumFLO:agmtForBankNums){
			if(agreementDetailWithMyBankNumFLO.getAgreementNumber().longValue() == existingAgreementId.longValue())
				return false;
		}
		
		return true;		
	}
	
	
}