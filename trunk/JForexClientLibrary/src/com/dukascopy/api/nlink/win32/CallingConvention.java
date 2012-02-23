/*   */ package com.dukascopy.api.nlink.win32;
/*   */ 
/*   */ public enum CallingConvention
/*   */ {
/* 4 */   STDCALL, CDECL, THISCALL;
/*   */ 
/*   */   private int intval;
/*   */ 
/* 9 */   public int getIntCode() { return this.intval;
/*   */   }
/*   */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.CallingConvention
 * JD-Core Version:    0.6.0
 */