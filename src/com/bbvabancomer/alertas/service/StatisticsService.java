package com.bbvabancomer.alertas.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;

import com.bbvabancomer.alertas.app.SMSApplication;
import com.bbvabancomer.alertas.dao.DatabaseAdapter;
import com.bbvabancomer.alertas.helper.DateHelper;
import com.bbvabancomer.alertas.helper.NumberHelper;
import com.bbvabancomer.alertas.helper.OverallServiceStates;
import com.bbvabancomer.alertas.vo.Evento;
import com.bbvabancomer.alertas.vo.Recepcion;

public class StatisticsService extends Service {

	private static final String tag = "StatisticsService";
	private static final int MINIMUM_QUANTITY = 2;
	private static final double REPONSE_LIMIT_IN_SECONDS =5*60;
	private SMSApplication app;
	private DatabaseAdapter database;
	public StatisticsBinder statisticsBinder = new StatisticsBinder();
	
	@Override
	public void onCreate() {
		app = (SMSApplication)getApplication();
		database = app.getDatabaseAdapter().open();
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return statisticsBinder;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		database.close();
	}
	
	public class StatisticsBinder extends Binder{
		
		public StatisticsService getService(){
			return StatisticsService.this;
		}
	}
	
	public Map<String, Object> getData(){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, Object> enviosDataStats = calculateTimeBetweenEvents(Evento.class);
		Map<String, Object> recepcionDataStats = calculateTimeBetweenEvents(Recepcion.class);
		
		if(enviosDataStats.size() > MINIMUM_QUANTITY && recepcionDataStats.size() > MINIMUM_QUANTITY){
			
			double responseMean = (Double)enviosDataStats.get("mean");
			double receptionMean = (Double)recepcionDataStats.get("mean");
			
			long totalEnvios = database.count("eventos");
			long totalRecepciones = (database.count("recepcion") );
			
			double recepcionSummatory = (Double)recepcionDataStats.get("sum");
			double tiempoTotalRecepcion = recepcionSummatory == 0 ? totalRecepciones : recepcionSummatory;
			
			double enviosSummatory = (Double)enviosDataStats.get("sum");
			double tiempoTotalEnvios = enviosSummatory == 0 ? totalEnvios : enviosSummatory;
			
			double availability = getServiceAvailbility( 	totalEnvios, 
															(Double)recepcionDataStats.get("sum"), 
															totalEnvios, 
															totalRecepciones) ;
			double integrity = (double)(totalEnvios - totalRecepciones);
			
			double[] input = new double[]{ integrity, availability, tiempoTotalRecepcion };
			double[] weights = new double[]{ 1, 3, 1 };
			
			String overallStatus = getOverallStatus(input, weights).toString();
			
			resultMap.put("totalMessagesEnviados", totalEnvios );
			resultMap.put("totalMensajesRecibidos", totalRecepciones );
			resultMap.put("perdidaMensajes", (double)(totalEnvios - totalRecepciones ) );
			resultMap.put("tiempoPromedioEnvios", responseMean );
			resultMap.put("tiempoPromedioRecepcion", receptionMean );
			resultMap.put("relacionEnvioRecepcion", getEnvioRecepcionRatio(totalEnvios, totalRecepciones) );
			
			resultMap.put("tiempoTotalEnvios", tiempoTotalEnvios );
			resultMap.put("valorMaximoEnvio", enviosDataStats.get("max") );
			resultMap.put("valorMinimoEnvio", enviosDataStats.get("min") );
			resultMap.put("desviacionEnvios", enviosDataStats.get("std") );
			
			resultMap.put("tiempoTotalRecepcion", tiempoTotalRecepcion );
			resultMap.put("valorMaximoRecepcion", recepcionDataStats.get("max") );
			resultMap.put("valorMinimoRecepcion", recepcionDataStats.get("min") );
			resultMap.put("desviacionRecepciones", recepcionDataStats.get("std") );
			
			resultMap.put("overallStatus", overallStatus );
			resultMap.put("availability", availability );
		}
		
		return resultMap;
	}
	
