package com.orderservice.usecase.impl;

import com.orderservice.entity.ProductOrder;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.dto.OrderRollbackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderRollbackService {

    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;

    public void rollbackOrder(OrderRollbackDto command) {
        productOrderRepository.findById(command.productOrderId()).ifPresent(productOrder -> {
            productOrder.rollback(command.totalPrice(), command.totalDiscount());
            cancelOrderLine(productOrder, command.orderLineId());
            productOrderRepository.save(productOrder);
        });
    }
    private void cancelOrderLine(ProductOrder productOrder, Long orderLineId) {
        productOrder.getOrderLines().stream()
            .filter(orderLine -> orderLine.getId().equals(orderLineId))
            .findFirst()
            .ifPresent(orderLine -> {
                orderLine.cancelOrderLine();
                orderLineRepository.save(orderLine);
            });
    }
}
