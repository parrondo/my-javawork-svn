/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ public final class BaseTypeBinding extends TypeBinding
/*     */ {
/*  24 */   public static final int[] CONVERSIONS = initializeConversions();
/*     */   public static final int IDENTITY = 1;
/*     */   public static final int WIDENING = 2;
/*     */   public static final int NARROWING = 4;
/*     */   public static final int MAX_CONVERSIONS = 256;
/*     */   public char[] simpleName;
/*     */   private char[] constantPoolName;
/*     */ 
/*     */   public static final int[] initializeConversions()
/*     */   {
/*  31 */     int[] table = new int[256];
/*     */ 
/*  33 */     table[85] = 1;
/*     */ 
/*  35 */     table[51] = 1;
/*  36 */     table[67] = 2;
/*  37 */     table[35] = 4;
/*  38 */     table['£'] = 2;
/*  39 */     table[115] = 2;
/*  40 */     table[''] = 2;
/*  41 */     table[''] = 2;
/*     */ 
/*  43 */     table[52] = 4;
/*  44 */     table[68] = 1;
/*  45 */     table[36] = 4;
/*  46 */     table['¤'] = 2;
/*  47 */     table[116] = 2;
/*  48 */     table[''] = 2;
/*  49 */     table[''] = 2;
/*     */ 
/*  51 */     table[50] = 4;
/*  52 */     table[66] = 4;
/*  53 */     table[34] = 1;
/*  54 */     table['¢'] = 2;
/*  55 */     table[114] = 2;
/*  56 */     table[''] = 2;
/*  57 */     table[''] = 2;
/*     */ 
/*  59 */     table[58] = 4;
/*  60 */     table[74] = 4;
/*  61 */     table[42] = 4;
/*  62 */     table['ª'] = 1;
/*  63 */     table[122] = 2;
/*  64 */     table[''] = 2;
/*  65 */     table[''] = 2;
/*     */ 
/*  67 */     table[55] = 4;
/*  68 */     table[71] = 4;
/*  69 */     table[39] = 4;
/*  70 */     table['§'] = 4;
/*  71 */     table[119] = 1;
/*  72 */     table[''] = 2;
/*  73 */     table[''] = 2;
/*     */ 
/*  75 */     table[57] = 4;
/*  76 */     table[73] = 4;
/*  77 */     table[41] = 4;
/*  78 */     table['©'] = 4;
/*  79 */     table[121] = 4;
/*  80 */     table[''] = 1;
/*  81 */     table[''] = 2;
/*     */ 
/*  83 */     table[56] = 4;
/*  84 */     table[72] = 4;
/*  85 */     table[40] = 4;
/*  86 */     table['¨'] = 4;
/*  87 */     table[120] = 4;
/*  88 */     table[''] = 4;
/*  89 */     table[''] = 1;
/*     */ 
/*  91 */     return table;
/*     */   }
/*     */ 
/*     */   public static final boolean isNarrowing(int left, int right)
/*     */   {
/* 101 */     int right2left = right + (left << 4);
/*     */ 
/* 104 */     return (right2left >= 0) && 
/* 103 */       (right2left < 256) && 
/* 104 */       ((CONVERSIONS[right2left] & 0x5) != 0);
/*     */   }
/*     */ 
/*     */   public static final boolean isWidening(int left, int right)
/*     */   {
/* 115 */     int right2left = right + (left << 4);
/*     */ 
/* 118 */     return (right2left >= 0) && 
/* 117 */       (right2left < 256) && 
/* 118 */       ((CONVERSIONS[right2left] & 0x3) != 0);
/*     */   }
/*     */ 
/*     */   BaseTypeBinding(int id, char[] name, char[] constantPoolName)
/*     */   {
/* 126 */     this.tagBits |= 2L;
/* 127 */     this.id = id;
/* 128 */     this.simpleName = name;
/* 129 */     this.constantPoolName = constantPoolName;
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/* 136 */     return constantPoolName();
/*     */   }
/*     */ 
/*     */   public char[] constantPoolName()
/*     */   {
/* 143 */     return this.constantPoolName;
/*     */   }
/*     */ 
/*     */   public PackageBinding getPackage()
/*     */   {
/* 148 */     return null;
/*     */   }
/*     */ 
/*     */   public final boolean isCompatibleWith(TypeBinding left)
/*     */   {
/* 154 */     if (this == left)
/* 155 */       return true;
/* 156 */     int right2left = this.id + (left.id << 4);
/* 157 */     if ((right2left >= 0) && 
/* 158 */       (right2left < 256) && 
/* 159 */       ((CONVERSIONS[right2left] & 0x3) != 0))
/* 160 */       return true;
/* 161 */     return (this == TypeBinding.NULL) && (!left.isBaseType());
/*     */   }
/*     */ 
/*     */   public boolean isUncheckedException(boolean includeSupertype)
/*     */   {
/* 169 */     return this == TypeBinding.NULL;
/*     */   }
/*     */ 
/*     */   public int kind()
/*     */   {
/* 176 */     return 132;
/*     */   }
/*     */   public char[] qualifiedSourceName() {
/* 179 */     return this.simpleName;
/*     */   }
/*     */ 
/*     */   public char[] readableName() {
/* 183 */     return this.simpleName;
/*     */   }
/*     */ 
/*     */   public char[] shortReadableName() {
/* 187 */     return this.simpleName;
/*     */   }
/*     */ 
/*     */   public char[] sourceName() {
/* 191 */     return this.simpleName;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 195 */     return new String(this.constantPoolName) + " (id=" + this.id + ")";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding
 * JD-Core Version:    0.6.0
 */