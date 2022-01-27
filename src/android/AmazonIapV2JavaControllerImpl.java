
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

import java.lang.reflect.Type;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.amazon.cptplugins.AndroidResources;
import com.amazon.cptplugins.CrossPlatformTool;
import com.amazon.cptplugins.concurrent.SdkEvent;
import com.amazon.cptplugins.concurrent.SdkEventListener;
import com.amazon.cptplugins.concurrent.SdkCallbackListener;
import com.amazon.cptplugins.concurrent.Callback;
import com.amazon.cptplugins.exceptions.AmazonException;
import com.amazon.cptplugins.exceptions.jsonutils.AmazonError;
import com.amazon.cptplugins.json.JsonSerializer;
import com.amazon.cptplugins.json.JsonSerializerImpl;
import com.amazon.cptplugins.validation.Assert;

import com.google.gson.reflect.TypeToken;

public class AmazonIapV2JavaControllerImpl implements AmazonIapV2JavaController {
    private final static int MAX_BLOCKED_CONCURRENT_ASYNC_CALLS = 10;
    private final static String TAG = "AmazonIapV2JavaControllerImpl";
    private final JsonSerializer json;
    private final ExecutorService executor;
    private final AmazonIapV2 amazonIapV2;
    private volatile Context context = null;
    private volatile SdkEventListener sdkEventListener = null;
    private volatile SdkCallbackListener sdkCallbackListener = null;
    private volatile static AndroidResources androidResources = null;

    /**
      * Creates a new controller (and a new java service)
      */
    public static AmazonIapV2JavaControllerImpl newInstance() {
        AmazonIapV2JavaControllerImpl instance = new AmazonIapV2JavaControllerImpl(new JsonSerializerImpl(), Executors.newFixedThreadPool(MAX_BLOCKED_CONCURRENT_ASYNC_CALLS));
        instance.amazonIapV2.setAmazonIapV2JavaController(instance);
        return instance;
    }
    
    public static AmazonIapV2JavaControllerImpl newInstance(final Context context) {
        AmazonIapV2JavaControllerImpl instance = new AmazonIapV2JavaControllerImpl(new JsonSerializerImpl(), Executors.newFixedThreadPool(MAX_BLOCKED_CONCURRENT_ASYNC_CALLS));
        instance.setContext(context);
        return instance;
    }

    private AmazonIapV2JavaControllerImpl(final JsonSerializer json, final ExecutorService executor) {
        this.amazonIapV2 = new AmazonIapV2Impl();
        this.json = json;
        this.executor = executor;
    }

    /**
      * Registers the controller with the service
      */
    @Override
    public void registerToJavaService() {
        amazonIapV2.setAmazonIapV2JavaController(this);
	}

    @Override
    public void setSdkEventListener(final SdkEventListener listener) {
        this.sdkEventListener = listener;
    }
    
    @Override
    public void setSdkCallbackListener(final SdkCallbackListener listener) {
        this.sdkCallbackListener = listener;
    }

