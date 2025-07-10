package com.arshalif.cashi.bdd

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

/**
 * Cucumber Test Runner for BDD scenarios
 * 
 * This runner executes all feature files and generates reports
 * in both console and HTML format.
 */
@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["classpath:features"],
    glue = ["com.arshalif.cashi.bdd"],
    plugin = [
        "pretty"
    ],
    snippets = CucumberOptions.SnippetType.CAMELCASE
)
class CucumberTestRunner 