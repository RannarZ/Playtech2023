public class WrongMove {
    private int timestamp;
    private int sessionID;

    WrongMove(int timestamp, int sessionID){
        this.timestamp = timestamp;
        this.sessionID = sessionID;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getSessionID() {
        return sessionID;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String toString() {
        return "WrongMove{" +
                "timestamp=" + timestamp +
                ", sessionID=" + sessionID +
                '}';
    }
}
