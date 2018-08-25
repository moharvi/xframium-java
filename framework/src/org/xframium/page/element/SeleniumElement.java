/*******************************************************************************
 * xFramium
 *
 * Copyright 2016 by Moreland Labs, Ltd. (http://www.morelandlabs.com)
 *
 * Some open source application is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU General Public 
 * License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *  
 * Some open source application is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with xFramium.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @license GPL-3.0+ <http://spdx.org/licenses/GPL-3.0+>
 *******************************************************************************/
package org.xframium.page.element;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.ContextAware;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.HasTouchScreen;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.xframium.device.cloud.CloudRegistry;
import org.xframium.device.cloud.action.CloudActionProvider;
import org.xframium.device.factory.DeviceWebDriver;
import org.xframium.device.factory.DriverFactory;
import org.xframium.device.factory.MorelandWebElement;
import org.xframium.exception.ObjectIdentificationException;
import org.xframium.exception.ScriptConfigurationException;
import org.xframium.exception.ScriptException;
import org.xframium.integrations.common.PercentagePoint;
import org.xframium.integrations.perfectoMobile.rest.PerfectoMobile;
import org.xframium.integrations.perfectoMobile.rest.services.Imaging.ImageFormat;
import org.xframium.integrations.perfectoMobile.rest.services.Imaging.Resolution;
import org.xframium.integrations.perfectoMobile.rest.services.Imaging.Screen;
import org.xframium.integrations.perfectoMobile.rest.services.Repositories.RepositoryType;
import org.xframium.page.BY;
import org.xframium.page.ElementDescriptor;
import org.xframium.page.PageManager;
import org.xframium.page.element.spi.set.SetMethodFactory;
import org.xframium.spi.driver.CachingDriver;
import org.xframium.spi.driver.NativeDriverProvider;
import org.xframium.spi.driver.VisualDriverProvider;
import org.xframium.utility.XPathGenerator;
import org.xframium.utility.html.HTMLElementLookup;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

// TODO: Auto-generated Javadoc
/**
 * The Class SeleniumElement.
 */
public class SeleniumElement extends AbstractElement
{

  /** The log. */
  private static Log log = LogFactory.getLog(SeleniumElement.class);

  /** The Constant EXECUTION_ID. */
  private static final String EXECUTION_ID = "EXECUTION_ID";

  /** The Constant DEVICE_NAME. */
  private static final String DEVICE_NAME = "DEVICE_NAME";

  /** The located element. */
  private WebElement locatedElement;

  /** The count. */
  private int count = -1;

  /** The index. */
  private int index = -1;

  /** The use visual driver. */
  private boolean useVisualDriver = false;

  private boolean clonedElement = false;

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.Element#cloneElement()
   */
  public Element cloneElement()
  {
    //
    // If we already cloned this once then don't need to again
    //
    // if ( clonedElement )
    // {
    // return this;
    // }
    SeleniumElement element = new SeleniumElement(getBy(), getRawKey(), getElementName(), getPageName(), getContextElement(), locatedElement, index);
    element.setDriver(getWebDriver());
    element.setDeviceContext(getDeviceContext());
    element.subElementList.addAll(subElementList);
    if (elementProperties != null)
    {
      element.elementProperties = new HashMap<String, String>(5);
      element.elementProperties.putAll(elementProperties);
    }

    element.clonedElement = true;
    return element;
  }

  /**
   * Instantiates a new selenium element.
   *
   * @param by
   *          the by
   * 
   * @param elementKey
   *          the element key
   * @param fieldName
   *          the field name
   * @param pageName
   *          the page name
   * @param contextElement
   *          the context element
   */
  public SeleniumElement( BY by, String elementKey, String fieldName, String pageName, String contextElement )
  {
    super(by, elementKey, fieldName, pageName, contextElement);
  }

