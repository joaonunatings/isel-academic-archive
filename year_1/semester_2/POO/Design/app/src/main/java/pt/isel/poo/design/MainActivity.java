package pt.isel.poo.design;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener  {


    public static final String FILE ="design.txt";
    private Design model;
    private Figures currentFigure;
    private DesignView view;
    private Button resetButton,loadButton,saveButton;
    RadioGroup radioGroup;
    boolean line = true,rect = false,circle = false ,pixel = false;



    @Override

 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new Design();
        view = findViewById(R.id.designView);
        view.setModel(model);
        view.setOnTouchListener(this);

        resetButton = findViewById(R.id.resetButton);
        saveButton = findViewById(R.id.saveButton);
        loadButton = findViewById(R.id.loadButton);
        radioGroup = findViewById(R.id.radioGroup);
        updateUI();

        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                model.reset();
                updateUI();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try (PrintStream output = new PrintStream(openFileOutput(FILE, MODE_PRIVATE))) {
                    model.save(output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });




        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try (Scanner input = new Scanner(openFileInput(FILE))) {
                    view.setModel(model);
                    view.postInvalidate();
                    Design.createFrom(input);
                    updateUI();
                } catch (FileNotFoundException e) {
                    // TODO: Error handling for real
                    e.printStackTrace();
                }
            }
        });
    }

    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch(v.getId()) {
            case R.id.lineButton:

                if (checked){
                    line = true;
                    pixel = false;
                    circle = false;
                    rect = false;
                    break;
                }

            case R.id.rectButton:

                if (checked) {

                    line = false;
                    pixel = false;
                    circle = false;
                    rect = true;
                    break;
                }
            case R.id.pixelButton:
                if (checked){
                    line = false;
                    pixel = true;
                    circle = false;
                    rect = false;
                    break;
                }


            case R.id.circleButton:

                if (checked){
                    line = false;
                    pixel = false;
                    circle = true;
                    rect = false;
                    break;
                }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentFigure = new Figures();
                if (line) currentFigure.setFigure('L');
                if (pixel) currentFigure.setFigure('P');
                if (circle) currentFigure.setFigure('C');
                if (rect) currentFigure.setFigure('R');

                currentFigure.setPointA(new Point((int) event.getX(), (int) event.getY()));
                return true;

            case MotionEvent.ACTION_UP:

                model.finalList.add(currentFigure);
                model.list.clear();
                view.postInvalidate();


                return true;
            case MotionEvent.ACTION_MOVE:
                model.list.add(currentFigure);
                view.postInvalidate();

                currentFigure.setPointB(new Point((int) event.getX(), (int) event.getY()));
                if (currentFigure.getFigure() =='C') {
                    currentFigure.setRadius(currentFigure.getPointA(),currentFigure.getPointB());
                }


                return true;
        }
        return false;
    }

    void updateUI() {
        view.postInvalidate();
    }



}
