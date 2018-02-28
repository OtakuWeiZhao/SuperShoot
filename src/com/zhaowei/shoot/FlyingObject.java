package com.zhaowei.shoot;

import java.awt.image.BufferedImage;

/** 飞行物 */
public abstract class FlyingObject {
	protected BufferedImage image;// 图片
	protected int width;// 宽
	protected int height;// 高
	protected int x;// x坐标
	protected int y;// y坐标

	/** 飞行物走一步 */
	public abstract void step();

	/** 检查飞行物是否越界 */
	public abstract boolean outOfBounds();

	/** 敌人被子弹射击 */
	public boolean shootBy(Bullet bullet) {
		int x1 = this.x;// x1:敌人的x
		int x2 = this.x + this.width;//
		int y1 = this.y;// y1:敌人的y
		int y2 = this.y + this.height;
		int x = bullet.x;// 子弹的x
		int y = bullet.y;// 子弹的y
		return x > x1 && x < x2 && y > y1 && y < y2;// x在x1和x2之间,并且y在y1和y2之间
	}
}