    @Override
    public String getUserData(final String jsonMsg) {
        try {
            final RequestOutput result = this.amazonIapV2.getUserData();
            return json.toJson(result);
        } catch (final Exception e) {
            return json.toJson(new AmazonError(e));
        }
    }
    @Override
    public String purchase(final String jsonMsg) {
        try {
            final Type typeOfT = new TypeToken<SkuInput>() { }.getType();
            final SkuInput input = json.fromJson(jsonMsg, typeOfT);
            final RequestOutput result = this.amazonIapV2.purchase(input);
            return json.toJson(result);
        } catch (final Exception e) {
            return json.toJson(new AmazonError(e));
        }
    }
    @Override
    public String getProductData(final String jsonMsg) {
        try {
            final Type typeOfT = new TypeToken<SkusInput>() { }.getType();
            final SkusInput input = json.fromJson(jsonMsg, typeOfT);
            final RequestOutput result = this.amazonIapV2.getProductData(input);
            return json.toJson(result);
        } catch (final Exception e) {
            return json.toJson(new AmazonError(e));
        }
    }
    @Override
    public String getPurchaseUpdates(final String jsonMsg) {
        try {
            final Type typeOfT = new TypeToken<ResetInput>() { }.getType();
            final ResetInput input = json.fromJson(jsonMsg, typeOfT);
            final RequestOutput result = this.amazonIapV2.getPurchaseUpdates(input);
            return json.toJson(result);
        } catch (final Exception e) {
            return json.toJson(new AmazonError(e));
        }
    }
    @Override
    public String notifyFulfillment(final String jsonMsg) {
        try {
            final Type typeOfT = new TypeToken<NotifyFulfillmentInput>() { }.getType();
            final NotifyFulfillmentInput input = json.fromJson(jsonMsg, typeOfT);
            this.amazonIapV2.notifyFulfillment(input);
            return "{}";
        } catch (final Exception e) {
            return json.toJson(new AmazonError(e));
        }
    }

    @Override
    public void fireGetUserDataResponse(final GetUserDataResponse eventName) {
        final SdkEvent<GetUserDataResponse> sdkEvent = new SdkEvent<GetUserDataResponse>("getUserDataResponse", eventName);
        final String sdkEventJson = json.toJson(sdkEvent);
        if (this.sdkEventListener != null) {
            this.sdkEventListener.fireSdkEvent(sdkEventJson);
        }
    }
    @Override
    public void firePurchaseResponse(final PurchaseResponse eventName) {
        final SdkEvent<PurchaseResponse> sdkEvent = new SdkEvent<PurchaseResponse>("purchaseResponse", eventName);
        final String sdkEventJson = json.toJson(sdkEvent);
        if (this.sdkEventListener != null) {
            this.sdkEventListener.fireSdkEvent(sdkEventJson);
        }
    }
    @Override
    public void fireGetProductDataResponse(final GetProductDataResponse eventName) {
        final SdkEvent<GetProductDataResponse> sdkEvent = new SdkEvent<GetProductDataResponse>("getProductDataResponse", eventName);
        final String sdkEventJson = json.toJson(sdkEvent);
        if (this.sdkEventListener != null) {
            this.sdkEventListener.fireSdkEvent(sdkEventJson);
        }
    }
    @Override
    public void fireGetPurchaseUpdatesResponse(final GetPurchaseUpdatesResponse eventName) {
        final SdkEvent<GetPurchaseUpdatesResponse> sdkEvent = new SdkEvent<GetPurchaseUpdatesResponse>("getPurchaseUpdatesResponse", eventName);
        final String sdkEventJson = json.toJson(sdkEvent);
        if (this.sdkEventListener != null) {
            this.sdkEventListener.fireSdkEvent(sdkEventJson);
        }
    }

    @Override
    public Activity getCurrentAndroidActivity() {
        return AmazonIapV2JavaControllerImpl.delegateGetCurrentAndroidActivity();
    }

    @Override
    public void setAndroidResources(final AndroidResources androidResources) {
        AmazonIapV2JavaControllerImpl.androidResources = androidResources;
    }

    @Override
    public Context getContext() {
        return (Context) this.context;
    }

    @Override
    public void setContext(final Context androidContext) {
        this.context = androidContext;
    }

    @Override
    public void handleSdkCallback(final String responseJSON) {
        if (this.sdkCallbackListener != null) {
            this.sdkCallbackListener.handleSdkCallback(responseJSON);
        }
    }
    
    @Override
    public boolean runningOnUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
    
    private static Activity delegateGetCurrentAndroidActivity() {
        Assert.notNull(androidResources, "androidResoures");
        return androidResources.getCurrentAndroidActivity();
    }
    
    public CrossPlatformTool getCrossPlatformTool(){
        return androidResources.getCrossPlatformTool();
    }
}
