package org.apache.lucene.index;

import java.io.IOException;

public abstract interface TermPositions extends TermDocs
{
  public abstract int nextPosition()
    throws IOException;

  public abstract int getPayloadLength();

  public abstract byte[] getPayload(byte[] paramArrayOfByte, int paramInt)
    throws IOException;

  public abstract boolean isPayloadAvailable();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermPositions
 * JD-Core Version:    0.6.0
 */