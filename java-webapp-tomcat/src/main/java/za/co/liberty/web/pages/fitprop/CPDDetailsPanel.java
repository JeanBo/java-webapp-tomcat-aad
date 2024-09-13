package za.co.liberty.web.pages.fitprop;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.naming.NamingException;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;

import za.co.liberty.business.guicontrollers.fitprop.IFitAndProperGuiController;
import za.co.liberty.dto.common.ValuesDTO;
import za.co.liberty.dto.rating.CPDCycle;
import za.co.liberty.exceptions.fatal.CommunicationException;
import za.co.liberty.helpers.lookup.ServiceLocator;
import za.co.liberty.srs.integration.api.moodle.qualification.Category;
import za.co.liberty.srs.integration.api.moodle.qualification.Cpd;
import za.co.liberty.srs.integration.api.moodle.qualification.GetQualifications;
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
public class CPDDetailsPanel extends BasePanel implements ISecurityPanel {
	private static final long serialVersionUID = 1L;

	private Form panelForm;

	private SRSDataGrid cpdDetailsGrid;

	private boolean initialised;

	private GetQualifications moodleResponse;

	private transient IFitAndProperGuiController guiController;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private Collection<CPDCycle> cpdCycles;

	private long partyoid;

	/**
	 * Default constructor using the moodle web service response
	 *
	 * @param id
	 * @param editState
	 * @param parentPage
	 */
	public CPDDetailsPanel(String id, long partyoid, GetQualifications moodleResponse, EditStateType editState, Page parentPage) {
		super(id, editState, parentPage);
		this.moodleResponse = moodleResponse;
		this.partyoid = partyoid;
	}

	public Class getPanelClass() {
		return CPDDetailsPanel.class;
	}

	/**
	 * Load the components on the page on first render, so that the components are only generated when the page is displayed
	 */
	@Override
	protected void onBeforeRender() {
		if (!initialised) {
			initialised = true;
			// get cycle dates
			cpdCycles = getFitAndProperGuiController().getCPDCycleDates(partyoid, moodleResponse);
			if (cpdCycles == null) {
				cpdCycles = Collections.EMPTY_LIST;
			}
			add(panelForm = createForm("cpdForm"));
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
		form.add(cpdDetailsGrid = createCPDDetailsGrid("cpdDetails"));
		form.add(getCPDHoursTotalField("cpdHours"));
		form.add(new WebComponent("br1").setVisible(moodleResponse != null && moodleResponse.getCpds() != null && moodleResponse.getCpds()
				.size() > 0));
		form.add(new WebComponent("br2").setVisible(moodleResponse != null && moodleResponse.getCpds() != null && moodleResponse.getCpds()
				.size() > 0));
		return form;
	}

	/**
	 * Get the total CPD hours per category for the last two cycles
	 *
	 * @param id
	 * @return
	 */
	private RepeatingView getCPDHoursTotalField(String id) {
		RepeatingView cpdPanel = new RepeatingView(id);
		boolean cpdCycleExists = false;
		int counter = 0;
		if (moodleResponse != null && moodleResponse.getCpds() != null && cpdCycles != null) {
			for (CPDCycle cycle : cpdCycles) {
				counter++;
				List<ValuesDTO> values = getFitAndProperGuiController().getCPDTotalsPerCategoryForaCPDCycle(moodleResponse.getCpds(), cycle.getCycleStartDate(),
						cycle.getCycleEndDate());
				for (ValuesDTO value : values) {
					Label lab = new Label("value" + value.getId() + "_" + counter,
							"Total Hours Accrued for Category " + value.getId() + " is <b>" + value.getText() + "</b> for cycle ["
									+ dateFormat.format(cycle.getCycleStartDate()) + " - " + dateFormat.format(cycle.getCycleEndDate()) + "]");
					lab.setEscapeModelStrings(false);
					cpdPanel.add(lab);
					cpdCycleExists = true;
				}
			}
		}
		if (!cpdCycleExists) {
			Label lab = new Label("value", "Advisers CPD cycle has not started yet.");
			cpdPanel.add(lab);
		}
		return cpdPanel;
	}

	/**
	 * Create a grid for the accreditation roles
	 *
	 * @return
	 */
	private SRSDataGrid createCPDDetailsGrid(String id) {
		List<Cpd> cpds = null;
		if (moodleResponse != null && moodleResponse.getCpds() != null) {
			cpds = moodleResponse.getCpds();
		}
		if (cpds == null) {
			cpds = new ArrayList<Cpd>();
		}
		SRSDataGrid grid = new SRSDataGrid(id, new DataProviderAdapter(new SortableListDataProvider<Cpd>(cpds)), getAccredColumns(), getEditState(), null);
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
	private List<IGridColumn> getAccredColumns() {
		Vector<IGridColumn> cols = new Vector<IGridColumn>();
		// cpdCode col
		cols.add(new SRSDataGridColumn<Cpd>("cpdCode", new Model<String>("CPD Code"), "cpdCode", "cpdCode", getEditState()).setInitialSize(90));
		// cpdDescription col
		cols.add(new SRSDataGridColumn<Cpd>("cpdDescription", new Model<String>("Activity Name"), "cpdDescription", "cpdDescription", getEditState())
				.setInitialSize(200)
				.setWrapText(true));

//		cpdInstitutionName col
		cols.add(new SRSDataGridColumn<Cpd>("cpdInstitutionName", new Model<String>("Institution name"), "cpdInstitutionName", "cpdInstitutionName",
				getEditState()).setInitialSize(140)
				.setWrapText(true));
//		cpdInstitutionCode col
		cols.add(new SRSDataGridColumn<Cpd>("cpdInstitutionCode", new Model<String>("Institution Code"), "cpdInstitutionCode", "cpdInstitutionCode",
				getEditState()).setInitialSize(100));

		// cpdNumberOfHours col
		cols.add(new SRSDataGridColumn<Cpd>("cpdNumberOfHours", new Model<String>("No. Hours"), "cpdNumberOfHours", "cpdNumberOfHours", getEditState())
				.setInitialSize(60)
				.setWrapText(true));
		// cpdStartDate col
		cols.add(new SRSDataGridColumn<Cpd>("cpdDate", new Model<String>("CPD Date"), "cpdDate", "cpdDate", getEditState()).setInitialSize(80)
				.setWrapText(true));
//		categories col
		cols.add(new SRSDataGridColumn<Cpd>("categories", new Model<String>("Categories"), "categories", "categories", getEditState()) {
			private static final long serialVersionUID = 1L;

			@Override
			public Panel newCellPanel(WebMarkupContainer parent, String componentId, IModel rowModel, String objectProperty, EditStateType state, Cpd data) {
				String display = getCategoriesString(data);
				return HelperPanel.getInstance(componentId, new Label("value", display));
			}

		}.setInitialSize(160)
				.setWrapText(true));
		return cols;
	}

	/**
	 * get categories as a string to display
	 *
	 * @return
	 */
	public String getCategoriesString(Cpd cpdData) {
		if (cpdData != null && cpdData.getCategories() != null && cpdData.getCategories()
				.size() > 0) {
			StringBuilder builder = new StringBuilder(cpdData.getCategories()
					.size() * 5);
			int count = 0;
			for (Category cat : cpdData.getCategories()) {
				for (SubCategory subCat : cat.getSubCategory()) {
					if (count != 0) {
						builder.append(", ");
					}
					builder.append(subCat.getName());
					count++;
				}
			}
			return builder.toString();
		}
		return "";
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
