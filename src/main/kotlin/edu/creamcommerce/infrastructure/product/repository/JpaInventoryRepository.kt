package edu.creamcommerce.infrastructure.product.repository

import edu.creamcommerce.infrastructure.product.entity.InventoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaInventoryRepository : JpaRepository<InventoryEntity, String> {
    fun findAllByOptionIdIn(optionIds: List<String>): List<InventoryEntity>
}