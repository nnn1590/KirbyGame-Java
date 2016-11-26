package Kirby;

import jig.Collision;
import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;

/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 * Transitions From StartUpState
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	int lives;
	float xOffset;
	float yOffset;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		lives = 3;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		KirbyGame bg = (KirbyGame)game;
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		KirbyGame bg = (KirbyGame)game;
		
		float kXOffset = 0;
		float kYOffset = 0;
		
		float maxXOffset = (bg.map.getWidth() * 32) - (bg.SCREEN_WIDTH / 2.f);
		float maxYOffset = (bg.map.getHeight() * 32) - (bg.SCREEN_HEIGHT / 2.f);
 
		if (bg.kirby.getX() > maxXOffset)
			kXOffset = maxXOffset - (bg.SCREEN_WIDTH / 2.f);
		else if (bg.kirby.getX() <= bg.SCREEN_WIDTH / 2.f)
			kXOffset = bg.kirby.getX() - (bg.SCREEN_WIDTH / 2.f);
		
		if (bg.kirby.getY() > maxYOffset)
			kYOffset = maxYOffset - (bg.SCREEN_HEIGHT / 2.f);
		else if (bg.kirby.getY() <= bg.SCREEN_HEIGHT / 2.f)
			kYOffset = bg.kirby.getY() - (bg.SCREEN_HEIGHT / 2.f);
		
		bg.map.render((int)(-1 * (xOffset % 32)), (int)(-1 * (yOffset % 32)), 
				(int)(xOffset / 32), (int)(yOffset / 32), 33, 19);
		
		bg.kirby.render(g);
		
		g.drawString("Lives: " + lives, 10, 50);
		g.drawString("Level: " + bg.level, 10, 30);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		Input input = container.getInput();
		KirbyGame bg = (KirbyGame)game;
		
		xOffset = bg.kirby.getX() - bg.SCREEN_WIDTH / 2.f;
		yOffset = bg.kirby.getY() - bg.SCREEN_HEIGHT / 2.f;
		
		// kirby collision with cubs
		Vector move = null;
		for (Underbrush u : bg.underbrushes) {
			Collision c = bg.kirby.collides(u);
			if (bg.kirby.collides(u) != null) {
				move = c.getMinPenetration();
				break;
			}
		}
		
		/*Collision kirbyNest = bg.kirby.collides(bg.nest);
		if (kirbyNest != null) {
			if (bg.kirby.holdingCub()) {
				bg.cubs.remove(bg.kirby.getRescueCub());
				bg.kirby.setRescueCub(null);
			}
			move = kirbyNest.getMinPenetration();
		}*/
		
		keyPresses(input, bg, delta, move);
		
		for (Cub c : bg.cubs) {
			c.setMoving(bg);
			c.update(delta);
		}
		
		// kirby collision with cubs
		/*if (!bg.kirby.holdingCub()) {
			for (Cub c : bg.cubs) {
				if (bg.kirby.collides(c) != null) {
					bg.kirby.setRescueCub(c);
					break;
				}
			}
		}
		
		// poacher collision with kirby or cubs
		Collision poacherkirby = bg.kirby.collides(bg.poacher);
		Collision poacherCub = null;
		for (Cub c : bg.cubs) {
			Collision coll = bg.poacher.collides(c);
			if (coll != null && !c.isHeld()) {
				bg.kirby.setRescueCub(null);
				c.removeImage(ResourceManager.getImage(c.getCurImage()));
				bg.cubs.remove(c);
				poacherCub = coll;
				lives -= 1;
				break;
			}
		}
		
		if (poacherkirby != null) {
			lives -= 1;
			bg.kirby.setPosition(bg.SCREEN_WIDTH - 50, bg.SCREEN_HEIGHT - 50);
			bg.kirby.setvPos(bg.SCREEN_WIDTH - 50, bg.SCREEN_HEIGHT - 50);
			bg.poacher.setPosition(50, 50);
			bg.poacher.setReset(bg);
		}*/
		
		bg.kirby.update(delta);
		bg.kirby.setVertex(bg);
		//bg.poacher.setMoving(bg);
		bg.poacher.update(delta);
		
		//ResourceManager.getSound(BounceGame.HITPADDLE_RSC).play();
		
		// Change levels
		/*if (bg.cubs.size() == 0) {
			bg.level++;
			if (bg.level == 4) {
				game.enterState(kirbyGame.GAMEOVERSTATE, new EmptyTransition(), new HorizontalSplitTransition());
			} else {
				game.enterState(kirbyGame.STARTUPSTATE, new EmptyTransition(), new HorizontalSplitTransition());
			}
		}*/

		checkLives(game, bg);
		
	}
	
	private void keyPresses(Input input, KirbyGame bg, int delta, Vector move) {		
		// Control user input
		if (input.isKeyDown(Input.KEY_LEFT) && (move == null || move.getX() <= 0)) 
			bg.kirby.setVelocity(new Vector(-.3f, 0));
		else if (input.isKeyDown(Input.KEY_RIGHT) && (move == null || move.getX() >= 0)) 
			bg.kirby.setVelocity(new Vector(.3f, 0f));
		/*else if (input.isKeyDown(Input.KEY_UP) && (move == null || move.getY() <= 0)) 
			bg.kirby.setVelocity(new Vector(0f, -.3f));
		else if (input.isKeyDown(Input.KEY_DOWN) && (move == null || move.getY() >= 0)) 
			bg.kirby.setVelocity(new Vector(0f, .3f));*/
		else 
			bg.kirby.setVelocity(new Vector(0f, 0f));
		
		// if space pressed, kirby drops cub
		/*if (input.isKeyDown(Input.KEY_SPACE) && bg.kirby.holdingCub()) 
			bg.kirby.dropCub();*/
		
	}
	
	private void checkLives(StateBasedGame game, KirbyGame bg) {
		// Game over state if no lives left
		if (lives <= 0) {
			//((GameOverState)game.getState(kirbyGame.GAMEOVERSTATE)).setUserScore(bounces);
			bg.level = 1;
			lives = 3;
			game.enterState(KirbyGame.GAMEOVERSTATE);
		}
	}

	@Override
	public int getID() {
		return KirbyGame.PLAYINGSTATE;
	}
	
}