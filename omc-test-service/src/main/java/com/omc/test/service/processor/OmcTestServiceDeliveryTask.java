package com.omc.test.service.processor;

import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcTask;

public class OmcTestServiceDeliveryTask extends OmcTask {

	public OmcTestServiceDeliveryTask(OmcEvent omcEvent) {
		super(omcEvent);
	}

	@Override
	public void run() {
		
	}
}