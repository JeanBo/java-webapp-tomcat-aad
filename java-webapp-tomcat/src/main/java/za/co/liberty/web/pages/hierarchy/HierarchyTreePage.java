package za.co.liberty.web.pages.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow.MaskType;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.NestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.extensions.markup.html.repeater.tree.theme.HumanTheme;
import org.apache.wicket.extensions.markup.html.repeater.util.TreeModelProvider;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.business.guicontrollers.hierarchy.IHierarchyOrganogramGUIController;
import za.co.liberty.dto.gui.tree.TreeNodeDTO;
import za.co.liberty.dto.userprofiles.ISessionUserProfile;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.web.pages.BasePage;
import za.co.liberty.web.pages.hierarchy.model.HierarchyTreeModel;
import za.co.liberty.web.pages.maintainagreement.MaintainAgreementPage;
import za.co.liberty.web.system.SRSAuthWebSession;
import za.co.liberty.web.wicket.ajax.attributes.SRSAjaxCallListener;


/**
 * The purpose of this class is to display the hierarchy tree. 
 * Nodes are added using a wrapper, where the on node click will trigger the navigation tree panel popup screen 
 * @author JWV2310
 *  
 */
public class HierarchyTreePage extends BasePage  {

	
	private static final long serialVersionUID = 1L;

	NestedTree<TreeNodeWrapper> hierarchyTree;
	Panel panel;
	private ModalWindow displayWindow;
	HierarchyTreeModel hierarchyTreeModel = new HierarchyTreeModel();
	String pageName;
	BasePage parentPage;
	FeedbackPanel feedbackPanel;
	boolean hasHierarchyAccess;
	private transient IHierarchyOrganogramGUIController hierarchyOrganogramManagement;
	
	List<TreeNodeDTO> a = new ArrayList<TreeNodeDTO>();
	private static final Logger logger = Logger.getLogger(HierarchyTreePage.class);
	
	public HierarchyTreePage() {
		pageName = "Hierarchy Navigation Page";
		initiateComponents();
	}
	
	@SuppressWarnings("unchecked")
	private void initiateComponents(){
		add(hierarchyTree = createHierarchyTree("hierarchyTree"));
		add(panel = createEmptyPanel("hierarchyTreeNodeInfo"));
		add(displayWindow = createSearchWindow("displayNodeDetail"));
	}
	
	private Panel createEmptyPanel(String id){
		Panel panel = new EmptyPanel(id);
		panel.setOutputMarkupId(true);
		return panel;
	}
	
	/**
	 * This will create the base tree as a linktree where the nodes will be populated
	 * @param id
	 * @return
	 */
	private NestedTree createHierarchyTree(String id) {
		
		/*
		 * Wrap the Swing TreeModel with a TreeModelProvider for Wicket 7 Tree functions
		 */
		TreeModelProvider<TreeNodeWrapper> treeModelProvider = new TreeModelProvider<TreeNodeWrapper>(hierarchyTreeModel) {

			@Override
			public IModel<TreeNodeWrapper> model(TreeNodeWrapper arg0) {
//				logger.info("TreeModelProvider - Model called " + arg0.getTreeNodeDTO().getOid());
				return Model.of(arg0);
			}
			
		};
			
		hierarchyTree = new DefaultNestedTree<TreeNodeWrapper>(id, treeModelProvider ) {

			private static final long serialVersionUID = -4440674500231512101L;

			/**
			 * Override content component to provide special logic when clicking the text 
			 * as it must not expand or fold the tree nodes.  It must trigger a pop-up window. 
			 */
			@Override
			protected Component newContentComponent(String id, IModel<TreeNodeWrapper> node) {
				// TODO Auto-generated method stub
//				return super.newContentComponent(id, node);
				Folder<TreeNodeWrapper> f = new Folder<TreeNodeWrapper>(id, this, node) {

					@Override
					protected MarkupContainer newLinkComponent(String id, IModel<TreeNodeWrapper> model) {
						return new AjaxFallbackLink<Void>(id) {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean isEnabled() {
								if (((TreeNodeWrapper)model.getObject()).getTreeNodeDTO().getOid() == -1) {
									return false;
								}
								return true;
							}

							@Override
							public void onClick(AjaxRequestTarget target) {
								logger.info("On click clicked for " + ((TreeNodeWrapper)model.getObject()).getTreeNodeDTO().getOid());
								
								hierarchyTreeModel.setTreeNodeDTO(((TreeNodeWrapper) node.getObject()).dto);
								boolean canViewAgreement = getHierarchyOrganogramController().canUserViewAgreement(hierarchyTreeModel.getTreeNodeDTO().getOid(), SRSAuthWebSession.get().getSessionUser());
								hierarchyTreeModel.setUserViewHierarchyNode(canViewAgreement);
								displayWindow.show(target);
								target.add(panel);
								
//								StyledLinkLabel.this.onClick(target);
							}
							
							@Override
							protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
								super.updateAjaxAttributes(attributes);
							        
							        // SRS Convenience method for overLay hiding/showing
							        attributes.getAjaxCallListeners().add(new SRSAjaxCallListener());
							}
						};
					}
					
					
						
				};
			
				f.add(new AttributeModifier("style", ";padding: 2px;"));
				return f;
			}

			
		};
			
