package za.co.liberty.web.wicket.markup.html.grid;

import org.apache.wicket.markup.repeater.data.IDataProvider;

import com.inmethod.grid.DataProviderAdapter;

/**
 * Provides a way to actually get the dataProvider sent though
 * @author dzs2610
 *
 */
public class SRSDataProviderAdapter<T,S> extends DataProviderAdapter<T,S> {
	private static final long serialVersionUID = 1L;

	private final IDataProvider<T> provider;
	
	public SRSDataProviderAdapter(IDataProvider<T> provider) {
		super(provider);
		this.provider = provider;
	}

	public IDataProvider<T> getDataProvider() {
		return provider;
	}
}
