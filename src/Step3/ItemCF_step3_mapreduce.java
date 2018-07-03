package step3;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.Text;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.ClassNotFoundException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.fs.FileSystem;

public class MR3 {
    private static String inPath = "/ItemCF/step1_output/";
    private static String outPath = "/ItemCF/step3_output";
    private static String hdfs = "hdfs://master:8020"; //待修改

    public int run() {
        try {
            Configuration conf = new Configuration();
            conf.set("fs.defaultFS", hdfs);
            Job job = Job.getInstance(conf, "step3");

            job.setJarByClass(MR3.class);
            job.setMapperClass(Mapper3.class);
            job.setReducerClass(Reducer3.class);

            job.setMapOutputKeyClass(Text.class);
            job.setMapOutputValueClass(Text.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);

            FileSystem fs = FileSystem.get(conf);
            Path path = new Path(inPath);
            if (fs.exists(path)) {
                FileInputFormat.addInputPath(job, path);
            } else {
                System.out.println("error! not exist");
            }

            Path outputPath = new Path(outPath);
            fs.delete(outputPath ,true);
            FileOutputFormat.setOutputPath(job, outputPath);
            return job.waitForCompletion(true) ? 1 : -1;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public static void main(String[] args) {
        int result = -1;
        result = new MR3().run();
        if (result == 1) {
            System.out.println("Step3 success...");
        } else {
            System.out.println("Step3 fail...");
        }
    }
}