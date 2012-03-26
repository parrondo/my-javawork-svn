package singlejartest;

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

class MA1030Action extends AbstractAction {
	public MA1030Action(String name) {
		super(name);
	}

	public void actionPerformed(ActionEvent event) {
		System.out.println(getValue(Action.NAME) + " selected.");
	}
}
