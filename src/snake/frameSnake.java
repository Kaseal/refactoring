package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


public class frameSnake extends JFrame implements KeyListener, Runnable, ActionListener
{
  private int aX, aY, _area[][], frameSnakeHeight, frameSnakeWidth, sleep, incr,
          snakeLength, level, lup, frameSnakeX, frameSnakeY;
  private JPanel area[][];
  private String direction;
  private Vector snake;
  private Color snakeHead, snakeTail, fieldArea, fieldBorder, food, obstacle;
  private Thread t;
  private boolean end, pause, isObstacle;
  private MenuItem exit;

  //конструктор главного окна
  frameSnake()
  {
    frameSnakeHeight = 340;//высота окна
    frameSnakeWidth = 340;//ширина окна
    frameSnakeX = 300;//расположение окна по X
    frameSnakeY = 200;//расположение окна по Y
    aX = 30;//размер поля по X
    aY = 30;//размер поля по Y
    area = new JPanel[aX][aY];//массив панелей
    _area = new int[aX][aY];//массив с данными
    direction = "UP";//направление хода змейки
    snake = new Vector(3, 1);//динамическое представление змейки
    snakeHead = Color.blue;//цвет головы змейки
    snakeTail = Color.red;//цвет хвоста змейки
    fieldArea = new Color(200, 200, 0);//цвет пустого поля
    fieldBorder = Color.black;//цвет границы панели
    obstacle = Color.darkGray;//цвет препятствия
    food = Color.green;//цвет еды
    sleep = 500;//начальная скорость хода змейки
    isObstacle = true;//true - будут появляться препятствия
    pause = false;//true - игра будет приостановлена
    end = false;//true - игра окончена

    level = 1;//вспомогательная переменная
    lup = 0;//вспомогательная переменная(+10 еды LevelUP)
    incr = 0;//вспомогательная переменная(+60 тиков Sleep-50(увеличение скорости))
    snakeLength = snake.size();//вспомогательная переменная(размер змейки)

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
    int i = new Random().nextInt(aX);//начальное расположение змейки
    int j = new Random().nextInt(aY);//...
    snake.addElement(new coord(i, j));//занесение координат начального расположения
    snake.addElement(new coord(i, j));//в динамическое представление змейки
    snake.addElement(new coord(i, j));//...
    areaShow();//прорисовка поля
    snakeShow();//прорисовка змейки
    snakeInit();//инициализация змейки в массиве _area[][]
    foodShow();//прорисовка еды
    t = new Thread(this, "MyTimer");//создание таймера
    t.start();//запуск таймера(начало игры)
  }//play()

  //прорисовка еды
  void foodShow()
  {
    int i = new Random().nextInt(aX - 1);
    int j = new Random().nextInt(aY - 1);
    _area[j][i] = 3;
    area[j][i].setBackground(food);
  }//foodShow();

  //прописовка препятствия
  void obstacleShow()
  {
    int i = new Random().nextInt(aX - 1);
    int j = new Random().nextInt(aY - 1);
    _area[j][i] = 4;
    area[j][i].setBackground(obstacle);
  }//foodShow();

  //инициализация змейки в массиве _array[][]
  void snakeInit()
  {
    coord c;
    for(int i = 0; i < snake.size(); i++)//обход вектора змейки
    {
      c = (coord)snake.elementAt(i);
      if(i == 0)
        _area[c.get_y()][c.get_x()] = 2;
      else
        _area[c.get_y()][c.get_x()] = 1;
    }
  }//snakeInit()

