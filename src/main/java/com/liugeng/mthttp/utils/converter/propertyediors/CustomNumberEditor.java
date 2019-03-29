package com.liugeng.mthttp.utils.converter.propertyediors;

import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;

import org.apache.commons.lang3.StringUtils;

import com.liugeng.mthttp.utils.NumberParser;

/**
 * store the text that should parse into Number type
 */
public class CustomNumberEditor extends PropertyEditorSupport{
    private final Class<? extends Number> numberClass;
    private final boolean allowEmpty;

    public CustomNumberEditor(Class<? extends Number> numberClass, boolean allowEmpty){
        if(numberClass == null || !Number.class.isAssignableFrom(numberClass)){
            throw new IllegalArgumentException("Property class must be a subClass of Number.");
        }
        this.numberClass = numberClass;
        this.allowEmpty = allowEmpty;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(this.allowEmpty && StringUtils.isEmpty(text)){
            this.setValue(null);
        } else {
            this.setValue(NumberParser.parseNumber(text, this.numberClass));
        }
    }

}
