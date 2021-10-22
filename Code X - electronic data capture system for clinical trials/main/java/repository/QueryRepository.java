// package ;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryRepository extends JpaRepository<model.Query, String> {

	@Query("select q from Query q where sxx = ?1 and sxx = ?2 and qxx = ?3 order by qorder desc")
	List<x.model.Query> findAllQueries(String sxx, String sxx, Integer qxx);

	@Query("select max(q.qid) from Query q where sxx = ?1 and sxx = ?2 and vxx = ?3 and sxx = ?4 and sxx = ?5 and dxx = ?6 and cxx = ?7")
	Integer findMaxQid(String sxx, String sxx, Integer vxx, Integer sxx, String sxx, String dxx, String cxx);

	List<x.model.Query> findAllBySxxAndSxxAndVxxAndSxxAndSxxAndDxxOrderByRxxDesc(String sxx, String sxx, Integer vxx, Integer sxx, String sxx, String dxx);
}
