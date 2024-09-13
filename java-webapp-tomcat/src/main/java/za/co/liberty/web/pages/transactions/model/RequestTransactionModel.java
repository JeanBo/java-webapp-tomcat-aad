package za.co.liberty.web.pages.transactions.model;

import java.io.Serializable;
import java.util.List;

import za.co.liberty.dto.transaction.RequestTransactionDTO;
import za.co.liberty.web.data.enums.RequestTransactionKindType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

/**
 * Data model for transaction requests.
 * 
 * @author JZB0608 - 2017-05-29
 *
 */
public class RequestTransactionModel implements Serializable, IMaintenancePageModel<RequestTransactionDTO>  {

	private static final long serialVersionUID = 1L;
	
	private RequestTransactionDTO selectedItem;
	private List<RequestTransactionDTO> selectionList;
	private List<RequestTransactionKindType> requestTransactionKindTypeList;
	
	private RequestTransactionKindType selectedBoxName;
	
	private Object panelModel;
	
	
	public RequestTransactionKindType getSelectedBoxName() {
		return selectedBoxName;
	}

	public void setSelectedBoxName(RequestTransactionKindType selectedBoxName) {
		this.selectedBoxName = selectedBoxName;
	}

	public RequestTransactionDTO getSelectedItem() {
		return selectedItem;
	}

	public List<RequestTransactionDTO> getSelectionList() {
		return selectionList;
	}

	public void setSelectedItem(RequestTransactionDTO selected) {
		this.selectedItem = selected;
	}

	public void setSelectionList(List<RequestTransactionDTO> selectionList) {
		this.selectionList = selectionList;
	}

	public List<RequestTransactionKindType> getRequestTransactionKindTypeList() {
		return requestTransactionKindTypeList;
	}

	public void setRequestTransactionKindTypeList(
			List<RequestTransactionKindType> requestTransactionKindTypeList) {
		this.requestTransactionKindTypeList = requestTransactionKindTypeList;
	}

	public Object getPanelModel() {
		return panelModel;
	}

	public void setPanelModel(Object panelModel) {
		this.panelModel = panelModel;
	}


}
