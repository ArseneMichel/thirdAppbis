package com.epsi.thirdAppbis;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import java.util.List;

@EnableCircuitBreaker
@SpringBootApplication
@RestController
public class ThirdAppbisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThirdAppbisApplication.class, args);
	}

	@Autowired
	DiscoveryClient discoveryClient;

	@HystrixCommand(fallbackMethod = "defaultMessage")
	@GetMapping("/")
	public String hello() {
		List<ServiceInstance> instances = discoveryClient.getInstances("name-of-the-microservice1");
		ServiceInstance test = instances.get(0);
		String hostname = test.getHost();
		int port = test.getPort();
		RestTemplate restTemplate = new RestTemplate();
		String microservice1Address = "http://" + hostname + ":" + port;
		ResponseEntity<String> response =
				restTemplate.getForEntity(microservice1Address, String.class);
		String s = response.getBody();
		return s;
	}



	@Autowired
	private LoadBalancerClient loadBalancer;
	@GetMapping("/yeay")
	public void method() {

		ServiceInstance serviceInstance = loadBalancer.choose("name-of-the-microservice1");
				System.out.println(serviceInstance.getUri());
	}


	public String defaultMessage() {
		return "Salut du load balancer !";
	}
}
