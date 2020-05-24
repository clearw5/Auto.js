package org.mozilla.javascript;

import org.mozilla.javascript.debug.DebuggableScript;
import org.mozilla.javascript.xml.XMLLib;

public class ContextWrapper extends Context {
    private final Context mContext;

    public ContextWrapper(Context context) {
        mContext = context;
    }

    public static Context getCurrentContext() {
        return Context.getCurrentContext();
    }

    public static Context enter() {
        return Context.enter();
    }

    @Deprecated
    public static Context enter(Context cx) {
        return Context.enter(cx);
    }

    public static void exit() {
        Context.exit();
    }

    @Deprecated
    public static Object call(ContextAction action) {
        return Context.call(action);
    }

    public static Object call(ContextFactory factory, Callable callable, Scriptable scope, Scriptable thisObj, Object[] args) {
        return Context.call(factory, callable, scope, thisObj, args);
    }

    @Deprecated
    public static void addContextListener(ContextListener listener) {
        Context.addContextListener(listener);
    }

    @Deprecated
    public static void removeContextListener(ContextListener listener) {
        Context.removeContextListener(listener);
    }

    @Override
    public void setLanguageVersion(int version) {
        mContext.setLanguageVersion(version);
    }

    public static boolean isValidLanguageVersion(int version) {
        return Context.isValidLanguageVersion(version);
    }

    public static void checkLanguageVersion(int version) {
        Context.checkLanguageVersion(version);
    }

    public static void reportWarning(String message, String sourceName, int lineno, String lineSource, int lineOffset) {
        Context.reportWarning(message, sourceName, lineno, lineSource, lineOffset);
    }

    public static void reportWarning(String message) {
        Context.reportWarning(message);
    }

    public static void reportWarning(String message, Throwable t) {
        Context.reportWarning(message, t);
    }

    public static void reportError(String message, String sourceName, int lineno, String lineSource, int lineOffset) {
        Context.reportError(message, sourceName, lineno, lineSource, lineOffset);
    }

    public static void reportError(String message) {
        Context.reportError(message);
    }

    public static EvaluatorException reportRuntimeError(String message, String sourceName, int lineno, String lineSource, int lineOffset) {
        return Context.reportRuntimeError(message, sourceName, lineno, lineSource, lineOffset);
    }

    public static EvaluatorException reportRuntimeError(String message) {
        return Context.reportRuntimeError(message);
    }

    @Override
    public ScriptableObject initStandardObjects(ScriptableObject scope, boolean sealed) {
        return mContext.initStandardObjects(scope, sealed);
    }

    @Override
    public ScriptableObject initSafeStandardObjects(ScriptableObject scope, boolean sealed) {
        return mContext.initSafeStandardObjects(scope, sealed);
    }

    public static Object getUndefinedValue() {
        return Context.getUndefinedValue();
    }

    @Override
    public Object executeScriptWithContinuations(Script script, Scriptable scope) throws ContinuationPending {
        return mContext.executeScriptWithContinuations(script, scope);
    }

    @Override
    public Object callFunctionWithContinuations(Callable function, Scriptable scope, Object[] args) throws ContinuationPending {
        return mContext.callFunctionWithContinuations(function, scope, args);
    }

    @Override
    public ContinuationPending captureContinuation() {
        return mContext.captureContinuation();
    }

    @Override
    public Object resumeContinuation(Object continuation, Scriptable scope, Object functionResult) throws ContinuationPending {
        return mContext.resumeContinuation(continuation, scope, functionResult);
    }

    @Override
    public Scriptable newObject(Scriptable scope) {
        return mContext.newObject(scope);
    }

    @Override
    public Scriptable newObject(Scriptable scope, String constructorName) {
        return mContext.newObject(scope, constructorName);
    }

    @Override
    public Scriptable newObject(Scriptable scope, String constructorName, Object[] args) {
        return mContext.newObject(scope, constructorName, args);
    }

    @Override
    public Scriptable newArray(Scriptable scope, int length) {
        return mContext.newArray(scope, length);
    }

    @Override
    public Scriptable newArray(Scriptable scope, Object[] elements) {
        return mContext.newArray(scope, elements);
    }

    public static boolean toBoolean(Object value) {
        return Context.toBoolean(value);
    }

    public static double toNumber(Object value) {
        return Context.toNumber(value);
    }

    public static String toString(Object value) {
        return Context.toString(value);
    }

    public static Scriptable toObject(Object value, Scriptable scope) {
        return Context.toObject(value, scope);
    }

    @Deprecated
    public static Scriptable toObject(Object value, Scriptable scope, Class<?> staticType) {
        return Context.toObject(value, scope, staticType);
    }

    public static Object javaToJS(Object value, Scriptable scope) {
        return Context.javaToJS(value, scope);
    }

    public static Object jsToJava(Object value, Class<?> desiredType) throws EvaluatorException {
        return Context.jsToJava(value, desiredType);
    }

    @Deprecated
    public static Object toType(Object value, Class<?> desiredType) throws IllegalArgumentException {
        return Context.toType(value, desiredType);
    }

    public static RuntimeException throwAsScriptRuntimeEx(Throwable e) {
        return Context.throwAsScriptRuntimeEx(e);
    }

    public static boolean isValidOptimizationLevel(int optimizationLevel) {
        return Context.isValidOptimizationLevel(optimizationLevel);
    }

    public static void checkOptimizationLevel(int optimizationLevel) {
        Context.checkOptimizationLevel(optimizationLevel);
    }

    @Deprecated
    public static void setCachingEnabled(boolean cachingEnabled) {
        Context.setCachingEnabled(cachingEnabled);
    }

    public static DebuggableScript getDebuggableView(Script script) {
        return Context.getDebuggableView(script);
    }

    @Override
    public boolean hasFeature(int featureIndex) {
        return mContext.hasFeature(featureIndex);
    }

    @Override
    public XMLLib.Factory getE4xImplementationFactory() {
        return mContext.getE4xImplementationFactory();
    }

    @Override
    public void setGenerateObserverCount(boolean generateObserverCount) {
        mContext.setGenerateObserverCount(generateObserverCount);
    }

    @Override
    public void observeInstructionCount(int instructionCount) {
        mContext.observeInstructionCount(instructionCount);
    }

    @Override
    public GeneratedClassLoader createClassLoader(ClassLoader parent) {
        return mContext.createClassLoader(parent);
    }

    @Override
    public void addActivationName(String name) {
        mContext.addActivationName(name);
    }

    @Override
    public void removeActivationName(String name) {
        mContext.removeActivationName(name);
    }
}
