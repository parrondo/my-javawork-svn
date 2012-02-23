package org.apache.lucene.index;

import java.io.IOException;
import java.util.List;

public abstract interface IndexDeletionPolicy
{
  public abstract void onInit(List<? extends IndexCommit> paramList)
    throws IOException;

  public abstract void onCommit(List<? extends IndexCommit> paramList)
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.IndexDeletionPolicy
 * JD-Core Version:    0.6.0
 */