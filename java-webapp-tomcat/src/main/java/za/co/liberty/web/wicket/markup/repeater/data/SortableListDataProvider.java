package za.co.liberty.web.wicket.markup.repeater.data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.core.util.lang.PropertyResolver;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.util.SingleSortState;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * <p>A data provider that is based on a list but also allows 
 * for sorting.</p>
 * 
 * <p>Note that the object retrieved with the sortproperty have 
 * to implement {@link Comparable}.</p>
 * 
 * @author JZB0608 - 29 May 2008
 * Modified by Dean Scott(DZS2610) - 23 July
 * Modified to preserve model objects
 * NB Please use getGridData() when getting the data for usage
 *  The reason for this is that the list might change 
 * 
 */
public class SortableListDataProvider<T extends Object> implements 
		ISortableDataProvider<T, T>, IDataProvider<T>, Comparator<T> {

	private static final long serialVersionUID = -1503468966666940287L;

	private SingleSortState<T> state = new SingleSortState<T>();

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
	public SortableListDataProvider(List<T> list) {
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
	 * @see ISortableDataProvider#getSortState()
	 */
	public ISortState getSortState() {
		if (getGridData().size()==0) {
			/* Fix issue with DataProviderAdapter */ 
			return null;
		}
		return state;
	}

	/**
	 * @see ISortableDataProvider#setSortState(ISortState)
	 */
	public void setSortState(ISortState state) {
		if (!(state instanceof SingleSortState)) {
			throw new IllegalArgumentException(
					"argument [state] must be an instance of SingleSortState, but it is ["
							+ state.getClass().getName() + "]:["
							+ state.toString() + "]");
		}
		this.state = (SingleSortState) state;
	}

	/**
	 * @see IDataProvider#iterator(int, int)
	 */
	public Iterator<? extends T> iterator(long start, long end) {
		list = getGridData();
		if (list.size()==0) {
			return (Iterator<? extends T>) Collections.emptyList().iterator();
		}
		
		/* Sort the list */
		if (state.getSort() != null) {
			if (state.getSort().isAscending()) {
				Collections.sort(list, this);
			} else {
				Collections.reverse(list);
			}

		}

		/* Return an iterator */
		if (list.size() <= end) {
			return list.subList((int)start, (int)end).listIterator();
		} else {
			return list.subList((int)start, list.size()).listIterator();
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


	/**
	 * Compare with sort property
	 */
	public int compare(T o1, T o2) {
		String sortProperty = (String) state.getSort().getProperty();
		
		Object obj1 = PropertyResolver.getValue(sortProperty,o1);
		Object obj2 = PropertyResolver.getValue(sortProperty,o2);
		
//		Logger.getLogger(this.getClass()).info("Sort by property " + sortProperty + "  with values obj1=" + obj1
//				+ "  ,obj2=" + obj2);
		
//		Object obj1 = new PropertyModel(o1, sortProperty).getObject();
//		Object obj2 = new PropertyModel(o2, sortProperty).getObject();
		
		if (obj1==null && obj2==null) return 0;
		if (obj1==null) return -1;
		if (obj2==null) return 1;
		
		if (obj1 instanceof Comparable) {
			return ((Comparable)obj1).compareTo((Comparable)obj2);
		}
		
		return 0;
	}

}