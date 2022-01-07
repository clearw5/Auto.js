package com.tony.resolver;

/**
 * 可以在JavaScript中扩展 默认实现是 GSON 解析的 DefaultGSONResolver @see com.tony.resolver.DefaultGSONResolver
 */
public interface JSONResolver {

    /**
     * 将对象转换成 JSON字符串
     *
     * @param obj
     * @return jsonString
     */
    String toJSONString(Object obj);

    /**
     * 根据json字符串获取 指定json key内容 并转为String
     *
     * @param jsonString
     * @param name       key
     * @return
     */
    String getString(String jsonString, String name);

    /**
     * 可以嵌套调用 获取对象，不转为String
     *
     * @param jsonString
     * @param name
     * @return
     */
    Object getObject(String jsonString, String name);

    //---------------

    /**
     * 设置原始JSONString
     *
     * @param jsonString
     * @return
     */
    JSONResolver setOrigin(String jsonString);

    String getString(String name);

    Object getObject(String name);

    //---------------

    /**
     * 创建新的封装 内部new一个Map 创建JSON对象
     *
     * @return
     */
    JSONResolver newObject();

    JSONResolver put(String name, Object value);

    /**
     * 将创建的JSON对象转换成字符串
     *
     * @return
     */
    String toJSONString();
}
