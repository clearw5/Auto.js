
module.exports = function(__runtime__, scope){
    var fs = __runtime__.files;
    var files = Object.create(fs);
    files.join = function(base){
        var paths = Array.prototype.slice.call(arguments, 1);
        return fs.join(base, paths);
    }
    scope.files = files;
    scope.open = function(path, mode, encoding, bufferSize){
         if(arguments.length == 1){
             return files.open(path);
         }else if(arguments.length == 2){
             return files.open(path, mode);
         }else if(arguments.length == 3){
             return files.open(path, mode, encoding);
         }else if(arguments.length == 4){
             return files.open(path, mode, encoding, bufferSize);
         }
    };

}