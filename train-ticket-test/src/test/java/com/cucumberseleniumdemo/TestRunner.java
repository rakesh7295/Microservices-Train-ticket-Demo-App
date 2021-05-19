package com.cucumberseleniumdemo;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = { "src/test/resources/features/"},
        plugin = {
                "pretty",
		"json:target/cucumber-json/cucumber.json",
		"junit:target/cucumber-reports/cucumber.xml",	
		"html:target/cucumber-reports"
               // "html:results/html",
               // "json:results/json/result.json",
               // "junit:results/junit/cucumber.xml"
        },
        monochrome = true
)


public class TestRunner {
}
