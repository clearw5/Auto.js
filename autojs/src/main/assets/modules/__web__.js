

module.exports = function(__runtime__, scope){
    scope.newInjectableWebClient = function(){
        return new com.stardust.autojs.runtime.api.InjectableWebClient(org.mozilla.javascript.Context.getCurrentContext(), scope);
    }

    scope.newInjectableWebView = function(activity){
        return new com.stardust.autojs.runtime.api.InjectableWebView(scope.activity, org.mozilla.javascript.Context.getCurrentContext(), scope);
    }
}


