package org.apache.lucene.document;

import java.io.Serializable;

public abstract interface FieldSelector extends Serializable
{
  public abstract FieldSelectorResult accept(String paramString);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.FieldSelector
 * JD-Core Version:    0.6.0
 */