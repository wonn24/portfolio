// package ;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "", "", "", "", "" }) })
public class CRF {

	@NotNull
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private Long id;

	@Column(length = 6)
	private Integer _order = 0;

	// ...

	public Integer get_order() {
		return _order;
	}

	public void set_order(Integer _order) {
		this._order = _order;
	}

	// ...
}
