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
package org.xframium.page.keyWord.step.spi;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import javax.imageio.ImageIO;
import org.openqa.selenium.WebDriver;
import org.xframium.container.SuiteContainer;
import org.xframium.device.factory.DeviceWebDriver;
import org.xframium.exception.ScriptConfigurationException;
import org.xframium.exception.ScriptException;
import org.xframium.imaging.ImagingActionType;
import org.xframium.integrations.perfectoMobile.rest.services.Imaging.Resolution;
import org.xframium.page.Page;
import org.xframium.page.data.PageData;
import org.xframium.page.keyWord.step.AbstractKeyWordStep;
import org.xframium.reporting.ExecutionContext;
import org.xframium.reporting.ExecutionContextTest;

// TODO: Auto-generated Javadoc
/**
 * <b>Keyword(s):</b> <code>VERIFY_CONTRAST</code><br>
 * The verify contrast is used to locate an element on the screen and perform a Lumenosity analysis on it.  The Lumenosity calculate
 * is derived from the Imaging 2.0 standard.  The returned value will be between 0 and 21.  Please refer to the Imaging 2.0 specification for 
 * a definition of the values  Note that PageManager can be configured to dump these images to disk 
 * for analysis purposes.<br><br>

 * <b>Parameters:</b> <br>
 * <ul>
 * <li>Resolution: high, med or low.  The specifies the screenshot resolution that will be used.</li>
 * <li>Minimum Contrast:  The minimum amount of contrast that must be present</li>
 * <li>Maximum Contrast:  The maximum amount of contrast that must be present</li>
 * </ul>

 * <br><b>Example(s): </b><ul>
 * <li> This example will find the LOGO element and analyze the contrast ratio to ensure that it meets AAA standards<br>
 * {@literal <step name="LOGO" type="VERIFY_CONTRAST" page="TEST_PAGE"> }<br>
 * &nbsp;&nbsp;&nbsp;{@literal  <parameter type="static" value="high" /> }<br>
 * &nbsp;&nbsp;&nbsp;{@literal  <parameter type="static" value="7.0" /> }<br>
 * &nbsp;&nbsp;&nbsp;{@literal  <parameter type="static" value="21.1" /> }<br>
 * {@literal </step> }
 * </li>
 * </ul>
 */
