package file;

public class NetRecieveSpeed extends NetSendRecieveSpeedAdapter{
private volatile static NetRecieveSpeed me;
	
	private volatile static long startReceiveTime;
	private volatile static long lastReceiveTime;
	
	private volatile static long allReceiveBytes;
	private volatile static long curSpeed;
	private volatile static long averSpeed;
	
	private NetRecieveSpeed() {	
	}
	
	public static NetRecieveSpeed newInstance() {
		if (me == null) {
			synchronized (NetRecieveSpeed.class) {
				if(me == null) {
					startReceiveTime = lastReceiveTime =
							System.currentTimeMillis();
					allReceiveBytes = 0;
					curSpeed = averSpeed = 0;
					me = new NetRecieveSpeed();
				}
			}
		}
		return me;
	}
	
	public static void clear() {
		me = null;
		allReceiveBytes = 0;
		curSpeed = averSpeed = 0;
	}
 
	@Override
	public void afterRecieve(int recieveBytes) {
		long curTime = System.currentTimeMillis(); //现在时间
		long deltaTime = curTime - lastReceiveTime; //变化时间
		long allTime = curTime - startReceiveTime; //总时间
		
		curSpeed = (long) ((double)recieveBytes * 1000 / deltaTime);//瞬时速度
		allReceiveBytes = allReceiveBytes + recieveBytes;
		averSpeed = (long) ((double)recieveBytes * 1000 / allTime);//平均速度
		
		lastReceiveTime = curTime;
	}
	
	public long getCurSpeed() {
		return curSpeed;
	}
 
	public long getAverSpeed() {
		return averSpeed;
	}
}
