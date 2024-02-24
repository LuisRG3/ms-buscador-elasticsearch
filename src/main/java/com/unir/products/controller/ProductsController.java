package com.unir.products.controller;

import java.util.*;

import com.unir.products.model.pojo.ProductDto;
import com.unir.products.model.response.ProductsQueryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.unir.products.model.pojo.Product;
import com.unir.products.model.request.CreateProductRequest;
import com.unir.products.service.ProductsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Products Controller", description = "Microservicio encargado de exponer operaciones CRUD sobre productos alojados en una base de datos en memoria.")
public class ProductsController {

    private final ProductsService service;

    @GetMapping("/products")
    @Operation(
            operationId = "Obtener productos",
            description = "Operacion de lectura",
            summary = "Se devuelve una lista de todos los productos almacenados en la base de datos.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    public ResponseEntity<ProductsQueryResponse> getProducts(
            @RequestHeader Map<String, String> headers,
            @Parameter(name = "nombre", description = "Nombre del producto. No tiene por que ser exacto", example = "iPhone", required = false)
            @RequestParam(required = false) String nombre,
            @Parameter(name = "categoria", description = "País del producto. Debe ser exacto", example = "ES", required = false)
            @RequestParam(required = false) String categoria,
            @Parameter(name = "descripcionCorta", description = "Descripcion corta del producto. No tiene por que ser exacta", example = "Estupendo", required = false)
            @RequestParam(required = false) String descripcionCorta,
            @Parameter(name = "descripcionLarga", description = "Descripcion completa del producto. No tiene por que ser exacta", example = "Estupendo detallado", required = false)
            @RequestParam(required = false) String descripcionLarga,
            @Parameter(name = "valorunitario", description = "Precio de cada producto", example = "20", required = false)
            @RequestParam(required = false) Double valorunitario,
            @Parameter(name = "indValorUnitario", description = "Indicador de busqueda del Precio de producto si es 1 es menor igual al precio indicado si es 2 es mayor igual al precio indicado, si no se especifica lo tomara como igual al precio indicado", example = "1", required = false)
            @RequestParam(required = false) Integer indValorUnitario,
            @Parameter(name = "aggregate", description = "Indicar si quiere agregacion por categoria", example = "true", required = false)
            @RequestParam(required = false, defaultValue = "false") Boolean aggregate ) {

        log.info("headers: {}", headers);
        ProductsQueryResponse products = service.getProducts(nombre, categoria, descripcionCorta,descripcionLarga,valorunitario,indValorUnitario,aggregate);

        if (products != null) {
            return ResponseEntity.ok(products);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/products/{productId}")
    @Operation(
            operationId = "Obtener un producto",
            description = "Operacion de lectura",
            summary = "Se devuelve un producto a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    public ResponseEntity<Product> getProduct(@PathVariable String productId) {

        log.info("Request received for product {}", productId);
        Optional<Product> productOptional = service.getProduct(productId);
        Product product = productOptional.orElseThrow(() -> new NoSuchElementException("No se encontró el producto")); // Lanza NoSuchElementException si el Optional está vacío

        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @DeleteMapping("/products/{productId}")
    @Operation(
            operationId = "Eliminar un producto",
            description = "Operacion de escritura",
            summary = "Se elimina un producto a partir de su identificador.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {

        Boolean removed = service.removeProduct(productId);

        if (Boolean.TRUE.equals(removed)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/products")
    @Operation(
            operationId = "Insertar un producto",
            description = "Operacion de escritura",
            summary = "Se crea un producto a partir de sus datos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear.",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateProductRequest.class))))
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Datos incorrectos introducidos.")
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "No se ha encontrado el producto con el identificador indicado.")
    public ResponseEntity<Product> addProduct(@RequestBody CreateProductRequest request) {
        try {
            Product createdProduct = service.createProduct(request);

            if (createdProduct != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PatchMapping("/products/{productId}")
    @Operation(
            operationId = "Modificar parcialmente un producto",
            description = "Operacion de escritura",
            summary = "Se modifica parcialmente un producto.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = String.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "400",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Producto inválido o datos incorrectos introducidos.")
    public ResponseEntity<Product> patchProduct(@PathVariable String productId, @RequestBody String patchBody) {

        Product patched = service.updateProduct(productId, patchBody);
        if (patched != null) {
            return ResponseEntity.ok(patched);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/products/{productId}")
    @Operation(
            operationId = "Modificar totalmente un producto",
            description = "Operacion de escritura",
            summary = "Se modifica totalmente un producto.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a actualizar.",
                    required = true,
                    content = @Content(mediaType = "application/merge-patch+json", schema = @Schema(implementation = ProductDto.class))))
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class)))
    @ApiResponse(
            responseCode = "404",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class)),
            description = "Producto no encontrado.")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @RequestBody ProductDto body) {

        Product updated = service.updateProduct(productId, body);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