  /**
   * Instantiates a new selenium element.
   *
   * @param by
   *          the by
   * @param elementKey
   *          the element key
   * @param fieldName
   *          the field name
   * @param pageName
   *          the page name
   * @param contextElement
   *          the context element
   * @param locatedElement
   *          the located element
   * @param index
   *          the index
   */
  private SeleniumElement( BY by, String elementKey, String fieldName, String pageName, String contextElement, WebElement locatedElement, int index )
  {
    super(by, elementKey, fieldName, pageName, contextElement);
    this.locatedElement = locatedElement;
    this.index = index;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_getImage(com.
   * morelandLabs.integrations.perfectoMobile.rest.services.Imaging. Resolution)
   */
  @Override
  public Image _getImage( Resolution imageResolution )
  {
    WebElement imageElement = getElement();

    if (imageElement != null)
    {
      if (imageElement.getLocation() != null && imageElement.getSize() != null && imageElement.getSize().getWidth() > 0 && imageElement.getSize().getHeight() > 0)
      {

        byte[] imageData = null;
        if (getWebDriver() instanceof TakesScreenshot)
        {
          try
          {
            imageData = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
          }
          catch (Exception e)
          {
            log.error("Error taking screenshot", e);
          }
        }
        if (imageData != null && imageData.length > 0)
        {
          try
          {
            BufferedImage fullImage = ImageIO.read(new ByteArrayInputStream(imageData));
            CloudActionProvider aP = getWebDriver().getCloud().getCloudActionProvider();

            Point imageLocation = aP.translatePoint(getWebDriver(), imageElement.getLocation());
            Dimension imageSize = aP.translateDimension(getWebDriver(), imageElement.getSize());

            return fullImage.getSubimage(imageLocation.getX(), imageLocation.getY(), imageSize.getWidth(), imageSize.getHeight());
          }
          catch (Exception e)
          {
            log.error(Thread.currentThread().getName() + ": Error extracting image data", e);
          }
        }
        else
          log.warn(Thread.currentThread().getName() + ": No image data could be retrieved");

      }
      else
        log.warn(Thread.currentThread().getName() + ": The element returned via " + getKey() + " did not contain a location or size");
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.Element#setDriver(java.lang.Object)
   */
  @Override
  public void setDriver( Object webDriver )
  {
    this.setWebDriver((DeviceWebDriver) webDriver);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return getClass().getSimpleName() + " - " + getBy() + " {" + getRawKey() + "}";
  }

  /**
   * Gets the element.
   *
   * @param elementName
   *          the element name
   * @return the element
   */
  private Element getElement( String elementName )
  {

    ElementDescriptor elementDescriptor = new ElementDescriptor(PageManager.instance(getWebDriver().getxFID()).getSiteName(), getPageName(), elementName);

    if (log.isDebugEnabled())
      log.debug(Thread.currentThread().getName() + ": Attempting to locate element using [" + elementDescriptor.toString() + "]");

    Element myElement = PageManager.instance(getWebDriver().getxFID()).getElementProvider().getElement(elementDescriptor);
    myElement.setDriver(getWebDriver());
    myElement = myElement.cloneElement();
    PageManager.instance(getWebDriver().getxFID()).getElementProvider().setCachedElement(myElement, elementDescriptor);
    myElement.setExecutionContext(getExecutionContext());
    return myElement;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.Element#getCount()
   */
  @Override
  public int getCount()
  {
    if (count == -1)
      count = _getAll().length;
    return count;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.Element#getIndex()
   */
  @Override
  public int getIndex()
  {
    return index;
  }

  /**
   * Use by.
   *
   * @return the by
   */
  private ReportableBy useBy()
  {
    if (getDeviceContext() != null && !getDeviceContext().trim().isEmpty())
    {
      if (getWebDriver() instanceof VisualDriverProvider)
        useVisualDriver = true;
      else if (getWebDriver() instanceof ContextAware)
        ((ContextAware) getWebDriver()).context(getDeviceContext());
    }
    else if (getBy().getContext() != null)
    {
      if (getWebDriver() instanceof VisualDriverProvider)
        useVisualDriver = true;
      else if (getWebDriver() instanceof ContextAware)
        ((ContextAware) getWebDriver()).context(getBy().getContext());
    }

    switch (getBy())
    {
    case COMPLEX:
      if (subElementList != null && subElementList.size() > 0)
      {
        SubElement[] subList = getSubElement(((DeviceWebDriver) getWebDriver()).getAut(), ((DeviceWebDriver) getWebDriver()).getPopulatedDevice().getOs().toLowerCase(), (DeviceWebDriver) getWebDriver());
        if (subList.length == 1)
        {
          //
          // Single element found
          //
          ReportableBy foundBy = _useBy(subList[0].getBy(), applyToken(subList[0].getKey()));

          if (foundBy == null)
            throw new ScriptConfigurationException("Could not locate sub-element type for " + getName());

          return foundBy;
        }
        else if (subList.length > 1)
        {
          //
          // Support for lookups returning at multiple levels
          //
          List<By> byList = new ArrayList<By>(subList.length);

          for (SubElement s : subList)
          {
            By foundBy = _useBy(s.getBy(), applyToken(s.getKey()));

            if (foundBy == null)
              throw new ScriptConfigurationException("Could not locate sub-element type for " + getName());

            byList.add(foundBy);
          }

          return new ByCollection(byList.toArray(new By[0]));
        }
        else
        {
          throw new ScriptConfigurationException("Could not locate sub-element for " + getName() + "( " + ((DeviceWebDriver) getWebDriver()).getPopulatedDevice().getOs() + "," + ((DeviceWebDriver) getWebDriver()).getCloud().getProvider() + ", " + ((DeviceWebDriver) getWebDriver()).getAut().getName() + " (" + ((DeviceWebDriver) getWebDriver()).getAut().getVersion() + ") )");
        }
      }
      else
      {
        throw new ScriptConfigurationException("No sub-elements for " + getName());
      }
    default:
      return _useBy(getBy(), getKey());

    }
  }

  private ByWrapper _useBy( BY byType, String keyValue )
  {
    switch (byType)
    {
    case CLASS:
      return new ByWrapper(By.className(keyValue));

    case CSS:
      return new ByWrapper(By.cssSelector(keyValue));

    case ID:
      return new ByWrapper(By.id(keyValue));

    case LINK_TEXT:
      return new ByWrapper(By.linkText(keyValue));

    case NAME:
      return new ByWrapper(By.name(keyValue));

    case TAG_NAME:
      return new ByWrapper(By.tagName(keyValue));

    case XPATH:

      if (getWebDriver().getPopulatedDevice().getOs() != null && getWebDriver().getPopulatedDevice().getOs().toUpperCase().equals("IOS"))
      {
        if ((getWebDriver().getPopulatedDevice().getOsVersion() != null && getWebDriver().getPopulatedDevice().getOsVersion().startsWith("11")) || (getWebDriver().getCapabilities().getCapability(DriverFactory.AUTOMATION_NAME) != null && getWebDriver().getCapabilities().getCapability(DriverFactory.AUTOMATION_NAME).equals("XCUITest")))
        {
          if (keyValue.contains("UIA"))
          {
            log.warn("UI Autopmation XPATH detected - replacing UIA Code in " + keyValue);
            keyValue = XPathGenerator.XCUIConvert(keyValue);
          }
        }
      }

      return new ByWrapper(By.xpath(keyValue));

    case NATURAL:
      return new ByWrapper(new ByNaturalLanguage(keyValue, getWebDriver()));

    case V_TEXT:
      return new ByWrapper(new ByOCR(keyValue, getElementProperties(), getWebDriver()));

    case V_IMAGE:
      return new ByWrapper(new ByImage(keyValue, getElementProperties(), getWebDriver()));

    case HTML:
      HTMLElementLookup elementLookup = new HTMLElementLookup(keyValue);
      return new ByWrapper(By.xpath(elementLookup.toXPath()));

    case PROP:
      Map<String, String> propertyMap = new HashMap<String, String>(10);
      propertyMap.put("resource-id", ((DeviceWebDriver) getWebDriver()).getAut().getAndroidIdentifier());
      return new ByWrapper(By.xpath(XPathGenerator.generateXPathFromProperty(propertyMap, keyValue)));
    default:
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_moveTo()
   */

  private PointOption createPoint( WebElement webElement )
  {
    int x = webElement.getLocation().getX() + (webElement.getSize().getWidth() / 2);
    int y = webElement.getLocation().getX() + (webElement.getSize().getWidth() / 2);

    return PointOption.point(x, y);
  }

  public boolean _moveTo()
  {
    WebElement webElement = getElement();
    if (webElement != null && webElement.getSize().getHeight() > 0 && webElement.getSize().getWidth() > 0)
    {
      if (getWebDriver() instanceof HasInputDevices)
      {
        if (isTimed())
          getActionProvider().startTimer((DeviceWebDriver) getWebDriver(), this, getExecutionContext());

        if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof AppiumDriver)
        {
          new TouchAction((AppiumDriver) ((DeviceWebDriver) getWebDriver()).getNativeDriver()).moveTo(createPoint(webElement)).perform();
        }
        else if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof RemoteWebDriver)
        {
          if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof HasTouchScreen)
            new TouchActions(getWebDriver()).moveToElement(webElement).build().perform();
          else
            new Actions(getWebDriver()).moveToElement(webElement).build().perform();
        }

        return true;
      }
    }

    return false;
  }

  private CloudActionProvider getActionProvider()
  {
    return ((DeviceWebDriver) getWebDriver()).getCloud().getCloudActionProvider();
  }

  public boolean _clickFor( int length )
  {
    WebElement webElement = getElement();
    if (webElement != null && webElement.getSize().getHeight() > 0 && webElement.getSize().getWidth() > 0)
    {
      if (getWebDriver() instanceof HasInputDevices)
      {
        if (isTimed())
          getActionProvider().startTimer((DeviceWebDriver) getWebDriver(), this, getExecutionContext());

        if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof AppiumDriver)
        {
          new TouchAction((AppiumDriver<?>) ((DeviceWebDriver) getWebDriver()).getNativeDriver()).press(createPoint(webElement)).waitAction(WaitOptions.waitOptions(Duration.ofMillis(length))).release().perform();
        }
        else if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof RemoteWebDriver)
        {
          CloudActionProvider aP = getWebDriver().getCloud().getCloudActionProvider();

          Point clickLocation = aP.translatePoint(getWebDriver(), webElement.getLocation());
          Dimension clickSize = aP.translateDimension(getWebDriver(), webElement.getSize());
          Dimension windowSize = aP.translateDimension(getWebDriver(), getWebDriver().manage().window().getSize());

          double x = clickLocation.getX() + (clickSize.getWidth() / 2);
          double y = clickLocation.getY() + (clickSize.getHeight() / 2);

          int percentX = (int) (x / windowSize.getWidth() * 100.0);
          int percentY = (int) (y / windowSize.getHeight() * 100.0);

          try
          {
            new TouchActions(getWebDriver()).longPress(webElement).build().perform();
          }
          catch (Exception e)
          {
            ((DeviceWebDriver) getWebDriver()).getCloud().getCloudActionProvider().tap((DeviceWebDriver) getWebDriver(), new PercentagePoint(percentX, percentY, true), length);
          }

        }

        return true;
      }
    }

    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_press()
   */
  public boolean _press()
  {
    WebElement webElement = getElement();
    if (webElement != null && webElement.getSize().getHeight() > 0 && webElement.getSize().getWidth() > 0)
    {
      if (getWebDriver() instanceof HasInputDevices)
      {
        if (isTimed())
          getActionProvider().startTimer((DeviceWebDriver) getWebDriver(), this, getExecutionContext());

        if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof AppiumDriver)
        {
          new TouchAction((AppiumDriver) ((DeviceWebDriver) getWebDriver()).getNativeDriver()).press(createPoint(webElement)).perform();
        }
        else if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof RemoteWebDriver)
        {
          if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof HasTouchScreen)
            new TouchActions(getWebDriver()).clickAndHold(webElement).build().perform();
          else
            new Actions(getWebDriver()).clickAndHold(webElement).build().perform();
        }

        return true;
      }
    }

    return false;
  }

