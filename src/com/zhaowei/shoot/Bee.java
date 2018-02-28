package com.zhaowei.shoot;

import java.util.Random;

/** 蜜蜂:既是飞行物,也是奖励 */
public class Bee extends FlyingObject implements Award {
	private int xspeed = 1;// x走步的步数
	private int yspeed = 2;// y走步的步数
	private int awardType;// 奖励的类型

	/** 构造方法 */
	public Bee() {
		image = ShootGame.bee;// 图片
		width = image.getWidth();// 宽
		height = image.getHeight();// 高
		Random rand = new Random();// 随机数对象
		x = rand.nextInt(ShootGame.WIDTH - this.width);// x:0到屏幕宽-蜜蜂宽的随机数
		y = -this.height;// y:负的蜜蜂的高
		awardType = rand.nextInt(2);// 生成0-1之间的随机数
	}

	/** 重写 getType() */
	public int getType() {
		return awardType;// 返回奖励的类型
	}

	/** 重写step */
	public void step() {
		x += xspeed;// x+(向左或向右)
		y += yspeed;// y+(向下)
		if (x >= ShootGame.WIDTH - this.width) {// 若x>=屏幕宽-蜜蜂宽,则加负(向左)
			xspeed = -1;
		}
		if (x <= 0) {// 若x<=0,则加正(向右)
			xspeed = 1;
		}
	}

	/** 重写outOFBounds */
	public boolean outOfBounds() {
		return y >= ShootGame.HEIGHT;// y坐标>=窗口的高,即为出界
	}
}
