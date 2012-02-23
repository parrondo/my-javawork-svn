package org.apache.lucene.index;

import java.io.IOException;

abstract class FormatPostingsFieldsConsumer
{
  abstract FormatPostingsTermsConsumer addField(FieldInfo paramFieldInfo)
    throws IOException;

  abstract void finish()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FormatPostingsFieldsConsumer
 * JD-Core Version:    0.6.0
 */