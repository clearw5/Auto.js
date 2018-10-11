package com.stardust.util;

import java.util.Map;
import java.util.Set;

public interface BiMap<K, V> extends Map<K, V> {

    K getKey(V value);

    Set<V> valueSet();
}
