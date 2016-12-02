package com.midtrans.sdk.corekit.core;

import android.content.Context;

/**
 * Created by ziahaqi on 15/06/2016.
 */
public interface ISdkFlow {

    void runUIFlow(Context context);

    void runRegisterCard(Context context);

    void runCreditCard(Context context);

    void runBankTransfer(Context context);

    void runPermataBankTransfer(Context context);

    void runMandiriBankTransfer(Context context);

    void runBCABankTransfer(Context context);

    void runOtherBankTransfer(Context context);

    void runBCAKlikPay(Context context);

    void runKlikBCA(Context context);

    void runMandiriClickpay(Context context);

    void runMandiriECash(Context context);
}
