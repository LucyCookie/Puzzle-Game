package f15.ssui.p1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * The main activity class acting as the controller for the puzzle game. It handles the events for
 * the game layout and implements the game logic.
 *
 * @author qiqu
 */
public class GameActivity extends AppCompatActivity {

    File picDir = new File(Environment.getExternalStorageDirectory(), "/Puzzle/Pictures");//file directory for the game app

    boolean firstCreate = true;

    Map<Integer, Integer> puzzleMap;//<id, number of the piece>

    PuzzleMap staticMap=new PuzzleMap();//the map of pic and the puzzle blocks

    int score=0, lastMoveID;

    //files of the game
    File puzzleFile = new File(picDir, "PuzzleForShow");
    File tc = new File(picDir, "tc");
    File tl = new File(picDir, "tl");
    File tr = new File(picDir, "tr");
    File cl = new File(picDir, "cl");
    File cc = new File(picDir, "cc");
    File cr = new File(picDir, "cr");
    File bl = new File(picDir, "bl");
    File bc = new File(picDir, "bc");
    File br = new File(picDir, "br");

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState==null) {

            setContentView(R.layout.activity_game);

            firstCreate = true;

//            gameActivity = this;

            //http://blog.csdn.net/smking/article/details/8255621
            //get the default puzzle pic to the app folder
            if (!picDir.exists()) {
                System.out.println((picDir.mkdirs()));
                File puzzlePic = new File(picDir, "Puzzle");
                BitmapDrawable puzzleDrawable = (BitmapDrawable) this.getResources().getDrawable(R.drawable.puzzle);
                Bitmap puzzle = puzzleDrawable.getBitmap();
                try {
                    FileOutputStream fileOut = new FileOutputStream(puzzlePic);
                    puzzle.compress(Bitmap.CompressFormat.PNG, 100, fileOut);
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {//deal with the rotation
            firstCreate=false;
            staticMap= (PuzzleMap) savedInstanceState.getSerializable("staticMap");//retrieve the map to draw the puzzle panel and get the correct score
//            puzzleMap=staticMap.puzzleMap;//TODO
            if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE) setContentView(R.layout.activity_game_land);
            else setContentView(R.layout.activity_game);
            Puzzle.placePuzzles(staticMap.puzzleMap,this);
            updateScore();
//            Log.d("cookie","no first");
        }

        // Find the new game button in the layout.
//        ImageButton newGameButton = (ImageButton) findViewById(R.id.newGame);

        // Register the onClick listener to detect when the users selects a new game.
        // When the user clicks this button, the game should reset the code to 0 and shuffle the
        // puzzle.
    }

    //on rotation, keep the map unchanged
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("staticMap", staticMap);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //reference: http://tekeye.biz/2013/android-view-screen-size
    //initialize the map
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (firstCreate) {
            staticMap.puzzleMap = Puzzle.setPicture(this, picDir);
            firstCreate = false;
//            staticMap.puzzleMap=puzzleMap;

//            Log.d("cookie","first to port");
        }
    }

    //do when newGame button is clicked
    public void newGame(View v) {

        //reset the last move piece if the last move button was clicked before
        ImageButton lastMove=(ImageButton)findViewById(lastMoveID);
        if (lastMove!=null){
            lastMove.setBackgroundColor(Color.TRANSPARENT);
        }
        lastMoveID=0;
//        Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show();

        // shuffles the puzzle here.
        staticMap.puzzleMap=Puzzle.setPicture(this, picDir);

        // Reset the score.
        TextView scoreText = (TextView) findViewById(R.id.score);
        scoreText.setText("score:0");
    }

    public void clickPuzzle(View v) {
        //reset the last move piece if the last move button was clicked before
        ImageButton lastMove=(ImageButton)findViewById(lastMoveID);
        if (lastMove!=null){
            lastMove.setBackgroundColor(Color.TRANSPARENT);
        }
        if (staticMap.puzzleMap.get(v.getId()) != 1) {//if not click on the blank piece
            switch (v.getId()) {//deal with each piece according to the position
                case R.id.tl: {//find the blank piece first and switch the pics, if no blank piece around do nothing. Same for all
                    if (staticMap.puzzleMap.get(R.id.tc) == 1) {
                        ((ImageButton) findViewById(R.id.tc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        //update the map and the last move block
                        staticMap.puzzleMap.put(R.id.tc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.tc;
                    } else if (staticMap.puzzleMap.get(R.id.cl) == 1) {
                        ((ImageButton) findViewById(R.id.cl)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cl, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cl;
                    }
                    break;
                }
                case R.id.tc: {
                    if (staticMap.puzzleMap.get(R.id.tl) == 1) {
                        ((ImageButton) findViewById(R.id.tl)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.tl, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.tl;
                    } else if (staticMap.puzzleMap.get(R.id.tr) == 1) {
                        ((ImageButton) findViewById(R.id.tr)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.tr, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.tr;
                    } else if (staticMap.puzzleMap.get(R.id.cc) == 1) {
                        ((ImageButton) findViewById(R.id.cc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cc;
                    }
                    break;
                }
                case R.id.tr: {
                    if (staticMap.puzzleMap.get(R.id.tc) == 1) {
                        ((ImageButton) findViewById(R.id.tc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.tc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.tc;
                    } else if (staticMap.puzzleMap.get(R.id.cr) == 1) {
                        ((ImageButton) findViewById(R.id.cr)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cr, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cr;
                    }
                    break;
                }
                case R.id.cl: {
                    if (staticMap.puzzleMap.get(R.id.tl) == 1) {
                        ((ImageButton) findViewById(R.id.tl)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.tl, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.tl;
                    } else if (staticMap.puzzleMap.get(R.id.cc) == 1) {
                        ((ImageButton) findViewById(R.id.cc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cc;
                    } else if (staticMap.puzzleMap.get(R.id.bl) == 1) {
                        ((ImageButton) findViewById(R.id.bl)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.bl, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.bl;
                    }
                    break;
                }
                case R.id.cc: {
                    if (staticMap.puzzleMap.get(R.id.tc) == 1) {
                        ((ImageButton) findViewById(R.id.tc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.tc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.tc;
                    } else if (staticMap.puzzleMap.get(R.id.cl) == 1) {
                        ((ImageButton) findViewById(R.id.cl)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cl, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cl;
                    } else if (staticMap.puzzleMap.get(R.id.bc) == 1) {
                        ((ImageButton) findViewById(R.id.bc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.bc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.bc;
                    } else if (staticMap.puzzleMap.get(R.id.cr) == 1) {
                        ((ImageButton) findViewById(R.id.cr)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cr, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cr;
                    }
                    break;
                }
                case R.id.cr: {
                    if (staticMap.puzzleMap.get(R.id.tr) == 1) {
                        ((ImageButton) findViewById(R.id.tr)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.tr, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.tr;
                    } else if (staticMap.puzzleMap.get(R.id.cc) == 1) {
                        ((ImageButton) findViewById(R.id.cc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cc;
                    } else if (staticMap.puzzleMap.get(R.id.br) == 1) {
                        ((ImageButton) findViewById(R.id.br)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.br, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.br;
                    }
                    break;
                }
                case R.id.bl: {
                     if (staticMap.puzzleMap.get(R.id.cl) == 1) {
                         ((ImageButton) findViewById(R.id.cl)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                         staticMap.puzzleMap.put(R.id.cl, staticMap.puzzleMap.get(v.getId()));
                         staticMap.puzzleMap.put(v.getId(), 1);
                         lastMoveID=R.id.cl;
                    } else if (staticMap.puzzleMap.get(R.id.bc) == 1) {
                         ((ImageButton) findViewById(R.id.bc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                         staticMap.puzzleMap.put(R.id.bc, staticMap.puzzleMap.get(v.getId()));
                         staticMap.puzzleMap.put(v.getId(), 1);
                         lastMoveID=R.id.bc;
                    }
                    break;
                }
                case R.id.bc: {
                    if (staticMap.puzzleMap.get(R.id.bl) == 1) {
                        ((ImageButton) findViewById(R.id.bl)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.bl, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.bl;
                    } else if (staticMap.puzzleMap.get(R.id.cc) == 1) {
                        ((ImageButton) findViewById(R.id.cc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cc;
                    } else if (staticMap.puzzleMap.get(R.id.br) == 1) {
                        ((ImageButton) findViewById(R.id.br)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.br, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.br;
                    }
                    break;
                }
                case R.id.br: {
                    if (staticMap.puzzleMap.get(R.id.cr) == 1) {
                        ((ImageButton) findViewById(R.id.cr)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.cr, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.cr;
                    } else if (staticMap.puzzleMap.get(R.id.bc) == 1) {
                        ((ImageButton) findViewById(R.id.bc)).setImageDrawable(((ImageButton) v).getDrawable());
                        ((ImageButton) v).setImageURI(Uri.fromFile(tl));
                        staticMap.puzzleMap.put(R.id.bc, staticMap.puzzleMap.get(v.getId()));
                        staticMap.puzzleMap.put(v.getId(), 1);
                        lastMoveID=R.id.bc;
                    }
                    break;
                }
            }
            //update score
            updateScore();
        }
    }

    public void updateScore(){
        int count=0, pieceNum;
        for (Map.Entry entry: staticMap.puzzleMap.entrySet()){
            pieceNum= (int) entry.getValue();
            switch ((Integer)entry.getKey()){//judge if the block has the corresponding pic
                case R.id.tl:{
                    if (pieceNum==1) count++;
                    break;
                }
                case R.id.tc:{
                    if (pieceNum==2) count++;
                    break;
                }
                case R.id.tr:{
                    if(pieceNum==3) count++;
                    break;
                }
                case R.id.cl:{
                    if (pieceNum==4) count++;
                    break;
                }
                case R.id.cc:{
                    if (pieceNum==5) count++;
                    break;
                }
                case R.id.cr:{
                    if (pieceNum==6) count++;
                    break;
                }
                case R.id.bl:{
                    if (pieceNum==7) count++;
                    break;
                }
                case R.id.bc:{
                    if (pieceNum==8) count++;
                    break;
                }
                case R.id.br:{
                    if (pieceNum==9) count++;
                    break;
                }
            }
        }
        if (count>score){//only increase score, never decrease. Show the highest score that the play has got in the game
            score=count;
            TextView scoreText = (TextView) findViewById(R.id.score);
            scoreText.setText("score:" + score);
        }
        //if the puzzle is fixed, show the message that the player wins
        if (score==9) Toast.makeText(this, "You WIN!!!", Toast.LENGTH_LONG).show();
    }

    //show the last move when lastMove button clicked
    public void lastMove(View v){
        ImageButton lastMove=(ImageButton)findViewById(lastMoveID);
        if (lastMove!=null){
            lastMove.setBackgroundColor(Color.RED);
        } else Toast.makeText(this, "No last move", Toast.LENGTH_LONG).show();
    }

    //show the original (square) picture of the puzzle
    public void showPuzzle(View v){
        View show=findViewById(R.id.OriginalPuzzle);
        ImageView puzzlePic= (ImageView) findViewById(R.id.PuzzlePic);
        show.bringToFront();
        show.setVisibility(View.VISIBLE);
        puzzlePic.setImageURI(Uri.fromFile(puzzleFile));
    }

    //hide the original (square) picture of the puzzle when user click on the screen
    public void hidePuzzle(View v){
        View show=findViewById(R.id.OriginalPuzzle);
        show.setVisibility(View.INVISIBLE);
    }
}

//to retain the map for the rotation
 class PuzzleMap implements Serializable {
     Map<Integer, Integer> puzzleMap;
     public PuzzleMap() {
         try {
             Thread.sleep(1);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }
 }