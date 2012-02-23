/*    */ package com.dukascopy.dds2.greed.agent.protocol;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.compiler.JFXClassLoader;
/*    */ import java.io.IOException;
/*    */ import java.net.URL;
/*    */ import java.net.URLConnection;
/*    */ import java.net.URLStreamHandler;
/*    */ 
/*    */ public class JFXStreamHandler extends URLStreamHandler
/*    */ {
/*    */   ClassLoader classLoader;
/*    */ 
/*    */   public JFXStreamHandler(ClassLoader classLoader)
/*    */   {
/* 21 */     this.classLoader = classLoader;
/*    */   }
/*    */ 
/*    */   protected URLConnection openConnection(URL u)
/*    */     throws IOException
/*    */   {
/* 29 */     URLConnection connection = null;
/* 30 */     if ((this.classLoader instanceof JFXClassLoader)) {
/* 31 */       JFXClassLoader loader = (JFXClassLoader)this.classLoader;
/* 32 */       connection = new JFXURLConnection(u, loader.findResourceAsBytes(u.getHost() + u.getPath()));
/*    */     }
/*    */ 
/* 35 */     return connection;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.protocol.JFXStreamHandler
 * JD-Core Version:    0.6.0
 */