public class KWSContrastRatio extends AbstractKeyWordStep
{
    public KWSContrastRatio()
    {
        kwName = "Luminosity validation";
        kwDescription = "Allows thte script to analyze the named element using the Imaging 2.0 algorithm and validates its success against the Imaging 2.0 success criteria";
        kwHelp = "https://www.xframium.org/keyword.html#kw-checkcontrast";
        category = "Verification";
        featureId = 18;
    }
	/* (non-Javadoc)
	 * @see com.perfectoMobile.page.keyWord.step.AbstractKeyWordStep#_executeStep(com.perfectoMobile.page.Page, org.openqa.selenium.WebDriver, java.util.Map, java.util.Map)
	 */
	@Override
	public boolean _executeStep( Page pageObject, WebDriver webDriver, Map<String, Object> contextMap, Map<String, PageData> dataMap, Map<String, Page> pageMap, SuiteContainer sC, ExecutionContextTest executionContext )
	{
		if (getParameterList().size() < 2 )
			throw new ScriptConfigurationException( "Verify Color must have 3 parameters" );
		
		long fileKey = System.currentTimeMillis();
		Resolution resolution = Resolution.valueOf( ( getParameterValue( getParameterList().get( 0 ), contextMap, dataMap, executionContext.getxFID() ) + "" ).toLowerCase() );
		double minContrast = Double.parseDouble( getParameterValue( getParameterList().get( 1 ), contextMap, dataMap, executionContext.getxFID() ) + "" );
		double maxContrast = Double.parseDouble( getParameterValue( getParameterList().get( 2 ), contextMap, dataMap, executionContext.getxFID() ) + "" );
		
		
		BufferedImage elementValue = (BufferedImage)getElement( pageObject, contextMap, webDriver, dataMap, executionContext ).getImage( resolution );
		String fileName = fileKey + "-" + getName() + ".png";
        File imageFile = new File ( new File( ExecutionContext.instance(( (DeviceWebDriver) webDriver ).getxFID()).getReportFolder(( (DeviceWebDriver) webDriver ).getxFID()), "artifacts" ), fileName );
        if ( elementValue != null )
        {
            try
            {
                imageFile.getParentFile().mkdirs();
                ImageIO.write( elementValue, "png", imageFile );
            }
            catch( Exception e )
            {
                
            }
        }

		
		int minColor = 0;
		double minLumens = 255;
		int maxColor = 0;
		double maxLumens = 0;
		double luminosity = 0;
		for ( int x=0; x<elementValue.getWidth(); x++ )
		{
			for ( int y=0; y<elementValue.getHeight(); y++ )
			{
				int[] colorData = extractColors( elementValue.getRGB( x, y ) );
				luminosity = calculateLuminance( colorData );
				if ( luminosity > maxLumens )
				{
					maxLumens = luminosity;
					maxColor = elementValue.getRGB( x, y );
				}
				else if ( luminosity < minLumens )
				{
					minLumens = luminosity;
					minColor = elementValue.getRGB( x, y );
				}
				
			}
		}
		
		double contrastRatio = (maxLumens + 0.05) / (minLumens + 0.05 );
		if ( contrastRatio < minContrast || contrastRatio > maxContrast )
		{
		    String contrastMessage = "The contrast for [" + getName() + "] should be between [#" + Integer.toHexString( minColor ) + "] and [" + Integer.toHexString( maxColor ) + "] was [" + contrastRatio + "] and fell outside of the expected range";
		    
		   
		    executionContext.getStep().addExecutionParameter( "ImagingStatus", "false" );
            executionContext.getStep().addExecutionParameter( "ImagingType", ImagingActionType.ContrastVerification.name() );
            executionContext.getStep().addExecutionParameter( "ImagingImage", "../../artifacts/" + fileName  );
            executionContext.getStep().addExecutionParameter( "ImagingImage_ABS", imageFile.getAbsolutePath()  );
            executionContext.getStep().addExecutionParameter( "ImagingContrastRange", minContrast + "-" + maxContrast );
            executionContext.getStep().addExecutionParameter( "ImagingContrast", contrastRatio + "" );
            executionContext.getStep().addExecutionParameter( "ImagingError", contrastMessage );
			throw new ScriptException( contrastMessage );
		}
		else
		{
		    executionContext.getStep().addExecutionParameter( "ImagingStatus", "true" );
            executionContext.getStep().addExecutionParameter( "ImagingType", ImagingActionType.ContrastVerification.name() );
            executionContext.getStep().addExecutionParameter( "ImagingImage", "../../artifacts/" + fileName  );
            executionContext.getStep().addExecutionParameter( "ImagingImage_ABS", imageFile.getAbsolutePath()  );
            executionContext.getStep().addExecutionParameter( "ImagingContrastRange", minContrast + "-" + maxContrast );
            executionContext.getStep().addExecutionParameter( "ImagingContrast", contrastRatio + "" );
		}
		
		if ( getContext() != null )
		{
			if ( log.isInfoEnabled() )
				log.info( "Setting Context Data to [" + contrastRatio + "] for [" + getContext() + "]" );
			addContext( getContext(), contrastRatio + "", contextMap, executionContext );
		}
		
		return true;
	}

	/**
	 * Calculate luminance.
	 *
	 * @param rgb the rgb
	 * @return the double
	 */
	private static double calculateLuminance( int[] rgb )
	{
		double[] dRGB = new double[3];
		
		for ( int i=0; i<3; i++ )
		{		
			dRGB[ i ] = rgb[ i ] / 255.0;
			
			if ( dRGB[ i ] <= 0.03928 )
				dRGB[i] = dRGB[ i ] / 12.92;
			else
				dRGB[ i ] = Math.pow( ( ( dRGB[ i ] + 0.055 ) / 1.055 ) , 2.4 );
		}
		
		return ( dRGB[ 0 ] * 0.2126 ) + (dRGB[ 1 ] * 0.7152) + ( dRGB[2] * 0.0722 );

	}
	
	/**
	 * Extract colors.
	 *
	 * @param colorData the color data
	 * @return the int[]
	 */
	public int[] extractColors( int colorData )
	{
		return new int[] { (int) ( (colorData & 0x00ff0000) >> 16 ), (int) ( (colorData & 0x0000ff00) >> 8 ), (int) ( (colorData & 0x000000ff) )} ;
	}

}
