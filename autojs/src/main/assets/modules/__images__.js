
module.exports = function (runtime, scope) {
    const ResultAdapter = require("result_adapter");

    var MatchingResult = (function () {
        var comparators = {
            "left": (l, r) => l.point.x - r.point.x,
            "top": (l, r) => l.point.y - r.point.y,
            "right": (l, r) => r.point.x - l.point.x,
            "bottom": (l, r) => r.point.y - l.point.y
        }
        function MatchingResult(list) {
            if (Array.isArray(list)) {
                this.matches = list;
            } else {
                this.matches = runtime.bridges.bridges.toArray(list);
            }
            this.__defineGetter__("points", () => {
                if (typeof (this.__points__) == 'undefined') {
                    this.__points__ = this.matches.map(m => m.point);
                }
                return this.__points__;
            });
        }
        MatchingResult.prototype.first = function () {
            if (this.matches.length == 0) {
                return null;
            }
            return this.matches[0];
        }
        MatchingResult.prototype.last = function () {
            if (this.matches.length == 0) {
                return null;
            }
            return this.matches[this.matches.length - 1];
        }
        MatchingResult.prototype.findMax = function (cmp) {
            if (this.matches.length == 0) {
                return null;
            }
            var target = this.matches[0];
            this.matches.forEach(m => {
                if (cmp(target, m) > 0) {
                    target = m;
                }
            });
            return target;
        }
        MatchingResult.prototype.leftmost = function () {
            return this.findMax(comparators.left);
        }
        MatchingResult.prototype.topmost = function () {
            return this.findMax(comparators.top);
        }
        MatchingResult.prototype.rightmost = function () {
            return this.findMax(comparators.right);
        }
        MatchingResult.prototype.bottommost = function () {
            return this.findMax(comparators.bottom);
        }
        MatchingResult.prototype.worst = function () {
            return this.findMax((l, r) => l.similarity - r.similarity);
        }
        MatchingResult.prototype.best = function () {
            return this.findMax((l, r) => r.similarity - l.similarity);
        }
        MatchingResult.prototype.sortBy = function (cmp) {
            var comparatorFn = null;
            if (typeof (cmp) == 'string') {
                cmp.split("-").forEach(direction => {
                    var buildInFn = comparators[direction];
                    if (!buildInFn) {
                        throw new Error("unknown direction '" + direction + "' in '" + cmp + "'");
                    }
                    (function (fn) {
                        if (comparatorFn == null) {
                            comparatorFn = fn;
                        } else {
                            comparatorFn = (function (comparatorFn, fn) {
                                return function (l, r) {
                                    var cmpValue = comparatorFn(l, r);
                                    if (cmpValue == 0) {
                                        return fn(l, r);
                                    }
                                    return cmpValue;
                                }
                            })(comparatorFn, fn);
                        }
                    })(buildInFn);
                });
            } else {
                comparatorFn = cmp;
            }
            var clone = this.matches.slice();
            clone.sort(comparatorFn);
            return new MatchingResult(clone);
        }
        return MatchingResult;
    })();

    function images() {
    }
    if (android.os.Build.VERSION.SDK_INT >= 21) {
        util.__assignFunctions__(runtime.images, images, ['captureScreen', 'read', 'copy', 'load', 'clip', 'pixel'])
    }
    images.opencvImporter = JavaImporter(
        org.opencv.core.Point,
        org.opencv.core.Point3,
        org.opencv.core.Rect,
        org.opencv.core.Algorithm,
        org.opencv.core.Scalar,
        org.opencv.core.Size,
        org.opencv.core.Core,
        org.opencv.core.CvException,
        org.opencv.core.CvType,
        org.opencv.core.TermCriteria,
        org.opencv.core.RotatedRect,
        org.opencv.core.Range,
        org.opencv.imgproc.Imgproc,
        com.stardust.autojs.core.opencv
    );
    with (images.opencvImporter) {
        const defaultColorThreshold = 4;

        var colors = Object.create(runtime.colors);
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

        var javaImages = runtime.getImages();

        var colorFinder = javaImages.colorFinder;

        images.requestScreenCapture = function (landscape) {
            let ScreenCapturer = com.stardust.autojs.core.image.capture.ScreenCapturer;
            var orientation = ScreenCapturer.ORIENTATION_AUTO;
            if (landscape === true) {
                orientation = ScreenCapturer.ORIENTATION_LANDSCAPE;
            }
            if (landscape === false) {
                orientation = ScreenCapturer.ORIENTATION_PORTRAIT;
            }
            return ResultAdapter.wait(javaImages.requestScreenCapture(orientation));
        }

        images.save = function (img, path, format, quality) {
            format = format || "png";
            quality = quality == undefined ? 100 : quality;
            return javaImages.save(img, path, format, quality);
        }

        images.saveImage = images.save;

        images.grayscale = function (img, dstCn) {
            return images.cvtColor(img, "BGR2GRAY", dstCn);
        }

        images.threshold = function (img, threshold, maxVal, type) {
            initIfNeeded();
            var mat = new Mat();
            type = type || "BINARY";
            type = Imgproc["THRESH_" + type];
            Imgproc.threshold(img.mat, mat, threshold, maxVal, type);
            return images.matToImage(mat);
        }

        images.inRange = function (img, lowerBound, upperBound) {
            initIfNeeded();
            var lb = new Scalar(colors.red(lowerBound), colors.green(lowerBound),
                colors.blue(lowerBound), colors.alpha(lowerBound));
            var ub = new Scalar(colors.red(upperBound), colors.green(upperBound),
                colors.blue(upperBound), colors.alpha(lowerBound))
            var bi = new Mat();
            Core.inRange(img.mat, lb, ub, bi);
            return images.matToImage(bi);
        }

        images.interval = function (img, color, threshold) {
            initIfNeeded();
            var lb = new Scalar(colors.red(color) - threshold, colors.green(color) - threshold,
                colors.blue(color) - threshold, colors.alpha(color));
            var ub = new Scalar(colors.red(color) + threshold, colors.green(color) + threshold,
                colors.blue(color) + threshold, colors.alpha(color));
            var bi = new Mat();
            Core.inRange(img.mat, lb, ub, bi);
            return images.matToImage(bi);
        }

        images.adaptiveThreshold = function (img, maxValue, adaptiveMethod, thresholdType, blockSize, C) {
            initIfNeeded();
            var mat = new Mat();
            adaptiveMethod = Imgproc["ADAPTIVE_THRESH_" + adaptiveMethod];
            thresholdType = Imgproc["THRESH_" + thresholdType];
            Imgproc.adaptiveThreshold(img.mat, mat, maxValue, adaptiveMethod, thresholdType, blockSize, C);
            return images.matToImage(mat);

        }
        images.blur = function (img, size, point, type) {
            initIfNeeded();
            var mat = new Mat();
            size = newSize(size);
            type = Core["BORDER_" + (type || "DEFAULT")];
            if (point == undefined) {
                Imgproc.blur(img.mat, mat, size);
            } else {
                Imgproc.blur(img.mat, mat, size, new Point(point[0], point[1]), type);
            }
            return images.matToImage(mat);
        }

        images.medianBlur = function (img, size) {
            initIfNeeded();
            var mat = new Mat();
            Imgproc.medianBlur(img.mat, mat, size);
            return images.matToImage(mat);
        }


        images.gaussianBlur = function (img, size, sigmaX, sigmaY, type) {
            initIfNeeded();
            var mat = new Mat();
            size = newSize(size);
            sigmaX = sigmaX == undefined ? 0 : sigmaX;
            sigmaY = sigmaY == undefined ? 0 : sigmaY;
            type = Core["BORDER_" + (type || "DEFAULT")];
            Imgproc.GaussianBlur(img.mat, mat, size, sigmaX, sigmaY, type);
            return images.matToImage(mat);
        }

        images.cvtColor = function (img, code, dstCn) {
            initIfNeeded();
            var mat = new Mat();
            code = Imgproc["COLOR_" + code];
            if (dstCn == undefined) {
                Imgproc.cvtColor(img.mat, mat, code);
            } else {
                Imgproc.cvtColor(img.mat, mat, code, dstCn);
            }
            return images.matToImage(mat);
        }

        images.findCircles = function (grayImg, options) {
            initIfNeeded();
            options = options || {};
            var mat = options.region == undefined ? grayImg.mat : new Mat(grayImg.mat, buildRegion(options.region, grayImg));
            var resultMat = new Mat()
            var dp = options.dp == undefined ? 1 : options.dp;
            var minDst = options.minDst == undefined ? grayImg.height / 8 : options.minDst;
            var param1 = options.param1 == undefined ? 100 : options.param1;
            var param2 = options.param2 == undefined ? 100 : options.param2;
            var minRadius = options.minRadius == undefined ? 0 : options.minRadius;
            var maxRadius = options.maxRadius == undefined ? 0 : options.maxRadius;
            Imgproc.HoughCircles(mat, resultMat, Imgproc.CV_HOUGH_GRADIENT, dp, minDst, param1, param2, minRadius, maxRadius);
            var result = [];
            for (var i = 0; i < resultMat.rows(); i++) {
                for (var j = 0; j < resultMat.cols(); j++) {
                    var d = resultMat.get(i, j);
                    result.push({
                        x: d[0],
                        y: d[1],
                        radius: d[2]
                    });
                }
            }
            if (options.region != undefined) {
                mat.release();
            }
            resultMat.release();
            return result;
        }

        images.resize = function (img, size, interpolation) {
            initIfNeeded();
            var mat = new Mat();
            interpolation = Imgproc["INTER_" + (interpolation || "LINEAR")];
            Imgproc.resize(img.mat, mat, newSize(size), 0, 0, interpolation);
            return images.matToImage(mat);
        }

        images.scale = function (img, fx, fy, interpolation) {
            initIfNeeded();
            var mat = new Mat();
            interpolation = Imgproc["INTER_" + (interpolation || "LINEAR")];
            Imgproc.resize(img.mat, mat, newSize([0, 0]), fx, fy, interpolation);
            return images.matToImage(mat);
        }

        images.rotate = function (img, degree, x, y) {
            initIfNeeded();
            if (x == undefined) {
                x = img.width / 2;
            }
            if (y == undefined) {
                y = img.height / 2;
            }
            return javaImages.rotate(img, x, y, degree);
        }

        images.concat = function (img1, img2, direction) {
            initIfNeeded();
            direction = direction || "right";
            return javaImages.concat(img1, img2, android.view.Gravity[direction.toUpperCase()]);
        }

        images.detectsColor = function (img, color, x, y, threshold, algorithm) {
            initIfNeeded();
            color = parseColor(color);
            algorithm = algorithm || "diff";
            threshold = threshold || defaultColorThreshold;
            var colorDetector = getColorDetector(color, algorithm, threshold);
            var pixel = images.pixel(img, x, y);
            return colorDetector.detectsColor(colors.red(pixel), colors.green(pixel), colors.blue(pixel));
        }

        images.findColor = function (img, color, options) {
            initIfNeeded();
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
            initIfNeeded();
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
            initIfNeeded();
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
            initIfNeeded();
            options = options || {};
            var threshold = options.threshold || 0.9;
            var maxLevel = -1;
            if (typeof (options.level) == 'number') {
                maxLevel = options.level;
            }
            var weakThreshold = options.weakThreshold || 0.6;
            if (options.region) {
                return javaImages.findImage(img, template, weakThreshold, threshold, buildRegion(options.region, img), maxLevel);
            } else {
                return javaImages.findImage(img, template, weakThreshold, threshold, null, maxLevel);
            }
        }

        images.matchTemplate = function (img, template, options) {
            initIfNeeded();
            options = options || {};
            var threshold = options.threshold || 0.9;
            var maxLevel = -1;
            if (typeof (options.level) == 'number') {
                maxLevel = options.level;
            }
            var max = options.max || 5;
            var weakThreshold = options.weakThreshold || 0.6;
            var result;
            if (options.region) {
                result = javaImages.matchTemplate(img, template, weakThreshold, threshold, buildRegion(options.region, img), maxLevel, max);
            } else {
                result = javaImages.matchTemplate(img, template, weakThreshold, threshold, null, maxLevel, max);
            }
            return new MatchingResult(result);
        }



        images.findImageInRegion = function (img, template, x, y, width, height, threshold) {
            return images.findImage(img, template, {
                region: [x, y, width, height],
                threshold: threshold
            });
        }

        images.fromBase64 = function (base64) {
            return javaImages.fromBase64(base64);
        }

        images.toBase64 = function (img, format, quality) {
            format = format || "png";
            quality = quality == undefined ? 100 : quality;
            return javaImages.toBase64(img, format, quality);
        }

        images.fromBytes = function (bytes) {
            return javaImages.fromBytes(bytes);
        }

        images.toBytes = function (img, format, quality) {
            format = format || "png";
            quality = quality == undefined ? 100 : quality;
            return javaImages.toBytes(img, format, quality);
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

        images.matToImage = function (img) {
            initIfNeeded();
            return Image.ofMat(img);
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
            if (region == undefined) {
                region = [];
            }
            var x = region[0] === undefined ? 0 : region[0];
            var y = region[1] === undefined ? 0 : region[1];
            var width = region[2] === undefined ? img.getWidth() - x : region[2];
            var height = region[3] === undefined ? (img.getHeight() - y) : region[3];
            var r = new org.opencv.core.Rect(x, y, width, height);
            if (x < 0 || y < 0 || x + width > img.width || y + height > img.height) {
                throw new Error("out of region: region = [" + [x, y, width, height] + "], image.size = [" + [img.width, img.height] + "]");
            }
            return r;
        }

        function parseColor(color) {
            if (typeof (color) == 'string') {
                color = colors.parseColor(color);
            }
            return color;
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

        function initIfNeeded() {
            javaImages.initOpenCvIfNeeded();
        }

        scope.__asGlobal__(images, ['requestScreenCapture', 'captureScreen', 'findImage', 'findImageInRegion', 'findColor', 'findColorInRegion', 'findColorEquals', 'findMultiColors']);

        scope.colors = colors;

        return images;
    }
}