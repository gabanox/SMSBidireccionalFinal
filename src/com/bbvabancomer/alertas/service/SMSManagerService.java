package com.bbvabancomer.alertas.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.bbvabancomer.alertas.activity.WelcomeActivity;
import com.bbvabancomer.alertas.app.SMSApplication;
import com.bbvabancomer.alertas.dao.DatabaseAdapter;
import com.bbvabancomer.alertas.vo.Evento;

public class SMSManagerService extends Service {

	private static final String tag = "SMSManagerService";
	public static final String SERVICE_MESSAGE = "SMSManagerService";
	public static final String SERVICE_STATUS = "Estatus del servicio";
	public static final String SERVICE_INPUT = "action";
	private SMSApplication app;
	private DatabaseAdapter databaseAdapter;
	private SmsManager smsManager;
	private Timer timer;
	private Map<String, Object> settings = new HashMap<String, Object>();
	
	Intent activityNotificactionBroadcast = new Intent(
			WelcomeActivity.RECEIVE_FROM_SERVICE);
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate() {
		timer = new Timer();
		app = (SMSApplication)getApplication();
		settings = (HashMap<String, Object>)app.getSettings().getAll();
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		
		databaseAdapter = app.getDatabaseAdapter().open();
		
		smsManager = SmsManager.getDefault();
		dispatchUIMessage("Arrancando servicio");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
				Date now = new Date();
				String df = new SimpleDateFormat("yyy/mm/dd hh:mm:ss").format(now);
				Log.i(df, "Enviando mensaje SMS a las " + df);
				
				postSMS( (String)settings.get("destinationAddress") );
				
				if(createEventoFromService(now)){
					dispatchUIMessage("Mensaje SMS enviado a las " + df);
				}else {
					dispatchUIMessage("El mensaje no pudo ser enviado");
				}
			}
		}, 0, (((Integer)(settings.get("sendingInterval") ) ).longValue()*1000)*60 );
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void dispatchUIMessage(String msg){
		activityNotificactionBroadcast.putExtra(SMSManagerService.SERVICE_STATUS, msg);
		sendBroadcast(activityNotificactionBroadcast);
	}
	
	private void postSMS(String destinationAddress){
		smsManager.sendTextMessage( destinationAddress, null, "MENSAJE_PRUEBA", null, null );
	}
	
	private boolean createEventoFromService(Date d){
		Evento e = new Evento("service_message", "mensaje creado por " + tag , d );
		return databaseAdapter.createEvent(e); 
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		timer.cancel();
		databaseAdapter.close();
		stopSelf();
		
		dispatchUIMessage("Servicio detenido");
	}
	
}
