package za.co.liberty.web.pages.taxdetails.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.party.taxdetails.DirectivesDTO;
import za.co.liberty.dto.party.taxdetails.LabourBrokersDTO;
import za.co.liberty.dto.party.taxdetails.TaxDetailsDTO;
import za.co.liberty.dto.party.taxdetails.TrustCompDTO;
import za.co.liberty.dto.party.taxdetails.VatDTO;
import za.co.liberty.dto.party.taxdetails.BBBEEDTO;

/**
 * Panel model keeping all tax details info for the panel<br/>
 * <strong>NOTE: the collection sent in through the constructor is only added to when getPanelData() is called<strong>
 * 
 * @author MZP0801
 *
 */

public class TaxDetailsPanelModel  implements Serializable{ 
	
	private static final long serialVersionUID = 1L;
	
	private TaxDetailsDTO taxDetailsDTO;
	private TaxDetailsDTO taxDetailsBeforeImage;
	
	
	
	public TaxDetailsDTO getTaxDetailsBeforeImage() {
		return taxDetailsBeforeImage;
	}


	public TaxDetailsDTO getTaxDetailsDTO() {
		return taxDetailsDTO;
	}


	public void setTaxDetailsBeforeImage(TaxDetailsDTO taxDetailsBeforeImage) {
		this.taxDetailsBeforeImage = taxDetailsBeforeImage;
	}


	public void setTaxDetailsDTO(TaxDetailsDTO taxDetailsDTO) {
		this.taxDetailsDTO = taxDetailsDTO;
	}
	
}
