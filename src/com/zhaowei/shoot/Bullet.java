package com.zhaowei.shoot;

/** 子弹:是飞行物 */
public class Bullet extends FlyingObject {
	private int speed = 5;// 走步的步数

	/** 构造方法 x和y随英雄机的位置不同而不同 */
	public Bullet(int x, int y) {
		image = ShootGame.bullet;// 图片
		width = image.getWidth();// 宽
		height = image.getHeight();// 高
		this.x = x;// x坐标
		this.y = y;// y坐标
	}

	/** 重写step */
	public void step() {
		y -= speed;// y-(向上)
	}

	/** 重写outOFBounds */
	public boolean outOfBounds() {
		return y <= -this.height;// 子弹的y坐标<=子弹的高
	}
}
