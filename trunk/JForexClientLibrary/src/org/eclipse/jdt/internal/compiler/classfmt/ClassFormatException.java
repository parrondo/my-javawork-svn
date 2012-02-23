/*     */ package org.eclipse.jdt.internal.compiler.classfmt;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ 
/*     */ public class ClassFormatException extends Exception
/*     */ {
/*     */   public static final int ErrBadMagic = 1;
/*     */   public static final int ErrBadMinorVersion = 2;
/*     */   public static final int ErrBadMajorVersion = 3;
/*     */   public static final int ErrBadConstantClass = 4;
/*     */   public static final int ErrBadConstantString = 5;
/*     */   public static final int ErrBadConstantNameAndType = 6;
/*     */   public static final int ErrBadConstantFieldRef = 7;
/*     */   public static final int ErrBadConstantMethodRef = 8;
/*     */   public static final int ErrBadConstantInterfaceMethodRef = 9;
/*     */   public static final int ErrBadConstantPoolIndex = 10;
/*     */   public static final int ErrBadSuperclassName = 11;
/*     */   public static final int ErrInterfaceCannotBeFinal = 12;
/*     */   public static final int ErrInterfaceMustBeAbstract = 13;
/*     */   public static final int ErrBadModifiers = 14;
/*     */   public static final int ErrClassCannotBeAbstractFinal = 15;
/*     */   public static final int ErrBadClassname = 16;
/*     */   public static final int ErrBadFieldInfo = 17;
/*     */   public static final int ErrBadMethodInfo = 17;
/*     */   public static final int ErrEmptyConstantPool = 18;
/*     */   public static final int ErrMalformedUtf8 = 19;
/*     */   public static final int ErrUnknownConstantTag = 20;
/*     */   public static final int ErrTruncatedInput = 21;
/*     */   public static final int ErrMethodMustBeAbstract = 22;
/*     */   public static final int ErrMalformedAttribute = 23;
/*     */   public static final int ErrBadInterface = 24;
/*     */   public static final int ErrInterfaceMustSubclassObject = 25;
/*     */   public static final int ErrIncorrectInterfaceMethods = 26;
/*     */   public static final int ErrInvalidMethodName = 27;
/*     */   public static final int ErrInvalidMethodSignature = 28;
/*     */   private static final long serialVersionUID = 6667458511042774540L;
/*     */   private int errorCode;
/*     */   private int bufferPosition;
/*     */   private RuntimeException nestedException;
/*     */   private char[] fileName;
/*     */ 
/*     */   public ClassFormatException(RuntimeException e, char[] fileName)
/*     */   {
/*  54 */     this.nestedException = e;
/*  55 */     this.fileName = fileName;
/*     */   }
/*     */   public ClassFormatException(int code) {
/*  58 */     this.errorCode = code;
/*     */   }
/*     */   public ClassFormatException(int code, int bufPos) {
/*  61 */     this.errorCode = code;
/*  62 */     this.bufferPosition = bufPos;
/*     */   }
/*     */ 
/*     */   public int getErrorCode()
/*     */   {
/*  68 */     return this.errorCode;
/*     */   }
/*     */ 
/*     */   public int getBufferPosition()
/*     */   {
/*  74 */     return this.bufferPosition;
/*     */   }
/*     */ 
/*     */   public Throwable getException()
/*     */   {
/*  83 */     return this.nestedException;
/*     */   }
/*     */   public void printStackTrace() {
/*  86 */     printStackTrace(System.err);
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintStream output)
/*     */   {
/*  96 */     synchronized (output) {
/*  97 */       super.printStackTrace(output);
/*  98 */       Throwable throwable = getException();
/*  99 */       if (throwable != null) {
/* 100 */         if (this.fileName != null) {
/* 101 */           output.print("Caused in ");
/* 102 */           output.print(this.fileName);
/* 103 */           output.print(" by: ");
/*     */         } else {
/* 105 */           output.print("Caused by: ");
/*     */         }
/* 107 */         throwable.printStackTrace(output);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void printStackTrace(PrintWriter output)
/*     */   {
/* 119 */     synchronized (output) {
/* 120 */       super.printStackTrace(output);
/* 121 */       Throwable throwable = getException();
/* 122 */       if (throwable != null) {
/* 123 */         if (this.fileName != null) {
/* 124 */           output.print("Caused in ");
/* 125 */           output.print(this.fileName);
/* 126 */           output.print(" by: ");
/*     */         } else {
/* 128 */           output.print("Caused by: ");
/*     */         }
/* 130 */         throwable.printStackTrace(output);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.ClassFormatException
 * JD-Core Version:    0.6.0
 */