package com.example;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.service.GeoIpDetails;
import com.example.service.GeoIpService;
import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;

public class GeoIpRunner {
	private static final Logger LOG = LoggerFactory.getLogger(GeoIpRunner.class);
	
	public static void main(String[] args) throws IOException {
		HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());
		final GeoIpService service = new GeoIpService();
		final GeoIpDetails details = service.getDetails("www.google.com");
		LOG.info("GeoIp response: {}", details);
		System.in.read();
	}
}
