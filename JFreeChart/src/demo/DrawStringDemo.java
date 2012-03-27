package demo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.FontChooserPanel;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

public class DrawStringDemo extends ApplicationFrame
  implements ActionListener, ChangeListener
{
  private JComboBox combo1;
  private JComboBox combo2;
  private JComboBox combo3;
  private JSlider rotation;
  private DrawStringPanel drawStringPanel1;
  private DrawStringPanel drawStringPanel2;

  public DrawStringDemo(String paramString)
  {
    super(paramString);
    setContentPane(createContentPane());
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    if (paramActionEvent.getActionCommand().equals("fontButton.clicked"))
      displayFontDialog();
    if (paramActionEvent.getActionCommand().equals("combo1.changed"))
      handleCombo1Change();
    if (paramActionEvent.getActionCommand().equals("combo2.changed"))
      handleCombo2Change();
    if (!(paramActionEvent.getActionCommand().equals("combo3.changed")))
      return;
    handleCombo3Change();
  }

  public void stateChanged(ChangeEvent paramChangeEvent)
  {
    int i = this.rotation.getValue();
    double d = 6.283185307179586D * i / 360.0D;
    this.drawStringPanel2.setAngle(d);
    this.drawStringPanel2.invalidate();
    this.drawStringPanel2.repaint();
  }

  private void handleCombo1Change()
  {
    String str = this.combo1.getSelectedItem().toString();
    this.drawStringPanel1.setAnchor(convertStringToAnchor(str));
    this.drawStringPanel1.invalidate();
    this.drawStringPanel1.repaint();
  }

  private void handleCombo2Change()
  {
    String str = this.combo2.getSelectedItem().toString();
    this.drawStringPanel2.setAnchor(convertStringToAnchor(str));
    this.drawStringPanel2.invalidate();
    this.drawStringPanel2.repaint();
  }

  private void handleCombo3Change()
  {
    String str = this.combo3.getSelectedItem().toString();
    this.drawStringPanel2.setRotationAnchor(convertStringToAnchor(str));
    this.drawStringPanel2.invalidate();
    this.drawStringPanel2.repaint();
  }

  private JPanel createContentPane()
  {
    JPanel localJPanel = new JPanel(new BorderLayout());
    JTabbedPane localJTabbedPane = new JTabbedPane();
    localJTabbedPane.add("Alignment", createTab1Content());
    localJTabbedPane.add("Rotation", createTab2Content());
    localJPanel.add(localJTabbedPane);
    return localJPanel;
  }

  private JPanel createTab1Content()
  {
    JPanel localJPanel1 = new JPanel(new BorderLayout());
    this.combo1 = new JComboBox();
    this.combo1.setActionCommand("combo1.changed");
    populateTextAnchorCombo(this.combo1);
    this.combo1.addActionListener(this);
    JPanel localJPanel2 = new JPanel();
    localJPanel2.add(this.combo1);
    JButton localJButton = new JButton("Select Font...");
    localJButton.setActionCommand("fontButton.clicked");
    localJButton.addActionListener(this);
    localJPanel2.add(localJButton);
    localJPanel1.add(localJPanel2, "North");
    this.drawStringPanel1 = new DrawStringPanel("0123456789", false);
    localJPanel1.add(this.drawStringPanel1);
    return localJPanel1;
  }

  private JPanel createTab2Content()
  {
    JPanel localJPanel1 = new JPanel(new BorderLayout());
    JPanel localJPanel2 = new JPanel();
    localJPanel2.add(new JLabel("Text anchor: "));
    this.combo2 = new JComboBox();
    populateTextAnchorCombo(this.combo2);
    this.combo2.setActionCommand("combo2.changed");
    this.combo2.addActionListener(this);
    localJPanel2.add(this.combo2);
    localJPanel2.add(new JLabel("Rotation anchor: "));
    this.combo3 = new JComboBox();
    populateTextAnchorCombo(this.combo3);
    this.combo3.setActionCommand("combo3.changed");
    this.combo3.addActionListener(this);
    localJPanel2.add(this.combo3);
    this.rotation = new JSlider(-360, 360, 0);
    this.rotation.setMajorTickSpacing(60);
    this.rotation.setMinorTickSpacing(10);
    this.rotation.setPaintLabels(true);
    this.rotation.setPaintTicks(true);
    this.rotation.addChangeListener(this);
    localJPanel1.add(this.rotation, "South");
    localJPanel1.add(localJPanel2, "North");
    this.drawStringPanel2 = new DrawStringPanel("Rotated Text", true);
    localJPanel1.add(this.drawStringPanel2);
    return localJPanel1;
  }

  private void displayFontDialog()
  {
    FontChooserPanel localFontChooserPanel = new FontChooserPanel(this.drawStringPanel1.getFont());
    int i = JOptionPane.showConfirmDialog(this, localFontChooserPanel, "Font Selection", 2, -1);
    if (i != 0)
      return;
    this.drawStringPanel1.setFont(localFontChooserPanel.getSelectedFont());
    this.drawStringPanel2.setFont(localFontChooserPanel.getSelectedFont());
  }

  private void populateTextAnchorCombo(JComboBox paramJComboBox)
  {
    paramJComboBox.addItem("TextAnchor.TOP_LEFT");
    paramJComboBox.addItem("TextAnchor.TOP_CENTER");
    paramJComboBox.addItem("TextAnchor.TOP_RIGHT");
    paramJComboBox.addItem("TextAnchor.HALF_ASCENT_LEFT");
    paramJComboBox.addItem("TextAnchor.HALF_ASCENT_CENTER");
    paramJComboBox.addItem("TextAnchor.HALF_ASCENT_RIGHT");
    paramJComboBox.addItem("TextAnchor.CENTER_LEFT");
    paramJComboBox.addItem("TextAnchor.CENTER");
    paramJComboBox.addItem("TextAnchor.CENTER_RIGHT");
    paramJComboBox.addItem("TextAnchor.BASELINE_LEFT");
    paramJComboBox.addItem("TextAnchor.BASELINE_CENTER");
    paramJComboBox.addItem("TextAnchor.BASELINE_RIGHT");
    paramJComboBox.addItem("TextAnchor.BOTTOM_LEFT");
    paramJComboBox.addItem("TextAnchor.BOTTOM_CENTER");
    paramJComboBox.addItem("TextAnchor.BOTTOM_RIGHT");
  }

  private TextAnchor convertStringToAnchor(String paramString)
  {
    if (paramString.equals("TextAnchor.TOP_LEFT"))
      return TextAnchor.TOP_LEFT;
    if (paramString.equals("TextAnchor.TOP_CENTER"))
      return TextAnchor.TOP_CENTER;
    if (paramString.equals("TextAnchor.TOP_RIGHT"))
      return TextAnchor.TOP_RIGHT;
    if (paramString.equals("TextAnchor.CENTER_LEFT"))
      return TextAnchor.CENTER_LEFT;
    if (paramString.equals("TextAnchor.CENTER"))
      return TextAnchor.CENTER;
    if (paramString.equals("TextAnchor.CENTER_RIGHT"))
      return TextAnchor.CENTER_RIGHT;
    if (paramString.equals("TextAnchor.HALF_ASCENT_LEFT"))
      return TextAnchor.HALF_ASCENT_LEFT;
    if (paramString.equals("TextAnchor.HALF_ASCENT_CENTER"))
      return TextAnchor.HALF_ASCENT_CENTER;
    if (paramString.equals("TextAnchor.HALF_ASCENT_RIGHT"))
      return TextAnchor.HALF_ASCENT_RIGHT;
    if (paramString.equals("TextAnchor.BASELINE_LEFT"))
      return TextAnchor.BASELINE_LEFT;
    if (paramString.equals("TextAnchor.BASELINE_CENTER"))
      return TextAnchor.BASELINE_CENTER;
    if (paramString.equals("TextAnchor.BASELINE_RIGHT"))
      return TextAnchor.BASELINE_RIGHT;
    if (paramString.equals("TextAnchor.BOTTOM_LEFT"))
      return TextAnchor.BOTTOM_LEFT;
    if (paramString.equals("TextAnchor.BOTTOM_CENTER"))
      return TextAnchor.BOTTOM_CENTER;
    if (paramString.equals("TextAnchor.BOTTOM_RIGHT"))
      return TextAnchor.BOTTOM_RIGHT;
    return null;
  }

  public static void main(String[] paramArrayOfString)
  {
    DrawStringDemo localDrawStringDemo = new DrawStringDemo("DrawString Demo");
    localDrawStringDemo.pack();
    RefineryUtilities.centerFrameOnScreen(localDrawStringDemo);
    localDrawStringDemo.setVisible(true);
  }
}