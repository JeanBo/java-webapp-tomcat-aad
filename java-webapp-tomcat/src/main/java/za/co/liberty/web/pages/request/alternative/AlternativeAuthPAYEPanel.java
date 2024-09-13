package za.co.liberty.web.pages.request.alternative;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Page;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.request.IRequestViewGuiController;
import za.co.liberty.dto.gui.request.ViewRequestModelDTO;
import za.co.liberty.dto.gui.request.alternative.AlternativePAYERequestDTO;
import za.co.liberty.exceptions.data.ValidationException;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.request.tree.RequestTreePanel;
import za.co.liberty.web.pages.request.tree.model.RequestTreePanelModel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;

/**
 * Panel used to show PAYE Related transactions linked settlements
 * 
 * @author jzb0608
 *
 */
public class AlternativeAuthPAYEPanel extends Panel {

	private static final long serialVersionUID = 1L;

	private transient static Logger logger = Logger.getLogger(AlternativeAuthPAYEPanel.class);
	
	private ViewRequestModelDTO pageModel;
	protected static FeedbackPanel feedbackPanel;
	protected AlternativePAYERequestDTO alternativeDTO = null;
	protected EditStateType editStateType;
	
	protected SRSDataGrid tempDataGrid;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param editStateType
	 * @param model
	 * @param parentPage
	 */
	public AlternativeAuthPAYEPanel(String id, EditStateType editStateType,
			ViewRequestModelDTO model, Page parentPage) {
		super(id);
		this.pageModel = model;
		this.editStateType = editStateType;
		
		/* Retrieve a DTO for the alternative request */
		try {
			alternativeDTO = (AlternativePAYERequestDTO) ServiceLocator.lookupService(IRequestViewGuiController.class)
					.getAlternativeRequestDTO(
							model.getRequestEnquiryRowList().get(0)
									.getRequestId());
			if (logger.isDebugEnabled())
					logger.debug("After retrieve - DirEffStartDate :" + alternativeDTO.getDirectiveEffectiveStartDate()
						+ "\n  LimitPayeAmount :" + alternativeDTO.getDirectiveLimitPayeAmount()
						+ "\n  Calc Basis :" + alternativeDTO.getTaxBasis() + " - " + alternativeDTO.getTaxBasisKind()
						+ "\n  Limit PAYE refund :" + alternativeDTO.getIsPayeLimitRefund());
		} catch (NamingException e) {
			throw new CommunicationException(e);
		} catch (ValidationException e) {
			logger.warn("Validation error", e);
			e.printStackTrace();
			this.warn("Unable to retrieve PAYE detail, only showing properties");
		}
		
		/*
		 * Add tree model showing default properties, always shown.
		 */
		RequestTreePanelModel treeModel = new RequestTreePanelModel();
		treeModel.setRequestNo(model.getViewRequestContextDto().getRequestDto().getRequestId());
		add(new RequestTreePanel("payeProperties", treeModel ));
		
		
		
		/*
		 * Do the additional info if the DTO was retrieved only
		 */
		WebMarkupContainer payeContainer = null;
		
		boolean isHide = false;
		if (alternativeDTO == null) {
			payeContainer.setVisible(false);
			alternativeDTO = new AlternativePAYERequestDTO();
			isHide = true;
			payeContainer = new EmptyPanel("payeContainer");
		} else {
			payeContainer = new WebMarkupContainer("payeContainer");
		}
		add(payeContainer);
		
		/*
		 * Only add the paye container fields if not an empty panel
		 */
		if (!isHide) {
			payeContainer.add(new Label("payeTaxPeriodToDatePaid", new Model(alternativeDTO.getPayeTaxPeriodToDatePaid()))); //"R 10'0000"));
			payeContainer.add(new Label("totalPayeTaxPeriodToDateDue", new Model(alternativeDTO.getTotalPayeTaxPeriodToDateDue()))); //"R 12'0000"));
			payeContainer.add(new Label("totalPayeAmountPerCompany", new Model(alternativeDTO.getTotalPayeAmountPerCompany()))); //"R 2'0000"));
			
			List<AlternativeFieldDTO> list = new ArrayList<AlternativeFieldDTO>();
			
			logger.info("Before placement1 - DirEffStartDate :" + alternativeDTO.getDirectiveEffectiveStartDate()
			+ "\n  LimitPayeAmount :" + alternativeDTO.getDirectiveLimitPayeAmount()
			+ "\n  Calc Basis :" + alternativeDTO.getTaxBasis() + " - " + alternativeDTO.getTaxBasisKind()
			+ "\n  Limit PAYE refund :" + alternativeDTO.getIsPayeLimitRefund());
			
			if (!isHide) {
				if (logger.isDebugEnabled())
					logger.debug("Before placement - DirEffStartDate :" + alternativeDTO.getDirectiveEffectiveStartDate()
						+ "\n  LimitPayeAmount :" + alternativeDTO.getDirectiveLimitPayeAmount()
						+ "\n  Calc Basis :" + alternativeDTO.getTaxBasis() + " - " + alternativeDTO.getTaxBasisKind()
						+ "\n  Limit PAYE refund :" + alternativeDTO.getIsPayeLimitRefund());
				
				list.add(new AlternativeFieldDTO("Tax Basis", alternativeDTO.getTaxBasisKind(), "Tax Basis kind used for this settlement"));
				
				if (alternativeDTO.getDirectiveEffectiveStartDate()!=null)
					list.add(new AlternativeFieldDTO("Directive Effective Start Date", alternativeDTO.getDirectiveEffectiveStartDate(),
						"First date that the directive is considered active for settlements."));
				if (alternativeDTO.getDirectiveLimitPayeRequestOid()!=null)
					list.add(new AlternativeFieldDTO("Directive Limit Request OID", alternativeDTO.getDirectiveLimitPayeRequestOid(),
						"Request OID of the last PAYE request before the directive is active.  Used to limit PAYE refunds."));
				
				if (alternativeDTO.getDirectiveLimitAnnualTEAmount()!=null)
					list.add(new AlternativeFieldDTO("Directive Limit Annual TE", alternativeDTO.getDirectiveLimitAnnualTEAmount(),
						"Annual Taxable Earnings of last PAYE request.  Excluded from directive PAYE calculation"));
				if (alternativeDTO.getDirectiveLimitMonthlyTEAmount()!=null)
					list.add(new AlternativeFieldDTO("Directive Limit Monthly TE", alternativeDTO.getDirectiveLimitMonthlyTEAmount(),
						"Monthly Taxable Earnings of last PAYE request.  Excluded from directive PAYE calculation"));
				if (alternativeDTO.getDirectiveLimitPayeAmount()!=null)
					list.add(new AlternativeFieldDTO("Directive Minimum PAYE Amount", alternativeDTO.getDirectiveLimitPayeAmount(),
						"PAYE for the year may not go below the PAYE deducted before changing to directive tax basis."));
				
				list.add(new AlternativeFieldDTO("Limit PAYE Refund", alternativeDTO.getIsPayeLimitRefund(),
						"PAYE refunds were limited to the minimum PAYE amount if this is true."));
			}
			payeContainer.add(createRowValuesOne("rowValuesOne", list));
		}
		
	}
	
