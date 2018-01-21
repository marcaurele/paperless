package space.paperless.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;

import java.io.File;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import space.paperless.domain.ScanOptions;
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

	@Test
	@Ignore
	public void post_scans_documentIsScanned() throws JsonProcessingException {
		ScanOptions options = new ScanOptions();

		options.setNumber(1);
		options.setSource("flatbed");

		ObjectMapper objectMapper = new ObjectMapper();

		given().standaloneSetup(controller).contentType("application/json")
				.body(objectMapper.writeValueAsString(options)).when().post("/scans").then().statusCode(200)
				.body("success", equalTo(true)).body("output", equalTo("")).body("fileName", new BaseMatcher<String>() {

					@Override
					public boolean matches(Object item) {
						File pdf = new File(item.toString());
						boolean exists = pdf.exists();

						if (exists) {
							pdf.delete();
						}

						return exists;
					}

					@Override
					public void describeTo(Description description) {
					}
				});
	}

}
