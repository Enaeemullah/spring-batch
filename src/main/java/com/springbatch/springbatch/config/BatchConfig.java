package com.springbatch.springbatch.config;

import com.springbatch.springbatch.model.Customer;
import com.springbatch.springbatch.model.UserData;
import com.springbatch.springbatch.repository.CustomerRepository;
import com.springbatch.springbatch.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomerRepository customerRepository;


    @Bean
    public FlatFileItemReader<Customer> reader() {
        FlatFileItemReader<Customer> customerFlatFileItemReader = new FlatFileItemReader<>();

        customerFlatFileItemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        customerFlatFileItemReader.setName("CustomerData");
        customerFlatFileItemReader.setLinesToSkip(1);
        customerFlatFileItemReader.setLineMapper(lineMapperUser());
        return customerFlatFileItemReader;
    }

    private LineMapper<Customer> lineMapperUser() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames("customerId", "firstName", "lastName", "email", "gender");
        BeanWrapperFieldSetMapper<Customer> wrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        wrapperFieldSetMapper.setTargetType(Customer.class);
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(wrapperFieldSetMapper);
        return lineMapper;
    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
        simpleAsyncTaskExecutor.setConcurrencyLimit(10);
        return simpleAsyncTaskExecutor;
    }


    public Step step1() {
        return stepBuilderFactory.get("csv-step").<Customer, Customer>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job job1() {
        return jobBuilderFactory.get("customerData")
                .flow(step1())
                .end()
                .build();
    }


    @Bean
    public FlatFileItemReader<UserData> readerUser() {
        FlatFileItemReader<UserData> customerFlatFileItemReader = new FlatFileItemReader<>();

        customerFlatFileItemReader.setResource(new FileSystemResource("src/main/resources/users.csv"));
        customerFlatFileItemReader.setName("userData");
        customerFlatFileItemReader.setLinesToSkip(1);
        customerFlatFileItemReader.setLineMapper(lineMapper());
        return customerFlatFileItemReader;
    }

    private LineMapper<UserData> lineMapper() {
        DefaultLineMapper<UserData> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setStrict(false);
        tokenizer.setNames("userId", "firstName", "lastName");
        BeanWrapperFieldSetMapper<UserData> wrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        wrapperFieldSetMapper.setTargetType(UserData.class);
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(wrapperFieldSetMapper);
        return lineMapper;
    }

    @Bean
    public UserDataProcessor userDataProcessor() {
        return new UserDataProcessor();
    }

    @Bean
    public RepositoryItemWriter<UserData> userWriter() {
        RepositoryItemWriter<UserData> writer = new RepositoryItemWriter<>();
        writer.setRepository(userRepository);
        writer.setMethodName("save");
        return writer;
    }


    @Bean
    public Step step2() {
        return stepBuilderFactory.get("csv-step").<UserData, UserData>chunk(10)
                .reader(readerUser())
                .processor(userDataProcessor())
                .writer(userWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job job2() {
        return jobBuilderFactory.get("importUser")
                .flow(step2())
                .end()
                .build();
    }

}
