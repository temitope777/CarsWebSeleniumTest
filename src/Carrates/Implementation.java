package Carrates;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.openqa.selenium.By;
import static java.lang.System.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;


public class Implementation {
    static ExtentTest test;
    static ExtentReports report;
    static WebDriver driver;

    static  FileLoaderCls config;
    public static void main(String[] args) {
        try {
            Initialise();
            // TODO: Load files from
            List<String> a;
            List<String> extractedPlateNums = ExtractPlateNumbers(config.getInputfilelocation());
            List<CarDetails> processedInput = ProcessExtractedNumPlates(extractedPlateNums);
            List<CarDetails> existingOutput = processoutputFile(config.getInputfilelocation());
            compareResults(processedInput, existingOutput);
            EndTest();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void compareResults(List<CarDetails> processed, List<CarDetails> existing) {

        if ((existing != null) && !existing.isEmpty()) {


            processed.forEach(a ->
                    {
                        Optional<CarDetails> processedexisting = existing.stream().filter(b -> b.getRegistration().equals(a.getRegistration())).findAny();
                        ExtentTest node = test.createNode(String.format("Test for %s ", a.getRegistration()));

                        if (processedexisting.isPresent()) {
                            test.log(Status.PASS, String.format("Registration %s matched", a.getRegistration()));
                            ExtentTest x;
                            x = processedexisting.get().getMake() == a.getMake() ? node.log(Status.PASS, "Make matched") : node.log(Status.FAIL, "Make unmatched");
                            x = processedexisting.get().getModel() == a.getModel() ? node.log(Status.PASS, "Model matched") : node.log(Status.FAIL, "Model unmatched");
                            x = processedexisting.get().getColour() == a.getColour() ? node.log(Status.PASS, "Colour matched") : node.log(Status.FAIL, "Colour unmatched");
                            x = processedexisting.get().getRegistered() == a.getRegistered() ? node.log(Status.PASS, "Registred matched") : node.log(Status.FAIL, "Registred unmatched");
                            x = processedexisting.get().getYear() == a.getYear() ? node.log(Status.PASS, "getYear matched") : node.log(Status.PASS, "getYear matched");

                        } else {
                            node.log(Status.FAIL, String.format("Car with Registration %s not found", a.getRegistration()));
                        }
                        report.flush();
                    }
            );
        } else {
            test.log(Status.FAIL, "no match found");
            report.flush();
        }
    }


    //Done Methods

    static List<CarDetails> processoutputFile(String filename) {

        List<CarDetails> carDetails = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {

            stream.skip(1).forEachOrdered(line ->

                    {
                        CarDetails carDetail = new CarDetails();

                        String[] lineList = line.split(",");
                        carDetail.setRegistration(lineList[0]);
                        carDetail.setMake(lineList[1]);
                        carDetail.setModel(lineList[2]);
                        carDetail.setColour(lineList[3]);
                        carDetail.setColour(lineList[3]);
                        carDetail.setYear(lineList[4]);
                        carDetails.add(carDetail);

                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carDetails;
    }


    public static void Initialise() {
        FileLoaderCls configloader = new FileLoaderCls();
        configloader.GetConfigurations();
        System.setProperty(configloader.getWebdriverType(), configloader.getWebdriverlocation());
        driver = configloader.getWebDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        report = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(configloader.getReportfilelocation());
        report.attachReporter(spark);
        test = report.createTest(configloader.getReportTitle());
        config=configloader;
    }

    private static void EndTest() {
        driver.close();
    }

    static List<String> ExtractPlateNumbers(String filename) {
        final List<String>[] allRegistrations = new List[]{new ArrayList<>()};
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            stream.forEachOrdered(line ->
                    {
                        List<String> result = ProcessLine(line);
                        if (!result.isEmpty()) {
                            allRegistrations[0] = Stream.concat(allRegistrations[0].stream(), result.stream())
                                    .collect(Collectors.toList());
                        }

                    }

            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> finalResult = allRegistrations[0];
        return finalResult;
    }

    private static List<String> ProcessLine(String line) {
        List<String> words = new ArrayList<>();
        out.println(line);
        Pattern pattern = Pattern.compile("\\b[A-Z][A-Z0-9]+\\b");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            words.add(matcher.group());
        }

        List<String> fullRegNum = words.stream().filter(a -> a.length() == 7).collect(Collectors.toList());

        List<String> partialRegNum = words.stream().filter(a -> a.length() == 4).collect(Collectors.toList());
        partialRegNum.forEach(fourletterCap ->
        {
            String spacedRegNums = ProcessPartialNumPlate(fourletterCap, line);
            if (!spacedRegNums.isEmpty()) {
                fullRegNum.add(spacedRegNums);
            }
        });
        return fullRegNum;

    }

    private static String ProcessPartialNumPlate(String fourletterCap, String line) {

        String[] stringList = line.split(" ");
        int reqIndex = Arrays.asList(stringList).indexOf(fourletterCap);
        String nextitem = stringList[reqIndex + 1];
        return fourletterCap + nextitem;
    }


    private static List<CarDetails> ProcessExtractedNumPlates(List<String> numPlates) {

        // launch Fire fox and direct it to the Base URL
        List<CarDetails> allExtractedCars = new ArrayList<>();
        numPlates.forEach(a -> {
            driver.get(config.getBaseUrl());
            WebElement input = driver.findElement(By.id("vrm-input"));
            input.sendKeys(a);
            WebElement button = driver.findElement(By.xpath("//button[text()='Free Car Check']"));
            button.click();
            try {

                CarDetails cardetail = getCarDetails();
                allExtractedCars.add(cardetail);

            } catch (Exception ex) {
                ex.printStackTrace();
                ExtentTest node = test.createNode("Test failed during request");
                node.log(Status.FAIL, String.format("Search engine could not find %s", a));
                report.flush();
            }

        });
        // get the actual value of the title

        return allExtractedCars;
    }

    private static CarDetails getCarDetails() {
        WebElement div = driver.findElement(By.xpath("//*[@id=\"m\"]/div[2]/div[5]/div[1]/div/span/div[2]"));
        //List<WebElement> rows = div.findElements(By.tagName("dl"));
        CarDetails cardetail = new CarDetails();

        //Registration
        String registration = div.findElement(By.xpath("//dt[text()='Registration']/following-sibling::dd")).getText();
        cardetail.setRegistration(registration);

        //Make
        String make = div.findElement(By.xpath("//dt[text()='Make']/following-sibling::dd")).getText();
        cardetail.setMake(make);

        //Model
        String model = div.findElement(By.xpath("//dt[text()='Model']/following-sibling::dd")).getText();
        cardetail.setModel(model);

        //Model
        String colour = div.findElement(By.xpath("//dt[text()='Colour']/following-sibling::dd")).getText();
        cardetail.setColour(colour);

        //Year
        String year = div.findElement(By.xpath("//dt[text()='Year']/following-sibling::dd")).getText();
        cardetail.setYear(year);

        //registered
        String registered = div.findElement(By.xpath("//dt[text()='Registered']/following-sibling::dd")).getText();
        cardetail.setRegistered(registered);
        return cardetail;
    }


}
