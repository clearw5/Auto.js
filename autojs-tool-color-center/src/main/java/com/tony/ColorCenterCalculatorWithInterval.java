package com.tony;

import android.util.Log;

import com.stardust.autojs.core.image.ImageWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * 计算颜色值的中心点位置，原始图像在外部二值化
 */
public class ColorCenterCalculatorWithInterval {
    private ImageWrapper img;
    private BitCheck checker;
    private int startX;
    private int offset;
    private int width;
    private int height;
    private Point point;
    private boolean useBfs = false;
    private ScriptLogger scriptLogger;

    public ColorCenterCalculatorWithInterval(ImageWrapper intervalImg, int startX, int x, int y) {
        this.img = intervalImg;
        width = img.getWidth();
        height = img.getHeight();
        int v = width - startX;
        int o = 0;
        while (v >> 1 > 0) {
            o++;
            v >>= 1;
        }
        this.offset = o + 1;
        this.point = new Point(x, y);
        checker = new BitCheck(height << this.offset | (width - startX));
        this.startX = startX;
    }

    public ImageWrapper getImg() {
        return this.img;
    }

    protected String getLogTag() {
        return "ColorCenterCalculatorWithInterval";
    }

    public Point getCenterPoint() {
        try {
            logD(getLogTag(), "准备获取所有相邻点");
            List<Point> allPoints = this.getAllNearlyPoints();
            logD(getLogTag(), "获取所有相邻点个数：" + allPoints.size());
            double minX = width + 1, maxX = -1, minY = height + 1, maxY = -1;
            for (Point point : allPoints) {
                if (minX > point.x) {
                    minX = point.x;
                }
                if (minY > point.y) {
                    minY = point.y;
                }
                if (maxX < point.x) {
                    maxX = point.x;
                }
                if (maxY < point.y) {
                    maxY = point.y;
                }
            }
            Point resultPoint = new Point((int) ((maxX + minX) / 2), (int) ((maxY + minY) / 2));
            resultPoint.setBottom((int) maxY);
            resultPoint.setTop((int) minY);
            resultPoint.setLeft((int) minX);
            resultPoint.setRight((int) maxX);
            resultPoint.setSame(allPoints.size());
            int count = 0;
            for (int x = (int) minX; x <= maxX; x++) {
                for (int y = (int) minY; y <= maxY; y++) {
                    if ((this.img.getBitmap().getPixel(x, y) & 0xFFFFFF) == 0xFFFFFF) {
                        count++;
                    }
                }
            }
            resultPoint.setRegionSame(count);
            return resultPoint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isUnchecked(Point point) {
        return this.checker.isUnchecked((int) point.y << offset | (int) (point.x - startX));
    }

    private boolean isOutOfRange(Point point) {
        return point.x < startX || point.x >= width || point.y < 0 || point.y >= height;
    }

    private Point getUncheckedPoint(Point current, int[] direction) {
        Point next = new Point(current.x + direction[0], current.y + direction[1]);
        if (this.isOutOfRange(next) || !this.isUnchecked(next)) {
            return null;
        }
        return next;
    }

    private List<Point> getNearlyPointsDfs(final Point point) {
        int[][] directions = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        Stack<Point> stack = new Stack<>();
        stack.push(point);
        this.isUnchecked(point);
        List<Point> nearlyPoints = new ArrayList<Point>() {{
            add(point);
        }};
        int step = 0;
        long start = new Date().getTime();
        while (!stack.isEmpty()) {
            Point target = stack.peek();
            boolean allChecked = true;
            for (int[] direct : directions) {
                Point checkItem = this.getUncheckedPoint(target, direct);
                if (checkItem == null) {
                    continue;
                }
                step++;
                allChecked = false;
                if ((this.img.getBitmap().getPixel((int) checkItem.x, (int) checkItem.y) & 0xFFFFFF) == 0xFFFFFF) {
                    nearlyPoints.add(checkItem);
                    stack.push(checkItem);
                }
            }
            if (allChecked) {
                stack.pop();
            }
        }
        // TODO LOG
        logD(getLogTag(), "DFS 总计执行：" + step + " 耗时：" + (new Date().getTime() - start) + "ms");
        return nearlyPoints;
    }

    private List<Point> getNearlyPointsBfs(final Point point) {
        int[][] directions = new int[][]{{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        LinkedList<Point> queue = new LinkedList<>();
        queue.offer(point);
        this.isUnchecked(point);
        List<Point> nearlyPoints = new ArrayList<Point>() {{
            add(point);
        }};
        int step = 0;
        long start = new Date().getTime();
        while (!queue.isEmpty()) {
            Point target = queue.poll();
            for (int[] direct : directions) {
                Point checkItem = this.getUncheckedPoint(target, direct);
                if (checkItem == null) {
                    continue;
                }
                step++;
                if ((this.img.getBitmap().getPixel((int) checkItem.x, (int) checkItem.y) & 0xFFFFFF) == 0xFFFFFF) {
                    nearlyPoints.add(checkItem);
                    queue.offer(checkItem);
                }
            }
        }
        // TODO LOG
        logD(getLogTag(), "BFS 总计执行：" + step + " 耗时：" + (new Date().getTime() - start) + "ms");
        return nearlyPoints;
    }


    private List<Point> getAllNearlyPoints() {
        if (this.isUseBfs()) {
            logD(getLogTag(), "使用BFS计算中心点");
            return this.getNearlyPointsBfs(this.point);
        } else {
            logD(getLogTag(), "使用DFS计算中心点");
            return this.getNearlyPointsDfs(this.point);
        }
    }


    public boolean isUseBfs() {
        return useBfs;
    }

    public void setUseBfs(boolean useBfs) {
        this.useBfs = useBfs;
    }

    protected void logD(String logTag, String message) {
        Log.d(logTag, message);
        if (this.scriptLogger != null) {
            this.scriptLogger.debug(message);
        }
    }

    protected void logE(String logTag, String message) {
        Log.e(logTag, message);
        if (this.scriptLogger != null) {
            this.scriptLogger.error(message);
        }
    }
    public void setScriptLogger(ScriptLogger scriptLogger) {
        this.scriptLogger = scriptLogger;
    }
}
