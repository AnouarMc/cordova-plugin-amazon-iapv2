
/* 
* Copyright 2014 Amazon.com,
* Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the
* "License"). You may not use this file except in compliance
* with the License. A copy of the License is located at
*
* http://aws.amazon.com/apache2.0/
*
* or in the "license" file accompanying this file. This file is
* distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
* CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and
* limitations under the License.
*/
package com.amazon.device.iap.cpt;

import android.content.Context;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.amazon.cptplugins.AndroidResources;
import com.amazon.cptplugins.CrossPlatformTool;
import com.amazon.cptplugins.SdkEvent;


import android.app.Activity;

import com.amazon.device.iap.cpt.AmazonIapV2JavaController;
import com.amazon.device.iap.cpt.AmazonIapV2JavaControllerImpl;

import com.amazon.cptplugins.concurrent.SdkEventListener;

public class AmazonIapV2Plugin extends CordovaPlugin implements AndroidResources,  SdkEventListener {
    private static final String TAG = "AmazonIapV2";
    private static final String ERROR = "error";
    private static final String CALLER_ID = "callerId";
    private static final String RESPONSE = "response";
    private static final String PLUGIN = "AmazonIapV2";

    private volatile CordovaInterface cordova;
    private AmazonIapV2JavaControllerImpl sdkController = null;
    
    public AmazonIapV2Plugin() {
            
    }
    
    public AmazonIapV2JavaControllerImpl getSdkController() {
        return this.sdkController;
    }
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordova = cordova;
        Context context = ((CordovaActivity)cordova.getActivity()).getApplicationContext();
        sdkController = AmazonIapV2JavaControllerImpl.newInstance(context);
        this.sdkController.setAndroidResources((AndroidResources)this);
        this.sdkController.setSdkEventListener((SdkEventListener)this);
        this.sdkController.registerToJavaService();        
    }

    @Override
    public Activity getCurrentAndroidActivity() {
        return this.cordova.getActivity();
    }
    
    @Override
    public CrossPlatformTool getCrossPlatformTool() {
        return CrossPlatformTool.CORDOVA;
    }

    private enum OPERATIONS {
            GETUSERDATA {
                @Override
                public boolean execute(JSONArray args, CallbackContext callbackContext, AmazonIapV2Plugin plugin) {
                    LOG.d(TAG,"Executing GetUserData...");
                    String result;
                    result = plugin.getSdkController().getUserData(args.toString());
                    return plugin.sendPluginResult(result,callbackContext,false);
                }
            },
            PURCHASE {
                @Override
                public boolean execute(JSONArray args, CallbackContext callbackContext, AmazonIapV2Plugin plugin) {
                    LOG.d(TAG,"Executing Purchase...");
                    String result;
                    try {
                        JSONObject inputJson = args.getJSONObject(0);
                        result = plugin.getSdkController().purchase(inputJson.toString());
                        return plugin.sendPluginResult(result,callbackContext,false);
                    } catch(JSONException e) {
                        LOG.d(TAG,e.getMessage());
                            return false;
                    }
                }
            },
            GETPRODUCTDATA {
                @Override
                public boolean execute(JSONArray args, CallbackContext callbackContext, AmazonIapV2Plugin plugin) {
                    LOG.d(TAG,"Executing GetProductData...");
                    String result;
                    try {
                        JSONObject inputJson = args.getJSONObject(0);
                        result = plugin.getSdkController().getProductData(inputJson.toString());
                        return plugin.sendPluginResult(result,callbackContext,false);
                    } catch(JSONException e) {
                        LOG.d(TAG,e.getMessage());
                            return false;
                    }
                }
            },
            GETPURCHASEUPDATES {
                @Override
                public boolean execute(JSONArray args, CallbackContext callbackContext, AmazonIapV2Plugin plugin) {
                    LOG.d(TAG,"Executing GetPurchaseUpdates...");
                    String result;
                    try {
                        JSONObject inputJson = args.getJSONObject(0);
                        result = plugin.getSdkController().getPurchaseUpdates(inputJson.toString());
                        return plugin.sendPluginResult(result,callbackContext,false);
                    } catch(JSONException e) {
                        LOG.d(TAG,e.getMessage());
                            return false;
                    }
                }
            },
            NOTIFYFULFILLMENT {
                @Override
                public boolean execute(JSONArray args, CallbackContext callbackContext, AmazonIapV2Plugin plugin) {
                    LOG.d(TAG,"Executing NotifyFulfillment...");
                    String result;
                    try {
                        JSONObject inputJson = args.getJSONObject(0);
                        result = plugin.getSdkController().notifyFulfillment(inputJson.toString());
                        return plugin.sendPluginResult(result,callbackContext,false);
                    } catch(JSONException e) {
                        LOG.d(TAG,e.getMessage());
                            return false;
                    }
                }
            };

        public abstract boolean execute(JSONArray args, CallbackContext callbackContext, AmazonIapV2Plugin plugin);
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            return OPERATIONS.valueOf(action.toUpperCase()).execute(args, callbackContext, this);
        } catch (IllegalArgumentException e) {
            LOG.d(TAG,"Invalid action - " + action);
            return false;
        }
        //return true;
    }

    @Override
    public void fireSdkEvent(String jsonEventResponse) {
        LOG.d(TAG,"Sdk event was fired");
        final String escapedJsonEventResponse = jsonEventResponse.replaceAll("'", "\\'");
        this.webView.sendJavascript("javascript:" + PLUGIN + ".fire('" + escapedJsonEventResponse + "')");
    }


    public boolean sendPluginResult(final String result,final CallbackContext callbackContext,final boolean keepCallback) {
        boolean ret = false;
        PluginResult resultToSend = null;
        try {
            if (result == null || result.equals("null")) {
                String errorMsg = "error: null response from plugin";
                resultToSend = new PluginResult(Status.ERROR, errorMsg);
                ret = false;
            } else {
                JSONObject resultJO = new JSONObject(result);
                if (resultJO.has(RESPONSE)) {
                    JSONObject responseJO = resultJO.getJSONObject(RESPONSE);
                    if (responseJO.has(ERROR)) {
                        String errorMsg = "error : " + (String)responseJO.get(ERROR);
                        resultToSend = new PluginResult(Status.ERROR,errorMsg);
                        ret = false;
                    } else {
                        resultToSend = new PluginResult(Status.OK,responseJO);
                         if (keepCallback) {
                                                resultToSend.setKeepCallback(true);
                                        }
                        ret = true;
                    }
                } else { //TODO need to remove this else block once we refactor response JSON
                    if (resultJO.has(ERROR)) {
                        String errorMsg = "error : " + (String)resultJO.get(ERROR);
                        resultToSend = new PluginResult(Status.ERROR,errorMsg);
                        ret = false;
                    } else {
                        resultToSend = new PluginResult(Status.OK,resultJO);
                        if (keepCallback) {
                                                    resultToSend.setKeepCallback(true);
                                            }
                        ret = true;
                    }            
                }
            }    
            if (resultToSend != null) {
                callbackContext.sendPluginResult(resultToSend);
            }
        } catch (JSONException e) {
            callbackContext.error(e.getMessage());
            ret = false;
        }
        return ret;
    }
}
