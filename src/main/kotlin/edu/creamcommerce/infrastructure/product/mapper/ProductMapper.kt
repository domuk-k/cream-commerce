package edu.creamcommerce.infrastructure.product.mapper

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.*
import edu.creamcommerce.infrastructure.product.entity.InventoryEntity
import edu.creamcommerce.infrastructure.product.entity.ProductEntity
import edu.creamcommerce.infrastructure.product.entity.ProductOptionEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ProductMapper {
    // Product 매핑
    fun toEntity(domain: Product): ProductEntity {
        return ProductEntity(
            id = domain.id.value,
            name = domain.name,
            description = domain.description,
            price = domain.price.amount,
            status = domain.status,
            stockStatus = domain.stockStatus,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    fun updateEntity(entity: ProductEntity, domain: Product): ProductEntity {
        entity.name = domain.name
        entity.description = domain.description
        entity.price = domain.price.amount
        entity.status = domain.status
        entity.stockStatus = domain.stockStatus
        entity.updatedAt = LocalDateTime.now()
        return entity
    }
    
    fun toDomain(entity: ProductEntity): Product {
        return Product.create(
            id = ProductId(entity.id),
            name = entity.name,
            description = entity.description,
            price = Money(entity.price),
            options = emptyList()
        ).apply {
            // 상태 동기화 (필요시)
            when (entity.status) {
                this.status -> {} // 이미 같으면 변경 불필요
                ProductStatus.Active -> this.activate()
                ProductStatus.Suspended -> this.suspend()
                ProductStatus.Discontinued -> this.discontinue()
                ProductStatus.Draft -> {} // 기본 상태이므로 변경 불필요
            }
        }
    }
    
    // ProductOption 매핑
    fun toEntity(domain: ProductOption): ProductOptionEntity {
        return ProductOptionEntity(
            id = domain.id.value,
            productId = domain.productId.value,
            name = domain.name,
            additionalPrice = domain.additionalPrice.amount,
            sku = domain.sku,
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    fun updateEntity(entity: ProductOptionEntity, domain: ProductOption): ProductOptionEntity {
        entity.name = domain.name
        entity.additionalPrice = domain.additionalPrice.amount
        entity.sku = domain.sku
        entity.status = domain.status
        entity.updatedAt = LocalDateTime.now()
        return entity
    }
    
    fun toDomain(entity: ProductOptionEntity, inventoryEntity: InventoryEntity?): ProductOption {
        return ProductOption.create(
            id = OptionId(entity.id),
            productId = ProductId(entity.productId),
            name = entity.name,
            additionalPrice = Money(entity.additionalPrice),
            sku = entity.sku,
            stock = inventoryEntity?.quantity ?: 0,
        )
    }
    
    // Inventory 매핑
    fun toEntity(domain: Inventory): InventoryEntity {
        return InventoryEntity(
            optionId = domain.optionId.value,
            quantity = domain.quantity,
            lowStockThreshold = domain.lowStockThreshold,
            status = domain.status,
            lastUpdated = domain.lastUpdated
        )
    }
    
    fun updateEntity(entity: InventoryEntity, domain: Inventory): InventoryEntity {
        entity.quantity = domain.quantity
        entity.lowStockThreshold = domain.lowStockThreshold
        entity.status = domain.status
        entity.lastUpdated = LocalDateTime.now()
        return entity
    }
    
    fun toDomain(entity: InventoryEntity): Inventory {
        return Inventory.create(
            optionId = OptionId(entity.optionId),
            quantity = entity.quantity,
            lowStockThreshold = entity.lowStockThreshold,
        )
    }
} 