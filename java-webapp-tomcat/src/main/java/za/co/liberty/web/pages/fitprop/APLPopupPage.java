package za.co.liberty.web.pages.fitprop;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.agreement.IAgreementManagement;
import za.co.liberty.dto.agreement.ProductAccreditationDTO;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.helpers.util.SRSUtility;
import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.BaseWindowPage;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * APL Page to display all the products this advisor is allowed to sell
 * 
 * @author DZS2610
 *
 */
public class APLPopupPage extends BaseWindowPage {
    private static final long serialVersionUID = 1L;

    private long agreementNumber;

    private GetQualifications moodleData;

    private SRSDataGrid grid;

    private transient IAgreementManagement agreementManagement;

    public APLPopupPage(long agreementnumber, GetQualifications moodleData) {
	agreementNumber = agreementnumber;
	this.moodleData = moodleData;
    }

    @Override
    protected void onBeforeRender() {
	// add grid
	add(grid = createAPLDetailsGrid("appldetailsgrid", new ArrayList<ProductAccreditationDTO>()));
	// add self update after 100 millis to get the list
	add(new AbstractAjaxTimerBehavior(Duration.milliseconds(100)) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    protected void onTimer(AjaxRequestTarget target) {
		List<ProductAccreditationDTO> approvedproducts = getAgreementManagement().getProductAccreditationList(moodleData, agreementNumber, true, true);
		SRSDataGrid grid2 = createAPLDetailsGrid("appldetailsgrid", approvedproducts);
		grid.replaceWith(grid2);
		grid = grid2;
		target.add(grid);
		this.stop(target);

	    }
	});
	super.onBeforeRender();
    }

    /**
     * Create a grid for the accreditation roles
     * 
     * @return
     */
    private SRSDataGrid createAPLDetailsGrid(String id, List<ProductAccreditationDTO> approvedproducts) {

	approvedproducts = SRSUtility.makeListUniqueUsingField("productDescription", ProductAccreditationDTO.class, approvedproducts);

	SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<ProductAccreditationDTO>(approvedproducts)),
		getProductColumns(), EditStateType.VIEW, null);
	grid.setCleanSelectionOnPageChange(false);
	grid.setClickRowToSelect(false);
	grid.setAllowSelectMultiple(true);
	// grid.setGridWidth(650, GridSizeUnit.PIXELS);
	grid.setGridWidth(98, GridSizeUnit.PERCENTAGE);
	grid.setRowsPerPage(25);
	grid.setContentHeight(400, SizeUnit.PX);
	return grid;
    }

    /**
     * Get the list of columns for the grid
     * 
     * @return
     */
    private List<IGridColumn> getProductColumns() {
	Vector<IGridColumn> cols = new Vector<IGridColumn>();
	// qualificationCode col
	cols.add(new SRSDataGridColumn<ProductAccreditationDTO>("productDescription", new Model<String>("Product"), "productDescription", "productDescription",
		EditStateType.VIEW).setInitialSize(500)
			.setWrapText(true));
	return cols;
    }

    @Override
    public String getPageName() {
	return "Approved Product List for agreement " + agreementNumber;
    }

    /**
     * get the agreement manager bean
     * 
     * @return
     */
    public IAgreementManagement getAgreementManagement() {
	if (agreementManagement == null) {
	    try {
		agreementManagement = ServiceLocator.lookupService(IAgreementManagement.class);
	    } catch (NamingException e) {
		throw new CommunicationException(e);
	    }
	}
	return agreementManagement;
    }
}
