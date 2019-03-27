package com.liugeng.mthttp.utils.asm;

import java.util.Map;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.tree.AnnotationNode;

public class AnnotationAttributeReadingVisitor extends AnnotationNode {

    private final String annotationType;
    private final Map<String, AnnotationAttributes> attributesMap;
    private final AnnotationAttributes attributes = new AnnotationAttributes();

    public AnnotationAttributeReadingVisitor(String annotationType, String desc, Map<String, AnnotationAttributes> attributesMap){
        super(262144, desc);
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
