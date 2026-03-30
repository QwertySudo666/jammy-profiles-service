package jammy.platform.models;

import jammy.platform.entities.InstrumentEntity;
import jammy.platform.entities.ProfileEntity;
import jammy.platform.enums.SkillLevel;
import java.util.Set;

public class SearchFilter {
  private ProfileEntity profile;
  private String name;
  private String targetLocation;
  private SkillLevel targetSkill;
  private Integer minExperience;
  private Boolean isActive;
  private Set<InstrumentEntity> targetInstrumentEntities;
}
