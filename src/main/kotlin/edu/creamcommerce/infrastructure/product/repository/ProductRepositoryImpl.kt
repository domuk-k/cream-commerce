package edu.creamcommerce.infrastructure.product.repository

import edu.creamcommerce.domain.product.*
import edu.creamcommerce.infrastructure.product.mapper.ProductMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class ProductRepositoryImpl(
    private val jpaProductRepository: JpaProductRepository,
    private val jpaProductOptionRepository: JpaProductOptionRepository,
    private val jpaInventoryRepository: JpaInventoryRepository,
    private val productMapper: ProductMapper
) : ProductRepository {
    
    // 상품 관련
    override fun findById(id: ProductId): Product? {
        return jpaProductRepository.findById(id.value).orElse(null)?.let {
            val product = productMapper.toDomain(it)
            val options = findOptionsByProductId(id)
            
            options.forEach { option ->
                product.addOption(option)
            }
            
            product
        }
    }
    
    override fun findAll(): List<Product> {
        val products = jpaProductRepository.findAll().map {
            productMapper.toDomain(it)
        }
        
        products.forEach { product ->
            val options = findOptionsByProductId(product.id)
            options.forEach { option ->
                product.addOption(option)
            }
        }
        
        return products
    }
    
    @Transactional
    override fun save(product: Product): Product {
        val entity = if (product.id != null) {
            val existingEntity = jpaProductRepository.findById(product.id!!.value).orElse(null)
            // 기존 옵션 목록 조회
            val existingOptions = findOptionsByProductId(product.id)
            
            // 도메인 모델에 없지만 DB에 있는 옵션은 삭제
            val currentOptionIds = product.options.map { it.id.value }.toSet()
            existingOptions.forEach { option ->
                if (!currentOptionIds.contains(option.id.value)) {
                    deleteOptionById(option.id)
                }
            }
            
            // 현재 옵션 저장 (중복 저장 로직 제거)
            product.options.forEach { option -> saveOption(option) }
            
            if (existingEntity != null) {
                productMapper.updateEntity(existingEntity, product)
            } else {
                productMapper.toEntity(product)
            }
        } else {
            productMapper.toEntity(product)
        }
        
        val savedEntity = jpaProductRepository.save(entity)
        val savedProduct = productMapper.toDomain(savedEntity)
        
        // 저장된 상품에 옵션 로드
        val options = findOptionsByProductId(savedProduct.id)
        options.forEach { option ->
            savedProduct.addOption(option)
        }
        
        return savedProduct
    }
    
    // 상품 옵션 관련
    override fun findOptionById(id: OptionId): ProductOption? {
        val optionEntity = jpaProductOptionRepository.findById(id.value).orElse(null)
        val inventoryEntity = jpaInventoryRepository.findById(id.value).orElse(null)
        
        return productMapper.toDomain(optionEntity, inventoryEntity)
    }
    
    override fun findOptionsByProductId(productId: ProductId): List<ProductOption> {
        // 최신 상태를 얻기 위해 영속성 컨텍스트 초기화
        // entityManager.clear()  // 필요하면 주석 해제
        
        val optionEntities = jpaProductOptionRepository.findAllByProductId(productId.value)
        
        if (optionEntities.isEmpty()) {
            return emptyList()
        }
        
        val inventoryMap = jpaInventoryRepository
            .findAllByOptionIdIn(optionEntities.map { it.id })
            .associateBy { it.optionId }
        
        return optionEntities.map { optEnt ->
            // 옵션에 해당하는 인벤토리가 없을 경우 새로 생성
            val inventoryEntity = inventoryMap[optEnt.id] ?: run {
                // 새 인벤토리 생성 및 저장
                val newInventory = Inventory.create(
                    optionId = OptionId(optEnt.id),
                    quantity = 0,
                    lowStockThreshold = 5
                )
                val entity = productMapper.toEntity(newInventory)
                // 저장 결과를 반환
                jpaInventoryRepository.save(entity)
            }
            
            productMapper.toDomain(optEnt, inventoryEntity)
        }
    }
    
    @Transactional
    override fun saveOption(productOption: ProductOption): ProductOption {
        val entity = if (productOption.id != null) {
            val existingEntity = jpaProductOptionRepository.findById(productOption.id!!.value).orElse(null)
            if (existingEntity != null) {
                productMapper.updateEntity(existingEntity, productOption)
            } else {
                productMapper.toEntity(productOption)
            }
        } else {
            productMapper.toEntity(productOption)
        }
        
        val savedEntity = jpaProductOptionRepository.save(entity)
        // 변경사항을 즉시 데이터베이스에 반영
        jpaProductOptionRepository.flush()
        
        // 인벤토리 찾기
        var inventoryEntity = jpaInventoryRepository.findById(savedEntity.id).orElse(null)
        
        // 인벤토리가 없으면 새로 생성하고 저장
        if (inventoryEntity == null) {
            val newInventory = Inventory.create(
                optionId = OptionId(savedEntity.id),
                quantity = productOption.inventory.quantity,
                lowStockThreshold = productOption.inventory.lowStockThreshold
            )
            inventoryEntity = jpaInventoryRepository.save(productMapper.toEntity(newInventory))
            // 인벤토리 변경사항도 즉시 데이터베이스에 반영
            jpaInventoryRepository.flush()
        }
        
        return productMapper.toDomain(savedEntity, inventoryEntity)
    }
    
    @Transactional
    override fun deleteOptionById(id: OptionId) {
        try {
            // 인벤토리 삭제 (ID로 직접 삭제)
            jpaInventoryRepository.deleteById(id.value)
        } catch (e: Exception) {
            // 인벤토리가 없는 경우 무시
        }
        
        try {
            // 옵션 삭제 (ID로 직접 삭제)
            jpaProductOptionRepository.deleteById(id.value)
        } catch (e: Exception) {
            // 옵션이 없는 경우 무시
        }
        
        // 명시적으로 SQL을 실행하여 확실히 삭제
        jpaProductOptionRepository.flush()
    }
    
    override fun findInventoryByOptionId(optionId: OptionId): Inventory? {
        return jpaInventoryRepository.findById(optionId.value).orElse(null)?.let {
            productMapper.toDomain(it)
        }
    }
    
    @Transactional
    override fun saveInventory(inventory: Inventory): Inventory {
        val entity = jpaInventoryRepository.findById(inventory.optionId.value)
            .map { existingEntity ->
                productMapper.updateEntity(existingEntity, inventory)
            }
            .orElseGet { productMapper.toEntity(inventory) }
        
        val savedEntity = jpaInventoryRepository.save(entity)
        return productMapper.toDomain(savedEntity)
    }
} 