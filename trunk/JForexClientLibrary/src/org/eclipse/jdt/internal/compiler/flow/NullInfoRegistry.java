/*     */ package org.eclipse.jdt.internal.compiler.flow;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ 
/*     */ public class NullInfoRegistry extends UnconditionalFlowInfo
/*     */ {
/*     */   public NullInfoRegistry(UnconditionalFlowInfo upstream)
/*     */   {
/*  41 */     this.maxFieldCount = upstream.maxFieldCount;
/*  42 */     if ((upstream.tagBits & 0x2) != 0)
/*     */     {
/*     */       long u1;
/*     */       long u2;
/*     */       long u3;
/*     */       long nu3;
/*     */       long u4;
/*     */       long nu4;
/*  44 */       this.nullBit2 = 
/*  47 */         ((u1 = upstream.nullBit1) & (
/*  45 */         u2 = upstream.nullBit2) & (
/*  46 */         nu3 = (u3 = upstream.nullBit3) ^ 0xFFFFFFFF) & (
/*  47 */         nu4 = (u4 = upstream.nullBit4) ^ 0xFFFFFFFF));
/*     */       long nu2;
/*  48 */       this.nullBit3 = (u1 & (nu2 = u2 ^ 0xFFFFFFFF) & u3 & nu4);
/*  49 */       this.nullBit4 = (u1 & nu2 & nu3 & u4);
/*  50 */       if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0L) {
/*  51 */         this.tagBits |= 2;
/*     */       }
/*  53 */       if (upstream.extra != null) {
/*  54 */         this.extra = new long[6][];
/*  55 */         int length = upstream.extra[2].length;
/*  56 */         for (int i = 2; i < 6; i++) {
/*  57 */           this.extra[i] = new long[length];
/*     */         }
/*  59 */         for (int i = 0; i < tmp279_278; i++)
/*     */         {
/*     */           long tmp208_207 = upstream.extra[2][i]; u1 = tmp208_207;
/*     */           long tmp219_218 = upstream.extra[3][i]; u2 = tmp219_218;
/*     */           long tmp239_238 = ((u3 = upstream.extra[4][i]) ^ 0xFFFFFFFF); tmp208_207 = tmp239_238;
/*     */           long tmp259_258 = ((u4 = upstream.extra[5][i]) ^ 0xFFFFFFFF); tmp239_238 = tmp259_258;
/*     */ 
/*  60 */           this.extra[3][i] = 
/*  63 */             (tmp208_207 & 
/*  61 */             tmp219_218 & 
/*  62 */             tmp239_238 & 
/*  63 */             tmp259_258);
/*     */           long tmp279_278 = (u2 ^ 0xFFFFFFFF); nu2 = tmp279_278; this.extra[4][i] = (u1 & tmp279_278 & u3 & tmp239_238);
/*  65 */           this.extra[5][i] = (u1 & nu2 & tmp208_207 & u4);
/*  66 */           if ((this.extra[3][i] | this.extra[4][i] | this.extra[5][i]) != 0L)
/*  67 */             this.tagBits |= 2;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public NullInfoRegistry add(NullInfoRegistry other)
/*     */   {
/*  81 */     if ((other.tagBits & 0x2) == 0) {
/*  82 */       return this;
/*     */     }
/*  84 */     this.tagBits |= 2;
/*  85 */     this.nullBit1 |= other.nullBit1;
/*  86 */     this.nullBit2 |= other.nullBit2;
/*  87 */     this.nullBit3 |= other.nullBit3;
/*  88 */     this.nullBit4 |= other.nullBit4;
/*  89 */     if (other.extra != null) {
/*  90 */       if (this.extra == null) {
/*  91 */         this.extra = new long[6][];
/*  92 */         int i = 2; for (int length = other.extra[2].length; i < 6; i++)
/*  93 */           System.arraycopy(other.extra[i], 0, 
/*  94 */             this.extra[i] =  = new long[length], 0, length);
/*     */       }
/*     */       else {
/*  97 */         int length = this.extra[2].length; int otherLength = other.extra[2].length;
/*  98 */         if (otherLength > length)
/*  99 */           for (int i = 2; i < 6; i++) {
/* 100 */             System.arraycopy(this.extra[i], 0, 
/* 101 */               this.extra[i] =  = new long[otherLength], 0, length);
/* 102 */             System.arraycopy(other.extra[i], length, 
/* 103 */               this.extra[i], length, otherLength - length);
/*     */           }
/* 105 */         else if (otherLength < length) {
/* 106 */           length = otherLength;
/*     */         }
/* 108 */         for (int i = 2; i < 6; i++) {
/* 109 */           for (int j = 0; j < length; j++) {
/* 110 */             this.extra[i][j] |= other.extra[i][j];
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 115 */     return this;
/*     */   }
/*     */ 
/*     */   public void markAsComparedEqualToNonNull(LocalVariableBinding local)
/*     */   {
/* 120 */     if (this != DEAD_END) {
/* 121 */       this.tagBits |= 2;
/*     */       int position;
/* 124 */       if ((position = local.id + this.maxFieldCount) < 64)
/*     */       {
/* 126 */         this.nullBit1 |= 1L << position;
/*     */       }
/*     */       else
/*     */       {
/* 135 */         int vectorIndex = position / 64 - 1;
/* 136 */         if (this.extra == null) {
/* 137 */           int length = vectorIndex + 1;
/* 138 */           this.extra = new long[6][];
/* 139 */           for (int j = 2; j < 6; j++)
/* 140 */             this.extra[j] = new long[length];
/*     */         }
/*     */         else
/*     */         {
/*     */           int oldLength;
/* 145 */           if (vectorIndex >= (oldLength = this.extra[2].length)) {
/* 146 */             for (int j = 2; j < 6; j++) {
/* 147 */               System.arraycopy(this.extra[j], 0, 
/* 148 */                 this.extra[j] =  = new long[vectorIndex + 1], 0, 
/* 149 */                 oldLength);
/*     */             }
/*     */           }
/*     */         }
/* 153 */         this.extra[2][vectorIndex] |= 1L << position % 64;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyNonNull(LocalVariableBinding local)
/*     */   {
/* 165 */     if (this != DEAD_END) {
/* 166 */       this.tagBits |= 2;
/*     */       int position;
/* 169 */       if ((position = local.id + this.maxFieldCount) < 64)
/*     */       {
/* 171 */         this.nullBit3 |= 1L << position;
/*     */       }
/*     */       else
/*     */       {
/* 180 */         int vectorIndex = position / 64 - 1;
/* 181 */         if (this.extra == null) {
/* 182 */           int length = vectorIndex + 1;
/* 183 */           this.extra = new long[6][];
/* 184 */           for (int j = 2; j < 6; j++)
/* 185 */             this.extra[j] = new long[length];
/*     */         }
/*     */         else
/*     */         {
/*     */           int oldLength;
/* 190 */           if (vectorIndex >= (oldLength = this.extra[2].length)) {
/* 191 */             for (int j = 2; j < 6; j++) {
/* 192 */               System.arraycopy(this.extra[j], 0, 
/* 193 */                 this.extra[j] =  = new long[vectorIndex + 1], 0, 
/* 194 */                 oldLength);
/*     */             }
/*     */           }
/*     */         }
/* 198 */         this.extra[4][vectorIndex] |= 1L << position % 64;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyNull(LocalVariableBinding local)
/*     */   {
/* 211 */     if (this != DEAD_END) {
/* 212 */       this.tagBits |= 2;
/*     */       int position;
/* 215 */       if ((position = local.id + this.maxFieldCount) < 64)
/*     */       {
/* 217 */         this.nullBit2 |= 1L << position;
/*     */       }
/*     */       else
/*     */       {
/* 226 */         int vectorIndex = position / 64 - 1;
/* 227 */         if (this.extra == null) {
/* 228 */           int length = vectorIndex + 1;
/* 229 */           this.extra = new long[6][];
/* 230 */           for (int j = 2; j < 6; j++)
/* 231 */             this.extra[j] = new long[length];
/*     */         }
/*     */         else
/*     */         {
/*     */           int oldLength;
/* 236 */           if (vectorIndex >= (oldLength = this.extra[2].length)) {
/* 237 */             for (int j = 2; j < 6; j++) {
/* 238 */               System.arraycopy(this.extra[j], 0, 
/* 239 */                 this.extra[j] =  = new long[vectorIndex + 1], 0, 
/* 240 */                 oldLength);
/*     */             }
/*     */           }
/*     */         }
/* 244 */         this.extra[3][vectorIndex] |= 1L << position % 64;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void markAsDefinitelyUnknown(LocalVariableBinding local)
/*     */   {
/* 256 */     if (this != DEAD_END) {
/* 257 */       this.tagBits |= 2;
/*     */       int position;
/* 260 */       if ((position = local.id + this.maxFieldCount) < 64)
/*     */       {
/* 262 */         this.nullBit4 |= 1L << position;
/*     */       }
/*     */       else
/*     */       {
/* 271 */         int vectorIndex = position / 64 - 1;
/* 272 */         if (this.extra == null) {
/* 273 */           int length = vectorIndex + 1;
/* 274 */           this.extra = new long[6][];
/* 275 */           for (int j = 2; j < 6; j++)
/* 276 */             this.extra[j] = new long[length];
/*     */         }
/*     */         else
/*     */         {
/*     */           int oldLength;
/* 281 */           if (vectorIndex >= (oldLength = this.extra[2].length)) {
/* 282 */             for (int j = 2; j < 6; j++) {
/* 283 */               System.arraycopy(this.extra[j], 0, 
/* 284 */                 this.extra[j] =  = new long[vectorIndex + 1], 0, 
/* 285 */                 oldLength);
/*     */             }
/*     */           }
/*     */         }
/* 289 */         this.extra[5][vectorIndex] |= 1L << position % 64;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public UnconditionalFlowInfo mitigateNullInfoOf(FlowInfo flowInfo)
/*     */   {
/* 310 */     if ((this.tagBits & 0x2) == 0) {
/* 311 */       return flowInfo.unconditionalInits();
/*     */     }
/*     */ 
/* 314 */     boolean newCopy = false;
/* 315 */     UnconditionalFlowInfo source = flowInfo.unconditionalInits();
/*     */     long s1;
/*     */     long s3;
/*     */     long s4;
/*     */     long a2;
/*     */     long a4;
/* 317 */     long m1 = (s1 = source.nullBit1) & (s3 = source.nullBit3) & (
/* 318 */       s4 = source.nullBit4) & (
/* 320 */       (a2 = this.nullBit2) | (a4 = this.nullBit4));
/*     */     long s2;
/*     */     long a3;
/* 322 */     long m2 = s1 & (s2 = this.nullBit2) & (s3 ^ s4) & (
/* 324 */       (a3 = this.nullBit3) | a4);
/*     */     long ns3;
/*     */     long ns4;
/*     */     long ns2;
/* 329 */     long m3 = s1 & (s2 & (ns3 = s3 ^ 0xFFFFFFFF) & (ns4 = s4 ^ 0xFFFFFFFF) & (a3 | a4) | 
/* 330 */       (ns2 = s2 ^ 0xFFFFFFFF) & s3 & ns4 & (a2 | a4) | 
/* 331 */       ns2 & ns3 & s4 & (a2 | a3));
/*     */     long m;
/* 332 */     if ((m = m1 | m2 | m3) != 0L) {
/* 333 */       newCopy = true;
/* 334 */       source = source.unconditionalCopy();
/* 335 */       source.nullBit1 &= (m ^ 0xFFFFFFFF);
/*     */       long nm1;
/*     */       long nm2;
/* 336 */       source.nullBit2 &= (nm1 = m1 ^ 0xFFFFFFFF) & ((nm2 = m2 ^ 0xFFFFFFFF) | a4);
/* 337 */       source.nullBit3 &= (nm1 | a2) & nm2;
/* 338 */       source.nullBit4 &= nm1 & nm2;
/*     */     }
/* 340 */     if ((this.extra != null) && (source.extra != null)) {
/* 341 */       int length = this.extra[2].length; int sourceLength = source.extra[0].length;
/* 342 */       if (sourceLength < length) {
/* 343 */         length = sourceLength;
/*     */       }
/* 345 */       for (int i = 0; i < length; i++) {
/* 346 */         m1 = (s1 = source.extra[2][i]) & (s3 = source.extra[4][i]) & (
/* 347 */           s4 = source.extra[5][i]) & (
/* 348 */           (a2 = this.extra[3][i]) | (a4 = this.extra[5][i]));
/* 349 */         m2 = s1 & (s2 = this.extra[3][i]) & (s3 ^ s4) & (
/* 350 */           (a3 = this.extra[4][i]) | a4);
/* 351 */         m3 = s1 & (s2 & (ns3 = s3 ^ 0xFFFFFFFF) & (ns4 = s4 ^ 0xFFFFFFFF) & (a3 | a4) | 
/* 352 */           (ns2 = s2 ^ 0xFFFFFFFF) & s3 & ns4 & (a2 | a4) | 
/* 353 */           ns2 & ns3 & s4 & (a2 | a3));
/* 354 */         if ((m = m1 | m2 | m3) != 0L) {
/* 355 */           if (!newCopy) {
/* 356 */             newCopy = true;
/* 357 */             source = source.unconditionalCopy();
/*     */           }
/* 359 */           source.extra[2][i] &= (m ^ 0xFFFFFFFF);
/*     */           long tmp565_564 = (m1 ^ 0xFFFFFFFF); long nm1 = tmp565_564;
/*     */           long tmp574_573 = (m2 ^ 0xFFFFFFFF); long nm2 = tmp574_573; source.extra[3][i] &= tmp565_564 & (tmp574_573 | a4);
/* 361 */           source.extra[4][i] &= (nm1 | a2) & nm2;
/* 362 */           source.extra[5][i] &= nm1 & nm2;
/*     */         }
/*     */       }
/*     */     }
/* 366 */     return source;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 370 */     if (this.extra == null) {
/* 371 */       return "NullInfoRegistry<" + this.nullBit1 + 
/* 372 */         this.nullBit2 + this.nullBit3 + this.nullBit4 + 
/* 373 */         ">";
/*     */     }
/*     */ 
/* 376 */     String nullS = "NullInfoRegistry<[" + this.nullBit1 + 
/* 377 */       this.nullBit2 + this.nullBit3 + this.nullBit4;
/*     */ 
/* 379 */     int i = 0;
/*     */ 
/* 381 */     int ceil = this.extra[0].length > 3 ? 
/* 380 */       3 : 
/* 381 */       this.extra[0].length;
/* 382 */     for (; i < ceil; i++) {
/* 383 */       nullS = nullS + "," + this.extra[2][i] + 
/* 384 */         this.extra[3][i] + this.extra[4][i] + this.extra[5][i];
/*     */     }
/* 386 */     if (ceil < this.extra[0].length) {
/* 387 */       nullS = nullS + ",...";
/*     */     }
/* 389 */     return nullS + "]>";
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.NullInfoRegistry
 * JD-Core Version:    0.6.0
 */