package org.xframium.reporting;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import org.openqa.selenium.Capabilities;
import org.xframium.application.ApplicationDescriptor;
import org.xframium.device.ConnectedDevice;
import org.xframium.device.cloud.CloudDescriptor;
import org.xframium.exception.XFramiumException;
import org.xframium.exception.XFramiumException.ExceptionType;
import org.xframium.page.Page;
import org.xframium.page.StepStatus;
import org.xframium.page.data.PageData;
import org.xframium.page.keyWord.KeyWordParameter;
import org.xframium.page.keyWord.KeyWordStep;
import org.xframium.page.keyWord.KeyWordTest;
import org.xframium.page.keyWord.step.SyntheticStep;
import org.xframium.spi.Device;

public class ExecutionContextTest
{
    public enum TestStatus
    {
        PASSED,
        FAILED,
        SKIPPED;
    }

    private boolean inStepListener = false;
    private String xFID = null;
    private ExceptionType exceptionType;
    private KeyWordTest test;
    private String testName;
    private CloudDescriptor cloudDescriptor;
    private Device device;
    private Date startTime = new Date( System.currentTimeMillis() );
    private Date endTime;
    private List<ExecutionContextStep> stepList = new ArrayList<ExecutionContextStep>( 10 ); 
    private ExecutionContextStep currentStep;
    private Throwable testException; 
    private TestStatus testStatus;
    private String sessionId;
    private Map<String,String> executionParameters = new HashMap<String,String>( 20 );
    private Map<String,PageData> dataMap = null;
    private Map<String,Page> pageMap = null;
    private Map<String,Object> contextMap = null;
    private String message;
    private String messageDetail;
    private String folderName;
    private Map<String,String> sPMap = new HashMap<String,String>( 10 );
    private ApplicationDescriptor aut;
    private StringBuilder csvOutput = new StringBuilder();
    private Map<String,?> c = null;
    private Map<String,?> dC = null;
    private KeyWordStep failedStep = null;
    private transient Stack<String> deviceStack = new Stack<String>();
    
    private Map<String,ConnectedDevice> deviceMap = new HashMap<String,ConnectedDevice>( 5 );
    
    private String timerName;
    
    private Map<String,Integer[]> callMap = new HashMap<String,Integer[]>( 10 );
    private Map<String,Integer[]> pageUsageMap = new HashMap<String,Integer[]>( 10 );
    private Map<String,ElementUsage> elementUsageMap = new TreeMap<String,ElementUsage>( );
    private List<ElementUsage> elementUsage = new ArrayList<ElementUsage>( 10 );
    
    private int iterationCount = 0;

    private List<Integer> featureList = new ArrayList<Integer>( 50 );
    
    public void addFeature( int featureId )
    {
      featureList.add( featureId );
    }
    
    public String getFeatures()
    {
      StringBuilder s = new StringBuilder();
      
      for ( int i : featureList )
        s.append( i ).append( "," );
      
      String returnValue = s.toString();
      
      if  ( returnValue.length() > 1 )
        returnValue = returnValue.substring( 0, returnValue.length() - 1 );
      
      return returnValue;
    }
    
    public boolean isInStepListener()
    {
        return inStepListener;
    }

    public void setInStepListener(boolean inStepListener)
    {
        this.inStepListener = inStepListener;
    }

    public int getIterationCount()
    {
        return iterationCount;
    }

    public void setIterationCount( int iterationCount )
    {
        this.iterationCount = iterationCount;
    }

    private class FailureComparator implements Comparator<ElementUsage>
    {

        @Override
        public int compare( ElementUsage o1, ElementUsage o2 )
        {
            return Integer.compare( o2.getFailCount(), o1.getFailCount() );
        }
    }
    
    public String peekAtDevice()
    {
        try
        {
            return deviceStack.peek();
        }
        catch( Exception e )
        {
            return null;
        }
    }
    
    public String popDevice()
    {
        try
        {
            return deviceStack.pop();
        }
        catch( Exception e )
        {
            return null;
        }
    }
    
    public void pushDevice( String deviceName )
    {
        deviceStack.push( deviceName );
    }
    
