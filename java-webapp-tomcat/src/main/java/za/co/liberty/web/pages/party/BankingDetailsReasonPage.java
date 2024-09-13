package za.co.liberty.web.pages.party;

import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
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
public class BankingDetailsReasonPage extends BaseWindowPage {
	
	private static final long serialVersionUID = 1L;
	
	private long partyoid;	
	private ContextDTO contextDTO;
	
	private transient IPartyMaintenanceController partyMaintenanceController;
	
	private EditStateType editState;
	
	private Page parentPage;
	private BankingDetailsPanelModel pageModel;

	private ModalWindow window;
	
	private Label reasonLabel;
	private TextArea<String> reasonTextArea;
	
	public BankingDetailsReasonPage(ModalWindow window,
			BankingDetailsPanelModel pageModel) {
		this.window = window;
		this.pageModel=pageModel;
		
		add(reasonLabel = new Label("reasonLabel", "Why are you changing the accountholder of this party?"));
		add(reasonTextArea = new TextArea<String>("reasonTextArea"));
	}
	
	/* (non-Javadoc)
	 * @see za.co.liberty.web.pages.BaseWindowPage#getPageName()
	 */
	@Override
	public String getPageName() {
		return "Banking Details Reason";
	}
	
}
