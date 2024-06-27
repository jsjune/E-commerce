package com.orderservice.usecase.impl;


import com.orderservice.adapter.DeliveryClient;
import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.PaymentClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.adapter.req.ProcessDeliveryRequest;
import com.orderservice.adapter.res.CartDto;
import com.orderservice.adapter.res.PaymentDto;
import com.orderservice.adapter.res.ProductDto;
import com.orderservice.adapter.req.ProcessPaymentRequest;
import com.orderservice.entity.ProdcutOrderStatus;
import com.orderservice.entity.ProductOrder;
import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.controller.res.OrderListResponseDto;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderLineStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.repository.ProductOrderRepository;
import com.orderservice.usecase.kafka.OrderKafkaProducer;
import com.orderservice.usecase.OrderUseCase;
import com.orderservice.usecase.dto.OrderDto;
import com.orderservice.usecase.dto.RegisterOrderOfCartDto;
import com.orderservice.usecase.dto.RegisterOrderOfProductDto;
import com.orderservice.usecase.kafka.event.OrderLineEvent;
import com.orderservice.usecase.kafka.event.ProductOrderEvent;
import com.orderservice.utils.error.ErrorCode;
import com.orderservice.utils.error.GlobalException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService implements OrderUseCase {

    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final MemberClient memberClient;
    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;
    private final OrderKafkaProducer orderKafkaProducer;

    @Override
    public OrderDetailResponseDto registerOrderOfCart(Long memberId, RegisterOrderOfCartDto command) {
        long totalPrice = 0L;
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProdcutOrderStatus.INITIATED)
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        List<OrderLine> orderLines = new ArrayList<>();
        List<CartDto> cartList = memberClient.getCartList(memberId, command.cartIds());
        for (CartDto cart : cartList) {
            totalPrice += cart.price() * cart.quantity();
            OrderLine orderLine = OrderLine.builder()
                .productId(cart.productId())
                .productName(cart.productName())
                .price(cart.price())
                .quantity(cart.quantity())
                .thumbnailUrl(cart.thumbnailUrl())
                .discount(0L)
                .orderLineStatus(OrderLineStatus.INITIATED)
                .build();
            orderLines.add(orderLine);
            productOrder.addOrderLine(orderLine);

        }
        productOrder.assignTotalPrice(totalPrice);
        orderLineRepository.saveAll(orderLines);
        return OrderDetailResponseDto.builder()
            .productOrderId(productOrder.getId())
            .orderLines(productOrder.getOrderLines())
            .orderStatus(productOrder.getProductOrderStatus().name())
            .totalPrice(productOrder.getTotalPrice())
            .totalDiscount(productOrder.getTotalDiscount())
            .build();
    }

    @Override
    public OrderDetailResponseDto registerOrder(Long memberId, RegisterOrderOfProductDto command) {
        ProductDto product = productClient.getProduct(command.productId());
        if (product == null) {
            throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProdcutOrderStatus.INITIATED)
            .totalPrice(product.price() * command.quantity())
            .totalDiscount(0L)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productId(product.productId())
            .productName(product.productName())
            .price(product.price())
            .thumbnailUrl(product.thumbnailUrl())
            .quantity(command.quantity())
            .discount(0L)
            .orderLineStatus(OrderLineStatus.INITIATED)
            .build();
        productOrder.addOrderLine(orderLine);
        orderLineRepository.save(orderLine);
        return OrderDetailResponseDto.builder()
            .productOrderId(productOrder.getId())
            .orderLines(productOrder.getOrderLines())
            .orderStatus(productOrder.getProductOrderStatus().name())
            .totalPrice(productOrder.getTotalPrice())
            .totalDiscount(productOrder.getTotalDiscount())
            .build();
    }

    @Override
    public void submitOrder(Long memberId, OrderDto command) throws Exception {
        Optional<ProductOrder> findProductOrder = productOrderRepository.findById(
            command.orderId());
        if (findProductOrder.isPresent()) {
            ProductOrder productOrder = findProductOrder.get();

            List<Long> productIds = new ArrayList<>();
            long finalTotalPrice = 0L;
            long totalDiscount = 0L;
            for (OrderLine orderLine : productOrder.getOrderLines()) {
                totalDiscount += orderLine.getDiscount();
                finalTotalPrice += orderLine.getPrice() * orderLine.getQuantity();
                productIds.add(orderLine.getProductId());

                OrderLineEvent orderLineEvent = OrderLineEvent.builder()
                    .orderLineId(orderLine.getId())
                    .productId(orderLine.getProductId())
                    .productName(orderLine.getProductName())
                    .price(orderLine.getPrice())
                    .discount(orderLine.getDiscount())
                    .quantity(orderLine.getQuantity())
                    .build();
                ProductOrderEvent productOrderEvent = ProductOrderEvent.builder()
                    .productOrderId(productOrder.getId())
                    .orderLine(orderLineEvent)
                    .memberId(memberId)
                    .paymentMethodId(command.paymentMethodId())
                    .deliveryAddressId(command.deliveryAddressId())
                    .build();
                orderKafkaProducer.occurPaymentEvent(productOrderEvent);
            }

            // 장바구니 비우기
            memberClient.clearCart(memberId, productIds);

            productOrder.finalizeOrder(ProdcutOrderStatus.COMPLETED, finalTotalPrice, totalDiscount);
            productOrderRepository.save(productOrder);
        }
    }

    @Override
    public OrderDetailResponseDto getOrder(Long memberId, Long orderId) {
        Optional<ProductOrder> findProductOrder = productOrderRepository.findByIdAndMemberId(
            orderId, memberId);
        if (findProductOrder.isPresent()) {
            ProductOrder productOrder = findProductOrder.get();
            return OrderDetailResponseDto.builder()
                .productOrderId(productOrder.getId())
                .orderLines(productOrder.getOrderLines())
                .orderStatus(productOrder.getProductOrderStatus().name())
                .totalPrice(productOrder.getTotalPrice())
                .totalDiscount(productOrder.getTotalDiscount())
                .build();
        }
        return null;
    }

    @Override
    public OrderListResponseDto getOrders(Long memberId) {
        List<ProductOrder> findProductOrder = productOrderRepository.findAllByMemberId(memberId);
        return new OrderListResponseDto(findProductOrder);
    }

    @Override
    public void cancelOrder(Long memberId, Long orderLineId) {
        orderLineRepository.findById(orderLineId).ifPresent(orderLine -> {
            Boolean check = deliveryClient.deliveryStatusCheck(orderLine.getDeliveryId());
            if (!check) {
                throw new GlobalException(ErrorCode.DELIVERY_STATUS_NOT_REQUESTED);
            }
            orderLine.cancelOrderLine();
            Boolean incrementStock = productClient.incrementStock(orderLine.getProductId(),
                orderLine.getQuantity());
            if(incrementStock == null) {
                throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            ProductOrder productOrder = orderLine.getProductOrder();
            Long price = orderLine.getPrice() * orderLine.getQuantity();
            productOrder.cancelOrder(price, orderLine.getDiscount());
            orderLineRepository.save(orderLine);
        });
    }
}
