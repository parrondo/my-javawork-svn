package org.apache.lucene.search;

import java.io.Closeable;
import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;

@Deprecated
public abstract interface Searchable extends Closeable
{
  public abstract void search(Weight paramWeight, Filter paramFilter, Collector paramCollector)
    throws IOException;

  public abstract void close()
    throws IOException;

  public abstract int docFreq(Term paramTerm)
    throws IOException;

  public abstract int[] docFreqs(Term[] paramArrayOfTerm)
    throws IOException;

  public abstract int maxDoc()
    throws IOException;

  public abstract TopDocs search(Weight paramWeight, Filter paramFilter, int paramInt)
    throws IOException;

  public abstract Document doc(int paramInt)
    throws CorruptIndexException, IOException;

  public abstract Document doc(int paramInt, FieldSelector paramFieldSelector)
    throws CorruptIndexException, IOException;

  public abstract Query rewrite(Query paramQuery)
    throws IOException;

  public abstract Explanation explain(Weight paramWeight, int paramInt)
    throws IOException;

  public abstract TopFieldDocs search(Weight paramWeight, Filter paramFilter, int paramInt, Sort paramSort)
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Searchable
 * JD-Core Version:    0.6.0
 */