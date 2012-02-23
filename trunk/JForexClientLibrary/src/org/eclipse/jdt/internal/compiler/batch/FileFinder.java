/*    */ package org.eclipse.jdt.internal.compiler.batch;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class FileFinder
/*    */ {
/*    */   public static String[] find(File f, String pattern)
/*    */   {
/* 19 */     ArrayList files = new ArrayList();
/* 20 */     find0(f, pattern, files);
/* 21 */     String[] result = new String[files.size()];
/* 22 */     files.toArray(result);
/* 23 */     return result;
/*    */   }
/*    */   private static void find0(File f, String pattern, ArrayList collector) {
/* 26 */     if (f.isDirectory()) {
/* 27 */       String[] files = f.list();
/* 28 */       if (files == null) return;
/* 29 */       int i = 0; for (int max = files.length; i < max; i++) {
/* 30 */         File current = new File(f, files[i]);
/* 31 */         if (current.isDirectory()) {
/* 32 */           find0(current, pattern, collector);
/*    */         }
/* 34 */         else if (current.getName().toUpperCase().endsWith(pattern))
/* 35 */           collector.add(current.getAbsolutePath());
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.FileFinder
 * JD-Core Version:    0.6.0
 */