    public String getxFID()
    {
        return xFID;
    }

    public void setxFID( String xFID )
    {
        this.xFID = xFID;
    }

    public Map<String, ConnectedDevice> getDeviceMap()
    {
        return deviceMap;
    }

    public void setDeviceMap( Map<String, ConnectedDevice> deviceMap )
    {
        this.deviceMap = deviceMap;
    }

    public KeyWordStep getFailedStep()
    {
        return failedStep;
    }

    public void setFailedStep( KeyWordStep failedStep )
    {
        this.failedStep = failedStep;
    }

    public void addToCSV( String line )
    {
        csvOutput.append( line ).append( "\r\n" );
    }
    
    public void addToCSV( String[] line )
    {
        for ( int i=0; i<line.length; i++ )
        {
            csvOutput.append( line[i] );
            if ( i < csvOutput.length() - 1 )
                csvOutput.append( "," );
        }
        
        csvOutput.append( "\r\n" );
    }
    
    public String getCSVReport()
    {
        return csvOutput.toString();
    }


    public void setDesiredCapabilities( Capabilities c )
    {
        if ( c != null )
            this.c = c.asMap();
    }
    

    public void setDerivedCapabilities( Capabilities c )
    {
        if ( c != null )
            this.dC = c.asMap();
    }

    public Map<String,Object> toMap()
    {
        Map<String,Object> asMap = new HashMap<String,Object>( 20 );
        
        asMap.put( "exceptionType", exceptionType );
        asMap.put( "name", testName );
        asMap.put( "startTime", startTime );
        asMap.put( "endTime", endTime );
        asMap.put( "testStatus", testStatus );
        asMap.put( "passed", getStepCount( StepStatus.SUCCESS ) );
        asMap.put( "failed", getStepCount( StepStatus.FAILURE ) );
        asMap.put( "ignored", getStepCount( StepStatus.FAILURE_IGNORED ) );
        asMap.put( "total", getStepCount( null ) );
        asMap.put( "cloud", cloudDescriptor );
        asMap.put( "device", device );
        asMap.put( "sessionId", sessionId );
        asMap.put( "folderName", folderName );
        if ( test != null )
            asMap.put( "tagNames", test.getTags() );
        callMap.clear();
        analyzeCalls( callMap );
        
        pageUsageMap.clear();
        elementUsageMap.clear();
        analyzePageElements( pageUsageMap, elementUsageMap );
        
        asMap.put( "callMap", callMap );
        asMap.put( "pageUsageMap", pageUsageMap );
        
        elementUsage.clear();
        elementUsage.addAll( elementUsageMap.values() );
        Collections.sort( elementUsage, new FailureComparator() );
        asMap.put( "elementUsage", elementUsage );
        asMap.put( "elementUsageMap", elementUsageMap );

        
        return asMap;
    }
    
    
    
    public String getMessageDetail()
    {
        if ( messageDetail == null )
        {
            if ( getStepException() != null )
            {
                message = getStepException().getMessage();
                
                StringWriter sW = new StringWriter();
                getStepException().printStackTrace( new PrintWriter( sW ) );
                messageDetail = sW.toString();
            }
        }
        return messageDetail;
    }

    public void setMessageDetail( String messageDetail )
    {
        this.messageDetail = messageDetail;
    }

    private long timerStart;
    
    public String getTimerName()
    {
        return timerName;
    }
    
    public void setTimerName( String timerName )
    {
        this.timerName = timerName;
        timerStart = System.currentTimeMillis();
    }
    
    public long getTimerStart()
    {
        return timerStart;
    }
    
    public void clearTimer()
    {
        this.timerName = null;
    }
    
    public String getTestName()
    {
        return testName;
    }



    public void setTestName( String testName )
    {
        this.testName = testName;
    }



    public void popupateSystemProperties()
    {
        sPMap.clear();
        Properties sP = System.getProperties();
        for ( Object key : sP.keySet() )
        {
            sPMap.put( (String) key, sP.getProperty( (String)key ) ); 
        }
    }

    public ApplicationDescriptor getAut()
    {
        return aut;
    }

    public void setAut( ApplicationDescriptor aut )
    {
        this.aut = aut;
    }

