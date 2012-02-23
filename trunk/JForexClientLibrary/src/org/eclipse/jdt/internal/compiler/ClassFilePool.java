/*    */ package org.eclipse.jdt.internal.compiler;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*    */ 
/*    */ public class ClassFilePool
/*    */ {
/*    */   public static final int POOL_SIZE = 25;
/*    */   ClassFile[] classFiles;
/*    */ 
/*    */   private ClassFilePool()
/*    */   {
/* 23 */     this.classFiles = new ClassFile[25];
/*    */   }
/*    */ 
/*    */   public static ClassFilePool newInstance() {
/* 27 */     return new ClassFilePool();
/*    */   }
/*    */ 
/*    */   public synchronized ClassFile acquire(SourceTypeBinding typeBinding) {
/* 31 */     for (int i = 0; i < 25; i++) {
/* 32 */       ClassFile classFile = this.classFiles[i];
/* 33 */       if (classFile == null) {
/* 34 */         ClassFile newClassFile = new ClassFile(typeBinding);
/* 35 */         this.classFiles[i] = newClassFile;
/* 36 */         newClassFile.isShared = true;
/* 37 */         return newClassFile;
/*    */       }
/* 39 */       if (!classFile.isShared) {
/* 40 */         classFile.reset(typeBinding);
/* 41 */         classFile.isShared = true;
/* 42 */         return classFile;
/*    */       }
/*    */     }
/* 45 */     return new ClassFile(typeBinding);
/*    */   }
/*    */   public synchronized void release(ClassFile classFile) {
/* 48 */     classFile.isShared = false;
/*    */   }
/*    */   public void reset() {
/* 51 */     Arrays.fill(this.classFiles, null);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ClassFilePool
 * JD-Core Version:    0.6.0
 */