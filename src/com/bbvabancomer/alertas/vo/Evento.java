package com.bbvabancomer.alertas.vo;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.bbvabancomer.alertas.dao.DatabaseAdapter;

public class Evento implements Parcelable{

	private long _id;
	private String name;
	private String description;
	private Date creation;
	
	private static final int ID_INDEX = 0;
	private static final int NAME_INDEX = 1;
	private static final int DESCRIPTION_INDEX = 2;
	private static final int CREATION_INDEX = 3;
	
	Cursor cursor;
	ContentValues values = new ContentValues();
	
	public static final Parcelable.Creator<Evento> CREATOR = new Parcelable.Creator<Evento>() {
		
		public Evento createFromParcel(Parcel source) {
			return new Evento(source);
		}

		@Override
		public Evento[] newArray(int size) {
			return new Evento[size];
		};
	};
	
	public Evento(){}
	
	public Evento(Cursor cursor){
		this.cursor = cursor; 
	}
	
	public Evento(String name, String description, Date creation){
		this.name = name;
		this.description = description;
		this.creation = creation;
		setContentValues();
	}
	
	public Evento(long _id, String name, String description, Date creation){
		this._id = _id;
		this.name = name;
		this.description = description;
		this.creation = creation;
		setContentValues();
	}
	
	public Evento(Parcel parcel){
		this._id = parcel.readLong();
		this.name = parcel.readString();
		this.description = parcel.readString();
		this.creation = new Date(parcel.readLong());
	}
	
	public String getNameFromCursor(){
		return cursor.getString(NAME_INDEX);
	}
	
	public String getDescriptionFromCursor(){
		return cursor.getString(DESCRIPTION_INDEX);
	}
	
	public Date getCreationFromCursor(){
		return new Date(cursor.getLong(CREATION_INDEX));
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeLong(this._id);
		dest.writeLong(this.creation.getTime());
		dest.writeString(this.description);
		dest.writeString(this.name);
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public ContentValues getValues() {
		return values;
	}

	public void setValues(ContentValues values) {
		this.values = values;
	}
	
	private void setContentValues(){
		
		if(this._id != 0.0 ){
			values.put(DatabaseAdapter.EVENT_ID, this._id );
		}
		values.put(DatabaseAdapter.EVENT_NAME_TAG, this.name );
		values.put(DatabaseAdapter.EVENT_DESCRIPTION_TAG, this.description);
		values.put(DatabaseAdapter.EVENT_DATE_TAG, this.creation.getTime() );
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Date getCreation() {
		return creation;
	}

	@Override
	public String toString() {
		return "Evento [_id=" + _id + ", name=" + name + ", description="
				+ description + ", creation=" + creation + "]";
	}
}
