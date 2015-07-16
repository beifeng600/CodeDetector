package util.common.file.codedetector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *  Author: beifeng600
 * 	编码检测的主类
 *  https://github.com/beifeng600/CodeDetector
 */
public class CodeDetector {

	public static final String DefaultEncoding = "UTF-8";
	public static final String UTF8 = "UTF-8";	//现在支持的编码 UTF-8(with or without BOM)/GBK/UTF-16LE/UTF-16BE
	public static final String GBK = "GBK";
	public static final String UTF16LE = "UTF-16LE";
	public static final String UTF16BE = "UTF-16BE";
	
	private static boolean hasBOM = false;
	
	public static String in_encoding = CodeDetector.UTF8;
	public static String out_encoding = CodeDetector.UTF8;
	
	public static HashMap<String, String> encodingHM = new HashMap<String, String>();
	
	private static String chineseChPath = "/dict/ChineseChDict.dict";
	private static String puntDictPath = "/dict/其他(标点等)符号.txt";
	private static String digitDictPath = "/dict/数字字符.txt";
	private static String enDictPath = "/dict/英文字符.txt";
	private static String greekDictPath = "/dict/希腊字母.txt";
	
	private static HashMap<String,Integer> chineseChHM = new HashMap<String,Integer>();
	private static HashMap<String,Integer> puntHM = new HashMap<String,Integer>();
	private static HashMap<String,Integer> digitHM = new HashMap<String,Integer>();
	private static HashMap<String,Integer> enHM = new HashMap<String,Integer>();
	private static HashMap<String,Integer> greekHM = new HashMap<String,Integer>();
	private static HashMap<String,Integer> otherHM = new HashMap<String,Integer>();
	
	static {
		encodingHM.put(UTF8, "yes");
		encodingHM.put(GBK, "yes");
		encodingHM.put(UTF16LE, "yes");
		encodingHM.put(UTF16BE, "yes");
		Init();
	}
	
	public static void Init(){
		chineseChHM = loadDict(chineseChPath, chineseChHM);
		puntHM = loadDict(puntDictPath, puntHM);
		digitHM = loadDict(digitDictPath, digitHM);
		enHM = loadDict(enDictPath, enHM);
		greekHM = loadDict(greekDictPath, greekHM);
		
		otherHM.put(" ", 0);
		otherHM.put("	", 0);
		otherHM.put("\r", 0);
		otherHM.put("\n", 0);
		otherHM.put("\t", 0);
	}
	
	
	
	public static boolean hasBOM() {
		return hasBOM;
	}



	public static HashMap<String,Integer> loadDict(String dictPath, HashMap<String, Integer> dictHM){
		
		try {
//			FileInputStream fileInputStream = new FileInputStream(dictPath);
        	InputStream inputStream = CodeDetector.class.getResourceAsStream(dictPath);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, DefaultEncoding);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			int lineNum = 0;
			String strLine = null;
			while ((strLine = bufferedReader.readLine()) != null) {
				strLine = strLine.trim();
				lineNum += 1;
				
				if ("".equalsIgnoreCase(strLine.trim())) {
					continue;
				}
				
				if (strLine.startsWith("####")) {
					continue;
				}
				
				String[] strArr = strLine.split("\t");
				
				if(dictHM.containsKey(strArr[0].trim()) || strArr[0].length()!=1){
					System.out.println("Error\t"+lineNum+"\t"+strLine);
					continue;
				}else{
					dictHM.put(strArr[0], 0);
				}
				
			}
			
			System.out.println("Loaded "+dictPath+" ! size= "+dictHM.size()+", lineNum= "+lineNum);

			bufferedReader.close();
			inputStreamReader.close();
	        inputStream.close();
//			fileInputStream.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return dictHM;
	}
	
