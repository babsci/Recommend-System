hadoop学习笔记：
核心组成：1.HDFS：分布式文件系统，存储海量的数据
          2.MapReduce：并行处理框架，实现任务分解和调度
用途：搭建大型数据仓库，PB级数据的存储、处理、分析、统计等业务
优势：1.高扩展
      2.低成本
	  3.成熟的生态圈（Hbase、Hive、zookeeper）
版本：推荐使用v1.2 稳定且适合初学者

配置步骤：
1.创建云主机配置Linux环境 Windows用户建议使用云主机 虚拟机受限制
2.jdk安装和设置环境变量
3.hadoop安装和配置 + 修改四个配置文件
（不熟Linux 一把辛酸泪 之后有时间会回来总结一下安装过程出现的错误）

HDFS基本概念
-Block 默认大小为64M 文件存储处理的逻辑单元
-NameNode 管理节点 存放文件元数据
 1.文件与数据块的映射表
 2.数据块与数据节点的映射表
-DataNode 工作节点 存放真正的数据块

容错
数据块副本
每个数据块3个副本，分布在两个机架内的三个节点
心跳检测
DataNode定期向NameNode发送心跳信息
二级NameNode
二级NameNode定期同步元数据映像文件和修改日志 NameNode发生故障的时候备胎转正

HDFS文件读写
读取请求：
  1.客户端发出读取请求
  2.NameNode返回元数据
  3.读取Blocks
写文件：
  1.文件拆分成块
  2.返回DataNode
  3.写入Blocks
  4.流水线复制
  5.更新元数据

HDFS特点
1.数据冗余，硬件容错
2.流式的数据访问
3.存储大文件
适用性：
适合数据批量读写，吞吐量高
  -适合一次写入多次读取，顺序读写
不适合交互式应用，低延迟很难满足
  -不支持多用户并发写相同文件
  
HDFS使用
hadoop fs -ls                        #列出HDFS内目录内容
hadoop fs -mkdir input               #创建出新目录
hadoop fs -put 文件名 input/         #将文件存储在input内
hadoop fs -cat input/文件名          #查看文件内容
hadoop fs -get input/文件名 新文件名 #将文件下下来
hadoop dfsadmin -report              #报告存储情况（namenode、datanode等）

MapReduce原理
Map(拆分成小的子任务)+Reduce(汇总)
Input split切分 -> shuffle交换 -> output
-Job&Task
-JobTracker(job -> map任务、reduce任务)
 1.作业调度
 2.分配任务、监控任务执行进度
 3.监控TaskTracker状态
-TaskTracker(map reduce)
 1.执行任务
 2.汇报任务状态
 
容错机制
1.重复执行 *4次
2.推测执行 reduce端等待map执行得慢 再新建 直到出现结果

Hadoop样例1：
单词计数：
1.编写WordCount.java，包括Mapper类和Reducer类
2.编译WordCount.java，Javac -classpath 编译成.class
3.打包 jar -cvf WordCount.jar classes
4.作业提交
  hadoop jar WordCount.jar WordCount input output  指定输入和输出文件
  
vim WordCount.java
javac -classpath /opt/hadoop-1.2.1/hadoop-core-1.2.1.jar:/opt/
hadoop-1.2.1/lib/commons/cli-1.2.jar -d word_count_class/ WordCount.java
cd word_count_class
ls
jar -cvf WordCount.jar *.class
//输入
vim file1
vim file2
hadoop -mkdir input_word_count
hadoop fs -put input/* input_wordcount/
//提交执行
hadoop jar word_count_class/word_count.jar WordCount input_wordcount output_wordcount
//查看执行结果
hadoop fs -ls output_wordcount
hadoop fs -cat  output_wordcount/?

Hadoop样例2：
用MapReduce进行排序
分区->reduce排序

hadoop 1.x默认block大小为64MB
hadoop 2.x默认block大小为128MB
可以在hdfs-site.xml中设置参数：dfs.block.size

由于NameNode内存有限，大量的小文件会给HDFS带来性能上的问题，故适合存放大文件
在实际情况下，map任务个数受多个条件限制，一般一个datanode的map任务数量控制在10-100比较合适
增加map个数，可增大mapred.map.tasks
减少map个数，可增大mapred.min.split.size
如果要减少map个数，但有很多小文件，可以先合并成大文件，再使用准则2

map-shuffle中间的本地优化过程：combine
在本地按照key先行一轮排序和合并，再进行网络混洗
在多数情况下combine可以看成是对本地数据的reduce操作，可以复用reduce的逻辑
job.setCombinerClass(MyReduce.class)
job.setReducerClass(MyReduce.class)

在一个MapReduce作业中，以下三者的数量总是相等的：
patitoner的数量
reduce任务的数量
最终输出文件

reduce任务数量建议设为一个较大的值
调节参数mapred.reduce.tasks
再代码中调用job.setNumReduceTasks(int n)的方法

Hadoop的分布式缓存
1.在main方法中加载共享文件的HDFS路径（目录/文件），可以在末尾#+别名
  String cache = "hdfs://10.105.xxx.xxx:8020/cache/file"; //目录
  cache = cache + ”#myfile“  //别名
  job.addCacheFile(new Path(cache).toUri(),conf);
2.在Mapper类/Reducer的setup方法中，用输入流获取分布式缓存中的文件
 protected void setup(Context context)throwa IOException,InterruptedException{
   FileReader reader = new FileReader("myfile");
   BufferedReader br = new BufferedReader(reader);
   ...
   }

矩阵相乘
矩阵在文件中的表示:
  1 2 -2 0
  3 3 4 -3
  
  行 tab 列_值 列_值  列_值     列_值
  1      1_1，   2_2，  3_-2，  4_0
  
思路：
1.将右矩阵转置 即可转化为左矩阵行与右矩阵的行相乘
2.将整个右矩阵载入分布式缓存
3.将左矩阵的行作为Map输入
4.在Map执行之前将缓存的右矩阵以行为单位放入List
5.在Map计算时从List中取出所有行分别与输入行相乘

相似度
余弦相似度
欧氏距离
切比雪夫距离
...


ItemCF 基于物品的推荐算法
算法思想：推荐与用户之前喜欢的物品相似的物品
MapReduce步骤
步骤1：根据用户行为列表计算用户、物品的评分矩阵
       输入:用户ID,物品ID，分值
	   输出：物品ID(行) - 用户ID(列) - 分值
	   
步骤2：根据用户、物品的评分矩阵计算物品和物品的相似度矩阵
       cos<a,b>
       方阵 对角线对称 两两相乘得到内容
	   输入：步骤1的输出
	   缓存：步骤1的输出
	   （输出和缓存是相同的文件）
	   输出：物品ID（行）-物品ID(列)-相似度
	   
步骤3：相似度矩阵 * 评分矩阵 = 推荐列表
       3‘1：将评分矩阵转置
	   输入：步骤1的输出
	   输出：用户ID（行）-物品（列）-分值
	   
	   3’2：相似度矩阵*评分矩阵（经过转置之后）
	   输入：步骤2的输出
	   缓存：步骤3的输出
	   输出：物品ID(行) -用户ID（列）-分值
	   
步骤4: 将推荐列表中之前有过行为的(评分矩阵不为0)的元素置零
       输入：步骤4的输出
	   缓存：步骤1的输出
	   输出：用户ID(行)-物品ID(列)-分值（最终的推荐列表）
	   
//步骤5:取出最感兴趣的物品


 









  


