package org.apache.lucene.index;

import java.io.IOException;

abstract class FormatPostingsPositionsConsumer
{
  abstract void addPosition(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws IOException;

  abstract void finish()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsPositionsConsumer
 * JD-Core Version:    0.6.0
 */