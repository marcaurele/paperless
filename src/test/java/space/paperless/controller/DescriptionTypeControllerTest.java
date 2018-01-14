package space.paperless.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.hasItems;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import space.paperless.controller.DescriptionTypeController;
import space.paperless.domain.DescriptionType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class DescriptionTypeControllerTest {

	@Autowired
	private DescriptionTypeController controller;

	@Test
	public void get_descriptionFields_fieldTypesAreReturned() {
		given().standaloneSetup(controller).when().get("/descriptionTypes").then().statusCode(200).body("name",
				hasItems(
						Arrays.asList(DescriptionType.values()).stream().map(t -> t.getName()).toArray(String[]::new)));
	}
}
