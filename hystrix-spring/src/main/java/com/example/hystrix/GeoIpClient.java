package com.example.hystrix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.service.GeoIpDetails;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Component
public class GeoIpClient {
	@Autowired private RestTemplate restTemplate;

	@HystrixCommand(
		groupKey = "GeoIp",
		commandKey = "GetDetails",
		fallbackMethod = "getFallback",
		threadPoolKey = "GeoIp",		
		commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
            @HystrixProperty(name = "metrics.healthSnapshot.intervalInMilliseconds", value = "1000"),
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "20000"),
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
		},
		threadPoolProperties = {
			@HystrixProperty(name = "coreSize", value = "4"),
			@HystrixProperty(name = "maxQueueSize", value = "100")
		}
    )
	public GeoIpDetails getDetails(final String host) {
		return restTemplate.getForObject(
			UriComponentsBuilder
				.fromHttpUrl("http://freegeoip.net/{format}/{host}")
				.buildAndExpand("json", host)
				.toUri(), 
			GeoIpDetails.class);
	}
	
	public GeoIpDetails getFallback(final String host) {
		return new GeoIpDetails();
	}
}
