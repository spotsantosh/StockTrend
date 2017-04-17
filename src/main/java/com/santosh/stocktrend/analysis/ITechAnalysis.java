package com.santosh.stocktrend.analysis;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.UpdateListener;
/**
 * Interface defining technical analysis, implementer class needed to add
 * CEP query and provide event handlers
 * @author santosh
 *
 */
public interface ITechAnalysis extends UpdateListener {
	String getName();
	void init(EPServiceProvider epService, ISignalListener signalListener);
}
