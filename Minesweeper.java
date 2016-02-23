import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
 * @author Anwar Saleeby
 * @collaborators 
 * 
 * 
 * 	@description Minesweeper.java is a clone of the classic MineSweeper game from the early microsoft years.
 * 				 Descriptions of individual methods and algorithms will be placed above the method/instance names,
 */
public class Minesweeper extends Application implements Serializable
{

	// Integers for our Tile size and the width and height of the StackPane
	private static final int TILE_SIZE = 40;
	private static final int W = 800;
	private static final int H = 600;
	// Integers for our clickable tile panes.
	private static final int X_TILES = W / TILE_SIZE;
	private static final int Y_TILES = H / TILE_SIZE;
	// 2D array of tiles that will form the grid on the gui.

	private Tile[][] grid = new Tile[X_TILES][Y_TILES];
	private Scene scene;
	// Score integer for keeping score.
	private int score = 0;
	// Score text that will update when tile is clicked.
	private Text scoreText;
	// The Stage that contains our score
	protected Stage score_stage = null;
	protected static Stage mouse_clickStage = null;

	// start method for our gui
	@Override
	public void start(Stage primaryStage) throws Exception
	{

		this.score_stage = primaryStage;
		BorderPane shell = new BorderPane();
		shell.setCenter(createContent());
		HBox scorePane = new HBox();
		Label score = new Label("Score: ");
		scoreText = new Text("0");
		scorePane.getChildren().addAll(score, scoreText);
		shell.setTop(scorePane);
		scene = new Scene(shell);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper Clone");
		primaryStage.show();

		mouse_clickStage = new Stage();

	}

	// Create content method that adds our bombs to the scene
	private Parent createContent()
	{
		Pane initial = new Pane();
		initial.setPrefSize(W, H);
		// Nested for loop for the generation of the grid for x and y
		// coordinates
		// as well as for generating the probability that a bomb will be next to
		// a tile.
		for (int y = 0; y < Y_TILES; y++)
		{
			for (int x = 0; x < X_TILES; x++)
			{
				Tile tile = new Tile(x, y, Math.random() < 0.2);

				grid[x][y] = tile;
				initial.getChildren().add(tile);

			}
		}
		// nested for loop for our actual game logic including a continue
		// condition.,
		// a long algorithm that gets the number of bombs and counts them for
		// bomb generation
		for (int y = 0; y < Y_TILES; y++)
		{
			for (int x = 0; x < X_TILES; x++)
			{
				Tile tile = grid[x][y];

				if (tile.hasBomb)
					continue;

				// algorithm
				long bombs = getBombs(tile).stream().filter(t -> t.hasBomb).count();

				if (bombs > 0)
				{
					tile.text.setText(String.valueOf(bombs));
					tile.setNeighborBombs(bombs);
				}

			}
		}

		return initial;
	}

	// A list of tiles that contains our algorithms for bomb placement when
	// randomly generated
	// the points are array of ints contains the logic for the bomb placement.
	private List<Tile> getBombs(Tile tile)
	{
		List<Tile> potentialBombs = new ArrayList<>();
		
		/*
		 * To Visualize how this array works:
		 * X's are bombs
		 * T's are other tiles
		 * TTT
		 * TXT
		 * TTT
		 */
		int[] points = new int[]
		{ -1, -1, -1, 0, -1, 1, 0, -1, 0, 1, 1, -1, 1, 0, 1, 1 };

		for (int i = 0; i < points.length; i++)
		{
			int dx = points[i];
			int dy = points[++i];

			int newX = tile.x + dx;
			int newY = tile.y + dy;

			// this if statement prevents our arraylist from initialzing at -1,
			// which would cause an error.
			if (newX >= 0 && newX < X_TILES && newY >= 0 && newY < Y_TILES)
			{
				potentialBombs.add(grid[newX][newY]);

			}
		}

		return potentialBombs;
	}

	// Tile class that contains all of our properties for each tile.
	private class Tile extends StackPane
	{
		private int x, y;
		private boolean hasBomb;
		private boolean isOpen = false;

		// Creates borders for each tile, each one is minus -2 to fit in the
		// window.

		private Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
		private Text text = new Text();
		private int neighborBombs;

		// Tile constructor.
		public Tile(int x, int y, boolean hasBomb)
		{
			this.x = x;
			this.y = y;
			this.hasBomb = hasBomb;

			border.setStroke(Color.FIREBRICK);
			
			
			text.setFont(Font.font(18));
			text.setText(hasBomb ? "X" : "");
			text.setVisible(false);

			getChildren().addAll(border, text);

			setTranslateX(x * TILE_SIZE);
			setTranslateY(y * TILE_SIZE);

			// Lambda to actually open the tile.
			setOnMouseClicked(e -> open());
		}

		// method to set the number of bombs from the long numBombs that is used
		// to actually count the bombs
		// We cast to int for score keeping
		public void setNeighborBombs(long numBombs)
		{
			neighborBombs = (int) numBombs;

		}

		// Open method for clicking each tile.
		public void open()
		{
			if (isOpen)
				return;

			if (hasBomb)
			{
				System.out.println("Game Over. " + "Your Score is: " + score);
				scene.setRoot(createContent());
				try
				{
					start(score_stage);
					score = 0;
				} catch (Exception e)
				{
					e.printStackTrace();
				}

				return;
			}

			score = score + neighborBombs * 100;
			scoreText.setText(score + "");
			isOpen = true;
			text.setVisible(true);
			border.setFill(null);

			if (text.getText().isEmpty())
			{
				getBombs(this).forEach(Tile::open);
			}
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}

}