package za.co.liberty.web.pages.salesBCLinking.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import za.co.liberty.dto.agreement.SalesBCLinking.LinkedAdviserDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.PanelAdviserSearchDTO;
import za.co.liberty.dto.agreement.SalesBCLinking.SalesPanelDTO;

public class SalesBCLinkingPanelModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SalesPanelDTO salesPanelDTO;
	/*
	 * List to hold the advisers that are a result of the SRS Agreement,Thirteen
	 * Digit Code or the Panel search
	 */
	private List<LinkedAdviserDTO> searchAdvisersList;
	/* List to hold the advisers who are to be newly linked to the panel */
	private List<LinkedAdviserDTO> newLinkAdvisersList;

	private Date endDate;
	private Date newStartDate;
	
	private PanelAdviserSearchDTO panelAdviserSearchDTO;

	public SalesPanelDTO getSalesPanelDTO() {
		return salesPanelDTO;
	}

	public void setSalesPanelDTO(SalesPanelDTO salesPanelDTO) {
		this.salesPanelDTO = salesPanelDTO;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getNewStartDate() {
		return newStartDate;
	}

	public void setNewStartDate(Date newStartDate) {
		this.newStartDate = newStartDate;
	}

	public List<LinkedAdviserDTO> getSearchAdvisersList() {
		if (searchAdvisersList == null) {
			return new ArrayList<LinkedAdviserDTO>();
		}
		return searchAdvisersList;
	}

	public void setSearchAdvisersList(List<LinkedAdviserDTO> searchAdvisersList) {
		this.searchAdvisersList = searchAdvisersList;
	}

	public List<LinkedAdviserDTO> getNewLinkAdvisersList() {
		return newLinkAdvisersList;
	}

	public void setNewLinkAdvisersList(
			List<LinkedAdviserDTO> newLinkAdvisersList) {
		this.newLinkAdvisersList = newLinkAdvisersList;
	}

	public PanelAdviserSearchDTO getPanelAdviserSearchDTO() {
		return panelAdviserSearchDTO;
	}

	public void setPanelAdviserSearchDTO(PanelAdviserSearchDTO panelAdviserSearchDTO) {
		this.panelAdviserSearchDTO = panelAdviserSearchDTO;
	}
	
	

}
