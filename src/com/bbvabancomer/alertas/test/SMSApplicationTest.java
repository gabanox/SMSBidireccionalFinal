package com.bbvabancomer.alertas.test;

import android.database.sqlite.SQLiteDatabase;
import android.test.ApplicationTestCase;

import com.bbvabancomer.alertas.app.SMSApplication;
import com.bbvabancomer.alertas.dao.DatabaseAdapter;

public class SMSApplicationTest extends ApplicationTestCase<SMSApplication> {

	private DatabaseAdapter databaseAdapter;
	private SQLiteDatabase database;
	
	public SMSApplicationTest() {
		super(SMSApplication.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnCreate() {
		databaseAdapter = new DatabaseAdapter(getContext() ).open();
		assertNotNull(databaseAdapter);
	}

	public void testOnTerminate() {
		databaseAdapter.close();
		assertFalse(database.isOpen() );
	}

	public void testOnLowMemory() {
		fail("Not yet implemented");
	}

	public void testGetDatabaseAdapter() {
		fail("Not yet implemented");
	}

	public void testSetDatabaseAdapter() {
		fail("Not yet implemented");
	}

}
