package za.co.liberty.database.enums;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import za.co.liberty.database.DBAccessor;
import za.co.liberty.database.PreparedStatementObject;
import za.co.liberty.database.QueryObject;
import za.co.liberty.dto.databaseenum.CostCenterDBEnumDTO;
import za.co.liberty.dto.databaseenum.CountryCodeDBEnumDTO;
import za.co.liberty.dto.databaseenum.DBEnum;
import za.co.liberty.dto.databaseenum.DBEnumProperty;
import za.co.liberty.dto.databaseenum.DatabaseEnumDTO;
import za.co.liberty.dto.databaseenum.EthnicityDBEnumDTO;
import za.co.liberty.dto.databaseenum.GenderDBEnumDTO;
import za.co.liberty.dto.databaseenum.JobTitleDBEnumDTO;
import za.co.liberty.dto.databaseenum.LanguagePreferenceDBEnumDTO;
import za.co.liberty.dto.databaseenum.LegalFormDBEnumDTO;
import za.co.liberty.dto.databaseenum.MaritalStatusDBEnumDTO;
import za.co.liberty.dto.databaseenum.PostalAddressTypeDBEnumDTO;
import za.co.liberty.dto.databaseenum.PrefixTitleDBEnumDTO;

/**
 * Will have methods to get commonly used enums from the DB
 * @author DZS2610
 *
 */
public class DatabaseEnumHelper {	
	
	/** Cache the values always, restart of server will force refresh of values. Usually app is restarted when the enums are changed */
	private static HashMap<String, List<DatabaseEnumDTO>> cacheMap = new HashMap<String, List<DatabaseEnumDTO>>();
	
	private static final Logger logger = Logger.getLogger(DatabaseEnumHelper.class);
	
//	class InternalObj extends Object {
//		
//		public InternalObj() {
//			
//		}
//	}

	/**
	 * Place holder for additional properties
	 * 
	 * @author jzb0608
	 *
	 */
	private static class DBProperty extends Object {
		private String columnName;
		private String fieldName;
	}
	
	/**
	 * 
	 * Return a list of DatabaseEnumDTO objects with the key's and values filled in<br/>
	 * The data in the table must have a value being a string and value being an integer<br/>	 
	 * @param <T>
	 * @param typeOfEnum
	 * @param excludeUNKNOWNOption if true then any name 'UNKNOWN' will be excluded
	 * @param excludeRESERVEDVALUEOption if true then any name 'RESERVED VALUE' will be excluded
	 * @return
	 */
	public static <T extends DatabaseEnumDTO> List<T> getDatabaseDTO(Class<T> typeOfEnum,boolean excludeUNKNOWNOption, boolean excludeRESERVEDVALUEOption){
		return getDatabaseDTO(typeOfEnum,excludeUNKNOWNOption, excludeRESERVEDVALUEOption,false);
	}
	
	public static <T extends DatabaseEnumDTO> List<T> getDatabaseDTO(Class<T> typeOfEnum,
			boolean excludeUNKNOWNOption, boolean excludeRESERVEDVALUEOption, boolean orderNames){
		return getDatabaseDTO(typeOfEnum, excludeUNKNOWNOption, excludeRESERVEDVALUEOption, orderNames, true);
	}
	
	/**
	 * 
	 * Return a list of DatabaseEnumDTO objects with the key's and values filled in<br/>
	 * The data in the table must have a value being a string and value being an integer<br/>
	 * Will sort the list by name if orderNames is true
	 * @param <T>
	 * @param typeOfEnum
	 * @param excludeUNKNOWNOption if true then any name 'UNKNOWN' will be excluded
	 * @param excludeRESERVEDVALUEOption if true then any name 'RESERVED VALUE' will be excluded
	 * @return
	 */
	public static <T extends DatabaseEnumDTO> List<T> getDatabaseDTO(Class<T> typeOfEnum,
			boolean excludeUNKNOWNOption, boolean excludeRESERVEDVALUEOption, boolean orderNames, boolean limitRetrieveAll){
		DBEnum dbenum = typeOfEnum.getAnnotation(DBEnum.class);
		if(dbenum == null){
			throw new IllegalArgumentException("Class sent in does not contain the DBEnum annotation");
		}	
		
		
//		if (typeOfEnum.equals(za.co.liberty.dto.databaseenum.PrefixTitleDBEnumDTO.class)) {
//			List<T> list = new ArrayList<T>();
//			
//			return list;
//		} else {

//		}
		
		
//		String nameCol = dbenum.nameColumn();	
//		String where = "";
//		if(excludeRESERVEDVALUEOption){
//			where = " where " + nameCol + " != 'RESERVED VALUE' ";
//		}
//		if(excludeUNKNOWNOption){
//			if(where.contains("where")){
//				where += " and " + nameCol + " != 'UNKNOWN' ";	
//			}else{
//				where += " where " + nameCol + " != 'UNKNOWN' ";	
//			}			
//		}
//		if (limitRetrieveAll && dbenum.retrieveAllWhereClause().length()>0) {
//			if(where.contains("where")){
//				where += " and " + dbenum.retrieveAllWhereClause() + " ";	
//			}else{
//				where += " where " + dbenum.retrieveAllWhereClause() + " ";	
//			}	
//		}
//		if(orderNames){
//			where += " order by " + nameCol + " ";
//		}
		return getDatabaseDTO(typeOfEnum,"",null);
	}
	
