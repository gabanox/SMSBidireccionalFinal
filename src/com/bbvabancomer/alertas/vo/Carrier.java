package com.bbvabancomer.alertas.vo;

public class Carrier {

	private String carrierName;
	private String destinationAddress;
	private String responseAddress;
	private int sendingInterval;
	
	public Carrier(String carrierName, String destinationAddress,
			String responseAddress, int sendingInterval) {
		super();
		this.carrierName = carrierName;
		this.destinationAddress = destinationAddress;
		this.responseAddress = responseAddress;
		this.sendingInterval = sendingInterval;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public String getResponseAddress() {
		return responseAddress;
	}

	public void setResponseAddress(String responseAddress) {
		this.responseAddress = responseAddress;
	}

	public int getSendingInterval() {
		return sendingInterval / 1000;
	}

	public void setSendingInterval(int sendingInterval) {
		this.sendingInterval = sendingInterval;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o)
		{
			return true;
		}
		
		if(o instanceof Carrier){
			
			Carrier carrier = (Carrier)o;
			
			return 	carrier.getCarrierName().equals(this.getCarrierName() ) &&
					carrier.getDestinationAddress().equals(this.getDestinationAddress() ) &&
					carrier.getResponseAddress().equals(this.getResponseAddress() ) &&
					carrier.getSendingInterval() == this.getSendingInterval();
		}else {
			return false;
		}
	}
}
