package demo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.jfree.data.category.DefaultCategoryDataset;

class Animator extends Timer
  implements ActionListener
{
  private DefaultCategoryDataset dataset;

  Animator(DefaultCategoryDataset paramDefaultCategoryDataset)
  {
    super(20, null);
    this.dataset = paramDefaultCategoryDataset;
    addActionListener(this);
  }

  public void actionPerformed(ActionEvent paramActionEvent)
  {
    int i = (int)(Math.random() * this.dataset.getRowCount());
    Comparable localComparable1 = this.dataset.getRowKey(i);
    int j = (int)(Math.random() * this.dataset.getColumnCount());
    Comparable localComparable2 = this.dataset.getColumnKey(j);
    int k = (Math.random() - 0.5D < 0.0D) ? -5 : 5;
    this.dataset.setValue(Math.max(0.0D, this.dataset.getValue(i, j).doubleValue() + k), localComparable1, localComparable2);
  }
}