  //двмжение змейки
  boolean snakeMove(String direction)
  {
    coord c, c_buf, c_buf2;
    c = (coord)snake.elementAt(0);
    c_buf2 = (coord)snake.elementAt(1);
    boolean pd = false, isGrow = false;

    if(direction == "UP")
    {
      if(c.get_y() - 1 >= 0)
      {
        if(_area[c.get_y() - 1][c.get_x()] != 1)
          pd = true;
      }
      else
        pd = true;
      if(pd == true)
      {
        c_buf2 = new coord(c.get_x(), c.get_y());
        if(c.get_y() - 1 < 0)
          c.set_y(aY - 1);
        else
          c.set_y(c.get_y() - 1);
        if(_area[c.get_y()][c.get_x()] == 3)
          isGrow = true;
        if(_area[c.get_y()][c.get_x()] == 4)
          return false;
        _area[c.get_y()][c.get_x()] = 2;
        for(int i = 1; i < snake.size(); i++)
        {
          c = (coord)snake.elementAt(i);
          c_buf = new coord(c.get_x(), c.get_y());
          c.set_x(c_buf2.get_x());
          c.set_y(c_buf2.get_y());
          _area[c.get_y()][c.get_x()] = 1;
          c_buf2.set_x(c_buf.get_x());
          c_buf2.set_y(c_buf.get_y());
        }
      }
    }//if(direction == "UP")

    if(direction == "DOWN")
    {
      if(c.get_y() + 1 <= aY - 1)
      {
        if(_area[c.get_y() + 1][c.get_x()] != 1)
          pd = true;
      }
      else
        pd = true;
      if(pd == true)
      {
        c_buf2 = new coord(c.get_x(), c.get_y());
        if(c.get_y() + 1 > aY - 1)
          c.set_y(0);
        else
          c.set_y(c.get_y() + 1);
        if(_area[c.get_y()][c.get_x()] == 3)
          isGrow = true;
        if(_area[c.get_y()][c.get_x()] == 4)
          return false;
        _area[c.get_y()][c.get_x()] = 2;
        for(int i = 1; i < snake.size(); i++)
        {
          c = (coord)snake.elementAt(i);
          c_buf = new coord(c.get_x(), c.get_y());
          c.set_x(c_buf2.get_x());
          c.set_y(c_buf2.get_y());
          _area[c.get_y()][c.get_x()] = 1;
          c_buf2.set_x(c_buf.get_x());
          c_buf2.set_y(c_buf.get_y());
        }
      }
    }//if(direction == "DOWN")

    if(direction == "LEFT")
    {
      if(c.get_x() - 1 >= 0)
      {
        if(_area[c.get_y()][c.get_x() - 1] != 1)
          pd = true;
      }
      else
        pd = true;
      if(pd == true)
      {
        c_buf2 = new coord(c.get_x(), c.get_y());
        if(c.get_x() - 1 < 0)
          c.set_x(aX - 1);
        else
          c.set_x(c.get_x() - 1);
        if(_area[c.get_y()][c.get_x()] == 3)
          isGrow = true;
        if(_area[c.get_y()][c.get_x()] == 4)
          return false;
        _area[c.get_y()][c.get_x()] = 2;
        for(int i = 1; i < snake.size(); i++)
        {
          c = (coord)snake.elementAt(i);
          c_buf = new coord(c.get_x(), c.get_y());
          c.set_x(c_buf2.get_x());
          c.set_y(c_buf2.get_y());
          _area[c.get_y()][c.get_x()] = 1;
          c_buf2.set_x(c_buf.get_x());
          c_buf2.set_y(c_buf.get_y());
        }
      }
    }//if(direction == "LEFT")

    if(direction == "RIGHT")
    {
      if(c.get_x() + 1 <= aX - 1)
      {
        if(_area[c.get_y()][c.get_x() + 1] != 1)
          pd = true;
      }
      else
        pd = true;
      if(pd == true)
      {
        c_buf2 = new coord(c.get_x(), c.get_y());
        if(c.get_x() + 1 > aX - 1)
          c.set_x(0);
        else
          c.set_x(c.get_x() + 1);
        if(_area[c.get_y()][c.get_x()] == 3)
          isGrow = true;
        if(_area[c.get_y()][c.get_x()] == 4)
          return false;
        _area[c.get_y()][c.get_x()] = 2;
        for(int i = 1; i < snake.size(); i++)
        {
          c = (coord)snake.elementAt(i);
          c_buf = new coord(c.get_x(), c.get_y());
          c.set_x(c_buf2.get_x());
          c.set_y(c_buf2.get_y());
          _area[c.get_y()][c.get_x()] = 1;
          c_buf2.set_x(c_buf.get_x());
          c_buf2.set_y(c_buf.get_y());
        }
      }
    }//if(direction == "RIGHT")

    if(pd == true)
    {
      if(isGrow == false)
      {
        area[c_buf2.get_y()][c_buf2.get_x()].setBackground(fieldArea);
        _area[c_buf2.get_y()][c_buf2.get_x()] = 0;
      }
      else
      {
        snake.addElement(new coord(c_buf2.get_x(), c_buf2.get_y()));
        _area[c_buf2.get_y()][c_buf2.get_x()] = 1;
        incr++;
        snakeLength++;
        lup++;
        foodShow();
      }
    }

    snakeShow();
    return pd;
  }//snakeMove()

