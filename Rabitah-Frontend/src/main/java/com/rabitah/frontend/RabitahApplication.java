package com.rabitah.frontend;
import javafx.application.Application; import javafx.stage.Stage;
public class RabitahApplication extends Application {private AppContext context;@Override public void start(Stage stage){context=new AppContext();context.router().start(stage);}@Override public void stop(){if(context!=null)context.close();}public static void main(String[] args){launch(args);}}
