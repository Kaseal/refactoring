package snake;

public enum GameFieldAreaValue {
	EMPTY(0), BODY(1), HEAD(2), FOOD(3), OBSTACLE(4);

	private final int value;

	GameFieldAreaValue(final int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
