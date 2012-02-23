package org.apache.lucene.index;

import java.io.IOException;

abstract class DocFieldConsumerPerThread
{
  abstract void startDocument()
    throws IOException;

  abstract DocumentsWriter.DocWriter finishDocument()
    throws IOException;

  abstract DocFieldConsumerPerField addField(FieldInfo paramFieldInfo);

  abstract void abort();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocFieldConsumerPerThread
 * JD-Core Version:    0.6.0
 */