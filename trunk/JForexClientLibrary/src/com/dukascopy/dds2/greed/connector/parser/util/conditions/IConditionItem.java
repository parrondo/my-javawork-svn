package com.dukascopy.dds2.greed.connector.parser.util.conditions;

import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
import com.dukascopy.dds2.greed.connector.parser.util.Declaration;

public abstract interface IConditionItem
{
  public abstract ASTNode getNode();

  public abstract void setNode(ASTNode paramASTNode);

  public abstract IConditionItem getParent();

  public abstract void setParent(IConditionItem paramIConditionItem);

  public abstract void add(IConditionItem paramIConditionItem);

  public abstract IConditionItem get(int paramInt);

  public abstract int size();

  public abstract StringBuilder print();

  public abstract StringBuilder print(int paramInt);

  public abstract boolean hasChildren();

  public abstract ConditionRoot getConditionRoot();

  public abstract void setConditionRoot(ConditionRoot paramConditionRoot);

  public abstract Declaration getDeclarationRoot();

  public abstract boolean isInLRParnesis();

  public abstract void setInLRParnesis(boolean paramBoolean);

  public abstract boolean isBoolResult();

  public abstract void setBoolResult(boolean paramBoolean);

  public abstract IConditionItem prev();

  public abstract IConditionItem next();

  public abstract void setPreviousConditionItem(IConditionItem paramIConditionItem);

  public abstract void setNextConditionItem(IConditionItem paramIConditionItem);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.conditions.IConditionItem
 * JD-Core Version:    0.6.0
 */