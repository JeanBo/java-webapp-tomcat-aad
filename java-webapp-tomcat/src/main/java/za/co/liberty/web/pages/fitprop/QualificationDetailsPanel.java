package za.co.liberty.web.pages.fitprop;

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

import za.co.liberty.srs.integration.api.moodle.qualification.Category;
import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
import za.co.liberty.srs.integration.api.moodle.qualification.Qualification;
import za.co.liberty.srs.integration.api.moodle.qualification.SubCategory;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.pages.panels.HelperPanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Panel to display a very simplistic view of the CPD details for a party
 * 
 * @author DZS2610
 *
 */
public class QualificationDetailsPanel extends BasePanel implements ISecurityPanel {
    private static final long serialVersionUID = 1L;

    private Form panelForm;

    private SRSDataGrid qualDetailsGrid;

    private boolean initialised;

    private GetQualifications moodleResponse;

    /**
     * Default constructor using the moodle web service response
     * 
     * @param id
     * @param editState
     * @param parentPage
     */
    public QualificationDetailsPanel(String id, GetQualifications moodleResponse, EditStateType editState, Page parentPage) {
	super(id, editState, parentPage);
	this.moodleResponse = moodleResponse;
    }

    public Class getPanelClass() {
	return QualificationDetailsPanel.class;
    }

    /**
     * Load the components on the page on first render, so that the components are only generated when the page is displayed
     */
    @Override
    protected void onBeforeRender() {
	if (!initialised) {
	    initialised = true;
	    add(panelForm = createForm("qualForm"));

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
	form.add(qualDetailsGrid = createQualificationDetailsGrid("qualDetails"));
	return form;
    }

    /**
     * Create a grid for the accreditation roles
     * 
     * @return
     */
    private SRSDataGrid createQualificationDetailsGrid(String id) {
	List<Qualification> quals = null;
	if (moodleResponse != null && moodleResponse.getCpds() != null) {
	    quals = moodleResponse.getQualifications();
	}
	if (quals == null) {
	    quals = new ArrayList<Qualification>();
	}
	SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<Qualification>(quals)), getQualColumns(), getEditState(),
		null);
	grid.setCleanSelectionOnPageChange(false);
	grid.setClickRowToSelect(false);
	grid.setAllowSelectMultiple(true);
	// grid.setGridWidth(650, GridSizeUnit.PIXELS);
	grid.setGridWidth(99, GridSizeUnit.PERCENTAGE);
	grid.setRowsPerPage(3);
	grid.setContentHeight(150, SizeUnit.PX);
	return grid;
    }

    /**
     * Get the list of columns for the grid
     * 
     * @return
     */
    private List<IGridColumn> getQualColumns() {
	Vector<IGridColumn> cols = new Vector<IGridColumn>();
	// qualificationCode col
	cols.add(new SRSDataGridColumn<Qualification>("qualificationCode", new Model<String>("Code"), "qualificationCode", "qualificationCode", getEditState())
		.setInitialSize(40));
	// qualificationDescription col
	cols.add(new SRSDataGridColumn<Qualification>("qualificationDescription", new Model<String>("Description"), "qualificationDescription",
		"qualificationDescription", getEditState()).setInitialSize(250)
			.setWrapText(true));
	// qualificationDate achieved
	cols.add(new SRSDataGridColumn<Qualification>("qualificationDate", new Model<String>("Date Achieved"), "qualificationDate", "qualificationDate",
		getEditState()).setInitialSize(90)
			.setWrapText(true));
////		qualificationType col
//		cols.add(new SRSDataGridColumn<Qualification>("qualificationType",
//				new Model<String>("Type"), "qualificationType", "qualificationType", getEditState()).setInitialSize(140).setWrapText(true));
	// institutionName col
	cols.add(new SRSDataGridColumn<Qualification>("institutionName", new Model<String>("Institution"), "institutionName", "institutionName", getEditState())
		.setInitialSize(140)
		.setWrapText(true));
////		institutionType col
//		cols.add(new SRSDataGridColumn<Qualification>("institutionType",
//				new Model<String>("Institution Type"), "institutionType", "institutionType", getEditState()).setInitialSize(140).setWrapText(true));
//		qualificationCompleted col
//		cols.add(new SRSDataGridColumn<Qualification>("statusIndicator",
//				new Model<String>("Completed"), "statusIndicator", "statusIndicator", getEditState()).setInitialSize(140).setWrapText(true));
////		nqfLevel col
	cols.add(new SRSDataGridColumn<Qualification>("nqfLevel", new Model<String>("NQF Level"), "nqfLevel", "nqfLevel", getEditState()).setInitialSize(60)
		.setWrapText(true));
//		credits col
	cols.add(new SRSDataGridColumn<Qualification>("qualificationCredits", new Model<String>("Credits"), "qualificationCredits", "qualificationCredits",
		getEditState()).setInitialSize(50)
			.setWrapText(true));
//		categories col
	cols.add(new SRSDataGridColumn<Qualification>("categories", new Model<String>("Categories"), "categories", "categories", getEditState()) {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state,
		    Qualification data) {
		String display = getCategoriesString(data);
		return HelperPanel.getInstance(componentId, new Label("value", display));
	    }

	}.setInitialSize(500)
		.setWrapText(true));
	return cols;
    }

    /**
     * get categories as a string to display
     * 
     * @return
     */
    public String getCategoriesString(Qualification qualificationData) {
	if (qualificationData != null && qualificationData.getCategories() != null && qualificationData.getCategories() != null
		&& qualificationData.getCategories()
			.size() > 0) {
	    StringBuilder builder = new StringBuilder(qualificationData.getCategories()
		    .size() * 5);
	    int count = 0;
	    for (Category cat : qualificationData.getCategories()) {
		// now loop through sub cats
		for (SubCategory sub : cat.getSubCategory()) {
		    if (sub.getName() != null && !sub.getCode()
			    .trim()
			    .equals("")) {
			if (count != 0) {
			    builder.append(", ");
			}
			count++;
			builder.append(cat.getName() + "." + sub.getCode() + "(" + sub.getName() + ")");
		    }
		}
	    }
	    return builder.toString();
	}
	return "";
    }
}
