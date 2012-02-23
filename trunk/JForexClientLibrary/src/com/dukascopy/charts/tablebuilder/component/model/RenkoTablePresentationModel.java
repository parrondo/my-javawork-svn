/*    */ package com.dukascopy.charts.tablebuilder.component.model;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.renko.RenkoData;
/*    */ import com.dukascopy.charts.tablebuilder.component.column.RenkoColumnBean;
/*    */ 
/*    */ public class RenkoTablePresentationModel extends DataTablePresentationAbstractModel<RenkoColumnBean, RenkoData>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public RenkoTablePresentationModel()
/*    */   {
/* 15 */     super(RenkoColumnBean.class, RenkoData.class);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.model.RenkoTablePresentationModel
 * JD-Core Version:    0.6.0
 */