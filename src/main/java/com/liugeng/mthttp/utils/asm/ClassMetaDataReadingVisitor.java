package com.liugeng.mthttp.utils.asm;

import com.liugeng.mthttp.utils.ClassUtils;
import org.objectweb.asm.*;

/**
 * 通过asm工具读取类的元数据
 */
public class ClassMetaDataReadingVisitor implements ClassVisitor, ClassMetadata {

	private boolean isAbstract;
	private boolean isInterface;
	private boolean isFinal;
	private boolean isAnnotation;
	private String className;
	private String superClassName;
	private String[] interfaces;

	public ClassMetaDataReadingVisitor() {

	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		this.className = ClassUtils.convertResourcePathtoClassName(name);
		this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
		this.isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;
		this.isFinal = (access & Opcodes.ACC_FINAL) != 0;
		this.isAnnotation = (access & Opcodes.ACC_ANNOTATION) != 0;

		if(superName != null){
			this.superClassName = ClassUtils.convertResourcePathtoClassName(superName);
		}
		this.interfaces = new String[interfaces.length];
		for(int i = 0; i < interfaces.length; i++){
			this.interfaces[i] = ClassUtils.convertResourcePathtoClassName(interfaces[i]);
		}
	}

	@Override
	public void visitSource(String s, String s1) {

	}

	@Override
	public void visitOuterClass(String s, String s1, String s2) {

	}

	@Override
	public AnnotationVisitor visitAnnotation(String s, boolean b) {
		return null;
	}

	@Override
	public void visitAttribute(Attribute attribute) {

	}

	@Override
	public void visitInnerClass(String s, String s1, String s2, int i) {

	}

	@Override
	public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
		return null;
	}

	@Override
	public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
		return null;
	}

	@Override
	public void visitEnd() {

	}

	public void setInterface(boolean anInterface) {
		isInterface = anInterface;
	}

	public void setFinal(boolean aFinal) {
		isFinal = aFinal;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public boolean isInterface() {
		return isInterface;
	}

	@Override
	public boolean isAnnotation() {
		return isAnnotation;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}

	@Override
	public boolean isFinal() {
		return isFinal;
	}

	@Override
	public boolean hasSuperClass() {
		return superClassName != null;
	}

	@Override
	public String getSuperClassName() {
		return superClassName;
	}

	@Override
	public String[] getInterfaceNames() {
		return interfaces;
	}
}
