/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.optimizer;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import java.text.MessageFormat;
/*    */ 
/*    */ public class CommitErrorException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 6881109110984982524L;
/*    */ 
/*    */   public CommitErrorException(String messageKey)
/*    */   {
/* 20 */     super(LocalizationManager.getText(messageKey));
/*    */   }
/*    */ 
/*    */   public CommitErrorException(String messageKey, Object[] params) {
/* 24 */     super(MessageFormat.format(LocalizationManager.getText(messageKey), params));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.optimizer.CommitErrorException
 * JD-Core Version:    0.6.0
 */