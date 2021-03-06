package iLabDataDriven;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;

/**
 * Created by maheshwaripenugonda on 2019/02/14.
 */
public  class Utility {
    public  static String getScreenshot(WebDriver driver) throws IOException {

        TakesScreenshot ts=(TakesScreenshot)driver;
        File src=ts.getScreenshotAs(OutputType.FILE);
        String path=System.getProperty("user.dir")+"/test_output/screenshots/testimages"+System.currentTimeMillis()+".png";
        File destination =new File(path);
        try{
            FileUtils.copyFile(src,destination);
        } catch (IOException e) {
            System.out.println(" captured failed"+e.getMessage());
        }
      return path;
    }

}
