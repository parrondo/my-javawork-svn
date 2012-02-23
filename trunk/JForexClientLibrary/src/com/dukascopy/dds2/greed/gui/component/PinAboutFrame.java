/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Toolkit;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.BoxLayout;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ class PinAboutFrame extends BasicDecoratedFrame
/*    */ {
/*    */   private static PinAboutFrame singleInstance;
/*    */ 
/*    */   private PinAboutFrame()
/*    */   {
/* 23 */     build();
/*    */   }
/*    */ 
/*    */   public static PinAboutFrame getInstance() {
/* 27 */     if (singleInstance == null) {
/* 28 */       singleInstance = new PinAboutFrame();
/*    */     }
/* 30 */     return singleInstance;
/*    */   }
/*    */ 
/*    */   private void build() {
/* 34 */     setTitle("pin.about.frame.title");
/* 35 */     setLayout(new BoxLayout(getContentPane(), 1));
/* 36 */     setBackground(Color.WHITE);
/*    */ 
/* 38 */     JLocalizableLabel topText = new JLocalizableLabel("pin.about.frame.top.text");
/* 39 */     topText.setHorizontalAlignment(0);
/* 40 */     topText.setBackground(Color.WHITE);
/* 41 */     JPanel topPanel = new JPanel(new BorderLayout());
/* 42 */     topPanel.setBackground(Color.WHITE);
/* 43 */     topPanel.add(topText);
/*    */ 
/* 45 */     JPanel pinAboutPanel = new JPanel(new BorderLayout());
/* 46 */     JLabel label = new JLabel(GuiUtilsAndConstants.PIN_ABOUT_LOGO);
/* 47 */     label.setHorizontalAlignment(0);
/* 48 */     pinAboutPanel.add(label);
/*    */ 
/* 50 */     JLocalizableLabel bottomText = new JLocalizableLabel("pin.about.frame.bottom.text");
/* 51 */     bottomText.setHorizontalAlignment(0);
/* 52 */     bottomText.setBackground(Color.WHITE);
/* 53 */     JPanel bottomPanel = new JPanel(new BorderLayout());
/* 54 */     bottomPanel.setBackground(Color.WHITE);
/* 55 */     bottomPanel.add(bottomText);
/*    */ 
/* 57 */     createWhiteEmptySpace();
/*    */ 
/* 59 */     add(topPanel);
/* 60 */     add(pinAboutPanel);
/* 61 */     add(bottomPanel);
/*    */ 
/* 63 */     createWhiteEmptySpace();
/*    */ 
/* 65 */     setResizable(false);
/* 66 */     pack();
/*    */ 
/* 68 */     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
/* 69 */     Dimension frameSize = getSize();
/* 70 */     setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
/*    */   }
/*    */ 
/*    */   private void createWhiteEmptySpace() {
/* 74 */     JPanel result = new JPanel();
/* 75 */     result.setLayout(new BoxLayout(result, 1));
/* 76 */     result.setBackground(Color.white);
/* 77 */     result.add(Box.createVerticalStrut(10));
/* 78 */     result.add(Box.createHorizontalStrut(420));
/* 79 */     add(result);
/*    */   }
/*    */ 
/*    */   public void localize()
/*    */   {
/* 84 */     super.localize();
/* 85 */     doLayout();
/* 86 */     repaint();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.PinAboutFrame
 * JD-Core Version:    0.6.0
 */