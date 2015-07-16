jar调用方法
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