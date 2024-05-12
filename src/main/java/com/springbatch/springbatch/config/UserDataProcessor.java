package com.springbatch.springbatch.config;

import com.springbatch.springbatch.model.UserData;
import org.springframework.batch.item.ItemProcessor;

public class UserDataProcessor implements ItemProcessor<UserData,UserData> {

    @Override
    public UserData process(UserData userData) throws Exception {
        return userData;
    }
}
