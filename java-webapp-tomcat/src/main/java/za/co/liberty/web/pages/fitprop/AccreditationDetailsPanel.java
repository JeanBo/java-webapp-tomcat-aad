package za.co.liberty.web.pages.fitprop;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.srs.integration.api.moodle.qualification.Accreditation;
import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Panel to display a very simplistic view of the Accreditation details for a party
 * 
 * @author DZS2610
 *
 */
public class AccreditationDetailsPanel extends BasePanel implements ISecurityPanel {
    private static final long serialVersionUID = 1L;

    private Form panelForm;

    private SRSDataGrid accredDetailsGrid;

    private boolean initialised;

    private GetQualifications moodleResponse;

    /**
     * Default constructor using the moodle web service response
     * 
     * @param id
     * @param editState
     * @param parentPage
     */
    public AccreditationDetailsPanel(String id, GetQualifications moodleResponse, EditStateType editState, Page parentPage) {
	super(id, editState, parentPage);
	this.moodleResponse = moodleResponse;
    }

    public Class getPanelClass() {
	return AccreditationDetailsPanel.class;
    }

    /**
     * Load the components on the page on first render, so that the components are only generated when the page is displayed
     */
    @Override
    protected void onBeforeRender() {
	if (!initialised) {
	    initialised = true;
	    add(panelForm = createForm("accredForm"));

	}
	super.onBeforeRender();
    }

    /**
     * Create the form for this panel
     * 
     * @param id
     * @return
     */
    private Form createForm(String id) {
	Form form = new Form(id);
	form.add(accredDetailsGrid = createAccredDetailsGrid("accredDetails"));
	return form;
    }

    /**
     * Create a grid for the accreditation roles
     * 
     * @return
     */
    private SRSDataGrid createAccredDetailsGrid(String id) {
	List<Accreditation> accredds = null;
	if (moodleResponse != null && moodleResponse.getAccreditation() != null) {
	    accredds = moodleResponse.getAccreditation();
	}
	if (accredds == null) {
	    accredds = new ArrayList<Accreditation>();
	}
	SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<Accreditation>(accredds)), getAccredColumns(),
		getEditState(), null);
	grid.setCleanSelectionOnPageChange(false);
	grid.setClickRowToSelect(false);
	grid.setAllowSelectMultiple(true);
	// grid.setGridWidth(650, GridSizeUnit.PIXELS);
	grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
	grid.setRowsPerPage(5);
	grid.setContentHeight(140, SizeUnit.PX);
	return grid;
    }

    /**
     * Get the list of columns for the grid
     * 
     * @return
     */
    private List<IGridColumn> getAccredColumns() {//
	Vector<IGridColumn> cols = new Vector<IGridColumn>();
	// add in the exam code col
	cols.add(new SRSDataGridColumn<Accreditation>("examCode", new Model<String>("Exam Code"), "examCode", "examCode", getEditState()).setInitialSize(70));
	// add in the name col
	cols.add(new SRSDataGridColumn<Accreditation>("examName", new Model<String>("Name"), "examName", "examName", getEditState()).setInitialSize(500)
		.setWrapText(true));
	// exam date col
	cols.add(new SRSDataGridColumn<Accreditation>("examDate", new Model<String>("Date"), "examDate", "examDate", getEditState()).setInitialSize(80)
		.setWrapText(true));
	// passed col
	cols.add(new SRSDataGridColumn<Accreditation>("examPassed", new Model<String>("Passed"), "examPassed", "examPassed", getEditState()).setInitialSize(50)
		.setWrapText(true));
//		examResultPercentage col
	cols.add(new SRSDataGridColumn<Accreditation>("examResultPercentage", new Model<String>("Result %"), "examResultPercentage", "examResultPercentage",
		getEditState()) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state,
		    Accreditation data) {
		String display = "";
		if (data != null && data.getExamResultPercentage() != null) {
		    display = data.getExamResultPercentage()
			    .setScale(1, RoundingMode.HALF_UP)
			    .toPlainString() + "%";
		}
		return HelperPanel.getInstance(componentId, new Label("value", display));
	    }

	}.setInitialSize(60)
		.setWrapText(true));
//		examPassPercentage col
	cols.add(new SRSDataGridColumn<Accreditation>("examPassPercentage", new Model<String>("Pass %"), "examPassPercentage", "examPassPercentage",
		getEditState()) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state,
		    Accreditation data) {
		String display = "";
		if (data != null && data.getExamPassPercentage() != null) {
		    display = data.getExamPassPercentage()
			    .setScale(1, RoundingMode.HALF_UP)
			    .toPlainString() + "%";
		}
		return HelperPanel.getInstance(componentId, new Label("value", display));
	    }
	}.setInitialSize(50)
		.setWrapText(true));
	return cols;
    }
}
