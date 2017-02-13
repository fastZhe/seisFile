
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import groovy.text.SimpleTemplateEngine

class FDSNQueryParamGenerator {
    def engine = new SimpleTemplateEngine()

    String createItem(key, doc, service) {
        String t = ""
        if (key in dateTypes) {t="Date"}
        else if(key in floatTypes) {t="float"}
        else if (key in intTypes) {t="int"}
        else if (key in booleanTypes) {t="boolean"}
        String setter = 'set'
        if (key in addTypes) { setter = 'appendTo' }
        String locidSpaceCheck = ""
        if (key in locIdTypes) {locidSpaceCheck = 'if (value == null || Channel.EMPTY_LOC_CODE.equals(value.trim())) { value = "--";}\n        ' }
        def binding = ['key':key, 'doc':doc, 'type':t, 'service':service, 'setter':setter, 'locidSpaceCheck':locidSpaceCheck]
        return engine.createTemplate(templateText).make(binding)
    }

    String createPre(service) {
        def binding = ['service':service]
        return engine.createTemplate(preTemplate).make(binding)
    }

    String createPost(service) {
        def ws = service.toLowerCase()
        def binding = ['service':service, 'ws':ws, 'extra':extraPostCode[service]]
        return engine.createTemplate(postTemplate).make(binding)
    }

    def preTemplate = '''
package edu.sc.seis.seisFile.fdsnws;

import java.util.Date;

import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.ChannelTimeWindow;

/** Autogenerated by groovy FDSNQueryParamGenerator.groovy in src/metacode/groovy
 */
public class FDSN${service.capitalize()}QueryParams extends AbstractQueryParams implements Cloneable {

    public FDSN${service}QueryParams() {
        this(${service=='Event'?'USGS_HOST':'DEFAULT_HOST'});
    }
    
    public FDSN${service}QueryParams(String host) {
        super(host==null ? ${service=='Event'?'USGS_HOST':'DEFAULT_HOST'} : host);
    }

    public FDSN${service}QueryParams clone() {
        FDSN${service}QueryParams out = new FDSN${service}QueryParams(getHost());
        out.cloneNonParams(this);
        for (String key : params.keySet()) {
            out.setParam(key, params.get(key));
        }
        return out;
    }

    public FDSN${service}QueryParams setHost(String host) {
        this.host = host;
        return this;
    }
'''

    def postTemplate = '''
    ${extra}

}
'''

    def templateText = '''
    public static final String ${key.toUpperCase()} = "${key.toLowerCase()}";

    /** $doc
     */
    public FDSN${service}QueryParams ${setter}${key.capitalize()}(${type==''?'String':type} value) {
        ${locidSpaceCheck}${setter}Param(${key.toUpperCase()}, value);
        return this;
    }

    public FDSN${service}QueryParams clear${key.capitalize()}() {
        clearParam(${key.toUpperCase()});
        return this;
    }
'''

    def addTypes = ['network', 'station', 'location', 'channel']
    
    def locIdTypes = ['location', 'loc']

    def dateTypes = ['startTime', 'endTime', 'startBefore', 'startAfter', 'endBefore', 'endAfter', 'updatedAfter']

    def intTypes = ['minimumLength', 'limit', 'offset']

    def floatTypes = ['minLatitude', 'maxLatitude', 'minLongitude', 'maxLongitude', 'latitude', 'longitude', 'minRadius', 'maxRadius', 'minMagnitude', 'maxMagnitude', 'minDepth', 'maxDepth']

    def booleanTypes = ['longestOnly', 'includeRestricted', 'includeAvailability', 'includeAllOrigins', 'includeAllMagnitudes', 'includeArrivals']

    def dataSelectParams = ['startTime':'Limit results to time series samples on or after the specified start time',
        'endTime':'Limit results to time series samples on or before the specified end time',
        'network':'Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.',
        'station':'Select one or more SEED station codes. Multiple codes are comma-separated.',
        'location':'Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case "--" (two dashes) will be translated to a string of two space characters to match blank location IDs.',
        'channel':'Select one or more SEED channel codes. Multiple codes are comma-separated.',
        'quality':'Select a specific SEED quality indicator, handling is data center dependent.',
        'minimumLength':'Limit results to continuous data segments of a minimum length specified in seconds.',
        'longestOnly':'Limit results to the longest continuous segment per channel.']

