
module.exports = function (__runtime__, scope) {
    var threads = Object.create(__runtime__.threads);


    scope.sync = function (func, lock) {
        lock = lock || null;
        return new org.mozilla.javascript.Synchronizer(func, lock);
    }

    global.Promise.prototype.wait = function () {
        var disposable = threads.disposable();
        promise.then(result => {
            disposable.setAndNotify({ result: result });
        }).catch(error => {
            cont.resumeError({ error: error });
        });
        var r = cont.blockedGet();
        if (r.error) {
            throw r.error;
        }
        return r.result;
    }

    return threads;
}