/*     */ package org.apache.lucene.store;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ 
/*     */ public class VerifyingLockFactory extends LockFactory
/*     */ {
/*     */   LockFactory lf;
/*     */   byte id;
/*     */   String host;
/*     */   int port;
/*     */ 
/*     */   public VerifyingLockFactory(byte id, LockFactory lf, String host, int port)
/*     */     throws IOException
/*     */   {
/* 109 */     this.id = id;
/* 110 */     this.lf = lf;
/* 111 */     this.host = host;
/* 112 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public synchronized Lock makeLock(String lockName)
/*     */   {
/* 117 */     return new CheckedLock(this.lf.makeLock(lockName));
/*     */   }
/*     */ 
/*     */   public synchronized void clearLock(String lockName)
/*     */     throws IOException
/*     */   {
/* 123 */     this.lf.clearLock(lockName);
/*     */   }
/*     */ 
/*     */   private class CheckedLock extends Lock
/*     */   {
/*     */     private Lock lock;
/*     */ 
/*     */     public CheckedLock(Lock lock)
/*     */     {
/*  50 */       this.lock = lock;
/*     */     }
/*     */ 
/*     */     private void verify(byte message) {
/*     */       try {
/*  55 */         Socket s = new Socket(VerifyingLockFactory.this.host, VerifyingLockFactory.this.port);
/*  56 */         OutputStream out = s.getOutputStream();
/*  57 */         out.write(VerifyingLockFactory.this.id);
/*  58 */         out.write(message);
/*  59 */         InputStream in = s.getInputStream();
/*  60 */         int result = in.read();
/*  61 */         in.close();
/*  62 */         out.close();
/*  63 */         s.close();
/*  64 */         if (result != 0)
/*  65 */           throw new RuntimeException("lock was double acquired");
/*     */       } catch (Exception e) {
/*  67 */         throw new RuntimeException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public synchronized boolean obtain(long lockWaitTimeout)
/*     */       throws LockObtainFailedException, IOException
/*     */     {
/*  74 */       boolean obtained = this.lock.obtain(lockWaitTimeout);
/*  75 */       if (obtained)
/*  76 */         verify(1);
/*  77 */       return obtained;
/*     */     }
/*     */ 
/*     */     public synchronized boolean obtain()
/*     */       throws LockObtainFailedException, IOException
/*     */     {
/*  83 */       return this.lock.obtain();
/*     */     }
/*     */ 
/*     */     public synchronized boolean isLocked() throws IOException
/*     */     {
/*  88 */       return this.lock.isLocked();
/*     */     }
/*     */ 
/*     */     public synchronized void release() throws IOException
/*     */     {
/*  93 */       if (isLocked()) {
/*  94 */         verify(0);
/*  95 */         this.lock.release();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.VerifyingLockFactory
 * JD-Core Version:    0.6.0
 */