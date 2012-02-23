package org.apache.lucene.index;

import java.io.IOException;

public class SerialMergeScheduler extends MergeScheduler
{
  public synchronized void merge(IndexWriter writer)
    throws CorruptIndexException, IOException
  {
    while (true)
    {
      MergePolicy.OneMerge merge = writer.getNextMerge();
      if (merge == null)
        break;
      writer.merge(merge);
    }
  }

  public void close()
  {
  }
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SerialMergeScheduler
 * JD-Core Version:    0.6.0
 */