		hierarchyTree.add(new HumanTheme());
		    			
//			hierarchyTree = new LinkTree("hierarchyTree", hierarchyTreeModel){

//			private static final long serialVersionUID = -4440674500231512101L;
//			
//			@Override
//			protected Component newNodeComponent(String id, IModel model) {
//				return new LinkIconPanel(id, model, hierarchyTree)
//				{
//					private static final long serialVersionUID = 1L;
//
//					@Override
//					protected void addComponents(IModel model, BaseTree tree) {
//						super.addComponents(model, tree);
//					}
//					
//					@Override
//					protected void onNodeLinkClicked(Object node, BaseTree tree, AjaxRequestTarget target) {
//						super.onNodeLinkClicked(node, tree, target);
//						hierarchyTreeModel.setTreeNodeDTO(((TreeNodeWrapper) node).dto);
//						boolean canViewAgreement = getHierarchyOrganogramController().canUserViewAgreement(hierarchyTreeModel.getTreeNodeDTO().getOid(), SRSAuthWebSession.get().getSessionUser());
//						hierarchyTreeModel.setUserViewHierarchyNode(canViewAgreement);
//						displayWindow.show(target);
//						target.addComponent(panel);
//					}
//					
//					/**
//					 * Creates the icon component for the node
//					 * 
//					 * @param componentId
//					 * @param tree
//					 * @param model
//					 * @return icon image component
//					 */
//					protected Component newImageComponent(String componentId, final BaseTree tree,
//							final IModel model)
//					{	
//						return new Image(componentId)
//						{
//							private static final long serialVersionUID = 1L;
//							
//							
//							protected void onComponentTag(final ComponentTag tag)
//							{
//								checkComponentTag(tag, "img");
//								//TODO change this to a enumeration for hierarchy types, just hierarchy types
//								TreeNodeDTO dto = ((TreeNodeWrapper) model.getObject()).getTreeNodeDTO();
//								long typeTag = dto.getType();
//								if(typeTag == 1642){
//									tag.put("src", "/SRSAppWeb/images/plus.png");
//								}else if(typeTag == 1641){
//									tag.put("src", "/SRSAppWeb/images/minus.png");
//								}else if(typeTag == 1643){
//									tag.put("src", "/SRSAppWeb/images/page.gif ");
//								}else if(typeTag == 1644){
//									tag.put("src", "/SRSAppWeb/images/arrow_down.gif");
//								}else if(typeTag == 1644){
//									tag.put("src", "/SRSAppWeb/images/arrow_down.gif");
//								}else{
//									tag.put("src", "/SRSAppWeb/images/quote.gif");
//								}	
//							}
//
//						};
//						
//					}
//
//				};
//			}
//
//		};
		
		
		
