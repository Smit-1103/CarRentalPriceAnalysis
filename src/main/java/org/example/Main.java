package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Main {
    private static WebDriver driver;  // WebDriver instance

    // This is the main method, the entry point of our program.
    public static void main(String[] args) {
        // We're setting up ChromeDriver using WebDriverManager to handle browser automation.
        WebDriverManager.chromedriver().setup();
        // Initializing a ChromeDriver instance to interact with the Chrome browser.
        driver = new ChromeDriver();

        try {
            // We're calling the method scrapeAndSaveData() to retrieve and save data to Excel.
            scrapeAndSaveData();
        } finally {
            // After the operation, we're ensuring that if the driver is not null, it's properly closed to release resources.
            if (driver != null) {
                driver.quit();
            }
        }
    }

    // This method scrapes data and saves it to an Excel file.
    private static void scrapeAndSaveData() {
        // Path where the Excel file will be saved
        String excelFilePath = "rental_car_data.xlsx";

        try {
            // Defining the header for the Excel file
            String[] header = {"Car Name", "Car Image URL", "Car Rating", "Car Price"};

            // Creating a new Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Car Data");

            // Creating the header row in the Excel sheet
            Row headerRow = sheet.createRow(0);
            // Populating the header row with the defined header
            for (int i = 0; i < header.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header[i]);
            }

            // Calling a method to scrape car data and populate the sheet
            scrapeCarData(sheet);

            // Saving the workbook to the specified file path
            try (FileOutputStream outputStream = new FileOutputStream(new File(excelFilePath))) {
                workbook.write(outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();  // Printing stack trace if an IO exception occurs
        }
    }

    // This method scrapes car data from a rental website and populates it into the provided Excel sheet.
    private static void scrapeCarData(Sheet sheet) {
        try {
            driver.manage().window().maximize();  // Maximize the browser window
            driver.get("https://getaround.com/");  // Navigate to the target website
            System.out.println("Website opened.");

            //Task 3 Advance selenium command to handle the pop up
            // Handling the cookie consent modal by waiting for its presence and clicking to accept.
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("js_cookie-consent-modal__agreement"))).click();

            // Interacting with the search input field to specify the location.
            WebElement input = driver.findElement(By.id("order_address"));
            input.click();
            input.sendKeys("Hollywood, Los Angeles");

            // Wait until the suggestion appears and select it
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[1]/div[1]/div/div/div[1]/ul/li[2]")));
            WebElement itemselect = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[1]/div[1]/div/div/div[1]/ul/li[2]"));
            itemselect.click();

            // Open the date picker for the rental period
            WebElement div = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[1]"));
            div.click();

            // Scroll to the date picker element
            Actions action2 = new Actions(driver);
            WebElement divdate = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]"));
            action2.scrollToElement(divdate).perform();

            // Select the start date for the rental period
            WebElement datepicker = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]/div[1]/div[2]/div[2]/div[2]/div"));
            datepicker.click();

            // Select the start time for the rental period
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[1]/div[3]/div/div/div/div/div/div/div[16]")));
            WebElement timepicker = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[1]/div[3]/div/div/div/div/div/div/div[16]"));
            timepicker.click();
            Thread.sleep(1000);

            // Open the date picker for the end date
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[3]")));
            WebElement div2 = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[3]"));
            div2.click();

            // Scroll to the date picker element
            WebElement divdate2 = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]"));
            action2.scrollToElement(divdate2).perform();

            // Select the end date for the rental period
            WebElement dropdatepicker = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[4]/div/div/div/div/div/div/div[2]/div[1]/div[1]/div[2]/div[3]/div[2]"));
            dropdatepicker.click();

            // Select the end time for the rental period
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[3]/div[3]/div/div/div/div/div/div/div[18]")));
            WebElement droptimepicker = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[1]/div[2]/div/div/div[3]/div[3]/div/div/div/div/div/div/div[18]"));
            droptimepicker.click();

            // Wait until the search button is clickable and then click it
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[2]/button")));
            WebElement searchbutton = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div[1]/div/form/div/div[2]/button"));
            searchbutton.click();

            Thread.sleep(2000);

            // Scroll to the footer to load more results
            Actions action = new Actions(driver);
            WebElement footer = driver.findElement(By.className("corporate_footer__container"));
            action.scrollToElement(footer).perform();

            boolean loadMoreVisible = true;

            // Scrolling to load more results and continuously clicking "Load More" if available.
            // This ensures more results are loaded onto the page.
            while (loadMoreVisible) {
                try {
                    WebElement loadmore = driver.findElement(By.className("search-results__load-more-button"));
                    if (loadmore.isDisplayed()) {
                        action.scrollToElement(footer).perform();
                        Thread.sleep(2000);
                        loadmore.click();
                        Thread.sleep(2000);
                        action.scrollToElement(footer).perform();
                    } else {
                        loadMoreVisible = false;
                    }
                } catch (NoSuchElementException e) {
                    loadMoreVisible = false;
                }
                System.out.println("Load more clicked");
            }

            // Find all car data elements on the page
            List<WebElement> carImg = driver.findElements(By.className("car_card__header"));
            List<WebElement> carName = driver.findElements(By.className("car_card__title"));
            List<WebElement> carRating = driver.findElements(By.className("cobalt-rating__label"));
            List<WebElement> carPrice = driver.findElements(By.className("car_card__pricing-value"));

            System.out.println(carImg.size());

            // Extract data for each car and write to Excel
            for (int i = 0; i < carImg.size(); i++) {
                String name = carName.get(i).getText();
                String imgURL = carImg.get(i).getAttribute("data-background-image");
                String rating = carRating.get(i).getText();
                String price = carPrice.get(i).getText();

                // Write data to Excel
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0).setCellValue(name);
                row.createCell(1).setCellValue(imgURL);
                row.createCell(2).setCellValue(rating);
                row.createCell(3).setCellValue(price);
            }

            //scraping the data from *** PAGE 2 ***
            // Navigate to another URL to scrape additional data
            driver.get("https://getaround.com/search?address=Fairmount%2C+Newark&address_source=poi&poi_id=2949&latitude=40.7426&longitude=-74.1921&city_display_name=&start_date=2024-06-13&start_time=07%3A30&end_date=2024-06-15&end_time=06%3A30&country_scope=US&display_view=list&pickup_method=false&pickup_method_explicit_choice=&administrative_area=");
            Thread.sleep(4000);
            System.out.println("on another website");

            WebElement footer2 = driver.findElement(By.className("corporate_footer__container"));
            action.scrollToElement(footer2).perform();

            boolean loadMoreVisible2 = true;

            // This involves waiting for page elements, scrolling to load more results, and extracting car data.
            while (loadMoreVisible2) {
                try {
                    WebElement loadmore = driver.findElement(By.className("search-results__load-more-button"));
                    if (loadmore.isDisplayed()) {
                        action.scrollToElement(footer2).perform();
                        Thread.sleep(2000);
                        loadmore.click();
                        Thread.sleep(2000);
                        action.scrollToElement(footer2).perform();
                    } else {
                        loadMoreVisible2 = false;
                    }
                } catch (NoSuchElementException e) {
                    loadMoreVisible2 = false;
                }
                System.out.println("Load more clicked");
            }

            // Find all car data elements on the second page
            List<WebElement> carImgsite2 = driver.findElements(By.className("car_card__header"));
            List<WebElement> carNamesite2 = driver.findElements(By.className("car_card__title"));
            List<WebElement> carRatingsite2 = driver.findElements(By.className("cobalt-rating__label"));
            List<WebElement> carPricesite2 = driver.findElements(By.className("car_card__pricing-value"));

            System.out.println(carImgsite2.size());

            // Extract data for each car and write to Excel
            for (int i = 0; i < carImgsite2.size(); i++) {
                String name2 = carNamesite2.get(i).getText();
                String imgURL2 = carImgsite2.get(i).getAttribute("data-background-image");
                String rating2 = carRatingsite2.get(i).getText();
                String price2 = carPricesite2.get(i).getText();

                // Write data to Excel
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(0).setCellValue(name2);
                row.createCell(1).setCellValue(imgURL2);
                row.createCell(2).setCellValue(rating2);
                row.createCell(3).setCellValue(price2);
            }

        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace if an exception occurs
        }
    }
}
