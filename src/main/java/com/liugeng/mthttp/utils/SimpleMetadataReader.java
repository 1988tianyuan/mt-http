package com.liugeng.mthttp.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.liugeng.mthttp.utils.asm.AnnotationMetadata;
import com.liugeng.mthttp.utils.asm.AnnotationMetadataReadingVisitor;
import com.liugeng.mthttp.utils.asm.ClassMetadata;
import com.liugeng.mthttp.utils.io.Resource;
import org.objectweb.asm.ClassReader;

/**
 * read Class info by asm tools
 */
public class SimpleMetadataReader implements MetadataReader {

    private final Resource resource;
    private final ClassMetadata classMetadata;
    private final AnnotationMetadata annotationMetadata;

    public SimpleMetadataReader(Resource resource) throws IOException{
        InputStream inputStream = new BufferedInputStream(resource.getInputStream());
        ClassReader classReader;
        try {
            // retrieve class binary code by ClassReader
            classReader = new ClassReader(inputStream);
        } finally {
            inputStream.close();
        }
        AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor();
        classReader.accept(visitor, ClassReader.SKIP_DEBUG);
        this.annotationMetadata = visitor;
        this.classMetadata = visitor;
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
}
