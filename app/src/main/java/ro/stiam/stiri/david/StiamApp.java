package ro.stiam.stiri.david;

import android.app.Application;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class StiamApp extends Application {
	
    public JSONObject getJSON(String url){
    	String result  = new String();
        try {
            URL location = new URL(url);
            URLConnection connection = location.openConnection();
            BufferedReader data = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = data.readLine()) != null) {
            	result = line;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    
	    if(result != null){
	    	try {
				return new JSONObject(result);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return new JSONObject();
    }

}
