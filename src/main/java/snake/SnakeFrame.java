package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static snake.Direction.DOWN;
import static snake.Direction.LEFT;
import static snake.Direction.RIGHT;
import static snake.Direction.UP;


public class SnakeFrame extends JFrame implements KeyListener, Runnable, ActionListener
{
	private int aX, aY, frameSnakeHeight, frameSnakeWidth, sleep, incr, level, lup, frameSnakeX, frameSnakeY;

	private GameField gameField;
	private Snake snake;

	private Thread t;
	private boolean end, pause, isObstacle;
	private MenuItem exit;


	void loadProperties() {
		Properties properties = new Properties();
		Path path = Paths.get("");
		String propertyFilePath = path.toAbsolutePath().toString() + "/src/main/resources/config.properties";
		try {
			properties.load(new FileInputStream(propertyFilePath));
		} catch (IOException exception) {
			System.out.println("File not found " +  propertyFilePath);
		}
		frameSnakeHeight = getIntProperty(properties,"snake.frame.height");
		frameSnakeWidth = getIntProperty(properties,"snake.frame.width");
		frameSnakeX = getIntProperty(properties,"snake.frame.position.x");
		frameSnakeY = getIntProperty(properties,"snake.frame.position.y");
		aX = getIntProperty(properties,"snake.frame.width.cells.count");
		aY = getIntProperty(properties,"snake.frame.height.cells.count");
		sleep = getIntProperty(properties,"snake.frame.thread.sleep");
	}

