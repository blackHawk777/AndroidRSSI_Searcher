package com.daniel.senderserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.daniel.model.RSSIModel;

public class SenderServerTask extends AsyncTask<RSSIModel , Void, Void> {
	int counter=0;
	private final static String URI = "http://webmapserveropenshift-municipalpayment.rhcloud.com/WebMaps/rssidata";
	HttpClient httpClient = new DefaultHttpClient();
	HttpPost post = new HttpPost(URI);
	
	
	@Override
	protected Void doInBackground(RSSIModel... model) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(model[0].getRssiModels().size());
		for (int i = 0; i <model[0].getRssiModels().size() ; i++) {
			nameValuePairs.add(new BasicNameValuePair("sql" + Integer.toString(counter++) + "", model[0].getRssiModels().get(i)));
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpClient.execute(post);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
	

}
