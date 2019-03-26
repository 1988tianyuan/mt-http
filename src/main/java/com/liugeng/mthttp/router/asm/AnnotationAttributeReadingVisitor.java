package com.liugeng.mthttp.router.asm;

import java.util.Map;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public class AnnotationAttributeReadingVisitor extends AnnotationVisitor {

    private final String annotationType;
    private final Map<String, AnnotationAttributes> attributesMap;
    private final AnnotationAttributes attributes = new AnnotationAttributes();

    public AnnotationAttributeReadingVisitor(String annotationType, Map<String, AnnotationAttributes> attributesMap){
        super(262144);
        this.annotationType = annotationType;
        this.attributesMap = attributesMap;
    }

    @Override
    public void visit(String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
    }

    @Override
    public void visitEnd() {
        attributesMap.put(annotationType, attributes);
    }
}
