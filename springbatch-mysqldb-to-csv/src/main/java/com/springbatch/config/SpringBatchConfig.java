package com.springbatch.config;


import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	
	//To read the data from db or any file
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
	
}
