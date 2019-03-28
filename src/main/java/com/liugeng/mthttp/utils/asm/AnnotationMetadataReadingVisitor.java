package com.liugeng.mthttp.utils.asm;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * 通过asm工具读取类的Annotation数据
 */
public class AnnotationMetadataReadingVisitor implements ClassVisitor, AnnotationMetadata {

	// 存放已经遍历完成的当前类的注解全限名
	private final Set<String> annotationSet = new LinkedHashSet<>();
	// 存放每个注解对应的属性值
	private final Map<String, AnnotationAttributes> attributesMap= new LinkedHashMap<>();

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		String className = Type.getType(desc).getClassName();
		this.annotationSet.add(className);
		return new AnnotationAttributeReadingVisitor(className, attributesMap);
	}

	public AnnotationAttributes getAnnotationAttributes(String annotation){
		return attributesMap.get(annotation);
	}

	public boolean hasAnnotation(String annotation) {
		return annotationSet.contains(annotation);
	}

	@Override
	public Set<String> getAnnotationTypes() {
		return annotationSet;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

	}

	@Override
	public void visitSource(String source, String debug) {

	}

	@Override
	public void visitOuterClass(String owner, String name, String desc) {

	}

	@Override
	public void visitAttribute(Attribute attr) {

	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {

	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		return null;
	}

	@Override
	public void visitEnd() {

	}
}
