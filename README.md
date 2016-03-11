# AccessLogStats
Few operations that could be done with large Apache access logs

**Access log entry format**

    180.76.15.24 - - [23/Feb/2016:08:25:04 +0400] "GET /product/JM00116469/nature-made-dha-90-capsules-japanese-imported-supplements-1/259383 HTTP/1.1" 301 178 "-" "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)"


The source [https://github.com/ruhaim/AccessLogStats/blob/master/src/AccessLogStats.java]
The program ran succesfully on a logfile with over a million records.

**Input**

Accepts 2 command line params `inputPath` `outputPath` 
If not specified the defaults below are used  

    inputPath = "samplelog.log";

and 

    outputPath = "output.txt";


**Output**

    ------------
    Most accessed Pages
    ------------
    101376 : /product/JM00116469/nature-made-dha-90-capsules-japanese-imported-supplements-1/259383
    45056 : /
    ------------
    Top 10 IPs by access
    ------------
    101376 : 180.76.15.24
    45056 : 127.0.0.1
    ------------
    Top 20 IPs by bandwidth
    ------------
    18045106 : 180.76.15.24
    6307980 : 127.0.0.1
    ------------
    Top 10 IPs by bandwidth
    ------------
    101376 : Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)
    45056 : Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.5 Safari/535.19

**Analysis**

The implemented code is **not** the most performant, but several things have been taken into consideration to perform best.

1. Use of ConcurrentMap and AtomicInteger, works well when run in threaded form
2. Using just 1 regex to extract all data in groups
3. Using BufferedReader and BufferedWriter
4. Using a Comparator to sort through a list efficiently

It needs to be noted that the above can be made to run even faster by coding it with threads where operations could be run parallel to reduce total time.

The most efficient solution to solve this kind of problem is the use of Yarn and Hadoop, Though I had heard about them before I never had the need to try them out. This exercise made me explore it on a deeper level. But unfortunately after several hours of reading and config changes I couldn't manage to setup the development environment for it. 
Hopefully I will give it a shot again sometime in the future. 

The use of configurable (30, 60, 120 minute) window was not implemented. That's a only few more hours of effort.

From what I have read Hadoop and Yarn combined with Apache Spark have reliable support with ever-growing data.
On a simple level, the once a log stat operation completes we could store the index of the last read line elsewhere which could be used to resume operations the next time the service runs. 
