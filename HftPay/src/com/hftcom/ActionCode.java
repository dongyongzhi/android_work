package com.hftcom;


public enum ActionCode {
	
	SELF(1),
	
	HTML5(2),
	
	OTHER(3);
	
	private final int code;

	ActionCode(int code) {
		this.code = code;
	}
	
	public static ActionCode convert(int code) {
		for(ActionCode e : ActionCode.values()) {
			if(e.getCode() == code) {
				return e;
			}
		}
		return SELF;
	}

	public int getCode() {
		return code;
	}
	
}
