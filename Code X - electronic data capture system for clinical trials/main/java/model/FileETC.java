// package ;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "" }) })
public class FileETC {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ...

	@Column
	@Lob
	private byte[] file;

	@Column(length = 30)
	private ZonedDateTime registeredDate;

	@Column(length = 100, nullable = false)
	private String registeredBy;

	// ...
	
	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public ZonedDateTime getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(ZonedDateTime registeredDate) {
		this.registeredDate = registeredDate;
	}

	public String getRegisteredBy() {
		return registeredBy;
	}

	public void setRegisteredBy(String registeredBy) {
		this.registeredBy = registeredBy;
	}

}
