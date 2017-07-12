
module.exports = function(__runtime__, scope){
   var images = {};
   if(android.os.Build.VERSION.SDK_INT < 19){
        return images;
   }

   var rtImages = __runtime__.getImages();

   var colorFinder = rtImages.colorFinder;

   images.requestScreenCapture = rtImages.requestScreenCapture.bind(rtImages);

   images.captureScreen = rtImages.captureScreen.bind(rtImages);

   images.saveImage = rtImages.saveImage.bind(rtImages);

   images.pixel = rtImages.pixel;

   images.detectsColor = rtImages.detectsColor.bind(rtImages);

   images.findColor = function(img, color, options){
        if(typeof(color) == 'string'){
            if(color.startsWith('#')){
                color = parseInt('0x' + color.substring(1));
            }else{
                color = parseInt('0x' + color);
            }
        }
        options = options || {};
        var region = options.region || [];
        x = region[0] || 0;
        y = region[1] || 0;
        width = region[2] || (img.getWidth() - x);
        height = region[3] || (img.getHeight() - y);
        threads = options.threads || 2;
        if(options.threshold !== 0){
            threshold = options.threshold || 8;
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
                return new com.stardust.autojs.runtime.api.image.ColorDetector.RGBDistanceDetector(color, threshold);
            case "equal":
                return new com.stardust.autojs.runtime.api.image.ColorDetector.EqualityDetector(color);
            case "diff":
                return new com.stardust.autojs.runtime.api.image.ColorDetector.DifferenceDetector(color, threshold);
            case "rgb+":
                return new com.stardust.autojs.runtime.api.image.ColorDetector.WeightedRGBDistanceDetector(color, threshold);
            case "hs":
                return new com.stardust.autojs.runtime.api.image.ColorDetector.HSDistanceDetector(color, threshold);
        }
        throw new Error("Unknown algorithm: " + algorithm);
   }

   scope.__asGlobal__(images, ['requestScreenCapture', 'captureScreen', 'findColor', 'findColorInRegion', 'findColorEquals']);

   return images;
}