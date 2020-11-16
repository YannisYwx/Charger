package com.sevenchip.charger.holder;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.activity.ChargerDetailActivity;
import com.sevenchip.charger.activity.ChargerSettingsActivity;
import com.sevenchip.charger.activity.SettingsFragment;
import com.sevenchip.charger.base.BaseRecycleHolder;
import com.sevenchip.charger.data.bean.BatteryCharger;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.data.status.WorkStatus;
import com.sevenchip.charger.utils.AppUIFormatUtils;
import com.sevenchip.charger.widget.SwitchButton;

import java.util.IntSummaryStatistics;

import butterknife.BindView;

import static com.sevenchip.charger.activity.TestUDPConnActivity.TAG;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/26 17:45
 * Email : 923080261@qq.com
 * Description :
 */
public class ChargerDeviceHolder extends BaseRecycleHolder<BatteryCharger> implements View.OnClickListener {

    private static final String CH1 = "CH-1";
    private static final String CH2 = "CH-2";

    @BindView(R.id.tv_charger_device)
    TextView tvChargerName;
    @BindView(R.id.iv_arrows)
    ImageView ivArrows;
    @BindView(R.id.tv_sb_1)
    TextView tvCH1Sb;
    @BindView(R.id.tv_channel_1)
    TextView tvCH1Name;
    @BindView(R.id.tv_status_1)
    TextView tvCH1Status;
    @BindView(R.id.tv_t)
    TextView tvCH1T;
    @BindView(R.id.tv_v)
    TextView tvCH1V;
    @BindView(R.id.tv_current_1)
    TextView tvCH1Current;
    @BindView(R.id.tv_temperature_1)
    TextView tvCH1Temperature;
    @BindView(R.id.tv_voltage_1)
    TextView tvCH1Voltage;
    @BindView(R.id.pb_charger_status_1)
    ProgressBar pbCH1;
    @BindView(R.id.tv_pb_v_1)
    TextView tvCH1CPbv;
    @BindView(R.id.tv_c_time)
    TextView tvCH1CurrentTime;
    @BindView(R.id.sb_status_1)
    ImageView sbCH1;

    @BindView(R.id.tv_sb_2)
    TextView tvCH2Sb;
    @BindView(R.id.tv_channel_2)
    TextView tvCH2Name;
    @BindView(R.id.tv_status_2)
    TextView tvCH2Status;
    @BindView(R.id.tv_t_2)
    TextView tvCH2T;
    @BindView(R.id.tv_v_2)
    TextView tvCH2V;
    @BindView(R.id.tv_current_2)
    TextView tvCH2Current;
    @BindView(R.id.tv_temperature_2)
    TextView tvCH2Temperature;
    @BindView(R.id.tv_voltage_2)
    TextView tvCH2Voltage;
    @BindView(R.id.pb_charger_status_2)
    ProgressBar pbCH2;
    @BindView(R.id.tv_pb_v_2)
    TextView tvCH2CPbv;
    @BindView(R.id.tv_c_time_2)
    TextView tvCH2CurrentTime;
    @BindView(R.id.sb_status_2)
    ImageView sbCH2;

    OnBatteryStatusChangeListener mListener;

    public ChargerDeviceHolder(@NonNull View itemView, OnBatteryStatusChangeListener listener) {
        super(itemView);
        this.mListener = listener;
    }

