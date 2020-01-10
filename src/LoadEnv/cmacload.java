package LoadEnv;

import org.testng.annotations.Test;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class cmacload {
	
	String  baseUrl = "https://cmac-load.cox.com/prweb/";
	public static WebDriver driver;
	private static String userName = "CMACModalContractorOKO";
	private static String userPass = "rules";
	private static String AdminUser = "BusinessAdmin";
	public static String browser;
	public static WebDriverWait wait;
	
	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		
		System.setProperty("webdriver.chrome.driver","C:\\Users\\deemalho\\Downloads\\chromedriver.exe");
		driver = new ChromeDriver();
		DesiredCapabilities capability = new DesiredCapabilities();
		capability.setBrowserName("chrome");
	    capability.setPlatform(Platform.LINUX);
		capability.setCapability (CapabilityType.ACCEPT_SSL_CERTS, true);
		driver.manage().window().maximize();
	    wait = new WebDriverWait(driver, 10);
		try{
			LoginComponentCheck();
		}
		catch(Exception e)
		{
			
		}
	}

	public void LoginComponentCheck() throws IOException, InterruptedException {
		driver.get(baseUrl);
			   
		System.out.println(driver.getTitle());
		driver.findElement(By.id("txtUserID")).clear();
		driver.findElement(By.id("txtUserID")).sendKeys(userName);
		driver.findElement(By.id("txtPassword")).clear();
		driver.findElement(By.id("txtPassword")).sendKeys(userPass);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@id='sub']//span[@class='loginButtonText'][contains(text(),'Log in')]")));
		driver.findElement(By.xpath("//button[@id='sub']//span[@class='loginButtonText'][contains(text(),'Log in')]")).click();
		System.out.println("Title :"+driver.getTitle());
		System.out.println("**************** Logged into CMAC-LOAD ****************");
	 }
	
	@Test(enabled = true)
	public void IssueOrder() throws Exception {
		
		File scr = new File("C:\\Users\\deemalho\\Downloads\\SeleniumJava\\Excel\\CMAC.xlsx");
		FileInputStream fis = new FileInputStream(scr);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet1 =	wb.getSheetAt(0);
		int noOfRows=sheet1.getPhysicalNumberOfRows();
		if(noOfRows<1)
		{
			System.out.println("SHEET HAS NO DATA OF WATTS ORDER & PART NO.");
			Assert.fail();
		}
		
		List<String> WattsOrderList = new ArrayList<>();
		
		System.out.println("Number of Rows: "+noOfRows);
		
			for(int i=0;i<noOfRows;i++)
			{
				driver.navigate().refresh();
				Thread.sleep(4000);
				 for(int j=0;j<=50;j++)  // second statement of i for
					if(sheet1.getRow(i).getCell(j)!=null) // only one statement of j loop i.e. if else statement
					{
						WattsOrderList.add(j,sheet1.getRow(i).getCell(j).getStringCellValue());
					}
					else
					{
						IssueWatts(WattsOrderList, i, j);
						break;
						
					}//end of else of j loop
			}// end if i loop
		
		wb.close();	   
	}//end of IssueOrder
	
	@Test(enabled = false)
	public void UnIssueOrder() throws Exception {

		driver.get(baseUrl);
		File scr = new File("C:\\Users\\deemalho\\Downloads\\SeleniumJava\\Excel\\CMAC.xlsx");
		FileInputStream fis = new FileInputStream(scr);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet1 =	wb.getSheetAt(0);
		String WattsOrder = sheet1.getRow(1).getCell(0).getStringCellValue();
		String PartNo = sheet1.getRow(1).getCell(1).getStringCellValue();
		
		System.out.println("SEARCHING WATTS Order :" + WattsOrder);
		if(SearchWattsOrder(WattsOrder))
		{
			System.out.println("WATTS Order: " + WattsOrder +" FOUND!");
			driver.findElement(By.xpath("//span[contains(text(),'"+WattsOrder+"')]")).click();
			System.out.println("WATTS ORDER CLICKED SUCCESSFULLY");
			System.out.println("SEARCHING PART No. :" + PartNo);
			if(SearchPartNo(PartNo))
			{
				System.out.println("Part No: " + PartNo +" FOUND!");
				System.out.println("Un-Issuing Material for PartNo "+PartNo);
			    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[@id='$PpyWorkPage$pBoMDetails$l1']//td[3]//span"))); //waiting for the first PartNo to load up on the page after clicking on Issue MAterials
				System.out.println("FOUND the first PartNo to load up on the page after clicking on Issue Materials");
				int i=1;	
						
				while(true)
					{
						if(driver.findElements(By.xpath("//tr[@id='$PpyWorkPage$pBoMDetails$l"+i+"']//td[3]//span")).size() != 0) //only if a Part No. is displayed @ ith position then only compare the text
							if(driver.findElement(By.xpath("//tr[@id='$PpyWorkPage$pBoMDetails$l"+i+"']//td[3]//span")).getText().equals(PartNo))
							{
								System.out.println("FOUND the location of PART NO. "+PartNo+" at "+i);
								break;
							}
							else
							{
								System.out.println("Trying to find "+i);
								i++;
							}
								
					}
						
					Thread.sleep(6000);
						
					WebElement element = driver.findElement(By.xpath("//button[contains(text(),'Un-Issue Materials') and @class='Secondary_Button pzhc pzbutton']"));
					Actions actions = new Actions(driver);
					actions.moveToElement(element).click().build().perform();
					System.out.println("Clicked on Un-Issue materials.");
						
					Thread.sleep(4000);
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='UnissuedQuantity"+i+"']")));
					driver.findElement(By.xpath("//input[@id='UnissuedQuantity"+i+"']")).sendKeys("1"); // or use this xpath //tr[@id='$PpyWorkPage$pBoMDetails$l"+i+"']//td[12]//input
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@name='ModalButtonActionArea_pyWorkPage_20']")));
					driver.findElement(By.xpath("//button[@name='ModalButtonActionArea_pyWorkPage_20']")).click();
						
					Thread.sleep(5000);
					//
					WebElement element1 = driver.findElement(By.xpath("//button[@name='ModalButtonActionArea_pyWorkPage_21']"));
					actions.moveToElement(element1).click().build().perform();
					//
					System.out.println("Material Un-Issued Successfully!");
					Thread.sleep(12000);
					//wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='fadeOut']")));
					//assertEquals(driver.findElement(By.xpath("//input[@id='fadeOut']")), "The issue transaction was successful");
						
			}// end of PART No FOUND if
			else
				{
					System.out.println(PartNo+" Not FOUND!");
					Assert.fail();
				}
						
					
		}// end of WATTS ORDER FOUND if
		else
			{
				System.out.println(WattsOrder + " Not FOUND!");
				Assert.fail();
			}
		
		wb.close();	   
	}//end of IssueOrder
	
	@AfterClass(alwaysRun = true)
	
	public void AC() throws Exception 
	{
		System.out.println("**************** End of After Class ****************");
		driver.close();
		driver.quit();
	}
	
	public boolean SearchWattsOrder(String wattsOrder) throws InterruptedException {
		
		Thread.sleep(5000);
		while(driver.findElements(By.xpath("//span[contains(text(),'"+wattsOrder+"')]")).size() != 0  || driver.findElements(By.xpath("//a[contains(text(),'>')]")).size() !=0 ) //Second statement of this function
			if(driver.findElements(By.xpath("//span[contains(text(),'"+wattsOrder+"')]")).size() != 0)
			{	
				return true;
			}
			else
			{
				if(driver.findElements(By.xpath("//a[contains(text(),'>')]")).size() != 0)
				{
					driver.findElement(By.xpath("//a[contains(text(),'>')]")).click();
					Thread.sleep(3000);
				}
			}
		
		return false; //Third statement of this function
		
	}// end of SearchWattsOrder Function
	
	public boolean SearchPartNo(String partNo) throws InterruptedException {
		
		Thread.sleep(5000);
		
		if(driver.findElements(By.xpath("//span[contains(text(),'"+partNo+"')]")).size() != 0)
		{
			return true;	
		}
		return false;
	} // end of SearchPartNo Function

	public void IssueWatts(List<String> WattsOrderList, int i, int j) throws InterruptedException
	{
		WattsOrderList.removeIf(item -> item == null || "".equals(item));
		
		System.out.println("Current Value of i: "+i+" & Value of j: "+j);
		System.out.println("WattsOrderList: ");
		System.out.println(WattsOrderList);
		
		String WattsOrder = WattsOrderList.get(0);
		System.out.println("SEARCHING WATTS Order :" + WattsOrder);
		Thread.sleep(3000);
		if(SearchWattsOrder(WattsOrder))
		{
			System.out.println("WATTS Order: " + WattsOrder +" FOUND!");
			driver.findElement(By.xpath("//span[contains(text(),'"+WattsOrder+"')]")).click();
			System.out.println("WATTS ORDER CLICKED SUCCESSFULLY");
			
			for (int k = 1; k < WattsOrderList.size(); k++) 
			{
				String PartNo = WattsOrderList.get(k);
				System.out.println("SEARCHING PART No. :" + PartNo);
				if(SearchPartNo(PartNo))
				{
					System.out.println("Part No: " + PartNo +" FOUND!");
				}
				else
				{
					WattsOrderList.remove(PartNo);
				 	System.out.println("PART NO. "+PartNo+" for Watts Order"+WattsOrder+" Not FOUND!");
				 	System.out.println("PART NO. "+PartNo+" Removed!");
				}
			}
			
			System.out.println("########## Issuing Material for PartNo "+WattsOrderList+" ##########");
			
			Thread.sleep(5000);
			
			WebElement element = driver.findElement(By.xpath("//button[contains(text(),'Issue Materials') and @class='Primary pzhc pzbutton']"));
			Actions actions = new Actions(driver);
			actions.moveToElement(element).click().build().perform();
			System.out.println("Clicked on Issue materials.");
			Thread.sleep(5000);
	    //	wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//tr[@id='$PpyWorkPage$pBoMDetails$l1']//td[3]//span"))); //waiting for the first PartNo to load up on the page after clicking on Issue MAterials
		//	System.out.println("FOUND the first PartNo to load up on the page after clicking on Issue Materials");
			
			for (int k = 1; k < WattsOrderList.size(); k++)
			{
				int i1=1;
				while(true)
				{
					if(driver.findElements(By.xpath("//tr[@id='$PpyWorkPage$pBoMDetails$l"+i1+"']//td[3]//span")).size() != 0) //only if a Part No. is displayed @ ith position then only compare the text
						if(driver.findElement(By.xpath("//tr[@id='$PpyWorkPage$pBoMDetails$l"+i1+"']//td[3]//span")).getText().equals(WattsOrderList.get(k)))
						{
							System.out.println("FOUND the location of PART NO. "+WattsOrderList.get(k)+" at "+i1);
							break;
						}
						else
						{
							//System.out.println("Trying to find "+i1);
							i1++;
						}			
				}
				int max=5;
				int min=1;
				Random rand = new Random();
				int n = rand.nextInt((max - min) + 1) + min;
				Thread.sleep(3000);
				//System.out.println("Value of i for reference : "+i1);
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='IssuedQuantity"+i1+"']")));
				driver.findElement(By.xpath("//input[@id='IssuedQuantity"+i1+"']")).clear();
				driver.findElement(By.xpath("//input[@id='IssuedQuantity"+i1+"']")).sendKeys(Integer.toString(n)); // or use this xpath //tr[@id='$PpyWorkPage$pBoMDetails$l"+i+"']//td[12]//input
				Thread.sleep(3000);
				/*wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id='fadeOut']")));
				assertEquals(driver.findElement(By.xpath("//input[@id='fadeOut']")), "The issue transaction was successful");*/
			}
			
			try{
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@name='ModalButtonActionArea_pyWorkPage_19']")));
				driver.findElement(By.xpath("//button[@name='ModalButtonActionArea_pyWorkPage_19']")).click();
				
				Thread.sleep(5000);
				WebElement element1 = driver.findElement(By.xpath("//button[@name='ModalButtonActionArea_pyWorkPage_22']"));
				actions.moveToElement(element1).click().build().perform();
				System.out.println("Material Issued Successfully for Watts Order: "+WattsOrder);
				Thread.sleep(12000);
			}
			catch (JavascriptException e) 
			{
				System.out.println("EXCEPTION OCCURED! ISSUING of Watts Order "+WattsOrderList.get(0)+" failed. Switching to the next WATTS ORDER.");
				driver.findElement(By.xpath("//div[@id='modaldialog_hd']//button[@id='container_close']")).click();
				Thread.sleep(4000);
				WebElement element1 = driver.findElement(By.xpath("//a[@name='ActionAreaHeader_pyWorkPage_6']"));
				actions.moveToElement(element1).click().build().perform();
				//driver.findElement(By.xpath("//a[@name='ActionAreaHeader_pyWorkPage_6']")).click();
				Thread.sleep(6000);
				System.out.println("CATCH BLOCK FINISHED!");
				System.out.println();
			}
		}
		else
		{
			System.out.println("WattsOrder "+WattsOrder + " Not FOUND!");
			System.out.println();
		}
		
		WattsOrderList.clear();
		System.out.println("WATTSORDERLIST AFTER FLUSHING IS: "+WattsOrderList);
		System.out.println("WATTSORDERLIST SIZE: "+WattsOrderList.size());

	}
}