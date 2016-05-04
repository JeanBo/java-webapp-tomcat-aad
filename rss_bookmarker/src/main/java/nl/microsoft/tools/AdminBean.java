package nl.microsoft.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminBean {

	private static final Logger logger = LoggerFactory.getLogger(AdminBean.class);

	private static XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	private static String DIR = "/tmp/";
	//private static XMLEvent endSection = eventFactory.createDTD("\n");
	//private static XMLEvent tabSection = eventFactory.createDTD("\t");
	
	List<RssFile> fileList = new ArrayList<RssFile>();

	public List<RssFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<RssFile> fileList) {
		this.fileList = fileList;
	}


	public void init() {
		File folder = new File(DIR);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			logger.info("evaluating file: " + file.getName());
			if (file.isFile() && file.getName().endsWith(".xml")) {
				RssFile rssFile = new RssFile();
				rssFile.setFileName(file.getName());
				fileList.add(rssFile)asdasdasd;
			}
		}
	}

	@Transactional
	public void deleteFile(String selectedfile) {
		File fileToDelete = new File(DIR + selectedfile);
		fileToDelete.delete();
	}

	@Transactional
	public String loadFile(String selectedfile) {
		StringBuffer result = new StringBuffer();
		try {
			File fileToLoad = new File(DIR + selectedfile);
			String line;
			if (fileToLoad.canRead()) {
				FileReader fileReader = new FileReader(DIR+selectedfile);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				while ((line = bufferedReader.readLine()) != null) {
					int tabs = line.indexOf("\t");
					for(int i=0; i<tabs; i++ ){
						logger.info("adding tab...");
						result.append("\t");
					}
					result.append(line+"\n");
				}
				bufferedReader.close();
			}
		} catch (FileNotFoundException e) {
			logger.error("file not found!! exception: "+e.getMessage());
		} catch (IOException ex){
			logger.error("IO Exception!! exception: "+ex.getMessage());			
		}
		return result.toString();
	}

	//	TODO: create some kind of XML validation according to a rss ver 2.0 schema
	@Transactional
	public void saveFile(String filename, String content) throws IOException {
		logger.info("writing to file: "+filename);
		File file = new File(DIR+filename);
		FileOutputStream fop = new FileOutputStream(file);
		byte[] contentInBytes = content.getBytes();
		fop.write(contentInBytes);
		fop.flush();
		fop.close();
	}

	
	@Transactional
	public void createInitialFile(String filename) throws FileNotFoundException, XMLStreamException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		//if(!filename.endsWith(".xml"))
			//AdminBean.filename = filename+".xml";
		// create XMLEventWriter
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(DIR + filename));
		// create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);

		// create config open tag
		eventWriter.add(eventFactory.createStartElement("", "", "rss"));
		eventWriter.add(eventFactory.createAttribute("version", "2.0"));
		eventWriter.add(eventFactory.createCharacters("\n"));
		eventWriter.add(eventFactory.createCharacters("\t"));
		eventWriter.add(eventFactory.createStartElement("", "", "channel"));

		createNode(eventWriter, "title", "rss feeder", 2);
		createNode(eventWriter, "link", "http://portal.azure.com", 2);
		createNode(eventWriter, "description", "portal for azure", 2);

		createNodeItem(eventWriter, "microsoft corporate", "http://www.microsoft.com", "great company");

		eventWriter.add(eventFactory.createCharacters("\n"));
		eventWriter.add(eventFactory.createCharacters("\t"));

		eventWriter.add(eventFactory.createEndElement("", "", "channel"));
		eventWriter.add(eventFactory.createCharacters("\n"));
		eventWriter.add(eventFactory.createEndElement("", "", "rss"));
		eventWriter.add(eventFactory.createCharacters("\n"));

		eventWriter.close();

	}

	private void createNodeItem(XMLEventWriter eventWriter, String title, String link, String description)
			throws XMLStreamException {
		doIndentation(eventWriter, 2);
		eventWriter.add(eventFactory.createStartElement("", "", "item"));
		createNode(eventWriter, "title", title, 3);
		createNode(eventWriter, "link", link, 3);
		createNode(eventWriter, "description", description, 3);
		doIndentation(eventWriter, 2);
		eventWriter.add(eventFactory.createEndElement("", "", "item"));
	}

	private void doIndentation(XMLEventWriter eventWriter, int times) throws XMLStreamException {
		eventWriter.add(eventFactory.createCharacters("\n"));
		for (int i = 0; i < times; i++) {
			eventWriter.add(eventFactory.createCharacters("\t"));
		}
	}

	private void createNode(XMLEventWriter eventWriter, String name, String value, int indentation) throws XMLStreamException {
		eventWriter.add(eventFactory.createCharacters("\n"));
		for (int i = 0; i < indentation; i++) {
			eventWriter.add(eventFactory.createCharacters("\t"));
		}
		eventWriter.add(eventFactory.createStartElement("", "", name));
		eventWriter.add(eventFactory.createCharacters(value));
		eventWriter.add(eventFactory.createEndElement("", "", name));
	}

}
