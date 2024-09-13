package za.co.liberty.business.guicontrollers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;

@Stateless
public class SegmentNameGUIController implements ISegmentNameGUIController {
	
	static Logger logger = Logger.getLogger(SegmentNameGUIController.class);
//	@EJB
//	protected ISegmentNameManagement segmentNameManager;
//	
//	@EJB
//	protected ISegmentManagement segmentManager;

	
	
	public List<SegmentNameDTO> findAllSegmentNamesList() {
		List<SegmentNameDTO> nameList = new ArrayList<SegmentNameDTO>();
		SegmentNameDTO name = new SegmentNameDTO();
		name.setSegmentName("Agency");
		name.setId(1);
		nameList.add(name);
		
		name = new SegmentNameDTO();
		name.setSegmentName("Broker");
		name.setId(2);
		nameList.add(name);
		
		name = new SegmentNameDTO();
		name.setSegmentName("Franchise");
		name.setId(3);
		nameList.add(name);
		
		return nameList;
	}
	
	public List<SegmentDTO> findAllSegmentsForSegmentNameList(int id) {
		throw new IllegalStateException();
//		return segmentManager.findAllSegmentsPerSegmentNameList(id);
	}
	
	public SegmentNameDTO findSegmentNamePerID(int id) throws DataNotFoundException{
		for (SegmentNameDTO d : findAllSegmentNamesList()) {
			if (d.getId() == id) {
				return d;
			}
		}
		
		SegmentNameDTO name = new SegmentNameDTO();
		name.setSegmentName("dummy");
		name.setId(id);
		return name;
//		throw new DataNotFoundException();
	}

	public SegmentNameDTO addSegmentName(SegmentNameDTO addSegmentName){
		logger.info("Add segment name");
		addSegmentName.setId(5 + new Random(10).nextInt());
		return addSegmentName;
	}
	
	public SegmentNameDTO updateSegmentName(SegmentNameDTO updtSegmentName){
		logger.info("Update segment name");
		return updtSegmentName;
//		return segmentNameManager.updateSegmentName(updtSegmentName); 
	}
	
	public SegmentDTO findSegmentPerID(int id) throws DataNotFoundException{
		throw new IllegalStateException();
	}

	public SegmentDTO addSegment(SegmentDTO addSegmentDTO) throws ValidationException{
		throw new IllegalStateException();
	}
	
	public SegmentDTO updateSegment(SegmentDTO updateSegmentDTO) throws ValidationException {
		throw new IllegalStateException();
	}
}
