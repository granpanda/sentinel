package gp.e3.sentinel.util;

import java.util.ArrayList;
import java.util.List;

import gp.e3.sentinel.domain.entities.System;

public class SystemFactoryForTests {
	
	public static System getDefaultSystem() {
		
		long id = 1;
		String name = "systemName";
		String url = "http://www.e3.granpanda.com";
		
		return new System(id, name, url);
	}
	
	public static System getDefaultSystem(int number) {
		
		long id = 1 + number;
		String name = "systemName" + number;
		String url = "http://www.e3.granpanda.com" + "/" + number;
		
		return new System(id, name, url);
	}
	
	public static List<System> getSystemsList(int listSize) {
		
		List<System> systems = new ArrayList<System>();
		
		for (int i = 0; i < listSize; i++) {
			
			systems.add(getDefaultSystem(i));
		}
		
		return systems;
	}
}