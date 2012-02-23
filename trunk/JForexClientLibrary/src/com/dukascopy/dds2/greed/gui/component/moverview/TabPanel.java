/*     */ package com.dukascopy.dds2.greed.gui.component.moverview;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.config.MarketOverviewConfig;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.FocusEvent;
/*     */ import java.awt.event.FocusListener;
/*     */ import java.awt.event.KeyEvent;
/*     */ import java.awt.event.KeyListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.util.List;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.plaf.basic.BasicButtonUI;
/*     */ 
/*     */ public class TabPanel extends JPanel
/*     */   implements ChangeListener, PlatformSpecific
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private JButton closeButton;
/*     */   private MarketOverviewFrame instrumentFrame;
/*     */   private final JTabbedPane pane;
/*     */   private List<TabContentPanel> contentPanes;
/*     */   private JLabel textLabel;
/*     */   private JTextArea renamingTextArea;
/* 269 */   private static final MouseListener buttonMouseListener = new MouseAdapter() {
/*     */     public void mouseEntered(MouseEvent e) {
/* 271 */       Component component = e.getComponent();
/*     */ 
/* 273 */       if (((component instanceof AbstractButton)) && (component.isEnabled())) {
/* 274 */         AbstractButton button = (AbstractButton)component;
/* 275 */         button.setBorderPainted(true);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent e) {
/* 280 */       Component component = e.getComponent();
/* 281 */       if ((component instanceof AbstractButton)) {
/* 282 */         AbstractButton button = (AbstractButton)component;
/* 283 */         button.setBorderPainted(false);
/*     */       }
/*     */     }
/* 269 */   };
/*     */ 
/* 288 */   private static TabPanel focusedPanel = null;
/*     */ 
/*     */   public TabPanel(MarketOverviewFrame frame)
/*     */   {
/*  63 */     FlowLayout flow = new FlowLayout(0, 0, 0);
/*     */ 
/*  65 */     setLayout(flow);
/*     */ 
/*  67 */     this.instrumentFrame = frame;
/*  68 */     this.pane = frame.getTPane();
/*  69 */     this.contentPanes = frame.getContentPanes();
/*     */ 
/*  71 */     this.pane.addChangeListener(this);
/*     */ 
/*  73 */     if (this.pane == null) {
/*  74 */       throw new NullPointerException("TabbedPane is null");
/*     */     }
/*     */ 
/*  77 */     setOpaque(false);
/*     */ 
/*  79 */     this.textLabel = new JLabel()
/*     */     {
/*     */       public String getText()
/*     */       {
/*  83 */         int i = TabPanel.this.indexOfTabComponent(TabPanel.this.pane, TabPanel.this);
/*  84 */         if (i != -1) {
/*  85 */           return TabPanel.this.pane.getTitleAt(i);
/*     */         }
/*  87 */         return null;
/*     */       }
/*     */ 
/*     */       public void setText(String text)
/*     */       {
/*  92 */         int i = TabPanel.this.indexOfTabComponent(TabPanel.this.pane, TabPanel.this);
/*  93 */         if (i != -1)
/*  94 */           TabPanel.this.pane.setTitleAt(i, text);
/*     */       }
/*     */     };
/* 100 */     this.renamingTextArea = new JTextArea();
/*     */ 
/* 102 */     this.renamingTextArea.addFocusListener(new FocusListener()
/*     */     {
/*     */       public void focusGained(FocusEvent arg0)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void focusLost(FocusEvent lostEvent)
/*     */       {
/* 110 */         String tabName = TabPanel.this.renamingTextArea.getText();
/* 111 */         String trimmedName = tabName.trim();
/*     */ 
/* 113 */         if (trimmedName.length() < 1) {
/* 114 */           TabPanel.this.startRenaming();
/* 115 */           return;
/*     */         }
/*     */ 
/* 118 */         TabPanel.this.renamingTextArea.setVisible(false);
/* 119 */         TabPanel.this.textLabel.setText(TabPanel.this.renamingTextArea.getText());
/* 120 */         TabPanel.this.textLabel.setVisible(true);
/* 121 */         TabPanel.this.repaint();
/*     */       }
/*     */     });
/* 126 */     this.renamingTextArea.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyPressed(KeyEvent e)
/*     */       {
/* 131 */         if ('\n' == e.getKeyChar())
/*     */         {
/* 133 */           String textWithOutLineDelimiters = TabPanel.this.renamingTextArea.getText().replaceAll("\n", "");
/* 134 */           String tabName = textWithOutLineDelimiters;
/* 135 */           String trimmedName = tabName.trim();
/*     */ 
/* 137 */           if (trimmedName.length() < 1) {
/* 138 */             TabPanel.this.startRenaming();
/* 139 */             return;
/*     */           }
/*     */ 
/* 142 */           TabPanel.this.renamingTextArea.setVisible(false);
/*     */ 
/* 144 */           TabPanel.this.textLabel.setText(trimmedName);
/* 145 */           TabPanel.this.textLabel.setVisible(true);
/* 146 */           TabPanel.this.repaint();
/*     */         }
/*     */       }
/*     */ 
/*     */       public void keyReleased(KeyEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void keyTyped(KeyEvent e)
/*     */       {
/*     */       }
/*     */     });
/* 155 */     add(this.textLabel);
/*     */ 
/* 157 */     this.renamingTextArea.setVisible(false);
/* 158 */     add(this.renamingTextArea);
/*     */ 
/* 160 */     int xLeftCross = 8;
/* 161 */     add(Box.createHorizontalStrut(16));
/*     */ 
/* 163 */     this.closeButton = new TabButton();
/* 164 */     add(this.closeButton);
/*     */ 
/* 168 */     if (MACOSX)
/* 169 */       setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0));
/*     */     else
/* 171 */       setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 4));
/*     */   }
/*     */ 
/*     */   private int indexOfTabComponent(JTabbedPane tabbedPane, Component tabComponent)
/*     */   {
/* 180 */     int rc = 0;
/*     */     try {
/* 182 */       rc = Integer.valueOf(this.pane.indexOfTabComponent(tabComponent)).intValue();
/*     */     } catch (Exception e) {
/* 184 */       e.printStackTrace();
/*     */     }
/* 186 */     return rc;
/*     */   }
/*     */ 
/*     */   public void startRenaming()
/*     */   {
/* 191 */     String textWithOutLineDelimiters = this.textLabel.getText().replaceAll("\n", "");
/* 192 */     textWithOutLineDelimiters = textWithOutLineDelimiters.trim();
/*     */ 
/* 194 */     this.renamingTextArea.setFont(this.textLabel.getFont());
/* 195 */     this.renamingTextArea.setText(textWithOutLineDelimiters);
/*     */ 
/* 197 */     this.renamingTextArea.setVisible(false);
/* 198 */     this.renamingTextArea.setVisible(true);
/* 199 */     this.textLabel.setVisible(false);
/*     */ 
/* 201 */     revalidate();
/* 202 */     repaint();
/*     */ 
/* 204 */     this.renamingTextArea.selectAll();
/* 205 */     this.renamingTextArea.requestFocus();
/*     */   }
/*     */ 
/*     */   public void stateChanged(ChangeEvent e)
/*     */   {
/*     */   }
/*     */ 
/*     */   public JButton getCloseButton()
/*     */   {
/* 314 */     return this.closeButton;
/*     */   }
/*     */ 
/*     */   public void setCloseButton(JButton closeButton) {
/* 318 */     this.closeButton = closeButton;
/*     */   }
/*     */ 
/*     */   public JTextArea getRenamingTextArea() {
/* 322 */     return this.renamingTextArea;
/*     */   }
/*     */ 
/*     */   public void setRenamingTextArea(JTextArea renamingTextArea) {
/* 326 */     this.renamingTextArea = renamingTextArea;
/*     */   }
/*     */ 
/*     */   public JLabel getTextLabel() {
/* 330 */     return this.textLabel;
/*     */   }
/*     */ 
/*     */   public void setTextLabel(JLabel textLabel) {
/* 334 */     this.textLabel = textLabel;
/*     */   }
/*     */ 
/*     */   private class TabButton extends JButton
/*     */     implements ActionListener
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 213 */     int size = 17;
/*     */ 
/*     */     public TabButton()
/*     */     {
/* 217 */       setPreferredSize(new Dimension(this.size, this.size));
/* 218 */       setToolTipText("Close this tab");
/*     */ 
/* 220 */       setUI(new BasicButtonUI());
/* 221 */       setContentAreaFilled(false);
/* 222 */       setFocusable(false);
/*     */ 
/* 224 */       setBorder(BorderFactory.createLineBorder(Color.GRAY));
/* 225 */       setBorderPainted(false);
/*     */ 
/* 227 */       addMouseListener(TabPanel.buttonMouseListener);
/* 228 */       setRolloverEnabled(true);
/*     */ 
/* 230 */       addActionListener(this);
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e) {
/* 234 */       int indexOfClsingTab = TabPanel.this.indexOfTabComponent(TabPanel.this.pane, TabPanel.this);
/*     */ 
/* 236 */       if (indexOfClsingTab > -1) {
/* 237 */         TabPanel.this.instrumentFrame.getMarketOverviewConfig().getTabs().remove(indexOfClsingTab);
/* 238 */         TabPanel.this.pane.removeTabAt(indexOfClsingTab);
/* 239 */         TabPanel.this.contentPanes.remove(indexOfClsingTab);
/* 240 */         TabPanel.this.instrumentFrame.saveInstruments();
/*     */       }
/*     */ 
/* 243 */       TabPanel.this.instrumentFrame.enableOrDisableAllCloseButtons();
/*     */     }
/*     */ 
/*     */     public void updateUI()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void paintComponent(Graphics g) {
/* 251 */       super.paintComponent(g);
/* 252 */       Graphics2D g2 = (Graphics2D)g.create();
/*     */ 
/* 254 */       if (getModel().isPressed()) {
/* 255 */         g2.translate(1, 1);
/*     */       }
/* 257 */       g2.setStroke(new BasicStroke(2.0F, 1, 0));
/* 258 */       g2.setColor(Color.GRAY);
/* 259 */       if (getModel().isRollover()) {
/* 260 */         g2.setColor(Color.DARK_GRAY);
/*     */       }
/* 262 */       int delta = 5;
/* 263 */       g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
/* 264 */       g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
/* 265 */       g2.dispose();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.moverview.TabPanel
 * JD-Core Version:    0.6.0
 */