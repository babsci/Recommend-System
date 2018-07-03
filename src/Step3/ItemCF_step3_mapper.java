package step3;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.lang.InterruptedException;

public class Mapper3 extends Mapper<LongWritable, Text, Text, Text> {

    private Text outKey = new Text();
    private Text outValue = new Text();
    /**

     * key: line number

     * value: line-number col_num

     */
    @Override

    protected void map(LongWritable key, Text value, Context context) 
        throws IOException, InterruptedException{
        System.out.println("tell you the value");
        String[] rowAndLine = value.toString().split("\t");
        System.out.println(value.toString());
        // 矩阵的行号
        String row = rowAndLine[0];
        String[] lines = rowAndLine[1].split(",");
		//["1_0","2_3","3_-1","4_2","5_-3"]
		
        for (int i = 0; i < lines.length; ++i) {
            String column = lines[i].split("_")[0];
            String valuestr = lines[i].split("_")[1];
            // key: 列号, value: 行号_值
            outKey.set(column);
            outValue.set(row + "_" + valuestr);
            context.write(outKey, outValue);
        }
    }
}