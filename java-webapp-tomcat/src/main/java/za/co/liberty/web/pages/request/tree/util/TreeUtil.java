/*
 * Created on 2005/08/03
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package za.co.liberty.web.pages.request.tree.util;

import java.text.SimpleDateFormat;

/**
 * @author gzg1812
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TreeUtil {

	public static String formatDate(java.util.Date date){
		if(date != null){
			SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
			return sdf.format(date);		
		} else {
			return "";
		}		
	}
}
