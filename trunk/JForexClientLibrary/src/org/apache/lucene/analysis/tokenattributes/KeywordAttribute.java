package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public abstract interface KeywordAttribute extends Attribute
{
  public abstract boolean isKeyword();

  public abstract void setKeyword(boolean paramBoolean);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.KeywordAttribute
 * JD-Core Version:    0.6.0
 */