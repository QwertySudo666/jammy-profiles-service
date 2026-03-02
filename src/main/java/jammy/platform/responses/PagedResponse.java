package jammy.platform.responses;

import java.util.List;

public record PagedResponse<T>(List<T> data, long totalCount, int page, int size) {}
