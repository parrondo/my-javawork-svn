/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Wildcard;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public abstract class TypeConverter
/*     */ {
/*     */   int namePos;
/*     */   protected ProblemReporter problemReporter;
/*     */   protected boolean has1_5Compliance;
/*     */   private char memberTypeSeparator;
/*     */ 
/*     */   protected TypeConverter(ProblemReporter problemReporter, char memberTypeSeparator)
/*     */   {
/*  42 */     this.problemReporter = problemReporter;
/*  43 */     this.has1_5Compliance = (problemReporter.options.complianceLevel >= 3211264L);
/*  44 */     this.memberTypeSeparator = memberTypeSeparator;
/*     */   }
/*     */ 
/*     */   private void addIdentifiers(String typeSignature, int start, int endExclusive, int identCount, ArrayList fragments) {
/*  48 */     if (identCount == 1)
/*     */     {
/*     */       char[] identifier;
/*  50 */       typeSignature.getChars(start, endExclusive, identifier = new char[endExclusive - start], 0);
/*  51 */       fragments.add(identifier);
/*     */     } else {
/*  53 */       fragments.add(extractIdentifiers(typeSignature, start, endExclusive - 1, identCount));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected ImportReference createImportReference(String[] importName, int start, int end, boolean onDemand, int modifiers)
/*     */   {
/*  66 */     int length = importName.length;
/*  67 */     long[] positions = new long[length];
/*  68 */     long position = (start << 32) + end;
/*  69 */     char[][] qImportName = new char[length][];
/*  70 */     for (int i = 0; i < length; i++) {
/*  71 */       qImportName[i] = importName[i].toCharArray();
/*  72 */       positions[i] = position;
/*     */     }
/*  74 */     return new ImportReference(
/*  75 */       qImportName, 
/*  76 */       positions, 
/*  77 */       onDemand, 
/*  78 */       modifiers);
/*     */   }
/*     */ 
/*     */   protected TypeParameter createTypeParameter(char[] typeParameterName, char[][] typeParameterBounds, int start, int end)
/*     */   {
/*  83 */     TypeParameter parameter = new TypeParameter();
/*  84 */     parameter.name = typeParameterName;
/*  85 */     parameter.sourceStart = start;
/*  86 */     parameter.sourceEnd = end;
/*  87 */     if (typeParameterBounds != null) {
/*  88 */       int length = typeParameterBounds.length;
/*  89 */       if (length > 0) {
/*  90 */         parameter.type = createTypeReference(typeParameterBounds[0], start, end);
/*  91 */         if (length > 1) {
/*  92 */           parameter.bounds = new TypeReference[length - 1];
/*  93 */           for (int i = 1; i < length; i++) {
/*  94 */             TypeReference bound = createTypeReference(typeParameterBounds[i], start, end);
/*  95 */             bound.bits |= 16;
/*  96 */             parameter.bounds[(i - 1)] = bound;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 101 */     return parameter;
/*     */   }
/*     */ 
/*     */   protected TypeReference createTypeReference(char[] typeName, int start, int end)
/*     */   {
/* 112 */     int length = typeName.length;
/* 113 */     this.namePos = 0;
/* 114 */     return decodeType(typeName, length, start, end);
/*     */   }
/*     */ 
/*     */   protected TypeReference createTypeReference(String typeSignature, int start, int end)
/*     */   {
/* 125 */     int length = typeSignature.length();
/* 126 */     this.namePos = 0;
/* 127 */     return decodeType(typeSignature, length, start, end);
/*     */   }
/*     */ 
/*     */   private TypeReference decodeType(String typeSignature, int length, int start, int end) {
/* 131 */     int identCount = 1;
/* 132 */     int dim = 0;
/* 133 */     int nameFragmentStart = this.namePos; int nameFragmentEnd = -1;
/* 134 */     boolean nameStarted = false;
/* 135 */     ArrayList fragments = null;
/* 136 */     while (this.namePos < length) {
/* 137 */       char currentChar = typeSignature.charAt(this.namePos);
/* 138 */       switch (currentChar) {
/*     */       case 'Z':
/* 140 */         if (nameStarted) break;
/* 141 */         this.namePos += 1;
/* 142 */         if (dim == 0) {
/* 143 */           return new SingleTypeReference(TypeBinding.BOOLEAN.simpleName, (start << 32) + end);
/*     */         }
/* 145 */         return new ArrayTypeReference(TypeBinding.BOOLEAN.simpleName, dim, (start << 32) + end);
/*     */       case 'B':
/* 149 */         if (nameStarted) break;
/* 150 */         this.namePos += 1;
/* 151 */         if (dim == 0) {
/* 152 */           return new SingleTypeReference(TypeBinding.BYTE.simpleName, (start << 32) + end);
/*     */         }
/* 154 */         return new ArrayTypeReference(TypeBinding.BYTE.simpleName, dim, (start << 32) + end);
/*     */       case 'C':
/* 158 */         if (nameStarted) break;
/* 159 */         this.namePos += 1;
/* 160 */         if (dim == 0) {
/* 161 */           return new SingleTypeReference(TypeBinding.CHAR.simpleName, (start << 32) + end);
/*     */         }
/* 163 */         return new ArrayTypeReference(TypeBinding.CHAR.simpleName, dim, (start << 32) + end);
/*     */       case 'D':
/* 167 */         if (nameStarted) break;
/* 168 */         this.namePos += 1;
/* 169 */         if (dim == 0) {
/* 170 */           return new SingleTypeReference(TypeBinding.DOUBLE.simpleName, (start << 32) + end);
/*     */         }
/* 172 */         return new ArrayTypeReference(TypeBinding.DOUBLE.simpleName, dim, (start << 32) + end);
/*     */       case 'F':
/* 176 */         if (nameStarted) break;
/* 177 */         this.namePos += 1;
/* 178 */         if (dim == 0) {
/* 179 */           return new SingleTypeReference(TypeBinding.FLOAT.simpleName, (start << 32) + end);
/*     */         }
/* 181 */         return new ArrayTypeReference(TypeBinding.FLOAT.simpleName, dim, (start << 32) + end);
/*     */       case 'I':
/* 185 */         if (nameStarted) break;
/* 186 */         this.namePos += 1;
/* 187 */         if (dim == 0) {
/* 188 */           return new SingleTypeReference(TypeBinding.INT.simpleName, (start << 32) + end);
/*     */         }
/* 190 */         return new ArrayTypeReference(TypeBinding.INT.simpleName, dim, (start << 32) + end);
/*     */       case 'J':
/* 194 */         if (nameStarted) break;
/* 195 */         this.namePos += 1;
/* 196 */         if (dim == 0) {
/* 197 */           return new SingleTypeReference(TypeBinding.LONG.simpleName, (start << 32) + end);
/*     */         }
/* 199 */         return new ArrayTypeReference(TypeBinding.LONG.simpleName, dim, (start << 32) + end);
/*     */       case 'S':
/* 203 */         if (nameStarted) break;
/* 204 */         this.namePos += 1;
/* 205 */         if (dim == 0) {
/* 206 */           return new SingleTypeReference(TypeBinding.SHORT.simpleName, (start << 32) + end);
/*     */         }
/* 208 */         return new ArrayTypeReference(TypeBinding.SHORT.simpleName, dim, (start << 32) + end);
/*     */       case 'V':
/* 212 */         if (nameStarted) break;
/* 213 */         this.namePos += 1;
/* 214 */         return new SingleTypeReference(TypeBinding.VOID.simpleName, (start << 32) + end);
/*     */       case 'L':
/*     */       case 'Q':
/*     */       case 'T':
/* 220 */         if (nameStarted) break;
/* 221 */         nameFragmentStart = this.namePos + 1;
/* 222 */         nameStarted = true;
/*     */ 
/* 224 */         break;
/*     */       case '*':
/* 226 */         this.namePos += 1;
/* 227 */         Wildcard result = new Wildcard(0);
/* 228 */         result.sourceStart = start;
/* 229 */         result.sourceEnd = end;
/* 230 */         return result;
/*     */       case '+':
/* 232 */         this.namePos += 1;
/* 233 */         Wildcard result = new Wildcard(1);
/* 234 */         result.bound = decodeType(typeSignature, length, start, end);
/* 235 */         result.sourceStart = start;
/* 236 */         result.sourceEnd = end;
/* 237 */         return result;
/*     */       case '-':
/* 239 */         this.namePos += 1;
/* 240 */         Wildcard result = new Wildcard(2);
/* 241 */         result.bound = decodeType(typeSignature, length, start, end);
/* 242 */         result.sourceStart = start;
/* 243 */         result.sourceEnd = end;
/* 244 */         return result;
/*     */       case '[':
/* 246 */         dim++;
/* 247 */         break;
/*     */       case ';':
/*     */       case '>':
/* 250 */         nameFragmentEnd = this.namePos - 1;
/* 251 */         this.namePos += 1;
/* 252 */         break;
/*     */       case '$':
/* 254 */         if (this.memberTypeSeparator != '$') {
/*     */           break;
/*     */         }
/*     */       case '.':
/* 258 */         if (!nameStarted) {
/* 259 */           nameFragmentStart = this.namePos + 1;
/* 260 */           nameStarted = true; } else {
/* 261 */           if (this.namePos <= nameFragmentStart) break;
/* 262 */           identCount++;
/* 263 */         }break;
/*     */       case '<':
/* 265 */         nameFragmentEnd = this.namePos - 1;
/*     */ 
/* 267 */         if (!this.has1_5Compliance) break label1127;
/* 269 */         if (fragments == null) fragments = new ArrayList(2);
/* 270 */         addIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd + 1, identCount, fragments);
/* 271 */         this.namePos += 1;
/* 272 */         TypeReference[] arguments = decodeTypeArguments(typeSignature, length, start, end);
/* 273 */         fragments.add(arguments);
/* 274 */         identCount = 1;
/* 275 */         nameStarted = false;
/*     */       }
/*     */ 
/* 279 */       this.namePos += 1;
/*     */     }
/* 281 */     label1127: if (fragments == null)
/*     */     {
/* 283 */       if (identCount == 1) {
/* 284 */         if (dim == 0) {
/* 285 */           char[] nameFragment = new char[nameFragmentEnd - nameFragmentStart + 1];
/* 286 */           typeSignature.getChars(nameFragmentStart, nameFragmentEnd + 1, nameFragment, 0);
/* 287 */           return new SingleTypeReference(nameFragment, (start << 32) + end);
/*     */         }
/* 289 */         char[] nameFragment = new char[nameFragmentEnd - nameFragmentStart + 1];
/* 290 */         typeSignature.getChars(nameFragmentStart, nameFragmentEnd + 1, nameFragment, 0);
/* 291 */         return new ArrayTypeReference(nameFragment, dim, (start << 32) + end);
/*     */       }
/*     */ 
/* 294 */       long[] positions = new long[identCount];
/* 295 */       long pos = (start << 32) + end;
/* 296 */       for (int i = 0; i < identCount; i++) {
/* 297 */         positions[i] = pos;
/*     */       }
/* 299 */       char[][] identifiers = extractIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd, identCount);
/* 300 */       if (dim == 0) {
/* 301 */         return new QualifiedTypeReference(identifiers, positions);
/*     */       }
/* 303 */       return new ArrayQualifiedTypeReference(identifiers, dim, positions);
/*     */     }
/*     */ 
/* 309 */     if (nameStarted) {
/* 310 */       addIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd + 1, identCount, fragments);
/*     */     }
/* 312 */     int fragmentLength = fragments.size();
/* 313 */     if (fragmentLength == 2) {
/* 314 */       Object firstFragment = fragments.get(0);
/* 315 */       if ((firstFragment instanceof char[]))
/*     */       {
/* 317 */         return new ParameterizedSingleTypeReference((char[])firstFragment, (TypeReference[])fragments.get(1), dim, (start << 32) + end);
/*     */       }
/*     */     }
/*     */ 
/* 321 */     identCount = 0;
/* 322 */     for (int i = 0; i < fragmentLength; i++) {
/* 323 */       Object element = fragments.get(i);
/* 324 */       if ((element instanceof char[][]))
/* 325 */         identCount += ((char[][])element).length;
/* 326 */       else if ((element instanceof char[]))
/* 327 */         identCount++;
/*     */     }
/* 329 */     char[][] tokens = new char[identCount][];
/* 330 */     TypeReference[][] arguments = new TypeReference[identCount][];
/* 331 */     int index = 0;
/* 332 */     for (int i = 0; i < fragmentLength; i++) {
/* 333 */       Object element = fragments.get(i);
/* 334 */       if ((element instanceof char[][])) {
/* 335 */         char[][] fragmentTokens = (char[][])element;
/* 336 */         int fragmentTokenLength = fragmentTokens.length;
/* 337 */         System.arraycopy(fragmentTokens, 0, tokens, index, fragmentTokenLength);
/* 338 */         index += fragmentTokenLength;
/* 339 */       } else if ((element instanceof char[])) {
/* 340 */         tokens[(index++)] = ((char[])element);
/*     */       } else {
/* 342 */         arguments[(index - 1)] = ((TypeReference[])element);
/*     */       }
/*     */     }
/* 345 */     long[] positions = new long[identCount];
/* 346 */     long pos = (start << 32) + end;
/* 347 */     for (int i = 0; i < identCount; i++) {
/* 348 */       positions[i] = pos;
/*     */     }
/* 350 */     return new ParameterizedQualifiedTypeReference(tokens, arguments, dim, positions);
/*     */   }
/*     */ 
/*     */   private TypeReference decodeType(char[] typeName, int length, int start, int end)
/*     */   {
/* 355 */     int identCount = 1;
/* 356 */     int dim = 0;
/* 357 */     int nameFragmentStart = this.namePos; int nameFragmentEnd = -1;
/* 358 */     ArrayList fragments = null;
/* 359 */     while (this.namePos < length) {
/* 360 */       char currentChar = typeName[this.namePos];
/* 361 */       switch (currentChar) {
/*     */       case '?':
/* 363 */         this.namePos += 1;
/* 364 */         while (typeName[this.namePos] == ' ') this.namePos += 1;
/*     */         int max;
/*     */         int ahead;
/* 365 */         switch (typeName[this.namePos])
/*     */         {
/*     */         case 's':
/* 368 */           max = TypeConstants.WILDCARD_SUPER.length - 1;
/* 369 */           ahead = 1; break;
/*     */         case 'e':
/* 370 */           while (typeName[(this.namePos + ahead)] == TypeConstants.WILDCARD_SUPER[(ahead + 1)])
/*     */           {
/* 369 */             ahead++; if (ahead < max)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 374 */             this.namePos += max;
/* 375 */             Wildcard result = new Wildcard(2);
/* 376 */             result.bound = decodeType(typeName, length, start, end);
/* 377 */             result.sourceStart = start;
/* 378 */             result.sourceEnd = end;
/* 379 */             return result;
/*     */ 
/* 384 */             int max = TypeConstants.WILDCARD_EXTENDS.length - 1;
/* 385 */             int ahead = 1;
/* 386 */             while (typeName[(this.namePos + ahead)] == TypeConstants.WILDCARD_EXTENDS[(ahead + 1)])
/*     */             {
/* 385 */               ahead++; if (ahead < max)
/*     */               {
/*     */                 continue;
/*     */               }
/*     */ 
/* 390 */               this.namePos += max;
/* 391 */               Wildcard result = new Wildcard(1);
/* 392 */               result.bound = decodeType(typeName, length, start, end);
/* 393 */               result.sourceStart = start;
/* 394 */               result.sourceEnd = end;
/* 395 */               return result;
/*     */             }
/*     */           }
/*     */         }
/* 399 */         Wildcard result = new Wildcard(0);
/* 400 */         result.sourceStart = start;
/* 401 */         result.sourceEnd = end;
/* 402 */         return result;
/*     */       case '[':
/* 404 */         if (dim == 0) nameFragmentEnd = this.namePos - 1;
/* 405 */         dim++;
/* 406 */         break;
/*     */       case ']':
/* 408 */         break;
/*     */       case ',':
/*     */       case '>':
/* 411 */         break;
/*     */       case '.':
/* 413 */         if (nameFragmentStart < 0) nameFragmentStart = this.namePos + 1;
/* 414 */         identCount++;
/* 415 */         break;
/*     */       case '<':
/* 418 */         if (!this.has1_5Compliance) break label541;
/* 420 */         if (fragments == null) fragments = new ArrayList(2);
/* 421 */         nameFragmentEnd = this.namePos - 1;
/* 422 */         char[][] identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, this.namePos);
/* 423 */         fragments.add(identifiers);
/* 424 */         this.namePos += 1;
/* 425 */         TypeReference[] arguments = decodeTypeArguments(typeName, length, start, end);
/* 426 */         fragments.add(arguments);
/* 427 */         identCount = 0;
/* 428 */         nameFragmentStart = -1;
/* 429 */         nameFragmentEnd = -1;
/*     */       }
/*     */ 
/* 433 */       this.namePos += 1;
/*     */     }
/* 435 */     label541: if (nameFragmentEnd < 0) nameFragmentEnd = this.namePos - 1;
/* 436 */     if (fragments == null)
/*     */     {
/* 438 */       if (identCount == 1) {
/* 439 */         if (dim == 0)
/*     */         {
/*     */           char[] nameFragment;
/* 441 */           if ((nameFragmentStart != 0) || (nameFragmentEnd >= 0)) {
/* 442 */             int nameFragmentLength = nameFragmentEnd - nameFragmentStart + 1;
/*     */             char[] nameFragment;
/* 443 */             System.arraycopy(typeName, nameFragmentStart, nameFragment = new char[nameFragmentLength], 0, nameFragmentLength);
/*     */           } else {
/* 445 */             nameFragment = typeName;
/*     */           }
/* 447 */           return new SingleTypeReference(nameFragment, (start << 32) + end);
/*     */         }
/* 449 */         int nameFragmentLength = nameFragmentEnd - nameFragmentStart + 1;
/* 450 */         char[] nameFragment = new char[nameFragmentLength];
/* 451 */         System.arraycopy(typeName, nameFragmentStart, nameFragment, 0, nameFragmentLength);
/* 452 */         return new ArrayTypeReference(nameFragment, dim, (start << 32) + end);
/*     */       }
/*     */ 
/* 455 */       long[] positions = new long[identCount];
/* 456 */       long pos = (start << 32) + end;
/* 457 */       for (int i = 0; i < identCount; i++) {
/* 458 */         positions[i] = pos;
/*     */       }
/* 460 */       char[][] identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, nameFragmentEnd + 1);
/* 461 */       if (dim == 0) {
/* 462 */         return new QualifiedTypeReference(identifiers, positions);
/*     */       }
/* 464 */       return new ArrayQualifiedTypeReference(identifiers, dim, positions);
/*     */     }
/*     */ 
/* 470 */     if ((nameFragmentStart > 0) && (nameFragmentStart < length)) {
/* 471 */       char[][] identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, nameFragmentEnd + 1);
/* 472 */       fragments.add(identifiers);
/*     */     }
/* 474 */     int fragmentLength = fragments.size();
/* 475 */     if (fragmentLength == 2) {
/* 476 */       char[][] firstFragment = (char[][])fragments.get(0);
/* 477 */       if (firstFragment.length == 1)
/*     */       {
/* 479 */         return new ParameterizedSingleTypeReference(firstFragment[0], (TypeReference[])fragments.get(1), dim, (start << 32) + end);
/*     */       }
/*     */     }
/*     */ 
/* 483 */     identCount = 0;
/* 484 */     for (int i = 0; i < fragmentLength; i++) {
/* 485 */       Object element = fragments.get(i);
/* 486 */       if ((element instanceof char[][])) {
/* 487 */         identCount += ((char[][])element).length;
/*     */       }
/*     */     }
/* 490 */     char[][] tokens = new char[identCount][];
/* 491 */     TypeReference[][] arguments = new TypeReference[identCount][];
/* 492 */     int index = 0;
/* 493 */     for (int i = 0; i < fragmentLength; i++) {
/* 494 */       Object element = fragments.get(i);
/* 495 */       if ((element instanceof char[][])) {
/* 496 */         char[][] fragmentTokens = (char[][])element;
/* 497 */         int fragmentTokenLength = fragmentTokens.length;
/* 498 */         System.arraycopy(fragmentTokens, 0, tokens, index, fragmentTokenLength);
/* 499 */         index += fragmentTokenLength;
/*     */       } else {
/* 501 */         arguments[(index - 1)] = ((TypeReference[])element);
/*     */       }
/*     */     }
/* 504 */     long[] positions = new long[identCount];
/* 505 */     long pos = (start << 32) + end;
/* 506 */     for (int i = 0; i < identCount; i++) {
/* 507 */       positions[i] = pos;
/*     */     }
/* 509 */     return new ParameterizedQualifiedTypeReference(tokens, arguments, dim, positions);
/*     */   }
/*     */ 
/*     */   private TypeReference[] decodeTypeArguments(char[] typeName, int length, int start, int end)
/*     */   {
/* 514 */     ArrayList argumentList = new ArrayList(1);
/* 515 */     int count = 0;
/* 516 */     while (this.namePos < length) {
/* 517 */       TypeReference argument = decodeType(typeName, length, start, end);
/* 518 */       count++;
/* 519 */       argumentList.add(argument);
/* 520 */       if ((this.namePos >= length) || 
/* 521 */         (typeName[this.namePos] == '>')) {
/*     */         break;
/*     */       }
/* 524 */       this.namePos += 1;
/*     */     }
/* 526 */     TypeReference[] typeArguments = new TypeReference[count];
/* 527 */     argumentList.toArray(typeArguments);
/* 528 */     return typeArguments;
/*     */   }
/*     */ 
/*     */   private TypeReference[] decodeTypeArguments(String typeSignature, int length, int start, int end) {
/* 532 */     ArrayList argumentList = new ArrayList(1);
/* 533 */     int count = 0;
/* 534 */     while (this.namePos < length) {
/* 535 */       TypeReference argument = decodeType(typeSignature, length, start, end);
/* 536 */       count++;
/* 537 */       argumentList.add(argument);
/* 538 */       if ((this.namePos >= length) || 
/* 539 */         (typeSignature.charAt(this.namePos) == '>')) {
/*     */         break;
/*     */       }
/*     */     }
/* 543 */     TypeReference[] typeArguments = new TypeReference[count];
/* 544 */     argumentList.toArray(typeArguments);
/* 545 */     return typeArguments;
/*     */   }
/*     */ 
/*     */   private char[][] extractIdentifiers(String typeSignature, int start, int endInclusive, int identCount) {
/* 549 */     char[][] result = new char[identCount][];
/* 550 */     int charIndex = start;
/* 551 */     int i = 0;
/* 552 */     while (charIndex < endInclusive)
/*     */     {
/*     */       char currentChar;
/* 554 */       if (((currentChar = typeSignature.charAt(charIndex)) == this.memberTypeSeparator) || (currentChar == '.')) {
/* 555 */         typeSignature.getChars(start, charIndex, result[(i++)] =  = new char[charIndex - start], 0);
/* 556 */         charIndex++; start = charIndex;
/*     */       } else {
/* 558 */         charIndex++;
/*     */       }
/*     */     }
/* 560 */     typeSignature.getChars(start, charIndex + 1, result[(i++)] =  = new char[charIndex - start + 1], 0);
/* 561 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.TypeConverter
 * JD-Core Version:    0.6.0
 */