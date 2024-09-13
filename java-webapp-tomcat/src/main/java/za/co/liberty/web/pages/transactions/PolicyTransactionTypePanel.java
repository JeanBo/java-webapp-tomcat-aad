/**
 * 
 */
package za.co.liberty.web.pages.transactions;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import za.co.liberty.dto.gui.context.PolicyTransactionTypeEnum;
import za.co.liberty.dto.transaction.IPolicyTransactionModel;
import za.co.liberty.web.data.enums.EditStateType;

/**
 * @author zzt2108
 *
 */
public class PolicyTransactionTypePanel extends AbstractPolicyTransactionPanel {

	
	private static final long serialVersionUID = 1064247924367959460L;

	IPolicyTransactionModel pageModel;
	protected static FeedbackPanel feedbackPanel;
	
	List<PolicyTransactionTypeEnum> transactionSearchTypes = Arrays.asList(PolicyTransactionTypeEnum.values());
	
	@SuppressWarnings("unchecked")
	public PolicyTransactionTypePanel(String id, EditStateType editStateType, IPolicyTransactionModel model, Page parentPage) {
		super(id, editStateType, model, parentPage);
		this.pageModel = model;
		//add(createTransactionTypeChoiceField("transactionTypeGroup"));
	}

	

}
