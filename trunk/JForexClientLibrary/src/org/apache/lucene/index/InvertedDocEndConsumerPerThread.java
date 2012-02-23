package org.apache.lucene.index;

abstract class InvertedDocEndConsumerPerThread
{
  abstract void startDocument();

  abstract InvertedDocEndConsumerPerField addField(DocInverterPerField paramDocInverterPerField, FieldInfo paramFieldInfo);

  abstract void finishDocument();

  abstract void abort();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.InvertedDocEndConsumerPerThread
 * JD-Core Version:    0.6.0
 */