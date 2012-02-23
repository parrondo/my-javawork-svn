package org.apache.lucene.index;

import java.io.IOException;

abstract class TermsHashConsumerPerThread
{
  abstract void startDocument()
    throws IOException;

  abstract DocumentsWriter.DocWriter finishDocument()
    throws IOException;

  public abstract TermsHashConsumerPerField addField(TermsHashPerField paramTermsHashPerField, FieldInfo paramFieldInfo);

  public abstract void abort();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.TermsHashConsumerPerThread
 * JD-Core Version:    0.6.0
 */