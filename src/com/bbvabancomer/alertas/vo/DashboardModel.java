package com.bbvabancomer.alertas.vo;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DashboardModel {

	private TextView totalRowsText;
	private TextView generalStatus;
	private TextView availability;
	private TextView totalMessagesSent;
	private TextView totalMessagesReceived;
	private TextView totalMessagesLoss;
	private TextView sendingTimeAverage;
	private TextView receptionTimeAvegage;
	
	private ProgressBar availabilityBar;
	
	public TextView getTotalRowsText() {
		return totalRowsText;
	}
	public void setTotalRowsText(View totalRowsText) {
		this.totalRowsText = (TextView)totalRowsText;
	}
	public TextView getGeneralStatus() {
		return generalStatus;
	}
	public void setGeneralStatus(View generalStatus) {
		this.generalStatus = (TextView)generalStatus;
	}
	public TextView getAvailability() {
		return availability;
	}
	public void setAvailability(View availability) {
		this.availability = (TextView)availability;
	}
	public TextView getTotalMessagesSent() {
		return totalMessagesSent;
	}
	public void setTotalMessagesSent(View totalMessagesSent) {
		this.totalMessagesSent = (TextView)totalMessagesSent;
	}
	public TextView getTotalMessagesReceived() {
		return totalMessagesReceived;
	}
	public void setTotalMessagesReceived(View totalMessagesReceived) {
		this.totalMessagesReceived = (TextView)totalMessagesReceived;
	}
	public TextView getTotalMessagesLoss() {
		return totalMessagesLoss;
	}
	public void setTotalMessagesLoss(View totalMessagesLoss) {
		this.totalMessagesLoss = (TextView)totalMessagesLoss;
	}
	public TextView getSendingTimeAverage() {
		return sendingTimeAverage;
	}
	public void setSendingTimeAverage(View sendingTimeAverage) {
		this.sendingTimeAverage = (TextView)sendingTimeAverage;
	}
	public TextView getReceptionTimeAvegage() {
		return receptionTimeAvegage;
	}
	public void setReceptionTimeAvegage(View receptionTimeAvegage) {
		this.receptionTimeAvegage = (TextView)receptionTimeAvegage;
	}
	public ProgressBar getAvailabilityBar() {
		return availabilityBar;
	}
	public void setAvailabilityBar(View availabilityBar) {
		this.availabilityBar = (ProgressBar)availabilityBar;
	}
	
}
