package edu.creamcommerce.infrastructure.order.repository

import edu.creamcommerce.domain.order.*
import edu.creamcommerce.infrastructure.order.mapper.OrderMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class OrderRepositoryImpl(
    private val jpaOrderRepository: JpaOrderRepository,
    private val jpaOrderItemRepository: JpaOrderItemRepository,
    private val orderMapper: OrderMapper
) : OrderRepository {
    
    @Transactional
    override fun save(order: Order): Order {
        // 주문 엔티티 저장
        val orderEntity = if (order.id != null) {
            val existingEntity = jpaOrderRepository.findById(order.id!!.value).orElse(null)
            if (existingEntity != null) {
                orderMapper.updateEntity(existingEntity, order)
            } else {
                orderMapper.toEntity(order)
            }
        } else {
            orderMapper.toEntity(order)
        }
        
        val savedOrderEntity = jpaOrderRepository.save(orderEntity)
        
        // 주문 아이템 저장
        order.orderItems.forEach { orderItem ->
            saveOrderItem(orderItem, OrderId(savedOrderEntity.id))
        }
        
        return findById(OrderId(savedOrderEntity.id))!!
    }
    
    override fun findById(id: OrderId): Order? {
        val orderEntity = jpaOrderRepository.findById(id.value).orElse(null) ?: return null
        val orderItems = findOrderItemsByOrderId(id)
        
        return orderMapper.toDomain(orderEntity, orderItems)
    }
    
    override fun findByUserId(userId: String): List<Order> {
        return jpaOrderRepository.findByUserId(userId).map { orderEntity ->
            val orderItems = findOrderItemsByOrderId(OrderId(orderEntity.id))
            orderMapper.toDomain(orderEntity, orderItems)
        }
    }
    
    override fun findAll(): List<Order> {
        return jpaOrderRepository.findAll().map { orderEntity ->
            val orderItems = findOrderItemsByOrderId(OrderId(orderEntity.id))
            orderMapper.toDomain(orderEntity, orderItems)
        }
    }
    
    // 주문 아이템 관련
    override fun findOrderItemById(id: OrderItemId): OrderItem? {
        return jpaOrderItemRepository.findById(id.value).orElse(null)?.let {
            orderMapper.toDomain(it)
        }
    }
    
    override fun findOrderItemsByOrderId(orderId: OrderId): List<OrderItem> {
        return jpaOrderItemRepository.findByOrderId(orderId.value).map {
            orderMapper.toDomain(it)
        }
    }
    
    @Transactional
    private fun saveOrderItem(orderItem: OrderItem, orderId: OrderId): OrderItem {
        val entity = if (orderItem.id != null) {
            val existingEntity = jpaOrderItemRepository.findById(orderItem.id!!.value).orElse(null)
            if (existingEntity != null) {
                orderMapper.updateEntity(existingEntity, orderItem)
            } else {
                orderMapper.toEntity(orderItem, orderId)
            }
        } else {
            orderMapper.toEntity(orderItem, orderId)
        }
        
        val savedEntity = jpaOrderItemRepository.save(entity)
        return orderMapper.toDomain(savedEntity)
    }
} 