package za.co.liberty.web.pages.party;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.business.request.IRequestEnquiryManagement;
import za.co.liberty.business.request.IRequestManagement;
import za.co.liberty.dto.agreement.properties.BankingDetailsHistoryDTO;
import za.co.liberty.dto.userprofiles.ContextDTO;
import za.co.liberty.exceptions.data.QueryTimeoutException;
import za.co.liberty.exceptions.error.request.RequestException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.party.model.BankingDetailsPanelModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;

/**
 * History Page for AVS R Web Service Call
 * @author PZM2509
 *
 */
public class BankingDetailsRequestHistoryPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private long partyoid;	
	private ContextDTO contextDTO;
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	
	private EditStateType editState;
	
	private Page parentPage;
	private BankingDetailsPanelModel pageModel;

	private ModalWindow window;
	private long agmtId;
	
	private List<BankingDetailsHistoryDTO> dtoList;
	
	
	public BankingDetailsRequestHistoryPage(ModalWindow window,
			BankingDetailsPanelModel pageModel,List<BankingDetailsHistoryDTO> dtoList) {
		this.window = window;
		this.pageModel=pageModel;
		this.dtoList = dtoList;
		init();
		
	}
	
	/**
	 * Init all the page variables
	 *
	 */
	private void init(){
			
		try {
				this.dtoList = sortByVerifiedDate(dtoList);
			} catch (Exception e) {
				// Do nothing to allow current process to continue
			}
			
			this.add(getDataGrid("bankingRequestHistoryGrid"));	
	}
	
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.BaseWindowPage#getPageName()
	 */
	@Override
	public String getPageName() {
		return "Banking Request History";
	}
	
	/**
	 * Get the grid for display on the role window
	 * @return
	 */
	private SRSDataGrid getDataGrid(String id){	
		//List <BankingDetailsHistoryDTO> dtoList = getBankingRequestHistory1(); 
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new ListDataProvider<BankingDetailsHistoryDTO>(dtoList)),
				getBankingRequestColumns(), EditStateType.VIEW);
		grid.setCleanSelectionOnPageChange(false);
		grid.setClickRowToSelect(false);        
        //grid.setContentHeight(100, SizeUnit.PX);
        grid.setAllowSelectMultiple(true);
        grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
        grid.setRowsPerPage(10);
        grid.setContentHeight(170, SizeUnit.PX);
        return grid;
	}
	
	/**
	 * Get columns for grid based on type selected
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<IGridColumn> getBankingRequestColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>(4);
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("isAccountActive",new Model("Is Account Active"),"isAccountActive", EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("isAccountVerified",new Model("Is Account Verified"),"isAccountVerified",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("isIdentityNumber",new Model("Is Identity Number"),"isIdentityNumber",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("isAccountAcceptDebits",new Model("Is Account Accept Debits"),"isAccountAcceptDebits",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("isAccountAcceptCredits",new Model("Is Account Accept Credits"),"isAccountAcceptCredits",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("isaccountThreeMonths",new Model("Is Account Three Months"),"isaccountThreeMonths",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("isLastName",new Model("Is Last Name"),"isLastName",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("verifiedDate",new Model("Verified Date"),"verifiedDate",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("expiredDate",new Model("Expired Date"),"expiredDate", EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("requestorUACFID",new Model("RequestorUACF ID"),"requestorUACFID",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("authoriserUACFID",new Model("AuthoriserUACF ID"),"authoriserUACFID",EditStateType.VIEW).setInitialSize(80));
		cols.add(new SRSDataGridColumn<BankingDetailsHistoryDTO>("accountDetailsChangeReason",new Model("Reason"),"accountDetailsChangeReason",EditStateType.VIEW).setInitialSize(640));
		
		
		
		return cols;
	}
	
	/*
	 * Sort the dtoList by Verified Date
	 */
	private List<BankingDetailsHistoryDTO> sortByVerifiedDate(List<BankingDetailsHistoryDTO> unSortedList) {
		
		//Define a Comparator to compare by Verified Date
		Comparator<BankingDetailsHistoryDTO> comparator = new Comparator<BankingDetailsHistoryDTO>() {
			
			@Override
			public int compare(BankingDetailsHistoryDTO dto1, BankingDetailsHistoryDTO dto2) {
				return new Long(
						DateUtil.getInstance()
						.compareDatePart(dto1.getVerifiedDate(), dto2.getVerifiedDate())
						)
						.intValue();
			}
		};
		
		//Sort in reverse order to get descending order 
		List<BankingDetailsHistoryDTO> sortedList = (List<BankingDetailsHistoryDTO>) unSortedList.stream().sorted(comparator.reversed()).collect(Collectors.toList());
		
		return sortedList;
	}
	
}
