package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public abstract interface TypeAttribute extends Attribute
{
  public static final String DEFAULT_TYPE = "word";

  public abstract String type();

  public abstract void setType(String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.TypeAttribute
 * JD-Core Version:    0.6.0
 */