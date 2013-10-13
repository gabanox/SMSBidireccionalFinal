package com.bbvabancomer.alertas.helper;

public enum OverallServiceStates {

	NO_DETERMINABLE( 0 ),
	MALO           ( 1 ),
	REGULAR        ( 2 ),
	ACEPTABLE      ( 3 ),
	BUENO          ( 4 ),
	EXCELENTE      ( 5 );
	
	private Integer status;
	
	private OverallServiceStates(Integer status){
		this.status = status;
	}
}
