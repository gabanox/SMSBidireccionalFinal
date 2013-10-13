package com.bbvabancomer.alertas.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.bbvabancomer.alertas.dao.DatabaseAdapter;

public class SMSApplication extends Application {

	private static final String SETTINGS_FILE_NAME = "PrefFile";
	private DatabaseAdapter databaseAdapter;
	private SharedPreferences settings;
	private boolean defaultMode;
	private boolean configured;
	
	@Override
	public void onCreate() {
		super.onCreate();
		databaseAdapter = new DatabaseAdapter(getApplicationContext());
		configured = checkConfig() ? true : false;
	}
	
	@Override
	public void onTerminate() {
		databaseAdapter.close();
		super.onTerminate();

	}
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		
		SQLiteDatabase.releaseMemory();
		Toast.makeText(getBaseContext(), 
				"Niveles bajos de memoria, intentando liberar recursos", Toast.LENGTH_LONG);
	}
	
	 protected boolean checkConfig(){
		settings = getSharedPreferences(SETTINGS_FILE_NAME, MODE_WORLD_READABLE);
		return settings != null && settings.getAll().size() > 0 ? true : false;
	}
	
	public DatabaseAdapter getDatabaseAdapter() {
		return databaseAdapter;
	}

	public void setDatabaseAdapter(DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;
	}

	public SharedPreferences getSettings() {
		return settings;
	}

	public void setSettings(SharedPreferences settings) {
		this.settings = settings;
	}

	public boolean isConfigured() {
		return checkConfig();
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	public boolean getDefaultModeStatus() {
		return defaultMode;
	}

	public void setDefaultMode(boolean defaultMode) {
		this.defaultMode = defaultMode;
	}

}
