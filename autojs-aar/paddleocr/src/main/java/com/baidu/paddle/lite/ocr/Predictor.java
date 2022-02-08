package com.baidu.paddle.lite.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static android.graphics.Color.*;

public class Predictor {
    private static final String TAG = Predictor.class.getSimpleName();
    public boolean isLoaded = false;
    public int warmupIterNum = 1;
    public int inferIterNum = 1;
    public int cpuThreadNum = 4;
    public String cpuPowerMode = "LITE_POWER_HIGH";
    public String modelPath = "";
    public String modelName = "";
    protected OCRPredictorNative paddlePredictor = null;
    protected float inferenceTime = 0;
    // Only for object detection
    protected Vector<String> wordLabels = new Vector<>();
    protected String inputColorFormat = "BGR";
    protected long[] inputShape = new long[]{1, 3, 960};
    protected float[] inputMean = new float[]{0.485f, 0.456f, 0.406f};
    protected float[] inputStd = new float[]{0.229f, 0.224f, 0.225f};
    protected float scoreThreshold = 0.1f;
    protected Bitmap inputImage = null;
    protected float preprocessTime = 0;
    protected float postprocessTime = 0;
    protected boolean useSlim = true;

    /**
     * 检测模型
     */
    public String detModelFilename = "ch_ppocr_mobile_v2.0_det_opt.nb";
    /**
     * 识别模型
     */
    public String recModelFilename = "ch_ppocr_mobile_v2.0_rec_opt.nb";
    /**
     * 文本方向检测模型
     */
    public String clsModelFilename = "ch_ppocr_mobile_v2.0_cls_opt.nb";

    private final String defaultLabelPath = "labels/ppocr_keys_v1.txt";
    private final String defaultModelPath = "models/ocr_v2_for_cpu";
    private final String defaultModelPathSlim = "models/ocr_v2_for_cpu(slim)";

    public Predictor() {
    }

    public boolean init(Context appCtx) {
        return this.init(appCtx, defaultModelPath, defaultLabelPath);
    }

    public boolean init(Context appCtx, boolean useSlim) {
        if (this.isLoaded && this.useSlim == useSlim) {
            return true;
        }
        this.useSlim = useSlim;
        if (useSlim) {
            return this.init(appCtx, defaultModelPathSlim, defaultLabelPath);
        } else {
            return this.init(appCtx, defaultModelPath, defaultLabelPath);
        }
    }

    public boolean init(Context appCtx, String modelPath, String labelPath) {
        isLoaded = loadModel(appCtx, modelPath, cpuThreadNum, cpuPowerMode);
        if (!isLoaded) {
            return false;
        }
        isLoaded = loadLabel(appCtx, labelPath);
        return isLoaded;
    }


    public boolean init(Context appCtx, String modelPath, String labelPath, int cpuThreadNum, String cpuPowerMode,
                        long[] inputShape, float[] inputMean,
                        float[] inputStd, float scoreThreshold) {
        if (inputShape.length != 3) {
            Log.e(TAG, "Size of input shape should be: 3");
            return false;
        }
        if (inputMean.length != inputShape[1]) {
            Log.e(TAG, "Size of input mean should be: " + Long.toString(inputShape[1]));
            return false;
        }
        if (inputStd.length != inputShape[1]) {
            Log.e(TAG, "Size of input std should be: " + Long.toString(inputShape[1]));
            return false;
        }
        if (inputShape[0] != 1) {
            Log.e(TAG, "Only one batch is supported in the image classification demo, you can use any batch size in " +
                    "your Apps!");
            return false;
        }
        if (inputShape[1] != 1 && inputShape[1] != 3) {
            Log.e(TAG, "Only one/three channels are supported in the image classification demo, you can use any " +
                    "channel size in your Apps!");
            return false;
        }
        boolean isLoaded = init(appCtx, modelPath, labelPath);
        if (!isLoaded) {
            return false;
        }
        this.inputShape = inputShape;
        this.inputMean = inputMean;
        this.inputStd = inputStd;
        this.scoreThreshold = scoreThreshold;
        return true;
    }

