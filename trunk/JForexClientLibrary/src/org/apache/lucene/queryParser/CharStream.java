package org.apache.lucene.queryParser;

import java.io.IOException;

public abstract interface CharStream
{
  public abstract char readChar()
    throws IOException;

  /** @deprecated */
  public abstract int getColumn();

  /** @deprecated */
  public abstract int getLine();

  public abstract int getEndColumn();

  public abstract int getEndLine();

  public abstract int getBeginColumn();

  public abstract int getBeginLine();

  public abstract void backup(int paramInt);

  public abstract char BeginToken()
    throws IOException;

  public abstract String GetImage();

  public abstract char[] GetSuffix(int paramInt);

  public abstract void Done();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.queryParser.CharStream
 * JD-Core Version:    0.6.0
 */