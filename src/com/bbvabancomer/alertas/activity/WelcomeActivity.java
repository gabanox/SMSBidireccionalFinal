package com.bbvabancomer.alertas.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bbvabancomer.alertas.R;
import com.bbvabancomer.alertas.app.SMSApplication;
import com.bbvabancomer.alertas.dao.DatabaseAdapter;
import com.bbvabancomer.alertas.service.SMSManagerService;
import com.bbvabancomer.alertas.service.StatisticsService;
import com.bbvabancomer.alertas.vo.DashboardModel;
import com.bbvabancomer.alertas.vo.Recepcion;

public class WelcomeActivity extends GenericActivity {
    
	private static final String tag = "WelcomeActivity";
	public static final String RECEIVE_FROM_SERVICE ="com.bbvabancomer.alertas.activity.action.RECEIVE_FROM_SERVICE";
	
	private SMSApplication app;
	private ToggleButton serviceButton;
	private ImageButton refreshButton;
	private EditText messageCenter;
	private TextView 	overallServiceStatus; 
	private ProgressDialog progressDialog;
	
	private DashboardModel dashboardModel;
	private DatabaseAdapter databaseAdapter;
	private Cursor cursor;
	private Timer timer;
	private Map<String, Object> settings;
	private Map<String, Object> info;
	private ServiceConnection statisticsServiceConnection;
	private StatisticsService statisticsService;
	private BroadcastReceiver smsManagerServiceReceiver;
	private BroadcastReceiver smsArrivalReceiver;
	
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        app = (SMSApplication)getApplication();
        settings = (HashMap<String, Object>)app.getSettings().getAll();
        info = new HashMap<String, Object>();
        dashboardModel = new DashboardModel();
        
