package space.paperless.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import space.paperless.repository.IdUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class FilesControllerTest {

	@Autowired
	private FilesController controller;

	@Test
	public void get_file_fileIsReturned() {
		ValidatableMockMvcResponse response = given().standaloneSetup(controller).when()
				.get("/repositories/archive/files/" + IdUtils.id("archive", "type2", "201501_Captain_Future.pdf"))
				.then();

		response.statusCode(200);
		response.contentType("application/pdf");
	}
}
