package chanlun;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

class TestAction extends AbstractAction {
	public TestAction(String name) {
		super(name);
	}

	public void actionPerformed(ActionEvent event) {
		System.out.println(getValue(Action.NAME) + " selected.");
	}
}

class MAAction extends AbstractAction {
	public MAAction(String name) {
		super(name);
	}
	public void actionPerformed(ActionEvent event) {
		System.out.println(getValue(Action.NAME) + " selected.");
	}
}
