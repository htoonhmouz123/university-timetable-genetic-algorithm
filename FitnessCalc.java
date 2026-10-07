import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// check chromosome all 6 HardViolations & 5 softViolations
public class FitnessCalc {
    private int WHARD = 100;
    private int WSOFT = 10;


    public FitnessCalc(){}
    public FitnessCalc(List<Course> courses, int WHARD, int WSOFT){
        this.WHARD = WHARD;
        this.WSOFT = WSOFT;
    }

    public FitnessResult evaluate(Chromosome cm) {
        int hardViolations =0;
        int softViolations =0;
        List<Gene> genes = cm.getGenes();

        // Hard Constraints
        for(int i=0; i<genes.size();i++) { // check conflicts between pairs of courses o(n^2)
            Gene g1 = genes.get(i);
            Course c1 = g1.getCourse();
            for (int j = i + 1; j < genes.size(); j++) {
                Gene g2 = genes.get(j);
                Course c2 = g2.getCourse();

                if (sameTime(g1.getTimeslot(), g2.getTimeslot())) { // only courses at same day/time can conflict
                    if (c1.getInstructor().getId().equals(c2.getInstructor().getId())) {// H1 = instructor confilct
                        hardViolations++;
                    }
                    if (g1.getRoom().getId().equals(g2.getRoom().getId())) {// H2 = room conflict
                        hardViolations++;
                    }
                    if (commonStuGroup(c1, c2)) {// H3 = same studentGroup at same time
                        hardViolations++;
                    }
                }
            }
        }
        // H4 = Room capacity
        for(int i=0; i<genes.size();i++) {
            Gene gene = genes.get(i);
            Course course = gene.getCourse();
            Room room = gene.getRoom();
            if(room.getCapacity() < course.getNumOfStudents()) {
                hardViolations++;
            }
        }
        // H5 = Room type
        for(int i=0; i<genes.size();i++) {
            Gene gene = genes.get(i);
            Course course = gene.getCourse();
            Room room = gene.getRoom();
            if(!room.getType().equalsIgnoreCase(course.getType())) {
                hardViolations++;
            }
        }
        // H6 = Instructor availability
        for(int i=0; i<genes.size();i++) {
            Gene gene = genes.get(i);
            Course course = gene.getCourse();
            Instructor instructor = course.getInstructor();
            if(!instructor.isAvailable(gene.getTimeslot())) {
                hardViolations++;
            }
        }

        // Soft Constraints
        // S1 = avoid early classes
        for(int i=0; i<genes.size();i++) {
            if(genes.get(i).getTimeslot().getPeriodInDay() ==0){
                softViolations++;
            }
        }
        // S2 = avoid late classes
        for(int i=0; i<genes.size();i++) {
            if(genes.get(i).getTimeslot().getPeriodInDay() == 3){
                softViolations++;
            }
        }
        // S3 = minimize Student gaps
        softViolations = softViolations + calcStugroupGaps(cm);
        // S4 = instructor prefermorn but course is late
        for(int i=0; i<genes.size();i++) {
            Gene gene = genes.get(i);
            Instructor instructor = gene.getCourse().getInstructor();
            if(instructor.preferMorn() && gene.getTimeslot().getPeriodInDay() >=2 ) {
                softViolations++;
            }
        }
        // S5 = minimize num of working days
        softViolations = softViolations + calcStuGroupWorkingDayPenalty(cm);

        //FITNESS
        int penalty = (WHARD*hardViolations) + (WSOFT*softViolations);
        double fitness = 1.0/ (1+penalty);
        return new FitnessResult(hardViolations, softViolations, penalty,fitness);

    }

    private boolean sameTime(TimeSlot t1, TimeSlot t2) {
        return t1.getDay().equals(t2.getDay()) && t1.getTime().equals(t2.getTime());
    }

