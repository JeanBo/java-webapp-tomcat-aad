package za.co.liberty.web.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import za.co.liberty.dto.contracting.ContractEnquiryDTO;
import za.co.liberty.dto.contracting.ContractSearchResultDTO;

public class ContractSearchDataProvider extends SortableDataProvider implements Comparator {

	private static final long serialVersionUID = 1L;
	
	List list = new ArrayList();
	
	public ContractSearchDataProvider(){
		//set default sort
		setSort("policyStartDt",SortOrder.ASCENDING);
			
	}
	
	public ContractSearchDataProvider(ContractEnquiryDTO contractEnqDTO){
		//set default sort
		setSort("policyStartDt",SortOrder.ASCENDING);
		// important or you'll get a null pointer on line 40
				
		list = contractEnqDTO.getContractSrchResults();

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


	public Iterator iterator(int start, int end) {
		if(getSort().isAscending())
			Collections.sort(list,this);
		else
			Collections.reverse(list);
		
		ArrayList<ContractSearchResultDTO> newList = new ArrayList<ContractSearchResultDTO>();
		if(list.size() <= end)
			newList.addAll(list.subList(start,end));
		else
			newList.addAll(list.subList(start,list.size()));
		return newList.iterator();
	}



	public int compare(Object contractEnquiryDTO1, Object contractEnquiryDTO2) {
		SortParam sortParam = getSort();
		
		String property = ((String)sortParam.getProperty());
		
		String camelCase = property.substring(0,1).toUpperCase() + 
				property.substring(1, property.length());
		
		try {
			Method method1 =  contractEnquiryDTO1.getClass().getMethod("get" + camelCase,null);
			Object obj1 = method1.invoke(contractEnquiryDTO1, null);
			Method method2 =  contractEnquiryDTO2.getClass().getMethod("get" + camelCase,null);
			Object obj2 = method2.invoke(contractEnquiryDTO2, null);
			
			if(obj1 == null && obj2 == null) 
				return 0;
			if(obj1 == null) 
				return -1;
			if(obj2 == null)
				return 1;
			
			if(obj1 instanceof BigDecimal)
				return ((BigDecimal)obj1).compareTo((BigDecimal)obj2);
			if(obj1 instanceof Date)
				return ((Date)obj1).compareTo((Date)obj2);
			
			if(obj1 instanceof String)
				return ((String)obj1).compareTo((String)obj2);

				
		
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

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

}