    public String getFolderName()
    {
        return folderName;
    }

    public void setFolderName( String folderName )
    {
        this.folderName = folderName;
    }

    public void addExecutionParameter( String name, String value )
    {
        executionParameters.put( name,  value );
    }
    
    public String getElementParameter( String name )
    {
        return executionParameters.get( name );
    }
    
    public ExecutionContextStep getStep()
    {
        return currentStep;
    }
    
    public Map<String, PageData> getDataMap()
    {
        return dataMap;
    }

    

    public void setDataMap( Map<String, PageData> dataMap )
    {
        this.dataMap = dataMap;
    }



    public Map<String, Page> getPageMap()
    {
        return pageMap;
    }



    public void setPageMap( Map<String, Page> pageMap )
    {
        this.pageMap = pageMap;
    }



    public Map<String, Object> getContextMap()
    {
        return contextMap;
    }



    public void setContextMap( Map<String, Object> contextMap )
    {
        this.contextMap = contextMap;
    }



    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId( String sessionId )
    {
        this.sessionId = sessionId;
    }

    public Device getDevice()
    {
        return device;
    }

    public void setDevice( Device device )
    {
        this.device = device;
    }

    public Throwable getTestException()
    {
        return testException;
    }

    public void setTestException( Throwable testException )
    {
        this.testException = testException;
    }

    public TestStatus getTestStatus()
    {
        return testStatus;
    }

    public void setTestStatus( TestStatus testStatus )
    {
        this.testStatus = testStatus;
    }

    public CloudDescriptor getCloud()
    {
        return cloudDescriptor;
    }

    public void setCloud( CloudDescriptor cloudDescriptor )
    {
        this.cloudDescriptor = cloudDescriptor;
    }

    public KeyWordTest getTest()
    {
        return test;
    }

