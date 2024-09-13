package za.co.liberty.web.data.enums;

public enum TaxIndicatorType {
	 I_FINAL("I"), T_INTERIM("T"); 
	 
	 private String code;
	 
	 private TaxIndicatorType(String c) {   
		 code = c; } 
	 
	 
	 public String getCode() {   
		 return code; 
	 }
}