    protected boolean loadModel(Context appCtx, String modelPath, int cpuThreadNum, String cpuPowerMode) {
        // Release model if exists
        releaseModel();

        // Load model
        if (modelPath.isEmpty()) {
            return false;
        }
        String realPath = modelPath;
        if (modelPath.charAt(0) != '/') {
            // Read model files from custom path if the first character of mode path is '/'
            // otherwise copy model to cache from assets
            realPath = appCtx.getCacheDir() + "/" + modelPath;
            Utils.copyDirectoryFromAssets(appCtx, modelPath, realPath);
        }

        OCRPredictorNative.Config config = new OCRPredictorNative.Config();
        config.cpuThreadNum = cpuThreadNum;
        config.detModelFilename = realPath + File.separator + detModelFilename;
        config.recModelFilename = realPath + File.separator + recModelFilename;
        config.clsModelFilename = realPath + File.separator + clsModelFilename;
        Log.i("Predictor", "model path" + config.detModelFilename + " ; " + config.recModelFilename + ";" + config.clsModelFilename);
        config.cpuPower = cpuPowerMode;
        paddlePredictor = new OCRPredictorNative(config);

        this.cpuThreadNum = cpuThreadNum;
        this.cpuPowerMode = cpuPowerMode;
        this.modelPath = realPath;
        this.modelName = realPath.substring(realPath.lastIndexOf("/") + 1);
        return true;
    }

    public void releaseModel() {
        if (paddlePredictor != null) {
            paddlePredictor.destroy();
            paddlePredictor = null;
        }
        isLoaded = false;
        modelPath = "";
        modelName = "";
    }