    public void setTest( KeyWordTest test )
    {
        this.test = test;
    }
    
    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime( long startTime )
    {
        this.startTime = new Date( startTime );
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime( long endTime )
    {
        this.endTime = new Date( endTime );
    }

    public List<ExecutionContextStep> getStepList()
    {
        return stepList;
    }

    public void setStepList( List<ExecutionContextStep> stepList )
    {
        this.stepList = stepList;
    }

    public void startStep( KeyWordStep step, Map<String, Object> contextMap, Map<String, PageData> dataMap )
    {
        if ( currentStep == null )
        {
            currentStep = new ExecutionContextStep( null );
            currentStep.setStep( step );
            stepList.add( currentStep );
        }
        else
        {
            ExecutionContextStep subStep = new ExecutionContextStep( currentStep );
            subStep.setStep( step );
            currentStep.getStepList().add( subStep );
            currentStep = subStep;
        }
        
        for ( KeyWordParameter p : step.getParameterList() )
        {
            currentStep.addParameterValue( step.getParameterValue( p, contextMap, dataMap, getxFID() ) + "" );
        }
    }

    
    public String getMessage()
    {
        if ( message == null )
        {
            if ( getStepException() != null )
            {
                message = getStepException().getMessage();
                
                StringWriter sW = new StringWriter();
                getStepException().printStackTrace( new PrintWriter( sW ) );
                messageDetail = sW.toString();
            }
        }
        
        return message;
    }



    public void setMessage( String message )
    {
        this.message = message;
    }

    public int getStepCount( StepStatus stepStatus )
    {
        int stepCount = 0;
        
        for ( ExecutionContextStep s : stepList )
            stepCount += s.getStepCount( stepStatus );
        
        return stepCount;
    }
    
    public void analyzePageElements( Map<String,Integer[]> pageMap, Map<String,ElementUsage> elementMap )
    {
        for ( ExecutionContextStep s : stepList )
        {
            if ( s.getStep().isOrMapping() )
            {
                String keyName = s.getStep().getSiteName() + "." + s.getStep().getPageName();
                Integer[] passFail = pageMap.get( keyName );
                if ( passFail == null )
                {
                    passFail = new Integer[] { 0, 0 };
                    pageMap.put( keyName, passFail );
                }
                
                if ( s != null && s.getStepStatus() != null )
                {
                    if ( s.getStepStatus().equals( StepStatus.SUCCESS ) )
                        passFail[ 0 ]++;
                    else if ( s.getStepStatus().equals( StepStatus.FAILURE ) )
                        passFail[ 1 ]++;
                }
                
                keyName = s.getStep().getSiteName() + "." + s.getStep().getPageName() + "." + s.getStep().getName();
                ElementUsage eU = elementMap.get( keyName );
                if ( eU == null )
                {
                    eU = new ElementUsage( s.getStep().getSiteName(), s.getStep().getPageName(), s.getStep().getName() );
                    elementMap.put( keyName, eU );
                }
                
                if ( s != null && s.getStepStatus() != null )
                {
                    if ( s.getStepStatus().equals( StepStatus.SUCCESS ) )
                        eU.setPassCount( eU.getPassCount() + 1 );
                    else if ( s.getStepStatus().equals( StepStatus.FAILURE ) )
                        eU.setFailCount( eU.getFailCount() + 1 );
                }
            }
            
            s.analyzePageElements( pageMap, elementMap );
        }
    }
    
    public void analyzeCalls( Map<String,Integer[]> callMap )
    {
        for ( ExecutionContextStep s : stepList )
        {
            if ( "CALL".equals( s.getStep().getKw() ) || "CALL2".equals( s.getStep().getKw() ) )
            {
                Integer[] passFail = callMap.get( s.getStep().getName() );
                if ( passFail == null )
                {
                    passFail = new Integer[] { 0, 0 };
                    callMap.put( s.getStep().getName(), passFail );
                }
                
                if ( s.getStepStatus().equals( StepStatus.SUCCESS ) )
                    passFail[ 0 ]++;
                else
                    passFail[ 1 ]++;
            }
            
            s.analyzeCalls( callMap );
        }
    }

    public boolean completeStep( StepStatus stepStatus, Throwable throwable, String customMessage )
    {
        if ( currentStep == null )
            return false;
        currentStep.setEndTime( System.currentTimeMillis() );
        currentStep.setStepStatus( stepStatus );
        currentStep.setThrowable( throwable );
        currentStep.setCustomMessage(customMessage);
        if ( currentStep.getParentStep() != null )
            currentStep = currentStep.getParentStep();
        else
            currentStep = null;
        
        return true;
    }
    
    public void completeTest( TestStatus testStatus, Throwable testException )
    {
        if ( stepList == null || stepList.size() == 0 )
        {
            startStep( new SyntheticStep( test.getName(), "TEST" ), contextMap, dataMap );
            completeStep( StepStatus.FAILURE, testException, null );
        }
        
        
        endTime = new Date( System.currentTimeMillis() );
        this.testStatus = testStatus;
        if ( testException != null )
            message = testException.getMessage();
        this.testException = testException;
        if ( testStatus.equals( TestStatus.FAILED ) )
            exceptionType = _getExceptionType();
        
    }
    
    public boolean getStatus()
    {
        for ( ExecutionContextStep eS : stepList )
        {
            if ( !eS.getStatus() )
                return false;
        }
        
        return true;
    }
    
    public ExceptionType getExceptionType()
    {
        return exceptionType;
    }
    
    private ExceptionType _getExceptionType()
    {
        for ( ExecutionContextStep eS : stepList )
        {
            if ( !eS.getStatus() )
                return eS.getExceptionType();
        }
        
        if ( testException != null && testException instanceof XFramiumException )
            return ( (XFramiumException) testException ).getType();
        
        return null;
    }
    
    public String getScreenShotLocation()
    {

        for ( ExecutionContextStep eS : stepList )
        {
            String screenShot = eS.getScreenShotLocation();
            if ( screenShot != null )
                return screenShot;
        }
        
        return null;
    }
    
    public Throwable getStepException()
    {
        for ( ExecutionContextStep eS : stepList )
        {
            if ( !eS.getStatus() )
                return eS.getStepException();
        }
        
        return testException;
    }

    public Map<String, String> getExecutionParameters()
    {
        return executionParameters;
    }
    
    public String getExecutionParameter( String name )
    {
        return executionParameters.get( name );
    }
    
    
}
