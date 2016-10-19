/*
 * ������վ:http://www.mob.com
 * ����֧��QQ: 4006852216
 * �ٷ�΢��:ShareSDK   ����������°汾�Ļ������ǽ����һʱ��ͨ��΢�Ž��汾�����������͸��������ʹ�ù��������κ����⣬Ҳ����ͨ��΢��������ȡ����ϵ�����ǽ�����24Сʱ�ڸ���ظ���
 *
 * Copyright (c) 2013�� mob.com. All rights reserved.
 */

package cn.sharesdk.onekeyshare;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.mob.tools.FakeActivity;

/** �鿴�༭ҳ����ͼƬ������ */
public class PicViewer extends FakeActivity implements OnTouchListener
{
	private ImageView ivViewer;
	private Bitmap pic;

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	DisplayMetrics dm;

	/** ��С���ű��� */
	float minScaleR = 1f;
	/** ������ű��� */
	static final float MAX_SCALE = 10f;

	/** ��ʼ״̬ */
	static final int NONE = 0;
	/** �϶� */
	static final int DRAG = 1;
	/** ���� */
	static final int ZOOM = 2;

	/** ��ǰģʽ */
	int mode = NONE;

	PointF prev = new PointF();
	PointF mid = new PointF();
	float dist = 1f;

	/** ����ͼƬ������� */
	public void setImageBitmap(Bitmap pic)
	{
		this.pic = pic;
		if (ivViewer != null)
		{
			ivViewer.setImageBitmap(pic);
		}
	}

	public void onCreate()
	{
		ivViewer = new ImageView(activity);
		ivViewer.setScaleType(ScaleType.MATRIX);
		ivViewer.setBackgroundColor(0xc0000000);
		ivViewer.setOnTouchListener(this);
		if (pic != null && !pic.isRecycled())
		{
			ivViewer.setImageBitmap(pic);
		}
		dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);// ��ȡ�ֱ���
		minZoom();
		CheckView();
		ivViewer.setImageMatrix(matrix);
		activity.setContentView(ivViewer);

	}

	/**
	 * ��������
	 */
	public boolean onTouch(View v, MotionEvent event)
	{

		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		// ���㰴��
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			prev.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		// ���㰴��
		case MotionEvent.ACTION_POINTER_DOWN:
			dist = spacing(event);
			// �����������������10�����ж�Ϊ���ģʽ
			if (spacing(event) > 10f)
			{
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG)
			{
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - prev.x, event.getY() - prev.y);
			} else if (mode == ZOOM)
			{
				float newDist = spacing(event);
				if (newDist > 10f)
				{
					matrix.set(savedMatrix);
					float tScale = newDist / dist;
					matrix.postScale(tScale, tScale, mid.x, mid.y);
				}
			}
			break;
		}
		ivViewer.setImageMatrix(matrix);
		CheckView();
		return true;
	}

	/**
	 * ���������С���ű������Զ�����
	 */
	private void CheckView()
	{
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM)
		{
			if (p[0] < minScaleR)
			{
				// Log.d("", "��ǰ���ż���:"+p[0]+",��С���ż���:"+minScaleR);
				matrix.setScale(minScaleR, minScaleR);
			}
			if (p[0] > MAX_SCALE)
			{
				// Log.d("", "��ǰ���ż���:"+p[0]+",������ż���:"+MAX_SCALE);
				matrix.set(savedMatrix);
			}
		}
		center();
	}

	/**
	 * ��С���ű��������Ϊ100%
	 */
	private void minZoom()
	{
		minScaleR = Math.min((float) dm.widthPixels / (float) pic.getWidth(),
				(float) dm.heightPixels / (float) pic.getHeight());
		// ����С���ű�����ʾ
		matrix.setScale(minScaleR, minScaleR);
	}

	private void center()
	{
		center(true, true);
	}

	/**
	 * �����������
	 */
	protected void center(boolean horizontal, boolean vertical)
	{

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, pic.getWidth(), pic.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical)
		{
			// ͼƬС����Ļ��С���������ʾ��������Ļ���Ϸ������������ƣ��·�������������
			int screenHeight = dm.heightPixels;
			if (height < screenHeight)
			{
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0)
			{
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight)
			{
				deltaY = ivViewer.getHeight() - rect.bottom;
			}
		}

		if (horizontal)
		{
			int screenWidth = dm.widthPixels;
			if (width < screenWidth)
			{
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0)
			{
				deltaX = -rect.left;
			} else if (rect.right < screenWidth)
			{
				deltaX = ivViewer.getWidth() - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * ����ľ���
	 */
	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * ������е�
	 */
	private void midPoint(PointF point, MotionEvent event)
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

}
