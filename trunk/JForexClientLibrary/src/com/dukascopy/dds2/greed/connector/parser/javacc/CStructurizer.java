/*    */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*    */ 
/*    */ import com.dukascopy.api.JFException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class CStructurizer
/*    */ {
/* 10 */   private static CStructurizer fgStructurizerSingelton = new CStructurizer();
/*    */   private CPPParser fParser;
/*    */   private ParserCallback cb;
/*    */ 
/*    */   public static CStructurizer getCStructurizer()
/*    */   {
/* 13 */     return fgStructurizerSingelton;
/*    */   }
/*    */ 
/*    */   public synchronized void parse(IStructurizerCallback callback, InputStream inputStream)
/*    */     throws IOException, ParseException, JFException
/*    */   {
/* 22 */     LinePositionInputStream lpiStream = new LinePositionInputStream(inputStream);
/*    */     try {
/* 24 */       this.cb = new ParserCallback(lpiStream, callback);
/* 25 */       if (this.fParser == null)
/* 26 */         this.fParser = new CPPParser(lpiStream);
/*    */       else {
/* 28 */         CPPParser.ReInit(lpiStream);
/*    */       }
/* 30 */       CPPParser.setParserCallback(this.cb);
/* 31 */       this.cb.setJjtree(CPPParser.jjtree);
/* 32 */       CPPParser.translation_unit();
/*    */     }
/*    */     catch (TokenMgrError error)
/*    */     {
/* 36 */       throw new JFException(error.getMessage());
/*    */     }
/*    */     catch (ParseException e)
/*    */     {
/* 40 */       e.printStackTrace();
/* 41 */       throw new JFException(e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public CPPParser getParser() {
/* 46 */     return this.fParser;
/*    */   }
/*    */   public ParserCallback getParserCallback() {
/* 49 */     return this.cb;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.CStructurizer
 * JD-Core Version:    0.6.0
 */