package org.apache.lucene.store;

import java.io.Closeable;
import java.io.IOException;

public abstract class IndexOutput extends DataOutput
  implements Closeable
{
  public abstract void flush()
    throws IOException;

  public abstract void close()
    throws IOException;

  public abstract long getFilePointer();

  public abstract void seek(long paramLong)
    throws IOException;

  public abstract long length()
    throws IOException;

  public void setLength(long length)
    throws IOException
  {
  }
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.IndexOutput
 * JD-Core Version:    0.6.0
 */