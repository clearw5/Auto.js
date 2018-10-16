var J = {};

J.instanceOf = function(obj, clazz){
    return java.lang.Class.forName(clazz).isAssignableFrom(obj.getClass());
}

function typeToClass(type) {
    if (typeof(type) != 'string') {
        return type;
    }
    if(type == 'string'){
        return java.lang.String;
    }
    var types = {
        "int": "Integer",
        "long": "Long",
        "double": "Double",
        "char": "Character",
        "byte": "Byte",
        "float": "Float"
    };

    if (types[type]) {
        return Packages["java.lang." + types[type]].TYPE;
    }
    return Packages[type];
}

function array(type) {
    var clazz = typeToClass(type);
    var args = arguments;
    args[0] = clazz;
    return java.lang.reflect.Array.newInstance.apply(null, args);
}

J.array = array;

J.toJsArray = function(list, nullListToEmptyArray){
    if(list == null || list == undefined){
        if(nullListToEmptyArray){
            return [];
        }
        return null;
    }
    let arr = Array(list.size());
    for(let i = 0; i < list.size(); i++){
        arr[i] = list.get(i);
    }
    return arr;
}

J.objectToMap = function(obj){
    if(obj == null || obj === undefined){
        return null;
    }
    let map = new java.util.HashMap();
    for(let key in obj){
        if(obj.hasOwnProperty(key)){
            map.put(key, obj[key]);
        }
    }
    return map;
}

J.mapToObject = function(map){
    if(map == null || map === undefined){
        return null;
    }
    let iter = map.entrySet().iterator();
    let obj = {};
    while(iter.hasNext()){
        let entry = iter.next();
        obj[entry.key] = entry.value;
    }
    return obj;
}

module.exports = J;