/*    */ package com.dukascopy.api.connector;
/*    */ 
/*    */ import com.dukascopy.api.IContext;
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.JFException;
/*    */ 
/*    */ public abstract class AbstractConnectorStrategyImpl extends AbstractConnectorImpl
/*    */   implements IStrategy, IConst, IColor, IWinUser32
/*    */ {
/*    */   public void onStart(IContext context)
/*    */     throws JFException
/*    */   {
/* 12 */     getConnector().onInit(context);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.AbstractConnectorStrategyImpl
 * JD-Core Version:    0.6.0
 */