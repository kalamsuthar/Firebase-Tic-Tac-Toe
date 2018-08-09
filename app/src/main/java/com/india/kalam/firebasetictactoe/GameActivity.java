package com.india.kalam.firebasetictactoe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {

    private static  final  String TAG = GameActivity.class.getSimpleName();

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    User loggedInUser;
    private String userId;

    TextView playerText, turn;

    Button GameButton1, GameButton2, GameButton3,
            GameButton4, GameButton5, GameButton6,
            GameButton7, GameButton8, GameButton9,
            choosePlayerScreen;

    ImageView imageview1, imageview2, imageview3,
            imageview4, imageview5, imageview6,
            imageview7, imageview8, imageview9,
            imageview10, boardView;

    // represents buttons clicked
    int[] gameBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // initializes all of the buttons, image views, and text views
        playerText = (TextView) findViewById(R.id.playerTextView);
        turn = (TextView) findViewById(R.id.turnTextView);

        GameButton1 = (Button) findViewById(R.id.GameButton1);
        GameButton2 = (Button) findViewById(R.id.GameButton2);
        GameButton3 = (Button) findViewById(R.id.GameButton3);
        GameButton4 = (Button) findViewById(R.id.GameButton4);
        GameButton5 = (Button) findViewById(R.id.GameButton5);
        GameButton6 = (Button) findViewById(R.id.GameButton6);
        GameButton7 = (Button) findViewById(R.id.GameButton7);
        GameButton8 = (Button) findViewById(R.id.GameButton8);
        GameButton9 = (Button) findViewById(R.id.GameButton9);
        choosePlayerScreen = (Button) findViewById(R.id.choosePlayerMenu);

        imageview1 = (ImageView) findViewById(R.id.imageView1);
        imageview2 = (ImageView) findViewById(R.id.imageView2);
        imageview3 = (ImageView) findViewById(R.id.imageView3);
        imageview4 = (ImageView) findViewById(R.id.imageView4);
        imageview5 = (ImageView) findViewById(R.id.imageView5);
        imageview6 = (ImageView) findViewById(R.id.imageView6);
        imageview7 = (ImageView) findViewById(R.id.imageView7);
        imageview8 = (ImageView) findViewById(R.id.imageView8);
        imageview9 = (ImageView) findViewById(R.id.imageView9);
        imageview10=(ImageView) findViewById(R.id.imageView10);
        boardView=(ImageView)findViewById(R.id.tictactoeView);

        // draws the game board
        drawGameBoard();

        // initializes the game board array to 0
        gameBoard=new int[9];
        for(int i=0;i<gameBoard.length;i++)
            gameBoard[i]=0;

        // sets the on click listener for all of the game buttons
        GameButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(0);
            }
        });

        GameButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(1);
            }
        });

        GameButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(2);
            }
        });

        GameButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(3);
            }
        });

        GameButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(4);
            }
        });

        GameButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(5);
            }
        });

        GameButton7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(6);
            }
        });

        GameButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(7);
            }
        });

        GameButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameTurn(8);
            }
        });

        // sends the user back to the mainActivity
        choosePlayerScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, ChoosePlayerActivity.class);
                startActivity(intent);
            }
        });

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        final FirebaseUser currentUserLoggedIn = FirebaseAuth.getInstance().getCurrentUser();

        userId = currentUserLoggedIn.getUid();

        addUserChangeListener();


    }

    public  void addUserChangeListener(){
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                Log.e(TAG, "User data has changed!" + user.name + ", " + user.email);
                loggedInUser = user;

                playerText.setText(loggedInUser.email + " vs. " + loggedInUser.opponentEmail);

                if (user.myGame.gameInProgress == false && user.currentlyPlaying == true) {
                    if (loggedInUser.myGame.currentLetter.equals("X")) {
                        // populates the gameBoard array, swapping x with y
                        gameBoard[user.myGame.currentMove] = 2;
                        printMove(user.myGame.currentMove, "O");
                    } else {
                        gameBoard[user.myGame.currentMove] = 1;
                        printMove(user.myGame.currentMove, "X");
                    }

                    // removes all of the buttons
                    removeAllButtons();

                    // make sure we draw the line
                    if (checkGameWon() == 0) ;
                    // checks of the game has been tied
                    checkTie();

                    // initialize game values
                    mFirebaseDatabase.child(loggedInUser.myID).child("myGame").child("gameInProgress").setValue(false);
                    mFirebaseDatabase.child(loggedInUser.myID).child("currentlyPlaying").setValue(false);

                    mFirebaseDatabase.child(loggedInUser.opponentID).child("myGame").child("gameInProgress").setValue(false);
                    mFirebaseDatabase.child(loggedInUser.opponentID).child("currentlyPlaying").setValue(false);

                    // initialize each users opponents
                    mFirebaseDatabase.child(loggedInUser.myID).child("opponentEmail").setValue("");
                    mFirebaseDatabase.child(loggedInUser.myID).child("opponentID").setValue("");

                    mFirebaseDatabase.child(loggedInUser.opponentID).child("opponentEmail").setValue("");
                    mFirebaseDatabase.child(loggedInUser.opponentID).child("opponentID").setValue("");

                    Toast.makeText(GameActivity.this, "GAME IS OVER", Toast.LENGTH_LONG).show();
                }
                // if my turn, draw board and enable buttons
                else if (user.myGame.currentTurn.equals(user.myID)) {
                    turn.setText("Your turn to make a move");
                    // make sure there was a move

                    if (user.myGame.currentMove != -1) {
                        if (loggedInUser.myGame.currentLetter.equals("X")) {

                            // populates the gameBoard array
                            gameBoard[user.myGame.currentMove] = 2;
                            printMove(user.myGame.currentMove, "O");
                        } else {
                            gameBoard[user.myGame.currentMove] = 1;
                            printMove(user.myGame.currentMove, "X");
                        }

                        // re-enable buttons
                        updateButtons();
                    }
                }
                else // not my turn
                {
                    // removes the button that was played
                    removeAllButtons();

                    turn.setText("Waiting for the other player");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG,"Failed to read user", databaseError.toException());
            }
        });
    }

    void gameTurn(int move){
        int gameWon = -1;
        boolean gameTie = false;

        // set the board to a move and print it
        if(loggedInUser.myGame.currentLetter.equals("X")) {
            gameBoard[move] = 1;
            printMove(move, "X");
        }
        else {
            gameBoard[move] = 2;
            printMove(move, "O");
        }

        // update the status of the buttons, graying out the ones that were played
        updateButtons();

        // checks if the game has been won or not
        gameWon = checkGameWon();

        // checks of the game has been tied
        gameTie = checkTie();

        // create game, set the database values
        Game game = new Game(loggedInUser.opponentID);
        game.currentMove = move;

        if(loggedInUser.myGame.currentLetter.equals("X"))
            game.currentLetter = "O";
        else
            game.currentLetter = "X";

        // if the game hasn't been won and the game hasn't been tied the other players turn is made
        if (gameWon == 0 && !gameTie) {
            game.gameInProgress = true;
        }
        // if the game has been won
        else if (gameWon != 0) {
            // removes all of the buttons
            removeAllButtons();
            game.gameInProgress = false;
        }
        else {
            playerText.setText("The game is a tie!");
            turn.setText("No one wins");
            game.gameInProgress = false;
        }

        // set data
        mFirebaseDatabase.child(loggedInUser.myID).child("myGame").setValue(game);
        mFirebaseDatabase.child(loggedInUser.opponentID).child("myGame").setValue(game);
    }

    // update status of buttons
    void updateButtons() {
        if (gameBoard[0] != 0) {
            GameButton1.setVisibility(View.INVISIBLE);
        }
        else
            GameButton1.setVisibility(View.VISIBLE);

        if (gameBoard[1] != 0) {
            GameButton2.setVisibility(View.INVISIBLE);
        }
        else
            GameButton2.setVisibility(View.VISIBLE);

        if (gameBoard[2] != 0) {
            GameButton3.setVisibility(View.INVISIBLE);
        }
        else
            GameButton3.setVisibility(View.VISIBLE);

        if (gameBoard[3] != 0) {
            GameButton4.setVisibility(View.INVISIBLE);
        }
        else
            GameButton4.setVisibility(View.VISIBLE);

        if (gameBoard[4] != 0) {
            GameButton5.setVisibility(View.INVISIBLE);
        }
        else
            GameButton5.setVisibility(View.VISIBLE);

        if (gameBoard[5] != 0) {
            GameButton6.setVisibility(View.INVISIBLE);
        }
        else
            GameButton6.setVisibility(View.VISIBLE);

        if (gameBoard[6] != 0) {
            GameButton7.setVisibility(View.INVISIBLE);
        }
        else
            GameButton7.setVisibility(View.VISIBLE);

        if (gameBoard[7] != 0) {
            GameButton8.setVisibility(View.INVISIBLE);
        }
        else
            GameButton8.setVisibility(View.VISIBLE);

        if (gameBoard[8] != 0) {
            GameButton9.setVisibility(View.INVISIBLE);
        }
        else
            GameButton9.setVisibility(View.VISIBLE);
    }

    // removes all of the buttons if it is not a players turn
    void removeAllButtons()
    {
        GameButton1.setVisibility(View.INVISIBLE);
        GameButton2.setVisibility(View.INVISIBLE);
        GameButton3.setVisibility(View.INVISIBLE);
        GameButton4.setVisibility(View.INVISIBLE);
        GameButton5.setVisibility(View.INVISIBLE);
        GameButton6.setVisibility(View.INVISIBLE);
        GameButton7.setVisibility(View.INVISIBLE);
        GameButton8.setVisibility(View.INVISIBLE);
        GameButton9.setVisibility(View.INVISIBLE);
    }

    /*  DRAW CODE */

    // draws an X at a specified location
    void drawX(int startx1, int starty1, int endx1, int endy1, int startx2, int starty2, int endx2, int endy2, ImageView imageView) {
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        canvas.drawLine(startx1, starty1, endx1, endy1, paint);
        canvas.drawLine(startx2, starty2, endx2, endy2, paint);
        imageView.setVisibility(View.VISIBLE);

        // animates the drawing to fade in
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        imageView.startAnimation(animation);
    }

    // draws an O at a specified location
    void drawO(float x, int y, ImageView imageView) {
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(x, y, 100, paint);
        imageView.setVisibility(View.VISIBLE);

        // animates the drawing to fade in
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        imageView.startAnimation(animation);
    }

    // draws a line at a specified location, when the game has won
    void drawLine(int startx, int starty, int endx, int endy, ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(startx, starty, endx, endy, paint);

        // animates the drawing to fade in
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(4000);
        imageView.startAnimation(animation);
    }

    // checks to see if the game has been won, if it has it draws a line over the winning squares
    int checkGameWon() {
        int sx, sy, ex, ey;
        Point size = new Point();
        // gets the size of the current display window
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        // win first row
        if (gameBoard[0] != 0 && gameBoard[0] == gameBoard[1] && gameBoard[0] == gameBoard[2])
        {
            sx= 0;
            sy=(int) (height * .30);

            ex= width;
            ey=(int) (height * .30);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[0];
        }
        // win second row
        else if(gameBoard[3]!=0&&gameBoard[3]==gameBoard[4]&& gameBoard[3]==gameBoard[5])
        {
            sx= 0;
            sy=(int) (height * .54);

            ex= width;
            ey=(int) (height * .54);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[3];
        }
        // win third row
        else if(gameBoard[6]!=0&&gameBoard[6]==gameBoard[7]&&gameBoard[6]==gameBoard[8])
        {
            sx= 0;
            sy=(int) (height * .77);

            ex= width;
            ey=(int) (height * .77);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[6];
        }
        // win first column
        else if (gameBoard[0]!=0&&gameBoard[0]==gameBoard[3]&&gameBoard[0]==gameBoard[6])
        {
            sx=(int) (width * .15);
            sy=(int) (height * .18);

            ex=(int) (width *.15);
            ey=(int) (height * .89);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[0];
        }
        // win second column
        else if (gameBoard[1]!=0&&gameBoard[1]==gameBoard[4]&&gameBoard[1]==gameBoard[7])
        {
            sx=(int) (width * .50);
            sy=(int) (height * .18);

            ex=(int) (width *.50);
            ey=(int) (height * .89);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[1];
        }
        // win third column
        else if(gameBoard[2]!=0&&gameBoard[2]==gameBoard[5]&&gameBoard[2]==gameBoard[8])
        {
            sx=(int) (width * .85);
            sy=(int) (height * .18);

            ex=(int) (width *.85);
            ey=(int) (height * .89);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[2];
        }
        // win diagonal \
        else if(gameBoard[0]!=0&&gameBoard[0]==gameBoard[4]&&gameBoard[0]==gameBoard[8])
        {
            sx=(int) (width * .01);
            sy=(int) (height * .21);

            ex=(int) (width *.99);
            ey=(int) (height * .86);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[0];
        }
        // win diagonal /
        else if(gameBoard[2]!=0&&gameBoard[2]==gameBoard[4]&&gameBoard[2]==gameBoard[6])
        {
            sx=(int) (width *.99);
            sy=(int) (height * .21);

            ex=(int) (width *.01);
            ey=(int) (height * .86);

            drawLine(sx, sy, ex, ey,imageview10);
            return gameBoard[2];
        }
        else
            // game not won
            return 0;
    }

    // check to see if the game is a tie
    boolean checkTie()
    {
        for (int aGameBoard : gameBoard) {
            if (aGameBoard == 0)
                return false;
        }
        return true;
    }

    // prints the move that was played
    void printMove(int move, String character)
    {
        int x, y;
        // gets the size of the display window
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);
        int width = size.x;
        int height = size.y;
        // switch statement for each move
        switch (move)
        {
            case 0:
                x=(int) (width*.15);
                y=(int) (height * .30);
                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview1);
                else
                    drawO(x,y,imageview1);
                break;
            case 1:
                x=(int) (width*.50);
                y=(int) (height * .30);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview2);
                else
                    drawO(x,y,imageview2);
                break;
            case 2:
                x=(int) (width*.85);
                y=(int) (height * .30);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview3);
                else
                    drawO(x,y,imageview3);
                break;
            case 3:
                x=(int) (width*.15);
                y=(int) (height * .54);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview4);
                else
                    drawO(x,y,imageview4);
                break;
            case 4:
                x=(int) (width*.50);
                y=(int) (height * .54);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview5);
                else
                    drawO(x,y,imageview5);
                break;

            case 5:
                x=(int) (width*.85);
                y=(int) (height * .54);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview6);
                else
                    drawO(x,y,imageview6);
                break;

            case 6:
                x=(int) (width*.15);
                y=(int) (height * .77);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview7);
                else
                    drawO(x,y,imageview7);
                break;

            case 7:
                x=(int) (width*.50);
                y=(int) (height * .77);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview8);
                else
                    drawO(x,y,imageview8);
                break;

            case 8:
                x=(int) (width*.85);
                y=(int) (height * .77);

                if(character.equals("X"))
                    drawX(x-100, y-100, x+100, y+100, x+100, y-100, x-100, y+100, imageview9);
                else
                    drawO(x,y,imageview9);
        }
    }

    // draws the game board on the screen
    private void drawGameBoard()
    {
        boardView.setVisibility(View.VISIBLE);
        Bitmap bitmap = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        boardView.setImageBitmap(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(20);
        paint.setStyle(Paint.Style.STROKE);

        int sx, sy, ex, ey, x, y;

        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();

        display.getSize(size);
        int width = size.x;
        int height = size.y;

        sx= 0;
        sy=(int)(height*.42);
        ex= width;
        ey=(int)(height*.42);

        canvas.drawLine(sx, sy, ex, ey, paint);

        sx= 0;
        sy=(int)(height*.65);
        ex= width;
        ey=(int)(height*.65);

        canvas.drawLine(sx, sy, ex, ey, paint);

        sx=(int)(width*.32);
        sy=(int)(height*.18);
        ex=(int)(width*.32);
        ey=(int)(height*.89);

        canvas.drawLine(sx, sy, ex, ey, paint);

        sx=(int)(width*.68);
        sy=(int)(height*.18);
        ex=(int)(width*.68);
        ey=(int)(height*.89);

        canvas.drawLine(sx, sy, ex, ey, paint);

        x=(int)(width*.07);
        y=(int)(height*.22);

        GameButton1.setX(x);
        GameButton1.setY(y);

        x=(int)(width*.38);
        y=(int)(height*.22);

        GameButton2.setX(x);
        GameButton2.setY(y);

        x=(int)(width*.69);
        y=(int)(height*.22);

        GameButton3.setX(x);
        GameButton3.setY(y);

        x=(int)(width*.07);
        y=(int)(height*.43);

        GameButton4.setX(x);
        GameButton4.setY(y);

        x=(int)(width*.38);
        y=(int)(height*.43);

        GameButton5.setX(x);
        GameButton5.setY(y);

        x=(int)(width*.69);
        y=(int)(height*.43);

        GameButton6.setX(x);
        GameButton6.setY(y);

        x=(int)(width*.07);
        y=(int)(height*.64);

        GameButton7.setX(x);
        GameButton7.setY(y);

        x=(int)(width*.38);
        y=(int)(height*.64);

        GameButton8.setX(x);
        GameButton8.setY(y);

        x=(int)(width*.69);
        y=(int)(height*.64);

        GameButton9.setX(x);
        GameButton9.setY(y);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                Toast.makeText(this, "Clicked Logoff", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(GameActivity.this, RegisterActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
