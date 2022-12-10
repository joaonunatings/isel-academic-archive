package pt.isel.poo.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.TimeAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import pt.isel.poo.covid.model.Direction;
import pt.isel.poo.covid.model.Hero;
import pt.isel.poo.covid.model.Level;
import pt.isel.poo.covid.model.Virus;
import pt.isel.poo.covid.tile.OnTileTouchListener;
import pt.isel.poo.covid.tile.TilePanel;
import pt.isel.poo.covid.view.LevelView;

public class MainActivity extends AppCompatActivity {

    private final String FILENAME = "covid_levels.txt";
    private final String SAVEFILE = "saved_level.txt";
    private final String ORIENTATIONSAVE = "orientation_save_file.txt";
    public int currentLevel = 1;
    private Level level ;
    private Scanner in  ;
    private LevelView view;
    private Hero hero;
    private ArrayList <Virus> virusList;
    TextView gameStateText;
    TextView virusText;
    TextView levelText;
    Button okButton;

    private final Map<Integer, Direction> directions = new HashMap<>();{
        directions.put(R.id.leftButton, Direction.WEST);
        directions.put(R.id.rightButton, Direction.EAST);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final TilePanel panel = findViewById(R.id.levelView);

        panel.setListener(new OnTileTouchListener() {
            @Override
            public boolean onClick(int xTile, int yTile) {
                if(Math.abs(hero.getPosition().y - yTile) < xTile - hero.getPosition().x ) hero.changeDirection(Direction.EAST);
                if(-Math.abs(hero.getPosition().y - yTile) > xTile - hero.getPosition().x) hero.changeDirection(Direction.WEST);
                return false;
            }

            @Override
            public boolean onDrag(int xFrom, int yFrom, int xTo, int yTo) {
                return false;
            }

            @Override
            public void onDragEnd(int x, int y) {

            }

            @Override
            public void onDragCancel() {

            }
        });

        virusText = findViewById(R.id.virusText);
        levelText = findViewById(R.id.levelText);
        //first initialization
        if(savedInstanceState == null) {

            try {
                initializeLevel(FILENAME, currentLevel, panel);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Loader.LevelFormatException e) {
                e.printStackTrace();
            }
        }
        else{
            //initialization when there is a screen orientation change.
            try {
                SharedPreferences prefs = getSharedPreferences("PreferencesName", MODE_PRIVATE);
                currentLevel = prefs.getInt("currentLevel", 0);
                loadSavedLevel(ORIENTATIONSAVE,currentLevel,panel);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Loader.LevelFormatException e) {
                e.printStackTrace();
            }
        }


        //save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try (PrintStream output = new PrintStream(openFileOutput(SAVEFILE, MODE_PRIVATE))) {
                    SharedPreferences.Editor editor = getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
                    editor.putInt("savedLevel", currentLevel);
                    editor.apply();

                    if(level!=null)level.save(output,currentLevel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View source) {
                hero.changeDirection(directions.get(source.getId()));
            }
        };

        findViewById(R.id.leftButton).setOnClickListener(listener);
        findViewById(R.id.rightButton).setOnClickListener(listener);

        //the game state text that starts hidden
        gameStateText = findViewById(R.id.gameStateMessage);

        //hidden ok button
        okButton = findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String gameState = gameStateText.getText().toString();

                if(gameState.equals(getResources().getString(R.string.nothingToLoad))) hideButtons();

                if(gameState.equals(getResources().getString(R.string.gameOver))) quit();

                if(gameState.equals(getResources().getString(R.string.noMoreLevels))) quit();

