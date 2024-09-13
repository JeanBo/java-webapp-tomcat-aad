package za.co.liberty.web.pages.maintainagreement;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;

import za.co.liberty.interfaces.rating.FranchiseTemplateKindEnum;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.maintainagreement.model.DistributionDetailPageModel;
import za.co.liberty.web.pages.maintainagreement.model.DistributionKindGroupPageModel;

public class DistributionDetailsPopup {
	
	public static ModalWindow createModalWindow(final DistributionDetailPageModel pageModel,final String id) {
		final ModalWindow window = new ModalWindow(id);
		
		if(id.equals("templateHistoryWindow"))
			window.setTitle("Distribution Details Template History");
		else 
			window.setTitle("Distribution Details");
		
		// Create the page
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {
				if(id.equals("templateHistoryWindow")){
					return new TemplateHistoryPage(window,pageModel);
				}else {
					if(pageModel.getFranchiseTemplateKindEnum() != null && pageModel.getDistributionKindGroupRatesDTOs() !=null && pageModel.getDistributionKindGroupRatesDTOs().size() > 0){
						if(pageModel.getFranchiseTemplateKindEnum() == FranchiseTemplateKindEnum.FRANCHISE ||
								pageModel.getFranchiseTemplateKindEnum() == FranchiseTemplateKindEnum.FRANCHISE_MANAGER) {
							DistributionKindGroupPageModel distributionKindGroupPageModel = new DistributionKindGroupPageModel();
							distributionKindGroupPageModel.setDistributionKindGroupDTOs(pageModel.getDistributionKindGroupRatesDTOs());
							if(distributionKindGroupPageModel.getDistributionKindGroupDTOs() != null){
								return new DistributionKindGroupPage(window,distributionKindGroupPageModel, EditStateType.VIEW);
							}
						}
					}
					return new DistributionDetailsPage(window,pageModel);
				}
			}
		});
		
		// Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;
			
			public void onClose(AjaxRequestTarget target) {
				
			}
			
		});

		// Initialise window settings
		window.setMinimalHeight(420);
		window.setInitialHeight(520);
		window.setMinimalWidth(750);
		window.setInitialWidth(750);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
		
		return window;
	}

}
