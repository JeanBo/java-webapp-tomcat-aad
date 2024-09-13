package za.co.liberty.web.pages.baureports.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NamingException;

import za.co.liberty.business.baureports.IBAUReportsManagement;
import za.co.liberty.constants.ISRSConstants;
import za.co.liberty.dto.common.ValuesDTO;
import za.co.liberty.dto.rating.SRSDescriptionDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.AgreementKindType;
/**
 * @author PKS2802
 */
public class BAUReportsUtility implements ISRSConstants{
	
//	Added to Load the Description from ratinmg DB only once.-Performance tuning
	private static List<SRSDescriptionDTO> listDescriptionDTO;


	/**
	 * Gets the CommissionKinds List
	 */
	public static List<ValuesDTO> getAllCommisionKindsList(){

		return loadValuesDTOFromSRSDescription(RATING_COMMISIONKIND);
	}

	/**
	 * Gets the AgreementKinds List
	 */
	public static List<ValuesDTO> getAllAgreementKinds(){

		//return loadValuesDTOFromSRSDescription(RATING_AGREEMENTKIND);
		return loadAgreementKindsList();
	}

	/**
	 * Gets the ProductReferences List
	 */
	public static List<ValuesDTO> getAllProductReferences(){

		return loadValuesDTOFromSRSDescription(RATING_PRODUCT_REF_KIND);
	}

	/**
	 * Gets the Benefit Types List
	 */
	public static List<ValuesDTO> getAllBenefitTypes(){

		return loadValuesDTOFromSRSDescription(RATING_BENEFIT_TYPE);
	}

	/**
	 * Gets the Premium Frequencies List
	 */
	public static List<ValuesDTO> getAllPremFrequencies(){

		return loadValuesDTOFromSRSDescription(RATING_PREMIUM_FREQ);
	}


	/**
	 * Gets the Contribution Increase Indicator List
	 */
	public static List<ValuesDTO> getAllContribIncrIndicators(){

		return loadValuesDTOFromSRSDescription(RATING_CONTRIBINCRIND);
	}
	
	
	/**
	 * Gets the Bean From the Business layer 
	 */

	public static IBAUReportsManagement getReportsManagement() {
		IBAUReportsManagement reportsManagement;
		try {
			reportsManagement = ServiceLocator.lookupService(IBAUReportsManagement.class);
		} catch (NamingException e) {
			throw new CommunicationException(e);
		}
		
		return reportsManagement;
	}

	public static List<ValuesDTO> loadValuesDTOFromSRSDescription(String name)
	{
		if(listDescriptionDTO == null){
		
			IBAUReportsManagement reportsManagement = getReportsManagement();
			listDescriptionDTO = reportsManagement.loadDescriptionFromRating();
		}
		
		List<ValuesDTO> valuesDTOList = new ArrayList<ValuesDTO>();
		if(listDescriptionDTO != null)
		{
			for(SRSDescriptionDTO descriptionDTO:listDescriptionDTO)
			{
				if(name.equalsIgnoreCase(descriptionDTO.getName()))
				{
					ValuesDTO valuesDTO = new ValuesDTO(String.valueOf(descriptionDTO.getReferenceId()),
							descriptionDTO.getDesciption());
					valuesDTOList.add(valuesDTO);
				}

			}

		}
		
		return valuesDTOList;
	}
	
	//Validate for Text Areas to have comma seperated values
	public static boolean validateForInputTextAreas(String input) {

		String inputStr = trim(input);
		if(inputStr == null)
			return true;
		if(inputStr.indexOf(",") == -1)
		{
			try{
				Integer.parseInt(inputStr);
				return true;
			}catch (NumberFormatException ne) {
				return false;
			}
		}
			
		StringTokenizer strTokens = new StringTokenizer(inputStr,",");
		while(strTokens.hasMoreTokens())
		{
			String str = strTokens.nextToken();
			try{
				Integer.parseInt(str);
			}catch (NumberFormatException ne) {
				return false;
			}
		}
		return true;

	}
	
	public static String trim(String input)
	{
		if(input == null)
			return null;
		return input.trim();

	}
	
	//Since the AgreementKinds from the SRS.DESCRIPTION table in Rating DB is not used.
	//Retrieve from AgreementKindType enum
	private static List<ValuesDTO> loadAgreementKindsList(){
		
		List<ValuesDTO>  list = new ArrayList<ValuesDTO>();
		for(AgreementKindType agType:AgreementKindType.values())
		{
			ValuesDTO valuesDTO = new ValuesDTO(String.valueOf(agType.getKind()),agType.getDescription());
			list.add(valuesDTO);
		}
		
		return list;
	}
}
