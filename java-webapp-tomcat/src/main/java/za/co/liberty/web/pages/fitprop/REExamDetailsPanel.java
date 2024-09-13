package za.co.liberty.web.pages.fitprop;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.fitprop.IFitAndProperGuiController;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
import za.co.liberty.srs.integration.api.moodle.qualification.RegulatoryExam;
import za.co.liberty.web.data.enums.EditStateType;
import za.co.liberty.web.pages.interfaces.ISecurityPanel;
import za.co.liberty.web.pages.panels.BasePanel;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid.GridSizeUnit;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGridColumn;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

/**
 * Panel to display a very simplistic view of the RE exam details for a party
 * 
 * @author DZS2610
 *
 */
public class REExamDetailsPanel extends BasePanel implements ISecurityPanel {
    private static final long serialVersionUID = 1L;

    private Form panelForm;

    private SRSDataGrid reDetailsGrid;

    private boolean initialised;

    private GetQualifications moodleResponse;

    long partyoid;

    private transient IFitAndProperGuiController guiController;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Default constructor using the moodle web service response
     * 
     * @param id
     * @param editState
     * @param parentPage
     */
    public REExamDetailsPanel(String id, long partyoid, GetQualifications moodleResponse, EditStateType editState, Page parentPage) {
	super(id, editState, parentPage);
	this.moodleResponse = moodleResponse;
	this.partyoid = partyoid;
    }

    public Class getPanelClass() {
	return REExamDetailsPanel.class;
    }

    /**
     * Load the components on the page on first render, so that the components are only generated when the page is displayed
     */
    @Override
    protected void onBeforeRender() {
	if (!initialised) {
	    initialised = true;
	    add(panelForm = createForm("reForm"));

	}
	super.onBeforeRender();
    }

    /**
     * display the Re1 due date
     * 
     * @param id
     * @return
     */
    private Label createRE1Date(String id) {
	String reDate = "";
	Date re = getFitAndProperGuiController().getRE1DueDate(partyoid);
	if (re != null) {
	    reDate = "<br/>Level 1 RE Due Date: " + dateFormat.format(re);
	}
	return (Label) new Label(id, reDate).setEscapeModelStrings(false)
		.setRenderBodyOnly(true);
    }

    /**
     * display the Re2 due date
     * 
     * @param id
     * @return
     */
    private Label createRE2Date(String id) {
	String reDate = "<br/>";
	Date re = getFitAndProperGuiController().getRE2DueDate(partyoid);
	if (re != null) {
	    reDate = "<br/>Level 2 RE Due Date: " + dateFormat.format(re) + "<br/><br/>";
	}
	return (Label) new Label(id, reDate).setEscapeModelStrings(false)
		.setRenderBodyOnly(true);
    }

    /**
     * Create the form for this panel
     * 
     * @param id
     * @return
     */
    private Form createForm(String id) {
	Form form = new Form(id);
	form.add(reDetailsGrid = createREDetailsGrid("reDetails"));
	form.add(createRE1Date("re1DueDate"));
	form.add(createRE2Date("re2DueDate"));
	return form;
    }

    /**
     * Create a grid for the re details
     * 
     * @return
     */
    private SRSDataGrid createREDetailsGrid(String id) {
	List<RegulatoryExam> reDetails = null;
	if (moodleResponse != null && moodleResponse.getCpds() != null) {
	    reDetails = moodleResponse.getRegulatoryExams();
	}
	if (reDetails == null) {
	    reDetails = new ArrayList<RegulatoryExam>();
	}
	SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<RegulatoryExam>(reDetails)), getREColumns(), getEditState(),
		null);
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
    private List<IGridColumn> getREColumns() {
	Vector<IGridColumn> cols = new Vector<IGridColumn>();
	// qualificationCode col
	cols.add(new SRSDataGridColumn<RegulatoryExam>("examCode", new Model<String>("Code"), "examCode", "examCode", getEditState()).setInitialSize(50));
	// QualificationDescr col
	cols.add(
		new SRSDataGridColumn<RegulatoryExam>("examDescription", new Model<String>("Description"), "examDescription", "examDescription", getEditState())
			.setInitialSize(500)
			.setWrapText(true));
	// qualificationDate achieved
	cols.add(
		new SRSDataGridColumn<RegulatoryExam>("examDate", new Model<String>("Date Achieved"), "examDate", "examDate", getEditState()).setInitialSize(90)
			.setWrapText(true));
//		qualificationCompleted col
//		cols.add(new SRSDataGridColumn<RegulatoryExam>("examPassed",
//				new Model<String>("Passed"), "examPassed", "examPassed", getEditState()).setInitialSize(140).setWrapText(true));
	return cols;
    }

    /**
     * Get the gui controller for the Panel
     * 
     * @return
     */
    private IFitAndProperGuiController getFitAndProperGuiController() {
	if (guiController == null) {
	    try {
		guiController = ServiceLocator.lookupService(IFitAndProperGuiController.class);
	    } catch (NamingException e) {
		throw new CommunicationException(e);
	    }
	}
	return guiController;
    }
}
