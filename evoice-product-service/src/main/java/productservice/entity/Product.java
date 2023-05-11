package productservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Audited
public class Product {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @EqualsAndHashCode.Include
    private UUID id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private ProductType type;

    @Column(name = "end_date")
    private LocalDateTime end_date;

    @NotAudited
    @ToString.Exclude
    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ProductName> names;

    @NotAudited
    @ToString.Exclude
    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ProductPrice> prices;

    @NotAudited
    @ToString.Exclude
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_user", joinColumns = {@JoinColumn(name = "product_id")})
    @Column(name = "user_id")
    private List<UUID> users;

    public ProductName getActualName() {
        if (CollectionUtils.isEmpty(names)) {
            throw new NullPointerException("Names collection is empty");
        }

        return names.parallelStream().filter(name -> name.getEnd_date() == null).findFirst().orElseThrow(
                () -> new NullPointerException("Names collection doesnt contains any actual name")
        );
    }

    public ProductPrice getActualPrice() {
        if (CollectionUtils.isEmpty(prices)) {
            throw new NullPointerException("Prices collection is empty");
        }

        return prices.parallelStream().filter(price -> price.getEnd_date() == null).findFirst().orElseThrow(
                () -> new NullPointerException("Names collection doesnt contains any actual name")
        );
    }
}
