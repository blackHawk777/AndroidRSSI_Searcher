package com.daniel.androidsearcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.bitmapworker.BitmapWorker;
import com.daniel.fileworker.FileWorker;
import com.daniel.wifiworker.ScannerWiFi;

public class MainActivity extends Activity {
	
	
	private ImageView mapView;
    int x=0;
    int y=0;
    private BitmapWorker bitmapWorker = new BitmapWorker();
    String service = Context.WIFI_SERVICE;
    private Integer type_signal=0;
    private File file;
    private ArrayList<String> arrayListPoints = new ArrayList<String>();
    private final static String FILE_NAME_SIGNALS="WiFiRecords.txt";
    private final static String RESULT_DIR="/SearcherData";
    String resultString="";
    TextView tv;
    Boolean isStopped=true;
    Boolean isFirst = true;
    Button buttonStart;
    Button buttonStop;
    //TextView helpView;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapView = (ImageView)findViewById(R.id.map_img_view);
        buttonStart = (Button)findViewById(R.id.button1);
        buttonStop = (Button)findViewById(R.id.button2);
        //helpView = (TextView)findViewById(R.id.textHelp);
        tv = (TextView)findViewById(R.id.textView1);
        linearLayoutForSSID = (LinearLayout)findViewById(R.id.linearLayoutForSSID);
        scanResults = new ArrayList<ScanResult>();
        fw.createFile(FILE_NAME_SIGNALS, RESULT_DIR);
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
	                        checkBox.setText(sr.SSID);
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
	                    Toast.makeText(getApplication(),"”кажите WiFi точки ", Toast.LENGTH_SHORT).show();
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
                   // String[] splitedItem=((String)pointsView.getSelectedItem()).split(",");
                    //resultString+="INSERT INTO Points VALUES (" +splitedItem[0]+ "," +splitedItem[1]+ calculateCoordinats(wifiPoints, type_signal) + "); \n";
                    resultString+="INSERT INTO Points VALUES ("+ x + "," + y  + calculateCoordinats(wifiPoints, type_signal) + "); \n";
                    fw.recordToFile(resultString);
                    testValue=0;

                }
				
				 if(!calculatingThread.isInterrupted())
	                {
	                    try{
	                        calculatingThread.interrupt();
	                    }
	                    catch (Exception e)
	                    {
	                        e.printStackTrace();
	                    }
	                }
				
			}
		});
	}
	
	protected String calculateCoordinats(ArrayList<String> wifiPoints, Integer type_signal){
        //String[] wifiPoints={"ADSL_Wireless","NIRVANA","Sha_virus","VANO"};
        //String[] wifiPoints2={"VANO","andreiru","DIR-300NRU","DIR-620"};
        int[] min = {0,0,0,0,0};
        int[] max= {300,300,300,300,300};
        int[] sum= {0,0,0,0,0};
        int[]masmeasures={1,1,1,1,1};
        int[] average= {0,0,0,0,0};
        for (List<ScanResult> resultsList : listOfResults){
            for(ScanResult results : resultsList){
                for(int j=0; j<wifiPoints.size(); j++){

                    if(results.SSID.equals(wifiPoints.get(j))){


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

        }

       /* for (int i=0; i<wifiPoints.size(); i++){
            resultAve+= "," + average[i];
        }
        */
        return result;
    }
	
	
	/*  public void configureSpinner() throws IOException {
    arrayListPoints=fw.readPointsFile(file);
    // ArrayAdapter<?> arrayAdapter = ArrayAdapter.createFromResource(myContext, R.array.points_items,R.layout.support_simple_spinner_dropdown_item);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(myContext, android.R.layout.simple_spinner_dropdown_item,arrayListPoints);
    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    pointsView.setAdapter(arrayAdapter);
    pointsView.setPromptId(R.string.help_for_spinner);
}
*/
	
	

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
             Toast.makeText(myContext, R.string.help_max,Toast.LENGTH_LONG).show();
             return true;
         case R.id.action_ave_type:
             type_signal=2;
             Toast.makeText(myContext, R.string.help_ave,Toast.LENGTH_LONG).show();
             return true;
         case R.id.action_min_type:
             type_signal=3;
             Toast.makeText(myContext, R.string.help_min,Toast.LENGTH_LONG).show();
             return  true;
         case R.id.map_menu_item:
             // получить картинку
             try {
                 mapView.setImageBitmap(bitmapWorker.pictureToBitmap(file));
                 mapView.setOnTouchListener(new View.OnTouchListener() {
                     @Override
                     public boolean onTouch(View v, MotionEvent event) {
                         x=(int)v.getX();
                         y=(int)v.getY();
                         Toast.makeText(myContext, "X = " + x + " Y = " + y +"", Toast.LENGTH_SHORT).show();
                         return false;
                     }
                 });
             } catch (FileNotFoundException e) {
                 e.printStackTrace();
             }
             return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
