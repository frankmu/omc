package com.omc.management.processor;

public class OmcManagementDefaultProcessor implements OmcManagementProcessor {

	@Override
	public int getRequestQueueSize() {
		return 10;
	}

	@Override
	public int getDeliveryQueueSize() {
		return 10;
	}

	@Override
	public int getTaskExecutorCorePoolSize() {
		return 10;
	}

	@Override
	public int getTaskExecutorMaxPoolSize() {
		return 10;
	}
}
