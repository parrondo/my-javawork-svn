/*     */ package com.dukascopy.dds2.greed.agent;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AgentServerRunnable
/*     */   implements Runnable
/*     */ {
/*  25 */   private static final Logger LOGGER = LoggerFactory.getLogger(AgentServerRunnable.class);
/*     */ 
/*  27 */   protected DatagramSocket socket = null;
/*     */ 
/*  29 */   private DDSAgent agent = null;
/*     */ 
/*  31 */   private InetAddress localhost = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
/*     */ 
/*     */   public AgentServerRunnable(DDSAgent agent)
/*     */     throws IOException
/*     */   {
/*  38 */     this.socket = new DatagramSocket(7000);
/*  39 */     this.agent = agent;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     while (true)
/*     */       try
/*     */       {
/*  48 */         byte[] buf = new byte[119];
/*     */ 
/*  51 */         DatagramPacket packet = new DatagramPacket(buf, buf.length);
/*  52 */         this.socket.receive(packet);
/*     */ 
/*  55 */         InetAddress address = packet.getAddress();
/*  56 */         if (!address.equals(this.localhost))
/*     */           continue;
/*  58 */         AgentRequest request = new AgentRequest(packet.getData());
/*     */ 
/*  62 */         Future future = this.agent.executeTask(new Callable(request)
/*     */         {
/*     */           public AgentResponse call() throws Exception {
/*  65 */             AgentResponse response = AgentServerRunnable.this.process(this.val$request);
/*  66 */             return response;
/*     */           }
/*     */         });
/*  70 */         AgentResponse response = (AgentResponse)future.get();
/*  71 */         if (response == null) {
/*  72 */           response = new AgentResponse(-99);
/*     */         }
/*  74 */         buf = response.getBytes();
/*     */ 
/*  77 */         int port = packet.getPort();
/*  78 */         packet = new DatagramPacket(buf, buf.length, address, port);
/*  79 */         this.socket.send(packet);
/*  80 */         continue;
/*  81 */         LOGGER.warn("agent request rejected from:" + address);
/*     */ 
/*  86 */         continue;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  85 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public AgentResponse process(AgentRequest request)
/*     */   {
/*  96 */     AgentResponse response = null;
/*     */     try
/*     */     {
/*  99 */       Method method = this.agent.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
/*     */ 
/* 101 */       Object o = method.invoke(this.agent, request.getParameters());
/* 102 */       response = new AgentResponse(0);
/* 103 */       if ((o instanceof Long))
/* 104 */         response.setLongValue(((Long)o).longValue());
/* 105 */       else if ((o instanceof Double))
/* 106 */         response.setDoubleValue(((Double)o).doubleValue());
/*     */       else {
/* 108 */         response.setIntValue(((Integer)o).intValue());
/*     */       }
/*     */     }
/*     */     catch (InvocationTargetException te)
/*     */     {
/* 113 */       LOGGER.error(te.getMessage(), te);
/* 114 */       Throwable ee = te.getCause();
/* 115 */       if ((ee instanceof AgentException))
/* 116 */         return new AgentResponse(ee);
/*     */     }
/*     */     catch (Exception e) {
/* 119 */       LOGGER.error(e.getMessage(), e);
/* 120 */       return new AgentResponse(e);
/*     */     }
/*     */ 
/* 123 */     return response;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.AgentServerRunnable
 * JD-Core Version:    0.6.0
 */