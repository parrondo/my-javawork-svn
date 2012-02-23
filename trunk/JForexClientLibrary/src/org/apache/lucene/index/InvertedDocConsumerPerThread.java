package org.apache.lucene.index;

import java.io.IOException;

abstract class InvertedDocConsumerPerThread
{
  abstract void startDocument()
    throws IOException;

  abstract InvertedDocConsumerPerField addField(DocInverterPerField paramDocInverterPerField, FieldInfo paramFieldInfo);

  abstract DocumentsWriter.DocWriter finishDocument()
    throws IOException;

  abstract void abort();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.InvertedDocConsumerPerThread
 * JD-Core Version:    0.6.0
 */