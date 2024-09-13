package za.co.liberty.web.wicket.markup.html.link;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupException;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 * <p>An implementation of the HTML IFrame tag which links it's source
 * to any specified URL which does not have to be a Wicket page.</p>
 * 
 * <p>The HTML tag may be defined as <code>iframe or div</code> and will
 * be re-written as the correct type when being rendered.</p>
 * 
 * @author JZB0608 - 11 Feb 2010
 *
 */
public class ExternalInlineFrame extends WebMarkupContainer {

	private static final long serialVersionUID = 1L;
	
	private String height;
	
	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param model
	 * 		Url
	 */
	public ExternalInlineFrame(String id, IModel model) {
		super(id, model);
	}
	
	/**
	 * Override the height attribute
	 * 
	 * @param height
	 * @return
	 */
	public ExternalInlineFrame setHeightAttribute(String height) {
		this.height = height;
		return this;
	}
	
	/**
	 * Handles this frame's tag.
	 * 
	 * @param tag
	 *            the component tag
	 * @see org.apache.wicket.Component#onComponentTag(ComponentTag)
	 */
	protected final void onComponentTag(final ComponentTag tag) {

		checkComponentTag(tag, "iframe", "div");
		
		tag.setName("iframe");
		
		// Set href to link to this frame's frameRequested method
		CharSequence url = (String)getDefaultModel().getObject();

		// generate the src attribute
		tag.put("src", Strings.replaceAll(url, "&", "&amp;"));
		if (height!=null) {
			tag.put("height", height);
		}
		
		super.onComponentTag(tag);
	}

	
	/**
	 * Checks whether the given type has the expected name.
	 * 
	 * @param tag
	 *            The tag to check
	 * @param name
	 *            The expected tag name
	 * @throws MarkupException
	 *             Thrown if the tag is not of the right name
	 */
	protected final void checkComponentTag(final ComponentTag tag, final String ... nameList) {
		String tagName = tag.getName();
		
		for (String n : nameList) {
			if (tagName.equalsIgnoreCase(n)) 	{
				return;
			}
		}
		
		StringBuilder builder = new StringBuilder();
		for (String n : nameList) {
			if (builder.length()>0) {
				builder.append(" ,");
			}
			builder.append(n);
		}
		findMarkupStream().throwMarkupException(
				"Component " + getId() + " must be applied to a tag of type '" + nameList + "', not " +
					tag.toUserDebugString());
	}
	
}
