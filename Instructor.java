import java.util.ArrayList;
import java.util.List;
public class Instructor {
    private String id;
    private String name;
    private List<TimeSlot> unAvailableSlots; // slots inst cannot teach at
    private boolean preferMorn;

    public  Instructor(String id, String name, boolean preferMorn){
        this.id = id;
        this.name = name;
        this.preferMorn = preferMorn;
        unAvailableSlots = new ArrayList<>();
    }

    // H6 = instructor cant be during time they said is unavailable
    public void addUnavailableSlot(TimeSlot timeSlot){
        unAvailableSlots.add(timeSlot);
    }

    // H1 = instructor cant teach 2 courses at same time
    public boolean isAvailable(TimeSlot timeSlot){  // if we found same day & time , inst not available
        for(int i=0; i<unAvailableSlots.size();i++){
            TimeSlot unavailable = unAvailableSlots.get(i);
            if(unavailable.getDay().equals(timeSlot.getDay()) && unavailable.getTime().equals(timeSlot.getTime())){
                return false;
            }
        }
        return true;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean preferMorn() {   // S4 = instructor prefers morning
        return preferMorn;
    }
    public List<TimeSlot> getUnAvailableSlots() {
        return unAvailableSlots;
    }
    @Override
    public String toString() {
        return name;
    }
}
