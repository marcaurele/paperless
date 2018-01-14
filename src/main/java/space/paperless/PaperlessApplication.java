package space.paperless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "space.paperless" })
public class PaperlessApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaperlessApplication.class, args);
	}
}
