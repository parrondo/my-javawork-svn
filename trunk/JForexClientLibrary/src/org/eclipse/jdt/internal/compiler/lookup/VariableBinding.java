/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*    */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*    */ 
/*    */ public abstract class VariableBinding extends Binding
/*    */ {
/*    */   public int modifiers;
/*    */   public TypeBinding type;
/*    */   public char[] name;
/*    */   protected Constant constant;
/*    */   public int id;
/*    */   public long tagBits;
/*    */ 
/*    */   public VariableBinding(char[] name, TypeBinding type, int modifiers, Constant constant)
/*    */   {
/* 27 */     this.name = name;
/* 28 */     this.type = type;
/* 29 */     this.modifiers = modifiers;
/* 30 */     this.constant = constant;
/* 31 */     if (type != null)
/* 32 */       this.tagBits |= type.tagBits & 0x80;
/*    */   }
/*    */ 
/*    */   public Constant constant()
/*    */   {
/* 37 */     return this.constant;
/*    */   }
/*    */   public abstract AnnotationBinding[] getAnnotations();
/*    */ 
/*    */   public final boolean isBlankFinal() {
/* 43 */     return (this.modifiers & 0x4000000) != 0;
/*    */   }
/*    */ 
/*    */   public final boolean isFinal()
/*    */   {
/* 49 */     return (this.modifiers & 0x10) != 0;
/*    */   }
/*    */   public char[] readableName() {
/* 52 */     return this.name;
/*    */   }
/*    */   public void setConstant(Constant constant) {
/* 55 */     this.constant = constant;
/*    */   }
/*    */   public String toString() {
/* 58 */     StringBuffer output = new StringBuffer(10);
/* 59 */     ASTNode.printModifiers(this.modifiers, output);
/* 60 */     if ((this.modifiers & 0x2000000) != 0) {
/* 61 */       output.append("[unresolved] ");
/*    */     }
/* 63 */     output.append(this.type != null ? this.type.debugName() : "<no type>");
/* 64 */     output.append(" ");
/* 65 */     output.append(this.name != null ? new String(this.name) : "<no name>");
/* 66 */     return output.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.VariableBinding
 * JD-Core Version:    0.6.0
 */