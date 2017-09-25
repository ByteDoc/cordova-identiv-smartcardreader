/*global cordova, module*/

var IdentivSmartcardReader = (function () {
    "use strict";
    var CORDOVA_PLUGIN_NAME = "IdentivSmartcardReader",
		CORDOVA_ACTION_ECHO = "ECHO",
		CORDOVA_ACTION_TEST_READER = "TEST_READER",
		
		CORDOVA_ACTION_GET_USB_PERMISSION = "GET_USB_PERMISSION",
		CORDOVA_ACTION_ESTABLISH_CONTEXT = "ESTABLISH_CONTEXT",
		CORDOVA_ACTION_RELEASE_CONTEXT = "RELEASE_CONTEXT",
		CORDOVA_ACTION_TEST_LIST = "TEST_LIST",
		CORDOVA_ACTION_CARD_CONNECT = "CARD_CONNECT",
		CORDOVA_ACTION_CARD_DISCONNECT = "CARD_DISCONNECT",

		
		argsObject = {},
		argsArray = [],
		
		successCallback,
		errorCallback;
		
    function debugLog(message) {
        console.log("IdentivSmartcardReader.js: " + message);
    }
    function isSet(checkVar) {
        return typeof (checkVar) != "undefined" && checkVar !== null && checkVar !== "";
    }
    /**
     * ensure that needed values are set in the argsObject
     * and set default values if initial or bad value ...
     */
	function checkArgsObject() {
        argsObject = argsArray[0];
        
    }
    function getArgsArray(args) {
        // args auf erlaubten typ/inhalt prüfen
        // nur ein Object erlaubt, kein Array!
        if (typeof (args) != "object" || args === null || Array.isArray(args)) {
            args = {};
        }
        return [args];  // Array erstellen
    }
    function init(args, cbSuccess, cbError) {
        debugLog("args before init: " + JSON.stringify(args));
        argsArray = getArgsArray(args);
        checkArgsObject();
        successCallback = cbSuccess;
        errorCallback = cbError;
        debugLog("argsObject at the end of init: " + JSON.stringify(argsObject));
    }
    function shutdown(argsArray, errorCallback) {
        if (typeof (errorCallback) != "function") {
            errorCallback = errorCallback;
        }
        // cordova.exec(
            // emptyCallback,
            // errorCallback,
            // CORDOVA_PLUGIN_NAME,
            // CORDOVA_ACTION_STOP_RFID_LISTENER,
            // argsArray
		// );
    }
	
	
	function testReader(args, successCallback, errorCallback) {
		var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_TEST_READER, argsArray);
	}
	
	
	
	function getUSBPermission(args, successCallback, errorCallback) {
		var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_GET_USB_PERMISSION, argsArray);
	}
	
	function establishContext(args, successCallback, errorCallback) {
		var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_ESTABLISH_CONTEXT, argsArray);
	}
	
	function releaseContext(args, successCallback, errorCallback) {
		var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_RELEASE_CONTEXT, argsArray);
	}
	
	function testList(args, successCallback, errorCallback) {
		var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_TEST_LIST, argsArray);
	}
	
	
	
	function cardConnect(args, successCallback, errorCallback) {
		var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_CARD_CONNECT, argsArray);
	}
	
	function cardDisconnect(args, successCallback, errorCallback) {
		var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_CARD_DISCONNECT, argsArray);
	}



    function echo(args, successCallback, errorCallback) {
        var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_ECHO, argsArray);
    }
    return {
        echo: echo,
		testReader: testReader,
		getUSBPermission: getUSBPermission,
		establishContext: establishContext,
		releaseContext: releaseContext,
		testList: testList,
		
		cardConnect: cardConnect,
		cardDisconnect: cardDisconnect
    };


}());

module.exports = {
	echo: IdentivSmartcardReader.echo,
	testReader: IdentivSmartcardReader.testReader,
	
	getUSBPermission: IdentivSmartcardReader.getUSBPermission,
	establishContext: IdentivSmartcardReader.establishContext,
	releaseContext: IdentivSmartcardReader.releaseContext,
	testList: IdentivSmartcardReader.testList,
	
	cardConnect: IdentivSmartcardReader.cardConnect,
	cardDisconnect: IdentivSmartcardReader.cardDisconnect
};
