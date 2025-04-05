package edu.creamcommerce.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("크림 커머스 API")
                    .description("전자상거래 웹사이트 크림 커머스의 REST API 문서")
                    .version("v1.0.0")
            )
            .servers(listOf(Server().url("/")))
    }
}


