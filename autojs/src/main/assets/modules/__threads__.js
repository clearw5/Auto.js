
module.exports = function(__runtime__, scope){
   var threads = Object.create(__runtime__.threads);


   scope.sync = function(func, lock){
        lock = lock || null;
        return new org.mozilla.javascript.Synchronizer(func, lock);
   }

   return threads;
}