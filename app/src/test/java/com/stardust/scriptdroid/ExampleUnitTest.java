package com.stardust.scriptdroid;

import com.jecelyin.common.http.Base64;
import com.stardust.util.LimitedHashMap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    // TODO: 2017/3/3 自定义函数
    // TODO: 2017/3/19 exist函数 √
    // TODO: 2017/3/23 tasker插件 √
    // TODO: 2017/3/23 任务管理与控制台 √
    // TODO: 2017/3/23 悬浮窗加入控制台
    // TODO: 2017/3/24 文件读写api
    // TODO: 2017/3/24 网络读写api
    // TODO: 2017/3/24 常驻后台api
    // TODO: 2017/3/24 ui。E4x
    // TODO: 2017/3/24  编辑界面文档和自动补全
    // TODO: 2017/3/24 驻留模式
    //// TODO: 2017/3/26 NODEJS
    // TODO: 2017/3/31 自定义快捷方式图标


    // FIXME: 2017/3/23 死机重启问题


    @Test
    public void test() {
        LimitedHashMap<String, Integer> hashMap = new LimitedHashMap<>(5);
        hashMap.put("a", 1);
        hashMap.put("b", 1);
        hashMap.put("c", 1);
        hashMap.put("d", 1);
        hashMap.put("e", 1);
        hashMap.get("a");
        hashMap.put("f", 1);
        assertFalse(hashMap.containsKey("a"));
    }

    @Test
    public void testAutoReorder() {
        LimitedHashMap<String, Integer> hashMap = new LimitedHashMap<>(5);
        hashMap.put("a", 1);
        hashMap.put("b", 2);
        hashMap.put("c", 3);
        hashMap.put("d", 4);
        hashMap.put("e", 5);
        hashMap.get("a");
        hashMap.put("f", 6);
        assertTrue(hashMap.containsKey("a"));
        assertFalse(hashMap.containsKey("b"));
    }


}