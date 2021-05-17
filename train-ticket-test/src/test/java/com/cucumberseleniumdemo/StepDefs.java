package com.cucumberseleniumdemo;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import org.testng.Assert;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StepDefs 
{
    WebDriver driver;

    @Before public void setUp()
    { 
        System.setProperty("webdriver.chrome.driver", "/var/lib/jenkins/jobs/Train-ticket-Demo-MicroservicesApplication/branches/master/workspace/train-ticket-test/src/test/java/com/cucumberseleniumdemo/chromedriver");
        ChromeOptions options = new ChromeOptions().setHeadless(true);

        driver = new ChromeDriver(options);

        driver.get("http://10.0.100.124:32677");

        driver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
    } 
  
  	@Given("User opens Login Page")
    public void user_enters_url() 
    {
       driver.findElement(By.xpath("//*[@id='goto_admin']/a/span")).click(); 
       
    }
        
    @When("User enters invalid username and password")
    public void user_enters_Username_and_Password() 
    {
        driver.findElement(By.xpath("//*[@id='doc-ipt-email-1']")).sendKeys("admin");

        driver.findElement(By.xpath("//*[@id='doc-ipt-pwd-1']")).sendKeys("345672");

    }

    @Then("User should not be logged in")
    public void user_should_be_logged_in() 
    {
        driver.findElement(By.xpath("/html/body/div/div/div[2]/form/fieldset/p/button")).click();
      
      	WebDriverWait wait = new WebDriverWait(driver, 50);
        
        wait.until(ExpectedConditions.alertIsPresent());
 
      	String errmsg = "Wrong user name and password!";		
        		
      	String msg = driver.switchTo().alert().getText();	
      
      	Assert.assertFalse(errmsg.equals(msg));
                     
    }
    
  	@Given("User opens login page")
    public void user_enters_url_login_page() 
    {
       driver.findElement(By.xpath("//*[@id='goto_admin']/a/span")).click(); 
       
    }
        
    @When("User enters valid username and password")
    public void user_enters_username_and_password() 
    {
        driver.findElement(By.xpath("//*[@id='doc-ipt-email-1']")).sendKeys("admin");

        driver.findElement(By.xpath("//*[@id='doc-ipt-pwd-1']")).sendKeys("222222");

    }

    @Then("User should be logged in")
    public void user_should_be_Logged_in() 
    {
        driver.findElement(By.xpath("/html/body/div/div/div[2]/form/fieldset/p/button")).click();

      	driver.close();
                     
    }
    
      
}

