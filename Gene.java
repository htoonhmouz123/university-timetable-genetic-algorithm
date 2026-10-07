public class Gene {
    private Course course;
    private int slotIndex;

    public Gene(Course course, int slotIndex) {
        this.course = course;
        this.slotIndex = slotIndex;
    }

    public Gene(Course course, TimeSlot timeSlot, Room room) {
        this.course = course;
        this.slotIndex = SlotTable.convertToIndex(timeSlot, room);// convert timeslot+room into slot
    }

    public Course getCourse() {
        return course;
    }

    public TimeSlot getTimeslot() {
        return SlotTable.convertToTS(slotIndex);
    }
    public Room getRoom() {
        return SlotTable.convertToRoom(slotIndex);
    }
    public int getSlotIndex() {
        return slotIndex;
    }

    public Gene copy(){ // create new gene with sam info
        return new Gene(course, slotIndex);
    }

    @Override
    public String toString() {
        return "(" + course.getId()+ "," + getTimeslot() + ", " + getRoom().getId() + ")";
    }

    //For example: Gene = (Monday, 10:00, R102).
    // The chromosome becomes: [(Sun,08,R101), (Sun,09,R102), (Mon,10,R103), (Tue,11,R101),
    public String toString2(){
        TimeSlot timeSlot = getTimeslot();
        return "(" + timeSlot.getDay().substring(0,3) + "," + timeSlot.getTime() + "," +getRoom().getId() + ")";
    }

}
