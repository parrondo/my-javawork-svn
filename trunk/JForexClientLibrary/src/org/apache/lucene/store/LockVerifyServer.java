/*    */ package org.apache.lucene.store;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class LockVerifyServer
/*    */ {
/*    */   private static String getTime(long startTime)
/*    */   {
/* 39 */     return "[" + (System.currentTimeMillis() - startTime) / 1000L + "s] ";
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws IOException
/*    */   {
/* 44 */     if (args.length != 1) {
/* 45 */       System.out.println("\nUsage: java org.apache.lucene.store.LockVerifyServer port\n");
/* 46 */       System.exit(1);
/*    */     }
/*    */ 
/* 49 */     int port = Integer.parseInt(args[0]);
/*    */ 
/* 51 */     ServerSocket s = new ServerSocket(port);
/* 52 */     s.setReuseAddress(true);
/* 53 */     System.out.println("\nReady on port " + port + "...");
/*    */ 
/* 55 */     int lockedID = 0;
/* 56 */     long startTime = System.currentTimeMillis();
/*    */     while (true)
/*    */     {
/* 59 */       Socket cs = s.accept();
/* 60 */       OutputStream out = cs.getOutputStream();
/* 61 */       InputStream in = cs.getInputStream();
/*    */ 
/* 63 */       int id = in.read();
/* 64 */       int command = in.read();
/*    */ 
/* 66 */       boolean err = false;
/*    */ 
/* 68 */       if (command == 1)
/*    */       {
/* 70 */         if (lockedID != 0) {
/* 71 */           err = true;
/* 72 */           System.out.println(getTime(startTime) + " ERROR: id " + id + " got lock, but " + lockedID + " already holds the lock");
/*    */         }
/* 74 */         lockedID = id;
/* 75 */       } else if (command == 0) {
/* 76 */         if (lockedID != id) {
/* 77 */           err = true;
/* 78 */           System.out.println(getTime(startTime) + " ERROR: id " + id + " released the lock, but " + lockedID + " is the one holding the lock");
/*    */         }
/* 80 */         lockedID = 0;
/*    */       } else {
/* 82 */         throw new RuntimeException("unrecognized command " + command);
/*    */       }
/* 84 */       System.out.print(".");
/*    */ 
/* 86 */       if (err)
/* 87 */         out.write(1);
/*    */       else {
/* 89 */         out.write(0);
/*    */       }
/* 91 */       out.close();
/* 92 */       in.close();
/* 93 */       cs.close();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.store.LockVerifyServer
 * JD-Core Version:    0.6.0
 */