package com.bookpricems.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bookpricems.info.BookInfo;
import com.bookpricems.info.BookPriceInfo;

@RestController
public class BookSearchController {
	
	@Autowired
	LoadBalancerClient loadBalancerClient;
	
	@GetMapping("/mybook/{bookId}") 
	public BookInfo getBookById(@PathVariable Integer bookId) {
		System.out.println("BookSearchController -- getBookById");
		
		BookInfo bookInfo = new BookInfo(bookId, "Master Spring Boot 2", "Srinivas", "JLC", "Java");
		
		ServiceInstance instance = loadBalancerClient.choose("MyBookPriceMS");
		String baseURL = instance.getUri().toString();
		System.out.println("Base URL : "+baseURL);
		
		String endpoint=baseURL+"/bookPrice/"+bookId; 
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<BookPriceInfo> respEntity = restTemplate.getForEntity(endpoint, BookPriceInfo.class);
		BookPriceInfo bookPriceInfo = respEntity.getBody();
		bookInfo.setPrice(bookPriceInfo.getPrice());
		bookInfo.setOffer(bookPriceInfo.getOffer());
		bookInfo.setServerPort(bookPriceInfo.getServerPort());
		
		return bookInfo;
	}
	
}
