
module.exports = function(__runtime__, scope){
    scope.files = com.stardust.pio.PFile;
    scope.open = function(path, mode, encoding, bufferSize){
         if(arguments.length == 1){
             return com.stardust.pio.PFile.open(path);
         }else if(arguments.length == 2){
             return com.stardust.pio.PFile.open(path, mode);
         }else if(arguments.length == 3){
             return com.stardust.pio.PFile.open(path, mode, encoding);
         }else if(arguments.length == 4){
             return com.stardust.pio.PFile.open(path, mode, encoding, bufferSize);
         }
    };

}