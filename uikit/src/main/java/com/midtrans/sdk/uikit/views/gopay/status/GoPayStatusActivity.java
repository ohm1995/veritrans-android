package com.midtrans.sdk.uikit.views.gopay.status;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.midtrans.sdk.corekit.core.Logger;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.models.TransactionResponse;
import com.midtrans.sdk.uikit.R;
import com.midtrans.sdk.uikit.abstracts.BasePaymentActivity;
import com.midtrans.sdk.uikit.utilities.SdkUIFlowUtil;
import com.midtrans.sdk.uikit.widgets.BoldTextView;
import com.midtrans.sdk.uikit.widgets.DefaultTextView;
import com.midtrans.sdk.uikit.widgets.FancyButton;
import com.midtrans.sdk.uikit.widgets.SemiBoldTextView;

/**
 * Created by Fajar on 16/11/17.
 */

public class GoPayStatusActivity extends BasePaymentActivity {

    private static final String TAG = GoPayStatusActivity.class.getSimpleName();

    public static final String EXTRA_PAYMENT_STATUS = "extra.status";

    private FancyButton buttonPrimary;
    private SemiBoldTextView textTitle;
    private BoldTextView merchantName;
    private ImageView qrCodeContainer;
    private FancyButton qrCodeRefresh;

    private boolean isInstructionShown = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gopay_status);
        bindData();
    }

    private void bindData() {
        final TransactionResponse response = (TransactionResponse) getIntent().getSerializableExtra(EXTRA_PAYMENT_STATUS);
        if (response != null) {
            showProgressLayout();

            final LinearLayout instructionLayout = (LinearLayout) findViewById(R.id.gopay_instruction_layout);
            merchantName = (BoldTextView) findViewById(R.id.gopay_merchant_name);
            final DefaultTextView instructionToggle = (DefaultTextView) findViewById(R.id.gopay_instruction_toggle);
            instructionToggle.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isInstructionShown = !isInstructionShown;
                    if (isInstructionShown) {
                        instructionToggle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_up, 0);
                        instructionLayout.setVisibility(View.VISIBLE);
                    } else {
                        instructionToggle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_down, 0);
                        instructionLayout.setVisibility(View.GONE);
                    }
                }
            });

            //process qr code
            final String qrCodeUrl = response.getQrCodeUrl();
            qrCodeContainer = (ImageView) findViewById(R.id.gopay_qr_code);
            qrCodeRefresh = (FancyButton) findViewById(R.id.gopay_reload_qr_button);
            setTextColor(qrCodeRefresh);
            setIconColorFilter(qrCodeRefresh);
            qrCodeRefresh.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressLayout();
                    loadQrCode(qrCodeUrl, qrCodeContainer);
                }
            });
            loadQrCode(qrCodeUrl, qrCodeContainer);

            buttonPrimary.setText(getString(R.string.done));
            buttonPrimary.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    showConfirmationDialog(getString(R.string.confirm_gopay_qr_scan_tablet));
                }
            });
            buttonPrimary.setTextBold();
        }
        textTitle.setText(getString(R.string.gopay_status_title));
    }

    @Override
    public void bindViews() {
        textTitle = (SemiBoldTextView) findViewById(R.id.text_page_title);
        buttonPrimary = (FancyButton) findViewById(R.id.button_primary);
    }

    @Override
    public void initTheme() {
        setPrimaryBackgroundColor(buttonPrimary);
    }

    /**
     * A method for loading QR code based on url that is part of GO-PAY payment response
     * We use Glide listener in order to detect whether image downloading is good or not
     * If it is good, then display the QR code; else display reload button so this method is
     * called once again.
     *
     * @param url       location of QR code to be downloaded
     * @param container view to display the image
     */
    private void loadQrCode(String url, final ImageView container) {
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.gopay_qr_code_frame);
                        frameLayout.setBackgroundColor(getResources().getColor(R.color.light_gray));
                        qrCodeRefresh.setVisibility(View.VISIBLE);
                        Logger.e(TAG, e.getMessage());
                        setMerchantName(false);
                        hideProgressLayout();
                        Toast.makeText(GoPayStatusActivity.this, getString(R.string.error_qr_code), Toast.LENGTH_SHORT)
                                .show();
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.gopay_qr_code_frame);
                        frameLayout.setBackgroundColor(0);
                        qrCodeRefresh.setVisibility(View.GONE);
                        setMerchantName(true);
                        hideProgressLayout();
                        return false;
                    }
                })
                .into(container);
    }

    @Override
    public void onBackPressed() {
        if (isDetailShown) {
            displayOrHideItemDetails();
        } else {
            showConfirmationDialog(getString(R.string.confirm_gopay_qr_scan_tablet));
        }
    }

    private void showConfirmationDialog(String message) {
        try {
            AlertDialog dialog = new AlertDialog.Builder(GoPayStatusActivity.this, R.style.AlertDialogCustom)
                    .setPositiveButton(R.string.text_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!GoPayStatusActivity.this.isFinishing()) {
                                dialog.dismiss();
                                finish();
                            }
                        }
                    })
                    .setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!GoPayStatusActivity.this.isFinishing()) {
                                dialog.dismiss();
                            }
                        }
                    })
                    .setTitle(R.string.cancel_transaction)
                    .setMessage(message)
                    .create();
            dialog.show();
        } catch (Exception e) {
            Logger.e(TAG, "showDialog:" + e.getMessage());
        }
    }

    private void setMerchantName(boolean isQrLoaded) {
        if (isQrLoaded) {
            MidtransSDK midtransSDK = MidtransSDK.getInstance();
            if (midtransSDK != null && !TextUtils.isEmpty(midtransSDK.getMerchantName())) {
                merchantName.setText(midtransSDK.getMerchantName());
                merchantName.setVisibility(View.VISIBLE);
            } else {
                merchantName.setVisibility(View.GONE);
            }
        } else {
            merchantName.setVisibility(View.GONE);
        }
    }
}
