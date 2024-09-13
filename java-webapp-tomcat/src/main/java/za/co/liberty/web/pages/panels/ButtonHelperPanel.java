package za.co.liberty.web.pages.panels;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Helper that helps with the placement of various number of
 * buttons on a panel.
 * 
 * @author JZB0608 - 16 Apr 2008
 *
 */
public class ButtonHelperPanel extends Panel {

	private static final long serialVersionUID = 7064261285725855652L;

	/**
	 * Default constructor 
	 * 
	 * @param id
	 * @param butList
	 */
	ButtonHelperPanel(String id, Button... butList) {
		super(id);
		for (Button but : butList) {
			add(but);
		}
	}

	/**
	 * Retrieve a new instance of button helper for the specified number of
	 * buttons.  Note that the button id's should start with button1 and 
	 * increment from there.
	 * 
	 * @param id
	 * @param butList
	 * @return
	 */
	public static ButtonHelperPanel getInstance(String id, Button... butList) {
		
		switch (butList.length) {
		case 1:
			return new ButtonHelperPanelOne(id, butList);
		case 2:
			return new ButtonHelperPanelTwo(id, butList);
		case 3:
			return new ButtonHelperPanelThree(id, butList);
		case 4:
			return new ButtonHelperPanelFour(id, butList);
		case 5:
			return new ButtonHelperPanelFive(id, butList);
		case 6:
			return new ButtonHelperPanelSix(id, butList);
		case 7:
			return new ButtonHelperPanelSeven(id, butList);
		}
		throw new IllegalArgumentException("The system doesn't cater for the specified number of buttons");
	}
 
}

	/**
	 * A one button panel
	 * 
	 * @author JZB0608 - 16 Apr 2008
	 *
	 */
	class ButtonHelperPanelOne extends ButtonHelperPanel {
	
		private static final long serialVersionUID = -1302089390272564931L;
	
		public ButtonHelperPanelOne(String id, Button... butList) {
			super(id, butList);
		}
		
	}
	
	/**
	 * A two panel button.
	 * 
	 * @author JZB0608 - 16 Apr 2008
	 *
	 */
	class ButtonHelperPanelTwo extends ButtonHelperPanel {
	
	
		private static final long serialVersionUID = -6829619367834027224L;
	
		public ButtonHelperPanelTwo(String id, Button... butList) {
			super(id, butList);
		}
	}
	
	/**
	 * A three button panel
	 *  
	 * @author MZP0801
	 *
	 */
	class ButtonHelperPanelThree extends ButtonHelperPanel {
	
	
		private static final long serialVersionUID = -6829619367834027211L;
	
		public ButtonHelperPanelThree(String id, Button... butList) {
			super(id, butList);
		}
	}
	
	/**
	 * A five panel button.
	 * 
	 * @author JZB0608 - 16 Apr 2008
	 *
	 */
	class ButtonHelperPanelFive extends ButtonHelperPanel {
	
	
		private static final long serialVersionUID = -6829619367834027224L;
	
		public ButtonHelperPanelFive(String id, Button... butList) {
			super(id, butList);
		}
	}
	
	/**
	 * A six button panel.
	 * 
	 * @author DZS2610 - 07 July 2009
	 *
	 */
	class ButtonHelperPanelSix extends ButtonHelperPanel {
	
	
		private static final long serialVersionUID = -6829619367834027224L;
	
		public ButtonHelperPanelSix(String id, Button... butList) {
			super(id, butList);
		}
	}
	
	/**
	 * A four panel button.
	 * 
	 * @author JZB0608 - 16 Apr 2008
	 *
	 */
	class ButtonHelperPanelFour extends ButtonHelperPanel {
	
	
		private static final long serialVersionUID = -6829619367834027224L;
	
		public ButtonHelperPanelFour(String id, Button... butList) {
			super(id, butList);
		}
	}
	
	
	/**
	 * A seven panel button.
	 * 
	 * @author pks2802 - 16 Sep 2008
	 *
	 */
	class ButtonHelperPanelSeven extends ButtonHelperPanel {


		private static final long serialVersionUID = -6829619367834027224L;

		public ButtonHelperPanelSeven(String id, Button... butList) {
			super(id, butList);
		}
	}
	
