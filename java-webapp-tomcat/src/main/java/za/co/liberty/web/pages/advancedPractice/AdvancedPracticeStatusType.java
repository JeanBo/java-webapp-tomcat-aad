package za.co.liberty.web.pages.advancedPractice;

import java.util.ArrayList;
import java.util.List;

import za.co.liberty.database.enums.DatabaseEnumHelper;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO;
import za.co.liberty.dto.databaseenum.PartyStatusDBEnumDTO.PartyStatus;

public enum AdvancedPracticeStatusType {
	
	ACTIVE(1,"Active"),
	TERMINATED(5,"Terminated");	
	
	private static ArrayList<AdvancedPracticeStatusType> advancedPracticeStatusType;
	private int id;
	private String name;
	private PartyStatusDBEnumDTO partyStatusDBEnumDTO;

	
//	private AdvancedPracticeStatusType(long statusCode,String description){
//		this(statusCode,description);
//	}
	
	private AdvancedPracticeStatusType(int statusCode,String description){
		this.id = statusCode;
		this.name = description;		
		partyStatusDBEnumDTO = DatabaseEnumHelper.getDatabaseEnumUsingKey(PartyStatusDBEnumDTO.class, id);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}


	/**
	 * Returns only the status applicable to the FAIS details
	 * @return
	 */
	public static ArrayList<AdvancedPracticeStatusType> getStatus(){
		if(advancedPracticeStatusType == null){
			advancedPracticeStatusType = new ArrayList<AdvancedPracticeStatusType>();
			for(AdvancedPracticeStatusType status : AdvancedPracticeStatusType.values()){
				
					advancedPracticeStatusType.add(status);
				
			}
		}
		return advancedPracticeStatusType;
	}
	
	/**
	 * Returns the AdvancedPracticeStatusType that matchedthe astute name given
	 * @param astuteName
	 * @return
	 */
	public static AdvancedPracticeStatusType getStatusWithAStuteName(String astuteName){
		if(astuteName == null){
			return null;
		}
		for(AdvancedPracticeStatusType status : AdvancedPracticeStatusType.values()){
			
				return status;
			
		}
		return null;
	}
	/**
	 * Returns the AdvancedPracticeStatusType that match the Status code
	 * @param statusCode
	 * @return
	 */
	public static AdvancedPracticeStatusType getStatusWithCode(short statusCode){
		
		for(AdvancedPracticeStatusType status : AdvancedPracticeStatusType.values()){
			
				return status;
			
		}
		return null;
	}

	public PartyStatusDBEnumDTO getPartyStatusDBEnumDTO() {
		return partyStatusDBEnumDTO;
	}	
	
	/**
	 * Please fill in comments
	 * @return
	 */
	public static List<PartyStatusDBEnumDTO> getPartyStatusDBEnums(){
		ArrayList<PartyStatusDBEnumDTO> ret = new ArrayList<PartyStatusDBEnumDTO>(AdvancedPracticeStatusType.values().length);
		for(AdvancedPracticeStatusType type : AdvancedPracticeStatusType.values()){
			ret.add(type.getPartyStatusDBEnumDTO());
		}
		return ret;
	}
}
