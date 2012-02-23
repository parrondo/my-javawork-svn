/*     */ package com.dukascopy.dds2.greed.mt.common;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.mt.AgentManager;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ public class Request
/*     */ {
/*  38 */   private static Logger log = LoggerFactory.getLogger(Request.class.getName());
/*     */ 
/* 182 */   private String methodName = null;
/*     */ 
/* 188 */   private Class[] parameterTypes = new Class[11];
/*     */ 
/* 194 */   private Object[] parameterValues = new Object[11];
/*     */ 
/*     */   public Request(byte[] bs)
/*     */   {
/*     */     try
/*     */     {
/*  42 */       String s = new String(bs);
/*  43 */       ByteArrayInputStream byteOut = new ByteArrayInputStream(bs, 0, s.trim().length());
/*     */ 
/*  45 */       DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
/*     */ 
/*  47 */       DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
/*  48 */       Document doc = docBuilder.parse(byteOut);
/*     */ 
/*  50 */       Node idNode = doc.getElementsByTagName("id").item(0);
/*  51 */       Integer mtId = new Integer(idNode.getFirstChild().getTextContent().trim());
/*     */ 
/*  53 */       Node method = doc.getElementsByTagName("method").item(0);
/*  54 */       this.methodName = method.getFirstChild().getTextContent().trim();
/*     */ 
/*  56 */       Node version = doc.getElementsByTagName("version").item(0);
/*  57 */       int ver = Integer.parseInt(version.getFirstChild().getTextContent());
/*     */ 
/*  60 */       NodeList params = doc.getElementsByTagName("param");
/*     */ 
/*  62 */       if (ver != 1) {
/*  63 */         log.error(LocalizationManager.getText("ERR_OLD_VERSION_MSG"));
/*  64 */         AgentManager agent = (AgentManager)GreedContext.get("ddsAgent");
/*  65 */         if (agent != null) {
/*  66 */           agent.setError(mtId, 5, "ERR_OLD_VERSION_MSG");
/*     */         }
/*  68 */         return;
/*     */       }
/*     */ 
/*  71 */       List listTypes = new ArrayList();
/*  72 */       List listValues = new ArrayList();
/*     */ 
/*  74 */       for (int i = 0; i < params.getLength(); i++) {
/*  75 */         Element node = (Element)params.item(i);
/*  76 */         int paramType = 0;
/*  77 */         if (node.getElementsByTagName("type").item(0).getFirstChild() != null) {
/*  78 */           paramType = Integer.parseInt(node.getElementsByTagName("type").item(0).getFirstChild().getTextContent().trim());
/*     */         }
/*     */ 
/*  83 */         String paramValue = "";
/*  84 */         if (node.getElementsByTagName("value").item(0).getFirstChild() != null) {
/*  85 */           paramValue = node.getElementsByTagName("value").item(0).getFirstChild().getTextContent().trim();
/*     */         }
/*     */ 
/*  88 */         switch (paramType) {
/*     */         case 0:
/*  90 */           break;
/*     */         case 1:
/*  92 */           listTypes.add(String.class);
/*  93 */           listValues.add(paramValue);
/*  94 */           break;
/*     */         case 2:
/*  96 */           listTypes.add(Double.TYPE);
/*  97 */           if (paramValue.isEmpty()) {
/*  98 */             paramValue = "0";
/*     */           }
/* 100 */           listValues.add(Double.valueOf(Double.parseDouble(paramValue)));
/* 101 */           break;
/*     */         case 3:
/* 103 */           listTypes.add(Long.TYPE);
/* 104 */           if (paramValue.isEmpty()) {
/* 105 */             paramValue = "0";
/*     */           }
/* 107 */           listValues.add(Long.valueOf(Long.parseLong(paramValue)));
/* 108 */           break;
/*     */         case 4:
/* 110 */           listTypes.add(Integer.TYPE);
/* 111 */           if (paramValue.isEmpty()) {
/* 112 */             paramValue = "0";
/*     */           }
/* 114 */           listValues.add(Integer.valueOf(Integer.parseInt(paramValue)));
/*     */         }
/*     */       }
/*     */ 
/* 118 */       this.parameterTypes = ((Class[])listTypes.toArray(new Class[0]));
/* 119 */       this.parameterValues = listValues.toArray(new Object[0]);
/*     */     }
/*     */     catch (IOException e) {
/* 122 */       e.printStackTrace();
/*     */     } catch (SAXParseException err) {
/* 124 */       err.printStackTrace();
/*     */     } catch (SAXException saxe) {
/* 126 */       saxe.printStackTrace();
/*     */     } catch (NumberFormatException nfe) {
/* 128 */       nfe.printStackTrace();
/*     */     } catch (Throwable t) {
/* 130 */       t.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private double readDouble(DataInputStream dataInputStream, int size) {
/* 135 */     byte[] readbuff = new byte[size];
/* 136 */     String s = "0";
/*     */     try {
/* 138 */       dataInputStream.read(readbuff);
/* 139 */       s = new String(readbuff).trim();
/*     */     } catch (IOException e) {
/* 141 */       e.printStackTrace();
/*     */     }
/* 143 */     return Double.valueOf(s).doubleValue();
/*     */   }
/*     */ 
/*     */   private long readLong(DataInputStream dataInputStream, int size) {
/* 147 */     byte[] data = new byte[8];
/* 148 */     byte[] readbuff = new byte[size];
/*     */     try {
/* 150 */       dataInputStream.read(readbuff);
/* 151 */       for (int i = 0; i < readbuff.length; i++)
/* 152 */         data[i] = readbuff[i];
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 156 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 159 */     return (data[7] << 56) + ((data[6] & 0xFF) << 48) + ((data[5] & 0xFF) << 40) + ((data[4] & 0xFF) << 32) + ((data[3] & 0xFF) << 24) + ((data[2] & 0xFF) << 16) + ((data[1] & 0xFF) << 8) + ((data[0] & 0xFF) << 0);
/*     */   }
/*     */ 
/*     */   private int readInt(DataInputStream dataInputStream, int size)
/*     */   {
/* 167 */     byte[] data = new byte[8];
/* 168 */     byte[] readbuff = new byte[size];
/*     */     try {
/* 170 */       dataInputStream.read(readbuff);
/* 171 */       for (int i = 0; i < readbuff.length; i++)
/* 172 */         data[i] = readbuff[i];
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 176 */       e.printStackTrace();
/*     */     }
/* 178 */     return ((data[3] & 0xFF) << 24) + ((data[2] & 0xFF) << 16) + ((data[1] & 0xFF) << 8) + ((data[0] & 0xFF) << 0);
/*     */   }
/*     */ 
/*     */   public String getMethodName()
/*     */   {
/* 185 */     return this.methodName;
/*     */   }
/*     */ 
/*     */   public Class[] getParameterTypes()
/*     */   {
/* 191 */     return this.parameterTypes;
/*     */   }
/*     */ 
/*     */   public Object[] getParameters()
/*     */   {
/* 197 */     return this.parameterValues;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.common.Request
 * JD-Core Version:    0.6.0
 */