import java.util.ArrayList;
import java.util.List;

public class Dataset {
    private List<TimeSlot> timeslots;
    private List<Room> rooms;
    private List<Instructor> instructors;
    private List<Course> courses;
    private List<StudentGroup> studentGroups;

    public Dataset() {
        timeslots = new ArrayList<>();
        instructors = new ArrayList<>();
        rooms = new ArrayList<>();
        courses = new ArrayList<>();
        studentGroups = new ArrayList<>();
        createTimeSlots(); //build fixed 5days * 4periods
    }

    private void createTimeSlots() {
        List <String> days = new ArrayList<>();
        days.add("Sunday");
        days.add("Monday");
        days.add("Tuesday");
        days.add("Wednesday");
        days.add("Thursday");

        List<String> times = new ArrayList<>();
        times.add("08:00");
        times.add("09:00");
        times.add("10:00");
        times.add("11:00");

        int periodIndex=0;
        for(int i =0; i<days.size();i++) {
            String day = days.get(i);
            for(int j=0; j<times.size();j++) {
                String time = times.get(j);
                timeslots.add(new TimeSlot(day,time,periodIndex));
                periodIndex++;
            }
        }
    }

    public void addRoom(Room room){
        rooms.add(room);
    }
    public void addInstructor(Instructor instructor){
        instructors.add(instructor);
    }
    public void addCourse(Course course){
        courses.add(course);
    }
    public void addStudentGroup(StudentGroup studentGroup){
        studentGroups.add(studentGroup);
    }
    public void finalDS(){
        SlotTable.loadData(timeslots, rooms);
    }

    // used for instruction unavailabel checking
    public TimeSlot findTimeSlot(String day, String time) {
        for(int i=0; i<timeslots.size();i++) {
            TimeSlot t = timeslots.get(i);
            if(t.getDay().equals(day) && t.getTime().equals(time)){
                return t;
            }
        }
        return null;
    }

    public Instructor findInstructor(String instructorID) {
        for(int i=0; i<instructors.size();i++) {
            Instructor instructor = instructors.get(i);
            if(instructor.getId().equals(instructorID)){
                return instructor;
            }
        }
        return null;
    }

    public Room findRoom(String roomID){
        for(int i=0; i<rooms.size();i++) {
            Room room = rooms.get(i);
            if(room.getId().equals(roomID)){
                return room;
            }
        }
        return null;
    }

    public StudentGroup findStudentGroup(String groupID){
        for(int i=0; i<studentGroups.size();i++) {
            StudentGroup studentGroup = studentGroups.get(i);
            if(studentGroup.getId().equals(groupID)){
                return studentGroup;
            }
        }
        return null;
    }

    public Course findCourse(String courseID) {
        for(int i=0; i<courses.size();i++) {
            Course c = courses.get(i);
            if(c.getId().equals(courseID)){
                return c;
            }
        }
        return null;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }
    public List<Course> getCourses() {
        return courses;
    }
    public List<StudentGroup> getStudentGroups() {
        return studentGroups;
    }
    public List<TimeSlot> getTimeslots() {
        return timeslots;
    }
    public List<Room> getRooms() {
        return rooms;
    }

}
