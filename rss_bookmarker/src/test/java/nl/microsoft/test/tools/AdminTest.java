package nl.microsoft.test.tools;

import java.io.File;
import java.io.FileNotFoundException;

import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.microsoft.tools.AdminBean;

@RunWith(Arquillian.class)
public class AdminTest {
	
    @Inject
	private AdminBean adminBean;

	private static final Logger testLogger = LoggerFactory.getLogger(AdminTest.class);
	String fileName = "popje.xml";

	@Test
	public void doTest() throws FileNotFoundException, XMLStreamException{
		adminBean.createInitialFile(fileName);
		File file = new File("/tmp/"+fileName);
		assert(file.exists());
	}
	
	@After
	public void cleanup(){
		testLogger.info("cleaning up after myself..");
		File file = new File("/tmp/"+fileName);
		file.delete();
	}
	
	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "rssBookmarker.war")
                .addPackage("nl.microsoft.tools")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	
}
