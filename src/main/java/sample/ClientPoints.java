package sample;

import java.util.Arrays;

public class ClientPoints implements Cloneable {

    private String prin;
    private String order;
    private String job;
    private String account;
    private String name;
    private String address;
    private String compldate;
    private String code;
    private String type;
    private String tech;
    private int[] points = new int[33];
    private String eqAdded;
    private String eqRemoved;
    private String curEquip;
    private int totalPoints;

    public ClientPoints() {

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getPoint(int index) {
        return points[index];
    }

    public ClientPoints(String prin, String order, String job, String account, String name, String address, String compldate, String code, String type, String tech, int[] points, String eqAdded, String eqRemoved, String curEquip, int totalPoints) {
        this.prin = prin;
        this.order = order;
        this.job = job;
        this.account = account;
        this.name = name;
        this.address = address;
        this.compldate = compldate;
        this.code = code;
        this.type = type;
        this.tech = tech;
        this.points = points;
        this.eqAdded = eqAdded;
        this.eqRemoved = eqRemoved;
        this.curEquip = curEquip;
        this.totalPoints = totalPoints;
    }

    public String toString() {
        System.out.print(getPrin() + " ");
        System.out.print(getOrder() + " ");
        System.out.print(getJob() + " ");
        System.out.print(getAccount() + " ");
        System.out.print(getName() + " ");
        System.out.print(getAddress() + " ");
        System.out.print(getCompldate() + " ");
        System.out.print(getCode() + " ");
        System.out.print(getType() + " ");
        System.out.print(getTech() + " ");
        int[] points = getPoints();
        for (int i = 0; i < points.length; i++) {
            System.out.print(points[i] + " ");
        }
        System.out.print(getEqAdded() + " ");
        System.out.print(getEqRemoved() + " ");
        System.out.print(getCurEquip() + " ");
        System.out.print(getTotalPoints() + " ");
        return "";
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientPoints that = (ClientPoints) o;

        if (totalPoints != that.totalPoints) return false;
        if (prin != null ? !prin.equals(that.prin) : that.prin != null) return false;
        if (order != null ? !order.equals(that.order) : that.order != null) return false;
        if (job != null ? !job.equals(that.job) : that.job != null) return false;
        if (account != null ? !account.equals(that.account) : that.account != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (compldate != null ? !compldate.equals(that.compldate) : that.compldate != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (tech != null ? !tech.equals(that.tech) : that.tech != null) return false;
        if (!Arrays.equals(points, that.points)) return false;
        if (eqAdded != null ? !eqAdded.equals(that.eqAdded) : that.eqAdded != null) return false;
        if (eqRemoved != null ? !eqRemoved.equals(that.eqRemoved) : that.eqRemoved != null) return false;
        return curEquip != null ? curEquip.equals(that.curEquip) : that.curEquip == null;

    }

    @Override
    public int hashCode() {
        int result = prin != null ? prin.hashCode() : 0;
        result = 31 * result + (order != null ? order.hashCode() : 0);
        result = 31 * result + (job != null ? job.hashCode() : 0);
        result = 31 * result + (account != null ? account.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (compldate != null ? compldate.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (tech != null ? tech.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(points);
        result = 31 * result + (eqAdded != null ? eqAdded.hashCode() : 0);
        result = 31 * result + (eqRemoved != null ? eqRemoved.hashCode() : 0);
        result = 31 * result + (curEquip != null ? curEquip.hashCode() : 0);
        result = 31 * result + totalPoints;
        return result;
    }

    public String getPrin() {
        return prin;
    }

    public void setPrin(String prin) {
        this.prin = prin.trim();
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order.trim();
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address.trim();
    }

    public String getCompldate() {
        return compldate;
    }

    public void setCompldate(String compldate) {
        this.compldate = compldate.trim();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type.trim();
    }

    public String getTech() {
        return tech;
    }

    public void setTech(String tech) {
        this.tech = tech.trim();
    }

    public int[] getPoints() {
        return points;
    }

    public void setPoints(int[] points) {
        this.points = points;
    }

    public String getEqAdded() {
        return eqAdded;
    }

    public void setEqAdded(String eqAdded) {
        this.eqAdded = eqAdded.trim();
    }

    public String getEqRemoved() {
        return eqRemoved;
    }

    public void setEqRemoved(String eqRemoved) {
        this.eqRemoved = eqRemoved.trim();
    }

    public String getCurEquip() {
        return curEquip;
    }

    public void setCurEquip(String curEquip) {
        this.curEquip = curEquip.trim();
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

}
