package space.paperless.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class DescriptionIndexControllerTest {

	@Autowired
	private DescriptionIndexController controller;

	@Test
	public void get_indexes_fieldTypesAreReturned() {
		ValidatableMockMvcResponse response = given().standaloneSetup(controller).when().get("/indexes").then();

		response.statusCode(200);
		response.body("$.size", equalTo(4));
	}

	@Test
	public void get_folderNamesIndex_indexIsReturned() {
		ValidatableMockMvcResponse response = given().standaloneSetup(controller).when().get("/indexes/type").then();

		response.statusCode(200);
		response.body("descriptionType.name", equalTo("type"));
		response.body("elements.size", equalTo(3)).body("elements", hasItems("type1", "type1/subtype1", "type2"));
	}

	@Test
	public void get_jsonIndex_indexIsReturned() {
		ValidatableMockMvcResponse response = given().standaloneSetup(controller).when().get("/indexes/reference")
				.then();

		response.statusCode(200);
		response.body("descriptionType.name", equalTo("reference"));
		response.body("elements.size", equalTo(5)).body("elements",
				hasItems("Things", "Vehicle", "Vehicle/Tesla", "Vehicle/Valley Forge", "Vehicle/Millenium Falco"));
	}

	@Test
	public void get_unknownDictionary_404Returned() {
		given().standaloneSetup(controller).when().get("/indexes/toto").then().statusCode(404);
	}
}
