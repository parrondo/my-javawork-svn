/*    */ package com.dukascopy.charts.dialogs;
/*    */ 
/*    */ import com.dukascopy.charts.main.DDSChartsControllerImpl;
/*    */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.ThemeManager;
/*    */ import com.dukascopy.charts.theme.CommonThemeSettingsPanel;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JButton;
/*    */ import javax.swing.JDialog;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class ChartThemesDialog extends JDialog
/*    */   implements ActionListener
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final String OK_ACTION = "theme_ok";
/*    */   private static final String CANCEL_ACTION = "theme_cancel";
/*    */   private JButton btnOk;
/*    */   private JButton btnCancel;
/*    */   private CommonThemeSettingsPanel themesPanel;
/* 36 */   private int chartId = -1;
/*    */ 
/*    */   public ChartThemesDialog(JFrame parentFrame, int chartId)
/*    */   {
/* 40 */     super(parentFrame, false);
/* 41 */     this.chartId = chartId;
/*    */ 
/* 43 */     setSize(650, 450);
/* 44 */     setLocationRelativeTo(parentFrame);
/* 45 */     setTitle(LocalizationManager.getText("title.themes"));
/*    */ 
/* 47 */     init();
/*    */   }
/*    */ 
/*    */   private void init() {
/* 51 */     String themeName = DDSChartsControllerImpl.getInstance().getTheme(this.chartId);
/* 52 */     ITheme selectedTheme = ThemeManager.getTheme(themeName);
/*    */ 
/* 54 */     this.themesPanel = new CommonThemeSettingsPanel(this.chartId, selectedTheme);
/* 55 */     this.themesPanel.resetFields();
/*    */ 
/* 57 */     this.btnOk = new JLocalizableButton("button.ok");
/* 58 */     this.btnOk.setActionCommand("theme_ok");
/* 59 */     this.btnOk.addActionListener(this);
/*    */ 
/* 61 */     this.btnOk.setFocusCycleRoot(true);
/*    */ 
/* 63 */     this.btnCancel = new JLocalizableButton("button.cancel");
/* 64 */     this.btnCancel.setActionCommand("theme_cancel");
/* 65 */     this.btnCancel.addActionListener(this);
/*    */ 
/* 67 */     JPanel buttonPanel = new JPanel(new AbsoluteLayout());
/* 68 */     buttonPanel.setPreferredSize(new Dimension(buttonPanel.getPreferredSize().width, 35));
/* 69 */     buttonPanel.add(this.btnOk, new AbsoluteLayoutConstraints(220, 5, 90, 25));
/* 70 */     buttonPanel.add(this.btnCancel, new AbsoluteLayoutConstraints(335, 5, 90, 25));
/*    */ 
/* 72 */     JPanel contentPane = new JPanel(new BorderLayout());
/* 73 */     contentPane.add(this.themesPanel, "Center");
/* 74 */     contentPane.add(buttonPanel, "South");
/*    */ 
/* 76 */     setContentPane(contentPane);
/*    */ 
/* 78 */     setResizable(false);
/* 79 */     setVisible(true);
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 84 */     if (e.getActionCommand().equals("theme_cancel")) {
/* 85 */       dispose();
/* 86 */     } else if ((e.getActionCommand().equals("theme_ok")) && 
/* 87 */       (this.themesPanel.verifySettings())) {
/* 88 */       this.themesPanel.applySettings();
/* 89 */       dispose();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.ChartThemesDialog
 * JD-Core Version:    0.6.0
 */