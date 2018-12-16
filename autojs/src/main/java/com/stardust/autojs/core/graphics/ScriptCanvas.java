package com.stardust.autojs.core.graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.stardust.autojs.core.image.ImageWrapper;
import com.stardust.autojs.runtime.ScriptRuntime;

/**
 * Created by Stardust on 2018/3/22.
 */

public class ScriptCanvas {

    private Canvas mCanvas;
    private Bitmap mBitmap;

    public ScriptCanvas(int width, int height) {
        this(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
    }

    public ScriptCanvas(@NonNull Bitmap bitmap) {
        mCanvas = new Canvas(bitmap);
        mBitmap = bitmap;
    }

    public ScriptCanvas(@NonNull ImageWrapper image) {
        this(image.getBitmap().copy(image.getBitmap().getConfig(), true));
    }

    public ScriptCanvas() {

    }

    public Canvas getAndroidCanvas() {
        return mCanvas;
    }

    void setCanvas(Canvas canvas) {
        mCanvas = canvas;
    }

    public ImageWrapper toImage() {
        return ImageWrapper.ofBitmap(mBitmap.copy(mBitmap.getConfig(), true));
    }

    public boolean isHardwareAccelerated() {
        return mCanvas.isHardwareAccelerated();
    }

    public void setBitmap(@Nullable Bitmap bitmap) {
        mCanvas.setBitmap(bitmap);
    }

    public boolean isOpaque() {
        return mCanvas.isOpaque();
    }

    public int getWidth() {
        return mCanvas.getWidth();
    }

    public int getHeight() {
        return mCanvas.getHeight();
    }

    public int getDensity() {
        return mCanvas.getDensity();
    }

    public void setDensity(int density) {
        mCanvas.setDensity(density);
    }

    public int getMaximumBitmapWidth() {
        return mCanvas.getMaximumBitmapWidth();
    }

    public int getMaximumBitmapHeight() {
        return mCanvas.getMaximumBitmapHeight();
    }

    public int save() {
        return mCanvas.save();
    }

    public int saveLayer(@Nullable RectF bounds, @Nullable Paint paint, int saveFlags) {
        return mCanvas.saveLayer(bounds, paint, saveFlags);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int saveLayer(@Nullable RectF bounds, @Nullable Paint paint) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return mCanvas.saveLayer(bounds, paint);
    }

    public int saveLayer(float left, float top, float right, float bottom, @Nullable Paint paint, int saveFlags) {
        return mCanvas.saveLayer(left, top, right, bottom, paint, saveFlags);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int saveLayer(float left, float top, float right, float bottom, @Nullable Paint paint) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return mCanvas.saveLayer(left, top, right, bottom, paint);
    }

    public int saveLayerAlpha(@Nullable RectF bounds, int alpha, int saveFlags) {
        return mCanvas.saveLayerAlpha(bounds, alpha, saveFlags);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int saveLayerAlpha(@Nullable RectF bounds, int alpha) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return mCanvas.saveLayerAlpha(bounds, alpha);
    }

    public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha, int saveFlags) {
        return mCanvas.saveLayerAlpha(left, top, right, bottom, alpha, saveFlags);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int saveLayerAlpha(float left, float top, float right, float bottom, int alpha) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        return mCanvas.saveLayerAlpha(left, top, right, bottom, alpha);
    }

    public void restore() {
        mCanvas.restore();
    }

    public int getSaveCount() {
        return mCanvas.getSaveCount();
    }

    public void restoreToCount(int saveCount) {
        mCanvas.restoreToCount(saveCount);
    }

    public void translate(float dx, float dy) {
        mCanvas.translate(dx, dy);
    }

    public void scale(float sx, float sy) {
        mCanvas.scale(sx, sy);
    }

    public void scale(float sx, float sy, float px, float py) {
        mCanvas.scale(sx, sy, px, py);
    }

    public void rotate(float degrees) {
        mCanvas.rotate(degrees);
    }

