package cn.ac.cenc.tool;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.sc.seis.seisFile.mseed.*;

import java.io.*;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 描述:
 * 256 miniseed数据读取
 *
 * @author huangzhe
 * @create 2019-01-30 14:50
 */
public class MiniSeed256Input {

    public static void main(String[] args) {

        try {
            new MiniSeed256Input().readMiniSeedFile(new File("/Users/huangzhe/Downloads/256.mseed"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读到头了");
        } catch (SeedFormatException e) {
            e.printStackTrace();
        }

        System.out.println("hello world");
    }





    public void readMiniSeedFile(File sourceFile) throws IOException, SeedFormatException {
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(sourceFile));
        int count=0;
        DataRecord pre=null;
        while (true) {
            count++;
            System.out.println("总共："+count);
            DataRecord read = (DataRecord) DataRecord.read(dataInputStream);
            if (count%2==1) pre=read;
            try {
                DataRecord dataRecord = MiniseedConvert.convert256To512(pre, read);
                System.out.println(dataRecord.getRecordSize());
                Btime begin = dataRecord.getBtimeRange().getBegin();
                Btime end = dataRecord.getBtimeRange().getEnd();

                //System.out.println(dataRecord.getBtimeRange().getBegin().getLocalDateTime().toInstant(ZoneOffset.of("+08:00")));
                System.out.println(begin.getLocalDateTime()+"  begin");
                System.out.println(end.getLocalDateTime()+"  end");
                System.out.println("开始天 ："+begin.getLocalDateTime().getDayOfMonth());


            } catch (CodecException e) {
                e.printStackTrace();
            }
            DataHeader controlHeader = (DataHeader) read.getControlHeader();

            Btime begin = read.getBtimeRange().getBegin();
            Btime end = read.getBtimeRange().getEnd();
            System.out.println(begin.getLocalDateTime()+"  begin");
            System.out.println(end.getLocalDateTime()+"  end");
            System.out.println("开始天 ："+begin.getLocalDateTime().getDayOfMonth());
            DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
            System.out.println(begin.getLocalDateTime().format(dateTimeFormatter));
            System.out.println(controlHeader.getStartTime());
            Blockette blockette = read.getBlockettes()[0];
            byte[] bytes1 = read.toByteArray();
            System.out.println(bytes1.length+"  数据包大小");
            int type = blockette.getType();
            System.out.println("  sq"+controlHeader.getSequenceNum() +"  "+controlHeader.getTypeCode()+"  ");
            try {
                DecompressedData decompress = read.decompress();
                int[] asInt = decompress.getAsInt();
            } catch (CodecException e) {
                e.printStackTrace();
            }
            if (type == 1000) {
                Blockette1000 b1000 = (Blockette1000) blockette;
                System.out.println(b1000.getEncodingFormat());
                System.out.println(read.getData().length);
                System.out.println(read.getRecordSize()+"  record");
                byte[] bytes = b1000.toBytes();
                System.out.println(bytes.length + "   大小");
            }

        }

    }


}
