package com.tqdev.crudapi;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.tqdev.crudapi.service.CrudApiService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = CrudApiApp.class, loader = SpringBootContextLoader.class)
public class SampleTests {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	CrudApiService service;

	private MockMvc mockMvc;

	@Before
	public void setup() throws Exception {
		mockMvc = webAppContextSetup(this.wac).build();
		DatabaseRecords.fromFile("records.json").create(service);
	}

	@Test
	public void testListPosts() throws Exception {
		mockMvc.perform(get("/data/posts").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.records[*].id", hasItems(1, 2)))
				.andExpect(jsonPath("$.records[*].content", hasItems("blog started", "It works!")));
	}

	@Test
	public void testListPostColumns() throws Exception {
		mockMvc.perform(get("/data/posts?columns=id,content").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.records[*].id", hasSize(2))).andExpect(jsonPath("$.records[0].*", hasSize(2)));
	}

	@Test
	public void testUserRead() throws Exception {
		mockMvc.perform(get("/data/users/1").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.username").value("user1"));
	}

}