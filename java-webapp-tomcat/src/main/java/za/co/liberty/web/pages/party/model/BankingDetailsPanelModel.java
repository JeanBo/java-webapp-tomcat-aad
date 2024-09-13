package za.co.liberty.web.pages.party.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.party.taxdetails.DirectivesDTO;
import za.co.liberty.dto.party.taxdetails.LabourBrokersDTO;
import za.co.liberty.dto.party.taxdetails.TaxDetailsDTO;
import za.co.liberty.dto.party.taxdetails.TrustCompDTO;
import za.co.liberty.dto.party.taxdetails.VatDTO;
import za.co.liberty.dto.agreement.properties.BankingDetailsHistoryDTO;
import za.co.liberty.dto.party.bankingdetail.BankingDetailsDTO;
import za.co.liberty.dto.party.taxdetails.BBBEEDTO;

/**
 * Panel model keeping all Banking details info for the panel<br/>
 * <strong>NOTE: the collection sent in through the constructor is only added to when getPanelData() is called<strong>
 * 
 * @author PZM2509
 *
 */

public class BankingDetailsPanelModel  implements Serializable{ 
	
	private static final long serialVersionUID = 1L;
	
	private BankingDetailsDTO bankingDetailsDTO;
	private BankingDetailsDTO bankingDetailsBeforeImage;
	private List<BankingDetailsHistoryDTO> bankingDetailsHistoryList;
	
	
	public BankingDetailsPanelModel() {
		setBankingDetailsHistoryList(new ArrayList<BankingDetailsHistoryDTO> ());
	}
	
	
	
	public BankingDetailsDTO getBankingDetailsBeforeImage() {
		return bankingDetailsBeforeImage;
	}


	public BankingDetailsDTO getBankingDetailsDTO() {
		return bankingDetailsDTO;
	}


	public void setBankingDetailsBeforeImage(BankingDetailsDTO bankingDetailsBeforeImage) {
		this.bankingDetailsBeforeImage = bankingDetailsBeforeImage;
	}


	public void setBankingDetailsDTO(BankingDetailsDTO bankingDetailsDTO) {
		this.bankingDetailsDTO = bankingDetailsDTO;
	}
	
	public List<BankingDetailsHistoryDTO> getBankingDetailsHistoryList(){
		return bankingDetailsHistoryList;
	}
	
	private void setBankingDetailsHistoryList(
			List<BankingDetailsHistoryDTO> bankingDetailsHistoryList) {
		this.bankingDetailsHistoryList = bankingDetailsHistoryList;
	}
	
}
