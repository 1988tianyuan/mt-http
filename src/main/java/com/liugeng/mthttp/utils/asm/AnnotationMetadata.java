package com.liugeng.mthttp.utils.asm;

import java.util.Set;

public interface AnnotationMetadata extends ClassMetadata {

    boolean hasAnnotation(String annotation);

    Set<String> getAnnotationTypes();

    AnnotationAttributes getAnnotationAttributes(String annotationName);
}
