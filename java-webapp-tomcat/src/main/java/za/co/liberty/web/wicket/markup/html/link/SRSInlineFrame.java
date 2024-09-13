package za.co.liberty.web.wicket.markup.html.link;


import org.apache.wicket.Page;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.util.string.Strings;

/**
 * <p>Basically a copy of {@linkplain InlineFrame} with the added height attribute
 * and allowance of div tags.</p>
 * 
 * <p>The {@linkplain #onComponentTag(ComponentTag)} is final in  {@linkplain InlineFrame} so
 * I had to copy the source.</p>
 * 
 * @author JZB0608 - 17 Mar 2010
 *
 */
public class SRSInlineFrame extends InlineFrame {
	
	private static final long serialVersionUID = 1L;


	private String height;
	
//	public <C extends Page> SRSInlineFrame(String id, Class<C> c) {
//		super(id, c);
//	}
	
	public SRSInlineFrame(String id, PageProvider page) {
		super(id, page);
	}
	
	/**
	 * Handles this frame's tag.
	 * 
	 * @param tag
	 *            the component tag
	 * @see org.apache.wicket.Component#onComponentTag(ComponentTag)
	 */
	@Override
	protected void onComponentTag(final ComponentTag tag)
	{
		checkComponentTag(tag, "iframe", "div");
		
		tag.setName("iframe");

		// Set href to link to this frame's frameRequested method
		CharSequence url = getURL();

		// generate the src attribute
		tag.put("src", Strings.replaceAll(url, "&", "&amp;"));
		if (height!=null) {
			tag.put("height", height);
		}
		super.onComponentTag(tag);
	}

	/**
	 * Override the height attribute
	 * 
	 * @param height
	 * @return
	 */
	public SRSInlineFrame setHeightAttribute(String height) {
		this.height = height;
		return this;
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