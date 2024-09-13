package za.co.liberty.web.pages.party.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.party.medicalaid.MedicalAidDetailDTO;

public class MedicalAidDetailsPanelModel implements Serializable{	
	
	private static final long serialVersionUID = 1L;

	private long currentPartyID;
	
	private long currentPartyType;
	
	private MedicalAidDetailDTO medicalAidDetailBeforeImage;
	
	private MedicalAidDetailDTO medicalAidDetail;	
	
	private List<Date> libertyMonthStartDates = new ArrayList<Date>();

	public MedicalAidDetailDTO getMedicalAidDetail() {
		return medicalAidDetail;
	}

	public void setMedicalAidDetail(MedicalAidDetailDTO medicalAidDetail) {
		this.medicalAidDetail = medicalAidDetail;
	}

	public MedicalAidDetailDTO getMedicalAidDetailBeforeImage() {
		return medicalAidDetailBeforeImage;
	}

	public void setMedicalAidDetailBeforeImage(
			MedicalAidDetailDTO medicalAidDetailBeforeImage) {
		this.medicalAidDetailBeforeImage = medicalAidDetailBeforeImage;
	}

	public long getCurrentPartyID() {
		return currentPartyID;
	}

	public void setCurrentPartyID(long currentPartyID) {
		this.currentPartyID = currentPartyID;
	}

	public List<Date> getLibertyMonthStartDates() {
		return libertyMonthStartDates;
	}

	public void setLibertyMonthStartDates(List<Date> libertyMonthStartDates) {
		this.libertyMonthStartDates = libertyMonthStartDates;
	}

	public long getCurrentPartyType() {
		return currentPartyType;
	}

	public void setCurrentPartyType(long currentPartyType) {
		this.currentPartyType = currentPartyType;
	}
}
