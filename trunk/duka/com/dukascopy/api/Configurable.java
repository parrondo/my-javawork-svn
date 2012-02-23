/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields marked with this annotation will be shown in dialog before strategy start. Values from that dialog will be set to fields before
 * calling onStart method
 *
 * @author Denis Larka
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configurable {
    /**
     * Name of the field that will appear in dialog
     *
     * @return value for field
     */
    String value();

    /**
     * This field is exclusively for File parameters,
	 * for other types of parameters fileType value will be ignored.
	 *
     * <p>
     * File parameters can be visualized in 2 ways:
     * </p>
     * <p>
     * <b>1.</b> textField and button("..."),
     * which allow you to specify file from your file system.
     * Do not use fileType or define it ="" to use this visualization
     * </p>
     * <p>
     * <b>2.</b> combobox with predefined list of file(s) from strategy directory.
     * fileType should not be empty to use this visualization.
     * In this case fileType is filter, which specifies what files to show in combo.<br>
     * e.g. fieldType="*" -> return all your files from Strategy directory,<br>
     * fieldType="*.xml" -> return only files with XML extension from Strategy directory<br>
     * </p>
     *
     * @return fileName filter
     */
    String fileType() default "";

    /**
     * This field is suitable for parameter's types as : int, long or double.
     * For other types of parameters stepSize value will be ignored.
     *
     * Value must be positive & greater then zero
     *
     * Default stepSize for int & long is 1, for double it's 0.5
     *
     * @return step value as String
     */
    double stepSize() default 0;

    /**
     * Setting this to true means field is obligatory
     *
     * @return true - obligatory, default false
     */
    boolean obligatory() default false;
    
    /**
     * Setting this to true means field is read only and cannot be modified
     *
     * @return true - read only, default false
     */
    boolean readOnly() default false;
    
    /**
     * A short description of annotated field
     * @return description of configurable field
     */
    String description() default "";
    
    
    /**
     * Setting if 
     *
     * @return true - obligatory, default false
     */
    boolean datetimeAsLong() default false;
}