
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

var cordova = require('cordova');
var exec = require('cordova/exec');

var AmazonIapV2 = function() {
}
var serviceName = 'AmazonIapV2Plugin';
AmazonIapV2.prototype = {
    listeners : [],
    addListener: function(eventId, listener){
               if (typeof this.listeners[eventId] == "undefined") {
                    this.listeners[eventId] = [];
               }
               
               this.listeners[eventId].push(listener);
               console.log('Listener was added for an event');
    },

    removeListener: function(eventId, listener) {
               if (typeof this.listeners[eventId] != "undefined") {
                    for (var i = this.listeners[eventId].length; i--; ) {
                        if (this.listeners[eventId][i] == listener) {
                            this.listeners[eventId].splice(i, 1);
                        }
                    }
               }
    },

    fire: function(event) {   
               if (typeof event == "string") {
                   event = JSON.parse(event
                           .replace(/\n/g, "\\n")
                           .replace(/\r/g, "\\r")
                           .replace(/\t/g, "\\t")
                           .replace(/\f/g, "\\f"));
                }

                if (!event.eventId) {
                    throw new Error("Event object missing 'eventId' property.");
                }
                console.log('Event received');
                if (this.listeners[event.eventId] instanceof Array) {
                    var listeners = this.listeners[event.eventId];
                    for (var i = 0, len = listeners.length; i < len; i++) {
                        listeners[i].call(this, event);
                    }
                }
    }
};

AmazonIapV2.prototype.getUserData = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, serviceName, 'getUserData', [])
};

AmazonIapV2.prototype.purchase = function(successCallback, errorCallback, options) {
    exec(successCallback, errorCallback, serviceName, 'purchase', options)
};

AmazonIapV2.prototype.getProductData = function(successCallback, errorCallback, options) {
    exec(successCallback, errorCallback, serviceName, 'getProductData', options)
};

AmazonIapV2.prototype.getPurchaseUpdates = function(successCallback, errorCallback, options) {
    exec(successCallback, errorCallback, serviceName, 'getPurchaseUpdates', options)
};

AmazonIapV2.prototype.notifyFulfillment = function(successCallback, errorCallback, options) {
    exec(successCallback, errorCallback, serviceName, 'notifyFulfillment', options)
};

module.exports = new AmazonIapV2();

