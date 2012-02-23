package org.apache.lucene.index;

import java.io.IOException;
import org.apache.lucene.document.Fieldable;

abstract class TermsHashConsumerPerField
{
  abstract boolean start(Fieldable[] paramArrayOfFieldable, int paramInt)
    throws IOException;

  abstract void finish()
    throws IOException;

  abstract void skippingLongTerm()
    throws IOException;

  abstract void start(Fieldable paramFieldable);

  abstract void newTerm(int paramInt)
    throws IOException;

  abstract void addTerm(int paramInt)
    throws IOException;

  abstract int getStreamCount();

  abstract ParallelPostingsArray createPostingsArray(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermsHashConsumerPerField
 * JD-Core Version:    0.6.0
 */