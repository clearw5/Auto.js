package com.stardust.mi666;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stardust on 2017/4/24.
 */

public class Solver {

    private boolean mSolved = false;
    private List<Integer> mResult = new ArrayList<>();
    private List<Integer> mTmpList = new ArrayList<>();
    private int[] value;


    void solve(int sum, int index) {
        if (mSolved || index < 0) return;
        if (sum == value[index] && mTmpList.size() == 5) {
            mSolved = true;
            mTmpList.add(value[index]);
            mResult.addAll(mTmpList);
            return;
        }
        mTmpList.add(value[index]);
        solve(sum - value[index], index - 1);
        mTmpList.remove(mTmpList.size() - 1);
        solve(sum, index - 1);
    }

    public void solve(int[] num) {
        mResult.clear();
        mTmpList.clear();
        mSolved = false;
        value = num;
        solve(6666, 15);
    }

    public boolean isSolved() {
        return mSolved;
    }

    public List<Integer> getResult() {
        return mResult;
    }

    public void solve(List<Integer> numbers) {
        int[] num = new int[numbers.size()];
        for (int i = 0; i < num.length; i++) {
            num[i] = numbers.get(i);
        }
        solve(num);
    }
}
