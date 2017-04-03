package com.midtrans.sdk.ui.abtracts;

import android.content.Context;

import com.midtrans.sdk.core.models.merchant.ItemDetails;
import com.midtrans.sdk.core.models.snap.transaction.SnapTransaction;
import com.midtrans.sdk.ui.MidtransUi;
import com.midtrans.sdk.ui.R;
import com.midtrans.sdk.ui.models.ItemDetail;
import com.midtrans.sdk.ui.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakawm on 3/27/17.
 */

public class BasePaymentPresenter extends BasePresenter {

    public List<ItemDetail> createItemDetails(Context context) {
        List<ItemDetail> itemViewDetails = new ArrayList<>();

        SnapTransaction snapTransaction = MidtransUi.getInstance().getTransaction();

        if (snapTransaction != null) {
            // Add amount
            String amount = context.getString(R.string.prefix_money, Utils.getFormattedAmount(snapTransaction.transactionDetails.grossAmount));

            // Add header total amount
            itemViewDetails.add(new ItemDetail(
                    null,
                    amount,
                    ItemDetail.TYPE_ITEM_HEADER,
                    snapTransaction.itemDetails.size() > 0));

            // Add items
            for (ItemDetails itemDetails : MidtransUi.getInstance().getCheckoutTokenRequest().itemDetails) {
                String price = context.getString(R.string.prefix_money, Utils.getFormattedAmount(itemDetails.quantity * itemDetails.price));
                String itemName = itemDetails.name;
                if (itemDetails.quantity > 1) {
                    itemName = context.getString(R.string.text_item_name_format, itemDetails.name, itemDetails.quantity);
                }
                itemViewDetails.add(new ItemDetail(itemName, price, ItemDetail.TYPE_ITEM, true));
            }
            return itemViewDetails;
        } else {
            return null;
        }
    }
}
