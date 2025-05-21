package vn.techmaster.nowj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.annotation.PostConstruct;

@EnableJpaAuditing
@SpringBootApplication
public class NowjApplication {

	public static void main(String[] args) {
		SpringApplication.run(NowjApplication.class, args);
	}

	@PostConstruct
	public void openBrowser() {
		try {
			Thread.sleep(2000);
			String url = "http://localhost:8080/";
			String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
			new ProcessBuilder(chromePath, url).start();
		} catch (Exception ignored) {
		}
	}
}
