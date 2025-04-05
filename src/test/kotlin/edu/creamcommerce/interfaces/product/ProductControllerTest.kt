package edu.creamcommerce.interfaces.product

import io.kotest.core.spec.style.ShouldSpec
import org.hamcrest.Matchers.anything
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@WebMvcTest(ProductController::class)
class ProductControllerTest : ShouldSpec({
    val mockMvc = MockMvcBuilders.standaloneSetup(ProductController()).build()

    should("GET /api/products returns products for category '의류'") {
        mockMvc.perform(
            get("/api/products")
                .param("category", "의류")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.products[0].name").value(anything()))
            .andExpect(jsonPath("$.totalCount").value(2))
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.totalPages").value(1))
    }

    should("GET /api/products/1 returns product detail for id 1") {
        mockMvc.perform(
            get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(anything()))
            .andExpect(jsonPath("$.category").value(anything()))
    }

    should("GET /api/products/top-selling returns top selling products") {
        mockMvc.perform(
            get("/api/products/top-selling")
                .param("limit", "2")
                .param("period", "WEEKLY")
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.products[0].name").value(anything()))
            .andExpect(jsonPath("$.period").value(anything()))
    }
})