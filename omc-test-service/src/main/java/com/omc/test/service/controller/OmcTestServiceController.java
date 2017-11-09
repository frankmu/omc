package com.omc.test.service.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OmcTestServiceController {

	@CrossOrigin
	@RequestMapping(value = "/state", method = RequestMethod.GET)
	public String getOmcServiceState() {
		return "get /state";
	}

	@CrossOrigin
	@RequestMapping(value = "/go", method = RequestMethod.POST)
	public String callOmcCommonService() {
		return "post /go";
	}

}