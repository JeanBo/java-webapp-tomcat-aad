package za.co.liberty.web.pages.transactions;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.transaction.ProcessAdvanceRequestDTO;
import za.co.liberty.dto.transaction.RequestTransactionDTO;
import za.co.liberty.dto.transaction.SettleRequestDTO;
import za.co.liberty.dto.transaction.VEDTransactionDTO;
import za.co.liberty.exceptions.security.TabAccessException;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.pages.request.tree.RequestTreePanel;
import za.co.liberty.web.pages.request.tree.model.RequestTreePanelModel;
import za.co.liberty.web.pages.transactions.model.RequestTransactionModel;

/**
 * Authorisation panel for transactions raised on the {@linkplain TransactionGuiPage}
 * 
 * @author jzb0608 2017/07/28
 * 
 *
 */
public class TransactionAuthorisationPanel extends BaseRequestViewAndAuthorisePanel {
	
	private static final long serialVersionUID = 1L;
	//private static Logger logger = Logger.getLogger(MaintainBusinessCardAuthorisationPanel.class);
	
	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param pageModel
	 * @param editState
	 * @throws TabAccessException 
	 */
	public TransactionAuthorisationPanel(String id, 
			ViewRequestModelDTO viewRequestPageModel) {
		super(id, viewRequestPageModel);	
	}

	/**
	 * Initialise the panels required.
	 * 
	 */
	@Override
	public List<Panel> createPanels(String id, Object imageObject) {
		List<RequestKindType> requestKindList = getPageModel().getRequestKindList();
		List<Panel> panelList = new ArrayList<Panel>();
		
		// Process Party hierarchy Details
		if (requestKindList.contains(RequestKindType.ProcessExternalPayments)) {
			RequestTransactionModel model = new RequestTransactionModel();
			model.setSelectedItem((RequestTransactionDTO) imageObject);
			Panel p = new TransactionExternalPaymentPanel(id,EditStateType.AUTHORISE, model,null);
			panelList.add(p);
			
		} else if (requestKindList.contains(RequestKindType.ProcessVariableEarningsOrDeductions)) {
			RequestTransactionModel model = new RequestTransactionModel();
			model.setSelectedItem((VEDTransactionDTO) imageObject);
			Panel p = new TransactionVariableEarningDeductionPanel(id,EditStateType.AUTHORISE, model,null);
			panelList.add(p);
			
			// Now lets add the property panel
			RequestTreePanelModel treeModel = new RequestTreePanelModel();
			treeModel.setRequestNo(getPageModel().getViewRequestContextDto().getRequestDto().getRequestId());
			panelList.add(new RequestTreePanel(id, treeModel ));
		} else if (requestKindList.contains(RequestKindType.ManualSettle)) {
			RequestTransactionModel model = new RequestTransactionModel();
			model.setSelectedItem((SettleRequestDTO) imageObject);
			Panel p = new ManualSettleTransactionPanel(id,EditStateType.AUTHORISE, model,null);
			panelList.add(p);
			
			// Now lets add the property panel
			RequestTreePanelModel treeModel = new RequestTreePanelModel();
			treeModel.setRequestNo(getPageModel().getViewRequestContextDto().getRequestDto().getRequestId());
			panelList.add(new RequestTreePanel(id, treeModel ));

			this.warn("The Request/Property Tree View is limited when selecting the 'Manual Settle' Request in context.  "
					+ "Use the 'Settle' Request for more detail.");
			
		}else if (requestKindList.contains(RequestKindType.ProcessAdvance)) {
			RequestTransactionModel model = new RequestTransactionModel();
			model.setSelectedItem((ProcessAdvanceRequestDTO) imageObject);
			Panel p = new ProcessAdvanceTransactionPanel(id,EditStateType.AUTHORISE, model,null);
			panelList.add(p);
			
			// Now lets add the property panel
			RequestTreePanelModel treeModel = new RequestTreePanelModel();
			treeModel.setRequestNo(getPageModel().getViewRequestContextDto().getRequestDto().getRequestId());
			panelList.add(new RequestTreePanel(id, treeModel ));
		}
		
		return panelList;
	}
	
}