package com.mcxgroup.postmates;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;

@Slf4j
@SpringBootApplication
@ServletComponentScan
public class PostmatesApplication {
	public static void main(String[] args) {
		SpringApplication.run(PostmatesApplication.class, args);
		log.info("服务启动成功");
	}

}
