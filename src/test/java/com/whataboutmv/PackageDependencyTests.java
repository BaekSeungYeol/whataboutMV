package com.whataboutmv;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

    private static final String MOVIE = "..modules.movie..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";
    private static final String MAIN = "..modules.movie..";


    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.whataboutmv.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.whataboutmv.modules..");

    @ArchTest
    ArchRule studyPackageRule = classes().that().resideInAPackage(MAIN)
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage(MOVIE, EVENT, MAIN);

    @ArchTest
    ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
            .should().accessClassesThat().resideInAnyPackage(MOVIE, ACCOUNT, EVENT);

    @ArchTest
    ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
            .should().accessClassesThat().resideInAnyPackage(TAG,ZONE,ACCOUNT);

    // circular dependency check
    @ArchTest
    ArchRule cycleCheck = slices().matching("com.whataboutmv.modules.(*)..")
            .should().beFreeOfCycles();
}
