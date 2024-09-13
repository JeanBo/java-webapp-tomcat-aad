package za.co.liberty.web.wicket.ajax;

import java.nio.charset.Charset;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.encoding.UrlEncoder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.time.Duration;

/**
 * Helper behavior that triggers a download once an AjaxEvent is complete.
 *  
 * @author jzb0608 2014-02-18
 *
 */
public abstract class AjaxDownload extends AbstractAjaxBehavior {
	private static final long serialVersionUID = 1L;
	
	private boolean addAntiCache;

	public AjaxDownload() {
		this(true);
	}

	public AjaxDownload(boolean addAntiCache) {
		super();
		this.addAntiCache = addAntiCache;
	}

	/**
	 * Call this method to initiate the download.
	 */
	public void initiate(AjaxRequestTarget target) {
		String url = getCallbackUrl().toString();

		if (addAntiCache) {
			url = url + (url.contains("?") ? "&" : "?");
			url = url + "antiCache=" + System.currentTimeMillis();
		}

		// the timeout is needed to let Wicket release the channel
		target.appendJavaScript("setTimeout(\"window.location.href='" + url
				+ "'\", 100);");
	}

	public void onRequest() {
	
		final String fn = UrlEncoder.QUERY_INSTANCE.encode(getFileName(),
				Charset.defaultCharset());

		IResourceStream resourceStream = getResourceStream();
//
//		getComponent().getRequestCycle().setRequestTarget(
//				new ResourceStreamRequestTarget(resourceStream) {
//					@Override
//					public String getFileName() {
//						return fn;
//					}
//
//					@Override
//					public void respond(RequestCycle requestCycle) {
//						super.respond(requestCycle);
//					}
//				});

		// Wicket 7 implements handlers very differently, changes to below
		IRequestHandler d;
		ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(
//				new StringResourceStream(builder.toString(), "text/plain")
				resourceStream) {
			
			@Override
	        public void respond(IRequestCycle requestCycle) {
	            super.respond(requestCycle);
	        }
			
		};
	
		handler.setContentDisposition(ContentDisposition.ATTACHMENT);
		handler.setCacheDuration(Duration.ONE_HOUR);
		handler.setFileName(fn);
				
		RequestCycle.get().scheduleRequestHandlerAfterCurrent(handler);
		
	
	}

	/**
	 * Override this method for a file name which will let the browser prompt
	 * with a save/open dialog.
	 * 
	 * @see ResourceStreamRequestTarget#getFileName()
	 */
	protected abstract String getFileName();

	/**
	 * Hook method providing the actual resource stream.
	 */
	protected abstract IResourceStream getResourceStream();
}