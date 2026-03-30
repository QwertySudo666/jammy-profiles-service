package jammy.platform.entities;

import jakarta.persistence.*;
import jammy.platform.enums.SkillLevel;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "search_filters")
@Getter
@Setter
public class SearchFilterEntity {
  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id")
  private ProfileEntity profile;

  private String name;

  @Column(name = "target_location")
  private String targetLocation;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_skill")
  private SkillLevel targetSkill;

  @Column(name = "min_experience")
  private Integer minExperience;

  @Column(name = "is_active")
  private Boolean isActive;

  @ManyToMany
  @JoinTable(
      name = "filter_instruments",
      joinColumns = @JoinColumn(name = "filter_id"),
      inverseJoinColumns = @JoinColumn(name = "instrument_id"))
  private Set<InstrumentEntity> targetInstrumentEntities;
}