    //check if 2 courses share at least one student group
    private boolean commonStuGroup(Course c1, Course c2) {
        List<StudentGroup> group1 = c1.getGroups();
        List<StudentGroup> group2 = c2.getGroups();
        for(int i=0; i<group1.size();i++) {
            StudentGroup firstGroup = group1.get(i);
            for(int j=0; j<group2.size();j++) {
                StudentGroup secondGroup = group2.get(j);
                if(firstGroup.getId().equals(secondGroup.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private int calcStugroupGaps(Chromosome cm) {
        int totalGaps =0;
        List<StudentGroup> groups = getAllGroups(cm);
        List<Gene> genes = cm.getGenes();
        for(int i=0; i<groups.size();i++) {
            StudentGroup group = groups.get(i);

            // find days that this group has course
            List<String> days = new ArrayList<>();
            for(int j=0; j<genes.size(); j++) {
                Gene gene = genes.get(j);
                if(courseBelongGroup(gene.getCourse(),group)){
                    String day = gene.getTimeslot().getDay();
                    if(!days.contains(day)) {
                        days.add(day);
                    }
                }
            }
            //check each day separately (periods in day), then count gaps
            for(int k=0; k<days.size();k++) {
                String day = days.get(k);
                List<Integer> periods = new ArrayList<>();
                for(int s=0; s<genes.size(); s++) {
                    Gene gene = genes.get(s);
                    if(courseBelongGroup(gene.getCourse(), group) && gene.getTimeslot().getDay().equals(day)){
                        int period = gene.getTimeslot().getPeriodInDay();
                        if(!periods.contains(period)) {
                            periods.add(period);
                        }
                    }
                }
                if( periods.size() > 1 ) {
                    Collections.sort(periods); // sort periods to find gaps
                    for(int l=0; l<periods.size()-1; l++) {
                        int current =  periods.get(l);
                        int next = periods.get(l+1);

                        if(next - current > 1) { //means there is at least one empty period btw classes
                            totalGaps += next -current -1;
                        }
                    }
                }
            }
        }
        return totalGaps;
    }

    // all student group appear in chromosome
    private List<StudentGroup> getAllGroups(Chromosome cm) {
        List<StudentGroup> groups = new ArrayList<>();
        List<Gene> genes = cm.getGenes();
        for(int i=0; i<genes.size();i++) {
            Course course = genes.get(i).getCourse();
            List<StudentGroup> courseGroups = course.getGroups();
            for(int j=0; j<courseGroups.size(); j++) {
                StudentGroup group = courseGroups.get(j);
                if(!containsGroup(groups, group)) {
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    //check if in the group list there is this group
    private boolean containsGroup(List<StudentGroup> groups, StudentGroup targetGroup) {
        for(int i=0; i<groups.size();i++) {
            if(groups.get(i).getId().equals(targetGroup.getId())) {
                return true;
            }
        }
        return false;
    }
    private boolean courseBelongGroup(Course course, StudentGroup targetGroup) { // check if course belongs to group
        List<StudentGroup> courseGroups = course.getGroups();
        for(int  i=0; i<courseGroups.size();i++) {
            if(courseGroups.get(i).getId().equals(targetGroup.getId())) {
                return true;
            }
        }
        return false;
    }

    // if student group has more than 4 days , then add penalty per extra day
    private int calcStuGroupWorkingDayPenalty(Chromosome cm) {
        int penalty = 0;
        List<StudentGroup> groups = getAllGroups(cm);
        List<Gene>  genes = cm.getGenes();
        for(int i =0; i<groups.size();i++) {
            StudentGroup group = groups.get(i);
            List<String> workingDays = new ArrayList<>();
            for(int j=0; j<genes.size(); j++) {
                Gene gene = genes.get(j);
                if(courseBelongGroup(gene.getCourse(), group)) {
                    String day = gene.getTimeslot().getDay();
                    if(!workingDays.contains(day)) {
                        workingDays.add(day);
                    }
                }
            }
            if(workingDays.size() > 4) {
                penalty += workingDays.size() - 4;
            }
        }
        return penalty;
    }
}
