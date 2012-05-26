package chanlun;

import java.util.*;
import com.dukascopy.api.*;

public class IBarCompareLow implements Comparator<IBar>{
	public int compare(IBar left, IBar right) {

		if (left.getLow() > right.getLow())
			return 1;
		else if (left.getLow() < right.getLow())
			return -1;
		else {
			return 0;
		}
	}

}
