package nl.microsoft.tools;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean
@RequestScoped
public class AdminController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

	
    @Inject
	private AdminBean adminBean;
    
    String selectedfile;
	String serverUrl;
	String fileContent;
	String createFile; 
	
	public String getServerUrl() {
		return serverUrl;
		//HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		//return req.getRequestURL().toString();
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public String getCreateFile() {
		return createFile;
	}

	public void setCreateFile(String createFile) {
		this.createFile = createFile;
	}	
    
	public String getSelectedfile() {
		return selectedfile;
	}

	public void setSelectedfile(String selectedfile) {
		this.selectedfile = selectedfile;
	}

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

	//	Links to Actual code ;)
	
	@PostConstruct
	public void init(){
		logger.info("initializing....");
		adminBean.init();
	}
	
	public String createFile() throws FileNotFoundException, XMLStreamException{
		logger.info("trying to CREATE file.."+filename);
		adminBean.createInitialFile(filename);
		return "refresh-hack";
	}
	
	public String deleteFile(){
		logger.info("trying to delete file.."+selectedfile);
		adminBean.deleteFile(selectedfile);
		adminBean.init();
		return "refresh-hack";
	}

	public String editFile(){
		logger.info("trying to edit file.."+selectedfile);
		fileContent = adminBean.loadFile(selectedfile);
		setServerUrl(selectedfile);
		return "editfile";
	}
	
	public String saveFile() throws IOException{
		String fileToSave = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("hidden1");
		logger.info("trying to save file via adminController.."+fileToSave);
		adminBean.saveFile(fileToSave,fileContent);
		return "refresh-hack";
	}


	
}
