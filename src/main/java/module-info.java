module com.hyozlet.tempo {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.groovy;


    opens com.hyozlet.tempo to javafx.fxml;
    exports com.hyozlet.tempo;
}