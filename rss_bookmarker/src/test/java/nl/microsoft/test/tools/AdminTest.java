package nl.microsoft.test.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@RunWith(Arquillian.class)
public class AdminTest {
	
    //@Inject
	//private AdminBean adminBean;

	private static final Logger testLogger = LoggerFactory.getLogger(AdminTest.class);

	/*
	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap.create(WebArchive.class, "rssBookmarker.war")
                .addPackage("nl.microsoft.tools")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}
	
	@Before
	public void init(){
		testLogger.info("initializing unit test");
		//if(adminBean==null)
			//fail("adminBean == null, not initialized");
	}
	
	@Test
	public void testAdmin() throws FileNotFoundException, XMLStreamException{
		AdminBean aBean = new AdminBean();
		aBean.createFile("poep.xml");
	}
	*/
}
