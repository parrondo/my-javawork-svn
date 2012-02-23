/*     */ package com.dukascopy.dds2.greed.gui.component.status;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.connect.ConnectLabel;
/*     */ import com.dukascopy.dds2.greed.gui.component.connect.ConnectStatus;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountInfoListener;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.font.TextAttribute;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JSeparator;
/*     */ 
/*     */ public class GreedStatusBar extends JPanel
/*     */   implements PlatformSpecific, AccountInfoListener
/*     */ {
/*     */   private AccountStatementPanel accountStatement;
/*     */   private ConnectLabel connectLabel;
/*     */   private HeapSizePanel heapSizePanel;
/*     */   private JLabel skypeButton;
/*     */   private JLocalizableLabel detachedFarmesCount;
/*     */ 
/*     */   public GreedStatusBar()
/*     */   {
/*  55 */     BoxLayout box = new BoxLayout(this, 0);
/*  56 */     setLayout(box);
/*     */ 
/*  58 */     this.accountStatement = new AccountStatementPanel();
/*  59 */     add(this.accountStatement);
/*  60 */     add(Box.createHorizontalGlue());
/*     */ 
/*  63 */     initRightSide();
/*     */ 
/*  65 */     this.skypeButton.addMouseListener(new MouseAdapter() {
/*     */       public void mouseClicked(MouseEvent e) {
/*  67 */         GuiUtilsAndConstants.skypeUs();
/*     */       }
/*     */ 
/*     */       public void mouseEntered(MouseEvent e) {
/*  71 */         GreedStatusBar.this.setCursor(Cursor.getPredefinedCursor(12));
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e) {
/*  75 */         GreedStatusBar.this.setCursor(Cursor.getDefaultCursor());
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public void onAccountInfo(AccountInfoMessage accountInfo)
/*     */   {
/*  86 */     this.accountStatement.onAccountInfo(accountInfo);
/*  87 */     if (this.skypeButton.getIcon() == null)
/*  88 */       this.skypeButton.setIcon(GuiUtilsAndConstants.SKYPE_ICON);
/*     */   }
/*     */ 
/*     */   public void setConnectStatus(ConnectStatus status)
/*     */   {
/*  97 */     this.connectLabel.setStatus(status);
/*     */   }
/*     */ 
/*     */   public ConnectStatus getConnectStatus()
/*     */   {
/* 105 */     return this.connectLabel.getStatus();
/*     */   }
/*     */ 
/*     */   public void flashConnectIcon()
/*     */   {
/* 112 */     this.connectLabel.flash();
/*     */   }
/*     */ 
/*     */   public AccountStatementPanel getAccountStatement()
/*     */   {
/* 120 */     return this.accountStatement;
/*     */   }
/*     */ 
/*     */   public void updateFrameCount()
/*     */   {
/* 127 */     int detachedCount = 0;
/* 128 */     for (Frame frame : JFrame.getFrames()) {
/* 129 */       if ((!(frame instanceof JFrame)) || (!frame.isVisible()) || (frame.getName().equals("ID_JF_CLIENTFORM")) || (frame.getName().equals("ID_JF_LOGINFORM")))
/*     */       {
/*     */         continue;
/*     */       }
/* 133 */       detachedCount++;
/*     */     }
/*     */ 
/* 136 */     this.detachedFarmesCount.setTextParams(new Object[] { String.valueOf(detachedCount) });
/* 137 */     this.detachedFarmesCount.setText("label.detached");
/*     */   }
/*     */ 
/*     */   public void setHeapSizePanelVisible(boolean visible) {
/* 141 */     this.heapSizePanel.setVisible(visible);
/* 142 */     revalidate();
/* 143 */     repaint();
/*     */   }
/*     */ 
/*     */   private void addSeparator(JPanel panel) {
/* 147 */     JSeparator separator = new JSeparator(1);
/* 148 */     separator.setMaximumSize(new Dimension(5, 15));
/* 149 */     panel.add(Box.createHorizontalStrut(7));
/* 150 */     panel.add(separator);
/* 151 */     panel.add(Box.createHorizontalStrut(7));
/*     */   }
/*     */ 
/*     */   private void initRightSide() {
/* 155 */     JPanel right = new JPanel();
/*     */ 
/* 157 */     this.connectLabel = new ConnectLabel(ConnectStatus.OFFLINE);
/*     */ 
/* 159 */     BoxLayout box = new BoxLayout(right, 0);
/* 160 */     right.setLayout(box);
/*     */ 
/* 162 */     this.connectLabel.setHorizontalAlignment(0);
/*     */ 
/* 164 */     boolean showHeapSize = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).getHeapSizeShown();
/* 165 */     this.heapSizePanel = new HeapSizePanel();
/* 166 */     this.heapSizePanel.setVisible(showHeapSize);
/* 167 */     right.add(this.heapSizePanel);
/*     */ 
/* 169 */     if ((!GreedContext.IS_KAKAKU_LABEL) && (GuiUtilsAndConstants.SKYPE_TO_PARTNER != null)) {
/* 170 */       addSeparator(right);
/*     */     }
/*     */ 
/* 173 */     this.skypeButton = new JResizableLabel("Skype");
/* 174 */     if (MACOSX) {
/* 175 */       this.skypeButton.putClientProperty("JComponent.sizeVariant", "small");
/* 176 */       this.connectLabel.putClientProperty("JComponent.sizeVariant", "small");
/*     */     }
/*     */ 
/* 179 */     if ((!GreedContext.IS_KAKAKU_LABEL) && (GuiUtilsAndConstants.LABEL_SKYPE_ID != null)) {
/* 180 */       right.add(this.skypeButton);
/* 181 */       addSeparator(right);
/*     */     }
/*     */ 
/* 184 */     right.add(this.connectLabel);
/* 185 */     addSeparator(right);
/*     */ 
/* 187 */     this.detachedFarmesCount = createLinkedLabel();
/* 188 */     right.add(this.detachedFarmesCount);
/* 189 */     right.add(Box.createHorizontalStrut(5));
/*     */ 
/* 191 */     add(right);
/*     */   }
/*     */ 
/*     */   private JLocalizableLabel createLinkedLabel() {
/* 195 */     Map fontAttributes = new HashMap();
/* 196 */     fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
/*     */ 
/* 198 */     Font underlinedFont = this.connectLabel.getFont().deriveFont(fontAttributes);
/* 199 */     Font plainFont = this.connectLabel.getFont();
/*     */ 
/* 201 */     JLocalizableLabel result = new JLocalizableLabel(plainFont, underlinedFont)
/*     */     {
/*     */     };
/* 235 */     return result;
/*     */   }
/*     */ 
/*     */   public void disose()
/*     */   {
/* 242 */     this.heapSizePanel.setCanContinue(false);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar
 * JD-Core Version:    0.6.0
 */