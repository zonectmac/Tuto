package com.xinyanyuan.project.at15.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ajra.multiactiontextview.InputObject;
import com.ajra.multiactiontextview.MultiActionTextView;
import com.ajra.multiactiontextview.MultiActionTextviewClickListener;
import com.bm.photoview.libraries.Info;
import com.bm.photoview.libraries.PhotoView;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.xinyanyuan.project.at15.App;
import com.xinyanyuan.project.at15.R;
import com.xinyanyuan.project.at15.activity.MainActivity;
import com.xinyanyuan.project.at15.activity.PublishActivity;
import com.xinyanyuan.project.at15.activity.anim.ActivityAmin;
import com.xinyanyuan.project.at15.adapter.ListItemAdapter;
import com.xinyanyuan.project.at15.interfaces.PingLun;
import com.xinyanyuan.project.at15.model.Comments;
import com.xinyanyuan.project.at15.model.Praises;
import com.xinyanyuan.project.at15.model.Replys;
import com.xinyanyuan.project.at15.model.Speek;
import com.xinyanyuan.project.at15.utils.BitmapLinUtils;
import com.xinyanyuan.project.at15.utils.BitmapLinUtils.SysooLin;
import com.xinyanyuan.project.at15.utils.ConnUtils;
import com.xinyanyuan.project.at15.utils.ConnUtils.OnHttpCallBack;
import com.xinyanyuan.project.at15.utils.ImageLoadHelper;
import com.xinyanyuan.project.at15.utils.SharedUtils;
import com.xinyanyuan.project.at15.view.CircleImageView;
import com.xinyanyuan.project.at15.view.GridViewLin;
import com.xinyanyuan.project.at15.view.ListViewLin;
import com.xinyanyuan.project.at15.view.xlist.XListView;
import com.xinyanyuan.project.at15.view.xlist.XListView.IXListViewListener;

