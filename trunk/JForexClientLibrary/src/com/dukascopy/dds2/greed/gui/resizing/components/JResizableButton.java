/*    */ package com.dukascopy.dds2.greed.gui.resizing.components;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*    */ import javax.swing.Icon;
/*    */ import javax.swing.JButton;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class JResizableButton extends JButton
/*    */   implements IResizableButton
/*    */ {
/* 14 */   private static final Logger LOGGER = LoggerFactory.getLogger(JResizableButton.class);
/*    */   private final ResizingManager.ComponentSize defaultSize;
/* 17 */   private Icon inActiveIcon = null;
/*    */   private Icon activeIcon;
/*    */ 
/*    */   public JResizableButton()
/*    */   {
/* 21 */     this.defaultSize = ResizingManager.ComponentSize.TOLBAR_BTN_SIZE;
/*    */   }
/*    */ 
/*    */   public JResizableButton(ResizingManager.ComponentSize defaultSize)
/*    */   {
/* 26 */     this.defaultSize = defaultSize;
/* 27 */     applySize();
/*    */   }
/*    */ 
/*    */   public JResizableButton(Icon icon)
/*    */   {
/* 32 */     super(icon);
/* 33 */     this.activeIcon = icon;
/* 34 */     this.defaultSize = ResizingManager.ComponentSize.TOLBAR_BTN_SIZE;
/*    */   }
/*    */ 
/*    */   public JResizableButton(Icon icon, ResizingManager.ComponentSize defaultSize)
/*    */   {
/* 39 */     super(icon);
/* 40 */     this.activeIcon = icon;
/* 41 */     this.defaultSize = ResizingManager.ComponentSize.TOLBAR_BTN_SIZE;
/* 42 */     applySize();
/*    */   }
/*    */ 
/*    */   public Object getDefaultSize()
/*    */   {
/* 48 */     return this.defaultSize;
/*    */   }
/*    */ 
/*    */   public void setSizeMode(Object object)
/*    */   {
/* 53 */     applySize();
/*    */   }
/*    */ 
/*    */   private void applySize() {
/* 57 */     setMinimumSize(this.defaultSize.getSize());
/* 58 */     setMaximumSize(this.defaultSize.getSize());
/* 59 */     setPreferredSize(this.defaultSize.getSize());
/*    */   }
/*    */ 
/*    */   public void setActive(boolean isActive) {
/* 63 */     if ((this.inActiveIcon == null) && (this.activeIcon == null)) {
/* 64 */       LOGGER.error("Inactive and active icon's isn't defined for this object ");
/*    */     }
/*    */ 
/* 67 */     if (!isActive)
/* 68 */       setIcon(this.inActiveIcon);
/*    */     else
/* 70 */       setIcon(this.activeIcon);
/*    */   }
/*    */ 
/*    */   public void setInactiveIcon(Icon icon)
/*    */   {
/* 75 */     this.inActiveIcon = icon;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.resizing.components.JResizableButton
 * JD-Core Version:    0.6.0
 */