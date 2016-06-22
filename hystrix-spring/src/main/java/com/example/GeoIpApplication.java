package com.example;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.servopublisher.HystrixServoMetricsPublisher;
import com.netflix.hystrix.strategy.HystrixPlugins;

@SpringBootApplication
@EnableHystrixDashboard
@EnableHystrix
@EnableScheduling
public class GeoIpApplication {
	@Bean
	RestTemplate restTemplate() {
		final SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

		factory.setConnectTimeout(1000);
		factory.setReadTimeout(3000);

		return new RestTemplate(factory);
	}

	@PostConstruct
	public void init() {
		HystrixPlugins.getInstance().registerMetricsPublisher(
			HystrixServoMetricsPublisher.getInstance());
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(GeoIpApplication.class, args);
	}
}
