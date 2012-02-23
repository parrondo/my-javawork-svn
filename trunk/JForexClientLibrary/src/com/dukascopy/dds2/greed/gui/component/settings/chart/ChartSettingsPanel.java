/*     */ package com.dukascopy.dds2.greed.gui.component.settings.chart;
/*     */ 
/*     */ import com.dukascopy.api.Filter;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.DailyFilter;
/*     */ import com.dukascopy.charts.settings.ChartSettings.GridType;
/*     */ import com.dukascopy.charts.settings.ChartSettings.LineConstructionMethod;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.AbstractSettingsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.dnd.DragGestureEvent;
/*     */ import java.awt.dnd.DragGestureListener;
/*     */ import java.awt.dnd.DragSource;
/*     */ import java.awt.dnd.DragSourceDragEvent;
/*     */ import java.awt.dnd.DragSourceDropEvent;
/*     */ import java.awt.dnd.DragSourceEvent;
/*     */ import java.awt.dnd.DragSourceListener;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseWheelEvent;
/*     */ import java.awt.event.MouseWheelListener;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.DefaultListModel;
/*     */ import javax.swing.DropMode;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JList.DropLocation;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSpinner;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.SpinnerModel;
/*     */ import javax.swing.SpinnerNumberModel;
/*     */ import javax.swing.TransferHandler;
/*     */ import javax.swing.TransferHandler.TransferSupport;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ 
/*     */ public class ChartSettingsPanel extends AbstractSettingsPanel
/*     */   implements ActionListener
/*     */ {
/*     */   private FilterEditorPanel filterPanel;
/*     */   private OrdersEditorPanel ordersPanel;
/*     */   private DailyFilterPanel dailyFilterPanel;
/*     */   private VisualizationEditorPanel visualizationPanel;
/*     */   private DrawingSequenceEditorPanel drawingSequencePanel;
/*     */ 
/*     */   public ChartSettingsPanel(SettingsTabbedFrame parent)
/*     */   {
/* 103 */     super(parent);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e) {
/* 107 */     this.parent.settingsChanged(true);
/*     */   }
/*     */ 
/*     */   public void applySettings()
/*     */   {
/* 112 */     apply(ChartSettings.Option.FILTER, this.filterPanel);
/*     */ 
/* 114 */     apply(ChartSettings.Option.DAILYFILTER, this.dailyFilterPanel);
/*     */ 
/* 116 */     apply(ChartSettings.Option.ENTRY_ORDERS, this.ordersPanel);
/* 117 */     apply(ChartSettings.Option.STOP_ORDERS, this.ordersPanel);
/* 118 */     apply(ChartSettings.Option.OPEN_POSITIONS, this.ordersPanel);
/* 119 */     apply(ChartSettings.Option.CLOSED_ORDERS, this.ordersPanel);
/* 120 */     apply(ChartSettings.Option.POSITIONS_LABELS, this.ordersPanel);
/*     */ 
/* 122 */     apply(ChartSettings.Option.GRID, this.visualizationPanel);
/* 123 */     apply(ChartSettings.Option.GRID_SIZE, this.visualizationPanel);
/* 124 */     apply(ChartSettings.Option.LAST_CANDLE_TRACKING, this.visualizationPanel);
/* 125 */     apply(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING, this.visualizationPanel);
/* 126 */     apply(ChartSettings.Option.PERIOD_SEPARATORS, this.visualizationPanel);
/* 127 */     apply(ChartSettings.Option.CANDLE_CANVAS, this.visualizationPanel);
/* 128 */     apply(ChartSettings.Option.RANDOW_DRAWINGS_COLOR, this.visualizationPanel);
/* 129 */     apply(ChartSettings.Option.LINE_CONSTRUCTION_METHOD, this.visualizationPanel);
/* 130 */     apply(ChartSettings.Option.POINT_AND_FIGURE_GRID_CONSTRUCTION, this.visualizationPanel);
/*     */ 
/* 132 */     apply(ChartSettings.Option.DRAWING_SEQUENCE_GRID, this.drawingSequencePanel);
/* 133 */     apply(ChartSettings.Option.DRAWING_SEQUENCE_PERIOD_SEPARATORS, this.drawingSequencePanel);
/* 134 */     apply(ChartSettings.Option.DRAWING_SEQUENCE_CANDLES, this.drawingSequencePanel);
/* 135 */     apply(ChartSettings.Option.DRAWING_SEQUENCE_INDICATORS, this.drawingSequencePanel);
/* 136 */     apply(ChartSettings.Option.DRAWING_SEQUENCE_DRAWINGS, this.drawingSequencePanel);
/* 137 */     apply(ChartSettings.Option.DRAWING_SEQUENCE_ORDERS, this.drawingSequencePanel);
/*     */   }
/*     */ 
/*     */   public void resetFields()
/*     */   {
/* 142 */     reset(ChartSettings.Option.FILTER, this.filterPanel);
/*     */ 
/* 144 */     reset(ChartSettings.Option.DAILYFILTER, this.dailyFilterPanel);
/*     */ 
/* 146 */     reset(ChartSettings.Option.ENTRY_ORDERS, this.ordersPanel);
/* 147 */     reset(ChartSettings.Option.STOP_ORDERS, this.ordersPanel);
/* 148 */     reset(ChartSettings.Option.OPEN_POSITIONS, this.ordersPanel);
/* 149 */     reset(ChartSettings.Option.CLOSED_ORDERS, this.ordersPanel);
/* 150 */     reset(ChartSettings.Option.POSITIONS_LABELS, this.ordersPanel);
/*     */ 
/* 152 */     reset(ChartSettings.Option.GRID, this.visualizationPanel);
/* 153 */     reset(ChartSettings.Option.GRID_SIZE, this.visualizationPanel);
/* 154 */     reset(ChartSettings.Option.LAST_CANDLE_TRACKING, this.visualizationPanel);
/* 155 */     reset(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING, this.visualizationPanel);
/* 156 */     reset(ChartSettings.Option.PERIOD_SEPARATORS, this.visualizationPanel);
/* 157 */     reset(ChartSettings.Option.CANDLE_CANVAS, this.visualizationPanel);
/* 158 */     reset(ChartSettings.Option.RANDOW_DRAWINGS_COLOR, this.visualizationPanel);
/* 159 */     reset(ChartSettings.Option.LINE_CONSTRUCTION_METHOD, this.visualizationPanel);
/* 160 */     reset(ChartSettings.Option.POINT_AND_FIGURE_GRID_CONSTRUCTION, this.visualizationPanel);
/*     */ 
/* 162 */     reset(ChartSettings.Option.DRAWING_SEQUENCE_GRID, this.drawingSequencePanel);
/* 163 */     reset(ChartSettings.Option.DRAWING_SEQUENCE_PERIOD_SEPARATORS, this.drawingSequencePanel);
/* 164 */     reset(ChartSettings.Option.DRAWING_SEQUENCE_CANDLES, this.drawingSequencePanel);
/* 165 */     reset(ChartSettings.Option.DRAWING_SEQUENCE_INDICATORS, this.drawingSequencePanel);
/* 166 */     reset(ChartSettings.Option.DRAWING_SEQUENCE_DRAWINGS, this.drawingSequencePanel);
/* 167 */     reset(ChartSettings.Option.DRAWING_SEQUENCE_ORDERS, this.drawingSequencePanel);
/*     */   }
/*     */ 
/*     */   public void resetToDefaults()
/*     */   {
/* 172 */     ChartSettings.resetOptions();
/*     */ 
/* 174 */     setDefault(ChartSettings.Option.FILTER, this.filterPanel);
/*     */ 
/* 176 */     setDefault(ChartSettings.Option.DAILYFILTER, this.dailyFilterPanel);
/*     */ 
/* 178 */     setDefault(ChartSettings.Option.STOP_ORDERS, this.ordersPanel);
/* 179 */     setDefault(ChartSettings.Option.ENTRY_ORDERS, this.ordersPanel);
/* 180 */     setDefault(ChartSettings.Option.OPEN_POSITIONS, this.ordersPanel);
/* 181 */     setDefault(ChartSettings.Option.CLOSED_ORDERS, this.ordersPanel);
/* 182 */     setDefault(ChartSettings.Option.POSITIONS_LABELS, this.ordersPanel);
/*     */ 
/* 184 */     setDefault(ChartSettings.Option.GRID, this.visualizationPanel);
/* 185 */     setDefault(ChartSettings.Option.GRID_SIZE, this.visualizationPanel);
/* 186 */     setDefault(ChartSettings.Option.LAST_CANDLE_TRACKING, this.visualizationPanel);
/* 187 */     setDefault(ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING, this.visualizationPanel);
/* 188 */     setDefault(ChartSettings.Option.PERIOD_SEPARATORS, this.visualizationPanel);
/* 189 */     setDefault(ChartSettings.Option.CANDLE_CANVAS, this.visualizationPanel);
/* 190 */     setDefault(ChartSettings.Option.RANDOW_DRAWINGS_COLOR, this.visualizationPanel);
/* 191 */     setDefault(ChartSettings.Option.LINE_CONSTRUCTION_METHOD, this.visualizationPanel);
/* 192 */     setDefault(ChartSettings.Option.POINT_AND_FIGURE_GRID_CONSTRUCTION, this.visualizationPanel);
/*     */ 
/* 194 */     setDefault(ChartSettings.Option.DRAWING_SEQUENCE_GRID, this.drawingSequencePanel);
/* 195 */     setDefault(ChartSettings.Option.DRAWING_SEQUENCE_PERIOD_SEPARATORS, this.drawingSequencePanel);
/* 196 */     setDefault(ChartSettings.Option.DRAWING_SEQUENCE_CANDLES, this.drawingSequencePanel);
/* 197 */     setDefault(ChartSettings.Option.DRAWING_SEQUENCE_INDICATORS, this.drawingSequencePanel);
/* 198 */     setDefault(ChartSettings.Option.DRAWING_SEQUENCE_DRAWINGS, this.drawingSequencePanel);
/* 199 */     setDefault(ChartSettings.Option.DRAWING_SEQUENCE_ORDERS, this.drawingSequencePanel);
/*     */   }
/*     */ 
/*     */   private void setDefault(ChartSettings.Option option, IChartSettingsEditor editor) {
/* 203 */     Object value = ChartSettings.get(option);
/* 204 */     editor.set(option, value);
/*     */   }
/*     */ 
/*     */   public boolean verifySettings()
/*     */   {
/* 209 */     return true;
/*     */   }
/*     */ 
/*     */   protected void build() {
/* 213 */     setLayout(new GridBagLayout());
/*     */ 
/* 215 */     this.filterPanel = new FilterEditorPanel("border.ticks.candles.filtration", this);
/* 216 */     this.dailyFilterPanel = new DailyFilterPanel("border.daily.filtration", this);
/* 217 */     this.ordersPanel = new OrdersEditorPanel("border.orders.visualization", this);
/* 218 */     this.visualizationPanel = new VisualizationEditorPanel("border.chart.option", this);
/* 219 */     this.drawingSequencePanel = new DrawingSequenceEditorPanel("border.drawing.sequence", this);
/*     */ 
/* 221 */     GridBagConstraints gbc = new GridBagConstraints();
/* 222 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, 1, 10, gbc, this, this.filterPanel);
/* 223 */     GridBagLayoutHelper.add(0, 1, 1.0D, 1.0D, 1, 1, 0, 0, 0, 0, 1, 10, gbc, this, this.dailyFilterPanel);
/* 224 */     GridBagLayoutHelper.add(0, 3, 1.0D, 1.0D, 1, 1, 0, 0, 0, 0, 1, 10, gbc, this, this.ordersPanel);
/* 225 */     GridBagLayoutHelper.add(1, 0, 1.0D, 1.0D, 1, 2, 0, 0, 0, 0, 1, 10, gbc, this, this.visualizationPanel);
/* 226 */     GridBagLayoutHelper.add(1, 2, 1.0D, 1.0D, 1, 2, 0, 0, 0, 0, 1, 10, gbc, this, this.drawingSequencePanel);
/*     */   }
/*     */ 
/*     */   private void apply(ChartSettings.Option option, IChartSettingsEditor editor) {
/* 230 */     Object value = editor.get(option);
/* 231 */     ChartSettings.set(option, value);
/*     */ 
/* 233 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).save(option, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   private void reset(ChartSettings.Option option, IChartSettingsEditor editor) {
/* 237 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 238 */     String value = storage.load(option);
/* 239 */     value = value == null ? String.valueOf(ChartSettings.get(option)) : value;
/* 240 */     editor.set(option, ChartSettings.valueOf(option, value));
/*     */   }
/*     */   private static final class DrawingSequenceEditorPanel extends ChartSettingsPanel.EditorPanel {
/* 743 */     private final ChartSettings.Option[] optionSequence = new ChartSettings.Option[6];
/*     */     private final JList drawingSequenceList;
/*     */ 
/*     */     public DrawingSequenceEditorPanel(String title, ActionListener actionListener) {
/* 747 */       super(actionListener);
/* 748 */       setName("DrawingSequenceEditorPanel");
/*     */ 
/* 750 */       this.drawingSequenceList = new JList(new DefaultListModel());
/* 751 */       this.drawingSequenceList.setCellRenderer(getListCellRenderer());
/* 752 */       this.drawingSequenceList.setDragEnabled(true);
/* 753 */       this.drawingSequenceList.setDropMode(DropMode.INSERT);
/* 754 */       this.drawingSequenceList.setTransferHandler(new DrawingSequenceListDropHandler(this.drawingSequenceList));
/* 755 */       new DrawingSequenceDragListener(this.drawingSequenceList);
/*     */ 
/* 757 */       ChartSettingsPanel.access$1600(this, new JScrollPane(this.drawingSequenceList), 0, 0, 1, 1, 2, 1, 17, 1);
/*     */     }
/*     */ 
/*     */     private void updateList() {
/* 761 */       DefaultListModel model = (DefaultListModel)this.drawingSequenceList.getModel();
/* 762 */       model.clear();
/* 763 */       for (ChartSettings.Option opt : this.optionSequence)
/* 764 */         model.addElement(opt);
/*     */     }
/*     */ 
/*     */     private ListCellRenderer getListCellRenderer()
/*     */     {
/* 769 */       return new ListCellRenderer() {
/*     */         JPanel cellPanel;
/*     */         JLocalizableLabel label;
/*     */         JLabel numLabel;
/*     */ 
/* 777 */         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) { if (!(value instanceof ChartSettings.Option)) {
/* 778 */             return null;
/*     */           }
/*     */ 
/* 781 */           ChartSettings.Option option = (ChartSettings.Option)value;
/*     */           String localizationKey;
/* 784 */           switch (ChartSettingsPanel.1.$SwitchMap$com$dukascopy$charts$settings$ChartSettings$Option[option.ordinal()]) {
/*     */           case 6:
/* 786 */             localizationKey = "drawing.sequence.value.grid";
/* 787 */             break;
/*     */           case 7:
/* 789 */             localizationKey = "drawing.sequence.value.period.separators";
/* 790 */             break;
/*     */           case 8:
/* 792 */             localizationKey = "drawing.sequence.value.candles";
/* 793 */             break;
/*     */           case 9:
/* 795 */             localizationKey = "drawing.sequence.value.indicators";
/* 796 */             break;
/*     */           case 10:
/* 798 */             localizationKey = "drawing.sequence.value.drawings";
/* 799 */             break;
/*     */           case 11:
/* 801 */             localizationKey = "drawing.sequence.value.orders";
/* 802 */             break;
/*     */           default:
/* 804 */             throw new IllegalStateException();
/*     */           }
/*     */ 
/* 807 */           this.cellPanel = getCellPanel();
/*     */ 
/* 809 */           this.numLabel.setText(String.valueOf(index + 1));
/* 810 */           this.label.setText(localizationKey);
/*     */ 
/* 814 */           if (isSelected) {
/* 815 */             this.cellPanel.setBackground(list.getSelectionBackground());
/* 816 */             this.cellPanel.setForeground(list.getSelectionForeground());
/*     */           }
/*     */           else {
/* 819 */             this.cellPanel.setBackground(list.getBackground());
/* 820 */             this.cellPanel.setForeground(list.getForeground());
/*     */           }
/*     */ 
/* 823 */           return this.cellPanel; }
/*     */ 
/*     */         private JPanel getCellPanel()
/*     */         {
/* 827 */           if (null == this.cellPanel) {
/* 828 */             this.numLabel = new JLabel();
/* 829 */             this.label = new JLocalizableLabel();
/*     */ 
/* 831 */             this.cellPanel = new JPanel(new FlowLayout(0, 15, 2));
/* 832 */             this.cellPanel.add(this.numLabel);
/* 833 */             this.cellPanel.add(this.label);
/* 834 */             this.cellPanel.setOpaque(true);
/*     */           }
/* 836 */           return this.cellPanel;
/*     */         } } ;
/*     */     }
/*     */ 
/*     */     protected void update(ChartSettings.Option option) {
/* 842 */       Object optionValue = this.options.get(option);
/* 843 */       if (!(optionValue instanceof Integer)) {
/* 844 */         return;
/*     */       }
/* 846 */       int position = ((Integer)optionValue).intValue();
/* 847 */       this.optionSequence[position] = option;
/*     */ 
/* 849 */       updateList();
/*     */     }
/*     */ 
/*     */     class DrawingSequenceListDropHandler extends TransferHandler
/*     */     {
/*     */       JList list;
/*     */ 
/*     */       public DrawingSequenceListDropHandler(JList list)
/*     */       {
/* 890 */         this.list = list;
/*     */       }
/*     */ 
/*     */       public boolean canImport(TransferHandler.TransferSupport support) {
/* 894 */         if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
/* 895 */           return false;
/*     */         }
/* 897 */         JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
/*     */ 
/* 899 */         return dl.getIndex() != -1;
/*     */       }
/*     */ 
/*     */       public boolean importData(TransferHandler.TransferSupport support)
/*     */       {
/* 906 */         if (!canImport(support)) {
/* 907 */           return false;
/* 910 */         }
/*     */ Transferable transferable = support.getTransferable();
/*     */         String indexString;
/*     */         try { indexString = (String)transferable.getTransferData(DataFlavor.stringFlavor);
/*     */         } catch (Exception e) {
/* 915 */           return false;
/*     */         }
/*     */ 
/* 918 */         int index = Integer.parseInt(indexString);
/* 919 */         JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();
/* 920 */         int dropTargetIndex = dl.getIndex();
/*     */ 
/* 922 */         if ((index == dropTargetIndex) || (index == dropTargetIndex - 1)) {
/* 923 */           return false;
/*     */         }
/*     */ 
/* 926 */         DefaultListModel model = (DefaultListModel)this.list.getModel();
/* 927 */         ChartSettings.Option option = (ChartSettings.Option)model.getElementAt(index);
/* 928 */         model.removeElementAt(index);
/* 929 */         if (index < dropTargetIndex) {
/* 930 */           model.insertElementAt(option, dropTargetIndex - 1);
/* 931 */           this.list.setSelectedIndex(dropTargetIndex - 1);
/*     */         } else {
/* 933 */           model.insertElementAt(option, dropTargetIndex);
/* 934 */           this.list.setSelectedIndex(dropTargetIndex);
/*     */         }
/*     */ 
/* 937 */         updateOptionValues();
/* 938 */         return true;
/*     */       }
/*     */       private void updateOptionValues() {
/* 941 */         DefaultListModel model = (DefaultListModel)ChartSettingsPanel.DrawingSequenceEditorPanel.this.drawingSequenceList.getModel();
/* 942 */         for (int i = 0; i < model.getSize(); i++) {
/* 943 */           ChartSettings.Option option = (ChartSettings.Option)model.getElementAt(i);
/* 944 */           ChartSettingsPanel.DrawingSequenceEditorPanel.this.optionSequence[i] = option;
/* 945 */           ChartSettingsPanel.DrawingSequenceEditorPanel.this.options.put(option, Integer.valueOf(i));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     class DrawingSequenceDragListener
/*     */       implements DragSourceListener, DragGestureListener
/*     */     {
/*     */       final JList list;
/* 857 */       DragSource ds = new DragSource();
/*     */ 
/*     */       public DrawingSequenceDragListener(JList list) {
/* 860 */         this.list = list;
/* 861 */         this.ds.createDefaultDragGestureRecognizer(list, 2, this);
/*     */       }
/*     */ 
/*     */       public void dragGestureRecognized(DragGestureEvent dge) {
/* 865 */         StringSelection transferable = new StringSelection(Integer.toString(this.list.getSelectedIndex()));
/* 866 */         this.ds.startDrag(dge, DragSource.DefaultCopyDrop, transferable, this);
/* 867 */         ChartSettingsPanel.DrawingSequenceEditorPanel.this.actionListener.actionPerformed(new ActionEvent(this.list, 1001, null));
/*     */       }
/*     */ 
/*     */       public void dragEnter(DragSourceDragEvent dsde)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void dragExit(DragSourceEvent dse)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void dragOver(DragSourceDragEvent dsde)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void dragDropEnd(DragSourceDropEvent dsde)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void dropActionChanged(DragSourceDragEvent dsde)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class VisualizationEditorPanel extends ChartSettingsPanel.EditorPanel
/*     */   {
/*     */     private static final int ROW_HEIGHT = 24;
/*     */     private final JRadioButton[] radioButtons;
/*     */     private final ButtonGroup[] buttonGroups;
/*     */     private final GridSizeSpinner gridSizeSpinner;
/*     */     private final JLocalizableLabel gridSizeLabel;
/*     */     private JComboBox cmbLineConstructionMethod;
/* 476 */     private Map<ChartSettings.Option, JCheckBox> checkBoxes = new EnumMap(ChartSettings.Option.class);
/*     */ 
/*     */     public VisualizationEditorPanel(String title, ActionListener actionListener) {
/* 479 */       super(actionListener);
/* 480 */       setName("VisualizationEditorPanel");
/*     */ 
/* 482 */       this.radioButtons = new JRadioButton[] { createRadioButton("radio.grid.static", ChartSettings.Option.GRID, ChartSettings.GridType.STATIC), createRadioButton("radio.grid.pip", ChartSettings.Option.GRID, ChartSettings.GridType.PIP), createRadioButton("radio.throughout.the.chart", ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING, Boolean.TRUE), createRadioButton("radio.from.last.price", ChartSettings.Option.THROUGHOUT_LAST_CANDLE_TRACKING, Boolean.FALSE) };
/*     */ 
/* 491 */       this.buttonGroups = new ButtonGroup[] { new ButtonGroup()
/*     */       {
/*     */       }
/*     */       , new ButtonGroup()
/*     */       {
/*     */       }
/*     */        };
/* 502 */       int row = 0;
/*     */ 
/* 505 */       JCheckBox gridCheckBox = new JLocalizableCheckBox("check.show.grid")
/*     */       {
/*     */       };
/* 519 */       this.gridSizeSpinner = new GridSizeSpinner();
/* 520 */       this.gridSizeLabel = new JLocalizableLabel("label.gridsize");
/*     */ 
/* 522 */       JPanel subPanel = new JPanel(new FlowLayout(0));
/* 523 */       subPanel.add(gridCheckBox);
/* 524 */       subPanel.add(this.gridSizeSpinner);
/* 525 */       subPanel.add(this.gridSizeLabel);
/* 526 */       subPanel.setPreferredSize(new Dimension(subPanel.getPreferredSize().width, 24));
/*     */ 
/* 528 */       ChartSettingsPanel.access$600(this, subPanel, 0, row++, 1, 1, 1, 1, 17, 2);
/*     */ 
/* 530 */       JCheckBox pointAndFigureGridCheckBox = createCheckBox("check.point.and.figure.grid", ChartSettings.Option.POINT_AND_FIGURE_GRID_CONSTRUCTION);
/* 531 */       this.checkBoxes.put(ChartSettings.Option.POINT_AND_FIGURE_GRID_CONSTRUCTION, pointAndFigureGridCheckBox);
/*     */ 
/* 533 */       subPanel = new JPanel(new FlowLayout(0));
/* 534 */       subPanel.add(this.radioButtons[0]);
/* 535 */       subPanel.add(this.radioButtons[1]);
/* 536 */       subPanel.add(pointAndFigureGridCheckBox);
/* 537 */       subPanel.setPreferredSize(new Dimension(subPanel.getPreferredSize().width, 24));
/*     */ 
/* 539 */       ChartSettingsPanel.access$700(this, subPanel, 0, row++, 0, 1, 1, 1, 17, 0, 0, 0, 17, 2);
/*     */ 
/* 546 */       JCheckBox checkBox = createCheckBox("check.show.last.candle", ChartSettings.Option.LAST_CANDLE_TRACKING);
/* 547 */       checkBox.addActionListener(new ActionListener() {
/*     */         public void actionPerformed(ActionEvent e) {
/* 549 */           ChartSettingsPanel.VisualizationEditorPanel.this.update(ChartSettings.Option.LAST_CANDLE_TRACKING);
/*     */         }
/*     */       });
/* 553 */       this.checkBoxes.put(ChartSettings.Option.LAST_CANDLE_TRACKING, checkBox);
/* 554 */       subPanel = new JPanel(new FlowLayout(0));
/* 555 */       subPanel.add(checkBox);
/* 556 */       subPanel.setPreferredSize(new Dimension(subPanel.getPreferredSize().width, 24));
/*     */ 
/* 558 */       ChartSettingsPanel.access$800(this, subPanel, 0, row++, 1, 1, 1, 1, 17, 2);
/*     */ 
/* 560 */       subPanel = new JPanel(new FlowLayout(0));
/* 561 */       subPanel.add(this.radioButtons[2]);
/* 562 */       subPanel.add(this.radioButtons[3]);
/* 563 */       subPanel.setPreferredSize(new Dimension(subPanel.getPreferredSize().width, 24));
/*     */ 
/* 565 */       ChartSettingsPanel.access$900(this, subPanel, 0, row++, 0, 1, 1, 1, 17, 0, 0, 0, 17, 2);
/*     */ 
/* 570 */       checkBox = createCheckBox("check.show.period.separators", ChartSettings.Option.PERIOD_SEPARATORS);
/* 571 */       this.checkBoxes.put(ChartSettings.Option.PERIOD_SEPARATORS, checkBox);
/*     */ 
/* 573 */       ChartSettingsPanel.access$1000(this, checkBox, 0, row++, 1, 1, 2, 1, 17, 2);
/*     */ 
/* 575 */       checkBox = createCheckBox("check.show.candle.canvas", ChartSettings.Option.CANDLE_CANVAS);
/* 576 */       this.checkBoxes.put(ChartSettings.Option.CANDLE_CANVAS, checkBox);
/* 577 */       ChartSettingsPanel.access$1100(this, checkBox, 0, row++, 1, 1, 2, 1, 17, 2);
/*     */ 
/* 579 */       checkBox = createCheckBox("check.drawings.random.color", ChartSettings.Option.RANDOW_DRAWINGS_COLOR);
/* 580 */       this.checkBoxes.put(ChartSettings.Option.RANDOW_DRAWINGS_COLOR, checkBox);
/* 581 */       ChartSettingsPanel.access$1200(this, checkBox, 0, row++, 1, 1, 2, 1, 17, 2);
/*     */ 
/* 584 */       subPanel = new JPanel(new FlowLayout(0));
/* 585 */       subPanel.add(new JLocalizableLabel("label.line.construction.method"));
/* 586 */       subPanel.add(getCmbLineConstructionMethod());
/*     */ 
/* 588 */       ChartSettingsPanel.access$1300(this, subPanel, 0, row++, 1, 1, 2, 1, 17, 2);
/*     */     }
/*     */ 
/*     */     protected void update(ChartSettings.Option option) {
/* 592 */       Object optionValue = this.options.get(option);
/* 593 */       JCheckBox checkBox = (JCheckBox)this.checkBoxes.get(option);
/*     */ 
/* 595 */       switch (ChartSettingsPanel.1.$SwitchMap$com$dukascopy$charts$settings$ChartSettings$Option[option.ordinal()]) {
/*     */       case 1:
/* 597 */         ChartSettings.GridType gridType = (ChartSettings.GridType)optionValue;
/*     */ 
/* 599 */         boolean showGrid = gridType != ChartSettings.GridType.NONE;
/* 600 */         checkBox.setSelected(showGrid);
/* 601 */         this.radioButtons[0].setEnabled(showGrid);
/* 602 */         this.radioButtons[1].setEnabled(showGrid);
/*     */ 
/* 604 */         switch (ChartSettingsPanel.1.$SwitchMap$com$dukascopy$charts$settings$ChartSettings$GridType[gridType.ordinal()]) {
/*     */         case 1:
/* 606 */           this.buttonGroups[0].clearSelection();
/* 607 */           this.gridSizeLabel.setText("label.gridsize");
/* 608 */           break;
/*     */         case 2:
/* 610 */           this.radioButtons[0].setSelected(true);
/* 611 */           this.gridSizeLabel.setText("label.gridsize.px");
/* 612 */           break;
/*     */         case 3:
/* 614 */           this.radioButtons[1].setSelected(true);
/* 615 */           this.gridSizeLabel.setText("label.gridsize.pip");
/*     */         }
/*     */ 
/* 619 */         this.gridSizeSpinner.setGridType(gridType);
/* 620 */         break;
/*     */       case 2:
/* 622 */         this.gridSizeSpinner.setValue(optionValue);
/* 623 */         break;
/*     */       case 3:
/* 625 */         getCmbLineConstructionMethod().setSelectedItem(optionValue);
/* 626 */         break;
/*     */       case 4:
/* 628 */         boolean selected = checkBox.isSelected();
/* 629 */         this.radioButtons[2].setEnabled(selected);
/* 630 */         this.radioButtons[3].setEnabled(selected);
/* 631 */         break;
/*     */       case 5:
/* 633 */         this.radioButtons[3].setSelected(true);
/* 634 */         break;
/*     */       default:
/* 636 */         checkBox.setSelected(optionValue == null ? false : ((Boolean)optionValue).booleanValue());
/*     */       }
/*     */     }
/*     */ 
/*     */     private JComboBox getCmbLineConstructionMethod()
/*     */     {
/* 700 */       if (this.cmbLineConstructionMethod == null) {
/* 701 */         this.cmbLineConstructionMethod = new JComboBox();
/* 702 */         for (ChartSettings.LineConstructionMethod method : ChartSettings.LineConstructionMethod.values()) {
/* 703 */           this.cmbLineConstructionMethod.addItem(method);
/*     */         }
/*     */ 
/* 706 */         this.cmbLineConstructionMethod.setRenderer(new DefaultListCellRenderer() {
/*     */           public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 708 */             Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*     */ 
/* 710 */             if ((value instanceof ChartSettings.LineConstructionMethod)) {
/* 711 */               ChartSettings.LineConstructionMethod method = (ChartSettings.LineConstructionMethod)value;
/* 712 */               JLabel label = new JLabel();
/* 713 */               label.setText(LocalizationManager.getText(method.getCaptionKey()));
/* 714 */               label.setOpaque(true);
/* 715 */               label.setForeground(comp.getForeground());
/* 716 */               label.setBackground(comp.getBackground());
/* 717 */               return label;
/*     */             }
/* 719 */             return comp;
/*     */           }
/*     */         });
/* 723 */         this.cmbLineConstructionMethod.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 726 */             ChartSettingsPanel.VisualizationEditorPanel.this.options.put(ChartSettings.Option.LINE_CONSTRUCTION_METHOD, ChartSettingsPanel.VisualizationEditorPanel.this.getCmbLineConstructionMethod().getSelectedItem());
/*     */           }
/*     */         });
/* 730 */         this.cmbLineConstructionMethod.addActionListener(this.actionListener);
/*     */       }
/*     */ 
/* 733 */       return this.cmbLineConstructionMethod;
/*     */     }
/*     */ 
/*     */     private class GridSizeSpinner extends JSpinner
/*     */     {
/*     */       final Map<ChartSettings.GridType, SpinnerNumberModel> spinnerModels;
/*     */ 
/*     */       public GridSizeSpinner()
/*     */       {
/* 646 */         this.spinnerModels = new EnumMap(ChartSettings.GridType.class, ChartSettingsPanel.VisualizationEditorPanel.this)
/*     */         {
/*     */         };
/* 651 */         addChangeListener(new ChangeListener(ChartSettingsPanel.VisualizationEditorPanel.this)
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/* 654 */             ChartSettingsPanel.VisualizationEditorPanel.this.actionListener.actionPerformed(null);
/*     */           }
/*     */         });
/* 658 */         addMouseWheelListener(new Object(ChartSettingsPanel.VisualizationEditorPanel.this)
/*     */         {
/*     */           public void mouseWheelMoved(MouseWheelEvent e) {
/* 661 */             int unitsToScroll = e.getUnitsToScroll();
/*     */ 
/* 663 */             Object value = null;
/* 664 */             if (unitsToScroll > 0) {
/* 665 */               value = ChartSettingsPanel.VisualizationEditorPanel.GridSizeSpinner.this.getModel().getNextValue();
/*     */             }
/* 667 */             else if (unitsToScroll < 0) {
/* 668 */               value = ChartSettingsPanel.VisualizationEditorPanel.GridSizeSpinner.this.getModel().getPreviousValue();
/*     */             }
/*     */ 
/* 671 */             if (value != null)
/* 672 */               ChartSettingsPanel.VisualizationEditorPanel.GridSizeSpinner.this.setValue(value);
/*     */           }
/*     */         });
/*     */       }
/*     */ 
/*     */       public void setGridType(ChartSettings.GridType gridType) {
/* 679 */         if (gridType == ChartSettings.GridType.NONE) {
/* 680 */           setEnabled(false);
/* 681 */           ChartSettingsPanel.VisualizationEditorPanel.this.gridSizeLabel.setEnabled(false);
/* 682 */           return;
/*     */         }
/* 684 */         SpinnerNumberModel numberModel = (SpinnerNumberModel)this.spinnerModels.get(gridType);
/* 685 */         setModel(numberModel);
/* 686 */         setValue(numberModel.getMinimum());
/* 687 */         setEnabled(true);
/* 688 */         ChartSettingsPanel.VisualizationEditorPanel.this.gridSizeLabel.setEnabled(true);
/*     */       }
/*     */ 
/*     */       public void setValue(Object value)
/*     */       {
/* 694 */         super.setValue(value);
/* 695 */         ChartSettingsPanel.VisualizationEditorPanel.this.options.put(ChartSettings.Option.GRID_SIZE, (Integer)value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class OrdersEditorPanel extends ChartSettingsPanel.EditorPanel
/*     */   {
/* 418 */     private Map<ChartSettings.Option, JCheckBox> checkBoxes = new EnumMap(ChartSettings.Option.class);
/*     */ 
/*     */     public OrdersEditorPanel(String title, ActionListener actionListener) {
/* 421 */       super(actionListener);
/* 422 */       setName("OrdersEditorPanel");
/*     */ 
/* 425 */       JCheckBox checkBox = createCheckBox("check.entry.orders", ChartSettings.Option.ENTRY_ORDERS);
/* 426 */       this.checkBoxes.put(ChartSettings.Option.ENTRY_ORDERS, checkBox);
/* 427 */       add(0, checkBox);
/*     */ 
/* 430 */       JCheckBox checkBox = createCheckBox("check.stop.orders", ChartSettings.Option.STOP_ORDERS);
/* 431 */       this.checkBoxes.put(ChartSettings.Option.STOP_ORDERS, checkBox);
/* 432 */       add(1, checkBox);
/*     */ 
/* 435 */       JCheckBox checkBox = createCheckBox("check.open.positions", ChartSettings.Option.OPEN_POSITIONS);
/* 436 */       this.checkBoxes.put(ChartSettings.Option.OPEN_POSITIONS, checkBox);
/* 437 */       ChartSettingsPanel.access$100(this, checkBox, 1, 0, 1, 0, 17, 0);
/*     */ 
/* 440 */       JCheckBox checkBox = createCheckBox("check.closed.positions", ChartSettings.Option.CLOSED_ORDERS);
/* 441 */       this.checkBoxes.put(ChartSettings.Option.CLOSED_ORDERS, checkBox);
/* 442 */       ChartSettingsPanel.access$200(this, checkBox, 1, 1, 1, 0, 17, 0);
/*     */ 
/* 445 */       JCheckBox checkBox = createCheckBox("check.show.positions.labels", ChartSettings.Option.POSITIONS_LABELS);
/* 446 */       this.checkBoxes.put(ChartSettings.Option.POSITIONS_LABELS, checkBox);
/* 447 */       ChartSettingsPanel.access$300(this, checkBox, 0, 2, 1, 0, 17, 0);
/*     */     }
/*     */ 
/*     */     protected void update(ChartSettings.Option option)
/*     */     {
/* 452 */       Object optionsValue = this.options.get(option);
/*     */ 
/* 454 */       JCheckBox checkBox = (JCheckBox)this.checkBoxes.get(option);
/* 455 */       if (optionsValue != null)
/* 456 */         checkBox.setSelected(((Boolean)optionsValue).booleanValue());
/*     */       else
/* 458 */         checkBox.setSelected(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class DailyFilterPanel extends ChartSettingsPanel.EditorPanel
/*     */   {
/* 371 */     private final Map<ChartSettings.DailyFilter, JRadioButton> radioButtons = new HashMap();
/*     */ 
/*     */     public DailyFilterPanel(String title, ActionListener actionListener) {
/* 374 */       super(actionListener);
/* 375 */       setName("FilterEditorPanel");
/*     */ 
/* 377 */       this.radioButtons.put(ChartSettings.DailyFilter.SUNDAY_IN_MONDAY, createRadioButton("radio.dailyfilter.sunday.in.monday", ChartSettings.Option.DAILYFILTER, ChartSettings.DailyFilter.SUNDAY_IN_MONDAY));
/*     */ 
/* 381 */       this.radioButtons.put(ChartSettings.DailyFilter.NONE, createRadioButton("radio.dailyfilter.no.filter", ChartSettings.Option.DAILYFILTER, ChartSettings.DailyFilter.NONE));
/*     */ 
/* 385 */       this.radioButtons.put(ChartSettings.DailyFilter.SKIP_SUNDAY, createRadioButton("radio.dailyfilter.skip.sunday", ChartSettings.Option.DAILYFILTER, ChartSettings.DailyFilter.SKIP_SUNDAY));
/*     */ 
/* 390 */       ButtonGroup buttonGroup = new ButtonGroup();
/*     */ 
/* 393 */       JRadioButton radioButton = (JRadioButton)this.radioButtons.get(ChartSettings.DailyFilter.SUNDAY_IN_MONDAY);
/* 394 */       buttonGroup.add(radioButton);
/* 395 */       add(0, radioButton);
/*     */ 
/* 398 */       JRadioButton radioButton = (JRadioButton)this.radioButtons.get(ChartSettings.DailyFilter.SKIP_SUNDAY);
/* 399 */       buttonGroup.add(radioButton);
/* 400 */       add(1, radioButton);
/*     */ 
/* 403 */       JRadioButton radioButton = (JRadioButton)this.radioButtons.get(ChartSettings.DailyFilter.NONE);
/* 404 */       buttonGroup.add(radioButton);
/* 405 */       add(2, radioButton);
/*     */     }
/*     */ 
/*     */     protected void update(ChartSettings.Option option)
/*     */     {
/* 410 */       ((JRadioButton)this.radioButtons.get(this.options.get(option))).setSelected(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class FilterEditorPanel extends ChartSettingsPanel.EditorPanel
/*     */   {
/* 324 */     private final Map<Filter, JRadioButton> radioButtons = new EnumMap(Filter.class);
/*     */ 
/*     */     public FilterEditorPanel(String title, ActionListener actionListener) {
/* 327 */       super(actionListener);
/* 328 */       setName("FilterEditorPanel");
/*     */ 
/* 330 */       this.radioButtons.put(Filter.WEEKENDS, createRadioButton("radio.filter.weekend.candles", ChartSettings.Option.FILTER, Filter.WEEKENDS));
/*     */ 
/* 334 */       this.radioButtons.put(Filter.ALL_FLATS, createRadioButton("radio.filter.flat.candles", ChartSettings.Option.FILTER, Filter.ALL_FLATS));
/*     */ 
/* 338 */       this.radioButtons.put(Filter.NO_FILTER, createRadioButton("radio.filter.is.disabled", ChartSettings.Option.FILTER, Filter.NO_FILTER));
/*     */ 
/* 343 */       ButtonGroup buttonGroup = new ButtonGroup();
/*     */ 
/* 346 */       JRadioButton radioButton = (JRadioButton)this.radioButtons.get(Filter.WEEKENDS);
/* 347 */       buttonGroup.add(radioButton);
/* 348 */       add(0, radioButton);
/*     */ 
/* 351 */       JRadioButton radioButton = (JRadioButton)this.radioButtons.get(Filter.ALL_FLATS);
/* 352 */       buttonGroup.add(radioButton);
/* 353 */       add(1, radioButton);
/*     */ 
/* 356 */       JRadioButton radioButton = (JRadioButton)this.radioButtons.get(Filter.NO_FILTER);
/* 357 */       buttonGroup.add(radioButton);
/* 358 */       add(2, radioButton);
/*     */     }
/*     */ 
/*     */     protected void update(ChartSettings.Option option)
/*     */     {
/* 363 */       ((JRadioButton)this.radioButtons.get(this.options.get(option))).setSelected(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class EditorPanel extends JPanel
/*     */     implements ChartSettingsPanel.IChartSettingsEditor
/*     */   {
/*     */     protected final JRoundedBorder border;
/*     */     protected final ActionListener actionListener;
/* 253 */     protected final Map<ChartSettings.Option, Object> options = new EnumMap(ChartSettings.Option.class);
/*     */ 
/*     */     protected EditorPanel(String title, ActionListener actionListener) {
/* 256 */       this.actionListener = actionListener;
/*     */ 
/* 258 */       this.border = new JLocalizableRoundedBorder(this, title);
/* 259 */       setBorder(this.border);
/* 260 */       setLayout(new GridBagLayout());
/*     */     }
/*     */ 
/*     */     public final void set(ChartSettings.Option option, Object value) {
/* 264 */       this.options.put(option, value);
/* 265 */       update(option);
/*     */     }
/*     */     protected abstract void update(ChartSettings.Option paramOption);
/*     */ 
/*     */     public final Object get(ChartSettings.Option option) {
/* 271 */       return this.options.get(option);
/*     */     }
/*     */ 
/*     */     protected void add(int row, Component component) {
/* 275 */       ChartSettingsPanel.access$000(this, component, 0, row, 1, 0, 17, 0);
/*     */     }
/*     */ 
/*     */     protected JLocalizableRadioButton createRadioButton(String label, ChartSettings.Option option, Object value)
/*     */     {
/* 283 */       return new JLocalizableRadioButton(label, label, option, value)
/*     */       {
/*     */       };
/*     */     }
/*     */ 
/*     */     protected JLocalizableCheckBox createCheckBox(String label, ChartSettings.Option option)
/*     */     {
/* 304 */       return new JLocalizableCheckBox(label, option)
/*     */       {
/*     */       };
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface IChartSettingsEditor
/*     */   {
/*     */     public abstract void set(ChartSettings.Option paramOption, Object paramObject);
/*     */ 
/*     */     public abstract Object get(ChartSettings.Option paramOption);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.settings.chart.ChartSettingsPanel
 * JD-Core Version:    0.6.0
 */