package com.orderservice.usecase.impl;

import com.orderservice.infrastructure.entity.ProductOrder;
import com.orderservice.infrastructure.repository.OrderLineRepository;
import com.orderservice.infrastructure.repository.ProductOrderRepository;
import com.orderservice.usecase.dto.OrderRollbackDto;
import com.orderservice.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderRollbackService {

    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final RedisUtils redisUtils;

    public void rollbackOrder(OrderRollbackDto command) {
        redisUtils.increaseStock(command.productId(), command.quantity());
        productOrderRepository.findById(command.productOrderId()).ifPresent(productOrder -> {
            productOrder.rollback(command.totalPrice(), command.totalDiscount());
            cancelOrderLine(productOrder, command.orderLineId(), command.paymentId(), command.deliveryId());
            productOrderRepository.save(productOrder);
        });
    }

    private void cancelOrderLine(ProductOrder productOrder, Long orderLineId, Long paymentId, Long deliveryId) {
        productOrder.getOrderLines().stream()
            .filter(orderLine -> orderLine.getId().equals(orderLineId))
            .findFirst()
            .ifPresent(orderLine -> {
                orderLine.cancelOrderLine(paymentId, deliveryId);
                orderLineRepository.save(orderLine);
            });
    }
}
