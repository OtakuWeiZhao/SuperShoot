package com.zhaowei.shoot;

import java.awt.image.BufferedImage;

/** 英雄机:是飞行物 */
public class Hero extends FlyingObject {
	private int doubleFire;// 火力值
	private int life;// 命
	private BufferedImage[] images = {};// 图片数组
	private int index;// 控制切换的频率

	public Hero() {
		image = ShootGame.hero0;// 图片
		width = image.getWidth();// 宽
		height = image.getHeight();// 高
		x = 150;// x坐标
		y = 400;// y坐标
		doubleFire = 0;// 默认火力值为0
		life = 3;// 3条命
		images = new BufferedImage[] { ShootGame.hero0 };
		index = 0;// 控制切换的频率
	}

	/** 重写step */
	public void step() {// 10毫秒走一次
		image = images[index++ / 10 % images.length];// 每100毫秒走一次
		/**
		 * index++; int a=index/10; int b=a%2;//2即images.length image=images[b];
		 */
	}

	/** 英雄机发射子弹 */
	public Bullet[] shoot() {
		int xStep = this.width / 4;
		if (doubleFire > 0) {// 双倍
			Bullet[] bullets = new Bullet[2];// 双发子弹
			bullets[0] = new Bullet(this.x + 1 * xStep, this.y - 20);
			bullets[1] = new Bullet(this.x + 3 * xStep, this.y - 20);
			return bullets;
		} else {// 单倍
			Bullet[] bullets = new Bullet[1];// 单发子弹
			bullets[0] = new Bullet(this.x + 2 * xStep, this.y - 20);
			return bullets;
		}
	}

	/** 英雄机随着鼠标动 */
	public void moveTo(int x, int y) {
		this.x = x - this.width / 2;
		this.y = y - this.height / 2;
	}

	/** 重写outOFBounds */
	public boolean outOfBounds() {
		return false;// 永不越界
	}

	/** 增命 */
	public void addLife() {
		life++;
	}

	/** 获取命 */
	public int getLife() {
		return life;
	}

	public void subtractLife() {
		life--;
	}

	/** 增火力值 */
	public void addDoubleFire() {
		doubleFire += 40;
	}

	public void setDoubleFire(int doubleFire) {
		this.doubleFire = doubleFire;
	}

	/** 英雄机撞击敌人 this:英雄机 other:敌人 */
	public boolean hit(FlyingObject other) {
		int x1 = other.x - this.width / 2;// x1:敌人的x-1/2英雄机的宽
		int x2 = other.x + other.width + this.width / 2;//
		int y1 = other.y - this.height / 2;// y1:敌人的y-1/英雄的高
		int y2 = other.y + other.height + this.height / 2;
		int x = this.x + this.width / 2;// x:英雄机的x+1/2英雄机的宽
		int y = this.y + this.height / 2;// y:英雄机的y+1/2英雄机的高
		return x > x1 && x < x2 && y > y1 && y < y2;// x在x1 x2之间,并且y在y1 y2之间即撞上了
	}
}