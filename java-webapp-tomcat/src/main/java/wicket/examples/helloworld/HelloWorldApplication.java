package wicket.examples.helloworld;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.protocol.http.WebApplication;


import org.apache.wicket.settings.DebugSettings;
import wicket.example.tree.BeginnersTreePage;

import wicket.examples.datepick.DatesPage;
import wicket.examples.modalwindow.ModalWindowPage;

public class HelloWorldApplication extends WebApplication {
    public HelloWorldApplication() {
//        setDebugSettings(DebugSettings.)
    }

    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class getHomePage() {
        System.out.println("Starting the application");
       // return ModalWindowPage.class;
    	//return DatesPage.class;
    	return HelloWorld.class;
    }
    
   
   }