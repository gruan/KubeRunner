package kuberunner;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.server.handler.BySelector;

/**
 * Created by george on 5/26/15.
 */
public class Runner
{
    private String link;
    private WebDriver driver;

    /**
     * Loads the specified url in the browser
     *
     * @param url     The url of the page
     * @param browser The browser. Firefox and Chrome are supported
     */
    public Runner(String url, String browser)
    {
        this.link = url;

        browser = browser.toUpperCase();
        if (browser.equals("FIREFOX"))
        {
            driver = new FirefoxDriver();
        }

        driver.get(link);
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
     * Returns whether the game is running or not
     *
     * @param box   The Webelement that contains level information
     * @param level The current level
     * @return True if the game is running, false otherwise
     */
    public boolean isRunning(WebElement box, int[] level)
    {
        box = driver.findElement(By.cssSelector("#box"));
        String lv = box.getAttribute("class");
        level[0] = lv.charAt(2) - 48;

        WebElement timeElem = driver.findElement(By.cssSelector("#room > header > span.time"));
        String timeStr = timeElem.getText();
        int time = stringToInt(timeStr);

        if (level[0] != 1 && time != 1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * From three square rgb values, determines whether the squares
     * are different and if one is, it clicks that one.
     * @param squares The array of squares
     * @param values The array of corresponding values
     * @return Whether a click was made or not
     */
    public boolean clickSquare(WebElement[] squares, String[] values)
    {
        boolean found = false;
        if( !values[0].equals(values[1]) )
        {
            if( !values[0].equals(values[2]) )
            {
                squares[0].click();
                found = true;
            }
            else
            {
                squares[1].click();
                found = true;
            }
        }
        else if( !values[0].equals(values[2]) )
        {
            squares[2].click();
            found = true;
        }

        return found;
    }

    /**
     * Finds the different square and clicks it in the level
     * @param level The current level in the game
     */
    public void findAndClick( int [] level )
    {
        int elements = level[0] * level[0];
        WebElement [] squares = new WebElement[3];
        String [] values = new String[3];
        String normalVal;

        for( int i = 0; i < 3; i++ )
        {
            int j = i + 1;
            String squareCSS = "#box > span:nth-child(" + j + ")";

            squares[i] = driver.findElement(By.cssSelector(squareCSS));
            values[i] = squares[i].getAttribute("style");
        }

        // Check if first 3 squares
        if( clickSquare( squares, values ) )
        {
            return;
        }
        else
        {
            normalVal = values[0];
        }

        for( int i = 3; i < elements; i++ )
        {
            int j = i + 1;
            String squareCSS = "#box > span:nth-child(" + j + ")";
            WebElement sqr = driver.findElement(By.cssSelector(squareCSS));
            String val = sqr.getAttribute("style");
            if( !val.equals(normalVal) )
            {
                sqr.click();
                return;
            }
        }
    }

    public static void main( String [] args )
    {
        Runner game = new Runner( "http://kuku-kube.com/", "firefox" );
        game.startGame();

        WebElement box = game.driver.findElement(By.cssSelector("#box"));
        int [] level = new int [1];

        while( game.isRunning(box, level) )
        {
            game.findAndClick( level );
        }
    }
}
