package edu.creamcommerce

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["edu.creamcommerce.infrastructure"])
@EntityScan(basePackages = ["edu.creamcommerce.infrastructure"])
@EnableTransactionManagement
class CreamCommerceApplication

fun main(args: Array<String>) {
    runApplication<CreamCommerceApplication>(*args)
}
