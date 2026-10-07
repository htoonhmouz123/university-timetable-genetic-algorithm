import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class EnterData {
    private Dataset dataset,result;
    private ComboBox<Instructor> instCombo;
    private ComboBox<Course> courseCombo;
    private ListView<String> roomList;
    private ListView<String> instructorList;
    private ListView<String> coursesList;
    private ListView<String> groupsList;

  public Dataset create(Stage primaryStage){
      return create(primaryStage, null);
  }

  public Dataset create(Stage primaryStage, Dataset existingDS){
      if(existingDS != null){
          dataset = existingDS;
      }else{
          dataset = new Dataset();
      }
      result = null;
      Stage stage = new Stage();
      stage.setTitle("Add Data Manually");

      TabPane tabPane = new TabPane();
      Tab roomsTab = new Tab("Rooms", createrooms());
      Tab instructorTab = new Tab("Instructor", createinstrs());
      Tab coursesTab = new Tab("Courses",createcourses());
      Tab groupsTab = new Tab("Groups",creategroups());
      tabPane.getTabs().addAll(roomsTab,instructorTab,coursesTab,groupsTab);

      for(int i=0; i<dataset.getRooms().size(); i++){
          Room r = dataset.getRooms().get(i);
          roomList.getItems().add(r.getId()+" | Capacity: "+r.getCapacity()+" | "+r.getType());
      }
      for(int i=0; i<dataset.getInstructors().size(); i++){
          Instructor ins = dataset.getInstructors().get(i);
          instructorList.getItems().add(ins.getId()+" - "+ins.getName());
      }
      instCombo.setItems(FXCollections.observableArrayList(dataset.getInstructors()));
      for(int i=0; i<dataset.getCourses().size(); i++){
          Course c = dataset.getCourses().get(i);
          coursesList.getItems().add(c.toString()+" - "+c.getName());
      }
      courseCombo.setItems(FXCollections.observableArrayList(dataset.getCourses()));
      for(int i=0; i<dataset.getStudentGroups().size(); i++){
          StudentGroup g = dataset.getStudentGroups().get(i);
          groupsList.getItems().add(g.getId());
      }
      Button donebtn = new Button("Done");
      Button cancelbtn = new Button("Cancel");
      HBox hBox = new HBox(10);
      hBox.setPadding(new Insets(10));
      hBox.getChildren().addAll(donebtn,cancelbtn);

      BorderPane borderPane = new BorderPane();
      borderPane.setCenter(tabPane);
      borderPane.setBottom(hBox);

      donebtn.setOnAction(e -> {
          if(dataset.getRooms().isEmpty()){
            new Alert(Alert.AlertType.ERROR,"Please add room").show();
            return;
          }
          if(dataset.getInstructors().isEmpty()){
              new Alert(Alert.AlertType.ERROR,"Please add instructor").show();
              return;
          }
          if(dataset.getCourses().isEmpty()){
              new Alert(Alert.AlertType.ERROR,"Please add course").show();
              return;
          }
          dataset.finalDS();
          result = dataset;
          stage.close();
      });
      cancelbtn.setOnAction(e -> {
          result = null;
          stage.close();
      });
      Scene scene = new Scene(borderPane,800,750);
      stage.setScene(scene);
      stage.showAndWait();
      return result;
  }

  private Pane createrooms(){
      TextField idF = new TextField();
      TextField capacityF = new TextField();
      ComboBox<String> roomTypeBox = new ComboBox<>(FXCollections.observableArrayList("Lecture","Lab"));
      roomTypeBox.setValue("Lecture");
      Button addBtn = new Button("Add Room");
      roomList =  new ListView<>();
      addBtn.setOnAction(e -> {
          String id = idF.getText().trim();
          String capacity = capacityF.getText().trim();
          if(id.isEmpty() || capacity.isEmpty()){
              new Alert(Alert.AlertType.ERROR,"Please enter room ID and capacity").show();
              return;
          }
          if(dataset.findRoom(id)!=null){
              new Alert(Alert.AlertType.ERROR,"Room already exists").show();
              return;
          }
          int capacity2;
          try {
              capacity2 = Integer.parseInt(capacity);
          }catch (NumberFormatException ex){
              new Alert(Alert.AlertType.ERROR, "capacity must be a valid number").show();
              return;
          }
          Room room = new Room(id,capacity2,roomTypeBox.getValue());
          dataset.addRoom(room);
          roomList.getItems().add(room.getId() +" | Capacity: "+room.getCapacity()+ " | "+room.getType());
          idF.clear();
          capacityF.clear();
      });
      GridPane gpane = new GridPane();
      gpane.setHgap(10);
      gpane.setVgap(10);
      gpane.setPadding(new Insets(15));
      gpane.add(new Label("Room ID:"),0,0);
      gpane.add(idF,1,0);
      gpane.add(new Label("Capacity:"),0,1);
      gpane.add(capacityF,1,1);
      gpane.add(new Label("Type:"),0,2);
      gpane.add(roomTypeBox,1,2);
      gpane.add(addBtn,1,3);

      BorderPane borderPane = new BorderPane();
      borderPane.setTop(gpane);
      borderPane.setCenter(roomList);
      return borderPane;
  }

    private Pane createinstrs(){
        TextField idF = new TextField();
        TextField nameF = new TextField();
        CheckBox prefMorn = new CheckBox("Prefers Morning");
        ComboBox<String> dayCombo = new ComboBox<>(FXCollections.observableArrayList("Sunday","Monday","Tuesday","Wednesday","Thursday"));
        dayCombo.setValue("Sunday");
        ComboBox<String> timeCombo = new ComboBox<>(FXCollections.observableArrayList("08:00","09:00","10:00","11:00"));
        timeCombo.setValue("08:00");
        List<TimeSlot> unavailableslots = new ArrayList<>();
        ListView<String> unavailableList = new ListView<>();
        unavailableList.setPrefHeight(100);

        Button unavailableBtn = new Button("Add Unavailable Time");
        unavailableBtn.setOnAction(e -> {
            String day = dayCombo.getValue();
            String time = timeCombo.getValue();
            TimeSlot timeSlot = dataset.findTimeSlot(day,time);
            if(timeSlot != null){
                if(!unavailableslots.contains(timeSlot)){
                    unavailableslots.add(timeSlot);
                    unavailableList.getItems().add(day+ " "+time);
                }
            }
        });

        Button addInstBtn = new Button("Add Instructor");
        instructorList =  new ListView<>();
        addInstBtn.setOnAction(e -> {
            String id = idF.getText().trim();
            String name = nameF.getText().trim();
            if(id.isEmpty() || name.isEmpty()){
                new Alert(Alert.AlertType.ERROR,"Please enter instructor ID and Name").show();
                return;
            }
            if(dataset.findInstructor(id)!=null){
                new Alert(Alert.AlertType.ERROR,"Instructor already exists").show();
                return;
            }
            Instructor instructor = new Instructor(id, name,prefMorn.isSelected());
            for(int i=0; i<unavailableslots.size();i++){
                instructor.addUnavailableSlot(unavailableslots.get(i));
            }
            dataset.addInstructor(instructor);
            instructorList.getItems().add(instructor.getId() +" - "+instructor.getName());
            instCombo.setItems(FXCollections.observableArrayList(dataset.getInstructors()));
            idF.clear();
            nameF.clear();
            prefMorn.setSelected(false);
            unavailableslots.clear();
            unavailableList.getItems().clear();
        });
        GridPane gpane = new GridPane();
        gpane.setHgap(10);
        gpane.setVgap(10);
        gpane.setPadding(new Insets(15));
        gpane.add(new Label("Instructor ID:"),0,0);
        gpane.add(idF,1,0);
        gpane.add(new Label("Name:"),0,1);
        gpane.add(nameF,1,1);
        gpane.add(prefMorn,1,2);
        gpane.add(new Label("Unavailable Day:"),0,3);
        gpane.add(dayCombo,1,3);
        gpane.add(new Label("Unavailable Time:"),0,4);
        gpane.add(timeCombo,1,4);
        gpane.add(unavailableBtn,1,5);
        gpane.add(new Label("Unavailable Times:"),0,6);
        gpane.add(unavailableList,1,6);
        gpane.add(addInstBtn,1,7);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(gpane);
        borderPane.setCenter(instructorList);
        return borderPane;
    }

    private Pane createcourses(){
        TextField idF = new TextField();
        TextField nameF = new TextField();
        TextField studentF = new TextField();
        instCombo = new ComboBox<>();

        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Lecture", "Lab"));
        typeBox.setValue("Lecture");

        Button addBtn = new Button("Add course");
        coursesList =  new ListView<>();
        addBtn.setOnAction(e -> {
            String id = idF.getText().trim();
            String name = nameF.getText().trim();
            String student = studentF.getText().trim();
            Instructor instructor = instCombo.getValue();
           if(id.isEmpty() || name.isEmpty() || student.isEmpty() || instructor == null){
               new Alert(Alert.AlertType.ERROR,"Please enter aal fields").show();
               return;
           }
           if(dataset.findCourse(id)!=null){
               new Alert(Alert.AlertType.ERROR,"Course already exists").show();
               return;
           }
           int students;
           try{
               students = Integer.parseInt(student);
           }catch(NumberFormatException nfe){
               new Alert(Alert.AlertType.ERROR,"Number of student should be valid number").show();
               return;
           }

           Course course = new Course(id,name,students,instructor,typeBox.getValue());
           dataset.addCourse(course);
           coursesList.getItems().add(course.toString());
           courseCombo.setItems(FXCollections.observableArrayList(dataset.getCourses()));
           idF.clear();
           nameF.clear();
           studentF.clear();
        });
        GridPane gpane = new GridPane();
        gpane.setHgap(10);
        gpane.setVgap(10);
        gpane.setPadding(new Insets(15));
        gpane.add(new Label("Course ID:"),0,0);
        gpane.add(idF,1,0);
        gpane.add(new Label("Course Name:"),0,1);
        gpane.add(nameF,1,1);
        gpane.add(new Label("Number of Students:"),0,2);
        gpane.add(studentF,1,2);
        gpane.add(new Label("Instructor:"),0,3);
        gpane.add(instCombo,1,3);
        gpane.add(new Label("Type:"),0,4);
        gpane.add(typeBox,1,4);
        gpane.add(addBtn,1,5);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(gpane);
        borderPane.setCenter(coursesList);
        return borderPane;
    }

    private Pane creategroups(){
        TextField idF = new TextField();
        courseCombo = new ComboBox<>();

       List<Course> selected = new ArrayList<>();
       ListView<String> selectedcourseList = new ListView<>();

        Button addBtn = new Button("Add course");
        addBtn.setOnAction(e -> {
         Course course = courseCombo.getValue();
            if(course == null){
                new Alert(Alert.AlertType.ERROR,"Please choose course").show();
                return;
            }
           if(!selected.contains(course)){
               selected.add(course);
               selectedcourseList.getItems().add(course.toString());
           }
        });
        Button addGroupBtn = new Button("Add group");
        groupsList = new ListView<>();
        addGroupBtn.setOnAction(e -> {
           String id = idF.getText().trim();
           if(id.isEmpty()){
               new Alert(Alert.AlertType.ERROR,"Please enter group ID").show();
               return;
           }
           if(dataset.findStudentGroup(id)!=null){
               new Alert(Alert.AlertType.ERROR,"Group already exists").show();
               return;
           }
           StudentGroup studentGroup = new StudentGroup(id);
           for(int i=0; selected.size()!=i; i++){
               studentGroup.addCourse(selected.get(i));
           }
           dataset.addStudentGroup(studentGroup);
           groupsList.getItems().add(studentGroup.getId());
           idF.clear();
           selected.clear();
           selectedcourseList.getItems().clear();
        });

        GridPane gpane = new GridPane();
        gpane.setHgap(10);
        gpane.setVgap(10);
        gpane.setPadding(new Insets(15));
        gpane.add(new Label("Group ID:"),0,0);
        gpane.add(idF,1,0);
        gpane.add(new Label("Course:"),0,1);
        gpane.add(courseCombo,1,1);
        gpane.add(addBtn,1,2);
        gpane.add(new Label("Group courses:"),0,3);
        gpane.add(selectedcourseList,1,3);
        gpane.add(addGroupBtn,1,4);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(gpane);
        borderPane.setCenter(groupsList);
        return borderPane;
    }



}
