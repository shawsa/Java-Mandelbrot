import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import javafx.geometry.Orientation;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class GUI extends Application{
	public static Mandelbrot mandelbrot;
	public static ImageView imageView;
	public static boolean dragging = false;
	public static double dragStartX,dragStartY,dragStopX,dragStopY;
	
	//public static WritableImage img;
	public static void main(String[] args){
		dragStartX = 0;
		dragStartY = 0;
		dragStopX = 0;
		dragStopY = 0;
		dragging = false;
		
		mandelbrot = new Mandelbrot();
		imageView = new ImageView();
		imageView.setImage(mandelbrot.generateImage());
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage){
		primaryStage.setTitle("Mandelbrot/Juila Set Explorer");
		
		GridPane grid = new GridPane();
		
		ColumnConstraints column = new ColumnConstraints(80);
		grid.getColumnConstraints().add(column);
		column = new ColumnConstraints(80);
		grid.getColumnConstraints().add(column);
		column = new ColumnConstraints(200);
		grid.getColumnConstraints().add(column);
		
		
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5,5,5,5));
		
		int row = 0;
		
		//Julia
		CheckBox cbJulia = new CheckBox("Julia");
		grid.add(cbJulia,0,row,1,1);
		row++;
		Label lblJuliaRe = new Label("Re:");
		grid.add(lblJuliaRe,1,row);
		TextField txtJuliaRe = new TextField();
		grid.add(txtJuliaRe,2,row);
		row++;
		Label lblJuliaIm = new Label("Im:");
		grid.add(lblJuliaIm,1,row);
		TextField txtJuliaIm = new TextField();
		grid.add(txtJuliaIm,2,row);
		row++;
		
		//Center
		//x center
		Label lblCenter = new Label("Center");
		grid.add(lblCenter,0,row);
		row++;
		Label lblCenterRe = new Label("Re:");
		grid.add(lblCenterRe,1,row);
		TextField txtCenterRe = new TextField();
		txtCenterRe.setText(String.valueOf(mandelbrot.x_center));
		grid.add(txtCenterRe,2,row);
		row++;
		//y center
		Label lblCenterIm = new Label("Im:");
		grid.add(lblCenterIm,1,row);
		TextField txtCenterIm = new TextField();
		txtCenterIm.setText(String.valueOf(mandelbrot.y_center));
		grid.add(txtCenterIm,2,row);
		row++;
		
		//Aspect checkbox
		CheckBox cbAspect = new CheckBox("Aspect (overrides y height and y pixels)");
		grid.add(cbAspect,0,row,3,1);
		cbAspect.setSelected(true);
		row++;
		TextField txtAspectNum = new TextField("16");
		txtAspectNum.setPrefWidth(5);
		grid.add(txtAspectNum,1,row);
		TextField txtAspectDen = new TextField("9");
		txtAspectDen.setPrefWidth(5);
		grid.add(txtAspectDen,2,row);
		row++;
		
		//Window
		Label lblWindow = new Label("Window");
		grid.add(lblWindow,0,row,3,1);
		row++;
		Label lblWidth = new Label("Width");
		grid.add(lblWidth,1,row);
		TextField txtWidth = new TextField();
		grid.add(txtWidth,2,row);
		txtWidth.setText(String.valueOf(mandelbrot.x_width));
		row++;
		Label lblHeight = new Label("Height");
		grid.add(lblHeight,1,row);
		TextField txtHeight = new TextField();
		grid.add(txtHeight,2,row);
		txtHeight.setText(String.valueOf(mandelbrot.y_height));
		row++;
		Label lblXmin = new Label("x-min");
		grid.add(lblXmin,1,row);
		Label lblXminVal = new Label();
		grid.add(lblXminVal,2,row);
		lblXminVal.setText(String.valueOf(mandelbrot.x_min));
		row++;
		Label lblXmax = new Label("x-max");
		grid.add(lblXmax,1,row);
		Label lblXmaxVal = new Label();
		grid.add(lblXmaxVal,2,row);
		lblXmaxVal.setText(String.valueOf(mandelbrot.x_max));
		row++;
		Label lblYmin = new Label("y-min");
		grid.add(lblYmin,1,row);
		Label lblYminVal = new Label();
		grid.add(lblYminVal,2,row);
		lblYminVal.setText(String.valueOf(mandelbrot.y_min));
		row++;
		Label lblYmax = new Label("y-max");
		grid.add(lblYmax,1,row);
		Label lblYmaxVal = new Label();
		grid.add(lblYmaxVal,2,row);
		lblYmaxVal.setText(String.valueOf(mandelbrot.y_max));
		row++;
		
		//Image
		//Pixels
		Label lblImage = new Label("Image");
		grid.add(lblImage,0,row);
		row++;
		Label lblXpixels = new Label("x pixels");
		grid.add(lblXpixels,1,row);
		TextField txtXpixels = new TextField();
		grid.add(txtXpixels,2,row);
		txtXpixels.setText(String.valueOf(mandelbrot.x_pixels));
		row++;
		Label lblYpixels = new Label("y pixels");
		grid.add(lblYpixels,1,row);
		TextField txtYpixels = new TextField();
		grid.add(txtYpixels,2,row);
		txtYpixels.setText(String.valueOf(mandelbrot.y_pixels));
		row++;
		//Iteration
		Label lblIterationLimit = new Label("iteration limit:");
		grid.add(lblIterationLimit,1,row);
		TextField txtIterationLimit = new TextField();
		grid.add(txtIterationLimit,2,row);
		txtIterationLimit.setText(String.valueOf(mandelbrot.iteration_limit));
		row++;
		
		//Color
		Label lblColor = new Label("Coloring");
		grid.add(lblColor,0,row,3,1);
		row++;
		Label lblIterationColorStart = new Label("color start");
		grid.add(lblIterationColorStart,1,row);
		TextField txtColorStart = new TextField("0");
		grid.add(txtColorStart,2,row);
		row++;
		Label lblPallet = new Label("pallet");
		grid.add(lblPallet,1,row);
		TextField txtPallet = new TextField("prism");
		grid.add(txtPallet,2,row);
		row++;
		Label lblColorScale = new Label("color scaling");
		grid.add(lblColorScale,1,row);
		ObservableList<String> colorScaleOptions = FXCollections.observableArrayList("linear","logarithmic");
		ComboBox<String> cbxColorScale = new ComboBox<>(colorScaleOptions);
		grid.add(cbxColorScale,2,row);
		cbxColorScale.setValue("linear");
		row++;
		
		//Recolor Button
		Button btnRecolor = new Button("Recolor");
		grid.add(btnRecolor,1,row);
		row++;
		
		//Generate Button
		Button btnGenerate = new Button("Generate");
		grid.add(btnGenerate,0,row);
		row++;
		
		//Save Button
		Button btnSave = new Button("Save");
		grid.add(btnSave,0,row);
		TextField txtPath = new TextField("image.png");
		grid.add(txtPath,1,row,2,1);
		row++;
		
		//Error text
		Text lblError = new Text();
		grid.add(lblError,0,row,3,1);
		lblError.setFill(Color.FIREBRICK);
		
		//Image View
		VBox imgBox = new VBox(1,imageView);
		grid.add(imgBox,4,0,1,row);
		
		//Listeners
		//image view listeners
		imageView.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			 @Override
			 public void handle(MouseEvent event) {
				dragStartX = event.getX();
				dragStartY = event.getY();
				dragging = true;
				event.consume();
			 }
		});
		imageView.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			 @Override
			 public void handle(MouseEvent event) {
				//System.out.println(event.getX());
				dragStopX = event.getX();
				dragStopY = event.getY();
				if(dragging && dragStartX < dragStopX && dragStartY < dragStopY){
					double x_center = (dragStopX + dragStartX)/2 * mandelbrot.x_width/mandelbrot.x_pixels + mandelbrot.x_min;
					double y_center = mandelbrot.y_max - (dragStopY + dragStartY)/2 * mandelbrot.y_height/mandelbrot.y_pixels;
					double x_width = (dragStopX-dragStartX) * mandelbrot.x_width/mandelbrot.x_pixels;
					double y_height = (dragStopY-dragStartY) * mandelbrot.y_height/mandelbrot.y_pixels;
					txtCenterRe.setText(String.valueOf(x_center));
					txtCenterIm.setText(String.valueOf(y_center));
					txtWidth.setText(String.valueOf(x_width));
					txtHeight.setText(String.valueOf(y_height));
					btnGenerate.fire();
				}
				dragging = false;
				event.consume();
			 }
		});
		
		//Recolor Listener
		btnRecolor.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				String errorText = "";
				lblError.setText("");
				boolean hasError = false;
				int iteration_color_start = mandelbrot.iteration_color_start;
				try{iteration_color_start = Integer.parseInt(txtColorStart.getText());}catch(Exception e){
						errorText += "Error parsing color start\n"; hasError = true;}
				if(mandelbrot.iteration_limit <= iteration_color_start || mandelbrot.iteration_limit <1 || iteration_color_start <0){
					errorText += "Invalid limit or color start parameters.\n";
					hasError = true; 
				}else{
					mandelbrot.iteration_color_start = iteration_color_start;
				}
				String color = "reds";
				color = txtPallet.getText();
				try{mandelbrot.setSpectrum(color);}catch(Exception e){
					errorText += "Error parsing pallet.\n"; hasError = true;}
				if(hasError){
					lblError.setText(errorText);
				}else{
					imageView.setImage(mandelbrot.colorImage(cbxColorScale.getValue().toString()));
				}
			}
		});
		
		//Generate Button Listener
		btnGenerate.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				String errorText = "";
				lblError.setText("");
				boolean hasError = false;
				//Julia inputs
				if(cbJulia.isSelected()){
					mandelbrot.julia = true;
					double julia_x = mandelbrot.julia_center.r;
					double julia_y = mandelbrot.julia_center.i;
					try{julia_x = Double.parseDouble(txtJuliaRe.getText());}catch(Exception e){
							errorText += "Error parsing Julia Re\n"; hasError = true;}
					try{julia_y = Double.parseDouble(txtJuliaIm.getText());}catch(Exception e){
							errorText += "Error parsing Julia Im\n"; hasError = true;}
					mandelbrot.setJulia(julia_x,julia_y);
				}else{
					mandelbrot.julia = false;
				}
				//Center inputs
				double x = mandelbrot.x_center;
				double y = mandelbrot.y_center;
				try{x = Double.parseDouble(txtCenterRe.getText());}catch(Exception e){
						errorText += "Error parsing Center Re\n"; hasError = true;}
				try{y = Double.parseDouble(txtCenterIm.getText());}catch(Exception e){
						errorText += "Error parsing Center Im\n"; hasError = true;}
				mandelbrot.setCenter(x,y);
				
				//Set Aspect
				if(cbAspect.isSelected()){
					double top = 16;
					double bottom = 9;
					try{top = Double.parseDouble(txtAspectNum.getText());}catch(Exception e){
						errorText += "Error parsing Aspect Horizontal\n"; hasError = true;}
					try{bottom = Double.parseDouble(txtAspectDen.getText());}catch(Exception e){
							errorText += "Error parsing Aspect Vertical\n"; hasError = true;}
					if(bottom==0){
						errorText += "Error: Aspect Vertical Cannot be 0";
						hasError = true;
					}else{mandelbrot.setAspect(top/bottom);}
				}
				
				//Set window
				double width = mandelbrot.x_width;
				double height = mandelbrot.y_height;
				try{width = Double.parseDouble(txtWidth.getText());}catch(Exception e){
						errorText += "Error parsing Width\n"; hasError = true;}
				try{height = Double.parseDouble(txtHeight.getText());}catch(Exception e){
						errorText += "Error parsing Height\n"; hasError = true;}
				
				//Set pixels
				int x_pixels = mandelbrot.x_pixels;
				int y_pixels = mandelbrot.y_pixels;
				try{x_pixels = Integer.parseInt(txtXpixels.getText());}catch(Exception e){
						errorText += "Error parsing x pixels\n"; hasError = true;}
				try{y_pixels = Integer.parseInt(txtYpixels.getText());}catch(Exception e){
						errorText += "Error parsing y pixels\n"; hasError = true;}
				//Apply window dimensions
				mandelbrot.setPixels(x_pixels,y_pixels);
				if(cbAspect.isSelected()){
					mandelbrot.setWidth(width);
					txtHeight.setText(String.valueOf(mandelbrot.y_height));
					txtYpixels.setText(String.valueOf(mandelbrot.y_pixels));
				}else{
					mandelbrot.setWidthHeight(width,height);
				}
				lblXminVal.setText(String.valueOf(mandelbrot.x_min));
				lblXmaxVal.setText(String.valueOf(mandelbrot.x_max));
				lblYminVal.setText(String.valueOf(mandelbrot.y_min));
				lblYmaxVal.setText(String.valueOf(mandelbrot.y_max));
				
				//Iteration
				int iteration_limit = mandelbrot.iteration_limit;
				int iteration_color_start = mandelbrot.iteration_color_start;
				try{iteration_limit = Integer.parseInt(txtIterationLimit.getText());}catch(Exception e){
						errorText += "Error parsing iteration limit\n"; hasError = true;}
				try{iteration_color_start = Integer.parseInt(txtColorStart.getText());}catch(Exception e){
						errorText += "Error parsing color start\n"; hasError = true;}
				if(iteration_limit <= iteration_color_start || iteration_limit <1 || iteration_color_start <0){
					errorText += "Invalid limit or color start parameters.\n";
					hasError = true; 
				}else{
					mandelbrot.iteration_limit = iteration_limit;
					mandelbrot.iteration_color_start = iteration_color_start;
				}
				
				
				/*
				//Color
				String color = "reds";
				color = txtPallet.getText();
				try{mandelbrot.setSpectrum(color);}catch(Exception e){
					errorText += "Error parsing pallet.\n"; hasError = true;}
				*/
				
				//If error
				if(hasError){
					lblError.setText(errorText);
				}else{
					//generate image
					mandelbrot.calculateValues();
					btnRecolor.fire();
					//imageView.setImage(mandelbrot.generateImage());
				}
			}
		});
		
		btnSave.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				BufferedImage img = SwingFXUtils.fromFXImage(imageView.getImage(), null);
				File f = new File(txtPath.getText());
				try{
					ImageIO.write(img, "PNG", f);
				}catch(Exception e){
					lblError.setText("Failed to write file.");
				}
			}
		});

		//grid.setGridLinesVisible(true);
		
		Scene scene = new Scene(grid);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
}
