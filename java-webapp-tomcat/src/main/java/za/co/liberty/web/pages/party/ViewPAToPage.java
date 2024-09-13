package za.co.liberty.web.pages.party;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.SerializationUtils;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
//import org.apache.wicket.ajax.IAjaxCallDecorator;
//import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.form.AjaxFallbackButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;

import za.co.liberty.business.guicontrollers.partymaintenance.IPartyMaintenanceController;
import za.co.liberty.dto.party.PartyDTO;
import za.co.liberty.dto.party.PartyRoleDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.PartyRoleType;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.data.enums.PanelToRequestMapping;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.pages.party.MaintainPartyPage;
import za.co.liberty.web.pages.party.PartyHierarchyPanel;
import za.co.liberty.web.pages.party.model.MaintainPartyPageModel;
import za.co.liberty.web.system.SRSAuthWebSession;

/**
 * Page that will show the Party heirarchy screen with Pa roles only
 * @author DZS2610
 *
 */
public class ViewPAToPage extends BaseWindowPage {

	private static final long serialVersionUID = 1L;
	private boolean initialised;			
	
	private MaintainPartyPageModel pageModel;
	
	private ModalWindow popupWindow;
	
	public ViewPAToPage(ModalWindow popupWindow, MaintainPartyPageModel pageModel){
		this.pageModel = pageModel;
		this.popupWindow = popupWindow;
	}
	
	

	@Override
	protected void onBeforeRender() {
		if(!initialised){
			add(createPartyHierarchyPanel("paDetailsPanel"));
			initialised = true;
		}		
		super.onBeforeRender();
	}
	
	
	
	/**
	 * Creates the party heirarchy panel that allows viewing of Pa roles
	 * @param id
	 * @return
	 */
	private Panel createPartyHierarchyPanel(String id){
		//List<PartyRoleType> typesToUse = new ArrayList<PartyRoleType>(1);
		List<PartyRoleType> typesToUse = new ArrayList<PartyRoleType>();
		typesToUse.add(PartyRoleType.ISPERSONALASISSTANTTO);
		typesToUse.add(PartyRoleType.HASDIRECTOR);
		PartyHierarchyPanel panel = new PartyHierarchyPanel(id,pageModel,EditStateType.VIEW,getFeedBackPanel(),this,typesToUse);			
		return panel;
	}
	
	@Override
	public String getPageName() {		
		//return "Assistant To";
		return "Related Roles";
	}
}
