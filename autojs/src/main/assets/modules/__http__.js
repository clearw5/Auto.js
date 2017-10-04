module.exports = function(runtime, scope){
    importPackage(Packages["okhttp3"]);
    var http = {};
    http.get = function(url, options, callback){
        options = options || {};
        options.method = "GET";
        return http.request(url, options, callback);
    }

    http.client = function(){
        if(!http._client_){
            http._client_ = new OkHttpClient();
        }
        return http._client_;
    }

    http.post = function(url, data, options, callback){
        options = options || {};
        options.method = "POST";
        options.contentType = options.contentType || "application/x-www-form-urlencoded";
        if(data){
            fillPostData(options, data);
        }
        return http.request(url, options, callback);
    }

    http.postJson = function(url, data, options, callback){
       options = options || {};
       options.contentType = "application/json";
       return http.post(url, data, options, callback);
    }

    http.request = function(url, options, callback){
        var call = http.newCall(buildRequest(url, options));
        if(!callback){
            return wrapResponse(call.execute());
        }
        call.enqueue(new Callback({
            onResponse: function(call, res){
                callback(res);
            },
            onFailure: function(call, ex){
                callback(null, ex);
            }
        }));
    }

    http.buildRequest = function(url, options){
        var r = new Request.Builder();
        if(!url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://" + url;
        }
        r.url(url);
        if(options.headers){
            setHeaders(r, options.headers);
        }
        if(options.body){
            r.method(options.method, parseBody(options, options.body));
        }else{
            r.method(options.method, null);
        }
        return r.build();
    }

    function fillPostData(options, data){
        if(options.contentType == "application/x-www-form-urlencoded"){
            var b = new FormBody.Builder();
            for(var key in data){
                if(data.hasOwnProperty(key)){
                    b.add(key, data[key]);
                }
            }
            options.body = b.build();
        }else if(options.contentType == "application/json"){
            options.body = JSON.stringify(data);
        }else{
            //todo what?
        }
    }

    function setHeaders(r, headers){
      for(var key in headers){
        if(headers.hasOwnProperty(key)){
            r.header(key, headers[key]);
        }
      }
    }

    function parseBody(options, body){
        if(typeof(body) == "string"){
            body = RequestBody.create(MediaType.parse(options.contentType), body);
        }else{
            body = new RequestBody({
               contentType: function(){
                   return MediaType.parse(options.contentType);
               },
               writeTo: body
           });
        }
        return body;
    }

    function wrapResponse(res){
        var r = {};
        r.statusCode = res.code();
        var headers = res.headers();
        r.headers = {};
        for(var i = 0; i < headers.size(); i++){
            r.headers[headers.name(i)] = headers.value(i);
        }
        r.body = Object.create(res.body());
        r.body.json = function(){
            return JSON.parse(r.body.string());
        }
        r.request = res.request();
        r.url = r.request.url();
        r.method = r.request.method();
        return r;
    }

    return http;
}