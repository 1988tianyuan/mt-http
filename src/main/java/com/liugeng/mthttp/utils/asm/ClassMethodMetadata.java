package com.liugeng.mthttp.utils.asm;

import java.util.Map;
import java.util.Set;

public interface ClassMethodMetadata {

	Set<ClassMethodReadingVisitor.MethodInfo> getMethodInfoSet();

	Map<ClassMethodReadingVisitor.MethodInfo, Set<String>> getMethodAnnotationNameMap();

	Set<String> getAnnotationNamesByMethod(ClassMethodReadingVisitor.MethodInfo methodName);

	boolean checkMethodAnnotationAbsent(ClassMethodReadingVisitor.MethodInfo methodName, String annotationName);

	Map<String, AnnotationAttributes> getMethodAnnotationAttrMap (ClassMethodReadingVisitor.MethodInfo methodName);

	AnnotationAttributes getMethodAnnotationAttr(ClassMethodReadingVisitor.MethodInfo methodInfo, String annotationName);
}
