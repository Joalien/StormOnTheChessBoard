package fr.kubys.matchmaking;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;

public class MatchmakingArchitectureIT {

    private static final JavaClasses classes = new ClassFileImporter().importPackages("fr.kubys.matchmaking");
    ArchRule rule;

    @Test
    void model_should_not_depend_on_controller() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..matchmaking.model..")
                .should().dependOnClassesThat().resideInAPackage("..matchmaking.controller..");
    }

    @Test
    void model_should_not_depend_on_spring() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..matchmaking.model..")
                .should().dependOnClassesThat().resideInAPackage("..org.springframework..");
    }

    @Test
    void matchmaking_should_not_depend_on_any_other_kubys_module() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage("fr.kubys.matchmaking..")
                .should().dependOnClassesThat(
                        resideInAPackage("fr.kubys..").and(not(resideInAPackage("fr.kubys.matchmaking..")))
                );
    }

    @AfterEach
    void check() {
        rule.check(classes);
    }
}
