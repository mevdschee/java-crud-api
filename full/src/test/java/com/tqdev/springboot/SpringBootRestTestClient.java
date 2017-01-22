package com.tqdev.springboot;

import java.net.URI;

import org.springframework.web.client.RestTemplate;

import com.tqdev.springboot.model.User;
import com.tqdev.springboot.service.ListResponse;

public class SpringBootRestTestClient {

	public static final String REST_SERVICE_URI = "http://localhost:8080/data";

	/* GET */
	private static void listAllUsers() {
		System.out.println("Testing listAllUsers API-----------");

		RestTemplate restTemplate = new RestTemplate();
		ListResponse users = restTemplate.getForObject(REST_SERVICE_URI + "/users", ListResponse.class);

		if (users != null) {
			for (Object user : users.records) {
				System.out.println(user);
			}
		} else {
			System.out.println("No user exist----------");
		}
	}

	/* GET */
	private static void getUser() {
		System.out.println("Testing getUser API----------");
		RestTemplate restTemplate = new RestTemplate();
		Object user = restTemplate.getForObject(REST_SERVICE_URI + "/users/1", Object.class);
		System.out.println(user);
	}

	/* POST */
	private static void createUser() {
		System.out.println("Testing create User API----------");
		RestTemplate restTemplate = new RestTemplate();
		User user = new User("0", "Sarah", 51, 134);
		URI uri = restTemplate.postForLocation(REST_SERVICE_URI + "/users", user, User.class);
		System.out.println("Location : " + uri.toASCIIString());
	}

	/* PUT */
	private static void updateUser() {
		System.out.println("Testing update User API----------");
		RestTemplate restTemplate = new RestTemplate();
		User user = new User("1", "Tomy", 33, 70000);
		restTemplate.put(REST_SERVICE_URI + "/users/1", user);
		System.out.println(user);
	}

	/* DELETE */
	private static void deleteUser() {
		System.out.println("Testing delete User API----------");
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.delete(REST_SERVICE_URI + "/users/3");
	}

	public static void main(String args[]) {
		listAllUsers();
		getUser();
		createUser();
		listAllUsers();
		updateUser();
		listAllUsers();
		deleteUser();
		listAllUsers();
	}
}