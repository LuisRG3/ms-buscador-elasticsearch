package com.unir.products.data;

import java.net.InetAddress;
import java.util.*;

import com.unir.products.model.pojo.Product;
import com.unir.products.model.response.AggregationDetails;
import com.unir.products.model.response.ProductsQueryResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {

    @Value("${server.fullAddress}")
    private String serverFullAddress;

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] nombreSearchFields = {"nombre", "nombre._2gram", "nombre._3gram"};
    private final String[] descripcioncortaSearchFields = {"descripcioncorta", "descripcioncorta._2gram", "descripcioncorta._3gram"};
    private final String[] descripcionlargaSearchFields = {"descripcionlarga", "descripcionlarga._2gram", "descripcionlarga._3gram"};

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Boolean delete(Product product) {
        productRepository.delete(product);
        return Boolean.TRUE;
    }

    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @SneakyThrows
    public ProductsQueryResponse findProducts(String nombre, String categoria, String descripcioncorta, String descripcionlarga, Double valorunitario, Integer indValorUnitario, Boolean aggregate) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(nombre)) {
            querySpec.must(QueryBuilders.multiMatchQuery(nombre, nombreSearchFields).type(Type.BOOL_PREFIX));
        }

        if (!StringUtils.isEmpty(categoria)) {
            querySpec.must(QueryBuilders.matchQuery("categoria", categoria));
        }

        if (!StringUtils.isEmpty(descripcioncorta)) {
            querySpec.must(QueryBuilders.multiMatchQuery(descripcioncorta, descripcioncortaSearchFields).type(Type.BOOL_PREFIX));
        }

        if (!StringUtils.isEmpty(descripcionlarga)) {
            querySpec.must(QueryBuilders.multiMatchQuery(descripcionlarga, descripcionlargaSearchFields).type(Type.BOOL_PREFIX));
        }

        //Si no he recibido ningun parametro, busco todos los elementos.
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        //Filtro implicito
        //No le pido al usuario que lo introduzca pero lo aplicamos proactivamente en todas las peticiones
        //En este caso, que los productos sean visibles (estado correcto de la entidad)
        //querySpec.must(QueryBuilders.termQuery("visible", true));

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Agrupacion por Categoria").field("categoria").size(1000));
            nativeSearchQueryBuilder.withMaxResults(0);
        }

        //Opcionalmente, podemos paginar los resultados
        //nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 10));

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Product> result = elasticClient.search(query, Product.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms countryAgg = (ParsedStringTerms) aggs.get("Agrupacion por Categoria");

            //Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
            String queryParams = getQueryParams(nombre, categoria, descripcioncorta, descripcionlarga);
            countryAgg.getBuckets()
                    .forEach(
                            bucket -> responseAggs.add(
                                    new AggregationDetails(
                                            bucket.getKey().toString(),
                                            (int) bucket.getDocCount(),
                                            serverFullAddress + "/products?country=" + bucket.getKey() + queryParams)));
        }
        return new ProductsQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }

    /**
     * Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
     *
     * @param nombre        - nombre del producto
     * @param categoria - categoria del producto
     * @param descripcioncorta     - descripcion corta del producto
     * @param descripcionlarga     - descripcion larga del producto
     * @return
     */
    private String getQueryParams(String nombre, String categoria, String descripcioncorta, String descripcionlarga) {
        String queryParams = (StringUtils.isEmpty(nombre) ? "" : "&nombre=" + nombre)
                + (StringUtils.isEmpty(categoria) ? "" : "&categoria=" + categoria)
                + (StringUtils.isEmpty(descripcioncorta) ? "" : "&descripcioncorta=" + descripcioncorta)
                + (StringUtils.isEmpty(descripcionlarga) ? "" : "&descripcionlarga=" + descripcionlarga);
        // Eliminamos el ultimo & si existe
        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
    }
}
