jar���÷���
	���� jar/CodeDetector-1.0.jar����ӵ���Ŀpath�У�

���������룺
		String inputPath = "D:\\workspace\\UtilityTools\\TestDir";
		String resPath = "D:\\workspace\\UtilityTools\\TestDir_encoding.txt";
		
		//CodeDetector.detectDir(inputPath, "");
		CodeDetector.detectDir(inputPath, resPath);
	
	����ת�����룺
		String inputPath = "D:\\workspace\\UtilityTools\\TestDir";
		String resPath = "D:\\workspace\\UtilityTools\\TestDir_UTF8";
		
		CodeDetector.changeCodeDir(inputPath, resPath, "UTF-8");