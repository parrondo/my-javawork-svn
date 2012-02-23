/*    */ package com.dukascopy.charts.tablebuilder.component.model;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.TickColumnBean;
/*    */ 
/*    */ public class TickDataTablePresentationModel extends DataTablePresentationAbstractModel<TickColumnBean, TickData>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public TickDataTablePresentationModel()
/*    */   {
/* 16 */     super(TickColumnBean.class, TickData.class);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.model.TickDataTablePresentationModel
 * JD-Core Version:    0.6.0
 */