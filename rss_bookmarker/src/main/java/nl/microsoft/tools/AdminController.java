package nl.microsoft.tools;

import java.io.FileNotFoundException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;

@ManagedBean
@RequestScoped
public class AdminController {

    @Inject
	private AdminBean adminBean;
    
	public AdminBean getAdminBean() {
		return adminBean;
	}

	public void setAdminBean(AdminBean adminBean) {
		this.adminBean = adminBean;
	}

	String filename;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void init(){
		adminBean.init();
	}
	
	public void createFile() throws FileNotFoundException, XMLStreamException{
		adminBean.createFile(filename);
		//return "../index";
	}
}
