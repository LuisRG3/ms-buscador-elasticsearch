package com.unir.products.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.unir.products.data.DataAccessRepository;
import com.unir.products.data.ProductRepository;
import com.unir.products.model.pojo.ProductDto;
import com.unir.products.model.response.ProductsQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.unir.products.model.pojo.Product;
import com.unir.products.model.request.CreateProductRequest;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {


	private final DataAccessRepository repositoryEslastick;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public ProductsQueryResponse getProducts(String nombre, String categoria, String descripcioncorta, String descripcionlarga, Double valorunitario, Integer indValorUnitario, Boolean aggregate){

			return repositoryEslastick.findProducts(nombre, categoria, descripcioncorta, descripcionlarga,valorunitario, indValorUnitario,aggregate );
	}

	@Override
	public Optional<Product> getProduct(String productId) {
		return repositoryEslastick.findById(productId);
	}

	@Override
	public Boolean removeProduct(String productId) {

		try {
			Optional<Product> dto=repositoryEslastick.findById(productId);
			Product product = dto.orElse(new Product());
			return repositoryEslastick.delete(product);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Product createProduct(CreateProductRequest request) {

		if (request != null && StringUtils.hasLength(request.getCodigo().trim())
				&& StringUtils.hasLength(request.getNombre().trim())
				&& StringUtils.hasLength(request.getCategoria().trim())
				&& StringUtils.hasLength(request.getEmpresaAsociada().trim())
				&& StringUtils.hasLength(request.getDescripcionCorta().trim())
				&& StringUtils.hasLength(request.getDescripcionLarga().trim())
				&& StringUtils.hasLength(request.getProducto().trim())
				&& StringUtils.hasLength(request.getImagen().trim())
				&& request.getPrecio()!=null  && request.getCantidadDisponible()!=null
				&& request.getPuntuacion()!=null
				 ) {

			Product product = Product.builder().codigo(request.getCodigo()).nombre(request.getNombre()).categoria(request.getCategoria())
					.empresaAsociada(request.getEmpresaAsociada())
					.descripcionCorta(request.getDescripcionCorta()).descripcionLarga(request.getDescripcionLarga())
					.Producto(request.getProducto())
					.imagen(request.getImagen()).precio(request.getPrecio()).cantidadDisponible(request.getCantidadDisponible())
					.puntuacion(request.getPuntuacion()).build();

			log.info("Producto nuevo: {}", product );

			return repositoryEslastick.save(product);
		} else {
			return null;
		}
	}

	@Override
	public Product updateProduct(String productId, String updateRequest) {
		//PATCH se implementa en este caso mediante Merge Patch: https://datatracker.ietf.org/doc/html/rfc7386
		Optional<Product> dto=repositoryEslastick.findById(productId);
		Product product = dto.orElse(null);
		if (product != null) {
			try {
				JsonMergePatch jsonMergePatch = JsonMergePatch.fromJson(objectMapper.readTree(updateRequest));
				JsonNode target = jsonMergePatch.apply(objectMapper.readTree(objectMapper.writeValueAsString(product)));
				Product patched = objectMapper.treeToValue(target, Product.class);
				repositoryEslastick.save(patched);
				return patched;
			} catch (JsonProcessingException | JsonPatchException e) {
				log.error("Error updating product {}", productId, e);
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public Product updateProduct(String productId, ProductDto updateRequest) {
		Optional<Product> dto=repositoryEslastick.findById(productId);
		Product product = dto.orElse(null);
		if (product != null) {
			product.update(updateRequest);
			repositoryEslastick.save(product);
			return product;
		} else {
			return null;
		}
	}


}