  //прорисовка змейки
  void snakeShow()
  {
    coord c;
    for(int i = 0; i < snake.size(); i++)
    {
      c = (coord)snake.elementAt(i);
      if(i == 0)
        area[c.get_y()][c.get_x()].setBackground(snakeHead);
      else
        area[c.get_y()][c.get_x()].setBackground(snakeTail);
    }
  }//snakeShow()

  //прорисовка поля
  void areaShow()
  {
    for(int i = 0; i < aX; i++)
      for(int j = 0; j < aY; j++)
      {
        area[i][j] = new JPanel();//создание панелей
        area[i][j].setBackground(fieldArea);//установка цвета панели
        area[i][j].setBorder(BorderFactory.createLineBorder(fieldBorder));//установка цвета границы панели
        add(area[i][j]);//добавление панели к окну
        _area[i][j] = 0;
      }//for
  }//areaShow()

  //удаление препятствий
  void removeObstacles()
  {
    for(int i = 0; i < aX; i++)
      for(int j = 0; j < aY; j++)
        if(_area[i][j] == 4)
        {
          _area[i][j] = 0;
          area[i][j].setBackground(fieldArea);
        }
  }//removeObstacles()

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
                obstacleShow();
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
            foodShow();

          if(snakeMove(direction) == false || end == true)
          {
            JOptionPane.showMessageDialog(this, "Game Over!!! Your level is " + level +
                            ". Snake Length " + snakeLength,
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

  //нажатие клавиши
  public void keyPressed(KeyEvent ke)
  {
    if(ke.getKeyCode() == KeyEvent.VK_UP)
    {
      if(pause == false)
      {
        if(direction != "DOWN")
        {
          direction = "UP";
          if(snakeMove(direction) == false)
            end = true;
        }
      }
    }
    if(ke.getKeyCode() == KeyEvent.VK_DOWN)
    {
      if(pause == false)
      {
        if(direction != "UP")
        {
          direction = "DOWN";
          if(snakeMove(direction) == false)
            end = true;
        }
      }
    }
    if(ke.getKeyCode() == KeyEvent.VK_LEFT)
    {
      if(pause == false)
      {
        if(direction != "RIGHT")
        {
          direction = "LEFT";
          if(snakeMove(direction) == false)
            end = true;
        }
      }
    }
    if(ke.getKeyCode() == KeyEvent.VK_RIGHT)
    {
      if(pause == false)
      {
        if(direction != "LEFT")
        {
          direction = "RIGHT";
          if(snakeMove(direction) == false)
            end = true;
        }
      }
    }
    if(ke.getKeyCode() == KeyEvent.VK_PAUSE)
      if(pause == true)
        pause = false;
      else
        pause = true;
    if(ke.getKeyCode() == KeyEvent.VK_O)
      if(isObstacle == true)
      {
        isObstacle = false;
        removeObstacles();
      }
      else
        isObstacle = true;
  }//keyPressed

  //освобождение клавиши
  public void keyReleased(KeyEvent ke) {}//keyReleased
  //генерирование символа клавишей
  public void keyTyped(KeyEvent ke) {}//keyTyped

  class coord
  {
    private int x, y;

    coord() {x = 0; y = 0;};
    coord(int x, int y) {this.x = x; this.y = y;}
    void set_x(int x) {this.x = x;}
    void set_y(int y) {this.y = y;}
    int get_x() {return x;}
    int get_y() {return y;}
  }//class coord

}//class frameSnake
