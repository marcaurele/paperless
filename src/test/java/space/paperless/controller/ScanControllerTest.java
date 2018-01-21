package space.paperless.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.hasItems;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import space.paperless.scanner.Scanner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource("/test-application.properties")
public class ScanControllerTest {

	@Autowired
	private ScanController controller;

	@Autowired
	private Scanner scanner;

	@Test
	public void get_scanSource_scanSourcesAreReturned() {
		given().standaloneSetup(controller).when().get("/scannerSources").then().statusCode(200).body("$",
				hasItems(scanner.getSources()));
	}
}
