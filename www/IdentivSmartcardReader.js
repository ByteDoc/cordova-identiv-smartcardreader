/*global cordova, module*/

var IdentivSmartcardReader = (function () {
    "use strict";
    var CORDOVA_PLUGIN_NAME = "IdentivSmartcardReader",
		CORDOVA_ACTION_ECHO = "ECHO",
		CORDOVA_ACTION_TEST_READER = "TEST_READER",

		
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



    function echo(args, successCallback, errorCallback) {
        var argsArray = getArgsArray(args);
        cordova.exec(successCallback, errorCallback, CORDOVA_PLUGIN_NAME, CORDOVA_ACTION_ECHO, argsArray);
    }
    return {
        echo: echo,
		testReader: testReader
    };


}());

module.exports = {
	echo: IdentivSmartcardReader.echo,
	testReader: IdentivSmartcardReader.testReader
};


