package demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.jfree.chart.plot.PiePlot3D;

class Rotator extends Timer
  implements ActionListener
{
  private PiePlot3D plot;
  private int angle = 270;

  Rotator(PiePlot3D paramPiePlot3D)
  {
    super(100, null);
    this.plot = paramPiePlot3D;
    addActionListener(this);
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    this.plot.setStartAngle(this.angle);
    this.angle += 1;
    if (this.angle != 360)
      return;
    this.angle = 0;
  }
}