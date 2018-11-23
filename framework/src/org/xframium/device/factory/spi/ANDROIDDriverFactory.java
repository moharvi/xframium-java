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
/*
 * 
 */
package org.xframium.device.factory.spi;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.xframium.application.ApplicationRegistry;
import org.xframium.content.ContentManager;
import org.xframium.device.DeviceManager;
import org.xframium.device.cloud.CloudDescriptor;
import org.xframium.device.cloud.CloudDescriptor.ProviderType;
import org.xframium.device.factory.AbstractDriverFactory;
import org.xframium.device.factory.DeviceWebDriver;
import org.xframium.exception.DeviceConfigurationException;
import org.xframium.page.keyWord.KeyWordDriver;
import org.xframium.reporting.ExecutionContext;
import org.xframium.spi.Device;

import io.appium.java_client.android.AndroidDriver;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ANDROIDDriver objects.
 */
public class ANDROIDDriverFactory extends AbstractDriverFactory
{
	
	/* (non-Javadoc)
	 * @see com.perfectoMobile.device.factory.AbstractDriverFactory#_createDriver(com.perfectoMobile.device.Device)
	 */
	@Override
	protected DeviceWebDriver _createDriver( Device currentDevice, CloudDescriptor useCloud, String xFID, boolean swallowException )
	{
		DeviceWebDriver webDriver = null;
		try
		{
			DesiredCapabilities dc = new DesiredCapabilities( "", "", Platform.ANY );
			
            
            DeviceManager.instance( xFID ).setCurrentCloud( useCloud );
            

			if ( currentDevice.getDeviceName() != null && !currentDevice.getDeviceName().isEmpty() )
			{
				dc.setCapability( ID, currentDevice.getDeviceName() );
			}
			else
			{				
				if ( !isBlank( currentDevice.getOs() ) ) 
					dc.setCapability( useCloud.getCloudActionProvider().getCloudPlatformName(currentDevice), currentDevice.getOs() );
				
				if ( !isBlank( currentDevice.getOsVersion() ) )
					dc.setCapability( PLATFORM_VERSION, currentDevice.getOsVersion() );
				
				if ( !isBlank( currentDevice.getModel() ) )
					dc.setCapability( MODEL, currentDevice.getModel() );
			}
			
			useCloud.getCloudActionProvider().addAuthenticationCapabilities( useCloud, dc );
			
			
			addCapabilities( dc, currentDevice, ApplicationRegistry.instance( xFID ).getAUT() );
			
			dc.setCapability( AUTOMATION_NAME, "Appium" );
			
            if (( ContentManager.instance(xFID).getCurrentContentKey() != null ) &&
                ( ContentManager.instance(xFID).getContentValue( Device.LOCALE ) != null ))
            {
                String localeToConfigure = ContentManager.instance(xFID).getContentValue( Device.LOCALE );

                dc.setCapability( Device.LOCALE, localeToConfigure );
            }	
            
            if ( ProviderType.BROWSERSTACK.equals( ProviderType.valueOf( useCloud.getProvider() ) ) )
            {
                dc.setCapability( "project", ExecutionContext.instance(xFID).getSuiteName() );
                if ( KeyWordDriver.instance(xFID).getTags() != null && KeyWordDriver.instance(xFID).getTags().length > 0 )                	
                    dc.setCapability( "build", KeyWordDriver.instance(xFID).getTags()[ 0 ] );
                dc.setCapability( "name", currentDevice.getCapabilities().get( "_testName" ) );
            }
            
            if ( ProviderType.SAUCELABS.equals( ProviderType.valueOf( useCloud.getProvider() ) ) )
            {
                dc.setCapability( "testobject_suite_name", ExecutionContext.instance(xFID).getSuiteName() );
                if ( KeyWordDriver.instance(xFID).getTags() != null && KeyWordDriver.instance(xFID).getTags().length > 0 )                	
                    dc.setCapability( "build", KeyWordDriver.instance(xFID).getTags()[ 0 ] );
                dc.setCapability( "testobject_test_name", currentDevice.getCapabilities().get( "_testName" ) );
            }
			
            URL hubUrl = new URL( useCloud.getCloudUrl( dc ) );
            
            if ( log.isDebugEnabled() )
                log.debug( Thread.currentThread().getName() + ": Acquiring Device as: \r\n" + capabilitiesToString( dc ) + "\r\nagainst " + hubUrl );
			
			webDriver = new DeviceWebDriver( new AndroidDriver( hubUrl, dc ), DeviceManager.instance( xFID ).isCachingEnabled(), currentDevice, dc );
	
			webDriver.manage().timeouts().implicitlyWait( 10, TimeUnit.SECONDS );
			
			Capabilities caps = ( (AndroidDriver) webDriver.getWebDriver() ).getCapabilities();
			webDriver.setExecutionId( useCloud.getCloudActionProvider().getExecutionId( webDriver ) );
			webDriver.setReportKey( caps.getCapability( "reportKey" ) + "" );
			webDriver.setDeviceName( caps.getCapability( "deviceName" )+ "" );
			if ( useCloud.getProvider().equals( "PERFECTO" ) && caps.getCapability( "windTunnelReportUrl" ) != null )
                webDriver.setWindTunnelReport( caps.getCapability( "windTunnelReportUrl" ) + "" );
			webDriver.context( "NATIVE_APP" );
			
			if( ApplicationRegistry.instance(xFID).getAUT() != null && ApplicationRegistry.instance(xFID).getAUT().getAndroidIdentifier() != null && !ApplicationRegistry.instance(xFID).getAUT().getAndroidIdentifier().isEmpty() )
            {
			    if ( ApplicationRegistry.instance(xFID).getAUT().isAutoStart() && ( (AndroidDriver) webDriver.getNativeDriver() ).isAppInstalled( ApplicationRegistry.instance(xFID).getAUT().getAndroidIdentifier() ) )
                {
                    if ( !useCloud.getCloudActionProvider().openApplication( ApplicationRegistry.instance(xFID).getAUT().getName(), webDriver, xFID ) )
                        throw new DeviceConfigurationException( ApplicationRegistry.instance(xFID).getAUT().getAndroidIdentifier() );
                }
                else
                {
                    if ( ApplicationRegistry.instance(xFID).getAUT().isAutoStart() )
                    {
                        useCloud.getCloudActionProvider().installApplication( ApplicationRegistry.instance(xFID).getAUT().getName(), webDriver, false, false, true );
                        if ( !useCloud.getCloudActionProvider().openApplication( ApplicationRegistry.instance(xFID).getAUT().getName(), webDriver, xFID ) )
                            throw new DeviceConfigurationException( ApplicationRegistry.instance(xFID).getAUT().getAndroidIdentifier() );
                    }
                }
			    
			    webDriver.setAut( ApplicationRegistry.instance(xFID).getAUT(), xFID );
			    
			    String interruptString = ApplicationRegistry.instance(xFID).getAUT().getCapabilities().get( "deviceInterrupts" )  != null ? (String)ApplicationRegistry.instance(xFID).getAUT().getCapabilities().get( "deviceInterrupts" ) : DeviceManager.instance( xFID ).getDeviceInterrupts();
	            webDriver.setDeviceInterrupts( getDeviceInterrupts( interruptString, webDriver.getExecutionId(), webDriver.getDeviceName() ) );
            }
			
			webDriver.setCloud( useCloud );
			return webDriver;
		}
	
		catch( Exception e )
		{
		    log.fatal( "Could not connect to " + currentDevice + " (" + e.getMessage() + ")" );
            log.debug( e );
            if ( !swallowException )
            	throw new IllegalStateException( "Could not connect to " + currentDevice + " (" + e.getMessage() + ")", e );
			if ( webDriver != null )
			{
				try { webDriver.close(); } catch( Exception e2 ) {}
				try { webDriver.quit(); } catch( Exception e2 ) {}
			}
			return null;
		}
	}
}
