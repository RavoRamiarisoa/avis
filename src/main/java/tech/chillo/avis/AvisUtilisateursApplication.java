package tech.chillo.avis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AvisUtilisateursApplication {

	public static void main(final String[] args) {
		SpringApplication.run(AvisUtilisateursApplication.class, args);
	}

}
