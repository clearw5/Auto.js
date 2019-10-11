package com.stardust.autojs.core.internal;

/**
 * Created by Stardust on 2017/7/19.
 */

public class Functions {

    @SuppressWarnings("unchecked")
    public static Object call(Func func, Object[] args) {
        if (func instanceof Func0) {
            return ((Func0) func).call();
        } else if (func instanceof Func1) {
            return ((Func1) func).call(args[0]);
        } else if (func instanceof Func2) {
            return ((Func2) func).call(args[0], args[1]);
        } else if (func instanceof Func3) {
            return ((Func3) func).call(args[0], args[1], args[2]);
        } else if (func instanceof Func4) {
            return ((Func4) func).call(args[0], args[1], args[2], args[3]);
        }
        throw new IllegalArgumentException("Unknown func: " + func);
    }

    public interface Func {

    }

    public interface Func0<R> extends Func {
        R call();
    }

    public interface Func1<T1, R> extends Func {
        R call(T1 t1);
    }

    public interface Func2<T1, T2, R> extends Func {
        R call(T1 t1, T2 t2);
    }

    public interface Func3<T1, T2, T3, R> extends Func {
        R call(T1 t1, T2 t2, T3 t3);
    }

    public interface Func4<T1, T2, T3, T4, R> extends Func {
        R call(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    public interface VoidFunc1<T1> extends Func {
        void call(T1 t1);
    }

    public interface VoidFunc2<T1, T2> extends Func {
        void call(T1 t1, T2 t2);
    }

    public interface VoidFunc3<T1, T2, T3> extends Func {
        void call(T1 t1, T2 t2, T3 t3);
    }

    public interface VoidFunc4<T1, T2, T3, T4> extends Func {
        void call(T1 t1, T2 t2, T3 t3, T4 t4);
    }


}
