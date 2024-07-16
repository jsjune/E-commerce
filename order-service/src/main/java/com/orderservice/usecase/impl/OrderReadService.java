package com.orderservice.usecase.impl;

import com.ecommerce.common.cache.CachingCartListDto;
import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.usecase.dto.CartDto;
import com.orderservice.usecase.dto.ProductDto;
import com.orderservice.usecase.dto.OrderDetailResponseDto;
import com.orderservice.usecase.dto.OrderListResponseDto;
import com.orderservice.infrastructure.entity.OrderLine;
import com.orderservice.infrastructure.entity.OrderLineStatus;
import com.orderservice.infrastructure.entity.ProductOrder;
import com.orderservice.infrastructure.entity.ProductOrderStatus;
import com.orderservice.infrastructure.repository.ProductOrderRepository;
import com.orderservice.usecase.OrderReadUseCase;
import com.orderservice.usecase.dto.RegisterOrderFromCartDto;
import com.orderservice.usecase.dto.RegisterOrderFromProductDto;
import com.orderservice.utils.RedisUtils;
import com.orderservice.utils.error.ErrorCode;
import com.orderservice.utils.error.GlobalException;
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
        return new OrderDetailResponseDto(productOrder);
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
        return new OrderDetailResponseDto(productOrder);
    }

    @Override
    public OrderDetailResponseDto getOrder(Long memberId, Long orderId) {
        Optional<ProductOrder> findProductOrder = productOrderRepository.findByIdAndMemberId(
            orderId, memberId);
        if (findProductOrder.isPresent()) {
            ProductOrder productOrder = findProductOrder.get();
            return new OrderDetailResponseDto(productOrder);
        }
        return null;
    }

    @Override
    public OrderListResponseDto getOrders(Long memberId) {
        List<ProductOrder> findProductOrder = productOrderRepository.findAllByMemberId(memberId);
        return new OrderListResponseDto(
            findProductOrder.stream()
                .map(OrderDetailResponseDto::new).toList()
        );
    }
}
