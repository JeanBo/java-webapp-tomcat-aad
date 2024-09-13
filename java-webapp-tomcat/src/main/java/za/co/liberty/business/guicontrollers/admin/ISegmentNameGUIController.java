package za.co.liberty.business.guicontrollers.admin;

import java.util.List;

import javax.ejb.Local;


import za.co.liberty.dto.rating.SegmentDTO;
import za.co.liberty.dto.rating.SegmentNameDTO;
import za.co.liberty.exceptions.data.DataNotFoundException;
import za.co.liberty.exceptions.data.ValidationException;

/**
 * Segment name and segment share this component as gui controller. Contain intermediary level gui controller to business layer for 
 * all segment and segment name funtionality
 * @author JWV2310
 *
 */

@Local
public interface ISegmentNameGUIController {
	/**
	 * Find and return all segment names
	 * @return
	 */
	public List<SegmentNameDTO> findAllSegmentNamesList();
	
	/**
	 * Find all segments for a specific segment name. Return segment in List
	 * @param id
	 * @return
	 */
	public List<SegmentDTO> findAllSegmentsForSegmentNameList(int id);
	
	/**
	 * Find segment name per id
	 * @param id
	 * @return
	 */
	public SegmentNameDTO findSegmentNamePerID(int id) throws DataNotFoundException;
	
	/**
	 * Add a segment name
	 * @param addSegmentName
	 * @return
	 */
	public SegmentNameDTO addSegmentName(SegmentNameDTO addSegmentName);
	
	/**
	 * Update a segment name
	 * @param updateSegmentName
	 * @return
	 */
	public SegmentNameDTO updateSegmentName(SegmentNameDTO updateSegmentName);
	
	/**
	 * Find a segment per id
	 * @param id
	 * @return
	 */
	public SegmentDTO findSegmentPerID(int id) throws DataNotFoundException;
	
	/**
	 * Update a segment
	 * @param a
	 * @return
	 */
	public SegmentDTO updateSegment(SegmentDTO a) throws ValidationException;
	
	/**
	 * Add a new segment
	 * @param b
	 * @return
	 */
	public SegmentDTO addSegment(SegmentDTO b) throws ValidationException;
	
}