	/**
	 * CReate a list view 
	 * @param id
	 * @return
	 */
	protected ListView<AlternativeFieldDTO> createRowValuesOne(String id, List<AlternativeFieldDTO> list) {
		
		
		
		ListView<AlternativeFieldDTO> rows = new ListView<AlternativeFieldDTO>(id, list) {
			int i =0;
			
			@Override
			protected void populateItem(ListItem<AlternativeFieldDTO> item) {
			
				if (logger.isDebugEnabled())
					logger.debug("populateItem  desc="+item.getModelObject().getDescription()
						+  "    value=" + item.getModelObject().getValue()
						+  "    title=" + item.getModelObject().getTitle());
				item.add(new Label("rowOneLabel", item.getModelObject().getDescription()));	
				item.add(new Label("rowOneValue", new Model((Serializable)item.getModelObject().getValue())));	
				if (item.getModelObject().getTitle()!=null) {
					item.add(new AttributeModifier("title", item.getModelObject().getTitle()));
				}
			}
			
		};
		
		return rows;
	}
	
	/**
	 * Decorate the style tag to hide the component
	 * 
	 * @param isHidden Hide component if true. 
	 * @param tag
	 */
	private void decorateComponentStyleToHide(boolean isHidden, ComponentTag tag) {
		if (!isHidden) {
			return;
		}
		String val = (String) tag.getAttributes().get("style");
		val = (val ==null) ? "" : val;
		val += " ;visibility:hidden;";
		tag.put("style", val);
	}
	
}

class AlternativeFieldDTO implements Serializable {
	String description;
	Object value;
	String title;
	
	public AlternativeFieldDTO (String description, Object value) {
		this(description, value, null);
	}
	public AlternativeFieldDTO (String description, Object value, String title) {
		this.description = description;
		this.value = value;
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
