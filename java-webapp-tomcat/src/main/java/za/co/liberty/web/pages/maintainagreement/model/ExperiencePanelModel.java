package za.co.liberty.web.pages.maintainagreement.model;

import java.io.Serializable;
/**
 * 
 * @author AAA1210
 *
 */
public class ExperiencePanelModel implements Serializable {
	

	private long years;
	private long monthsinTotal;
	private long months;

	public long getMonths() {
		return months;
	}

	public void setMonths(long months) {
		this.months = months;
	}

	public long getYears() {
		return years;
	}

	public void setYears(long years) {
		this.years = years;
	}

	public long getMonthsinTotal(){
		 monthsinTotal=(years*12)+months;
		return monthsinTotal;
	}

	
	/*private long monthsOfExperience;

	public long getMonthsOfExperience() {
		return monthsOfExperience;
	}

	public void setMonthsOfExperience(long monthsOfExperience) {
		this.monthsOfExperience = monthsOfExperience;
	}

	public long getMonths() {
		return (monthsOfExperience % 12);
	}

	public long getYears() {

		return (monthsOfExperience / 12);

	}*/
	
}
