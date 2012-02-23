package com.dukascopy.dds2.greed.connector.parser.util;

import java.util.List;
import java.util.Map;

public abstract interface IDeclaration
{
  public abstract String startText();

  public abstract String endText();

  public abstract List<IDeclaration> getChildren();

  public abstract Map<String, IDeclaration> getVariables();

  public abstract Map<String, IDeclaration> getFunctions();

  public abstract Map<String, IDeclaration> getClasses();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.IDeclaration
 * JD-Core Version:    0.6.0
 */