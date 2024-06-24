package com.orderservice.order.usecase.impl;


import com.orderservice.adapter.MemberClient;
import com.orderservice.adapter.ProductClient;
import com.orderservice.adapter.dto.CartDto;
import com.orderservice.adapter.dto.ProductDto;
import com.orderservice.delivery.entity.Delivery;
import com.orderservice.delivery.usecase.DeliveryUseCase;
import com.orderservice.order.controller.req.OrderRequest;
import com.orderservice.order.controller.req.ProductOrderRequestDto;
import com.orderservice.order.controller.res.OrderDetailResponseDto;
import com.orderservice.order.controller.res.OrderListResponseDto;
import com.orderservice.order.entity.OrderLine;
import com.orderservice.order.entity.OrderLineStatus;
import com.orderservice.order.entity.ProdcutOrderStatus;
import com.orderservice.order.entity.ProductOrder;
import com.orderservice.order.repository.OrderLineRepository;
import com.orderservice.order.repository.ProductOrderRepository;
import com.orderservice.order.usecase.OrderUseCase;
import com.orderservice.payment.entity.Payment;
import com.orderservice.payment.usecase.PaymentUseCase;
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
    private final PaymentUseCase paymentUseCase;
    private final DeliveryUseCase deliveryUseCase;
    private final MemberClient memberClient;
    private final ProductClient productClient;

    @Override
    public OrderDetailResponseDto registerOrderOfCart(Long memberId, List<Long> cartIds) {
        int totalPrice = 0;
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProdcutOrderStatus.INITIATED)
            .totalDiscount(0)
            .build();
        productOrderRepository.save(productOrder);
        List<OrderLine> orderLines = new ArrayList<>();
        List<CartDto> cartList = memberClient.getCartList(memberId, cartIds);
        for (CartDto cart : cartList) {
            totalPrice += cart.price() * cart.quantity();
            OrderLine orderLine = OrderLine.builder()
                .productId(cart.productId())
                .productName(cart.productName())
                .price(cart.price())
                .quantity(cart.quantity())
                .thumbnailUrl(cart.thumbnailUrl())
                .discount(0)
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
    public OrderDetailResponseDto registerOrder(Long memberId, ProductOrderRequestDto request) {
        ProductDto product = productClient.getProduct(request.getProductId());
        if (product == null) {
            throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        ProductOrder productOrder = ProductOrder.builder()
            .memberId(memberId)
            .productOrderStatus(ProdcutOrderStatus.INITIATED)
            .totalPrice(product.price() * request.getQuantity())
            .totalDiscount(0)
            .build();
        productOrderRepository.save(productOrder);
        OrderLine orderLine = OrderLine.builder()
            .productId(product.productId())
            .productName(product.productName())
            .price(product.price())
            .thumbnailUrl(product.thumbnailUrl())
            .quantity(request.getQuantity())
            .discount(0)
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
    public void submitOrder(Long memberId, OrderRequest request) throws Exception {
        Optional<ProductOrder> findProductOrder = productOrderRepository.findById(
            request.getOrderId());
        if (findProductOrder.isPresent()) {
            ProductOrder productOrder = findProductOrder.get();

            List<Long> productIds = new ArrayList<>();
            int finalTotalPrice = 0;
            int totalDiscount = 0;
            for (OrderLine orderLine : productOrder.getOrderLines()) {
                totalDiscount += orderLine.getDiscount();
                productIds.add(orderLine.getProductId());

                // 결제 요청
                Payment payment = paymentUseCase.processPayment(memberId, orderLine,
                    request.getPaymentMethodId());

                finalTotalPrice += payment.getTotalPrice();
                Delivery delivery = deliveryUseCase.processDelivery(orderLine,
                    request.getDeliveryAddressId());

                orderLine.finalizeOrderLine(OrderLineStatus.PAYMENT_COMPLETED, payment.getId(),
                    delivery.getId());

                orderLineRepository.save(orderLine);

                // 재고 감소
                Boolean checkStock = productClient.decreaseStock(orderLine.getProductId(),
                    orderLine.getQuantity());
                if (checkStock == null) {
                    throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
                }else if(!checkStock) {
                    throw new GlobalException(ErrorCode.PRODUCT_STOCK_NOT_ENOUGH);
                }
            }

            // 장바구니 비우기
            memberClient.clearCart(memberId, productIds);

            productOrder.finalizeOrder(ProdcutOrderStatus.COMPLETED, finalTotalPrice,
                totalDiscount);
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
            deliveryUseCase.deliveryStatusCheck(orderLine.getDeliveryId());
            orderLine.cancelOrderLine();
            Boolean incrementStock = productClient.incrementStock(orderLine.getProductId(),
                orderLine.getQuantity());
            if(incrementStock == null) {
                throw new GlobalException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            ProductOrder productOrder = orderLine.getProductOrder();
            int price = orderLine.getPrice() * orderLine.getQuantity();
            productOrder.cancelOrder(price, orderLine.getDiscount());
            orderLineRepository.save(orderLine);
        });
    }
}