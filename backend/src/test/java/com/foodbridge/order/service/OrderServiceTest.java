package com.foodbridge.order.service;

import com.foodbridge.order.dto.OrderItemDto;
import com.foodbridge.order.dto.OrderRequest;
import com.foodbridge.order.dto.OrderResponse;
import com.foodbridge.order.model.Order;
import com.foodbridge.order.model.OrderStatus;
import com.foodbridge.order.repository.OrderRepository;
import com.foodbridge.user.model.User;
import com.foodbridge.user.repository.UserRepository;
import com.foodbridge.delivery.repository.DeliveryPartnerRepository;
import com.foodbridge.food.model.FoodItem;
import com.foodbridge.food.repository.FoodItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @InjectMocks
    private OrderService orderService;

    private User recipient;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        recipient = User.builder().id("user-1").email("recipient@test.com").build();

        testOrder = Order.builder()
                .id("order-1")
                .recipient(recipient)
                .status(OrderStatus.PENDING)
                .items(new ArrayList<>())
                .totalAmount(100.0)
                .build();
    }

    @Test
    void createOrder_Success() {
        OrderRequest request = new OrderRequest();
        request.setDonorId("donor-1");
        request.setTotalAmount(100.0);
        request.setDeliveryAddress("123 Main St");
        
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setFoodItemId("food-1");
        itemDto.setQuantity(2.0);
        itemDto.setUnitPrice(50.0);
        request.setItems(List.of(itemDto));

        FoodItem foodItem = FoodItem.builder().id("food-1").name("Burger").build();
        when(userRepository.findByEmail("recipient@test.com")).thenReturn(Optional.of(recipient));
        when(userRepository.findById("donor-1")).thenReturn(Optional.of(User.builder().id("donor-1").build()));
        when(foodItemRepository.findById("food-1")).thenReturn(Optional.of(foodItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId("order-generated-id");
            return saved;
        });

        OrderResponse response = orderService.createOrder(request, "recipient@test.com");

        assertNotNull(response);
        assertEquals("order-generated-id", response.getId());
        assertEquals("user-1", response.getRecipientId());
        assertEquals("donor-1", response.getDonorId());
        assertEquals(100.0, response.getTotalAmount());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertEquals("123 Main St", response.getDeliveryAddress());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void updateStatus_Success() {
        when(orderRepository.findById("order-1")).thenReturn(Optional.of(testOrder));
        when(deliveryPartnerRepository.findById("driver-1")).thenReturn(Optional.of(com.foodbridge.delivery.model.DeliveryPartner.builder().id("driver-1").build()));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateStatus("order-1", OrderStatus.CONFIRMED, "driver-1");

        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        assertEquals("driver-1", response.getDeliveryPartnerId());
    }

    @Test
    void getOrderById_ThrowsNotFound() {
        when(orderRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(com.foodbridge.common.exception.ResourceNotFoundException.class, () -> {
            orderService.getOrderById("invalid-id");
        });
    }
}
