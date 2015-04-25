package com.daniel.bitmapworker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapWorker {
	
	private static final String PATH_TO_PICTURE="/mnt/sdcard/SearcherData/map.png";

	public Bitmap pictureToBitmap(File graphicFile) throws FileNotFoundException {
	   graphicFile = new File(PATH_TO_PICTURE);
	   FileInputStream inputStream = new FileInputStream(graphicFile);
	   Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
	   return bitmap;
	   }
}
