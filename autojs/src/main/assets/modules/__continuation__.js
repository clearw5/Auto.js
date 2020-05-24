module.exports = function (runtime, global) {
    const Continuation = com.stardust.autojs.rhino.continuation.Continuation;

    function continuation() {

    }

    continuation.create = function (scope) {
        scope = scope || global;
        var cont = Object.create(runtime.createContinuation(scope));
        cont.await = function () {
            let result = cont.suspend();
            if (result.error != null) {
                throw result.error;
            }
            return result.result;
        };
        cont.resumeError = function (error) {
            if (error == null || error == undefined) {
                throw TypeError("error is null or undefined");
            }
            cont.resumeWith(Continuation.Result.Companion.failure(error));
        }
        cont.resume = function (result) {
            cont.resumeWith(Continuation.Result.Companion.success(result));
        }
        return cont;
    }

    function awaitPromise(scope, promise) {
        var cont = continuation.create(scope);
        promise.then(result => {
            cont.resume(result);
        }).catch(error => {
            cont.resumeError(error);
        });
        return cont.await();
    }

    continuation.await = function (any) {
        if (Object.getPrototypeOf(any).constructor === Promise) {
            return awaitPromise(global, any);
        }
        throw new TypeError('cannot await ' + any);
    }

    continuation.delay = function (millis) {
         var cont = continuation.create();
         setTimeout(()=>{
             cont.resume();
         }, millis);
         cont.await();
     }

    continuation.__defineGetter__('enabled', function () {
        return engines.myEngine().hasFeature("continuation");
    });

    global.Promise.prototype.await = function () {
        return continuation.await(this);
    }

    return continuation;
}