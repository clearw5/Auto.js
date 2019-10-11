
var bridges = {};

bridges.call = function (func, target, args) {
    var arr = [];
    var len = args.length;
    for (var i = 0; i < len; i++) {
        arr.push(wrap(args[i]));
    }
    return func.apply(target, arr);
};

/*
   Java Object: 拥有getClass, notify, toString, hashCode, equals等函数
               没有prototype, __proto__, constructor等属性
               使用obj.xxx时如果没有xxx属性可能会直接抛出异常而不是undefined？？？
               只能使用in关键字来判断某个属性是否存在(但in关键字不能用于JavaScript基本类型)
               typeof()返回'object'
               instanceof Object为false

*/
function wrap(value){
    if(value == null || value == undefined){
        return value;
    }
    if(!(typeof(value) == 'object' && value.getClass && util.isFunction(value.getClass))){
        return value;
    }
    var c = value.getClass();
    if(!(c.getName && util.isFunction(c.getName))){
        return value;
    }
    var name = c.getName();
    if(name == 'java.lang.Boolean'){
        return value == true;
    }
    //TODO: is is necessary?
    if(name == 'java.lang.Integer' || name == 'java.lang.Long' || name == 'java.lang.Double'
        || name == 'java.lang.Float'){
        return Number(value);
    }
    return value;
}

bridges.toArray = function (iterable) {
    var iterator = iterable.iterator();
    var arr = [];
    while (iterator.hasNext()) {
        arr.push(iterator.next());
    }
    return arr;
};

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

bridges.toString = function (o) {
    return String(o);
};


module.exports = bridges;
