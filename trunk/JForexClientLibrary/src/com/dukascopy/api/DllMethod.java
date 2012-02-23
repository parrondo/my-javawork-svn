package com.dukascopy.api;

import com.dukascopy.api.nlink.CallConvention;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface DllMethod
{
  public abstract String value();

  public abstract CallConvention convention();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.DllMethod
 * JD-Core Version:    0.6.0
 */