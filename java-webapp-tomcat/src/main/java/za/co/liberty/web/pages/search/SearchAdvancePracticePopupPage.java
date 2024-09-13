package za.co.liberty.web.pages.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.inmethod.grid.DataProviderAdapter;
import com.inmethod.grid.IGridColumn;
import com.inmethod.grid.SizeUnit;
import com.inmethod.grid.column.PropertyColumn;

import za.co.liberty.dto.contracting.ResultLinkedItemDTO;
import za.co.liberty.dto.gui.context.ResultContextItemDTO;
import za.co.liberty.web.wicket.markup.html.grid.SRSDataGrid;
import za.co.liberty.web.wicket.markup.repeater.data.SortableListDataProvider;

public class SearchAdvancePracticePopupPage extends WebPage{

	private DataTable managerData;
	

	public SearchAdvancePracticePopupPage(ResultContextItemDTO resultContextItemDTO) {
		Form form = new ManagerPopupPageForm("managerPopUpForm",resultContextItemDTO); 
		add(form);
	}

	class ManagerPopupPageForm extends Form {

		public ManagerPopupPageForm(String id,ResultContextItemDTO resultContextItemDTO) {
			super(id);
			
            long partyID = resultContextItemDTO.getPartyDTO() != null ? resultContextItemDTO.getPartyDTO().getPartyOid() : 000 ;
            List<ResultLinkedItemDTO> list = new ArrayList<ResultLinkedItemDTO>();
            
            ResultLinkedItemDTO dto = resultContextItemDTO.getLinkedDTO();
         
            if(dto.getLinkedList() != null && dto.getLinkedList().size() >0){
            	for(ResultLinkedItemDTO itemDTO : dto.getLinkedList() ){
            		if(itemDTO.getPracticeNumber().equals(partyID) ){
            			list.add(itemDTO);	
            		}
            		
            	}
            }else if(dto.getLinkedList() != null && dto.getLinkedList().size() == 0){
            	
            }else if(dto != null && dto.getAgreementNr() > 0) {
            	 list.add(dto);
            }	else {
            	ResultLinkedItemDTO itemDTO = new ResultLinkedItemDTO();
            	itemDTO.setAgreementNr(0L);
            	itemDTO.setName("Not Found");
            	list.add(itemDTO);
            }
            
           
            
            List<IGridColumn> columns = new ArrayList<IGridColumn>();
            columns.add(new PropertyColumn(new Model("Practice Number"), "practiceNumber","practiceNumber").setInitialSize(100));
    		columns.add(new PropertyColumn(new Model("Name"), "name","name").setInitialSize(250));
   			columns.add(new PropertyColumn(new Model("Agreement #"), "agreementNr", "agreementNr").setInitialSize(100));

   			SRSDataGrid grid = new SRSDataGrid("managerDataTable", new DataProviderAdapter(new SortableListDataProvider<ResultLinkedItemDTO>(list)),
   					columns, null);
   			grid.setAutoResize(false);
   			grid.setRowsPerPage(5);
   			grid.setContentHeight(70, SizeUnit.PX);
   			grid.setAllowSelectMultiple(false);
   			
			add(grid);
		}
	}



}
