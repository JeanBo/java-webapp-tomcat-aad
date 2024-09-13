package za.co.liberty.web.pages.hierarchy;

public enum MiRegionChoicesType {
	
	OUTLYING(1,"Outlying"), METRO(2,"Metro");
	
	private Integer id;
	private String name;

	MiRegionChoicesType (Integer id, String name){
		this.id = id;
		this.name = name;
	}

}
