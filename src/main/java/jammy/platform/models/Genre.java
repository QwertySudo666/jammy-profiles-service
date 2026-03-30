package jammy.platform.models;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Genre {
  private final UUID id;
  private final String name;
}
