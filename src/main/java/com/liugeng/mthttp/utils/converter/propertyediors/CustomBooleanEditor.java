package com.liugeng.mthttp.utils.converter.propertyediors;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;

/**
 * 将字符串转换为Boolean类型
 */
public class CustomBooleanEditor extends PropertyEditorSupport {
    private final boolean allowEmpty;

    public CustomBooleanEditor(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String input = (text != null ? text.trim() : null);
        if(this.allowEmpty && StringUtils.isNotEmpty(input)){
            this.setValue(null);
        } else if (ValueType.VALUE_TRUE.getValue().equalsIgnoreCase(input) || ValueType.VALUE_ON.getValue().equalsIgnoreCase(input)
                || ValueType.VALUE_YES.getValue().equalsIgnoreCase(input) || ValueType.VALUE_0.getValue().equalsIgnoreCase(input)) {
            this.setValue(Boolean.TRUE);
        } else if (ValueType.VALUE_FALSE.getValue().equalsIgnoreCase(input) || ValueType.VALUE_OFF.getValue().equalsIgnoreCase(input)
                || ValueType.VALUE_NO.getValue().equalsIgnoreCase(input) || ValueType.VALUE_1.getValue().equalsIgnoreCase(input)) {
            this.setValue(Boolean.FALSE);
        } else {
            throw new IllegalArgumentException("Invalid boolean value : " + text);
        }
    }

    private enum ValueType{
        VALUE_TRUE("true"), VALUE_FALSE("false"),
        VALUE_YES("yes"), VALUE_NO("no"),
        VALUE_ON("on"), VALUE_OFF("off"),
        VALUE_0("0"), VALUE_1("1");
        private String value;
        private ValueType(String value){
            this.value = value;
        }
        private String getValue(){
            return this.value;
        }
    }
}
