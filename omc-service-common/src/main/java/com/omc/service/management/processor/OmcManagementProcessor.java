package com.omc.service.management.processor;

public interface OmcManagementProcessor {

	public int getRequestQueueSize();

	public int getDeliveryQueueSize();

	public int getTaskExecutorCorePoolSize();

	public int getTaskExecutorMaxPoolSize();
}
