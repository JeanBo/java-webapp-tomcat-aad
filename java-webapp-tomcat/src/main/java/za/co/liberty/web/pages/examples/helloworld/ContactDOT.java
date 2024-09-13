package za.co.liberty.web.pages.examples.helloworld;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Test Data Object for test project use only
 * 
 * @author JZB0608
 *
 */
public class ContactDOT implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String firstName;
	private String lastName;
	private String homePhone;
	private String cellPhone;
	
	
	public ContactDOT(Long id, String firstName, String lastName, String homePhone, String cellPhone) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.cellPhone = cellPhone;
		this.homePhone = homePhone;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getHomePhone() {
		return homePhone;
	}
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	public String getCellPhone() {
		return cellPhone;
	}
	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}
	
	public static List<ContactDOT> getExampleList() {
		List<ContactDOT> list = new ArrayList<ContactDOT>();
		list.add(new ContactDOT(1L, "Jean", "Bodemer", "083 111 1122","083 111 1111"));
		list.add(new ContactDOT(2L, "Piet", "Pompies", "083 222 1122","083 222 1111"));
		list.add(new ContactDOT(3L, "Toon", "Meyer", "083 333 1122","083 333 1111"));
		list.add(new ContactDOT(4L, "Pelo", "Dladla", "083 444 1122","083 444 1111"));
		list.add(new ContactDOT(5L, "Philani", "TheDad", "083 555 1122","083 555 1111"));
		list.add(new ContactDOT(6L, "Jean", "Bodemer", "083 111 1122","083 111 1111"));
		list.add(new ContactDOT(7L, "Piet", "Pompies", "083 222 1122","083 222 1111"));
		list.add(new ContactDOT(8L, "Toon", "Meyer", "083 333 1122","083 333 1111"));
		list.add(new ContactDOT(9L, "Pelo", "Dladla", "083 444 1122","083 444 1111"));
		list.add(new ContactDOT(10L, "Philani", "TheDad", "083 555 1122","083 555 1111"));
		list.add(new ContactDOT(11L, "Sipho", "Motale", "083 444 1122","083 444 1111"));
		list.add(new ContactDOT(12L, "Peeps", "People", "083 555 1122","083 555 1111"));
		return list;
	}
	
}
