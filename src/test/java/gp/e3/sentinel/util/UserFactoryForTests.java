package gp.e3.sentinel.util;

import java.util.ArrayList;
import java.util.List;

import gp.e3.sentinel.domain.entities.User;

public class UserFactoryForTests {

	public static User getDefaultUser() {
		
		String mail = "user@user.com";
		String fullName = "Full Name";
		
		List<Long> systemsUserIsSubscribedTo = new ArrayList<Long>();
		systemsUserIsSubscribedTo.add((long) 1);
		systemsUserIsSubscribedTo.add((long) 2);
		systemsUserIsSubscribedTo.add((long) 3);
		
		return new User(mail, fullName, systemsUserIsSubscribedTo);
	}
	
	public static User getDefaultUser(int number) {
		
		String mail = "user" + number + "@user.com";
		String fullName = "Full Name " + number;
		
		List<Long> systemsUserIsSubscribedTo = new ArrayList<Long>();
		systemsUserIsSubscribedTo.add((long) 1 + number);
		systemsUserIsSubscribedTo.add((long) 2 + number);
		systemsUserIsSubscribedTo.add((long) 3 + number);
		
		return new User(mail, fullName, systemsUserIsSubscribedTo);
	}
	
	public static List<User> getUsersList(int listSize) {
		
		List<User> users = new ArrayList<User>();
		
		for (int i = 0; i < listSize; i++) {
			users.add(getDefaultUser(i));
		}
		
		return users;
	}
}