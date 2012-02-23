package org.apache.lucene.index;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

abstract class InvertedDocEndConsumer
{
  abstract InvertedDocEndConsumerPerThread addThread(DocInverterPerThread paramDocInverterPerThread);

  abstract void flush(Map<InvertedDocEndConsumerPerThread, Collection<InvertedDocEndConsumerPerField>> paramMap, SegmentWriteState paramSegmentWriteState)
    throws IOException;

  abstract void abort();

  abstract void setFieldInfos(FieldInfos paramFieldInfos);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.InvertedDocEndConsumer
 * JD-Core Version:    0.6.0
 */