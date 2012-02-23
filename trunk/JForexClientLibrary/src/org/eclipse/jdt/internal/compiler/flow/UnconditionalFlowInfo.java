/*      */ package org.eclipse.jdt.internal.compiler.flow;
/*      */ 
/*      */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ 
/*      */ public class UnconditionalFlowInfo extends FlowInfo
/*      */ {
/*      */   public static final boolean COVERAGE_TEST_FLAG = false;
/*      */   public static int CoverageTestId;
/*      */   public long definiteInits;
/*      */   public long potentialInits;
/*      */   public long nullBit1;
/*      */   public long nullBit2;
/*      */   public long nullBit3;
/*      */   public long nullBit4;
/*      */   public static final int extraLength = 6;
/*      */   public long[][] extra;
/*      */   public int maxFieldCount;
/*      */   public static final int BitCacheSize = 64;
/*      */ 
/*      */   public FlowInfo addInitializationsFrom(FlowInfo inits)
/*      */   {
/*   89 */     if (this == DEAD_END)
/*   90 */       return this;
/*   91 */     if (inits == DEAD_END)
/*   92 */       return this;
/*   93 */     UnconditionalFlowInfo otherInits = inits.unconditionalInits();
/*      */ 
/*   96 */     this.definiteInits |= otherInits.definiteInits;
/*      */ 
/*   98 */     this.potentialInits |= otherInits.potentialInits;
/*      */ 
/*  100 */     boolean thisHadNulls = (this.tagBits & 0x2) != 0;
/*  101 */     boolean otherHasNulls = (otherInits.tagBits & 0x2) != 0;
/*      */ 
/*  107 */     if (otherHasNulls) {
/*  108 */       if (!thisHadNulls) {
/*  109 */         this.nullBit1 = otherInits.nullBit1;
/*  110 */         this.nullBit2 = otherInits.nullBit2;
/*  111 */         this.nullBit3 = otherInits.nullBit3;
/*  112 */         this.nullBit4 = otherInits.nullBit4;
/*      */       }
/*      */       else
/*      */       {
/*      */         long b1;
/*      */         long a1;
/*      */         long a3;
/*      */         long a4;
/*      */         long b2;
/*      */         long nb2;
/*      */         long b4;
/*      */         long nb4;
/*      */         long na4;
/*      */         long na3;
/*      */         long a2;
/*      */         long na2;
/*      */         long b3;
/*      */         long nb3;
/*  120 */         this.nullBit1 = 
/*  121 */           ((b1 = otherInits.nullBit1) | 
/*  121 */           (a1 = this.nullBit1) & ((a3 = this.nullBit3) & (
/*  122 */           a4 = this.nullBit4) & (nb2 = (b2 = otherInits.nullBit2) ^ 0xFFFFFFFF) & (
/*  123 */           nb4 = (b4 = otherInits.nullBit4) ^ 0xFFFFFFFF) | 
/*  124 */           ((na4 = a4 ^ 0xFFFFFFFF) | (na3 = a3 ^ 0xFFFFFFFF)) & (
/*  125 */           (na2 = (a2 = this.nullBit2) ^ 0xFFFFFFFF) & nb2 | 
/*  126 */           a2 & (nb3 = (b3 = otherInits.nullBit3) ^ 0xFFFFFFFF) & nb4)));
/*      */         long nb1;
/*      */         long na1;
/*  127 */         this.nullBit2 = 
/*  129 */           (b2 & (nb4 | nb3) | 
/*  128 */           na3 & na4 & b2 | 
/*  129 */           a2 & (nb3 & nb4 | 
/*  130 */           (nb1 = b1 ^ 0xFFFFFFFF) & (na3 | (na1 = a1 ^ 0xFFFFFFFF)) | 
/*  131 */           a1 & b2));
/*  132 */         this.nullBit3 = 
/*  136 */           (b3 & (nb1 & (b2 | a2 | na1) | 
/*  133 */           b1 & (b4 | nb2 | a1 & a3) | 
/*  134 */           na1 & na2 & na4) | 
/*  135 */           a3 & nb2 & nb4 | 
/*  136 */           nb1 & ((na2 & a4 | na1) & a3 | 
/*  137 */           a1 & na2 & na4 & b2));
/*  138 */         this.nullBit4 = 
/*  146 */           (nb1 & (a4 & (na3 & nb3 | (a3 | na2) & nb2) | 
/*  139 */           a1 & (a3 & nb2 & b4 | 
/*  140 */           a2 & b2 & (b4 | a3 & na4 & nb3))) | 
/*  141 */           b1 & (a3 & a4 & b4 | 
/*  142 */           na2 & na4 & nb3 & b4 | 
/*  143 */           a2 & ((b3 | a4) & b4 | 
/*  144 */           na3 & a4 & b2 & b3) | 
/*  145 */           na1 & (b4 | (a4 | a2) & b2 & b3)) | 
/*  146 */           (na1 & (na3 & nb3 | na2 & nb2) | 
/*  147 */           a1 & (nb2 & nb3 | a2 & a3)) & b4);
/*      */       }
/*      */ 
/*  154 */       this.tagBits |= 2;
/*      */     }
/*      */ 
/*  157 */     if ((this.extra != null) || (otherInits.extra != null)) {
/*  158 */       int mergeLimit = 0; int copyLimit = 0;
/*  159 */       if (this.extra != null) {
/*  160 */         if (otherInits.extra != null)
/*      */         {
/*      */           int length;
/*      */           int otherLength;
/*  164 */           if ((length = this.extra[0].length) < 
/*  164 */             (otherLength = otherInits.extra[0].length))
/*      */           {
/*  166 */             for (int j = 0; j < 6; j++) {
/*  167 */               System.arraycopy(this.extra[j], 0, 
/*  168 */                 this.extra[j] =  = new long[otherLength], 0, length);
/*      */             }
/*  170 */             mergeLimit = length;
/*  171 */             copyLimit = otherLength;
/*      */           }
/*      */           else
/*      */           {
/*  179 */             mergeLimit = otherLength;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*  187 */       else if (otherInits.extra != null)
/*      */       {
/*  191 */         this.extra = new long[6][];
/*      */         int otherLength;
/*  192 */         System.arraycopy(otherInits.extra[0], 0, 
/*  193 */           this.extra[0] =  = new long[otherLength = 
/*  194 */           otherInits.extra[0].length], 0, otherLength);
/*  195 */         System.arraycopy(otherInits.extra[1], 0, 
/*  196 */           this.extra[1] =  = new long[otherLength], 0, otherLength);
/*  197 */         if (otherHasNulls) {
/*  198 */           for (int j = 2; j < 6; j++) {
/*  199 */             System.arraycopy(otherInits.extra[j], 0, 
/*  200 */               this.extra[j] =  = new long[otherLength], 0, otherLength);
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*  209 */           for (int j = 2; j < 6; j++) {
/*  210 */             this.extra[j] = new long[otherLength];
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  221 */       for (int i = 0; i < mergeLimit; i++) {
/*  222 */         this.extra[0][i] |= otherInits.extra[0][i];
/*  223 */         this.extra[1][i] |= otherInits.extra[1][i];
/*      */       }
/*  225 */       for (; i < copyLimit; i++) {
/*  226 */         this.extra[0][i] = otherInits.extra[0][i];
/*  227 */         this.extra[1][i] = otherInits.extra[1][i];
/*      */       }
/*      */ 
/*  230 */       if (!thisHadNulls) {
/*  231 */         if (copyLimit < mergeLimit) {
/*  232 */           copyLimit = mergeLimit;
/*      */         }
/*  234 */         mergeLimit = 0;
/*      */       }
/*  236 */       if (!otherHasNulls) {
/*  237 */         copyLimit = 0;
/*  238 */         mergeLimit = 0;
/*      */       }
/*  240 */       for (i = 0; i < mergeLimit; i++)
/*      */       {
/*      */         long tmp973_972 = otherInits.extra[2][i]; long b1 = tmp973_972;
/*      */         long tmp985_984 = this.extra[2][i]; long a1 = tmp985_984;
/*      */         long tmp997_996 = this.extra[4][i]; long a3 = tmp997_996;
/*      */         long tmp1009_1008 = this.extra[5][i]; long a4 = tmp1009_1008;
/*      */         long b2;
/*      */         long tmp1029_1028 = ((b2 = otherInits.extra[3][i]) ^ 0xFFFFFFFF); long nb2 = tmp1029_1028;
/*      */         long b4;
/*      */         long tmp1049_1048 = ((b4 = otherInits.extra[5][i]) ^ 0xFFFFFFFF); long nb4 = tmp1049_1048;
/*      */         long tmp1059_1058 = (a4 ^ 0xFFFFFFFF); long na4 = tmp1059_1058;
/*      */         long tmp1068_1067 = (a3 ^ 0xFFFFFFFF); long na3 = tmp1068_1067;
/*      */         long a2;
/*      */         long tmp1088_1087 = ((a2 = this.extra[3][i]) ^ 0xFFFFFFFF); long na2 = tmp1088_1087;
/*      */         long b3;
/*      */         long tmp1112_1111 = ((b3 = otherInits.extra[4][i]) ^ 0xFFFFFFFF); long nb3 = tmp1112_1111;
/*      */ 
/*  241 */         this.extra[2][i] = 
/*  242 */           (tmp973_972 | 
/*  242 */           tmp985_984 & (tmp997_996 & 
/*  243 */           tmp1009_1008 & tmp1029_1028 & 
/*  244 */           tmp1049_1048 | 
/*  245 */           (tmp1059_1058 | tmp1068_1067) & (
/*  246 */           tmp1088_1087 & nb2 | 
/*  247 */           a2 & tmp1112_1111 & nb4)));
/*      */         long tmp1163_1162 = (b1 ^ 0xFFFFFFFF); long nb1 = tmp1163_1162;
/*      */         long tmp1174_1173 = (a1 ^ 0xFFFFFFFF); long na1 = tmp1174_1173;
/*      */ 
/*  248 */         this.extra[3][i] = 
/*  250 */           (b2 & (nb4 | nb3) | 
/*  249 */           na3 & na4 & b2 | 
/*  250 */           a2 & (nb3 & nb4 | 
/*  251 */           tmp1163_1162 & (na3 | tmp1174_1173) | 
/*  252 */           a1 & b2));
/*  253 */         this.extra[4][i] = 
/*  257 */           (b3 & (nb1 & (b2 | a2 | na1) | 
/*  254 */           b1 & (b4 | nb2 | a1 & a3) | 
/*  255 */           na1 & na2 & na4) | 
/*  256 */           a3 & nb2 & nb4 | 
/*  257 */           nb1 & ((na2 & a4 | na1) & a3 | 
/*  258 */           a1 & na2 & na4 & b2));
/*  259 */         this.extra[5][i] = 
/*  267 */           (nb1 & (a4 & (na3 & nb3 | (a3 | na2) & nb2) | 
/*  260 */           a1 & (a3 & nb2 & b4 | 
/*  261 */           a2 & b2 & (b4 | a3 & na4 & nb3))) | 
/*  262 */           b1 & (a3 & a4 & b4 | 
/*  263 */           na2 & na4 & nb3 & b4 | 
/*  264 */           a2 & ((b3 | a4) & b4 | 
/*  265 */           na3 & a4 & b2 & b3) | 
/*  266 */           na1 & (b4 | (a4 | a2) & b2 & b3)) | 
/*  267 */           (na1 & (na3 & nb3 | na2 & nb2) | 
/*  268 */           a1 & (nb2 & nb3 | a2 & a3)) & b4);
/*      */       }
/*      */ 
/*  275 */       for (; i < copyLimit; i++) {
/*  276 */         for (int j = 2; j < 6; j++) {
/*  277 */           this.extra[j][i] = otherInits.extra[j][i];
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  286 */     return this;
/*      */   }
/*      */ 
/*      */   public FlowInfo addPotentialInitializationsFrom(FlowInfo inits) {
/*  290 */     if (this == DEAD_END) {
/*  291 */       return this;
/*      */     }
/*  293 */     if (inits == DEAD_END) {
/*  294 */       return this;
/*      */     }
/*  296 */     UnconditionalFlowInfo otherInits = inits.unconditionalInits();
/*      */ 
/*  298 */     this.potentialInits |= otherInits.potentialInits;
/*      */ 
/*  300 */     if (this.extra != null) {
/*  301 */       if (otherInits.extra != null)
/*      */       {
/*  303 */         int i = 0;
/*      */         int length;
/*      */         int otherLength;
/*  304 */         if ((length = this.extra[0].length) < (otherLength = otherInits.extra[0].length))
/*      */         {
/*  306 */           for (int j = 0; j < 6; j++) {
/*  307 */             System.arraycopy(this.extra[j], 0, 
/*  308 */               this.extra[j] =  = new long[otherLength], 0, length);
/*      */           }
/*  310 */           for (; i < length; i++) {
/*  311 */             this.extra[1][i] |= otherInits.extra[1][i];
/*      */           }
/*  313 */           for (; i < otherLength; i++)
/*  314 */             this.extra[1][i] = otherInits.extra[1][i];
/*      */         }
/*      */         else
/*      */         {
/*      */           do
/*      */           {
/*  320 */             this.extra[1][i] |= otherInits.extra[1][i];
/*      */ 
/*  319 */             i++; } while (i < otherLength);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*  325 */     else if (otherInits.extra != null)
/*      */     {
/*  327 */       int otherLength = otherInits.extra[0].length;
/*  328 */       this.extra = new long[6][];
/*  329 */       for (int j = 0; j < 6; j++) {
/*  330 */         this.extra[j] = new long[otherLength];
/*      */       }
/*  332 */       System.arraycopy(otherInits.extra[1], 0, this.extra[1], 0, 
/*  333 */         otherLength);
/*      */     }
/*  335 */     addPotentialNullInfoFrom(otherInits);
/*  336 */     return this;
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo addPotentialNullInfoFrom(UnconditionalFlowInfo otherInits)
/*      */   {
/*  352 */     if (((this.tagBits & 0x1) != 0) || 
/*  353 */       ((otherInits.tagBits & 0x1) != 0) || 
/*  354 */       ((otherInits.tagBits & 0x2) == 0)) {
/*  355 */       return this;
/*      */     }
/*      */ 
/*  358 */     boolean thisHadNulls = (this.tagBits & 0x2) != 0;
/*  359 */     boolean thisHasNulls = false;
/*      */     long b2;
/*      */     long b3;
/*      */     long nb3;
/*      */     long b1;
/*      */     long nb1;
/*      */     long nb2;
/*      */     long b4;
/*  364 */     if (thisHadNulls)
/*      */     {
/*      */       long a1;
/*      */       long a3;
/*      */       long a4;
/*      */       long b2;
/*      */       long nb2;
/*      */       long b4;
/*      */       long nb4;
/*      */       long b1;
/*      */       long b3;
/*      */       long a2;
/*      */       long na2;
/*      */       long na4;
/*      */       long na3;
/*      */       long nb3;
/*  365 */       this.nullBit1 = 
/*  366 */         ((a1 = this.nullBit1) & (
/*  366 */         (a3 = this.nullBit3) & (a4 = this.nullBit4) & (
/*  367 */         (nb2 = (b2 = otherInits.nullBit2) ^ 0xFFFFFFFF) & (
/*  368 */         nb4 = (b4 = otherInits.nullBit4) ^ 0xFFFFFFFF) | 
/*  369 */         (b1 = otherInits.nullBit1) & (b3 = otherInits.nullBit3)) | 
/*  370 */         (na2 = (a2 = this.nullBit2) ^ 0xFFFFFFFF) & (
/*  371 */         b1 & b3 | ((na4 = a4 ^ 0xFFFFFFFF) | (na3 = a3 ^ 0xFFFFFFFF)) & nb2) | 
/*  372 */         a2 & ((na4 | na3) & ((nb3 = b3 ^ 0xFFFFFFFF) & nb4 | b1 & b2))));
/*      */       long nb1;
/*      */       long na1;
/*  373 */       this.nullBit2 = 
/*  374 */         (b2 & (nb3 | (nb1 = b1 ^ 0xFFFFFFFF)) | 
/*  374 */         a2 & (nb3 & nb4 | b2 | na3 | (na1 = a1 ^ 0xFFFFFFFF)));
/*  375 */       this.nullBit3 = 
/*  380 */         (b3 & (nb1 & b2 | 
/*  376 */         a2 & (nb2 | a3) | 
/*  377 */         na1 & nb2 | 
/*  378 */         a1 & na2 & na4 & b1) | 
/*  379 */         a3 & (nb2 & nb4 | na2 & a4 | na1) | 
/*  380 */         a1 & na2 & na4 & b2);
/*  381 */       this.nullBit4 = 
/*  384 */         (na3 & (nb1 & nb3 & b4 | 
/*  382 */         a4 & (nb3 | b1 & b2)) | 
/*  383 */         nb2 & (na3 & b1 & nb3 | na2 & (nb1 & b4 | b1 & nb3 | a4)) | 
/*  384 */         a3 & (a4 & (nb2 | b1 & b3) | 
/*  385 */         a1 & a2 & (nb1 & b4 | na4 & (b2 | b1) & nb3)));
/*      */ 
/*  391 */       if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0L)
/*  392 */         thisHasNulls = true;
/*      */     }
/*      */     else {
/*  395 */       this.nullBit1 = 0L;
/*  396 */       this.nullBit2 = 
/*  397 */         ((b2 = otherInits.nullBit2) & (
/*  397 */         (nb3 = (b3 = otherInits.nullBit3) ^ 0xFFFFFFFF) | (
/*  398 */         nb1 = (b1 = otherInits.nullBit1) ^ 0xFFFFFFFF)));
/*  399 */       this.nullBit3 = (b3 & (nb1 | (nb2 = b2 ^ 0xFFFFFFFF)));
/*  400 */       this.nullBit4 = ((b1 ^ 0xFFFFFFFF) & (b3 ^ 0xFFFFFFFF) & (b4 = otherInits.nullBit4) | (b2 ^ 0xFFFFFFFF) & (b1 & (b3 ^ 0xFFFFFFFF) | (b1 ^ 0xFFFFFFFF) & b4));
/*      */ 
/*  406 */       if ((this.nullBit2 | this.nullBit3 | this.nullBit4) != 0L) {
/*  407 */         thisHasNulls = true;
/*      */       }
/*      */     }
/*      */ 
/*  411 */     if (otherInits.extra != null) {
/*  412 */       int mergeLimit = 0; int copyLimit = otherInits.extra[0].length;
/*  413 */       if (this.extra == null) {
/*  414 */         this.extra = new long[6][];
/*  415 */         for (int j = 0; j < 6; j++) {
/*  416 */           this.extra[j] = new long[copyLimit];
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  424 */         mergeLimit = copyLimit;
/*  425 */         if (mergeLimit > this.extra[0].length) {
/*  426 */           mergeLimit = this.extra[0].length;
/*  427 */           for (int j = 0; j < 6; j++) {
/*  428 */             System.arraycopy(this.extra[j], 0, 
/*  429 */               this.extra[j] =  = new long[copyLimit], 0, 
/*  430 */               mergeLimit);
/*      */           }
/*  432 */           if (!thisHadNulls) {
/*  433 */             mergeLimit = 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  445 */       for (int i = 0; i < mergeLimit; i++)
/*      */       {
/*      */         long tmp728_727 = this.extra[2][i]; long a1 = tmp728_727;
/*      */         long tmp740_739 = this.extra[4][i]; long a3 = tmp740_739;
/*      */         long tmp752_751 = this.extra[5][i]; long a4 = tmp752_751;
/*      */         long tmp772_771 = ((b2 = otherInits.extra[3][i]) ^ 0xFFFFFFFF); nb2 = tmp772_771;
/*      */         long tmp791_790 = ((b4 = otherInits.extra[5][i]) ^ 0xFFFFFFFF); long nb4 = tmp791_790;
/*      */         long tmp804_803 = otherInits.extra[2][i]; b1 = tmp804_803;
/*      */         long tmp816_815 = otherInits.extra[4][i]; b3 = tmp816_815;
/*      */         long a2;
/*      */         long tmp838_837 = ((a2 = this.extra[3][i]) ^ 0xFFFFFFFF); long na2 = tmp838_837;
/*      */         long tmp852_851 = (a4 ^ 0xFFFFFFFF); long na4 = tmp852_851;
/*      */         long tmp861_860 = (a3 ^ 0xFFFFFFFF); long na3 = tmp861_860;
/*      */         long tmp884_883 = (b3 ^ 0xFFFFFFFF); nb3 = tmp884_883;
/*      */ 
/*  446 */         this.extra[2][i] = 
/*  447 */           (tmp728_727 & (
/*  447 */           tmp740_739 & tmp752_751 & (
/*  448 */           tmp772_771 & 
/*  449 */           tmp791_790 | 
/*  450 */           tmp804_803 & tmp816_815) | 
/*  451 */           tmp838_837 & (
/*  452 */           b1 & b3 | (tmp852_851 | tmp861_860) & nb2) | 
/*  453 */           a2 & ((na4 | na3) & (tmp884_883 & nb4 | b1 & b2))));
/*      */         long tmp919_918 = (b1 ^ 0xFFFFFFFF); nb1 = tmp919_918;
/*      */         long tmp943_942 = (a1 ^ 0xFFFFFFFF); long na1 = tmp943_942;
/*      */ 
/*  454 */         this.extra[3][i] = 
/*  455 */           (b2 & (nb3 | tmp919_918) | 
/*  455 */           a2 & (nb3 & nb4 | b2 | na3 | tmp943_942));
/*  456 */         this.extra[4][i] = 
/*  461 */           (b3 & (nb1 & b2 | 
/*  457 */           a2 & (nb2 | a3) | 
/*  458 */           na1 & nb2 | 
/*  459 */           a1 & na2 & na4 & b1) | 
/*  460 */           a3 & (nb2 & nb4 | na2 & a4 | na1) | 
/*  461 */           a1 & na2 & na4 & b2);
/*  462 */         this.extra[5][i] = 
/*  465 */           (na3 & (nb1 & nb3 & b4 | 
/*  463 */           a4 & (nb3 | b1 & b2)) | 
/*  464 */           nb2 & (na3 & b1 & nb3 | na2 & (nb1 & b4 | b1 & nb3 | a4)) | 
/*  465 */           a3 & (a4 & (nb2 | b1 & b3) | 
/*  466 */           a1 & a2 & (nb1 & b4 | na4 & (b2 | b1) & nb3)));
/*  467 */         if ((this.extra[3][i] | this.extra[4][i] | this.extra[5][i]) != 0L) {
/*  468 */           thisHasNulls = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  476 */       for (; i < copyLimit; i++) {
/*  477 */         this.extra[2][i] = 0L;
/*      */         long tmp1201_1200 = otherInits.extra[3][i]; b2 = tmp1201_1200;
/*      */         long tmp1220_1219 = ((b3 = otherInits.extra[4][i]) ^ 0xFFFFFFFF); nb3 = tmp1220_1219;
/*      */         long tmp1239_1238 = ((b1 = otherInits.extra[2][i]) ^ 0xFFFFFFFF); nb1 = tmp1239_1238;
/*      */ 
/*  478 */         this.extra[3][i] = 
/*  479 */           (tmp1201_1200 & (
/*  479 */           tmp1220_1219 | 
/*  480 */           tmp1239_1238));
/*      */         long tmp1263_1262 = (b2 ^ 0xFFFFFFFF); nb2 = tmp1263_1262; this.extra[4][i] = (b3 & (nb1 | tmp1263_1262));
/*      */         long tmp1299_1298 = otherInits.extra[5][i]; b4 = tmp1299_1298; this.extra[5][i] = ((b1 ^ 0xFFFFFFFF) & (b3 ^ 0xFFFFFFFF) & tmp1299_1298 | (b2 ^ 0xFFFFFFFF) & (b1 & (b3 ^ 0xFFFFFFFF) | (b1 ^ 0xFFFFFFFF) & b4));
/*  483 */         if ((this.extra[3][i] | this.extra[4][i] | this.extra[5][i]) != 0L) {
/*  484 */           thisHasNulls = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  493 */     if (thisHasNulls) {
/*  494 */       this.tagBits |= 2;
/*      */     }
/*      */     else {
/*  497 */       this.tagBits &= 2;
/*      */     }
/*  499 */     return this;
/*      */   }
/*      */ 
/*      */   public final boolean cannotBeDefinitelyNullOrNonNull(LocalVariableBinding local) {
/*  503 */     if (((this.tagBits & 0x2) == 0) || 
/*  504 */       ((local.type.tagBits & 0x2) != 0L))
/*  505 */       return false;
/*      */     int position;
/*  508 */     if ((position = local.id + this.maxFieldCount) < 64)
/*      */     {
/*  510 */       return (
/*  511 */         ((this.nullBit1 ^ 0xFFFFFFFF) & (
/*  512 */         this.nullBit2 & this.nullBit3 | this.nullBit4) | 
/*  513 */         (this.nullBit2 ^ 0xFFFFFFFF) & (this.nullBit3 ^ 0xFFFFFFFF) & this.nullBit4) & 
/*  514 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  517 */     if (this.extra == null)
/*  518 */       return false;
/*      */     int vectorIndex;
/*  522 */     if ((vectorIndex = position / 64 - 1) >= 
/*  522 */       this.extra[0].length)
/*  523 */       return false;
/*      */     long a2;
/*      */     long a3;
/*      */     long a4;
/*  526 */     return (
/*  527 */       ((this.extra[2][vectorIndex] ^ 0xFFFFFFFF) & (
/*  528 */       (a2 = this.extra[3][vectorIndex]) & (a3 = this.extra[4][vectorIndex]) | (a4 = this.extra[5][vectorIndex])) | 
/*  529 */       (a2 ^ 0xFFFFFFFF) & (a3 ^ 0xFFFFFFFF) & a4) & 
/*  530 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean cannotBeNull(LocalVariableBinding local) {
/*  534 */     if (((this.tagBits & 0x2) == 0) || 
/*  535 */       ((local.type.tagBits & 0x2) != 0L))
/*  536 */       return false;
/*      */     int position;
/*  539 */     if ((position = local.id + this.maxFieldCount) < 64)
/*      */     {
/*  541 */       return (this.nullBit1 & this.nullBit3 & (
/*  542 */         this.nullBit2 & this.nullBit4 | this.nullBit2 ^ 0xFFFFFFFF) & 
/*  543 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  546 */     if (this.extra == null)
/*  547 */       return false;
/*      */     int vectorIndex;
/*  551 */     if ((vectorIndex = position / 64 - 1) >= 
/*  551 */       this.extra[0].length) {
/*  552 */       return false;
/*      */     }
/*  554 */     return (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & (
/*  555 */       this.extra[3][vectorIndex] & this.extra[5][vectorIndex] | 
/*  556 */       this.extra[3][vectorIndex] ^ 0xFFFFFFFF) & 
/*  557 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean canOnlyBeNull(LocalVariableBinding local) {
/*  561 */     if (((this.tagBits & 0x2) == 0) || 
/*  562 */       ((local.type.tagBits & 0x2) != 0L))
/*  563 */       return false;
/*      */     int position;
/*  566 */     if ((position = local.id + this.maxFieldCount) < 64)
/*      */     {
/*  568 */       return (this.nullBit1 & this.nullBit2 & (
/*  569 */         this.nullBit3 ^ 0xFFFFFFFF | this.nullBit4 ^ 0xFFFFFFFF) & 
/*  570 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  573 */     if (this.extra == null)
/*  574 */       return false;
/*      */     int vectorIndex;
/*  578 */     if ((vectorIndex = position / 64 - 1) >= 
/*  578 */       this.extra[0].length) {
/*  579 */       return false;
/*      */     }
/*  581 */     return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (
/*  582 */       this.extra[4][vectorIndex] ^ 0xFFFFFFFF | this.extra[5][vectorIndex] ^ 0xFFFFFFFF) & 
/*  583 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public FlowInfo copy()
/*      */   {
/*  588 */     if (this == DEAD_END) {
/*  589 */       return this;
/*      */     }
/*  591 */     UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
/*      */ 
/*  593 */     copy.definiteInits = this.definiteInits;
/*  594 */     copy.potentialInits = this.potentialInits;
/*  595 */     boolean hasNullInfo = (this.tagBits & 0x2) != 0;
/*  596 */     if (hasNullInfo) {
/*  597 */       copy.nullBit1 = this.nullBit1;
/*  598 */       copy.nullBit2 = this.nullBit2;
/*  599 */       copy.nullBit3 = this.nullBit3;
/*  600 */       copy.nullBit4 = this.nullBit4;
/*      */     }
/*  602 */     copy.tagBits = this.tagBits;
/*  603 */     copy.maxFieldCount = this.maxFieldCount;
/*  604 */     if (this.extra != null)
/*      */     {
/*  606 */       copy.extra = new long[6][];
/*      */       int length;
/*  607 */       System.arraycopy(this.extra[0], 0, 
/*  608 */         copy.extra[0] =  = new long[length = this.extra[0].length], 0, 
/*  609 */         length);
/*  610 */       System.arraycopy(this.extra[1], 0, 
/*  611 */         copy.extra[1] =  = new long[length], 0, length);
/*  612 */       if (hasNullInfo) {
/*  613 */         for (int j = 2; j < 6; j++) {
/*  614 */           System.arraycopy(this.extra[j], 0, 
/*  615 */             copy.extra[j] =  = new long[length], 0, length);
/*      */         }
/*      */       }
/*      */       else {
/*  619 */         for (int j = 2; j < 6; j++) {
/*  620 */           copy.extra[j] = new long[length];
/*      */         }
/*      */       }
/*      */     }
/*  624 */     return copy;
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo discardInitializationInfo()
/*      */   {
/*  633 */     if (this == DEAD_END) {
/*  634 */       return this;
/*      */     }
/*  636 */     this.definiteInits = 
/*  637 */       (this.potentialInits = 0L);
/*  638 */     if (this.extra != null) {
/*  639 */       int i = 0; for (int length = this.extra[0].length; i < length; i++)
/*      */       {
/*      */         long tmp54_53 = 0L; this.extra[1][i] = tmp54_53; this.extra[0][i] = tmp54_53;
/*      */       }
/*      */     }
/*  643 */     return this;
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo discardNonFieldInitializations()
/*      */   {
/*  651 */     int limit = this.maxFieldCount;
/*  652 */     if (limit < 64) {
/*  653 */       long mask = (1L << limit) - 1L;
/*  654 */       this.definiteInits &= mask;
/*  655 */       this.potentialInits &= mask;
/*  656 */       this.nullBit1 &= mask;
/*  657 */       this.nullBit2 &= mask;
/*  658 */       this.nullBit3 &= mask;
/*  659 */       this.nullBit4 &= mask;
/*      */     }
/*      */ 
/*  662 */     if (this.extra == null) {
/*  663 */       return this;
/*      */     }
/*  665 */     int length = this.extra[0].length;
/*      */     int vectorIndex;
/*  666 */     if ((vectorIndex = limit / 64 - 1) >= length) {
/*  667 */       return this;
/*      */     }
/*  669 */     if (vectorIndex >= 0)
/*      */     {
/*  671 */       long mask = (1L << limit % 64) - 1L;
/*  672 */       for (int j = 0; j < 6; j++) {
/*  673 */         this.extra[j][vectorIndex] &= mask;
/*      */       }
/*      */     }
/*  676 */     for (int i = vectorIndex + 1; i < length; i++) {
/*  677 */       for (int j = 0; j < 6; j++) {
/*  678 */         this.extra[j][i] = 0L;
/*      */       }
/*      */     }
/*  681 */     return this;
/*      */   }
/*      */ 
/*      */   public FlowInfo initsWhenFalse() {
/*  685 */     return this;
/*      */   }
/*      */ 
/*      */   public FlowInfo initsWhenTrue() {
/*  689 */     return this;
/*      */   }
/*      */ 
/*      */   private final boolean isDefinitelyAssigned(int position)
/*      */   {
/*  698 */     if (position < 64)
/*      */     {
/*  700 */       return (this.definiteInits & 1L << position) != 0L;
/*      */     }
/*      */ 
/*  703 */     if (this.extra == null)
/*  704 */       return false;
/*      */     int vectorIndex;
/*  707 */     if ((vectorIndex = position / 64 - 1) >= 
/*  707 */       this.extra[0].length) {
/*  708 */       return false;
/*      */     }
/*  710 */     return (this.extra[0][vectorIndex] & 
/*  711 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isDefinitelyAssigned(FieldBinding field)
/*      */   {
/*  717 */     if ((this.tagBits & 0x1) != 0) {
/*  718 */       return true;
/*      */     }
/*  720 */     return isDefinitelyAssigned(field.id);
/*      */   }
/*      */ 
/*      */   public final boolean isDefinitelyAssigned(LocalVariableBinding local)
/*      */   {
/*  725 */     if (((this.tagBits & 0x1) != 0) && ((local.declaration.bits & 0x40000000) != 0)) {
/*  726 */       return true;
/*      */     }
/*  728 */     return isDefinitelyAssigned(local.id + this.maxFieldCount);
/*      */   }
/*      */ 
/*      */   public final boolean isDefinitelyNonNull(LocalVariableBinding local)
/*      */   {
/*  733 */     if (((this.tagBits & 0x1) != 0) || 
/*  734 */       ((this.tagBits & 0x2) == 0)) {
/*  735 */       return false;
/*      */     }
/*  737 */     if (((local.type.tagBits & 0x2) != 0L) || 
/*  738 */       (local.constant() != Constant.NotAConstant)) {
/*  739 */       return true;
/*      */     }
/*  741 */     int position = local.id + this.maxFieldCount;
/*  742 */     if (position < 64) {
/*  743 */       return (this.nullBit1 & this.nullBit3 & (this.nullBit2 ^ 0xFFFFFFFF | this.nullBit4) & 
/*  744 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  747 */     if (this.extra == null)
/*  748 */       return false;
/*      */     int vectorIndex;
/*  752 */     if ((vectorIndex = position / 64 - 1) >= 
/*  752 */       this.extra[0].length) {
/*  753 */       return false;
/*      */     }
/*  755 */     return (this.extra[2][vectorIndex] & this.extra[4][vectorIndex] & (
/*  756 */       this.extra[3][vectorIndex] ^ 0xFFFFFFFF | this.extra[5][vectorIndex]) & 
/*  757 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isDefinitelyNull(LocalVariableBinding local)
/*      */   {
/*  762 */     if (((this.tagBits & 0x1) != 0) || 
/*  763 */       ((this.tagBits & 0x2) == 0) || 
/*  764 */       ((local.type.tagBits & 0x2) != 0L)) {
/*  765 */       return false;
/*      */     }
/*  767 */     int position = local.id + this.maxFieldCount;
/*  768 */     if (position < 64) {
/*  769 */       return (this.nullBit1 & this.nullBit2 & (
/*  770 */         this.nullBit3 ^ 0xFFFFFFFF | this.nullBit4 ^ 0xFFFFFFFF) & 
/*  771 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  774 */     if (this.extra == null)
/*  775 */       return false;
/*      */     int vectorIndex;
/*  779 */     if ((vectorIndex = position / 64 - 1) >= 
/*  779 */       this.extra[0].length) {
/*  780 */       return false;
/*      */     }
/*  782 */     return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (
/*  783 */       this.extra[4][vectorIndex] ^ 0xFFFFFFFF | this.extra[5][vectorIndex] ^ 0xFFFFFFFF) & 
/*  784 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isDefinitelyUnknown(LocalVariableBinding local)
/*      */   {
/*  789 */     if (((this.tagBits & 0x1) != 0) || 
/*  790 */       ((this.tagBits & 0x2) == 0)) {
/*  791 */       return false;
/*      */     }
/*  793 */     int position = local.id + this.maxFieldCount;
/*  794 */     if (position < 64) {
/*  795 */       return (this.nullBit1 & this.nullBit4 & (
/*  796 */         this.nullBit2 ^ 0xFFFFFFFF) & (this.nullBit3 ^ 0xFFFFFFFF) & 1L << position) != 0L;
/*      */     }
/*      */ 
/*  799 */     if (this.extra == null)
/*  800 */       return false;
/*      */     int vectorIndex;
/*  804 */     if ((vectorIndex = position / 64 - 1) >= 
/*  804 */       this.extra[0].length) {
/*  805 */       return false;
/*      */     }
/*  807 */     return (this.extra[2][vectorIndex] & this.extra[5][vectorIndex] & (
/*  808 */       this.extra[3][vectorIndex] ^ 0xFFFFFFFF) & (this.extra[4][vectorIndex] ^ 0xFFFFFFFF) & 
/*  809 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   private final boolean isPotentiallyAssigned(int position)
/*      */   {
/*  817 */     if (position < 64)
/*      */     {
/*  819 */       return (this.potentialInits & 1L << position) != 0L;
/*      */     }
/*      */ 
/*  822 */     if (this.extra == null)
/*  823 */       return false;
/*      */     int vectorIndex;
/*  827 */     if ((vectorIndex = position / 64 - 1) >= 
/*  827 */       this.extra[0].length) {
/*  828 */       return false;
/*      */     }
/*  830 */     return (this.extra[1][vectorIndex] & 
/*  831 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isPotentiallyAssigned(FieldBinding field) {
/*  835 */     return isPotentiallyAssigned(field.id);
/*      */   }
/*      */ 
/*      */   public final boolean isPotentiallyAssigned(LocalVariableBinding local)
/*      */   {
/*  840 */     if (local.constant() != Constant.NotAConstant) {
/*  841 */       return true;
/*      */     }
/*  843 */     return isPotentiallyAssigned(local.id + this.maxFieldCount);
/*      */   }
/*      */ 
/*      */   public final boolean isPotentiallyNonNull(LocalVariableBinding local) {
/*  847 */     if (((this.tagBits & 0x2) == 0) || 
/*  848 */       ((local.type.tagBits & 0x2) != 0L))
/*  849 */       return false;
/*      */     int position;
/*  852 */     if ((position = local.id + this.maxFieldCount) < 64)
/*      */     {
/*  854 */       return (this.nullBit3 & (this.nullBit1 ^ 0xFFFFFFFF | this.nullBit2 ^ 0xFFFFFFFF) & 
/*  855 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  858 */     if (this.extra == null)
/*  859 */       return false;
/*      */     int vectorIndex;
/*  863 */     if ((vectorIndex = position / 64 - 1) >= 
/*  863 */       this.extra[0].length) {
/*  864 */       return false;
/*      */     }
/*  866 */     return (this.extra[4][vectorIndex] & (
/*  867 */       this.extra[2][vectorIndex] ^ 0xFFFFFFFF | this.extra[3][vectorIndex] ^ 0xFFFFFFFF) & 
/*  868 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isPotentiallyNull(LocalVariableBinding local) {
/*  872 */     if (((this.tagBits & 0x2) == 0) || 
/*  873 */       ((local.type.tagBits & 0x2) != 0L))
/*  874 */       return false;
/*      */     int position;
/*  877 */     if ((position = local.id + this.maxFieldCount) < 64)
/*      */     {
/*  879 */       return (this.nullBit2 & (this.nullBit1 ^ 0xFFFFFFFF | this.nullBit3 ^ 0xFFFFFFFF) & 
/*  880 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  883 */     if (this.extra == null)
/*  884 */       return false;
/*      */     int vectorIndex;
/*  888 */     if ((vectorIndex = position / 64 - 1) >= 
/*  888 */       this.extra[0].length) {
/*  889 */       return false;
/*      */     }
/*  891 */     return (this.extra[3][vectorIndex] & (
/*  892 */       this.extra[2][vectorIndex] ^ 0xFFFFFFFF | this.extra[4][vectorIndex] ^ 0xFFFFFFFF) & 
/*  893 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isPotentiallyUnknown(LocalVariableBinding local)
/*      */   {
/*  898 */     if (((this.tagBits & 0x1) != 0) || 
/*  899 */       ((this.tagBits & 0x2) == 0)) {
/*  900 */       return false;
/*      */     }
/*  902 */     int position = local.id + this.maxFieldCount;
/*  903 */     if (position < 64) {
/*  904 */       return (this.nullBit4 & (
/*  905 */         this.nullBit1 ^ 0xFFFFFFFF | (this.nullBit2 ^ 0xFFFFFFFF) & (this.nullBit3 ^ 0xFFFFFFFF)) & 
/*  906 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  909 */     if (this.extra == null)
/*  910 */       return false;
/*      */     int vectorIndex;
/*  914 */     if ((vectorIndex = position / 64 - 1) >= 
/*  914 */       this.extra[0].length) {
/*  915 */       return false;
/*      */     }
/*  917 */     return (this.extra[5][vectorIndex] & (
/*  918 */       this.extra[2][vectorIndex] ^ 0xFFFFFFFF | 
/*  919 */       (this.extra[3][vectorIndex] ^ 0xFFFFFFFF) & (this.extra[4][vectorIndex] ^ 0xFFFFFFFF)) & 
/*  920 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isProtectedNonNull(LocalVariableBinding local) {
/*  924 */     if (((this.tagBits & 0x2) == 0) || 
/*  925 */       ((local.type.tagBits & 0x2) != 0L))
/*  926 */       return false;
/*      */     int position;
/*  929 */     if ((position = local.id + this.maxFieldCount) < 64)
/*      */     {
/*  931 */       return (this.nullBit1 & this.nullBit3 & this.nullBit4 & 1L << position) != 0L;
/*      */     }
/*      */ 
/*  934 */     if (this.extra == null)
/*  935 */       return false;
/*      */     int vectorIndex;
/*  939 */     if ((vectorIndex = position / 64 - 1) >= 
/*  939 */       this.extra[0].length) {
/*  940 */       return false;
/*      */     }
/*  942 */     return (this.extra[2][vectorIndex] & 
/*  943 */       this.extra[4][vectorIndex] & 
/*  944 */       this.extra[5][vectorIndex] & 
/*  945 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public final boolean isProtectedNull(LocalVariableBinding local) {
/*  949 */     if (((this.tagBits & 0x2) == 0) || 
/*  950 */       ((local.type.tagBits & 0x2) != 0L))
/*  951 */       return false;
/*      */     int position;
/*  954 */     if ((position = local.id + this.maxFieldCount) < 64)
/*      */     {
/*  956 */       return (this.nullBit1 & this.nullBit2 & (
/*  957 */         this.nullBit3 ^ this.nullBit4) & 
/*  958 */         1L << position) != 0L;
/*      */     }
/*      */ 
/*  961 */     if (this.extra == null)
/*  962 */       return false;
/*      */     int vectorIndex;
/*  966 */     if ((vectorIndex = position / 64 - 1) >= 
/*  966 */       this.extra[0].length) {
/*  967 */       return false;
/*      */     }
/*  969 */     return (this.extra[2][vectorIndex] & this.extra[3][vectorIndex] & (
/*  970 */       this.extra[4][vectorIndex] ^ this.extra[5][vectorIndex]) & 
/*  971 */       1L << position % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public void markAsComparedEqualToNonNull(LocalVariableBinding local)
/*      */   {
/*  976 */     if (this != DEAD_END) {
/*  977 */       this.tagBits |= 2;
/*      */       int position;
/*  982 */       if ((position = local.id + this.maxFieldCount) < 64)
/*      */       {
/*      */         long mask;
/*      */         long a1;
/*      */         long a2;
/*      */         long na2;
/*      */         long a3;
/*      */         long a4;
/*  984 */         if (((mask = 1L << position) & (
/*  985 */           a1 = this.nullBit1) & (
/*  986 */           na2 = (a2 = this.nullBit2) ^ 0xFFFFFFFF) & (
/*  987 */           (a3 = this.nullBit3) ^ 0xFFFFFFFF) & (
/*  988 */           a4 = this.nullBit4)) != 
/*  989 */           0L) {
/*  990 */           this.nullBit4 &= (mask ^ 0xFFFFFFFF);
/*  991 */         } else if ((mask & a1 & na2 & a3) == 0L) {
/*  992 */           this.nullBit4 |= mask;
/*  993 */           if ((mask & a1) == 0L) {
/*  994 */             if ((mask & a2 & (a3 ^ a4)) != 0L) {
/*  995 */               this.nullBit2 &= (mask ^ 0xFFFFFFFF);
/*      */             }
/*  997 */             else if ((mask & (a2 | a3 | a4)) == 0L) {
/*  998 */               this.nullBit2 |= mask;
/*      */             }
/*      */           }
/*      */         }
/* 1002 */         this.nullBit1 |= mask;
/* 1003 */         this.nullBit3 |= mask;
/*      */       }
/*      */       else
/*      */       {
/* 1012 */         int vectorIndex = position / 64 - 1;
/* 1013 */         if (this.extra == null) {
/* 1014 */           int length = vectorIndex + 1;
/* 1015 */           this.extra = new long[6][];
/* 1016 */           for (int j = 0; j < 6; j++)
/* 1017 */             this.extra[j] = new long[length];
/*      */         }
/*      */         else
/*      */         {
/*      */           int oldLength;
/* 1027 */           if (vectorIndex >= (oldLength = this.extra[0].length)) {
/* 1028 */             int newLength = vectorIndex + 1;
/* 1029 */             for (int j = 0; j < 6; j++)
/* 1030 */               System.arraycopy(this.extra[j], 0, 
/* 1031 */                 this.extra[j] =  = new long[newLength], 0, 
/* 1032 */                 oldLength);
/*      */           }
/*      */         }
/*      */         long mask;
/*      */         long a1;
/*      */         long a2;
/*      */         long na2;
/*      */         long a3;
/*      */         long a4;
/* 1042 */         if (((mask = 1L << position % 64) & (
/* 1043 */           a1 = this.extra[2][vectorIndex]) & (
/* 1044 */           na2 = (a2 = this.extra[3][vectorIndex]) ^ 0xFFFFFFFF) & (
/* 1045 */           (a3 = this.extra[4][vectorIndex]) ^ 0xFFFFFFFF) & (
/* 1046 */           a4 = this.extra[5][vectorIndex])) != 
/* 1047 */           0L) {
/* 1048 */           this.extra[5][vectorIndex] &= (mask ^ 0xFFFFFFFF);
/* 1049 */         } else if ((mask & a1 & na2 & a3) == 0L) {
/* 1050 */           this.extra[5][vectorIndex] |= mask;
/* 1051 */           if ((mask & a1) == 0L) {
/* 1052 */             if ((mask & a2 & (a3 ^ a4)) != 0L) {
/* 1053 */               this.extra[3][vectorIndex] &= (mask ^ 0xFFFFFFFF);
/*      */             }
/* 1055 */             else if ((mask & (a2 | a3 | a4)) == 0L) {
/* 1056 */               this.extra[3][vectorIndex] |= mask;
/*      */             }
/*      */           }
/*      */         }
/* 1060 */         this.extra[2][vectorIndex] |= mask;
/* 1061 */         this.extra[4][vectorIndex] |= mask;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void markAsComparedEqualToNull(LocalVariableBinding local)
/*      */   {
/* 1073 */     if (this != DEAD_END) {
/* 1074 */       this.tagBits |= 2;
/*      */       int position;
/* 1078 */       if ((position = local.id + this.maxFieldCount) < 64)
/*      */       {
/*      */         long mask;
/* 1080 */         if (((mask = 1L << position) & this.nullBit1) != 0L) {
/* 1081 */           if ((mask & (
/* 1082 */             this.nullBit2 ^ 0xFFFFFFFF | this.nullBit3 | 
/* 1083 */             this.nullBit4 ^ 0xFFFFFFFF)) != 0L)
/* 1084 */             this.nullBit4 &= (mask ^ 0xFFFFFFFF);
/*      */         }
/* 1086 */         else if ((mask & this.nullBit4) != 0L) {
/* 1087 */           this.nullBit3 &= (mask ^ 0xFFFFFFFF);
/*      */         }
/* 1089 */         else if ((mask & this.nullBit2) != 0L) {
/* 1090 */           this.nullBit3 &= (mask ^ 0xFFFFFFFF);
/* 1091 */           this.nullBit4 |= mask;
/*      */         } else {
/* 1093 */           this.nullBit3 |= mask;
/*      */         }
/*      */ 
/* 1096 */         this.nullBit1 |= mask;
/* 1097 */         this.nullBit2 |= mask;
/*      */       }
/*      */       else
/*      */       {
/* 1106 */         int vectorIndex = position / 64 - 1;
/* 1107 */         long mask = 1L << position % 64;
/* 1108 */         if (this.extra == null) {
/* 1109 */           int length = vectorIndex + 1;
/* 1110 */           this.extra = new long[6][];
/* 1111 */           for (int j = 0; j < 6; j++)
/* 1112 */             this.extra[j] = new long[length];
/*      */         }
/*      */         else
/*      */         {
/*      */           int oldLength;
/* 1122 */           if (vectorIndex >= (oldLength = this.extra[0].length)) {
/* 1123 */             int newLength = vectorIndex + 1;
/* 1124 */             for (int j = 0; j < 6; j++) {
/* 1125 */               System.arraycopy(this.extra[j], 0, 
/* 1126 */                 this.extra[j] =  = new long[newLength], 0, 
/* 1127 */                 oldLength);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1136 */         if ((mask & this.extra[2][vectorIndex]) != 0L) {
/* 1137 */           if ((mask & (
/* 1138 */             this.extra[3][vectorIndex] ^ 0xFFFFFFFF | this.extra[4][vectorIndex] | 
/* 1139 */             this.extra[5][vectorIndex] ^ 0xFFFFFFFF)) != 0L)
/* 1140 */             this.extra[5][vectorIndex] &= (mask ^ 0xFFFFFFFF);
/*      */         }
/* 1142 */         else if ((mask & this.extra[5][vectorIndex]) != 0L) {
/* 1143 */           this.extra[4][vectorIndex] &= (mask ^ 0xFFFFFFFF);
/*      */         }
/* 1145 */         else if ((mask & this.extra[3][vectorIndex]) != 0L) {
/* 1146 */           this.extra[4][vectorIndex] &= (mask ^ 0xFFFFFFFF);
/* 1147 */           this.extra[5][vectorIndex] |= mask;
/*      */         } else {
/* 1149 */           this.extra[4][vectorIndex] |= mask;
/*      */         }
/*      */ 
/* 1152 */         this.extra[2][vectorIndex] |= mask;
/* 1153 */         this.extra[3][vectorIndex] |= mask;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private final void markAsDefinitelyAssigned(int position)
/*      */   {
/* 1163 */     if (this != DEAD_END)
/*      */     {
/* 1165 */       if (position < 64)
/*      */       {
/*      */         long mask;
/* 1168 */         this.definiteInits |= (mask = 1L << position);
/* 1169 */         this.potentialInits |= mask;
/*      */       }
/*      */       else
/*      */       {
/* 1173 */         int vectorIndex = position / 64 - 1;
/* 1174 */         if (this.extra == null) {
/* 1175 */           int length = vectorIndex + 1;
/* 1176 */           this.extra = new long[6][];
/* 1177 */           for (int j = 0; j < 6; j++)
/* 1178 */             this.extra[j] = new long[length];
/*      */         }
/*      */         else
/*      */         {
/*      */           int oldLength;
/* 1183 */           if (vectorIndex >= (oldLength = this.extra[0].length))
/* 1184 */             for (int j = 0; j < 6; j++)
/* 1185 */               System.arraycopy(this.extra[j], 0, 
/* 1186 */                 this.extra[j] =  = new long[vectorIndex + 1], 0, 
/* 1187 */                 oldLength);
/*      */         }
/*      */         long tmp166_165 = (1L << position % 64); long mask = tmp166_165;
/*      */ 
/* 1192 */         this.extra[0][vectorIndex] |= 
/* 1193 */           tmp166_165;
/* 1194 */         this.extra[1][vectorIndex] |= mask;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void markAsDefinitelyAssigned(FieldBinding field) {
/* 1200 */     if (this != DEAD_END)
/* 1201 */       markAsDefinitelyAssigned(field.id);
/*      */   }
/*      */ 
/*      */   public void markAsDefinitelyAssigned(LocalVariableBinding local) {
/* 1205 */     if (this != DEAD_END)
/* 1206 */       markAsDefinitelyAssigned(local.id + this.maxFieldCount);
/*      */   }
/*      */ 
/*      */   public void markAsDefinitelyNonNull(LocalVariableBinding local)
/*      */   {
/* 1211 */     if (this != DEAD_END) {
/* 1212 */       this.tagBits |= 2;
/*      */       int position;
/* 1216 */       if ((position = local.id + this.maxFieldCount) < 64)
/*      */       {
/*      */         long mask;
/* 1218 */         this.nullBit1 |= (mask = 1L << position);
/* 1219 */         this.nullBit3 |= mask;
/*      */ 
/* 1221 */         this.nullBit2 &= (mask ^= -1L);
/* 1222 */         this.nullBit4 &= mask;
/*      */       }
/*      */       else
/*      */       {
/*      */         int vectorIndex;
/*      */         long tmp113_112 = (1L << position % 64); long mask = tmp113_112;
/*      */ 
/* 1232 */         this.extra[2][(vectorIndex = position / 64 - 1)] |= 
/* 1233 */           tmp113_112;
/* 1234 */         this.extra[4][vectorIndex] |= mask;
/*      */         long tmp145_144 = (mask ^ 0xFFFFFFFF); mask = tmp145_144; this.extra[3][vectorIndex] &= tmp145_144;
/* 1236 */         this.extra[5][vectorIndex] &= mask;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void markAsDefinitelyNull(LocalVariableBinding local)
/*      */   {
/* 1248 */     if (this != DEAD_END) {
/* 1249 */       this.tagBits |= 2;
/*      */       int position;
/* 1253 */       if ((position = local.id + this.maxFieldCount) < 64)
/*      */       {
/*      */         long mask;
/* 1255 */         this.nullBit1 |= (mask = 1L << position);
/* 1256 */         this.nullBit2 |= mask;
/*      */ 
/* 1258 */         this.nullBit3 &= (mask ^= -1L);
/* 1259 */         this.nullBit4 &= mask;
/*      */       }
/*      */       else
/*      */       {
/*      */         int vectorIndex;
/*      */         long tmp113_112 = (1L << position % 64); long mask = tmp113_112;
/*      */ 
/* 1269 */         this.extra[2][(vectorIndex = position / 64 - 1)] |= 
/* 1270 */           tmp113_112;
/* 1271 */         this.extra[3][vectorIndex] |= mask;
/*      */         long tmp145_144 = (mask ^ 0xFFFFFFFF); mask = tmp145_144; this.extra[4][vectorIndex] &= tmp145_144;
/* 1273 */         this.extra[5][vectorIndex] &= mask;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void markAsDefinitelyUnknown(LocalVariableBinding local)
/*      */   {
/* 1291 */     if (this != DEAD_END) {
/* 1292 */       this.tagBits |= 2;
/*      */       int position;
/* 1296 */       if ((position = local.id + this.maxFieldCount) < 64)
/*      */       {
/*      */         long mask;
/* 1299 */         this.nullBit1 |= (mask = 1L << position);
/* 1300 */         this.nullBit4 |= mask;
/*      */ 
/* 1302 */         this.nullBit2 &= (mask ^= -1L);
/* 1303 */         this.nullBit3 &= mask;
/*      */       }
/*      */       else
/*      */       {
/*      */         int vectorIndex;
/*      */         long tmp113_112 = (1L << position % 64); long mask = tmp113_112;
/*      */ 
/* 1313 */         this.extra[2][(vectorIndex = position / 64 - 1)] |= 
/* 1314 */           tmp113_112;
/* 1315 */         this.extra[5][vectorIndex] |= mask;
/*      */         long tmp145_144 = (mask ^ 0xFFFFFFFF); mask = tmp145_144; this.extra[3][vectorIndex] &= tmp145_144;
/* 1317 */         this.extra[4][vectorIndex] &= mask;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo mergedWith(UnconditionalFlowInfo otherInits)
/*      */   {
/* 1328 */     if (((otherInits.tagBits & 0x1) != 0) && (this != DEAD_END))
/*      */     {
/* 1334 */       return this;
/*      */     }
/* 1336 */     if ((this.tagBits & 0x1) != 0)
/*      */     {
/* 1342 */       return (UnconditionalFlowInfo)otherInits.copy();
/*      */     }
/*      */ 
/* 1346 */     this.definiteInits &= otherInits.definiteInits;
/*      */ 
/* 1348 */     this.potentialInits |= otherInits.potentialInits;
/*      */ 
/* 1352 */     boolean thisHasNulls = (this.tagBits & 0x2) != 0;
/* 1353 */     boolean otherHasNulls = (otherInits.tagBits & 0x2) != 0;
/* 1354 */     boolean thisHadNulls = thisHasNulls;
/*      */ 
/* 1360 */     if (thisHadNulls) {
/* 1361 */       if (otherHasNulls)
/*      */       {
/*      */         long a2;
/*      */         long a3;
/*      */         long a4;
/*      */         long b1;
/*      */         long b2;
/*      */         long nb2;
/*      */         long a1;
/*      */         long b3;
/*      */         long b4;
/*      */         long na2;
/*      */         long nb4;
/*      */         long na4;
/*      */         long na3;
/*      */         long nb3;
/* 1362 */         this.nullBit1 = 
/* 1365 */           ((a2 = this.nullBit2) & (a3 = this.nullBit3) & (
/* 1363 */           a4 = this.nullBit4) & (b1 = otherInits.nullBit1) & (
/* 1364 */           nb2 = (b2 = otherInits.nullBit2) ^ 0xFFFFFFFF) | 
/* 1365 */           (a1 = this.nullBit1) & (b1 & (a3 & a4 & (b3 = otherInits.nullBit3) & (
/* 1366 */           b4 = otherInits.nullBit4) | 
/* 1367 */           (na2 = a2 ^ 0xFFFFFFFF) & nb2 & (
/* 1368 */           (nb4 = b4 ^ 0xFFFFFFFF) | (na4 = a4 ^ 0xFFFFFFFF) | 
/* 1369 */           (na3 = a3 ^ 0xFFFFFFFF) & (nb3 = b3 ^ 0xFFFFFFFF)) | 
/* 1370 */           a2 & b2 & ((na4 | na3) & (nb4 | nb3))) | 
/* 1371 */           na2 & b2 & b3 & b4));
/*      */         long nb1;
/*      */         long na1;
/* 1372 */         this.nullBit2 = 
/* 1373 */           (b2 & (nb3 | (nb1 = b1 ^ 0xFFFFFFFF) | a3 & (a4 | (na1 = a1 ^ 0xFFFFFFFF)) & nb4) | 
/* 1373 */           a2 & (b2 | na4 & b3 & (b4 | nb1) | na3 | na1));
/* 1374 */         this.nullBit3 = 
/* 1377 */           (b3 & (nb2 & b4 | nb1 | a3 & (na4 & nb4 | a4 & b4)) | 
/* 1375 */           a3 & (na2 & a4 | na1) | 
/* 1376 */           (a2 | na1) & b1 & nb2 & nb4 | 
/* 1377 */           a1 & na2 & na4 & (b2 | nb1));
/* 1378 */         this.nullBit4 = 
/* 1383 */           (na3 & (nb1 & nb3 & b4 | 
/* 1379 */           b1 & (nb2 & nb3 | a4 & b2 & nb4) | 
/* 1380 */           na1 & a4 & (nb3 | b1 & b2)) | 
/* 1381 */           a3 & a4 & (b3 & b4 | b1 & nb2) | 
/* 1382 */           na2 & (nb1 & b4 | b1 & nb3 | na1 & a4) & nb2 | 
/* 1383 */           a1 & (na3 & (nb3 & b4 | 
/* 1384 */           b1 & b2 & b3 & nb4 | 
/* 1385 */           na2 & (nb3 | nb2)) | 
/* 1386 */           na2 & b3 & b4 | 
/* 1387 */           a2 & (nb1 & b4 | a3 & na4 & b1) & nb3));
/*      */       }
/*      */       else
/*      */       {
/* 1394 */         long a1 = this.nullBit1;
/* 1395 */         this.nullBit1 = 0L;
/*      */         long a2;
/*      */         long a3;
/*      */         long na1;
/*      */         long na3;
/* 1396 */         this.nullBit2 = ((a2 = this.nullBit2) & (na3 = (a3 = this.nullBit3) ^ 0xFFFFFFFF | (na1 = a1 ^ 0xFFFFFFFF)));
/*      */         long na2;
/*      */         long a4;
/* 1397 */         this.nullBit3 = (a3 & ((na2 = a2 ^ 0xFFFFFFFF) & (a4 = this.nullBit4) | na1) | a1 & na2 & (a4 ^ 0xFFFFFFFF));
/* 1398 */         this.nullBit4 = ((na3 | na2) & na1 & a4 | a1 & na3 & na2);
/*      */       }
/*      */ 
/*      */     }
/* 1405 */     else if (otherHasNulls) {
/* 1406 */       this.nullBit1 = 0L;
/*      */       long b2;
/*      */       long b3;
/*      */       long b1;
/*      */       long nb1;
/*      */       long nb3;
/* 1407 */       this.nullBit2 = ((b2 = otherInits.nullBit2) & (nb3 = (b3 = otherInits.nullBit3) ^ 0xFFFFFFFF | (nb1 = (b1 = otherInits.nullBit1) ^ 0xFFFFFFFF)));
/*      */       long nb2;
/*      */       long b4;
/* 1408 */       this.nullBit3 = (b3 & ((nb2 = b2 ^ 0xFFFFFFFF) & (b4 = otherInits.nullBit4) | nb1) | b1 & nb2 & (b4 ^ 0xFFFFFFFF));
/* 1409 */       this.nullBit4 = ((nb3 | nb2) & nb1 & b4 | b1 & nb3 & nb2);
/*      */ 
/* 1415 */       thisHasNulls = 
/* 1417 */         (this.nullBit2 != 0L) || 
/* 1418 */         (this.nullBit3 != 0L) || 
/* 1419 */         (this.nullBit4 != 0L);
/*      */     }
/*      */ 
/* 1423 */     if ((this.extra != null) || (otherInits.extra != null)) {
/* 1424 */       int mergeLimit = 0; int copyLimit = 0; int resetLimit = 0;
/*      */ 
/* 1426 */       if (this.extra != null) {
/* 1427 */         if (otherInits.extra != null)
/*      */         {
/*      */           int length;
/*      */           int otherLength;
/* 1431 */           if ((length = this.extra[0].length) < 
/* 1431 */             (otherLength = otherInits.extra[0].length))
/*      */           {
/* 1433 */             for (int j = 0; j < 6; j++) {
/* 1434 */               System.arraycopy(this.extra[j], 0, 
/* 1435 */                 this.extra[j] =  = new long[otherLength], 0, length);
/*      */             }
/* 1437 */             mergeLimit = length;
/* 1438 */             copyLimit = otherLength;
/*      */           }
/*      */           else
/*      */           {
/* 1447 */             mergeLimit = otherLength;
/* 1448 */             resetLimit = length;
/*      */           }
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/* 1457 */           resetLimit = this.extra[0].length;
/*      */         }
/*      */ 
/*      */       }
/* 1465 */       else if (otherInits.extra != null)
/*      */       {
/* 1467 */         int otherLength = otherInits.extra[0].length;
/* 1468 */         this.extra = new long[6][];
/* 1469 */         for (int j = 0; j < 6; j++) {
/* 1470 */           this.extra[j] = new long[otherLength];
/*      */         }
/* 1472 */         System.arraycopy(otherInits.extra[1], 0, 
/* 1473 */           this.extra[1], 0, otherLength);
/* 1474 */         copyLimit = otherLength;
/*      */       }
/*      */ 
/* 1483 */       for (int i = 0; i < mergeLimit; i++) {
/* 1484 */         this.extra[0][i] &= otherInits.extra[0][i];
/* 1485 */         this.extra[1][i] |= otherInits.extra[1][i];
/*      */       }
/* 1487 */       for (; i < copyLimit; i++) {
/* 1488 */         this.extra[1][i] = otherInits.extra[1][i];
/*      */       }
/* 1490 */       for (; i < resetLimit; i++) {
/* 1491 */         this.extra[0][i] = 0L;
/*      */       }
/*      */ 
/* 1494 */       if (!otherHasNulls) {
/* 1495 */         if (resetLimit < mergeLimit) {
/* 1496 */           resetLimit = mergeLimit;
/*      */         }
/* 1498 */         copyLimit = 0;
/* 1499 */         mergeLimit = 0;
/*      */       }
/* 1501 */       if (!thisHadNulls) {
/* 1502 */         resetLimit = 0;
/*      */       }
/*      */ 
/* 1505 */       for (i = 0; i < mergeLimit; i++)
/*      */       {
/*      */         long tmp1195_1194 = this.extra[3][i]; long a2 = tmp1195_1194;
/*      */         long tmp1207_1206 = this.extra[4][i]; long a3 = tmp1207_1206;
/*      */         long tmp1220_1219 = this.extra[5][i]; long a4 = tmp1220_1219;
/*      */         long tmp1233_1232 = otherInits.extra[2][i]; long b1 = tmp1233_1232;
/*      */         long b2;
/*      */         long tmp1253_1252 = ((b2 = otherInits.extra[3][i]) ^ 0xFFFFFFFF); long nb2 = tmp1253_1252;
/*      */         long tmp1266_1265 = this.extra[2][i]; long a1 = tmp1266_1265;
/*      */         long tmp1285_1284 = otherInits.extra[4][i]; long b3 = tmp1285_1284;
/*      */         long tmp1298_1297 = otherInits.extra[5][i]; long b4 = tmp1298_1297;
/*      */         long tmp1308_1307 = (a2 ^ 0xFFFFFFFF); long na2 = tmp1308_1307;
/*      */         long tmp1320_1319 = (b4 ^ 0xFFFFFFFF); long nb4 = tmp1320_1319;
/*      */         long tmp1329_1328 = (a4 ^ 0xFFFFFFFF); long na4 = tmp1329_1328;
/*      */         long tmp1339_1338 = (a3 ^ 0xFFFFFFFF); long na3 = tmp1339_1338;
/*      */         long tmp1348_1347 = (b3 ^ 0xFFFFFFFF); long nb3 = tmp1348_1347;
/*      */ 
/* 1506 */         this.extra[2][i] = 
/* 1509 */           (tmp1195_1194 & tmp1207_1206 & 
/* 1507 */           tmp1220_1219 & tmp1233_1232 & 
/* 1508 */           tmp1253_1252 | 
/* 1509 */           tmp1266_1265 & (b1 & (a3 & a4 & tmp1285_1284 & 
/* 1510 */           tmp1298_1297 | 
/* 1511 */           tmp1308_1307 & nb2 & (
/* 1512 */           tmp1320_1319 | tmp1329_1328 | 
/* 1513 */           tmp1339_1338 & tmp1348_1347) | 
/* 1514 */           a2 & b2 & ((na4 | na3) & (nb4 | nb3))) | 
/* 1515 */           na2 & b2 & b3 & b4));
/*      */         long tmp1407_1406 = (b1 ^ 0xFFFFFFFF); long nb1 = tmp1407_1406;
/*      */         long tmp1421_1420 = (a1 ^ 0xFFFFFFFF); long na1 = tmp1421_1420; this.extra[3][i] = 
/* 1517 */           (b2 & (nb3 | tmp1407_1406 | a3 & (a4 | tmp1421_1420) & nb4) | 
/* 1517 */           a2 & (b2 | na4 & b3 & (b4 | nb1) | na3 | na1));
/* 1518 */         this.extra[4][i] = 
/* 1521 */           (b3 & (nb2 & b4 | nb1 | a3 & (na4 & nb4 | a4 & b4)) | 
/* 1519 */           a3 & (na2 & a4 | na1) | 
/* 1520 */           (a2 | na1) & b1 & nb2 & nb4 | 
/* 1521 */           a1 & na2 & na4 & (b2 | nb1));
/* 1522 */         this.extra[5][i] = 
/* 1527 */           (na3 & (nb1 & nb3 & b4 | 
/* 1523 */           b1 & (nb2 & nb3 | a4 & b2 & nb4) | 
/* 1524 */           na1 & a4 & (nb3 | b1 & b2)) | 
/* 1525 */           a3 & a4 & (b3 & b4 | b1 & nb2) | 
/* 1526 */           na2 & (nb1 & b4 | b1 & nb3 | na1 & a4) & nb2 | 
/* 1527 */           a1 & (na3 & (nb3 & b4 | 
/* 1528 */           b1 & b2 & b3 & nb4 | 
/* 1529 */           na2 & (nb3 | nb2)) | 
/* 1530 */           na2 & b3 & b4 | 
/* 1531 */           a2 & (nb1 & b4 | a3 & na4 & b1) & nb3));
/* 1532 */         thisHasNulls = (thisHasNulls) || 
/* 1533 */           (this.extra[3][i] != 0L) || 
/* 1534 */           (this.extra[4][i] != 0L) || 
/* 1535 */           (this.extra[5][i] != 0L);
/*      */       }
/*      */ 
/* 1542 */       for (; i < copyLimit; i++) {
/* 1543 */         this.extra[2][i] = 0L;
/*      */         long tmp1783_1782 = otherInits.extra[3][i]; long b2 = tmp1783_1782;
/*      */         long b3;
/*      */         long b1;
/*      */         long nb1;
/*      */         long tmp1822_1821 = ((b3 = otherInits.extra[4][i]) ^ 0xFFFFFFFF | (nb1 = (b1 = otherInits.extra[2][i]) ^ 0xFFFFFFFF)); long nb3 = tmp1822_1821; this.extra[3][i] = (tmp1783_1782 & tmp1822_1821);
/*      */         long tmp1843_1842 = (b2 ^ 0xFFFFFFFF); long nb2 = tmp1843_1842;
/*      */         long tmp1855_1854 = otherInits.extra[5][i]; long b4 = tmp1855_1854; this.extra[4][i] = (b3 & (tmp1843_1842 & tmp1855_1854 | nb1) | b1 & nb2 & (b4 ^ 0xFFFFFFFF));
/* 1546 */         this.extra[5][i] = ((nb3 | nb2) & nb1 & b4 | b1 & nb3 & nb2);
/* 1547 */         thisHasNulls = (thisHasNulls) || 
/* 1548 */           (this.extra[3][i] != 0L) || 
/* 1549 */           (this.extra[4][i] != 0L) || 
/* 1550 */           (this.extra[5][i] != 0L);
/*      */       }
/*      */ 
/* 1557 */       for (; i < resetLimit; i++) {
/* 1558 */         long a1 = this.extra[2][i];
/* 1559 */         this.extra[2][i] = 0L;
/*      */         long tmp2009_2008 = this.extra[3][i]; long a2 = tmp2009_2008;
/*      */         long a3;
/*      */         long na1;
/*      */         long tmp2038_2037 = ((a3 = this.extra[4][i]) ^ 0xFFFFFFFF | (na1 = a1 ^ 0xFFFFFFFF)); long na3 = tmp2038_2037; this.extra[3][i] = (tmp2009_2008 & tmp2038_2037);
/*      */         long tmp2059_2058 = (a2 ^ 0xFFFFFFFF); long na2 = tmp2059_2058;
/*      */         long tmp2071_2070 = this.extra[5][i]; long a4 = tmp2071_2070; this.extra[4][i] = (a3 & (tmp2059_2058 & tmp2071_2070 | na1) | a1 & na2 & (a4 ^ 0xFFFFFFFF));
/* 1562 */         this.extra[5][i] = ((na3 | na2) & na1 & a4 | a1 & na3 & na2);
/* 1563 */         thisHasNulls = (thisHasNulls) || 
/* 1564 */           (this.extra[3][i] != 0L) || 
/* 1565 */           (this.extra[4][i] != 0L) || 
/* 1566 */           (this.extra[5][i] != 0L);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1574 */     if (thisHasNulls) {
/* 1575 */       this.tagBits |= 2;
/*      */     }
/*      */     else {
/* 1578 */       this.tagBits &= -3;
/*      */     }
/* 1580 */     return this;
/*      */   }
/*      */ 
/*      */   static int numberOfEnclosingFields(ReferenceBinding type)
/*      */   {
/* 1587 */     int count = 0;
/* 1588 */     type = type.enclosingType();
/* 1589 */     while (type != null) {
/* 1590 */       count += type.fieldCount();
/* 1591 */       type = type.enclosingType();
/*      */     }
/* 1593 */     return count;
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo nullInfoLessUnconditionalCopy() {
/* 1597 */     if (this == DEAD_END) {
/* 1598 */       return this;
/*      */     }
/* 1600 */     UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
/* 1601 */     copy.definiteInits = this.definiteInits;
/* 1602 */     copy.potentialInits = this.potentialInits;
/* 1603 */     this.tagBits &= -3;
/* 1604 */     copy.maxFieldCount = this.maxFieldCount;
/* 1605 */     if (this.extra != null)
/*      */     {
/* 1607 */       copy.extra = new long[6][];
/*      */       int length;
/* 1608 */       System.arraycopy(this.extra[0], 0, 
/* 1609 */         copy.extra[0] =  = 
/* 1610 */         new long[length = this.extra[0].length], 0, length);
/* 1611 */       System.arraycopy(this.extra[1], 0, 
/* 1612 */         copy.extra[1] =  = new long[length], 0, length);
/* 1613 */       for (int j = 2; j < 6; j++) {
/* 1614 */         copy.extra[j] = new long[length];
/*      */       }
/*      */     }
/* 1617 */     return copy;
/*      */   }
/*      */ 
/*      */   public FlowInfo safeInitsWhenTrue() {
/* 1621 */     return copy();
/*      */   }
/*      */ 
/*      */   public FlowInfo setReachMode(int reachMode) {
/* 1625 */     if (this == DEAD_END) {
/* 1626 */       return this;
/*      */     }
/* 1628 */     if (reachMode == 0) {
/* 1629 */       this.tagBits &= -2;
/*      */     } else {
/* 1631 */       if ((this.tagBits & 0x1) == 0)
/*      */       {
/* 1634 */         this.potentialInits = 0L;
/* 1635 */         if (this.extra != null) {
/* 1636 */           int i = 0; int length = this.extra[0].length;
/* 1637 */           for (; i < length; i++) {
/* 1638 */             this.extra[1][i] = 0L;
/*      */           }
/*      */         }
/*      */       }
/* 1642 */       this.tagBits |= 1;
/*      */     }
/* 1644 */     return this;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1649 */     if (this == DEAD_END) {
/* 1650 */       return "FlowInfo.DEAD_END";
/*      */     }
/* 1652 */     if ((this.tagBits & 0x2) != 0) {
/* 1653 */       if (this.extra == null) {
/* 1654 */         return "FlowInfo<def: " + this.definiteInits + 
/* 1655 */           ", pot: " + this.potentialInits + 
/* 1656 */           ", reachable:" + ((this.tagBits & 0x1) == 0) + 
/* 1657 */           ", null: " + this.nullBit1 + 
/* 1658 */           this.nullBit2 + this.nullBit3 + this.nullBit4 + 
/* 1659 */           ">";
/*      */       }
/*      */ 
/* 1662 */       String def = "FlowInfo<def:[" + this.definiteInits;
/* 1663 */       String pot = "], pot:[" + this.potentialInits;
/* 1664 */       String nullS = ", null:[" + this.nullBit1 + 
/* 1665 */         this.nullBit2 + this.nullBit3 + this.nullBit4;
/*      */ 
/* 1667 */       int i = 0;
/*      */ 
/* 1669 */       int ceil = this.extra[0].length > 3 ? 
/* 1668 */         3 : 
/* 1669 */         this.extra[0].length;
/* 1670 */       for (; i < ceil; i++) {
/* 1671 */         def = def + "," + this.extra[0][i];
/* 1672 */         pot = pot + "," + this.extra[1][i];
/* 1673 */         nullS = nullS + "," + this.extra[2][i] + 
/* 1674 */           this.extra[3][i] + this.extra[4][i] + this.extra[5][i];
/*      */       }
/* 1676 */       if (ceil < this.extra[0].length) {
/* 1677 */         def = def + ",...";
/* 1678 */         pot = pot + ",...";
/* 1679 */         nullS = nullS + ",...";
/*      */       }
/* 1681 */       return def + pot + 
/* 1682 */         "], reachable:" + ((this.tagBits & 0x1) == 0) + 
/* 1683 */         nullS + 
/* 1684 */         "]>";
/*      */     }
/*      */ 
/* 1688 */     if (this.extra == null) {
/* 1689 */       return "FlowInfo<def: " + this.definiteInits + 
/* 1690 */         ", pot: " + this.potentialInits + 
/* 1691 */         ", reachable:" + ((this.tagBits & 0x1) == 0) + 
/* 1692 */         ", no null info>";
/*      */     }
/*      */ 
/* 1695 */     String def = "FlowInfo<def:[" + this.definiteInits;
/* 1696 */     String pot = "], pot:[" + this.potentialInits;
/*      */ 
/* 1698 */     int i = 0;
/*      */ 
/* 1700 */     int ceil = this.extra[0].length > 3 ? 
/* 1699 */       3 : 
/* 1700 */       this.extra[0].length;
/* 1701 */     for (; i < ceil; i++) {
/* 1702 */       def = def + "," + this.extra[0][i];
/* 1703 */       pot = pot + "," + this.extra[1][i];
/*      */     }
/* 1705 */     if (ceil < this.extra[0].length) {
/* 1706 */       def = def + ",...";
/* 1707 */       pot = pot + ",...";
/*      */     }
/* 1709 */     return def + pot + 
/* 1710 */       "], reachable:" + ((this.tagBits & 0x1) == 0) + 
/* 1711 */       ", no null info>";
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo unconditionalCopy()
/*      */   {
/* 1717 */     return (UnconditionalFlowInfo)copy();
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo unconditionalFieldLessCopy()
/*      */   {
/* 1722 */     UnconditionalFlowInfo copy = new UnconditionalFlowInfo();
/* 1723 */     copy.tagBits = this.tagBits;
/* 1724 */     copy.maxFieldCount = this.maxFieldCount;
/* 1725 */     int limit = this.maxFieldCount;
/* 1726 */     if (limit < 64)
/*      */     {
/*      */       long mask;
/* 1728 */       this.definiteInits &= (mask = (1L << limit) - 1L ^ 0xFFFFFFFF);
/* 1729 */       this.potentialInits &= mask;
/* 1730 */       this.nullBit1 &= mask;
/* 1731 */       this.nullBit2 &= mask;
/* 1732 */       this.nullBit3 &= mask;
/* 1733 */       this.nullBit4 &= mask;
/*      */     }
/*      */ 
/* 1736 */     if (this.extra == null)
/* 1737 */       return copy;
/*      */     int vectorIndex;
/*      */     int length;
/* 1741 */     if ((vectorIndex = limit / 64 - 1) >= 
/* 1741 */       (length = this.extra[0].length)) {
/* 1742 */       return copy;
/*      */     }
/*      */ 
/* 1745 */     copy.extra = new long[6][];
/*      */     int copyStart;
/* 1746 */     if ((copyStart = vectorIndex + 1) < length) {
/* 1747 */       int copyLength = length - copyStart;
/* 1748 */       for (int j = 0; j < 6; j++) {
/* 1749 */         System.arraycopy(this.extra[j], copyStart, 
/* 1750 */           copy.extra[j] =  = new long[length], copyStart, 
/* 1751 */           copyLength);
/*      */       }
/*      */     }
/* 1754 */     else if (vectorIndex >= 0) {
/* 1755 */       for (int j = 0; j < 6; j++) {
/* 1756 */         copy.extra[j] = new long[length];
/*      */       }
/*      */     }
/* 1759 */     if (vectorIndex >= 0) {
/* 1760 */       long mask = (1L << limit % 64) - 1L ^ 0xFFFFFFFF;
/* 1761 */       for (int j = 0; j < 6; j++) {
/* 1762 */         copy.extra[j][vectorIndex] = 
/* 1763 */           (this.extra[j][vectorIndex] & mask);
/*      */       }
/*      */     }
/* 1766 */     return copy;
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo unconditionalInits()
/*      */   {
/* 1771 */     return this;
/*      */   }
/*      */ 
/*      */   public UnconditionalFlowInfo unconditionalInitsWithoutSideEffect() {
/* 1775 */     return this;
/*      */   }
/*      */ 
/*      */   public static class AssertionFailedException extends RuntimeException
/*      */   {
/*      */     private static final long serialVersionUID = 1827352841030089703L;
/*      */ 
/*      */     public AssertionFailedException(String message)
/*      */     {
/*   35 */       super();
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo
 * JD-Core Version:    0.6.0
 */