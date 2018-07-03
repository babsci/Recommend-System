package step5;

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

public class Mapper5 extends Mapper<LongWritable, Text, Text, Text> {
    private Text outKey = new Text();
    private Text outValue = new Text();
    private List<String> cacheList = new ArrayList<String>();
    private DecimalFormat df = new DecimalFormat("0.00");
    
	@Override

    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        try{
			
        FileReader fr = new FileReader("itemUserScore3");
        BufferedReader bf = new BufferedReader(fr);

        String line = null;
        while ((line = bf.readLine()) != null) {

            cacheList.add(line);

        }

        fr.close();
        bf.close();
        }catch(Exception e){
		   e.printStackTrace();
		}
    }

    /**

     * @param key: row

     * @param value: row tab col_value, ...,

     */

    @Override

    protected void map(LongWritable key, Text value, Context context) 

        throws IOException, InterruptedException {

        String item_matrix1 = value.toString().split("\t")[0];
        String[] user_score_array_matrix1 = value.toString().split("\t")[1].split(",");

        for (String line : cacheList) {
            String item_matrix2 = line.split("\t")[0];
            String[] user_score_array_matrix2 = line.split("\t")[1].split(",");
            
			
			//如果物品ID相同
			if(item_matrix1.equals(item_matrix2)){
				//遍历matrix1的列
				for(String user_score : user_score_array_matrix1){
					boolean flag = false;
					String user_matrix1 = user_score_matrix1.split("_")[0];
					String score_matrix1 = user_score_matrix1.split("_")[1];
					
					//遍历矩阵2的列
					for(String user_score_matrix2 : user_score_array_matrix2){
						String user_matrix2 = user_score_matrix1.split("_")[0];
						if(user_matrix1.equals(user_matrix2)){
							flag = true;
						}
					}
					if(flag == false){
					    outKey.set(user_matrix1);
						outValue.set(item_matrix1 + "_" + "score_matrix1");
						context.write(outKey,outValue);
					}
				}
			}  
        }
    }
}