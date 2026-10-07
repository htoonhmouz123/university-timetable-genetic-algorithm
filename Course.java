import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private String name;
    private int numOfStudents;   // H4 = room capacity checking
    private Instructor instructor;
    private String type;        //H5 = lecture / lab
    private List<StudentGroup> groups; // course may be required by more than one group

    public Course(String id, String name, int numOfStudents, Instructor instructor, String type) {
        this.id = id;
        this.name = name;
        this.numOfStudents = numOfStudents;
        this.instructor = instructor;
        this.type = type;
        this.groups=new ArrayList<>();
    }

    // link group to course & course to group
    public void addGroup(StudentGroup group) {
        if(!groups.contains(group)) {
            groups.add(group);
        }
        if(!group.getCourses().contains(this)){
            group.getCourses().add(this);
        }
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getNumOfStudents() {
        return numOfStudents;
    }
   public Instructor getInstructor() {
        return instructor;
   }
    public String getType() {
        return type;
    }
    public List<StudentGroup> getGroups() {
        return groups;
    }
    @Override
    public String toString() {
        return id+" - " +name;
    }
}
