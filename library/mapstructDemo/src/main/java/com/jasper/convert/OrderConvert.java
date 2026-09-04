package com.jasper.convert;

import com.jasper.pojo.Order;
import com.jasper.pojo.OrderDTO;
import com.jasper.pojo.Product;
import com.jasper.pojo.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderConvert {
    OrderConvert INSTANCE = Mappers.getMapper(OrderConvert.class);
    // orderDTO.product(productDTO)   ORDER.product(product)
    // 这里会自己调用下面的productDTOToProduct
    Order OrderDTOToOrder(OrderDTO orderDTO);

    @Mapping(target = "desc", source = "des")
    Product productDTOToProduct(ProductDTO productDTO);
}
