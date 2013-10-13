package com.bbvabancomer.alertas.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bbvabancomer.alertas.helper.DatabaseHelper;
import com.bbvabancomer.alertas.vo.Evento;
import com.bbvabancomer.alertas.vo.Recepcion;

public class DatabaseAdapter {

	private static final String tag = "DatabaseAdapter";
	public static final String EVENTOS_TABLE = "eventos";
	public static final String RECEPCION_TABLE = "recepcion";
	public static final String EVENT_ID = "_id";
	public static final String EVENT_NAME_TAG = "name";
	public static final String EVENT_DESCRIPTION_TAG = "description";
	public static final String EVENT_DATE_TAG = "creation";
	
	public static final int EVENTOS_ID_INDEX = 0;
	public static final int EVENTOS_NAME_INDEX = 1;
	public static final int EVENTOS_DESCRIPTION_INDEX = 2;
	public static final int EVENTOS_CREATION_INDEX = 3;
	
	public static final int RECEPCION_ID_INDEX = 0;
	public static final int RECEPCION_RECEPTION_INDEX = 1;
	
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;
	private Context context;
	
	public DatabaseAdapter(Context context){
		this.context = context;
	}
	
	public DatabaseAdapter open(){
		
		Log.d(tag, "Opening database");
		try{
			databaseHelper = new DatabaseHelper(context);
			database = databaseHelper.getWritableDatabase();
			
		}catch(Exception e){
			Log.e("DatabaseAdapter", e.getMessage());
		}
		return this;
	}
	
	public void close(){
		databaseHelper.close();
	}
	
	public boolean createEvent(Evento evento){
		Log.i(tag, "Creando evento " + evento.toString() );
		return database.insert(EVENTOS_TABLE, null, evento.getValues() ) >= 0;
	}
	
	
	public boolean updateEvent(long rowId, Evento evento){
		return database.update(
				EVENTOS_TABLE, evento.getValues(), DatabaseAdapter.EVENT_ID + "=" + rowId, null ) > 0;
	}
	
	public Cursor retrieveEvento(long id){
		
		Cursor cursor = database.query(
				EVENTOS_TABLE, null, EVENT_ID + " = " + id, null, null, null, null);
			if(cursor != null){
				cursor.moveToFirst();
			}
		return cursor;
	}
	
	public Cursor retrieveAllEventos(){
		
		Cursor c = null;
		String sql = "select _id, " +
							"name, " +
							"description, " +
							"creation " +
							"from " + EVENTOS_TABLE + 
							" order by creation asc";
		c = database.rawQuery(sql, null);
		
		if(c!= null){
			c.moveToFirst();
		}
		return c;
	}
	
	public Cursor retrieveAllRecepciones(){
		
		Cursor c = null;
		String sql = "select _id, " + 
							"reception " + 
							"from " + RECEPCION_TABLE + 
							" order by reception asc";
		c = database.rawQuery(sql, null);
		
		if(c != null){
			c.moveToFirst();
		}
		return c;
	}
	
	public boolean deleteEventoById(long id){
		return database.delete(EVENTOS_TABLE, EVENT_ID + "=" + id, null) >= 0;
	}
	
	public void deleteFrom(String tableName){
		
		String deleteStatement = "DELETE from " + tableName;
		database.execSQL(deleteStatement);
	}
	
	public long count(String tableName){
		Cursor total = database.rawQuery("SELECT * from " + tableName, null);
		return total.getCount();
	}
	
	public boolean createRecepcion(Recepcion r){
		
		ContentValues value = new ContentValues();
			value.put("reception", r.getCreation().getTime() );
			
		Log.i(tag, "Creando recepcion " + r.toString() );
		return database.insert(RECEPCION_TABLE, null, value ) >= 0;
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void setDatabase(SQLiteDatabase database) {
		this.database = database;
	}
	
}
