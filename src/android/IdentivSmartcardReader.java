package org.apache.cordova.plugin;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.identive.libs.SCard.SCARD_READERSTATE;
import com.identive.libs.SCard;
import com.identive.libs.SCard.SCardAttribute;
import com.identive.libs.SCard.SCardIOBuffer;
import com.identive.libs.SCard.SCardState;
import com.identive.libs.WinDefs;

import java.util.*;
import android.util.Log;
import android.content.*;
import java.lang.reflect.*;

public class IdentivSmartcardReader extends CordovaPlugin {
    public enum CordovaAction {
        ECHO, TEST_READER, GET_USB_PERMISSION, ESTABLISH_CONTEXT, RELEASE_CONTEXT, TEST_LIST
    }

    IdentivSmartcardReader reader = null;
    
    JSONObject argsObject;
    JSONArray argsArray;
    CallbackContext callbackContext;
    CordovaAction action;
    
    @Override
    public boolean execute(String actionString, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i("IdentivSmartcardReader", "execute called for action " + actionString);
        this.callbackContext = callbackContext;
        // read argument object, expected as first entry in args array
        try {
            argsArray = args;
            argsObject = args.getJSONObject(0);

        } catch (JSONException e){
            Log.e("IdentivSmartcardReader", "Error: JSONException " + e + " was thrown. No or bad argument object supplied!");
            callbackContext.error(e.getMessage());
            return false;
        }
        
        
        try {
            action = CordovaAction.valueOf(actionString);
        } catch (IllegalArgumentException e) {
            Log.e("IdentivSmartcardReader", "Error: JSONException " + e + " was thrown. No valid action supplied!");
            callbackContext.error(e.getMessage());
            return false;
        }
        
        if (actionString.equals("ECHO")) {
            String message = args.getString(0);
            this.echo(message, callbackContext, args);
            return true;
        }

		switch (action) {
			case TEST_READER:
				return testReader(args, callbackContext);
			case GET_USB_PERMISSION:
				return getUSBPermission(args, callbackContext);
			case ESTABLISH_CONTEXT:
				return establishContext(args, callbackContext);
			case RELEASE_CONTEXT:
				return releaseContext(args, callbackContext);
			case TEST_LIST:
				return testList(args, callbackContext);
        }
        
        return false;
    }
	
	private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }
	
	private Context getBaseContext() {
        return this.cordova.getActivity().getBaseContext();
    }
	
	private void readListOfReaders() {
		long lRetval = 0;
		ArrayList<String> deviceList = new ArrayList<String>();
		SCard trans = new SCard();
		lRetval = trans.SCardListReaders(getBaseContext(),deviceList);
		
		CharSequence[] items = null;
		
       	items = deviceList.toArray(new CharSequence[deviceList.size()]);
		
		String readerName, argsId;
		for(int i=0; i< items.length; i++){
			readerName = (String) items[i];
			argsId = "Reader_id_" + i;
			
			Log.d(argsId, readerName);
			try{
				argsObject.put(argsId, readerName);
			} catch (JSONException e) {
				Log.e("IdentivSmartcardReader", "JSONException: " + e);
			}
		}
		
		try{
			readerName = (String) items[0];
			argsObject.put("Reader_0", readerName);
			readerName = (String) items[1];
			argsObject.put("Reader_1", readerName);
		} catch (JSONException e) {
			Log.e("IdentivSmartcardReader", "JSONException: " + e);
		}
	}
	
	private void testList(JSONArray args, CallbackContext callbackContext) {
		long lRetval = 0;
		ArrayList<String> deviceList = new ArrayList<String>();
		SCard trans = new SCard();
		lRetval = trans.SCardListReaders(getBaseContext(),deviceList);
		argsObject.put("SCardListReaders", lRetval);
		Log.d("SCardListReaders", "Result - " + lRetval);
		
		try{
			argsObject.put("deviceList_size", deviceList.size());
		} catch (JSONException e) {
			Log.e("IdentivSmartcardReader", "JSONException: " + e);
		}
	}
	
	private boolean getUSBPermission(JSONArray args, CallbackContext callbackContext) {

        try{
			SCard trans = new SCard();
			
			long lRetval = trans.USBRequestPermission(getApplicationContext());
			argsObject.put("USBRequestPermission", lRetval);
			Log.d("USBRequestPermission", "Result - " + lRetval);
        } catch (JSONException e) {
            Log.e("IdentivSmartcardReader", "JSONException: " + e);
        }
		
		callbackContext.success(args);
        
		return true;
	}
	
	private boolean establishContext(JSONArray args, CallbackContext callbackContext) {

        try{
			SCard trans = new SCard();
			
			long lRetval = trans.SCardEstablishContext(getBaseContext());
			argsObject.put("SCardEstablishContext", lRetval);
			Log.d("SCardEstablishContext", "Result - " + lRetval);
		
        } catch (JSONException e) {
            Log.e("IdentivSmartcardReader", "JSONException: " + e);
        }
		
		callbackContext.success(args);
        
		return true;
	}
	
	private boolean releaseContext(JSONArray args, CallbackContext callbackContext) {

        try{
			SCard trans = new SCard();
			
			long lRetval = trans.SCardReleaseContext();
			argsObject.put("SCardReleaseContext", lRetval);
			Log.d("SCardReleaseContext", "Result - " + lRetval);
		
		
        } catch (JSONException e) {
            Log.e("IdentivSmartcardReader", "JSONException: " + e);
        }
		
		callbackContext.success(args);
        
		return true;
	}
	
	private boolean testReader(JSONArray args, CallbackContext callbackContext) {
		
		//JSONObject testResults;
		
        try{
            //testResults = new JSONObject();
			SCard trans = new SCard();
			
			
			long lRetval = trans.USBRequestPermission(getApplicationContext());
			argsObject.put("USBRequestPermission", lRetval);
			Log.d("USBRequestPermission", "Result - " + lRetval);
			
			lRetval = trans.SCardEstablishContext(getBaseContext());
			argsObject.put("SCardEstablishContext", lRetval);
			Log.d("SCardEstablishContext", "Result - " + lRetval);
			
			readListOfReaders();
			
			// lRetval = trans.SCardDisconnect(1);
			// argsObject.put("SCardDisconnect", lRetval);
			// Log.d("SCardDisconnect", "Result - " + lRetval);
			
			lRetval = trans.SCardReleaseContext();
			argsObject.put("SCardReleaseContext", lRetval);
			Log.d("SCardReleaseContext", "Result - " + lRetval);
		
		
        } catch (JSONException e) {
            Log.e("IdentivSmartcardReader", "JSONException: " + e);
        }
		
		
		callbackContext.success(args);
        
		return true;
	}
    
    private void echo(String message, CallbackContext callbackContext, JSONArray args) {
        if (message != null && message.length() > 0) {
            callbackContext.success(args);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

}