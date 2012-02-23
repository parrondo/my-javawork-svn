/*     */ package org.eclipse.jdt.internal.compiler.impl;
/*     */ 
/*     */ public class IrritantSet
/*     */ {
/*     */   public static final int GROUP_MASK = -536870912;
/*     */   public static final int GROUP_SHIFT = 29;
/*     */   public static final int GROUP_MAX = 3;
/*     */   public static final int GROUP0 = 0;
/*     */   public static final int GROUP1 = 536870912;
/*     */   public static final int GROUP2 = 1073741824;
/*  39 */   public static final IrritantSet ALL = new IrritantSet(
/*  40 */     536870911);
/*     */ 
/*  41 */   public static final IrritantSet BOXING = new IrritantSet(
/*  42 */     536871168);
/*     */ 
/*  43 */   public static final IrritantSet CAST = new IrritantSet(
/*  44 */     67108864);
/*     */ 
/*  45 */   public static final IrritantSet DEPRECATION = new IrritantSet(
/*  46 */     4);
/*     */ 
/*  47 */   public static final IrritantSet DEP_ANN = new IrritantSet(
/*  48 */     536879104);
/*     */ 
/*  49 */   public static final IrritantSet FALLTHROUGH = new IrritantSet(
/*  50 */     537395200);
/*     */ 
/*  51 */   public static final IrritantSet FINALLY = new IrritantSet(
/*  52 */     16777216);
/*     */ 
/*  53 */   public static final IrritantSet HIDING = new IrritantSet(
/*  54 */     8);
/*     */ 
/*  56 */   public static final IrritantSet INCOMPLETE_SWITCH = new IrritantSet(
/*  57 */     536875008);
/*     */ 
/*  58 */   public static final IrritantSet NLS = new IrritantSet(
/*  59 */     256);
/*     */ 
/*  60 */   public static final IrritantSet NULL = new IrritantSet(
/*  61 */     536871040);
/*     */ 
/*  63 */   public static final IrritantSet RESTRICTION = new IrritantSet(
/*  64 */     536870944);
/*     */ 
/*  66 */   public static final IrritantSet SERIAL = new IrritantSet(
/*  67 */     536870920);
/*     */ 
/*  68 */   public static final IrritantSet STATIC_ACCESS = new IrritantSet(
/*  69 */     268435456);
/*     */ 
/*  71 */   public static final IrritantSet SYNTHETIC_ACCESS = new IrritantSet(
/*  72 */     128);
/*     */ 
/*  73 */   public static final IrritantSet SUPER = new IrritantSet(
/*  74 */     537919488);
/*     */ 
/*  75 */   public static final IrritantSet UNUSED = new IrritantSet(
/*  76 */     16);
/*     */ 
/*  78 */   public static final IrritantSet UNCHECKED = new IrritantSet(
/*  79 */     536870914);
/*     */ 
/*  81 */   public static final IrritantSet UNQUALIFIED_FIELD_ACCESS = new IrritantSet(
/*  82 */     4194304);
/*     */ 
/*  84 */   public static final IrritantSet COMPILER_DEFAULT_ERRORS = new IrritantSet(0);
/*  85 */   public static final IrritantSet COMPILER_DEFAULT_WARNINGS = new IrritantSet(0);
/*     */ 
/* 146 */   private int[] bits = new int[3];
/*     */ 
/*     */   static
/*     */   {
/*  87 */     COMPILER_DEFAULT_WARNINGS
/*  89 */       .set(
/*  90 */       16838239)
/* 104 */       .set(
/* 105 */       721667838)
/* 122 */       .set(1073741826);
/*     */ 
/* 124 */     ALL.setAll();
/* 125 */     HIDING
/* 126 */       .set(131072)
/* 127 */       .set(65536)
/* 128 */       .set(536871936);
/* 129 */     NULL
/* 130 */       .set(538968064)
/* 131 */       .set(541065216);
/* 132 */     RESTRICTION.set(536887296);
/* 133 */     STATIC_ACCESS.set(2048);
/* 134 */     UNUSED
/* 135 */       .set(32)
/* 136 */       .set(32768)
/* 137 */       .set(8388608)
/* 138 */       .set(537001984)
/* 139 */       .set(1024)
/* 140 */       .set(553648128)
/* 141 */       .set(603979776);
/* 142 */     UNCHECKED.set(536936448);
/*     */   }
/*     */ 
/*     */   public IrritantSet(int singleGroupIrritants)
/*     */   {
/* 152 */     initialize(singleGroupIrritants);
/*     */   }
/*     */ 
/*     */   public IrritantSet(IrritantSet other)
/*     */   {
/* 159 */     initialize(other);
/*     */   }
/*     */ 
/*     */   public boolean areAllSet() {
/* 163 */     for (int i = 0; i < 3; i++) {
/* 164 */       if (this.bits[i] != 536870911)
/* 165 */         return false;
/*     */     }
/* 167 */     return true;
/*     */   }
/*     */ 
/*     */   public IrritantSet clear(int singleGroupIrritants) {
/* 171 */     int group = (singleGroupIrritants & 0xE0000000) >> 29;
/* 172 */     this.bits[group] &= (singleGroupIrritants ^ 0xFFFFFFFF);
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */   public IrritantSet clearAll() {
/* 177 */     for (int i = 0; i < 3; i++) {
/* 178 */       this.bits[i] = 0;
/*     */     }
/* 180 */     return this;
/*     */   }
/*     */ 
/*     */   public void initialize(int singleGroupIrritants)
/*     */   {
/* 189 */     if (singleGroupIrritants == 0)
/* 190 */       return;
/* 191 */     int group = (singleGroupIrritants & 0xE0000000) >> 29;
/* 192 */     this.bits[group] = (singleGroupIrritants & 0x1FFFFFFF);
/*     */   }
/*     */ 
/*     */   public void initialize(IrritantSet other) {
/* 196 */     if (other == null)
/* 197 */       return;
/* 198 */     System.arraycopy(other.bits, 0, this.bits = new int[3], 0, 3);
/*     */   }
/*     */ 
/*     */   public boolean isAnySet(IrritantSet other)
/*     */   {
/* 206 */     if (other == null)
/* 207 */       return false;
/* 208 */     for (int i = 0; i < 3; i++) {
/* 209 */       if ((this.bits[i] & other.bits[i]) != 0)
/* 210 */         return true;
/*     */     }
/* 212 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isSet(int singleGroupIrritants) {
/* 216 */     int group = (singleGroupIrritants & 0xE0000000) >> 29;
/* 217 */     return (this.bits[group] & singleGroupIrritants) != 0;
/*     */   }
/*     */ 
/*     */   public IrritantSet set(int singleGroupIrritants) {
/* 221 */     int group = (singleGroupIrritants & 0xE0000000) >> 29;
/* 222 */     this.bits[group] |= singleGroupIrritants & 0x1FFFFFFF;
/* 223 */     return this;
/*     */   }
/*     */ 
/*     */   public IrritantSet set(IrritantSet other)
/*     */   {
/* 232 */     if (other == null)
/* 233 */       return this;
/* 234 */     boolean wasNoOp = true;
/* 235 */     for (int i = 0; i < 3; i++) {
/* 236 */       int otherIrritant = other.bits[i] & 0x1FFFFFFF;
/*     */ 
/* 239 */       if ((this.bits[i] & otherIrritant) != otherIrritant) {
/* 240 */         wasNoOp = false;
/* 241 */         this.bits[i] |= otherIrritant;
/*     */       }
/*     */     }
/* 244 */     return wasNoOp ? null : this;
/*     */   }
/*     */ 
/*     */   public IrritantSet setAll() {
/* 248 */     for (int i = 0; i < 3; i++) {
/* 249 */       this.bits[i] |= 536870911;
/*     */     }
/*     */ 
/* 252 */     return this;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.IrritantSet
 * JD-Core Version:    0.6.0
 */