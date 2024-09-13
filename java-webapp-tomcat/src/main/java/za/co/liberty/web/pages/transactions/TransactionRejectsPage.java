package za.co.liberty.web.pages.transactions;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.transactions.TransactionRejectsGuiController;
import za.co.liberty.dto.gui.context.RejectsErrorFlagType;
import za.co.liberty.dto.pretransactionreject.RejectElementDTO;
import za.co.liberty.exceptions.ApplicationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.transactions.model.TransactionRejectsModel;

/**
 * Transaction reject GUI, caters for DPE's at the moment.
 * 
 * VED's to be added with new TransactionGUIPage at a later stage.
 * 
 * jzb0608 24/01/2019 - Edit now does a pop-up allowing for the reject to be edited. 
 *
 */
public class TransactionRejectsPage extends BasePage {
	
	private final String REJECTS_PANEL_ID = "transactionRejectsPanel";

	private TransactionRejectsPanel rejectsPanel;
	
	private transient TransactionRejectsGuiController guiController;
	
	Model<TransactionRejectsModel> model;
	
	public TransactionRejectsPage() {
		model = new Model<TransactionRejectsModel>(createModel());
		// 
		model.getObject().setFlagType(RejectsErrorFlagType.BUSINESS);
		add(rejectsPanel = createRejectsPanel(REJECTS_PANEL_ID, model));
	}
	
	private TransactionRejectsPanel createRejectsPanel(String id, Model<TransactionRejectsModel> model) {
		rejectsPanel = new TransactionRejectsPanel(id, model);
		return rejectsPanel;
	}
	
	/**
	 * Create the model by retrieving 
	 * @return
	 */
	private TransactionRejectsModel createModel() {
		
		TransactionRejectsModel rejectsModel = new TransactionRejectsModel();
		rejectsModel.setSearchResults(new ArrayList<RejectElementDTO>());
		
		//The list from the DB contains a null, hence the logic below. An error occurs if the null reference is added to a DropDown data model
		List<String> components = new ArrayList<String>();
		try {
			for (String component : getGuiController().getComponentIdFromRejectElement()) {
				if(component != null)
					components.add(component);
			}
			rejectsModel.setComponentIds(components);
		} catch (ApplicationException e) {
			e.printStackTrace();
			getFeedbackPanel().error(e.getMessage());
		}
		
		return rejectsModel;
	}
	
	/**
	 * Return an instance to the GuiController bean for this page.
	 * 
	 * @return
	 */
	protected TransactionRejectsGuiController getGuiController() {
		if (guiController==null) {
			try {
				guiController =  ServiceLocator.lookupService(TransactionRejectsGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}

		}
		return guiController;
	}

	@Override
	public String getPageName() {
		return "Policy Rejects Page";
	}

}