        if(app.isConfigured() == false ){
        	startActivity(new Intent(this, ConfigurationActivity.class) );
        }else {
        	findViews();
       		doServiceConnection();
       		bindServices();
       		registerBroadcastReceivers();
       		addUIListeners();
        }
    }
    
    private void displayDashboard(){
    	
    	timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask() {
    		
    		@Override
    		public void run() {
//				fillData();
    			//call service through fillData()
    			
    		}
    	}, 0, (((Integer)(settings.get("sendingInterval") ) ).longValue()*1000)*60 );
    }
    
    private void fillData(){
    	
    	showLoadingDialog();
    	
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				info = statisticsService.getData();
			}
		}).start();
    	
    	if(info != null && info.size() > 0){
    		updateDashboard(info);
    	}else {
    		resetControls();
    	}
    	
    	hideLoadingDialog();
    }
    
    private void findViews(){
    	
         serviceButton = (ToggleButton)findViewById(R.id.serviceControl);
         refreshButton = (ImageButton)findViewById(R.id.refresh);
         messageCenter = (EditText)findViewById(R.id.messageCenterText);
         overallServiceStatus = (TextView)findViewById(R.id.overallServiceStatusLabel);
         	overallServiceStatus.append( app.getSettings().getString("carrier", "Sin telefonica seleccionada!") );
         	
         dashboardModel.setGeneralStatus(findViewById(R.id.generalStatus) );
         dashboardModel.setAvailability(findViewById(R.id.availability) );
         dashboardModel.setAvailabilityBar(findViewById(R.id.availabilityBar ) );
         dashboardModel.setTotalMessagesSent(findViewById(R.id.totalMessagesSent) );
         dashboardModel.setTotalMessagesReceived(findViewById(R.id.totalMessagesReceived) );
         dashboardModel.setTotalMessagesLoss(findViewById(R.id.totalMessagesLoss) );
         dashboardModel.setSendingTimeAverage(findViewById(R.id.sendingTimeAverage) );
         dashboardModel.setReceptionTimeAvegage(findViewById(R.id.receptionTimeAvegage) );

    }
    
    private void doServiceConnection(){
    	
    	statisticsServiceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				displayMessage("El servicio de estadistica no se encuentra disponible");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder binder) {
				statisticsService = ((StatisticsService.StatisticsBinder)binder).getService();
			}
		};
    }
    
    private void bindServices(){
    	Intent i = new Intent(this, StatisticsService.class);
		bindService(i,statisticsServiceConnection, Context.BIND_AUTO_CREATE );
    }
    
    private void registerBroadcastReceivers(){
    	
    	 smsManagerServiceReceiver = new BroadcastReceiver() {
 			
 			@Override
 			public void onReceive(Context context, Intent intent) {
 				
 				if( intent.getAction().equals(RECEIVE_FROM_SERVICE) ){
 					
 					Bundle extras = intent.getExtras();
 					
 					if( extras != null && extras.size() > 0){
 						displayMessage(extras.getString(SMSManagerService.SERVICE_STATUS) );
 						fillData();
 					}else {
 						displayMessage("No se recibio informacion del servicio de SMS");
 					}
 				}
 			}
 		};
 		
 		smsArrivalReceiver = new BroadcastReceiver() {
 			
 			Intent filter = new Intent("android.provider.Telephony.SMS_RECEIVED");
 			@Override
 			public void onReceive(Context context, Intent intent) {
 				
 				databaseAdapter.open();
 				if( intent.filterEquals(filter) ){
 					
 					Date now = new Date();
 					String df = new SimpleDateFormat("yyy/mm/dd hh:mm:ss").format(now);
 					
 					displayMessage("Mensaje " + settings.get("carrier") + " recibido a " + df);
 					databaseAdapter.createRecepcion( new Recepcion( new Date() ) );
 					fillData();
 				}
 			}
 		};
 		
        registerReceiver(smsManagerServiceReceiver, new IntentFilter(RECEIVE_FROM_SERVICE) );
        registerReceiver(smsArrivalReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED") );
    }
    
    private void addUIListeners(){
    	
    	serviceButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
				if(isChecked){
					startService( new Intent(getBaseContext(), SMSManagerService.class) );
					displayDashboard();
				}else{
					stopService( new Intent(getBaseContext(), SMSManagerService.class) );
					timer.cancel();
				}
			}
		});
        
        refreshButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fillData();
			}
		});
    }
    
    private void displayMessage(String msg){
    	messageCenter.setText(msg);
    }
    
    private void resetControls(){
    	dashboardModel.getGeneralStatus().setText("Informacion insuficiente");
		dashboardModel.getGeneralStatus().setTextColor(Color.GREEN);
		dashboardModel.getAvailability().setText("N/A");
		dashboardModel.getTotalMessagesSent().setText("N/A");
		dashboardModel.getTotalMessagesReceived().setText("N/A");
		dashboardModel.getTotalMessagesLoss().setText("N/A");
		dashboardModel.getSendingTimeAverage().setText("N/A");
		dashboardModel.getReceptionTimeAvegage().setText("N/A");
    }
    
    private void updateDashboard(Map<String, Object> serviceData ){
    	
    	dashboardModel.getGeneralStatus().setText( (String)serviceData.get("overallStatus") );
    	dashboardModel.getAvailability().setText( String.valueOf(serviceData.get("availability") ) );
    	dashboardModel.getAvailabilityBar().setProgress( ((Double)serviceData.get("availability")).intValue() );
    	dashboardModel.getTotalMessagesSent().setText( String.valueOf(serviceData.get("totalMessagesEnviados") ) );
    	dashboardModel.getTotalMessagesReceived().setText( String.valueOf(serviceData.get("totalMensajesRecibidos") ) );
    	dashboardModel.getTotalMessagesLoss().setText( String.valueOf(serviceData.get("perdidaMensajes") ) );
    	dashboardModel.getSendingTimeAverage().setText( String.valueOf(serviceData.get("tiempoPromedioEnvios") ) );
    	dashboardModel.getReceptionTimeAvegage().setText( String.valueOf(serviceData.get("tiempoPromedioRecepcion") ) );
    }
    
    private void showLoadingDialog(){
    	progressDialog = ProgressDialog.show(WelcomeActivity.this, "", "Procesando.. espere", true);
    }
    
    private void hideLoadingDialog(){
    	progressDialog.dismiss();
    }
    
    //LIFECICLE
    
    @Override
    protected void onStart() {
    	super.onStart();
    	 databaseAdapter = app.getDatabaseAdapter().open();
    	 
    	 if(databaseAdapter.getDatabase().isOpen() ){
    		 cursor = databaseAdapter.retrieveAllEventos();
    	 }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if(databaseAdapter.getDatabase().isOpen() && !cursor.isClosed() ){
//    		fillData();
    	}else {
    		displayMessage("Ocurrio un problema con la base de datos :/");
    	}
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	
		menu.removeItem(R.id.principal);
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    	cursor.requery();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	cursor.deactivate();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	cursor.close();
    }
    
}