public class InfoFragment extends BaseFragment implements OnClickListener {
	private XListView lv_speak;
	private SpeekAdapter speekAdapter;
	private RelativeLayout rl_send;
	private TextView tv_send;
	private EditText et_msg;
	private MainActivity main = null;
	private boolean isComment = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
	}

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_info;
	}

	@Override
	protected void initView() {
		rl_send = (RelativeLayout) findViewById(R.id.rl_send);// 评论的输入和发送界面
		rl_send.setOnClickListener(this);
		tv_send = (TextView) rl_send.findViewById(R.id.tv_send);
		tv_send.setOnClickListener(this);
		et_msg = (EditText) rl_send.findViewById(R.id.et_msg);
		et_msg.addTextChangedListener(mTextWatcher);
		speekAdapter = new SpeekAdapter();
		lv_speak = (XListView) findViewById(R.id.lv_speak);
		lv_speak.setAdapter(speekAdapter);
		lv_speak.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				// 下拉刷新的时候调用
				pageNum = 1;
				getSpeekList();
				lv_speak.setPullLoadEnable(true);
			}

			@Override
			public void onLoadMore() {
				// 上拉加载更多的时候调用
				pageNum++;
				getSpeekList();
			}
		});
		findViewById(R.id.iv_add).setOnClickListener(this);
		getSpeekList();
	}

	TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			tv_send.setEnabled(s.length() > 0);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_add:
			startActivity(new Intent(getActivity(), PublishActivity.class));
			ActivityAmin.startActivityAmin(getActivity());
			break;
		case R.id.tv_send:
			if (isComment) {
				pingLunSpeek();
			} else {
				replySpeak();
			}
			break;
		case R.id.rl_send:
			rl_send.setVisibility(View.GONE);
			closeRuanJianPan();
			break;
		}
	}

	/**
	 * 回复说说
	 */
	protected void replySpeak() {
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());

		params.put("commentId", commentId);
		params.put("contentR", et_msg.getText().toString());
		params.put("toUserPhoneR", toUserPhoneRR);
		ConnUtils.post(ConnUtils.URL_REPLY_SPEAK, params, new OnHttpCallBack() {

			@Override
			public void onOk(JSONObject json) {
				Toast.makeText(getActivity(), json.optString("message"),
						Toast.LENGTH_SHORT).show();
				if (json.optInt("code") == 1) {
					getSpeekInfo(speekAdapter.getItem(currentSendSpeekPosition)
							.getSpeekId(), currentSendSpeekPosition);
					Toast.makeText(main, currentSendSpeekPosition + "",
							Toast.LENGTH_SHORT).show();
					et_msg.setText("");
					closeRuanJianPan();
				}
			}

			@Override
			public void onFail(int code, String err) {

			}
		});
	}

	/**
	 * 评论说说
	 */
	private void pingLunSpeek() {
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());
		params.put("speakId", speekAdapter.getItem(currentSendSpeekPosition)
				.getSpeekId());
		params.put("contentC", et_msg.getText().toString());
		ConnUtils.post(ConnUtils.URL_COMMENT_SPEAK, params,
				new OnHttpCallBack() {

					@Override
					public void onOk(JSONObject json) {
						Toast.makeText(getActivity(),
								json.optString("message"), Toast.LENGTH_SHORT)
								.show();
						if (json.optInt("code") == 1) {
							getSpeekInfo(
									speekAdapter.getItem(
											currentSendSpeekPosition)
											.getSpeekId(),
									currentSendSpeekPosition);
							Toast.makeText(main, currentSendSpeekPosition + "",
									Toast.LENGTH_SHORT).show();
							et_msg.setText("");
							closeRuanJianPan();
						}
					}

					@Override
					public void onFail(int code, String err) {

					}

				});
	}

	private int pageNum = 1;

	private void getSpeekList() {
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());
		params.put("pageNum", pageNum);
		params.put("countNum", 10);
		ConnUtils.post(ConnUtils.URL_GETSPEAK_LIST, params,
				new OnHttpCallBack() {
					@Override
					public void onOk(JSONObject json) {
						int code = json.optInt("code");
						Toast.makeText(getActivity(),
								json.optString("message"), Toast.LENGTH_SHORT)
								.show();
						if (code == 1) {
							JSONArray jsonArray = json.optJSONArray("list");
							ArrayList<Speek> listSpeek = new ArrayList<Speek>();
							if (jsonArray.length() < 10)//
							// 证明数据库已经没有更多数据了。禁用上拉加载更多
							{
								lv_speak.setPullLoadEnable(false);
							}
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonSpeek = jsonArray
										.optJSONObject(i);
								int browseNum = jsonSpeek.optInt("browseNum");
								int speakId = jsonSpeek.optInt("speakId");
								long publicTime = jsonSpeek
										.optLong("publicTime");
								String nickName = jsonSpeek
										.optString("nickName");
								String mobileType = jsonSpeek
										.optString("mobileType");
								String icon = jsonSpeek.optString("icon");
								String content = jsonSpeek.optString("content");
								JSONArray arrayPhotos = jsonSpeek
										.optJSONArray("photos");
								ArrayList<String> photos = new ArrayList<String>();
								for (int j = 0; j < arrayPhotos.length(); j++) {
									photos.add(arrayPhotos.optString(j));
								}
								JSONArray arrayPraises = jsonSpeek
										.optJSONArray("praises");
								ArrayList<Praises> praises = new ArrayList<Praises>();
								for (int j = 0; j < arrayPraises.length(); j++) {
									JSONObject jsonPraises = arrayPraises
											.optJSONObject(j);
									int praiseId = jsonPraises
											.optInt("praiseId");
									String nickNameP = jsonPraises
											.optString("nickNameP");
									String userPhoneP = jsonPraises
											.optString("userPhoneP");
									praises.add(new Praises(nickNameP,
											userPhoneP, praiseId));
								}
								JSONArray arrayComments = jsonSpeek
										.optJSONArray("comments");
								ArrayList<Comments> comments = new ArrayList<Comments>();
								for (int j = 0; j < arrayComments.length(); j++) {
									JSONObject jsonComments = arrayComments
											.optJSONObject(j);
									int commentId = jsonComments
											.optInt("commentId");
									String contentC = jsonComments
											.optString("contentC");
									String nickNameC = jsonComments
											.optString("nickNameC");
									long timeC = jsonComments.optLong("timeC");
									String userPhoneC = jsonComments
											.optString("userPhoneC");
									JSONArray arrayReplys = jsonComments
											.optJSONArray("replys");
									ArrayList<Replys> replys = new ArrayList<Replys>();
									for (int k = 0; k < arrayReplys.length(); k++) {
										JSONObject jsonReply = arrayReplys
												.optJSONObject(k);
										int replyId = jsonReply
												.optInt("replyId");
										long timeR = jsonReply.optLong("timeR");
										String contentR = jsonReply
												.optString("contentR");
										String

										nickNameR = jsonReply
												.optString("nickNameR");
										String toNickNameR = jsonReply
												.optString("toNickNameR");
										String toUserPhoneR = jsonReply
												.optString("toUserPhoneR");
										String userPhoneR = jsonReply
												.optString("userPhoneR");
										replys.add(new Replys(nickNameR,
												userPhoneR, toNickNameR,
												toUserPhoneR, contentR, timeR,
												replyId));
									}
									comments.add(new Comments(nickNameC,
											userPhoneC, timeC, commentId,
											contentC, replys));
								}
								listSpeek.add(new Speek(icon, nickName,
										content, mobileType, browseNum,
										speakId, publicTime, photos, praises,
										comments));
							}
							// 刷新适配器
							if (pageNum == 1) {
								speekAdapter.setmList(listSpeek);
								lv_speak.stopRefresh();
							} else {
								speekAdapter.addList(listSpeek);
								lv_speak.stopLoadMore();
							}
						}
					}

					@Override
					public void onFail(int code, String err) {

					}
				});
	}

	// private void getSpeekLists()
	// {
	// RequestParams params = new RequestParams();
	// params.put("userId", App.getApp().getUser().getUserId());
	// params.put("pageNum", pageNum);
	// params.put("countNum", 10);
	// ConnUtils.post(ConnUtils.URL_GETSPEAK_LIST, params, new OnHttpCallBack()
	// {
	//
	// @Override
	// public void onOk(JSONObject json)
	// {
	// int code = json.optInt("code");
	// Toast.makeText(getActivity(), json.optString("message"),
	// Toast.LENGTH_SHORT).show();
	// if (code == 1)
	// {
	// JSONArray jsonArray = json.optJSONArray("list");
	// ArrayList<Speek> listSpeek = new ArrayList<Speek>();
	// if (jsonArray.length() < 10)// 证明数据库已经没有更多数据了。禁用上拉加载更多
	// {
	// lv_speak.setPullLoadEnable(false);
	// }
	// for (int i = 0; i < jsonArray.length(); i++)
	// {
	// JSONObject jsonSpeek = jsonArray.optJSONObject(i);
	// String string = jsonSpeek.toString();
	// Speek speek = new Gson().fromJson(string, Speek.class);
	// listSpeek.add(speek);
	// }
	// // 刷新适配器
	// if (pageNum == 1)
	// {
	// speekAdapter.setmList(listSpeek);
	// lv_speak.stopRefresh();
	// } else
	// {
	// speekAdapter.addList(listSpeek);
	// lv_speak.stopLoadMore();
	// }
	// }
	// }
	//
	// @Override
	// public void onFail(int code, String err)
	// {
	//
	// }
	// });
	// }

	private void getSpeekInfo(int speakId, final int position) {
		RequestParams params = new RequestParams();
		params.put("speakId", speakId);
		ConnUtils.post(ConnUtils.URL_GET_SPEAK_INFO, params,
				new OnHttpCallBack() {

					@Override
					public void onOk(JSONObject json) {
						if (json.optInt("code") == 1) {
							JSONArray array = json.optJSONArray("list");
							String string = array.optJSONObject(0).toString();
							Speek speek = new Gson().fromJson(string,
									Speek.class);
							speekAdapter.setItem(position, speek);
						}
					}

					@Override
					public void onFail(int code, String err) {

					}
				});

	}

	private void praiseSpeek(final int position) {
		final List<Speek> list = speekAdapter.getmList();
		RequestParams params = new RequestParams();
		params.put("userId", App.getApp().getUser().getUserId());
		params.put("speakId", list.get(position).getSpeekId());
		ConnUtils.post(ConnUtils.URL_PRAISES_SPEAK, params,
				new OnHttpCallBack() {
					@Override
					public void onOk(JSONObject json) {
						Toast.makeText(getActivity(),
								json.optString("message"), Toast.LENGTH_SHORT)
								.show();
						if (json.optInt("code") == 1) {
							getSpeekInfo(list.get(position).getSpeekId(),
									position);
						}
					}

					@Override
					public void onFail(int code, String err) {

					}
				});
	}

	private void closeRuanJianPan() {
		if (getActivity().getCurrentFocus() != null) {
			((InputMethodManager) getActivity().getSystemService(
					Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					getActivity().getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private int currentSendSpeekPosition = -1;// 当前即将发送的评论的说说的ID
	private int currentPinglunId = -1;// 当前评论的id
	private int commentId;
	private String toUserPhoneRR;
	private String nickNameRR;

	/**
	 * 说说的适配器
	 * 
	 * @author Administrator
	 *
	 */
	private class SpeekAdapter extends ListItemAdapter<Speek> {

		public SpeekAdapter() {
			super(getActivity());
		}

		private OnClickListener mOnClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				switch (v.getId()) {
				case R.id.iv_praise:
					praiseSpeek(position);
					break;
				case R.id.iv_comment:
					rl_send.setVisibility(View.VISIBLE);
					currentSendSpeekPosition = position;
					break;
				case R.id.iv_share:
					SharedUtils.showShare(getActivity());
					break;
				case R.id.tv_say:
					rl_send.setVisibility(View.VISIBLE);
					currentSendSpeekPosition = position;
					break;
				}
			}
		};

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.view_speek_item,
						null);
				new HolderSpeek(convertView);
			}
			HolderSpeek holderSpeek = (HolderSpeek) convertView.getTag();
			Speek speek = getItem(position);
			ImageLoadHelper.displayImage2Url(speek.getIcon(),
					holderSpeek.civ_speekIcon);
			holderSpeek.tv_nickName.setText(speek.getNickName());
			holderSpeek.tv_time.setText(speek.getPublicTime());
			holderSpeek.tv_content.setText(speek.getContent());
			holderSpeek.tv_mobileType.setText(speek.getMobileType());
			holderSpeek.tv_browseNum.setText(speek.getBrowseNum());
			holderSpeek.iv_praise.setOnClickListener(mOnClickListener);
			holderSpeek.iv_praise.setTag(position);
			holderSpeek.iv_comments.setOnClickListener(mOnClickListener);
			holderSpeek.iv_comments.setTag(position);
			holderSpeek.iv_share.setOnClickListener(mOnClickListener);
			holderSpeek.iv_share.setTag(position);
			holderSpeek.tv_say.setOnClickListener(mOnClickListener);
			holderSpeek.tv_say.setTag(position);
			List<String> photos = speek.getPhotos();
			switch (photos.size()) {
			case 1:
				holderSpeek.gv_speekImage.setNumColumns(2);
				break;
			default:
				holderSpeek.gv_speekImage.setNumColumns(3);
				break;
			}
			/**********************
			 * 图片
			 **************************/
			ImageAdapter adapter = new ImageAdapter(photos.size());
			holderSpeek.gv_speekImage.setAdapter(adapter);
			adapter.setmList(photos);
			/*********************
			 * 点赞
			 ***************************/
			List<Praises> praises = speek.getPraises();
			holderSpeek.tv_praises.setText("");
			if (praises.size() > 0) {
				formatPraise(holderSpeek.tv_praises, praises);
			}
			/********************
			 * 评论与回复
			 ****************************/
			List<PingLun> listPl = new ArrayList<PingLun>();
			List<Comments> comments = speek.getComments();
			for (Comments c : comments) {
				listPl.add(c);
				List<Replys> replys = c.getReplys();
				for (Replys r : replys) {
					listPl.add(r);
				}
			}
			final PingLunAdapter pingLunAdapter = new PingLunAdapter();
			holderSpeek.lv_pingLun.setAdapter(pingLunAdapter);
			holderSpeek.lv_pingLun
					.setOnItemClickListener(new OnItemClickListener() {// 必须在Item里面添加
						// android:descendantFocusability="blocksDescendants"才能拿到监听事件

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int positions, long id) {
							currentSendSpeekPosition = position;
							PingLun PingLunItem = pingLunAdapter
									.getItem(positions);
							if (PingLunItem instanceof Comments) {// 评论
								Comments c = (Comments) PingLunItem;
								commentId = c.getCommentId();
								nickNameRR = c.getNickNameC();
							} else if (PingLunItem instanceof Replys) {
								Replys r = (Replys) PingLunItem;
								toUserPhoneRR = r.getUserPhoneR();
								nickNameRR = r.getNickNameR();
								commentId = r.getReplyId();
							}
							et_msg.setHint("回复" + nickNameRR + ":");
							currentPinglunId = positions;
							Toast.makeText(mContext,
									currentPinglunId + "" + toUserPhoneRR,
									Toast.LENGTH_SHORT).show();
							rl_send.setVisibility(View.VISIBLE);
							isComment = false;
						}
					});
			pingLunAdapter.setmList(listPl);
			/************************************************/
			return convertView;

		}

		class PingLunAdapter extends ListItemAdapter<PingLun> {

			public PingLunAdapter() {
				super(getActivity());
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv_pinglun = null;

				if (convertView == null) {
					convertView = View.inflate(mContext,
							R.layout.view_pinglun_item, null);
					tv_pinglun = (TextView) convertView
							.findViewById(R.id.tv_pinglun);
					convertView.setTag(tv_pinglun);
				} else
					tv_pinglun = (TextView) convertView.getTag();
				PingLun item = getItem(position);
				PingLunMultiActionClickListener pingLunMultiActionClickListener = new PingLunMultiActionClickListener(
						getmList());
				if (item instanceof Comments)// 评论
				{
					Comments c = (Comments) item;
					SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
					stringBuilder.append(c.getNickNameC());
					stringBuilder.append(":");
					stringBuilder.append(c.getContentC());
					InputObject contentClick = new InputObject();
					contentClick.setStartSpan(0);
					contentClick.setEndSpan(c.getNickNameC().length());
					contentClick.setStringBuilder(stringBuilder);
					contentClick
							.setMultiActionTextviewClickListener(pingLunMultiActionClickListener);
					contentClick.setInputObject(c.getUserPhoneC());
					MultiActionTextView
							.addActionOnTextViewWithoutLink(contentClick);
					MultiActionTextView.setSpannableText(tv_pinglun,
							stringBuilder, Color.parseColor("#7BC846"));
				} else
				// 回复
				{
					Replys r = (Replys) item;
					SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
					String HUIFU = "回复";
					stringBuilder.append(r.getNickNameR());
					stringBuilder.append(HUIFU);
					stringBuilder.append(r.getToNickNameR());
					stringBuilder.append(":");
					stringBuilder.append(r.getContentR());
					int start = 0;
					int end = r.getNickNameR().length();
					InputObject contentClick = new InputObject();
					contentClick.setStartSpan(start);
					contentClick.setEndSpan(end);
					contentClick.setStringBuilder(stringBuilder);
					contentClick
							.setMultiActionTextviewClickListener(pingLunMultiActionClickListener);
					contentClick.setInputObject(r.getUserPhoneR());
					MultiActionTextView
							.addActionOnTextViewWithoutLink(contentClick);
					start = end + HUIFU.length();
					end = start + r.getToNickNameR().length();
					InputObject contentClick2 = new InputObject();
					contentClick2.setStartSpan(start);
					contentClick2.setEndSpan(end);
					contentClick2.setStringBuilder(stringBuilder);
					contentClick2
							.setMultiActionTextviewClickListener(pingLunMultiActionClickListener);
					contentClick2.setInputObject(r.getToUserPhoneR());
					MultiActionTextView
							.addActionOnTextViewWithoutLink(contentClick2);
					MultiActionTextView.setSpannableText(tv_pinglun,
							stringBuilder, Color.parseColor("#7BC846"));
				}
				return convertView;
			}
		}

		class PingLunMultiActionClickListener implements

		MultiActionTextviewClickListener {
			List<PingLun> listPl = null;

			public PingLunMultiActionClickListener(List<PingLun> listPl) {
				this.listPl = listPl;
			}

			@Override
			public void onTextClick(InputObject inputObject) {
				String phone = String.valueOf(inputObject.getInputObject());
				for (PingLun pingLun : listPl) {
					if (pingLun instanceof Comments)// 评论
					{
						Comments c = (Comments) pingLun;
						String userPhoneC = c.getUserPhoneC();
						if (userPhoneC.equals(phone)) {
							Toast.makeText(mContext,
									"应该跳转到" + userPhoneC + "的说说详情去",
									Toast.LENGTH_SHORT).show();
							break;
						}
					} else if (pingLun instanceof Replys)
					// 回复
					{
						Replys r = (Replys) pingLun;
						String userPhoneR = r.getUserPhoneR();
						String toUserPhoneR = r.getToUserPhoneR();
						if (userPhoneR.equals(phone)) {
							Toast.makeText(mContext,
									"应该跳转到" + userPhoneR + "的说说详情去",
									Toast.LENGTH_SHORT).show();
							break;
						} else if (toUserPhoneR.equals(phone)) {
							Toast.makeText(mContext,
									"应该跳转到" + toUserPhoneR + "的说说详情去",
									Toast.LENGTH_SHORT).show();
							break;
						}
					}

				}
			}
		}

		void formatPraise(TextView tv, List<Praises> praises) {
			// 张三、李四、王五啊、老刘六六六、张飞覅菲菲飞觉得很赞
			MyMultiActionClickListener myMultiActionClickListener = new MyMultiActionClickListener(
					praises);
			SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
			for (int i = 0; i < praises.size(); i++) {
				stringBuilder.append(praises.get

				(i).getNickNameP());
				stringBuilder.append("、");
			}
			stringBuilder.delete(stringBuilder.length() - 1,
					stringBuilder.length());// 删除最后一个、号
			stringBuilder.append("觉得很赞");
			int startSpan = 0;
			int endSpan = 0;
			for (int i = 0; i < praises.size(); i++) {
				if (i > 0) {
					startSpan = endSpan;
				}
				Praises p = praises.get(i);
				endSpan = startSpan + p.getNickNameP().length()
						+ (praises.size() - 1 == i ? 0 : 1);
				InputObject contentClick = new InputObject();
				contentClick.setStartSpan(startSpan);
				contentClick.setEndSpan(endSpan);
				contentClick.setStringBuilder(stringBuilder);
				contentClick
						.setMultiActionTextviewClickListener(myMultiActionClickListener);
				contentClick.setOperationType(p.getPraiseId());
				MultiActionTextView
						.addActionOnTextViewWithoutLink(contentClick);
			}
			MultiActionTextView.setSpannableText(tv, stringBuilder, Color.BLUE);
		}

		class MyMultiActionClickListener implements

		MultiActionTextviewClickListener {
			List<Praises> praises = null;

			public MyMultiActionClickListener(List<Praises> praises) {
				this.praises = praises;
			}

			@Override
			public void onTextClick(InputObject

			inputObject) {
				int operation = inputObject.getOperationType();
				for (Praises p : praises) {
					if (p.getPraiseId() == operation) {
						Toast.makeText(mContext,
								"应该跳转到" + p.getUserPhoneP() + "的主页去",

								Toast.LENGTH_SHORT).show();
					}
				}
			}
		}

		class ImageAdapter extends ListItemAdapter<String> {
			int initCount = 0;

			public ImageAdapter(int initCount) {
				super(getActivity());
				this.initCount = initCount;
			}

			@Override
			public int getCount() {
				return initCount == 4 ? super.getCount() + 1 : super.getCount();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				PhotoView iv_photo = null;
				if (convertView == null) {
					convertView = View.inflate(mContext,
							R.layout.view_image_item, null);
					iv_photo = (PhotoView) convertView
							.findViewById(R.id.iv_photo);
					convertView.setTag(iv_photo);
				} else
					iv_photo = (PhotoView) convertView.getTag();
				int gvWidth = getActivity().getResources().getDisplayMetrics().widthPixels
						- BitmapLinUtils.dp2px(mContext, 26);
				int photoWidth = gvWidth / 3;
				LayoutParams lp = (LayoutParams) iv_photo.getLayoutParams();
				lp.width = photoWidth;
				lp.height = photoWidth;
				iv_photo.setLayoutParams(lp);
				int index = 0;
				switch (initCount) {
				case 1:
					break;
				case 4:
					if (position == 2) {
						iv_photo.setImageResource(0);
					} else {
						index = position >= 3 ? position - 1 : position;
					}
					break;
				default:
					index = position;
					break;
				}
				ImageLoadHelper.getInstance().displayImage(getItem(index),
						iv_photo, ImageLoadHelper.getDefaultFadeOptions());
				// 设置不可以双指缩放移动放大等操作，跟普通的image一模一样,默认情况下就是disenable()状态
				iv_photo.disenable();
				iv_photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				iv_photo.setTag(getItem(index));
				iv_photo.setOnClickListener(new OnClickListener() {

					private String lastImageUrl;

					@Override
					public void onClick(View v) {
						PhotoView pv = (PhotoView) v;
						lastImageUrl = String.valueOf(v.getTag());
						main.getPv_showPhoto().setVisibility(View.VISIBLE);
						Info lasPhotoViewInfo = pv.getInfo();
						main.setLastPhotoViewInfo(lasPhotoViewInfo);
						main.getPv_showPhoto().animaFrom(lasPhotoViewInfo);
						SysooLin.i("lastImageUrl" + lastImageUrl);
						ImageLoadHelper.getInstance().displayImage(
								lastImageUrl, main.getPv_showPhoto(),
								ImageLoadHelper.getDefaultFadeOptions());
					}
				});
				return convertView;
			}
		}

		class HolderSpeek {
			CircleImageView civ_speekIcon;// 头像
			TextView tv_nickName;// 昵称
			TextView tv_time;// 发表时间
			TextView tv_content;// 发表内容
			GridViewLin gv_speekImage;// 发表的图片
			ListViewLin lv_pingLun;// 发表的评论
			TextView tv_mobileType;// 发表的手机类型
			ImageView iv_praise;// 点赞按钮
			ImageView iv_comments;// 评论按钮
			ImageView iv_share;// 分享按钮
			TextView tv_praises;// 点赞内容
			TextView tv_browseNum;// 浏览次数
			TextView tv_say;// 评论按钮

			public HolderSpeek(View v) {
				civ_speekIcon = (CircleImageView) v
						.findViewById(R.id.civ_speekIcon);
				tv_nickName = (TextView) v.findViewById(R.id.tv_nickName);
				tv_time = (TextView) v.findViewById(R.id.tv_publicTime);
				tv_content = (TextView) v.findViewById(R.id.tv_content);
				gv_speekImage = (GridViewLin) v
						.findViewById(R.id.gv_speekImage);
				tv_mobileType = (TextView) v.findViewById(R.id.tv_mobileType);
				iv_share = (ImageView) v.findViewById(R.id.iv_share);
				iv_praise = (ImageView) v.findViewById(R.id.iv_praise);
				iv_comments = (ImageView) v.findViewById(R.id.iv_comment);
				tv_praises = (TextView) v.findViewById(R.id.tv_praises);
				tv_browseNum = (TextView) v.findViewById(R.id.tv_browseNum);
				tv_say = (TextView) v.findViewById(R.id.tv_say);
				lv_pingLun = (ListViewLin) v.findViewById(R.id.lv_pingLun);
				v.setTag(this);
			}

		}

	}

}
