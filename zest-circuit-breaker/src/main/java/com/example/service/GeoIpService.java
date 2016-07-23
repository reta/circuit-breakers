package com.example.service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;

import javax.management.ObjectName;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.qi4j.library.circuitbreaker.CircuitBreaker;
import org.qi4j.library.circuitbreaker.jmx.CircuitBreakerJMX;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GeoIpService {
	private static final String URL = "http://freegeoip.net/";
	private final ObjectMapper mapper = new ObjectMapper();
	private final CircuitBreaker breaker = new CircuitBreaker(5, 1000 * 120);
	
	public GeoIpService() throws Exception {
		final ObjectName name = new ObjectName("circuit-breakers", "zest-circuit-breaker", "freegeoip.net");
		ManagementFactory.getPlatformMBeanServer().registerMBean(new CircuitBreakerJMX(breaker, name), name);
	}

	public GeoIpDetails getDetails(final String host) {
		try {
			if (breaker.isOn()) {
				final GeoIpDetails details = mapper.readValue(get(host), GeoIpDetails.class);
				breaker.success();
				return details;
			} else /* fallback to empty response */ {
				return new GeoIpDetails();
			}
		} catch (final IOException ex) {
			breaker.throwable(ex);
			throw new RuntimeException("Communication with '" + URL + "' failed", ex);
		} catch (final URISyntaxException ex) {
			breaker.trip(); /* hard exception but should never happen, just trip circuit breaker immediately */
			throw new RuntimeException("Invalid service endpoint: " + URL, ex);
		}
	}

	private String get(final String host) throws IOException, URISyntaxException {
		return Request
			.Get(new URIBuilder(URL).setPath("/json/" + host).build())
			.connectTimeout(1000)
			.socketTimeout(3000)
			.execute()
			.returnContent()
			.asString();
	}
}
