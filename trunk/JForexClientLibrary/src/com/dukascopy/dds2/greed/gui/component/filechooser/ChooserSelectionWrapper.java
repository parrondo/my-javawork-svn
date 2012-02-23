/*    */ package com.dukascopy.dds2.greed.gui.component.filechooser;
/*    */ 
/*    */ import com.dukascopy.transport.common.datafeed.Location;
/*    */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class ChooserSelectionWrapper
/*    */ {
/*    */   private List<FileItem> fileItems;
/*    */   private Location location;
/*    */ 
/*    */   public FileItem getFileItem()
/*    */   {
/* 15 */     return (FileItem)this.fileItems.get(0);
/*    */   }
/*    */ 
/*    */   public List<FileItem> getFileItems() {
/* 19 */     return this.fileItems;
/*    */   }
/*    */ 
/*    */   public Location getLocation() {
/* 23 */     return this.location;
/*    */   }
/*    */ 
/*    */   public ChooserSelectionWrapper(FileItem fileItem, Location location)
/*    */   {
/* 28 */     this.fileItems = new ArrayList();
/* 29 */     this.fileItems.add(fileItem);
/*    */ 
/* 31 */     this.location = location;
/*    */   }
/*    */ 
/*    */   public ChooserSelectionWrapper(List<FileItem> fileItems, Location location) {
/* 35 */     this.fileItems = fileItems;
/* 36 */     this.location = location;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper
 * JD-Core Version:    0.6.0
 */