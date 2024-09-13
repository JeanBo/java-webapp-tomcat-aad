package za.co.liberty.web.pages.maintainagreement.fais;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;

import za.co.liberty.dto.agreement.maintainagreement.fais.FAISLicensePanelGridDTO;
import za.co.liberty.dto.party.fais.supervision.FAISCategorySupervisionDTO;
import za.co.liberty.helpers.util.DateUtil;
import za.co.liberty.web.data.enums.EditStateType;

/**
 * This 
 * @author aaa1210
 *
 */
public class SuperVisionPanel extends Panel{

	WebMarkupContainer cont =null;
	private boolean initialized;
	 RepeatingView repeating;
	 List <FAISCategorySupervisionDTO> list=null;
//	 FAISLicenseCategoryDTO categoryDTO;
	 EditStateType editState;
	 
	 FAISLicensePanelGridDTO dataModel;
	
	public SuperVisionPanel(String id, EditStateType editState, FAISLicensePanelGridDTO dataModel) { //FAISLicenseCategoryDTO categoryDTO){//List <FAISCategorySupervisionDTO> faisCategorySupervisionDTO) {
		super(id);
		this.dataModel = dataModel;
//		this.categoryDTO=categoryDTO;
		this.editState=editState;
		
		onBeforeRender();
		
		
	}
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	
	
	public class SuperVisionForm extends Form {
		private static final long serialVersionUID = 5808296649559984427L;

		public SuperVisionForm(String id) {
			super(id);
								
			add(repeating=getRepeatingView());
			
		}
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.Component#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {

		
		initPanel();
		if (!initialized) {
			add(new SuperVisionForm("superVisionForm"));
			initialized = true;
		}else{
			RepeatingView view=getRepeatingView();
			repeating.replaceWith(view);
			repeating = view;
			
		}
		
		super.onBeforeRender();
	}
	/**
	 * initialize panel
	 *
	 */
	private void initPanel() {
		if( dataModel.getFaisLicenseCategoryDTO().getFaisCategorySupervisionDTO()==null 
				|| dataModel.getFaisLicenseCategoryDTO().getFaisCategorySupervisionDTO().isEmpty()){
			list=new ArrayList<FAISCategorySupervisionDTO>();
		}else{
		list=(List)dataModel.getFaisLicenseCategoryDTO().getFaisCategorySupervisionDTO();
		}
	}
	private RepeatingView getRepeatingView() {
		
		RepeatingView repeatingView= new RepeatingView("repeating");
		if (!list.isEmpty() && list.size() > 0) {
			DateUtil dateUtil=DateUtil.getInstance();
			Iterator<FAISCategorySupervisionDTO> iterator = list.iterator();
			while (iterator.hasNext()) {

				FAISCategorySupervisionDTO supervisionDTO = iterator.next();
				if (supervisionDTO != null && supervisionDTO.getSupervisionTypeDBEnumDTO() != null) {
					String effectivetoDisplay = "";
					if (supervisionDTO.getEffectiveTo() != null) {
						effectivetoDisplay = ""
								+ dateUtil.dateFormat("dd-MMM-yyyy",supervisionDTO.getEffectiveTo()) + "]";
					} else {
						effectivetoDisplay = "infinity]";
					}
					if(supervisionDTO.getEffectiveFrom() != null){
						String str = supervisionDTO.getSupervisionTypeDBEnumDTO()
								.getName()
								+ "["
								+ ""
								+dateUtil.dateFormat("dd-MMM-yyyy", supervisionDTO.getEffectiveFrom())
								+ " to " + effectivetoDisplay;
						repeatingView.add(new Label(repeatingView.newChildId(), str));
					}

				}
			}
		}
		return repeatingView;
	}
}
