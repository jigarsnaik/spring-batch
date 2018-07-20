package hello;

import org.springframework.batch.item.ItemProcessor;

public class NotificationItemProcessor implements ItemProcessor<Notification, Notification> {

	@Override
	public Notification process(Notification person) throws Exception {
		person.setNotificationStatus("SENT");
		return person;
	}

}
