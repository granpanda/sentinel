package gp.e3.sentinel.util;

import gp.e3.sentinel.domain.entities.System;

public class SystemFactoryForTests {
	
	public static System getDefaultSystem() {
		
		long id = 1;
		String name = "systemName";
		String url = "http://www.e3.granpanda.com";
		
		return new System(id, name, url);
	}
}