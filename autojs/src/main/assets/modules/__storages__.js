
module.exports = function(__runtime__, scope){
    var storages = {};
    storages.create = function(name){
        return new LocalStorage(name);
    }

    storages.remove = function(name){
        this.create(name).clear();
    }

    return storages;

    function LocalStorage(name){
        this._storage = new com.stardust.autojs.core.storage.LocalStorage(context, name);
        this.put = function(key, value){
            if(typeof(value) == 'undefined'){
                throw new TypeError('value cannot be undefined');
            }
            this._storage.put(key, JSON.stringify(value));
        }
        this.get = function(key, defaultValue){
            var value = this._storage.getString(key, null);
            if(!value){
                return defaultValue;
            }
            return JSON.parse(value);
        }
        this.remove = function(key){
            this._storage.remove(key);
        }
        this.contains = function(key){
            return this._storage.contains(key);
        }
        this.clear = function(key){
            this._storage.clear();
        }
    }
}

