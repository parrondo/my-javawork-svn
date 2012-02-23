/*     */ package org.eclipse.jdt.internal.compiler.problem;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class DefaultProblem extends CategorizedProblem
/*     */ {
/*     */   private char[] fileName;
/*     */   private int id;
/*     */   private int startPosition;
/*     */   private int endPosition;
/*     */   private int line;
/*     */   private int column;
/*     */   private int severity;
/*     */   private String[] arguments;
/*     */   private String message;
/*     */   private static final String MARKER_TYPE_PROBLEM = "org.eclipse.jdt.core.problem";
/*     */   private static final String MARKER_TYPE_TASK = "org.eclipse.jdt.core.task";
/*  30 */   public static final Object[] EMPTY_VALUES = new Object[0];
/*     */ 
/*     */   public DefaultProblem(char[] originatingFileName, String message, int id, String[] stringArguments, int severity, int startPosition, int endPosition, int line, int column)
/*     */   {
/*  43 */     this.fileName = originatingFileName;
/*  44 */     this.message = message;
/*  45 */     this.id = id;
/*  46 */     this.arguments = stringArguments;
/*  47 */     this.severity = severity;
/*  48 */     this.startPosition = startPosition;
/*  49 */     this.endPosition = endPosition;
/*  50 */     this.line = line;
/*  51 */     this.column = column;
/*     */   }
/*     */ 
/*     */   public String errorReportSource(char[] unitSource)
/*     */   {
/*  62 */     if ((this.startPosition > this.endPosition) || 
/*  63 */       ((this.startPosition < 0) && (this.endPosition < 0)) || 
/*  64 */       (unitSource.length == 0)) {
/*  65 */       return Messages.problem_noSourceInformation;
/*     */     }
/*  67 */     StringBuffer errorBuffer = new StringBuffer();
/*  68 */     errorBuffer.append(' ').append(Messages.bind(Messages.problem_atLine, String.valueOf(this.line)));
/*  69 */     errorBuffer.append(Util.LINE_SEPARATOR);
/*  70 */     errorBuffer.append('\t');
/*     */ 
/*  82 */     int length = unitSource.length;
/*  83 */     for (int begin = this.startPosition >= length ? length - 1 : this.startPosition; begin > 0; begin--)
/*     */     {
/*     */       char c;
/*  84 */       if (((c = unitSource[(begin - 1)]) == '\n') || (c == '\r')) break;
/*     */     }
/*  86 */     for (int end = this.endPosition >= length ? length - 1 : this.endPosition; end + 1 < length; end++)
/*     */     {
/*     */       char c;
/*  87 */       if (((c = unitSource[(end + 1)]) == '\r') || (c == '\n'))
/*     */         break;
/*     */     }
/*     */     char c;
/*  91 */     while (((c = unitSource[begin]) == ' ') || (c == '\t'))
/*     */     {
/*     */       char c;
/*  91 */       begin++;
/*     */     }
/*     */ 
/*  95 */     errorBuffer.append(unitSource, begin, end - begin + 1);
/*  96 */     errorBuffer.append(Util.LINE_SEPARATOR).append("\t");
/*     */ 
/*  99 */     for (int i = begin; i < this.startPosition; i++) {
/* 100 */       errorBuffer.append(unitSource[i] == '\t' ? '\t' : ' ');
/*     */     }
/* 102 */     for (int i = this.startPosition; i <= (this.endPosition >= length ? length - 1 : this.endPosition); i++) {
/* 103 */       errorBuffer.append('^');
/*     */     }
/* 105 */     return errorBuffer.toString();
/*     */   }
/*     */ 
/*     */   public String[] getArguments()
/*     */   {
/* 112 */     return this.arguments;
/*     */   }
/*     */ 
/*     */   public int getCategoryID()
/*     */   {
/* 118 */     return ProblemReporter.getProblemCategory(this.severity, this.id);
/*     */   }
/*     */ 
/*     */   public int getID()
/*     */   {
/* 127 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getInternalCategoryMessage()
/*     */   {
/* 137 */     switch (getCategoryID()) {
/*     */     case 0:
/* 139 */       return "unspecified";
/*     */     case 10:
/* 141 */       return "buildpath";
/*     */     case 20:
/* 143 */       return "syntax";
/*     */     case 30:
/* 145 */       return "import";
/*     */     case 40:
/* 147 */       return "type";
/*     */     case 50:
/* 149 */       return "member";
/*     */     case 60:
/* 151 */       return "internal";
/*     */     case 70:
/* 153 */       return "javadoc";
/*     */     case 80:
/* 155 */       return "code style";
/*     */     case 90:
/* 157 */       return "potential programming problem";
/*     */     case 100:
/* 159 */       return "name shadowing conflict";
/*     */     case 110:
/* 161 */       return "deprecation";
/*     */     case 120:
/* 163 */       return "unnecessary code";
/*     */     case 130:
/* 165 */       return "unchecked/raw";
/*     */     case 140:
/* 167 */       return "nls";
/*     */     case 150:
/* 169 */       return "restriction";
/*     */     }
/* 171 */     return null;
/*     */   }
/*     */ 
/*     */   public String getMarkerType()
/*     */   {
/* 179 */     return this.id == 536871362 ? 
/* 180 */       "org.eclipse.jdt.core.task" : 
/* 181 */       "org.eclipse.jdt.core.problem";
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/* 189 */     return this.message;
/*     */   }
/*     */ 
/*     */   public char[] getOriginatingFileName()
/*     */   {
/* 197 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public int getSourceEnd()
/*     */   {
/* 205 */     return this.endPosition;
/*     */   }
/*     */ 
/*     */   public int getSourceColumnNumber()
/*     */   {
/* 212 */     return this.column;
/*     */   }
/*     */ 
/*     */   public int getSourceLineNumber()
/*     */   {
/* 219 */     return this.line;
/*     */   }
/*     */ 
/*     */   public int getSourceStart()
/*     */   {
/* 226 */     return this.startPosition;
/*     */   }
/*     */ 
/*     */   public boolean isError()
/*     */   {
/* 234 */     return (this.severity & 0x1) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isWarning()
/*     */   {
/* 242 */     return (this.severity & 0x1) == 0;
/*     */   }
/*     */ 
/*     */   public void setOriginatingFileName(char[] fileName) {
/* 246 */     this.fileName = fileName;
/*     */   }
/*     */ 
/*     */   public void setSourceEnd(int sourceEnd)
/*     */   {
/* 256 */     this.endPosition = sourceEnd;
/*     */   }
/*     */ 
/*     */   public void setSourceLineNumber(int lineNumber)
/*     */   {
/* 265 */     this.line = lineNumber;
/*     */   }
/*     */ 
/*     */   public void setSourceStart(int sourceStart)
/*     */   {
/* 275 */     this.startPosition = sourceStart;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 279 */     String s = "Pb(" + (this.id & 0xFFFFFF) + ") ";
/* 280 */     if (this.message != null) {
/* 281 */       s = s + this.message;
/*     */     }
/* 283 */     else if (this.arguments != null) {
/* 284 */       for (int i = 0; i < this.arguments.length; i++)
/* 285 */         s = s + " " + this.arguments[i];
/*     */     }
/* 287 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.problem.DefaultProblem
 * JD-Core Version:    0.6.0
 */