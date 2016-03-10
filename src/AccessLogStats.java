
import java.io.BufferedReader;
import java.io.IOException;
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


//public static class LogEntryMapper extends Mapper<Object, Text, Text, IntWritable> {
//		
//		private final static IntWritable one = new IntWritable(1);
//		private Text url = new Text();
//		
//		private Pattern p = Pattern.compile("(?:GET|POST)\\s([^\\s]+)");
//		
//		public void map(Object key, Text value, Context context) 
//			throws IOException, InterruptedException {
//			String[] entries = value.toString().split("\r?\n"); 
//			for (int i=0, len=entries.length; i<len; i+=1) {
//				Matcher matcher = p.matcher(entries[i]);
//				if (matcher.find()) {
//					url.set(matcher.group(1));
//					context.write(url, one);
//				}
//			}
//		}
//	}
//	
//	public static class LogEntryReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
//		
//		private IntWritable total = new IntWritable();
//		
//		public void reduce(Text key, Iterable<IntWritable> values, Context context)
//			throws IOException, InterruptedException {
//			int sum = 0;
//		    for (IntWritable value : values) {
//		    	sum += value.get();
//		    }
//		    total.set(sum);
//		    context.write(key, total);
//		}
//	}
	static String inputPath, outputPath;
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		

		
		if (args.length != 2) {
			System.err.println("Usage: loganalyzer <in> <out>");
			Scanner in = new Scanner(System.in);
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
		//Mapper
		Pattern urlPattern = Pattern.compile("(?:GET|POST)\\s([^\\s]+)");
		final ConcurrentMap<String, AtomicInteger> urlMap = new ConcurrentHashMap<>();
		System.out.println(Paths.get(inputPath).toAbsolutePath());
		try (BufferedReader br = Files.newBufferedReader(Paths.get(inputPath), StandardCharsets.UTF_8)) {
		    for (String line = null; (line = br.readLine()) != null;) {
		    	Matcher matcher = urlPattern.matcher(line);
				if (matcher.find()) {
					urlMap.putIfAbsent(matcher.group(1), new AtomicInteger(0));
				    urlMap.get(matcher.group(1)).incrementAndGet();
				} 
		    }
		}
		
		List<String> values = new ArrayList<>(urlMap.keySet());
		Collections.sort(values, new Comparator<String>() {
		  public int compare(String a, String b) {
		    // no need to worry about nulls as we know a and b are both in pl
		    return urlMap.get(a).get() - urlMap.get(b).get();
		  }
		});

		for(String val : values) {
		  System.out.println(val + "," + urlMap.get(val));
		}
		

	}
	
}
