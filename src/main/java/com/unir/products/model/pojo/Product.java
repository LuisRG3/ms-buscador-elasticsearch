package com.unir.products.model.pojo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.annotation.Id;

@Document(indexName = "products", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Product {

	@Id
	private String id;

	@Field(type = FieldType.Keyword, name = "codigocodigo")
	private String codigo;

	@Field(type = FieldType.Search_As_You_Type, name = "nombre")
	private String nombre;

	@Field(type = FieldType.Keyword, name = "categoria")
	private String categoria;

	@Field(type = FieldType.Text, name = "empresaAsociada")
	private String empresaAsociada;

	@Field(type = FieldType.Search_As_You_Type, name = "descripcioncorta")
	private String descripcioncorta;

	@Field(type = FieldType.Search_As_You_Type, name = "descripcionlarga")
	private String descripcionlarga;

	@Field(type = FieldType.Text, name = "Producto")
	private String Producto;

	@Field(type = FieldType.Long, name = "precio")
	private Long precio;

	@Field(type = FieldType.Long, name = "cantidadDisponible")
	private Long cantidadDisponible;

	@Field(type = FieldType.Long, name = "puntuacion")
	private Long puntuacion;

	@Field(type = FieldType.Text, name = "imagen")
	private String imagen;
	


	public void update(ProductDto productDto) {
		this.descripcioncorta = productDto.getDescripcioncorta();
		this.descripcionlarga = productDto.getDescripcionlarga();
		this.precio = productDto.getPrecio();
		this.cantidadDisponible = productDto.getCantidadDisponible();
		this.Producto = productDto.getProducto();
		this.nombre = productDto.getNombre();
		this.codigo = productDto.getCodigo();
		this.categoria = productDto.getCategoria();
		this.puntuacion=productDto.getPuntuacion();
		this.empresaAsociada=productDto.getEmpresaAsociada();
	}

}
