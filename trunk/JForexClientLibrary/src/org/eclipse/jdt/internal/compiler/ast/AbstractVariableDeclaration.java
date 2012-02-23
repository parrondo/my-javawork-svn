/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public abstract class AbstractVariableDeclaration extends Statement
/*     */   implements InvocationSite
/*     */ {
/*     */   public int declarationEnd;
/*     */   public int declarationSourceEnd;
/*     */   public int declarationSourceStart;
/*     */   public int hiddenVariableDepth;
/*     */   public Expression initialization;
/*     */   public int modifiers;
/*     */   public int modifiersSourceStart;
/*     */   public Annotation[] annotations;
/*     */   public char[] name;
/*     */   public TypeReference type;
/*     */   public static final int FIELD = 1;
/*     */   public static final int INITIALIZER = 2;
/*     */   public static final int ENUM_CONSTANT = 3;
/*     */   public static final int LOCAL_VARIABLE = 4;
/*     */   public static final int PARAMETER = 5;
/*     */   public static final int TYPE_PARAMETER = 6;
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  35 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public TypeBinding[] genericTypeArguments()
/*     */   {
/*  50 */     return null;
/*     */   }
/*     */ 
/*     */   public abstract int getKind();
/*     */ 
/*     */   public boolean isSuperAccess()
/*     */   {
/*  62 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isTypeAccess()
/*     */   {
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output) {
/*  73 */     printAsExpression(indent, output);
/*  74 */     switch (getKind()) {
/*     */     case 3:
/*  76 */       return output.append(',');
/*     */     }
/*  78 */     return output.append(';');
/*     */   }
/*     */ 
/*     */   public StringBuffer printAsExpression(int indent, StringBuffer output)
/*     */   {
/*  83 */     printIndent(indent, output);
/*  84 */     printModifiers(this.modifiers, output);
/*  85 */     if (this.annotations != null) printAnnotations(this.annotations, output);
/*     */ 
/*  87 */     if (this.type != null) {
/*  88 */       this.type.print(0, output).append(' ');
/*     */     }
/*  90 */     output.append(this.name);
/*  91 */     switch (getKind()) {
/*     */     case 3:
/*  93 */       if (this.initialization == null) break;
/*  94 */       this.initialization.printExpression(indent, output);
/*     */ 
/*  96 */       break;
/*     */     default:
/*  98 */       if (this.initialization == null) break;
/*  99 */       output.append(" = ");
/* 100 */       this.initialization.printExpression(indent, output);
/*     */     }
/*     */ 
/* 103 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setActualReceiverType(ReferenceBinding receiverType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setDepth(int depth)
/*     */   {
/* 122 */     this.hiddenVariableDepth = depth;
/*     */   }
/*     */ 
/*     */   public void setFieldIndex(int depth)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration
 * JD-Core Version:    0.6.0
 */