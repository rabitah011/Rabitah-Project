package com.rabitah.frontend;
import javafx.application.Application; import javafx.stage.Stage;
public class RabitahApplication extends Application {private AppContext context;@Override public void start(Stage stage){context=new AppContext();context.router().start(stage);}@Override public void stop(){if(context!=null)context.close();}public static void main(String[] args){System.setProperty("glass.gtk.uiScale",System.getProperty("glass.gtk.uiScale","1.0"));System.setProperty("prism.allowhidpi",System.getProperty("prism.allowhidpi","false"));launch(args);}}
