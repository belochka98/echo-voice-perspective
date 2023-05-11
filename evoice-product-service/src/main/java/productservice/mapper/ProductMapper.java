package productservice.mapper;

import apicore.amqp.product.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import productservice.entity.Product;

import java.util.List;
import java.util.function.Function;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ProductMapper extends Function<Product, ProductDto> {
    @Override
    @Mapping(target = "productId", source = "id")
    @Mapping(target = "productType", expression = "java(source.getType().getName().name())")
    @Mapping(target = "name", expression = "java(source.getActualName().getName())")
    @Mapping(target = "description", expression = "java(source.getActualName().getDescription())")
    @Mapping(target = "price", expression = "java(source.getActualPrice().getPrice())")
    ProductDto apply(Product source);

    List<ProductDto> applyAsListDto(List<Product> source);
}
