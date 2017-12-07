package com.omc.service.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class OmcCommonUtil {

	public static int MAX_PORT_NUMBER = 38999;
	public static int MIN_PORT_NUMBER = 36000;

	public static int getRandomServerPort(int serverPort) {
		if(serverPort > 0 && serverPort < 65535) {
			return serverPort;
		}
		return  MIN_PORT_NUMBER + (int)(Math.random() * ((MAX_PORT_NUMBER - MIN_PORT_NUMBER) + 1));
	}

	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "Unknown Host";
		}
	}
}