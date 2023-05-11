package productservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import productservice.entity.enums.ProductTypeCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_type")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductType {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name")
    @Enumerated(EnumType.STRING)
    private ProductTypeCode name;

    @Column(name = "description")
    private String description;

    @Column(name = "end_date")
    private LocalDateTime end_date;
}
