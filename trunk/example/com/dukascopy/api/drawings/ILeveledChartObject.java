/*
 * Copyright 2010 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.drawings;

import java.awt.Color;

public interface ILeveledChartObject extends IChartDependentChartObject {

	/**
	 * Returns level label text by specified index.
	 * 
	 * @param index
	 *            - level index.
	 * @exception IllegalArgumentException
	 *                - if level index is out of bounds.
	 * @return level label text.
	 * @author stanislavs.rubens
	 */
	String getLevelLabel(int index);

	/**
	 * Sets label text to level, specified by index.
	 * 
	 * @param index
	 *            - level index.
	 * @param label
	 *            - text to set.
	 * @exception IllegalArgumentException
	 *                - if level index is out of bounds.
	 * @author stanislavs.rubens
	 */
	void setLevelLabel(int index, String label);

	/**
	 * Returns level value by specified index.
	 * 
	 * @param index
	 *            - level index.
	 * @exception IllegalArgumentException
	 *                - if level index is out of bounds.
	 * @return level value.
	 * @author stanislavs.rubens
	 */
	Double getLevelValue(int index);

	/**
	 * Sets value to level, specified by index.
	 * 
	 * @param index
	 *            - level index.
	 * @param value
	 *            - value to set.
	 * @exception IllegalArgumentException
	 *                - if level index is out of bounds.
	 * @author stanislavs.rubens
	 */
	void setLevelValue(int index, Double value);

	/**
	 * Returns level color by specified index.
	 * 
	 * @param index
	 *            - level index.
	 * @exception IllegalArgumentException
	 *                - if level index is out of bounds.
	 * @return level color.
	 * @author stanislavs.rubens
	 */
	Color getLevelColor(int index);

	/**
	 * Sets color to level, specified by index.
	 * 
	 * @param index
	 *            - level index.
	 * @param color
	 *            - color to set.
	 * @exception IllegalArgumentException
	 *                - if level index is out of bounds.
	 * @author stanislavs.rubens
	 */
	void setLevelColor(int index, Color color);

	/**
	 * Adds new level to object levels.
	 * 
	 * @param label
	 *            - level label text.
	 * @param color
	 *            - level color.
	 * @param value
	 *            - level value.
	 * @author stanislavs.rubens
	 */
	void addLevel(String label, Double value, Color color);

	/**
	 * Removes level by specified index.
	 * 
	 * @param index
	 *            - index of level to remove.
	 * @exception IllegalArgumentException
	 *                - if index is out of bounds.
	 * @author stanislavs.rubens
	 */
	void removeLevel(int index);

	/**
	 * Returns number of levels
	 * 
	 * @return number of levels
	 */
	int getLevelsCount();
}