package space.paperless.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.FixedContentNegotiationStrategy;

import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import space.paperless.repository.IdUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class DocumentControllerTest {

	@Autowired
	private DocumentController controller;

	@Test
	public void get_document_documentIsReturned() {
		MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).setContentNegotiationManager(
				new ContentNegotiationManager(new FixedContentNegotiationStrategy(MediaType.APPLICATION_JSON_UTF8)))
				.build();
		ValidatableMockMvcResponse response = given().mockMvc(mvc).when()
				.get("/repositories/archive/documents/" + IdUtils.id("archive", "type2", "201501_Captain_Future.pdf"))
				.then();

		response.statusCode(200);
		response.body("documentId", equalTo(IdUtils.id("archive", "type2", "201501_Captain_Future.pdf")));
		response.body("descriptions.name", hasItem(IdUtils.id("Captain_Future.pdf")));
	}
}
