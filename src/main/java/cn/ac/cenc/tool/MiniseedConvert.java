package cn.ac.cenc.tool;

import com.google.common.primitives.Bytes;
import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.Steim2;
import edu.iris.dmc.seedcodec.SteimFrameBlock;
import edu.sc.seis.seisFile.mseed.*;

import javax.xml.crypto.Data;
import java.io.IOException;

/**
 * 描述:
 * 256与512miniseed包的转换
 *
 * @author huangzhe
 * @create 2019-01-30 15:51
 */
public class MiniseedConvert {


   public static DataRecord convert256To512(DataRecord a1,DataRecord a2) throws SeedFormatException, CodecException, IOException {
       DataRecord pre=null;
       DataRecord post=null;

       //判断哪个包是时间早
       if (a1.getStartBtime().before(a2.getStartBtime())){
           pre=a1;
           post=a2;
       }else {
           pre=a2;
           post=a1;
       }

       DataHeader h1 = a1.getHeader();
       DataHeader h2 = a2.getHeader();

       int[] preint = pre.decompress().getAsInt();
       int[] postint = post.decompress().getAsInt();
       Blockette1000 blockette = (Blockette1000)pre.getBlockettes()[0];

       // TODO: 2019-01-30 swapBytes是否需要设置 ?
       int[] d1 = Steim2.decode(pre.getData(), h1.getNumSamples(), false);
       int[] d2 = Steim2.decode(post.getData(), h2.getNumSamples(), false);
       int[] data=new int[d1.length+d2.length];
       System.arraycopy(d1,0,data,0,d1.length);
       System.arraycopy(d2,0,data,d1.length,d2.length);


       int encodingType=blockette.getType();


       DataHeader dataHeader=new DataHeader(h1.getSequenceNum(),h1.getTypeCode(),h1.isContinuation());
       dataHeader.setActivityFlags(h1.getActivityFlags());
       dataHeader.setChannelIdentifier(h1.getChannelIdentifier());
       dataHeader.setDataBlocketteOffset((short)h1.getDataBlocketteOffset());
       dataHeader.setDataOffset((short)h1.getDataOffset());
       dataHeader.setDataQualityFlags(h1.getDataQualityFlags());
       dataHeader.setIOClockFlags(h1.getIOClockFlags());
       dataHeader.setLocationIdentifier(h1.getLocationIdentifier());
       dataHeader.setNetworkCode(h1.getNetworkCode());
       dataHeader.setNumSamples((short)(h1.getNumSamples()+h2.getNumSamples()));
       dataHeader.setSampleRateFactor((short)h1.getSampleRateFactor());
       dataHeader.setSampleRateMultiplier((short)h1.getSampleRateMultiplier());
       dataHeader.setStartBtime(h1.getStartBtime());
       dataHeader.setStationIdentifier(h1.getStationIdentifier());
       dataHeader.setTimeCorrection(h1.getTimeCorrection());

       DataRecord dataRecord=new DataRecord(dataHeader);
       Blockette1000 b1000=new Blockette1000();
       //11为steim2
       b1000.setEncodingFormat((byte) encodingType);
       //512
       b1000.setDataRecordLength((byte)9);

       b1000.setWordOrder(blockette.getWordOrder());
       b1000.setReserved(blockette.getReserved());

       dataRecord.addBlockette(b1000);

       SteimFrameBlock encode = Steim2.encode(data, 7);

       //512对应的数据长度为  448
       dataRecord.setData(encode.getEncodedData());
      // dataRecord.


       
//
//       DataRecord dataRecord=new DataRecord();
       return dataRecord;
   }


}
