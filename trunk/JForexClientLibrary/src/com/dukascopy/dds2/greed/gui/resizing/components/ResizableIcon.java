/*    */ package com.dukascopy.dds2.greed.gui.resizing.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.Resizable;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager;
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.SizeMode;
/*    */ import javax.swing.ImageIcon;
/*    */ 
/*    */ public class ResizableIcon extends ImageIcon
/*    */   implements Resizable
/*    */ {
/*    */   private static final String SMALL_PATH = "rc/media/";
/*    */   private static final String DEFAULT_ICON = "systray_question_mark.png";
/*    */   private final String imageName;
/*    */ 
/*    */   public ResizableIcon()
/*    */   {
/* 31 */     this.imageName = "";
/* 32 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   public ResizableIcon(String imageName)
/*    */   {
/* 37 */     this.imageName = imageName;
/* 38 */     ResizingManager.addResizable(this);
/*    */   }
/*    */ 
/*    */   public Object getDefaultSize()
/*    */   {
/* 44 */     return ResizingManager.SizeMode.SMALL;
/*    */   }
/*    */ 
/*    */   public void setSizeMode(Object object)
/*    */   {
/* 49 */     setImage(StratUtils.loadImage(getImagePath()));
/*    */   }
/*    */ 
/*    */   private String getImagePath()
/*    */   {
/* 58 */     StringBuffer resultPath = new StringBuffer(getSizeModesPath());
/*    */ 
/* 60 */     if (this.imageName != null) {
/* 61 */       resultPath.append(this.imageName);
/*    */     }
/*    */ 
/* 65 */     return resultPath.toString();
/*    */   }
/*    */ 
/*    */   private String getSizeModesPath()
/*    */   {
/* 77 */     return "rc/media/";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon
 * JD-Core Version:    0.6.0
 */