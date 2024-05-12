package com.springbatch.springbatch.config;

import com.springbatch.springbatch.model.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer,Customer> {


    @Override
    public Customer process(Customer item) throws Exception {
        return item;
    }
}
