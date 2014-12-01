SikuliRemoteWebdriver
=====================

This is the remote driver version of sikuliwebdriver. This works similar to sikulifirefoxdriver
(https://code.google.com/p/sikuli-api/wiki/SikuliWebDriver). 

Key differences from sikuliwebdriver.

1. Works with remote webdriver - so all browsers that are supported by selenium are supported by sikuliremotedriver as well
2. Has a new method to get the corresponding webelment of an image. You can do soemthing like
    WebElement we = sikuliremotedriverobject.findImage(url of image)
    we.getText();
3. Scrolls through the entire page looking for the element rather than just the area displayed in view port.

Example program:

      SikuliRemoteWebDriver driver = new SikuliRemoteWebDriver(DesiredCapabilities.chrome());
      driver.get("http://www.sears.com");
			driver.findElementById("keyword").sendKeys("blender");
			ImageElement image =  driver.findImageElement(new URL("file:///D:/sikuli learning/RemoteSikuli/search.png"));
			image.getAsWebElement().click();
			ImageElement image1 =  driver.findImageElement(new URL("file:///D:/sikuli learning/RemoteSikuli/blender.png"));
			image1.click();

Note:-
This is using old the Sikuli 1.1 jar.
