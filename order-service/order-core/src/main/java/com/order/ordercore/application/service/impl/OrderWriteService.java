package com.order.ordercore.application.service.impl;

import com.ecommerce.common.cache.CachingCartListDto;
import com.order.ordercore.adapter.DeliveryClient;
import com.order.ordercore.adapter.MemberClient;
import com.order.ordercore.adapter.ProductClient;
import com.order.ordercore.application.service.dto.CartDto;
import com.order.orderapi.usecase.dto.OrderDtoFromCart;
import com.order.orderapi.usecase.dto.OrderDtoFromProduct;
import com.order.ordercore.application.service.dto.ProductDto;
import com.order.ordercore.application.utils.RedisUtils;
import com.order.orderapi.common.error.ErrorCode;
import com.order.orderapi.common.error.GlobalException;
import com.order.ordercore.infrastructure.entity.ProductOrder;
import com.order.ordercore.infrastructure.kafka.event.ProductInfoEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvent;
import com.order.ordercore.infrastructure.kafka.event.SubmitOrderEvents;
import com.order.ordercore.infrastructure.repository.OrderLineRepository;
import com.order.orderapi.usecase.OrderWriteUseCase;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderWriteService implements OrderWriteUseCase {

    private final OrderLineRepository orderLineRepository;
    private final MemberClient memberClient;
    private final ProductClient productClient;
    private final DeliveryClient deliveryClient;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final RedisUtils redisUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void submitOrderFromProduct(Long memberId, OrderDtoFromProduct command) {
        ProductDto product = productClient.getProduct(command.productId());
        if (product == null) {
            throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        Boolean result = redisUtils.decreaseStock(product.productId(), command.quantity());
        if (result == null || !result) {
            throw new GlobalException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
        }
        SubmitOrderEvent submitEvent = generateSubmitEvent(memberId, command, product);
        eventPublisher.publishEvent(submitEvent);
    }

    private SubmitOrderEvent generateSubmitEvent(Long memberId, OrderDtoFromProduct command,
        ProductDto product) {
        return SubmitOrderEvent.builder()
            .memberId(memberId)
            .paymentMethodId(command.paymentMethodId())
            .deliveryAddressId(command.deliveryAddressId())
            .quantity(command.quantity())
            .productId(product.productId())
            .productName(product.productName())
            .price(product.price())
            .thumbnailUrl(product.thumbnailUrl())
            .build();
    }

    @Override
    public void submitOrderFromCart(Long memberId, OrderDtoFromCart command) {
        List<CartDto> cartList = fetchCartDtosWithFallback(memberId, command.cartIds());
        List<ProductInfoEvent> productInfoEvents = new ArrayList<>();
        for (CartDto cart : cartList) {
            ProductInfoEvent productInfoEvent = generateSubmitEvent(memberId, command, cart);
            productInfoEvents.add(productInfoEvent);
        }
        SubmitOrderEvents submitOrderEvents = SubmitOrderEvents.builder()
            .memberId(memberId)
            .paymentMethodId(command.paymentMethodId())
            .deliveryAddressId(command.deliveryAddressId())
            .productInfo(productInfoEvents)
            .build();
        eventPublisher.publishEvent(submitOrderEvents);
        memberClient.clearCart(memberId, command.cartIds());
    }

    private List<CartDto> fetchCartDtosWithFallback(Long memberId, List<Long> cartIds) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("memberClient");
        return circuitBreaker.run(
            () -> memberClient.getCartList(memberId, cartIds),
            throwable -> retrieveCartListFromRedis(memberId, cartIds)
        );
    }

    private List<CartDto> retrieveCartListFromRedis(Long memberId, List<Long> cartIds) {
        List<CartDto> carts = new ArrayList<>();
        List<CachingCartListDto> cartList = redisUtils.getCartList(memberId);
        if (cartList != null) {
            for (CachingCartListDto cachingCartListDto : cartList) {
                if (cartIds.contains(cachingCartListDto.cartId())) {
                    CartDto cartDto = new CartDto(cachingCartListDto.productId(),
                        cachingCartListDto.productName(),
                        cachingCartListDto.price(), cachingCartListDto.thumbnailImageUrl(),
                        cachingCartListDto.quantity());
                    carts.add(cartDto);
                }
            }
            return carts;
        }
        return null;
    }

    private ProductInfoEvent generateSubmitEvent(Long memberId, OrderDtoFromCart command,
        CartDto cart) {
        return ProductInfoEvent.builder()
            .productId(cart.productId())
            .quantity(cart.quantity())
            .productName(cart.productName())
            .price(cart.price())
            .thumbnailUrl(cart.thumbnailUrl())
            .build();
    }

    @Override
    @Transactional
    public void cancelOrder(Long memberId, Long orderLineId) {
        orderLineRepository.findById(orderLineId).ifPresent(orderLine -> {
            Boolean check = deliveryClient.deliveryStatusCheck(orderLine.getDeliveryId());
            if (!check) {
                throw new GlobalException(ErrorCode.DELIVERY_STATUS_NOT_REQUESTED);
            }
            orderLine.cancel();
            Boolean incrementStock = productClient.incrementStock(orderLine.getProductId(),
                orderLine.getQuantity());
            if (incrementStock == null) {
                throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            ProductOrder productOrder = orderLine.getProductOrder();
            Long price = orderLine.getPrice() * orderLine.getQuantity();
            productOrder.cancelOrder(price, orderLine.getDiscount());
            orderLineRepository.save(orderLine);
        });
    }


}
