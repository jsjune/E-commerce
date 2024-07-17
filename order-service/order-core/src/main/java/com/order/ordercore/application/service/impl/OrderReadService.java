package com.order.ordercore.application.service.impl;

import com.ecommerce.common.cache.CachingCartListDto;
import com.order.orderapi.usecase.dto.OrderLineDto;
import com.order.ordercore.adapter.MemberClient;
import com.order.ordercore.adapter.ProductClient;
import com.order.ordercore.application.service.dto.CartDto;
import com.order.orderapi.usecase.dto.OrderDetailResponseDto;
import com.order.orderapi.usecase.dto.OrderListResponseDto;
import com.order.ordercore.application.service.dto.ProductDto;
import com.order.orderapi.usecase.dto.RegisterOrderFromCartDto;
import com.order.orderapi.usecase.dto.RegisterOrderFromProductDto;
import com.order.ordercore.application.utils.RedisUtils;
import com.order.orderapi.common.error.ErrorCode;
import com.order.orderapi.common.error.GlobalException;
import com.order.ordercore.infrastructure.entity.OrderLine;
import com.order.ordercore.infrastructure.entity.OrderLineStatus;
import com.order.ordercore.infrastructure.entity.ProductOrder;
import com.order.ordercore.infrastructure.entity.ProductOrderStatus;
import com.order.ordercore.infrastructure.repository.ProductOrderRepository;
import com.order.orderapi.usecase.OrderReadUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderReadService implements OrderReadUseCase {

    private final ProductOrderRepository productOrderRepository;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final RedisUtils redisUtils;
    private final MemberClient memberClient;
    private final ProductClient productClient;

    @Override
    public OrderDetailResponseDto getOrderFromCart(Long memberId,
        RegisterOrderFromCartDto command) {
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProductOrderStatus.INITIATED)
            .totalDiscount(0L)
            .build();
        List<OrderLine> orderLines = new ArrayList<>();

        List<CartDto> cartList = fetchCartDtosWithFallback(memberId, command.cartIds());
        long totalPrice = 0L;
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
        List<OrderLineDto> orderLineList = getOrderLineDtoList(
            productOrder);
        return OrderDetailResponseDto.builder()
            .orderLines(orderLineList)
            .orderStatus(productOrder.getProductOrderStatus().name())
            .totalPrice(productOrder.getTotalPrice())
            .totalDiscount(productOrder.getTotalDiscount())
            .build();
    }

    private List<CartDto> fetchCartDtosWithFallback(Long memberId, List<Long> cartIds) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("memberClient");
        return circuitBreaker.run(
            () -> memberClient.getCartList(memberId, cartIds),
            throwable -> retrieveCartListFromRedis(memberId, cartIds)
        );
    }

    private List<CartDto> retrieveCartListFromRedis(Long memberId, List<Long> cartIds) {
        List<CartDto> cartDtos = new ArrayList<>();
        List<CachingCartListDto> cachingCartListDtos = redisUtils.getCartList(memberId);
        if (cachingCartListDtos != null) {
            for (CachingCartListDto cachingCartListDto : cachingCartListDtos) {
                if (cartIds.contains(cachingCartListDto.cartId())) {
                    CartDto cartDto = new CartDto(cachingCartListDto.productId(),
                        cachingCartListDto.productName(),
                        cachingCartListDto.price(), cachingCartListDto.thumbnailImageUrl(),
                        cachingCartListDto.quantity());
                    cartDtos.add(cartDto);
                }
            }
            return cartDtos;
        }
        return cartDtos;
    }

    @Override
    public OrderDetailResponseDto getOrderFromProduct(Long memberId,
        RegisterOrderFromProductDto command) {
        ProductDto product = productClient.getProduct(command.productId());
        if (product == null) {
            throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProductOrderStatus.INITIATED)
            .totalPrice(product.price() * command.quantity())
            .totalDiscount(0L)
            .build();
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
        List<OrderLineDto> orderLineList = getOrderLineDtoList(
            productOrder);
        return OrderDetailResponseDto.builder()
            .orderLines(orderLineList)
            .orderStatus(productOrder.getProductOrderStatus().name())
            .totalPrice(productOrder.getTotalPrice())
            .totalDiscount(productOrder.getTotalDiscount())
            .build();
    }

    private static List<OrderLineDto> getOrderLineDtoList(ProductOrder productOrder) {
        List<OrderLineDto> orderLineList = new ArrayList<>();
        for (OrderLine order : productOrder.getOrderLines()) {
            OrderLineDto orderLineDto = OrderLineDto.builder()
                .productId(order.getProductId())
                .productName(order.getProductName())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .thumbnailUrl(order.getThumbnailUrl())
                .status(order.getOrderLineStatus().name())
                .paymentId(order.getPaymentId())
                .deliveryId(order.getDeliveryId())
                .build();
            orderLineList.add(orderLineDto);
        }
        return orderLineList;
    }

    @Override
    public OrderDetailResponseDto getOrder(Long memberId, Long orderId) {
        Optional<ProductOrder> findProductOrder = productOrderRepository.findByIdAndMemberId(
            orderId, memberId);
        if (findProductOrder.isPresent()) {
            ProductOrder productOrder = findProductOrder.get();
            List<OrderLineDto> orderLineList = getOrderLineDtoList(
                productOrder);
            return OrderDetailResponseDto.builder()
                .orderLines(orderLineList)
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
        List<OrderDetailResponseDto> orders = new ArrayList<>();
        for (ProductOrder productOrder : findProductOrder) {
            List<OrderLineDto> orderLineList = getOrderLineDtoList(
                productOrder);
            OrderDetailResponseDto orderDetailResponseDto = OrderDetailResponseDto.builder()
                .orderLines(orderLineList)
                .orderStatus(productOrder.getProductOrderStatus().name())
                .totalPrice(productOrder.getTotalPrice())
                .totalDiscount(productOrder.getTotalDiscount())
                .build();
            orders.add(orderDetailResponseDto);
        }
        return new OrderListResponseDto(orders);
    }
}
