package snake;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Snake {

	private final Color headColor;
	private final Color tailColor;

	private final ArrayList<Coord> body = new ArrayList<>();

	private Direction direction;

	public Snake(int startPositionX, int startPositionY, Color headColor, Color tailColor, Direction direction) {
		Random random = new Random();
		int x = random.nextInt(startPositionX);
		int y = random.nextInt(startPositionY);

		body.add(new Coord(x, y));
		body.add(new Coord(x, y));
		body.add(new Coord(x, y));

		this.headColor = headColor;
		this.tailColor = tailColor;

		this.direction = direction;
	}

	public int getSize() {
		return body.size();
	}

	public Coord getElement(int i) {
		return body.get(i);
	}

	public void addElement(Coord newElement) {
		body.add(newElement);
	}

	public Color getHeadColor() {
		return headColor;
	}

	public Color getTailColor() {
		return tailColor;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(final Direction direction) {
		this.direction = direction;
	}
}
