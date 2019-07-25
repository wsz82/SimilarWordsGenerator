package similarwordsgenerator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppView {

    private final Generator gn;

    private CheckBox sorted;
    private CheckBox firstChar;
    private CheckBox lastChar;
    private TextField numberOfWords;
    private TextField minWordLength;
    private TextField maxWordLength;
    private TextField levelOfCompression;

    private Path path = null;
    private List<String> wordsToSave = new ArrayList<>();
    private List<String> wordsToAnalyse = new ArrayList<>();

    public AppView(Generator gn) {
        this.gn = gn;
    }

    public void init (Stage primaryStage, Parameters initParameters, File userHomeProgram, ISaver saver, SaverWords saverWords) {

        Group root = new Group();
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Similar Words Generator");

        final FileChooser fcLoad = new FileChooser();
        fcLoad.setInitialDirectory(userHomeProgram);
        final FileChooser fcSaveRatios = new FileChooser();
        fcSaveRatios.setInitialDirectory(userHomeProgram);
        final FileChooser fcSaveWords = new FileChooser();
        fcSaveWords.setInitialDirectory(userHomeProgram);

        final Button loadButton = new Button("Load");
        final Button generateButton = new Button("Generate");
        final Button saveRatiosButton = new Button("Save ratios");
        saveRatiosButton.setDisable(true);
        final Button saveWordsButton = new Button("Save words");
        saveWordsButton.setDisable(true);
        final Button compressButton = new Button("Compress");
        compressButton.setDisable(true);

        sorted = new CheckBox("Sort words");
        sorted.setSelected(initParameters.isSorted());
        firstChar = new CheckBox("First char as in input");
        firstChar.setSelected(initParameters.isFirstCharAsInInput());
        lastChar = new CheckBox("Last char as in input");
        lastChar.setSelected(initParameters.isLastCharAsInInput());

        numberOfWords = new TextField(Integer.toString(initParameters.getNumberOfWords()));
        final Label numberOfWordsLabel = new Label("Number of words:");
        minWordLength = minMaxTextFields(initParameters.getMinWordLength()); //new TextField(Integer.toString(parameters.getMinWordLength()));
        final Label minWordLengthLabel = new Label("Min. word length:");
        maxWordLength = minMaxTextFields(initParameters.getMaxWordLength());
        final Label maxWordLengthLabel = new Label("Max. word length:");
        levelOfCompression = new TextField();
        levelOfCompression.setDisable(true);
        final Label levelOfCompressionLabel = new Label("Level of compression:");
        levelOfCompressionLabel.setDisable(true);

        final Label optionsLabel = new Label("Options");


        TextArea inputManual = new TextArea();
        inputManual.setPrefSize(150,500);
        final Label inputManualLabel = new Label("Input");

        TextArea output = new TextArea();
        output.setPrefSize(150,500);
        output.setEditable(false);
        final Label outputLabel = new Label("Output");


        numberOfWords.setMaxWidth(50);
        numberOfWords.setTextFormatter(new TextFormatter<>(this::filterForNumbersOfWords));

        minWordLength.setMaxWidth(50);
        minWordLength.setTextFormatter(new TextFormatter<>(f -> filterForMinWordLength(maxWordLength, f)));

        maxWordLength.setMaxWidth(50);
        maxWordLength.setTextFormatter(new TextFormatter<>(this::filterForMaxWordLength));
        maxWordLength.focusedProperty().addListener((ov, old_val, new_val) -> {
                    try {
                        if (!new_val && !minWordLength.getText().isEmpty() && (Integer.parseInt(maxWordLength.getText()) < Integer.parseInt(minWordLength.getText()))) {
                            maxWordLength.setText("");
                        }
                    } catch (NumberFormatException e) {
                        maxWordLength.setText("");
                    }
                }
        );

        levelOfCompression.setMaxWidth(50);
        levelOfCompression.setTextFormatter(new TextFormatter<>(this::filterForLevelOfCompression));

        fcSaveRatios.setTitle("Save ratios to a file");
        fcSaveRatios.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Ratios", "*.bin"));

        fcSaveWords.setTitle("Save words to a file");
        fcSaveWords.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text", "*.txt", "*.csv"));

        fcLoad.setTitle("Load a text or ratios file");
        fcLoad.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt", "*.csv"),
                new FileChooser.ExtensionFilter("Ratios", "*.bin"));

        primaryStage.setOnCloseRequest(event -> {
            Parameters parametersToSave = settingParameters();
            new Memento(parametersToSave, wordsToSave);
        });

//        primaryStage.onCloseRequestProperty().addListener(observable -> {
//            Parameters parametersToSave = settingParameters();
//            new Memento(parametersToSave, wordsToSave);
//        });

        loadButton.setOnAction(f -> {
            File file = fcLoad.showOpenDialog(primaryStage);

            if (file != null) {
                path = file.toPath();
                inputManual.setText(path.getFileName().toString());
                inputManual.setEditable(false);
            }
        });

        inputManual.setTextFormatter(new TextFormatter<>(f -> {
            if (inputManual.getText().endsWith("\n\n")) {

                f.setText("");
            }
            return f;
        }));

        inputManual.setOnMouseClicked(mouseEvent -> {
            if ( !inputManual.getText().isEmpty() && path != null && inputManual.getText().equals(path.getFileName().toString()) && mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {

                path = null;
                wordsToAnalyse = Collections.emptyList();

                inputManual.setText("");
                inputManual.setEditable(true);

            }
        });

        inputManual.textProperty().addListener((ov, s, t) -> {

//            if (path != null) {
//                inputManual.setText(path.getFileName().toString());       //change to initParameters
//            }

            if (!t.isEmpty()) {

                if (path == null) {
                    wordsToAnalyse = Arrays.asList(t.split("\n"));
                }

                saveRatiosButton.setDisable(false);
                levelOfCompression.setDisable(false);
                levelOfCompressionLabel.setDisable(false);
                compressButton.setDisable(false);

            } else {

                path = null;
                wordsToAnalyse = Collections.emptyList();

                saveRatiosButton.setDisable(true);
                compressButton.setDisable(true);
                levelOfCompression.setDisable(true);
                levelOfCompressionLabel.setDisable(true);
            }

        });

        output.textProperty().addListener((observableValue, s, t1) -> {
            if (!output.getText().isEmpty()) {
                saveWordsButton.setDisable(false);
            } else {
                saveWordsButton.setDisable(true);
            }
        });

        saveRatiosButton.setOnAction(e -> {
            File file = fcSaveRatios.showSaveDialog(primaryStage);
            if (file != null) saver.save(gn.getAnalyser(), file.getPath());
        });

        saveWordsButton.setOnAction(e -> {
            File file = fcSaveWords.showSaveDialog(primaryStage);
            if (file != null) {
                saverWords.save(wordsToSave, file.getPath());
            }
        });

        generateButton.setOnAction(f -> {
            try {
                output.setText("");
                Parameters parameters = settingParameters();

                wordsToSave.removeAll(wordsToSave);

                try {
                    wordsToSave.addAll(gn.generate(parameters));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (String word : wordsToSave) {
                    output.setText(output.getText() + (word + "\n"));
                }
            } catch (NullPointerException en) {
                loadButton.fire();
            }
        });

        compressButton.setOnAction(e -> {

            int levelOfCompressionValue = 0;

            try {
                levelOfCompressionValue = Integer.parseInt(levelOfCompression.getText());
            } catch (NumberFormatException en) {
                levelOfCompression.requestFocus();
            }

            if (levelOfCompressionValue > 0) {
                try {
                    gn.getAnalyser().compress(levelOfCompressionValue);
                } catch (NullPointerException en) {
                    loadButton.fire();
                }
            }
        });

//        sorted.selectedProperty().addListener((ov, old_val, new_val) ->       // chyba niepotrzebne
//                parameters.setSorted(new_val)
//        );
//
//        firstChar.selectedProperty().addListener((ov, old_val, new_val) ->
//                gn.setFirstCharAsInInput(new_val)
//        );
//
//        lastChar.selectedProperty().addListener((ov, old_val, new_val) ->
//                gn.setLastCharAsInInput(new_val)
//        );

        final GridPane options = new GridPane();
        GridPane.setConstraints(loadButton, 0,0);
        GridPane.setConstraints(generateButton, 0,1);
        GridPane.setConstraints(sorted, 0,2);
        GridPane.setConstraints(firstChar, 0,3);
        GridPane.setConstraints(lastChar, 0,4);
        GridPane.setConstraints(numberOfWordsLabel, 0,5);
        GridPane.setConstraints(numberOfWords, 1,5);
        GridPane.setConstraints(minWordLengthLabel, 0,6);
        GridPane.setConstraints(minWordLength, 1,6);
        GridPane.setConstraints(maxWordLengthLabel, 0,7);
        GridPane.setConstraints(maxWordLength, 1,7);
        GridPane.setConstraints(levelOfCompressionLabel, 0,8);
        GridPane.setConstraints(levelOfCompression, 1,8);
        GridPane.setConstraints(compressButton, 0,9);
        GridPane.setConstraints(saveRatiosButton, 0,10);
        GridPane.setConstraints(saveWordsButton, 0,11);
        options.setHgap(6);
        options.setVgap(6);
        options.getChildren().addAll(loadButton, generateButton, saveRatiosButton, sorted, firstChar, lastChar, numberOfWords, numberOfWordsLabel, minWordLength, minWordLengthLabel, maxWordLength, maxWordLengthLabel, saveWordsButton, levelOfCompressionLabel, levelOfCompression, compressButton);

        final VBox optionsPane = new VBox(12);
        optionsPane.getChildren().addAll(optionsLabel, options);
        optionsPane.setAlignment(Pos.TOP_CENTER);
        optionsPane.setPadding(new Insets(12, 12, 12, 12));

        final VBox inputManualPane = new VBox(12);
        inputManualPane.getChildren().addAll(inputManualLabel, inputManual);
        inputManualPane.setAlignment(Pos.TOP_CENTER);
        inputManualPane.setPadding(new Insets(12, 12, 12, 12));

        final VBox outputPane = new VBox(12);
        outputPane.getChildren().addAll(outputLabel, output);
        outputPane.setAlignment(Pos.TOP_CENTER);
        outputPane.setPadding(new Insets(12, 12, 12, 12));

        final Pane hp = new HBox(12);
        hp.getChildren().addAll(optionsPane, inputManualPane, outputPane);
        hp.setPadding(new Insets(12, 12, 12, 12));

        root.getChildren().addAll(hp);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private Parameters settingParameters() {

        Parameters.Builder parametersBuilder = new Parameters.Builder();

        parametersBuilder.setNumberOfWords(Integer.parseInt(numberOfWords.getText()));
        try {
            parametersBuilder.setMinWordLength(Integer.parseInt(minWordLength.getText()));
        } catch (NumberFormatException e) {
            parametersBuilder.setMinWordLength(0);
        }
        try {
            parametersBuilder.setMaxWordLength(Integer.parseInt(maxWordLength.getText()));
        } catch (NumberFormatException e) {
            parametersBuilder.setMaxWordLength(0);
        }
        parametersBuilder.setFirstCharAsInInput(firstChar.isSelected());
        parametersBuilder.setLastCharAsInInput(lastChar.isSelected());
        parametersBuilder.setSorted(sorted.isSelected());
        if (!levelOfCompression.getText().equals("")) {
            parametersBuilder.setCompressionNumber(Integer.parseInt(levelOfCompression.getText()));
        }
        parametersBuilder.setPath(path);
        parametersBuilder.setInput(wordsToAnalyse);

        return parametersBuilder.build();
    }

    private TextField minMaxTextFields (int number) {
        if (number == 0) {
            return new TextField();
        } else {
            return new TextField(Integer.toString(number));
        }
    }

    private TextFormatter.Change filterForLevelOfCompression(TextFormatter.Change f) {
        try {
            int input = Integer.parseInt(f.getControlNewText());
            if ( input < 1 ) {
                f.setText("");
            }
        } catch (NumberFormatException e) {
            f.setText("");
        }
        if (f.getControlNewText().isEmpty()) {
            f.setText("");
        }
        return f;
    }

    private TextFormatter.Change filterForMaxWordLength(TextFormatter.Change f) {
        try {
            int input = Integer.parseInt(f.getControlNewText());
            if ( input < 1 ) {
                f.setText("");
            }
        } catch (NumberFormatException e) {
            f.setText("");
        }
        return f;
    }

    private TextFormatter.Change filterForMinWordLength(TextField maxWordLength, TextFormatter.Change f) {
        try {
            int input = Integer.parseInt(f.getControlNewText());
            if ( input < 1 ) {
                f.setText("");
            }
            if ( !maxWordLength.getText().isEmpty() && input > Integer.parseInt(maxWordLength.getText()) ) {
                f.setText("");
            }

        } catch (NumberFormatException e) {
            f.setText("");
        }
        return f;
    }

    private TextFormatter.Change filterForNumbersOfWords(TextFormatter.Change f) {
        try {
            int input = Integer.parseInt(f.getControlNewText());
            if ( input < 1 ) {
                f.setText("1");
            }
        } catch (NumberFormatException e) {
            f.setText("");
        }
        if (f.getControlNewText().isEmpty()) {
            f.setText("1");
        }
        return f;
    }
}