	public static String readFile(String filePath, String encoding){
		
        try {   
        	FileInputStream fileInputStream = new FileInputStream(filePath);
        	InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encoding);
        	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    		
        	StringBuilder strBuilder = new StringBuilder();
        	
	        String strLine = null;
	        int lineNum = 0;
			while((strLine=bufferedReader.readLine())!=null){
				lineNum += 1;
				
				if(lineNum == 1 && CodeDetector.UTF8.equalsIgnoreCase(encoding)){
					byte[] startLineBytes = strLine.getBytes();
					if(startLineBytes.length>=3 && startLineBytes[0]==(byte)0xEF && startLineBytes[1]==(byte)0xBB && startLineBytes[2]==(byte)0xBF){
						
						hasBOM = true;
					}
				}

				strBuilder.append(strLine);
			}
			
			bufferedReader.close();
	        inputStreamReader.close();
	        fileInputStream.close();
	        
	        return strBuilder.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	} 
	
	public static double calculateValidRatio(String text){
		
		int matchCount = 0;
		for (int i = 0; i < text.length(); ++i) {
			String ch = text.charAt(i) + "";
			if (CodeDetector.chineseChHM.containsKey(ch)) {
				matchCount += 1;
			} else if (CodeDetector.puntHM.containsKey(ch)) {
				matchCount += 1;
			} else if (CodeDetector.greekDictPath.contains(ch)) {
				matchCount += 1;
			} else if (CodeDetector.digitHM.containsKey(ch)) {
				matchCount += 1;
			} else if (CodeDetector.enHM.containsKey(ch)) {
				matchCount += 1;
			} else if (CodeDetector.otherHM.containsKey(ch)) {
				matchCount += 1;
			}
		}
		
//		System.out.println("match ratio: "+(matchCount*1.0/text.length()));
			
		return matchCount*1.0/text.length();
	}
	
	public static String detectorCode(String filePath){
		
		hasBOM = false;
		
		List<HMObj> hmList = new ArrayList<HMObj>();
		
		Iterator iter = encodingHM.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry)iter.next();
			Object key = entry.getKey();
//			Object val = entry.getValue();
			
