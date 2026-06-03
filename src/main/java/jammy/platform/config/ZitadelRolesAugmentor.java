package jammy.platform.config;

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class ZitadelRolesAugmentor implements SecurityIdentityAugmentor {

  @Override
  public Uni augment(SecurityIdentity identity, AuthenticationRequestContext context) {
    if (!identity.isAnonymous() && identity.getPrincipal() instanceof OidcJwtCallerPrincipal) {
      OidcJwtCallerPrincipal jwtPrincipal = (OidcJwtCallerPrincipal) identity.getPrincipal();

      String zitadelRolesClaim = "urn:zitadel:iam:org:project:roles";

      if (jwtPrincipal.getClaims().hasClaim(zitadelRolesClaim)) {
        Map<String, Object> rolesMap = jwtPrincipal.getClaim(zitadelRolesClaim);
        Set<String> roles = rolesMap.keySet();

        QuarkusSecurityIdentity.Builder builder = QuarkusSecurityIdentity.builder(identity);
        for (String role : roles) {
          builder.addRole(role);
        }
        return Uni.createFrom().item(builder.build());
      }
    }
    return Uni.createFrom().item(identity);
  }
}
