
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

#import <Cordova/CDV.h>
#import "CDVAmazonIapV2.h"

static NSString * const kPluginName = @"AmazonIapV2";
static NSString * const kCrossPlatformTool = @"CORDOVA";

@interface CDVAmazonIapV2 ()

@property AMAZONAmazonIapV2ObjectiveCControllerImpl *controller;

//Mapping of requestIds to callbacks
@property NSMutableDictionary *operationCallbacks; 

@end

@implementation CDVAmazonIapV2

- (void)pluginInitialize {

	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPause) name:UIApplicationDidEnterBackgroundNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onResume) name:UIApplicationWillEnterForegroundNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onOrientationWillChange) name:UIApplicationWillChangeStatusBarOrientationNotification object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onOrientationDidChange) name:UIApplicationDidChangeStatusBarOrientationNotification object:nil];
	
	self.controller = [AMAZONAmazonIapV2ObjectiveCControllerImpl sharedInstance];
	self.controller.eventListenerDelegate = self;
	self.controller.crossPlatformTool = kCrossPlatformTool;
	
}

- (void)onPause {
	//App is being put to background
}

- (void)onResume {
	//App is about to enter foreground
}

- (void)onOrientationWillChange {

}

- (void)onOrientationDidChange {

}

- (void)getUserData:(CDVInvokedUrlCommand*)command {
	NSLog(@"Executing getUserData....");
	NSData *resultJSON;
	resultJSON = [self.controller getUserData:nil];
	[self sendPluginResult:resultJSON callbackId:command.callbackId keepCallback:false];
}
- (void)purchase:(CDVInvokedUrlCommand*)command {
	NSLog(@"Executing purchase....");
	NSData *resultJSON;
	if (command.arguments) {
        	id arg = [command.arguments objectAtIndex:0];
        	if (![arg isKindOfClass:[NSDictionary class]]) {
            		CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid input parameters..."];
            		[self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            		return;
        	}
        	NSDictionary *inputDict = (NSDictionary *)arg;
        	if (inputDict) {	
				NSError *error;
				NSData *jsonInputArgs = [NSJSONSerialization dataWithJSONObject:inputDict 
options:NSJSONWritingPrettyPrinted error:&error];
				resultJSON = [self.controller purchase:jsonInputArgs];
				[self sendPluginResult:resultJSON callbackId:command.callbackId keepCallback:false];	
		}
	}	
}
- (void)getProductData:(CDVInvokedUrlCommand*)command {
	NSLog(@"Executing getProductData....");
	NSData *resultJSON;
	if (command.arguments) {
        	id arg = [command.arguments objectAtIndex:0];
        	if (![arg isKindOfClass:[NSDictionary class]]) {
            		CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid input parameters..."];
            		[self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            		return;
        	}
        	NSDictionary *inputDict = (NSDictionary *)arg;
        	if (inputDict) {	
				NSError *error;
				NSData *jsonInputArgs = [NSJSONSerialization dataWithJSONObject:inputDict 
options:NSJSONWritingPrettyPrinted error:&error];
				resultJSON = [self.controller getProductData:jsonInputArgs];
				[self sendPluginResult:resultJSON callbackId:command.callbackId keepCallback:false];	
		}
	}	
}
- (void)getPurchaseUpdates:(CDVInvokedUrlCommand*)command {
	NSLog(@"Executing getPurchaseUpdates....");
	NSData *resultJSON;
	if (command.arguments) {
        	id arg = [command.arguments objectAtIndex:0];
        	if (![arg isKindOfClass:[NSDictionary class]]) {
            		CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid input parameters..."];
            		[self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            		return;
        	}
        	NSDictionary *inputDict = (NSDictionary *)arg;
        	if (inputDict) {	
				NSError *error;
				NSData *jsonInputArgs = [NSJSONSerialization dataWithJSONObject:inputDict 
options:NSJSONWritingPrettyPrinted error:&error];
				resultJSON = [self.controller getPurchaseUpdates:jsonInputArgs];
				[self sendPluginResult:resultJSON callbackId:command.callbackId keepCallback:false];	
		}
	}	
}
- (void)notifyFulfillment:(CDVInvokedUrlCommand*)command {
	NSLog(@"Executing notifyFulfillment....");
	NSData *resultJSON;
	if (command.arguments) {
        	id arg = [command.arguments objectAtIndex:0];
        	if (![arg isKindOfClass:[NSDictionary class]]) {
            		CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid input parameters..."];
            		[self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
            		return;
        	}
        	NSDictionary *inputDict = (NSDictionary *)arg;
        	if (inputDict) {	
				NSError *error;
				NSData *jsonInputArgs = [NSJSONSerialization dataWithJSONObject:inputDict 
options:NSJSONWritingPrettyPrinted error:&error];
				resultJSON = [self.controller notifyFulfillment:jsonInputArgs];
				[self sendPluginResult:resultJSON callbackId:command.callbackId keepCallback:false];	
		}
	}	
}

- (void)sendPluginResult:(NSData *)resultJson callbackId:(NSString *)callbackID keepCallback:(bool)keep {
	CDVPluginResult* result = nil;
	if (resultJson) {
		NSError *error = nil;
		id jsonResponse = [NSJSONSerialization JSONObjectWithData:resultJson 
                                                  	options:0 
                                                    	error:&error];
		NSDictionary *responseDict = (NSDictionary *)jsonResponse;
		if (responseDict) {
			if (callbackID == nil) {
				if ([[responseDict allKeys] containsObject:@"callerId"]) {
					NSString *UUID = [responseDict objectForKey:@"callerId"];	
					if (UUID == nil) {
						NSLog(@"Unique callbackId is nil..can not send result to JS.");
						return;
					}

					callbackID = [self.operationCallbacks objectForKey:UUID];
					if (callbackID == nil) {
						NSLog(@"CallbackId is nil..can not send result to JS.");
						return;
					}		
				}
			}
			if ([[responseDict allKeys] containsObject:@"response"]) {
				NSDictionary *jsonDict = [responseDict objectForKey:@"response"];
				if ([[jsonDict allKeys] containsObject:@"error"]) {
					NSString *errorMsg = [jsonDict objectForKey:@"error"];
					result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMsg];
				} else {
					result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:jsonDict];
				}
				if (keep) {
					[result setKeepCallbackAsBool:keep];
				}
			} else {
				if ([[responseDict allKeys] containsObject:@"error"]) {
                                        NSString *errorMsg = [responseDict objectForKey:@"error"];
                                        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMsg];
                                } else {
                                        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:responseDict];
                                }
                                if (keep) {
                                        [result setKeepCallbackAsBool:keep];
                                }
			}

			if (result != nil) {
				[self.commandDelegate sendPluginResult:result callbackId:callbackID];
				[self.operationCallbacks removeObjectForKey:callbackID];
			}
		}
	}	
}

#pragma mark AMAZONAmazonIapV2EventListenerDelegate

- (void)fireSDKEvent:(NSString *)eventJSONString {
    NSLog(@"SDK Event was fired");
    NSString* escapedEventString = [eventJSONString stringByReplacingOccurrencesOfString:@"'"withString:@"\\'"];
    [self writeJavascript: [NSString stringWithFormat:@"%@.fire('%@')", kPluginName, escapedEventString]];
    NSLog(@"JS event was written");
}

@end

