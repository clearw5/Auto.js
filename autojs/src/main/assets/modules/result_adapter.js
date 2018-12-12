function ResultAdapter() {
    if (ui.isUiThread()) {
        this.cont = continuation.create();
        this.impl = {
            setResult: (result) => {
                this.cont.resume(result);
            },
            setError: (error) => {
                this.cont.resumeError(error);
            },
            get: () => {
                return this.cont.await();
            }
        };
    } else {
        this.disposable = threads.disposable();
        this.impl = {
            setResult: (result) => {
                this.disposable.setAndNotify({ result: result });
            },
            setError: (error) => {
                this.disposable.setAndNotify({ error: error });
            },
            get: () => {
                let result = this.disposable.blockedGet();
                return getOrThrow(result);
            }
        };
    }
}

function getOrThrow(result) {
    if (result.error) {
        throw result.error;
    }
    return result.result;
}

ResultAdapter.prototype.setResult = function (result) {
    this.impl.setResult(result);
}

ResultAdapter.prototype.setError = function (error) {
    this.impl.setError(error);
}

ResultAdapter.prototype.callback = function () {
    var that = this;
    return function (result, error) {
        if (that.result !== undefined) {
            that.result = {
                result: result,
                error: error
            };
            return;
        }
        if (error) {
            that.setError(error);
        } else {
            that.setResult(result);
        }
    };
}

ResultAdapter.prototype.get = function () {
    if (this.result) {
        return getOrThrow(this.result);
    }
    this.result = null;
    return this.impl.get();
}

ResultAdapter.promise = function (promiseAdapter) {
    return new Promise(function (resolve, reject) {
        promiseAdapter.onResolve(function (result) {
            resolve(result);
        }).onReject(function (error) {
            reject(error);
        });
    })
}

ResultAdapter.wait = function (promise) {
    var proto = Object.getPrototypeOf(promise);
    if (!proto || proto.constructor !== Promise) {
        promise = ResultAdapter.promise(promise);
    }
    if (continuation.enabled) {
        return promise.await();
    } else {
        return promise.wait();
    }
}

module.exports = ResultAdapter;