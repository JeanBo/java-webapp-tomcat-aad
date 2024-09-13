package za.co.liberty.web.pages.transactions.model;

public enum UpfrontCommissionPercentageEnum {

	ZERO(0, 0),
	TEN(1, 10),
	TWENTY(2, 20),
	TWENTYFIVE(3, 25),
	THIRTY(4, 30),
	FOURTY(5, 40),
	FIFTY(6, 50);
	
	
	private int id;
	private int value;
	
	UpfrontCommissionPercentageEnum(int id, int value){
		this.id=id;
		this.value=value;
	}

	public int getId() {
		return id;
	}


	public int getValue() {
		return value;
	}
	
	public static UpfrontCommissionPercentageEnum getCommPercentageByValue(int value){
		for (UpfrontCommissionPercentageEnum commPercentage : UpfrontCommissionPercentageEnum.values()) {
			if(value == commPercentage.getValue())
				return commPercentage;
		}
		
		return null;
		
	}

	
	
	
}
