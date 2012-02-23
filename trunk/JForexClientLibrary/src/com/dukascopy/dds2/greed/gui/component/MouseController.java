/*     */ package com.dukascopy.dds2.greed.gui.component;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.ICurvesProtocolHandler;
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.file.filter.XMLFileFilter;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.ChooserSelectionWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.filechooser.TransportFileChooser;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.workspace.WorkspaceOptionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreePanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.Hidable;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.transport.common.datafeed.FileType;
/*     */ import com.dukascopy.transport.common.datafeed.Location;
/*     */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.MouseMotionListener;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFormattedTextField;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.JSpinner.NumberEditor;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MouseController
/*     */   implements MouseListener, MouseMotionListener
/*     */ {
/*  48 */   private static Logger LOGGER = LoggerFactory.getLogger(MouseController.class);
/*     */   private JPanel panel;
/*  51 */   public int activeY = 15;
/*  52 */   public int activeX = 0;
/*  53 */   public int startY = 0;
/*  54 */   private boolean visibilitySwitchAllowed = true;
/*     */ 
/*  56 */   private final JPopupMenu popupMenu = new JPopupMenu();
/*  57 */   private final JMenuItem showHideItem = new JMenuItem();
/*     */ 
/*     */   public MouseController(JPanel panel) {
/*  60 */     this.panel = panel;
/*     */   }
/*     */ 
/*     */   public MouseController()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent e) {
/*  68 */     if (1 == e.getButton()) {
/*  69 */       if (((this.panel instanceof Hidable)) && (showHand(e)))
/*  70 */         ((Hidable)this.panel).switchVisibility();
/*     */     }
/*     */     else
/*  73 */       showPopup(e);
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent e)
/*     */   {
/*  78 */     showPopup(e);
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent e) {
/*  82 */     showPopup(e);
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent e) {
/*  86 */     if (!this.visibilitySwitchAllowed) {
/*  87 */       return;
/*     */     }
/*  89 */     if ((this.panel.isVisible()) && 
/*  90 */       (!showHand(e))) {
/*  91 */       return;
/*     */     }
/*  93 */     this.panel.setCursor(Cursor.getPredefinedCursor(12));
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent e) {
/*  97 */     if (this.panel.isVisible())
/*  98 */       this.panel.setCursor(Cursor.getDefaultCursor());
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent e) {
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent e) {
/* 105 */     if (this.panel.isVisible())
/* 106 */       if ((showHand(e)) && (this.visibilitySwitchAllowed))
/* 107 */         this.panel.setCursor(Cursor.getPredefinedCursor(12));
/*     */       else
/* 109 */         this.panel.setCursor(Cursor.getDefaultCursor());
/*     */   }
/*     */ 
/*     */   private void showPopup(MouseEvent e)
/*     */   {
/* 116 */     if (!this.visibilitySwitchAllowed) {
/* 117 */       return;
/*     */     }
/* 119 */     if ((this.panel.isVisible()) && 
/* 120 */       (!showHand(e))) {
/* 121 */       return;
/*     */     }
/*     */ 
/* 124 */     if (!e.isPopupTrigger()) return;
/*     */ 
/* 126 */     cleanPopup();
/*     */ 
/* 128 */     createManuActions();
/*     */ 
/* 130 */     this.popupMenu.show(this.panel, e.getX(), e.getY());
/*     */   }
/*     */ 
/*     */   private void cleanPopup()
/*     */   {
/* 135 */     this.popupMenu.removeAll();
/* 136 */     for (int i = 0; i < this.showHideItem.getActionListeners().length; i++) {
/* 137 */       this.showHideItem.removeActionListener(this.showHideItem.getActionListeners()[i]);
/*     */     }
/* 139 */     this.showHideItem.removeAll();
/*     */   }
/*     */ 
/*     */   private boolean showHand(MouseEvent e) {
/* 143 */     return (e.getY() < this.activeY) && (e.getY() > this.startY) && (e.getX() > this.activeX) && (e.getX() < this.panel.getSize().width - this.activeX);
/*     */   }
/*     */ 
/*     */   public boolean isVisibilitySwitchAllowed() {
/* 147 */     return this.visibilitySwitchAllowed;
/*     */   }
/*     */ 
/*     */   public void setVisibilitySwitchAllowed(boolean visibilitySwitchAllowed) {
/* 151 */     this.visibilitySwitchAllowed = visibilitySwitchAllowed;
/*     */   }
/*     */ 
/*     */   private void createManuActions()
/*     */   {
/* 156 */     if ((this.panel instanceof TickerPanel))
/*     */     {
/* 158 */       TickerPanel tp = (TickerPanel)this.panel;
/* 159 */       if (!GreedContext.isStrategyAllowed()) {
/* 160 */         String itemName = tp.isExpanded() ? LocalizationManager.getText("menu.item.hide") : LocalizationManager.getText("menu.item.show");
/*     */ 
/* 162 */         this.showHideItem.setText(itemName);
/*     */ 
/* 164 */         this.showHideItem.addActionListener(new ActionListener(tp)
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 167 */             this.val$tp.switchVisibility();
/*     */           }
/*     */         });
/* 170 */         this.popupMenu.add(this.showHideItem);
/*     */       }
/* 172 */     } else if ((this.panel instanceof WorkspaceTreePanel)) {
/* 173 */       JMenuItem openWorkspaceMenuItem = new JMenuItem(LocalizationManager.getText("menu.item.open.workspace"));
/* 174 */       openWorkspaceMenuItem.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 176 */           MouseController.openWorkspace();
/*     */         }
/*     */       });
/* 180 */       JMenuItem saveWorkspaceMenuItem = new JMenuItem(LocalizationManager.getText("menu.item.save.workspace"));
/* 181 */       saveWorkspaceMenuItem.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 183 */           MouseController.saveWorkspace();
/*     */         }
/*     */       });
/* 187 */       JMenuItem saveAsWorkspaceMenuItem = new JMenuItem(LocalizationManager.getText("menu.item.save.as.workspace"));
/* 188 */       saveAsWorkspaceMenuItem.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 190 */           MouseController.saveAsWorkspace();
/*     */         }
/*     */       });
/* 194 */       JMenuItem deleteSavedSettingsMenuItem = new JMenuItem(LocalizationManager.getText("label.delete.settings"));
/* 195 */       deleteSavedSettingsMenuItem.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 197 */           MouseController.deleteSavedSettings();
/*     */         }
/*     */       });
/* 201 */       this.popupMenu.add(openWorkspaceMenuItem);
/* 202 */       this.popupMenu.add(saveWorkspaceMenuItem);
/* 203 */       this.popupMenu.add(saveAsWorkspaceMenuItem);
/* 204 */       this.popupMenu.addSeparator();
/* 205 */       this.popupMenu.add(deleteSavedSettingsMenuItem);
/* 206 */     } else if ((this.panel instanceof Hidable)) {
/* 207 */       boolean expanded = ((Hidable)this.panel).isExpanded();
/* 208 */       String itemName = expanded ? LocalizationManager.getText("menu.item.hide") : LocalizationManager.getText("menu.item.show");
/* 209 */       this.showHideItem.setText(itemName);
/*     */ 
/* 211 */       this.showHideItem.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 214 */           ((Hidable)MouseController.this.panel).switchVisibility();
/*     */         }
/*     */       });
/* 217 */       this.popupMenu.add(this.showHideItem);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void openWorkspace()
/*     */   {
/* 223 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 224 */     clientSettingsStorage.saveWorkspaceSettings();
/*     */ 
/* 226 */     String wsFolder = clientSettingsStorage.getMyWorkspaceSettingsFolderPath();
/* 227 */     XMLFileFilter xmlFileFilter = new XMLFileFilter(LocalizationManager.getText("jforex.workspace.files"));
/*     */ 
/* 229 */     JFileChooser chooser = DCFileChooser.createFileChooser(wsFolder, null, xmlFileFilter, 0);
/*     */ 
/* 231 */     ChooserSelectionWrapper selection = TransportFileChooser.showOpenDialog(FileType.WORKSPACE, chooser, (JFrame)GreedContext.get("clientGui"), Boolean.valueOf(false), GreedContext.CLIENT_MODE);
/*     */ 
/* 233 */     if (selection != null)
/*     */     {
/* 235 */       Location loc = selection.getLocation();
/* 236 */       FileItem f = selection.getFileItem();
/*     */ 
/* 238 */       File wsFile = new File(f.getFileName());
/*     */ 
/* 240 */       if (loc != Location.LOCAL)
/*     */       {
/* 242 */         wsFile = new File(wsFolder, f.getFileName());
/*     */ 
/* 244 */         boolean continueLoop = true;
/*     */         do
/* 246 */           if (wsFile.exists())
/*     */           {
/* 248 */             String newName = JOptionPane.showInputDialog(LocalizationManager.getTextWithArguments("choose.another.name", new Object[] { wsFile.getName(), wsFile.getParent() }), wsFile.getName());
/*     */ 
/* 250 */             wsFile = new File(wsFolder, newName);
/*     */           }
/*     */           else {
/* 253 */             continueLoop = false;
/*     */           }
/* 255 */         while (continueLoop);
/*     */ 
/* 257 */         byte[] data = null;
/*     */         try
/*     */         {
/* 260 */           data = FeedDataProvider.getCurvesProtocolHandler().downloadFile(f.getFileId().longValue(), new FileProgressListener()).getFileData();
/*     */         } catch (Exception e) {
/* 262 */           LOGGER.error("Error retrieving remote workspace data: " + f.getFileName());
/*     */         }
/*     */         try
/*     */         {
/* 266 */           IOUtils.writeByteArrayToFile(wsFile, data);
/*     */         } catch (IOException e) {
/* 268 */           LOGGER.error("Error saving remote workspace data to local file: " + wsFile.getAbsolutePath(), e);
/*     */         }
/*     */       }
/*     */ 
/* 272 */       clientSettingsStorage.loadWorkspaceSettingsFrom(wsFile);
/* 273 */       PlatformInitUtils.reinitPlatform();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void saveWorkspace() {
/* 278 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 279 */     clientSettingsStorage.saveWorkspaceSettings();
/*     */   }
/*     */ 
/*     */   public static void saveAsWorkspace()
/*     */   {
/* 286 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 288 */     String wsFolder = clientSettingsStorage.getMyWorkspaceSettingsFolderPath();
/* 289 */     XMLFileFilter xmlFileFilter = new XMLFileFilter(LocalizationManager.getText("jforex.workspace.files"));
/*     */ 
/* 291 */     String workspaceName = clientSettingsStorage.getMyWorkspaceSettingsFilePath();
/*     */ 
/* 293 */     File userWs = new File(workspaceName);
/*     */ 
/* 295 */     JFileChooser chooser = DCFileChooser.createFileChooser(wsFolder, userWs, xmlFileFilter, 0);
/*     */ 
/* 297 */     WorkspaceOptionsPanel worspaceSettingsPanel = new WorkspaceOptionsPanel();
/* 298 */     worspaceSettingsPanel.getEnableWorkspaceAutoSaving().setSelected(false);
/* 299 */     worspaceSettingsPanel.getEnableWorkspaceSaveOnExit().setSelected(false);
/* 300 */     worspaceSettingsPanel.getWorkspaceAutoSavingPeriod().setEnabled(false);
/*     */ 
/* 302 */     chooser.add(worspaceSettingsPanel, "South");
/*     */ 
/* 304 */     ChooserSelectionWrapper selection = TransportFileChooser.showSaveDialog(FileType.WORKSPACE, chooser, (JFrame)GreedContext.get("clientGui"), userWs.getName());
/*     */ 
/* 306 */     if (selection != null)
/*     */     {
/* 308 */       FileItem f = selection.getFileItem();
/*     */ 
/* 311 */       Boolean saveOnExit = new Boolean(worspaceSettingsPanel.getEnableWorkspaceSaveOnExit().isSelected());
/* 312 */       Long autoSavePeriodInMinutes = Long.valueOf(0L);
/*     */ 
/* 314 */       JSpinner spinner = worspaceSettingsPanel.getWorkspaceAutoSavingPeriod();
/*     */ 
/* 316 */       if (worspaceSettingsPanel.getEnableWorkspaceAutoSaving().isSelected()) {
/*     */         try {
/* 318 */           spinner.commitEdit();
/*     */         }
/*     */         catch (Exception e) {
/* 321 */           ((JSpinner.NumberEditor)spinner.getEditor()).getTextField().setValue(spinner.getValue());
/*     */         }
/* 323 */         autoSavePeriodInMinutes = new Long(((Integer)spinner.getValue()).intValue());
/*     */       }
/*     */ 
/* 326 */       clientSettingsStorage.saveWorkspaceSettingsAs(f.getFileName(), autoSavePeriodInMinutes, saveOnExit);
/*     */ 
/* 328 */       Location loc = selection.getLocation();
/*     */ 
/* 330 */       if (loc != Location.LOCAL) {
/* 331 */         ByteArrayOutputStream os = null;
/*     */         try
/*     */         {
/* 334 */           os = (ByteArrayOutputStream)clientSettingsStorage.writeWorkspaceSettingsToOutputStream(os);
/*     */ 
/* 336 */           f.setFileData(os.toByteArray());
/*     */ 
/* 338 */           FeedDataProvider.getCurvesProtocolHandler().uploadFile(f, GreedContext.CLIENT_MODE, new FileProgressListener());
/*     */         }
/*     */         catch (Exception e) {
/* 341 */           LOGGER.error("Error saving workspace in remote storage: " + f.getFileName());
/*     */         } finally {
/* 343 */           IOUtils.closeQuietly(os);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void deleteSavedSettings()
/*     */   {
/* 351 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).deleteAllSettings();
/* 352 */     PlatformInitUtils.reinitPlatform();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.MouseController
 * JD-Core Version:    0.6.0
 */