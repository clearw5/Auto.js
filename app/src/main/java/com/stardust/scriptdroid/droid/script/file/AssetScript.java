package com.stardust.scriptdroid.droid.script.file;

import android.content.Context;
import android.util.Log;

import com.efurture.script.JSTransformer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetScript {



	public static String read(Context context, String fileName){
		try {
			InputStream is = context.getAssets().open(fileName);
			ByteArrayOutputStream output = new ByteArrayOutputStream();

			BufferedInputStream bufferIn = new BufferedInputStream(is);
			byte[] buffer = new byte[2048];
			int length = 0;
			while ((length = bufferIn.read(buffer)) >= 0){
				 output.write(buffer, 0, length);
			}
			String script = output.toString();
			Log.e("compiled ScriptEngine", script);
			return script;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String toScript(Context context, String fileName){
		try {
			InputStream is = context.getAssets().open(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String script = JSTransformer.parse(br);
			Log.e("ScriptEngine", script);
			return script;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static String toScript(String fileName){
		try {
			InputStream is = new FileInputStream(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String script =  JSTransformer.parse(br);
			Log.e("ScriptEngine", script);
			return script;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static String toScript(InputStream is){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String script =  JSTransformer.parse(br);
			Log.e("ScriptEngine", script);
			return script;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
