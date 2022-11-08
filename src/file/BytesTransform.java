package file;

public class BytesTransform {private static final String HEX= "0123456789ABCDEF";

public BytesTransform() {
}

public static long bytesToLong(byte[] bytes, int offset) {
	long res = 0;
	
	for(int index = 7; index >= 0; index--) {
		res <<= 8;
		res |= bytes[offset + index] & 0xFF;
	}
	
	return res;
}
//上下这两个方法操作与byteToInt方法相同，处理的长度不同而已
public static long bytesToLong(byte[] bytes) {
	long res = 0;
	
	for(int index = 7; index >= 0; index--) {
		res <<= 8;
		res |= bytes[index] & 0xFF;
	}
	
	return res;
}

public static byte[] longToBytes(long val) {
	byte[] res = new byte[8];
	
	for (int index = 0; index < res.length; index++) {
		res[index] = (byte)((val >> index * 8) & 0xFF);
	}
	
	return res;	
}
//上下这两个方法操作与intToBytes方法相同，处理的长度不同而已
public static byte[] longToBytes(byte[] res, int offset, long val) {
	byte longBytes[] = longToBytes(val);
	
	for(int index = 0; index < longBytes.length; index++) {
		res[offset + index] = longBytes[index];
	}
	
	return res;
}

//将byte数组指定位置元素转换为int，与下面的方法操作相同
public static int bytesToInt(byte[] bytes,int offset) {
	int res = 0;
	
	for(int index = 3; index >= 0; index--) {
		res <<= 8;
		res |= bytes[offset + index] & 0xFF;
	}
	
	return res;
}

public static int bytesToInt(byte[] bytes) {
	int res = 0;
	
	//res 0x12345678 存储到byte中，结果是:
	//78 56 34 12
	
	//res: 00 00 00 00 左移8位 00 00 00 00
	//res |= 0x12;
	//res: 00 00 00 12 左移8位 00 00 12 00
	//res |= 0x34;
	//res: 00 00 12 34 左移8位 00 12 34 00
	//res |= 0x56;
	//res: 00 12 34 56 左移8位 12 34 56 00
	//res |= 0x78;
	//res: 12 34 56 78
	
	
	for(int index = 3; index >= 0; index--) {
		res <<= 8;
		res |= bytes[index] & 0xFF;
	}
	
	return res;
}

//将int转化后的四个byte放入传入的byte数组的指定位置
public static byte[] intToBytes(byte[] res, int offset, int val) {
	byte intBytes[] = intToBytes(val);
	
	for(int index = 0; index < intBytes.length; index++) {
		res[offset + index] = intBytes[index];
	}
	
	return res;
}

public static byte[] intToBytes(int val) {
	byte[] res = new byte[4];
	
	/*result[3] = (byte)((val >> 24) & 0xFF);
	*result[2] = (byte)((val >> 16) & 0xFF);
	*result[1] = (byte)((val >> 8) & 0xFF);
	*result[0] = (byte)((val >> 0) & 0xFF);
	*分别取不同的位
	*/
	for (int index = 0; index < res.length; index++) {
		res[index] = (byte)((val >> index * 8) & 0xFF);
	}
	
	return res;	
}

//传入的字符串必须符合格式规定，从第一个字符开始每两个字符代表一个byte
//每对字符都是高位字符加低位字符，如E9
public static byte[] stringToBytes(String str) {
	if (str == null) {
		return null;
	}
	
	int len = str.length();
	//字符串长度不为偶数不可以转化
	if (len <= 0 || len % 2 != 0) {
		return null;
	}
	byte[] res = new byte[len/2];
	
	int strIndex = 0;
	int binaryIndex = 0;
	while(strIndex < len) {
		//获得高位与低位
		int hVal = HEX.indexOf(str.charAt(strIndex));
		int lVal = HEX.indexOf(str.charAt(strIndex + 1));
		res[binaryIndex++] = (byte) ((hVal << 4) | lVal);
		
		strIndex += 2;
	}
	
	return res;	
}

//全部转化
public static String bytesToString(byte[] bytes) {
	return bytesToString(bytes, 0, bytes.length);
}

//部分转换(从offset开始将len长度的byte转化为String)
public static String bytesToString(byte[] bytes, int offset, int len) {
	StringBuffer res = new StringBuffer();
	
	//一个byte用两个十六进制的数字表示
	for (int index = offset; index < offset + len; index ++) {
		byte val = bytes[index];
		
		//分别与0x0F,即0000 1111做与运算取得该字节的高4位与低四位，在HEX中添加对应的字符
		res.append(HEX.charAt((val >> 4) & 0x0F))
			.append(HEX.charAt(val & 0x0F));
	}
	return res.toString();
}
}