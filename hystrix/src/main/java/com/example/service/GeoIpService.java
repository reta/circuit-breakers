package com.example.service;

import java.io.IOException;

import com.example.hystrix.GeoIpHystrixCommand;
import com.fasterxml.jackson.databind.ObjectMapper;

import rx.Observable;

public class GeoIpService {
	private final ObjectMapper mapper = new ObjectMapper();
	
	public GeoIpDetails getDetails(final String host) throws IOException {
		return mapper.readValue(new GeoIpHystrixCommand(host).execute(), 
			GeoIpDetails.class);
	}
	
	public Observable<GeoIpDetails> getDetailsObservable(final String host) {
		return new GeoIpHystrixCommand(host)
			.observe()
			.map(result -> {
				try {
					return mapper.readValue(result, GeoIpDetails.class);
				} catch(final IOException ex) {
					throw new RuntimeException(ex);
				}
			});
	}
}
