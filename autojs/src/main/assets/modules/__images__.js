
module.exports = function (__runtime__, scope) {
    const defaultColorThreshold = 4;
    importPackage(org.opencv.core);
    const Imgproc = org.opencv.imgproc.Imgproc;

    var images = {};
    var colors = Object.create(__runtime__.colors);
    colors.alpha = function (color) {
        color = parseColor(color);
        return color >>> 24;
    }
    colors.red = function (color) {
        color = parseColor(color);
        return (color >> 16) & 0xFF;
    }
    colors.green = function (color) {
        color = parseColor(color);
        return (color >> 8) & 0xFF;
    }
    colors.blue = function (color) {
        color = parseColor(color);
        return color & 0xFF;
    }

    colors.isSimilar = function (c1, c2, threshold, algorithm) {
        c1 = parseColor(c1);
        c2 = parseColor(c2);
        threshold = threshold == undefined ? 4 : threshold;
        algorithm = algorithm == undefined ? "diff" : algorithm;
        var colorDetector = getColorDetector(c1, algorithm, threshold);
        return colorDetector.detectsColor(colors.red(c2), colors.green(c2), colors.blue(c2));
    }

    if (android.os.Build.VERSION.SDK_INT < 19) {
        return images;
    }

    var rtImages = __runtime__.getImages();

    var colorFinder = rtImages.colorFinder;

    images.requestScreenCapture = rtImages.requestScreenCapture.bind(rtImages);

    images.captureScreen = rtImages.captureScreen.bind(rtImages);

    images.read = rtImages.read.bind(rtImages);

    images.copy = rtImages.copy.bind(rtImages);

    images.load = rtImages.load.bind(rtImages);

    images.clip = rtImages.clip.bind(rtImages);

    images.save = function (img, path, format, quality) {
        format = format || "png";
        quality = quality == undefined ? 100 : quality;
        return rtImages.save(img, path, format, quality);
    }

    images.saveImage = images.save;

    images.pixel = rtImages.pixel;

    images.grayscale = function (img, dstCn) {
        return images.cvtColor(img, "BGR2GRAY", dstCn);
    }

    images.threshold = function (img, threshold, maxVal, type) {
        var mat = newMat();
        type = type || "binary";
        type = Imgproc["THRESH_" + type.toUpperCase()];
        Imgproc.threshold(img.mat, mat, threshold, maxVal, type);
        return matToImage(mat);
    }

    images.blur = function (img, size, point, type) {
        var mat = newMat();
        size = newSize(size);
        type = Imgproc["BORDER_" + (type || "CONSTANT")];
        if (point == undefined) {
            Imgproc.blur(img.mat, mat, size);
        } else {
            Imgproc.blur(img.mat, mat, size, new Point(point[0], point[1]), type);
        }
        return matToImage(mat);
    }

    images.medianBlur = function (img, size) {
        var mat = newMat();
        Imgproc.medianBlur(img.mat, mat, size);
        return matToImage(mat);
    }


    images.gaussianBlur = function (img, size, sigmaX, sigmaY, type) {
        var mat = newMat();
        size = newSize(size);
        sigmaX = sigmaX == undefined ? 0 : sigmaX;
        sigmaY = sigmaY == undefined ? 0 : sigmaY;
        type = Imgproc["BORDER_" + (type || "DEFAULT")];
        Imgproc.GaussianBlur(img.mat, mat, size, sigmaX, sigmaY, type);
        return matToImage(mat);
    }

    images.cvtColor = function (img, code, dstCn) {
        var mat = newMat();
        code = Imgproc["COLOR_" + code];
        if (dstCn == undefined) {
            Imgproc.cvtColor(img.mat, mat, code);
        } else {
            Imgproc.cvtColor(img.mat, mat, code, dstCn);
        }
        return matToImage(mat);
    }

    images.detectsColor = function (img, color, x, y, threshold, algorithm) {
        color = parseColor(color);
        algorithm = algorithm || "diff";
        threshold = threshold || defaultColorThreshold;
        var colorDetector = getColorDetector(color, algorithm, threshold);
        var pixel = images.pixel(img, x, y);
        return colorDetector.detectsColor(colors.red(pixel), colors.green(pixel), colors.blue(pixel));
    }

    images.findColor = function (img, color, options) {
        color = parseColor(color);
        options = options || {};
        var region = options.region || [];
        if (options.similarity) {
            var threshold = parseInt(255 * (1 - options.similarity));
        } else {
            var threshold = options.threshold || defaultColorThreshold;
        }
        if (options.region) {
            return colorFinder.findColor(img, color, threshold, buildRegion(options.region, img));
        } else {
            return colorFinder.findColor(img, color, threshold, null);
        }
    }

    images.findColorInRegion = function (img, color, x, y, width, height, threshold) {
        return findColor(img, color, {
            region: [x, y, width, height],
            threshold: threshold
        });
    }

    images.findColorEquals = function (img, color, x, y, width, height) {
        return findColor(img, color, {
            region: [x, y, width, height],
            threshold: 0
        });
    }

    images.findAllPointsForColor = function (img, color, options) {
        color = parseColor(color);
        options = options || {};
        if (options.similarity) {
            var threshold = parseInt(255 * (1 - options.similarity));
        } else {
            var threshold = options.threshold || defaultColorThreshold;
        }
        if (options.region) {
            return toPointArray(colorFinder.findAllPointsForColor(img, color, threshold, buildRegion(options.region, img)));
        } else {
            return toPointArray(colorFinder.findAllPointsForColor(img, color, threshold, null));
        }
    }

    images.findMultiColors = function (img, firstColor, paths, options) {
        options = options || {};
        firstColor = parseColor(firstColor);
        var list = java.lang.reflect.Array.newInstance(java.lang.Integer.TYPE, paths.length * 3);
        for (var i = 0; i < paths.length; i++) {
            var p = paths[i];
            list[i * 3] = p[0];
            list[i * 3 + 1] = p[1];
            list[i * 3 + 2] = parseColor(p[2]);
        }
        var region = options.region ? buildRegion(options.region, img) : null;
        var threshold = options.threshold === undefined ? defaultColorThreshold : options.threshold;
        return colorFinder.findMultiColors(img, firstColor, threshold, region, list);
    }

    images.findImage = function (img, template, options) {
        options = options || {};
        var threshold = options.threshold || 0.9;
        var maxLevel = -1;
        if (typeof (options.level) == 'number') {
            maxLevel = options.level;
        }
        var weakThreshold = options.weakThreshold || 0.7;
        if (options.region) {
            return rtImages.findImage(img, template, weakThreshold, threshold, buildRegion(options.region, img), maxLevel);
        } else {
            return rtImages.findImage(img, template, weakThreshold, threshold, null, maxLevel);
        }
    }

    images.findImageInRegion = function (img, template, x, y, width, height, threshold) {
        return images.findImage(img, template, {
            region: [x, y, width, height],
            threshold: threshold
        });
    }

    images.fromBase64 = function (base64) {
        return rtImages.fromBase64(base64);
    }

    images.toBase64 = function (img, format, quality) {
        format = format || "png";
        quality = quality == undefined ? 100 : quality;
        return rtImages.toBase64(img, format, quality);
    }

    images.fromBytes = function (bytes) {
        return rtImages.fromBytes(bytes);
    }

    images.toBytes = function (img, format, quality) {
        format = format || "png";
        quality = quality == undefined ? 100 : quality;
        return rtImages.toBytes(img, format, quality);
    }

    images.readPixels = function (path) {
        var img = images.read(path);
        var bitmap = img.getBitmap();
        var w = bitmap.getWidth();
        var h = bitmap.getHeight();
        var pixels = util.java.array("int", w * h);
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
        img.recycle();
        return {
            data: pixels,
            width: w,
            height: h
        };
    }


    function getColorDetector(color, algorithm, threshold) {
        switch (algorithm) {
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


    function toPointArray(points) {
        var arr = [];
        for (var i = 0; i < points.length; i++) {
            arr.push(points[i]);
        }
        return arr;
    }

    function buildRegion(region, img) {
        var x = region[0] === undefined ? 0 : region[0];
        var y = region[1] === undefined ? 0 : region[1];
        var width = region[2] === undefined ? img.getWidth() - x : region[2];
        var height = region[3] === undefined ? (img.getHeight() - y) : region[3];
        var r = new org.opencv.core.Rect(x, y, width, height);
        return r;
    }

    function parseColor(color) {
        if (typeof (color) == 'string') {
            color = colors.parseColor(color);
        }
        return color;
    }

    function newMat() {
        return new org.opencv.core.Mat();
    }

    function matToImage(mat) {
        return com.stardust.autojs.core.image.ImageWrapper.ofMat(mat);
    }

    function newSize(size) {
        if (!Array.isArray(size)) {
            size = [size, size];
        }
        if (size.length == 1) {
            size = [size[0], size[0]];
        }
        return new Size(size[0], size[1]);
    }

    scope.__asGlobal__(images, ['requestScreenCapture', 'captureScreen', 'findImage', 'findImageInRegion', 'findColor', 'findColorInRegion', 'findColorEquals', 'findMultiColors']);

    scope.colors = colors;

    return images;
}