package fake.flight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.etl.aapi.common.DataSource;
import com.etl.aapi.common.data.ErrorResponse;
import com.etl.aapi.flight.data.Acknowledgement;
import com.etl.aapi.flight.data.Flight;
import com.etl.aapi.flight.data.search.SearchCriteria;
import com.etl.aapi.flight.utils.HttpClientUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class FakeSender {

	@RequestMapping(path = "/search/oneway", method = RequestMethod.POST)
	public Acknowledgement onewaySearch(@RequestBody SearchCriteria searchCriteria) throws Exception {

		String onewayPayloadPath = "S:/etl_git/fake-flight-sender/oneway-flight.json";
		String flightJson = convertFileDataIntoString(onewayPayloadPath);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		Flight flight = mapper.readValue(flightJson, Flight.class);

		HttpClientUtils.postAsyncFlightData("http://127.0.0.1:8080/aggregator-api/results/add-flight",
				searchCriteria.getSearchId(), null, "OUTBOUND", flight, "TEK_TRAVELV10");
		System.out.println("/v10/search/oneway");

		Acknowledgement ack = new Acknowledgement();
		ack.addSupplier(DataSource.TEKTRAVELV10.toString());
		ack.setScraperAcknowledgement(false);

		ack.setAckCode("RECEIVED");

		return ack;
	}

	@RequestMapping(path = "/search/roundtrip", method = RequestMethod.POST)
	public String roundtripSearch() {
		System.out.println("/v10/search/roundtrip");
		return "/search/roundtrip";
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(HttpServletRequest req, Exception e) {
		ErrorResponse error = new ErrorResponse(500, e.getMessage());
		return new ResponseEntity<>(error, HttpStatus.OK);
	}

	private static String convertFileDataIntoString(String onewayPayloadPath) throws IOException {
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			br = new BufferedReader(new FileReader(onewayPayloadPath));
			sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
		} catch (Exception e) {
			System.out.println("error occur during reading file data and appending it into a string");
			throw new IOException(e.getMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("error occur during closing of BufferReader");
				throw new IOException(e.getMessage());
			}
		}
		return sb.toString();
	}
}
