package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.store.Directory;

public abstract class PayloadProcessorProvider
{
  public abstract DirPayloadProcessor getDirProcessor(Directory paramDirectory)
    throws IOException;

  public static abstract class PayloadProcessor
  {
    public abstract int payloadLength()
      throws IOException;

    public abstract byte[] processPayload(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException;
  }

  public static abstract class DirPayloadProcessor
  {
    public abstract PayloadProcessorProvider.PayloadProcessor getProcessor(Term paramTerm)
      throws IOException;
  }
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.PayloadProcessorProvider
 * JD-Core Version:    0.6.0
 */