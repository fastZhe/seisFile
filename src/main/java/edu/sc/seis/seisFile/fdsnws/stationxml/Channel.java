package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Channel extends BaseNodeType {

    /** for hibernate, etc */
    Channel() {}
    
    Channel(Station station) {
        this.station = station;
        this.networkCode = station.getNetworkCode();
        this.stationCode = station.getStationCode();
    }

    public Channel(Station station, String locCode, String chanCode) {
        this(station);
        this.locCode = locCode;
        this.code = chanCode;
    }
    
    public Channel(XMLEventReader reader, Station station) throws XMLStreamException,
            StationXMLException {
        this(station);
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.CHANNEL, reader);
        super.parseAttributes(startE);
        locCode = Channel.fixLocCode(StaxUtil.pullAttribute(startE, StationXMLTagNames.LOC_CODE));
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (super.parseSubElement(elName, reader)) {
                    // super handled it
                } else if (elName.equals(StationXMLTagNames.LAT)) {
                    latitude = new FloatType(reader, StationXMLTagNames.LAT, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.LON)) {
                    longitude = new FloatType(reader, StationXMLTagNames.LON, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.ELEVATION)) {
                    elevation = new FloatType(reader, StationXMLTagNames.ELEVATION, Unit.METER);
                } else if (elName.equals(StationXMLTagNames.DEPTH)) {
                    depth = new FloatType(reader, StationXMLTagNames.DEPTH, Unit.METER);
                } else if (elName.equals(StationXMLTagNames.AZIMUTH)) {
                    azimuth = new FloatType(reader, StationXMLTagNames.AZIMUTH, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.DIP)) {
                    dip = new FloatType(reader, StationXMLTagNames.DIP, Unit.DEGREE);
                } else if (elName.equals(StationXMLTagNames.TYPE)) {
                    typeList.add(StaxUtil.pullText(reader, StationXMLTagNames.TYPE));
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE)) {
                    sampleRate = new FloatType(reader, StationXMLTagNames.SAMPLE_RATE, Unit.HERTZ);
                } else if (elName.equals(StationXMLTagNames.SAMPLE_RATE_RATIO)) {
                    sampleRateRatio = new SampleRateRatio(reader);
                } else if (elName.equals(StationXMLTagNames.STORAGEFORMAT)) {
                    storageFormat = StaxUtil.pullText(reader, StationXMLTagNames.STORAGEFORMAT);
                } else if (elName.equals(StationXMLTagNames.CLOCK_DRIFT)) {
                    clockDrift = new FloatType(reader, StationXMLTagNames.CLOCK_DRIFT, clockDriftUnit);
                } else if (elName.equals(StationXMLTagNames.CALIBRATIONUNITS)) {
                    calibrationUnits = new Unit(reader, StationXMLTagNames.CALIBRATIONUNITS);
                } else if (elName.equals(StationXMLTagNames.SENSOR)) {
                    sensor = new Sensor(reader);
                } else if (elName.equals(StationXMLTagNames.PREAMPLIFIER)) {
                    preAmplifier = new PreAmplifier(reader);
                } else if (elName.equals(StationXMLTagNames.DATALOGGER)) {
                    dataLogger = new DataLogger(reader);
                } else if (elName.equals(StationXMLTagNames.EQUIPMENT)) {
                    equipmentList.add(new Equipment(reader));
                } else if (elName.equals(StationXMLTagNames.RESPONSE)) {
                    response = new Response(reader);
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }

    public SampleRateRatio getSampleRateRatio() {
        return sampleRateRatio;
    }

    public FloatType getSampleRate() {
        return sampleRate;
    }

    public FloatType getClockDrift() {
        return clockDrift;
    }

    public String getClockDriftUnit() {
        return clockDriftUnit;
    }

    public Unit getCalibrationUnits() {
        return calibrationUnits;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public PreAmplifier getPreAmplifier() {
        return preAmplifier;
    }

    public DataLogger getDataLogger() {
        return dataLogger;
    }

    public List<Equipment> getEquipment() {
        return equipmentList;
    }

    public Response getResponse() {
        return response;
    }
    
    public String getChannelCode() {
        return getCode();
    }

    public String getLocCode() {
        return locCode;
    }

    public String getStationCode() {
        return stationCode;
    }

    @Deprecated
    public String getNetworkCode() {
        return networkCode;
    }

    public String getNetworkId() {
        return getNetwork().getNetworkId();
    }

    public FloatType getLatitude() {
        return latitude;
    }

    public FloatType getLon() {
        return longitude;
    }

    public FloatType getElevation() {
        return elevation;
    }

    public FloatType getDepth() {
        return depth;
    }

    public FloatType getAzimuth() {
        return azimuth;
    }

    public FloatType getDip() {
        return dip;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public String getStorageFormat() {
        return storageFormat;
    }

    @Override
    public String toString() {
        return getNetworkCode()+"."+getStationCode()+"."+getLocCode()+"."+getCode();
    }

    
    
    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    
    public void setEquipmentList(List<Equipment> equipmentList) {
        this.equipmentList = equipmentList;
    }

    
    public FloatType getLongitude() {
        return longitude;
    }
    
    public Station getStation() {
        return station;
    }
    
    public Network getNetwork() {
        try {
            return getStation().getNetwork();
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setStation(Station station) {
        this.station = station;
    }
    
    public void setLongitude(FloatType longitude) {
        this.longitude = longitude;
    }

    
    public void setSampleRateRatio(SampleRateRatio sampleRateRatio) {
        this.sampleRateRatio = sampleRateRatio;
    }

    
    public void setSampleRate(FloatType sampleRate) {
        this.sampleRate = sampleRate;
    }

    
    public void setClockDrift(FloatType clockDrift) {
        this.clockDrift = clockDrift;
    }

    
    public void setClockDriftUnit(String clockDriftUnit) {
        this.clockDriftUnit = clockDriftUnit;
    }

    
    public void setCalibrationUnits(Unit calibrationUnits) {
        this.calibrationUnits = calibrationUnits;
    }

    
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    
    public void setPreAmplifier(PreAmplifier preAmplifier) {
        this.preAmplifier = preAmplifier;
    }

    
    public void setDataLogger(DataLogger dataLogger) {
        this.dataLogger = dataLogger;
    }

    
    public void setResponse(Response response) {
        this.response = response;
    }

    
    public void setLocCode(String locCode) {
        this.locCode = locCode;
    }

    
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    
    public void setNetworkCode(String networkCode) {
        this.networkCode = networkCode;
    }

    
    public void setLatitude(FloatType latitude) {
        this.latitude = latitude;
    }

    
    public void setElevation(FloatType elevation) {
        this.elevation = elevation;
    }

    
    public void setDepth(FloatType depth) {
        this.depth = depth;
    }

    
    public void setAzimuth(FloatType azimuth) {
        this.azimuth = azimuth;
    }

    
    public void setDip(FloatType dip) {
        this.dip = dip;
    }

    
    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    
    public void setStorageFormat(String storageFormat) {
        this.storageFormat = storageFormat;
    }

    private Station station;

    private SampleRateRatio sampleRateRatio;

    private FloatType sampleRate;

    private FloatType clockDrift;

    private String clockDriftUnit = "SECONDS/SAMPLE";

    private Unit calibrationUnits;

    private Sensor sensor;

    private PreAmplifier preAmplifier;

    private DataLogger dataLogger;

    private List<Equipment> equipmentList = new ArrayList<Equipment>();

    private Response response;

    private String locCode, stationCode, networkCode;

    private FloatType latitude, longitude, elevation, depth, azimuth, dip;

    List<String> typeList = new ArrayList<String>();

    String storageFormat;
    
    public static String fixLocCode(String locCode) {
        String out = locCode;
        if (locCode == null ) { 
            out = EMPTY_LOC_CODE; 
        } else {
            out = out.trim();
            if (out.length() == 0 || out.equals("--")) { out = EMPTY_LOC_CODE; }
        }
        return out;
    }
    
    public static final String EMPTY_LOC_CODE = "";

}
