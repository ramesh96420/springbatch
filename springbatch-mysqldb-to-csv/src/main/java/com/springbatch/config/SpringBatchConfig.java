package com.springbatch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.springbatch.model.Person;
import com.springbatch.processor.PersonItemProcessor;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private DataSource dataSource;
	
	//item reader to read the data from db
	@Bean
	public JdbcCursorItemReader<Person> reader(){
		
		JdbcCursorItemReader<Person> itemReader = new JdbcCursorItemReader<Person>();
		itemReader.setDataSource(dataSource);
		itemReader.setSql("SELECT person_id, first_name, last_name, email, age from person");
		itemReader.setRowMapper(new PersonRowMapper());
		return itemReader;
		
	}
	
	//item processor
	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}

	//item writer
	@Bean
	public FlatFileItemWriter<Person> writer(){
		
		FlatFileItemWriter<Person> writer = new FlatFileItemWriter<Person>();
		writer.setResource(new ClassPathResource("persons.csv"));
		
		//to separate person object values with ","
		DelimitedLineAggregator<Person> lineAggregator = new DelimitedLineAggregator<Person>();
		lineAggregator.setDelimiter(",");
		
		//it can person bean object value to person.csv file
		BeanWrapperFieldExtractor<Person> wrapperFieldExtractor = new BeanWrapperFieldExtractor<Person>();
		wrapperFieldExtractor.setNames(new String[] {"personId", "firstName", "lastName", "email", "age"});
		
		lineAggregator.setFieldExtractor(wrapperFieldExtractor);
		
		writer.setLineAggregator(lineAggregator);
		return writer;
	}
	
	//Step
	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Person, Person>chunk(100).reader(reader()).processor(processor()).writer(writer()).build();
	}
	
	//Job
	@Bean
	public Job exportPersonJob() {
		return jobBuilderFactory.get("exportPersonJob").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
	}
}
