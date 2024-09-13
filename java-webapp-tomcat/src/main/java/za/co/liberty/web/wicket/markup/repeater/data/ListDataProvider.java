package za.co.liberty.web.wicket.markup.repeater.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A list data provider that does not provide sorting. 
 * 
 * @author JZB0608 - 08 Apr 2009
 *
 * @param <T>
 */
public class ListDataProvider<T extends Object> implements 
		IDataProvider {

	private static final long serialVersionUID = -1503468966666940287L;

	private List<T> list;
	
	/**
	 * Keep a list of ModelObje
	 */
	private HashMap<Object,IModel> modelObjects;
	

	/**
	 * Default constructor 
	 * 
	 * @param list
	 */
	public ListDataProvider(List<T> list) {
		this.list = list;
		createModelObjects();
	}
	
	/**
	 * Override this method if your data list always changes
	 * 
	 * @return
	 */
	protected List<T> getGridData(){
		return list;
	}
	
	/**
	 * Creates the Map of models
	 *
	 */
	private void createModelObjects(){
		if(getGridData() != null){
			modelObjects = new HashMap<Object, IModel>(getGridData().size());
			for(Object obj : getGridData()){
				modelObjects.put(obj, new Model((Serializable)obj));
			}
		}
	}

	/**
	 * @see IDataProvider#iterator(int, int)
	 */
	public Iterator  iterator(long start, long end) {
		list = getGridData();
		if (list.size()==0) {
			return Collections.emptyList().iterator();
		}
		if (list.size()<start) {
			start = 0;
		}
		/* Return an iterator */
		if (list.size() <= end) {
			return list.subList((int)start, (int)end).listIterator();
		} else {
			return list.subList((int)start, (int)list.size()).listIterator();
		}

	}

	/**
	 * @see IDataProvider#size()
	 */
	public long size() {
		return getGridData().size();
	}

	/**
	 * @see IDataProvider#model(Object)
	 */
	public IModel model(Object object) {
		if (getGridData().size()==0) {
			return null;
		}
		if(modelObjects.get(object) == null){
			modelObjects.put(object, new Model((Serializable)object));
		}
		return modelObjects.get(object);
	}

	/**
	 * @see org.apache.wicket.model.IDetachable#detach()
	 */
	public void detach() {
	}


}