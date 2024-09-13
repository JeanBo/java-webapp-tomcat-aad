package za.co.liberty.web.pages.party.model;

import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.party.QualificationDTO;
import za.co.liberty.web.pages.interfaces.IPanelModel;

/**
 * Panel model keeping all Qualifications details info for the panel
 *
 */
public class QualificationsPanelModel implements IPanelModel<List<QualificationDTO>> {

	private static final long serialVersionUID = 1L;
	
	
	private List<QualificationDTO> qualifications = new ArrayList<QualificationDTO>();
	

	public List<QualificationDTO> getPanelData() {
		
		return qualifications;
	}
	
	/**
	 * DEfault constructor requires the initial list to work with
	 * @param initialContactDetailsList
	 */
	public QualificationsPanelModel(List<QualificationDTO> initialQualificationsList){
		if(initialQualificationsList != null){
			qualifications = initialQualificationsList;
		}		
	}

}
