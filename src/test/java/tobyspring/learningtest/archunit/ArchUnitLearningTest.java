package tobyspring.learningtest.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "tobyspring.learningtest.archunit")
public class ArchUnitLearningTest {

    /**
     * Application 클래스를 의존하는 클래스는 application, adapter 패키지에만 있어야 한다.
     */
    @ArchTest
    void application(JavaClasses classes) {
        classes().that().resideInAPackage("..application..")
                .should().onlyHaveDependentClassesThat()
                .resideInAnyPackage("..application..", "..adapter..")
                .check(classes);
    }

    /**
     * application 에 있는 클래스는 adapter 패키지에 있는 클래스에 의존하면 안 된다.
     */
    @ArchTest
    void applicationNotDependOnAdapter(JavaClasses classes) {
       noClasses().that().resideInAPackage("..application..")
                .should().dependOnClassesThat().resideInAPackage("..adapter..")
                .check(classes);
    }

    /**
     * domain 의 클래스는 domain, java
     */
    @ArchTest
    void domain(JavaClasses classes) {
        classes().that().resideInAPackage("..domain..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("..domain..", "java..")
                .check(classes);
    }

}
