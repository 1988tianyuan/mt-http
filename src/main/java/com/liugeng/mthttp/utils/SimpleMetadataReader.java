package com.liugeng.mthttp.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.liugeng.mthttp.utils.asm.AnnotationMetadata;
import com.liugeng.mthttp.utils.asm.AnnotationMetadataReadingVisitor;
import com.liugeng.mthttp.utils.asm.ClassMetaDataReadingVisitor;
import com.liugeng.mthttp.utils.asm.ClassMetadata;
import com.liugeng.mthttp.utils.asm.ClassMethodMetadata;
import com.liugeng.mthttp.utils.io.Resource;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;

/**
 * read Class info by asm tools
 */
public class SimpleMetadataReader implements MetadataReader {

    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;
    private final ClassMethodMetadata classMethodMetadata;

    public SimpleMetadataReader(Resource resource) throws IOException{
        InputStream inputStream = new BufferedInputStream(resource.getInputStream());
        ClassReader classReader;
        try {
            // retrieve class binary code by ClassReader
            classReader = new ClassReader(inputStream);
        } finally {
            inputStream.close();
        }
        ClassMetaDataReadingVisitor visitor = new ClassMetaDataReadingVisitor();
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);
        this.annotationMetadata = visitor.getAnnotationMetadata();
        this.classMetadata = visitor;
        this.classMethodMetadata = visitor.getClassMethodMetadata();
        this.resource = resource;
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public ClassMetadata getClassMetadata() {
        return classMetadata;
    }

    @Override
    public AnnotationMetadata getAnnotationMetadata() {
        return annotationMetadata;
    }

    @Override
    public ClassMethodMetadata getClassMethodMetadata() {
        return classMethodMetadata;
    }
}
