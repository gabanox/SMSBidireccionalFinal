package com.bbvabancomer.alertas.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "bitacora";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE_EVENTOS = "CREATE TABLE eventos(" +
			"_id      integer primary key autoincrement," +
			"name     text not null," +
			"description text not null," + 
			"creation timestamp" + 
			");";
	
	private static final String CREATE_TABLE_RECEPCION = "CREATE TABLE recepcion(" + 
			"_id	integer primary key autoincrement," + 
			"reception timestamp" +
			");";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_EVENTOS);
		db.execSQL(CREATE_TABLE_RECEPCION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		Log.i(DatabaseHelper.class.getName(), "Opening database!");
	}
}
