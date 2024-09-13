package za.co.liberty.web.pages.fitprop;

import java.text.SimpleDateFormat;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.business.guicontrollers.fitprop.IFitAndProperGuiController;
import za.co.liberty.dto.common.TimePeriod;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.agreements.ITemporalPropertyFLO;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;

/**
 * Panel to display a very simplistic view of the honsety and integrity details for an agreement
 * @author DZS2610
 *
 */
public class HonestyAndIntegrityDetailsPanel extends BasePanel implements ISecurityPanel {
	private static final long serialVersionUID = 1L;
	
    private Form panelForm;
    
    private RepeatingView detailsLabel;
	
	private boolean initialised;
	
	private transient IFitAndProperGuiController guiController;
	
	private long agreementNumber;	
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Will fetch the data needed for display
	 * @param id
	 * @param editState
	 * @param parentPage
	 */
	public HonestyAndIntegrityDetailsPanel(String id,long agreementNumber, EditStateType editState,
			Page parentPage) {
		super(id, editState, parentPage);
		this.agreementNumber = agreementNumber;			
	}	
	
	
	public Class getPanelClass() {		
		return HonestyAndIntegrityDetailsPanel.class;
	}
	

	/**
	 * Load the components on the page on first render, 
	 * so that the components are only generated when the page is displayed 
	 */
	@Override
	protected void onBeforeRender() {
		if(!initialised) {			
			initialised=true;
			add(panelForm = createForm("hiForm"));						
		}		
		super.onBeforeRender();
	}	
	
	/**
	 * Create the form for this panel
	 * @param id
	 * @return
	 */
	private Form createForm(String id){
		Form form = new Form(id);
		//get the HI property from the DB
		ITemporalPropertyFLO<Integer> prop = getFitAndProperGuiController().getKrollCheckIndicator(agreementNumber);
		//get the cycle dates
		TimePeriod timePeriod =  getFitAndProperGuiController().getCurrentHonestyAndIntegrityTimePeriod();
		
		String hiCycle = "Current Honesty and Integrity cycle is ["
			+dateFormat.format(timePeriod.getStart())+" - "
		+dateFormat.format(timePeriod.getEnd())+"]";
		String hiDeclarationRecieved = "Declaration Received: ";
		String hiDeclarationRecievedDate = "Declaration Received Date: ";
		String hiDeclarationRecievedExpiryDate = "Declaration Expiry Date: ";
		if(prop != null && prop.getValue() != null
				&& prop.getValue() == 1){
			hiDeclarationRecieved += "True";
			hiDeclarationRecievedDate += (prop.getEffectiveFrom() != null) ? prop.getEffectiveFrom() : "";
			hiDeclarationRecievedExpiryDate += (prop.getEffectiveTo() != null) ? prop.getEffectiveTo() : "";
		}else{
			hiDeclarationRecieved += "False";			
		}		
		form.add(new Label("hicycle",hiCycle).setEscapeModelStrings(false));			
		form.add(new Label("hiDeclarationRecieved",hiDeclarationRecieved).setEscapeModelStrings(false));
		form.add(new Label("hiDeclarationRecievedDate",hiDeclarationRecievedDate).setEscapeModelStrings(false));	
		form.add(new Label("hiDeclarationRecievedExpiryDate",hiDeclarationRecievedExpiryDate).setEscapeModelStrings(false));	
		return form;
	}
	
	
	
	
	/**
	 * Get the gui controller for the Panel
	 * @return
	 */
	private IFitAndProperGuiController getFitAndProperGuiController(){
		if(guiController == null){
			try {
				guiController = ServiceLocator.lookupService(IFitAndProperGuiController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return guiController;
	}

}
