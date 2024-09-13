package za.co.liberty.web.pages.transactions.model;

public enum GLCompanyCodeEnum {

	LLAL("LLAL"),
	CALL("CALL"),
	SAAM("SAAM"),
	RALC("RALC"),
	IEBL("IEBL"),
	CHAL("CHAL");
	
	GLCompanyCodeEnum(String code){
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