			String text = readFile(filePath, (String)key);
			double score = calculateValidRatio(text);
			HMObj hmObj = new HMObj((String)key, score);
			hmList.add(hmObj);
		}
		
		Collections.sort(hmList, Collections.reverseOrder());
		
		return hmList.get(0).getStrKey();
	}
	
	public static String changeCodeFile(String filePath, String resPath, String src_encoding, String dst_encoding){
		  
        try {   
        	FileInputStream fileInputStream = new FileInputStream(filePath);
        	InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, src_encoding);
        	BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        	
        	if(resPath==null || "".equalsIgnoreCase(resPath)){
    			resPath = filePath.substring(0, filePath.lastIndexOf('.')) + "_output" + filePath.substring(filePath.lastIndexOf('.'));
    		}
    		
    		FileOutputStream fileOutputStream = new FileOutputStream(resPath);
    		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, dst_encoding);
    		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
    		
	        String strLine = null;
	        int lineNum = 0;
			while((strLine=bufferedReader.readLine())!=null){
				lineNum += 1;
				
//				if("".equalsIgnoreCase(strLine.trim())){
//					continue;
//				}
				if(lineNum == 1 && CodeDetector.UTF8.equalsIgnoreCase(src_encoding)){
					byte[] startLineBytes = strLine.getBytes();
					if(startLineBytes.length>=3 && startLineBytes[0]==(byte)0xEF && startLineBytes[1]==(byte)0xBB && startLineBytes[2]==(byte)0xBF){
						byte[] copyArray = new byte[startLineBytes.length-3];
						System.arraycopy(startLineBytes, 3, copyArray, 0, copyArray.length);
						strLine = new String(copyArray);
					}
				}
				
				bufferedWriter.write(strLine);
				bufferedWriter.newLine();
			}
			
			bufferedWriter.flush();
			bufferedWriter.close();
			outputStreamWriter.close();
			fileOutputStream.close();
			
			
			bufferedReader.close();
	        inputStreamReader.close();
	        fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static void detectDir(String inputDir, String resPath){
		try{
        	if(resPath==null || "".equalsIgnoreCase(resPath)){
    			resPath = inputDir+ "_encoding"+".txt";
    		}
    		
    		FileOutputStream fileOutputStream = new FileOutputStream(resPath,true);
    		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, DefaultEncoding);
    		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
    		
    		File rootFile = new File(inputDir);
    		if (!rootFile.exists()) {
    			System.out.println("Please input a Directory!");
    		}

    		File[] fileList = rootFile.listFiles();
    		for (File file : fileList) {
    			if(file.isDirectory()){
    				detectDir(file.getAbsolutePath(), resPath);
    				
    			}else{
    				String encoding = detectorCode(file.getAbsolutePath());
    				bufferedWriter.write(file.getAbsolutePath()+"\t"+encoding);
    				if(CodeDetector.UTF8.equalsIgnoreCase(encoding) && !hasBOM){
    					bufferedWriter.write("无BOM");
    				}
    				bufferedWriter.newLine();
    			}
    		}
    		
    		System.out.println("Complete "+inputDir);
    		
    		bufferedWriter.flush();
    		bufferedWriter.close();
    		outputStreamWriter.close();
    		fileOutputStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void changeCodeDir(String inputDir, String resDir, String dst_encoding){
		
		File rootFile = new File(inputDir);
		if (!rootFile.exists()) {
			System.out.println("Please input a Directory!");
		}
		
		if ("".equalsIgnoreCase(resDir)) {
			resDir = inputDir + "_result";
		}
		
		File resFile = new File(resDir);
		if (!resFile.exists()) {
			resFile.mkdir();
		}

		File[] fileList = rootFile.listFiles();
		for (File file : fileList) {
			if(file.isDirectory()){
				changeCodeDir(file.getAbsolutePath(), resDir+"/"+file.getName(), dst_encoding);
				
			}else{
				String src_encoding = detectorCode(file.getAbsolutePath());
				System.out.println(file.getAbsolutePath()+"\t"+src_encoding);
				changeCodeFile(file.getAbsolutePath(), resDir+"/"+file.getName(), src_encoding, dst_encoding);
			}
		}
		
		System.out.println("Complete "+inputDir);
	}
	
	public static void main(String args[]){
		
		if(args.length < 1){
			System.out.println("Usage:");
			System.out.println("java CodeDetector input [outputFile]");
			System.out.println("java CodeDetector input output out_encoding");
			return;
		}
		
		String inputPath = args[0];
		String resPath = "";
		if(args.length >= 2){
			resPath = args[1];
		}
//		boolean append = false;
		
		String out_encoding = "";
		if(args.length >= 3){
			out_encoding = args[2];
			
			if(CodeDetector.encodingHM.containsKey(out_encoding)){
				CodeDetector.out_encoding = out_encoding;
			}else{
				System.out.println("Please input correct encoding!");
				Iterator iter = CodeDetector.encodingHM.entrySet().iterator();
				while(iter.hasNext()){
					Map.Entry entry = (Map.Entry)iter.next();
					System.out.println(entry.getKey());
				}
				
				return;
			}
		}
		
		
		File inputFile = new File(inputPath);
		if(!inputFile.exists()){
			System.out.println(inputPath+" does not exists!");
			return;
		}
		
		System.out.println("Begin!\t"+(new Date()));
		
		if(inputFile.isFile()){
			if(args.length >= 3){
				String src_encoding = detectorCode(inputPath);
				CodeDetector.changeCodeFile(inputPath, resPath, src_encoding, out_encoding);
			}else{
				System.out.println("encoding: \t"+CodeDetector.detectorCode(inputPath));
			}
		}else{
			if(args.length >= 3){
				CodeDetector.changeCodeDir(inputPath, resPath, out_encoding);
			}else{
				CodeDetector.detectDir(inputPath, resPath);
			}
		}
		
		System.out.println("Complete!\t"+(new Date()));
		
		
	}
	
}
