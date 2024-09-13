package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import za.co.liberty.dto.userprofiles.RuleDTO;
import za.co.liberty.interfaces.rules.ArithmeticType;
import za.co.liberty.interfaces.rules.RuleDataType;
import za.co.liberty.web.data.pages.IMaintenancePageModel;

/**
 * Rules model class
 * 
 * @author jzb0608 - 23 Apr 2008
 * 
 */
public class RulesModel implements Serializable, IMaintenancePageModel<RuleDTO> {

	private static final long serialVersionUID = 4736602430175422792L;

	private List<RuleDTO> selectionList;
	
	List<ArithmeticType> arithmeticTypes = new ArrayList<ArithmeticType>();
	
	List<RuleDataType> dataTypes = new ArrayList<RuleDataType>();

	private RuleDTO selectedRule;

	public List<RuleDTO> getSelectionList() {
		return selectionList;
	}

	public void setSelectionList(List<RuleDTO> ruleList) {
		this.selectionList = ruleList;
	}

	public RuleDTO getSelectedItem() {
		return selectedRule;
	}

	public void setSelectedItem(RuleDTO selectedRule) {
		this.selectedRule = selectedRule;
	}

	public List<ArithmeticType> getArithmeticTypes() {
		return arithmeticTypes;
	}

	public void setArithmeticTypes(List<ArithmeticType> arithmeticTypes) {
		this.arithmeticTypes = arithmeticTypes;
	}

	public List<RuleDataType> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(List<RuleDataType> dataTypes) {
		this.dataTypes = dataTypes;
	}
}