		/*
		 * expand root - always, if branch manager, the expansion will carry on further.
		 */
		hierarchyTree.expand(((TreeNodeWrapper) hierarchyTreeModel.getRoot()));

		
		Stack<Long> hierarchyStack = new Stack<Long>();
		List<Long> hierarchyNodeStrucL = new ArrayList<Long>();
		
		long loggedInParty = SRSAuthWebSession.get().getSessionUser().getPartyOid();
		ISessionUserProfile userProfile = SRSAuthWebSession.get().getSessionUser();
//		hierarchyNodeStrucL = getHierarchyOrganogramController().getLogginInUserHierarchySecurityLevel(loggedInParty,userProfile);
//		
//		for(Long e: hierarchyNodeStrucL){
//			hierarchyStack.add(e);
//		}
		
		TreeNodeWrapper parentNode = (TreeNodeWrapper)hierarchyTreeModel.getRoot();
		
//		while (hierarchyStack.empty()==false) {
//			Enumeration<?> enumList = (parentNode).children();
//			
//			Long nodeOid = hierarchyStack.pop();
//			
//			while (enumList.hasMoreElements()) {
//				TreeNodeWrapper obj = (TreeNodeWrapper) enumList.nextElement();
//				if (obj.getTreeNodeDTO().getOid()==nodeOid.longValue()) {
//					
//					int index = hierarchyTreeModel.getIndexOfChild(parentNode, obj);
//					TreeNodeWrapper childNode = (TreeNodeWrapper) hierarchyTreeModel.getChild(parentNode, index);
//					hierarchyTree.getTreeState().expandNode((TreeNode) childNode);
//					parentNode = childNode;
//					break;
//				}
//			}
//			
//			
//		}

	
		return hierarchyTree;
	}
	
	
	private ModalWindow createSearchWindow(String id){
		
		final ModalWindow window = new ModalWindow(id);
		
		window.setPageCreator(new ModalWindow.PageCreator() {
			private static final long serialVersionUID = 1L;
			public Page createPage() {	
				logger.info("Create modal window");
				return new HierarchyTreePanel(hierarchyTreeModel, window, getEditState(),feedbackPanel);	
			}
		});		
		
		//Close window call back
		window.setWindowClosedCallback(new ModalWindow.WindowClosedCallback() {
			private static final long serialVersionUID = 1L;			
			public void onClose(AjaxRequestTarget target) {
				if(hierarchyTreeModel.isAgreementNodeClicked()){
				  setResponsePage(MaintainAgreementPage.class);
				}  
			}				
		});
		
		window.setMinimalHeight(620);
		window.setInitialHeight(620);
		window.setMinimalWidth(790);
		window.setInitialWidth(790);
		window.setMaskType(MaskType.SEMI_TRANSPARENT);
		window.setCssClassName(ModalWindow.CSS_CLASS_GRAY);
//		window.setPageMapName("rtretrete");
		window.setCookieName("rtretrete");
		return window;
	}

	@Override
	public String getPageName() {

		return pageName;
	}

//	protected AbstractTree getTree() {
//		
//		return hierarchyTree;
//	}
	
	protected IHierarchyOrganogramGUIController getHierarchyOrganogramController() {
		if (hierarchyOrganogramManagement == null) {
			try {
				hierarchyOrganogramManagement = ServiceLocator.lookupService(IHierarchyOrganogramGUIController.class);
			} catch (NamingException namingErr) {
				logger.error(this.getPageName()
						+ " hierarchyOrganogramManagement can not be looked up:"
						+ namingErr);
				CommunicationException comm = new CommunicationException(" hierarchyOrganogramManagement can not be looked up",namingErr);
				throw comm;
			}
		}
		return hierarchyOrganogramManagement;
	}
	
}
