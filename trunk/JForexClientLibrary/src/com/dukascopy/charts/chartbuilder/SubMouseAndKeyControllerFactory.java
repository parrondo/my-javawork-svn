/*    */ package com.dukascopy.charts.chartbuilder;
/*    */ 
/*    */ import com.dukascopy.charts.drawings.NewDrawingsCoordinator;
/*    */ import com.dukascopy.charts.drawings.PopupManagerForDrawings;
/*    */ import com.dukascopy.charts.orders.OrdersManagerImpl;
/*    */ import com.dukascopy.charts.view.displayabledatapart.IDrawingsManagerContainer;
/*    */ import java.util.LinkedHashMap;
/*    */ 
/*    */ public class SubMouseAndKeyControllerFactory
/*    */ {
/*    */   final GuiRefresher guiRefresher;
/*    */   final IDrawingsManagerContainer drawingsManagerContainer;
/*    */   final NewDrawingsCoordinator newDrawingsCoordinator;
/*    */   final PopupManagerForDrawings popupManagerForDrawings;
/*    */   private final OrdersManagerImpl ordersManagerImpl;
/*    */ 
/*    */   public SubMouseAndKeyControllerFactory(GuiRefresher guiRefresher, IDrawingsManagerContainer drawingsManagerContainer, NewDrawingsCoordinator newDrawingsCoordinator, PopupManagerForDrawings popupManagerForDrawings, OrdersManagerImpl ordersManagerImpl)
/*    */   {
/* 26 */     this.guiRefresher = guiRefresher;
/* 27 */     this.drawingsManagerContainer = drawingsManagerContainer;
/* 28 */     this.newDrawingsCoordinator = newDrawingsCoordinator;
/* 29 */     this.popupManagerForDrawings = popupManagerForDrawings;
/* 30 */     this.ordersManagerImpl = ordersManagerImpl;
/*    */   }
/*    */ 
/*    */   public SubDrawingsMouseAndKeyController createSubDrawingsMouseAndKeyController(SubIndicatorGroup subIndicatorGroup)
/*    */   {
/* 35 */     LinkedHashMap subDrawingsManagersMap = this.drawingsManagerContainer.getSubDrawingsManagers(subIndicatorGroup.getSubWindowId());
/* 36 */     return new SubDrawingsMouseAndKeyControllerImpl(this.guiRefresher, new SubDrawingsManagersContainer(subDrawingsManagersMap.values()), this.newDrawingsCoordinator, this.popupManagerForDrawings, this.ordersManagerImpl, this.drawingsManagerContainer.getMainDrawingsManager());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.SubMouseAndKeyControllerFactory
 * JD-Core Version:    0.6.0
 */