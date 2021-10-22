// package ;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "" }) })
public class UserHash {
	@NotNull
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	@Column(length = 13, nullable = false)
	private String uid;

	// ...

	@CreationTimestamp
	@Column(length = 30)
	private ZonedDateTime registeredDate;

	// ...

	public ZonedDateTime getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(ZonedDateTime registeredDate) {
		this.registeredDate = registeredDate;
	}
}
