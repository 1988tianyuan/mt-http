package com.liugeng.mthttp.utils.asm;

import com.google.common.collect.Sets;

import java.util.LinkedHashMap;
import java.util.Set;

public class AnnotationAttributes extends LinkedHashMap<String, Set<String>> {

    public void putMulti(String key, String value) {
        Set<String> valueSet;
        if (containsKey(key)) {
            valueSet = get(key);
            valueSet.add(value);
        } else {
            valueSet = Sets.newHashSet(value);
            put(key, valueSet);
        }
    }

}
