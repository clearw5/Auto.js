package com.stardust.scriptdroid.tool;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Stardust on 2017/4/22.
 */

public class BitmapTool {

    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        return Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
    }

    /*

    public static MatOfDMatch matchesBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.BRISK);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        Mat descriptors1 = computeDescriptors(bitmap1, detector, descriptor);
        Mat descriptors2 = computeDescriptors(bitmap2, detector, descriptor);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1, descriptors2, matches);
        return matches;
    }

    private static Mat computeDescriptors(Bitmap bitmap, FeatureDetector detector, DescriptorExtractor descriptor) {
        Mat mat = bitmapToMat(bitmap.copy(bitmap.getConfig(), true));
        Mat descriptors = new Mat();
        MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
        detector.detect(mat, keyPoints1);
        descriptor.compute(mat, keyPoints1, descriptors);
        return descriptors;
    }

    private static Mat bitmapToMat(Bitmap bmp) {
        Mat mat = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, mat);
        return mat;
    }

    public static Core.MinMaxLocResult templateMatching(Bitmap bitmap, Bitmap tmp, int matchMethod) {
        Mat img = bitmapToMat(bitmap);
        Mat template = bitmapToMat(tmp);

        // / Create the result matrix
        int result_cols = img.cols() - template.cols() + 1;
        int result_rows = img.rows() - template.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, template, result, matchMethod);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        return mmr;
    }
    */
}
