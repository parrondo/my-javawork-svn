/*    */ package org.eclipse.jdt.internal.compiler.codegen;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public class ExceptionLabel extends Label
/*    */ {
/* 18 */   public int[] ranges = { -1, -1 };
/* 19 */   public int count = 0;
/*    */   public TypeBinding exceptionType;
/*    */ 
/*    */   public ExceptionLabel(CodeStream codeStream, TypeBinding exceptionType)
/*    */   {
/* 23 */     super(codeStream);
/* 24 */     this.exceptionType = exceptionType;
/*    */   }
/*    */ 
/*    */   public void place()
/*    */   {
/* 29 */     this.codeStream.registerExceptionHandler(this);
/* 30 */     this.position = this.codeStream.getPosition();
/*    */   }
/*    */ 
/*    */   public void placeEnd() {
/* 34 */     int endPosition = this.codeStream.position;
/* 35 */     if (this.ranges[(this.count - 1)] == endPosition)
/*    */     {
/* 37 */       this.count -= 1;
/*    */     }
/* 39 */     else this.ranges[(this.count++)] = endPosition;
/*    */   }
/*    */ 
/*    */   public void placeStart()
/*    */   {
/* 44 */     int startPosition = this.codeStream.position;
/* 45 */     if ((this.count > 0) && (this.ranges[(this.count - 1)] == startPosition))
/*    */     {
/* 47 */       this.count -= 1;
/* 48 */       return;
/*    */     }
/*    */     int length;
/* 52 */     if (this.count == (length = this.ranges.length)) {
/* 53 */       System.arraycopy(this.ranges, 0, this.ranges = new int[length * 2], 0, length);
/*    */     }
/* 55 */     this.ranges[(this.count++)] = startPosition;
/*    */   }
/*    */   public String toString() {
/* 58 */     String basic = getClass().getName();
/* 59 */     basic = basic.substring(basic.lastIndexOf('.') + 1);
/* 60 */     StringBuffer buffer = new StringBuffer(basic);
/* 61 */     buffer.append('@').append(Integer.toHexString(hashCode()));
/* 62 */     buffer.append("(type=").append(this.exceptionType == null ? CharOperation.NO_CHAR : this.exceptionType.readableName());
/* 63 */     buffer.append(", position=").append(this.position);
/* 64 */     buffer.append(", ranges = ");
/* 65 */     if (this.count == 0) {
/* 66 */       buffer.append("[]");
/*    */     } else {
/* 68 */       for (int i = 0; i < this.count; i++) {
/* 69 */         if ((i & 0x1) == 0)
/* 70 */           buffer.append("[").append(this.ranges[i]);
/*    */         else {
/* 72 */           buffer.append(",").append(this.ranges[i]).append("]");
/*    */         }
/*    */       }
/* 75 */       if ((this.count & 0x1) == 1) {
/* 76 */         buffer.append(",?]");
/*    */       }
/*    */     }
/* 79 */     buffer.append(')');
/* 80 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.ExceptionLabel
 * JD-Core Version:    0.6.0
 */