/*      */ package org.eclipse.jdt.internal.compiler;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
/*      */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.CharArrayCache;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.StackMapFrame;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream.ExceptionMarker;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream.StackDepthMarker;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream.StackMarker;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.VerificationTypeInfo;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.impl.StringConstant;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ShouldNotImplement;
/*      */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class ClassFile
/*      */   implements TypeConstants, TypeIds
/*      */ {
/*      */   private byte[] bytes;
/*      */   public CodeStream codeStream;
/*      */   public ConstantPool constantPool;
/*      */   public int constantPoolOffset;
/*      */   public byte[] contents;
/*      */   public int contentsOffset;
/*      */   protected boolean creatingProblemType;
/*      */   public ClassFile enclosingClassFile;
/*      */   public byte[] header;
/*      */   public int headerOffset;
/*      */   public Set innerClassesBindings;
/*      */   public int methodCount;
/*      */   public int methodCountOffset;
/*  117 */   boolean isShared = false;
/*      */   public int produceAttributes;
/*      */   public SourceTypeBinding referenceBinding;
/*      */   public boolean isNestedType;
/*      */   public long targetJDK;
/*  125 */   public List missingTypes = null;
/*      */   public Set visitedTypes;
/*      */   public static final int INITIAL_CONTENTS_SIZE = 400;
/*      */   public static final int INITIAL_HEADER_SIZE = 1500;
/*      */   public static final int INNER_CLASSES_SIZE = 5;
/*      */ 
/*      */   public static void createProblemType(TypeDeclaration typeDeclaration, CompilationResult unitResult)
/*      */   {
/*  141 */     SourceTypeBinding typeBinding = typeDeclaration.binding;
/*  142 */     ClassFile classFile = getNewInstance(typeBinding);
/*  143 */     classFile.initialize(typeBinding, null, true);
/*      */ 
/*  145 */     if (typeBinding.hasMemberTypes())
/*      */     {
/*  147 */       ReferenceBinding[] members = typeBinding.memberTypes;
/*  148 */       int i = 0; for (int l = members.length; i < l; i++) {
/*  149 */         classFile.recordInnerClasses(members[i]);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  154 */     if (typeBinding.isNestedType()) {
/*  155 */       classFile.recordInnerClasses(typeBinding);
/*      */     }
/*  157 */     TypeVariableBinding[] typeVariables = typeBinding.typeVariables();
/*  158 */     int i = 0; for (int max = typeVariables.length; i < max; i++) {
/*  159 */       TypeVariableBinding typeVariableBinding = typeVariables[i];
/*  160 */       if ((typeVariableBinding.tagBits & 0x800) != 0L) {
/*  161 */         Util.recordNestedType(classFile, typeVariableBinding);
/*      */       }
/*      */     }
/*      */ 
/*  165 */     FieldBinding[] fields = typeBinding.fields();
/*  166 */     if ((fields != null) && (fields != Binding.NO_FIELDS)) {
/*  167 */       classFile.addFieldInfos();
/*      */     }
/*      */     else {
/*  170 */       classFile.contents[(classFile.contentsOffset++)] = 0;
/*  171 */       classFile.contents[(classFile.contentsOffset++)] = 0;
/*      */     }
/*      */ 
/*  174 */     classFile.setForMethodInfos();
/*      */ 
/*  177 */     CategorizedProblem[] problems = unitResult.getErrors();
/*  178 */     if (problems == null)
/*  179 */       problems = new CategorizedProblem[0];
/*      */     int problemsLength;
/*  181 */     CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
/*  182 */     System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
/*      */ 
/*  184 */     AbstractMethodDeclaration[] methodDecls = typeDeclaration.methods;
/*  185 */     if (methodDecls != null) {
/*  186 */       if (typeBinding.isInterface())
/*      */       {
/*  189 */         classFile.addProblemClinit(problemsCopy);
/*  190 */         int i = 0; for (int length = methodDecls.length; i < length; i++) {
/*  191 */           AbstractMethodDeclaration methodDecl = methodDecls[i];
/*  192 */           MethodBinding method = methodDecl.binding;
/*  193 */           if ((method != null) && (!method.isConstructor()))
/*  194 */             classFile.addAbstractMethod(methodDecl, method);
/*      */         }
/*      */       } else {
/*  197 */         int i = 0; for (int length = methodDecls.length; i < length; i++) {
/*  198 */           AbstractMethodDeclaration methodDecl = methodDecls[i];
/*  199 */           MethodBinding method = methodDecl.binding;
/*  200 */           if (method != null) {
/*  201 */             if (method.isConstructor())
/*  202 */               classFile.addProblemConstructor(methodDecl, method, problemsCopy);
/*  203 */             else if (method.isAbstract())
/*  204 */               classFile.addAbstractMethod(methodDecl, method);
/*      */             else {
/*  206 */               classFile.addProblemMethod(methodDecl, method, problemsCopy);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  211 */       classFile.addDefaultAbstractMethods();
/*      */     }
/*      */ 
/*  216 */     if (typeDeclaration.memberTypes != null) {
/*  217 */       int i = 0; for (int max = typeDeclaration.memberTypes.length; i < max; i++) {
/*  218 */         TypeDeclaration memberType = typeDeclaration.memberTypes[i];
/*  219 */         if (memberType.binding != null) {
/*  220 */           createProblemType(memberType, unitResult);
/*      */         }
/*      */       }
/*      */     }
/*  224 */     classFile.addAttributes();
/*  225 */     unitResult.record(typeBinding.constantPoolName(), classFile);
/*      */   }
/*      */   public static ClassFile getNewInstance(SourceTypeBinding typeBinding) {
/*  228 */     LookupEnvironment env = typeBinding.scope.environment();
/*  229 */     return env.classFilePool.acquire(typeBinding);
/*      */   }
/*      */ 
/*      */   protected ClassFile()
/*      */   {
/*      */   }
/*      */ 
/*      */   public ClassFile(SourceTypeBinding typeBinding)
/*      */   {
/*  242 */     this.constantPool = new ConstantPool(this);
/*  243 */     CompilerOptions options = typeBinding.scope.compilerOptions();
/*  244 */     this.targetJDK = options.targetJDK;
/*  245 */     this.produceAttributes = options.produceDebugAttributes;
/*  246 */     this.referenceBinding = typeBinding;
/*  247 */     this.isNestedType = typeBinding.isNestedType();
/*  248 */     if (this.targetJDK >= 3276800L) {
/*  249 */       this.produceAttributes |= 8;
/*  250 */       this.codeStream = new StackMapFrameCodeStream(this);
/*  251 */     } else if (this.targetJDK == 2949124L) {
/*  252 */       this.targetJDK = 2949123L;
/*  253 */       this.produceAttributes |= 16;
/*  254 */       this.codeStream = new StackMapFrameCodeStream(this);
/*      */     } else {
/*  256 */       this.codeStream = new CodeStream(this);
/*      */     }
/*  258 */     initByteArrays();
/*      */   }
/*      */ 
/*      */   public void addAbstractMethod(AbstractMethodDeclaration method, MethodBinding methodBinding)
/*      */   {
/*  273 */     methodBinding.modifiers = 1025;
/*      */ 
/*  275 */     generateMethodInfoHeader(methodBinding);
/*  276 */     int methodAttributeOffset = this.contentsOffset;
/*  277 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*  278 */     completeMethodInfo(methodAttributeOffset, attributeNumber);
/*      */   }
/*      */ 
/*      */   public void addAttributes()
/*      */   {
/*  291 */     this.contents[(this.methodCountOffset++)] = (byte)(this.methodCount >> 8);
/*  292 */     this.contents[this.methodCountOffset] = (byte)this.methodCount;
/*      */ 
/*  294 */     int attributesNumber = 0;
/*      */ 
/*  296 */     int attributeOffset = this.contentsOffset;
/*  297 */     this.contentsOffset += 2;
/*      */ 
/*  300 */     if ((this.produceAttributes & 0x1) != 0) {
/*  301 */       String fullFileName = 
/*  302 */         new String(this.referenceBinding.scope.referenceCompilationUnit().getFileName());
/*  303 */       fullFileName = fullFileName.replace('\\', '/');
/*  304 */       int lastIndex = fullFileName.lastIndexOf('/');
/*  305 */       if (lastIndex != -1) {
/*  306 */         fullFileName = fullFileName.substring(lastIndex + 1, fullFileName.length());
/*      */       }
/*      */ 
/*  310 */       if (this.contentsOffset + 8 >= this.contents.length) {
/*  311 */         resizeContents(8);
/*      */       }
/*  313 */       int sourceAttributeNameIndex = 
/*  314 */         this.constantPool.literalIndex(AttributeNamesConstants.SourceName);
/*  315 */       this.contents[(this.contentsOffset++)] = (byte)(sourceAttributeNameIndex >> 8);
/*  316 */       this.contents[(this.contentsOffset++)] = (byte)sourceAttributeNameIndex;
/*      */ 
/*  319 */       this.contents[(this.contentsOffset++)] = 0;
/*  320 */       this.contents[(this.contentsOffset++)] = 0;
/*  321 */       this.contents[(this.contentsOffset++)] = 0;
/*  322 */       this.contents[(this.contentsOffset++)] = 2;
/*      */ 
/*  324 */       int fileNameIndex = this.constantPool.literalIndex(fullFileName.toCharArray());
/*  325 */       this.contents[(this.contentsOffset++)] = (byte)(fileNameIndex >> 8);
/*  326 */       this.contents[(this.contentsOffset++)] = (byte)fileNameIndex;
/*  327 */       attributesNumber++;
/*      */     }
/*      */ 
/*  330 */     if (this.referenceBinding.isDeprecated())
/*      */     {
/*  333 */       if (this.contentsOffset + 6 >= this.contents.length) {
/*  334 */         resizeContents(6);
/*      */       }
/*  336 */       int deprecatedAttributeNameIndex = 
/*  337 */         this.constantPool.literalIndex(AttributeNamesConstants.DeprecatedName);
/*  338 */       this.contents[(this.contentsOffset++)] = (byte)(deprecatedAttributeNameIndex >> 8);
/*  339 */       this.contents[(this.contentsOffset++)] = (byte)deprecatedAttributeNameIndex;
/*      */ 
/*  341 */       this.contents[(this.contentsOffset++)] = 0;
/*  342 */       this.contents[(this.contentsOffset++)] = 0;
/*  343 */       this.contents[(this.contentsOffset++)] = 0;
/*  344 */       this.contents[(this.contentsOffset++)] = 0;
/*  345 */       attributesNumber++;
/*      */     }
/*      */ 
/*  348 */     char[] genericSignature = this.referenceBinding.genericSignature();
/*  349 */     if (genericSignature != null)
/*      */     {
/*  352 */       if (this.contentsOffset + 8 >= this.contents.length) {
/*  353 */         resizeContents(8);
/*      */       }
/*  355 */       int signatureAttributeNameIndex = 
/*  356 */         this.constantPool.literalIndex(AttributeNamesConstants.SignatureName);
/*  357 */       this.contents[(this.contentsOffset++)] = (byte)(signatureAttributeNameIndex >> 8);
/*  358 */       this.contents[(this.contentsOffset++)] = (byte)signatureAttributeNameIndex;
/*      */ 
/*  360 */       this.contents[(this.contentsOffset++)] = 0;
/*  361 */       this.contents[(this.contentsOffset++)] = 0;
/*  362 */       this.contents[(this.contentsOffset++)] = 0;
/*  363 */       this.contents[(this.contentsOffset++)] = 2;
/*  364 */       int signatureIndex = 
/*  365 */         this.constantPool.literalIndex(genericSignature);
/*  366 */       this.contents[(this.contentsOffset++)] = (byte)(signatureIndex >> 8);
/*  367 */       this.contents[(this.contentsOffset++)] = (byte)signatureIndex;
/*  368 */       attributesNumber++;
/*      */     }
/*  370 */     if ((this.targetJDK >= 3211264L) && 
/*  371 */       (this.referenceBinding.isNestedType()) && 
/*  372 */       (!this.referenceBinding.isMemberType()))
/*      */     {
/*  374 */       if (this.contentsOffset + 10 >= this.contents.length) {
/*  375 */         resizeContents(10);
/*      */       }
/*  377 */       int enclosingMethodAttributeNameIndex = 
/*  378 */         this.constantPool.literalIndex(AttributeNamesConstants.EnclosingMethodName);
/*  379 */       this.contents[(this.contentsOffset++)] = (byte)(enclosingMethodAttributeNameIndex >> 8);
/*  380 */       this.contents[(this.contentsOffset++)] = (byte)enclosingMethodAttributeNameIndex;
/*      */ 
/*  382 */       this.contents[(this.contentsOffset++)] = 0;
/*  383 */       this.contents[(this.contentsOffset++)] = 0;
/*  384 */       this.contents[(this.contentsOffset++)] = 0;
/*  385 */       this.contents[(this.contentsOffset++)] = 4;
/*      */ 
/*  387 */       int enclosingTypeIndex = this.constantPool.literalIndexForType(this.referenceBinding.enclosingType().constantPoolName());
/*  388 */       this.contents[(this.contentsOffset++)] = (byte)(enclosingTypeIndex >> 8);
/*  389 */       this.contents[(this.contentsOffset++)] = (byte)enclosingTypeIndex;
/*  390 */       byte methodIndexByte1 = 0;
/*  391 */       byte methodIndexByte2 = 0;
/*  392 */       if ((this.referenceBinding instanceof LocalTypeBinding)) {
/*  393 */         MethodBinding methodBinding = ((LocalTypeBinding)this.referenceBinding).enclosingMethod;
/*  394 */         if (methodBinding != null) {
/*  395 */           int enclosingMethodIndex = this.constantPool.literalIndexForNameAndType(methodBinding.selector, methodBinding.signature(this));
/*  396 */           methodIndexByte1 = (byte)(enclosingMethodIndex >> 8);
/*  397 */           methodIndexByte2 = (byte)enclosingMethodIndex;
/*      */         }
/*      */       }
/*  400 */       this.contents[(this.contentsOffset++)] = methodIndexByte1;
/*  401 */       this.contents[(this.contentsOffset++)] = methodIndexByte2;
/*  402 */       attributesNumber++;
/*      */     }
/*  404 */     if (this.targetJDK >= 3211264L) {
/*  405 */       TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
/*  406 */       if (typeDeclaration != null) {
/*  407 */         Annotation[] annotations = typeDeclaration.annotations;
/*  408 */         if (annotations != null) {
/*  409 */           attributesNumber += generateRuntimeAnnotations(annotations);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  414 */     if (this.referenceBinding.isHierarchyInconsistent()) {
/*  415 */       ReferenceBinding superclass = this.referenceBinding.superclass;
/*  416 */       if (superclass != null) {
/*  417 */         this.missingTypes = superclass.collectMissingTypes(this.missingTypes);
/*      */       }
/*  419 */       ReferenceBinding[] superInterfaces = this.referenceBinding.superInterfaces();
/*  420 */       int i = 0; for (int max = superInterfaces.length; i < max; i++) {
/*  421 */         this.missingTypes = superInterfaces[i].collectMissingTypes(this.missingTypes);
/*      */       }
/*      */ 
/*  424 */       if (this.contentsOffset + 6 >= this.contents.length) {
/*  425 */         resizeContents(6);
/*      */       }
/*  427 */       int inconsistentHierarchyNameIndex = 
/*  428 */         this.constantPool.literalIndex(AttributeNamesConstants.InconsistentHierarchy);
/*  429 */       this.contents[(this.contentsOffset++)] = (byte)(inconsistentHierarchyNameIndex >> 8);
/*  430 */       this.contents[(this.contentsOffset++)] = (byte)inconsistentHierarchyNameIndex;
/*      */ 
/*  432 */       this.contents[(this.contentsOffset++)] = 0;
/*  433 */       this.contents[(this.contentsOffset++)] = 0;
/*  434 */       this.contents[(this.contentsOffset++)] = 0;
/*  435 */       this.contents[(this.contentsOffset++)] = 0;
/*  436 */       attributesNumber++;
/*      */     }
/*      */ 
/*  439 */     int numberOfInnerClasses = this.innerClassesBindings == null ? 0 : this.innerClassesBindings.size();
/*  440 */     if (numberOfInnerClasses != 0) {
/*  441 */       ReferenceBinding[] innerClasses = new ReferenceBinding[numberOfInnerClasses];
/*  442 */       this.innerClassesBindings.toArray(innerClasses);
/*  443 */       Arrays.sort(innerClasses, new Comparator() {
/*      */         public int compare(Object o1, Object o2) {
/*  445 */           TypeBinding binding1 = (TypeBinding)o1;
/*  446 */           TypeBinding binding2 = (TypeBinding)o2;
/*  447 */           return CharOperation.compareTo(binding1.constantPoolName(), binding2.constantPoolName());
/*      */         }
/*      */       });
/*  451 */       int exSize = 8 * numberOfInnerClasses + 8;
/*  452 */       if (exSize + this.contentsOffset >= this.contents.length) {
/*  453 */         resizeContents(exSize);
/*      */       }
/*      */ 
/*  457 */       int attributeNameIndex = 
/*  458 */         this.constantPool.literalIndex(AttributeNamesConstants.InnerClassName);
/*  459 */       this.contents[(this.contentsOffset++)] = (byte)(attributeNameIndex >> 8);
/*  460 */       this.contents[(this.contentsOffset++)] = (byte)attributeNameIndex;
/*  461 */       int value = (numberOfInnerClasses << 3) + 2;
/*  462 */       this.contents[(this.contentsOffset++)] = (byte)(value >> 24);
/*  463 */       this.contents[(this.contentsOffset++)] = (byte)(value >> 16);
/*  464 */       this.contents[(this.contentsOffset++)] = (byte)(value >> 8);
/*  465 */       this.contents[(this.contentsOffset++)] = (byte)value;
/*  466 */       this.contents[(this.contentsOffset++)] = (byte)(numberOfInnerClasses >> 8);
/*  467 */       this.contents[(this.contentsOffset++)] = (byte)numberOfInnerClasses;
/*  468 */       for (int i = 0; i < numberOfInnerClasses; i++) {
/*  469 */         ReferenceBinding innerClass = innerClasses[i];
/*  470 */         int accessFlags = innerClass.getAccessFlags();
/*  471 */         int innerClassIndex = this.constantPool.literalIndexForType(innerClass.constantPoolName());
/*      */ 
/*  473 */         this.contents[(this.contentsOffset++)] = (byte)(innerClassIndex >> 8);
/*  474 */         this.contents[(this.contentsOffset++)] = (byte)innerClassIndex;
/*      */ 
/*  476 */         if (innerClass.isMemberType())
/*      */         {
/*  478 */           int outerClassIndex = this.constantPool.literalIndexForType(innerClass.enclosingType().constantPoolName());
/*  479 */           this.contents[(this.contentsOffset++)] = (byte)(outerClassIndex >> 8);
/*  480 */           this.contents[(this.contentsOffset++)] = (byte)outerClassIndex;
/*      */         }
/*      */         else {
/*  483 */           this.contents[(this.contentsOffset++)] = 0;
/*  484 */           this.contents[(this.contentsOffset++)] = 0;
/*      */         }
/*      */ 
/*  487 */         if (!innerClass.isAnonymousType()) {
/*  488 */           int nameIndex = this.constantPool.literalIndex(innerClass.sourceName());
/*  489 */           this.contents[(this.contentsOffset++)] = (byte)(nameIndex >> 8);
/*  490 */           this.contents[(this.contentsOffset++)] = (byte)nameIndex;
/*      */         }
/*      */         else {
/*  493 */           this.contents[(this.contentsOffset++)] = 0;
/*  494 */           this.contents[(this.contentsOffset++)] = 0;
/*      */         }
/*      */ 
/*  497 */         if (innerClass.isAnonymousType())
/*  498 */           accessFlags &= -17;
/*  499 */         else if ((innerClass.isMemberType()) && (innerClass.isInterface())) {
/*  500 */           accessFlags |= 8;
/*      */         }
/*  502 */         this.contents[(this.contentsOffset++)] = (byte)(accessFlags >> 8);
/*  503 */         this.contents[(this.contentsOffset++)] = (byte)accessFlags;
/*      */       }
/*  505 */       attributesNumber++;
/*      */     }
/*  507 */     if (this.missingTypes != null) {
/*  508 */       generateMissingTypesAttribute();
/*  509 */       attributesNumber++;
/*      */     }
/*      */ 
/*  512 */     if (attributeOffset + 2 >= this.contents.length) {
/*  513 */       resizeContents(2);
/*      */     }
/*  515 */     this.contents[(attributeOffset++)] = (byte)(attributesNumber >> 8);
/*  516 */     this.contents[attributeOffset] = (byte)attributesNumber;
/*      */ 
/*  519 */     this.header = this.constantPool.poolContent;
/*  520 */     this.headerOffset = this.constantPool.currentOffset;
/*  521 */     int constantPoolCount = this.constantPool.currentIndex;
/*  522 */     this.header[(this.constantPoolOffset++)] = (byte)(constantPoolCount >> 8);
/*  523 */     this.header[this.constantPoolOffset] = (byte)constantPoolCount;
/*      */   }
/*      */ 
/*      */   public void addDefaultAbstractMethods()
/*      */   {
/*  532 */     MethodBinding[] defaultAbstractMethods = 
/*  533 */       this.referenceBinding.getDefaultAbstractMethods();
/*  534 */     int i = 0; for (int max = defaultAbstractMethods.length; i < max; i++) {
/*  535 */       generateMethodInfoHeader(defaultAbstractMethods[i]);
/*  536 */       int methodAttributeOffset = this.contentsOffset;
/*  537 */       int attributeNumber = generateMethodInfoAttribute(defaultAbstractMethods[i]);
/*  538 */       completeMethodInfo(methodAttributeOffset, attributeNumber);
/*      */     }
/*      */   }
/*      */ 
/*      */   private int addFieldAttributes(FieldBinding fieldBinding, int fieldAttributeOffset) {
/*  543 */     int attributesNumber = 0;
/*      */ 
/*  546 */     Constant fieldConstant = fieldBinding.constant();
/*  547 */     if (fieldConstant != Constant.NotAConstant) {
/*  548 */       if (this.contentsOffset + 8 >= this.contents.length) {
/*  549 */         resizeContents(8);
/*      */       }
/*      */ 
/*  552 */       int constantValueNameIndex = 
/*  553 */         this.constantPool.literalIndex(AttributeNamesConstants.ConstantValueName);
/*  554 */       this.contents[(this.contentsOffset++)] = (byte)(constantValueNameIndex >> 8);
/*  555 */       this.contents[(this.contentsOffset++)] = (byte)constantValueNameIndex;
/*      */ 
/*  557 */       this.contents[(this.contentsOffset++)] = 0;
/*  558 */       this.contents[(this.contentsOffset++)] = 0;
/*  559 */       this.contents[(this.contentsOffset++)] = 0;
/*  560 */       this.contents[(this.contentsOffset++)] = 2;
/*  561 */       attributesNumber++;
/*      */ 
/*  563 */       switch (fieldConstant.typeID()) {
/*      */       case 5:
/*  565 */         int booleanValueIndex = 
/*  566 */           this.constantPool.literalIndex(fieldConstant.booleanValue() ? 1 : 0);
/*  567 */         this.contents[(this.contentsOffset++)] = (byte)(booleanValueIndex >> 8);
/*  568 */         this.contents[(this.contentsOffset++)] = (byte)booleanValueIndex;
/*  569 */         break;
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 10:
/*  574 */         int integerValueIndex = 
/*  575 */           this.constantPool.literalIndex(fieldConstant.intValue());
/*  576 */         this.contents[(this.contentsOffset++)] = (byte)(integerValueIndex >> 8);
/*  577 */         this.contents[(this.contentsOffset++)] = (byte)integerValueIndex;
/*  578 */         break;
/*      */       case 9:
/*  580 */         int floatValueIndex = 
/*  581 */           this.constantPool.literalIndex(fieldConstant.floatValue());
/*  582 */         this.contents[(this.contentsOffset++)] = (byte)(floatValueIndex >> 8);
/*  583 */         this.contents[(this.contentsOffset++)] = (byte)floatValueIndex;
/*  584 */         break;
/*      */       case 8:
/*  586 */         int doubleValueIndex = 
/*  587 */           this.constantPool.literalIndex(fieldConstant.doubleValue());
/*  588 */         this.contents[(this.contentsOffset++)] = (byte)(doubleValueIndex >> 8);
/*  589 */         this.contents[(this.contentsOffset++)] = (byte)doubleValueIndex;
/*  590 */         break;
/*      */       case 7:
/*  592 */         int longValueIndex = 
/*  593 */           this.constantPool.literalIndex(fieldConstant.longValue());
/*  594 */         this.contents[(this.contentsOffset++)] = (byte)(longValueIndex >> 8);
/*  595 */         this.contents[(this.contentsOffset++)] = (byte)longValueIndex;
/*  596 */         break;
/*      */       case 11:
/*  598 */         int stringValueIndex = 
/*  599 */           this.constantPool.literalIndex(
/*  600 */           ((StringConstant)fieldConstant).stringValue());
/*  601 */         if (stringValueIndex == -1) {
/*  602 */           if (!this.creatingProblemType)
/*      */           {
/*  604 */             TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
/*  605 */             FieldDeclaration[] fieldDecls = typeDeclaration.fields;
/*  606 */             int i = 0; for (int max = fieldDecls.length; i < max; i++) {
/*  607 */               if (fieldDecls[i].binding != fieldBinding)
/*      */                 continue;
/*  609 */               typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(
/*  610 */                 fieldDecls[i]);
/*      */             }
/*      */           }
/*      */           else
/*      */           {
/*  615 */             this.contentsOffset = fieldAttributeOffset;
/*      */           }
/*      */         } else {
/*  618 */           this.contents[(this.contentsOffset++)] = (byte)(stringValueIndex >> 8);
/*  619 */           this.contents[(this.contentsOffset++)] = (byte)stringValueIndex;
/*      */         }case 6:
/*      */       }
/*      */     }
/*  623 */     if ((this.targetJDK < 3211264L) && (fieldBinding.isSynthetic())) {
/*  624 */       if (this.contentsOffset + 6 >= this.contents.length) {
/*  625 */         resizeContents(6);
/*      */       }
/*  627 */       int syntheticAttributeNameIndex = 
/*  628 */         this.constantPool.literalIndex(AttributeNamesConstants.SyntheticName);
/*  629 */       this.contents[(this.contentsOffset++)] = (byte)(syntheticAttributeNameIndex >> 8);
/*  630 */       this.contents[(this.contentsOffset++)] = (byte)syntheticAttributeNameIndex;
/*      */ 
/*  632 */       this.contents[(this.contentsOffset++)] = 0;
/*  633 */       this.contents[(this.contentsOffset++)] = 0;
/*  634 */       this.contents[(this.contentsOffset++)] = 0;
/*  635 */       this.contents[(this.contentsOffset++)] = 0;
/*  636 */       attributesNumber++;
/*      */     }
/*  638 */     if (fieldBinding.isDeprecated()) {
/*  639 */       if (this.contentsOffset + 6 >= this.contents.length) {
/*  640 */         resizeContents(6);
/*      */       }
/*  642 */       int deprecatedAttributeNameIndex = 
/*  643 */         this.constantPool.literalIndex(AttributeNamesConstants.DeprecatedName);
/*  644 */       this.contents[(this.contentsOffset++)] = (byte)(deprecatedAttributeNameIndex >> 8);
/*  645 */       this.contents[(this.contentsOffset++)] = (byte)deprecatedAttributeNameIndex;
/*      */ 
/*  647 */       this.contents[(this.contentsOffset++)] = 0;
/*  648 */       this.contents[(this.contentsOffset++)] = 0;
/*  649 */       this.contents[(this.contentsOffset++)] = 0;
/*  650 */       this.contents[(this.contentsOffset++)] = 0;
/*  651 */       attributesNumber++;
/*      */     }
/*      */ 
/*  654 */     char[] genericSignature = fieldBinding.genericSignature();
/*  655 */     if (genericSignature != null)
/*      */     {
/*  658 */       if (this.contentsOffset + 8 >= this.contents.length) {
/*  659 */         resizeContents(8);
/*      */       }
/*  661 */       int signatureAttributeNameIndex = 
/*  662 */         this.constantPool.literalIndex(AttributeNamesConstants.SignatureName);
/*  663 */       this.contents[(this.contentsOffset++)] = (byte)(signatureAttributeNameIndex >> 8);
/*  664 */       this.contents[(this.contentsOffset++)] = (byte)signatureAttributeNameIndex;
/*      */ 
/*  666 */       this.contents[(this.contentsOffset++)] = 0;
/*  667 */       this.contents[(this.contentsOffset++)] = 0;
/*  668 */       this.contents[(this.contentsOffset++)] = 0;
/*  669 */       this.contents[(this.contentsOffset++)] = 2;
/*  670 */       int signatureIndex = 
/*  671 */         this.constantPool.literalIndex(genericSignature);
/*  672 */       this.contents[(this.contentsOffset++)] = (byte)(signatureIndex >> 8);
/*  673 */       this.contents[(this.contentsOffset++)] = (byte)signatureIndex;
/*  674 */       attributesNumber++;
/*      */     }
/*  676 */     if (this.targetJDK >= 3211264L) {
/*  677 */       FieldDeclaration fieldDeclaration = fieldBinding.sourceField();
/*  678 */       if (fieldDeclaration != null) {
/*  679 */         Annotation[] annotations = fieldDeclaration.annotations;
/*  680 */         if (annotations != null) {
/*  681 */           attributesNumber += generateRuntimeAnnotations(annotations);
/*      */         }
/*      */       }
/*      */     }
/*  685 */     if ((fieldBinding.tagBits & 0x80) != 0L) {
/*  686 */       this.missingTypes = fieldBinding.type.collectMissingTypes(this.missingTypes);
/*      */     }
/*  688 */     return attributesNumber;
/*      */   }
/*      */ 
/*      */   private void addFieldInfo(FieldBinding fieldBinding)
/*      */   {
/*  699 */     if (this.contentsOffset + 8 >= this.contents.length) {
/*  700 */       resizeContents(8);
/*      */     }
/*      */ 
/*  704 */     int accessFlags = fieldBinding.getAccessFlags();
/*  705 */     if (this.targetJDK < 3211264L)
/*      */     {
/*  707 */       accessFlags &= -4097;
/*      */     }
/*  709 */     this.contents[(this.contentsOffset++)] = (byte)(accessFlags >> 8);
/*  710 */     this.contents[(this.contentsOffset++)] = (byte)accessFlags;
/*      */ 
/*  712 */     int nameIndex = this.constantPool.literalIndex(fieldBinding.name);
/*  713 */     this.contents[(this.contentsOffset++)] = (byte)(nameIndex >> 8);
/*  714 */     this.contents[(this.contentsOffset++)] = (byte)nameIndex;
/*      */ 
/*  716 */     int descriptorIndex = this.constantPool.literalIndex(fieldBinding.type);
/*  717 */     this.contents[(this.contentsOffset++)] = (byte)(descriptorIndex >> 8);
/*  718 */     this.contents[(this.contentsOffset++)] = (byte)descriptorIndex;
/*  719 */     int fieldAttributeOffset = this.contentsOffset;
/*  720 */     int attributeNumber = 0;
/*      */ 
/*  722 */     this.contentsOffset += 2;
/*  723 */     attributeNumber += addFieldAttributes(fieldBinding, fieldAttributeOffset);
/*  724 */     if (this.contentsOffset + 2 >= this.contents.length) {
/*  725 */       resizeContents(2);
/*      */     }
/*  727 */     this.contents[(fieldAttributeOffset++)] = (byte)(attributeNumber >> 8);
/*  728 */     this.contents[fieldAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addFieldInfos()
/*      */   {
/*  746 */     SourceTypeBinding currentBinding = this.referenceBinding;
/*  747 */     FieldBinding[] syntheticFields = currentBinding.syntheticFields();
/*  748 */     int fieldCount = currentBinding.fieldCount() + (syntheticFields == null ? 0 : syntheticFields.length);
/*      */ 
/*  751 */     if (fieldCount > 65535) {
/*  752 */       this.referenceBinding.scope.problemReporter().tooManyFields(this.referenceBinding.scope.referenceType());
/*      */     }
/*  754 */     this.contents[(this.contentsOffset++)] = (byte)(fieldCount >> 8);
/*  755 */     this.contents[(this.contentsOffset++)] = (byte)fieldCount;
/*      */ 
/*  757 */     FieldDeclaration[] fieldDecls = currentBinding.scope.referenceContext.fields;
/*  758 */     int i = 0; for (int max = fieldDecls == null ? 0 : fieldDecls.length; i < max; i++) {
/*  759 */       FieldDeclaration fieldDecl = fieldDecls[i];
/*  760 */       if (fieldDecl.binding != null) {
/*  761 */         addFieldInfo(fieldDecl.binding);
/*      */       }
/*      */     }
/*      */ 
/*  765 */     if (syntheticFields != null) {
/*  766 */       int i = 0; for (int max = syntheticFields.length; i < max; i++)
/*  767 */         addFieldInfo(syntheticFields[i]);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addMissingAbstractProblemMethod(MethodDeclaration methodDeclaration, MethodBinding methodBinding, CategorizedProblem problem, CompilationResult compilationResult)
/*      */   {
/*  774 */     generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
/*  775 */     int methodAttributeOffset = this.contentsOffset;
/*  776 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/*  779 */     attributeNumber++;
/*      */ 
/*  781 */     int codeAttributeOffset = this.contentsOffset;
/*  782 */     generateCodeAttributeHeader();
/*  783 */     StringBuffer buffer = new StringBuffer(25);
/*  784 */     buffer.append("\t" + problem.getMessage() + "\n");
/*  785 */     buffer.insert(0, Messages.compilation_unresolvedProblem);
/*  786 */     String problemString = buffer.toString();
/*      */ 
/*  788 */     this.codeStream.init(this);
/*  789 */     this.codeStream.preserveUnusedLocals = true;
/*  790 */     this.codeStream.initializeMaxLocals(methodBinding);
/*      */ 
/*  793 */     this.codeStream.generateCodeAttributeForProblemMethod(problemString);
/*      */ 
/*  795 */     completeCodeAttributeForMissingAbstractProblemMethod(
/*  796 */       methodBinding, 
/*  797 */       codeAttributeOffset, 
/*  798 */       compilationResult.getLineSeparatorPositions(), 
/*  799 */       problem.getSourceLineNumber());
/*      */ 
/*  801 */     completeMethodInfo(methodAttributeOffset, attributeNumber);
/*      */   }
/*      */ 
/*      */   public void addProblemClinit(CategorizedProblem[] problems)
/*      */   {
/*  811 */     generateMethodInfoHeaderForClinit();
/*      */ 
/*  813 */     this.contentsOffset -= 2;
/*  814 */     int attributeOffset = this.contentsOffset;
/*  815 */     this.contentsOffset += 2;
/*  816 */     int attributeNumber = 0;
/*      */ 
/*  818 */     int codeAttributeOffset = this.contentsOffset;
/*  819 */     generateCodeAttributeHeader();
/*  820 */     this.codeStream.resetForProblemClinit(this);
/*  821 */     String problemString = "";
/*  822 */     int problemLine = 0;
/*  823 */     if (problems != null) {
/*  824 */       int max = problems.length;
/*  825 */       StringBuffer buffer = new StringBuffer(25);
/*  826 */       int count = 0;
/*  827 */       for (int i = 0; i < max; i++) {
/*  828 */         CategorizedProblem problem = problems[i];
/*  829 */         if ((problem != null) && (problem.isError())) {
/*  830 */           buffer.append("\t" + problem.getMessage() + "\n");
/*  831 */           count++;
/*  832 */           if (problemLine == 0) {
/*  833 */             problemLine = problem.getSourceLineNumber();
/*      */           }
/*  835 */           problems[i] = null;
/*      */         }
/*      */       }
/*  838 */       if (count > 1)
/*  839 */         buffer.insert(0, Messages.compilation_unresolvedProblems);
/*      */       else {
/*  841 */         buffer.insert(0, Messages.compilation_unresolvedProblem);
/*      */       }
/*  843 */       problemString = buffer.toString();
/*      */     }
/*      */ 
/*  847 */     this.codeStream.generateCodeAttributeForProblemMethod(problemString);
/*  848 */     attributeNumber++;
/*  849 */     completeCodeAttributeForClinit(
/*  850 */       codeAttributeOffset, 
/*  851 */       problemLine);
/*  852 */     if (this.contentsOffset + 2 >= this.contents.length) {
/*  853 */       resizeContents(2);
/*      */     }
/*  855 */     this.contents[(attributeOffset++)] = (byte)(attributeNumber >> 8);
/*  856 */     this.contents[attributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addProblemConstructor(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems)
/*      */   {
/*  872 */     generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
/*  873 */     int methodAttributeOffset = this.contentsOffset;
/*  874 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/*  877 */     attributeNumber++;
/*  878 */     int codeAttributeOffset = this.contentsOffset;
/*  879 */     generateCodeAttributeHeader();
/*  880 */     this.codeStream.reset(method, this);
/*  881 */     String problemString = "";
/*  882 */     int problemLine = 0;
/*  883 */     if (problems != null) {
/*  884 */       int max = problems.length;
/*  885 */       StringBuffer buffer = new StringBuffer(25);
/*  886 */       int count = 0;
/*  887 */       for (int i = 0; i < max; i++) {
/*  888 */         CategorizedProblem problem = problems[i];
/*  889 */         if ((problem != null) && (problem.isError())) {
/*  890 */           buffer.append("\t" + problem.getMessage() + "\n");
/*  891 */           count++;
/*  892 */           if (problemLine == 0) {
/*  893 */             problemLine = problem.getSourceLineNumber();
/*      */           }
/*      */         }
/*      */       }
/*  897 */       if (count > 1)
/*  898 */         buffer.insert(0, Messages.compilation_unresolvedProblems);
/*      */       else {
/*  900 */         buffer.insert(0, Messages.compilation_unresolvedProblem);
/*      */       }
/*  902 */       problemString = buffer.toString();
/*      */     }
/*      */ 
/*  906 */     this.codeStream.generateCodeAttributeForProblemMethod(problemString);
/*  907 */     completeCodeAttributeForProblemMethod(
/*  908 */       method, 
/*  909 */       methodBinding, 
/*  910 */       codeAttributeOffset, 
/*  911 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/*  913 */       .referenceCompilationUnit().compilationResult
/*  915 */       .getLineSeparatorPositions(), 
/*  916 */       problemLine);
/*  917 */     completeMethodInfo(methodAttributeOffset, attributeNumber);
/*      */   }
/*      */ 
/*      */   public void addProblemConstructor(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems, int savedOffset)
/*      */   {
/*  935 */     this.contentsOffset = savedOffset;
/*  936 */     this.methodCount -= 1;
/*  937 */     addProblemConstructor(method, methodBinding, problems);
/*      */   }
/*      */ 
/*      */   public void addProblemMethod(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems)
/*      */   {
/*  952 */     if ((methodBinding.isAbstract()) && (methodBinding.declaringClass.isInterface())) {
/*  953 */       method.abort(8, null);
/*      */     }
/*      */ 
/*  956 */     generateMethodInfoHeader(methodBinding, methodBinding.modifiers & 0xFFFFF2FF);
/*  957 */     int methodAttributeOffset = this.contentsOffset;
/*  958 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/*  961 */     attributeNumber++;
/*      */ 
/*  963 */     int codeAttributeOffset = this.contentsOffset;
/*  964 */     generateCodeAttributeHeader();
/*  965 */     this.codeStream.reset(method, this);
/*  966 */     String problemString = "";
/*  967 */     int problemLine = 0;
/*  968 */     if (problems != null) {
/*  969 */       int max = problems.length;
/*  970 */       StringBuffer buffer = new StringBuffer(25);
/*  971 */       int count = 0;
/*  972 */       for (int i = 0; i < max; i++) {
/*  973 */         CategorizedProblem problem = problems[i];
/*  974 */         if ((problem == null) || 
/*  975 */           (!problem.isError()) || 
/*  976 */           (problem.getSourceStart() < method.declarationSourceStart) || 
/*  977 */           (problem.getSourceEnd() > method.declarationSourceEnd)) continue;
/*  978 */         buffer.append("\t" + problem.getMessage() + "\n");
/*  979 */         count++;
/*  980 */         if (problemLine == 0) {
/*  981 */           problemLine = problem.getSourceLineNumber();
/*      */         }
/*  983 */         problems[i] = null;
/*      */       }
/*      */ 
/*  986 */       if (count > 1)
/*  987 */         buffer.insert(0, Messages.compilation_unresolvedProblems);
/*      */       else {
/*  989 */         buffer.insert(0, Messages.compilation_unresolvedProblem);
/*      */       }
/*  991 */       problemString = buffer.toString();
/*      */     }
/*      */ 
/*  995 */     this.codeStream.generateCodeAttributeForProblemMethod(problemString);
/*  996 */     completeCodeAttributeForProblemMethod(
/*  997 */       method, 
/*  998 */       methodBinding, 
/*  999 */       codeAttributeOffset, 
/* 1000 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1002 */       .referenceCompilationUnit().compilationResult
/* 1004 */       .getLineSeparatorPositions(), 
/* 1005 */       problemLine);
/* 1006 */     completeMethodInfo(methodAttributeOffset, attributeNumber);
/*      */   }
/*      */ 
/*      */   public void addProblemMethod(AbstractMethodDeclaration method, MethodBinding methodBinding, CategorizedProblem[] problems, int savedOffset)
/*      */   {
/* 1025 */     this.contentsOffset = savedOffset;
/* 1026 */     this.methodCount -= 1;
/* 1027 */     addProblemMethod(method, methodBinding, problems);
/*      */   }
/*      */ 
/*      */   public void addSpecialMethods()
/*      */   {
/* 1042 */     generateMissingAbstractMethods(this.referenceBinding.scope.referenceType().missingAbstractMethods, this.referenceBinding.scope.referenceCompilationUnit().compilationResult);
/*      */ 
/* 1044 */     MethodBinding[] defaultAbstractMethods = this.referenceBinding.getDefaultAbstractMethods();
/* 1045 */     int i = 0; for (int max = defaultAbstractMethods.length; i < max; i++) {
/* 1046 */       generateMethodInfoHeader(defaultAbstractMethods[i]);
/* 1047 */       int methodAttributeOffset = this.contentsOffset;
/* 1048 */       int attributeNumber = generateMethodInfoAttribute(defaultAbstractMethods[i]);
/* 1049 */       completeMethodInfo(methodAttributeOffset, attributeNumber);
/*      */     }
/*      */ 
/* 1052 */     SyntheticMethodBinding[] syntheticMethods = this.referenceBinding.syntheticMethods();
/* 1053 */     if (syntheticMethods != null) {
/* 1054 */       int i = 0; for (int max = syntheticMethods.length; i < max; i++) {
/* 1055 */         SyntheticMethodBinding syntheticMethod = syntheticMethods[i];
/* 1056 */         switch (syntheticMethod.purpose)
/*      */         {
/*      */         case 1:
/*      */         case 3:
/* 1061 */           addSyntheticFieldReadAccessMethod(syntheticMethod);
/* 1062 */           break;
/*      */         case 2:
/*      */         case 4:
/* 1067 */           addSyntheticFieldWriteAccessMethod(syntheticMethod);
/* 1068 */           break;
/*      */         case 5:
/*      */         case 7:
/*      */         case 8:
/* 1073 */           addSyntheticMethodAccessMethod(syntheticMethod);
/* 1074 */           break;
/*      */         case 6:
/* 1077 */           addSyntheticConstructorAccessMethod(syntheticMethod);
/* 1078 */           break;
/*      */         case 9:
/* 1081 */           addSyntheticEnumValuesMethod(syntheticMethod);
/* 1082 */           break;
/*      */         case 10:
/* 1085 */           addSyntheticEnumValueOfMethod(syntheticMethod);
/* 1086 */           break;
/*      */         case 11:
/* 1089 */           addSyntheticSwitchTable(syntheticMethod);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addSyntheticConstructorAccessMethod(SyntheticMethodBinding methodBinding)
/*      */   {
/* 1102 */     generateMethodInfoHeader(methodBinding);
/* 1103 */     int methodAttributeOffset = this.contentsOffset;
/*      */ 
/* 1105 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/* 1107 */     int codeAttributeOffset = this.contentsOffset;
/* 1108 */     attributeNumber++;
/* 1109 */     generateCodeAttributeHeader();
/* 1110 */     this.codeStream.init(this);
/* 1111 */     this.codeStream.generateSyntheticBodyForConstructorAccess(methodBinding);
/* 1112 */     completeCodeAttributeForSyntheticMethod(
/* 1113 */       methodBinding, 
/* 1114 */       codeAttributeOffset, 
/* 1115 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1117 */       .referenceCompilationUnit().compilationResult
/* 1119 */       .getLineSeparatorPositions());
/*      */ 
/* 1121 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 1122 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addSyntheticEnumValueOfMethod(SyntheticMethodBinding methodBinding)
/*      */   {
/* 1132 */     generateMethodInfoHeader(methodBinding);
/* 1133 */     int methodAttributeOffset = this.contentsOffset;
/*      */ 
/* 1135 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/* 1137 */     int codeAttributeOffset = this.contentsOffset;
/* 1138 */     attributeNumber++;
/* 1139 */     generateCodeAttributeHeader();
/* 1140 */     this.codeStream.init(this);
/* 1141 */     this.codeStream.generateSyntheticBodyForEnumValueOf(methodBinding);
/* 1142 */     completeCodeAttributeForSyntheticMethod(
/* 1143 */       methodBinding, 
/* 1144 */       codeAttributeOffset, 
/* 1145 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1147 */       .referenceCompilationUnit().compilationResult
/* 1149 */       .getLineSeparatorPositions());
/*      */ 
/* 1151 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 1152 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addSyntheticEnumValuesMethod(SyntheticMethodBinding methodBinding)
/*      */   {
/* 1162 */     generateMethodInfoHeader(methodBinding);
/* 1163 */     int methodAttributeOffset = this.contentsOffset;
/*      */ 
/* 1165 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/* 1167 */     int codeAttributeOffset = this.contentsOffset;
/* 1168 */     attributeNumber++;
/* 1169 */     generateCodeAttributeHeader();
/* 1170 */     this.codeStream.init(this);
/* 1171 */     this.codeStream.generateSyntheticBodyForEnumValues(methodBinding);
/* 1172 */     completeCodeAttributeForSyntheticMethod(
/* 1173 */       methodBinding, 
/* 1174 */       codeAttributeOffset, 
/* 1175 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1177 */       .referenceCompilationUnit().compilationResult
/* 1179 */       .getLineSeparatorPositions());
/*      */ 
/* 1181 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 1182 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addSyntheticFieldReadAccessMethod(SyntheticMethodBinding methodBinding)
/*      */   {
/* 1193 */     generateMethodInfoHeader(methodBinding);
/* 1194 */     int methodAttributeOffset = this.contentsOffset;
/*      */ 
/* 1196 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/* 1198 */     int codeAttributeOffset = this.contentsOffset;
/* 1199 */     attributeNumber++;
/* 1200 */     generateCodeAttributeHeader();
/* 1201 */     this.codeStream.init(this);
/* 1202 */     this.codeStream.generateSyntheticBodyForFieldReadAccess(methodBinding);
/* 1203 */     completeCodeAttributeForSyntheticMethod(
/* 1204 */       methodBinding, 
/* 1205 */       codeAttributeOffset, 
/* 1206 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1208 */       .referenceCompilationUnit().compilationResult
/* 1210 */       .getLineSeparatorPositions());
/*      */ 
/* 1212 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 1213 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addSyntheticFieldWriteAccessMethod(SyntheticMethodBinding methodBinding)
/*      */   {
/* 1224 */     generateMethodInfoHeader(methodBinding);
/* 1225 */     int methodAttributeOffset = this.contentsOffset;
/*      */ 
/* 1227 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/* 1229 */     int codeAttributeOffset = this.contentsOffset;
/* 1230 */     attributeNumber++;
/* 1231 */     generateCodeAttributeHeader();
/* 1232 */     this.codeStream.init(this);
/* 1233 */     this.codeStream.generateSyntheticBodyForFieldWriteAccess(methodBinding);
/* 1234 */     completeCodeAttributeForSyntheticMethod(
/* 1235 */       methodBinding, 
/* 1236 */       codeAttributeOffset, 
/* 1237 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1239 */       .referenceCompilationUnit().compilationResult
/* 1241 */       .getLineSeparatorPositions());
/*      */ 
/* 1243 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 1244 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addSyntheticMethodAccessMethod(SyntheticMethodBinding methodBinding)
/*      */   {
/* 1254 */     generateMethodInfoHeader(methodBinding);
/* 1255 */     int methodAttributeOffset = this.contentsOffset;
/*      */ 
/* 1257 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/* 1259 */     int codeAttributeOffset = this.contentsOffset;
/* 1260 */     attributeNumber++;
/* 1261 */     generateCodeAttributeHeader();
/* 1262 */     this.codeStream.init(this);
/* 1263 */     this.codeStream.generateSyntheticBodyForMethodAccess(methodBinding);
/* 1264 */     completeCodeAttributeForSyntheticMethod(
/* 1265 */       methodBinding, 
/* 1266 */       codeAttributeOffset, 
/* 1267 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1269 */       .referenceCompilationUnit().compilationResult
/* 1271 */       .getLineSeparatorPositions());
/*      */ 
/* 1273 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 1274 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void addSyntheticSwitchTable(SyntheticMethodBinding methodBinding) {
/* 1278 */     generateMethodInfoHeader(methodBinding);
/* 1279 */     int methodAttributeOffset = this.contentsOffset;
/*      */ 
/* 1281 */     int attributeNumber = generateMethodInfoAttribute(methodBinding);
/*      */ 
/* 1283 */     int codeAttributeOffset = this.contentsOffset;
/* 1284 */     attributeNumber++;
/* 1285 */     generateCodeAttributeHeader();
/* 1286 */     this.codeStream.init(this);
/* 1287 */     this.codeStream.generateSyntheticBodyForSwitchTable(methodBinding);
/* 1288 */     completeCodeAttributeForSyntheticMethod(
/* 1289 */       true, 
/* 1290 */       methodBinding, 
/* 1291 */       codeAttributeOffset, 
/* 1292 */       ((SourceTypeBinding)methodBinding.declaringClass).scope
/* 1294 */       .referenceCompilationUnit().compilationResult
/* 1296 */       .getLineSeparatorPositions());
/*      */ 
/* 1298 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 1299 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public void completeCodeAttribute(int codeAttributeOffset)
/*      */   {
/* 1316 */     this.contents = this.codeStream.bCodeStream;
/* 1317 */     int localContentsOffset = this.codeStream.classFileOffset;
/*      */ 
/* 1322 */     int code_length = this.codeStream.position;
/* 1323 */     if (code_length > 65535) {
/* 1324 */       this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(
/* 1325 */         this.codeStream.methodDeclaration);
/*      */     }
/* 1327 */     if (localContentsOffset + 20 >= this.contents.length) {
/* 1328 */       resizeContents(20);
/*      */     }
/* 1330 */     int max_stack = this.codeStream.stackMax;
/* 1331 */     this.contents[(codeAttributeOffset + 6)] = (byte)(max_stack >> 8);
/* 1332 */     this.contents[(codeAttributeOffset + 7)] = (byte)max_stack;
/* 1333 */     int max_locals = this.codeStream.maxLocals;
/* 1334 */     this.contents[(codeAttributeOffset + 8)] = (byte)(max_locals >> 8);
/* 1335 */     this.contents[(codeAttributeOffset + 9)] = (byte)max_locals;
/* 1336 */     this.contents[(codeAttributeOffset + 10)] = (byte)(code_length >> 24);
/* 1337 */     this.contents[(codeAttributeOffset + 11)] = (byte)(code_length >> 16);
/* 1338 */     this.contents[(codeAttributeOffset + 12)] = (byte)(code_length >> 8);
/* 1339 */     this.contents[(codeAttributeOffset + 13)] = (byte)code_length;
/*      */ 
/* 1341 */     boolean addStackMaps = (this.produceAttributes & 0x8) != 0;
/*      */ 
/* 1343 */     ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
/* 1344 */     int exceptionHandlersCount = 0;
/* 1345 */     int i = 0; for (int length = this.codeStream.exceptionLabelsCounter; i < length; i++) {
/* 1346 */       exceptionHandlersCount += this.codeStream.exceptionLabels[i].count / 2;
/*      */     }
/* 1348 */     int exSize = exceptionHandlersCount * 8 + 2;
/* 1349 */     if (exSize + localContentsOffset >= this.contents.length) {
/* 1350 */       resizeContents(exSize);
/*      */     }
/*      */ 
/* 1354 */     this.contents[(localContentsOffset++)] = (byte)(exceptionHandlersCount >> 8);
/* 1355 */     this.contents[(localContentsOffset++)] = (byte)exceptionHandlersCount;
/* 1356 */     int i = 0; for (int max = this.codeStream.exceptionLabelsCounter; i < max; i++) {
/* 1357 */       ExceptionLabel exceptionLabel = exceptionLabels[i];
/* 1358 */       if (exceptionLabel != null) {
/* 1359 */         int iRange = 0; int maxRange = exceptionLabel.count;
/* 1360 */         if ((maxRange & 0x1) != 0) {
/* 1361 */           this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(
/* 1362 */             Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)), 
/* 1363 */             this.codeStream.methodDeclaration);
/*      */         }
/* 1365 */         while (iRange < maxRange) {
/* 1366 */           int start = exceptionLabel.ranges[(iRange++)];
/* 1367 */           this.contents[(localContentsOffset++)] = (byte)(start >> 8);
/* 1368 */           this.contents[(localContentsOffset++)] = (byte)start;
/* 1369 */           int end = exceptionLabel.ranges[(iRange++)];
/* 1370 */           this.contents[(localContentsOffset++)] = (byte)(end >> 8);
/* 1371 */           this.contents[(localContentsOffset++)] = (byte)end;
/* 1372 */           int handlerPC = exceptionLabel.position;
/* 1373 */           if (addStackMaps) {
/* 1374 */             StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 1375 */             stackMapFrameCodeStream.addFramePosition(handlerPC);
/*      */           }
/*      */ 
/* 1378 */           this.contents[(localContentsOffset++)] = (byte)(handlerPC >> 8);
/* 1379 */           this.contents[(localContentsOffset++)] = (byte)handlerPC;
/* 1380 */           if (exceptionLabel.exceptionType == null)
/*      */           {
/* 1382 */             this.contents[(localContentsOffset++)] = 0;
/* 1383 */             this.contents[(localContentsOffset++)] = 0;
/*      */           }
/*      */           else
/*      */           {
/*      */             int nameIndex;
/*      */             int nameIndex;
/* 1386 */             if (exceptionLabel.exceptionType == TypeBinding.NULL)
/*      */             {
/* 1388 */               nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
/*      */             }
/* 1390 */             else nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
/*      */ 
/* 1392 */             this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 1393 */             this.contents[(localContentsOffset++)] = (byte)nameIndex;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1399 */     int codeAttributeAttributeOffset = localContentsOffset;
/* 1400 */     int attributeNumber = 0;
/*      */ 
/* 1402 */     localContentsOffset += 2;
/* 1403 */     if (localContentsOffset + 2 >= this.contents.length) {
/* 1404 */       resizeContents(2);
/*      */     }
/*      */ 
/* 1408 */     if ((this.produceAttributes & 0x2) != 0)
/*      */     {
/*      */       int[] pcToSourceMapTable;
/* 1416 */       if (((pcToSourceMapTable = this.codeStream.pcToSourceMap) != null) && 
/* 1417 */         (this.codeStream.pcToSourceMapSize != 0)) {
/* 1418 */         int lineNumberNameIndex = 
/* 1419 */           this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
/* 1420 */         if (localContentsOffset + 8 >= this.contents.length) {
/* 1421 */           resizeContents(8);
/*      */         }
/* 1423 */         this.contents[(localContentsOffset++)] = (byte)(lineNumberNameIndex >> 8);
/* 1424 */         this.contents[(localContentsOffset++)] = (byte)lineNumberNameIndex;
/* 1425 */         int lineNumberTableOffset = localContentsOffset;
/* 1426 */         localContentsOffset += 6;
/*      */ 
/* 1428 */         int numberOfEntries = 0;
/* 1429 */         int length = this.codeStream.pcToSourceMapSize;
/* 1430 */         for (int i = 0; i < length; )
/*      */         {
/* 1432 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 1433 */             resizeContents(4);
/*      */           }
/* 1435 */           int pc = pcToSourceMapTable[(i++)];
/* 1436 */           this.contents[(localContentsOffset++)] = (byte)(pc >> 8);
/* 1437 */           this.contents[(localContentsOffset++)] = (byte)pc;
/* 1438 */           int lineNumber = pcToSourceMapTable[(i++)];
/* 1439 */           this.contents[(localContentsOffset++)] = (byte)(lineNumber >> 8);
/* 1440 */           this.contents[(localContentsOffset++)] = (byte)lineNumber;
/* 1441 */           numberOfEntries++;
/*      */         }
/*      */ 
/* 1444 */         int lineNumberAttr_length = numberOfEntries * 4 + 2;
/* 1445 */         this.contents[(lineNumberTableOffset++)] = (byte)(lineNumberAttr_length >> 24);
/* 1446 */         this.contents[(lineNumberTableOffset++)] = (byte)(lineNumberAttr_length >> 16);
/* 1447 */         this.contents[(lineNumberTableOffset++)] = (byte)(lineNumberAttr_length >> 8);
/* 1448 */         this.contents[(lineNumberTableOffset++)] = (byte)lineNumberAttr_length;
/* 1449 */         this.contents[(lineNumberTableOffset++)] = (byte)(numberOfEntries >> 8);
/* 1450 */         this.contents[(lineNumberTableOffset++)] = (byte)numberOfEntries;
/* 1451 */         attributeNumber++;
/*      */       }
/*      */     }
/*      */ 
/* 1455 */     if ((this.produceAttributes & 0x4) != 0) {
/* 1456 */       int numberOfEntries = 0;
/* 1457 */       int localVariableNameIndex = 
/* 1458 */         this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
/* 1459 */       boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration.isStatic();
/* 1460 */       int maxOfEntries = 8 + 10 * (methodDeclarationIsStatic ? 0 : 1);
/* 1461 */       for (int i = 0; i < this.codeStream.allLocalsCounter; i++) {
/* 1462 */         LocalVariableBinding localVariableBinding = this.codeStream.locals[i];
/* 1463 */         maxOfEntries += 10 * localVariableBinding.initializationCount;
/*      */       }
/*      */ 
/* 1466 */       if (localContentsOffset + maxOfEntries >= this.contents.length) {
/* 1467 */         resizeContents(maxOfEntries);
/*      */       }
/* 1469 */       this.contents[(localContentsOffset++)] = (byte)(localVariableNameIndex >> 8);
/* 1470 */       this.contents[(localContentsOffset++)] = (byte)localVariableNameIndex;
/* 1471 */       int localVariableTableOffset = localContentsOffset;
/*      */ 
/* 1473 */       localContentsOffset += 6;
/*      */ 
/* 1476 */       SourceTypeBinding declaringClassBinding = null;
/* 1477 */       if (!methodDeclarationIsStatic) {
/* 1478 */         numberOfEntries++;
/* 1479 */         this.contents[(localContentsOffset++)] = 0;
/* 1480 */         this.contents[(localContentsOffset++)] = 0;
/* 1481 */         this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 1482 */         this.contents[(localContentsOffset++)] = (byte)code_length;
/* 1483 */         int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
/* 1484 */         this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 1485 */         this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 1486 */         declaringClassBinding = (SourceTypeBinding)this.codeStream.methodDeclaration.binding.declaringClass;
/* 1487 */         int descriptorIndex = 
/* 1488 */           this.constantPool.literalIndex(
/* 1489 */           declaringClassBinding.signature());
/* 1490 */         this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 1491 */         this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 1492 */         this.contents[(localContentsOffset++)] = 0;
/* 1493 */         this.contents[(localContentsOffset++)] = 0;
/*      */       }
/*      */ 
/* 1496 */       int genericLocalVariablesCounter = 0;
/* 1497 */       LocalVariableBinding[] genericLocalVariables = (LocalVariableBinding[])null;
/* 1498 */       int numberOfGenericEntries = 0;
/*      */ 
/* 1500 */       int i = 0; for (int max = this.codeStream.allLocalsCounter; i < max; i++) {
/* 1501 */         LocalVariableBinding localVariable = this.codeStream.locals[i];
/* 1502 */         if (localVariable.declaration != null) {
/* 1503 */           TypeBinding localVariableTypeBinding = localVariable.type;
/* 1504 */           boolean isParameterizedType = (localVariableTypeBinding.isParameterizedType()) || (localVariableTypeBinding.isTypeVariable());
/* 1505 */           if ((localVariable.initializationCount != 0) && (isParameterizedType)) {
/* 1506 */             if (genericLocalVariables == null)
/*      */             {
/* 1508 */               genericLocalVariables = new LocalVariableBinding[max];
/*      */             }
/* 1510 */             genericLocalVariables[(genericLocalVariablesCounter++)] = localVariable;
/*      */           }
/* 1512 */           for (int j = 0; j < localVariable.initializationCount; j++) {
/* 1513 */             int startPC = localVariable.initializationPCs[(j << 1)];
/* 1514 */             int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 1515 */             if (startPC != endPC) {
/* 1516 */               if (endPC == -1) {
/* 1517 */                 localVariable.declaringScope.problemReporter().abortDueToInternalError(
/* 1518 */                   Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)), 
/* 1519 */                   (ASTNode)localVariable.declaringScope.methodScope().referenceContext);
/*      */               }
/* 1521 */               if (isParameterizedType) {
/* 1522 */                 numberOfGenericEntries++;
/*      */               }
/*      */ 
/* 1525 */               numberOfEntries++;
/* 1526 */               this.contents[(localContentsOffset++)] = (byte)(startPC >> 8);
/* 1527 */               this.contents[(localContentsOffset++)] = (byte)startPC;
/* 1528 */               int length = endPC - startPC;
/* 1529 */               this.contents[(localContentsOffset++)] = (byte)(length >> 8);
/* 1530 */               this.contents[(localContentsOffset++)] = (byte)length;
/* 1531 */               int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 1532 */               this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 1533 */               this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 1534 */               int descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
/* 1535 */               this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 1536 */               this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 1537 */               int resolvedPosition = localVariable.resolvedPosition;
/* 1538 */               this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 1539 */               this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1543 */       int value = numberOfEntries * 10 + 2;
/* 1544 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 24);
/* 1545 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 16);
/* 1546 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 8);
/* 1547 */       this.contents[(localVariableTableOffset++)] = (byte)value;
/* 1548 */       this.contents[(localVariableTableOffset++)] = (byte)(numberOfEntries >> 8);
/* 1549 */       this.contents[localVariableTableOffset] = (byte)numberOfEntries;
/* 1550 */       attributeNumber++;
/*      */ 
/* 1552 */       boolean currentInstanceIsGeneric = 
/* 1553 */         (!methodDeclarationIsStatic) && 
/* 1554 */         (declaringClassBinding != null) && 
/* 1555 */         (declaringClassBinding.typeVariables != Binding.NO_TYPE_VARIABLES);
/* 1556 */       if ((genericLocalVariablesCounter != 0) || (currentInstanceIsGeneric))
/*      */       {
/* 1558 */         numberOfGenericEntries += (currentInstanceIsGeneric ? 1 : 0);
/* 1559 */         maxOfEntries = 8 + numberOfGenericEntries * 10;
/*      */ 
/* 1561 */         if (localContentsOffset + maxOfEntries >= this.contents.length) {
/* 1562 */           resizeContents(maxOfEntries);
/*      */         }
/* 1564 */         int localVariableTypeNameIndex = 
/* 1565 */           this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
/* 1566 */         this.contents[(localContentsOffset++)] = (byte)(localVariableTypeNameIndex >> 8);
/* 1567 */         this.contents[(localContentsOffset++)] = (byte)localVariableTypeNameIndex;
/* 1568 */         value = numberOfGenericEntries * 10 + 2;
/* 1569 */         this.contents[(localContentsOffset++)] = (byte)(value >> 24);
/* 1570 */         this.contents[(localContentsOffset++)] = (byte)(value >> 16);
/* 1571 */         this.contents[(localContentsOffset++)] = (byte)(value >> 8);
/* 1572 */         this.contents[(localContentsOffset++)] = (byte)value;
/* 1573 */         this.contents[(localContentsOffset++)] = (byte)(numberOfGenericEntries >> 8);
/* 1574 */         this.contents[(localContentsOffset++)] = (byte)numberOfGenericEntries;
/* 1575 */         if (currentInstanceIsGeneric) {
/* 1576 */           this.contents[(localContentsOffset++)] = 0;
/* 1577 */           this.contents[(localContentsOffset++)] = 0;
/* 1578 */           this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 1579 */           this.contents[(localContentsOffset++)] = (byte)code_length;
/* 1580 */           int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
/* 1581 */           this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 1582 */           this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 1583 */           int descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.genericTypeSignature());
/* 1584 */           this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 1585 */           this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 1586 */           this.contents[(localContentsOffset++)] = 0;
/* 1587 */           this.contents[(localContentsOffset++)] = 0;
/*      */         }
/*      */ 
/* 1590 */         for (int i = 0; i < genericLocalVariablesCounter; i++) {
/* 1591 */           LocalVariableBinding localVariable = genericLocalVariables[i];
/* 1592 */           for (int j = 0; j < localVariable.initializationCount; j++) {
/* 1593 */             int startPC = localVariable.initializationPCs[(j << 1)];
/* 1594 */             int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 1595 */             if (startPC == endPC) {
/*      */               continue;
/*      */             }
/* 1598 */             this.contents[(localContentsOffset++)] = (byte)(startPC >> 8);
/* 1599 */             this.contents[(localContentsOffset++)] = (byte)startPC;
/* 1600 */             int length = endPC - startPC;
/* 1601 */             this.contents[(localContentsOffset++)] = (byte)(length >> 8);
/* 1602 */             this.contents[(localContentsOffset++)] = (byte)length;
/* 1603 */             int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 1604 */             this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 1605 */             this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 1606 */             int descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
/* 1607 */             this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 1608 */             this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 1609 */             int resolvedPosition = localVariable.resolvedPosition;
/* 1610 */             this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 1611 */             this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */           }
/*      */         }
/*      */ 
/* 1615 */         attributeNumber++;
/*      */       }
/*      */     }
/*      */ 
/* 1619 */     if (addStackMaps) {
/* 1620 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 1621 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 1622 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 1623 */         ArrayList frames = new ArrayList();
/* 1624 */         traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 1625 */         int numberOfFrames = frames.size();
/* 1626 */         if (numberOfFrames > 1) {
/* 1627 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 1629 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 1630 */             resizeContents(8);
/*      */           }
/* 1632 */           int stackMapTableAttributeNameIndex = 
/* 1633 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
/* 1634 */           this.contents[(localContentsOffset++)] = (byte)(stackMapTableAttributeNameIndex >> 8);
/* 1635 */           this.contents[(localContentsOffset++)] = (byte)stackMapTableAttributeNameIndex;
/*      */ 
/* 1637 */           int stackMapTableAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 1639 */           localContentsOffset += 4;
/* 1640 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 1641 */             resizeContents(4);
/*      */           }
/* 1643 */           int numberOfFramesOffset = localContentsOffset;
/* 1644 */           localContentsOffset += 2;
/* 1645 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 1646 */             resizeContents(2);
/*      */           }
/* 1648 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 1649 */           StackMapFrame prevFrame = null;
/* 1650 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 1652 */             prevFrame = currentFrame;
/* 1653 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 1656 */             int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
/*      */             int numberOfDifferentLocals;
/*      */             int i;
/* 1657 */             switch (currentFrame.getFrameType(prevFrame)) {
/*      */             case 2:
/* 1659 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 1660 */                 resizeContents(3);
/*      */               }
/* 1662 */               numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
/* 1663 */               this.contents[(localContentsOffset++)] = (byte)(251 + numberOfDifferentLocals);
/* 1664 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 1665 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 1666 */               int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
/* 1667 */               int numberOfLocals = currentFrame.getNumberOfLocals();
/* 1668 */               i = index; break;
/*      */             case 0:
/*      */             case 3:
/*      */             case 1:
/*      */             case 5:
/*      */             case 6:
/*      */             case 4: } while (true) { if (localContentsOffset + 6 >= this.contents.length) {
/* 1670 */                 resizeContents(6);
/*      */               }
/* 1672 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 1673 */               if (info == null) {
/* 1674 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 1676 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 1682 */                   this.contents[(localContentsOffset++)] = 1;
/* 1683 */                   break;
/*      */                 case 9:
/* 1685 */                   this.contents[(localContentsOffset++)] = 2;
/* 1686 */                   break;
/*      */                 case 7:
/* 1688 */                   this.contents[(localContentsOffset++)] = 4;
/* 1689 */                   i++;
/* 1690 */                   break;
/*      */                 case 8:
/* 1692 */                   this.contents[(localContentsOffset++)] = 3;
/* 1693 */                   i++;
/* 1694 */                   break;
/*      */                 case 12:
/* 1696 */                   this.contents[(localContentsOffset++)] = 5;
/* 1697 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 1699 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 1700 */                   switch (info.tag) {
/*      */                   case 8:
/* 1702 */                     int offset = info.offset;
/* 1703 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 1704 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 1705 */                     break;
/*      */                   case 7:
/* 1707 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 1708 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 1709 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 1712 */                 numberOfDifferentLocals--;
/*      */               }
/* 1668 */               i++; if (i >= currentFrame.locals.length) break; if (numberOfDifferentLocals > 0)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 1715 */               break;
/*      */ 
/* 1717 */               if (localContentsOffset + 1 >= this.contents.length) {
/* 1718 */                 resizeContents(1);
/*      */               }
/* 1720 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 1721 */               break;
/*      */ 
/* 1723 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 1724 */                 resizeContents(3);
/*      */               }
/* 1726 */               this.contents[(localContentsOffset++)] = -5;
/* 1727 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 1728 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 1729 */               break;
/*      */ 
/* 1731 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 1732 */                 resizeContents(3);
/*      */               }
/* 1734 */               int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
/* 1735 */               this.contents[(localContentsOffset++)] = (byte)(251 - numberOfDifferentLocals);
/* 1736 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 1737 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 1738 */               break;
/*      */ 
/* 1740 */               if (localContentsOffset + 4 >= this.contents.length) {
/* 1741 */                 resizeContents(4);
/*      */               }
/* 1743 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta + 64);
/* 1744 */               if (currentFrame.stackItems[0] == null)
/* 1745 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else
/* 1747 */                 switch (currentFrame.stackItems[0].id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 1753 */                   this.contents[(localContentsOffset++)] = 1;
/* 1754 */                   break;
/*      */                 case 9:
/* 1756 */                   this.contents[(localContentsOffset++)] = 2;
/* 1757 */                   break;
/*      */                 case 7:
/* 1759 */                   this.contents[(localContentsOffset++)] = 4;
/* 1760 */                   break;
/*      */                 case 8:
/* 1762 */                   this.contents[(localContentsOffset++)] = 3;
/* 1763 */                   break;
/*      */                 case 12:
/* 1765 */                   this.contents[(localContentsOffset++)] = 5;
/* 1766 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 1768 */                   VerificationTypeInfo info = currentFrame.stackItems[0];
/* 1769 */                   byte tag = (byte)info.tag;
/* 1770 */                   this.contents[(localContentsOffset++)] = tag;
/* 1771 */                   switch (tag) {
/*      */                   case 8:
/* 1773 */                     int offset = info.offset;
/* 1774 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 1775 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 1776 */                     break;
/*      */                   case 7:
/* 1778 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 1779 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 1780 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   default:
/* 1784 */                     break;
/*      */ 
/* 1786 */                     if (localContentsOffset + 6 >= this.contents.length) {
/* 1787 */                       resizeContents(6);
/*      */                     }
/* 1789 */                     this.contents[(localContentsOffset++)] = -9;
/* 1790 */                     this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 1791 */                     this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 1792 */                     if (currentFrame.stackItems[0] == null)
/* 1793 */                       this.contents[(localContentsOffset++)] = 0;
/*      */                     else
/* 1795 */                       switch (currentFrame.stackItems[0].id()) {
/*      */                       case 2:
/*      */                       case 3:
/*      */                       case 4:
/*      */                       case 5:
/*      */                       case 10:
/* 1801 */                         this.contents[(localContentsOffset++)] = 1;
/* 1802 */                         break;
/*      */                       case 9:
/* 1804 */                         this.contents[(localContentsOffset++)] = 2;
/* 1805 */                         break;
/*      */                       case 7:
/* 1807 */                         this.contents[(localContentsOffset++)] = 4;
/* 1808 */                         break;
/*      */                       case 8:
/* 1810 */                         this.contents[(localContentsOffset++)] = 3;
/* 1811 */                         break;
/*      */                       case 12:
/* 1813 */                         this.contents[(localContentsOffset++)] = 5;
/* 1814 */                         break;
/*      */                       case 6:
/*      */                       case 11:
/*      */                       default:
/* 1816 */                         VerificationTypeInfo info = currentFrame.stackItems[0];
/* 1817 */                         byte tag = (byte)info.tag;
/* 1818 */                         this.contents[(localContentsOffset++)] = tag;
/* 1819 */                         switch (tag) {
/*      */                         case 8:
/* 1821 */                           int offset = info.offset;
/* 1822 */                           this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 1823 */                           this.contents[(localContentsOffset++)] = (byte)offset;
/* 1824 */                           break;
/*      */                         case 7:
/* 1826 */                           int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 1827 */                           this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 1828 */                           this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                         default:
/* 1832 */                           break;
/*      */ 
/* 1835 */                           if (localContentsOffset + 5 >= this.contents.length) {
/* 1836 */                             resizeContents(5);
/*      */                           }
/* 1838 */                           this.contents[(localContentsOffset++)] = -1;
/* 1839 */                           this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 1840 */                           this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 1841 */                           int numberOfLocalOffset = localContentsOffset;
/* 1842 */                           localContentsOffset += 2;
/* 1843 */                           int numberOfLocalEntries = 0;
/* 1844 */                           int numberOfLocals = currentFrame.getNumberOfLocals();
/* 1845 */                           int numberOfEntries = 0;
/* 1846 */                           int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 1847 */                           for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 1848 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 1849 */                               resizeContents(3);
/*      */                             }
/* 1851 */                             VerificationTypeInfo info = currentFrame.locals[i];
/* 1852 */                             if (info == null) {
/* 1853 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             } else {
/* 1855 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 1861 */                                 this.contents[(localContentsOffset++)] = 1;
/* 1862 */                                 break;
/*      */                               case 9:
/* 1864 */                                 this.contents[(localContentsOffset++)] = 2;
/* 1865 */                                 break;
/*      */                               case 7:
/* 1867 */                                 this.contents[(localContentsOffset++)] = 4;
/* 1868 */                                 i++;
/* 1869 */                                 break;
/*      */                               case 8:
/* 1871 */                                 this.contents[(localContentsOffset++)] = 3;
/* 1872 */                                 i++;
/* 1873 */                                 break;
/*      */                               case 12:
/* 1875 */                                 this.contents[(localContentsOffset++)] = 5;
/* 1876 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 1878 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 1879 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 1881 */                                   int offset = info.offset;
/* 1882 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 1883 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 1884 */                                   break;
/*      */                                 case 7:
/* 1886 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 1887 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 1888 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/* 1891 */                               numberOfLocalEntries++;
/*      */                             }
/* 1893 */                             numberOfEntries++;
/*      */                           }
/* 1895 */                           if (localContentsOffset + 4 >= this.contents.length) {
/* 1896 */                             resizeContents(4);
/*      */                           }
/* 1898 */                           this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 1899 */                           this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 1900 */                           int numberOfStackItems = currentFrame.numberOfStackItems;
/* 1901 */                           this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 1902 */                           this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 1903 */                           for (int i = 0; i < numberOfStackItems; i++) {
/* 1904 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 1905 */                               resizeContents(3);
/*      */                             }
/* 1907 */                             VerificationTypeInfo info = currentFrame.stackItems[i];
/* 1908 */                             if (info == null)
/* 1909 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             else
/* 1911 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 1917 */                                 this.contents[(localContentsOffset++)] = 1;
/* 1918 */                                 break;
/*      */                               case 9:
/* 1920 */                                 this.contents[(localContentsOffset++)] = 2;
/* 1921 */                                 break;
/*      */                               case 7:
/* 1923 */                                 this.contents[(localContentsOffset++)] = 4;
/* 1924 */                                 break;
/*      */                               case 8:
/* 1926 */                                 this.contents[(localContentsOffset++)] = 3;
/* 1927 */                                 break;
/*      */                               case 12:
/* 1929 */                                 this.contents[(localContentsOffset++)] = 5;
/* 1930 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 1932 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 1933 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 1935 */                                   int offset = info.offset;
/* 1936 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 1937 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 1938 */                                   break;
/*      */                                 case 7:
/* 1940 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 1941 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 1942 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                   }
/*      */                 } }
/*      */           }
/* 1950 */           numberOfFrames--;
/* 1951 */           if (numberOfFrames != 0) {
/* 1952 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 1953 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 1955 */             int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
/* 1956 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 1957 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 1958 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 1959 */             this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
/* 1960 */             attributeNumber++;
/*      */           } else {
/* 1962 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1968 */     if ((this.produceAttributes & 0x10) != 0) {
/* 1969 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 1970 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 1971 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 1972 */         ArrayList frames = new ArrayList();
/* 1973 */         traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 1974 */         int numberOfFrames = frames.size();
/* 1975 */         if (numberOfFrames > 1) {
/* 1976 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 1978 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 1979 */             resizeContents(8);
/*      */           }
/* 1981 */           int stackMapAttributeNameIndex = 
/* 1982 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
/* 1983 */           this.contents[(localContentsOffset++)] = (byte)(stackMapAttributeNameIndex >> 8);
/* 1984 */           this.contents[(localContentsOffset++)] = (byte)stackMapAttributeNameIndex;
/*      */ 
/* 1986 */           int stackMapAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 1988 */           localContentsOffset += 4;
/* 1989 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 1990 */             resizeContents(4);
/*      */           }
/* 1992 */           int numberOfFramesOffset = localContentsOffset;
/* 1993 */           localContentsOffset += 2;
/* 1994 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 1995 */             resizeContents(2);
/*      */           }
/* 1997 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 1998 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 2000 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 2003 */             int frameOffset = currentFrame.pc;
/*      */ 
/* 2005 */             if (localContentsOffset + 5 >= this.contents.length) {
/* 2006 */               resizeContents(5);
/*      */             }
/* 2008 */             this.contents[(localContentsOffset++)] = (byte)(frameOffset >> 8);
/* 2009 */             this.contents[(localContentsOffset++)] = (byte)frameOffset;
/* 2010 */             int numberOfLocalOffset = localContentsOffset;
/* 2011 */             localContentsOffset += 2;
/* 2012 */             int numberOfLocalEntries = 0;
/* 2013 */             int numberOfLocals = currentFrame.getNumberOfLocals();
/* 2014 */             int numberOfEntries = 0;
/* 2015 */             int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 2016 */             for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 2017 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 2018 */                 resizeContents(3);
/*      */               }
/* 2020 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 2021 */               if (info == null) {
/* 2022 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 2024 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 2030 */                   this.contents[(localContentsOffset++)] = 1;
/* 2031 */                   break;
/*      */                 case 9:
/* 2033 */                   this.contents[(localContentsOffset++)] = 2;
/* 2034 */                   break;
/*      */                 case 7:
/* 2036 */                   this.contents[(localContentsOffset++)] = 4;
/* 2037 */                   i++;
/* 2038 */                   break;
/*      */                 case 8:
/* 2040 */                   this.contents[(localContentsOffset++)] = 3;
/* 2041 */                   i++;
/* 2042 */                   break;
/*      */                 case 12:
/* 2044 */                   this.contents[(localContentsOffset++)] = 5;
/* 2045 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 2047 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 2048 */                   switch (info.tag) {
/*      */                   case 8:
/* 2050 */                     int offset = info.offset;
/* 2051 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2052 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 2053 */                     break;
/*      */                   case 7:
/* 2055 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2056 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2057 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 2060 */                 numberOfLocalEntries++;
/*      */               }
/* 2062 */               numberOfEntries++;
/*      */             }
/* 2064 */             if (localContentsOffset + 4 >= this.contents.length) {
/* 2065 */               resizeContents(4);
/*      */             }
/* 2067 */             this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 2068 */             this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 2069 */             int numberOfStackItems = currentFrame.numberOfStackItems;
/* 2070 */             this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 2071 */             this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 2072 */             for (int i = 0; i < numberOfStackItems; i++) {
/* 2073 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 2074 */                 resizeContents(3);
/*      */               }
/* 2076 */               VerificationTypeInfo info = currentFrame.stackItems[i];
/* 2077 */               if (info == null)
/* 2078 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else {
/* 2080 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 2086 */                   this.contents[(localContentsOffset++)] = 1;
/* 2087 */                   break;
/*      */                 case 9:
/* 2089 */                   this.contents[(localContentsOffset++)] = 2;
/* 2090 */                   break;
/*      */                 case 7:
/* 2092 */                   this.contents[(localContentsOffset++)] = 4;
/* 2093 */                   break;
/*      */                 case 8:
/* 2095 */                   this.contents[(localContentsOffset++)] = 3;
/* 2096 */                   break;
/*      */                 case 12:
/* 2098 */                   this.contents[(localContentsOffset++)] = 5;
/* 2099 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 2101 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 2102 */                   switch (info.tag) {
/*      */                   case 8:
/* 2104 */                     int offset = info.offset;
/* 2105 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2106 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 2107 */                     break;
/*      */                   case 7:
/* 2109 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2110 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2111 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2118 */           numberOfFrames--;
/* 2119 */           if (numberOfFrames != 0) {
/* 2120 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 2121 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 2123 */             int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
/* 2124 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 2125 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 2126 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 2127 */             this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
/* 2128 */             attributeNumber++;
/*      */           } else {
/* 2130 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2136 */     this.contents[(codeAttributeAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 2137 */     this.contents[codeAttributeAttributeOffset] = (byte)attributeNumber;
/*      */ 
/* 2140 */     int codeAttributeLength = localContentsOffset - (codeAttributeOffset + 6);
/* 2141 */     this.contents[(codeAttributeOffset + 2)] = (byte)(codeAttributeLength >> 24);
/* 2142 */     this.contents[(codeAttributeOffset + 3)] = (byte)(codeAttributeLength >> 16);
/* 2143 */     this.contents[(codeAttributeOffset + 4)] = (byte)(codeAttributeLength >> 8);
/* 2144 */     this.contents[(codeAttributeOffset + 5)] = (byte)codeAttributeLength;
/* 2145 */     this.contentsOffset = localContentsOffset;
/*      */   }
/*      */ 
/*      */   public void completeCodeAttributeForClinit(int codeAttributeOffset)
/*      */   {
/* 2162 */     this.contents = this.codeStream.bCodeStream;
/* 2163 */     int localContentsOffset = this.codeStream.classFileOffset;
/*      */ 
/* 2168 */     int code_length = this.codeStream.position;
/* 2169 */     if (code_length > 65535) {
/* 2170 */       this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(
/* 2171 */         this.codeStream.methodDeclaration.scope.referenceType());
/*      */     }
/* 2173 */     if (localContentsOffset + 20 >= this.contents.length) {
/* 2174 */       resizeContents(20);
/*      */     }
/* 2176 */     int max_stack = this.codeStream.stackMax;
/* 2177 */     this.contents[(codeAttributeOffset + 6)] = (byte)(max_stack >> 8);
/* 2178 */     this.contents[(codeAttributeOffset + 7)] = (byte)max_stack;
/* 2179 */     int max_locals = this.codeStream.maxLocals;
/* 2180 */     this.contents[(codeAttributeOffset + 8)] = (byte)(max_locals >> 8);
/* 2181 */     this.contents[(codeAttributeOffset + 9)] = (byte)max_locals;
/* 2182 */     this.contents[(codeAttributeOffset + 10)] = (byte)(code_length >> 24);
/* 2183 */     this.contents[(codeAttributeOffset + 11)] = (byte)(code_length >> 16);
/* 2184 */     this.contents[(codeAttributeOffset + 12)] = (byte)(code_length >> 8);
/* 2185 */     this.contents[(codeAttributeOffset + 13)] = (byte)code_length;
/*      */ 
/* 2187 */     boolean addStackMaps = (this.produceAttributes & 0x8) != 0;
/*      */ 
/* 2189 */     ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
/* 2190 */     int exceptionHandlersCount = 0;
/* 2191 */     int i = 0; for (int length = this.codeStream.exceptionLabelsCounter; i < length; i++) {
/* 2192 */       exceptionHandlersCount += this.codeStream.exceptionLabels[i].count / 2;
/*      */     }
/* 2194 */     int exSize = exceptionHandlersCount * 8 + 2;
/* 2195 */     if (exSize + localContentsOffset >= this.contents.length) {
/* 2196 */       resizeContents(exSize);
/*      */     }
/*      */ 
/* 2200 */     this.contents[(localContentsOffset++)] = (byte)(exceptionHandlersCount >> 8);
/* 2201 */     this.contents[(localContentsOffset++)] = (byte)exceptionHandlersCount;
/* 2202 */     int i = 0; for (int max = this.codeStream.exceptionLabelsCounter; i < max; i++) {
/* 2203 */       ExceptionLabel exceptionLabel = exceptionLabels[i];
/* 2204 */       if (exceptionLabel != null) {
/* 2205 */         int iRange = 0; int maxRange = exceptionLabel.count;
/* 2206 */         if ((maxRange & 0x1) != 0) {
/* 2207 */           this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(
/* 2208 */             Messages.bind(Messages.abort_invalidExceptionAttribute, new String(this.codeStream.methodDeclaration.selector)), 
/* 2209 */             this.codeStream.methodDeclaration);
/*      */         }
/* 2211 */         while (iRange < maxRange) {
/* 2212 */           int start = exceptionLabel.ranges[(iRange++)];
/* 2213 */           this.contents[(localContentsOffset++)] = (byte)(start >> 8);
/* 2214 */           this.contents[(localContentsOffset++)] = (byte)start;
/* 2215 */           int end = exceptionLabel.ranges[(iRange++)];
/* 2216 */           this.contents[(localContentsOffset++)] = (byte)(end >> 8);
/* 2217 */           this.contents[(localContentsOffset++)] = (byte)end;
/* 2218 */           int handlerPC = exceptionLabel.position;
/* 2219 */           this.contents[(localContentsOffset++)] = (byte)(handlerPC >> 8);
/* 2220 */           this.contents[(localContentsOffset++)] = (byte)handlerPC;
/* 2221 */           if (addStackMaps) {
/* 2222 */             StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 2223 */             stackMapFrameCodeStream.addFramePosition(handlerPC);
/*      */           }
/*      */ 
/* 2226 */           if (exceptionLabel.exceptionType == null)
/*      */           {
/* 2228 */             this.contents[(localContentsOffset++)] = 0;
/* 2229 */             this.contents[(localContentsOffset++)] = 0;
/*      */           }
/*      */           else
/*      */           {
/*      */             int nameIndex;
/*      */             int nameIndex;
/* 2232 */             if (exceptionLabel.exceptionType == TypeBinding.NULL)
/*      */             {
/* 2234 */               nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
/*      */             }
/* 2236 */             else nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
/*      */ 
/* 2238 */             this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 2239 */             this.contents[(localContentsOffset++)] = (byte)nameIndex;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2245 */     int codeAttributeAttributeOffset = localContentsOffset;
/* 2246 */     int attributeNumber = 0;
/*      */ 
/* 2248 */     localContentsOffset += 2;
/* 2249 */     if (localContentsOffset + 2 >= this.contents.length) {
/* 2250 */       resizeContents(2);
/*      */     }
/*      */ 
/* 2254 */     if ((this.produceAttributes & 0x2) != 0)
/*      */     {
/*      */       int[] pcToSourceMapTable;
/* 2262 */       if (((pcToSourceMapTable = this.codeStream.pcToSourceMap) != null) && 
/* 2263 */         (this.codeStream.pcToSourceMapSize != 0)) {
/* 2264 */         int lineNumberNameIndex = 
/* 2265 */           this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
/* 2266 */         if (localContentsOffset + 8 >= this.contents.length) {
/* 2267 */           resizeContents(8);
/*      */         }
/* 2269 */         this.contents[(localContentsOffset++)] = (byte)(lineNumberNameIndex >> 8);
/* 2270 */         this.contents[(localContentsOffset++)] = (byte)lineNumberNameIndex;
/* 2271 */         int lineNumberTableOffset = localContentsOffset;
/* 2272 */         localContentsOffset += 6;
/*      */ 
/* 2274 */         int numberOfEntries = 0;
/* 2275 */         int length = this.codeStream.pcToSourceMapSize;
/* 2276 */         for (int i = 0; i < length; )
/*      */         {
/* 2278 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 2279 */             resizeContents(4);
/*      */           }
/* 2281 */           int pc = pcToSourceMapTable[(i++)];
/* 2282 */           this.contents[(localContentsOffset++)] = (byte)(pc >> 8);
/* 2283 */           this.contents[(localContentsOffset++)] = (byte)pc;
/* 2284 */           int lineNumber = pcToSourceMapTable[(i++)];
/* 2285 */           this.contents[(localContentsOffset++)] = (byte)(lineNumber >> 8);
/* 2286 */           this.contents[(localContentsOffset++)] = (byte)lineNumber;
/* 2287 */           numberOfEntries++;
/*      */         }
/*      */ 
/* 2290 */         int lineNumberAttr_length = numberOfEntries * 4 + 2;
/* 2291 */         this.contents[(lineNumberTableOffset++)] = (byte)(lineNumberAttr_length >> 24);
/* 2292 */         this.contents[(lineNumberTableOffset++)] = (byte)(lineNumberAttr_length >> 16);
/* 2293 */         this.contents[(lineNumberTableOffset++)] = (byte)(lineNumberAttr_length >> 8);
/* 2294 */         this.contents[(lineNumberTableOffset++)] = (byte)lineNumberAttr_length;
/* 2295 */         this.contents[(lineNumberTableOffset++)] = (byte)(numberOfEntries >> 8);
/* 2296 */         this.contents[(lineNumberTableOffset++)] = (byte)numberOfEntries;
/* 2297 */         attributeNumber++;
/*      */       }
/*      */     }
/*      */ 
/* 2301 */     if ((this.produceAttributes & 0x4) != 0) {
/* 2302 */       int numberOfEntries = 0;
/*      */ 
/* 2304 */       if ((this.codeStream.pcToSourceMap != null) && 
/* 2305 */         (this.codeStream.pcToSourceMapSize != 0)) {
/* 2306 */         int localVariableNameIndex = 
/* 2307 */           this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
/* 2308 */         if (localContentsOffset + 8 >= this.contents.length) {
/* 2309 */           resizeContents(8);
/*      */         }
/* 2311 */         this.contents[(localContentsOffset++)] = (byte)(localVariableNameIndex >> 8);
/* 2312 */         this.contents[(localContentsOffset++)] = (byte)localVariableNameIndex;
/* 2313 */         int localVariableTableOffset = localContentsOffset;
/* 2314 */         localContentsOffset += 6;
/*      */ 
/* 2321 */         int genericLocalVariablesCounter = 0;
/* 2322 */         LocalVariableBinding[] genericLocalVariables = (LocalVariableBinding[])null;
/* 2323 */         int numberOfGenericEntries = 0;
/*      */ 
/* 2325 */         int i = 0; for (int max = this.codeStream.allLocalsCounter; i < max; i++) {
/* 2326 */           LocalVariableBinding localVariable = this.codeStream.locals[i];
/* 2327 */           if (localVariable.declaration != null) {
/* 2328 */             TypeBinding localVariableTypeBinding = localVariable.type;
/* 2329 */             boolean isParameterizedType = (localVariableTypeBinding.isParameterizedType()) || (localVariableTypeBinding.isTypeVariable());
/* 2330 */             if ((localVariable.initializationCount != 0) && (isParameterizedType)) {
/* 2331 */               if (genericLocalVariables == null)
/*      */               {
/* 2333 */                 genericLocalVariables = new LocalVariableBinding[max];
/*      */               }
/* 2335 */               genericLocalVariables[(genericLocalVariablesCounter++)] = localVariable;
/*      */             }
/* 2337 */             for (int j = 0; j < localVariable.initializationCount; j++) {
/* 2338 */               int startPC = localVariable.initializationPCs[(j << 1)];
/* 2339 */               int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 2340 */               if (startPC != endPC) {
/* 2341 */                 if (endPC == -1) {
/* 2342 */                   localVariable.declaringScope.problemReporter().abortDueToInternalError(
/* 2343 */                     Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)), 
/* 2344 */                     (ASTNode)localVariable.declaringScope.methodScope().referenceContext);
/*      */                 }
/* 2346 */                 if (localContentsOffset + 10 >= this.contents.length) {
/* 2347 */                   resizeContents(10);
/*      */                 }
/*      */ 
/* 2350 */                 numberOfEntries++;
/* 2351 */                 if (isParameterizedType) {
/* 2352 */                   numberOfGenericEntries++;
/*      */                 }
/* 2354 */                 this.contents[(localContentsOffset++)] = (byte)(startPC >> 8);
/* 2355 */                 this.contents[(localContentsOffset++)] = (byte)startPC;
/* 2356 */                 int length = endPC - startPC;
/* 2357 */                 this.contents[(localContentsOffset++)] = (byte)(length >> 8);
/* 2358 */                 this.contents[(localContentsOffset++)] = (byte)length;
/* 2359 */                 int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 2360 */                 this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 2361 */                 this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 2362 */                 int descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
/* 2363 */                 this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 2364 */                 this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 2365 */                 int resolvedPosition = localVariable.resolvedPosition;
/* 2366 */                 this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 2367 */                 this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2371 */         int value = numberOfEntries * 10 + 2;
/* 2372 */         this.contents[(localVariableTableOffset++)] = (byte)(value >> 24);
/* 2373 */         this.contents[(localVariableTableOffset++)] = (byte)(value >> 16);
/* 2374 */         this.contents[(localVariableTableOffset++)] = (byte)(value >> 8);
/* 2375 */         this.contents[(localVariableTableOffset++)] = (byte)value;
/* 2376 */         this.contents[(localVariableTableOffset++)] = (byte)(numberOfEntries >> 8);
/* 2377 */         this.contents[localVariableTableOffset] = (byte)numberOfEntries;
/* 2378 */         attributeNumber++;
/*      */ 
/* 2380 */         if (genericLocalVariablesCounter != 0)
/*      */         {
/* 2383 */           int maxOfEntries = 8 + numberOfGenericEntries * 10;
/*      */ 
/* 2385 */           if (localContentsOffset + maxOfEntries >= this.contents.length) {
/* 2386 */             resizeContents(maxOfEntries);
/*      */           }
/* 2388 */           int localVariableTypeNameIndex = 
/* 2389 */             this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
/* 2390 */           this.contents[(localContentsOffset++)] = (byte)(localVariableTypeNameIndex >> 8);
/* 2391 */           this.contents[(localContentsOffset++)] = (byte)localVariableTypeNameIndex;
/* 2392 */           value = numberOfGenericEntries * 10 + 2;
/* 2393 */           this.contents[(localContentsOffset++)] = (byte)(value >> 24);
/* 2394 */           this.contents[(localContentsOffset++)] = (byte)(value >> 16);
/* 2395 */           this.contents[(localContentsOffset++)] = (byte)(value >> 8);
/* 2396 */           this.contents[(localContentsOffset++)] = (byte)value;
/* 2397 */           this.contents[(localContentsOffset++)] = (byte)(numberOfGenericEntries >> 8);
/* 2398 */           this.contents[(localContentsOffset++)] = (byte)numberOfGenericEntries;
/* 2399 */           for (int i = 0; i < genericLocalVariablesCounter; i++) {
/* 2400 */             LocalVariableBinding localVariable = genericLocalVariables[i];
/* 2401 */             for (int j = 0; j < localVariable.initializationCount; j++) {
/* 2402 */               int startPC = localVariable.initializationPCs[(j << 1)];
/* 2403 */               int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 2404 */               if (startPC == endPC)
/*      */                 continue;
/* 2406 */               this.contents[(localContentsOffset++)] = (byte)(startPC >> 8);
/* 2407 */               this.contents[(localContentsOffset++)] = (byte)startPC;
/* 2408 */               int length = endPC - startPC;
/* 2409 */               this.contents[(localContentsOffset++)] = (byte)(length >> 8);
/* 2410 */               this.contents[(localContentsOffset++)] = (byte)length;
/* 2411 */               int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 2412 */               this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 2413 */               this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 2414 */               int descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
/* 2415 */               this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 2416 */               this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 2417 */               int resolvedPosition = localVariable.resolvedPosition;
/* 2418 */               this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 2419 */               this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */             }
/*      */           }
/*      */ 
/* 2423 */           attributeNumber++;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2428 */     if ((this.produceAttributes & 0x8) != 0) {
/* 2429 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 2430 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 2431 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 2432 */         ArrayList frames = new ArrayList();
/* 2433 */         traverse(null, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, true);
/* 2434 */         int numberOfFrames = frames.size();
/* 2435 */         if (numberOfFrames > 1) {
/* 2436 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 2438 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 2439 */             resizeContents(8);
/*      */           }
/* 2441 */           int stackMapTableAttributeNameIndex = 
/* 2442 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
/* 2443 */           this.contents[(localContentsOffset++)] = (byte)(stackMapTableAttributeNameIndex >> 8);
/* 2444 */           this.contents[(localContentsOffset++)] = (byte)stackMapTableAttributeNameIndex;
/*      */ 
/* 2446 */           int stackMapTableAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 2448 */           localContentsOffset += 4;
/* 2449 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 2450 */             resizeContents(4);
/*      */           }
/* 2452 */           int numberOfFramesOffset = localContentsOffset;
/* 2453 */           localContentsOffset += 2;
/* 2454 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 2455 */             resizeContents(2);
/*      */           }
/* 2457 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 2458 */           StackMapFrame prevFrame = null;
/* 2459 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 2461 */             prevFrame = currentFrame;
/* 2462 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 2465 */             int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
/*      */             int numberOfDifferentLocals;
/*      */             int i;
/* 2466 */             switch (currentFrame.getFrameType(prevFrame)) {
/*      */             case 2:
/* 2468 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 2469 */                 resizeContents(3);
/*      */               }
/* 2471 */               numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
/* 2472 */               this.contents[(localContentsOffset++)] = (byte)(251 + numberOfDifferentLocals);
/* 2473 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 2474 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 2475 */               int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
/* 2476 */               int numberOfLocals = currentFrame.getNumberOfLocals();
/* 2477 */               i = index; break;
/*      */             case 0:
/*      */             case 3:
/*      */             case 1:
/*      */             case 5:
/*      */             case 6:
/*      */             case 4: } while (true) { if (localContentsOffset + 6 >= this.contents.length) {
/* 2479 */                 resizeContents(6);
/*      */               }
/* 2481 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 2482 */               if (info == null) {
/* 2483 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 2485 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 2491 */                   this.contents[(localContentsOffset++)] = 1;
/* 2492 */                   break;
/*      */                 case 9:
/* 2494 */                   this.contents[(localContentsOffset++)] = 2;
/* 2495 */                   break;
/*      */                 case 7:
/* 2497 */                   this.contents[(localContentsOffset++)] = 4;
/* 2498 */                   i++;
/* 2499 */                   break;
/*      */                 case 8:
/* 2501 */                   this.contents[(localContentsOffset++)] = 3;
/* 2502 */                   i++;
/* 2503 */                   break;
/*      */                 case 12:
/* 2505 */                   this.contents[(localContentsOffset++)] = 5;
/* 2506 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 2508 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 2509 */                   switch (info.tag) {
/*      */                   case 8:
/* 2511 */                     int offset = info.offset;
/* 2512 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2513 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 2514 */                     break;
/*      */                   case 7:
/* 2516 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2517 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2518 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 2521 */                 numberOfDifferentLocals--;
/*      */               }
/* 2477 */               i++; if (i >= currentFrame.locals.length) break; if (numberOfDifferentLocals > 0)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 2524 */               break;
/*      */ 
/* 2526 */               if (localContentsOffset + 1 >= this.contents.length) {
/* 2527 */                 resizeContents(1);
/*      */               }
/* 2529 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 2530 */               break;
/*      */ 
/* 2532 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 2533 */                 resizeContents(3);
/*      */               }
/* 2535 */               this.contents[(localContentsOffset++)] = -5;
/* 2536 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 2537 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 2538 */               break;
/*      */ 
/* 2540 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 2541 */                 resizeContents(3);
/*      */               }
/* 2543 */               int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
/* 2544 */               this.contents[(localContentsOffset++)] = (byte)(251 - numberOfDifferentLocals);
/* 2545 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 2546 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 2547 */               break;
/*      */ 
/* 2549 */               if (localContentsOffset + 4 >= this.contents.length) {
/* 2550 */                 resizeContents(4);
/*      */               }
/* 2552 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta + 64);
/* 2553 */               if (currentFrame.stackItems[0] == null)
/* 2554 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else
/* 2556 */                 switch (currentFrame.stackItems[0].id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 2562 */                   this.contents[(localContentsOffset++)] = 1;
/* 2563 */                   break;
/*      */                 case 9:
/* 2565 */                   this.contents[(localContentsOffset++)] = 2;
/* 2566 */                   break;
/*      */                 case 7:
/* 2568 */                   this.contents[(localContentsOffset++)] = 4;
/* 2569 */                   break;
/*      */                 case 8:
/* 2571 */                   this.contents[(localContentsOffset++)] = 3;
/* 2572 */                   break;
/*      */                 case 12:
/* 2574 */                   this.contents[(localContentsOffset++)] = 5;
/* 2575 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 2577 */                   VerificationTypeInfo info = currentFrame.stackItems[0];
/* 2578 */                   byte tag = (byte)info.tag;
/* 2579 */                   this.contents[(localContentsOffset++)] = tag;
/* 2580 */                   switch (tag) {
/*      */                   case 8:
/* 2582 */                     int offset = info.offset;
/* 2583 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2584 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 2585 */                     break;
/*      */                   case 7:
/* 2587 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2588 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2589 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   default:
/* 2593 */                     break;
/*      */ 
/* 2595 */                     if (localContentsOffset + 6 >= this.contents.length) {
/* 2596 */                       resizeContents(6);
/*      */                     }
/* 2598 */                     this.contents[(localContentsOffset++)] = -9;
/* 2599 */                     this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 2600 */                     this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 2601 */                     if (currentFrame.stackItems[0] == null)
/* 2602 */                       this.contents[(localContentsOffset++)] = 0;
/*      */                     else
/* 2604 */                       switch (currentFrame.stackItems[0].id()) {
/*      */                       case 2:
/*      */                       case 3:
/*      */                       case 4:
/*      */                       case 5:
/*      */                       case 10:
/* 2610 */                         this.contents[(localContentsOffset++)] = 1;
/* 2611 */                         break;
/*      */                       case 9:
/* 2613 */                         this.contents[(localContentsOffset++)] = 2;
/* 2614 */                         break;
/*      */                       case 7:
/* 2616 */                         this.contents[(localContentsOffset++)] = 4;
/* 2617 */                         break;
/*      */                       case 8:
/* 2619 */                         this.contents[(localContentsOffset++)] = 3;
/* 2620 */                         break;
/*      */                       case 12:
/* 2622 */                         this.contents[(localContentsOffset++)] = 5;
/* 2623 */                         break;
/*      */                       case 6:
/*      */                       case 11:
/*      */                       default:
/* 2625 */                         VerificationTypeInfo info = currentFrame.stackItems[0];
/* 2626 */                         byte tag = (byte)info.tag;
/* 2627 */                         this.contents[(localContentsOffset++)] = tag;
/* 2628 */                         switch (tag) {
/*      */                         case 8:
/* 2630 */                           int offset = info.offset;
/* 2631 */                           this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2632 */                           this.contents[(localContentsOffset++)] = (byte)offset;
/* 2633 */                           break;
/*      */                         case 7:
/* 2635 */                           int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2636 */                           this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2637 */                           this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                         default:
/* 2641 */                           break;
/*      */ 
/* 2644 */                           if (localContentsOffset + 5 >= this.contents.length) {
/* 2645 */                             resizeContents(5);
/*      */                           }
/* 2647 */                           this.contents[(localContentsOffset++)] = -1;
/* 2648 */                           this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 2649 */                           this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 2650 */                           int numberOfLocalOffset = localContentsOffset;
/* 2651 */                           localContentsOffset += 2;
/* 2652 */                           int numberOfLocalEntries = 0;
/* 2653 */                           int numberOfLocals = currentFrame.getNumberOfLocals();
/* 2654 */                           int numberOfEntries = 0;
/* 2655 */                           int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 2656 */                           for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 2657 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 2658 */                               resizeContents(3);
/*      */                             }
/* 2660 */                             VerificationTypeInfo info = currentFrame.locals[i];
/* 2661 */                             if (info == null) {
/* 2662 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             } else {
/* 2664 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 2670 */                                 this.contents[(localContentsOffset++)] = 1;
/* 2671 */                                 break;
/*      */                               case 9:
/* 2673 */                                 this.contents[(localContentsOffset++)] = 2;
/* 2674 */                                 break;
/*      */                               case 7:
/* 2676 */                                 this.contents[(localContentsOffset++)] = 4;
/* 2677 */                                 i++;
/* 2678 */                                 break;
/*      */                               case 8:
/* 2680 */                                 this.contents[(localContentsOffset++)] = 3;
/* 2681 */                                 i++;
/* 2682 */                                 break;
/*      */                               case 12:
/* 2684 */                                 this.contents[(localContentsOffset++)] = 5;
/* 2685 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 2687 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 2688 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 2690 */                                   int offset = info.offset;
/* 2691 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2692 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 2693 */                                   break;
/*      */                                 case 7:
/* 2695 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2696 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2697 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/* 2700 */                               numberOfLocalEntries++;
/*      */                             }
/* 2702 */                             numberOfEntries++;
/*      */                           }
/* 2704 */                           if (localContentsOffset + 4 >= this.contents.length) {
/* 2705 */                             resizeContents(4);
/*      */                           }
/* 2707 */                           this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 2708 */                           this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 2709 */                           int numberOfStackItems = currentFrame.numberOfStackItems;
/* 2710 */                           this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 2711 */                           this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 2712 */                           for (int i = 0; i < numberOfStackItems; i++) {
/* 2713 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 2714 */                               resizeContents(3);
/*      */                             }
/* 2716 */                             VerificationTypeInfo info = currentFrame.stackItems[i];
/* 2717 */                             if (info == null)
/* 2718 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             else
/* 2720 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 2726 */                                 this.contents[(localContentsOffset++)] = 1;
/* 2727 */                                 break;
/*      */                               case 9:
/* 2729 */                                 this.contents[(localContentsOffset++)] = 2;
/* 2730 */                                 break;
/*      */                               case 7:
/* 2732 */                                 this.contents[(localContentsOffset++)] = 4;
/* 2733 */                                 break;
/*      */                               case 8:
/* 2735 */                                 this.contents[(localContentsOffset++)] = 3;
/* 2736 */                                 break;
/*      */                               case 12:
/* 2738 */                                 this.contents[(localContentsOffset++)] = 5;
/* 2739 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 2741 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 2742 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 2744 */                                   int offset = info.offset;
/* 2745 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2746 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 2747 */                                   break;
/*      */                                 case 7:
/* 2749 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2750 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2751 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                   }
/*      */                 } }
/*      */           }
/* 2759 */           numberOfFrames--;
/* 2760 */           if (numberOfFrames != 0) {
/* 2761 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 2762 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 2764 */             int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
/* 2765 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 2766 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 2767 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 2768 */             this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
/* 2769 */             attributeNumber++;
/*      */           } else {
/* 2771 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2777 */     if ((this.produceAttributes & 0x10) != 0) {
/* 2778 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 2779 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 2780 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 2781 */         ArrayList frames = new ArrayList();
/* 2782 */         traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 2783 */         int numberOfFrames = frames.size();
/* 2784 */         if (numberOfFrames > 1) {
/* 2785 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 2787 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 2788 */             resizeContents(8);
/*      */           }
/* 2790 */           int stackMapAttributeNameIndex = 
/* 2791 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
/* 2792 */           this.contents[(localContentsOffset++)] = (byte)(stackMapAttributeNameIndex >> 8);
/* 2793 */           this.contents[(localContentsOffset++)] = (byte)stackMapAttributeNameIndex;
/*      */ 
/* 2795 */           int stackMapAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 2797 */           localContentsOffset += 4;
/* 2798 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 2799 */             resizeContents(4);
/*      */           }
/* 2801 */           int numberOfFramesOffset = localContentsOffset;
/* 2802 */           localContentsOffset += 2;
/* 2803 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 2804 */             resizeContents(2);
/*      */           }
/* 2806 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 2807 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 2809 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 2812 */             int frameOffset = currentFrame.pc;
/*      */ 
/* 2814 */             if (localContentsOffset + 5 >= this.contents.length) {
/* 2815 */               resizeContents(5);
/*      */             }
/* 2817 */             this.contents[(localContentsOffset++)] = (byte)(frameOffset >> 8);
/* 2818 */             this.contents[(localContentsOffset++)] = (byte)frameOffset;
/* 2819 */             int numberOfLocalOffset = localContentsOffset;
/* 2820 */             localContentsOffset += 2;
/* 2821 */             int numberOfLocalEntries = 0;
/* 2822 */             int numberOfLocals = currentFrame.getNumberOfLocals();
/* 2823 */             int numberOfEntries = 0;
/* 2824 */             int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 2825 */             for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 2826 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 2827 */                 resizeContents(3);
/*      */               }
/* 2829 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 2830 */               if (info == null) {
/* 2831 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 2833 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 2839 */                   this.contents[(localContentsOffset++)] = 1;
/* 2840 */                   break;
/*      */                 case 9:
/* 2842 */                   this.contents[(localContentsOffset++)] = 2;
/* 2843 */                   break;
/*      */                 case 7:
/* 2845 */                   this.contents[(localContentsOffset++)] = 4;
/* 2846 */                   i++;
/* 2847 */                   break;
/*      */                 case 8:
/* 2849 */                   this.contents[(localContentsOffset++)] = 3;
/* 2850 */                   i++;
/* 2851 */                   break;
/*      */                 case 12:
/* 2853 */                   this.contents[(localContentsOffset++)] = 5;
/* 2854 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 2856 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 2857 */                   switch (info.tag) {
/*      */                   case 8:
/* 2859 */                     int offset = info.offset;
/* 2860 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2861 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 2862 */                     break;
/*      */                   case 7:
/* 2864 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2865 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2866 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 2869 */                 numberOfLocalEntries++;
/*      */               }
/* 2871 */               numberOfEntries++;
/*      */             }
/* 2873 */             if (localContentsOffset + 4 >= this.contents.length) {
/* 2874 */               resizeContents(4);
/*      */             }
/* 2876 */             this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 2877 */             this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 2878 */             int numberOfStackItems = currentFrame.numberOfStackItems;
/* 2879 */             this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 2880 */             this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 2881 */             for (int i = 0; i < numberOfStackItems; i++) {
/* 2882 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 2883 */                 resizeContents(3);
/*      */               }
/* 2885 */               VerificationTypeInfo info = currentFrame.stackItems[i];
/* 2886 */               if (info == null)
/* 2887 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else {
/* 2889 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 2895 */                   this.contents[(localContentsOffset++)] = 1;
/* 2896 */                   break;
/*      */                 case 9:
/* 2898 */                   this.contents[(localContentsOffset++)] = 2;
/* 2899 */                   break;
/*      */                 case 7:
/* 2901 */                   this.contents[(localContentsOffset++)] = 4;
/* 2902 */                   break;
/*      */                 case 8:
/* 2904 */                   this.contents[(localContentsOffset++)] = 3;
/* 2905 */                   break;
/*      */                 case 12:
/* 2907 */                   this.contents[(localContentsOffset++)] = 5;
/* 2908 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 2910 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 2911 */                   switch (info.tag) {
/*      */                   case 8:
/* 2913 */                     int offset = info.offset;
/* 2914 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 2915 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 2916 */                     break;
/*      */                   case 7:
/* 2918 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 2919 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 2920 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 2927 */           numberOfFrames--;
/* 2928 */           if (numberOfFrames != 0) {
/* 2929 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 2930 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 2932 */             int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
/* 2933 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 2934 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 2935 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 2936 */             this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
/* 2937 */             attributeNumber++;
/*      */           } else {
/* 2939 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2947 */     if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
/* 2948 */       resizeContents(2);
/*      */     }
/* 2950 */     this.contents[(codeAttributeAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 2951 */     this.contents[codeAttributeAttributeOffset] = (byte)attributeNumber;
/*      */ 
/* 2953 */     int codeAttributeLength = localContentsOffset - (codeAttributeOffset + 6);
/* 2954 */     this.contents[(codeAttributeOffset + 2)] = (byte)(codeAttributeLength >> 24);
/* 2955 */     this.contents[(codeAttributeOffset + 3)] = (byte)(codeAttributeLength >> 16);
/* 2956 */     this.contents[(codeAttributeOffset + 4)] = (byte)(codeAttributeLength >> 8);
/* 2957 */     this.contents[(codeAttributeOffset + 5)] = (byte)codeAttributeLength;
/* 2958 */     this.contentsOffset = localContentsOffset;
/*      */   }
/*      */ 
/*      */   public void completeCodeAttributeForClinit(int codeAttributeOffset, int problemLine)
/*      */   {
/* 2975 */     this.contents = this.codeStream.bCodeStream;
/* 2976 */     int localContentsOffset = this.codeStream.classFileOffset;
/*      */ 
/* 2981 */     int code_length = this.codeStream.position;
/* 2982 */     if (code_length > 65535) {
/* 2983 */       this.codeStream.methodDeclaration.scope.problemReporter().bytecodeExceeds64KLimit(
/* 2984 */         this.codeStream.methodDeclaration.scope.referenceType());
/*      */     }
/* 2986 */     if (localContentsOffset + 20 >= this.contents.length) {
/* 2987 */       resizeContents(20);
/*      */     }
/* 2989 */     int max_stack = this.codeStream.stackMax;
/* 2990 */     this.contents[(codeAttributeOffset + 6)] = (byte)(max_stack >> 8);
/* 2991 */     this.contents[(codeAttributeOffset + 7)] = (byte)max_stack;
/* 2992 */     int max_locals = this.codeStream.maxLocals;
/* 2993 */     this.contents[(codeAttributeOffset + 8)] = (byte)(max_locals >> 8);
/* 2994 */     this.contents[(codeAttributeOffset + 9)] = (byte)max_locals;
/* 2995 */     this.contents[(codeAttributeOffset + 10)] = (byte)(code_length >> 24);
/* 2996 */     this.contents[(codeAttributeOffset + 11)] = (byte)(code_length >> 16);
/* 2997 */     this.contents[(codeAttributeOffset + 12)] = (byte)(code_length >> 8);
/* 2998 */     this.contents[(codeAttributeOffset + 13)] = (byte)code_length;
/*      */ 
/* 3001 */     this.contents[(localContentsOffset++)] = 0;
/* 3002 */     this.contents[(localContentsOffset++)] = 0;
/*      */ 
/* 3005 */     int codeAttributeAttributeOffset = localContentsOffset;
/* 3006 */     int attributeNumber = 0;
/* 3007 */     localContentsOffset += 2;
/* 3008 */     if (localContentsOffset + 2 >= this.contents.length) {
/* 3009 */       resizeContents(2);
/*      */     }
/*      */ 
/* 3013 */     if ((this.produceAttributes & 0x2) != 0) {
/* 3014 */       if (localContentsOffset + 20 >= this.contents.length) {
/* 3015 */         resizeContents(20);
/*      */       }
/*      */ 
/* 3023 */       int lineNumberNameIndex = 
/* 3024 */         this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
/* 3025 */       this.contents[(localContentsOffset++)] = (byte)(lineNumberNameIndex >> 8);
/* 3026 */       this.contents[(localContentsOffset++)] = (byte)lineNumberNameIndex;
/* 3027 */       this.contents[(localContentsOffset++)] = 0;
/* 3028 */       this.contents[(localContentsOffset++)] = 0;
/* 3029 */       this.contents[(localContentsOffset++)] = 0;
/* 3030 */       this.contents[(localContentsOffset++)] = 6;
/* 3031 */       this.contents[(localContentsOffset++)] = 0;
/* 3032 */       this.contents[(localContentsOffset++)] = 1;
/*      */ 
/* 3034 */       this.contents[(localContentsOffset++)] = 0;
/* 3035 */       this.contents[(localContentsOffset++)] = 0;
/* 3036 */       this.contents[(localContentsOffset++)] = (byte)(problemLine >> 8);
/* 3037 */       this.contents[(localContentsOffset++)] = (byte)problemLine;
/*      */ 
/* 3039 */       attributeNumber++;
/*      */     }
/*      */ 
/* 3042 */     if ((this.produceAttributes & 0x4) != 0) {
/* 3043 */       int localVariableNameIndex = 
/* 3044 */         this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
/* 3045 */       if (localContentsOffset + 8 >= this.contents.length) {
/* 3046 */         resizeContents(8);
/*      */       }
/* 3048 */       this.contents[(localContentsOffset++)] = (byte)(localVariableNameIndex >> 8);
/* 3049 */       this.contents[(localContentsOffset++)] = (byte)localVariableNameIndex;
/* 3050 */       this.contents[(localContentsOffset++)] = 0;
/* 3051 */       this.contents[(localContentsOffset++)] = 0;
/* 3052 */       this.contents[(localContentsOffset++)] = 0;
/* 3053 */       this.contents[(localContentsOffset++)] = 2;
/* 3054 */       this.contents[(localContentsOffset++)] = 0;
/* 3055 */       this.contents[(localContentsOffset++)] = 0;
/* 3056 */       attributeNumber++;
/*      */     }
/*      */ 
/* 3059 */     if ((this.produceAttributes & 0x8) != 0) {
/* 3060 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 3061 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 3062 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 3063 */         ArrayList frames = new ArrayList();
/* 3064 */         traverse(null, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, true);
/* 3065 */         int numberOfFrames = frames.size();
/* 3066 */         if (numberOfFrames > 1) {
/* 3067 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 3069 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 3070 */             resizeContents(8);
/*      */           }
/* 3072 */           int stackMapTableAttributeNameIndex = 
/* 3073 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
/* 3074 */           this.contents[(localContentsOffset++)] = (byte)(stackMapTableAttributeNameIndex >> 8);
/* 3075 */           this.contents[(localContentsOffset++)] = (byte)stackMapTableAttributeNameIndex;
/*      */ 
/* 3077 */           int stackMapTableAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 3079 */           localContentsOffset += 4;
/* 3080 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 3081 */             resizeContents(4);
/*      */           }
/* 3083 */           numberOfFrames = 0;
/* 3084 */           int numberOfFramesOffset = localContentsOffset;
/* 3085 */           localContentsOffset += 2;
/* 3086 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 3087 */             resizeContents(2);
/*      */           }
/* 3089 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 3090 */           StackMapFrame prevFrame = null;
/* 3091 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 3093 */             prevFrame = currentFrame;
/* 3094 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 3097 */             numberOfFrames++;
/* 3098 */             int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
/*      */             int numberOfDifferentLocals;
/*      */             int i;
/* 3099 */             switch (currentFrame.getFrameType(prevFrame)) {
/*      */             case 2:
/* 3101 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3102 */                 resizeContents(3);
/*      */               }
/* 3104 */               numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
/* 3105 */               this.contents[(localContentsOffset++)] = (byte)(251 + numberOfDifferentLocals);
/* 3106 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3107 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3108 */               int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
/* 3109 */               int numberOfLocals = currentFrame.getNumberOfLocals();
/* 3110 */               i = index; break;
/*      */             case 0:
/*      */             case 3:
/*      */             case 1:
/*      */             case 5:
/*      */             case 6:
/*      */             case 4: } while (true) { if (localContentsOffset + 6 >= this.contents.length) {
/* 3112 */                 resizeContents(6);
/*      */               }
/* 3114 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 3115 */               if (info == null) {
/* 3116 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 3118 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 3124 */                   this.contents[(localContentsOffset++)] = 1;
/* 3125 */                   break;
/*      */                 case 9:
/* 3127 */                   this.contents[(localContentsOffset++)] = 2;
/* 3128 */                   break;
/*      */                 case 7:
/* 3130 */                   this.contents[(localContentsOffset++)] = 4;
/* 3131 */                   i++;
/* 3132 */                   break;
/*      */                 case 8:
/* 3134 */                   this.contents[(localContentsOffset++)] = 3;
/* 3135 */                   i++;
/* 3136 */                   break;
/*      */                 case 12:
/* 3138 */                   this.contents[(localContentsOffset++)] = 5;
/* 3139 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 3141 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3142 */                   switch (info.tag) {
/*      */                   case 8:
/* 3144 */                     int offset = info.offset;
/* 3145 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3146 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 3147 */                     break;
/*      */                   case 7:
/* 3149 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3150 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3151 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 3154 */                 numberOfDifferentLocals--;
/*      */               }
/* 3110 */               i++; if (i >= currentFrame.locals.length) break; if (numberOfDifferentLocals > 0)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 3157 */               break;
/*      */ 
/* 3159 */               if (localContentsOffset + 1 >= this.contents.length) {
/* 3160 */                 resizeContents(1);
/*      */               }
/* 3162 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3163 */               break;
/*      */ 
/* 3165 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3166 */                 resizeContents(3);
/*      */               }
/* 3168 */               this.contents[(localContentsOffset++)] = -5;
/* 3169 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3170 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3171 */               break;
/*      */ 
/* 3173 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3174 */                 resizeContents(3);
/*      */               }
/* 3176 */               int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
/* 3177 */               this.contents[(localContentsOffset++)] = (byte)(251 - numberOfDifferentLocals);
/* 3178 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3179 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3180 */               break;
/*      */ 
/* 3182 */               if (localContentsOffset + 4 >= this.contents.length) {
/* 3183 */                 resizeContents(4);
/*      */               }
/* 3185 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta + 64);
/* 3186 */               if (currentFrame.stackItems[0] == null)
/* 3187 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else
/* 3189 */                 switch (currentFrame.stackItems[0].id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 3195 */                   this.contents[(localContentsOffset++)] = 1;
/* 3196 */                   break;
/*      */                 case 9:
/* 3198 */                   this.contents[(localContentsOffset++)] = 2;
/* 3199 */                   break;
/*      */                 case 7:
/* 3201 */                   this.contents[(localContentsOffset++)] = 4;
/* 3202 */                   break;
/*      */                 case 8:
/* 3204 */                   this.contents[(localContentsOffset++)] = 3;
/* 3205 */                   break;
/*      */                 case 12:
/* 3207 */                   this.contents[(localContentsOffset++)] = 5;
/* 3208 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 3210 */                   VerificationTypeInfo info = currentFrame.stackItems[0];
/* 3211 */                   byte tag = (byte)info.tag;
/* 3212 */                   this.contents[(localContentsOffset++)] = tag;
/* 3213 */                   switch (tag) {
/*      */                   case 8:
/* 3215 */                     int offset = info.offset;
/* 3216 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3217 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 3218 */                     break;
/*      */                   case 7:
/* 3220 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3221 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3222 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   default:
/* 3226 */                     break;
/*      */ 
/* 3228 */                     if (localContentsOffset + 6 >= this.contents.length) {
/* 3229 */                       resizeContents(6);
/*      */                     }
/* 3231 */                     this.contents[(localContentsOffset++)] = -9;
/* 3232 */                     this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3233 */                     this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3234 */                     if (currentFrame.stackItems[0] == null)
/* 3235 */                       this.contents[(localContentsOffset++)] = 0;
/*      */                     else
/* 3237 */                       switch (currentFrame.stackItems[0].id()) {
/*      */                       case 2:
/*      */                       case 3:
/*      */                       case 4:
/*      */                       case 5:
/*      */                       case 10:
/* 3243 */                         this.contents[(localContentsOffset++)] = 1;
/* 3244 */                         break;
/*      */                       case 9:
/* 3246 */                         this.contents[(localContentsOffset++)] = 2;
/* 3247 */                         break;
/*      */                       case 7:
/* 3249 */                         this.contents[(localContentsOffset++)] = 4;
/* 3250 */                         break;
/*      */                       case 8:
/* 3252 */                         this.contents[(localContentsOffset++)] = 3;
/* 3253 */                         break;
/*      */                       case 12:
/* 3255 */                         this.contents[(localContentsOffset++)] = 5;
/* 3256 */                         break;
/*      */                       case 6:
/*      */                       case 11:
/*      */                       default:
/* 3258 */                         VerificationTypeInfo info = currentFrame.stackItems[0];
/* 3259 */                         byte tag = (byte)info.tag;
/* 3260 */                         this.contents[(localContentsOffset++)] = tag;
/* 3261 */                         switch (tag) {
/*      */                         case 8:
/* 3263 */                           int offset = info.offset;
/* 3264 */                           this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3265 */                           this.contents[(localContentsOffset++)] = (byte)offset;
/* 3266 */                           break;
/*      */                         case 7:
/* 3268 */                           int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3269 */                           this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3270 */                           this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                         default:
/* 3274 */                           break;
/*      */ 
/* 3277 */                           if (localContentsOffset + 5 >= this.contents.length) {
/* 3278 */                             resizeContents(5);
/*      */                           }
/* 3280 */                           this.contents[(localContentsOffset++)] = -1;
/* 3281 */                           this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3282 */                           this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3283 */                           int numberOfLocalOffset = localContentsOffset;
/* 3284 */                           localContentsOffset += 2;
/* 3285 */                           int numberOfLocalEntries = 0;
/* 3286 */                           int numberOfLocals = currentFrame.getNumberOfLocals();
/* 3287 */                           int numberOfEntries = 0;
/* 3288 */                           int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 3289 */                           for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 3290 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 3291 */                               resizeContents(3);
/*      */                             }
/* 3293 */                             VerificationTypeInfo info = currentFrame.locals[i];
/* 3294 */                             if (info == null) {
/* 3295 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             } else {
/* 3297 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 3303 */                                 this.contents[(localContentsOffset++)] = 1;
/* 3304 */                                 break;
/*      */                               case 9:
/* 3306 */                                 this.contents[(localContentsOffset++)] = 2;
/* 3307 */                                 break;
/*      */                               case 7:
/* 3309 */                                 this.contents[(localContentsOffset++)] = 4;
/* 3310 */                                 i++;
/* 3311 */                                 break;
/*      */                               case 8:
/* 3313 */                                 this.contents[(localContentsOffset++)] = 3;
/* 3314 */                                 i++;
/* 3315 */                                 break;
/*      */                               case 12:
/* 3317 */                                 this.contents[(localContentsOffset++)] = 5;
/* 3318 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 3320 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3321 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 3323 */                                   int offset = info.offset;
/* 3324 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3325 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 3326 */                                   break;
/*      */                                 case 7:
/* 3328 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3329 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3330 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/* 3333 */                               numberOfLocalEntries++;
/*      */                             }
/* 3335 */                             numberOfEntries++;
/*      */                           }
/* 3337 */                           if (localContentsOffset + 4 >= this.contents.length) {
/* 3338 */                             resizeContents(4);
/*      */                           }
/* 3340 */                           this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 3341 */                           this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 3342 */                           int numberOfStackItems = currentFrame.numberOfStackItems;
/* 3343 */                           this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 3344 */                           this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 3345 */                           for (int i = 0; i < numberOfStackItems; i++) {
/* 3346 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 3347 */                               resizeContents(3);
/*      */                             }
/* 3349 */                             VerificationTypeInfo info = currentFrame.stackItems[i];
/* 3350 */                             if (info == null)
/* 3351 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             else
/* 3353 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 3359 */                                 this.contents[(localContentsOffset++)] = 1;
/* 3360 */                                 break;
/*      */                               case 9:
/* 3362 */                                 this.contents[(localContentsOffset++)] = 2;
/* 3363 */                                 break;
/*      */                               case 7:
/* 3365 */                                 this.contents[(localContentsOffset++)] = 4;
/* 3366 */                                 break;
/*      */                               case 8:
/* 3368 */                                 this.contents[(localContentsOffset++)] = 3;
/* 3369 */                                 break;
/*      */                               case 12:
/* 3371 */                                 this.contents[(localContentsOffset++)] = 5;
/* 3372 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 3374 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3375 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 3377 */                                   int offset = info.offset;
/* 3378 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3379 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 3380 */                                   break;
/*      */                                 case 7:
/* 3382 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3383 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3384 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                   }
/*      */                 } }
/*      */           }
/* 3392 */           if (numberOfFrames != 0) {
/* 3393 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 3394 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 3396 */             int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
/* 3397 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 3398 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 3399 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 3400 */             this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
/* 3401 */             attributeNumber++;
/*      */           } else {
/* 3403 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3409 */     if ((this.produceAttributes & 0x10) != 0) {
/* 3410 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 3411 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 3412 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 3413 */         ArrayList frames = new ArrayList();
/* 3414 */         traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 3415 */         int numberOfFrames = frames.size();
/* 3416 */         if (numberOfFrames > 1) {
/* 3417 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 3419 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 3420 */             resizeContents(8);
/*      */           }
/* 3422 */           int stackMapAttributeNameIndex = 
/* 3423 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
/* 3424 */           this.contents[(localContentsOffset++)] = (byte)(stackMapAttributeNameIndex >> 8);
/* 3425 */           this.contents[(localContentsOffset++)] = (byte)stackMapAttributeNameIndex;
/*      */ 
/* 3427 */           int stackMapAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 3429 */           localContentsOffset += 4;
/* 3430 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 3431 */             resizeContents(4);
/*      */           }
/* 3433 */           int numberOfFramesOffset = localContentsOffset;
/* 3434 */           localContentsOffset += 2;
/* 3435 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 3436 */             resizeContents(2);
/*      */           }
/* 3438 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 3439 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 3441 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 3444 */             int frameOffset = currentFrame.pc;
/*      */ 
/* 3446 */             if (localContentsOffset + 5 >= this.contents.length) {
/* 3447 */               resizeContents(5);
/*      */             }
/* 3449 */             this.contents[(localContentsOffset++)] = (byte)(frameOffset >> 8);
/* 3450 */             this.contents[(localContentsOffset++)] = (byte)frameOffset;
/* 3451 */             int numberOfLocalOffset = localContentsOffset;
/* 3452 */             localContentsOffset += 2;
/* 3453 */             int numberOfLocalEntries = 0;
/* 3454 */             int numberOfLocals = currentFrame.getNumberOfLocals();
/* 3455 */             int numberOfEntries = 0;
/* 3456 */             int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 3457 */             for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 3458 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3459 */                 resizeContents(3);
/*      */               }
/* 3461 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 3462 */               if (info == null) {
/* 3463 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 3465 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 3471 */                   this.contents[(localContentsOffset++)] = 1;
/* 3472 */                   break;
/*      */                 case 9:
/* 3474 */                   this.contents[(localContentsOffset++)] = 2;
/* 3475 */                   break;
/*      */                 case 7:
/* 3477 */                   this.contents[(localContentsOffset++)] = 4;
/* 3478 */                   i++;
/* 3479 */                   break;
/*      */                 case 8:
/* 3481 */                   this.contents[(localContentsOffset++)] = 3;
/* 3482 */                   i++;
/* 3483 */                   break;
/*      */                 case 12:
/* 3485 */                   this.contents[(localContentsOffset++)] = 5;
/* 3486 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 3488 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3489 */                   switch (info.tag) {
/*      */                   case 8:
/* 3491 */                     int offset = info.offset;
/* 3492 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3493 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 3494 */                     break;
/*      */                   case 7:
/* 3496 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3497 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3498 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 3501 */                 numberOfLocalEntries++;
/*      */               }
/* 3503 */               numberOfEntries++;
/*      */             }
/* 3505 */             if (localContentsOffset + 4 >= this.contents.length) {
/* 3506 */               resizeContents(4);
/*      */             }
/* 3508 */             this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 3509 */             this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 3510 */             int numberOfStackItems = currentFrame.numberOfStackItems;
/* 3511 */             this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 3512 */             this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 3513 */             for (int i = 0; i < numberOfStackItems; i++) {
/* 3514 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3515 */                 resizeContents(3);
/*      */               }
/* 3517 */               VerificationTypeInfo info = currentFrame.stackItems[i];
/* 3518 */               if (info == null)
/* 3519 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else {
/* 3521 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 3527 */                   this.contents[(localContentsOffset++)] = 1;
/* 3528 */                   break;
/*      */                 case 9:
/* 3530 */                   this.contents[(localContentsOffset++)] = 2;
/* 3531 */                   break;
/*      */                 case 7:
/* 3533 */                   this.contents[(localContentsOffset++)] = 4;
/* 3534 */                   break;
/*      */                 case 8:
/* 3536 */                   this.contents[(localContentsOffset++)] = 3;
/* 3537 */                   break;
/*      */                 case 12:
/* 3539 */                   this.contents[(localContentsOffset++)] = 5;
/* 3540 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 3542 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3543 */                   switch (info.tag) {
/*      */                   case 8:
/* 3545 */                     int offset = info.offset;
/* 3546 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3547 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 3548 */                     break;
/*      */                   case 7:
/* 3550 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3551 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3552 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 3559 */           numberOfFrames--;
/* 3560 */           if (numberOfFrames != 0) {
/* 3561 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 3562 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 3564 */             int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
/* 3565 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 3566 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 3567 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 3568 */             this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
/* 3569 */             attributeNumber++;
/*      */           } else {
/* 3571 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3579 */     if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
/* 3580 */       resizeContents(2);
/*      */     }
/* 3582 */     this.contents[(codeAttributeAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 3583 */     this.contents[codeAttributeAttributeOffset] = (byte)attributeNumber;
/*      */ 
/* 3585 */     int codeAttributeLength = localContentsOffset - (codeAttributeOffset + 6);
/* 3586 */     this.contents[(codeAttributeOffset + 2)] = (byte)(codeAttributeLength >> 24);
/* 3587 */     this.contents[(codeAttributeOffset + 3)] = (byte)(codeAttributeLength >> 16);
/* 3588 */     this.contents[(codeAttributeOffset + 4)] = (byte)(codeAttributeLength >> 8);
/* 3589 */     this.contents[(codeAttributeOffset + 5)] = (byte)codeAttributeLength;
/* 3590 */     this.contentsOffset = localContentsOffset;
/*      */   }
/*      */ 
/*      */   public void completeCodeAttributeForMissingAbstractProblemMethod(MethodBinding binding, int codeAttributeOffset, int[] startLineIndexes, int problemLine)
/*      */   {
/* 3602 */     this.contents = this.codeStream.bCodeStream;
/* 3603 */     int localContentsOffset = this.codeStream.classFileOffset;
/*      */ 
/* 3605 */     int max_stack = this.codeStream.stackMax;
/* 3606 */     this.contents[(codeAttributeOffset + 6)] = (byte)(max_stack >> 8);
/* 3607 */     this.contents[(codeAttributeOffset + 7)] = (byte)max_stack;
/* 3608 */     int max_locals = this.codeStream.maxLocals;
/* 3609 */     this.contents[(codeAttributeOffset + 8)] = (byte)(max_locals >> 8);
/* 3610 */     this.contents[(codeAttributeOffset + 9)] = (byte)max_locals;
/* 3611 */     int code_length = this.codeStream.position;
/* 3612 */     this.contents[(codeAttributeOffset + 10)] = (byte)(code_length >> 24);
/* 3613 */     this.contents[(codeAttributeOffset + 11)] = (byte)(code_length >> 16);
/* 3614 */     this.contents[(codeAttributeOffset + 12)] = (byte)(code_length >> 8);
/* 3615 */     this.contents[(codeAttributeOffset + 13)] = (byte)code_length;
/*      */ 
/* 3617 */     if (localContentsOffset + 50 >= this.contents.length) {
/* 3618 */       resizeContents(50);
/*      */     }
/* 3620 */     this.contents[(localContentsOffset++)] = 0;
/* 3621 */     this.contents[(localContentsOffset++)] = 0;
/*      */ 
/* 3623 */     int codeAttributeAttributeOffset = localContentsOffset;
/* 3624 */     int attributeNumber = 0;
/* 3625 */     localContentsOffset += 2;
/* 3626 */     if (localContentsOffset + 2 >= this.contents.length) {
/* 3627 */       resizeContents(2);
/*      */     }
/*      */ 
/* 3630 */     if ((this.produceAttributes & 0x2) != 0) {
/* 3631 */       if (localContentsOffset + 12 >= this.contents.length) {
/* 3632 */         resizeContents(12);
/*      */       }
/*      */ 
/* 3640 */       int lineNumberNameIndex = 
/* 3641 */         this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
/* 3642 */       this.contents[(localContentsOffset++)] = (byte)(lineNumberNameIndex >> 8);
/* 3643 */       this.contents[(localContentsOffset++)] = (byte)lineNumberNameIndex;
/* 3644 */       this.contents[(localContentsOffset++)] = 0;
/* 3645 */       this.contents[(localContentsOffset++)] = 0;
/* 3646 */       this.contents[(localContentsOffset++)] = 0;
/* 3647 */       this.contents[(localContentsOffset++)] = 6;
/* 3648 */       this.contents[(localContentsOffset++)] = 0;
/* 3649 */       this.contents[(localContentsOffset++)] = 1;
/* 3650 */       if (problemLine == 0) {
/* 3651 */         problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length - 1);
/*      */       }
/*      */ 
/* 3654 */       this.contents[(localContentsOffset++)] = 0;
/* 3655 */       this.contents[(localContentsOffset++)] = 0;
/* 3656 */       this.contents[(localContentsOffset++)] = (byte)(problemLine >> 8);
/* 3657 */       this.contents[(localContentsOffset++)] = (byte)problemLine;
/*      */ 
/* 3659 */       attributeNumber++;
/*      */     }
/*      */ 
/* 3662 */     if ((this.produceAttributes & 0x8) != 0) {
/* 3663 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 3664 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 3665 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 3666 */         ArrayList frames = new ArrayList();
/* 3667 */         traverse(binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 3668 */         int numberOfFrames = frames.size();
/* 3669 */         if (numberOfFrames > 1) {
/* 3670 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 3672 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 3673 */             resizeContents(8);
/*      */           }
/* 3675 */           int stackMapTableAttributeNameIndex = 
/* 3676 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
/* 3677 */           this.contents[(localContentsOffset++)] = (byte)(stackMapTableAttributeNameIndex >> 8);
/* 3678 */           this.contents[(localContentsOffset++)] = (byte)stackMapTableAttributeNameIndex;
/*      */ 
/* 3680 */           int stackMapTableAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 3682 */           localContentsOffset += 4;
/* 3683 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 3684 */             resizeContents(4);
/*      */           }
/* 3686 */           numberOfFrames = 0;
/* 3687 */           int numberOfFramesOffset = localContentsOffset;
/* 3688 */           localContentsOffset += 2;
/* 3689 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 3690 */             resizeContents(2);
/*      */           }
/* 3692 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 3693 */           StackMapFrame prevFrame = null;
/* 3694 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 3696 */             prevFrame = currentFrame;
/* 3697 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 3700 */             numberOfFrames++;
/* 3701 */             int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
/*      */             int numberOfDifferentLocals;
/*      */             int i;
/* 3702 */             switch (currentFrame.getFrameType(prevFrame)) {
/*      */             case 2:
/* 3704 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3705 */                 resizeContents(3);
/*      */               }
/* 3707 */               numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
/* 3708 */               this.contents[(localContentsOffset++)] = (byte)(251 + numberOfDifferentLocals);
/* 3709 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3710 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3711 */               int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
/* 3712 */               int numberOfLocals = currentFrame.getNumberOfLocals();
/* 3713 */               i = index; break;
/*      */             case 0:
/*      */             case 3:
/*      */             case 1:
/*      */             case 5:
/*      */             case 6:
/*      */             case 4: } while (true) { if (localContentsOffset + 6 >= this.contents.length) {
/* 3715 */                 resizeContents(6);
/*      */               }
/* 3717 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 3718 */               if (info == null) {
/* 3719 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 3721 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 3727 */                   this.contents[(localContentsOffset++)] = 1;
/* 3728 */                   break;
/*      */                 case 9:
/* 3730 */                   this.contents[(localContentsOffset++)] = 2;
/* 3731 */                   break;
/*      */                 case 7:
/* 3733 */                   this.contents[(localContentsOffset++)] = 4;
/* 3734 */                   i++;
/* 3735 */                   break;
/*      */                 case 8:
/* 3737 */                   this.contents[(localContentsOffset++)] = 3;
/* 3738 */                   i++;
/* 3739 */                   break;
/*      */                 case 12:
/* 3741 */                   this.contents[(localContentsOffset++)] = 5;
/* 3742 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 3744 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3745 */                   switch (info.tag) {
/*      */                   case 8:
/* 3747 */                     int offset = info.offset;
/* 3748 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3749 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 3750 */                     break;
/*      */                   case 7:
/* 3752 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3753 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3754 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 3757 */                 numberOfDifferentLocals--;
/*      */               }
/* 3713 */               i++; if (i >= currentFrame.locals.length) break; if (numberOfDifferentLocals > 0)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 3760 */               break;
/*      */ 
/* 3762 */               if (localContentsOffset + 1 >= this.contents.length) {
/* 3763 */                 resizeContents(1);
/*      */               }
/* 3765 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3766 */               break;
/*      */ 
/* 3768 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3769 */                 resizeContents(3);
/*      */               }
/* 3771 */               this.contents[(localContentsOffset++)] = -5;
/* 3772 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3773 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3774 */               break;
/*      */ 
/* 3776 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 3777 */                 resizeContents(3);
/*      */               }
/* 3779 */               int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
/* 3780 */               this.contents[(localContentsOffset++)] = (byte)(251 - numberOfDifferentLocals);
/* 3781 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3782 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3783 */               break;
/*      */ 
/* 3785 */               if (localContentsOffset + 4 >= this.contents.length) {
/* 3786 */                 resizeContents(4);
/*      */               }
/* 3788 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta + 64);
/* 3789 */               if (currentFrame.stackItems[0] == null)
/* 3790 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else
/* 3792 */                 switch (currentFrame.stackItems[0].id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 3798 */                   this.contents[(localContentsOffset++)] = 1;
/* 3799 */                   break;
/*      */                 case 9:
/* 3801 */                   this.contents[(localContentsOffset++)] = 2;
/* 3802 */                   break;
/*      */                 case 7:
/* 3804 */                   this.contents[(localContentsOffset++)] = 4;
/* 3805 */                   break;
/*      */                 case 8:
/* 3807 */                   this.contents[(localContentsOffset++)] = 3;
/* 3808 */                   break;
/*      */                 case 12:
/* 3810 */                   this.contents[(localContentsOffset++)] = 5;
/* 3811 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 3813 */                   VerificationTypeInfo info = currentFrame.stackItems[0];
/* 3814 */                   byte tag = (byte)info.tag;
/* 3815 */                   this.contents[(localContentsOffset++)] = tag;
/* 3816 */                   switch (tag) {
/*      */                   case 8:
/* 3818 */                     int offset = info.offset;
/* 3819 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3820 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 3821 */                     break;
/*      */                   case 7:
/* 3823 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3824 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3825 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   default:
/* 3829 */                     break;
/*      */ 
/* 3831 */                     if (localContentsOffset + 6 >= this.contents.length) {
/* 3832 */                       resizeContents(6);
/*      */                     }
/* 3834 */                     this.contents[(localContentsOffset++)] = -9;
/* 3835 */                     this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3836 */                     this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3837 */                     if (currentFrame.stackItems[0] == null)
/* 3838 */                       this.contents[(localContentsOffset++)] = 0;
/*      */                     else
/* 3840 */                       switch (currentFrame.stackItems[0].id()) {
/*      */                       case 2:
/*      */                       case 3:
/*      */                       case 4:
/*      */                       case 5:
/*      */                       case 10:
/* 3846 */                         this.contents[(localContentsOffset++)] = 1;
/* 3847 */                         break;
/*      */                       case 9:
/* 3849 */                         this.contents[(localContentsOffset++)] = 2;
/* 3850 */                         break;
/*      */                       case 7:
/* 3852 */                         this.contents[(localContentsOffset++)] = 4;
/* 3853 */                         break;
/*      */                       case 8:
/* 3855 */                         this.contents[(localContentsOffset++)] = 3;
/* 3856 */                         break;
/*      */                       case 12:
/* 3858 */                         this.contents[(localContentsOffset++)] = 5;
/* 3859 */                         break;
/*      */                       case 6:
/*      */                       case 11:
/*      */                       default:
/* 3861 */                         VerificationTypeInfo info = currentFrame.stackItems[0];
/* 3862 */                         byte tag = (byte)info.tag;
/* 3863 */                         this.contents[(localContentsOffset++)] = tag;
/* 3864 */                         switch (tag) {
/*      */                         case 8:
/* 3866 */                           int offset = info.offset;
/* 3867 */                           this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3868 */                           this.contents[(localContentsOffset++)] = (byte)offset;
/* 3869 */                           break;
/*      */                         case 7:
/* 3871 */                           int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3872 */                           this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3873 */                           this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                         default:
/* 3877 */                           break;
/*      */ 
/* 3880 */                           if (localContentsOffset + 5 >= this.contents.length) {
/* 3881 */                             resizeContents(5);
/*      */                           }
/* 3883 */                           this.contents[(localContentsOffset++)] = -1;
/* 3884 */                           this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 3885 */                           this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 3886 */                           int numberOfLocalOffset = localContentsOffset;
/* 3887 */                           localContentsOffset += 2;
/* 3888 */                           int numberOfLocalEntries = 0;
/* 3889 */                           int numberOfLocals = currentFrame.getNumberOfLocals();
/* 3890 */                           int numberOfEntries = 0;
/* 3891 */                           int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 3892 */                           for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 3893 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 3894 */                               resizeContents(3);
/*      */                             }
/* 3896 */                             VerificationTypeInfo info = currentFrame.locals[i];
/* 3897 */                             if (info == null) {
/* 3898 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             } else {
/* 3900 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 3906 */                                 this.contents[(localContentsOffset++)] = 1;
/* 3907 */                                 break;
/*      */                               case 9:
/* 3909 */                                 this.contents[(localContentsOffset++)] = 2;
/* 3910 */                                 break;
/*      */                               case 7:
/* 3912 */                                 this.contents[(localContentsOffset++)] = 4;
/* 3913 */                                 i++;
/* 3914 */                                 break;
/*      */                               case 8:
/* 3916 */                                 this.contents[(localContentsOffset++)] = 3;
/* 3917 */                                 i++;
/* 3918 */                                 break;
/*      */                               case 12:
/* 3920 */                                 this.contents[(localContentsOffset++)] = 5;
/* 3921 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 3923 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3924 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 3926 */                                   int offset = info.offset;
/* 3927 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3928 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 3929 */                                   break;
/*      */                                 case 7:
/* 3931 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3932 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3933 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/* 3936 */                               numberOfLocalEntries++;
/*      */                             }
/* 3938 */                             numberOfEntries++;
/*      */                           }
/* 3940 */                           if (localContentsOffset + 4 >= this.contents.length) {
/* 3941 */                             resizeContents(4);
/*      */                           }
/* 3943 */                           this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 3944 */                           this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 3945 */                           int numberOfStackItems = currentFrame.numberOfStackItems;
/* 3946 */                           this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 3947 */                           this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 3948 */                           for (int i = 0; i < numberOfStackItems; i++) {
/* 3949 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 3950 */                               resizeContents(3);
/*      */                             }
/* 3952 */                             VerificationTypeInfo info = currentFrame.stackItems[i];
/* 3953 */                             if (info == null)
/* 3954 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             else
/* 3956 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 3962 */                                 this.contents[(localContentsOffset++)] = 1;
/* 3963 */                                 break;
/*      */                               case 9:
/* 3965 */                                 this.contents[(localContentsOffset++)] = 2;
/* 3966 */                                 break;
/*      */                               case 7:
/* 3968 */                                 this.contents[(localContentsOffset++)] = 4;
/* 3969 */                                 break;
/*      */                               case 8:
/* 3971 */                                 this.contents[(localContentsOffset++)] = 3;
/* 3972 */                                 break;
/*      */                               case 12:
/* 3974 */                                 this.contents[(localContentsOffset++)] = 5;
/* 3975 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 3977 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 3978 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 3980 */                                   int offset = info.offset;
/* 3981 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 3982 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 3983 */                                   break;
/*      */                                 case 7:
/* 3985 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 3986 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 3987 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                   }
/*      */                 } }
/*      */           }
/* 3995 */           if (numberOfFrames != 0) {
/* 3996 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 3997 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 3999 */             int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
/* 4000 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 4001 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 4002 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 4003 */             this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
/* 4004 */             attributeNumber++;
/*      */           } else {
/* 4006 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 4012 */     if ((this.produceAttributes & 0x10) != 0) {
/* 4013 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 4014 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 4015 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 4016 */         ArrayList frames = new ArrayList();
/* 4017 */         traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 4018 */         int numberOfFrames = frames.size();
/* 4019 */         if (numberOfFrames > 1) {
/* 4020 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 4022 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 4023 */             resizeContents(8);
/*      */           }
/* 4025 */           int stackMapAttributeNameIndex = 
/* 4026 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
/* 4027 */           this.contents[(localContentsOffset++)] = (byte)(stackMapAttributeNameIndex >> 8);
/* 4028 */           this.contents[(localContentsOffset++)] = (byte)stackMapAttributeNameIndex;
/*      */ 
/* 4030 */           int stackMapAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 4032 */           localContentsOffset += 4;
/* 4033 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 4034 */             resizeContents(4);
/*      */           }
/* 4036 */           int numberOfFramesOffset = localContentsOffset;
/* 4037 */           localContentsOffset += 2;
/* 4038 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 4039 */             resizeContents(2);
/*      */           }
/* 4041 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 4042 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 4044 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 4047 */             int frameOffset = currentFrame.pc;
/*      */ 
/* 4049 */             if (localContentsOffset + 5 >= this.contents.length) {
/* 4050 */               resizeContents(5);
/*      */             }
/* 4052 */             this.contents[(localContentsOffset++)] = (byte)(frameOffset >> 8);
/* 4053 */             this.contents[(localContentsOffset++)] = (byte)frameOffset;
/* 4054 */             int numberOfLocalOffset = localContentsOffset;
/* 4055 */             localContentsOffset += 2;
/* 4056 */             int numberOfLocalEntries = 0;
/* 4057 */             int numberOfLocals = currentFrame.getNumberOfLocals();
/* 4058 */             int numberOfEntries = 0;
/* 4059 */             int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 4060 */             for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 4061 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 4062 */                 resizeContents(3);
/*      */               }
/* 4064 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 4065 */               if (info == null) {
/* 4066 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 4068 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 4074 */                   this.contents[(localContentsOffset++)] = 1;
/* 4075 */                   break;
/*      */                 case 9:
/* 4077 */                   this.contents[(localContentsOffset++)] = 2;
/* 4078 */                   break;
/*      */                 case 7:
/* 4080 */                   this.contents[(localContentsOffset++)] = 4;
/* 4081 */                   i++;
/* 4082 */                   break;
/*      */                 case 8:
/* 4084 */                   this.contents[(localContentsOffset++)] = 3;
/* 4085 */                   i++;
/* 4086 */                   break;
/*      */                 case 12:
/* 4088 */                   this.contents[(localContentsOffset++)] = 5;
/* 4089 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 4091 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 4092 */                   switch (info.tag) {
/*      */                   case 8:
/* 4094 */                     int offset = info.offset;
/* 4095 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4096 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 4097 */                     break;
/*      */                   case 7:
/* 4099 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4100 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4101 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 4104 */                 numberOfLocalEntries++;
/*      */               }
/* 4106 */               numberOfEntries++;
/*      */             }
/* 4108 */             if (localContentsOffset + 4 >= this.contents.length) {
/* 4109 */               resizeContents(4);
/*      */             }
/* 4111 */             this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 4112 */             this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 4113 */             int numberOfStackItems = currentFrame.numberOfStackItems;
/* 4114 */             this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 4115 */             this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 4116 */             for (int i = 0; i < numberOfStackItems; i++) {
/* 4117 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 4118 */                 resizeContents(3);
/*      */               }
/* 4120 */               VerificationTypeInfo info = currentFrame.stackItems[i];
/* 4121 */               if (info == null)
/* 4122 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else {
/* 4124 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 4130 */                   this.contents[(localContentsOffset++)] = 1;
/* 4131 */                   break;
/*      */                 case 9:
/* 4133 */                   this.contents[(localContentsOffset++)] = 2;
/* 4134 */                   break;
/*      */                 case 7:
/* 4136 */                   this.contents[(localContentsOffset++)] = 4;
/* 4137 */                   break;
/*      */                 case 8:
/* 4139 */                   this.contents[(localContentsOffset++)] = 3;
/* 4140 */                   break;
/*      */                 case 12:
/* 4142 */                   this.contents[(localContentsOffset++)] = 5;
/* 4143 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 4145 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 4146 */                   switch (info.tag) {
/*      */                   case 8:
/* 4148 */                     int offset = info.offset;
/* 4149 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4150 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 4151 */                     break;
/*      */                   case 7:
/* 4153 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4154 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4155 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 4162 */           numberOfFrames--;
/* 4163 */           if (numberOfFrames != 0) {
/* 4164 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 4165 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 4167 */             int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
/* 4168 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 4169 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 4170 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 4171 */             this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
/* 4172 */             attributeNumber++;
/*      */           } else {
/* 4174 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4182 */     if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
/* 4183 */       resizeContents(2);
/*      */     }
/* 4185 */     this.contents[(codeAttributeAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 4186 */     this.contents[codeAttributeAttributeOffset] = (byte)attributeNumber;
/*      */ 
/* 4188 */     int codeAttributeLength = localContentsOffset - (codeAttributeOffset + 6);
/* 4189 */     this.contents[(codeAttributeOffset + 2)] = (byte)(codeAttributeLength >> 24);
/* 4190 */     this.contents[(codeAttributeOffset + 3)] = (byte)(codeAttributeLength >> 16);
/* 4191 */     this.contents[(codeAttributeOffset + 4)] = (byte)(codeAttributeLength >> 8);
/* 4192 */     this.contents[(codeAttributeOffset + 5)] = (byte)codeAttributeLength;
/* 4193 */     this.contentsOffset = localContentsOffset;
/*      */   }
/*      */ 
/*      */   public void completeCodeAttributeForProblemMethod(AbstractMethodDeclaration method, MethodBinding binding, int codeAttributeOffset, int[] startLineIndexes, int problemLine)
/*      */   {
/* 4210 */     this.contents = this.codeStream.bCodeStream;
/* 4211 */     int localContentsOffset = this.codeStream.classFileOffset;
/*      */ 
/* 4213 */     int max_stack = this.codeStream.stackMax;
/* 4214 */     this.contents[(codeAttributeOffset + 6)] = (byte)(max_stack >> 8);
/* 4215 */     this.contents[(codeAttributeOffset + 7)] = (byte)max_stack;
/* 4216 */     int max_locals = this.codeStream.maxLocals;
/* 4217 */     this.contents[(codeAttributeOffset + 8)] = (byte)(max_locals >> 8);
/* 4218 */     this.contents[(codeAttributeOffset + 9)] = (byte)max_locals;
/* 4219 */     int code_length = this.codeStream.position;
/* 4220 */     this.contents[(codeAttributeOffset + 10)] = (byte)(code_length >> 24);
/* 4221 */     this.contents[(codeAttributeOffset + 11)] = (byte)(code_length >> 16);
/* 4222 */     this.contents[(codeAttributeOffset + 12)] = (byte)(code_length >> 8);
/* 4223 */     this.contents[(codeAttributeOffset + 13)] = (byte)code_length;
/*      */ 
/* 4225 */     if (localContentsOffset + 50 >= this.contents.length) {
/* 4226 */       resizeContents(50);
/*      */     }
/*      */ 
/* 4230 */     this.contents[(localContentsOffset++)] = 0;
/* 4231 */     this.contents[(localContentsOffset++)] = 0;
/*      */ 
/* 4233 */     int codeAttributeAttributeOffset = localContentsOffset;
/* 4234 */     int attributeNumber = 0;
/* 4235 */     localContentsOffset += 2;
/* 4236 */     if (localContentsOffset + 2 >= this.contents.length) {
/* 4237 */       resizeContents(2);
/*      */     }
/*      */ 
/* 4240 */     if ((this.produceAttributes & 0x2) != 0) {
/* 4241 */       if (localContentsOffset + 20 >= this.contents.length) {
/* 4242 */         resizeContents(20);
/*      */       }
/*      */ 
/* 4250 */       int lineNumberNameIndex = 
/* 4251 */         this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
/* 4252 */       this.contents[(localContentsOffset++)] = (byte)(lineNumberNameIndex >> 8);
/* 4253 */       this.contents[(localContentsOffset++)] = (byte)lineNumberNameIndex;
/* 4254 */       this.contents[(localContentsOffset++)] = 0;
/* 4255 */       this.contents[(localContentsOffset++)] = 0;
/* 4256 */       this.contents[(localContentsOffset++)] = 0;
/* 4257 */       this.contents[(localContentsOffset++)] = 6;
/* 4258 */       this.contents[(localContentsOffset++)] = 0;
/* 4259 */       this.contents[(localContentsOffset++)] = 1;
/* 4260 */       if (problemLine == 0) {
/* 4261 */         problemLine = Util.getLineNumber(binding.sourceStart(), startLineIndexes, 0, startLineIndexes.length - 1);
/*      */       }
/*      */ 
/* 4264 */       this.contents[(localContentsOffset++)] = 0;
/* 4265 */       this.contents[(localContentsOffset++)] = 0;
/* 4266 */       this.contents[(localContentsOffset++)] = (byte)(problemLine >> 8);
/* 4267 */       this.contents[(localContentsOffset++)] = (byte)problemLine;
/*      */ 
/* 4269 */       attributeNumber++;
/*      */     }
/*      */ 
/* 4272 */     if ((this.produceAttributes & 0x4) != 0)
/*      */     {
/* 4275 */       int numberOfEntries = 0;
/*      */ 
/* 4277 */       int localVariableNameIndex = 
/* 4278 */         this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
/* 4279 */       if (localContentsOffset + 8 >= this.contents.length) {
/* 4280 */         resizeContents(8);
/*      */       }
/* 4282 */       this.contents[(localContentsOffset++)] = (byte)(localVariableNameIndex >> 8);
/* 4283 */       this.contents[(localContentsOffset++)] = (byte)localVariableNameIndex;
/* 4284 */       int localVariableTableOffset = localContentsOffset;
/* 4285 */       localContentsOffset += 6;
/*      */ 
/* 4289 */       SourceTypeBinding declaringClassBinding = null;
/* 4290 */       boolean methodDeclarationIsStatic = this.codeStream.methodDeclaration.isStatic();
/* 4291 */       if (!methodDeclarationIsStatic) {
/* 4292 */         numberOfEntries++;
/* 4293 */         if (localContentsOffset + 10 >= this.contents.length) {
/* 4294 */           resizeContents(10);
/*      */         }
/* 4296 */         this.contents[(localContentsOffset++)] = 0;
/* 4297 */         this.contents[(localContentsOffset++)] = 0;
/* 4298 */         this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 4299 */         this.contents[(localContentsOffset++)] = (byte)code_length;
/* 4300 */         int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
/* 4301 */         this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 4302 */         this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 4303 */         declaringClassBinding = (SourceTypeBinding)this.codeStream.methodDeclaration.binding.declaringClass;
/* 4304 */         int descriptorIndex = 
/* 4305 */           this.constantPool.literalIndex(declaringClassBinding.signature());
/* 4306 */         this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 4307 */         this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/*      */ 
/* 4309 */         this.contents[(localContentsOffset++)] = 0;
/* 4310 */         this.contents[(localContentsOffset++)] = 0;
/*      */       }
/*      */ 
/* 4313 */       int genericLocalVariablesCounter = 0;
/* 4314 */       LocalVariableBinding[] genericLocalVariables = (LocalVariableBinding[])null;
/* 4315 */       int numberOfGenericEntries = 0;
/*      */       int argSize;
/*      */       int argSize;
/* 4317 */       if (binding.isConstructor()) {
/* 4318 */         ReferenceBinding declaringClass = binding.declaringClass;
/* 4319 */         if (declaringClass.isNestedType()) {
/* 4320 */           NestedTypeBinding methodDeclaringClass = (NestedTypeBinding)declaringClass;
/* 4321 */           int argSize = methodDeclaringClass.getEnclosingInstancesSlotSize();
/*      */           SyntheticArgumentBinding[] syntheticArguments;
/* 4323 */           if ((syntheticArguments = methodDeclaringClass.syntheticEnclosingInstances()) != null) {
/* 4324 */             int i = 0; for (int max = syntheticArguments.length; i < max; i++) {
/* 4325 */               LocalVariableBinding localVariable = syntheticArguments[i];
/* 4326 */               TypeBinding localVariableTypeBinding = localVariable.type;
/* 4327 */               if ((localVariableTypeBinding.isParameterizedType()) || (localVariableTypeBinding.isTypeVariable())) {
/* 4328 */                 if (genericLocalVariables == null)
/*      */                 {
/* 4330 */                   genericLocalVariables = new LocalVariableBinding[max];
/*      */                 }
/* 4332 */                 genericLocalVariables[(genericLocalVariablesCounter++)] = localVariable;
/* 4333 */                 numberOfGenericEntries++;
/*      */               }
/* 4335 */               if (localContentsOffset + 10 >= this.contents.length) {
/* 4336 */                 resizeContents(10);
/*      */               }
/*      */ 
/* 4339 */               numberOfEntries++;
/* 4340 */               this.contents[(localContentsOffset++)] = 0;
/* 4341 */               this.contents[(localContentsOffset++)] = 0;
/* 4342 */               this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 4343 */               this.contents[(localContentsOffset++)] = (byte)code_length;
/* 4344 */               int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 4345 */               this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 4346 */               this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 4347 */               int descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
/* 4348 */               this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 4349 */               this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 4350 */               int resolvedPosition = localVariable.resolvedPosition;
/* 4351 */               this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 4352 */               this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */             }
/*      */           }
/*      */         } else {
/* 4356 */           argSize = 1;
/*      */         }
/*      */       } else {
/* 4359 */         argSize = binding.isStatic() ? 0 : 1;
/*      */       }
/*      */ 
/* 4362 */       int genericArgumentsCounter = 0;
/* 4363 */       int[] genericArgumentsNameIndexes = (int[])null;
/* 4364 */       int[] genericArgumentsResolvedPositions = (int[])null;
/* 4365 */       TypeBinding[] genericArgumentsTypeBindings = (TypeBinding[])null;
/*      */ 
/* 4367 */       if (method.binding != null) {
/* 4368 */         TypeBinding[] parameters = method.binding.parameters;
/* 4369 */         Argument[] arguments = method.arguments;
/* 4370 */         if ((parameters != null) && (arguments != null)) {
/* 4371 */           int i = 0; for (int max = parameters.length; i < max; i++) {
/* 4372 */             TypeBinding argumentBinding = parameters[i];
/* 4373 */             if (localContentsOffset + 10 >= this.contents.length) {
/* 4374 */               resizeContents(10);
/*      */             }
/*      */ 
/* 4377 */             numberOfEntries++;
/* 4378 */             this.contents[(localContentsOffset++)] = 0;
/* 4379 */             this.contents[(localContentsOffset++)] = 0;
/* 4380 */             this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 4381 */             this.contents[(localContentsOffset++)] = (byte)code_length;
/* 4382 */             int nameIndex = this.constantPool.literalIndex(arguments[i].name);
/* 4383 */             this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 4384 */             this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 4385 */             int resolvedPosition = argSize;
/* 4386 */             if ((argumentBinding.isParameterizedType()) || (argumentBinding.isTypeVariable())) {
/* 4387 */               if (genericArgumentsCounter == 0)
/*      */               {
/* 4389 */                 genericArgumentsNameIndexes = new int[max];
/* 4390 */                 genericArgumentsResolvedPositions = new int[max];
/* 4391 */                 genericArgumentsTypeBindings = new TypeBinding[max];
/*      */               }
/* 4393 */               genericArgumentsNameIndexes[genericArgumentsCounter] = nameIndex;
/* 4394 */               genericArgumentsResolvedPositions[genericArgumentsCounter] = resolvedPosition;
/* 4395 */               genericArgumentsTypeBindings[(genericArgumentsCounter++)] = argumentBinding;
/*      */             }
/* 4397 */             int descriptorIndex = this.constantPool.literalIndex(argumentBinding.signature());
/* 4398 */             this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 4399 */             this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 4400 */             switch (argumentBinding.id) {
/*      */             case 7:
/*      */             case 8:
/* 4403 */               argSize += 2;
/* 4404 */               break;
/*      */             default:
/* 4406 */               argSize++;
/*      */             }
/*      */ 
/* 4409 */             this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 4410 */             this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */           }
/*      */         }
/*      */       }
/* 4414 */       int value = numberOfEntries * 10 + 2;
/* 4415 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 24);
/* 4416 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 16);
/* 4417 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 8);
/* 4418 */       this.contents[(localVariableTableOffset++)] = (byte)value;
/* 4419 */       this.contents[(localVariableTableOffset++)] = (byte)(numberOfEntries >> 8);
/* 4420 */       this.contents[localVariableTableOffset] = (byte)numberOfEntries;
/* 4421 */       attributeNumber++;
/*      */ 
/* 4423 */       boolean currentInstanceIsGeneric = 
/* 4424 */         (!methodDeclarationIsStatic) && 
/* 4425 */         (declaringClassBinding != null) && 
/* 4426 */         (declaringClassBinding.typeVariables != Binding.NO_TYPE_VARIABLES);
/* 4427 */       if ((genericLocalVariablesCounter != 0) || (genericArgumentsCounter != 0) || (currentInstanceIsGeneric))
/*      */       {
/* 4429 */         numberOfEntries = numberOfGenericEntries + genericArgumentsCounter + (currentInstanceIsGeneric ? 1 : 0);
/*      */ 
/* 4431 */         int maxOfEntries = 8 + numberOfEntries * 10;
/* 4432 */         if (localContentsOffset + maxOfEntries >= this.contents.length) {
/* 4433 */           resizeContents(maxOfEntries);
/*      */         }
/* 4435 */         int localVariableTypeNameIndex = 
/* 4436 */           this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
/* 4437 */         this.contents[(localContentsOffset++)] = (byte)(localVariableTypeNameIndex >> 8);
/* 4438 */         this.contents[(localContentsOffset++)] = (byte)localVariableTypeNameIndex;
/* 4439 */         value = numberOfEntries * 10 + 2;
/* 4440 */         this.contents[(localContentsOffset++)] = (byte)(value >> 24);
/* 4441 */         this.contents[(localContentsOffset++)] = (byte)(value >> 16);
/* 4442 */         this.contents[(localContentsOffset++)] = (byte)(value >> 8);
/* 4443 */         this.contents[(localContentsOffset++)] = (byte)value;
/* 4444 */         this.contents[(localContentsOffset++)] = (byte)(numberOfEntries >> 8);
/* 4445 */         this.contents[(localContentsOffset++)] = (byte)numberOfEntries;
/* 4446 */         if (currentInstanceIsGeneric) {
/* 4447 */           numberOfEntries++;
/* 4448 */           this.contents[(localContentsOffset++)] = 0;
/* 4449 */           this.contents[(localContentsOffset++)] = 0;
/* 4450 */           this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 4451 */           this.contents[(localContentsOffset++)] = (byte)code_length;
/* 4452 */           int nameIndex = this.constantPool.literalIndex(ConstantPool.This);
/* 4453 */           this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 4454 */           this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 4455 */           int descriptorIndex = this.constantPool.literalIndex(declaringClassBinding.genericTypeSignature());
/* 4456 */           this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 4457 */           this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 4458 */           this.contents[(localContentsOffset++)] = 0;
/* 4459 */           this.contents[(localContentsOffset++)] = 0;
/*      */         }
/*      */ 
/* 4462 */         for (int i = 0; i < genericLocalVariablesCounter; i++) {
/* 4463 */           LocalVariableBinding localVariable = genericLocalVariables[i];
/* 4464 */           this.contents[(localContentsOffset++)] = 0;
/* 4465 */           this.contents[(localContentsOffset++)] = 0;
/* 4466 */           this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 4467 */           this.contents[(localContentsOffset++)] = (byte)code_length;
/* 4468 */           int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 4469 */           this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 4470 */           this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 4471 */           int descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
/* 4472 */           this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 4473 */           this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 4474 */           int resolvedPosition = localVariable.resolvedPosition;
/* 4475 */           this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 4476 */           this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */         }
/* 4478 */         for (int i = 0; i < genericArgumentsCounter; i++) {
/* 4479 */           this.contents[(localContentsOffset++)] = 0;
/* 4480 */           this.contents[(localContentsOffset++)] = 0;
/* 4481 */           this.contents[(localContentsOffset++)] = (byte)(code_length >> 8);
/* 4482 */           this.contents[(localContentsOffset++)] = (byte)code_length;
/* 4483 */           int nameIndex = genericArgumentsNameIndexes[i];
/* 4484 */           this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 4485 */           this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 4486 */           int descriptorIndex = this.constantPool.literalIndex(genericArgumentsTypeBindings[i].genericTypeSignature());
/* 4487 */           this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 4488 */           this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 4489 */           int resolvedPosition = genericArgumentsResolvedPositions[i];
/* 4490 */           this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 4491 */           this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */         }
/* 4493 */         attributeNumber++;
/*      */       }
/*      */     }
/*      */ 
/* 4497 */     if ((this.produceAttributes & 0x8) != 0) {
/* 4498 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 4499 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 4500 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 4501 */         ArrayList frames = new ArrayList();
/* 4502 */         traverse(binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 4503 */         int numberOfFrames = frames.size();
/* 4504 */         if (numberOfFrames > 1) {
/* 4505 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 4507 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 4508 */             resizeContents(8);
/*      */           }
/* 4510 */           int stackMapTableAttributeNameIndex = 
/* 4511 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
/* 4512 */           this.contents[(localContentsOffset++)] = (byte)(stackMapTableAttributeNameIndex >> 8);
/* 4513 */           this.contents[(localContentsOffset++)] = (byte)stackMapTableAttributeNameIndex;
/*      */ 
/* 4515 */           int stackMapTableAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 4517 */           localContentsOffset += 4;
/* 4518 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 4519 */             resizeContents(4);
/*      */           }
/* 4521 */           numberOfFrames = 0;
/* 4522 */           int numberOfFramesOffset = localContentsOffset;
/* 4523 */           localContentsOffset += 2;
/* 4524 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 4525 */             resizeContents(2);
/*      */           }
/* 4527 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 4528 */           StackMapFrame prevFrame = null;
/* 4529 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 4531 */             prevFrame = currentFrame;
/* 4532 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 4535 */             numberOfFrames++;
/* 4536 */             int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
/*      */             int numberOfDifferentLocals;
/*      */             int i;
/* 4537 */             switch (currentFrame.getFrameType(prevFrame)) {
/*      */             case 2:
/* 4539 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 4540 */                 resizeContents(3);
/*      */               }
/* 4542 */               numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
/* 4543 */               this.contents[(localContentsOffset++)] = (byte)(251 + numberOfDifferentLocals);
/* 4544 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 4545 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 4546 */               int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
/* 4547 */               int numberOfLocals = currentFrame.getNumberOfLocals();
/* 4548 */               i = index; break;
/*      */             case 0:
/*      */             case 3:
/*      */             case 1:
/*      */             case 5:
/*      */             case 6:
/*      */             case 4: } while (true) { if (localContentsOffset + 6 >= this.contents.length) {
/* 4550 */                 resizeContents(6);
/*      */               }
/* 4552 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 4553 */               if (info == null) {
/* 4554 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 4556 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 4562 */                   this.contents[(localContentsOffset++)] = 1;
/* 4563 */                   break;
/*      */                 case 9:
/* 4565 */                   this.contents[(localContentsOffset++)] = 2;
/* 4566 */                   break;
/*      */                 case 7:
/* 4568 */                   this.contents[(localContentsOffset++)] = 4;
/* 4569 */                   i++;
/* 4570 */                   break;
/*      */                 case 8:
/* 4572 */                   this.contents[(localContentsOffset++)] = 3;
/* 4573 */                   i++;
/* 4574 */                   break;
/*      */                 case 12:
/* 4576 */                   this.contents[(localContentsOffset++)] = 5;
/* 4577 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 4579 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 4580 */                   switch (info.tag) {
/*      */                   case 8:
/* 4582 */                     int offset = info.offset;
/* 4583 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4584 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 4585 */                     break;
/*      */                   case 7:
/* 4587 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4588 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4589 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 4592 */                 numberOfDifferentLocals--;
/*      */               }
/* 4548 */               i++; if (i >= currentFrame.locals.length) break; if (numberOfDifferentLocals > 0)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 4595 */               break;
/*      */ 
/* 4597 */               if (localContentsOffset + 1 >= this.contents.length) {
/* 4598 */                 resizeContents(1);
/*      */               }
/* 4600 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 4601 */               break;
/*      */ 
/* 4603 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 4604 */                 resizeContents(3);
/*      */               }
/* 4606 */               this.contents[(localContentsOffset++)] = -5;
/* 4607 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 4608 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 4609 */               break;
/*      */ 
/* 4611 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 4612 */                 resizeContents(3);
/*      */               }
/* 4614 */               int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
/* 4615 */               this.contents[(localContentsOffset++)] = (byte)(251 - numberOfDifferentLocals);
/* 4616 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 4617 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 4618 */               break;
/*      */ 
/* 4620 */               if (localContentsOffset + 4 >= this.contents.length) {
/* 4621 */                 resizeContents(4);
/*      */               }
/* 4623 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta + 64);
/* 4624 */               if (currentFrame.stackItems[0] == null)
/* 4625 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else
/* 4627 */                 switch (currentFrame.stackItems[0].id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 4633 */                   this.contents[(localContentsOffset++)] = 1;
/* 4634 */                   break;
/*      */                 case 9:
/* 4636 */                   this.contents[(localContentsOffset++)] = 2;
/* 4637 */                   break;
/*      */                 case 7:
/* 4639 */                   this.contents[(localContentsOffset++)] = 4;
/* 4640 */                   break;
/*      */                 case 8:
/* 4642 */                   this.contents[(localContentsOffset++)] = 3;
/* 4643 */                   break;
/*      */                 case 12:
/* 4645 */                   this.contents[(localContentsOffset++)] = 5;
/* 4646 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 4648 */                   VerificationTypeInfo info = currentFrame.stackItems[0];
/* 4649 */                   byte tag = (byte)info.tag;
/* 4650 */                   this.contents[(localContentsOffset++)] = tag;
/* 4651 */                   switch (tag) {
/*      */                   case 8:
/* 4653 */                     int offset = info.offset;
/* 4654 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4655 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 4656 */                     break;
/*      */                   case 7:
/* 4658 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4659 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4660 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   default:
/* 4664 */                     break;
/*      */ 
/* 4666 */                     if (localContentsOffset + 6 >= this.contents.length) {
/* 4667 */                       resizeContents(6);
/*      */                     }
/* 4669 */                     this.contents[(localContentsOffset++)] = -9;
/* 4670 */                     this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 4671 */                     this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 4672 */                     if (currentFrame.stackItems[0] == null)
/* 4673 */                       this.contents[(localContentsOffset++)] = 0;
/*      */                     else
/* 4675 */                       switch (currentFrame.stackItems[0].id()) {
/*      */                       case 2:
/*      */                       case 3:
/*      */                       case 4:
/*      */                       case 5:
/*      */                       case 10:
/* 4681 */                         this.contents[(localContentsOffset++)] = 1;
/* 4682 */                         break;
/*      */                       case 9:
/* 4684 */                         this.contents[(localContentsOffset++)] = 2;
/* 4685 */                         break;
/*      */                       case 7:
/* 4687 */                         this.contents[(localContentsOffset++)] = 4;
/* 4688 */                         break;
/*      */                       case 8:
/* 4690 */                         this.contents[(localContentsOffset++)] = 3;
/* 4691 */                         break;
/*      */                       case 12:
/* 4693 */                         this.contents[(localContentsOffset++)] = 5;
/* 4694 */                         break;
/*      */                       case 6:
/*      */                       case 11:
/*      */                       default:
/* 4696 */                         VerificationTypeInfo info = currentFrame.stackItems[0];
/* 4697 */                         byte tag = (byte)info.tag;
/* 4698 */                         this.contents[(localContentsOffset++)] = tag;
/* 4699 */                         switch (tag) {
/*      */                         case 8:
/* 4701 */                           int offset = info.offset;
/* 4702 */                           this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4703 */                           this.contents[(localContentsOffset++)] = (byte)offset;
/* 4704 */                           break;
/*      */                         case 7:
/* 4706 */                           int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4707 */                           this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4708 */                           this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                         default:
/* 4712 */                           break;
/*      */ 
/* 4715 */                           if (localContentsOffset + 5 >= this.contents.length) {
/* 4716 */                             resizeContents(5);
/*      */                           }
/* 4718 */                           this.contents[(localContentsOffset++)] = -1;
/* 4719 */                           this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 4720 */                           this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 4721 */                           int numberOfLocalOffset = localContentsOffset;
/* 4722 */                           localContentsOffset += 2;
/* 4723 */                           int numberOfLocalEntries = 0;
/* 4724 */                           int numberOfLocals = currentFrame.getNumberOfLocals();
/* 4725 */                           int numberOfEntries = 0;
/* 4726 */                           int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 4727 */                           for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 4728 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 4729 */                               resizeContents(3);
/*      */                             }
/* 4731 */                             VerificationTypeInfo info = currentFrame.locals[i];
/* 4732 */                             if (info == null) {
/* 4733 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             } else {
/* 4735 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 4741 */                                 this.contents[(localContentsOffset++)] = 1;
/* 4742 */                                 break;
/*      */                               case 9:
/* 4744 */                                 this.contents[(localContentsOffset++)] = 2;
/* 4745 */                                 break;
/*      */                               case 7:
/* 4747 */                                 this.contents[(localContentsOffset++)] = 4;
/* 4748 */                                 i++;
/* 4749 */                                 break;
/*      */                               case 8:
/* 4751 */                                 this.contents[(localContentsOffset++)] = 3;
/* 4752 */                                 i++;
/* 4753 */                                 break;
/*      */                               case 12:
/* 4755 */                                 this.contents[(localContentsOffset++)] = 5;
/* 4756 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 4758 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 4759 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 4761 */                                   int offset = info.offset;
/* 4762 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4763 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 4764 */                                   break;
/*      */                                 case 7:
/* 4766 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4767 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4768 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/* 4771 */                               numberOfLocalEntries++;
/*      */                             }
/* 4773 */                             numberOfEntries++;
/*      */                           }
/* 4775 */                           if (localContentsOffset + 4 >= this.contents.length) {
/* 4776 */                             resizeContents(4);
/*      */                           }
/* 4778 */                           this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 4779 */                           this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 4780 */                           int numberOfStackItems = currentFrame.numberOfStackItems;
/* 4781 */                           this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 4782 */                           this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 4783 */                           for (int i = 0; i < numberOfStackItems; i++) {
/* 4784 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 4785 */                               resizeContents(3);
/*      */                             }
/* 4787 */                             VerificationTypeInfo info = currentFrame.stackItems[i];
/* 4788 */                             if (info == null)
/* 4789 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             else
/* 4791 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 4797 */                                 this.contents[(localContentsOffset++)] = 1;
/* 4798 */                                 break;
/*      */                               case 9:
/* 4800 */                                 this.contents[(localContentsOffset++)] = 2;
/* 4801 */                                 break;
/*      */                               case 7:
/* 4803 */                                 this.contents[(localContentsOffset++)] = 4;
/* 4804 */                                 break;
/*      */                               case 8:
/* 4806 */                                 this.contents[(localContentsOffset++)] = 3;
/* 4807 */                                 break;
/*      */                               case 12:
/* 4809 */                                 this.contents[(localContentsOffset++)] = 5;
/* 4810 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 4812 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 4813 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 4815 */                                   int offset = info.offset;
/* 4816 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4817 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 4818 */                                   break;
/*      */                                 case 7:
/* 4820 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4821 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4822 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                   }
/*      */                 } }
/*      */           }
/* 4830 */           if (numberOfFrames != 0) {
/* 4831 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 4832 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 4834 */             int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
/* 4835 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 4836 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 4837 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 4838 */             this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
/* 4839 */             attributeNumber++;
/*      */           } else {
/* 4841 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 4847 */     if ((this.produceAttributes & 0x10) != 0) {
/* 4848 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 4849 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 4850 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 4851 */         ArrayList frames = new ArrayList();
/* 4852 */         traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 4853 */         int numberOfFrames = frames.size();
/* 4854 */         if (numberOfFrames > 1) {
/* 4855 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 4857 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 4858 */             resizeContents(8);
/*      */           }
/* 4860 */           int stackMapAttributeNameIndex = 
/* 4861 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
/* 4862 */           this.contents[(localContentsOffset++)] = (byte)(stackMapAttributeNameIndex >> 8);
/* 4863 */           this.contents[(localContentsOffset++)] = (byte)stackMapAttributeNameIndex;
/*      */ 
/* 4865 */           int stackMapAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 4867 */           localContentsOffset += 4;
/* 4868 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 4869 */             resizeContents(4);
/*      */           }
/* 4871 */           int numberOfFramesOffset = localContentsOffset;
/* 4872 */           localContentsOffset += 2;
/* 4873 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 4874 */             resizeContents(2);
/*      */           }
/* 4876 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 4877 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 4879 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 4882 */             int frameOffset = currentFrame.pc;
/*      */ 
/* 4884 */             if (localContentsOffset + 5 >= this.contents.length) {
/* 4885 */               resizeContents(5);
/*      */             }
/* 4887 */             this.contents[(localContentsOffset++)] = (byte)(frameOffset >> 8);
/* 4888 */             this.contents[(localContentsOffset++)] = (byte)frameOffset;
/* 4889 */             int numberOfLocalOffset = localContentsOffset;
/* 4890 */             localContentsOffset += 2;
/* 4891 */             int numberOfLocalEntries = 0;
/* 4892 */             int numberOfLocals = currentFrame.getNumberOfLocals();
/* 4893 */             int numberOfEntries = 0;
/* 4894 */             int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 4895 */             for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 4896 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 4897 */                 resizeContents(3);
/*      */               }
/* 4899 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 4900 */               if (info == null) {
/* 4901 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 4903 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 4909 */                   this.contents[(localContentsOffset++)] = 1;
/* 4910 */                   break;
/*      */                 case 9:
/* 4912 */                   this.contents[(localContentsOffset++)] = 2;
/* 4913 */                   break;
/*      */                 case 7:
/* 4915 */                   this.contents[(localContentsOffset++)] = 4;
/* 4916 */                   i++;
/* 4917 */                   break;
/*      */                 case 8:
/* 4919 */                   this.contents[(localContentsOffset++)] = 3;
/* 4920 */                   i++;
/* 4921 */                   break;
/*      */                 case 12:
/* 4923 */                   this.contents[(localContentsOffset++)] = 5;
/* 4924 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 4926 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 4927 */                   switch (info.tag) {
/*      */                   case 8:
/* 4929 */                     int offset = info.offset;
/* 4930 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4931 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 4932 */                     break;
/*      */                   case 7:
/* 4934 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4935 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4936 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 4939 */                 numberOfLocalEntries++;
/*      */               }
/* 4941 */               numberOfEntries++;
/*      */             }
/* 4943 */             if (localContentsOffset + 4 >= this.contents.length) {
/* 4944 */               resizeContents(4);
/*      */             }
/* 4946 */             this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 4947 */             this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 4948 */             int numberOfStackItems = currentFrame.numberOfStackItems;
/* 4949 */             this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 4950 */             this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 4951 */             for (int i = 0; i < numberOfStackItems; i++) {
/* 4952 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 4953 */                 resizeContents(3);
/*      */               }
/* 4955 */               VerificationTypeInfo info = currentFrame.stackItems[i];
/* 4956 */               if (info == null)
/* 4957 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else {
/* 4959 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 4965 */                   this.contents[(localContentsOffset++)] = 1;
/* 4966 */                   break;
/*      */                 case 9:
/* 4968 */                   this.contents[(localContentsOffset++)] = 2;
/* 4969 */                   break;
/*      */                 case 7:
/* 4971 */                   this.contents[(localContentsOffset++)] = 4;
/* 4972 */                   break;
/*      */                 case 8:
/* 4974 */                   this.contents[(localContentsOffset++)] = 3;
/* 4975 */                   break;
/*      */                 case 12:
/* 4977 */                   this.contents[(localContentsOffset++)] = 5;
/* 4978 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 4980 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 4981 */                   switch (info.tag) {
/*      */                   case 8:
/* 4983 */                     int offset = info.offset;
/* 4984 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 4985 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 4986 */                     break;
/*      */                   case 7:
/* 4988 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 4989 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 4990 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 4997 */           numberOfFrames--;
/* 4998 */           if (numberOfFrames != 0) {
/* 4999 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 5000 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 5002 */             int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
/* 5003 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 5004 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 5005 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 5006 */             this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
/* 5007 */             attributeNumber++;
/*      */           } else {
/* 5009 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5016 */     if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
/* 5017 */       resizeContents(2);
/*      */     }
/* 5019 */     this.contents[(codeAttributeAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 5020 */     this.contents[codeAttributeAttributeOffset] = (byte)attributeNumber;
/*      */ 
/* 5022 */     int codeAttributeLength = localContentsOffset - (codeAttributeOffset + 6);
/* 5023 */     this.contents[(codeAttributeOffset + 2)] = (byte)(codeAttributeLength >> 24);
/* 5024 */     this.contents[(codeAttributeOffset + 3)] = (byte)(codeAttributeLength >> 16);
/* 5025 */     this.contents[(codeAttributeOffset + 4)] = (byte)(codeAttributeLength >> 8);
/* 5026 */     this.contents[(codeAttributeOffset + 5)] = (byte)codeAttributeLength;
/* 5027 */     this.contentsOffset = localContentsOffset;
/*      */   }
/*      */ 
/*      */   public void completeCodeAttributeForSyntheticMethod(boolean hasExceptionHandlers, SyntheticMethodBinding binding, int codeAttributeOffset, int[] startLineIndexes)
/*      */   {
/* 5049 */     this.contents = this.codeStream.bCodeStream;
/* 5050 */     int localContentsOffset = this.codeStream.classFileOffset;
/*      */ 
/* 5055 */     int max_stack = this.codeStream.stackMax;
/* 5056 */     this.contents[(codeAttributeOffset + 6)] = (byte)(max_stack >> 8);
/* 5057 */     this.contents[(codeAttributeOffset + 7)] = (byte)max_stack;
/* 5058 */     int max_locals = this.codeStream.maxLocals;
/* 5059 */     this.contents[(codeAttributeOffset + 8)] = (byte)(max_locals >> 8);
/* 5060 */     this.contents[(codeAttributeOffset + 9)] = (byte)max_locals;
/* 5061 */     int code_length = this.codeStream.position;
/* 5062 */     this.contents[(codeAttributeOffset + 10)] = (byte)(code_length >> 24);
/* 5063 */     this.contents[(codeAttributeOffset + 11)] = (byte)(code_length >> 16);
/* 5064 */     this.contents[(codeAttributeOffset + 12)] = (byte)(code_length >> 8);
/* 5065 */     this.contents[(codeAttributeOffset + 13)] = (byte)code_length;
/* 5066 */     if (localContentsOffset + 40 >= this.contents.length) {
/* 5067 */       resizeContents(40);
/*      */     }
/*      */ 
/* 5070 */     boolean addStackMaps = (this.produceAttributes & 0x8) != 0;
/* 5071 */     if (hasExceptionHandlers)
/*      */     {
/* 5073 */       ExceptionLabel[] exceptionLabels = this.codeStream.exceptionLabels;
/* 5074 */       int exceptionHandlersCount = 0;
/* 5075 */       int i = 0; for (int length = this.codeStream.exceptionLabelsCounter; i < length; i++) {
/* 5076 */         exceptionHandlersCount += this.codeStream.exceptionLabels[i].count / 2;
/*      */       }
/* 5078 */       int exSize = exceptionHandlersCount * 8 + 2;
/* 5079 */       if (exSize + localContentsOffset >= this.contents.length) {
/* 5080 */         resizeContents(exSize);
/*      */       }
/*      */ 
/* 5084 */       this.contents[(localContentsOffset++)] = (byte)(exceptionHandlersCount >> 8);
/* 5085 */       this.contents[(localContentsOffset++)] = (byte)exceptionHandlersCount;
/* 5086 */       int i = 0; for (int max = this.codeStream.exceptionLabelsCounter; i < max; i++) {
/* 5087 */         ExceptionLabel exceptionLabel = exceptionLabels[i];
/* 5088 */         if (exceptionLabel != null) {
/* 5089 */           int iRange = 0; int maxRange = exceptionLabel.count;
/* 5090 */           if ((maxRange & 0x1) != 0) {
/* 5091 */             this.referenceBinding.scope.problemReporter().abortDueToInternalError(
/* 5092 */               Messages.bind(Messages.abort_invalidExceptionAttribute, new String(binding.selector), 
/* 5093 */               this.referenceBinding.scope.problemReporter().referenceContext));
/*      */           }
/* 5095 */           while (iRange < maxRange) {
/* 5096 */             int start = exceptionLabel.ranges[(iRange++)];
/* 5097 */             this.contents[(localContentsOffset++)] = (byte)(start >> 8);
/* 5098 */             this.contents[(localContentsOffset++)] = (byte)start;
/* 5099 */             int end = exceptionLabel.ranges[(iRange++)];
/* 5100 */             this.contents[(localContentsOffset++)] = (byte)(end >> 8);
/* 5101 */             this.contents[(localContentsOffset++)] = (byte)end;
/* 5102 */             int handlerPC = exceptionLabel.position;
/* 5103 */             if (addStackMaps) {
/* 5104 */               StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 5105 */               stackMapFrameCodeStream.addFramePosition(handlerPC);
/*      */             }
/*      */ 
/* 5108 */             this.contents[(localContentsOffset++)] = (byte)(handlerPC >> 8);
/* 5109 */             this.contents[(localContentsOffset++)] = (byte)handlerPC;
/* 5110 */             if (exceptionLabel.exceptionType == null)
/*      */             {
/* 5112 */               this.contents[(localContentsOffset++)] = 0;
/* 5113 */               this.contents[(localContentsOffset++)] = 0;
/*      */             }
/*      */             else
/*      */             {
/*      */               int nameIndex;
/*      */               int nameIndex;
/*      */               int nameIndex;
/* 5116 */               switch (exceptionLabel.exceptionType.id)
/*      */               {
/*      */               case 12:
/* 5119 */                 nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangClassNotFoundExceptionConstantPoolName);
/* 5120 */                 break;
/*      */               case 7:
/* 5123 */                 nameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangNoSuchFieldErrorConstantPoolName);
/* 5124 */                 break;
/*      */               default:
/* 5126 */                 nameIndex = this.constantPool.literalIndexForType(exceptionLabel.exceptionType);
/*      */               }
/* 5128 */               this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 5129 */               this.contents[(localContentsOffset++)] = (byte)nameIndex;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 5137 */       this.contents[(localContentsOffset++)] = 0;
/* 5138 */       this.contents[(localContentsOffset++)] = 0;
/*      */     }
/*      */ 
/* 5141 */     int codeAttributeAttributeOffset = localContentsOffset;
/* 5142 */     int attributeNumber = 0;
/*      */ 
/* 5144 */     localContentsOffset += 2;
/* 5145 */     if (localContentsOffset + 2 >= this.contents.length) {
/* 5146 */       resizeContents(2);
/*      */     }
/*      */ 
/* 5150 */     if ((this.produceAttributes & 0x2) != 0) {
/* 5151 */       if (localContentsOffset + 12 >= this.contents.length) {
/* 5152 */         resizeContents(12);
/*      */       }
/* 5154 */       int index = 0;
/* 5155 */       int lineNumberNameIndex = 
/* 5156 */         this.constantPool.literalIndex(AttributeNamesConstants.LineNumberTableName);
/* 5157 */       this.contents[(localContentsOffset++)] = (byte)(lineNumberNameIndex >> 8);
/* 5158 */       this.contents[(localContentsOffset++)] = (byte)lineNumberNameIndex;
/* 5159 */       int lineNumberTableOffset = localContentsOffset;
/* 5160 */       localContentsOffset += 6;
/*      */ 
/* 5163 */       index = Util.getLineNumber(binding.sourceStart, startLineIndexes, 0, startLineIndexes.length - 1);
/* 5164 */       this.contents[(localContentsOffset++)] = 0;
/* 5165 */       this.contents[(localContentsOffset++)] = 0;
/* 5166 */       this.contents[(localContentsOffset++)] = (byte)(index >> 8);
/* 5167 */       this.contents[(localContentsOffset++)] = (byte)index;
/*      */ 
/* 5169 */       this.contents[(lineNumberTableOffset++)] = 0;
/* 5170 */       this.contents[(lineNumberTableOffset++)] = 0;
/* 5171 */       this.contents[(lineNumberTableOffset++)] = 0;
/* 5172 */       this.contents[(lineNumberTableOffset++)] = 6;
/* 5173 */       this.contents[(lineNumberTableOffset++)] = 0;
/* 5174 */       this.contents[(lineNumberTableOffset++)] = 1;
/* 5175 */       attributeNumber++;
/*      */     }
/*      */ 
/* 5178 */     if ((this.produceAttributes & 0x4) != 0) {
/* 5179 */       int numberOfEntries = 0;
/* 5180 */       int localVariableNameIndex = 
/* 5181 */         this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTableName);
/* 5182 */       if (localContentsOffset + 8 > this.contents.length) {
/* 5183 */         resizeContents(8);
/*      */       }
/* 5185 */       this.contents[(localContentsOffset++)] = (byte)(localVariableNameIndex >> 8);
/* 5186 */       this.contents[(localContentsOffset++)] = (byte)localVariableNameIndex;
/* 5187 */       int localVariableTableOffset = localContentsOffset;
/* 5188 */       localContentsOffset += 6;
/*      */ 
/* 5194 */       int genericLocalVariablesCounter = 0;
/* 5195 */       LocalVariableBinding[] genericLocalVariables = (LocalVariableBinding[])null;
/* 5196 */       int numberOfGenericEntries = 0;
/*      */ 
/* 5198 */       int i = 0; for (int max = this.codeStream.allLocalsCounter; i < max; i++) {
/* 5199 */         LocalVariableBinding localVariable = this.codeStream.locals[i];
/* 5200 */         if (localVariable.declaration != null) {
/* 5201 */           TypeBinding localVariableTypeBinding = localVariable.type;
/* 5202 */           boolean isParameterizedType = (localVariableTypeBinding.isParameterizedType()) || (localVariableTypeBinding.isTypeVariable());
/* 5203 */           if ((localVariable.initializationCount != 0) && (isParameterizedType)) {
/* 5204 */             if (genericLocalVariables == null)
/*      */             {
/* 5206 */               genericLocalVariables = new LocalVariableBinding[max];
/*      */             }
/* 5208 */             genericLocalVariables[(genericLocalVariablesCounter++)] = localVariable;
/*      */           }
/* 5210 */           for (int j = 0; j < localVariable.initializationCount; j++) {
/* 5211 */             int startPC = localVariable.initializationPCs[(j << 1)];
/* 5212 */             int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 5213 */             if (startPC != endPC) {
/* 5214 */               if (endPC == -1) {
/* 5215 */                 localVariable.declaringScope.problemReporter().abortDueToInternalError(
/* 5216 */                   Messages.bind(Messages.abort_invalidAttribute, new String(localVariable.name)), 
/* 5217 */                   (ASTNode)localVariable.declaringScope.methodScope().referenceContext);
/*      */               }
/* 5219 */               if (localContentsOffset + 10 > this.contents.length) {
/* 5220 */                 resizeContents(10);
/*      */               }
/*      */ 
/* 5223 */               numberOfEntries++;
/* 5224 */               if (isParameterizedType) {
/* 5225 */                 numberOfGenericEntries++;
/*      */               }
/* 5227 */               this.contents[(localContentsOffset++)] = (byte)(startPC >> 8);
/* 5228 */               this.contents[(localContentsOffset++)] = (byte)startPC;
/* 5229 */               int length = endPC - startPC;
/* 5230 */               this.contents[(localContentsOffset++)] = (byte)(length >> 8);
/* 5231 */               this.contents[(localContentsOffset++)] = (byte)length;
/* 5232 */               int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 5233 */               this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 5234 */               this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 5235 */               int descriptorIndex = this.constantPool.literalIndex(localVariableTypeBinding.signature());
/* 5236 */               this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 5237 */               this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 5238 */               int resolvedPosition = localVariable.resolvedPosition;
/* 5239 */               this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 5240 */               this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 5244 */       int value = numberOfEntries * 10 + 2;
/* 5245 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 24);
/* 5246 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 16);
/* 5247 */       this.contents[(localVariableTableOffset++)] = (byte)(value >> 8);
/* 5248 */       this.contents[(localVariableTableOffset++)] = (byte)value;
/* 5249 */       this.contents[(localVariableTableOffset++)] = (byte)(numberOfEntries >> 8);
/* 5250 */       this.contents[localVariableTableOffset] = (byte)numberOfEntries;
/* 5251 */       attributeNumber++;
/*      */ 
/* 5253 */       if (genericLocalVariablesCounter != 0)
/*      */       {
/* 5255 */         int maxOfEntries = 8 + numberOfGenericEntries * 10;
/*      */ 
/* 5257 */         if (localContentsOffset + maxOfEntries >= this.contents.length) {
/* 5258 */           resizeContents(maxOfEntries);
/*      */         }
/* 5260 */         int localVariableTypeNameIndex = 
/* 5261 */           this.constantPool.literalIndex(AttributeNamesConstants.LocalVariableTypeTableName);
/* 5262 */         this.contents[(localContentsOffset++)] = (byte)(localVariableTypeNameIndex >> 8);
/* 5263 */         this.contents[(localContentsOffset++)] = (byte)localVariableTypeNameIndex;
/* 5264 */         value = numberOfGenericEntries * 10 + 2;
/* 5265 */         this.contents[(localContentsOffset++)] = (byte)(value >> 24);
/* 5266 */         this.contents[(localContentsOffset++)] = (byte)(value >> 16);
/* 5267 */         this.contents[(localContentsOffset++)] = (byte)(value >> 8);
/* 5268 */         this.contents[(localContentsOffset++)] = (byte)value;
/* 5269 */         this.contents[(localContentsOffset++)] = (byte)(numberOfGenericEntries >> 8);
/* 5270 */         this.contents[(localContentsOffset++)] = (byte)numberOfGenericEntries;
/*      */ 
/* 5272 */         for (int i = 0; i < genericLocalVariablesCounter; i++) {
/* 5273 */           LocalVariableBinding localVariable = genericLocalVariables[i];
/* 5274 */           for (int j = 0; j < localVariable.initializationCount; j++) {
/* 5275 */             int startPC = localVariable.initializationPCs[(j << 1)];
/* 5276 */             int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 5277 */             if (startPC == endPC)
/*      */               continue;
/* 5279 */             this.contents[(localContentsOffset++)] = (byte)(startPC >> 8);
/* 5280 */             this.contents[(localContentsOffset++)] = (byte)startPC;
/* 5281 */             int length = endPC - startPC;
/* 5282 */             this.contents[(localContentsOffset++)] = (byte)(length >> 8);
/* 5283 */             this.contents[(localContentsOffset++)] = (byte)length;
/* 5284 */             int nameIndex = this.constantPool.literalIndex(localVariable.name);
/* 5285 */             this.contents[(localContentsOffset++)] = (byte)(nameIndex >> 8);
/* 5286 */             this.contents[(localContentsOffset++)] = (byte)nameIndex;
/* 5287 */             int descriptorIndex = this.constantPool.literalIndex(localVariable.type.genericTypeSignature());
/* 5288 */             this.contents[(localContentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 5289 */             this.contents[(localContentsOffset++)] = (byte)descriptorIndex;
/* 5290 */             int resolvedPosition = localVariable.resolvedPosition;
/* 5291 */             this.contents[(localContentsOffset++)] = (byte)(resolvedPosition >> 8);
/* 5292 */             this.contents[(localContentsOffset++)] = (byte)resolvedPosition;
/*      */           }
/*      */         }
/*      */ 
/* 5296 */         attributeNumber++;
/*      */       }
/*      */     }
/*      */ 
/* 5300 */     if (addStackMaps) {
/* 5301 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 5302 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 5303 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 5304 */         ArrayList frames = new ArrayList();
/* 5305 */         traverse(binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 5306 */         int numberOfFrames = frames.size();
/* 5307 */         if (numberOfFrames > 1) {
/* 5308 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 5310 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 5311 */             resizeContents(8);
/*      */           }
/* 5313 */           int stackMapTableAttributeNameIndex = 
/* 5314 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapTableName);
/* 5315 */           this.contents[(localContentsOffset++)] = (byte)(stackMapTableAttributeNameIndex >> 8);
/* 5316 */           this.contents[(localContentsOffset++)] = (byte)stackMapTableAttributeNameIndex;
/*      */ 
/* 5318 */           int stackMapTableAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 5320 */           localContentsOffset += 4;
/* 5321 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 5322 */             resizeContents(4);
/*      */           }
/* 5324 */           int numberOfFramesOffset = localContentsOffset;
/* 5325 */           localContentsOffset += 2;
/* 5326 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 5327 */             resizeContents(2);
/*      */           }
/* 5329 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 5330 */           StackMapFrame prevFrame = null;
/* 5331 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 5333 */             prevFrame = currentFrame;
/* 5334 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 5337 */             int offsetDelta = currentFrame.getOffsetDelta(prevFrame);
/*      */             int numberOfDifferentLocals;
/*      */             int i;
/* 5338 */             switch (currentFrame.getFrameType(prevFrame)) {
/*      */             case 2:
/* 5340 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 5341 */                 resizeContents(3);
/*      */               }
/* 5343 */               numberOfDifferentLocals = currentFrame.numberOfDifferentLocals(prevFrame);
/* 5344 */               this.contents[(localContentsOffset++)] = (byte)(251 + numberOfDifferentLocals);
/* 5345 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 5346 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 5347 */               int index = currentFrame.getIndexOfDifferentLocals(numberOfDifferentLocals);
/* 5348 */               int numberOfLocals = currentFrame.getNumberOfLocals();
/* 5349 */               i = index; break;
/*      */             case 0:
/*      */             case 3:
/*      */             case 1:
/*      */             case 5:
/*      */             case 6:
/*      */             case 4: } while (true) { if (localContentsOffset + 6 >= this.contents.length) {
/* 5351 */                 resizeContents(6);
/*      */               }
/* 5353 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 5354 */               if (info == null) {
/* 5355 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 5357 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 5363 */                   this.contents[(localContentsOffset++)] = 1;
/* 5364 */                   break;
/*      */                 case 9:
/* 5366 */                   this.contents[(localContentsOffset++)] = 2;
/* 5367 */                   break;
/*      */                 case 7:
/* 5369 */                   this.contents[(localContentsOffset++)] = 4;
/* 5370 */                   i++;
/* 5371 */                   break;
/*      */                 case 8:
/* 5373 */                   this.contents[(localContentsOffset++)] = 3;
/* 5374 */                   i++;
/* 5375 */                   break;
/*      */                 case 12:
/* 5377 */                   this.contents[(localContentsOffset++)] = 5;
/* 5378 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 5380 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 5381 */                   switch (info.tag) {
/*      */                   case 8:
/* 5383 */                     int offset = info.offset;
/* 5384 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 5385 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 5386 */                     break;
/*      */                   case 7:
/* 5388 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 5389 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 5390 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 5393 */                 numberOfDifferentLocals--;
/*      */               }
/* 5349 */               i++; if (i >= currentFrame.locals.length) break; if (numberOfDifferentLocals > 0)
/*      */               {
/*      */                 continue;
/*      */               }
/*      */ 
/* 5396 */               break;
/*      */ 
/* 5398 */               if (localContentsOffset + 1 >= this.contents.length) {
/* 5399 */                 resizeContents(1);
/*      */               }
/* 5401 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 5402 */               break;
/*      */ 
/* 5404 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 5405 */                 resizeContents(3);
/*      */               }
/* 5407 */               this.contents[(localContentsOffset++)] = -5;
/* 5408 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 5409 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 5410 */               break;
/*      */ 
/* 5412 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 5413 */                 resizeContents(3);
/*      */               }
/* 5415 */               int numberOfDifferentLocals = -currentFrame.numberOfDifferentLocals(prevFrame);
/* 5416 */               this.contents[(localContentsOffset++)] = (byte)(251 - numberOfDifferentLocals);
/* 5417 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 5418 */               this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 5419 */               break;
/*      */ 
/* 5421 */               if (localContentsOffset + 4 >= this.contents.length) {
/* 5422 */                 resizeContents(4);
/*      */               }
/* 5424 */               this.contents[(localContentsOffset++)] = (byte)(offsetDelta + 64);
/* 5425 */               if (currentFrame.stackItems[0] == null)
/* 5426 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else
/* 5428 */                 switch (currentFrame.stackItems[0].id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 5434 */                   this.contents[(localContentsOffset++)] = 1;
/* 5435 */                   break;
/*      */                 case 9:
/* 5437 */                   this.contents[(localContentsOffset++)] = 2;
/* 5438 */                   break;
/*      */                 case 7:
/* 5440 */                   this.contents[(localContentsOffset++)] = 4;
/* 5441 */                   break;
/*      */                 case 8:
/* 5443 */                   this.contents[(localContentsOffset++)] = 3;
/* 5444 */                   break;
/*      */                 case 12:
/* 5446 */                   this.contents[(localContentsOffset++)] = 5;
/* 5447 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 5449 */                   VerificationTypeInfo info = currentFrame.stackItems[0];
/* 5450 */                   byte tag = (byte)info.tag;
/* 5451 */                   this.contents[(localContentsOffset++)] = tag;
/* 5452 */                   switch (tag) {
/*      */                   case 8:
/* 5454 */                     int offset = info.offset;
/* 5455 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 5456 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 5457 */                     break;
/*      */                   case 7:
/* 5459 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 5460 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 5461 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   default:
/* 5465 */                     break;
/*      */ 
/* 5467 */                     if (localContentsOffset + 6 >= this.contents.length) {
/* 5468 */                       resizeContents(6);
/*      */                     }
/* 5470 */                     this.contents[(localContentsOffset++)] = -9;
/* 5471 */                     this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 5472 */                     this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 5473 */                     if (currentFrame.stackItems[0] == null)
/* 5474 */                       this.contents[(localContentsOffset++)] = 0;
/*      */                     else
/* 5476 */                       switch (currentFrame.stackItems[0].id()) {
/*      */                       case 2:
/*      */                       case 3:
/*      */                       case 4:
/*      */                       case 5:
/*      */                       case 10:
/* 5482 */                         this.contents[(localContentsOffset++)] = 1;
/* 5483 */                         break;
/*      */                       case 9:
/* 5485 */                         this.contents[(localContentsOffset++)] = 2;
/* 5486 */                         break;
/*      */                       case 7:
/* 5488 */                         this.contents[(localContentsOffset++)] = 4;
/* 5489 */                         break;
/*      */                       case 8:
/* 5491 */                         this.contents[(localContentsOffset++)] = 3;
/* 5492 */                         break;
/*      */                       case 12:
/* 5494 */                         this.contents[(localContentsOffset++)] = 5;
/* 5495 */                         break;
/*      */                       case 6:
/*      */                       case 11:
/*      */                       default:
/* 5497 */                         VerificationTypeInfo info = currentFrame.stackItems[0];
/* 5498 */                         byte tag = (byte)info.tag;
/* 5499 */                         this.contents[(localContentsOffset++)] = tag;
/* 5500 */                         switch (tag) {
/*      */                         case 8:
/* 5502 */                           int offset = info.offset;
/* 5503 */                           this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 5504 */                           this.contents[(localContentsOffset++)] = (byte)offset;
/* 5505 */                           break;
/*      */                         case 7:
/* 5507 */                           int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 5508 */                           this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 5509 */                           this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                         default:
/* 5513 */                           break;
/*      */ 
/* 5516 */                           if (localContentsOffset + 5 >= this.contents.length) {
/* 5517 */                             resizeContents(5);
/*      */                           }
/* 5519 */                           this.contents[(localContentsOffset++)] = -1;
/* 5520 */                           this.contents[(localContentsOffset++)] = (byte)(offsetDelta >> 8);
/* 5521 */                           this.contents[(localContentsOffset++)] = (byte)offsetDelta;
/* 5522 */                           int numberOfLocalOffset = localContentsOffset;
/* 5523 */                           localContentsOffset += 2;
/* 5524 */                           int numberOfLocalEntries = 0;
/* 5525 */                           int numberOfLocals = currentFrame.getNumberOfLocals();
/* 5526 */                           int numberOfEntries = 0;
/* 5527 */                           int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 5528 */                           for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 5529 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 5530 */                               resizeContents(3);
/*      */                             }
/* 5532 */                             VerificationTypeInfo info = currentFrame.locals[i];
/* 5533 */                             if (info == null) {
/* 5534 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             } else {
/* 5536 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 5542 */                                 this.contents[(localContentsOffset++)] = 1;
/* 5543 */                                 break;
/*      */                               case 9:
/* 5545 */                                 this.contents[(localContentsOffset++)] = 2;
/* 5546 */                                 break;
/*      */                               case 7:
/* 5548 */                                 this.contents[(localContentsOffset++)] = 4;
/* 5549 */                                 i++;
/* 5550 */                                 break;
/*      */                               case 8:
/* 5552 */                                 this.contents[(localContentsOffset++)] = 3;
/* 5553 */                                 i++;
/* 5554 */                                 break;
/*      */                               case 12:
/* 5556 */                                 this.contents[(localContentsOffset++)] = 5;
/* 5557 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 5559 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 5560 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 5562 */                                   int offset = info.offset;
/* 5563 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 5564 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 5565 */                                   break;
/*      */                                 case 7:
/* 5567 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 5568 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 5569 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/* 5572 */                               numberOfLocalEntries++;
/*      */                             }
/* 5574 */                             numberOfEntries++;
/*      */                           }
/* 5576 */                           if (localContentsOffset + 4 >= this.contents.length) {
/* 5577 */                             resizeContents(4);
/*      */                           }
/* 5579 */                           this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 5580 */                           this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 5581 */                           int numberOfStackItems = currentFrame.numberOfStackItems;
/* 5582 */                           this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 5583 */                           this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 5584 */                           for (int i = 0; i < numberOfStackItems; i++) {
/* 5585 */                             if (localContentsOffset + 3 >= this.contents.length) {
/* 5586 */                               resizeContents(3);
/*      */                             }
/* 5588 */                             VerificationTypeInfo info = currentFrame.stackItems[i];
/* 5589 */                             if (info == null)
/* 5590 */                               this.contents[(localContentsOffset++)] = 0;
/*      */                             else
/* 5592 */                               switch (info.id()) {
/*      */                               case 2:
/*      */                               case 3:
/*      */                               case 4:
/*      */                               case 5:
/*      */                               case 10:
/* 5598 */                                 this.contents[(localContentsOffset++)] = 1;
/* 5599 */                                 break;
/*      */                               case 9:
/* 5601 */                                 this.contents[(localContentsOffset++)] = 2;
/* 5602 */                                 break;
/*      */                               case 7:
/* 5604 */                                 this.contents[(localContentsOffset++)] = 4;
/* 5605 */                                 break;
/*      */                               case 8:
/* 5607 */                                 this.contents[(localContentsOffset++)] = 3;
/* 5608 */                                 break;
/*      */                               case 12:
/* 5610 */                                 this.contents[(localContentsOffset++)] = 5;
/* 5611 */                                 break;
/*      */                               case 6:
/*      */                               case 11:
/*      */                               default:
/* 5613 */                                 this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 5614 */                                 switch (info.tag) {
/*      */                                 case 8:
/* 5616 */                                   int offset = info.offset;
/* 5617 */                                   this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 5618 */                                   this.contents[(localContentsOffset++)] = (byte)offset;
/* 5619 */                                   break;
/*      */                                 case 7:
/* 5621 */                                   int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 5622 */                                   this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 5623 */                                   this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                                 }
/*      */                               }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */                   }
/*      */                 } }
/*      */           }
/* 5631 */           numberOfFrames--;
/* 5632 */           if (numberOfFrames != 0) {
/* 5633 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 5634 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 5636 */             int attributeLength = localContentsOffset - stackMapTableAttributeLengthOffset - 4;
/* 5637 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 5638 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 5639 */             this.contents[(stackMapTableAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 5640 */             this.contents[stackMapTableAttributeLengthOffset] = (byte)attributeLength;
/* 5641 */             attributeNumber++;
/*      */           } else {
/* 5643 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 5649 */     if ((this.produceAttributes & 0x10) != 0) {
/* 5650 */       StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 5651 */       stackMapFrameCodeStream.removeFramePosition(code_length);
/* 5652 */       if (stackMapFrameCodeStream.hasFramePositions()) {
/* 5653 */         ArrayList frames = new ArrayList();
/* 5654 */         traverse(this.codeStream.methodDeclaration.binding, max_locals, this.contents, codeAttributeOffset + 14, code_length, frames, false);
/* 5655 */         int numberOfFrames = frames.size();
/* 5656 */         if (numberOfFrames > 1) {
/* 5657 */           int stackMapTableAttributeOffset = localContentsOffset;
/*      */ 
/* 5659 */           if (localContentsOffset + 8 >= this.contents.length) {
/* 5660 */             resizeContents(8);
/*      */           }
/* 5662 */           int stackMapAttributeNameIndex = 
/* 5663 */             this.constantPool.literalIndex(AttributeNamesConstants.StackMapName);
/* 5664 */           this.contents[(localContentsOffset++)] = (byte)(stackMapAttributeNameIndex >> 8);
/* 5665 */           this.contents[(localContentsOffset++)] = (byte)stackMapAttributeNameIndex;
/*      */ 
/* 5667 */           int stackMapAttributeLengthOffset = localContentsOffset;
/*      */ 
/* 5669 */           localContentsOffset += 4;
/* 5670 */           if (localContentsOffset + 4 >= this.contents.length) {
/* 5671 */             resizeContents(4);
/*      */           }
/* 5673 */           int numberOfFramesOffset = localContentsOffset;
/* 5674 */           localContentsOffset += 2;
/* 5675 */           if (localContentsOffset + 2 >= this.contents.length) {
/* 5676 */             resizeContents(2);
/*      */           }
/* 5678 */           StackMapFrame currentFrame = (StackMapFrame)frames.get(0);
/* 5679 */           for (int j = 1; j < numberOfFrames; j++)
/*      */           {
/* 5681 */             currentFrame = (StackMapFrame)frames.get(j);
/*      */ 
/* 5684 */             int frameOffset = currentFrame.pc;
/*      */ 
/* 5686 */             if (localContentsOffset + 5 >= this.contents.length) {
/* 5687 */               resizeContents(5);
/*      */             }
/* 5689 */             this.contents[(localContentsOffset++)] = (byte)(frameOffset >> 8);
/* 5690 */             this.contents[(localContentsOffset++)] = (byte)frameOffset;
/* 5691 */             int numberOfLocalOffset = localContentsOffset;
/* 5692 */             localContentsOffset += 2;
/* 5693 */             int numberOfLocalEntries = 0;
/* 5694 */             int numberOfLocals = currentFrame.getNumberOfLocals();
/* 5695 */             int numberOfEntries = 0;
/* 5696 */             int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
/* 5697 */             for (int i = 0; (i < localsLength) && (numberOfLocalEntries < numberOfLocals); i++) {
/* 5698 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 5699 */                 resizeContents(3);
/*      */               }
/* 5701 */               VerificationTypeInfo info = currentFrame.locals[i];
/* 5702 */               if (info == null) {
/* 5703 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               } else {
/* 5705 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 5711 */                   this.contents[(localContentsOffset++)] = 1;
/* 5712 */                   break;
/*      */                 case 9:
/* 5714 */                   this.contents[(localContentsOffset++)] = 2;
/* 5715 */                   break;
/*      */                 case 7:
/* 5717 */                   this.contents[(localContentsOffset++)] = 4;
/* 5718 */                   i++;
/* 5719 */                   break;
/*      */                 case 8:
/* 5721 */                   this.contents[(localContentsOffset++)] = 3;
/* 5722 */                   i++;
/* 5723 */                   break;
/*      */                 case 12:
/* 5725 */                   this.contents[(localContentsOffset++)] = 5;
/* 5726 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 5728 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 5729 */                   switch (info.tag) {
/*      */                   case 8:
/* 5731 */                     int offset = info.offset;
/* 5732 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 5733 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 5734 */                     break;
/*      */                   case 7:
/* 5736 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 5737 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 5738 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/* 5741 */                 numberOfLocalEntries++;
/*      */               }
/* 5743 */               numberOfEntries++;
/*      */             }
/* 5745 */             if (localContentsOffset + 4 >= this.contents.length) {
/* 5746 */               resizeContents(4);
/*      */             }
/* 5748 */             this.contents[(numberOfLocalOffset++)] = (byte)(numberOfEntries >> 8);
/* 5749 */             this.contents[numberOfLocalOffset] = (byte)numberOfEntries;
/* 5750 */             int numberOfStackItems = currentFrame.numberOfStackItems;
/* 5751 */             this.contents[(localContentsOffset++)] = (byte)(numberOfStackItems >> 8);
/* 5752 */             this.contents[(localContentsOffset++)] = (byte)numberOfStackItems;
/* 5753 */             for (int i = 0; i < numberOfStackItems; i++) {
/* 5754 */               if (localContentsOffset + 3 >= this.contents.length) {
/* 5755 */                 resizeContents(3);
/*      */               }
/* 5757 */               VerificationTypeInfo info = currentFrame.stackItems[i];
/* 5758 */               if (info == null)
/* 5759 */                 this.contents[(localContentsOffset++)] = 0;
/*      */               else {
/* 5761 */                 switch (info.id()) {
/*      */                 case 2:
/*      */                 case 3:
/*      */                 case 4:
/*      */                 case 5:
/*      */                 case 10:
/* 5767 */                   this.contents[(localContentsOffset++)] = 1;
/* 5768 */                   break;
/*      */                 case 9:
/* 5770 */                   this.contents[(localContentsOffset++)] = 2;
/* 5771 */                   break;
/*      */                 case 7:
/* 5773 */                   this.contents[(localContentsOffset++)] = 4;
/* 5774 */                   break;
/*      */                 case 8:
/* 5776 */                   this.contents[(localContentsOffset++)] = 3;
/* 5777 */                   break;
/*      */                 case 12:
/* 5779 */                   this.contents[(localContentsOffset++)] = 5;
/* 5780 */                   break;
/*      */                 case 6:
/*      */                 case 11:
/*      */                 default:
/* 5782 */                   this.contents[(localContentsOffset++)] = (byte)info.tag;
/* 5783 */                   switch (info.tag) {
/*      */                   case 8:
/* 5785 */                     int offset = info.offset;
/* 5786 */                     this.contents[(localContentsOffset++)] = (byte)(offset >> 8);
/* 5787 */                     this.contents[(localContentsOffset++)] = (byte)offset;
/* 5788 */                     break;
/*      */                   case 7:
/* 5790 */                     int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
/* 5791 */                     this.contents[(localContentsOffset++)] = (byte)(indexForType >> 8);
/* 5792 */                     this.contents[(localContentsOffset++)] = (byte)indexForType;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 5799 */           numberOfFrames--;
/* 5800 */           if (numberOfFrames != 0) {
/* 5801 */             this.contents[(numberOfFramesOffset++)] = (byte)(numberOfFrames >> 8);
/* 5802 */             this.contents[numberOfFramesOffset] = (byte)numberOfFrames;
/*      */ 
/* 5804 */             int attributeLength = localContentsOffset - stackMapAttributeLengthOffset - 4;
/* 5805 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 5806 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 5807 */             this.contents[(stackMapAttributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 5808 */             this.contents[stackMapAttributeLengthOffset] = (byte)attributeLength;
/* 5809 */             attributeNumber++;
/*      */           } else {
/* 5811 */             localContentsOffset = stackMapTableAttributeOffset;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 5819 */     if (codeAttributeAttributeOffset + 2 >= this.contents.length) {
/* 5820 */       resizeContents(2);
/*      */     }
/* 5822 */     this.contents[(codeAttributeAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 5823 */     this.contents[codeAttributeAttributeOffset] = (byte)attributeNumber;
/*      */ 
/* 5826 */     int codeAttributeLength = localContentsOffset - (codeAttributeOffset + 6);
/* 5827 */     this.contents[(codeAttributeOffset + 2)] = (byte)(codeAttributeLength >> 24);
/* 5828 */     this.contents[(codeAttributeOffset + 3)] = (byte)(codeAttributeLength >> 16);
/* 5829 */     this.contents[(codeAttributeOffset + 4)] = (byte)(codeAttributeLength >> 8);
/* 5830 */     this.contents[(codeAttributeOffset + 5)] = (byte)codeAttributeLength;
/* 5831 */     this.contentsOffset = localContentsOffset;
/*      */   }
/*      */ 
/*      */   public void completeCodeAttributeForSyntheticMethod(SyntheticMethodBinding binding, int codeAttributeOffset, int[] startLineIndexes)
/*      */   {
/* 5852 */     completeCodeAttributeForSyntheticMethod(
/* 5853 */       false, 
/* 5854 */       binding, 
/* 5855 */       codeAttributeOffset, 
/* 5856 */       startLineIndexes);
/*      */   }
/*      */ 
/*      */   public void completeMethodInfo(int methodAttributeOffset, int attributeNumber)
/*      */   {
/* 5870 */     this.contents[(methodAttributeOffset++)] = (byte)(attributeNumber >> 8);
/* 5871 */     this.contents[methodAttributeOffset] = (byte)attributeNumber;
/*      */   }
/*      */ 
/*      */   public char[] fileName()
/*      */   {
/* 5881 */     return this.constantPool.UTF8Cache.returnKeyFor(2);
/*      */   }
/*      */ 
/*      */   private void generateAnnotation(Annotation annotation, int currentOffset) {
/* 5885 */     int startingContentsOffset = currentOffset;
/* 5886 */     if (this.contentsOffset + 4 >= this.contents.length) {
/* 5887 */       resizeContents(4);
/*      */     }
/* 5889 */     TypeBinding annotationTypeBinding = annotation.resolvedType;
/* 5890 */     if (annotationTypeBinding == null) {
/* 5891 */       this.contentsOffset = startingContentsOffset;
/* 5892 */       return;
/*      */     }
/* 5894 */     int typeIndex = this.constantPool.literalIndex(annotationTypeBinding.signature());
/* 5895 */     this.contents[(this.contentsOffset++)] = (byte)(typeIndex >> 8);
/* 5896 */     this.contents[(this.contentsOffset++)] = (byte)typeIndex;
/* 5897 */     if ((annotation instanceof NormalAnnotation)) {
/* 5898 */       NormalAnnotation normalAnnotation = (NormalAnnotation)annotation;
/* 5899 */       MemberValuePair[] memberValuePairs = normalAnnotation.memberValuePairs;
/* 5900 */       if (memberValuePairs != null) {
/* 5901 */         int memberValuePairsLength = memberValuePairs.length;
/* 5902 */         this.contents[(this.contentsOffset++)] = (byte)(memberValuePairsLength >> 8);
/* 5903 */         this.contents[(this.contentsOffset++)] = (byte)memberValuePairsLength;
/* 5904 */         for (int i = 0; i < memberValuePairsLength; i++) {
/* 5905 */           MemberValuePair memberValuePair = memberValuePairs[i];
/* 5906 */           if (this.contentsOffset + 2 >= this.contents.length) {
/* 5907 */             resizeContents(2);
/*      */           }
/* 5909 */           int elementNameIndex = this.constantPool.literalIndex(memberValuePair.name);
/* 5910 */           this.contents[(this.contentsOffset++)] = (byte)(elementNameIndex >> 8);
/* 5911 */           this.contents[(this.contentsOffset++)] = (byte)elementNameIndex;
/* 5912 */           MethodBinding methodBinding = memberValuePair.binding;
/* 5913 */           if (methodBinding == null)
/* 5914 */             this.contentsOffset = startingContentsOffset;
/*      */           else
/*      */             try {
/* 5917 */               generateElementValue(memberValuePair.value, methodBinding.returnType, startingContentsOffset);
/*      */             } catch (ClassCastException localClassCastException1) {
/* 5919 */               this.contentsOffset = startingContentsOffset;
/*      */             } catch (ShouldNotImplement localShouldNotImplement1) {
/* 5921 */               this.contentsOffset = startingContentsOffset;
/*      */             }
/*      */         }
/*      */       }
/*      */       else {
/* 5926 */         this.contents[(this.contentsOffset++)] = 0;
/* 5927 */         this.contents[(this.contentsOffset++)] = 0;
/*      */       }
/* 5929 */     } else if ((annotation instanceof SingleMemberAnnotation)) {
/* 5930 */       SingleMemberAnnotation singleMemberAnnotation = (SingleMemberAnnotation)annotation;
/*      */ 
/* 5932 */       this.contents[(this.contentsOffset++)] = 0;
/* 5933 */       this.contents[(this.contentsOffset++)] = 1;
/* 5934 */       if (this.contentsOffset + 2 >= this.contents.length) {
/* 5935 */         resizeContents(2);
/*      */       }
/* 5937 */       int elementNameIndex = this.constantPool.literalIndex(VALUE);
/* 5938 */       this.contents[(this.contentsOffset++)] = (byte)(elementNameIndex >> 8);
/* 5939 */       this.contents[(this.contentsOffset++)] = (byte)elementNameIndex;
/* 5940 */       MethodBinding methodBinding = singleMemberAnnotation.memberValuePairs()[0].binding;
/* 5941 */       if (methodBinding == null)
/* 5942 */         this.contentsOffset = startingContentsOffset;
/*      */       else
/*      */         try {
/* 5945 */           generateElementValue(singleMemberAnnotation.memberValue, methodBinding.returnType, startingContentsOffset);
/*      */         } catch (ClassCastException localClassCastException2) {
/* 5947 */           this.contentsOffset = startingContentsOffset;
/*      */         } catch (ShouldNotImplement localShouldNotImplement2) {
/* 5949 */           this.contentsOffset = startingContentsOffset;
/*      */         }
/*      */     }
/*      */     else
/*      */     {
/* 5954 */       this.contents[(this.contentsOffset++)] = 0;
/* 5955 */       this.contents[(this.contentsOffset++)] = 0;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateCodeAttributeHeader()
/*      */   {
/* 5966 */     if (this.contentsOffset + 20 >= this.contents.length) {
/* 5967 */       resizeContents(20);
/*      */     }
/* 5969 */     int constantValueNameIndex = 
/* 5970 */       this.constantPool.literalIndex(AttributeNamesConstants.CodeName);
/* 5971 */     this.contents[(this.contentsOffset++)] = (byte)(constantValueNameIndex >> 8);
/* 5972 */     this.contents[(this.contentsOffset++)] = (byte)constantValueNameIndex;
/*      */ 
/* 5974 */     this.contentsOffset += 12;
/*      */   }
/*      */ 
/*      */   private void generateElementValue(Expression defaultValue, TypeBinding memberValuePairReturnType, int attributeOffset)
/*      */   {
/* 5981 */     Constant constant = defaultValue.constant;
/* 5982 */     TypeBinding defaultValueBinding = defaultValue.resolvedType;
/* 5983 */     if (defaultValueBinding == null) {
/* 5984 */       this.contentsOffset = attributeOffset;
/*      */     } else {
/* 5986 */       if ((memberValuePairReturnType.isArrayType()) && (!defaultValueBinding.isArrayType()))
/*      */       {
/* 5988 */         if (this.contentsOffset + 3 >= this.contents.length) {
/* 5989 */           resizeContents(3);
/*      */         }
/* 5991 */         this.contents[(this.contentsOffset++)] = 91;
/* 5992 */         this.contents[(this.contentsOffset++)] = 0;
/* 5993 */         this.contents[(this.contentsOffset++)] = 1;
/*      */       }
/* 5995 */       if ((constant != null) && (constant != Constant.NotAConstant))
/* 5996 */         generateElementValue(attributeOffset, defaultValue, constant, memberValuePairReturnType.leafComponentType());
/*      */       else
/* 5998 */         generateElementValueForNonConstantExpression(defaultValue, attributeOffset, defaultValueBinding);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateElementValue(int attributeOffset, Expression defaultValue, Constant constant, TypeBinding binding)
/*      */   {
/* 6007 */     if (this.contentsOffset + 3 >= this.contents.length) {
/* 6008 */       resizeContents(3);
/*      */     }
/* 6010 */     switch (binding.id) {
/*      */     case 5:
/* 6012 */       this.contents[(this.contentsOffset++)] = 90;
/* 6013 */       int booleanValueIndex = 
/* 6014 */         this.constantPool.literalIndex(constant.booleanValue() ? 1 : 0);
/* 6015 */       this.contents[(this.contentsOffset++)] = (byte)(booleanValueIndex >> 8);
/* 6016 */       this.contents[(this.contentsOffset++)] = (byte)booleanValueIndex;
/* 6017 */       break;
/*      */     case 3:
/* 6019 */       this.contents[(this.contentsOffset++)] = 66;
/* 6020 */       int integerValueIndex = 
/* 6021 */         this.constantPool.literalIndex(constant.intValue());
/* 6022 */       this.contents[(this.contentsOffset++)] = (byte)(integerValueIndex >> 8);
/* 6023 */       this.contents[(this.contentsOffset++)] = (byte)integerValueIndex;
/* 6024 */       break;
/*      */     case 2:
/* 6026 */       this.contents[(this.contentsOffset++)] = 67;
/* 6027 */       int integerValueIndex = 
/* 6028 */         this.constantPool.literalIndex(constant.intValue());
/* 6029 */       this.contents[(this.contentsOffset++)] = (byte)(integerValueIndex >> 8);
/* 6030 */       this.contents[(this.contentsOffset++)] = (byte)integerValueIndex;
/* 6031 */       break;
/*      */     case 10:
/* 6033 */       this.contents[(this.contentsOffset++)] = 73;
/* 6034 */       int integerValueIndex = 
/* 6035 */         this.constantPool.literalIndex(constant.intValue());
/* 6036 */       this.contents[(this.contentsOffset++)] = (byte)(integerValueIndex >> 8);
/* 6037 */       this.contents[(this.contentsOffset++)] = (byte)integerValueIndex;
/* 6038 */       break;
/*      */     case 4:
/* 6040 */       this.contents[(this.contentsOffset++)] = 83;
/* 6041 */       int integerValueIndex = 
/* 6042 */         this.constantPool.literalIndex(constant.intValue());
/* 6043 */       this.contents[(this.contentsOffset++)] = (byte)(integerValueIndex >> 8);
/* 6044 */       this.contents[(this.contentsOffset++)] = (byte)integerValueIndex;
/* 6045 */       break;
/*      */     case 9:
/* 6047 */       this.contents[(this.contentsOffset++)] = 70;
/* 6048 */       int floatValueIndex = 
/* 6049 */         this.constantPool.literalIndex(constant.floatValue());
/* 6050 */       this.contents[(this.contentsOffset++)] = (byte)(floatValueIndex >> 8);
/* 6051 */       this.contents[(this.contentsOffset++)] = (byte)floatValueIndex;
/* 6052 */       break;
/*      */     case 8:
/* 6054 */       this.contents[(this.contentsOffset++)] = 68;
/* 6055 */       int doubleValueIndex = 
/* 6056 */         this.constantPool.literalIndex(constant.doubleValue());
/* 6057 */       this.contents[(this.contentsOffset++)] = (byte)(doubleValueIndex >> 8);
/* 6058 */       this.contents[(this.contentsOffset++)] = (byte)doubleValueIndex;
/* 6059 */       break;
/*      */     case 7:
/* 6061 */       this.contents[(this.contentsOffset++)] = 74;
/* 6062 */       int longValueIndex = 
/* 6063 */         this.constantPool.literalIndex(constant.longValue());
/* 6064 */       this.contents[(this.contentsOffset++)] = (byte)(longValueIndex >> 8);
/* 6065 */       this.contents[(this.contentsOffset++)] = (byte)longValueIndex;
/* 6066 */       break;
/*      */     case 11:
/* 6068 */       this.contents[(this.contentsOffset++)] = 115;
/* 6069 */       int stringValueIndex = 
/* 6070 */         this.constantPool.literalIndex(((StringConstant)constant).stringValue().toCharArray());
/* 6071 */       if (stringValueIndex == -1) {
/* 6072 */         if (!this.creatingProblemType)
/*      */         {
/* 6074 */           TypeDeclaration typeDeclaration = this.referenceBinding.scope.referenceContext;
/* 6075 */           typeDeclaration.scope.problemReporter().stringConstantIsExceedingUtf8Limit(defaultValue);
/*      */         }
/*      */         else {
/* 6078 */           this.contentsOffset = attributeOffset;
/*      */         }
/*      */       } else {
/* 6081 */         this.contents[(this.contentsOffset++)] = (byte)(stringValueIndex >> 8);
/* 6082 */         this.contents[(this.contentsOffset++)] = (byte)stringValueIndex;
/*      */       }case 6:
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateElementValueForNonConstantExpression(Expression defaultValue, int attributeOffset, TypeBinding defaultValueBinding) {
/* 6088 */     if (defaultValueBinding != null) {
/* 6089 */       if (defaultValueBinding.isEnum()) {
/* 6090 */         if (this.contentsOffset + 5 >= this.contents.length) {
/* 6091 */           resizeContents(5);
/*      */         }
/* 6093 */         this.contents[(this.contentsOffset++)] = 101;
/* 6094 */         FieldBinding fieldBinding = null;
/* 6095 */         if ((defaultValue instanceof QualifiedNameReference)) {
/* 6096 */           QualifiedNameReference nameReference = (QualifiedNameReference)defaultValue;
/* 6097 */           fieldBinding = (FieldBinding)nameReference.binding;
/* 6098 */         } else if ((defaultValue instanceof SingleNameReference)) {
/* 6099 */           SingleNameReference nameReference = (SingleNameReference)defaultValue;
/* 6100 */           fieldBinding = (FieldBinding)nameReference.binding;
/*      */         } else {
/* 6102 */           this.contentsOffset = attributeOffset;
/*      */         }
/* 6104 */         if (fieldBinding != null) {
/* 6105 */           int enumConstantTypeNameIndex = this.constantPool.literalIndex(fieldBinding.type.signature());
/* 6106 */           int enumConstantNameIndex = this.constantPool.literalIndex(fieldBinding.name);
/* 6107 */           this.contents[(this.contentsOffset++)] = (byte)(enumConstantTypeNameIndex >> 8);
/* 6108 */           this.contents[(this.contentsOffset++)] = (byte)enumConstantTypeNameIndex;
/* 6109 */           this.contents[(this.contentsOffset++)] = (byte)(enumConstantNameIndex >> 8);
/* 6110 */           this.contents[(this.contentsOffset++)] = (byte)enumConstantNameIndex;
/*      */         }
/* 6112 */       } else if (defaultValueBinding.isAnnotationType()) {
/* 6113 */         if (this.contentsOffset + 1 >= this.contents.length) {
/* 6114 */           resizeContents(1);
/*      */         }
/* 6116 */         this.contents[(this.contentsOffset++)] = 64;
/* 6117 */         generateAnnotation((Annotation)defaultValue, attributeOffset);
/* 6118 */       } else if (defaultValueBinding.isArrayType())
/*      */       {
/* 6120 */         if (this.contentsOffset + 3 >= this.contents.length) {
/* 6121 */           resizeContents(3);
/*      */         }
/* 6123 */         this.contents[(this.contentsOffset++)] = 91;
/* 6124 */         if ((defaultValue instanceof ArrayInitializer)) {
/* 6125 */           ArrayInitializer arrayInitializer = (ArrayInitializer)defaultValue;
/* 6126 */           int arrayLength = arrayInitializer.expressions != null ? arrayInitializer.expressions.length : 0;
/* 6127 */           this.contents[(this.contentsOffset++)] = (byte)(arrayLength >> 8);
/* 6128 */           this.contents[(this.contentsOffset++)] = (byte)arrayLength;
/* 6129 */           for (int i = 0; i < arrayLength; i++)
/* 6130 */             generateElementValue(arrayInitializer.expressions[i], defaultValueBinding.leafComponentType(), attributeOffset);
/*      */         }
/*      */         else {
/* 6133 */           this.contentsOffset = attributeOffset;
/*      */         }
/*      */       }
/*      */       else {
/* 6137 */         if (this.contentsOffset + 3 >= this.contents.length) {
/* 6138 */           resizeContents(3);
/*      */         }
/* 6140 */         this.contents[(this.contentsOffset++)] = 99;
/* 6141 */         if ((defaultValue instanceof ClassLiteralAccess)) {
/* 6142 */           ClassLiteralAccess classLiteralAccess = (ClassLiteralAccess)defaultValue;
/* 6143 */           int classInfoIndex = this.constantPool.literalIndex(classLiteralAccess.targetType.signature());
/* 6144 */           this.contents[(this.contentsOffset++)] = (byte)(classInfoIndex >> 8);
/* 6145 */           this.contents[(this.contentsOffset++)] = (byte)classInfoIndex;
/*      */         } else {
/* 6147 */           this.contentsOffset = attributeOffset;
/*      */         }
/*      */       }
/*      */     }
/* 6151 */     else this.contentsOffset = attributeOffset;
/*      */   }
/*      */ 
/*      */   public int generateMethodInfoAttribute(MethodBinding methodBinding)
/*      */   {
/* 6170 */     this.contentsOffset += 2;
/* 6171 */     if (this.contentsOffset + 2 >= this.contents.length) {
/* 6172 */       resizeContents(2);
/*      */     }
/*      */ 
/* 6183 */     int attributeNumber = 0;
/*      */     ReferenceBinding[] thrownsExceptions;
/* 6184 */     if ((thrownsExceptions = methodBinding.thrownExceptions) != Binding.NO_EXCEPTIONS)
/*      */     {
/* 6187 */       int length = thrownsExceptions.length;
/* 6188 */       int exSize = 8 + length * 2;
/* 6189 */       if (exSize + this.contentsOffset >= this.contents.length) {
/* 6190 */         resizeContents(exSize);
/*      */       }
/* 6192 */       int exceptionNameIndex = 
/* 6193 */         this.constantPool.literalIndex(AttributeNamesConstants.ExceptionsName);
/* 6194 */       this.contents[(this.contentsOffset++)] = (byte)(exceptionNameIndex >> 8);
/* 6195 */       this.contents[(this.contentsOffset++)] = (byte)exceptionNameIndex;
/*      */ 
/* 6197 */       int attributeLength = length * 2 + 2;
/* 6198 */       this.contents[(this.contentsOffset++)] = (byte)(attributeLength >> 24);
/* 6199 */       this.contents[(this.contentsOffset++)] = (byte)(attributeLength >> 16);
/* 6200 */       this.contents[(this.contentsOffset++)] = (byte)(attributeLength >> 8);
/* 6201 */       this.contents[(this.contentsOffset++)] = (byte)attributeLength;
/* 6202 */       this.contents[(this.contentsOffset++)] = (byte)(length >> 8);
/* 6203 */       this.contents[(this.contentsOffset++)] = (byte)length;
/* 6204 */       for (int i = 0; i < length; i++) {
/* 6205 */         int exceptionIndex = this.constantPool.literalIndexForType(thrownsExceptions[i]);
/* 6206 */         this.contents[(this.contentsOffset++)] = (byte)(exceptionIndex >> 8);
/* 6207 */         this.contents[(this.contentsOffset++)] = (byte)exceptionIndex;
/*      */       }
/* 6209 */       attributeNumber++;
/*      */     }
/* 6211 */     if (methodBinding.isDeprecated())
/*      */     {
/* 6214 */       if (this.contentsOffset + 6 >= this.contents.length) {
/* 6215 */         resizeContents(6);
/*      */       }
/* 6217 */       int deprecatedAttributeNameIndex = 
/* 6218 */         this.constantPool.literalIndex(AttributeNamesConstants.DeprecatedName);
/* 6219 */       this.contents[(this.contentsOffset++)] = (byte)(deprecatedAttributeNameIndex >> 8);
/* 6220 */       this.contents[(this.contentsOffset++)] = (byte)deprecatedAttributeNameIndex;
/*      */ 
/* 6222 */       this.contents[(this.contentsOffset++)] = 0;
/* 6223 */       this.contents[(this.contentsOffset++)] = 0;
/* 6224 */       this.contents[(this.contentsOffset++)] = 0;
/* 6225 */       this.contents[(this.contentsOffset++)] = 0;
/*      */ 
/* 6227 */       attributeNumber++;
/*      */     }
/* 6229 */     if (this.targetJDK < 3211264L) {
/* 6230 */       if (methodBinding.isSynthetic())
/*      */       {
/* 6233 */         if (this.contentsOffset + 6 >= this.contents.length) {
/* 6234 */           resizeContents(6);
/*      */         }
/* 6236 */         int syntheticAttributeNameIndex = 
/* 6237 */           this.constantPool.literalIndex(AttributeNamesConstants.SyntheticName);
/* 6238 */         this.contents[(this.contentsOffset++)] = (byte)(syntheticAttributeNameIndex >> 8);
/* 6239 */         this.contents[(this.contentsOffset++)] = (byte)syntheticAttributeNameIndex;
/*      */ 
/* 6241 */         this.contents[(this.contentsOffset++)] = 0;
/* 6242 */         this.contents[(this.contentsOffset++)] = 0;
/* 6243 */         this.contents[(this.contentsOffset++)] = 0;
/* 6244 */         this.contents[(this.contentsOffset++)] = 0;
/*      */ 
/* 6246 */         attributeNumber++;
/*      */       }
/* 6248 */       if (methodBinding.isVarargs())
/*      */       {
/* 6254 */         if (this.contentsOffset + 6 >= this.contents.length) {
/* 6255 */           resizeContents(6);
/*      */         }
/* 6257 */         int varargsAttributeNameIndex = 
/* 6258 */           this.constantPool.literalIndex(AttributeNamesConstants.VarargsName);
/* 6259 */         this.contents[(this.contentsOffset++)] = (byte)(varargsAttributeNameIndex >> 8);
/* 6260 */         this.contents[(this.contentsOffset++)] = (byte)varargsAttributeNameIndex;
/*      */ 
/* 6262 */         this.contents[(this.contentsOffset++)] = 0;
/* 6263 */         this.contents[(this.contentsOffset++)] = 0;
/* 6264 */         this.contents[(this.contentsOffset++)] = 0;
/* 6265 */         this.contents[(this.contentsOffset++)] = 0;
/*      */ 
/* 6267 */         attributeNumber++;
/*      */       }
/*      */     }
/*      */ 
/* 6271 */     char[] genericSignature = methodBinding.genericSignature();
/* 6272 */     if (genericSignature != null)
/*      */     {
/* 6275 */       if (this.contentsOffset + 8 >= this.contents.length) {
/* 6276 */         resizeContents(8);
/*      */       }
/* 6278 */       int signatureAttributeNameIndex = 
/* 6279 */         this.constantPool.literalIndex(AttributeNamesConstants.SignatureName);
/* 6280 */       this.contents[(this.contentsOffset++)] = (byte)(signatureAttributeNameIndex >> 8);
/* 6281 */       this.contents[(this.contentsOffset++)] = (byte)signatureAttributeNameIndex;
/*      */ 
/* 6283 */       this.contents[(this.contentsOffset++)] = 0;
/* 6284 */       this.contents[(this.contentsOffset++)] = 0;
/* 6285 */       this.contents[(this.contentsOffset++)] = 0;
/* 6286 */       this.contents[(this.contentsOffset++)] = 2;
/* 6287 */       int signatureIndex = 
/* 6288 */         this.constantPool.literalIndex(genericSignature);
/* 6289 */       this.contents[(this.contentsOffset++)] = (byte)(signatureIndex >> 8);
/* 6290 */       this.contents[(this.contentsOffset++)] = (byte)signatureIndex;
/* 6291 */       attributeNumber++;
/*      */     }
/* 6293 */     if (this.targetJDK >= 3211264L) {
/* 6294 */       AbstractMethodDeclaration methodDeclaration = methodBinding.sourceMethod();
/* 6295 */       if (methodDeclaration != null) {
/* 6296 */         Annotation[] annotations = methodDeclaration.annotations;
/* 6297 */         if (annotations != null) {
/* 6298 */           attributeNumber += generateRuntimeAnnotations(annotations);
/*      */         }
/* 6300 */         if ((methodBinding.tagBits & 0x400) != 0L) {
/* 6301 */           Argument[] arguments = methodDeclaration.arguments;
/* 6302 */           if (arguments != null) {
/* 6303 */             attributeNumber += generateRuntimeAnnotationsForParameters(arguments);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 6308 */     if ((methodBinding.tagBits & 0x80) != 0L) {
/* 6309 */       this.missingTypes = methodBinding.collectMissingTypes(this.missingTypes);
/*      */     }
/* 6311 */     return attributeNumber;
/*      */   }
/*      */ 
/*      */   public int generateMethodInfoAttribute(MethodBinding methodBinding, AnnotationMethodDeclaration declaration) {
/* 6315 */     int attributesNumber = generateMethodInfoAttribute(methodBinding);
/* 6316 */     int attributeOffset = this.contentsOffset;
/* 6317 */     if ((declaration.modifiers & 0x20000) != 0)
/*      */     {
/* 6319 */       int annotationDefaultNameIndex = 
/* 6320 */         this.constantPool.literalIndex(AttributeNamesConstants.AnnotationDefaultName);
/* 6321 */       this.contents[(this.contentsOffset++)] = (byte)(annotationDefaultNameIndex >> 8);
/* 6322 */       this.contents[(this.contentsOffset++)] = (byte)annotationDefaultNameIndex;
/* 6323 */       int attributeLengthOffset = this.contentsOffset;
/* 6324 */       this.contentsOffset += 4;
/* 6325 */       if (this.contentsOffset + 4 >= this.contents.length) {
/* 6326 */         resizeContents(4);
/*      */       }
/* 6328 */       generateElementValue(declaration.defaultValue, declaration.binding.returnType, attributeOffset);
/* 6329 */       if (this.contentsOffset != attributeOffset) {
/* 6330 */         int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
/* 6331 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 6332 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 6333 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 6334 */         this.contents[(attributeLengthOffset++)] = (byte)attributeLength;
/* 6335 */         attributesNumber++;
/*      */       }
/*      */     }
/* 6338 */     return attributesNumber;
/*      */   }
/*      */ 
/*      */   public void generateMethodInfoHeader(MethodBinding methodBinding)
/*      */   {
/* 6351 */     generateMethodInfoHeader(methodBinding, methodBinding.modifiers);
/*      */   }
/*      */ 
/*      */   public void generateMethodInfoHeader(MethodBinding methodBinding, int accessFlags)
/*      */   {
/* 6367 */     this.methodCount += 1;
/* 6368 */     if (this.contentsOffset + 10 >= this.contents.length) {
/* 6369 */       resizeContents(10);
/*      */     }
/* 6371 */     if (this.targetJDK < 3211264L)
/*      */     {
/* 6374 */       accessFlags &= -4225;
/*      */     }
/* 6376 */     if ((methodBinding.tagBits & 0x400) != 0L) {
/* 6377 */       accessFlags &= -3;
/*      */     }
/* 6379 */     this.contents[(this.contentsOffset++)] = (byte)(accessFlags >> 8);
/* 6380 */     this.contents[(this.contentsOffset++)] = (byte)accessFlags;
/* 6381 */     int nameIndex = this.constantPool.literalIndex(methodBinding.selector);
/* 6382 */     this.contents[(this.contentsOffset++)] = (byte)(nameIndex >> 8);
/* 6383 */     this.contents[(this.contentsOffset++)] = (byte)nameIndex;
/* 6384 */     int descriptorIndex = this.constantPool.literalIndex(methodBinding.signature(this));
/* 6385 */     this.contents[(this.contentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 6386 */     this.contents[(this.contentsOffset++)] = (byte)descriptorIndex;
/*      */   }
/*      */ 
/*      */   public void generateMethodInfoHeaderForClinit()
/*      */   {
/* 6400 */     this.methodCount += 1;
/* 6401 */     if (this.contentsOffset + 10 >= this.contents.length) {
/* 6402 */       resizeContents(10);
/*      */     }
/* 6404 */     this.contents[(this.contentsOffset++)] = 0;
/* 6405 */     this.contents[(this.contentsOffset++)] = 8;
/* 6406 */     int nameIndex = this.constantPool.literalIndex(ConstantPool.Clinit);
/* 6407 */     this.contents[(this.contentsOffset++)] = (byte)(nameIndex >> 8);
/* 6408 */     this.contents[(this.contentsOffset++)] = (byte)nameIndex;
/* 6409 */     int descriptorIndex = 
/* 6410 */       this.constantPool.literalIndex(ConstantPool.ClinitSignature);
/* 6411 */     this.contents[(this.contentsOffset++)] = (byte)(descriptorIndex >> 8);
/* 6412 */     this.contents[(this.contentsOffset++)] = (byte)descriptorIndex;
/*      */ 
/* 6414 */     this.contents[(this.contentsOffset++)] = 0;
/* 6415 */     this.contents[(this.contentsOffset++)] = 1;
/*      */   }
/*      */ 
/*      */   public void generateMissingAbstractMethods(MethodDeclaration[] methodDeclarations, CompilationResult compilationResult)
/*      */   {
/* 6426 */     if (methodDeclarations != null) {
/* 6427 */       TypeDeclaration currentDeclaration = this.referenceBinding.scope.referenceContext;
/* 6428 */       int typeDeclarationSourceStart = currentDeclaration.sourceStart();
/* 6429 */       int typeDeclarationSourceEnd = currentDeclaration.sourceEnd();
/* 6430 */       int i = 0; for (int max = methodDeclarations.length; i < max; i++) {
/* 6431 */         MethodDeclaration methodDeclaration = methodDeclarations[i];
/* 6432 */         MethodBinding methodBinding = methodDeclaration.binding;
/* 6433 */         String readableName = new String(methodBinding.readableName());
/* 6434 */         CategorizedProblem[] problems = compilationResult.problems;
/* 6435 */         int problemsCount = compilationResult.problemCount;
/* 6436 */         for (int j = 0; j < problemsCount; j++) {
/* 6437 */           CategorizedProblem problem = problems[j];
/* 6438 */           if ((problem == null) || 
/* 6439 */             (problem.getID() != 67109264) || 
/* 6440 */             (problem.getMessage().indexOf(readableName) == -1) || 
/* 6441 */             (problem.getSourceStart() < typeDeclarationSourceStart) || 
/* 6442 */             (problem.getSourceEnd() > typeDeclarationSourceEnd))
/*      */             continue;
/* 6444 */           addMissingAbstractProblemMethod(methodDeclaration, methodBinding, problem, compilationResult);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void generateMissingTypesAttribute()
/*      */   {
/* 6452 */     int initialSize = this.missingTypes.size();
/* 6453 */     int[] missingTypesIndexes = new int[initialSize];
/* 6454 */     int numberOfMissingTypes = 0;
/* 6455 */     if (initialSize > 1)
/* 6456 */       Collections.sort(this.missingTypes, new Comparator() {
/*      */         public int compare(Object o1, Object o2) {
/* 6458 */           TypeBinding typeBinding1 = (TypeBinding)o1;
/* 6459 */           TypeBinding typeBinding2 = (TypeBinding)o2;
/* 6460 */           return CharOperation.compareTo(typeBinding1.constantPoolName(), typeBinding2.constantPoolName());
/*      */         }
/*      */       });
/* 6464 */     int previousIndex = 0;
/* 6465 */     for (int i = 0; i < initialSize; i++) {
/* 6466 */       int missingTypeIndex = this.constantPool.literalIndexForType((TypeBinding)this.missingTypes.get(i));
/* 6467 */       if (previousIndex == missingTypeIndex) {
/*      */         continue;
/*      */       }
/* 6470 */       previousIndex = missingTypeIndex;
/* 6471 */       missingTypesIndexes[(numberOfMissingTypes++)] = missingTypeIndex;
/*      */     }
/*      */ 
/* 6474 */     int attributeLength = numberOfMissingTypes * 2 + 2;
/* 6475 */     if (this.contentsOffset + attributeLength + 6 >= this.contents.length) {
/* 6476 */       resizeContents(attributeLength + 6);
/*      */     }
/* 6478 */     int missingTypesNameIndex = this.constantPool.literalIndex(AttributeNamesConstants.MissingTypesName);
/* 6479 */     this.contents[(this.contentsOffset++)] = (byte)(missingTypesNameIndex >> 8);
/* 6480 */     this.contents[(this.contentsOffset++)] = (byte)missingTypesNameIndex;
/*      */ 
/* 6483 */     this.contents[(this.contentsOffset++)] = (byte)(attributeLength >> 24);
/* 6484 */     this.contents[(this.contentsOffset++)] = (byte)(attributeLength >> 16);
/* 6485 */     this.contents[(this.contentsOffset++)] = (byte)(attributeLength >> 8);
/* 6486 */     this.contents[(this.contentsOffset++)] = (byte)attributeLength;
/*      */ 
/* 6489 */     this.contents[(this.contentsOffset++)] = (byte)(numberOfMissingTypes >> 8);
/* 6490 */     this.contents[(this.contentsOffset++)] = (byte)numberOfMissingTypes;
/*      */ 
/* 6492 */     for (int i = 0; i < numberOfMissingTypes; i++) {
/* 6493 */       int missingTypeIndex = missingTypesIndexes[i];
/* 6494 */       this.contents[(this.contentsOffset++)] = (byte)(missingTypeIndex >> 8);
/* 6495 */       this.contents[(this.contentsOffset++)] = (byte)missingTypeIndex;
/*      */     }
/*      */   }
/*      */ 
/*      */   private int generateRuntimeAnnotations(Annotation[] annotations)
/*      */   {
/* 6504 */     int attributesNumber = 0;
/* 6505 */     int length = annotations.length;
/* 6506 */     int visibleAnnotationsCounter = 0;
/* 6507 */     int invisibleAnnotationsCounter = 0;
/*      */ 
/* 6509 */     for (int i = 0; i < length; i++) {
/* 6510 */       Annotation annotation = annotations[i];
/* 6511 */       if (isRuntimeInvisible(annotation))
/* 6512 */         invisibleAnnotationsCounter++;
/* 6513 */       else if (isRuntimeVisible(annotation)) {
/* 6514 */         visibleAnnotationsCounter++;
/*      */       }
/*      */     }
/*      */ 
/* 6518 */     int annotationAttributeOffset = this.contentsOffset;
/* 6519 */     int constantPOffset = this.constantPool.currentOffset;
/* 6520 */     int constantPoolIndex = this.constantPool.currentIndex;
/* 6521 */     if (invisibleAnnotationsCounter != 0) {
/* 6522 */       if (this.contentsOffset + 10 >= this.contents.length) {
/* 6523 */         resizeContents(10);
/*      */       }
/* 6525 */       int runtimeInvisibleAnnotationsAttributeNameIndex = 
/* 6526 */         this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleAnnotationsName);
/* 6527 */       this.contents[(this.contentsOffset++)] = (byte)(runtimeInvisibleAnnotationsAttributeNameIndex >> 8);
/* 6528 */       this.contents[(this.contentsOffset++)] = (byte)runtimeInvisibleAnnotationsAttributeNameIndex;
/* 6529 */       int attributeLengthOffset = this.contentsOffset;
/* 6530 */       this.contentsOffset += 4;
/*      */ 
/* 6532 */       int annotationsLengthOffset = this.contentsOffset;
/* 6533 */       this.contentsOffset += 2;
/*      */ 
/* 6535 */       int counter = 0;
/* 6536 */       for (int i = 0; i < length; i++) {
/* 6537 */         if (invisibleAnnotationsCounter == 0) break;
/* 6538 */         Annotation annotation = annotations[i];
/* 6539 */         if (isRuntimeInvisible(annotation)) {
/* 6540 */           int currentAnnotationOffset = this.contentsOffset;
/* 6541 */           generateAnnotation(annotation, currentAnnotationOffset);
/* 6542 */           invisibleAnnotationsCounter--;
/* 6543 */           if (this.contentsOffset != currentAnnotationOffset) {
/* 6544 */             counter++;
/*      */           }
/*      */         }
/*      */       }
/* 6548 */       if (counter != 0) {
/* 6549 */         this.contents[(annotationsLengthOffset++)] = (byte)(counter >> 8);
/* 6550 */         this.contents[(annotationsLengthOffset++)] = (byte)counter;
/*      */ 
/* 6552 */         int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
/* 6553 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 6554 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 6555 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 6556 */         this.contents[(attributeLengthOffset++)] = (byte)attributeLength;
/* 6557 */         attributesNumber++;
/*      */       } else {
/* 6559 */         this.contentsOffset = annotationAttributeOffset;
/*      */ 
/* 6561 */         this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeInvisibleAnnotationsName, constantPoolIndex, constantPOffset);
/*      */       }
/*      */     }
/*      */ 
/* 6565 */     annotationAttributeOffset = this.contentsOffset;
/* 6566 */     constantPOffset = this.constantPool.currentOffset;
/* 6567 */     constantPoolIndex = this.constantPool.currentIndex;
/* 6568 */     if (visibleAnnotationsCounter != 0) {
/* 6569 */       if (this.contentsOffset + 10 >= this.contents.length) {
/* 6570 */         resizeContents(10);
/*      */       }
/* 6572 */       int runtimeVisibleAnnotationsAttributeNameIndex = 
/* 6573 */         this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleAnnotationsName);
/* 6574 */       this.contents[(this.contentsOffset++)] = (byte)(runtimeVisibleAnnotationsAttributeNameIndex >> 8);
/* 6575 */       this.contents[(this.contentsOffset++)] = (byte)runtimeVisibleAnnotationsAttributeNameIndex;
/* 6576 */       int attributeLengthOffset = this.contentsOffset;
/* 6577 */       this.contentsOffset += 4;
/*      */ 
/* 6579 */       int annotationsLengthOffset = this.contentsOffset;
/* 6580 */       this.contentsOffset += 2;
/*      */ 
/* 6582 */       int counter = 0;
/* 6583 */       for (int i = 0; i < length; i++) {
/* 6584 */         if (visibleAnnotationsCounter == 0) break;
/* 6585 */         Annotation annotation = annotations[i];
/* 6586 */         if (isRuntimeVisible(annotation)) {
/* 6587 */           visibleAnnotationsCounter--;
/* 6588 */           int currentAnnotationOffset = this.contentsOffset;
/* 6589 */           generateAnnotation(annotation, currentAnnotationOffset);
/* 6590 */           if (this.contentsOffset != currentAnnotationOffset) {
/* 6591 */             counter++;
/*      */           }
/*      */         }
/*      */       }
/* 6595 */       if (counter != 0) {
/* 6596 */         this.contents[(annotationsLengthOffset++)] = (byte)(counter >> 8);
/* 6597 */         this.contents[(annotationsLengthOffset++)] = (byte)counter;
/*      */ 
/* 6599 */         int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
/* 6600 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 6601 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 6602 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 6603 */         this.contents[(attributeLengthOffset++)] = (byte)attributeLength;
/* 6604 */         attributesNumber++;
/*      */       } else {
/* 6606 */         this.contentsOffset = annotationAttributeOffset;
/* 6607 */         this.constantPool.resetForAttributeName(AttributeNamesConstants.RuntimeVisibleAnnotationsName, constantPoolIndex, constantPOffset);
/*      */       }
/*      */     }
/* 6610 */     return attributesNumber;
/*      */   }
/*      */ 
/*      */   private int generateRuntimeAnnotationsForParameters(Argument[] arguments) {
/* 6614 */     int argumentsLength = arguments.length;
/*      */ 
/* 6617 */     int invisibleParametersAnnotationsCounter = 0;
/* 6618 */     int visibleParametersAnnotationsCounter = 0;
/* 6619 */     int[][] annotationsCounters = new int[argumentsLength][2];
/* 6620 */     for (int i = 0; i < argumentsLength; i++) {
/* 6621 */       Argument argument = arguments[i];
/* 6622 */       Annotation[] annotations = argument.annotations;
/* 6623 */       if (annotations != null) {
/* 6624 */         int j = 0; for (int max2 = annotations.length; j < max2; j++) {
/* 6625 */           Annotation annotation = annotations[j];
/* 6626 */           if (isRuntimeInvisible(annotation)) {
/* 6627 */             annotationsCounters[i][1] += 1;
/* 6628 */             invisibleParametersAnnotationsCounter++;
/* 6629 */           } else if (isRuntimeVisible(annotation)) {
/* 6630 */             annotationsCounters[i][0] += 1;
/* 6631 */             visibleParametersAnnotationsCounter++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 6636 */     int attributesNumber = 0;
/* 6637 */     int annotationAttributeOffset = this.contentsOffset;
/* 6638 */     if (invisibleParametersAnnotationsCounter != 0) {
/* 6639 */       int globalCounter = 0;
/* 6640 */       if (this.contentsOffset + 7 >= this.contents.length) {
/* 6641 */         resizeContents(7);
/*      */       }
/* 6643 */       int attributeNameIndex = 
/* 6644 */         this.constantPool.literalIndex(AttributeNamesConstants.RuntimeInvisibleParameterAnnotationsName);
/* 6645 */       this.contents[(this.contentsOffset++)] = (byte)(attributeNameIndex >> 8);
/* 6646 */       this.contents[(this.contentsOffset++)] = (byte)attributeNameIndex;
/* 6647 */       int attributeLengthOffset = this.contentsOffset;
/* 6648 */       this.contentsOffset += 4;
/*      */ 
/* 6650 */       this.contents[(this.contentsOffset++)] = (byte)argumentsLength;
/* 6651 */       for (int i = 0; i < argumentsLength; i++) {
/* 6652 */         if (this.contentsOffset + 2 >= this.contents.length) {
/* 6653 */           resizeContents(2);
/*      */         }
/* 6655 */         if (invisibleParametersAnnotationsCounter == 0) {
/* 6656 */           this.contents[(this.contentsOffset++)] = 0;
/* 6657 */           this.contents[(this.contentsOffset++)] = 0;
/*      */         } else {
/* 6659 */           int numberOfInvisibleAnnotations = annotationsCounters[i][1];
/* 6660 */           int invisibleAnnotationsOffset = this.contentsOffset;
/*      */ 
/* 6662 */           this.contentsOffset += 2;
/* 6663 */           int counter = 0;
/* 6664 */           if (numberOfInvisibleAnnotations != 0) {
/* 6665 */             Argument argument = arguments[i];
/* 6666 */             Annotation[] annotations = argument.annotations;
/* 6667 */             int j = 0; for (int max = annotations.length; j < max; j++) {
/* 6668 */               Annotation annotation = annotations[j];
/* 6669 */               if (isRuntimeInvisible(annotation)) {
/* 6670 */                 int currentAnnotationOffset = this.contentsOffset;
/* 6671 */                 generateAnnotation(annotation, currentAnnotationOffset);
/* 6672 */                 if (this.contentsOffset != currentAnnotationOffset) {
/* 6673 */                   counter++;
/* 6674 */                   globalCounter++;
/*      */                 }
/* 6676 */                 invisibleParametersAnnotationsCounter--;
/*      */               }
/*      */             }
/*      */           }
/* 6680 */           this.contents[(invisibleAnnotationsOffset++)] = (byte)(counter >> 8);
/* 6681 */           this.contents[invisibleAnnotationsOffset] = (byte)counter;
/*      */         }
/*      */       }
/* 6684 */       if (globalCounter != 0) {
/* 6685 */         int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
/* 6686 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 6687 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 6688 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 6689 */         this.contents[(attributeLengthOffset++)] = (byte)attributeLength;
/* 6690 */         attributesNumber++;
/*      */       }
/*      */       else {
/* 6693 */         this.contentsOffset = annotationAttributeOffset;
/*      */       }
/*      */     }
/* 6696 */     if (visibleParametersAnnotationsCounter != 0) {
/* 6697 */       int globalCounter = 0;
/* 6698 */       if (this.contentsOffset + 7 >= this.contents.length) {
/* 6699 */         resizeContents(7);
/*      */       }
/* 6701 */       int attributeNameIndex = 
/* 6702 */         this.constantPool.literalIndex(AttributeNamesConstants.RuntimeVisibleParameterAnnotationsName);
/* 6703 */       this.contents[(this.contentsOffset++)] = (byte)(attributeNameIndex >> 8);
/* 6704 */       this.contents[(this.contentsOffset++)] = (byte)attributeNameIndex;
/* 6705 */       int attributeLengthOffset = this.contentsOffset;
/* 6706 */       this.contentsOffset += 4;
/*      */ 
/* 6708 */       this.contents[(this.contentsOffset++)] = (byte)argumentsLength;
/* 6709 */       for (int i = 0; i < argumentsLength; i++) {
/* 6710 */         if (this.contentsOffset + 2 >= this.contents.length) {
/* 6711 */           resizeContents(2);
/*      */         }
/* 6713 */         if (visibleParametersAnnotationsCounter == 0) {
/* 6714 */           this.contents[(this.contentsOffset++)] = 0;
/* 6715 */           this.contents[(this.contentsOffset++)] = 0;
/*      */         } else {
/* 6717 */           int numberOfVisibleAnnotations = annotationsCounters[i][0];
/* 6718 */           int visibleAnnotationsOffset = this.contentsOffset;
/*      */ 
/* 6720 */           this.contentsOffset += 2;
/* 6721 */           int counter = 0;
/* 6722 */           if (numberOfVisibleAnnotations != 0) {
/* 6723 */             Argument argument = arguments[i];
/* 6724 */             Annotation[] annotations = argument.annotations;
/* 6725 */             int j = 0; for (int max = annotations.length; j < max; j++) {
/* 6726 */               Annotation annotation = annotations[j];
/* 6727 */               if (isRuntimeVisible(annotation)) {
/* 6728 */                 int currentAnnotationOffset = this.contentsOffset;
/* 6729 */                 generateAnnotation(annotation, currentAnnotationOffset);
/* 6730 */                 if (this.contentsOffset != currentAnnotationOffset) {
/* 6731 */                   counter++;
/* 6732 */                   globalCounter++;
/*      */                 }
/* 6734 */                 visibleParametersAnnotationsCounter--;
/*      */               }
/*      */             }
/*      */           }
/* 6738 */           this.contents[(visibleAnnotationsOffset++)] = (byte)(counter >> 8);
/* 6739 */           this.contents[visibleAnnotationsOffset] = (byte)counter;
/*      */         }
/*      */       }
/* 6742 */       if (globalCounter != 0) {
/* 6743 */         int attributeLength = this.contentsOffset - attributeLengthOffset - 4;
/* 6744 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 24);
/* 6745 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 16);
/* 6746 */         this.contents[(attributeLengthOffset++)] = (byte)(attributeLength >> 8);
/* 6747 */         this.contents[(attributeLengthOffset++)] = (byte)attributeLength;
/* 6748 */         attributesNumber++;
/*      */       }
/*      */       else {
/* 6751 */         this.contentsOffset = annotationAttributeOffset;
/*      */       }
/*      */     }
/* 6754 */     return attributesNumber;
/*      */   }
/*      */ 
/*      */   public byte[] getBytes()
/*      */   {
/* 6767 */     if (this.bytes == null) {
/* 6768 */       this.bytes = new byte[this.headerOffset + this.contentsOffset];
/* 6769 */       System.arraycopy(this.header, 0, this.bytes, 0, this.headerOffset);
/* 6770 */       System.arraycopy(this.contents, 0, this.bytes, this.headerOffset, this.contentsOffset);
/*      */     }
/* 6772 */     return this.bytes;
/*      */   }
/*      */ 
/*      */   public char[][] getCompoundName()
/*      */   {
/* 6781 */     return CharOperation.splitOn('/', fileName());
/*      */   }
/*      */ 
/*      */   private int getParametersCount(char[] methodSignature) {
/* 6785 */     int i = CharOperation.indexOf('(', methodSignature);
/* 6786 */     i++;
/* 6787 */     char currentCharacter = methodSignature[i];
/* 6788 */     if (currentCharacter == ')') {
/* 6789 */       return 0;
/*      */     }
/* 6791 */     int result = 0;
/*      */     while (true) {
/* 6793 */       currentCharacter = methodSignature[i];
/* 6794 */       if (currentCharacter == ')') {
/* 6795 */         return result;
/*      */       }
/* 6797 */       switch (currentCharacter)
/*      */       {
/*      */       case '[':
/* 6800 */         int scanType = scanType(methodSignature, i + 1);
/* 6801 */         result++;
/* 6802 */         i = scanType + 1;
/* 6803 */         break;
/*      */       case 'L':
/* 6805 */         int scanType = CharOperation.indexOf(';', methodSignature, 
/* 6806 */           i + 1);
/* 6807 */         result++;
/* 6808 */         i = scanType + 1;
/* 6809 */         break;
/*      */       case 'B':
/*      */       case 'C':
/*      */       case 'D':
/*      */       case 'F':
/*      */       case 'I':
/*      */       case 'J':
/*      */       case 'S':
/*      */       case 'Z':
/* 6818 */         result++;
/* 6819 */         i++;
/*      */       }
/*      */     }
/* 6822 */     throw new IllegalArgumentException();
/*      */   }
/*      */ 
/*      */   private char[] getReturnType(char[] methodSignature)
/*      */   {
/* 6829 */     int paren = CharOperation.lastIndexOf(')', methodSignature);
/*      */ 
/* 6831 */     return CharOperation.subarray(methodSignature, paren + 1, 
/* 6832 */       methodSignature.length);
/*      */   }
/*      */ 
/*      */   private final int i4At(byte[] reference, int relativeOffset, int structOffset)
/*      */   {
/* 6838 */     int position = relativeOffset + structOffset;
/* 6839 */     return ((reference[(position++)] & 0xFF) << 24) + (
/* 6840 */       (reference[(position++)] & 0xFF) << 16) + (
/* 6841 */       (reference[(position++)] & 0xFF) << 8) + (
/* 6842 */       reference[position] & 0xFF);
/*      */   }
/*      */ 
/*      */   protected void initByteArrays() {
/* 6846 */     int members = this.referenceBinding.methods().length + this.referenceBinding.fields().length;
/* 6847 */     this.header = new byte[1500];
/* 6848 */     this.contents = new byte[members < 15 ? 400 : 1500];
/*      */   }
/*      */ 
/*      */   public void initialize(SourceTypeBinding aType, ClassFile parentClassFile, boolean createProblemType)
/*      */   {
/* 6853 */     this.header[(this.headerOffset++)] = -54;
/* 6854 */     this.header[(this.headerOffset++)] = -2;
/* 6855 */     this.header[(this.headerOffset++)] = -70;
/* 6856 */     this.header[(this.headerOffset++)] = -66;
/*      */ 
/* 6858 */     long targetVersion = this.targetJDK;
/* 6859 */     if (targetVersion == 3342336L) {
/* 6860 */       targetVersion = 3276800L;
/*      */     }
/* 6862 */     this.header[(this.headerOffset++)] = (byte)(int)(targetVersion >> 8);
/* 6863 */     this.header[(this.headerOffset++)] = (byte)(int)(targetVersion >> 0);
/* 6864 */     this.header[(this.headerOffset++)] = (byte)(int)(targetVersion >> 24);
/* 6865 */     this.header[(this.headerOffset++)] = (byte)(int)(targetVersion >> 16);
/*      */ 
/* 6867 */     this.constantPoolOffset = this.headerOffset;
/* 6868 */     this.headerOffset += 2;
/* 6869 */     this.constantPool.initialize(this);
/*      */ 
/* 6872 */     int accessFlags = aType.getAccessFlags();
/* 6873 */     if (aType.isPrivate()) {
/* 6874 */       accessFlags &= -2;
/*      */     }
/* 6876 */     if (aType.isProtected()) {
/* 6877 */       accessFlags |= 1;
/*      */     }
/*      */ 
/* 6881 */     accessFlags = accessFlags & 
/* 6881 */       0xFFFFF6D1;
/*      */ 
/* 6890 */     if (!aType.isInterface()) {
/* 6891 */       accessFlags |= 32;
/*      */     }
/* 6893 */     if (aType.isAnonymousType()) {
/* 6894 */       accessFlags &= -17;
/*      */     }
/* 6896 */     this.enclosingClassFile = parentClassFile;
/*      */ 
/* 6900 */     this.contents[(this.contentsOffset++)] = (byte)(accessFlags >> 8);
/* 6901 */     this.contents[(this.contentsOffset++)] = (byte)accessFlags;
/* 6902 */     int classNameIndex = this.constantPool.literalIndexForType(aType);
/* 6903 */     this.contents[(this.contentsOffset++)] = (byte)(classNameIndex >> 8);
/* 6904 */     this.contents[(this.contentsOffset++)] = (byte)classNameIndex;
/*      */     int superclassNameIndex;
/*      */     int superclassNameIndex;
/* 6906 */     if (aType.isInterface())
/* 6907 */       superclassNameIndex = this.constantPool.literalIndexForType(ConstantPool.JavaLangObjectConstantPoolName);
/*      */     else {
/* 6909 */       superclassNameIndex = 
/* 6910 */         aType.superclass == null ? 0 : this.constantPool.literalIndexForType(aType.superclass);
/*      */     }
/* 6912 */     this.contents[(this.contentsOffset++)] = (byte)(superclassNameIndex >> 8);
/* 6913 */     this.contents[(this.contentsOffset++)] = (byte)superclassNameIndex;
/* 6914 */     ReferenceBinding[] superInterfacesBinding = aType.superInterfaces();
/* 6915 */     int interfacesCount = superInterfacesBinding.length;
/* 6916 */     this.contents[(this.contentsOffset++)] = (byte)(interfacesCount >> 8);
/* 6917 */     this.contents[(this.contentsOffset++)] = (byte)interfacesCount;
/* 6918 */     for (int i = 0; i < interfacesCount; i++) {
/* 6919 */       int interfaceIndex = this.constantPool.literalIndexForType(superInterfacesBinding[i]);
/* 6920 */       this.contents[(this.contentsOffset++)] = (byte)(interfaceIndex >> 8);
/* 6921 */       this.contents[(this.contentsOffset++)] = (byte)interfaceIndex;
/*      */     }
/* 6923 */     this.creatingProblemType = createProblemType;
/*      */ 
/* 6927 */     this.codeStream.maxFieldCount = aType.scope.outerMostClassScope().referenceType().maxFieldCount;
/*      */   }
/*      */ 
/*      */   private void initializeDefaultLocals(StackMapFrame frame, MethodBinding methodBinding, int maxLocals, int codeLength)
/*      */   {
/* 6934 */     if (maxLocals != 0) {
/* 6935 */       int resolvedPosition = 0;
/*      */ 
/* 6937 */       boolean isConstructor = methodBinding.isConstructor();
/* 6938 */       if (isConstructor) {
/* 6939 */         LocalVariableBinding localVariableBinding = new LocalVariableBinding("this".toCharArray(), methodBinding.declaringClass, 0, false);
/* 6940 */         localVariableBinding.resolvedPosition = 0;
/* 6941 */         this.codeStream.record(localVariableBinding);
/* 6942 */         localVariableBinding.recordInitializationStartPC(0);
/* 6943 */         localVariableBinding.recordInitializationEndPC(codeLength);
/* 6944 */         frame.putLocal(resolvedPosition, 
/* 6946 */           new VerificationTypeInfo(6, 
/* 6946 */           methodBinding.declaringClass));
/* 6947 */         resolvedPosition++;
/* 6948 */       } else if (!methodBinding.isStatic()) {
/* 6949 */         LocalVariableBinding localVariableBinding = new LocalVariableBinding("this".toCharArray(), methodBinding.declaringClass, 0, false);
/* 6950 */         localVariableBinding.resolvedPosition = 0;
/* 6951 */         this.codeStream.record(localVariableBinding);
/* 6952 */         localVariableBinding.recordInitializationStartPC(0);
/* 6953 */         localVariableBinding.recordInitializationEndPC(codeLength);
/* 6954 */         frame.putLocal(resolvedPosition, 
/* 6956 */           new VerificationTypeInfo(7, 
/* 6956 */           methodBinding.declaringClass));
/* 6957 */         resolvedPosition++;
/*      */       }
/*      */ 
/* 6960 */       if (isConstructor) {
/* 6961 */         if (methodBinding.declaringClass.isEnum()) {
/* 6962 */           LocalVariableBinding localVariableBinding = new LocalVariableBinding(" name".toCharArray(), this.referenceBinding.scope.getJavaLangString(), 0, false);
/* 6963 */           localVariableBinding.resolvedPosition = resolvedPosition;
/* 6964 */           this.codeStream.record(localVariableBinding);
/* 6965 */           localVariableBinding.recordInitializationStartPC(0);
/* 6966 */           localVariableBinding.recordInitializationEndPC(codeLength);
/*      */ 
/* 6968 */           frame.putLocal(resolvedPosition, 
/* 6970 */             new VerificationTypeInfo(11, 
/* 6970 */             ConstantPool.JavaLangStringConstantPoolName));
/* 6971 */           resolvedPosition++;
/*      */ 
/* 6973 */           localVariableBinding = new LocalVariableBinding(" ordinal".toCharArray(), TypeBinding.INT, 0, false);
/* 6974 */           localVariableBinding.resolvedPosition = resolvedPosition;
/* 6975 */           this.codeStream.record(localVariableBinding);
/* 6976 */           localVariableBinding.recordInitializationStartPC(0);
/* 6977 */           localVariableBinding.recordInitializationEndPC(codeLength);
/* 6978 */           frame.putLocal(resolvedPosition, 
/* 6979 */             new VerificationTypeInfo(TypeBinding.INT));
/* 6980 */           resolvedPosition++;
/*      */         }
/*      */ 
/* 6984 */         if (methodBinding.declaringClass.isNestedType())
/*      */         {
/*      */           ReferenceBinding[] enclosingInstanceTypes;
/* 6986 */           if ((enclosingInstanceTypes = methodBinding.declaringClass.syntheticEnclosingInstanceTypes()) != null) {
/* 6987 */             int i = 0; for (int max = enclosingInstanceTypes.length; i < max; i++)
/*      */             {
/* 6991 */               LocalVariableBinding localVariableBinding = new LocalVariableBinding((" enclosingType" + i).toCharArray(), enclosingInstanceTypes[i], 0, false);
/* 6992 */               localVariableBinding.resolvedPosition = resolvedPosition;
/* 6993 */               this.codeStream.record(localVariableBinding);
/* 6994 */               localVariableBinding.recordInitializationStartPC(0);
/* 6995 */               localVariableBinding.recordInitializationEndPC(codeLength);
/*      */ 
/* 6997 */               frame.putLocal(resolvedPosition, 
/* 6998 */                 new VerificationTypeInfo(enclosingInstanceTypes[i]));
/* 6999 */               resolvedPosition++;
/*      */             }
/*      */           }
/*      */           TypeBinding[] arguments;
/* 7004 */           if ((arguments = methodBinding.parameters) != null) {
/* 7005 */             int i = 0; for (int max = arguments.length; i < max; i++) {
/* 7006 */               TypeBinding typeBinding = arguments[i];
/* 7007 */               frame.putLocal(resolvedPosition, 
/* 7008 */                 new VerificationTypeInfo(typeBinding));
/* 7009 */               switch (typeBinding.id) {
/*      */               case 7:
/*      */               case 8:
/* 7012 */                 resolvedPosition += 2;
/* 7013 */                 break;
/*      */               default:
/* 7015 */                 resolvedPosition++;
/*      */               }
/*      */             }
/*      */           }
/*      */           SyntheticArgumentBinding[] syntheticArguments;
/* 7021 */           if ((syntheticArguments = methodBinding.declaringClass.syntheticOuterLocalVariables()) != null) {
/* 7022 */             int i = 0; for (int max = syntheticArguments.length; i < max; i++) {
/* 7023 */               TypeBinding typeBinding = syntheticArguments[i].type;
/* 7024 */               LocalVariableBinding localVariableBinding = new LocalVariableBinding((" synthetic" + i).toCharArray(), typeBinding, 0, false);
/* 7025 */               localVariableBinding.resolvedPosition = resolvedPosition;
/* 7026 */               this.codeStream.record(localVariableBinding);
/* 7027 */               localVariableBinding.recordInitializationStartPC(0);
/* 7028 */               localVariableBinding.recordInitializationEndPC(codeLength);
/*      */ 
/* 7030 */               frame.putLocal(resolvedPosition, 
/* 7031 */                 new VerificationTypeInfo(typeBinding));
/* 7032 */               switch (typeBinding.id) {
/*      */               case 7:
/*      */               case 8:
/* 7035 */                 resolvedPosition += 2;
/* 7036 */                 break;
/*      */               default:
/* 7038 */                 resolvedPosition++;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*      */           TypeBinding[] arguments;
/* 7044 */           if ((arguments = methodBinding.parameters) != null) {
/* 7045 */             int i = 0; for (int max = arguments.length; i < max; i++) {
/* 7046 */               TypeBinding typeBinding = arguments[i];
/* 7047 */               frame.putLocal(resolvedPosition, 
/* 7048 */                 new VerificationTypeInfo(typeBinding));
/* 7049 */               switch (typeBinding.id) {
/*      */               case 7:
/*      */               case 8:
/* 7052 */                 resolvedPosition += 2;
/* 7053 */                 break;
/*      */               default:
/* 7055 */                 resolvedPosition++;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*      */         TypeBinding[] arguments;
/* 7062 */         if ((arguments = methodBinding.parameters) != null) {
/* 7063 */           int i = 0; for (int max = arguments.length; i < max; i++) {
/* 7064 */             TypeBinding typeBinding = arguments[i];
/* 7065 */             frame.putLocal(resolvedPosition, 
/* 7066 */               new VerificationTypeInfo(typeBinding));
/* 7067 */             switch (typeBinding.id) {
/*      */             case 7:
/*      */             case 8:
/* 7070 */               resolvedPosition += 2;
/* 7071 */               break;
/*      */             default:
/* 7073 */               resolvedPosition++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initializeLocals(boolean isStatic, int currentPC, StackMapFrame currentFrame) {
/* 7082 */     VerificationTypeInfo[] locals = currentFrame.locals;
/* 7083 */     int localsLength = locals.length;
/* 7084 */     int i = 0;
/* 7085 */     if (!isStatic)
/*      */     {
/* 7087 */       i = 1;
/*      */     }
/* 7089 */     for (; i < localsLength; i++) {
/* 7090 */       locals[i] = null;
/*      */     }
/* 7092 */     i = 0;
/* 7093 */     for (int max = this.codeStream.allLocalsCounter; i < max; i++) {
/* 7094 */       LocalVariableBinding localVariable = this.codeStream.locals[i];
/* 7095 */       if (localVariable != null) {
/* 7096 */         int resolvedPosition = localVariable.resolvedPosition;
/* 7097 */         TypeBinding localVariableTypeBinding = localVariable.type;
/* 7098 */         for (int j = 0; j < localVariable.initializationCount; j++) {
/* 7099 */           int startPC = localVariable.initializationPCs[(j << 1)];
/* 7100 */           int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 7101 */           if (currentPC < startPC)
/*      */             continue;
/* 7103 */           if (currentPC >= endPC)
/*      */             continue;
/* 7105 */           if (currentFrame.locals[resolvedPosition] != null) break;
/* 7106 */           currentFrame.locals[resolvedPosition] = 
/* 7107 */             new VerificationTypeInfo(
/* 7108 */             localVariableTypeBinding);
/*      */ 
/* 7110 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isRuntimeInvisible(Annotation annotation) {
/* 7117 */     TypeBinding annotationBinding = annotation.resolvedType;
/* 7118 */     if (annotationBinding == null) {
/* 7119 */       return false;
/*      */     }
/* 7121 */     long metaTagBits = annotationBinding.getAnnotationTagBits();
/* 7122 */     if ((metaTagBits & 0x0) == 0L) {
/* 7123 */       return true;
/*      */     }
/* 7125 */     return (metaTagBits & 0x0) == 35184372088832L;
/*      */   }
/*      */ 
/*      */   private boolean isRuntimeVisible(Annotation annotation) {
/* 7129 */     TypeBinding annotationBinding = annotation.resolvedType;
/* 7130 */     if (annotationBinding == null) {
/* 7131 */       return false;
/*      */     }
/* 7133 */     long metaTagBits = annotationBinding.getAnnotationTagBits();
/* 7134 */     if ((metaTagBits & 0x0) == 0L) {
/* 7135 */       return false;
/*      */     }
/* 7137 */     return (metaTagBits & 0x0) == 52776558133248L;
/*      */   }
/*      */ 
/*      */   public ClassFile outerMostEnclosingClassFile()
/*      */   {
/* 7147 */     ClassFile current = this;
/* 7148 */     while (current.enclosingClassFile != null)
/* 7149 */       current = current.enclosingClassFile;
/* 7150 */     return current;
/*      */   }
/*      */ 
/*      */   public void recordInnerClasses(TypeBinding binding) {
/* 7154 */     if (this.innerClassesBindings == null) {
/* 7155 */       this.innerClassesBindings = new HashSet(5);
/*      */     }
/* 7157 */     ReferenceBinding innerClass = (ReferenceBinding)binding;
/* 7158 */     this.innerClassesBindings.add(innerClass.erasure());
/* 7159 */     ReferenceBinding enclosingType = innerClass.enclosingType();
/* 7160 */     while ((enclosingType != null) && (
/* 7161 */       enclosingType.isNestedType())) {
/* 7162 */       this.innerClassesBindings.add(enclosingType.erasure());
/* 7163 */       enclosingType = enclosingType.enclosingType();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reset(SourceTypeBinding typeBinding)
/*      */   {
/* 7169 */     CompilerOptions options = typeBinding.scope.compilerOptions();
/* 7170 */     this.referenceBinding = typeBinding;
/* 7171 */     this.isNestedType = typeBinding.isNestedType();
/* 7172 */     this.targetJDK = options.targetJDK;
/* 7173 */     this.produceAttributes = options.produceDebugAttributes;
/* 7174 */     if (this.targetJDK >= 3276800L) {
/* 7175 */       this.produceAttributes |= 8;
/* 7176 */     } else if (this.targetJDK == 2949124L) {
/* 7177 */       this.targetJDK = 2949123L;
/* 7178 */       this.produceAttributes |= 16;
/*      */     }
/* 7180 */     this.bytes = null;
/* 7181 */     this.constantPool.reset();
/* 7182 */     this.codeStream.reset(this);
/* 7183 */     this.constantPoolOffset = 0;
/* 7184 */     this.contentsOffset = 0;
/* 7185 */     this.creatingProblemType = false;
/* 7186 */     this.enclosingClassFile = null;
/* 7187 */     this.headerOffset = 0;
/* 7188 */     this.methodCount = 0;
/* 7189 */     this.methodCountOffset = 0;
/* 7190 */     if (this.innerClassesBindings != null) {
/* 7191 */       this.innerClassesBindings.clear();
/*      */     }
/* 7193 */     this.missingTypes = null;
/* 7194 */     this.visitedTypes = null;
/*      */   }
/*      */ 
/*      */   private final void resizeContents(int minimalSize)
/*      */   {
/* 7201 */     int length = this.contents.length;
/* 7202 */     int toAdd = length;
/* 7203 */     if (toAdd < minimalSize)
/* 7204 */       toAdd = minimalSize;
/* 7205 */     System.arraycopy(this.contents, 0, this.contents = new byte[length + toAdd], 0, length);
/*      */   }
/*      */ 
/*      */   private VerificationTypeInfo retrieveLocal(int currentPC, int resolvedPosition) {
/* 7209 */     int i = 0; for (int max = this.codeStream.allLocalsCounter; i < max; i++) {
/* 7210 */       LocalVariableBinding localVariable = this.codeStream.locals[i];
/* 7211 */       if ((localVariable == null) || 
/* 7212 */         (resolvedPosition != localVariable.resolvedPosition)) continue;
/* 7213 */       for (int j = 0; j < localVariable.initializationCount; j++) {
/* 7214 */         int startPC = localVariable.initializationPCs[(j << 1)];
/* 7215 */         int endPC = localVariable.initializationPCs[((j << 1) + 1)];
/* 7216 */         if (currentPC < startPC)
/*      */           continue;
/* 7218 */         if (currentPC < endPC)
/*      */         {
/* 7220 */           return new VerificationTypeInfo(localVariable.type);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 7225 */     return null;
/*      */   }
/*      */ 
/*      */   private int scanType(char[] methodSignature, int index) {
/* 7229 */     switch (methodSignature[index])
/*      */     {
/*      */     case '[':
/* 7232 */       return scanType(methodSignature, index + 1);
/*      */     case 'L':
/* 7234 */       return CharOperation.indexOf(';', methodSignature, index + 1);
/*      */     case 'B':
/*      */     case 'C':
/*      */     case 'D':
/*      */     case 'F':
/*      */     case 'I':
/*      */     case 'J':
/*      */     case 'S':
/*      */     case 'Z':
/* 7243 */       return index;
/*      */     }
/* 7245 */     throw new IllegalArgumentException();
/*      */   }
/*      */ 
/*      */   public void setForMethodInfos()
/*      */   {
/* 7255 */     this.methodCountOffset = this.contentsOffset;
/* 7256 */     this.contentsOffset += 2;
/*      */   }
/*      */ 
/*      */   public void traverse(MethodBinding methodBinding, int maxLocals, byte[] bytecodes, int codeOffset, int codeLength, ArrayList frames, boolean isClinit) {
/* 7260 */     StackMapFrameCodeStream stackMapFrameCodeStream = (StackMapFrameCodeStream)this.codeStream;
/* 7261 */     int[] framePositions = stackMapFrameCodeStream.getFramePositions();
/* 7262 */     int pc = codeOffset;
/*      */ 
/* 7264 */     int[] constantPoolOffsets = this.constantPool.offsets;
/* 7265 */     byte[] poolContents = this.constantPool.poolContent;
/*      */ 
/* 7268 */     int indexInFramePositions = 0;
/* 7269 */     int framePositionsLength = framePositions.length;
/* 7270 */     int currentFramePosition = framePositions[0];
/*      */ 
/* 7273 */     int indexInStackDepthMarkers = 0;
/* 7274 */     StackMapFrameCodeStream.StackDepthMarker[] stackDepthMarkers = stackMapFrameCodeStream.getStackDepthMarkers();
/* 7275 */     int stackDepthMarkersLength = stackDepthMarkers == null ? 0 : stackDepthMarkers.length;
/* 7276 */     boolean hasStackDepthMarkers = stackDepthMarkersLength != 0;
/* 7277 */     StackMapFrameCodeStream.StackDepthMarker stackDepthMarker = null;
/* 7278 */     if (hasStackDepthMarkers) {
/* 7279 */       stackDepthMarker = stackDepthMarkers[0];
/*      */     }
/*      */ 
/* 7283 */     int indexInStackMarkers = 0;
/* 7284 */     StackMapFrameCodeStream.StackMarker[] stackMarkers = stackMapFrameCodeStream.getStackMarkers();
/* 7285 */     int stackMarkersLength = stackMarkers == null ? 0 : stackMarkers.length;
/* 7286 */     boolean hasStackMarkers = stackMarkersLength != 0;
/* 7287 */     StackMapFrameCodeStream.StackMarker stackMarker = null;
/* 7288 */     if (hasStackMarkers) {
/* 7289 */       stackMarker = stackMarkers[0];
/*      */     }
/*      */ 
/* 7293 */     int indexInExceptionMarkers = 0;
/* 7294 */     StackMapFrameCodeStream.ExceptionMarker[] exceptionMarkers = stackMapFrameCodeStream.getExceptionMarkers();
/* 7295 */     int exceptionsMarkersLength = exceptionMarkers == null ? 0 : exceptionMarkers.length;
/* 7296 */     boolean hasExceptionMarkers = exceptionsMarkersLength != 0;
/* 7297 */     StackMapFrameCodeStream.ExceptionMarker exceptionMarker = null;
/* 7298 */     if (hasExceptionMarkers) {
/* 7299 */       exceptionMarker = exceptionMarkers[0];
/*      */     }
/*      */ 
/* 7302 */     StackMapFrame frame = new StackMapFrame(maxLocals);
/* 7303 */     if (!isClinit) {
/* 7304 */       initializeDefaultLocals(frame, methodBinding, maxLocals, codeLength);
/*      */     }
/* 7306 */     frame.pc = -1;
/* 7307 */     frames.add(frame.duplicate());
/*      */     do {
/* 7309 */       int currentPC = pc - codeOffset;
/* 7310 */       if ((hasStackMarkers) && (stackMarker.pc == currentPC)) {
/* 7311 */         VerificationTypeInfo[] infos = frame.stackItems;
/* 7312 */         VerificationTypeInfo[] tempInfos = new VerificationTypeInfo[frame.numberOfStackItems];
/* 7313 */         System.arraycopy(infos, 0, tempInfos, 0, frame.numberOfStackItems);
/* 7314 */         stackMarker.setInfos(tempInfos);
/* 7315 */       } else if ((hasStackMarkers) && (stackMarker.destinationPC == currentPC)) {
/* 7316 */         VerificationTypeInfo[] infos = stackMarker.infos;
/* 7317 */         frame.stackItems = infos;
/* 7318 */         frame.numberOfStackItems = infos.length;
/* 7319 */         indexInStackMarkers++;
/* 7320 */         if (indexInStackMarkers < stackMarkersLength)
/* 7321 */           stackMarker = stackMarkers[indexInStackMarkers];
/*      */         else {
/* 7323 */           hasStackMarkers = false;
/*      */         }
/*      */       }
/* 7326 */       if ((hasStackDepthMarkers) && (stackDepthMarker.pc == currentPC)) {
/* 7327 */         TypeBinding typeBinding = stackDepthMarker.typeBinding;
/* 7328 */         if (typeBinding != null) {
/* 7329 */           if (stackDepthMarker.delta > 0)
/* 7330 */             frame.addStackItem(new VerificationTypeInfo(typeBinding));
/*      */           else
/* 7332 */             frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(typeBinding);
/*      */         }
/*      */         else {
/* 7335 */           frame.numberOfStackItems -= 1;
/*      */         }
/* 7337 */         indexInStackDepthMarkers++;
/* 7338 */         if (indexInStackDepthMarkers < stackDepthMarkersLength)
/* 7339 */           stackDepthMarker = stackDepthMarkers[indexInStackDepthMarkers];
/*      */         else {
/* 7341 */           hasStackDepthMarkers = false;
/*      */         }
/*      */       }
/* 7344 */       if ((hasExceptionMarkers) && (exceptionMarker.pc == currentPC)) {
/* 7345 */         frame.numberOfStackItems = 0;
/* 7346 */         frame.addStackItem(new VerificationTypeInfo(0, 7, exceptionMarker.constantPoolName));
/* 7347 */         indexInExceptionMarkers++;
/* 7348 */         if (indexInExceptionMarkers < exceptionsMarkersLength)
/* 7349 */           exceptionMarker = exceptionMarkers[indexInExceptionMarkers];
/*      */         else {
/* 7351 */           hasExceptionMarkers = false;
/*      */         }
/*      */       }
/* 7354 */       if (currentFramePosition < currentPC) {
/*      */         do {
/* 7356 */           indexInFramePositions++;
/* 7357 */           if (indexInFramePositions < framePositionsLength) {
/* 7358 */             currentFramePosition = framePositions[indexInFramePositions];
/*      */           }
/*      */           else
/* 7361 */             return;
/*      */         }
/* 7363 */         while (currentFramePosition < currentPC);
/*      */       }
/* 7365 */       if (currentFramePosition == currentPC)
/*      */       {
/* 7367 */         StackMapFrame currentFrame = frame.duplicate();
/* 7368 */         currentFrame.pc = currentPC;
/*      */ 
/* 7370 */         initializeLocals(isClinit ? true : methodBinding.isStatic(), currentPC, currentFrame);
/*      */ 
/* 7372 */         frames.add(currentFrame);
/* 7373 */         indexInFramePositions++;
/* 7374 */         if (indexInFramePositions < framePositionsLength) {
/* 7375 */           currentFramePosition = framePositions[indexInFramePositions];
/*      */         }
/*      */         else {
/* 7378 */           return;
/*      */         }
/*      */       }
/* 7381 */       byte opcode = (byte)u1At(bytecodes, 0, pc);
/* 7382 */       switch (opcode) {
/*      */       case 0:
/* 7384 */         pc++;
/* 7385 */         break;
/*      */       case 1:
/* 7387 */         frame.addStackItem(TypeBinding.NULL);
/* 7388 */         pc++;
/* 7389 */         break;
/*      */       case 2:
/*      */       case 3:
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 8:
/* 7397 */         frame.addStackItem(TypeBinding.INT);
/* 7398 */         pc++;
/* 7399 */         break;
/*      */       case 9:
/*      */       case 10:
/* 7402 */         frame.addStackItem(TypeBinding.LONG);
/* 7403 */         pc++;
/* 7404 */         break;
/*      */       case 11:
/*      */       case 12:
/*      */       case 13:
/* 7408 */         frame.addStackItem(TypeBinding.FLOAT);
/* 7409 */         pc++;
/* 7410 */         break;
/*      */       case 14:
/*      */       case 15:
/* 7413 */         frame.addStackItem(TypeBinding.DOUBLE);
/* 7414 */         pc++;
/* 7415 */         break;
/*      */       case 16:
/* 7417 */         frame.addStackItem(TypeBinding.BYTE);
/* 7418 */         pc += 2;
/* 7419 */         break;
/*      */       case 17:
/* 7421 */         frame.addStackItem(TypeBinding.SHORT);
/* 7422 */         pc += 3;
/* 7423 */         break;
/*      */       case 18:
/* 7425 */         int index = u1At(bytecodes, 1, pc);
/* 7426 */         switch (u1At(poolContents, 0, constantPoolOffsets[index])) {
/*      */         case 8:
/* 7428 */           frame
/* 7429 */             .addStackItem(new VerificationTypeInfo(
/* 7430 */             11, 
/* 7431 */             ConstantPool.JavaLangStringConstantPoolName));
/* 7432 */           break;
/*      */         case 3:
/* 7434 */           frame.addStackItem(TypeBinding.INT);
/* 7435 */           break;
/*      */         case 4:
/* 7437 */           frame.addStackItem(TypeBinding.FLOAT);
/* 7438 */           break;
/*      */         case 7:
/* 7440 */           frame.addStackItem(
/* 7442 */             new VerificationTypeInfo(16, 
/* 7442 */             ConstantPool.JavaLangClassConstantPoolName));
/*      */         case 5:
/* 7444 */         case 6: } pc += 2;
/* 7445 */         break;
/*      */       case 19:
/* 7447 */         int index = u2At(bytecodes, 1, pc);
/* 7448 */         switch (u1At(poolContents, 0, constantPoolOffsets[index])) {
/*      */         case 8:
/* 7450 */           frame
/* 7451 */             .addStackItem(new VerificationTypeInfo(
/* 7452 */             11, 
/* 7453 */             ConstantPool.JavaLangStringConstantPoolName));
/* 7454 */           break;
/*      */         case 3:
/* 7456 */           frame.addStackItem(TypeBinding.INT);
/* 7457 */           break;
/*      */         case 4:
/* 7459 */           frame.addStackItem(TypeBinding.FLOAT);
/* 7460 */           break;
/*      */         case 7:
/* 7462 */           frame.addStackItem(
/* 7464 */             new VerificationTypeInfo(16, 
/* 7464 */             ConstantPool.JavaLangClassConstantPoolName));
/*      */         case 5:
/* 7466 */         case 6: } pc += 3;
/* 7467 */         break;
/*      */       case 20:
/* 7469 */         int index = u2At(bytecodes, 1, pc);
/* 7470 */         switch (u1At(poolContents, 0, constantPoolOffsets[index])) {
/*      */         case 6:
/* 7472 */           frame.addStackItem(TypeBinding.DOUBLE);
/* 7473 */           break;
/*      */         case 5:
/* 7475 */           frame.addStackItem(TypeBinding.LONG);
/*      */         }
/*      */ 
/* 7478 */         pc += 3;
/* 7479 */         break;
/*      */       case 21:
/* 7481 */         frame.addStackItem(TypeBinding.INT);
/* 7482 */         pc += 2;
/* 7483 */         break;
/*      */       case 22:
/* 7485 */         frame.addStackItem(TypeBinding.LONG);
/* 7486 */         pc += 2;
/* 7487 */         break;
/*      */       case 23:
/* 7489 */         frame.addStackItem(TypeBinding.FLOAT);
/* 7490 */         pc += 2;
/* 7491 */         break;
/*      */       case 24:
/* 7493 */         frame.addStackItem(TypeBinding.DOUBLE);
/* 7494 */         pc += 2;
/* 7495 */         break;
/*      */       case 25:
/* 7497 */         int index = u1At(bytecodes, 1, pc);
/* 7498 */         VerificationTypeInfo localsN = retrieveLocal(currentPC, index);
/* 7499 */         frame.addStackItem(localsN);
/* 7500 */         pc += 2;
/* 7501 */         break;
/*      */       case 26:
/*      */       case 27:
/*      */       case 28:
/*      */       case 29:
/* 7506 */         frame.addStackItem(TypeBinding.INT);
/* 7507 */         pc++;
/* 7508 */         break;
/*      */       case 30:
/*      */       case 31:
/*      */       case 32:
/*      */       case 33:
/* 7513 */         frame.addStackItem(TypeBinding.LONG);
/* 7514 */         pc++;
/* 7515 */         break;
/*      */       case 34:
/*      */       case 35:
/*      */       case 36:
/*      */       case 37:
/* 7520 */         frame.addStackItem(TypeBinding.FLOAT);
/* 7521 */         pc++;
/* 7522 */         break;
/*      */       case 38:
/*      */       case 39:
/*      */       case 40:
/*      */       case 41:
/* 7527 */         frame.addStackItem(TypeBinding.DOUBLE);
/* 7528 */         pc++;
/* 7529 */         break;
/*      */       case 42:
/* 7531 */         VerificationTypeInfo locals0 = frame.locals[0];
/*      */ 
/* 7533 */         if (locals0 == null) {
/* 7534 */           locals0 = retrieveLocal(currentPC, 0);
/*      */         }
/* 7536 */         frame.addStackItem(locals0);
/* 7537 */         pc++;
/* 7538 */         break;
/*      */       case 43:
/* 7540 */         VerificationTypeInfo locals1 = retrieveLocal(currentPC, 1);
/* 7541 */         frame.addStackItem(locals1);
/* 7542 */         pc++;
/* 7543 */         break;
/*      */       case 44:
/* 7545 */         VerificationTypeInfo locals2 = retrieveLocal(currentPC, 2);
/* 7546 */         frame.addStackItem(locals2);
/* 7547 */         pc++;
/* 7548 */         break;
/*      */       case 45:
/* 7550 */         VerificationTypeInfo locals3 = retrieveLocal(currentPC, 3);
/* 7551 */         frame.addStackItem(locals3);
/* 7552 */         pc++;
/* 7553 */         break;
/*      */       case 46:
/* 7555 */         frame.numberOfStackItems -= 2;
/* 7556 */         frame.addStackItem(TypeBinding.INT);
/* 7557 */         pc++;
/* 7558 */         break;
/*      */       case 47:
/* 7560 */         frame.numberOfStackItems -= 2;
/* 7561 */         frame.addStackItem(TypeBinding.LONG);
/* 7562 */         pc++;
/* 7563 */         break;
/*      */       case 48:
/* 7565 */         frame.numberOfStackItems -= 2;
/* 7566 */         frame.addStackItem(TypeBinding.FLOAT);
/* 7567 */         pc++;
/* 7568 */         break;
/*      */       case 49:
/* 7570 */         frame.numberOfStackItems -= 2;
/* 7571 */         frame.addStackItem(TypeBinding.DOUBLE);
/* 7572 */         pc++;
/* 7573 */         break;
/*      */       case 50:
/* 7575 */         frame.numberOfStackItems -= 1;
/* 7576 */         frame.replaceWithElementType();
/* 7577 */         pc++;
/* 7578 */         break;
/*      */       case 51:
/* 7580 */         frame.numberOfStackItems -= 2;
/* 7581 */         frame.addStackItem(TypeBinding.BYTE);
/* 7582 */         pc++;
/* 7583 */         break;
/*      */       case 52:
/* 7585 */         frame.numberOfStackItems -= 2;
/* 7586 */         frame.addStackItem(TypeBinding.CHAR);
/* 7587 */         pc++;
/* 7588 */         break;
/*      */       case 53:
/* 7590 */         frame.numberOfStackItems -= 2;
/* 7591 */         frame.addStackItem(TypeBinding.SHORT);
/* 7592 */         pc++;
/* 7593 */         break;
/*      */       case 54:
/*      */       case 55:
/*      */       case 56:
/*      */       case 57:
/* 7598 */         frame.numberOfStackItems -= 1;
/* 7599 */         pc += 2;
/* 7600 */         break;
/*      */       case 58:
/* 7602 */         int index = u1At(bytecodes, 1, pc);
/* 7603 */         frame.numberOfStackItems -= 1;
/* 7604 */         pc += 2;
/* 7605 */         break;
/*      */       case 75:
/* 7607 */         frame.locals[0] = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7608 */         frame.numberOfStackItems -= 1;
/* 7609 */         pc++;
/* 7610 */         break;
/*      */       case 59:
/*      */       case 60:
/*      */       case 61:
/*      */       case 62:
/*      */       case 63:
/*      */       case 64:
/*      */       case 65:
/*      */       case 66:
/*      */       case 67:
/*      */       case 68:
/*      */       case 69:
/*      */       case 70:
/*      */       case 71:
/*      */       case 72:
/*      */       case 73:
/*      */       case 74:
/*      */       case 76:
/*      */       case 77:
/*      */       case 78:
/* 7630 */         frame.numberOfStackItems -= 1;
/* 7631 */         pc++;
/* 7632 */         break;
/*      */       case 79:
/*      */       case 80:
/*      */       case 81:
/*      */       case 82:
/*      */       case 83:
/*      */       case 84:
/*      */       case 85:
/*      */       case 86:
/* 7641 */         frame.numberOfStackItems -= 3;
/* 7642 */         pc++;
/* 7643 */         break;
/*      */       case 87:
/* 7645 */         frame.numberOfStackItems -= 1;
/* 7646 */         pc++;
/* 7647 */         break;
/*      */       case 88:
/* 7649 */         int numberOfStackItems = frame.numberOfStackItems;
/* 7650 */         switch (frame.stackItems[(numberOfStackItems - 1)].id()) {
/*      */         case 7:
/*      */         case 8:
/* 7653 */           frame.numberOfStackItems -= 1;
/* 7654 */           break;
/*      */         default:
/* 7656 */           frame.numberOfStackItems -= 2;
/*      */         }
/* 7658 */         pc++;
/* 7659 */         break;
/*      */       case 89:
/* 7661 */         frame.addStackItem(frame.stackItems[(frame.numberOfStackItems - 1)]);
/* 7662 */         pc++;
/* 7663 */         break;
/*      */       case 90:
/* 7665 */         VerificationTypeInfo info = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7666 */         frame.numberOfStackItems -= 1;
/* 7667 */         VerificationTypeInfo info2 = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7668 */         frame.numberOfStackItems -= 1;
/* 7669 */         frame.addStackItem(info);
/* 7670 */         frame.addStackItem(info2);
/* 7671 */         frame.addStackItem(info);
/* 7672 */         pc++;
/* 7673 */         break;
/*      */       case 91:
/* 7675 */         VerificationTypeInfo info = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7676 */         frame.numberOfStackItems -= 1;
/* 7677 */         VerificationTypeInfo info2 = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7678 */         frame.numberOfStackItems -= 1;
/* 7679 */         switch (info2.id()) {
/*      */         case 7:
/*      */         case 8:
/* 7682 */           frame.addStackItem(info);
/* 7683 */           frame.addStackItem(info2);
/* 7684 */           frame.addStackItem(info);
/* 7685 */           break;
/*      */         default:
/* 7687 */           int numberOfStackItems = frame.numberOfStackItems;
/* 7688 */           VerificationTypeInfo info3 = frame.stackItems[(numberOfStackItems - 1)];
/* 7689 */           frame.numberOfStackItems -= 1;
/* 7690 */           frame.addStackItem(info);
/* 7691 */           frame.addStackItem(info3);
/* 7692 */           frame.addStackItem(info2);
/* 7693 */           frame.addStackItem(info);
/*      */         }
/* 7695 */         pc++;
/* 7696 */         break;
/*      */       case 92:
/* 7698 */         VerificationTypeInfo info = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7699 */         frame.numberOfStackItems -= 1;
/* 7700 */         switch (info.id()) {
/*      */         case 7:
/*      */         case 8:
/* 7703 */           frame.addStackItem(info);
/* 7704 */           frame.addStackItem(info);
/* 7705 */           break;
/*      */         default:
/* 7707 */           VerificationTypeInfo info2 = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7708 */           frame.numberOfStackItems -= 1;
/* 7709 */           frame.addStackItem(info2);
/* 7710 */           frame.addStackItem(info);
/* 7711 */           frame.addStackItem(info2);
/* 7712 */           frame.addStackItem(info);
/*      */         }
/* 7714 */         pc++;
/* 7715 */         break;
/*      */       case 93:
/* 7717 */         VerificationTypeInfo info = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7718 */         frame.numberOfStackItems -= 1;
/* 7719 */         VerificationTypeInfo info2 = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7720 */         frame.numberOfStackItems -= 1;
/* 7721 */         switch (info.id()) {
/*      */         case 7:
/*      */         case 8:
/* 7724 */           frame.addStackItem(info);
/* 7725 */           frame.addStackItem(info2);
/* 7726 */           frame.addStackItem(info);
/* 7727 */           break;
/*      */         default:
/* 7729 */           VerificationTypeInfo info3 = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7730 */           frame.numberOfStackItems -= 1;
/* 7731 */           frame.addStackItem(info2);
/* 7732 */           frame.addStackItem(info);
/* 7733 */           frame.addStackItem(info3);
/* 7734 */           frame.addStackItem(info2);
/* 7735 */           frame.addStackItem(info);
/*      */         }
/* 7737 */         pc++;
/* 7738 */         break;
/*      */       case 94:
/* 7740 */         int numberOfStackItems = frame.numberOfStackItems;
/* 7741 */         VerificationTypeInfo info = frame.stackItems[(numberOfStackItems - 1)];
/* 7742 */         frame.numberOfStackItems -= 1;
/* 7743 */         VerificationTypeInfo info2 = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 7744 */         frame.numberOfStackItems -= 1;
/* 7745 */         switch (info.id()) {
/*      */         case 7:
/*      */         case 8:
/* 7748 */           switch (info2.id())
/*      */           {
/*      */           case 7:
/*      */           case 8:
/* 7752 */             frame.addStackItem(info);
/* 7753 */             frame.addStackItem(info2);
/* 7754 */             frame.addStackItem(info);
/* 7755 */             break;
/*      */           default:
/* 7758 */             numberOfStackItems = frame.numberOfStackItems;
/* 7759 */             VerificationTypeInfo info3 = frame.stackItems[(numberOfStackItems - 1)];
/* 7760 */             frame.numberOfStackItems -= 1;
/* 7761 */             frame.addStackItem(info);
/* 7762 */             frame.addStackItem(info3);
/* 7763 */             frame.addStackItem(info2);
/* 7764 */             frame.addStackItem(info);
/*      */           }
/* 7766 */           break;
/*      */         default:
/* 7768 */           numberOfStackItems = frame.numberOfStackItems;
/* 7769 */           VerificationTypeInfo info3 = frame.stackItems[(numberOfStackItems - 1)];
/* 7770 */           frame.numberOfStackItems -= 1;
/* 7771 */           switch (info3.id())
/*      */           {
/*      */           case 7:
/*      */           case 8:
/* 7775 */             frame.addStackItem(info2);
/* 7776 */             frame.addStackItem(info);
/* 7777 */             frame.addStackItem(info3);
/* 7778 */             frame.addStackItem(info2);
/* 7779 */             frame.addStackItem(info);
/* 7780 */             break;
/*      */           default:
/* 7783 */             numberOfStackItems = frame.numberOfStackItems;
/* 7784 */             VerificationTypeInfo info4 = frame.stackItems[(numberOfStackItems - 1)];
/* 7785 */             frame.numberOfStackItems -= 1;
/* 7786 */             frame.addStackItem(info2);
/* 7787 */             frame.addStackItem(info);
/* 7788 */             frame.addStackItem(info4);
/* 7789 */             frame.addStackItem(info3);
/* 7790 */             frame.addStackItem(info2);
/* 7791 */             frame.addStackItem(info);
/*      */           }
/*      */         }
/* 7794 */         pc++;
/* 7795 */         break;
/*      */       case 95:
/* 7797 */         int numberOfStackItems = frame.numberOfStackItems;
/* 7798 */         VerificationTypeInfo info = frame.stackItems[(numberOfStackItems - 1)];
/* 7799 */         VerificationTypeInfo info2 = frame.stackItems[(numberOfStackItems - 2)];
/* 7800 */         frame.stackItems[(numberOfStackItems - 1)] = info2;
/* 7801 */         frame.stackItems[(numberOfStackItems - 2)] = info;
/* 7802 */         pc++;
/* 7803 */         break;
/*      */       case -128:
/*      */       case -127:
/*      */       case -126:
/*      */       case -125:
/*      */       case 96:
/*      */       case 97:
/*      */       case 98:
/*      */       case 99:
/*      */       case 100:
/*      */       case 101:
/*      */       case 102:
/*      */       case 103:
/*      */       case 104:
/*      */       case 105:
/*      */       case 106:
/*      */       case 107:
/*      */       case 108:
/*      */       case 109:
/*      */       case 110:
/*      */       case 111:
/*      */       case 112:
/*      */       case 113:
/*      */       case 114:
/*      */       case 115:
/*      */       case 120:
/*      */       case 121:
/*      */       case 122:
/*      */       case 123:
/*      */       case 124:
/*      */       case 125:
/*      */       case 126:
/*      */       case 127:
/* 7836 */         frame.numberOfStackItems -= 1;
/* 7837 */         pc++;
/* 7838 */         break;
/*      */       case 116:
/*      */       case 117:
/*      */       case 118:
/*      */       case 119:
/* 7843 */         pc++;
/* 7844 */         break;
/*      */       case -124:
/* 7846 */         pc += 3;
/* 7847 */         break;
/*      */       case -123:
/* 7849 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.LONG);
/* 7850 */         pc++;
/* 7851 */         break;
/*      */       case -122:
/* 7853 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.FLOAT);
/* 7854 */         pc++;
/* 7855 */         break;
/*      */       case -121:
/* 7857 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.DOUBLE);
/* 7858 */         pc++;
/* 7859 */         break;
/*      */       case -120:
/* 7861 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.INT);
/* 7862 */         pc++;
/* 7863 */         break;
/*      */       case -119:
/* 7865 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.FLOAT);
/* 7866 */         pc++;
/* 7867 */         break;
/*      */       case -118:
/* 7869 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.DOUBLE);
/* 7870 */         pc++;
/* 7871 */         break;
/*      */       case -117:
/* 7873 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.INT);
/* 7874 */         pc++;
/* 7875 */         break;
/*      */       case -116:
/* 7877 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.LONG);
/* 7878 */         pc++;
/* 7879 */         break;
/*      */       case -115:
/* 7881 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.DOUBLE);
/* 7882 */         pc++;
/* 7883 */         break;
/*      */       case -114:
/* 7885 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.INT);
/* 7886 */         pc++;
/* 7887 */         break;
/*      */       case -113:
/* 7889 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.LONG);
/* 7890 */         pc++;
/* 7891 */         break;
/*      */       case -112:
/* 7893 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.FLOAT);
/* 7894 */         pc++;
/* 7895 */         break;
/*      */       case -111:
/* 7897 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.BYTE);
/* 7898 */         pc++;
/* 7899 */         break;
/*      */       case -110:
/* 7901 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.CHAR);
/* 7902 */         pc++;
/* 7903 */         break;
/*      */       case -109:
/* 7905 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.SHORT);
/* 7906 */         pc++;
/* 7907 */         break;
/*      */       case -108:
/*      */       case -107:
/*      */       case -106:
/*      */       case -105:
/*      */       case -104:
/* 7913 */         frame.numberOfStackItems -= 2;
/* 7914 */         frame.addStackItem(TypeBinding.INT);
/* 7915 */         pc++;
/* 7916 */         break;
/*      */       case -103:
/*      */       case -102:
/*      */       case -101:
/*      */       case -100:
/*      */       case -99:
/*      */       case -98:
/* 7923 */         frame.numberOfStackItems -= 1;
/* 7924 */         pc += 3;
/* 7925 */         break;
/*      */       case -97:
/*      */       case -96:
/*      */       case -95:
/*      */       case -94:
/*      */       case -93:
/*      */       case -92:
/*      */       case -91:
/*      */       case -90:
/* 7934 */         frame.numberOfStackItems -= 2;
/* 7935 */         pc += 3;
/* 7936 */         break;
/*      */       case -89:
/* 7938 */         pc += 3;
/* 7939 */         break;
/*      */       case -86:
/* 7941 */         pc++;
/* 7942 */         while ((pc - codeOffset & 0x3) != 0) {
/* 7943 */           pc++;
/*      */         }
/* 7945 */         pc += 4;
/* 7946 */         int low = i4At(bytecodes, 0, pc);
/* 7947 */         pc += 4;
/* 7948 */         int high = i4At(bytecodes, 0, pc);
/* 7949 */         pc += 4;
/* 7950 */         int length = high - low + 1;
/* 7951 */         pc += length * 4;
/* 7952 */         frame.numberOfStackItems -= 1;
/* 7953 */         break;
/*      */       case -85:
/* 7955 */         pc++;
/* 7956 */         while ((pc - codeOffset & 0x3) != 0) {
/* 7957 */           pc++;
/*      */         }
/* 7959 */         pc += 4;
/* 7960 */         int npairs = (int)u4At(bytecodes, 0, pc);
/* 7961 */         pc += 4 + npairs * 8;
/* 7962 */         frame.numberOfStackItems -= 1;
/* 7963 */         break;
/*      */       case -84:
/*      */       case -83:
/*      */       case -82:
/*      */       case -81:
/*      */       case -80:
/* 7969 */         frame.numberOfStackItems -= 1;
/* 7970 */         pc++;
/* 7971 */         break;
/*      */       case -79:
/* 7973 */         pc++;
/* 7974 */         break;
/*      */       case -78:
/* 7976 */         int index = u2At(bytecodes, 1, pc);
/* 7977 */         int nameAndTypeIndex = u2At(poolContents, 3, 
/* 7978 */           constantPoolOffsets[index]);
/* 7979 */         int utf8index = u2At(poolContents, 3, 
/* 7980 */           constantPoolOffsets[nameAndTypeIndex]);
/* 7981 */         char[] descriptor = utf8At(poolContents, 
/* 7982 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 7983 */           poolContents, 1, 
/* 7984 */           constantPoolOffsets[utf8index]));
/* 7985 */         if (descriptor.length == 1)
/*      */         {
/* 7987 */           switch (descriptor[0]) {
/*      */           case 'Z':
/* 7989 */             frame.addStackItem(TypeBinding.BOOLEAN);
/* 7990 */             break;
/*      */           case 'B':
/* 7992 */             frame.addStackItem(TypeBinding.BYTE);
/* 7993 */             break;
/*      */           case 'C':
/* 7995 */             frame.addStackItem(TypeBinding.CHAR);
/* 7996 */             break;
/*      */           case 'D':
/* 7998 */             frame.addStackItem(TypeBinding.DOUBLE);
/* 7999 */             break;
/*      */           case 'F':
/* 8001 */             frame.addStackItem(TypeBinding.FLOAT);
/* 8002 */             break;
/*      */           case 'I':
/* 8004 */             frame.addStackItem(TypeBinding.INT);
/* 8005 */             break;
/*      */           case 'J':
/* 8007 */             frame.addStackItem(TypeBinding.LONG);
/* 8008 */             break;
/*      */           case 'S':
/* 8010 */             frame.addStackItem(TypeBinding.SHORT);
/*      */           default:
/* 8012 */             break;
/*      */           }
/* 8013 */         } else if (descriptor[0] == '[')
/* 8014 */           frame.addStackItem(new VerificationTypeInfo(0, descriptor));
/*      */         else {
/* 8016 */           frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(descriptor, 1, descriptor.length - 1)));
/*      */         }
/* 8018 */         pc += 3;
/* 8019 */         break;
/*      */       case -77:
/* 8021 */         frame.numberOfStackItems -= 1;
/* 8022 */         pc += 3;
/* 8023 */         break;
/*      */       case -76:
/* 8025 */         int index = u2At(bytecodes, 1, pc);
/* 8026 */         int nameAndTypeIndex = u2At(poolContents, 3, 
/* 8027 */           constantPoolOffsets[index]);
/* 8028 */         int utf8index = u2At(poolContents, 3, 
/* 8029 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8030 */         char[] descriptor = utf8At(poolContents, 
/* 8031 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8032 */           poolContents, 1, 
/* 8033 */           constantPoolOffsets[utf8index]));
/* 8034 */         frame.numberOfStackItems -= 1;
/* 8035 */         if (descriptor.length == 1)
/*      */         {
/* 8037 */           switch (descriptor[0]) {
/*      */           case 'Z':
/* 8039 */             frame.addStackItem(TypeBinding.BOOLEAN);
/* 8040 */             break;
/*      */           case 'B':
/* 8042 */             frame.addStackItem(TypeBinding.BYTE);
/* 8043 */             break;
/*      */           case 'C':
/* 8045 */             frame.addStackItem(TypeBinding.CHAR);
/* 8046 */             break;
/*      */           case 'D':
/* 8048 */             frame.addStackItem(TypeBinding.DOUBLE);
/* 8049 */             break;
/*      */           case 'F':
/* 8051 */             frame.addStackItem(TypeBinding.FLOAT);
/* 8052 */             break;
/*      */           case 'I':
/* 8054 */             frame.addStackItem(TypeBinding.INT);
/* 8055 */             break;
/*      */           case 'J':
/* 8057 */             frame.addStackItem(TypeBinding.LONG);
/* 8058 */             break;
/*      */           case 'S':
/* 8060 */             frame.addStackItem(TypeBinding.SHORT);
/*      */           default:
/* 8062 */             break;
/*      */           }
/* 8063 */         } else if (descriptor[0] == '[')
/* 8064 */           frame.addStackItem(new VerificationTypeInfo(0, descriptor));
/*      */         else {
/* 8066 */           frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(descriptor, 1, descriptor.length - 1)));
/*      */         }
/* 8068 */         pc += 3;
/* 8069 */         break;
/*      */       case -75:
/* 8071 */         frame.numberOfStackItems -= 2;
/* 8072 */         pc += 3;
/* 8073 */         break;
/*      */       case -74:
/* 8075 */         int index = u2At(bytecodes, 1, pc);
/* 8076 */         int nameAndTypeIndex = u2At(poolContents, 3, 
/* 8077 */           constantPoolOffsets[index]);
/* 8078 */         int utf8index = u2At(poolContents, 3, 
/* 8079 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8080 */         char[] descriptor = utf8At(poolContents, 
/* 8081 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8082 */           poolContents, 1, 
/* 8083 */           constantPoolOffsets[utf8index]));
/* 8084 */         utf8index = u2At(poolContents, 1, 
/* 8085 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8086 */         char[] name = utf8At(poolContents, 
/* 8087 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8088 */           poolContents, 1, 
/* 8089 */           constantPoolOffsets[utf8index]));
/* 8090 */         frame.numberOfStackItems -= getParametersCount(descriptor) + 1;
/* 8091 */         char[] returnType = getReturnType(descriptor);
/* 8092 */         if (returnType.length == 1)
/*      */         {
/* 8094 */           switch (returnType[0]) {
/*      */           case 'Z':
/* 8096 */             frame.addStackItem(TypeBinding.BOOLEAN);
/* 8097 */             break;
/*      */           case 'B':
/* 8099 */             frame.addStackItem(TypeBinding.BYTE);
/* 8100 */             break;
/*      */           case 'C':
/* 8102 */             frame.addStackItem(TypeBinding.CHAR);
/* 8103 */             break;
/*      */           case 'D':
/* 8105 */             frame.addStackItem(TypeBinding.DOUBLE);
/* 8106 */             break;
/*      */           case 'F':
/* 8108 */             frame.addStackItem(TypeBinding.FLOAT);
/* 8109 */             break;
/*      */           case 'I':
/* 8111 */             frame.addStackItem(TypeBinding.INT);
/* 8112 */             break;
/*      */           case 'J':
/* 8114 */             frame.addStackItem(TypeBinding.LONG);
/* 8115 */             break;
/*      */           case 'S':
/* 8117 */             frame.addStackItem(TypeBinding.SHORT);
/*      */           default:
/* 8119 */             break;
/*      */           }
/*      */         }
/* 8121 */         else if (returnType[0] == '[')
/* 8122 */           frame.addStackItem(new VerificationTypeInfo(0, returnType));
/*      */         else {
/* 8124 */           frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
/*      */         }
/*      */ 
/* 8127 */         pc += 3;
/* 8128 */         break;
/*      */       case -73:
/* 8130 */         int index = u2At(bytecodes, 1, pc);
/* 8131 */         int nameAndTypeIndex = u2At(poolContents, 3, 
/* 8132 */           constantPoolOffsets[index]);
/* 8133 */         int utf8index = u2At(poolContents, 3, 
/* 8134 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8135 */         char[] descriptor = utf8At(poolContents, 
/* 8136 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8137 */           poolContents, 1, 
/* 8138 */           constantPoolOffsets[utf8index]));
/* 8139 */         utf8index = u2At(poolContents, 1, 
/* 8140 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8141 */         char[] name = utf8At(poolContents, 
/* 8142 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8143 */           poolContents, 1, 
/* 8144 */           constantPoolOffsets[utf8index]));
/* 8145 */         frame.numberOfStackItems -= getParametersCount(descriptor);
/* 8146 */         if (CharOperation.equals(ConstantPool.Init, name))
/*      */         {
/* 8148 */           frame.stackItems[(frame.numberOfStackItems - 1)].tag = 7;
/*      */         }
/* 8150 */         frame.numberOfStackItems -= 1;
/* 8151 */         char[] returnType = getReturnType(descriptor);
/* 8152 */         if (returnType.length == 1)
/*      */         {
/* 8154 */           switch (returnType[0]) {
/*      */           case 'Z':
/* 8156 */             frame.addStackItem(TypeBinding.BOOLEAN);
/* 8157 */             break;
/*      */           case 'B':
/* 8159 */             frame.addStackItem(TypeBinding.BYTE);
/* 8160 */             break;
/*      */           case 'C':
/* 8162 */             frame.addStackItem(TypeBinding.CHAR);
/* 8163 */             break;
/*      */           case 'D':
/* 8165 */             frame.addStackItem(TypeBinding.DOUBLE);
/* 8166 */             break;
/*      */           case 'F':
/* 8168 */             frame.addStackItem(TypeBinding.FLOAT);
/* 8169 */             break;
/*      */           case 'I':
/* 8171 */             frame.addStackItem(TypeBinding.INT);
/* 8172 */             break;
/*      */           case 'J':
/* 8174 */             frame.addStackItem(TypeBinding.LONG);
/* 8175 */             break;
/*      */           case 'S':
/* 8177 */             frame.addStackItem(TypeBinding.SHORT);
/*      */           default:
/* 8179 */             break;
/*      */           }
/*      */         }
/* 8181 */         else if (returnType[0] == '[')
/* 8182 */           frame.addStackItem(new VerificationTypeInfo(0, returnType));
/*      */         else {
/* 8184 */           frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
/*      */         }
/*      */ 
/* 8187 */         pc += 3;
/* 8188 */         break;
/*      */       case -72:
/* 8190 */         int index = u2At(bytecodes, 1, pc);
/* 8191 */         int nameAndTypeIndex = u2At(poolContents, 3, 
/* 8192 */           constantPoolOffsets[index]);
/* 8193 */         int utf8index = u2At(poolContents, 3, 
/* 8194 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8195 */         char[] descriptor = utf8At(poolContents, 
/* 8196 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8197 */           poolContents, 1, 
/* 8198 */           constantPoolOffsets[utf8index]));
/* 8199 */         utf8index = u2At(poolContents, 1, 
/* 8200 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8201 */         char[] name = utf8At(poolContents, 
/* 8202 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8203 */           poolContents, 1, 
/* 8204 */           constantPoolOffsets[utf8index]));
/* 8205 */         frame.numberOfStackItems -= getParametersCount(descriptor);
/* 8206 */         char[] returnType = getReturnType(descriptor);
/* 8207 */         if (returnType.length == 1)
/*      */         {
/* 8209 */           switch (returnType[0]) {
/*      */           case 'Z':
/* 8211 */             frame.addStackItem(TypeBinding.BOOLEAN);
/* 8212 */             break;
/*      */           case 'B':
/* 8214 */             frame.addStackItem(TypeBinding.BYTE);
/* 8215 */             break;
/*      */           case 'C':
/* 8217 */             frame.addStackItem(TypeBinding.CHAR);
/* 8218 */             break;
/*      */           case 'D':
/* 8220 */             frame.addStackItem(TypeBinding.DOUBLE);
/* 8221 */             break;
/*      */           case 'F':
/* 8223 */             frame.addStackItem(TypeBinding.FLOAT);
/* 8224 */             break;
/*      */           case 'I':
/* 8226 */             frame.addStackItem(TypeBinding.INT);
/* 8227 */             break;
/*      */           case 'J':
/* 8229 */             frame.addStackItem(TypeBinding.LONG);
/* 8230 */             break;
/*      */           case 'S':
/* 8232 */             frame.addStackItem(TypeBinding.SHORT);
/*      */           default:
/* 8234 */             break;
/*      */           }
/*      */         }
/* 8236 */         else if (returnType[0] == '[')
/* 8237 */           frame.addStackItem(new VerificationTypeInfo(0, returnType));
/*      */         else {
/* 8239 */           frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
/*      */         }
/*      */ 
/* 8242 */         pc += 3;
/* 8243 */         break;
/*      */       case -71:
/* 8245 */         int index = u2At(bytecodes, 1, pc);
/* 8246 */         int nameAndTypeIndex = u2At(poolContents, 3, 
/* 8247 */           constantPoolOffsets[index]);
/* 8248 */         int utf8index = u2At(poolContents, 3, 
/* 8249 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8250 */         char[] descriptor = utf8At(poolContents, 
/* 8251 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8252 */           poolContents, 1, 
/* 8253 */           constantPoolOffsets[utf8index]));
/* 8254 */         utf8index = u2At(poolContents, 1, 
/* 8255 */           constantPoolOffsets[nameAndTypeIndex]);
/* 8256 */         char[] name = utf8At(poolContents, 
/* 8257 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8258 */           poolContents, 1, 
/* 8259 */           constantPoolOffsets[utf8index]));
/*      */ 
/* 8263 */         frame.numberOfStackItems -= getParametersCount(descriptor) + 1;
/* 8264 */         char[] returnType = getReturnType(descriptor);
/* 8265 */         if (returnType.length == 1)
/*      */         {
/* 8267 */           switch (returnType[0]) {
/*      */           case 'Z':
/* 8269 */             frame.addStackItem(TypeBinding.BOOLEAN);
/* 8270 */             break;
/*      */           case 'B':
/* 8272 */             frame.addStackItem(TypeBinding.BYTE);
/* 8273 */             break;
/*      */           case 'C':
/* 8275 */             frame.addStackItem(TypeBinding.CHAR);
/* 8276 */             break;
/*      */           case 'D':
/* 8278 */             frame.addStackItem(TypeBinding.DOUBLE);
/* 8279 */             break;
/*      */           case 'F':
/* 8281 */             frame.addStackItem(TypeBinding.FLOAT);
/* 8282 */             break;
/*      */           case 'I':
/* 8284 */             frame.addStackItem(TypeBinding.INT);
/* 8285 */             break;
/*      */           case 'J':
/* 8287 */             frame.addStackItem(TypeBinding.LONG);
/* 8288 */             break;
/*      */           case 'S':
/* 8290 */             frame.addStackItem(TypeBinding.SHORT);
/*      */           default:
/* 8292 */             break;
/*      */           }
/*      */         }
/* 8294 */         else if (returnType[0] == '[')
/* 8295 */           frame.addStackItem(new VerificationTypeInfo(0, returnType));
/*      */         else {
/* 8297 */           frame.addStackItem(new VerificationTypeInfo(0, CharOperation.subarray(returnType, 1, returnType.length - 1)));
/*      */         }
/*      */ 
/* 8300 */         pc += 5;
/* 8301 */         break;
/*      */       case -69:
/* 8303 */         int index = u2At(bytecodes, 1, pc);
/* 8304 */         int utf8index = u2At(poolContents, 1, 
/* 8305 */           constantPoolOffsets[index]);
/* 8306 */         char[] className = utf8At(poolContents, 
/* 8307 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8308 */           poolContents, 1, 
/* 8309 */           constantPoolOffsets[utf8index]));
/* 8310 */         VerificationTypeInfo verificationTypeInfo = new VerificationTypeInfo(0, 8, className);
/* 8311 */         verificationTypeInfo.offset = currentPC;
/* 8312 */         frame.addStackItem(verificationTypeInfo);
/* 8313 */         pc += 3;
/* 8314 */         break;
/*      */       case -68:
/* 8316 */         char[] constantPoolName = (char[])null;
/* 8317 */         switch (u1At(bytecodes, 1, pc)) {
/*      */         case 10:
/* 8319 */           constantPoolName = new char[] { '[', 'I' };
/* 8320 */           break;
/*      */         case 8:
/* 8322 */           constantPoolName = new char[] { '[', 'B' };
/* 8323 */           break;
/*      */         case 4:
/* 8325 */           constantPoolName = new char[] { '[', 'Z' };
/* 8326 */           break;
/*      */         case 9:
/* 8328 */           constantPoolName = new char[] { '[', 'S' };
/* 8329 */           break;
/*      */         case 5:
/* 8331 */           constantPoolName = new char[] { '[', 'C' };
/* 8332 */           break;
/*      */         case 11:
/* 8334 */           constantPoolName = new char[] { '[', 'J' };
/* 8335 */           break;
/*      */         case 6:
/* 8337 */           constantPoolName = new char[] { '[', 'F' };
/* 8338 */           break;
/*      */         case 7:
/* 8340 */           constantPoolName = new char[] { '[', 'D' };
/*      */         }
/* 8342 */         frame.stackItems
/* 8343 */           [(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(1, constantPoolName);
/* 8344 */         pc += 2;
/* 8345 */         break;
/*      */       case -67:
/* 8347 */         int index = u2At(bytecodes, 1, pc);
/* 8348 */         int utf8index = u2At(poolContents, 1, 
/* 8349 */           constantPoolOffsets[index]);
/* 8350 */         char[] className = utf8At(poolContents, 
/* 8351 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8352 */           poolContents, 1, 
/* 8353 */           constantPoolOffsets[utf8index]));
/* 8354 */         int classNameLength = className.length;
/*      */         char[] constantPoolName;
/* 8355 */         if (className[0] != '[')
/*      */         {
/*      */           char[] constantPoolName;
/* 8357 */           System.arraycopy(className, 0, constantPoolName = new char[classNameLength + 3], 2, classNameLength);
/* 8358 */           constantPoolName[0] = '[';
/* 8359 */           constantPoolName[1] = 'L';
/* 8360 */           constantPoolName[(classNameLength + 2)] = ';';
/*      */         }
/*      */         else {
/* 8363 */           System.arraycopy(className, 0, constantPoolName = new char[classNameLength + 1], 1, classNameLength);
/* 8364 */           constantPoolName[0] = '[';
/*      */         }
/* 8366 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(0, constantPoolName);
/* 8367 */         pc += 3;
/* 8368 */         break;
/*      */       case -66:
/* 8370 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.INT);
/* 8371 */         pc++;
/* 8372 */         break;
/*      */       case -65:
/* 8374 */         frame.numberOfStackItems -= 1;
/* 8375 */         pc++;
/* 8376 */         break;
/*      */       case -64:
/* 8378 */         int index = u2At(bytecodes, 1, pc);
/* 8379 */         int utf8index = u2At(poolContents, 1, 
/* 8380 */           constantPoolOffsets[index]);
/* 8381 */         char[] className = utf8At(poolContents, 
/* 8382 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8383 */           poolContents, 1, 
/* 8384 */           constantPoolOffsets[utf8index]));
/* 8385 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(0, className);
/* 8386 */         pc += 3;
/* 8387 */         break;
/*      */       case -63:
/* 8390 */         frame.stackItems[(frame.numberOfStackItems - 1)] = new VerificationTypeInfo(TypeBinding.INT);
/* 8391 */         pc += 3;
/* 8392 */         break;
/*      */       case -62:
/*      */       case -61:
/* 8395 */         frame.numberOfStackItems -= 1;
/* 8396 */         pc++;
/* 8397 */         break;
/*      */       case -60:
/* 8399 */         opcode = (byte)u1At(bytecodes, 1, pc);
/* 8400 */         if (opcode == -124)
/*      */         {
/* 8404 */           pc += 6;
/*      */         } else {
/* 8406 */           int index = u2At(bytecodes, 2, pc);
/*      */ 
/* 8408 */           switch (opcode) {
/*      */           case 21:
/* 8410 */             frame.addStackItem(TypeBinding.INT);
/* 8411 */             break;
/*      */           case 23:
/* 8413 */             frame.addStackItem(TypeBinding.FLOAT);
/* 8414 */             break;
/*      */           case 25:
/* 8416 */             VerificationTypeInfo localsN = frame.locals[index];
/* 8417 */             if (localsN == null) {
/* 8418 */               localsN = retrieveLocal(currentPC, index);
/*      */             }
/* 8420 */             frame.addStackItem(localsN);
/* 8421 */             break;
/*      */           case 22:
/* 8423 */             frame.addStackItem(TypeBinding.LONG);
/* 8424 */             break;
/*      */           case 24:
/* 8426 */             frame.addStackItem(TypeBinding.DOUBLE);
/* 8427 */             break;
/*      */           case 54:
/* 8429 */             frame.numberOfStackItems -= 1;
/* 8430 */             break;
/*      */           case 56:
/* 8432 */             frame.numberOfStackItems -= 1;
/* 8433 */             break;
/*      */           case 58:
/* 8435 */             frame.locals[index] = frame.stackItems[(frame.numberOfStackItems - 1)];
/* 8436 */             frame.numberOfStackItems -= 1;
/* 8437 */             break;
/*      */           case 55:
/* 8439 */             frame.numberOfStackItems -= 1;
/* 8440 */             break;
/*      */           case 57:
/* 8442 */             frame.numberOfStackItems -= 1;
/*      */           }
/*      */ 
/* 8445 */           pc += 4;
/*      */         }
/* 8447 */         break;
/*      */       case -59:
/* 8449 */         int index = u2At(bytecodes, 1, pc);
/* 8450 */         int utf8index = u2At(poolContents, 1, 
/* 8451 */           constantPoolOffsets[index]);
/* 8452 */         char[] className = utf8At(poolContents, 
/* 8453 */           constantPoolOffsets[utf8index] + 3, u2At(
/* 8454 */           poolContents, 1, 
/* 8455 */           constantPoolOffsets[utf8index]));
/* 8456 */         int dimensions = u1At(bytecodes, 3, pc);
/* 8457 */         frame.numberOfStackItems -= dimensions;
/* 8458 */         int classNameLength = className.length;
/* 8459 */         char[] constantPoolName = new char[classNameLength + dimensions];
/* 8460 */         for (int i = 0; i < dimensions; i++) {
/* 8461 */           constantPoolName[i] = '[';
/*      */         }
/* 8463 */         System.arraycopy(className, 0, constantPoolName, dimensions, classNameLength);
/* 8464 */         frame.addStackItem(new VerificationTypeInfo(0, constantPoolName));
/* 8465 */         pc += 4;
/* 8466 */         break;
/*      */       case -58:
/*      */       case -57:
/* 8469 */         frame.numberOfStackItems -= 1;
/* 8470 */         pc += 3;
/* 8471 */         break;
/*      */       case -56:
/* 8473 */         pc += 5;
/* 8474 */         break;
/*      */       case -88:
/*      */       case -87:
/*      */       case -70:
/*      */       case -55:
/*      */       case -54:
/*      */       case -53:
/*      */       case -52:
/*      */       case -51:
/*      */       case -50:
/*      */       case -49:
/*      */       case -48:
/*      */       case -47:
/*      */       case -46:
/*      */       case -45:
/*      */       case -44:
/*      */       case -43:
/*      */       case -42:
/*      */       case -41:
/*      */       case -40:
/*      */       case -39:
/*      */       case -38:
/*      */       case -37:
/*      */       case -36:
/*      */       case -35:
/*      */       case -34:
/*      */       case -33:
/*      */       case -32:
/*      */       case -31:
/*      */       case -30:
/*      */       case -29:
/*      */       case -28:
/*      */       case -27:
/*      */       case -26:
/*      */       case -25:
/*      */       case -24:
/*      */       case -23:
/*      */       case -22:
/*      */       case -21:
/*      */       case -20:
/*      */       case -19:
/*      */       case -18:
/*      */       case -17:
/*      */       case -16:
/*      */       case -15:
/*      */       case -14:
/*      */       case -13:
/*      */       case -12:
/*      */       case -11:
/*      */       case -10:
/*      */       case -9:
/*      */       case -8:
/*      */       case -7:
/*      */       case -6:
/*      */       case -5:
/*      */       case -4:
/*      */       case -3:
/*      */       case -2:
/*      */       case -1:
/*      */       default:
/* 8476 */         this.codeStream.methodDeclaration.scope.problemReporter().abortDueToInternalError(
/* 8477 */           Messages.bind(
/* 8478 */           Messages.abort_invalidOpcode, 
/* 8479 */           new Object[] { 
/* 8480 */           new Byte(opcode), 
/* 8481 */           new Integer(pc), 
/* 8482 */           new String(methodBinding.shortReadableName()) }), 
/* 8484 */           this.codeStream.methodDeclaration);
/*      */       }
/*      */     }
/* 8487 */     while (pc < codeLength + codeOffset);
/*      */   }
/*      */ 
/*      */   private final int u1At(byte[] reference, int relativeOffset, int structOffset)
/*      */   {
/* 8495 */     return reference[(relativeOffset + structOffset)] & 0xFF;
/*      */   }
/*      */ 
/*      */   private final int u2At(byte[] reference, int relativeOffset, int structOffset)
/*      */   {
/* 8500 */     int position = relativeOffset + structOffset;
/* 8501 */     return ((reference[(position++)] & 0xFF) << 8) + (
/* 8502 */       reference[position] & 0xFF);
/*      */   }
/*      */ 
/*      */   private final long u4At(byte[] reference, int relativeOffset, int structOffset)
/*      */   {
/* 8507 */     int position = relativeOffset + structOffset;
/* 8508 */     return ((reference[(position++)] & 0xFF) << 24) + (
/* 8509 */       (reference[(position++)] & 0xFF) << 16) + (
/* 8510 */       (reference[(position++)] & 0xFF) << 8) + (reference[position] & 0xFF);
/*      */   }
/*      */ 
/*      */   public char[] utf8At(byte[] reference, int absoluteOffset, int bytesAvailable)
/*      */   {
/* 8515 */     int length = bytesAvailable;
/* 8516 */     char[] outputBuf = new char[bytesAvailable];
/* 8517 */     int outputPos = 0;
/* 8518 */     int readOffset = absoluteOffset;
/*      */ 
/* 8520 */     while (length != 0) {
/* 8521 */       int x = reference[(readOffset++)] & 0xFF;
/* 8522 */       length--;
/* 8523 */       if ((0x80 & x) != 0) {
/* 8524 */         if ((x & 0x20) != 0) {
/* 8525 */           length -= 2;
/* 8526 */           x = (x & 0xF) << 12 | 
/* 8527 */             (reference[(readOffset++)] & 0x3F) << 6 | 
/* 8528 */             reference[(readOffset++)] & 0x3F;
/*      */         } else {
/* 8530 */           length--;
/* 8531 */           x = (x & 0x1F) << 6 | reference[(readOffset++)] & 0x3F;
/*      */         }
/*      */       }
/* 8534 */       outputBuf[(outputPos++)] = (char)x;
/*      */     }
/*      */ 
/* 8537 */     if (outputPos != bytesAvailable) {
/* 8538 */       System.arraycopy(outputBuf, 0, outputBuf = new char[outputPos], 
/* 8539 */         0, outputPos);
/*      */     }
/* 8541 */     return outputBuf;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ClassFile
 * JD-Core Version:    0.6.0
 */