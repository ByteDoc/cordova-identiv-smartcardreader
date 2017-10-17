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
import com.identive.libs.WinDefs.*;
import com.identive.libs.*;

import java.util.*;
import android.util.Log;
import android.content.*;
import java.lang.reflect.*;

public class IdentivSmartcardReader extends CordovaPlugin {
    public enum CordovaAction {
        ECHO, TEST_READER, GET_USB_PERMISSION, ESTABLISH_CONTEXT, RELEASE_CONTEXT, TEST_LIST, CARD_CONNECT, CARD_DISCONNECT, GET_CARD_STATUS_CHANGE
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
				
			case GET_CARD_STATUS_CHANGE:
				return getCardStatusChange(args, callbackContext);
				
			case CARD_CONNECT:
				return cardConnect(args, callbackContext);
			case CARD_DISCONNECT:
				return cardDisconnect(args, callbackContext);
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
	
	private boolean testList(JSONArray args, CallbackContext callbackContext) {
		long lRetval = 0;
		ArrayList<String> deviceList = new ArrayList<String>();
		SCard trans = new SCard();
		lRetval = trans.SCardListReaders(getBaseContext(),deviceList);

		try{
			argsObject.put("SCardListReaders", lRetval);
			Log.d("IdentivSmartcardReader", "Result - " + lRetval);
		
			argsObject.put("deviceList_size", deviceList.size());
		} catch (JSONException e) {
			Log.e("IdentivSmartcardReader", "JSONException: " + e);
		}
		
		callbackContext.success(args);
		
		return true;
	}
	
	private String getReaderById(int id) {
		long lRetval = 0;
		ArrayList<String> deviceList = new ArrayList<String>();
		SCard trans = new SCard();
		lRetval = trans.SCardListReaders(getBaseContext(),deviceList);
		CharSequence[] items = null;
		
       	items = deviceList.toArray(new CharSequence[deviceList.size()]);
		return (String) items[id];
	}
	
	private boolean cardConnect(JSONArray args, CallbackContext callbackContext) {

        try{
			SCard trans = new SCard();
			
			String readerName = getReaderById(1);
			
			int mode = (int) WinDefs.SCARD_SHARE_EXCLUSIVE;
			int protocol = (int) WinDefs.SCARD_PROTOCOL_TX;
			
			long status = trans.SCardConnect(readerName, mode, protocol); 	
			
			argsObject.put("SCardConnect", status);
			Log.d("SCardConnect", "Result - " + status);
        } catch (JSONException e) {
            Log.e("IdentivSmartcardReader", "JSONException: " + e);
        }
		
		callbackContext.success(args);
        
		return true;
	}
	
