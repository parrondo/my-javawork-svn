/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ public abstract class Binding
/*     */ {
/*     */   public static final int FIELD = 1;
/*     */   public static final int LOCAL = 2;
/*     */   public static final int VARIABLE = 3;
/*     */   public static final int TYPE = 4;
/*     */   public static final int METHOD = 8;
/*     */   public static final int PACKAGE = 16;
/*     */   public static final int IMPORT = 32;
/*     */   public static final int ARRAY_TYPE = 68;
/*     */   public static final int BASE_TYPE = 132;
/*     */   public static final int PARAMETERIZED_TYPE = 260;
/*     */   public static final int WILDCARD_TYPE = 516;
/*     */   public static final int RAW_TYPE = 1028;
/*     */   public static final int GENERIC_TYPE = 2052;
/*     */   public static final int TYPE_PARAMETER = 4100;
/*     */   public static final int INTERSECTION_TYPE = 8196;
/*  35 */   public static final TypeBinding[] NO_TYPES = new TypeBinding[0];
/*  36 */   public static final TypeBinding[] NO_PARAMETERS = new TypeBinding[0];
/*  37 */   public static final ReferenceBinding[] NO_EXCEPTIONS = new ReferenceBinding[0];
/*  38 */   public static final ReferenceBinding[] ANY_EXCEPTION = new ReferenceBinding[1];
/*  39 */   public static final FieldBinding[] NO_FIELDS = new FieldBinding[0];
/*  40 */   public static final MethodBinding[] NO_METHODS = new MethodBinding[0];
/*  41 */   public static final ReferenceBinding[] NO_SUPERINTERFACES = new ReferenceBinding[0];
/*  42 */   public static final ReferenceBinding[] NO_MEMBER_TYPES = new ReferenceBinding[0];
/*  43 */   public static final TypeVariableBinding[] NO_TYPE_VARIABLES = new TypeVariableBinding[0];
/*  44 */   public static final AnnotationBinding[] NO_ANNOTATIONS = new AnnotationBinding[0];
/*  45 */   public static final ElementValuePair[] NO_ELEMENT_VALUE_PAIRS = new ElementValuePair[0];
/*     */ 
/*  47 */   public static final FieldBinding[] UNINITIALIZED_FIELDS = new FieldBinding[0];
/*  48 */   public static final MethodBinding[] UNINITIALIZED_METHODS = new MethodBinding[0];
/*  49 */   public static final ReferenceBinding[] UNINITIALIZED_REFERENCE_TYPES = new ReferenceBinding[0];
/*     */ 
/*     */   public abstract int kind();
/*     */ 
/*     */   public char[] computeUniqueKey()
/*     */   {
/*  60 */     return computeUniqueKey(true);
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/*  67 */     return null;
/*     */   }
/*     */ 
/*     */   public long getAnnotationTagBits()
/*     */   {
/*  76 */     return 0L;
/*     */   }
/*     */ 
/*     */   public void initializeDeprecatedAnnotationTagBits()
/*     */   {
/*     */   }
/*     */ 
/*     */   public final boolean isValidBinding()
/*     */   {
/*  92 */     return problemId() == 0;
/*     */   }
/*     */ 
/*     */   public int problemId()
/*     */   {
/* 101 */     return 0;
/*     */   }
/*     */ 
/*     */   public abstract char[] readableName();
/*     */ 
/*     */   public char[] shortReadableName()
/*     */   {
/* 109 */     return readableName();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.Binding
 * JD-Core Version:    0.6.0
 */