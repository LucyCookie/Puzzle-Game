package f15.ssui.p1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiqu on 9/12/15.
 */
public class Puzzle {
    static public Map<Integer, Integer> setPicture(GameActivity game, File picDir) {//clipping method from http://blog.csdn.net/yangdeli888/article/details/7894748
        if (!game.tc.exists()) {//if the first time to start the app
            Bitmap puzzlePic = BitmapFactory.decodeFile(picDir + "/Puzzle");//get the default puzzle pic
            RelativeLayout puzzleLayout = (RelativeLayout) game.findViewById(R.id.Puzzle);//get the puzzle layout

            //scale the pic according to the puzzle layout
            int gameWidth = puzzleLayout.getWidth();
            int gameHeight = game.findViewById(R.id.Puzzle).getHeight();
            int picWidth = puzzlePic.getWidth();
            int picHeight = puzzlePic.getHeight();
            Bitmap squarePic;
            //cut the default pic to square
            if (picHeight > picWidth)
                squarePic = Bitmap.createBitmap(puzzlePic, 0, (picHeight - picWidth) / 2, picWidth, picWidth);
            else
                squarePic = Bitmap.createBitmap(puzzlePic, (picWidth - picHeight) / 2, 0, picHeight, picHeight);
            int divideWidth = squarePic.getWidth() / 3;
            Matrix scale = new Matrix();
            int targetWidth;
            //get the desired width of the square area of the puzzle
            if (gameHeight > gameWidth) targetWidth = gameWidth;
            else targetWidth = gameHeight;
            float scaleRatio = (float) targetWidth / squarePic.getWidth();//get the scale ration
            scale.postScale(scaleRatio, scaleRatio);

            //save the puzzle pieces to app folder
            try {
                FileOutputStream puzzlePieceOutput = new FileOutputStream(game.puzzleFile);
                Bitmap showPuzzle=Bitmap.createBitmap(squarePic,0,0,squarePic.getWidth(),squarePic.getWidth(),scale,true);
                showPuzzle.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                puzzlePieceOutput = new FileOutputStream(game.tl);
                int[] colors = new int[targetWidth * targetWidth / 9];
                for (int i = 0; i < colors.length; i++) {
                    colors[i] = 0x888888;
                }
                Bitmap tlpic = Bitmap.createBitmap(colors, targetWidth / 3, targetWidth / 3, Bitmap.Config.RGB_565);
                tlpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap tcpic = Bitmap.createBitmap(squarePic, divideWidth, 0, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.tc);
                tcpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap trpic = Bitmap.createBitmap(squarePic, 2 * divideWidth, 0, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.tr);
                trpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap clpic = Bitmap.createBitmap(squarePic, 0, divideWidth, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.cl);
                clpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap ccpic = Bitmap.createBitmap(squarePic, divideWidth, divideWidth, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.cc);
                ccpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap crpic = Bitmap.createBitmap(squarePic, 2 * divideWidth, divideWidth, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.cr);
                crpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap blpic = Bitmap.createBitmap(squarePic, 0, 2 * divideWidth, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.bl);
                blpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap bcpic = Bitmap.createBitmap(squarePic, divideWidth, 2 * divideWidth, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.bc);
                bcpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                Bitmap brpic = Bitmap.createBitmap(squarePic, 2 * divideWidth, 2 * divideWidth, divideWidth, divideWidth, scale, true);
                puzzlePieceOutput = new FileOutputStream(game.br);
                brpic.compress(Bitmap.CompressFormat.PNG, 100, puzzlePieceOutput);
                puzzlePieceOutput.flush();

                puzzlePieceOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //get the random mapping of the blocks and the pics
        Map<Integer, Integer> puzzleMap = randomPuzzle();

        //put the pics to blocks according to the mapping
        placePuzzles(puzzleMap,game);

        //set the score to 0 when the game is newly started
        game.score = 0;
        TextView scoreText = (TextView) game.findViewById(R.id.score);
        scoreText.setText("score:0");

        return puzzleMap;
    }

    //get the random map of the blocks and the pics
    private static Map<Integer, Integer> randomPuzzle() {
        Map<Integer, Integer> reversedMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> puzzleMap = new HashMap<Integer, Integer>();
        int number;
        List<String> records = new ArrayList<String>();
        for (int i = 1; i < 10; i++) records.add(Integer.toString(i));
        while (reversedMap.size() < 9) {
            if (records.size() == 1) reversedMap.put(Integer.valueOf(records.get(0)), R.id.br);//only random for the first 8, then the last one is set
            else {
                number = (int) (Math.random() * 9 + 1);
                if (reversedMap.get(number) == null) {
                    records.remove(Integer.toString(number));
                    switch (reversedMap.size()) {
                        case 0: {
                            reversedMap.put(number, R.id.tl);
                            break;
                        }
                        case 1: {
                            reversedMap.put(number, R.id.tc);
                            break;
                        }
                        case 2: {
                            reversedMap.put(number, R.id.tr);
                            break;
                        }
                        case 3: {
                            reversedMap.put(number, R.id.cl);
                            break;
                        }
                        case 4: {
                            reversedMap.put(number, R.id.cc);
                            break;
                        }
                        case 5: {
                            reversedMap.put(number, R.id.cr);
                            break;
                        }
                        case 6: {
                            reversedMap.put(number, R.id.bl);
                            break;
                        }
                        case 7: {
                            reversedMap.put(number, R.id.bc);
                            break;
                        }
                        case 8: {
                            reversedMap.put(number, R.id.br);
                            break;
                        }
                    }
                }
            }
        }
        //reverse the map so it's easier to use for other parts
        for (Map.Entry entry : reversedMap.entrySet()) {
            puzzleMap.put((Integer) entry.getValue(), (Integer) entry.getKey());
        }
        return puzzleMap;
    }

    //place the pics according to the map
    public static void placePuzzles(Map<Integer, Integer> puzzleMap, GameActivity game){
        for (Map.Entry entry : puzzleMap.entrySet()) {
            ImageButton puzzlePiece = (ImageButton) game.findViewById((Integer) entry.getKey());
            switch ((int) entry.getValue()) {
                case 1: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.tl));//http://stackoverflow.com/questions/3870638/how-to-use-setimageuri-on-android
                    break;
                }
                case 2: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.tc));
                    break;
                }
                case 3: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.tr));
                    break;
                }
                case 4: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.cl));
                    break;
                }
                case 5: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.cc));
                    break;
                }
                case 6: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.cr));
                    break;
                }
                case 7: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.bl));
                    break;
                }
                case 8: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.bc));
                    break;
                }
                case 9: {
                    puzzlePiece.setImageURI(Uri.fromFile(game.br));
                    break;
                }
            }
        }
    }
}
