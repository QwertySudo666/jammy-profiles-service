package jammy.platform.repositories.impl;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jammy.platform.entities.SearchFilterEntity;
import java.util.UUID;

@ApplicationScoped
public class SearchFilterRepository implements PanacheRepositoryBase<SearchFilterEntity, UUID> {}
