package com.hzmct.gpio;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.example.x6.gpioctl.GpioUtils;
import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.concurrent.Executors;

/**
 * @author Woong on 4/7/21
 * @website http://woong.cn
 */
public class GpioActivity extends QMUIActivity {
    private static final String TAG = "GpioActivity";

    QMUITopBar mTopBar;
    QMUIGroupListView mGroupListView;

    private QMUICommonListItemView gpioPlatformItem;
    private QMUICommonListItemView gpioReadItem;
    private QMUICommonListItemView gpioWriteItem;
    private int dialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    private GpioUtils gpioUtils = null;
    private String[] platformArray;
    private int gpioCheckIndex = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpio);
        initView();
        initData();
    }

    private void initView() {
        mTopBar = findViewById(R.id.top_bar);
        mGroupListView = findViewById(R.id.groupListView);

        mTopBar.setTitle("GPIO");

        gpioPlatformItem = mGroupListView.createItemView("gpio 平台");
        gpioPlatformItem.setOrientation(QMUICommonListItemView.HORIZONTAL);
        gpioPlatformItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        gpioPlatformItem.setDetailText("请先选择待使用的 GPIO 平台");

        gpioWriteItem = mGroupListView.createItemView("gpio 写操作");
        gpioWriteItem.setOrientation(QMUICommonListItemView.HORIZONTAL);
        gpioWriteItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_NONE);

        gpioReadItem = mGroupListView.createItemView("gpio 读操作");
        gpioReadItem.setOrientation(QMUICommonListItemView.HORIZONTAL);
        gpioReadItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_NONE);

        QMUIGroupListView.newSection(this)
                .setTitle("GPIO")
                .addItemView(gpioPlatformItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new QMUIDialog.CheckableDialogBuilder(GpioActivity.this)
                                .setCheckedIndex(gpioCheckIndex)
                                .addItems(platformArray, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, final int which) {
                                        Executors.newCachedThreadPool().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (gpioUtils != null) {
                                                    gpioUtils.close();
                                                    gpioUtils = null;
                                                }

                                                switch (which) {
                                                    case 0:
                                                        gpioUtils = GpioUtils.getInstance("/dev/rk_gpio");
                                                        break;
                                                    case 1:
                                                        gpioUtils = GpioUtils.getInstance("/dev/sunxi_gpio");
                                                        break;
                                                    case 2:
                                                        gpioUtils = GpioUtils.getInstance("/dev/mc_gpio");
                                                        break;
                                                }
                                            }
                                        });

                                        final QMUITipDialog tipDialog = new QMUITipDialog.Builder(GpioActivity.this)
                                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                                .setTipWord("正在开启 GPIO")
                                                .create();
                                        tipDialog.show();

                                        mGroupListView.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (tipDialog.isShowing()) {
                                                    tipDialog.dismiss();
                                                }
                                            }
                                        }, 3000);

                                        gpioCheckIndex = which;
                                        gpioPlatformItem.setDetailText(platformArray[which]);
                                        dialog.dismiss();
                                    }
                                })
                                .create(dialogStyle).show();
                    }
                })
                .addItemView(gpioWriteItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (gpioUtils == null) {
                            Toast.makeText(GpioActivity.this, "请先选择 gpio 平台", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(GpioActivity.this);
                        builder.setTitle("请输入 GPIO 号(计算方式见首页)")
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                                .addAction("设低", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        try {
                                            int gpioId = Integer.parseInt(builder.getEditText().getText().toString().trim());
                                            gpioUtils.setGpioDirection(gpioId, GpioUtils.GPIO_DIRECTION_OUT);
                                            gpioUtils.gpioSetValue(gpioId, GpioUtils.GPIO_VALUE_LOW);
                                        } catch (Exception e) {
                                            Toast.makeText(GpioActivity.this, "请输入正确的 GPIO 号", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("设高", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        try {
                                            int gpioId = Integer.parseInt(builder.getEditText().getText().toString().trim());
                                            gpioUtils.setGpioDirection(gpioId, GpioUtils.GPIO_DIRECTION_OUT);
                                            gpioUtils.gpioSetValue(gpioId, GpioUtils.GPIO_VALUE_HIGH);
                                        } catch (Exception e) {
                                            Toast.makeText(GpioActivity.this, "请输入正确的 GPIO 号", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .create(dialogStyle).show();
                    }
                })
                .addItemView(gpioReadItem, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (gpioUtils == null) {
                            Toast.makeText(GpioActivity.this, "请先选择 gpio 平台", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(GpioActivity.this);
                        builder.setTitle("请输入 GPIO 号(计算方式见首页)")
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                                .addAction("读取", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        try {
                                            int gpioId = Integer.parseInt(builder.getEditText().getText().toString().trim());
                                            gpioUtils.setGpioDirection(gpioId, GpioUtils.GPIO_DIRECTION_IN);
                                            String gpioValue = gpioUtils.gpioGetValue(gpioId) == 0 ? "低" : "高";
                                            gpioReadItem.setDetailText(gpioValue);
                                        } catch (Exception e) {
                                            Toast.makeText(GpioActivity.this, "请输入正确的 GPIO 号", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }
                                })
                                .create(dialogStyle).show();
                    }
                })
                .addTo(mGroupListView);
    }

    private void initData() {
        platformArray = getResources().getStringArray(R.array.gpio);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpioUtils != null) {
            gpioUtils.close();
        }
    }
}
