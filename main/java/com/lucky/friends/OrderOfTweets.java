package com.lucky.friends;



import java.util.ArrayList;
import java.util.List;


public class OrderOfTweets {
    List<String> orderOfTweets;
    public OrderOfTweets(){

    }

    public OrderOfTweets(List<String> orderOfTweets) {
        this.orderOfTweets = orderOfTweets;
    }

    public List<String> getOrderOfTweets() {
        if(orderOfTweets == null){
            return new ArrayList<>();
        }
        return orderOfTweets;
    }

    public void setOrderOfTweets(List<String> orderOfTweets) {
        this.orderOfTweets = orderOfTweets;
    }

    @Override
    public String toString() {
        return "OrderOfTweets{" +
                "orderOfTweets=" + orderOfTweets +
                '}';
    }
}
