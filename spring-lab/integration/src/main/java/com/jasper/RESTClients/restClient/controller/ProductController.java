package com.jasper.RESTClients.restClient.controller;

import com.jasper.pojo.dto.CreateProductRequest;
import com.jasper.pojo.dto.Product;
import com.jasper.result.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.rmi.RemoteException;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {
    private final RestClient restClient;

    @GetMapping("/{id}")
    public Product index(@PathVariable Long id) {
        return restClient
                .get()
                // URI 模板变量
                .uri("http://localhost:8081/products/{id}",id)
                .header("X-Request-Id", "123456")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        (request, response) -> {
                            throw new RemoteException();
                        }
                )
                // return object  default use jackson
                .body(Product.class)

                ;
    }

    @PostMapping
    public ApiResponse<Product> createProduct(@RequestBody CreateProductRequest request){
        return  restClient.post()
                .uri("http://localhost:8081/products")
                // 指定 HTTP 请求和响应的数据格式
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Product>>() {
                });
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Product> deleteProduct(@PathVariable Long id) {
        return  restClient
                .delete()
                .uri("http://localhost:8081/products/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Product>>() {
                });
    }
}
