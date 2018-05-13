package com.tqdev.crudapi;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.net.URLEncoder;

import org.apache.tomcat.util.codec.binary.Base64;
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
@ContextConfiguration(classes = ApiApp.class, loader = SpringBootContextLoader.class)
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
	public void test015DeletePostIgnoreColumns() throws Exception {
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
	public void test022ListWithZeroPageSize() throws Exception {
		mockMvc.perform(get("/data/posts?order=id&page=1,0")).andExpect(status().isOk())
				.andExpect(content().string("{\"records\":[],\"results\":12}"));
	}

	@Test
	public void test023ListWithZeroSize() throws Exception {
		mockMvc.perform(get("/data/posts?order=id&size=0")).andExpect(status().isOk())
				.andExpect(content().string("{\"records\":[]}"));
	}

	@Test
	public void test024ListWithPaginateLastPage() throws Exception {
		mockMvc.perform(get("/data/posts?page=3,5&order=id")).andExpect(status().isOk()).andExpect(content().string(
				"{\"records\":[{\"id\":13,\"user_id\":1,\"category_id\":1,\"content\":\"#9\"},{\"id\":14,\"user_id\":1,\"category_id\":1,\"content\":\"#10\"}],\"results\":12}"));
	}

	@Test
	public void test025ListExampleFromReadmeFullRecord() throws Exception {
		mockMvc.perform(get("/data/posts?filter=id,eq,1")).andExpect(status().isOk()).andExpect(content()
				.string("{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"}]}"));
	}

	@Test
	public void test026ListExampleFromReadmeWithExclude() throws Exception {
		mockMvc.perform(get("/data/posts?exclude=id&filter=id,eq,1")).andExpect(status().isOk()).andExpect(
				content().string("{\"records\":[{\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"}]}"));
	}

	@Test
	public void test027ListExampleFromReadmeUsersOnly() throws Exception {
		mockMvc.perform(get("/data/posts?include=users&filter=id,eq,1")).andExpect(status().isOk()).andExpect(content()
				.string("{\"records\":[{\"id\":1,\"user_id\":{\"id\":1,\"username\":\"user1\",\"password\":\"pass1\",\"location\":null},\"category_id\":1,\"content\":\"blog started\"}]}"));
	}

	@Test
	public void test028ReadExampleFromReadmeUsersOnly() throws Exception {
		mockMvc.perform(get("/data/posts/1?include=users")).andExpect(status().isOk()).andExpect(content().string(
				"{\"id\":1,\"user_id\":{\"id\":1,\"username\":\"user1\",\"password\":\"pass1\",\"location\":null},\"category_id\":1,\"content\":\"blog started\"}"));
	}

	@Test
	public void test029ListExampleFromReadmeCommentsOnly() throws Exception {
		mockMvc.perform(get("/data/posts?include=comments&filter=id,eq,1")).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\",\"comments\":[{\"id\":1,\"post_id\":1,\"message\":\"great\"},{\"id\":2,\"post_id\":1,\"message\":\"fantastic\"}]}]}"));
	}

	@Test
	public void test030ListExampleFromReadmeTagsOnly() throws Exception {
		mockMvc.perform(get("/data/posts?include=tags&filter=id,eq,1")).andExpect(status().isOk()).andExpect(content()
				.string("{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\",\"post_tags\":[{\"id\":1,\"name\":\"funny\",\"is_important\":false},{\"id\":2,\"name\":\"important\",\"is_important\":true}]}]}"));
	}

	@Test
	public void test031ListExampleFromReadmeTagsWithIncludePath() throws Exception {
		mockMvc.perform(get("/data/posts?include=categories&include=post_tags,tags&include=comments&filter=id,eq,1"))
				.andExpect(status().isOk()).andExpect(content().string(
						"{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":{\"id\":1,\"name\":\"announcement\",\"icon\":null},\"content\":\"blog started\",\"post_tags\":[{\"id\":1,\"post_id\":1,\"tag_id\":{\"id\":1,\"name\":\"funny\",\"is_important\":false}},{\"id\":2,\"post_id\":1,\"tag_id\":{\"id\":2,\"name\":\"important\",\"is_important\":true}}],\"comments\":[{\"id\":1,\"post_id\":1,\"message\":\"great\"},{\"id\":2,\"post_id\":1,\"message\":\"fantastic\"}]}]}"));
	}

	@Test
	public void test032ListExampleFromReadme() throws Exception {
		mockMvc.perform(get("/data/posts?include=categories&include=tags&include=comments&filter=id,eq,1"))
				.andExpect(status().isOk()).andExpect(content().string(
						"{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":{\"id\":1,\"name\":\"announcement\",\"icon\":null},\"content\":\"blog started\",\"post_tags\":[{\"id\":1,\"name\":\"funny\",\"is_important\":false},{\"id\":2,\"name\":\"important\",\"is_important\":true}],\"comments\":[{\"id\":1,\"post_id\":1,\"message\":\"great\"},{\"id\":2,\"post_id\":1,\"message\":\"fantastic\"}]}]}"));
	}

	@Test
	public void test033ListExampleFromReadmeTagNameOnly() throws Exception {
		mockMvc.perform(
				get("/data/posts?columns=tags.name&include=categories&include=post_tags,tags&include=comments&filter=id,eq,1"))
				.andExpect(status().isOk()).andExpect(content().string(
							"{\"records\":[{\"id\":1,\"category_id\":{\"id\":1},\"post_tags\":[{\"post_id\":1,\"tag_id\":{\"id\":1,\"name\":\"funny\"}},{\"post_id\":1,\"tag_id\":{\"id\":2,\"name\":\"important\"}}],\"comments\":[{\"post_id\":1},{\"post_id\":1}]}]}"));
	}

	@Test
	public void test034ListExampleFromReadmeWithTransformWithExclude() throws Exception {
		mockMvc.perform(
				get("/data/posts?include=categories&include=post_tags,tags&include=comments&exclude=comments.message&filter=id,eq,1"))
				.andExpect(status().isOk()).andExpect(content().string(
						"{\"records\":[{\"id\":1,\"user_id\":1,\"category_id\":{\"id\":1,\"name\":\"announcement\",\"icon\":null},\"content\":\"blog started\",\"post_tags\":[{\"id\":1,\"post_id\":1,\"tag_id\":{\"id\":1,\"name\":\"funny\",\"is_important\":false}},{\"id\":2,\"post_id\":1,\"tag_id\":{\"id\":2,\"name\":\"important\",\"is_important\":true}}],\"comments\":[{\"id\":1,\"post_id\":1},{\"id\":2,\"post_id\":1}]}]}"));
	}

	@Test
	public void test035EditCategoryWithBinaryContent() throws Exception {
		String string = "€ \000abc\000\n\r\\b\000";
		String binary = Base64.encodeBase64String(string.getBytes("UTF-8"));
		String b64url = Base64.encodeBase64URLSafeString(string.getBytes("UTF-8"));
		mockMvc.perform(
				put("/data/categories/2").contentType("application/json").content("{\"icon\":\"" + b64url + "\"}"))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/categories/2")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":2,\"name\":\"article\",\"icon\":\"" + binary + "\"}"));
	}

	@Test
	public void test036EditCategoryWithNull() throws Exception {

		mockMvc.perform(put("/data/categories/2").contentType("application/json").content("{\"icon\":null}"))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/categories/2")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":2,\"name\":\"article\",\"icon\":null}"));
	}

	@Test
	public void test037EditCategoryWithBinaryContentWithPost() throws Exception {
		String string = "€ \000abc\000\n\r\\b\000";
		String binary = Base64.encodeBase64String(string.getBytes("UTF-8"));
		String b64url = Base64.encodeBase64URLSafeString(string.getBytes("UTF-8"));
		mockMvc.perform(
				put("/data/categories/2").contentType("application/x-www-form-urlencoded").content("icon=" + b64url))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/categories/2")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":2,\"name\":\"article\",\"icon\":\"" + binary + "\"}"));
	}

	@Test
	public void test038ListCategoriesWithBinaryContent() throws Exception {
		mockMvc.perform(get("/data/categories")).andExpect(status().isOk()).andExpect(content().string(
				"{\"records\":[{\"id\":1,\"name\":\"announcement\",\"icon\":null},{\"id\":2,\"name\":\"article\",\"icon\":\"4oKsIABhYmMACg1cYgA=\"}]}"));
	}

	@Test
	public void test039EditCategoryWithNullWithPost() throws Exception {
		mockMvc.perform(
				put("/data/categories/2").contentType("application/x-www-form-urlencoded").content("icon__is_null"))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/categories/2")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":2,\"name\":\"article\",\"icon\":null}"));
	}

	@Test
	public void test040AddPostFailure() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/json").content("[\"truncat")).andExpect(status().isNotAcceptable())
				.andExpect(content().string("{\"code\":1008,\"message\":\"Cannot read HTTP message\"}"));
	}

	@Test
	public void test041CorsPreFlight() throws Exception {
		mockMvc.perform(options("/data/posts/1?columns=id").header("Origin", "http://example.com")
				.header("Access-Control-Request-Method", "POST")
				.header("Access-Control-Request-Headers", "X-XSRF-TOKEN, X-Requested-With")).andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://example.com"))
				.andExpect(header().string("Access-Control-Allow-Headers", "X-XSRF-TOKEN"))
				.andExpect(header().string("Access-Control-Allow-Methods", "OPTIONS,GET,PUT,POST,DELETE,PATCH"))
				.andExpect(header().string("Access-Control-Allow-Credentials", "true"))
				.andExpect(header().string("Access-Control-Max-Age", "1728000"));
	}

	@Test
	public void test042CorsHeaders() throws Exception {
		mockMvc.perform(get("/data/posts/1?columns=id").header("Origin", "http://example.com")).andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://example.com"))
				.andExpect(header().string("Access-Control-Allow-Credentials", "true"));
	}

	@Test
	public void test043ErrorOnInvalidJson() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/json").content("{\"}"))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string("{\"code\":1008,\"message\":\"Cannot read HTTP message\"}"));
	}

	@Test
	public void test044ErrorOnDuplicatePrimaryKey() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/json")
				.content("{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started (duplicate)\"}"))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string("{\"code\":1009,\"message\":\"Duplicate key exception\"}"));
	}

	@Test
	public void test045ErrorOnFailingForeignKeyConstraint() throws Exception {
		mockMvc.perform(post("/data/posts").contentType("application/json")
				.content("{\"user_id\":3,\"category_id\":1,\"content\":\"fk constraint\"}"))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string("{\"code\":1010,\"message\":\"Data integrity violation\"}"));
	}

	@Test
	public void test046ErrorOnNonExistingTable() throws Exception {
		mockMvc.perform(get("/data/postzzz")).andExpect(status().isNotFound())
				.andExpect(content().string("{\"code\":1001,\"message\":\"Table 'postzzz' not found\"}"));
	}

	@Test
	public void test047ErrorOnInvalidPath() throws Exception {
		mockMvc.perform(get("/postzzz")).andExpect(status().isNotFound())
				.andExpect(content().string("{\"code\":1000,\"message\":\"Route '/postzzz' not found\"}"));
	}

	@Test
	public void test048ErrorOnInvalidArgumentCount() throws Exception {
		mockMvc.perform(put("/data/posts/1,2").contentType("application/json")
				.content("{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"}"))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string("{\"code\":1002,\"message\":\"Argument count mismatch in '1,2'\"}"));
	}

	@Test
	public void test049ErrorOnInvalidArgumentCount() throws Exception {
		mockMvc.perform(put("/data/posts/1,2").contentType("application/json")
				.content("[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"}]"))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string("{\"code\":1002,\"message\":\"Argument count mismatch in '1,2'\"}"));
	}

	@Test
	public void test050NoErrorOnArgumentCountOne() throws Exception {
		mockMvc.perform(put("/data/posts/1").contentType("application/json")
				.content("[{\"id\":1,\"user_id\":1,\"category_id\":1,\"content\":\"blog started\"}]"))
				.andExpect(status().isOk()).andExpect(content().string("[1]"));
	}

	@Test
	public void test051ErrorOnInvalidArgumentCount() throws Exception {
		mockMvc.perform(put("/data/posts/1").contentType("application/json").content("[{\"id\":1},{\"id\":2}]"))
				.andExpect(status().isNotAcceptable())
				.andExpect(content().string("{\"code\":1002,\"message\":\"Argument count mismatch in '1'\"}"));
	}

	@Test
	public void test052EditUserLocation() throws Exception {
		mockMvc.perform(put("/data/users/1").contentType("application/json").content("{\"location\":\"POINT(30 20)\"}"))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/users/1?columns=id,location")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":1,\"location\":\"POINT(30 20)\"}"));
	}

	@Test
	public void test053ListUserLocations() throws Exception {
		mockMvc.perform(get("/data/users?columns=id,location")).andExpect(status().isOk()).andExpect(content()
				.string("{\"records\":[{\"id\":1,\"location\":\"POINT(30 20)\"},{\"id\":2,\"location\":null}]}"));
	}

	@Test
	public void test054EditUserWithId() throws Exception {
		mockMvc.perform(
				put("/data/users/1").contentType("application/json").content("{\"id\":2,\"password\":\"testtest2\"}"))
				.andExpect(status().isOk()).andExpect(content().string("1"));
		mockMvc.perform(get("/data/users/1?columns=id,username,password")).andExpect(status().isOk())
				.andExpect(content().string("{\"id\":1,\"username\":\"user1\",\"password\":\"testtest2\"}"));
	}

	@Test
	public void test055FilterCategoryOnNullIcon() throws Exception {
		mockMvc.perform(get("/data/categories?filter=icon,is,null")).andExpect(status().isOk()).andExpect(content()
				.string("{\"records\":[{\"id\":1,\"name\":\"announcement\",\"icon\":null},{\"id\":2,\"name\":\"article\",\"icon\":null}]}"));
	}

	@Test
	public void test056FilterCategoryOnNotNullIcon() throws Exception {
		mockMvc.perform(get("/data/categories?filter=icon,nis,null")).andExpect(status().isOk())
				.andExpect(content().string("{\"records\":[]}"));
	}

	@Test
	public void test057FilterOnAnd() throws Exception {
		mockMvc.perform(get("/data/posts?columns=id&filter=id,ge,1&filter=id,le,2")).andExpect(status().isOk())
				.andExpect(content().string("{\"records\":[{\"id\":1},{\"id\":2}]}"));
	}

	@Test
	public void test058FilterOnOr() throws Exception {
		mockMvc.perform(get("/data/posts?columns=id&filter1=id,eq,1&filter2=id,eq,2")).andExpect(status().isOk())
				.andExpect(content().string("{\"records\":[{\"id\":1},{\"id\":2}]}"));
	}

	@Test
	public void test059FilterOnAndPlusOr() throws Exception {
		mockMvc.perform(get("/data/posts?columns=id&filter1=id,eq,1&filter2=id,gt,1&filter2=id,lt,3"))
				.andExpect(status().isOk()).andExpect(content().string("{\"records\":[{\"id\":1},{\"id\":2}]}"));
	}

	@Test
	public void test060FilterOnOrPlusAnd() throws Exception {
		mockMvc.perform(get("/data/posts?columns=id&filter1=id,eq,1&filter2=id,eq,2&filter=user_id,eq,1"))
				.andExpect(status().isOk()).andExpect(content().string("{\"records\":[{\"id\":1},{\"id\":2}]}"));
	}

	@Test
	public void test061GetPostContentWithIncludedTagNames() throws Exception {
		mockMvc.perform(get("/data/posts/1?columns=content,tags.name&include=tags")).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"id\":1,\"content\":\"blog started\",\"post_tags\":[{\"id\":1,\"name\":\"funny\"},{\"id\":2,\"name\":\"important\"}]}"));
	}

	@Test
	public void test062GetKunsthåndværk() throws Exception {
   		mockMvc.perform(get("/data/kunsthåndværk").contentType("text/html; charset=UTF-8")).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"records\":[{\"id\":1,\"Umlauts ä_ö_ü-COUNT\":1}]}"));
	}

	@Test
	public void test062MetaGetDatabase() throws Exception {
		mockMvc.perform(get("/meta")).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"records\":[{\"id\":1,\"Umlauts ä_ö_ü-COUNT\":1}]}"));
	}

	@Test
	public void test062MetaGetBarcodesTable() throws Exception {
		mockMvc.perform(get("/meta/barcodes")).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"records\":[{\"id\":1,\"Umlauts ä_ö_ü-COUNT\":1}]}"));
	}

	@Test
	public void test062MetaGetBarcodesIdColumn() throws Exception {
		mockMvc.perform(get("/meta/barcodes/id")).andExpect(status().isOk())
				.andExpect(content().string(
						"{\"records\":[{\"id\":1,\"Umlauts ä_ö_ü-COUNT\":1}]}"));
	}
}