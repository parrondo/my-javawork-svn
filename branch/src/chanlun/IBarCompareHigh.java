package chanlun;

import java.util.*;
import com.dukascopy.api.*;

public class IBarCompareHigh implements Comparator<IBar>{
	
	public int compare(IBar left, IBar right) {

		if (left.getHigh() < right.getHigh())
			return 1;
		else if (left.getHigh() > right.getHigh())
			return -1;
		else {
			return 0;
		}
	}
}