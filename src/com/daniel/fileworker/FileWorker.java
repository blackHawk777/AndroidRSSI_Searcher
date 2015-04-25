package com.daniel.fileworker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class FileWorker {
	private File sdFile;
    private File sdPath;
    private final static String POINTS_FILE="points.txt";
    private final static String DIRECTORY="/SearcherData";
    private ArrayList<String> pointsList = new ArrayList<String>();
	public File getSdFile() {
		return sdFile;
	}
	public void setSdFile(File sdFile) {
		this.sdFile = sdFile;
	}
	public File getSdPath() {
		return sdPath;
	}
	public void setSdPath(File sdPath) {
		this.sdPath = sdPath;
	}
	
	
	 public void createFile(String fileName, String dir)
	    {
	        sdPath = Environment.getExternalStorageDirectory();
	        sdPath = new File(sdPath.getAbsolutePath() + dir);
	        sdPath.mkdirs();
	        sdFile = new File(sdPath, fileName);
	    }

	    public void recordToFile(String resultString)
	    {
	        try {

	            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile,true));
	            bw.write(resultString);
	            bw.close();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    public ArrayList<String> readPointsFile(File file) throws IOException {
	        file = Environment.getExternalStorageDirectory();
	        file = new File(file.getAbsolutePath() + DIRECTORY);
	        File points = new File(file, POINTS_FILE);

	        //создание файла
	        if(!points.exists()) {
	            points.createNewFile();
	            // заполнить файл координатами
	        }
	        else
	        {
	            BufferedReader br = new BufferedReader(new FileReader(points));
	        	String result="";
	            try {
	                while ((result = br.readLine()) != null) {
	                    pointsList.add(new String(result));
	                }
	                br.close();
	            	            }
	            catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return pointsList;
	    }
}
