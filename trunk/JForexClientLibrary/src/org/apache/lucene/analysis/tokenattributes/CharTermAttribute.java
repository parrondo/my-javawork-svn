package org.apache.lucene.analysis.tokenattributes;

import org.apache.lucene.util.Attribute;

public abstract interface CharTermAttribute extends Attribute, CharSequence, Appendable
{
  public abstract void copyBuffer(char[] paramArrayOfChar, int paramInt1, int paramInt2);

  public abstract char[] buffer();

  public abstract char[] resizeBuffer(int paramInt);

  public abstract CharTermAttribute setLength(int paramInt);

  public abstract CharTermAttribute setEmpty();

  public abstract CharTermAttribute append(CharSequence paramCharSequence);

  public abstract CharTermAttribute append(CharSequence paramCharSequence, int paramInt1, int paramInt2);

  public abstract CharTermAttribute append(char paramChar);

  public abstract CharTermAttribute append(String paramString);

  public abstract CharTermAttribute append(StringBuilder paramStringBuilder);

  public abstract CharTermAttribute append(CharTermAttribute paramCharTermAttribute);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 * JD-Core Version:    0.6.0
 */