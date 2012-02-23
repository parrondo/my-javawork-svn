/*    */ package com.dukascopy.dds2.greed.gui.component.dialog.reports;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.PriceAmountTextField;
/*    */ import com.dukascopy.transport.common.msg.request.ReportParameter;
/*    */ import java.awt.Font;
/*    */ 
/*    */ public class ReportParameterEditNumberField extends PriceAmountTextField
/*    */   implements ReportParamDetailsInf
/*    */ {
/*    */   private ReportParameter param;
/*    */ 
/*    */   public ReportParameterEditNumberField(ReportParameter param, Font font)
/*    */   {
/* 19 */     super(20);
/* 20 */     setFont(font);
/* 21 */     this.param = param;
/*    */   }
/*    */ 
/*    */   public String getKey() {
/* 25 */     return this.param.getKey();
/*    */   }
/*    */ 
/*    */   public String getValue() {
/* 29 */     return getText();
/*    */   }
/*    */ 
/*    */   public String getUrlString() {
/* 33 */     return getKey() + "=" + getValue();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dialog.reports.ReportParameterEditNumberField
 * JD-Core Version:    0.6.0
 */