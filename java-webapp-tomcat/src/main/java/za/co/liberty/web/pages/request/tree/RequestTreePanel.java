package za.co.liberty.web.pages.request.tree;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.extensions.markup.html.repeater.tree.DefaultNestedTree;
import org.apache.wicket.extensions.markup.html.repeater.tree.content.Folder;
import org.apache.wicket.extensions.markup.html.repeater.util.TreeModelProvider;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import za.co.liberty.web.pages.request.tree.model.RequestTreeModel;
import za.co.liberty.web.pages.request.tree.model.RequestTreePanelModel;
import za.co.liberty.web.pages.request.tree.model.SRSRequestNode;
import za.co.liberty.web.pages.request.tree.nodes.SRSRequestTree;
import za.co.liberty.web.pages.request.tree.nodes.SRSTreeManager;
import za.co.liberty.web.pages.request.tree.nodes.TreeNode;

/**
 * Request Tree Panel shows properties of requests
 * 
 * @author JZB0608
 *
 */
public class RequestTreePanel extends Panel {

	static final Logger logger = Logger.getLogger(RequestTreePanel.class);

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param requestTreePanelModel
	 */
	public RequestTreePanel(String id, RequestTreePanelModel requestTreePanelModel) {
		super(id);

		SRSRequestTree requestTree = new SRSRequestTree(requestTreePanelModel.getRequestNo(),
				new SRSTreeManager("Request Tree"));

		TreeNode rootTreeNode = requestTree.buildTree();

		SRSRequestNode rootNode = new SRSRequestNode(rootTreeNode);

		final RequestTreeModel requestTreeModel = new RequestTreeModel(rootNode);

		/*
		 * Wrap the Swing TreeModel with a TreeModelProvider for Wicket 7 Tree functions
		 */
		TreeModelProvider<SRSRequestNode> treeModelProvider = new TreeModelProvider<SRSRequestNode>(requestTreeModel) {

			@Override
			public IModel<SRSRequestNode> model(SRSRequestNode arg0) {
//				logger.info("TreeModelProvider - Model called " + arg0.getRequestTreeNode().getData());
				return Model.of(arg0);
			}

		};

		DefaultNestedTree<SRSRequestNode> tree = new DefaultNestedTree<SRSRequestNode>("requestTree",
				treeModelProvider) {
			/**
			 * Override content component to provide special logic when clicking the text as
			 * it must not expand or fold the tree nodes. It must trigger a pop-up window.
			 */
			@Override
			protected Component newContentComponent(String id, IModel<SRSRequestNode> node) {
				// TODO Auto-generated method stub
//				return super.newContentComponent(id, node);
				Folder<SRSRequestNode> f = new Folder<SRSRequestNode>(id, this, node) {

					@Override
					protected MarkupContainer newLinkComponent(String id, IModel<SRSRequestNode> model) {
						return new AjaxFallbackLink<Void>(id) {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean isEnabled() {
								
								return true;
							}

							@Override
							public void onClick(AjaxRequestTarget target) {
								logger.info("On click clicked for " + ((SRSRequestNode)model.getObject()).getRequestTreeNode().getData());	
//								StyledLinkLabel.this.onClick(target);
							}
						};
					}

						
				};
			
				f.add(new AttributeModifier("style", ";padding: 2px;"));
				return f;
			}
		};
		
		add(tree);

		tree.expand(rootNode);
		add(tree);
		add(createEmptyPanel("requestTreeNodeInfo"));

	}

	private Panel createEmptyPanel(String id) {
		Panel panel = new EmptyPanel(id);
		panel.setOutputMarkupId(true);
		return panel;
	}
}
