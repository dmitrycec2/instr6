package ru.sibintek.test.jmeter_custom_sampler_webdav;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContext.TestLogicalAction;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import main.Main_Parse;


public class App extends NT_DefaultSample {
	File file;
	byte[] data;
    Date transactionTimeStart;
    Date transactionTimeEnd;
    
	public void main() {
		try {
			uploadFile(null, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("1234");
	}
	

	@Override
	protected void setupTestSpecial(JavaSamplerContext javaSamplerContext) {


			//filesize = getIntParameter(javaSamplerContext, FILESIZE_PARAM);
			//file = createSampleFile(filesize);
			//data = FileUtils.readFileToByteArray(file);

	}

	@Override
	protected SampleResult runTestSpecial(JavaSamplerContext javaSamplerContext) {
		SampleResult sampleResult = new SampleResult();

       /* userName = getStringParameter(javaSamplerContext, USER_NAME_PARAM);
        userPass = getStringParameter(javaSamplerContext, USER_PASS_PARAM);
        mainfolder=getStringParameter(javaSamplerContext, MAINFOLDER_PARAM);
        subfolder=getStringParameter(javaSamplerContext, SUBFOLDER_PARAM);
        filetype=getStringParameter(javaSamplerContext, FILETYPE_PARAM);
        applicationname=getStringParameter(javaSamplerContext, APPLICATION_NAME_PARAM); */
        
		sampleResult.sampleStart();
		try {

        	//uploadFile(sampleResult,javaSamplerContext);
		    uploadFile2(sampleResult,javaSamplerContext);
		    
        	sampleResult.setResponseMessage("ok");
            sampleResult.setSuccessful(true);
        } catch (Exception e) {
            // nop
        } finally {
            sampleResult.sampleEnd();
        }
		

		return sampleResult;
	}

	@Override
	protected void teardownTestSpecial(JavaSamplerContext javaSamplerContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getScriptName() {
		// TODO Auto-generated method stub
		return null;
	}
   
	
	
	
	
	
    public void uploadFile2(SampleResult sampleResult, JavaSamplerContext javaSamplerContext) 
    {
    	transactionTimeStart=new Date();
       //to do 
    	try {
			List<String> fileNames = Main_Parse.main(getIntParameter(javaSamplerContext, COUNT_I_DOC_STORE_PARAM ));
			for(String s:fileNames) {
				System.out.println(s);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	transactionTimeEnd=new Date();
    	SampleResult subResult = sampleResult.createTestSample(getTimeDiff(transactionTimeStart,transactionTimeEnd));
		//subResult.setSampleLabel("mysample");
		subResult.setSuccessful(true);
		subResult.setResponseMessage("ok");
		subResult.setParent(sampleResult);
		
		sampleResult.addRawSubResult(subResult);
		subResult.setSampleLabel("mysample");
		
		//System.out.print("111111111111111111"+subResult.getSampleLabel());
		//sampleResult.addSubResult(subResult);
		
    }
    
    
    
    public void uploadFile(SampleResult sampleResult, JavaSamplerContext javaSamplerContext) 
    {
    	
    	//URI url = URI.create("http://172.30.48.73:8080/webdav/DavWWWRoot/NT27-Nov-2019%201139351/medium");
    	/*hostUrl="http://172.30.48.73:8080";
    	mainfolder="NT27-Nov-2019 1139351";
    	subfolder="medium";*/
    	
    	String currentTransaction=null;
    	String mainfolder_urlenc=mainfolder.replace(" ","%20");
    	URI url = null;
    	try {
    		
    		
			currentTransaction = "_UC07-02_WebDavOpen_"+subfolder;
			transactionTimeStart=new Date();
			url = URI.create(hostUrl+"/webdav/DavWWWRoot/"+mainfolder_urlenc);
			    Sardine sardine = SardineFactory.begin(userName, userPass); 	       
			        List<DavResource> resources;					
							resources = sardine.list(url.toString(),3);
			transactionTimeEnd=new Date();
			sendInfoToInflux(sampleResult,javaSamplerContext,currentTransaction,true,getTimeDiff(transactionTimeStart,transactionTimeEnd));
			
    		
			currentTransaction = "_UC07-02_FileResourceTarget_"+subfolder;
			transactionTimeStart=new Date();
			url = URI.create(hostUrl+"/webdav/DavWWWRoot/"+mainfolder_urlenc+"/"+subfolder);
							resources = sardine.list(url.toString(),3);
			transactionTimeEnd=new Date();
			sendInfoToInflux(sampleResult,javaSamplerContext,currentTransaction,true,getTimeDiff(transactionTimeStart,transactionTimeEnd));
        	


	            final UUID filename = UUID.randomUUID();  
	            Random rand = new Random(); 
	            String filepath=resources.get(rand.nextInt(resources.size())).getPath();                   
	            Pattern pattern = Pattern.compile("/webdav/DavWWWRoot/"+mainfolder+"/"+subfolder+"(.*)/"); 
	            Matcher m = pattern.matcher(filepath); 
	            while (m.find()) 
	            	filepath=m.group(); 
	       
	            
	            String fullpath = hostUrl+filepath+filename+"."+filetype;
	           // String fullpath = "http://172.30.48.73:8080/webdav/DavWWWRoot/NT27-Nov-2019%201139351/medium"+filename+".pdf";
	            //System.out.println("url"+url);
	            //System.out.println("fullpath"+fullpath);  
	            fullpath=fullpath.replace(" ","%20");
	            System.out.println("fullpath"+fullpath); 
            
            currentTransaction = "_UC07-04_FileUpload_"+subfolder;         
			transactionTimeStart=new Date();
					            sardine.put(fullpath, data);
			transactionTimeEnd=new Date();
			sendInfoToInflux(sampleResult,javaSamplerContext,currentTransaction,true,getTimeDiff(transactionTimeStart,transactionTimeEnd));       	
            
            
     
			} catch (Exception e) {
				// TODO Auto-generated catch block
				sendInfoToInflux(sampleResult,javaSamplerContext,currentTransaction,false,1000);
				sampleResult.setResponseMessage(e.getMessage());
				e.printStackTrace();
			}
    }
    
    
    private File createSampleFile(int filesize) throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for(int i=0;i<filesize;i++)
        writer.write("a");
        writer.close();

        return file;
    }
    
    public static int getIntParameter(JavaSamplerContext javaSamplerContext, String paramName) {
        if (paramName == null || paramName.isEmpty()) {
            return 0;
        }
        String result = getStringParameter(javaSamplerContext, paramName);
        return Integer.valueOf((result == null || result.isEmpty()) ? "0" : result);
    }
    
    public static String getStringParameter(JavaSamplerContext javaSamplerContext, String paramName) {
        if (paramName == null || paramName.isEmpty()) {
            return null;
        }
        String paramValue = javaSamplerContext.getParameter(paramName);
        paramValue = (paramValue == null) ? javaSamplerContext.getJMeterVariables().get(paramName) : paramValue;
        return (paramValue == null) ? javaSamplerContext.getJMeterContext().getVariables().get(paramName) : paramValue;
    }


    void sendInfoToInflux(SampleResult sampleResult, JavaSamplerContext javaSamplerContext, String transactionName, Boolean transactionResult, long transactionTime) {
    	NTGeneralInfluxIntegration  ntGeneralinfluxIntegration = new NTGeneralInfluxIntegration(influxurl,influxdatabase,influxmeasurementName);
		ntGeneralinfluxIntegration.setNodeName("default");
		ntGeneralinfluxIntegration.setApplicationName(applicationname);
		ntGeneralinfluxIntegration.setTransactionName(transactionName);
		ntGeneralinfluxIntegration.setStatus(transactionResult);
		ntGeneralinfluxIntegration.setThreadName(Thread.currentThread().getName());
		ntGeneralinfluxIntegration.setTransactionTime(transactionTime);
		ntGeneralinfluxIntegration.setMaxAT(javaSamplerContext.getJMeterContext().getThreadGroup().getNumberOfThreads());
	
		
		
		try {
			ntGeneralinfluxIntegration.sendInfoToInflux();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static long getTimeDiff(Date date_first, Date date_last) {
        long diff = date_last.getTime() - date_first.getTime();
        return diff;
    }

}
