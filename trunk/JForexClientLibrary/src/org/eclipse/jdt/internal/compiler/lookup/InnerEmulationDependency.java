/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ public class InnerEmulationDependency
/*    */ {
/*    */   public BlockScope scope;
/*    */   public boolean wasEnclosingInstanceSupplied;
/*    */ 
/*    */   public InnerEmulationDependency(BlockScope scope, boolean wasEnclosingInstanceSupplied)
/*    */   {
/* 19 */     this.scope = scope;
/* 20 */     this.wasEnclosingInstanceSupplied = wasEnclosingInstanceSupplied;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.InnerEmulationDependency
 * JD-Core Version:    0.6.0
 */