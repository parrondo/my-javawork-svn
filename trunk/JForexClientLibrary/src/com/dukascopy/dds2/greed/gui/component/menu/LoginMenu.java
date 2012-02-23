/*    */ package com.dukascopy.dds2.greed.gui.component.menu;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*    */ import javax.swing.JMenu;
/*    */ import javax.swing.JMenuBar;
/*    */ 
/*    */ public class LoginMenu extends JMenuBar
/*    */ {
/*    */   public LoginMenu()
/*    */   {
/* 19 */     build();
/*    */   }
/*    */ 
/*    */   private void build() {
/* 23 */     JMenu options = new JLocalizableMenu("item.login.options");
/*    */ 
/* 25 */     JLocalizableMenuItem helpAbout = new JLocalizableMenuItem("item.login.about");
/* 26 */     helpAbout.setAction(new AboutThisApplicationMenuAction("item.login.about"));
/* 27 */     options.add(helpAbout);
/*    */ 
/* 29 */     add(options);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.menu.LoginMenu
 * JD-Core Version:    0.6.0
 */