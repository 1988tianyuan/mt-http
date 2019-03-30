package com.liugeng.mthttp.utils.converter;

import java.beans.PropertyEditor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import com.liugeng.mthttp.exception.TypeMismatchException;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.utils.ClassUtils;
import com.liugeng.mthttp.utils.converter.propertyediors.CustomBooleanEditor;
import com.liugeng.mthttp.utils.converter.propertyediors.CustomNumberEditor;

/**
 * converter the origin Object(almost {@link String}) into target type
 * should not be reference type, just primitive type or String
 */
public class PrimitiveTypeConverter extends TypeConverter {

    private final Map<Class<?>, PropertyEditor> defaultEditors = new HashMap<>();

    public PrimitiveTypeConverter(){
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convertIfNecessary(Parameter parameter, ConnectContext context) throws Exception {
        Map<String, Object> origParams = getReqParams(context);
        Class<?> requireType = parameter.getType();
        String argName = parameter.getName();
        Object value = origParams.get(argName);
        if (ClassUtils.isAssignableValue(requireType, value)) {
            return value;
        } else {
            boolean isPrim = org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper(requireType);
            boolean isString = (value instanceof String);
            if (value == null) {
                if (isPrim) {
                    value = "0";
                } else {
                    return null;
                }
            }
            if(isString){
                PropertyEditor editor = this.findDefaultEditor(requireType);
                try {
                    editor.setAsText((String) value);
                } catch (IllegalArgumentException e) {
                    throw new TypeMismatchException(value, requireType);
                }
                return editor.getValue();
            }else {
                throw new IllegalArgumentException("just support to convert String or primitive type, value: " + value +
                    " is not valid!");
            }
        }
    }

    private PropertyEditor findDefaultEditor(Class<?> requireType){
        PropertyEditor editor = this.getDefaultEditor(requireType);
        if (editor == null){
            throw new RuntimeException("Editor for " + requireType + " has not been implemented.");
        }
        return editor;
    }

    private PropertyEditor getDefaultEditor(Class<?> requireType){
        if(this.defaultEditors.isEmpty()){
            this.initDefaultEditors();
        }
        return this.defaultEditors.get(requireType);
    }

    private void initDefaultEditors(){
        defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
        defaultEditors.put(int.class, new CustomNumberEditor(Integer.class, true));
        defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
        defaultEditors.put(long.class, new CustomNumberEditor(Long.class, true));
        defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
        defaultEditors.put(double.class, new CustomNumberEditor(Double.class, true));
        defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
        defaultEditors.put(float.class, new CustomNumberEditor(Float.class, true));
        defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
        defaultEditors.put(short.class, new CustomNumberEditor(Short.class, true));
        defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));
    }
}
