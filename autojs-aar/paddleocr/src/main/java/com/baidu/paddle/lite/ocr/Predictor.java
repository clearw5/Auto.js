package com.baidu.paddle.lite.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

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

    private static final String CHECK_IMG_BASE64 = "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";
    private static final Bitmap checkingBitmap = BitmapFactory.decodeByteArray(Base64.decode(CHECK_IMG_BASE64, Base64.DEFAULT), 0, Base64.decode(CHECK_IMG_BASE64, Base64.DEFAULT).length);
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

    private int retryTime = 1;

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
        if (!checkModelLoadedSuccess()) {
            init(appCtx, modelPath, labelPath);
        }
        return isLoaded;
    }

    /**
     * 初始化模型后通过识别预设图片校验是否初始化成功
     * 曲线救国 深层的失败原因需要后续排查
     *
     * @return
     */
    private boolean checkModelLoadedSuccess() {
        if (!isLoaded) {
            return false;
        }
        List<OcrResult> results = runOcr(checkingBitmap);
        StringBuilder sb = new StringBuilder();
        for (OcrResult result : results) {
            sb.append(result.getLabel());
        }
        isLoaded = "测试".equals(sb.toString());
        Log.d(TAG, "第" + retryTime + "次 校验是否初始化成功: " + isLoaded);
        return isLoaded || retryTime++ >= 5;
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
            // 没有必要每次都复制
            Utils.copyDirectoryFromAssetsIfNeeded(appCtx, modelPath, realPath);
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
                    word.append(" ");
                }
            }
            r.setLabel(word.toString());
        }
        return results;
    }


}
