package za.co.liberty.web.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.contracting.MPEDTO;

public class MPEDataProvider extends SortableDataProvider{
	
	List list = new ArrayList();
	
	public MPEDataProvider(){
		//set default sort
		setSort("indivMpeDueDate",SortOrder.ASCENDING);
			
		}

	public MPEDataProvider(ArrayList<MPEDTO> list){
		
		//set default sort
		setSort("indivMpeDueDate",SortOrder.ASCENDING);
		// important or you'll get a null pointer on line 40
		if(list == null)
			list = new ArrayList();
				
		this.list = list;

	}


	public long size() {
		return list != null? list.size():0;
	}

	public IModel model(final Object object) {
		return new AbstractReadOnlyModel() {
			public Object getObject() {
				return object;
			}
		};
	}

//	@Override
//	public Iterator iterator(long start, long end) {
//		if (list.size()==0) {
//			return (Iterator) Collections.emptyList().iterator();
//		}
//		
//	// #WICKETFIX #WICKETTEST - Fix the sort function
//		/* Sort the list */
////		if (getSort() != null) {
////			if (getSort().isAscending()) {
////				Collections.sort(list, new Comparator<T>() {
////				});
////			} else {
////				Collections.reverse(list);
////			}
////
////		}
//
//		/* Return an iterator */
//		if (list.size() <= end) {
//			return list.subList((int)start, (int)end).listIterator();
//		} else {
//			return list.subList((int)start, list.size()).listIterator();
//		}
//	}
	
	/*
	 * 
	 */
	@Override
	public Iterator iterator( long first,  long count) {
		List newList = new ArrayList();
		
		SortParam sortParam = getSort();
		final boolean isAscending = sortParam.isAscending();
		final String column = (String) sortParam.getProperty();
		
		newList.addAll(list.subList((int)first, (int)(first + count)));

		 Collections.sort(newList, new Comparator() {

	            public int compare(Object obj1, Object obj2) {
	                PropertyModel model1 = new PropertyModel(obj1, column);
	                PropertyModel model2 = new PropertyModel(obj2, column);

	                Object modelObject1 = model1.getObject();
	                Object modelObject2 = model2.getObject();

	                
	                int compare = 0;
	                if (modelObject1 == null || modelObject2 == null) {
	                	// Deal with nulls, if both are null they will be equal = 0
	                	if (modelObject1 == null && modelObject2 != null) {
	                		compare = -1;
	                	} else if (modelObject1 == null && modelObject2 != null) {
	                		compare = 1;
	                	}
	                } else {
	                	compare = ((Comparable) modelObject1).compareTo(modelObject2);
	                }
	                
	                if (!isAscending)
	                    compare *= -1;

	                return compare;
	            }
	        });

		return newList.iterator();
	}
	
}
