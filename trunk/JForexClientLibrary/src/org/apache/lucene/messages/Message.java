package org.apache.lucene.messages;

import java.io.Serializable;
import java.util.Locale;

public abstract interface Message extends Serializable
{
  public abstract String getKey();

  public abstract Object[] getArguments();

  public abstract String getLocalizedMessage();

  public abstract String getLocalizedMessage(Locale paramLocale);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.messages.Message
 * JD-Core Version:    0.6.0
 */