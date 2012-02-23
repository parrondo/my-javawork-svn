/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.CacheInputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class ParserTest
/*     */   implements IStructurizerCallback
/*     */ {
/*     */   private CacheInputStream fCacheInputStream;
/*     */ 
/*     */   public ParserTest(CacheInputStream cis)
/*     */   {
/*  23 */     this.fCacheInputStream = cis;
/*     */   }
/*     */ 
/*     */   public void println(String where, String text) {
/*  27 */     System.out.println(where + ":" + text);
/*     */   }
/*     */ 
/*     */   public void println(String text) {
/*  31 */     System.out.println(text);
/*     */   }
/*     */ 
/*     */   public void functionDeclBegin(String name, int nameStartPos, int nameEndPos, int declStartPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void functionDeclEnd(int declEndPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fieldDecl(String name, int nameStartPos, int nameEndPos, int declStartPos, int declEndPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fieldListDecl(String name, int nameStartPos, int nameEndPos, int declStartPos, int declEndPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void structDeclBegin(String name, int kind, int nameStartPos, int nameEndPos, int declStartPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void structDeclEnd(int declEndPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void superDecl(String name)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void includeDecl(String name, int startPos, int endPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void defineDecl(String name, int startPos, int endPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void reportError(Throwable throwable)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void whileIterationBegin(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void whileIterationEnd(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void forIterationBegin(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void forIterationEnd(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void doIterationBegin(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void doIterationEnd(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public String print()
/*     */   {
/* 119 */     return "";
/*     */   }
/*     */ 
/*     */   public void fieldCounterDecl(String name, int nameStartPos, int nameEndPos, int declStartPos, int declEndPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fieldCounterListDecl(String name, int nameStartPos, int nameEndPos, int declStartPos, int declEndPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void ifSelectionBegin(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void ifSelectionEnd(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void switchSelectionBegin(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void switchSelectionEnd(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void returnStatement(Token beginToken, Token endToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void elseSelectionBegin(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void elseSelectionEnd(Token token)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void expressionSelection(Token beginToken, Token endToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void breakStatement(Token beginToken, Token endToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void continueStatement(Token beginToken, Token endToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void gotoStatement(Token beginToken, Token endToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void importDecl(String name, int startPos, int endPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void propertyDecl(String name, int startPos, int endPos)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void caseStatementBegin(Token beginToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void caseStatementEnd(Token endToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void defaultStatementBegin(Token beginToken)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void defaultStatementEnd(Token endToken)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.ParserTest
 * JD-Core Version:    0.6.0
 */