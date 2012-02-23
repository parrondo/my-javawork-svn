/*    */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*    */ 
/*    */ import com.dukascopy.api.JFException;
/*    */ import com.dukascopy.dds2.greed.connector.helpers.ConverterHelpers;
/*    */ import com.dukascopy.dds2.greed.connector.helpers.ExternalEngine;
/*    */ import com.dukascopy.dds2.greed.connector.parser.util.CacheInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class ParserManager
/*    */ {
/* 12 */   String encoding = "UTF-8";
/* 13 */   ExternalEngine externalEngine = ExternalEngine.MT4STRATEGY;
/*    */ 
/*    */   public static CPPParser getParser() {
/* 16 */     return null;
/*    */   }
/*    */ 
/*    */   public String parse(StringBuilder buf, ExternalEngine engine, String encoding) throws IOException, JFException {
/* 20 */     ParserCallback cb = null;
/* 21 */     this.encoding = encoding;
/* 22 */     this.externalEngine = engine;
/*    */     try
/*    */     {
/* 25 */       InputStream is = ConverterHelpers.convertStringToStream(buf.toString(), encoding);
/* 26 */       CacheInputStream cis = new CacheInputStream(is);
/*    */ 
/* 28 */       CStructurizer.getCStructurizer().parse(new ParserTest(cis), cis);
/* 29 */       cb = CStructurizer.getCStructurizer().getParserCallback();
/*    */     }
/*    */     catch (ParseException pxc)
/*    */     {
/* 49 */       pxc.printStackTrace();
/* 50 */       throw new JFException("Parse exception", pxc);
/*    */     }
/*    */     catch (Exception exc)
/*    */     {
/* 54 */       exc.printStackTrace();
/* 55 */       throw new JFException("Parse exception", exc);
/*    */     }
/*    */ 
/* 58 */     return cb.print(engine);
/*    */   }
/*    */ 
/*    */   public ExternalEngine getExternalEngine() {
/* 62 */     return this.externalEngine;
/*    */   }
/*    */ 
/*    */   public void setExternalEngine(ExternalEngine externalEngine) {
/* 66 */     this.externalEngine = externalEngine;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.ParserManager
 * JD-Core Version:    0.6.0
 */