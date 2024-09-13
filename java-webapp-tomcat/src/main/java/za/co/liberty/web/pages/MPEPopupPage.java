
package za.co.liberty.web.pages;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import za.co.liberty.dto.contracting.ContractSearchResultDTO;
import za.co.liberty.dto.contracting.MPEDTO;
import za.co.liberty.web.constants.SRSAppWebConstants;
import za.co.liberty.web.helpers.MPEDataProvider;

public class MPEPopupPage extends WebPage{

	private DataTable mpeDatas;

	public MPEPopupPage(ContractSearchResultDTO contractSearchResultDTO)
	{
		Form form = new MPEPopupPageForm("mpePopUpForm",contractSearchResultDTO); 
		add(form);
	}

	class MPEPopupPageForm extends Form{


		public MPEPopupPageForm(String id,ContractSearchResultDTO contractSearchResultDTO) {
			super(id);

			ArrayList<MPEDTO> mpeDTOList = contractSearchResultDTO.getIndividualMPEDataList();

			IColumn[] iColSearchRes = new IColumn[]{
					new PropertyColumn(new Model(SRSAppWebConstants.MPE_DUEDATE),"indivMpeDueDate","indivMpeDueDate"),
					new PropertyColumn(new Model(SRSAppWebConstants.MPE_AMOUNT),"indivMpeAmount","indivMpeAmount")};
			MPEDataProvider dataProvider = new MPEDataProvider(mpeDTOList);

			mpeDatas = new AjaxFallbackDefaultDataTable("mpeDataTable",Arrays.asList(iColSearchRes),dataProvider,50);

			add(mpeDatas);
		}
	}



}
