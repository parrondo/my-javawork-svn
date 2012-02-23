/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.charts.utils.file.filter.AbstractFileFilter;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.filechooser.FileSystemView;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractFileSelectionPanel extends JPanel
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  37 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileSelectionPanel.class);
/*     */   private final String defaultFileName;
/*  41 */   protected JLocalizableCheckBox chbSaveToFile = null;
/*  42 */   private JTextField txtSaveToFile = new JTextField(20);
/*  43 */   private JButton btnBrowseFile = new JButton(LocalizationManager.getText("tester.file.browse"));
/*  44 */   private File currentDir = FileSystemView.getFileSystemView().getDefaultDirectory();
/*     */   protected final TesterParameters testerParameters;
/*     */ 
/*     */   public AbstractFileSelectionPanel(String checkBoxCaption, String defaultFileName, TesterParameters testerParameters)
/*     */   {
/*  49 */     this.defaultFileName = defaultFileName;
/*  50 */     this.testerParameters = testerParameters;
/*  51 */     this.chbSaveToFile = new JLocalizableCheckBox(checkBoxCaption);
/*     */ 
/*  53 */     buildUI();
/*     */   }
/*     */ 
/*     */   public List<JComponent> getPanelComponents()
/*     */   {
/*  62 */     List components = new ArrayList();
/*     */ 
/*  64 */     components.add(this.chbSaveToFile);
/*  65 */     components.add(this.txtSaveToFile);
/*  66 */     components.add(this.btnBrowseFile);
/*     */ 
/*  68 */     return components;
/*     */   }
/*     */ 
/*     */   private void buildUI() {
/*  72 */     this.chbSaveToFile.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e)
/*     */       {
/*  77 */         if (AbstractFileSelectionPanel.this.isSaveMessagesToFile()) {
/*  78 */           String fileName = AbstractFileSelectionPanel.this.getMessagesFilePath();
/*  79 */           if (fileName.length() < 1)
/*  80 */             AbstractFileSelectionPanel.this.setMessagesFilePath(new File(AbstractFileSelectionPanel.this.currentDir, AbstractFileSelectionPanel.this.defaultFileName).getAbsolutePath());
/*     */         }
/*     */       }
/*     */     });
/*  85 */     this.btnBrowseFile.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  88 */         AbstractFileSelectionPanel.this.browseFileName();
/*     */       }
/*     */     });
/*  92 */     setLayout(new GridBagLayout());
/*  93 */     add(this.chbSaveToFile, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
/*     */ 
/*  96 */     add(this.txtSaveToFile, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 5, 0, 0), 0, 0));
/*     */ 
/*  99 */     add(this.btnBrowseFile, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 5, 0, 0), 0, 0));
/*     */ 
/* 103 */     setData();
/*     */   }
/*     */ 
/*     */   public boolean isSaveMessagesToFile() {
/* 107 */     return this.chbSaveToFile.isSelected();
/*     */   }
/*     */ 
/*     */   public File getFile() {
/* 111 */     if (isMessagesFileNameValid()) {
/* 112 */       return new File(getMessagesFilePath());
/*     */     }
/* 114 */     return null;
/*     */   }
/*     */ 
/*     */   private String getMessagesFilePath()
/*     */   {
/* 123 */     return this.txtSaveToFile.getText().trim();
/*     */   }
/*     */ 
/*     */   private void setMessagesFilePath(String path) {
/* 127 */     this.txtSaveToFile.setText(path);
/*     */   }
/*     */ 
/*     */   private void browseFileName()
/*     */   {
/* 135 */     File currentFile = getFile();
/* 136 */     if (currentFile == null) {
/* 137 */       currentFile = new File(this.currentDir, this.defaultFileName);
/*     */     }
/*     */ 
/* 140 */     File file = DCFileChooser.saveFile(currentFile.getParent(), currentFile, getFileFilter(), 2);
/* 141 */     if (file != null) {
/* 142 */       this.currentDir = file.getParentFile();
/* 143 */       setMessagesFilePath(file.getAbsolutePath());
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isMessagesFileNameValid() {
/* 148 */     String path = getMessagesFilePath();
/* 149 */     if (path.length() > 0)
/*     */     {
/* 152 */       File file = new File(path);
/* 153 */       if (!file.exists())
/* 154 */         return true;
/*     */       try
/*     */       {
/* 157 */         return file.canWrite();
/*     */       } catch (SecurityException ex) {
/* 159 */         LOGGER.debug("Application cannot write to file " + path, ex);
/* 160 */         return false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 165 */     return false;
/*     */   }
/*     */ 
/*     */   protected void setSelected(boolean select)
/*     */   {
/* 170 */     this.chbSaveToFile.setSelected(select);
/*     */   }
/*     */ 
/*     */   protected void setFile(File file) {
/* 174 */     if (file != null) {
/* 175 */       String path = file.getAbsolutePath();
/* 176 */       if ((path != null) && (path.trim().length() > 0))
/* 177 */         setMessagesFilePath(path.trim());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract AbstractFileFilter getFileFilter();
/*     */ 
/*     */   protected abstract void setData();
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.AbstractFileSelectionPanel
 * JD-Core Version:    0.6.0
 */