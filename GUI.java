import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.*;
import javafx.concurrent.Task;
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
		
		VBox parameterVBox = new VBox();

		int gridVgap = 5;		
		int mainGridRow = 0;
		
		//Window
		//Center
		TitledPane tpWindow = new TitledPane();
		tpWindow.setText("Window");
		GridPane gridWindow = new GridPane();
		gridWindow.setVgap(gridVgap);
		int rowWindowGrid = 0;
		Label lblCenterRe = new Label("Center Re: ");
		gridWindow.add(lblCenterRe,0,rowWindowGrid);
		TextField txtCenterRe = new TextField();
		txtCenterRe.setText(String.valueOf(mandelbrot.x_center));
		gridWindow.add(txtCenterRe,1,rowWindowGrid);
		rowWindowGrid++;
		Label lblCenterIm = new Label("Center Im: ");
		gridWindow.add(lblCenterIm,0,rowWindowGrid);
		TextField txtCenterIm = new TextField();
		txtCenterIm.setText(String.valueOf(mandelbrot.y_center));
		gridWindow.add(txtCenterIm,1,rowWindowGrid);
		rowWindowGrid++;
		//Aspect Ratio
		HBox aspectHBox = new HBox();
		CheckBox cbAspect = new CheckBox("Aspect Ratio  ");
		aspectHBox.getChildren().add(cbAspect);
		cbAspect.setSelected(true);
		TextField txtAspectNum = new TextField("16");
		txtAspectNum.setPrefWidth(40);
		aspectHBox.getChildren().add(txtAspectNum);
		Label lblAspectColon = new Label(" : ");
		aspectHBox.getChildren().add(lblAspectColon);
		TextField txtAspectDen = new TextField("9");
		txtAspectDen.setPrefWidth(40);
		aspectHBox.getChildren().add(txtAspectDen);
		gridWindow.add(aspectHBox,0,rowWindowGrid,2,1);
		rowWindowGrid++;
		Label lblAspectNote = new Label("     (overrides y height and y pixels)");
		gridWindow.add(lblAspectNote,0,rowWindowGrid,2,1);
		rowWindowGrid++;
		//Dimensions
		Label lblWidth = new Label("Width: ");
		gridWindow.add(lblWidth,0,rowWindowGrid);
		TextField txtWidth = new TextField();
		gridWindow.add(txtWidth,1,rowWindowGrid);
		txtWidth.setText(String.valueOf(mandelbrot.x_width));
		rowWindowGrid++;
		Label lblHeight = new Label("Height: ");
		gridWindow.add(lblHeight,0,rowWindowGrid);
		TextField txtHeight = new TextField();
		gridWindow.add(txtHeight,1,rowWindowGrid);
		txtHeight.setText(String.valueOf(mandelbrot.y_height));
		rowWindowGrid++;
		Label lblXmin = new Label("x-min: ");
		gridWindow.add(lblXmin,0,rowWindowGrid);
		Label lblXminVal = new Label();
		gridWindow.add(lblXminVal,1,rowWindowGrid);
		lblXminVal.setText(String.valueOf(mandelbrot.x_min));
		rowWindowGrid++;
		Label lblXmax = new Label("x-max: ");
		gridWindow.add(lblXmax,0,rowWindowGrid);
		Label lblXmaxVal = new Label();
		gridWindow.add(lblXmaxVal,1,rowWindowGrid);
		lblXmaxVal.setText(String.valueOf(mandelbrot.x_max));
		rowWindowGrid++;
		Label lblYmin = new Label("y-min: ");
		gridWindow.add(lblYmin,0,rowWindowGrid);
		Label lblYminVal = new Label();
		gridWindow.add(lblYminVal,1,rowWindowGrid);
		lblYminVal.setText(String.valueOf(mandelbrot.y_min));
		rowWindowGrid++;
		Label lblYmax = new Label("y-max: ");
		gridWindow.add(lblYmax,0,rowWindowGrid);
		Label lblYmaxVal = new Label();
		gridWindow.add(lblYmaxVal,1,rowWindowGrid);
		lblYmaxVal.setText(String.valueOf(mandelbrot.y_max));
		rowWindowGrid++;
		tpWindow.setContent(gridWindow);
		parameterVBox.getChildren().add(tpWindow);
		
		//Julia
		TitledPane tpJulia = new TitledPane();
		tpJulia.setText("Julia");
		tpJulia.setExpanded(false);
		GridPane gridJulia = new GridPane();
		gridJulia.setVgap(gridVgap);
		int rowJuliaGrid = 0;
		CheckBox cbJulia = new CheckBox("Julia");
		gridJulia.add(cbJulia,0,rowJuliaGrid,2,1);
		rowJuliaGrid++;
		Label lblJuliaRe = new Label("Re: ");
		gridJulia.add(lblJuliaRe,0,rowJuliaGrid);
		TextField txtJuliaRe = new TextField();
		gridJulia.add(txtJuliaRe,1,rowJuliaGrid);
		rowJuliaGrid++;
		Label lblJuliaIm = new Label("Im: ");
		gridJulia.add(lblJuliaIm,0,rowJuliaGrid);
		TextField txtJuliaIm = new TextField();
		gridJulia.add(txtJuliaIm,1,rowJuliaGrid);
		rowJuliaGrid++;
		Button btnSetJulia = new Button("Set Julia to Center");
		gridJulia.add(btnSetJulia,0,rowJuliaGrid,2,1);
		rowJuliaGrid++;
		tpJulia.setContent(gridJulia);
		parameterVBox.getChildren().add(tpJulia);
		
		//Mobius
		TitledPane tpMobius = new TitledPane();
		tpMobius.setText("Mobius");
		tpMobius.setExpanded(false);
		GridPane gridMobius = new GridPane();
		gridMobius.setVgap(gridVgap);
		int rowMobiusGrid = 0;
		CheckBox cbMobius = new CheckBox("Mobius");
		gridMobius.add(cbMobius,0,rowMobiusGrid,4,1);
		rowMobiusGrid++;
		//A
		Label lblA = new Label("A: ");
		gridMobius.add(lblA,0,rowMobiusGrid);
		TextField txtARe = new TextField(String.valueOf(mandelbrot.A.r));
		gridMobius.add(txtARe,1,rowMobiusGrid);
		Label lblAPlus = new Label(" + ");
		gridMobius.add(lblAPlus,2,rowMobiusGrid);
		TextField txtAIm = new TextField(String.valueOf(mandelbrot.A.i));
		gridMobius.add(txtAIm,3,rowMobiusGrid);
		Label lblAi = new Label(" i");
		gridMobius.add(lblAi,4,rowMobiusGrid);
		rowMobiusGrid++;
		//B
		Label lblB = new Label("B: ");
		gridMobius.add(lblB,0,rowMobiusGrid);
		TextField txtBRe = new TextField(String.valueOf(mandelbrot.B.r));
		gridMobius.add(txtBRe,1,rowMobiusGrid);
		Label lblBPlus = new Label(" + ");
		gridMobius.add(lblBPlus,2,rowMobiusGrid);
		TextField txtBIm = new TextField(String.valueOf(mandelbrot.B.i));
		gridMobius.add(txtBIm,3,rowMobiusGrid);
		Label lblBi = new Label(" i");
		gridMobius.add(lblBi,4,rowMobiusGrid);
		rowMobiusGrid++;
		//C
		Label lblC = new Label("C: ");
		gridMobius.add(lblC,0,rowMobiusGrid);
		TextField txtCRe = new TextField(String.valueOf(mandelbrot.C.r));
		gridMobius.add(txtCRe,1,rowMobiusGrid);
		Label lblCPlus = new Label(" + ");
		gridMobius.add(lblCPlus,2,rowMobiusGrid);
		TextField txtCIm = new TextField(String.valueOf(mandelbrot.C.i));
		gridMobius.add(txtCIm,3,rowMobiusGrid);
		Label lblCi = new Label(" i");
		gridMobius.add(lblCi,4,rowMobiusGrid);
		rowMobiusGrid++;
		//D
		Label lblD = new Label("D: ");
		gridMobius.add(lblD,0,rowMobiusGrid);
		TextField txtDRe = new TextField(String.valueOf(mandelbrot.D.r));
		gridMobius.add(txtDRe,1,rowMobiusGrid);
		Label lblDPlus = new Label(" + ");
		gridMobius.add(lblDPlus,2,rowMobiusGrid);
		TextField txtDIm = new TextField(String.valueOf(mandelbrot.D.i));
		gridMobius.add(txtDIm,3,rowMobiusGrid);
		Label lblDi = new Label(" i");
		gridMobius.add(lblDi,4,rowMobiusGrid);
		rowMobiusGrid++;
		
		
		tpMobius.setContent(gridMobius);
		parameterVBox.getChildren().add(tpMobius);
		
		//Image
		TitledPane tpImage = new TitledPane();
		tpImage.setText("Image");
		GridPane gridImage = new GridPane();
		gridImage.setVgap(gridVgap);
		int rowImageGrid = 0;
		//Pixels
		Label lblXpixels = new Label("x pixels: ");
		gridImage.add(lblXpixels,0,rowImageGrid);
		TextField txtXpixels = new TextField();
		gridImage.add(txtXpixels,1,rowImageGrid);
		txtXpixels.setText(String.valueOf(mandelbrot.x_pixels));
		rowImageGrid++;
		Label lblYpixels = new Label("y pixels: ");
		gridImage.add(lblYpixels,0,rowImageGrid);
		TextField txtYpixels = new TextField();
		gridImage.add(txtYpixels,1,rowImageGrid);
		txtYpixels.setText(String.valueOf(mandelbrot.y_pixels));
		rowImageGrid++;
		//Iteration
		Label lblIterationLimit = new Label("iteration limit: ");
		gridImage.add(lblIterationLimit,0,rowImageGrid);
		TextField txtIterationLimit = new TextField();
		gridImage.add(txtIterationLimit,1,rowImageGrid);
		txtIterationLimit.setText(String.valueOf(mandelbrot.iteration_limit));
		rowImageGrid++;
		
		tpImage.setContent(gridImage);
		parameterVBox.getChildren().add(tpImage);
		
		
		//Color
		TitledPane tpColor = new TitledPane();
		tpColor.setText("Coloring");
		GridPane gridColor = new GridPane();
		gridColor.setVgap(gridVgap);
		int rowColorGrid = 0;
		
		Label lblIterationColorStart = new Label("Color Start: ");
		gridColor.add(lblIterationColorStart,0,rowColorGrid);
		TextField txtColorStart = new TextField("0");
		gridColor.add(txtColorStart,1,rowColorGrid);
		rowColorGrid++;
		
		Label lblPallet = new Label("Pallet: ");
		gridColor.add(lblPallet,0,rowColorGrid);
		ObservableList<String> spectrumList = FXCollections.observableArrayList(mandelbrot.listSpectrums());
		ComboBox<String> cbxPallet = new ComboBox<>(spectrumList);
		gridColor.add(cbxPallet,1,rowColorGrid);
		cbxPallet.setValue(mandelbrot.listSpectrums()[0]);
		rowColorGrid++;
		
		
		Label lblColorScale = new Label("Pallet Scaling: ");
		gridColor.add(lblColorScale,0,rowColorGrid);
		ObservableList<String> colorScaleOptions = FXCollections.observableArrayList(mandelbrot.listScales());
		ComboBox<String> cbxColorScale = new ComboBox<>(colorScaleOptions);
		gridColor.add(cbxColorScale,1,rowColorGrid);
		cbxColorScale.setValue(mandelbrot.listScales()[0]);
		rowColorGrid++;
		
		//Recolor Button
		Button btnRecolor = new Button("Recolor");
		gridColor.add(btnRecolor,0,rowColorGrid);
		rowColorGrid++;
		
		tpColor.setContent(gridColor);
		parameterVBox.getChildren().add(tpColor);
		
		
		//Generate Button
		Button btnGenerate = new Button("Generate");
		parameterVBox.getChildren().add(btnGenerate);
		
		
		//Save Button
		HBox saveHBox = new HBox();
		Button btnSave = new Button("Save");
		saveHBox.getChildren().add(btnSave);
		TextField txtPath = new TextField("image.png");
		saveHBox.getChildren().add(txtPath);
		parameterVBox.getChildren().add(saveHBox);
		
		
		//Error text
		Text lblError = new Text();
		parameterVBox.getChildren().add(lblError);
		lblError.setFill(Color.FIREBRICK);
		
		//Image View
		VBox pictureVBox = new VBox();
		pictureVBox.getChildren().add(imageView);
		
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
		
		//Julia set listener
		btnSetJulia.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				if(mandelbrot.julia){
					lblError.setText("Error: must focus on mandelbrot set.");
				}
				txtJuliaRe.setText(txtCenterRe.getText());
				txtJuliaIm.setText(txtCenterIm.getText());
				txtCenterRe.setText("0");
				txtCenterIm.setText("0");
				txtWidth.setText("5");		
				cbJulia.setSelected(true);
				btnGenerate.fire();
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
				color = cbxPallet.getValue();
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
					btnSetJulia.setDisable(true);
					double julia_x = mandelbrot.julia_center.r;
					double julia_y = mandelbrot.julia_center.i;
					try{julia_x = Double.parseDouble(txtJuliaRe.getText());}catch(Exception e){
							errorText += "Error parsing Julia Re\n"; hasError = true;}
					try{julia_y = Double.parseDouble(txtJuliaIm.getText());}catch(Exception e){
							errorText += "Error parsing Julia Im\n"; hasError = true;}
					mandelbrot.setJulia(julia_x,julia_y);
				}else{
					mandelbrot.julia = false;
					btnSetJulia.setDisable(false);
				}
				//Mobius imputs
				if(cbMobius.isSelected()){
					mandelbrot.mobius = true;
					//A
					double mobiusAr = mandelbrot.A.r;
					double mobiusAi = mandelbrot.A.i;
					try{mobiusAr = Double.parseDouble(txtARe.getText());}catch(Exception e){
							errorText += "Error parsing Mobius A Re\n"; hasError = true;}
					try{mobiusAi = Double.parseDouble(txtAIm.getText());}catch(Exception e){
							errorText += "Error parsing Mobius A Im\n"; hasError = true;}
					mandelbrot.setMobiusA(mobiusAr,mobiusAi);
					//B
					double mobiusBr = mandelbrot.B.r;
					double mobiusBi = mandelbrot.B.i;
					try{mobiusBr = Double.parseDouble(txtBRe.getText());}catch(Exception e){
							errorText += "Error parsing Mobius B Re\n"; hasError = true;}
					try{mobiusBi = Double.parseDouble(txtBIm.getText());}catch(Exception e){
							errorText += "Error parsing Mobius B Im\n"; hasError = true;}
					mandelbrot.setMobiusB(mobiusBr,mobiusBi);
					//C
					double mobiusCr = mandelbrot.C.r;
					double mobiusCi = mandelbrot.C.i;
					try{mobiusCr = Double.parseDouble(txtCRe.getText());}catch(Exception e){
							errorText += "Error parsing Mobius C Re\n"; hasError = true;}
					try{mobiusCi = Double.parseDouble(txtCIm.getText());}catch(Exception e){
							errorText += "Error parsing Mobius C Im\n"; hasError = true;}
					mandelbrot.setMobiusC(mobiusCr,mobiusCi);
					//D
					double mobiusDr = mandelbrot.D.r;
					double mobiusDi = mandelbrot.D.i;
					try{mobiusDr = Double.parseDouble(txtDRe.getText());}catch(Exception e){
							errorText += "Error parsing Mobius D Re\n"; hasError = true;}
					try{mobiusDi = Double.parseDouble(txtDIm.getText());}catch(Exception e){
							errorText += "Error parsing Mobius D Im\n"; hasError = true;}
					mandelbrot.setMobiusD(mobiusDr,mobiusDi);
				}else{
					mandelbrot.mobius = false;
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
				
				//If error
				if(hasError){
					lblError.setText(errorText);
				}else{
					mandelbrot.calculateValues();
					btnRecolor.fire();
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
		
		//Set the Stage
		ScrollPane leftScrollPane = new ScrollPane();
		int leftScrollPaneWidth = 380;
		leftScrollPane.setMinWidth(leftScrollPaneWidth);
		leftScrollPane.setContent(parameterVBox);
		HBox mainHBox = new HBox();
		mainHBox.getChildren().add(leftScrollPane);
		ScrollPane rightScrollPane = new ScrollPane();
		rightScrollPane.setContent(pictureVBox);
		mainHBox.getChildren().add(rightScrollPane);
		
		Scene scene = new Scene(mainHBox);
		primaryStage.setScene(scene);
		
		primaryStage.show();
	}
}
