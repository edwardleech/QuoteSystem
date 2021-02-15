package marketmaker.data;

/**
 * Request of Quote from Client
 */
public class Request {

    private int securityId;
    private int way;
    private int size;
    private long requestStartTime;

    public Request (int securityId, int way, int size, long requestStartTime)
    {
        this.securityId = securityId;
        this.way = way;
        this.size = size;
        this.requestStartTime = requestStartTime;
    }

    public int getSecurityId() {
        return securityId;
    }

    public void setSecurityId(int securityId) {
        this.securityId = securityId;
    }

    public int getWay() {
        return way;
    }

    public void setWay(int way) {
        this.way = way;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getRequestStartTime() {
        return requestStartTime;
    }

    public void setRequestStartTime(long requestStartTime) {
        this.requestStartTime = requestStartTime;
    }
}
