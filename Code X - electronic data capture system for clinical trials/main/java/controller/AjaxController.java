

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class AjaxController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private AuditRepository rpst_audit;

	@Autowired
	private QueryRepository rpst_query;

	@Autowired
	private UserRepository rpst_user;

	@Autowired
	private UserListRepository rpst_userlist;

	@Autowired
	private PermissionRepository rpst_permission;

	@Autowired
	private FlagRepository rpst_flag;

	@Autowired
	private MessageRepository rpst_msg;

	String QUERY_STATE_OPENED = Audit.queryStatus.opened.toString();
	String QUERY_STATE_IN_PROGRESS = Audit.queryStatus.in_progress.toString();
	String QUERY_STATE_PENDING = Audit.queryStatus.pending.toString();
	String QUERY_STATE_CLOSED = Audit.queryStatus.closed.toString();
	String QUERY_TYPE_AUTO = Audit.queryType.auto.toString();
	String QUERY_TYPE_MANUAL = Audit.queryType.manual.toString();

	// ...

	@PostMapping("/query_open/{...}/{...}/{...}/{...}/{...}/{...}/{...}")
	public ResponseEntity<Object> queryOpen(@PathVariable String ..., @PathVariable String ..., HttpSession ...) {
		// get query history, related queries
		if (httpSession.getAttribute("fullName") == null) {
			// return
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "logout");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		return new ResponseEntity<Object>("", HttpStatus.OK);
	}

	// ...

	@PostMapping("/reply_submit")
	public ResponseEntity<?> replySubmit(@RequestBody Query query, HttpSession httpSession) throws JsonProcessingException {
		// get query history, related queries
		Locale locale = LocaleContextHolder.getLocale();

		if (httpSession.getAttribute("fullName") == null) {
			// return
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "logout");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		String who = httpSession.getAttribute("uid").toString();

		String studyid = query.getStudyid();
		String siteid = query.getSiteid();
		Integer visit = query.getVisit();
		Integer sid = query.getSid();
		String scrno = query.getScrno();
		String domain = query.getDomain();
		String cdashig = query.getCdashig();
		String value = query.getValue();
		String comment = query.getComment();
		Integer qid = query.getQid();
		Integer qorder = query.getQorder();

		// set an answer in query table
		Query q = new Query();
		q.setQid(qid);
		q.setQorder(qorder);
		q.setStudyid(studyid);
		q.setSiteid(siteid);
		q.setScrno(scrno);
		q.setSid(sid);
		q.setVisit(visit);
		q.setDomain(domain);
		q.setRegisteredBy(who);
		q.setState(QUERY_STATE_CLOSED);
		q.setCdashig(cdashig);
		q.setType(QUERY_TYPE_MANUAL);
		q.setValue(value);
		q.setComment(comment);
		rpst_query.save(q);

		// write an audit table
		String message = "...";

		Audit audit = new Audit();
		audit.setCategory(Audit.categories.QUERY.toString());
		audit.setComment(message);
		audit.setDomain(domain);
		audit.setNew_value(q.getComment());
		audit.setRegisteredBy(who);
		audit.setScrno(scrno);
		audit.setSid(sid);
		audit.setSiteid(siteid);
		audit.setState(Audit.states.initial.toString());
		audit.setStudyid(studyid);
		audit.setVar_name(cdashig);
		audit.setVisit(visit);
		rpst_audit.save(audit);

		// update query qid:1
		List<Query> listQuery = rpst_query.findAllByStudyidAndSiteidAndScrnoAndVisitAndDomainAndSidAndCdashigAndQidAndQorder(...);
		listQuery.get(0).setState(QUERY_STATE_IN_PROGRESS);
		rpst_query.save(listQuery.get(0));

		// set result
		HashMap<Object, Object> map = new HashMap<>();
		map.put("msg_text", messageSource.getMessage("messages.success.query_submit", new Object[] { 0 }, locale));
		map.put("icon_icon", "success");
		map.put("icon_type", "toast");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(map);

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	@PostMapping("/query_state_submit")
	public ResponseEntity<?> queryStateSubmit(@RequestBody Query query, HttpSession httpSession) throws JsonProcessingException {
		// get query history, related queries
		Locale locale = LocaleContextHolder.getLocale();

		if (httpSession.getAttribute("fullName") == null) {
			// return
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "logout");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}

		String who = httpSession.getAttribute("uid").toString();

		String studyid = query.getStudyid();
		String siteid = query.getSiteid();
		Integer visit = query.getVisit();
		Integer sid = query.getSid();
		String scrno = query.getScrno();
		String domain = query.getDomain();
		String cdashig = query.getCdashig();
		Integer qid = query.getQid();
		String state = query.getState();

		List<Query> listQuery = rpst_query.findAllByStudyidAndSiteidAndScrnoAndVisitAndDomainAndSidAndCdashigAndQidAndQorder(...);

		// set an answer in query table
		listQuery.get(0).setState(state);
		listQuery.get(0).setRegisteredBy(who);
		rpst_query.save(listQuery.get(0));

		// write an audit table
		String message = "...";

		Audit audit = new Audit();
		audit.setCategory(Audit.categories.QUERY.toString());
		audit.setComment(message);
		audit.setDomain(domain);
		audit.setNew_value(state);
		audit.setRegisteredBy(who);
		audit.setScrno(scrno);
		audit.setSid(sid);
		audit.setSiteid(siteid);
		audit.setState(state);
		audit.setStudyid(studyid);
		audit.setVar_name(cdashig);
		audit.setVisit(visit);
		rpst_audit.save(audit);

		// set result
		HashMap<Object, Object> map = new HashMap<>();
		map.put("msg_text", messageSource.getMessage("messages.success.query_submit", new Object[] { 0 }, locale));
		map.put("icon_icon", "success");
		map.put("icon_type", "toast");

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(map);

		return new ResponseEntity<>(json, HttpStatus.OK);
	}

	// ...

}
