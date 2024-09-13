package za.co.liberty.web.pages.fitprop;

import java.util.Date;
import java.util.TreeMap;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.business.guicontrollers.fitprop.IFitAndProperGuiController;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTO;
import za.co.liberty.dto.agreement.properties.FAISLicenseDTO;
import za.co.liberty.dto.party.fais.FAISLicenseCategoryDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.common.ITypeFLO;
import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;
import za.co.liberty.web.pages.panels.BasePanel;

/**
 * Panel to display a very simplistic view of the FAIS licence details for an agreement
 * 
 * @author DZS2610
 *
 */
public class SimpleFAISDetailsPanel extends BasePanel implements ISecurityPanel {
    private static final long serialVersionUID = 1L;

    private Form panelForm;

    private RepeatingView faisDetailsLabel;

    private FAISLicensePanelModel faisLicensePanelModel;

    private boolean initialised;

    private ModalWindow faisPopup;

    private transient IFitAndProperGuiController guiController;

    private long agreementNumber;

    private int agreementKind;

    private Date agreementStartDate;

    private GetQualifications moodleData;

    /**
     * Will fetch the data needed for display
     * 
     * @param id
     * @param editState
     * @param parentPage
     */
    public SimpleFAISDetailsPanel(String id, long agreementNumber, int agreementkind, EditStateType editState, Page parentPage, GetQualifications moodleData,
	    Date agreementStartDate) {
	super(id, editState, parentPage);
	this.agreementNumber = agreementNumber;
	this.agreementKind = agreementkind;
	this.moodleData = moodleData;
	this.agreementStartDate = agreementStartDate;
    }

    /**
     * Will only use the data in the model and fetch no additional data
     * 
     * @param id
     * @param editState
     * @param parentPage
     */
    public SimpleFAISDetailsPanel(String id, long agreementNumber, FAISLicensePanelModel panelModel, EditStateType editState, Page parentPage,
	    GetQualifications moodleData) {
	super(id, editState, parentPage);
	faisLicensePanelModel = panelModel;
	this.agreementNumber = agreementNumber;
	this.moodleData = moodleData;
	this.agreementStartDate = faisLicensePanelModel.getAgreementStartDate();
    }

    public Class getPanelClass() {
	return SimpleFAISDetailsPanel.class;
    }

    /**
     * Load the components on the page on first render, so that the components are only generated when the page is displayed
     */
    @Override
    protected void onBeforeRender() {
	if (!initialised) {
	    initialised = true;
	    add(panelForm = createForm("faisForm"));
	    // get the FAIS details for the agreement number
	    initPanelModel();
	}
	super.onBeforeRender();
    }

    /**
     * Create the form for this panel
     * 
     * @param id
     * @return
     */
    private Form createForm(String id) {
	Form form = new Form(id);
	form.add(faisPopup = createFAISLicenceWindow("faisPopup"));
	form.add(createClickHereLink("faisclick"));
	form.add(faisDetailsLabel = createFAISDetailsRepeater("faisDetails"));
	return form;
    }

    /**
     * Create the FAIS licence details label
     * 
     * @param id
     * @return
     */
    private RepeatingView createFAISDetailsRepeater(String id) {
	// first sort the list
	TreeMap<String, FAISLicenseCategoryDTO> data = new TreeMap<String, FAISLicenseCategoryDTO>();

	// Check for null, and if null do not go into loop
	if (faisLicensePanelModel.getFaisLicenseDTO() != null) {
	    for (FAISLicenseCategoryDTO cat : faisLicensePanelModel.getFaisLicenseDTO()
		    .getFaisLicenseCategoryDTOs()) {
		data.put("" + cat.getTypeOid(), cat);
	    }
	}

	RepeatingView view = new RepeatingView(id);
	if (faisLicensePanelModel.getFaisLicenseDTO() != null) {
	    for (FAISLicenseCategoryDTO cat : data.values()) {
		ITypeFLO typeVO = null;
		try {
		    typeVO = getFitAndProperGuiController().getType(cat.getTypeOid());
		} catch (DataNotFoundException e) {
		    error("No details could be found for the category of type " + cat.getTypeOid() + " please contact support for details.");
		}
		view.add(new Label(view.newChildId(), ((typeVO != null) ? typeVO.getName() : "Type " + cat.getTypeOid()) + " - " + typeVO.getDescription()));
	    }
	}
	return view;
    }

    /*
     * Add in all extra detail to the pagemodel that this panel requires
     */
    private FAISLicensePanelModel initPanelModel() {
	if (faisLicensePanelModel != null) {
	    return faisLicensePanelModel;
	}
	// get the FAIS licence for the agreement
	if (agreementNumber <= 0) {
	    return null;
	}
	// get the belongs to for the FAIS popup
	Long belongsToAgmtNumber = getFitAndProperGuiController().getBelongsToAgreementNumber(agreementNumber);
	FAISLicenseDTO licenceDetails = null;
	try {
	    licenceDetails = getFitAndProperGuiController().getFAISLicenceDTOForAgreement(agreementNumber);
	} catch (DataNotFoundException e) {
	    error("No licence details could be found for agreement number " + agreementNumber);
	}

	MaintainFAISLicenseDTO mDTO = new MaintainFAISLicenseDTO(null, licenceDetails, null);
	FAISLicensePanelModel faisPanelModel = new FAISLicensePanelModel(mDTO, belongsToAgmtNumber, agreementKind, agreementNumber, agreementStartDate);
	return faisPanelModel;
    }

    /**
     * Create the click here link
     * 
     * @param id
     * @return
     */
    private AjaxLink createClickHereLink(String id) {
	AjaxLink link = new AjaxLink(id) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void onClick(AjaxRequestTarget target) {
		faisPopup.show(target);
	    }
	};
	return link;
    }

    /**
     * Create the modal window for the FAIS licence details
     * 
     * @param id
     * @return
     */
    private ModalWindow createFAISLicenceWindow(String id) {
	final ModalWindow window = new ModalWindow(id);
	window.setTitle("FAIS Licence Details for agreement " + agreementNumber);

	window.setPageCreator(new ModalWindow.PageCreator() {
	    private static final long serialVersionUID = 1L;

	    public Page createPage() {
		return new FAISPopupPage(agreementNumber, faisLicensePanelModel);
	    }
	});

	// Initialise window settings
	window.setMinimalHeight(500);
	window.setInitialHeight(500);
	window.setMinimalWidth(750);
	window.setInitialWidth(750);
	window.setMaskType(MaskType.SEMI_TRANSPARENT);
	window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
	return window;
    }

    /**
     * Get the gui controller for the Panel
     * 
     * @return
     */
    private IFitAndProperGuiController getFitAndProperGuiController() {
	if (guiController == null) {
	    try {
		guiController = ServiceLocator.lookupService(IFitAndProperGuiController.class);
	    } catch (NamingException e) {
		throw new CommunicationException(e);
	    }
	}
	return guiController;
    }
}
