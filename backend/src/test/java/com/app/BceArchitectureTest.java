package com.app;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Mechanical Architecture Cage for Adam Bien's BCE (Boundary-Control-Entity) Pattern in Quarkus.
 * Enforces layering, domain slice isolation, and bans legacy/Spring anti-patterns.
 */
@AnalyzeClasses(
    packages = "com.app",
    importOptions = {ImportOption.DoNotIncludeTests.class}
)
public class BceArchitectureTest {

    // 1. Strict BCE Layering (Allowing empty layers so new projects compile & pass on Day 1)
    @ArchTest
    static final ArchRule bce_layers_are_strictly_respected = Architectures.layeredArchitecture()
        .consideringOnlyDependenciesInLayers()
        .withOptionalLayers(true)
        .layer("Boundary").definedBy("..boundary..")
        .layer("Control").definedBy("..control..")
        .layer("Entity").definedBy("..entity..")
        .whereLayer("Boundary").mayNotBeAccessedByAnyLayer()
        .whereLayer("Control").mayOnlyBeAccessedByLayers("Boundary", "Control")
        .whereLayer("Entity").mayOnlyBeAccessedByLayers("Boundary", "Control", "Entity");

    // 2. Entities must be pure (zero dependencies on Control or Boundary)
    @ArchTest
    static final ArchRule entities_must_not_depend_on_control_or_boundary = noClasses()
        .that().resideInAPackage("..entity..")
        .should().dependOnClassesThat().resideInAnyPackage("..control..", "..boundary..")
        .allowEmptyShould(true)
        .because("Entities represent pure domain state and payloads and must not know about services or REST endpoints.");

    // 3. Cross-Domain Isolation: A domain's Control layer is private to its own domain slice!
    // Other domains may ONLY communicate via ..boundary.. or ..entity..
    @ArchTest
    static final ArchRule control_classes_are_isolated_to_their_own_domain_slice = classes()
        .that().resideInAPackage("com.app.(*).control..")
        .should(onlyBeAccessedBySameDomainSlice())
        .allowEmptyShould(true)
        .because("Cross-domain communication must go through another domain's Boundary or shared Entity, never directly into another domain's Control.");

    // 4. No cyclic dependencies between business domain slices
    @ArchTest
    static final ArchRule domain_slices_must_be_free_of_cycles = SlicesRuleDefinition.slices()
        .matching("com.app.(*)..")
        .should().beFreeOfCycles()
        .allowEmptyShould(true);

    // 5. Ban Spring & Legacy Java EE hallucinations
    @ArchTest
    static final ArchRule ban_spring_and_legacy_javaee_imports = noClasses()
        .should().dependOnClassesThat().resideInAnyPackage(
            "org.springframework..",
            "javax.persistence..",
            "javax.ws.rs..",
            "javax.inject..",
            "javax.enterprise..",
            "javax.validation.."
        )
        .allowEmptyShould(true)
        .because("This is a modern Quarkus + Jakarta EE (jakarta.*) codebase. Never use Spring or legacy javax.* packages.");

    // 6. Ban *Impl suffix anti-pattern
    @ArchTest
    static final ArchRule ban_impl_class_naming_antipattern = noClasses()
        .should().haveSimpleNameEndingWith("Impl")
        .allowEmptyShould(true)
        .because("In BCE, classes in ..control.. implement business logic directly without redundant *Service/*ServiceImpl interfaces.");

    private static ArchCondition<JavaClass> onlyBeAccessedBySameDomainSlice() {
        return new ArchCondition<>("only be accessed by classes within the same business domain slice") {
            @Override
            public void check(JavaClass targetControlClass, ConditionEvents events) {
                String targetSlice = extractDomainSlice(targetControlClass.getPackageName());
                if (targetSlice == null) {
                    return;
                }
                targetControlClass.getAccessesToSelf().forEach(access -> {
                    JavaClass originClass = access.getOriginOwner();
                    // Ignore Quarkus generated synthetic/proxy classes in same package
                    String originSlice = extractDomainSlice(originClass.getPackageName());
                    if (originSlice != null && !originSlice.equals(targetSlice)) {
                        String message = String.format(
                            "Class <%s> in domain [%s] accesses Control class <%s> in domain [%s] (%s)",
                            originClass.getFullName(), originSlice,
                            targetControlClass.getFullName(), targetSlice,
                            access.getDescription()
                        );
                        events.add(SimpleConditionEvent.violated(access, message));
                    }
                });
            }

            private String extractDomainSlice(String packageName) {
                String prefix = "com.app.";
                if (!packageName.startsWith(prefix)) {
                    return null;
                }
                String remainder = packageName.substring(prefix.length());
                int dotIndex = remainder.indexOf('.');
                return dotIndex > 0 ? remainder.substring(0, dotIndex) : remainder;
            }
        };
    }
}
