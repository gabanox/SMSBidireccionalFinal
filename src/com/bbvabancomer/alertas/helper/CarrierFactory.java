package com.bbvabancomer.alertas.helper;

import com.bbvabancomer.alertas.vo.Carrier;

public class CarrierFactory {

	public static Carrier createCarrier(String name){
		
		if(name.equals("TELCEL") ){
			return new Carrier(name, "123456", "123456", 10000);
			
		}else if(name.equals("NEXTEL") ){
			return new Carrier(name, "456789", "456789", 15000);
			
		}else if(name.equals("MOVISTAR") ){
			return new Carrier(name, "96325", "96325", 20000);
			
		}else if(name.equals("IUSACELL") ){
			return new Carrier(name, "74125", "74125", 30000);
			
		}else if(name.equals("UNEFON") ){
			return new Carrier(name, "1800", "1800", 35000);
			
		}
		return null;
	}
}
