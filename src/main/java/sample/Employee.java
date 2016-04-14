package sample;

import java.util.ArrayList;
import java.util.List;

public class Employee {

    private int technumber;
    private String firstName;
    private String secondName;
    private ArrayList<Task> tasks = new ArrayList<>();
    private int taskCount = 0;
    //TODO return interface
    public List<Task> getTasks() {
        return tasks;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public int[] getPointsByAccountId(String accountID){
        for (Task task : tasks) {
            if (task.getAccountID().equals(accountID)) return task.getTaskPoints();
        }
        throw new IllegalArgumentException("No such job with this accountID");
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
        taskCount=tasks.size();
    }

    public Employee() {

    }



    public void addTask(Task task){
        tasks.add(task);
        taskCount++;
    }


    private int convertTaskPoint(int task, int value) {
        if (task == 0 || task == 11 || task == 13 || task == 19 || task >= 21 && task <= 24 || task >= 26 && task <= 27)
            return value * 2;
        else if (task >= 1 && task <= 2 || task == 17 || task == 20
                || task == 30)
            return value * 4;
        else if (task == 3 || task == 12 || task == 14 || task == 16)
            return value;
        else if (task == 4 || task == 7)
            return value * 6;
        else if (task == 5 || task == 8)
            return value * 7;
        else if (task == 6 || task == 9 || task == 32)
            return value * 8;
        else if (task == 10 || task == 15 || task == 18 || task == 25 || task == 29)
            return value * 3;
        else if (task == 28)
            return value * 5;
        else if (task == 31)
            return 0;
        else throw new IllegalArgumentException();
    }

    public void convertTaskPoints() {
        for (Task task : tasks) {
            if (task != null) {
                int[] taskPoints = task.getTaskPoints();
                for (int i = 0; i < taskPoints.length; i++) {
                    task.setTaskPoints(i, convertTaskPoint(i, taskPoints[i]));
                }
            }
        }
    }

    public void getTotals(){

    }

    public Employee(int technumber, String firstName, String secondName) {
        this.technumber = technumber;
        this.firstName = firstName;
        this.secondName = secondName;
    }

    public int getTechnumber() {
        return technumber;
    }

    public void setTechnumber(int technumber) {
        this.technumber = technumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
}
