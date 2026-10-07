import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class App extends Application {

    private Dataset dataset;
    private GA ga1;
    private Chromosome chromosome1;

    private TableView<TimeTable> timeTable;
    private LineChart<Number, Number> chart;
    private Label l1,l2;
    private CheckBox compare;

    private TextField populationF, generationsF, crossoverRateF, mutationRateF, elitismF;
    private ComboBox<String> selectionBOX, crossoverBOX, mutationBOX;

    public static class TimeTable {
        private final String day;
        private final String time;
        private final String course;
        private final String instructor;
        private final String room;

        public TimeTable(String day, String time, String course, String instructor, String room) {
            this.day = day;
            this.time = time;
            this.course = course;
            this.instructor = instructor;
            this.room = room;
        }

        public String getDay() {
            return day;
        }
        public String getTime() {
            return time;
        }
        public String getCourse() {
            return course;
        }
        public String getInstructor() {
            return instructor;
        }
        public String getRoom() {
            return room;
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Scheduling - Genetic Algorithm");

        Button dataBtn = new Button("Add data");
        dataBtn.setOnAction(event -> {
            EnterData enterData = new EnterData();
            Dataset result = enterData.create(primaryStage, dataset);
            if(result != null){
                dataset = result;
                l1.setText("Courses: "+dataset.getCourses().size()+ " | Rooms: "+dataset.getRooms().size()+" | Instructors:"+dataset.getInstructors().size());
            }
        });

        Button loadBtn =  new Button("Load File");
        loadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if(file == null) return;
            try{
                dataset = DataLoader.loadFile(file.getAbsolutePath());
                l1.setText("Courses: "+ dataset.getCourses().size() + " | Rooms: "+dataset.getRooms().size()+ " | Instructors: "+dataset.getInstructors().size());
                if(!DataLoader.getErrors().isEmpty()){
                    new Alert(Alert.AlertType.WARNING, DataLoader.getErrors()+"").show();
                }
            }catch (IOException ex){
                new Alert(Alert.AlertType.ERROR, "Load failed" + ex.getMessage(), ButtonType.OK).show();
            }catch(Exception ex){
                new Alert(Alert.AlertType.ERROR, "Invalid data file"+ ex.getMessage(), ButtonType.OK).show();
            }
        });

        Button saveDSBtn = new Button("Save DataSet");
        saveDSBtn.setOnAction(event -> {
           if(dataset == null){
               new Alert(Alert.AlertType.ERROR, "Dataset is null", ButtonType.OK).show();
               return;
           }
           FileChooser fileChooser = new FileChooser();
           fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
           fileChooser.setInitialFileName("loadData.txt");
           File file = fileChooser.showSaveDialog(primaryStage);
           if(file == null) return;
           try{
               DataLoader.saveFile(dataset, file.getAbsolutePath());
               new Alert(Alert.AlertType.INFORMATION, "Dataset saved", ButtonType.OK).show();
           }catch (IOException ex){
               new Alert(Alert.AlertType.ERROR, "Save failed", ButtonType.OK).show();
           }
        });

        compare = new CheckBox("Compare HC & GA");
        Button runBtn =  new Button("Run");
        runBtn.setOnAction(e -> {
            if(dataset == null || dataset.getCourses().isEmpty()){
                new Alert(Alert.AlertType.WARNING, "please load data", ButtonType.OK).show();
                return;
            }
            int populationSize, generations,elitismCount;
            double croosoverRate, mutationRate;
            try{
                populationSize = Integer.parseInt(populationF.getText().trim());
                generations = Integer.parseInt(generationsF.getText().trim());
                croosoverRate = Double.parseDouble(crossoverRateF.getText().trim());
                mutationRate = Double.parseDouble(mutationRateF.getText().trim());
                int elitism2 = Integer.parseInt(elitismF.getText().trim());
                elitismCount = Math.max(1, populationSize*elitism2/100);
            }catch (NumberFormatException ex){
                new Alert(Alert.AlertType.ERROR, "Invalid entered parameters", ButtonType.OK).show();
                return;
            }
            boolean useRolette = selectionBOX.getValue().equals("Roulette Wheel");
            boolean use1point = crossoverBOX.getValue().equals("ONE-point");
            boolean useSwap = mutationBOX.getValue().equals("Swap");
            dataset.finalDS();
            GA ga = new GA(dataset.getCourses(), dataset.getTimeslots(), dataset.getRooms(), populationSize, generations, croosoverRate, mutationRate, elitismCount,System.currentTimeMillis(), useRolette, use1point, useSwap);
            Chromosome best = ga.run();
            FitnessResult result = ga.evaluate(best);

            if(compare.isSelected()) {
                HillClimbing hc = new HillClimbing(dataset.getCourses(), dataset.getTimeslots(), dataset.getRooms(), 2500, 42);
                Chromosome hcBest = hc.run();
                showCompareChart(ga.getBestFitnessHistory(), hc.getFitnessHistory());
            }else{
                showChart(ga.getBestFitnessHistory());
            }
            ga1 = ga;
            chromosome1 = best;

            showTimeTable(best);
            showStats(best, result, ga);
        });

        Button saveBtn =  new Button("Save File");
        saveBtn.setOnAction(e -> {
            if(chromosome1 == null){
                new Alert(Alert.AlertType.WARNING, "No data to save", ButtonType.OK).show();
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName("timetable.txt");
            File file = fileChooser.showSaveDialog(primaryStage);
            if(file == null) return;
            try(FileWriter writer = new FileWriter(file)){
                writer.write("Day,Time,Course,Instructor,Room\n");
                List<Course> courses = dataset.getCourses();
                for(int i = 0; i < courses.size(); i++){
                    Course course = courses.get(i);
                    Gene gene = chromosome1.getGene(i);
                    writer.write(gene.getTimeslot().getDay()+"," +gene.getTimeslot().getTime()+","+course.getName()+","+course.getInstructor().getName()+","+gene.getRoom().getId()+"\n");
                }
                new Alert(Alert.AlertType.INFORMATION, "Saved", ButtonType.OK).show();
            }catch (IOException ex){
                new Alert(Alert.AlertType.ERROR, "Failed to save data", ButtonType.OK).show();
            }
        });

        Button saveGraphBtn =  new Button("Save Fitness Graph");
        saveGraphBtn.setOnAction(e -> {
            if(ga1 == null){
                new Alert(Alert.AlertType.WARNING, "No data to save", ButtonType.OK).show();
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("fitnessHistory.csv");
            File file = fileChooser.showSaveDialog(primaryStage);
            if(file == null) return;
            try(FileWriter writer = new FileWriter(file)){
                writer.write("Generation,BestFitness\n");
                List<Double> history = ga1.getBestFitnessHistory();
                for(int i = 0; i < history.size(); i++){
                    writer.write(i+","+history.get(i)+"\n");
                }
                new Alert(Alert.AlertType.INFORMATION, "Saved", ButtonType.OK).show();
            }catch (IOException ex){
                new Alert(Alert.AlertType.ERROR, "Failed to save data", ButtonType.OK).show();
            }
        });

        Button expBtn =  new Button("Run Experiments");
        expBtn.setOnAction(e -> {
            if(dataset == null || dataset.getCourses().isEmpty()){
                new Alert(Alert.AlertType.WARNING, "No data to save", ButtonType.OK).show();
                return;
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("experimentsResults.csv");
            File file = fileChooser.showSaveDialog(primaryStage);
            if(file == null) return;
            try{
               ExpRun.runAllexp(dataset, file.getAbsolutePath());
                new Alert(Alert.AlertType.INFORMATION, "Experiments done", ButtonType.OK).show();
            }catch (IOException ex){
                new Alert(Alert.AlertType.ERROR, "Failed to save data", ButtonType.OK).show();
            }
        });

        Button dsBtn = new Button ("Run Dataset size experiment");
        dsBtn.setOnAction(e -> {
            FileChooser input = new FileChooser();
            input.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            List<File> files = input.showOpenMultipleDialog(primaryStage);
            if(files == null || files.isEmpty()) return;

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("DatasetSizeResults.csv");
            File file = fileChooser.showSaveDialog(primaryStage);
            if(file == null) return;
            try{
                String[] paths = new String[files.size()];
                for(int i = 0; i < files.size(); i++){
                    paths[i] = files.get(i).getAbsolutePath();
                }
                ExpRun.runDatasetExp(paths, file.getAbsolutePath());                 new Alert(Alert.AlertType.INFORMATION, "Experiments done", ButtonType.OK).show();
            }catch (IOException ex){
                new Alert(Alert.AlertType.ERROR, "Failed to save data", ButtonType.OK).show();
            }
        });

        l1=new Label("No data loaded");

        HBox h1 = new HBox(10,loadBtn, dataBtn,runBtn,saveBtn,saveDSBtn,saveGraphBtn);
        HBox h2 = new HBox(10,expBtn,dsBtn, compare);
        h1.setPadding(new Insets(10,10,0,10));
        h2.setPadding(new Insets(5,10,5,10));
        h1.setAlignment(Pos.CENTER_LEFT);
        h2.setAlignment(Pos.CENTER_LEFT);

        populationF=new TextField("100");
        generationsF=new TextField("500");
        crossoverRateF=new TextField("0.80");
        mutationRateF=new TextField("0.05");
        elitismF=new TextField("5");
        selectionBOX = new ComboBox<>(FXCollections.observableArrayList("Tournament", "Roulette Wheel"));
        selectionBOX.getSelectionModel().select(0);
        crossoverBOX = new ComboBox<>(FXCollections.observableArrayList("TWO-point", "ONE-point"));
        crossoverBOX.getSelectionModel().select(0);
        mutationBOX = new ComboBox<>(FXCollections.observableArrayList("Random", "Swap"));
        mutationBOX.getSelectionModel().select(0);

        GridPane gpane = new GridPane();
        gpane.setHgap(8);
        gpane.setVgap(8);
        gpane.setPadding(new Insets(10,10,10,10));
        gpane.addRow(0,new Label("Population: "),populationF,new Label("Generations: "), generationsF);
        gpane.addRow(1,new Label("Crossover Rate: "),crossoverRateF,new Label("Mutation Rate: "), mutationRateF);
        gpane.addRow(2,new Label("Elitism: "),elitismF,new Label("Selection: "), selectionBOX);
        gpane.addRow(3,new Label("Crossover: "),crossoverBOX,new Label("Mutation: "), mutationBOX);

        TitledPane tp = new TitledPane("GA parameters",gpane);
        tp.setCollapsible(true);

        timeTable = new TableView<>();
        TableColumn<TimeTable,String> dayCol = new TableColumn<>("Day");
        TableColumn<TimeTable,String> timeCol = new TableColumn<>("Time");
        TableColumn<TimeTable,String> courseCol = new TableColumn<>("Course");
        TableColumn<TimeTable,String> instCol = new TableColumn<>("Instructor");
        TableColumn<TimeTable,String> roomCol = new TableColumn<>("Room");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        courseCol.setCellValueFactory(new PropertyValueFactory<>("course"));
        instCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));
        timeTable.getColumns().addAll(dayCol,timeCol,courseCol,instCol,roomCol);

        NumberAxis x = new  NumberAxis();
        x.setLabel("Generation");
        NumberAxis y = new  NumberAxis();
        y.setLabel("Best Fitness");
        chart = new LineChart<>(x,y);
        chart.setTitle("Fitness Convergence");

        SplitPane splitPane = new SplitPane(timeTable,chart);
        splitPane.setDividerPositions(0.38);

        l2 = new Label("");
        VBox v = new VBox(h1, h2, tp, l1);

        BorderPane root = new BorderPane();
        root.setTop(v);
        root.setCenter(splitPane);
        root.setBottom(l2);

        Scene scene = new Scene(root, 1050, 680);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showTimeTable(Chromosome chromosome) {
        ObservableList<TimeTable> timeTables = FXCollections.observableArrayList();
        List<Course> courses = dataset.getCourses();
        for(int i=0;i<courses.size();i++){
            Course course = courses.get(i);
            Gene gene = chromosome.getGene(i);
            timeTables.add(new TimeTable(gene.getTimeslot().getDay(), gene.getTimeslot().getTime(), course.getName(), course.getInstructor().getName(), gene.getRoom().getId()));
        }
        timeTable.setItems(timeTables);
    }

    private void showChart(List<Double> GAhistory){
        chart.getData().clear();
        XYChart.Series<Number, Number> GAseries = new XYChart.Series<>();
        GAseries.setName("Genetic Algorithm");
        for(int i=0;i<GAhistory.size();i++){
            GAseries.getData().add(new XYChart.Data<>(i,GAhistory.get(i)));
        }
        chart.getData().add(GAseries);
    }
    private void showCompareChart(List<Double> GAhistory, List<Double> HChistory){
        chart.getData().clear();
        XYChart.Series<Number, Number> GAseries = new XYChart.Series<>();
        GAseries.setName("Genetic Algorithm");
        for(int i=0;i<GAhistory.size();i++){
            GAseries.getData().add(new XYChart.Data<>(i,GAhistory.get(i)));
        }
        XYChart.Series<Number, Number> HCseries = new XYChart.Series<>();
        HCseries.setName("Hill Climbing");
        for(int i=0;i<HChistory.size();i++){
            HCseries.getData().add(new XYChart.Data<>(i,HChistory.get(i)));
        }
        chart.getData().addAll(GAseries, HCseries);
    }

    private void showStats(Chromosome chromosome, FitnessResult result, GA ga) {
        l2.setText("Courses scheduled: "+chromosome.getGenes().size()+"/"+dataset.getCourses().size()+ " | Hard violations: "+result.getHardViolations()+
                " | Soft violations: "+ result.getSoftViolations()+ " | Final penalty: "+result.getPenalty()+ " | Best fitness: "+result.getFitness()+ " | Generations: "+ga.getBestFitnessHistory().size()+ " | ExecutionTime: "+ga.getExecutionTime());
    }
}
