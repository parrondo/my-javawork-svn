/*    */ package org.eclipse.jdt.internal.compiler.parser;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*    */ 
/*    */ public class RecoveredStatement extends RecoveredElement
/*    */ {
/*    */   public Statement statement;
/*    */ 
/*    */   public RecoveredStatement(Statement statement, RecoveredElement parent, int bracketBalance)
/*    */   {
/* 26 */     super(parent, bracketBalance);
/* 27 */     this.statement = statement;
/*    */   }
/*    */ 
/*    */   public ASTNode parseTree()
/*    */   {
/* 33 */     return this.statement;
/*    */   }
/*    */ 
/*    */   public int sourceEnd()
/*    */   {
/* 39 */     return this.statement.sourceEnd;
/*    */   }
/*    */   public String toString(int tab) {
/* 42 */     return tabString(tab) + "Recovered statement:\n" + this.statement.print(tab + 1, new StringBuffer(10));
/*    */   }
/*    */   public Statement updatedStatement(int depth, Set knownTypes) {
/* 45 */     return this.statement;
/*    */   }
/*    */   public void updateParseTree() {
/* 48 */     updatedStatement(0, new HashSet());
/*    */   }
/*    */ 
/*    */   public void updateSourceEndIfNecessary(int bodyStart, int bodyEnd)
/*    */   {
/* 54 */     if (this.statement.sourceEnd == 0)
/* 55 */       this.statement.sourceEnd = bodyEnd;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredStatement
 * JD-Core Version:    0.6.0
 */