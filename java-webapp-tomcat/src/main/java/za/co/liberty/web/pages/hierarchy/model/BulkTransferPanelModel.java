package za.co.liberty.web.pages.hierarchy.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import za.co.liberty.dto.agreement.maintainagreement.BulkAgreementTransferDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.dto.party.HierarchyNodeDTO;

/**
 * Panel Model for the bulk agreement transfer panel
 * @author dzs2610
 *
 */
@SuppressWarnings("unchecked")
public class BulkTransferPanelModel implements Serializable{
	private static final long serialVersionUID = 1L;

	private HierarchyNodeDTO branchFrom = new HierarchyNodeDTO();
	
	private ResultPartyDTO branchTo = new ResultPartyDTO();
	
	private List<BulkTransferGridModel> gridData;
	
	private List<BulkAgreementTransferDTO> selections = Collections.EMPTY_LIST;
	

	public List<BulkAgreementTransferDTO> getSelections() {
		return selections;
	}

	public void setSelections(List<BulkAgreementTransferDTO> selections) {
		this.selections = selections;
	}

	public List<BulkTransferGridModel> getGridData() {
		return gridData;
	}

	public void setGridData(List<BulkTransferGridModel> gridData) {
		this.gridData = gridData;
	}

	public HierarchyNodeDTO getBranchFrom() {
		return branchFrom;
	}

	public void setBranchFrom(HierarchyNodeDTO branchFrom) {
		this.branchFrom = branchFrom;
	}

	public ResultPartyDTO getBranchTo() {
		return branchTo;
	}

	public void setBranchTo(ResultPartyDTO branchTo) {
		this.branchTo = branchTo;
	}	 
}