	private boolean cardDisconnect(JSONArray args, CallbackContext callbackContext) {

        try{
			SCard trans = new SCard();
			
			int disposition = (int) WinDefs.SCARD_LEAVE_CARD;
			
			long status = trans.SCardDisconnect(disposition); 	
			
			argsObject.put("SCardDisconnect", status);
			Log.d("SCardDisconnect", "Result - " + status);
        } catch (JSONException e) {
            Log.e("IdentivSmartcardReader", "JSONException: " + e);
        }
		
		callbackContext.success(args);
        
		return true;
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
	
	private boolean getCardStatusChange(JSONArray args, CallbackContext callbackContext) {
		
		SCard trans = new SCard();
		String sstr = "";
		String rstr = "";
		boolean flag = false;
		CharSequence[] items = null;
		
		long lRetVal;
		
		String selectedRdr = "Identiv uTrust 4701 F CL Reader 0";
		//selectedRdr = (String) items[item];
		
		try{
			
			long lRetval = trans.USBRequestPermission(getApplicationContext());
			argsObject.put("USBRequestPermission", lRetval);
			Log.d("USBRequestPermission", "Result - " + lRetval);
			
			lRetval = trans.SCardEstablishContext(getBaseContext());
			argsObject.put("SCardEstablishContext", lRetval);
			Log.d("SCardEstablishContext", "Result - " + lRetval);
			
			ArrayList<String> deviceList = new ArrayList<String>();
			lRetval = trans.SCardListReaders(getBaseContext(),deviceList);
			Log.d("SCardListReaders", "Result - " + lRetval);
			
			lRetval = trans.SCardListReaders(getBaseContext(),deviceList);
			Log.d("IdenitveGetZ", "Result - " + lRetval);
			items = deviceList.toArray(new CharSequence[deviceList.size()]);

			selectedRdr = (String) items[1];
			
			
        } catch (JSONException e) {
            Log.e("IdentivSmartcardReader", "JSONException: " + e);
			return true;
        }
		
		SCARD_READERSTATE[] rgReaderStates = new SCARD_READERSTATE[5];
		rgReaderStates[0] = trans.new SCARD_READERSTATE();
		rgReaderStates[0].setnCurrentState(WinDefs.SCARD_STATE_UNAWARE);
		rgReaderStates[0].setSzReader(selectedRdr);
		
		do{
			if(flag) break;
			
			lRetVal = trans.SCardGetStatusChange(0, rgReaderStates, 1);
			if((rgReaderStates[0].getnEventState() & WinDefs.SCARD_STATE_CHANGED) == WinDefs.SCARD_STATE_CHANGED){
				rgReaderStates[0].setnEventState(rgReaderStates[0].getnEventState() - WinDefs.SCARD_STATE_CHANGED);
				if(rgReaderStates[0].getnEventState() == WinDefs.SCARD_STATE_PRESENT){
					sstr = "";
					// for(int i = 0; i < rgReaderStates[0].getnAtr(); i++){
						// int temp = rgReaderStates[0].getabyAtr()[i] & 0xFF;
						// if(temp < 16){
							// sstr = sstr.toUpperCase() + "0" + Integer.toHexString(rgReaderStates[0].getabyAtr()[i]) + " ";
						// }else{
							// sstr = sstr.toUpperCase() + Integer.toHexString(temp) + " " ;
						// }
					// }
					
					// connect the card to read details
					int mode = (int) WinDefs.SCARD_SHARE_EXCLUSIVE;
					int protocol = (int) WinDefs.SCARD_PROTOCOL_TX;
					
					long status = trans.SCardConnect(selectedRdr, mode, protocol); 	
					
					try{
						argsObject.put("SCardConnect", status);
						Log.d("SCardConnect", "Result - " + status);
					} catch (JSONException e) {
						Log.e("IdentivSmartcardReader", "JSONException: " + e);
					}
		
		
					String transmitCmd = "ffca000000";
					// taken from https://stackoverflow.com/questions/9514684/what-apdu-command-gets-card-id
					byte[] buf = transmitCmd.getBytes();
					byte[] inbuf = new byte[transmitCmd.length()/2];
					byte[] newBuf = { (byte) 255, (byte) 202, 00, 00, 00 };
					byte[] newBuf2 = new byte[5];
					newBuf2[0] = (byte) (Integer.parseInt("ff",16) & 0xff);
					newBuf2[1] = (byte) (Integer.parseInt("ca",16) & 0xff);
					newBuf2[2] = (byte) (Integer.parseInt("00",16) & 0xff);
					newBuf2[3] = (byte) (Integer.parseInt("00",16) & 0xff);
					newBuf2[4] = (byte) (Integer.parseInt("00",16) & 0xff);
					do{
						for(int i = 0; i < inbuf.length*2; i++){
							//(byte) (Integer.parseInt("ef",16) & 0xff);
							
							if(48 <= buf[i] && buf[i] <= 57){
								Log.d("SCardControl_TRANSMIT", "buf["+i+"]=(" + buf[i] + ")");
								buf[i] = (byte) ((buf[i] & 0x0F));
							}else if((97 <= buf[i] && buf[i] <= 102) || (65 <= buf[i] && buf[i] <= 70)){
								Log.d("SCardControl_TRANSMIT", "buf["+i+"]=(" + buf[i] + ") ... adding 9");
								buf[i] = (byte) ((buf[i] + 9) & 0x0F);
							}
						}
						Log.d("SCardControl_TRANSMIT", "inbuf.length=(" + inbuf.length + ")");
						for(int i = 0, j = 0; i < inbuf.length*2; ++i,j++){
							inbuf[j] = (byte) ((buf[i]<<4) | (buf[++i]));
							Log.d("SCardControl_TRANSMIT", "inbuf["+j+"]=(" + inbuf[j] + ")");
						}
						
						
						SCardIOBuffer transmit = trans.new SCardIOBuffer();
						//transmit.setnInBufferSize(inbuf.length);
						//transmit.setAbyInBuffer(inbuf);
						
						transmit.setnInBufferSize(newBuf.length);
						transmit.setAbyInBuffer(newBuf);
						
						transmit.setnOutBufferSize(0xffffff);
						transmit.setAbyOutBuffer(new byte[0xffffff]);
						long lRetval = trans.SCardControl((int)WinDefs.IOCTL_CCID_ESCAPE, transmit);
						if(lRetval != 0){
							// Toast.makeText(getApplicationContext(), "Unkown Command", Toast.LENGTH_SHORT).show();
							try{
								argsObject.put("SCardControl", status);
								Log.d("SCardControl", "Result - " + status);
							} catch (JSONException e) {
								Log.e("IdentivSmartcardReader", "JSONException: " + e);
							}
							callbackContext.error("IdentivSmartcardReader, SCardControl, Unkown Command");
							return true;
						}
							
						for(int i = 0; i < transmit.getnBytesReturned(); i++){
							int temp = transmit.getAbyOutBuffer()[i] & 0xFF;
							if(temp < 16){
								rstr = rstr.toUpperCase() + "0" + Integer.toHexString(transmit.getAbyOutBuffer()[i]) ;
								sstr = sstr.toUpperCase() + "0" + Integer.toHexString(transmit.getAbyOutBuffer()[i]) + " ";
							}else{
								rstr = rstr.toUpperCase() + Integer.toHexString(temp) ;
								sstr = sstr.toUpperCase() + Integer.toHexString(temp) + " " ;
							}
						}								
					}while(false);	
					

				}else{
					sstr = "Card Absent";
				}
			}
			if (sstr != "" && sstr != "Card Absent") {
				try{
					argsObject.put("readerId", sstr);
					Log.d("IdentivSmartcardReader", "Result sstr - " + sstr);
					
					argsObject.put("readerId_rstr", rstr);
					Log.d("IdentivSmartcardReader", "Result rstr - " + rstr);
				} catch (JSONException e) {
					Log.e("IdentivSmartcardReader", "JSONException: " + e);
				}
				callbackContext.success(args);
				
				return true;
			}
			int nTemp = rgReaderStates[0].getnCurrentState(); 
			rgReaderStates[0].setnCurrentState(rgReaderStates[0].getnEventState());
			rgReaderStates[0].setnEventState(nTemp);
		}while(true);
		
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