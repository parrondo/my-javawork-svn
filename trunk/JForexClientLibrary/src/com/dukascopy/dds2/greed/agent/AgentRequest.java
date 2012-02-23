/*     */ package com.dukascopy.dds2.greed.agent;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AgentRequest
/*     */ {
/*  21 */   private static final Logger LOGGER = LoggerFactory.getLogger(AgentRequest.class);
/*     */ 
/* 106 */   private String methodName = null;
/*     */ 
/* 112 */   private Class[] parameterTypes = new Class[8];
/*     */ 
/* 118 */   private Object[] parameterValues = new Object[8];
/*     */ 
/*     */   public AgentRequest(byte[] bs)
/*     */   {
/*     */     try
/*     */     {
/*  25 */       DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bs));
/*  26 */       byte[] agentName = new byte[6];
/*  27 */       dataInputStream.read(agentName);
/*  28 */       byte version = dataInputStream.readByte();
/*  29 */       if (version != 1)
/*     */       {
/*  31 */         return;
/*     */       }
/*  33 */       byte[] methodNameBytes = new byte[40];
/*  34 */       dataInputStream.read(methodNameBytes);
/*  35 */       this.methodName = new String(methodNameBytes).trim();
/*     */ 
/*  37 */       List listTypes = new ArrayList();
/*  38 */       List listValues = new ArrayList();
/*     */ 
/*  40 */       for (int i = 0; i < 8; i++) {
/*  41 */         byte paramType = dataInputStream.readByte();
/*  42 */         byte[] paramValue = new byte[8];
/*     */ 
/*  44 */         switch (paramType)
/*     */         {
/*     */         case 0:
/*  48 */           break;
/*     */         case 1:
/*  52 */           listTypes.add(String.class);
/*  53 */           dataInputStream.read(paramValue);
/*  54 */           listValues.add(new String(paramValue).trim());
/*  55 */           break;
/*     */         case 2:
/*  58 */           listTypes.add(Double.TYPE);
/*  59 */           listValues.add(Double.valueOf(readDouble(dataInputStream)));
/*  60 */           break;
/*     */         case 3:
/*  63 */           listTypes.add(Long.TYPE);
/*  64 */           listValues.add(Long.valueOf(readLong(dataInputStream)));
/*  65 */           break;
/*     */         case 4:
/*  67 */           listTypes.add(Integer.TYPE);
/*  68 */           listValues.add(Integer.valueOf(readInt(dataInputStream)));
/*     */         }
/*     */       }
/*     */ 
/*  72 */       this.parameterTypes = ((Class[])listTypes.toArray(new Class[0]));
/*  73 */       this.parameterValues = listValues.toArray(new Object[0]);
/*     */     }
/*     */     catch (IOException e) {
/*  76 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private double readDouble(DataInputStream dataInputStream) {
/*  81 */     return Double.longBitsToDouble(readLong(dataInputStream));
/*     */   }
/*     */ 
/*     */   private long readLong(DataInputStream dataInputStream) {
/*  85 */     byte[] data = new byte[8];
/*     */     try {
/*  87 */       dataInputStream.read(data);
/*     */     } catch (IOException e) {
/*  89 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/*  92 */     return (data[7] << 56) + ((data[6] & 0xFF) << 48) + ((data[5] & 0xFF) << 40) + ((data[4] & 0xFF) << 32) + ((data[3] & 0xFF) << 24) + ((data[2] & 0xFF) << 16) + ((data[1] & 0xFF) << 8) + ((data[0] & 0xFF) << 0);
/*     */   }
/*     */ 
/*     */   private int readInt(DataInputStream dataInputStream)
/*     */   {
/*  97 */     byte[] data = new byte[8];
/*     */     try {
/*  99 */       dataInputStream.read(data);
/*     */     } catch (IOException e) {
/* 101 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 103 */     return ((data[3] & 0xFF) << 24) + ((data[2] & 0xFF) << 16) + ((data[1] & 0xFF) << 8) + ((data[0] & 0xFF) << 0);
/*     */   }
/*     */ 
/*     */   public String getMethodName()
/*     */   {
/* 109 */     return this.methodName;
/*     */   }
/*     */ 
/*     */   public Class[] getParameterTypes()
/*     */   {
/* 115 */     return this.parameterTypes;
/*     */   }
/*     */ 
/*     */   public Object[] getParameters()
/*     */   {
/* 121 */     return this.parameterValues;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.AgentRequest
 * JD-Core Version:    0.6.0
 */