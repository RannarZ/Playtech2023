public class WrongMove {
    private int timestamp;
    private int sessionID;
    private int index;

    WrongMove(int timestamp, int sessionID, int index){
        this.timestamp = timestamp;
        this.sessionID = sessionID;
        this.index = index;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getSessionID() {
        return sessionID;
    }

    public int getIndex() {
        return index;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String toString() {
        return "WrongMove{" +
                "timestamp=" + timestamp +
                ", sessionID=" + sessionID +
                '}';
    }
}
