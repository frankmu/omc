package com.omc.test.service.controller;

import java.util.concurrent.BlockingQueue;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.omc.service.domain.OmcEvent;
import com.omc.service.domain.OmcObserverState;
import com.omc.service.exception.OmcRequestQueueFullException;
import com.omc.service.util.OmcEventUtil;

@RestController
public class OmcTestServiceController {

	@Autowired
	BlockingQueue<OmcEvent> requestQueue;

	@Autowired
	BlockingQueue<OmcEvent> deliveryQueue;

	@Autowired
	OmcObserverState omcObserverState;

	@CrossOrigin
	@RequestMapping(value = "/state", method = RequestMethod.GET)
	public OmcObserverState getOmcServiceState() {
		omcObserverState.setRQSize(requestQueue.size());
		omcObserverState.setDQSize(deliveryQueue.size());
		return omcObserverState;
	}

	@CrossOrigin
	@RequestMapping(value = "/go", method = RequestMethod.POST)
	public boolean callOmcCommonService(@Valid @RequestBody OmcEvent omcEvent) throws OmcRequestQueueFullException {
		if(requestQueue.remainingCapacity() == 0) {
			throw new OmcRequestQueueFullException("Request queue is full, please try again later.");
		}
		OmcEventUtil.appendCurrentObserver(omcEvent, omcObserverState.getObname());
		requestQueue.add(omcEvent);
		return true;
	}
}