package com.midtrans.sdk.widgets;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.midtrans.sdk.coreflow.models.SaveCardRequest;
import com.midtrans.sdk.widgets.utils.CardUtils;
import com.midtrans.sdk.widgets.utils.FlipAnimation;

/**
 * Created by ziahaqi on 9/13/16.
 */
public class CardDetailForm extends RelativeLayout{
    private SaveCardRequest cardDetail;
    private EditText editCardDetailCvv;
    private RelativeLayout layoutCardList, cardContainerFront, cardContainerBack;
    private String cardCvv;
    private TextView textCardNumber;

    public SaveCardRequest getSelectedCard() {
        return cardDetail;
    }

    public boolean checkFormValidity() {
        this.cardCvv = editCardDetailCvv.getText().toString();
        if(TextUtils.isEmpty(cardCvv)){
            Toast.makeText(getContext(), getContext().getString(R.string.cvv_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getCardCVV() {
        return this.cardCvv;
    }

    public CardDetailForm(Context context, SaveCardRequest cardDetail) {
        super(context);
        this.cardDetail = cardDetail;
        initView();
    }


    private void initView(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_card_detail, this, true);
        editCardDetailCvv = (EditText) findViewById(R.id.edit_cvv);
        layoutCardList = (RelativeLayout) findViewById(R.id.layout_card_list);
        cardContainerFront = (RelativeLayout) findViewById(R.id.card_container_front_side);
        cardContainerBack = (RelativeLayout) findViewById(R.id.card_container_back_side);
        textCardNumber = (TextView) findViewById(R.id.text_card_number);
        if(cardDetail != null){
            int length = cardDetail.getMaskedCard().length();
            String cardNumber = cardDetail.getMaskedCard().substring(length - 5 , length);
            textCardNumber.setText(getContext().getString(R.string.card_prefix) + cardNumber);
        }
        initCardListActionHandler();
    }

    private void initCardListActionHandler() {

        cardContainerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

        cardContainerFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipCard();
            }
        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        layoutCardList = null;
        cardContainerFront = null;
        cardContainerBack = null;
    }

    private void flipCard() {
        Animation scaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        scaleDown.setDuration(150);
        Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);

        scaleUp.setDuration(150);
        scaleUp.setStartOffset(150);

        FlipAnimation flipAnimation = new FlipAnimation(cardContainerFront, cardContainerBack);
        flipAnimation.setStartOffset(100);
        flipAnimation.setDuration(200);
        if (cardContainerFront.getVisibility() == View.GONE) {
            flipAnimation.reverse();
            CardUtils.hideKeyboard(getContext());
        }
        flipAnimation.setAnimationListener(new Animation.AnimationListener() {
                                               @Override
                                               public void onAnimationStart(Animation animation) {

                                               }

                                               @Override
                                               public void onAnimationEnd(Animation animation) {
                                                   Handler handler = new Handler();
                                                   handler.postDelayed(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                           if (cardContainerFront.getVisibility() == View
                                                                   .VISIBLE) {

                                                               CardUtils.hideKeyboard(getContext());
                                                           } else {
                                                               CardUtils.showKeyboard(getContext(), editCardDetailCvv);
                                                           }
                                                       }
                                                   }, 50);
                                               }

                                               @Override
                                               public void onAnimationRepeat(Animation animation) {

                                               }
                                           }

        );
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setDuration(300);
        animationSet.addAnimation(scaleDown);
        animationSet.addAnimation(flipAnimation);
        animationSet.addAnimation(scaleUp);
        layoutCardList.startAnimation(animationSet);
    }

}