package za.co.liberty.web.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
//import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class AgreementDataProvider extends SortableDataProvider {

	List list = new ArrayList();

	public AgreementDataProvider(PageParameters pgParam) {
		// important or you'll get a null pointer on line 40
		setSort("name", SortOrder.ASCENDING);
		
		list = getAgreementList(pgParam);

	}

	public Iterator iterator(final int first, final int count) {
		List newList = new ArrayList();
		
		SortParam sortParam = getSort();
		boolean isAscending = sortParam.isAscending();
		String column = (String)sortParam.getProperty();
		
		newList.addAll(list.subList(first, first + count));

		return newList.iterator();
	}

	public long size() {
		return list.size();
	}

	public IModel model(final Object object) {
		return new AbstractReadOnlyModel() {
			public Object getObject() {
				return object;
			}
		};
	}
	
	public List getAgreementList(PageParameters pgParam){
		
		List agList = new ArrayList();
		//Hit the DB. Get the Data for Agreements and populate the arraylist
		//Hardcoding at the moment.
		agList.add(new AgreementSearchResultForm("612","Active","BrokerIntermediaryAgreement",
				"Johny Depp","Commisions","GIM"));
		agList.add(new AgreementSearchResultForm("60002","Active","BrokerIntermediaryAgreement",
				"Johny Depp","Commisions","GIM"));
		agList.add(new AgreementSearchResultForm("40003","Active","BrokerIntermediaryAgreement",
				"Johny Depp","Commisions","GIM"));
		//Finally 
		
		return agList;
				
	}

	@Override
	public Iterator iterator(long arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}



}

class AgreementSearchResultForm implements Serializable{

	private String agNo;
	private String agStatus;
	private String agDivision;
	private String name;
	private String ispart;
	private String belongsto;
	
	public AgreementSearchResultForm(String agNo, String agStatus, String agDivision, 
			String name, String ispart, String belongsto) {
		super();
		setAgNo(agNo);
		setAgStatus(agStatus);
		setAgDivision(agDivision);
		setName(name);
		setIspart(ispart);
		setBelongsto(belongsto);
	}
	public String getAgDivision() {
		return agDivision;
	}
	public void setAgDivision(String agDivision) {
		this.agDivision = agDivision;
	}
	public String getAgNo() {
		return agNo;
	}
	public void setAgNo(String agNo) {
		this.agNo = agNo;
	}
	public String getAgStatus() {
		return agStatus;
	}
	public void setAgStatus(String agStatus) {
		this.agStatus = agStatus;
	}
	public String getBelongsto() {
		return belongsto;
	}
	public void setBelongsto(String belongsto) {
		this.belongsto = belongsto;
	}
	public String getIspart() {
		return ispart;
	}
	public void setIspart(String ispart) {
		this.ispart = ispart;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}