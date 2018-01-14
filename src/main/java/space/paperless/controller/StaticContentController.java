package space.paperless.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StaticContentController {

	@RequestMapping(value = "/")
	public String index() {
		return "index.html";
	}
}
