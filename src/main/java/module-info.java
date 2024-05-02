module com.mehdimst.javafocus {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.mehdimst.javafocus to javafx.fxml;
    exports com.mehdimst.javafocus;
}