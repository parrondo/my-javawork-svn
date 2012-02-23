/*    */ package com.dukascopy.dds2.greed.config;
/*    */ 
/*    */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import java.util.Properties;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class GreedProperties extends Properties
/*    */ {
/* 12 */   private static Logger LOGGER = LoggerFactory.getLogger(GreedProperties.class);
/*    */   private static GreedProperties instance;
/*    */   private final String username;
/*    */   private final String ticket;
/*    */   private final String instanceId;
/*    */ 
/*    */   public static GreedProperties getInstance(String username, String ticket, String instanceId)
/*    */   {
/* 21 */     if (instance == null) {
/* 22 */       instance = new GreedProperties(username, ticket, instanceId);
/*    */     }
/* 24 */     return instance;
/*    */   }
/*    */ 
/*    */   private GreedProperties(String username, String ticket, String instanceId)
/*    */   {
/* 29 */     this.username = username;
/* 30 */     this.ticket = ticket;
/* 31 */     this.instanceId = instanceId;
/* 32 */     init();
/*    */   }
/*    */ 
/*    */   private void init() {
/* 36 */     Properties properties = loadProperties();
/* 37 */     if (properties != null)
/* 38 */       putAll(properties);
/*    */   }
/*    */ 
/*    */   private Properties loadProperties()
/*    */   {
/*    */     try {
/* 44 */       return GreedContext.AUTHORIZATION_CLIENT.getAllProperties(this.username, this.ticket, this.instanceId);
/*    */     } catch (Exception ex) {
/* 46 */       LOGGER.error("Error while loading properties", ex);
/*    */     }
/* 48 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.config.GreedProperties
 * JD-Core Version:    0.6.0
 */