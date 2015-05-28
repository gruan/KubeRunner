package kuberunner;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Scanner;

/**
 * Created by george on 5/26/15.
 *
 * Loads Kuku-Kube and plays the game.
 * Firefox and Chrome are supported. Chrome works much faster than firefox.
 */
public class Runner
{
    private WebDriver driver;

    /**
     * Loads the specified url in the browser
     *
     * @param browser The browser. Firefox and Chrome are supported.
     */
    public Runner( String browser )
    {
        browser = browser.toUpperCase();
        if (browser.equals("FIREFOX"))
        {
            driver = new FirefoxDriver();
        }
        if(browser.equals("CHROME"))
        {
            driver = new ChromeDriver();
        }

        driver.get("http://kuku-kube.com");
    }

    /**
     * Starts the instance of the game KuKu-Kube
     */
    public void startGame()
    {
        while (true)
        {
            WebElement start = driver.findElement(By.cssSelector("#index > div.btns > button"));
            if (start.isDisplayed())
            {
                start.click();
                return;
            }
        }
    }

    /**
     * Converts a string representing an integer to its integer literal
     *
     * @param number The string representation of the integer
     * @return The literal integer
     */
    public int stringToInt(String number)
    {
        int result = 0;
        int base = number.length() - 1;
        for (int i = 0; i < number.length(); i++)
        {
            int digit = number.charAt(i) - 48;
            if (digit >= 0 && digit <= 9)
            {
                result += digit * Math.pow(10, base);
            } else
            {
                return -1;
            }
            base--;
        }
        return result;
    }

    /**
     * Checks if an alert is present
     * @param user The username of the user
     * @return True if alert is present, false otherwise
     */
    public boolean isAlertPresent( String user )
    {
        try
        {
            Alert alert = driver.switchTo().alert();
            alert.sendKeys(user);
            alert.accept();
            return true;
        }
        catch( NoAlertPresentException e )
        {
            return false;
        }
    }

    /**
     * Returns whether the game is running or not
     * @return True if the game is running, false otherwise
     */
    public boolean isRunning()
    {
        WebElement box = driver.findElement(By.cssSelector("#box"));
        String lv = box.getAttribute("class");
        int level = lv.charAt(2) - 48;

        WebElement timeElem = driver.findElement(By.cssSelector("#room > header > span.time"));
        String timeStr = timeElem.getText();
        int time = stringToInt(timeStr);

        return level != 1 && time != 1;
    }

    /**
     * From three square rgb values, determines whether the squares
     * are different and if one is, it clicks that one.
     *
     * NOTE: Remember that the list of WebElement span starts at 1 not 0!
     * @param values The array of corresponding values
     * @return Whether a click was made or not
     */
    public boolean firstThree( String[] values )
    {
        boolean found = false;
        if( !values[0].equals(values[1]) )
        {
            if( !values[0].equals(values[2]) )
            {
                driver.findElement(By.cssSelector("#box > span:nth-child(1)")).click();
                found = true;
            }
            else
            {
                driver.findElement(By.cssSelector("#box > span:nth-child(2)")).click();
                found = true;
            }
        }
        else if( !values[0].equals(values[2]) )
        {
            driver.findElement(By.cssSelector("#box > span:nth-child(3)")).click();
            found = true;
        }

        return found;
    }

    /**
     * Parses the source page for the html values of the squares
     * @return An array of the html values of the squares
     */
    public String[] parseSrc()
    {
        String [] values;

        String src = driver.getPageSource();
        int begin = src.indexOf("496px;\"><") + 8;
        int endScope = src.indexOf("id=\"dialog\"");
        src = src.substring(begin, endScope);
        int end = src.indexOf("</span></div>");
        src = src.substring(0, end);

        values = src.split("</span>");

        return values;
    }

    /**
     * Finds the different square and clicks it in the level
     */
    public void findAndClick()
    {
        String normalVal;
        String [] values = parseSrc();
        if( firstThree(values) )
        {
            return;
        }
        else
        {
            normalVal = values[0];
        }

        for( int i = 3; i < values.length; i++ )
        {
            if( !values[i].equals(normalVal) )
            {
                int squareIndex = i + 1;
                String squareCSS = "#box > span:nth-child(" + squareIndex + ")";
                driver.findElement(By.cssSelector(squareCSS)).click();
                return;
            }
        }

    }

    public static void main( String [] args )
    {
        String user;
        String browser;
        Scanner input = new Scanner( System.in );
        boolean invalid = true;

        do
        {
            System.out.println("What is your name?");
            user = input.nextLine();
            System.out.println("Which browser would you like to use? Firefox or Chrome (Note: Chrome runs faster).");
            browser = input.nextLine();

            if( user!= null && (browser.toUpperCase().equals("CHROME") || browser.toUpperCase().equals("FIREFOX")) )
            {
                System.out.println("Please wait...");
                invalid = false;
            }
            else
            {
                System.out.println("Error! please input a non-empty username and a valid browser!");
            }

        } while(invalid);

        Runner game = new Runner( browser );
        game.startGame();

        while( game.isRunning() )
        {
            game.findAndClick();
        }

        while( !game.isAlertPresent(user) )
        {}
    }
}