    protected boolean loadLabel(Context appCtx, String labelPath) {
        wordLabels.clear();
        wordLabels.add("black");
        // Load word labels from file
        try {
            InputStream assetsInputStream = appCtx.getAssets().open(labelPath);
            int available = assetsInputStream.available();
            byte[] lines = new byte[available];
            assetsInputStream.read(lines);
            assetsInputStream.close();
            String words = new String(lines);
            String[] contents = words.split("\n");
            for (String content : contents) {
                wordLabels.add(content);
            }
            Log.i(TAG, "Word label size: " + wordLabels.size());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return true;
    }


    public List<OcrResult> runOcr(Bitmap inputImage) {
        if (inputImage == null || !isLoaded()) {
            return Collections.emptyList();
        }

        // Pre-process image, and feed input tensor with pre-processed data

        Bitmap scaleImage = Utils.resizeWithStep(inputImage, Long.valueOf(inputShape[2]).intValue(), 32);

        Date start = new Date();
        int channels = (int) inputShape[1];
        int width = scaleImage.getWidth();
        int height = scaleImage.getHeight();
        float[] inputData = new float[channels * width * height];
        if (channels == 3) {
            int[] channelIdx = null;
            if (inputColorFormat.equalsIgnoreCase("RGB")) {
                channelIdx = new int[]{0, 1, 2};
            } else if (inputColorFormat.equalsIgnoreCase("BGR")) {
                channelIdx = new int[]{2, 1, 0};
            } else {
                Log.i(TAG, "Unknown color format " + inputColorFormat + ", only RGB and BGR color format is " +
                        "supported!");
                return Collections.emptyList();
            }

            int[] channelStride = new int[]{width * height, width * height * 2};
            int[] pixels = new int[width * height];
            scaleImage.getPixels(pixels, 0, scaleImage.getWidth(), 0, 0, scaleImage.getWidth(), scaleImage.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int color = pixels[i];
                float[] rgb = new float[]{(float) red(color) / 255.0f, (float) green(color) / 255.0f,
                        (float) blue(color) / 255.0f};
                inputData[i] = (rgb[channelIdx[0]] - inputMean[0]) / inputStd[0];
                inputData[i + channelStride[0]] = (rgb[channelIdx[1]] - inputMean[1]) / inputStd[1];
                inputData[i + channelStride[1]] = (rgb[channelIdx[2]] - inputMean[2]) / inputStd[2];
            }
        } else if (channels == 1) {
            int[] pixels = new int[width * height];
            scaleImage.getPixels(pixels, 0, scaleImage.getWidth(), 0, 0, scaleImage.getWidth(), scaleImage.getHeight());
            for (int i = 0; i < pixels.length; i++) {
                int color = pixels[i];
                float gray = (float) (red(color) + green(color) + blue(color)) / 3.0f / 255.0f;
                inputData[i] = (gray - inputMean[0]) / inputStd[0];
            }
        } else {
            Log.i(TAG, "Unsupported channel size " + channels + ",  only channel 1 and 3 is " +
                    "supported!");
            return Collections.emptyList();
        }
        float[] pixels = inputData;
        Log.i(TAG, "pixels " + pixels[0] + " " + pixels[1] + " " + pixels[2] + " " + pixels[3]
                + " " + pixels[pixels.length / 2] + " " + pixels[pixels.length / 2 + 1] + " " + pixels[pixels.length - 2] + " " + pixels[pixels.length - 1]);
        Date end = new Date();
        preprocessTime = (float) (end.getTime() - start.getTime());

        // Warm up
        for (int i = 0; i < warmupIterNum; i++) {
            paddlePredictor.runImage(inputData, width, height, channels, inputImage);
        }
        warmupIterNum = 0; // do not need warm
        // Run inference
        start = new Date();
        ArrayList<OcrResultModel> results = paddlePredictor.runImage(inputData, width, height, channels, inputImage);
        end = new Date();
        inferenceTime = (end.getTime() - start.getTime()) / (float) inferIterNum;

        results = postprocess(results);
        Log.i(TAG, "[stat] Preprocess Time: " + preprocessTime
                + " ; Inference Time: " + inferenceTime + " ;Box Size " + results.size());
        List<OcrResult> ocrResults = new ArrayList<>();
        for (OcrResultModel resultModel : results) {
            ocrResults.add(new OcrResult(resultModel));
        }
        return ocrResults;
    }


    public boolean isLoaded() {
        return paddlePredictor != null && isLoaded;
    }

    public String modelPath() {
        return modelPath;
    }

    public String modelName() {
        return modelName;
    }

    public int cpuThreadNum() {
        return cpuThreadNum;
    }

    public String cpuPowerMode() {
        return cpuPowerMode;
    }

    public float inferenceTime() {
        return inferenceTime;
    }

    public Bitmap inputImage() {
        return inputImage;
    }

    public float preprocessTime() {
        return preprocessTime;
    }

    public float postprocessTime() {
        return postprocessTime;
    }

    public String getDefaultLabelPath() {
        return defaultLabelPath;
    }

    public String getDefaultModelPath() {
        return defaultModelPath;
    }

    public String getDefaultModelPathSlim() {
        return defaultModelPathSlim;
    }

    public boolean isUseSlim() {
        return useSlim;
    }

    public void setInputImage(Bitmap image) {
        if (image == null) {
            return;
        }
        this.inputImage = image.copy(Bitmap.Config.ARGB_8888, true);
    }

    private ArrayList<OcrResultModel> postprocess(ArrayList<OcrResultModel> results) {
        for (OcrResultModel r : results) {
            StringBuilder word = new StringBuilder();
            for (int index : r.getWordIndex()) {
                if (index >= 0 && index < wordLabels.size()) {
                    word.append(wordLabels.get(index));
                } else {
                    Log.e(TAG, "Word index is not in label list:" + index);
                    word.append("×");
                }
            }
            r.setLabel(word.toString());
        }
        return results;
    }


}