    @Override
    protected void refreshViewHolder(BatteryCharger data) {

        //在辅助状态非零时，app只能发送停止命令，不能发送开始命令
        tvChargerName.setText(data.getDeviceId().substring(5));
        UpstreamData ch1 = data.getCH01();
        UpstreamData ch2 = data.getCH02();

        if (ch1 != null && data.isCH1Online()) {
            tvCH1Name.setText(CH1);
            tvCH1Current.setText(String.valueOf(ch1.getCurrentCurrent()));
            AppUIFormatUtils.setChargerStatusInfo(tvCH1Status, ch1);
            tvCH1Temperature.setText(String.valueOf(ch1.getChargerTemperature()));
            tvCH1Voltage.setText(String.valueOf(ch1.getTotalVoltage()));
            tvCH1CurrentTime.setText(ch1.getChargingTime());
            pbCH1.setProgress(ch1.getChargingPercent());
            int wokeStatus = AppUIFormatUtils.getChargerWorkStatus(ch1);
            pbCH1.setProgressDrawable(mContext.getResources().getDrawable(wokeStatus == WorkStatus.Finished ?
                    R.drawable.layer_list_full_drawable : R.drawable.layer_list_charging_drawable));
            tvCH1CPbv.setText(String.format("%smAh", ch1.getCurrentCapacity()));
            tvCH1Name.setOnClickListener(this);
            tvCH1Status.setOnClickListener(this);
            tvCH1Temperature.setOnClickListener(this);
            tvCH1Current.setOnClickListener(this);
            tvCH1CPbv.setOnClickListener(this);
            tvCH1CurrentTime.setOnClickListener(this);
            tvCH1Voltage.setOnClickListener(this);
            tvCH1V.setOnClickListener(this);
            tvCH1V.setOnClickListener(this);
            pbCH1.setOnClickListener(this);
            sbCH1.setOnClickListener(this);
            sbCH1.setEnabled(true);

            int status = ch1.getBatteryStatus();
            if (status == 0) {
                //标准状态 设备可能处于空闲也可能处于工作
                sbCH1.setSelected(ch1.getDownstreamData().getStatus() == 0);
            } else {
                //只能发送停止命令 且此时的开关状态为 0 开
                sbCH1.setSelected(true);
            }
            tvCH1Sb.setText(sbCH1.isSelected() ? R.string.charger_status_start : R.string.charger_status_stop);
        } else {
            sbCH1.setEnabled(false);
            sbCH1.setSelected(false);
            tvCH1Sb.setText(R.string.charger_status_stop);
            tvCH1Current.setText("0.0");
            AppUIFormatUtils.setOfflineStatus(tvCH1Status);
            tvCH1Temperature.setText("0.0");
            tvCH1Voltage.setText("0");
            tvCH1CurrentTime.setText("00:00:00");
            pbCH1.setProgress(0);
            tvCH1CPbv.setText("0mAh");
        }

        if (ch2 != null && data.isCH2Online()) {
            tvCH2Name.setText(CH2);
            tvCH2Current.setText(String.valueOf(ch2.getCurrentCurrent()));
            AppUIFormatUtils.setChargerStatusInfo(tvCH2Status, ch2);
            tvCH2Temperature.setText(String.valueOf(ch2.getChargerTemperature()));
            tvCH2Voltage.setText(String.valueOf(ch2.getTotalVoltage()));
            tvCH2CurrentTime.setText(ch2.getChargingTime());
            pbCH2.setProgress(ch2.getChargingPercent());
            int wokeStatus = AppUIFormatUtils.getChargerWorkStatus(ch2);
            pbCH2.setProgressDrawable(mContext.getResources().getDrawable(wokeStatus == WorkStatus.Finished ?
                    R.drawable.layer_list_full_drawable : R.drawable.layer_list_charging_drawable));
            tvCH2CPbv.setText(String.format("%smAh", ch2.getCurrentCapacity()));
            tvCH2Name.setOnClickListener(this);
            tvCH2Status.setOnClickListener(this);
            tvCH2Temperature.setOnClickListener(this);
            tvCH2Current.setOnClickListener(this);
            tvCH2CPbv.setOnClickListener(this);
            tvCH2CurrentTime.setOnClickListener(this);
            tvCH2Voltage.setOnClickListener(this);
            tvCH2T.setOnClickListener(this);
            tvCH2V.setOnClickListener(this);
            pbCH2.setOnClickListener(this);
            sbCH2.setOnClickListener(this);
            sbCH2.setEnabled(true);
            int status = ch2.getBatteryStatus();
            if (status == 0) {
                //标准状态 设备可能处于空闲也可能处于工作
                sbCH2.setSelected(ch2.getDownstreamData().getStatus() == 0);
            } else {
                //只能发送停止命令 且此时的开关状态为 0 开
                sbCH2.setSelected(true);
            }
            tvCH2Sb.setText(sbCH2.isSelected() ? R.string.charger_status_start : R.string.charger_status_stop);

        } else {
            sbCH2.setEnabled(false);
            sbCH2.setSelected(false);
            tvCH2Sb.setText(R.string.charger_status_stop);
            tvCH2Current.setText("0.0");
            AppUIFormatUtils.setOfflineStatus(tvCH2Status);
            tvCH2Temperature.setText("0.0");
            tvCH2Voltage.setText("0");
            tvCH2CurrentTime.setText("00:00:00");
            pbCH2.setProgress(0);
            tvCH2CPbv.setText("0mAh");
        }
        ivArrows.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean isChecked;
        switch (v.getId()) {
            case R.id.iv_arrows:
                ChargerSettingsActivity.start(mContext, getData());
                break;
            case R.id.tv_channel_1:
            case R.id.tv_status_1:
            case R.id.tv_current_1:
            case R.id.tv_temperature_1:
            case R.id.tv_voltage_1:
            case R.id.pb_charger_status_1:
            case R.id.tv_pb_v_1:
            case R.id.tv_c_time:
            case R.id.tv_t:
            case R.id.tv_v:
                if (getData().getCH01() != null && getData().isCH1Online()) {
                    ChargerDetailActivity.start(mContext, getData().getCH01());
                }
                break;
            case R.id.tv_channel_2:
            case R.id.tv_status_2:
            case R.id.tv_current_2:
            case R.id.tv_temperature_2:
            case R.id.tv_voltage_2:
            case R.id.pb_charger_status_2:
            case R.id.tv_pb_v_2:
            case R.id.tv_c_time_2:
            case R.id.tv_t_2:
            case R.id.tv_v_2:
                if (getData().getCH02() != null && getData().isCH2Online()) {
                    ChargerDetailActivity.start(mContext, getData().getCH02());
                }
                break;
            case R.id.sb_status_1:
                if (getData().getCH01() != null && getData().isCH1Online()) {
                    isChecked = sbCH1.isSelected();
                    if (mListener != null) {
                        tvCH1Sb.setText(!isChecked ? R.string.charger_status_start : R.string.charger_status_stop);
                        mListener.OnBatteryStatusChanged(!isChecked, getData().getCH01());
                    }
                    sbCH1.setSelected(!isChecked);
                }
                break;
            case R.id.sb_status_2:
                if (getData().getCH02() != null && getData().isCH2Online()) {
                    isChecked = sbCH2.isSelected();
                    if (mListener != null) {
                        tvCH2Sb.setText(!isChecked ? R.string.charger_status_start : R.string.charger_status_stop);
                        mListener.OnBatteryStatusChanged(!isChecked, getData().getCH02());
                    }
                    sbCH2.setSelected(!isChecked);
                }
                break;
            default:
                break;
        }
    }

    public interface OnBatteryStatusChangeListener {
        void OnBatteryStatusChanged(boolean status, UpstreamData upstreamData);
    }
}
