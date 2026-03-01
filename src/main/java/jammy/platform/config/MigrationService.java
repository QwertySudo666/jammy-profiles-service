//package jammy.platform.config;
//
//import io.quarkus.liquibase.LiquibaseFactory;
//import jakarta.enterprise.context.ApplicationScoped;
//import jakarta.inject.Inject;
//import liquibase.Liquibase;
//import liquibase.changelog.ChangeSetStatus;
//
//import java.util.List;
//
//@ApplicationScoped
//public class MigrationService {
//    // You can Inject the object if you want to use it manually
//    @Inject
//    LiquibaseFactory liquibaseFactory;
//
//    public void checkMigration() {
//        // Get the list of liquibase change set statuses
//        try (Liquibase liquibase = liquibaseFactory.createLiquibase()) {
//            List<ChangeSetStatus> status = liquibase.getChangeSetStatuses(liquibaseFactory.createContexts(), liquibaseFactory.createLabels());
//        }
//    }
//}