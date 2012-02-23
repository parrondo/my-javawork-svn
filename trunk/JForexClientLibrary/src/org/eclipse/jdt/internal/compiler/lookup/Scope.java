/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
/*      */ import org.eclipse.jdt.internal.compiler.util.ObjectVector;
/*      */ import org.eclipse.jdt.internal.compiler.util.SimpleSet;
/*      */ 
/*      */ public abstract class Scope
/*      */ {
/*      */   public static final int BLOCK_SCOPE = 1;
/*      */   public static final int CLASS_SCOPE = 3;
/*      */   public static final int COMPILATION_UNIT_SCOPE = 4;
/*      */   public static final int METHOD_SCOPE = 2;
/*      */   public static final int NOT_COMPATIBLE = -1;
/*      */   public static final int COMPATIBLE = 0;
/*      */   public static final int AUTOBOX_COMPATIBLE = 1;
/*      */   public static final int VARARGS_COMPATIBLE = 2;
/*      */   public static final int EQUAL_OR_MORE_SPECIFIC = -1;
/*      */   public static final int NOT_RELATED = 0;
/*      */   public static final int MORE_GENERIC = 1;
/*      */   public int kind;
/*      */   public Scope parent;
/*      */ 
/*      */   protected Scope(int kind, Scope parent)
/*      */   {
/*   49 */     this.kind = kind;
/*   50 */     this.parent = parent;
/*      */   }
/*      */ 
/*      */   public static int compareTypes(TypeBinding left, TypeBinding right)
/*      */   {
/*   60 */     if (left.isCompatibleWith(right))
/*   61 */       return -1;
/*   62 */     if (right.isCompatibleWith(left))
/*   63 */       return 1;
/*   64 */     return 0;
/*      */   }
/*      */ 
/*      */   public static TypeBinding convertEliminatingTypeVariables(TypeBinding originalType, ReferenceBinding genericType, int rank, Set eliminatedVariables)
/*      */   {
/*   72 */     if ((originalType.tagBits & 0x20000000) != 0L) {
/*   73 */       switch (originalType.kind()) {
/*      */       case 68:
/*   75 */         ArrayBinding originalArrayType = (ArrayBinding)originalType;
/*   76 */         TypeBinding originalLeafComponentType = originalArrayType.leafComponentType;
/*   77 */         TypeBinding substitute = convertEliminatingTypeVariables(originalLeafComponentType, genericType, rank, eliminatedVariables);
/*   78 */         if (substitute == originalLeafComponentType) break;
/*   79 */         return originalArrayType.environment.createArrayType(substitute.leafComponentType(), substitute.dimensions() + originalArrayType.dimensions());
/*      */       case 260:
/*   83 */         ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
/*   84 */         ReferenceBinding originalEnclosing = paramType.enclosingType();
/*   85 */         ReferenceBinding substitutedEnclosing = originalEnclosing;
/*   86 */         if (originalEnclosing != null) {
/*   87 */           substitutedEnclosing = (ReferenceBinding)convertEliminatingTypeVariables(originalEnclosing, genericType, rank, eliminatedVariables);
/*      */         }
/*   89 */         TypeBinding[] originalArguments = paramType.arguments;
/*   90 */         TypeBinding[] substitutedArguments = originalArguments;
/*   91 */         int i = 0; for (int length = originalArguments == null ? 0 : originalArguments.length; i < length; i++) {
/*   92 */           TypeBinding originalArgument = originalArguments[i];
/*   93 */           TypeBinding substitutedArgument = convertEliminatingTypeVariables(originalArgument, paramType.genericType(), i, eliminatedVariables);
/*   94 */           if (substitutedArgument != originalArgument) {
/*   95 */             if (substitutedArguments == originalArguments) {
/*   96 */               System.arraycopy(originalArguments, 0, substitutedArguments = new TypeBinding[length], 0, i);
/*      */             }
/*   98 */             substitutedArguments[i] = substitutedArgument;
/*   99 */           } else if (substitutedArguments != originalArguments) {
/*  100 */             substitutedArguments[i] = originalArgument;
/*      */           }
/*      */         }
/*  103 */         if ((originalEnclosing == substitutedEnclosing) && (originalArguments == substitutedArguments)) break;
/*  104 */         return paramType.environment.createParameterizedType(paramType.genericType(), substitutedArguments, substitutedEnclosing);
/*      */       case 4100:
/*  108 */         if (genericType == null) {
/*      */           break;
/*      */         }
/*  111 */         TypeVariableBinding originalVariable = (TypeVariableBinding)originalType;
/*  112 */         if ((eliminatedVariables != null) && (eliminatedVariables.contains(originalType))) {
/*  113 */           return originalVariable.environment.createWildcard(genericType, rank, null, null, 0);
/*      */         }
/*  115 */         TypeBinding originalUpperBound = originalVariable.upperBound();
/*  116 */         if (eliminatedVariables == null) {
/*  117 */           eliminatedVariables = new HashSet(2);
/*      */         }
/*  119 */         eliminatedVariables.add(originalVariable);
/*  120 */         TypeBinding substitutedUpperBound = convertEliminatingTypeVariables(originalUpperBound, genericType, rank, eliminatedVariables);
/*  121 */         eliminatedVariables.remove(originalVariable);
/*  122 */         return originalVariable.environment.createWildcard(genericType, rank, substitutedUpperBound, null, 1);
/*      */       case 1028:
/*  124 */         break;
/*      */       case 2052:
/*  126 */         ReferenceBinding currentType = (ReferenceBinding)originalType;
/*  127 */         ReferenceBinding originalEnclosing = currentType.enclosingType();
/*  128 */         ReferenceBinding substitutedEnclosing = originalEnclosing;
/*  129 */         if (originalEnclosing != null) {
/*  130 */           substitutedEnclosing = (ReferenceBinding)convertEliminatingTypeVariables(originalEnclosing, genericType, rank, eliminatedVariables);
/*      */         }
/*  132 */         TypeBinding[] originalArguments = currentType.typeVariables();
/*  133 */         TypeBinding[] substitutedArguments = originalArguments;
/*  134 */         int i = 0; for (int length = originalArguments == null ? 0 : originalArguments.length; i < length; i++) {
/*  135 */           TypeBinding originalArgument = originalArguments[i];
/*  136 */           TypeBinding substitutedArgument = convertEliminatingTypeVariables(originalArgument, currentType, i, eliminatedVariables);
/*  137 */           if (substitutedArgument != originalArgument) {
/*  138 */             if (substitutedArguments == originalArguments) {
/*  139 */               System.arraycopy(originalArguments, 0, substitutedArguments = new TypeBinding[length], 0, i);
/*      */             }
/*  141 */             substitutedArguments[i] = substitutedArgument;
/*  142 */           } else if (substitutedArguments != originalArguments) {
/*  143 */             substitutedArguments[i] = originalArgument;
/*      */           }
/*      */         }
/*  146 */         if ((originalEnclosing == substitutedEnclosing) && (originalArguments == substitutedArguments)) break;
/*  147 */         return ((TypeVariableBinding)originalArguments[0]).environment.createParameterizedType(genericType, substitutedArguments, substitutedEnclosing);
/*      */       case 516:
/*  151 */         WildcardBinding wildcard = (WildcardBinding)originalType;
/*  152 */         TypeBinding originalBound = wildcard.bound;
/*  153 */         TypeBinding substitutedBound = originalBound;
/*  154 */         if (originalBound == null) break;
/*  155 */         substitutedBound = convertEliminatingTypeVariables(originalBound, genericType, rank, eliminatedVariables);
/*  156 */         if (substitutedBound == originalBound) break;
/*  157 */         return wildcard.environment.createWildcard(wildcard.genericType, wildcard.rank, substitutedBound, null, wildcard.boundKind);
/*      */       case 8196:
/*  162 */         WildcardBinding intersection = (WildcardBinding)originalType;
/*  163 */         TypeBinding originalBound = intersection.bound;
/*  164 */         TypeBinding substitutedBound = originalBound;
/*  165 */         if (originalBound != null) {
/*  166 */           substitutedBound = convertEliminatingTypeVariables(originalBound, genericType, rank, eliminatedVariables);
/*      */         }
/*  168 */         TypeBinding[] originalOtherBounds = intersection.otherBounds;
/*  169 */         TypeBinding[] substitutedOtherBounds = originalOtherBounds;
/*  170 */         int i = 0; for (int length = originalOtherBounds == null ? 0 : originalOtherBounds.length; i < length; i++) {
/*  171 */           TypeBinding originalOtherBound = originalOtherBounds[i];
/*  172 */           TypeBinding substitutedOtherBound = convertEliminatingTypeVariables(originalOtherBound, genericType, rank, eliminatedVariables);
/*  173 */           if (substitutedOtherBound != originalOtherBound) {
/*  174 */             if (substitutedOtherBounds == originalOtherBounds) {
/*  175 */               System.arraycopy(originalOtherBounds, 0, substitutedOtherBounds = new TypeBinding[length], 0, i);
/*      */             }
/*  177 */             substitutedOtherBounds[i] = substitutedOtherBound;
/*  178 */           } else if (substitutedOtherBounds != originalOtherBounds) {
/*  179 */             substitutedOtherBounds[i] = originalOtherBound;
/*      */           }
/*      */         }
/*  182 */         if ((substitutedBound == originalBound) && (substitutedOtherBounds == originalOtherBounds)) break;
/*  183 */         return intersection.environment.createWildcard(intersection.genericType, intersection.rank, substitutedBound, substitutedOtherBounds, intersection.boundKind);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  188 */     return originalType;
/*      */   }
/*      */ 
/*      */   public static TypeBinding getBaseType(char[] name)
/*      */   {
/*  193 */     int length = name.length;
/*  194 */     if ((length > 2) && (length < 8)) {
/*  195 */       switch (name[0]) {
/*      */       case 'i':
/*  197 */         if ((length != 3) || (name[1] != 'n') || (name[2] != 't')) break;
/*  198 */         return TypeBinding.INT;
/*      */       case 'v':
/*  201 */         if ((length != 4) || (name[1] != 'o') || (name[2] != 'i') || (name[3] != 'd')) break;
/*  202 */         return TypeBinding.VOID;
/*      */       case 'b':
/*  205 */         if ((length == 7) && 
/*  206 */           (name[1] == 'o') && 
/*  207 */           (name[2] == 'o') && 
/*  208 */           (name[3] == 'l') && 
/*  209 */           (name[4] == 'e') && 
/*  210 */           (name[5] == 'a') && 
/*  211 */           (name[6] == 'n'))
/*  212 */           return TypeBinding.BOOLEAN;
/*  213 */         if ((length != 4) || (name[1] != 'y') || (name[2] != 't') || (name[3] != 'e')) break;
/*  214 */         return TypeBinding.BYTE;
/*      */       case 'c':
/*  217 */         if ((length != 4) || (name[1] != 'h') || (name[2] != 'a') || (name[3] != 'r')) break;
/*  218 */         return TypeBinding.CHAR;
/*      */       case 'd':
/*  221 */         if ((length != 6) || 
/*  222 */           (name[1] != 'o') || 
/*  223 */           (name[2] != 'u') || 
/*  224 */           (name[3] != 'b') || 
/*  225 */           (name[4] != 'l') || 
/*  226 */           (name[5] != 'e')) break;
/*  227 */         return TypeBinding.DOUBLE;
/*      */       case 'f':
/*  230 */         if ((length != 5) || 
/*  231 */           (name[1] != 'l') || 
/*  232 */           (name[2] != 'o') || 
/*  233 */           (name[3] != 'a') || 
/*  234 */           (name[4] != 't')) break;
/*  235 */         return TypeBinding.FLOAT;
/*      */       case 'l':
/*  238 */         if ((length != 4) || (name[1] != 'o') || (name[2] != 'n') || (name[3] != 'g')) break;
/*  239 */         return TypeBinding.LONG;
/*      */       case 's':
/*  242 */         if ((length != 5) || 
/*  243 */           (name[1] != 'h') || 
/*  244 */           (name[2] != 'o') || 
/*  245 */           (name[3] != 'r') || 
/*  246 */           (name[4] != 't')) break;
/*  247 */         return TypeBinding.SHORT;
/*      */       }
/*      */     }
/*  250 */     return null;
/*      */   }
/*      */ 
/*      */   public static ReferenceBinding[] greaterLowerBound(ReferenceBinding[] types)
/*      */   {
/*  255 */     if (types == null) return null;
/*  256 */     int length = types.length;
/*  257 */     if (length == 0) return null;
/*  258 */     ReferenceBinding[] result = types;
/*  259 */     int removed = 0;
/*  260 */     for (int i = 0; i < length; i++) {
/*  261 */       ReferenceBinding iType = result[i];
/*  262 */       if (iType != null) {
/*  263 */         for (int j = 0; j < length; j++)
/*  264 */           if (i != j) {
/*  265 */             ReferenceBinding jType = result[j];
/*  266 */             if ((jType == null) || 
/*  267 */               (!iType.isCompatibleWith(jType))) continue;
/*  268 */             if (result == types) {
/*  269 */               System.arraycopy(result, 0, result = new ReferenceBinding[length], 0, length);
/*      */             }
/*  271 */             result[j] = null;
/*  272 */             removed++;
/*      */           }
/*      */       }
/*      */     }
/*  276 */     if (removed == 0) return result;
/*  277 */     if (length == removed) return null;
/*  278 */     ReferenceBinding[] trimmedResult = new ReferenceBinding[length - removed];
/*  279 */     int i = 0; for (int index = 0; i < length; i++) {
/*  280 */       ReferenceBinding iType = result[i];
/*  281 */       if (iType != null) {
/*  282 */         trimmedResult[(index++)] = iType;
/*      */       }
/*      */     }
/*  285 */     return trimmedResult;
/*      */   }
/*      */ 
/*      */   public static TypeBinding[] greaterLowerBound(TypeBinding[] types)
/*      */   {
/*  290 */     if (types == null) return null;
/*  291 */     int length = types.length;
/*  292 */     if (length == 0) return null;
/*  293 */     TypeBinding[] result = types;
/*  294 */     int removed = 0;
/*  295 */     for (int i = 0; i < length; i++) {
/*  296 */       TypeBinding iType = result[i];
/*  297 */       if (iType != null) {
/*  298 */         for (int j = 0; j < length; j++)
/*  299 */           if (i != j) {
/*  300 */             TypeBinding jType = result[j];
/*  301 */             if ((jType == null) || 
/*  302 */               (!iType.isCompatibleWith(jType))) continue;
/*  303 */             if (result == types) {
/*  304 */               System.arraycopy(result, 0, result = new TypeBinding[length], 0, length);
/*      */             }
/*  306 */             result[j] = null;
/*  307 */             removed++;
/*      */           }
/*      */       }
/*      */     }
/*  311 */     if (removed == 0) return result;
/*  312 */     if (length == removed) return null;
/*  313 */     TypeBinding[] trimmedResult = new TypeBinding[length - removed];
/*  314 */     int i = 0; for (int index = 0; i < length; i++) {
/*  315 */       TypeBinding iType = result[i];
/*  316 */       if (iType != null) {
/*  317 */         trimmedResult[(index++)] = iType;
/*      */       }
/*      */     }
/*  320 */     return trimmedResult;
/*      */   }
/*      */ 
/*      */   public static ReferenceBinding[] substitute(Substitution substitution, ReferenceBinding[] originalTypes)
/*      */   {
/*  328 */     if (originalTypes == null) return null;
/*  329 */     ReferenceBinding[] substitutedTypes = originalTypes;
/*  330 */     int i = 0; for (int length = originalTypes.length; i < length; i++) {
/*  331 */       ReferenceBinding originalType = originalTypes[i];
/*  332 */       TypeBinding substitutedType = substitute(substitution, originalType);
/*  333 */       if (!(substitutedType instanceof ReferenceBinding)) {
/*  334 */         return null;
/*      */       }
/*  336 */       if (substitutedType != originalType) {
/*  337 */         if (substitutedTypes == originalTypes) {
/*  338 */           System.arraycopy(originalTypes, 0, substitutedTypes = new ReferenceBinding[length], 0, i);
/*      */         }
/*  340 */         substitutedTypes[i] = ((ReferenceBinding)substitutedType);
/*  341 */       } else if (substitutedTypes != originalTypes) {
/*  342 */         substitutedTypes[i] = originalType;
/*      */       }
/*      */     }
/*  345 */     return substitutedTypes;
/*      */   }
/*      */ 
/*      */   public static TypeBinding substitute(Substitution substitution, TypeBinding originalType)
/*      */   {
/*  360 */     if (originalType == null) return null;
/*  361 */     switch (originalType.kind())
/*      */     {
/*      */     case 4100:
/*  364 */       return substitution.substitute((TypeVariableBinding)originalType);
/*      */     case 260:
/*  367 */       ParameterizedTypeBinding originalParameterizedType = (ParameterizedTypeBinding)originalType;
/*  368 */       ReferenceBinding originalEnclosing = originalType.enclosingType();
/*  369 */       ReferenceBinding substitutedEnclosing = originalEnclosing;
/*  370 */       if (originalEnclosing != null) {
/*  371 */         substitutedEnclosing = (ReferenceBinding)substitute(substitution, originalEnclosing);
/*      */       }
/*  373 */       TypeBinding[] originalArguments = originalParameterizedType.arguments;
/*  374 */       TypeBinding[] substitutedArguments = originalArguments;
/*  375 */       if (originalArguments != null) {
/*  376 */         if (substitution.isRawSubstitution()) {
/*  377 */           return originalParameterizedType.environment.createRawType(originalParameterizedType.genericType(), substitutedEnclosing);
/*      */         }
/*  379 */         substitutedArguments = substitute(substitution, originalArguments);
/*      */       }
/*  381 */       if ((substitutedArguments == originalArguments) && (substitutedEnclosing == originalEnclosing)) break;
/*  382 */       return originalParameterizedType.environment.createParameterizedType(
/*  383 */         originalParameterizedType.genericType(), substitutedArguments, substitutedEnclosing);
/*      */     case 68:
/*  388 */       ArrayBinding originalArrayType = (ArrayBinding)originalType;
/*  389 */       TypeBinding originalLeafComponentType = originalArrayType.leafComponentType;
/*  390 */       TypeBinding substitute = substitute(substitution, originalLeafComponentType);
/*  391 */       if (substitute == originalLeafComponentType) break;
/*  392 */       return originalArrayType.environment.createArrayType(substitute.leafComponentType(), substitute.dimensions() + originalType.dimensions());
/*      */     case 516:
/*      */     case 8196:
/*  398 */       WildcardBinding wildcard = (WildcardBinding)originalType;
/*  399 */       if (wildcard.boundKind == 0) break;
/*  400 */       TypeBinding originalBound = wildcard.bound;
/*  401 */       TypeBinding substitutedBound = substitute(substitution, originalBound);
/*  402 */       TypeBinding[] originalOtherBounds = wildcard.otherBounds;
/*  403 */       TypeBinding[] substitutedOtherBounds = substitute(substitution, originalOtherBounds);
/*  404 */       if ((substitutedBound == originalBound) && (originalOtherBounds == substitutedOtherBounds)) break;
/*  405 */       return wildcard.environment.createWildcard(wildcard.genericType, wildcard.rank, substitutedBound, substitutedOtherBounds, wildcard.boundKind);
/*      */     case 4:
/*  411 */       if (!originalType.isMemberType()) break;
/*  412 */       ReferenceBinding originalReferenceType = (ReferenceBinding)originalType;
/*  413 */       ReferenceBinding originalEnclosing = originalType.enclosingType();
/*  414 */       ReferenceBinding substitutedEnclosing = originalEnclosing;
/*  415 */       if (originalEnclosing != null) {
/*  416 */         substitutedEnclosing = (ReferenceBinding)substitute(substitution, originalEnclosing);
/*      */       }
/*      */ 
/*  420 */       if (substitutedEnclosing == originalEnclosing) break;
/*  421 */       return substitution.isRawSubstitution() ? 
/*  422 */         substitution.environment().createRawType(originalReferenceType, substitutedEnclosing) : 
/*  423 */         substitution.environment().createParameterizedType(originalReferenceType, null, substitutedEnclosing);
/*      */     case 2052:
/*  427 */       ReferenceBinding originalReferenceType = (ReferenceBinding)originalType;
/*  428 */       ReferenceBinding originalEnclosing = originalType.enclosingType();
/*  429 */       ReferenceBinding substitutedEnclosing = originalEnclosing;
/*  430 */       if (originalEnclosing != null) {
/*  431 */         substitutedEnclosing = (ReferenceBinding)substitute(substitution, originalEnclosing);
/*      */       }
/*      */ 
/*  434 */       if (substitution.isRawSubstitution()) {
/*  435 */         return substitution.environment().createRawType(originalReferenceType, substitutedEnclosing);
/*      */       }
/*      */ 
/*  438 */       TypeBinding[] originalArguments = originalReferenceType.typeVariables();
/*  439 */       TypeBinding[] substitutedArguments = substitute(substitution, originalArguments);
/*  440 */       return substitution.environment().createParameterizedType(originalReferenceType, substitutedArguments, substitutedEnclosing);
/*      */     }
/*  442 */     return originalType;
/*      */   }
/*      */ 
/*      */   public static TypeBinding[] substitute(Substitution substitution, TypeBinding[] originalTypes)
/*      */   {
/*  450 */     if (originalTypes == null) return null;
/*  451 */     TypeBinding[] substitutedTypes = originalTypes;
/*  452 */     int i = 0; for (int length = originalTypes.length; i < length; i++) {
/*  453 */       TypeBinding originalType = originalTypes[i];
/*  454 */       TypeBinding substitutedParameter = substitute(substitution, originalType);
/*  455 */       if (substitutedParameter != originalType) {
/*  456 */         if (substitutedTypes == originalTypes) {
/*  457 */           System.arraycopy(originalTypes, 0, substitutedTypes = new TypeBinding[length], 0, i);
/*      */         }
/*  459 */         substitutedTypes[i] = substitutedParameter;
/*  460 */       } else if (substitutedTypes != originalTypes) {
/*  461 */         substitutedTypes[i] = originalType;
/*      */       }
/*      */     }
/*  464 */     return substitutedTypes;
/*      */   }
/*      */ 
/*      */   public TypeBinding boxing(TypeBinding type)
/*      */   {
/*  471 */     if (type.isBaseType())
/*  472 */       return environment().computeBoxingType(type);
/*  473 */     return type;
/*      */   }
/*      */ 
/*      */   public final ClassScope classScope() {
/*  477 */     Scope scope = this;
/*      */     do {
/*  479 */       if ((scope instanceof ClassScope))
/*  480 */         return (ClassScope)scope;
/*  481 */       scope = scope.parent;
/*  482 */     }while (scope != null);
/*  483 */     return null;
/*      */   }
/*      */ 
/*      */   public final CompilationUnitScope compilationUnitScope() {
/*  487 */     Scope lastScope = null;
/*  488 */     Scope scope = this;
/*      */     do {
/*  490 */       lastScope = scope;
/*  491 */       scope = scope.parent;
/*  492 */     }while (scope != null);
/*  493 */     return (CompilationUnitScope)lastScope;
/*      */   }
/*      */ 
/*      */   public final CompilerOptions compilerOptions()
/*      */   {
/*  501 */     return compilationUnitScope().environment.globalOptions;
/*      */   }
/*      */ 
/*      */   protected final MethodBinding computeCompatibleMethod(MethodBinding method, TypeBinding[] arguments, InvocationSite invocationSite)
/*      */   {
/*  511 */     TypeBinding[] genericTypeArguments = invocationSite.genericTypeArguments();
/*  512 */     TypeBinding[] parameters = method.parameters;
/*  513 */     TypeVariableBinding[] typeVariables = method.typeVariables;
/*  514 */     if ((parameters == arguments) && 
/*  515 */       ((method.returnType.tagBits & 0x20000000) == 0L) && 
/*  516 */       (genericTypeArguments == null) && 
/*  517 */       (typeVariables == Binding.NO_TYPE_VARIABLES)) {
/*  518 */       return method;
/*      */     }
/*  520 */     int argLength = arguments.length;
/*  521 */     int paramLength = parameters.length;
/*  522 */     boolean isVarArgs = method.isVarargs();
/*  523 */     if ((argLength != paramLength) && (
/*  524 */       (!isVarArgs) || (argLength < paramLength - 1))) {
/*  525 */       return null;
/*      */     }
/*  527 */     if (typeVariables != Binding.NO_TYPE_VARIABLES) {
/*  528 */       TypeBinding[] newArgs = (TypeBinding[])null;
/*  529 */       for (int i = 0; i < argLength; i++) {
/*  530 */         TypeBinding param = i < paramLength ? parameters[i] : parameters[(paramLength - 1)];
/*  531 */         if (arguments[i].isBaseType() != param.isBaseType()) {
/*  532 */           if (newArgs == null) {
/*  533 */             newArgs = new TypeBinding[argLength];
/*  534 */             System.arraycopy(arguments, 0, newArgs, 0, argLength);
/*      */           }
/*  536 */           newArgs[i] = environment().computeBoxingType(arguments[i]);
/*      */         }
/*      */       }
/*  539 */       if (newArgs != null)
/*  540 */         arguments = newArgs;
/*  541 */       method = ParameterizedGenericMethodBinding.computeCompatibleMethod(method, arguments, this, invocationSite);
/*  542 */       if (method == null) return null;
/*  543 */       if (!method.isValidBinding()) return method; 
/*      */     }
/*  544 */     else if ((genericTypeArguments != null) && (compilerOptions().complianceLevel < 3342336L)) {
/*  545 */       if ((method instanceof ParameterizedGenericMethodBinding)) {
/*  546 */         if (!((ParameterizedGenericMethodBinding)method).wasInferred)
/*      */         {
/*  548 */           return new ProblemMethodBinding(method, method.selector, genericTypeArguments, 13);
/*      */         }
/*  549 */       } else if ((!method.isOverriding()) || (!isOverriddenMethodGeneric(method))) {
/*  550 */         return new ProblemMethodBinding(method, method.selector, genericTypeArguments, 11);
/*      */       }
/*      */     }
/*      */ 
/*  554 */     if (parameterCompatibilityLevel(method, arguments) > -1)
/*  555 */       return method;
/*  556 */     if (genericTypeArguments != null)
/*  557 */       return new ProblemMethodBinding(method, method.selector, arguments, 12);
/*  558 */     return null;
/*      */   }
/*      */ 
/*      */   protected boolean connectTypeVariables(TypeParameter[] typeParameters, boolean checkForErasedCandidateCollisions)
/*      */   {
/*  567 */     if ((typeParameters == null) || (compilerOptions().sourceLevel < 3211264L)) return true;
/*  568 */     Map invocations = new HashMap(2);
/*  569 */     boolean noProblems = true;
/*      */ 
/*  571 */     int i = 0; for (int paramLength = typeParameters.length; i < paramLength; i++) {
/*  572 */       TypeParameter typeParameter = typeParameters[i];
/*  573 */       TypeVariableBinding typeVariable = typeParameter.binding;
/*  574 */       if (typeVariable == null) return false;
/*      */ 
/*  576 */       typeVariable.superclass = getJavaLangObject();
/*  577 */       typeVariable.superInterfaces = Binding.NO_SUPERINTERFACES;
/*      */ 
/*  579 */       typeVariable.firstBound = null;
/*      */     }
/*  581 */     int i = 0; for (int paramLength = typeParameters.length; i < paramLength; i++) {
/*  582 */       TypeParameter typeParameter = typeParameters[i];
/*  583 */       TypeVariableBinding typeVariable = typeParameter.binding;
/*  584 */       TypeReference typeRef = typeParameter.type;
/*  585 */       if (typeRef == null)
/*      */         continue;
/*  587 */       boolean isFirstBoundTypeVariable = false;
/*  588 */       TypeBinding superType = this.kind == 2 ? 
/*  589 */         typeRef.resolveType((BlockScope)this, false) : 
/*  590 */         typeRef.resolveType((ClassScope)this);
/*  591 */       if (superType == null) {
/*  592 */         typeVariable.tagBits |= 131072L;
/*      */       } else {
/*  594 */         typeRef.resolvedType = superType;
/*      */ 
/*  596 */         switch (superType.kind()) {
/*      */         case 68:
/*  598 */           problemReporter().boundCannotBeArray(typeRef, superType);
/*  599 */           typeVariable.tagBits |= 131072L;
/*  600 */           break;
/*      */         case 4100:
/*  602 */           isFirstBoundTypeVariable = true;
/*  603 */           TypeVariableBinding varSuperType = (TypeVariableBinding)superType;
/*  604 */           if ((varSuperType.rank < typeVariable.rank) || (varSuperType.declaringElement != typeVariable.declaringElement) || 
/*  605 */             (compilerOptions().complianceLevel > 3276800L)) break;
/*  606 */           problemReporter().forwardTypeVariableReference(typeParameter, varSuperType);
/*  607 */           typeVariable.tagBits |= 131072L;
/*  608 */           break;
/*      */         default:
/*  613 */           if (!((ReferenceBinding)superType).isFinal()) break;
/*  614 */           problemReporter().finalVariableBound(typeVariable, typeRef);
/*      */         }
/*      */ 
/*  618 */         ReferenceBinding superRefType = (ReferenceBinding)superType;
/*  619 */         if (!superType.isInterface())
/*  620 */           typeVariable.superclass = superRefType;
/*      */         else {
/*  622 */           typeVariable.superInterfaces = new ReferenceBinding[] { superRefType };
/*      */         }
/*  624 */         typeVariable.tagBits |= superType.tagBits & 0x800;
/*  625 */         typeVariable.firstBound = superRefType;
/*      */       }
/*      */ 
/*  628 */       TypeReference[] boundRefs = typeParameter.bounds;
/*  629 */       if (boundRefs != null) {
/*  630 */         int j = 0; for (int boundLength = boundRefs.length; j < boundLength; j++) {
/*  631 */           typeRef = boundRefs[j];
/*  632 */           superType = this.kind == 2 ? 
/*  633 */             typeRef.resolveType((BlockScope)this, false) : 
/*  634 */             typeRef.resolveType((ClassScope)this);
/*  635 */           if (superType == null) {
/*  636 */             typeVariable.tagBits |= 131072L;
/*      */           }
/*      */           else {
/*  639 */             typeVariable.tagBits |= superType.tagBits & 0x800;
/*  640 */             boolean didAlreadyComplain = !typeRef.resolvedType.isValidBinding();
/*  641 */             if ((isFirstBoundTypeVariable) && (j == 0)) {
/*  642 */               problemReporter().noAdditionalBoundAfterTypeVariable(typeRef);
/*  643 */               typeVariable.tagBits |= 131072L;
/*  644 */               didAlreadyComplain = true;
/*      */             } else {
/*  646 */               if (superType.isArrayType()) {
/*  647 */                 if (didAlreadyComplain) continue;
/*  648 */                 problemReporter().boundCannotBeArray(typeRef, superType);
/*  649 */                 typeVariable.tagBits |= 131072L;
/*      */ 
/*  651 */                 continue;
/*      */               }
/*  653 */               if (!superType.isInterface()) {
/*  654 */                 if (didAlreadyComplain) continue;
/*  655 */                 problemReporter().boundMustBeAnInterface(typeRef, superType);
/*  656 */                 typeVariable.tagBits |= 131072L;
/*      */ 
/*  658 */                 continue;
/*      */               }
/*      */             }
/*      */ 
/*  662 */             if ((checkForErasedCandidateCollisions) && (typeVariable.firstBound == typeVariable.superclass) && 
/*  663 */               (hasErasedCandidatesCollisions(superType, typeVariable.superclass, invocations, typeVariable, typeRef)))
/*      */             {
/*      */               continue;
/*      */             }
/*      */ 
/*  668 */             ReferenceBinding superRefType = (ReferenceBinding)superType;
/*  669 */             int index = typeVariable.superInterfaces.length;
/*      */             while (true) { ReferenceBinding previousInterface = typeVariable.superInterfaces[index];
/*  671 */               if (previousInterface == superRefType) {
/*  672 */                 problemReporter().duplicateBounds(typeRef, superType);
/*  673 */                 typeVariable.tagBits |= 131072L;
/*      */               }
/*      */               else {
/*  676 */                 if ((checkForErasedCandidateCollisions) && 
/*  677 */                   (hasErasedCandidatesCollisions(superType, previousInterface, invocations, typeVariable, typeRef)))
/*      */                   break;
/*  669 */                 index--; if (index >= 0)
/*      */                 {
/*      */                   continue;
/*      */                 }
/*      */ 
/*  682 */                 int size = typeVariable.superInterfaces.length;
/*  683 */                 System.arraycopy(typeVariable.superInterfaces, 0, typeVariable.superInterfaces = new ReferenceBinding[size + 1], 0, size);
/*  684 */                 typeVariable.superInterfaces[size] = superRefType;
/*      */               } }
/*      */           }
/*      */         }
/*      */       }
/*  688 */       noProblems &= (typeVariable.tagBits & 0x20000) == 0L;
/*      */     }
/*  690 */     return noProblems;
/*      */   }
/*      */ 
/*      */   public ArrayBinding createArrayType(TypeBinding type, int dimension) {
/*  694 */     if (type.isValidBinding()) {
/*  695 */       return environment().createArrayType(type, dimension);
/*      */     }
/*  697 */     return new ArrayBinding(type, dimension, environment());
/*      */   }
/*      */ 
/*      */   public TypeVariableBinding[] createTypeVariables(TypeParameter[] typeParameters, Binding declaringElement)
/*      */   {
/*  702 */     if ((typeParameters == null) || (compilerOptions().sourceLevel < 3211264L)) {
/*  703 */       return Binding.NO_TYPE_VARIABLES;
/*      */     }
/*  705 */     PackageBinding unitPackage = compilationUnitScope().fPackage;
/*  706 */     int length = typeParameters.length;
/*  707 */     TypeVariableBinding[] typeVariableBindings = new TypeVariableBinding[length];
/*  708 */     int count = 0;
/*  709 */     for (int i = 0; i < length; i++) {
/*  710 */       TypeParameter typeParameter = typeParameters[i];
/*  711 */       TypeVariableBinding parameterBinding = new TypeVariableBinding(typeParameter.name, declaringElement, i, environment());
/*  712 */       parameterBinding.fPackage = unitPackage;
/*  713 */       typeParameter.binding = parameterBinding;
/*      */ 
/*  716 */       for (int j = 0; j < count; j++) {
/*  717 */         TypeVariableBinding knownVar = typeVariableBindings[j];
/*  718 */         if (CharOperation.equals(knownVar.sourceName, typeParameter.name))
/*  719 */           problemReporter().duplicateTypeParameterInType(typeParameter);
/*      */       }
/*  721 */       typeVariableBindings[(count++)] = parameterBinding;
/*      */     }
/*      */ 
/*  740 */     if (count != length)
/*  741 */       System.arraycopy(typeVariableBindings, 0, typeVariableBindings = new TypeVariableBinding[count], 0, count);
/*  742 */     return typeVariableBindings;
/*      */   }
/*      */ 
/*      */   public final ClassScope enclosingClassScope() {
/*  746 */     Scope scope = this;
/*  747 */     while ((scope = scope.parent) != null) {
/*  748 */       if ((scope instanceof ClassScope)) return (ClassScope)scope;
/*      */     }
/*  750 */     return null;
/*      */   }
/*      */ 
/*      */   public final MethodScope enclosingMethodScope() {
/*  754 */     Scope scope = this;
/*  755 */     while ((scope = scope.parent) != null) {
/*  756 */       if ((scope instanceof MethodScope)) return (MethodScope)scope;
/*      */     }
/*  758 */     return null;
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding enclosingReceiverType()
/*      */   {
/*  764 */     Scope scope = this;
/*      */     do {
/*  766 */       if ((scope instanceof ClassScope)) {
/*  767 */         return environment().convertToParameterizedType(((ClassScope)scope).referenceContext.binding);
/*      */       }
/*  769 */       scope = scope.parent;
/*  770 */     }while (scope != null);
/*  771 */     return null;
/*      */   }
/*      */ 
/*      */   public ReferenceContext enclosingReferenceContext()
/*      */   {
/*  778 */     Scope current = this;
/*  779 */     while ((current = 
/*  787 */       current.parent) != null) {
/*  780 */       switch (current.kind) {
/*      */       case 2:
/*  782 */         return ((MethodScope)current).referenceContext;
/*      */       case 3:
/*  784 */         return ((ClassScope)current).referenceContext;
/*      */       case 4:
/*  786 */         return ((CompilationUnitScope)current).referenceContext;
/*      */       }
/*      */     }
/*  789 */     return null;
/*      */   }
/*      */ 
/*      */   public final SourceTypeBinding enclosingSourceType()
/*      */   {
/*  795 */     Scope scope = this;
/*      */     do {
/*  797 */       if ((scope instanceof ClassScope))
/*  798 */         return ((ClassScope)scope).referenceContext.binding;
/*  799 */       scope = scope.parent;
/*  800 */     }while (scope != null);
/*  801 */     return null;
/*      */   }
/*      */ 
/*      */   public final LookupEnvironment environment() {
/*  805 */     Scope unitScope = this;
/*      */     Scope scope;
/*  806 */     while ((scope = unitScope.parent) != null)
/*      */     {
/*      */       Scope scope;
/*  807 */       unitScope = scope;
/*  808 */     }return ((CompilationUnitScope)unitScope).environment;
/*      */   }
/*      */ 
/*      */   protected MethodBinding findDefaultAbstractMethod(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite, ReferenceBinding classHierarchyStart, ObjectVector found, MethodBinding concreteMatch)
/*      */   {
/*  821 */     int startFoundSize = found.size;
/*  822 */     ReferenceBinding currentType = classHierarchyStart;
/*  823 */     while (currentType != null) {
/*  824 */       findMethodInSuperInterfaces(currentType, selector, found, invocationSite);
/*  825 */       currentType = currentType.superclass();
/*      */     }
/*  827 */     MethodBinding[] candidates = (MethodBinding[])null;
/*  828 */     int candidatesCount = 0;
/*  829 */     MethodBinding problemMethod = null;
/*  830 */     int foundSize = found.size;
/*  831 */     if (foundSize > startFoundSize)
/*      */     {
/*  833 */       for (int i = startFoundSize; i < foundSize; i++) {
/*  834 */         MethodBinding methodBinding = (MethodBinding)found.elementAt(i);
/*  835 */         MethodBinding compatibleMethod = computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
/*  836 */         if (compatibleMethod != null) {
/*  837 */           if (compatibleMethod.isValidBinding()) {
/*  838 */             if ((concreteMatch != null) && (environment().methodVerifier().areMethodsCompatible(concreteMatch, compatibleMethod)))
/*      */               continue;
/*  840 */             if (candidatesCount == 0) {
/*  841 */               candidates = new MethodBinding[foundSize - startFoundSize + 1];
/*  842 */               if (concreteMatch != null)
/*  843 */                 candidates[(candidatesCount++)] = concreteMatch;
/*      */             }
/*  845 */             candidates[(candidatesCount++)] = compatibleMethod;
/*  846 */           } else if (problemMethod == null) {
/*  847 */             problemMethod = compatibleMethod;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  853 */     if (candidatesCount < 2) {
/*  854 */       if (concreteMatch == null) {
/*  855 */         if (candidatesCount == 0)
/*  856 */           return problemMethod;
/*  857 */         concreteMatch = candidates[0];
/*      */       }
/*  859 */       compilationUnitScope().recordTypeReferences(concreteMatch.thrownExceptions);
/*  860 */       return concreteMatch;
/*      */     }
/*      */ 
/*  863 */     if (compilerOptions().complianceLevel >= 3145728L)
/*  864 */       return mostSpecificMethodBinding(candidates, candidatesCount, argumentTypes, invocationSite, receiverType);
/*  865 */     return mostSpecificInterfaceMethodBinding(candidates, candidatesCount, invocationSite);
/*      */   }
/*      */ 
/*      */   public ReferenceBinding findDirectMemberType(char[] typeName, ReferenceBinding enclosingType)
/*      */   {
/*  870 */     if ((enclosingType.tagBits & 0x10000) != 0L) {
/*  871 */       return null;
/*      */     }
/*  873 */     ReferenceBinding enclosingReceiverType = enclosingReceiverType();
/*  874 */     CompilationUnitScope unitScope = compilationUnitScope();
/*  875 */     unitScope.recordReference(enclosingType, typeName);
/*  876 */     ReferenceBinding memberType = enclosingType.getMemberType(typeName);
/*  877 */     if (memberType != null) {
/*  878 */       unitScope.recordTypeReference(memberType);
/*  879 */       if (enclosingReceiverType == null ? 
/*  880 */         memberType.canBeSeenBy(getCurrentPackage()) : 
/*  881 */         memberType.canBeSeenBy(enclosingType, enclosingReceiverType))
/*  882 */         return memberType;
/*  883 */       return new ProblemReferenceBinding(new char[][] { typeName }, memberType, 2);
/*      */     }
/*  885 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding findExactMethod(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite)
/*      */   {
/*  890 */     CompilationUnitScope unitScope = compilationUnitScope();
/*  891 */     unitScope.recordTypeReferences(argumentTypes);
/*  892 */     MethodBinding exactMethod = receiverType.getExactMethod(selector, argumentTypes, unitScope);
/*  893 */     if ((exactMethod != null) && (exactMethod.typeVariables == Binding.NO_TYPE_VARIABLES) && (!exactMethod.isBridge()))
/*      */     {
/*  895 */       if (compilerOptions().sourceLevel >= 3211264L) {
/*  896 */         int i = argumentTypes.length;
/*      */         do { if (isPossibleSubtypeOfRawType(argumentTypes[i]))
/*  898 */             return null;
/*  896 */           i--; } while (i >= 0);
/*      */       }
/*      */ 
/*  901 */       unitScope.recordTypeReferences(exactMethod.thrownExceptions);
/*  902 */       if ((exactMethod.isAbstract()) && (exactMethod.thrownExceptions != Binding.NO_EXCEPTIONS)) {
/*  903 */         return null;
/*      */       }
/*  905 */       if ((receiverType.isInterface()) || (exactMethod.canBeSeenBy(receiverType, invocationSite, this))) {
/*  906 */         if ((argumentTypes == Binding.NO_PARAMETERS) && 
/*  907 */           (CharOperation.equals(selector, TypeConstants.GETCLASS)) && 
/*  908 */           (exactMethod.returnType.isParameterizedType())) {
/*  909 */           return ParameterizedMethodBinding.instantiateGetClass(receiverType, exactMethod, this);
/*      */         }
/*      */ 
/*  912 */         if (invocationSite.genericTypeArguments() != null) {
/*  913 */           exactMethod = computeCompatibleMethod(exactMethod, argumentTypes, invocationSite);
/*      */         }
/*  915 */         return exactMethod;
/*      */       }
/*      */     }
/*  918 */     return null;
/*      */   }
/*      */ 
/*      */   public FieldBinding findField(TypeBinding receiverType, char[] fieldName, InvocationSite invocationSite, boolean needResolve)
/*      */   {
/*  933 */     CompilationUnitScope unitScope = compilationUnitScope();
/*  934 */     unitScope.recordTypeReference(receiverType);
/*      */     TypeBinding leafType;
/*      */     TypeBinding leafType;
/*  938 */     switch (receiverType.kind()) {
/*      */     case 132:
/*  940 */       return null;
/*      */     case 516:
/*      */     case 4100:
/*      */     case 8196:
/*  944 */       TypeBinding receiverErasure = receiverType.erasure();
/*  945 */       if (!receiverErasure.isArrayType()) break label189;
/*  947 */       leafType = receiverErasure.leafComponentType();
/*  948 */       break;
/*      */     case 68:
/*  950 */       leafType = receiverType.leafComponentType();
/*  951 */       break;
/*      */     default:
/*  953 */       break;
/*      */     }
/*      */     TypeBinding leafType;
/*  955 */     if (((leafType instanceof ReferenceBinding)) && 
/*  956 */       (!((ReferenceBinding)leafType).canBeSeenBy(this)))
/*  957 */       return new ProblemFieldBinding((ReferenceBinding)leafType, fieldName, 8);
/*  958 */     if (CharOperation.equals(fieldName, TypeConstants.LENGTH)) {
/*  959 */       if ((leafType.tagBits & 0x80) != 0L) {
/*  960 */         return new ProblemFieldBinding(ArrayBinding.ArrayLength, null, fieldName, 1);
/*      */       }
/*  962 */       return ArrayBinding.ArrayLength;
/*      */     }
/*  964 */     return null;
/*      */ 
/*  967 */     label189: ReferenceBinding currentType = (ReferenceBinding)receiverType;
/*  968 */     if (!currentType.canBeSeenBy(this)) {
/*  969 */       return new ProblemFieldBinding(currentType, fieldName, 8);
/*      */     }
/*  971 */     currentType.initializeForStaticImports();
/*  972 */     FieldBinding field = currentType.getField(fieldName, needResolve);
/*  973 */     if (field != null) {
/*  974 */       if (invocationSite == null ? 
/*  975 */         field.canBeSeenBy(getCurrentPackage()) : 
/*  976 */         field.canBeSeenBy(currentType, invocationSite, this))
/*  977 */         return field;
/*  978 */       return new ProblemFieldBinding(field, field.declaringClass, fieldName, 2);
/*      */     }
/*      */ 
/*  981 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/*  982 */     int nextPosition = 0;
/*  983 */     FieldBinding visibleField = null;
/*  984 */     boolean keepLooking = true;
/*  985 */     FieldBinding notVisibleField = null;
/*      */ 
/*  987 */     while (keepLooking) {
/*  988 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*  989 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/*  990 */         if (interfacesToVisit == null) {
/*  991 */           interfacesToVisit = itsInterfaces;
/*  992 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/*  994 */           int itsLength = itsInterfaces.length;
/*  995 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/*  996 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  997 */           for (int a = 0; a < itsLength; a++) {
/*  998 */             ReferenceBinding next = itsInterfaces[a];
/*  999 */             int b = 0;
/* 1000 */             while (next != interfacesToVisit[b])
/*      */             {
/*  999 */               b++; if (b < nextPosition)
/*      */                 continue;
/* 1001 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/* 1005 */       if ((currentType = currentType.superclass()) == null) {
/*      */         break;
/*      */       }
/* 1008 */       unitScope.recordTypeReference(currentType);
/* 1009 */       currentType.initializeForStaticImports();
/* 1010 */       currentType = (ReferenceBinding)currentType.capture(this, invocationSite == null ? 0 : invocationSite.sourceEnd());
/* 1011 */       if ((field = currentType.getField(fieldName, needResolve)) != null) {
/* 1012 */         keepLooking = false;
/* 1013 */         if (field.canBeSeenBy(receiverType, invocationSite, this)) {
/* 1014 */           if (visibleField == null)
/* 1015 */             visibleField = field;
/*      */           else
/* 1017 */             return new ProblemFieldBinding(visibleField, visibleField.declaringClass, fieldName, 3);
/*      */         }
/* 1019 */         else if (notVisibleField == null) {
/* 1020 */           notVisibleField = field;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1026 */     if (interfacesToVisit != null) {
/* 1027 */       ProblemFieldBinding ambiguous = null;
/* 1028 */       for (int i = 0; i < nextPosition; i++) {
/* 1029 */         ReferenceBinding anInterface = interfacesToVisit[i];
/* 1030 */         unitScope.recordTypeReference(anInterface);
/*      */ 
/* 1032 */         if ((field = anInterface.getField(fieldName, true)) != null) {
/* 1033 */           if (visibleField == null) {
/* 1034 */             visibleField = field;
/*      */           } else {
/* 1036 */             ambiguous = new ProblemFieldBinding(visibleField, visibleField.declaringClass, fieldName, 3);
/* 1037 */             break;
/*      */           }
/*      */         } else {
/* 1040 */           ReferenceBinding[] itsInterfaces = anInterface.superInterfaces();
/* 1041 */           if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/* 1042 */             int itsLength = itsInterfaces.length;
/* 1043 */             if (nextPosition + itsLength >= interfacesToVisit.length)
/* 1044 */               System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 1045 */             for (int a = 0; a < itsLength; a++) {
/* 1046 */               ReferenceBinding next = itsInterfaces[a];
/* 1047 */               int b = 0;
/* 1048 */               while (next != interfacesToVisit[b])
/*      */               {
/* 1047 */                 b++; if (b < nextPosition)
/*      */                   continue;
/* 1049 */                 interfacesToVisit[(nextPosition++)] = next;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1054 */       if (ambiguous != null) {
/* 1055 */         return ambiguous;
/*      */       }
/*      */     }
/* 1058 */     if (visibleField != null)
/* 1059 */       return visibleField;
/* 1060 */     if (notVisibleField != null) {
/* 1061 */       return new ProblemFieldBinding(notVisibleField, currentType, fieldName, 2);
/*      */     }
/* 1063 */     return null;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding findMemberType(char[] typeName, ReferenceBinding enclosingType)
/*      */   {
/* 1068 */     if ((enclosingType.tagBits & 0x10000) != 0L) {
/* 1069 */       return null;
/*      */     }
/* 1071 */     ReferenceBinding enclosingSourceType = enclosingSourceType();
/* 1072 */     PackageBinding currentPackage = getCurrentPackage();
/* 1073 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 1074 */     unitScope.recordReference(enclosingType, typeName);
/* 1075 */     ReferenceBinding memberType = enclosingType.getMemberType(typeName);
/* 1076 */     if (memberType != null) {
/* 1077 */       unitScope.recordTypeReference(memberType);
/* 1078 */       if ((enclosingSourceType == null) || ((this.parent == unitScope) && ((enclosingSourceType.tagBits & 0x40000) == 0L)) ? 
/* 1079 */         memberType.canBeSeenBy(currentPackage) : 
/* 1080 */         memberType.canBeSeenBy(enclosingType, enclosingSourceType))
/* 1081 */         return memberType;
/* 1082 */       return new ProblemReferenceBinding(new char[][] { typeName }, memberType, 2);
/*      */     }
/*      */ 
/* 1086 */     ReferenceBinding currentType = enclosingType;
/* 1087 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/* 1088 */     int nextPosition = 0;
/* 1089 */     ReferenceBinding visibleMemberType = null;
/* 1090 */     boolean keepLooking = true;
/* 1091 */     ReferenceBinding notVisible = null;
/*      */ 
/* 1093 */     while (keepLooking) {
/* 1094 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/* 1095 */       if (itsInterfaces == null) {
/* 1096 */         ReferenceBinding sourceType = currentType.isParameterizedType() ? 
/* 1097 */           ((ParameterizedTypeBinding)currentType).genericType() : 
/* 1098 */           currentType;
/* 1099 */         if (sourceType.isHierarchyBeingConnected())
/* 1100 */           return null;
/* 1101 */         ((SourceTypeBinding)sourceType).scope.connectTypeHierarchy();
/* 1102 */         itsInterfaces = currentType.superInterfaces();
/*      */       }
/* 1104 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/* 1105 */         if (interfacesToVisit == null) {
/* 1106 */           interfacesToVisit = itsInterfaces;
/* 1107 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/* 1109 */           int itsLength = itsInterfaces.length;
/* 1110 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/* 1111 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 1112 */           for (int a = 0; a < itsLength; a++) {
/* 1113 */             ReferenceBinding next = itsInterfaces[a];
/* 1114 */             int b = 0;
/* 1115 */             while (next != interfacesToVisit[b])
/*      */             {
/* 1114 */               b++; if (b < nextPosition)
/*      */                 continue;
/* 1116 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/* 1120 */       if ((currentType = currentType.superclass()) == null) {
/*      */         break;
/*      */       }
/* 1123 */       unitScope.recordReference(currentType, typeName);
/* 1124 */       if ((memberType = currentType.getMemberType(typeName)) != null) {
/* 1125 */         unitScope.recordTypeReference(memberType);
/* 1126 */         keepLooking = false;
/* 1127 */         if (enclosingSourceType == null ? 
/* 1128 */           memberType.canBeSeenBy(currentPackage) : 
/* 1129 */           memberType.canBeSeenBy(enclosingType, enclosingSourceType)) {
/* 1130 */           if (visibleMemberType == null)
/* 1131 */             visibleMemberType = memberType;
/*      */           else
/* 1133 */             return new ProblemReferenceBinding(new char[][] { typeName }, visibleMemberType, 3);
/*      */         }
/* 1135 */         else notVisible = memberType;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1140 */     if (interfacesToVisit != null) {
/* 1141 */       ProblemReferenceBinding ambiguous = null;
/* 1142 */       for (int i = 0; i < nextPosition; i++) {
/* 1143 */         ReferenceBinding anInterface = interfacesToVisit[i];
/* 1144 */         unitScope.recordReference(anInterface, typeName);
/* 1145 */         if ((memberType = anInterface.getMemberType(typeName)) != null) {
/* 1146 */           unitScope.recordTypeReference(memberType);
/* 1147 */           if (visibleMemberType == null) {
/* 1148 */             visibleMemberType = memberType;
/*      */           } else {
/* 1150 */             ambiguous = new ProblemReferenceBinding(new char[][] { typeName }, visibleMemberType, 3);
/* 1151 */             break;
/*      */           }
/*      */         } else {
/* 1154 */           ReferenceBinding[] itsInterfaces = anInterface.superInterfaces();
/* 1155 */           if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/* 1156 */             int itsLength = itsInterfaces.length;
/* 1157 */             if (nextPosition + itsLength >= interfacesToVisit.length)
/* 1158 */               System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 1159 */             for (int a = 0; a < itsLength; a++) {
/* 1160 */               ReferenceBinding next = itsInterfaces[a];
/* 1161 */               int b = 0;
/* 1162 */               while (next != interfacesToVisit[b])
/*      */               {
/* 1161 */                 b++; if (b < nextPosition)
/*      */                   continue;
/* 1163 */                 interfacesToVisit[(nextPosition++)] = next;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1168 */       if (ambiguous != null)
/* 1169 */         return ambiguous;
/*      */     }
/* 1171 */     if (visibleMemberType != null)
/* 1172 */       return visibleMemberType;
/* 1173 */     if (notVisible != null)
/* 1174 */       return new ProblemReferenceBinding(new char[][] { typeName }, notVisible, 2);
/* 1175 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding findMethod(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite)
/*      */   {
/* 1180 */     return findMethod(receiverType, selector, argumentTypes, invocationSite, false);
/*      */   }
/*      */ 
/*      */   public MethodBinding findMethod(ReferenceBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite, boolean inStaticContext)
/*      */   {
/* 1185 */     ReferenceBinding currentType = receiverType;
/* 1186 */     boolean receiverTypeIsInterface = receiverType.isInterface();
/* 1187 */     ObjectVector found = new ObjectVector(3);
/* 1188 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 1189 */     unitScope.recordTypeReferences(argumentTypes);
/*      */ 
/* 1191 */     if (receiverTypeIsInterface) {
/* 1192 */       unitScope.recordTypeReference(receiverType);
/* 1193 */       MethodBinding[] receiverMethods = receiverType.getMethods(selector, argumentTypes.length);
/* 1194 */       if (receiverMethods.length > 0)
/* 1195 */         found.addAll(receiverMethods);
/* 1196 */       findMethodInSuperInterfaces(receiverType, selector, found, invocationSite);
/* 1197 */       currentType = getJavaLangObject();
/*      */     }
/*      */ 
/* 1201 */     long complianceLevel = compilerOptions().complianceLevel;
/* 1202 */     boolean isCompliant14 = complianceLevel >= 3145728L;
/* 1203 */     boolean isCompliant15 = complianceLevel >= 3211264L;
/* 1204 */     ReferenceBinding classHierarchyStart = currentType;
/* 1205 */     MethodVerifier verifier = environment().methodVerifier();
/* 1206 */     while (currentType != null) {
/* 1207 */       unitScope.recordTypeReference(currentType);
/* 1208 */       currentType = (ReferenceBinding)currentType.capture(this, invocationSite == null ? 0 : invocationSite.sourceEnd());
/* 1209 */       MethodBinding[] currentMethods = currentType.getMethods(selector, argumentTypes.length);
/* 1210 */       int currentLength = currentMethods.length;
/* 1211 */       if (currentLength > 0) {
/* 1212 */         if ((isCompliant14) && ((receiverTypeIsInterface) || (found.size > 0))) {
/* 1213 */           int i = 0; for (int l = currentLength; i < l; i++) {
/* 1214 */             MethodBinding currentMethod = currentMethods[i];
/* 1215 */             if (currentMethod != null)
/* 1216 */               if ((receiverTypeIsInterface) && (!currentMethod.isPublic())) {
/* 1217 */                 currentLength--;
/* 1218 */                 currentMethods[i] = null;
/*      */               }
/*      */               else
/*      */               {
/* 1227 */                 int j = 0; for (int max = found.size; j < max; j++) {
/* 1228 */                   MethodBinding matchingMethod = (MethodBinding)found.elementAt(j);
/* 1229 */                   MethodBinding matchingOriginal = matchingMethod.original();
/* 1230 */                   MethodBinding currentOriginal = matchingOriginal.findOriginalInheritedMethod(currentMethod);
/* 1231 */                   if ((currentOriginal != null) && (verifier.isParameterSubsignature(matchingOriginal, currentOriginal))) {
/* 1232 */                     if ((isCompliant15) && 
/* 1233 */                       (matchingMethod.isBridge()) && (!currentMethod.isBridge())) {
/*      */                       break;
/*      */                     }
/* 1236 */                     currentLength--;
/* 1237 */                     currentMethods[i] = null;
/* 1238 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/*      */           }
/*      */         }
/* 1244 */         if (currentLength > 0)
/*      */         {
/* 1246 */           if (currentMethods.length == currentLength) {
/* 1247 */             found.addAll(currentMethods);
/*      */           } else {
/* 1249 */             int i = 0; for (int max = currentMethods.length; i < max; i++) {
/* 1250 */               MethodBinding currentMethod = currentMethods[i];
/* 1251 */               if (currentMethod != null)
/* 1252 */                 found.add(currentMethod);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1257 */       currentType = currentType.superclass();
/*      */     }
/*      */ 
/* 1261 */     int foundSize = found.size;
/* 1262 */     MethodBinding[] candidates = (MethodBinding[])null;
/* 1263 */     int candidatesCount = 0;
/* 1264 */     MethodBinding problemMethod = null;
/* 1265 */     boolean searchForDefaultAbstractMethod = (isCompliant14) && (!receiverTypeIsInterface) && ((receiverType.isAbstract()) || (receiverType.isTypeVariable()));
/* 1266 */     if (foundSize > 0)
/*      */     {
/* 1268 */       for (int i = 0; i < foundSize; i++) {
/* 1269 */         MethodBinding methodBinding = (MethodBinding)found.elementAt(i);
/* 1270 */         MethodBinding compatibleMethod = computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
/* 1271 */         if (compatibleMethod != null) {
/* 1272 */           if (compatibleMethod.isValidBinding()) {
/* 1273 */             if ((foundSize == 1) && (compatibleMethod.canBeSeenBy(receiverType, invocationSite, this)))
/*      */             {
/* 1275 */               if (searchForDefaultAbstractMethod)
/* 1276 */                 return findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, compatibleMethod);
/* 1277 */               unitScope.recordTypeReferences(compatibleMethod.thrownExceptions);
/* 1278 */               return compatibleMethod;
/*      */             }
/* 1280 */             if (candidatesCount == 0)
/* 1281 */               candidates = new MethodBinding[foundSize];
/* 1282 */             candidates[(candidatesCount++)] = compatibleMethod;
/* 1283 */           } else if (problemMethod == null) {
/* 1284 */             problemMethod = compatibleMethod;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1291 */     if (candidatesCount == 0) {
/* 1292 */       if (problemMethod != null) {
/* 1293 */         switch (problemMethod.problemId()) {
/*      */         case 11:
/*      */         case 13:
/* 1296 */           return problemMethod;
/*      */         case 12:
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1302 */       MethodBinding interfaceMethod = 
/* 1303 */         findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, null);
/* 1304 */       if (interfaceMethod != null) return interfaceMethod;
/* 1305 */       if (found.size == 0) return null;
/* 1306 */       if (problemMethod != null) return problemMethod;
/*      */ 
/* 1315 */       int bestArgMatches = -1;
/* 1316 */       MethodBinding bestGuess = (MethodBinding)found.elementAt(0);
/* 1317 */       int argLength = argumentTypes.length;
/* 1318 */       foundSize = found.size;
/* 1319 */       for (int i = 0; i < foundSize; i++) {
/* 1320 */         MethodBinding methodBinding = (MethodBinding)found.elementAt(i);
/* 1321 */         TypeBinding[] params = methodBinding.parameters;
/* 1322 */         int paramLength = params.length;
/* 1323 */         int argMatches = 0;
/* 1324 */         for (int a = 0; a < argLength; a++) {
/* 1325 */           TypeBinding arg = argumentTypes[a];
/* 1326 */           for (int p = a == 0 ? 0 : a - 1; (p < paramLength) && (p < a + 1); p++) {
/* 1327 */             if (params[p] == arg) {
/* 1328 */               argMatches++;
/* 1329 */               break;
/*      */             }
/*      */           }
/*      */         }
/* 1333 */         if (argMatches < bestArgMatches)
/*      */           continue;
/* 1335 */         if (argMatches == bestArgMatches) {
/* 1336 */           int diff1 = paramLength < argLength ? 2 * (argLength - paramLength) : paramLength - argLength;
/* 1337 */           int bestLength = bestGuess.parameters.length;
/* 1338 */           int diff2 = bestLength < argLength ? 2 * (argLength - bestLength) : bestLength - argLength;
/* 1339 */           if (diff1 >= diff2)
/*      */             continue;
/*      */         }
/* 1342 */         bestArgMatches = argMatches;
/* 1343 */         bestGuess = methodBinding;
/*      */       }
/* 1345 */       return new ProblemMethodBinding(bestGuess, bestGuess.selector, argumentTypes, 1);
/*      */     }
/*      */ 
/* 1349 */     int visiblesCount = 0;
/* 1350 */     if (receiverTypeIsInterface) {
/* 1351 */       if (candidatesCount == 1) {
/* 1352 */         unitScope.recordTypeReferences(candidates[0].thrownExceptions);
/* 1353 */         return candidates[0];
/*      */       }
/* 1355 */       visiblesCount = candidatesCount;
/*      */     } else {
/* 1357 */       for (int i = 0; i < candidatesCount; i++) {
/* 1358 */         MethodBinding methodBinding = candidates[i];
/* 1359 */         if (methodBinding.canBeSeenBy(receiverType, invocationSite, this)) {
/* 1360 */           if (visiblesCount != i) {
/* 1361 */             candidates[i] = null;
/* 1362 */             candidates[visiblesCount] = methodBinding;
/*      */           }
/* 1364 */           visiblesCount++;
/*      */         }
/*      */       }
/* 1367 */       switch (visiblesCount) {
/*      */       case 0:
/* 1369 */         MethodBinding interfaceMethod = 
/* 1370 */           findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, null);
/* 1371 */         if (interfaceMethod != null) return interfaceMethod;
/* 1372 */         return new ProblemMethodBinding(candidates[0], candidates[0].selector, candidates[0].parameters, 2);
/*      */       case 1:
/* 1374 */         if (searchForDefaultAbstractMethod)
/* 1375 */           return findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, candidates[0]);
/* 1376 */         unitScope.recordTypeReferences(candidates[0].thrownExceptions);
/* 1377 */         return candidates[0];
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1383 */     if (complianceLevel <= 3080192L) {
/* 1384 */       ReferenceBinding declaringClass = candidates[0].declaringClass;
/* 1385 */       return !declaringClass.isInterface() ? 
/* 1386 */         mostSpecificClassMethodBinding(candidates, visiblesCount, invocationSite) : 
/* 1387 */         mostSpecificInterfaceMethodBinding(candidates, visiblesCount, invocationSite);
/*      */     }
/*      */ 
/* 1391 */     if (compilerOptions().sourceLevel >= 3211264L) {
/* 1392 */       for (int i = 0; i < visiblesCount; i++) {
/* 1393 */         MethodBinding candidate = candidates[i];
/* 1394 */         if ((candidate instanceof ParameterizedGenericMethodBinding))
/* 1395 */           candidate = ((ParameterizedGenericMethodBinding)candidate).originalMethod;
/* 1396 */         if ((candidate instanceof ParameterizedMethodBinding)) {
/* 1397 */           for (int j = i + 1; j < visiblesCount; j++) {
/* 1398 */             MethodBinding otherCandidate = candidates[j];
/* 1399 */             if ((otherCandidate == candidate) || (
/* 1400 */               (candidate.declaringClass == otherCandidate.declaringClass) && (candidate.areParametersEqual(otherCandidate)))) {
/* 1401 */               return new ProblemMethodBinding(candidates[i], candidates[i].selector, candidates[i].parameters, 3);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1407 */     if (inStaticContext) {
/* 1408 */       MethodBinding[] staticCandidates = new MethodBinding[visiblesCount];
/* 1409 */       int staticCount = 0;
/* 1410 */       for (int i = 0; i < visiblesCount; i++)
/* 1411 */         if (candidates[i].isStatic())
/* 1412 */           staticCandidates[(staticCount++)] = candidates[i];
/* 1413 */       if (staticCount == 1)
/* 1414 */         return staticCandidates[0];
/* 1415 */       if (staticCount > 1) {
/* 1416 */         return mostSpecificMethodBinding(staticCandidates, staticCount, argumentTypes, invocationSite, receiverType);
/*      */       }
/*      */     }
/* 1419 */     MethodBinding mostSpecificMethod = mostSpecificMethodBinding(candidates, visiblesCount, argumentTypes, invocationSite, receiverType);
/* 1420 */     if (searchForDefaultAbstractMethod) {
/* 1421 */       if (mostSpecificMethod.isValidBinding())
/*      */       {
/* 1423 */         return findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, mostSpecificMethod);
/*      */       }
/* 1425 */       MethodBinding interfaceMethod = findDefaultAbstractMethod(receiverType, selector, argumentTypes, invocationSite, classHierarchyStart, found, null);
/* 1426 */       if ((interfaceMethod != null) && (interfaceMethod.isValidBinding()))
/* 1427 */         return interfaceMethod;
/*      */     }
/* 1429 */     return mostSpecificMethod;
/*      */   }
/*      */ 
/*      */   public MethodBinding findMethodForArray(ArrayBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite)
/*      */   {
/* 1439 */     TypeBinding leafType = receiverType.leafComponentType();
/* 1440 */     if (((leafType instanceof ReferenceBinding)) && 
/* 1441 */       (!((ReferenceBinding)leafType).canBeSeenBy(this))) {
/* 1442 */       return new ProblemMethodBinding(selector, Binding.NO_PARAMETERS, (ReferenceBinding)leafType, 8);
/*      */     }
/*      */ 
/* 1445 */     ReferenceBinding object = getJavaLangObject();
/* 1446 */     MethodBinding methodBinding = object.getExactMethod(selector, argumentTypes, null);
/* 1447 */     if (methodBinding != null)
/*      */     {
/* 1449 */       if (argumentTypes == Binding.NO_PARAMETERS)
/* 1450 */         switch (selector[0]) {
/*      */         case 'c':
/* 1452 */           if (!CharOperation.equals(selector, TypeConstants.CLONE)) break;
/* 1453 */           return environment().computeArrayClone(methodBinding);
/*      */         case 'g':
/* 1457 */           if ((!CharOperation.equals(selector, TypeConstants.GETCLASS)) || (!methodBinding.returnType.isParameterizedType())) break;
/* 1458 */           return ParameterizedMethodBinding.instantiateGetClass(receiverType, methodBinding, this);
/*      */         case 'd':
/*      */         case 'e':
/*      */         case 'f':
/*      */         }
/* 1463 */       if (methodBinding.canBeSeenBy(receiverType, invocationSite, this))
/* 1464 */         return methodBinding;
/*      */     }
/* 1466 */     methodBinding = findMethod(object, selector, argumentTypes, invocationSite);
/* 1467 */     if (methodBinding == null)
/* 1468 */       return new ProblemMethodBinding(selector, argumentTypes, 1);
/* 1469 */     return methodBinding;
/*      */   }
/*      */ 
/*      */   protected void findMethodInSuperInterfaces(ReferenceBinding currentType, char[] selector, ObjectVector found, InvocationSite invocationSite) {
/* 1473 */     ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/* 1474 */     if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/* 1475 */       ReferenceBinding[] interfacesToVisit = itsInterfaces;
/* 1476 */       int nextPosition = interfacesToVisit.length;
/* 1477 */       for (int i = 0; i < nextPosition; i++) {
/* 1478 */         currentType = interfacesToVisit[i];
/* 1479 */         compilationUnitScope().recordTypeReference(currentType);
/* 1480 */         currentType = (ReferenceBinding)currentType.capture(this, invocationSite == null ? 0 : invocationSite.sourceEnd());
/* 1481 */         MethodBinding[] currentMethods = currentType.getMethods(selector);
/* 1482 */         if (currentMethods.length > 0) {
/* 1483 */           int foundSize = found.size;
/* 1484 */           if (foundSize > 0)
/*      */           {
/* 1486 */             int c = 0; for (int l = currentMethods.length; c < l; c++) {
/* 1487 */               MethodBinding current = currentMethods[c];
/* 1488 */               int f = 0;
/* 1489 */               while (current != found.elementAt(f))
/*      */               {
/* 1488 */                 f++; if (f < foundSize)
/*      */                   continue;
/* 1490 */                 found.add(current);
/*      */               }
/*      */             }
/*      */           } else {
/* 1493 */             found.addAll(currentMethods);
/*      */           }
/*      */         }
/* 1496 */         if (((itsInterfaces = currentType.superInterfaces()) != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/* 1497 */           int itsLength = itsInterfaces.length;
/* 1498 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/* 1499 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 1500 */           for (int a = 0; a < itsLength; a++) {
/* 1501 */             ReferenceBinding next = itsInterfaces[a];
/* 1502 */             int b = 0;
/* 1503 */             while (next != interfacesToVisit[b])
/*      */             {
/* 1502 */               b++; if (b < nextPosition)
/*      */                 continue;
/* 1504 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public ReferenceBinding findType(char[] typeName, PackageBinding declarationPackage, PackageBinding invocationPackage)
/*      */   {
/* 1517 */     compilationUnitScope().recordReference(declarationPackage.compoundName, typeName);
/* 1518 */     ReferenceBinding typeBinding = declarationPackage.getType(typeName);
/* 1519 */     if (typeBinding == null) {
/* 1520 */       return null;
/*      */     }
/* 1522 */     if ((typeBinding.isValidBinding()) && 
/* 1523 */       (declarationPackage != invocationPackage) && (!typeBinding.canBeSeenBy(invocationPackage))) {
/* 1524 */       return new ProblemReferenceBinding(new char[][] { typeName }, typeBinding, 2);
/*      */     }
/* 1526 */     return typeBinding;
/*      */   }
/*      */ 
/*      */   public LocalVariableBinding findVariable(char[] variable)
/*      */   {
/* 1531 */     return null;
/*      */   }
/*      */ 
/*      */   public Binding getBinding(char[] name, int mask, InvocationSite invocationSite, boolean needResolve)
/*      */   {
/* 1554 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 1555 */     LookupEnvironment env = unitScope.environment;
/*      */     try {
/* 1557 */       env.missingClassFileLocation = invocationSite;
/* 1558 */       Binding binding = null;
/* 1559 */       FieldBinding problemField = null;
/* 1560 */       if ((mask & 0x3) != 0) {
/* 1561 */         boolean insideStaticContext = false;
/* 1562 */         boolean insideConstructorCall = false;
/* 1563 */         boolean insideTypeAnnotation = false;
/*      */ 
/* 1565 */         FieldBinding foundField = null;
/*      */ 
/* 1567 */         ProblemFieldBinding foundInsideProblem = null;
/*      */ 
/* 1569 */         Scope scope = this;
/* 1570 */         int depth = 0;
/* 1571 */         int foundDepth = 0;
/* 1572 */         ReferenceBinding foundActualReceiverType = null;
/*      */         while (true) {
/* 1574 */           switch (scope.kind) {
/*      */           case 2:
/* 1576 */             MethodScope methodScope = (MethodScope)scope;
/* 1577 */             insideStaticContext |= methodScope.isStatic;
/* 1578 */             insideConstructorCall |= methodScope.isConstructorCall;
/* 1579 */             insideTypeAnnotation = methodScope.insideTypeAnnotation;
/*      */           case 1:
/* 1583 */             LocalVariableBinding variableBinding = scope.findVariable(name);
/*      */ 
/* 1585 */             if (variableBinding == null) break;
/* 1586 */             if ((foundField != null) && (foundField.isValidBinding())) {
/* 1587 */               localObject2 = new ProblemFieldBinding(
/* 1588 */                 foundField, 
/* 1589 */                 foundField.declaringClass, 
/* 1590 */                 name, 
/* 1591 */                 5);
/*      */               return localObject2;
/*      */             }
/*      */ 
/* 1592 */             if (depth > 0)
/* 1593 */               invocationSite.setDepth(depth);
/* 1594 */             localObject2 = variableBinding;
/*      */             return localObject2;
/*      */           case 3:
/* 1598 */             ClassScope classScope = (ClassScope)scope;
/* 1599 */             ReferenceBinding receiverType = classScope.enclosingReceiverType();
/* 1600 */             if (!insideTypeAnnotation) {
/* 1601 */               FieldBinding fieldBinding = classScope.findField(receiverType, name, invocationSite, needResolve);
/*      */ 
/* 1605 */               if (fieldBinding != null) {
/* 1606 */                 if (fieldBinding.problemId() == 3) {
/* 1607 */                   if ((foundField == null) || (foundField.problemId() == 2))
/*      */                   {
/* 1609 */                     localObject2 = fieldBinding;
/*      */                     return localObject2;
/*      */                   }
/* 1611 */                   localObject2 = new ProblemFieldBinding(
/* 1612 */                     foundField, 
/* 1613 */                     foundField.declaringClass, 
/* 1614 */                     name, 
/* 1615 */                     5);
/*      */                   return localObject2;
/*      */                 }
/*      */ 
/* 1618 */                 ProblemFieldBinding insideProblem = null;
/* 1619 */                 if (fieldBinding.isValidBinding()) {
/* 1620 */                   if (!fieldBinding.isStatic()) {
/* 1621 */                     if (insideConstructorCall)
/* 1622 */                       insideProblem = 
/* 1623 */                         new ProblemFieldBinding(
/* 1624 */                         fieldBinding, 
/* 1625 */                         fieldBinding.declaringClass, 
/* 1626 */                         name, 
/* 1627 */                         6);
/* 1628 */                     else if (insideStaticContext) {
/* 1629 */                       insideProblem = 
/* 1630 */                         new ProblemFieldBinding(
/* 1631 */                         fieldBinding, 
/* 1632 */                         fieldBinding.declaringClass, 
/* 1633 */                         name, 
/* 1634 */                         7);
/*      */                     }
/*      */                   }
/* 1637 */                   if ((receiverType == fieldBinding.declaringClass) || (compilerOptions().complianceLevel >= 3145728L))
/*      */                   {
/* 1640 */                     if (foundField == null) {
/* 1641 */                       if (depth > 0) {
/* 1642 */                         invocationSite.setDepth(depth);
/* 1643 */                         invocationSite.setActualReceiverType(receiverType);
/*      */                       }
/*      */ 
/* 1646 */                       localObject2 = insideProblem == null ? fieldBinding : insideProblem;
/*      */                       return localObject2;
/*      */                     }
/* 1648 */                     if (foundField.isValidBinding())
/*      */                     {
/* 1650 */                       if (foundField.declaringClass != fieldBinding.declaringClass)
/*      */                       {
/* 1652 */                         localObject2 = new ProblemFieldBinding(
/* 1653 */                           foundField, 
/* 1654 */                           foundField.declaringClass, 
/* 1655 */                           name, 
/* 1656 */                           5);
/*      */                         return localObject2;
/*      */                       }
/*      */                     }
/*      */ 
/*      */                   }
/*      */ 
/*      */                 }
/*      */ 
/* 1660 */                 if ((foundField == null) || ((foundField.problemId() == 2) && (fieldBinding.problemId() != 2)))
/*      */                 {
/* 1662 */                   foundDepth = depth;
/* 1663 */                   foundActualReceiverType = receiverType;
/* 1664 */                   foundInsideProblem = insideProblem;
/* 1665 */                   foundField = fieldBinding;
/*      */                 }
/*      */               }
/*      */             }
/* 1669 */             insideTypeAnnotation = false;
/* 1670 */             depth++;
/* 1671 */             insideStaticContext |= receiverType.isStatic();
/*      */ 
/* 1675 */             MethodScope enclosingMethodScope = scope.methodScope();
/* 1676 */             insideConstructorCall = enclosingMethodScope == null ? false : enclosingMethodScope.isConstructorCall;
/* 1677 */             break;
/*      */           case 4:
/* 1679 */             break;
/*      */           }
/* 1681 */           scope = scope.parent;
/*      */         }
/*      */ 
/* 1684 */         if (foundInsideProblem != null) {
/* 1685 */           localObject2 = foundInsideProblem;
/*      */           return localObject2;
/* 1686 */         }if (foundField != null) {
/* 1687 */           if (foundField.isValidBinding()) {
/* 1688 */             if (foundDepth > 0) {
/* 1689 */               invocationSite.setDepth(foundDepth);
/* 1690 */               invocationSite.setActualReceiverType(foundActualReceiverType);
/*      */             }
/* 1692 */             localObject2 = foundField;
/*      */             return localObject2;
/*      */           }
/* 1694 */           problemField = foundField;
/* 1695 */           foundField = null;
/*      */         }
/*      */ 
/* 1698 */         if (compilerOptions().sourceLevel >= 3211264L)
/*      */         {
/* 1700 */           unitScope.faultInImports();
/* 1701 */           ImportBinding[] imports = unitScope.imports;
/* 1702 */           if (imports != null)
/*      */           {
/* 1704 */             int i = 0; for (int length = imports.length; i < length; i++) {
/* 1705 */               ImportBinding importBinding = imports[i];
/* 1706 */               if ((!importBinding.isStatic()) || (importBinding.onDemand) || 
/* 1707 */                 (!CharOperation.equals(importBinding.compoundName[(importBinding.compoundName.length - 1)], name)) || 
/* 1708 */                 (unitScope.resolveSingleImport(importBinding, 13) == null) || (!(importBinding.resolvedImport instanceof FieldBinding))) continue;
/* 1709 */               foundField = (FieldBinding)importBinding.resolvedImport;
/* 1710 */               ImportReference importReference = importBinding.reference;
/* 1711 */               if ((importReference != null) && (needResolve)) {
/* 1712 */                 importReference.bits |= 2;
/*      */               }
/* 1714 */               invocationSite.setActualReceiverType(foundField.declaringClass);
/* 1715 */               if (foundField.isValidBinding()) {
/* 1716 */                 localObject2 = foundField;
/*      */                 return localObject2;
/*      */               }
/* 1718 */               if (problemField == null) {
/* 1719 */                 problemField = foundField;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 1725 */             boolean foundInImport = false;
/* 1726 */             int i = 0; for (int length = imports.length; i < length; i++) {
/* 1727 */               ImportBinding importBinding = imports[i];
/* 1728 */               if ((importBinding.isStatic()) && (importBinding.onDemand)) {
/* 1729 */                 Binding resolvedImport = importBinding.resolvedImport;
/* 1730 */                 if ((resolvedImport instanceof ReferenceBinding)) {
/* 1731 */                   FieldBinding temp = findField((ReferenceBinding)resolvedImport, name, invocationSite, needResolve);
/* 1732 */                   if (temp != null) {
/* 1733 */                     if (!temp.isValidBinding()) {
/* 1734 */                       if (problemField == null)
/* 1735 */                         problemField = temp; 
/*      */                     } else {
/* 1736 */                       if ((!temp.isStatic()) || 
/* 1737 */                         (foundField == temp)) continue;
/* 1738 */                       ImportReference importReference = importBinding.reference;
/* 1739 */                       if ((importReference != null) && (needResolve)) {
/* 1740 */                         importReference.bits |= 2;
/*      */                       }
/* 1742 */                       if (foundInImport)
/*      */                       {
/* 1744 */                         localObject2 = new ProblemFieldBinding(
/* 1745 */                           foundField, 
/* 1746 */                           foundField.declaringClass, 
/* 1747 */                           name, 
/* 1748 */                           3);
/*      */                         return localObject2;
/*      */                       }
/*      */ 
/* 1749 */                       foundField = temp;
/* 1750 */                       foundInImport = true;
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 1756 */             if (foundField != null) {
/* 1757 */               invocationSite.setActualReceiverType(foundField.declaringClass);
/* 1758 */               localObject2 = foundField;
/*      */               return localObject2;
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1765 */       if ((mask & 0x4) != 0) {
/* 1766 */         if ((binding = getBaseType(name)) != null) {
/* 1767 */           localObject2 = binding;
/*      */           return localObject2;
/* 1768 */         }binding = getTypeOrPackage(name, (mask & 0x10) == 0 ? 4 : 20, needResolve);
/* 1769 */         if ((binding.isValidBinding()) || (mask == 4)) {
/* 1770 */           localObject2 = binding;
/*      */           return localObject2;
/*      */         }
/* 1772 */       } else if ((mask & 0x10) != 0) {
/* 1773 */         unitScope.recordSimpleReference(name);
/* 1774 */         if ((binding = env.getTopLevelPackage(name)) != null) {
/* 1775 */           localObject2 = binding;
/*      */           return localObject2;
/*      */         }
/*      */       }
/* 1777 */       if (problemField != null) { localObject2 = problemField;
/*      */         return localObject2;
/*      */       }
/* 1778 */       if ((binding != null) && (binding.problemId() != 1)) {
/* 1779 */         localObject2 = binding;
/*      */         return localObject2;
/* 1780 */       }Object localObject2 = new ProblemBinding(name, enclosingSourceType(), 1);
/*      */       return localObject2;
/*      */     } catch (AbortCompilation e) {
/* 1782 */       e.updateContext(invocationSite, referenceCompilationUnit().compilationResult);
/* 1783 */       throw e;
/*      */     } finally {
/* 1785 */       env.missingClassFileLocation = null;
/* 1786 */     }throw localObject1;
/*      */   }
/*      */ 
/*      */   public MethodBinding getConstructor(ReferenceBinding receiverType, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
/* 1790 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 1791 */     LookupEnvironment env = unitScope.environment;
/*      */     try {
/* 1793 */       env.missingClassFileLocation = invocationSite;
/* 1794 */       unitScope.recordTypeReference(receiverType);
/* 1795 */       unitScope.recordTypeReferences(argumentTypes);
/* 1796 */       MethodBinding methodBinding = receiverType.getExactConstructor(argumentTypes);
/* 1797 */       if ((methodBinding != null) && (methodBinding.canBeSeenBy(invocationSite, this)))
/*      */       {
/* 1799 */         if (invocationSite.genericTypeArguments() != null)
/* 1800 */           methodBinding = computeCompatibleMethod(methodBinding, argumentTypes, invocationSite);
/* 1801 */         localObject2 = methodBinding;
/*      */         return localObject2;
/*      */       }
/* 1803 */       MethodBinding[] methods = receiverType.getMethods(TypeConstants.INIT, argumentTypes.length);
/* 1804 */       if (methods == Binding.NO_METHODS) {
/* 1805 */         localObject2 = new ProblemMethodBinding(
/* 1806 */           TypeConstants.INIT, 
/* 1807 */           argumentTypes, 
/* 1808 */           1);
/*      */         return localObject2;
/*      */       }
/*      */ 
/* 1810 */       MethodBinding[] compatible = new MethodBinding[methods.length];
/* 1811 */       int compatibleIndex = 0;
/* 1812 */       MethodBinding problemMethod = null;
/* 1813 */       int i = 0; for (int length = methods.length; i < length; i++) {
/* 1814 */         MethodBinding compatibleMethod = computeCompatibleMethod(methods[i], argumentTypes, invocationSite);
/* 1815 */         if (compatibleMethod != null) {
/* 1816 */           if (compatibleMethod.isValidBinding())
/* 1817 */             compatible[(compatibleIndex++)] = compatibleMethod;
/* 1818 */           else if (problemMethod == null)
/* 1819 */             problemMethod = compatibleMethod;
/*      */         }
/*      */       }
/* 1822 */       if (compatibleIndex == 0) {
/* 1823 */         if (problemMethod == null) {
/* 1824 */           localObject2 = new ProblemMethodBinding(methods[0], TypeConstants.INIT, argumentTypes, 1);
/*      */           return localObject2;
/* 1825 */         }localObject2 = problemMethod;
/*      */         return localObject2;
/*      */       }
/*      */ 
/* 1829 */       MethodBinding[] visible = new MethodBinding[compatibleIndex];
/* 1830 */       int visibleIndex = 0;
/* 1831 */       for (int i = 0; i < compatibleIndex; i++) {
/* 1832 */         MethodBinding method = compatible[i];
/* 1833 */         if (method.canBeSeenBy(invocationSite, this))
/* 1834 */           visible[(visibleIndex++)] = method;
/*      */       }
/* 1836 */       if (visibleIndex == 1) { localObject2 = visible[0];
/*      */         return localObject2;
/*      */       }
/* 1837 */       if (visibleIndex == 0) {
/* 1838 */         localObject2 = new ProblemMethodBinding(
/* 1839 */           compatible[0], 
/* 1840 */           TypeConstants.INIT, 
/* 1841 */           compatible[0].parameters, 
/* 1842 */           2);
/*      */         return localObject2;
/*      */       }
/*      */ 
/* 1844 */       Object localObject2 = mostSpecificMethodBinding(visible, visibleIndex, argumentTypes, invocationSite, receiverType);
/*      */       return localObject2;
/*      */     } catch (AbortCompilation e) {
/* 1846 */       e.updateContext(invocationSite, referenceCompilationUnit().compilationResult);
/* 1847 */       throw e;
/*      */     } finally {
/* 1849 */       env.missingClassFileLocation = null;
/* 1850 */     }throw localObject1;
/*      */   }
/*      */ 
/*      */   public final PackageBinding getCurrentPackage() {
/* 1854 */     Scope unitScope = this;
/*      */     Scope scope;
/* 1855 */     while ((scope = unitScope.parent) != null)
/*      */     {
/*      */       Scope scope;
/* 1856 */       unitScope = scope;
/* 1857 */     }return ((CompilationUnitScope)unitScope).fPackage;
/*      */   }
/*      */ 
/*      */   public int getDeclarationModifiers()
/*      */   {
/* 1865 */     switch (this.kind) {
/*      */     case 1:
/*      */     case 2:
/* 1868 */       MethodScope methodScope = methodScope();
/* 1869 */       if (!methodScope.isInsideInitializer())
/*      */       {
/* 1871 */         MethodBinding context = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
/* 1872 */         if (context == null) break;
/* 1873 */         return context.modifiers;
/*      */       } else {
/* 1875 */         SourceTypeBinding type = ((BlockScope)this).referenceType().binding;
/*      */ 
/* 1878 */         if (methodScope.initializedField != null)
/* 1879 */           return methodScope.initializedField.modifiers;
/* 1880 */         if (type == null) break;
/* 1881 */         return type.modifiers;
/*      */       }
/*      */ 
/*      */     case 3:
/* 1885 */       ReferenceBinding context = ((ClassScope)this).referenceType().binding;
/* 1886 */       if (context == null) break;
/* 1887 */       return context.modifiers;
/*      */     }
/*      */ 
/* 1890 */     return -1;
/*      */   }
/*      */ 
/*      */   public FieldBinding getField(TypeBinding receiverType, char[] fieldName, InvocationSite invocationSite) {
/* 1894 */     LookupEnvironment env = environment();
/*      */     try {
/* 1896 */       env.missingClassFileLocation = invocationSite;
/* 1897 */       FieldBinding field = findField(receiverType, fieldName, invocationSite, true);
/* 1898 */       if (field != null) { localObject2 = field;
/*      */         return localObject2;
/*      */       }
/* 1900 */       Object localObject2 = new ProblemFieldBinding(
/* 1901 */         (receiverType instanceof ReferenceBinding) ? (ReferenceBinding)receiverType : null, 
/* 1902 */         fieldName, 
/* 1903 */         1);
/*      */       return localObject2;
/*      */     }
/*      */     catch (AbortCompilation e)
/*      */     {
/* 1905 */       e.updateContext(invocationSite, referenceCompilationUnit().compilationResult);
/* 1906 */       throw e;
/*      */     } finally {
/* 1908 */       env.missingClassFileLocation = null;
/* 1909 */     }throw localObject1;
/*      */   }
/*      */ 
/*      */   public MethodBinding getImplicitMethod(char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite)
/*      */   {
/* 1927 */     boolean insideStaticContext = false;
/* 1928 */     boolean insideConstructorCall = false;
/* 1929 */     boolean insideTypeAnnotation = false;
/* 1930 */     MethodBinding foundMethod = null;
/* 1931 */     MethodBinding foundProblem = null;
/* 1932 */     boolean foundProblemVisible = false;
/* 1933 */     Scope scope = this;
/* 1934 */     int depth = 0;
/*      */     CompilerOptions options;
/* 1937 */     boolean inheritedHasPrecedence = (options = compilerOptions()).complianceLevel >= 3145728L;
/*      */     while (true)
/*      */     {
/* 1940 */       switch (scope.kind) {
/*      */       case 2:
/* 1942 */         MethodScope methodScope = (MethodScope)scope;
/* 1943 */         insideStaticContext |= methodScope.isStatic;
/* 1944 */         insideConstructorCall |= methodScope.isConstructorCall;
/* 1945 */         insideTypeAnnotation = methodScope.insideTypeAnnotation;
/* 1946 */         break;
/*      */       case 3:
/* 1948 */         ClassScope classScope = (ClassScope)scope;
/* 1949 */         ReferenceBinding receiverType = classScope.enclosingReceiverType();
/* 1950 */         if (!insideTypeAnnotation)
/*      */         {
/* 1953 */           MethodBinding methodBinding = classScope.findExactMethod(receiverType, selector, argumentTypes, invocationSite);
/* 1954 */           if (methodBinding == null)
/* 1955 */             methodBinding = classScope.findMethod(receiverType, selector, argumentTypes, invocationSite);
/* 1956 */           if (methodBinding != null) {
/* 1957 */             if (foundMethod == null) {
/* 1958 */               if (methodBinding.isValidBinding()) {
/* 1959 */                 if ((!methodBinding.isStatic()) && ((insideConstructorCall) || (insideStaticContext))) {
/* 1960 */                   if ((foundProblem != null) && (foundProblem.problemId() != 2))
/* 1961 */                     return foundProblem;
/* 1962 */                   return new ProblemMethodBinding(
/* 1963 */                     methodBinding, 
/* 1964 */                     methodBinding.selector, 
/* 1965 */                     methodBinding.parameters, 
/* 1966 */                     insideConstructorCall ? 
/* 1967 */                     6 : 
/* 1968 */                     7);
/*      */                 }
/* 1970 */                 if ((inheritedHasPrecedence) || 
/* 1971 */                   (receiverType == methodBinding.declaringClass) || 
/* 1972 */                   (receiverType.getMethods(selector) != Binding.NO_METHODS))
/*      */                 {
/* 1977 */                   if (foundProblemVisible) {
/* 1978 */                     return foundProblem;
/*      */                   }
/* 1980 */                   if (depth > 0) {
/* 1981 */                     invocationSite.setDepth(depth);
/* 1982 */                     invocationSite.setActualReceiverType(receiverType);
/*      */                   }
/*      */ 
/* 1985 */                   if ((argumentTypes == Binding.NO_PARAMETERS) && 
/* 1986 */                     (CharOperation.equals(selector, TypeConstants.GETCLASS)) && 
/* 1987 */                     (methodBinding.returnType.isParameterizedType())) {
/* 1988 */                     return ParameterizedMethodBinding.instantiateGetClass(receiverType, methodBinding, this);
/*      */                   }
/* 1990 */                   return methodBinding;
/*      */                 }
/*      */ 
/* 1993 */                 if ((foundProblem == null) || (foundProblem.problemId() == 2)) {
/* 1994 */                   if (foundProblem != null) foundProblem = null;
/*      */ 
/* 1997 */                   if (depth > 0) {
/* 1998 */                     invocationSite.setDepth(depth);
/* 1999 */                     invocationSite.setActualReceiverType(receiverType);
/*      */                   }
/* 2001 */                   foundMethod = methodBinding;
/*      */                 }
/*      */               } else {
/* 2004 */                 if ((methodBinding.problemId() != 2) && (methodBinding.problemId() != 1))
/* 2005 */                   return methodBinding;
/* 2006 */                 if (foundProblem == null) {
/* 2007 */                   foundProblem = methodBinding;
/*      */                 }
/* 2009 */                 if ((!foundProblemVisible) && (methodBinding.problemId() == 1)) {
/* 2010 */                   MethodBinding closestMatch = ((ProblemMethodBinding)methodBinding).closestMatch;
/* 2011 */                   if ((closestMatch != null) && (closestMatch.canBeSeenBy(receiverType, invocationSite, this))) {
/* 2012 */                     foundProblem = methodBinding;
/* 2013 */                     foundProblemVisible = true;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 2018 */             else if ((methodBinding.problemId() == 3) || (
/* 2019 */               (foundMethod.declaringClass != methodBinding.declaringClass) && (
/* 2020 */               (receiverType == methodBinding.declaringClass) || (receiverType.getMethods(selector) != Binding.NO_METHODS))))
/*      */             {
/* 2024 */               return new ProblemMethodBinding(
/* 2025 */                 methodBinding, 
/* 2026 */                 selector, 
/* 2027 */                 argumentTypes, 
/* 2028 */                 5);
/*      */             }
/*      */           }
/*      */         }
/* 2032 */         insideTypeAnnotation = false;
/* 2033 */         depth++;
/* 2034 */         insideStaticContext |= receiverType.isStatic();
/*      */ 
/* 2038 */         MethodScope enclosingMethodScope = scope.methodScope();
/* 2039 */         insideConstructorCall = enclosingMethodScope == null ? false : enclosingMethodScope.isConstructorCall;
/* 2040 */         break;
/*      */       case 4:
/* 2042 */         break;
/*      */       }
/* 2044 */       scope = scope.parent;
/*      */     }
/*      */ 
/* 2047 */     if ((insideStaticContext) && (options.sourceLevel >= 3211264L)) {
/* 2048 */       if (foundProblem != null) {
/* 2049 */         if ((foundProblem.declaringClass != null) && (foundProblem.declaringClass.id == 1))
/* 2050 */           return foundProblem;
/* 2051 */         if ((foundProblem.problemId() == 1) && (foundProblemVisible)) {
/* 2052 */           return foundProblem;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2057 */       CompilationUnitScope unitScope = (CompilationUnitScope)scope;
/* 2058 */       unitScope.faultInImports();
/* 2059 */       ImportBinding[] imports = unitScope.imports;
/* 2060 */       if (imports != null) {
/* 2061 */         ObjectVector visible = null;
/* 2062 */         boolean skipOnDemand = false;
/* 2063 */         int i = 0; for (int length = imports.length; i < length; i++) {
/* 2064 */           ImportBinding importBinding = imports[i];
/* 2065 */           if (importBinding.isStatic()) {
/* 2066 */             Binding resolvedImport = importBinding.resolvedImport;
/* 2067 */             MethodBinding possible = null;
/* 2068 */             if (importBinding.onDemand) {
/* 2069 */               if ((!skipOnDemand) && ((resolvedImport instanceof ReferenceBinding)))
/*      */               {
/* 2071 */                 possible = findMethod((ReferenceBinding)resolvedImport, selector, argumentTypes, invocationSite, true);
/*      */               }
/* 2073 */             } else if ((resolvedImport instanceof MethodBinding)) {
/* 2074 */               MethodBinding staticMethod = (MethodBinding)resolvedImport;
/* 2075 */               if (CharOperation.equals(staticMethod.selector, selector))
/*      */               {
/* 2077 */                 possible = findMethod(staticMethod.declaringClass, selector, argumentTypes, invocationSite, true);
/*      */               }
/* 2078 */             } else if ((resolvedImport instanceof FieldBinding))
/*      */             {
/* 2080 */               FieldBinding staticField = (FieldBinding)resolvedImport;
/* 2081 */               if (CharOperation.equals(staticField.name, selector))
/*      */               {
/* 2083 */                 char[][] importName = importBinding.reference.tokens;
/* 2084 */                 TypeBinding referencedType = getType(importName, importName.length - 1);
/* 2085 */                 if (referencedType != null)
/*      */                 {
/* 2087 */                   possible = findMethod((ReferenceBinding)referencedType, selector, argumentTypes, invocationSite, true);
/*      */                 }
/*      */               }
/*      */             }
/* 2091 */             if ((possible != null) && (possible != foundProblem)) {
/* 2092 */               if (!possible.isValidBinding()) {
/* 2093 */                 if (foundProblem == null)
/* 2094 */                   foundProblem = possible;
/* 2095 */               } else if (possible.isStatic()) {
/* 2096 */                 MethodBinding compatibleMethod = computeCompatibleMethod(possible, argumentTypes, invocationSite);
/* 2097 */                 if (compatibleMethod != null) {
/* 2098 */                   if (compatibleMethod.isValidBinding()) {
/* 2099 */                     if (compatibleMethod.canBeSeenBy(unitScope.fPackage)) {
/* 2100 */                       if ((visible == null) || (!visible.contains(compatibleMethod))) {
/* 2101 */                         ImportReference importReference = importBinding.reference;
/* 2102 */                         if (importReference != null) {
/* 2103 */                           importReference.bits |= 2;
/*      */                         }
/* 2105 */                         if ((!skipOnDemand) && (!importBinding.onDemand)) {
/* 2106 */                           visible = null;
/* 2107 */                           skipOnDemand = true;
/*      */                         }
/* 2109 */                         if (visible == null)
/* 2110 */                           visible = new ObjectVector(3);
/* 2111 */                         visible.add(compatibleMethod);
/*      */                       }
/* 2113 */                     } else if (foundProblem == null)
/* 2114 */                       foundProblem = new ProblemMethodBinding(compatibleMethod, selector, compatibleMethod.parameters, 2);
/*      */                   }
/* 2116 */                   else if (foundProblem == null)
/* 2117 */                     foundProblem = compatibleMethod;
/*      */                 }
/* 2119 */                 else if (foundProblem == null) {
/* 2120 */                   foundProblem = new ProblemMethodBinding(possible, selector, argumentTypes, 1);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2126 */         if (visible != null) {
/* 2127 */           MethodBinding[] temp = new MethodBinding[visible.size];
/* 2128 */           visible.copyInto(temp);
/* 2129 */           foundMethod = mostSpecificMethodBinding(temp, temp.length, argumentTypes, invocationSite, null);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2134 */     if (foundMethod != null) {
/* 2135 */       invocationSite.setActualReceiverType(foundMethod.declaringClass);
/* 2136 */       return foundMethod;
/*      */     }
/* 2138 */     if (foundProblem != null) {
/* 2139 */       return foundProblem;
/*      */     }
/* 2141 */     return new ProblemMethodBinding(selector, argumentTypes, 1);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaIoSerializable() {
/* 2145 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2146 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_IO_SERIALIZABLE);
/* 2147 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_IO_SERIALIZABLE, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaLangAnnotationAnnotation() {
/* 2151 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2152 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION);
/* 2153 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ANNOTATION_ANNOTATION, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaLangAssertionError() {
/* 2157 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2158 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ASSERTIONERROR);
/* 2159 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ASSERTIONERROR, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaLangClass() {
/* 2163 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2164 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CLASS);
/* 2165 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_CLASS, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaLangCloneable() {
/* 2169 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2170 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_CLONEABLE);
/* 2171 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_CLONEABLE, this);
/*      */   }
/*      */   public final ReferenceBinding getJavaLangEnum() {
/* 2174 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2175 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ENUM);
/* 2176 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ENUM, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaLangIterable() {
/* 2180 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2181 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_ITERABLE);
/* 2182 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_ITERABLE, this);
/*      */   }
/*      */   public final ReferenceBinding getJavaLangObject() {
/* 2185 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2186 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_OBJECT);
/* 2187 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaLangString() {
/* 2191 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2192 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_STRING);
/* 2193 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_STRING, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getJavaLangThrowable() {
/* 2197 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2198 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_LANG_THROWABLE);
/* 2199 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_LANG_THROWABLE, this);
/*      */   }
/*      */   public final ReferenceBinding getJavaUtilIterator() {
/* 2202 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2203 */     unitScope.recordQualifiedReference(TypeConstants.JAVA_UTIL_ITERATOR);
/* 2204 */     return unitScope.environment.getResolvedType(TypeConstants.JAVA_UTIL_ITERATOR, this);
/*      */   }
/*      */ 
/*      */   public final ReferenceBinding getMemberType(char[] typeName, ReferenceBinding enclosingType)
/*      */   {
/* 2210 */     ReferenceBinding memberType = findMemberType(typeName, enclosingType);
/* 2211 */     if (memberType != null) return memberType;
/* 2212 */     char[][] compoundName = { typeName };
/* 2213 */     return new ProblemReferenceBinding(compoundName, null, 1);
/*      */   }
/*      */ 
/*      */   public MethodBinding getMethod(TypeBinding receiverType, char[] selector, TypeBinding[] argumentTypes, InvocationSite invocationSite) {
/* 2217 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2218 */     LookupEnvironment env = unitScope.environment;
/*      */     try {
/* 2220 */       env.missingClassFileLocation = invocationSite;
/* 2221 */       switch (receiverType.kind()) {
/*      */       case 132:
/* 2223 */         localObject2 = new ProblemMethodBinding(selector, argumentTypes, 1);
/*      */         return localObject2;
/*      */       case 68:
/* 2225 */         unitScope.recordTypeReference(receiverType);
/* 2226 */         localObject2 = findMethodForArray((ArrayBinding)receiverType, selector, argumentTypes, invocationSite);
/*      */         return localObject2;
/*      */       }
/* 2228 */       unitScope.recordTypeReference(receiverType);
/*      */ 
/* 2230 */       ReferenceBinding currentType = (ReferenceBinding)receiverType;
/* 2231 */       if (!currentType.canBeSeenBy(this)) {
/* 2232 */         localObject2 = new ProblemMethodBinding(selector, argumentTypes, 8);
/*      */         return localObject2;
/*      */       }
/*      */ 
/* 2235 */       MethodBinding methodBinding = findExactMethod(currentType, selector, argumentTypes, invocationSite);
/* 2236 */       if (methodBinding != null) { localObject2 = methodBinding;
/*      */         return localObject2;
/*      */       }
/* 2238 */       methodBinding = findMethod(currentType, selector, argumentTypes, invocationSite);
/* 2239 */       if (methodBinding == null) {
/* 2240 */         localObject2 = new ProblemMethodBinding(selector, argumentTypes, 1);
/*      */         return localObject2;
/* 2241 */       }if (!methodBinding.isValidBinding()) {
/* 2242 */         localObject2 = methodBinding;
/*      */         return localObject2;
/*      */       }
/*      */ 
/* 2245 */       if ((argumentTypes == Binding.NO_PARAMETERS) && 
/* 2246 */         (CharOperation.equals(selector, TypeConstants.GETCLASS)) && 
/* 2247 */         (methodBinding.returnType.isParameterizedType())) {
/* 2248 */         localObject2 = ParameterizedMethodBinding.instantiateGetClass(receiverType, methodBinding, this);
/*      */         return localObject2;
/*      */       }
/* 2250 */       Object localObject2 = methodBinding;
/*      */       return localObject2;
/*      */     } catch (AbortCompilation e) {
/* 2252 */       e.updateContext(invocationSite, referenceCompilationUnit().compilationResult);
/* 2253 */       throw e;
/*      */     } finally {
/* 2255 */       env.missingClassFileLocation = null;
/* 2256 */     }throw localObject1;
/*      */   }
/*      */ 
/*      */   public final Binding getPackage(char[][] compoundName)
/*      */   {
/* 2266 */     compilationUnitScope().recordQualifiedReference(compoundName);
/* 2267 */     Binding binding = getTypeOrPackage(compoundName[0], 20, true);
/* 2268 */     if (binding == null) {
/* 2269 */       char[][] qName = { compoundName[0] };
/* 2270 */       return new ProblemReferenceBinding(qName, environment().createMissingType(null, compoundName), 1);
/*      */     }
/* 2272 */     if (!binding.isValidBinding()) {
/* 2273 */       if ((binding instanceof PackageBinding)) {
/* 2274 */         char[][] qName = { compoundName[0] };
/* 2275 */         return new ProblemReferenceBinding(qName, null, 1);
/*      */       }
/* 2277 */       return binding;
/*      */     }
/* 2279 */     if (!(binding instanceof PackageBinding)) return null;
/*      */ 
/* 2281 */     int currentIndex = 1; int length = compoundName.length;
/* 2282 */     PackageBinding packageBinding = (PackageBinding)binding;
/* 2283 */     while (currentIndex < length) {
/* 2284 */       binding = packageBinding.getTypeOrPackage(compoundName[(currentIndex++)]);
/* 2285 */       if (binding == null) {
/* 2286 */         return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, currentIndex), null, 1);
/*      */       }
/* 2288 */       if (!binding.isValidBinding())
/* 2289 */         return new ProblemReferenceBinding(
/* 2290 */           CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2291 */           (binding instanceof ReferenceBinding) ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, 
/* 2292 */           binding.problemId());
/* 2293 */       if (!(binding instanceof PackageBinding))
/* 2294 */         return packageBinding;
/* 2295 */       packageBinding = (PackageBinding)binding;
/*      */     }
/* 2297 */     return new ProblemReferenceBinding(compoundName, null, 1);
/*      */   }
/*      */ 
/*      */   public final TypeBinding getType(char[] name)
/*      */   {
/* 2308 */     TypeBinding binding = getBaseType(name);
/* 2309 */     if (binding != null) return binding;
/* 2310 */     return (ReferenceBinding)getTypeOrPackage(name, 4, true);
/*      */   }
/*      */ 
/*      */   public final TypeBinding getType(char[] name, PackageBinding packageBinding)
/*      */   {
/* 2318 */     if (packageBinding == null) {
/* 2319 */       return getType(name);
/*      */     }
/* 2321 */     Binding binding = packageBinding.getTypeOrPackage(name);
/* 2322 */     if (binding == null) {
/* 2323 */       return new ProblemReferenceBinding(
/* 2324 */         CharOperation.arrayConcat(packageBinding.compoundName, name), 
/* 2325 */         null, 
/* 2326 */         1);
/*      */     }
/* 2328 */     if (!binding.isValidBinding()) {
/* 2329 */       return new ProblemReferenceBinding(
/* 2330 */         (binding instanceof ReferenceBinding) ? ((ReferenceBinding)binding).compoundName : CharOperation.arrayConcat(packageBinding.compoundName, name), 
/* 2331 */         (binding instanceof ReferenceBinding) ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, 
/* 2332 */         binding.problemId());
/*      */     }
/* 2334 */     ReferenceBinding typeBinding = (ReferenceBinding)binding;
/* 2335 */     if (!typeBinding.canBeSeenBy(this))
/* 2336 */       return new ProblemReferenceBinding(
/* 2337 */         typeBinding.compoundName, 
/* 2338 */         typeBinding, 
/* 2339 */         2);
/* 2340 */     return typeBinding;
/*      */   }
/*      */ 
/*      */   public final TypeBinding getType(char[][] compoundName, int typeNameLength)
/*      */   {
/* 2349 */     if (typeNameLength == 1)
/*      */     {
/* 2351 */       TypeBinding binding = getBaseType(compoundName[0]);
/* 2352 */       if (binding != null) return binding;
/*      */     }
/*      */ 
/* 2355 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 2356 */     unitScope.recordQualifiedReference(compoundName);
/* 2357 */     Binding binding = getTypeOrPackage(compoundName[0], typeNameLength == 1 ? 4 : 20, true);
/* 2358 */     if (binding == null) {
/* 2359 */       char[][] qName = { compoundName[0] };
/* 2360 */       return new ProblemReferenceBinding(qName, environment().createMissingType(compilationUnitScope().getCurrentPackage(), qName), 1);
/*      */     }
/* 2362 */     if (!binding.isValidBinding()) {
/* 2363 */       if ((binding instanceof PackageBinding)) {
/* 2364 */         char[][] qName = { compoundName[0] };
/* 2365 */         return new ProblemReferenceBinding(
/* 2366 */           qName, 
/* 2367 */           environment().createMissingType(null, qName), 
/* 2368 */           1);
/*      */       }
/* 2370 */       return (ReferenceBinding)binding;
/*      */     }
/* 2372 */     int currentIndex = 1;
/* 2373 */     boolean checkVisibility = false;
/* 2374 */     if ((binding instanceof PackageBinding)) {
/* 2375 */       PackageBinding packageBinding = (PackageBinding)binding;
/* 2376 */       while (currentIndex < typeNameLength) {
/* 2377 */         binding = packageBinding.getTypeOrPackage(compoundName[(currentIndex++)]);
/* 2378 */         if (binding == null) {
/* 2379 */           char[][] qName = CharOperation.subarray(compoundName, 0, currentIndex);
/* 2380 */           return new ProblemReferenceBinding(
/* 2381 */             qName, 
/* 2382 */             environment().createMissingType(packageBinding, qName), 
/* 2383 */             1);
/*      */         }
/* 2385 */         if (!binding.isValidBinding())
/* 2386 */           return new ProblemReferenceBinding(
/* 2387 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2388 */             (binding instanceof ReferenceBinding) ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, 
/* 2389 */             binding.problemId());
/* 2390 */         if (!(binding instanceof PackageBinding))
/*      */           break;
/* 2392 */         packageBinding = (PackageBinding)binding;
/*      */       }
/* 2394 */       if ((binding instanceof PackageBinding)) {
/* 2395 */         char[][] qName = CharOperation.subarray(compoundName, 0, currentIndex);
/* 2396 */         return new ProblemReferenceBinding(
/* 2397 */           qName, 
/* 2398 */           environment().createMissingType(null, qName), 
/* 2399 */           1);
/*      */       }
/* 2401 */       checkVisibility = true;
/*      */     }
/*      */ 
/* 2405 */     ReferenceBinding typeBinding = (ReferenceBinding)binding;
/* 2406 */     unitScope.recordTypeReference(typeBinding);
/* 2407 */     if ((checkVisibility) && 
/* 2408 */       (!typeBinding.canBeSeenBy(this)))
/* 2409 */       return new ProblemReferenceBinding(
/* 2410 */         CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2411 */         typeBinding, 
/* 2412 */         2);
/*      */     do
/*      */     {
/* 2415 */       typeBinding = getMemberType(compoundName[(currentIndex++)], typeBinding);
/* 2416 */       if (!typeBinding.isValidBinding()) {
/* 2417 */         if ((typeBinding instanceof ProblemReferenceBinding)) {
/* 2418 */           ProblemReferenceBinding problemBinding = (ProblemReferenceBinding)typeBinding;
/* 2419 */           return new ProblemReferenceBinding(
/* 2420 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2421 */             problemBinding.closestReferenceMatch(), 
/* 2422 */             typeBinding.problemId());
/*      */         }
/* 2424 */         return new ProblemReferenceBinding(
/* 2425 */           CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2426 */           (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), 
/* 2427 */           typeBinding.problemId());
/*      */       }
/*      */     }
/* 2414 */     while (currentIndex < typeNameLength);
/*      */ 
/* 2430 */     return typeBinding;
/*      */   }
/*      */ 
/*      */   final Binding getTypeOrPackage(char[] name, int mask, boolean needResolve)
/*      */   {
/* 2436 */     Scope scope = this;
/* 2437 */     ReferenceBinding foundType = null;
/* 2438 */     boolean insideStaticContext = false;
/* 2439 */     boolean insideTypeAnnotation = false;
/* 2440 */     if ((mask & 0x4) == 0) {
/* 2441 */       Scope next = scope;
/* 2442 */       while ((next = scope.parent) != null)
/* 2443 */         scope = next;
/*      */     } else {
/* 2445 */       boolean inheritedHasPrecedence = compilerOptions().complianceLevel >= 3145728L;
/*      */       while (true) {
/* 2447 */         switch (scope.kind) {
/*      */         case 2:
/* 2449 */           MethodScope methodScope = (MethodScope)scope;
/* 2450 */           AbstractMethodDeclaration methodDecl = methodScope.referenceMethod();
/* 2451 */           if (methodDecl != null) {
/* 2452 */             if (methodDecl.binding != null) {
/* 2453 */               TypeVariableBinding typeVariable = methodDecl.binding.getTypeVariable(name);
/* 2454 */               if (typeVariable != null)
/* 2455 */                 return typeVariable;
/*      */             }
/*      */             else {
/* 2458 */               TypeParameter[] params = methodDecl.typeParameters();
/* 2459 */               int i = params == null ? 0 : params.length;
/*      */               do { if ((CharOperation.equals(params[i].name, name)) && 
/* 2461 */                   (params[i].binding != null) && (params[i].binding.isValidBinding()))
/* 2462 */                   return params[i].binding;
/* 2459 */                 i--; } while (i >= 0);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 2465 */           insideStaticContext |= methodScope.isStatic;
/* 2466 */           insideTypeAnnotation = methodScope.insideTypeAnnotation;
/*      */         case 1:
/* 2469 */           ReferenceBinding localType = ((BlockScope)scope).findLocalType(name);
/* 2470 */           if (localType == null) break;
/* 2471 */           if ((foundType != null) && (foundType != localType))
/* 2472 */             return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
/* 2473 */           return localType;
/*      */         case 3:
/* 2477 */           SourceTypeBinding sourceType = ((ClassScope)scope).referenceContext.binding;
/* 2478 */           if ((scope == this) && ((sourceType.tagBits & 0x40000) == 0L))
/*      */           {
/* 2481 */             TypeVariableBinding typeVariable = sourceType.getTypeVariable(name);
/* 2482 */             if (typeVariable != null)
/* 2483 */               return typeVariable;
/* 2484 */             if (CharOperation.equals(name, sourceType.sourceName))
/* 2485 */               return sourceType;
/* 2486 */             insideStaticContext |= sourceType.isStatic();
/*      */           }
/*      */           else
/*      */           {
/* 2490 */             if (!insideTypeAnnotation)
/*      */             {
/* 2492 */               ReferenceBinding memberType = findMemberType(name, sourceType);
/* 2493 */               if (memberType != null) {
/* 2494 */                 if (memberType.problemId() == 3) {
/* 2495 */                   if ((foundType == null) || (foundType.problemId() == 2))
/*      */                   {
/* 2497 */                     return memberType;
/*      */                   }
/* 2499 */                   return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
/*      */                 }
/* 2501 */                 if ((memberType.isValidBinding()) && (
/* 2502 */                   (sourceType == memberType.enclosingType()) || (inheritedHasPrecedence))) {
/* 2503 */                   if ((insideStaticContext) && (!memberType.isStatic()) && (sourceType.isGenericType())) {
/* 2504 */                     return new ProblemReferenceBinding(new char[][] { name }, memberType, 7);
/*      */                   }
/*      */ 
/* 2507 */                   if ((foundType == null) || ((inheritedHasPrecedence) && (foundType.problemId() == 2))) {
/* 2508 */                     return memberType;
/*      */                   }
/* 2510 */                   if ((foundType.isValidBinding()) && (foundType != memberType)) {
/* 2511 */                     return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
/*      */                   }
/*      */                 }
/* 2514 */                 if ((foundType == null) || ((foundType.problemId() == 2) && (memberType.problemId() != 2)))
/*      */                 {
/* 2516 */                   foundType = memberType;
/*      */                 }
/*      */               }
/*      */             }
/* 2519 */             TypeVariableBinding typeVariable = sourceType.getTypeVariable(name);
/* 2520 */             if (typeVariable != null) {
/* 2521 */               if (insideStaticContext)
/* 2522 */                 return new ProblemReferenceBinding(new char[][] { name }, typeVariable, 7);
/* 2523 */               return typeVariable;
/*      */             }
/* 2525 */             insideStaticContext |= sourceType.isStatic();
/* 2526 */             insideTypeAnnotation = false;
/* 2527 */             if (!CharOperation.equals(sourceType.sourceName, name)) break;
/* 2528 */             if ((foundType != null) && (foundType != sourceType) && (foundType.problemId() != 2))
/* 2529 */               return new ProblemReferenceBinding(new char[][] { name }, foundType, 5);
/* 2530 */             return sourceType;
/*      */           }
/*      */ 
/*      */         case 4:
/* 2534 */           break;
/*      */         }
/* 2536 */         scope = scope.parent;
/*      */       }
/*      */ 
/* 2538 */       if ((foundType != null) && (foundType.problemId() != 2)) {
/* 2539 */         return foundType;
/*      */       }
/*      */     }
/*      */ 
/* 2543 */     CompilationUnitScope unitScope = (CompilationUnitScope)scope;
/* 2544 */     HashtableOfObject typeOrPackageCache = unitScope.typeOrPackageCache;
/* 2545 */     if (typeOrPackageCache != null) {
/* 2546 */       Binding cachedBinding = (Binding)typeOrPackageCache.get(name);
/* 2547 */       if (cachedBinding != null) {
/* 2548 */         if ((cachedBinding instanceof ImportBinding)) {
/* 2549 */           ImportReference importReference = ((ImportBinding)cachedBinding).reference;
/* 2550 */           if (importReference != null) {
/* 2551 */             importReference.bits |= 2;
/*      */           }
/* 2553 */           if ((cachedBinding instanceof ImportConflictBinding))
/* 2554 */             typeOrPackageCache.put(name, cachedBinding = ((ImportConflictBinding)cachedBinding).conflictingTypeBinding);
/*      */           else
/* 2556 */             typeOrPackageCache.put(name, cachedBinding = ((ImportBinding)cachedBinding).resolvedImport);
/*      */         }
/* 2558 */         if ((mask & 0x4) != 0) {
/* 2559 */           if ((foundType != null) && (foundType.problemId() != 2) && (cachedBinding.problemId() != 3))
/* 2560 */             return foundType;
/* 2561 */           if ((cachedBinding instanceof ReferenceBinding))
/* 2562 */             return cachedBinding;
/*      */         }
/* 2564 */         if (((mask & 0x10) != 0) && ((cachedBinding instanceof PackageBinding))) {
/* 2565 */           return cachedBinding;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2570 */     if ((mask & 0x4) != 0) {
/* 2571 */       ImportBinding[] imports = unitScope.imports;
/* 2572 */       if ((imports != null) && (typeOrPackageCache == null)) {
/* 2573 */         int i = 0; for (int length = imports.length; i < length; i++) {
/* 2574 */           ImportBinding importBinding = imports[i];
/* 2575 */           if ((importBinding.onDemand) || 
/* 2576 */             (!CharOperation.equals(importBinding.compoundName[(importBinding.compoundName.length - 1)], name))) continue;
/* 2577 */           Binding resolvedImport = unitScope.resolveSingleImport(importBinding, 4);
/* 2578 */           if ((resolvedImport == null) || 
/* 2579 */             (!(resolvedImport instanceof TypeBinding))) continue;
/* 2580 */           ImportReference importReference = importBinding.reference;
/* 2581 */           if (importReference != null)
/* 2582 */             importReference.bits |= 2;
/* 2583 */           return resolvedImport;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2591 */       PackageBinding currentPackage = unitScope.fPackage;
/* 2592 */       unitScope.recordReference(currentPackage.compoundName, name);
/* 2593 */       Binding binding = currentPackage.getTypeOrPackage(name);
/* 2594 */       if ((binding instanceof ReferenceBinding)) {
/* 2595 */         ReferenceBinding referenceType = (ReferenceBinding)binding;
/* 2596 */         if ((referenceType.tagBits & 0x80) == 0L) {
/* 2597 */           if (typeOrPackageCache != null)
/* 2598 */             typeOrPackageCache.put(name, referenceType);
/* 2599 */           return referenceType;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2604 */       if (imports != null) {
/* 2605 */         boolean foundInImport = false;
/* 2606 */         ReferenceBinding type = null;
/* 2607 */         int i = 0; for (int length = imports.length; i < length; i++) {
/* 2608 */           ImportBinding someImport = imports[i];
/* 2609 */           if (someImport.onDemand) {
/* 2610 */             Binding resolvedImport = someImport.resolvedImport;
/* 2611 */             ReferenceBinding temp = null;
/* 2612 */             if ((resolvedImport instanceof PackageBinding)) {
/* 2613 */               temp = findType(name, (PackageBinding)resolvedImport, currentPackage);
/* 2614 */             } else if (someImport.isStatic()) {
/* 2615 */               temp = findMemberType(name, (ReferenceBinding)resolvedImport);
/* 2616 */               if ((temp != null) && (!temp.isStatic()))
/* 2617 */                 temp = null;
/*      */             } else {
/* 2619 */               temp = findDirectMemberType(name, (ReferenceBinding)resolvedImport);
/*      */             }
/* 2621 */             if ((temp != type) && (temp != null)) {
/* 2622 */               if (temp.isValidBinding()) {
/* 2623 */                 ImportReference importReference = someImport.reference;
/* 2624 */                 if (importReference != null) {
/* 2625 */                   importReference.bits |= 2;
/*      */                 }
/* 2627 */                 if (foundInImport)
/*      */                 {
/* 2629 */                   temp = new ProblemReferenceBinding(new char[][] { name }, type, 3);
/* 2630 */                   if (typeOrPackageCache != null)
/* 2631 */                     typeOrPackageCache.put(name, temp);
/* 2632 */                   return temp;
/*      */                 }
/* 2634 */                 type = temp;
/* 2635 */                 foundInImport = true;
/* 2636 */               } else if (foundType == null) {
/* 2637 */                 foundType = temp;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2642 */         if (type != null) {
/* 2643 */           if (typeOrPackageCache != null)
/* 2644 */             typeOrPackageCache.put(name, type);
/* 2645 */           return type;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2650 */     unitScope.recordSimpleReference(name);
/* 2651 */     if ((mask & 0x10) != 0) {
/* 2652 */       PackageBinding packageBinding = unitScope.environment.getTopLevelPackage(name);
/* 2653 */       if (packageBinding != null) {
/* 2654 */         if (typeOrPackageCache != null)
/* 2655 */           typeOrPackageCache.put(name, packageBinding);
/* 2656 */         return packageBinding;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2661 */     if (foundType == null) {
/* 2662 */       char[][] qName = { name };
/* 2663 */       ReferenceBinding closestMatch = null;
/* 2664 */       if ((((mask & 0x10) != 0) || (unitScope.environment.getTopLevelPackage(name) == null)) && 
/* 2665 */         (needResolve)) {
/* 2666 */         closestMatch = environment().createMissingType(unitScope.fPackage, qName);
/*      */       }
/*      */ 
/* 2669 */       foundType = new ProblemReferenceBinding(qName, closestMatch, 1);
/* 2670 */       if ((typeOrPackageCache != null) && ((mask & 0x10) != 0))
/* 2671 */         typeOrPackageCache.put(name, foundType);
/*      */     }
/* 2673 */     else if ((foundType.tagBits & 0x80) != 0L) {
/* 2674 */       char[][] qName = { name };
/* 2675 */       foundType = new ProblemReferenceBinding(qName, foundType, 1);
/* 2676 */       if ((typeOrPackageCache != null) && ((mask & 0x10) != 0))
/* 2677 */         typeOrPackageCache.put(name, foundType);
/*      */     }
/* 2679 */     return foundType;
/*      */   }
/*      */ 
/*      */   public final Binding getTypeOrPackage(char[][] compoundName)
/*      */   {
/* 2687 */     int nameLength = compoundName.length;
/* 2688 */     if (nameLength == 1) {
/* 2689 */       TypeBinding binding = getBaseType(compoundName[0]);
/* 2690 */       if (binding != null) return binding;
/*      */     }
/* 2692 */     Binding binding = getTypeOrPackage(compoundName[0], 20, true);
/* 2693 */     if (!binding.isValidBinding()) return binding;
/*      */ 
/* 2695 */     int currentIndex = 1;
/* 2696 */     boolean checkVisibility = false;
/* 2697 */     if ((binding instanceof PackageBinding)) {
/* 2698 */       PackageBinding packageBinding = (PackageBinding)binding;
/*      */ 
/* 2700 */       while (currentIndex < nameLength) {
/* 2701 */         binding = packageBinding.getTypeOrPackage(compoundName[(currentIndex++)]);
/* 2702 */         if (binding == null)
/* 2703 */           return new ProblemReferenceBinding(
/* 2704 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2705 */             null, 
/* 2706 */             1);
/* 2707 */         if (!binding.isValidBinding())
/* 2708 */           return new ProblemReferenceBinding(
/* 2709 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2710 */             (binding instanceof ReferenceBinding) ? (ReferenceBinding)((ReferenceBinding)binding).closestMatch() : null, 
/* 2711 */             binding.problemId());
/* 2712 */         if (!(binding instanceof PackageBinding))
/*      */           break;
/* 2714 */         packageBinding = (PackageBinding)binding;
/*      */       }
/* 2716 */       if ((binding instanceof PackageBinding)) return binding;
/* 2717 */       checkVisibility = true;
/*      */     }
/*      */ 
/* 2720 */     ReferenceBinding typeBinding = (ReferenceBinding)binding;
/* 2721 */     ReferenceBinding qualifiedType = (ReferenceBinding)environment().convertToRawType(typeBinding, false);
/*      */ 
/* 2723 */     if ((checkVisibility) && 
/* 2724 */       (!typeBinding.canBeSeenBy(this)))
/* 2725 */       return new ProblemReferenceBinding(
/* 2726 */         CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2727 */         typeBinding, 
/* 2728 */         2);
/*      */     do
/*      */     {
/* 2731 */       typeBinding = getMemberType(compoundName[(currentIndex++)], typeBinding);
/*      */ 
/* 2733 */       if (!typeBinding.isValidBinding()) {
/* 2734 */         return new ProblemReferenceBinding(
/* 2735 */           CharOperation.subarray(compoundName, 0, currentIndex), 
/* 2736 */           (ReferenceBinding)typeBinding.closestMatch(), 
/* 2737 */           typeBinding.problemId());
/*      */       }
/* 2739 */       if (typeBinding.isGenericType())
/* 2740 */         qualifiedType = environment().createRawType(typeBinding, qualifiedType);
/*      */       else
/* 2742 */         qualifiedType = (qualifiedType != null) && ((qualifiedType.isRawType()) || (qualifiedType.isParameterizedType())) ? 
/* 2743 */           environment().createParameterizedType(typeBinding, null, qualifiedType) : 
/* 2744 */           typeBinding;
/*      */     }
/* 2730 */     while (currentIndex < nameLength);
/*      */ 
/* 2747 */     return qualifiedType;
/*      */   }
/*      */ 
/*      */   protected boolean hasErasedCandidatesCollisions(TypeBinding one, TypeBinding two, Map invocations, ReferenceBinding type, ASTNode typeRef) {
/* 2751 */     invocations.clear();
/* 2752 */     TypeBinding[] mecs = minimalErasedCandidates(new TypeBinding[] { one, two }, invocations);
/* 2753 */     if (mecs != null) {
/* 2754 */       int k = 0; for (int max = mecs.length; k < max; k++) {
/* 2755 */         TypeBinding mec = mecs[k];
/* 2756 */         if (mec != null) {
/* 2757 */           Object value = invocations.get(mec);
/* 2758 */           if ((value instanceof TypeBinding[])) {
/* 2759 */             TypeBinding[] invalidInvocations = (TypeBinding[])value;
/* 2760 */             problemReporter().superinterfacesCollide(invalidInvocations[0].erasure(), typeRef, invalidInvocations[0], invalidInvocations[1]);
/* 2761 */             type.tagBits |= 131072L;
/* 2762 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2766 */     return false;
/*      */   }
/*      */ 
/*      */   public CaseStatement innermostSwitchCase()
/*      */   {
/* 2773 */     Scope scope = this;
/*      */     do {
/* 2775 */       if ((scope instanceof BlockScope))
/* 2776 */         return ((BlockScope)scope).enclosingCase;
/* 2777 */       scope = scope.parent;
/* 2778 */     }while (scope != null);
/* 2779 */     return null;
/*      */   }
/*      */ 
/*      */   protected boolean isAcceptableMethod(MethodBinding one, MethodBinding two) {
/* 2783 */     TypeBinding[] oneParams = one.parameters;
/* 2784 */     TypeBinding[] twoParams = two.parameters;
/* 2785 */     int oneParamsLength = oneParams.length;
/* 2786 */     int twoParamsLength = twoParams.length;
/* 2787 */     if (oneParamsLength == twoParamsLength) {
/* 2788 */       for (int i = 0; i < oneParamsLength; i++) {
/* 2789 */         TypeBinding oneParam = oneParams[i];
/* 2790 */         TypeBinding twoParam = twoParams[i];
/*      */         TypeBinding originalTwoParam;
/* 2791 */         if ((oneParam == twoParam) || (oneParam.isCompatibleWith(twoParam))) {
/* 2792 */           if (two.declaringClass.isRawType())
/*      */             continue;
/* 2794 */           originalTwoParam = two.original().parameters[i].leafComponentType();
/* 2795 */         }switch (originalTwoParam.kind()) {
/*      */         case 4100:
/* 2797 */           if (((TypeVariableBinding)originalTwoParam).hasOnlyRawBounds()) {
/*      */             continue;
/*      */           }
/*      */         case 260:
/*      */         case 516:
/*      */         case 8196:
/* 2803 */           TypeBinding originalOneParam = one.original().parameters[i].leafComponentType();
/* 2804 */           switch (originalOneParam.kind()) {
/*      */           case 4:
/*      */           case 2052:
/* 2807 */             TypeBinding inheritedTwoParam = oneParam.findSuperTypeOriginatingFrom(twoParam);
/* 2808 */             if ((inheritedTwoParam == null) || (!inheritedTwoParam.leafComponentType().isRawType())) continue;
/* 2809 */             return false;
/*      */           case 4100:
/* 2811 */             if (!((TypeVariableBinding)originalOneParam).upperBound().isRawType()) continue;
/* 2812 */             return false;
/*      */           case 1028:
/* 2815 */             return false;
/*      */           }default:
/* 2817 */           continue;
/*      */ 
/* 2819 */           if ((i == oneParamsLength - 1) && (one.isVarargs()) && (two.isVarargs())) {
/* 2820 */             TypeBinding eType = ((ArrayBinding)twoParam).elementsType();
/* 2821 */             if ((oneParam == eType) || (oneParam.isCompatibleWith(eType)))
/* 2822 */               return true;
/*      */           }
/* 2824 */           return false;
/*      */         }
/*      */       }
/* 2827 */       return true;
/*      */     }
/*      */ 
/* 2830 */     if ((one.isVarargs()) && (two.isVarargs())) {
/* 2831 */       if (oneParamsLength > twoParamsLength)
/*      */       {
/* 2833 */         if (((ArrayBinding)twoParams[(twoParamsLength - 1)]).elementsType().id != 1) {
/* 2834 */           return false;
/*      */         }
/*      */       }
/* 2837 */       for (int i = (oneParamsLength > twoParamsLength ? twoParamsLength : oneParamsLength) - 2; i >= 0; i--)
/* 2838 */         if ((oneParams[i] != twoParams[i]) && (!oneParams[i].isCompatibleWith(twoParams[i])))
/* 2839 */           return false;
/* 2840 */       if ((parameterCompatibilityLevel(one, twoParams) == -1) && 
/* 2841 */         (parameterCompatibilityLevel(two, oneParams) == 2))
/* 2842 */         return true;
/*      */     }
/* 2844 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isBoxingCompatibleWith(TypeBinding expressionType, TypeBinding targetType) {
/* 2848 */     LookupEnvironment environment = environment();
/* 2849 */     if ((environment.globalOptions.sourceLevel < 3211264L) || (expressionType.isBaseType() == targetType.isBaseType())) {
/* 2850 */       return false;
/*      */     }
/*      */ 
/* 2853 */     TypeBinding convertedType = environment.computeBoxingType(expressionType);
/* 2854 */     return (convertedType == targetType) || (convertedType.isCompatibleWith(targetType));
/*      */   }
/*      */ 
/*      */   public final boolean isDefinedInField(FieldBinding field)
/*      */   {
/* 2862 */     Scope scope = this;
/*      */     do {
/* 2864 */       if ((scope instanceof MethodScope)) {
/* 2865 */         MethodScope methodScope = (MethodScope)scope;
/* 2866 */         if (methodScope.initializedField == field) return true;
/*      */       }
/* 2868 */       scope = scope.parent;
/* 2869 */     }while (scope != null);
/* 2870 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isDefinedInMethod(MethodBinding method)
/*      */   {
/* 2876 */     Scope scope = this;
/*      */     do {
/* 2878 */       if ((scope instanceof MethodScope)) {
/* 2879 */         ReferenceContext refContext = ((MethodScope)scope).referenceContext;
/* 2880 */         if (((refContext instanceof AbstractMethodDeclaration)) && 
/* 2881 */           (((AbstractMethodDeclaration)refContext).binding == method))
/* 2882 */           return true;
/*      */       }
/* 2884 */       scope = scope.parent;
/* 2885 */     }while (scope != null);
/* 2886 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isDefinedInSameUnit(ReferenceBinding type)
/*      */   {
/* 2893 */     ReferenceBinding enclosingType = type;
/* 2894 */     while ((type = enclosingType.enclosingType()) != null) {
/* 2895 */       enclosingType = type;
/*      */     }
/*      */ 
/* 2898 */     Scope unitScope = this;
/*      */     Scope scope;
/* 2899 */     while ((scope = unitScope.parent) != null)
/*      */     {
/*      */       Scope scope;
/* 2900 */       unitScope = scope;
/*      */     }
/*      */ 
/* 2903 */     SourceTypeBinding[] topLevelTypes = ((CompilationUnitScope)unitScope).topLevelTypes;
/* 2904 */     int i = topLevelTypes.length;
/*      */     do { if (topLevelTypes[i] == enclosingType)
/* 2906 */         return true;
/* 2904 */       i--; } while (i >= 0);
/*      */ 
/* 2907 */     return false;
/*      */   }
/*      */ 
/*      */   public final boolean isDefinedInType(ReferenceBinding type)
/*      */   {
/* 2913 */     Scope scope = this;
/*      */     do {
/* 2915 */       if (((scope instanceof ClassScope)) && 
/* 2916 */         (((ClassScope)scope).referenceContext.binding == type))
/* 2917 */         return true;
/* 2918 */       scope = scope.parent;
/* 2919 */     }while (scope != null);
/* 2920 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isInsideCase(CaseStatement caseStatement)
/*      */   {
/* 2928 */     Scope scope = this;
/*      */     do {
/* 2930 */       switch (scope.kind) {
/*      */       case 1:
/* 2932 */         if (((BlockScope)scope).enclosingCase != caseStatement) break;
/* 2933 */         return true;
/*      */       }
/*      */ 
/* 2936 */       scope = scope.parent;
/*      */     }
/* 2937 */     while (scope != null);
/* 2938 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isInsideDeprecatedCode() {
/* 2942 */     switch (this.kind) {
/*      */     case 1:
/*      */     case 2:
/* 2945 */       MethodScope methodScope = methodScope();
/* 2946 */       if (!methodScope.isInsideInitializer())
/*      */       {
/* 2948 */         MethodBinding context = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
/* 2949 */         if ((context != null) && (context.isViewedAsDeprecated()))
/* 2950 */           return true;
/* 2951 */       } else if ((methodScope.initializedField != null) && (methodScope.initializedField.isViewedAsDeprecated()))
/*      */       {
/* 2953 */         return true;
/*      */       }
/* 2955 */       SourceTypeBinding declaringType = ((BlockScope)this).referenceType().binding;
/* 2956 */       if (declaringType == null) break;
/* 2957 */       declaringType.initializeDeprecatedAnnotationTagBits();
/* 2958 */       if (!declaringType.isViewedAsDeprecated()) break;
/* 2959 */       return true;
/*      */     case 3:
/* 2963 */       ReferenceBinding context = ((ClassScope)this).referenceType().binding;
/* 2964 */       if (context == null) break;
/* 2965 */       context.initializeDeprecatedAnnotationTagBits();
/* 2966 */       if (!context.isViewedAsDeprecated()) break;
/* 2967 */       return true;
/*      */     case 4:
/* 2972 */       CompilationUnitDeclaration unit = referenceCompilationUnit();
/* 2973 */       if ((unit.types == null) || (unit.types.length <= 0)) break;
/* 2974 */       SourceTypeBinding type = unit.types[0].binding;
/* 2975 */       if (type == null) break;
/* 2976 */       type.initializeDeprecatedAnnotationTagBits();
/* 2977 */       if (!type.isViewedAsDeprecated()) break;
/* 2978 */       return true;
/*      */     }
/*      */ 
/* 2982 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isOverriddenMethodGeneric(MethodBinding method) {
/* 2986 */     MethodVerifier verifier = environment().methodVerifier();
/* 2987 */     ReferenceBinding currentType = method.declaringClass.superclass();
/* 2988 */     while (currentType != null) {
/* 2989 */       MethodBinding[] currentMethods = currentType.getMethods(method.selector);
/* 2990 */       int i = 0; for (int l = currentMethods.length; i < l; i++) {
/* 2991 */         MethodBinding currentMethod = currentMethods[i];
/* 2992 */         if ((currentMethod != null) && (currentMethod.original().typeVariables != Binding.NO_TYPE_VARIABLES) && 
/* 2993 */           (verifier.doesMethodOverride(method, currentMethod)))
/* 2994 */           return true;
/*      */       }
/* 2996 */       currentType = currentType.superclass();
/*      */     }
/* 2998 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isPossibleSubtypeOfRawType(TypeBinding paramType) {
/* 3002 */     TypeBinding t = paramType.leafComponentType();
/* 3003 */     if (t.isBaseType()) return false;
/*      */ 
/* 3005 */     ReferenceBinding currentType = (ReferenceBinding)t;
/* 3006 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/* 3007 */     int nextPosition = 0;
/*      */     do {
/* 3009 */       if (currentType.isRawType()) return true;
/* 3010 */       if (!currentType.isHierarchyConnected()) return true;
/*      */ 
/* 3012 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/* 3013 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/* 3014 */         if (interfacesToVisit == null) {
/* 3015 */           interfacesToVisit = itsInterfaces;
/* 3016 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/* 3018 */           int itsLength = itsInterfaces.length;
/* 3019 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/* 3020 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 3021 */           for (int a = 0; a < itsLength; a++) {
/* 3022 */             ReferenceBinding next = itsInterfaces[a];
/* 3023 */             int b = 0;
/* 3024 */             while (next != interfacesToVisit[b])
/*      */             {
/* 3023 */               b++; if (b < nextPosition)
/*      */                 continue;
/* 3025 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/*      */     }
/* 3029 */     while ((currentType = currentType.superclass()) != null);
/*      */ 
/* 3031 */     for (int i = 0; i < nextPosition; i++) {
/* 3032 */       currentType = interfacesToVisit[i];
/* 3033 */       if (currentType.isRawType()) return true;
/*      */ 
/* 3035 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/* 3036 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/* 3037 */         int itsLength = itsInterfaces.length;
/* 3038 */         if (nextPosition + itsLength >= interfacesToVisit.length)
/* 3039 */           System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/* 3040 */         for (int a = 0; a < itsLength; a++) {
/* 3041 */           ReferenceBinding next = itsInterfaces[a];
/* 3042 */           int b = 0;
/* 3043 */           while (next != interfacesToVisit[b])
/*      */           {
/* 3042 */             b++; if (b < nextPosition)
/*      */               continue;
/* 3044 */             interfacesToVisit[(nextPosition++)] = next;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 3048 */     return false;
/*      */   }
/*      */ 
/*      */   private TypeBinding leastContainingInvocation(TypeBinding mec, Object invocationData, List lubStack) {
/* 3052 */     if (invocationData == null) return mec;
/* 3053 */     if ((invocationData instanceof TypeBinding)) {
/* 3054 */       return (TypeBinding)invocationData;
/*      */     }
/* 3056 */     TypeBinding[] invocations = (TypeBinding[])invocationData;
/*      */ 
/* 3059 */     int dim = mec.dimensions();
/* 3060 */     mec = mec.leafComponentType();
/*      */ 
/* 3062 */     int argLength = mec.typeVariables().length;
/* 3063 */     if (argLength == 0) return mec;
/*      */ 
/* 3066 */     TypeBinding[] bestArguments = new TypeBinding[argLength];
/* 3067 */     int i = 0; for (int length = invocations.length; i < length; i++) {
/* 3068 */       TypeBinding invocation = invocations[i].leafComponentType();
/* 3069 */       switch (invocation.kind()) {
/*      */       case 2052:
/* 3071 */         TypeVariableBinding[] invocationVariables = invocation.typeVariables();
/* 3072 */         for (int j = 0; j < argLength; j++) {
/* 3073 */           TypeBinding bestArgument = leastContainingTypeArgument(bestArguments[j], invocationVariables[j], (ReferenceBinding)mec, j, lubStack);
/* 3074 */           if (bestArgument == null) return null;
/* 3075 */           bestArguments[j] = bestArgument;
/*      */         }
/* 3077 */         break;
/*      */       case 260:
/* 3079 */         ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)invocation;
/* 3080 */         for (int j = 0; j < argLength; j++) {
/* 3081 */           TypeBinding bestArgument = leastContainingTypeArgument(bestArguments[j], parameterizedType.arguments[j], (ReferenceBinding)mec, j, lubStack);
/* 3082 */           if (bestArgument == null) return null;
/* 3083 */           bestArguments[j] = bestArgument;
/*      */         }
/* 3085 */         break;
/*      */       case 1028:
/* 3087 */         return dim == 0 ? invocation : environment().createArrayType(invocation, dim);
/*      */       }
/*      */     }
/* 3090 */     TypeBinding least = environment().createParameterizedType((ReferenceBinding)mec.erasure(), bestArguments, mec.enclosingType());
/* 3091 */     return dim == 0 ? least : environment().createArrayType(least, dim);
/*      */   }
/*      */ 
/*      */   private TypeBinding leastContainingTypeArgument(TypeBinding u, TypeBinding v, ReferenceBinding genericType, int rank, List lubStack)
/*      */   {
/* 3096 */     if (u == null) return v;
/* 3097 */     if (u == v) return u;
/* 3098 */     if (v.isWildcard()) {
/* 3099 */       WildcardBinding wildV = (WildcardBinding)v;
/* 3100 */       if (u.isWildcard()) {
/* 3101 */         WildcardBinding wildU = (WildcardBinding)u;
/* 3102 */         switch (wildU.boundKind)
/*      */         {
/*      */         case 1:
/* 3105 */           switch (wildV.boundKind)
/*      */           {
/*      */           case 1:
/* 3108 */             TypeBinding lub = lowerUpperBound(new TypeBinding[] { wildU.bound, wildV.bound }, lubStack);
/* 3109 */             if (lub == null) return null;
/*      */ 
/* 3111 */             if (lub == TypeBinding.INT) return environment().createWildcard(genericType, rank, null, null, 0);
/* 3112 */             return environment().createWildcard(genericType, rank, lub, null, 1);
/*      */           case 2:
/* 3115 */             if (wildU.bound == wildV.bound) return wildU.bound;
/* 3116 */             return environment().createWildcard(genericType, rank, null, null, 0);
/*      */           }
/* 3118 */           break;
/*      */         case 2:
/* 3122 */           if (wildU.boundKind != 2) break;
/* 3123 */           TypeBinding[] glb = greaterLowerBound(new TypeBinding[] { wildU.bound, wildV.bound });
/* 3124 */           if (glb == null) return null;
/* 3125 */           return environment().createWildcard(genericType, rank, glb[0], null, 2);
/*      */         default:
/* 3127 */           break;
/*      */         }
/*      */       } else {
/* 3129 */         switch (wildV.boundKind)
/*      */         {
/*      */         case 1:
/* 3132 */           TypeBinding lub = lowerUpperBound(new TypeBinding[] { u, wildV.bound }, lubStack);
/* 3133 */           if (lub == null) return null;
/*      */ 
/* 3135 */           if (lub == TypeBinding.INT) return environment().createWildcard(genericType, rank, null, null, 0);
/* 3136 */           return environment().createWildcard(genericType, rank, lub, null, 1);
/*      */         case 2:
/* 3139 */           TypeBinding[] glb = greaterLowerBound(new TypeBinding[] { u, wildV.bound });
/* 3140 */           if (glb == null) return null;
/* 3141 */           return environment().createWildcard(genericType, rank, glb[0], null, 2);
/*      */         case 0:
/*      */         }
/*      */       }
/* 3145 */     } else if (u.isWildcard()) {
/* 3146 */       WildcardBinding wildU = (WildcardBinding)u;
/* 3147 */       switch (wildU.boundKind)
/*      */       {
/*      */       case 1:
/* 3150 */         TypeBinding lub = lowerUpperBound(new TypeBinding[] { wildU.bound, v }, lubStack);
/* 3151 */         if (lub == null) return null;
/*      */ 
/* 3153 */         if (lub == TypeBinding.INT) return environment().createWildcard(genericType, rank, null, null, 0);
/* 3154 */         return environment().createWildcard(genericType, rank, lub, null, 1);
/*      */       case 2:
/* 3157 */         TypeBinding[] glb = greaterLowerBound(new TypeBinding[] { wildU.bound, v });
/* 3158 */         if (glb == null) return null;
/* 3159 */         return environment().createWildcard(genericType, rank, glb[0], null, 2);
/*      */       case 0:
/*      */       }
/*      */     }
/* 3163 */     TypeBinding lub = lowerUpperBound(new TypeBinding[] { u, v }, lubStack);
/* 3164 */     if (lub == null) return null;
/*      */ 
/* 3166 */     if (lub == TypeBinding.INT) return environment().createWildcard(genericType, rank, null, null, 0);
/* 3167 */     return environment().createWildcard(genericType, rank, lub, null, 1);
/*      */   }
/*      */ 
/*      */   public TypeBinding lowerUpperBound(TypeBinding[] types)
/*      */   {
/* 3176 */     int typeLength = types.length;
/* 3177 */     if (typeLength == 1) {
/* 3178 */       TypeBinding type = types[0];
/* 3179 */       return type == null ? TypeBinding.VOID : type;
/*      */     }
/* 3181 */     return lowerUpperBound(types, new ArrayList(1));
/*      */   }
/*      */ 
/*      */   private TypeBinding lowerUpperBound(TypeBinding[] types, List lubStack)
/*      */   {
/* 3187 */     int typeLength = types.length;
/* 3188 */     if (typeLength == 1) {
/* 3189 */       TypeBinding type = types[0];
/* 3190 */       return type == null ? TypeBinding.VOID : type;
/*      */     }
/*      */ 
/* 3193 */     int stackLength = lubStack.size();
/* 3194 */     label155: for (int i = 0; i < stackLength; i++) {
/* 3195 */       TypeBinding[] lubTypes = (TypeBinding[])lubStack.get(i);
/* 3196 */       int lubTypeLength = lubTypes.length;
/* 3197 */       if (lubTypeLength >= typeLength) {
/* 3198 */         int j = 0;
/*      */         while (true) { TypeBinding type = types[j];
/* 3200 */           if (type != null) {
/* 3201 */             int k = 0;
/*      */             while (true) { TypeBinding lubType = lubTypes[k];
/* 3203 */               if ((lubType != null) && (
/* 3204 */                 (lubType == type) || (lubType.isEquivalentTo(type))))
/*      */                 break;
/* 3201 */               k++; if (k < lubTypeLength)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 3206 */               break label155;
/*      */             }
/*      */           }
/* 3198 */           j++; if (j >= typeLength)
/*      */           {
/* 3209 */             return TypeBinding.INT;
/*      */           } }
/*      */       }
/*      */     }
/* 3212 */     lubStack.add(types);
/* 3213 */     Map invocations = new HashMap(1);
/* 3214 */     TypeBinding[] mecs = minimalErasedCandidates(types, invocations);
/* 3215 */     if (mecs == null) return null;
/* 3216 */     int length = mecs.length;
/* 3217 */     if (length == 0) return TypeBinding.VOID;
/* 3218 */     int count = 0;
/* 3219 */     TypeBinding firstBound = null;
/* 3220 */     int commonDim = -1;
/* 3221 */     for (int i = 0; i < length; i++) {
/* 3222 */       TypeBinding mec = mecs[i];
/* 3223 */       if (mec != null) {
/* 3224 */         mec = leastContainingInvocation(mec, invocations.get(mec), lubStack);
/* 3225 */         if (mec == null) return null;
/* 3226 */         int dim = mec.dimensions();
/* 3227 */         if (commonDim == -1)
/* 3228 */           commonDim = dim;
/* 3229 */         else if (dim != commonDim) {
/* 3230 */           return null;
/*      */         }
/* 3232 */         if ((firstBound == null) && (!mec.leafComponentType().isInterface())) firstBound = mec.leafComponentType();
/* 3233 */         mecs[(count++)] = mec;
/*      */       }
/*      */     }
/* 3235 */     switch (count) { case 0:
/* 3236 */       return TypeBinding.VOID;
/*      */     case 1:
/* 3237 */       return mecs[0];
/*      */     case 2:
/* 3239 */       if ((commonDim == 0 ? mecs[1].id : mecs[1].leafComponentType().id) == 1) return mecs[0];
/* 3240 */       if ((commonDim == 0 ? mecs[0].id : mecs[0].leafComponentType().id) != 1) break; return mecs[1];
/*      */     }
/* 3242 */     TypeBinding[] otherBounds = new TypeBinding[count - 1];
/* 3243 */     int rank = 0;
/* 3244 */     for (int i = 0; i < count; i++) {
/* 3245 */       TypeBinding mec = commonDim == 0 ? mecs[i] : mecs[i].leafComponentType();
/* 3246 */       if (mec.isInterface()) {
/* 3247 */         otherBounds[(rank++)] = mec;
/*      */       }
/*      */     }
/* 3250 */     TypeBinding intersectionType = environment().createWildcard(null, 0, firstBound, otherBounds, 1);
/* 3251 */     return commonDim == 0 ? intersectionType : environment().createArrayType(intersectionType, commonDim);
/*      */   }
/*      */ 
/*      */   public final MethodScope methodScope() {
/* 3255 */     Scope scope = this;
/*      */     do {
/* 3257 */       if ((scope instanceof MethodScope))
/* 3258 */         return (MethodScope)scope;
/* 3259 */       scope = scope.parent;
/* 3260 */     }while (scope != null);
/* 3261 */     return null;
/*      */   }
/*      */ 
/*      */   protected TypeBinding[] minimalErasedCandidates(TypeBinding[] types, Map allInvocations)
/*      */   {
/* 3273 */     int length = types.length;
/* 3274 */     int indexOfFirst = -1; int actualLength = 0;
/* 3275 */     for (int i = 0; i < length; i++) {
/* 3276 */       TypeBinding type = types[i];
/* 3277 */       if (type != null) {
/* 3278 */         if (type.isBaseType()) return null;
/* 3279 */         if (indexOfFirst < 0) indexOfFirst = i;
/* 3280 */         actualLength++;
/*      */       }
/*      */     }
/* 3282 */     switch (actualLength) { case 0:
/* 3283 */       return Binding.NO_TYPES;
/*      */     case 1:
/* 3284 */       return types;
/*      */     }
/* 3286 */     TypeBinding firstType = types[indexOfFirst];
/* 3287 */     if (firstType.isBaseType()) return null;
/*      */ 
/* 3291 */     ArrayList typesToVisit = new ArrayList(5);
/*      */ 
/* 3293 */     int dim = firstType.dimensions();
/* 3294 */     TypeBinding leafType = firstType.leafComponentType();
/*      */     TypeBinding firstErasure;
/*      */     TypeBinding firstErasure;
/* 3297 */     switch (leafType.kind()) {
/*      */     case 68:
/*      */     case 260:
/*      */     case 1028:
/* 3301 */       firstErasure = firstType.erasure();
/* 3302 */       break;
/*      */     default:
/* 3304 */       firstErasure = firstType;
/*      */     }
/*      */ 
/* 3307 */     if (firstErasure != firstType) {
/* 3308 */       allInvocations.put(firstErasure, firstType);
/*      */     }
/* 3310 */     typesToVisit.add(firstType);
/* 3311 */     int max = 1;
/*      */ 
/* 3313 */     for (int i = 0; i < max; i++) {
/* 3314 */       TypeBinding typeToVisit = (TypeBinding)typesToVisit.get(i);
/* 3315 */       dim = typeToVisit.dimensions();
/* 3316 */       if (dim > 0) {
/* 3317 */         leafType = typeToVisit.leafComponentType();
/* 3318 */         switch (leafType.id) {
/*      */         case 1:
/* 3320 */           if (dim <= 1) break;
/* 3321 */           TypeBinding elementType = ((ArrayBinding)typeToVisit).elementsType();
/* 3322 */           if (typesToVisit.contains(elementType)) continue;
/* 3323 */           typesToVisit.add(elementType);
/* 3324 */           max++;
/*      */ 
/* 3326 */           break;
/*      */         case 2:
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 7:
/*      */         case 8:
/*      */         case 9:
/*      */         case 10:
/* 3337 */           TypeBinding superType = getJavaIoSerializable();
/* 3338 */           if (!typesToVisit.contains(superType)) {
/* 3339 */             typesToVisit.add(superType);
/* 3340 */             max++;
/*      */           }
/* 3342 */           superType = getJavaLangCloneable();
/* 3343 */           if (!typesToVisit.contains(superType)) {
/* 3344 */             typesToVisit.add(superType);
/* 3345 */             max++;
/*      */           }
/* 3347 */           superType = getJavaLangObject();
/* 3348 */           if (typesToVisit.contains(superType)) continue;
/* 3349 */           typesToVisit.add(superType);
/* 3350 */           max++;
/*      */ 
/* 3352 */           break;
/*      */         case 6:
/*      */         }
/*      */ 
/* 3356 */         typeToVisit = leafType;
/*      */       }
/* 3358 */       ReferenceBinding currentType = (ReferenceBinding)typeToVisit;
/* 3359 */       if (currentType.isCapture()) {
/* 3360 */         TypeBinding firstBound = ((CaptureBinding)currentType).firstBound;
/* 3361 */         if ((firstBound != null) && (firstBound.isArrayType())) {
/* 3362 */           TypeBinding superType = dim == 0 ? firstBound : environment().createArrayType(firstBound, dim);
/* 3363 */           if (typesToVisit.contains(superType)) continue;
/* 3364 */           typesToVisit.add(superType);
/* 3365 */           max++;
/* 3366 */           TypeBinding superTypeErasure = (firstBound.isTypeVariable()) || (firstBound.isWildcard()) ? superType : superType.erasure();
/* 3367 */           if (superTypeErasure == superType) continue;
/* 3368 */           allInvocations.put(superTypeErasure, superType);
/*      */ 
/* 3371 */           continue;
/*      */         }
/*      */       }
/*      */ 
/* 3375 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/* 3376 */       if (itsInterfaces != null) {
/* 3377 */         int j = 0; for (int count = itsInterfaces.length; j < count; j++) {
/* 3378 */           TypeBinding itsInterface = itsInterfaces[j];
/* 3379 */           TypeBinding superType = dim == 0 ? itsInterface : environment().createArrayType(itsInterface, dim);
/* 3380 */           if (!typesToVisit.contains(superType)) {
/* 3381 */             typesToVisit.add(superType);
/* 3382 */             max++;
/* 3383 */             TypeBinding superTypeErasure = (itsInterface.isTypeVariable()) || (itsInterface.isWildcard()) ? superType : superType.erasure();
/* 3384 */             if (superTypeErasure != superType) {
/* 3385 */               allInvocations.put(superTypeErasure, superType);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3390 */       TypeBinding itsSuperclass = currentType.superclass();
/* 3391 */       if (itsSuperclass != null) {
/* 3392 */         TypeBinding superType = dim == 0 ? itsSuperclass : environment().createArrayType(itsSuperclass, dim);
/* 3393 */         if (!typesToVisit.contains(superType)) {
/* 3394 */           typesToVisit.add(superType);
/* 3395 */           max++;
/* 3396 */           TypeBinding superTypeErasure = (itsSuperclass.isTypeVariable()) || (itsSuperclass.isWildcard()) ? superType : superType.erasure();
/* 3397 */           if (superTypeErasure != superType) {
/* 3398 */             allInvocations.put(superTypeErasure, superType);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 3403 */     int superLength = typesToVisit.size();
/* 3404 */     TypeBinding[] erasedSuperTypes = new TypeBinding[superLength];
/* 3405 */     int rank = 0;
/* 3406 */     for (Iterator iter = typesToVisit.iterator(); iter.hasNext(); ) {
/* 3407 */       TypeBinding type = (TypeBinding)iter.next();
/* 3408 */       leafType = type.leafComponentType();
/* 3409 */       erasedSuperTypes[(rank++)] = ((leafType.isTypeVariable()) || (leafType.isWildcard()) ? type : type.erasure());
/*      */     }
/*      */ 
/* 3412 */     int remaining = superLength;
/* 3413 */     for (int i = indexOfFirst + 1; i < length; i++) {
/* 3414 */       TypeBinding otherType = types[i];
/* 3415 */       if (otherType != null) {
/* 3416 */         if (otherType.isArrayType())
/* 3417 */           for (int j = 0; j < superLength; j++) {
/* 3418 */             TypeBinding erasedSuperType = erasedSuperTypes[j];
/* 3419 */             if ((erasedSuperType == null) || (erasedSuperType == otherType))
/*      */               continue;
/*      */             TypeBinding match;
/* 3421 */             if ((match = otherType.findSuperTypeOriginatingFrom(erasedSuperType)) == null) {
/* 3422 */               erasedSuperTypes[j] = null;
/* 3423 */               remaining--; if (remaining == 0) return null;
/*      */             }
/*      */             else
/*      */             {
/* 3427 */               Object invocationData = allInvocations.get(erasedSuperType);
/* 3428 */               if (invocationData == null) {
/* 3429 */                 allInvocations.put(erasedSuperType, match);
/* 3430 */               } else if ((invocationData instanceof TypeBinding)) {
/* 3431 */                 if (match == invocationData)
/*      */                   continue;
/* 3433 */                 TypeBinding[] someInvocations = { (TypeBinding)invocationData, match };
/* 3434 */                 allInvocations.put(erasedSuperType, someInvocations);
/*      */               }
/*      */               else {
/* 3437 */                 TypeBinding[] someInvocations = (TypeBinding[])invocationData;
/*      */ 
/* 3439 */                 int invocLength = someInvocations.length;
/* 3440 */                 int k = 0;
/* 3441 */                 while (someInvocations[k] != match)
/*      */                 {
/* 3440 */                   k++; if (k < invocLength) {
/*      */                     continue;
/*      */                   }
/* 3443 */                   System.arraycopy(someInvocations, 0, someInvocations = new TypeBinding[invocLength + 1], 0, invocLength);
/* 3444 */                   allInvocations.put(erasedSuperType, someInvocations);
/* 3445 */                   someInvocations[invocLength] = match;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         else
/* 3451 */           for (int j = 0; j < superLength; j++) {
/* 3452 */             TypeBinding erasedSuperType = erasedSuperTypes[j];
/* 3453 */             if (erasedSuperType == null)
/*      */               continue;
/*      */             TypeBinding match;
/*      */             TypeBinding match;
/* 3455 */             if ((erasedSuperType == otherType) || ((erasedSuperType.id == 1) && (otherType.isInterface()))) {
/* 3456 */               match = erasedSuperType;
/*      */             }
/*      */             else
/*      */             {
/*      */               TypeBinding match;
/* 3458 */               if (erasedSuperType.isArrayType())
/* 3459 */                 match = null;
/*      */               else {
/* 3461 */                 match = otherType.findSuperTypeOriginatingFrom(erasedSuperType);
/*      */               }
/* 3463 */               if (match == null) {
/* 3464 */                 erasedSuperTypes[j] = null;
/* 3465 */                 remaining--; if (remaining != 0) continue; return null;
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/* 3470 */             Object invocationData = allInvocations.get(erasedSuperType);
/* 3471 */             if (invocationData == null) {
/* 3472 */               allInvocations.put(erasedSuperType, match);
/* 3473 */             } else if ((invocationData instanceof TypeBinding)) {
/* 3474 */               if (match == invocationData)
/*      */                 continue;
/* 3476 */               TypeBinding[] someInvocations = { (TypeBinding)invocationData, match };
/* 3477 */               allInvocations.put(erasedSuperType, someInvocations);
/*      */             }
/*      */             else {
/* 3480 */               TypeBinding[] someInvocations = (TypeBinding[])invocationData;
/*      */ 
/* 3482 */               int invocLength = someInvocations.length;
/* 3483 */               int k = 0;
/* 3484 */               while (someInvocations[k] != match)
/*      */               {
/* 3483 */                 k++; if (k < invocLength) {
/*      */                   continue;
/*      */                 }
/* 3486 */                 System.arraycopy(someInvocations, 0, someInvocations = new TypeBinding[invocLength + 1], 0, invocLength);
/* 3487 */                 allInvocations.put(erasedSuperType, someInvocations);
/* 3488 */                 someInvocations[invocLength] = match;
/*      */               }
/*      */             }
/*      */           }
/*      */       }
/*      */     }
/* 3494 */     if (remaining > 1) {
/* 3495 */       for (int i = 0; i < superLength; i++) {
/* 3496 */         TypeBinding erasedSuperType = erasedSuperTypes[i];
/* 3497 */         if (erasedSuperType != null) {
/* 3498 */           for (int j = 0; j < superLength; j++)
/* 3499 */             if (i != j) {
/* 3500 */               TypeBinding otherType = erasedSuperTypes[j];
/* 3501 */               if (otherType != null)
/* 3502 */                 if ((erasedSuperType instanceof ReferenceBinding)) {
/* 3503 */                   if (((otherType.id == 1) && (erasedSuperType.isInterface())) || 
/* 3504 */                     (erasedSuperType.findSuperTypeOriginatingFrom(otherType) == null)) continue;
/* 3505 */                   erasedSuperTypes[j] = null;
/* 3506 */                   remaining--;
/*      */                 } else {
/* 3508 */                   if ((!erasedSuperType.isArrayType()) || 
/* 3509 */                     ((otherType.isArrayType()) && 
/* 3510 */                     (otherType.leafComponentType().id == 1) && 
/* 3511 */                     (otherType.dimensions() == erasedSuperType.dimensions()) && 
/* 3512 */                     (erasedSuperType.leafComponentType().isInterface())) || 
/* 3513 */                     (erasedSuperType.findSuperTypeOriginatingFrom(otherType) == null)) continue;
/* 3514 */                   erasedSuperTypes[j] = null;
/* 3515 */                   remaining--;
/*      */                 }
/*      */             }
/*      */         }
/*      */       }
/*      */     }
/* 3521 */     return erasedSuperTypes;
/*      */   }
/*      */ 
/*      */   protected final MethodBinding mostSpecificClassMethodBinding(MethodBinding[] visible, int visibleSize, InvocationSite invocationSite)
/*      */   {
/* 3535 */     MethodBinding previous = null;
/* 3536 */     for (int i = 0; i < visibleSize; i++) {
/* 3537 */       MethodBinding method = visible[i];
/* 3538 */       if ((previous != null) && (method.declaringClass != previous.declaringClass)) {
/*      */         break;
/*      */       }
/* 3541 */       if (!method.isStatic()) previous = method;
/* 3542 */       int j = 0;
/* 3543 */       while ((i == j) || 
/* 3544 */         (visible[j].areParametersCompatibleWith(method.parameters)))
/*      */       {
/* 3542 */         j++; if (j < visibleSize)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/* 3547 */         compilationUnitScope().recordTypeReferences(method.thrownExceptions);
/* 3548 */         return method;
/*      */       }
/*      */     }
/* 3550 */     return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
/*      */   }
/*      */ 
/*      */   protected final MethodBinding mostSpecificInterfaceMethodBinding(MethodBinding[] visible, int visibleSize, InvocationSite invocationSite)
/*      */   {
/* 3583 */     for (int i = 0; i < visibleSize; i++) {
/* 3584 */       MethodBinding method = visible[i];
/* 3585 */       int j = 0;
/* 3586 */       while ((i == j) || 
/* 3587 */         (visible[j].areParametersCompatibleWith(method.parameters)))
/*      */       {
/* 3585 */         j++; if (j < visibleSize)
/*      */         {
/*      */           continue;
/*      */         }
/*      */ 
/* 3590 */         compilationUnitScope().recordTypeReferences(method.thrownExceptions);
/* 3591 */         return method;
/*      */       }
/*      */     }
/* 3593 */     return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
/*      */   }
/*      */ 
/*      */   protected final MethodBinding mostSpecificMethodBinding(MethodBinding[] visible, int visibleSize, TypeBinding[] argumentTypes, InvocationSite invocationSite, ReferenceBinding receiverType)
/*      */   {
/* 3598 */     int[] compatibilityLevels = new int[visibleSize];
/* 3599 */     for (int i = 0; i < visibleSize; i++) {
/* 3600 */       compatibilityLevels[i] = parameterCompatibilityLevel(visible[i], argumentTypes);
/*      */     }
/* 3602 */     InvocationSite tieBreakInvocationSite = new InvocationSite(invocationSite) { private final InvocationSite val$invocationSite;
/*      */ 
/* 3603 */       public TypeBinding[] genericTypeArguments() { return null; } 
/* 3604 */       public boolean isSuperAccess() { return this.val$invocationSite.isSuperAccess(); } 
/* 3605 */       public boolean isTypeAccess() { return this.val$invocationSite.isTypeAccess(); } 
/*      */       public void setActualReceiverType(ReferenceBinding actualReceiverType) {  }
/*      */ 
/*      */       public void setDepth(int depth) {  }
/*      */ 
/*      */       public void setFieldIndex(int depth) {  }
/*      */ 
/* 3609 */       public int sourceStart() { return this.val$invocationSite.sourceStart(); } 
/* 3610 */       public int sourceEnd() { return this.val$invocationSite.sourceStart();
/*      */       }
/*      */     };
/* 3612 */     MethodBinding[] moreSpecific = new MethodBinding[visibleSize];
/* 3613 */     int count = 0;
/* 3614 */     int level = 0; for (int max = 2; level <= max; level++)
/* 3615 */       for (int i = 0; i < visibleSize; i++)
/* 3616 */         if (compatibilityLevels[i] == level) {
/* 3617 */           max = level;
/* 3618 */           MethodBinding current = visible[i];
/* 3619 */           MethodBinding original = current.original();
/* 3620 */           MethodBinding tiebreakMethod = current.tiebreakMethod();
/* 3621 */           int j = 0;
/*      */           while (true) { if ((i != j) && (compatibilityLevels[j] == level)) {
/* 3623 */               MethodBinding next = visible[j];
/* 3624 */               if (original == next.original())
/*      */               {
/* 3626 */                 compatibilityLevels[j] = -1;
/*      */               }
/*      */               else
/*      */               {
/* 3630 */                 MethodBinding methodToTest = next;
/* 3631 */                 if ((next instanceof ParameterizedGenericMethodBinding)) {
/* 3632 */                   ParameterizedGenericMethodBinding pNext = (ParameterizedGenericMethodBinding)next;
/* 3633 */                   if ((!pNext.isRaw) || (pNext.isStatic()))
/*      */                   {
/* 3636 */                     methodToTest = pNext.originalMethod;
/*      */                   }
/*      */                 }
/* 3639 */                 MethodBinding acceptable = computeCompatibleMethod(methodToTest, tiebreakMethod.parameters, tieBreakInvocationSite);
/*      */ 
/* 3647 */                 if ((acceptable == null) || (!acceptable.isValidBinding()))
/*      */                   break;
/* 3649 */                 if (!isAcceptableMethod(tiebreakMethod, acceptable)) {
/*      */                   break;
/*      */                 }
/* 3652 */                 if ((current.isBridge()) && (!next.isBridge()) && 
/* 3653 */                   (tiebreakMethod.areParametersEqual(acceptable)))
/*      */                   break;
/*      */               }
/*      */             }
/* 3621 */             j++; if (j < visibleSize)
/*      */             {
/*      */               continue;
/*      */             }
/*      */ 
/* 3656 */             moreSpecific[i] = current;
/* 3657 */             count++;
/*      */           }
/*      */         }
/* 3660 */     if (count == 1) {
/* 3661 */       for (int i = 0; i < visibleSize; i++)
/* 3662 */         if (moreSpecific[i] != null) {
/* 3663 */           compilationUnitScope().recordTypeReferences(visible[i].thrownExceptions);
/* 3664 */           return visible[i];
/*      */         }
/*      */     }
/* 3667 */     else if (count == 0) {
/* 3668 */       return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
/*      */     }
/*      */ 
/* 3674 */     if (receiverType != null)
/* 3675 */       receiverType = (receiverType instanceof CaptureBinding) ? receiverType : (ReferenceBinding)receiverType.erasure();
/* 3676 */     for (int i = 0; i < visibleSize; i++) {
/* 3677 */       MethodBinding current = moreSpecific[i];
/* 3678 */       if (current != null) {
/* 3679 */         ReferenceBinding[] mostSpecificExceptions = (ReferenceBinding[])null;
/* 3680 */         MethodBinding original = current.original();
/* 3681 */         boolean shouldIntersectExceptions = (original.declaringClass.isAbstract()) && (original.thrownExceptions != Binding.NO_EXCEPTIONS);
/* 3682 */         int j = 0;
/*      */         while (true) { MethodBinding next = moreSpecific[j];
/* 3684 */           if ((next != null) && (i != j)) {
/* 3685 */             MethodBinding original2 = next.original();
/* 3686 */             if (original.declaringClass == original2.declaringClass)
/*      */               break label1180;
/* 3689 */             if (!original.isAbstract()) {
/* 3690 */               if (!original2.isAbstract())
/*      */               {
/* 3693 */                 original2 = original.findOriginalInheritedMethod(original2);
/* 3694 */                 if (original2 == null)
/*      */                   break;
/* 3696 */                 if (((current.hasSubstitutedParameters()) || (original.typeVariables != Binding.NO_TYPE_VARIABLES)) && 
/* 3697 */                   (!environment().methodVerifier().isParameterSubsignature(original, original2)))
/* 3698 */                   break;
/*      */               }
/* 3700 */             } else if (receiverType != null) {
/* 3701 */               TypeBinding superType = receiverType.findSuperTypeOriginatingFrom(original.declaringClass.erasure());
/* 3702 */               if ((original.declaringClass != superType) && ((superType instanceof ReferenceBinding)))
/*      */               {
/* 3706 */                 MethodBinding[] superMethods = ((ReferenceBinding)superType).getMethods(original.selector, argumentTypes.length);
/* 3707 */                 int m = 0; for (int l = superMethods.length; m < l; m++) {
/* 3708 */                   if (superMethods[m].original() == original) {
/* 3709 */                     original = superMethods[m];
/* 3710 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/* 3714 */               superType = receiverType.findSuperTypeOriginatingFrom(original2.declaringClass.erasure());
/* 3715 */               if ((original2.declaringClass != superType) && ((superType instanceof ReferenceBinding)))
/*      */               {
/* 3719 */                 MethodBinding[] superMethods = ((ReferenceBinding)superType).getMethods(original2.selector, argumentTypes.length);
/* 3720 */                 int m = 0; for (int l = superMethods.length; m < l; m++) {
/* 3721 */                   if (superMethods[m].original() == original2) {
/* 3722 */                     original2 = superMethods[m];
/* 3723 */                     break;
/*      */                   }
/*      */                 }
/*      */               }
/* 3727 */               if (original.typeVariables != Binding.NO_TYPE_VARIABLES)
/* 3728 */                 original2 = original.computeSubstitutedMethod(original2, environment());
/* 3729 */               if ((original2 == null) || (!original.areParameterErasuresEqual(original2)))
/*      */                 break;
/* 3731 */               if ((original.returnType != original2.returnType) && 
/* 3732 */                 (next.original().typeVariables != Binding.NO_TYPE_VARIABLES ? 
/* 3733 */                 original.returnType.erasure().findSuperTypeOriginatingFrom(original2.returnType.erasure()) == null : 
/* 3735 */                 !current.returnType.isCompatibleWith(next.returnType)))
/*      */               {
/*      */                 break;
/*      */               }
/*      */ 
/* 3740 */               if ((shouldIntersectExceptions) && (original2.declaringClass.isInterface()) && 
/* 3741 */                 (current.thrownExceptions != next.thrownExceptions))
/* 3742 */                 if (next.thrownExceptions == Binding.NO_EXCEPTIONS) {
/* 3743 */                   mostSpecificExceptions = Binding.NO_EXCEPTIONS;
/*      */                 } else {
/* 3745 */                   if (mostSpecificExceptions == null) {
/* 3746 */                     mostSpecificExceptions = current.thrownExceptions;
/*      */                   }
/* 3748 */                   int mostSpecificLength = mostSpecificExceptions.length;
/* 3749 */                   int nextLength = next.thrownExceptions.length;
/* 3750 */                   SimpleSet temp = new SimpleSet(mostSpecificLength);
/* 3751 */                   boolean changed = false;
/* 3752 */                   for (int t = 0; t < mostSpecificLength; t++) {
/* 3753 */                     ReferenceBinding exception = mostSpecificExceptions[t];
/* 3754 */                     for (int s = 0; s < nextLength; s++) {
/* 3755 */                       ReferenceBinding nextException = next.thrownExceptions[s];
/* 3756 */                       if (exception.isCompatibleWith(nextException)) {
/* 3757 */                         temp.add(exception);
/* 3758 */                         break;
/* 3759 */                       }if (nextException.isCompatibleWith(exception)) {
/* 3760 */                         temp.add(nextException);
/* 3761 */                         changed = true;
/* 3762 */                         break;
/*      */                       }
/* 3764 */                       changed = true;
/*      */                     }
/*      */                   }
/*      */ 
/* 3768 */                   if (changed) {
/* 3769 */                     mostSpecificExceptions = temp.elementSize == 0 ? Binding.NO_EXCEPTIONS : new ReferenceBinding[temp.elementSize];
/* 3770 */                     temp.asArray(mostSpecificExceptions);
/*      */                   }
/*      */                 }
/*      */             }
/*      */           }
/* 3682 */           j++; if (j < visibleSize)
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/* 3777 */           if ((mostSpecificExceptions != null) && (mostSpecificExceptions != current.thrownExceptions)) {
/* 3778 */             return new MostSpecificExceptionMethodBinding(current, mostSpecificExceptions);
/*      */           }
/* 3780 */           return current;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3785 */     label1180: return new ProblemMethodBinding(visible[0], visible[0].selector, visible[0].parameters, 3);
/*      */   }
/*      */ 
/*      */   public final ClassScope outerMostClassScope() {
/* 3789 */     ClassScope lastClassScope = null;
/* 3790 */     Scope scope = this;
/*      */     do {
/* 3792 */       if ((scope instanceof ClassScope))
/* 3793 */         lastClassScope = (ClassScope)scope;
/* 3794 */       scope = scope.parent;
/* 3795 */     }while (scope != null);
/* 3796 */     return lastClassScope;
/*      */   }
/*      */ 
/*      */   public final MethodScope outerMostMethodScope() {
/* 3800 */     MethodScope lastMethodScope = null;
/* 3801 */     Scope scope = this;
/*      */     do {
/* 3803 */       if ((scope instanceof MethodScope))
/* 3804 */         lastMethodScope = (MethodScope)scope;
/* 3805 */       scope = scope.parent;
/* 3806 */     }while (scope != null);
/* 3807 */     return lastMethodScope;
/*      */   }
/*      */ 
/*      */   public int parameterCompatibilityLevel(MethodBinding method, TypeBinding[] arguments) {
/* 3811 */     TypeBinding[] parameters = method.parameters;
/* 3812 */     int paramLength = parameters.length;
/* 3813 */     int argLength = arguments.length;
/*      */ 
/* 3815 */     if (compilerOptions().sourceLevel < 3211264L) {
/* 3816 */       if (paramLength != argLength)
/* 3817 */         return -1;
/* 3818 */       for (int i = 0; i < argLength; i++) {
/* 3819 */         TypeBinding param = parameters[i];
/* 3820 */         TypeBinding arg = arguments[i];
/* 3821 */         if ((arg != param) && (!arg.isCompatibleWith(param)))
/* 3822 */           return -1;
/*      */       }
/* 3824 */       return 0;
/*      */     }
/*      */ 
/* 3827 */     int level = 0;
/* 3828 */     int lastIndex = argLength;
/* 3829 */     LookupEnvironment env = environment();
/* 3830 */     if (method.isVarargs()) {
/* 3831 */       lastIndex = paramLength - 1;
/* 3832 */       if (paramLength == argLength) {
/* 3833 */         TypeBinding param = parameters[lastIndex];
/* 3834 */         TypeBinding arg = arguments[lastIndex];
/* 3835 */         if (param != arg) {
/* 3836 */           level = parameterCompatibilityLevel(arg, param, env);
/* 3837 */           if (level == -1)
/*      */           {
/* 3839 */             param = ((ArrayBinding)param).elementsType();
/* 3840 */             if (parameterCompatibilityLevel(arg, param, env) == -1)
/* 3841 */               return -1;
/* 3842 */             level = 2;
/*      */           }
/*      */         }
/*      */       } else {
/* 3846 */         if (paramLength < argLength) {
/* 3847 */           TypeBinding param = ((ArrayBinding)parameters[lastIndex]).elementsType();
/* 3848 */           for (int i = lastIndex; i < argLength; i++) {
/* 3849 */             TypeBinding arg = arguments[i];
/* 3850 */             if ((param != arg) && (parameterCompatibilityLevel(arg, param, env) == -1))
/* 3851 */               return -1;
/*      */           }
/* 3853 */         } else if (lastIndex != argLength) {
/* 3854 */           return -1;
/*      */         }
/* 3856 */         level = 2;
/*      */       }
/* 3858 */     } else if (paramLength != argLength) {
/* 3859 */       return -1;
/*      */     }
/*      */ 
/* 3862 */     for (int i = 0; i < lastIndex; i++) {
/* 3863 */       TypeBinding param = parameters[i];
/* 3864 */       TypeBinding arg = arguments[i];
/* 3865 */       if (arg != param) {
/* 3866 */         int newLevel = parameterCompatibilityLevel(arg, param, env);
/* 3867 */         if (newLevel == -1)
/* 3868 */           return -1;
/* 3869 */         if (newLevel > level)
/* 3870 */           level = newLevel;
/*      */       }
/*      */     }
/* 3873 */     return level;
/*      */   }
/*      */ 
/*      */   private int parameterCompatibilityLevel(TypeBinding arg, TypeBinding param, LookupEnvironment env)
/*      */   {
/* 3878 */     if (arg.isCompatibleWith(param))
/* 3879 */       return 0;
/* 3880 */     if (arg.isBaseType() != param.isBaseType()) {
/* 3881 */       TypeBinding convertedType = env.computeBoxingType(arg);
/* 3882 */       if ((convertedType == param) || (convertedType.isCompatibleWith(param)))
/* 3883 */         return 1;
/*      */     }
/* 3885 */     return -1;
/*      */   }
/*      */   public abstract ProblemReporter problemReporter();
/*      */ 
/*      */   public final CompilationUnitDeclaration referenceCompilationUnit() {
/* 3891 */     Scope unitScope = this;
/*      */     Scope scope;
/* 3892 */     while ((scope = unitScope.parent) != null)
/*      */     {
/*      */       Scope scope;
/* 3893 */       unitScope = scope;
/* 3894 */     }return ((CompilationUnitScope)unitScope).referenceContext;
/*      */   }
/*      */ 
/*      */   public ReferenceContext referenceContext()
/*      */   {
/* 3902 */     Scope current = this;
/*      */     do
/* 3904 */       switch (current.kind) {
/*      */       case 2:
/* 3906 */         return ((MethodScope)current).referenceContext;
/*      */       case 3:
/* 3908 */         return ((ClassScope)current).referenceContext;
/*      */       case 4:
/* 3910 */         return ((CompilationUnitScope)current).referenceContext;
/*      */       }
/* 3912 */     while ((current = current.parent) != null);
/*      */ 
/* 3913 */     return null;
/*      */   }
/*      */ 
/*      */   public void deferBoundCheck(TypeReference typeRef) {
/* 3917 */     if (this.kind == 3) {
/* 3918 */       ClassScope classScope = (ClassScope)this;
/* 3919 */       if (classScope.deferredBoundChecks == null) {
/* 3920 */         classScope.deferredBoundChecks = new ArrayList(3);
/* 3921 */         classScope.deferredBoundChecks.add(typeRef);
/* 3922 */       } else if (!classScope.deferredBoundChecks.contains(typeRef)) {
/* 3923 */         classScope.deferredBoundChecks.add(typeRef);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   int startIndex()
/*      */   {
/* 3930 */     return 0;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.Scope
 * JD-Core Version:    0.6.0
 */