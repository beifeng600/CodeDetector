package util.common.file.codedetector;

/**
 *  Author: beifeng600
 * 	对于HashMap按照值进行排序的POJO类
 * 	https://github.com/beifeng600/CodeDetector
 */

public class HMObj implements Comparable<HMObj>{
	
	String strKey;	//键
	double value; 	//值
	
	public HMObj(String strKey, double value) {
		super();
		this.strKey = strKey;
		this.value = value;
	}
	public String getStrKey() {
		return strKey;
	}
	public void setStrKey(String strKey) {
		this.strKey = strKey;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return strKey + "####" + value;
	}
	@Override
	public int compareTo(HMObj o) {
		// TODO Auto-generated method stub
		
		if(this.getValue() == o.getValue()){
			return 0;
		}else if(this.getValue()>o.getValue()){
			return 1;
		}else{
			return -1;
		}
	}
	
}
