package com.dukascopy.api.nlink.win32;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
public @interface NativeImport
{
  public abstract String library();

  public abstract String function();

  public abstract CallingConvention convention();

  public abstract Charset charset();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.NativeImport
 * JD-Core Version:    0.6.0
 */