                if(gameState.equals(getResources().getString(R.string.levelCompleted))){
                    try {
                        currentLevel++;
                        initializeLevel(FILENAME,currentLevel,panel);
                        hideButtons();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Loader.LevelFormatException e) {
                        e.printStackTrace();
                    }
                    if(level == null){
                        showButtons(getResources().getString(R.string.noMoreLevels));
                        currentLevel--;
                    }
                }
            }
        });

        Button loadButton = findViewById(R.id.loadButton);

        //load saved file
        loadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if(fileExists(MainActivity.this)) {

                        SharedPreferences prefs = getSharedPreferences("PreferencesName", MODE_PRIVATE);
                        currentLevel = prefs.getInt("savedLevel", 0);
                        if(level!=null) {
                            loadSavedLevel(SAVEFILE,currentLevel,panel);
                            hideButtons();
                        }
                    }
                    else {
                        showButtons(getResources().getString(R.string.nothingToLoad));

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Loader.LevelFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        final TimeAnimator animator = new TimeAnimator();
        animator.setTimeListener(new TimeAnimator.TimeListener() {
            int elapsedTime = 0;
            int interval = 350;

            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {


                if(level!=null)
                    if(!hero.isDead() && level.getVirusCount() > 0){
                        //Movement caused by gravity
                        if (elapsedTime >= interval) {
                            elapsedTime = 0;
                            hero.changeDirection(Direction.SOUTH);
                            heroMove();
                            virusMoveDown();
                            updateText();

                        }
                    //Movement caused by player
                        else{
                            elapsedTime += deltaTime;
                            if(hero.getHeroDirection() == Direction.EAST || hero.getHeroDirection() == Direction.WEST){
                                //Virus movement
                                virusMove(hero.getHeroDirection());
                                //Player movement
                                heroMove();
                                //Reset time
                                elapsedTime = 0 ;
                            }
                        }
                    }
                // Player is dead or no more virus on the current level.
                else{
                    //Level Completed / No more Virus
                    if(virusList.size() == 0){
                        showButtons(getResources().getString(R.string.levelCompleted));

                    }
                    //Player is dead
                    else{
                        showButtons(getResources().getString(R.string.gameOver));
                    }
                }
            }
        });
        animator.start();
    }

    public  void setLevel(Scanner in, int currentLevel) throws Loader.LevelFormatException {
        Loader loader = new Loader(in);
        level =  loader.load(currentLevel);
    }


    public void initializeLevel(String fileName,int currentLevel,TilePanel panel) throws IOException, Loader.LevelFormatException {

        in = new Scanner(getAssets().open(fileName));
        setLevel(in,currentLevel);
        if(level!=null){
            view = new LevelView(panel,level);
            hero = level.getHero();
            virusList = level.getVirusList();
            updateText();

        }
    }

    public void heroMove(){
        hero.move();
        hero.changeDirection(Direction.NONE);

    }

    public void virusMove(Direction direction){
        Iterator<Virus> itr = virusList.iterator();
        while (itr.hasNext()){
            Virus virus = itr.next();
            if(hero.getPosition().add(direction).x == virus.getPosition().x &&
            hero.getPosition().add(direction).y == virus.getPosition().y){
                virus.changeDirection(direction);
                virus.move();
                virus.changeDirection(Direction.NONE);


            }
        }
    }

    public void virusMoveDown(){
        Iterator<Virus> itr = virusList.iterator();
        while (itr.hasNext()){
            Virus virus = itr.next();
            virus.changeDirection(Direction.SOUTH);
            if(virus.isDead())itr.remove();
                virus.move();
                updateText();
                virus.changeDirection(Direction.NONE);

        }
    }

    public void quit(){
        finish();
        System.exit(0);
    }

    public void hideButtons(){
        gameStateText.setVisibility(View.INVISIBLE);
        okButton.setVisibility(View.INVISIBLE);
    }

    public void showButtons(String message){
        gameStateText.setVisibility(View.VISIBLE);
        gameStateText.setText(message);
        okButton.setVisibility(View.VISIBLE);
    }

    public void updateText(){
        levelText.setText(getResources().getString(R.string.level)+ currentLevel);
        virusText.setText(getResources().getString(R.string.virus) + virusList.size());
    }

    public boolean fileExists(Context context) {
        File file = context.getFileStreamPath(SAVEFILE);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public void loadSavedLevel(String fileName,int currentLevel,TilePanel panel) throws IOException, Loader.LevelFormatException {

        in = new Scanner(openFileInput(fileName));
        setLevel(in,currentLevel);
        view = new LevelView(panel,level);
        hero = level.getHero();
        virusList = level.getVirusList();
        updateText();

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        try (PrintStream output = new PrintStream(openFileOutput(ORIENTATIONSAVE, MODE_PRIVATE))) {
            level.save(output,currentLevel);
            SharedPreferences.Editor editor = getSharedPreferences("PreferencesName", MODE_PRIVATE).edit();
            editor.putInt("currentLevel", currentLevel);
            editor.apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

