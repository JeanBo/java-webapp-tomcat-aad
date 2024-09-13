package za.co.liberty.web.pages.fitprop;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ImageButton;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.business.guicontrollers.fitprop.IFitAndProperGuiController;
import za.co.liberty.dto.agreement.maintainagreement.MaintainFAISLicenseDTO;
import za.co.liberty.dto.agreement.properties.FAISLicenseDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.error.WebServiceException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
import za.co.liberty.srs.integration.api.moodle.qualification.Qualification;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.fitprop.model.FitAndProperPageModel;
import za.co.liberty.web.pages.fitprop.model.FitAndProperPanelModel;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.maintainagreement.model.FAISLicensePanelModel;
import za.co.liberty.web.pages.panels.BasePanel;

/**
 * The Panel containing all the fit and proper detail for the agreement
 * 
 * @author DZS2610
 * 
 */
public class FitAndProperPanel extends BasePanel implements ISecurityPanel {
    private static final long serialVersionUID = 1L;

    private static final transient Logger logger = Logger.getLogger(FitAndProperPanel.class);

    private ModalWindow aplPopup;

    private FitAndProperPageModel pageModel;

    private FitAndProperPanelModel panelModel;

    private FeedbackPanel feedBackPanel;

    private Form panelForm;

    private SimpleFAISDetailsPanel faisLicensePanel;

    private AccreditationDetailsPanel accredPanel;

    private CPDDetailsPanel cpdPanel;

    private QualificationDetailsPanel qualPanel;

    private REExamDetailsPanel rePanel;

    private HonestyAndIntegrityDetailsPanel hiPanel;

    private String editImageSource = "/SRSAppWeb/images/edit.gif";

    private String popupImageSource = "/SRSAppWeb/images/rightArrow.png";

    private String refreshImageSource = "/SRSAppWeb/images/refresh.png";

    private boolean initialised;

    private Page parentPage;

    private transient IFitAndProperGuiController guiController;

    public FitAndProperPanel(String id, FitAndProperPageModel pageModel, EditStateType editState, FeedbackPanel feedBackPanel, Page parentPage) {
	super(id, editState, parentPage);
	this.pageModel = pageModel;
	this.feedBackPanel = feedBackPanel;
	this.parentPage = parentPage;
    }

