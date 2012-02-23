/*       */ package org.eclipse.jdt.internal.compiler.parser;
/*       */ 
/*       */ import java.io.BufferedInputStream;
/*       */ import java.io.BufferedWriter;
/*       */ import java.io.File;
/*       */ import java.io.FileOutputStream;
/*       */ import java.io.FileWriter;
/*       */ import java.io.IOException;
/*       */ import java.io.InputStream;
/*       */ import java.io.PrintStream;
/*       */ import java.util.ArrayList;
/*       */ import java.util.Collections;
/*       */ import java.util.Iterator;
/*       */ import java.util.List;
/*       */ import java.util.Locale;
/*       */ import java.util.MissingResourceException;
/*       */ import java.util.ResourceBundle;
/*       */ import java.util.StringTokenizer;
/*       */ import org.eclipse.jdt.core.compiler.CharOperation;
/*       */ import org.eclipse.jdt.core.compiler.InvalidInputException;
/*       */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*       */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*       */ import org.eclipse.jdt.internal.compiler.ReadManager;
/*       */ import org.eclipse.jdt.internal.compiler.ast.AND_AND_Expression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*       */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*       */ import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Assignment;
/*       */ import org.eclipse.jdt.internal.compiler.ast.BinaryExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*       */ import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.CastExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.CharLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
/*       */ import org.eclipse.jdt.internal.compiler.ast.CombinedBinaryExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ConditionalExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.DoStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.DoubleLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.EqualExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.FalseLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.FieldReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.FloatLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ForStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.IfStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*       */ import org.eclipse.jdt.internal.compiler.ast.InstanceOfExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.IntLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.IntLiteralMinValue;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Javadoc;
/*       */ import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.LongLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.LongLiteralMinValue;
/*       */ import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
/*       */ import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
/*       */ import org.eclipse.jdt.internal.compiler.ast.MessageSend;
/*       */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.NameReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
/*       */ import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.OR_OR_Expression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.OperatorIds;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.QualifiedSuperReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Reference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
/*       */ import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.SuperReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ThisReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.TrueLiteral;
/*       */ import org.eclipse.jdt.internal.compiler.ast.TryStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*       */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*       */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*       */ import org.eclipse.jdt.internal.compiler.ast.UnaryExpression;
/*       */ import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
/*       */ import org.eclipse.jdt.internal.compiler.ast.Wildcard;
/*       */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*       */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*       */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*       */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*       */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*       */ import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
/*       */ import org.eclipse.jdt.internal.compiler.parser.diagnose.DiagnoseParser;
/*       */ import org.eclipse.jdt.internal.compiler.parser.diagnose.RangeUtil;
/*       */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*       */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
/*       */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*       */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*       */ import org.eclipse.jdt.internal.compiler.util.Util;
/*       */ 
/*       */ public class Parser
/*       */   implements ParserBasicInformation, TerminalTokens, OperatorIds, TypeIds
/*       */ {
/*       */   protected static final int THIS_CALL = 3;
/*       */   protected static final int SUPER_CALL = 2;
/*    50 */   public static final char[] FALL_THROUGH_TAG = "$FALL-THROUGH$".toCharArray();
/*       */ 
/*    52 */   public static char[] asb = null;
/*    53 */   public static char[] asr = null;
/*       */   protected static final int AstStackIncrement = 100;
/*    56 */   public static char[] base_action = null;
/*       */   public static final int BracketKinds = 3;
/*    59 */   public static short[] check_table = null;
/*       */   public static final int CurlyBracket = 2;
/*       */   private static final boolean DEBUG = false;
/*       */   private static final boolean DEBUG_AUTOMATON = false;
/*       */   private static final String EOF_TOKEN = "$eof";
/*       */   private static final String ERROR_TOKEN = "$error";
/*       */   protected static final int ExpressionStackIncrement = 100;
/*       */   protected static final int GenericsStackIncrement = 10;
/*       */   private static final String FILEPREFIX = "parser";
/*    71 */   public static char[] in_symb = null;
/*       */   private static final String INVALID_CHARACTER = "Invalid Character";
/*    73 */   public static char[] lhs = null;
/*       */ 
/*    75 */   public static String[] name = null;
/*    76 */   public static char[] nasb = null;
/*    77 */   public static char[] nasr = null;
/*    78 */   public static char[] non_terminal_index = null;
/*       */   private static final String READABLE_NAMES_FILE = "readableNames";
/*       */   private static final String READABLE_NAMES_FILE_NAME = "org.eclipse.jdt.internal.compiler.parser.readableNames";
/*    82 */   public static String[] readableName = null;
/*       */ 
/*    84 */   public static byte[] rhs = null;
/*       */ 
/*    86 */   public static int[] reverse_index = null;
/*    87 */   public static char[] recovery_templates_index = null;
/*    88 */   public static char[] recovery_templates = null;
/*    89 */   public static char[] statements_recovery_filter = null;
/*       */ 
/*    91 */   public static long[] rules_compliance = null;
/*       */   public static final int RoundBracket = 0;
/*    95 */   public static byte[] scope_la = null;
/*    96 */   public static char[] scope_lhs = null;
/*       */ 
/*    98 */   public static char[] scope_prefix = null;
/*    99 */   public static char[] scope_rhs = null;
/*   100 */   public static char[] scope_state = null;
/*       */ 
/*   102 */   public static char[] scope_state_set = null;
/*   103 */   public static char[] scope_suffix = null;
/*       */   public static final int SquareBracket = 1;
/*       */   protected static final int StackIncrement = 255;
/*   109 */   public static char[] term_action = null;
/*   110 */   public static byte[] term_check = null;
/*       */ 
/*   112 */   public static char[] terminal_index = null;
/*       */   private static final String UNEXPECTED_EOF = "Unexpected End Of File";
/*   115 */   public static boolean VERBOSE_RECOVERY = false;
/*       */   protected int astLengthPtr;
/*       */   protected int[] astLengthStack;
/*       */   protected int astPtr;
/*   776 */   protected ASTNode[] astStack = new ASTNode[100];
/*       */   public CompilationUnitDeclaration compilationUnit;
/*       */   protected RecoveredElement currentElement;
/*       */   public int currentToken;
/*   781 */   protected boolean diet = false;
/*   782 */   protected int dietInt = 0;
/*       */   protected int endPosition;
/*       */   protected int endStatementPosition;
/*       */   protected int expressionLengthPtr;
/*       */   protected int[] expressionLengthStack;
/*       */   protected int expressionPtr;
/*   788 */   protected Expression[] expressionStack = new Expression[100];
/*       */   public int firstToken;
/*       */   protected int genericsIdentifiersLengthPtr;
/*   792 */   protected int[] genericsIdentifiersLengthStack = new int[10];
/*       */   protected int genericsLengthPtr;
/*   794 */   protected int[] genericsLengthStack = new int[10];
/*       */   protected int genericsPtr;
/*   796 */   protected ASTNode[] genericsStack = new ASTNode[10];
/*       */   protected boolean hasError;
/*       */   protected boolean hasReportedError;
/*       */   protected int identifierLengthPtr;
/*       */   protected int[] identifierLengthStack;
/*       */   protected long[] identifierPositionStack;
/*       */   protected int identifierPtr;
/*       */   protected char[][] identifierStack;
/*       */   protected boolean ignoreNextOpeningBrace;
/*       */   protected int intPtr;
/*       */   protected int[] intStack;
/*       */   public int lastAct;
/*       */   protected int lastCheckPoint;
/*       */   protected int lastErrorEndPosition;
/*   815 */   protected int lastErrorEndPositionBeforeRecovery = -1;
/*       */   protected int lastIgnoredToken;
/*       */   protected int nextIgnoredToken;
/*       */   protected int listLength;
/*       */   protected int listTypeParameterLength;
/*       */   protected int lParenPos;
/*       */   protected int rParenPos;
/*       */   protected int modifiers;
/*       */   protected int modifiersSourceStart;
/*       */   protected int[] nestedMethod;
/*       */   protected int nestedType;
/*       */   protected int dimensions;
/*   827 */   ASTNode[] noAstNodes = new ASTNode[100];
/*       */ 
/*   829 */   Expression[] noExpressions = new Expression[100];
/*       */ 
/*   831 */   protected boolean optimizeStringLiterals = true;
/*       */   protected CompilerOptions options;
/*       */   protected ProblemReporter problemReporter;
/*       */   protected int rBraceStart;
/*       */   protected int rBraceEnd;
/*       */   protected int rBraceSuccessorStart;
/*       */   protected int realBlockPtr;
/*       */   protected int[] realBlockStack;
/*       */   protected int recoveredStaticInitializerStart;
/*       */   public ReferenceContext referenceContext;
/*   841 */   public boolean reportOnlyOneSyntaxError = false;
/*   842 */   public boolean reportSyntaxErrorIsRequired = true;
/*       */   protected boolean restartRecovery;
/*   844 */   protected boolean annotationRecoveryActivated = true;
/*       */   protected int lastPosistion;
/*   847 */   public boolean methodRecoveryActivated = false;
/*   848 */   protected boolean statementRecoveryActivated = false;
/*       */   protected TypeDeclaration[] recoveredTypes;
/*       */   protected int recoveredTypePtr;
/*       */   protected int nextTypeStart;
/*       */   protected TypeDeclaration pendingRecoveredType;
/*       */   public RecoveryScanner recoveryScanner;
/*       */   public Scanner scanner;
/*   856 */   protected int[] stack = new int['ÿ'];
/*       */   protected int stateStackTop;
/*       */   protected int synchronizedBlockSourceStart;
/*       */   protected int[] variablesCounter;
/*       */   protected boolean checkExternalizeStrings;
/*       */   protected boolean recordStringLiterals;
/*       */   public Javadoc javadoc;
/*       */   public JavadocParser javadocParser;
/*       */   protected int lastJavadocEnd;
/*       */   public ReadManager readManager;
/*       */ 
/*       */   static
/*       */   {
/*       */     try
/*       */     {
/*   119 */       initTables();
/*       */     } catch (IOException ex) {
/*   121 */       throw new ExceptionInInitializerError(ex.getMessage());
/*       */     }
/*       */   }
/*       */ 
/*       */   public static int asi(int state) {
/*   126 */     return asb[original_state(state)];
/*       */   }
/*       */   public static final short base_check(int i) {
/*   129 */     return check_table[(i - 704)];
/*       */   }
/*       */   private static final void buildFile(String filename, List listToDump) {
/*   132 */     BufferedWriter writer = null;
/*       */     try {
/*   134 */       writer = new BufferedWriter(new FileWriter(filename));
/*   135 */       for (Iterator iterator = listToDump.iterator(); iterator.hasNext(); ) {
/*   136 */         writer.write(String.valueOf(iterator.next()));
/*       */       }
/*   138 */       writer.flush();
/*       */     }
/*       */     catch (IOException localIOException1)
/*       */     {
/*   142 */       if (writer != null)
/*       */         try {
/*   144 */           writer.close();
/*       */         }
/*       */         catch (IOException localIOException2)
/*       */         {
/*       */         }
/*       */     }
/*       */     finally
/*       */     {
/*   142 */       if (writer != null)
/*       */         try {
/*   144 */           writer.close();
/*       */         }
/*       */         catch (IOException localIOException3)
/*       */         {
/*       */         }
/*       */     }
/*   150 */     System.out.println(filename + " creation complete");
/*       */   }
/*       */ 
/*       */   private static void buildFileForCompliance(String file, int length, String[] tokens)
/*       */   {
/*   157 */     byte[] result = new byte[length * 8];
/*       */ 
/*   159 */     for (int i = 0; i < tokens.length; i += 3) {
/*   160 */       if ("2".equals(tokens[i])) {
/*   161 */         int index = Integer.parseInt(tokens[(i + 1)]);
/*   162 */         String token = tokens[(i + 2)].trim();
/*   163 */         long compliance = 0L;
/*   164 */         if ("1.4".equals(token))
/*   165 */           compliance = 3145728L;
/*   166 */         else if ("1.5".equals(token))
/*   167 */           compliance = 3211264L;
/*   168 */         else if ("recovery".equals(token)) {
/*   169 */           compliance = 9223372036854775807L;
/*       */         }
/*       */ 
/*   172 */         int j = index * 8;
/*   173 */         result[j] = (byte)(int)(compliance >>> 56);
/*   174 */         result[(j + 1)] = (byte)(int)(compliance >>> 48);
/*   175 */         result[(j + 2)] = (byte)(int)(compliance >>> 40);
/*   176 */         result[(j + 3)] = (byte)(int)(compliance >>> 32);
/*   177 */         result[(j + 4)] = (byte)(int)(compliance >>> 24);
/*   178 */         result[(j + 5)] = (byte)(int)(compliance >>> 16);
/*   179 */         result[(j + 6)] = (byte)(int)(compliance >>> 8);
/*   180 */         result[(j + 7)] = (byte)(int)compliance;
/*       */       }
/*       */     }
/*       */ 
/*   184 */     buildFileForTable(file, result);
/*       */   }
/*       */   private static final String[] buildFileForName(String filename, String contents) {
/*   187 */     String[] result = new String[contents.length()];
/*   188 */     result[0] = null;
/*   189 */     int resultCount = 1;
/*       */ 
/*   191 */     StringBuffer buffer = new StringBuffer();
/*       */ 
/*   193 */     int start = contents.indexOf("name[]");
/*   194 */     start = contents.indexOf('"', start);
/*   195 */     int end = contents.indexOf("};", start);
/*       */ 
/*   197 */     contents = contents.substring(start, end);
/*       */ 
/*   199 */     boolean addLineSeparator = false;
/*   200 */     int tokenStart = -1;
/*   201 */     StringBuffer currentToken = new StringBuffer();
/*   202 */     for (int i = 0; i < contents.length(); i++) {
/*   203 */       char c = contents.charAt(i);
/*   204 */       if (c == '"') {
/*   205 */         if (tokenStart == -1) {
/*   206 */           tokenStart = i + 1;
/*       */         } else {
/*   208 */           if (addLineSeparator) {
/*   209 */             buffer.append('\n');
/*   210 */             result[(resultCount++)] = currentToken.toString();
/*   211 */             currentToken = new StringBuffer();
/*       */           }
/*   213 */           String token = contents.substring(tokenStart, i);
/*   214 */           if (token.equals("$error"))
/*   215 */             token = "Invalid Character";
/*   216 */           else if (token.equals("$eof")) {
/*   217 */             token = "Unexpected End Of File";
/*       */           }
/*   219 */           buffer.append(token);
/*   220 */           currentToken.append(token);
/*   221 */           addLineSeparator = true;
/*   222 */           tokenStart = -1;
/*       */         }
/*       */       }
/*   225 */       if ((tokenStart == -1) && (c == '+')) {
/*   226 */         addLineSeparator = false;
/*       */       }
/*       */     }
/*   229 */     if (currentToken.length() > 0) {
/*   230 */       result[(resultCount++)] = currentToken.toString();
/*       */     }
/*       */ 
/*   233 */     buildFileForTable(filename, buffer.toString().toCharArray());
/*       */ 
/*   235 */     System.arraycopy(result, 0, result = new String[resultCount], 0, resultCount);
/*   236 */     return result;
/*       */   }
/*       */ 
/*       */   private static void buildFileForReadableName(String file, char[] newLhs, char[] newNonTerminalIndex, String[] newName, String[] tokens)
/*       */   {
/*   245 */     ArrayList entries = new ArrayList();
/*       */ 
/*   247 */     boolean[] alreadyAdded = new boolean[newName.length];
/*       */ 
/*   249 */     for (int i = 0; i < tokens.length; i += 3) {
/*   250 */       if ("1".equals(tokens[i])) {
/*   251 */         int index = newNonTerminalIndex[newLhs[Integer.parseInt(tokens[(i + 1)])]];
/*   252 */         StringBuffer buffer = new StringBuffer();
/*   253 */         if (alreadyAdded[index] == 0) {
/*   254 */           alreadyAdded[index] = true;
/*   255 */           buffer.append(newName[index]);
/*   256 */           buffer.append('=');
/*   257 */           buffer.append(tokens[(i + 2)].trim());
/*   258 */           buffer.append('\n');
/*   259 */           entries.add(String.valueOf(buffer));
/*       */         }
/*       */       }
/*       */     }
/*   263 */     int i = 1;
/*   264 */     while (!"Invalid Character".equals(newName[i])) i++;
/*   265 */     i++;
/*   266 */     for (; i < alreadyAdded.length; i++) {
/*   267 */       if (alreadyAdded[i] == 0) {
/*   268 */         System.out.println(newName[i] + " has no readable name");
/*       */       }
/*       */     }
/*   271 */     Collections.sort(entries);
/*   272 */     buildFile(file, entries);
/*       */   }
/*       */   private static final void buildFileForTable(String filename, byte[] bytes) {
/*   275 */     FileOutputStream stream = null;
/*       */     try {
/*   277 */       stream = new FileOutputStream(filename);
/*   278 */       stream.write(bytes);
/*       */     }
/*       */     catch (IOException localIOException1)
/*       */     {
/*   282 */       if (stream != null)
/*       */         try {
/*   284 */           stream.close();
/*       */         }
/*       */         catch (IOException localIOException2)
/*       */         {
/*       */         }
/*       */     }
/*       */     finally
/*       */     {
/*   282 */       if (stream != null)
/*       */         try {
/*   284 */           stream.close();
/*       */         }
/*       */         catch (IOException localIOException3)
/*       */         {
/*       */         }
/*       */     }
/*   290 */     System.out.println(filename + " creation complete");
/*       */   }
/*       */   private static final void buildFileForTable(String filename, char[] chars) {
/*   293 */     byte[] bytes = new byte[chars.length * 2];
/*   294 */     for (int i = 0; i < chars.length; i++) {
/*   295 */       bytes[(2 * i)] = (byte)(chars[i] >>> '\b');
/*   296 */       bytes[(2 * i + 1)] = (byte)(chars[i] & 0xFF);
/*       */     }
/*       */ 
/*   299 */     FileOutputStream stream = null;
/*       */     try {
/*   301 */       stream = new FileOutputStream(filename);
/*   302 */       stream.write(bytes);
/*       */     }
/*       */     catch (IOException localIOException1)
/*       */     {
/*   306 */       if (stream != null)
/*       */         try {
/*   308 */           stream.close();
/*       */         }
/*       */         catch (IOException localIOException2)
/*       */         {
/*       */         }
/*       */     }
/*       */     finally
/*       */     {
/*   306 */       if (stream != null)
/*       */         try {
/*   308 */           stream.close();
/*       */         }
/*       */         catch (IOException localIOException3)
/*       */         {
/*       */         }
/*       */     }
/*   314 */     System.out.println(filename + " creation complete");
/*       */   }
/*       */ 
/*       */   private static final byte[] buildFileOfByteFor(String filename, String tag, String[] tokens)
/*       */   {
/*   320 */     int i = 0;
/*       */ 
/*   322 */     while (!tokens[(i++)].equals(tag));
/*   325 */     byte[] bytes = new byte[tokens.length];
/*   326 */     int ic = 0;
/*       */     String token;
/*   328 */     while (!(token = tokens[(i++)]).equals("}"))
/*       */     {
/*       */       String token;
/*   329 */       int c = Integer.parseInt(token);
/*   330 */       bytes[(ic++)] = (byte)c;
/*       */     }
/*       */ 
/*   334 */     System.arraycopy(bytes, 0, bytes = new byte[ic], 0, ic);
/*       */ 
/*   336 */     buildFileForTable(filename, bytes);
/*   337 */     return bytes;
/*       */   }
/*       */ 
/*       */   private static final char[] buildFileOfIntFor(String filename, String tag, String[] tokens)
/*       */   {
/*   343 */     int i = 0;
/*       */ 
/*   345 */     while (!tokens[(i++)].equals(tag));
/*   348 */     char[] chars = new char[tokens.length];
/*   349 */     int ic = 0;
/*       */     String token;
/*   351 */     while (!(token = tokens[(i++)]).equals("}"))
/*       */     {
/*       */       String token;
/*   352 */       int c = Integer.parseInt(token);
/*   353 */       chars[(ic++)] = (char)c;
/*       */     }
/*       */ 
/*   357 */     System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
/*       */ 
/*   359 */     buildFileForTable(filename, chars);
/*   360 */     return chars;
/*       */   }
/*       */ 
/*       */   private static final void buildFileOfShortFor(String filename, String tag, String[] tokens)
/*       */   {
/*   366 */     int i = 0;
/*       */ 
/*   368 */     while (!tokens[(i++)].equals(tag));
/*   371 */     char[] chars = new char[tokens.length];
/*   372 */     int ic = 0;
/*       */     String token;
/*   374 */     while (!(token = tokens[(i++)]).equals("}"))
/*       */     {
/*       */       String token;
/*   375 */       int c = Integer.parseInt(token);
/*   376 */       chars[(ic++)] = (char)(c + 32768);
/*       */     }
/*       */ 
/*   380 */     System.arraycopy(chars, 0, chars = new char[ic], 0, ic);
/*       */ 
/*   382 */     buildFileForTable(filename, chars);
/*       */   }
/*       */ 
/*       */   private static void buildFilesForRecoveryTemplates(String indexFilename, String templatesFilename, char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName, char[] newLhs, String[] tokens)
/*       */   {
/*   393 */     int[] newReverse = computeReverseTable(newTerminalIndex, newNonTerminalIndex, newName);
/*       */ 
/*   395 */     char[] newRecoveyTemplatesIndex = new char[newNonTerminalIndex.length];
/*   396 */     char[] newRecoveyTemplates = new char[newNonTerminalIndex.length];
/*   397 */     int newRecoveyTemplatesPtr = 0;
/*       */ 
/*   399 */     for (int i = 0; i < tokens.length; i += 3) {
/*   400 */       if ("3".equals(tokens[i])) {
/*   401 */         int length = newRecoveyTemplates.length;
/*   402 */         if (length == newRecoveyTemplatesPtr + 1) {
/*   403 */           System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
/*       */         }
/*   405 */         newRecoveyTemplates[(newRecoveyTemplatesPtr++)] = '\000';
/*       */ 
/*   407 */         int index = newLhs[Integer.parseInt(tokens[(i + 1)])];
/*       */ 
/*   409 */         newRecoveyTemplatesIndex[index] = (char)newRecoveyTemplatesPtr;
/*       */ 
/*   411 */         String token = tokens[(i + 2)].trim();
/*   412 */         StringTokenizer st = new StringTokenizer(new String(token), " ");
/*   413 */         String[] terminalNames = new String[st.countTokens()];
/*   414 */         int t = 0;
/*   415 */         while (st.hasMoreTokens()) {
/*   416 */           terminalNames[(t++)] = st.nextToken();
/*       */         }
/*       */ 
/*   419 */         for (int j = 0; j < terminalNames.length; j++) {
/*   420 */           int symbol = getSymbol(terminalNames[j], newName, newReverse);
/*   421 */           if (symbol > -1) {
/*   422 */             length = newRecoveyTemplates.length;
/*   423 */             if (length == newRecoveyTemplatesPtr + 1) {
/*   424 */               System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
/*       */             }
/*   426 */             newRecoveyTemplates[(newRecoveyTemplatesPtr++)] = (char)symbol;
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*   431 */     newRecoveyTemplates[(newRecoveyTemplatesPtr++)] = '\000';
/*   432 */     System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[newRecoveyTemplatesPtr], 0, newRecoveyTemplatesPtr);
/*       */ 
/*   434 */     buildFileForTable(indexFilename, newRecoveyTemplatesIndex);
/*   435 */     buildFileForTable(templatesFilename, newRecoveyTemplates);
/*       */   }
/*       */ 
/*       */   private static void buildFilesForStatementsRecoveryFilter(String filename, char[] newNonTerminalIndex, char[] newLhs, String[] tokens)
/*       */   {
/*   443 */     char[] newStatementsRecoveryFilter = new char[newNonTerminalIndex.length];
/*       */ 
/*   445 */     for (int i = 0; i < tokens.length; i += 3) {
/*   446 */       if ("4".equals(tokens[i])) {
/*   447 */         int index = newLhs[Integer.parseInt(tokens[(i + 1)])];
/*       */ 
/*   449 */         newStatementsRecoveryFilter[index] = '\001';
/*       */       }
/*       */     }
/*   452 */     buildFileForTable(filename, newStatementsRecoveryFilter);
/*       */   }
/*       */ 
/*       */   public static final void buildFilesFromLPG(String dataFilename, String dataFilename2)
/*       */   {
/*   462 */     char[] contents = CharOperation.NO_CHAR;
/*       */     try {
/*   464 */       contents = Util.getFileCharContent(new File(dataFilename), null);
/*       */     } catch (IOException localIOException1) {
/*   466 */       System.out.println(Messages.parser_incorrectPath);
/*   467 */       return;
/*       */     }
/*   469 */     StringTokenizer st = 
/*   470 */       new StringTokenizer(new String(contents), " \t\n\r[]={,;");
/*   471 */     String[] tokens = new String[st.countTokens()];
/*   472 */     int j = 0;
/*   473 */     while (st.hasMoreTokens()) {
/*   474 */       tokens[(j++)] = st.nextToken();
/*       */     }
/*       */ 
/*   477 */     int i = 0;
/*       */ 
/*   479 */     i++; char[] newLhs = buildFileOfIntFor("parser" + i + ".rsc", "lhs", tokens);
/*   480 */     i++; buildFileOfShortFor("parser" + i + ".rsc", "check_table", tokens);
/*   481 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "asb", tokens);
/*   482 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "asr", tokens);
/*   483 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "nasb", tokens);
/*   484 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "nasr", tokens);
/*   485 */     i++; char[] newTerminalIndex = buildFileOfIntFor("parser" + i + ".rsc", "terminal_index", tokens);
/*   486 */     i++; char[] newNonTerminalIndex = buildFileOfIntFor("parser" + i + ".rsc", "non_terminal_index", tokens);
/*   487 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "term_action", tokens);
/*       */ 
/*   489 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "scope_prefix", tokens);
/*   490 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "scope_suffix", tokens);
/*   491 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "scope_lhs", tokens);
/*   492 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "scope_state_set", tokens);
/*   493 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "scope_rhs", tokens);
/*   494 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "scope_state", tokens);
/*   495 */     i++; buildFileOfIntFor("parser" + i + ".rsc", "in_symb", tokens);
/*       */ 
/*   497 */     i++; byte[] newRhs = buildFileOfByteFor("parser" + i + ".rsc", "rhs", tokens);
/*   498 */     i++; buildFileOfByteFor("parser" + i + ".rsc", "term_check", tokens);
/*   499 */     i++; buildFileOfByteFor("parser" + i + ".rsc", "scope_la", tokens);
/*       */ 
/*   501 */     i++; String[] newName = buildFileForName("parser" + i + ".rsc", new String(contents));
/*       */ 
/*   503 */     contents = CharOperation.NO_CHAR;
/*       */     try {
/*   505 */       contents = Util.getFileCharContent(new File(dataFilename2), null);
/*       */     } catch (IOException localIOException2) {
/*   507 */       System.out.println(Messages.parser_incorrectPath);
/*   508 */       return;
/*       */     }
/*   510 */     st = new StringTokenizer(new String(contents), "\t\n\r#");
/*   511 */     tokens = new String[st.countTokens()];
/*   512 */     j = 0;
/*   513 */     while (st.hasMoreTokens()) {
/*   514 */       tokens[(j++)] = st.nextToken();
/*       */     }
/*       */ 
/*   517 */     i++; buildFileForCompliance("parser" + i + ".rsc", newRhs.length, tokens);
/*   518 */     buildFileForReadableName("readableNames.properties", newLhs, newNonTerminalIndex, newName, tokens);
/*       */ 
/*   521 */     i++;
/*   522 */     i++;
/*       */ 
/*   520 */     buildFilesForRecoveryTemplates(
/*   521 */       "parser" + i + ".rsc", 
/*   522 */       "parser" + i + ".rsc", 
/*   523 */       newTerminalIndex, 
/*   524 */       newNonTerminalIndex, 
/*   525 */       newName, 
/*   526 */       newLhs, 
/*   527 */       tokens);
/*       */ 
/*   530 */     i++;
/*       */ 
/*   529 */     buildFilesForStatementsRecoveryFilter(
/*   530 */       "parser" + i + ".rsc", 
/*   531 */       newNonTerminalIndex, 
/*   532 */       newLhs, 
/*   533 */       tokens);
/*       */ 
/*   536 */     System.out.println(Messages.parser_moveFiles);
/*       */   }
/*       */   protected static int[] computeReverseTable(char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName) {
/*   539 */     int[] newReverseTable = new int[newName.length];
/*   540 */     for (int j = 0; j < newName.length; j++)
/*       */     {
/*   542 */       int k = 0;
/*       */       while (true) if (newTerminalIndex[k] == j) {
/*   544 */           newReverseTable[j] = k;
/*       */         }
/*       */         else
/*       */         {
/*   542 */           k++; if (k < newTerminalIndex.length)
/*       */           {
/*       */             continue;
/*       */           }
/*       */ 
/*   548 */           for (int k = 0; k < newNonTerminalIndex.length; k++) {
/*   549 */             if (newNonTerminalIndex[k] == j) {
/*   550 */               newReverseTable[j] = (-k);
/*   551 */               break;
/*       */             }
/*       */           }
/*       */         }
/*       */     }
/*   556 */     return newReverseTable;
/*       */   }
/*       */ 
/*       */   private static int getSymbol(String terminalName, String[] newName, int[] newReverse) {
/*   560 */     for (int j = 0; j < newName.length; j++) {
/*   561 */       if (terminalName.equals(newName[j])) {
/*   562 */         return newReverse[j];
/*       */       }
/*       */     }
/*   565 */     return -1;
/*       */   }
/*       */   public static int in_symbol(int state) {
/*   568 */     return in_symb[original_state(state)];
/*       */   }
/*       */ 
/*       */   public static final void initTables() throws IOException
/*       */   {
/*   573 */     int i = 0;
/*   574 */     i++; lhs = readTable("parser" + i + ".rsc");
/*   575 */     i++; char[] chars = readTable("parser" + i + ".rsc");
/*   576 */     check_table = new short[chars.length];
/*   577 */     for (int c = chars.length; c-- > 0; ) {
/*   578 */       check_table[c] = (short)(chars[c] - 32768);
/*       */     }
/*   580 */     i++; asb = readTable("parser" + i + ".rsc");
/*   581 */     i++; asr = readTable("parser" + i + ".rsc");
/*   582 */     i++; nasb = readTable("parser" + i + ".rsc");
/*   583 */     i++; nasr = readTable("parser" + i + ".rsc");
/*   584 */     i++; terminal_index = readTable("parser" + i + ".rsc");
/*   585 */     i++; non_terminal_index = readTable("parser" + i + ".rsc");
/*   586 */     i++; term_action = readTable("parser" + i + ".rsc");
/*       */ 
/*   588 */     i++; scope_prefix = readTable("parser" + i + ".rsc");
/*   589 */     i++; scope_suffix = readTable("parser" + i + ".rsc");
/*   590 */     i++; scope_lhs = readTable("parser" + i + ".rsc");
/*   591 */     i++; scope_state_set = readTable("parser" + i + ".rsc");
/*   592 */     i++; scope_rhs = readTable("parser" + i + ".rsc");
/*   593 */     i++; scope_state = readTable("parser" + i + ".rsc");
/*   594 */     i++; in_symb = readTable("parser" + i + ".rsc");
/*       */ 
/*   596 */     i++; rhs = readByteTable("parser" + i + ".rsc");
/*   597 */     i++; term_check = readByteTable("parser" + i + ".rsc");
/*   598 */     i++; scope_la = readByteTable("parser" + i + ".rsc");
/*       */ 
/*   600 */     i++; name = readNameTable("parser" + i + ".rsc");
/*       */ 
/*   602 */     i++; rules_compliance = readLongTable("parser" + i + ".rsc");
/*       */ 
/*   604 */     readableName = readReadableNameTable("org.eclipse.jdt.internal.compiler.parser.readableNames");
/*       */ 
/*   606 */     reverse_index = computeReverseTable(terminal_index, non_terminal_index, name);
/*       */ 
/*   608 */     i++; recovery_templates_index = readTable("parser" + i + ".rsc");
/*   609 */     i++; recovery_templates = readTable("parser" + i + ".rsc");
/*       */ 
/*   611 */     i++; statements_recovery_filter = readTable("parser" + i + ".rsc");
/*       */ 
/*   613 */     base_action = lhs;
/*       */   }
/*       */   public static int nasi(int state) {
/*   616 */     return nasb[original_state(state)];
/*       */   }
/*       */   public static int ntAction(int state, int sym) {
/*   619 */     return base_action[(state + sym)];
/*       */   }
/*       */   protected static int original_state(int state) {
/*   622 */     return -base_check(state);
/*       */   }
/*       */ 
/*       */   protected static byte[] readByteTable(String filename)
/*       */     throws IOException
/*       */   {
/*   629 */     InputStream stream = Parser.class.getResourceAsStream(filename);
/*   630 */     if (stream == null) {
/*   631 */       throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
/*       */     }
/*   633 */     byte[] bytes = (byte[])null;
/*       */     try {
/*   635 */       stream = new BufferedInputStream(stream);
/*   636 */       bytes = Util.getInputStreamAsByteArray(stream, -1);
/*       */     } finally {
/*       */       try {
/*   639 */         stream.close();
/*       */       }
/*       */       catch (IOException localIOException1) {
/*       */       }
/*       */     }
/*   644 */     return bytes;
/*       */   }
/*       */ 
/*       */   protected static long[] readLongTable(String filename)
/*       */     throws IOException
/*       */   {
/*   650 */     InputStream stream = Parser.class.getResourceAsStream(filename);
/*   651 */     if (stream == null) {
/*   652 */       throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
/*       */     }
/*   654 */     byte[] bytes = (byte[])null;
/*       */     try {
/*   656 */       stream = new BufferedInputStream(stream);
/*   657 */       bytes = Util.getInputStreamAsByteArray(stream, -1);
/*       */     } finally {
/*       */       try {
/*   660 */         stream.close();
/*       */       }
/*       */       catch (IOException localIOException1)
/*       */       {
/*       */       }
/*       */     }
/*       */ 
/*   667 */     int length = bytes.length;
/*   668 */     if (length % 8 != 0) {
/*   669 */       throw new IOException(Messages.bind(Messages.parser_corruptedFile, filename));
/*       */     }
/*       */ 
/*   672 */     long[] longs = new long[length / 8];
/*   673 */     int i = 0;
/*   674 */     int longIndex = 0;
/*       */     do
/*       */     {
/*   677 */       longs[(longIndex++)] = 
/*   678 */         (((bytes[(i++)] & 0xFF) << 56) + (
/*   679 */         (bytes[(i++)] & 0xFF) << 48) + (
/*   680 */         (bytes[(i++)] & 0xFF) << 40) + (
/*   681 */         (bytes[(i++)] & 0xFF) << 32) + (
/*   682 */         (bytes[(i++)] & 0xFF) << 24) + (
/*   683 */         (bytes[(i++)] & 0xFF) << 16) + (
/*   684 */         (bytes[(i++)] & 0xFF) << 8) + (
/*   685 */         bytes[(i++)] & 0xFF));
/*       */     }
/*   687 */     while (i != length);
/*       */ 
/*   690 */     return longs;
/*       */   }
/*       */ 
/*       */   protected static String[] readNameTable(String filename) throws IOException {
/*   694 */     char[] contents = readTable(filename);
/*   695 */     char[][] nameAsChar = CharOperation.splitOn('\n', contents);
/*       */ 
/*   697 */     String[] result = new String[nameAsChar.length + 1];
/*   698 */     result[0] = null;
/*   699 */     for (int i = 0; i < nameAsChar.length; i++) {
/*   700 */       result[(i + 1)] = new String(nameAsChar[i]);
/*       */     }
/*       */ 
/*   703 */     return result;
/*       */   }
/*       */   protected static String[] readReadableNameTable(String filename) {
/*   706 */     String[] result = new String[name.length];
/*       */     try
/*       */     {
/*   710 */       bundle = ResourceBundle.getBundle(filename, Locale.getDefault());
/*       */     }
/*       */     catch (MissingResourceException e)
/*       */     {
/*       */       ResourceBundle bundle;
/*   712 */       System.out.println("Missing resource : " + filename.replace('.', '/') + ".properties for locale " + Locale.getDefault());
/*   713 */       throw e;
/*       */     }
/*       */     ResourceBundle bundle;
/*   715 */     for (int i = 0; i < 111; i++) {
/*   716 */       result[i] = name[i];
/*       */     }
/*   718 */     for (int i = 110; i < name.length; i++) {
/*       */       try {
/*   720 */         String n = bundle.getString(name[i]);
/*   721 */         if ((n != null) && (n.length() > 0))
/*   722 */           result[i] = n;
/*       */         else
/*   724 */           result[i] = name[i];
/*       */       }
/*       */       catch (MissingResourceException localMissingResourceException1) {
/*   727 */         result[i] = name[i];
/*       */       }
/*       */     }
/*   730 */     return result;
/*       */   }
/*       */ 
/*       */   protected static char[] readTable(String filename)
/*       */     throws IOException
/*       */   {
/*   736 */     InputStream stream = Parser.class.getResourceAsStream(filename);
/*   737 */     if (stream == null) {
/*   738 */       throw new IOException(Messages.bind(Messages.parser_missingFile, filename));
/*       */     }
/*   740 */     byte[] bytes = (byte[])null;
/*       */     try {
/*   742 */       stream = new BufferedInputStream(stream);
/*   743 */       bytes = Util.getInputStreamAsByteArray(stream, -1);
/*       */     } finally {
/*       */       try {
/*   746 */         stream.close();
/*       */       }
/*       */       catch (IOException localIOException1)
/*       */       {
/*       */       }
/*       */     }
/*       */ 
/*   753 */     int length = bytes.length;
/*   754 */     if ((length & 0x1) != 0) {
/*   755 */       throw new IOException(Messages.bind(Messages.parser_corruptedFile, filename));
/*       */     }
/*       */ 
/*   758 */     char[] chars = new char[length / 2];
/*   759 */     int i = 0;
/*   760 */     int charIndex = 0;
/*       */     do
/*       */     {
/*   763 */       chars[(charIndex++)] = (char)(((bytes[(i++)] & 0xFF) << 8) + (bytes[(i++)] & 0xFF));
/*   764 */     }while (i != length);
/*       */ 
/*   767 */     return chars;
/*       */   }
/*       */   public static int tAction(int state, int sym) {
/*   770 */     return term_action[base_action[state]];
/*       */   }
/*       */ 
/*       */   public Parser(ProblemReporter problemReporter, boolean optimizeStringLiterals)
/*       */   {
/*   874 */     this.problemReporter = problemReporter;
/*   875 */     this.options = problemReporter.options;
/*   876 */     this.optimizeStringLiterals = optimizeStringLiterals;
/*   877 */     initializeScanner();
/*   878 */     this.astLengthStack = new int[50];
/*   879 */     this.expressionLengthStack = new int[30];
/*   880 */     this.intStack = new int[50];
/*   881 */     this.identifierStack = new char[30][];
/*   882 */     this.identifierLengthStack = new int[30];
/*   883 */     this.nestedMethod = new int[30];
/*   884 */     this.realBlockStack = new int[30];
/*   885 */     this.identifierPositionStack = new long[30];
/*   886 */     this.variablesCounter = new int[30];
/*       */ 
/*   889 */     this.javadocParser = createJavadocParser();
/*       */   }
/*       */   protected void annotationRecoveryCheckPoint(int start, int end) {
/*   892 */     if (this.lastCheckPoint < end)
/*   893 */       this.lastCheckPoint = (end + 1);
/*       */   }
/*       */ 
/*       */   public void arrayInitializer(int length)
/*       */   {
/*   901 */     ArrayInitializer ai = new ArrayInitializer();
/*   902 */     if (length != 0) {
/*   903 */       this.expressionPtr -= length;
/*   904 */       System.arraycopy(this.expressionStack, this.expressionPtr + 1, ai.expressions = new Expression[length], 0, length);
/*       */     }
/*   906 */     pushOnExpressionStack(ai);
/*       */ 
/*   908 */     ai.sourceEnd = this.endStatementPosition;
/*   909 */     ai.sourceStart = this.intStack[(this.intPtr--)];
/*       */   }
/*       */ 
/*       */   protected void blockReal()
/*       */   {
/*   914 */     this.realBlockStack[this.realBlockPtr] += 1;
/*       */   }
/*       */ 
/*       */   public RecoveredElement buildInitialRecoveryState()
/*       */   {
/*   925 */     this.lastCheckPoint = 0;
/*   926 */     this.lastErrorEndPositionBeforeRecovery = this.scanner.currentPosition;
/*       */ 
/*   928 */     RecoveredElement element = null;
/*   929 */     if ((this.referenceContext instanceof CompilationUnitDeclaration)) {
/*   930 */       element = new RecoveredUnit(this.compilationUnit, 0, this);
/*       */ 
/*   935 */       this.compilationUnit.currentPackage = null;
/*   936 */       this.compilationUnit.imports = null;
/*   937 */       this.compilationUnit.types = null;
/*   938 */       this.currentToken = 0;
/*   939 */       this.listLength = 0;
/*   940 */       this.listTypeParameterLength = 0;
/*   941 */       this.endPosition = 0;
/*   942 */       this.endStatementPosition = 0;
/*   943 */       return element;
/*       */     }
/*   945 */     if ((this.referenceContext instanceof AbstractMethodDeclaration)) {
/*   946 */       element = new RecoveredMethod((AbstractMethodDeclaration)this.referenceContext, null, 0, this);
/*   947 */       this.lastCheckPoint = ((AbstractMethodDeclaration)this.referenceContext).bodyStart;
/*   948 */       if (this.statementRecoveryActivated) {
/*   949 */         element = element.add(new Block(0), 0);
/*       */       }
/*       */ 
/*       */     }
/*   953 */     else if ((this.referenceContext instanceof TypeDeclaration)) {
/*   954 */       TypeDeclaration type = (TypeDeclaration)this.referenceContext;
/*   955 */       for (int i = 0; i < type.fields.length; i++) {
/*   956 */         FieldDeclaration field = type.fields[i];
/*   957 */         if ((field == null) || 
/*   958 */           (field.getKind() != 2) || 
/*   959 */           (((Initializer)field).block == null) || 
/*   960 */           (field.declarationSourceStart > this.scanner.initialPosition) || 
/*   961 */           (this.scanner.initialPosition > field.declarationSourceEnd) || 
/*   962 */           (this.scanner.eofPosition > field.declarationSourceEnd + 1)) continue;
/*   963 */         element = new RecoveredInitializer(field, null, 1, this);
/*   964 */         this.lastCheckPoint = field.declarationSourceStart;
/*   965 */         break;
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*   972 */     if (element == null) return element;
/*       */ 
/*   974 */     for (int i = 0; i <= this.astPtr; i++) {
/*   975 */       ASTNode node = this.astStack[i];
/*   976 */       if ((node instanceof AbstractMethodDeclaration)) {
/*   977 */         AbstractMethodDeclaration method = (AbstractMethodDeclaration)node;
/*   978 */         if (method.declarationSourceEnd == 0) {
/*   979 */           element = element.add(method, 0);
/*   980 */           this.lastCheckPoint = method.bodyStart;
/*       */         } else {
/*   982 */           element = element.add(method, 0);
/*   983 */           this.lastCheckPoint = (method.declarationSourceEnd + 1);
/*       */         }
/*       */ 
/*       */       }
/*   987 */       else if ((node instanceof Initializer)) {
/*   988 */         Initializer initializer = (Initializer)node;
/*       */ 
/*   990 */         if (initializer.block != null) {
/*   991 */           if (initializer.declarationSourceEnd == 0) {
/*   992 */             element = element.add(initializer, 1);
/*   993 */             this.lastCheckPoint = initializer.sourceStart;
/*       */           } else {
/*   995 */             element = element.add(initializer, 0);
/*   996 */             this.lastCheckPoint = (initializer.declarationSourceEnd + 1);
/*       */           }
/*       */         }
/*       */       }
/*  1000 */       else if ((node instanceof FieldDeclaration)) {
/*  1001 */         FieldDeclaration field = (FieldDeclaration)node;
/*  1002 */         if (field.declarationSourceEnd == 0) {
/*  1003 */           element = element.add(field, 0);
/*  1004 */           if (field.initialization == null)
/*  1005 */             this.lastCheckPoint = (field.sourceEnd + 1);
/*       */           else
/*  1007 */             this.lastCheckPoint = (field.initialization.sourceEnd + 1);
/*       */         }
/*       */         else {
/*  1010 */           element = element.add(field, 0);
/*  1011 */           this.lastCheckPoint = (field.declarationSourceEnd + 1);
/*       */         }
/*       */ 
/*       */       }
/*  1015 */       else if ((node instanceof TypeDeclaration)) {
/*  1016 */         TypeDeclaration type = (TypeDeclaration)node;
/*  1017 */         if (type.declarationSourceEnd == 0) {
/*  1018 */           element = element.add(type, 0);
/*  1019 */           this.lastCheckPoint = type.bodyStart;
/*       */         } else {
/*  1021 */           element = element.add(type, 0);
/*  1022 */           this.lastCheckPoint = (type.declarationSourceEnd + 1);
/*       */         }
/*       */       }
/*       */       else {
/*  1026 */         if ((node instanceof ImportReference)) {
/*  1027 */           ImportReference importRef = (ImportReference)node;
/*  1028 */           element = element.add(importRef, 0);
/*  1029 */           this.lastCheckPoint = (importRef.declarationSourceEnd + 1);
/*       */         }
/*  1031 */         if (this.statementRecoveryActivated) {
/*  1032 */           if ((node instanceof Block)) {
/*  1033 */             Block block = (Block)node;
/*  1034 */             element = element.add(block, 0);
/*  1035 */             this.lastCheckPoint = (block.sourceEnd + 1);
/*  1036 */           } else if ((node instanceof LocalDeclaration)) {
/*  1037 */             LocalDeclaration statement = (LocalDeclaration)node;
/*  1038 */             element = element.add(statement, 0);
/*  1039 */             this.lastCheckPoint = (statement.sourceEnd + 1);
/*  1040 */           } else if ((node instanceof Expression)) {
/*  1041 */             if ((!(node instanceof Assignment)) && 
/*  1042 */               (!(node instanceof PrefixExpression)) && 
/*  1043 */               (!(node instanceof PostfixExpression)) && 
/*  1044 */               (!(node instanceof MessageSend)) && 
/*  1045 */               (!(node instanceof AllocationExpression)))
/*       */               continue;
/*  1047 */             Expression statement = (Expression)node;
/*  1048 */             element = element.add(statement, 0);
/*  1049 */             if (statement.statementEnd != -1)
/*  1050 */               this.lastCheckPoint = (statement.statementEnd + 1);
/*       */             else {
/*  1052 */               this.lastCheckPoint = (statement.sourceEnd + 1);
/*       */             }
/*       */           }
/*  1055 */           else if ((node instanceof Statement)) {
/*  1056 */             Statement statement = (Statement)node;
/*  1057 */             element = element.add(statement, 0);
/*  1058 */             this.lastCheckPoint = (statement.sourceEnd + 1);
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*  1063 */     if ((this.statementRecoveryActivated) && 
/*  1064 */       (this.pendingRecoveredType != null) && 
/*  1065 */       (this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd))
/*       */     {
/*  1067 */       element = element.add(this.pendingRecoveredType, 0);
/*  1068 */       this.lastCheckPoint = (this.pendingRecoveredType.declarationSourceEnd + 1);
/*  1069 */       this.pendingRecoveredType = null;
/*       */     }
/*       */ 
/*  1072 */     return element;
/*       */   }
/*       */ 
/*       */   protected void checkAndSetModifiers(int flag)
/*       */   {
/*  1083 */     if ((this.modifiers & flag) != 0) {
/*  1084 */       this.modifiers |= 4194304;
/*       */     }
/*  1086 */     this.modifiers |= flag;
/*       */ 
/*  1088 */     if (this.modifiersSourceStart < 0) this.modifiersSourceStart = this.scanner.startPosition;
/*       */ 
/*  1090 */     if ((this.currentElement != null) && (this.annotationRecoveryActivated))
/*  1091 */       this.currentElement.addModifier(flag, this.modifiersSourceStart);
/*       */   }
/*       */ 
/*       */   public void checkComment()
/*       */   {
/*  1097 */     if (((!this.diet) || (this.dietInt != 0)) && (this.scanner.commentPtr >= 0)) {
/*  1098 */       flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */     }
/*       */ 
/*  1101 */     int lastComment = this.scanner.commentPtr;
/*       */ 
/*  1103 */     if (this.modifiersSourceStart >= 0)
/*       */     {
/*  1105 */       while (lastComment >= 0) {
/*  1106 */         int commentSourceStart = this.scanner.commentStarts[lastComment];
/*  1107 */         if (commentSourceStart < 0) commentSourceStart = -commentSourceStart;
/*  1108 */         if (commentSourceStart <= this.modifiersSourceStart) break;
/*  1109 */         lastComment--;
/*       */       }
/*       */     }
/*  1112 */     if (lastComment >= 0)
/*       */     {
/*  1114 */       this.modifiersSourceStart = this.scanner.commentStarts[0];
/*  1115 */       if (this.modifiersSourceStart < 0) this.modifiersSourceStart = (-this.modifiersSourceStart);
/*       */ 
/*  1118 */       while ((lastComment >= 0) && (this.scanner.commentStops[lastComment] < 0)) lastComment--;
/*  1119 */       if ((lastComment >= 0) && (this.javadocParser != null)) {
/*  1120 */         int commentEnd = this.scanner.commentStops[lastComment] - 1;
/*       */ 
/*  1122 */         if (this.javadocParser.shouldReportProblems)
/*  1123 */           this.javadocParser.reportProblems = ((this.currentElement == null) || (commentEnd > this.lastJavadocEnd));
/*       */         else {
/*  1125 */           this.javadocParser.reportProblems = false;
/*       */         }
/*  1127 */         if (this.javadocParser.checkDeprecation(lastComment)) {
/*  1128 */           checkAndSetModifiers(1048576);
/*       */         }
/*  1130 */         this.javadoc = this.javadocParser.docComment;
/*  1131 */         if (this.currentElement == null) this.lastJavadocEnd = commentEnd; 
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void checkNonNLSAfterBodyEnd(int declarationEnd) {
/*  1136 */     if (this.scanner.currentPosition - 1 <= declarationEnd) {
/*  1137 */       this.scanner.eofPosition = (declarationEnd < 2147483647 ? declarationEnd + 1 : declarationEnd);
/*       */       try {
/*  1139 */         while (this.scanner.getNextToken() != 68);
/*       */       }
/*       */       catch (InvalidInputException localInvalidInputException)
/*       */       {
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void classInstanceCreation(boolean isQualified)
/*       */   {
/*       */     int length;
/*  1153 */     if (((length = this.astLengthStack[(this.astLengthPtr--)]) == 1) && 
/*  1154 */       (this.astStack[this.astPtr] == null))
/*       */     {
/*  1156 */       this.astPtr -= 1;
/*       */       AllocationExpression alloc;
/*       */       AllocationExpression alloc;
/*  1157 */       if (isQualified)
/*  1158 */         alloc = new QualifiedAllocationExpression();
/*       */       else {
/*  1160 */         alloc = new AllocationExpression();
/*       */       }
/*  1162 */       alloc.sourceEnd = this.endPosition;
/*       */ 
/*  1164 */       if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  1165 */         this.expressionPtr -= length;
/*  1166 */         System.arraycopy(
/*  1167 */           this.expressionStack, 
/*  1168 */           this.expressionPtr + 1, 
/*  1169 */           alloc.arguments = new Expression[length], 
/*  1170 */           0, 
/*  1171 */           length);
/*       */       }
/*  1173 */       alloc.type = getTypeReference(0);
/*       */ 
/*  1177 */       alloc.sourceStart = this.intStack[(this.intPtr--)];
/*  1178 */       pushOnExpressionStack(alloc);
/*       */     } else {
/*  1180 */       dispatchDeclarationInto(length);
/*  1181 */       TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
/*  1182 */       anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
/*  1183 */       anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
/*  1184 */       if (anonymousTypeDeclaration.allocation != null) {
/*  1185 */         anonymousTypeDeclaration.allocation.sourceEnd = this.endStatementPosition;
/*       */       }
/*  1187 */       if ((length == 0) && (!containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd))) {
/*  1188 */         anonymousTypeDeclaration.bits |= 8;
/*       */       }
/*  1190 */       this.astPtr -= 1;
/*  1191 */       this.astLengthPtr -= 1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected ParameterizedQualifiedTypeReference computeQualifiedGenericsFromRightSide(TypeReference rightSide, int dim) {
/*  1195 */     int nameSize = this.identifierLengthStack[this.identifierLengthPtr];
/*  1196 */     int tokensSize = nameSize;
/*  1197 */     if ((rightSide instanceof ParameterizedSingleTypeReference))
/*  1198 */       tokensSize++;
/*  1199 */     else if ((rightSide instanceof SingleTypeReference))
/*  1200 */       tokensSize++;
/*  1201 */     else if ((rightSide instanceof ParameterizedQualifiedTypeReference))
/*  1202 */       tokensSize += ((QualifiedTypeReference)rightSide).tokens.length;
/*  1203 */     else if ((rightSide instanceof QualifiedTypeReference)) {
/*  1204 */       tokensSize += ((QualifiedTypeReference)rightSide).tokens.length;
/*       */     }
/*  1206 */     TypeReference[][] typeArguments = new TypeReference[tokensSize][];
/*  1207 */     char[][] tokens = new char[tokensSize][];
/*  1208 */     long[] positions = new long[tokensSize];
/*  1209 */     if ((rightSide instanceof ParameterizedSingleTypeReference)) {
/*  1210 */       ParameterizedSingleTypeReference singleParameterizedTypeReference = (ParameterizedSingleTypeReference)rightSide;
/*  1211 */       tokens[nameSize] = singleParameterizedTypeReference.token;
/*  1212 */       positions[nameSize] = ((singleParameterizedTypeReference.sourceStart << 32) + singleParameterizedTypeReference.sourceEnd);
/*  1213 */       typeArguments[nameSize] = singleParameterizedTypeReference.typeArguments;
/*  1214 */     } else if ((rightSide instanceof SingleTypeReference)) {
/*  1215 */       SingleTypeReference singleTypeReference = (SingleTypeReference)rightSide;
/*  1216 */       tokens[nameSize] = singleTypeReference.token;
/*  1217 */       positions[nameSize] = ((singleTypeReference.sourceStart << 32) + singleTypeReference.sourceEnd);
/*  1218 */     } else if ((rightSide instanceof ParameterizedQualifiedTypeReference)) {
/*  1219 */       ParameterizedQualifiedTypeReference parameterizedTypeReference = (ParameterizedQualifiedTypeReference)rightSide;
/*  1220 */       TypeReference[][] rightSideTypeArguments = parameterizedTypeReference.typeArguments;
/*  1221 */       System.arraycopy(rightSideTypeArguments, 0, typeArguments, nameSize, rightSideTypeArguments.length);
/*  1222 */       char[][] rightSideTokens = parameterizedTypeReference.tokens;
/*  1223 */       System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
/*  1224 */       long[] rightSidePositions = parameterizedTypeReference.sourcePositions;
/*  1225 */       System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
/*  1226 */     } else if ((rightSide instanceof QualifiedTypeReference)) {
/*  1227 */       QualifiedTypeReference qualifiedTypeReference = (QualifiedTypeReference)rightSide;
/*  1228 */       char[][] rightSideTokens = qualifiedTypeReference.tokens;
/*  1229 */       System.arraycopy(rightSideTokens, 0, tokens, nameSize, rightSideTokens.length);
/*  1230 */       long[] rightSidePositions = qualifiedTypeReference.sourcePositions;
/*  1231 */       System.arraycopy(rightSidePositions, 0, positions, nameSize, rightSidePositions.length);
/*       */     }
/*       */ 
/*  1234 */     int currentTypeArgumentsLength = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  1235 */     TypeReference[] currentTypeArguments = new TypeReference[currentTypeArgumentsLength];
/*  1236 */     this.genericsPtr -= currentTypeArgumentsLength;
/*  1237 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, currentTypeArguments, 0, currentTypeArgumentsLength);
/*       */ 
/*  1239 */     if (nameSize == 1) {
/*  1240 */       tokens[0] = this.identifierStack[this.identifierPtr];
/*  1241 */       positions[0] = this.identifierPositionStack[(this.identifierPtr--)];
/*  1242 */       typeArguments[0] = currentTypeArguments;
/*       */     } else {
/*  1244 */       this.identifierPtr -= nameSize;
/*  1245 */       System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, nameSize);
/*  1246 */       System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, nameSize);
/*  1247 */       typeArguments[(nameSize - 1)] = currentTypeArguments;
/*       */     }
/*  1249 */     this.identifierLengthPtr -= 1;
/*  1250 */     return new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, positions);
/*       */   }
/*       */   protected void concatExpressionLists() {
/*  1253 */     this.expressionLengthStack[(--this.expressionLengthPtr)] += 1;
/*       */   }
/*       */   protected void concatGenericsLists() {
/*  1256 */     this.genericsLengthStack[(this.genericsLengthPtr - 1)] += this.genericsLengthStack[(this.genericsLengthPtr--)];
/*       */   }
/*       */ 
/*       */   protected void concatNodeLists()
/*       */   {
/*  1272 */     this.astLengthStack[(this.astLengthPtr - 1)] += this.astLengthStack[(this.astLengthPtr--)];
/*       */   }
/*       */   protected void consumeAdditionalBound() {
/*  1275 */     pushOnGenericsStack(getTypeReference(this.intStack[(this.intPtr--)]));
/*       */   }
/*       */ 
/*       */   protected void consumeAdditionalBound1() {
/*       */   }
/*       */ 
/*       */   protected void consumeAdditionalBoundList() {
/*  1282 */     concatGenericsLists();
/*       */   }
/*       */   protected void consumeAdditionalBoundList1() {
/*  1285 */     concatGenericsLists();
/*       */   }
/*       */ 
/*       */   protected void consumeAllocationHeader()
/*       */   {
/*  1293 */     if (this.currentElement == null) {
/*  1294 */       return;
/*       */     }
/*  1296 */     if (this.currentToken == 69)
/*       */     {
/*  1298 */       TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
/*  1299 */       anonymousType.name = CharOperation.NO_CHAR;
/*  1300 */       anonymousType.bits |= 768;
/*  1301 */       anonymousType.sourceStart = this.intStack[(this.intPtr--)];
/*  1302 */       anonymousType.declarationSourceStart = anonymousType.sourceStart;
/*  1303 */       anonymousType.sourceEnd = this.rParenPos;
/*  1304 */       QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
/*  1305 */       alloc.type = getTypeReference(0);
/*  1306 */       alloc.sourceStart = anonymousType.sourceStart;
/*  1307 */       alloc.sourceEnd = anonymousType.sourceEnd;
/*  1308 */       this.lastCheckPoint = (anonymousType.bodyStart = this.scanner.currentPosition);
/*  1309 */       this.currentElement = this.currentElement.add(anonymousType, 0);
/*  1310 */       this.lastIgnoredToken = -1;
/*  1311 */       this.currentToken = 0;
/*  1312 */       return;
/*       */     }
/*  1314 */     this.lastCheckPoint = this.scanner.startPosition;
/*  1315 */     this.restartRecovery = true;
/*       */   }
/*       */   protected void consumeAnnotationAsModifier() {
/*  1318 */     Expression expression = this.expressionStack[this.expressionPtr];
/*  1319 */     int sourceStart = expression.sourceStart;
/*  1320 */     if (this.modifiersSourceStart < 0)
/*  1321 */       this.modifiersSourceStart = sourceStart;
/*       */   }
/*       */ 
/*       */   protected void consumeAnnotationName() {
/*  1325 */     if (this.currentElement != null) {
/*  1326 */       int start = this.intStack[this.intPtr];
/*  1327 */       int end = (int)(this.identifierPositionStack[this.identifierPtr] & 0xFFFFFFFF);
/*  1328 */       annotationRecoveryCheckPoint(start, end);
/*       */ 
/*  1330 */       if (this.annotationRecoveryActivated) {
/*  1331 */         this.currentElement = this.currentElement.addAnnotationName(this.identifierPtr, this.identifierLengthPtr, start, 0);
/*       */       }
/*       */     }
/*  1334 */     this.recordStringLiterals = false;
/*       */   }
/*       */ 
/*       */   protected void consumeAnnotationTypeDeclaration()
/*       */   {
/*       */     int length;
/*  1338 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)
/*       */     {
/*  1341 */       dispatchDeclarationInto(length);
/*       */     }
/*       */ 
/*  1344 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  1347 */     typeDecl.checkConstructors(this);
/*       */ 
/*  1350 */     if (this.scanner.containsAssertKeyword) {
/*  1351 */       typeDecl.bits |= 1;
/*       */     }
/*  1353 */     typeDecl.addClinit();
/*  1354 */     typeDecl.bodyEnd = this.endStatementPosition;
/*  1355 */     if ((length == 0) && (!containsComment(typeDecl.bodyStart, typeDecl.bodyEnd))) {
/*  1356 */       typeDecl.bits |= 8;
/*       */     }
/*  1358 */     typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */   protected void consumeAnnotationTypeDeclarationHeader() {
/*  1361 */     TypeDeclaration annotationTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
/*  1362 */     if (this.currentToken == 69) {
/*  1363 */       annotationTypeDeclaration.bodyStart = this.scanner.currentPosition;
/*       */     }
/*  1365 */     if (this.currentElement != null) {
/*  1366 */       this.restartRecovery = true;
/*       */     }
/*       */ 
/*  1369 */     this.scanner.commentPtr = -1;
/*       */   }
/*       */ 
/*       */   protected void consumeAnnotationTypeDeclarationHeaderName()
/*       */   {
/*  1374 */     TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
/*  1375 */     if (this.nestedMethod[this.nestedType] == 0) {
/*  1376 */       if (this.nestedType != 0)
/*  1377 */         annotationTypeDeclaration.bits |= 1024;
/*       */     }
/*       */     else
/*       */     {
/*  1381 */       annotationTypeDeclaration.bits |= 256;
/*  1382 */       markEnclosingMemberWithLocalType();
/*  1383 */       blockReal();
/*       */     }
/*       */ 
/*  1387 */     long pos = this.identifierPositionStack[this.identifierPtr];
/*  1388 */     annotationTypeDeclaration.sourceEnd = (int)pos;
/*  1389 */     annotationTypeDeclaration.sourceStart = (int)(pos >>> 32);
/*  1390 */     annotationTypeDeclaration.name = this.identifierStack[(this.identifierPtr--)];
/*  1391 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  1397 */     this.intPtr -= 1;
/*  1398 */     this.intPtr -= 1;
/*       */ 
/*  1400 */     annotationTypeDeclaration.modifiersSourceStart = this.intStack[(this.intPtr--)];
/*  1401 */     annotationTypeDeclaration.modifiers = (this.intStack[(this.intPtr--)] | 0x2000 | 0x200);
/*  1402 */     if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
/*  1403 */       annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
/*  1404 */       this.intPtr -= 1;
/*       */     } else {
/*  1406 */       int atPosition = this.intStack[(this.intPtr--)];
/*       */ 
/*  1408 */       annotationTypeDeclaration.declarationSourceStart = atPosition;
/*       */     }
/*       */ 
/*  1412 */     if (((annotationTypeDeclaration.bits & 0x400) == 0) && ((annotationTypeDeclaration.bits & 0x100) == 0) && 
/*  1413 */       (this.compilationUnit != null) && (!CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName())))
/*  1414 */       annotationTypeDeclaration.bits |= 4096;
/*       */     int length;
/*  1420 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  1421 */       System.arraycopy(
/*  1422 */         this.expressionStack, 
/*  1423 */         this.expressionPtr -= length + 1, 
/*  1424 */         annotationTypeDeclaration.annotations = new Annotation[length], 
/*  1425 */         0, 
/*  1426 */         length);
/*       */     }
/*  1428 */     annotationTypeDeclaration.bodyStart = (annotationTypeDeclaration.sourceEnd + 1);
/*       */ 
/*  1431 */     annotationTypeDeclaration.javadoc = this.javadoc;
/*  1432 */     this.javadoc = null;
/*  1433 */     pushOnAstStack(annotationTypeDeclaration);
/*  1434 */     if ((!this.statementRecoveryActivated) && 
/*  1435 */       (this.options.sourceLevel < 3211264L) && 
/*  1436 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  1437 */       problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
/*       */     }
/*       */ 
/*  1441 */     if (this.currentElement != null) {
/*  1442 */       this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
/*  1443 */       this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
/*  1444 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters()
/*       */   {
/*  1450 */     TypeDeclaration annotationTypeDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
/*       */ 
/*  1452 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  1453 */     this.genericsPtr -= length;
/*  1454 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, annotationTypeDeclaration.typeParameters = new TypeParameter[length], 0, length);
/*       */ 
/*  1456 */     problemReporter().invalidUsageOfTypeParametersForAnnotationDeclaration(annotationTypeDeclaration);
/*       */ 
/*  1458 */     annotationTypeDeclaration.bodyStart = (annotationTypeDeclaration.typeParameters[(length - 1)].declarationSourceEnd + 1);
/*       */ 
/*  1462 */     this.listTypeParameterLength = 0;
/*       */ 
/*  1464 */     if (this.nestedMethod[this.nestedType] == 0) {
/*  1465 */       if (this.nestedType != 0)
/*  1466 */         annotationTypeDeclaration.bits |= 1024;
/*       */     }
/*       */     else
/*       */     {
/*  1470 */       annotationTypeDeclaration.bits |= 256;
/*  1471 */       markEnclosingMemberWithLocalType();
/*  1472 */       blockReal();
/*       */     }
/*       */ 
/*  1476 */     long pos = this.identifierPositionStack[this.identifierPtr];
/*  1477 */     annotationTypeDeclaration.sourceEnd = (int)pos;
/*  1478 */     annotationTypeDeclaration.sourceStart = (int)(pos >>> 32);
/*  1479 */     annotationTypeDeclaration.name = this.identifierStack[(this.identifierPtr--)];
/*  1480 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  1486 */     this.intPtr -= 1;
/*  1487 */     this.intPtr -= 1;
/*       */ 
/*  1489 */     annotationTypeDeclaration.modifiersSourceStart = this.intStack[(this.intPtr--)];
/*  1490 */     annotationTypeDeclaration.modifiers = (this.intStack[(this.intPtr--)] | 0x2000 | 0x200);
/*  1491 */     if (annotationTypeDeclaration.modifiersSourceStart >= 0) {
/*  1492 */       annotationTypeDeclaration.declarationSourceStart = annotationTypeDeclaration.modifiersSourceStart;
/*  1493 */       this.intPtr -= 1;
/*       */     } else {
/*  1495 */       int atPosition = this.intStack[(this.intPtr--)];
/*       */ 
/*  1497 */       annotationTypeDeclaration.declarationSourceStart = atPosition;
/*       */     }
/*       */ 
/*  1501 */     if (((annotationTypeDeclaration.bits & 0x400) == 0) && ((annotationTypeDeclaration.bits & 0x100) == 0) && 
/*  1502 */       (this.compilationUnit != null) && (!CharOperation.equals(annotationTypeDeclaration.name, this.compilationUnit.getMainTypeName()))) {
/*  1503 */       annotationTypeDeclaration.bits |= 4096;
/*       */     }
/*       */ 
/*  1508 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  1509 */       System.arraycopy(
/*  1510 */         this.expressionStack, 
/*  1511 */         this.expressionPtr -= length + 1, 
/*  1512 */         annotationTypeDeclaration.annotations = new Annotation[length], 
/*  1513 */         0, 
/*  1514 */         length);
/*       */     }
/*       */ 
/*  1517 */     annotationTypeDeclaration.javadoc = this.javadoc;
/*  1518 */     this.javadoc = null;
/*  1519 */     pushOnAstStack(annotationTypeDeclaration);
/*  1520 */     if ((!this.statementRecoveryActivated) && 
/*  1521 */       (this.options.sourceLevel < 3211264L) && 
/*  1522 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  1523 */       problemReporter().invalidUsageOfAnnotationDeclarations(annotationTypeDeclaration);
/*       */     }
/*       */ 
/*  1527 */     if (this.currentElement != null) {
/*  1528 */       this.lastCheckPoint = annotationTypeDeclaration.bodyStart;
/*  1529 */       this.currentElement = this.currentElement.add(annotationTypeDeclaration, 0);
/*  1530 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeAnnotationTypeMemberDeclaration() {
/*  1535 */     AnnotationMethodDeclaration annotationTypeMemberDeclaration = (AnnotationMethodDeclaration)this.astStack[this.astPtr];
/*  1536 */     annotationTypeMemberDeclaration.modifiers |= 16777216;
/*       */ 
/*  1539 */     int declarationEndPosition = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*  1540 */     annotationTypeMemberDeclaration.bodyStart = this.endStatementPosition;
/*  1541 */     annotationTypeMemberDeclaration.bodyEnd = declarationEndPosition;
/*  1542 */     annotationTypeMemberDeclaration.declarationSourceEnd = declarationEndPosition;
/*       */   }
/*       */ 
/*       */   protected void consumeAnnotationTypeMemberDeclarations() {
/*  1546 */     concatNodeLists();
/*       */   }
/*       */   protected void consumeAnnotationTypeMemberDeclarationsopt() {
/*  1549 */     this.nestedType -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeArgumentList() {
/*  1553 */     concatExpressionLists();
/*       */   }
/*       */ 
/*       */   protected void consumeArguments()
/*       */   {
/*  1558 */     pushOnIntStack(this.rParenPos);
/*       */   }
/*       */ 
/*       */   protected void consumeArrayAccess(boolean unspecifiedReference)
/*       */   {
/*       */     Expression exp;
/*       */     Expression exp;
/*  1567 */     if (unspecifiedReference) {
/*  1568 */       exp = 
/*  1569 */         this.expressionStack[this.expressionPtr] =  = 
/*  1570 */         new ArrayReference(
/*  1571 */         getUnspecifiedReferenceOptimized(), 
/*  1572 */         this.expressionStack[this.expressionPtr]);
/*       */     } else {
/*  1574 */       this.expressionPtr -= 1;
/*  1575 */       this.expressionLengthPtr -= 1;
/*  1576 */       exp = 
/*  1577 */         this.expressionStack[this.expressionPtr] =  = 
/*  1578 */         new ArrayReference(
/*  1579 */         this.expressionStack[this.expressionPtr], 
/*  1580 */         this.expressionStack[(this.expressionPtr + 1)]);
/*       */     }
/*  1582 */     exp.sourceEnd = this.endStatementPosition;
/*       */   }
/*       */ 
/*       */   protected void consumeArrayCreationExpressionWithInitializer()
/*       */   {
/*  1589 */     ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
/*  1590 */     this.expressionLengthPtr -= 1;
/*  1591 */     arrayAllocation.initializer = ((ArrayInitializer)this.expressionStack[(this.expressionPtr--)]);
/*       */ 
/*  1593 */     arrayAllocation.type = getTypeReference(0);
/*  1594 */     arrayAllocation.type.bits |= 1073741824;
/*  1595 */     int length = this.expressionLengthStack[(this.expressionLengthPtr--)];
/*  1596 */     this.expressionPtr -= length;
/*  1597 */     System.arraycopy(
/*  1598 */       this.expressionStack, 
/*  1599 */       this.expressionPtr + 1, 
/*  1600 */       arrayAllocation.dimensions = new Expression[length], 
/*  1601 */       0, 
/*  1602 */       length);
/*  1603 */     arrayAllocation.sourceStart = this.intStack[(this.intPtr--)];
/*  1604 */     if (arrayAllocation.initializer == null)
/*  1605 */       arrayAllocation.sourceEnd = this.endStatementPosition;
/*       */     else {
/*  1607 */       arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd;
/*       */     }
/*  1609 */     pushOnExpressionStack(arrayAllocation);
/*       */   }
/*       */ 
/*       */   protected void consumeArrayCreationExpressionWithoutInitializer()
/*       */   {
/*  1616 */     ArrayAllocationExpression arrayAllocation = new ArrayAllocationExpression();
/*  1617 */     arrayAllocation.type = getTypeReference(0);
/*  1618 */     arrayAllocation.type.bits |= 1073741824;
/*  1619 */     int length = this.expressionLengthStack[(this.expressionLengthPtr--)];
/*  1620 */     this.expressionPtr -= length;
/*  1621 */     System.arraycopy(
/*  1622 */       this.expressionStack, 
/*  1623 */       this.expressionPtr + 1, 
/*  1624 */       arrayAllocation.dimensions = new Expression[length], 
/*  1625 */       0, 
/*  1626 */       length);
/*  1627 */     arrayAllocation.sourceStart = this.intStack[(this.intPtr--)];
/*  1628 */     if (arrayAllocation.initializer == null)
/*  1629 */       arrayAllocation.sourceEnd = this.endStatementPosition;
/*       */     else {
/*  1631 */       arrayAllocation.sourceEnd = arrayAllocation.initializer.sourceEnd;
/*       */     }
/*  1633 */     pushOnExpressionStack(arrayAllocation);
/*       */   }
/*       */ 
/*       */   protected void consumeArrayCreationHeader()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeArrayInitializer()
/*       */   {
/*  1642 */     arrayInitializer(this.expressionLengthStack[(this.expressionLengthPtr--)]);
/*       */   }
/*       */   protected void consumeArrayTypeWithTypeArgumentsName() {
/*  1645 */     this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
/*  1646 */     pushOnGenericsLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeAssertStatement() {
/*  1650 */     this.expressionLengthPtr -= 2;
/*  1651 */     pushOnAstStack(new AssertStatement(this.expressionStack[(this.expressionPtr--)], this.expressionStack[(this.expressionPtr--)], this.intStack[(this.intPtr--)]));
/*       */   }
/*       */ 
/*       */   protected void consumeAssignment()
/*       */   {
/*  1657 */     int op = this.intStack[(this.intPtr--)];
/*       */ 
/*  1659 */     this.expressionPtr -= 1; this.expressionLengthPtr -= 1;
/*  1660 */     this.expressionStack[this.expressionPtr] = 
/*  1661 */       (op != 30 ? 
/*  1662 */       new CompoundAssignment(
/*  1663 */       this.expressionStack[this.expressionPtr], 
/*  1664 */       this.expressionStack[(this.expressionPtr + 1)], 
/*  1665 */       op, 
/*  1666 */       this.scanner.startPosition - 1) : 
/*  1667 */       new Assignment(
/*  1668 */       this.expressionStack[this.expressionPtr], 
/*  1669 */       this.expressionStack[(this.expressionPtr + 1)], 
/*  1670 */       this.scanner.startPosition - 1));
/*       */ 
/*  1672 */     if (this.pendingRecoveredType != null)
/*       */     {
/*  1676 */       if ((this.pendingRecoveredType.allocation != null) && 
/*  1677 */         (this.scanner.startPosition - 1 <= this.pendingRecoveredType.declarationSourceEnd)) {
/*  1678 */         this.expressionStack[this.expressionPtr] = this.pendingRecoveredType.allocation;
/*  1679 */         this.pendingRecoveredType = null;
/*  1680 */         return;
/*       */       }
/*  1682 */       this.pendingRecoveredType = null;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeAssignmentOperator(int pos)
/*       */   {
/*  1699 */     pushOnIntStack(pos);
/*       */   }
/*       */ 
/*       */   protected void consumeBinaryExpression(int op)
/*       */   {
/*  1722 */     this.expressionPtr -= 1;
/*  1723 */     this.expressionLengthPtr -= 1;
/*  1724 */     Expression expr1 = this.expressionStack[this.expressionPtr];
/*  1725 */     Expression expr2 = this.expressionStack[(this.expressionPtr + 1)];
/*  1726 */     switch (op) {
/*       */     case 1:
/*  1728 */       this.expressionStack[this.expressionPtr] = 
/*  1729 */         new OR_OR_Expression(
/*  1730 */         expr1, 
/*  1731 */         expr2, 
/*  1732 */         op);
/*  1733 */       break;
/*       */     case 0:
/*  1735 */       this.expressionStack[this.expressionPtr] = 
/*  1736 */         new AND_AND_Expression(
/*  1737 */         expr1, 
/*  1738 */         expr2, 
/*  1739 */         op);
/*  1740 */       break;
/*       */     case 14:
/*  1743 */       if (this.optimizeStringLiterals) {
/*  1744 */         if ((expr1 instanceof StringLiteral)) {
/*  1745 */           if ((expr1.bits & 0x1FE00000) >> 21 == 0) {
/*  1746 */             if ((expr2 instanceof CharLiteral))
/*  1747 */               this.expressionStack[this.expressionPtr] = 
/*  1748 */                 ((StringLiteral)expr1).extendWith((CharLiteral)expr2);
/*  1749 */             else if ((expr2 instanceof StringLiteral))
/*  1750 */               this.expressionStack[this.expressionPtr] = 
/*  1751 */                 ((StringLiteral)expr1).extendWith((StringLiteral)expr2);
/*       */             else
/*  1753 */               this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
/*       */           }
/*       */           else
/*  1756 */             this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
/*       */         }
/*  1758 */         else if ((expr1 instanceof CombinedBinaryExpression))
/*       */         {
/*       */           CombinedBinaryExpression cursor;
/*  1765 */           if ((cursor = (CombinedBinaryExpression)expr1).arity < cursor.arityMax) {
/*  1766 */             cursor.left = new BinaryExpression(cursor);
/*  1767 */             cursor.arity += 1;
/*       */           } else {
/*  1769 */             cursor.left = new CombinedBinaryExpression(cursor);
/*  1770 */             cursor.arity = 0;
/*  1771 */             cursor.tuneArityMax();
/*       */           }
/*  1773 */           cursor.right = expr2;
/*  1774 */           cursor.sourceEnd = expr2.sourceEnd;
/*  1775 */           this.expressionStack[this.expressionPtr] = cursor;
/*       */         }
/*       */         else
/*       */         {
/*  1779 */           if ((expr1 instanceof BinaryExpression))
/*       */           {
/*  1784 */             if ((expr1.bits & 0xFC0) >> 
/*  1785 */               6 == 14) {
/*  1786 */               this.expressionStack[this.expressionPtr] = 
/*  1787 */                 new CombinedBinaryExpression(expr1, expr2, 14, 1);
/*       */ 
/*  1786 */               break;
/*       */             }
/*       */           }
/*  1789 */           this.expressionStack[this.expressionPtr] = 
/*  1790 */             new BinaryExpression(expr1, expr2, 14);
/*       */         }
/*  1792 */       } else if ((expr1 instanceof StringLiteral)) {
/*  1793 */         if (((expr2 instanceof StringLiteral)) && 
/*  1794 */           ((expr1.bits & 0x1FE00000) >> 21 == 0))
/*       */         {
/*  1796 */           this.expressionStack[this.expressionPtr] = 
/*  1797 */             ((StringLiteral)expr1).extendsWith((StringLiteral)expr2);
/*       */         }
/*       */         else
/*  1800 */           this.expressionStack[this.expressionPtr] = 
/*  1801 */             new BinaryExpression(expr1, expr2, 14);
/*       */       }
/*  1803 */       else if ((expr1 instanceof CombinedBinaryExpression))
/*       */       {
/*       */         CombinedBinaryExpression cursor;
/*  1806 */         if ((cursor = (CombinedBinaryExpression)expr1).arity < cursor.arityMax) {
/*  1807 */           cursor.left = new BinaryExpression(cursor);
/*       */ 
/*  1809 */           cursor.bits &= -534773761;
/*  1810 */           cursor.arity += 1;
/*       */         } else {
/*  1812 */           cursor.left = new CombinedBinaryExpression(cursor);
/*       */ 
/*  1814 */           cursor.bits &= -534773761;
/*  1815 */           cursor.arity = 0;
/*  1816 */           cursor.tuneArityMax();
/*       */         }
/*  1818 */         cursor.right = expr2;
/*  1819 */         cursor.sourceEnd = expr2.sourceEnd;
/*       */ 
/*  1823 */         this.expressionStack[this.expressionPtr] = cursor;
/*  1824 */       } else if (((expr1 instanceof BinaryExpression)) && 
/*  1825 */         ((expr1.bits & 0xFC0) >> 
/*  1826 */         6 == 14))
/*       */       {
/*  1828 */         this.expressionStack[this.expressionPtr] = 
/*  1829 */           new CombinedBinaryExpression(expr1, expr2, 14, 1);
/*       */       } else {
/*  1831 */         this.expressionStack[this.expressionPtr] = 
/*  1832 */           new BinaryExpression(expr1, expr2, 14);
/*       */       }
/*  1834 */       break;
/*       */     case 4:
/*  1836 */       this.intPtr -= 1;
/*  1837 */       this.expressionStack[this.expressionPtr] = 
/*  1838 */         new BinaryExpression(
/*  1839 */         expr1, 
/*  1840 */         expr2, 
/*  1841 */         op);
/*  1842 */       break;
/*       */     default:
/*  1844 */       this.expressionStack[this.expressionPtr] = 
/*  1845 */         new BinaryExpression(
/*  1846 */         expr1, 
/*  1847 */         expr2, 
/*  1848 */         op);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeBinaryExpressionWithName(int op)
/*       */   {
/*  1855 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*  1856 */     this.expressionPtr -= 1;
/*  1857 */     this.expressionLengthPtr -= 1;
/*       */ 
/*  1901 */     Expression expr1 = this.expressionStack[(this.expressionPtr + 1)];
/*  1902 */     Expression expr2 = this.expressionStack[this.expressionPtr];
/*       */ 
/*  1905 */     switch (op) {
/*       */     case 1:
/*  1907 */       this.expressionStack[this.expressionPtr] = 
/*  1908 */         new OR_OR_Expression(
/*  1909 */         expr1, 
/*  1910 */         expr2, 
/*  1911 */         op);
/*  1912 */       break;
/*       */     case 0:
/*  1914 */       this.expressionStack[this.expressionPtr] = 
/*  1915 */         new AND_AND_Expression(
/*  1916 */         expr1, 
/*  1917 */         expr2, 
/*  1918 */         op);
/*  1919 */       break;
/*       */     case 14:
/*  1922 */       if (this.optimizeStringLiterals) {
/*  1923 */         if (((expr1 instanceof StringLiteral)) && 
/*  1924 */           ((expr1.bits & 0x1FE00000) >> 21 == 0)) {
/*  1925 */           if ((expr2 instanceof CharLiteral))
/*  1926 */             this.expressionStack[this.expressionPtr] = 
/*  1927 */               ((StringLiteral)expr1).extendWith((CharLiteral)expr2);
/*  1928 */           else if ((expr2 instanceof StringLiteral))
/*  1929 */             this.expressionStack[this.expressionPtr] = 
/*  1930 */               ((StringLiteral)expr1).extendWith((StringLiteral)expr2);
/*       */           else
/*  1932 */             this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
/*       */         }
/*       */         else
/*  1935 */           this.expressionStack[this.expressionPtr] = new BinaryExpression(expr1, expr2, 14);
/*       */       }
/*  1937 */       else if ((expr1 instanceof StringLiteral)) {
/*  1938 */         if (((expr2 instanceof StringLiteral)) && 
/*  1939 */           ((expr1.bits & 0x1FE00000) >> 21 == 0))
/*       */         {
/*  1941 */           this.expressionStack[this.expressionPtr] = 
/*  1942 */             ((StringLiteral)expr1).extendsWith((StringLiteral)expr2);
/*       */         }
/*  1944 */         else this.expressionStack[this.expressionPtr] = 
/*  1945 */             new BinaryExpression(
/*  1946 */             expr1, 
/*  1947 */             expr2, 
/*  1948 */             op);
/*       */       }
/*       */       else {
/*  1951 */         this.expressionStack[this.expressionPtr] = 
/*  1952 */           new BinaryExpression(
/*  1953 */           expr1, 
/*  1954 */           expr2, 
/*  1955 */           op);
/*       */       }
/*  1957 */       break;
/*       */     case 4:
/*  1959 */       this.intPtr -= 1;
/*  1960 */       this.expressionStack[this.expressionPtr] = 
/*  1961 */         new BinaryExpression(
/*  1962 */         expr1, 
/*  1963 */         expr2, 
/*  1964 */         op);
/*  1965 */       break;
/*       */     default:
/*  1967 */       this.expressionStack[this.expressionPtr] = 
/*  1968 */         new BinaryExpression(
/*  1969 */         expr1, 
/*  1970 */         expr2, 
/*  1971 */         op);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeBlock()
/*       */   {
/*  1978 */     int statementsLength = this.astLengthStack[(this.astLengthPtr--)];
/*       */     Block block;
/*  1980 */     if (statementsLength == 0) {
/*  1981 */       Block block = new Block(0);
/*  1982 */       block.sourceStart = this.intStack[(this.intPtr--)];
/*  1983 */       block.sourceEnd = this.endStatementPosition;
/*       */ 
/*  1985 */       if (!containsComment(block.sourceStart, block.sourceEnd)) {
/*  1986 */         block.bits |= 8;
/*       */       }
/*  1988 */       this.realBlockPtr -= 1;
/*       */     } else {
/*  1990 */       block = new Block(this.realBlockStack[(this.realBlockPtr--)]);
/*  1991 */       this.astPtr -= statementsLength;
/*  1992 */       System.arraycopy(
/*  1993 */         this.astStack, 
/*  1994 */         this.astPtr + 1, 
/*  1995 */         block.statements = new Statement[statementsLength], 
/*  1996 */         0, 
/*  1997 */         statementsLength);
/*  1998 */       block.sourceStart = this.intStack[(this.intPtr--)];
/*  1999 */       block.sourceEnd = this.endStatementPosition;
/*       */     }
/*  2001 */     pushOnAstStack(block);
/*       */   }
/*       */ 
/*       */   protected void consumeBlockStatements() {
/*  2005 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeCaseLabel() {
/*  2009 */     this.expressionLengthPtr -= 1;
/*  2010 */     Expression expression = this.expressionStack[(this.expressionPtr--)];
/*  2011 */     CaseStatement caseStatement = new CaseStatement(expression, expression.sourceEnd, this.intStack[(this.intPtr--)]);
/*       */ 
/*  2013 */     if (hasLeadingTagComment(FALL_THROUGH_TAG, caseStatement.sourceStart)) {
/*  2014 */       caseStatement.bits |= 536870912;
/*       */     }
/*  2016 */     pushOnAstStack(caseStatement);
/*       */   }
/*       */ 
/*       */   protected void consumeCastExpressionLL1()
/*       */   {
/*  2025 */     this.expressionPtr -= 1;
/*       */     Expression exp;
/*       */      tmp51_48 = new CastExpression(
/*  2028 */       exp = this.expressionStack[(this.expressionPtr + 1)], 
/*  2029 */       getTypeReference(this.expressionStack[this.expressionPtr]));
/*       */ 
/*  2027 */     Expression cast = tmp51_48;
/*       */ 
/*  2026 */     this.expressionStack[this.expressionPtr] = 
/*  2027 */       tmp51_48;
/*       */ 
/*  2030 */     this.expressionLengthPtr -= 1;
/*  2031 */     updateSourcePosition(cast);
/*  2032 */     cast.sourceEnd = exp.sourceEnd;
/*       */   }
/*       */ 
/*       */   protected void consumeCastExpressionWithGenericsArray()
/*       */   {
/*  2038 */     int end = this.intStack[(this.intPtr--)];
/*       */ 
/*  2040 */     int dim = this.intStack[(this.intPtr--)];
/*  2041 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*       */     Expression exp;
/*       */     Expression castType;
/*       */      tmp83_80 = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(dim)); Expression cast = tmp83_80; this.expressionStack[this.expressionPtr] = tmp83_80;
/*  2044 */     this.intPtr -= 1;
/*  2045 */     castType.sourceEnd = (end - 1);
/*  2046 */     castType.sourceStart = ((cast.sourceStart = this.intStack[(this.intPtr--)]) + 1);
/*  2047 */     cast.sourceEnd = exp.sourceEnd;
/*       */   }
/*       */ 
/*       */   protected void consumeCastExpressionWithNameArray()
/*       */   {
/*  2053 */     int end = this.intStack[(this.intPtr--)];
/*       */ 
/*  2056 */     pushOnGenericsLengthStack(0);
/*  2057 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*       */     Expression exp;
/*       */     Expression castType;
/*       */      tmp84_81 = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(this.intStack[(this.intPtr--)])); Expression cast = tmp84_81; this.expressionStack[this.expressionPtr] = tmp84_81;
/*  2060 */     castType.sourceEnd = (end - 1);
/*  2061 */     castType.sourceStart = ((cast.sourceStart = this.intStack[(this.intPtr--)]) + 1);
/*  2062 */     cast.sourceEnd = exp.sourceEnd;
/*       */   }
/*       */ 
/*       */   protected void consumeCastExpressionWithPrimitiveType()
/*       */   {
/*  2072 */     int end = this.intStack[(this.intPtr--)];
/*       */     Expression exp;
/*       */     Expression castType;
/*       */      tmp66_63 = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = getTypeReference(this.intStack[(this.intPtr--)])); Expression cast = tmp66_63; this.expressionStack[this.expressionPtr] = tmp66_63;
/*  2074 */     castType.sourceEnd = (end - 1);
/*  2075 */     castType.sourceStart = ((cast.sourceStart = this.intStack[(this.intPtr--)]) + 1);
/*  2076 */     cast.sourceEnd = exp.sourceEnd;
/*       */   }
/*       */ 
/*       */   protected void consumeCastExpressionWithQualifiedGenericsArray()
/*       */   {
/*  2081 */     int end = this.intStack[(this.intPtr--)];
/*       */ 
/*  2083 */     int dim = this.intStack[(this.intPtr--)];
/*  2084 */     TypeReference rightSide = getTypeReference(0);
/*       */ 
/*  2086 */     ParameterizedQualifiedTypeReference qualifiedParameterizedTypeReference = computeQualifiedGenericsFromRightSide(rightSide, dim);
/*  2087 */     this.intPtr -= 1;
/*       */     Expression exp;
/*       */     Expression castType;
/*       */      tmp93_90 = new CastExpression(exp = this.expressionStack[this.expressionPtr], castType = qualifiedParameterizedTypeReference); Expression cast = tmp93_90; this.expressionStack[this.expressionPtr] = tmp93_90;
/*  2089 */     castType.sourceEnd = (end - 1);
/*  2090 */     castType.sourceStart = ((cast.sourceStart = this.intStack[(this.intPtr--)]) + 1);
/*  2091 */     cast.sourceEnd = exp.sourceEnd;
/*       */   }
/*       */ 
/*       */   protected void consumeCatches() {
/*  2095 */     optimizedConcatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeCatchHeader()
/*       */   {
/*  2100 */     if (this.currentElement == null) {
/*  2101 */       return;
/*       */     }
/*       */ 
/*  2104 */     if (!(this.currentElement instanceof RecoveredBlock)) {
/*  2105 */       if (!(this.currentElement instanceof RecoveredMethod)) {
/*  2106 */         return;
/*       */       }
/*  2108 */       RecoveredMethod rMethod = (RecoveredMethod)this.currentElement;
/*  2109 */       if ((rMethod.methodBody != null) || (rMethod.bracketBalance <= 0)) {
/*  2110 */         return;
/*       */       }
/*       */     }
/*       */ 
/*  2114 */     Argument arg = (Argument)this.astStack[(this.astPtr--)];
/*       */ 
/*  2116 */     LocalDeclaration localDeclaration = new LocalDeclaration(arg.name, arg.sourceStart, arg.sourceEnd);
/*  2117 */     localDeclaration.type = arg.type;
/*  2118 */     localDeclaration.declarationSourceStart = arg.declarationSourceStart;
/*  2119 */     localDeclaration.declarationSourceEnd = arg.declarationSourceEnd;
/*       */ 
/*  2121 */     this.currentElement = this.currentElement.add(localDeclaration, 0);
/*  2122 */     this.lastCheckPoint = this.scanner.startPosition;
/*  2123 */     this.restartRecovery = true;
/*  2124 */     this.lastIgnoredToken = -1;
/*       */   }
/*       */ 
/*       */   protected void consumeClassBodyDeclaration()
/*       */   {
/*  2130 */     this.nestedMethod[this.nestedType] -= 1;
/*  2131 */     Block block = (Block)this.astStack[(this.astPtr--)];
/*  2132 */     this.astLengthPtr -= 1;
/*  2133 */     if (this.diet) block.bits &= -9;
/*  2134 */     Initializer initializer = (Initializer)this.astStack[this.astPtr];
/*  2135 */     initializer.declarationSourceStart = (initializer.sourceStart = block.sourceStart);
/*  2136 */     initializer.block = block;
/*  2137 */     this.intPtr -= 1;
/*  2138 */     initializer.bodyStart = this.intStack[(this.intPtr--)];
/*  2139 */     this.realBlockPtr -= 1;
/*  2140 */     int javadocCommentStart = this.intStack[(this.intPtr--)];
/*  2141 */     if (javadocCommentStart != -1) {
/*  2142 */       initializer.declarationSourceStart = javadocCommentStart;
/*  2143 */       initializer.javadoc = this.javadoc;
/*  2144 */       this.javadoc = null;
/*       */     }
/*  2146 */     initializer.bodyEnd = this.endPosition;
/*  2147 */     initializer.sourceEnd = this.endStatementPosition;
/*  2148 */     initializer.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeClassBodyDeclarations() {
/*  2152 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeClassBodyDeclarationsopt() {
/*  2156 */     this.nestedType -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeClassBodyopt() {
/*  2160 */     pushOnAstStack(null);
/*  2161 */     this.endPosition = this.rParenPos;
/*       */   }
/*       */ 
/*       */   protected void consumeClassDeclaration()
/*       */   {
/*       */     int length;
/*  2167 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)
/*       */     {
/*  2170 */       dispatchDeclarationInto(length);
/*       */     }
/*       */ 
/*  2173 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  2176 */     boolean hasConstructor = typeDecl.checkConstructors(this);
/*       */ 
/*  2179 */     if (!hasConstructor) {
/*  2180 */       switch (TypeDeclaration.kind(typeDecl.modifiers)) {
/*       */       case 1:
/*       */       case 3:
/*  2183 */         boolean insideFieldInitializer = false;
/*  2184 */         if (this.diet) {
/*  2185 */           for (int i = this.nestedType; i > 0; i--) {
/*  2186 */             if (this.variablesCounter[i] > 0) {
/*  2187 */               insideFieldInitializer = true;
/*  2188 */               break;
/*       */             }
/*       */           }
/*       */         }
/*  2192 */         typeDecl.createDefaultConstructor((!this.diet) || (insideFieldInitializer), true);
/*       */       case 2:
/*       */       }
/*       */     }
/*  2196 */     if (this.scanner.containsAssertKeyword) {
/*  2197 */       typeDecl.bits |= 1;
/*       */     }
/*  2199 */     typeDecl.addClinit();
/*  2200 */     typeDecl.bodyEnd = this.endStatementPosition;
/*  2201 */     if ((length == 0) && (!containsComment(typeDecl.bodyStart, typeDecl.bodyEnd))) {
/*  2202 */       typeDecl.bits |= 8;
/*       */     }
/*       */ 
/*  2205 */     typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeClassHeader()
/*       */   {
/*  2210 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  2211 */     if (this.currentToken == 69) {
/*  2212 */       typeDecl.bodyStart = this.scanner.currentPosition;
/*       */     }
/*  2214 */     if (this.currentElement != null) {
/*  2215 */       this.restartRecovery = true;
/*       */     }
/*       */ 
/*  2218 */     this.scanner.commentPtr = -1;
/*       */   }
/*       */ 
/*       */   protected void consumeClassHeaderExtends()
/*       */   {
/*  2223 */     TypeReference superClass = getTypeReference(0);
/*       */ 
/*  2225 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  2226 */     typeDecl.superclass = superClass;
/*  2227 */     superClass.bits |= 16;
/*  2228 */     typeDecl.bodyStart = (typeDecl.superclass.sourceEnd + 1);
/*       */ 
/*  2230 */     if (this.currentElement != null)
/*  2231 */       this.lastCheckPoint = typeDecl.bodyStart;
/*       */   }
/*       */ 
/*       */   protected void consumeClassHeaderImplements()
/*       */   {
/*  2236 */     int length = this.astLengthStack[(this.astLengthPtr--)];
/*       */ 
/*  2238 */     this.astPtr -= length;
/*       */ 
/*  2240 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  2241 */     System.arraycopy(
/*  2242 */       this.astStack, 
/*  2243 */       this.astPtr + 1, 
/*  2244 */       typeDecl.superInterfaces = new TypeReference[length], 
/*  2245 */       0, 
/*  2246 */       length);
/*  2247 */     int i = 0; for (int max = typeDecl.superInterfaces.length; i < max; i++) {
/*  2248 */       typeDecl.superInterfaces[i].bits |= 16;
/*       */     }
/*  2250 */     typeDecl.bodyStart = (typeDecl.superInterfaces[(length - 1)].sourceEnd + 1);
/*  2251 */     this.listLength = 0;
/*       */ 
/*  2253 */     if (this.currentElement != null)
/*  2254 */       this.lastCheckPoint = typeDecl.bodyStart;
/*       */   }
/*       */ 
/*       */   protected void consumeClassHeaderName1()
/*       */   {
/*  2259 */     TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
/*  2260 */     if (this.nestedMethod[this.nestedType] == 0) {
/*  2261 */       if (this.nestedType != 0)
/*  2262 */         typeDecl.bits |= 1024;
/*       */     }
/*       */     else
/*       */     {
/*  2266 */       typeDecl.bits |= 256;
/*  2267 */       markEnclosingMemberWithLocalType();
/*  2268 */       blockReal();
/*       */     }
/*       */ 
/*  2272 */     long pos = this.identifierPositionStack[this.identifierPtr];
/*  2273 */     typeDecl.sourceEnd = (int)pos;
/*  2274 */     typeDecl.sourceStart = (int)(pos >>> 32);
/*  2275 */     typeDecl.name = this.identifierStack[(this.identifierPtr--)];
/*  2276 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  2282 */     typeDecl.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  2283 */     this.intPtr -= 1;
/*       */ 
/*  2285 */     typeDecl.modifiersSourceStart = this.intStack[(this.intPtr--)];
/*  2286 */     typeDecl.modifiers = this.intStack[(this.intPtr--)];
/*  2287 */     if (typeDecl.modifiersSourceStart >= 0) {
/*  2288 */       typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
/*       */     }
/*       */ 
/*  2292 */     if (((typeDecl.bits & 0x400) == 0) && ((typeDecl.bits & 0x100) == 0) && 
/*  2293 */       (this.compilationUnit != null) && (!CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())))
/*  2294 */       typeDecl.bits |= 4096;
/*       */     int length;
/*  2300 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  2301 */       System.arraycopy(
/*  2302 */         this.expressionStack, 
/*  2303 */         this.expressionPtr -= length + 1, 
/*  2304 */         typeDecl.annotations = new Annotation[length], 
/*  2305 */         0, 
/*  2306 */         length);
/*       */     }
/*  2308 */     typeDecl.bodyStart = (typeDecl.sourceEnd + 1);
/*  2309 */     pushOnAstStack(typeDecl);
/*       */ 
/*  2311 */     this.listLength = 0;
/*       */ 
/*  2313 */     if (this.currentElement != null) {
/*  2314 */       this.lastCheckPoint = typeDecl.bodyStart;
/*  2315 */       this.currentElement = this.currentElement.add(typeDecl, 0);
/*  2316 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */ 
/*  2319 */     typeDecl.javadoc = this.javadoc;
/*  2320 */     this.javadoc = null;
/*       */   }
/*       */ 
/*       */   protected void consumeClassInstanceCreationExpression() {
/*  2324 */     classInstanceCreation(false);
/*       */   }
/*       */ 
/*       */   protected void consumeClassInstanceCreationExpressionName() {
/*  2328 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*       */   }
/*       */ 
/*       */   protected void consumeClassInstanceCreationExpressionQualified()
/*       */   {
/*  2333 */     classInstanceCreation(true);
/*       */ 
/*  2335 */     QualifiedAllocationExpression qae = 
/*  2336 */       (QualifiedAllocationExpression)this.expressionStack[this.expressionPtr];
/*       */ 
/*  2338 */     if (qae.anonymousType == null) {
/*  2339 */       this.expressionLengthPtr -= 1;
/*  2340 */       this.expressionPtr -= 1;
/*  2341 */       qae.enclosingInstance = this.expressionStack[this.expressionPtr];
/*  2342 */       this.expressionStack[this.expressionPtr] = qae;
/*       */     }
/*  2344 */     qae.sourceStart = qae.enclosingInstance.sourceStart;
/*       */   }
/*       */ 
/*       */   protected void consumeClassInstanceCreationExpressionQualifiedWithTypeArguments()
/*       */   {
/*       */     int length;
/*  2352 */     if (((length = this.astLengthStack[(this.astLengthPtr--)]) == 1) && (this.astStack[this.astPtr] == null))
/*       */     {
/*  2354 */       this.astPtr -= 1;
/*  2355 */       QualifiedAllocationExpression alloc = new QualifiedAllocationExpression();
/*  2356 */       alloc.sourceEnd = this.endPosition;
/*       */ 
/*  2358 */       if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  2359 */         this.expressionPtr -= length;
/*  2360 */         System.arraycopy(
/*  2361 */           this.expressionStack, 
/*  2362 */           this.expressionPtr + 1, 
/*  2363 */           alloc.arguments = new Expression[length], 
/*  2364 */           0, 
/*  2365 */           length);
/*       */       }
/*  2367 */       alloc.type = getTypeReference(0);
/*       */ 
/*  2369 */       length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  2370 */       this.genericsPtr -= length;
/*  2371 */       System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
/*  2372 */       this.intPtr -= 1;
/*       */ 
/*  2376 */       alloc.sourceStart = this.intStack[(this.intPtr--)];
/*  2377 */       pushOnExpressionStack(alloc);
/*       */     } else {
/*  2379 */       dispatchDeclarationInto(length);
/*  2380 */       TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
/*  2381 */       anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
/*  2382 */       anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
/*  2383 */       if ((length == 0) && (!containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd))) {
/*  2384 */         anonymousTypeDeclaration.bits |= 8;
/*       */       }
/*  2386 */       this.astPtr -= 1;
/*  2387 */       this.astLengthPtr -= 1;
/*       */ 
/*  2389 */       QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
/*  2390 */       if (allocationExpression != null) {
/*  2391 */         allocationExpression.sourceEnd = this.endStatementPosition;
/*       */ 
/*  2393 */         length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  2394 */         this.genericsPtr -= length;
/*  2395 */         System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
/*  2396 */         allocationExpression.sourceStart = this.intStack[(this.intPtr--)];
/*       */       }
/*       */     }
/*       */ 
/*  2400 */     QualifiedAllocationExpression qae = 
/*  2401 */       (QualifiedAllocationExpression)this.expressionStack[this.expressionPtr];
/*       */ 
/*  2403 */     if (qae.anonymousType == null) {
/*  2404 */       this.expressionLengthPtr -= 1;
/*  2405 */       this.expressionPtr -= 1;
/*  2406 */       qae.enclosingInstance = this.expressionStack[this.expressionPtr];
/*  2407 */       this.expressionStack[this.expressionPtr] = qae;
/*       */     }
/*  2409 */     qae.sourceStart = qae.enclosingInstance.sourceStart;
/*       */   }
/*       */ 
/*       */   protected void consumeClassInstanceCreationExpressionWithTypeArguments()
/*       */   {
/*       */     int length;
/*  2415 */     if (((length = this.astLengthStack[(this.astLengthPtr--)]) == 1) && 
/*  2416 */       (this.astStack[this.astPtr] == null))
/*       */     {
/*  2418 */       this.astPtr -= 1;
/*  2419 */       AllocationExpression alloc = new AllocationExpression();
/*  2420 */       alloc.sourceEnd = this.endPosition;
/*       */ 
/*  2422 */       if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  2423 */         this.expressionPtr -= length;
/*  2424 */         System.arraycopy(
/*  2425 */           this.expressionStack, 
/*  2426 */           this.expressionPtr + 1, 
/*  2427 */           alloc.arguments = new Expression[length], 
/*  2428 */           0, 
/*  2429 */           length);
/*       */       }
/*  2431 */       alloc.type = getTypeReference(0);
/*       */ 
/*  2433 */       length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  2434 */       this.genericsPtr -= length;
/*  2435 */       System.arraycopy(this.genericsStack, this.genericsPtr + 1, alloc.typeArguments = new TypeReference[length], 0, length);
/*  2436 */       this.intPtr -= 1;
/*       */ 
/*  2440 */       alloc.sourceStart = this.intStack[(this.intPtr--)];
/*  2441 */       pushOnExpressionStack(alloc);
/*       */     } else {
/*  2443 */       dispatchDeclarationInto(length);
/*  2444 */       TypeDeclaration anonymousTypeDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
/*  2445 */       anonymousTypeDeclaration.declarationSourceEnd = this.endStatementPosition;
/*  2446 */       anonymousTypeDeclaration.bodyEnd = this.endStatementPosition;
/*  2447 */       if ((length == 0) && (!containsComment(anonymousTypeDeclaration.bodyStart, anonymousTypeDeclaration.bodyEnd))) {
/*  2448 */         anonymousTypeDeclaration.bits |= 8;
/*       */       }
/*  2450 */       this.astPtr -= 1;
/*  2451 */       this.astLengthPtr -= 1;
/*       */ 
/*  2453 */       QualifiedAllocationExpression allocationExpression = anonymousTypeDeclaration.allocation;
/*  2454 */       if (allocationExpression != null) {
/*  2455 */         allocationExpression.sourceEnd = this.endStatementPosition;
/*       */ 
/*  2457 */         length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  2458 */         this.genericsPtr -= length;
/*  2459 */         System.arraycopy(this.genericsStack, this.genericsPtr + 1, allocationExpression.typeArguments = new TypeReference[length], 0, length);
/*  2460 */         allocationExpression.sourceStart = this.intStack[(this.intPtr--)];
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeClassOrInterface() {
/*  2465 */     this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] += this.identifierLengthStack[this.identifierLengthPtr];
/*  2466 */     pushOnGenericsLengthStack(0);
/*       */   }
/*       */   protected void consumeClassOrInterfaceName() {
/*  2469 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*  2470 */     pushOnGenericsLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeClassTypeElt() {
/*  2474 */     pushOnAstStack(getTypeReference(0));
/*       */ 
/*  2477 */     this.listLength += 1;
/*       */   }
/*       */ 
/*       */   protected void consumeClassTypeList() {
/*  2481 */     optimizedConcatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeCompilationUnit()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeConditionalExpression(int op)
/*       */   {
/*  2490 */     this.intPtr -= 2;
/*  2491 */     this.expressionPtr -= 2;
/*  2492 */     this.expressionLengthPtr -= 2;
/*  2493 */     this.expressionStack[this.expressionPtr] = 
/*  2494 */       new ConditionalExpression(
/*  2495 */       this.expressionStack[this.expressionPtr], 
/*  2496 */       this.expressionStack[(this.expressionPtr + 1)], 
/*  2497 */       this.expressionStack[(this.expressionPtr + 2)]);
/*       */   }
/*       */ 
/*       */   protected void consumeConditionalExpressionWithName(int op)
/*       */   {
/*  2504 */     this.intPtr -= 2;
/*  2505 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*  2506 */     this.expressionPtr -= 2;
/*  2507 */     this.expressionLengthPtr -= 2;
/*  2508 */     this.expressionStack[this.expressionPtr] = 
/*  2509 */       new ConditionalExpression(
/*  2510 */       this.expressionStack[(this.expressionPtr + 2)], 
/*  2511 */       this.expressionStack[this.expressionPtr], 
/*  2512 */       this.expressionStack[(this.expressionPtr + 1)]);
/*       */   }
/*       */ 
/*       */   protected void consumeConstructorBlockStatements() {
/*  2516 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeConstructorBody()
/*       */   {
/*  2521 */     this.nestedMethod[this.nestedType] -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeConstructorDeclaration()
/*       */   {
/*  2539 */     this.intPtr -= 1;
/*  2540 */     this.intPtr -= 1;
/*       */ 
/*  2543 */     this.realBlockPtr -= 1;
/*  2544 */     ExplicitConstructorCall constructorCall = null;
/*  2545 */     Statement[] statements = (Statement[])null;
/*       */     int length;
/*  2546 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0) {
/*  2547 */       this.astPtr -= length;
/*  2548 */       if (!this.options.ignoreMethodBodies)
/*  2549 */         if ((this.astStack[(this.astPtr + 1)] instanceof ExplicitConstructorCall))
/*       */         {
/*  2551 */           System.arraycopy(
/*  2552 */             this.astStack, 
/*  2553 */             this.astPtr + 2, 
/*  2554 */             statements = new Statement[length - 1], 
/*  2555 */             0, 
/*  2556 */             length - 1);
/*  2557 */           constructorCall = (ExplicitConstructorCall)this.astStack[(this.astPtr + 1)];
/*       */         } else {
/*  2559 */           System.arraycopy(
/*  2560 */             this.astStack, 
/*  2561 */             this.astPtr + 1, 
/*  2562 */             statements = new Statement[length], 
/*  2563 */             0, 
/*  2564 */             length);
/*  2565 */           constructorCall = SuperReference.implicitSuperConstructorCall();
/*       */         }
/*       */     }
/*       */     else {
/*  2569 */       boolean insideFieldInitializer = false;
/*  2570 */       if (this.diet) {
/*  2571 */         for (int i = this.nestedType; i > 0; i--) {
/*  2572 */           if (this.variablesCounter[i] > 0) {
/*  2573 */             insideFieldInitializer = true;
/*  2574 */             break;
/*       */           }
/*       */         }
/*       */       }
/*       */ 
/*  2579 */       if ((!this.diet) || (insideFieldInitializer))
/*       */       {
/*  2581 */         constructorCall = SuperReference.implicitSuperConstructorCall();
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  2586 */     ConstructorDeclaration cd = (ConstructorDeclaration)this.astStack[this.astPtr];
/*  2587 */     cd.constructorCall = constructorCall;
/*  2588 */     cd.statements = statements;
/*       */ 
/*  2591 */     if ((constructorCall != null) && (cd.constructorCall.sourceEnd == 0)) {
/*  2592 */       cd.constructorCall.sourceEnd = cd.sourceEnd;
/*  2593 */       cd.constructorCall.sourceStart = cd.sourceStart;
/*       */     }
/*       */ 
/*  2596 */     if (((!this.diet) || (this.dietInt != 0)) && 
/*  2597 */       (statements == null) && 
/*  2598 */       ((constructorCall == null) || (constructorCall.isImplicitSuper())) && 
/*  2599 */       (!containsComment(cd.bodyStart, this.endPosition))) {
/*  2600 */       cd.bits |= 8;
/*       */     }
/*       */ 
/*  2606 */     cd.bodyEnd = this.endPosition;
/*  2607 */     cd.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeConstructorHeader()
/*       */   {
/*  2612 */     AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  2614 */     if (this.currentToken == 69) {
/*  2615 */       method.bodyStart = this.scanner.currentPosition;
/*       */     }
/*       */ 
/*  2618 */     if (this.currentElement != null) {
/*  2619 */       if (this.currentToken == 27) {
/*  2620 */         method.modifiers |= 16777216;
/*  2621 */         method.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*  2622 */         method.bodyEnd = (this.scanner.currentPosition - 1);
/*  2623 */         if ((this.currentElement.parseTree() == method) && (this.currentElement.parent != null)) {
/*  2624 */           this.currentElement = this.currentElement.parent;
/*       */         }
/*       */       }
/*  2627 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeConstructorHeaderName()
/*       */   {
/*  2633 */     if ((this.currentElement != null) && 
/*  2634 */       (this.lastIgnoredToken == 43)) {
/*  2635 */       this.lastCheckPoint = this.scanner.startPosition;
/*  2636 */       this.restartRecovery = true;
/*  2637 */       return;
/*       */     }
/*       */ 
/*  2642 */     ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
/*       */ 
/*  2645 */     cd.selector = this.identifierStack[this.identifierPtr];
/*  2646 */     long selectorSource = this.identifierPositionStack[(this.identifierPtr--)];
/*  2647 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  2650 */     cd.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  2651 */     cd.modifiers = this.intStack[(this.intPtr--)];
/*       */     int length;
/*  2654 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  2655 */       System.arraycopy(
/*  2656 */         this.expressionStack, 
/*  2657 */         this.expressionPtr -= length + 1, 
/*  2658 */         cd.annotations = new Annotation[length], 
/*  2659 */         0, 
/*  2660 */         length);
/*       */     }
/*       */ 
/*  2663 */     cd.javadoc = this.javadoc;
/*  2664 */     this.javadoc = null;
/*       */ 
/*  2667 */     cd.sourceStart = (int)(selectorSource >>> 32);
/*  2668 */     pushOnAstStack(cd);
/*  2669 */     cd.sourceEnd = this.lParenPos;
/*  2670 */     cd.bodyStart = (this.lParenPos + 1);
/*  2671 */     this.listLength = 0;
/*       */ 
/*  2674 */     if (this.currentElement != null) {
/*  2675 */       this.lastCheckPoint = cd.bodyStart;
/*  2676 */       if ((((this.currentElement instanceof RecoveredType)) && (this.lastIgnoredToken != 3)) || 
/*  2677 */         (cd.modifiers != 0)) {
/*  2678 */         this.currentElement = this.currentElement.add(cd, 0);
/*  2679 */         this.lastIgnoredToken = -1;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeConstructorHeaderNameWithTypeParameters()
/*       */   {
/*  2686 */     if ((this.currentElement != null) && 
/*  2687 */       (this.lastIgnoredToken == 43)) {
/*  2688 */       this.lastCheckPoint = this.scanner.startPosition;
/*  2689 */       this.restartRecovery = true;
/*  2690 */       return;
/*       */     }
/*       */ 
/*  2695 */     ConstructorDeclaration cd = new ConstructorDeclaration(this.compilationUnit.compilationResult);
/*       */ 
/*  2698 */     cd.selector = this.identifierStack[this.identifierPtr];
/*  2699 */     long selectorSource = this.identifierPositionStack[(this.identifierPtr--)];
/*  2700 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  2703 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  2704 */     this.genericsPtr -= length;
/*  2705 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, cd.typeParameters = new TypeParameter[length], 0, length);
/*       */ 
/*  2708 */     cd.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  2709 */     cd.modifiers = this.intStack[(this.intPtr--)];
/*       */ 
/*  2711 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  2712 */       System.arraycopy(
/*  2713 */         this.expressionStack, 
/*  2714 */         this.expressionPtr -= length + 1, 
/*  2715 */         cd.annotations = new Annotation[length], 
/*  2716 */         0, 
/*  2717 */         length);
/*       */     }
/*       */ 
/*  2720 */     cd.javadoc = this.javadoc;
/*  2721 */     this.javadoc = null;
/*       */ 
/*  2724 */     cd.sourceStart = (int)(selectorSource >>> 32);
/*  2725 */     pushOnAstStack(cd);
/*  2726 */     cd.sourceEnd = this.lParenPos;
/*  2727 */     cd.bodyStart = (this.lParenPos + 1);
/*  2728 */     this.listLength = 0;
/*       */ 
/*  2731 */     if (this.currentElement != null) {
/*  2732 */       this.lastCheckPoint = cd.bodyStart;
/*  2733 */       if ((((this.currentElement instanceof RecoveredType)) && (this.lastIgnoredToken != 3)) || 
/*  2734 */         (cd.modifiers != 0)) {
/*  2735 */         this.currentElement = this.currentElement.add(cd, 0);
/*  2736 */         this.lastIgnoredToken = -1;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeCreateInitializer() {
/*  2741 */     pushOnAstStack(new Initializer(null, 0));
/*       */   }
/*       */ 
/*       */   protected void consumeDefaultLabel() {
/*  2745 */     CaseStatement defaultStatement = new CaseStatement(null, this.intStack[(this.intPtr--)], this.intStack[(this.intPtr--)]);
/*       */ 
/*  2747 */     if (hasLeadingTagComment(FALL_THROUGH_TAG, defaultStatement.sourceStart)) {
/*  2748 */       defaultStatement.bits |= 536870912;
/*       */     }
/*  2750 */     pushOnAstStack(defaultStatement);
/*       */   }
/*       */   protected void consumeDefaultModifiers() {
/*  2753 */     checkComment();
/*  2754 */     pushOnIntStack(this.modifiers);
/*  2755 */     pushOnIntStack(
/*  2756 */       this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
/*  2757 */     resetModifiers();
/*  2758 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeDiet() {
/*  2762 */     checkComment();
/*  2763 */     pushOnIntStack(this.modifiersSourceStart);
/*  2764 */     resetModifiers();
/*  2765 */     jumpOverMethodBody();
/*       */   }
/*       */ 
/*       */   protected void consumeDims() {
/*  2769 */     pushOnIntStack(this.dimensions);
/*  2770 */     this.dimensions = 0;
/*       */   }
/*       */ 
/*       */   protected void consumeDimWithOrWithOutExpr() {
/*  2774 */     pushOnExpressionStack(null);
/*       */ 
/*  2776 */     if ((this.currentElement != null) && (this.currentToken == 69)) {
/*  2777 */       this.ignoreNextOpeningBrace = true;
/*  2778 */       this.currentElement.bracketBalance += 1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeDimWithOrWithOutExprs() {
/*  2783 */     concatExpressionLists();
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyAnnotationTypeMemberDeclarationsopt() {
/*  2787 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyArgumentListopt() {
/*  2791 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyArguments() {
/*  2795 */     FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
/*  2796 */     pushOnIntStack(fieldDeclaration.sourceEnd);
/*  2797 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyArrayInitializer() {
/*  2801 */     arrayInitializer(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyArrayInitializeropt() {
/*  2805 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyBlockStatementsopt() {
/*  2809 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyCatchesopt() {
/*  2813 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyClassBodyDeclarationsopt() {
/*  2817 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyDimsopt() {
/*  2821 */     pushOnIntStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyEnumDeclarations() {
/*  2825 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyExpression() {
/*  2829 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyForInitopt() {
/*  2833 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyForUpdateopt() {
/*  2837 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyInterfaceMemberDeclarationsopt() {
/*  2841 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyInternalCompilationUnit()
/*       */   {
/*  2846 */     if (this.compilationUnit.isPackageInfo()) {
/*  2847 */       this.compilationUnit.types = new TypeDeclaration[1];
/*  2848 */       this.compilationUnit.createPackageInfoType();
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyMemberValueArrayInitializer()
/*       */   {
/*  2854 */     arrayInitializer(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyMemberValuePairsopt() {
/*  2858 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyMethodHeaderDefaultValue() {
/*  2862 */     AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
/*  2863 */     if (method.isAnnotationMethod()) {
/*  2864 */       pushOnExpressionStackLengthStack(0);
/*       */     }
/*  2866 */     this.recordStringLiterals = true;
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyStatement() {
/*  2870 */     char[] source = this.scanner.source;
/*  2871 */     if (source[this.endStatementPosition] == ';') {
/*  2872 */       pushOnAstStack(new EmptyStatement(this.endStatementPosition, this.endStatementPosition));
/*       */     } else {
/*  2874 */       if (source.length > 5) {
/*  2875 */         int c1 = 0; int c2 = 0; int c3 = 0; int c4 = 0;
/*  2876 */         int pos = this.endStatementPosition - 4;
/*  2877 */         while (source[pos] == 'u') {
/*  2878 */           pos--;
/*       */         }
/*  2880 */         if ((source[pos] == '\\') && 
/*  2881 */           ((c1 = ScannerHelper.getNumericValue(source[(this.endStatementPosition - 3)])) <= 15) && 
/*  2882 */           (c1 >= 0) && 
/*  2883 */           ((c2 = ScannerHelper.getNumericValue(source[(this.endStatementPosition - 2)])) <= 15) && 
/*  2884 */           (c2 >= 0) && 
/*  2885 */           ((c3 = ScannerHelper.getNumericValue(source[(this.endStatementPosition - 1)])) <= 15) && 
/*  2886 */           (c3 >= 0) && 
/*  2887 */           ((c4 = ScannerHelper.getNumericValue(source[this.endStatementPosition])) <= 15) && 
/*  2888 */           (c4 >= 0) && 
/*  2889 */           ((char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4) == ';'))
/*       */         {
/*  2891 */           pushOnAstStack(new EmptyStatement(pos, this.endStatementPosition));
/*  2892 */           return;
/*       */         }
/*       */       }
/*  2895 */       pushOnAstStack(new EmptyStatement(this.endPosition + 1, this.endStatementPosition));
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeEmptySwitchBlock() {
/*  2900 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeEmptyTypeDeclaration()
/*       */   {
/*  2906 */     pushOnAstLengthStack(0);
/*  2907 */     if (!this.statementRecoveryActivated) problemReporter().superfluousSemicolon(this.endPosition + 1, this.endStatementPosition);
/*  2908 */     flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeEnhancedForStatement()
/*       */   {
/*  2915 */     this.astLengthPtr -= 1;
/*  2916 */     Statement statement = (Statement)this.astStack[(this.astPtr--)];
/*       */ 
/*  2919 */     ForeachStatement foreachStatement = (ForeachStatement)this.astStack[this.astPtr];
/*  2920 */     foreachStatement.action = statement;
/*       */ 
/*  2922 */     if ((statement instanceof EmptyStatement)) statement.bits |= 1;
/*       */ 
/*  2924 */     foreachStatement.sourceEnd = this.endStatementPosition;
/*       */   }
/*       */ 
/*       */   protected void consumeEnhancedForStatementHeader() {
/*  2928 */     ForeachStatement statement = (ForeachStatement)this.astStack[this.astPtr];
/*       */ 
/*  2930 */     this.expressionLengthPtr -= 1;
/*  2931 */     Expression collection = this.expressionStack[(this.expressionPtr--)];
/*  2932 */     statement.collection = collection;
/*  2933 */     statement.sourceEnd = this.rParenPos;
/*       */ 
/*  2935 */     if ((!this.statementRecoveryActivated) && 
/*  2936 */       (this.options.sourceLevel < 3211264L) && 
/*  2937 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition))
/*  2938 */       problemReporter().invalidUsageOfForeachStatements(statement.elementVariable, collection);
/*       */   }
/*       */ 
/*       */   protected void consumeEnhancedForStatementHeaderInit(boolean hasModifiers)
/*       */   {
/*  2944 */     char[] identifierName = this.identifierStack[this.identifierPtr];
/*  2945 */     long namePosition = this.identifierPositionStack[this.identifierPtr];
/*       */ 
/*  2947 */     LocalDeclaration localDeclaration = createLocalDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
/*  2948 */     localDeclaration.declarationSourceEnd = localDeclaration.declarationEnd;
/*       */ 
/*  2950 */     int extraDims = this.intStack[(this.intPtr--)];
/*  2951 */     this.identifierPtr -= 1;
/*  2952 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  2954 */     int declarationSourceStart = 0;
/*  2955 */     int modifiersValue = 0;
/*  2956 */     if (hasModifiers) {
/*  2957 */       declarationSourceStart = this.intStack[(this.intPtr--)];
/*  2958 */       modifiersValue = this.intStack[(this.intPtr--)];
/*       */     } else {
/*  2960 */       this.intPtr -= 2;
/*       */     }
/*       */ 
/*  2963 */     TypeReference type = getTypeReference(this.intStack[(this.intPtr--)] + extraDims);
/*       */     int length;
/*  2967 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  2968 */       System.arraycopy(
/*  2969 */         this.expressionStack, 
/*  2970 */         this.expressionPtr -= length + 1, 
/*  2971 */         localDeclaration.annotations = new Annotation[length], 
/*  2972 */         0, 
/*  2973 */         length);
/*       */     }
/*  2975 */     if (hasModifiers) {
/*  2976 */       localDeclaration.declarationSourceStart = declarationSourceStart;
/*  2977 */       localDeclaration.modifiers = modifiersValue;
/*       */     } else {
/*  2979 */       localDeclaration.declarationSourceStart = type.sourceStart;
/*       */     }
/*  2981 */     localDeclaration.type = type;
/*       */ 
/*  2983 */     ForeachStatement iteratorForStatement = 
/*  2984 */       new ForeachStatement(
/*  2985 */       localDeclaration, 
/*  2986 */       this.intStack[(this.intPtr--)]);
/*  2987 */     pushOnAstStack(iteratorForStatement);
/*       */ 
/*  2989 */     iteratorForStatement.sourceEnd = localDeclaration.declarationSourceEnd;
/*       */   }
/*       */ 
/*       */   protected void consumeEnterAnonymousClassBody(boolean qualified) {
/*  2993 */     TypeReference typeReference = getTypeReference(0);
/*       */ 
/*  2995 */     TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
/*  2996 */     anonymousType.name = CharOperation.NO_CHAR;
/*  2997 */     anonymousType.bits |= 768;
/*  2998 */     QualifiedAllocationExpression alloc = new QualifiedAllocationExpression(anonymousType);
/*  2999 */     markEnclosingMemberWithLocalType();
/*  3000 */     pushOnAstStack(anonymousType);
/*       */ 
/*  3002 */     alloc.sourceEnd = this.rParenPos;
/*       */     int argumentLength;
/*  3004 */     if ((argumentLength = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3005 */       this.expressionPtr -= argumentLength;
/*  3006 */       System.arraycopy(
/*  3007 */         this.expressionStack, 
/*  3008 */         this.expressionPtr + 1, 
/*  3009 */         alloc.arguments = new Expression[argumentLength], 
/*  3010 */         0, 
/*  3011 */         argumentLength);
/*       */     }
/*       */ 
/*  3014 */     if (qualified) {
/*  3015 */       this.expressionLengthPtr -= 1;
/*  3016 */       alloc.enclosingInstance = this.expressionStack[(this.expressionPtr--)];
/*       */     }
/*       */ 
/*  3019 */     alloc.type = typeReference;
/*       */ 
/*  3021 */     anonymousType.sourceEnd = alloc.sourceEnd;
/*       */ 
/*  3023 */     anonymousType.sourceStart = (anonymousType.declarationSourceStart = alloc.type.sourceStart);
/*  3024 */     alloc.sourceStart = this.intStack[(this.intPtr--)];
/*  3025 */     pushOnExpressionStack(alloc);
/*       */ 
/*  3027 */     anonymousType.bodyStart = this.scanner.currentPosition;
/*  3028 */     this.listLength = 0;
/*       */ 
/*  3031 */     this.scanner.commentPtr = -1;
/*       */ 
/*  3034 */     if (this.currentElement != null) {
/*  3035 */       this.lastCheckPoint = anonymousType.bodyStart;
/*  3036 */       this.currentElement = this.currentElement.add(anonymousType, 0);
/*  3037 */       if (!(this.currentElement instanceof RecoveredAnnotation)) {
/*  3038 */         this.currentToken = 0;
/*       */       } else {
/*  3040 */         this.ignoreNextOpeningBrace = true;
/*  3041 */         this.currentElement.bracketBalance += 1;
/*       */       }
/*  3043 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeEnterCompilationUnit()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeEnterMemberValue() {
/*  3052 */     if ((this.currentElement != null) && ((this.currentElement instanceof RecoveredAnnotation))) {
/*  3053 */       RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
/*  3054 */       recoveredAnnotation.hasPendingMemberValueName = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeEnterMemberValueArrayInitializer() {
/*  3059 */     if (this.currentElement != null) {
/*  3060 */       this.ignoreNextOpeningBrace = true;
/*  3061 */       this.currentElement.bracketBalance += 1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeEnterVariable()
/*       */   {
/*  3068 */     char[] identifierName = this.identifierStack[this.identifierPtr];
/*  3069 */     long namePosition = this.identifierPositionStack[this.identifierPtr];
/*  3070 */     int extendedDimension = this.intStack[(this.intPtr--)];
/*       */ 
/*  3073 */     boolean isLocalDeclaration = this.nestedMethod[this.nestedType] != 0;
/*       */     AbstractVariableDeclaration declaration;
/*       */     AbstractVariableDeclaration declaration;
/*  3074 */     if (isLocalDeclaration)
/*       */     {
/*  3076 */       declaration = 
/*  3077 */         createLocalDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
/*       */     }
/*       */     else {
/*  3080 */       declaration = 
/*  3081 */         createFieldDeclaration(identifierName, (int)(namePosition >>> 32), (int)namePosition);
/*       */     }
/*       */ 
/*  3084 */     this.identifierPtr -= 1;
/*  3085 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  3087 */     int variableIndex = this.variablesCounter[this.nestedType];
/*  3088 */     int typeDim = 0;
/*       */     TypeReference type;
/*  3089 */     if (variableIndex == 0)
/*       */     {
/*  3091 */       if (isLocalDeclaration) {
/*  3092 */         declaration.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  3093 */         declaration.modifiers = this.intStack[(this.intPtr--)];
/*       */         int length;
/*  3096 */         if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3097 */           System.arraycopy(
/*  3098 */             this.expressionStack, 
/*  3099 */             this.expressionPtr -= length + 1, 
/*  3100 */             declaration.annotations = new Annotation[length], 
/*  3101 */             0, 
/*  3102 */             length);
/*       */         }
/*  3104 */         TypeReference type = getTypeReference(typeDim = this.intStack[(this.intPtr--)]);
/*  3105 */         if (declaration.declarationSourceStart == -1)
/*       */         {
/*  3107 */           declaration.declarationSourceStart = type.sourceStart;
/*       */         }
/*  3109 */         pushOnAstStack(type);
/*       */       } else {
/*  3111 */         TypeReference type = getTypeReference(typeDim = this.intStack[(this.intPtr--)]);
/*  3112 */         pushOnAstStack(type);
/*  3113 */         declaration.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  3114 */         declaration.modifiers = this.intStack[(this.intPtr--)];
/*       */         int length;
/*  3117 */         if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3118 */           System.arraycopy(
/*  3119 */             this.expressionStack, 
/*  3120 */             this.expressionPtr -= length + 1, 
/*  3121 */             declaration.annotations = new Annotation[length], 
/*  3122 */             0, 
/*  3123 */             length);
/*       */         }
/*       */ 
/*  3126 */         FieldDeclaration fieldDeclaration = (FieldDeclaration)declaration;
/*  3127 */         fieldDeclaration.javadoc = this.javadoc;
/*       */       }
/*  3129 */       this.javadoc = null;
/*       */     } else {
/*  3131 */       type = (TypeReference)this.astStack[(this.astPtr - variableIndex)];
/*  3132 */       typeDim = type.dimensions();
/*  3133 */       AbstractVariableDeclaration previousVariable = 
/*  3134 */         (AbstractVariableDeclaration)this.astStack[this.astPtr];
/*  3135 */       declaration.declarationSourceStart = previousVariable.declarationSourceStart;
/*  3136 */       declaration.modifiers = previousVariable.modifiers;
/*  3137 */       Annotation[] annotations = previousVariable.annotations;
/*  3138 */       if (annotations != null) {
/*  3139 */         int annotationsLength = annotations.length;
/*  3140 */         System.arraycopy(annotations, 0, declaration.annotations = new Annotation[annotationsLength], 0, annotationsLength);
/*       */       }
/*       */     }
/*       */ 
/*  3144 */     if (extendedDimension == 0) {
/*  3145 */       declaration.type = type;
/*       */     } else {
/*  3147 */       int dimension = typeDim + extendedDimension;
/*  3148 */       declaration.type = copyDims(type, dimension);
/*       */     }
/*  3150 */     this.variablesCounter[this.nestedType] += 1;
/*  3151 */     pushOnAstStack(declaration);
/*       */ 
/*  3153 */     if (this.currentElement != null) {
/*  3154 */       if ((!(this.currentElement instanceof RecoveredType)) && (
/*  3155 */         (this.currentToken == 3) || 
/*  3157 */         (Util.getLineNumber(declaration.type.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) != 
/*  3158 */         Util.getLineNumber((int)(namePosition >>> 32), this.scanner.lineEnds, 0, this.scanner.linePtr)))) {
/*  3159 */         this.lastCheckPoint = (int)(namePosition >>> 32);
/*  3160 */         this.restartRecovery = true;
/*  3161 */         return;
/*       */       }
/*  3163 */       if (isLocalDeclaration) {
/*  3164 */         LocalDeclaration localDecl = (LocalDeclaration)this.astStack[this.astPtr];
/*  3165 */         this.lastCheckPoint = (localDecl.sourceEnd + 1);
/*  3166 */         this.currentElement = this.currentElement.add(localDecl, 0);
/*       */       } else {
/*  3168 */         FieldDeclaration fieldDecl = (FieldDeclaration)this.astStack[this.astPtr];
/*  3169 */         this.lastCheckPoint = (fieldDecl.sourceEnd + 1);
/*  3170 */         this.currentElement = this.currentElement.add(fieldDecl, 0);
/*       */       }
/*  3172 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeEnumBodyNoConstants()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeEnumBodyWithConstants() {
/*  3181 */     concatNodeLists();
/*       */   }
/*       */   protected void consumeEnumConstantHeader() {
/*  3184 */     FieldDeclaration enumConstant = (FieldDeclaration)this.astStack[this.astPtr];
/*  3185 */     boolean foundOpeningBrace = this.currentToken == 69;
/*  3186 */     if (foundOpeningBrace)
/*       */     {
/*  3188 */       TypeDeclaration anonymousType = new TypeDeclaration(this.compilationUnit.compilationResult);
/*  3189 */       anonymousType.name = CharOperation.NO_CHAR;
/*  3190 */       anonymousType.bits |= 768;
/*  3191 */       int start = this.scanner.startPosition;
/*  3192 */       anonymousType.declarationSourceStart = start;
/*  3193 */       anonymousType.sourceStart = start;
/*  3194 */       anonymousType.sourceEnd = start;
/*  3195 */       anonymousType.modifiers = 0;
/*  3196 */       anonymousType.bodyStart = this.scanner.currentPosition;
/*  3197 */       markEnclosingMemberWithLocalType();
/*  3198 */       pushOnAstStack(anonymousType);
/*  3199 */       QualifiedAllocationExpression allocationExpression = new QualifiedAllocationExpression(anonymousType);
/*  3200 */       allocationExpression.enumConstant = enumConstant;
/*       */       int length;
/*  3204 */       if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3205 */         this.expressionPtr -= length;
/*  3206 */         System.arraycopy(
/*  3207 */           this.expressionStack, 
/*  3208 */           this.expressionPtr + 1, 
/*  3209 */           allocationExpression.arguments = new Expression[length], 
/*  3210 */           0, 
/*  3211 */           length);
/*       */       }
/*  3213 */       enumConstant.initialization = allocationExpression;
/*       */     } else {
/*  3215 */       AllocationExpression allocationExpression = new AllocationExpression();
/*  3216 */       allocationExpression.enumConstant = enumConstant;
/*       */       int length;
/*  3219 */       if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3220 */         this.expressionPtr -= length;
/*  3221 */         System.arraycopy(
/*  3222 */           this.expressionStack, 
/*  3223 */           this.expressionPtr + 1, 
/*  3224 */           allocationExpression.arguments = new Expression[length], 
/*  3225 */           0, 
/*  3226 */           length);
/*       */       }
/*  3228 */       enumConstant.initialization = allocationExpression;
/*       */     }
/*       */ 
/*  3232 */     if (this.currentElement != null)
/*  3233 */       if (foundOpeningBrace) {
/*  3234 */         TypeDeclaration anonymousType = (TypeDeclaration)this.astStack[this.astPtr];
/*  3235 */         this.currentElement = this.currentElement.add(anonymousType, 0);
/*  3236 */         this.lastCheckPoint = anonymousType.bodyStart;
/*  3237 */         this.lastIgnoredToken = -1;
/*  3238 */         this.currentToken = 0;
/*       */       } else {
/*  3240 */         if (this.currentToken == 27) {
/*  3241 */           RecoveredType currentType = currentRecoveryType();
/*  3242 */           if (currentType != null) {
/*  3243 */             currentType.insideEnumConstantPart = false;
/*       */           }
/*       */         }
/*  3246 */         this.lastCheckPoint = this.scanner.startPosition;
/*  3247 */         this.lastIgnoredToken = -1;
/*  3248 */         this.restartRecovery = true;
/*       */       }
/*       */   }
/*       */ 
/*       */   protected void consumeEnumConstantHeaderName() {
/*  3253 */     if ((this.currentElement != null) && (
/*  3254 */       ((!(this.currentElement instanceof RecoveredType)) && (
/*  3255 */       (!(this.currentElement instanceof RecoveredField)) || (((RecoveredField)this.currentElement).fieldDeclaration.type != null))) || 
/*  3256 */       (this.lastIgnoredToken == 3))) {
/*  3257 */       this.lastCheckPoint = this.scanner.startPosition;
/*  3258 */       this.restartRecovery = true;
/*  3259 */       return;
/*       */     }
/*       */ 
/*  3262 */     long namePosition = this.identifierPositionStack[this.identifierPtr];
/*  3263 */     char[] constantName = this.identifierStack[this.identifierPtr];
/*  3264 */     int sourceEnd = (int)namePosition;
/*  3265 */     FieldDeclaration enumConstant = createFieldDeclaration(constantName, (int)(namePosition >>> 32), sourceEnd);
/*  3266 */     this.identifierPtr -= 1;
/*  3267 */     this.identifierLengthPtr -= 1;
/*  3268 */     enumConstant.modifiersSourceStart = this.intStack[(this.intPtr--)];
/*  3269 */     enumConstant.modifiers = this.intStack[(this.intPtr--)];
/*  3270 */     enumConstant.declarationSourceStart = enumConstant.modifiersSourceStart;
/*       */ 
/*  3273 */     if (((enumConstant.bits & 0x400) == 0) && ((enumConstant.bits & 0x100) == 0) && 
/*  3274 */       (this.compilationUnit != null) && (!CharOperation.equals(enumConstant.name, this.compilationUnit.getMainTypeName())))
/*  3275 */       enumConstant.bits |= 4096;
/*       */     int length;
/*  3281 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3282 */       System.arraycopy(
/*  3283 */         this.expressionStack, 
/*  3284 */         this.expressionPtr -= length + 1, 
/*  3285 */         enumConstant.annotations = new Annotation[length], 
/*  3286 */         0, 
/*  3287 */         length);
/*       */     }
/*  3289 */     pushOnAstStack(enumConstant);
/*  3290 */     if (this.currentElement != null) {
/*  3291 */       this.lastCheckPoint = (enumConstant.sourceEnd + 1);
/*  3292 */       this.currentElement = this.currentElement.add(enumConstant, 0);
/*       */     }
/*       */ 
/*  3295 */     enumConstant.javadoc = this.javadoc;
/*  3296 */     this.javadoc = null;
/*       */   }
/*       */ 
/*       */   protected void consumeEnumConstantNoClassBody() {
/*  3300 */     int endOfEnumConstant = this.intStack[(this.intPtr--)];
/*  3301 */     FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
/*  3302 */     fieldDeclaration.declarationEnd = endOfEnumConstant;
/*  3303 */     fieldDeclaration.declarationSourceEnd = endOfEnumConstant;
/*       */   }
/*       */   protected void consumeEnumConstants() {
/*  3306 */     concatNodeLists();
/*       */   }
/*       */   protected void consumeEnumConstantWithClassBody() {
/*  3309 */     dispatchDeclarationInto(this.astLengthStack[(this.astLengthPtr--)]);
/*  3310 */     TypeDeclaration anonymousType = (TypeDeclaration)this.astStack[(this.astPtr--)];
/*  3311 */     this.astLengthPtr -= 1;
/*  3312 */     anonymousType.bodyEnd = this.endPosition;
/*  3313 */     anonymousType.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*  3314 */     FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[this.astPtr];
/*  3315 */     fieldDeclaration.declarationEnd = this.endStatementPosition;
/*  3316 */     fieldDeclaration.declarationSourceEnd = anonymousType.declarationSourceEnd;
/*  3317 */     this.intPtr -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeEnumDeclaration()
/*       */   {
/*       */     int length;
/*  3322 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)
/*       */     {
/*  3325 */       dispatchDeclarationIntoEnumDeclaration(length);
/*       */     }
/*       */ 
/*  3328 */     TypeDeclaration enumDeclaration = (TypeDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  3331 */     boolean hasConstructor = enumDeclaration.checkConstructors(this);
/*       */ 
/*  3334 */     if (!hasConstructor) {
/*  3335 */       boolean insideFieldInitializer = false;
/*  3336 */       if (this.diet) {
/*  3337 */         for (int i = this.nestedType; i > 0; i--) {
/*  3338 */           if (this.variablesCounter[i] > 0) {
/*  3339 */             insideFieldInitializer = true;
/*  3340 */             break;
/*       */           }
/*       */         }
/*       */       }
/*  3344 */       enumDeclaration.createDefaultConstructor((!this.diet) || (insideFieldInitializer), true);
/*       */     }
/*       */ 
/*  3348 */     if (this.scanner.containsAssertKeyword) {
/*  3349 */       enumDeclaration.bits |= 1;
/*       */     }
/*  3351 */     enumDeclaration.addClinit();
/*  3352 */     enumDeclaration.bodyEnd = this.endStatementPosition;
/*  3353 */     if ((length == 0) && (!containsComment(enumDeclaration.bodyStart, enumDeclaration.bodyEnd))) {
/*  3354 */       enumDeclaration.bits |= 8;
/*       */     }
/*       */ 
/*  3357 */     enumDeclaration.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */   protected void consumeEnumDeclarations() {
/*       */   }
/*       */ 
/*       */   protected void consumeEnumHeader() {
/*  3363 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  3364 */     if (this.currentToken == 69) {
/*  3365 */       typeDecl.bodyStart = this.scanner.currentPosition;
/*       */     }
/*       */ 
/*  3368 */     if (this.currentElement != null) {
/*  3369 */       this.restartRecovery = true;
/*       */     }
/*       */ 
/*  3373 */     this.scanner.commentPtr = -1;
/*       */   }
/*       */ 
/*       */   protected void consumeEnumHeaderName() {
/*  3377 */     TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
/*  3378 */     if (this.nestedMethod[this.nestedType] == 0) {
/*  3379 */       if (this.nestedType != 0) {
/*  3380 */         enumDeclaration.bits |= 1024;
/*       */       }
/*       */     }
/*       */     else
/*       */     {
/*  3385 */       blockReal();
/*       */     }
/*       */ 
/*  3388 */     long pos = this.identifierPositionStack[this.identifierPtr];
/*  3389 */     enumDeclaration.sourceEnd = (int)pos;
/*  3390 */     enumDeclaration.sourceStart = (int)(pos >>> 32);
/*  3391 */     enumDeclaration.name = this.identifierStack[(this.identifierPtr--)];
/*  3392 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  3398 */     enumDeclaration.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  3399 */     this.intPtr -= 1;
/*       */ 
/*  3401 */     enumDeclaration.modifiersSourceStart = this.intStack[(this.intPtr--)];
/*  3402 */     enumDeclaration.modifiers = (this.intStack[(this.intPtr--)] | 0x4000);
/*  3403 */     if (enumDeclaration.modifiersSourceStart >= 0)
/*  3404 */       enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
/*       */     int length;
/*  3408 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3409 */       System.arraycopy(
/*  3410 */         this.expressionStack, 
/*  3411 */         this.expressionPtr -= length + 1, 
/*  3412 */         enumDeclaration.annotations = new Annotation[length], 
/*  3413 */         0, 
/*  3414 */         length);
/*       */     }
/*       */ 
/*  3419 */     enumDeclaration.bodyStart = (enumDeclaration.sourceEnd + 1);
/*  3420 */     pushOnAstStack(enumDeclaration);
/*       */ 
/*  3422 */     this.listLength = 0;
/*       */ 
/*  3424 */     if ((!this.statementRecoveryActivated) && 
/*  3425 */       (this.options.sourceLevel < 3211264L) && 
/*  3426 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition))
/*       */     {
/*  3428 */       problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
/*       */     }
/*       */ 
/*  3432 */     if (this.currentElement != null) {
/*  3433 */       this.lastCheckPoint = enumDeclaration.bodyStart;
/*  3434 */       this.currentElement = this.currentElement.add(enumDeclaration, 0);
/*  3435 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */ 
/*  3438 */     enumDeclaration.javadoc = this.javadoc;
/*  3439 */     this.javadoc = null;
/*       */   }
/*       */ 
/*       */   protected void consumeEnumHeaderNameWithTypeParameters() {
/*  3443 */     TypeDeclaration enumDeclaration = new TypeDeclaration(this.compilationUnit.compilationResult);
/*       */ 
/*  3445 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  3446 */     this.genericsPtr -= length;
/*  3447 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, enumDeclaration.typeParameters = new TypeParameter[length], 0, length);
/*       */ 
/*  3449 */     problemReporter().invalidUsageOfTypeParametersForEnumDeclaration(enumDeclaration);
/*       */ 
/*  3451 */     enumDeclaration.bodyStart = (enumDeclaration.typeParameters[(length - 1)].declarationSourceEnd + 1);
/*       */ 
/*  3455 */     this.listTypeParameterLength = 0;
/*       */ 
/*  3457 */     if (this.nestedMethod[this.nestedType] == 0) {
/*  3458 */       if (this.nestedType != 0) {
/*  3459 */         enumDeclaration.bits |= 1024;
/*       */       }
/*       */     }
/*       */     else
/*       */     {
/*  3464 */       blockReal();
/*       */     }
/*       */ 
/*  3467 */     long pos = this.identifierPositionStack[this.identifierPtr];
/*  3468 */     enumDeclaration.sourceEnd = (int)pos;
/*  3469 */     enumDeclaration.sourceStart = (int)(pos >>> 32);
/*  3470 */     enumDeclaration.name = this.identifierStack[(this.identifierPtr--)];
/*  3471 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  3477 */     enumDeclaration.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  3478 */     this.intPtr -= 1;
/*       */ 
/*  3480 */     enumDeclaration.modifiersSourceStart = this.intStack[(this.intPtr--)];
/*  3481 */     enumDeclaration.modifiers = (this.intStack[(this.intPtr--)] | 0x4000);
/*  3482 */     if (enumDeclaration.modifiersSourceStart >= 0) {
/*  3483 */       enumDeclaration.declarationSourceStart = enumDeclaration.modifiersSourceStart;
/*       */     }
/*       */ 
/*  3486 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3487 */       System.arraycopy(
/*  3488 */         this.expressionStack, 
/*  3489 */         this.expressionPtr -= length + 1, 
/*  3490 */         enumDeclaration.annotations = new Annotation[length], 
/*  3491 */         0, 
/*  3492 */         length);
/*       */     }
/*       */ 
/*  3497 */     enumDeclaration.bodyStart = (enumDeclaration.sourceEnd + 1);
/*  3498 */     pushOnAstStack(enumDeclaration);
/*       */ 
/*  3500 */     this.listLength = 0;
/*       */ 
/*  3502 */     if ((!this.statementRecoveryActivated) && 
/*  3503 */       (this.options.sourceLevel < 3211264L) && 
/*  3504 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition))
/*       */     {
/*  3506 */       problemReporter().invalidUsageOfEnumDeclarations(enumDeclaration);
/*       */     }
/*       */ 
/*  3510 */     if (this.currentElement != null) {
/*  3511 */       this.lastCheckPoint = enumDeclaration.bodyStart;
/*  3512 */       this.currentElement = this.currentElement.add(enumDeclaration, 0);
/*  3513 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */ 
/*  3516 */     enumDeclaration.javadoc = this.javadoc;
/*  3517 */     this.javadoc = null;
/*       */   }
/*       */ 
/*       */   protected void consumeEqualityExpression(int op)
/*       */   {
/*  3525 */     this.expressionPtr -= 1;
/*  3526 */     this.expressionLengthPtr -= 1;
/*  3527 */     this.expressionStack[this.expressionPtr] = 
/*  3528 */       new EqualExpression(
/*  3529 */       this.expressionStack[this.expressionPtr], 
/*  3530 */       this.expressionStack[(this.expressionPtr + 1)], 
/*  3531 */       op);
/*       */   }
/*       */ 
/*       */   protected void consumeEqualityExpressionWithName(int op)
/*       */   {
/*  3539 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*  3540 */     this.expressionPtr -= 1;
/*  3541 */     this.expressionLengthPtr -= 1;
/*  3542 */     this.expressionStack[this.expressionPtr] = 
/*  3543 */       new EqualExpression(
/*  3544 */       this.expressionStack[(this.expressionPtr + 1)], 
/*  3545 */       this.expressionStack[this.expressionPtr], 
/*  3546 */       op);
/*       */   }
/*       */ 
/*       */   protected void consumeExitMemberValue() {
/*  3550 */     if ((this.currentElement != null) && ((this.currentElement instanceof RecoveredAnnotation))) {
/*  3551 */       RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
/*  3552 */       recoveredAnnotation.hasPendingMemberValueName = false;
/*  3553 */       recoveredAnnotation.memberValuPairEqualEnd = -1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeExitTryBlock() {
/*  3558 */     if (this.currentElement != null)
/*  3559 */       this.restartRecovery = true;
/*       */   }
/*       */ 
/*       */   protected void consumeExitVariableWithInitialization()
/*       */   {
/*  3565 */     this.expressionLengthPtr -= 1;
/*  3566 */     AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)this.astStack[this.astPtr];
/*  3567 */     variableDecl.initialization = this.expressionStack[(this.expressionPtr--)];
/*       */ 
/*  3570 */     variableDecl.declarationSourceEnd = variableDecl.initialization.sourceEnd;
/*  3571 */     variableDecl.declarationEnd = variableDecl.initialization.sourceEnd;
/*       */ 
/*  3573 */     recoveryExitFromVariable();
/*       */   }
/*       */ 
/*       */   protected void consumeExitVariableWithoutInitialization()
/*       */   {
/*  3579 */     AbstractVariableDeclaration variableDecl = (AbstractVariableDeclaration)this.astStack[this.astPtr];
/*  3580 */     variableDecl.declarationSourceEnd = variableDecl.declarationEnd;
/*  3581 */     if ((this.currentElement != null) && ((this.currentElement instanceof RecoveredField)) && 
/*  3582 */       (this.endStatementPosition > variableDecl.sourceEnd)) {
/*  3583 */       this.currentElement.updateSourceEndIfNecessary(this.endStatementPosition);
/*       */     }
/*       */ 
/*  3586 */     recoveryExitFromVariable();
/*       */   }
/*       */ 
/*       */   protected void consumeExplicitConstructorInvocation(int flag, int recFlag)
/*       */   {
/*  3601 */     int startPosition = this.intStack[(this.intPtr--)];
/*  3602 */     ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
/*       */     int length;
/*  3604 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3605 */       this.expressionPtr -= length;
/*  3606 */       System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
/*       */     }
/*  3608 */     switch (flag) {
/*       */     case 0:
/*  3610 */       ecc.sourceStart = startPosition;
/*  3611 */       break;
/*       */     case 1:
/*  3613 */       this.expressionLengthPtr -= 1;
/*  3614 */       ecc.sourceStart = (ecc.qualification = this.expressionStack[(this.expressionPtr--)]).sourceStart;
/*  3615 */       break;
/*       */     case 2:
/*  3617 */       ecc.sourceStart = (ecc.qualification = getUnspecifiedReferenceOptimized()).sourceStart;
/*       */     }
/*       */ 
/*  3620 */     pushOnAstStack(ecc);
/*  3621 */     ecc.sourceEnd = this.endStatementPosition;
/*       */   }
/*       */ 
/*       */   protected void consumeExplicitConstructorInvocationWithTypeArguments(int flag, int recFlag)
/*       */   {
/*  3636 */     int startPosition = this.intStack[(this.intPtr--)];
/*  3637 */     ExplicitConstructorCall ecc = new ExplicitConstructorCall(recFlag);
/*       */ 
/*  3639 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3640 */       this.expressionPtr -= length;
/*  3641 */       System.arraycopy(this.expressionStack, this.expressionPtr + 1, ecc.arguments = new Expression[length], 0, length);
/*       */     }
/*  3643 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  3644 */     this.genericsPtr -= length;
/*  3645 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, ecc.typeArguments = new TypeReference[length], 0, length);
/*  3646 */     ecc.typeArgumentsSourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  3648 */     switch (flag) {
/*       */     case 0:
/*  3650 */       ecc.sourceStart = startPosition;
/*  3651 */       break;
/*       */     case 1:
/*  3653 */       this.expressionLengthPtr -= 1;
/*  3654 */       ecc.sourceStart = (ecc.qualification = this.expressionStack[(this.expressionPtr--)]).sourceStart;
/*  3655 */       break;
/*       */     case 2:
/*  3657 */       ecc.sourceStart = (ecc.qualification = getUnspecifiedReferenceOptimized()).sourceStart;
/*       */     }
/*       */ 
/*  3661 */     pushOnAstStack(ecc);
/*  3662 */     ecc.sourceEnd = this.endStatementPosition;
/*       */   }
/*       */ 
/*       */   protected void consumeExpressionStatement() {
/*  3666 */     this.expressionLengthPtr -= 1;
/*  3667 */     Expression expression = this.expressionStack[(this.expressionPtr--)];
/*  3668 */     expression.statementEnd = this.endStatementPosition;
/*  3669 */     pushOnAstStack(expression);
/*       */   }
/*       */ 
/*       */   protected void consumeFieldAccess(boolean isSuperAccess)
/*       */   {
/*  3675 */     FieldReference fr = 
/*  3676 */       new FieldReference(
/*  3677 */       this.identifierStack[this.identifierPtr], 
/*  3678 */       this.identifierPositionStack[(this.identifierPtr--)]);
/*  3679 */     this.identifierLengthPtr -= 1;
/*  3680 */     if (isSuperAccess)
/*       */     {
/*  3682 */       fr.sourceStart = this.intStack[(this.intPtr--)];
/*  3683 */       fr.receiver = new SuperReference(fr.sourceStart, this.endPosition);
/*  3684 */       pushOnExpressionStack(fr);
/*       */     }
/*       */     else {
/*  3687 */       fr.receiver = this.expressionStack[this.expressionPtr];
/*       */ 
/*  3689 */       fr.sourceStart = fr.receiver.sourceStart;
/*  3690 */       this.expressionStack[this.expressionPtr] = fr;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeFieldDeclaration()
/*       */   {
/*  3709 */     int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
/*       */ 
/*  3711 */     for (int i = variableDeclaratorsCounter - 1; i >= 0; i--) {
/*  3712 */       FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[(this.astPtr - i)];
/*  3713 */       fieldDeclaration.declarationSourceEnd = this.endStatementPosition;
/*  3714 */       fieldDeclaration.declarationEnd = this.endStatementPosition;
/*       */     }
/*       */ 
/*  3717 */     updateSourceDeclarationParts(variableDeclaratorsCounter);
/*  3718 */     int endPos = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*  3719 */     if (endPos != this.endStatementPosition) {
/*  3720 */       for (int i = 0; i < variableDeclaratorsCounter; i++) {
/*  3721 */         FieldDeclaration fieldDeclaration = (FieldDeclaration)this.astStack[(this.astPtr - i)];
/*  3722 */         fieldDeclaration.declarationSourceEnd = endPos;
/*       */       }
/*       */     }
/*       */ 
/*  3726 */     int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
/*  3727 */     System.arraycopy(
/*  3728 */       this.astStack, 
/*  3729 */       startIndex, 
/*  3730 */       this.astStack, 
/*  3731 */       startIndex - 1, 
/*  3732 */       variableDeclaratorsCounter);
/*  3733 */     this.astPtr -= 1;
/*  3734 */     this.astLengthStack[(--this.astLengthPtr)] = variableDeclaratorsCounter;
/*       */ 
/*  3737 */     if (this.currentElement != null) {
/*  3738 */       this.lastCheckPoint = (endPos + 1);
/*  3739 */       if ((this.currentElement.parent != null) && ((this.currentElement instanceof RecoveredField)) && 
/*  3740 */         (!(this.currentElement instanceof RecoveredInitializer))) {
/*  3741 */         this.currentElement = this.currentElement.parent;
/*       */       }
/*       */ 
/*  3744 */       this.restartRecovery = true;
/*       */     }
/*  3746 */     this.variablesCounter[this.nestedType] = 0;
/*       */   }
/*       */ 
/*       */   protected void consumeForceNoDiet() {
/*  3750 */     this.dietInt += 1;
/*       */   }
/*       */ 
/*       */   protected void consumeForInit() {
/*  3754 */     pushOnAstLengthStack(-1);
/*       */   }
/*       */ 
/*       */   protected void consumeFormalParameter(boolean isVarArgs)
/*       */   {
/*  3769 */     this.identifierLengthPtr -= 1;
/*  3770 */     char[] identifierName = this.identifierStack[this.identifierPtr];
/*  3771 */     long namePositions = this.identifierPositionStack[(this.identifierPtr--)];
/*  3772 */     int extendedDimensions = this.intStack[(this.intPtr--)];
/*  3773 */     int endOfEllipsis = 0;
/*  3774 */     if (isVarArgs) {
/*  3775 */       endOfEllipsis = this.intStack[(this.intPtr--)];
/*       */     }
/*  3777 */     int firstDimensions = this.intStack[(this.intPtr--)];
/*  3778 */     int typeDimensions = firstDimensions + extendedDimensions;
/*  3779 */     TypeReference type = getTypeReference(typeDimensions);
/*  3780 */     if (isVarArgs) {
/*  3781 */       type = copyDims(type, typeDimensions + 1);
/*  3782 */       if (extendedDimensions == 0) {
/*  3783 */         type.sourceEnd = endOfEllipsis;
/*       */       }
/*  3785 */       type.bits |= 16384;
/*       */     }
/*  3787 */     int modifierPositions = this.intStack[(this.intPtr--)];
/*  3788 */     this.intPtr -= 1;
/*  3789 */     Argument arg = 
/*  3790 */       new Argument(
/*  3791 */       identifierName, 
/*  3792 */       namePositions, 
/*  3793 */       type, 
/*  3794 */       this.intStack[(this.intPtr + 1)] & 0xFFEFFFFF);
/*  3795 */     arg.declarationSourceStart = modifierPositions;
/*       */     int length;
/*  3798 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  3799 */       System.arraycopy(
/*  3800 */         this.expressionStack, 
/*  3801 */         this.expressionPtr -= length + 1, 
/*  3802 */         arg.annotations = new Annotation[length], 
/*  3803 */         0, 
/*  3804 */         length);
/*       */     }
/*  3806 */     pushOnAstStack(arg);
/*       */ 
/*  3810 */     this.listLength += 1;
/*       */ 
/*  3812 */     if (isVarArgs)
/*  3813 */       if ((!this.statementRecoveryActivated) && 
/*  3814 */         (this.options.sourceLevel < 3211264L) && 
/*  3815 */         (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition))
/*  3816 */         problemReporter().invalidUsageOfVarargs(arg);
/*  3817 */       else if ((!this.statementRecoveryActivated) && 
/*  3818 */         (extendedDimensions > 0))
/*  3819 */         problemReporter().illegalExtendedDimensions(arg);
/*       */   }
/*       */ 
/*       */   protected void consumeFormalParameterList()
/*       */   {
/*  3825 */     optimizedConcatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeFormalParameterListopt() {
/*  3829 */     pushOnAstLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumeGenericType()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeGenericTypeArrayType()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeGenericTypeNameArrayType()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeImportDeclaration() {
/*  3845 */     ImportReference impt = (ImportReference)this.astStack[this.astPtr];
/*       */ 
/*  3847 */     impt.declarationEnd = this.endStatementPosition;
/*  3848 */     impt.declarationSourceEnd = 
/*  3849 */       flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
/*       */ 
/*  3852 */     if (this.currentElement != null) {
/*  3853 */       this.lastCheckPoint = (impt.declarationSourceEnd + 1);
/*  3854 */       this.currentElement = this.currentElement.add(impt, 0);
/*  3855 */       this.lastIgnoredToken = -1;
/*  3856 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeImportDeclarations()
/*       */   {
/*  3862 */     optimizedConcatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeInsideCastExpression() {
/*       */   }
/*       */ 
/*       */   protected void consumeInsideCastExpressionLL1() {
/*  3869 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*       */   }
/*       */ 
/*       */   protected void consumeInsideCastExpressionWithQualifiedGenerics()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeInstanceOfExpression()
/*       */   {
/*       */      tmp44_41 = 
/*  3881 */       new InstanceOfExpression(
/*  3882 */       this.expressionStack[this.expressionPtr], 
/*  3883 */       getTypeReference(this.intStack[(this.intPtr--)]));
/*       */ 
/*  3880 */     Expression exp = tmp44_41; this.expressionStack[this.expressionPtr] = tmp44_41;
/*       */ 
/*  3884 */     if (exp.sourceEnd == 0)
/*       */     {
/*  3886 */       exp.sourceEnd = (this.scanner.startPosition - 1);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeInstanceOfExpressionWithName()
/*       */   {
/*  3895 */     TypeReference reference = getTypeReference(this.intStack[(this.intPtr--)]);
/*  3896 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*       */      tmp54_51 = 
/*  3899 */       new InstanceOfExpression(
/*  3900 */       this.expressionStack[this.expressionPtr], 
/*  3901 */       reference);
/*       */ 
/*  3898 */     Expression exp = tmp54_51; this.expressionStack[this.expressionPtr] = tmp54_51;
/*       */ 
/*  3902 */     if (exp.sourceEnd == 0)
/*       */     {
/*  3904 */       exp.sourceEnd = (this.scanner.startPosition - 1);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceDeclaration()
/*       */   {
/*       */     int length;
/*  3912 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)
/*       */     {
/*  3915 */       dispatchDeclarationInto(length);
/*       */     }
/*       */ 
/*  3918 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  3921 */     typeDecl.checkConstructors(this);
/*       */ 
/*  3924 */     if (this.scanner.containsAssertKeyword) {
/*  3925 */       typeDecl.bits |= 1;
/*       */     }
/*  3927 */     typeDecl.addClinit();
/*  3928 */     typeDecl.bodyEnd = this.endStatementPosition;
/*  3929 */     if ((length == 0) && (!containsComment(typeDecl.bodyStart, typeDecl.bodyEnd))) {
/*  3930 */       typeDecl.bits |= 8;
/*       */     }
/*  3932 */     typeDecl.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceHeader()
/*       */   {
/*  3937 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  3938 */     if (this.currentToken == 69) {
/*  3939 */       typeDecl.bodyStart = this.scanner.currentPosition;
/*       */     }
/*  3941 */     if (this.currentElement != null) {
/*  3942 */       this.restartRecovery = true;
/*       */     }
/*       */ 
/*  3945 */     this.scanner.commentPtr = -1;
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceHeaderExtends() {
/*  3949 */     int length = this.astLengthStack[(this.astLengthPtr--)];
/*       */ 
/*  3951 */     this.astPtr -= length;
/*  3952 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  3953 */     System.arraycopy(
/*  3954 */       this.astStack, 
/*  3955 */       this.astPtr + 1, 
/*  3956 */       typeDecl.superInterfaces = new TypeReference[length], 
/*  3957 */       0, 
/*  3958 */       length);
/*  3959 */     int i = 0; for (int max = typeDecl.superInterfaces.length; i < max; i++) {
/*  3960 */       typeDecl.superInterfaces[i].bits |= 16;
/*       */     }
/*  3962 */     typeDecl.bodyStart = (typeDecl.superInterfaces[(length - 1)].sourceEnd + 1);
/*  3963 */     this.listLength = 0;
/*       */ 
/*  3965 */     if (this.currentElement != null)
/*  3966 */       this.lastCheckPoint = typeDecl.bodyStart;
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceHeaderName1()
/*       */   {
/*  3971 */     TypeDeclaration typeDecl = new TypeDeclaration(this.compilationUnit.compilationResult);
/*       */ 
/*  3973 */     if (this.nestedMethod[this.nestedType] == 0) {
/*  3974 */       if (this.nestedType != 0)
/*  3975 */         typeDecl.bits |= 1024;
/*       */     }
/*       */     else
/*       */     {
/*  3979 */       typeDecl.bits |= 256;
/*  3980 */       markEnclosingMemberWithLocalType();
/*  3981 */       blockReal();
/*       */     }
/*       */ 
/*  3985 */     long pos = this.identifierPositionStack[this.identifierPtr];
/*  3986 */     typeDecl.sourceEnd = (int)pos;
/*  3987 */     typeDecl.sourceStart = (int)(pos >>> 32);
/*  3988 */     typeDecl.name = this.identifierStack[(this.identifierPtr--)];
/*  3989 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  3995 */     typeDecl.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  3996 */     this.intPtr -= 1;
/*  3997 */     typeDecl.modifiersSourceStart = this.intStack[(this.intPtr--)];
/*  3998 */     typeDecl.modifiers = (this.intStack[(this.intPtr--)] | 0x200);
/*  3999 */     if (typeDecl.modifiersSourceStart >= 0) {
/*  4000 */       typeDecl.declarationSourceStart = typeDecl.modifiersSourceStart;
/*       */     }
/*       */ 
/*  4004 */     if (((typeDecl.bits & 0x400) == 0) && ((typeDecl.bits & 0x100) == 0) && 
/*  4005 */       (this.compilationUnit != null) && (!CharOperation.equals(typeDecl.name, this.compilationUnit.getMainTypeName())))
/*  4006 */       typeDecl.bits |= 4096;
/*       */     int length;
/*  4012 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  4013 */       System.arraycopy(
/*  4014 */         this.expressionStack, 
/*  4015 */         this.expressionPtr -= length + 1, 
/*  4016 */         typeDecl.annotations = new Annotation[length], 
/*  4017 */         0, 
/*  4018 */         length);
/*       */     }
/*  4020 */     typeDecl.bodyStart = (typeDecl.sourceEnd + 1);
/*  4021 */     pushOnAstStack(typeDecl);
/*  4022 */     this.listLength = 0;
/*       */ 
/*  4024 */     if (this.currentElement != null) {
/*  4025 */       this.lastCheckPoint = typeDecl.bodyStart;
/*  4026 */       this.currentElement = this.currentElement.add(typeDecl, 0);
/*  4027 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */ 
/*  4030 */     typeDecl.javadoc = this.javadoc;
/*  4031 */     this.javadoc = null;
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceMemberDeclarations() {
/*  4035 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceMemberDeclarationsopt() {
/*  4039 */     this.nestedType -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceType() {
/*  4043 */     pushOnAstStack(getTypeReference(0));
/*       */ 
/*  4046 */     this.listLength += 1;
/*       */   }
/*       */ 
/*       */   protected void consumeInterfaceTypeList() {
/*  4050 */     optimizedConcatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeInternalCompilationUnit()
/*       */   {
/*  4056 */     if (this.compilationUnit.isPackageInfo()) {
/*  4057 */       this.compilationUnit.types = new TypeDeclaration[1];
/*  4058 */       this.compilationUnit.createPackageInfoType();
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeInternalCompilationUnitWithTypes()
/*       */   {
/*       */     int length;
/*  4068 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)
/*  4069 */       if (this.compilationUnit.isPackageInfo()) {
/*  4070 */         this.compilationUnit.types = new TypeDeclaration[length + 1];
/*  4071 */         this.astPtr -= length;
/*  4072 */         System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 1, length);
/*  4073 */         this.compilationUnit.createPackageInfoType();
/*       */       } else {
/*  4075 */         this.compilationUnit.types = new TypeDeclaration[length];
/*  4076 */         this.astPtr -= length;
/*  4077 */         System.arraycopy(this.astStack, this.astPtr + 1, this.compilationUnit.types, 0, length);
/*       */       }
/*       */   }
/*       */ 
/*       */   protected void consumeInvalidAnnotationTypeDeclaration()
/*       */   {
/*  4083 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  4084 */     if (!this.statementRecoveryActivated) problemReporter().illegalLocalTypeDeclaration(typeDecl);
/*       */ 
/*  4086 */     this.astPtr -= 1;
/*  4087 */     pushOnAstLengthStack(-1);
/*  4088 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeInvalidConstructorDeclaration()
/*       */   {
/*  4093 */     ConstructorDeclaration cd = (ConstructorDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  4095 */     cd.bodyEnd = this.endPosition;
/*  4096 */     cd.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */ 
/*  4099 */     cd.modifiers |= 16777216;
/*       */   }
/*       */ 
/*       */   protected void consumeInvalidConstructorDeclaration(boolean hasBody)
/*       */   {
/*  4112 */     if (hasBody)
/*       */     {
/*  4114 */       this.intPtr -= 1;
/*       */     }
/*       */ 
/*  4118 */     if (hasBody)
/*  4119 */       this.realBlockPtr -= 1;
/*       */     int length;
/*  4123 */     if ((hasBody) && ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)) {
/*  4124 */       this.astPtr -= length;
/*       */     }
/*  4126 */     ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)this.astStack[this.astPtr];
/*  4127 */     constructorDeclaration.bodyEnd = this.endStatementPosition;
/*  4128 */     constructorDeclaration.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*  4129 */     if (!hasBody)
/*  4130 */       constructorDeclaration.modifiers |= 16777216;
/*       */   }
/*       */ 
/*       */   protected void consumeInvalidEnumDeclaration()
/*       */   {
/*  4135 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  4136 */     if (!this.statementRecoveryActivated) problemReporter().illegalLocalTypeDeclaration(typeDecl);
/*       */ 
/*  4138 */     this.astPtr -= 1;
/*  4139 */     pushOnAstLengthStack(-1);
/*  4140 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeInvalidInterfaceDeclaration()
/*       */   {
/*  4145 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  4146 */     if (!this.statementRecoveryActivated) problemReporter().illegalLocalTypeDeclaration(typeDecl);
/*       */ 
/*  4148 */     this.astPtr -= 1;
/*  4149 */     pushOnAstLengthStack(-1);
/*  4150 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeInvalidMethodDeclaration()
/*       */   {
/*  4166 */     this.intPtr -= 1;
/*       */ 
/*  4170 */     this.realBlockPtr -= 1;
/*       */     int length;
/*  4172 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0) {
/*  4173 */       this.astPtr -= length;
/*       */     }
/*       */ 
/*  4177 */     MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
/*  4178 */     md.bodyEnd = this.endPosition;
/*  4179 */     md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */ 
/*  4182 */     if (!this.statementRecoveryActivated) problemReporter().abstractMethodNeedingNoBody(md); 
/*       */   }
/*       */ 
/*       */   protected void consumeLabel()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeLeftParen() {
/*  4189 */     pushOnIntStack(this.lParenPos);
/*       */   }
/*       */ 
/*       */   protected void consumeLocalVariableDeclaration()
/*       */   {
/*  4206 */     int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
/*       */ 
/*  4209 */     int startIndex = this.astPtr - this.variablesCounter[this.nestedType] + 1;
/*  4210 */     System.arraycopy(
/*  4211 */       this.astStack, 
/*  4212 */       startIndex, 
/*  4213 */       this.astStack, 
/*  4214 */       startIndex - 1, 
/*  4215 */       variableDeclaratorsCounter);
/*  4216 */     this.astPtr -= 1;
/*  4217 */     this.astLengthStack[(--this.astLengthPtr)] = variableDeclaratorsCounter;
/*  4218 */     this.variablesCounter[this.nestedType] = 0;
/*       */   }
/*       */ 
/*       */   protected void consumeLocalVariableDeclarationStatement()
/*       */   {
/*  4224 */     this.realBlockStack[this.realBlockPtr] += 1;
/*       */ 
/*  4227 */     int variableDeclaratorsCounter = this.astLengthStack[this.astLengthPtr];
/*  4228 */     for (int i = variableDeclaratorsCounter - 1; i >= 0; i--) {
/*  4229 */       LocalDeclaration localDeclaration = (LocalDeclaration)this.astStack[(this.astPtr - i)];
/*  4230 */       localDeclaration.declarationSourceEnd = this.endStatementPosition;
/*  4231 */       localDeclaration.declarationEnd = this.endStatementPosition;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeMarkerAnnotation()
/*       */   {
/*  4237 */     MarkerAnnotation markerAnnotation = null;
/*       */ 
/*  4239 */     int oldIndex = this.identifierPtr;
/*       */ 
/*  4241 */     TypeReference typeReference = getAnnotationType();
/*  4242 */     markerAnnotation = new MarkerAnnotation(typeReference, this.intStack[(this.intPtr--)]);
/*  4243 */     markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
/*  4244 */     pushOnExpressionStack(markerAnnotation);
/*  4245 */     if ((!this.statementRecoveryActivated) && 
/*  4246 */       (this.options.sourceLevel < 3211264L) && 
/*  4247 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  4248 */       problemReporter().invalidUsageOfAnnotation(markerAnnotation);
/*       */     }
/*  4250 */     this.recordStringLiterals = true;
/*       */ 
/*  4252 */     if ((this.currentElement != null) && ((this.currentElement instanceof RecoveredAnnotation)))
/*  4253 */       this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(markerAnnotation, oldIndex);
/*       */   }
/*       */ 
/*       */   protected void consumeMemberValueArrayInitializer()
/*       */   {
/*  4259 */     arrayInitializer(this.expressionLengthStack[(this.expressionLengthPtr--)]);
/*       */   }
/*       */   protected void consumeMemberValueAsName() {
/*  4262 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*       */   }
/*       */ 
/*       */   protected void consumeMemberValuePair() {
/*  4266 */     char[] simpleName = this.identifierStack[this.identifierPtr];
/*  4267 */     long position = this.identifierPositionStack[(this.identifierPtr--)];
/*  4268 */     this.identifierLengthPtr -= 1;
/*  4269 */     int end = (int)position;
/*  4270 */     int start = (int)(position >>> 32);
/*  4271 */     Expression value = this.expressionStack[(this.expressionPtr--)];
/*  4272 */     this.expressionLengthPtr -= 1;
/*  4273 */     MemberValuePair memberValuePair = new MemberValuePair(simpleName, start, end, value);
/*  4274 */     pushOnAstStack(memberValuePair);
/*       */ 
/*  4276 */     if ((this.currentElement != null) && ((this.currentElement instanceof RecoveredAnnotation))) {
/*  4277 */       RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
/*       */ 
/*  4279 */       recoveredAnnotation.setKind(1);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeMemberValuePairs() {
/*  4284 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeMemberValues() {
/*  4288 */     concatExpressionLists();
/*       */   }
/*       */ 
/*       */   protected void consumeMethodBody() {
/*  4292 */     this.nestedMethod[this.nestedType] -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeMethodDeclaration(boolean isNotAbstract)
/*       */   {
/*  4309 */     if (isNotAbstract)
/*       */     {
/*  4311 */       this.intPtr -= 1;
/*  4312 */       this.intPtr -= 1;
/*       */     }
/*       */ 
/*  4315 */     int explicitDeclarations = 0;
/*  4316 */     Statement[] statements = (Statement[])null;
/*  4317 */     if (isNotAbstract)
/*       */     {
/*  4319 */       explicitDeclarations = this.realBlockStack[(this.realBlockPtr--)];
/*       */       int length;
/*  4320 */       if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0) {
/*  4321 */         if (this.options.ignoreMethodBodies)
/*  4322 */           this.astPtr -= length;
/*       */         else {
/*  4324 */           System.arraycopy(
/*  4325 */             this.astStack, 
/*  4326 */             this.astPtr -= length + 1, 
/*  4327 */             statements = new Statement[length], 
/*  4328 */             0, 
/*  4329 */             length);
/*       */         }
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  4335 */     MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
/*  4336 */     md.statements = statements;
/*  4337 */     md.explicitDeclarations = explicitDeclarations;
/*       */ 
/*  4341 */     if (!isNotAbstract)
/*  4342 */       md.modifiers |= 16777216;
/*  4343 */     else if (((!this.diet) || (this.dietInt != 0)) && (statements == null) && (!containsComment(md.bodyStart, this.endPosition))) {
/*  4344 */       md.bits |= 8;
/*       */     }
/*       */ 
/*  4348 */     md.bodyEnd = this.endPosition;
/*  4349 */     md.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeMethodHeader()
/*       */   {
/*  4358 */     AbstractMethodDeclaration method = (AbstractMethodDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  4360 */     if (this.currentToken == 69) {
/*  4361 */       method.bodyStart = this.scanner.currentPosition;
/*       */     }
/*       */ 
/*  4364 */     if (this.currentElement != null)
/*       */     {
/*  4371 */       if (this.currentToken == 27) {
/*  4372 */         method.modifiers |= 16777216;
/*  4373 */         method.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*  4374 */         method.bodyEnd = (this.scanner.currentPosition - 1);
/*  4375 */         if ((this.currentElement.parseTree() == method) && (this.currentElement.parent != null))
/*  4376 */           this.currentElement = this.currentElement.parent;
/*       */       }
/*  4378 */       else if ((this.currentToken == 69) && 
/*  4379 */         ((this.currentElement instanceof RecoveredMethod)) && 
/*  4380 */         (((RecoveredMethod)this.currentElement).methodDeclaration != method)) {
/*  4381 */         this.ignoreNextOpeningBrace = true;
/*  4382 */         this.currentElement.bracketBalance += 1;
/*       */       }
/*       */ 
/*  4385 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeMethodHeaderDefaultValue() {
/*  4390 */     MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  4393 */     int length = this.expressionLengthStack[(this.expressionLengthPtr--)];
/*  4394 */     if (length == 1) {
/*  4395 */       this.intPtr -= 1;
/*  4396 */       this.intPtr -= 1;
/*  4397 */       if (md.isAnnotationMethod()) {
/*  4398 */         ((AnnotationMethodDeclaration)md).defaultValue = this.expressionStack[this.expressionPtr];
/*  4399 */         md.modifiers |= 131072;
/*       */       }
/*  4401 */       this.expressionPtr -= 1;
/*  4402 */       this.recordStringLiterals = true;
/*       */     }
/*       */ 
/*  4405 */     if ((this.currentElement != null) && 
/*  4406 */       (md.isAnnotationMethod()))
/*  4407 */       this.currentElement.updateSourceEndIfNecessary(((AnnotationMethodDeclaration)md).defaultValue.sourceEnd);
/*       */   }
/*       */ 
/*       */   protected void consumeMethodHeaderExtendedDims()
/*       */   {
/*  4414 */     MethodDeclaration md = (MethodDeclaration)this.astStack[this.astPtr];
/*  4415 */     int extendedDims = this.intStack[(this.intPtr--)];
/*  4416 */     if (md.isAnnotationMethod()) {
/*  4417 */       ((AnnotationMethodDeclaration)md).extendedDimensions = extendedDims;
/*       */     }
/*  4419 */     if (extendedDims != 0) {
/*  4420 */       TypeReference returnType = md.returnType;
/*  4421 */       md.sourceEnd = this.endPosition;
/*  4422 */       int dims = returnType.dimensions() + extendedDims;
/*  4423 */       md.returnType = copyDims(returnType, dims);
/*  4424 */       if (this.currentToken == 69) {
/*  4425 */         md.bodyStart = (this.endPosition + 1);
/*       */       }
/*       */ 
/*  4428 */       if (this.currentElement != null)
/*  4429 */         this.lastCheckPoint = md.bodyStart;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeMethodHeaderName(boolean isAnnotationMethod)
/*       */   {
/*  4437 */     MethodDeclaration md = null;
/*  4438 */     if (isAnnotationMethod) {
/*  4439 */       md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
/*  4440 */       this.recordStringLiterals = false;
/*       */     } else {
/*  4442 */       md = new MethodDeclaration(this.compilationUnit.compilationResult);
/*       */     }
/*       */ 
/*  4446 */     md.selector = this.identifierStack[this.identifierPtr];
/*  4447 */     long selectorSource = this.identifierPositionStack[(this.identifierPtr--)];
/*  4448 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  4450 */     md.returnType = getTypeReference(this.intStack[(this.intPtr--)]);
/*       */ 
/*  4452 */     md.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  4453 */     md.modifiers = this.intStack[(this.intPtr--)];
/*       */     int length;
/*  4456 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  4457 */       System.arraycopy(
/*  4458 */         this.expressionStack, 
/*  4459 */         this.expressionPtr -= length + 1, 
/*  4460 */         md.annotations = new Annotation[length], 
/*  4461 */         0, 
/*  4462 */         length);
/*       */     }
/*       */ 
/*  4465 */     md.javadoc = this.javadoc;
/*  4466 */     this.javadoc = null;
/*       */ 
/*  4469 */     md.sourceStart = (int)(selectorSource >>> 32);
/*  4470 */     pushOnAstStack(md);
/*  4471 */     md.sourceEnd = this.lParenPos;
/*  4472 */     md.bodyStart = (this.lParenPos + 1);
/*  4473 */     this.listLength = 0;
/*       */ 
/*  4476 */     if (this.currentElement != null)
/*  4477 */       if (((this.currentElement instanceof RecoveredType)) || 
/*  4479 */         (Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == 
/*  4480 */         Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr))) {
/*  4481 */         this.lastCheckPoint = md.bodyStart;
/*  4482 */         this.currentElement = this.currentElement.add(md, 0);
/*  4483 */         this.lastIgnoredToken = -1;
/*       */       } else {
/*  4485 */         this.lastCheckPoint = md.sourceStart;
/*  4486 */         this.restartRecovery = true;
/*       */       }
/*       */   }
/*       */ 
/*       */   protected void consumeMethodHeaderNameWithTypeParameters(boolean isAnnotationMethod)
/*       */   {
/*  4494 */     MethodDeclaration md = null;
/*  4495 */     if (isAnnotationMethod) {
/*  4496 */       md = new AnnotationMethodDeclaration(this.compilationUnit.compilationResult);
/*  4497 */       this.recordStringLiterals = false;
/*       */     } else {
/*  4499 */       md = new MethodDeclaration(this.compilationUnit.compilationResult);
/*       */     }
/*       */ 
/*  4503 */     md.selector = this.identifierStack[this.identifierPtr];
/*  4504 */     long selectorSource = this.identifierPositionStack[(this.identifierPtr--)];
/*  4505 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  4507 */     md.returnType = getTypeReference(this.intStack[(this.intPtr--)]);
/*       */ 
/*  4510 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  4511 */     this.genericsPtr -= length;
/*  4512 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, md.typeParameters = new TypeParameter[length], 0, length);
/*       */ 
/*  4515 */     md.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  4516 */     md.modifiers = this.intStack[(this.intPtr--)];
/*       */ 
/*  4518 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  4519 */       System.arraycopy(
/*  4520 */         this.expressionStack, 
/*  4521 */         this.expressionPtr -= length + 1, 
/*  4522 */         md.annotations = new Annotation[length], 
/*  4523 */         0, 
/*  4524 */         length);
/*       */     }
/*       */ 
/*  4527 */     md.javadoc = this.javadoc;
/*  4528 */     this.javadoc = null;
/*       */ 
/*  4531 */     md.sourceStart = (int)(selectorSource >>> 32);
/*  4532 */     pushOnAstStack(md);
/*  4533 */     md.sourceEnd = this.lParenPos;
/*  4534 */     md.bodyStart = (this.lParenPos + 1);
/*  4535 */     this.listLength = 0;
/*       */ 
/*  4538 */     if (this.currentElement != null)
/*       */     {
/*       */       boolean isType;
/*  4540 */       if (((isType = this.currentElement instanceof RecoveredType)) || 
/*  4542 */         (Util.getLineNumber(md.returnType.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr) == 
/*  4543 */         Util.getLineNumber(md.sourceStart, this.scanner.lineEnds, 0, this.scanner.linePtr))) {
/*  4544 */         if (isType) {
/*  4545 */           ((RecoveredType)this.currentElement).pendingTypeParameters = null;
/*       */         }
/*  4547 */         this.lastCheckPoint = md.bodyStart;
/*  4548 */         this.currentElement = this.currentElement.add(md, 0);
/*  4549 */         this.lastIgnoredToken = -1;
/*       */       } else {
/*  4551 */         this.lastCheckPoint = md.sourceStart;
/*  4552 */         this.restartRecovery = true;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeMethodHeaderRightParen() {
/*  4558 */     int length = this.astLengthStack[(this.astLengthPtr--)];
/*  4559 */     this.astPtr -= length;
/*  4560 */     AbstractMethodDeclaration md = (AbstractMethodDeclaration)this.astStack[this.astPtr];
/*  4561 */     md.sourceEnd = this.rParenPos;
/*       */ 
/*  4563 */     if (length != 0) {
/*  4564 */       System.arraycopy(
/*  4565 */         this.astStack, 
/*  4566 */         this.astPtr + 1, 
/*  4567 */         md.arguments = new Argument[length], 
/*  4568 */         0, 
/*  4569 */         length);
/*       */     }
/*  4571 */     md.bodyStart = (this.rParenPos + 1);
/*  4572 */     this.listLength = 0;
/*       */ 
/*  4574 */     if (this.currentElement != null) {
/*  4575 */       this.lastCheckPoint = md.bodyStart;
/*  4576 */       if (this.currentElement.parseTree() == md) return;
/*       */ 
/*  4579 */       if ((md.isConstructor()) && (
/*  4580 */         (length != 0) || 
/*  4581 */         (this.currentToken == 69) || 
/*  4582 */         (this.currentToken == 105))) {
/*  4583 */         this.currentElement = this.currentElement.add(md, 0);
/*  4584 */         this.lastIgnoredToken = -1;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeMethodHeaderThrowsClause()
/*       */   {
/*  4591 */     int length = this.astLengthStack[(this.astLengthPtr--)];
/*  4592 */     this.astPtr -= length;
/*  4593 */     AbstractMethodDeclaration md = (AbstractMethodDeclaration)this.astStack[this.astPtr];
/*  4594 */     System.arraycopy(
/*  4595 */       this.astStack, 
/*  4596 */       this.astPtr + 1, 
/*  4597 */       md.thrownExceptions = new TypeReference[length], 
/*  4598 */       0, 
/*  4599 */       length);
/*  4600 */     md.sourceEnd = md.thrownExceptions[(length - 1)].sourceEnd;
/*  4601 */     md.bodyStart = (md.thrownExceptions[(length - 1)].sourceEnd + 1);
/*  4602 */     this.listLength = 0;
/*       */ 
/*  4604 */     if (this.currentElement != null)
/*  4605 */       this.lastCheckPoint = md.bodyStart;
/*       */   }
/*       */ 
/*       */   protected void consumeMethodInvocationName()
/*       */   {
/*  4613 */     MessageSend m = newMessageSend();
/*  4614 */     m.sourceEnd = this.rParenPos;
/*  4615 */     m.sourceStart = 
/*  4616 */       (int)((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
/*  4617 */     m.selector = this.identifierStack[(this.identifierPtr--)];
/*  4618 */     if (this.identifierLengthStack[this.identifierLengthPtr] == 1) {
/*  4619 */       m.receiver = ThisReference.implicitThis();
/*  4620 */       this.identifierLengthPtr -= 1;
/*       */     } else {
/*  4622 */       this.identifierLengthStack[this.identifierLengthPtr] -= 1;
/*  4623 */       m.receiver = getUnspecifiedReference();
/*  4624 */       m.sourceStart = m.receiver.sourceStart;
/*       */     }
/*  4626 */     pushOnExpressionStack(m);
/*       */   }
/*       */ 
/*       */   protected void consumeMethodInvocationNameWithTypeArguments()
/*       */   {
/*  4633 */     MessageSend m = newMessageSendWithTypeArguments();
/*  4634 */     m.sourceEnd = this.rParenPos;
/*  4635 */     m.sourceStart = 
/*  4636 */       (int)((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
/*  4637 */     m.selector = this.identifierStack[(this.identifierPtr--)];
/*  4638 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  4641 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  4642 */     this.genericsPtr -= length;
/*  4643 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
/*  4644 */     this.intPtr -= 1;
/*       */ 
/*  4646 */     m.receiver = getUnspecifiedReference();
/*  4647 */     m.sourceStart = m.receiver.sourceStart;
/*  4648 */     pushOnExpressionStack(m);
/*       */   }
/*       */ 
/*       */   protected void consumeMethodInvocationPrimary()
/*       */   {
/*  4654 */     MessageSend m = newMessageSend();
/*  4655 */     m.sourceStart = 
/*  4656 */       (int)((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
/*  4657 */     m.selector = this.identifierStack[(this.identifierPtr--)];
/*  4658 */     this.identifierLengthPtr -= 1;
/*  4659 */     m.receiver = this.expressionStack[this.expressionPtr];
/*  4660 */     m.sourceStart = m.receiver.sourceStart;
/*  4661 */     m.sourceEnd = this.rParenPos;
/*  4662 */     this.expressionStack[this.expressionPtr] = m;
/*       */   }
/*       */ 
/*       */   protected void consumeMethodInvocationPrimaryWithTypeArguments()
/*       */   {
/*  4668 */     MessageSend m = newMessageSendWithTypeArguments();
/*  4669 */     m.sourceStart = 
/*  4670 */       (int)((m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr]) >>> 32);
/*  4671 */     m.selector = this.identifierStack[(this.identifierPtr--)];
/*  4672 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  4675 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  4676 */     this.genericsPtr -= length;
/*  4677 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
/*  4678 */     this.intPtr -= 1;
/*       */ 
/*  4680 */     m.receiver = this.expressionStack[this.expressionPtr];
/*  4681 */     m.sourceStart = m.receiver.sourceStart;
/*  4682 */     m.sourceEnd = this.rParenPos;
/*  4683 */     this.expressionStack[this.expressionPtr] = m;
/*       */   }
/*       */ 
/*       */   protected void consumeMethodInvocationSuper()
/*       */   {
/*  4688 */     MessageSend m = newMessageSend();
/*  4689 */     m.sourceStart = this.intStack[(this.intPtr--)];
/*  4690 */     m.sourceEnd = this.rParenPos;
/*  4691 */     m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
/*  4692 */     m.selector = this.identifierStack[(this.identifierPtr--)];
/*  4693 */     this.identifierLengthPtr -= 1;
/*  4694 */     m.receiver = new SuperReference(m.sourceStart, this.endPosition);
/*  4695 */     pushOnExpressionStack(m);
/*       */   }
/*       */ 
/*       */   protected void consumeMethodInvocationSuperWithTypeArguments()
/*       */   {
/*  4700 */     MessageSend m = newMessageSendWithTypeArguments();
/*  4701 */     this.intPtr -= 1;
/*  4702 */     m.sourceEnd = this.rParenPos;
/*  4703 */     m.nameSourcePosition = this.identifierPositionStack[this.identifierPtr];
/*  4704 */     m.selector = this.identifierStack[(this.identifierPtr--)];
/*  4705 */     this.identifierLengthPtr -= 1;
/*       */ 
/*  4708 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  4709 */     this.genericsPtr -= length;
/*  4710 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, m.typeArguments = new TypeReference[length], 0, length);
/*  4711 */     m.sourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  4713 */     m.receiver = new SuperReference(m.sourceStart, this.endPosition);
/*  4714 */     pushOnExpressionStack(m);
/*       */   }
/*       */   protected void consumeModifiers() {
/*  4717 */     int savedModifiersSourceStart = this.modifiersSourceStart;
/*  4718 */     checkComment();
/*  4719 */     pushOnIntStack(this.modifiers);
/*  4720 */     if (this.modifiersSourceStart >= savedModifiersSourceStart) {
/*  4721 */       this.modifiersSourceStart = savedModifiersSourceStart;
/*       */     }
/*  4723 */     pushOnIntStack(this.modifiersSourceStart);
/*  4724 */     resetModifiers();
/*       */   }
/*       */   protected void consumeModifiers2() {
/*  4727 */     this.expressionLengthStack[(this.expressionLengthPtr - 1)] += this.expressionLengthStack[(this.expressionLengthPtr--)];
/*       */   }
/*       */   protected void consumeNameArrayType() {
/*  4730 */     pushOnGenericsLengthStack(0);
/*  4731 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*       */   }
/*       */ 
/*       */   protected void consumeNestedMethod() {
/*  4735 */     jumpOverMethodBody();
/*  4736 */     this.nestedMethod[this.nestedType] += 1;
/*  4737 */     pushOnIntStack(this.scanner.currentPosition);
/*  4738 */     consumeOpenBlock();
/*       */   }
/*       */ 
/*       */   protected void consumeNestedType() {
/*  4742 */     int length = this.nestedMethod.length;
/*  4743 */     if (++this.nestedType >= length) {
/*  4744 */       System.arraycopy(
/*  4745 */         this.nestedMethod, 0, 
/*  4746 */         this.nestedMethod = new int[length + 30], 0, 
/*  4747 */         length);
/*       */ 
/*  4749 */       System.arraycopy(
/*  4750 */         this.variablesCounter, 0, 
/*  4751 */         this.variablesCounter = new int[length + 30], 0, 
/*  4752 */         length);
/*       */     }
/*  4754 */     this.nestedMethod[this.nestedType] = 0;
/*  4755 */     this.variablesCounter[this.nestedType] = 0;
/*       */   }
/*       */ 
/*       */   protected void consumeNormalAnnotation() {
/*  4759 */     NormalAnnotation normalAnnotation = null;
/*       */ 
/*  4761 */     int oldIndex = this.identifierPtr;
/*       */ 
/*  4763 */     TypeReference typeReference = getAnnotationType();
/*  4764 */     normalAnnotation = new NormalAnnotation(typeReference, this.intStack[(this.intPtr--)]);
/*       */     int length;
/*  4766 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0) {
/*  4767 */       System.arraycopy(
/*  4768 */         this.astStack, 
/*  4769 */         this.astPtr -= length + 1, 
/*  4770 */         normalAnnotation.memberValuePairs = new MemberValuePair[length], 
/*  4771 */         0, 
/*  4772 */         length);
/*       */     }
/*  4774 */     normalAnnotation.declarationSourceEnd = this.rParenPos;
/*  4775 */     pushOnExpressionStack(normalAnnotation);
/*       */ 
/*  4777 */     if (this.currentElement != null) {
/*  4778 */       annotationRecoveryCheckPoint(normalAnnotation.sourceStart, normalAnnotation.declarationSourceEnd);
/*       */ 
/*  4780 */       if ((this.currentElement instanceof RecoveredAnnotation)) {
/*  4781 */         this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(normalAnnotation, oldIndex);
/*       */       }
/*       */     }
/*       */ 
/*  4785 */     if ((!this.statementRecoveryActivated) && 
/*  4786 */       (this.options.sourceLevel < 3211264L) && 
/*  4787 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  4788 */       problemReporter().invalidUsageOfAnnotation(normalAnnotation);
/*       */     }
/*  4790 */     this.recordStringLiterals = true;
/*       */   }
/*       */ 
/*       */   protected void consumeOneDimLoop() {
/*  4794 */     this.dimensions += 1;
/*       */   }
/*       */ 
/*       */   protected void consumeOnlySynchronized() {
/*  4798 */     pushOnIntStack(this.synchronizedBlockSourceStart);
/*  4799 */     resetModifiers();
/*  4800 */     this.expressionLengthPtr -= 1;
/*       */   }
/*       */   protected void consumeOnlyTypeArguments() {
/*  4803 */     if ((!this.statementRecoveryActivated) && 
/*  4804 */       (this.options.sourceLevel < 3211264L) && 
/*  4805 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  4806 */       int length = this.genericsLengthStack[this.genericsLengthPtr];
/*  4807 */       problemReporter().invalidUsageOfTypeArguments(
/*  4808 */         (TypeReference)this.genericsStack[(this.genericsPtr - length + 1)], 
/*  4809 */         (TypeReference)this.genericsStack[this.genericsPtr]);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeOnlyTypeArgumentsForCastExpression()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumeOpenBlock() {
/*  4818 */     pushOnIntStack(this.scanner.startPosition);
/*  4819 */     int stackLength = this.realBlockStack.length;
/*  4820 */     if (++this.realBlockPtr >= stackLength) {
/*  4821 */       System.arraycopy(
/*  4822 */         this.realBlockStack, 0, 
/*  4823 */         this.realBlockStack = new int[stackLength + 255], 0, 
/*  4824 */         stackLength);
/*       */     }
/*  4826 */     this.realBlockStack[this.realBlockPtr] = 0;
/*       */   }
/*       */ 
/*       */   protected void consumePackageComment() {
/*  4830 */     if (this.options.sourceLevel >= 3211264L) {
/*  4831 */       checkComment();
/*  4832 */       resetModifiers();
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumePackageDeclaration()
/*       */   {
/*  4840 */     ImportReference impt = this.compilationUnit.currentPackage;
/*  4841 */     this.compilationUnit.javadoc = this.javadoc;
/*  4842 */     this.javadoc = null;
/*       */ 
/*  4844 */     impt.declarationEnd = this.endStatementPosition;
/*  4845 */     impt.declarationSourceEnd = flushCommentsDefinedPriorTo(impt.declarationSourceEnd);
/*       */   }
/*       */ 
/*       */   protected void consumePackageDeclarationName()
/*       */   {
/*       */     int length;
/*  4854 */     char[][] tokens = 
/*  4855 */       new char[length = this.identifierLengthStack[(this.identifierLengthPtr--)]][];
/*  4856 */     this.identifierPtr -= length;
/*  4857 */     long[] positions = new long[length];
/*  4858 */     System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
/*  4859 */     System.arraycopy(
/*  4860 */       this.identifierPositionStack, 
/*  4861 */       this.identifierPtr--, 
/*  4862 */       positions, 
/*  4863 */       0, 
/*  4864 */       length);
/*       */ 
/*  4866 */     ImportReference impt = new ImportReference(tokens, positions, true, 0);
/*  4867 */     this.compilationUnit.currentPackage = impt;
/*       */ 
/*  4869 */     if (this.currentToken == 27)
/*  4870 */       impt.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*       */     else {
/*  4872 */       impt.declarationSourceEnd = impt.sourceEnd;
/*       */     }
/*  4874 */     impt.declarationEnd = impt.declarationSourceEnd;
/*       */ 
/*  4876 */     impt.declarationSourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  4879 */     if (this.javadoc != null) {
/*  4880 */       impt.declarationSourceStart = this.javadoc.sourceStart;
/*       */     }
/*       */ 
/*  4884 */     if (this.currentElement != null) {
/*  4885 */       this.lastCheckPoint = (impt.declarationSourceEnd + 1);
/*  4886 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumePackageDeclarationNameWithModifiers()
/*       */   {
/*       */     int length;
/*  4896 */     char[][] tokens = 
/*  4897 */       new char[length = this.identifierLengthStack[(this.identifierLengthPtr--)]][];
/*  4898 */     this.identifierPtr -= length;
/*  4899 */     long[] positions = new long[length];
/*  4900 */     System.arraycopy(this.identifierStack, ++this.identifierPtr, tokens, 0, length);
/*  4901 */     System.arraycopy(
/*  4902 */       this.identifierPositionStack, 
/*  4903 */       this.identifierPtr--, 
/*  4904 */       positions, 
/*  4905 */       0, 
/*  4906 */       length);
/*       */ 
/*  4908 */     int packageModifiersSourceStart = this.intStack[(this.intPtr--)];
/*  4909 */     int packageModifiers = this.intStack[(this.intPtr--)];
/*       */ 
/*  4911 */     ImportReference impt = new ImportReference(tokens, positions, true, packageModifiers);
/*  4912 */     this.compilationUnit.currentPackage = impt;
/*       */ 
/*  4914 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  4915 */       System.arraycopy(
/*  4916 */         this.expressionStack, 
/*  4917 */         this.expressionPtr -= length + 1, 
/*  4918 */         impt.annotations = new Annotation[length], 
/*  4919 */         0, 
/*  4920 */         length);
/*  4921 */       impt.declarationSourceStart = packageModifiersSourceStart;
/*  4922 */       this.intPtr -= 1;
/*       */     } else {
/*  4924 */       impt.declarationSourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  4926 */       if (this.javadoc != null) {
/*  4927 */         impt.declarationSourceStart = this.javadoc.sourceStart;
/*       */       }
/*       */     }
/*       */ 
/*  4931 */     if (this.currentToken == 27)
/*  4932 */       impt.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*       */     else {
/*  4934 */       impt.declarationSourceEnd = impt.sourceEnd;
/*       */     }
/*  4936 */     impt.declarationEnd = impt.declarationSourceEnd;
/*       */ 
/*  4939 */     if (this.currentElement != null) {
/*  4940 */       this.lastCheckPoint = (impt.declarationSourceEnd + 1);
/*  4941 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumePostfixExpression() {
/*  4946 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArray() {
/*  4950 */     Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
/*  4951 */     updateSourcePosition(parenthesizedExpression);
/*  4952 */     int numberOfParenthesis = (parenthesizedExpression.bits & 0x1FE00000) >> 21;
/*  4953 */     parenthesizedExpression.bits &= -534773761;
/*  4954 */     parenthesizedExpression.bits |= numberOfParenthesis + 1 << 21;
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayArrayType() {
/*  4958 */     this.intPtr -= 1;
/*       */ 
/*  4960 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*  4961 */     pushOnGenericsLengthStack(0);
/*       */ 
/*  4963 */     pushOnExpressionStack(
/*  4964 */       new ClassLiteralAccess(this.intStack[(this.intPtr--)], getTypeReference(this.intStack[(this.intPtr--)])));
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayName() {
/*  4968 */     this.intPtr -= 1;
/*       */ 
/*  4971 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*  4972 */     pushOnGenericsLengthStack(0);
/*  4973 */     TypeReference typeReference = getTypeReference(0);
/*       */ 
/*  4975 */     pushOnExpressionStack(
/*  4976 */       new ClassLiteralAccess(this.intStack[(this.intPtr--)], typeReference));
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayNameSuper()
/*       */   {
/*  4981 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*  4982 */     pushOnGenericsLengthStack(0);
/*  4983 */     TypeReference typeReference = getTypeReference(0);
/*       */ 
/*  4985 */     pushOnExpressionStack(
/*  4986 */       new QualifiedSuperReference(
/*  4987 */       typeReference, 
/*  4988 */       this.intStack[(this.intPtr--)], 
/*  4989 */       this.endPosition));
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayNameThis()
/*       */   {
/*  4994 */     pushOnGenericsIdentifiersLengthStack(this.identifierLengthStack[this.identifierLengthPtr]);
/*  4995 */     pushOnGenericsLengthStack(0);
/*       */ 
/*  4997 */     TypeReference typeReference = getTypeReference(0);
/*       */ 
/*  4999 */     pushOnExpressionStack(
/*  5000 */       new QualifiedThisReference(
/*  5001 */       typeReference, 
/*  5002 */       this.intStack[(this.intPtr--)], 
/*  5003 */       this.endPosition));
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayPrimitiveArrayType() {
/*  5007 */     this.intPtr -= 1;
/*  5008 */     pushOnExpressionStack(
/*  5009 */       new ClassLiteralAccess(this.intStack[(this.intPtr--)], getTypeReference(this.intStack[(this.intPtr--)])));
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayPrimitiveType() {
/*  5013 */     this.intPtr -= 1;
/*  5014 */     pushOnExpressionStack(
/*  5015 */       new ClassLiteralAccess(this.intStack[(this.intPtr--)], getTypeReference(0)));
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayThis() {
/*  5019 */     pushOnExpressionStack(new ThisReference(this.intStack[(this.intPtr--)], this.endPosition));
/*       */   }
/*       */ 
/*       */   protected void consumePrimaryNoNewArrayWithName() {
/*  5023 */     pushOnExpressionStack(getUnspecifiedReferenceOptimized());
/*  5024 */     Expression parenthesizedExpression = this.expressionStack[this.expressionPtr];
/*  5025 */     updateSourcePosition(parenthesizedExpression);
/*  5026 */     int numberOfParenthesis = (parenthesizedExpression.bits & 0x1FE00000) >> 21;
/*  5027 */     parenthesizedExpression.bits &= -534773761;
/*  5028 */     parenthesizedExpression.bits |= numberOfParenthesis + 1 << 21;
/*       */   }
/*       */ 
/*       */   protected void consumePrimitiveArrayType()
/*       */   {
/*       */   }
/*       */ 
/*       */   protected void consumePrimitiveType() {
/*  5036 */     pushOnIntStack(0);
/*       */   }
/*       */   protected void consumePushLeftBrace() {
/*  5039 */     pushOnIntStack(this.endPosition);
/*       */   }
/*       */   protected void consumePushModifiers() {
/*  5042 */     pushOnIntStack(this.modifiers);
/*  5043 */     pushOnIntStack(this.modifiersSourceStart);
/*  5044 */     resetModifiers();
/*  5045 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */   protected void consumePushModifiersForHeader() {
/*  5048 */     checkComment();
/*  5049 */     pushOnIntStack(this.modifiers);
/*  5050 */     pushOnIntStack(this.modifiersSourceStart);
/*  5051 */     resetModifiers();
/*  5052 */     pushOnExpressionStackLengthStack(0);
/*       */   }
/*       */ 
/*       */   protected void consumePushPosition()
/*       */   {
/*  5057 */     pushOnIntStack(this.endPosition);
/*       */   }
/*       */   protected void consumePushRealModifiers() {
/*  5060 */     checkComment();
/*  5061 */     pushOnIntStack(this.modifiers);
/*  5062 */     pushOnIntStack(this.modifiersSourceStart);
/*  5063 */     resetModifiers();
/*       */   }
/*       */ 
/*       */   protected void consumeQualifiedName()
/*       */   {
/*  5070 */     this.identifierLengthStack[(--this.identifierLengthPtr)] += 1;
/*       */   }
/*       */ 
/*       */   protected void consumeRecoveryMethodHeaderName() {
/*  5074 */     boolean isAnnotationMethod = false;
/*  5075 */     if ((this.currentElement instanceof RecoveredType)) {
/*  5076 */       isAnnotationMethod = (((RecoveredType)this.currentElement).typeDeclaration.modifiers & 0x2000) != 0;
/*       */     } else {
/*  5078 */       RecoveredType recoveredType = this.currentElement.enclosingType();
/*  5079 */       if (recoveredType != null) {
/*  5080 */         isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & 0x2000) != 0;
/*       */       }
/*       */     }
/*  5083 */     consumeMethodHeaderName(isAnnotationMethod);
/*       */   }
/*       */ 
/*       */   protected void consumeRecoveryMethodHeaderNameWithTypeParameters() {
/*  5087 */     boolean isAnnotationMethod = false;
/*  5088 */     if ((this.currentElement instanceof RecoveredType)) {
/*  5089 */       isAnnotationMethod = (((RecoveredType)this.currentElement).typeDeclaration.modifiers & 0x2000) != 0;
/*       */     } else {
/*  5091 */       RecoveredType recoveredType = this.currentElement.enclosingType();
/*  5092 */       if (recoveredType != null) {
/*  5093 */         isAnnotationMethod = (recoveredType.typeDeclaration.modifiers & 0x2000) != 0;
/*       */       }
/*       */     }
/*  5096 */     consumeMethodHeaderNameWithTypeParameters(isAnnotationMethod);
/*       */   }
/*       */ 
/*       */   protected void consumeReduceImports()
/*       */   {
/*       */     int length;
/*  5101 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0) {
/*  5102 */       this.astPtr -= length;
/*  5103 */       System.arraycopy(
/*  5104 */         this.astStack, 
/*  5105 */         this.astPtr + 1, 
/*  5106 */         this.compilationUnit.imports = new ImportReference[length], 
/*  5107 */         0, 
/*  5108 */         length);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeReferenceType() {
/*  5112 */     pushOnIntStack(0);
/*       */   }
/*       */   protected void consumeReferenceType1() {
/*  5115 */     pushOnGenericsStack(getTypeReference(this.intStack[(this.intPtr--)]));
/*       */   }
/*       */   protected void consumeReferenceType2() {
/*  5118 */     pushOnGenericsStack(getTypeReference(this.intStack[(this.intPtr--)]));
/*       */   }
/*       */   protected void consumeReferenceType3() {
/*  5121 */     pushOnGenericsStack(getTypeReference(this.intStack[(this.intPtr--)]));
/*       */   }
/*       */ 
/*       */   protected void consumeRestoreDiet() {
/*  5125 */     this.dietInt -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeRightParen() {
/*  5129 */     pushOnIntStack(this.rParenPos);
/*       */   }
/*       */ 
/*       */   protected void consumeRule(int act) {
/*  5133 */     switch (act) {
/*       */     case 30:
/*  5135 */       consumePrimitiveType();
/*  5136 */       break;
/*       */     case 44:
/*  5139 */       consumeReferenceType();
/*  5140 */       break;
/*       */     case 48:
/*  5143 */       consumeClassOrInterfaceName();
/*  5144 */       break;
/*       */     case 49:
/*  5147 */       consumeClassOrInterface();
/*  5148 */       break;
/*       */     case 50:
/*  5151 */       consumeGenericType();
/*  5152 */       break;
/*       */     case 51:
/*  5155 */       consumeArrayTypeWithTypeArgumentsName();
/*  5156 */       break;
/*       */     case 52:
/*  5159 */       consumePrimitiveArrayType();
/*  5160 */       break;
/*       */     case 53:
/*  5163 */       consumeNameArrayType();
/*  5164 */       break;
/*       */     case 54:
/*  5167 */       consumeGenericTypeNameArrayType();
/*  5168 */       break;
/*       */     case 55:
/*  5171 */       consumeGenericTypeArrayType();
/*  5172 */       break;
/*       */     case 60:
/*  5175 */       consumeQualifiedName();
/*  5176 */       break;
/*       */     case 61:
/*  5179 */       consumeCompilationUnit();
/*  5180 */       break;
/*       */     case 62:
/*  5183 */       consumeInternalCompilationUnit();
/*  5184 */       break;
/*       */     case 63:
/*  5187 */       consumeInternalCompilationUnit();
/*  5188 */       break;
/*       */     case 64:
/*  5191 */       consumeInternalCompilationUnitWithTypes();
/*  5192 */       break;
/*       */     case 65:
/*  5195 */       consumeInternalCompilationUnitWithTypes();
/*  5196 */       break;
/*       */     case 66:
/*  5199 */       consumeInternalCompilationUnit();
/*  5200 */       break;
/*       */     case 67:
/*  5203 */       consumeInternalCompilationUnitWithTypes();
/*  5204 */       break;
/*       */     case 68:
/*  5207 */       consumeInternalCompilationUnitWithTypes();
/*  5208 */       break;
/*       */     case 69:
/*  5211 */       consumeEmptyInternalCompilationUnit();
/*  5212 */       break;
/*       */     case 70:
/*  5215 */       consumeReduceImports();
/*  5216 */       break;
/*       */     case 71:
/*  5219 */       consumeEnterCompilationUnit();
/*  5220 */       break;
/*       */     case 87:
/*  5223 */       consumeCatchHeader();
/*  5224 */       break;
/*       */     case 89:
/*  5227 */       consumeImportDeclarations();
/*  5228 */       break;
/*       */     case 91:
/*  5231 */       consumeTypeDeclarations();
/*  5232 */       break;
/*       */     case 92:
/*  5235 */       consumePackageDeclaration();
/*  5236 */       break;
/*       */     case 93:
/*  5239 */       consumePackageDeclarationNameWithModifiers();
/*  5240 */       break;
/*       */     case 94:
/*  5243 */       consumePackageDeclarationName();
/*  5244 */       break;
/*       */     case 95:
/*  5247 */       consumePackageComment();
/*  5248 */       break;
/*       */     case 100:
/*  5251 */       consumeImportDeclaration();
/*  5252 */       break;
/*       */     case 101:
/*  5255 */       consumeSingleTypeImportDeclarationName();
/*  5256 */       break;
/*       */     case 102:
/*  5259 */       consumeImportDeclaration();
/*  5260 */       break;
/*       */     case 103:
/*  5263 */       consumeTypeImportOnDemandDeclarationName();
/*  5264 */       break;
/*       */     case 106:
/*  5267 */       consumeEmptyTypeDeclaration();
/*  5268 */       break;
/*       */     case 110:
/*  5271 */       consumeModifiers2();
/*  5272 */       break;
/*       */     case 122:
/*  5275 */       consumeAnnotationAsModifier();
/*  5276 */       break;
/*       */     case 123:
/*  5279 */       consumeClassDeclaration();
/*  5280 */       break;
/*       */     case 124:
/*  5283 */       consumeClassHeader();
/*  5284 */       break;
/*       */     case 125:
/*  5287 */       consumeTypeHeaderNameWithTypeParameters();
/*  5288 */       break;
/*       */     case 127:
/*  5291 */       consumeClassHeaderName1();
/*  5292 */       break;
/*       */     case 128:
/*  5295 */       consumeClassHeaderExtends();
/*  5296 */       break;
/*       */     case 129:
/*  5299 */       consumeClassHeaderImplements();
/*  5300 */       break;
/*       */     case 131:
/*  5303 */       consumeInterfaceTypeList();
/*  5304 */       break;
/*       */     case 132:
/*  5307 */       consumeInterfaceType();
/*  5308 */       break;
/*       */     case 135:
/*  5311 */       consumeClassBodyDeclarations();
/*  5312 */       break;
/*       */     case 139:
/*  5315 */       consumeClassBodyDeclaration();
/*  5316 */       break;
/*       */     case 140:
/*  5319 */       consumeDiet();
/*  5320 */       break;
/*       */     case 141:
/*  5323 */       consumeClassBodyDeclaration();
/*  5324 */       break;
/*       */     case 142:
/*  5327 */       consumeCreateInitializer();
/*  5328 */       break;
/*       */     case 149:
/*  5331 */       consumeEmptyTypeDeclaration();
/*  5332 */       break;
/*       */     case 152:
/*  5335 */       consumeFieldDeclaration();
/*  5336 */       break;
/*       */     case 154:
/*  5339 */       consumeVariableDeclarators();
/*  5340 */       break;
/*       */     case 157:
/*  5343 */       consumeEnterVariable();
/*  5344 */       break;
/*       */     case 158:
/*  5347 */       consumeExitVariableWithInitialization();
/*  5348 */       break;
/*       */     case 159:
/*  5351 */       consumeExitVariableWithoutInitialization();
/*  5352 */       break;
/*       */     case 160:
/*  5355 */       consumeForceNoDiet();
/*  5356 */       break;
/*       */     case 161:
/*  5359 */       consumeRestoreDiet();
/*  5360 */       break;
/*       */     case 166:
/*  5364 */       consumeMethodDeclaration(true);
/*  5365 */       break;
/*       */     case 167:
/*  5369 */       consumeMethodDeclaration(false);
/*  5370 */       break;
/*       */     case 168:
/*  5373 */       consumeMethodHeader();
/*  5374 */       break;
/*       */     case 169:
/*  5377 */       consumeMethodHeaderNameWithTypeParameters(false);
/*  5378 */       break;
/*       */     case 170:
/*  5381 */       consumeMethodHeaderName(false);
/*  5382 */       break;
/*       */     case 171:
/*  5385 */       consumeMethodHeaderRightParen();
/*  5386 */       break;
/*       */     case 172:
/*  5389 */       consumeMethodHeaderExtendedDims();
/*  5390 */       break;
/*       */     case 173:
/*  5393 */       consumeMethodHeaderThrowsClause();
/*  5394 */       break;
/*       */     case 174:
/*  5397 */       consumeConstructorHeader();
/*  5398 */       break;
/*       */     case 175:
/*  5401 */       consumeConstructorHeaderNameWithTypeParameters();
/*  5402 */       break;
/*       */     case 176:
/*  5405 */       consumeConstructorHeaderName();
/*  5406 */       break;
/*       */     case 178:
/*  5409 */       consumeFormalParameterList();
/*  5410 */       break;
/*       */     case 179:
/*  5413 */       consumeFormalParameter(false);
/*  5414 */       break;
/*       */     case 180:
/*  5417 */       consumeFormalParameter(true);
/*  5418 */       break;
/*       */     case 182:
/*  5421 */       consumeClassTypeList();
/*  5422 */       break;
/*       */     case 183:
/*  5425 */       consumeClassTypeElt();
/*  5426 */       break;
/*       */     case 184:
/*  5429 */       consumeMethodBody();
/*  5430 */       break;
/*       */     case 185:
/*  5433 */       consumeNestedMethod();
/*  5434 */       break;
/*       */     case 186:
/*  5437 */       consumeStaticInitializer();
/*  5438 */       break;
/*       */     case 187:
/*  5441 */       consumeStaticOnly();
/*  5442 */       break;
/*       */     case 188:
/*  5445 */       consumeConstructorDeclaration();
/*  5446 */       break;
/*       */     case 189:
/*  5449 */       consumeInvalidConstructorDeclaration();
/*  5450 */       break;
/*       */     case 190:
/*  5453 */       consumeExplicitConstructorInvocation(0, 3);
/*  5454 */       break;
/*       */     case 191:
/*  5457 */       consumeExplicitConstructorInvocationWithTypeArguments(0, 3);
/*  5458 */       break;
/*       */     case 192:
/*  5461 */       consumeExplicitConstructorInvocation(0, 2);
/*  5462 */       break;
/*       */     case 193:
/*  5465 */       consumeExplicitConstructorInvocationWithTypeArguments(0, 2);
/*  5466 */       break;
/*       */     case 194:
/*  5469 */       consumeExplicitConstructorInvocation(1, 2);
/*  5470 */       break;
/*       */     case 195:
/*  5473 */       consumeExplicitConstructorInvocationWithTypeArguments(1, 2);
/*  5474 */       break;
/*       */     case 196:
/*  5477 */       consumeExplicitConstructorInvocation(2, 2);
/*  5478 */       break;
/*       */     case 197:
/*  5481 */       consumeExplicitConstructorInvocationWithTypeArguments(2, 2);
/*  5482 */       break;
/*       */     case 198:
/*  5485 */       consumeExplicitConstructorInvocation(1, 3);
/*  5486 */       break;
/*       */     case 199:
/*  5489 */       consumeExplicitConstructorInvocationWithTypeArguments(1, 3);
/*  5490 */       break;
/*       */     case 200:
/*  5493 */       consumeExplicitConstructorInvocation(2, 3);
/*  5494 */       break;
/*       */     case 201:
/*  5497 */       consumeExplicitConstructorInvocationWithTypeArguments(2, 3);
/*  5498 */       break;
/*       */     case 202:
/*  5501 */       consumeInterfaceDeclaration();
/*  5502 */       break;
/*       */     case 203:
/*  5505 */       consumeInterfaceHeader();
/*  5506 */       break;
/*       */     case 204:
/*  5509 */       consumeTypeHeaderNameWithTypeParameters();
/*  5510 */       break;
/*       */     case 206:
/*  5513 */       consumeInterfaceHeaderName1();
/*  5514 */       break;
/*       */     case 207:
/*  5517 */       consumeInterfaceHeaderExtends();
/*  5518 */       break;
/*       */     case 210:
/*  5521 */       consumeInterfaceMemberDeclarations();
/*  5522 */       break;
/*       */     case 211:
/*  5525 */       consumeEmptyTypeDeclaration();
/*  5526 */       break;
/*       */     case 213:
/*  5529 */       consumeInvalidMethodDeclaration();
/*  5530 */       break;
/*       */     case 214:
/*  5533 */       consumeInvalidConstructorDeclaration(true);
/*  5534 */       break;
/*       */     case 215:
/*  5537 */       consumeInvalidConstructorDeclaration(false);
/*  5538 */       break;
/*       */     case 223:
/*  5541 */       consumePushLeftBrace();
/*  5542 */       break;
/*       */     case 224:
/*  5545 */       consumeEmptyArrayInitializer();
/*  5546 */       break;
/*       */     case 225:
/*  5549 */       consumeArrayInitializer();
/*  5550 */       break;
/*       */     case 226:
/*  5553 */       consumeArrayInitializer();
/*  5554 */       break;
/*       */     case 228:
/*  5557 */       consumeVariableInitializers();
/*  5558 */       break;
/*       */     case 229:
/*  5561 */       consumeBlock();
/*  5562 */       break;
/*       */     case 230:
/*  5565 */       consumeOpenBlock();
/*  5566 */       break;
/*       */     case 232:
/*  5569 */       consumeBlockStatements();
/*  5570 */       break;
/*       */     case 236:
/*  5573 */       consumeInvalidInterfaceDeclaration();
/*  5574 */       break;
/*       */     case 237:
/*  5577 */       consumeInvalidAnnotationTypeDeclaration();
/*  5578 */       break;
/*       */     case 238:
/*  5581 */       consumeInvalidEnumDeclaration();
/*  5582 */       break;
/*       */     case 239:
/*  5585 */       consumeLocalVariableDeclarationStatement();
/*  5586 */       break;
/*       */     case 240:
/*  5589 */       consumeLocalVariableDeclaration();
/*  5590 */       break;
/*       */     case 241:
/*  5593 */       consumeLocalVariableDeclaration();
/*  5594 */       break;
/*       */     case 242:
/*  5597 */       consumePushModifiers();
/*  5598 */       break;
/*       */     case 243:
/*  5601 */       consumePushModifiersForHeader();
/*  5602 */       break;
/*       */     case 244:
/*  5605 */       consumePushRealModifiers();
/*  5606 */       break;
/*       */     case 270:
/*  5609 */       consumeEmptyStatement();
/*  5610 */       break;
/*       */     case 271:
/*  5613 */       consumeStatementLabel();
/*  5614 */       break;
/*       */     case 272:
/*  5617 */       consumeStatementLabel();
/*  5618 */       break;
/*       */     case 273:
/*  5621 */       consumeLabel();
/*  5622 */       break;
/*       */     case 274:
/*  5625 */       consumeExpressionStatement();
/*  5626 */       break;
/*       */     case 283:
/*  5629 */       consumeStatementIfNoElse();
/*  5630 */       break;
/*       */     case 284:
/*  5633 */       consumeStatementIfWithElse();
/*  5634 */       break;
/*       */     case 285:
/*  5637 */       consumeStatementIfWithElse();
/*  5638 */       break;
/*       */     case 286:
/*  5641 */       consumeStatementSwitch();
/*  5642 */       break;
/*       */     case 287:
/*  5645 */       consumeEmptySwitchBlock();
/*  5646 */       break;
/*       */     case 290:
/*  5649 */       consumeSwitchBlock();
/*  5650 */       break;
/*       */     case 292:
/*  5653 */       consumeSwitchBlockStatements();
/*  5654 */       break;
/*       */     case 293:
/*  5657 */       consumeSwitchBlockStatement();
/*  5658 */       break;
/*       */     case 295:
/*  5661 */       consumeSwitchLabels();
/*  5662 */       break;
/*       */     case 296:
/*  5665 */       consumeCaseLabel();
/*  5666 */       break;
/*       */     case 297:
/*  5669 */       consumeDefaultLabel();
/*  5670 */       break;
/*       */     case 298:
/*  5673 */       consumeStatementWhile();
/*  5674 */       break;
/*       */     case 299:
/*  5677 */       consumeStatementWhile();
/*  5678 */       break;
/*       */     case 300:
/*  5681 */       consumeStatementDo();
/*  5682 */       break;
/*       */     case 301:
/*  5685 */       consumeStatementFor();
/*  5686 */       break;
/*       */     case 302:
/*  5689 */       consumeStatementFor();
/*  5690 */       break;
/*       */     case 303:
/*  5693 */       consumeForInit();
/*  5694 */       break;
/*       */     case 307:
/*  5697 */       consumeStatementExpressionList();
/*  5698 */       break;
/*       */     case 308:
/*  5701 */       consumeSimpleAssertStatement();
/*  5702 */       break;
/*       */     case 309:
/*  5705 */       consumeAssertStatement();
/*  5706 */       break;
/*       */     case 310:
/*  5709 */       consumeStatementBreak();
/*  5710 */       break;
/*       */     case 311:
/*  5713 */       consumeStatementBreakWithLabel();
/*  5714 */       break;
/*       */     case 312:
/*  5717 */       consumeStatementContinue();
/*  5718 */       break;
/*       */     case 313:
/*  5721 */       consumeStatementContinueWithLabel();
/*  5722 */       break;
/*       */     case 314:
/*  5725 */       consumeStatementReturn();
/*  5726 */       break;
/*       */     case 315:
/*  5729 */       consumeStatementThrow();
/*  5730 */       break;
/*       */     case 316:
/*  5733 */       consumeStatementSynchronized();
/*  5734 */       break;
/*       */     case 317:
/*  5737 */       consumeOnlySynchronized();
/*  5738 */       break;
/*       */     case 318:
/*  5741 */       consumeStatementTry(false);
/*  5742 */       break;
/*       */     case 319:
/*  5745 */       consumeStatementTry(true);
/*  5746 */       break;
/*       */     case 321:
/*  5749 */       consumeExitTryBlock();
/*  5750 */       break;
/*       */     case 323:
/*  5753 */       consumeCatches();
/*  5754 */       break;
/*       */     case 324:
/*  5757 */       consumeStatementCatch();
/*  5758 */       break;
/*       */     case 326:
/*  5761 */       consumeLeftParen();
/*  5762 */       break;
/*       */     case 327:
/*  5765 */       consumeRightParen();
/*  5766 */       break;
/*       */     case 332:
/*  5769 */       consumePrimaryNoNewArrayThis();
/*  5770 */       break;
/*       */     case 333:
/*  5773 */       consumePrimaryNoNewArray();
/*  5774 */       break;
/*       */     case 334:
/*  5777 */       consumePrimaryNoNewArrayWithName();
/*  5778 */       break;
/*       */     case 337:
/*  5781 */       consumePrimaryNoNewArrayNameThis();
/*  5782 */       break;
/*       */     case 338:
/*  5785 */       consumePrimaryNoNewArrayNameSuper();
/*  5786 */       break;
/*       */     case 339:
/*  5789 */       consumePrimaryNoNewArrayName();
/*  5790 */       break;
/*       */     case 340:
/*  5793 */       consumePrimaryNoNewArrayArrayType();
/*  5794 */       break;
/*       */     case 341:
/*  5797 */       consumePrimaryNoNewArrayPrimitiveArrayType();
/*  5798 */       break;
/*       */     case 342:
/*  5801 */       consumePrimaryNoNewArrayPrimitiveType();
/*  5802 */       break;
/*       */     case 345:
/*  5805 */       consumeAllocationHeader();
/*  5806 */       break;
/*       */     case 346:
/*  5809 */       consumeClassInstanceCreationExpressionWithTypeArguments();
/*  5810 */       break;
/*       */     case 347:
/*  5813 */       consumeClassInstanceCreationExpression();
/*  5814 */       break;
/*       */     case 348:
/*  5817 */       consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
/*  5818 */       break;
/*       */     case 349:
/*  5821 */       consumeClassInstanceCreationExpressionQualified();
/*  5822 */       break;
/*       */     case 350:
/*  5825 */       consumeClassInstanceCreationExpressionQualified();
/*  5826 */       break;
/*       */     case 351:
/*  5829 */       consumeClassInstanceCreationExpressionQualifiedWithTypeArguments();
/*  5830 */       break;
/*       */     case 352:
/*  5833 */       consumeClassInstanceCreationExpressionName();
/*  5834 */       break;
/*       */     case 353:
/*  5837 */       consumeClassBodyopt();
/*  5838 */       break;
/*       */     case 355:
/*  5841 */       consumeEnterAnonymousClassBody(false);
/*  5842 */       break;
/*       */     case 356:
/*  5845 */       consumeClassBodyopt();
/*  5846 */       break;
/*       */     case 358:
/*  5849 */       consumeEnterAnonymousClassBody(true);
/*  5850 */       break;
/*       */     case 360:
/*  5853 */       consumeArgumentList();
/*  5854 */       break;
/*       */     case 361:
/*  5857 */       consumeArrayCreationHeader();
/*  5858 */       break;
/*       */     case 362:
/*  5861 */       consumeArrayCreationHeader();
/*  5862 */       break;
/*       */     case 363:
/*  5865 */       consumeArrayCreationExpressionWithoutInitializer();
/*  5866 */       break;
/*       */     case 364:
/*  5869 */       consumeArrayCreationExpressionWithInitializer();
/*  5870 */       break;
/*       */     case 365:
/*  5873 */       consumeArrayCreationExpressionWithoutInitializer();
/*  5874 */       break;
/*       */     case 366:
/*  5877 */       consumeArrayCreationExpressionWithInitializer();
/*  5878 */       break;
/*       */     case 368:
/*  5881 */       consumeDimWithOrWithOutExprs();
/*  5882 */       break;
/*       */     case 370:
/*  5885 */       consumeDimWithOrWithOutExpr();
/*  5886 */       break;
/*       */     case 371:
/*  5889 */       consumeDims();
/*  5890 */       break;
/*       */     case 374:
/*  5893 */       consumeOneDimLoop();
/*  5894 */       break;
/*       */     case 375:
/*  5897 */       consumeFieldAccess(false);
/*  5898 */       break;
/*       */     case 376:
/*  5901 */       consumeFieldAccess(true);
/*  5902 */       break;
/*       */     case 377:
/*  5905 */       consumeMethodInvocationName();
/*  5906 */       break;
/*       */     case 378:
/*  5909 */       consumeMethodInvocationNameWithTypeArguments();
/*  5910 */       break;
/*       */     case 379:
/*  5913 */       consumeMethodInvocationPrimaryWithTypeArguments();
/*  5914 */       break;
/*       */     case 380:
/*  5917 */       consumeMethodInvocationPrimary();
/*  5918 */       break;
/*       */     case 381:
/*  5921 */       consumeMethodInvocationSuperWithTypeArguments();
/*  5922 */       break;
/*       */     case 382:
/*  5925 */       consumeMethodInvocationSuper();
/*  5926 */       break;
/*       */     case 383:
/*  5929 */       consumeArrayAccess(true);
/*  5930 */       break;
/*       */     case 384:
/*  5933 */       consumeArrayAccess(false);
/*  5934 */       break;
/*       */     case 385:
/*  5937 */       consumeArrayAccess(false);
/*  5938 */       break;
/*       */     case 387:
/*  5941 */       consumePostfixExpression();
/*  5942 */       break;
/*       */     case 390:
/*  5945 */       consumeUnaryExpression(14, true);
/*  5946 */       break;
/*       */     case 391:
/*  5949 */       consumeUnaryExpression(13, true);
/*  5950 */       break;
/*       */     case 392:
/*  5953 */       consumePushPosition();
/*  5954 */       break;
/*       */     case 395:
/*  5957 */       consumeUnaryExpression(14);
/*  5958 */       break;
/*       */     case 396:
/*  5961 */       consumeUnaryExpression(13);
/*  5962 */       break;
/*       */     case 398:
/*  5965 */       consumeUnaryExpression(14, false);
/*  5966 */       break;
/*       */     case 399:
/*  5969 */       consumeUnaryExpression(13, false);
/*  5970 */       break;
/*       */     case 401:
/*  5973 */       consumeUnaryExpression(12);
/*  5974 */       break;
/*       */     case 402:
/*  5977 */       consumeUnaryExpression(11);
/*  5978 */       break;
/*       */     case 404:
/*  5981 */       consumeCastExpressionWithPrimitiveType();
/*  5982 */       break;
/*       */     case 405:
/*  5985 */       consumeCastExpressionWithGenericsArray();
/*  5986 */       break;
/*       */     case 406:
/*  5989 */       consumeCastExpressionWithQualifiedGenericsArray();
/*  5990 */       break;
/*       */     case 407:
/*  5993 */       consumeCastExpressionLL1();
/*  5994 */       break;
/*       */     case 408:
/*  5997 */       consumeCastExpressionWithNameArray();
/*  5998 */       break;
/*       */     case 409:
/*  6001 */       consumeOnlyTypeArgumentsForCastExpression();
/*  6002 */       break;
/*       */     case 410:
/*  6005 */       consumeInsideCastExpression();
/*  6006 */       break;
/*       */     case 411:
/*  6009 */       consumeInsideCastExpressionLL1();
/*  6010 */       break;
/*       */     case 412:
/*  6013 */       consumeInsideCastExpressionWithQualifiedGenerics();
/*  6014 */       break;
/*       */     case 414:
/*  6017 */       consumeBinaryExpression(15);
/*  6018 */       break;
/*       */     case 415:
/*  6021 */       consumeBinaryExpression(9);
/*  6022 */       break;
/*       */     case 416:
/*  6025 */       consumeBinaryExpression(16);
/*  6026 */       break;
/*       */     case 418:
/*  6029 */       consumeBinaryExpression(14);
/*  6030 */       break;
/*       */     case 419:
/*  6033 */       consumeBinaryExpression(13);
/*  6034 */       break;
/*       */     case 421:
/*  6037 */       consumeBinaryExpression(10);
/*  6038 */       break;
/*       */     case 422:
/*  6041 */       consumeBinaryExpression(17);
/*  6042 */       break;
/*       */     case 423:
/*  6045 */       consumeBinaryExpression(19);
/*  6046 */       break;
/*       */     case 425:
/*  6049 */       consumeBinaryExpression(4);
/*  6050 */       break;
/*       */     case 426:
/*  6053 */       consumeBinaryExpression(6);
/*  6054 */       break;
/*       */     case 427:
/*  6057 */       consumeBinaryExpression(5);
/*  6058 */       break;
/*       */     case 428:
/*  6061 */       consumeBinaryExpression(7);
/*  6062 */       break;
/*       */     case 430:
/*  6065 */       consumeInstanceOfExpression();
/*  6066 */       break;
/*       */     case 432:
/*  6069 */       consumeEqualityExpression(18);
/*  6070 */       break;
/*       */     case 433:
/*  6073 */       consumeEqualityExpression(29);
/*  6074 */       break;
/*       */     case 435:
/*  6077 */       consumeBinaryExpression(2);
/*  6078 */       break;
/*       */     case 437:
/*  6081 */       consumeBinaryExpression(8);
/*  6082 */       break;
/*       */     case 439:
/*  6085 */       consumeBinaryExpression(3);
/*  6086 */       break;
/*       */     case 441:
/*  6089 */       consumeBinaryExpression(0);
/*  6090 */       break;
/*       */     case 443:
/*  6093 */       consumeBinaryExpression(1);
/*  6094 */       break;
/*       */     case 445:
/*  6097 */       consumeConditionalExpression(23);
/*  6098 */       break;
/*       */     case 448:
/*  6101 */       consumeAssignment();
/*  6102 */       break;
/*       */     case 450:
/*  6105 */       ignoreExpressionAssignment();
/*  6106 */       break;
/*       */     case 451:
/*  6109 */       consumeAssignmentOperator(30);
/*  6110 */       break;
/*       */     case 452:
/*  6113 */       consumeAssignmentOperator(15);
/*  6114 */       break;
/*       */     case 453:
/*  6117 */       consumeAssignmentOperator(9);
/*  6118 */       break;
/*       */     case 454:
/*  6121 */       consumeAssignmentOperator(16);
/*  6122 */       break;
/*       */     case 455:
/*  6125 */       consumeAssignmentOperator(14);
/*  6126 */       break;
/*       */     case 456:
/*  6129 */       consumeAssignmentOperator(13);
/*  6130 */       break;
/*       */     case 457:
/*  6133 */       consumeAssignmentOperator(10);
/*  6134 */       break;
/*       */     case 458:
/*  6137 */       consumeAssignmentOperator(17);
/*  6138 */       break;
/*       */     case 459:
/*  6141 */       consumeAssignmentOperator(19);
/*  6142 */       break;
/*       */     case 460:
/*  6145 */       consumeAssignmentOperator(2);
/*  6146 */       break;
/*       */     case 461:
/*  6149 */       consumeAssignmentOperator(8);
/*  6150 */       break;
/*       */     case 462:
/*  6153 */       consumeAssignmentOperator(3);
/*  6154 */       break;
/*       */     case 466:
/*  6157 */       consumeEmptyExpression();
/*  6158 */       break;
/*       */     case 471:
/*  6161 */       consumeEmptyClassBodyDeclarationsopt();
/*  6162 */       break;
/*       */     case 472:
/*  6165 */       consumeClassBodyDeclarationsopt();
/*  6166 */       break;
/*       */     case 473:
/*  6169 */       consumeDefaultModifiers();
/*  6170 */       break;
/*       */     case 474:
/*  6173 */       consumeModifiers();
/*  6174 */       break;
/*       */     case 475:
/*  6177 */       consumeEmptyBlockStatementsopt();
/*  6178 */       break;
/*       */     case 477:
/*  6181 */       consumeEmptyDimsopt();
/*  6182 */       break;
/*       */     case 479:
/*  6185 */       consumeEmptyArgumentListopt();
/*  6186 */       break;
/*       */     case 483:
/*  6189 */       consumeFormalParameterListopt();
/*  6190 */       break;
/*       */     case 487:
/*  6193 */       consumeEmptyInterfaceMemberDeclarationsopt();
/*  6194 */       break;
/*       */     case 488:
/*  6197 */       consumeInterfaceMemberDeclarationsopt();
/*  6198 */       break;
/*       */     case 489:
/*  6201 */       consumeNestedType();
/*  6202 */       break;
/*       */     case 490:
/*  6205 */       consumeEmptyForInitopt();
/*  6206 */       break;
/*       */     case 492:
/*  6209 */       consumeEmptyForUpdateopt();
/*  6210 */       break;
/*       */     case 496:
/*  6213 */       consumeEmptyCatchesopt();
/*  6214 */       break;
/*       */     case 498:
/*  6217 */       consumeEnumDeclaration();
/*  6218 */       break;
/*       */     case 499:
/*  6221 */       consumeEnumHeader();
/*  6222 */       break;
/*       */     case 500:
/*  6225 */       consumeEnumHeaderName();
/*  6226 */       break;
/*       */     case 501:
/*  6229 */       consumeEnumHeaderNameWithTypeParameters();
/*  6230 */       break;
/*       */     case 502:
/*  6233 */       consumeEnumBodyNoConstants();
/*  6234 */       break;
/*       */     case 503:
/*  6237 */       consumeEnumBodyNoConstants();
/*  6238 */       break;
/*       */     case 504:
/*  6241 */       consumeEnumBodyWithConstants();
/*  6242 */       break;
/*       */     case 505:
/*  6245 */       consumeEnumBodyWithConstants();
/*  6246 */       break;
/*       */     case 507:
/*  6249 */       consumeEnumConstants();
/*  6250 */       break;
/*       */     case 508:
/*  6253 */       consumeEnumConstantHeaderName();
/*  6254 */       break;
/*       */     case 509:
/*  6257 */       consumeEnumConstantHeader();
/*  6258 */       break;
/*       */     case 510:
/*  6261 */       consumeEnumConstantWithClassBody();
/*  6262 */       break;
/*       */     case 511:
/*  6265 */       consumeEnumConstantNoClassBody();
/*  6266 */       break;
/*       */     case 512:
/*  6269 */       consumeArguments();
/*  6270 */       break;
/*       */     case 513:
/*  6273 */       consumeEmptyArguments();
/*  6274 */       break;
/*       */     case 515:
/*  6277 */       consumeEnumDeclarations();
/*  6278 */       break;
/*       */     case 516:
/*  6281 */       consumeEmptyEnumDeclarations();
/*  6282 */       break;
/*       */     case 518:
/*  6285 */       consumeEnhancedForStatement();
/*  6286 */       break;
/*       */     case 519:
/*  6289 */       consumeEnhancedForStatement();
/*  6290 */       break;
/*       */     case 520:
/*  6293 */       consumeEnhancedForStatementHeaderInit(false);
/*  6294 */       break;
/*       */     case 521:
/*  6297 */       consumeEnhancedForStatementHeaderInit(true);
/*  6298 */       break;
/*       */     case 522:
/*  6301 */       consumeEnhancedForStatementHeader();
/*  6302 */       break;
/*       */     case 523:
/*  6305 */       consumeImportDeclaration();
/*  6306 */       break;
/*       */     case 524:
/*  6309 */       consumeSingleStaticImportDeclarationName();
/*  6310 */       break;
/*       */     case 525:
/*  6313 */       consumeImportDeclaration();
/*  6314 */       break;
/*       */     case 526:
/*  6317 */       consumeStaticImportOnDemandDeclarationName();
/*  6318 */       break;
/*       */     case 527:
/*  6321 */       consumeTypeArguments();
/*  6322 */       break;
/*       */     case 528:
/*  6325 */       consumeOnlyTypeArguments();
/*  6326 */       break;
/*       */     case 530:
/*  6329 */       consumeTypeArgumentList1();
/*  6330 */       break;
/*       */     case 532:
/*  6333 */       consumeTypeArgumentList();
/*  6334 */       break;
/*       */     case 533:
/*  6337 */       consumeTypeArgument();
/*  6338 */       break;
/*       */     case 537:
/*  6341 */       consumeReferenceType1();
/*  6342 */       break;
/*       */     case 538:
/*  6345 */       consumeTypeArgumentReferenceType1();
/*  6346 */       break;
/*       */     case 540:
/*  6349 */       consumeTypeArgumentList2();
/*  6350 */       break;
/*       */     case 543:
/*  6353 */       consumeReferenceType2();
/*  6354 */       break;
/*       */     case 544:
/*  6357 */       consumeTypeArgumentReferenceType2();
/*  6358 */       break;
/*       */     case 546:
/*  6361 */       consumeTypeArgumentList3();
/*  6362 */       break;
/*       */     case 549:
/*  6365 */       consumeReferenceType3();
/*  6366 */       break;
/*       */     case 550:
/*  6369 */       consumeWildcard();
/*  6370 */       break;
/*       */     case 551:
/*  6373 */       consumeWildcardWithBounds();
/*  6374 */       break;
/*       */     case 552:
/*  6377 */       consumeWildcardBoundsExtends();
/*  6378 */       break;
/*       */     case 553:
/*  6381 */       consumeWildcardBoundsSuper();
/*  6382 */       break;
/*       */     case 554:
/*  6385 */       consumeWildcard1();
/*  6386 */       break;
/*       */     case 555:
/*  6389 */       consumeWildcard1WithBounds();
/*  6390 */       break;
/*       */     case 556:
/*  6393 */       consumeWildcardBounds1Extends();
/*  6394 */       break;
/*       */     case 557:
/*  6397 */       consumeWildcardBounds1Super();
/*  6398 */       break;
/*       */     case 558:
/*  6401 */       consumeWildcard2();
/*  6402 */       break;
/*       */     case 559:
/*  6405 */       consumeWildcard2WithBounds();
/*  6406 */       break;
/*       */     case 560:
/*  6409 */       consumeWildcardBounds2Extends();
/*  6410 */       break;
/*       */     case 561:
/*  6413 */       consumeWildcardBounds2Super();
/*  6414 */       break;
/*       */     case 562:
/*  6417 */       consumeWildcard3();
/*  6418 */       break;
/*       */     case 563:
/*  6421 */       consumeWildcard3WithBounds();
/*  6422 */       break;
/*       */     case 564:
/*  6425 */       consumeWildcardBounds3Extends();
/*  6426 */       break;
/*       */     case 565:
/*  6429 */       consumeWildcardBounds3Super();
/*  6430 */       break;
/*       */     case 566:
/*  6433 */       consumeTypeParameterHeader();
/*  6434 */       break;
/*       */     case 567:
/*  6437 */       consumeTypeParameters();
/*  6438 */       break;
/*       */     case 569:
/*  6441 */       consumeTypeParameterList();
/*  6442 */       break;
/*       */     case 571:
/*  6445 */       consumeTypeParameterWithExtends();
/*  6446 */       break;
/*       */     case 572:
/*  6449 */       consumeTypeParameterWithExtendsAndBounds();
/*  6450 */       break;
/*       */     case 574:
/*  6453 */       consumeAdditionalBoundList();
/*  6454 */       break;
/*       */     case 575:
/*  6457 */       consumeAdditionalBound();
/*  6458 */       break;
/*       */     case 577:
/*  6461 */       consumeTypeParameterList1();
/*  6462 */       break;
/*       */     case 578:
/*  6465 */       consumeTypeParameter1();
/*  6466 */       break;
/*       */     case 579:
/*  6469 */       consumeTypeParameter1WithExtends();
/*  6470 */       break;
/*       */     case 580:
/*  6473 */       consumeTypeParameter1WithExtendsAndBounds();
/*  6474 */       break;
/*       */     case 582:
/*  6477 */       consumeAdditionalBoundList1();
/*  6478 */       break;
/*       */     case 583:
/*  6481 */       consumeAdditionalBound1();
/*  6482 */       break;
/*       */     case 589:
/*  6485 */       consumeUnaryExpression(14);
/*  6486 */       break;
/*       */     case 590:
/*  6489 */       consumeUnaryExpression(13);
/*  6490 */       break;
/*       */     case 593:
/*  6493 */       consumeUnaryExpression(12);
/*  6494 */       break;
/*       */     case 594:
/*  6497 */       consumeUnaryExpression(11);
/*  6498 */       break;
/*       */     case 597:
/*  6501 */       consumeBinaryExpression(15);
/*  6502 */       break;
/*       */     case 598:
/*  6505 */       consumeBinaryExpressionWithName(15);
/*  6506 */       break;
/*       */     case 599:
/*  6509 */       consumeBinaryExpression(9);
/*  6510 */       break;
/*       */     case 600:
/*  6513 */       consumeBinaryExpressionWithName(9);
/*  6514 */       break;
/*       */     case 601:
/*  6517 */       consumeBinaryExpression(16);
/*  6518 */       break;
/*       */     case 602:
/*  6521 */       consumeBinaryExpressionWithName(16);
/*  6522 */       break;
/*       */     case 604:
/*  6525 */       consumeBinaryExpression(14);
/*  6526 */       break;
/*       */     case 605:
/*  6529 */       consumeBinaryExpressionWithName(14);
/*  6530 */       break;
/*       */     case 606:
/*  6533 */       consumeBinaryExpression(13);
/*  6534 */       break;
/*       */     case 607:
/*  6537 */       consumeBinaryExpressionWithName(13);
/*  6538 */       break;
/*       */     case 609:
/*  6541 */       consumeBinaryExpression(10);
/*  6542 */       break;
/*       */     case 610:
/*  6545 */       consumeBinaryExpressionWithName(10);
/*  6546 */       break;
/*       */     case 611:
/*  6549 */       consumeBinaryExpression(17);
/*  6550 */       break;
/*       */     case 612:
/*  6553 */       consumeBinaryExpressionWithName(17);
/*  6554 */       break;
/*       */     case 613:
/*  6557 */       consumeBinaryExpression(19);
/*  6558 */       break;
/*       */     case 614:
/*  6561 */       consumeBinaryExpressionWithName(19);
/*  6562 */       break;
/*       */     case 616:
/*  6565 */       consumeBinaryExpression(4);
/*  6566 */       break;
/*       */     case 617:
/*  6569 */       consumeBinaryExpressionWithName(4);
/*  6570 */       break;
/*       */     case 618:
/*  6573 */       consumeBinaryExpression(6);
/*  6574 */       break;
/*       */     case 619:
/*  6577 */       consumeBinaryExpressionWithName(6);
/*  6578 */       break;
/*       */     case 620:
/*  6581 */       consumeBinaryExpression(5);
/*  6582 */       break;
/*       */     case 621:
/*  6585 */       consumeBinaryExpressionWithName(5);
/*  6586 */       break;
/*       */     case 622:
/*  6589 */       consumeBinaryExpression(7);
/*  6590 */       break;
/*       */     case 623:
/*  6593 */       consumeBinaryExpressionWithName(7);
/*  6594 */       break;
/*       */     case 625:
/*  6597 */       consumeInstanceOfExpressionWithName();
/*  6598 */       break;
/*       */     case 626:
/*  6601 */       consumeInstanceOfExpression();
/*  6602 */       break;
/*       */     case 628:
/*  6605 */       consumeEqualityExpression(18);
/*  6606 */       break;
/*       */     case 629:
/*  6609 */       consumeEqualityExpressionWithName(18);
/*  6610 */       break;
/*       */     case 630:
/*  6613 */       consumeEqualityExpression(29);
/*  6614 */       break;
/*       */     case 631:
/*  6617 */       consumeEqualityExpressionWithName(29);
/*  6618 */       break;
/*       */     case 633:
/*  6621 */       consumeBinaryExpression(2);
/*  6622 */       break;
/*       */     case 634:
/*  6625 */       consumeBinaryExpressionWithName(2);
/*  6626 */       break;
/*       */     case 636:
/*  6629 */       consumeBinaryExpression(8);
/*  6630 */       break;
/*       */     case 637:
/*  6633 */       consumeBinaryExpressionWithName(8);
/*  6634 */       break;
/*       */     case 639:
/*  6637 */       consumeBinaryExpression(3);
/*  6638 */       break;
/*       */     case 640:
/*  6641 */       consumeBinaryExpressionWithName(3);
/*  6642 */       break;
/*       */     case 642:
/*  6645 */       consumeBinaryExpression(0);
/*  6646 */       break;
/*       */     case 643:
/*  6649 */       consumeBinaryExpressionWithName(0);
/*  6650 */       break;
/*       */     case 645:
/*  6653 */       consumeBinaryExpression(1);
/*  6654 */       break;
/*       */     case 646:
/*  6657 */       consumeBinaryExpressionWithName(1);
/*  6658 */       break;
/*       */     case 648:
/*  6661 */       consumeConditionalExpression(23);
/*  6662 */       break;
/*       */     case 649:
/*  6665 */       consumeConditionalExpressionWithName(23);
/*  6666 */       break;
/*       */     case 653:
/*  6669 */       consumeAnnotationTypeDeclarationHeaderName();
/*  6670 */       break;
/*       */     case 654:
/*  6673 */       consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
/*  6674 */       break;
/*       */     case 655:
/*  6677 */       consumeAnnotationTypeDeclarationHeaderNameWithTypeParameters();
/*  6678 */       break;
/*       */     case 656:
/*  6681 */       consumeAnnotationTypeDeclarationHeaderName();
/*  6682 */       break;
/*       */     case 657:
/*  6685 */       consumeAnnotationTypeDeclarationHeader();
/*  6686 */       break;
/*       */     case 658:
/*  6689 */       consumeAnnotationTypeDeclaration();
/*  6690 */       break;
/*       */     case 660:
/*  6693 */       consumeEmptyAnnotationTypeMemberDeclarationsopt();
/*  6694 */       break;
/*       */     case 661:
/*  6697 */       consumeAnnotationTypeMemberDeclarationsopt();
/*  6698 */       break;
/*       */     case 663:
/*  6701 */       consumeAnnotationTypeMemberDeclarations();
/*  6702 */       break;
/*       */     case 664:
/*  6705 */       consumeMethodHeaderNameWithTypeParameters(true);
/*  6706 */       break;
/*       */     case 665:
/*  6709 */       consumeMethodHeaderName(true);
/*  6710 */       break;
/*       */     case 666:
/*  6713 */       consumeEmptyMethodHeaderDefaultValue();
/*  6714 */       break;
/*       */     case 667:
/*  6717 */       consumeMethodHeaderDefaultValue();
/*  6718 */       break;
/*       */     case 668:
/*  6721 */       consumeMethodHeader();
/*  6722 */       break;
/*       */     case 669:
/*  6725 */       consumeAnnotationTypeMemberDeclaration();
/*  6726 */       break;
/*       */     case 677:
/*  6729 */       consumeAnnotationName();
/*  6730 */       break;
/*       */     case 678:
/*  6733 */       consumeNormalAnnotation();
/*  6734 */       break;
/*       */     case 679:
/*  6737 */       consumeEmptyMemberValuePairsopt();
/*  6738 */       break;
/*       */     case 682:
/*  6741 */       consumeMemberValuePairs();
/*  6742 */       break;
/*       */     case 683:
/*  6745 */       consumeMemberValuePair();
/*  6746 */       break;
/*       */     case 684:
/*  6749 */       consumeEnterMemberValue();
/*  6750 */       break;
/*       */     case 685:
/*  6753 */       consumeExitMemberValue();
/*  6754 */       break;
/*       */     case 687:
/*  6757 */       consumeMemberValueAsName();
/*  6758 */       break;
/*       */     case 690:
/*  6761 */       consumeMemberValueArrayInitializer();
/*  6762 */       break;
/*       */     case 691:
/*  6765 */       consumeMemberValueArrayInitializer();
/*  6766 */       break;
/*       */     case 692:
/*  6769 */       consumeEmptyMemberValueArrayInitializer();
/*  6770 */       break;
/*       */     case 693:
/*  6773 */       consumeEmptyMemberValueArrayInitializer();
/*  6774 */       break;
/*       */     case 694:
/*  6777 */       consumeEnterMemberValueArrayInitializer();
/*  6778 */       break;
/*       */     case 696:
/*  6781 */       consumeMemberValues();
/*  6782 */       break;
/*       */     case 697:
/*  6785 */       consumeMarkerAnnotation();
/*  6786 */       break;
/*       */     case 698:
/*  6789 */       consumeSingleMemberAnnotationMemberValue();
/*  6790 */       break;
/*       */     case 699:
/*  6793 */       consumeSingleMemberAnnotation();
/*  6794 */       break;
/*       */     case 700:
/*  6797 */       consumeRecoveryMethodHeaderNameWithTypeParameters();
/*  6798 */       break;
/*       */     case 701:
/*  6801 */       consumeRecoveryMethodHeaderName();
/*  6802 */       break;
/*       */     case 702:
/*  6805 */       consumeMethodHeader();
/*  6806 */       break;
/*       */     case 703:
/*  6809 */       consumeMethodHeader();
/*       */     case 31:
/*       */     case 32:
/*       */     case 33:
/*       */     case 34:
/*       */     case 35:
/*       */     case 36:
/*       */     case 37:
/*       */     case 38:
/*       */     case 39:
/*       */     case 40:
/*       */     case 41:
/*       */     case 42:
/*       */     case 43:
/*       */     case 45:
/*       */     case 46:
/*       */     case 47:
/*       */     case 56:
/*       */     case 57:
/*       */     case 58:
/*       */     case 59:
/*       */     case 72:
/*       */     case 73:
/*       */     case 74:
/*       */     case 75:
/*       */     case 76:
/*       */     case 77:
/*       */     case 78:
/*       */     case 79:
/*       */     case 80:
/*       */     case 81:
/*       */     case 82:
/*       */     case 83:
/*       */     case 84:
/*       */     case 85:
/*       */     case 86:
/*       */     case 88:
/*       */     case 90:
/*       */     case 96:
/*       */     case 97:
/*       */     case 98:
/*       */     case 99:
/*       */     case 104:
/*       */     case 105:
/*       */     case 107:
/*       */     case 108:
/*       */     case 109:
/*       */     case 111:
/*       */     case 112:
/*       */     case 113:
/*       */     case 114:
/*       */     case 115:
/*       */     case 116:
/*       */     case 117:
/*       */     case 118:
/*       */     case 119:
/*       */     case 120:
/*       */     case 121:
/*       */     case 126:
/*       */     case 130:
/*       */     case 133:
/*       */     case 134:
/*       */     case 136:
/*       */     case 137:
/*       */     case 138:
/*       */     case 143:
/*       */     case 144:
/*       */     case 145:
/*       */     case 146:
/*       */     case 147:
/*       */     case 148:
/*       */     case 150:
/*       */     case 151:
/*       */     case 153:
/*       */     case 155:
/*       */     case 156:
/*       */     case 162:
/*       */     case 163:
/*       */     case 164:
/*       */     case 165:
/*       */     case 177:
/*       */     case 181:
/*       */     case 205:
/*       */     case 208:
/*       */     case 209:
/*       */     case 212:
/*       */     case 216:
/*       */     case 217:
/*       */     case 218:
/*       */     case 219:
/*       */     case 220:
/*       */     case 221:
/*       */     case 222:
/*       */     case 227:
/*       */     case 231:
/*       */     case 233:
/*       */     case 234:
/*       */     case 235:
/*       */     case 245:
/*       */     case 246:
/*       */     case 247:
/*       */     case 248:
/*       */     case 249:
/*       */     case 250:
/*       */     case 251:
/*       */     case 252:
/*       */     case 253:
/*       */     case 254:
/*       */     case 255:
/*       */     case 256:
/*       */     case 257:
/*       */     case 258:
/*       */     case 259:
/*       */     case 260:
/*       */     case 261:
/*       */     case 262:
/*       */     case 263:
/*       */     case 264:
/*       */     case 265:
/*       */     case 266:
/*       */     case 267:
/*       */     case 268:
/*       */     case 269:
/*       */     case 275:
/*       */     case 276:
/*       */     case 277:
/*       */     case 278:
/*       */     case 279:
/*       */     case 280:
/*       */     case 281:
/*       */     case 282:
/*       */     case 288:
/*       */     case 289:
/*       */     case 291:
/*       */     case 294:
/*       */     case 304:
/*       */     case 305:
/*       */     case 306:
/*       */     case 320:
/*       */     case 322:
/*       */     case 325:
/*       */     case 328:
/*       */     case 329:
/*       */     case 330:
/*       */     case 331:
/*       */     case 335:
/*       */     case 336:
/*       */     case 343:
/*       */     case 344:
/*       */     case 354:
/*       */     case 357:
/*       */     case 359:
/*       */     case 367:
/*       */     case 369:
/*       */     case 372:
/*       */     case 373:
/*       */     case 386:
/*       */     case 388:
/*       */     case 389:
/*       */     case 393:
/*       */     case 394:
/*       */     case 397:
/*       */     case 400:
/*       */     case 403:
/*       */     case 413:
/*       */     case 417:
/*       */     case 420:
/*       */     case 424:
/*       */     case 429:
/*       */     case 431:
/*       */     case 434:
/*       */     case 436:
/*       */     case 438:
/*       */     case 440:
/*       */     case 442:
/*       */     case 444:
/*       */     case 446:
/*       */     case 447:
/*       */     case 449:
/*       */     case 463:
/*       */     case 464:
/*       */     case 465:
/*       */     case 467:
/*       */     case 468:
/*       */     case 469:
/*       */     case 470:
/*       */     case 476:
/*       */     case 478:
/*       */     case 480:
/*       */     case 481:
/*       */     case 482:
/*       */     case 484:
/*       */     case 485:
/*       */     case 486:
/*       */     case 491:
/*       */     case 493:
/*       */     case 494:
/*       */     case 495:
/*       */     case 497:
/*       */     case 506:
/*       */     case 514:
/*       */     case 517:
/*       */     case 529:
/*       */     case 531:
/*       */     case 534:
/*       */     case 535:
/*       */     case 536:
/*       */     case 539:
/*       */     case 541:
/*       */     case 542:
/*       */     case 545:
/*       */     case 547:
/*       */     case 548:
/*       */     case 568:
/*       */     case 570:
/*       */     case 573:
/*       */     case 576:
/*       */     case 581:
/*       */     case 584:
/*       */     case 585:
/*       */     case 586:
/*       */     case 587:
/*       */     case 588:
/*       */     case 591:
/*       */     case 592:
/*       */     case 595:
/*       */     case 596:
/*       */     case 603:
/*       */     case 608:
/*       */     case 615:
/*       */     case 624:
/*       */     case 627:
/*       */     case 632:
/*       */     case 635:
/*       */     case 638:
/*       */     case 641:
/*       */     case 644:
/*       */     case 647:
/*       */     case 650:
/*       */     case 651:
/*       */     case 652:
/*       */     case 659:
/*       */     case 662:
/*       */     case 670:
/*       */     case 671:
/*       */     case 672:
/*       */     case 673:
/*       */     case 674:
/*       */     case 675:
/*       */     case 676:
/*       */     case 680:
/*       */     case 681:
/*       */     case 686:
/*       */     case 688:
/*       */     case 689:
/*       */     case 695: }  } 
/*  6816 */   protected void consumeSimpleAssertStatement() { this.expressionLengthPtr -= 1;
/*  6817 */     pushOnAstStack(new AssertStatement(this.expressionStack[(this.expressionPtr--)], this.intStack[(this.intPtr--)])); }
/*       */ 
/*       */   protected void consumeSingleMemberAnnotation()
/*       */   {
/*  6821 */     SingleMemberAnnotation singleMemberAnnotation = null;
/*       */ 
/*  6823 */     int oldIndex = this.identifierPtr;
/*       */ 
/*  6825 */     TypeReference typeReference = getAnnotationType();
/*  6826 */     singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.intStack[(this.intPtr--)]);
/*  6827 */     singleMemberAnnotation.memberValue = this.expressionStack[(this.expressionPtr--)];
/*  6828 */     this.expressionLengthPtr -= 1;
/*  6829 */     singleMemberAnnotation.declarationSourceEnd = this.rParenPos;
/*  6830 */     pushOnExpressionStack(singleMemberAnnotation);
/*       */ 
/*  6833 */     if (this.currentElement != null) {
/*  6834 */       annotationRecoveryCheckPoint(singleMemberAnnotation.sourceStart, singleMemberAnnotation.declarationSourceEnd);
/*       */ 
/*  6836 */       if ((this.currentElement instanceof RecoveredAnnotation)) {
/*  6837 */         this.currentElement = ((RecoveredAnnotation)this.currentElement).addAnnotation(singleMemberAnnotation, oldIndex);
/*       */       }
/*       */     }
/*       */ 
/*  6841 */     if ((!this.statementRecoveryActivated) && 
/*  6842 */       (this.options.sourceLevel < 3211264L) && 
/*  6843 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  6844 */       problemReporter().invalidUsageOfAnnotation(singleMemberAnnotation);
/*       */     }
/*  6846 */     this.recordStringLiterals = true;
/*       */   }
/*       */ 
/*       */   protected void consumeSingleMemberAnnotationMemberValue() {
/*  6850 */     if ((this.currentElement != null) && ((this.currentElement instanceof RecoveredAnnotation))) {
/*  6851 */       RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
/*       */ 
/*  6853 */       recoveredAnnotation.setKind(2);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeSingleStaticImportDeclarationName()
/*       */   {
/*       */     int length;
/*  6865 */     char[][] tokens = new char[length = this.identifierLengthStack[(this.identifierLengthPtr--)]][];
/*  6866 */     this.identifierPtr -= length;
/*  6867 */     long[] positions = new long[length];
/*  6868 */     System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  6869 */     System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
/*       */     ImportReference impt;
/*  6870 */     pushOnAstStack(impt = new ImportReference(tokens, positions, false, 8));
/*       */ 
/*  6872 */     this.modifiers = 0;
/*  6873 */     this.modifiersSourceStart = -1;
/*       */ 
/*  6875 */     if (this.currentToken == 27)
/*  6876 */       impt.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*       */     else {
/*  6878 */       impt.declarationSourceEnd = impt.sourceEnd;
/*       */     }
/*  6880 */     impt.declarationEnd = impt.declarationSourceEnd;
/*       */ 
/*  6882 */     impt.declarationSourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  6884 */     if ((!this.statementRecoveryActivated) && 
/*  6885 */       (this.options.sourceLevel < 3211264L) && 
/*  6886 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  6887 */       impt.modifiers = 0;
/*  6888 */       problemReporter().invalidUsageOfStaticImports(impt);
/*       */     }
/*       */ 
/*  6892 */     if (this.currentElement != null) {
/*  6893 */       this.lastCheckPoint = (impt.declarationSourceEnd + 1);
/*  6894 */       this.currentElement = this.currentElement.add(impt, 0);
/*  6895 */       this.lastIgnoredToken = -1;
/*  6896 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeSingleTypeImportDeclarationName()
/*       */   {
/*       */     int length;
/*  6906 */     char[][] tokens = new char[length = this.identifierLengthStack[(this.identifierLengthPtr--)]][];
/*  6907 */     this.identifierPtr -= length;
/*  6908 */     long[] positions = new long[length];
/*  6909 */     System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  6910 */     System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
/*       */     ImportReference impt;
/*  6911 */     pushOnAstStack(impt = new ImportReference(tokens, positions, false, 0));
/*       */ 
/*  6913 */     if (this.currentToken == 27)
/*  6914 */       impt.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*       */     else {
/*  6916 */       impt.declarationSourceEnd = impt.sourceEnd;
/*       */     }
/*  6918 */     impt.declarationEnd = impt.declarationSourceEnd;
/*       */ 
/*  6920 */     impt.declarationSourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  6923 */     if (this.currentElement != null) {
/*  6924 */       this.lastCheckPoint = (impt.declarationSourceEnd + 1);
/*  6925 */       this.currentElement = this.currentElement.add(impt, 0);
/*  6926 */       this.lastIgnoredToken = -1;
/*  6927 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeStatementBreak()
/*       */   {
/*  6934 */     pushOnAstStack(new BreakStatement(null, this.intStack[(this.intPtr--)], this.endStatementPosition));
/*       */ 
/*  6936 */     if (this.pendingRecoveredType != null)
/*       */     {
/*  6940 */       if ((this.pendingRecoveredType.allocation == null) && 
/*  6941 */         (this.endPosition <= this.pendingRecoveredType.declarationSourceEnd)) {
/*  6942 */         this.astStack[this.astPtr] = this.pendingRecoveredType;
/*  6943 */         this.pendingRecoveredType = null;
/*  6944 */         return;
/*       */       }
/*  6946 */       this.pendingRecoveredType = null;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeStatementBreakWithLabel()
/*       */   {
/*  6953 */     pushOnAstStack(
/*  6954 */       new BreakStatement(
/*  6955 */       this.identifierStack[(this.identifierPtr--)], 
/*  6956 */       this.intStack[(this.intPtr--)], 
/*  6957 */       this.endStatementPosition));
/*  6958 */     this.identifierLengthPtr -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeStatementCatch()
/*       */   {
/*  6969 */     this.astLengthPtr -= 1;
/*  6970 */     this.listLength = 0;
/*       */   }
/*       */ 
/*       */   protected void consumeStatementContinue()
/*       */   {
/*  6976 */     pushOnAstStack(
/*  6977 */       new ContinueStatement(
/*  6978 */       null, 
/*  6979 */       this.intStack[(this.intPtr--)], 
/*  6980 */       this.endStatementPosition));
/*       */   }
/*       */ 
/*       */   protected void consumeStatementContinueWithLabel()
/*       */   {
/*  6986 */     pushOnAstStack(
/*  6987 */       new ContinueStatement(
/*  6988 */       this.identifierStack[(this.identifierPtr--)], 
/*  6989 */       this.intStack[(this.intPtr--)], 
/*  6990 */       this.endStatementPosition));
/*  6991 */     this.identifierLengthPtr -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeStatementDo()
/*       */   {
/*  6997 */     this.intPtr -= 1;
/*       */ 
/*  6999 */     Statement statement = (Statement)this.astStack[this.astPtr];
/*  7000 */     this.expressionLengthPtr -= 1;
/*  7001 */     this.astStack[this.astPtr] = 
/*  7002 */       new DoStatement(
/*  7003 */       this.expressionStack[(this.expressionPtr--)], 
/*  7004 */       statement, 
/*  7005 */       this.intStack[(this.intPtr--)], 
/*  7006 */       this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeStatementExpressionList() {
/*  7010 */     concatExpressionLists();
/*       */   }
/*       */ 
/*       */   protected void consumeStatementFor()
/*       */   {
/*  7017 */     Expression cond = null;
/*       */ 
/*  7019 */     boolean scope = true;
/*       */ 
/*  7022 */     this.astLengthPtr -= 1;
/*  7023 */     Statement statement = (Statement)this.astStack[(this.astPtr--)];
/*       */     int length;
/*       */     Statement[] updates;
/*       */     Statement[] updates;
/*  7026 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) == 0) {
/*  7027 */       updates = (Statement[])null;
/*       */     } else {
/*  7029 */       this.expressionPtr -= length;
/*  7030 */       System.arraycopy(
/*  7031 */         this.expressionStack, 
/*  7032 */         this.expressionPtr + 1, 
/*  7033 */         updates = new Statement[length], 
/*  7034 */         0, 
/*  7035 */         length);
/*       */     }
/*       */ 
/*  7038 */     if (this.expressionLengthStack[(this.expressionLengthPtr--)] != 0)
/*  7039 */       cond = this.expressionStack[(this.expressionPtr--)];
/*       */     Statement[] inits;
/*  7042 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) == 0) {
/*  7043 */       Statement[] inits = (Statement[])null;
/*  7044 */       scope = false;
/*       */     }
/*  7046 */     else if (length == -1) {
/*  7047 */       scope = false;
/*  7048 */       length = this.expressionLengthStack[(this.expressionLengthPtr--)];
/*  7049 */       this.expressionPtr -= length;
/*       */       Statement[] inits;
/*  7050 */       System.arraycopy(
/*  7051 */         this.expressionStack, 
/*  7052 */         this.expressionPtr + 1, 
/*  7053 */         inits = new Statement[length], 
/*  7054 */         0, 
/*  7055 */         length);
/*       */     } else {
/*  7057 */       this.astPtr -= length;
/*  7058 */       System.arraycopy(
/*  7059 */         this.astStack, 
/*  7060 */         this.astPtr + 1, 
/*  7061 */         inits = new Statement[length], 
/*  7062 */         0, 
/*  7063 */         length);
/*       */     }
/*       */ 
/*  7066 */     pushOnAstStack(
/*  7067 */       new ForStatement(
/*  7068 */       inits, 
/*  7069 */       cond, 
/*  7070 */       updates, 
/*  7071 */       statement, 
/*  7072 */       scope, 
/*  7073 */       this.intStack[(this.intPtr--)], 
/*  7074 */       this.endStatementPosition));
/*       */   }
/*       */ 
/*       */   protected void consumeStatementIfNoElse()
/*       */   {
/*  7080 */     this.expressionLengthPtr -= 1;
/*  7081 */     Statement thenStatement = (Statement)this.astStack[this.astPtr];
/*  7082 */     this.astStack[this.astPtr] = 
/*  7083 */       new IfStatement(
/*  7084 */       this.expressionStack[(this.expressionPtr--)], 
/*  7085 */       thenStatement, 
/*  7086 */       this.intStack[(this.intPtr--)], 
/*  7087 */       this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeStatementIfWithElse()
/*       */   {
/*  7093 */     this.expressionLengthPtr -= 1;
/*       */ 
/*  7096 */     this.astLengthPtr -= 1;
/*       */ 
/*  7099 */     this.astStack[(--this.astPtr)] = 
/*  7100 */       new IfStatement(
/*  7101 */       this.expressionStack[(this.expressionPtr--)], 
/*  7102 */       (Statement)this.astStack[this.astPtr], 
/*  7103 */       (Statement)this.astStack[(this.astPtr + 1)], 
/*  7104 */       this.intStack[(this.intPtr--)], 
/*  7105 */       this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeStatementLabel()
/*       */   {
/*  7112 */     Statement statement = (Statement)this.astStack[this.astPtr];
/*  7113 */     this.astStack[this.astPtr] = 
/*  7114 */       new LabeledStatement(
/*  7115 */       this.identifierStack[this.identifierPtr], 
/*  7116 */       statement, 
/*  7117 */       this.identifierPositionStack[(this.identifierPtr--)], 
/*  7118 */       this.endStatementPosition);
/*  7119 */     this.identifierLengthPtr -= 1;
/*       */   }
/*       */ 
/*       */   protected void consumeStatementReturn()
/*       */   {
/*  7125 */     if (this.expressionLengthStack[(this.expressionLengthPtr--)] != 0) {
/*  7126 */       pushOnAstStack(
/*  7127 */         new ReturnStatement(
/*  7128 */         this.expressionStack[(this.expressionPtr--)], 
/*  7129 */         this.intStack[(this.intPtr--)], 
/*  7130 */         this.endStatementPosition));
/*       */     }
/*       */     else
/*  7133 */       pushOnAstStack(new ReturnStatement(null, this.intStack[(this.intPtr--)], this.endStatementPosition));
/*       */   }
/*       */ 
/*       */   protected void consumeStatementSwitch()
/*       */   {
/*  7144 */     SwitchStatement switchStatement = new SwitchStatement();
/*  7145 */     this.expressionLengthPtr -= 1;
/*  7146 */     switchStatement.expression = this.expressionStack[(this.expressionPtr--)];
/*       */     int length;
/*  7147 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0) {
/*  7148 */       this.astPtr -= length;
/*  7149 */       System.arraycopy(
/*  7150 */         this.astStack, 
/*  7151 */         this.astPtr + 1, 
/*  7152 */         switchStatement.statements = new Statement[length], 
/*  7153 */         0, 
/*  7154 */         length);
/*       */     }
/*  7156 */     switchStatement.explicitDeclarations = this.realBlockStack[(this.realBlockPtr--)];
/*  7157 */     pushOnAstStack(switchStatement);
/*  7158 */     switchStatement.blockStart = this.intStack[(this.intPtr--)];
/*  7159 */     switchStatement.sourceStart = this.intStack[(this.intPtr--)];
/*  7160 */     switchStatement.sourceEnd = this.endStatementPosition;
/*  7161 */     if ((length == 0) && (!containsComment(switchStatement.blockStart, switchStatement.sourceEnd)))
/*  7162 */       switchStatement.bits |= 8;
/*       */   }
/*       */ 
/*       */   protected void consumeStatementSynchronized()
/*       */   {
/*  7169 */     if (this.astLengthStack[this.astLengthPtr] == 0) {
/*  7170 */       this.astLengthStack[this.astLengthPtr] = 1;
/*  7171 */       this.expressionLengthPtr -= 1;
/*  7172 */       this.astStack[(++this.astPtr)] = 
/*  7173 */         new SynchronizedStatement(
/*  7174 */         this.expressionStack[(this.expressionPtr--)], 
/*  7175 */         null, 
/*  7176 */         this.intStack[(this.intPtr--)], 
/*  7177 */         this.endStatementPosition);
/*       */     } else {
/*  7179 */       this.expressionLengthPtr -= 1;
/*  7180 */       this.astStack[this.astPtr] = 
/*  7181 */         new SynchronizedStatement(
/*  7182 */         this.expressionStack[(this.expressionPtr--)], 
/*  7183 */         (Block)this.astStack[this.astPtr], 
/*  7184 */         this.intStack[(this.intPtr--)], 
/*  7185 */         this.endStatementPosition);
/*       */     }
/*  7187 */     resetModifiers();
/*       */   }
/*       */ 
/*       */   protected void consumeStatementThrow() {
/*  7191 */     this.expressionLengthPtr -= 1;
/*  7192 */     pushOnAstStack(new ThrowStatement(this.expressionStack[(this.expressionPtr--)], this.intStack[(this.intPtr--)], this.endStatementPosition));
/*       */   }
/*       */ 
/*       */   protected void consumeStatementTry(boolean withFinally)
/*       */   {
/*  7199 */     TryStatement tryStmt = new TryStatement();
/*       */ 
/*  7201 */     if (withFinally) {
/*  7202 */       this.astLengthPtr -= 1;
/*  7203 */       tryStmt.finallyBlock = ((Block)this.astStack[(this.astPtr--)]);
/*       */     }
/*       */     int length;
/*  7206 */     if ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0) {
/*  7207 */       if (length == 1) {
/*  7208 */         tryStmt.catchBlocks = new Block[] { (Block)this.astStack[(this.astPtr--)] };
/*  7209 */         tryStmt.catchArguments = new Argument[] { (Argument)this.astStack[(this.astPtr--)] };
/*       */       } else {
/*  7211 */         Block[] bks = tryStmt.catchBlocks = new Block[length];
/*  7212 */         Argument[] args = tryStmt.catchArguments = new Argument[length];
/*  7213 */         while (length-- > 0) {
/*  7214 */           bks[length] = ((Block)this.astStack[(this.astPtr--)]);
/*  7215 */           args[length] = ((Argument)this.astStack[(this.astPtr--)]);
/*       */         }
/*       */       }
/*       */     }
/*       */ 
/*  7220 */     this.astLengthPtr -= 1;
/*  7221 */     tryStmt.tryBlock = ((Block)this.astStack[(this.astPtr--)]);
/*       */ 
/*  7224 */     tryStmt.sourceEnd = this.endStatementPosition;
/*  7225 */     tryStmt.sourceStart = this.intStack[(this.intPtr--)];
/*  7226 */     pushOnAstStack(tryStmt);
/*       */   }
/*       */ 
/*       */   protected void consumeStatementWhile()
/*       */   {
/*  7232 */     this.expressionLengthPtr -= 1;
/*  7233 */     Statement statement = (Statement)this.astStack[this.astPtr];
/*  7234 */     this.astStack[this.astPtr] = 
/*  7235 */       new WhileStatement(
/*  7236 */       this.expressionStack[(this.expressionPtr--)], 
/*  7237 */       statement, 
/*  7238 */       this.intStack[(this.intPtr--)], 
/*  7239 */       this.endStatementPosition);
/*       */   }
/*       */ 
/*       */   protected void consumeStaticImportOnDemandDeclarationName()
/*       */   {
/*       */     int length;
/*  7248 */     char[][] tokens = new char[length = this.identifierLengthStack[(this.identifierLengthPtr--)]][];
/*  7249 */     this.identifierPtr -= length;
/*  7250 */     long[] positions = new long[length];
/*  7251 */     System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  7252 */     System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
/*       */     ImportReference impt;
/*  7253 */     pushOnAstStack(impt = new ImportReference(tokens, positions, true, 8));
/*       */ 
/*  7255 */     this.modifiers = 0;
/*  7256 */     this.modifiersSourceStart = -1;
/*       */ 
/*  7258 */     if (this.currentToken == 27)
/*  7259 */       impt.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*       */     else {
/*  7261 */       impt.declarationSourceEnd = impt.sourceEnd;
/*       */     }
/*  7263 */     impt.declarationEnd = impt.declarationSourceEnd;
/*       */ 
/*  7265 */     impt.declarationSourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  7267 */     if ((!this.statementRecoveryActivated) && 
/*  7268 */       (this.options.sourceLevel < 3211264L) && 
/*  7269 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  7270 */       impt.modifiers = 0;
/*  7271 */       problemReporter().invalidUsageOfStaticImports(impt);
/*       */     }
/*       */ 
/*  7275 */     if (this.currentElement != null) {
/*  7276 */       this.lastCheckPoint = (impt.declarationSourceEnd + 1);
/*  7277 */       this.currentElement = this.currentElement.add(impt, 0);
/*  7278 */       this.lastIgnoredToken = -1;
/*  7279 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeStaticInitializer()
/*       */   {
/*  7286 */     Block block = (Block)this.astStack[this.astPtr];
/*  7287 */     if (this.diet) block.bits &= -9;
/*  7288 */     Initializer initializer = new Initializer(block, 8);
/*  7289 */     this.astStack[this.astPtr] = initializer;
/*  7290 */     initializer.sourceEnd = this.endStatementPosition;
/*  7291 */     initializer.declarationSourceEnd = flushCommentsDefinedPriorTo(this.endStatementPosition);
/*  7292 */     this.nestedMethod[this.nestedType] -= 1;
/*  7293 */     initializer.declarationSourceStart = this.intStack[(this.intPtr--)];
/*  7294 */     initializer.bodyStart = this.intStack[(this.intPtr--)];
/*  7295 */     initializer.bodyEnd = this.endPosition;
/*       */ 
/*  7297 */     initializer.javadoc = this.javadoc;
/*  7298 */     this.javadoc = null;
/*       */ 
/*  7301 */     if (this.currentElement != null) {
/*  7302 */       this.lastCheckPoint = initializer.declarationSourceEnd;
/*  7303 */       this.currentElement = this.currentElement.add(initializer, 0);
/*  7304 */       this.lastIgnoredToken = -1;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeStaticOnly() {
/*  7309 */     int savedModifiersSourceStart = this.modifiersSourceStart;
/*  7310 */     checkComment();
/*  7311 */     if (this.modifiersSourceStart >= savedModifiersSourceStart) {
/*  7312 */       this.modifiersSourceStart = savedModifiersSourceStart;
/*       */     }
/*  7314 */     pushOnIntStack(this.scanner.currentPosition);
/*  7315 */     pushOnIntStack(
/*  7316 */       this.modifiersSourceStart >= 0 ? this.modifiersSourceStart : this.scanner.startPosition);
/*  7317 */     jumpOverMethodBody();
/*  7318 */     this.nestedMethod[this.nestedType] += 1;
/*  7319 */     resetModifiers();
/*  7320 */     this.expressionLengthPtr -= 1;
/*       */ 
/*  7323 */     if (this.currentElement != null)
/*  7324 */       this.recoveredStaticInitializerStart = this.intStack[this.intPtr];
/*       */   }
/*       */ 
/*       */   protected void consumeSwitchBlock()
/*       */   {
/*  7329 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeSwitchBlockStatement() {
/*  7333 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeSwitchBlockStatements() {
/*  7337 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeSwitchLabels() {
/*  7341 */     optimizedConcatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeToken(int type)
/*       */   {
/*  7364 */     switch (type) {
/*       */     case 26:
/*  7366 */       pushIdentifier();
/*  7367 */       if ((this.scanner.useAssertAsAnIndentifier) && 
/*  7368 */         (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  7369 */         long positions = this.identifierPositionStack[this.identifierPtr];
/*  7370 */         if (!this.statementRecoveryActivated) problemReporter().useAssertAsAnIdentifier((int)(positions >>> 32), (int)positions);
/*       */       }
/*  7372 */       if ((!this.scanner.useEnumAsAnIndentifier) || 
/*  7373 */         (this.lastErrorEndPositionBeforeRecovery >= this.scanner.currentPosition)) break;
/*  7374 */       long positions = this.identifierPositionStack[this.identifierPtr];
/*  7375 */       if (this.statementRecoveryActivated) break; problemReporter().useEnumAsAnIdentifier((int)(positions >>> 32), (int)positions);
/*       */ 
/*  7377 */       break;
/*       */     case 95:
/*  7380 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7381 */       pushOnIntStack(this.scanner.startPosition);
/*  7382 */       break;
/*       */     case 56:
/*  7384 */       checkAndSetModifiers(1024);
/*  7385 */       pushOnExpressionStackLengthStack(0);
/*  7386 */       break;
/*       */     case 62:
/*  7388 */       checkAndSetModifiers(2048);
/*  7389 */       pushOnExpressionStackLengthStack(0);
/*  7390 */       break;
/*       */     case 57:
/*  7392 */       checkAndSetModifiers(16);
/*  7393 */       pushOnExpressionStackLengthStack(0);
/*  7394 */       break;
/*       */     case 58:
/*  7396 */       checkAndSetModifiers(256);
/*  7397 */       pushOnExpressionStackLengthStack(0);
/*  7398 */       break;
/*       */     case 59:
/*  7400 */       checkAndSetModifiers(2);
/*  7401 */       pushOnExpressionStackLengthStack(0);
/*  7402 */       break;
/*       */     case 60:
/*  7404 */       checkAndSetModifiers(4);
/*  7405 */       pushOnExpressionStackLengthStack(0);
/*  7406 */       break;
/*       */     case 61:
/*  7408 */       checkAndSetModifiers(1);
/*  7409 */       pushOnExpressionStackLengthStack(0);
/*  7410 */       break;
/*       */     case 63:
/*  7412 */       checkAndSetModifiers(128);
/*  7413 */       pushOnExpressionStackLengthStack(0);
/*  7414 */       break;
/*       */     case 64:
/*  7416 */       checkAndSetModifiers(64);
/*  7417 */       pushOnExpressionStackLengthStack(0);
/*  7418 */       break;
/*       */     case 54:
/*  7420 */       checkAndSetModifiers(8);
/*  7421 */       pushOnExpressionStackLengthStack(0);
/*  7422 */       break;
/*       */     case 55:
/*  7424 */       this.synchronizedBlockSourceStart = this.scanner.startPosition;
/*  7425 */       checkAndSetModifiers(32);
/*  7426 */       pushOnExpressionStackLengthStack(0);
/*  7427 */       break;
/*       */     case 40:
/*  7430 */       pushIdentifier(-6);
/*  7431 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7432 */       pushOnIntStack(this.scanner.startPosition);
/*  7433 */       break;
/*       */     case 32:
/*  7438 */       pushIdentifier(-5);
/*  7439 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7440 */       pushOnIntStack(this.scanner.startPosition);
/*  7441 */       break;
/*       */     case 33:
/*  7443 */       pushIdentifier(-3);
/*  7444 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7445 */       pushOnIntStack(this.scanner.startPosition);
/*  7446 */       break;
/*       */     case 34:
/*  7448 */       pushIdentifier(-2);
/*  7449 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7450 */       pushOnIntStack(this.scanner.startPosition);
/*  7451 */       break;
/*       */     case 35:
/*  7453 */       pushIdentifier(-8);
/*  7454 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7455 */       pushOnIntStack(this.scanner.startPosition);
/*  7456 */       break;
/*       */     case 36:
/*  7458 */       pushIdentifier(-9);
/*  7459 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7460 */       pushOnIntStack(this.scanner.startPosition);
/*  7461 */       break;
/*       */     case 37:
/*  7463 */       pushIdentifier(-10);
/*  7464 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7465 */       pushOnIntStack(this.scanner.startPosition);
/*  7466 */       break;
/*       */     case 38:
/*  7468 */       pushIdentifier(-7);
/*  7469 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7470 */       pushOnIntStack(this.scanner.startPosition);
/*  7471 */       break;
/*       */     case 39:
/*  7473 */       pushIdentifier(-4);
/*  7474 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7475 */       pushOnIntStack(this.scanner.startPosition);
/*  7476 */       break;
/*       */     case 47:
/*  7479 */       pushOnExpressionStack(
/*  7480 */         new IntLiteral(
/*  7481 */         this.scanner.getCurrentTokenSource(), 
/*  7482 */         this.scanner.startPosition, 
/*  7483 */         this.scanner.currentPosition - 1));
/*  7484 */       break;
/*       */     case 48:
/*  7486 */       pushOnExpressionStack(
/*  7487 */         new LongLiteral(
/*  7488 */         this.scanner.getCurrentTokenSource(), 
/*  7489 */         this.scanner.startPosition, 
/*  7490 */         this.scanner.currentPosition - 1));
/*  7491 */       break;
/*       */     case 49:
/*  7493 */       pushOnExpressionStack(
/*  7494 */         new FloatLiteral(
/*  7495 */         this.scanner.getCurrentTokenSource(), 
/*  7496 */         this.scanner.startPosition, 
/*  7497 */         this.scanner.currentPosition - 1));
/*  7498 */       break;
/*       */     case 50:
/*  7500 */       pushOnExpressionStack(
/*  7501 */         new DoubleLiteral(
/*  7502 */         this.scanner.getCurrentTokenSource(), 
/*  7503 */         this.scanner.startPosition, 
/*  7504 */         this.scanner.currentPosition - 1));
/*  7505 */       break;
/*       */     case 51:
/*  7507 */       pushOnExpressionStack(
/*  7508 */         new CharLiteral(
/*  7509 */         this.scanner.getCurrentTokenSource(), 
/*  7510 */         this.scanner.startPosition, 
/*  7511 */         this.scanner.currentPosition - 1));
/*  7512 */       break;
/*       */     case 52:
/*       */       StringLiteral stringLiteral;
/*  7515 */       if ((this.recordStringLiterals) && 
/*  7516 */         (this.checkExternalizeStrings) && 
/*  7517 */         (this.lastPosistion < this.scanner.currentPosition) && 
/*  7518 */         (!this.statementRecoveryActivated)) {
/*  7519 */         StringLiteral stringLiteral = createStringLiteral(
/*  7520 */           this.scanner.getCurrentTokenSourceString(), 
/*  7521 */           this.scanner.startPosition, 
/*  7522 */           this.scanner.currentPosition - 1, 
/*  7523 */           Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
/*  7524 */         this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
/*       */       } else {
/*  7526 */         stringLiteral = createStringLiteral(
/*  7527 */           this.scanner.getCurrentTokenSourceString(), 
/*  7528 */           this.scanner.startPosition, 
/*  7529 */           this.scanner.currentPosition - 1, 
/*  7530 */           0);
/*       */       }
/*  7532 */       pushOnExpressionStack(stringLiteral);
/*  7533 */       break;
/*       */     case 44:
/*  7535 */       pushOnExpressionStack(
/*  7536 */         new FalseLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
/*  7537 */       break;
/*       */     case 46:
/*  7539 */       pushOnExpressionStack(
/*  7540 */         new TrueLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
/*  7541 */       break;
/*       */     case 45:
/*  7543 */       pushOnExpressionStack(
/*  7544 */         new NullLiteral(this.scanner.startPosition, this.scanner.currentPosition - 1));
/*  7545 */       break;
/*       */     case 41:
/*       */     case 42:
/*  7549 */       this.endPosition = (this.scanner.currentPosition - 1);
/*  7550 */       pushOnIntStack(this.scanner.startPosition);
/*  7551 */       break;
/*       */     case 73:
/*       */     case 74:
/*       */     case 75:
/*       */     case 76:
/*       */     case 77:
/*       */     case 78:
/*       */     case 79:
/*       */     case 80:
/*       */     case 81:
/*       */     case 82:
/*       */     case 83:
/*       */     case 96:
/*       */     case 100:
/*       */     case 101:
/*  7566 */       pushOnIntStack(this.scanner.startPosition);
/*  7567 */       break;
/*       */     case 43:
/*  7570 */       resetModifiers();
/*  7571 */       pushOnIntStack(this.scanner.startPosition);
/*  7572 */       break;
/*       */     case 72:
/*  7574 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7575 */       pushOnIntStack(this.scanner.startPosition);
/*  7576 */       break;
/*       */     case 98:
/*  7578 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7579 */       pushOnIntStack(this.scanner.startPosition);
/*  7580 */       break;
/*       */     case 97:
/*  7582 */       pushOnIntStack(this.scanner.startPosition);
/*  7583 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7584 */       break;
/*       */     case 70:
/*  7587 */       this.endPosition = this.scanner.startPosition;
/*  7588 */       this.endStatementPosition = (this.scanner.currentPosition - 1);
/*  7589 */       break;
/*       */     case 69:
/*  7591 */       this.endStatementPosition = (this.scanner.currentPosition - 1);
/*       */     case 1:
/*       */     case 2:
/*       */     case 66:
/*       */     case 67:
/*  7597 */       this.endPosition = this.scanner.startPosition;
/*  7598 */       break;
/*       */     case 8:
/*       */     case 9:
/*  7601 */       this.endPosition = this.scanner.startPosition;
/*  7602 */       this.endStatementPosition = (this.scanner.currentPosition - 1);
/*  7603 */       break;
/*       */     case 27:
/*       */     case 31:
/*  7606 */       this.endStatementPosition = (this.scanner.currentPosition - 1);
/*  7607 */       this.endPosition = (this.scanner.startPosition - 1);
/*       */ 
/*  7609 */       break;
/*       */     case 29:
/*  7612 */       this.rParenPos = (this.scanner.currentPosition - 1);
/*  7613 */       break;
/*       */     case 28:
/*  7615 */       this.lParenPos = this.scanner.startPosition;
/*  7616 */       break;
/*       */     case 53:
/*  7618 */       pushOnIntStack(this.scanner.startPosition);
/*  7619 */       break;
/*       */     case 23:
/*  7621 */       pushOnIntStack(this.scanner.startPosition);
/*  7622 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7623 */       break;
/*       */     case 7:
/*  7625 */       pushOnIntStack(this.scanner.startPosition);
/*  7626 */       break;
/*       */     case 107:
/*  7628 */       pushOnIntStack(this.scanner.currentPosition - 1);
/*  7629 */       break;
/*       */     case 71:
/*  7631 */       if ((this.currentElement == null) || (!(this.currentElement instanceof RecoveredAnnotation))) break;
/*  7632 */       RecoveredAnnotation recoveredAnnotation = (RecoveredAnnotation)this.currentElement;
/*  7633 */       if (recoveredAnnotation.memberValuPairEqualEnd != -1) break;
/*  7634 */       recoveredAnnotation.memberValuPairEqualEnd = (this.scanner.currentPosition - 1);
/*       */     case 3:
/*       */     case 4:
/*       */     case 5:
/*       */     case 6:
/*       */     case 10:
/*       */     case 11:
/*       */     case 12:
/*       */     case 13:
/*       */     case 14:
/*       */     case 15:
/*       */     case 16:
/*       */     case 17:
/*       */     case 18:
/*       */     case 19:
/*       */     case 20:
/*       */     case 21:
/*       */     case 22:
/*       */     case 24:
/*       */     case 25:
/*       */     case 30:
/*       */     case 65:
/*       */     case 68:
/*       */     case 84:
/*       */     case 85:
/*       */     case 86:
/*       */     case 87:
/*       */     case 88:
/*       */     case 89:
/*       */     case 90:
/*       */     case 91:
/*       */     case 92:
/*       */     case 93:
/*       */     case 94:
/*       */     case 99:
/*       */     case 102:
/*       */     case 103:
/*       */     case 104:
/*       */     case 105:
/*       */     case 106:
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeTypeArgument()
/*       */   {
/*  7682 */     pushOnGenericsStack(getTypeReference(this.intStack[(this.intPtr--)]));
/*       */   }
/*       */   protected void consumeTypeArgumentList() {
/*  7685 */     concatGenericsLists();
/*       */   }
/*       */   protected void consumeTypeArgumentList1() {
/*  7688 */     concatGenericsLists();
/*       */   }
/*       */   protected void consumeTypeArgumentList2() {
/*  7691 */     concatGenericsLists();
/*       */   }
/*       */   protected void consumeTypeArgumentList3() {
/*  7694 */     concatGenericsLists();
/*       */   }
/*       */   protected void consumeTypeArgumentReferenceType1() {
/*  7697 */     concatGenericsLists();
/*  7698 */     pushOnGenericsStack(getTypeReference(0));
/*  7699 */     this.intPtr -= 1;
/*       */   }
/*       */   protected void consumeTypeArgumentReferenceType2() {
/*  7702 */     concatGenericsLists();
/*  7703 */     pushOnGenericsStack(getTypeReference(0));
/*  7704 */     this.intPtr -= 1;
/*       */   }
/*       */   protected void consumeTypeArguments() {
/*  7707 */     concatGenericsLists();
/*  7708 */     this.intPtr -= 1;
/*       */ 
/*  7710 */     if ((!this.statementRecoveryActivated) && 
/*  7711 */       (this.options.sourceLevel < 3211264L) && 
/*  7712 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  7713 */       int length = this.genericsLengthStack[this.genericsLengthPtr];
/*  7714 */       problemReporter().invalidUsageOfTypeArguments(
/*  7715 */         (TypeReference)this.genericsStack[(this.genericsPtr - length + 1)], 
/*  7716 */         (TypeReference)this.genericsStack[this.genericsPtr]);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeTypeDeclarations() {
/*  7721 */     concatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeTypeHeaderNameWithTypeParameters()
/*       */   {
/*  7726 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*       */ 
/*  7729 */     int length = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  7730 */     this.genericsPtr -= length;
/*  7731 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeDecl.typeParameters = new TypeParameter[length], 0, length);
/*       */ 
/*  7733 */     typeDecl.bodyStart = (typeDecl.typeParameters[(length - 1)].declarationSourceEnd + 1);
/*       */ 
/*  7735 */     this.listTypeParameterLength = 0;
/*       */ 
/*  7737 */     if (this.currentElement != null) {
/*  7738 */       RecoveredType recoveredType = (RecoveredType)this.currentElement;
/*  7739 */       recoveredType.pendingTypeParameters = null;
/*       */ 
/*  7741 */       this.lastCheckPoint = typeDecl.bodyStart;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeTypeImportOnDemandDeclarationName()
/*       */   {
/*       */     int length;
/*  7751 */     char[][] tokens = new char[length = this.identifierLengthStack[(this.identifierLengthPtr--)]][];
/*  7752 */     this.identifierPtr -= length;
/*  7753 */     long[] positions = new long[length];
/*  7754 */     System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  7755 */     System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
/*       */     ImportReference impt;
/*  7756 */     pushOnAstStack(impt = new ImportReference(tokens, positions, true, 0));
/*       */ 
/*  7758 */     if (this.currentToken == 27)
/*  7759 */       impt.declarationSourceEnd = (this.scanner.currentPosition - 1);
/*       */     else {
/*  7761 */       impt.declarationSourceEnd = impt.sourceEnd;
/*       */     }
/*  7763 */     impt.declarationEnd = impt.declarationSourceEnd;
/*       */ 
/*  7765 */     impt.declarationSourceStart = this.intStack[(this.intPtr--)];
/*       */ 
/*  7768 */     if (this.currentElement != null) {
/*  7769 */       this.lastCheckPoint = (impt.declarationSourceEnd + 1);
/*  7770 */       this.currentElement = this.currentElement.add(impt, 0);
/*  7771 */       this.lastIgnoredToken = -1;
/*  7772 */       this.restartRecovery = true;
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameter1() {
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameter1WithExtends() {
/*  7780 */     TypeReference superType = (TypeReference)this.genericsStack[(this.genericsPtr--)];
/*  7781 */     this.genericsLengthPtr -= 1;
/*  7782 */     TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
/*  7783 */     typeParameter.declarationSourceEnd = superType.sourceEnd;
/*  7784 */     typeParameter.type = superType;
/*  7785 */     superType.bits |= 16;
/*  7786 */     this.genericsStack[this.genericsPtr] = typeParameter;
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameter1WithExtendsAndBounds() {
/*  7790 */     int additionalBoundsLength = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  7791 */     TypeReference[] bounds = new TypeReference[additionalBoundsLength];
/*  7792 */     this.genericsPtr -= additionalBoundsLength;
/*  7793 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
/*  7794 */     TypeReference superType = getTypeReference(this.intStack[(this.intPtr--)]);
/*  7795 */     TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
/*  7796 */     typeParameter.declarationSourceEnd = bounds[(additionalBoundsLength - 1)].sourceEnd;
/*  7797 */     typeParameter.type = superType;
/*  7798 */     superType.bits |= 16;
/*  7799 */     typeParameter.bounds = bounds;
/*  7800 */     int i = 0; for (int max = bounds.length; i < max; i++)
/*  7801 */       bounds[i].bits |= 16;
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameterHeader()
/*       */   {
/*  7806 */     TypeParameter typeParameter = new TypeParameter();
/*  7807 */     long pos = this.identifierPositionStack[this.identifierPtr];
/*  7808 */     int end = (int)pos;
/*  7809 */     typeParameter.declarationSourceEnd = end;
/*  7810 */     typeParameter.sourceEnd = end;
/*  7811 */     int start = (int)(pos >>> 32);
/*  7812 */     typeParameter.declarationSourceStart = start;
/*  7813 */     typeParameter.sourceStart = start;
/*  7814 */     typeParameter.name = this.identifierStack[(this.identifierPtr--)];
/*  7815 */     this.identifierLengthPtr -= 1;
/*  7816 */     pushOnGenericsStack(typeParameter);
/*       */ 
/*  7818 */     this.listTypeParameterLength += 1;
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameterList() {
/*  7822 */     concatGenericsLists();
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameterList1() {
/*  7826 */     concatGenericsLists();
/*       */   }
/*       */   protected void consumeTypeParameters() {
/*  7829 */     int startPos = this.intStack[(this.intPtr--)];
/*       */ 
/*  7831 */     if ((this.currentElement != null) && 
/*  7832 */       ((this.currentElement instanceof RecoveredType))) {
/*  7833 */       RecoveredType recoveredType = (RecoveredType)this.currentElement;
/*  7834 */       int length = this.genericsLengthStack[this.genericsLengthPtr];
/*  7835 */       TypeParameter[] typeParameters = new TypeParameter[length];
/*  7836 */       System.arraycopy(this.genericsStack, this.genericsPtr - length + 1, typeParameters, 0, length);
/*       */ 
/*  7838 */       recoveredType.add(typeParameters, startPos);
/*       */     }
/*       */ 
/*  7843 */     if ((!this.statementRecoveryActivated) && 
/*  7844 */       (this.options.sourceLevel < 3211264L) && 
/*  7845 */       (this.lastErrorEndPositionBeforeRecovery < this.scanner.currentPosition)) {
/*  7846 */       int length = this.genericsLengthStack[this.genericsLengthPtr];
/*  7847 */       problemReporter().invalidUsageOfTypeParameters(
/*  7848 */         (TypeParameter)this.genericsStack[(this.genericsPtr - length + 1)], 
/*  7849 */         (TypeParameter)this.genericsStack[this.genericsPtr]);
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameterWithExtends() {
/*  7854 */     TypeReference superType = getTypeReference(this.intStack[(this.intPtr--)]);
/*  7855 */     TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
/*  7856 */     typeParameter.declarationSourceEnd = superType.sourceEnd;
/*  7857 */     typeParameter.type = superType;
/*  7858 */     superType.bits |= 16;
/*       */   }
/*       */ 
/*       */   protected void consumeTypeParameterWithExtendsAndBounds() {
/*  7862 */     int additionalBoundsLength = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  7863 */     TypeReference[] bounds = new TypeReference[additionalBoundsLength];
/*  7864 */     this.genericsPtr -= additionalBoundsLength;
/*  7865 */     System.arraycopy(this.genericsStack, this.genericsPtr + 1, bounds, 0, additionalBoundsLength);
/*  7866 */     TypeReference superType = getTypeReference(this.intStack[(this.intPtr--)]);
/*  7867 */     TypeParameter typeParameter = (TypeParameter)this.genericsStack[this.genericsPtr];
/*  7868 */     typeParameter.type = superType;
/*  7869 */     superType.bits |= 16;
/*  7870 */     typeParameter.bounds = bounds;
/*  7871 */     typeParameter.declarationSourceEnd = bounds[(additionalBoundsLength - 1)].sourceEnd;
/*  7872 */     int i = 0; for (int max = bounds.length; i < max; i++)
/*  7873 */       bounds[i].bits |= 16;
/*       */   }
/*       */ 
/*       */   protected void consumeUnaryExpression(int op)
/*       */   {
/*  7891 */     Expression exp = this.expressionStack[this.expressionPtr];
/*       */     Expression r;
/*       */     Expression r;
/*  7892 */     if (op == 13)
/*       */     {
/*       */       Expression r;
/*  7893 */       if (((exp instanceof IntLiteral)) && (((IntLiteral)exp).mayRepresentMIN_VALUE())) {
/*  7894 */         r = this.expressionStack[this.expressionPtr] =  = new IntLiteralMinValue();
/*       */       }
/*       */       else
/*       */       {
/*       */         Expression r;
/*  7896 */         if (((exp instanceof LongLiteral)) && (((LongLiteral)exp).mayRepresentMIN_VALUE()))
/*  7897 */           r = this.expressionStack[this.expressionPtr] =  = new LongLiteralMinValue();
/*       */         else
/*  7899 */           r = this.expressionStack[this.expressionPtr] =  = new UnaryExpression(exp, op);
/*       */       }
/*       */     }
/*       */     else {
/*  7903 */       r = this.expressionStack[this.expressionPtr] =  = new UnaryExpression(exp, op);
/*       */     }
/*  7905 */     r.sourceStart = this.intStack[(this.intPtr--)];
/*  7906 */     r.sourceEnd = exp.sourceEnd;
/*       */   }
/*       */ 
/*       */   protected void consumeUnaryExpression(int op, boolean post)
/*       */   {
/*  7917 */     Expression leftHandSide = this.expressionStack[this.expressionPtr];
/*  7918 */     if ((leftHandSide instanceof Reference))
/*       */     {
/*  7920 */       if (post)
/*  7921 */         this.expressionStack[this.expressionPtr] = 
/*  7922 */           new PostfixExpression(
/*  7923 */           leftHandSide, 
/*  7924 */           IntLiteral.One, 
/*  7925 */           op, 
/*  7926 */           this.endStatementPosition);
/*       */       else
/*  7928 */         this.expressionStack[this.expressionPtr] = 
/*  7929 */           new PrefixExpression(
/*  7930 */           leftHandSide, 
/*  7931 */           IntLiteral.One, 
/*  7932 */           op, 
/*  7933 */           this.intStack[(this.intPtr--)]);
/*       */     }
/*       */     else
/*       */     {
/*  7937 */       if (!post) {
/*  7938 */         this.intPtr -= 1;
/*       */       }
/*  7940 */       if (!this.statementRecoveryActivated) problemReporter().invalidUnaryExpression(leftHandSide); 
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void consumeVariableDeclarators()
/*       */   {
/*  7945 */     optimizedConcatNodeLists();
/*       */   }
/*       */ 
/*       */   protected void consumeVariableInitializers() {
/*  7949 */     concatExpressionLists();
/*       */   }
/*       */   protected void consumeWildcard() {
/*  7952 */     Wildcard wildcard = new Wildcard(0);
/*  7953 */     wildcard.sourceEnd = this.intStack[(this.intPtr--)];
/*  7954 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  7955 */     pushOnGenericsStack(wildcard);
/*       */   }
/*       */   protected void consumeWildcard1() {
/*  7958 */     Wildcard wildcard = new Wildcard(0);
/*  7959 */     wildcard.sourceEnd = this.intStack[(this.intPtr--)];
/*  7960 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  7961 */     pushOnGenericsStack(wildcard);
/*       */   }
/*       */ 
/*       */   protected void consumeWildcard1WithBounds() {
/*       */   }
/*       */ 
/*       */   protected void consumeWildcard2() {
/*  7968 */     Wildcard wildcard = new Wildcard(0);
/*  7969 */     wildcard.sourceEnd = this.intStack[(this.intPtr--)];
/*  7970 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  7971 */     pushOnGenericsStack(wildcard);
/*       */   }
/*       */ 
/*       */   protected void consumeWildcard2WithBounds() {
/*       */   }
/*       */ 
/*       */   protected void consumeWildcard3() {
/*  7978 */     Wildcard wildcard = new Wildcard(0);
/*  7979 */     wildcard.sourceEnd = this.intStack[(this.intPtr--)];
/*  7980 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  7981 */     pushOnGenericsStack(wildcard);
/*       */   }
/*       */ 
/*       */   protected void consumeWildcard3WithBounds() {
/*       */   }
/*       */ 
/*       */   protected void consumeWildcardBounds1Extends() {
/*  7988 */     Wildcard wildcard = new Wildcard(1);
/*  7989 */     wildcard.bound = ((TypeReference)this.genericsStack[this.genericsPtr]);
/*  7990 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  7991 */     this.intPtr -= 1;
/*  7992 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  7993 */     this.genericsStack[this.genericsPtr] = wildcard;
/*       */   }
/*       */   protected void consumeWildcardBounds1Super() {
/*  7996 */     Wildcard wildcard = new Wildcard(2);
/*  7997 */     wildcard.bound = ((TypeReference)this.genericsStack[this.genericsPtr]);
/*  7998 */     this.intPtr -= 1;
/*  7999 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  8000 */     this.intPtr -= 1;
/*  8001 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  8002 */     this.genericsStack[this.genericsPtr] = wildcard;
/*       */   }
/*       */   protected void consumeWildcardBounds2Extends() {
/*  8005 */     Wildcard wildcard = new Wildcard(1);
/*  8006 */     wildcard.bound = ((TypeReference)this.genericsStack[this.genericsPtr]);
/*  8007 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  8008 */     this.intPtr -= 1;
/*  8009 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  8010 */     this.genericsStack[this.genericsPtr] = wildcard;
/*       */   }
/*       */   protected void consumeWildcardBounds2Super() {
/*  8013 */     Wildcard wildcard = new Wildcard(2);
/*  8014 */     wildcard.bound = ((TypeReference)this.genericsStack[this.genericsPtr]);
/*  8015 */     this.intPtr -= 1;
/*  8016 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  8017 */     this.intPtr -= 1;
/*  8018 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  8019 */     this.genericsStack[this.genericsPtr] = wildcard;
/*       */   }
/*       */   protected void consumeWildcardBounds3Extends() {
/*  8022 */     Wildcard wildcard = new Wildcard(1);
/*  8023 */     wildcard.bound = ((TypeReference)this.genericsStack[this.genericsPtr]);
/*  8024 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  8025 */     this.intPtr -= 1;
/*  8026 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  8027 */     this.genericsStack[this.genericsPtr] = wildcard;
/*       */   }
/*       */   protected void consumeWildcardBounds3Super() {
/*  8030 */     Wildcard wildcard = new Wildcard(2);
/*  8031 */     wildcard.bound = ((TypeReference)this.genericsStack[this.genericsPtr]);
/*  8032 */     this.intPtr -= 1;
/*  8033 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  8034 */     this.intPtr -= 1;
/*  8035 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  8036 */     this.genericsStack[this.genericsPtr] = wildcard;
/*       */   }
/*       */   protected void consumeWildcardBoundsExtends() {
/*  8039 */     Wildcard wildcard = new Wildcard(1);
/*  8040 */     wildcard.bound = getTypeReference(this.intStack[(this.intPtr--)]);
/*  8041 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  8042 */     this.intPtr -= 1;
/*  8043 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  8044 */     pushOnGenericsStack(wildcard);
/*       */   }
/*       */   protected void consumeWildcardBoundsSuper() {
/*  8047 */     Wildcard wildcard = new Wildcard(2);
/*  8048 */     wildcard.bound = getTypeReference(this.intStack[(this.intPtr--)]);
/*  8049 */     this.intPtr -= 1;
/*  8050 */     wildcard.sourceEnd = wildcard.bound.sourceEnd;
/*  8051 */     this.intPtr -= 1;
/*  8052 */     wildcard.sourceStart = this.intStack[(this.intPtr--)];
/*  8053 */     pushOnGenericsStack(wildcard);
/*       */   }
/*       */ 
/*       */   protected void consumeWildcardWithBounds()
/*       */   {
/*       */   }
/*       */ 
/*       */   public boolean containsComment(int sourceStart, int sourceEnd)
/*       */   {
/*  8067 */     int iComment = this.scanner.commentPtr;
/*  8068 */     for (; iComment >= 0; iComment--) {
/*  8069 */       int commentStart = this.scanner.commentStarts[iComment];
/*  8070 */       if (commentStart < 0) commentStart = -commentStart;
/*       */ 
/*  8072 */       if (commentStart < sourceStart)
/*       */         continue;
/*  8074 */       if (commentStart <= sourceEnd)
/*  8075 */         return true;
/*       */     }
/*  8077 */     return false;
/*       */   }
/*       */ 
/*       */   public MethodDeclaration convertToMethodDeclaration(ConstructorDeclaration c, CompilationResult compilationResult) {
/*  8081 */     MethodDeclaration m = new MethodDeclaration(compilationResult);
/*  8082 */     m.typeParameters = c.typeParameters;
/*  8083 */     m.sourceStart = c.sourceStart;
/*  8084 */     m.sourceEnd = c.sourceEnd;
/*  8085 */     m.bodyStart = c.bodyStart;
/*  8086 */     m.bodyEnd = c.bodyEnd;
/*  8087 */     m.declarationSourceEnd = c.declarationSourceEnd;
/*  8088 */     m.declarationSourceStart = c.declarationSourceStart;
/*  8089 */     m.selector = c.selector;
/*  8090 */     m.statements = c.statements;
/*  8091 */     m.modifiers = c.modifiers;
/*  8092 */     m.annotations = c.annotations;
/*  8093 */     m.arguments = c.arguments;
/*  8094 */     m.thrownExceptions = c.thrownExceptions;
/*  8095 */     m.explicitDeclarations = c.explicitDeclarations;
/*  8096 */     m.returnType = null;
/*  8097 */     m.javadoc = c.javadoc;
/*  8098 */     return m;
/*       */   }
/*       */ 
/*       */   protected TypeReference copyDims(TypeReference typeRef, int dim) {
/*  8102 */     return typeRef.copyDims(dim);
/*       */   }
/*       */   protected FieldDeclaration createFieldDeclaration(char[] fieldDeclarationName, int sourceStart, int sourceEnd) {
/*  8105 */     return new FieldDeclaration(fieldDeclarationName, sourceStart, sourceEnd);
/*       */   }
/*       */   protected JavadocParser createJavadocParser() {
/*  8108 */     return new JavadocParser(this);
/*       */   }
/*       */   protected LocalDeclaration createLocalDeclaration(char[] localDeclarationName, int sourceStart, int sourceEnd) {
/*  8111 */     return new LocalDeclaration(localDeclarationName, sourceStart, sourceEnd);
/*       */   }
/*       */   protected StringLiteral createStringLiteral(char[] token, int start, int end, int lineNumber) {
/*  8114 */     return new StringLiteral(token, start, end, lineNumber);
/*       */   }
/*       */   protected RecoveredType currentRecoveryType() {
/*  8117 */     if (this.currentElement != null) {
/*  8118 */       if ((this.currentElement instanceof RecoveredType)) {
/*  8119 */         return (RecoveredType)this.currentElement;
/*       */       }
/*  8121 */       return this.currentElement.enclosingType();
/*       */     }
/*       */ 
/*  8124 */     return null;
/*       */   }
/*       */   public CompilationUnitDeclaration dietParse(ICompilationUnit sourceUnit, CompilationResult compilationResult) {
/*  8129 */     boolean old = this.diet;
/*       */     CompilationUnitDeclaration parsedUnit;
/*       */     try { this.diet = true;
/*  8132 */       parsedUnit = parse(sourceUnit, compilationResult);
/*       */     }
/*       */     finally
/*       */     {
/*       */       CompilationUnitDeclaration parsedUnit;
/*  8134 */       this.diet = old;
/*       */     }
/*  8136 */     return parsedUnit;
/*       */   }
/*       */ 
/*       */   protected void dispatchDeclarationInto(int length)
/*       */   {
/*  8147 */     if (length == 0)
/*  8148 */       return;
/*  8149 */     int[] flag = new int[length + 1];
/*  8150 */     int size1 = 0; int size2 = 0; int size3 = 0;
/*  8151 */     boolean hasAbstractMethods = false;
/*  8152 */     for (int i = length - 1; i >= 0; i--) {
/*  8153 */       ASTNode astNode = this.astStack[(this.astPtr--)];
/*  8154 */       if ((astNode instanceof AbstractMethodDeclaration))
/*       */       {
/*  8156 */         flag[i] = 2;
/*  8157 */         size2++;
/*  8158 */         if (((AbstractMethodDeclaration)astNode).isAbstract())
/*  8159 */           hasAbstractMethods = true;
/*       */       }
/*  8161 */       else if ((astNode instanceof TypeDeclaration)) {
/*  8162 */         flag[i] = 3;
/*  8163 */         size3++;
/*       */       }
/*       */       else {
/*  8166 */         flag[i] = 1;
/*  8167 */         size1++;
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  8172 */     TypeDeclaration typeDecl = (TypeDeclaration)this.astStack[this.astPtr];
/*  8173 */     if (size1 != 0) {
/*  8174 */       typeDecl.fields = new FieldDeclaration[size1];
/*       */     }
/*  8176 */     if (size2 != 0) {
/*  8177 */       typeDecl.methods = new AbstractMethodDeclaration[size2];
/*  8178 */       if (hasAbstractMethods) typeDecl.bits |= 2048;
/*       */     }
/*  8180 */     if (size3 != 0) {
/*  8181 */       typeDecl.memberTypes = new TypeDeclaration[size3];
/*       */     }
/*       */ 
/*  8185 */     size1 = size2 = size3 = 0;
/*  8186 */     int flagI = flag[0]; int start = 0;
/*       */ 
/*  8188 */     for (int end = 0; end <= length; end++)
/*       */     {
/*  8190 */       if (flagI == flag[end])
/*       */         continue;
/*  8192 */       switch (flagI)
/*       */       {
/*       */       case 1:
/*       */         int length2;
/*  8194 */         size1 += (length2 = end - start);
/*  8195 */         System.arraycopy(
/*  8196 */           this.astStack, 
/*  8197 */           this.astPtr + start + 1, 
/*  8198 */           typeDecl.fields, 
/*  8199 */           size1 - length2, 
/*  8200 */           length2);
/*  8201 */         break;
/*       */       case 2:
/*       */         int length2;
/*  8203 */         size2 += (length2 = end - start);
/*  8204 */         System.arraycopy(
/*  8205 */           this.astStack, 
/*  8206 */           this.astPtr + start + 1, 
/*  8207 */           typeDecl.methods, 
/*  8208 */           size2 - length2, 
/*  8209 */           length2);
/*  8210 */         break;
/*       */       case 3:
/*       */         int length2;
/*  8212 */         size3 += (length2 = end - start);
/*  8213 */         System.arraycopy(
/*  8214 */           this.astStack, 
/*  8215 */           this.astPtr + start + 1, 
/*  8216 */           typeDecl.memberTypes, 
/*  8217 */           size3 - length2, 
/*  8218 */           length2);
/*       */       }
/*       */ 
/*  8221 */       flagI = flag[(start = end)];
/*       */     }
/*       */ 
/*  8225 */     if (typeDecl.memberTypes != null)
/*  8226 */       for (int i = typeDecl.memberTypes.length - 1; i >= 0; i--)
/*  8227 */         typeDecl.memberTypes[i].enclosingType = typeDecl;
/*       */   }
/*       */ 
/*       */   protected void dispatchDeclarationIntoEnumDeclaration(int length)
/*       */   {
/*  8233 */     if (length == 0)
/*  8234 */       return;
/*  8235 */     int[] flag = new int[length + 1];
/*  8236 */     int size1 = 0; int size2 = 0; int size3 = 0;
/*  8237 */     TypeDeclaration enumDeclaration = (TypeDeclaration)this.astStack[(this.astPtr - length)];
/*  8238 */     boolean hasAbstractMethods = false;
/*  8239 */     for (int i = length - 1; i >= 0; i--) {
/*  8240 */       ASTNode astNode = this.astStack[(this.astPtr--)];
/*  8241 */       if ((astNode instanceof AbstractMethodDeclaration))
/*       */       {
/*  8243 */         flag[i] = 2;
/*  8244 */         size2++;
/*  8245 */         if (((AbstractMethodDeclaration)astNode).isAbstract())
/*  8246 */           hasAbstractMethods = true;
/*       */       }
/*  8248 */       else if ((astNode instanceof TypeDeclaration)) {
/*  8249 */         flag[i] = 3;
/*  8250 */         size3++;
/*  8251 */       } else if ((astNode instanceof FieldDeclaration)) {
/*  8252 */         flag[i] = 1;
/*  8253 */         size1++;
/*       */       }
/*       */ 
/*       */     }
/*       */ 
/*  8263 */     if (size1 != 0) {
/*  8264 */       enumDeclaration.fields = new FieldDeclaration[size1];
/*       */     }
/*  8266 */     if (size2 != 0) {
/*  8267 */       enumDeclaration.methods = new AbstractMethodDeclaration[size2];
/*  8268 */       if (hasAbstractMethods) enumDeclaration.bits |= 2048;
/*       */     }
/*  8270 */     if (size3 != 0) {
/*  8271 */       enumDeclaration.memberTypes = new TypeDeclaration[size3];
/*       */     }
/*       */ 
/*  8275 */     size1 = size2 = size3 = 0;
/*  8276 */     int flagI = flag[0]; int start = 0;
/*       */ 
/*  8278 */     for (int end = 0; end <= length; end++)
/*       */     {
/*  8280 */       if (flagI == flag[end])
/*       */         continue;
/*  8282 */       switch (flagI)
/*       */       {
/*       */       case 1:
/*       */         int length2;
/*  8284 */         size1 += (length2 = end - start);
/*  8285 */         System.arraycopy(
/*  8286 */           this.astStack, 
/*  8287 */           this.astPtr + start + 1, 
/*  8288 */           enumDeclaration.fields, 
/*  8289 */           size1 - length2, 
/*  8290 */           length2);
/*  8291 */         break;
/*       */       case 2:
/*       */         int length2;
/*  8293 */         size2 += (length2 = end - start);
/*  8294 */         System.arraycopy(
/*  8295 */           this.astStack, 
/*  8296 */           this.astPtr + start + 1, 
/*  8297 */           enumDeclaration.methods, 
/*  8298 */           size2 - length2, 
/*  8299 */           length2);
/*  8300 */         break;
/*       */       case 3:
/*       */         int length2;
/*  8302 */         size3 += (length2 = end - start);
/*  8303 */         System.arraycopy(
/*  8304 */           this.astStack, 
/*  8305 */           this.astPtr + start + 1, 
/*  8306 */           enumDeclaration.memberTypes, 
/*  8307 */           size3 - length2, 
/*  8308 */           length2);
/*       */       }
/*       */ 
/*  8311 */       flagI = flag[(start = end)];
/*       */     }
/*       */ 
/*  8315 */     if (enumDeclaration.memberTypes != null)
/*  8316 */       for (int i = enumDeclaration.memberTypes.length - 1; i >= 0; i--)
/*  8317 */         enumDeclaration.memberTypes[i].enclosingType = enumDeclaration;
/*       */   }
/*       */ 
/*       */   protected CompilationUnitDeclaration endParse(int act)
/*       */   {
/*  8322 */     this.lastAct = act;
/*       */ 
/*  8324 */     if (this.statementRecoveryActivated) {
/*  8325 */       RecoveredElement recoveredElement = buildInitialRecoveryState();
/*       */ 
/*  8327 */       if (recoveredElement != null) {
/*  8328 */         recoveredElement.topElement().updateParseTree();
/*       */       }
/*       */ 
/*  8331 */       if (this.hasError) resetStacks(); 
/*       */     }
/*  8332 */     else if (this.currentElement != null) {
/*  8333 */       if (VERBOSE_RECOVERY) {
/*  8334 */         System.out.print(Messages.parser_syntaxRecovery);
/*  8335 */         System.out.println("--------------------------");
/*  8336 */         System.out.println(this.compilationUnit);
/*  8337 */         System.out.println("----------------------------------");
/*       */       }
/*  8339 */       this.currentElement.topElement().updateParseTree();
/*       */     }
/*  8341 */     else if ((this.diet & VERBOSE_RECOVERY)) {
/*  8342 */       System.out.print(Messages.parser_regularParse);
/*  8343 */       System.out.println("--------------------------");
/*  8344 */       System.out.println(this.compilationUnit);
/*  8345 */       System.out.println("----------------------------------");
/*       */     }
/*       */ 
/*  8348 */     persistLineSeparatorPositions();
/*  8349 */     for (int i = 0; i < this.scanner.foundTaskCount; i++) {
/*  8350 */       if (this.statementRecoveryActivated) continue; problemReporter().task(
/*  8351 */         new String(this.scanner.foundTaskTags[i]), 
/*  8352 */         new String(this.scanner.foundTaskMessages[i]), 
/*  8353 */         this.scanner.foundTaskPriorities[i] == null ? null : new String(this.scanner.foundTaskPriorities[i]), 
/*  8354 */         this.scanner.foundTaskPositions[i][0], 
/*  8355 */         this.scanner.foundTaskPositions[i][1]);
/*       */     }
/*  8357 */     return this.compilationUnit;
/*       */   }
/*       */ 
/*       */   public int flushCommentsDefinedPriorTo(int position)
/*       */   {
/*  8373 */     int lastCommentIndex = this.scanner.commentPtr;
/*  8374 */     if (lastCommentIndex < 0) return position;
/*       */ 
/*  8377 */     int index = lastCommentIndex;
/*  8378 */     int validCount = 0;
/*  8379 */     while (index >= 0) {
/*  8380 */       int commentEnd = this.scanner.commentStops[index];
/*  8381 */       if (commentEnd < 0) commentEnd = -commentEnd;
/*  8382 */       if (commentEnd <= position) {
/*       */         break;
/*       */       }
/*  8385 */       index--;
/*  8386 */       validCount++;
/*       */     }
/*       */ 
/*  8390 */     if (validCount > 0) {
/*  8391 */       int immediateCommentEnd = -this.scanner.commentStops[(index + 1)];
/*  8392 */       if (immediateCommentEnd > 0)
/*       */       {
/*  8394 */         immediateCommentEnd--;
/*  8395 */         if (Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr) == 
/*  8396 */           Util.getLineNumber(immediateCommentEnd, this.scanner.lineEnds, 0, this.scanner.linePtr)) {
/*  8397 */           position = immediateCommentEnd;
/*  8398 */           validCount--;
/*  8399 */           index++;
/*       */         }
/*       */       }
/*       */     }
/*       */ 
/*  8404 */     if (index < 0) return position;
/*       */ 
/*  8406 */     switch (validCount)
/*       */     {
/*       */     case 0:
/*  8409 */       break;
/*       */     case 2:
/*  8412 */       this.scanner.commentStarts[0] = this.scanner.commentStarts[(index + 1)];
/*  8413 */       this.scanner.commentStops[0] = this.scanner.commentStops[(index + 1)];
/*  8414 */       this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[(index + 1)];
/*  8415 */       this.scanner.commentStarts[1] = this.scanner.commentStarts[(index + 2)];
/*  8416 */       this.scanner.commentStops[1] = this.scanner.commentStops[(index + 2)];
/*  8417 */       this.scanner.commentTagStarts[1] = this.scanner.commentTagStarts[(index + 2)];
/*  8418 */       break;
/*       */     case 1:
/*  8420 */       this.scanner.commentStarts[0] = this.scanner.commentStarts[(index + 1)];
/*  8421 */       this.scanner.commentStops[0] = this.scanner.commentStops[(index + 1)];
/*  8422 */       this.scanner.commentTagStarts[0] = this.scanner.commentTagStarts[(index + 1)];
/*  8423 */       break;
/*       */     default:
/*  8425 */       System.arraycopy(this.scanner.commentStarts, index + 1, this.scanner.commentStarts, 0, validCount);
/*  8426 */       System.arraycopy(this.scanner.commentStops, index + 1, this.scanner.commentStops, 0, validCount);
/*  8427 */       System.arraycopy(this.scanner.commentTagStarts, index + 1, this.scanner.commentTagStarts, 0, validCount);
/*       */     }
/*  8429 */     this.scanner.commentPtr = (validCount - 1);
/*  8430 */     return position;
/*       */   }
/*       */ 
/*       */   protected TypeReference getAnnotationType() {
/*  8434 */     int length = this.identifierLengthStack[(this.identifierLengthPtr--)];
/*  8435 */     if (length == 1) {
/*  8436 */       return new SingleTypeReference(
/*  8437 */         this.identifierStack[this.identifierPtr], 
/*  8438 */         this.identifierPositionStack[(this.identifierPtr--)]);
/*       */     }
/*  8440 */     char[][] tokens = new char[length][];
/*  8441 */     this.identifierPtr -= length;
/*  8442 */     long[] positions = new long[length];
/*  8443 */     System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  8444 */     System.arraycopy(
/*  8445 */       this.identifierPositionStack, 
/*  8446 */       this.identifierPtr + 1, 
/*  8447 */       positions, 
/*  8448 */       0, 
/*  8449 */       length);
/*  8450 */     return new QualifiedTypeReference(tokens, positions);
/*       */   }
/*       */ 
/*       */   public int getFirstToken()
/*       */   {
/*  8480 */     return this.firstToken;
/*       */   }
/*       */ 
/*       */   public int[] getJavaDocPositions()
/*       */   {
/*  8492 */     int javadocCount = 0;
/*  8493 */     int max = this.scanner.commentPtr;
/*  8494 */     for (int i = 0; i <= max; i++)
/*       */     {
/*  8496 */       if ((this.scanner.commentStarts[i] >= 0) && (this.scanner.commentStops[i] > 0)) {
/*  8497 */         javadocCount++;
/*       */       }
/*       */     }
/*  8500 */     if (javadocCount == 0) return null;
/*       */ 
/*  8502 */     int[] positions = new int[2 * javadocCount];
/*  8503 */     int index = 0;
/*  8504 */     for (int i = 0; i <= max; i++)
/*       */     {
/*  8506 */       int commentStart = this.scanner.commentStarts[i];
/*  8507 */       if (commentStart >= 0) {
/*  8508 */         int commentStop = this.scanner.commentStops[i];
/*  8509 */         if (commentStop > 0) {
/*  8510 */           positions[(index++)] = commentStart;
/*  8511 */           positions[(index++)] = (commentStop - 1);
/*       */         }
/*       */       }
/*       */     }
/*  8515 */     return positions;
/*       */   }
/*       */ 
/*       */   public void getMethodBodies(CompilationUnitDeclaration unit)
/*       */   {
/*  8520 */     if (unit == null) return;
/*       */ 
/*  8522 */     if (unit.ignoreMethodBodies) {
/*  8523 */       unit.ignoreFurtherInvestigation = true;
/*  8524 */       return;
/*       */     }
/*       */ 
/*  8528 */     if ((unit.bits & 0x10) != 0) {
/*  8529 */       return;
/*       */     }
/*       */ 
/*  8533 */     int[] oldLineEnds = this.scanner.lineEnds;
/*  8534 */     int oldLinePtr = this.scanner.linePtr;
/*       */ 
/*  8537 */     CompilationResult compilationResult = unit.compilationResult;
/*  8538 */     char[] contents = this.readManager != null ? 
/*  8539 */       this.readManager.getContents(compilationResult.compilationUnit) : 
/*  8540 */       compilationResult.compilationUnit.getContents();
/*  8541 */     this.scanner.setSource(contents, compilationResult);
/*       */ 
/*  8543 */     if ((this.javadocParser != null) && (this.javadocParser.checkDocComment)) {
/*  8544 */       this.javadocParser.scanner.setSource(contents);
/*       */     }
/*  8546 */     if (unit.types != null) {
/*  8547 */       int i = 0; for (int length = unit.types.length; i < length; i++) {
/*  8548 */         unit.types[i].parseMethods(this, unit);
/*       */       }
/*       */     }
/*       */ 
/*  8552 */     unit.bits |= 16;
/*       */ 
/*  8556 */     this.scanner.lineEnds = oldLineEnds;
/*  8557 */     this.scanner.linePtr = oldLinePtr;
/*       */   }
/*       */ 
/*       */   protected char getNextCharacter(char[] comment, int[] index)
/*       */   {
/*       */     int tmp3_2 = 0;
/*       */     int[] tmp3_1 = index;
/*       */     int tmp5_4 = tmp3_1[tmp3_2]; tmp3_1[tmp3_2] = (tmp5_4 + 1); char nextCharacter = comment[tmp5_4];
/*  8561 */     switch (nextCharacter)
/*       */     {
/*       */     case '\\':
/*  8564 */       index[0] += 1;
/*  8565 */       while (comment[index[0]] == 'u') index[0] += 1;
/*       */       int tmp62_61 = 0;
/*       */       int[] tmp62_60 = index;
/*       */       int tmp64_63 = tmp62_60[tmp62_61]; tmp62_60[tmp62_61] = (tmp64_63 + 1);
/*       */       int c1;
/*  8566 */       if (((c1 = ScannerHelper.getNumericValue(comment[tmp64_63])) > 15) || 
/*  8567 */         (c1 < 0))
/*       */         break;
/*       */       int tmp88_87 = 0;
/*       */       int[] tmp88_86 = index;
/*       */       int tmp90_89 = tmp88_86[tmp88_87]; tmp88_86[tmp88_87] = (tmp90_89 + 1);
/*       */       int c2;
/*  8568 */       if (((c2 = ScannerHelper.getNumericValue(comment[tmp90_89])) > 15) || (c2 < 0))
/*       */         break;
/*       */       int tmp114_113 = 0;
/*       */       int[] tmp114_112 = index;
/*       */       int tmp116_115 = tmp114_112[tmp114_113]; tmp114_112[tmp114_113] = (tmp116_115 + 1);
/*       */       int c3;
/*  8569 */       if (((c3 = ScannerHelper.getNumericValue(comment[tmp116_115])) > 15) || (c3 < 0))
/*       */         break;
/*       */       int tmp140_139 = 0;
/*       */       int[] tmp140_138 = index;
/*       */       int tmp142_141 = tmp140_138[tmp140_139]; tmp140_138[tmp140_139] = (tmp142_141 + 1);
/*       */       int c4;
/*  8570 */       if (((c4 = ScannerHelper.getNumericValue(comment[tmp142_141])) > 15) || (c4 < 0)) break;
/*  8571 */       nextCharacter = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
/*       */     }
/*       */ 
/*  8575 */     return nextCharacter;
/*       */   }
/*       */ 
/*       */   protected Expression getTypeReference(Expression exp) {
/*  8579 */     exp.bits &= -8;
/*  8580 */     exp.bits |= 4;
/*  8581 */     return exp;
/*       */   }
/*       */ 
/*       */   protected TypeReference getTypeReference(int dim)
/*       */   {
/*  8588 */     int length = this.identifierLengthStack[(this.identifierLengthPtr--)];
/*       */     TypeReference ref;
/*  8589 */     if (length < 0) {
/*  8590 */       TypeReference ref = TypeReference.baseTypeReference(-length, dim);
/*  8591 */       ref.sourceStart = this.intStack[(this.intPtr--)];
/*  8592 */       if (dim == 0) {
/*  8593 */         ref.sourceEnd = this.intStack[(this.intPtr--)];
/*       */       } else {
/*  8595 */         this.intPtr -= 1;
/*  8596 */         ref.sourceEnd = this.endPosition;
/*       */       }
/*       */     } else {
/*  8599 */       int numberOfIdentifiers = this.genericsIdentifiersLengthStack[(this.genericsIdentifiersLengthPtr--)];
/*       */       TypeReference ref;
/*  8600 */       if ((length != numberOfIdentifiers) || (this.genericsLengthStack[this.genericsLengthPtr] != 0))
/*       */       {
/*  8602 */         ref = getTypeReferenceForGenericType(dim, length, numberOfIdentifiers);
/*  8603 */       } else if (length == 1)
/*       */       {
/*  8605 */         this.genericsLengthPtr -= 1;
/*       */         TypeReference ref;
/*  8606 */         if (dim == 0) {
/*  8607 */           ref = 
/*  8608 */             new SingleTypeReference(
/*  8609 */             this.identifierStack[this.identifierPtr], 
/*  8610 */             this.identifierPositionStack[(this.identifierPtr--)]);
/*       */         } else {
/*  8612 */           TypeReference ref = 
/*  8613 */             new ArrayTypeReference(
/*  8614 */             this.identifierStack[this.identifierPtr], 
/*  8615 */             dim, 
/*  8616 */             this.identifierPositionStack[(this.identifierPtr--)]);
/*  8617 */           ref.sourceEnd = this.endPosition;
/*       */         }
/*       */       } else {
/*  8620 */         this.genericsLengthPtr -= 1;
/*       */ 
/*  8622 */         char[][] tokens = new char[length][];
/*  8623 */         this.identifierPtr -= length;
/*  8624 */         long[] positions = new long[length];
/*  8625 */         System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  8626 */         System.arraycopy(
/*  8627 */           this.identifierPositionStack, 
/*  8628 */           this.identifierPtr + 1, 
/*  8629 */           positions, 
/*  8630 */           0, 
/*  8631 */           length);
/*       */         TypeReference ref;
/*  8632 */         if (dim == 0) {
/*  8633 */           ref = new QualifiedTypeReference(tokens, positions);
/*       */         } else {
/*  8635 */           ref = new ArrayQualifiedTypeReference(tokens, dim, positions);
/*  8636 */           ref.sourceEnd = this.endPosition;
/*       */         }
/*       */       }
/*       */     }
/*  8640 */     return ref;
/*       */   }
/*       */   protected TypeReference getTypeReferenceForGenericType(int dim, int identifierLength, int numberOfIdentifiers) {
/*  8643 */     if ((identifierLength == 1) && (numberOfIdentifiers == 1)) {
/*  8644 */       int currentTypeArgumentsLength = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  8645 */       TypeReference[] typeArguments = new TypeReference[currentTypeArgumentsLength];
/*  8646 */       this.genericsPtr -= currentTypeArgumentsLength;
/*  8647 */       System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments, 0, currentTypeArgumentsLength);
/*  8648 */       ParameterizedSingleTypeReference parameterizedSingleTypeReference = new ParameterizedSingleTypeReference(this.identifierStack[this.identifierPtr], typeArguments, dim, this.identifierPositionStack[(this.identifierPtr--)]);
/*  8649 */       if (dim != 0) {
/*  8650 */         parameterizedSingleTypeReference.sourceEnd = this.endStatementPosition;
/*       */       }
/*  8652 */       return parameterizedSingleTypeReference;
/*       */     }
/*  8654 */     TypeReference[][] typeArguments = new TypeReference[numberOfIdentifiers][];
/*  8655 */     char[][] tokens = new char[numberOfIdentifiers][];
/*  8656 */     long[] positions = new long[numberOfIdentifiers];
/*  8657 */     int index = numberOfIdentifiers;
/*  8658 */     int currentIdentifiersLength = identifierLength;
/*  8659 */     while (index > 0) {
/*  8660 */       int currentTypeArgumentsLength = this.genericsLengthStack[(this.genericsLengthPtr--)];
/*  8661 */       if (currentTypeArgumentsLength != 0) {
/*  8662 */         this.genericsPtr -= currentTypeArgumentsLength;
/*  8663 */         System.arraycopy(this.genericsStack, this.genericsPtr + 1, typeArguments[(index - 1)] =  = new TypeReference[currentTypeArgumentsLength], 0, currentTypeArgumentsLength);
/*       */       }
/*  8665 */       switch (currentIdentifiersLength)
/*       */       {
/*       */       case 1:
/*  8668 */         tokens[(index - 1)] = this.identifierStack[this.identifierPtr];
/*  8669 */         positions[(index - 1)] = this.identifierPositionStack[(this.identifierPtr--)];
/*  8670 */         break;
/*       */       default:
/*  8673 */         this.identifierPtr -= currentIdentifiersLength;
/*  8674 */         System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, index - currentIdentifiersLength, currentIdentifiersLength);
/*  8675 */         System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, index - currentIdentifiersLength, currentIdentifiersLength);
/*       */       }
/*  8677 */       index -= currentIdentifiersLength;
/*  8678 */       if (index > 0) {
/*  8679 */         currentIdentifiersLength = this.identifierLengthStack[(this.identifierLengthPtr--)];
/*       */       }
/*       */     }
/*  8682 */     ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference = new ParameterizedQualifiedTypeReference(tokens, typeArguments, dim, positions);
/*  8683 */     if (dim != 0) {
/*  8684 */       parameterizedQualifiedTypeReference.sourceEnd = this.endStatementPosition;
/*       */     }
/*  8686 */     return parameterizedQualifiedTypeReference;
/*       */   }
/*       */ 
/*       */   protected NameReference getUnspecifiedReference()
/*       */   {
/*       */     int length;
/*       */     NameReference ref;
/*       */     NameReference ref;
/*  8694 */     if ((length = this.identifierLengthStack[(this.identifierLengthPtr--)]) == 1)
/*       */     {
/*  8696 */       ref = 
/*  8697 */         new SingleNameReference(
/*  8698 */         this.identifierStack[this.identifierPtr], 
/*  8699 */         this.identifierPositionStack[(this.identifierPtr--)]);
/*       */     }
/*       */     else
/*       */     {
/*  8703 */       char[][] tokens = new char[length][];
/*  8704 */       this.identifierPtr -= length;
/*  8705 */       System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  8706 */       long[] positions = new long[length];
/*  8707 */       System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
/*  8708 */       ref = 
/*  8709 */         new QualifiedNameReference(tokens, 
/*  8710 */         positions, 
/*  8711 */         (int)(this.identifierPositionStack[(this.identifierPtr + 1)] >> 32), 
/*  8712 */         (int)this.identifierPositionStack[(this.identifierPtr + length)]);
/*       */     }
/*  8714 */     return ref;
/*       */   }
/*       */ 
/*       */   protected NameReference getUnspecifiedReferenceOptimized()
/*       */   {
/*       */     int length;
/*  8726 */     if ((length = this.identifierLengthStack[(this.identifierLengthPtr--)]) == 1)
/*       */     {
/*  8728 */       NameReference ref = 
/*  8729 */         new SingleNameReference(
/*  8730 */         this.identifierStack[this.identifierPtr], 
/*  8731 */         this.identifierPositionStack[(this.identifierPtr--)]);
/*  8732 */       ref.bits &= -8;
/*  8733 */       ref.bits |= 3;
/*  8734 */       return ref;
/*       */     }
/*       */ 
/*  8743 */     char[][] tokens = new char[length][];
/*  8744 */     this.identifierPtr -= length;
/*  8745 */     System.arraycopy(this.identifierStack, this.identifierPtr + 1, tokens, 0, length);
/*  8746 */     long[] positions = new long[length];
/*  8747 */     System.arraycopy(this.identifierPositionStack, this.identifierPtr + 1, positions, 0, length);
/*  8748 */     NameReference ref = new QualifiedNameReference(
/*  8749 */       tokens, 
/*  8750 */       positions, 
/*  8751 */       (int)(this.identifierPositionStack[(this.identifierPtr + 1)] >> 32), 
/*  8752 */       (int)this.identifierPositionStack[(this.identifierPtr + length)]);
/*  8753 */     ref.bits &= -8;
/*  8754 */     ref.bits |= 3;
/*  8755 */     return ref;
/*       */   }
/*       */ 
/*       */   public void goForBlockStatementsopt()
/*       */   {
/*  8760 */     this.firstToken = 67;
/*  8761 */     this.scanner.recordLineSeparator = false;
/*       */   }
/*       */ 
/*       */   public void goForBlockStatementsOrCatchHeader()
/*       */   {
/*  8766 */     this.firstToken = 4;
/*  8767 */     this.scanner.recordLineSeparator = false;
/*       */   }
/*       */ 
/*       */   public void goForClassBodyDeclarations()
/*       */   {
/*  8772 */     this.firstToken = 20;
/*  8773 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForCompilationUnit()
/*       */   {
/*  8778 */     this.firstToken = 8;
/*  8779 */     this.scanner.foundTaskCount = 0;
/*  8780 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForExpression()
/*       */   {
/*  8785 */     this.firstToken = 5;
/*  8786 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForFieldDeclaration()
/*       */   {
/*  8791 */     this.firstToken = 24;
/*  8792 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForGenericMethodDeclaration()
/*       */   {
/*  8797 */     this.firstToken = 6;
/*  8798 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForHeaders() {
/*  8802 */     RecoveredType currentType = currentRecoveryType();
/*  8803 */     if ((currentType != null) && (currentType.insideEnumConstantPart))
/*  8804 */       this.firstToken = 66;
/*       */     else {
/*  8806 */       this.firstToken = 11;
/*       */     }
/*  8808 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForImportDeclaration()
/*       */   {
/*  8813 */     this.firstToken = 25;
/*  8814 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForInitializer()
/*       */   {
/*  8819 */     this.firstToken = 10;
/*  8820 */     this.scanner.recordLineSeparator = false;
/*       */   }
/*       */ 
/*       */   public void goForMemberValue()
/*       */   {
/*  8825 */     this.firstToken = 25;
/*  8826 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForMethodBody()
/*       */   {
/*  8831 */     this.firstToken = 9;
/*  8832 */     this.scanner.recordLineSeparator = false;
/*       */   }
/*       */ 
/*       */   public void goForPackageDeclaration()
/*       */   {
/*  8837 */     this.firstToken = 23;
/*  8838 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public void goForTypeDeclaration()
/*       */   {
/*  8843 */     this.firstToken = 1;
/*  8844 */     this.scanner.recordLineSeparator = true;
/*       */   }
/*       */ 
/*       */   public boolean hasLeadingTagComment(char[] commentPrefixTag, int rangeEnd)
/*       */   {
/*  8852 */     int iComment = this.scanner.commentPtr;
/*  8853 */     if (iComment < 0) return false;
/*  8854 */     int iStatement = this.astLengthPtr;
/*  8855 */     if ((iStatement < 0) || (this.astLengthStack[iStatement] <= 1)) return false;
/*       */ 
/*  8857 */     ASTNode lastNode = this.astStack[this.astPtr];
/*  8858 */     int rangeStart = lastNode.sourceEnd;
/*  8859 */     for (; iComment >= 0; iComment--) {
/*  8860 */       int commentStart = this.scanner.commentStarts[iComment];
/*  8861 */       if (commentStart < 0) commentStart = -commentStart;
/*       */ 
/*  8863 */       if (commentStart < rangeStart) return false;
/*       */ 
/*  8865 */       if (commentStart > rangeEnd)
/*       */         continue;
/*  8867 */       char[] source = this.scanner.source;
/*  8868 */       int charPos = commentStart + 2;
/*       */ 
/*  8870 */       for (; charPos < rangeEnd; charPos++) {
/*  8871 */         char c = source[charPos];
/*  8872 */         if ((c >= '') || ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x100) == 0)) {
/*       */           break;
/*       */         }
/*       */       }
/*  8876 */       int iTag = 0; for (int length = commentPrefixTag.length; iTag < length; charPos++) {
/*  8877 */         if (charPos >= rangeEnd) return false;
/*  8878 */         if (source[charPos] != commentPrefixTag[iTag]) return false;
/*  8876 */         iTag++;
/*       */       }
/*       */ 
/*  8880 */       return true;
/*       */     }
/*  8882 */     return false;
/*       */   }
/*       */ 
/*       */   protected void ignoreExpressionAssignment()
/*       */   {
/*  8887 */     this.intPtr -= 1;
/*  8888 */     ArrayInitializer arrayInitializer = (ArrayInitializer)this.expressionStack[(this.expressionPtr--)];
/*  8889 */     this.expressionLengthPtr -= 1;
/*       */ 
/*  8891 */     if (!this.statementRecoveryActivated) problemReporter().arrayConstantsOnlyInArrayInitializers(arrayInitializer.sourceStart, arrayInitializer.sourceEnd); 
/*       */   }
/*       */ 
/*       */   public void initialize() {
/*  8894 */     initialize(false);
/*       */   }
/*       */ 
/*       */   public void initialize(boolean initializeNLS)
/*       */   {
/*  8899 */     this.astPtr = -1;
/*  8900 */     this.astLengthPtr = -1;
/*  8901 */     this.expressionPtr = -1;
/*  8902 */     this.expressionLengthPtr = -1;
/*  8903 */     this.identifierPtr = -1;
/*  8904 */     this.identifierLengthPtr = -1;
/*  8905 */     this.intPtr = -1;
/*       */     int tmp41_40 = 0; this.nestedType = tmp41_40; this.nestedMethod[tmp41_40] = 0;
/*  8907 */     this.variablesCounter[this.nestedType] = 0;
/*  8908 */     this.dimensions = 0;
/*  8909 */     this.realBlockPtr = -1;
/*  8910 */     this.compilationUnit = null;
/*  8911 */     this.referenceContext = null;
/*  8912 */     this.endStatementPosition = 0;
/*       */ 
/*  8917 */     int astLength = this.astStack.length;
/*  8918 */     if (this.noAstNodes.length < astLength) {
/*  8919 */       this.noAstNodes = new ASTNode[astLength];
/*       */     }
/*       */ 
/*  8923 */     System.arraycopy(this.noAstNodes, 0, this.astStack, 0, astLength);
/*       */ 
/*  8925 */     int expressionLength = this.expressionStack.length;
/*  8926 */     if (this.noExpressions.length < expressionLength) {
/*  8927 */       this.noExpressions = new Expression[expressionLength];
/*       */     }
/*       */ 
/*  8930 */     System.arraycopy(this.noExpressions, 0, this.expressionStack, 0, expressionLength);
/*       */ 
/*  8933 */     this.scanner.commentPtr = -1;
/*  8934 */     this.scanner.foundTaskCount = 0;
/*  8935 */     this.scanner.eofPosition = 2147483647;
/*  8936 */     this.recordStringLiterals = true;
/*  8937 */     boolean checkNLS = this.options.getSeverity(256) != -1;
/*  8938 */     this.checkExternalizeStrings = checkNLS;
/*  8939 */     this.scanner.checkNonExternalizedStringLiterals = ((initializeNLS) && (checkNLS));
/*  8940 */     this.scanner.lastPosition = -1;
/*       */ 
/*  8942 */     resetModifiers();
/*       */ 
/*  8945 */     this.lastCheckPoint = -1;
/*  8946 */     this.currentElement = null;
/*  8947 */     this.restartRecovery = false;
/*  8948 */     this.hasReportedError = false;
/*  8949 */     this.recoveredStaticInitializerStart = 0;
/*  8950 */     this.lastIgnoredToken = -1;
/*  8951 */     this.lastErrorEndPosition = -1;
/*  8952 */     this.lastErrorEndPositionBeforeRecovery = -1;
/*  8953 */     this.lastJavadocEnd = -1;
/*  8954 */     this.listLength = 0;
/*  8955 */     this.listTypeParameterLength = 0;
/*  8956 */     this.lastPosistion = -1;
/*       */ 
/*  8958 */     this.rBraceStart = 0;
/*  8959 */     this.rBraceEnd = 0;
/*  8960 */     this.rBraceSuccessorStart = 0;
/*       */ 
/*  8962 */     this.genericsIdentifiersLengthPtr = -1;
/*  8963 */     this.genericsLengthPtr = -1;
/*  8964 */     this.genericsPtr = -1;
/*       */   }
/*       */   public void initializeScanner() {
/*  8967 */     this.scanner = 
/*  8975 */       new Scanner(false, 
/*  8969 */       false, 
/*  8970 */       false, 
/*  8971 */       this.options.sourceLevel, 
/*  8972 */       this.options.complianceLevel, 
/*  8973 */       this.options.taskTags, 
/*  8974 */       this.options.taskPriorites, 
/*  8975 */       this.options.isTaskCaseSensitive);
/*       */   }
/*       */ 
/*       */   public void jumpOverMethodBody()
/*       */   {
/*  8984 */     if ((this.diet) && (this.dietInt == 0))
/*  8985 */       this.scanner.diet = true; 
/*       */   }
/*       */ 
/*       */   private void jumpOverType() {
/*  8988 */     if ((this.recoveredTypes != null) && (this.nextTypeStart > -1) && (this.nextTypeStart < this.scanner.currentPosition))
/*       */     {
/*  8994 */       TypeDeclaration typeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
/*  8995 */       boolean isAnonymous = typeDeclaration.allocation != null;
/*       */ 
/*  8997 */       this.scanner.startPosition = (typeDeclaration.declarationSourceEnd + 1);
/*  8998 */       this.scanner.currentPosition = (typeDeclaration.declarationSourceEnd + 1);
/*  8999 */       this.scanner.diet = false;
/*       */ 
/*  9001 */       if (!isAnonymous)
/*  9002 */         ((RecoveryScanner)this.scanner).setPendingTokens(new int[] { 27, 75 });
/*       */       else {
/*  9004 */         ((RecoveryScanner)this.scanner).setPendingTokens(new int[] { 26, 71, 26 });
/*       */       }
/*       */ 
/*  9007 */       this.pendingRecoveredType = typeDeclaration;
/*       */       try
/*       */       {
/*  9010 */         this.currentToken = this.scanner.getNextToken();
/*       */       }
/*       */       catch (InvalidInputException localInvalidInputException)
/*       */       {
/*       */       }
/*  9015 */       if (++this.recoveredTypePtr < this.recoveredTypes.length) {
/*  9016 */         TypeDeclaration nextTypeDeclaration = this.recoveredTypes[this.recoveredTypePtr];
/*  9017 */         this.nextTypeStart = 
/*  9018 */           (nextTypeDeclaration.allocation == null ? 
/*  9019 */           nextTypeDeclaration.declarationSourceStart : 
/*  9020 */           nextTypeDeclaration.allocation.sourceStart);
/*       */       } else {
/*  9022 */         this.nextTypeStart = 2147483647;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   protected void markEnclosingMemberWithLocalType() {
/*  9027 */     if (this.currentElement != null) return;
/*  9028 */     for (int i = this.astPtr; i >= 0; i--) {
/*  9029 */       ASTNode node = this.astStack[i];
/*  9030 */       if ((!(node instanceof AbstractMethodDeclaration)) && 
/*  9031 */         (!(node instanceof FieldDeclaration))) {
/*  9032 */         if (!(node instanceof TypeDeclaration))
/*       */           continue;
/*  9034 */         if (((TypeDeclaration)node).declarationSourceEnd != 0) continue; 
/*       */       } else {
/*  9035 */         node.bits |= 2;
/*  9036 */         return;
/*       */       }
/*       */     }
/*       */ 
/*  9040 */     if (((this.referenceContext instanceof AbstractMethodDeclaration)) || 
/*  9041 */       ((this.referenceContext instanceof TypeDeclaration)))
/*  9042 */       ((ASTNode)this.referenceContext).bits |= 2;
/*       */   }
/*       */ 
/*       */   protected boolean moveRecoveryCheckpoint()
/*       */   {
/*  9054 */     int pos = this.lastCheckPoint;
/*       */ 
/*  9056 */     this.scanner.startPosition = pos;
/*  9057 */     this.scanner.currentPosition = pos;
/*  9058 */     this.scanner.diet = false;
/*       */ 
/*  9061 */     if (this.restartRecovery) {
/*  9062 */       this.lastIgnoredToken = -1;
/*  9063 */       this.scanner.insideRecovery = true;
/*  9064 */       return true;
/*       */     }
/*       */ 
/*  9068 */     this.lastIgnoredToken = this.nextIgnoredToken;
/*  9069 */     this.nextIgnoredToken = -1;
/*       */     do
/*       */       try {
/*  9072 */         this.nextIgnoredToken = this.scanner.getNextToken();
/*  9073 */         if (this.scanner.currentPosition == this.scanner.startPosition) {
/*  9074 */           this.scanner.currentPosition += 1;
/*  9075 */           this.nextIgnoredToken = -1;
/*       */         }
/*       */       }
/*       */       catch (InvalidInputException localInvalidInputException) {
/*  9079 */         pos = this.scanner.currentPosition;
/*       */       }
/*  9081 */     while (this.nextIgnoredToken < 0);
/*       */ 
/*  9083 */     if ((this.nextIgnoredToken == 68) && 
/*  9084 */       (this.currentToken == 68)) {
/*  9085 */       return false;
/*       */     }
/*       */ 
/*  9088 */     this.lastCheckPoint = this.scanner.currentPosition;
/*       */ 
/*  9091 */     this.scanner.startPosition = pos;
/*  9092 */     this.scanner.currentPosition = pos;
/*  9093 */     this.scanner.commentPtr = -1;
/*  9094 */     this.scanner.foundTaskCount = 0;
/*  9095 */     return true;
/*       */   }
/*       */ 
/*       */   protected MessageSend newMessageSend()
/*       */   {
/*  9168 */     MessageSend m = new MessageSend();
/*       */     int length;
/*  9170 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  9171 */       this.expressionPtr -= length;
/*  9172 */       System.arraycopy(
/*  9173 */         this.expressionStack, 
/*  9174 */         this.expressionPtr + 1, 
/*  9175 */         m.arguments = new Expression[length], 
/*  9176 */         0, 
/*  9177 */         length);
/*       */     }
/*  9179 */     return m;
/*       */   }
/*       */   protected MessageSend newMessageSendWithTypeArguments() {
/*  9182 */     MessageSend m = new MessageSend();
/*       */     int length;
/*  9184 */     if ((length = this.expressionLengthStack[(this.expressionLengthPtr--)]) != 0) {
/*  9185 */       this.expressionPtr -= length;
/*  9186 */       System.arraycopy(
/*  9187 */         this.expressionStack, 
/*  9188 */         this.expressionPtr + 1, 
/*  9189 */         m.arguments = new Expression[length], 
/*  9190 */         0, 
/*  9191 */         length);
/*       */     }
/*  9193 */     return m;
/*       */   }
/*       */ 
/*       */   protected void optimizedConcatNodeLists()
/*       */   {
/*  9213 */     this.astLengthStack[(--this.astLengthPtr)] += 1;
/*       */   }
/*       */ 
/*       */   protected void parse()
/*       */   {
/*  9228 */     boolean isDietParse = this.diet;
/*  9229 */     int oldFirstToken = getFirstToken();
/*  9230 */     this.hasError = false;
/*       */ 
/*  9232 */     this.hasReportedError = false;
/*  9233 */     int act = 942;
/*  9234 */     this.stateStackTop = -1;
/*  9235 */     this.currentToken = getFirstToken();
/*       */     while (true) {
/*  9237 */       int stackLength = this.stack.length;
/*  9238 */       if (++this.stateStackTop >= stackLength) {
/*  9239 */         System.arraycopy(
/*  9240 */           this.stack, 0, 
/*  9241 */           this.stack = new int[stackLength + 255], 0, 
/*  9242 */           stackLength);
/*       */       }
/*  9244 */       this.stack[this.stateStackTop] = act;
/*       */ 
/*  9246 */       act = tAction(act, this.currentToken);
/*  9247 */       if ((act == 12741) || (this.restartRecovery))
/*       */       {
/*  9257 */         int errorPos = this.scanner.currentPosition - 1;
/*  9258 */         if (!this.hasReportedError) {
/*  9259 */           this.hasError = true;
/*       */         }
/*  9261 */         int previousToken = this.currentToken;
/*  9262 */         if (resumeOnSyntaxError()) {
/*  9263 */           if ((act == 12741) && (previousToken != 0)) this.lastErrorEndPosition = errorPos;
/*  9264 */           act = 942;
/*  9265 */           this.stateStackTop = -1;
/*  9266 */           this.currentToken = getFirstToken();
/*  9267 */           continue;
/*       */         }
/*  9269 */         act = 12741;
/*  9270 */         break;
/*       */       }
/*  9272 */       if (act <= 703) {
/*  9273 */         this.stateStackTop -= 1;
/*       */       }
/*  9279 */       else if (act > 12741) {
/*  9280 */         consumeToken(this.currentToken);
/*  9281 */         if (this.currentElement != null) {
/*  9282 */           boolean oldValue = this.recordStringLiterals;
/*  9283 */           this.recordStringLiterals = false;
/*  9284 */           recoveryTokenCheck();
/*  9285 */           this.recordStringLiterals = oldValue;
/*       */         }
/*       */         try {
/*  9288 */           this.currentToken = this.scanner.getNextToken();
/*       */         } catch (InvalidInputException e) {
/*  9290 */           if (!this.hasReportedError) {
/*  9291 */             problemReporter().scannerError(this, e.getMessage());
/*  9292 */             this.hasReportedError = true;
/*       */           }
/*  9294 */           this.lastCheckPoint = this.scanner.currentPosition;
/*  9295 */           this.currentToken = 0;
/*  9296 */           this.restartRecovery = true;
/*       */         }
/*  9298 */         if (this.statementRecoveryActivated) {
/*  9299 */           jumpOverType();
/*       */         }
/*  9301 */         act -= 12741;
/*       */       }
/*       */       else
/*       */       {
/*  9308 */         if (act >= 12740) break;
/*  9309 */         consumeToken(this.currentToken);
/*  9310 */         if (this.currentElement != null) {
/*  9311 */           boolean oldValue = this.recordStringLiterals;
/*  9312 */           this.recordStringLiterals = false;
/*  9313 */           recoveryTokenCheck();
/*  9314 */           this.recordStringLiterals = oldValue;
/*       */         }
/*       */         try {
/*  9317 */           this.currentToken = this.scanner.getNextToken();
/*       */         } catch (InvalidInputException e) {
/*  9319 */           if (!this.hasReportedError) {
/*  9320 */             problemReporter().scannerError(this, e.getMessage());
/*  9321 */             this.hasReportedError = true;
/*       */           }
/*  9323 */           this.lastCheckPoint = this.scanner.currentPosition;
/*  9324 */           this.currentToken = 0;
/*  9325 */           this.restartRecovery = true;
/*       */         }
/*  9327 */         if (this.statementRecoveryActivated) {
/*  9328 */           jumpOverType();
/*       */ 
/*  9333 */           continue;
/*       */         }
/*       */ 
/*       */       }
/*       */ 
/*       */       do
/*       */       {
/*  9345 */         consumeRule(act);
/*  9346 */         this.stateStackTop -= rhs[act] - 1;
/*  9347 */         act = ntAction(this.stack[this.stateStackTop], lhs[act]);
/*       */       }
/*       */ 
/*  9355 */       while (act <= 703);
/*       */     }
/*       */ 
/*  9366 */     endParse(act);
/*       */ 
/*  9368 */     NLSTag[] tags = this.scanner.getNLSTags();
/*  9369 */     if (tags != null) {
/*  9370 */       this.compilationUnit.nlsTags = tags;
/*       */     }
/*       */ 
/*  9373 */     this.scanner.checkNonExternalizedStringLiterals = false;
/*  9374 */     if ((this.reportSyntaxErrorIsRequired) && (this.hasError) && (!this.statementRecoveryActivated))
/*  9375 */       if (!this.options.performStatementsRecovery) {
/*  9376 */         reportSyntaxErrors(isDietParse, oldFirstToken);
/*       */       } else {
/*  9378 */         RecoveryScannerData data = this.referenceContext.compilationResult().recoveryScannerData;
/*       */ 
/*  9380 */         if (this.recoveryScanner == null)
/*  9381 */           this.recoveryScanner = new RecoveryScanner(this.scanner, data);
/*       */         else {
/*  9383 */           this.recoveryScanner.setData(data);
/*       */         }
/*       */ 
/*  9386 */         this.recoveryScanner.setSource(this.scanner.source);
/*  9387 */         this.recoveryScanner.lineEnds = this.scanner.lineEnds;
/*  9388 */         this.recoveryScanner.linePtr = this.scanner.linePtr;
/*       */ 
/*  9390 */         reportSyntaxErrors(isDietParse, oldFirstToken);
/*       */ 
/*  9392 */         if (data == null) {
/*  9393 */           this.referenceContext.compilationResult().recoveryScannerData = 
/*  9394 */             this.recoveryScanner.getData();
/*       */         }
/*       */ 
/*  9397 */         if ((this.methodRecoveryActivated) && (this.options.performStatementsRecovery)) {
/*  9398 */           this.methodRecoveryActivated = false;
/*  9399 */           recoverStatements();
/*  9400 */           this.methodRecoveryActivated = true;
/*       */ 
/*  9402 */           this.lastAct = 12741;
/*       */         }
/*       */       }
/*       */   }
/*       */ 
/*       */   public void parse(ConstructorDeclaration cd, CompilationUnitDeclaration unit, boolean recordLineSeparator)
/*       */   {
/*  9415 */     boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
/*  9416 */     if (this.options.performMethodsFullRecovery) {
/*  9417 */       this.methodRecoveryActivated = true;
/*       */     }
/*       */ 
/*  9420 */     initialize();
/*  9421 */     goForBlockStatementsopt();
/*  9422 */     if (recordLineSeparator) {
/*  9423 */       this.scanner.recordLineSeparator = true;
/*       */     }
/*  9425 */     this.nestedMethod[this.nestedType] += 1;
/*  9426 */     pushOnRealBlockStack(0);
/*       */ 
/*  9428 */     this.referenceContext = cd;
/*  9429 */     this.compilationUnit = unit;
/*       */ 
/*  9431 */     this.scanner.resetTo(cd.bodyStart, cd.bodyEnd);
/*       */     try {
/*  9433 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9435 */       this.lastAct = 12741;
/*       */     } finally {
/*  9437 */       this.nestedMethod[this.nestedType] -= 1;
/*  9438 */       if (this.options.performStatementsRecovery) {
/*  9439 */         this.methodRecoveryActivated = oldMethodRecoveryActivated;
/*       */       }
/*       */     }
/*       */ 
/*  9443 */     checkNonNLSAfterBodyEnd(cd.declarationSourceEnd);
/*       */ 
/*  9445 */     if (this.lastAct == 12741) {
/*  9446 */       cd.bits |= 524288;
/*  9447 */       initialize();
/*  9448 */       return;
/*       */     }
/*       */ 
/*  9452 */     cd.explicitDeclarations = this.realBlockStack[(this.realBlockPtr--)];
/*       */     int length;
/*  9454 */     if ((this.astLengthPtr > -1) && ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)) {
/*  9455 */       this.astPtr -= length;
/*  9456 */       if (!this.options.ignoreMethodBodies)
/*  9457 */         if ((this.astStack[(this.astPtr + 1)] instanceof ExplicitConstructorCall))
/*       */         {
/*  9460 */           System.arraycopy(
/*  9461 */             this.astStack, 
/*  9462 */             this.astPtr + 2, 
/*  9463 */             cd.statements = new Statement[length - 1], 
/*  9464 */             0, 
/*  9465 */             length - 1);
/*  9466 */           cd.constructorCall = ((ExplicitConstructorCall)this.astStack[(this.astPtr + 1)]);
/*       */         } else {
/*  9468 */           System.arraycopy(
/*  9469 */             this.astStack, 
/*  9470 */             this.astPtr + 1, 
/*  9471 */             cd.statements = new Statement[length], 
/*  9472 */             0, 
/*  9473 */             length);
/*  9474 */           cd.constructorCall = SuperReference.implicitSuperConstructorCall();
/*       */         }
/*       */     }
/*       */     else {
/*  9478 */       if (!this.options.ignoreMethodBodies) {
/*  9479 */         cd.constructorCall = SuperReference.implicitSuperConstructorCall();
/*       */       }
/*  9481 */       if (!containsComment(cd.bodyStart, cd.bodyEnd)) {
/*  9482 */         cd.bits |= 8;
/*       */       }
/*       */     }
/*       */ 
/*  9486 */     ExplicitConstructorCall explicitConstructorCall = cd.constructorCall;
/*  9487 */     if ((explicitConstructorCall != null) && (explicitConstructorCall.sourceEnd == 0)) {
/*  9488 */       explicitConstructorCall.sourceEnd = cd.sourceEnd;
/*  9489 */       explicitConstructorCall.sourceStart = cd.sourceStart;
/*       */     }
/*       */   }
/*       */ 
/*       */   public void parse(FieldDeclaration field, TypeDeclaration type, CompilationUnitDeclaration unit, char[] initializationSource)
/*       */   {
/*  9503 */     initialize();
/*  9504 */     goForExpression();
/*  9505 */     this.nestedMethod[this.nestedType] += 1;
/*       */ 
/*  9507 */     this.referenceContext = type;
/*  9508 */     this.compilationUnit = unit;
/*       */ 
/*  9510 */     this.scanner.setSource(initializationSource);
/*  9511 */     this.scanner.resetTo(0, initializationSource.length - 1);
/*       */     try {
/*  9513 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9515 */       this.lastAct = 12741;
/*       */     } finally {
/*  9517 */       this.nestedMethod[this.nestedType] -= 1;
/*       */     }
/*       */ 
/*  9520 */     if (this.lastAct == 12741) {
/*  9521 */       field.bits |= 524288;
/*  9522 */       return;
/*       */     }
/*       */ 
/*  9525 */     field.initialization = this.expressionStack[this.expressionPtr];
/*       */ 
/*  9528 */     if ((type.bits & 0x2) != 0)
/*  9529 */       field.bits |= 2;
/*       */   }
/*       */ 
/*       */   public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult)
/*       */   {
/*  9539 */     return parse(sourceUnit, compilationResult, -1, -1);
/*       */   }
/*       */ 
/*       */   public CompilationUnitDeclaration parse(ICompilationUnit sourceUnit, CompilationResult compilationResult, int start, int end)
/*       */   {
/*       */     CompilationUnitDeclaration unit;
/*       */     try {
/*  9553 */       initialize(true);
/*  9554 */       goForCompilationUnit();
/*       */ 
/*  9557 */       this.referenceContext = 
/*  9558 */         (this.compilationUnit = 
/*  9559 */         new CompilationUnitDeclaration(
/*  9560 */         this.problemReporter, 
/*  9561 */         compilationResult, 
/*  9562 */         0));
/*       */       char[] contents;
/*       */       try {
/*  9567 */         contents = this.readManager != null ? this.readManager.getContents(sourceUnit) : sourceUnit.getContents();
/*       */       }
/*       */       catch (AbortCompilationUnit abortException)
/*       */       {
/*       */         char[] contents;
/*  9569 */         problemReporter().cannotReadSource(this.compilationUnit, abortException, this.options.verbose);
/*  9570 */         contents = CharOperation.NO_CHAR;
/*       */       }
/*  9572 */       this.scanner.setSource(contents);
/*  9573 */       this.compilationUnit.sourceEnd = (this.scanner.source.length - 1);
/*  9574 */       if (end != -1) this.scanner.resetTo(start, end);
/*  9575 */       if ((this.javadocParser != null) && (this.javadocParser.checkDocComment)) {
/*  9576 */         this.javadocParser.scanner.setSource(contents);
/*  9577 */         if (end != -1) {
/*  9578 */           this.javadocParser.scanner.resetTo(start, end);
/*       */         }
/*       */       }
/*       */ 
/*  9582 */       parse();
/*       */     } finally {
/*  9584 */       CompilationUnitDeclaration unit = this.compilationUnit;
/*  9585 */       this.compilationUnit = null;
/*       */ 
/*  9587 */       if (!this.diet) unit.bits |= 16;
/*       */     }
/*  9589 */     return unit;
/*       */   }
/*       */ 
/*       */   public void parse(Initializer initializer, TypeDeclaration type, CompilationUnitDeclaration unit)
/*       */   {
/*  9602 */     boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
/*  9603 */     if (this.options.performMethodsFullRecovery) {
/*  9604 */       this.methodRecoveryActivated = true;
/*       */     }
/*       */ 
/*  9607 */     initialize();
/*  9608 */     goForBlockStatementsopt();
/*  9609 */     this.nestedMethod[this.nestedType] += 1;
/*  9610 */     pushOnRealBlockStack(0);
/*       */ 
/*  9612 */     this.referenceContext = type;
/*  9613 */     this.compilationUnit = unit;
/*       */ 
/*  9615 */     this.scanner.resetTo(initializer.bodyStart, initializer.bodyEnd);
/*       */     try {
/*  9617 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9619 */       this.lastAct = 12741;
/*       */     } finally {
/*  9621 */       this.nestedMethod[this.nestedType] -= 1;
/*  9622 */       if (this.options.performStatementsRecovery) {
/*  9623 */         this.methodRecoveryActivated = oldMethodRecoveryActivated;
/*       */       }
/*       */     }
/*       */ 
/*  9627 */     checkNonNLSAfterBodyEnd(initializer.declarationSourceEnd);
/*       */ 
/*  9629 */     if (this.lastAct == 12741) {
/*  9630 */       initializer.bits |= 524288;
/*  9631 */       return;
/*       */     }
/*       */ 
/*  9635 */     initializer.block.explicitDeclarations = this.realBlockStack[(this.realBlockPtr--)];
/*       */     int length;
/*  9637 */     if ((this.astLengthPtr > -1) && ((length = this.astLengthStack[(this.astLengthPtr--)]) > 0)) {
/*  9638 */       System.arraycopy(this.astStack, this.astPtr -= length + 1, initializer.block.statements = new Statement[length], 0, length);
/*       */     }
/*  9641 */     else if (!containsComment(initializer.block.sourceStart, initializer.block.sourceEnd)) {
/*  9642 */       initializer.block.bits |= 8;
/*       */     }
/*       */ 
/*  9647 */     if ((type.bits & 0x2) != 0)
/*  9648 */       initializer.bits |= 2;
/*       */   }
/*       */ 
/*       */   public void parse(MethodDeclaration md, CompilationUnitDeclaration unit)
/*       */   {
/*  9658 */     if (md.isAbstract())
/*  9659 */       return;
/*  9660 */     if (md.isNative())
/*  9661 */       return;
/*  9662 */     if ((md.modifiers & 0x1000000) != 0) {
/*  9663 */       return;
/*       */     }
/*  9665 */     boolean oldMethodRecoveryActivated = this.methodRecoveryActivated;
/*  9666 */     if (this.options.performMethodsFullRecovery) {
/*  9667 */       this.methodRecoveryActivated = true;
/*  9668 */       this.rParenPos = md.sourceEnd;
/*       */     }
/*  9670 */     initialize();
/*  9671 */     goForBlockStatementsopt();
/*  9672 */     this.nestedMethod[this.nestedType] += 1;
/*  9673 */     pushOnRealBlockStack(0);
/*       */ 
/*  9675 */     this.referenceContext = md;
/*  9676 */     this.compilationUnit = unit;
/*       */ 
/*  9678 */     this.scanner.resetTo(md.bodyStart, md.bodyEnd);
/*       */     try
/*       */     {
/*  9681 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9683 */       this.lastAct = 12741;
/*       */     } finally {
/*  9685 */       this.nestedMethod[this.nestedType] -= 1;
/*  9686 */       if (this.options.performStatementsRecovery) {
/*  9687 */         this.methodRecoveryActivated = oldMethodRecoveryActivated;
/*       */       }
/*       */     }
/*       */ 
/*  9691 */     checkNonNLSAfterBodyEnd(md.declarationSourceEnd);
/*       */ 
/*  9693 */     if (this.lastAct == 12741) {
/*  9694 */       md.bits |= 524288;
/*  9695 */       return;
/*       */     }
/*       */ 
/*  9699 */     md.explicitDeclarations = this.realBlockStack[(this.realBlockPtr--)];
/*       */     int length;
/*  9701 */     if ((this.astLengthPtr > -1) && ((length = this.astLengthStack[(this.astLengthPtr--)]) != 0)) {
/*  9702 */       if (this.options.ignoreMethodBodies)
/*       */       {
/*  9704 */         this.astPtr -= length;
/*       */       }
/*  9706 */       else System.arraycopy(
/*  9707 */           this.astStack, 
/*  9708 */           this.astPtr -= length + 1, 
/*  9709 */           md.statements = new Statement[length], 
/*  9710 */           0, 
/*  9711 */           length);
/*       */ 
/*       */     }
/*  9714 */     else if (!containsComment(md.bodyStart, md.bodyEnd))
/*  9715 */       md.bits |= 8;
/*       */   }
/*       */ 
/*       */   public ASTNode[] parseClassBodyDeclarations(char[] source, int offset, int length, CompilationUnitDeclaration unit)
/*       */   {
/*  9720 */     boolean oldDiet = this.diet;
/*       */ 
/*  9722 */     initialize();
/*  9723 */     goForClassBodyDeclarations();
/*       */ 
/*  9725 */     this.scanner.setSource(source);
/*  9726 */     this.scanner.resetTo(offset, offset + length - 1);
/*  9727 */     if ((this.javadocParser != null) && (this.javadocParser.checkDocComment)) {
/*  9728 */       this.javadocParser.scanner.setSource(source);
/*  9729 */       this.javadocParser.scanner.resetTo(offset, offset + length - 1);
/*       */     }
/*       */ 
/*  9733 */     this.nestedType = 1;
/*       */ 
/*  9736 */     TypeDeclaration referenceContextTypeDeclaration = new TypeDeclaration(unit.compilationResult);
/*  9737 */     referenceContextTypeDeclaration.name = Util.EMPTY_STRING.toCharArray();
/*  9738 */     referenceContextTypeDeclaration.fields = new FieldDeclaration[0];
/*  9739 */     this.compilationUnit = unit;
/*  9740 */     unit.types = new TypeDeclaration[1];
/*  9741 */     unit.types[0] = referenceContextTypeDeclaration;
/*  9742 */     this.referenceContext = unit;
/*       */     try
/*       */     {
/*  9746 */       this.diet = true;
/*  9747 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9749 */       this.lastAct = 12741;
/*       */     } finally {
/*  9751 */       this.diet = oldDiet;
/*       */     }
/*       */ 
/*  9754 */     ASTNode[] result = (ASTNode[])null;
/*  9755 */     if (this.lastAct == 12741) {
/*  9756 */       if ((!this.options.performMethodsFullRecovery) && (!this.options.performStatementsRecovery)) {
/*  9757 */         return null;
/*       */       }
/*       */ 
/*  9760 */       List bodyDeclarations = new ArrayList();
/*  9761 */       ASTVisitor visitor = new ASTVisitor(bodyDeclarations) { private final List val$bodyDeclarations;
/*       */ 
/*  9763 */         public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) { if (!methodDeclaration.isDefaultConstructor()) {
/*  9764 */             this.val$bodyDeclarations.add(methodDeclaration);
/*       */           }
/*  9766 */           return false; }
/*       */ 
/*       */         public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
/*  9769 */           this.val$bodyDeclarations.add(fieldDeclaration);
/*  9770 */           return false;
/*       */         }
/*       */         public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
/*  9773 */           this.val$bodyDeclarations.add(memberTypeDeclaration);
/*  9774 */           return false;
/*       */         }
/*       */       };
/*  9777 */       unit.ignoreFurtherInvestigation = false;
/*  9778 */       unit.traverse(visitor, unit.scope);
/*  9779 */       unit.ignoreFurtherInvestigation = true;
/*  9780 */       result = (ASTNode[])bodyDeclarations.toArray(new ASTNode[bodyDeclarations.size()]);
/*       */     }
/*       */     else
/*       */     {
/*       */       int astLength;
/*  9783 */       if ((this.astLengthPtr > -1) && ((astLength = this.astLengthStack[(this.astLengthPtr--)]) != 0)) {
/*  9784 */         result = new ASTNode[astLength];
/*  9785 */         this.astPtr -= astLength;
/*  9786 */         System.arraycopy(this.astStack, this.astPtr + 1, result, 0, astLength);
/*       */       }
/*       */       else {
/*  9789 */         result = new ASTNode[0];
/*       */       }
/*       */     }
/*  9792 */     boolean containsInitializers = false;
/*  9793 */     TypeDeclaration typeDeclaration = null;
/*  9794 */     int i = 0; for (int max = result.length; i < max; i++)
/*       */     {
/*  9796 */       ASTNode node = result[i];
/*  9797 */       if ((node instanceof TypeDeclaration)) {
/*  9798 */         ((TypeDeclaration)node).parseMethods(this, unit);
/*  9799 */       } else if ((node instanceof AbstractMethodDeclaration)) {
/*  9800 */         ((AbstractMethodDeclaration)node).parseStatements(this, unit);
/*  9801 */       } else if ((node instanceof FieldDeclaration)) {
/*  9802 */         FieldDeclaration fieldDeclaration = (FieldDeclaration)node;
/*  9803 */         switch (fieldDeclaration.getKind()) {
/*       */         case 2:
/*  9805 */           containsInitializers = true;
/*  9806 */           if (typeDeclaration == null) {
/*  9807 */             typeDeclaration = referenceContextTypeDeclaration;
/*       */           }
/*  9809 */           if (typeDeclaration.fields == null) {
/*  9810 */             typeDeclaration.fields = new FieldDeclaration[1];
/*  9811 */             typeDeclaration.fields[0] = fieldDeclaration;
/*       */           } else {
/*  9813 */             int length2 = typeDeclaration.fields.length;
/*  9814 */             FieldDeclaration[] temp = new FieldDeclaration[length2 + 1];
/*  9815 */             System.arraycopy(typeDeclaration.fields, 0, temp, 0, length2);
/*  9816 */             temp[length2] = fieldDeclaration;
/*  9817 */             typeDeclaration.fields = temp;
/*       */           }
/*       */         }
/*       */       }
/*       */ 
/*  9822 */       if (((node.bits & 
/*  9822 */         0x80000) != 0) && (!this.options.performMethodsFullRecovery) && (!this.options.performStatementsRecovery)) {
/*  9823 */         return null;
/*       */       }
/*       */     }
/*  9826 */     if (containsInitializers) {
/*  9827 */       FieldDeclaration[] fieldDeclarations = typeDeclaration.fields;
/*  9828 */       int i = 0; for (int max = fieldDeclarations.length; i < max; i++) {
/*  9829 */         Initializer initializer = (Initializer)fieldDeclarations[i];
/*  9830 */         initializer.parseStatements(this, typeDeclaration, unit);
/*  9831 */         if (((initializer.bits & 0x80000) != 0) && (!this.options.performMethodsFullRecovery) && (!this.options.performStatementsRecovery)) {
/*  9832 */           return null;
/*       */         }
/*       */       }
/*       */     }
/*  9836 */     return result;
/*       */   }
/*       */ 
/*       */   public Expression parseExpression(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
/*  9840 */     initialize();
/*  9841 */     goForExpression();
/*  9842 */     this.nestedMethod[this.nestedType] += 1;
/*       */ 
/*  9844 */     this.referenceContext = unit;
/*  9845 */     this.compilationUnit = unit;
/*       */ 
/*  9847 */     this.scanner.setSource(source);
/*  9848 */     this.scanner.resetTo(offset, offset + length - 1);
/*       */     try {
/*  9850 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9852 */       this.lastAct = 12741;
/*       */     } finally {
/*  9854 */       this.nestedMethod[this.nestedType] -= 1;
/*       */     }
/*       */ 
/*  9857 */     if (this.lastAct == 12741) {
/*  9858 */       return null;
/*       */     }
/*       */ 
/*  9861 */     return this.expressionStack[this.expressionPtr];
/*       */   }
/*       */ 
/*       */   public Expression parseMemberValue(char[] source, int offset, int length, CompilationUnitDeclaration unit) {
/*  9865 */     initialize();
/*  9866 */     goForMemberValue();
/*  9867 */     this.nestedMethod[this.nestedType] += 1;
/*       */ 
/*  9869 */     this.referenceContext = unit;
/*  9870 */     this.compilationUnit = unit;
/*       */ 
/*  9872 */     this.scanner.setSource(source);
/*  9873 */     this.scanner.resetTo(offset, offset + length - 1);
/*       */     try {
/*  9875 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9877 */       this.lastAct = 12741;
/*       */     } finally {
/*  9879 */       this.nestedMethod[this.nestedType] -= 1;
/*       */     }
/*       */ 
/*  9882 */     if (this.lastAct == 12741) {
/*  9883 */       return null;
/*       */     }
/*       */ 
/*  9886 */     return this.expressionStack[this.expressionPtr];
/*       */   }
/*       */   public void parseStatements(ReferenceContext rc, int start, int end, TypeDeclaration[] types, CompilationUnitDeclaration unit) {
/*  9889 */     boolean oldStatementRecoveryEnabled = this.statementRecoveryActivated;
/*  9890 */     this.statementRecoveryActivated = true;
/*       */ 
/*  9892 */     initialize();
/*       */ 
/*  9894 */     goForBlockStatementsopt();
/*  9895 */     this.nestedMethod[this.nestedType] += 1;
/*  9896 */     pushOnRealBlockStack(0);
/*       */ 
/*  9898 */     pushOnAstLengthStack(0);
/*       */ 
/*  9900 */     this.referenceContext = rc;
/*  9901 */     this.compilationUnit = unit;
/*       */ 
/*  9903 */     this.pendingRecoveredType = null;
/*       */ 
/*  9905 */     if ((types != null) && (types.length > 0)) {
/*  9906 */       this.recoveredTypes = types;
/*  9907 */       this.recoveredTypePtr = 0;
/*  9908 */       this.nextTypeStart = 
/*  9909 */         (this.recoveredTypes[0].allocation == null ? 
/*  9910 */         this.recoveredTypes[0].declarationSourceStart : 
/*  9911 */         this.recoveredTypes[0].allocation.sourceStart);
/*       */     } else {
/*  9913 */       this.recoveredTypes = null;
/*  9914 */       this.recoveredTypePtr = -1;
/*  9915 */       this.nextTypeStart = -1;
/*       */     }
/*       */ 
/*  9918 */     this.scanner.resetTo(start, end);
/*       */ 
/*  9921 */     this.lastCheckPoint = this.scanner.initialPosition;
/*       */ 
/*  9924 */     this.stateStackTop = -1;
/*       */     try
/*       */     {
/*  9927 */       parse();
/*       */     } catch (AbortCompilation localAbortCompilation) {
/*  9929 */       this.lastAct = 12741;
/*       */     } finally {
/*  9931 */       this.nestedMethod[this.nestedType] -= 1;
/*  9932 */       this.recoveredTypes = null;
/*  9933 */       this.statementRecoveryActivated = oldStatementRecoveryEnabled;
/*       */     }
/*       */ 
/*  9936 */     checkNonNLSAfterBodyEnd(end);
/*       */   }
/*       */   public void persistLineSeparatorPositions() {
/*  9939 */     if (this.scanner.recordLineSeparator)
/*  9940 */       this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
/*       */   }
/*       */ 
/*       */   protected void prepareForBlockStatements()
/*       */   {
/*       */     int tmp6_5 = 0; this.nestedType = tmp6_5; this.nestedMethod[tmp6_5] = 1;
/*  9948 */     this.variablesCounter[this.nestedType] = 0;
/*       */     int tmp28_27 = 1; this.realBlockPtr = tmp28_27; this.realBlockStack[tmp28_27] = 0;
/*       */   }
/*       */ 
/*       */   public ProblemReporter problemReporter()
/*       */   {
/*  9959 */     if (this.scanner.recordLineSeparator) {
/*  9960 */       this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
/*       */     }
/*  9962 */     this.problemReporter.referenceContext = this.referenceContext;
/*  9963 */     return this.problemReporter;
/*       */   }
/*       */ 
/*       */   protected void pushIdentifier()
/*       */   {
/*  9970 */     int stackLength = this.identifierStack.length;
/*  9971 */     if (++this.identifierPtr >= stackLength) {
/*  9972 */       System.arraycopy(
/*  9973 */         this.identifierStack, 0, 
/*  9974 */         this.identifierStack = new char[stackLength + 20][], 0, 
/*  9975 */         stackLength);
/*  9976 */       System.arraycopy(
/*  9977 */         this.identifierPositionStack, 0, 
/*  9978 */         this.identifierPositionStack = new long[stackLength + 20], 0, 
/*  9979 */         stackLength);
/*       */     }
/*  9981 */     this.identifierStack[this.identifierPtr] = this.scanner.getCurrentIdentifierSource();
/*  9982 */     this.identifierPositionStack[this.identifierPtr] = 
/*  9983 */       ((this.scanner.startPosition << 32) + (this.scanner.currentPosition - 1));
/*       */ 
/*  9985 */     stackLength = this.identifierLengthStack.length;
/*  9986 */     if (++this.identifierLengthPtr >= stackLength) {
/*  9987 */       System.arraycopy(
/*  9988 */         this.identifierLengthStack, 0, 
/*  9989 */         this.identifierLengthStack = new int[stackLength + 10], 0, 
/*  9990 */         stackLength);
/*       */     }
/*  9992 */     this.identifierLengthStack[this.identifierLengthPtr] = 1;
/*       */   }
/*       */ 
/*       */   protected void pushIdentifier(int flag)
/*       */   {
/* 10000 */     int stackLength = this.identifierLengthStack.length;
/* 10001 */     if (++this.identifierLengthPtr >= stackLength) {
/* 10002 */       System.arraycopy(
/* 10003 */         this.identifierLengthStack, 0, 
/* 10004 */         this.identifierLengthStack = new int[stackLength + 10], 0, 
/* 10005 */         stackLength);
/*       */     }
/* 10007 */     this.identifierLengthStack[this.identifierLengthPtr] = flag;
/*       */   }
/*       */ 
/*       */   protected void pushOnAstLengthStack(int pos) {
/* 10011 */     int stackLength = this.astLengthStack.length;
/* 10012 */     if (++this.astLengthPtr >= stackLength) {
/* 10013 */       System.arraycopy(
/* 10014 */         this.astLengthStack, 0, 
/* 10015 */         this.astLengthStack = new int[stackLength + 255], 0, 
/* 10016 */         stackLength);
/*       */     }
/* 10018 */     this.astLengthStack[this.astLengthPtr] = pos;
/*       */   }
/*       */ 
/*       */   protected void pushOnAstStack(ASTNode node)
/*       */   {
/* 10024 */     int stackLength = this.astStack.length;
/* 10025 */     if (++this.astPtr >= stackLength) {
/* 10026 */       System.arraycopy(
/* 10027 */         this.astStack, 0, 
/* 10028 */         this.astStack = new ASTNode[stackLength + 100], 0, 
/* 10029 */         stackLength);
/* 10030 */       this.astPtr = stackLength;
/*       */     }
/* 10032 */     this.astStack[this.astPtr] = node;
/*       */ 
/* 10034 */     stackLength = this.astLengthStack.length;
/* 10035 */     if (++this.astLengthPtr >= stackLength) {
/* 10036 */       System.arraycopy(
/* 10037 */         this.astLengthStack, 0, 
/* 10038 */         this.astLengthStack = new int[stackLength + 100], 0, 
/* 10039 */         stackLength);
/*       */     }
/* 10041 */     this.astLengthStack[this.astLengthPtr] = 1;
/*       */   }
/*       */ 
/*       */   protected void pushOnExpressionStack(Expression expr) {
/* 10045 */     int stackLength = this.expressionStack.length;
/* 10046 */     if (++this.expressionPtr >= stackLength) {
/* 10047 */       System.arraycopy(
/* 10048 */         this.expressionStack, 0, 
/* 10049 */         this.expressionStack = new Expression[stackLength + 100], 0, 
/* 10050 */         stackLength);
/*       */     }
/* 10052 */     this.expressionStack[this.expressionPtr] = expr;
/*       */ 
/* 10054 */     stackLength = this.expressionLengthStack.length;
/* 10055 */     if (++this.expressionLengthPtr >= stackLength) {
/* 10056 */       System.arraycopy(
/* 10057 */         this.expressionLengthStack, 0, 
/* 10058 */         this.expressionLengthStack = new int[stackLength + 100], 0, 
/* 10059 */         stackLength);
/*       */     }
/* 10061 */     this.expressionLengthStack[this.expressionLengthPtr] = 1;
/*       */   }
/*       */ 
/*       */   protected void pushOnExpressionStackLengthStack(int pos) {
/* 10065 */     int stackLength = this.expressionLengthStack.length;
/* 10066 */     if (++this.expressionLengthPtr >= stackLength) {
/* 10067 */       System.arraycopy(
/* 10068 */         this.expressionLengthStack, 0, 
/* 10069 */         this.expressionLengthStack = new int[stackLength + 255], 0, 
/* 10070 */         stackLength);
/*       */     }
/* 10072 */     this.expressionLengthStack[this.expressionLengthPtr] = pos;
/*       */   }
/*       */   protected void pushOnGenericsIdentifiersLengthStack(int pos) {
/* 10075 */     int stackLength = this.genericsIdentifiersLengthStack.length;
/* 10076 */     if (++this.genericsIdentifiersLengthPtr >= stackLength) {
/* 10077 */       System.arraycopy(
/* 10078 */         this.genericsIdentifiersLengthStack, 0, 
/* 10079 */         this.genericsIdentifiersLengthStack = new int[stackLength + 10], 0, 
/* 10080 */         stackLength);
/*       */     }
/* 10082 */     this.genericsIdentifiersLengthStack[this.genericsIdentifiersLengthPtr] = pos;
/*       */   }
/*       */   protected void pushOnGenericsLengthStack(int pos) {
/* 10085 */     int stackLength = this.genericsLengthStack.length;
/* 10086 */     if (++this.genericsLengthPtr >= stackLength) {
/* 10087 */       System.arraycopy(
/* 10088 */         this.genericsLengthStack, 0, 
/* 10089 */         this.genericsLengthStack = new int[stackLength + 10], 0, 
/* 10090 */         stackLength);
/*       */     }
/* 10092 */     this.genericsLengthStack[this.genericsLengthPtr] = pos;
/*       */   }
/*       */ 
/*       */   protected void pushOnGenericsStack(ASTNode node)
/*       */   {
/* 10098 */     int stackLength = this.genericsStack.length;
/* 10099 */     if (++this.genericsPtr >= stackLength) {
/* 10100 */       System.arraycopy(
/* 10101 */         this.genericsStack, 0, 
/* 10102 */         this.genericsStack = new ASTNode[stackLength + 10], 0, 
/* 10103 */         stackLength);
/*       */     }
/* 10105 */     this.genericsStack[this.genericsPtr] = node;
/*       */ 
/* 10107 */     stackLength = this.genericsLengthStack.length;
/* 10108 */     if (++this.genericsLengthPtr >= stackLength) {
/* 10109 */       System.arraycopy(
/* 10110 */         this.genericsLengthStack, 0, 
/* 10111 */         this.genericsLengthStack = new int[stackLength + 10], 0, 
/* 10112 */         stackLength);
/*       */     }
/* 10114 */     this.genericsLengthStack[this.genericsLengthPtr] = 1;
/*       */   }
/*       */ 
/*       */   protected void pushOnIntStack(int pos) {
/* 10118 */     int stackLength = this.intStack.length;
/* 10119 */     if (++this.intPtr >= stackLength) {
/* 10120 */       System.arraycopy(
/* 10121 */         this.intStack, 0, 
/* 10122 */         this.intStack = new int[stackLength + 255], 0, 
/* 10123 */         stackLength);
/*       */     }
/* 10125 */     this.intStack[this.intPtr] = pos;
/*       */   }
/*       */ 
/*       */   protected void pushOnRealBlockStack(int i) {
/* 10129 */     int stackLength = this.realBlockStack.length;
/* 10130 */     if (++this.realBlockPtr >= stackLength) {
/* 10131 */       System.arraycopy(
/* 10132 */         this.realBlockStack, 0, 
/* 10133 */         this.realBlockStack = new int[stackLength + 255], 0, 
/* 10134 */         stackLength);
/*       */     }
/* 10136 */     this.realBlockStack[this.realBlockPtr] = i;
/*       */   }
/*       */ 
/*       */   protected void recoverStatements()
/*       */   {
/* 10278 */     Parser.1.MethodVisitor methodVisitor = new Parser.1.MethodVisitor(this);
/* 10279 */     Parser.1.TypeVisitor typeVisitor = new Parser.1.TypeVisitor(this);
/* 10280 */     methodVisitor.typeVisitor = typeVisitor;
/* 10281 */     typeVisitor.methodVisitor = methodVisitor;
/*       */ 
/* 10283 */     if ((this.referenceContext instanceof AbstractMethodDeclaration)) {
/* 10284 */       ((AbstractMethodDeclaration)this.referenceContext).traverse(methodVisitor, null);
/* 10285 */     } else if ((this.referenceContext instanceof TypeDeclaration)) {
/* 10286 */       TypeDeclaration typeContext = (TypeDeclaration)this.referenceContext;
/*       */ 
/* 10288 */       int length = typeContext.fields.length;
/* 10289 */       for (int i = 0; i < length; i++) {
/* 10290 */         FieldDeclaration fieldDeclaration = typeContext.fields[i];
/* 10291 */         switch (fieldDeclaration.getKind()) {
/*       */         case 2:
/* 10293 */           Initializer initializer = (Initializer)fieldDeclaration;
/* 10294 */           if (initializer.block != null) {
/* 10295 */             methodVisitor.enclosingType = typeContext;
/* 10296 */             initializer.traverse(methodVisitor, null);
/*       */           }
/*       */         }
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public void recoveryExitFromVariable() {
/* 10304 */     if ((this.currentElement != null) && (this.currentElement.parent != null))
/* 10305 */       if ((this.currentElement instanceof RecoveredLocalVariable))
/*       */       {
/* 10307 */         int end = ((RecoveredLocalVariable)this.currentElement).localDeclaration.sourceEnd;
/* 10308 */         this.currentElement.updateSourceEndIfNecessary(end);
/* 10309 */         this.currentElement = this.currentElement.parent;
/* 10310 */       } else if (((this.currentElement instanceof RecoveredField)) && 
/* 10311 */         (!(this.currentElement instanceof RecoveredInitializer)))
/*       */       {
/* 10313 */         int end = ((RecoveredField)this.currentElement).fieldDeclaration.sourceEnd;
/* 10314 */         this.currentElement.updateSourceEndIfNecessary(end);
/* 10315 */         this.currentElement = this.currentElement.parent;
/*       */       }
/*       */   }
/*       */ 
/*       */   public void recoveryTokenCheck()
/*       */   {
/* 10323 */     switch (this.currentToken) {
/*       */     case 52:
/* 10325 */       if ((!this.recordStringLiterals) || 
/* 10326 */         (!this.checkExternalizeStrings) || 
/* 10327 */         (this.lastPosistion >= this.scanner.currentPosition) || 
/* 10328 */         (this.statementRecoveryActivated)) break label385; StringLiteral stringLiteral = createStringLiteral(
/* 10330 */         this.scanner.getCurrentTokenSourceString(), 
/* 10331 */         this.scanner.startPosition, 
/* 10332 */         this.scanner.currentPosition - 1, 
/* 10333 */         Util.getLineNumber(this.scanner.startPosition, this.scanner.lineEnds, 0, this.scanner.linePtr));
/* 10334 */       this.compilationUnit.recordStringLiteral(stringLiteral, this.currentElement != null);
/*       */ 
/* 10336 */       break;
/*       */     case 69:
/* 10338 */       RecoveredElement newElement = null;
/* 10339 */       if (!this.ignoreNextOpeningBrace) {
/* 10340 */         newElement = this.currentElement.updateOnOpeningBrace(this.scanner.startPosition - 1, this.scanner.currentPosition - 1);
/*       */       }
/* 10342 */       this.lastCheckPoint = this.scanner.currentPosition;
/* 10343 */       if (newElement == null) break label385; this.restartRecovery = true;
/* 10345 */       this.currentElement = newElement;
/*       */ 
/* 10347 */       break;
/*       */     case 31:
/* 10350 */       this.rBraceStart = (this.scanner.startPosition - 1);
/* 10351 */       this.rBraceEnd = (this.scanner.currentPosition - 1);
/* 10352 */       this.endPosition = flushCommentsDefinedPriorTo(this.rBraceEnd);
/* 10353 */       RecoveredElement newElement = 
/* 10354 */         this.currentElement.updateOnClosingBrace(this.scanner.startPosition, this.rBraceEnd);
/* 10355 */       this.lastCheckPoint = this.scanner.currentPosition;
/* 10356 */       if (newElement == this.currentElement) break label385; this.currentElement = newElement;
/*       */ 
/* 10364 */       break;
/*       */     case 27:
/* 10366 */       this.endStatementPosition = (this.scanner.currentPosition - 1);
/* 10367 */       this.endPosition = (this.scanner.startPosition - 1);
/* 10368 */       RecoveredType currentType = currentRecoveryType();
/* 10369 */       if (currentType == null) break;
/* 10370 */       currentType.insideEnumConstantPart = false;
/*       */     }
/*       */ 
/* 10374 */     if ((this.rBraceEnd > this.rBraceSuccessorStart) && (this.scanner.currentPosition != this.scanner.startPosition)) {
/* 10375 */       this.rBraceSuccessorStart = this.scanner.startPosition;
/*       */     }
/*       */ 
/* 10380 */     label385: this.ignoreNextOpeningBrace = false;
/*       */   }
/*       */ 
/*       */   protected void reportSyntaxErrors(boolean isDietParse, int oldFirstToken) {
/* 10384 */     if ((this.referenceContext instanceof MethodDeclaration)) {
/* 10385 */       MethodDeclaration methodDeclaration = (MethodDeclaration)this.referenceContext;
/* 10386 */       if ((methodDeclaration.bits & 0x20) != 0) {
/* 10387 */         return;
/*       */       }
/*       */     }
/* 10390 */     this.compilationUnit.compilationResult.lineSeparatorPositions = this.scanner.getLineEnds();
/* 10391 */     this.scanner.recordLineSeparator = false;
/*       */ 
/* 10393 */     int start = this.scanner.initialPosition;
/* 10394 */     int end = this.scanner.eofPosition == 2147483647 ? this.scanner.eofPosition : this.scanner.eofPosition - 1;
/* 10395 */     if (isDietParse) {
/* 10396 */       TypeDeclaration[] types = this.compilationUnit.types;
/* 10397 */       int[][] intervalToSkip = RangeUtil.computeDietRange(types);
/* 10398 */       DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, intervalToSkip[0], intervalToSkip[1], intervalToSkip[2], this.options);
/* 10399 */       diagnoseParser.diagnoseParse(false);
/*       */ 
/* 10401 */       reportSyntaxErrorsForSkippedMethod(types);
/* 10402 */       this.scanner.resetTo(start, end);
/*       */     } else {
/* 10404 */       DiagnoseParser diagnoseParser = new DiagnoseParser(this, oldFirstToken, start, end, this.options);
/* 10405 */       diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
/*       */     }
/*       */   }
/*       */ 
/*       */   private void reportSyntaxErrorsForSkippedMethod(TypeDeclaration[] types) {
/* 10409 */     if (types != null)
/* 10410 */       for (int i = 0; i < types.length; i++) {
/* 10411 */         TypeDeclaration[] memberTypes = types[i].memberTypes;
/* 10412 */         if (memberTypes != null) {
/* 10413 */           reportSyntaxErrorsForSkippedMethod(memberTypes);
/*       */         }
/*       */ 
/* 10416 */         AbstractMethodDeclaration[] methods = types[i].methods;
/* 10417 */         if (methods != null) {
/* 10418 */           for (int j = 0; j < methods.length; j++) {
/* 10419 */             AbstractMethodDeclaration method = methods[j];
/* 10420 */             if ((method.bits & 0x20) != 0) {
/* 10421 */               if (method.isAnnotationMethod()) {
/* 10422 */                 DiagnoseParser diagnoseParser = new DiagnoseParser(this, 23, method.declarationSourceStart, method.declarationSourceEnd, this.options);
/* 10423 */                 diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
/*       */               } else {
/* 10425 */                 DiagnoseParser diagnoseParser = new DiagnoseParser(this, 6, method.declarationSourceStart, method.declarationSourceEnd, this.options);
/* 10426 */                 diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
/*       */               }
/*       */             }
/*       */           }
/*       */ 
/*       */         }
/*       */ 
/* 10433 */         FieldDeclaration[] fields = types[i].fields;
/* 10434 */         if (fields != null) {
/* 10435 */           int length = fields.length;
/* 10436 */           for (int j = 0; j < length; j++)
/* 10437 */             if ((fields[j] instanceof Initializer)) {
/* 10438 */               Initializer initializer = (Initializer)fields[j];
/* 10439 */               if ((initializer.bits & 0x20) != 0) {
/* 10440 */                 DiagnoseParser diagnoseParser = new DiagnoseParser(this, 10, initializer.declarationSourceStart, initializer.declarationSourceEnd, this.options);
/* 10441 */                 diagnoseParser.diagnoseParse(this.options.performStatementsRecovery);
/*       */               }
/*       */             }
/*       */         }
/*       */       }
/*       */   }
/*       */ 
/*       */   protected void resetModifiers()
/*       */   {
/* 10450 */     this.modifiers = 0;
/* 10451 */     this.modifiersSourceStart = -1;
/* 10452 */     this.scanner.commentPtr = -1;
/*       */   }
/*       */ 
/*       */   protected void resetStacks()
/*       */   {
/* 10459 */     this.astPtr = -1;
/* 10460 */     this.astLengthPtr = -1;
/* 10461 */     this.expressionPtr = -1;
/* 10462 */     this.expressionLengthPtr = -1;
/* 10463 */     this.identifierPtr = -1;
/* 10464 */     this.identifierLengthPtr = -1;
/* 10465 */     this.intPtr = -1;
/*       */     int tmp41_40 = 0; this.nestedType = tmp41_40; this.nestedMethod[tmp41_40] = 0;
/* 10467 */     this.variablesCounter[this.nestedType] = 0;
/* 10468 */     this.dimensions = 0;
/*       */     int tmp68_67 = 0; this.realBlockPtr = tmp68_67; this.realBlockStack[tmp68_67] = 0;
/* 10470 */     this.recoveredStaticInitializerStart = 0;
/* 10471 */     this.listLength = 0;
/* 10472 */     this.listTypeParameterLength = 0;
/*       */ 
/* 10474 */     this.genericsIdentifiersLengthPtr = -1;
/* 10475 */     this.genericsLengthPtr = -1;
/* 10476 */     this.genericsPtr = -1;
/*       */   }
/*       */ 
/*       */   protected boolean resumeAfterRecovery()
/*       */   {
/* 10486 */     if ((!this.methodRecoveryActivated) && (!this.statementRecoveryActivated))
/*       */     {
/* 10489 */       resetStacks();
/* 10490 */       resetModifiers();
/*       */ 
/* 10493 */       if (!moveRecoveryCheckpoint()) {
/* 10494 */         return false;
/*       */       }
/*       */ 
/* 10498 */       if ((this.referenceContext instanceof CompilationUnitDeclaration)) {
/* 10499 */         goForHeaders();
/* 10500 */         this.diet = true;
/* 10501 */         return true;
/*       */       }
/*       */ 
/* 10505 */       return false;
/* 10506 */     }if (!this.statementRecoveryActivated)
/*       */     {
/* 10509 */       resetStacks();
/* 10510 */       resetModifiers();
/*       */ 
/* 10513 */       if (!moveRecoveryCheckpoint()) {
/* 10514 */         return false;
/*       */       }
/*       */ 
/* 10518 */       goForHeaders();
/* 10519 */       return true;
/*       */     }
/* 10521 */     return false;
/*       */   }
/*       */ 
/*       */   protected boolean resumeOnSyntaxError()
/*       */   {
/* 10526 */     if (this.currentElement == null)
/*       */     {
/* 10528 */       this.javadoc = null;
/*       */ 
/* 10531 */       if (this.statementRecoveryActivated) return false;
/*       */ 
/* 10534 */       this.currentElement = buildInitialRecoveryState();
/*       */     }
/*       */ 
/* 10537 */     if (this.currentElement == null) return false;
/*       */ 
/* 10540 */     if (this.restartRecovery) {
/* 10541 */       this.restartRecovery = false;
/*       */     }
/*       */ 
/* 10544 */     updateRecoveryState();
/* 10545 */     if ((getFirstToken() == 20) && 
/* 10546 */       ((this.referenceContext instanceof CompilationUnitDeclaration))) {
/* 10547 */       TypeDeclaration typeDeclaration = new TypeDeclaration(this.referenceContext.compilationResult());
/* 10548 */       typeDeclaration.name = Util.EMPTY_STRING.toCharArray();
/* 10549 */       this.currentElement = this.currentElement.add(typeDeclaration, 0);
/*       */     }
/*       */ 
/* 10553 */     if (this.lastPosistion < this.scanner.currentPosition) {
/* 10554 */       this.lastPosistion = this.scanner.currentPosition;
/* 10555 */       this.scanner.lastPosition = this.scanner.currentPosition;
/*       */     }
/*       */ 
/* 10559 */     return resumeAfterRecovery();
/*       */   }
/*       */   public void setMethodsFullRecovery(boolean enabled) {
/* 10562 */     this.options.performMethodsFullRecovery = enabled;
/*       */   }
/*       */   public void setStatementsRecovery(boolean enabled) {
/* 10565 */     if (enabled) this.options.performMethodsFullRecovery = true;
/* 10566 */     this.options.performStatementsRecovery = enabled;
/*       */   }
/*       */ 
/*       */   public String toString()
/*       */   {
/* 10571 */     String s = "lastCheckpoint : int = " + String.valueOf(this.lastCheckPoint) + "\n";
/* 10572 */     s = s + "identifierStack : char[" + (this.identifierPtr + 1) + "][] = {";
/* 10573 */     for (int i = 0; i <= this.identifierPtr; i++) {
/* 10574 */       s = s + "\"" + String.valueOf(this.identifierStack[i]) + "\",";
/*       */     }
/* 10576 */     s = s + "}\n";
/*       */ 
/* 10578 */     s = s + "identifierLengthStack : int[" + (this.identifierLengthPtr + 1) + "] = {";
/* 10579 */     for (int i = 0; i <= this.identifierLengthPtr; i++) {
/* 10580 */       s = s + this.identifierLengthStack[i] + ",";
/*       */     }
/* 10582 */     s = s + "}\n";
/*       */ 
/* 10584 */     s = s + "astLengthStack : int[" + (this.astLengthPtr + 1) + "] = {";
/* 10585 */     for (int i = 0; i <= this.astLengthPtr; i++) {
/* 10586 */       s = s + this.astLengthStack[i] + ",";
/*       */     }
/* 10588 */     s = s + "}\n";
/* 10589 */     s = s + "astPtr : int = " + String.valueOf(this.astPtr) + "\n";
/*       */ 
/* 10591 */     s = s + "intStack : int[" + (this.intPtr + 1) + "] = {";
/* 10592 */     for (int i = 0; i <= this.intPtr; i++) {
/* 10593 */       s = s + this.intStack[i] + ",";
/*       */     }
/* 10595 */     s = s + "}\n";
/*       */ 
/* 10597 */     s = s + "expressionLengthStack : int[" + (this.expressionLengthPtr + 1) + "] = {";
/* 10598 */     for (int i = 0; i <= this.expressionLengthPtr; i++) {
/* 10599 */       s = s + this.expressionLengthStack[i] + ",";
/*       */     }
/* 10601 */     s = s + "}\n";
/*       */ 
/* 10603 */     s = s + "expressionPtr : int = " + String.valueOf(this.expressionPtr) + "\n";
/*       */ 
/* 10605 */     s = s + "genericsIdentifiersLengthStack : int[" + (this.genericsIdentifiersLengthPtr + 1) + "] = {";
/* 10606 */     for (int i = 0; i <= this.genericsIdentifiersLengthPtr; i++) {
/* 10607 */       s = s + this.genericsIdentifiersLengthStack[i] + ",";
/*       */     }
/* 10609 */     s = s + "}\n";
/*       */ 
/* 10611 */     s = s + "genericsLengthStack : int[" + (this.genericsLengthPtr + 1) + "] = {";
/* 10612 */     for (int i = 0; i <= this.genericsLengthPtr; i++) {
/* 10613 */       s = s + this.genericsLengthStack[i] + ",";
/*       */     }
/* 10615 */     s = s + "}\n";
/*       */ 
/* 10617 */     s = s + "genericsPtr : int = " + String.valueOf(this.genericsPtr) + "\n";
/*       */ 
/* 10619 */     s = s + "\n\n\n----------------Scanner--------------\n" + this.scanner.toString();
/* 10620 */     return s;
/*       */   }
/*       */ 
/*       */   protected void updateRecoveryState()
/*       */   {
/* 10629 */     this.currentElement.updateFromParserState();
/*       */ 
/* 10635 */     recoveryTokenCheck();
/*       */   }
/*       */ 
/*       */   protected void updateSourceDeclarationParts(int variableDeclaratorsCounter)
/*       */   {
/* 10643 */     int endTypeDeclarationPosition = 
/* 10644 */       -1 + this.astStack[(this.astPtr - variableDeclaratorsCounter + 1)].sourceStart;
/* 10645 */     for (int i = 0; i < variableDeclaratorsCounter - 1; i++)
/*       */     {
/* 10647 */       FieldDeclaration field = (FieldDeclaration)this.astStack[(this.astPtr - i - 1)];
/* 10648 */       field.endPart1Position = endTypeDeclarationPosition;
/* 10649 */       field.endPart2Position = (-1 + this.astStack[(this.astPtr - i)].sourceStart);
/*       */     }
/*       */     FieldDeclaration field;
/* 10652 */     (field = (FieldDeclaration)this.astStack[this.astPtr]).endPart1Position = 
/* 10653 */       endTypeDeclarationPosition;
/* 10654 */     field.endPart2Position = field.declarationSourceEnd;
/*       */   }
/*       */ 
/*       */   protected void updateSourcePosition(Expression exp)
/*       */   {
/* 10664 */     exp.sourceEnd = this.intStack[(this.intPtr--)];
/* 10665 */     exp.sourceStart = this.intStack[(this.intPtr--)];
/*       */   }
/*       */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.Parser
 * JD-Core Version:    0.6.0
 */