package hello;

import org.springframework.batch.support.annotation.Classifier;

public class ExchangeWriterRouterClassifier {

	@Classifier
	public String classify(Notification notification) {
		return notification.getNotificationType();
	}
}
