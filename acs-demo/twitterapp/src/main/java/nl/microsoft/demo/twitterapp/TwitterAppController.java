package nl.microsoft.demo.twitterapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TwitterAppController {

    @RequestMapping("/status")
    public String index(Model model) {
    	return "status";
    }
}
