package com.zhaowei.shoot;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;

//主程序类
public class ShootGame extends JPanel {
	public static final int WIDTH = 400;// 窗口宽
	public static final int HEIGHT = 645;// 窗口高

	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	private int state = 0;

	public static BufferedImage background;// 背景图
	public static BufferedImage start;// 启动图
	public static BufferedImage pause;// 暂停图
	public static BufferedImage gameover;// 游戏结束图
	public static BufferedImage airplane;// 敌机
	public static BufferedImage bee;// 蜜蜂
	public static BufferedImage bullet;// 子弹
	public static BufferedImage hero0;// 英雄机0

	private Hero hero = new Hero();// 英雄机对象
	private Bullet[] bullets = {};// 子弹数组对象
	private FlyingObject[] flyings = {};// 敌人对象(敌机+小蜜蜂)

	static {// 初始化静态资源
		try {
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 创建敌人(敌机+小蜜蜂)对象 */
	public static FlyingObject nextOne() {
		Random rand = new Random();// 随机数对象
		int type = rand.nextInt(20);// 生成0-19之间的数
		if (type == 0) {// 若为0则返回小蜜蜂对象
			return new Bee();
		} else {// 1到19时返回敌机对象
			return new Airplane();
		}
	}

	int flyEnteredIndex = 0;

	/** 敌人(敌机+小蜜蜂)入场 */
	public void enterAction() {// 10毫秒走一次
		flyEnteredIndex++;// 每10毫秒增1
		if (flyEnteredIndex % 40 == 0) {// 400毫秒走一次
			FlyingObject obj = nextOne();// 获取敌人(敌机+小蜜蜂)
			flyings = Arrays.copyOf(flyings, flyings.length + 1);// 扩容
			flyings[flyings.length - 1] = obj;// 将敌人对象赋值给flyings数组中最后一个元素
		}
	}

	/** 飞行物走一步 */
	public void stepAction() {// 10毫秒走一次
		hero.step();
		for (int i = 0; i < flyings.length; i++) {
			flyings[i].step();// 敌人走一步
		}
		for (int i = 0; i < bullets.length; i++) {
			bullets[i].step();// 子弹走一步
		}
	}

	int shootIndex = 0;

	/** 子弹入场(英雄机发射子弹) */
	public void shootAction() {// 10毫秒走一次
		shootIndex++;// 每10毫秒增1
		if (shootIndex % 10 == 0) {// 300毫秒(10*30)
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
			System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
		}
	}

	public void outOfBoundsAction() {
		int index = 0;// 1.不越界敌人数组的下标 2.不越界敌人个数
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			if (!f.outOfBounds()) {// 若不越界
				flyingLives[index] = f;// 将不越界
				index++;// 1.不越界敌人数组下标增1
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);// 将数组元素复制到flyings数组中,index

		index = 0;
		Bullet[] bulletLives = new Bullet[bullets.length];
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			if (!b.outOfBounds()) {
				bulletLives[index] = b;
				index++;
			}
		}
		bullets = Arrays.copyOf(bulletLives, index);
	}

	/** 所有子弹撞击所有敌人 */
	public void bangAction() {
		for (int i = 0; i < bullets.length; i++) {
			bang(bullets[i]);// 将每个子弹传递给bang方法
		}
	}

	int score = 0;// 得分

	/** 一个子弹与所有敌人撞 */
	public void bang(Bullet bullet) {
		int index = -1;// 被撞敌人下标
		for (int i = 0; i < flyings.length; i++) {// 遍历所有敌人
			FlyingObject obj = flyings[i];
			if (obj.shootBy(bullet)) {
				index = i;// 记录被撞敌人的下标
				break;// 剩余的敌人不需要再对比了
			}
		}
		if (index != -1) {// 撞上了
			FlyingObject one = flyings[index];// 获取被撞敌人
			if (one instanceof Enemy) {// 若被撞对象是敌人
				Enemy e = (Enemy) one;// 将被撞对象强转为分
				score += e.getScore();// 累加分
			}
			if (one instanceof Award) {// 若被撞对象是奖励
				Award a = (Award) one;// 将被撞对象强转为命
				int type = a.getType();// 获取奖励类型
				switch (type) {
				case Award.DOUBLE_FIRE:// 奖励类型为火力值
					hero.addDoubleFire();// 英雄机火力值增1
					break;
				case Award.LIFE:// 奖励类型为命时英雄机命增1
					hero.addLife();// 英雄机命增1
					break;
				}
			}
			// 将被撞敌人与数组中最后一个元素交换
			FlyingObject temp = flyings[index];
			flyings[index] = flyings[flyings.length - 1];
			flyings[flyings.length - 1] = temp;
			// 缩容(去掉最后一个元素,即被撞敌人对象)
			flyings = Arrays.copyOf(flyings, flyings.length - 1);
		}
	}

	/** 检查游戏结束 */
	public void checkGameOverAction() {
		if (isGameOver()) {// 若游戏结束
			state = GAME_OVER;
		}
	}

	/** 判断游戏是否结束true即为游戏结束 */
	public boolean isGameOver() {
		for (int i = 0; i < flyings.length; i++) {// 遍历每个敌人
			FlyingObject f = flyings[i];
			if (hero.hit(f)) {// 撞上了
				hero.subtractLife();// 英雄机减命
				hero.setDoubleFire(0);// 火力值归零
				// 交换被撞敌人对象与数组中的最后一位数
				FlyingObject temp = flyings[i];
				flyings[i] = flyings[flyings.length - 1];
				flyings[flyings.length - 1] = temp;
				// 缩容
				flyings = Arrays.copyOf(flyings, flyings.length - 1);
			}
		}
		return hero.getLife() <= 0;// 英雄机命数小于0时游戏结束
	}

	/** 启动程序的执行 */
	public void action() {
		MouseAdapter l = new MouseAdapter() {// 创建侦听器
			/** 鼠标移动事件 */
			public void mouseMoved(MouseEvent e) {
				int x = e.getX();// 获取鼠标x坐标
				int y = e.getY();// 获取鼠标y坐标
				hero.moveTo(x, y);// 英雄机随鼠标移动
			}

			public void mouseClicked(MouseEvent e) {
				switch (state) {
				case START:
					state = RUNNING;
					break;
				case GAME_OVER:
					score = 0;
					hero = new Hero();
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					state = START;
					break;
				}
			}

			public void mouseExited(MouseEvent e) {
				if (state == RUNNING) {
					state = PAUSE;
				}
			}

			public void mouseEntered(MouseEvent e) {
				if (state == PAUSE) {
					state = RUNNING;
				}
			}
		};
		this.addMouseListener(l);// 处理鼠标操作事件
		this.addMouseMotionListener(l);// 处理鼠标滑动事件

		Timer timer = new Timer();// 创建定时器对象
		int intervel = 10;// 定时间隔以(毫秒为单位)
		timer.schedule(new TimerTask() {
			public void run() {// 定时干的事-每10毫秒走一次
				if (state == RUNNING) {
					enterAction();// 敌机走一步
					stepAction();// 飞行物走一步
					shootAction();// 子弹入场(英雄机发射子弹)
					outOfBoundsAction();// 删除越界飞行物
					bangAction();// 子弹射击敌人
					checkGameOverAction();// 检查游戏结束
				}
				repaint();// 重画(调用paint方法)
			}
		}, intervel, intervel);
	}

	/** 重写paint() g:画笔 */
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null);// 画背景
		paintHero(g);// 画英雄机对象
		paintFlyingObjects(g);// 画敌人(敌机+小蜜蜂)对象
		paintBullets(g);// 画子弹对象
		paintScore(g);// 画分和画命
		paintState(g);
	}

	/** 画状态 */
	public void paintState(Graphics g) {
		switch (state) {
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER:
			g.drawImage(gameover, 0, 0, null);
			break;
		}
	}

	/** 画分画命 */
	public void paintScore(Graphics g) {
		g.setColor(new Color(0xff0000));// 设置颜色纯红
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));// 字体样式 加粗 字号
		hero.getLife();
		g.drawString("SCORE:" + score, 10, 25);
		g.drawString("LIFE:" + hero.getLife(), 10, 45);
	}

	/** 画英雄机对象 */
	public void paintHero(Graphics g) {
		g.drawImage(hero.image, hero.x, hero.y, null);
	}

	/** 画敌人(敌机+小蜜蜂)对象 */
	public void paintFlyingObjects(Graphics g) {
		for (int i = 0; i < flyings.length; i++) {// 遍历所有敌人
			FlyingObject f = flyings[i];// 获取每一个敌人
			g.drawImage(f.image, f.x, f.y, null);
		}
	}

	/** 画子弹对象 */
	public void paintBullets(Graphics g) {
		for (int i = 0; i < bullets.length; i++) {
			Bullet b = bullets[i];
			g.drawImage(b.image, b.x, b.y, null);
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Super Shoot");// 创建窗口对象
		ShootGame game = new ShootGame();// 创建面板对象
		frame.add(game);// 将面板添加到窗口上
		frame.setSize(WIDTH, HEIGHT);// 设置窗口大小
		frame.setAlwaysOnTop(true);// 设置一直居上
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置默认关闭操作(窗口关闭时退出程序)
		frame.setLocationRelativeTo(null);// 设置窗口居中
		frame.setVisible(true);// 1.设置窗口可见 2.尽快调用paint()

		game.action();// 启动程序的执行
	}
}
