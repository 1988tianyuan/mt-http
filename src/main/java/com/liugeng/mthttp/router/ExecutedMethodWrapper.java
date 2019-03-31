package com.liugeng.mthttp.router;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class ExecutedMethodWrapper {

	private Method userMethod;

	private Class<?> userClass;

	private Parameter[] parameters;

	private Object userObject;

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public Method getUserMethod() {
		return userMethod;
	}

	public void setUserMethod(Method userMethod) {
		this.userMethod = userMethod;
	}

	public Class<?> getUserClass() {
		return userClass;
	}

	public void setUserClass(Class<?> userClass) {
		this.userClass = userClass;
	}

	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}
}
