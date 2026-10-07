
// ROOMS = id,capacity,type
// INSTRUCTORS = id,name,preferMorn,unavailablSlots
// COURSES = id,name,numStudents,insID,type
// GROUPS = id,courseID1,courseID2..

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataLoader {
    private static List<String> errors = new ArrayList<>();
    public static List<String> getErrors(){
        return errors;
    }

    public static Dataset loadFile(String filename) throws IOException {
        errors.clear();
        Dataset dataset = new Dataset();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        String currentSce = "";
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.charAt(0) == '#'){
                currentSce = line.substring(1).trim().toUpperCase();
                continue;
            }
            switch (currentSce){
                case "ROOMS":
                    String[] roomParts = line.split(",");
                    dataset.addRoom(new Room(roomParts[0].trim(), Integer.parseInt(roomParts[1].trim()), roomParts[2].trim()));
                    break;
                case "INSTRUCTORS":
                    String[] instructorParts = line.split(",");
                    Instructor instructor = new Instructor(instructorParts[0].trim(), instructorParts[1].trim(),Boolean.parseBoolean(instructorParts[2].trim()));

                    // optional for tims inst cant teach
                    if(instructorParts.length > 3){
                        String unavailableF = instructorParts[3].trim();
                        if(!unavailableF.equals("-") && !unavailableF.isEmpty()){
                            String[] unavailable =  unavailableF.split(";");
                            for(int i=0; i<unavailable.length; i++){
                                String[] dayTime= unavailable[i].split("-");
                                if(dayTime.length == 2){
                                    TimeSlot timeSlot = dataset.findTimeSlot(dayTime[0].trim(), dayTime[1].trim());
                                    if(timeSlot != null){
                                        instructor.addUnavailableSlot(timeSlot);
                                    }else{
                                        errors.add("Instructor has invalid unavailable time "+ unavailable[i]);
                                    }
                                }
                            }
                        }
                    }
                    dataset.addInstructor(instructor);
                    break;
                case "COURSES":
                    String[] courseParts = line.split(",");
                    String id = courseParts[0].trim();
                    Instructor instructor1 = dataset.findInstructor(courseParts[3].trim());
                    if(instructor1 == null){
                        throw new IllegalArgumentException("Course "+ id + " has unknown instructor "+ courseParts[3].trim());
                    }
                    dataset.addCourse(new Course(id, courseParts[1].trim(), Integer.parseInt(courseParts[2].trim()), instructor1, courseParts[4].trim()));
                    break;
                case "GROUPS":
                    String[] groupParts = line.split(",");
                    StudentGroup group = new  StudentGroup(groupParts[0].trim());
                    if(groupParts.length > 1){
                        String[] courseIds =  groupParts[1].trim().split(";");
                        for(int i = 0; i< courseIds.length; i++){
                            String courseId = courseIds[i].trim();
                            Course course = dataset.findCourse(courseId);
                            if(course != null){
                                group.addCourse(course);
                            }else{
                                errors.add("Group "+group.getId()+ " to unknown course "+ courseIds[i].trim());
                            }
                        }
                    }
                    dataset.addStudentGroup(group);
                    break;
                default:
                    break;
            }
        }
        br.close();
        if(dataset.getRooms().isEmpty()) throw new IllegalArgumentException("No rooms in file");
        if(dataset.getInstructors().isEmpty()) throw new IllegalArgumentException("No instructors in file");
        if(dataset.getCourses().isEmpty()) throw new IllegalArgumentException("No Courses in file");

        dataset.finalDS();
        return dataset;
    }

    public static void saveFile(Dataset dataset, String filename) throws IOException {
        try(FileWriter fw = new FileWriter(filename)) {
            fw.write("#ROOMS\n");
            List<Room> rooms = dataset.getRooms();
            for(int i = 0; i < rooms.size(); i++){
                Room room = rooms.get(i);
                fw.write(room.getId()+","+ room.getCapacity()+","+room.getType()+"\n");
            }

            fw.write("#INSTRUCTORS\n");
            List<Instructor> instructors = dataset.getInstructors();
            for(int i = 0; i < instructors.size(); i++){
                Instructor instructor = instructors.get(i);
                String unavailable = "-";
                List<TimeSlot> timeSlots = instructor.getUnAvailableSlots();
                if(!timeSlots.isEmpty()){
                    StringBuilder sb =  new StringBuilder();
                    for(int j = 0; j < timeSlots.size(); j++){
                        if(j>0) sb.append(";");
                        sb.append(timeSlots.get(j).getDay()).append("-").append(timeSlots.get(j).getTime());
                    }
                    unavailable = sb.toString();
                }
                fw.write(instructor.getId()+","+instructor.getName()+","+instructor.preferMorn()+","+unavailable+"\n");
            }

            fw.write("#COURSES\n");
            List<Course> courses = dataset.getCourses();
            for(int i = 0; i < courses.size(); i++){
                Course course = courses.get(i);
                fw.write(course.getId()+","+course.getName()+","+course.getNumOfStudents()+","+course.getInstructor().getId()+","+course.getType()+"\n");
            }

            fw.write("#GROUPS\n");
            List<StudentGroup> studentGroups = dataset.getStudentGroups();
            for(int i = 0; i < studentGroups.size(); i++){
                StudentGroup studentGroup = studentGroups.get(i);
                List<Course> groupCourses = studentGroup.getCourses();
                StringBuilder sb =  new StringBuilder();
                for(int j = 0; j < groupCourses.size(); j++){
                    if(j>0) sb.append(";");
                    sb.append(groupCourses.get(j).getId());
                }
                fw.write(studentGroup.getId()+","+sb.toString()+"\n");
            }
        }
    }


}
