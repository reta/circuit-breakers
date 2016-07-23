package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.service.GeoIpDetails;
import com.example.service.GeoIpService;

public class GeoIpRunner {
	private static final Logger LOG = LoggerFactory.getLogger(GeoIpRunner.class);
	
	public static void main(String[] args) throws Exception {
		final GeoIpService service = new GeoIpService();
		final GeoIpDetails details = service.getDetails("www.google.com");
		LOG.info("GeoIp response: {}", details);
		System.in.read();
	}
}
