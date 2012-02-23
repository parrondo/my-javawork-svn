/*     */ package com.dukascopy.transport.common.msg;
/*     */ 
/*     */ import com.dukascopy.transport.common.mina.Base64Encoder;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.zip.DataFormatException;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.Inflater;
/*     */ 
/*     */ public class MarketBunch extends ProtocolMessage
/*     */ {
/*     */   public static final String TYPE = "cmbunch";
/*     */   public static final String DATA = "d";
/*  25 */   private Map<String, InstrumentMarketBunch> instrumentBunchMap = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   public MarketBunch()
/*     */   {
/*  31 */     setType("cmbunch");
/*     */   }
/*     */ 
/*     */   public MarketBunch(ProtocolMessage message)
/*     */   {
/*  38 */     super(message);
/*  39 */     setType("cmbunch");
/*  40 */     put("d", message.getString("d"));
/*  41 */     if (message.getString("d") != null)
/*  42 */       parseData(message.getString("d"));
/*     */   }
/*     */ 
/*     */   public Map<String, InstrumentMarketBunch> getInstrumentBunchMap()
/*     */   {
/*  50 */     return this.instrumentBunchMap;
/*     */   }
/*     */ 
/*     */   public void setInstrumentBunchMap(Map<String, InstrumentMarketBunch> instrumentBunch)
/*     */   {
/*  59 */     this.instrumentBunchMap = instrumentBunch;
/*     */   }
/*     */ 
/*     */   public InstrumentMarketBunch getInstrumentBunch(String instrument)
/*     */   {
/*  67 */     return (InstrumentMarketBunch)this.instrumentBunchMap.get(instrument);
/*     */   }
/*     */ 
/*     */   public Set<String> getInstruments()
/*     */   {
/*  75 */     return this.instrumentBunchMap.keySet();
/*     */   }
/*     */ 
/*     */   public void addInstrumentBunch(String instrument, InstrumentMarketBunch instrumentBunch)
/*     */   {
/*  83 */     synchronized (this.instrumentBunchMap) {
/*  84 */       this.instrumentBunchMap.put(instrument, instrumentBunch);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void parseData(String encodedData) {
/*  89 */     if (encodedData != null) {
/*  90 */       byte[] data = Base64Encoder.decode(encodedData);
/*  91 */       byte[] d = decompressData(data);
/*     */       try {
/*  93 */         ObjectInputStream dis = new ObjectInputStream(new ByteArrayInputStream(d));
/*  94 */         String instrument = (String)dis.readObject();
/*  95 */         while (instrument != null) {
/*  96 */           InstrumentMarketBunch bunch = (InstrumentMarketBunch)dis.readObject();
/*  97 */           this.instrumentBunchMap.put(instrument, bunch);
/*  98 */           instrument = (String)dis.readObject();
/*     */         }
/*     */       } catch (Exception e) {
/* 101 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toProtocolString()
/*     */   {
/*     */     try
/*     */     {
/* 115 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 116 */       ObjectOutputStream dos = new ObjectOutputStream(baos);
/* 117 */       Map map = new HashMap();
/* 118 */       synchronized (this.instrumentBunchMap) {
/* 119 */         map.putAll(this.instrumentBunchMap);
/*     */       }
/* 121 */       for (String instrument : map.keySet()) {
/* 122 */         dos.writeObject(instrument);
/* 123 */         InstrumentMarketBunch bunch = (InstrumentMarketBunch)map.get(instrument);
/* 124 */         synchronized (bunch) {
/* 125 */           bunch = new InstrumentMarketBunch(bunch);
/*     */         }
/* 127 */         dos.writeObject(bunch);
/*     */       }
/* 129 */       dos.writeObject(null);
/* 130 */       dos.flush();
/* 131 */       byte[] bytes = baos.toByteArray();
/* 132 */       bytes = compressData(bytes, 1);
/* 133 */       String s = new String(Base64Encoder.encode(bytes));
/* 134 */       put("d", s);
/* 135 */       StringBuffer sb = new StringBuffer();
/* 136 */       sb.append(super.toString());
/* 137 */       return sb.toString();
/*     */     }
/*     */     catch (IOException e) {
/* 140 */       e.printStackTrace();
/*     */     }
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   private byte[] compressData(byte[] input, int compression)
/*     */   {
/* 151 */     Deflater compressor = new Deflater();
/* 152 */     compressor.setLevel(compression);
/* 153 */     compressor.setInput(input);
/* 154 */     compressor.finish();
/* 155 */     ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
/*     */ 
/* 158 */     byte[] buf = new byte[1024];
/* 159 */     while (!compressor.finished()) {
/* 160 */       int count = compressor.deflate(buf);
/* 161 */       bos.write(buf, 0, count);
/*     */     }
/*     */     try {
/* 164 */       bos.close();
/*     */     } catch (IOException e) {
/*     */     }
/* 167 */     return bos.toByteArray();
/*     */   }
/*     */ 
/*     */   private byte[] decompressData(byte[] input)
/*     */   {
/* 175 */     Inflater decompressor = new Inflater();
/* 176 */     decompressor.setInput(input);
/* 177 */     ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
/* 178 */     byte[] buf = new byte[1024];
/* 179 */     while (!decompressor.finished())
/*     */       try {
/* 181 */         int count = decompressor.inflate(buf);
/* 182 */         bos.write(buf, 0, count);
/*     */       } catch (DataFormatException e) {
/* 184 */         e.printStackTrace();
/* 185 */         return new byte[0];
/*     */       }
/*     */     try
/*     */     {
/* 189 */       bos.close();
/*     */     } catch (IOException e) {
/*     */     }
/* 192 */     byte[] decompressedData = bos.toByteArray();
/* 193 */     return decompressedData;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.msg.MarketBunch
 * JD-Core Version:    0.6.0
 */