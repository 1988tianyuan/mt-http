package com.liugeng.mthttp.router.asm;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.Type;

/**
 * 通过asm工具读取类的Annotation数据
 */
public class AnnotationMetadataReadingVisitor extends ClassMetaDataReadingVisitor implements AnnotationMetadata {

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
}
