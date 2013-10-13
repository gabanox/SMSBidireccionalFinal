package com.bbvabancomer.alertas.vo;

import java.util.Date;


public class Recepcion {

	private long _id;
	private Date creation;
	
	public Recepcion(){}
	
	public Recepcion(long _id){
		this._id = _id;
	}
	
	public Recepcion(long _id, Date creation){
		this._id = _id;
		this.creation = creation; 
	}

	public Recepcion(Date d){
		this.creation = d;
	}
	
	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	@Override
	public String toString() {
		return "Recepcion [_id=" + _id + ", creation=" + creation + "]";
	}
	
	
}