    public void rotate(float degrees, float px, float py) {
        mCanvas.rotate(degrees, px, py);
    }

    public void skew(float sx, float sy) {
        mCanvas.skew(sx, sy);
    }

    public void concat(@Nullable Matrix matrix) {
        mCanvas.concat(matrix);
    }

    public void setMatrix(@Nullable Matrix matrix) {
        mCanvas.setMatrix(matrix);
    }

    @Deprecated
    public void getMatrix(@NonNull Matrix ctm) {
        mCanvas.getMatrix(ctm);
    }

    @Deprecated
    public Matrix getMatrix() {
        return mCanvas.getMatrix();
    }

    public boolean clipRect(@NonNull RectF rect, @NonNull Region.Op op) {
        return mCanvas.clipRect(rect, op);
    }

    public boolean clipRect(@NonNull Rect rect, @NonNull Region.Op op) {
        return mCanvas.clipRect(rect, op);
    }

    public boolean clipRect(@NonNull RectF rect) {
        return mCanvas.clipRect(rect);
    }

    public boolean clipRect(@NonNull Rect rect) {
        return mCanvas.clipRect(rect);
    }

    public boolean clipRect(float left, float top, float right, float bottom, @NonNull Region.Op op) {
        return mCanvas.clipRect(left, top, right, bottom, op);
    }

    public boolean clipRect(float left, float top, float right, float bottom) {
        return mCanvas.clipRect(left, top, right, bottom);
    }

    public boolean clipRect(int left, int top, int right, int bottom) {
        return mCanvas.clipRect(left, top, right, bottom);
    }

    public boolean clipPath(@NonNull Path path, @NonNull Region.Op op) {
        return mCanvas.clipPath(path, op);
    }

    public boolean clipPath(@NonNull Path path) {
        return mCanvas.clipPath(path);
    }

    public DrawFilter getDrawFilter() {
        return mCanvas.getDrawFilter();
    }

    public void setDrawFilter(@Nullable DrawFilter filter) {
        mCanvas.setDrawFilter(filter);
    }

    public boolean quickReject(@NonNull RectF rect, @NonNull Canvas.EdgeType type) {
        return mCanvas.quickReject(rect, type);
    }

    public boolean quickReject(@NonNull Path path, @NonNull Canvas.EdgeType type) {
        return mCanvas.quickReject(path, type);
    }

    public boolean quickReject(float left, float top, float right, float bottom, @NonNull Canvas.EdgeType type) {
        return mCanvas.quickReject(left, top, right, bottom, type);
    }

    public boolean getClipBounds(@Nullable Rect bounds) {
        return mCanvas.getClipBounds(bounds);
    }

    public Rect getClipBounds() {
        return mCanvas.getClipBounds();
    }

    public void drawRGB(int r, int g, int b) {
        mCanvas.drawRGB(r, g, b);
    }

    public void drawARGB(int a, int r, int g, int b) {
        mCanvas.drawARGB(a, r, g, b);
    }

    public void drawColor(int color) {
        mCanvas.drawColor(color);
    }

    public void drawColor(int color, @NonNull PorterDuff.Mode mode) {
        mCanvas.drawColor(color, mode);
    }

    public void drawPaint(@NonNull Paint paint) {
        mCanvas.drawPaint(paint);
    }

    public void drawPoints(float[] pts, int offset, int count, @NonNull Paint paint) {
        mCanvas.drawPoints(pts, offset, count, paint);
    }

    public void drawPoints(@NonNull float[] pts, @NonNull Paint paint) {
        mCanvas.drawPoints(pts, paint);
    }

    public void drawPoint(float x, float y, @NonNull Paint paint) {
        mCanvas.drawPoint(x, y, paint);
    }