  public boolean _clickAt( int offsetX, int offsetY )
  {
    WebElement webElement = getElement();

    if (webElement != null)
    {

      CloudActionProvider aP = getWebDriver().getCloud().getCloudActionProvider();
      Dimension elementSize = aP.translateDimension(getWebDriver(), webElement.getSize());

      int useX = (int) ((double) elementSize.getWidth() * ((double) offsetX / 100.0) + webElement.getLocation().getX());
      int useY = (int) ((double) elementSize.getHeight() * ((double) offsetY / 100.0) + webElement.getLocation().getY());

      if (log.isInfoEnabled())
        log.info("Clicking " + useX + "," + useY + " pixels relative to " + getName());

      if (getWebDriver() instanceof HasInputDevices)
      {
        if (isTimed())
          getActionProvider().startTimer((DeviceWebDriver) getWebDriver(), this, getExecutionContext());

        aP.tap(getWebDriver(), new PercentagePoint(useX, useY, false), 500);

        return true;
      }

      return true;
    }

    return false;

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_mouseDoubleClick()
   */
  public boolean _mouseDoubleClick()
  {
    WebElement webElement = getElement();
    if (webElement != null && webElement.getSize().getHeight() > 0 && webElement.getSize().getWidth() > 0)
    {
      if (getWebDriver() instanceof HasInputDevices)
      {
        if (isTimed())
          getActionProvider().startTimer((DeviceWebDriver) getWebDriver(), this, getExecutionContext());

        if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof AppiumDriver)
        {
          new TouchActions((AppiumDriver) ((DeviceWebDriver) getWebDriver()).getNativeDriver()).doubleTap(webElement).perform();
        }
        else if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof RemoteWebDriver)
        {
          if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof HasTouchScreen)
            new TouchActions(getWebDriver()).doubleClick().build().perform();
          else
            new Actions(getWebDriver()).doubleClick(webElement).build().perform();
        }

        return true;
      }
    }

    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_release()
   */
  public boolean _release()
  {
    WebElement webElement = getElement();
    if (webElement != null && webElement.getSize().getHeight() > 0 && webElement.getSize().getWidth() > 0)
    {
      if (getWebDriver() instanceof HasInputDevices)
      {
        if (isTimed())
          getActionProvider().startTimer((DeviceWebDriver) getWebDriver(), this, getExecutionContext());

        if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof AppiumDriver)
        {
          new TouchAction((AppiumDriver) ((DeviceWebDriver) getWebDriver()).getNativeDriver()).release().perform();
        }
        else if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof RemoteWebDriver)
        {
          if (((DeviceWebDriver) getWebDriver()).getNativeDriver() instanceof HasTouchScreen)
            new TouchActions(getWebDriver()).release().build().perform();
          else
            new Actions(getWebDriver()).release().build().perform();
        }
        return true;
      }
    }

    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_getAll()
   */
  @Override
  protected Element[] _getAll()
  {
    ReportableBy useBy = useBy();
    if (useBy != null)
    {
      Element fromContext = getContext();
      if (fromContext == null && getContextElement() != null && !getContextElement().isEmpty())
        fromContext = getElement(getContextElement());

      if (log.isInfoEnabled())
        log.info(Thread.currentThread().getName() + ": Locating element by [" + getBy() + "] using [" + getKey() + "]");

      List<WebElement> webElements = null;

      if (fromContext != null)
        webElements = ((WebElement) fromContext.getNative()).findElements((By) useBy());
      else
        webElements = ((WebDriver) getWebDriver()).findElements((By) useBy());

      getExecutionContext().getStep().setLocatorResult(useBy.getResults());

      if (log.isInfoEnabled())
      {
        if (webElements == null)
          log.info(Thread.currentThread().getName() + ": Could not locate by [" + getBy() + "] using [" + getKey() + "]");
        else
          log.info(webElements.size() + "Elements Located using " + getKey());
      }

      Element[] foundElements = new Element[webElements.size()];

      for (int i = 0; i < webElements.size(); i++)
      {
        foundElements[i] = new SeleniumElement(getBy(), getKey() + "[" + (i + 1) + "]", getElementName(), getPageName(), getContextElement(), webElements.get(i), i);
        foundElements[i].setDriver(getWebDriver());
      }

      return foundElements;
    }
    else
      return null;
  }

  public int getModifiedX( int currentX )
  {
    return (int) (currentX * getWebDriver().getWidthModifier());
  }

  public int getModifiedY( int currentY )
  {
    return (int) (currentY * getWebDriver().getHeightModifier());
  }

  /**
   * Gets the element.
   *
   * @return the element
   */
  private WebElement getElement()
  {

    if (locatedElement != null)
    {
      if (log.isDebugEnabled())
        log.debug(Thread.currentThread().getName() + ": Element " + getKey() + " Read from cache");
      return locatedElement;
    }

    String currentContext = null;
    if (getWebDriver() instanceof ContextAware)
      currentContext = ((ContextAware) getWebDriver()).getContext();

    Element fromContext = getContext();
    if (fromContext == null && getContextElement() != null && !getContextElement().isEmpty())
      fromContext = getElement(getContextElement());

    ReportableBy useBy = null;

    try
    {
      useBy = useBy();
      if (useBy != null)
      {

        WebElement webElement = null;

        if (useVisualDriver && "VISUAL".equals(getBy().getContext()))
        {
          webElement = ((VisualDriverProvider) getWebDriver()).getVisualDriver().findElement((By) useBy);
        }
        else
        {
          List<WebElement> elementList = null;

          if (fromContext != null)
            elementList = ((WebElement) fromContext.getNative()).findElements((By) useBy);
          else
            elementList = ((WebDriver) getWebDriver()).findElements((By) useBy);

          if (elementList == null || elementList.size() == 0)
            throw new ScriptException("Could not locate elements using " + useBy.toString());

          if (elementList.size() == 1)
            webElement = elementList.get(0);
          else if (elementList.size() > 1 && isAllowMultiple())
            webElement = elementList.get(0);
          else if (elementList.size() > 1 && !isAllowMultiple())
            throw new ScriptConfigurationException(getKey() + " returned multiple values while 1 was expected");

        }

        if (log.isDebugEnabled())
        {
          if (webElement == null)
            log.debug(Thread.currentThread().getName() + ": Could not locate by [" + getBy() + "] using [" + getKey() + "]");
          else
            log.debug(Thread.currentThread().getName() + ": Element " + getKey() + " Located");
        }

        if (isCacheNative())
          locatedElement = webElement;
        return webElement;
      }

      return null;
    }
    finally
    {
      try
      {
        if (useBy != null)
          getExecutionContext().getStep().setLocatorResult(useBy.getResults());
      }
      catch (Exception e)
      {

      }

      if (currentContext != null && getWebDriver() instanceof ContextAware)
        ((ContextAware) getWebDriver()).context(currentContext);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_getNative()
   */
  @Override
  protected Object _getNative()
  {
    return getElement();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_getStyle(java.lang.
   * String)
   */
  protected String _getStyle( String styleProperty )
  {
    return getElement().getCssValue(styleProperty);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_getValue()
   */
  @Override
  protected String _getValue()
  {
    WebElement currentElement = getElement();

    if (currentElement.getTagName() == null)
      return null;

    String returnValue = null;
    switch (currentElement.getTagName().toUpperCase())
    {
    case "IMG":
      returnValue = currentElement.getAttribute("src");
      break;

    case "INPUT":
      returnValue = currentElement.getAttribute("value");
      break;

    case "SELECT":
      returnValue = new Select(currentElement).getFirstSelectedOption().getText();
      break;

    case "UIATEXTFIELD":
      returnValue = currentElement.getAttribute("value");
      break;

    case "ANDROID.WIDGET.EDITTEXT":
      returnValue = currentElement.getAttribute("text");
      break;

    default:
      returnValue = currentElement.getText();
      break;
    }

    return returnValue;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_getAttribute(java.lang.
   * String)
   */
  @Override
  protected String _getAttribute( String attributeName )
  {
    if (log.isDebugEnabled())
      log.debug("Getting Attribute value for " + attributeName);
    return getElement().getAttribute(attributeName);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_isVisible()
   */
  @Override
  protected boolean _isVisible()
  {
    WebElement webElement = (WebElement) getElement();

    if (webElement == null)
      throw new ScriptException("Element was found but not visible");

    boolean returnValue = webElement.isDisplayed();
    if (returnValue)
      getActionProvider().getSupportedTimers((DeviceWebDriver) getWebDriver(), getExecutionContext().getTimerName(), getExecutionContext(), null);
    return returnValue;
  }

  @Override
  protected boolean _isFocused()
  {
    WebElement webElement = (WebElement) getElement();

    boolean returnValue = false;

    if (getNativeDriver() instanceof AppiumDriver)
    {
      String focusValue = getAttribute("focused");

      if (focusValue != null)
        returnValue = Boolean.parseBoolean(focusValue);
    }
    else if (getNativeDriver() instanceof RemoteWebDriver)
      returnValue = webElement.equals(getWebDriver().switchTo().activeElement());

    if (returnValue)
      getActionProvider().getSupportedTimers((DeviceWebDriver) getWebDriver(), getExecutionContext().getTimerName(), getExecutionContext(), null);
    return returnValue;
  }

  @Override
  protected boolean _isEnabled()
  {
    WebElement webElement = (WebElement) getElement();

    if (webElement == null)
      throw new ScriptException("Element was found but not enabled");

    boolean returnValue = webElement.isEnabled();
    if (returnValue)
      getActionProvider().getSupportedTimers((DeviceWebDriver) getWebDriver(), getExecutionContext().getTimerName(), getExecutionContext(), null);

    return returnValue;

  }

  private WebDriver getNativeDriver()
  {

    if (getWebDriver() instanceof NativeDriverProvider)
      return ((NativeDriverProvider) getWebDriver()).getNativeDriver();
    else
      return getWebDriver();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_isPresent()
   */
  @Override
  protected boolean _isPresent()
  {
    WebElement webElement = getElement();
    if (webElement != null)
    {
      getActionProvider().getSupportedTimers((DeviceWebDriver) getWebDriver(), getExecutionContext().getTimerName(), getExecutionContext(), null);
    }
    return webElement != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_waitForPresent(long,
   * java.util.concurrent.TimeUnit)
   */
  @Override
  protected boolean _waitFor( long timeOut, TimeUnit timeUnit, WAIT_FOR waitType, String value )
  {
    long implicitWait = getWebDriver().getImplicitWait();

    if (implicitWait > (timeOut * 1000))
      getWebDriver().manage().timeouts().implicitlyWait(timeOut, TimeUnit.SECONDS);

    try
    {
      ReportableBy useBy = useBy();

      try
      {

        String currentContext = null;
        if (getWebDriver() instanceof ContextAware)
          currentContext = ((ContextAware) getWebDriver()).getContext();

        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeOut, 250);

        WebElement webElement = null;
        boolean returnValue = false;

        switch (waitType)
        {
        case CLICKABLE:
          webElement = wait.until(new Function<WebDriver, WebElement>()
          {

            @Override
            public WebElement apply( WebDriver webDriver )
            {
              return ExpectedConditions.elementToBeClickable((By) useBy).apply(webDriver);
            }

          });

          break;

        case INVISIBLE:
          returnValue = wait.until(new Function<WebDriver, Boolean>()
          {

            @Override
            public Boolean apply( WebDriver webDriver )
            {
              return ExpectedConditions.invisibilityOfElementLocated((By) useBy).apply(webDriver);
            }

          });
          break;

        case PRESENT:
          webElement = wait.until(new Function<WebDriver, WebElement>()
          {

            @Override
            public WebElement apply( WebDriver webDriver )
            {
              return ExpectedConditions.presenceOfElementLocated((By) useBy).apply(webDriver);
            }

          });

          break;

        case SELECTABLE:
          returnValue = wait.until(new Function<WebDriver, Boolean>()
          {

            @Override
            public Boolean apply( WebDriver webDriver )
            {
              return ExpectedConditions.elementToBeSelected((By) useBy).apply(webDriver);
            }

          });
          break;

        case TEXT_VALUE_PRESENT:
          returnValue = wait.until(new Function<WebDriver, Boolean>()
          {

            @Override
            public Boolean apply( WebDriver webDriver )
            {
              return ExpectedConditions.textToBePresentInElementValue((By) useBy, value).apply(webDriver);
            }

          });
          break;

        case VISIBLE:
          webElement = wait.until(new Function<WebDriver, WebElement>()
          {

            @Override
            public WebElement apply( WebDriver webDriver )
            {
              return ExpectedConditions.visibilityOfElementLocated((By) useBy).apply(webDriver);
            }

          });
          break;

        case FRAME:
          webElement = wait.until(new Function<WebDriver, WebElement>()
          {

            @Override
            public WebElement apply( WebDriver webDriver )
            {
              try
              {
                WebElement webElement = webDriver.findElement((By) useBy);
                if (webElement instanceof MorelandWebElement)
                  webDriver.switchTo().frame(((MorelandWebElement) webElement).getWebElement());
                else
                  webDriver.switchTo().frame(webElement);
                return webElement;
              }
              catch (Exception e)
              {

                return null;
              }
            }

          });
          break;

        case CLICKABLE_THEN_CLICK:
          webElement = wait.until(new Function<WebDriver, WebElement>()
          {

            @Override
            public WebElement apply( WebDriver webDriver )
            {
              return ExpectedConditions.elementToBeClickable((By) useBy).apply(webDriver);
            }

          });
          webElement.click();
          break;

        case ENABLED_THEN_SET:
          webElement = wait.until(new Function<WebDriver, WebElement>()
          {

            @Override
            public WebElement apply( WebDriver webDriver )
            {
              return ExpectedConditions.elementToBeClickable((By) useBy).apply(webDriver);
            }

          });

          SetMethodFactory.instance().createSetMethod(webElement.getTagName()).set(webElement, getWebDriver(), value, SetMethod.DEFAULT.name(), getExecutionContext().getxFID());
          break;

        default:
          throw new IllegalArgumentException("Unknown Wait Condition [" + waitType + "]");
        }

        if (currentContext != null && getWebDriver() instanceof ContextAware)
          ((ContextAware) getWebDriver()).context(currentContext);

        if (webElement != null || returnValue == true)
          getActionProvider().getSupportedTimers((DeviceWebDriver) getWebDriver(), getExecutionContext().getTimerName(), getExecutionContext(), null);

        return webElement != null || returnValue == true;
      }
      catch (Exception e)
      {
        throw new ObjectIdentificationException(getBy(), (By) useBy);
      }
      finally
      {
        getExecutionContext().getStep().setLocatorResult(useBy.getResults());
      }
    }
    finally
    {
      if (implicitWait > 0)
      {
        try
        {
          getWebDriver().manage().timeouts().implicitlyWait(implicitWait, TimeUnit.MILLISECONDS);
        }
        catch (Exception e)
        {

        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_setValue(java.lang.
   * String)
   */
  @Override
  protected void _setValue( String currentValue, SetMethod setMethod, String xFID )
  {
    boolean enableCache = false;
    if (getWebDriver() instanceof CachingDriver)
    {
      if (((CachingDriver) getWebDriver()).isCachingEnabled())
      {
        ((CachingDriver) getWebDriver()).setCachingEnabled(false);
        enableCache = true;
      }
    }

    try
    {
      WebElement webElement = getElement();

      if (!SetMethodFactory.instance().createSetMethod(webElement.getTagName()).set(webElement, getWebDriver(), currentValue, setMethod.name().toUpperCase(), xFID))
        throw new ScriptException("Could not set " + getName() + " with " + currentValue);
    }
    finally
    {
      if (enableCache)
        ((CachingDriver) getWebDriver()).setCachingEnabled(true);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.AbstractElement#_click()
   */
  @Override
  protected void _click()
  {
    boolean enableCache = false;
    if (getWebDriver() instanceof CachingDriver)
    {
      if (((CachingDriver) getWebDriver()).isCachingEnabled())
      {
        ((CachingDriver) getWebDriver()).setCachingEnabled(false);
        enableCache = true;
      }
    }
    try
    {
      WebElement e = getElement();
      if (isTimed())
        getActionProvider().startTimer((DeviceWebDriver) getWebDriver(), this, getExecutionContext());
      e.click();
    }
    finally
    {
      if (enableCache)
        ((CachingDriver) getWebDriver()).setCachingEnabled(true);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.Element#getExecutionId()
   */
  public String getExecutionId()
  {
    return getWebDriver().getProperty(EXECUTION_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.perfectoMobile.page.element.Element#getDeviceName()
   */
  public String getDeviceName()
  {
    return getWebDriver().getProperty(DEVICE_NAME);
  }

  @Override
  protected Dimension _getSize()
  {
    WebElement webElement = getElement();
    return webElement.getSize();
  }

  @Override
  protected Point _getAt()
  {
    WebElement webElement = getElement();
    return webElement.getLocation();
  }

  @Override
  protected boolean _isSelected()
  {
    WebElement webElement = getElement();
    return webElement.isSelected();
  }
}
