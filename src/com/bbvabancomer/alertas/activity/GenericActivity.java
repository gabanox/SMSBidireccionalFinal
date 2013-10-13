package com.bbvabancomer.alertas.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bbvabancomer.alertas.R;

public class GenericActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.navigation, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		Intent intent = null;
    	
    	switch ( item.getItemId() ) {
    	
    	case R.id.principal:
    		intent = new Intent(getApplicationContext(), WelcomeActivity.class);
    		break;
    	
		case R.id.configuracion:
			intent = new Intent(getApplicationContext(), ConfigurationActivity.class);
			break;

		case R.id.eventos:
			intent = new Intent(getApplicationContext(), RegistroEventosActivity.class);
			
		default:
			break;
		}
    	
    	startActivity(intent);
		return super.onMenuItemSelected(featureId, item);
	}
}
