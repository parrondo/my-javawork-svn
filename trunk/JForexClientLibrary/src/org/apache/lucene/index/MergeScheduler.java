package org.apache.lucene.index;

import java.io.IOException;

public abstract class MergeScheduler
{
  public abstract void merge(IndexWriter paramIndexWriter)
    throws CorruptIndexException, IOException;

  public abstract void close()
    throws CorruptIndexException, IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.MergeScheduler
 * JD-Core Version:    0.6.0
 */