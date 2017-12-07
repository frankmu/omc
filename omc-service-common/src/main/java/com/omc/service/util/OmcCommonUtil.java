package com.omc.service.util;

public class OmcCommonUtil {

	public static int MAX_PORT_NUMBER = 38999;
	public static int MIN_PORT_NUMBER = 36000;

	public static int getRandomServerPort() {
		return  MIN_PORT_NUMBER + (int)(Math.random() * ((MAX_PORT_NUMBER - MIN_PORT_NUMBER) + 1));
	}
}