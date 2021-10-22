

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("crf")
public class CRFController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private StudyRepository rpst_study;

	@Autowired
	private AuditRepository rpst_audit;

	@Autowired
	private CRFRepository rpst_crf;

	@Autowired
	private CRFOPTIONSRepository rpst_crf_options;

	@Autowired
	private ParticipantRepository rpst_participant;

	@Autowired
	private FlagRepository rpst_flag;

	@Autowired
	private UserRepository rpst_user;

	@Autowired
	private UserListRepository rpst_userlist;

	@Autowired
	private DMRepository rpst_dm;

	@Autowired
	private QueryRepository rpst_query;

	@Autowired
	private ValidRepository rpst_valid;

	@Autowired
	private MessageRepository rpst_msg;

	@Autowired
	private PermissionRepository rpst_permission;

	@Autowired
	private CSTRepository rpst_chulaSampleTracking;

	@Autowired
	private FileETCRepository rpst_etc;

	@Autowired
	private FileLabRepository rpst_file_lab;


	@GetMapping("/{...}/{...}/{...}/{...}/dm")
	public String requestCRFDM(@PathVariable String ..., Model model, HttpSession httpSession) {
		Locale locale = LocaleContextHolder.getLocale();

		if (httpSession.getAttribute("fullName") == null) {
			// session is expired, then return
			model.addAttribute("msg_host", "logout");
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", messageSource.getMessage("messages.error.session_expired", new Object[] { 0 }, locale));

			return "forms/index_navy";
		}
		// initialize user information
		// temporary purpose

		// initialize page information
		httpSession.setAttribute("action", "crf-dm"); // parameter

		// set study parameters
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);
		String studyid = listStudy.get(0).getStudyid();
		String domain = "DM";
		Double version = listStudy.get(0).getVersion();

		model.addAttribute("age_min", 0);
		model.addAttribute("age_max", 100);

		List<Participant> listParticipant = rpst_participant.findAllByStudyidAndSiteid(studyid, siteid);
		model.addAttribute("listParticipant", listParticipant);
		model.addAttribute("mapVisit", buildListVisits(studyid, version));
		model.addAttribute("version", version);

		model.addAttribute("studyid", studyid); // parameter
		model.addAttribute("studyShortName", studyShortName);
		model.addAttribute("siteid", siteid);
		model.addAttribute("domain", domain);
		model.addAttribute("visit", visit);
		model.addAttribute("scrno", scrno);

		// list CRF
		List<CRF> listCRF = rpst_crf.findAllByStudyidAndDomainAndVisitAndVersion(...);
		if (listCRF.size() == 0) {
			// return
			model.addAttribute("msg_host", "error-400");
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", messageSource.getMessage("messages.error.session_expired", new Object[] { 0 }, locale));

			return "forms/index_navy";
		}
		model.addAttribute("listCRF", listCRF);

		// find DM object
		List<DM> listDM = rpst_dm.findAllBySTUDYIDAndSITEIDAndVISITAndSCRNO(...);
		if (listDM.size() == 0) {
			DM objDM = new DM();
			objDM.setSTUDYID(studyid);
			objDM.setSITEID(siteid);
			objDM.setVISIT(visit);
			objDM.setSCRNO(scrno);
			model.addAttribute("objDM", objDM);
		} else {
			model.addAttribute("objDM", listDM.get(0));
			model.addAttribute("isUpdate", 1);
		}

		// find DM options (for all visits)
		List<CRF_OPTIONS> listAGEU = rpst_crf_options.findAllByStudyidAndVisitAndDomainAndVersionAndCdashig(...);
		model.addAttribute("listAGEU", listAGEU);

		Integer sid_default = 0;
		List<Flag> list_flag = rpst_flag.findFlags(...);
		if (list_flag.size() > 0)
			model.addAttribute("mapFlag", list_flag.stream().collect(Collectors.toMap(Flag::getCdashig, Function.identity(), (oldValue, newValue) -> oldValue)));
		else
			model.addAttribute("mapFlag", new HashMap<String, Flag>());

		/* Begin::for mapping query history */
		List<Query> listQuery = rpst_query.findAllByStudyidAndSiteidAndVisitAndSidAndScrnoAndDomainOrderByRegisteredDateDesc(...);

		// convert ZonedDateTime with Zone ID
		Map<String, Integer> mapQueryPending = new HashMap<>();
		ZoneId zid = ZoneId.of(httpSession.getAttribute("timezone").toString());
		for (Query q : listQuery) {
			q.setConvertedDate(q.getRegisteredDate().withZoneSameInstant(zid));

			if (!q.getState().equals(QUERY_STATE_CLOSED)) {
				if (mapQueryPending.containsKey(q.getCdashig()))
					mapQueryPending.put(q.getCdashig(), mapQueryPending.get(q.getCdashig()) + 1);
				else
					mapQueryPending.put(q.getCdashig(), 1);
			}
		}
		model.addAttribute("mapQuery", listQuery.stream().collect(Collectors.groupingBy(Query::getCdashig, Collectors.toList())));
		model.addAttribute("mapQueryPending", mapQueryPending);

		// user list is required for users' information
		List<UserList> listUserList = rpst_userlist.findAllByStudyidAndSiteid(studyid, siteid);

		for (UserList ul : listUserList) {
			List<User> listUser = rpst_user.findAllByUid(ul.getUid());

			if (listUser.size() == 0)
				continue;

			ul.setOrganization(listUser.get(0).getOrganization());
			ul.setFirstName(listUser.get(0).getFirstName());
			ul.setLastName(listUser.get(0).getLastName());
		}
		model.addAttribute("listUser", listUserList.stream().collect(Collectors.toMap(UserList::getUid, Function.identity(), (oldValue, newValue) -> oldValue)));
		/* End::for mapping query history */

		/* Begin:: get not closed queries */
		Set<String> setAutoQueryOpened = listQuery.stream().filter(x -> x.getState().equals(QUERY_STATE_OPENED) && x.getType().equals("auto")).map(Query::getCdashig).collect(Collectors.toSet());
		model.addAttribute("setAutoQueryOpened", setAutoQueryOpened);
		/* End:: get not closed queries */

		return "forms/index_navy";
	}

	@PostMapping(value = "/{...}/{siteid}/{...}/{...}/dm")
	private String updateCRFDM(@PathVariable String ..., Model model, DM objDM, HttpSession httpSession) {
		Locale locale = LocaleContextHolder.getLocale();

		if (httpSession.getAttribute("fullName") == null) {
			// session is expired, then return
			model.addAttribute("msg_host", "logout");
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", messageSource.getMessage("messages.error.session_expired", new Object[] { 0 }, locale));

			return "forms/index_navy";
		}

		String who = httpSession.getAttribute("uid").toString();
		String domain = "DM";

		// find DB whether old record is stored or not
		List<DM> listDM = rpst_dm.findAllBySTUDYIDAndSITEIDAndVISITAndSCRNO(...);
		if (listDM.size() == 0) { // create new record
			saveAndAudit(...);
		} else { // update
			updateAndAudit(...);
		}

		// initialize page information
		httpSession.setAttribute("action", "crf-dm"); // parameter

		// set study parameters
		List<Study> listStudy = rpst_study.findAllByShortname(studyShortName);
		String studyid = listStudy.get(0).getStudyid();
		Double version = listStudy.get(0).getVersion();

		listDM = rpst_dm.findAllBySTUDYIDAndSITEIDAndVISITAndSCRNO(...);
		model.addAttribute("age_min", 0);
		model.addAttribute("age_max", 100);		
		model.addAttribute("objDM", listDM.get(0));
		model.addAttribute("isUpdate", 1);

		// find DM options AGEU (for all visits)
		List<CRF_OPTIONS> listAGEU = rpst_crf_options.findAllByStudyidAndVisitAndDomainAndVersionAndCdashig(...);
		model.addAttribute("listAGEU", listAGEU);

		/* Begin:: loading CRF and flag */
		// list CRF
		List<CRF> listCRF = rpst_crf.findAllByStudyidAndDomainAndVisitAndVersion(...);
		if (listCRF.size() == 0) {
			// return
			model.addAttribute("msg_host", "error-400");
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", messageSource.getMessage("messages.error.session_expired", new Object[] { 0 }, locale));

			return "forms/index_navy";
		}
		model.addAttribute("listCRF", listCRF);

		Integer sid_default = 0;
		List<Flag> listFlag = rpst_flag.findFlags(studyid, siteid, scrno, visit, sid_default, domain);
		if (listFlag.size() > 0)
			model.addAttribute("mapFlag", listFlag.stream().collect(Collectors.toMap(Flag::getCdashig, Function.identity(), (oldValue, newValue) -> oldValue)));
		else
			model.addAttribute("mapFlag", new HashMap<String, Flag>());
		/* End:: loading CRF and flag */

		// message
		model.addAttribute("msg_type", "success");
		model.addAttribute("msg_text", messageSource.getMessage("messages.warning.08", new Object[] { 0 }, locale));

		/* Being:: loading participant list */
		List<Participant> listParticipant = rpst_participant.findAllByStudyidAndSiteid(...);
		model.addAttribute("listParticipant", listParticipant);
		model.addAttribute("mapVisit", buildListVisits(studyid, version));
		model.addAttribute("version", version);
		/* End:: loading participant list */

		/* Being:: loading study-related parameters */
		model.addAttribute("studyid", studyid); // parameter
		model.addAttribute("studyShortName", studyShortName);
		model.addAttribute("siteid", siteid);
		model.addAttribute("domain", domain);
		model.addAttribute("visit", visit);
		model.addAttribute("scrno", scrno);
		/* End:: loading study-related parameters */

		// user list is required for users' information
		List<UserList> listUserList = rpst_userlist.findAllByStudyidAndSiteid(...);
		for (UserList ul : listUserList) {
			List<User> listUser = rpst_user.findAllByUid(ul.getUid());

			if (listUser.size() == 0)
				continue;

			ul.setOrganization(listUser.get(0).getOrganization());
			ul.setFirstName(listUser.get(0).getFirstName());
			ul.setLastName(listUser.get(0).getLastName());
		}
		model.addAttribute("listUser", listUserList.stream().collect(Collectors.toMap(UserList::getUid, Function.identity(), (oldValue, newValue) -> oldValue)));

		/* Begin:: return Error check results */
		List<Query> listQuery = rpst_query.findAllByStudyidAndSiteidAndVisitAndSidAndScrnoAndDomainOrderByRegisteredDateDesc(...);
		Set<Integer> setAutoQueryRuleNumbers = listQuery.stream().filter(x -> x.getType().equals(QUERY_TYPE_AUTO) && !x.getState().equals(QUERY_STATE_CLOSED)).map(Query::getRulenumber).collect(Collectors.toSet());
		try {
			checkAndWriteAutoQuery(...);
		} catch (NullPointerException e) {
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", "Error Check rules must be modified.");
		}

		listQuery = rpst_query.findAllByStudyidAndSiteidAndVisitAndSidAndScrnoAndDomainOrderByRegisteredDateDesc(...);
		/* End:: return Error check results */

		/* Begin:: get not closed queries */
		// convert ZonedDateTime with Zone ID
		Map<String, Integer> mapQueryPending = new HashMap<>();
		ZoneId zid = ZoneId.of(httpSession.getAttribute("timezone").toString());
		for (Query q : listQuery) {
			q.setConvertedDate(q.getRegisteredDate().withZoneSameInstant(zid));

			if (!q.getState().equals(QUERY_STATE_CLOSED)) {
				if (mapQueryPending.containsKey(q.getCdashig()))
					mapQueryPending.put(q.getCdashig(), mapQueryPending.get(q.getCdashig()) + 1);
				else
					mapQueryPending.put(q.getCdashig(), 1);
			}
		}
		model.addAttribute("mapQuery", listQuery.stream().collect(Collectors.groupingBy(Query::getCdashig, Collectors.toList())));
		model.addAttribute("mapQueryPending", mapQueryPending);

		Set<String> setAutoQueryOpened = listQuery.stream().filter(x -> x.getState().equals(QUERY_STATE_OPENED) && x.getType().equals("auto")).map(Query::getCdashig).collect(Collectors.toSet());
		model.addAttribute("setAutoQueryOpened", setAutoQueryOpened);
		/* End:: get not closed queries */

		return "forms/index_navy";
	}

	// ...
		
	private Map<Object, Map<Object, Object>> loadIDList(String studyid, String siteid) {
		Map<Object, Map<Object, Object>> mapIDs = new HashMap<>();

		List<FileETC> listFileETC = rpst_etc.findAllByStudyidAndSiteid(studyid, siteid);

		try {
			InputStream stream = new ByteArrayInputStream(listFileETC.get(0).getFile());
			Workbook wb = new XSSFWorkbook(OPCPackage.open(stream));

			int totalNumberOfSheets = wb.getNumberOfSheets();

			// initialize mapMeta
			Map<Object, Object> mapMeta = new HashMap<>();
			mapMeta.put("total_tabs", totalNumberOfSheets);
			mapMeta.put("hasHeaderReadError", false);

			Sheet sheet = wb.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Cell cell;

				if (row.getRowNum() > 0) { // starts with the second line (index 1)
					Map<Object, Object> map = new HashMap<>();

					cell = row.getCell(1);
					map.put("id", cell.getStringCellValue());

					cell = row.getCell(3);
					map.put("status", cell.getStringCellValue());

					cell = row.getCell(4);
					map.put("en_popup1", cell.getStringCellValue());

					cell = row.getCell(5);
					map.put("action", cell.getStringCellValue());

					cell = row.getCell(6);
					map.put("en_popup2", cell.getStringCellValue());

					cell = row.getCell(2);
					mapIDs.put(cell.getStringCellValue(), map);
				}
			}

			wb.close();
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}

		return mapIDs;
	}
}
