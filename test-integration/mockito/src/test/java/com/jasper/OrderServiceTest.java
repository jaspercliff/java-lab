package com.jasper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_success() {
        // Arrange：模拟库存服务返回 100
        when(storageService.getStock("ITEM_001"))
                .thenReturn(100);

        // Act：执行真正的业务代码
        String result = orderService.createOrder("ITEM_001", 2);

        // Assert：验证业务结果
        assertEquals("订单创建成功", result);
    }

    @Test
    void createOrder_storageFailure() {
        when(storageService.getStock("ITEM_001"))
                .thenThrow(new RuntimeException("库存服务不可用"));

        RuntimeException exception = Assertions
                .assertThrows(
                        RuntimeException.class,
                        () -> orderService.createOrder("ITEM_001", 2)
                );

        assertEquals("库存服务不可用", exception.getMessage());
    }


    @Test
    void createOrder_verifyInvocation() {
        when(storageService.getStock("ITEM_001"))
                .thenReturn(100);

        orderService.createOrder("ITEM_001", 2);

        // 验证指定方法被调用了 1 次
        verify(storageService).getStock("ITEM_001");
//       验证指定方法被调用了至少 1 次
        verify(storageService, Mockito.atLeastOnce()).getStock("ITEM_001");
    }
}