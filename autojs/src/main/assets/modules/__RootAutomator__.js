module.exports = function(__runtime__, scope){
    function RootAutomator(inputDevice, nonBlockingForReady){
        inputDevice = inputDevice == undefined ? null : inputDevice;
        this.__ra__ = Object.create(new com.stardust.autojs.core.inputevent.RootAutomator(scope.context, inputDevice, !nonBlockingForReady));
        var methods = ["sendEvent", "touch", "setScreenMetrics", "touchX", "touchY", "sendSync",  "sendMtSync", "tap",
            "swipe", "press", "longPress", "touchDown", "touchUp", "touchMove", "getDefaultId", "setDefaultId", "exit"];
        for(var i = 0; i < methods.length; i++){
            var method = methods[i];
            this[method] = this.__ra__[method].bind(this.__ra__);
        }
        return this;
   }
    var p = RootAutomator.prototype;
    return RootAutomator;
}