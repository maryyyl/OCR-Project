//package mk.ukim.finki.wp.ocr_platform_project_backend.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class OpenAiConfig {
//    @Value("${openai.api.key}")
//    private String apiKey;
//
//    @Bean
//    public WebClient openAiWebClient() {
//        return WebClient.builder()
//                .baseUrl("https://api.openai.com")
//                .defaultHeader("Authorization", "Bearer " + apiKey)
//                .defaultHeader("Content-Type", "application/json")
//                .codecs(c -> c.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
//                .build();
//    }
//}