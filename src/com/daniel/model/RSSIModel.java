package com.daniel.model;

import java.io.Serializable;
import java.util.ArrayList;

public class RSSIModel implements Serializable {

	private ArrayList<String> rssiModels;
	
	public RSSIModel()
	{
		
	}

	public ArrayList<String> getRssiModels() {
		return rssiModels;
	}

	public void setRssiModels(ArrayList<String> rssiModels) {
		this.rssiModels = rssiModels;
	}

	
}
