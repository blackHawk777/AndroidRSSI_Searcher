package com.daniel.androidsearcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.androidsearcher.R;
import com.daniel.bitmapworker.BitmapWorker;
import com.daniel.fileworker.FileWorker;
import com.daniel.model.RSSIModel;
import com.daniel.senderserver.SenderServerTask;
import com.daniel.wifiworker.ScannerWiFi;

public class MainActivity extends Activity {
	
	
	//private ImageView mapView;
    int x=0;
    int y=0;
    private BitmapWorker bitmapWorker = new BitmapWorker();
    String service = Context.WIFI_SERVICE;
    private Integer type_signal=0;
    private File file;
    private ArrayList<String> arrayListPoints = new ArrayList<String>();
    private ArrayList<String> sqlStrings = new ArrayList<String>();
    private final static String RESULT_DIR="/SearcherData";
    String resultString="";
    String[] rssi_points;
    TextView tv;
    int sqlQueryid=0;
    Boolean isStopped=true;
    Boolean isFirst = true;
    SenderServerTask task;
    Button buttonStart;
    Button buttonStop;
    TextView helpView;
    String create_query;
    private final static String WIFI_RECORDS="WiFiRecords.txt";
    private final static String POINT_FILE="points.txt";
    private RSSIModel rssiModel = new RSSIModel();
    int testValue=0;
    WifiManager wifi;
    ScannerWiFi wifiThread;
    Thread calculatingThread;
    ArrayList<List<ScanResult>> listOfResults;
    List<ScanResult> scanResults;
    ArrayList<String> wifiPoints;
    LinearLayout linearLayoutForSSID;
    private Context myContext = this;
    private int countOfFindSSID=0;
    private Spinner pointsView;
    Boolean isContinue=false;
    public FileWorker fw = new FileWorker();
    private boolean isFirstRecord=true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pointsView = (Spinner)findViewById(R.id.points_view);
		try {
			configureSpinner();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//mapView = (ImageView)findViewById(R.id.map_img_view);
        buttonStart = (Button)findViewById(R.id.button1);
        buttonStop = (Button)findViewById(R.id.button2);
        helpView = (TextView)findViewById(R.id.textHelp);
        tv = (TextView)findViewById(R.id.textView1);
        linearLayoutForSSID = (LinearLayout)findViewById(R.id.linearLayoutForSSID);
        scanResults = new ArrayList<ScanResult>();
        fw.createFile(WIFI_RECORDS, RESULT_DIR);
        wifi = (WifiManager)getSystemService(service);
        wifiThread = new ScannerWiFi(wifi);
        calculatingThread= new Thread(wifiThread);
        calculatingThread.setName("calculateThread");
        calculatingThread.start();
        registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if (!isStopped) {
                    tv.setText("Recording..");
                    listOfResults.add(wifi.getScanResults());
                    testValue++;
                    // Toast.makeText(getApplicationContext(), "Record # " + Integer.toString(testValue) + "done", Toast.LENGTH_LONG).show();
                    tv.setText("Record #" + Integer.toString(testValue) + " done");
				}
				 if (isFirst) {
	                    scanResults = wifi.getScanResults();
	                    countOfFindSSID = scanResults.size();
	                    int idNumber = 0;
	                    for (ScanResult sr : scanResults) {
	                        CheckBox checkBox = new CheckBox(myContext);
	                        checkBox.setText("MAC: " + sr.BSSID + " SSID: " + sr.SSID);
	                        checkBox.setId(++idNumber);
	                        linearLayoutForSSID.addView(checkBox);

	                    }
	                    isFirst = false;
	                }
				
			}
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        
        buttonStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  if(wifiPoints==null)
	                    wifiPoints = new ArrayList<String>();
	                if(wifiPoints.size()==0){
	                    wifiPoints = new ArrayList<String>();
	                    for (int i = 0; i < linearLayoutForSSID.getChildCount(); i++){
	                        CheckBox checkBox = (CheckBox) linearLayoutForSSID.getChildAt(i);
	                        if(checkBox.isChecked())
	                            wifiPoints.add((String) checkBox.getText());
	                    }
	                }
	                if (!(wifiPoints.size()==0)){
	                    listOfResults = new ArrayList<List<ScanResult>>();
	                    isStopped=false;
	                }
	                else
	                {
	                    Toast.makeText(getApplication(),"Укажите WiFi точки ", Toast.LENGTH_SHORT).show();
	                }
				
			}
		});
        
        buttonStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!isStopped)
                {
                    isStopped=true;
                    resultString="";
                    int rssi_name_pos=28;
                    String[] splitedItem=((String)pointsView.getSelectedItem()).split(",");
                    rssi_points = new String[wifiPoints.size()];
                    for(int i=0; i<wifiPoints.size();i++)
                    {
                    	rssi_points[i]=wifiPoints.get(i).substring(rssi_name_pos);
                    }
                   /* rssi_names=wifiPoints.get(0).substring(rssi_name_pos) +"," + wifiPoints.get(1).substring(rssi_name_pos)
                    		+ "," +wifiPoints.get(2).substring(rssi_name_pos) + "," + wifiPoints.get(3).substring(rssi_name_pos) + ","
                    		+ wifiPoints.get(4).substring(rssi_name_pos) + "\n";
                    
                    for (int j=0; j<rssi_points.length;j++)
                    {
                    	//resultString+=rssi_points[j] +",";
                    	resultString=rssi_points[j];
                    }
                    */
                    	if (isFirstRecord){
                    		create_query="CREATE TABLE IF NOT EXISTS RSSI (id INTEGER PRIMARY KEY AUTO_INCREMENT, x NUMERIC, y NUMERIC";
                    	for(int i=0;i<rssi_points.length;i++)
                    		create_query+="," + rssi_points[i] + " NUMERIC";
                    	 create_query+=");";
                    		fw.recordToFile(create_query);
                    		isFirstRecord=false;
                    	}                    	
                    	resultString+="\n" + "INSERT INTO RSSI VALUES (" + ++sqlQueryid +"," +splitedItem[0]+ "," +splitedItem[1]+ calculateCoordinats(wifiPoints, type_signal) + ");";
							fw.recordToFile(resultString);
                    //resultString+="INSERT INTO Points VALUES ("+ x + "," + y  + calculateCoordinats(wifiPoints, type_signal) + "); \n";
                    
                    testValue=0;
                   
                }
				
			}
		});
	}
	
	protected String calculateCoordinats(ArrayList<String> wifiPoints, Integer type_signal){
        //String[] wifiPoints={"ADSL_Wireless","NIRVANA","Sha_virus","VANO"};
        //String[] wifiPoints2={"VANO","andreiru","DIR-300NRU","DIR-620"};
       /* int[] min = {0,0,0,0,0};
        int[] max= {300,300,300,300,300};
        int[] sum= {0,0,0,0,0};
        int[]masmeasures={1,1,1,1,1};
        int[] average= {0,0,0,0,0};
        */
		int[] min= new int[wifiPoints.size()];
        int[] max = new int[wifiPoints.size()];
        int[] sum = new int[wifiPoints.size()];
        int[]masmeasures = new int[wifiPoints.size()];
        int[] average=new int[wifiPoints.size()];
        for (int i=0;i<wifiPoints.size();i++)
        {
        	min[i]=0;
        	max[i]=300;
        	sum[i]=0;
        	masmeasures[i]=1;
        	average[i]=0;
        }

        for (List<ScanResult> resultsList : listOfResults){
            for(ScanResult results : resultsList){
                for(int j=0; j<wifiPoints.size(); j++){

                    if(results.BSSID.contentEquals(wifiPoints.get(j).subSequence(5, 22))){


                        masmeasures[j]++;

                        int lev= Math.abs(results.level);
                        sum[j]+= lev;

                        if(lev<max[j]){
                            max[j]=lev;
                        }
                        if(lev>min[j]){
                            min[j]=lev;
                        }
                    }


                }
            }

        }


        for (int i=0; i<wifiPoints.size(); i++){
            average[i]=sum[i]/masmeasures[i];
        }

        listOfResults=null;
        String result="";

        switch (type_signal)
        {
            case 1:
                result="";
                for (int i=0; i<wifiPoints.size(); i++){
                    result+= "," + max[i];
                }

            case 2:
                result="";
                for (int i=0; i<wifiPoints.size(); i++){
                    result+= "," + average[i];
                }

            case 3:
                result="";
                for (int i=0; i<wifiPoints.size(); i++){
                    result+= "," + min[i];
                }
            default:
            	 result="";
                 for (int i=0; i<wifiPoints.size(); i++){
                     result+= "," + average[i];
                 }

        }

       /* for (int i=0; i<wifiPoints.size(); i++){
            resultAve+= "," + average[i];
        }
        */
        return result;
    }
	
	
	public void configureSpinner() throws IOException {
	    arrayListPoints=fw.readPointsFile(file, POINT_FILE);
	    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_dropdown_item,arrayListPoints);
	    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    pointsView.setAdapter(arrayAdapter);
	    pointsView.setPromptId(R.string.help_for_spinner);
}

	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		 switch (id) {
		 
         case R.id.action_max_type:
             type_signal=1;
             Toast.makeText(myContext, R.string.help_max,Toast.LENGTH_SHORT).show();
             return true;
         case R.id.action_ave_type:
             type_signal=2;
             Toast.makeText(myContext, R.string.help_ave,Toast.LENGTH_SHORT).show();
             return true;
         case R.id.action_min_type:
             type_signal=3;
             Toast.makeText(myContext, R.string.help_min,Toast.LENGTH_SHORT).show();
             return  true;
         case R.id.send_to_server:
        	 // вызов AsyncTask
        	 try {
        		  rssiModel.setRssiModels(fw.readPointsFile(file, WIFI_RECORDS));
        		  task=new SenderServerTask();
        		  task.execute(rssiModel);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	 return true;
        	 
       /*  case R.id.map_menu_item:
             // получить картинку
             try {
                 mapView.setImageBitmap(bitmapWorker.pictureToBitmap(file));
                 mapView.setOnTouchListener(new View.OnTouchListener() {
                     @Override
                     public boolean onTouch(View v, MotionEvent event) {
                         x=(int)event.getX();
                         y=(int)event.getY();
                         Toast.makeText(myContext, "X = " + x + " Y = " + y +"", Toast.LENGTH_SHORT).show();
                         return false;
                     }
                 });
             } catch (FileNotFoundException e) {
                 e.printStackTrace();
             }
             return true;
             */
		}
		return super.onOptionsItemSelected(item);
	}
	
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
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Toast.makeText(myContext,"Данные успешны отправлены",Toast.LENGTH_SHORT).show();
		}
		

	}
	
}

