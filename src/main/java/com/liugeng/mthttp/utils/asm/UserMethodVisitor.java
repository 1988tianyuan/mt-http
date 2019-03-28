package com.liugeng.mthttp.utils.asm;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.google.common.collect.Collections2;

public class UserMethodVisitor implements MethodVisitor {

	private final ClassMethodReadingVisitor.MethodInfo methodInfo;

	private final Map<ClassMethodReadingVisitor.MethodInfo, Set<String>> methodAnnotationNameMap;

	private final Map<ClassMethodReadingVisitor.MethodInfo, Map<String, AnnotationAttributes>> methodAnnotationAttrMap;

	public UserMethodVisitor(ClassMethodReadingVisitor.MethodInfo methodInfo, Map<ClassMethodReadingVisitor.MethodInfo, Set<String>> methodAnnotationNameMap,
		Map<ClassMethodReadingVisitor.MethodInfo, Map<String, AnnotationAttributes>> methodAnnotationAttrMap) {
		this.methodInfo = methodInfo;
		this.methodAnnotationNameMap = methodAnnotationNameMap;
		this.methodAnnotationAttrMap = methodAnnotationAttrMap;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return null;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		// store current annotation name into current method annotation set
		String annoClassName = Type.getType(desc).getClassName();
		// if annoSet of current method doesn't exist then create new one
		Set<String> annoSet = methodAnnotationNameMap.computeIfAbsent(methodInfo, k -> new HashSet<>());
		annoSet.add(annoClassName);
		// if attributesMap of current method doesn't exist then create new one
		Map<String, AnnotationAttributes> attributesMap = methodAnnotationAttrMap.computeIfAbsent(methodInfo,
			k -> new LinkedHashMap<>());
		// create AnnotationVisitor about current method annotation then begin visit
		return new AnnotationAttributeReadingVisitor(annoClassName, attributesMap);
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		return null;
	}

	@Override
	public void visitEnd() {

	}

	@Override
	public void visitAttribute(Attribute attr) {

	}

	@Override
	public void visitCode() {

	}

	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {

	}

	@Override
	public void visitInsn(int opcode) {

	}

	@Override
	public void visitIntInsn(int opcode, int operand) {

	}

	@Override
	public void visitVarInsn(int opcode, int var) {

	}

	@Override
	public void visitTypeInsn(int opcode, String type) {

	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {

	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc) {

	}

	@Override
	public void visitJumpInsn(int opcode, Label label) {

	}

	@Override
	public void visitLabel(Label label) {

	}

	@Override
	public void visitLdcInsn(Object cst) {

	}

	@Override
	public void visitIincInsn(int var, int increment) {

	}

	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {

	}

	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {

	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {

	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {

	}

	@Override
	public void visitLineNumber(int line, Label start) {

	}

	@Override
	public void visitMaxs(int maxStack, int maxLocals) {

	}
}
