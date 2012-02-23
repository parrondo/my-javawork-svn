/*    */ package com.dukascopy.dds2.greed.connector;
/*    */ 
/*    */ import com.dukascopy.api.connector.IConnector;
/*    */ import com.dukascopy.api.connector.IConnectorHelpers;
/*    */ import com.dukascopy.api.connector.IConnectorManager;
/*    */ import com.dukascopy.api.connector.IConnectorManager.ConnectorKeys;
/*    */ 
/*    */ public class ConnectorManager
/*    */   implements IConnectorManager
/*    */ {
/* 12 */   private static ConnectorManager instance = null;
/* 13 */   private IConnectorHelpers connectorHelpers = null;
/*    */   public static final String connectorHelpersClassPath = "com.dukascopy.api.connector.ConnectorHelpers";
/*    */ 
/*    */   /** @deprecated */
/*    */   public IConnector createConnectorInstance(String className)
/*    */     throws InstantiationException, IllegalAccessException, ClassNotFoundException
/*    */   {
/* 21 */     return (IConnector)Class.forName(className).newInstance();
/*    */   }
/*    */ 
/*    */   private IConnectorHelpers getConnectorHelpers() {
/* 25 */     if (this.connectorHelpers == null) {
/*    */       try {
/* 27 */         this.connectorHelpers = ((IConnectorHelpers)Class.forName("com.dukascopy.api.connector.ConnectorHelpers").newInstance());
/*    */       }
/*    */       catch (InstantiationException e) {
/* 30 */         e.printStackTrace();
/*    */       }
/*    */       catch (IllegalAccessException e) {
/* 33 */         e.printStackTrace();
/*    */       }
/*    */       catch (ClassNotFoundException e) {
/* 36 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 39 */     return this.connectorHelpers;
/*    */   }
/*    */ 
/*    */   public static final IConnectorManager getInstance() {
/* 43 */     if (instance == null) {
/* 44 */       instance = new ConnectorManager();
/*    */     }
/* 46 */     return instance;
/*    */   }
/*    */ 
/*    */   public Object get(IConnectorManager.ConnectorKeys key)
/*    */   {
/* 51 */     Object result = null;
/* 52 */     switch (1.$SwitchMap$com$dukascopy$api$connector$IConnectorManager$ConnectorKeys[key.ordinal()]) {
/*    */     case 1:
/* 54 */       result = getConnectorHelpers().getConnectorInstance();
/* 55 */       break;
/*    */     case 2:
/* 58 */       result = getConnectorHelpers();
/* 59 */       break;
/*    */     case 3:
/* 62 */       result = getConnectorHelpers().getCharts();
/* 63 */       break;
/*    */     case 4:
/* 66 */       result = getConnectorHelpers().getComboBoxModel();
/*    */     }
/*    */ 
/* 69 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.ConnectorManager
 * JD-Core Version:    0.6.0
 */