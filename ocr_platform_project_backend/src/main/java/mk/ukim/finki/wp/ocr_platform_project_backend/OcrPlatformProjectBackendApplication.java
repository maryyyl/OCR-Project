package mk.ukim.finki.wp.ocr_platform_project_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OcrPlatformProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcrPlatformProjectBackendApplication.class, args);
    }

}
