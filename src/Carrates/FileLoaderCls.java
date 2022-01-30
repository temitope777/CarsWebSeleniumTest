package Carrates;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.URL;

public class FileLoaderCls {

    public  void GetConfigurations()
    {
        ClassLoader classLoader = getClass().getClassLoader();
        URL filesurl;
        filesurl = classLoader.getResource("car_input.txt");
        this.inputfilelocation = filesurl.getPath();

        filesurl = classLoader.getResource("car_output.txt");
        this.outputfilelocation = filesurl.getPath();

        filesurl = classLoader.getResource("geckodriver");
        this.webdriverlocation = filesurl.getPath();

        filesurl = classLoader.getResource("reportfile.html");
        this.reportfilelocation = filesurl.getPath();

    }

    private String inputfilelocation;
    public String getInputfilelocation() {
        return inputfilelocation;
    }
    public void setInputfilelocation(String c) {
        this.inputfilelocation = c;
    }

    private String outputfilelocation;
    public String getOutputfilelocation() {
        return outputfilelocation;
    }
    public void setOutputfilelocation(String c) {
        this.outputfilelocation = c;
    }

    private String reportfilelocation;
    public String getReportfilelocation() {
        return reportfilelocation;
    }
    public void setReportfilelocation(String c) {
        this.reportfilelocation = c;
    }

    private String webdriverlocation;
    public String getWebdriverlocation() {
        return webdriverlocation;
    }
    public void setWebdriverlocation(String c) {
        this.webdriverlocation = c;
    }

    private String webdrivertype = "webdriver.gecko.driver";;
    public String getWebdriverType() {
        return webdrivertype;
    }
    public void setWebdriverType(String c) {
        this.webdrivertype = c;
    }

    public String getReportTitle() {
        return "Car plate report test";
    }
    public String getBaseUrl() {
        return "https://cartaxcheck.co.uk/";
    }

    public WebDriver getWebDriver() {
       if (this.webdrivertype.contains("gecko"))
       {
           return  new FirefoxDriver();
       }
       if (this.webdrivertype.contains("chrome"))
        {
            return  new ChromeDriver();
        }
        return  new FirefoxDriver();
    }
}
