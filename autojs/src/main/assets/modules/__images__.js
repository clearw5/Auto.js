
module.exports = function(__runtime__, scope){
   var images = {};
   var colors = Object.create(android.graphics.Color);
   colors.toString = function(color){
        return '#' + (color >>> 0).toString(16);
   }
   if(android.os.Build.VERSION.SDK_INT < 19){
        return images;
   }

   var rtImages = __runtime__.getImages();

   var colorFinder = rtImages.colorFinder;

   images.requestScreenCapture = rtImages.requestScreenCapture.bind(rtImages);

   images.captureScreen = rtImages.captureScreen.bind(rtImages);

   images.saveImage = rtImages.saveImage.bind(rtImages);

   images.pixel = rtImages.pixel;

   images.detectsColor = function(img, color, x, y, threshold, algorithm){
        color = parseColor(color);
        algorithm =  algorithm || "rgb";
        threshold = threshold || 16;
        var colorDetector = getColorDetector(color, algorithm, threshold);
        var pixel = images.pixel(img, x, y);
        return colorDetector.detectsColor(colors.red(pixel), colors.green(pixel), colors.blue(pixel));
   }

   images.findColor = function(img, color, options){
        color = parseColor(color);
        options = options || {};
        var region = options.region || [];
        var x = region[0] || 0;
        var y = region[1] || 0;
        var width = region[2] || (img.getWidth() - x);
        var height = region[3] || (img.getHeight() - y);
        var threads = options.threads || 2;
        if(options.similarity){
            var threshold = parseInt(255 * (1 - options.similarity));
        }else{
            var threshold = options.threshold || 16;
        }
        algorithm = options.algorithm || "rgb";
        var rect = new android.graphics.Rect(x, y, width + x, height + y);
        var colorDetector = getColorDetector(color, algorithm, threshold);
        return colorFinder.findColorConcurrently(img, colorDetector, rect, threads);
   }

   images.findColorInRegion = function(img, color, x, y, width, height, threads, algorithm, threshold){
        return findColor(img, color, {
            region: [x, y, width, height],
            algorithm: algorithm,
            threshold: threshold,
            threads: threads
        });
   }

   images.findColorEquals = function(img, color, x, y, width, height, threads){
       return findColor(img, color, {
           region: [x, y, width, height],
           algorithm: "equal",
           threads: threads
       });
   }

   function getColorDetector(color, algorithm, threshold){
        switch(algorithm){
            case "rgb":
                return new com.stardust.autojs.core.image.ColorDetector.RGBDistanceDetector(color, threshold);
            case "equal":
                return new com.stardust.autojs.core.image.ColorDetector.EqualityDetector(color);
            case "diff":
                return new com.stardust.autojs.core.image.ColorDetector.DifferenceDetector(color, threshold);
            case "rgb+":
                return new com.stardust.autojs.core.image.ColorDetector.WeightedRGBDistanceDetector(color, threshold);
            case "hs":
                return new com.stardust.autojs.core.image.ColorDetector.HSDistanceDetector(color, threshold);
        }
        throw new Error("Unknown algorithm: " + algorithm);
   }

   function parseColor(color){
     if(typeof(color) == 'string'){
        if(color.startsWith('#')){
           return parseInt('0x' + color.substring(1));
        }else{
           return parseInt('0x' + color);
        }
      }
      return color;
   }

   scope.__asGlobal__(images, ['requestScreenCapture', 'captureScreen', 'findColor', 'findColorInRegion', 'findColorEquals']);

   scope.colors = colors;

   return images;
}