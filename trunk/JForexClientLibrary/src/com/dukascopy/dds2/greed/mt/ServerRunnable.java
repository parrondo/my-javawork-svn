/*     */ package com.dukascopy.dds2.greed.mt;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.mt.common.IAgent;
/*     */ import com.dukascopy.dds2.greed.mt.common.Request;
/*     */ import com.dukascopy.dds2.greed.mt.common.Response;
/*     */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
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
/*     */ public class ServerRunnable
/*     */   implements Runnable
/*     */ {
/*  29 */   private static Logger log = LoggerFactory.getLogger(ServerRunnable.class.getName());
/*     */ 
/*  31 */   protected DatagramSocket socket = null;
/*  32 */   private InetAddress localhost = InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
/*     */   private IAgent agent;
/*     */ 
/*     */   public ServerRunnable()
/*     */     throws IOException
/*     */   {
/*  41 */     this.socket = new DatagramSocket(7000);
/*  42 */     this.agent = Agent.getInstance();
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     while (true)
/*     */       try {
/*  48 */         byte[] buf = new byte[4096];
/*     */ 
/*  50 */         DatagramPacket packet = new DatagramPacket(buf, buf.length);
/*  51 */         this.socket.receive(packet);
/*     */ 
/*  54 */         InetAddress address = packet.getAddress();
/*  55 */         if (!address.equals(this.localhost))
/*     */           continue;
/*  57 */         Request request = new Request(packet.getData());
/*     */ 
/*  61 */         Future future = this.agent.executeTask(new Callable(request)
/*     */         {
/*     */           public Response call() throws Exception {
/*  64 */             Response response = ServerRunnable.this.process(this.val$request);
/*  65 */             return response;
/*     */           }
/*     */         });
/*  69 */         Response response = (Response)future.get();
/*  70 */         if (response == null) {
/*  71 */           response = new Response(-99);
/*     */         }
/*  73 */         buf = response.getBytes();
/*     */ 
/*  76 */         int port = packet.getPort();
/*  77 */         packet = new DatagramPacket(buf, buf.length, address, port);
/*  78 */         this.socket.send(packet);
/*  79 */         continue;
/*  80 */         System.out.println("agent request rejected from:" + address);
/*     */ 
/*  85 */         continue;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  84 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   public Response process(Request request)
/*     */   {
/*  94 */     Response response = null;
/*     */     try
/*     */     {
/* 107 */       Method method = Agent.class.getMethod(request.getMethodName(), request.getParameterTypes());
/*     */ 
/* 109 */       Object o = method.invoke(this.agent, request.getParameters());
/* 110 */       response = new Response(0);
/* 111 */       if ((o instanceof String))
/* 112 */         response.setStringValue((String)o);
/* 113 */       else if ((o instanceof Long))
/* 114 */         response.setLongValue(((Long)o).longValue());
/* 115 */       else if ((o instanceof Double))
/* 116 */         response.setDoubleValue(((Double)o).doubleValue());
/* 117 */       else if ((o instanceof Boolean))
/* 118 */         response.setIntValue(((Boolean)o).booleanValue() ? 0 : 1);
/* 119 */       else if ((o instanceof Integer))
/* 120 */         response.setIntValue(((Integer)o).intValue());
/*     */       else {
/* 122 */         response.setVoidValue();
/*     */       }
/*     */     }
/*     */     catch (InvocationTargetException te)
/*     */     {
/* 127 */       te.printStackTrace();
/* 128 */       Throwable ee = te.getCause();
/* 129 */       if ((ee instanceof MTAgentException)) {
/* 130 */         return new Response(ee);
/*     */       }
/* 132 */       te.printStackTrace();
/*     */     }
/*     */     catch (Exception e) {
/* 135 */       e.printStackTrace();
/* 136 */       return new Response(e);
/*     */     }
/*     */ 
/* 139 */     return response;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.ServerRunnable
 * JD-Core Version:    0.6.0
 */