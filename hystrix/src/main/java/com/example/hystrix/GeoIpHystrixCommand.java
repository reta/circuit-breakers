package com.example.hystrix;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class GeoIpHystrixCommand extends HystrixCommand<String> {
	// Template: http://freegeoip.net/{format}/{host}
	private static final String URL = "http://freegeoip.net/";
	private final String host;
	
	public GeoIpHystrixCommand(final String host) {
		super(
			Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GeoIp"))
				.andCommandKey(HystrixCommandKey.Factory.asKey("GetDetails"))
				.andCommandPropertiesDefaults(
					HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(5000)
						.withMetricsHealthSnapshotIntervalInMilliseconds(1000)
						.withMetricsRollingStatisticalWindowInMilliseconds(20000)
						.withCircuitBreakerSleepWindowInMilliseconds(10000)
				)
				.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GeoIp"))
				.andThreadPoolPropertiesDefaults(
					HystrixThreadPoolProperties.Setter()
						.withCoreSize(4)
						.withMaxQueueSize(100)
				)
        );
		this.host = host;
	}
	
	@Override
	protected String run() throws Exception {
		return Request
			.Get(new URIBuilder(URL).setPath("/json/" + host).build())
			.connectTimeout(1000)
	        .socketTimeout(3000)
	        .execute()
	        .returnContent()
	        .asString();
	}
	
	@Override
	protected String getFallback() {
		return "{}"; /* empty response */
	}
}
