/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public class SignatureWrapper
/*    */ {
/*    */   public char[] signature;
/*    */   public int start;
/*    */   public int end;
/*    */   public int bracket;
/*    */ 
/*    */   public SignatureWrapper(char[] signature)
/*    */   {
/* 22 */     this.signature = signature;
/* 23 */     this.start = 0;
/* 24 */     this.end = (this.bracket = -1);
/*    */   }
/*    */   public boolean atEnd() {
/* 27 */     return (this.start < 0) || (this.start >= this.signature.length);
/*    */   }
/*    */   public int computeEnd() {
/* 30 */     int index = this.start;
/* 31 */     while (this.signature[index] == '[')
/* 32 */       index++;
/* 33 */     switch (this.signature[index]) {
/*    */     case 'L':
/*    */     case 'T':
/* 36 */       this.end = CharOperation.indexOf(';', this.signature, this.start);
/* 37 */       if (this.bracket <= this.start) {
/* 38 */         this.bracket = CharOperation.indexOf('<', this.signature, this.start);
/*    */       }
/* 40 */       if ((this.bracket > this.start) && (this.bracket < this.end)) {
/* 41 */         this.end = this.bracket; } else {
/* 42 */         if (this.end != -1) break;
/* 43 */         this.end = (this.signature.length + 1);
/* 44 */       }break;
/*    */     default:
/* 46 */       this.end = this.start;
/*    */     }
/*    */ 
/* 49 */     this.start = (this.end + 1);
/* 50 */     return this.end;
/*    */   }
/*    */   public char[] nextWord() {
/* 53 */     this.end = CharOperation.indexOf(';', this.signature, this.start);
/* 54 */     if (this.bracket <= this.start)
/* 55 */       this.bracket = CharOperation.indexOf('<', this.signature, this.start);
/* 56 */     int dot = CharOperation.indexOf('.', this.signature, this.start);
/*    */ 
/* 58 */     if ((this.bracket > this.start) && (this.bracket < this.end))
/* 59 */       this.end = this.bracket;
/* 60 */     if ((dot > this.start) && (dot < this.end)) {
/* 61 */       this.end = dot;
/*    */     }
/* 63 */     return CharOperation.subarray(this.signature, this.start, this.start = this.end);
/*    */   }
/*    */   public String toString() {
/* 66 */     return new String(this.signature) + " @ " + this.start;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.SignatureWrapper
 * JD-Core Version:    0.6.0
 */