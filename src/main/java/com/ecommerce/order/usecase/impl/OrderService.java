package com.ecommerce.order.usecase.impl;

import com.ecommerce.delivery.entity.Delivery;
import com.ecommerce.delivery.usecase.DeliveryUseCase;
import com.ecommerce.common.adapter.ProductClient;
import com.ecommerce.member.entity.Cart;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.usecase.AuthUseCase;
import com.ecommerce.member.usecase.CartUseCase;
import com.ecommerce.common.adapter.dto.ProductDto;
import com.ecommerce.order.controller.req.OrderRequest;
import com.ecommerce.order.controller.req.ProductOrderRequestDto;
import com.ecommerce.order.controller.res.OrderDetailResponseDto;
import com.ecommerce.order.controller.res.OrderListResponseDto;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.OrderLineStatus;
import com.ecommerce.order.entity.ProdcutOrderStatus;
import com.ecommerce.order.entity.ProductOrder;
import com.ecommerce.order.repository.OrderLineRepository;
import com.ecommerce.order.repository.ProductOrderRepository;
import com.ecommerce.order.usecase.OrderUseCase;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.usecase.PaymentUseCase;
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
    private final AuthUseCase authUseCase;
    private final PaymentUseCase paymentUseCase;
    private final DeliveryUseCase deliveryUseCase;
    private final CartUseCase cartUseCase;
    private final ProductClient productClient;

    @Override
    public OrderDetailResponseDto registerOrderOfCart(Long memberId, List<Long> cartIds) {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            int totalPrice = 0;
            ProductOrder productOrder = ProductOrder.builder()
                .member(member)
                .productOrderStatus(ProdcutOrderStatus.INITIATED)
                .totalDiscount(0)
                .build();
            productOrderRepository.save(productOrder);
            List<OrderLine> orderLines = new ArrayList<>();
            for (Cart cart : member.getCarts()) {
                totalPrice += cart.getPrice() * cart.getQuantity();
                OrderLine orderLine = OrderLine.builder()
                    .productId(cart.getProductId())
                    .productName(cart.getProductName())
                    .price(cart.getPrice())
                    .quantity(cart.getQuantity())
                    .thumbnailUrl(cart.getThumbnailUrl())
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
        return null;
    }

    @Override
    public OrderDetailResponseDto registerOrder(Long memberId, ProductOrderRequestDto request) {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            ProductDto product = productClient.getProduct(request.getProductId());
            ProductOrder productOrder = ProductOrder.builder()
                .member(member)
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
        return null;
    }

    @Override
    public void submitOrder(Long memberId, OrderRequest request) throws Exception {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
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
                    Payment payment = paymentUseCase.processPayment(member, orderLine,
                        request.getPaymentMethodId());

                    finalTotalPrice += payment.getTotalPrice();
                    Delivery delivery = deliveryUseCase.processDelivery(orderLine,
                        request.getDeliveryAddressId());

                    orderLine.finalizeOrderLine(OrderLineStatus.PAYMENT_COMPLETED, payment.getId(),
                        delivery.getId());

                    orderLineRepository.save(orderLine);

                    // 재고 감소
                    productClient.decreaseStock(orderLine.getProductId(), orderLine.getQuantity());
                }

                // 장바구니 비우기
                cartUseCase.clearCart(memberId, productIds);

                productOrder.finalizeOrder(ProdcutOrderStatus.COMPLETED, finalTotalPrice,
                    totalDiscount);
                productOrderRepository.save(productOrder);
            }
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
            productClient.incrementStock(orderLine.getProductId(), orderLine.getQuantity());
            ProductOrder productOrder = orderLine.getProductOrder();
            int price = orderLine.getPrice() * orderLine.getQuantity();
            productOrder.cancelOrder(price, orderLine.getDiscount());
            orderLineRepository.save(orderLine);
        });
    }
}
