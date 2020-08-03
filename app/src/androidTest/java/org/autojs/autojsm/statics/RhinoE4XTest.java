package org.autojs.autojs.statics;

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Created by Stardust on 2017/5/13.
 */

public class RhinoE4XTest {

    @Test
    public void testAttributeName() {
        Context context = Context.enter();
        Scriptable scriptable = context.initStandardObjects();
        context.setOptimizationLevel(-1);
        Object o = context.evaluateString(scriptable, "XML.ignoreProcessingInstructions = true; (<xml id=\"foo\"></xml>).attributes()[0].name()", "<e4x>", 1, null);
        System.out.println(o);
    }
}
