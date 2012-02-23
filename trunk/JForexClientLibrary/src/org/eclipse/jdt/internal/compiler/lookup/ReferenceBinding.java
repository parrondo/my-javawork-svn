/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
/*      */ 
/*      */ public abstract class ReferenceBinding extends TypeBinding
/*      */ {
/*      */   public char[][] compoundName;
/*      */   public char[] sourceName;
/*      */   public int modifiers;
/*      */   public PackageBinding fPackage;
/*      */   char[] fileName;
/*      */   char[] constantPoolName;
/*      */   char[] signature;
/*      */   private SimpleLookupTable compatibleCache;
/*   44 */   public static final ReferenceBinding LUB_GENERIC = new ReferenceBinding() { } ;
/*      */ 
/*   46 */   private static final Comparator FIELD_COMPARATOR = new Comparator() {
/*      */     public int compare(Object o1, Object o2) {
/*   48 */       char[] n1 = ((FieldBinding)o1).name;
/*   49 */       char[] n2 = ((FieldBinding)o2).name;
/*   50 */       return ReferenceBinding.compare(n1, n2, n1.length, n2.length);
/*      */     }
/*   46 */   };
/*      */ 
/*   53 */   private static final Comparator METHOD_COMPARATOR = new Comparator() {
/*      */     public int compare(Object o1, Object o2) {
/*   55 */       MethodBinding m1 = (MethodBinding)o1;
/*   56 */       MethodBinding m2 = (MethodBinding)o2;
/*   57 */       char[] s1 = m1.selector;
/*   58 */       char[] s2 = m2.selector;
/*   59 */       int c = ReferenceBinding.compare(s1, s2, s1.length, s2.length);
/*   60 */       return c == 0 ? m1.parameters.length - m2.parameters.length : c;
/*      */     }
/*   53 */   };
/*      */ 
/*      */   public static FieldBinding binarySearch(char[] name, FieldBinding[] sortedFields)
/*      */   {
/*   65 */     if (sortedFields == null)
/*   66 */       return null;
/*   67 */     int max = sortedFields.length;
/*   68 */     if (max == 0)
/*   69 */       return null;
/*   70 */     int left = 0; int right = max - 1; int nameLength = name.length;
/*   71 */     int mid = 0;
/*      */ 
/*   73 */     while (left <= right) {
/*   74 */       mid = left + (right - left) / 2;
/*      */       char[] midName;
/*   75 */       int compare = compare(name, midName = sortedFields[mid].name, nameLength, midName.length);
/*   76 */       if (compare < 0)
/*   77 */         right = mid - 1;
/*   78 */       else if (compare > 0)
/*   79 */         left = mid + 1;
/*      */       else {
/*   81 */         return sortedFields[mid];
/*      */       }
/*      */     }
/*   84 */     return null;
/*      */   }
/*      */ 
/*      */   public static long binarySearch(char[] selector, MethodBinding[] sortedMethods)
/*      */   {
/*   97 */     if (sortedMethods == null)
/*   98 */       return -1L;
/*   99 */     int max = sortedMethods.length;
/*  100 */     if (max == 0)
/*  101 */       return -1L;
/*  102 */     int left = 0; int right = max - 1; int selectorLength = selector.length;
/*  103 */     int mid = 0;
/*      */ 
/*  105 */     while (left <= right) {
/*  106 */       mid = left + (right - left) / 2;
/*      */       char[] midSelector;
/*  107 */       int compare = compare(selector, midSelector = sortedMethods[mid].selector, selectorLength, midSelector.length);
/*  108 */       if (compare < 0) {
/*  109 */         right = mid - 1;
/*  110 */       } else if (compare > 0) {
/*  111 */         left = mid + 1;
/*      */       } else {
/*  113 */         int start = mid; int end = mid;
/*      */         do {
/*  115 */           start--; if (start <= left) break; 
/*  115 */         }while (CharOperation.equals(sortedMethods[(start - 1)].selector, selector));
/*      */ 
/*  117 */         while ((end < right) && (CharOperation.equals(sortedMethods[(end + 1)].selector, selector))) end++;
/*  118 */         return start + (end << 32);
/*      */       }
/*      */     }
/*  121 */     return -1L;
/*      */   }
/*      */ 
/*      */   static int compare(char[] str1, char[] str2, int len1, int len2)
/*      */   {
/*  136 */     int n = Math.min(len1, len2);
/*  137 */     int i = 0;
/*  138 */     while (n-- != 0) {
/*  139 */       char c1 = str1[i];
/*  140 */       char c2 = str2[(i++)];
/*  141 */       if (c1 != c2) {
/*  142 */         return c1 - c2;
/*      */       }
/*      */     }
/*  145 */     return len1 - len2;
/*      */   }
/*      */ 
/*      */   public static void sortFields(FieldBinding[] sortedFields, int left, int right)
/*      */   {
/*  152 */     Arrays.sort(sortedFields, left, right, FIELD_COMPARATOR);
/*      */   }
/*      */ 
/*      */   public static void sortMethods(MethodBinding[] sortedMethods, int left, int right)
/*      */   {
/*  159 */     Arrays.sort(sortedMethods, left, right, METHOD_COMPARATOR);
/*      */   }
/*      */ 
/*      */   public FieldBinding[] availableFields()
/*      */   {
/*  166 */     return fields();
/*      */   }
/*      */ 
/*      */   public MethodBinding[] availableMethods()
/*      */   {
/*  173 */     return methods();
/*      */   }
/*      */ 
/*      */   public boolean canBeInstantiated()
/*      */   {
/*  180 */     return (this.modifiers & 0x6600) == 0;
/*      */   }
/*      */ 
/*      */   public final boolean canBeSeenBy(PackageBinding invocationPackage)
/*      */   {
/*  187 */     if (isPublic()) return true;
/*  188 */     if (isPrivate()) return false;
/*      */ 
/*  191 */     return invocationPackage == this.fPackage;
/*      */   }
/*      */ 
/*      */   public final boolean canBeSeenBy(ReferenceBinding receiverType, ReferenceBinding invocationType)
/*      */   {
/*  198 */     if (isPublic()) return true;
/*      */ 
/*  200 */     if ((invocationType == this) && (invocationType == receiverType)) return true;
/*      */ 
/*  202 */     if (isProtected())
/*      */     {
/*  208 */       if (invocationType == this) return true;
/*  209 */       if (invocationType.fPackage == this.fPackage) return true;
/*      */ 
/*  211 */       TypeBinding currentType = invocationType.erasure();
/*  212 */       TypeBinding declaringClass = enclosingType().erasure();
/*  213 */       if (declaringClass == invocationType) return true;
/*  214 */       if (declaringClass == null) return false;
/*      */       do
/*      */       {
/*  217 */         if (currentType.findSuperTypeOriginatingFrom(declaringClass) != null) return true;
/*      */ 
/*  219 */         currentType = currentType.enclosingType();
/*  220 */       }while (currentType != null);
/*  221 */       return false;
/*      */     }
/*      */ 
/*  224 */     if (isPrivate())
/*      */     {
/*  228 */       if ((receiverType != this) && (receiverType != enclosingType()))
/*      */       {
/*  230 */         if (receiverType.isTypeVariable()) { TypeVariableBinding typeVariable = (TypeVariableBinding)receiverType;
/*  232 */           if ((typeVariable.isErasureBoundTo(erasure())) || (typeVariable.isErasureBoundTo(enclosingType().erasure())));
/*      */         } else {
/*  235 */           return false;
/*      */         }
/*      */       }
/*      */ 
/*  239 */       if (invocationType != this) {
/*  240 */         ReferenceBinding outerInvocationType = invocationType;
/*  241 */         ReferenceBinding temp = outerInvocationType.enclosingType();
/*  242 */         while (temp != null) {
/*  243 */           outerInvocationType = temp;
/*  244 */           temp = temp.enclosingType();
/*      */         }
/*      */ 
/*  247 */         ReferenceBinding outerDeclaringClass = (ReferenceBinding)erasure();
/*  248 */         temp = outerDeclaringClass.enclosingType();
/*  249 */         while (temp != null) {
/*  250 */           outerDeclaringClass = temp;
/*  251 */           temp = temp.enclosingType();
/*      */         }
/*  253 */         if (outerInvocationType != outerDeclaringClass) return false;
/*      */       }
/*  255 */       return true;
/*      */     }
/*      */ 
/*  259 */     if (invocationType.fPackage != this.fPackage) return false;
/*      */ 
/*  261 */     ReferenceBinding currentType = receiverType;
/*  262 */     TypeBinding originalDeclaringClass = (enclosingType() == null ? this : enclosingType()).original();
/*      */     do {
/*  264 */       if (originalDeclaringClass == currentType.original()) return true;
/*  265 */       PackageBinding currentPackage = currentType.fPackage;
/*      */ 
/*  267 */       if ((currentPackage != null) && (currentPackage != this.fPackage)) return false; 
/*      */     }
/*  268 */     while ((currentType = currentType.superclass()) != null);
/*  269 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean canBeSeenBy(Scope scope)
/*      */   {
/*  276 */     if (isPublic()) return true;
/*      */ 
/*  278 */     SourceTypeBinding invocationType = scope.enclosingSourceType();
/*  279 */     if (invocationType == this) return true;
/*      */ 
/*  281 */     if (invocationType == null) {
/*  282 */       return (!isPrivate()) && (scope.getCurrentPackage() == this.fPackage);
/*      */     }
/*  284 */     if (isProtected())
/*      */     {
/*  290 */       if (invocationType.fPackage == this.fPackage) return true;
/*      */ 
/*  292 */       TypeBinding declaringClass = enclosingType();
/*  293 */       if (declaringClass == null) return false;
/*  294 */       declaringClass = declaringClass.erasure();
/*  295 */       TypeBinding currentType = invocationType.erasure();
/*      */       do
/*      */       {
/*  298 */         if (declaringClass == invocationType) return true;
/*  299 */         if (currentType.findSuperTypeOriginatingFrom(declaringClass) != null) return true;
/*      */ 
/*  301 */         currentType = currentType.enclosingType();
/*  302 */       }while (currentType != null);
/*  303 */       return false;
/*      */     }
/*  305 */     if (isPrivate())
/*      */     {
/*  308 */       ReferenceBinding outerInvocationType = invocationType;
/*  309 */       ReferenceBinding temp = outerInvocationType.enclosingType();
/*  310 */       while (temp != null) {
/*  311 */         outerInvocationType = temp;
/*  312 */         temp = temp.enclosingType();
/*      */       }
/*      */ 
/*  315 */       ReferenceBinding outerDeclaringClass = (ReferenceBinding)erasure();
/*  316 */       temp = outerDeclaringClass.enclosingType();
/*  317 */       while (temp != null) {
/*  318 */         outerDeclaringClass = temp;
/*  319 */         temp = temp.enclosingType();
/*      */       }
/*  321 */       return outerInvocationType == outerDeclaringClass;
/*      */     }
/*      */ 
/*  325 */     return invocationType.fPackage == this.fPackage;
/*      */   }
/*      */ 
/*      */   public char[] computeGenericTypeSignature(TypeVariableBinding[] typeVariables)
/*      */   {
/*  330 */     boolean isMemberOfGeneric = (isMemberType()) && ((enclosingType().modifiers & 0x40000000) != 0);
/*  331 */     if ((typeVariables == Binding.NO_TYPE_VARIABLES) && (!isMemberOfGeneric)) {
/*  332 */       return signature();
/*      */     }
/*  334 */     StringBuffer sig = new StringBuffer(10);
/*  335 */     if (isMemberOfGeneric) {
/*  336 */       char[] typeSig = enclosingType().genericTypeSignature();
/*  337 */       sig.append(typeSig, 0, typeSig.length - 1);
/*  338 */       sig.append('.');
/*  339 */       sig.append(this.sourceName);
/*      */     } else {
/*  341 */       char[] typeSig = signature();
/*  342 */       sig.append(typeSig, 0, typeSig.length - 1);
/*      */     }
/*  344 */     if (typeVariables == Binding.NO_TYPE_VARIABLES) {
/*  345 */       sig.append(';');
/*      */     } else {
/*  347 */       sig.append('<');
/*  348 */       int i = 0; for (int length = typeVariables.length; i < length; i++) {
/*  349 */         sig.append(typeVariables[i].genericTypeSignature());
/*      */       }
/*  351 */       sig.append(">;");
/*      */     }
/*  353 */     int sigLength = sig.length();
/*  354 */     char[] result = new char[sigLength];
/*  355 */     sig.getChars(0, sigLength, result, 0);
/*  356 */     return result;
/*      */   }
/*      */ 
/*      */   public void computeId()
/*      */   {
/*  361 */     switch (this.compoundName.length)
/*      */     {
/*      */     case 3:
/*  364 */       if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0])) {
/*  365 */         return;
/*      */       }
/*  367 */       char[] packageName = this.compoundName[1];
/*  368 */       if (packageName.length == 0) return;
/*  369 */       char[] typeName = this.compoundName[2];
/*  370 */       if (typeName.length == 0) return;
/*      */ 
/*  372 */       if (!CharOperation.equals(TypeConstants.LANG, this.compoundName[1])) {
/*  373 */         switch (packageName[0]) {
/*      */         case 'i':
/*  375 */           if (CharOperation.equals(packageName, TypeConstants.IO)) {
/*  376 */             switch (typeName[0]) {
/*      */             case 'E':
/*  378 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_EXTERNALIZABLE[2]))
/*  379 */                 this.id = 56;
/*  380 */               return;
/*      */             case 'I':
/*  382 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_IOEXCEPTION[2]))
/*  383 */                 this.id = 58;
/*  384 */               return;
/*      */             case 'O':
/*  386 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_OBJECTSTREAMEXCEPTION[2]))
/*  387 */                 this.id = 57;
/*  388 */               return;
/*      */             case 'P':
/*  390 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_PRINTSTREAM[2]))
/*  391 */                 this.id = 53;
/*  392 */               return;
/*      */             case 'S':
/*  394 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_IO_SERIALIZABLE[2]))
/*  395 */                 this.id = 37;
/*  396 */               return;
/*      */             }
/*      */           }
/*  399 */           return;
/*      */         case 'u':
/*  401 */           if (CharOperation.equals(packageName, TypeConstants.UTIL)) {
/*  402 */             switch (typeName[0]) {
/*      */             case 'C':
/*  404 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_COLLECTION[2]))
/*  405 */                 this.id = 59;
/*  406 */               return;
/*      */             case 'I':
/*  408 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_UTIL_ITERATOR[2]))
/*  409 */                 this.id = 39;
/*  410 */               return;
/*      */             }
/*      */           }
/*  413 */           return;
/*      */         }
/*  415 */         return;
/*      */       }
/*      */ 
/*  419 */       switch (typeName[0]) {
/*      */       case 'A':
/*  421 */         if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ASSERTIONERROR[2]))
/*  422 */           this.id = 35;
/*  423 */         return;
/*      */       case 'B':
/*  425 */         switch (typeName.length) {
/*      */         case 4:
/*  427 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BYTE[2]))
/*  428 */             this.id = 26;
/*  429 */           return;
/*      */         case 7:
/*  431 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_BOOLEAN[2]))
/*  432 */             this.id = 33;
/*  433 */           return;
/*      */         case 5:
/*  435 */         case 6: } return;
/*      */       case 'C':
/*  437 */         switch (typeName.length) {
/*      */         case 5:
/*  439 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASS[2]))
/*  440 */             this.id = 16;
/*  441 */           return;
/*      */         case 9:
/*  443 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CHARACTER[2]))
/*  444 */             this.id = 28;
/*  445 */           else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLONEABLE[2]))
/*  446 */             this.id = 36;
/*  447 */           return;
/*      */         case 22:
/*  449 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_CLASSNOTFOUNDEXCEPTION[2]))
/*  450 */             this.id = 23;
/*  451 */           return;
/*      */         }
/*  453 */         return;
/*      */       case 'D':
/*  455 */         switch (typeName.length) {
/*      */         case 6:
/*  457 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DOUBLE[2]))
/*  458 */             this.id = 32;
/*  459 */           return;
/*      */         case 10:
/*  461 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_DEPRECATED[2]))
/*  462 */             this.id = 44;
/*  463 */           return;
/*      */         case 7:
/*      */         case 8:
/*  465 */         case 9: } return;
/*      */       case 'E':
/*  467 */         switch (typeName.length) {
/*      */         case 4:
/*  469 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ENUM[2]))
/*  470 */             this.id = 41;
/*  471 */           return;
/*      */         case 5:
/*  473 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ERROR[2]))
/*  474 */             this.id = 19;
/*  475 */           return;
/*      */         case 9:
/*  477 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_EXCEPTION[2]))
/*  478 */             this.id = 25;
/*  479 */           return;
/*      */         case 6:
/*      */         case 7:
/*  481 */         case 8: } return;
/*      */       case 'F':
/*  483 */         if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_FLOAT[2]))
/*  484 */           this.id = 31;
/*  485 */         return;
/*      */       case 'I':
/*  487 */         switch (typeName.length) {
/*      */         case 7:
/*  489 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_INTEGER[2]))
/*  490 */             this.id = 29;
/*  491 */           return;
/*      */         case 8:
/*  493 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ITERABLE[2]))
/*  494 */             this.id = 38;
/*  495 */           return;
/*      */         case 24:
/*  497 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ILLEGALARGUMENTEXCEPTION[2]))
/*  498 */             this.id = 42;
/*  499 */           return;
/*      */         }
/*  501 */         return;
/*      */       case 'L':
/*  503 */         if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_LONG[2]))
/*  504 */           this.id = 30;
/*  505 */         return;
/*      */       case 'N':
/*  507 */         if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_NOCLASSDEFERROR[2]))
/*  508 */           this.id = 22;
/*  509 */         return;
/*      */       case 'O':
/*  511 */         switch (typeName.length) {
/*      */         case 6:
/*  513 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OBJECT[2]))
/*  514 */             this.id = 1;
/*  515 */           return;
/*      */         case 8:
/*  517 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_OVERRIDE[2]))
/*  518 */             this.id = 47;
/*  519 */           return;
/*      */         case 7:
/*  521 */         }return;
/*      */       case 'R':
/*  523 */         if (!CharOperation.equals(typeName, TypeConstants.JAVA_LANG_RUNTIMEEXCEPTION[2])) break;
/*  524 */         this.id = 24;
/*  525 */         break;
/*      */       case 'S':
/*  527 */         switch (typeName.length) {
/*      */         case 5:
/*  529 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SHORT[2]))
/*  530 */             this.id = 27;
/*  531 */           return;
/*      */         case 6:
/*  533 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRING[2]))
/*  534 */             this.id = 11;
/*  535 */           else if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SYSTEM[2]))
/*  536 */             this.id = 18;
/*  537 */           return;
/*      */         case 12:
/*  539 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUFFER[2]))
/*  540 */             this.id = 17;
/*  541 */           return;
/*      */         case 13:
/*  543 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_STRINGBUILDER[2]))
/*  544 */             this.id = 40;
/*  545 */           return;
/*      */         case 16:
/*  547 */           if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_SUPPRESSWARNINGS[2]))
/*  548 */             this.id = 49;
/*  549 */           return;
/*      */         case 7:
/*      */         case 8:
/*      */         case 9:
/*      */         case 10:
/*      */         case 11:
/*      */         case 14:
/*  551 */         case 15: } return;
/*      */       case 'T':
/*  553 */         if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_THROWABLE[2]))
/*  554 */           this.id = 21;
/*  555 */         return;
/*      */       case 'V':
/*  557 */         if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_VOID[2]))
/*  558 */           this.id = 34;
/*  559 */         return;
/*      */       case 'G':
/*      */       case 'H':
/*      */       case 'J':
/*      */       case 'K':
/*      */       case 'M':
/*      */       case 'P':
/*      */       case 'Q':
/*  561 */       case 'U': } break;
/*      */     case 4:
/*  564 */       if (!CharOperation.equals(TypeConstants.JAVA, this.compoundName[0]))
/*  565 */         return;
/*  566 */       if (!CharOperation.equals(TypeConstants.LANG, this.compoundName[1]))
/*  567 */         return;
/*  568 */       char[] packageName = this.compoundName[2];
/*  569 */       if (packageName.length == 0) return;
/*  570 */       char[] typeName = this.compoundName[3];
/*  571 */       if (typeName.length == 0) return;
/*  572 */       switch (packageName[0]) {
/*      */       case 'a':
/*  574 */         if (CharOperation.equals(packageName, TypeConstants.ANNOTATION)) {
/*  575 */           switch (typeName[0]) {
/*      */           case 'A':
/*  577 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION[3]))
/*  578 */               this.id = 43;
/*  579 */             return;
/*      */           case 'D':
/*  581 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_DOCUMENTED[3]))
/*  582 */               this.id = 45;
/*  583 */             return;
/*      */           case 'E':
/*  585 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_ELEMENTTYPE[3]))
/*  586 */               this.id = 52;
/*  587 */             return;
/*      */           case 'I':
/*  589 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_INHERITED[3]))
/*  590 */               this.id = 46;
/*  591 */             return;
/*      */           case 'R':
/*  593 */             switch (typeName.length) {
/*      */             case 9:
/*  595 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTION[3]))
/*  596 */                 this.id = 48;
/*  597 */               return;
/*      */             case 15:
/*  599 */               if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_RETENTIONPOLICY[3]))
/*  600 */                 this.id = 51;
/*  601 */               return;
/*      */             }
/*  603 */             return;
/*      */           case 'T':
/*  605 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_ANNOTATION_TARGET[3]))
/*  606 */               this.id = 50;
/*  607 */             return;
/*      */           }
/*      */         }
/*  610 */         return;
/*      */       case 'r':
/*  612 */         if (CharOperation.equals(packageName, TypeConstants.REFLECT)) {
/*  613 */           switch (typeName[0]) {
/*      */           case 'C':
/*  615 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_CONSTRUCTOR[2]))
/*  616 */               this.id = 20;
/*  617 */             return;
/*      */           case 'F':
/*  619 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_FIELD[2]))
/*  620 */               this.id = 54;
/*  621 */             return;
/*      */           case 'M':
/*  623 */             if (CharOperation.equals(typeName, TypeConstants.JAVA_LANG_REFLECT_METHOD[2]))
/*  624 */               this.id = 55;
/*  625 */             return;
/*      */           }
/*      */         }
/*  628 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public char[] computeUniqueKey(boolean isLeaf)
/*      */   {
/*  638 */     if (!isLeaf) return signature();
/*  639 */     return genericTypeSignature();
/*      */   }
/*      */ 
/*      */   public char[] constantPoolName()
/*      */   {
/*  648 */     if (this.constantPoolName != null) return this.constantPoolName;
/*  649 */     return this.constantPoolName = CharOperation.concatWith(this.compoundName, '/');
/*      */   }
/*      */ 
/*      */   public String debugName() {
/*  653 */     return this.compoundName != null ? new String(readableName()) : "UNNAMED TYPE";
/*      */   }
/*      */ 
/*      */   public final int depth() {
/*  657 */     int depth = 0;
/*  658 */     ReferenceBinding current = this;
/*  659 */     while ((current = current.enclosingType()) != null)
/*  660 */       depth++;
/*  661 */     return depth;
/*      */   }
/*      */ 
/*      */   public boolean detectAnnotationCycle() {
/*  665 */     if ((this.tagBits & 0x0) != 0L) return false;
/*  666 */     if ((this.tagBits & 0x80000000) != 0L) return true;
/*      */ 
/*  668 */     this.tagBits |= 2147483648L;
/*  669 */     MethodBinding[] currentMethods = methods();
/*  670 */     boolean inCycle = false;
/*  671 */     int i = 0; for (int l = currentMethods.length; i < l; i++) {
/*  672 */       TypeBinding returnType = currentMethods[i].returnType.leafComponentType();
/*  673 */       if (this == returnType) {
/*  674 */         if ((this instanceof SourceTypeBinding)) {
/*  675 */           MethodDeclaration decl = (MethodDeclaration)currentMethods[i].sourceMethod();
/*  676 */           ((SourceTypeBinding)this).scope.problemReporter().annotationCircularity(this, this, decl != null ? decl.returnType : null);
/*      */         }
/*  678 */       } else if ((returnType.isAnnotationType()) && (((ReferenceBinding)returnType).detectAnnotationCycle())) {
/*  679 */         if ((this instanceof SourceTypeBinding)) {
/*  680 */           MethodDeclaration decl = (MethodDeclaration)currentMethods[i].sourceMethod();
/*  681 */           ((SourceTypeBinding)this).scope.problemReporter().annotationCircularity(this, returnType, decl != null ? decl.returnType : null);
/*      */         }
/*  683 */         inCycle = true;
/*      */       }
/*      */     }
/*  686 */     if (inCycle)
/*  687 */       return true;
/*  688 */     this.tagBits |= 4294967296L;
/*  689 */     return false;
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding enclosingTypeAt(int relativeDepth) {
/*  693 */     ReferenceBinding current = this;
/*  694 */     while ((relativeDepth-- > 0) && (current != null))
/*  695 */       current = current.enclosingType();
/*  696 */     return current;
/*      */   }
/*      */ 
/*      */   public int enumConstantCount() {
/*  700 */     int count = 0;
/*  701 */     FieldBinding[] fields = fields();
/*  702 */     int i = 0; for (int length = fields.length; i < length; i++) {
/*  703 */       if ((fields[i].modifiers & 0x4000) == 0) continue; count++;
/*      */     }
/*  705 */     return count;
/*      */   }
/*      */ 
/*      */   public int fieldCount() {
/*  709 */     return fields().length;
/*      */   }
/*      */ 
/*      */   public FieldBinding[] fields() {
/*  713 */     return Binding.NO_FIELDS;
/*      */   }
/*      */ 
/*      */   public final int getAccessFlags() {
/*  717 */     return this.modifiers & 0xFFFF;
/*      */   }
/*      */ 
/*      */   public AnnotationBinding[] getAnnotations()
/*      */   {
/*  724 */     return retrieveAnnotations(this);
/*      */   }
/*      */ 
/*      */   public long getAnnotationTagBits()
/*      */   {
/*  731 */     return this.tagBits;
/*      */   }
/*      */ 
/*      */   public int getEnclosingInstancesSlotSize()
/*      */   {
/*  738 */     if (isStatic()) return 0;
/*  739 */     return enclosingType() == null ? 0 : 1;
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
/*  743 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope) {
/*  747 */     return null;
/*      */   }
/*      */   public FieldBinding getField(char[] fieldName, boolean needResolve) {
/*  750 */     return null;
/*      */   }
/*      */ 
/*      */   public char[] getFileName()
/*      */   {
/*  756 */     return this.fileName;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding getMemberType(char[] typeName) {
/*  760 */     ReferenceBinding[] memberTypes = memberTypes();
/*  761 */     int i = memberTypes.length;
/*      */     do { if (CharOperation.equals(memberTypes[i].sourceName, typeName))
/*  763 */         return memberTypes[i];
/*  761 */       i--; } while (i >= 0);
/*      */ 
/*  764 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] getMethods(char[] selector) {
/*  768 */     return Binding.NO_METHODS;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] getMethods(char[] selector, int suggestedParameterLength)
/*      */   {
/*  774 */     return getMethods(selector);
/*      */   }
/*      */ 
/*      */   public int getOuterLocalVariablesSlotSize()
/*      */   {
/*  781 */     return 0;
/*      */   }
/*      */ 
/*      */   public PackageBinding getPackage() {
/*  785 */     return this.fPackage;
/*      */   }
/*      */ 
/*      */   public TypeVariableBinding getTypeVariable(char[] variableName) {
/*  789 */     TypeVariableBinding[] typeVariables = typeVariables();
/*  790 */     int i = typeVariables.length;
/*      */     do { if (CharOperation.equals(typeVariables[i].sourceName, variableName))
/*  792 */         return typeVariables[i];
/*  790 */       i--; } while (i >= 0);
/*      */ 
/*  793 */     return null;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/*  799 */     return (this.compoundName == null) || (this.compoundName.length == 0) ? 
/*  800 */       super.hashCode() : 
/*  801 */       CharOperation.hashCode(this.compoundName[(this.compoundName.length - 1)]);
/*      */   }
/*      */ 
/*      */   public boolean hasIncompatibleSuperType(ReferenceBinding otherType)
/*      */   {
/*  810 */     if (this == otherType) return false; 
/*      */ ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/*  813 */     int nextPosition = 0;
/*  814 */     ReferenceBinding currentType = this;
/*      */     TypeBinding match;
/*      */     do { match = otherType.findSuperTypeOriginatingFrom(currentType);
/*  818 */       if ((match != null) && (match.isProvablyDistinct(currentType))) {
/*  819 */         return true;
/*      */       }
/*  821 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  822 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/*  823 */         if (interfacesToVisit == null) {
/*  824 */           interfacesToVisit = itsInterfaces;
/*  825 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/*  827 */           int itsLength = itsInterfaces.length;
/*  828 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/*  829 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  830 */           for (int a = 0; a < itsLength; a++) {
/*  831 */             ReferenceBinding next = itsInterfaces[a];
/*  832 */             int b = 0;
/*  833 */             while (next != interfacesToVisit[b])
/*      */             {
/*  832 */               b++; if (b < nextPosition)
/*      */                 continue;
/*  834 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         } }
/*  838 */     while ((currentType = currentType.superclass()) != null);
/*      */ 
/*  840 */     for (int i = 0; i < nextPosition; i++) {
/*  841 */       currentType = interfacesToVisit[i];
/*  842 */       if (currentType == otherType) return false;
/*  843 */       match = otherType.findSuperTypeOriginatingFrom(currentType);
/*  844 */       if ((match != null) && (match.isProvablyDistinct(currentType))) {
/*  845 */         return true;
/*      */       }
/*  847 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  848 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/*  849 */         int itsLength = itsInterfaces.length;
/*  850 */         if (nextPosition + itsLength >= interfacesToVisit.length)
/*  851 */           System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  852 */         for (int a = 0; a < itsLength; a++) {
/*  853 */           ReferenceBinding next = itsInterfaces[a];
/*  854 */           int b = 0;
/*  855 */           while (next != interfacesToVisit[b])
/*      */           {
/*  854 */             b++; if (b < nextPosition)
/*      */               continue;
/*  856 */             interfacesToVisit[(nextPosition++)] = next;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  860 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean hasMemberTypes() {
/*  864 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean hasRestrictedAccess() {
/*  868 */     return (this.modifiers & 0x40000) != 0;
/*      */   }
/*      */ 
/*      */   public boolean implementsInterface(ReferenceBinding anInterface, boolean searchHierarchy)
/*      */   {
/*  877 */     if (this == anInterface) {
/*  878 */       return true;
/*      */     }
/*  880 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/*  881 */     int nextPosition = 0;
/*  882 */     ReferenceBinding currentType = this;
/*      */     do {
/*  884 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  885 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/*  886 */         if (interfacesToVisit == null) {
/*  887 */           interfacesToVisit = itsInterfaces;
/*  888 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/*  890 */           int itsLength = itsInterfaces.length;
/*  891 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/*  892 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  893 */           for (int a = 0; a < itsLength; a++) {
/*  894 */             ReferenceBinding next = itsInterfaces[a];
/*  895 */             int b = 0;
/*  896 */             while (next != interfacesToVisit[b])
/*      */             {
/*  895 */               b++; if (b < nextPosition)
/*      */                 continue;
/*  897 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/*      */     }
/*  901 */     while ((searchHierarchy) && ((currentType = currentType.superclass()) != null));
/*      */ 
/*  903 */     for (int i = 0; i < nextPosition; i++) {
/*  904 */       currentType = interfacesToVisit[i];
/*  905 */       if (currentType.isEquivalentTo(anInterface)) {
/*  906 */         return true;
/*      */       }
/*  908 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  909 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/*  910 */         int itsLength = itsInterfaces.length;
/*  911 */         if (nextPosition + itsLength >= interfacesToVisit.length)
/*  912 */           System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  913 */         for (int a = 0; a < itsLength; a++) {
/*  914 */           ReferenceBinding next = itsInterfaces[a];
/*  915 */           int b = 0;
/*  916 */           while (next != interfacesToVisit[b])
/*      */           {
/*  915 */             b++; if (b < nextPosition)
/*      */               continue;
/*  917 */             interfacesToVisit[(nextPosition++)] = next;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  921 */     return false;
/*      */   }
/*      */ 
/*      */   boolean implementsMethod(MethodBinding method)
/*      */   {
/*  926 */     char[] selector = method.selector;
/*  927 */     ReferenceBinding type = this;
/*  928 */     while (type != null) {
/*  929 */       MethodBinding[] methods = type.methods();
/*      */       long range;
/*  931 */       if ((range = binarySearch(selector, methods)) >= 0L) {
/*  932 */         int start = (int)range; int end = (int)(range >> 32);
/*  933 */         for (int i = start; i <= end; i++) {
/*  934 */           if (methods[i].areParametersEqual(method))
/*  935 */             return true;
/*      */         }
/*      */       }
/*  938 */       type = type.superclass();
/*      */     }
/*  940 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isAbstract()
/*      */   {
/*  947 */     return (this.modifiers & 0x400) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isAnnotationType() {
/*  951 */     return (this.modifiers & 0x2000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isBinaryBinding() {
/*  955 */     return (this.tagBits & 0x40) != 0L;
/*      */   }
/*      */ 
/*      */   public boolean isClass() {
/*  959 */     return (this.modifiers & 0x6200) == 0;
/*      */   }
/*      */ 
/*      */   public boolean isCompatibleWith(TypeBinding otherType)
/*      */   {
/*  968 */     if (otherType == this)
/*  969 */       return true;
/*  970 */     if (otherType.id == 1)
/*  971 */       return true;
/*      */     Object result;
/*  973 */     if (this.compatibleCache == null) {
/*  974 */       this.compatibleCache = new SimpleLookupTable(3);
/*  975 */       result = null;
/*      */     } else {
/*  977 */       Object result = this.compatibleCache.get(otherType);
/*  978 */       if (result != null) {
/*  979 */         return result == Boolean.TRUE;
/*      */       }
/*      */     }
/*  982 */     this.compatibleCache.put(otherType, Boolean.FALSE);
/*  983 */     if (isCompatibleWith0(otherType)) {
/*  984 */       this.compatibleCache.put(otherType, Boolean.TRUE);
/*  985 */       return true;
/*      */     }
/*  987 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isCompatibleWith0(TypeBinding otherType)
/*      */   {
/*  994 */     if (otherType == this)
/*  995 */       return true;
/*  996 */     if (otherType.id == 1) {
/*  997 */       return true;
/*      */     }
/*      */ 
/* 1000 */     if (isEquivalentTo(otherType))
/* 1001 */       return true;
/* 1002 */     switch (otherType.kind()) {
/*      */     case 516:
/*      */     case 8196:
/* 1005 */       return false;
/*      */     case 4100:
/* 1009 */       if (!otherType.isCapture()) break;
/* 1010 */       CaptureBinding otherCapture = (CaptureBinding)otherType;
/*      */       TypeBinding otherLowerBound;
/* 1012 */       if ((otherLowerBound = otherCapture.lowerBound) == null) break;
/* 1013 */       if (otherLowerBound.isArrayType()) return false;
/* 1014 */       return isCompatibleWith(otherLowerBound);
/*      */     case 4:
/*      */     case 260:
/*      */     case 1028:
/*      */     case 2052:
/* 1022 */       switch (kind()) {
/*      */       case 260:
/*      */       case 1028:
/*      */       case 2052:
/* 1026 */         if (erasure() != otherType.erasure()) break;
/* 1027 */         return false;
/*      */       }
/*      */ 
/* 1030 */       ReferenceBinding otherReferenceType = (ReferenceBinding)otherType;
/* 1031 */       if (otherReferenceType.isInterface())
/* 1032 */         return implementsInterface(otherReferenceType, true);
/* 1033 */       if (isInterface())
/*      */       {
/* 1035 */         return false;
/* 1036 */       }return otherReferenceType.isSuperclassOf(this);
/*      */     }
/* 1038 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isDefault()
/*      */   {
/* 1046 */     return (this.modifiers & 0x7) == 0;
/*      */   }
/*      */ 
/*      */   public final boolean isDeprecated()
/*      */   {
/* 1053 */     return (this.modifiers & 0x100000) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isEnum() {
/* 1057 */     return (this.modifiers & 0x4000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isFinal()
/*      */   {
/* 1064 */     return (this.modifiers & 0x10) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isHierarchyBeingConnected()
/*      */   {
/* 1071 */     return ((this.tagBits & 0x200) == 0L) && ((this.tagBits & 0x100) != 0L);
/*      */   }
/*      */ 
/*      */   public boolean isHierarchyConnected()
/*      */   {
/* 1078 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isInterface()
/*      */   {
/* 1083 */     return (this.modifiers & 0x200) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isPrivate()
/*      */   {
/* 1090 */     return (this.modifiers & 0x2) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isOrEnclosedByPrivateType()
/*      */   {
/* 1097 */     if (isLocalType()) return true;
/* 1098 */     ReferenceBinding type = this;
/* 1099 */     while (type != null) {
/* 1100 */       if ((type.modifiers & 0x2) != 0)
/* 1101 */         return true;
/* 1102 */       type = type.enclosingType();
/*      */     }
/* 1104 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isProtected()
/*      */   {
/* 1111 */     return (this.modifiers & 0x4) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isPublic()
/*      */   {
/* 1118 */     return (this.modifiers & 0x1) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isStatic()
/*      */   {
/* 1125 */     return ((this.modifiers & 0x208) != 0) || ((this.tagBits & 0x4) == 0L);
/*      */   }
/*      */ 
/*      */   public final boolean isStrictfp()
/*      */   {
/* 1132 */     return (this.modifiers & 0x800) != 0;
/*      */   }
/*      */ 
/*      */   public boolean isSuperclassOf(ReferenceBinding otherType)
/*      */   {
/* 1140 */     while ((otherType = otherType.superclass()) != null) {
/* 1141 */       if (otherType.isEquivalentTo(this)) return true;
/*      */     }
/* 1143 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isThrowable()
/*      */   {
/* 1150 */     ReferenceBinding current = this;
/*      */     do
/* 1152 */       switch (current.id) {
/*      */       case 19:
/*      */       case 21:
/*      */       case 24:
/*      */       case 25:
/* 1157 */         return true;
/*      */       case 20:
/*      */       case 22:
/* 1159 */       case 23: }  while ((current = current.superclass()) != null);
/* 1160 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isUncheckedException(boolean includeSupertype)
/*      */   {
/* 1172 */     switch (this.id) {
/*      */     case 19:
/*      */     case 24:
/* 1175 */       return true;
/*      */     case 21:
/*      */     case 25:
/* 1178 */       return includeSupertype;
/*      */     case 20:
/*      */     case 22:
/* 1180 */     case 23: } ReferenceBinding current = this;
/* 1181 */     while ((current = current.superclass()) != null)
/* 1182 */       switch (current.id) {
/*      */       case 19:
/*      */       case 24:
/* 1185 */         return true;
/*      */       case 21:
/*      */       case 25:
/* 1188 */         return false;
/*      */       case 20:
/*      */       case 22:
/*      */       case 23:
/*      */       } return false;
/*      */   }
/*      */ 
/*      */   public final boolean isUsed()
/*      */   {
/* 1198 */     return (this.modifiers & 0x8000000) != 0;
/*      */   }
/*      */ 
/*      */   public final boolean isViewedAsDeprecated()
/*      */   {
/* 1206 */     return ((this.modifiers & 0x300000) != 0) || 
/* 1206 */       (getPackage().isViewedAsDeprecated());
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] memberTypes() {
/* 1210 */     return Binding.NO_MEMBER_TYPES;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] methods() {
/* 1214 */     return Binding.NO_METHODS;
/*      */   }
/* 1218 */   public final ReferenceBinding outermostEnclosingType() { ReferenceBinding current = this;
/*      */     ReferenceBinding last;
/*      */     do last = current;
/* 1221 */     while ((current = current.enclosingType()) != null);
/* 1222 */     return last;
/*      */   }
/*      */ 
/*      */   public char[] qualifiedSourceName()
/*      */   {
/* 1232 */     if (isMemberType())
/* 1233 */       return CharOperation.concat(enclosingType().qualifiedSourceName(), sourceName(), '.');
/* 1234 */     return sourceName();
/*      */   }
/*      */ 
/*      */   public char[] readableName()
/*      */   {
/*      */     char[] readableName;
/*      */     char[] readableName;
/* 1244 */     if (isMemberType())
/* 1245 */       readableName = CharOperation.concat(enclosingType().readableName(), this.sourceName, '.');
/*      */     else
/* 1247 */       readableName = CharOperation.concatWith(this.compoundName, '.');
/*      */     TypeVariableBinding[] typeVars;
/* 1250 */     if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
/* 1251 */       StringBuffer nameBuffer = new StringBuffer(10);
/* 1252 */       nameBuffer.append(readableName).append('<');
/* 1253 */       int i = 0; for (int length = typeVars.length; i < length; i++) {
/* 1254 */         if (i > 0) nameBuffer.append(',');
/* 1255 */         nameBuffer.append(typeVars[i].readableName());
/*      */       }
/* 1257 */       nameBuffer.append('>');
/* 1258 */       int nameLength = nameBuffer.length();
/* 1259 */       readableName = new char[nameLength];
/* 1260 */       nameBuffer.getChars(0, nameLength, readableName, 0);
/*      */     }
/* 1262 */     return readableName;
/*      */   }
/*      */ 
/*      */   public AnnotationHolder retrieveAnnotationHolder(Binding binding, boolean forceInitialization) {
/* 1266 */     SimpleLookupTable store = storedAnnotations(false);
/* 1267 */     return store == null ? null : (AnnotationHolder)store.get(binding);
/*      */   }
/*      */ 
/*      */   AnnotationBinding[] retrieveAnnotations(Binding binding) {
/* 1271 */     AnnotationHolder holder = retrieveAnnotationHolder(binding, true);
/* 1272 */     return holder == null ? Binding.NO_ANNOTATIONS : holder.getAnnotations();
/*      */   }
/*      */ 
/*      */   public void setAnnotations(AnnotationBinding[] annotations) {
/* 1276 */     storeAnnotations(this, annotations);
/*      */   }
/*      */ 
/*      */   public char[] shortReadableName()
/*      */   {
/*      */     char[] shortReadableName;
/*      */     char[] shortReadableName;
/* 1281 */     if (isMemberType())
/* 1282 */       shortReadableName = CharOperation.concat(enclosingType().shortReadableName(), this.sourceName, '.');
/*      */     else
/* 1284 */       shortReadableName = this.sourceName;
/*      */     TypeVariableBinding[] typeVars;
/* 1287 */     if ((typeVars = typeVariables()) != Binding.NO_TYPE_VARIABLES) {
/* 1288 */       StringBuffer nameBuffer = new StringBuffer(10);
/* 1289 */       nameBuffer.append(shortReadableName).append('<');
/* 1290 */       int i = 0; for (int length = typeVars.length; i < length; i++) {
/* 1291 */         if (i > 0) nameBuffer.append(',');
/* 1292 */         nameBuffer.append(typeVars[i].shortReadableName());
/*      */       }
/* 1294 */       nameBuffer.append('>');
/* 1295 */       int nameLength = nameBuffer.length();
/* 1296 */       shortReadableName = new char[nameLength];
/* 1297 */       nameBuffer.getChars(0, nameLength, shortReadableName, 0);
/*      */     }
/* 1299 */     return shortReadableName;
/*      */   }
/*      */ 
/*      */   public char[] signature() {
/* 1303 */     if (this.signature != null) {
/* 1304 */       return this.signature;
/*      */     }
/* 1306 */     return this.signature = CharOperation.concat('L', constantPoolName(), ';');
/*      */   }
/*      */ 
/*      */   public char[] sourceName() {
/* 1310 */     return this.sourceName;
/*      */   }
/*      */ 
/*      */   void storeAnnotationHolder(Binding binding, AnnotationHolder holder) {
/* 1314 */     if (holder == null) {
/* 1315 */       SimpleLookupTable store = storedAnnotations(false);
/* 1316 */       if (store != null)
/* 1317 */         store.removeKey(binding);
/*      */     } else {
/* 1319 */       SimpleLookupTable store = storedAnnotations(true);
/* 1320 */       if (store != null)
/* 1321 */         store.put(binding, holder);
/*      */     }
/*      */   }
/*      */ 
/*      */   void storeAnnotations(Binding binding, AnnotationBinding[] annotations) {
/* 1326 */     AnnotationHolder holder = null;
/* 1327 */     if ((annotations == null) || (annotations.length == 0)) {
/* 1328 */       SimpleLookupTable store = storedAnnotations(false);
/* 1329 */       if (store != null)
/* 1330 */         holder = (AnnotationHolder)store.get(binding);
/* 1331 */       if (holder == null) return; 
/*      */     }
/*      */     else {
/* 1333 */       SimpleLookupTable store = storedAnnotations(true);
/* 1334 */       if (store == null) return;
/* 1335 */       holder = (AnnotationHolder)store.get(binding);
/* 1336 */       if (holder == null)
/* 1337 */         holder = new AnnotationHolder();
/*      */     }
/* 1339 */     storeAnnotationHolder(binding, holder.setAnnotations(annotations));
/*      */   }
/*      */ 
/*      */   SimpleLookupTable storedAnnotations(boolean forceInitialize) {
/* 1343 */     return null;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding superclass() {
/* 1347 */     return null;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] superInterfaces() {
/* 1351 */     return Binding.NO_SUPERINTERFACES;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] syntheticEnclosingInstanceTypes() {
/* 1355 */     if (isStatic()) return null;
/* 1356 */     ReferenceBinding enclosingType = enclosingType();
/* 1357 */     if (enclosingType == null)
/* 1358 */       return null;
/* 1359 */     return new ReferenceBinding[] { enclosingType };
/*      */   }
/*      */   public SyntheticArgumentBinding[] syntheticOuterLocalVariables() {
/* 1362 */     return null;
/*      */   }
/*      */ 
/*      */   MethodBinding[] unResolvedMethods() {
/* 1366 */     return methods();
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding
 * JD-Core Version:    0.6.0
 */