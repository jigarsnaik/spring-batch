package hello;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.classify.BackToBackPatternClassifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	JobBuilderFactory jobBuilderFactory;

	@Autowired
	StepBuilderFactory stepBuilderFactory;

	@Autowired
	NotificationJobExecutionListener peopleJobExecutionListener;

	@Autowired
	DataSource dataSource;

	@Bean
	public JdbcCursorItemReader<Notification> reader() {
		return new JdbcCursorItemReaderBuilder<Notification>().dataSource(dataSource).name("notificationReader")
				.sql("SELECT * FROM NOTIFICATION").rowMapper(new BeanPropertyRowMapper<>(Notification.class)).build();

	}

	@Bean
	public ItemProcessor<Notification, Notification> processor() {
		return new NotificationItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Notification> notificationWriter() {
		JdbcBatchItemWriter<Notification> writer = new JdbcBatchItemWriter<Notification>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Notification>());
		writer.setSql("UPDATE NOTIFICATION SET NOTIFICATION_STATUS = :notificationStatus");
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	public JdbcBatchItemWriter<Notification> packWriter() {
		JdbcBatchItemWriter<Notification> writer = new JdbcBatchItemWriter<Notification>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Notification>());
		writer.setSql("INSERT INTO PACK_EXCHANGE(CLIENT_NAME,STATUS) VALUE(:clientName,:notificationStatus)");
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	public JdbcBatchItemWriter<Notification> letterWriter() {
		JdbcBatchItemWriter<Notification> writer = new JdbcBatchItemWriter<Notification>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Notification>());
		writer.setSql("INSERT INTO LETTER_EXCHANGE(CLIENT_NAME,STATUS) VALUE(:clientName,:notificationStatus)");
		writer.setDataSource(dataSource);
		return writer;
	}

	@SuppressWarnings("unchecked")
	@Bean
	public ItemWriter<Notification> compositeExchangeWriter() throws InstantiationException, IllegalAccessException {
		BackToBackPatternClassifier classifier = new BackToBackPatternClassifier();
		classifier.setRouterDelegate(new ExchangeWriterRouterClassifier());

		classifier.setMatcherMap(new HashMap<String, ItemWriter>() {
			{
				put("LETTER", letterWriter());
				put("PACK", packWriter());
			}
		});

		ClassifierCompositeItemWriter<Notification> writer = new ClassifierCompositeItemWriter<Notification>();
		writer.setClassifier(classifier);
		return writer;
	}

	/**
	 * Run the Job.
	 * 
	 * @param step1
	 * @return
	 */
	@Bean
	public Job personProcessingJob(Step step1) {
		return this.jobBuilderFactory.get("notificationJob")
				.listener(peopleJobExecutionListener)
				.incrementer(new RunIdIncrementer())
				.flow(step1)
				.end()
				.build();
	}

	/**
	 * Define Step
	 * 
	 * @param writer
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Bean
	public Step step1() throws InstantiationException, IllegalAccessException {
		return stepBuilderFactory
				.get("notificationStep")
				.<Notification, Notification>chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(compositeExchangeWriter())
				.build();
	}
	
}
