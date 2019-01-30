package cn.ac.cenc.tool;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.sc.seis.seisFile.mseed.*;

import java.io.*;

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
        while (true) {
            count++;
            System.out.println("总共："+count);
            DataRecord read = (DataRecord) DataRecord.read(dataInputStream);
            DataHeader controlHeader = (DataHeader) read.getControlHeader();
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