    public void drawLine(float startX, float startY, float stopX, float stopY, @NonNull Paint paint) {
        mCanvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    public void drawLines(@NonNull float[] pts, int offset, int count, @NonNull Paint paint) {
        mCanvas.drawLines(pts, offset, count, paint);
    }

    public void drawLines(@NonNull float[] pts, @NonNull Paint paint) {
        mCanvas.drawLines(pts, paint);
    }

    public void drawRect(@NonNull RectF rect, @NonNull Paint paint) {
        mCanvas.drawRect(rect, paint);
    }

    public void drawRect(@NonNull Rect r, @NonNull Paint paint) {
        mCanvas.drawRect(r, paint);
    }

    public void drawRect(float left, float top, float right, float bottom, @NonNull Paint paint) {
        mCanvas.drawRect(left, top, right, bottom, paint);
    }

    public void drawOval(@NonNull RectF oval, @NonNull Paint paint) {
        mCanvas.drawOval(oval, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void drawOval(float left, float top, float right, float bottom, @NonNull Paint paint) {
        mCanvas.drawOval(left, top, right, bottom, paint);
    }

    public void drawCircle(float cx, float cy, float radius, @NonNull Paint paint) {
        mCanvas.drawCircle(cx, cy, radius, paint);
    }

    public void drawArc(@NonNull RectF oval, float startAngle, float sweepAngle, boolean useCenter, @NonNull Paint paint) {
        mCanvas.drawArc(oval, startAngle, sweepAngle, useCenter, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void drawArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle, boolean useCenter, @NonNull Paint paint) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        mCanvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint);
    }


    public void drawRoundRect(@NonNull RectF rect, float rx, float ry, @NonNull Paint paint) {
        mCanvas.drawRoundRect(rect, rx, ry, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void drawRoundRect(float left, float top, float right, float bottom, float rx, float ry, @NonNull Paint paint) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.LOLLIPOP);
        mCanvas.drawRoundRect(left, top, right, bottom, rx, ry, paint);
    }

    public void drawPath(@NonNull Path path, @NonNull Paint paint) {
        mCanvas.drawPath(path, paint);
    }

    public void drawBitmap(@NonNull Bitmap bitmap, float left, float top, @Nullable Paint paint) {
        mCanvas.drawBitmap(bitmap, left, top, paint);
    }

    public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull RectF dst, @Nullable Paint paint) {
        mCanvas.drawBitmap(bitmap, src, dst, paint);
    }

    public void drawBitmap(@NonNull Bitmap bitmap, @Nullable Rect src, @NonNull Rect dst, @Nullable Paint paint) {
        mCanvas.drawBitmap(bitmap, src, dst, paint);
    }

    @Deprecated
    public void drawBitmap(@NonNull int[] colors, int offset, int stride, float x, float y, int width, int height, boolean hasAlpha, @Nullable Paint paint) {
        mCanvas.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
    }

    @Deprecated
    public void drawBitmap(@NonNull int[] colors, int offset, int stride, int x, int y, int width, int height, boolean hasAlpha, @Nullable Paint paint) {
        mCanvas.drawBitmap(colors, offset, stride, x, y, width, height, hasAlpha, paint);
    }

    public void drawBitmap(@NonNull Bitmap bitmap, @NonNull Matrix matrix, @Nullable Paint paint) {
        mCanvas.drawBitmap(bitmap, matrix, paint);
    }

    public void drawBitmapMesh(@NonNull Bitmap bitmap, int meshWidth, int meshHeight, @NonNull float[] verts, int vertOffset, @Nullable int[] colors, int colorOffset, @Nullable Paint paint) {
        mCanvas.drawBitmapMesh(bitmap, meshWidth, meshHeight, verts, vertOffset, colors, colorOffset, paint);
    }

    public void drawVertices(@NonNull Canvas.VertexMode mode, int vertexCount, @NonNull float[] verts, int vertOffset, @Nullable float[] texs, int texOffset, @Nullable int[] colors, int colorOffset, @Nullable short[] indices, int indexOffset, int indexCount, @NonNull Paint paint) {
        mCanvas.drawVertices(mode, vertexCount, verts, vertOffset, texs, texOffset, colors, colorOffset, indices, indexOffset, indexCount, paint);
    }

    public void drawText(@NonNull char[] text, int index, int count, float x, float y, @NonNull Paint paint) {
        mCanvas.drawText(text, index, count, x, y, paint);
    }

    public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint) {
        mCanvas.drawText(text, x, y, paint);
    }

