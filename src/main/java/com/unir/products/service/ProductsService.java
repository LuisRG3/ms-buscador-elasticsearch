package com.unir.products.service;

import com.unir.products.model.pojo.Product;
import com.unir.products.model.pojo.ProductDto;
import com.unir.products.model.request.CreateProductRequest;
import com.unir.products.model.response.ProductsQueryResponse;

import java.util.Optional;

public interface ProductsService {

	ProductsQueryResponse getProducts(String nombre, String categoria, String descripcioncorta, String descripcionlarga, Double valorunitario, Integer indValorUnitario, Boolean aggregate);
	
	Optional<Product> getProduct(String productId);
	
	Boolean removeProduct(String productId);
	
	Product createProduct(CreateProductRequest request);

	Product updateProduct(String productId, String updateRequest);

	Product updateProduct(String productId, ProductDto updateRequest);

}
