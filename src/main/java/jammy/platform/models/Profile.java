package jammy.platform.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class Profile {
    private UUID id;
    private String name;
}
