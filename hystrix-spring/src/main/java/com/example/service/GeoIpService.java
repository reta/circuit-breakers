package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.hystrix.GeoIpClient;

@Service
public class GeoIpService {
	private static final Logger LOG = LoggerFactory.getLogger(GeoIpService.class);
	
	@Autowired private GeoIpClient client;
	
	@Scheduled(fixedDelay = 10000)
	public void getDetails() {
		final GeoIpDetails details = client.getDetails("www.google.com");
		LOG.info("GeoIp response: {}", details);
	}
}
