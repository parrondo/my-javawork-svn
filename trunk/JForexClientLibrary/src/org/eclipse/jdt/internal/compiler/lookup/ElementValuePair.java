/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.NameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Reference;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ 
/*     */ public class ElementValuePair
/*     */ {
/*     */   char[] name;
/*     */   public Object value;
/*     */   public MethodBinding binding;
/*     */ 
/*     */   public static Object getValue(Expression expression)
/*     */   {
/*  23 */     if (expression == null)
/*  24 */       return null;
/*  25 */     Constant constant = expression.constant;
/*     */ 
/*  27 */     if ((constant != null) && (constant != Constant.NotAConstant)) {
/*  28 */       return constant;
/*     */     }
/*  30 */     if ((expression instanceof Annotation))
/*  31 */       return ((Annotation)expression).getCompilerAnnotation();
/*  32 */     if ((expression instanceof ArrayInitializer)) {
/*  33 */       Expression[] exprs = ((ArrayInitializer)expression).expressions;
/*  34 */       int length = exprs == null ? 0 : exprs.length;
/*  35 */       Object[] values = new Object[length];
/*  36 */       for (int i = 0; i < length; i++)
/*  37 */         values[i] = getValue(exprs[i]);
/*  38 */       return values;
/*     */     }
/*  40 */     if ((expression instanceof ClassLiteralAccess))
/*  41 */       return ((ClassLiteralAccess)expression).targetType;
/*  42 */     if ((expression instanceof Reference)) {
/*  43 */       FieldBinding fieldBinding = null;
/*  44 */       if ((expression instanceof FieldReference)) {
/*  45 */         fieldBinding = ((FieldReference)expression).fieldBinding();
/*  46 */       } else if ((expression instanceof NameReference)) {
/*  47 */         Binding binding = ((NameReference)expression).binding;
/*  48 */         if ((binding != null) && (binding.kind() == 1))
/*  49 */           fieldBinding = (FieldBinding)binding;
/*     */       }
/*  51 */       if ((fieldBinding != null) && ((fieldBinding.modifiers & 0x4000) > 0)) {
/*  52 */         return fieldBinding;
/*     */       }
/*     */     }
/*  55 */     return null;
/*     */   }
/*     */ 
/*     */   public ElementValuePair(char[] name, Expression expression, MethodBinding binding) {
/*  59 */     this(name, getValue(expression), binding);
/*     */   }
/*     */ 
/*     */   public ElementValuePair(char[] name, Object value, MethodBinding binding) {
/*  63 */     this.name = name;
/*  64 */     this.value = value;
/*  65 */     this.binding = binding;
/*     */   }
/*     */ 
/*     */   public char[] getName()
/*     */   {
/*  72 */     return this.name;
/*     */   }
/*     */ 
/*     */   public MethodBinding getMethodBinding()
/*     */   {
/*  79 */     return this.binding;
/*     */   }
/*     */ 
/*     */   public Object getValue()
/*     */   {
/*  91 */     return this.value;
/*     */   }
/*     */ 
/*     */   void setMethodBinding(MethodBinding binding)
/*     */   {
/*  96 */     this.binding = binding;
/*     */   }
/*     */ 
/*     */   void setValue(Object value)
/*     */   {
/* 101 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 105 */     StringBuffer buffer = new StringBuffer(5);
/* 106 */     buffer.append(this.name).append(" = ");
/* 107 */     buffer.append(this.value);
/* 108 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ElementValuePair
 * JD-Core Version:    0.6.0
 */