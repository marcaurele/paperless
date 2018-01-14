package space.paperless.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import space.paperless.controller.RepositoryController;
import space.paperless.domain.RepositoryId;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class RepositoryControllerTest {

	@Autowired
	private RepositoryController controller;

	@Test
	public void get_repositories_repositoryNamesAreReturned() {
		given().standaloneSetup(controller).when().get("/repositories").then().statusCode(200)
				.body("$.size", equalTo(2)).body("name", hasItems(
						Arrays.asList(RepositoryId.values()).stream().map(t -> t.getName()).toArray(String[]::new)));
	}

	@Test
	public void get_repository_repositoryIsReturned() {
		given().standaloneSetup(controller).when().get("/repositories/incoming").then().statusCode(200).body("name",
				equalTo("incoming"));
	}
}
