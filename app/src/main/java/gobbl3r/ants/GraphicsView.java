package gobbl3r.ants;

import gobbl3r.cards.CardConjureCrystals;
import gobbl3r.cards.CardKnight;
import gobbl3r.cards.CardTower;
import gobbl3r.cards.CardWall;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class GraphicsView extends View implements OnTouchListener {
	private static final String TAG = "GraphicsView";
		
    Paint paint = new Paint();
    
    /*
	 * number of card slots on board
	 */
	private final int num_of_slots = 8;
    
    List<Card> cards = new ArrayList<Card>();
    
    Bitmap bmp_background 	= BitmapFactory.decodeResource(getResources(), R.drawable.bg_800x480);
    Bitmap bmp_gate 		= BitmapFactory.decodeResource(getResources(), R.drawable.gate);
    
    Bitmap bmp_tower1 		= BitmapFactory.decodeResource(getResources(), R.drawable.tower_red);
    Bitmap bmp_tower2 		= BitmapFactory.decodeResource(getResources(), R.drawable.tower_yellow);
    
    Bitmap bmp_ferda1 		= BitmapFactory.decodeResource(getResources(), R.drawable.ferda1);
    Bitmap bmp_ferda2 		= BitmapFactory.decodeResource(getResources(), R.drawable.ferda2);
    
    Bitmap bmp_winner1		= BitmapFactory.decodeResource(getResources(), R.drawable.ferda1_win);
    Bitmap bmp_winner2 		= BitmapFactory.decodeResource(getResources(), R.drawable.ferda2_win);
    
    // Display metrics
    //private final float scale = getResources().getDisplayMetrics().density;
    private final int defaultWidth 	= 800;//px
    private final int defaultHeight = 480;//px
    private final int dWidth		= getResources().getDisplayMetrics().widthPixels;
    private final int dHeight		= getResources().getDisplayMetrics().heightPixels;
    private final float scaleW  	= dWidth/defaultWidth;
    private final float scaleH  	= dHeight/defaultHeight;
    
    // define position and size of elements
    private final int statsW 		= (int) (130*scaleW);
    private final int statsH 		= (int) (70*scaleH);
    private final int statsPadV 	= (int) (30*scaleH);
    private final int statsPadH 	= (int) (10*scaleW);
    private final int statsPadB 	= (int) (10*scaleH);
    private final int statsFontSize	= (int) (15*scaleH);
    private final int statsFontPad	= (int) (9*scaleH);
    private final int scoreFontSize	= (int) (25*scaleH);
    
    //define color of elements
    private final int statsRed		= Color.rgb(241, 52, 52);
    private final int statsGreen	= Color.rgb(4, 174, 4);
    private final int statsBlue		= Color.rgb(62, 136, 186);
    
    private final int gateWidth		= (int) (180*scaleW);
    private final int gateHeight	= (int) (102*scaleH);  
    private final int gatePosY		= (int) (230*scaleH);
    private final int gate1PosX		= (int) (150*scaleW);   
    private final int gate2PosX		= (int) (470*scaleW);
    
    private final int towerWidth	= (int) (120*scaleW);//picture size 216
    private final int towerHeight	= (int) (277*scaleW);//picture size 500
    private final int towerMinH		= (int) (100*scaleH); 
    private final float towerCoef	= (float) 2.15;
    private final int towerDPosY	= (int) (20*scaleH); 
    private final int tower1PosX	= (int) (180*scaleW);
    private final int tower2PosX	= (int) (500*scaleW);
    
    // ferda 
    private final int ferdaWidth	= (int) (50*scaleW);
    private final int ferdaHeight	= (int) (113*scaleH);  
    private final int ferdaPosY		= (int) (20*scaleH);
    private final int ferda1PosX	= (int) (150*scaleW);   
    private final int ferda2PosX	= (int) (600*scaleW);
    
 // ferda winner
    private final int winnerWidth	= (int) (100*scaleW);
    private final int winnerHeight	= (int) (98*scaleH);  
    private final int winnerPosY	= (int) (220*scaleH);
    private final int winner1PosX	= (int) (250*scaleW);   
    private final int winner2PosX	= (int) (470*scaleW);
    
    Bitmap tower1 = Bitmap.createScaledBitmap(bmp_tower1, towerWidth, towerHeight, true);
    Bitmap tower2 = Bitmap.createScaledBitmap(bmp_tower2, towerWidth, towerHeight, true);
    
    // GAME
    private Game game;
	
	public GraphicsView(Context context) {
		super(context);
		setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        
        initializePaint();        
        initializeCards();
        
        // Start game
        game = new Game(cards, createSlots(), createSlots());
	}

	@Override
    public void onDraw(Canvas canvas) {
        
        // draw background
		canvas.drawBitmap(bmp_background, 0, 0, paint);
		// draw towers
		drawTowers(canvas);
		// draw statistics
		drawStats(canvas);
		
		if(game.checkWin()){
  			drawWinner(canvas);
  		}else{
	  		drawCurrentPlayer(canvas);
  		}
		
		// draw slots with cards of current player
		game.getPlayerTurn().drawSlots(canvas);

    }
	
	/**
	 * Default initialization of paint
	 */
	private void initializePaint(){
		paint.setColor(Color.BLACK);
		paint.setTypeface(Typeface.MONOSPACE);
        paint.setAntiAlias(true);
	}
	
	/**
	 * Initialize of cards which will be used for game
	 */
	private void initializeCards(){
		cards.add(new CardKnight(this.getContext()));
		cards.add(new CardWall(this.getContext()));
		cards.add(new CardWall(this.getContext()));
		cards.add(new CardConjureCrystals(this.getContext()));
		cards.add(new CardTower(this.getContext()));
	}
	
	/**
	 * Initialize of cards which will be used for game
	 */
	private Slot[] createSlots(){
		Slot slots[] = new Slot[num_of_slots];
		for (int i = 0; i < num_of_slots; i++) {
			slots[i] = new Slot(i, this.getContext());
		}
		return slots; 
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {

        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();
        
       switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: 
            	if(!game.checkWin()){
			       	for (int i = 0; i < num_of_slots; i++) {
			          	if (game.getPlayerTurn().getSlot(i).getBounds().contains(point.x, point.y)) {
			          		
			          		if(game.playCard(i)){ // if card was available and played 
			          			if(!game.checkWin()){ // if player is not win game continue 
				          			game.nextRound();
				          		}else{				// else player wins
				          			game.playerWins();
				          		}
			          		}
			              	break;
			              }
			  		}
            	}else{
            		game.newGame();
            	}
                break;

            case MotionEvent.ACTION_MOVE:
                // finger moves on the screen
                break;

            case MotionEvent.ACTION_UP:   
            	Log.d(TAG, "UP"); 
                break;
        }
        
        if(event.getAction() == MotionEvent.ACTION_DOWN){
        	Log.d(TAG, "DOWN"); 
        }

        invalidate();
        return true;
    }
	
	/**
	 * Draw gates and towers of both players
	 * @param canvas
	 */
	public void drawTowers(Canvas canvas){
		// draw tower1
		int tower1Crop = (int) ((towerMinH - (game.player1.getCastle()))*towerCoef);
		int tower1posY = towerDPosY + tower1Crop;
		
		Bitmap curtower1 = Bitmap.createBitmap(tower1, 0, 0, tower1.getWidth(), tower1.getHeight()-tower1Crop);
		canvas.drawBitmap(curtower1, tower1PosX, tower1posY, paint);
		
		// draw towe2
		int tower2Crop = (int) ((towerMinH - (game.player2.getCastle()))*towerCoef);
		int tower2posY = towerDPosY + tower2Crop;
		
		Bitmap curtower2 = Bitmap.createBitmap(tower2, 0, 0, tower2.getWidth(), tower2.getHeight()-tower2Crop);
		canvas.drawBitmap(curtower2, tower2PosX, tower2posY, paint);
		
		// draw gate1
		canvas.drawBitmap(Bitmap.createScaledBitmap(bmp_gate, gateWidth, gateHeight, true), gate1PosX, gatePosY, paint);
		// draw gate2
		canvas.drawBitmap(Bitmap.createScaledBitmap(bmp_gate, gateWidth, gateHeight, true), gate2PosX, gatePosY, paint);
				
	}
	
	/**
	 * Draw statistics of both players
	 * @param canvas
	 */
	public void drawStats(Canvas canvas){		
		Rect red1 	= new Rect(statsPadH, statsPadV, statsPadH+statsW, statsPadV+statsH);
		Rect green1 = new Rect(statsPadH, statsPadV+statsH, statsPadH+statsW, statsPadV+statsH*2);
		Rect blue1 	= new Rect(statsPadH, statsPadV+statsH*2, statsPadH+statsW, statsPadV+statsH*3);
		Rect black1 = new Rect(statsPadH, statsPadV+statsH*3+statsPadB, statsPadH+statsW, statsPadV+statsH*4+statsPadB);
		
		Rect red2 	= new Rect(dWidth-statsPadH-statsW, statsPadV, dWidth-statsPadH, statsPadV+statsH);
		Rect green2 = new Rect(dWidth-statsPadH-statsW, statsPadV+statsH, dWidth-statsPadH, statsPadV+statsH*2);
		Rect blue2 	= new Rect(dWidth-statsPadH-statsW, statsPadV+statsH*2, dWidth-statsPadH, statsPadV+statsH*3);
		Rect black2 = new Rect(dWidth-statsPadH-statsW, statsPadV+statsH*3+statsPadB, dWidth-statsPadH, statsPadV+statsH*4+statsPadB);
		
		// draw boxes of statistics
		paint.setColor(statsRed);
		canvas.drawRect(red1, paint);
		canvas.drawRect(red2, paint);
		
		paint.setColor(statsGreen);
		canvas.drawRect(green1, paint);
		canvas.drawRect(green2, paint);
		
		paint.setColor(statsBlue);
		canvas.drawRect(blue1, paint);
		canvas.drawRect(blue2, paint);
		
		paint.setColor(Color.BLACK);
		canvas.drawRect(black1, paint);
		canvas.drawRect(black2, paint);
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(statsFontSize);
		
		// draw text into boxes player1
		canvas.drawText(this.getContext().getString(R.string.builders) + " " + String.valueOf(game.player1.getBuilder()), red1.left+statsFontPad, red1.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.bricks) + "     " + String.valueOf(game.player1.getBrick()), red1.left+statsFontPad, red1.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);
		
		canvas.drawText(this.getContext().getString(R.string.soldiers) + "    " + String.valueOf(game.player1.getSoldier()), green1.left+statsFontPad, green1.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.weapons) + "    " + String.valueOf(game.player1.getWeapon()), green1.left+statsFontPad, green1.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);

		canvas.drawText(this.getContext().getString(R.string.mages) + "    " + String.valueOf(game.player1.getMage()), blue1.left+statsFontPad, blue1.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.crystals) + "  " + String.valueOf(game.player1.getCrystal()), blue1.left+statsFontPad, blue1.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);

		canvas.drawText(this.getContext().getString(R.string.castle) + "      " + String.valueOf(game.player1.getCastle()), black1.left+statsFontPad, black1.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.wall) + "    " + String.valueOf(game.player1.getWall()), black1.left+statsFontPad, black1.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);
		
		// draw text into boxes player2
		canvas.drawText(this.getContext().getString(R.string.builders) + " " + String.valueOf(game.player2.getBuilder()), red2.left+statsFontPad, red2.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.bricks) + "     " + String.valueOf(game.player2.getBrick()), red2.left+statsFontPad, red2.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);
		
		canvas.drawText(this.getContext().getString(R.string.soldiers) + "    " + String.valueOf(game.player2.getSoldier()), green2.left+statsFontPad, green2.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.weapons) + "    " + String.valueOf(game.player2.getWeapon()), green2.left+statsFontPad, green2.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);

		canvas.drawText(this.getContext().getString(R.string.mages) + "    " + String.valueOf(game.player2.getMage()), blue2.left+statsFontPad, blue2.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.crystals) + "  " + String.valueOf(game.player2.getCrystal()), blue2.left+statsFontPad, blue2.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);

		canvas.drawText(this.getContext().getString(R.string.castle) + "      " + String.valueOf(game.player2.getCastle()), black2.left+statsFontPad, black2.top+statsFontSize+statsFontPad, paint);
		canvas.drawText(this.getContext().getString(R.string.wall) + "    " + String.valueOf(game.player2.getWall()), black2.left+statsFontPad, black2.top+(statsFontSize+statsFontPad)*2+statsFontPad, paint);
		
		paint.setColor(Color.BLACK);
		paint.setTextSize(scoreFontSize);
		// draw score of player1
		canvas.drawText(String.valueOf(game.player1.getScore()), (int)((red1.right-red1.left)/2)+red1.left, red1.top-5, paint);
		// draw score of player2
		canvas.drawText(String.valueOf(game.player2.getScore()), (int)((red2.right-red2.left)/2)+red2.left, red2.top-5, paint);
	}

	/**
	 * Draw illustration of player's turn
	 * @param canvas
	 */
	public void drawCurrentPlayer(Canvas canvas){
		if(game.getPlayerTurn().getId() == 1){
			canvas.drawBitmap(Bitmap.createScaledBitmap(bmp_ferda1, ferdaWidth, ferdaHeight, true), ferda1PosX, ferdaPosY, paint);
		}else{
			canvas.drawBitmap(Bitmap.createScaledBitmap(bmp_ferda2, ferdaWidth, ferdaHeight, true), ferda2PosX, ferdaPosY, paint);
		}
	}
	
	/**
	 * Draw illustration of winner.
	 * @param canvas
	 */
	public void drawWinner(Canvas canvas){
		if(game.getPlayerTurn().getId() == 1){
			canvas.drawBitmap(Bitmap.createScaledBitmap(bmp_winner1, winnerWidth, winnerHeight, true), winner1PosX, winnerPosY, paint);
		}else{
			canvas.drawBitmap(Bitmap.createScaledBitmap(bmp_winner2, winnerWidth, winnerHeight, true), winner2PosX, winnerPosY, paint);
		}
	}
}