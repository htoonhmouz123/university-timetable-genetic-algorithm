import java.util.List;

// Chromosome: [12, 25, 07, 31, 18, 42, 09, 15, 36, 21]
// For example: Slot 12 → Monday 09:00 R101,
public class SlotTable {
    private static List<TimeSlot> timeSlots;
    private static List<Room> rooms;

    // it setup slottable by giving it list of timeSlots  rooms
    public static void loadData(List<TimeSlot> timeSlotList, List<Room> roomList){
        timeSlots = timeSlotList;
        rooms = roomList;
    }

    public static int totalSlots(){
        return timeSlots.size()*rooms.size();
    }

    // takes timeSlot+room --> integer
    public static int convertToIndex(TimeSlot timeSlot, Room room){ //O(n)
        int tIndex = timeSlots.indexOf(timeSlot);
        int rIndex = rooms.indexOf(room);
        return (tIndex*rooms.size())+ rIndex;
    }

    public static TimeSlot convertToTS(int slotindex){
        int timeIndex = slotindex/rooms.size();
        return  timeSlots.get(timeIndex);
    }

    public static Room convertToRoom(int slotindex){
        int roomIndex = slotindex%rooms.size();
        return  rooms.get(roomIndex);
    }

    public static List<TimeSlot> getTimeSlots(){
        return timeSlots;
    }
    public static List<Room> getRooms(){
        return rooms;
    }


}
