
module.exports = function(__runtime__, scope){
    var files = com.stardust.pio.PFiles;
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