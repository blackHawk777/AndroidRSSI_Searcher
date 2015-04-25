package com.daniel.wifiworker;

import android.net.wifi.WifiManager;

public class ScannerWiFi implements Runnable {
	private WifiManager wifi;
    private boolean threadWork=true;
    
    
    public ScannerWiFi(WifiManager wifi) {
		this.setWifi(wifi);
	}
    
	@Override
	public void run() {
		while(threadWork)
			getWifi().startScan();
		
	}


	public WifiManager getWifi() {
		return wifi;
	}


	public void setWifi(WifiManager wifi) {
		this.wifi = wifi;
	}
}