	/*
	 * El dao entrega los datos ordenados no hay falta de hacer uso de 
	 * Comparator<T>
	 */
	private Map<String, Object> calculateTimeBetweenEvents(Class<? extends Object> clazz){
		
		Map<String, Object> responseStats = new HashMap<String, Object>();
		
		Cursor all = null;
		DescriptiveStatistics statistics = null; 
		int dateIndex = 0;
		
		if(clazz.getSimpleName().equals("Evento") ){
			all = database.retrieveAllEventos();
			dateIndex = DatabaseAdapter.EVENTOS_CREATION_INDEX;
			
		}else if(clazz.getSimpleName().equals("Recepcion") ){
			all = database.retrieveAllRecepciones();
			dateIndex = DatabaseAdapter.RECEPCION_RECEPTION_INDEX;
		}
		 
		int difference = 0, counter = 1;
		Date d1 = null, d2 = null;
		
			if(all != null && all.getCount() > MINIMUM_QUANTITY ){
				statistics = new DescriptiveStatistics( all.getCount() / 2);
			
				do {
					Date eventCreationTimestamp = new Date( all.getLong(dateIndex) );
					
					if(counter % 2 == 0){
						
						d2 = eventCreationTimestamp;
						difference = DateHelper.getDifferenceBetweenDatesInSeconds( //TODO hay un error con 60 => 0 
																					DateHelper.getMinorDate(d1, d2), 
																					DateHelper.getEldestDate(d1, d2) );
						if(difference <= REPONSE_LIMIT_IN_SECONDS){
							statistics.addValue(difference);
						}
						
					}else{
						d1 = eventCreationTimestamp; 
					}
					counter++;
					all.moveToNext();
						
				} while (all.isAfterLast() == false);
				
				responseStats.put("mean", statistics.getMean() );
				responseStats.put("max", statistics.getMax() );
				responseStats.put("min", statistics.getMin() );
				responseStats.put("std", statistics.getStandardDeviation() );
				responseStats.put("sum", statistics.getSum() );
				
			}else {
				responseStats.put("message", "No es posible hacer calculos con este conjunto de datos");
			}
			
		return responseStats;
	}	
	
	/*
	 * La disponibilidad del servicio se obtiene de la siguiente formula
	 * 
	 * Nombre							Acronimo		Calculo			
	 * tiempo medio entre errores		MTBF			Horas / Errores		
	 * tiempo medio de recuperacion		MTTR			Horas Reparacion / Errores
	 * 
	 * Disponibilidad = (MTBF / (MTBF + MTTR)) X 100
	 */
	private double getServiceAvailbility(double upTime, double downTime, long totalEventos, long totalRecepciones){
		
		double mtbf = calculateMeanTimeBetweenFailure(upTime, getErrorQuantity(totalEventos, totalRecepciones) );
		double mttr = calculateMeanTimeToRepair(downTime, getErrorQuantity(totalEventos, totalRecepciones) );
		double availability = calculateServiceAvailability(mtbf, mttr);
		
		return Double.valueOf(NumberHelper.formatNumber(2, 1, availability) ); 
	}
	
	private int getErrorQuantity(float totalSending, long totalReceptions){
		return  (int)(totalSending > 0 && totalReceptions > 0 ? (totalSending - totalReceptions ) : 0);
	}
	
	private double calculateMeanTimeBetweenFailure(double upTime, int errorQuantity){
		return errorQuantity > 0 ? (upTime / errorQuantity) : 0L;
	}
	
	private double calculateMeanTimeToRepair(double downTime, int errorQuantity){
		return errorQuantity > 0 ? (downTime / errorQuantity) : 0L;
	}
	
	private double calculateServiceAvailability(double mtbf, double mttr){
		return  mtbf > 0 && mttr > 0 ? (mtbf / (mtbf + mttr))*100 : 100F;
	}
	
	/*
	 * Media ponderada que determina el estatus general
	 */
	private OverallServiceStates getOverallStatus(double[] input, double[] weights) 
			throws IllegalArgumentException{
		
		double weightedMean = Math.floor(new Mean().evaluate(input, weights) );
		
		if(weightedMean >= 62 && weightedMean < 66){
			return OverallServiceStates.BUENO;
			
		}else if(weightedMean >= 66 && weightedMean < 70){
			return OverallServiceStates.ACEPTABLE;
			
		}else if(weightedMean >= 70 && weightedMean < 74){
			return OverallServiceStates.REGULAR;
			
		}else if(weightedMean >= 74 && weightedMean < 80){
			return OverallServiceStates.MALO;
			
		}else {
			return OverallServiceStates.NO_DETERMINABLE;
		}
		
	}
	
	private long getEnvioRecepcionRatio(long envios, long recepciones){
		return envios > 0 && recepciones > 0 ? envios / recepciones : -1;
	}

}
