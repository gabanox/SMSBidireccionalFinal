package com.bbvabancomer.alertas.activity;

import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bbvabancomer.alertas.R;
import com.bbvabancomer.alertas.app.SMSApplication;
import com.bbvabancomer.alertas.helper.CarrierFactory;
import com.bbvabancomer.alertas.service.SMSManagerService;
import com.bbvabancomer.alertas.vo.Carrier;

public class ConfigurationActivity extends GenericActivity implements OnSeekBarChangeListener {

	private SMSApplication app;
	private SharedPreferences preferences;
	private Carrier carrierConfiguration;
	private Resources resources ;
		
	private SeekBar intervalBar;
	private TextView intervalValue;
	private EditText destinationAddress, responseAddress;
	private Spinner carrier;
	private Button save, cancel;
	private CheckBox restore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		
		app = (SMSApplication)getApplication();
		preferences = app.getSettings();
		resources = getResources();
		
		findViews();
		addListeners();
		
		if(app.isConfigured()){
			populateConfigValues();
		}
	}
	
	private void populateConfigValues() {
		Map<String, ?> prefs = preferences.getAll();
		
		intervalBar.setProgress( ( ( (Integer)prefs.get("sendingInterval") ).intValue() ) );
		destinationAddress.setText( (String)prefs.get("destinationAddress") );
		responseAddress.setText( (String)prefs.get("responseAddress") );
		
		String[] carriers = resources.getStringArray(R.array.carriers);
		
		for(int i = 0; i < carriers.length; i++){
			if( carriers[i].equals( (String)prefs.get("carrier") ) ){
				restore.setChecked(app.getDefaultModeStatus() );
				
				carrier.setSelection(i);
			}
		}
	}

	private void findViews() {
		
		intervalBar = (SeekBar)findViewById(R.id.intervalBar);
		intervalValue = (TextView)findViewById(R.id.sendingIntevalLabel);
		save = (Button)findViewById(R.id.save);
		cancel = (Button)findViewById(R.id.cancel);
		restore = (CheckBox)findViewById(R.id.restore);
			restore.setChecked(app.getDefaultModeStatus() );
		destinationAddress = (EditText)findViewById(R.id.destinationAddressText);
		responseAddress = (EditText)findViewById(R.id.responseAddressText);
		carrier = (Spinner)findViewById(R.id.carrierNames);
	}

	private void addListeners(){
	
		//BAR
		intervalBar.setOnSeekBarChangeListener(this);
		
		//SPINNER
		carrier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				String[] carriers = resources.getStringArray(R.array.carriers);
				for(int i = 0; i < carriers.length;  i++){
					if(position == i){
						
						if(restore.isChecked() ){
							carrierConfiguration = CarrierFactory.createCarrier( carriers[i] );
							intervalBar.setProgress( carrierConfiguration.getSendingInterval() );
							destinationAddress.setText( carrierConfiguration.getDestinationAddress() );
							responseAddress.setText( carrierConfiguration.getResponseAddress() );
						}
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		//SAVE
		save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				save();
			}
		});
		
		//CANCEL
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showWelcomeActivity();
			}
		});
		
		//RESTORE
		restore.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				restore.setChecked(true);
				carrier.performClick();
			}
		});
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.removeItem(R.id.configuracion);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		intervalValue.setText(String.valueOf( progress ) );
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}

	private void showWelcomeActivity(){
		startActivity(new Intent(getApplicationContext(), WelcomeActivity.class) );
	}
	
	private void save(){
		
		Carrier newConfig = new Carrier(	carrier.getSelectedItem().toString(), 
											destinationAddress.getText().toString(), 
											responseAddress.getText().toString(), 
											(intervalBar.getProgress() *1000 ) );
		String msg = "";
		SharedPreferences.Editor editor = preferences.edit();
			editor.putInt("sendingInterval", newConfig.getSendingInterval() );
			editor.putString("destinationAddress", newConfig.getDestinationAddress() );
			editor.putString("responseAddress", newConfig.getResponseAddress() );
			editor.putString("carrier", newConfig.getCarrierName() );
			
			if( editor.commit() ){
				msg = "Configuracion guardada exitosamente";
				
				restartService();
				app.setConfigured(true);
			}else {
				msg = "Error al guardar la configuracion";
				app.setConfigured(false);
			}
			
			if( newConfig != null && newConfig.equals(carrierConfiguration) ){
				restore.setChecked(true);
			}else {
				restore.setChecked(false);
			}
			
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			showWelcomeActivity();
	}
	
	private void restartService(){
		
		stopService( new Intent(getBaseContext(), SMSManagerService.class) ); 
		startService( new Intent(getBaseContext(), SMSManagerService.class) );
	}

}
