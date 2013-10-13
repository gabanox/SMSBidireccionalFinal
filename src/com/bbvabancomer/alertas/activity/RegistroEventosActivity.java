package com.bbvabancomer.alertas.activity;

import java.util.Date;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.bbvabancomer.alertas.R;
import com.bbvabancomer.alertas.app.SMSApplication;
import com.bbvabancomer.alertas.dao.DatabaseAdapter;
import com.bbvabancomer.alertas.vo.Evento;

public class RegistroEventosActivity extends GenericActivity {

	private static final String tag = "RegistroEventosActivity";
	private static final int ADD_EVENTO = 0;
	private static final int REMOVE_ALL = 1;
	
	private SMSApplication app;
	private DatabaseAdapter adapter;
	Cursor cursor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eventos_list);
		
		app = (SMSApplication)getApplication();
		adapter = app.getDatabaseAdapter();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeItem(R.id.eventos);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Deprecated
	private void createEvent(){
		
		Evento e = new Evento(1L, "primer evento", "descripcion", new Date());
		if(adapter.createEvent(e)){
			Log.i(tag, "Evento created");
		}else {
			Log.i(tag, "Evento !created");
		}
		fillData();
	}

	private void fillData(){
		
		cursor = adapter.retrieveAllEventos();
		startManagingCursor(cursor);
		
		String[] columns = new String[]{
				DatabaseAdapter.EVENT_NAME_TAG,
				DatabaseAdapter.EVENT_DATE_TAG,
				DatabaseAdapter.EVENT_DESCRIPTION_TAG};
		
		int to[] = new int[] {R.id.name, R.id.creation, R.id.description};
		
		ListAdapter eventos = new SimpleCursorAdapter(
				this, R.layout.eventos_row, cursor, columns, to);
		//setListAdapter(eventos);
		
	}
	private void deleteAllEventos(){
		adapter.deleteFrom(DatabaseAdapter.EVENTOS_TABLE);
		fillData();
	}
	
}
