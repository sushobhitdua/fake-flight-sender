package fake.flight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import com.etl.aapi.springframework.ApplicationContextProvider;

@SpringBootApplication(exclude={MongoAutoConfiguration.class})
public class FakeFlightSenderApplication {

	public static void main(String[] args) {
		try {
			ApplicationContextProvider
					.setApplicationContext(SpringApplication.run(FakeFlightSenderApplication.class, args));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
