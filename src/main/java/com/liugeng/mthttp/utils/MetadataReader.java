package com.liugeng.mthttp.utils;

import com.liugeng.mthttp.utils.asm.AnnotationMetadata;
import com.liugeng.mthttp.utils.asm.ClassMetadata;
import com.liugeng.mthttp.utils.io.Resource;

public interface MetadataReader {
    Resource getResource();

    ClassMetadata getClassMetadata();

    AnnotationMetadata getAnnotationMetadata();
}
