/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class TwoPhaseCommitTool
/*     */ {
/*     */   private static void rollback(TwoPhaseCommit[] objects)
/*     */   {
/*  96 */     for (TwoPhaseCommit tpc : objects)
/*     */     {
/*  99 */       if (tpc == null) continue;
/*     */       try {
/* 101 */         tpc.rollback();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void execute(TwoPhaseCommit[] objects)
/*     */     throws TwoPhaseCommitTool.PrepareCommitFailException, TwoPhaseCommitTool.CommitFailException
/*     */   {
/* 130 */     TwoPhaseCommit tpc = null;
/*     */     try
/*     */     {
/* 133 */       for (int i = 0; i < objects.length; i++) {
/* 134 */         tpc = objects[i];
/* 135 */         if (tpc != null) {
/* 136 */           tpc.prepareCommit();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 142 */       rollback(objects);
/* 143 */       throw new PrepareCommitFailException(t, tpc);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 148 */       for (int i = 0; i < objects.length; i++) {
/* 149 */         tpc = objects[i];
/* 150 */         if (tpc != null) {
/* 151 */           tpc.commit();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 157 */       rollback(objects);
/* 158 */       throw new CommitFailException(t, tpc);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class CommitFailException extends IOException
/*     */   {
/*     */     public CommitFailException(Throwable cause, TwoPhaseCommit obj)
/*     */     {
/*  88 */       super();
/*  89 */       initCause(cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class PrepareCommitFailException extends IOException
/*     */   {
/*     */     public PrepareCommitFailException(Throwable cause, TwoPhaseCommit obj)
/*     */     {
/*  75 */       super();
/*  76 */       initCause(cause);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class TwoPhaseCommitWrapper
/*     */     implements TwoPhaseCommit
/*     */   {
/*     */     private final TwoPhaseCommit tpc;
/*     */     private final Map<String, String> commitData;
/*     */ 
/*     */     public TwoPhaseCommitWrapper(TwoPhaseCommit tpc, Map<String, String> commitData)
/*     */     {
/*  43 */       this.tpc = tpc;
/*  44 */       this.commitData = commitData;
/*     */     }
/*     */ 
/*     */     public void prepareCommit() throws IOException {
/*  48 */       prepareCommit(this.commitData);
/*     */     }
/*     */ 
/*     */     public void prepareCommit(Map<String, String> commitData) throws IOException {
/*  52 */       this.tpc.prepareCommit(this.commitData);
/*     */     }
/*     */ 
/*     */     public void commit() throws IOException {
/*  56 */       commit(this.commitData);
/*     */     }
/*     */ 
/*     */     public void commit(Map<String, String> commitData) throws IOException {
/*  60 */       this.tpc.commit(this.commitData);
/*     */     }
/*     */ 
/*     */     public void rollback() throws IOException {
/*  64 */       this.tpc.rollback();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.TwoPhaseCommitTool
 * JD-Core Version:    0.6.0
 */