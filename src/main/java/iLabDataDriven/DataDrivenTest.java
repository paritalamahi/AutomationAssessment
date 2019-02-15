package iLabDataDriven;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.opencsv.CSVWriter;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static iLabDataDriven.Utility.getScreenshot;


/**
 * Created by maheshwaripenugonda on 2019/02/13.
 */
public class DataDrivenTest {
    public WebDriver driver;
    public ExtentHtmlReporter htmlReporter;
    public ExtentReports extent;
    public ExtentTest test;

    @BeforeTest
    public void setup() {
        //path of ExtentReports
        htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir")+"/test_output/iLabReport.html");
        htmlReporter.setAppendExisting(true);
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

    }
    @Test(priority = 1)
    @Parameters("browser")
    public void crossbrowser(String browserName)  {
        test = extent.createTest("browser is started: "+browserName);
        if(browserName.equalsIgnoreCase("firefox")) {
            System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/geckodriver");
            driver = new FirefoxDriver();
            test.log(Status.PASS,"Firefox is launched successfully");
        }
        else if(browserName.equalsIgnoreCase("chrome")){
            //Intiated chrome Driver
            ChromeDriverManager Manager = new ChromeDriverManager();
            Manager.getInstance().setup();
            driver = new ChromeDriver();
            test.log(Status.PASS,"Chrome is launched successfully");


        }
    }

    @Test(dataProvider = "testdata",priority = 2)
    public void demoiLab(String username, String email) throws IOException {
        //Test is started
        test = extent.createTest("iLab test is started");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        //navigate to iLab website
        driver.get("https://www.ilabquality.com/careers/");
        //Logs to appear in the extent Report
        test.pass("Ilab website is loaded", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //Maximizing web window
        driver.manage().window().fullscreen();
        //click on carrers link
        driver.findElement(By.linkText("CAREERS")).click();
        test.pass("CAREERS is clicked", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //clicking on southAfrica link
        driver.findElement(By.linkText("South Africa")).click();
        test.pass("SouthAfrica  is clicked", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //Storing all openings into a list
        List<WebElement> currentopenings = driver.findElements(By.xpath("/html/body/section/div[2]/div/div/div/div[3]/div[2]/div/div"));
        int list = currentopenings.size();
        //Logs to show number of lists
        test.log(Status.INFO, "List of current openings :" + list);
        System.out.println(list);
        for (WebElement links : currentopenings) {
            String link = links.getText();
            //Printing list of openings in console
            System.out.println(link);
            //Exporting list of openings to .csv file
            //path of csv file
            String csv = System.getProperty("user.dir") + "/Exportlist.csv";
            //writing text to csv file using below methods
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            String countryopenings = link;
            writer.writeNext(new String[]{countryopenings});
            writer.close();
            //Log shows list of openings in extentReport
            test.log(Status.INFO, "List of current openings" + link);
            //System.out.println(links);
        }
        //click on first opening available in the list
        driver.findElement(By.xpath("/html/body/section/div[2]/div/div/div/div[3]/div[2]/div/div/div/div/div/div[1]/div[1]/div[2]/span[1]/a")).click();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //click on Apply online button
        WebElement onlinebtn = driver.findElement(By.xpath("//*[@id=\"wpjb-scroll\"]/div[1]/a"));
        js.executeScript("arguments[0].scrollIntoView();", onlinebtn);
        onlinebtn.click();
        //Enter name to your name field
        driver.findElement(By.id("applicant_name")).sendKeys(username);
        //Logs showing Name has entered with screenshot
        test.pass("Entered user Name", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //Enter email in email field
        driver.findElement(By.id("email")).sendKeys(email);
        //Logs showing Email has entered with screenshot
        test.pass("Entered Email", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //Enter mobile Number
        //driver.findElement(By.id("phone")).sendKeys(phone);
        Random rd = new Random();
        String m[]=new String[10];

        for (int i = 0; i< 10; i++)
        {
            int rdnum=rd.nextInt(10);
            m[i]=Integer.toString(rdnum);


        }
        String phoneNumber=m[0]+m[1]+m[2]+m[3]+m[4]+m[5]+m[6]+m[7]+m[8]+m[9];
        System.out.println(phoneNumber);
        driver.findElement(By.id("phone")).sendKeys(phoneNumber);

        //Logs showing MobileNumber has entered with screenshot
        test.pass("Entered Phone Number", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //clicking on submit button
        driver.findElement(By.id("wpjb_submit")).click();
        //Logs showing submit button has clicked with screenshot
        test.pass("clicked submit button", MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //Assertion statements to check whether the submission is successful or not
        String actualtext=driver.findElement(By.xpath("//*[@id=\"wpjb-apply-form\"]/fieldset[1]/div[5]/div/ul")).getText();
        System.out.println("Result:" +actualtext);
        Assert.assertEquals("You need to upload at least one file.",actualtext);
        test.pass("Result :"+actualtext,MediaEntityBuilder.createScreenCaptureFromPath(getScreenshot(driver)).build());
        //Assert.assertTrue(driver.getTitle().contains("Successfully applied"));

    }


        @AfterMethod
        public void Termination(ITestResult result) throws Exception {
            //Getting screenshot of Failure scenario
            if (result.getStatus() == ITestResult.FAILURE) {
            //calling getScreenshot methodin Utility class
            String temp = getScreenshot(driver);
            JavascriptExecutor js = ((JavascriptExecutor) driver);
            js.executeScript("window.scrollTo(1, document.body.scrollHeight)");
            //Appending Failure scenario with screenshot to logs
            test.fail(result.getThrowable().getMessage(), MediaEntityBuilder.createScreenCaptureFromPath(temp).build());
            }
        }
        @AfterTest
        public void teardown() {

          try {
              //Report flushes
              extent.flush();
              Thread.sleep(10000);
          } catch (InterruptedException e) {
              System.out.println(e.getMessage());
          }
          //terminate the browser
          driver.quit();

        }

        @DataProvider(name = "testdata")
        public static Object[][] testdata () {
            //Data reading from excel file
            Object[][] userdetails = new Object[0][];
            try {
                //path of excelfile
                ReadExcelFile config = new ReadExcelFile(System.getProperty("user.dir")+"/src/main/resources/iLabTestData.xlsx");
                int rows = config.getRowCount(0);
                userdetails = new Object[rows][2];

                for (int i = 0; i < rows; i++) {
                    //Retrieving Data from first column
                    userdetails[i][0] = config.getData(0, i, 0);
                    //Retrieving cellData from second column
                    userdetails[i][1] = config.getData(0, i, 1);

                }


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return userdetails;
        }
    }





