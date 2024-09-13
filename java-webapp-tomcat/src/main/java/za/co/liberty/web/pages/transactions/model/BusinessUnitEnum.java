package za.co.liberty.web.pages.transactions.model;

public enum BusinessUnitEnum {

	LLA("LLA"),
	CAL("CAL"),
	FED("FED"),
	ANN("ANN"),
	FRE("FRE"),
	RRE("RRE"),
	SAM("SAM"),
	SHR("SHR"),
	REN("REN"),
	CHA("CHA");
	
	BusinessUnitEnum(String code){
		this.setCode(code);
	}
	
	private String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}
