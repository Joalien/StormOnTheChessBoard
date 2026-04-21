package fr.kubys;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class CrossContextArchitectureIT {

    private static final String[] CHESS_PACKAGES = {
            "..board..", "..piece..", "..card..", "..game..", "..player..", "..repository.."
    };
    private static final String MATCHMAKING = "..matchmaking..";
    private static final String AI = "..ai..";

    private static final JavaClasses classes = new ClassFileImporter().importPackages("fr.kubys");
    ArchRule rule;

    @Test
    void chessboard_should_not_depend_on_matchmaking() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideInAnyPackage(CHESS_PACKAGES)
                .should().dependOnClassesThat().resideInAPackage(MATCHMAKING);
    }

    @Test
    void matchmaking_should_not_depend_on_chessboard() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage(MATCHMAKING)
                .should().dependOnClassesThat().resideInAnyPackage(CHESS_PACKAGES);
    }

    @Test
    void ai_should_not_depend_on_matchmaking() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage(AI)
                .should().dependOnClassesThat().resideInAPackage(MATCHMAKING);
    }

    @Test
    void matchmaking_should_not_depend_on_ai() {
        rule = ArchRuleDefinition.noClasses()
                .that().resideInAPackage(MATCHMAKING)
                .should().dependOnClassesThat().resideInAPackage(AI);
    }

    @AfterEach
    void check() {
        rule.check(classes);
    }
}
