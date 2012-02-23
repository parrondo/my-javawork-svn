/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public final class BitUtil
/*     */ {
/* 689 */   public static final byte[] ntzTable = { 8, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 7, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0 };
/*     */ 
/* 782 */   public static final byte[] nlzTable = { 8, 7, 6, 6, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
/*     */ 
/*     */   public static int pop(long x)
/*     */   {
/*  43 */     x -= (x >>> 1 & 0x55555555);
/*  44 */     x = (x & 0x33333333) + (x >>> 2 & 0x33333333);
/*  45 */     x = x + (x >>> 4) & 0xF0F0F0F;
/*  46 */     x += (x >>> 8);
/*  47 */     x += (x >>> 16);
/*  48 */     x += (x >>> 32);
/*  49 */     return (int)x & 0x7F;
/*     */   }
/*     */ 
/*     */   public static long pop_array(long[] A, int wordOffset, int numWords)
/*     */   {
/*  68 */     int n = wordOffset + numWords;
/*  69 */     long tot = 0L; long tot8 = 0L;
/*  70 */     long ones = 0L; long twos = 0L; long fours = 0L;
/*     */ 
/*  73 */     for (int i = wordOffset; i <= n - 8; i += 8)
/*     */     {
/*  84 */       long b = A[i]; long c = A[(i + 1)];
/*  85 */       long u = ones ^ b;
/*  86 */       long twosA = ones & b | u & c;
/*  87 */       ones = u ^ c;
/*     */ 
/*  91 */       long b = A[(i + 2)]; long c = A[(i + 3)];
/*  92 */       long u = ones ^ b;
/*  93 */       long twosB = ones & b | u & c;
/*  94 */       ones = u ^ c;
/*     */ 
/*  98 */       long u = twos ^ twosA;
/*  99 */       long foursA = twos & twosA | u & twosB;
/* 100 */       twos = u ^ twosB;
/*     */ 
/* 104 */       long b = A[(i + 4)]; long c = A[(i + 5)];
/* 105 */       long u = ones ^ b;
/* 106 */       twosA = ones & b | u & c;
/* 107 */       ones = u ^ c;
/*     */ 
/* 111 */       long b = A[(i + 6)]; long c = A[(i + 7)];
/* 112 */       long u = ones ^ b;
/* 113 */       twosB = ones & b | u & c;
/* 114 */       ones = u ^ c;
/*     */ 
/* 118 */       long u = twos ^ twosA;
/* 119 */       long foursB = twos & twosA | u & twosB;
/* 120 */       twos = u ^ twosB;
/*     */ 
/* 125 */       long u = fours ^ foursA;
/* 126 */       long eights = fours & foursA | u & foursB;
/* 127 */       fours = u ^ foursB;
/*     */ 
/* 129 */       tot8 += pop(eights);
/*     */     }
/*     */ 
/* 138 */     if (i <= n - 4)
/*     */     {
/* 141 */       long b = A[i]; long c = A[(i + 1)];
/* 142 */       long u = ones ^ b;
/* 143 */       long twosA = ones & b | u & c;
/* 144 */       ones = u ^ c;
/*     */ 
/* 147 */       long b = A[(i + 2)]; long c = A[(i + 3)];
/* 148 */       long u = ones ^ b;
/* 149 */       long twosB = ones & b | u & c;
/* 150 */       ones = u ^ c;
/*     */ 
/* 153 */       long u = twos ^ twosA;
/* 154 */       long foursA = twos & twosA | u & twosB;
/* 155 */       twos = u ^ twosB;
/*     */ 
/* 157 */       long eights = fours & foursA;
/* 158 */       fours ^= foursA;
/*     */ 
/* 160 */       tot8 += pop(eights);
/* 161 */       i += 4;
/*     */     }
/*     */ 
/* 164 */     if (i <= n - 2) {
/* 165 */       long b = A[i]; long c = A[(i + 1)];
/* 166 */       long u = ones ^ b;
/* 167 */       long twosA = ones & b | u & c;
/* 168 */       ones = u ^ c;
/*     */ 
/* 170 */       long foursA = twos & twosA;
/* 171 */       twos ^= twosA;
/*     */ 
/* 173 */       long eights = fours & foursA;
/* 174 */       fours ^= foursA;
/*     */ 
/* 176 */       tot8 += pop(eights);
/* 177 */       i += 2;
/*     */     }
/*     */ 
/* 180 */     if (i < n) {
/* 181 */       tot += pop(A[i]);
/*     */     }
/*     */ 
/* 184 */     tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);
/*     */ 
/* 189 */     return tot;
/*     */   }
/*     */ 
/*     */   public static long pop_intersect(long[] A, long[] B, int wordOffset, int numWords)
/*     */   {
/* 197 */     int n = wordOffset + numWords;
/* 198 */     long tot = 0L; long tot8 = 0L;
/* 199 */     long ones = 0L; long twos = 0L; long fours = 0L;
/*     */ 
/* 202 */     for (int i = wordOffset; i <= n - 8; i += 8)
/*     */     {
/* 207 */       long b = A[i] & B[i]; long c = A[(i + 1)] & B[(i + 1)];
/* 208 */       long u = ones ^ b;
/* 209 */       long twosA = ones & b | u & c;
/* 210 */       ones = u ^ c;
/*     */ 
/* 214 */       long b = A[(i + 2)] & B[(i + 2)]; long c = A[(i + 3)] & B[(i + 3)];
/* 215 */       long u = ones ^ b;
/* 216 */       long twosB = ones & b | u & c;
/* 217 */       ones = u ^ c;
/*     */ 
/* 221 */       long u = twos ^ twosA;
/* 222 */       long foursA = twos & twosA | u & twosB;
/* 223 */       twos = u ^ twosB;
/*     */ 
/* 227 */       long b = A[(i + 4)] & B[(i + 4)]; long c = A[(i + 5)] & B[(i + 5)];
/* 228 */       long u = ones ^ b;
/* 229 */       twosA = ones & b | u & c;
/* 230 */       ones = u ^ c;
/*     */ 
/* 234 */       long b = A[(i + 6)] & B[(i + 6)]; long c = A[(i + 7)] & B[(i + 7)];
/* 235 */       long u = ones ^ b;
/* 236 */       twosB = ones & b | u & c;
/* 237 */       ones = u ^ c;
/*     */ 
/* 241 */       long u = twos ^ twosA;
/* 242 */       long foursB = twos & twosA | u & twosB;
/* 243 */       twos = u ^ twosB;
/*     */ 
/* 248 */       long u = fours ^ foursA;
/* 249 */       long eights = fours & foursA | u & foursB;
/* 250 */       fours = u ^ foursB;
/*     */ 
/* 252 */       tot8 += pop(eights);
/*     */     }
/*     */ 
/* 256 */     if (i <= n - 4)
/*     */     {
/* 259 */       long b = A[i] & B[i]; long c = A[(i + 1)] & B[(i + 1)];
/* 260 */       long u = ones ^ b;
/* 261 */       long twosA = ones & b | u & c;
/* 262 */       ones = u ^ c;
/*     */ 
/* 265 */       long b = A[(i + 2)] & B[(i + 2)]; long c = A[(i + 3)] & B[(i + 3)];
/* 266 */       long u = ones ^ b;
/* 267 */       long twosB = ones & b | u & c;
/* 268 */       ones = u ^ c;
/*     */ 
/* 271 */       long u = twos ^ twosA;
/* 272 */       long foursA = twos & twosA | u & twosB;
/* 273 */       twos = u ^ twosB;
/*     */ 
/* 275 */       long eights = fours & foursA;
/* 276 */       fours ^= foursA;
/*     */ 
/* 278 */       tot8 += pop(eights);
/* 279 */       i += 4;
/*     */     }
/*     */ 
/* 282 */     if (i <= n - 2) {
/* 283 */       long b = A[i] & B[i]; long c = A[(i + 1)] & B[(i + 1)];
/* 284 */       long u = ones ^ b;
/* 285 */       long twosA = ones & b | u & c;
/* 286 */       ones = u ^ c;
/*     */ 
/* 288 */       long foursA = twos & twosA;
/* 289 */       twos ^= twosA;
/*     */ 
/* 291 */       long eights = fours & foursA;
/* 292 */       fours ^= foursA;
/*     */ 
/* 294 */       tot8 += pop(eights);
/* 295 */       i += 2;
/*     */     }
/*     */ 
/* 298 */     if (i < n) {
/* 299 */       tot += pop(A[i] & B[i]);
/*     */     }
/*     */ 
/* 302 */     tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);
/*     */ 
/* 307 */     return tot;
/*     */   }
/*     */ 
/*     */   public static long pop_union(long[] A, long[] B, int wordOffset, int numWords)
/*     */   {
/* 315 */     int n = wordOffset + numWords;
/* 316 */     long tot = 0L; long tot8 = 0L;
/* 317 */     long ones = 0L; long twos = 0L; long fours = 0L;
/*     */ 
/* 320 */     for (int i = wordOffset; i <= n - 8; i += 8)
/*     */     {
/* 331 */       long b = A[i] | B[i]; long c = A[(i + 1)] | B[(i + 1)];
/* 332 */       long u = ones ^ b;
/* 333 */       long twosA = ones & b | u & c;
/* 334 */       ones = u ^ c;
/*     */ 
/* 338 */       long b = A[(i + 2)] | B[(i + 2)]; long c = A[(i + 3)] | B[(i + 3)];
/* 339 */       long u = ones ^ b;
/* 340 */       long twosB = ones & b | u & c;
/* 341 */       ones = u ^ c;
/*     */ 
/* 345 */       long u = twos ^ twosA;
/* 346 */       long foursA = twos & twosA | u & twosB;
/* 347 */       twos = u ^ twosB;
/*     */ 
/* 351 */       long b = A[(i + 4)] | B[(i + 4)]; long c = A[(i + 5)] | B[(i + 5)];
/* 352 */       long u = ones ^ b;
/* 353 */       twosA = ones & b | u & c;
/* 354 */       ones = u ^ c;
/*     */ 
/* 358 */       long b = A[(i + 6)] | B[(i + 6)]; long c = A[(i + 7)] | B[(i + 7)];
/* 359 */       long u = ones ^ b;
/* 360 */       twosB = ones & b | u & c;
/* 361 */       ones = u ^ c;
/*     */ 
/* 365 */       long u = twos ^ twosA;
/* 366 */       long foursB = twos & twosA | u & twosB;
/* 367 */       twos = u ^ twosB;
/*     */ 
/* 372 */       long u = fours ^ foursA;
/* 373 */       long eights = fours & foursA | u & foursB;
/* 374 */       fours = u ^ foursB;
/*     */ 
/* 376 */       tot8 += pop(eights);
/*     */     }
/*     */ 
/* 380 */     if (i <= n - 4)
/*     */     {
/* 383 */       long b = A[i] | B[i]; long c = A[(i + 1)] | B[(i + 1)];
/* 384 */       long u = ones ^ b;
/* 385 */       long twosA = ones & b | u & c;
/* 386 */       ones = u ^ c;
/*     */ 
/* 389 */       long b = A[(i + 2)] | B[(i + 2)]; long c = A[(i + 3)] | B[(i + 3)];
/* 390 */       long u = ones ^ b;
/* 391 */       long twosB = ones & b | u & c;
/* 392 */       ones = u ^ c;
/*     */ 
/* 395 */       long u = twos ^ twosA;
/* 396 */       long foursA = twos & twosA | u & twosB;
/* 397 */       twos = u ^ twosB;
/*     */ 
/* 399 */       long eights = fours & foursA;
/* 400 */       fours ^= foursA;
/*     */ 
/* 402 */       tot8 += pop(eights);
/* 403 */       i += 4;
/*     */     }
/*     */ 
/* 406 */     if (i <= n - 2) {
/* 407 */       long b = A[i] | B[i]; long c = A[(i + 1)] | B[(i + 1)];
/* 408 */       long u = ones ^ b;
/* 409 */       long twosA = ones & b | u & c;
/* 410 */       ones = u ^ c;
/*     */ 
/* 412 */       long foursA = twos & twosA;
/* 413 */       twos ^= twosA;
/*     */ 
/* 415 */       long eights = fours & foursA;
/* 416 */       fours ^= foursA;
/*     */ 
/* 418 */       tot8 += pop(eights);
/* 419 */       i += 2;
/*     */     }
/*     */ 
/* 422 */     if (i < n) {
/* 423 */       tot += pop(A[i] | B[i]);
/*     */     }
/*     */ 
/* 426 */     tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);
/*     */ 
/* 431 */     return tot;
/*     */   }
/*     */ 
/*     */   public static long pop_andnot(long[] A, long[] B, int wordOffset, int numWords)
/*     */   {
/* 439 */     int n = wordOffset + numWords;
/* 440 */     long tot = 0L; long tot8 = 0L;
/* 441 */     long ones = 0L; long twos = 0L; long fours = 0L;
/*     */ 
/* 444 */     for (int i = wordOffset; i <= n - 8; i += 8)
/*     */     {
/* 455 */       long b = A[i] & (B[i] ^ 0xFFFFFFFF); long c = A[(i + 1)] & (B[(i + 1)] ^ 0xFFFFFFFF);
/* 456 */       long u = ones ^ b;
/* 457 */       long twosA = ones & b | u & c;
/* 458 */       ones = u ^ c;
/*     */ 
/* 462 */       long b = A[(i + 2)] & (B[(i + 2)] ^ 0xFFFFFFFF); long c = A[(i + 3)] & (B[(i + 3)] ^ 0xFFFFFFFF);
/* 463 */       long u = ones ^ b;
/* 464 */       long twosB = ones & b | u & c;
/* 465 */       ones = u ^ c;
/*     */ 
/* 469 */       long u = twos ^ twosA;
/* 470 */       long foursA = twos & twosA | u & twosB;
/* 471 */       twos = u ^ twosB;
/*     */ 
/* 475 */       long b = A[(i + 4)] & (B[(i + 4)] ^ 0xFFFFFFFF); long c = A[(i + 5)] & (B[(i + 5)] ^ 0xFFFFFFFF);
/* 476 */       long u = ones ^ b;
/* 477 */       twosA = ones & b | u & c;
/* 478 */       ones = u ^ c;
/*     */ 
/* 482 */       long b = A[(i + 6)] & (B[(i + 6)] ^ 0xFFFFFFFF); long c = A[(i + 7)] & (B[(i + 7)] ^ 0xFFFFFFFF);
/* 483 */       long u = ones ^ b;
/* 484 */       twosB = ones & b | u & c;
/* 485 */       ones = u ^ c;
/*     */ 
/* 489 */       long u = twos ^ twosA;
/* 490 */       long foursB = twos & twosA | u & twosB;
/* 491 */       twos = u ^ twosB;
/*     */ 
/* 496 */       long u = fours ^ foursA;
/* 497 */       long eights = fours & foursA | u & foursB;
/* 498 */       fours = u ^ foursB;
/*     */ 
/* 500 */       tot8 += pop(eights);
/*     */     }
/*     */ 
/* 504 */     if (i <= n - 4)
/*     */     {
/* 507 */       long b = A[i] & (B[i] ^ 0xFFFFFFFF); long c = A[(i + 1)] & (B[(i + 1)] ^ 0xFFFFFFFF);
/* 508 */       long u = ones ^ b;
/* 509 */       long twosA = ones & b | u & c;
/* 510 */       ones = u ^ c;
/*     */ 
/* 513 */       long b = A[(i + 2)] & (B[(i + 2)] ^ 0xFFFFFFFF); long c = A[(i + 3)] & (B[(i + 3)] ^ 0xFFFFFFFF);
/* 514 */       long u = ones ^ b;
/* 515 */       long twosB = ones & b | u & c;
/* 516 */       ones = u ^ c;
/*     */ 
/* 519 */       long u = twos ^ twosA;
/* 520 */       long foursA = twos & twosA | u & twosB;
/* 521 */       twos = u ^ twosB;
/*     */ 
/* 523 */       long eights = fours & foursA;
/* 524 */       fours ^= foursA;
/*     */ 
/* 526 */       tot8 += pop(eights);
/* 527 */       i += 4;
/*     */     }
/*     */ 
/* 530 */     if (i <= n - 2) {
/* 531 */       long b = A[i] & (B[i] ^ 0xFFFFFFFF); long c = A[(i + 1)] & (B[(i + 1)] ^ 0xFFFFFFFF);
/* 532 */       long u = ones ^ b;
/* 533 */       long twosA = ones & b | u & c;
/* 534 */       ones = u ^ c;
/*     */ 
/* 536 */       long foursA = twos & twosA;
/* 537 */       twos ^= twosA;
/*     */ 
/* 539 */       long eights = fours & foursA;
/* 540 */       fours ^= foursA;
/*     */ 
/* 542 */       tot8 += pop(eights);
/* 543 */       i += 2;
/*     */     }
/*     */ 
/* 546 */     if (i < n) {
/* 547 */       tot += pop(A[i] & (B[i] ^ 0xFFFFFFFF));
/*     */     }
/*     */ 
/* 550 */     tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);
/*     */ 
/* 555 */     return tot;
/*     */   }
/*     */ 
/*     */   public static long pop_xor(long[] A, long[] B, int wordOffset, int numWords) {
/* 559 */     int n = wordOffset + numWords;
/* 560 */     long tot = 0L; long tot8 = 0L;
/* 561 */     long ones = 0L; long twos = 0L; long fours = 0L;
/*     */ 
/* 564 */     for (int i = wordOffset; i <= n - 8; i += 8)
/*     */     {
/* 575 */       long b = A[i] ^ B[i]; long c = A[(i + 1)] ^ B[(i + 1)];
/* 576 */       long u = ones ^ b;
/* 577 */       long twosA = ones & b | u & c;
/* 578 */       ones = u ^ c;
/*     */ 
/* 582 */       long b = A[(i + 2)] ^ B[(i + 2)]; long c = A[(i + 3)] ^ B[(i + 3)];
/* 583 */       long u = ones ^ b;
/* 584 */       long twosB = ones & b | u & c;
/* 585 */       ones = u ^ c;
/*     */ 
/* 589 */       long u = twos ^ twosA;
/* 590 */       long foursA = twos & twosA | u & twosB;
/* 591 */       twos = u ^ twosB;
/*     */ 
/* 595 */       long b = A[(i + 4)] ^ B[(i + 4)]; long c = A[(i + 5)] ^ B[(i + 5)];
/* 596 */       long u = ones ^ b;
/* 597 */       twosA = ones & b | u & c;
/* 598 */       ones = u ^ c;
/*     */ 
/* 602 */       long b = A[(i + 6)] ^ B[(i + 6)]; long c = A[(i + 7)] ^ B[(i + 7)];
/* 603 */       long u = ones ^ b;
/* 604 */       twosB = ones & b | u & c;
/* 605 */       ones = u ^ c;
/*     */ 
/* 609 */       long u = twos ^ twosA;
/* 610 */       long foursB = twos & twosA | u & twosB;
/* 611 */       twos = u ^ twosB;
/*     */ 
/* 616 */       long u = fours ^ foursA;
/* 617 */       long eights = fours & foursA | u & foursB;
/* 618 */       fours = u ^ foursB;
/*     */ 
/* 620 */       tot8 += pop(eights);
/*     */     }
/*     */ 
/* 624 */     if (i <= n - 4)
/*     */     {
/* 627 */       long b = A[i] ^ B[i]; long c = A[(i + 1)] ^ B[(i + 1)];
/* 628 */       long u = ones ^ b;
/* 629 */       long twosA = ones & b | u & c;
/* 630 */       ones = u ^ c;
/*     */ 
/* 633 */       long b = A[(i + 2)] ^ B[(i + 2)]; long c = A[(i + 3)] ^ B[(i + 3)];
/* 634 */       long u = ones ^ b;
/* 635 */       long twosB = ones & b | u & c;
/* 636 */       ones = u ^ c;
/*     */ 
/* 639 */       long u = twos ^ twosA;
/* 640 */       long foursA = twos & twosA | u & twosB;
/* 641 */       twos = u ^ twosB;
/*     */ 
/* 643 */       long eights = fours & foursA;
/* 644 */       fours ^= foursA;
/*     */ 
/* 646 */       tot8 += pop(eights);
/* 647 */       i += 4;
/*     */     }
/*     */ 
/* 650 */     if (i <= n - 2) {
/* 651 */       long b = A[i] ^ B[i]; long c = A[(i + 1)] ^ B[(i + 1)];
/* 652 */       long u = ones ^ b;
/* 653 */       long twosA = ones & b | u & c;
/* 654 */       ones = u ^ c;
/*     */ 
/* 656 */       long foursA = twos & twosA;
/* 657 */       twos ^= twosA;
/*     */ 
/* 659 */       long eights = fours & foursA;
/* 660 */       fours ^= foursA;
/*     */ 
/* 662 */       tot8 += pop(eights);
/* 663 */       i += 2;
/*     */     }
/*     */ 
/* 666 */     if (i < n) {
/* 667 */       tot += pop(A[i] ^ B[i]);
/*     */     }
/*     */ 
/* 670 */     tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);
/*     */ 
/* 675 */     return tot;
/*     */   }
/*     */ 
/*     */   public static int ntz(long val)
/*     */   {
/* 705 */     int lower = (int)val;
/* 706 */     int lowByte = lower & 0xFF;
/* 707 */     if (lowByte != 0) return ntzTable[lowByte];
/*     */ 
/* 709 */     if (lower != 0) {
/* 710 */       lowByte = lower >>> 8 & 0xFF;
/* 711 */       if (lowByte != 0) return ntzTable[lowByte] + 8;
/* 712 */       lowByte = lower >>> 16 & 0xFF;
/* 713 */       if (lowByte != 0) return ntzTable[lowByte] + 16;
/*     */ 
/* 716 */       return ntzTable[(lower >>> 24)] + 24;
/*     */     }
/*     */ 
/* 719 */     int upper = (int)(val >> 32);
/* 720 */     lowByte = upper & 0xFF;
/* 721 */     if (lowByte != 0) return ntzTable[lowByte] + 32;
/* 722 */     lowByte = upper >>> 8 & 0xFF;
/* 723 */     if (lowByte != 0) return ntzTable[lowByte] + 40;
/* 724 */     lowByte = upper >>> 16 & 0xFF;
/* 725 */     if (lowByte != 0) return ntzTable[lowByte] + 48;
/*     */ 
/* 728 */     return ntzTable[(upper >>> 24)] + 56;
/*     */   }
/*     */ 
/*     */   public static int ntz(int val)
/*     */   {
/* 738 */     int lowByte = val & 0xFF;
/* 739 */     if (lowByte != 0) return ntzTable[lowByte];
/* 740 */     lowByte = val >>> 8 & 0xFF;
/* 741 */     if (lowByte != 0) return ntzTable[lowByte] + 8;
/* 742 */     lowByte = val >>> 16 & 0xFF;
/* 743 */     if (lowByte != 0) return ntzTable[lowByte] + 16;
/*     */ 
/* 746 */     return ntzTable[(val >>> 24)] + 24;
/*     */   }
/*     */ 
/*     */   public static int ntz2(long x)
/*     */   {
/* 754 */     int n = 0;
/* 755 */     int y = (int)x;
/* 756 */     if (y == 0) { n += 32; y = (int)(x >>> 32); }
/* 757 */     if ((y & 0xFFFF) == 0) { n += 16; y >>>= 16; }
/* 758 */     if ((y & 0xFF) == 0) { n += 8; y >>>= 8; }
/* 759 */     return ntzTable[(y & 0xFF)] + n;
/*     */   }
/*     */ 
/*     */   public static int ntz3(long x)
/*     */   {
/* 769 */     int n = 1;
/*     */ 
/* 772 */     int y = (int)x;
/* 773 */     if (y == 0) { n += 32; y = (int)(x >>> 32); }
/* 774 */     if ((y & 0xFFFF) == 0) { n += 16; y >>>= 16; }
/* 775 */     if ((y & 0xFF) == 0) { n += 8; y >>>= 8; }
/* 776 */     if ((y & 0xF) == 0) { n += 4; y >>>= 4; }
/* 777 */     if ((y & 0x3) == 0) { n += 2; y >>>= 2; }
/* 778 */     return n - (y & 0x1);
/*     */   }
/*     */ 
/*     */   public static int nlz(long x)
/*     */   {
/* 787 */     int n = 0;
/*     */ 
/* 789 */     int y = (int)(x >>> 32);
/* 790 */     if (y == 0) { n += 32; y = (int)x; }
/* 791 */     if ((y & 0xFFFF0000) == 0) { n += 16; y <<= 16; }
/* 792 */     if ((y & 0xFF000000) == 0) { n += 8; y <<= 8; }
/* 793 */     return n + nlzTable[(y >>> 24)];
/*     */   }
/*     */ 
/*     */   public static boolean isPowerOfTwo(int v)
/*     */   {
/* 806 */     return (v & v - 1) == 0;
/*     */   }
/*     */ 
/*     */   public static boolean isPowerOfTwo(long v)
/*     */   {
/* 811 */     return (v & v - 1L) == 0L;
/*     */   }
/*     */ 
/*     */   public static int nextHighestPowerOfTwo(int v)
/*     */   {
/* 816 */     v--;
/* 817 */     v |= v >> 1;
/* 818 */     v |= v >> 2;
/* 819 */     v |= v >> 4;
/* 820 */     v |= v >> 8;
/* 821 */     v |= v >> 16;
/* 822 */     v++;
/* 823 */     return v;
/*     */   }
/*     */ 
/*     */   public static long nextHighestPowerOfTwo(long v)
/*     */   {
/* 828 */     v -= 1L;
/* 829 */     v |= v >> 1;
/* 830 */     v |= v >> 2;
/* 831 */     v |= v >> 4;
/* 832 */     v |= v >> 8;
/* 833 */     v |= v >> 16;
/* 834 */     v |= v >> 32;
/* 835 */     v += 1L;
/* 836 */     return v;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.BitUtil
 * JD-Core Version:    0.6.0
 */