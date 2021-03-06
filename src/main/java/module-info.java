module com.example.netflexe {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.base;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.web;
    requires org.apache.commons.codec;
    requires com.google.common;
    requires java.mail;

    opens com.example.netflexe.Vue to javafx.fxml;
    opens com.example.netflexe.Model to javafx.fxml;
    opens com.example.netflexe.Controller to javafx.fxml;
    exports com.example.netflexe.Vue;
    exports com.example.netflexe.Model;
    exports com.example.netflexe.Controller;
}