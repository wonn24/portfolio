
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

@Controller
@RequestMapping("create")
public class CreateController {

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private StudyRepository rpst_study;

	@Autowired
	private MessageRepository rpst_msg;

	@Autowired
	private UserListRepository rpst_userlist;

	@GetMapping
	public String requestCreateNewStudy(Model model, Study objStudy, HttpSession httpSession) {
		Locale locale = LocaleContextHolder.getLocale();

		if (httpSession.getAttribute("fullName") == null) {
			// return

			model.addAttribute("msg_host", "logout");
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", messageSource.getMessage("messages.error.session_expired", new Object[] { 0 }, locale));

			return "forms/index_navy";
		}		

		List<String> list_study = rpst_study.findAllShortNames();
		Set<Object> setStudy = new HashSet<>(list_study.stream().map(String::toLowerCase).collect(Collectors.toList()));
		model.addAttribute("list_studyname", setStudy);		
		model.addAttribute("objStudy", objStudy);

		httpSession.setAttribute("action", "create"); // parameter

		return "forms/index_navy";
	}

	@PostMapping
	public String updateStudy(Model model, Study objStudy, HttpSession httpSession) {
		Locale locale = LocaleContextHolder.getLocale();

		String who = httpSession.getAttribute("fullName").toString();
		String uid = httpSession.getAttribute("uid").toString();

		Gson gson = new Gson();
		General[] siteids = gson.fromJson(objStudy.getSiteid(), General[].class);
		General[] sitenames = gson.fromJson(objStudy.getSitename(), General[].class);

		if (siteids.length != sitenames.length) {
			httpSession.setAttribute("action", "create"); // parameter
			model.addAttribute("objStudy", objStudy);
			model.addAttribute("msg_host", "create");
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", messageSource.getMessage("messages.error.wrong_data", new Object[] { 0 }, locale));

			return "forms/index_navy";
		}

		// check Studyid exists
		List<Study> listStudy = rpst_study.findAllByStudyid(objStudy.getStudyid());
		if (listStudy.size() > 0) {
			httpSession.setAttribute("action", "create"); // parameter
			model.addAttribute("objStudy", objStudy);
			model.addAttribute("msg_host", "create");
			model.addAttribute("msg_type", "popup");
			model.addAttribute("msg_icon", "error");
			model.addAttribute("msg_text", messageSource.getMessage("messages.error.duplicated_studyid", new Object[] { 0 }, locale));

			return "forms/index_navy";
		}

		for (int i = 0; i < siteids.length; i++) {
			// set study table
			Study study = new Study();
			study.setFullname(objStudy.getFullname());
			study.setShortname(objStudy.getShortname());
			study.setStudyid(objStudy.getStudyid());
			study.setSiteid(siteids[i].getValue());
			study.setSitename(sitenames[i].getValue());
			study.setMaxvisit(objStudy.getMaxvisit());
			study.setRegisteredBy(who);
			rpst_study.save(study);

			Message msg = new Message();
			msg.setUid(uid);
			msg.setStudyid(study.getStudyid());
			msg.setSiteid(study.getSiteid());
			msg.setState(Message.messageStates.unread.toString());
			msg.setComment("A study [" + study.getStudyid() + "; " + study.getSiteid() + "] has been created by " + who + ".");
			rpst_msg.save(msg);

			UserList ul = new UserList();
			ul.setStudyid(objStudy.getStudyid());
			ul.setSiteid(siteids[i].getValue());
			ul.setUid(uid);
			ul.setRid("...");
			ul.setRname("...");
			rpst_userlist.save(ul);
		}

		httpSession.setAttribute("action", "blank"); // parameter
		model.addAttribute("objStudy", new Study());

		httpSession.setAttribute("msg_host", "blank");
		httpSession.setAttribute("msg_type", "popup");
		httpSession.setAttribute("msg_icon", "success");
		httpSession.setAttribute("msg_text", messageSource.getMessage("messages.success.save", new Object[] { 0 }, locale));

		return "forms/index_navy";
	}
}
