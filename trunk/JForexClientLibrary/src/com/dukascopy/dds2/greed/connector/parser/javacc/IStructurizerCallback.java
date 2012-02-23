package com.dukascopy.dds2.greed.connector.parser.javacc;

public abstract interface IStructurizerCallback
{
  public abstract void includeDecl(String paramString, int paramInt1, int paramInt2);

  public abstract void defineDecl(String paramString, int paramInt1, int paramInt2);

  public abstract void importDecl(String paramString, int paramInt1, int paramInt2);

  public abstract void propertyDecl(String paramString, int paramInt1, int paramInt2);

  public abstract void functionDeclBegin(String paramString, int paramInt1, int paramInt2, int paramInt3);

  public abstract void functionDeclEnd(int paramInt);

  public abstract void fieldDecl(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void fieldListDecl(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void fieldCounterDecl(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void fieldCounterListDecl(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void structDeclBegin(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  public abstract void structDeclEnd(int paramInt);

  public abstract void superDecl(String paramString);

  public abstract void reportError(Throwable paramThrowable);

  public abstract void whileIterationBegin(Token paramToken);

  public abstract void whileIterationEnd(Token paramToken);

  public abstract void forIterationBegin(Token paramToken);

  public abstract void forIterationEnd(Token paramToken);

  public abstract void doIterationBegin(Token paramToken);

  public abstract void doIterationEnd(Token paramToken);

  public abstract void ifSelectionBegin(Token paramToken);

  public abstract void ifSelectionEnd(Token paramToken);

  public abstract void elseSelectionBegin(Token paramToken);

  public abstract void elseSelectionEnd(Token paramToken);

  public abstract void switchSelectionBegin(Token paramToken);

  public abstract void switchSelectionEnd(Token paramToken);

  public abstract void caseStatementBegin(Token paramToken);

  public abstract void caseStatementEnd(Token paramToken);

  public abstract void defaultStatementBegin(Token paramToken);

  public abstract void defaultStatementEnd(Token paramToken);

  public abstract void returnStatement(Token paramToken1, Token paramToken2);

  public abstract void expressionSelection(Token paramToken1, Token paramToken2);

  public abstract void breakStatement(Token paramToken1, Token paramToken2);

  public abstract void continueStatement(Token paramToken1, Token paramToken2);

  public abstract void gotoStatement(Token paramToken1, Token paramToken2);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.IStructurizerCallback
 * JD-Core Version:    0.6.0
 */