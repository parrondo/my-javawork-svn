package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collection;

abstract class DocConsumer
{
  abstract DocConsumerPerThread addThread(DocumentsWriterThreadState paramDocumentsWriterThreadState)
    throws IOException;

  abstract void flush(Collection<DocConsumerPerThread> paramCollection, SegmentWriteState paramSegmentWriteState)
    throws IOException;

  abstract void abort();

  abstract boolean freeRAM();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DocConsumer
 * JD-Core Version:    0.6.0
 */