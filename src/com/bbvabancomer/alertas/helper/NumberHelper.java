package com.bbvabancomer.alertas.helper;

import java.text.DecimalFormat;

public class NumberHelper {

	public static String formatNumber(int integerDigits, int decimalDigits, double value){
		
		String pattern = "";

			for(int i = 0; i < integerDigits; i++){
				pattern += "#";
			}
			
			pattern += ".";
			
			for(int j = 0; j < decimalDigits; j++){
				pattern += "#";
			}
			
		return  new DecimalFormat(pattern).format(value);
	}
}
