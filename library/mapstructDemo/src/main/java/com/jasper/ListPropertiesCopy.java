package com.jasper;

import com.jasper.convert.OrderConvert;
import com.jasper.pojo.Customer;
import com.jasper.pojo.Order;
import com.jasper.pojo.OrderDTO;
import com.jasper.pojo.ProductDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ListPropertiesCopy {
    public static void main(String[] args) {
        final Customer customer = new Customer(1, "jasper");
        final ProductDTO product = new ProductDTO(1, "coat", "coat");
        final ProductDTO product1 = new ProductDTO(2, "shirt", "shirt");
        final List<ProductDTO> list = Arrays.asList(product, product1);
        final OrderDTO orderDTO = new OrderDTO(1,customer, list);
        final Order order = OrderConvert.INSTANCE.OrderDTOToOrder(orderDTO);
        log.info("orderDTOToOrder:{}", order);
    }
}