    /**
     * Load the components on the page on first render, so that the components are only generated when the page is displayed
     */
    @Override
    protected void onBeforeRender() {
	if (!initialised) {
	    initialised = true;
	    initPanelModel();
	    add(panelForm = createForm("FPForm"));
	}
	if (feedBackPanel == null) {
	    feedBackPanel = this.getFeedBackPanel();
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
	form.add(aplPopup = createAPLWindow("aplPopup"));
	form.add(faisLicensePanel = createSimpleFAISDetailsPanel("faisDetails"));
	form.add(accredPanel = createAccreditationPanel("accredData"));
	form.add(cpdPanel = createCPDPanel("cpdData"));
	form.add(qualPanel = createQualificationPanel("qualData"));
	form.add(rePanel = createREExamPanel("reData"));
	form.add(hiPanel = createHIPanel("hiData"));
	form.add(createHighestNQFLabel("highestNQF"));
	form.add(createCPDRequired("cpdRequired"));
	// md5 the partyid as its needed this way for moodle
	String md5String = getMd5Digest("" + pageModel.getPartyoid());
	// form.add(createEditLink("editAccred",getFitAndProperGuiController().getMoodleAccreditationLink() + "?userid=" + pageModel.getPartyoid(),
	// "<b>Update</b>"));
	form.add(createImageEditLink("editAccred", getFitAndProperGuiController().getMoodleAccreditationLink() + "?partyid=" + md5String,
		"Update the Accreditation Details").setVisible(false));// disabled until furhter notice
	form.add(createImageEditLink("editCPD", getFitAndProperGuiController().getMoodleCPDLink() + "?partyid=" + md5String, "Update the CPD Details"));
	form.add(createImageEditLink("editQual", getFitAndProperGuiController().getMoodleQualificationsLink() + "?partyid=" + md5String,
		"Update the Qualifications Details"));
	form.add(createImageEditLink("editRE", getFitAndProperGuiController().getMoodleReLink() + "?partyid=" + md5String, "Update the RE Details"));
	// refreshes
	form.add(createRefreshButton("refreshAccred", "Refresh the Accreditation Details"));
	form.add(createRefreshButton("refreshCPD", "Refresh the CPD Details"));
	form.add(createRefreshButton("refreshQual", "Refresh the Qualifications Details"));
	form.add(createRefreshButton("refreshRE", "Refresh the RE Details"));
	form.add(createReportsSection("reportsDiv"));
	form.add(createAPLLink("aplClick"));
	return form;
    }

    /**
     * Get MD5 hash for a given string
     * 
     * @param pInput
     * @return
     */
    String getMd5Digest(String pInput) {
	try {
	    MessageDigest lDigest = MessageDigest.getInstance("MD5");
	    lDigest.update(pInput.getBytes());
	    BigInteger lHashInt = new BigInteger(1, lDigest.digest());
	    String ret = String.format("%1$032X", lHashInt);
	    // should be 32 but just incase do check
	    while (ret.length() < 32) {
		ret = "0" + ret;
	    }
	    return ret;
	} catch (NoSuchAlgorithmException lException) {
	    throw new RuntimeException(lException);
	}
    }

    /**
     * Create the link to reprots section
     * 
     * @return
     */
    private WebMarkupContainer createReportsSection(String id) {
	WebMarkupContainer comp = new WebMarkupContainer(id);
	comp.setOutputMarkupId(true);
	comp.setOutputMarkupPlaceholderTag(true);
	// not visible until future
	comp.setVisible(false);
	// add the link to reports
	comp.add(createImageEditLink("reportsclick", getFitAndProperGuiController().getClickViewLink(), "Click here for Reports", popupImageSource));
	return comp;
    }

    /**
     * Create the modal window for the APL details
     * 
     * @param id
     * @return
     */
    private ModalWindow createAPLWindow(String id) {
	final ModalWindow window = new ModalWindow(id);
	window.setTitle("Approved Product List Details for agreement " + pageModel.getAgreementNumber());

	window.setPageCreator(new ModalWindow.PageCreator() {
	    private static final long serialVersionUID = 1L;

	    public Page createPage() {
		return new APLPopupPage(+pageModel.getAgreementNumber(), panelModel.getWebServiceResponseData());
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
     * Create the APL link
     * 
     * @param id
     * @return
     */
    private ImageButton createAPLLink(String id) {
	ImageButton link = new ImageButton(id, "APL");
	link.add(new AttributeModifier("src", popupImageSource));
	link.add(new AttributeModifier("title", "Click here to view the APL list"));
	link.setOutputMarkupId(true);
	link.setOutputMarkupPlaceholderTag(true);
	// link.setVisible(pageModel.isUserManagesAgreement());
	link.add(new AjaxFormComponentUpdatingBehavior("click") {
	    private static final long serialVersionUID = 1L;

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//					//we dont want the page to submit so return false is appended
//					public CharSequence decorateScript(CharSequence script) {											
//						return script + "return false;";
//					}
//				};
//			}

	    @Override
	    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		super.updateAjaxAttributes(attributes);

		// Way of adding any handler
		attributes.getAjaxCallListeners()
			.add(new AjaxCallListener() {

			    @Override
			    public CharSequence getInitHandler(Component component) {
				CharSequence s = super.getInitHandler(component);
				return "return false;" + ((s == null) ? "" : s);
			    }

			});
	    }

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		aplPopup.show(target);
	    }
	});
	return link;
    }

    /**
     * Create refresh button
     * 
     * @param id
     * @return
     */
    private ImageButton createRefreshButton(String id, String displayText) {
	ImageButton link = new ImageButton(id, "Refresh");
	link.add(new AttributeModifier("src", refreshImageSource));
	link.add(new AttributeModifier("title", displayText));
	link.setOutputMarkupId(true);
	link.setOutputMarkupPlaceholderTag(true);
	link.setVisible(pageModel.isUserManagesAgreement());
	link.add(new AjaxFormComponentUpdatingBehavior("click") {
	    private static final long serialVersionUID = 1L;

//			@Override
//			protected IAjaxCallDecorator getAjaxCallDecorator() {
//				return new AjaxCallDecorator() {
//					private static final long serialVersionUID = 1L;
//					//we dont want the page to submit so return false is appended
//					public CharSequence decorateScript(CharSequence script) {											
//						return script + "return false;";
//					}
//				};
//			}

	    @Override
	    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
		super.updateAjaxAttributes(attributes);

		// Way of adding any handler
		attributes.getAjaxCallListeners()
			.add(new AjaxCallListener() {

			    @Override
			    public CharSequence getInitHandler(Component component) {
				CharSequence s = super.getInitHandler(component);
				return "return false;" + ((s == null) ? "" : s);
			    }

			});
	    }

	    @Override
	    protected void onUpdate(AjaxRequestTarget target) {
		// call moodle and refresh panels
		try {
		    GetQualifications moodleData = getFitAndProperGuiController().getWebServiceData(pageModel.getPartyoid());
		    panelModel.setWebServiceResponseData(moodleData);
		    // call refresh for each panel
		    AccreditationDetailsPanel accredPanelNew = createAccreditationPanel(accredPanel.getId());
		    accredPanel.replaceWith(accredPanelNew);
		    accredPanel = accredPanelNew;

		    CPDDetailsPanel cpdDetailsNew = createCPDPanel(cpdPanel.getId());
		    cpdPanel.replaceWith(cpdDetailsNew);
		    cpdPanel = cpdDetailsNew;

		    QualificationDetailsPanel qualPanelNew = createQualificationPanel(qualPanel.getId());
		    qualPanel.replaceWith(qualPanelNew);
		    qualPanel = qualPanelNew;

		    REExamDetailsPanel reDetailsNew = createREExamPanel(rePanel.getId());
		    rePanel.replaceWith(reDetailsNew);
		    rePanel = reDetailsNew;

		    target.add(accredPanel);
		    target.add(cpdPanel);
		    target.add(qualPanel);
		    target.add(rePanel);
		} catch (DataNotFoundException e) {
		    error("Could not get data from the learning system for refresh");
		    target.add(getFeedBackPanel());
		} catch (WebServiceException e) {
		    error("Could not get data from the learning system for refresh");
		    target.add(getFeedBackPanel());
		}
	    }
	});
	return link;
    }

    /**
     * Create edit link to moodle
     * 
     * @param id
     * @return
     */
    private ImageButton createImageEditLink(String id, String url, String displayText) {
	return createImageEditLink(id, url, displayText, editImageSource);
    }

    /**
     * Create edit link to moodle
     * 
     * @param id
     * @return
     */
    private ImageButton createImageEditLink(String id, String url, String displayText, String imageSource) {
	ImageButton link = new ImageButton(id, "Edit");
	link.add(new AttributeModifier("click", "window.open('" + url + "','mywindow'); return false;"));
	link.add(new AttributeModifier("src", imageSource));
	link.add(new AttributeModifier("title", displayText));
	link.setOutputMarkupId(true);
	link.setOutputMarkupPlaceholderTag(true);
	link.setVisible(pageModel.isUserManagesAgreement());
	return link;
    }

    /**
     * Work out and create the highest NQF lable for the qualifications label
     * 
     * @param id
     * @return
     */
    private Label createHighestNQFLabel(String id) {
	String highestNQFString = "";
	if (panelModel.getWebServiceResponseData() != null && panelModel.getWebServiceResponseData()
		.getQualifications() != null && panelModel.getWebServiceResponseData()
			.getQualifications() != null
		&& panelModel.getWebServiceResponseData()
			.getQualifications()
			.size() > 0) {
	    Qualification highestNQF = null;
	    for (Qualification qual : panelModel.getWebServiceResponseData()
		    .getQualifications()) {
		if (highestNQF == null || qual.getNqfLevel() > highestNQF.getNqfLevel()) {
		    highestNQF = qual;
		}
	    }
	    highestNQFString = "(Highest NQF Level is " + highestNQF.getNqfLevel() + ")";
	}
	return (Label) new Label(id, highestNQFString).setRenderBodyOnly(true);
    }

    /**
     * display the required hours for CPD
     * 
     * @param id
     * @return
     */
    private Label createCPDRequired(String id) {
	String cpdRequired = "";
	Long cpd = getFitAndProperGuiController().getCPDRequiredTotal();
	if (cpd != null) {
	    cpdRequired = "(" + cpd + " hours per cycle required)";
	}
	return (Label) new Label(id, cpdRequired).setRenderBodyOnly(true);
    }

    /**
     * Create edit link to moodle
     * 
     * @param id
     * @return
     */
    private ExternalLink createEditLink(String id, String url, String distplayText) {
	ExternalLink link = new ExternalLink(id, url, distplayText);
	link.setOutputMarkupId(true);
	link.setOutputMarkupPlaceholderTag(true);
	link.setEscapeModelStrings(false);
	link.setVisible(pageModel.isUserManagesAgreement());
	return link;
    }

    private SimpleFAISDetailsPanel createSimpleFAISDetailsPanel(String id) {
	SimpleFAISDetailsPanel faisPanel = new SimpleFAISDetailsPanel(id, pageModel.getAgreementNumber(), panelModel.getFaisLicensePanelModel(), getEditState(),
		parentPage, panelModel.getWebServiceResponseData());
	faisPanel.setOutputMarkupId(true);
	faisPanel.setOutputMarkupPlaceholderTag(true);
	return faisPanel;
    }

    /*
     * Add in all extra detail to the pagemodel that this panel requires
     */
    private FitAndProperPanelModel initPanelModel() {
	FitAndProperPanelModel panelModel = new FitAndProperPanelModel();
	// get the FAIS licence fro the agreement
	long agreementNumber = pageModel.getAgreementNumber();
	if (agreementNumber <= 0) {
	    return panelModel;
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

	FAISLicensePanelModel faisPanelModel = new FAISLicensePanelModel(mDTO, belongsToAgmtNumber, pageModel.getAgreementKind(),
		pageModel.getAgreementNumber(), pageModel.getAgreementStartdate());

	panelModel.setFaisLicensePanelModel(faisPanelModel);
	// call the web service and store the data in the model
	GetQualifications response = null;
	try {
	    response = getFitAndProperGuiController().getWebServiceData(pageModel.getPartyoid());
	} catch (DataNotFoundException e) {
	    error("Could not get data from the learning web service, please contact support if this error persists");
	} catch (WebServiceException e) {
	    error("Could not get data from the learning web service, please contact support if this error persists");
	}
	panelModel.setWebServiceResponseData(response);

	this.panelModel = panelModel;
	return this.panelModel;
    }

    public Class getPanelClass() {
	return FitAndProperPanel.class;
    }

    /**
     * Create the accreditation panel
     * 
     * @return
     */
    private AccreditationDetailsPanel createAccreditationPanel(String id) {
	AccreditationDetailsPanel accredData = new AccreditationDetailsPanel(id, panelModel.getWebServiceResponseData(), getEditState(), parentPage);
	accredData.setOutputMarkupId(true);
	accredData.setOutputMarkupPlaceholderTag(true);
	return accredData;
    }

    /**
     * Create the cpd panel
     * 
     * @return
     */
    private CPDDetailsPanel createCPDPanel(String id) {
	CPDDetailsPanel cpdData = new CPDDetailsPanel(id, pageModel.getPartyoid(), panelModel.getWebServiceResponseData(), getEditState(), parentPage);
	cpdData.setOutputMarkupId(true);
	cpdData.setOutputMarkupPlaceholderTag(true);
	return cpdData;
    }

    /**
     * Create the qualification panel
     * 
     * @return
     */
    private QualificationDetailsPanel createQualificationPanel(String id) {
	QualificationDetailsPanel qualData = new QualificationDetailsPanel(id, panelModel.getWebServiceResponseData(), getEditState(), parentPage);
	qualData.setOutputMarkupId(true);
	qualData.setOutputMarkupPlaceholderTag(true);
	return qualData;
    }

    /**
     * Create the re panel
     * 
     * @return
     */
    private REExamDetailsPanel createREExamPanel(String id) {
	REExamDetailsPanel reData = new REExamDetailsPanel(id, pageModel.getPartyoid(), panelModel.getWebServiceResponseData(), getEditState(), parentPage);
	reData.setOutputMarkupId(true);
	reData.setOutputMarkupPlaceholderTag(true);
	return reData;
    }

    /**
     * Create the honesty and integrity panel
     * 
     * @return
     */
    private HonestyAndIntegrityDetailsPanel createHIPanel(String id) {
	HonestyAndIntegrityDetailsPanel reData = new HonestyAndIntegrityDetailsPanel(id, pageModel.getAgreementNumber(), getEditState(), parentPage);
	reData.setOutputMarkupId(true);
	reData.setOutputMarkupPlaceholderTag(true);
	return reData;
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
