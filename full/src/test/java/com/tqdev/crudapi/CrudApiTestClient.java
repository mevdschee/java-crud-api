package com.tqdev.crudapi;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.tqdev.crudapi.model.User;
import com.tqdev.crudapi.service.ListResponse;

public class CrudApiTestClient {

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

	/* GET */
	private static void getUsers() {
		System.out.println("Testing getUsers API----------");
		RestTemplate restTemplate = new RestTemplate();
		Object[] users = restTemplate.getForObject(REST_SERVICE_URI + "/users/1,2", Object[].class);
		for (Object user : users) {
			System.out.println(user);
		}
	}

	/* GET */
	private static void getNonExistingUser() {
		System.out.println("Testing getNonExistingUser API----------");
		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.getForObject(REST_SERVICE_URI + "/users/666", Object.class);
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode() + ":" + e.getResponseBodyAsString());
		}
	}

	/* POST */
	private static void createUser() {
		System.out.println("Testing createUser API----------");
		RestTemplate restTemplate = new RestTemplate();
		User user = new User("0", "Sarah", 51, 134);
		HttpEntity<User> request = new HttpEntity<>(user);
		ResponseEntity<Object> response = restTemplate.exchange(REST_SERVICE_URI + "/users", HttpMethod.POST, request,
				Object.class);
		Object result = response.getBody();
		System.out.println(result);
	}

	/* POST */
	private static void createUsers() {
		System.out.println("Testing createUsers API----------");
		RestTemplate restTemplate = new RestTemplate();
		User user1 = new User("0", "Sarah2", 51, 134);
		User user2 = new User("0", "Sarah3", 51, 134);
		HttpEntity<User[]> request = new HttpEntity<>(new User[] { user1, user2 });
		ResponseEntity<Object> response = restTemplate.exchange(REST_SERVICE_URI + "/users", HttpMethod.POST, request,
				Object.class);
		Object result = response.getBody();
		System.out.println(result);
	}

	/* PUT */
	private static void updateUser() {
		System.out.println("Testing updateUser API----------");
		RestTemplate restTemplate = new RestTemplate();
		User user = new User("1", "Tomy", 33, 70000);
		HttpEntity<User> request = new HttpEntity<>(user);
		ResponseEntity<Object> response = restTemplate.exchange(REST_SERVICE_URI + "/users/1", HttpMethod.PUT, request,
				Object.class);
		Object result = response.getBody();
		System.out.println(result);
	}

	/* DELETE */
	private static void deleteUser() {
		System.out.println("Testing delete User API----------");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Object> response = restTemplate.exchange(REST_SERVICE_URI + "/users/3", HttpMethod.DELETE, null,
				Object.class);
		Object result = response.getBody();
		System.out.println(result);
	}

	public static void main(String args[]) {
		listAllUsers();
		getUser();
		getUsers();
		getNonExistingUser();
		createUser();
		createUsers();
		listAllUsers();
		updateUser();
		listAllUsers();
		deleteUser();
		listAllUsers();
	}
}