package com.tqdev.crudapi;

import java.util.ArrayList;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.tqdev.crudapi.core.record.ListResponse;
import com.tqdev.crudapi.core.record.Record;

public class CrudApiTestClient {

	public static final String REST_SERVICE_URI = "http://localhost:8080/data";

	private static Record createUser(String id, String name, int age, double salary) {
		Record user = new Record();
		user.put("id", id);
		user.put("name", name);
		user.put("age", age);
		user.put("salary", salary);
		return user;
	}

	/* GET */
	private static void listAllUsers() {
		System.out.println("Testing listAllUsers API-----------");

		RestTemplate restTemplate = new RestTemplate();
		ListResponse users = restTemplate.getForObject(REST_SERVICE_URI + "/users", ListResponse.class);

		if (users != null) {
			for (Record user : users.getRecords()) {
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
		Record user = createUser(null, "Sarah", 51, 34000);
		HttpEntity<Record> request = new HttpEntity<>(user);
		ResponseEntity<Object> response = restTemplate.exchange(REST_SERVICE_URI + "/users", HttpMethod.POST, request,
				Object.class);
		Object result = response.getBody();
		System.out.println(result);
	}

	/* POST */
	private static void createUsers() {
		System.out.println("Testing createUsers API----------");
		RestTemplate restTemplate = new RestTemplate();
		Record user1 = createUser(null, "Sarah2", 51, 34000);
		Record user2 = createUser(null, "Sarah3", 51, 34000);
		ArrayList<Record> users = new ArrayList<>();
		users.add(user1);
		users.add(user2);
		HttpEntity<ArrayList<Record>> request = new HttpEntity<>(users);
		ResponseEntity<Object> response = restTemplate.exchange(REST_SERVICE_URI + "/users", HttpMethod.POST, request,
				Object.class);
		Object result = response.getBody();
		System.out.println(result);
	}

	/* PUT */
	private static void updateUser() {
		System.out.println("Testing updateUser API----------");
		RestTemplate restTemplate = new RestTemplate();
		Record user = createUser("1", "Tomy", 33, 70000);
		HttpEntity<Record> request = new HttpEntity<>(user);
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