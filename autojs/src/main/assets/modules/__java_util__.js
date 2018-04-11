var J = {};

J.instanceOf = function(obj, clazz){
    return java.lang.Class.forName(clazz).isAssignableFrom(obj.getClass());
}



module.exports = J;