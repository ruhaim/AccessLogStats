
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.*;


public class AccessLogStats {

	private static String inputPath, outputPath;
	final private static ConcurrentMap<String, AtomicInteger> urlMap = new ConcurrentHashMap<>();
	final private static ConcurrentMap<String, AtomicInteger> ip2HitCountMap = new ConcurrentHashMap<>();
	final private static ConcurrentMap<String, AtomicInteger> ip2BandwidthMap = new ConcurrentHashMap<>();
	final private static ConcurrentMap<String, AtomicInteger> userAgent2UseCountMap = new ConcurrentHashMap<>();
	private static Scanner in;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		

		
		if (args.length != 2) {
			System.err.println("Usage: logStats <in> <out>");
			in = new Scanner(System.in);
			System.out.print("Enter Input File Path : ");
//			inputPath = in.nextLine();
			inputPath = "samplelog.log";
			System.out.print("Enter Output File Path : ");
//			outputPath = in.nextLine();
			outputPath = "output.txt";
			args = new String[]{inputPath, outputPath};
			
			//System.exit(2);
		}
		inputPath = args[0];
		outputPath = args[1];

		
		mapData();
		
		writeResultsToFile();
		

	}
	private static void writeResultsToFile()  throws IOException {
		PrintWriter out = null;
		try {
			File outPutFile = new File(outputPath);
			if(!outPutFile.exists()) {
				outPutFile.createNewFile();
			} 
		  out = new PrintWriter(new OutputStreamWriter(
		      new BufferedOutputStream(new FileOutputStream(outPutFile)), "UTF-8"));
		  aggregateResults(out);
		  
		} catch (UnsupportedEncodingException e) {
		  e.printStackTrace();
		} catch (FileNotFoundException e) {
		  e.printStackTrace();
		} finally {
		  if(out != null) {
		    out.flush();
		    out.close();
		    System.out.println("File "+ outputPath + " has been created");
		  }
		}
		
	}
	
	private static void aggregateResults(PrintWriter out){
		out.println("------------");
		out.println("Top 100 Most accessed Pages");
		out.println("------------");
		printTopDetails(urlMap, Integer.MAX_VALUE, out);
		
		
		out.println("------------");
		out.println("Top 10 IPs by access");
		out.println("------------");
		printTopDetails(ip2HitCountMap, 10, out);

		
		out.println("------------");
		out.println("Top 20 IPs by bandwidth");
		out.println("------------");
		printTopDetails(ip2BandwidthMap, 20, out);

		
		out.println("------------");
		out.println("Top 10 IPs by bandwidth");
		out.println("------------");
		printTopDetails(userAgent2UseCountMap, 10, out);
		
	}
	
	private static void printTopDetails(ConcurrentMap<String, AtomicInteger> dataMap, int limitMax, PrintWriter out){
		//Top 10 ip address by bandwidth
		List<String> tempList = new ArrayList<>(dataMap.keySet());
		Collections.sort(tempList, new Comparator<String>() {
			public int compare(String a, String b) {
				return dataMap.get(b).get() - dataMap.get(a).get();
			}
		});
		
		int limitCount = 0;
		for(String val : tempList) {
			if(++limitCount>limitMax){
				break;
			}
			out.println(dataMap.get(val) + " : " + val);
			
		}
	}
	
	private static void mapData() throws Exception{
		
		//Mapper

		Pattern captureAllPattern = Pattern.compile("^(\\S+) \\S+ \\S+ \\[[^:]+:\\d+:\\d+:\\d+ [^\\]]+\\] \\\"\\S+ (.*?) \\S+\\\" \\S+ (\\S+) \"[^\"]*\" \"([^\"]*)\"");
		//Gr 1 - IP Addr
		//Gr 2 - Url
		//Gr 3 - Bandwidth
		//GR 4 - Useragent
		
		System.out.println(Paths.get(inputPath).toAbsolutePath());
		try (BufferedReader br = Files.newBufferedReader(Paths.get(inputPath), StandardCharsets.UTF_8)) {
		    for (String line = null; (line = br.readLine()) != null;) {
		    	Matcher matcher = captureAllPattern.matcher(line);
				if (matcher.find()) {
					String ipAddr = matcher.group(1);
					String url = matcher.group(2);
					int bandwidth = Integer.parseInt(matcher.group(3));
					String useragent = matcher.group(4);
					
					urlMap.putIfAbsent(url, new AtomicInteger(0));
				    urlMap.get(url).incrementAndGet();
				    
				    ip2HitCountMap.putIfAbsent(ipAddr, new AtomicInteger(0));
				    ip2HitCountMap.get(ipAddr).incrementAndGet();

				    userAgent2UseCountMap.putIfAbsent(useragent, new AtomicInteger(0));
				    userAgent2UseCountMap.get(useragent).incrementAndGet();

				    ip2BandwidthMap.putIfAbsent(ipAddr, new AtomicInteger(bandwidth));
				    ip2BandwidthMap.get(ipAddr).addAndGet(bandwidth);
				    
				    
				} 
		    }
		}
		
	}
	
}
