package com.zhaowei.shoot;

import java.util.Random;

/** 敌机:是飞行物,也是敌人 */
public class Airplane extends FlyingObject implements Enemy {
	private int speed = 2;// 走步的步数

	/** 构造方法 */
	public Airplane() {
		image = ShootGame.airplane;// 图片
		width = image.getWidth();// 宽
		height = image.getHeight();// 高
		Random rand = new Random();// 随机数对象
		x = rand.nextInt(ShootGame.WIDTH - this.width);// x:0到屏幕宽-敌机宽之间的随机数
		y = -this.height;// y:负的敌机的高
	}

	/** 重写getScore() */
	public int getScore() {
		return 5;
	}

	/** 重写step */
	public void step() {
		y += speed;// y+(向下)
	}

	/** 重写outOFBounds */
	public boolean outOfBounds() {
		return y >= ShootGame.HEIGHT;// y坐标>=窗口的高,即为出界
	}
}
