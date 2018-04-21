
var bridges = {};

bridges.call = function (func, target, args) {
    var arr = [];
    var len = args.length;
    for (var i = 0; i < len; i++) {
        arr.push(wrap(args[i]));
    }
    return func.apply(target, arr);
};

function wrap(value){
    if(!(value instanceof Object && 'getClass' in value && util.isFunction(value.getClass))){
        return value;
    }
    var c = value.getClass();
    if(!('getName' in c && util.isFunction(c.getName))){
        return value;
    }
    var name = c.getName();
    if(name == 'java.lang.Boolean'){
        return Boolean(value);
    }
    //TODO: is is necessary?
    if(name == 'java.lang.Integer' || name == 'java.lang.Long' || name == 'java.lang.Double'
        || name == 'java.lang.Float'){
        return Number(value);
    }
    return value;
}

bridges.asArray = function (list) {
    var arr = [];
    for (var i = 0; i < list.size(); i++) {
        arr.push(list.get(i));
    }
    for (var key in list) {
        if (typeof (key) == 'number')
            continue;
        var v = list[key];
        if (typeof (v) == 'function') {
            arr[key] = v.bind(list);
        } else {
            arr[key] = v;
        }
    }
    return arr;
};
bridges.toArray = function (iterable) {
    var iterator = iterable.iterator();
    var arr = [];
    while (iterator.hasNext()) {
        arr.push(iterator.next());
    }
    return arr;
};
bridges.toString = function (o) {
    return String(o);
};


module.exports = bridges;
