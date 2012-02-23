package org.apache.lucene.search;

import java.io.IOException;
import java.io.Serializable;

public abstract class FieldComparatorSource
  implements Serializable
{
  public abstract FieldComparator newComparator(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldComparatorSource
 * JD-Core Version:    0.6.0
 */