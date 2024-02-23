package com.unir.products.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {

	@NotNull(message = "`codigo` cannot be null")
	@NotEmpty(message = "`codigo` cannot be empty")
	private String codigo;
	@NotNull(message = "`nombre` cannot be null")
	@NotEmpty(message = "`nombre` cannot be empty")
	private String nombre;
	@NotNull(message = "`categoria` cannot be null")
	@NotEmpty(message = "`categoria` cannot be empty")
	private String categoria;
	@NotNull(message = "`empresaAsociada` cannot be null")
	@NotEmpty(message = "`empresaAsociada` cannot be empty")
	private String empresaAsociada;
	@NotNull(message = "`descripcionCorta` cannot be null")
	@NotEmpty(message = "`descripcionCorta` cannot be empty")
	private String descripcionCorta;
	@NotNull(message = "`descripcionLarga` cannot be null")
	@NotEmpty(message = "`descripcionLarga` cannot be empty")
	private String descripcionLarga;
	@NotNull(message = "`Producto` cannot be null")
	@NotEmpty(message = "`Producto` cannot be empty")
	private String Producto;
	@NotNull(message = "`precio` cannot be null")
	@NotEmpty(message = "`precio` cannot be empty")
	private Long precio;
	@NotNull(message = "`cantidadDisponible` cannot be null")
	@NotEmpty(message = "`cantidadDisponible` cannot be empty")
	private Long cantidadDisponible;
	@NotNull(message = "`puntuacion` cannot be null")
	@NotEmpty(message = "`puntuacion` cannot be empty")
	private Long puntuacion;
	@NotNull(message = "`imagen` cannot be null")
	@NotEmpty(message = "`imagen` cannot be empty")
	private String imagen;
}
