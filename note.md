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


 









  


