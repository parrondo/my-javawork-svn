package org.apache.lucene.search;

import java.io.IOException;
import java.io.Serializable;
import org.apache.lucene.index.IndexReader;

public abstract class Filter
  implements Serializable
{
  public abstract DocIdSet getDocIdSet(IndexReader paramIndexReader)
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Filter
 * JD-Core Version:    0.6.0
 */