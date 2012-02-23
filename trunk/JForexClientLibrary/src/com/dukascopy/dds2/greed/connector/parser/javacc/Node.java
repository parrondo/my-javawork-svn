package com.dukascopy.dds2.greed.connector.parser.javacc;

public abstract interface Node
{
  public abstract void jjtOpen();

  public abstract void jjtClose();

  public abstract void jjtSetParent(Node paramNode);

  public abstract Node jjtGetParent();

  public abstract void jjtAddChild(Node paramNode, int paramInt);

  public abstract Node jjtGetChild(int paramInt);

  public abstract int jjtGetNumChildren();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.Node
 * JD-Core Version:    0.6.0
 */