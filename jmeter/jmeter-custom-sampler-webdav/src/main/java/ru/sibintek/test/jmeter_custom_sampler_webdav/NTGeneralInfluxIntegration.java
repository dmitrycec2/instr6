package ru.sibintek.test.jmeter_custom_sampler_webdav;

/*
 * In measurements, you must escape:
 * commas
 * spaces
 *
 * In tag keys, tag values, and field keys, you must escape:
 * commas
 * equal signs
 * spaces
 *
 * In string field values, you must escape:
 * double quotes
 * backslash character
 */

import org.apache.commons.net.ntp.TimeStamp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NTGeneralInfluxIntegration {

    private static final long serialVersionUID = 99937323455676572L;

    private static final int MINIMUM_TIMESTAMP_LENGTH = 10;
    private static final int INFLUX_TIMESTAMP_LENGTH = 19;

    // default tags names
    private static final String TAG_NAME_NODE = "node";
    private static final String TAG_NAME_APPLICATION = "application";
    private static final String TAG_NAME_TRANSACTION = "transaction";
    private static final String TAG_NAME_STATUS = "statut";
    private static final String TAG_NAME_THREAD = "thread";
    

    // default field names
    private static final String FIELD_NAME_VALUE = "avg";
    private static final String FIELD_NAME_MAXAT = "maxAT";

    // status
    private static final String STATUS_PASS = "ok";
    private static final String STATUS_FAIL = "fail";

    // default main parameters - necessary
    private String hostName = "http://loacalhost";
    private String dataBaseName = "metricsDB";
    private String measurementName = "jmeter";
    private long timeStampValue = -1;

    // tags map
    private static final Map<String,String> influxTags = new HashMap<>();
    static {
        influxTags.put(TAG_NAME_NODE, "default_node");
        influxTags.put(TAG_NAME_APPLICATION, "jmeter");
        influxTags.put(TAG_NAME_TRANSACTION, "empty");
        influxTags.put(TAG_NAME_STATUS, STATUS_FAIL);
        influxTags.put(TAG_NAME_THREAD, "empty");
    }

    // fields map
    private static final Map<String,String> influxFields = new HashMap<>();
    static {
        influxFields.put(FIELD_NAME_VALUE, "0");
    }


    /*
     * constructors
     * ==============================================
     */

    // default constructor
    @Deprecated
    public NTGeneralInfluxIntegration() {
    }

    // constructor with main parameters
    public NTGeneralInfluxIntegration(String hostName, String dataBaseName, String measurementName) {
        this.hostName = getValidHost(hostName);
        this.dataBaseName = getValidParam(dataBaseName);
        this.measurementName = getValidParam(measurementName);
    }


    /*
     * custom setters - optional
     * ==============================================
     */
    public void setCustomTag(String tagName, String tagValue) {
        influxTags.put(getValidParam(tagName), getValidParam(tagValue));
    }

    public void setCustomField(String fieldName, String fieldValue) {
        influxFields.put(getValidParam(fieldName), getValidParam(fieldValue));
    }


    /*
     * timeStamp setters - optional
     * ==============================================
     * influx documentation:
     * YYYY-MM-DD HH:MM:SS.nnnnnnnnn (RFC3339 format), 1677-09-21 00:12:43.145224194 - example
     * HH:MM:SS.nnnnnnnnn is optional and is set to 00:00:00.000000000 if not included.
     * 1587074400000000000 - timestamp in nanosec (19 symbols)
     * return true if valid
     */
    // milliseconds. 1463683075 - timestamp in sec (also possible)
    public boolean setTimeStamp(long timeInMillis) {
        int timeStampLength = String.valueOf(timeInMillis).length();
        if (timeInMillis > 0 &&
                timeStampLength >= MINIMUM_TIMESTAMP_LENGTH &&
                timeStampLength <= INFLUX_TIMESTAMP_LENGTH) {
            this.timeStampValue = getInfluxTimeStamp(timeInMillis);
            return true;
        }
        return false;
    }

    // standard Calender class can be used to form timestamp
    public boolean setTimeStamp(Calendar dateTime) {
        if (dateTime != null &&
                dateTime.isSet(Calendar.HOUR) &&
                dateTime.isSet(Calendar.MINUTE) &&
                dateTime.isSet(Calendar.SECOND) &&
                dateTime.isSet(Calendar.YEAR) &&
                dateTime.isSet(Calendar.MONTH) &&
                (dateTime.isSet(Calendar.DAY_OF_MONTH) ||
                        dateTime.isSet(Calendar.DATE) ||
                        dateTime.isSet(Calendar.DAY_OF_YEAR))) {
            this.timeStampValue = getInfluxTimeStamp(dateTime.getTimeInMillis());
            return true;
        }
        return false;
    }


    /*
     * default parameters setters - necessary
     * ==============================================
     */
    public void setNodeName(String nodeName) {
        influxTags.put(TAG_NAME_NODE, getValidParam(nodeName));
    }

    public void setApplicationName(String applicationName) {
        influxTags.put(TAG_NAME_APPLICATION, getValidParam(applicationName));
    }

    public void setTransactionName(String transactionName) {
        influxTags.put(TAG_NAME_TRANSACTION, getValidParam(transactionName));
    }

    public void setStatus(boolean isPassed) {
        influxTags.put(TAG_NAME_STATUS, isPassed ? STATUS_PASS : STATUS_FAIL);
    }

    public void setThreadName(String threadName) {
        influxTags.put(TAG_NAME_THREAD, getValidParam(threadName));
    }

    public void setTransactionTime(long timeInMillis) {
        influxFields.put(FIELD_NAME_VALUE, String.valueOf(timeInMillis >= 0 ? timeInMillis : 0));
    }
    public void setMaxAT(int maxAT) {
    	influxFields.put(FIELD_NAME_MAXAT, String.valueOf(maxAT >= 0 ? maxAT : 0));
    }

    /*
     * check values
     * ==============================================
     */
    public boolean isValid() {
        if (hostName.isEmpty() || dataBaseName.isEmpty() || measurementName.isEmpty())
            return false;
        return isMapValid(influxFields) && isMapValid(influxTags);
    }


    /*
     * send info into influx
     * ==============================================
     */
    public void sendInfoToInflux() throws Exception {
        int responseCode;
        String influxURL = hostName + "/write?db=" + dataBaseName;
        String requestBody = getInfluxBodyRequest();
        if (!isValid()) {
            throw new Exception("Influx write data ERROR: some params are invalid!" +
                    " || URL: " + influxURL +
                    " || requestBody: " + requestBody);
        }
        try {
            HttpURLConnection influxPost = (HttpURLConnection) new URL(influxURL).openConnection();
            influxPost.setRequestMethod("POST");
            influxPost.setDoOutput(true);
            influxPost.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            influxPost.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));
            System.out.println(requestBody);
            responseCode = influxPost.getResponseCode();
        } catch (IOException ioEx) {
            throw new Exception("Influx write data ERROR: " + ioEx.toString() +
                    " || URL: " + influxURL +
                    " || requestBody: " + requestBody);
        }
        if (responseCode != 204) {
            throw new Exception("Influx write data ERROR: invalid response!" +
                    " || response code: " + responseCode +
                    " || URL: " + influxURL +
                    " || requestBody: " + requestBody);
        }
    }


    /*
     * request body example (influx line protocol syntax):
     * <measurement>[,<tag_key>=<tag_value>[,<tag_key>=<tag_value>]] <field_key>=<field_value>[,<field_key>=<field_value>] [<timestamp>]
     * ==============================================
     */
    private String getInfluxBodyRequest() {
        StringBuffer requestStringBuff = new StringBuffer(measurementName);
        influxTags.forEach((tagName,tagValue) -> requestStringBuff
                .append(",").append(tagName).append("=").append(tagValue));
        requestStringBuff.append(" ");
        for (Map.Entry<String, String> entry : influxFields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if (' ' == requestStringBuff.charAt(requestStringBuff.length() - 1)) {
                requestStringBuff.append(fieldName).append("=").append(fieldValue);
                continue;
            }
            requestStringBuff.append(",").append(fieldName).append("=").append(fieldValue);
        }
        if (timeStampValue > 0)
            requestStringBuff.append(" ").append(timeStampValue);
        return requestStringBuff.toString();
    }


    /*
     * help methods
     * ==============================================
     */

    // remove invalid host symbols (like "/" or "\")
    private String getValidHost(String hostName) {
        if (hostName == null || hostName.isEmpty())
             return "";
        hostName = hostName.trim();
        int lastElemIndex = hostName.length() - 1;
        if ("/".equals(hostName.substring(lastElemIndex)) || "\\".equals(hostName.substring(lastElemIndex))) {
            hostName = getValidHost(hostName.substring(0, lastElemIndex));
        }
        return hostName;
    }

    // add quotes to tag value
    @Deprecated
    private String getInfluxTagValue(String value) {
        return "\"" + value + "\"";
    }

    // avoid nulls and extra spaces in parameters
    private String getValidParam(String value) {
        return (value == null || value.isEmpty()) ? "" : value.trim().replace(' ', '_');
    }

    // check any map for empty values
    private boolean isMapValid(Map<String,String> checkMap) {
        if (checkMap.isEmpty())
            return false;
        for (String oneValue : checkMap.values()) {
            if (oneValue.isEmpty())
                return false;
        }
        return true;
    }

    // making valid timeStamp (for influx)
    private long getInfluxTimeStamp(long time) {
        StringBuffer stringBufferTime = new StringBuffer(String.valueOf(time));
        while (stringBufferTime.length() < INFLUX_TIMESTAMP_LENGTH) {
            stringBufferTime.append('0');
        }
        return Long.parseLong(stringBufferTime.toString());
    }
}
