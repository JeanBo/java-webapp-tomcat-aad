package nl.microsoft.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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


public class AdminBean{

	private static final Logger logger = LoggerFactory.getLogger(AdminBean.class);
	
	private static String XML_BLOCK = "\n";
	private static String XML_INDENT = "\t";
	List<String> fileList = new ArrayList<String>();
	public List<String> getFileList() {
		return fileList;
	}

	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}

	XMLEventFactory eventFactory = XMLEventFactory.newInstance();

	public void init(){
		File folder = new File("/tmp/");
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			logger.info("evaluating file: "+file.getName());
			if (file.isFile() && file.getName().endsWith(".xml")) {
				fileList.add(file.getName());
			}
		}
	}
	
	@Transactional
	public void createFile(String filename) throws FileNotFoundException, XMLStreamException {
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// create XMLEventWriter
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream("/tmp/"+filename));

		// create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);

		// create config open tag
		eventWriter.add(eventFactory.createStartElement("", "", "rss"));
		eventWriter.add(eventFactory.createAttribute("version", "2.0"));
		//eventWriter.add(endSection);
		//eventWriter.add(tabSection);		
		eventWriter.add(eventFactory.createStartElement("", "", "channel"));

		createNode(eventWriter,"title","rss feeder",2);
		createNode(eventWriter,"link","http://w3schools.com",2);
		createNode(eventWriter,"description","rss feed for bookmarks",2);
		
		createNodeItem(eventWriter,"some test title","http://www.w3schools.com/xml/xml_rss.asp", "blablabla bla");
		
		//eventWriter.add(endSection);
		//eventWriter.add(tabSection);				

		eventWriter.add(eventFactory.createEndElement("", "", "channel"));
		//eventWriter.add(endSection);
		eventWriter.add(eventFactory.createEndElement("", "","rss"));	
		eventWriter.add(eventFactory.createCharacters("\n"));
		
		eventWriter.close();
		
	}


	private void createNodeItem(XMLEventWriter eventWriter, String title, String link, String description) throws XMLStreamException {
		doIndentation(eventWriter,2);
		eventWriter.add(eventFactory.createStartElement("", "", "item"));
		createNode(eventWriter,"title",title,3);		
		createNode(eventWriter,"link",link,3);		
		createNode(eventWriter,"description",description,3);
		doIndentation(eventWriter,2);
		eventWriter.add(eventFactory.createEndElement("", "", "item"));
	}
	  
	private void doIndentation(XMLEventWriter eventWriter,int times) throws XMLStreamException{
		//eventWriter.add(endSection);
		for (int i=0; i<times; i++){
			//eventWriter.add(tabSection);			
		}
	}
	
	private void createNode(XMLEventWriter eventWriter, String name, String value, int indentation) throws XMLStreamException {
		//eventWriter.add(endSection);
		for (int i=0; i<indentation; i++){
			//eventWriter.add(tabSection);			
		}
		eventWriter.add(eventFactory.createStartElement("", "", name));
		eventWriter.add(eventFactory.createCharacters(value));
		eventWriter.add(eventFactory.createEndElement("", "", name));
	}

}