    def stationParams = ['startTime':'Limit to metadata epochs startingon or after the specified start time.',
        'endTime':'Limit to metadata epochs ending on or before the specified end time.',
        'startBefore':'Limit to metadata epochs starting before specified time.',
        'startAfter':'Limit to metadata epochs starting after specified time.',
        'endBefore':'Limit to metadata epochs ending before specified time.',
        'endAfter':'Limit to metadata epochs ending after specified time.',
        'network':'Select one or more network codes. Can be SEED network codes or data center defined codes. Multiple codes are comma-separated.',
        'station':'Select one or more SEED station codes. Multiple codes are comma-separated.',
        'location':'Select one or more SEED location identifiers. Multiple identifiers are comma-separated. As a special case "--" (two dashes) will be translated to a string of two space characters to match blank location IDs.',
        'channel':'Select one or more SEED channel codes. Multiple codes are comma-separated.',
        'minLatitude':'Limit to stations with a latitude larger than the specified minimum.',
        'maxLatitude':'Limit to stations with a latitude smaller than the specified maximum.',
        'minLongitude':'Limit to stations with a longitude larger than the specified minimum.',
        'maxLongitude':'Limit to stations with a longitude smaller than the specified maximum.',
        'latitude':'Specify the latitude to be used for a radius search.',
        'longitude':'Specify the longitude to the used for a radius search.',
        'minRadius':'Limit results to stations within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'maxRadius':'Limit results to stations within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'level':'Specify the level of detail for the results.',
        'includeRestricted':'Specify if results should include information for restricted stations.',
        'includeAvailability':'Specify if results should include information about time series data availability.',
        'updatedAfter':'Limit to metadata updated after specified date; updates are data center specific.']

    def eventParams = ['startTime':'Limit to events on or after the specified start time.',
        'endTime':'Limit to events on or before the specified end time.',
        'minLatitude':'Limit to events with a latitude larger than the specified minimum.',
        'maxLatitude':'Limit to events with a latitude smaller than the specified maximum.',
        'minLongitude':'Limit to events with a longitude larger than the specified minimum.',
        'maxLongitude':'Limit to events with a longitude smaller than the specified maximum.',
        'latitude':'Specify the latitude to be used for a radius search.',
        'longitude':'Specify the longitude to the used for a radius search.',
        'minRadius':'Limit to events within the specified minimum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'maxRadius':'Limit to events within the specified maximum number of degrees from the geographic point defined by the latitude and longitude parameters.',
        'minDepth':'Limit to events with depth more than the specified minimum.',
        'maxDepth':'Limit to events with depth less than the specified maximum.',
        'minMagnitude':'Limit to events with a magnitude larger than the specified minimum.',
        'maxMagnitude':'Limit to events with a magnitude smaller than the specified maximum.',
        'magnitudeType':'Specify a magnitude type to use for testing the minimum and maximum limits.',
        'includeAllOrigins':'Specify if all origins for the event should be included, default is data center dependent but is suggested to be the preferred origin only.',
        'includeAllMagnitudes':'Specify if all magnitudes for the event should be included, default is data center dependent but is suggested to be the preferred magnitude only.',
        'includeArrivals':'Specify if phase arrivals should be included.',
        'eventid':'Select a specific event by ID; event identifiers are data center specific.',
        'limit':'Limit the results to the specified number of events.',
        'offset':'Return results starting at the event count specified, starting at 1.',
        'orderBy':'Order the result by time or magnitude with the following possibilities: time: order by origin descending time time-asc : order by origin ascending time magnitude: order by descending magnitude magnitude-asc : order by ascending magnitude',
        'catalog':'Limit to events from a specified catalog',
        'contributor':'Limit to events contributed by a specified contributor.',
        'updatedAfter':'Limit to events updated after the specified time.'
    ]

    def areaMethods = '''

'''

