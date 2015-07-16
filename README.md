# CodeDetector
Detect File encoding, Now support UTF-8,GBK,UTF-16LE,UTF-16BE, Java

检查文件编码，主要针对中文文本。
目前支持 UTF-8(BOM或无BOM)、GBK、UTF-16LE、UTF-16BE
单个文件或文件夹，
支持编码批量转换。

使用方法：
	1、直接使用，下载jar包，直接使用。
	下载jar/CodeDetector-1.0.jar
	
	检测编码：
	java -jar CodeDetector-1.0.jar TestDir [outputFile]
	其中，TestDir是要检测的文件夹名称，也可以是单个文件名。
	outputFile, 是要输出编码的文件，可以省略，默认为 "TestDir_encoding.txt"
	当要检测单个文件时，outputFile不起作用。
	
	批量转换编码：
	java -jar CodeDetector-1.0.jar TestDir resDir out_encoding
	其中，TestDir是要转换的文件夹名称，resDir是目的文件夹名称，
	out_encoding 是目的编码。
	
	2、代码调用
	下载 jar/CodeDetector-1.0.jar，添加到项目path中，
	
	批量检测编码：
		String inputPath = "D:\\workspace\\UtilityTools\\TestDir";
		String resPath = "D:\\workspace\\UtilityTools\\TestDir_encoding.txt";
		
		//CodeDetector.detectDir(inputPath, "");
		CodeDetector.detectDir(inputPath, resPath);
	
	批量转换编码：
		String inputPath = "D:\\workspace\\UtilityTools\\TestDir";
		String resPath = "D:\\workspace\\UtilityTools\\TestDir_UTF8";
		
		CodeDetector.changeCodeDir(inputPath, resPath, "UTF-8");
		
原理介绍：
	挨个编码的试，同时计算合法字符的个数。
	最后取合法字符比例最高的那种编码。
	
存在问题：
	1、单个文件的试 UTF-8有无BOM可以，测文件夹的时候有问题。
	2、对于小文件比较合适。大文件时，由于要读完整个文件，会比较费时，未来可以改成先检查文件大小，若小于某个值，全部读取；否则，只读取限定的大小。
	
beifeng600  2015年7月17日
