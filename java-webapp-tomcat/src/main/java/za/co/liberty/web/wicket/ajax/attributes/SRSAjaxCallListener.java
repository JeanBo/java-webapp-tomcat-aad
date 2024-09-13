package za.co.liberty.web.wicket.ajax.attributes;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.attributes.AjaxCallListener;

/**
 * Default implementation of AjaxCallListener.  This default implementation support the overlay function
 * in the SRS Application 
 * 
 * @author JZB0608
 *
 */
public class SRSAjaxCallListener extends AjaxCallListener {

	private static final long serialVersionUID = -8646950428881416931L;
	
	private boolean disableSelf = false;
	private boolean hideOnDone = true;
	
	public SRSAjaxCallListener() {
		this(false, true);
	}
	
	public SRSAjaxCallListener(boolean disableSelf) {
		this(disableSelf, true);
	}
	
	protected SRSAjaxCallListener(boolean disableSelf, boolean hideOnDone ) {
		this.disableSelf = disableSelf;
		this.hideOnDone = hideOnDone;
	}
	
	@Override
	public CharSequence getInitHandler(Component component) {
		CharSequence s =   super.getInitHandler(component);
		return ((disableSelf)?"this.disabled=true;" : "") 
				+ ((hideOnDone)?"overlay(true);" : "overlay('test');")+ ((s==null)?"":s);
	}
				
	@Override
	public CharSequence getDoneHandler(Component component) {	
		if (!hideOnDone) {
			return super.getDoneHandler(component);
		}
		CharSequence s =  super.getDoneHandler(component);
		return  "hideOverlay();" + ((s==null)?"":s);
	}
	
	/**
	 * Default where overlay is shown and hidden with done
	 * 
	 * @return
	 */
	public static SRSAjaxCallListener newShowOverlayDefault() {
		return new SRSAjaxCallListener();
	}
	
	/**
	 * Show overlay and disable the component it is attached to
	 * 
	 * @return
	 */
	public static SRSAjaxCallListener newShowOverlayWithDisableSelf() {
		return new SRSAjaxCallListener(true);
	}
	
	/**
	 * Show overlay that is only hidden on redirect or refresh
	 * 
	 * @return
	 */
	public static SRSAjaxCallListener newShowOverlayNoHideOnDone() {
		return new SRSAjaxCallListener(false, false);
	}
	
	/**
	 * Show overlay that is only hidden on redirect or refresh.   And disable 
	 * self when clicked.
	 * 
	 * @return
	 */
	public static SRSAjaxCallListener newShowOverlayNoHideWithDisableSelf() {
		return new SRSAjaxCallListener(false, true);
	}
	
}
