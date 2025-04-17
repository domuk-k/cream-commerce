package edu.creamcommerce.integration.config

import org.springframework.context.annotation.Configuration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

@Configuration(proxyBeanMethods = false)
class TestcontainersConfig {
    companion object {
        @JvmStatic
        private val mysqlContainer: MySQLContainer<*> =
            MySQLContainer(DockerImageName.parse("mysql:8.0.36"))
                .withDatabaseName("cream_commerce")
                .withUsername("test")
                .withPassword("test")
                .withInitScript("schema.sql")  // src/test/resources/schema.sql
                .apply { start() }
        
        @JvmStatic
        @DynamicPropertySource
        fun registerDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", mysqlContainer::getUsername)
            registry.add("spring.datasource.password", mysqlContainer::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "none" }
        }
    }
}