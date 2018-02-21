package com.tqdev.crudapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URLEncoder;

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
	public void test001ListPosts() throws Exception {
		mockMvc.perform(get("/data/posts")).andExpect(status().isOk()).andExpect(content().string(
				"{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"},{\"id\":2,\"user_id\":1,\"category_id\":2,\"content\":\"It works!\"}]}"));
	}

	@Test
	public void test002ListPostColumns() throws Exception {
		mockMvc.perform(get("/data/posts?columns=id,content")).andExpect(status().isOk()).andExpect(content().string(
				"{\"records\":[{\"id\":1,\"content\":\"blog started\"},{\"id\":2,\"content\":\"It works!\"}]}"));
	}

	@Test
	public void test003ReadPost() throws Exception {
		mockMvc.perform(get("/data/posts/2")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":2,\"user_id\":1,\"category_id\":2,\"content\":\"It works!\"}"));
	}

	@Test
	public void test004ReadPosts() throws Exception {
		mockMvc.perform(get("/data/posts/1,2")).andExpect(status().isOk()).andExpect(content().string(
				"[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"},{\"id\":2,\"user_id\":1,\"category_id\":2,\"content\":\"It works!\"}]"));
	}

	@Test
	public void test005ReadPostColumns() throws Exception {
		mockMvc.perform(get("/data/posts/2?columns=id,content")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":2,\"content\":\"It works!\"}"));
	}

	@Test
	public void test006AddPost() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/json")
				.content("{\"user_id\": 1, \"category_id\": 1, \"content\": \"test\"}")).andExpect(status().isOk())
				.andExpect(content().string("3"));
	}

	@Test
	public void test007EditPost() throws Exception {
		mockMvc.perform(put("/data/posts/3").contentType("application/json")
				.content("{\"user_id\":1,\"category_id\":1,\"content\":\"test (edited)\"}")).andExpect(status().isOk())
				.andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/3")).andExpect(status().isOk()).andExpect(
				content().string("{\"id\":3,\"user_id\":1,\"category_id\":1,\"content\":\"test (edited)\"}"));
	}

	@Test
	public void test008EditPostColumnsMissingField() throws Exception {
		mockMvc.perform(put("/data/posts/3?columns=id,content").contentType("application/json")
				.content("{\"content\":\"test (edited 2)\"}")).andExpect(status().isOk())
				.andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/3")).andExpect(status().isOk()).andExpect(
				content().string("{\"id\":3,\"user_id\":1,\"category_id\":1,\"content\":\"test (edited 2)\"}"));
	}

	@Test
	public void test009EditPostColumnsExtraField() throws Exception {
		mockMvc.perform(put("/data/posts/3?columns=id,content").contentType("application/json")
				.content("{\"user_id\":2,\"content\":\"test (edited 3)\"}")).andExpect(status().isOk())
				.andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/3")).andExpect(status().isOk()).andExpect(
				content().string("{\"id\":3,\"user_id\":1,\"category_id\":1,\"content\":\"test (edited 3)\"}"));
	}

	@Test
	public void test010EditPostWithUtf8Content() throws Exception {
		String utf8 = "🤗 Grüßgott, Вiтаю, dobrý deň, hyvää päivää, გამარჯობა, Γεια σας, góðan dag, здравствуйте";
		mockMvc.perform(put("/data/posts/2").contentType("application/json").content("{\"content\":\"" + utf8 + "\"}"))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/2")).andExpect(status().isOk())
				.andExpect(content().json("{\"id\":2,\"user_id\":1,\"category_id\":2,\"content\":\"" + utf8 + "\"}"));
	}

	@Test
	public void test011EditPostWithUtf8ContentWithPost() throws Exception {
		String utf8 = "🦀€ Grüßgott, Вiтаю, dobrý deň, hyvää päivää, გამარჯობა, Γεια σας, góðan dag, здравствуйте";
		String urlenc = URLEncoder.encode(utf8, "UTF-8");
		mockMvc.perform(
				put("/data/posts/2").contentType("application/x-www-form-urlencoded").content("content=" + urlenc))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/2")).andExpect(status().isOk())
				.andExpect(content().json("{\"id\":2,\"user_id\":1,\"category_id\":2,\"content\":\"" + utf8 + "\"}"));
	}

	@Test
	public void test012DeletePost() throws Exception {
		mockMvc.perform(delete("/data/posts/3")).andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/3")).andExpect(status().isNotFound())
				.andExpect(content().string("{\"code\":1003,\"message\":\"Record '3' not found\"}"));
	}

	@Test
	public void test013AddPostWithPost() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/x-www-form-urlencoded")
				.content("user_id=1&category_id=1&content=test")).andExpect(status().isOk())
				.andExpect(content().string("4"));
	}

	@Test
	public void test014EditPostWithPost() throws Exception {
		mockMvc.perform(put("/data/posts/4").contentType("application/x-www-form-urlencoded")
				.content("user_id=1&category_id=1&content=test+(edited)")).andExpect(status().isOk())
				.andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/4")).andExpect(status().isOk()).andExpect(
				content().string("{\"id\":4,\"user_id\":1,\"category_id\":1,\"content\":\"test (edited)\"}"));
	}

	@Test
	public void test015DeletePostWithPostIgnoreColumns() throws Exception {
		mockMvc.perform(delete("/data/posts/4?columns=id,content")).andExpect(status().isOk())
				.andExpect(content().string("1"));
		mockMvc.perform(get("/data/posts/4")).andExpect(status().isNotFound())
				.andExpect(content().string("{\"code\":1003,\"message\":\"Record '4' not found\"}"));
	}

	@Test
	public void test016ListWithPaginate() throws Exception {
		for (int i = 1; i <= 10; i++) {
			mockMvc.perform(post("/data/posts").contentType("application/json")
					.content("{\"user_id\":1,\"category_id\":1,\"content\":\"#" + i + "\"}")).andExpect(status().isOk())
					.andExpect(content().string("" + (4 + i)));
		}
		mockMvc.perform(get("/data/posts?page=2,2&order=id")).andExpect(status().isOk()).andExpect(content().string(
				"{\"records\":[{\"id\":5,\"user_id\":1,\"category_id\":1,\"content\":\"#1\"},{\"id\":6,\"user_id\":1,\"category_id\":1,\"content\":\"#2\"}],\"results\":12}"));
	}

	@Test
	public void test017ReadUserXml() throws Exception {
		mockMvc.perform(get("/data/users/1").accept("application/xml")).andExpect(status().isOk()).andExpect(content()
				.string("<Record><id>1</id><username>user1</username><password>pass1</password><location/></Record>"));
	}

	@Test
	public void test018ListUsersXml() throws Exception {
		mockMvc.perform(get("/data/users").accept("application/xml")).andExpect(status().isOk()).andExpect(content()
				.string("<ListResponse><Records><Record><id>1</id><username>user1</username><password>pass1</password><location/></Record><Record><id>2</id><username>user2</username><password>pass2</password><location/></Record></Records></ListResponse>"));
	}

	@Test
	public void test019ListWithPaginateInMultipleOrder() throws Exception {
		mockMvc.perform(get("/data/posts?page=1,2&order=category_id,asc&order=id,desc").accept("application/json"))
				.andExpect(status().isOk()).andExpect(content().string(
						"{\"records\":[{\"id\":14,\"user_id\":1,\"category_id\":1,\"content\":\"#10\"},{\"id\":13,\"user_id\":1,\"category_id\":1,\"content\":\"#9\"}],\"results\":12}"));
	}

	@Test
	public void test020ListWithPaginateInDescendingOrder() throws Exception {
		mockMvc.perform(get("/data/posts?page=2,2&order=id,desc").accept("application/json")).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"records\":[{\"id\":12,\"user_id\":1,\"category_id\":1,\"content\":\"#8\"},{\"id\":11,\"user_id\":1,\"category_id\":1,\"content\":\"#7\"}],\"results\":12}"));
	}

	@Test
	public void test021ListWithSize() throws Exception {
		mockMvc.perform(get("/data/posts?order=id&size=1")).andExpect(status().isOk()).andExpect(content()
				.string("{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"}]}"));
	}

	@Test
	public void test022ListWithSeek() throws Exception {
		mockMvc.perform(get("/data/posts?order=id&seek=4&size=2")).andExpect(status().isOk()).andExpect(content()
				.string("{\"records\":[{\"id\":5,\"user_id\":1,\"category_id\":1,\"content\":\"#1\"},{\"id\":6,\"user_id\":1,\"category_id\":1,\"content\":\"#2\"}]}"));
	}

	@Test
	public void test023ListWithSeekOnMultipleFields() throws Exception {
		mockMvc.perform(get("/data/posts?order=category_id,asc&order=id,desc&seek=1&seek=13&size=2"))
				.andExpect(status().isOk()).andExpect(content().string(
						"{\"records\":[{\"id\":12,\"user_id\":1,\"category_id\":1,\"content\":\"#8\"},{\"id\":11,\"user_id\":1,\"category_id\":1,\"content\":\"#7\"}]}"));
	}

	@Test
	public void test024ListWithZeroPageSize() throws Exception {
		mockMvc.perform(get("/data/posts?order=id&page=1,0")).andExpect(status().isOk())
				.andExpect(content().string("{\"records\":[],\"results\":12}"));
	}
}