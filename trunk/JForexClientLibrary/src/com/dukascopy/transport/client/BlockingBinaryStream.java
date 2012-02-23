/*     */ package com.dukascopy.transport.client;
/*     */ 
/*     */ import com.dukascopy.transport.common.msg.BinaryPartMessage;
/*     */ import com.dukascopy.transport.common.msg.StreamingStatus;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import org.apache.mina.common.IoSession;
/*     */ 
/*     */ public class BlockingBinaryStream extends InputStream
/*     */ {
/*     */   private static final int DEFAULT_TIMEOUT = 15000;
/*     */   private static final int DEFAULT_BUFFER_SIZE = 2097152;
/*  18 */   boolean closedByWriter = false;
/*  19 */   volatile boolean closedByReader = false;
/*     */   protected byte[] buffer;
/*  23 */   protected int in = -1;
/*     */ 
/*  25 */   protected int out = 0;
/*     */ 
/*  27 */   protected int ioTimeout = 15000;
/*     */   private IoSession session;
/*     */   private String streamId;
/*     */   private boolean ack_delayed;
/*     */   private boolean streamHandled;
/*     */   private boolean ioTerminate;
/*     */   private String errorMessage;
/*     */ 
/*     */   public BlockingBinaryStream(String streamId, IoSession session)
/*     */   {
/*  42 */     this(streamId, session, 2097152);
/*     */   }
/*     */ 
/*     */   public BlockingBinaryStream(String streamId, IoSession session, int bufferSize) {
/*  46 */     this(streamId, session, 2097152, 15000);
/*     */   }
/*     */ 
/*     */   public BlockingBinaryStream(String streamId, IoSession session, int bufferSize, int ioTimeout) {
/*  50 */     this.streamId = streamId;
/*  51 */     this.session = session;
/*  52 */     initBuffer(bufferSize);
/*  53 */     this.ioTimeout = ioTimeout;
/*     */   }
/*     */ 
/*     */   private void initBuffer(int bufferSize) {
/*  57 */     if (bufferSize <= 0) {
/*  58 */       throw new IllegalArgumentException("Buffer Size <= 0");
/*     */     }
/*  60 */     this.buffer = new byte[bufferSize];
/*     */   }
/*     */ 
/*     */   public synchronized void setIOTimeout(int ioTimeout) {
/*  64 */     this.ioTimeout = ioTimeout;
/*     */   }
/*     */ 
/*     */   public int getIOTimeout() {
/*  68 */     return this.ioTimeout;
/*     */   }
/*     */ 
/*     */   public synchronized boolean isClosed() {
/*  72 */     return (this.closedByWriter) || (this.closedByReader);
/*     */   }
/*     */ 
/*     */   protected synchronized void ioTerminate(String message) {
/*  76 */     this.ioTerminate = true;
/*  77 */     this.errorMessage = message;
/*  78 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized void binaryPartReceived(BinaryPartMessage msg) throws IOException {
/*  82 */     byte[] received = msg.getData();
/*     */ 
/*  84 */     if (received.length > 0) {
/*  85 */       receive(received, 0, received.length);
/*     */     }
/*  87 */     if (msg.isEOF()) {
/*  88 */       receivedLast();
/*     */     }
/*     */ 
/*  91 */     if (this.buffer.length - available() > this.buffer.length - received.length * 2) {
/*  92 */       this.ack_delayed = false;
/*  93 */       StreamingStatus ss = new StreamingStatus(msg.getStreamId(), "s_ack");
/*  94 */       this.session.write(ss);
/*     */     } else {
/*  96 */       this.ack_delayed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void receive(byte[] b, int off, int len) throws IOException {
/* 101 */     checkState();
/* 102 */     int bytesToTransfer = len;
/* 103 */     while (bytesToTransfer > 0) {
/* 104 */       if (this.in == this.out)
/* 105 */         awaitSpace();
/* 106 */       int nextTransferAmount = 0;
/* 107 */       if (this.out < this.in)
/* 108 */         nextTransferAmount = this.buffer.length - this.in;
/* 109 */       else if (this.in < this.out) {
/* 110 */         if (this.in == -1) {
/* 111 */           this.in = (this.out = 0);
/* 112 */           nextTransferAmount = this.buffer.length - this.in;
/*     */         } else {
/* 114 */           nextTransferAmount = this.out - this.in;
/*     */         }
/*     */       }
/* 117 */       if (nextTransferAmount > bytesToTransfer)
/* 118 */         nextTransferAmount = bytesToTransfer;
/* 119 */       assert (nextTransferAmount > 0);
/* 120 */       System.arraycopy(b, off, this.buffer, this.in, nextTransferAmount);
/* 121 */       bytesToTransfer -= nextTransferAmount;
/* 122 */       off += nextTransferAmount;
/* 123 */       this.in += nextTransferAmount;
/* 124 */       if (this.in >= this.buffer.length)
/* 125 */         this.in = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkState() throws IOException
/*     */   {
/* 131 */     if ((this.closedByWriter) || (this.closedByReader))
/* 132 */       throw new IOException("Stream closed");
/*     */   }
/*     */ 
/*     */   private void awaitSpace() throws IOException
/*     */   {
/* 137 */     while (this.in == this.out) {
/* 138 */       checkState();
/*     */ 
/* 141 */       notifyAll();
/*     */       try {
/* 143 */         wait(1000L);
/*     */       } catch (InterruptedException ex) {
/* 145 */         throw new InterruptedIOException();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void receivedLast() {
/* 151 */     this.closedByWriter = true;
/* 152 */     notifyAll();
/*     */   }
/*     */ 
/*     */   public synchronized boolean isEOF() {
/* 156 */     return (this.in < 0) && (this.closedByWriter);
/*     */   }
/*     */ 
/*     */   public synchronized int read() throws IOException {
/* 160 */     checkStartTransfer();
/* 161 */     checkDelayedAck();
/*     */ 
/* 163 */     if (this.closedByReader) {
/* 164 */       throw new IOException("Stream closed");
/*     */     }
/* 166 */     if (this.ioTerminate) {
/* 167 */       throw new IOException(this.errorMessage);
/*     */     }
/*     */ 
/* 170 */     int timeoutThisRead = this.ioTimeout;
/* 171 */     while (this.in < 0) {
/* 172 */       if (this.closedByWriter)
/*     */       {
/* 174 */         checkDelayedAck();
/* 175 */         return -1;
/*     */       }
/* 177 */       timeoutThisRead -= 50;
/* 178 */       if (timeoutThisRead < 0) {
/* 179 */         throw new IOException("Timeout while receiving data");
/*     */       }
/*     */ 
/* 182 */       notifyAll();
/*     */       try {
/* 184 */         wait(50L);
/*     */       } catch (InterruptedException ex) {
/* 186 */         throw new InterruptedIOException();
/*     */       }
/*     */     }
/* 189 */     int ret = this.buffer[(this.out++)] & 0xFF;
/* 190 */     if (this.out >= this.buffer.length) {
/* 191 */       this.out = 0;
/*     */     }
/* 193 */     if (this.in == this.out)
/*     */     {
/* 195 */       this.in = -1;
/*     */     }
/*     */ 
/* 198 */     return ret;
/*     */   }
/*     */ 
/*     */   public synchronized int read(byte[] b, int off, int len) throws IOException {
/* 202 */     if (b == null)
/* 203 */       throw new NullPointerException();
/* 204 */     if ((off < 0) || (len < 0) || (len > b.length - off))
/* 205 */       throw new IndexOutOfBoundsException();
/* 206 */     if (len == 0) {
/* 207 */       return 0;
/*     */     }
/*     */ 
/* 211 */     int c = read();
/* 212 */     if (c < 0) {
/* 213 */       return -1;
/*     */     }
/* 215 */     b[off] = (byte)c;
/* 216 */     int rlen = 1;
/* 217 */     while ((this.in >= 0) && (len > 1))
/*     */     {
/*     */       int available;
/*     */       int available;
/* 221 */       if (this.in > this.out)
/* 222 */         available = Math.min(this.buffer.length - this.out, this.in - this.out);
/*     */       else {
/* 224 */         available = this.buffer.length - this.out;
/*     */       }
/*     */ 
/* 228 */       if (available > len - 1) {
/* 229 */         available = len - 1;
/*     */       }
/* 231 */       System.arraycopy(this.buffer, this.out, b, off + rlen, available);
/* 232 */       this.out += available;
/* 233 */       rlen += available;
/* 234 */       len -= available;
/*     */ 
/* 236 */       if (this.out >= this.buffer.length) {
/* 237 */         this.out = 0;
/*     */       }
/* 239 */       if (this.in == this.out)
/*     */       {
/* 241 */         this.in = -1;
/*     */       }
/*     */     }
/* 244 */     return rlen;
/*     */   }
/*     */ 
/*     */   private synchronized void checkStartTransfer() {
/* 248 */     if (!this.streamHandled) {
/* 249 */       this.streamHandled = true;
/* 250 */       StreamingStatus ss = new StreamingStatus(this.streamId, "s_ack");
/* 251 */       this.session.write(ss);
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void checkDelayedAck() {
/* 256 */     if ((this.ack_delayed) && (this.buffer.length - available() > 0)) {
/* 257 */       this.ack_delayed = false;
/* 258 */       StreamingStatus ss = new StreamingStatus(this.streamId, "s_ack");
/* 259 */       this.session.write(ss);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized int available() {
/* 264 */     if (this.in < 0)
/* 265 */       return 0;
/* 266 */     if (this.in == this.out)
/* 267 */       return this.buffer.length;
/* 268 */     if (this.in > this.out) {
/* 269 */       return this.in - this.out;
/*     */     }
/* 271 */     return this.in + this.buffer.length - this.out;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 275 */     this.closedByReader = true;
/* 276 */     synchronized (this) {
/* 277 */       this.in = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getStreamId() {
/* 282 */     return this.streamId;
/*     */   }
/*     */ 
/*     */   public void setStreamId(String streamId) {
/* 286 */     this.streamId = streamId;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.BlockingBinaryStream
 * JD-Core Version:    0.6.0
 */