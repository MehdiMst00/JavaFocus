module com.mehdimst.javafocus {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires com.jfoenix;

    opens com.mehdimst.javafocus to javafx.fxml;
    exports com.mehdimst.javafocus;
}