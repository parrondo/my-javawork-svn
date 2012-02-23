/*    */ package org.eclipse.jdt.internal.compiler.codegen;
/*    */ 
/*    */ public class CaseLabel extends BranchLabel
/*    */ {
/* 15 */   public int instructionPosition = -1;
/*    */ 
/*    */   public CaseLabel(CodeStream codeStream)
/*    */   {
/* 22 */     super(codeStream);
/*    */   }
/*    */ 
/*    */   void branch()
/*    */   {
/* 30 */     if (this.position == -1) {
/* 31 */       addForwardReference(this.codeStream.position);
/*    */ 
/* 33 */       this.codeStream.position += 4;
/* 34 */       this.codeStream.classFileOffset += 4;
/*    */     }
/*    */     else
/*    */     {
/* 39 */       this.codeStream.writeSignedWord(this.position - this.instructionPosition);
/*    */     }
/*    */   }
/*    */ 
/*    */   void branchWide()
/*    */   {
/* 47 */     branch();
/*    */   }
/*    */ 
/*    */   public boolean isCaseLabel() {
/* 51 */     return true;
/*    */   }
/*    */   public boolean isStandardLabel() {
/* 54 */     return false;
/*    */   }
/*    */ 
/*    */   public void place()
/*    */   {
/* 60 */     if ((this.tagBits & 0x2) != 0)
/* 61 */       this.position = this.codeStream.getPosition();
/*    */     else {
/* 63 */       this.position = this.codeStream.position;
/*    */     }
/* 65 */     if (this.instructionPosition != -1) {
/* 66 */       int offset = this.position - this.instructionPosition;
/* 67 */       int[] forwardRefs = forwardReferences();
/* 68 */       int i = 0; for (int length = forwardReferenceCount(); i < length; i++) {
/* 69 */         this.codeStream.writeSignedWord(forwardRefs[i], offset);
/*    */       }
/*    */ 
/* 72 */       this.codeStream.addLabel(this);
/*    */     }
/*    */   }
/*    */ 
/*    */   void placeInstruction()
/*    */   {
/* 80 */     if (this.instructionPosition == -1)
/* 81 */       this.instructionPosition = this.codeStream.position;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.CaseLabel
 * JD-Core Version:    0.6.0
 */