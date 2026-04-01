package jammy.platform.models;

import jammy.platform.enums.SkillLevel;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchFilter {
  private String name;
  private String location;
  private SkillLevel skill;
  private Integer minExperience;
  private Integer minAge;
  private List<String> instruments;
  private List<String> genres;
}