	int getIntProperty(Properties properties, String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

	//конструктор главного окна
	SnakeFrame()
	{
		loadProperties();

//		frameSnakeHeight = 340;//высота окна
//		frameSnakeWidth = 340;//ширина окна
//		frameSnakeX = 300;//расположение окна по X
//		frameSnakeY = 200;//расположение окна по Y
//		aX = 30;//размер поля по X
//		aY = 30;//размер поля по Y
		gameField = new GameField(aX, aY, GREEN, DARK_GRAY, new Color(200, 200, 0), BLACK);

//		sleep = 500;//начальная скорость хода змейки
		isObstacle = true;//true - будут появляться препятствия
		pause = false;//true - игра будет приостановлена
		end = false;//true - игра окончена

		level = 1;//вспомогательная переменная
		lup = 0;//вспомогательная переменная(+10 еды LevelUP)
		incr = 0;//вспомогательная переменная(+60 тиков Sleep-50(увеличение скорости))

		snake = new Snake(aX, aY, BLUE, RED, UP);

		setTitle("Snake");//заголовок главного окна
		setSize(frameSnakeWidth, frameSnakeHeight);//установка размеров окна
		setLocation(frameSnakeX, frameSnakeY);//установка расположения окна
		setLayout(new GridLayout(aX, aY));//менеджер сеточной компановки

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MenuBar mbar = new MenuBar();//создание панели для меню
		setMenuBar(mbar);//установка меню для главного окна

		Menu file = new Menu("File");//создание меню
		file.add(exit = new MenuItem("Exit"));//создание подменю
		mbar.add(file);//добавление меню File к панели меню

		exit.addActionListener(this);//прослушивание меню
		addKeyListener(this);//прослушивание нажатий клавиш

		JOptionPane.showMessageDialog(this, "Press OK to start...",
				"Snake", JOptionPane.WARNING_MESSAGE);

		play();//начало игры
	}//frameSnake()

	//запуск игры
	void play()
	{
		end = false;

		gameField.initializeGameField(this);
		gameField.showSnake(snake);
		gameField.showFood();//прорисовка еды

		t = new Thread(this, "MyTimer");//создание таймера
		t.start();//запуск таймера(начало игры)
	}//play()

	//двмжение змейки
	boolean snakeMove(Direction direction)
	{
		Coord c, c_buf, c_buf2;
		c = snake.getElement(0);
		c_buf2 = snake.getElement(1);
		boolean pd = false, isGrow = false;

		if(direction.equals(UP))
		{
			if(c.getY() - 1 >= 0)
			{
				if(!gameField.isBody(c.getX(),c.getY() - 1)) {
					pd = true;
				}
			}
			else
				pd = true;
			if(pd)
			{
				c_buf2 = new Coord(c.getX(), c.getY());
				if(c.getY() - 1 < 0)
					c.setY(aY - 1);
				else
					c.setY(c.getY() - 1);
				if(gameField.isFood(c.getX(), c.getY())) {
					isGrow = true;
				}
				if(gameField.isObstacle(c.getX(), c.getY())) {
					return false;
				}
				gameField.setHead(c.getX(), c.getY());
				for(int i = 1; i < snake.getSize(); i++)
				{
					c = snake.getElement(i);
					c_buf = new Coord(c.getX(), c.getY());
					c.setX(c_buf2.getX());
					c.setY(c_buf2.getY());
					gameField.setBody(c.getX(), c.getY());
					c_buf2.setX(c_buf.getX());
					c_buf2.setY(c_buf.getY());
				}
			}
		}//if(direction == "UP")

		if(direction.equals(DOWN))
		{
			if(c.getY() + 1 <= aY - 1)
			{
				if(!gameField.isBody(c.getX(), c.getY() + 1)) {
					pd = true;
				}
			}
			else
				pd = true;
			if(pd)
			{
				c_buf2 = new Coord(c.getX(), c.getY());
				if(c.getY() + 1 > aY - 1)
					c.setY(0);
				else
					c.setY(c.getY() + 1);
				if(gameField.isFood(c.getX(), c.getY())) {
					isGrow = true;
				}
				if(gameField.isObstacle(c.getX(), c.getY())) {
					return false;
				}
				gameField.setHead(c.getX(), c.getY());
				for(int i = 1; i < snake.getSize(); i++)
				{
					c = snake.getElement(i);
					c_buf = new Coord(c.getX(), c.getY());
					c.setX(c_buf2.getX());
					c.setY(c_buf2.getY());
					gameField.setBody(c.getX(), c.getY());
					c_buf2.setX(c_buf.getX());
					c_buf2.setY(c_buf.getY());
				}
			}
		}//if(direction == "DOWN")

		if(direction.equals(LEFT))
		{
			if(c.getX() - 1 >= 0)
			{
				if(!gameField.isBody(c.getX() - 1, c.getY())) {
					pd = true;
				}
			}
			else
				pd = true;
			if(pd == true)
			{
				c_buf2 = new Coord(c.getX(), c.getY());
				if(c.getX() - 1 < 0)
					c.setX(aX - 1);
				else
					c.setX(c.getX() - 1);
				if(gameField.isFood(c.getX(), c.getY())) {
					isGrow = true;
				}
				if(gameField.isObstacle(c.getX(), c.getY())) {
					return false;
				}
				gameField.setHead(c.getX(), c.getY());
				for(int i = 1; i < snake.getSize(); i++)
				{
					c = snake.getElement(i);
					c_buf = new Coord(c.getX(), c.getY());
					c.setX(c_buf2.getX());
					c.setY(c_buf2.getY());
					gameField.setBody(c.getX(), c.getY());
					c_buf2.setX(c_buf.getX());
					c_buf2.setY(c_buf.getY());
				}
			}
		}//if(direction == "LEFT")

		if(direction.equals(RIGHT))
		{
			if(c.getX() + 1 <= aX - 1)
			{
				if(!gameField.isBody(c.getX() + 1, c.getY())) {
					pd = true;
				}
			}
			else
				pd = true;
			if(pd == true)
			{
				c_buf2 = new Coord(c.getX(), c.getY());
				if(c.getX() + 1 > aX - 1)
					c.setX(0);
				else
					c.setX(c.getX() + 1);
				if(gameField.isFood(c.getX(), c.getY())) {
					isGrow = true;
				}
				if(gameField.isObstacle(c.getX(), c.getY())) {
					return false;
				}
				gameField.setHead(c.getX(), c.getY());
				for(int i = 1; i < snake.getSize(); i++)
				{
					c = snake.getElement(i);
					c_buf = new Coord(c.getX(), c.getY());
					c.setX(c_buf2.getX());
					c.setY(c_buf2.getY());
					gameField.setBody(c.getX(), c.getY());
					c_buf2.setX(c_buf.getX());
					c_buf2.setY(c_buf.getY());
				}
			}
		}//if(direction == "RIGHT")

		if(pd == true)
		{
			if(isGrow == false)
			{
				gameField.showEmptyCell(c_buf2.getX(), c_buf2.getY());
			}
			else
			{
				snake.addElement(new Coord(c_buf2.getX(), c_buf2.getY()));
				gameField.setBody(c_buf2.getX(), c_buf2.getY());
				incr++;
				lup++;
				gameField.showFood();
			}
		}

		gameField.showSnake(snake);
		return pd;
	}//snakeMove()

	//цикл игры
	public void run()
	{
		try
		{
			for(;;)
			{
				if(pause == false)
				{
					if(incr >= 60)
					{
						if(sleep > 100)
							sleep = sleep - 50;
						if(isObstacle == true)
							if(level > 1)
								gameField.showObstacle();
						incr = 0;
					}
					Thread.sleep(sleep);
					incr++;
					if(lup == 10)
					{
						level++;
						lup = 0;
					}
					if(incr == 30)
						gameField.showFood();

					if(!snakeMove(snake.getDirection()) || end)
					{
						JOptionPane.showMessageDialog(this, "Game Over!!! Your level is " + level +
										". Snake Length " + snake.getSize(),
								"Snake", JOptionPane.WARNING_MESSAGE);
						System.exit(0);
						t.stop();
					}
				}//if(pause == false)
			}//for
		}
		catch(InterruptedException e) {/*error message*/};
	}//run()

	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == exit)
			System.exit(0);
	}

	public void keyPressed(KeyEvent ke) {
		if (!pause) {
			if (ke.getKeyCode() == KeyEvent.VK_UP && !snake.getDirection().equals(DOWN)) {
				snake.setDirection(UP);
				if (!snakeMove(snake.getDirection())) {
					end = true;
				}
			}
			if (ke.getKeyCode() == KeyEvent.VK_DOWN && !snake.getDirection().equals(UP)) {
				snake.setDirection(DOWN);
				if (!snakeMove(snake.getDirection())) {
					end = true;
				}
			}
			if (ke.getKeyCode() == KeyEvent.VK_LEFT && !snake.getDirection().equals(RIGHT)) {
				snake.setDirection(LEFT);
				if (!snakeMove(snake.getDirection())) {
					end = true;
				}
			}
			if (ke.getKeyCode() == KeyEvent.VK_RIGHT && !snake.getDirection().equals(LEFT)) {
				snake.setDirection(RIGHT);
				if (!snakeMove(snake.getDirection())) {
					end = true;
				}
			}
		}

		if (ke.getKeyCode() == KeyEvent.VK_PAUSE) {
			pause = !pause;
		}

		if (ke.getKeyCode() == KeyEvent.VK_O) {
			if (isObstacle) {
				isObstacle = false;
				gameField.removeObstacles();
			} else {
				isObstacle = true;
			}
		}
	}

	//освобождение клавиши
	public void keyReleased(KeyEvent ke) {}//keyReleased
	//генерирование символа клавишей
	public void keyTyped(KeyEvent ke) {}//keyTyped

}//class frameSnake
