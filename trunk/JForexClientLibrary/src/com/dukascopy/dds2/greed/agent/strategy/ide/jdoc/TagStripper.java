/*     */ package com.dukascopy.dds2.greed.agent.strategy.ide.jdoc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import javax.swing.text.html.HTMLEditorKit.ParserCallback;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class TagStripper extends HTMLEditorKit.ParserCallback
/*     */ {
/* 216 */   private static final Logger LOGGER = LoggerFactory.getLogger(TagStripper.class);
/*     */   private Writer out;
/*     */ 
/*     */   public TagStripper(Writer out)
/*     */   {
/* 220 */     this.out = out;
/*     */   }
/*     */ 
/*     */   public void handleText(char[] text, int position) {
/*     */     try {
/* 225 */       this.out.write(text);
/* 226 */       this.out.write(new char[] { ' ' });
/* 227 */       this.out.flush();
/*     */     } catch (IOException e) {
/* 229 */       LOGGER.debug(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.ide.jdoc.TagStripper
 * JD-Core Version:    0.6.0
 */