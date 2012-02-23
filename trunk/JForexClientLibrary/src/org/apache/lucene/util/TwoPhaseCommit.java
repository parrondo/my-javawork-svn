package org.apache.lucene.util;

import java.io.IOException;
import java.util.Map;

public abstract interface TwoPhaseCommit
{
  public abstract void prepareCommit()
    throws IOException;

  public abstract void prepareCommit(Map<String, String> paramMap)
    throws IOException;

  public abstract void commit()
    throws IOException;

  public abstract void commit(Map<String, String> paramMap)
    throws IOException;

  public abstract void rollback()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.TwoPhaseCommit
 * JD-Core Version:    0.6.0
 */