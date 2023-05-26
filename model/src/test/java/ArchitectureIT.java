import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;


public class ArchitectureIT {
    private static final String CORE = "..core..";
    private static final String PIECE = "..piece..";
    private static final String BOARD = "..board..";
    private static final String CARD = "..card..";
    private static final String PLAYER = "..player..";
    private static final String GAME = "..game..";
    private static final JavaClasses classes = new ClassFileImporter().importPackages("fr.kubys");
    ArchRule rule;

    @Test
    void core_should_not_depend_on_other_packages() {
        String[] dependencyPackages = getDependencyPackages(CORE, Collections.emptySet());
        rule = ArchRuleDefinition.classes()
                .that().resideInAPackage(CORE)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    private static String[] getDependencyPackages(String itself, Set<String> dependencies) {
        Stream<String> languageDependencies = Stream.of("java.lang..", "java.util..", "org.junit..");
        return Stream.of(
                        languageDependencies,
                        Stream.of(itself),
                        dependencies.stream()
                ).flatMap(dependency -> dependency)
                .toArray(String[]::new);
    }

    @Test
    void piece_should_only_depend_on_core() {
        String[] dependencyPackages = getDependencyPackages(PIECE, Set.of(CORE));
        rule = ArchRuleDefinition.classes()
                .that().resideInAPackage(PIECE)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void board_should_only_depend_on_core_and_piece() {
        String[] dependencyPackages = getDependencyPackages(BOARD, Set.of(CORE, PIECE));
        rule = ArchRuleDefinition.classes()
                .that().resideInAPackage(BOARD)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void card_should_only_depend_on_board_and_below() {
        String[] dependencyPackages = getDependencyPackages(CARD, Set.of(CORE, PIECE, BOARD));
        rule = ArchRuleDefinition.classes()
                .that().resideInAPackage(CARD)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void player_should_only_depend_on_card_and_core() {
        String[] dependencyPackages = getDependencyPackages(PLAYER, Set.of(CORE, CARD));
        rule = ArchRuleDefinition.classes()
                .that().resideInAPackage(PLAYER)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void nothing_should_depend_on_game() {
        rule = ArchRuleDefinition.classes()
                .that().resideInAPackage(GAME)
                .should().onlyHaveDependentClassesThat().resideInAPackage(GAME);
    }

    @Test
    void should_not_have_spring_dependencies_in_model() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideOutsideOfPackages()
                .should().dependOnClassesThat().resideInAPackage("..org.springframework..");
    }

    @AfterEach
    void check() {
        rule.check(classes);
    }
}
