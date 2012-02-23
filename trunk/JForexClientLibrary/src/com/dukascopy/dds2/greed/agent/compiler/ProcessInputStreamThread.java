/*    */ package com.dukascopy.dds2.greed.agent.compiler;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.Reader;
/*    */ import java.io.Writer;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ProcessInputStreamThread extends Thread
/*    */ {
/* 17 */   private static final Logger LOGGER = LoggerFactory.getLogger(ProcessInputStreamThread.class);
/*    */   private Reader reader;
/*    */   private Writer writer;
/*    */ 
/*    */   public ProcessInputStreamThread(InputStream in, PrintStream printStream)
/*    */   {
/* 24 */     this.reader = new InputStreamReader(new BufferedInputStream(in));
/* 25 */     if (printStream == null) {
/* 26 */       printStream = System.out;
/*    */     }
/* 28 */     this.writer = new OutputStreamWriter(printStream);
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */     try
/*    */     {
/*    */       int c;
/* 36 */       while ((c = this.reader.read()) != -1)
/*    */       {
/* 38 */         this.writer.write(c);
/* 39 */         if (c == 10) {
/* 40 */           this.writer.flush();
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (IOException ioe)
/*    */     {
/* 46 */       LOGGER.error(ioe.getMessage(), ioe);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.compiler.ProcessInputStreamThread
 * JD-Core Version:    0.6.0
 */