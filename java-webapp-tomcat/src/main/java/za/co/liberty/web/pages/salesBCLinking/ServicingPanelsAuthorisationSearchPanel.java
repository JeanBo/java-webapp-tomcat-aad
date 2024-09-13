package za.co.liberty.web.pages.salesBCLinking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

import za.co.liberty.business.guicontrollers.salesBCLinking.ISalesBCLinkingGuiController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.business.party.PartyManagement;
import za.co.liberty.dto.agreement.SalesBCLinking.BranchDetailsDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryResultDTO;
import za.co.liberty.dto.agreement.request.RequestEnquiryRowDTO;
import za.co.liberty.dto.gui.context.AgreementSearchType;
import za.co.liberty.dto.gui.request.RequestEnquiryPageModelDTO;
import za.co.liberty.dto.persistence.party.flow.PartyRoleContextFLO;
import za.co.liberty.dto.userprofiles.SessionUserHierarchyNodeDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.requests.PropertyKindType;
import za.co.liberty.interfaces.agreements.requests.RequestKindType;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.interfaces.party.OrganisationType;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.interfaces.persistence.agreement.request.IRequestEnquiryRow;
import za.co.liberty.party.util.Constants;
import za.co.liberty.persistence.party.IPartyEntityManager;
import za.co.liberty.web.pages.request.AbstractRequestEnquiryPanel;
import za.co.liberty.web.pages.request.BaseRequestViewAndAuthorisePanel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Authorisation panel specifically catering for service panel request authorisation only.
 * 
 * @author jzb0608
 *
 */
@SuppressWarnings("unused")
public class ServicingPanelsAuthorisationSearchPanel extends AbstractRequestEnquiryPanel {

	private static final long serialVersionUID = -2744915978242135201L;
	private transient ISalesBCLinkingGuiController guiController;
	
	
	/**
	 * Default constructor 
	 * 
	 * @param arg0
	 * @param model
	 */
	public ServicingPanelsAuthorisationSearchPanel(String arg0, IModel model, FeedbackPanel feedbackPanel) {
		super(arg0, model,feedbackPanel);
	}	
	
	/**
	 * Create the search form which holds all the search filter fields.
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Form createSearchForm(String id) {
		Form form = new Form(id) {		
			private static final long serialVersionUID = -6308633210871154462L;
			@Override
			protected void onSubmit() {
				super.onSubmit();	
			}
		};
				
		form.add(startDateField=createStartDateField("startDate"));
		form.add(endDateField=createEndDateField("endDate"));
		form.add(startDatePicker=createStartDatePicker("startDatePicker", startDateField));
		form.add(endDatePicker=createEndDatePicker("endDatePicker", endDateField));
		
		form.add(searchButton=createSearchButton("searchButton", form));
		form.add(nextButton=createNextButton("nextButton", form));
		nextButton.setVisible(false);
		form.add(exportForm=createExportForm("exportForm"));
		form.setOutputMarkupId(true);
		return form;
	}

	// ==============================================================================================
	// Generate fields
	// ==============================================================================================
	
	
	@Override
	/**
	 * Method created to perform post processing activities(possibly filtering
	 * the search results further) on the requests that are obtained as the
	 * search result
	 */
	public void doResultPostProcessing() {
		// /*Requests that have from or to branch of the logged in user.*/
		// List<RequestEnquiryRowDTO> enquiryResults =
		// resultDto.getEnquiryResultDto();
		//
		// for (RequestEnquiryRowDTO row: enquiryResults) {
		// row.get
		// }

		List<RequestEnquiryRowDTO> finalReuqestList = new ArrayList<RequestEnquiryRowDTO>();

		for (RequestEnquiryRowDTO row : dataModel.getSearchResultList()) {
			long fromRegionPartyOID = 0;
			long toRegionPartyOID = 0;
			Long branchFromOID = (Long) row
					.getAdditionalProperty(PropertyKindType.BranchFromOID
							.getPropertyKind());
			Long branchToOID = (Long) row
					.getAdditionalProperty(PropertyKindType.BranchToOID
							.getPropertyKind());

			/* Get the Region details for the BranchTo and BranchFrom */
			if (branchFromOID != null) {
				fromRegionPartyOID = getGUIController().getRegionPartyOID(
						branchFromOID);
			}

			if (branchToOID != null) {
				toRegionPartyOID = getGUIController().getRegionPartyOID(
						branchToOID);
			}

			Collection<SessionUserHierarchyNodeDTO> nodesManaged = SRSAuthWebSession
					.get().getSessionUser().getHierarchicalNodeAccessList();

			for (SessionUserHierarchyNodeDTO node : nodesManaged) {
				if (node.getPartyOid() != 0
						&& (node.getPartyOid() == fromRegionPartyOID || node
								.getPartyOid() == toRegionPartyOID)) {
					finalReuqestList.add(row);
				}
			}
		}

		dataModel.setSearchResultList(finalReuqestList);
	}
	


		private ISalesBCLinkingGuiController getGUIController() {
			if (guiController == null) {
				try {
					guiController = ServiceLocator
							.lookupService(ISalesBCLinkingGuiController.class);
				} catch (NamingException namingErr) {
					CommunicationException comm = new CommunicationException(
							"ISalesBCLinkingGuiController can not be looked up!");
					throw new CommunicationException(comm);
				}
			}
			return guiController;
		}
}
