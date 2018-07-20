package hello;

import java.io.Serializable;

public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long notificationId;

	private String clientName;

	private String notificationType;

	private String notificationStatus;

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationStatus() {
		return notificationStatus;
	}

	public void setNotificationStatus(String notificationStatus) {
		this.notificationStatus = notificationStatus;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Notification [notificationId=");
		builder.append(notificationId);
		builder.append(", clientName=");
		builder.append(clientName);
		builder.append(", notificationType=");
		builder.append(notificationType);
		builder.append(", notificationStatus=");
		builder.append(notificationStatus);
		builder.append("]");
		return builder.toString();
	}

}