package edu.creamcommerce.integration

import edu.creamcommerce.application.product.dto.command.AddProductOptionCommand
import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.command.UpdateProductCommand
import edu.creamcommerce.application.product.dto.command.UpdateProductStatusCommand
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.dto.query.GetTopProductsQuery
import edu.creamcommerce.application.product.usecase.*
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional
class ProductIntegrationTest : BaseIntegrationTest() {
    
    @Autowired
    private lateinit var createProductUseCase: CreateProductUseCase
    
    @Autowired
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    
    @Autowired
    private lateinit var getProductsUseCase: GetProductsUseCase
    
    @Autowired
    private lateinit var getTopProductsUseCase: GetTopProductsUseCase
    
    @Autowired
    private lateinit var updateProductUseCase: UpdateProductUseCase
    
    @Autowired
    private lateinit var updateProductStatusUseCase: UpdateProductStatusUseCase
    
    @Autowired
    private lateinit var addProductOptionUseCase: AddProductOptionUseCase
    
    @Autowired
    private lateinit var removeProductOptionUseCase: RemoveProductOptionUseCase
    
    private var productId = ProductId("")
    
    @BeforeEach
    fun setUp() {
        val createCommand = CreateProductCommand(
            name = "테스트 상품",
            price = BigDecimal.valueOf(10000),
        )
        
        val product = createProductUseCase(createCommand)
        productId = ProductId(product.id)
    }
    
    @Nested
    @DisplayName("상품 생성 유스케이스")
    inner class CreateProductTest {
        
        @Test
        @DisplayName("상품 생성 시 기본 정보가 정확히 저장된다")
        fun createProduct() {
            // given
            val command = CreateProductCommand(
                name = "신규 테스트 상품",
                price = BigDecimal.valueOf(15000),
            )
            
            // when
            val result = createProductUseCase(command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.name).isEqualTo(command.name)
            assertThat(result.price).isEqualTo(command.price)
        }
    }
    
    @Nested
    @DisplayName("상품 조회 유스케이스")
    inner class GetProductTest {
        
        @Test
        @DisplayName("ID로 상품 조회 시 정확한 상품 정보를 반환한다")
        fun getProductById() {
            // when
            val result = getProductByIdUseCase(productId)
            
            // then
            assertThat(result).isNotNull
            assertThat(result?.id).isEqualTo(productId.value)
            assertThat(result?.name).isEqualTo("테스트 상품")
        }
        
        @Test
        @DisplayName("상품 목록 조회 시 페이지네이션이 정확히 동작한다")
        fun getProducts() {
            // given
            // 추가 상품 9개 생성 (이미 1개 있으므로 총 10개)
            for (i in 1..9) {
                createProductUseCase(
                    CreateProductCommand(
                        name = "추가 상품 $i",
                        price = BigDecimal.valueOf(1000 * i.toLong()),
                    )
                )
            }
            
            // when
            val query = GetProductsQuery(page = 0, size = 5)
            val result = getProductsUseCase(query)
            
            // then
            assertThat(result.products).hasSize(5)
            assertThat(result.total).isEqualTo(10)
            assertThat(result.page).isEqualTo(0)
        }
        
        @Test
        @DisplayName("인기 상품 조회 시 상위 상품 목록을 반환한다")
        fun getTopProducts() {
            // given
            // 상품 추가 생성
            for (i in 1..5) {
                createProductUseCase(
                    CreateProductCommand(
                        name = "인기 상품 후보 $i",
                        price = BigDecimal.valueOf(5000 * i.toLong()),
                    )
                )
            }
            
            // when
            val result = getTopProductsUseCase(GetTopProductsQuery(3)) // 상위 3개 상품
            
            // then
            assertThat(result).hasSize(3)
        }
    }
    
    @Nested
    @DisplayName("상품 업데이트 유스케이스")
    inner class UpdateProductTest {
        
        @Test
        @DisplayName("상품 정보 업데이트 시 변경 내용이 정확히 반영된다")
        fun updateProduct() {
            // given
            val command = UpdateProductCommand(
                name = "수정된 상품명",
                description = "수정된 상품 설명",
                price = BigDecimal.valueOf(12000)
            )
            
            // when
            val result = updateProductUseCase(productId, command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.id).isEqualTo(productId.value)
            assertThat(result.name).isEqualTo(command.name)
            assertThat(result.description).isEqualTo(command.description)
            assertThat(result.price).isEqualTo(command.price)
            
            // 변경 내용 실제 적용 확인
            val updatedProduct = getProductByIdUseCase(productId)
            assertThat(updatedProduct?.name).isEqualTo(command.name)
        }
        
        @Test
        @DisplayName("상품 상태 업데이트 시 상태가 정확히 변경된다")
        fun updateProductStatus() {
            // given
            val command = UpdateProductStatusCommand(
                status = ProductStatus.Discontinued,
            )
            
            // when
            val result = updateProductStatusUseCase(productId, command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.id).isEqualTo(productId.value)
            assertThat(result.status).isEqualTo(command.status)
            
            // 변경 내용 실제 적용 확인
            val updatedProduct = getProductByIdUseCase(productId)
            assertThat(updatedProduct?.status).isEqualTo(command.status)
        }
    }
    
