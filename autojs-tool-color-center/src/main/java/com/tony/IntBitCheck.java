package com.tony;

/**
 * @author TonyJiang 2020/3/30
 */
public class IntBitCheck {
    private final int BYTE_SIZE = 1 << 5;
    private int[] ints;

    public IntBitCheck(long maxVal) {
        if (maxVal > (long) BYTE_SIZE * Integer.MAX_VALUE) {
            throw new IllegalArgumentException("初始值超过允许的最大值");
        }
        this.ints = new int[(int) Math.ceil((double) maxVal / BYTE_SIZE) + 1];
    }

    public boolean isUnchecked(long val) {
        // 将待校验值截取 高位 作为位序列索引
        int idx = (int) (val >> 5);

        // 低7位作为待校验值
        int position = 1 << (val & (BYTE_SIZE - 1));
        // 比较byte值对应位上是否为1，即与操作后是否相等
        boolean unset = (this.ints[idx] & position) != position;
        // 将对应位设为1
        this.ints[idx] = this.ints[idx] | position;
        return unset;
    }

}
