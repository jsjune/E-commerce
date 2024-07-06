package com.orderservice.usecase.impl;

import com.ecommerce.common.cache.CartListDto;
import com.orderservice.adapter.DeliveryClient;
import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.adapter.res.CartDto;
import com.orderservice.adapter.res.ProductDto;
import com.orderservice.controller.res.OrderDetailResponseDto;
import com.orderservice.entity.OrderLine;
import com.orderservice.entity.OrderLineStatus;
import com.orderservice.entity.ProductOrder;
import com.orderservice.entity.ProductOrderStatus;
import com.orderservice.repository.OrderLineRepository;
import com.orderservice.usecase.OrderWriteUseCase;
import com.orderservice.usecase.dto.OrderDtoFromCart;
import com.orderservice.usecase.dto.OrderDtoFromProduct;
import com.orderservice.usecase.dto.RegisterOrderFromCartDto;
import com.orderservice.usecase.dto.RegisterOrderFromProductDto;
import com.orderservice.usecase.kafka.KafkaHealthIndicator;
import com.orderservice.usecase.kafka.OrderKafkaProducer;
import com.orderservice.usecase.kafka.event.ProductInfoEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvent;
import com.orderservice.usecase.kafka.event.SubmitOrderEvents;
import com.orderservice.utils.RedisUtils;
import com.orderservice.utils.error.ErrorCode;
import com.orderservice.utils.error.GlobalException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
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
    private final OrderKafkaProducer orderKafkaProducer;
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final RedisUtils redisUtils;
    private final KafkaHealthIndicator kafkaHealthIndicator;

    @Override
    public void submitOrderFromProduct(Long memberId, OrderDtoFromProduct command) {
        try {
            ProductDto product = productClient.getProduct(command.productId());
            if (product == null) {
                throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            SubmitOrderEvent submitEvent = generateSubmitEvent(memberId, command, product);
            if (kafkaHealthIndicator.isKafkaUp()) {
                orderKafkaProducer.occurSubmitOrderFromProductEvent(submitEvent);
            } else {
                orderKafkaProducer.occurSubmitOrderFromProductEventFailure(submitEvent);
            }
        } catch (Exception e) {
            log.error("Failed to send payment event", e);
        }
    }

    private static SubmitOrderEvent generateSubmitEvent(Long memberId, OrderDtoFromProduct command,
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
        try {
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
            if (kafkaHealthIndicator.isKafkaUp()) {
                orderKafkaProducer.occurSubmitOrderFromCartEvent(submitOrderEvents);
            } else {
                orderKafkaProducer.occurSubmitOrderFromCartEventFailure(submitOrderEvents);
            }
            memberClient.clearCart(memberId, command.cartIds());
        } catch (Exception e) {
            log.error("Failed to send payment event", e);
        }
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
        List<CartListDto> cartList = redisUtils.getCartList(memberId);
        if (cartList != null) {
            for (CartListDto cartListDto : cartList) {
                if (cartIds.contains(cartListDto.cartId())) {
                    CartDto cartDto = new CartDto(cartListDto.productId(),
                        cartListDto.productName(),
                        cartListDto.price(), cartListDto.thumbnailImageUrl(),
                        cartListDto.quantity());
                    carts.add(cartDto);
                }
            }
            return carts;
        }
        return null;
    }

    private static ProductInfoEvent generateSubmitEvent(Long memberId, OrderDtoFromCart command,
        CartDto cart) {
        return ProductInfoEvent.builder()
            .productId(cart.productId())
            .quantity(cart.quantity())
            .productName(cart.productName())
            .price(cart.price())
            .thumbnailUrl(cart.thumbnailUrl())
            .build();
    }

    @Transactional
    @Override
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
