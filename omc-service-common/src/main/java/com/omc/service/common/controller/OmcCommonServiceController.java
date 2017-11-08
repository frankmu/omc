package com.omc.service.common.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OmcCommonServiceController {

	@CrossOrigin
	@RequestMapping(value = "/state", method = RequestMethod.GET)
	public void getOmcServiceState() {
	}
}