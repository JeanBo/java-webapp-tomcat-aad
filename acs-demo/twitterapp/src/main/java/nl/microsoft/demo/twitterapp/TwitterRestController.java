package nl.microsoft.demo.twitterapp;



import javax.annotation.PostConstruct;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@RestController
public class TwitterRestController{

	Twitter twitter = null;
	
	//	Hardcoded account, I am sharing this deliberately cause I don't care
	@PostConstruct
	public void init(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey("UC1Xzpv8z5jj7bMZiLr0dp1tD")
        .setOAuthConsumerSecret("vPWCYl3QNNJprfZfCcEHDNFBCcp4DwjuYltSjnCRV7GeHqOYxy")
        .setOAuthAccessToken("3311613753-DrV6q4GjxWmPR886WFOqAoR2e3608Z3ZUZdKtQW")
        .setOAuthAccessTokenSecret("cl51GBhxOPXSoM4LzLxTk7G2m83Z0kwtqMlnHKuPb7HBf");
        twitter = new TwitterFactory(cb.build()).getInstance();
	}


	
    @RequestMapping("/search/{hashtag}")
    public String index(@PathVariable("hashtag") String hashTag) {
    	String result = null;
        try {
			result = getTopicMessages(hashTag,10);
		} catch (TwitterException | JSONException e) {
			e.printStackTrace();
		}
        return result;
    }

    @RequestMapping("/search/{hashtag}/{count}")
    public String index(@PathVariable("hashtag") String hashTag,@PathVariable("count") int count) {
    	String result = null;
        try {
			result = getTopicMessages(hashTag,count);
		} catch (TwitterException | JSONException e) {
			e.printStackTrace();
		}
        return result;
    }

    
    private JSONObject getTopicMessages(String hashTagTopic) throws TwitterException, JSONException{
    
    	if(twitter==null)
        	init();
    
    	Query query = new Query(hashTagTopic);
    	query.count(100);
    	QueryResult qResult = twitter.search(query);
    	JSONArray jsonArray = new JSONArray();

    	JSONObject result = new JSONObject();
    	JSONObject root = new JSONObject();

    	for (Status status : qResult.getTweets()) {
            JSONObject jsonObj = new JSONObject();
            jsonObj.append("user", status.getUser().getScreenName());
            jsonObj.append("message", status.getText());
            jsonArray.put(jsonObj);
    	}
    	result.put("results",jsonArray);
    	root.put("Root", result);
    	return root;
    }

    private String getTopicMessages(String hashTagTopic,int size) throws TwitterException, JSONException{

    	StringBuffer result = new StringBuffer();
    	if(twitter==null)
        	init();
    
    	Query query = new Query(hashTagTopic);
    	query.count(size);
    	int counter=0;
    	QueryResult qResult = twitter.search(query);
    	result.append("<ul>");
    	for (Status status : qResult.getTweets()) {
    		counter++;
    		result.append("<li>");
            result.append(counter);
            result.append("  ,  ");
            result.append(status.getUser().getScreenName());
            result.append(" <B>===== SAYS =====</B>");
            result.append(status.getText());
            result.append("</li>");
    	}
    	result.append("</ul>");
    	return result.toString();
    }


}
