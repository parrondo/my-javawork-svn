/*    */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class LinePositionInputStream extends InputStream
/*    */ {
/*    */   private List fLinePositions;
/*    */   private InputStream fInputStream;
/*    */   private boolean fRRead;
/*    */   private boolean fAddLine;
/*    */   private int fCurrPosition;
/*    */ 
/*    */   public LinePositionInputStream(InputStream inputStream)
/*    */     throws IOException
/*    */   {
/* 33 */     this.fInputStream = inputStream;
/* 34 */     this.fLinePositions = new ArrayList(30);
/* 35 */     this.fAddLine = true;
/* 36 */     this.fRRead = false;
/* 37 */     this.fCurrPosition = 0;
/*    */   }
/*    */ 
/*    */   public int read() throws IOException {
/* 41 */     int ch = this.fInputStream.read();
/* 42 */     if ((this.fRRead) && (ch == 10)) {
/* 43 */       this.fRRead = false;
/*    */     } else {
/* 45 */       if (this.fAddLine) {
/* 46 */         this.fLinePositions.add(new Integer(this.fCurrPosition));
/* 47 */         this.fAddLine = false;
/*    */       }
/*    */ 
/* 50 */       if ((ch == 10) || (ch == 13)) {
/* 51 */         this.fAddLine = true;
/* 52 */         this.fRRead = (ch == 13);
/*    */       } else {
/* 54 */         this.fRRead = false;
/*    */       }
/*    */     }
/* 57 */     this.fCurrPosition += 1;
/* 58 */     return ch;
/*    */   }
/*    */ 
/*    */   public int getPosition(int line, int col) {
/* 62 */     line--;
/* 63 */     col--;
/* 64 */     if (line < this.fLinePositions.size()) {
/* 65 */       Integer lineStart = (Integer)this.fLinePositions.get(line);
/* 66 */       return lineStart.intValue() + col;
/*    */     }
/* 68 */     return -1;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.LinePositionInputStream
 * JD-Core Version:    0.6.0
 */