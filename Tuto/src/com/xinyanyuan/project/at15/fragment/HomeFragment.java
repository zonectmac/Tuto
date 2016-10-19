package com.xinyanyuan.project.at15.fragment;

import java.util.ArrayList;
import java.util.List;

import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.ConvenientBanner.Transformer;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.adapter.NetworkImageHolderView;

public class HomeFragment extends BaseFragment {

	@Override
	protected int getLayoutResID() {
		// TODO Auto-generated method stub
		return R.layout.activity_home;
	}

	private List<String> datas = new ArrayList<String>();
	private ConvenientBanner<String> convenientBanner;

	@SuppressWarnings("unchecked")
	@Override
	protected void initView() {
		datas.add("http://b.zol-img.com.cn/sjbizhi/images/8/640x1136/1441696497183.jpg?downfile=1441696497183.jpg");
		datas.add("http://b.zol-img.com.cn/sjbizhi/images/8/640x960/1441696512136.jpg?downfile=1441696512136.jpg");
		datas.add("http://b.zol-img.com.cn/sjbizhi/images/8/640x960/1441696515139.jpg?downfile=1441696515139.jpg");
		datas.add("http://b.zol-img.com.cn/sjbizhi/images/8/640x960/1441696487797.jpg?downfile=1441696487797.jpg");
		convenientBanner = (ConvenientBanner<String>) findViewById(R.id.convenientBanner);
		convenientBanner
				.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {

					@Override
					public NetworkImageHolderView createHolder() {

						return new NetworkImageHolderView();
					}
				}, datas)
				.setPageTransformer(Transformer.DefaultTransformer)
				// 设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
				.setPageIndicator(
						new int[] { R.drawable.ic_page_indicator,
								R.drawable.ic_page_indicator_focused })
				.startTurning(5000);
	}
	/**
	 * Transformer.DefaultTransformer.getClassName();
	 * Transformer.AccordionTransformer.getClassName();
	 * Transformer.BackgroundToForegroundTransformer.getClassName();
	 * Transformer.CubeInTransformer.getClassName();
	 * Transformer.CubeOutTransformer.getClassName();
	 * Transformer.DepthPageTransformer.getClassName();
	 * Transformer.FlipHorizontalTransformer .getClassName();
	 * Transformer.FlipVerticalTransformer.getClassName();
	 * Transformer.ForegroundToBackgroundTransformer .getClassName();
	 * Transformer.RotateDownTransformer.getClassName();
	 * Transformer.RotateUpTransformer.getClassName();
	 * Transformer.StackTransformer.getClassName();
	 * Transformer.ZoomInTransformer.getClassName();
	 * Transformer.ZoomOutTranformer.getClassName();
	 */
}