    @Nested
    @DisplayName("상품 옵션 관리 유스케이스")
    inner class ProductOptionTest {
        
        @Test
        @DisplayName("상품 옵션 추가 시 옵션이 정상적으로 등록된다")
        fun addProductOption() {
            // given
            val command = AddProductOptionCommand(
                name = "블랙/M",
                additionalPrice = BigDecimal.ZERO,
                sku = "TEST-BLACK-M-001",
                stock = 6
            )
            
            // when
            val option = addProductOptionUseCase(productId, command)
            
            // then
            assertThat(option.productId).isEqualTo(productId.value)
            
            // 상품 조회 시 옵션 정보 포함 확인
            val product = getProductByIdUseCase(productId)
            assertThat(product?.options).isNotEmpty
            assertThat(product?.options?.size).isEqualTo(1)
        }
        
        @Test
        @DisplayName("상품 옵션 제거 시 옵션이 정상적으로 삭제된다")
        fun removeProductOption() {
            // given
            // 옵션 추가
            val addCommand = AddProductOptionCommand(
                name = "화이트/L",
                additionalPrice = BigDecimal.valueOf(1000),
                stock = 5,
                sku = "TEST-WHITE-L-001"
            )
            
            // 옵션 추가 후 상품 조회
            addProductOptionUseCase(productId, addCommand)
            val productBeforeRemove = getProductByIdUseCase(productId)
            
            // 옵션 상태 체크
            println("제거 전 옵션 개수: ${productBeforeRemove?.options?.size}")
            println("제거 전 옵션 ID 목록: ${productBeforeRemove?.options?.map { it.id }}")
            
            // 실제 존재하는 첫 번째 옵션의 ID를 사용하여 제거
            val firstOptionId = productBeforeRemove?.options?.firstOrNull()?.id
                ?: throw IllegalStateException("옵션이 존재하지 않습니다")
            
            // 첫 번째 옵션 제거    
            println("제거할 옵션 ID: $firstOptionId")
            removeProductOptionUseCase(productId, OptionId(firstOptionId))
            
            // 명시적으로 DB에서 직접 조회하여 확인
            val afterRemoveProduct = getProductByIdUseCase(productId)
            println("제거 후 옵션 개수: ${afterRemoveProduct?.options?.size}")
            println("제거 후 옵션 ID 목록: ${afterRemoveProduct?.options?.map { it.id }}")
            
            // 검증
            assertThat(afterRemoveProduct?.options).isEmpty()
        }
    }
    
    @Nested
    @DisplayName("상품 통합 시나리오")
    inner class IntegratedProductScenarioTest {
        
        @Test
        @DisplayName("상품 생성, 옵션 추가, 정보 수정, 조회가 정상적으로 동작한다")
        fun createAndManageProduct() {
            // given
            val createCommand = CreateProductCommand(
                name = "통합 테스트 상품",
                price = BigDecimal.valueOf(30000),
            )
            
            // when - 생성
            val product = createProductUseCase(createCommand)
            val productId = ProductId(product.id)
            
            // 옵션 추가
            val option1 = addProductOptionUseCase(
                productId,
                AddProductOptionCommand(
                    name = "레드/S",
                    additionalPrice = BigDecimal.valueOf(0),
                    sku = "INT-RED-S-001",
                    stock = 10
                )
            )
            
            val option2 = addProductOptionUseCase(
                productId,
                AddProductOptionCommand(
                    name = "레드/M",
                    additionalPrice = BigDecimal.valueOf(1000),
                    sku = "INT-RED-M-001",
                    stock = 5
                )
            )
            
            // 상품 정보 수정
            val updatedProduct = updateProductUseCase(
                productId,
                UpdateProductCommand(
                    name = "수정된 통합 테스트 상품",
                    description = "수정된 설명",
                    price = BigDecimal.valueOf(35000)
                )
            )
            
            // then
            // 최종 상품 조회
            val finalProduct = getProductByIdUseCase(productId)
            
            assertThat(finalProduct).isNotNull
            assertThat(finalProduct?.name).isEqualTo("수정된 통합 테스트 상품")
            assertThat(finalProduct?.price).isEqualTo(BigDecimal.valueOf(35000))
            assertThat(finalProduct?.options).hasSize(2)
            
            // 옵션 상태 체크
            val productBeforeRemove = getProductByIdUseCase(productId)
            
            // 첫 번째 옵션 제거
            removeProductOptionUseCase(productId, OptionId(option1.id))
            
            // 명시적으로 DB에서 직접 조회하여 확인 (영속성 컨텍스트가 초기화된 상태)
            val afterRemoveProduct = getProductByIdUseCase(productId)
            println("통합 시나리오 - 제거 후 옵션 개수: ${afterRemoveProduct?.options?.size}")
            println("통합 시나리오 - 제거 후 옵션 ID 목록: ${afterRemoveProduct?.options?.map { it.id }}")
            
            // 검증
            assertThat(afterRemoveProduct?.options).hasSize(1)
            assertThat(afterRemoveProduct?.options?.map { it.id }).doesNotContain(option1.id)
            assertThat(afterRemoveProduct?.options?.first()?.name).isEqualTo("레드/M")
        }
    }
} 