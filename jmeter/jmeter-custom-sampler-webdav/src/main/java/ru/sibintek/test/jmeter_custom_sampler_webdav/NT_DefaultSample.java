package ru.sibintek.test.jmeter_custom_sampler_webdav;


import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Date;

import java.lang.Thread;

/**
 * general parent class for all scripts, extend it to add new script faster
 */
public abstract class NT_DefaultSample implements JavaSamplerClient {


    // params names
	protected static final String HOST_URL_PARAM              = "URL";
	protected static final String SCRIPT_NAME_PARAM           = "SCRIPT";
	protected static final String SUBFOLDER_PARAM        	  = "SUBFOLDER";
	protected static final String FILESIZE_PARAM        	  = "FILESIZE";
	protected static final String FILETYPE_PARAM        	  = "FILETYPE";
	protected static final String USER_NAME_PARAM             = "07_LOGIN";
	protected static final String USER_PASS_PARAM             = "07_PASSWORD";
	protected static final String MAINFOLDER_PARAM            = "07_MAIN_FOLDER";
	protected static final String PACING_IN_SEC               = "PACING_IN_SEC";
	protected static final String INFLUX_URL_PARAM            = "INFLUX_URL";
	protected static final String INFLUX_DATABASE_NAME_PARAM  = "INFLUX_DATABASE_NAME";
	protected static final String INFLUX_MEARUREMENT_NAME_PARAM = "INFLUX_MEARUREMENT_NAME";
	protected static final String APPLICATION_NAME_PARAM      = "APPLICATION_NAME";
	protected static final String COUNT_I_DOC_STORE_PARAM = "COUNT_I_DOC_STORE";
    // general fields
    protected String hostUrl;
    protected String userName;
    protected String userPass;

	
    protected String influxurl;
    protected String influxdatabase;
    protected String influxmeasurementName;
    protected String mainfolder;
    protected String subfolder;
    protected String filetype;
    protected int filesize;
    protected String applicationname;

    protected String count_i_doc_strore;
     
    
    /**
     * special method for custom operations if needed on setup
     * called after login
     * @param javaSamplerContext - input context from child
     */
    protected abstract void setupTestSpecial(JavaSamplerContext javaSamplerContext);

    /**
     * special method for custom operations if needed on runTime
     * @param javaSamplerContext - current user context
     * @return iteration result
     */
    protected abstract SampleResult runTestSpecial(JavaSamplerContext javaSamplerContext);

    /**
     * special method for custom operations if needed on teardown
     * called before logout
     */
    protected abstract void teardownTestSpecial(JavaSamplerContext javaSamplerContext);

    /**
     * get name of current script
     * @return current script name from child class
     */
    protected abstract String getScriptName();

    /**
     * get current logger from subclass
     * @return current logger object
     */
    

    /**
     * general method, called on startUp
     * use setupTestSpecial() method in subclass for custom operations
     * @param javaSamplerContext - current user context
     */
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        hostUrl = getStringParameter(javaSamplerContext, HOST_URL_PARAM);
       

        count_i_doc_strore = getStringParameter(javaSamplerContext, COUNT_I_DOC_STORE_PARAM);
        //influxurl = getStringParameter(javaSamplerContext, INFLUX_URL_PARAM);
        //influxdatabase = getStringParameter(javaSamplerContext, INFLUX_DATABASE_NAME_PARAM);
        //influxmeasurementName = getStringParameter(javaSamplerContext, INFLUX_MEARUREMENT_NAME_PARAM);
        
        	
        
        setupTestSpecial(javaSamplerContext);
    }

    /**
     * general runTime method
     * use runTestSpecial() method in subclass for custom operations
     * @param javaSamplerContext - current user context
     * @return iteration result
     */
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sampleResult = runTestSpecial(javaSamplerContext);
        
       // long idleTimeSec = sampleResult.getIdleTime();
        //long pacingTimeSec = Long.valueOf(getStringParameter(javaSamplerContext, PACING_IN_SEC));
       /* try {
            if(idleTimeSec < pacingTimeSec) {
                long pauseTimeInMillis = (pacingTimeSec - idleTimeSec) * 1000;
                //getLogger().logInfo("current pacing time: " + (pauseTimeInMillis/1000) + " sec");       // debug
                Thread.sleep(pauseTimeInMillis);
            }
        } catch (InterruptedException e) {
           
        }*/
        return sampleResult;
    }

    /**
     * general method, called at the end of test for current thread
     * use teardownTestSpecial() method for custom operations
     * @param javaSamplerContext - current user context
     */

    public void teardownTest(JavaSamplerContext javaSamplerContext) {
        teardownTestSpecial(javaSamplerContext);
        try {
          
        } catch (Exception e) {
            
        }
    }

    /**
     * set default arguments for current sample
     * @return settled arguments
     */

    public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument(COUNT_I_DOC_STORE_PARAM, "");
        //arguments.addArgument(SUBFOLDER_PARAM, "");
       // arguments.addArgument(FILETYPE_PARAM, "");
        //arguments.addArgument(FILESIZE_PARAM, "");
       // arguments.addArgument(APPLICATION_NAME_PARAM, "");
       

        
        return arguments;
    }



    /**
     * get current user name
     * @return login parameter
     */
    protected String getUserName() {
        return userName;
    }




    /**
     * get int parameter from current user context
     * @param javaSamplerContext - current context
     * @param paramName - name of parameter to get
     * @return int value if parameter exists, 0 - if doesn't
     */
    public static int getIntParameter(JavaSamplerContext javaSamplerContext, String paramName) {
        if (paramName == null || paramName.isEmpty()) {
            return 0;
        }
        String result = getStringParameter(javaSamplerContext, paramName);
        return Integer.valueOf((result == null || result.isEmpty()) ? "0" : result);
    }

    /**
     * get string parameter from current user context
     * @param javaSamplerContext - current context
     * @param paramName - name of parameter to get
     * @return string value if parameter exists, null - if doesn't
     */
    public static String getStringParameter(JavaSamplerContext javaSamplerContext, String paramName) {
        if (paramName == null || paramName.isEmpty()) {
            return null;
        }
        String paramValue = javaSamplerContext.getParameter(paramName);
        paramValue = (paramValue == null) ? javaSamplerContext.getJMeterVariables().get(paramName) : paramValue;
        return (paramValue == null) ? javaSamplerContext.getJMeterContext().getVariables().get(paramName) : paramValue;
    }
}