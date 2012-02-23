/*      */ package org.eclipse.jdt.internal.compiler.problem;
/*      */ 
/*      */ import java.io.CharConversionException;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringWriter;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*      */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*      */ import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
/*      */ import org.eclipse.jdt.internal.compiler.IProblemFactory;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Assignment;
/*      */ import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*      */ import org.eclipse.jdt.internal.compiler.ast.BranchStatement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CastExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.FieldReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*      */ import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Literal;
/*      */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
/*      */ import org.eclipse.jdt.internal.compiler.ast.MessageSend;
/*      */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.NameReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Reference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ThisReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TryStatement;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
/*      */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*      */ import org.eclipse.jdt.internal.compiler.parser.JavadocTagConstants;
/*      */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*      */ import org.eclipse.jdt.internal.compiler.parser.RecoveryScanner;
/*      */ import org.eclipse.jdt.internal.compiler.parser.Scanner;
/*      */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*      */ 
/*      */ public class ProblemReporter extends ProblemHandler
/*      */ {
/*      */   public ReferenceContext referenceContext;
/*      */   private Scanner positionScanner;
/*      */   private static final byte FIELD_ACCESS = 4;
/*      */   private static final byte CONSTRUCTOR_ACCESS = 8;
/*      */   private static final byte METHOD_ACCESS = 12;
/*      */ 
/*      */   public ProblemReporter(IErrorHandlingPolicy policy, CompilerOptions options, IProblemFactory problemFactory)
/*      */   {
/*  124 */     super(policy, options, problemFactory);
/*      */   }
/*      */ 
/*      */   private static int getElaborationId(int leadProblemId, byte elaborationVariant) {
/*  128 */     return leadProblemId << 8 | elaborationVariant;
/*      */   }
/*      */   public static int getIrritant(int problemID) {
/*  131 */     switch (problemID)
/*      */     {
/*      */     case 16777381:
/*  134 */       return 8;
/*      */     case 268435844:
/*  137 */       return 1024;
/*      */     case 67108974:
/*  140 */       return 1;
/*      */     case 67109274:
/*  143 */       return 2;
/*      */     case 67109277:
/*      */     case 67109278:
/*  147 */       return 16384;
/*      */     case 16777221:
/*      */     case 33554505:
/*      */     case 67108967:
/*      */     case 67109276:
/*      */     case 134217861:
/*  154 */       return 4;
/*      */     case 536870973:
/*  157 */       return 16;
/*      */     case 536870974:
/*  160 */       return 32;
/*      */     case 536871063:
/*  163 */       return 64;
/*      */     case 33554622:
/*      */     case 33554623:
/*      */     case 67109056:
/*      */     case 67109057:
/*  169 */       return 128;
/*      */     case 536871173:
/*      */     case 536871177:
/*  173 */       return 256;
/*      */     case 536871352:
/*  176 */       return 512;
/*      */     case 536871353:
/*  179 */       return 536870928;
/*      */     case 570425420:
/*      */     case 603979893:
/*  183 */       return 2048;
/*      */     case 553648146:
/*      */     case 570425422:
/*      */     case 603979895:
/*  188 */       return 268435456;
/*      */     case 536871090:
/*  191 */       return 8192;
/*      */     case 553648135:
/*      */     case 570425421:
/*      */     case 603979894:
/*      */     case 603979910:
/*  197 */       return 32768;
/*      */     case 536871002:
/*      */     case 536871006:
/*      */     case 536871007:
/*      */     case 570425435:
/*  203 */       return 65536;
/*      */     case 570425436:
/*      */     case 570425437:
/*  207 */       return 131072;
/*      */     case 16777249:
/*      */     case 16777787:
/*      */     case 16777792:
/*      */     case 16777793:
/*  213 */       return 536871936;
/*      */     case 536871091:
/*  216 */       return 262144;
/*      */     case 536871092:
/*      */     case 553648316:
/*  220 */       return 524288;
/*      */     case 536871372:
/*  223 */       return 134217728;
/*      */     case 553648309:
/*      */     case 553648311:
/*  227 */       return 67108864;
/*      */     case 536871096:
/*  230 */       return 16777216;
/*      */     case 536871097:
/*      */     case 536871098:
/*  234 */       return 8388608;
/*      */     case 570425423:
/*  237 */       return 4194304;
/*      */     case 536871101:
/*  240 */       return 536870913;
/*      */     case 16777746:
/*      */     case 16777747:
/*      */     case 16777748:
/*      */     case 16777752:
/*      */     case 16777761:
/*      */     case 16777785:
/*      */     case 16777786:
/*      */     case 67109423:
/*      */     case 67109438:
/*  251 */       return 536870914;
/*      */     case 16777788:
/*  254 */       return 536936448;
/*      */     case 67109491:
/*  257 */       return 536872960;
/*      */     case 536871540:
/*      */     case 536871541:
/*      */     case 536871542:
/*  262 */       return 536879104;
/*      */     case 16777753:
/*  265 */       return 536870916;
/*      */     case 536871008:
/*  268 */       return 536870920;
/*      */     case 16777523:
/*  271 */       return 536870944;
/*      */     case 16777496:
/*  274 */       return 536887296;
/*      */     case 67109665:
/*      */     case 134218530:
/*  278 */       return 536870976;
/*      */     case 536871363:
/*  281 */       return 536871040;
/*      */     case 536871364:
/*  284 */       return 538968064;
/*      */     case 536871365:
/*      */     case 536871366:
/*      */     case 536871367:
/*      */     case 536871368:
/*      */     case 536871369:
/*      */     case 536871370:
/*  292 */       return 541065216;
/*      */     case 536871632:
/*      */     case 536871633:
/*  296 */       return 536871168;
/*      */     case 33555193:
/*  299 */       return 536875008;
/*      */     case 16777842:
/*  302 */       return 536871424;
/*      */     case 536871543:
/*  305 */       return 536903680;
/*      */     case 536871547:
/*  308 */       return 570425344;
/*      */     case 536871111:
/*  311 */       return 537001984;
/*      */     case -1610612274:
/*      */     case -1610612273:
/*      */     case -1610612272:
/*      */     case -1610612271:
/*      */     case -1610612270:
/*      */     case -1610612269:
/*      */     case -1610612268:
/*      */     case -1610612267:
/*      */     case -1610612266:
/*      */     case -1610612264:
/*      */     case -1610612263:
/*      */     case -1610612262:
/*      */     case -1610612260:
/*      */     case -1610612258:
/*      */     case -1610612257:
/*      */     case -1610612256:
/*      */     case -1610612255:
/*      */     case -1610612254:
/*      */     case -1610612253:
/*      */     case -1610612252:
/*      */     case -1610612251:
/*      */     case -1610612249:
/*      */     case -1610612248:
/*      */     case -1610612247:
/*      */     case -1610612246:
/*      */     case -1610612245:
/*      */     case -1610612244:
/*      */     case -1610612243:
/*      */     case -1610612242:
/*      */     case -1610612241:
/*      */     case -1610612240:
/*      */     case -1610612239:
/*      */     case -1610612238:
/*      */     case -1610612237:
/*      */     case -1610612236:
/*      */     case -1610612235:
/*      */     case -1610612234:
/*      */     case -1610612233:
/*      */     case -1610612232:
/*      */     case -1610612231:
/*      */     case -1610612230:
/*      */     case -1610612229:
/*      */     case -1610612228:
/*      */     case -1610612227:
/*      */     case -1610612226:
/*      */     case -1610612225:
/*      */     case -1610612224:
/*      */     case -1610612223:
/*      */     case -1610612221:
/*      */     case -1610612220:
/*      */     case -1610612219:
/*      */     case -1610612218:
/*      */     case -1610612217:
/*      */     case -1610611886:
/*      */     case -1610611885:
/*      */     case -1610611884:
/*      */     case -1610611883:
/*      */     case -1610611882:
/*      */     case -1610611881:
/*      */     case -1610611880:
/*      */     case -1610611879:
/*      */     case -1610611878:
/*      */     case -1610611877:
/*  376 */       return 33554432;
/*      */     case -1610612265:
/*      */     case -1610612261:
/*      */     case -1610612259:
/*  381 */       return 2097152;
/*      */     case -1610612250:
/*  384 */       return 1048576;
/*      */     case 536870971:
/*  387 */       return 537133056;
/*      */     case 536871106:
/*  390 */       return 537395200;
/*      */     case 67109280:
/*  393 */       return 537919488;
/*      */     case 67109443:
/*      */     case 67109524:
/*  397 */       return 553648128;
/*      */     case 16777547:
/*  400 */       return 603979776;
/*      */     case 536871123:
/*  403 */       return 671088640;
/*      */     case 67109281:
/*  406 */       return 805306368;
/*      */     case 16777548:
/*  409 */       return 1073741825;
/*      */     case 536871061:
/*  412 */       return 1073741826;
/*      */     }
/*  414 */     return 0;
/*      */   }
/*      */ 
/*      */   public static int getProblemCategory(int severity, int problemID)
/*      */   {
/*  425 */     if ((severity & 0x80) == 0)
/*      */     {
/*  427 */       int irritant = getIrritant(problemID);
/*  428 */       switch (irritant) {
/*      */       case 1:
/*      */       case 128:
/*      */       case 512:
/*      */       case 2048:
/*      */       case 4194304:
/*      */       case 134217728:
/*      */       case 268435456:
/*      */       case 536870916:
/*      */       case 536870928:
/*      */       case 536871168:
/*      */       case 536871424:
/*      */       case 536872960:
/*      */       case 536879104:
/*      */       case 537133056:
/*  443 */         return 80;
/*      */       case 8:
/*      */       case 64:
/*      */       case 8192:
/*      */       case 262144:
/*      */       case 524288:
/*      */       case 16777216:
/*      */       case 536870920:
/*      */       case 536870976:
/*      */       case 536871040:
/*      */       case 536875008:
/*      */       case 537395200:
/*      */       case 537919488:
/*      */       case 538968064:
/*      */       case 541065216:
/*      */       case 671088640:
/*      */       case 805306368:
/*      */       case 1073741825:
/*      */       case 1073741826:
/*  463 */         return 90;
/*      */       case 2:
/*      */       case 16384:
/*      */       case 65536:
/*      */       case 131072:
/*      */       case 536871936:
/*  470 */         return 100;
/*      */       case 16:
/*      */       case 32:
/*      */       case 1024:
/*      */       case 32768:
/*      */       case 8388608:
/*      */       case 67108864:
/*      */       case 536870913:
/*      */       case 536903680:
/*      */       case 537001984:
/*      */       case 570425344:
/*      */       case 603979776:
/*  483 */         return 120;
/*      */       case 4:
/*  486 */         return 110;
/*      */       case 256:
/*  489 */         return 140;
/*      */       case 4096:
/*  492 */         return 0;
/*      */       case 1048576:
/*      */       case 2097152:
/*      */       case 33554432:
/*      */       case 33554436:
/*  498 */         return 70;
/*      */       case 536870914:
/*      */       case 536936448:
/*  502 */         return 130;
/*      */       case 536870944:
/*      */       case 536887296:
/*  506 */         return 150;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  513 */     switch (problemID) {
/*      */     case 16777540:
/*      */     case 536871612:
/*  516 */       return 10;
/*      */     }
/*      */ 
/*  519 */     if ((problemID & 0x40000000) != 0)
/*  520 */       return 20;
/*  521 */     if ((problemID & 0x10000000) != 0)
/*  522 */       return 30;
/*  523 */     if ((problemID & 0x1000000) != 0)
/*  524 */       return 40;
/*  525 */     if ((problemID & 0xE000000) != 0) {
/*  526 */       return 50;
/*      */     }
/*  528 */     return 60;
/*      */   }
/*      */   public void abortDueToInternalError(String errorMessage) {
/*  531 */     abortDueToInternalError(errorMessage, null);
/*      */   }
/*      */   public void abortDueToInternalError(String errorMessage, ASTNode location) {
/*  534 */     String[] arguments = { errorMessage };
/*  535 */     handle(
/*  536 */       0, 
/*  537 */       arguments, 
/*  538 */       arguments, 
/*  539 */       159, 
/*  540 */       location == null ? 0 : location.sourceStart, 
/*  541 */       location == null ? 0 : location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void abstractMethodCannotBeOverridden(SourceTypeBinding type, MethodBinding concreteMethod) {
/*  545 */     handle(
/*  547 */       67109275, 
/*  548 */       new String[] { 
/*  549 */       new String(type.sourceName()), 
/*  550 */       new String(
/*  551 */       CharOperation.concat(
/*  552 */       concreteMethod.declaringClass.readableName(), 
/*  553 */       concreteMethod.readableName(), 
/*  554 */       '.')) }, 
/*  555 */       new String[] { 
/*  556 */       new String(type.sourceName()), 
/*  557 */       new String(
/*  558 */       CharOperation.concat(
/*  559 */       concreteMethod.declaringClass.shortReadableName(), 
/*  560 */       concreteMethod.shortReadableName(), 
/*  561 */       '.')) }, 
/*  562 */       type.sourceStart(), 
/*  563 */       type.sourceEnd());
/*      */   }
/*      */   public void abstractMethodInAbstractClass(SourceTypeBinding type, AbstractMethodDeclaration methodDecl) {
/*  566 */     if ((type.isEnum()) && (type.isLocalType())) {
/*  567 */       FieldBinding field = type.scope.enclosingMethodScope().initializedField;
/*  568 */       FieldDeclaration decl = field.sourceField();
/*  569 */       String[] arguments = { new String(decl.name), new String(methodDecl.selector) };
/*  570 */       handle(
/*  571 */         67109629, 
/*  572 */         arguments, 
/*  573 */         arguments, 
/*  574 */         methodDecl.sourceStart, 
/*  575 */         methodDecl.sourceEnd);
/*      */     } else {
/*  577 */       String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
/*  578 */       handle(
/*  579 */         67109227, 
/*  580 */         arguments, 
/*  581 */         arguments, 
/*  582 */         methodDecl.sourceStart, 
/*  583 */         methodDecl.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abstractMethodInConcreteClass(SourceTypeBinding type) {
/*  587 */     if ((type.isEnum()) && (type.isLocalType())) {
/*  588 */       FieldBinding field = type.scope.enclosingMethodScope().initializedField;
/*  589 */       FieldDeclaration decl = field.sourceField();
/*  590 */       String[] arguments = { new String(decl.name) };
/*  591 */       handle(
/*  592 */         67109628, 
/*  593 */         arguments, 
/*  594 */         arguments, 
/*  595 */         decl.sourceStart(), 
/*  596 */         decl.sourceEnd());
/*      */     } else {
/*  598 */       String[] arguments = { new String(type.sourceName()) };
/*  599 */       handle(
/*  600 */         16777549, 
/*  601 */         arguments, 
/*  602 */         arguments, 
/*  603 */         type.sourceStart(), 
/*  604 */         type.sourceEnd());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abstractMethodMustBeImplemented(SourceTypeBinding type, MethodBinding abstractMethod) {
/*  608 */     if ((type.isEnum()) && (type.isLocalType())) {
/*  609 */       FieldBinding field = type.scope.enclosingMethodScope().initializedField;
/*  610 */       FieldDeclaration decl = field.sourceField();
/*  611 */       handle(
/*  614 */         67109627, 
/*  615 */         new String[] { 
/*  616 */         new String(abstractMethod.selector), 
/*  617 */         typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, false), 
/*  618 */         new String(decl.name) }, 
/*  620 */         new String[] { 
/*  621 */         new String(abstractMethod.selector), 
/*  622 */         typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, true), 
/*  623 */         new String(decl.name) }, 
/*  625 */         decl.sourceStart(), 
/*  626 */         decl.sourceEnd());
/*      */     } else {
/*  628 */       handle(
/*  631 */         67109264, 
/*  632 */         new String[] { 
/*  633 */         new String(abstractMethod.selector), 
/*  634 */         typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, false), 
/*  635 */         new String(abstractMethod.declaringClass.readableName()), 
/*  636 */         new String(type.readableName()) }, 
/*  638 */         new String[] { 
/*  639 */         new String(abstractMethod.selector), 
/*  640 */         typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, true), 
/*  641 */         new String(abstractMethod.declaringClass.shortReadableName()), 
/*  642 */         new String(type.shortReadableName()) }, 
/*  644 */         type.sourceStart(), 
/*  645 */         type.sourceEnd());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abstractMethodMustBeImplemented(SourceTypeBinding type, MethodBinding abstractMethod, MethodBinding concreteMethod) {
/*  649 */     handle(
/*  652 */       67109282, 
/*  653 */       new String[] { 
/*  654 */       new String(abstractMethod.selector), 
/*  655 */       typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, false), 
/*  656 */       new String(abstractMethod.declaringClass.readableName()), 
/*  657 */       new String(type.readableName()), 
/*  658 */       new String(concreteMethod.selector), 
/*  659 */       typesAsString(concreteMethod.isVarargs(), concreteMethod.parameters, false), 
/*  660 */       new String(concreteMethod.declaringClass.readableName()) }, 
/*  662 */       new String[] { 
/*  663 */       new String(abstractMethod.selector), 
/*  664 */       typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, true), 
/*  665 */       new String(abstractMethod.declaringClass.shortReadableName()), 
/*  666 */       new String(type.shortReadableName()), 
/*  667 */       new String(concreteMethod.selector), 
/*  668 */       typesAsString(concreteMethod.isVarargs(), concreteMethod.parameters, true), 
/*  669 */       new String(concreteMethod.declaringClass.shortReadableName()) }, 
/*  671 */       type.sourceStart(), 
/*  672 */       type.sourceEnd());
/*      */   }
/*      */   public void abstractMethodNeedingNoBody(AbstractMethodDeclaration method) {
/*  675 */     handle(
/*  676 */       603979889, 
/*  677 */       NoArgument, 
/*  678 */       NoArgument, 
/*  679 */       method.sourceStart, 
/*  680 */       method.sourceEnd, 
/*  681 */       method, 
/*  682 */       method.compilationResult());
/*      */   }
/*      */   public void alreadyDefinedLabel(char[] labelName, ASTNode location) {
/*  685 */     String[] arguments = { new String(labelName) };
/*  686 */     handle(
/*  687 */       536871083, 
/*  688 */       arguments, 
/*  689 */       arguments, 
/*  690 */       location.sourceStart, 
/*  691 */       location.sourceEnd);
/*      */   }
/*      */   public void annotationCannotOverrideMethod(MethodBinding overrideMethod, MethodBinding inheritedMethod) {
/*  694 */     ASTNode location = overrideMethod.sourceMethod();
/*  695 */     handle(
/*  696 */       67109480, 
/*  697 */       new String[] { 
/*  698 */       new String(overrideMethod.declaringClass.readableName()), 
/*  699 */       new String(inheritedMethod.declaringClass.readableName()), 
/*  700 */       new String(inheritedMethod.selector), 
/*  701 */       typesAsString(inheritedMethod.isVarargs(), inheritedMethod.parameters, false) }, 
/*  702 */       new String[] { 
/*  703 */       new String(overrideMethod.declaringClass.shortReadableName()), 
/*  704 */       new String(inheritedMethod.declaringClass.shortReadableName()), 
/*  705 */       new String(inheritedMethod.selector), 
/*  706 */       typesAsString(inheritedMethod.isVarargs(), inheritedMethod.parameters, true) }, 
/*  707 */       location.sourceStart, 
/*  708 */       location.sourceEnd);
/*      */   }
/*      */   public void annotationCircularity(TypeBinding sourceType, TypeBinding otherType, TypeReference reference) {
/*  711 */     if (sourceType == otherType)
/*  712 */       handle(
/*  713 */         16777822, 
/*  714 */         new String[] { new String(sourceType.readableName()) }, 
/*  715 */         new String[] { new String(sourceType.shortReadableName()) }, 
/*  716 */         reference.sourceStart, 
/*  717 */         reference.sourceEnd);
/*      */     else
/*  719 */       handle(
/*  720 */         16777823, 
/*  721 */         new String[] { new String(sourceType.readableName()), new String(otherType.readableName()) }, 
/*  722 */         new String[] { new String(sourceType.shortReadableName()), new String(otherType.shortReadableName()) }, 
/*  723 */         reference.sourceStart, 
/*  724 */         reference.sourceEnd); 
/*      */   }
/*      */ 
/*      */   public void annotationMembersCannotHaveParameters(AnnotationMethodDeclaration annotationMethodDeclaration) {
/*  727 */     handle(
/*  728 */       1610613353, 
/*  729 */       NoArgument, 
/*  730 */       NoArgument, 
/*  731 */       annotationMethodDeclaration.sourceStart, 
/*  732 */       annotationMethodDeclaration.sourceEnd);
/*      */   }
/*      */   public void annotationMembersCannotHaveTypeParameters(AnnotationMethodDeclaration annotationMethodDeclaration) {
/*  735 */     handle(
/*  736 */       1610613354, 
/*  737 */       NoArgument, 
/*  738 */       NoArgument, 
/*  739 */       annotationMethodDeclaration.sourceStart, 
/*  740 */       annotationMethodDeclaration.sourceEnd);
/*      */   }
/*      */   public void annotationTypeDeclarationCannotHaveConstructor(ConstructorDeclaration constructorDeclaration) {
/*  743 */     handle(
/*  744 */       1610613360, 
/*  745 */       NoArgument, 
/*  746 */       NoArgument, 
/*  747 */       constructorDeclaration.sourceStart, 
/*  748 */       constructorDeclaration.sourceEnd);
/*      */   }
/*      */   public void annotationTypeDeclarationCannotHaveSuperclass(TypeDeclaration typeDeclaration) {
/*  751 */     handle(
/*  752 */       1610613355, 
/*  753 */       NoArgument, 
/*  754 */       NoArgument, 
/*  755 */       typeDeclaration.sourceStart, 
/*  756 */       typeDeclaration.sourceEnd);
/*      */   }
/*      */   public void annotationTypeDeclarationCannotHaveSuperinterfaces(TypeDeclaration typeDeclaration) {
/*  759 */     handle(
/*  760 */       1610613356, 
/*  761 */       NoArgument, 
/*  762 */       NoArgument, 
/*  763 */       typeDeclaration.sourceStart, 
/*  764 */       typeDeclaration.sourceEnd);
/*      */   }
/*      */   public void annotationTypeUsedAsSuperinterface(SourceTypeBinding type, TypeReference superInterfaceRef, ReferenceBinding superType) {
/*  767 */     handle(
/*  768 */       16777842, 
/*  769 */       new String[] { new String(superType.readableName()), new String(type.sourceName()) }, 
/*  770 */       new String[] { new String(superType.shortReadableName()), new String(type.sourceName()) }, 
/*  771 */       superInterfaceRef.sourceStart, 
/*  772 */       superInterfaceRef.sourceEnd);
/*      */   }
/*      */   public void annotationValueMustBeAnnotation(TypeBinding annotationType, char[] name, Expression value, TypeBinding expectedType) {
/*  775 */     String str = new String(name);
/*  776 */     handle(
/*  777 */       536871537, 
/*  778 */       new String[] { new String(annotationType.readableName()), str, new String(expectedType.readableName()) }, 
/*  779 */       new String[] { new String(annotationType.shortReadableName()), str, new String(expectedType.readableName()) }, 
/*  780 */       value.sourceStart, 
/*  781 */       value.sourceEnd);
/*      */   }
/*      */   public void annotationValueMustBeArrayInitializer(TypeBinding annotationType, char[] name, Expression value) {
/*  784 */     String str = new String(name);
/*  785 */     handle(
/*  786 */       536871544, 
/*  787 */       new String[] { new String(annotationType.readableName()), str }, 
/*  788 */       new String[] { new String(annotationType.shortReadableName()), str }, 
/*  789 */       value.sourceStart, 
/*  790 */       value.sourceEnd);
/*      */   }
/*      */   public void annotationValueMustBeClassLiteral(TypeBinding annotationType, char[] name, Expression value) {
/*  793 */     String str = new String(name);
/*  794 */     handle(
/*  795 */       536871524, 
/*  796 */       new String[] { new String(annotationType.readableName()), str }, 
/*  797 */       new String[] { new String(annotationType.shortReadableName()), str }, 
/*  798 */       value.sourceStart, 
/*  799 */       value.sourceEnd);
/*      */   }
/*      */   public void annotationValueMustBeConstant(TypeBinding annotationType, char[] name, Expression value, boolean isEnum) {
/*  802 */     String str = new String(name);
/*  803 */     if (isEnum)
/*  804 */       handle(
/*  805 */         536871545, 
/*  806 */         new String[] { new String(annotationType.readableName()), str }, 
/*  807 */         new String[] { new String(annotationType.shortReadableName()), str }, 
/*  808 */         value.sourceStart, 
/*  809 */         value.sourceEnd);
/*      */     else
/*  811 */       handle(
/*  812 */         536871525, 
/*  813 */         new String[] { new String(annotationType.readableName()), str }, 
/*  814 */         new String[] { new String(annotationType.shortReadableName()), str }, 
/*  815 */         value.sourceStart, 
/*  816 */         value.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void anonymousClassCannotExtendFinalClass(TypeReference reference, TypeBinding type) {
/*  820 */     handle(
/*  821 */       16777245, 
/*  822 */       new String[] { new String(type.readableName()) }, 
/*  823 */       new String[] { new String(type.shortReadableName()) }, 
/*  824 */       reference.sourceStart, 
/*  825 */       reference.sourceEnd);
/*      */   }
/*      */   public void argumentTypeCannotBeVoid(SourceTypeBinding type, AbstractMethodDeclaration methodDecl, Argument arg) {
/*  828 */     String[] arguments = { new String(methodDecl.selector), new String(arg.name) };
/*  829 */     handle(
/*  830 */       67109228, 
/*  831 */       arguments, 
/*  832 */       arguments, 
/*  833 */       methodDecl.sourceStart, 
/*  834 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void argumentTypeCannotBeVoidArray(Argument arg) {
/*  837 */     handle(
/*  838 */       536870966, 
/*  839 */       NoArgument, 
/*  840 */       NoArgument, 
/*  841 */       arg.type.sourceStart, 
/*  842 */       arg.type.sourceEnd);
/*      */   }
/*      */   public void arrayConstantsOnlyInArrayInitializers(int sourceStart, int sourceEnd) {
/*  845 */     handle(
/*  846 */       1610612944, 
/*  847 */       NoArgument, 
/*  848 */       NoArgument, 
/*  849 */       sourceStart, 
/*  850 */       sourceEnd);
/*      */   }
/*      */   public void assignmentHasNoEffect(AbstractVariableDeclaration location, char[] name) {
/*  853 */     int severity = computeSeverity(536871090);
/*  854 */     if (severity == -1) return;
/*  855 */     String[] arguments = { new String(name) };
/*  856 */     int start = location.sourceStart;
/*  857 */     int end = location.sourceEnd;
/*  858 */     if (location.initialization != null) {
/*  859 */       end = location.initialization.sourceEnd;
/*      */     }
/*  861 */     handle(
/*  862 */       536871090, 
/*  863 */       arguments, 
/*  864 */       arguments, 
/*  865 */       severity, 
/*  866 */       start, 
/*  867 */       end);
/*      */   }
/*      */   public void assignmentHasNoEffect(Assignment location, char[] name) {
/*  870 */     int severity = computeSeverity(536871090);
/*  871 */     if (severity == -1) return;
/*  872 */     String[] arguments = { new String(name) };
/*  873 */     handle(
/*  874 */       536871090, 
/*  875 */       arguments, 
/*  876 */       arguments, 
/*  877 */       severity, 
/*  878 */       location.sourceStart, 
/*  879 */       location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void attemptToReturnNonVoidExpression(ReturnStatement returnStatement, TypeBinding expectedType) {
/*  883 */     handle(
/*  884 */       67108969, 
/*  885 */       new String[] { new String(expectedType.readableName()) }, 
/*  886 */       new String[] { new String(expectedType.shortReadableName()) }, 
/*  887 */       returnStatement.sourceStart, 
/*  888 */       returnStatement.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void attemptToReturnVoidValue(ReturnStatement returnStatement)
/*      */   {
/*  893 */     handle(
/*  894 */       67108970, 
/*  895 */       NoArgument, 
/*  896 */       NoArgument, 
/*  897 */       returnStatement.sourceStart, 
/*  898 */       returnStatement.sourceEnd);
/*      */   }
/*      */   public void autoboxing(Expression expression, TypeBinding originalType, TypeBinding convertedType) {
/*  901 */     if (this.options.getSeverity(536871168) == -1) return;
/*  902 */     handle(
/*  903 */       originalType.isBaseType() ? 536871632 : 536871633, 
/*  904 */       new String[] { new String(originalType.readableName()), new String(convertedType.readableName()) }, 
/*  905 */       new String[] { new String(originalType.shortReadableName()), new String(convertedType.shortReadableName()) }, 
/*  906 */       expression.sourceStart, 
/*  907 */       expression.sourceEnd);
/*      */   }
/*      */   public void boundCannotBeArray(ASTNode location, TypeBinding type) {
/*  910 */     handle(
/*  911 */       16777784, 
/*  912 */       new String[] { new String(type.readableName()) }, 
/*  913 */       new String[] { new String(type.shortReadableName()) }, 
/*  914 */       location.sourceStart, 
/*  915 */       location.sourceEnd);
/*      */   }
/*      */   public void boundMustBeAnInterface(ASTNode location, TypeBinding type) {
/*  918 */     handle(
/*  919 */       16777745, 
/*  920 */       new String[] { new String(type.readableName()) }, 
/*  921 */       new String[] { new String(type.shortReadableName()) }, 
/*  922 */       location.sourceStart, 
/*  923 */       location.sourceEnd);
/*      */   }
/*      */   public void bytecodeExceeds64KLimit(AbstractMethodDeclaration location) {
/*  926 */     MethodBinding method = location.binding;
/*  927 */     if (location.isConstructor())
/*  928 */       handle(
/*  929 */         536870981, 
/*  930 */         new String[] { new String(location.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/*  931 */         new String[] { new String(location.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/*  932 */         159, 
/*  933 */         location.sourceStart, 
/*  934 */         location.sourceEnd);
/*      */     else
/*  936 */       handle(
/*  937 */         536870975, 
/*  938 */         new String[] { new String(location.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/*  939 */         new String[] { new String(location.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/*  940 */         159, 
/*  941 */         location.sourceStart, 
/*  942 */         location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void bytecodeExceeds64KLimit(TypeDeclaration location) {
/*  946 */     handle(
/*  947 */       536870976, 
/*  948 */       NoArgument, 
/*  949 */       NoArgument, 
/*  950 */       159, 
/*  951 */       location.sourceStart, 
/*  952 */       location.sourceEnd);
/*      */   }
/*      */   public void cannotAllocateVoidArray(Expression expression) {
/*  955 */     handle(
/*  956 */       536870966, 
/*  957 */       NoArgument, 
/*  958 */       NoArgument, 
/*  959 */       expression.sourceStart, 
/*  960 */       expression.sourceEnd);
/*      */   }
/*      */   public void cannotAssignToFinalField(FieldBinding field, ASTNode location) {
/*  963 */     handle(
/*  964 */       33554512, 
/*  965 */       new String[] { 
/*  966 */       field.declaringClass == null ? "array" : new String(field.declaringClass.readableName()), 
/*  967 */       new String(field.readableName()) }, 
/*  968 */       new String[] { 
/*  969 */       field.declaringClass == null ? "array" : new String(field.declaringClass.shortReadableName()), 
/*  970 */       new String(field.shortReadableName()) }, 
/*  971 */       nodeSourceStart(field, location), 
/*  972 */       nodeSourceEnd(field, location));
/*      */   }
/*      */   public void cannotAssignToFinalLocal(LocalVariableBinding local, ASTNode location) {
/*  975 */     String[] arguments = { new String(local.readableName()) };
/*  976 */     handle(
/*  977 */       536870970, 
/*  978 */       arguments, 
/*  979 */       arguments, 
/*  980 */       nodeSourceStart(local, location), 
/*  981 */       nodeSourceEnd(local, location));
/*      */   }
/*      */   public void cannotAssignToFinalOuterLocal(LocalVariableBinding local, ASTNode location) {
/*  984 */     String[] arguments = { new String(local.readableName()) };
/*  985 */     handle(
/*  986 */       536870972, 
/*  987 */       arguments, 
/*  988 */       arguments, 
/*  989 */       nodeSourceStart(local, location), 
/*  990 */       nodeSourceEnd(local, location));
/*      */   }
/*      */   public void cannotDefineDimensionsAndInitializer(ArrayAllocationExpression expresssion) {
/*  993 */     handle(
/*  994 */       536871070, 
/*  995 */       NoArgument, 
/*  996 */       NoArgument, 
/*  997 */       expresssion.sourceStart, 
/*  998 */       expresssion.sourceEnd);
/*      */   }
/*      */   public void cannotDireclyInvokeAbstractMethod(MessageSend messageSend, MethodBinding method) {
/* 1001 */     handle(
/* 1002 */       67108968, 
/* 1003 */       new String[] { new String(method.declaringClass.readableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 1004 */       new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 1005 */       messageSend.sourceStart, 
/* 1006 */       messageSend.sourceEnd);
/*      */   }
/*      */   public void cannotExtendEnum(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
/* 1009 */     String name = new String(type.sourceName());
/* 1010 */     String superTypeFullName = new String(superTypeBinding.readableName());
/* 1011 */     String superTypeShortName = new String(superTypeBinding.shortReadableName());
/* 1012 */     if (superTypeShortName.equals(name)) superTypeShortName = superTypeFullName;
/* 1013 */     handle(
/* 1014 */       16777972, 
/* 1015 */       new String[] { superTypeFullName, name }, 
/* 1016 */       new String[] { superTypeShortName, name }, 
/* 1017 */       superclass.sourceStart, 
/* 1018 */       superclass.sourceEnd);
/*      */   }
/*      */   public void cannotImportPackage(ImportReference importRef) {
/* 1021 */     String[] arguments = { CharOperation.toString(importRef.tokens) };
/* 1022 */     handle(
/* 1023 */       268435843, 
/* 1024 */       arguments, 
/* 1025 */       arguments, 
/* 1026 */       importRef.sourceStart, 
/* 1027 */       importRef.sourceEnd);
/*      */   }
/*      */   public void cannotInstantiate(TypeReference typeRef, TypeBinding type) {
/* 1030 */     handle(
/* 1031 */       16777373, 
/* 1032 */       new String[] { new String(type.readableName()) }, 
/* 1033 */       new String[] { new String(type.shortReadableName()) }, 
/* 1034 */       typeRef.sourceStart, 
/* 1035 */       typeRef.sourceEnd);
/*      */   }
/*      */   public void cannotInvokeSuperConstructorInEnum(ExplicitConstructorCall constructorCall, MethodBinding enumConstructor) {
/* 1038 */     handle(
/* 1039 */       67109621, 
/* 1040 */       new String[] { 
/* 1041 */       new String(enumConstructor.declaringClass.sourceName()), 
/* 1042 */       typesAsString(enumConstructor.isVarargs(), enumConstructor.parameters, false) }, 
/* 1044 */       new String[] { 
/* 1045 */       new String(enumConstructor.declaringClass.sourceName()), 
/* 1046 */       typesAsString(enumConstructor.isVarargs(), enumConstructor.parameters, true) }, 
/* 1048 */       constructorCall.sourceStart, 
/* 1049 */       constructorCall.sourceEnd);
/*      */   }
/*      */   public void cannotReadSource(CompilationUnitDeclaration unit, AbortCompilationUnit abortException, boolean verbose) {
/* 1052 */     String fileName = new String(unit.compilationResult.fileName);
/* 1053 */     if ((abortException.exception instanceof CharConversionException))
/*      */     {
/* 1055 */       String encoding = abortException.encoding;
/* 1056 */       if (encoding == null) {
/* 1057 */         encoding = System.getProperty("file.encoding");
/*      */       }
/* 1059 */       String[] arguments = { fileName, encoding };
/* 1060 */       handle(
/* 1061 */         536871613, 
/* 1062 */         arguments, 
/* 1063 */         arguments, 
/* 1064 */         0, 
/* 1065 */         0);
/* 1066 */       return;
/*      */     }
/* 1068 */     StringWriter stringWriter = new StringWriter();
/* 1069 */     PrintWriter writer = new PrintWriter(stringWriter);
/* 1070 */     if (verbose) {
/* 1071 */       abortException.exception.printStackTrace(writer);
/*      */     } else {
/* 1073 */       writer.print(abortException.exception.getClass().getName());
/* 1074 */       writer.print(':');
/* 1075 */       writer.print(abortException.exception.getMessage());
/*      */     }
/* 1077 */     String exceptionTrace = stringWriter.toString();
/* 1078 */     String[] arguments = { fileName, exceptionTrace };
/* 1079 */     handle(
/* 1080 */       536871614, 
/* 1081 */       arguments, 
/* 1082 */       arguments, 
/* 1083 */       0, 
/* 1084 */       0);
/*      */   }
/*      */   public void cannotReferToNonFinalOuterLocal(LocalVariableBinding local, ASTNode location) {
/* 1087 */     String[] arguments = { new String(local.readableName()) };
/* 1088 */     handle(
/* 1089 */       536870937, 
/* 1090 */       arguments, 
/* 1091 */       arguments, 
/* 1092 */       nodeSourceStart(local, location), 
/* 1093 */       nodeSourceEnd(local, location));
/*      */   }
/*      */   public void cannotReturnInInitializer(ASTNode location) {
/* 1096 */     handle(
/* 1097 */       536871074, 
/* 1098 */       NoArgument, 
/* 1099 */       NoArgument, 
/* 1100 */       location.sourceStart, 
/* 1101 */       location.sourceEnd);
/*      */   }
/*      */   public void cannotThrowNull(ASTNode expression) {
/* 1104 */     handle(
/* 1105 */       536871089, 
/* 1106 */       NoArgument, 
/* 1107 */       NoArgument, 
/* 1108 */       expression.sourceStart, 
/* 1109 */       expression.sourceEnd);
/*      */   }
/*      */   public void cannotThrowType(ASTNode exception, TypeBinding expectedType) {
/* 1112 */     handle(
/* 1113 */       16777536, 
/* 1114 */       new String[] { new String(expectedType.readableName()) }, 
/* 1115 */       new String[] { new String(expectedType.shortReadableName()) }, 
/* 1116 */       exception.sourceStart, 
/* 1117 */       exception.sourceEnd);
/*      */   }
/*      */   public void cannotUseQualifiedEnumConstantInCaseLabel(Reference location, FieldBinding field) {
/* 1120 */     handle(
/* 1121 */       33555187, 
/* 1122 */       new String[] { String.valueOf(field.declaringClass.readableName()), String.valueOf(field.name) }, 
/* 1123 */       new String[] { String.valueOf(field.declaringClass.shortReadableName()), String.valueOf(field.name) }, 
/* 1124 */       location.sourceStart(), 
/* 1125 */       location.sourceEnd());
/*      */   }
/*      */   public void cannotUseSuperInCodeSnippet(int start, int end) {
/* 1128 */     handle(
/* 1129 */       536871334, 
/* 1130 */       NoArgument, 
/* 1131 */       NoArgument, 
/* 1132 */       159, 
/* 1133 */       start, 
/* 1134 */       end);
/*      */   }
/*      */   public void cannotUseSuperInJavaLangObject(ASTNode reference) {
/* 1137 */     handle(
/* 1138 */       16777217, 
/* 1139 */       NoArgument, 
/* 1140 */       NoArgument, 
/* 1141 */       reference.sourceStart, 
/* 1142 */       reference.sourceEnd);
/*      */   }
/*      */   public void caseExpressionMustBeConstant(Expression expression) {
/* 1145 */     handle(
/* 1146 */       536871065, 
/* 1147 */       NoArgument, 
/* 1148 */       NoArgument, 
/* 1149 */       expression.sourceStart, 
/* 1150 */       expression.sourceEnd);
/*      */   }
/*      */   public void classExtendFinalClass(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
/* 1153 */     String name = new String(type.sourceName());
/* 1154 */     String superTypeFullName = new String(superTypeBinding.readableName());
/* 1155 */     String superTypeShortName = new String(superTypeBinding.shortReadableName());
/* 1156 */     if (superTypeShortName.equals(name)) superTypeShortName = superTypeFullName;
/* 1157 */     handle(
/* 1158 */       16777529, 
/* 1159 */       new String[] { superTypeFullName, name }, 
/* 1160 */       new String[] { superTypeShortName, name }, 
/* 1161 */       superclass.sourceStart, 
/* 1162 */       superclass.sourceEnd);
/*      */   }
/*      */   public void codeSnippetMissingClass(String missing, int start, int end) {
/* 1165 */     String[] arguments = { missing };
/* 1166 */     handle(
/* 1167 */       536871332, 
/* 1168 */       arguments, 
/* 1169 */       arguments, 
/* 1170 */       159, 
/* 1171 */       start, 
/* 1172 */       end);
/*      */   }
/*      */   public void codeSnippetMissingMethod(String className, String missingMethod, String argumentTypes, int start, int end) {
/* 1175 */     String[] arguments = { className, missingMethod, argumentTypes };
/* 1176 */     handle(
/* 1177 */       536871333, 
/* 1178 */       arguments, 
/* 1179 */       arguments, 
/* 1180 */       159, 
/* 1181 */       start, 
/* 1182 */       end);
/*      */   }
/*      */   public void comparingIdenticalExpressions(Expression comparison) {
/* 1185 */     int severity = computeSeverity(536871123);
/* 1186 */     if (severity == -1) return;
/* 1187 */     handle(
/* 1188 */       536871123, 
/* 1189 */       NoArgument, 
/* 1190 */       NoArgument, 
/* 1191 */       severity, 
/* 1192 */       comparison.sourceStart, 
/* 1193 */       comparison.sourceEnd);
/*      */   }
/*      */ 
/*      */   public int computeSeverity(int problemID)
/*      */   {
/* 1204 */     switch (problemID) {
/*      */     case 536871362:
/* 1206 */       return 0;
/*      */     case 67109667:
/* 1208 */       return 0;
/*      */     case 16777538:
/* 1210 */       return 0;
/*      */     case -1610612270:
/*      */     case -1610612268:
/*      */     case -1610612264:
/*      */     case -1610612263:
/*      */     case -1610612262:
/*      */     case -1610612258:
/*      */     case -1610612256:
/*      */     case -1610612255:
/*      */     case -1610612254:
/*      */     case -1610612248:
/*      */     case -1610612246:
/*      */     case -1610612244:
/*      */     case -1610612242:
/*      */     case -1610612240:
/*      */     case -1610612238:
/*      */     case -1610612236:
/*      */     case -1610612235:
/*      */     case -1610612234:
/*      */     case -1610612233:
/*      */     case -1610612231:
/*      */     case -1610612229:
/*      */     case -1610612228:
/*      */     case -1610612227:
/*      */     case -1610612226:
/*      */     case -1610612225:
/*      */     case -1610612219:
/*      */     case -1610611886:
/*      */     case -1610611885:
/*      */     case -1610611884:
/*      */     case -1610611883:
/*      */     case -1610611882:
/*      */     case -1610611881:
/*      */     case -1610611880:
/*      */     case -1610611879:
/*      */     case -1610611878:
/*      */     case -1610611877:
/* 1251 */       if (this.options.reportInvalidJavadocTags) break;
/* 1252 */       return -1;
/*      */     case -1610612245:
/*      */     case -1610612241:
/*      */     case -1610612237:
/*      */     case -1610612230:
/* 1262 */       if ((this.options.reportInvalidJavadocTags) && (this.options.reportInvalidJavadocTagsDeprecatedRef)) break;
/* 1263 */       return -1;
/*      */     case -1610612271:
/*      */     case -1610612247:
/*      */     case -1610612243:
/*      */     case -1610612239:
/*      */     case -1610612232:
/* 1274 */       if ((this.options.reportInvalidJavadocTags) && (this.options.reportInvalidJavadocTagsNotVisibleRef)) break;
/* 1275 */       return -1;
/*      */     case -1610612220:
/* 1282 */       if (!"no_tag".equals(this.options.reportMissingJavadocTagDescription)) break;
/* 1283 */       return -1;
/*      */     case -1610612273:
/* 1287 */       if ("all_standard_tags".equals(this.options.reportMissingJavadocTagDescription)) break;
/* 1288 */       return -1;
/*      */     }
/*      */ 
/* 1292 */     int irritant = getIrritant(problemID);
/* 1293 */     if (irritant != 0) {
/* 1294 */       if (((problemID & 0x80000000) != 0) && (!this.options.docCommentSupport))
/* 1295 */         return -1;
/* 1296 */       return this.options.getSeverity(irritant);
/*      */     }
/* 1298 */     return 129;
/*      */   }
/*      */   public void conditionalArgumentsIncompatibleTypes(ConditionalExpression expression, TypeBinding trueType, TypeBinding falseType) {
/* 1301 */     handle(
/* 1302 */       16777232, 
/* 1303 */       new String[] { new String(trueType.readableName()), new String(falseType.readableName()) }, 
/* 1304 */       new String[] { new String(trueType.sourceName()), new String(falseType.sourceName()) }, 
/* 1305 */       expression.sourceStart, 
/* 1306 */       expression.sourceEnd);
/*      */   }
/*      */   public void conflictingImport(ImportReference importRef) {
/* 1309 */     String[] arguments = { CharOperation.toString(importRef.tokens) };
/* 1310 */     handle(
/* 1311 */       268435841, 
/* 1312 */       arguments, 
/* 1313 */       arguments, 
/* 1314 */       importRef.sourceStart, 
/* 1315 */       importRef.sourceEnd);
/*      */   }
/*      */   public void constantOutOfRange(Literal literal, TypeBinding literalType) {
/* 1318 */     String[] arguments = { new String(literalType.readableName()), new String(literal.source()) };
/* 1319 */     handle(
/* 1320 */       536871066, 
/* 1321 */       arguments, 
/* 1322 */       arguments, 
/* 1323 */       literal.sourceStart, 
/* 1324 */       literal.sourceEnd);
/*      */   }
/*      */   public void corruptedSignature(TypeBinding enclosingType, char[] signature, int position) {
/* 1327 */     handle(
/* 1328 */       536871612, 
/* 1329 */       new String[] { new String(enclosingType.readableName()), new String(signature), String.valueOf(position) }, 
/* 1330 */       new String[] { new String(enclosingType.shortReadableName()), new String(signature), String.valueOf(position) }, 
/* 1331 */       159, 
/* 1332 */       0, 
/* 1333 */       0);
/*      */   }
/*      */   public void deprecatedField(FieldBinding field, ASTNode location) {
/* 1336 */     int severity = computeSeverity(33554505);
/* 1337 */     if (severity == -1) return;
/* 1338 */     handle(
/* 1339 */       33554505, 
/* 1340 */       new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 1341 */       new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 1342 */       severity, 
/* 1343 */       nodeSourceStart(field, location), 
/* 1344 */       nodeSourceEnd(field, location));
/*      */   }
/*      */ 
/*      */   public void deprecatedMethod(MethodBinding method, ASTNode location) {
/* 1348 */     boolean isConstructor = method.isConstructor();
/* 1349 */     int severity = computeSeverity(isConstructor ? 134217861 : 67108967);
/* 1350 */     if (severity == -1) return;
/* 1351 */     if (isConstructor)
/* 1352 */       handle(
/* 1353 */         134217861, 
/* 1354 */         new String[] { new String(method.declaringClass.readableName()), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 1355 */         new String[] { new String(method.declaringClass.shortReadableName()), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 1356 */         severity, 
/* 1357 */         location.sourceStart, 
/* 1358 */         location.sourceEnd);
/*      */     else
/* 1360 */       handle(
/* 1361 */         67108967, 
/* 1362 */         new String[] { new String(method.declaringClass.readableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 1363 */         new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 1364 */         severity, 
/* 1365 */         location.sourceStart, 
/* 1366 */         location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void deprecatedType(TypeBinding type, ASTNode location) {
/* 1370 */     if (location == null) return;
/* 1371 */     int severity = computeSeverity(16777221);
/* 1372 */     if (severity == -1) return;
/* 1373 */     type = type.leafComponentType();
/* 1374 */     handle(
/* 1375 */       16777221, 
/* 1376 */       new String[] { new String(type.readableName()) }, 
/* 1377 */       new String[] { new String(type.shortReadableName()) }, 
/* 1378 */       severity, 
/* 1379 */       location.sourceStart, 
/* 1380 */       nodeSourceEnd(null, location));
/*      */   }
/*      */   public void disallowedTargetForAnnotation(Annotation annotation) {
/* 1383 */     handle(
/* 1384 */       16777838, 
/* 1385 */       new String[] { new String(annotation.resolvedType.readableName()) }, 
/* 1386 */       new String[] { new String(annotation.resolvedType.shortReadableName()) }, 
/* 1387 */       annotation.sourceStart, 
/* 1388 */       annotation.sourceEnd);
/*      */   }
/*      */   public void duplicateAnnotation(Annotation annotation) {
/* 1391 */     handle(
/* 1392 */       16777824, 
/* 1393 */       new String[] { new String(annotation.resolvedType.readableName()) }, 
/* 1394 */       new String[] { new String(annotation.resolvedType.shortReadableName()) }, 
/* 1395 */       annotation.sourceStart, 
/* 1396 */       annotation.sourceEnd);
/*      */   }
/*      */   public void duplicateAnnotationValue(TypeBinding annotationType, MemberValuePair memberValuePair) {
/* 1399 */     String name = new String(memberValuePair.name);
/* 1400 */     handle(
/* 1401 */       536871522, 
/* 1402 */       new String[] { name, new String(annotationType.readableName()) }, 
/* 1403 */       new String[] { name, new String(annotationType.shortReadableName()) }, 
/* 1404 */       memberValuePair.sourceStart, 
/* 1405 */       memberValuePair.sourceEnd);
/*      */   }
/*      */   public void duplicateBounds(ASTNode location, TypeBinding type) {
/* 1408 */     handle(
/* 1409 */       16777783, 
/* 1410 */       new String[] { new String(type.readableName()) }, 
/* 1411 */       new String[] { new String(type.shortReadableName()) }, 
/* 1412 */       location.sourceStart, 
/* 1413 */       location.sourceEnd);
/*      */   }
/*      */   public void duplicateCase(CaseStatement caseStatement) {
/* 1416 */     handle(
/* 1417 */       33554602, 
/* 1418 */       NoArgument, 
/* 1419 */       NoArgument, 
/* 1420 */       caseStatement.sourceStart, 
/* 1421 */       caseStatement.sourceEnd);
/*      */   }
/*      */   public void duplicateDefaultCase(ASTNode statement) {
/* 1424 */     handle(
/* 1425 */       536871078, 
/* 1426 */       NoArgument, 
/* 1427 */       NoArgument, 
/* 1428 */       statement.sourceStart, 
/* 1429 */       statement.sourceEnd);
/*      */   }
/*      */   public void duplicateEnumSpecialMethod(SourceTypeBinding type, AbstractMethodDeclaration methodDecl) {
/* 1432 */     MethodBinding method = methodDecl.binding;
/* 1433 */     handle(
/* 1434 */       67109618, 
/* 1435 */       new String[] { 
/* 1436 */       new String(methodDecl.selector), 
/* 1437 */       new String(method.declaringClass.readableName()), 
/* 1438 */       typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 1439 */       new String[] { 
/* 1440 */       new String(methodDecl.selector), 
/* 1441 */       new String(method.declaringClass.shortReadableName()), 
/* 1442 */       typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 1443 */       methodDecl.sourceStart, 
/* 1444 */       methodDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void duplicateFieldInType(SourceTypeBinding type, FieldDeclaration fieldDecl) {
/* 1448 */     handle(
/* 1449 */       33554772, 
/* 1450 */       new String[] { new String(type.sourceName()), new String(fieldDecl.name) }, 
/* 1451 */       new String[] { new String(type.shortReadableName()), new String(fieldDecl.name) }, 
/* 1452 */       fieldDecl.sourceStart, 
/* 1453 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void duplicateImport(ImportReference importRef) {
/* 1456 */     String[] arguments = { CharOperation.toString(importRef.tokens) };
/* 1457 */     handle(
/* 1458 */       268435842, 
/* 1459 */       arguments, 
/* 1460 */       arguments, 
/* 1461 */       importRef.sourceStart, 
/* 1462 */       importRef.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void duplicateInheritedMethods(SourceTypeBinding type, MethodBinding inheritedMethod1, MethodBinding inheritedMethod2) {
/* 1466 */     handle(
/* 1467 */       67109429, 
/* 1468 */       new String[] { 
/* 1469 */       new String(inheritedMethod1.selector), 
/* 1470 */       new String(inheritedMethod1.declaringClass.readableName()), 
/* 1471 */       typesAsString(inheritedMethod1.isVarargs(), inheritedMethod1.original().parameters, false), 
/* 1472 */       typesAsString(inheritedMethod2.isVarargs(), inheritedMethod2.original().parameters, false) }, 
/* 1473 */       new String[] { 
/* 1474 */       new String(inheritedMethod1.selector), 
/* 1475 */       new String(inheritedMethod1.declaringClass.shortReadableName()), 
/* 1476 */       typesAsString(inheritedMethod1.isVarargs(), inheritedMethod1.original().parameters, true), 
/* 1477 */       typesAsString(inheritedMethod2.isVarargs(), inheritedMethod2.original().parameters, true) }, 
/* 1478 */       type.sourceStart(), 
/* 1479 */       type.sourceEnd());
/*      */   }
/*      */   public void duplicateInitializationOfBlankFinalField(FieldBinding field, Reference reference) {
/* 1482 */     String[] arguments = { new String(field.readableName()) };
/* 1483 */     handle(
/* 1484 */       33554514, 
/* 1485 */       arguments, 
/* 1486 */       arguments, 
/* 1487 */       nodeSourceStart(field, reference), 
/* 1488 */       nodeSourceEnd(field, reference));
/*      */   }
/*      */   public void duplicateInitializationOfFinalLocal(LocalVariableBinding local, ASTNode location) {
/* 1491 */     String[] arguments = { new String(local.readableName()) };
/* 1492 */     handle(
/* 1493 */       536870969, 
/* 1494 */       arguments, 
/* 1495 */       arguments, 
/* 1496 */       nodeSourceStart(local, location), 
/* 1497 */       nodeSourceEnd(local, location));
/*      */   }
/*      */   public void duplicateMethodInType(SourceTypeBinding type, AbstractMethodDeclaration methodDecl, boolean equalParameters) {
/* 1500 */     MethodBinding method = methodDecl.binding;
/* 1501 */     if (equalParameters) {
/* 1502 */       handle(
/* 1503 */         67109219, 
/* 1504 */         new String[] { 
/* 1505 */         new String(methodDecl.selector), 
/* 1506 */         new String(method.declaringClass.readableName()), 
/* 1507 */         typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 1508 */         new String[] { 
/* 1509 */         new String(methodDecl.selector), 
/* 1510 */         new String(method.declaringClass.shortReadableName()), 
/* 1511 */         typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 1512 */         methodDecl.sourceStart, 
/* 1513 */         methodDecl.sourceEnd);
/*      */     } else {
/* 1515 */       int length = method.parameters.length;
/* 1516 */       TypeBinding[] erasures = new TypeBinding[length];
/* 1517 */       for (int i = 0; i < length; i++) {
/* 1518 */         erasures[i] = method.parameters[i].erasure();
/*      */       }
/* 1520 */       handle(
/* 1521 */         16777743, 
/* 1522 */         new String[] { 
/* 1523 */         new String(methodDecl.selector), 
/* 1524 */         new String(method.declaringClass.readableName()), 
/* 1525 */         typesAsString(method.isVarargs(), method.parameters, false), 
/* 1526 */         typesAsString(method.isVarargs(), erasures, false) }, 
/* 1527 */         new String[] { 
/* 1528 */         new String(methodDecl.selector), 
/* 1529 */         new String(method.declaringClass.shortReadableName()), 
/* 1530 */         typesAsString(method.isVarargs(), method.parameters, true), 
/* 1531 */         typesAsString(method.isVarargs(), erasures, true) }, 
/* 1532 */         methodDecl.sourceStart, 
/* 1533 */         methodDecl.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void duplicateModifierForField(ReferenceBinding type, FieldDeclaration fieldDecl)
/*      */   {
/* 1546 */     String[] arguments = { new String(fieldDecl.name) };
/* 1547 */     handle(
/* 1548 */       33554773, 
/* 1549 */       arguments, 
/* 1550 */       arguments, 
/* 1551 */       fieldDecl.sourceStart, 
/* 1552 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void duplicateModifierForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
/* 1555 */     handle(
/* 1556 */       67109221, 
/* 1557 */       new String[] { new String(type.sourceName()), new String(methodDecl.selector) }, 
/* 1558 */       new String[] { new String(type.shortReadableName()), new String(methodDecl.selector) }, 
/* 1559 */       methodDecl.sourceStart, 
/* 1560 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void duplicateModifierForType(SourceTypeBinding type) {
/* 1563 */     String[] arguments = { new String(type.sourceName()) };
/* 1564 */     handle(
/* 1565 */       16777517, 
/* 1566 */       arguments, 
/* 1567 */       arguments, 
/* 1568 */       type.sourceStart(), 
/* 1569 */       type.sourceEnd());
/*      */   }
/*      */   public void duplicateModifierForVariable(LocalDeclaration localDecl, boolean complainForArgument) {
/* 1572 */     String[] arguments = { new String(localDecl.name) };
/* 1573 */     handle(
/* 1574 */       complainForArgument ? 
/* 1575 */       67109232 : 
/* 1576 */       67109259, 
/* 1577 */       arguments, 
/* 1578 */       arguments, 
/* 1579 */       localDecl.sourceStart, 
/* 1580 */       localDecl.sourceEnd);
/*      */   }
/*      */   public void duplicateNestedType(TypeDeclaration typeDecl) {
/* 1583 */     String[] arguments = { new String(typeDecl.name) };
/* 1584 */     handle(
/* 1585 */       16777535, 
/* 1586 */       arguments, 
/* 1587 */       arguments, 
/* 1588 */       typeDecl.sourceStart, 
/* 1589 */       typeDecl.sourceEnd);
/*      */   }
/*      */   public void duplicateSuperinterface(SourceTypeBinding type, TypeReference reference, ReferenceBinding superType) {
/* 1592 */     handle(
/* 1593 */       16777530, 
/* 1594 */       new String[] { 
/* 1595 */       new String(superType.readableName()), 
/* 1596 */       new String(type.sourceName()) }, 
/* 1597 */       new String[] { 
/* 1598 */       new String(superType.shortReadableName()), 
/* 1599 */       new String(type.sourceName()) }, 
/* 1600 */       reference.sourceStart, 
/* 1601 */       reference.sourceEnd);
/*      */   }
/*      */   public void duplicateTargetInTargetAnnotation(TypeBinding annotationType, NameReference reference) {
/* 1604 */     FieldBinding field = reference.fieldBinding();
/* 1605 */     String name = new String(field.name);
/* 1606 */     handle(
/* 1607 */       536871533, 
/* 1608 */       new String[] { name, new String(annotationType.readableName()) }, 
/* 1609 */       new String[] { name, new String(annotationType.shortReadableName()) }, 
/* 1610 */       nodeSourceStart(field, reference), 
/* 1611 */       nodeSourceEnd(field, reference));
/*      */   }
/*      */   public void duplicateTypeParameterInType(TypeParameter typeParameter) {
/* 1614 */     handle(
/* 1615 */       536871432, 
/* 1616 */       new String[] { new String(typeParameter.name) }, 
/* 1617 */       new String[] { new String(typeParameter.name) }, 
/* 1618 */       typeParameter.sourceStart, 
/* 1619 */       typeParameter.sourceEnd);
/*      */   }
/*      */   public void duplicateTypes(CompilationUnitDeclaration compUnitDecl, TypeDeclaration typeDecl) {
/* 1622 */     String[] arguments = { new String(compUnitDecl.getFileName()), new String(typeDecl.name) };
/* 1623 */     this.referenceContext = typeDecl;
/* 1624 */     handle(
/* 1625 */       16777539, 
/* 1626 */       arguments, 
/* 1627 */       arguments, 
/* 1628 */       typeDecl.sourceStart, 
/* 1629 */       typeDecl.sourceEnd, 
/* 1630 */       compUnitDecl.compilationResult);
/*      */   }
/*      */   public void emptyControlFlowStatement(int sourceStart, int sourceEnd) {
/* 1633 */     handle(
/* 1634 */       553648316, 
/* 1635 */       NoArgument, 
/* 1636 */       NoArgument, 
/* 1637 */       sourceStart, 
/* 1638 */       sourceEnd);
/*      */   }
/*      */   public void enumAbstractMethodMustBeImplemented(AbstractMethodDeclaration method) {
/* 1641 */     MethodBinding abstractMethod = method.binding;
/* 1642 */     handle(
/* 1645 */       67109622, 
/* 1646 */       new String[] { 
/* 1647 */       new String(abstractMethod.selector), 
/* 1648 */       typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, false), 
/* 1649 */       new String(abstractMethod.declaringClass.readableName()) }, 
/* 1651 */       new String[] { 
/* 1652 */       new String(abstractMethod.selector), 
/* 1653 */       typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, true), 
/* 1654 */       new String(abstractMethod.declaringClass.shortReadableName()) }, 
/* 1656 */       method.sourceStart(), 
/* 1657 */       method.sourceEnd());
/*      */   }
/*      */   public void enumConstantMustImplementAbstractMethod(AbstractMethodDeclaration method, FieldDeclaration field) {
/* 1660 */     MethodBinding abstractMethod = method.binding;
/* 1661 */     handle(
/* 1662 */       67109627, 
/* 1663 */       new String[] { 
/* 1664 */       new String(abstractMethod.selector), 
/* 1665 */       typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, false), 
/* 1666 */       new String(field.name) }, 
/* 1668 */       new String[] { 
/* 1669 */       new String(abstractMethod.selector), 
/* 1670 */       typesAsString(abstractMethod.isVarargs(), abstractMethod.parameters, true), 
/* 1671 */       new String(field.name) }, 
/* 1673 */       field.sourceStart(), 
/* 1674 */       field.sourceEnd());
/*      */   }
/*      */   public void enumConstantsCannotBeSurroundedByParenthesis(Expression expression) {
/* 1677 */     handle(
/* 1678 */       1610613178, 
/* 1679 */       NoArgument, 
/* 1680 */       NoArgument, 
/* 1681 */       expression.sourceStart, 
/* 1682 */       expression.sourceEnd);
/*      */   }
/*      */   public void enumStaticFieldUsedDuringInitialization(FieldBinding field, ASTNode location) {
/* 1685 */     handle(
/* 1686 */       33555194, 
/* 1687 */       new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 1688 */       new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 1689 */       nodeSourceStart(field, location), 
/* 1690 */       nodeSourceEnd(field, location));
/*      */   }
/*      */   public void enumSwitchCannotTargetField(Reference reference, FieldBinding field) {
/* 1693 */     handle(
/* 1694 */       33555191, 
/* 1695 */       new String[] { String.valueOf(field.declaringClass.readableName()), String.valueOf(field.name) }, 
/* 1696 */       new String[] { String.valueOf(field.declaringClass.shortReadableName()), String.valueOf(field.name) }, 
/* 1697 */       nodeSourceStart(field, reference), 
/* 1698 */       nodeSourceEnd(field, reference));
/*      */   }
/*      */   public void errorNoMethodFor(MessageSend messageSend, TypeBinding recType, TypeBinding[] params) {
/* 1701 */     StringBuffer buffer = new StringBuffer();
/* 1702 */     StringBuffer shortBuffer = new StringBuffer();
/* 1703 */     int i = 0; for (int length = params.length; i < length; i++) {
/* 1704 */       if (i != 0) {
/* 1705 */         buffer.append(", ");
/* 1706 */         shortBuffer.append(", ");
/*      */       }
/* 1708 */       buffer.append(new String(params[i].readableName()));
/* 1709 */       shortBuffer.append(new String(params[i].shortReadableName()));
/*      */     }
/*      */ 
/* 1712 */     int id = recType.isArrayType() ? 67108980 : 67108978;
/* 1713 */     handle(
/* 1714 */       id, 
/* 1715 */       new String[] { new String(recType.readableName()), new String(messageSend.selector), buffer.toString() }, 
/* 1716 */       new String[] { new String(recType.shortReadableName()), new String(messageSend.selector), shortBuffer.toString() }, 
/* 1717 */       messageSend.sourceStart, 
/* 1718 */       messageSend.sourceEnd);
/*      */   }
/*      */   public void errorThisSuperInStatic(ASTNode reference) {
/* 1721 */     String[] arguments = { reference.isSuper() ? "super" : "this" };
/* 1722 */     handle(
/* 1723 */       536871112, 
/* 1724 */       arguments, 
/* 1725 */       arguments, 
/* 1726 */       reference.sourceStart, 
/* 1727 */       reference.sourceEnd);
/*      */   }
/*      */   public void expressionShouldBeAVariable(Expression expression) {
/* 1730 */     handle(
/* 1731 */       1610612959, 
/* 1732 */       NoArgument, 
/* 1733 */       NoArgument, 
/* 1734 */       expression.sourceStart, 
/* 1735 */       expression.sourceEnd);
/*      */   }
/*      */   public void fakeReachable(ASTNode location) {
/* 1738 */     int sourceStart = location.sourceStart;
/* 1739 */     int sourceEnd = location.sourceEnd;
/* 1740 */     if ((location instanceof LocalDeclaration)) {
/* 1741 */       LocalDeclaration declaration = (LocalDeclaration)location;
/* 1742 */       sourceStart = declaration.declarationSourceStart;
/* 1743 */       sourceEnd = declaration.declarationSourceEnd;
/*      */     }
/* 1745 */     handle(
/* 1746 */       536871061, 
/* 1747 */       NoArgument, 
/* 1748 */       NoArgument, 
/* 1749 */       sourceStart, 
/* 1750 */       sourceEnd);
/*      */   }
/*      */   public void fieldHiding(FieldDeclaration fieldDecl, Binding hiddenVariable) {
/* 1753 */     FieldBinding field = fieldDecl.binding;
/* 1754 */     if ((CharOperation.equals(TypeConstants.SERIALVERSIONUID, field.name)) && 
/* 1755 */       (field.isStatic()) && 
/* 1756 */       (field.isFinal()) && 
/* 1757 */       (TypeBinding.LONG == field.type)) {
/* 1758 */       return;
/*      */     }
/* 1760 */     if ((CharOperation.equals(TypeConstants.SERIALPERSISTENTFIELDS, field.name)) && 
/* 1761 */       (field.isStatic()) && 
/* 1762 */       (field.isFinal()) && 
/* 1763 */       (field.type.dimensions() == 1) && 
/* 1764 */       (CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTSTREAMFIELD, field.type.leafComponentType().readableName()))) {
/* 1765 */       return;
/*      */     }
/* 1767 */     boolean isLocal = hiddenVariable instanceof LocalVariableBinding;
/* 1768 */     int severity = computeSeverity(isLocal ? 570425436 : 570425437);
/* 1769 */     if (severity == -1) return;
/* 1770 */     if (isLocal) {
/* 1771 */       handle(
/* 1772 */         570425436, 
/* 1773 */         new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 1774 */         new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 1775 */         severity, 
/* 1776 */         nodeSourceStart(hiddenVariable, fieldDecl), 
/* 1777 */         nodeSourceEnd(hiddenVariable, fieldDecl));
/* 1778 */     } else if ((hiddenVariable instanceof FieldBinding)) {
/* 1779 */       FieldBinding hiddenField = (FieldBinding)hiddenVariable;
/* 1780 */       handle(
/* 1781 */         570425437, 
/* 1782 */         new String[] { new String(field.declaringClass.readableName()), new String(field.name), new String(hiddenField.declaringClass.readableName()) }, 
/* 1783 */         new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name), new String(hiddenField.declaringClass.shortReadableName()) }, 
/* 1784 */         severity, 
/* 1785 */         nodeSourceStart(hiddenField, fieldDecl), 
/* 1786 */         nodeSourceEnd(hiddenField, fieldDecl));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldsOrThisBeforeConstructorInvocation(ThisReference reference) {
/* 1790 */     handle(
/* 1791 */       134217866, 
/* 1792 */       NoArgument, 
/* 1793 */       NoArgument, 
/* 1794 */       reference.sourceStart, 
/* 1795 */       reference.sourceEnd);
/*      */   }
/*      */   public void finallyMustCompleteNormally(Block finallyBlock) {
/* 1798 */     handle(
/* 1799 */       536871096, 
/* 1800 */       NoArgument, 
/* 1801 */       NoArgument, 
/* 1802 */       finallyBlock.sourceStart, 
/* 1803 */       finallyBlock.sourceEnd);
/*      */   }
/*      */   public void finalMethodCannotBeOverridden(MethodBinding currentMethod, MethodBinding inheritedMethod) {
/* 1806 */     handle(
/* 1809 */       67109265, 
/* 1810 */       new String[] { new String(inheritedMethod.declaringClass.readableName()) }, 
/* 1811 */       new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 1812 */       currentMethod.sourceStart(), 
/* 1813 */       currentMethod.sourceEnd());
/*      */   }
/*      */   public void finalVariableBound(TypeVariableBinding typeVariable, TypeReference typeRef) {
/* 1816 */     int severity = computeSeverity(16777753);
/* 1817 */     if (severity == -1) return;
/* 1818 */     handle(
/* 1819 */       16777753, 
/* 1820 */       new String[] { new String(typeVariable.sourceName), new String(typeRef.resolvedType.readableName()) }, 
/* 1821 */       new String[] { new String(typeVariable.sourceName), new String(typeRef.resolvedType.shortReadableName()) }, 
/* 1822 */       severity, 
/* 1823 */       typeRef.sourceStart, 
/* 1824 */       typeRef.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void forbiddenReference(FieldBinding field, ASTNode location, byte classpathEntryType, String classpathEntryName, int problemId)
/*      */   {
/* 1830 */     int severity = computeSeverity(problemId);
/* 1831 */     if (severity == -1) return;
/* 1832 */     handle(
/* 1833 */       problemId, 
/* 1834 */       new String[] { new String(field.readableName()) }, 
/* 1835 */       getElaborationId(16777523, (byte)(0x4 | classpathEntryType)), 
/* 1836 */       new String[] { 
/* 1837 */       classpathEntryName, 
/* 1838 */       new String(field.shortReadableName()), 
/* 1839 */       new String(field.declaringClass.shortReadableName()) }, 
/* 1840 */       severity, 
/* 1841 */       nodeSourceStart(field, location), 
/* 1842 */       nodeSourceEnd(field, location));
/*      */   }
/*      */ 
/*      */   public void forbiddenReference(MethodBinding method, ASTNode location, byte classpathEntryType, String classpathEntryName, int problemId)
/*      */   {
/* 1848 */     int severity = computeSeverity(problemId);
/* 1849 */     if (severity == -1) return;
/* 1850 */     if (method.isConstructor())
/* 1851 */       handle(
/* 1852 */         problemId, 
/* 1853 */         new String[] { new String(method.readableName()) }, 
/* 1854 */         getElaborationId(16777523, (byte)(0x8 | classpathEntryType)), 
/* 1855 */         new String[] { 
/* 1856 */         classpathEntryName, 
/* 1857 */         new String(method.shortReadableName()) }, 
/* 1858 */         severity, 
/* 1859 */         location.sourceStart, 
/* 1860 */         location.sourceEnd);
/*      */     else
/* 1862 */       handle(
/* 1863 */         problemId, 
/* 1864 */         new String[] { new String(method.readableName()) }, 
/* 1865 */         getElaborationId(16777523, (byte)(0xC | classpathEntryType)), 
/* 1866 */         new String[] { 
/* 1867 */         classpathEntryName, 
/* 1868 */         new String(method.shortReadableName()), 
/* 1869 */         new String(method.declaringClass.shortReadableName()) }, 
/* 1870 */         severity, 
/* 1871 */         location.sourceStart, 
/* 1872 */         location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void forbiddenReference(TypeBinding type, ASTNode location, byte classpathEntryType, String classpathEntryName, int problemId)
/*      */   {
/* 1878 */     if (location == null) return;
/* 1879 */     int severity = computeSeverity(problemId);
/* 1880 */     if (severity == -1) return;
/* 1881 */     handle(
/* 1882 */       problemId, 
/* 1883 */       new String[] { new String(type.readableName()) }, 
/* 1884 */       getElaborationId(16777523, classpathEntryType), 
/* 1885 */       new String[] { 
/* 1886 */       classpathEntryName, 
/* 1887 */       new String(type.shortReadableName()) }, 
/* 1888 */       severity, 
/* 1889 */       location.sourceStart, 
/* 1890 */       location.sourceEnd);
/*      */   }
/*      */   public void forwardReference(Reference reference, int indexInQualification, FieldBinding field) {
/* 1893 */     handle(
/* 1894 */       570425419, 
/* 1895 */       NoArgument, 
/* 1896 */       NoArgument, 
/* 1897 */       nodeSourceStart(field, reference, indexInQualification), 
/* 1898 */       nodeSourceEnd(field, reference, indexInQualification));
/*      */   }
/*      */   public void forwardTypeVariableReference(ASTNode location, TypeVariableBinding type) {
/* 1901 */     handle(
/* 1902 */       16777744, 
/* 1903 */       new String[] { new String(type.readableName()) }, 
/* 1904 */       new String[] { new String(type.shortReadableName()) }, 
/* 1905 */       location.sourceStart, 
/* 1906 */       location.sourceEnd);
/*      */   }
/*      */   public void genericTypeCannotExtendThrowable(TypeDeclaration typeDecl) {
/* 1909 */     ASTNode location = typeDecl.binding.isAnonymousType() ? typeDecl.allocation.type : typeDecl.superclass;
/* 1910 */     handle(
/* 1911 */       16777773, 
/* 1912 */       new String[] { new String(typeDecl.binding.readableName()) }, 
/* 1913 */       new String[] { new String(typeDecl.binding.shortReadableName()) }, 
/* 1914 */       location.sourceStart, 
/* 1915 */       location.sourceEnd);
/*      */   }
/*      */ 
/*      */   private void handle(int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition)
/*      */   {
/* 1928 */     handle(
/* 1929 */       problemId, 
/* 1930 */       problemArguments, 
/* 1931 */       elaborationId, 
/* 1932 */       messageArguments, 
/* 1933 */       severity, 
/* 1934 */       problemStartPosition, 
/* 1935 */       problemEndPosition, 
/* 1936 */       this.referenceContext, 
/* 1937 */       this.referenceContext == null ? null : this.referenceContext.compilationResult());
/* 1938 */     this.referenceContext = null;
/*      */   }
/*      */ 
/*      */   private void handle(int problemId, String[] problemArguments, String[] messageArguments, int problemStartPosition, int problemEndPosition)
/*      */   {
/* 1950 */     handle(
/* 1951 */       problemId, 
/* 1952 */       problemArguments, 
/* 1953 */       messageArguments, 
/* 1954 */       problemStartPosition, 
/* 1955 */       problemEndPosition, 
/* 1956 */       this.referenceContext, 
/* 1957 */       this.referenceContext == null ? null : this.referenceContext.compilationResult());
/* 1958 */     this.referenceContext = null;
/*      */   }
/*      */ 
/*      */   private void handle(int problemId, String[] problemArguments, String[] messageArguments, int problemStartPosition, int problemEndPosition, CompilationResult unitResult)
/*      */   {
/* 1970 */     handle(
/* 1971 */       problemId, 
/* 1972 */       problemArguments, 
/* 1973 */       messageArguments, 
/* 1974 */       problemStartPosition, 
/* 1975 */       problemEndPosition, 
/* 1976 */       this.referenceContext, 
/* 1977 */       unitResult);
/* 1978 */     this.referenceContext = null;
/*      */   }
/*      */ 
/*      */   private void handle(int problemId, String[] problemArguments, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition)
/*      */   {
/* 1991 */     handle(
/* 1992 */       problemId, 
/* 1993 */       problemArguments, 
/* 1994 */       0, 
/* 1995 */       messageArguments, 
/* 1996 */       severity, 
/* 1997 */       problemStartPosition, 
/* 1998 */       problemEndPosition);
/*      */   }
/*      */ 
/*      */   public void hiddenCatchBlock(ReferenceBinding exceptionType, ASTNode location) {
/* 2002 */     handle(
/* 2003 */       16777381, 
/* 2004 */       new String[] { 
/* 2005 */       new String(exceptionType.readableName()) }, 
/* 2007 */       new String[] { 
/* 2008 */       new String(exceptionType.shortReadableName()) }, 
/* 2010 */       location.sourceStart, 
/* 2011 */       location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void hierarchyCircularity(SourceTypeBinding sourceType, ReferenceBinding superType, TypeReference reference) {
/* 2015 */     int start = 0;
/* 2016 */     int end = 0;
/*      */ 
/* 2018 */     if (reference == null) {
/* 2019 */       start = sourceType.sourceStart();
/* 2020 */       end = sourceType.sourceEnd();
/*      */     } else {
/* 2022 */       start = reference.sourceStart;
/* 2023 */       end = reference.sourceEnd;
/*      */     }
/*      */ 
/* 2026 */     if (sourceType == superType)
/* 2027 */       handle(
/* 2028 */         16777532, 
/* 2029 */         new String[] { new String(sourceType.readableName()) }, 
/* 2030 */         new String[] { new String(sourceType.shortReadableName()) }, 
/* 2031 */         start, 
/* 2032 */         end);
/*      */     else
/* 2034 */       handle(
/* 2035 */         16777533, 
/* 2036 */         new String[] { new String(sourceType.readableName()), new String(superType.readableName()) }, 
/* 2037 */         new String[] { new String(sourceType.shortReadableName()), new String(superType.shortReadableName()) }, 
/* 2038 */         start, 
/* 2039 */         end);
/*      */   }
/*      */ 
/*      */   public void hierarchyHasProblems(SourceTypeBinding type) {
/* 2043 */     String[] arguments = { new String(type.sourceName()) };
/* 2044 */     handle(
/* 2045 */       16777543, 
/* 2046 */       arguments, 
/* 2047 */       arguments, 
/* 2048 */       type.sourceStart(), 
/* 2049 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalAbstractModifierCombinationForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
/* 2052 */     String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
/* 2053 */     handle(
/* 2054 */       67109226, 
/* 2055 */       arguments, 
/* 2056 */       arguments, 
/* 2057 */       methodDecl.sourceStart, 
/* 2058 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void illegalAccessFromTypeVariable(TypeVariableBinding variable, ASTNode location) {
/* 2061 */     if ((location.bits & 0x8000) != 0) {
/* 2062 */       javadocInvalidReference(location.sourceStart, location.sourceEnd);
/*      */     } else {
/* 2064 */       String[] arguments = { new String(variable.sourceName) };
/* 2065 */       handle(
/* 2066 */         16777791, 
/* 2067 */         arguments, 
/* 2068 */         arguments, 
/* 2069 */         location.sourceStart, 
/* 2070 */         location.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void illegalClassLiteralForTypeVariable(TypeVariableBinding variable, ASTNode location) {
/* 2074 */     String[] arguments = { new String(variable.sourceName) };
/* 2075 */     handle(
/* 2076 */       16777774, 
/* 2077 */       arguments, 
/* 2078 */       arguments, 
/* 2079 */       location.sourceStart, 
/* 2080 */       location.sourceEnd);
/*      */   }
/*      */   public void illegalExtendedDimensions(AnnotationMethodDeclaration annotationTypeMemberDeclaration) {
/* 2083 */     handle(
/* 2084 */       67109465, 
/* 2085 */       NoArgument, 
/* 2086 */       NoArgument, 
/* 2087 */       annotationTypeMemberDeclaration.sourceStart, 
/* 2088 */       annotationTypeMemberDeclaration.sourceEnd);
/*      */   }
/*      */   public void illegalExtendedDimensions(Argument argument) {
/* 2091 */     handle(
/* 2092 */       1610613536, 
/* 2093 */       NoArgument, 
/* 2094 */       NoArgument, 
/* 2095 */       argument.sourceStart, 
/* 2096 */       argument.sourceEnd);
/*      */   }
/*      */   public void illegalGenericArray(TypeBinding leafComponentType, ASTNode location) {
/* 2099 */     handle(
/* 2100 */       16777751, 
/* 2101 */       new String[] { new String(leafComponentType.readableName()) }, 
/* 2102 */       new String[] { new String(leafComponentType.shortReadableName()) }, 
/* 2103 */       location.sourceStart, 
/* 2104 */       location.sourceEnd);
/*      */   }
/*      */   public void illegalInstanceOfGenericType(TypeBinding checkedType, ASTNode location) {
/* 2107 */     TypeBinding erasedType = checkedType.leafComponentType().erasure();
/* 2108 */     StringBuffer recommendedFormBuffer = new StringBuffer(10);
/* 2109 */     recommendedFormBuffer.append(erasedType.sourceName());
/* 2110 */     int count = erasedType.typeVariables().length;
/* 2111 */     if (count > 0) {
/* 2112 */       recommendedFormBuffer.append('<');
/* 2113 */       for (int i = 0; i < count; i++) {
/* 2114 */         if (i > 0) {
/* 2115 */           recommendedFormBuffer.append(',');
/*      */         }
/* 2117 */         recommendedFormBuffer.append('?');
/*      */       }
/* 2119 */       recommendedFormBuffer.append('>');
/*      */     }
/* 2121 */     int i = 0; for (int dim = checkedType.dimensions(); i < dim; i++) {
/* 2122 */       recommendedFormBuffer.append("[]");
/*      */     }
/* 2124 */     String recommendedForm = recommendedFormBuffer.toString();
/* 2125 */     if (checkedType.leafComponentType().isTypeVariable()) {
/* 2126 */       handle(
/* 2127 */         536871459, 
/* 2128 */         new String[] { new String(checkedType.readableName()), recommendedForm }, 
/* 2129 */         new String[] { new String(checkedType.shortReadableName()), recommendedForm }, 
/* 2130 */         location.sourceStart, 
/* 2131 */         location.sourceEnd);
/* 2132 */       return;
/*      */     }
/* 2134 */     handle(
/* 2135 */       536871458, 
/* 2136 */       new String[] { new String(checkedType.readableName()), recommendedForm }, 
/* 2137 */       new String[] { new String(checkedType.shortReadableName()), recommendedForm }, 
/* 2138 */       location.sourceStart, 
/* 2139 */       location.sourceEnd);
/*      */   }
/*      */   public void illegalLocalTypeDeclaration(TypeDeclaration typeDeclaration) {
/* 2142 */     if (isRecoveredName(typeDeclaration.name)) return;
/*      */ 
/* 2144 */     int problemID = 0;
/* 2145 */     if ((typeDeclaration.modifiers & 0x4000) != 0)
/* 2146 */       problemID = 536870943;
/* 2147 */     else if ((typeDeclaration.modifiers & 0x2000) != 0)
/* 2148 */       problemID = 536870942;
/* 2149 */     else if ((typeDeclaration.modifiers & 0x200) != 0) {
/* 2150 */       problemID = 536870938;
/*      */     }
/* 2152 */     if (problemID != 0) {
/* 2153 */       String[] arguments = { new String(typeDeclaration.name) };
/* 2154 */       handle(
/* 2155 */         problemID, 
/* 2156 */         arguments, 
/* 2157 */         arguments, 
/* 2158 */         typeDeclaration.sourceStart, 
/* 2159 */         typeDeclaration.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void illegalModifierCombinationFinalAbstractForClass(SourceTypeBinding type) {
/* 2163 */     String[] arguments = { new String(type.sourceName()) };
/* 2164 */     handle(
/* 2165 */       16777524, 
/* 2166 */       arguments, 
/* 2167 */       arguments, 
/* 2168 */       type.sourceStart(), 
/* 2169 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierCombinationFinalVolatileForField(ReferenceBinding type, FieldDeclaration fieldDecl) {
/* 2172 */     String[] arguments = { new String(fieldDecl.name) };
/*      */ 
/* 2174 */     handle(
/* 2175 */       33554777, 
/* 2176 */       arguments, 
/* 2177 */       arguments, 
/* 2178 */       fieldDecl.sourceStart, 
/* 2179 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void illegalModifierForAnnotationField(FieldDeclaration fieldDecl) {
/* 2182 */     String name = new String(fieldDecl.name);
/* 2183 */     handle(
/* 2184 */       536871527, 
/* 2185 */       new String[] { 
/* 2186 */       new String(fieldDecl.binding.declaringClass.readableName()), 
/* 2187 */       name }, 
/* 2189 */       new String[] { 
/* 2190 */       new String(fieldDecl.binding.declaringClass.shortReadableName()), 
/* 2191 */       name }, 
/* 2193 */       fieldDecl.sourceStart, 
/* 2194 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void illegalModifierForAnnotationMember(AbstractMethodDeclaration methodDecl) {
/* 2197 */     handle(
/* 2198 */       67109464, 
/* 2199 */       new String[] { 
/* 2200 */       new String(methodDecl.binding.declaringClass.readableName()), 
/* 2201 */       new String(methodDecl.selector) }, 
/* 2203 */       new String[] { 
/* 2204 */       new String(methodDecl.binding.declaringClass.shortReadableName()), 
/* 2205 */       new String(methodDecl.selector) }, 
/* 2207 */       methodDecl.sourceStart, 
/* 2208 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void illegalModifierForAnnotationMemberType(SourceTypeBinding type) {
/* 2211 */     String[] arguments = { new String(type.sourceName()) };
/* 2212 */     handle(
/* 2213 */       16777820, 
/* 2214 */       arguments, 
/* 2215 */       arguments, 
/* 2216 */       type.sourceStart(), 
/* 2217 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierForAnnotationType(SourceTypeBinding type) {
/* 2220 */     String[] arguments = { new String(type.sourceName()) };
/* 2221 */     handle(
/* 2222 */       16777819, 
/* 2223 */       arguments, 
/* 2224 */       arguments, 
/* 2225 */       type.sourceStart(), 
/* 2226 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierForClass(SourceTypeBinding type) {
/* 2229 */     String[] arguments = { new String(type.sourceName()) };
/* 2230 */     handle(
/* 2231 */       16777518, 
/* 2232 */       arguments, 
/* 2233 */       arguments, 
/* 2234 */       type.sourceStart(), 
/* 2235 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierForEnum(SourceTypeBinding type) {
/* 2238 */     String[] arguments = { new String(type.sourceName()) };
/* 2239 */     handle(
/* 2240 */       16777966, 
/* 2241 */       arguments, 
/* 2242 */       arguments, 
/* 2243 */       type.sourceStart(), 
/* 2244 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierForEnumConstant(ReferenceBinding type, FieldDeclaration fieldDecl) {
/* 2247 */     String[] arguments = { new String(fieldDecl.name) };
/* 2248 */     handle(
/* 2249 */       33555183, 
/* 2250 */       arguments, 
/* 2251 */       arguments, 
/* 2252 */       fieldDecl.sourceStart, 
/* 2253 */       fieldDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void illegalModifierForEnumConstructor(AbstractMethodDeclaration constructor) {
/* 2257 */     handle(
/* 2258 */       67109624, 
/* 2259 */       NoArgument, 
/* 2260 */       NoArgument, 
/* 2261 */       constructor.sourceStart, 
/* 2262 */       constructor.sourceEnd);
/*      */   }
/*      */   public void illegalModifierForField(ReferenceBinding type, FieldDeclaration fieldDecl) {
/* 2265 */     String[] arguments = { new String(fieldDecl.name) };
/* 2266 */     handle(
/* 2267 */       33554774, 
/* 2268 */       arguments, 
/* 2269 */       arguments, 
/* 2270 */       fieldDecl.sourceStart, 
/* 2271 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void illegalModifierForInterface(SourceTypeBinding type) {
/* 2274 */     String[] arguments = { new String(type.sourceName()) };
/* 2275 */     handle(
/* 2276 */       16777519, 
/* 2277 */       arguments, 
/* 2278 */       arguments, 
/* 2279 */       type.sourceStart(), 
/* 2280 */       type.sourceEnd());
/*      */   }
/*      */ 
/*      */   public void illegalModifierForInterfaceField(FieldDeclaration fieldDecl) {
/* 2284 */     String name = new String(fieldDecl.name);
/* 2285 */     handle(
/* 2286 */       33554775, 
/* 2287 */       new String[] { 
/* 2288 */       new String(fieldDecl.binding.declaringClass.readableName()), 
/* 2289 */       name }, 
/* 2291 */       new String[] { 
/* 2292 */       new String(fieldDecl.binding.declaringClass.shortReadableName()), 
/* 2293 */       name }, 
/* 2295 */       fieldDecl.sourceStart, 
/* 2296 */       fieldDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void illegalModifierForInterfaceMethod(AbstractMethodDeclaration methodDecl)
/*      */   {
/* 2301 */     handle(
/* 2302 */       67109223, 
/* 2303 */       new String[] { 
/* 2304 */       new String(methodDecl.selector) }, 
/* 2306 */       new String[] { 
/* 2307 */       new String(methodDecl.selector) }, 
/* 2309 */       methodDecl.sourceStart, 
/* 2310 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void illegalModifierForLocalClass(SourceTypeBinding type) {
/* 2313 */     String[] arguments = { new String(type.sourceName()) };
/* 2314 */     handle(
/* 2315 */       16777522, 
/* 2316 */       arguments, 
/* 2317 */       arguments, 
/* 2318 */       type.sourceStart(), 
/* 2319 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierForMemberClass(SourceTypeBinding type) {
/* 2322 */     String[] arguments = { new String(type.sourceName()) };
/* 2323 */     handle(
/* 2324 */       16777520, 
/* 2325 */       arguments, 
/* 2326 */       arguments, 
/* 2327 */       type.sourceStart(), 
/* 2328 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierForMemberEnum(SourceTypeBinding type) {
/* 2331 */     String[] arguments = { new String(type.sourceName()) };
/* 2332 */     handle(
/* 2333 */       16777969, 
/* 2334 */       arguments, 
/* 2335 */       arguments, 
/* 2336 */       type.sourceStart(), 
/* 2337 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalModifierForMemberInterface(SourceTypeBinding type) {
/* 2340 */     String[] arguments = { new String(type.sourceName()) };
/* 2341 */     handle(
/* 2342 */       16777521, 
/* 2343 */       arguments, 
/* 2344 */       arguments, 
/* 2345 */       type.sourceStart(), 
/* 2346 */       type.sourceEnd());
/*      */   }
/*      */ 
/*      */   public void illegalModifierForMethod(AbstractMethodDeclaration methodDecl)
/*      */   {
/* 2351 */     handle(
/* 2352 */       methodDecl.isConstructor() ? 67109233 : 67109222, 
/* 2353 */       new String[] { 
/* 2354 */       new String(methodDecl.selector) }, 
/* 2356 */       new String[] { 
/* 2357 */       new String(methodDecl.selector) }, 
/* 2359 */       methodDecl.sourceStart, 
/* 2360 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void illegalModifierForVariable(LocalDeclaration localDecl, boolean complainAsArgument) {
/* 2363 */     String[] arguments = { new String(localDecl.name) };
/* 2364 */     handle(
/* 2365 */       complainAsArgument ? 
/* 2366 */       67109220 : 
/* 2367 */       67109260, 
/* 2368 */       arguments, 
/* 2369 */       arguments, 
/* 2370 */       localDecl.sourceStart, 
/* 2371 */       localDecl.sourceEnd);
/*      */   }
/*      */   public void illegalPrimitiveOrArrayTypeForEnclosingInstance(TypeBinding enclosingType, ASTNode location) {
/* 2374 */     handle(
/* 2375 */       16777243, 
/* 2376 */       new String[] { new String(enclosingType.readableName()) }, 
/* 2377 */       new String[] { new String(enclosingType.shortReadableName()) }, 
/* 2378 */       location.sourceStart, 
/* 2379 */       location.sourceEnd);
/*      */   }
/*      */   public void illegalQualifiedParameterizedTypeAllocation(TypeReference qualifiedTypeReference, TypeBinding allocatedType) {
/* 2382 */     handle(
/* 2383 */       16777782, 
/* 2384 */       new String[] { new String(allocatedType.readableName()), new String(allocatedType.enclosingType().readableName()) }, 
/* 2385 */       new String[] { new String(allocatedType.shortReadableName()), new String(allocatedType.enclosingType().shortReadableName()) }, 
/* 2386 */       qualifiedTypeReference.sourceStart, 
/* 2387 */       qualifiedTypeReference.sourceEnd);
/*      */   }
/*      */   public void illegalStaticModifierForMemberType(SourceTypeBinding type) {
/* 2390 */     String[] arguments = { new String(type.sourceName()) };
/* 2391 */     handle(
/* 2392 */       16777527, 
/* 2393 */       arguments, 
/* 2394 */       arguments, 
/* 2395 */       type.sourceStart(), 
/* 2396 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalUsageOfQualifiedTypeReference(QualifiedTypeReference qualifiedTypeReference) {
/* 2399 */     StringBuffer buffer = new StringBuffer();
/* 2400 */     char[][] tokens = qualifiedTypeReference.tokens;
/* 2401 */     for (int i = 0; i < tokens.length; i++) {
/* 2402 */       if (i > 0) buffer.append('.');
/* 2403 */       buffer.append(tokens[i]);
/*      */     }
/* 2405 */     String[] arguments = { String.valueOf(buffer) };
/* 2406 */     handle(
/* 2407 */       1610612934, 
/* 2408 */       arguments, 
/* 2409 */       arguments, 
/* 2410 */       qualifiedTypeReference.sourceStart, 
/* 2411 */       qualifiedTypeReference.sourceEnd);
/*      */   }
/*      */   public void illegalUsageOfWildcard(TypeReference wildcard) {
/* 2414 */     handle(
/* 2415 */       1610613314, 
/* 2416 */       NoArgument, 
/* 2417 */       NoArgument, 
/* 2418 */       wildcard.sourceStart, 
/* 2419 */       wildcard.sourceEnd);
/*      */   }
/*      */   public void illegalVararg(Argument argType, AbstractMethodDeclaration methodDecl) {
/* 2422 */     String[] arguments = { CharOperation.toString(argType.type.getTypeName()), new String(methodDecl.selector) };
/* 2423 */     handle(
/* 2424 */       67109279, 
/* 2425 */       arguments, 
/* 2426 */       arguments, 
/* 2427 */       argType.sourceStart, 
/* 2428 */       argType.sourceEnd);
/*      */   }
/*      */   public void illegalVisibilityModifierCombinationForField(ReferenceBinding type, FieldDeclaration fieldDecl) {
/* 2431 */     String[] arguments = { new String(fieldDecl.name) };
/* 2432 */     handle(
/* 2433 */       33554776, 
/* 2434 */       arguments, 
/* 2435 */       arguments, 
/* 2436 */       fieldDecl.sourceStart, 
/* 2437 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void illegalVisibilityModifierCombinationForMemberType(SourceTypeBinding type) {
/* 2440 */     String[] arguments = { new String(type.sourceName()) };
/* 2441 */     handle(
/* 2442 */       16777526, 
/* 2443 */       arguments, 
/* 2444 */       arguments, 
/* 2445 */       type.sourceStart(), 
/* 2446 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalVisibilityModifierCombinationForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
/* 2449 */     String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
/* 2450 */     handle(
/* 2451 */       67109224, 
/* 2452 */       arguments, 
/* 2453 */       arguments, 
/* 2454 */       methodDecl.sourceStart, 
/* 2455 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void illegalVisibilityModifierForInterfaceMemberType(SourceTypeBinding type) {
/* 2458 */     String[] arguments = { new String(type.sourceName()) };
/* 2459 */     handle(
/* 2460 */       16777525, 
/* 2461 */       arguments, 
/* 2462 */       arguments, 
/* 2463 */       type.sourceStart(), 
/* 2464 */       type.sourceEnd());
/*      */   }
/*      */   public void illegalVoidExpression(ASTNode location) {
/* 2467 */     handle(
/* 2468 */       536871076, 
/* 2469 */       NoArgument, 
/* 2470 */       NoArgument, 
/* 2471 */       location.sourceStart, 
/* 2472 */       location.sourceEnd);
/*      */   }
/*      */   public void importProblem(ImportReference importRef, Binding expectedImport) {
/* 2475 */     if ((expectedImport instanceof FieldBinding)) {
/* 2476 */       int id = 33554502;
/* 2477 */       FieldBinding field = (FieldBinding)expectedImport;
/* 2478 */       String[] readableArguments = (String[])null;
/* 2479 */       String[] shortArguments = (String[])null;
/* 2480 */       switch (expectedImport.problemId()) {
/*      */       case 2:
/* 2482 */         id = 33554503;
/* 2483 */         readableArguments = new String[] { CharOperation.toString(importRef.tokens), new String(field.declaringClass.readableName()) };
/* 2484 */         shortArguments = new String[] { CharOperation.toString(importRef.tokens), new String(field.declaringClass.shortReadableName()) };
/* 2485 */         break;
/*      */       case 3:
/* 2487 */         id = 33554504;
/* 2488 */         readableArguments = new String[] { new String(field.readableName()) };
/* 2489 */         shortArguments = new String[] { new String(field.readableName()) };
/* 2490 */         break;
/*      */       case 8:
/* 2492 */         id = 16777219;
/* 2493 */         readableArguments = new String[] { new String(field.declaringClass.leafComponentType().readableName()) };
/* 2494 */         shortArguments = new String[] { new String(field.declaringClass.leafComponentType().shortReadableName()) };
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/* 2497 */       case 7: } handle(
/* 2498 */         id, 
/* 2499 */         readableArguments, 
/* 2500 */         shortArguments, 
/* 2501 */         nodeSourceStart(field, importRef), 
/* 2502 */         nodeSourceEnd(field, importRef));
/* 2503 */       return;
/*      */     }
/*      */ 
/* 2506 */     if (expectedImport.problemId() == 1) {
/* 2507 */       char[][] tokens = (expectedImport instanceof ProblemReferenceBinding) ? 
/* 2508 */         ((ProblemReferenceBinding)expectedImport).compoundName : 
/* 2509 */         importRef.tokens;
/* 2510 */       String[] arguments = { CharOperation.toString(tokens) };
/* 2511 */       handle(
/* 2512 */         268435846, 
/* 2513 */         arguments, 
/* 2514 */         arguments, 
/* 2515 */         importRef.sourceStart, 
/* 2516 */         (int)importRef.sourcePositions[(tokens.length - 1)]);
/* 2517 */       return;
/*      */     }
/* 2519 */     if (expectedImport.problemId() == 14) {
/* 2520 */       char[][] tokens = importRef.tokens;
/* 2521 */       String[] arguments = { CharOperation.toString(tokens) };
/* 2522 */       handle(
/* 2523 */         268435847, 
/* 2524 */         arguments, 
/* 2525 */         arguments, 
/* 2526 */         importRef.sourceStart, 
/* 2527 */         (int)importRef.sourcePositions[(tokens.length - 1)]);
/* 2528 */       return;
/*      */     }
/* 2530 */     invalidType(importRef, (TypeBinding)expectedImport);
/*      */   }
/*      */   public void incompatibleExceptionInThrowsClause(SourceTypeBinding type, MethodBinding currentMethod, MethodBinding inheritedMethod, ReferenceBinding exceptionType) {
/* 2533 */     if (type == currentMethod.declaringClass)
/*      */     {
/*      */       int id;
/*      */       int id;
/* 2535 */       if ((currentMethod.declaringClass.isInterface()) && 
/* 2536 */         (!inheritedMethod.isPublic()))
/* 2537 */         id = 67109278;
/*      */       else {
/* 2539 */         id = 67109266;
/*      */       }
/* 2541 */       handle(
/* 2544 */         id, 
/* 2545 */         new String[] { 
/* 2546 */         new String(exceptionType.sourceName()), 
/* 2547 */         new String(
/* 2548 */         CharOperation.concat(
/* 2549 */         inheritedMethod.declaringClass.readableName(), 
/* 2550 */         inheritedMethod.readableName(), 
/* 2551 */         '.')) }, 
/* 2552 */         new String[] { 
/* 2553 */         new String(exceptionType.sourceName()), 
/* 2554 */         new String(
/* 2555 */         CharOperation.concat(
/* 2556 */         inheritedMethod.declaringClass.shortReadableName(), 
/* 2557 */         inheritedMethod.shortReadableName(), 
/* 2558 */         '.')) }, 
/* 2559 */         currentMethod.sourceStart(), 
/* 2560 */         currentMethod.sourceEnd());
/*      */     } else {
/* 2562 */       handle(
/* 2565 */         67109267, 
/* 2566 */         new String[] { 
/* 2567 */         new String(exceptionType.sourceName()), 
/* 2568 */         new String(
/* 2569 */         CharOperation.concat(
/* 2570 */         currentMethod.declaringClass.sourceName(), 
/* 2571 */         currentMethod.readableName(), 
/* 2572 */         '.')), 
/* 2573 */         new String(
/* 2574 */         CharOperation.concat(
/* 2575 */         inheritedMethod.declaringClass.readableName(), 
/* 2576 */         inheritedMethod.readableName(), 
/* 2577 */         '.')) }, 
/* 2578 */         new String[] { 
/* 2579 */         new String(exceptionType.sourceName()), 
/* 2580 */         new String(
/* 2581 */         CharOperation.concat(
/* 2582 */         currentMethod.declaringClass.sourceName(), 
/* 2583 */         currentMethod.shortReadableName(), 
/* 2584 */         '.')), 
/* 2585 */         new String(
/* 2586 */         CharOperation.concat(
/* 2587 */         inheritedMethod.declaringClass.shortReadableName(), 
/* 2588 */         inheritedMethod.shortReadableName(), 
/* 2589 */         '.')) }, 
/* 2590 */         type.sourceStart(), 
/* 2591 */         type.sourceEnd());
/*      */     }
/*      */   }
/*      */   public void incompatibleReturnType(MethodBinding currentMethod, MethodBinding inheritedMethod) {
/* 2594 */     StringBuffer methodSignature = new StringBuffer();
/* 2595 */     methodSignature
/* 2596 */       .append(inheritedMethod.declaringClass.readableName())
/* 2597 */       .append('.')
/* 2598 */       .append(inheritedMethod.readableName());
/*      */ 
/* 2600 */     StringBuffer shortSignature = new StringBuffer();
/* 2601 */     shortSignature
/* 2602 */       .append(inheritedMethod.declaringClass.shortReadableName())
/* 2603 */       .append('.')
/* 2604 */       .append(inheritedMethod.shortReadableName());
/*      */ 
/* 2607 */     ReferenceBinding declaringClass = currentMethod.declaringClass;
/*      */     int id;
/*      */     int id;
/* 2608 */     if ((declaringClass.isInterface()) && 
/* 2609 */       (!inheritedMethod.isPublic()))
/* 2610 */       id = 67109277;
/*      */     else {
/* 2612 */       id = 67109268;
/*      */     }
/* 2614 */     AbstractMethodDeclaration method = currentMethod.sourceMethod();
/* 2615 */     int sourceStart = 0;
/* 2616 */     int sourceEnd = 0;
/* 2617 */     if (method == null) {
/* 2618 */       if ((declaringClass instanceof SourceTypeBinding)) {
/* 2619 */         SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)declaringClass;
/* 2620 */         sourceStart = sourceTypeBinding.sourceStart();
/* 2621 */         sourceEnd = sourceTypeBinding.sourceEnd();
/*      */       }
/* 2623 */     } else if (method.isConstructor()) {
/* 2624 */       sourceStart = method.sourceStart;
/* 2625 */       sourceEnd = method.sourceEnd;
/*      */     } else {
/* 2627 */       TypeReference returnType = ((MethodDeclaration)method).returnType;
/* 2628 */       sourceStart = returnType.sourceStart;
/* 2629 */       if ((returnType instanceof ParameterizedSingleTypeReference)) {
/* 2630 */         ParameterizedSingleTypeReference typeReference = (ParameterizedSingleTypeReference)returnType;
/* 2631 */         TypeReference[] typeArguments = typeReference.typeArguments;
/* 2632 */         if (typeArguments[(typeArguments.length - 1)].sourceEnd > typeReference.sourceEnd)
/* 2633 */           sourceEnd = retrieveClosingAngleBracketPosition(typeReference.sourceEnd);
/*      */         else
/* 2635 */           sourceEnd = returnType.sourceEnd;
/*      */       }
/* 2637 */       else if ((returnType instanceof ParameterizedQualifiedTypeReference)) {
/* 2638 */         ParameterizedQualifiedTypeReference typeReference = (ParameterizedQualifiedTypeReference)returnType;
/* 2639 */         sourceEnd = retrieveClosingAngleBracketPosition(typeReference.sourceEnd);
/*      */       } else {
/* 2641 */         sourceEnd = returnType.sourceEnd;
/*      */       }
/*      */     }
/* 2644 */     handle(
/* 2645 */       id, 
/* 2646 */       new String[] { methodSignature.toString() }, 
/* 2647 */       new String[] { shortSignature.toString() }, 
/* 2648 */       sourceStart, 
/* 2649 */       sourceEnd);
/*      */   }
/*      */   public void incorrectArityForParameterizedType(ASTNode location, TypeBinding type, TypeBinding[] argumentTypes) {
/* 2652 */     if (location == null) {
/* 2653 */       handle(
/* 2654 */         16777741, 
/* 2655 */         new String[] { new String(type.readableName()), typesAsString(false, argumentTypes, false) }, 
/* 2656 */         new String[] { new String(type.shortReadableName()), typesAsString(false, argumentTypes, true) }, 
/* 2657 */         131, 
/* 2658 */         0, 
/* 2659 */         0);
/* 2660 */       return;
/*      */     }
/* 2662 */     handle(
/* 2663 */       16777741, 
/* 2664 */       new String[] { new String(type.readableName()), typesAsString(false, argumentTypes, false) }, 
/* 2665 */       new String[] { new String(type.shortReadableName()), typesAsString(false, argumentTypes, true) }, 
/* 2666 */       location.sourceStart, 
/* 2667 */       location.sourceEnd);
/*      */   }
/*      */   public void incorrectLocationForNonEmptyDimension(ArrayAllocationExpression expression, int index) {
/* 2670 */     handle(
/* 2671 */       536871114, 
/* 2672 */       NoArgument, 
/* 2673 */       NoArgument, 
/* 2674 */       expression.dimensions[index].sourceStart, 
/* 2675 */       expression.dimensions[index].sourceEnd);
/*      */   }
/*      */   public void incorrectSwitchType(Expression expression, TypeBinding testType) {
/* 2678 */     handle(
/* 2679 */       16777385, 
/* 2680 */       new String[] { new String(testType.readableName()) }, 
/* 2681 */       new String[] { new String(testType.shortReadableName()) }, 
/* 2682 */       expression.sourceStart, 
/* 2683 */       expression.sourceEnd);
/*      */   }
/*      */   public void indirectAccessToStaticField(ASTNode location, FieldBinding field) {
/* 2686 */     int severity = computeSeverity(570425422);
/* 2687 */     if (severity == -1) return;
/* 2688 */     handle(
/* 2689 */       570425422, 
/* 2690 */       new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 2691 */       new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 2692 */       severity, 
/* 2693 */       nodeSourceStart(field, location), 
/* 2694 */       nodeSourceEnd(field, location));
/*      */   }
/*      */   public void indirectAccessToStaticMethod(ASTNode location, MethodBinding method) {
/* 2697 */     int severity = computeSeverity(603979895);
/* 2698 */     if (severity == -1) return;
/* 2699 */     handle(
/* 2700 */       603979895, 
/* 2701 */       new String[] { new String(method.declaringClass.readableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 2702 */       new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 2703 */       severity, 
/* 2704 */       location.sourceStart, 
/* 2705 */       location.sourceEnd);
/*      */   }
/*      */   public void inheritedMethodReducesVisibility(SourceTypeBinding type, MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
/* 2708 */     StringBuffer concreteSignature = new StringBuffer();
/* 2709 */     concreteSignature
/* 2710 */       .append(concreteMethod.declaringClass.readableName())
/* 2711 */       .append('.')
/* 2712 */       .append(concreteMethod.readableName());
/* 2713 */     StringBuffer shortSignature = new StringBuffer();
/* 2714 */     shortSignature
/* 2715 */       .append(concreteMethod.declaringClass.shortReadableName())
/* 2716 */       .append('.')
/* 2717 */       .append(concreteMethod.shortReadableName());
/* 2718 */     handle(
/* 2720 */       67109269, 
/* 2721 */       new String[] { 
/* 2722 */       concreteSignature.toString(), 
/* 2723 */       new String(abstractMethods[0].declaringClass.readableName()) }, 
/* 2724 */       new String[] { 
/* 2725 */       shortSignature.toString(), 
/* 2726 */       new String(abstractMethods[0].declaringClass.shortReadableName()) }, 
/* 2727 */       type.sourceStart(), 
/* 2728 */       type.sourceEnd());
/*      */   }
/*      */   public void inheritedMethodsHaveIncompatibleReturnTypes(ASTNode location, MethodBinding[] inheritedMethods, int length) {
/* 2731 */     StringBuffer methodSignatures = new StringBuffer();
/* 2732 */     StringBuffer shortSignatures = new StringBuffer();
/* 2733 */     int i = length;
/*      */     do { methodSignatures
/* 2735 */         .append(inheritedMethods[i].declaringClass.readableName())
/* 2736 */         .append('.')
/* 2737 */         .append(inheritedMethods[i].readableName());
/* 2738 */       shortSignatures
/* 2739 */         .append(inheritedMethods[i].declaringClass.shortReadableName())
/* 2740 */         .append('.')
/* 2741 */         .append(inheritedMethods[i].shortReadableName());
/* 2742 */       if (i != 0) {
/* 2743 */         methodSignatures.append(", ");
/* 2744 */         shortSignatures.append(", ");
/*      */       }
/* 2733 */       i--; } while (i >= 0);
/*      */ 
/* 2748 */     handle(
/* 2751 */       67109283, 
/* 2752 */       new String[] { methodSignatures.toString() }, 
/* 2753 */       new String[] { shortSignatures.toString() }, 
/* 2754 */       location.sourceStart, 
/* 2755 */       location.sourceEnd);
/*      */   }
/*      */   public void inheritedMethodsHaveIncompatibleReturnTypes(SourceTypeBinding type, MethodBinding[] inheritedMethods, int length) {
/* 2758 */     StringBuffer methodSignatures = new StringBuffer();
/* 2759 */     StringBuffer shortSignatures = new StringBuffer();
/* 2760 */     int i = length;
/*      */     do { methodSignatures
/* 2762 */         .append(inheritedMethods[i].declaringClass.readableName())
/* 2763 */         .append('.')
/* 2764 */         .append(inheritedMethods[i].readableName());
/* 2765 */       shortSignatures
/* 2766 */         .append(inheritedMethods[i].declaringClass.shortReadableName())
/* 2767 */         .append('.')
/* 2768 */         .append(inheritedMethods[i].shortReadableName());
/* 2769 */       if (i != 0) {
/* 2770 */         methodSignatures.append(", ");
/* 2771 */         shortSignatures.append(", ");
/*      */       }
/* 2760 */       i--; } while (i >= 0);
/*      */ 
/* 2775 */     handle(
/* 2778 */       67109283, 
/* 2779 */       new String[] { methodSignatures.toString() }, 
/* 2780 */       new String[] { shortSignatures.toString() }, 
/* 2781 */       type.sourceStart(), 
/* 2782 */       type.sourceEnd());
/*      */   }
/*      */   public void inheritedMethodsHaveNameClash(SourceTypeBinding type, MethodBinding oneMethod, MethodBinding twoMethod) {
/* 2785 */     handle(
/* 2786 */       67109424, 
/* 2787 */       new String[] { 
/* 2788 */       new String(oneMethod.selector), 
/* 2789 */       typesAsString(oneMethod.original().isVarargs(), oneMethod.original().parameters, false), 
/* 2790 */       new String(oneMethod.declaringClass.readableName()), 
/* 2791 */       typesAsString(twoMethod.original().isVarargs(), twoMethod.original().parameters, false), 
/* 2792 */       new String(twoMethod.declaringClass.readableName()) }, 
/* 2794 */       new String[] { 
/* 2795 */       new String(oneMethod.selector), 
/* 2796 */       typesAsString(oneMethod.original().isVarargs(), oneMethod.original().parameters, true), 
/* 2797 */       new String(oneMethod.declaringClass.shortReadableName()), 
/* 2798 */       typesAsString(twoMethod.original().isVarargs(), twoMethod.original().parameters, true), 
/* 2799 */       new String(twoMethod.declaringClass.shortReadableName()) }, 
/* 2801 */       type.sourceStart(), 
/* 2802 */       type.sourceEnd());
/*      */   }
/*      */   public void initializerMustCompleteNormally(FieldDeclaration fieldDecl) {
/* 2805 */     handle(
/* 2806 */       536871075, 
/* 2807 */       NoArgument, 
/* 2808 */       NoArgument, 
/* 2809 */       fieldDecl.sourceStart, 
/* 2810 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void innerTypesCannotDeclareStaticInitializers(ReferenceBinding innerType, Initializer initializer) {
/* 2813 */     handle(
/* 2814 */       536870936, 
/* 2815 */       new String[] { new String(innerType.readableName()) }, 
/* 2816 */       new String[] { new String(innerType.shortReadableName()) }, 
/* 2817 */       initializer.sourceStart, 
/* 2818 */       initializer.sourceStart);
/*      */   }
/*      */   public void interfaceCannotHaveConstructors(ConstructorDeclaration constructor) {
/* 2821 */     handle(
/* 2822 */       1610612943, 
/* 2823 */       NoArgument, 
/* 2824 */       NoArgument, 
/* 2825 */       constructor.sourceStart, 
/* 2826 */       constructor.sourceEnd, 
/* 2827 */       constructor, 
/* 2828 */       constructor.compilationResult());
/*      */   }
/*      */   public void interfaceCannotHaveInitializers(SourceTypeBinding type, FieldDeclaration fieldDecl) {
/* 2831 */     String[] arguments = { new String(type.sourceName()) };
/*      */ 
/* 2833 */     handle(
/* 2834 */       16777516, 
/* 2835 */       arguments, 
/* 2836 */       arguments, 
/* 2837 */       fieldDecl.sourceStart, 
/* 2838 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void invalidAnnotationMemberType(MethodDeclaration methodDecl) {
/* 2841 */     handle(
/* 2842 */       16777821, 
/* 2843 */       new String[] { 
/* 2844 */       new String(methodDecl.binding.returnType.readableName()), 
/* 2845 */       new String(methodDecl.selector), 
/* 2846 */       new String(methodDecl.binding.declaringClass.readableName()) }, 
/* 2848 */       new String[] { 
/* 2849 */       new String(methodDecl.binding.returnType.shortReadableName()), 
/* 2850 */       new String(methodDecl.selector), 
/* 2851 */       new String(methodDecl.binding.declaringClass.shortReadableName()) }, 
/* 2853 */       methodDecl.returnType.sourceStart, 
/* 2854 */       methodDecl.returnType.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void invalidBreak(ASTNode location) {
/* 2858 */     handle(
/* 2859 */       536871084, 
/* 2860 */       NoArgument, 
/* 2861 */       NoArgument, 
/* 2862 */       location.sourceStart, 
/* 2863 */       location.sourceEnd);
/*      */   }
/*      */   public void invalidConstructor(Statement statement, MethodBinding targetConstructor) {
/* 2866 */     boolean insideDefaultConstructor = 
/* 2867 */       ((this.referenceContext instanceof ConstructorDeclaration)) && 
/* 2868 */       (((ConstructorDeclaration)this.referenceContext).isDefaultConstructor());
/* 2869 */     boolean insideImplicitConstructorCall = 
/* 2870 */       ((statement instanceof ExplicitConstructorCall)) && 
/* 2871 */       (((ExplicitConstructorCall)statement).accessMode == 1);
/*      */ 
/* 2873 */     int sourceStart = statement.sourceStart;
/* 2874 */     int sourceEnd = statement.sourceEnd;
/* 2875 */     if ((statement instanceof AllocationExpression)) {
/* 2876 */       AllocationExpression allocation = (AllocationExpression)statement;
/* 2877 */       if (allocation.enumConstant != null) {
/* 2878 */         sourceStart = allocation.enumConstant.sourceStart;
/* 2879 */         sourceEnd = allocation.enumConstant.sourceEnd;
/*      */       }
/*      */     }
/*      */ 
/* 2883 */     int id = 134217858;
/* 2884 */     MethodBinding shownConstructor = targetConstructor;
/* 2885 */     switch (targetConstructor.problemId()) {
/*      */     case 1:
/* 2887 */       ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 2888 */       if ((problemConstructor.closestMatch != null) && 
/* 2889 */         ((problemConstructor.closestMatch.tagBits & 0x80) != 0L)) {
/* 2890 */         missingTypeInConstructor(statement, problemConstructor.closestMatch);
/* 2891 */         return;
/*      */       }
/*      */ 
/* 2895 */       if (insideDefaultConstructor)
/* 2896 */         id = 134217868;
/* 2897 */       else if (insideImplicitConstructorCall)
/* 2898 */         id = 134217871;
/*      */       else {
/* 2900 */         id = 134217858;
/*      */       }
/* 2902 */       break;
/*      */     case 2:
/* 2904 */       if (insideDefaultConstructor)
/* 2905 */         id = 134217869;
/* 2906 */       else if (insideImplicitConstructorCall)
/* 2907 */         id = 134217872;
/*      */       else {
/* 2909 */         id = 134217859;
/*      */       }
/* 2911 */       ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 2912 */       if (problemConstructor.closestMatch == null) break;
/* 2913 */       shownConstructor = problemConstructor.closestMatch.original();
/*      */ 
/* 2915 */       break;
/*      */     case 3:
/* 2917 */       if (insideDefaultConstructor)
/* 2918 */         id = 134217870;
/* 2919 */       else if (insideImplicitConstructorCall)
/* 2920 */         id = 134217873;
/*      */       else {
/* 2922 */         id = 134217860;
/*      */       }
/* 2924 */       break;
/*      */     case 10:
/* 2926 */       ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 2927 */       ParameterizedGenericMethodBinding substitutedConstructor = (ParameterizedGenericMethodBinding)problemConstructor.closestMatch;
/* 2928 */       shownConstructor = substitutedConstructor.original();
/* 2929 */       int augmentedLength = problemConstructor.parameters.length;
/* 2930 */       TypeBinding inferredTypeArgument = problemConstructor.parameters[(augmentedLength - 2)];
/* 2931 */       TypeVariableBinding typeParameter = (TypeVariableBinding)problemConstructor.parameters[(augmentedLength - 1)];
/* 2932 */       TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
/* 2933 */       System.arraycopy(problemConstructor.parameters, 0, invocationArguments, 0, augmentedLength - 2);
/* 2934 */       handle(
/* 2935 */         16777760, 
/* 2936 */         new String[] { 
/* 2937 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 2938 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 2939 */         new String(shownConstructor.declaringClass.readableName()), 
/* 2940 */         typesAsString(false, invocationArguments, false), 
/* 2941 */         new String(inferredTypeArgument.readableName()), 
/* 2942 */         new String(typeParameter.sourceName), 
/* 2943 */         parameterBoundAsString(typeParameter, false) }, 
/* 2944 */         new String[] { 
/* 2945 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 2946 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 2947 */         new String(shownConstructor.declaringClass.shortReadableName()), 
/* 2948 */         typesAsString(false, invocationArguments, true), 
/* 2949 */         new String(inferredTypeArgument.shortReadableName()), 
/* 2950 */         new String(typeParameter.sourceName), 
/* 2951 */         parameterBoundAsString(typeParameter, true) }, 
/* 2952 */         sourceStart, 
/* 2953 */         sourceEnd);
/* 2954 */       return;
/*      */     case 11:
/* 2957 */       ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 2958 */       shownConstructor = problemConstructor.closestMatch;
/* 2959 */       if (shownConstructor.typeVariables == Binding.NO_TYPE_VARIABLES)
/* 2960 */         handle(
/* 2961 */           16777767, 
/* 2962 */           new String[] { 
/* 2963 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 2964 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 2965 */           new String(shownConstructor.declaringClass.readableName()), 
/* 2966 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 2967 */           new String[] { 
/* 2968 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 2969 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 2970 */           new String(shownConstructor.declaringClass.shortReadableName()), 
/* 2971 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 2972 */           sourceStart, 
/* 2973 */           sourceEnd);
/*      */       else {
/* 2975 */         handle(
/* 2976 */           16777768, 
/* 2977 */           new String[] { 
/* 2978 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 2979 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 2980 */           new String(shownConstructor.declaringClass.readableName()), 
/* 2981 */           typesAsString(false, shownConstructor.typeVariables, false), 
/* 2982 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 2983 */           new String[] { 
/* 2984 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 2985 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 2986 */           new String(shownConstructor.declaringClass.shortReadableName()), 
/* 2987 */           typesAsString(false, shownConstructor.typeVariables, true), 
/* 2988 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 2989 */           sourceStart, 
/* 2990 */           sourceEnd);
/*      */       }
/* 2992 */       return;
/*      */     case 12:
/* 2994 */       ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 2995 */       shownConstructor = problemConstructor.closestMatch;
/* 2996 */       handle(
/* 2997 */         16777769, 
/* 2998 */         new String[] { 
/* 2999 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 3000 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 3001 */         new String(shownConstructor.declaringClass.readableName()), 
/* 3002 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, false), 
/* 3003 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 3004 */         new String[] { 
/* 3005 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 3006 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 3007 */         new String(shownConstructor.declaringClass.shortReadableName()), 
/* 3008 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, true), 
/* 3009 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 3010 */         sourceStart, 
/* 3011 */         sourceEnd);
/* 3012 */       return;
/*      */     case 13:
/* 3014 */       ProblemMethodBinding problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 3015 */       shownConstructor = problemConstructor.closestMatch;
/* 3016 */       handle(
/* 3017 */         16777771, 
/* 3018 */         new String[] { 
/* 3019 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 3020 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 3021 */         new String(shownConstructor.declaringClass.readableName()), 
/* 3022 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 3023 */         new String[] { 
/* 3024 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 3025 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 3026 */         new String(shownConstructor.declaringClass.shortReadableName()), 
/* 3027 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 3028 */         sourceStart, 
/* 3029 */         sourceEnd);
/* 3030 */       return;
/*      */     case 0:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     default:
/* 3033 */       needImplementation(statement);
/*      */     }
/*      */ 
/* 3037 */     handle(
/* 3038 */       id, 
/* 3039 */       new String[] { new String(targetConstructor.declaringClass.readableName()), typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false) }, 
/* 3040 */       new String[] { new String(targetConstructor.declaringClass.shortReadableName()), typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true) }, 
/* 3041 */       sourceStart, 
/* 3042 */       sourceEnd);
/*      */   }
/*      */   public void invalidContinue(ASTNode location) {
/* 3045 */     handle(
/* 3046 */       536871085, 
/* 3047 */       NoArgument, 
/* 3048 */       NoArgument, 
/* 3049 */       location.sourceStart, 
/* 3050 */       location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void invalidEnclosingType(Expression expression, TypeBinding type, ReferenceBinding enclosingType) {
/* 3054 */     if (enclosingType.isAnonymousType()) enclosingType = enclosingType.superclass();
/* 3055 */     if ((enclosingType.sourceName != null) && (enclosingType.sourceName.length == 0)) return;
/*      */ 
/* 3057 */     int flag = 16777218;
/* 3058 */     switch (type.problemId()) {
/*      */     case 1:
/* 3060 */       flag = 16777218;
/* 3061 */       break;
/*      */     case 2:
/* 3063 */       flag = 16777219;
/* 3064 */       break;
/*      */     case 3:
/* 3066 */       flag = 16777220;
/* 3067 */       break;
/*      */     case 4:
/* 3069 */       flag = 16777222;
/* 3070 */       break;
/*      */     case 0:
/*      */     default:
/* 3073 */       needImplementation(expression);
/*      */     }
/*      */ 
/* 3077 */     handle(
/* 3078 */       flag, 
/* 3079 */       new String[] { new String(enclosingType.readableName()) + "." + new String(type.readableName()) }, 
/* 3080 */       new String[] { new String(enclosingType.shortReadableName()) + "." + new String(type.shortReadableName()) }, 
/* 3081 */       expression.sourceStart, 
/* 3082 */       expression.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void invalidExplicitConstructorCall(ASTNode location) {
/* 3086 */     handle(
/* 3087 */       1207959691, 
/* 3088 */       NoArgument, 
/* 3089 */       NoArgument, 
/* 3090 */       location.sourceStart, 
/* 3091 */       location.sourceEnd);
/*      */   }
/*      */   public void invalidExpressionAsStatement(Expression expression) {
/* 3094 */     handle(
/* 3095 */       1610612958, 
/* 3096 */       NoArgument, 
/* 3097 */       NoArgument, 
/* 3098 */       expression.sourceStart, 
/* 3099 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidField(FieldReference fieldRef, TypeBinding searchedType) {
/* 3102 */     if (isRecoveredName(fieldRef.token)) return;
/*      */ 
/* 3104 */     int id = 33554502;
/* 3105 */     FieldBinding field = fieldRef.binding;
/* 3106 */     switch (field.problemId()) {
/*      */     case 1:
/* 3108 */       if ((searchedType.tagBits & 0x80) != 0L) {
/* 3109 */         handle(
/* 3110 */           16777218, 
/* 3111 */           new String[] { new String(searchedType.leafComponentType().readableName()) }, 
/* 3112 */           new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, 
/* 3113 */           fieldRef.receiver.sourceStart, 
/* 3114 */           fieldRef.receiver.sourceEnd);
/* 3115 */         return;
/*      */       }
/* 3117 */       id = 33554502;
/*      */ 
/* 3122 */       break;
/*      */     case 2:
/* 3124 */       handle(
/* 3125 */         33554503, 
/* 3126 */         new String[] { new String(fieldRef.token), new String(field.declaringClass.readableName()) }, 
/* 3127 */         new String[] { new String(fieldRef.token), new String(field.declaringClass.shortReadableName()) }, 
/* 3128 */         nodeSourceStart(field, fieldRef), 
/* 3129 */         nodeSourceEnd(field, fieldRef));
/* 3130 */       return;
/*      */     case 3:
/* 3132 */       id = 33554504;
/* 3133 */       break;
/*      */     case 7:
/* 3135 */       id = 33554506;
/* 3136 */       break;
/*      */     case 6:
/* 3138 */       id = 134217863;
/* 3139 */       break;
/*      */     case 5:
/* 3141 */       id = 33554628;
/* 3142 */       break;
/*      */     case 8:
/* 3144 */       handle(
/* 3145 */         16777219, 
/* 3146 */         new String[] { new String(searchedType.leafComponentType().readableName()) }, 
/* 3147 */         new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, 
/* 3148 */         fieldRef.receiver.sourceStart, 
/* 3149 */         fieldRef.receiver.sourceEnd);
/* 3150 */       return;
/*      */     case 0:
/*      */     case 4:
/*      */     default:
/* 3154 */       needImplementation(fieldRef);
/*      */     }
/*      */ 
/* 3158 */     String[] arguments = { 
/* 3158 */       new String(field.readableName()) };
/* 3159 */     handle(
/* 3160 */       id, 
/* 3161 */       arguments, 
/* 3162 */       arguments, 
/* 3163 */       nodeSourceStart(field, fieldRef), 
/* 3164 */       nodeSourceEnd(field, fieldRef));
/*      */   }
/*      */   public void invalidField(NameReference nameRef, FieldBinding field) {
/* 3167 */     if ((nameRef instanceof QualifiedNameReference)) {
/* 3168 */       QualifiedNameReference ref = (QualifiedNameReference)nameRef;
/* 3169 */       if (isRecoveredName(ref.tokens)) return; 
/*      */     }
/*      */     else {
/* 3171 */       SingleNameReference ref = (SingleNameReference)nameRef;
/* 3172 */       if (isRecoveredName(ref.token)) return;
/*      */     }
/* 3174 */     int id = 33554502;
/* 3175 */     switch (field.problemId()) {
/*      */     case 1:
/* 3177 */       TypeBinding declaringClass = field.declaringClass;
/* 3178 */       if ((declaringClass != null) && ((declaringClass.tagBits & 0x80) != 0L)) {
/* 3179 */         handle(
/* 3180 */           16777218, 
/* 3181 */           new String[] { new String(field.declaringClass.readableName()) }, 
/* 3182 */           new String[] { new String(field.declaringClass.shortReadableName()) }, 
/* 3183 */           nameRef.sourceStart, 
/* 3184 */           nameRef.sourceEnd);
/* 3185 */         return;
/*      */       }
/* 3187 */       id = 33554502;
/* 3188 */       break;
/*      */     case 2:
/* 3190 */       char[] name = field.readableName();
/* 3191 */       name = CharOperation.lastSegment(name, '.');
/* 3192 */       handle(
/* 3193 */         33554503, 
/* 3194 */         new String[] { new String(name), new String(field.declaringClass.readableName()) }, 
/* 3195 */         new String[] { new String(name), new String(field.declaringClass.shortReadableName()) }, 
/* 3196 */         nodeSourceStart(field, nameRef), 
/* 3197 */         nodeSourceEnd(field, nameRef));
/* 3198 */       return;
/*      */     case 3:
/* 3200 */       id = 33554504;
/* 3201 */       break;
/*      */     case 7:
/* 3203 */       id = 33554506;
/* 3204 */       break;
/*      */     case 6:
/* 3206 */       id = 134217863;
/* 3207 */       break;
/*      */     case 5:
/* 3209 */       id = 33554628;
/* 3210 */       break;
/*      */     case 8:
/* 3212 */       handle(
/* 3213 */         16777219, 
/* 3214 */         new String[] { new String(field.declaringClass.readableName()) }, 
/* 3215 */         new String[] { new String(field.declaringClass.shortReadableName()) }, 
/* 3216 */         nameRef.sourceStart, 
/* 3217 */         nameRef.sourceEnd);
/* 3218 */       return;
/*      */     case 0:
/*      */     case 4:
/*      */     default:
/* 3221 */       needImplementation(nameRef);
/*      */     }
/*      */ 
/* 3224 */     String[] arguments = { 
/* 3224 */       new String(field.readableName()) };
/* 3225 */     handle(
/* 3226 */       id, 
/* 3227 */       arguments, 
/* 3228 */       arguments, 
/* 3229 */       nameRef.sourceStart, 
/* 3230 */       nameRef.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void invalidField(QualifiedNameReference nameRef, FieldBinding field, int index, TypeBinding searchedType)
/*      */   {
/* 3241 */     if (isRecoveredName(nameRef.tokens)) return;
/*      */ 
/* 3243 */     if (searchedType.isBaseType()) {
/* 3244 */       handle(
/* 3245 */         33554653, 
/* 3246 */         new String[] { 
/* 3247 */         new String(searchedType.readableName()), 
/* 3248 */         CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index)), 
/* 3249 */         new String(nameRef.tokens[index]) }, 
/* 3250 */         new String[] { 
/* 3251 */         new String(searchedType.sourceName()), 
/* 3252 */         CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index)), 
/* 3253 */         new String(nameRef.tokens[index]) }, 
/* 3254 */         nameRef.sourceStart, 
/* 3255 */         (int)nameRef.sourcePositions[index]);
/* 3256 */       return;
/*      */     }
/*      */ 
/* 3259 */     int id = 33554502;
/* 3260 */     switch (field.problemId()) {
/*      */     case 1:
/* 3262 */       if ((searchedType.tagBits & 0x80) != 0L) {
/* 3263 */         handle(
/* 3264 */           16777218, 
/* 3265 */           new String[] { new String(searchedType.leafComponentType().readableName()) }, 
/* 3266 */           new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, 
/* 3267 */           nameRef.sourceStart, 
/* 3268 */           (int)nameRef.sourcePositions[(index - 1)]);
/* 3269 */         return;
/*      */       }
/* 3271 */       id = 33554502;
/*      */ 
/* 3276 */       break;
/*      */     case 2:
/* 3278 */       String fieldName = new String(nameRef.tokens[index]);
/* 3279 */       handle(
/* 3280 */         33554503, 
/* 3281 */         new String[] { fieldName, new String(field.declaringClass.readableName()) }, 
/* 3282 */         new String[] { fieldName, new String(field.declaringClass.shortReadableName()) }, 
/* 3283 */         nodeSourceStart(field, nameRef), 
/* 3284 */         nodeSourceEnd(field, nameRef));
/* 3285 */       return;
/*      */     case 3:
/* 3287 */       id = 33554504;
/* 3288 */       break;
/*      */     case 7:
/* 3290 */       id = 33554506;
/* 3291 */       break;
/*      */     case 6:
/* 3293 */       id = 134217863;
/* 3294 */       break;
/*      */     case 5:
/* 3296 */       id = 33554628;
/* 3297 */       break;
/*      */     case 8:
/* 3299 */       handle(
/* 3300 */         16777219, 
/* 3301 */         new String[] { new String(searchedType.leafComponentType().readableName()) }, 
/* 3302 */         new String[] { new String(searchedType.leafComponentType().shortReadableName()) }, 
/* 3303 */         nameRef.sourceStart, 
/* 3304 */         (int)nameRef.sourcePositions[(index - 1)]);
/* 3305 */       return;
/*      */     case 0:
/*      */     case 4:
/*      */     default:
/* 3308 */       needImplementation(nameRef);
/*      */     }
/*      */ 
/* 3311 */     String[] arguments = { 
/* 3311 */       CharOperation.toString(CharOperation.subarray(nameRef.tokens, 0, index + 1)) };
/* 3312 */     handle(
/* 3313 */       id, 
/* 3314 */       arguments, 
/* 3315 */       arguments, 
/* 3316 */       nameRef.sourceStart, 
/* 3317 */       (int)nameRef.sourcePositions[index]);
/*      */   }
/*      */ 
/*      */   public void invalidFileNameForPackageAnnotations(Annotation annotation) {
/* 3321 */     handle(
/* 3322 */       1610613338, 
/* 3323 */       NoArgument, 
/* 3324 */       NoArgument, 
/* 3325 */       annotation.sourceStart, 
/* 3326 */       annotation.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void invalidMethod(MessageSend messageSend, MethodBinding method) {
/* 3330 */     if (isRecoveredName(messageSend.selector)) return;
/*      */ 
/* 3332 */     int id = 67108964;
/* 3333 */     MethodBinding shownMethod = method;
/* 3334 */     switch (method.problemId()) {
/*      */     case 1:
/* 3336 */       if ((method.declaringClass.tagBits & 0x80) != 0L) {
/* 3337 */         handle(
/* 3338 */           16777218, 
/* 3339 */           new String[] { new String(method.declaringClass.readableName()) }, 
/* 3340 */           new String[] { new String(method.declaringClass.shortReadableName()) }, 
/* 3341 */           messageSend.receiver.sourceStart, 
/* 3342 */           messageSend.receiver.sourceEnd);
/* 3343 */         return;
/*      */       }
/* 3345 */       id = 67108964;
/* 3346 */       ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
/* 3347 */       if (problemMethod.closestMatch == null) break;
/* 3348 */       shownMethod = problemMethod.closestMatch;
/* 3349 */       if ((shownMethod.tagBits & 0x80) != 0L) {
/* 3350 */         missingTypeInMethod(messageSend, shownMethod);
/* 3351 */         return;
/*      */       }
/* 3353 */       String closestParameterTypeNames = typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false);
/* 3354 */       String parameterTypeNames = typesAsString(false, problemMethod.parameters, false);
/* 3355 */       String closestParameterTypeShortNames = typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true);
/* 3356 */       String parameterTypeShortNames = typesAsString(false, problemMethod.parameters, true);
/* 3357 */       handle(
/* 3358 */         67108979, 
/* 3359 */         new String[] { 
/* 3360 */         new String(shownMethod.declaringClass.readableName()), 
/* 3361 */         new String(shownMethod.selector), 
/* 3362 */         closestParameterTypeNames, 
/* 3363 */         parameterTypeNames }, 
/* 3365 */         new String[] { 
/* 3366 */         new String(shownMethod.declaringClass.shortReadableName()), 
/* 3367 */         new String(shownMethod.selector), 
/* 3368 */         closestParameterTypeShortNames, 
/* 3369 */         parameterTypeShortNames }, 
/* 3371 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 3372 */         (int)messageSend.nameSourcePosition);
/* 3373 */       return;
/*      */     case 2:
/* 3377 */       id = 67108965;
/* 3378 */       ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
/* 3379 */       if (problemMethod.closestMatch == null) break;
/* 3380 */       shownMethod = problemMethod.closestMatch.original();
/*      */ 
/* 3382 */       break;
/*      */     case 3:
/* 3384 */       id = 67108966;
/* 3385 */       break;
/*      */     case 5:
/* 3387 */       id = 67109059;
/* 3388 */       break;
/*      */     case 6:
/* 3390 */       id = 134217864;
/* 3391 */       break;
/*      */     case 7:
/* 3393 */       id = 603979977;
/* 3394 */       break;
/*      */     case 8:
/* 3396 */       handle(
/* 3397 */         16777219, 
/* 3398 */         new String[] { new String(method.declaringClass.readableName()) }, 
/* 3399 */         new String[] { new String(method.declaringClass.shortReadableName()) }, 
/* 3400 */         messageSend.receiver.sourceStart, 
/* 3401 */         messageSend.receiver.sourceEnd);
/* 3402 */       return;
/*      */     case 10:
/* 3404 */       ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
/* 3405 */       ParameterizedGenericMethodBinding substitutedMethod = (ParameterizedGenericMethodBinding)problemMethod.closestMatch;
/* 3406 */       shownMethod = substitutedMethod.original();
/* 3407 */       int augmentedLength = problemMethod.parameters.length;
/* 3408 */       TypeBinding inferredTypeArgument = problemMethod.parameters[(augmentedLength - 2)];
/* 3409 */       TypeVariableBinding typeParameter = (TypeVariableBinding)problemMethod.parameters[(augmentedLength - 1)];
/* 3410 */       TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
/* 3411 */       System.arraycopy(problemMethod.parameters, 0, invocationArguments, 0, augmentedLength - 2);
/* 3412 */       handle(
/* 3413 */         16777759, 
/* 3414 */         new String[] { 
/* 3415 */         new String(shownMethod.selector), 
/* 3416 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 3417 */         new String(shownMethod.declaringClass.readableName()), 
/* 3418 */         typesAsString(false, invocationArguments, false), 
/* 3419 */         new String(inferredTypeArgument.readableName()), 
/* 3420 */         new String(typeParameter.sourceName), 
/* 3421 */         parameterBoundAsString(typeParameter, false) }, 
/* 3422 */         new String[] { 
/* 3423 */         new String(shownMethod.selector), 
/* 3424 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 3425 */         new String(shownMethod.declaringClass.shortReadableName()), 
/* 3426 */         typesAsString(false, invocationArguments, true), 
/* 3427 */         new String(inferredTypeArgument.shortReadableName()), 
/* 3428 */         new String(typeParameter.sourceName), 
/* 3429 */         parameterBoundAsString(typeParameter, true) }, 
/* 3430 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 3431 */         (int)messageSend.nameSourcePosition);
/* 3432 */       return;
/*      */     case 11:
/* 3434 */       ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
/* 3435 */       shownMethod = problemMethod.closestMatch;
/* 3436 */       if (shownMethod.typeVariables == Binding.NO_TYPE_VARIABLES)
/* 3437 */         handle(
/* 3438 */           16777764, 
/* 3439 */           new String[] { 
/* 3440 */           new String(shownMethod.selector), 
/* 3441 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 3442 */           new String(shownMethod.declaringClass.readableName()), 
/* 3443 */           typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 3444 */           new String[] { 
/* 3445 */           new String(shownMethod.selector), 
/* 3446 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 3447 */           new String(shownMethod.declaringClass.shortReadableName()), 
/* 3448 */           typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 3449 */           (int)(messageSend.nameSourcePosition >>> 32), 
/* 3450 */           (int)messageSend.nameSourcePosition);
/*      */       else {
/* 3452 */         handle(
/* 3453 */           16777765, 
/* 3454 */           new String[] { 
/* 3455 */           new String(shownMethod.selector), 
/* 3456 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 3457 */           new String(shownMethod.declaringClass.readableName()), 
/* 3458 */           typesAsString(false, shownMethod.typeVariables, false), 
/* 3459 */           typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 3460 */           new String[] { 
/* 3461 */           new String(shownMethod.selector), 
/* 3462 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 3463 */           new String(shownMethod.declaringClass.shortReadableName()), 
/* 3464 */           typesAsString(false, shownMethod.typeVariables, true), 
/* 3465 */           typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 3466 */           (int)(messageSend.nameSourcePosition >>> 32), 
/* 3467 */           (int)messageSend.nameSourcePosition);
/*      */       }
/* 3469 */       return;
/*      */     case 12:
/* 3471 */       ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
/* 3472 */       shownMethod = problemMethod.closestMatch;
/* 3473 */       handle(
/* 3474 */         16777766, 
/* 3475 */         new String[] { 
/* 3476 */         new String(shownMethod.selector), 
/* 3477 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 3478 */         new String(shownMethod.declaringClass.readableName()), 
/* 3479 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownMethod).typeArguments, false), 
/* 3480 */         typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 3481 */         new String[] { 
/* 3482 */         new String(shownMethod.selector), 
/* 3483 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 3484 */         new String(shownMethod.declaringClass.shortReadableName()), 
/* 3485 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownMethod).typeArguments, true), 
/* 3486 */         typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 3487 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 3488 */         (int)messageSend.nameSourcePosition);
/* 3489 */       return;
/*      */     case 13:
/* 3491 */       ProblemMethodBinding problemMethod = (ProblemMethodBinding)method;
/* 3492 */       shownMethod = problemMethod.closestMatch;
/* 3493 */       handle(
/* 3494 */         16777770, 
/* 3495 */         new String[] { 
/* 3496 */         new String(shownMethod.selector), 
/* 3497 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 3498 */         new String(shownMethod.declaringClass.readableName()), 
/* 3499 */         typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 3500 */         new String[] { 
/* 3501 */         new String(shownMethod.selector), 
/* 3502 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 3503 */         new String(shownMethod.declaringClass.shortReadableName()), 
/* 3504 */         typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 3505 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 3506 */         (int)messageSend.nameSourcePosition);
/* 3507 */       return;
/*      */     case 0:
/*      */     case 4:
/*      */     case 9:
/*      */     default:
/* 3510 */       needImplementation(messageSend);
/*      */     }
/*      */ 
/* 3513 */     handle(
/* 3514 */       id, 
/* 3515 */       new String[] { 
/* 3516 */       new String(method.declaringClass.readableName()), 
/* 3517 */       new String(shownMethod.selector), typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false) }, 
/* 3518 */       new String[] { 
/* 3519 */       new String(method.declaringClass.shortReadableName()), 
/* 3520 */       new String(shownMethod.selector), typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true) }, 
/* 3521 */       (int)(messageSend.nameSourcePosition >>> 32), 
/* 3522 */       (int)messageSend.nameSourcePosition);
/*      */   }
/*      */   public void invalidNullToSynchronize(Expression expression) {
/* 3525 */     handle(
/* 3526 */       536871088, 
/* 3527 */       NoArgument, 
/* 3528 */       NoArgument, 
/* 3529 */       expression.sourceStart, 
/* 3530 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidOperator(BinaryExpression expression, TypeBinding leftType, TypeBinding rightType) {
/* 3533 */     String leftName = new String(leftType.readableName());
/* 3534 */     String rightName = new String(rightType.readableName());
/* 3535 */     String leftShortName = new String(leftType.shortReadableName());
/* 3536 */     String rightShortName = new String(rightType.shortReadableName());
/* 3537 */     if (leftShortName.equals(rightShortName)) {
/* 3538 */       leftShortName = leftName;
/* 3539 */       rightShortName = rightName;
/*      */     }
/* 3541 */     handle(
/* 3542 */       536871072, 
/* 3543 */       new String[] { 
/* 3544 */       expression.operatorToString(), 
/* 3545 */       leftName + ", " + rightName }, 
/* 3546 */       new String[] { 
/* 3547 */       expression.operatorToString(), 
/* 3548 */       leftShortName + ", " + rightShortName }, 
/* 3549 */       expression.sourceStart, 
/* 3550 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidOperator(CompoundAssignment assign, TypeBinding leftType, TypeBinding rightType) {
/* 3553 */     String leftName = new String(leftType.readableName());
/* 3554 */     String rightName = new String(rightType.readableName());
/* 3555 */     String leftShortName = new String(leftType.shortReadableName());
/* 3556 */     String rightShortName = new String(rightType.shortReadableName());
/* 3557 */     if (leftShortName.equals(rightShortName)) {
/* 3558 */       leftShortName = leftName;
/* 3559 */       rightShortName = rightName;
/*      */     }
/* 3561 */     handle(
/* 3562 */       536871072, 
/* 3563 */       new String[] { 
/* 3564 */       assign.operatorToString(), 
/* 3565 */       leftName + ", " + rightName }, 
/* 3566 */       new String[] { 
/* 3567 */       assign.operatorToString(), 
/* 3568 */       leftShortName + ", " + rightShortName }, 
/* 3569 */       assign.sourceStart, 
/* 3570 */       assign.sourceEnd);
/*      */   }
/*      */   public void invalidOperator(UnaryExpression expression, TypeBinding type) {
/* 3573 */     handle(
/* 3574 */       536871072, 
/* 3575 */       new String[] { expression.operatorToString(), new String(type.readableName()) }, 
/* 3576 */       new String[] { expression.operatorToString(), new String(type.shortReadableName()) }, 
/* 3577 */       expression.sourceStart, 
/* 3578 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidParameterizedExceptionType(TypeBinding exceptionType, ASTNode location) {
/* 3581 */     handle(
/* 3582 */       16777750, 
/* 3583 */       new String[] { new String(exceptionType.readableName()) }, 
/* 3584 */       new String[] { new String(exceptionType.shortReadableName()) }, 
/* 3585 */       location.sourceStart, 
/* 3586 */       location.sourceEnd);
/*      */   }
/*      */   public void invalidParenthesizedExpression(ASTNode reference) {
/* 3589 */     handle(
/* 3590 */       1610612961, 
/* 3591 */       NoArgument, 
/* 3592 */       NoArgument, 
/* 3593 */       reference.sourceStart, 
/* 3594 */       reference.sourceEnd);
/*      */   }
/*      */   public void invalidType(ASTNode location, TypeBinding type) {
/* 3597 */     if ((type instanceof ReferenceBinding)) {
/* 3598 */       if (isRecoveredName(((ReferenceBinding)type).compoundName)) return;
/*      */     }
/* 3600 */     else if ((type instanceof ArrayBinding)) {
/* 3601 */       TypeBinding leafType = ((ArrayBinding)type).leafComponentType;
/* 3602 */       if (((leafType instanceof ReferenceBinding)) && 
/* 3603 */         (isRecoveredName(((ReferenceBinding)leafType).compoundName))) return;
/*      */ 
/*      */     }
/*      */ 
/* 3607 */     if (type.isParameterizedType()) {
/* 3608 */       List missingTypes = type.collectMissingTypes(null);
/* 3609 */       if (missingTypes != null) {
/* 3610 */         for (Iterator iterator = missingTypes.iterator(); iterator.hasNext(); ) {
/* 3611 */           invalidType(location, (TypeBinding)iterator.next());
/*      */         }
/* 3613 */         return;
/*      */       }
/*      */     }
/* 3616 */     int id = 16777218;
/* 3617 */     switch (type.problemId()) {
/*      */     case 1:
/* 3619 */       id = 16777218;
/* 3620 */       break;
/*      */     case 2:
/* 3622 */       id = 16777219;
/* 3623 */       break;
/*      */     case 3:
/* 3625 */       id = 16777220;
/* 3626 */       break;
/*      */     case 4:
/* 3628 */       id = 16777222;
/* 3629 */       break;
/*      */     case 5:
/* 3631 */       id = 16777413;
/* 3632 */       break;
/*      */     case 7:
/* 3634 */       id = 536871434;
/* 3635 */       break;
/*      */     case 9:
/* 3637 */       id = 536871433;
/* 3638 */       break;
/*      */     case 0:
/*      */     case 6:
/*      */     case 8:
/*      */     default:
/* 3641 */       needImplementation(location);
/*      */     }
/*      */ 
/* 3645 */     int end = location.sourceEnd;
/*      */ 
/* 3646 */     if ((location instanceof QualifiedNameReference)) {
/* 3647 */       QualifiedNameReference ref = (QualifiedNameReference)location;
/* 3648 */       if (isRecoveredName(ref.tokens)) return;
/* 3649 */       if (ref.indexOfFirstFieldBinding >= 1)
/* 3650 */         end = (int)ref.sourcePositions[(ref.indexOfFirstFieldBinding - 1)];
/* 3651 */     } else if ((location instanceof ParameterizedQualifiedTypeReference))
/*      */     {
/* 3653 */       ParameterizedQualifiedTypeReference ref = (ParameterizedQualifiedTypeReference)location;
/* 3654 */       if (isRecoveredName(ref.tokens)) return;
/* 3655 */       if ((type instanceof ReferenceBinding)) {
/* 3656 */         char[][] name = ((ReferenceBinding)type).compoundName;
/* 3657 */         end = (int)ref.sourcePositions[(name.length - 1)];
/*      */       }
/* 3659 */     } else if ((location instanceof ArrayQualifiedTypeReference)) {
/* 3660 */       ArrayQualifiedTypeReference arrayQualifiedTypeReference = (ArrayQualifiedTypeReference)location;
/* 3661 */       if (isRecoveredName(arrayQualifiedTypeReference.tokens)) return;
/* 3662 */       TypeBinding leafType = type.leafComponentType();
/* 3663 */       if ((leafType instanceof ReferenceBinding)) {
/* 3664 */         char[][] name = ((ReferenceBinding)leafType).compoundName;
/* 3665 */         end = (int)arrayQualifiedTypeReference.sourcePositions[(name.length - 1)];
/*      */       } else {
/* 3667 */         long[] positions = arrayQualifiedTypeReference.sourcePositions;
/* 3668 */         end = (int)positions[(positions.length - 1)];
/*      */       }
/* 3670 */     } else if ((location instanceof QualifiedTypeReference)) {
/* 3671 */       QualifiedTypeReference ref = (QualifiedTypeReference)location;
/* 3672 */       if (isRecoveredName(ref.tokens)) return;
/* 3673 */       if ((type instanceof ReferenceBinding)) {
/* 3674 */         char[][] name = ((ReferenceBinding)type).compoundName;
/* 3675 */         if (name.length <= ref.sourcePositions.length)
/* 3676 */           end = (int)ref.sourcePositions[(name.length - 1)];
/*      */       }
/* 3678 */     } else if ((location instanceof ImportReference)) {
/* 3679 */       ImportReference ref = (ImportReference)location;
/* 3680 */       if (isRecoveredName(ref.tokens)) return;
/* 3681 */       if ((type instanceof ReferenceBinding)) {
/* 3682 */         char[][] name = ((ReferenceBinding)type).compoundName;
/* 3683 */         end = (int)ref.sourcePositions[(name.length - 1)];
/*      */       }
/* 3685 */     } else if ((location instanceof ArrayTypeReference)) {
/* 3686 */       ArrayTypeReference arrayTypeReference = (ArrayTypeReference)location;
/* 3687 */       if (isRecoveredName(arrayTypeReference.token)) return;
/* 3688 */       end = arrayTypeReference.originalSourceEnd;
/*      */     }
/* 3690 */     handle(
/* 3691 */       id, 
/* 3692 */       new String[] { new String(type.leafComponentType().readableName()) }, 
/* 3693 */       new String[] { new String(type.leafComponentType().shortReadableName()) }, 
/* 3694 */       location.sourceStart, 
/* 3695 */       end);
/*      */   }
/*      */   public void invalidTypeForCollection(Expression expression) {
/* 3698 */     handle(
/* 3699 */       536871493, 
/* 3700 */       NoArgument, 
/* 3701 */       NoArgument, 
/* 3702 */       expression.sourceStart, 
/* 3703 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidTypeReference(Expression expression) {
/* 3706 */     handle(
/* 3707 */       536871115, 
/* 3708 */       NoArgument, 
/* 3709 */       NoArgument, 
/* 3710 */       expression.sourceStart, 
/* 3711 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidTypeToSynchronize(Expression expression, TypeBinding type) {
/* 3714 */     handle(
/* 3715 */       536871087, 
/* 3716 */       new String[] { new String(type.readableName()) }, 
/* 3717 */       new String[] { new String(type.shortReadableName()) }, 
/* 3718 */       expression.sourceStart, 
/* 3719 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidTypeVariableAsException(TypeBinding exceptionType, ASTNode location) {
/* 3722 */     handle(
/* 3723 */       16777749, 
/* 3724 */       new String[] { new String(exceptionType.readableName()) }, 
/* 3725 */       new String[] { new String(exceptionType.shortReadableName()) }, 
/* 3726 */       location.sourceStart, 
/* 3727 */       location.sourceEnd);
/*      */   }
/*      */   public void invalidUnaryExpression(Expression expression) {
/* 3730 */     handle(
/* 3731 */       1610612942, 
/* 3732 */       NoArgument, 
/* 3733 */       NoArgument, 
/* 3734 */       expression.sourceStart, 
/* 3735 */       expression.sourceEnd);
/*      */   }
/*      */   public void invalidUsageOfAnnotation(Annotation annotation) {
/* 3738 */     handle(
/* 3739 */       1610613332, 
/* 3740 */       NoArgument, 
/* 3741 */       NoArgument, 
/* 3742 */       annotation.sourceStart, 
/* 3743 */       annotation.sourceEnd);
/*      */   }
/*      */   public void invalidUsageOfAnnotationDeclarations(TypeDeclaration annotationTypeDeclaration) {
/* 3746 */     handle(
/* 3747 */       1610613333, 
/* 3748 */       NoArgument, 
/* 3749 */       NoArgument, 
/* 3750 */       annotationTypeDeclaration.sourceStart, 
/* 3751 */       annotationTypeDeclaration.sourceEnd);
/*      */   }
/*      */   public void invalidUsageOfEnumDeclarations(TypeDeclaration enumDeclaration) {
/* 3754 */     handle(
/* 3755 */       1610613330, 
/* 3756 */       NoArgument, 
/* 3757 */       NoArgument, 
/* 3758 */       enumDeclaration.sourceStart, 
/* 3759 */       enumDeclaration.sourceEnd);
/*      */   }
/*      */   public void invalidUsageOfForeachStatements(LocalDeclaration elementVariable, Expression collection) {
/* 3762 */     handle(
/* 3763 */       1610613328, 
/* 3764 */       NoArgument, 
/* 3765 */       NoArgument, 
/* 3766 */       elementVariable.declarationSourceStart, 
/* 3767 */       collection.sourceEnd);
/*      */   }
/*      */   public void invalidUsageOfStaticImports(ImportReference staticImport) {
/* 3770 */     handle(
/* 3771 */       1610613327, 
/* 3772 */       NoArgument, 
/* 3773 */       NoArgument, 
/* 3774 */       staticImport.declarationSourceStart, 
/* 3775 */       staticImport.declarationSourceEnd);
/*      */   }
/*      */   public void invalidUsageOfTypeArguments(TypeReference firstTypeReference, TypeReference lastTypeReference) {
/* 3778 */     handle(
/* 3779 */       1610613329, 
/* 3780 */       NoArgument, 
/* 3781 */       NoArgument, 
/* 3782 */       firstTypeReference.sourceStart, 
/* 3783 */       lastTypeReference.sourceEnd);
/*      */   }
/*      */   public void invalidUsageOfTypeParameters(TypeParameter firstTypeParameter, TypeParameter lastTypeParameter) {
/* 3786 */     handle(
/* 3787 */       1610613326, 
/* 3788 */       NoArgument, 
/* 3789 */       NoArgument, 
/* 3790 */       firstTypeParameter.declarationSourceStart, 
/* 3791 */       lastTypeParameter.declarationSourceEnd);
/*      */   }
/*      */   public void invalidUsageOfTypeParametersForAnnotationDeclaration(TypeDeclaration annotationTypeDeclaration) {
/* 3794 */     TypeParameter[] parameters = annotationTypeDeclaration.typeParameters;
/* 3795 */     int length = parameters.length;
/* 3796 */     handle(
/* 3797 */       1610613334, 
/* 3798 */       NoArgument, 
/* 3799 */       NoArgument, 
/* 3800 */       parameters[0].declarationSourceStart, 
/* 3801 */       parameters[(length - 1)].declarationSourceEnd);
/*      */   }
/*      */   public void invalidUsageOfTypeParametersForEnumDeclaration(TypeDeclaration annotationTypeDeclaration) {
/* 3804 */     TypeParameter[] parameters = annotationTypeDeclaration.typeParameters;
/* 3805 */     int length = parameters.length;
/* 3806 */     handle(
/* 3807 */       1610613335, 
/* 3808 */       NoArgument, 
/* 3809 */       NoArgument, 
/* 3810 */       parameters[0].declarationSourceStart, 
/* 3811 */       parameters[(length - 1)].declarationSourceEnd);
/*      */   }
/*      */   public void invalidUsageOfVarargs(Argument argument) {
/* 3814 */     handle(
/* 3815 */       1610613331, 
/* 3816 */       NoArgument, 
/* 3817 */       NoArgument, 
/* 3818 */       argument.type.sourceStart, 
/* 3819 */       argument.sourceEnd);
/*      */   }
/*      */   public void isClassPathCorrect(char[][] wellKnownTypeName, CompilationUnitDeclaration compUnitDecl, Object location) {
/* 3822 */     this.referenceContext = compUnitDecl;
/* 3823 */     String[] arguments = { CharOperation.toString(wellKnownTypeName) };
/* 3824 */     int start = 0; int end = 0;
/* 3825 */     if (location != null) {
/* 3826 */       if ((location instanceof InvocationSite)) {
/* 3827 */         InvocationSite site = (InvocationSite)location;
/* 3828 */         start = site.sourceStart();
/* 3829 */         end = site.sourceEnd();
/* 3830 */       } else if ((location instanceof ASTNode)) {
/* 3831 */         ASTNode node = (ASTNode)location;
/* 3832 */         start = node.sourceStart();
/* 3833 */         end = node.sourceEnd();
/*      */       }
/*      */     }
/* 3836 */     handle(
/* 3837 */       16777540, 
/* 3838 */       arguments, 
/* 3839 */       arguments, 
/* 3840 */       start, 
/* 3841 */       end);
/*      */   }
/*      */   private boolean isIdentifier(int token) {
/* 3844 */     return token == 26;
/*      */   }
/*      */   private boolean isKeyword(int token) {
/* 3847 */     switch (token) {
/*      */     case 12:
/*      */     case 32:
/*      */     case 33:
/*      */     case 34:
/*      */     case 35:
/*      */     case 36:
/*      */     case 37:
/*      */     case 38:
/*      */     case 39:
/*      */     case 40:
/*      */     case 41:
/*      */     case 42:
/*      */     case 43:
/*      */     case 44:
/*      */     case 45:
/*      */     case 46:
/*      */     case 54:
/*      */     case 55:
/*      */     case 56:
/*      */     case 57:
/*      */     case 58:
/*      */     case 59:
/*      */     case 60:
/*      */     case 61:
/*      */     case 62:
/*      */     case 63:
/*      */     case 64:
/*      */     case 72:
/*      */     case 73:
/*      */     case 74:
/*      */     case 75:
/*      */     case 76:
/*      */     case 77:
/*      */     case 78:
/*      */     case 79:
/*      */     case 80:
/*      */     case 81:
/*      */     case 82:
/*      */     case 83:
/*      */     case 95:
/*      */     case 96:
/*      */     case 97:
/*      */     case 99:
/*      */     case 100:
/*      */     case 101:
/*      */     case 102:
/*      */     case 103:
/*      */     case 104:
/*      */     case 105:
/*      */     case 106:
/* 3898 */       return true;
/*      */     case 13:
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     case 26:
/*      */     case 27:
/*      */     case 28:
/*      */     case 29:
/*      */     case 30:
/*      */     case 31:
/*      */     case 47:
/*      */     case 48:
/*      */     case 49:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/*      */     case 53:
/*      */     case 65:
/*      */     case 66:
/*      */     case 67:
/*      */     case 68:
/*      */     case 69:
/*      */     case 70:
/*      */     case 71:
/*      */     case 84:
/*      */     case 85:
/*      */     case 86:
/*      */     case 87:
/*      */     case 88:
/*      */     case 89:
/*      */     case 90:
/*      */     case 91:
/*      */     case 92:
/*      */     case 93:
/*      */     case 94:
/* 3900 */     case 98: } return false;
/*      */   }
/*      */ 
/*      */   private boolean isLiteral(int token) {
/* 3904 */     switch (token) {
/*      */     case 47:
/*      */     case 48:
/*      */     case 49:
/*      */     case 50:
/*      */     case 51:
/*      */     case 52:
/* 3911 */       return true;
/*      */     }
/* 3913 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isRecoveredName(char[] simpleName)
/*      */   {
/* 3918 */     return simpleName == RecoveryScanner.FAKE_IDENTIFIER;
/*      */   }
/*      */ 
/*      */   private boolean isRecoveredName(char[][] qualifiedName) {
/* 3922 */     if (qualifiedName == null) return false;
/* 3923 */     for (int i = 0; i < qualifiedName.length; i++) {
/* 3924 */       if (qualifiedName[i] == RecoveryScanner.FAKE_IDENTIFIER) return true;
/*      */     }
/* 3926 */     return false;
/*      */   }
/*      */ 
/*      */   public void javadocAmbiguousMethodReference(int sourceStart, int sourceEnd, Binding fieldBinding, int modifiers) {
/* 3930 */     int severity = computeSeverity(-1610612225);
/* 3931 */     if (severity == -1) return;
/* 3932 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 3933 */       String[] arguments = { new String(fieldBinding.readableName()) };
/* 3934 */       handle(
/* 3935 */         -1610612225, 
/* 3936 */         arguments, 
/* 3937 */         arguments, 
/* 3938 */         severity, 
/* 3939 */         sourceStart, 
/* 3940 */         sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocDeprecatedField(FieldBinding field, ASTNode location, int modifiers) {
/* 3945 */     int severity = computeSeverity(-1610612245);
/* 3946 */     if (severity == -1) return;
/* 3947 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 3948 */       handle(
/* 3949 */         -1610612245, 
/* 3950 */         new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 3951 */         new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 3952 */         severity, 
/* 3953 */         nodeSourceStart(field, location), 
/* 3954 */         nodeSourceEnd(field, location));
/*      */   }
/*      */ 
/*      */   public void javadocDeprecatedMethod(MethodBinding method, ASTNode location, int modifiers)
/*      */   {
/* 3959 */     boolean isConstructor = method.isConstructor();
/* 3960 */     int severity = computeSeverity(isConstructor ? -1610612241 : -1610612237);
/* 3961 */     if (severity == -1) return;
/* 3962 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 3963 */       if (isConstructor)
/* 3964 */         handle(
/* 3965 */           -1610612241, 
/* 3966 */           new String[] { new String(method.declaringClass.readableName()), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 3967 */           new String[] { new String(method.declaringClass.shortReadableName()), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 3968 */           severity, 
/* 3969 */           location.sourceStart, 
/* 3970 */           location.sourceEnd);
/*      */       else
/* 3972 */         handle(
/* 3973 */           -1610612237, 
/* 3974 */           new String[] { new String(method.declaringClass.readableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 3975 */           new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 3976 */           severity, 
/* 3977 */           location.sourceStart, 
/* 3978 */           location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocDeprecatedType(TypeBinding type, ASTNode location, int modifiers)
/*      */   {
/* 3983 */     if (location == null) return;
/* 3984 */     int severity = computeSeverity(-1610612230);
/* 3985 */     if (severity == -1) return;
/* 3986 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 3987 */       if ((type.isMemberType()) && ((type instanceof ReferenceBinding)) && (!javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, ((ReferenceBinding)type).modifiers)))
/* 3988 */         handle(-1610612271, NoArgument, NoArgument, location.sourceStart, location.sourceEnd);
/*      */       else
/* 3990 */         handle(
/* 3991 */           -1610612230, 
/* 3992 */           new String[] { new String(type.readableName()) }, 
/* 3993 */           new String[] { new String(type.shortReadableName()) }, 
/* 3994 */           severity, 
/* 3995 */           location.sourceStart, 
/* 3996 */           location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocDuplicatedParamTag(char[] token, int sourceStart, int sourceEnd, int modifiers)
/*      */   {
/* 4001 */     int severity = computeSeverity(-1610612263);
/* 4002 */     if (severity == -1) return;
/* 4003 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4004 */       String[] arguments = { String.valueOf(token) };
/* 4005 */       handle(
/* 4006 */         -1610612263, 
/* 4007 */         arguments, 
/* 4008 */         arguments, 
/* 4009 */         severity, 
/* 4010 */         sourceStart, 
/* 4011 */         sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocDuplicatedReturnTag(int sourceStart, int sourceEnd) {
/* 4015 */     handle(-1610612260, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocDuplicatedTag(char[] tagName, int sourceStart, int sourceEnd) {
/* 4018 */     String[] arguments = { new String(tagName) };
/* 4019 */     handle(
/* 4020 */       -1610612272, 
/* 4021 */       arguments, 
/* 4022 */       arguments, 
/* 4023 */       sourceStart, 
/* 4024 */       sourceEnd);
/*      */   }
/*      */   public void javadocDuplicatedThrowsClassName(TypeReference typeReference, int modifiers) {
/* 4027 */     int severity = computeSeverity(-1610612256);
/* 4028 */     if (severity == -1) return;
/* 4029 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4030 */       String[] arguments = { String.valueOf(typeReference.resolvedType.sourceName()) };
/* 4031 */       handle(
/* 4032 */         -1610612256, 
/* 4033 */         arguments, 
/* 4034 */         arguments, 
/* 4035 */         severity, 
/* 4036 */         typeReference.sourceStart, 
/* 4037 */         typeReference.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocEmptyReturnTag(int sourceStart, int sourceEnd, int modifiers) {
/* 4041 */     int severity = computeSeverity(-1610612220);
/* 4042 */     if (severity == -1) return;
/* 4043 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4044 */       String[] arguments = { new String(JavadocTagConstants.TAG_RETURN) };
/* 4045 */       handle(-1610612220, arguments, arguments, sourceStart, sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocErrorNoMethodFor(MessageSend messageSend, TypeBinding recType, TypeBinding[] params, int modifiers) {
/* 4049 */     int id = recType.isArrayType() ? -1610612234 : -1610612236;
/* 4050 */     int severity = computeSeverity(id);
/* 4051 */     if (severity == -1) return;
/* 4052 */     StringBuffer buffer = new StringBuffer();
/* 4053 */     StringBuffer shortBuffer = new StringBuffer();
/* 4054 */     int i = 0; for (int length = params.length; i < length; i++) {
/* 4055 */       if (i != 0) {
/* 4056 */         buffer.append(", ");
/* 4057 */         shortBuffer.append(", ");
/*      */       }
/* 4059 */       buffer.append(new String(params[i].readableName()));
/* 4060 */       shortBuffer.append(new String(params[i].shortReadableName()));
/*      */     }
/* 4062 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4063 */       handle(
/* 4064 */         id, 
/* 4065 */         new String[] { new String(recType.readableName()), new String(messageSend.selector), buffer.toString() }, 
/* 4066 */         new String[] { new String(recType.shortReadableName()), new String(messageSend.selector), shortBuffer.toString() }, 
/* 4067 */         severity, 
/* 4068 */         messageSend.sourceStart, 
/* 4069 */         messageSend.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocHiddenReference(int sourceStart, int sourceEnd, Scope scope, int modifiers) {
/* 4073 */     Scope currentScope = scope;
/* 4074 */     while (currentScope.parent.kind != 4) {
/* 4075 */       if (!javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, currentScope.getDeclarationModifiers())) {
/* 4076 */         return;
/*      */       }
/* 4078 */       currentScope = currentScope.parent;
/*      */     }
/* 4080 */     String[] arguments = { this.options.getVisibilityString(this.options.reportInvalidJavadocTagsVisibility), this.options.getVisibilityString(modifiers) };
/* 4081 */     handle(-1610612271, arguments, arguments, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocInvalidConstructor(Statement statement, MethodBinding targetConstructor, int modifiers) {
/* 4085 */     if (!javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) return;
/* 4086 */     int sourceStart = statement.sourceStart;
/* 4087 */     int sourceEnd = statement.sourceEnd;
/* 4088 */     if ((statement instanceof AllocationExpression)) {
/* 4089 */       AllocationExpression allocation = (AllocationExpression)statement;
/* 4090 */       if (allocation.enumConstant != null) {
/* 4091 */         sourceStart = allocation.enumConstant.sourceStart;
/* 4092 */         sourceEnd = allocation.enumConstant.sourceEnd;
/*      */       }
/*      */     }
/* 4095 */     int id = -1610612244;
/* 4096 */     ProblemMethodBinding problemConstructor = null;
/* 4097 */     MethodBinding shownConstructor = null;
/* 4098 */     switch (targetConstructor.problemId()) {
/*      */     case 1:
/* 4100 */       id = -1610612244;
/* 4101 */       break;
/*      */     case 2:
/* 4103 */       id = -1610612243;
/* 4104 */       break;
/*      */     case 3:
/* 4106 */       id = -1610612242;
/* 4107 */       break;
/*      */     case 10:
/* 4109 */       int severity = computeSeverity(-1610611881);
/* 4110 */       if (severity == -1) return;
/* 4111 */       problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 4112 */       ParameterizedGenericMethodBinding substitutedConstructor = (ParameterizedGenericMethodBinding)problemConstructor.closestMatch;
/* 4113 */       shownConstructor = substitutedConstructor.original();
/*      */ 
/* 4115 */       int augmentedLength = problemConstructor.parameters.length;
/* 4116 */       TypeBinding inferredTypeArgument = problemConstructor.parameters[(augmentedLength - 2)];
/* 4117 */       TypeVariableBinding typeParameter = (TypeVariableBinding)problemConstructor.parameters[(augmentedLength - 1)];
/* 4118 */       TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
/* 4119 */       System.arraycopy(problemConstructor.parameters, 0, invocationArguments, 0, augmentedLength - 2);
/*      */ 
/* 4121 */       handle(
/* 4122 */         -1610611881, 
/* 4123 */         new String[] { 
/* 4124 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 4125 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 4126 */         new String(shownConstructor.declaringClass.readableName()), 
/* 4127 */         typesAsString(false, invocationArguments, false), 
/* 4128 */         new String(inferredTypeArgument.readableName()), 
/* 4129 */         new String(typeParameter.sourceName), 
/* 4130 */         parameterBoundAsString(typeParameter, false) }, 
/* 4131 */         new String[] { 
/* 4132 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 4133 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 4134 */         new String(shownConstructor.declaringClass.shortReadableName()), 
/* 4135 */         typesAsString(false, invocationArguments, true), 
/* 4136 */         new String(inferredTypeArgument.shortReadableName()), 
/* 4137 */         new String(typeParameter.sourceName), 
/* 4138 */         parameterBoundAsString(typeParameter, true) }, 
/* 4139 */         severity, 
/* 4140 */         sourceStart, 
/* 4141 */         sourceEnd);
/* 4142 */       return;
/*      */     case 11:
/* 4145 */       problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 4146 */       shownConstructor = problemConstructor.closestMatch;
/* 4147 */       boolean noTypeVariables = shownConstructor.typeVariables == Binding.NO_TYPE_VARIABLES;
/* 4148 */       int severity = computeSeverity(noTypeVariables ? -1610611880 : -1610611879);
/* 4149 */       if (severity == -1) return;
/* 4150 */       if (noTypeVariables)
/* 4151 */         handle(
/* 4152 */           -1610611880, 
/* 4153 */           new String[] { 
/* 4154 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 4155 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 4156 */           new String(shownConstructor.declaringClass.readableName()), 
/* 4157 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 4158 */           new String[] { 
/* 4159 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 4160 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 4161 */           new String(shownConstructor.declaringClass.shortReadableName()), 
/* 4162 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 4163 */           severity, 
/* 4164 */           sourceStart, 
/* 4165 */           sourceEnd);
/*      */       else {
/* 4167 */         handle(
/* 4168 */           -1610611879, 
/* 4169 */           new String[] { 
/* 4170 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 4171 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 4172 */           new String(shownConstructor.declaringClass.readableName()), 
/* 4173 */           typesAsString(false, shownConstructor.typeVariables, false), 
/* 4174 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 4175 */           new String[] { 
/* 4176 */           new String(shownConstructor.declaringClass.sourceName()), 
/* 4177 */           typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 4178 */           new String(shownConstructor.declaringClass.shortReadableName()), 
/* 4179 */           typesAsString(false, shownConstructor.typeVariables, true), 
/* 4180 */           typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 4181 */           severity, 
/* 4182 */           sourceStart, 
/* 4183 */           sourceEnd);
/*      */       }
/* 4185 */       return;
/*      */     case 12:
/* 4187 */       int severity = computeSeverity(-1610611878);
/* 4188 */       if (severity == -1) return;
/* 4189 */       problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 4190 */       shownConstructor = problemConstructor.closestMatch;
/* 4191 */       handle(
/* 4192 */         -1610611878, 
/* 4193 */         new String[] { 
/* 4194 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 4195 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 4196 */         new String(shownConstructor.declaringClass.readableName()), 
/* 4197 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, false), 
/* 4198 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 4199 */         new String[] { 
/* 4200 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 4201 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 4202 */         new String(shownConstructor.declaringClass.shortReadableName()), 
/* 4203 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownConstructor).typeArguments, true), 
/* 4204 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 4205 */         severity, 
/* 4206 */         sourceStart, 
/* 4207 */         sourceEnd);
/* 4208 */       return;
/*      */     case 13:
/* 4210 */       int severity = computeSeverity(-1610611877);
/* 4211 */       if (severity == -1) return;
/* 4212 */       problemConstructor = (ProblemMethodBinding)targetConstructor;
/* 4213 */       shownConstructor = problemConstructor.closestMatch;
/* 4214 */       handle(
/* 4215 */         -1610611877, 
/* 4216 */         new String[] { 
/* 4217 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 4218 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, false), 
/* 4219 */         new String(shownConstructor.declaringClass.readableName()), 
/* 4220 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 4221 */         new String[] { 
/* 4222 */         new String(shownConstructor.declaringClass.sourceName()), 
/* 4223 */         typesAsString(shownConstructor.isVarargs(), shownConstructor.parameters, true), 
/* 4224 */         new String(shownConstructor.declaringClass.shortReadableName()), 
/* 4225 */         typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 4226 */         severity, 
/* 4227 */         sourceStart, 
/* 4228 */         sourceEnd);
/* 4229 */       return;
/*      */     case 0:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     default:
/* 4232 */       needImplementation(statement);
/*      */     }
/*      */ 
/* 4235 */     int severity = computeSeverity(id);
/* 4236 */     if (severity == -1) return;
/* 4237 */     handle(
/* 4238 */       id, 
/* 4239 */       new String[] { new String(targetConstructor.declaringClass.readableName()), typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, false) }, 
/* 4240 */       new String[] { new String(targetConstructor.declaringClass.shortReadableName()), typesAsString(targetConstructor.isVarargs(), targetConstructor.parameters, true) }, 
/* 4241 */       severity, 
/* 4242 */       statement.sourceStart, 
/* 4243 */       statement.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocInvalidField(FieldReference fieldRef, Binding fieldBinding, TypeBinding searchedType, int modifiers)
/*      */   {
/* 4253 */     int id = -1610612248;
/* 4254 */     switch (fieldBinding.problemId()) {
/*      */     case 1:
/* 4256 */       id = -1610612248;
/* 4257 */       break;
/*      */     case 2:
/* 4259 */       id = -1610612247;
/* 4260 */       break;
/*      */     case 3:
/* 4262 */       id = -1610612246;
/* 4263 */       break;
/*      */     case 0:
/*      */     default:
/* 4266 */       needImplementation(fieldRef);
/*      */     }
/*      */ 
/* 4269 */     int severity = computeSeverity(id);
/* 4270 */     if (severity == -1) return;
/*      */ 
/* 4272 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4273 */       String[] arguments = { new String(fieldBinding.readableName()) };
/* 4274 */       handle(
/* 4275 */         id, 
/* 4276 */         arguments, 
/* 4277 */         arguments, 
/* 4278 */         severity, 
/* 4279 */         fieldRef.sourceStart, 
/* 4280 */         fieldRef.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocInvalidMemberTypeQualification(int sourceStart, int sourceEnd, int modifiers) {
/* 4284 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4285 */       handle(-1610612270, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocInvalidMethod(MessageSend messageSend, MethodBinding method, int modifiers)
/*      */   {
/* 4296 */     if (!javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) return;
/*      */ 
/* 4298 */     ProblemMethodBinding problemMethod = null;
/* 4299 */     MethodBinding shownMethod = null;
/* 4300 */     int id = -1610612240;
/* 4301 */     switch (method.problemId()) {
/*      */     case 1:
/* 4303 */       id = -1610612240;
/* 4304 */       problemMethod = (ProblemMethodBinding)method;
/* 4305 */       if (problemMethod.closestMatch == null) break;
/* 4306 */       int severity = computeSeverity(-1610612235);
/* 4307 */       if (severity == -1) return;
/* 4308 */       String closestParameterTypeNames = typesAsString(problemMethod.closestMatch.isVarargs(), problemMethod.closestMatch.parameters, false);
/* 4309 */       String parameterTypeNames = typesAsString(method.isVarargs(), method.parameters, false);
/* 4310 */       String closestParameterTypeShortNames = typesAsString(problemMethod.closestMatch.isVarargs(), problemMethod.closestMatch.parameters, true);
/* 4311 */       String parameterTypeShortNames = typesAsString(method.isVarargs(), method.parameters, true);
/* 4312 */       if (closestParameterTypeShortNames.equals(parameterTypeShortNames)) {
/* 4313 */         closestParameterTypeShortNames = closestParameterTypeNames;
/* 4314 */         parameterTypeShortNames = parameterTypeNames;
/*      */       }
/* 4316 */       handle(
/* 4317 */         -1610612235, 
/* 4318 */         new String[] { 
/* 4319 */         new String(problemMethod.closestMatch.declaringClass.readableName()), 
/* 4320 */         new String(problemMethod.closestMatch.selector), 
/* 4321 */         closestParameterTypeNames, 
/* 4322 */         parameterTypeNames }, 
/* 4324 */         new String[] { 
/* 4325 */         new String(problemMethod.closestMatch.declaringClass.shortReadableName()), 
/* 4326 */         new String(problemMethod.closestMatch.selector), 
/* 4327 */         closestParameterTypeShortNames, 
/* 4328 */         parameterTypeShortNames }, 
/* 4330 */         severity, 
/* 4331 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 4332 */         (int)messageSend.nameSourcePosition);
/* 4333 */       return;
/*      */     case 2:
/* 4337 */       id = -1610612239;
/* 4338 */       break;
/*      */     case 3:
/* 4340 */       id = -1610612238;
/* 4341 */       break;
/*      */     case 10:
/* 4343 */       int severity = computeSeverity(-1610611886);
/* 4344 */       if (severity == -1) return;
/* 4345 */       problemMethod = (ProblemMethodBinding)method;
/* 4346 */       ParameterizedGenericMethodBinding substitutedMethod = (ParameterizedGenericMethodBinding)problemMethod.closestMatch;
/* 4347 */       shownMethod = substitutedMethod.original();
/* 4348 */       int augmentedLength = problemMethod.parameters.length;
/* 4349 */       TypeBinding inferredTypeArgument = problemMethod.parameters[(augmentedLength - 2)];
/* 4350 */       TypeVariableBinding typeParameter = (TypeVariableBinding)problemMethod.parameters[(augmentedLength - 1)];
/* 4351 */       TypeBinding[] invocationArguments = new TypeBinding[augmentedLength - 2];
/* 4352 */       System.arraycopy(problemMethod.parameters, 0, invocationArguments, 0, augmentedLength - 2);
/* 4353 */       handle(
/* 4354 */         -1610611886, 
/* 4355 */         new String[] { 
/* 4356 */         new String(shownMethod.selector), 
/* 4357 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 4358 */         new String(shownMethod.declaringClass.readableName()), 
/* 4359 */         typesAsString(false, invocationArguments, false), 
/* 4360 */         new String(inferredTypeArgument.readableName()), 
/* 4361 */         new String(typeParameter.sourceName), 
/* 4362 */         parameterBoundAsString(typeParameter, false) }, 
/* 4363 */         new String[] { 
/* 4364 */         new String(shownMethod.selector), 
/* 4365 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 4366 */         new String(shownMethod.declaringClass.shortReadableName()), 
/* 4367 */         typesAsString(false, invocationArguments, true), 
/* 4368 */         new String(inferredTypeArgument.shortReadableName()), 
/* 4369 */         new String(typeParameter.sourceName), 
/* 4370 */         parameterBoundAsString(typeParameter, true) }, 
/* 4371 */         severity, 
/* 4372 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 4373 */         (int)messageSend.nameSourcePosition);
/* 4374 */       return;
/*      */     case 11:
/* 4376 */       problemMethod = (ProblemMethodBinding)method;
/* 4377 */       shownMethod = problemMethod.closestMatch;
/* 4378 */       boolean noTypeVariables = shownMethod.typeVariables == Binding.NO_TYPE_VARIABLES;
/* 4379 */       int severity = computeSeverity(noTypeVariables ? -1610611885 : -1610611884);
/* 4380 */       if (severity == -1) return;
/* 4381 */       if (noTypeVariables)
/* 4382 */         handle(
/* 4383 */           -1610611885, 
/* 4384 */           new String[] { 
/* 4385 */           new String(shownMethod.selector), 
/* 4386 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 4387 */           new String(shownMethod.declaringClass.readableName()), 
/* 4388 */           typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 4389 */           new String[] { 
/* 4390 */           new String(shownMethod.selector), 
/* 4391 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 4392 */           new String(shownMethod.declaringClass.shortReadableName()), 
/* 4393 */           typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 4394 */           severity, 
/* 4395 */           (int)(messageSend.nameSourcePosition >>> 32), 
/* 4396 */           (int)messageSend.nameSourcePosition);
/*      */       else {
/* 4398 */         handle(
/* 4399 */           -1610611884, 
/* 4400 */           new String[] { 
/* 4401 */           new String(shownMethod.selector), 
/* 4402 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 4403 */           new String(shownMethod.declaringClass.readableName()), 
/* 4404 */           typesAsString(false, shownMethod.typeVariables, false), 
/* 4405 */           typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 4406 */           new String[] { 
/* 4407 */           new String(shownMethod.selector), 
/* 4408 */           typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 4409 */           new String(shownMethod.declaringClass.shortReadableName()), 
/* 4410 */           typesAsString(false, shownMethod.typeVariables, true), 
/* 4411 */           typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 4412 */           severity, 
/* 4413 */           (int)(messageSend.nameSourcePosition >>> 32), 
/* 4414 */           (int)messageSend.nameSourcePosition);
/*      */       }
/* 4416 */       return;
/*      */     case 12:
/* 4418 */       int severity = computeSeverity(-1610611883);
/* 4419 */       if (severity == -1) return;
/* 4420 */       problemMethod = (ProblemMethodBinding)method;
/* 4421 */       shownMethod = problemMethod.closestMatch;
/* 4422 */       handle(
/* 4423 */         -1610611883, 
/* 4424 */         new String[] { 
/* 4425 */         new String(shownMethod.selector), 
/* 4426 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 4427 */         new String(shownMethod.declaringClass.readableName()), 
/* 4428 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownMethod).typeArguments, false), 
/* 4429 */         typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 4430 */         new String[] { 
/* 4431 */         new String(shownMethod.selector), 
/* 4432 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 4433 */         new String(shownMethod.declaringClass.shortReadableName()), 
/* 4434 */         typesAsString(false, ((ParameterizedGenericMethodBinding)shownMethod).typeArguments, true), 
/* 4435 */         typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 4436 */         severity, 
/* 4437 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 4438 */         (int)messageSend.nameSourcePosition);
/* 4439 */       return;
/*      */     case 13:
/* 4441 */       int severity = computeSeverity(-1610611882);
/* 4442 */       if (severity == -1) return;
/* 4443 */       problemMethod = (ProblemMethodBinding)method;
/* 4444 */       shownMethod = problemMethod.closestMatch;
/* 4445 */       handle(
/* 4446 */         -1610611882, 
/* 4447 */         new String[] { 
/* 4448 */         new String(shownMethod.selector), 
/* 4449 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, false), 
/* 4450 */         new String(shownMethod.declaringClass.readableName()), 
/* 4451 */         typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 4452 */         new String[] { 
/* 4453 */         new String(shownMethod.selector), 
/* 4454 */         typesAsString(shownMethod.isVarargs(), shownMethod.parameters, true), 
/* 4455 */         new String(shownMethod.declaringClass.shortReadableName()), 
/* 4456 */         typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 4457 */         severity, 
/* 4458 */         (int)(messageSend.nameSourcePosition >>> 32), 
/* 4459 */         (int)messageSend.nameSourcePosition);
/* 4460 */       return;
/*      */     case 0:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     default:
/* 4463 */       needImplementation(messageSend);
/*      */     }
/*      */ 
/* 4466 */     int severity = computeSeverity(id);
/* 4467 */     if (severity == -1) return;
/*      */ 
/* 4469 */     handle(
/* 4470 */       id, 
/* 4471 */       new String[] { 
/* 4472 */       new String(method.declaringClass.readableName()), 
/* 4473 */       new String(method.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 4474 */       new String[] { 
/* 4475 */       new String(method.declaringClass.shortReadableName()), 
/* 4476 */       new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 4477 */       severity, 
/* 4478 */       (int)(messageSend.nameSourcePosition >>> 32), 
/* 4479 */       (int)messageSend.nameSourcePosition);
/*      */   }
/*      */   public void javadocInvalidParamTagName(int sourceStart, int sourceEnd) {
/* 4482 */     handle(-1610612217, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocInvalidParamTypeParameter(int sourceStart, int sourceEnd) {
/* 4485 */     handle(-1610612267, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocInvalidReference(int sourceStart, int sourceEnd) {
/* 4488 */     handle(-1610612253, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocInvalidSeeHref(int sourceStart, int sourceEnd)
/*      */   {
/* 4495 */     handle(-1610612252, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocInvalidSeeReferenceArgs(int sourceStart, int sourceEnd) {
/* 4498 */     handle(-1610612251, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocInvalidSeeUrlReference(int sourceStart, int sourceEnd)
/*      */   {
/* 4505 */     handle(-1610612274, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocInvalidTag(int sourceStart, int sourceEnd) {
/* 4508 */     handle(-1610612249, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocInvalidThrowsClass(int sourceStart, int sourceEnd) {
/* 4511 */     handle(-1610612257, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocInvalidThrowsClassName(TypeReference typeReference, int modifiers) {
/* 4514 */     int severity = computeSeverity(-1610612255);
/* 4515 */     if (severity == -1) return;
/* 4516 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4517 */       String[] arguments = { String.valueOf(typeReference.resolvedType.sourceName()) };
/* 4518 */       handle(
/* 4519 */         -1610612255, 
/* 4520 */         arguments, 
/* 4521 */         arguments, 
/* 4522 */         severity, 
/* 4523 */         typeReference.sourceStart, 
/* 4524 */         typeReference.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocInvalidType(ASTNode location, TypeBinding type, int modifiers) {
/* 4528 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4529 */       int id = -1610612233;
/* 4530 */       switch (type.problemId()) {
/*      */       case 1:
/* 4532 */         id = -1610612233;
/* 4533 */         break;
/*      */       case 2:
/* 4535 */         id = -1610612232;
/* 4536 */         break;
/*      */       case 3:
/* 4538 */         id = -1610612231;
/* 4539 */         break;
/*      */       case 4:
/* 4541 */         id = -1610612229;
/* 4542 */         break;
/*      */       case 5:
/* 4544 */         id = -1610612226;
/* 4545 */         break;
/*      */       case 7:
/* 4547 */         id = -1610612268;
/* 4548 */         break;
/*      */       case 0:
/*      */       case 6:
/*      */       default:
/* 4551 */         needImplementation(location);
/*      */       }
/*      */ 
/* 4554 */       int severity = computeSeverity(id);
/* 4555 */       if (severity == -1) return;
/* 4556 */       handle(
/* 4557 */         id, 
/* 4558 */         new String[] { new String(type.readableName()) }, 
/* 4559 */         new String[] { new String(type.shortReadableName()) }, 
/* 4560 */         severity, 
/* 4561 */         location.sourceStart, 
/* 4562 */         location.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocInvalidValueReference(int sourceStart, int sourceEnd, int modifiers) {
/* 4566 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4567 */       handle(-1610612219, NoArgument, NoArgument, sourceStart, sourceEnd); 
/*      */   }
/*      */ 
/*      */   public void javadocMalformedSeeReference(int sourceStart, int sourceEnd) {
/* 4570 */     handle(-1610612223, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */   public void javadocMissing(int sourceStart, int sourceEnd, int modifiers) {
/* 4573 */     int severity = computeSeverity(-1610612250);
/* 4574 */     javadocMissing(sourceStart, sourceEnd, severity, modifiers);
/*      */   }
/*      */   public void javadocMissing(int sourceStart, int sourceEnd, int severity, int modifiers) {
/* 4577 */     if (severity == -1) return;
/* 4578 */     boolean overriding = (modifiers & 0x30000000) != 0;
/* 4579 */     boolean report = (this.options.getSeverity(1048576) != -1) && (
/* 4580 */       (!overriding) || (this.options.reportMissingJavadocCommentsOverriding));
/* 4581 */     if (report) {
/* 4582 */       String arg = javadocVisibilityArgument(this.options.reportMissingJavadocCommentsVisibility, modifiers);
/* 4583 */       if (arg != null) {
/* 4584 */         String[] arguments = { arg };
/* 4585 */         handle(
/* 4586 */           -1610612250, 
/* 4587 */           arguments, 
/* 4588 */           arguments, 
/* 4589 */           severity, 
/* 4590 */           sourceStart, 
/* 4591 */           sourceEnd);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocMissingHashCharacter(int sourceStart, int sourceEnd, String ref) {
/* 4596 */     int severity = computeSeverity(-1610612221);
/* 4597 */     if (severity == -1) return;
/* 4598 */     String[] arguments = { ref };
/* 4599 */     handle(
/* 4600 */       -1610612221, 
/* 4601 */       arguments, 
/* 4602 */       arguments, 
/* 4603 */       severity, 
/* 4604 */       sourceStart, 
/* 4605 */       sourceEnd);
/*      */   }
/*      */   public void javadocMissingIdentifier(int sourceStart, int sourceEnd, int modifiers) {
/* 4608 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4609 */       handle(-1610612269, NoArgument, NoArgument, sourceStart, sourceEnd); 
/*      */   }
/*      */ 
/*      */   public void javadocMissingParamName(int sourceStart, int sourceEnd, int modifiers) {
/* 4612 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4613 */       handle(-1610612264, NoArgument, NoArgument, sourceStart, sourceEnd); 
/*      */   }
/*      */ 
/*      */   public void javadocMissingParamTag(char[] name, int sourceStart, int sourceEnd, int modifiers) {
/* 4616 */     int severity = computeSeverity(-1610612265);
/* 4617 */     if (severity == -1) return;
/* 4618 */     boolean overriding = (modifiers & 0x30000000) != 0;
/* 4619 */     boolean report = (this.options.getSeverity(2097152) != -1) && (
/* 4620 */       (!overriding) || (this.options.reportMissingJavadocTagsOverriding));
/* 4621 */     if ((report) && (javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers))) {
/* 4622 */       String[] arguments = { String.valueOf(name) };
/* 4623 */       handle(
/* 4624 */         -1610612265, 
/* 4625 */         arguments, 
/* 4626 */         arguments, 
/* 4627 */         severity, 
/* 4628 */         sourceStart, 
/* 4629 */         sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocMissingReference(int sourceStart, int sourceEnd, int modifiers) {
/* 4633 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4634 */       handle(-1610612254, NoArgument, NoArgument, sourceStart, sourceEnd); 
/*      */   }
/*      */ 
/*      */   public void javadocMissingReturnTag(int sourceStart, int sourceEnd, int modifiers) {
/* 4637 */     boolean overriding = (modifiers & 0x30000000) != 0;
/* 4638 */     boolean report = (this.options.getSeverity(2097152) != -1) && (
/* 4639 */       (!overriding) || (this.options.reportMissingJavadocTagsOverriding));
/* 4640 */     if ((report) && (javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers)))
/* 4641 */       handle(-1610612261, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocMissingTagDescription(char[] tokenName, int sourceStart, int sourceEnd, int modifiers) {
/* 4645 */     int severity = computeSeverity(-1610612273);
/* 4646 */     if (severity == -1) return;
/* 4647 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4648 */       String[] arguments = { new String(tokenName) };
/*      */ 
/* 4650 */       handle(-1610612220, arguments, arguments, sourceStart, sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocMissingTagDescriptionAfterReference(int sourceStart, int sourceEnd, int modifiers) {
/* 4654 */     int severity = computeSeverity(-1610612273);
/* 4655 */     if (severity == -1) return;
/* 4656 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4657 */       handle(-1610612273, NoArgument, NoArgument, severity, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocMissingThrowsClassName(int sourceStart, int sourceEnd, int modifiers) {
/* 4661 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers))
/* 4662 */       handle(-1610612258, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocMissingThrowsTag(TypeReference typeRef, int modifiers) {
/* 4666 */     int severity = computeSeverity(-1610612259);
/* 4667 */     if (severity == -1) return;
/* 4668 */     boolean overriding = (modifiers & 0x30000000) != 0;
/* 4669 */     boolean report = (this.options.getSeverity(2097152) != -1) && (
/* 4670 */       (!overriding) || (this.options.reportMissingJavadocTagsOverriding));
/* 4671 */     if ((report) && (javadocVisibility(this.options.reportMissingJavadocTagsVisibility, modifiers))) {
/* 4672 */       String[] arguments = { String.valueOf(typeRef.resolvedType.sourceName()) };
/* 4673 */       handle(
/* 4674 */         -1610612259, 
/* 4675 */         arguments, 
/* 4676 */         arguments, 
/* 4677 */         severity, 
/* 4678 */         typeRef.sourceStart, 
/* 4679 */         typeRef.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocUndeclaredParamTagName(char[] token, int sourceStart, int sourceEnd, int modifiers) {
/* 4683 */     int severity = computeSeverity(-1610612262);
/* 4684 */     if (severity == -1) return;
/* 4685 */     if (javadocVisibility(this.options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 4686 */       String[] arguments = { String.valueOf(token) };
/* 4687 */       handle(
/* 4688 */         -1610612262, 
/* 4689 */         arguments, 
/* 4690 */         arguments, 
/* 4691 */         severity, 
/* 4692 */         sourceStart, 
/* 4693 */         sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void javadocUnexpectedTag(int sourceStart, int sourceEnd) {
/* 4698 */     handle(-1610612266, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocUnexpectedText(int sourceStart, int sourceEnd) {
/* 4702 */     handle(-1610612218, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   public void javadocUnterminatedInlineTag(int sourceStart, int sourceEnd) {
/* 4706 */     handle(-1610612224, NoArgument, NoArgument, sourceStart, sourceEnd);
/*      */   }
/*      */ 
/*      */   private boolean javadocVisibility(int visibility, int modifiers) {
/* 4710 */     if (modifiers < 0) return true;
/* 4711 */     switch (modifiers & 0x7) {
/*      */     case 1:
/* 4713 */       return true;
/*      */     case 4:
/* 4715 */       return visibility != 1;
/*      */     case 0:
/* 4717 */       return (visibility == 0) || (visibility == 2);
/*      */     case 2:
/* 4719 */       return visibility == 2;
/*      */     case 3:
/* 4721 */     }return true;
/*      */   }
/*      */ 
/*      */   private String javadocVisibilityArgument(int visibility, int modifiers) {
/* 4725 */     String argument = null;
/* 4726 */     switch (modifiers & 0x7) {
/*      */     case 1:
/* 4728 */       argument = "public";
/* 4729 */       break;
/*      */     case 4:
/* 4731 */       if (visibility == 1) break;
/* 4732 */       argument = "protected";
/*      */ 
/* 4734 */       break;
/*      */     case 0:
/* 4736 */       if ((visibility != 0) && (visibility != 2)) break;
/* 4737 */       argument = "default";
/*      */ 
/* 4739 */       break;
/*      */     case 2:
/* 4741 */       if (visibility != 2) break;
/* 4742 */       argument = "private";
/*      */     case 3:
/*      */     }
/*      */ 
/* 4746 */     return argument;
/*      */   }
/*      */ 
/*      */   public void localVariableHiding(LocalDeclaration local, Binding hiddenVariable, boolean isSpecialArgHidingField) {
/* 4750 */     if ((hiddenVariable instanceof LocalVariableBinding)) {
/* 4751 */       int id = (local instanceof Argument) ? 
/* 4752 */         536871006 : 
/* 4753 */         536871002;
/* 4754 */       int severity = computeSeverity(id);
/* 4755 */       if (severity == -1) return;
/* 4756 */       String[] arguments = { new String(local.name) };
/* 4757 */       handle(
/* 4758 */         id, 
/* 4759 */         arguments, 
/* 4760 */         arguments, 
/* 4761 */         severity, 
/* 4762 */         nodeSourceStart(hiddenVariable, local), 
/* 4763 */         nodeSourceEnd(hiddenVariable, local));
/* 4764 */     } else if ((hiddenVariable instanceof FieldBinding)) {
/* 4765 */       if ((isSpecialArgHidingField) && (!this.options.reportSpecialParameterHidingField)) {
/* 4766 */         return;
/*      */       }
/* 4768 */       int id = (local instanceof Argument) ? 
/* 4769 */         536871007 : 
/* 4770 */         570425435;
/* 4771 */       int severity = computeSeverity(id);
/* 4772 */       if (severity == -1) return;
/* 4773 */       FieldBinding field = (FieldBinding)hiddenVariable;
/* 4774 */       handle(
/* 4775 */         id, 
/* 4776 */         new String[] { new String(local.name), new String(field.declaringClass.readableName()) }, 
/* 4777 */         new String[] { new String(local.name), new String(field.declaringClass.shortReadableName()) }, 
/* 4778 */         severity, 
/* 4779 */         local.sourceStart, 
/* 4780 */         local.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void localVariableNonNullComparedToNull(LocalVariableBinding local, ASTNode location) {
/* 4785 */     int severity = computeSeverity(536871370);
/* 4786 */     if (severity == -1) return;
/* 4787 */     String[] arguments = { new String(local.name) };
/* 4788 */     handle(
/* 4789 */       536871370, 
/* 4790 */       arguments, 
/* 4791 */       arguments, 
/* 4792 */       severity, 
/* 4793 */       nodeSourceStart(local, location), 
/* 4794 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void localVariableNullComparedToNonNull(LocalVariableBinding local, ASTNode location) {
/* 4798 */     int severity = computeSeverity(536871366);
/* 4799 */     if (severity == -1) return;
/* 4800 */     String[] arguments = { new String(local.name) };
/* 4801 */     handle(
/* 4802 */       536871366, 
/* 4803 */       arguments, 
/* 4804 */       arguments, 
/* 4805 */       severity, 
/* 4806 */       nodeSourceStart(local, location), 
/* 4807 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void localVariableNullInstanceof(LocalVariableBinding local, ASTNode location) {
/* 4811 */     int severity = computeSeverity(536871368);
/* 4812 */     if (severity == -1) return;
/* 4813 */     String[] arguments = { new String(local.name) };
/* 4814 */     handle(
/* 4815 */       536871368, 
/* 4816 */       arguments, 
/* 4817 */       arguments, 
/* 4818 */       severity, 
/* 4819 */       nodeSourceStart(local, location), 
/* 4820 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void localVariableNullReference(LocalVariableBinding local, ASTNode location) {
/* 4824 */     int severity = computeSeverity(536871363);
/* 4825 */     if (severity == -1) return;
/* 4826 */     String[] arguments = { new String(local.name) };
/* 4827 */     handle(
/* 4828 */       536871363, 
/* 4829 */       arguments, 
/* 4830 */       arguments, 
/* 4831 */       severity, 
/* 4832 */       nodeSourceStart(local, location), 
/* 4833 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void localVariablePotentialNullReference(LocalVariableBinding local, ASTNode location) {
/* 4837 */     int severity = computeSeverity(536871364);
/* 4838 */     if (severity == -1) return;
/* 4839 */     String[] arguments = { new String(local.name) };
/* 4840 */     handle(
/* 4841 */       536871364, 
/* 4842 */       arguments, 
/* 4843 */       arguments, 
/* 4844 */       severity, 
/* 4845 */       nodeSourceStart(local, location), 
/* 4846 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void localVariableRedundantCheckOnNonNull(LocalVariableBinding local, ASTNode location) {
/* 4850 */     int severity = computeSeverity(536871369);
/* 4851 */     if (severity == -1) return;
/* 4852 */     String[] arguments = { new String(local.name) };
/* 4853 */     handle(
/* 4854 */       536871369, 
/* 4855 */       arguments, 
/* 4856 */       arguments, 
/* 4857 */       severity, 
/* 4858 */       nodeSourceStart(local, location), 
/* 4859 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void localVariableRedundantCheckOnNull(LocalVariableBinding local, ASTNode location) {
/* 4863 */     int severity = computeSeverity(536871365);
/* 4864 */     if (severity == -1) return;
/* 4865 */     String[] arguments = { new String(local.name) };
/* 4866 */     handle(
/* 4867 */       536871365, 
/* 4868 */       arguments, 
/* 4869 */       arguments, 
/* 4870 */       severity, 
/* 4871 */       nodeSourceStart(local, location), 
/* 4872 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void localVariableRedundantNullAssignment(LocalVariableBinding local, ASTNode location) {
/* 4876 */     int severity = computeSeverity(536871367);
/* 4877 */     if (severity == -1) return;
/* 4878 */     String[] arguments = { new String(local.name) };
/* 4879 */     handle(
/* 4880 */       536871367, 
/* 4881 */       arguments, 
/* 4882 */       arguments, 
/* 4883 */       severity, 
/* 4884 */       nodeSourceStart(local, location), 
/* 4885 */       nodeSourceEnd(local, location));
/*      */   }
/*      */ 
/*      */   public void methodMustOverride(AbstractMethodDeclaration method) {
/* 4889 */     MethodBinding binding = method.binding;
/* 4890 */     handle(
/* 4891 */       this.options.sourceLevel == 3211264L ? 67109487 : 67109498, 
/* 4892 */       new String[] { new String(binding.selector), typesAsString(binding.isVarargs(), binding.parameters, false), new String(binding.declaringClass.readableName()) }, 
/* 4893 */       new String[] { new String(binding.selector), typesAsString(binding.isVarargs(), binding.parameters, true), new String(binding.declaringClass.shortReadableName()) }, 
/* 4894 */       method.sourceStart, 
/* 4895 */       method.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void methodNameClash(MethodBinding currentMethod, MethodBinding inheritedMethod) {
/* 4899 */     handle(
/* 4900 */       67109424, 
/* 4901 */       new String[] { 
/* 4902 */       new String(currentMethod.selector), 
/* 4903 */       typesAsString(currentMethod.isVarargs(), currentMethod.parameters, false), 
/* 4904 */       new String(currentMethod.declaringClass.readableName()), 
/* 4905 */       typesAsString(inheritedMethod.isVarargs(), inheritedMethod.parameters, false), 
/* 4906 */       new String(inheritedMethod.declaringClass.readableName()) }, 
/* 4908 */       new String[] { 
/* 4909 */       new String(currentMethod.selector), 
/* 4910 */       typesAsString(currentMethod.isVarargs(), currentMethod.parameters, true), 
/* 4911 */       new String(currentMethod.declaringClass.shortReadableName()), 
/* 4912 */       typesAsString(inheritedMethod.isVarargs(), inheritedMethod.parameters, true), 
/* 4913 */       new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 4915 */       currentMethod.sourceStart(), 
/* 4916 */       currentMethod.sourceEnd());
/*      */   }
/*      */ 
/*      */   public void methodNeedBody(AbstractMethodDeclaration methodDecl) {
/* 4920 */     handle(
/* 4921 */       603979883, 
/* 4922 */       NoArgument, 
/* 4923 */       NoArgument, 
/* 4924 */       methodDecl.sourceStart, 
/* 4925 */       methodDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void methodNeedingNoBody(MethodDeclaration methodDecl) {
/* 4929 */     handle(
/* 4930 */       (methodDecl.modifiers & 0x100) != 0 ? 603979888 : 603979889, 
/* 4931 */       NoArgument, 
/* 4932 */       NoArgument, 
/* 4933 */       methodDecl.sourceStart, 
/* 4934 */       methodDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void methodWithConstructorName(MethodDeclaration methodDecl) {
/* 4938 */     handle(
/* 4939 */       67108974, 
/* 4940 */       NoArgument, 
/* 4941 */       NoArgument, 
/* 4942 */       methodDecl.sourceStart, 
/* 4943 */       methodDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void missingDeprecatedAnnotationForField(FieldDeclaration field) {
/* 4947 */     int severity = computeSeverity(536871540);
/* 4948 */     if (severity == -1) return;
/* 4949 */     FieldBinding binding = field.binding;
/* 4950 */     handle(
/* 4951 */       536871540, 
/* 4952 */       new String[] { new String(binding.declaringClass.readableName()), new String(binding.name) }, 
/* 4953 */       new String[] { new String(binding.declaringClass.shortReadableName()), new String(binding.name) }, 
/* 4954 */       severity, 
/* 4955 */       nodeSourceStart(binding, field), 
/* 4956 */       nodeSourceEnd(binding, field));
/*      */   }
/*      */ 
/*      */   public void missingDeprecatedAnnotationForMethod(AbstractMethodDeclaration method) {
/* 4960 */     int severity = computeSeverity(536871541);
/* 4961 */     if (severity == -1) return;
/* 4962 */     MethodBinding binding = method.binding;
/* 4963 */     handle(
/* 4964 */       536871541, 
/* 4965 */       new String[] { new String(binding.selector), typesAsString(binding.isVarargs(), binding.parameters, false), new String(binding.declaringClass.readableName()) }, 
/* 4966 */       new String[] { new String(binding.selector), typesAsString(binding.isVarargs(), binding.parameters, true), new String(binding.declaringClass.shortReadableName()) }, 
/* 4967 */       severity, 
/* 4968 */       method.sourceStart, 
/* 4969 */       method.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void missingDeprecatedAnnotationForType(TypeDeclaration type) {
/* 4973 */     int severity = computeSeverity(536871542);
/* 4974 */     if (severity == -1) return;
/* 4975 */     TypeBinding binding = type.binding;
/* 4976 */     handle(
/* 4977 */       536871542, 
/* 4978 */       new String[] { new String(binding.readableName()) }, 
/* 4979 */       new String[] { new String(binding.shortReadableName()) }, 
/* 4980 */       severity, 
/* 4981 */       type.sourceStart, 
/* 4982 */       type.sourceEnd);
/*      */   }
/*      */   public void missingEnumConstantCase(SwitchStatement switchStatement, FieldBinding enumConstant) {
/* 4985 */     handle(
/* 4986 */       33555193, 
/* 4987 */       new String[] { new String(enumConstant.declaringClass.readableName()), new String(enumConstant.name) }, 
/* 4988 */       new String[] { new String(enumConstant.declaringClass.shortReadableName()), new String(enumConstant.name) }, 
/* 4989 */       switchStatement.expression.sourceStart, 
/* 4990 */       switchStatement.expression.sourceEnd);
/*      */   }
/*      */   public void missingOverrideAnnotation(AbstractMethodDeclaration method) {
/* 4993 */     int severity = computeSeverity(67109491);
/* 4994 */     if (severity == -1) return;
/* 4995 */     MethodBinding binding = method.binding;
/* 4996 */     handle(
/* 4997 */       67109491, 
/* 4998 */       new String[] { new String(binding.selector), typesAsString(binding.isVarargs(), binding.parameters, false), new String(binding.declaringClass.readableName()) }, 
/* 4999 */       new String[] { new String(binding.selector), typesAsString(binding.isVarargs(), binding.parameters, true), new String(binding.declaringClass.shortReadableName()) }, 
/* 5000 */       severity, 
/* 5001 */       method.sourceStart, 
/* 5002 */       method.sourceEnd);
/*      */   }
/*      */   public void missingReturnType(AbstractMethodDeclaration methodDecl) {
/* 5005 */     handle(
/* 5006 */       16777327, 
/* 5007 */       NoArgument, 
/* 5008 */       NoArgument, 
/* 5009 */       methodDecl.sourceStart, 
/* 5010 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void missingSemiColon(Expression expression) {
/* 5013 */     handle(
/* 5014 */       1610612960, 
/* 5015 */       NoArgument, 
/* 5016 */       NoArgument, 
/* 5017 */       expression.sourceStart, 
/* 5018 */       expression.sourceEnd);
/*      */   }
/*      */   public void missingSerialVersion(TypeDeclaration typeDecl) {
/* 5021 */     String[] arguments = { new String(typeDecl.name) };
/* 5022 */     handle(
/* 5023 */       536871008, 
/* 5024 */       arguments, 
/* 5025 */       arguments, 
/* 5026 */       typeDecl.sourceStart, 
/* 5027 */       typeDecl.sourceEnd);
/*      */   }
/*      */   public void missingSynchronizedOnInheritedMethod(MethodBinding currentMethod, MethodBinding inheritedMethod) {
/* 5030 */     handle(
/* 5031 */       67109281, 
/* 5032 */       new String[] { 
/* 5033 */       new String(currentMethod.declaringClass.readableName()), 
/* 5034 */       new String(currentMethod.selector), 
/* 5035 */       typesAsString(currentMethod.isVarargs(), currentMethod.parameters, false) }, 
/* 5037 */       new String[] { 
/* 5038 */       new String(currentMethod.declaringClass.shortReadableName()), 
/* 5039 */       new String(currentMethod.selector), 
/* 5040 */       typesAsString(currentMethod.isVarargs(), currentMethod.parameters, true) }, 
/* 5042 */       currentMethod.sourceStart(), 
/* 5043 */       currentMethod.sourceEnd());
/*      */   }
/*      */   public void missingTypeInConstructor(ASTNode location, MethodBinding constructor) {
/* 5046 */     List missingTypes = constructor.collectMissingTypes(null);
/* 5047 */     TypeBinding missingType = (TypeBinding)missingTypes.get(0);
/* 5048 */     int start = location.sourceStart;
/* 5049 */     int end = location.sourceEnd;
/* 5050 */     if ((location instanceof QualifiedAllocationExpression)) {
/* 5051 */       QualifiedAllocationExpression qualifiedAllocation = (QualifiedAllocationExpression)location;
/* 5052 */       if (qualifiedAllocation.anonymousType != null) {
/* 5053 */         start = qualifiedAllocation.anonymousType.sourceStart;
/* 5054 */         end = qualifiedAllocation.anonymousType.sourceEnd;
/*      */       }
/*      */     }
/* 5057 */     handle(
/* 5058 */       134217857, 
/* 5059 */       new String[] { 
/* 5060 */       new String(constructor.declaringClass.readableName()), 
/* 5061 */       typesAsString(constructor.isVarargs(), constructor.parameters, false), 
/* 5062 */       new String(missingType.readableName()) }, 
/* 5064 */       new String[] { 
/* 5065 */       new String(constructor.declaringClass.shortReadableName()), 
/* 5066 */       typesAsString(constructor.isVarargs(), constructor.parameters, true), 
/* 5067 */       new String(missingType.shortReadableName()) }, 
/* 5069 */       start, 
/* 5070 */       end);
/*      */   }
/*      */ 
/*      */   public void missingTypeInMethod(MessageSend messageSend, MethodBinding method) {
/* 5074 */     List missingTypes = method.collectMissingTypes(null);
/* 5075 */     TypeBinding missingType = (TypeBinding)missingTypes.get(0);
/* 5076 */     handle(
/* 5077 */       67108984, 
/* 5078 */       new String[] { 
/* 5079 */       new String(method.declaringClass.readableName()), 
/* 5080 */       new String(method.selector), 
/* 5081 */       typesAsString(method.isVarargs(), method.parameters, false), 
/* 5082 */       new String(missingType.readableName()) }, 
/* 5084 */       new String[] { 
/* 5085 */       new String(method.declaringClass.shortReadableName()), 
/* 5086 */       new String(method.selector), 
/* 5087 */       typesAsString(method.isVarargs(), method.parameters, true), 
/* 5088 */       new String(missingType.shortReadableName()) }, 
/* 5090 */       (int)(messageSend.nameSourcePosition >>> 32), 
/* 5091 */       (int)messageSend.nameSourcePosition);
/*      */   }
/*      */   public void missingValueForAnnotationMember(Annotation annotation, char[] memberName) {
/* 5094 */     String memberString = new String(memberName);
/* 5095 */     handle(
/* 5096 */       16777825, 
/* 5097 */       new String[] { new String(annotation.resolvedType.readableName()), memberString }, 
/* 5098 */       new String[] { new String(annotation.resolvedType.shortReadableName()), memberString }, 
/* 5099 */       annotation.sourceStart, 
/* 5100 */       annotation.sourceEnd);
/*      */   }
/*      */   public void mustDefineDimensionsOrInitializer(ArrayAllocationExpression expression) {
/* 5103 */     handle(
/* 5104 */       536871071, 
/* 5105 */       NoArgument, 
/* 5106 */       NoArgument, 
/* 5107 */       expression.sourceStart, 
/* 5108 */       expression.sourceEnd);
/*      */   }
/*      */   public void mustSpecifyPackage(CompilationUnitDeclaration compUnitDecl) {
/* 5111 */     String[] arguments = { new String(compUnitDecl.getFileName()) };
/* 5112 */     handle(
/* 5113 */       536871238, 
/* 5114 */       arguments, 
/* 5115 */       arguments, 
/* 5116 */       compUnitDecl.sourceStart, 
/* 5117 */       compUnitDecl.sourceStart + 1);
/*      */   }
/*      */   public void mustUseAStaticMethod(MessageSend messageSend, MethodBinding method) {
/* 5120 */     handle(
/* 5121 */       603979977, 
/* 5122 */       new String[] { new String(method.declaringClass.readableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 5123 */       new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 5124 */       messageSend.sourceStart, 
/* 5125 */       messageSend.sourceEnd);
/*      */   }
/*      */   public void nativeMethodsCannotBeStrictfp(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
/* 5128 */     String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
/* 5129 */     handle(
/* 5130 */       67109231, 
/* 5131 */       arguments, 
/* 5132 */       arguments, 
/* 5133 */       methodDecl.sourceStart, 
/* 5134 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void needImplementation(ASTNode location) {
/* 5137 */     abortDueToInternalError(Messages.abort_missingCode, location);
/*      */   }
/*      */ 
/*      */   public void needToEmulateFieldAccess(FieldBinding field, ASTNode location, boolean isReadAccess) {
/* 5141 */     int id = isReadAccess ? 
/* 5142 */       33554622 : 
/* 5143 */       33554623;
/* 5144 */     int severity = computeSeverity(id);
/* 5145 */     if (severity == -1) return;
/* 5146 */     handle(
/* 5147 */       id, 
/* 5148 */       new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 5149 */       new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 5150 */       severity, 
/* 5151 */       nodeSourceStart(field, location), 
/* 5152 */       nodeSourceEnd(field, location));
/*      */   }
/*      */ 
/*      */   public void needToEmulateMethodAccess(MethodBinding method, ASTNode location)
/*      */   {
/* 5158 */     if (method.isConstructor()) {
/* 5159 */       int severity = computeSeverity(67109057);
/* 5160 */       if (severity == -1) return;
/* 5161 */       if (method.declaringClass.isEnum())
/* 5162 */         return;
/* 5163 */       handle(
/* 5164 */         67109057, 
/* 5165 */         new String[] { 
/* 5166 */         new String(method.declaringClass.readableName()), 
/* 5167 */         typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 5169 */         new String[] { 
/* 5170 */         new String(method.declaringClass.shortReadableName()), 
/* 5171 */         typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 5173 */         severity, 
/* 5174 */         location.sourceStart, 
/* 5175 */         location.sourceEnd);
/* 5176 */       return;
/*      */     }
/* 5178 */     int severity = computeSeverity(67109056);
/* 5179 */     if (severity == -1) return;
/* 5180 */     handle(
/* 5181 */       67109056, 
/* 5182 */       new String[] { 
/* 5183 */       new String(method.declaringClass.readableName()), 
/* 5184 */       new String(method.selector), 
/* 5185 */       typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 5187 */       new String[] { 
/* 5188 */       new String(method.declaringClass.shortReadableName()), 
/* 5189 */       new String(method.selector), 
/* 5190 */       typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 5192 */       severity, 
/* 5193 */       location.sourceStart, 
/* 5194 */       location.sourceEnd);
/*      */   }
/*      */   public void noAdditionalBoundAfterTypeVariable(TypeReference boundReference) {
/* 5197 */     handle(
/* 5198 */       16777789, 
/* 5199 */       new String[] { new String(boundReference.resolvedType.readableName()) }, 
/* 5200 */       new String[] { new String(boundReference.resolvedType.shortReadableName()) }, 
/* 5201 */       boundReference.sourceStart, 
/* 5202 */       boundReference.sourceEnd);
/*      */   }
/*      */   private int nodeSourceEnd(Binding field, ASTNode node) {
/* 5205 */     return nodeSourceEnd(field, node, 0);
/*      */   }
/*      */   private int nodeSourceEnd(Binding field, ASTNode node, int index) {
/* 5208 */     if ((node instanceof ArrayTypeReference))
/* 5209 */       return ((ArrayTypeReference)node).originalSourceEnd;
/* 5210 */     if ((node instanceof QualifiedNameReference)) {
/* 5211 */       QualifiedNameReference ref = (QualifiedNameReference)node;
/* 5212 */       if (ref.binding == field) {
/* 5213 */         if (index == 0) {
/* 5214 */           return (int)ref.sourcePositions[(ref.indexOfFirstFieldBinding - 1)];
/*      */         }
/* 5216 */         return (int)ref.sourcePositions[index];
/*      */       }
/*      */ 
/* 5219 */       FieldBinding[] otherFields = ref.otherBindings;
/* 5220 */       if (otherFields != null) {
/* 5221 */         int offset = ref.indexOfFirstFieldBinding;
/* 5222 */         if (index != 0) {
/* 5223 */           int i = 0; for (int length = otherFields.length; i < length; i++)
/* 5224 */             if ((otherFields[i] == field) && (i + offset == index))
/* 5225 */               return (int)ref.sourcePositions[(i + offset)];
/*      */         }
/*      */         else
/*      */         {
/* 5229 */           int i = 0; for (int length = otherFields.length; i < length; i++)
/* 5230 */             if (otherFields[i] == field)
/* 5231 */               return (int)ref.sourcePositions[(i + offset)];
/*      */         }
/*      */       }
/*      */     }
/* 5235 */     else if ((node instanceof ParameterizedQualifiedTypeReference)) {
/* 5236 */       ParameterizedQualifiedTypeReference reference = (ParameterizedQualifiedTypeReference)node;
/* 5237 */       if (index < reference.sourcePositions.length)
/* 5238 */         return (int)reference.sourcePositions[index];
/*      */     }
/* 5240 */     else if ((node instanceof ArrayQualifiedTypeReference)) {
/* 5241 */       ArrayQualifiedTypeReference reference = (ArrayQualifiedTypeReference)node;
/* 5242 */       int length = reference.sourcePositions.length;
/* 5243 */       return (int)reference.sourcePositions[(length - 1)];
/*      */     }
/* 5245 */     return node.sourceEnd;
/*      */   }
/*      */   private int nodeSourceStart(Binding field, ASTNode node) {
/* 5248 */     return nodeSourceStart(field, node, 0);
/*      */   }
/*      */   private int nodeSourceStart(Binding field, ASTNode node, int index) {
/* 5251 */     if ((node instanceof FieldReference)) {
/* 5252 */       FieldReference fieldReference = (FieldReference)node;
/* 5253 */       return (int)(fieldReference.nameSourcePosition >> 32);
/* 5254 */     }if ((node instanceof QualifiedNameReference)) {
/* 5255 */       QualifiedNameReference ref = (QualifiedNameReference)node;
/* 5256 */       if (ref.binding == field) {
/* 5257 */         if (index == 0) {
/* 5258 */           return (int)(ref.sourcePositions[(ref.indexOfFirstFieldBinding - 1)] >> 32);
/*      */         }
/* 5260 */         return (int)(ref.sourcePositions[index] >> 32);
/*      */       }
/*      */ 
/* 5263 */       FieldBinding[] otherFields = ref.otherBindings;
/* 5264 */       if (otherFields != null) {
/* 5265 */         int offset = ref.indexOfFirstFieldBinding;
/* 5266 */         if (index != 0) {
/* 5267 */           int i = 0; for (int length = otherFields.length; i < length; i++)
/* 5268 */             if ((otherFields[i] == field) && (i + offset == index))
/* 5269 */               return (int)(ref.sourcePositions[(i + offset)] >> 32);
/*      */         }
/*      */         else
/*      */         {
/* 5273 */           int i = 0; for (int length = otherFields.length; i < length; i++) {
/* 5274 */             if (otherFields[i] == field)
/* 5275 */               return (int)(ref.sourcePositions[(i + offset)] >> 32);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 5280 */     else if ((node instanceof ParameterizedQualifiedTypeReference)) {
/* 5281 */       ParameterizedQualifiedTypeReference reference = (ParameterizedQualifiedTypeReference)node;
/* 5282 */       return (int)(reference.sourcePositions[0] >>> 32);
/*      */     }
/* 5284 */     return node.sourceStart;
/*      */   }
/*      */   public void noMoreAvailableSpaceForArgument(LocalVariableBinding local, ASTNode location) {
/* 5287 */     String[] arguments = { new String(local.name) };
/* 5288 */     handle(
/* 5289 */       (local instanceof SyntheticArgumentBinding) ? 
/* 5290 */       536870979 : 
/* 5291 */       536870977, 
/* 5292 */       arguments, 
/* 5293 */       arguments, 
/* 5294 */       159, 
/* 5295 */       nodeSourceStart(local, location), 
/* 5296 */       nodeSourceEnd(local, location));
/*      */   }
/*      */   public void noMoreAvailableSpaceForConstant(TypeDeclaration typeDeclaration) {
/* 5299 */     handle(
/* 5300 */       536871343, 
/* 5301 */       new String[] { new String(typeDeclaration.binding.readableName()) }, 
/* 5302 */       new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 
/* 5303 */       159, 
/* 5304 */       typeDeclaration.sourceStart, 
/* 5305 */       typeDeclaration.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void noMoreAvailableSpaceForLocal(LocalVariableBinding local, ASTNode location) {
/* 5309 */     String[] arguments = { new String(local.name) };
/* 5310 */     handle(
/* 5311 */       536870978, 
/* 5312 */       arguments, 
/* 5313 */       arguments, 
/* 5314 */       159, 
/* 5315 */       nodeSourceStart(local, location), 
/* 5316 */       nodeSourceEnd(local, location));
/*      */   }
/*      */   public void noMoreAvailableSpaceInConstantPool(TypeDeclaration typeDeclaration) {
/* 5319 */     handle(
/* 5320 */       536871342, 
/* 5321 */       new String[] { new String(typeDeclaration.binding.readableName()) }, 
/* 5322 */       new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 
/* 5323 */       159, 
/* 5324 */       typeDeclaration.sourceStart, 
/* 5325 */       typeDeclaration.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void nonExternalizedStringLiteral(ASTNode location) {
/* 5329 */     handle(
/* 5330 */       536871173, 
/* 5331 */       NoArgument, 
/* 5332 */       NoArgument, 
/* 5333 */       location.sourceStart, 
/* 5334 */       location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void nonGenericTypeCannotBeParameterized(int index, ASTNode location, TypeBinding type, TypeBinding[] argumentTypes) {
/* 5338 */     if (location == null) {
/* 5339 */       handle(
/* 5340 */         16777740, 
/* 5341 */         new String[] { new String(type.readableName()), typesAsString(false, argumentTypes, false) }, 
/* 5342 */         new String[] { new String(type.shortReadableName()), typesAsString(false, argumentTypes, true) }, 
/* 5343 */         131, 
/* 5344 */         0, 
/* 5345 */         0);
/* 5346 */       return;
/*      */     }
/* 5348 */     handle(
/* 5349 */       16777740, 
/* 5350 */       new String[] { new String(type.readableName()), typesAsString(false, argumentTypes, false) }, 
/* 5351 */       new String[] { new String(type.shortReadableName()), typesAsString(false, argumentTypes, true) }, 
/* 5352 */       nodeSourceStart(null, location), 
/* 5353 */       nodeSourceEnd(null, location, index));
/*      */   }
/*      */   public void nonStaticAccessToStaticField(ASTNode location, FieldBinding field) {
/* 5356 */     nonStaticAccessToStaticField(location, field, -1);
/*      */   }
/*      */   public void nonStaticAccessToStaticField(ASTNode location, FieldBinding field, int index) {
/* 5359 */     int severity = computeSeverity(570425420);
/* 5360 */     if (severity == -1) return;
/* 5361 */     handle(
/* 5362 */       570425420, 
/* 5363 */       new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 5364 */       new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 5365 */       severity, 
/* 5366 */       nodeSourceStart(field, location, index), 
/* 5367 */       nodeSourceEnd(field, location, index));
/*      */   }
/*      */   public void nonStaticAccessToStaticMethod(ASTNode location, MethodBinding method) {
/* 5370 */     handle(
/* 5371 */       603979893, 
/* 5372 */       new String[] { new String(method.declaringClass.readableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 5373 */       new String[] { new String(method.declaringClass.shortReadableName()), new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 5374 */       location.sourceStart, 
/* 5375 */       location.sourceEnd);
/*      */   }
/*      */   public void nonStaticContextForEnumMemberType(SourceTypeBinding type) {
/* 5378 */     String[] arguments = { new String(type.sourceName()) };
/* 5379 */     handle(
/* 5380 */       536870944, 
/* 5381 */       arguments, 
/* 5382 */       arguments, 
/* 5383 */       type.sourceStart(), 
/* 5384 */       type.sourceEnd());
/*      */   }
/*      */ 
/*      */   public void noSuchEnclosingInstance(TypeBinding targetType, ASTNode location, boolean isConstructorCall)
/*      */   {
/*      */     int id;
/*      */     int id;
/* 5390 */     if (isConstructorCall)
/*      */     {
/* 5392 */       id = 536870940;
/*      */     }
/*      */     else
/*      */     {
/*      */       int id;
/* 5393 */       if (((location instanceof ExplicitConstructorCall)) && 
/* 5394 */         (((ExplicitConstructorCall)location).accessMode == 1))
/*      */       {
/* 5396 */         id = 16777236;
/*      */       }
/*      */       else
/*      */       {
/*      */         int id;
/* 5397 */         if (((location instanceof AllocationExpression)) && (
/* 5398 */           (((AllocationExpression)location).binding.declaringClass.isMemberType()) || (
/* 5399 */           (((AllocationExpression)location).binding.declaringClass.isAnonymousType()) && 
/* 5400 */           (((AllocationExpression)location).binding.declaringClass.superclass().isMemberType()))))
/*      */         {
/* 5402 */           id = 16777237;
/*      */         }
/*      */         else
/* 5405 */           id = 16777238;
/*      */       }
/*      */     }
/* 5408 */     handle(
/* 5409 */       id, 
/* 5410 */       new String[] { new String(targetType.readableName()) }, 
/* 5411 */       new String[] { new String(targetType.shortReadableName()) }, 
/* 5412 */       location.sourceStart, 
/* 5413 */       location.sourceEnd);
/*      */   }
/*      */   public void notCompatibleTypesError(EqualExpression expression, TypeBinding leftType, TypeBinding rightType) {
/* 5416 */     String leftName = new String(leftType.readableName());
/* 5417 */     String rightName = new String(rightType.readableName());
/* 5418 */     String leftShortName = new String(leftType.shortReadableName());
/* 5419 */     String rightShortName = new String(rightType.shortReadableName());
/* 5420 */     if (leftShortName.equals(rightShortName)) {
/* 5421 */       leftShortName = leftName;
/* 5422 */       rightShortName = rightName;
/*      */     }
/* 5424 */     handle(
/* 5425 */       16777231, 
/* 5426 */       new String[] { leftName, rightName }, 
/* 5427 */       new String[] { leftShortName, rightShortName }, 
/* 5428 */       expression.sourceStart, 
/* 5429 */       expression.sourceEnd);
/*      */   }
/*      */   public void notCompatibleTypesError(InstanceOfExpression expression, TypeBinding leftType, TypeBinding rightType) {
/* 5432 */     String leftName = new String(leftType.readableName());
/* 5433 */     String rightName = new String(rightType.readableName());
/* 5434 */     String leftShortName = new String(leftType.shortReadableName());
/* 5435 */     String rightShortName = new String(rightType.shortReadableName());
/* 5436 */     if (leftShortName.equals(rightShortName)) {
/* 5437 */       leftShortName = leftName;
/* 5438 */       rightShortName = rightName;
/*      */     }
/* 5440 */     handle(
/* 5441 */       16777232, 
/* 5442 */       new String[] { leftName, rightName }, 
/* 5443 */       new String[] { leftShortName, rightShortName }, 
/* 5444 */       expression.sourceStart, 
/* 5445 */       expression.sourceEnd);
/*      */   }
/*      */   public void notCompatibleTypesErrorInForeach(Expression expression, TypeBinding leftType, TypeBinding rightType) {
/* 5448 */     String leftName = new String(leftType.readableName());
/* 5449 */     String rightName = new String(rightType.readableName());
/* 5450 */     String leftShortName = new String(leftType.shortReadableName());
/* 5451 */     String rightShortName = new String(rightType.shortReadableName());
/* 5452 */     if (leftShortName.equals(rightShortName)) {
/* 5453 */       leftShortName = leftName;
/* 5454 */       rightShortName = rightName;
/*      */     }
/* 5456 */     handle(
/* 5457 */       16777796, 
/* 5458 */       new String[] { leftName, rightName }, 
/* 5459 */       new String[] { leftShortName, rightShortName }, 
/* 5460 */       expression.sourceStart, 
/* 5461 */       expression.sourceEnd);
/*      */   }
/*      */   public void objectCannotBeGeneric(TypeDeclaration typeDecl) {
/* 5464 */     handle(
/* 5465 */       536871435, 
/* 5466 */       NoArgument, 
/* 5467 */       NoArgument, 
/* 5468 */       typeDecl.typeParameters[0].sourceStart, 
/* 5469 */       typeDecl.typeParameters[(typeDecl.typeParameters.length - 1)].sourceEnd);
/*      */   }
/*      */   public void objectCannotHaveSuperTypes(SourceTypeBinding type) {
/* 5472 */     handle(
/* 5473 */       536871241, 
/* 5474 */       NoArgument, 
/* 5475 */       NoArgument, 
/* 5476 */       type.sourceStart(), 
/* 5477 */       type.sourceEnd());
/*      */   }
/*      */   public void objectMustBeClass(SourceTypeBinding type) {
/* 5480 */     handle(
/* 5481 */       536871242, 
/* 5482 */       NoArgument, 
/* 5483 */       NoArgument, 
/* 5484 */       type.sourceStart(), 
/* 5485 */       type.sourceEnd());
/*      */   }
/*      */   public void operatorOnlyValidOnNumericType(CompoundAssignment assignment, TypeBinding leftType, TypeBinding rightType) {
/* 5488 */     String leftName = new String(leftType.readableName());
/* 5489 */     String rightName = new String(rightType.readableName());
/* 5490 */     String leftShortName = new String(leftType.shortReadableName());
/* 5491 */     String rightShortName = new String(rightType.shortReadableName());
/* 5492 */     if (leftShortName.equals(rightShortName)) {
/* 5493 */       leftShortName = leftName;
/* 5494 */       rightShortName = rightName;
/*      */     }
/* 5496 */     handle(
/* 5497 */       16777233, 
/* 5498 */       new String[] { leftName, rightName }, 
/* 5499 */       new String[] { leftShortName, rightShortName }, 
/* 5500 */       assignment.sourceStart, 
/* 5501 */       assignment.sourceEnd);
/*      */   }
/*      */   public void overridesDeprecatedMethod(MethodBinding localMethod, MethodBinding inheritedMethod) {
/* 5504 */     handle(
/* 5505 */       67109276, 
/* 5506 */       new String[] { 
/* 5507 */       new String(
/* 5508 */       CharOperation.concat(
/* 5509 */       localMethod.declaringClass.readableName(), 
/* 5510 */       localMethod.readableName(), 
/* 5511 */       '.')), 
/* 5512 */       new String(inheritedMethod.declaringClass.readableName()) }, 
/* 5513 */       new String[] { 
/* 5514 */       new String(
/* 5515 */       CharOperation.concat(
/* 5516 */       localMethod.declaringClass.shortReadableName(), 
/* 5517 */       localMethod.shortReadableName(), 
/* 5518 */       '.')), 
/* 5519 */       new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 5520 */       localMethod.sourceStart(), 
/* 5521 */       localMethod.sourceEnd());
/*      */   }
/*      */   public void overridesMethodWithoutSuperInvocation(MethodBinding localMethod) {
/* 5524 */     handle(
/* 5525 */       67109280, 
/* 5526 */       new String[] { 
/* 5527 */       new String(
/* 5528 */       CharOperation.concat(
/* 5529 */       localMethod.declaringClass.readableName(), 
/* 5530 */       localMethod.readableName(), 
/* 5531 */       '.')) }, 
/* 5533 */       new String[] { 
/* 5534 */       new String(
/* 5535 */       CharOperation.concat(
/* 5536 */       localMethod.declaringClass.shortReadableName(), 
/* 5537 */       localMethod.shortReadableName(), 
/* 5538 */       '.')) }, 
/* 5540 */       localMethod.sourceStart(), 
/* 5541 */       localMethod.sourceEnd());
/*      */   }
/*      */   public void overridesPackageDefaultMethod(MethodBinding localMethod, MethodBinding inheritedMethod) {
/* 5544 */     handle(
/* 5545 */       67109274, 
/* 5546 */       new String[] { 
/* 5547 */       new String(
/* 5548 */       CharOperation.concat(
/* 5549 */       localMethod.declaringClass.readableName(), 
/* 5550 */       localMethod.readableName(), 
/* 5551 */       '.')), 
/* 5552 */       new String(inheritedMethod.declaringClass.readableName()) }, 
/* 5553 */       new String[] { 
/* 5554 */       new String(
/* 5555 */       CharOperation.concat(
/* 5556 */       localMethod.declaringClass.shortReadableName(), 
/* 5557 */       localMethod.shortReadableName(), 
/* 5558 */       '.')), 
/* 5559 */       new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 5560 */       localMethod.sourceStart(), 
/* 5561 */       localMethod.sourceEnd());
/*      */   }
/*      */   public void packageCollidesWithType(CompilationUnitDeclaration compUnitDecl) {
/* 5564 */     String[] arguments = { CharOperation.toString(compUnitDecl.currentPackage.tokens) };
/* 5565 */     handle(
/* 5566 */       16777537, 
/* 5567 */       arguments, 
/* 5568 */       arguments, 
/* 5569 */       compUnitDecl.currentPackage.sourceStart, 
/* 5570 */       compUnitDecl.currentPackage.sourceEnd);
/*      */   }
/*      */   public void packageIsNotExpectedPackage(CompilationUnitDeclaration compUnitDecl) {
/* 5573 */     String[] arguments = { 
/* 5574 */       CharOperation.toString(compUnitDecl.compilationResult.compilationUnit.getPackageName()), 
/* 5575 */       compUnitDecl.currentPackage == null ? "" : CharOperation.toString(compUnitDecl.currentPackage.tokens) };
/*      */ 
/* 5577 */     handle(
/* 5578 */       536871240, 
/* 5579 */       arguments, 
/* 5580 */       arguments, 
/* 5581 */       compUnitDecl.currentPackage == null ? 0 : compUnitDecl.currentPackage.sourceStart, 
/* 5582 */       compUnitDecl.currentPackage == null ? 0 : compUnitDecl.currentPackage.sourceEnd);
/*      */   }
/*      */   public void parameterAssignment(LocalVariableBinding local, ASTNode location) {
/* 5585 */     int severity = computeSeverity(536870971);
/* 5586 */     if (severity == -1) return;
/* 5587 */     String[] arguments = { new String(local.readableName()) };
/* 5588 */     handle(
/* 5589 */       536870971, 
/* 5590 */       arguments, 
/* 5591 */       arguments, 
/* 5592 */       severity, 
/* 5593 */       nodeSourceStart(local, location), 
/* 5594 */       nodeSourceEnd(local, location));
/*      */   }
/*      */   private String parameterBoundAsString(TypeVariableBinding typeVariable, boolean makeShort) {
/* 5597 */     StringBuffer nameBuffer = new StringBuffer(10);
/* 5598 */     if (typeVariable.firstBound == typeVariable.superclass)
/* 5599 */       nameBuffer.append(makeShort ? typeVariable.superclass.shortReadableName() : typeVariable.superclass.readableName());
/*      */     int length;
/* 5602 */     if ((length = typeVariable.superInterfaces.length) > 0) {
/* 5603 */       for (int i = 0; i < length; i++) {
/* 5604 */         if ((i > 0) || (typeVariable.firstBound == typeVariable.superclass)) nameBuffer.append(" & ");
/* 5605 */         nameBuffer.append(makeShort ? typeVariable.superInterfaces[i].shortReadableName() : typeVariable.superInterfaces[i].readableName());
/*      */       }
/*      */     }
/* 5608 */     return nameBuffer.toString();
/*      */   }
/*      */   public void parameterizedMemberTypeMissingArguments(ASTNode location, TypeBinding type) {
/* 5611 */     if (location == null) {
/* 5612 */       handle(
/* 5613 */         16777778, 
/* 5614 */         new String[] { new String(type.readableName()) }, 
/* 5615 */         new String[] { new String(type.shortReadableName()) }, 
/* 5616 */         131, 
/* 5617 */         0, 
/* 5618 */         0);
/* 5619 */       return;
/*      */     }
/* 5621 */     handle(
/* 5622 */       16777778, 
/* 5623 */       new String[] { new String(type.readableName()) }, 
/* 5624 */       new String[] { new String(type.shortReadableName()) }, 
/* 5625 */       location.sourceStart, 
/* 5626 */       location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void parseError(int startPosition, int endPosition, int currentToken, char[] currentTokenSource, String errorTokenName, String[] possibleTokens)
/*      */   {
/* 5636 */     if (possibleTokens.length == 0) {
/* 5637 */       if (isKeyword(currentToken)) {
/* 5638 */         String[] arguments = { new String(currentTokenSource) };
/* 5639 */         handle(
/* 5640 */           1610612946, 
/* 5641 */           arguments, 
/* 5642 */           arguments, 
/* 5644 */           startPosition, 
/* 5645 */           endPosition);
/* 5646 */         return;
/*      */       }
/* 5648 */       String[] arguments = { errorTokenName };
/* 5649 */       handle(
/* 5650 */         1610612941, 
/* 5651 */         arguments, 
/* 5652 */         arguments, 
/* 5654 */         startPosition, 
/* 5655 */         endPosition);
/* 5656 */       return;
/*      */     }
/*      */ 
/* 5661 */     StringBuffer list = new StringBuffer(20);
/* 5662 */     int i = 0; for (int max = possibleTokens.length; i < max; i++) {
/* 5663 */       if (i > 0)
/* 5664 */         list.append(", ");
/* 5665 */       list.append('"');
/* 5666 */       list.append(possibleTokens[i]);
/* 5667 */       list.append('"');
/*      */     }
/*      */ 
/* 5670 */     if (isKeyword(currentToken)) {
/* 5671 */       String[] arguments = { new String(currentTokenSource), list.toString() };
/* 5672 */       handle(
/* 5673 */         1610612945, 
/* 5674 */         arguments, 
/* 5675 */         arguments, 
/* 5677 */         startPosition, 
/* 5678 */         endPosition);
/* 5679 */       return;
/*      */     }
/*      */ 
/* 5682 */     if ((isLiteral(currentToken)) || 
/* 5683 */       (isIdentifier(currentToken))) {
/* 5684 */       errorTokenName = new String(currentTokenSource);
/*      */     }
/*      */ 
/* 5687 */     String[] arguments = { errorTokenName, list.toString() };
/* 5688 */     handle(
/* 5689 */       1610612940, 
/* 5690 */       arguments, 
/* 5691 */       arguments, 
/* 5693 */       startPosition, 
/* 5694 */       endPosition);
/*      */   }
/*      */ 
/*      */   public void parseErrorDeleteToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName)
/*      */   {
/* 5702 */     syntaxError(
/* 5703 */       1610612968, 
/* 5704 */       start, 
/* 5705 */       end, 
/* 5706 */       currentKind, 
/* 5707 */       errorTokenSource, 
/* 5708 */       errorTokenName, 
/* 5709 */       null);
/*      */   }
/*      */ 
/*      */   public void parseErrorDeleteTokens(int start, int end)
/*      */   {
/* 5715 */     handle(
/* 5716 */       1610612969, 
/* 5717 */       NoArgument, 
/* 5718 */       NoArgument, 
/* 5719 */       start, 
/* 5720 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorInsertAfterToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken)
/*      */   {
/* 5729 */     syntaxError(
/* 5730 */       1610612967, 
/* 5731 */       start, 
/* 5732 */       end, 
/* 5733 */       currentKind, 
/* 5734 */       errorTokenSource, 
/* 5735 */       errorTokenName, 
/* 5736 */       expectedToken);
/*      */   }
/*      */ 
/*      */   public void parseErrorInsertBeforeToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken)
/*      */   {
/* 5745 */     syntaxError(
/* 5746 */       1610612966, 
/* 5747 */       start, 
/* 5748 */       end, 
/* 5749 */       currentKind, 
/* 5750 */       errorTokenSource, 
/* 5751 */       errorTokenName, 
/* 5752 */       expectedToken);
/*      */   }
/*      */ 
/*      */   public void parseErrorInsertToComplete(int start, int end, String inserted, String completed)
/*      */   {
/* 5759 */     String[] arguments = { inserted, completed };
/* 5760 */     handle(
/* 5761 */       1610612976, 
/* 5762 */       arguments, 
/* 5763 */       arguments, 
/* 5764 */       start, 
/* 5765 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorInsertToCompletePhrase(int start, int end, String inserted)
/*      */   {
/* 5772 */     String[] arguments = { inserted };
/* 5773 */     handle(
/* 5774 */       1610612978, 
/* 5775 */       arguments, 
/* 5776 */       arguments, 
/* 5777 */       start, 
/* 5778 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorInsertToCompleteScope(int start, int end, String inserted)
/*      */   {
/* 5784 */     String[] arguments = { inserted };
/* 5785 */     handle(
/* 5786 */       1610612977, 
/* 5787 */       arguments, 
/* 5788 */       arguments, 
/* 5789 */       start, 
/* 5790 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorInvalidToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken)
/*      */   {
/* 5799 */     syntaxError(
/* 5800 */       1610612971, 
/* 5801 */       start, 
/* 5802 */       end, 
/* 5803 */       currentKind, 
/* 5804 */       errorTokenSource, 
/* 5805 */       errorTokenName, 
/* 5806 */       expectedToken);
/*      */   }
/*      */ 
/*      */   public void parseErrorMergeTokens(int start, int end, String expectedToken)
/*      */   {
/* 5812 */     String[] arguments = { expectedToken };
/* 5813 */     handle(
/* 5814 */       1610612970, 
/* 5815 */       arguments, 
/* 5816 */       arguments, 
/* 5817 */       start, 
/* 5818 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorMisplacedConstruct(int start, int end)
/*      */   {
/* 5823 */     handle(
/* 5824 */       1610612972, 
/* 5825 */       NoArgument, 
/* 5826 */       NoArgument, 
/* 5827 */       start, 
/* 5828 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorNoSuggestion(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName)
/*      */   {
/* 5836 */     syntaxError(
/* 5837 */       1610612941, 
/* 5838 */       start, 
/* 5839 */       end, 
/* 5840 */       currentKind, 
/* 5841 */       errorTokenSource, 
/* 5842 */       errorTokenName, 
/* 5843 */       null);
/*      */   }
/*      */ 
/*      */   public void parseErrorNoSuggestionForTokens(int start, int end)
/*      */   {
/* 5848 */     handle(
/* 5849 */       1610612974, 
/* 5850 */       NoArgument, 
/* 5851 */       NoArgument, 
/* 5852 */       start, 
/* 5853 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorReplaceToken(int start, int end, int currentKind, char[] errorTokenSource, String errorTokenName, String expectedToken)
/*      */   {
/* 5862 */     syntaxError(
/* 5863 */       1610612940, 
/* 5864 */       start, 
/* 5865 */       end, 
/* 5866 */       currentKind, 
/* 5867 */       errorTokenSource, 
/* 5868 */       errorTokenName, 
/* 5869 */       expectedToken);
/*      */   }
/*      */ 
/*      */   public void parseErrorReplaceTokens(int start, int end, String expectedToken)
/*      */   {
/* 5875 */     String[] arguments = { expectedToken };
/* 5876 */     handle(
/* 5877 */       1610612973, 
/* 5878 */       arguments, 
/* 5879 */       arguments, 
/* 5880 */       start, 
/* 5881 */       end);
/*      */   }
/*      */ 
/*      */   public void parseErrorUnexpectedEnd(int start, int end)
/*      */   {
/*      */     String[] arguments;
/*      */     String[] arguments;
/* 5888 */     if ((this.referenceContext instanceof ConstructorDeclaration)) {
/* 5889 */       arguments = new String[] { Messages.parser_endOfConstructor };
/*      */     }
/*      */     else
/*      */     {
/*      */       String[] arguments;
/* 5890 */       if ((this.referenceContext instanceof MethodDeclaration)) {
/* 5891 */         arguments = new String[] { Messages.parser_endOfMethod };
/*      */       }
/*      */       else
/*      */       {
/*      */         String[] arguments;
/* 5892 */         if ((this.referenceContext instanceof TypeDeclaration))
/* 5893 */           arguments = new String[] { Messages.parser_endOfInitializer };
/*      */         else
/* 5895 */           arguments = new String[] { Messages.parser_endOfFile }; 
/*      */       }
/*      */     }
/* 5897 */     handle(
/* 5898 */       1610612975, 
/* 5899 */       arguments, 
/* 5900 */       arguments, 
/* 5901 */       start, 
/* 5902 */       end);
/*      */   }
/*      */   public void possibleAccidentalBooleanAssignment(Assignment assignment) {
/* 5905 */     handle(
/* 5906 */       536871091, 
/* 5907 */       NoArgument, 
/* 5908 */       NoArgument, 
/* 5909 */       assignment.sourceStart, 
/* 5910 */       assignment.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void possibleFallThroughCase(CaseStatement caseStatement) {
/* 5914 */     handle(
/* 5915 */       536871106, 
/* 5916 */       NoArgument, 
/* 5917 */       NoArgument, 
/* 5918 */       caseStatement.sourceStart, 
/* 5919 */       caseStatement.sourceEnd);
/*      */   }
/*      */   public void publicClassMustMatchFileName(CompilationUnitDeclaration compUnitDecl, TypeDeclaration typeDecl) {
/* 5922 */     this.referenceContext = typeDecl;
/* 5923 */     String[] arguments = { new String(compUnitDecl.getFileName()), new String(typeDecl.name) };
/* 5924 */     handle(
/* 5925 */       16777541, 
/* 5926 */       arguments, 
/* 5927 */       arguments, 
/* 5928 */       typeDecl.sourceStart, 
/* 5929 */       typeDecl.sourceEnd, 
/* 5930 */       compUnitDecl.compilationResult);
/*      */   }
/*      */   public void rawMemberTypeCannotBeParameterized(ASTNode location, ReferenceBinding type, TypeBinding[] argumentTypes) {
/* 5933 */     if (location == null) {
/* 5934 */       handle(
/* 5935 */         16777777, 
/* 5936 */         new String[] { new String(type.readableName()), typesAsString(false, argumentTypes, false), new String(type.enclosingType().readableName()) }, 
/* 5937 */         new String[] { new String(type.shortReadableName()), typesAsString(false, argumentTypes, true), new String(type.enclosingType().shortReadableName()) }, 
/* 5938 */         131, 
/* 5939 */         0, 
/* 5940 */         0);
/* 5941 */       return;
/*      */     }
/* 5943 */     handle(
/* 5944 */       16777777, 
/* 5945 */       new String[] { new String(type.readableName()), typesAsString(false, argumentTypes, false), new String(type.enclosingType().readableName()) }, 
/* 5946 */       new String[] { new String(type.shortReadableName()), typesAsString(false, argumentTypes, true), new String(type.enclosingType().shortReadableName()) }, 
/* 5947 */       location.sourceStart, 
/* 5948 */       location.sourceEnd);
/*      */   }
/*      */   public void rawTypeReference(ASTNode location, TypeBinding type) {
/* 5951 */     type = type.leafComponentType();
/* 5952 */     handle(
/* 5953 */       16777788, 
/* 5954 */       new String[] { new String(type.readableName()), new String(type.erasure().readableName()) }, 
/* 5955 */       new String[] { new String(type.shortReadableName()), new String(type.erasure().shortReadableName()) }, 
/* 5956 */       location.sourceStart, 
/* 5957 */       nodeSourceEnd(null, location));
/*      */   }
/*      */   public void recursiveConstructorInvocation(ExplicitConstructorCall constructorCall) {
/* 5960 */     handle(
/* 5961 */       134217865, 
/* 5962 */       new String[] { 
/* 5963 */       new String(constructorCall.binding.declaringClass.readableName()), 
/* 5964 */       typesAsString(constructorCall.binding.isVarargs(), constructorCall.binding.parameters, false) }, 
/* 5966 */       new String[] { 
/* 5967 */       new String(constructorCall.binding.declaringClass.shortReadableName()), 
/* 5968 */       typesAsString(constructorCall.binding.isVarargs(), constructorCall.binding.parameters, true) }, 
/* 5970 */       constructorCall.sourceStart, 
/* 5971 */       constructorCall.sourceEnd);
/*      */   }
/*      */   public void redefineArgument(Argument arg) {
/* 5974 */     String[] arguments = { new String(arg.name) };
/* 5975 */     handle(
/* 5976 */       536870968, 
/* 5977 */       arguments, 
/* 5978 */       arguments, 
/* 5979 */       arg.sourceStart, 
/* 5980 */       arg.sourceEnd);
/*      */   }
/*      */   public void redefineLocal(LocalDeclaration localDecl) {
/* 5983 */     String[] arguments = { new String(localDecl.name) };
/* 5984 */     handle(
/* 5985 */       536870967, 
/* 5986 */       arguments, 
/* 5987 */       arguments, 
/* 5988 */       localDecl.sourceStart, 
/* 5989 */       localDecl.sourceEnd);
/*      */   }
/*      */   public void redundantSuperInterface(SourceTypeBinding type, TypeReference reference, ReferenceBinding superinterface, ReferenceBinding declaringType) {
/* 5992 */     int severity = computeSeverity(16777547);
/* 5993 */     if (severity != -1)
/* 5994 */       handle(
/* 5995 */         16777547, 
/* 5996 */         new String[] { 
/* 5997 */         new String(superinterface.readableName()), 
/* 5998 */         new String(type.readableName()), 
/* 5999 */         new String(declaringType.readableName()) }, 
/* 6000 */         new String[] { 
/* 6001 */         new String(superinterface.shortReadableName()), 
/* 6002 */         new String(type.shortReadableName()), 
/* 6003 */         new String(declaringType.shortReadableName()) }, 
/* 6004 */         severity, 
/* 6005 */         reference.sourceStart, 
/* 6006 */         reference.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void referenceMustBeArrayTypeAt(TypeBinding arrayType, ArrayReference arrayRef) {
/* 6010 */     handle(
/* 6011 */       536871062, 
/* 6012 */       new String[] { new String(arrayType.readableName()) }, 
/* 6013 */       new String[] { new String(arrayType.shortReadableName()) }, 
/* 6014 */       arrayRef.sourceStart, 
/* 6015 */       arrayRef.sourceEnd);
/*      */   }
/*      */   public void reset() {
/* 6018 */     this.positionScanner = null;
/*      */   }
/*      */   private int retrieveClosingAngleBracketPosition(int start) {
/* 6021 */     if (this.referenceContext == null) return start;
/* 6022 */     CompilationResult compilationResult = this.referenceContext.compilationResult();
/* 6023 */     if (compilationResult == null) return start;
/* 6024 */     ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
/* 6025 */     if (compilationUnit == null) return start;
/* 6026 */     char[] contents = compilationUnit.getContents();
/* 6027 */     if (contents.length == 0) return start;
/* 6028 */     if (this.positionScanner == null) {
/* 6029 */       this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false);
/* 6030 */       this.positionScanner.returnOnlyGreater = true;
/*      */     }
/* 6032 */     this.positionScanner.setSource(contents);
/* 6033 */     this.positionScanner.resetTo(start, contents.length);
/* 6034 */     int end = start;
/* 6035 */     int count = 0;
/*      */     try
/*      */     {
/*      */       int token;
/* 6038 */       while ((token = this.positionScanner.getNextToken()) != 68)
/*      */       {
/*      */         int token;
/* 6039 */         switch (token) {
/*      */         case 7:
/* 6041 */           count++;
/* 6042 */           break;
/*      */         case 13:
/* 6044 */           count--;
/* 6045 */           if (count != 0) continue;
/* 6046 */           end = this.positionScanner.currentPosition - 1;
/* 6047 */           break;
/*      */         case 69:
/* 6051 */           break label214;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException) {
/*      */     }
/* 6057 */     label214: return end;
/*      */   }
/*      */   private int retrieveEndingPositionAfterOpeningParenthesis(int sourceStart, int sourceEnd, int numberOfParen) {
/* 6060 */     if (this.referenceContext == null) return sourceEnd;
/* 6061 */     CompilationResult compilationResult = this.referenceContext.compilationResult();
/* 6062 */     if (compilationResult == null) return sourceEnd;
/* 6063 */     ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
/* 6064 */     if (compilationUnit == null) return sourceEnd;
/* 6065 */     char[] contents = compilationUnit.getContents();
/* 6066 */     if (contents.length == 0) return sourceEnd;
/* 6067 */     if (this.positionScanner == null) {
/* 6068 */       this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false);
/*      */     }
/* 6070 */     this.positionScanner.setSource(contents);
/* 6071 */     this.positionScanner.resetTo(sourceStart, sourceEnd);
/*      */     try
/*      */     {
/* 6074 */       int previousSourceEnd = sourceEnd;
/*      */       int token;
/* 6075 */       while ((token = this.positionScanner.getNextToken()) != 68)
/*      */       {
/*      */         int token;
/* 6076 */         switch (token) {
/*      */         case 29:
/* 6078 */           return previousSourceEnd;
/*      */         }
/* 6080 */         previousSourceEnd = this.positionScanner.currentPosition - 1;
/*      */       }
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException)
/*      */     {
/*      */     }
/* 6086 */     return sourceEnd;
/*      */   }
/*      */   private int retrieveStartingPositionAfterOpeningParenthesis(int sourceStart, int sourceEnd, int numberOfParen) {
/* 6089 */     if (this.referenceContext == null) return sourceStart;
/* 6090 */     CompilationResult compilationResult = this.referenceContext.compilationResult();
/* 6091 */     if (compilationResult == null) return sourceStart;
/* 6092 */     ICompilationUnit compilationUnit = compilationResult.getCompilationUnit();
/* 6093 */     if (compilationUnit == null) return sourceStart;
/* 6094 */     char[] contents = compilationUnit.getContents();
/* 6095 */     if (contents.length == 0) return sourceStart;
/* 6096 */     if (this.positionScanner == null) {
/* 6097 */       this.positionScanner = new Scanner(false, false, false, this.options.sourceLevel, this.options.complianceLevel, null, null, false);
/*      */     }
/* 6099 */     this.positionScanner.setSource(contents);
/* 6100 */     this.positionScanner.resetTo(sourceStart, sourceEnd);
/* 6101 */     int count = 0;
/*      */     try
/*      */     {
/*      */       int token;
/* 6104 */       while ((token = this.positionScanner.getNextToken()) != 68)
/*      */       {
/*      */         int token;
/* 6105 */         switch (token) {
/*      */         case 28:
/* 6107 */           count++;
/* 6108 */           if (count == numberOfParen) {
/* 6109 */             this.positionScanner.getNextToken();
/* 6110 */             return this.positionScanner.startPosition;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (InvalidInputException localInvalidInputException) {
/*      */     }
/* 6117 */     return sourceStart;
/*      */   }
/*      */   public void returnTypeCannotBeVoidArray(MethodDeclaration methodDecl) {
/* 6120 */     handle(
/* 6121 */       536870966, 
/* 6122 */       NoArgument, 
/* 6123 */       NoArgument, 
/* 6124 */       methodDecl.returnType.sourceStart, 
/* 6125 */       methodDecl.returnType.sourceEnd);
/*      */   }
/*      */   public void scannerError(Parser parser, String errorTokenName) {
/* 6128 */     Scanner scanner = parser.scanner;
/*      */ 
/* 6130 */     int flag = 1610612941;
/* 6131 */     int startPos = scanner.startPosition;
/* 6132 */     int endPos = scanner.currentPosition - 1;
/*      */ 
/* 6135 */     if (errorTokenName.equals("End_Of_Source")) {
/* 6136 */       flag = 1610612986;
/* 6137 */     } else if (errorTokenName.equals("Invalid_Hexa_Literal")) {
/* 6138 */       flag = 1610612987;
/* 6139 */     } else if (errorTokenName.equals("Invalid_Octal_Literal")) {
/* 6140 */       flag = 1610612988;
/* 6141 */     } else if (errorTokenName.equals("Invalid_Character_Constant")) {
/* 6142 */       flag = 1610612989;
/* 6143 */     } else if (errorTokenName.equals("Invalid_Escape")) {
/* 6144 */       flag = 1610612990;
/* 6145 */     } else if (errorTokenName.equals("Invalid_Unicode_Escape")) {
/* 6146 */       flag = 1610612992;
/*      */ 
/* 6148 */       char[] source = scanner.source;
/* 6149 */       int checkPos = scanner.currentPosition - 1;
/* 6150 */       if (checkPos >= source.length) checkPos = source.length - 1;
/* 6151 */       while (checkPos >= startPos) {
/* 6152 */         if (source[checkPos] == '\\') break;
/* 6153 */         checkPos--;
/*      */       }
/* 6155 */       startPos = checkPos;
/* 6156 */     } else if (errorTokenName.equals("Invalid_Low_Surrogate")) {
/* 6157 */       flag = 1610612999;
/* 6158 */     } else if (errorTokenName.equals("Invalid_High_Surrogate")) {
/* 6159 */       flag = 1610613000;
/*      */ 
/* 6161 */       char[] source = scanner.source;
/* 6162 */       int checkPos = scanner.startPosition + 1;
/* 6163 */       while (checkPos <= endPos) {
/* 6164 */         if (source[checkPos] == '\\') break;
/* 6165 */         checkPos++;
/*      */       }
/* 6167 */       endPos = checkPos - 1;
/* 6168 */     } else if (errorTokenName.equals("Invalid_Float_Literal")) {
/* 6169 */       flag = 1610612993;
/* 6170 */     } else if (errorTokenName.equals("Unterminated_String")) {
/* 6171 */       flag = 1610612995;
/* 6172 */     } else if (errorTokenName.equals("Unterminated_Comment")) {
/* 6173 */       flag = 1610612996;
/* 6174 */     } else if (errorTokenName.equals("Invalid_Char_In_String")) {
/* 6175 */       flag = 1610612995;
/* 6176 */     } else if (errorTokenName.equals("Invalid_Digit")) {
/* 6177 */       flag = 1610612998;
/*      */     }
/* 6179 */     String[] arguments = flag == 1610612941 ? 
/* 6180 */       new String[] { errorTokenName } : 
/* 6181 */       NoArgument;
/* 6182 */     handle(
/* 6183 */       flag, 
/* 6184 */       arguments, 
/* 6185 */       arguments, 
/* 6187 */       startPos, 
/* 6188 */       endPos, 
/* 6189 */       parser.compilationUnit.compilationResult);
/*      */   }
/*      */   public void shouldImplementHashcode(SourceTypeBinding type) {
/* 6192 */     handle(
/* 6193 */       16777548, 
/* 6194 */       new String[] { new String(type.readableName()) }, 
/* 6195 */       new String[] { new String(type.shortReadableName()) }, 
/* 6196 */       type.sourceStart(), 
/* 6197 */       type.sourceEnd());
/*      */   }
/*      */   public void shouldReturn(TypeBinding returnType, ASTNode location) {
/* 6200 */     handle(
/* 6201 */       603979884, 
/* 6202 */       new String[] { new String(returnType.readableName()) }, 
/* 6203 */       new String[] { new String(returnType.shortReadableName()) }, 
/* 6204 */       location.sourceStart, 
/* 6205 */       location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void signalNoImplicitStringConversionForCharArrayExpression(Expression expression) {
/* 6209 */     handle(
/* 6210 */       536871063, 
/* 6211 */       NoArgument, 
/* 6212 */       NoArgument, 
/* 6213 */       expression.sourceStart, 
/* 6214 */       expression.sourceEnd);
/*      */   }
/*      */   public void staticAndInstanceConflict(MethodBinding currentMethod, MethodBinding inheritedMethod) {
/* 6217 */     if (currentMethod.isStatic())
/* 6218 */       handle(
/* 6221 */         67109271, 
/* 6222 */         new String[] { new String(inheritedMethod.declaringClass.readableName()) }, 
/* 6223 */         new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 6224 */         currentMethod.sourceStart(), 
/* 6225 */         currentMethod.sourceEnd());
/*      */     else
/* 6227 */       handle(
/* 6230 */         67109270, 
/* 6231 */         new String[] { new String(inheritedMethod.declaringClass.readableName()) }, 
/* 6232 */         new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 6233 */         currentMethod.sourceStart(), 
/* 6234 */         currentMethod.sourceEnd()); 
/*      */   }
/*      */ 
/*      */   public void staticFieldAccessToNonStaticVariable(ASTNode location, FieldBinding field) {
/* 6237 */     String[] arguments = { new String(field.readableName()) };
/* 6238 */     handle(
/* 6239 */       33554506, 
/* 6240 */       arguments, 
/* 6241 */       arguments, 
/* 6242 */       nodeSourceStart(field, location), 
/* 6243 */       nodeSourceEnd(field, location));
/*      */   }
/*      */   public void staticInheritedMethodConflicts(SourceTypeBinding type, MethodBinding concreteMethod, MethodBinding[] abstractMethods) {
/* 6246 */     handle(
/* 6249 */       67109272, 
/* 6250 */       new String[] { 
/* 6251 */       new String(concreteMethod.readableName()), 
/* 6252 */       new String(abstractMethods[0].declaringClass.readableName()) }, 
/* 6253 */       new String[] { 
/* 6254 */       new String(concreteMethod.readableName()), 
/* 6255 */       new String(abstractMethods[0].declaringClass.shortReadableName()) }, 
/* 6256 */       type.sourceStart(), 
/* 6257 */       type.sourceEnd());
/*      */   }
/*      */   public void staticMemberOfParameterizedType(ASTNode location, ReferenceBinding type) {
/* 6260 */     if (location == null) {
/* 6261 */       handle(
/* 6262 */         16777779, 
/* 6263 */         new String[] { new String(type.readableName()), new String(type.enclosingType().readableName()) }, 
/* 6264 */         new String[] { new String(type.shortReadableName()), new String(type.enclosingType().shortReadableName()) }, 
/* 6265 */         131, 
/* 6266 */         0, 
/* 6267 */         0);
/* 6268 */       return;
/*      */     }
/* 6270 */     int end = location.sourceEnd;
/*      */ 
/* 6276 */     handle(
/* 6277 */       16777779, 
/* 6278 */       new String[] { new String(type.readableName()), new String(type.enclosingType().readableName()) }, 
/* 6279 */       new String[] { new String(type.shortReadableName()), new String(type.enclosingType().shortReadableName()) }, 
/* 6280 */       location.sourceStart, 
/* 6281 */       end);
/*      */   }
/*      */   public void stringConstantIsExceedingUtf8Limit(ASTNode location) {
/* 6284 */     handle(
/* 6285 */       536871064, 
/* 6286 */       NoArgument, 
/* 6287 */       NoArgument, 
/* 6288 */       location.sourceStart, 
/* 6289 */       location.sourceEnd);
/*      */   }
/*      */   public void superclassMustBeAClass(SourceTypeBinding type, TypeReference superclassRef, ReferenceBinding superType) {
/* 6292 */     handle(
/* 6293 */       16777528, 
/* 6294 */       new String[] { new String(superType.readableName()), new String(type.sourceName()) }, 
/* 6295 */       new String[] { new String(superType.shortReadableName()), new String(type.sourceName()) }, 
/* 6296 */       superclassRef.sourceStart, 
/* 6297 */       superclassRef.sourceEnd);
/*      */   }
/*      */   public void superfluousSemicolon(int sourceStart, int sourceEnd) {
/* 6300 */     handle(
/* 6301 */       536871092, 
/* 6302 */       NoArgument, 
/* 6303 */       NoArgument, 
/* 6304 */       sourceStart, 
/* 6305 */       sourceEnd);
/*      */   }
/*      */   public void superinterfaceMustBeAnInterface(SourceTypeBinding type, TypeReference superInterfaceRef, ReferenceBinding superType) {
/* 6308 */     handle(
/* 6309 */       16777531, 
/* 6310 */       new String[] { new String(superType.readableName()), new String(type.sourceName()) }, 
/* 6311 */       new String[] { new String(superType.shortReadableName()), new String(type.sourceName()) }, 
/* 6312 */       superInterfaceRef.sourceStart, 
/* 6313 */       superInterfaceRef.sourceEnd);
/*      */   }
/*      */   public void superinterfacesCollide(TypeBinding type, ASTNode decl, TypeBinding superType, TypeBinding inheritedSuperType) {
/* 6316 */     handle(
/* 6317 */       16777755, 
/* 6318 */       new String[] { new String(superType.readableName()), new String(inheritedSuperType.readableName()), new String(type.sourceName()) }, 
/* 6319 */       new String[] { new String(superType.shortReadableName()), new String(inheritedSuperType.shortReadableName()), new String(type.sourceName()) }, 
/* 6320 */       decl.sourceStart, 
/* 6321 */       decl.sourceEnd);
/*      */   }
/*      */   public void superTypeCannotUseWildcard(SourceTypeBinding type, TypeReference superclass, TypeBinding superTypeBinding) {
/* 6324 */     String name = new String(type.sourceName());
/* 6325 */     String superTypeFullName = new String(superTypeBinding.readableName());
/* 6326 */     String superTypeShortName = new String(superTypeBinding.shortReadableName());
/* 6327 */     if (superTypeShortName.equals(name)) superTypeShortName = superTypeFullName;
/* 6328 */     handle(
/* 6329 */       16777772, 
/* 6330 */       new String[] { superTypeFullName, name }, 
/* 6331 */       new String[] { superTypeShortName, name }, 
/* 6332 */       superclass.sourceStart, 
/* 6333 */       superclass.sourceEnd);
/*      */   }
/*      */ 
/*      */   private void syntaxError(int id, int startPosition, int endPosition, int currentKind, char[] currentTokenSource, String errorTokenName, String expectedToken)
/*      */   {
/*      */     String eTokenName;
/*      */     String eTokenName;
/* 6345 */     if ((isKeyword(currentKind)) || 
/* 6346 */       (isLiteral(currentKind)) || 
/* 6347 */       (isIdentifier(currentKind)))
/* 6348 */       eTokenName = new String(currentTokenSource);
/*      */     else
/* 6350 */       eTokenName = errorTokenName;
/*      */     String[] arguments;
/*      */     String[] arguments;
/* 6354 */     if (expectedToken != null)
/* 6355 */       arguments = new String[] { eTokenName, expectedToken };
/*      */     else {
/* 6357 */       arguments = new String[] { eTokenName };
/*      */     }
/* 6359 */     handle(
/* 6360 */       id, 
/* 6361 */       arguments, 
/* 6362 */       arguments, 
/* 6363 */       startPosition, 
/* 6364 */       endPosition);
/*      */   }
/*      */   public void task(String tag, String message, String priority, int start, int end) {
/* 6367 */     handle(
/* 6368 */       536871362, 
/* 6369 */       new String[] { tag, message, priority }, 
/* 6370 */       new String[] { tag, message, priority }, 
/* 6371 */       start, 
/* 6372 */       end);
/*      */   }
/*      */ 
/*      */   public void tooManyDimensions(ASTNode expression) {
/* 6376 */     handle(
/* 6377 */       536870980, 
/* 6378 */       NoArgument, 
/* 6379 */       NoArgument, 
/* 6380 */       expression.sourceStart, 
/* 6381 */       expression.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void tooManyFields(TypeDeclaration typeDeclaration) {
/* 6385 */     handle(
/* 6386 */       536871344, 
/* 6387 */       new String[] { new String(typeDeclaration.binding.readableName()) }, 
/* 6388 */       new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 
/* 6389 */       159, 
/* 6390 */       typeDeclaration.sourceStart, 
/* 6391 */       typeDeclaration.sourceEnd);
/*      */   }
/*      */   public void tooManyMethods(TypeDeclaration typeDeclaration) {
/* 6394 */     handle(
/* 6395 */       536871345, 
/* 6396 */       new String[] { new String(typeDeclaration.binding.readableName()) }, 
/* 6397 */       new String[] { new String(typeDeclaration.binding.shortReadableName()) }, 
/* 6398 */       159, 
/* 6399 */       typeDeclaration.sourceStart, 
/* 6400 */       typeDeclaration.sourceEnd);
/*      */   }
/*      */   public void typeCastError(CastExpression expression, TypeBinding leftType, TypeBinding rightType) {
/* 6403 */     String leftName = new String(leftType.readableName());
/* 6404 */     String rightName = new String(rightType.readableName());
/* 6405 */     String leftShortName = new String(leftType.shortReadableName());
/* 6406 */     String rightShortName = new String(rightType.shortReadableName());
/* 6407 */     if (leftShortName.equals(rightShortName)) {
/* 6408 */       leftShortName = leftName;
/* 6409 */       rightShortName = rightName;
/*      */     }
/* 6411 */     handle(
/* 6412 */       16777372, 
/* 6413 */       new String[] { rightName, leftName }, 
/* 6414 */       new String[] { rightShortName, leftShortName }, 
/* 6415 */       expression.sourceStart, 
/* 6416 */       expression.sourceEnd);
/*      */   }
/*      */   public void typeCollidesWithEnclosingType(TypeDeclaration typeDecl) {
/* 6419 */     String[] arguments = { new String(typeDecl.name) };
/* 6420 */     handle(
/* 6421 */       16777534, 
/* 6422 */       arguments, 
/* 6423 */       arguments, 
/* 6424 */       typeDecl.sourceStart, 
/* 6425 */       typeDecl.sourceEnd);
/*      */   }
/*      */   public void typeCollidesWithPackage(CompilationUnitDeclaration compUnitDecl, TypeDeclaration typeDecl) {
/* 6428 */     this.referenceContext = typeDecl;
/* 6429 */     String[] arguments = { new String(compUnitDecl.getFileName()), new String(typeDecl.name) };
/* 6430 */     handle(
/* 6431 */       16777538, 
/* 6432 */       arguments, 
/* 6433 */       arguments, 
/* 6434 */       typeDecl.sourceStart, 
/* 6435 */       typeDecl.sourceEnd, 
/* 6436 */       compUnitDecl.compilationResult);
/*      */   }
/*      */   public void typeHiding(TypeDeclaration typeDecl, TypeBinding hiddenType) {
/* 6439 */     int severity = computeSeverity(16777249);
/* 6440 */     if (severity == -1) return;
/* 6441 */     handle(
/* 6442 */       16777249, 
/* 6443 */       new String[] { new String(typeDecl.name), new String(hiddenType.shortReadableName()) }, 
/* 6444 */       new String[] { new String(typeDecl.name), new String(hiddenType.readableName()) }, 
/* 6445 */       severity, 
/* 6446 */       typeDecl.sourceStart, 
/* 6447 */       typeDecl.sourceEnd);
/*      */   }
/*      */   public void typeHiding(TypeDeclaration typeDecl, TypeVariableBinding hiddenTypeParameter) {
/* 6450 */     int severity = computeSeverity(16777792);
/* 6451 */     if (severity == -1) return;
/* 6452 */     if ((hiddenTypeParameter.declaringElement instanceof TypeBinding)) {
/* 6453 */       TypeBinding declaringType = (TypeBinding)hiddenTypeParameter.declaringElement;
/* 6454 */       handle(
/* 6455 */         16777792, 
/* 6456 */         new String[] { new String(typeDecl.name), new String(hiddenTypeParameter.readableName()), new String(declaringType.readableName()) }, 
/* 6457 */         new String[] { new String(typeDecl.name), new String(hiddenTypeParameter.shortReadableName()), new String(declaringType.shortReadableName()) }, 
/* 6458 */         severity, 
/* 6459 */         typeDecl.sourceStart, 
/* 6460 */         typeDecl.sourceEnd);
/*      */     }
/*      */     else {
/* 6463 */       MethodBinding declaringMethod = (MethodBinding)hiddenTypeParameter.declaringElement;
/* 6464 */       handle(
/* 6465 */         16777793, 
/* 6466 */         new String[] { 
/* 6467 */         new String(typeDecl.name), 
/* 6468 */         new String(hiddenTypeParameter.readableName()), 
/* 6469 */         new String(declaringMethod.selector), 
/* 6470 */         typesAsString(declaringMethod.isVarargs(), declaringMethod.parameters, false), 
/* 6471 */         new String(declaringMethod.declaringClass.readableName()) }, 
/* 6473 */         new String[] { 
/* 6474 */         new String(typeDecl.name), 
/* 6475 */         new String(hiddenTypeParameter.shortReadableName()), 
/* 6476 */         new String(declaringMethod.selector), 
/* 6477 */         typesAsString(declaringMethod.isVarargs(), declaringMethod.parameters, true), 
/* 6478 */         new String(declaringMethod.declaringClass.shortReadableName()) }, 
/* 6480 */         severity, 
/* 6481 */         typeDecl.sourceStart, 
/* 6482 */         typeDecl.sourceEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void typeHiding(TypeParameter typeParam, Binding hidden) {
/* 6486 */     int severity = computeSeverity(16777787);
/* 6487 */     if (severity == -1) return;
/* 6488 */     TypeBinding hiddenType = (TypeBinding)hidden;
/* 6489 */     handle(
/* 6490 */       16777787, 
/* 6491 */       new String[] { new String(typeParam.name), new String(hiddenType.readableName()) }, 
/* 6492 */       new String[] { new String(typeParam.name), new String(hiddenType.shortReadableName()) }, 
/* 6493 */       severity, 
/* 6494 */       typeParam.sourceStart, 
/* 6495 */       typeParam.sourceEnd);
/*      */   }
/*      */   public void typeMismatchError(TypeBinding actualType, TypeBinding expectedType, ASTNode location, ASTNode expectingLocation) {
/* 6498 */     if ((actualType != null) && ((actualType.tagBits & 0x80) != 0L)) {
/* 6499 */       handle(
/* 6500 */         16777218, 
/* 6501 */         new String[] { new String(actualType.leafComponentType().readableName()) }, 
/* 6502 */         new String[] { new String(actualType.leafComponentType().shortReadableName()) }, 
/* 6503 */         location.sourceStart, 
/* 6504 */         location.sourceEnd);
/* 6505 */       return;
/*      */     }
/* 6507 */     if ((expectingLocation != null) && ((expectedType.tagBits & 0x80) != 0L)) {
/* 6508 */       handle(
/* 6509 */         16777218, 
/* 6510 */         new String[] { new String(expectedType.leafComponentType().readableName()) }, 
/* 6511 */         new String[] { new String(expectedType.leafComponentType().shortReadableName()) }, 
/* 6512 */         expectingLocation.sourceStart, 
/* 6513 */         expectingLocation.sourceEnd);
/* 6514 */       return;
/*      */     }
/* 6516 */     char[] actualShortReadableName = actualType.shortReadableName();
/* 6517 */     char[] expectedShortReadableName = expectedType.shortReadableName();
/* 6518 */     if (CharOperation.equals(actualShortReadableName, expectedShortReadableName)) {
/* 6519 */       actualShortReadableName = actualType.readableName();
/* 6520 */       expectedShortReadableName = expectedType.readableName();
/*      */     }
/* 6522 */     handle(
/* 6523 */       16777233, 
/* 6524 */       new String[] { new String(actualType.readableName()), new String(expectedType.readableName()) }, 
/* 6525 */       new String[] { new String(actualShortReadableName), new String(expectedShortReadableName) }, 
/* 6526 */       location.sourceStart, 
/* 6527 */       location.sourceEnd);
/*      */   }
/*      */   public void typeMismatchError(TypeBinding typeArgument, TypeVariableBinding typeParameter, ReferenceBinding genericType, ASTNode location) {
/* 6530 */     if (location == null) {
/* 6531 */       handle(
/* 6532 */         16777742, 
/* 6533 */         new String[] { new String(typeArgument.readableName()), new String(genericType.readableName()), new String(typeParameter.sourceName), parameterBoundAsString(typeParameter, false) }, 
/* 6534 */         new String[] { new String(typeArgument.shortReadableName()), new String(genericType.shortReadableName()), new String(typeParameter.sourceName), parameterBoundAsString(typeParameter, true) }, 
/* 6535 */         131, 
/* 6536 */         0, 
/* 6537 */         0);
/* 6538 */       return;
/*      */     }
/* 6540 */     handle(
/* 6541 */       16777742, 
/* 6542 */       new String[] { new String(typeArgument.readableName()), new String(genericType.readableName()), new String(typeParameter.sourceName), parameterBoundAsString(typeParameter, false) }, 
/* 6543 */       new String[] { new String(typeArgument.shortReadableName()), new String(genericType.shortReadableName()), new String(typeParameter.sourceName), parameterBoundAsString(typeParameter, true) }, 
/* 6544 */       location.sourceStart, 
/* 6545 */       location.sourceEnd);
/*      */   }
/*      */   private String typesAsString(boolean isVarargs, TypeBinding[] types, boolean makeShort) {
/* 6548 */     StringBuffer buffer = new StringBuffer(10);
/* 6549 */     int i = 0; for (int length = types.length; i < length; i++) {
/* 6550 */       if (i != 0)
/* 6551 */         buffer.append(", ");
/* 6552 */       TypeBinding type = types[i];
/* 6553 */       boolean isVarargType = (isVarargs) && (i == length - 1);
/* 6554 */       if (isVarargType) type = ((ArrayBinding)type).elementsType();
/* 6555 */       buffer.append(new String(makeShort ? type.shortReadableName() : type.readableName()));
/* 6556 */       if (!isVarargType) continue; buffer.append("...");
/*      */     }
/* 6558 */     return buffer.toString();
/*      */   }
/*      */   public void undefinedAnnotationValue(TypeBinding annotationType, MemberValuePair memberValuePair) {
/* 6561 */     if (isRecoveredName(memberValuePair.name)) return;
/* 6562 */     String name = new String(memberValuePair.name);
/* 6563 */     handle(
/* 6564 */       67109475, 
/* 6565 */       new String[] { name, new String(annotationType.readableName()) }, 
/* 6566 */       new String[] { name, new String(annotationType.shortReadableName()) }, 
/* 6567 */       memberValuePair.sourceStart, 
/* 6568 */       memberValuePair.sourceEnd);
/*      */   }
/*      */   public void undefinedLabel(BranchStatement statement) {
/* 6571 */     if (isRecoveredName(statement.label)) return;
/* 6572 */     String[] arguments = { new String(statement.label) };
/* 6573 */     handle(
/* 6574 */       536871086, 
/* 6575 */       arguments, 
/* 6576 */       arguments, 
/* 6577 */       statement.sourceStart, 
/* 6578 */       statement.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void undefinedTypeVariableSignature(char[] variableName, ReferenceBinding binaryType) {
/* 6582 */     handle(
/* 6583 */       536871450, 
/* 6584 */       new String[] { new String(variableName), new String(binaryType.readableName()) }, 
/* 6585 */       new String[] { new String(variableName), new String(binaryType.shortReadableName()) }, 
/* 6586 */       131, 
/* 6587 */       0, 
/* 6588 */       0);
/*      */   }
/*      */   public void undocumentedEmptyBlock(int blockStart, int blockEnd) {
/* 6591 */     handle(
/* 6592 */       536871372, 
/* 6593 */       NoArgument, 
/* 6594 */       NoArgument, 
/* 6595 */       blockStart, 
/* 6596 */       blockEnd);
/*      */   }
/*      */   public void unexpectedStaticModifierForField(SourceTypeBinding type, FieldDeclaration fieldDecl) {
/* 6599 */     String[] arguments = { new String(fieldDecl.name) };
/* 6600 */     handle(
/* 6601 */       33554778, 
/* 6602 */       arguments, 
/* 6603 */       arguments, 
/* 6604 */       fieldDecl.sourceStart, 
/* 6605 */       fieldDecl.sourceEnd);
/*      */   }
/*      */   public void unexpectedStaticModifierForMethod(ReferenceBinding type, AbstractMethodDeclaration methodDecl) {
/* 6608 */     String[] arguments = { new String(type.sourceName()), new String(methodDecl.selector) };
/* 6609 */     handle(
/* 6610 */       67109225, 
/* 6611 */       arguments, 
/* 6612 */       arguments, 
/* 6613 */       methodDecl.sourceStart, 
/* 6614 */       methodDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void unhandledException(TypeBinding exceptionType, ASTNode location) {
/* 6618 */     boolean insideDefaultConstructor = 
/* 6619 */       ((this.referenceContext instanceof ConstructorDeclaration)) && 
/* 6620 */       (((ConstructorDeclaration)this.referenceContext).isDefaultConstructor());
/* 6621 */     boolean insideImplicitConstructorCall = 
/* 6622 */       ((location instanceof ExplicitConstructorCall)) && 
/* 6623 */       (((ExplicitConstructorCall)location).accessMode == 1);
/*      */ 
/* 6625 */     handle(
/* 6628 */       insideImplicitConstructorCall ? 
/* 6629 */       134217871 : insideDefaultConstructor ? 
/* 6627 */       16777362 : 
/* 6630 */       16777384, 
/* 6631 */       new String[] { new String(exceptionType.readableName()) }, 
/* 6632 */       new String[] { new String(exceptionType.shortReadableName()) }, 
/* 6633 */       location.sourceStart, 
/* 6634 */       location.sourceEnd);
/*      */   }
/*      */   public void unhandledWarningToken(Expression token) {
/* 6637 */     String[] arguments = { token.constant.stringValue() };
/* 6638 */     handle(
/* 6639 */       536871543, 
/* 6640 */       arguments, 
/* 6641 */       arguments, 
/* 6642 */       token.sourceStart, 
/* 6643 */       token.sourceEnd);
/*      */   }
/*      */   public void uninitializedBlankFinalField(FieldBinding field, ASTNode location) {
/* 6646 */     String[] arguments = { new String(field.readableName()) };
/* 6647 */     handle(
/* 6648 */       33554513, 
/* 6649 */       arguments, 
/* 6650 */       arguments, 
/* 6651 */       nodeSourceStart(field, location), 
/* 6652 */       nodeSourceEnd(field, location));
/*      */   }
/*      */   public void uninitializedLocalVariable(LocalVariableBinding binding, ASTNode location) {
/* 6655 */     String[] arguments = { new String(binding.readableName()) };
/* 6656 */     handle(
/* 6657 */       536870963, 
/* 6658 */       arguments, 
/* 6659 */       arguments, 
/* 6660 */       nodeSourceStart(binding, location), 
/* 6661 */       nodeSourceEnd(binding, location));
/*      */   }
/*      */   public void unmatchedBracket(int position, ReferenceContext context, CompilationResult compilationResult) {
/* 6664 */     handle(
/* 6665 */       1610612956, 
/* 6666 */       NoArgument, 
/* 6667 */       NoArgument, 
/* 6668 */       position, 
/* 6669 */       position, 
/* 6670 */       context, 
/* 6671 */       compilationResult);
/*      */   }
/*      */   public void unnecessaryCast(CastExpression castExpression) {
/* 6674 */     int severity = computeSeverity(553648309);
/* 6675 */     if (severity == -1) return;
/* 6676 */     TypeBinding castedExpressionType = castExpression.expression.resolvedType;
/* 6677 */     handle(
/* 6678 */       553648309, 
/* 6679 */       new String[] { new String(castedExpressionType.readableName()), new String(castExpression.type.resolvedType.readableName()) }, 
/* 6680 */       new String[] { new String(castedExpressionType.shortReadableName()), new String(castExpression.type.resolvedType.shortReadableName()) }, 
/* 6681 */       severity, 
/* 6682 */       castExpression.sourceStart, 
/* 6683 */       castExpression.sourceEnd);
/*      */   }
/*      */   public void unnecessaryElse(ASTNode location) {
/* 6686 */     handle(
/* 6687 */       536871101, 
/* 6688 */       NoArgument, 
/* 6689 */       NoArgument, 
/* 6690 */       location.sourceStart, 
/* 6691 */       location.sourceEnd);
/*      */   }
/*      */   public void unnecessaryEnclosingInstanceSpecification(Expression expression, ReferenceBinding targetType) {
/* 6694 */     handle(
/* 6695 */       16777239, 
/* 6696 */       new String[] { new String(targetType.readableName()) }, 
/* 6697 */       new String[] { new String(targetType.shortReadableName()) }, 
/* 6698 */       expression.sourceStart, 
/* 6699 */       expression.sourceEnd);
/*      */   }
/*      */   public void unnecessaryInstanceof(InstanceOfExpression instanceofExpression, TypeBinding checkType) {
/* 6702 */     int severity = computeSeverity(553648311);
/* 6703 */     if (severity == -1) return;
/* 6704 */     TypeBinding expressionType = instanceofExpression.expression.resolvedType;
/* 6705 */     handle(
/* 6706 */       553648311, 
/* 6707 */       new String[] { new String(expressionType.readableName()), new String(checkType.readableName()) }, 
/* 6708 */       new String[] { new String(expressionType.shortReadableName()), new String(checkType.shortReadableName()) }, 
/* 6709 */       severity, 
/* 6710 */       instanceofExpression.sourceStart, 
/* 6711 */       instanceofExpression.sourceEnd);
/*      */   }
/*      */   public void unnecessaryNLSTags(int sourceStart, int sourceEnd) {
/* 6714 */     handle(
/* 6715 */       536871177, 
/* 6716 */       NoArgument, 
/* 6717 */       NoArgument, 
/* 6718 */       sourceStart, 
/* 6719 */       sourceEnd);
/*      */   }
/*      */   public void unnecessaryTypeArgumentsForMethodInvocation(MethodBinding method, TypeBinding[] genericTypeArguments, TypeReference[] typeArguments) {
/* 6722 */     String methodName = method.isConstructor() ? 
/* 6723 */       new String(method.declaringClass.shortReadableName()) : 
/* 6724 */       new String(method.selector);
/* 6725 */     handle(
/* 6726 */       method.isConstructor() ? 
/* 6727 */       67109524 : 
/* 6728 */       67109443, 
/* 6729 */       new String[] { 
/* 6730 */       methodName, 
/* 6731 */       typesAsString(method.isVarargs(), method.parameters, false), 
/* 6732 */       new String(method.declaringClass.readableName()), 
/* 6733 */       typesAsString(false, genericTypeArguments, false) }, 
/* 6734 */       new String[] { 
/* 6735 */       methodName, 
/* 6736 */       typesAsString(method.isVarargs(), method.parameters, true), 
/* 6737 */       new String(method.declaringClass.shortReadableName()), 
/* 6738 */       typesAsString(false, genericTypeArguments, true) }, 
/* 6739 */       typeArguments[0].sourceStart, 
/* 6740 */       typeArguments[(typeArguments.length - 1)].sourceEnd);
/*      */   }
/*      */   public void unqualifiedFieldAccess(NameReference reference, FieldBinding field) {
/* 6743 */     int sourceStart = reference.sourceStart;
/* 6744 */     int sourceEnd = reference.sourceEnd;
/* 6745 */     if ((reference instanceof SingleNameReference)) {
/* 6746 */       int numberOfParens = (reference.bits & 0x1FE00000) >> 21;
/* 6747 */       if (numberOfParens != 0) {
/* 6748 */         sourceStart = retrieveStartingPositionAfterOpeningParenthesis(sourceStart, sourceEnd, numberOfParens);
/* 6749 */         sourceEnd = retrieveEndingPositionAfterOpeningParenthesis(sourceStart, sourceEnd, numberOfParens);
/*      */       } else {
/* 6751 */         sourceStart = nodeSourceStart(field, reference);
/* 6752 */         sourceEnd = nodeSourceEnd(field, reference);
/*      */       }
/*      */     } else {
/* 6755 */       sourceStart = nodeSourceStart(field, reference);
/* 6756 */       sourceEnd = nodeSourceEnd(field, reference);
/*      */     }
/* 6758 */     handle(
/* 6759 */       570425423, 
/* 6760 */       new String[] { new String(field.declaringClass.readableName()), new String(field.name) }, 
/* 6761 */       new String[] { new String(field.declaringClass.shortReadableName()), new String(field.name) }, 
/* 6762 */       sourceStart, 
/* 6763 */       sourceEnd);
/*      */   }
/*      */   public void unreachableCatchBlock(ReferenceBinding exceptionType, ASTNode location) {
/* 6766 */     handle(
/* 6767 */       83886247, 
/* 6768 */       new String[] { 
/* 6769 */       new String(exceptionType.readableName()) }, 
/* 6771 */       new String[] { 
/* 6772 */       new String(exceptionType.shortReadableName()) }, 
/* 6774 */       location.sourceStart, 
/* 6775 */       location.sourceEnd);
/*      */   }
/*      */   public void unreachableCode(Statement statement) {
/* 6778 */     int sourceStart = statement.sourceStart;
/* 6779 */     int sourceEnd = statement.sourceEnd;
/* 6780 */     if ((statement instanceof LocalDeclaration)) {
/* 6781 */       LocalDeclaration declaration = (LocalDeclaration)statement;
/* 6782 */       sourceStart = declaration.declarationSourceStart;
/* 6783 */       sourceEnd = declaration.declarationSourceEnd;
/* 6784 */     } else if ((statement instanceof Expression)) {
/* 6785 */       int statemendEnd = ((Expression)statement).statementEnd;
/* 6786 */       if (statemendEnd != -1) sourceEnd = statemendEnd;
/*      */     }
/* 6788 */     handle(
/* 6789 */       536871073, 
/* 6790 */       NoArgument, 
/* 6791 */       NoArgument, 
/* 6792 */       sourceStart, 
/* 6793 */       sourceEnd);
/*      */   }
/*      */ 
/*      */   public void unresolvableReference(NameReference nameRef, Binding binding)
/*      */   {
/* 6803 */     String[] arguments = { new String(binding.readableName()) };
/* 6804 */     int end = nameRef.sourceEnd;
/* 6805 */     if ((nameRef instanceof QualifiedNameReference)) {
/* 6806 */       QualifiedNameReference ref = (QualifiedNameReference)nameRef;
/* 6807 */       if (isRecoveredName(ref.tokens)) return;
/* 6808 */       if (ref.indexOfFirstFieldBinding >= 1)
/* 6809 */         end = (int)ref.sourcePositions[(ref.indexOfFirstFieldBinding - 1)];
/*      */     } else {
/* 6811 */       SingleNameReference ref = (SingleNameReference)nameRef;
/* 6812 */       if (isRecoveredName(ref.token)) return;
/*      */     }
/* 6814 */     handle(
/* 6815 */       570425394, 
/* 6816 */       arguments, 
/* 6817 */       arguments, 
/* 6818 */       nameRef.sourceStart, 
/* 6819 */       end);
/*      */   }
/*      */   public void unsafeCast(CastExpression castExpression, Scope scope) {
/* 6822 */     int severity = computeSeverity(16777761);
/* 6823 */     if (severity == -1) return;
/* 6824 */     TypeBinding castedExpressionType = castExpression.expression.resolvedType;
/* 6825 */     TypeBinding castExpressionResolvedType = castExpression.resolvedType;
/* 6826 */     handle(
/* 6827 */       16777761, 
/* 6828 */       new String[] { 
/* 6829 */       new String(castedExpressionType.readableName()), 
/* 6830 */       new String(castExpressionResolvedType.readableName()) }, 
/* 6832 */       new String[] { 
/* 6833 */       new String(castedExpressionType.shortReadableName()), 
/* 6834 */       new String(castExpressionResolvedType.shortReadableName()) }, 
/* 6836 */       severity, 
/* 6837 */       castExpression.sourceStart, 
/* 6838 */       castExpression.sourceEnd);
/*      */   }
/*      */   public void unsafeGenericArrayForVarargs(TypeBinding leafComponentType, ASTNode location) {
/* 6841 */     int severity = computeSeverity(67109438);
/* 6842 */     if (severity == -1) return;
/* 6843 */     handle(
/* 6844 */       67109438, 
/* 6845 */       new String[] { new String(leafComponentType.readableName()) }, 
/* 6846 */       new String[] { new String(leafComponentType.shortReadableName()) }, 
/* 6847 */       severity, 
/* 6848 */       location.sourceStart, 
/* 6849 */       location.sourceEnd);
/*      */   }
/*      */   public void unsafeRawFieldAssignment(FieldBinding field, TypeBinding expressionType, ASTNode location) {
/* 6852 */     int severity = computeSeverity(16777752);
/* 6853 */     if (severity == -1) return;
/* 6854 */     handle(
/* 6855 */       16777752, 
/* 6856 */       new String[] { 
/* 6857 */       new String(expressionType.readableName()), new String(field.name), new String(field.declaringClass.readableName()), new String(field.declaringClass.erasure().readableName()) }, 
/* 6858 */       new String[] { 
/* 6859 */       new String(expressionType.shortReadableName()), new String(field.name), new String(field.declaringClass.shortReadableName()), new String(field.declaringClass.erasure().shortReadableName()) }, 
/* 6860 */       severity, 
/* 6861 */       nodeSourceStart(field, location), 
/* 6862 */       nodeSourceEnd(field, location));
/*      */   }
/*      */   public void unsafeRawGenericMethodInvocation(ASTNode location, MethodBinding rawMethod, TypeBinding[] argumentTypes) {
/* 6865 */     boolean isConstructor = rawMethod.isConstructor();
/* 6866 */     int severity = computeSeverity(isConstructor ? 16777785 : 16777786);
/* 6867 */     if (severity == -1) return;
/* 6868 */     if (isConstructor)
/* 6869 */       handle(
/* 6870 */         16777785, 
/* 6871 */         new String[] { 
/* 6872 */         new String(rawMethod.declaringClass.sourceName()), 
/* 6873 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.original().parameters, false), 
/* 6874 */         new String(rawMethod.declaringClass.readableName()), 
/* 6875 */         typesAsString(false, argumentTypes, false) }, 
/* 6877 */         new String[] { 
/* 6878 */         new String(rawMethod.declaringClass.sourceName()), 
/* 6879 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.original().parameters, true), 
/* 6880 */         new String(rawMethod.declaringClass.shortReadableName()), 
/* 6881 */         typesAsString(false, argumentTypes, true) }, 
/* 6883 */         severity, 
/* 6884 */         location.sourceStart, 
/* 6885 */         location.sourceEnd);
/*      */     else
/* 6887 */       handle(
/* 6888 */         16777786, 
/* 6889 */         new String[] { 
/* 6890 */         new String(rawMethod.selector), 
/* 6891 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.original().parameters, false), 
/* 6892 */         new String(rawMethod.declaringClass.readableName()), 
/* 6893 */         typesAsString(false, argumentTypes, false) }, 
/* 6895 */         new String[] { 
/* 6896 */         new String(rawMethod.selector), 
/* 6897 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.original().parameters, true), 
/* 6898 */         new String(rawMethod.declaringClass.shortReadableName()), 
/* 6899 */         typesAsString(false, argumentTypes, true) }, 
/* 6901 */         severity, 
/* 6902 */         location.sourceStart, 
/* 6903 */         location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void unsafeRawInvocation(ASTNode location, MethodBinding rawMethod) {
/* 6907 */     boolean isConstructor = rawMethod.isConstructor();
/* 6908 */     int severity = computeSeverity(isConstructor ? 16777746 : 16777747);
/* 6909 */     if (severity == -1) return;
/* 6910 */     if (isConstructor)
/* 6911 */       handle(
/* 6912 */         16777746, 
/* 6913 */         new String[] { 
/* 6914 */         new String(rawMethod.declaringClass.readableName()), 
/* 6915 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.parameters, false), 
/* 6916 */         new String(rawMethod.declaringClass.erasure().readableName()) }, 
/* 6918 */         new String[] { 
/* 6919 */         new String(rawMethod.declaringClass.shortReadableName()), 
/* 6920 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.parameters, true), 
/* 6921 */         new String(rawMethod.declaringClass.erasure().shortReadableName()) }, 
/* 6923 */         severity, 
/* 6924 */         location.sourceStart, 
/* 6925 */         location.sourceEnd);
/*      */     else
/* 6927 */       handle(
/* 6928 */         16777747, 
/* 6929 */         new String[] { 
/* 6930 */         new String(rawMethod.selector), 
/* 6931 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.parameters, false), 
/* 6932 */         new String(rawMethod.declaringClass.readableName()), 
/* 6933 */         new String(rawMethod.declaringClass.erasure().readableName()) }, 
/* 6935 */         new String[] { 
/* 6936 */         new String(rawMethod.selector), 
/* 6937 */         typesAsString(rawMethod.original().isVarargs(), rawMethod.parameters, true), 
/* 6938 */         new String(rawMethod.declaringClass.shortReadableName()), 
/* 6939 */         new String(rawMethod.declaringClass.erasure().shortReadableName()) }, 
/* 6941 */         severity, 
/* 6942 */         location.sourceStart, 
/* 6943 */         location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void unsafeReturnTypeOverride(MethodBinding currentMethod, MethodBinding inheritedMethod, SourceTypeBinding type) {
/* 6947 */     int severity = computeSeverity(67109423);
/* 6948 */     if (severity == -1) return;
/* 6949 */     int start = type.sourceStart();
/* 6950 */     int end = type.sourceEnd();
/* 6951 */     if (currentMethod.declaringClass == type) {
/* 6952 */       ASTNode location = ((MethodDeclaration)currentMethod.sourceMethod()).returnType;
/* 6953 */       start = location.sourceStart();
/* 6954 */       end = location.sourceEnd();
/*      */     }
/* 6956 */     handle(
/* 6957 */       67109423, 
/* 6958 */       new String[] { 
/* 6959 */       new String(currentMethod.returnType.readableName()), 
/* 6960 */       new String(currentMethod.selector), 
/* 6961 */       typesAsString(currentMethod.original().isVarargs(), currentMethod.original().parameters, false), 
/* 6962 */       new String(currentMethod.declaringClass.readableName()), 
/* 6963 */       new String(inheritedMethod.returnType.readableName()), 
/* 6964 */       new String(inheritedMethod.declaringClass.readableName()) }, 
/* 6967 */       new String[] { 
/* 6968 */       new String(currentMethod.returnType.shortReadableName()), 
/* 6969 */       new String(currentMethod.selector), 
/* 6970 */       typesAsString(currentMethod.original().isVarargs(), currentMethod.original().parameters, true), 
/* 6971 */       new String(currentMethod.declaringClass.shortReadableName()), 
/* 6972 */       new String(inheritedMethod.returnType.shortReadableName()), 
/* 6973 */       new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 6976 */       severity, 
/* 6977 */       start, 
/* 6978 */       end);
/*      */   }
/*      */   public void unsafeTypeConversion(Expression expression, TypeBinding expressionType, TypeBinding expectedType) {
/* 6981 */     int severity = computeSeverity(16777748);
/* 6982 */     if (severity == -1) return;
/* 6983 */     handle(
/* 6984 */       16777748, 
/* 6985 */       new String[] { new String(expressionType.readableName()), new String(expectedType.readableName()), new String(expectedType.erasure().readableName()) }, 
/* 6986 */       new String[] { new String(expressionType.shortReadableName()), new String(expectedType.shortReadableName()), new String(expectedType.erasure().shortReadableName()) }, 
/* 6987 */       severity, 
/* 6988 */       expression.sourceStart, 
/* 6989 */       expression.sourceEnd);
/*      */   }
/*      */   public void unusedArgument(LocalDeclaration localDecl) {
/* 6992 */     int severity = computeSeverity(536870974);
/* 6993 */     if (severity == -1) return;
/* 6994 */     String[] arguments = { new String(localDecl.name) };
/* 6995 */     handle(
/* 6996 */       536870974, 
/* 6997 */       arguments, 
/* 6998 */       arguments, 
/* 6999 */       severity, 
/* 7000 */       localDecl.sourceStart, 
/* 7001 */       localDecl.sourceEnd);
/*      */   }
/*      */   public void unusedDeclaredThrownException(ReferenceBinding exceptionType, AbstractMethodDeclaration method, ASTNode location) {
/* 7004 */     boolean isConstructor = method.isConstructor();
/* 7005 */     int severity = computeSeverity(isConstructor ? 536871098 : 536871097);
/* 7006 */     if (severity == -1) return;
/* 7007 */     if (isConstructor)
/* 7008 */       handle(
/* 7009 */         536871098, 
/* 7010 */         new String[] { 
/* 7011 */         new String(method.binding.declaringClass.readableName()), 
/* 7012 */         typesAsString(method.binding.isVarargs(), method.binding.parameters, false), 
/* 7013 */         new String(exceptionType.readableName()) }, 
/* 7015 */         new String[] { 
/* 7016 */         new String(method.binding.declaringClass.shortReadableName()), 
/* 7017 */         typesAsString(method.binding.isVarargs(), method.binding.parameters, true), 
/* 7018 */         new String(exceptionType.shortReadableName()) }, 
/* 7020 */         severity, 
/* 7021 */         location.sourceStart, 
/* 7022 */         location.sourceEnd);
/*      */     else
/* 7024 */       handle(
/* 7025 */         536871097, 
/* 7026 */         new String[] { 
/* 7027 */         new String(method.binding.declaringClass.readableName()), 
/* 7028 */         new String(method.selector), 
/* 7029 */         typesAsString(method.binding.isVarargs(), method.binding.parameters, false), 
/* 7030 */         new String(exceptionType.readableName()) }, 
/* 7032 */         new String[] { 
/* 7033 */         new String(method.binding.declaringClass.shortReadableName()), 
/* 7034 */         new String(method.selector), 
/* 7035 */         typesAsString(method.binding.isVarargs(), method.binding.parameters, true), 
/* 7036 */         new String(exceptionType.shortReadableName()) }, 
/* 7038 */         severity, 
/* 7039 */         location.sourceStart, 
/* 7040 */         location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void unusedImport(ImportReference importRef) {
/* 7044 */     int severity = computeSeverity(268435844);
/* 7045 */     if (severity == -1) return;
/* 7046 */     String[] arguments = { CharOperation.toString(importRef.tokens) };
/* 7047 */     handle(
/* 7048 */       268435844, 
/* 7049 */       arguments, 
/* 7050 */       arguments, 
/* 7051 */       severity, 
/* 7052 */       importRef.sourceStart, 
/* 7053 */       importRef.sourceEnd);
/*      */   }
/*      */   public void unusedLabel(LabeledStatement statement) {
/* 7056 */     int severity = computeSeverity(536871111);
/* 7057 */     if (severity == -1) return;
/* 7058 */     String[] arguments = { new String(statement.label) };
/* 7059 */     handle(
/* 7060 */       536871111, 
/* 7061 */       arguments, 
/* 7062 */       arguments, 
/* 7063 */       severity, 
/* 7064 */       statement.sourceStart, 
/* 7065 */       statement.labelEnd);
/*      */   }
/*      */   public void unusedLocalVariable(LocalDeclaration localDecl) {
/* 7068 */     int severity = computeSeverity(536870973);
/* 7069 */     if (severity == -1) return;
/* 7070 */     String[] arguments = { new String(localDecl.name) };
/* 7071 */     handle(
/* 7072 */       536870973, 
/* 7073 */       arguments, 
/* 7074 */       arguments, 
/* 7075 */       severity, 
/* 7076 */       localDecl.sourceStart, 
/* 7077 */       localDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void unusedPrivateConstructor(ConstructorDeclaration constructorDecl) {
/* 7081 */     int severity = computeSeverity(603979910);
/* 7082 */     if (severity == -1) return;
/*      */ 
/* 7084 */     MethodBinding constructor = constructorDecl.binding;
/* 7085 */     handle(
/* 7086 */       603979910, 
/* 7087 */       new String[] { 
/* 7088 */       new String(constructor.declaringClass.readableName()), 
/* 7089 */       typesAsString(constructor.isVarargs(), constructor.parameters, false) }, 
/* 7091 */       new String[] { 
/* 7092 */       new String(constructor.declaringClass.shortReadableName()), 
/* 7093 */       typesAsString(constructor.isVarargs(), constructor.parameters, true) }, 
/* 7095 */       severity, 
/* 7096 */       constructorDecl.sourceStart, 
/* 7097 */       constructorDecl.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void unusedPrivateField(FieldDeclaration fieldDecl) {
/* 7101 */     int severity = computeSeverity(570425421);
/* 7102 */     if (severity == -1) return;
/*      */ 
/* 7104 */     FieldBinding field = fieldDecl.binding;
/*      */ 
/* 7106 */     if ((CharOperation.equals(TypeConstants.SERIALVERSIONUID, field.name)) && 
/* 7107 */       (field.isStatic()) && 
/* 7108 */       (field.isFinal()) && 
/* 7109 */       (TypeBinding.LONG == field.type)) {
/* 7110 */       return;
/*      */     }
/* 7112 */     if ((CharOperation.equals(TypeConstants.SERIALPERSISTENTFIELDS, field.name)) && 
/* 7113 */       (field.isStatic()) && 
/* 7114 */       (field.isFinal()) && 
/* 7115 */       (field.type.dimensions() == 1) && 
/* 7116 */       (CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTSTREAMFIELD, field.type.leafComponentType().readableName()))) {
/* 7117 */       return;
/*      */     }
/* 7119 */     handle(
/* 7120 */       570425421, 
/* 7121 */       new String[] { 
/* 7122 */       new String(field.declaringClass.readableName()), 
/* 7123 */       new String(field.name) }, 
/* 7125 */       new String[] { 
/* 7126 */       new String(field.declaringClass.shortReadableName()), 
/* 7127 */       new String(field.name) }, 
/* 7129 */       severity, 
/* 7130 */       nodeSourceStart(field, fieldDecl), 
/* 7131 */       nodeSourceEnd(field, fieldDecl));
/*      */   }
/*      */ 
/*      */   public void unusedPrivateMethod(AbstractMethodDeclaration methodDecl) {
/* 7135 */     int severity = computeSeverity(603979894);
/* 7136 */     if (severity == -1) return;
/*      */ 
/* 7138 */     MethodBinding method = methodDecl.binding;
/*      */ 
/* 7141 */     if ((!method.isStatic()) && 
/* 7142 */       (TypeBinding.VOID == method.returnType) && 
/* 7143 */       (method.parameters.length == 1) && 
/* 7144 */       (method.parameters[0].dimensions() == 0) && 
/* 7145 */       (CharOperation.equals(method.selector, TypeConstants.READOBJECT)) && 
/* 7146 */       (CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTINPUTSTREAM, method.parameters[0].readableName()))) {
/* 7147 */       return;
/*      */     }
/*      */ 
/* 7150 */     if ((!method.isStatic()) && 
/* 7151 */       (TypeBinding.VOID == method.returnType) && 
/* 7152 */       (method.parameters.length == 1) && 
/* 7153 */       (method.parameters[0].dimensions() == 0) && 
/* 7154 */       (CharOperation.equals(method.selector, TypeConstants.WRITEOBJECT)) && 
/* 7155 */       (CharOperation.equals(TypeConstants.CharArray_JAVA_IO_OBJECTOUTPUTSTREAM, method.parameters[0].readableName()))) {
/* 7156 */       return;
/*      */     }
/*      */ 
/* 7159 */     if ((!method.isStatic()) && 
/* 7160 */       (1 == method.returnType.id) && 
/* 7161 */       (method.parameters.length == 0) && 
/* 7162 */       (CharOperation.equals(method.selector, TypeConstants.READRESOLVE))) {
/* 7163 */       return;
/*      */     }
/*      */ 
/* 7166 */     if ((!method.isStatic()) && 
/* 7167 */       (1 == method.returnType.id) && 
/* 7168 */       (method.parameters.length == 0) && 
/* 7169 */       (CharOperation.equals(method.selector, TypeConstants.WRITEREPLACE))) {
/* 7170 */       return;
/*      */     }
/* 7172 */     handle(
/* 7173 */       603979894, 
/* 7174 */       new String[] { 
/* 7175 */       new String(method.declaringClass.readableName()), 
/* 7176 */       new String(method.selector), 
/* 7177 */       typesAsString(method.isVarargs(), method.parameters, false) }, 
/* 7179 */       new String[] { 
/* 7180 */       new String(method.declaringClass.shortReadableName()), 
/* 7181 */       new String(method.selector), 
/* 7182 */       typesAsString(method.isVarargs(), method.parameters, true) }, 
/* 7184 */       severity, 
/* 7185 */       methodDecl.sourceStart, 
/* 7186 */       methodDecl.sourceEnd);
/*      */   }
/*      */   public void unusedPrivateType(TypeDeclaration typeDecl) {
/* 7189 */     int severity = computeSeverity(553648135);
/* 7190 */     if (severity == -1) return;
/*      */ 
/* 7192 */     ReferenceBinding type = typeDecl.binding;
/* 7193 */     handle(
/* 7194 */       553648135, 
/* 7195 */       new String[] { 
/* 7196 */       new String(type.readableName()) }, 
/* 7198 */       new String[] { 
/* 7199 */       new String(type.shortReadableName()) }, 
/* 7201 */       severity, 
/* 7202 */       typeDecl.sourceStart, 
/* 7203 */       typeDecl.sourceEnd);
/*      */   }
/*      */   public void unusedWarningToken(Expression token) {
/* 7206 */     String[] arguments = { token.constant.stringValue() };
/* 7207 */     handle(
/* 7208 */       536871547, 
/* 7209 */       arguments, 
/* 7210 */       arguments, 
/* 7211 */       token.sourceStart, 
/* 7212 */       token.sourceEnd);
/*      */   }
/*      */   public void useAssertAsAnIdentifier(int sourceStart, int sourceEnd) {
/* 7215 */     handle(
/* 7216 */       536871352, 
/* 7217 */       NoArgument, 
/* 7218 */       NoArgument, 
/* 7219 */       sourceStart, 
/* 7220 */       sourceEnd);
/*      */   }
/*      */   public void useEnumAsAnIdentifier(int sourceStart, int sourceEnd) {
/* 7223 */     handle(
/* 7224 */       536871353, 
/* 7225 */       NoArgument, 
/* 7226 */       NoArgument, 
/* 7227 */       sourceStart, 
/* 7228 */       sourceEnd);
/*      */   }
/*      */   public void varargsArgumentNeedCast(MethodBinding method, TypeBinding argumentType, InvocationSite location) {
/* 7231 */     int severity = this.options.getSeverity(536870976);
/* 7232 */     if (severity == -1) return;
/* 7233 */     ArrayBinding varargsType = (ArrayBinding)method.parameters[(method.parameters.length - 1)];
/* 7234 */     if (method.isConstructor())
/* 7235 */       handle(
/* 7236 */         134218530, 
/* 7237 */         new String[] { 
/* 7238 */         new String(argumentType.readableName()), 
/* 7239 */         new String(varargsType.readableName()), 
/* 7240 */         new String(method.declaringClass.readableName()), 
/* 7241 */         typesAsString(method.isVarargs(), method.parameters, false), 
/* 7242 */         new String(varargsType.elementsType().readableName()) }, 
/* 7244 */         new String[] { 
/* 7245 */         new String(argumentType.shortReadableName()), 
/* 7246 */         new String(varargsType.shortReadableName()), 
/* 7247 */         new String(method.declaringClass.shortReadableName()), 
/* 7248 */         typesAsString(method.isVarargs(), method.parameters, true), 
/* 7249 */         new String(varargsType.elementsType().shortReadableName()) }, 
/* 7251 */         severity, 
/* 7252 */         location.sourceStart(), 
/* 7253 */         location.sourceEnd());
/*      */     else
/* 7255 */       handle(
/* 7256 */         67109665, 
/* 7257 */         new String[] { 
/* 7258 */         new String(argumentType.readableName()), 
/* 7259 */         new String(varargsType.readableName()), 
/* 7260 */         new String(method.selector), 
/* 7261 */         typesAsString(method.isVarargs(), method.parameters, false), 
/* 7262 */         new String(method.declaringClass.readableName()), 
/* 7263 */         new String(varargsType.elementsType().readableName()) }, 
/* 7265 */         new String[] { 
/* 7266 */         new String(argumentType.shortReadableName()), 
/* 7267 */         new String(varargsType.shortReadableName()), 
/* 7268 */         new String(method.selector), typesAsString(method.isVarargs(), method.parameters, true), 
/* 7269 */         new String(method.declaringClass.shortReadableName()), 
/* 7270 */         new String(varargsType.elementsType().shortReadableName()) }, 
/* 7272 */         severity, 
/* 7273 */         location.sourceStart(), 
/* 7274 */         location.sourceEnd());
/*      */   }
/*      */ 
/*      */   public void varargsConflict(MethodBinding method1, MethodBinding method2, SourceTypeBinding type) {
/* 7278 */     handle(
/* 7279 */       67109667, 
/* 7280 */       new String[] { 
/* 7281 */       new String(method1.selector), 
/* 7282 */       typesAsString(method1.isVarargs(), method1.parameters, false), 
/* 7283 */       new String(method1.declaringClass.readableName()), 
/* 7284 */       typesAsString(method2.isVarargs(), method2.parameters, false), 
/* 7285 */       new String(method2.declaringClass.readableName()) }, 
/* 7287 */       new String[] { 
/* 7288 */       new String(method1.selector), 
/* 7289 */       typesAsString(method1.isVarargs(), method1.parameters, true), 
/* 7290 */       new String(method1.declaringClass.shortReadableName()), 
/* 7291 */       typesAsString(method2.isVarargs(), method2.parameters, true), 
/* 7292 */       new String(method2.declaringClass.shortReadableName()) }, 
/* 7294 */       method1.declaringClass == type ? method1.sourceStart() : type.sourceStart(), 
/* 7295 */       method1.declaringClass == type ? method1.sourceEnd() : type.sourceEnd());
/*      */   }
/*      */   public void variableTypeCannotBeVoid(AbstractVariableDeclaration varDecl) {
/* 7298 */     String[] arguments = { new String(varDecl.name) };
/* 7299 */     handle(
/* 7300 */       536870964, 
/* 7301 */       arguments, 
/* 7302 */       arguments, 
/* 7303 */       varDecl.sourceStart, 
/* 7304 */       varDecl.sourceEnd);
/*      */   }
/*      */   public void variableTypeCannotBeVoidArray(AbstractVariableDeclaration varDecl) {
/* 7307 */     handle(
/* 7308 */       536870966, 
/* 7309 */       NoArgument, 
/* 7310 */       NoArgument, 
/* 7311 */       varDecl.type.sourceStart, 
/* 7312 */       varDecl.type.sourceEnd);
/*      */   }
/*      */   public void visibilityConflict(MethodBinding currentMethod, MethodBinding inheritedMethod) {
/* 7315 */     handle(
/* 7319 */       67109273, 
/* 7320 */       new String[] { new String(inheritedMethod.declaringClass.readableName()) }, 
/* 7321 */       new String[] { new String(inheritedMethod.declaringClass.shortReadableName()) }, 
/* 7322 */       currentMethod.sourceStart(), 
/* 7323 */       currentMethod.sourceEnd());
/*      */   }
/*      */   public void wildcardAssignment(TypeBinding variableType, TypeBinding expressionType, ASTNode location) {
/* 7326 */     handle(
/* 7327 */       16777758, 
/* 7328 */       new String[] { 
/* 7329 */       new String(expressionType.readableName()), new String(variableType.readableName()) }, 
/* 7330 */       new String[] { 
/* 7331 */       new String(expressionType.shortReadableName()), new String(variableType.shortReadableName()) }, 
/* 7332 */       location.sourceStart, 
/* 7333 */       location.sourceEnd);
/*      */   }
/*      */   public void wildcardInvocation(ASTNode location, TypeBinding receiverType, MethodBinding method, TypeBinding[] arguments) {
/* 7336 */     TypeBinding offendingArgument = null;
/* 7337 */     TypeBinding offendingParameter = null;
/* 7338 */     int i = 0; for (int length = method.parameters.length; i < length; i++) {
/* 7339 */       TypeBinding parameter = method.parameters[i];
/* 7340 */       if ((parameter.isWildcard()) && (((WildcardBinding)parameter).boundKind != 2)) {
/* 7341 */         offendingParameter = parameter;
/* 7342 */         offendingArgument = arguments[i];
/* 7343 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 7347 */     if (method.isConstructor())
/* 7348 */       handle(
/* 7349 */         16777756, 
/* 7350 */         new String[] { 
/* 7351 */         new String(receiverType.sourceName()), 
/* 7352 */         typesAsString(method.isVarargs(), method.parameters, false), 
/* 7353 */         new String(receiverType.readableName()), 
/* 7354 */         typesAsString(false, arguments, false), 
/* 7355 */         new String(offendingArgument.readableName()), 
/* 7356 */         new String(offendingParameter.readableName()) }, 
/* 7358 */         new String[] { 
/* 7359 */         new String(receiverType.sourceName()), 
/* 7360 */         typesAsString(method.isVarargs(), method.parameters, true), 
/* 7361 */         new String(receiverType.shortReadableName()), 
/* 7362 */         typesAsString(false, arguments, true), 
/* 7363 */         new String(offendingArgument.shortReadableName()), 
/* 7364 */         new String(offendingParameter.shortReadableName()) }, 
/* 7366 */         location.sourceStart, 
/* 7367 */         location.sourceEnd);
/*      */     else
/* 7369 */       handle(
/* 7370 */         16777757, 
/* 7371 */         new String[] { 
/* 7372 */         new String(method.selector), 
/* 7373 */         typesAsString(method.isVarargs(), method.parameters, false), 
/* 7374 */         new String(receiverType.readableName()), 
/* 7375 */         typesAsString(false, arguments, false), 
/* 7376 */         new String(offendingArgument.readableName()), 
/* 7377 */         new String(offendingParameter.readableName()) }, 
/* 7379 */         new String[] { 
/* 7380 */         new String(method.selector), 
/* 7381 */         typesAsString(method.isVarargs(), method.parameters, true), 
/* 7382 */         new String(receiverType.shortReadableName()), 
/* 7383 */         typesAsString(false, arguments, true), 
/* 7384 */         new String(offendingArgument.shortReadableName()), 
/* 7385 */         new String(offendingParameter.shortReadableName()) }, 
/* 7387 */         location.sourceStart, 
/* 7388 */         location.sourceEnd);
/*      */   }
/*      */ 
/*      */   public void wrongSequenceOfExceptionTypesError(TryStatement statement, TypeBinding exceptionType, int under, TypeBinding hidingExceptionType)
/*      */   {
/* 7395 */     TypeReference typeRef = statement.catchArguments[under].type;
/* 7396 */     handle(
/* 7397 */       553648315, 
/* 7398 */       new String[] { 
/* 7399 */       new String(exceptionType.readableName()), 
/* 7400 */       new String(hidingExceptionType.readableName()) }, 
/* 7402 */       new String[] { 
/* 7403 */       new String(exceptionType.shortReadableName()), 
/* 7404 */       new String(hidingExceptionType.shortReadableName()) }, 
/* 7406 */       typeRef.sourceStart, 
/* 7407 */       typeRef.sourceEnd);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.problem.ProblemReporter
 * JD-Core Version:    0.6.0
 */