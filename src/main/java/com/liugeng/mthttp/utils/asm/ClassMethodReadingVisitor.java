package com.liugeng.mthttp.utils.asm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * retrieve class method info by asm visitor
 */
public class ClassMethodReadingVisitor implements ClassVisitor, ClassMethodMetadata {

	// method name set of current class
	private final Set<MethodInfo> methodNameSet = new LinkedHashSet<>();
	// method annotation name set of each method
	private final Map<MethodInfo, Set<String>> methodAnnotationNameMap = new LinkedHashMap<>();
	// annotation attributes map of each method
	private final Map<MethodInfo, Map<String, AnnotationAttributes>> methodAnnotationAttrMap = new LinkedHashMap<>();

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		Type[] argTypes = Type.getArgumentTypes(desc);
		String[] paramArgTypes = null;
		if (argTypes.length > 0) {
			paramArgTypes = new String[argTypes.length];
			for (int i = 0; i < argTypes.length; i++) {
				paramArgTypes[i] = argTypes[i].getClassName();
			}
		}
		MethodInfo methodInfo = new MethodInfo(name, paramArgTypes);
		// store current method into methodNameSet
		methodNameSet.add(methodInfo);
		return new UserMethodVisitor(methodInfo, methodAnnotationNameMap, methodAnnotationAttrMap);
	}

	@Override
	public Map<String, AnnotationAttributes> getMethodAnnotationAttrMap (MethodInfo methodInfo) {
		return methodAnnotationAttrMap.get(methodInfo);
	}

	@Override
	public Set<ClassMethodReadingVisitor.MethodInfo> getMethodInfoSet() {
		return this.methodNameSet;
	}

	@Override
	public Map<ClassMethodReadingVisitor.MethodInfo, Set<String>> getMethodAnnotationNameMap() {
		return this.methodAnnotationNameMap;
	}

	@Override
	public Set<String> getAnnotationNamesByMethod(ClassMethodReadingVisitor.MethodInfo methodInfo) {
		return this.methodAnnotationNameMap.get(methodInfo);
	}

	@Override
	public boolean checkMethodAnnotationAbsent(ClassMethodReadingVisitor.MethodInfo methodInfo, String annotationName) {
		Set<String> annoSet = methodAnnotationNameMap.get(methodInfo);
		if (annoSet != null) {
			return annoSet.contains(annotationName);
		}
		return false;
	}

	@Override
	public AnnotationAttributes getMethodAnnotationAttr(ClassMethodReadingVisitor.MethodInfo methodInfo, String annotationName) {
		Map<String, AnnotationAttributes> annotationAttributesMap = methodAnnotationAttrMap.get(methodInfo);
		if (annotationAttributesMap != null) {
			return annotationAttributesMap.get(annotationName);
		}
		return null;
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
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		return null;
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
	public void visitEnd() {

	}


	public static class MethodInfo {

		private final String methodName;

		private final String[] argTypes;

		public MethodInfo(String methodName, String[] argTypes) {
			this.methodName = methodName;
			this.argTypes = argTypes;
		}

		public String getMethodName() {
			return methodName;
		}

		public String[] getArgTypes() {
			return argTypes;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;
			MethodInfo that = (MethodInfo)o;
			return Objects.equals(methodName, that.methodName) &&
				Arrays.equals(argTypes, that.argTypes);
		}

		@Override
		public int hashCode() {
			int result = Objects.hash(methodName);
			result = 31 * result + Arrays.hashCode(argTypes);
			return result;
		}
	}
}
