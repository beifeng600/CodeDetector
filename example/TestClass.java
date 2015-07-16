import java.util.Date;

import util.common.file.codedetector.CodeDetector;

public class TestClass {
	
	public static void main(String[] args){
		
		String filePath = "D:\\workspace\\UtilityTools\\TestDir";
		String resPath = "D:\\workspace\\UtilityTools\\TestDir_encoding.txt";
		
		System.out.println("Begin!\t"+(new Date()));
		
		String encoding = CodeDetector.detectorCode(filePath);
		
//		System.out.println(filePath+" encoding:\t"+encoding);
//		if(encoding.equalsIgnoreCase(CodeDetector.UTF8) && !CodeDetector.hasBOM()){
//			System.out.println("无BOM");
//		}
		
		CodeDetector.detectDir(filePath, resPath);
		
		System.out.println("Complete!\t"+(new Date()));
	}
	
}
