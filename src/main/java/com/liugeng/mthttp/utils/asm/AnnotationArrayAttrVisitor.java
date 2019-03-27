package com.liugeng.mthttp.utils.asm;

import org.objectweb.asm.AnnotationVisitor;

import java.util.Map;

/**
 * for annotation Array type attribute reading
 */
public class AnnotationArrayAttrVisitor implements AnnotationVisitor {

    private final String name;
    private final AnnotationAttributes attributes;

    public AnnotationArrayAttrVisitor(String name, AnnotationAttributes attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public void visit(String name, Object arrayValue) {
        attributes.putMulti(this.name, String.valueOf(arrayValue));
    }

    @Override
    public void visitEnum(String name, String desc, String value) {

    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return null;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return null;
    }

    @Override
    public void visitEnd() {

    }
}
