package com.dukascopy.dds2.greed.gui.table;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.swing.SortOrder;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface ColumnDescriptor
{
  public abstract String title();

  public abstract Class<?> contentClass();

  public abstract int width();

  public abstract int maxWidth();

  public abstract boolean resizable();

  public abstract boolean sortable();

  public abstract SortOrder sortOrder();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.table.ColumnDescriptor
 * JD-Core Version:    0.6.0
 */