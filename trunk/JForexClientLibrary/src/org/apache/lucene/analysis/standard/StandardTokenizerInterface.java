package org.apache.lucene.analysis.standard;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public abstract interface StandardTokenizerInterface
{
  public static final int YYEOF = -1;

  public abstract void getText(CharTermAttribute paramCharTermAttribute);

  public abstract int yychar();

  public abstract void yyreset(Reader paramReader);

  public abstract int yylength();

  public abstract int getNextToken()
    throws IOException;
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.StandardTokenizerInterface
 * JD-Core Version:    0.6.0
 */