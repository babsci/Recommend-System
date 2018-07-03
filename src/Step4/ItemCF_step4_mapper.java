package step4;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.InterruptedException;
import java.util.List;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.text.DecimalFormat;

public class Mapper4 extends Mapper<LongWritable, Text, Text, Text> {

    private Text outKey = new Text();
    private Text outValue = new Text();
    private List<String> cacheList = new ArrayList<String>();
    private DecimalFormat df = new DecimalFormat("0.00");
	
    @Override

    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);

        FileReader fr = new FileReader("itemUserScore2");
        BufferedReader bf = new BufferedReader(fr);
        String line = null;
        while ((line = bf.readLine()) != null) {
            cacheList.add(line);
        }
        fr.close();
        bf.close();
    }



    /**

     * @param key: row

     * @param value: row tab col_value, ...,

     */

    @Override

    protected void map(LongWritable key, Text value, Context context) 
        throws IOException, InterruptedException {
        String row_matrix1 = value.toString().split("\t")[0];
        String[] column_value_array_matrix1 = value.toString().split("\t")[1].split(",");
        for (String line : cacheList) {
            String row_matrix2 = line.split("\t")[0];
            String[] column_value_array_matrix2 = line.split("\t")[1].split(",");

            double result = 0;

            for (String cv : column_value_array_matrix1) {
                String column_matrix1 = cv.split("_")[0];
                String value_matrix1 = cv.split("_")[1];

                for (String cvm2 : column_value_array_matrix2) {
                    if (cvm2.startsWith(column_matrix1 + "_")) {
                        String value_matrix2 = cvm2.split("_")[1];
                        result += Double.valueOf(value_matrix1) * Double.valueOf(value_matrix2);
                    }
                }
            }

            if (result == 0) {
                continue;
            }
            //result是结果矩阵中的某元素，坐标为 行：row_matrix1 ,列：row_matrix2（右矩阵已经转置） 
            outKey.set(row_matrix1);
            outValue.set(row_matrix2 + "_" + df.format(result));
            //输出格式key:行 value:列_值
			context.write(outKey, outValue);
        }
    }
}