module.exports = function(__runtime__, scope){
    function RootAutomator(){
         this.__ra__ = Object.create(new com.stardust.autojs.runtime.api.RootAutomator(scope.context));
        var methods = ["sendEvent", "touch", "setScreenMetrics", "touchX", "touchY", "sendSync",  "sendMtSync", "tap",
            "swipe","touchDown", "touchUp", "touchMove", "getDefaultId", "setDefaultId", "exit"];
        for(var i = 0; i < methods.length; i++){
            var method = methods[i];
            this[method] = this.__ra__[method].bind(this.__ra__);
        }
   }
    var p = RootAutomator.prototype;
    return RootAutomator;
}