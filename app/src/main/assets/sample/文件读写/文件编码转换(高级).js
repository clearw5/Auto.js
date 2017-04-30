

convert("/sdcard/1.txt", "utf-8", "/sdcard/2.txt", "gbk");

/**
 * fromFile: 源文件路径
 * fromEncoding: 源文件编码
 * toFile: 输出文件路径
 * toEncoding: 输出文件编码
 */
function convert(fromFile, fromEncoding, toFile, toEncoding){
    fromFile = open(fromFile, "r", fromEncoding);
    toFile = open(toFile, "w", toEncoding);
    while(true){
        var line = fromFile.readline();
        if(!line)
            break;
        toFile.writeline(line);
    }
}