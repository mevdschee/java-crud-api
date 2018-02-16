package com.tqdev.crudapi;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WebAppConfiguration
@ContextConfiguration(classes = CrudApiApp.class, loader = SpringBootContextLoader.class)
public class SampleTests {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() throws Exception {
		mockMvc = webAppContextSetup(this.wac).build();
	}

	@Test
	public void testAddPosts() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/json")
				.content("{\"user_id\": 1, \"category_id\": 1, \"content\": \"test\"}").accept("application/json"))
				.andExpect(status().isOk()).andExpect(content().json("3"));
	}

	@Test
	public void testAddPostsFormEncoded() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/x-www-form-urlencoded")
				.content("user_id=1&category_id=1&content=test").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().json("4"));
	}

	@Test
	public void testListPostColumns() throws Exception {
		mockMvc.perform(get("/data/posts?columns=id,content").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.records[*].id", hasSize(4))).andExpect(jsonPath("$.records[0].*", hasSize(2)));
	}

	@Test
	public void testListPosts() throws Exception {
		mockMvc.perform(get("/data/posts").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.records[*].id", hasItems(1, 2)))
				.andExpect(jsonPath("$.records[*].content", hasItems("blog started", "It works!")));
	}

	@Test
	public void testListSingleUser() throws Exception {
		mockMvc.perform(get("/data/users?filter=id,eq,1").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json"))
				.andExpect(jsonPath("$.records[*].id", hasSize(1)))
				.andExpect(jsonPath("$.records[0].username").value("user1"));
	}

	@Test
	public void testReadUserJson() throws Exception {
		mockMvc.perform(get("/data/users/1").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/json")).andExpect(
						content().string("{\"id\":1,\"username\":\"user1\",\"password\":\"pass1\",\"location\":null}"));
	}

	@Test
	public void testReadUserXml() throws Exception {
		mockMvc.perform(get("/data/users/1").accept("application/xml")).andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("application/xml")).andExpect(content().string(
						"<Record><id>1</id><username>user1</username><password>pass1</password><location/></Record>"));
	}

}