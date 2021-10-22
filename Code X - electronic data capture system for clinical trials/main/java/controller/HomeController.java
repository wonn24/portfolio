

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class HomeController {
	@Autowired
	MessageRepository rpst_msg;

	@Autowired
	private StudyRepository rpst_study;

	@Autowired
	private UserListRepository rpst_userlist;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/")
	public String getUsers(HttpSession httpSession, Model model) {
		// project default page, nothing to do in this page
		if (httpSession.getAttribute("uid") == null || httpSession.getAttribute("login_result") == null) {
			// session is expired, then return
			return "redirect:/logout";
		}

		// get messages
		List<Message> listMessages = getMessages(httpSession.getAttribute("uid").toString());
		model.addAttribute("total_msg", listMessages.size());
		model.addAttribute("messages", listMessages);

		System.out.println("/home");

		resetUserInfo(httpSession);
		httpSession.setAttribute("action", "blank");

		model.addAttribute("messages", getMessages(httpSession.getAttribute("uid").toString()));

		return "forms/index_navy";
	}
	
	@GetMapping("/blank")
	public String initializeHome(HttpSession httpSession, Model model) {
		// project default page, nothing to do in this page
		httpSession.setAttribute("msg_host", null);
		httpSession.setAttribute("msg_type", null);
		httpSession.setAttribute("msg_text", null);
		httpSession.setAttribute("msg_icon", null);

		httpSession.setAttribute("action", "blank");

		return "redirect:/";
	}

	@GetMapping("/blank-login")
	public String initializeLogin(HttpSession httpSession, Model model) {
		// project default page, nothing to do in this page
		httpSession.setAttribute("msg_host", null);
		httpSession.setAttribute("msg_type", null);
		httpSession.setAttribute("msg_text", null);
		httpSession.setAttribute("msg_icon", null);

		httpSession.setAttribute("action", "login");

		return "redirect:/logout";
	}

	@GetMapping("/studies")
	public String getStudies(Model model, HttpSession httpSession) {
		// studies default page, study list will be shown
		if (httpSession.getAttribute("uid") == null) {
			// return
			return "redirect:/logout";
		}

		resetUserInfo(httpSession);
		httpSession.setAttribute("action", "studies"); // parameter

		String uid = httpSession.getAttribute("uid").toString();

		List<UserList> listUserList = rpst_userlist.findAllByUid(uid);
		List<Study> listStudy = rpst_study.findUniqueStudies();
		List<Study> listStudyDiff = listStudy.stream().filter(e -> (listUserList.stream().filter(d -> d.getStudyid().equals(e.getStudyid())).count()) > 0).collect(Collectors.toList());

		try {
			for (Study study : listStudyDiff) {

				// get logo images
				if (study.getLogofile() == null)
					continue;

				byte[] encodeBase64 = Base64.getEncoder().encode(study.getLogofile());
				String s = new String(encodeBase64, "UTF-8");
				study.setConvertedlogo(s);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		model.addAttribute("listStudy", listStudyDiff);

		List<Message> listMessages = getMessages(httpSession.getAttribute("uid").toString());
		model.addAttribute("total_msg", listMessages.size());
		model.addAttribute("messages", listMessages);

		return "forms/index_navy";
	}

	@GetMapping("/messages")
	public String getMessage(HttpSession httpSession, Model model) {
		resetUserInfo(httpSession);
		httpSession.setAttribute("action", "messages");

		model.addAttribute("listMessages", rpst_msg.findAllByUid(httpSession.getAttribute("uid").toString()));

		return "forms/index_navy";
	}

	@PostMapping("/markAsRead")
	@ResponseBody
	public ResponseEntity<?> markAsRead(HttpSession httpSession, Model model, @RequestBody Message message) {
		// mark selected messages as read

		resetUserInfo(httpSession);
		httpSession.setAttribute("action", "messages");

		List<Message> listMessage = rpst_msg.findAllByUid(httpSession.getAttribute("uid").toString());
		model.addAttribute("messages", listMessage);

		Map<Long, Message> mapTmp = listMessage.stream().collect(Collectors.toMap(Message::getId, x -> x));

		try {
			for (String key : message.getComment().split(",")) {
				Long id = Long.parseLong(key);
				if (mapTmp.containsKey(id)) {
					mapTmp.get(id).setState(Message.messageStates.read.toString());
					rpst_msg.save(mapTmp.get(id));
				}
			}
		} catch (NumberFormatException e) {
			e.getStackTrace();
		}

		try {
			HashMap<Object, Object> map = new HashMap<>();
			map.put("result", "success");

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(map);

			return new ResponseEntity<>(json, new HttpHeaders(), HttpStatus.OK);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException e2) {
				e2.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

	@PostMapping("/markAsUnread")
	@ResponseBody
	public ResponseEntity<?> markAsUnread(HttpSession httpSession, Model model, @RequestBody Message message) {
		// mark selected messages as read

		resetUserInfo(httpSession);
		httpSession.setAttribute("action", "messages");

		List<Message> listMessage = rpst_msg.findAllByUid(httpSession.getAttribute("uid").toString());
		model.addAttribute("messages", listMessage);

		Map<Long, Message> mapTmp = listMessage.stream().collect(Collectors.toMap(Message::getId, x -> x));

		try {
			for (String key : message.getComment().split(",")) {
				Long id = Long.parseLong(key);
				if (mapTmp.containsKey(id)) {
					mapTmp.get(id).setState(Message.messageStates.unread.toString());
					rpst_msg.save(mapTmp.get(id));
				}
			}
		} catch (NumberFormatException e) {
			e.getStackTrace();
		}

		try {
			HashMap<Object, Object> map = new HashMap<>();
			map.put("result", "success");

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(map);

			return new ResponseEntity<>(json, new HttpHeaders(), HttpStatus.OK);

		} catch (JsonProcessingException e) {
			e.printStackTrace();
			String jsonResult = null;
			try {
				HashMap<Object, Object> mapData = new HashMap<>();
				mapData.put("result", "error-400");
				ObjectMapper mapper = new ObjectMapper();
				jsonResult = mapper.writeValueAsString(mapData);

			} catch (JsonProcessingException e2) {
				e2.printStackTrace();
			}

			return new ResponseEntity<>(jsonResult, new HttpHeaders(), HttpStatus.OK);
		}
	}

}
