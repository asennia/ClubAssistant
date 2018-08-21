package com.windfind.clubassistant.member;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.windfind.clubassistant.CAApplication;
import com.windfind.clubassistant.DataModel;
import com.windfind.clubassistant.R;
import com.windfind.clubassistant.view.BottomBar;

public class EditMemberActivity extends AppCompatActivity {

	public static final int MODE_NEW = 0;
	public static final int MODE_EDIT = 1;

	private static final int MAX_POS = 2;

	private DataModel mModel;
	private Resources mRes;
	private LayoutInflater mInflater;

	private BottomBar mBottomBar;
	private EditMemberAdapter mAdapter;

	private ViewGroup mBaseInfoContainer;
	private ViewGroup mAbilityContainer;

	private ImageButton mBtnVip;
	private boolean mIsVip = false;
	private EditText mNameEditor;

	private PositionItem mPosFW;
	private PositionItem mPosWG;
	private PositionItem mPosDM;
	private PositionItem mPosDC;
	private PositionItem mPosGK;

	private int mPos = 0;
	private int mPosCount = 0;
	private boolean mPosLocked = false;

	private TextView mLabelSpeed;
	private TextView mLabelStrength;
	private TextView mLabelDefence;
	private TextView mLabelTech;
	private TextView mLabelPass;
	private TextView mLabelShoot;

	private SeekBar mSeekBarSpeed;
	private SeekBar mSeekBarStrength;
	private SeekBar mSeekBarDefence;
	private SeekBar mSeekBarTech;
	private SeekBar mSeekBarPass;
	private SeekBar mSeekBarShoot;

	private int mSpeed;
	private int mStrength;
	private int mDefence;
	private int mTech;
	private int mPass;
	private int mShoot;

	private int mDefaultValue;

	private int mFocusedColor;
	private int mUnfocusColor;

	private int mMode;
	private long mModifiedId;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mModel = ((CAApplication) getApplicationContext()).getModel();
		mRes = getResources();
		mInflater = LayoutInflater.from(this);
		mFocusedColor = mRes.getColor(R.color.new_member_pos_focused);
		mUnfocusColor = mRes.getColor(R.color.new_member_pos_unfocus);
		mDefaultValue = mRes.getInteger(R.integer.default_ability_value);

		setContentView(R.layout.edit_member_layout);

		initView();