    public void drawText(@NonNull String text, int start, int end, float x, float y, @NonNull Paint paint) {
        mCanvas.drawText(text, start, end, x, y, paint);
    }

    public void drawText(@NonNull CharSequence text, int start, int end, float x, float y, @NonNull Paint paint) {
        mCanvas.drawText(text, start, end, x, y, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void drawTextRun(@NonNull char[] text, int index, int count, int contextIndex, int contextCount, float x, float y, boolean isRtl, @NonNull Paint paint) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.M);
        mCanvas.drawTextRun(text, index, count, contextIndex, contextCount, x, y, isRtl, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void drawTextRun(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, float x, float y, boolean isRtl, @NonNull Paint paint) {
        ScriptRuntime.requiresApi(Build.VERSION_CODES.M);
        mCanvas.drawTextRun(text, start, end, contextStart, contextEnd, x, y, isRtl, paint);
    }

    @Deprecated
    public void drawPosText(@NonNull char[] text, int index, int count, @NonNull float[] pos, @NonNull Paint paint) {
        mCanvas.drawPosText(text, index, count, pos, paint);
    }

    @Deprecated
    public void drawPosText(@NonNull String text, @NonNull float[] pos, @NonNull Paint paint) {
        mCanvas.drawPosText(text, pos, paint);
    }

    public void drawTextOnPath(@NonNull char[] text, int index, int count, @NonNull Path path, float hOffset, float vOffset, @NonNull Paint paint) {
        mCanvas.drawTextOnPath(text, index, count, path, hOffset, vOffset, paint);
    }

    public void drawTextOnPath(@NonNull String text, @NonNull Path path, float hOffset, float vOffset, @NonNull Paint paint) {
        mCanvas.drawTextOnPath(text, path, hOffset, vOffset, paint);
    }

    public void drawPicture(@NonNull Picture picture) {
        mCanvas.drawPicture(picture);
    }

    public void drawPicture(@NonNull Picture picture, @NonNull RectF dst) {
        mCanvas.drawPicture(picture, dst);
    }

    public void drawPicture(@NonNull Picture picture, @NonNull Rect dst) {
        mCanvas.drawPicture(picture, dst);
    }

    public void drawImage(@NonNull ImageWrapper image, float left, float top, @Nullable Paint paint) {
        mCanvas.drawBitmap(image.getBitmap(), left, top, paint);
    }

    public void drawImage(@NonNull ImageWrapper image, float left, float top, float width, float height, @Nullable Paint paint) {
        mCanvas.drawBitmap(image.getBitmap(), null, new RectF(left, top, left + width, top + height), paint);
    }

    public void drawImage(@NonNull ImageWrapper image, int sx, int sy, int swidth, int sheight, float left, float top, float width, float height, @Nullable Paint paint) {
        mCanvas.drawBitmap(image.getBitmap(), new Rect(sx, sy, sx + swidth, sy + sheight),
                new RectF(left, top, left + width, top + height), paint);
    }

    public void drawImage(@NonNull ImageWrapper image, @Nullable Rect src, @NonNull RectF dst, @Nullable Paint paint) {
        mCanvas.drawBitmap(image.getBitmap(), src, dst, paint);
    }

    public void drawImage(@NonNull ImageWrapper image, @Nullable Rect src, @NonNull Rect dst, @Nullable Paint paint) {
        mCanvas.drawBitmap(image.getBitmap(), src, dst, paint);
    }

    public void drawImage(@NonNull ImageWrapper image, @NonNull Matrix matrix, @Nullable Paint paint) {
        mCanvas.drawBitmap(image.getBitmap(), matrix, paint);
    }
}
