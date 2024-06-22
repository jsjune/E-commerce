package com.ecommerce.order.usecase.impl;

import com.ecommerce.delivery.entity.Delivery;
import com.ecommerce.delivery.usecase.DeliveryUseCase;
import com.ecommerce.member.entity.Cart;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.usecase.AuthUseCase;
import com.ecommerce.member.usecase.CartUseCase;
import com.ecommerce.order.controller.req.OrderRequest;
import com.ecommerce.order.controller.req.ProductOrderRequestDto;
import com.ecommerce.order.controller.res.OrderDetailResponseDto;
import com.ecommerce.order.controller.res.OrderListResponseDto;
import com.ecommerce.order.entity.OrderLine;
import com.ecommerce.order.entity.OrderLineStatus;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.entity.ProductOrder;
import com.ecommerce.order.repository.OrderLineRepository;
import com.ecommerce.order.repository.ProductOrderRepository;
import com.ecommerce.order.usecase.OrderUseCase;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.usecase.PaymentUseCase;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.usecase.ProductReadUseCase;
import com.ecommerce.product.usecase.ProductWriteUseCase;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderUseCase {

    private final ProductOrderRepository productOrderRepository;
    private final OrderLineRepository orderLineRepository;
    private final AuthUseCase authUseCase;
    private final ProductReadUseCase productReadUseCase;
    private final PaymentUseCase paymentUseCase;
    private final DeliveryUseCase deliveryUseCase;
    private final ProductWriteUseCase productWriteUseCase;
    private final CartUseCase cartUseCase;

    @Override
    public void registerOrderOfCart(Long memberId, List<Long> cartIds) {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            List<OrderLine> orderLines = new ArrayList<>();
            int totalPrice = 0;
            for (Cart cart : member.getCarts()) {
                totalPrice += cart.getProduct().getPrice() * cart.getQuantity();
                OrderLine orderLine = OrderLine.builder()
                    .product(cart.getProduct())
                    .quantity(cart.getQuantity())
                    .status(OrderLineStatus.INITIATED)
                    .build();
                orderLines.add(orderLine);
            }
            ProductOrder productOrder = ProductOrder.builder()
                .member(member)
                .status(OrderStatus.INITIATED)
                .orderLines(orderLines)
                .totalPrice(totalPrice)
                .totalDiscount(0)
                .build();
            for (OrderLine orderLine : orderLines) {
                orderLine.assignToOrder(productOrder);
            }
            productOrderRepository.save(productOrder);
            orderLineRepository.saveAll(orderLines);
        }
    }

    @Override
    public void registerOrder(Long memberId, ProductOrderRequestDto request) {
        Optional<Member> findMember = authUseCase.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            Optional<Product> findProduct = productReadUseCase.findById(request.getProductId());
            if (findProduct.isPresent()) {
                Product product = findProduct.get();
                OrderLine orderLine = OrderLine.builder()
                    .product(product)
                    .quantity(request.getQuantity())
                    .status(OrderLineStatus.INITIATED)
                    .build();
                ProductOrder productOrder = ProductOrder.builder()
                    .member(member)
                    .status(OrderStatus.INITIATED)
                    .orderLines(List.of(orderLine))
                    .totalPrice(product.getPrice() * request.getQuantity())
                    .totalDiscount(0)
                    .build();
                productOrderRepository.save(productOrder);
                orderLineRepository.save(orderLine);
            }
        }
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
                int finalTotalDiscount = 0;
                for (OrderLine orderLine : productOrder.getOrderLines()) {
                    productIds.add(orderLine.getProduct().getId());

                    // 결제 요청
                    Payment payment = paymentUseCase.processPayment(member, orderLine,
                        request.getPaymentMethodId());

                    finalTotalPrice += payment.getTotalPrice();
                    finalTotalDiscount += payment.getDiscountPrice();
                    Delivery delivery = deliveryUseCase.processDelivery(orderLine,
                        request.getDeliveryAddressId());

                    orderLine.finalizeOrderLine(OrderLineStatus.PAYMENT_COMPLETED, payment.getId(),
                        delivery.getId());

                    orderLineRepository.save(orderLine);

                    // 재고 감소
                    productWriteUseCase.decreaseStock(orderLine.getProduct().getId(), orderLine.getQuantity());
                }

                // 장바구니 비우기
                cartUseCase.clearCart(memberId, productIds);

                productOrder.finalizeOrder(OrderStatus.PAYMENT_COMPLETED, finalTotalPrice, finalTotalDiscount);
                productOrderRepository.save(productOrder);
            }
        }
    }

    @Override
    public OrderDetailResponseDto getOrder(Long memberId, Long orderId) {
        Optional<ProductOrder> findProductOrder = productOrderRepository.findByIdAndMemberId(orderId, memberId);
        if(findProductOrder.isPresent()) {
            ProductOrder productOrder = findProductOrder.get();
            return OrderDetailResponseDto.builder()
                .orderLines(productOrder.getOrderLines())
                .orderStatus(productOrder.getStatus().name())
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
}
