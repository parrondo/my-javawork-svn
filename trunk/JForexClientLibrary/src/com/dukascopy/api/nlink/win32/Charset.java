/*   */ package com.dukascopy.api.nlink.win32;
/*   */ 
/*   */ public enum Charset
/*   */ {
/* 4 */   ANSI, UNICODE, AUTO;
/*   */ 
/*   */   private int intval;
/*   */ 
/* 8 */   public int getIntCode() { return this.intval;
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.Charset
 * JD-Core Version:    0.6.0
 */