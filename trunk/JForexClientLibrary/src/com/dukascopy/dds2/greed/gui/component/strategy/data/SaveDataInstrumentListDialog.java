/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.data;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTable;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.GridLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.event.TableModelEvent;
/*     */ import javax.swing.event.TableModelListener;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.text.Document;
/*     */ 
/*     */ public class SaveDataInstrumentListDialog extends JLocalizableDialog
/*     */ {
/*  89 */   private JTextField txtDirectory = new JTextField();
/*  90 */   private InstrumentsTableModel mdlInstruments = new InstrumentsTableModel();
/*     */   private List<InstrumentTableData> modalResult;
/*  92 */   private JLocalizableButton btnSaveSelected = new JLocalizableButton();
/*  93 */   private JLocalizableTable tblInstruments = new JLocalizableTable()
/*     */   {
/*     */     public void translate() {
/*  96 */       SaveDataInstrumentListDialog.this.mdlInstruments.translate();
/*     */     }
/*  93 */   };
/*     */ 
/*     */   public static List<InstrumentTableData> showModal(String titleKey, File directory, List<InstrumentTableData> instruments)
/*     */   {
/*  73 */     JFrame parentFrame = (JFrame)GreedContext.get("clientGui");
/*     */ 
/*  75 */     SaveDataInstrumentListDialog dialog = new SaveDataInstrumentListDialog(parentFrame);
/*  76 */     dialog.setTitle(titleKey);
/*  77 */     dialog.setModal(true);
/*  78 */     dialog.setDirectory(directory);
/*  79 */     dialog.setInstruments(instruments);
/*  80 */     dialog.updateButtonState();
/*  81 */     dialog.setPreferredSize(new Dimension(dialog.getPreferredSize().width, 300));
/*  82 */     dialog.pack();
/*  83 */     dialog.setLocationRelativeTo(parentFrame);
/*  84 */     dialog.modalResult = null;
/*  85 */     dialog.setVisible(true);
/*  86 */     return dialog.modalResult;
/*     */   }
/*     */ 
/*     */   public SaveDataInstrumentListDialog(JFrame parentFrame)
/*     */   {
/* 105 */     super(parentFrame);
/* 106 */     initUI();
/*     */   }
/*     */ 
/*     */   protected void initUI()
/*     */   {
/* 113 */     setDefaultCloseOperation(2);
/*     */ 
/* 115 */     this.mdlInstruments.addTableModelListener(new TableModelListener()
/*     */     {
/*     */       public void tableChanged(TableModelEvent e) {
/* 118 */         SaveDataInstrumentListDialog.this.updateButtonState();
/*     */       }
/*     */     });
/* 122 */     this.tblInstruments.setModel(this.mdlInstruments);
/* 123 */     this.tblInstruments.setAutoResizeMode(3);
/*     */ 
/* 126 */     TableColumnModel columnModel = this.tblInstruments.getColumnModel();
/* 127 */     columnModel.getColumn(1).setPreferredWidth(40);
/* 128 */     columnModel.getColumn(0).setPreferredWidth(40);
/* 129 */     columnModel.getColumn(2).setPreferredWidth(200);
/*     */ 
/* 131 */     this.txtDirectory.setColumns(40);
/* 132 */     this.txtDirectory.getDocument().addDocumentListener(new DocumentListener()
/*     */     {
/*     */       public void removeUpdate(DocumentEvent e) {
/* 135 */         SaveDataInstrumentListDialog.this.confirmTableEdit();
/* 136 */         SaveDataInstrumentListDialog.this.updateButtonState();
/*     */       }
/*     */ 
/*     */       public void insertUpdate(DocumentEvent e)
/*     */       {
/* 141 */         SaveDataInstrumentListDialog.this.confirmTableEdit();
/* 142 */         SaveDataInstrumentListDialog.this.updateButtonState();
/*     */       }
/*     */ 
/*     */       public void changedUpdate(DocumentEvent e)
/*     */       {
/* 147 */         SaveDataInstrumentListDialog.this.confirmTableEdit();
/* 148 */         SaveDataInstrumentListDialog.this.updateButtonState();
/*     */       }
/*     */     });
/* 151 */     JLocalizableLabel lblDirectory = new JLocalizableLabel("dialog.save.data.label.directory");
/* 152 */     JButton btnBrowse = new JButton("...");
/* 153 */     btnBrowse.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 156 */         SaveDataInstrumentListDialog.this.onActionBrowse(e);
/*     */       }
/*     */     });
/* 160 */     JLocalizableButton btnSelectAll = new JLocalizableButton();
/* 161 */     btnSelectAll.setText("dialog.save.data.button.select.all");
/* 162 */     btnSelectAll.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 165 */         SaveDataInstrumentListDialog.this.mdlInstruments.selectAll();
/*     */       }
/*     */     });
/* 169 */     JLocalizableButton btnSelectNone = new JLocalizableButton();
/* 170 */     btnSelectNone.setText("dialog.save.data.button.select.none");
/* 171 */     btnSelectNone.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 174 */         SaveDataInstrumentListDialog.this.mdlInstruments.unselectAll();
/*     */       }
/*     */     });
/* 178 */     this.btnSaveSelected.setText("dialog.save.data.button.save");
/* 179 */     this.btnSaveSelected.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 182 */         SaveDataInstrumentListDialog.this.onActionSaveSelected(e);
/*     */       }
/*     */     });
/* 186 */     JLocalizableButton btnCancel = new JLocalizableButton("dialog.save.data.button.cancel");
/* 187 */     btnCancel.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 190 */         SaveDataInstrumentListDialog.this.dispose();
/*     */       }
/*     */     });
/* 194 */     JPanel pnlSelectButtons = new JPanel();
/* 195 */     pnlSelectButtons.setLayout(new GridLayout(1, 2, 5, 0));
/* 196 */     pnlSelectButtons.add(btnSelectAll);
/* 197 */     pnlSelectButtons.add(btnSelectNone);
/*     */ 
/* 199 */     JPanel pnlButtons = new JPanel();
/* 200 */     pnlButtons.setLayout(new GridLayout(1, 2, 5, 0));
/* 201 */     pnlButtons.add(this.btnSaveSelected);
/* 202 */     pnlButtons.add(btnCancel);
/*     */ 
/* 204 */     GridBagLayout layoutMain = new GridBagLayout();
/* 205 */     JPanel pnlMain = new JPanel();
/* 206 */     pnlMain.setLayout(layoutMain);
/* 207 */     pnlMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 208 */     pnlMain.add(lblDirectory, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
/*     */ 
/* 211 */     pnlMain.add(this.txtDirectory, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(0, 5, 0, 0), 0, 0));
/*     */ 
/* 214 */     pnlMain.add(btnBrowse, new GridBagConstraints(2, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 5, 0, 0), 0, 0));
/*     */ 
/* 218 */     pnlMain.add(new JScrollPane(this.tblInstruments), new GridBagConstraints(0, 1, 3, 1, 1.0D, 1.0D, 17, 1, new Insets(5, 0, 0, 0), 0, 0));
/*     */ 
/* 222 */     pnlMain.add(pnlSelectButtons, new GridBagConstraints(0, 2, 3, 1, 0.0D, 0.0D, 17, 0, new Insets(5, 0, 0, 0), 0, 0));
/*     */ 
/* 226 */     pnlMain.add(pnlButtons, new GridBagConstraints(0, 3, 3, 1, 0.0D, 0.0D, 14, 0, new Insets(5, 5, 0, 0), 0, 0));
/*     */ 
/* 230 */     setContentPane(pnlMain);
/* 231 */     getRootPane().setDefaultButton(this.btnSaveSelected);
/*     */   }
/*     */ 
/*     */   private void confirmTableEdit()
/*     */   {
/* 238 */     TableCellEditor editor = this.tblInstruments.getCellEditor();
/* 239 */     if (editor != null)
/* 240 */       editor.stopCellEditing();
/*     */   }
/*     */ 
/*     */   private void onActionSaveSelected(ActionEvent e)
/*     */   {
/* 246 */     confirmTableEdit();
/*     */ 
/* 248 */     File directory = new File(this.txtDirectory.getText());
/*     */ 
/* 250 */     if (!directory.exists())
/*     */     {
/* 252 */       String pattern = LocalizationManager.getText("dialog.save.data.folder.not.exist");
/* 253 */       String message = MessageFormat.format(pattern, new Object[] { directory.getAbsolutePath() });
/* 254 */       int confirm = JOptionPane.showConfirmDialog(this, message, getTitle(), 0);
/* 255 */       if (confirm != 0) {
/* 256 */         return;
/*     */       }
/* 258 */       directory.mkdirs();
/*     */     }
/* 261 */     else if (!directory.isDirectory())
/*     */     {
/* 263 */       String pattern = LocalizationManager.getText("dialog.save.data.not.a.folder");
/* 264 */       String message = MessageFormat.format(pattern, new Object[] { directory.getAbsolutePath() });
/* 265 */       JOptionPane.showMessageDialog(this, message, getTitle(), 1);
/* 266 */       return;
/*     */     }
/*     */ 
/* 269 */     List saveData = this.mdlInstruments.getInstruments();
/* 270 */     List result = new LinkedList();
/*     */ 
/* 272 */     for (InstrumentTableData data : saveData) {
/* 273 */       String path = data.fileName;
/* 274 */       if (path == null) {
/* 275 */         String pattern = LocalizationManager.getText("dialog.save.data.file.name.absent");
/* 276 */         String message = MessageFormat.format(pattern, new Object[] { data.instrument.toString() });
/* 277 */         JOptionPane.showMessageDialog(this, message, getTitle(), 1);
/* 278 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 284 */     boolean confirmOverwrite = false;
/* 285 */     for (InstrumentTableData data : saveData) {
/* 286 */       String absolutePath = new File(directory, data.fileName).getAbsolutePath();
/* 287 */       result.add(new InstrumentTableData(data.instrument, absolutePath));
/*     */ 
/* 289 */       if (new File(data.fileName).exists()) {
/* 290 */         confirmOverwrite = true;
/*     */       }
/*     */     }
/*     */ 
/* 294 */     if (confirmOverwrite) {
/* 295 */       String message = LocalizationManager.getText("dialog.save.data.confirm.overwrite");
/* 296 */       int confirm = JOptionPane.showConfirmDialog(this, message, getTitle(), 0);
/* 297 */       if (confirm != 0) {
/* 298 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 304 */     this.modalResult = result;
/* 305 */     dispose();
/*     */   }
/*     */ 
/*     */   private void onActionBrowse(ActionEvent e)
/*     */   {
/* 311 */     confirmTableEdit();
/*     */ 
/* 313 */     File currentFile = new File(this.txtDirectory.getText());
/*     */ 
/* 315 */     String title = LocalizationManager.getText("dialog.save.data.folder.chooser.title");
/* 316 */     DCFileChooser chooser = DCFileChooser.createDCFileChooser(null, null);
/* 317 */     chooser.setDialogTitle(title);
/* 318 */     chooser.setDialogType(1);
/* 319 */     chooser.setFileSelectionMode(1);
/* 320 */     chooser.setMultiSelectionEnabled(false);
/* 321 */     if (currentFile.exists()) {
/* 322 */       chooser.setSelectedFile(currentFile);
/* 323 */       chooser.setCurrentDirectory(currentFile.getParentFile());
/*     */     }
/*     */ 
/* 326 */     String buttonText = LocalizationManager.getText("dialog.save.data.folder.chooser.select");
/* 327 */     int confirm = chooser.showDialog(this, buttonText);
/* 328 */     if (confirm == 0)
/* 329 */       this.txtDirectory.setText(chooser.getSelectedFile().getAbsolutePath());
/*     */   }
/*     */ 
/*     */   private void updateButtonState()
/*     */   {
/* 338 */     boolean valid = false;
/*     */ 
/* 341 */     for (int i = 0; i < this.mdlInstruments.getRowCount(); i++) {
/* 342 */       if (this.mdlInstruments.isSelected(i)) {
/* 343 */         valid = true;
/* 344 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 350 */     if (valid) {
/* 351 */       String directory = this.txtDirectory.getText().trim();
/* 352 */       File dir = new File(directory);
/* 353 */       if ((directory.length() > 0) && ((!dir.exists()) || (dir.isDirectory())))
/* 354 */         valid = true;
/*     */       else {
/* 356 */         valid = false;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 361 */     this.btnSaveSelected.setEnabled(valid);
/*     */   }
/*     */ 
/*     */   public void setDirectory(File directory) {
/* 365 */     if (directory == null)
/* 366 */       this.txtDirectory.setText(null);
/* 367 */     else if ((directory.exists()) && (directory.isFile()))
/* 368 */       this.txtDirectory.setText(directory.getAbsoluteFile().getParent());
/*     */     else
/* 370 */       this.txtDirectory.setText(directory.getAbsolutePath());
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<InstrumentTableData> instruments)
/*     */   {
/* 375 */     this.mdlInstruments.setInstruments(instruments);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.data.SaveDataInstrumentListDialog
 * JD-Core Version:    0.6.0
 */