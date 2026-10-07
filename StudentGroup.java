import java.util.ArrayList;
import java.util.List;

public class StudentGroup {
    private String id;
    private List<Course> courses;

    public StudentGroup(String id) {
        this.id = id;
        this.courses = new ArrayList<Course>();
    }

    // link group to course & course to group
    public void addCourse(Course course) {
        if(!courses.contains(course)) {
            courses.add(course);
        }
        if(!course.getGroups().contains(this)){
            course.addGroup(this);
        }
    }

    public String getId() {
        return id;
    }
    public List<Course> getCourses() {
        return courses;
    }
    @Override
    public String toString() {
        return id;
    }
}
