package sample;

public class Task {

    private String address;
    private String accountID;
    private int[] taskPoints = new int[33];

    public Task() {
    }

    public Task(String address, String accountID, int[] taskPoints) {
        this.address = address;
        this.accountID = accountID;
        this.taskPoints = taskPoints;
    }



    public String getAdress() {
        return address;
    }

    public void setAdress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public int[] getTaskPoints() {
        return taskPoints;
    }

    public void setTaskPoints(int[] taskPoints) {
        this.taskPoints = taskPoints;
    }

    public void setTaskPoints(int index,int points){
        taskPoints[index] = points;
    }
}