    def extraPostCode = ['Station':'''


    public FDSNStationQueryParams area(float minLat, float maxLat, float minLon, float maxLon) {
        return setMinLatitude(minLat).setMaxLatitude(maxLat).setMinLongitude(minLon).setMaxLongitude(maxLon);
    }

    public FDSNStationQueryParams ring(float lat, float lon, float maxRadius) {
        return setLatitude(lat).setLongitude(lon).setMaxRadius(maxRadius);
    }

    public FDSNStationQueryParams donut(float lat, float lon, float minRadius, float maxRadius) {
        return ring(lat, lon, maxRadius).setMinRadius(minRadius);
    }

    public static final String LEVEL_NETWORK = "network";

    public static final String LEVEL_STATION = "station";

    public static final String LEVEL_CHANNEL = "channel";

    public static final String LEVEL_RESPONSE = "response";

    @Override
    public String getServiceName() {
        return STATION_SERVICE;
    }

    public static final String STATION_SERVICE = "station";
''',
        'Event':'''

    public static final String USGS_HOST = "earthquake.usgs.gov";
    public static final String ISC_HOST = "www.isc.ac.uk";
    public static final String ISC_MIRROR_HOST = "isc-mirror.iris.washington.edu";

    public FDSNEventQueryParams area(float minLat, float maxLat, float minLon, float maxLon) {
        return setMinLatitude(minLat).setMaxLatitude(maxLat).setMinLongitude(minLon).setMaxLongitude(maxLon);
    }

    public FDSNEventQueryParams ring(float lat, float lon, float maxRadius) {
        return setLatitude(lat).setLongitude(lon).setMaxRadius(maxRadius);
    }

    public FDSNEventQueryParams donut(float lat, float lon, float minRadius, float maxRadius) {
        return ring(lat, lon, maxRadius).setMinRadius(minRadius);
    }

    /** time: order by origin descending time */
    public static final String ORDER_TIME = "time";

    /** time-asc : order by origin ascending time */
    public static final String ORDER_TIME_ASC = "time-asc";

    /**magnitude: order by descending magnitude */
    public static final String ORDER_MAGNITUDE = "magnitude";

    /**magnitude-asc : order by ascending magnitude*/
    public static final String ORDER_MAGNITUDE_ASC = "magnitude-asc";

    @Override
    public String getServiceName() {
        return EVENT_SERVICE;
    }

    public static final String EVENT_SERVICE = "event";
''',
        'DataSelect':'''

    public String formPostString() {
        java.util.List<ChannelTimeWindow> request = new java.util.ArrayList<ChannelTimeWindow>();
        String[] netSplit = getParam(NETWORK).split(",");
        String[] staSplit = getParam(STATION).split(",");
        String[] locSplit = getParam(LOCATION).split(",");
        String[] chanSplit = getParam(CHANNEL).split(",");
        java.text.SimpleDateFormat sdf = createDateFormat();
        try {
            Date beginDate = sdf.parse(getParam(STARTTIME));
            Date endDate = sdf.parse(getParam(ENDTIME));
            for (int n = 0; n < netSplit.length; n++) {
                for (int s = 0; s < staSplit.length; s++) {
                    for (int l = 0; l < locSplit.length; l++) {
                        for (int c = 0; c < chanSplit.length; c++) {
                            request.add(new ChannelTimeWindow(netSplit[n],
                                                              staSplit[s],
                                                              locSplit[l],
                                                              chanSplit[c],
                                                              beginDate,
                                                              endDate));
                        }
                    }
                }
            }
        } catch(java.text.ParseException e) {
            // should not happen as we are parsing Dates that we previously
            // formatted with
            // the same SimpleDateFormat
            throw new RuntimeException("Problem parsing date", e);
        }
        return formPostString(request);
    }

    /**
     * Forms the text for use in a POST request to the web service. Channel and
     * time window are taken from the list of ChannelTimeWindow.
     * 
     * @return
     */
    public String formPostString(java.util.List<ChannelTimeWindow> request) {
        StringBuffer out = new StringBuffer();
        if (getParam(QUALITY) != null) {
            out.append(QUALITY + "=" + getParam(QUALITY) + "\\n");
        }
        if (getParam(MINIMUMLENGTH) != null) {
            out.append(MINIMUMLENGTH + "=" + getParam(MINIMUMLENGTH) + "\\n");
        }
        if (getParam(LONGESTONLY) != null) {
            out.append(LONGESTONLY + "=" + getParam(LONGESTONLY) + "\\n");
        }
        String SEP = " ";
        for (ChannelTimeWindow ctw : request) {
            out.append(ctw.formString(SEP, createDateFormat(), true)+"\\n");
        }
        return out.toString();
    }

    @Override
    public String getServiceName() {
        return DATASELECT_SERVICE;
    }

    public static final String DATASELECT_SERVICE = "dataselect";

''']

    public static void main(String[] args) {
        def x = new FDSNQueryParamGenerator()
        def data = ['Station':x.stationParams, 'Event':x.eventParams, 'DataSelect':x.dataSelectParams]
        for (s in ['Station', 'Event', 'DataSelect'])  {
            new File("../../main/java/edu/sc/seis/seisFile/fdsnws/FDSN${s}QueryParams.java").withWriter { writer ->
                writer.println x.createPre(s)
                data.get(s).each() { k, v ->
                    writer.println x.createItem(k, v, s)
                }
                writer.println x.createPost(s)
            }
        }
    }
}
