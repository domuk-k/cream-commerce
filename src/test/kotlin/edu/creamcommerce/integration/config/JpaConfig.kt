package edu.creamcommerce.integration.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@Import(TestcontainersConfig::class)
@EnableTransactionManagement
class JpaConfig