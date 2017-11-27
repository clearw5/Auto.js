
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

   images.read = rtImages.read.bind(rtImages);

   images.load = rtImages.load.bind(rtImages);

   images.saveImage = rtImages.saveImage.bind(rtImages);

   images.save = rtImages.saveImage;

   images.pixel = rtImages.pixel;

   images.detectsColor = function(img, color, x, y, threshold, algorithm){
        color = parseColor(color);
        algorithm =  algorithm || "diff";
        threshold = threshold || 16;
        var colorDetector = getColorDetector(color, algorithm, threshold);
        var pixel = images.pixel(img, x, y);
        return colorDetector.detectsColor(colors.red(pixel), colors.green(pixel), colors.blue(pixel));
   }

   images.findColor = function(img, color, options){
        color = parseColor(color);
        options = options || {};
        var region = options.region || [];
        if(options.similarity){
            var threshold = parseInt(255 * (1 - options.similarity));
        }else{
            var threshold = options.threshold || 16;
        }
        if(options.region){
            return colorFinder.findColor(img, color, threshold, buildRegion(options.region, img));
        }else{
            return colorFinder.findColor(img, color, threshold, null);
        }
   }

   images.findColorInRegion = function(img, color, x, y, width, height, threshold){
        return findColor(img, color, {
            region: [x, y, width, height],
            threshold: threshold
        });
   }

   images.findColorEquals = function(img, color, x, y, width, height){
       return findColor(img, color, {
           region: [x, y, width, height],
           threshold: 0
       });
   }

   images.findColors = function(img, color, options){
       color = parseColor(color);
       options = options || {};
       if(options.similarity){
           var threshold = parseInt(255 * (1 - options.similarity));
       }else{
           var threshold = options.threshold || 16;
       }
       if(options.region){
           return toPointArray(colorFinder.findAllColors(img, color, threshold, buildRegion(options.region, img)));
       }else{
           return toPointArray(colorFinder.findAllColors(img, color, threshold, null));
       }
  }

  images.findImage = function(img, template, options){
       options = options || {};
       var threshold = options.threshold || 0.9;
       var maxLevel = options.level || -1;
       if(options.region){
            return rtImages.findImage(img, template, threshold, buildRegion(options, img), maxLevel);
       }else{
            return rtImages.findImage(img, template, threshold, null, maxLevel);
       }
  }

  images.findImageInRegion = function(img, template, x, y, width, height, threshold){
        return images.findImage(img, template, {
            region: [x, y, width, height],
            threshold: threshold
        });
  }

  function toPointArray(points){
     var arr = [];
     for(var i = 0; i < points.length; i++){
        arr.push(points[i]);
     }
     return arr;
  }

  function buildRegion(region, img){
     var x = region[0] || 0;
     var y = region[1] || 0;
     var width = region[2] || (img.getWidth() - x);
     var height = region[3] || (img.getHeight() - y);
     return new org.opencv.core.Rect(x, y, width, height);
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

   scope.__asGlobal__(images, ['requestScreenCapture', 'captureScreen', 'findImage', 'findImageInRegion', 'findColor', 'findColorInRegion', 'findColorEquals']);

   scope.colors = colors;

   return images;
}