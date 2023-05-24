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
    public static final String CORE = "core..";
    public static final String PIECE = "piece..";
    public static final String BOARD = "board..";
    public static final String CARD = "card..";
    public static final String PLAYER = "player..";
    public static final String GAME = "game..";
    private final JavaClasses classes = new ClassFileImporter().importPackages("");
    ArchRule rule;

    @Test
    void core_should_not_depend_on_other_packages() {
        String[] dependencyPackages = getDependencyPackages(CORE, Collections.emptySet());
        rule = ArchRuleDefinition.classes()
                .that().resideInAnyPackage(CORE)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void piece_should_only_depend_on_core() {
        String[] dependencyPackages = getDependencyPackages(PIECE, Set.of(CORE));
        rule = ArchRuleDefinition.classes()
                .that().resideInAnyPackage(PIECE)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void board_should_only_depend_on_core_and_piece() {
        String[] dependencyPackages = getDependencyPackages(BOARD, Set.of(CORE, PIECE));
        rule = ArchRuleDefinition.classes()
                .that().resideInAnyPackage(BOARD)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void card_should_only_depend_on_board_and_below() {
        String[] dependencyPackages = getDependencyPackages(CARD, Set.of(CORE, PIECE, BOARD));
        rule = ArchRuleDefinition.classes()
                .that().resideInAnyPackage(CARD)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void player_should_only_depend_on_card_and_core() {
        String[] dependencyPackages = getDependencyPackages(PLAYER, Set.of(CORE, CARD));
        rule = ArchRuleDefinition.classes()
                .that().resideInAnyPackage(PLAYER)
                .should().onlyDependOnClassesThat().resideInAnyPackage(dependencyPackages);
    }

    @Test
    void nothing_should_depend_on_game() {
        rule = ArchRuleDefinition.classes()
                .that().resideInAnyPackage(GAME)
                .should().onlyHaveDependentClassesThat().resideInAPackage(GAME);
    }

    private static String[] getDependencyPackages(String itself, Set<String> dependencies) {
        Stream<String> languageDependencies = Stream.of("java.lang..", "java.util..", "org.junit..", "org.slf4j..");
        return Stream.of(
                    languageDependencies,
                    Stream.of(itself),
                    dependencies.stream()
                ).flatMap(dependency -> dependency)
                .toArray(String[]::new);
    }

    @AfterEach
    void check() {
        rule.check(classes);
    }
}
