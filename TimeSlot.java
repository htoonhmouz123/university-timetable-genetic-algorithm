//represent day & time(teaching period) + the room teaching in
public class TimeSlot {
    private String day;
    private String time;
    private int slotNum; // 0-19 (5 days* 4 periods =20)
    private int periodInDay; // 0=8AM, 1=9AM..

    public TimeSlot(String day, String time, int slotNum) {
        this.day = day;
        this.time = time;
        this.slotNum = slotNum;
        this.periodInDay = slotNum%4; // find the period (0-3)
    }
    public String getDay() {
        return day;
    }
    public String getTime() {
        return time;
    }
    public int getSlotNum() {
        return slotNum;
    }
    public int getPeriodInDay() {
        return periodInDay;
    }

    @Override
    public String toString(){
        return day+ "-" +time;
    }

}
