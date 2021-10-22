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
public class Audit {
	public enum categories {
		// ...
	}

	public enum states {
		// ...
	}

	public enum queryType {
		// ...
	}

	public enum queryStatus {
		// ...
	}

	public enum lockStatus {
		// ...
	}

	@NotNull
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	// ...
}
