package com.unir.products.model.pojo;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductDto {

	private String codigo;
	private String nombre;
	private String categoria;
	private String empresaAsociada;
	private String descripcioncorta;
	private String descripcionlarga;
	private String Producto;
	private Long precio;
	private Long cantidadDisponible;
	private Long puntuacion;
	private String imagen;

}
