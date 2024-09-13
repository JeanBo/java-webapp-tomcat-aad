package za.co.liberty.web.pages.search;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyGUIController;
import za.co.liberty.business.party.IPartyManagement;
import za.co.liberty.dto.common.IDValueDTO;
import za.co.liberty.dto.contracting.ResultPartyDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.interfaces.party.IPartyNameAndIdFLO;
import za.co.liberty.web.pages.search.models.ContextSearchModel;
import za.co.liberty.web.wicket.markup.html.form.SRSDropDownChoice;


/**
 * Capture search values to search for a Node
 * 
 * @author DZS2610 - 06 March 2009
 *
 */
public class SearchHierarchyNodePanel extends Panel {

	/* Constants */
	private static final long serialVersionUID = 1L;
	
	/* Attributes */
	ContextSearchModel pageModel;
	
	private transient IHierarchyGUIController hierarchyMaintenanceController;
	
	private transient IPartyManagement partyManagement;
	
	private List<IDValueDTO> typeList;
	
	private List<IPartyNameAndIdFLO> channelList;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param pageModel
	 */
	public SearchHierarchyNodePanel(String id, 
			ContextSearchModel pageModel) {
		super(id);
		this.pageModel = pageModel;
		//get the type list
		try {
			typeList = getManagmentBean().getHierarchyTypeList();
		} catch (DataNotFoundException e) {
			//don't worry obout this
			typeList = new ArrayList<IDValueDTO>(0);
		}
		
		try {
			channelList = getManagmentBean().getHierarchyChannelList();
		} catch (DataNotFoundException e) {
			//don't worry obout this
			channelList  = new ArrayList<IPartyNameAndIdFLO>(0);
		}
		
		add(createNameField("name"));
		add(createTypeField("type"));
		add(createChannelField("channel"));		
	}

	/**
	 * Create the search channel field
	 * 
	 * @param id
	 * @return
	 */
	protected DropDownChoice createChannelField(String id) {
		//get fields from DB		
		return new SRSDropDownChoice(id, new PropertyModel(
				pageModel.getSearchValueObject(), "channel"){
				private static final long serialVersionUID = 1L;
				@Override
				public Object getObject() {
					final ResultPartyDTO obj = (ResultPartyDTO) super.getObject();
					//convert to flo
					if(obj == null){
						return null;
					}
					return createNewPartyNameAndIdFLO(obj);
				}

				@Override
				public void setObject(Object obj) {
//					use the Resultto create a flo
					IPartyNameAndIdFLO flo = (IPartyNameAndIdFLO) obj;
					if(flo == null){
						super.setObject(null);
						return;
					}
					//look up ResultPartyDTO
					try {
						super.setObject(getPartyManagement().findPartyWithObjectOid(flo.getOid()));
					} catch (DataNotFoundException e) {	
						//should never happen and serious if it does so throw comm exception
						throw new CommunicationException(e);
					}								
				}
			
		},channelList,new ChoiceRenderer("name", "oid"),"Select");
	}
	
	/**
	 * create a IPartyNameAndIdFLO from a ResultPartyDTO
	 * @param party
	 * @return
	 */
	private IPartyNameAndIdFLO createNewPartyNameAndIdFLO(final ResultPartyDTO party){
		return new IPartyNameAndIdFLO(){
			public String getName() {										
				return party.getName();
			}
			public long getOid() {
				return party.getOid();
			}
			public String getExternalReference() {
				return null;
			}
			public long getType() {
				return party.getTypeOid();
			}
			public String getJobTitle() {
				return party.getJobTitle();
			}
						
		};
	}
	
	/**
	 * Create the search type field
	 * 
	 * @param id
	 * @return
	 */
	protected DropDownChoice createTypeField(String id) {
		
		return new DropDownChoice(id, new PropertyModel(
				pageModel.getSearchValueObject(), "type"),typeList);
	}
	
	/**
	 * Create the search name field
	 * 
	 * @param id
	 * @return
	 */
	protected TextField createNameField(String id) {
		return (TextField) new TextField(id, new PropertyModel(
				pageModel.getSearchValueObject(), "name")).setRequired(true);
	}	
	
	/**
	 * Get HierarchyGUIController managment bean
	 * @return
	 */
	private IHierarchyGUIController getManagmentBean(){
		if(hierarchyMaintenanceController == null){
			try {
				hierarchyMaintenanceController = ServiceLocator.lookupService(IHierarchyGUIController.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return hierarchyMaintenanceController;
	}
	
	/**
	 * Get party managment bean
	 * @return
	 */
	private IPartyManagement getPartyManagement(){
		if(partyManagement == null){
			try {
				partyManagement = ServiceLocator.lookupService(IPartyManagement.class);
			} catch (NamingException e) {
				throw new CommunicationException(e);
			}
		}
		return partyManagement;
	}
}