		Intent intent = getIntent();
		mMode = intent.getIntExtra("mode", MODE_NEW);
		if (mMode == MODE_EDIT) {
			loadDataAndSetView(intent);
		} else {
			loadDefaultDataAndSetView();
		}
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void initView() {
		mBottomBar = findViewById(R.id.bottom_bar);
		mBottomBar.setLeftButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditMemberActivity.this.finish();
			}
		});

		mBottomBar.setRightButtonEnable(false);
		mBottomBar.setRightButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MemberBean member = new MemberBean();

				member.mName = mNameEditor.getText().toString();
				member.mPosition = mPos;
				member.mIsVip = mIsVip;
				member.mSpeed = mSpeed;
				member.mStrength = mStrength;
				member.mDefence = mDefence;
				member.mTech = mTech;
				member.mPass = mPass;
				member.mShoot = mShoot;

				switch (mMode) {
					case MODE_NEW:
						mModel.addMember(member);
						break;
					case MODE_EDIT:
						mModel.updateMember(mModifiedId, member);
						break;
					default:
						break;
				}

				EditMemberActivity.this.finish();
			}
		});

		ViewPager viewPager = findViewById(R.id.editor_pager);
		mAdapter = new EditMemberAdapter();
		viewPager.setAdapter(mAdapter);

		mBaseInfoContainer = (ViewGroup) mInflater.inflate(R.layout.member_normal_info_editor_page, null);
		initBaseInfoView();
		mAdapter.addItem(mBaseInfoContainer);

		mAbilityContainer = (ViewGroup) mInflater.inflate(R.layout.member_ability_info_editor_page, null);
		initAbilityView();

		ImageButton btnExpand = findViewById(R.id.btn_expend);
		if (btnExpand != null) {
			btnExpand.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mAdapter.addItem(mAbilityContainer);
				}
			});
		}
	}

	@SuppressWarnings("deprecation")
	private void initBaseInfoView() {
		mBtnVip = mBaseInfoContainer.findViewById(R.id.btn_vip);
		mBtnVip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mIsVip = !mIsVip;

				int drawableId = mIsVip ? R.drawable.star_selected : R.drawable.star_unselected;
				mBtnVip.setImageDrawable(mRes.getDrawable(drawableId));
			}
		});

		mNameEditor = mBaseInfoContainer.findViewById(R.id.new_member_editor);
		mNameEditor.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String content = mNameEditor.getText().toString();
				mBottomBar.setRightButtonEnable(!TextUtils.isEmpty(content));
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		ViewGroup container = mBaseInfoContainer.findViewById(R.id.item_fw);
		CheckBox cb = mBaseInfoContainer.findViewById(R.id.item_fw_cb);
		TextView label = mBaseInfoContainer.findViewById(R.id.item_fw_text);
		mPosFW = new PositionItem(container, cb, label, MemberBean.POS_FW);
		if (container != null) {
			container.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPosFW.setSelected(mPosFW.isUnselected());
				}
			});
		}
		if (cb != null) {
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mPosFW.onSelected(isChecked);
				}
			});
		}

		container = mBaseInfoContainer.findViewById(R.id.item_wg);
		cb = mBaseInfoContainer.findViewById(R.id.item_wg_cb);
		label = mBaseInfoContainer.findViewById(R.id.item_wg_text);
		mPosWG = new PositionItem(container, cb, label, MemberBean.POS_WG);
		if (container != null) {
			container.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPosWG.setSelected(mPosWG.isUnselected());
				}
			});
		}
		if (cb != null) {
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mPosWG.onSelected(isChecked);
				}
			});
		}

		container = mBaseInfoContainer.findViewById(R.id.item_dm);
		cb = mBaseInfoContainer.findViewById(R.id.item_dm_cb);
		label = mBaseInfoContainer.findViewById(R.id.item_dm_text);
		mPosDM = new PositionItem(container, cb, label, MemberBean.POS_DM);
		if (container != null) {
			container.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPosDM.setSelected(mPosDM.isUnselected());
				}
			});
		}
		if (cb != null) {
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mPosDM.onSelected(isChecked);
				}
			});
		}

		container = mBaseInfoContainer.findViewById(R.id.item_dc);
		cb = mBaseInfoContainer.findViewById(R.id.item_dc_cb);
		label = mBaseInfoContainer.findViewById(R.id.item_dc_text);
		mPosDC = new PositionItem(container, cb, label, MemberBean.POS_DC);
		if (container != null) {
			container.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPosDC.setSelected(mPosDC.isUnselected());
				}
			});
		}
		if (cb != null) {
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mPosDC.onSelected(isChecked);
				}
			});
		}

		container = mBaseInfoContainer.findViewById(R.id.item_gk);
		cb = mBaseInfoContainer.findViewById(R.id.item_gk_cb);
		label = mBaseInfoContainer.findViewById(R.id.item_gk_text);
		mPosGK = new PositionItem(container, cb, label, MemberBean.POS_GK);
		if (container != null) {
			container.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPosGK.setSelected(mPosGK.isUnselected());
				}
			});
		}
		if (cb != null) {
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mPosGK.onSelected(isChecked);
				}
			});
		}
	}

	private void initAbilityView() {
		mLabelSpeed = mAbilityContainer.findViewById(R.id.label_speed);
		mLabelStrength = mAbilityContainer.findViewById(R.id.label_strength);
		mLabelDefence = mAbilityContainer.findViewById(R.id.label_defence);
		mLabelTech = mAbilityContainer.findViewById(R.id.label_tech);
		mLabelPass = mAbilityContainer.findViewById(R.id.label_pass);
		mLabelShoot = mAbilityContainer.findViewById(R.id.label_shoot);

		mSeekBarSpeed = mAbilityContainer.findViewById(R.id.seekbar_speed);
		mSeekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mSpeed = progress;
				String content = getString(R.string.text_speed) + "  " + mSpeed;
				mLabelSpeed.setText(content);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		mSeekBarStrength = mAbilityContainer.findViewById(R.id.seekbar_strength);
		mSeekBarStrength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mStrength = progress;
				String content = getString(R.string.text_strength) + "  " + mStrength;
				mLabelStrength.setText(content);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		mSeekBarDefence = mAbilityContainer.findViewById(R.id.seekbar_defence);
		mSeekBarDefence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mDefence = progress;
				String content = getString(R.string.text_defence) + "  " + mDefence;
				mLabelDefence.setText(content);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		mSeekBarTech = mAbilityContainer.findViewById(R.id.seekbar_tech);
		mSeekBarTech.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mTech = progress;
				String content = getString(R.string.text_tech) + "  " + mTech;
				mLabelTech.setText(content);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		mSeekBarPass = mAbilityContainer.findViewById(R.id.seekbar_pass);
		mSeekBarPass.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mPass = progress;
				String content = getString(R.string.text_pass) + "  " + mPass;
				mLabelPass.setText(content);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		mSeekBarShoot = mAbilityContainer.findViewById(R.id.seekbar_shoot);
		mSeekBarShoot.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mShoot = progress;
				String content = getString(R.string.text_shoot) + "  " + mShoot;
				mLabelShoot.setText(content);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
	}

	/*
	 * If selected positions are more than MAX_POS, should forbid selecting more position
	 */
	private void checkPosCount(int count) {
		PositionItem[] pis = new PositionItem[] {mPosFW, mPosWG, mPosDM, mPosDC, mPosGK};

		if (count < MAX_POS) {
			if (!mPosLocked) {
				return;
			}

			// Unlock all disabled ViewGroup
			for (PositionItem pi : pis) {
				if (pi.isLocked()) {
					pi.setLocked(false);
				}
			}

			mPosLocked = false;
		} else {
			// Lock all unchecked CheckBox
			for (PositionItem pi : pis) {
				if (pi.isUnselected()) {
					pi.setLocked(true);
				}
			}

			mPosLocked = true;
		}
	}

	@SuppressWarnings("deprecation")
	private void loadDataAndSetView(Intent intent) {
		if (intent == null) {
			return;
		}

		long id = intent.getLongExtra("member_id", -1);
		if (id == -1) {
			return;
		}

		MemberBean member = mModel.getMemberById(id);
		if (member == null) {
			return;
		}

		mModifiedId = id;

		mNameEditor.setText(member.mName);

		int pos = member.mPosition;
		if ((pos & MemberBean.POS_FW) == MemberBean.POS_FW) {
			mPosFW.setSelected(true);
		}
		if ((pos & MemberBean.POS_WG) == MemberBean.POS_WG) {
			mPosWG.setSelected(true);
		}
		if ((pos & MemberBean.POS_DM) == MemberBean.POS_DM) {
			mPosDM.setSelected(true);
		}
		if ((pos & MemberBean.POS_DC) == MemberBean.POS_DC) {
			mPosDC.setSelected(true);
		}
		if ((pos & MemberBean.POS_GK) == MemberBean.POS_GK) {
			mPosGK.setSelected(true);
		}

		int drawableId = member.mIsVip ? R.drawable.star_selected : R.drawable.star_unselected;
		mBtnVip.setImageDrawable(mRes.getDrawable(drawableId));

		mSpeed = member.mSpeed;
		mStrength = member.mStrength;
		mDefence = member.mDefence;
		mTech = member.mTech;
		mPass = member.mPass;
		mShoot = member.mShoot;

		mSeekBarSpeed.setProgress(mSpeed);
		String content = getString(R.string.text_speed) + "  " + mSpeed;
		mLabelSpeed.setText(content);

		mSeekBarStrength.setProgress(mStrength);
		content = getString(R.string.text_strength) + "  " + mStrength;
		mLabelStrength.setText(content);

		mSeekBarDefence.setProgress(mDefence);
		content = getString(R.string.text_defence) + "  " + mDefence;
		mLabelDefence.setText(content);

		mSeekBarTech.setProgress(mTech);
		content = getString(R.string.text_tech) + "  " + mTech;
		mLabelTech.setText(content);

		mSeekBarPass.setProgress(mPass);
		content = getString(R.string.text_pass) + "  " + mPass;
		mLabelPass.setText(content);

		mSeekBarShoot.setProgress(mShoot);
		content = getString(R.string.text_shoot) + "  " + mShoot;
		mLabelShoot.setText(content);

		mBottomBar.setRightButtonEnable(true);
	}

	private void loadDefaultDataAndSetView() {
		mSpeed = mDefaultValue;
		mStrength = mDefaultValue;
		mDefence = mDefaultValue;
		mTech = mDefaultValue;
		mPass = mDefaultValue;
		mShoot = mDefaultValue;

		mSeekBarSpeed.setProgress(mSpeed);
		String content = getString(R.string.text_speed) + "  " + mSpeed;
		mLabelSpeed.setText(content);

		mSeekBarStrength.setProgress(mStrength);
		content = getString(R.string.text_strength) + "  " + mStrength;
		mLabelStrength.setText(content);

		mSeekBarDefence.setProgress(mDefence);
		content = getString(R.string.text_defence) + "  " + mDefence;
		mLabelDefence.setText(content);

		mSeekBarTech.setProgress(mTech);
		content = getString(R.string.text_tech) + "  " + mTech;
		mLabelTech.setText(content);

		mSeekBarPass.setProgress(mPass);
		content = getString(R.string.text_pass) + "  " + mPass;
		mLabelPass.setText(content);

		mSeekBarShoot.setProgress(mShoot);
		content = getString(R.string.text_shoot) + "  " + mShoot;
		mLabelShoot.setText(content);
	}

	private class PositionItem {
		private ViewGroup mContainer;
		private CheckBox mCheckbox;
		private TextView mLabel;
		private int mPosAttr;

		PositionItem(ViewGroup container, CheckBox cb, TextView label, int pos) {
			mContainer = container;
			mCheckbox = cb;
			mLabel = label;
			mPosAttr = pos;
		}

		void onSelected(boolean isSelected) {
			if (mLabel != null) {
				mLabel.setTextColor(isSelected ? mFocusedColor : mUnfocusColor);
			}

			if (isSelected) {
				mPos |= mPosAttr;
			} else {
				mPos &= ~mPosAttr;
			}

			mPosCount = isSelected ? mPosCount + 1 : mPosCount - 1;
			checkPosCount(mPosCount);
		}

		void setSelected(boolean isSelected) {
			if (mCheckbox != null) {
				mCheckbox.setChecked(isSelected);
			}
		}

		boolean isUnselected() {
			boolean ret = true;
			if (mCheckbox != null) {
				ret = mCheckbox.isChecked();
			}

			return !ret;
		}

		void setLocked(boolean isLock) {
			if (mContainer != null) {
				mContainer.setEnabled(!isLock);
			}

			if (mCheckbox != null) {
				mCheckbox.setEnabled(!isLock);
			}
		}

		boolean isLocked() {
			boolean ret = false;
			if (mContainer != null) {
				ret = !mContainer.isEnabled();
			}

			return ret;
		}
	}
}
