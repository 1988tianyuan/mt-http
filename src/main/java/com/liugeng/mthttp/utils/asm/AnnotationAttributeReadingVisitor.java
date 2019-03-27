package com.liugeng.mthttp.utils.asm;

import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;

public class AnnotationAttributeReadingVisitor implements AnnotationVisitor {

    private final String annotationType;
    private final Map<String, AnnotationAttributes> attributesMap;
    private final AnnotationAttributes attributes = new AnnotationAttributes();

    public AnnotationAttributeReadingVisitor(String annotationType, Map<String, AnnotationAttributes> attributesMap){
        this.annotationType = annotationType;
        this.attributesMap = attributesMap;
    }

    @Override
    public void visit(String attributeName, Object attributeValue) {
        attributes.putMulti(attributeName, String.valueOf(attributeValue));
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        attributes.putMulti(name, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String s, String s1) {
        return null;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        System.out.println("visitArray:" + name);
        return new AnnotationArrayAttrVisitor(name, attributes);
    }

    @Override
    public void visitEnd() {
        attributesMap.put(annotationType, attributes);
    }
}
