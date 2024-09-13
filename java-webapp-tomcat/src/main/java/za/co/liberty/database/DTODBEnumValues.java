package za.co.liberty.database;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import za.co.liberty.dto.databaseenum.DatabaseEnumDTO;

//To Mock DBEnum data we used this,We need to removed this while migrating 
public class DTODBEnumValues {
	
	
	public <T extends DatabaseEnumDTO> List<T> setValues(Class<T> typeOfEnum,Map<String,String> data) {
		List<DatabaseEnumDTO> list =new ArrayList<DatabaseEnumDTO>();
		try {	
		for(Map.Entry<String,String> entry:data.entrySet()) {
			
			DatabaseEnumDTO obj = typeOfEnum.newInstance();	
			
			Field keyField = DatabaseEnumDTO.class.getDeclaredField("key");
			keyField.setAccessible(true);
			keyField.set(obj, entry.getKey());
			keyField.setAccessible(false);
			
			Field nameField = DatabaseEnumDTO.class.getDeclaredField("name");
			nameField.setAccessible(true);
			nameField.set(obj, entry.getValue());
			nameField.setAccessible(false);
			list.add(obj);	
		}
		}
		catch(Exception e) {
			
		}
		return new ArrayList<T>((List<T>)list);
	}

}
