package jammy.platform.entities;

import jakarta.persistence.*;
import jammy.platform.enums.SkillLevel;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity {
  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private String name;

  private String location;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "skill_level")
  private SkillLevel skill;

  @Column(name = "years_of_experience")
  private Integer yearsOfExperience;

  private String description;

  @Column(name = "date_of_birth")
  private Instant dateOfBirth;

  @ManyToMany
  @JoinTable(
      name = "profile_instruments",
      joinColumns = @JoinColumn(name = "profile_id"),
      inverseJoinColumns = @JoinColumn(name = "instrument_id"))
  private Set<InstrumentEntity> instruments;

  @ManyToMany
  @JoinTable(
      name = "profile_genres",
      joinColumns = @JoinColumn(name = "profile_id"),
      inverseJoinColumns = @JoinColumn(name = "genre_id"))
  private Set<GenreEntity> genres;

  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MediaMetadataEntity> media;

  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProfileLinkEntity> links;

  public void setAvatar(String url) {
    MediaMetadataEntity avatar =
        MediaMetadataEntity.builder()
            .id(UUID.randomUUID())
            .url(url)
            .type("IMAGE")
            .isPrimary(true)
            .profile(this)
            .build();

    if (this.media == null) {
      this.media = new ArrayList<>();
    }
    this.media.add(avatar);
  }
}