	/**
	 * Return a list of DatabaseEnumDTO objects with the key's and values filled in</br>
	 * The data in the table must have a value being a string and value being an integer
	 * @param c
	 * @return
	 */
	public static <T extends DatabaseEnumDTO> List<T> getDatabaseDTO(Class<T> typeOfEnum){
		return getDatabaseDTO(typeOfEnum,"",null);
	}
	
	
	
	private static <T extends DatabaseEnumDTO> List<T> getDatabaseDTO(Class<T> typeOfEnum, String whereQuery, List<PreparedStatementObject> prepObjects){
		long time = System.currentTimeMillis();
		try{
			DBEnum dbenum = typeOfEnum.getAnnotation(DBEnum.class);
			if(dbenum == null){
				throw new IllegalArgumentException("Class sent in does not contain the DBEnum annotation");
			}
			
			if (true) {
				List<T> list = new ArrayList<T>();
				System.out.println("#JB Stuff "+ typeOfEnum);
				if (typeOfEnum.equals(CountryCodeDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "South Africa", "ZAR"},
								{"2", "India", "IND"},
							});
				
				} else if (typeOfEnum.equals(GenderDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "Male"},
								{"2", "Female"},
							});
				} else if (typeOfEnum.equals(EthnicityDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "Race1"},
								{"2", "Race2"},
							});

				} else if (typeOfEnum.equals(JobTitleDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "Working"},
								{"2", "Not Working"},
							});
				}
				else if (typeOfEnum.equals(MaritalStatusDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "Married"},
								{"2", "Single"},
							});
				}
				else if (typeOfEnum.equals(LanguagePreferenceDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "English"},
								{"2", "Afrikaans"},
							});
				}else if (typeOfEnum.equals(PostalAddressTypeDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "PO"},
								{"2", "Box"},
							});
				}else if (typeOfEnum.equals(PrefixTitleDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "Mr"},
								{"2", "Mrs"},
							});
				}else if (typeOfEnum.equals(LegalFormDBEnumDTO.class)) {
					System.out.println("    #JB --Stuff "+ typeOfEnum);
					list = getExamples(typeOfEnum, 
							new String[][] {
								{"1", "Pty"},
								{"2", "Org"},
							});
				}
				
				
				
				
				
				
				return list;
			}
			String dbJndiName = dbenum.databaseJNDIName();
			String schema = dbenum.databaseSchemaName();
			String tableName = dbenum.tableName();
			String nameCol = dbenum.nameColumn();
			String valueCol = dbenum.valueColumn();
			String mapKey = dbJndiName + schema + tableName + whereQuery;
			List<DatabaseEnumDTO> list = cacheMap.get(mapKey);
			if (!typeOfEnum.getName().equals(CostCenterDBEnumDTO.class.getName())) {
				if (list != null && (whereQuery == null || whereQuery.equals("") || !whereQuery.contains("?"))) {
					return new ArrayList<T>((List<T>) list);
				}
			}
			list = new ArrayList<DatabaseEnumDTO>();
			
			List<DBProperty> propList = new ArrayList<DBProperty>();
			// Lets see if there are any additional fields we need
			try {
				for (Field f : typeOfEnum.getDeclaredFields()) {
					if (f!= null) {
						DBEnumProperty prop = f.getAnnotation(DBEnumProperty.class);
						if (prop!=null && prop.dbColumnName()!=null) {
							DBProperty dbProp = new DBProperty();
							dbProp.columnName=prop.dbColumnName();
							dbProp.fieldName=f.getName();
							propList.add(dbProp);
						}
					}
				}
			} catch (Exception e) {
				// Ignore errors relating to this
			}
			
			//first we try get the datasource	
			DBAccessor acc = new DBAccessor();
			String query = "select * from " + schema + "."+tableName +" "+ whereQuery+" with ur";
			QueryObject queryObj = null;
			try {	
				if(prepObjects != null && prepObjects.size() >= 1){
					queryObj = acc.doQuery(query, getDataSource(dbJndiName),prepObjects);
				}
				else{
					queryObj = acc.doQuery(query, getDataSource(dbJndiName));
				}
				ResultSet rs = queryObj.getResultSet();
				while(rs.next()){		
					//name;
					//key;
					DatabaseEnumDTO obj = typeOfEnum.newInstance();		
					
					Field keyField = DatabaseEnumDTO.class.getDeclaredField("key");
					keyField.setAccessible(true);
					keyField.set(obj, rs.getString(valueCol));
					keyField.setAccessible(false);
					
					Field nameField = DatabaseEnumDTO.class.getDeclaredField("name");
					nameField.setAccessible(true);
					nameField.set(obj, rs.getString(nameCol));
					nameField.setAccessible(false);
					
					if (!propList.isEmpty()) {
						for (DBProperty prop : propList) {
							Field additionalField = typeOfEnum.getDeclaredField(prop.fieldName);
							additionalField.setAccessible(true);
							if (additionalField.getType()==Integer.class 
									|| additionalField.getType()==int.class) {
								additionalField.set(obj, rs.getInt(prop.columnName));
							} else if (additionalField.getType()==Long.class 
										|| additionalField.getType()==long.class) {
								additionalField.set(obj, rs.getLong(prop.columnName));
							} else if (additionalField.getType()==String.class) {								
								additionalField.set(obj, rs.getString(prop.columnName));
							} else {
								additionalField.set(obj, rs.getString(prop.columnName));
							}
							additionalField.setAccessible(false);
						}
					}
					
					list.add(obj);				
				}
				if(whereQuery == null || whereQuery.equals("")
						|| !whereQuery.contains("?")){
					cacheMap.put(mapKey, list);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Class sent in("+typeOfEnum.getName()+") could not be found using reflection or could not be instantiated using the default constructor");
			}finally{
				if(queryObj != null){
					queryObj.close();
				}
			}	
			return new ArrayList<T>((List<T>)list);
		}finally{
			if(logger.isDebugEnabled()){
				logger.debug("took " + (System.currentTimeMillis() - time) + " millis to get list for " + typeOfEnum);
			}
		}		
	}	
	
	/**
	 * Test method to assist with test cases for enums from a multi array String[][]  
	 * 
	 * One level (first array) is rows and the other columns
	 * 
	 * @param <T>
	 * @param typeOfEnum
	 * @param strings
	 * @return
	 */
	private static <T extends DatabaseEnumDTO> List<T> getExamples(Class<T> typeOfEnum, String[][] strings) {

		List<T> list = new ArrayList<T>();
		
		try {
			for (String[] row : strings) {
			
				T obj = typeOfEnum.newInstance();

				Field keyField = DatabaseEnumDTO.class.getDeclaredField("key");
				keyField.setAccessible(true);
				keyField.set(obj, row[0]);
				keyField.setAccessible(false);
				
				Field nameField = DatabaseEnumDTO.class.getDeclaredField("name");
				nameField.setAccessible(true);
				nameField.set(obj, row[1]);
				nameField.setAccessible(false);
				
				list.add(obj);
			}
		} catch (IllegalAccessException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Will return the database enum for the key and type given 
	 * @param typeOfEnum
	 * @param key
	 * @return
	 */
	public static <T extends DatabaseEnumDTO> T getDatabaseEnumUsingKey(Class<T> typeOfEnum, int key){
		List<T> list = getDatabaseDTO(typeOfEnum," where value = " + key + " ",null);
		if(list.size() > 0){
			return (T) list.get(0);
		}else{
			throw new IllegalArgumentException("Key("+key+") does not contain any value on the database");
			//TODO throw error, don't know which one to throw here as helpers has no access to exceptions project 
		}			
	}
	
	/**
	 * Will return the database enum for the key and type given 
	 * @param typeOfEnum
	 * @param key
	 * @return
	 */
	public static <T extends DatabaseEnumDTO> T getDatabaseEnumUsingKey(Class<T> typeOfEnum, String key){
		List<T> list = getDatabaseDTO(typeOfEnum," where value = '" + key + "' ",null);
		if(list.size() > 0){
			return (T) list.get(0);
		}else{
			throw new IllegalArgumentException("Key("+key+") does not contain any value on the database");
			//TODO throw error, don't know which one to throw here as helpers has no access to exceptions project 
		}			
	}
	
	/**
	 * Will return the database enum for the name and type given 
	 * @param typeOfEnum
	 * @param key
	 * @return
	 */
	public static <T extends DatabaseEnumDTO> T getDatabaseEnumUsingName(Class<T> typeOfEnum, String name){
		ArrayList<PreparedStatementObject> arr = new ArrayList<PreparedStatementObject>();
		PreparedStatementObject perpObj = new PreparedStatementObject(1,name);
		arr.add(perpObj);
		List<T> list = getDatabaseDTO(typeOfEnum," where name = ? ",arr);
		if(list.size() > 0){
			return (T) list.get(0);
		}else{
			throw new IllegalArgumentException("Name("+name+") does not contain any value on the database");
			//TODO throw error, don't know which one to throw here as helpers has no access to exceptions project 
		}			
	}
	
	/**
	 * Get a datasource using the given jndi name and store to a cached hashmap
	 * @param jndiName
	 * @return
	 * @throws QueryFileProcessingException
	 */
	private static DataSource getDataSource(String jndiName){		
		try{
			return (DataSource) new InitialContext().lookup(jndiName);				
		}catch(NamingException ex){
			throw new IllegalArgumentException("Could not access datasource with JNDI name " + jndiName,ex);
		}		
	}
}
