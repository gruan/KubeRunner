package kuberunner;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;

/**
 * Created by george on 5/26/15.
 *
 * Loads Kuku-Kube and plays the game
 */
public class Runner
{
    private WebDriver driver;

    /**
     * Loads the specified url in the browser
     *
     * @param browser The browser. Firefox supported. Chrome may be supported in the future.
     */
    public Runner( String browser )
    {
        browser = browser.toUpperCase();
        if (browser.equals("FIREFOX"))
        {
            driver = new FirefoxDriver();
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
     * @return True if alert is present, false otherwise
     */
    public boolean isAlertPresent()
    {
        try
        {
            driver.switchTo().alert();
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
     * @param squares The array of squares
     * @param values The array of corresponding values
     * @return Whether a click was made or not
     */
    public boolean firstThree(List<WebElement> squares, String[] values)
    {
        boolean found = false;
        if( !values[0].equals(values[1]) )
        {
            if( !values[0].equals(values[2]) )
            {
                squares.get(0).click();
                found = true;
            }
            else
            {
                squares.get(1).click();
                found = true;
            }
        }
        else if( !values[0].equals(values[2]) )
        {
            squares.get(2).click();
            found = true;
        }

        return found;
    }

    /**
     * Finds the different square and clicks it in the level
     */
    public void findAndClick()
    {
        List< WebElement > squares = driver.findElements(By.cssSelector("#box > *"));
        String [] firstThreeVals = new String[3];
        String normalVal;

        for( int i = 0; i < 3; i++ )
        {
            String temp = squares.get(i).getAttribute("style");
            firstThreeVals[i] = temp.substring(22, temp.length()-2);
        }

        // Check if first 3 squares
        if( firstThree(squares, firstThreeVals) )
        {
            return;
        }
        else
        {
            normalVal = firstThreeVals[0];
        }

        for( int i = 3; i < squares.size(); i++ )
        {
            String val = squares.get(i).getAttribute("style");
            val = val.substring(22, val.length()-2);
            if( !val.equals(normalVal) )
            {
                squares.get(i).click();
                return;
            }
        }
    }

    public static void main( String [] args )
    {
        Runner game = new Runner( "firefox" );
        game.startGame();

        while( game.isRunning() )
        {
            game.findAndClick();
        }
    }
}
