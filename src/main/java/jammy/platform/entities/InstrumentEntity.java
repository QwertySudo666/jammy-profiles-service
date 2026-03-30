package jammy.platform.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "instruments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentEntity {
  @Id private UUID id;

  @Column(unique = true, nullable = false)
  private String name;
}
