package snake;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static snake.GameFieldAreaValue.BODY;
import static snake.GameFieldAreaValue.EMPTY;
import static snake.GameFieldAreaValue.FOOD;
import static snake.GameFieldAreaValue.HEAD;
import static snake.GameFieldAreaValue.OBSTACLE;

public class GameField {

	private final int width;
	private final int height;

	private final int[][] area;
	private final JPanel[][] panelsArea;

	private final Color foodColor;
	private final Color obstacleColor;
	private final Color areaColor;
	private final Color borderColor;

	private final Random random = new Random();

	public GameField(final int width, final int height,
	                 final Color foodColor, final Color obstacleColor,
	                 final Color areaColor, final Color borderColor) {
		this.width = width;
		this.height = height;
		this.foodColor = foodColor;
		this.obstacleColor = obstacleColor;
		this.areaColor = areaColor;
		this.borderColor = borderColor;

		area = new int[width][height];
		panelsArea = new JPanel[width][height];
	}

	public void setHead(final int x, final int y) {
		setAreaCellValue(x, y, HEAD);
	}

	public void setBody(final int x, final int y) {
		setAreaCellValue(x, y, BODY);
	}

	public void showEmptyCell(final int x, final int y) {
		setEmpty(x, y);
		setPanelsAreaColor(x, y, areaColor);
	}

	public void setEmpty(final int x, final int y) {
		setAreaCellValue(x, y, EMPTY);
	}

	private void setPanelsAreaColor(final int x, final int y, final Color color) {
		panelsArea[y][x].setBackground(color);
	}

	private void setAreaCellValue(final int x, final int y, final GameFieldAreaValue value) {
		area[y][x] = value.getValue();
	}

	public boolean isFood(final int x, final int y) {
		return getAreaCellValue(x, y) == FOOD.getValue();
	}

	public boolean isObstacle(final int x, final int y) {
		return getAreaCellValue(x, y) == OBSTACLE.getValue();
	}

	public boolean isBody(final int x, final int y) {
		return getAreaCellValue(x, y) == BODY.getValue();
	}

	private int getAreaCellValue(final int x, final int y) {
		return area[y][x];
	}

	public void showFood() {
		int x = random.nextInt(width - 1);
		int y = random.nextInt(height - 1);
		area[y][x] = FOOD.getValue();
		setPanelsAreaColor(x, y, foodColor);
	}

	public void showObstacle() {
		int x = random.nextInt(width - 1);
		int y = random.nextInt(height - 1);
		area[y][x] = OBSTACLE.getValue();
		setPanelsAreaColor(x, y, obstacleColor);
	}

	public void showSnake(final Snake snake) {
		Coord c;
		for (int i = 0; i < snake.getSize(); i++) {
			c = snake.getElement(i);
			if (i == 0) {
				setPanelsAreaColor(c.getX(), c.getY(), snake.getHeadColor());
				setHead(c.getX(), c.getY());
			} else {
				setPanelsAreaColor(c.getX(), c.getY(), snake.getTailColor());
				setBody(c.getX(), c.getY());
			}
		}
	}

	public void initializeGameField(final JFrame frame) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				frame.add(initAndGetJPanel(i, j));
				clearAreaCell(i, j);
			}
		}
	}

	private JPanel initAndGetJPanel(final int i, final int j) {
		panelsArea[i][j] = new JPanel();
		panelsArea[i][j].setBackground(areaColor);
		panelsArea[i][j].setBorder(BorderFactory.createLineBorder(borderColor));
		return panelsArea[i][j];
	}

	private void clearAreaCell(final int i, final int j) {
		area[i][j] = EMPTY.getValue();
	}

	public void removeObstacles() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (isObstacle(i, j)) {
					clearAreaCell(i, j);
					setPanelsAreaColor(i, j, areaColor);
				}
			}
		}
	}

}
