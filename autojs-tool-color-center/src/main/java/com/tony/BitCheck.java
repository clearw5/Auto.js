package com.tony;

/**
 * @author TonyJiang 2019/12/6
 */
public class BitCheck {
    private int BUFFER_LENGTH;
    private final int BYTE_SIZE = 1 << 3;
    private byte[] bytes;

    public BitCheck(long maxVal) {
        if (maxVal > (long)BYTE_SIZE * Integer.MAX_VALUE) {
            throw new IllegalArgumentException("初始值超过允许的最大值");
        }
        this.BUFFER_LENGTH = (int)Math.ceil((double)maxVal / BYTE_SIZE) + 1;
        this.bytes = new byte[this.BUFFER_LENGTH];
    }

    /**
     * eg.
     * start bytes[idx]=0
     * check 6, pos: 1<<6=64 bytes[idx]: 0
     * bytes[idx] & pos = 0, unset
     * then. bytes[idx] = bytes[idx] | pos ,=2^6(01000000)
     * <p/>
     * check 7, pos: 1<<7=128 bytes[idx]: 64(01000000)
     * bytes[idx] & pos = 0, unset
     * then. bytes[idx] = bytes[idx] | pos, =192(11000000)
     */
    public boolean isUnchecked(long val) {
        // 将待校验值截取 高位 作为位序列索引
        int idx = (int)(val >> 3);

        // 低7位作为待校验值
        byte position = (byte)(1 << (val & (BYTE_SIZE - 1)));
        // 比较byte值对应位上是否为1，即与操作后是否相等
        boolean unset = (this.bytes[idx] & position) != position;
        // 将对应位设为1
        this.bytes[idx] = (byte)(this.bytes[idx] | position);
        return unset;
    }

}
