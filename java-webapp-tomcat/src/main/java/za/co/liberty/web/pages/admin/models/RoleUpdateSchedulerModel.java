package za.co.liberty.web.pages.admin.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Page model for Role Update scheduler.
 * 
 * @author JZB0608 - 02 Jun 2008
 *
 */
public class RoleUpdateSchedulerModel implements Serializable {

	private static final long serialVersionUID = 9090605160508034003L;
	
	private boolean isActive;
	private boolean isEditing;
	
	private String statusDescription;
	private String name;
	private Date startDate;
	private boolean isRepeating;
	private long interval;
	
	
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public boolean isEditing() {
		return isEditing;
	}
	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isRepeating() {
		return isRepeating;
	}
	public void setRepeating(boolean isRepeating) {
		this.isRepeating = isRepeating;
	}
	public long getInterval() {
		return interval;
	}
	public void setInterval(long interval) {
		this.interval = interval;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	
}
